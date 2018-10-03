# things we need for NLP
import nltk
nltk.download('punkt')
from nltk.stem.lancaster import LancasterStemmer
from nltk.stem.porter import PorterStemmer
from nltk.stem.snowball import SnowballStemmer

#stemmer = LancasterStemmer()
#stemmer = PorterStemmer()
stemmer = SnowballStemmer("english")

# things we need for Tensorflow
import numpy as np
import tflearn
import tensorflow as tf
import random

# things we need to create webservice
from flask import Flask
from flask import request

# things we need to convert ndarray to string
from json_tricks import dump, dumps, load, loads, strip_comments

# things we need to connect with MySQL
import mysql.connector as dbConnect

# connection with MySQL
mydb = dbConnect.connect(
  host="localhost",
  user="root",
  password="root",
  database="voiceapp"
)

# create app for webservice
app = Flask(__name__)

# create test webservice endpoint
@app.route('/', methods=['GET'])
def hello_world():
    return 'Hello World  .. (c) Kingfisher Plc'

# shutdown routine
def shutdown_server():
    func = request.environ.get('werkzeug.server.shutdown')
    if func is None:
        raise RuntimeError('Not running with the Werkzeug Server')
    func()

#create endpoint to shutdown the webservice
@app.route('/shutdown', methods=['GET'])
def shutdown():
    shutdown_server()
    return 'Server shutting down...'

# restore all of our data structures
import pickle
data = pickle.load( open( "training_data", "rb" ) )
words = data['words']
classes = data['classes']
train_x = data['train_x']
train_y = data['train_y']

# Build neural network
net = tflearn.input_data(shape=[None, len(train_x[0])])
net = tflearn.fully_connected(net, 8)
net = tflearn.fully_connected(net, 8)
net = tflearn.fully_connected(net, len(train_y[0]), activation='softmax')
net = tflearn.regression(net)

# Define model and setup tensorboard
model = tflearn.DNN(net, tensorboard_dir='tflearn_logs')

def clean_up_sentence(sentence):
    # tokenize the pattern
    sentence_words = nltk.word_tokenize(sentence)
    # stem each word
    sentence_words = [stemmer.stem(word.lower()) for word in sentence_words]
    return sentence_words

# return bag of words array: 0 or 1 for each word in the bag that exists in the sentence
def bow(sentence, words, show_details=False):
    # tokenize the pattern
    sentence_words = clean_up_sentence(sentence)
    # bag of words
    bag = [0]*len(words)  
    for s in sentence_words:
        for i,w in enumerate(words):
            if w == s: 
                bag[i] = 1
                if show_details:
                    print ("found in bag: %s" % w)

    return(np.array(bag))

def classify(sentence):
    # generate probabilities from the model
    results = model.predict([bow(sentence, words)])[0]
    # filter out predictions below a threshold
    results = [[i,r] for i,r in enumerate(results)]
    # sort by strength of probability
    results.sort(key=lambda x: x[1], reverse=True)
    return_list = []
    for r in results:
        return_list.append((classes[r[0]], str(round(r[1], 4))))
    # return tuple of intent and probability
    return return_list

# load our saved model
model.load('./model.tflearn')

@app.route('/process/<text>', methods=['GET'])
def process(text):
    return dumps(classify(text))
  
@app.route('/getIntent/<text>', methods=['GET'])
def getIntent(text):
    return dumps(classify(text)[0])

@app.route('/executionRule/<intent>', methods=['GET'])
def executionRule(intent):
    db_intent_rule = mydb.cursor()
    db_intent_rule.execute("select * from intentexecution where intent='"+intent+"' order by step desc")
    rules = []
    for row in db_intent_rule:
        rules.append(row[2]+"#"+row[3]+"#"+row[4])
    return dumps(rules)

# start the webservice
app.run(host='localhost', port=9000)