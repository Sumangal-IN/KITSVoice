package com.tcs.kitsvoice;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tcs.kitsvoice.model.ActionElement;
import com.tcs.kitsvoice.model.CallerID;
import com.tcs.kitsvoice.model.MemoryElement;
import com.tcs.kitsvoice.model.TestIntent;
import com.tcs.kitsvoice.repository.CallerIDRepository;
import com.tcs.kitsvoice.repository.TestIntentRepository;
import com.tcs.kitsvoice.service.ActionElementService;
import com.tcs.kitsvoice.service.MemoryElementService;

import com.twilio.http.HttpMethod;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Gather;
import com.twilio.twiml.voice.Say;

@EnableAutoConfiguration
@Controller
public class AppController {
	@Autowired
	private CallerIDRepository callerIDRepository;

	@Autowired
	private TestIntentRepository testIntentRepository;

	@Autowired
	private ActionElementService actionElementService;

	@Autowired
	private MemoryElementService memoryElementService;

	JsonParser parser = new JsonParser();

	private static Logger logger = LoggerFactory.getLogger(AppController.class);

	@RequestMapping(value = "/attendCall", method = RequestMethod.POST, produces = { "application/xml" })
	@ResponseBody
	public String attendCall(@RequestParam("Called") String callerNumber, @RequestParam("CallSid") String callSid) {
		callerIDRepository.save(new CallerID(callerNumber, callSid, new Timestamp(System.currentTimeMillis())));
		System.out.println("LOG_WATCH " + new CallerID(callerNumber, callSid, new Timestamp(System.currentTimeMillis())).toString());
		logger.info("LOG_WATCH " + new CallerID(callerNumber, callSid, new Timestamp(System.currentTimeMillis())).toString());
		Say say = new Say.Builder("Hello, Welcome to virtual call center").build();
		Gather gather = new Gather.Builder().inputs(Arrays.asList(Gather.Input.SPEECH)).action("/completed")/* .partialResultCallback("/partial") */.method(HttpMethod.POST).speechTimeout("auto").say(say).build();
		Say say2 = new Say.Builder("We didn't receive any input. Goodbye!").build();
		VoiceResponse response = new VoiceResponse.Builder().gather(gather).say(say2).build();
		return response.toXml();

		// Contact number: 448081689319
	}

	@RequestMapping(value = "/completed", method = RequestMethod.POST, produces = { "application/xml" })
	@ResponseBody
	public String finalresult(@RequestParam("SpeechResult") String speechResult, @RequestParam("CallSid") String callerSid) {
		System.out.println("speechResult: " + speechResult);
		speechResult = filter(speechResult);
		RestTemplate restTemplate = new RestTemplate();
		String intents = restTemplate.getForObject("https://fec63322.ngrok.io/getIntent/{speech}", String.class, valueSubsitutionForType(speechResult));
		JsonElement intent = parser.parse(intents);
		System.out.println(intent.getAsJsonArray().get(0).getAsString());
		return processIntent(callerSid, intent.getAsJsonArray().get(0).getAsString(), speechResult);
	}

	private String processIntent(String callerSid, String intent, String speechResult) {
		if (!loadIntent(callerSid, intent, true)) {
			String lastSpeech = "";
			ActionElement actionElement = actionElementService.peek(callerSid);
			if (actionElement.getAction().equals("PLAY_RANDOM")) {
				String parameter = actionElement.getParameter();
				String[] speech = parameter.split(":");
				lastSpeech = getSpeech(speech, callerSid);
			} else if (actionElementService.peek(callerSid).getAction().equals("VARIABLE_REQUIRED")) {
				String parameter = actionElement.getParameter();
				String[] speech = parameter.split("\\|")[1].split(":");
				lastSpeech = getSpeech(speech, callerSid);
			}
			return makeSpeech("Sorry I could not understand what you just said, " + lastSpeech, callerSid);
		}
		if(intent.equals("EscapeIntent"))
		{
			ActionElement actionElement = actionElementService.peek(callerSid);
			actionElementService.remove(callerSid, "intent", actionElement.getIntent());
			return makeSpeech("Ok, is there anything else I can help you with?", callerSid);
		}
		while (true) {
			ActionElement actionElement = actionElementService.peek(callerSid);
			String parameter = null;
			String variable = null;
			String value = null;
			String speech[] = null;
			String contextPath = null;
			String expectedValue = null;
			switch (actionElement.getAction()) {
			case "VARIABLE_REQUIRED":
				parameter = actionElement.getParameter();
				variable = parameter.split("\\|")[0];
				speech = parameter.split("\\|")[1].split(":");
				if (memoryElementService.get(callerSid, variable) == null)
					return makeSpeech(speech, callerSid);
				actionElementService.pop(callerSid);
				break;
			case "EXECUTE":
				// contextPath = actionElement.getParameter();
				// contextPath = variableSubsitutionFromMemory(contextPath, callerSid);
				// String serviceURL = addProtocolAndHost(contextPath);
				// String resultAsJSON = restTemplate.getForObject(serviceURL, String.class);
				String resultAsJSON = "{\"orderStatus\":\"Ready for shipment\",\"orderNumber_rel_customerID\":\"true\",\"orderCancellable\":\"false\",\"reasonOrderNotCancellable\":\"order already dispatched\"}";
				populateMemoryFromJSON(callerSid, resultAsJSON);
				actionElementService.pop(callerSid);
				break;
			case "PLAY_RANDOM":
				speech = actionElement.getParameter().split(":");
				actionElementService.pop(callerSid);
				return makeSpeech(speech, callerSid);
			case "EVALUATE_VARIABLE":
				parameter = actionElement.getParameter();
				String varType = parameter.split("\\|")[0];
				variable = parameter.split("\\|")[1];
				switch (varType) {
				case "dnumber":
					value = getNumber(speechResult);
					break;
				case "dname":
					value = speechResult;
					break;
				}
				memoryElementService.put(new MemoryElement(callerSid, variable, value));
				actionElementService.pop(callerSid);
				break;
			case "POP_LAST_ACTION":
				actionElementService.pop(callerSid);
				break;
			case "CLEAR_STACK":
				parameter = actionElement.getParameter();
				String type = parameter.split("\\|")[0];
				String element = parameter.split("\\|")[1];
				actionElementService.pop(callerSid);
				actionElementService.remove(callerSid, type, element);
				break;
			case "SPECIAL_NO":
				actionElementService.pop(callerSid);
				if (actionElementService.isEmpty(callerSid))
					return makeSpeech("Thank you, have a nice day", callerSid, true);
				else {
					actionElement = actionElementService.peek(callerSid);
					actionElementService.remove(callerSid, "intent", actionElement.getIntent());
					return makeSpeech("Is there anything else I can help you with?", callerSid);
				}
			case "LOAD_INTENT":
				actionElementService.pop(callerSid);
				parameter = actionElement.getParameter();
				loadIntent(callerSid, parameter, false);
				break;
			case "VALIDATE_VARIABLE":
				actionElementService.pop(callerSid);
				parameter = actionElement.getParameter();
				variable = parameter.split("\\|")[0];
				expectedValue = parameter.split("\\|")[1];
				List<String> variablesToRemove = Arrays.asList(parameter.split("\\|")[2].split(","));
				String intentToLoad = parameter.split("\\|")[3];
				speech = parameter.split("\\|")[4].split(":");
				if (memoryElementService.get(callerSid, variable) != null && memoryElementService.get(callerSid, variable).getValue().equals(expectedValue)) {
					for (String variableToRemove : variablesToRemove)
						memoryElementService.delete(callerSid, variableToRemove);
					actionElementService.remove(callerSid, "intent", actionElement.getIntent());
					if (!intentToLoad.equals(""))
						actionElementService.push(new ActionElement(callerSid, actionElement.getIntent(), "LOAD_INTENT", intentToLoad, null));
					return makeSpeech(speech, callerSid);
				}
				break;
			case "RESOLVE_TYPE_NUMBER":
				actionElementService.pop(callerSid);
				if (actionElementService.peek(callerSid).getAction().equals("VARIABLE_REQUIRED")) {
					variable = actionElementService.peek(callerSid).getParameter().split("\\|")[0];
					value = getNumber(speechResult);
					memoryElementService.put(new MemoryElement(callerSid, variable, value));
				}
				break;
			case "MEMORY_INSERT":
				parameter = actionElement.getParameter();
				variable = parameter.split("\\|")[0];
				value = parameter.split("\\|")[1];
				memoryElementService.put(new MemoryElement(callerSid, variable, value));
				actionElementService.pop(callerSid);
				break;
			case "MEMORY_REMOVE":
				variable = actionElement.getParameter();
				memoryElementService.delete(callerSid, variable);
				actionElementService.pop(callerSid);
				break;
			case "REMOVE_CURRENT_INTENT":
				actionElementService.pop(callerSid);
				parameter = actionElement.getParameter();
				variable = parameter.split("\\|")[0];
				expectedValue = parameter.split("\\|")[1];
				if (memoryElementService.get(callerSid, variable) != null && memoryElementService.get(callerSid, variable).getValue().equals(expectedValue))
					actionElementService.remove(callerSid, "intent", actionElement.getIntent());
				break;
			}
		}
	}

	private boolean loadIntent(String callerSid, String intent, boolean checkExpectedIntent) {
		if (checkExpectedIntent && !actionElementService.isEmpty(callerSid)) {
			boolean match_found = false;
			List<String> expectedIntents = Arrays.asList(actionElementService.peek(callerSid).getExpectedIntent().split(","));
			for (String expectedIntent : expectedIntents) {
				if (expectedIntent!=null && expectedIntent.equals(intent)) {
					match_found = true;
					break;
				}
			}
			if (!match_found)
				return false;
		}
		RestTemplate restTemplate = new RestTemplate();
		String rules = restTemplate.getForObject("https://fec63322.ngrok.io/executionRule/{intent}", String.class, intent);
		JsonElement ruleSet = parser.parse(rules);
		for (JsonElement rule : ruleSet.getAsJsonArray()) {
			System.out.println(rule);
			actionElementService.push(new ActionElement(callerSid, intent, rule.getAsString().split("#").length > 0 ? rule.getAsString().split("#")[0] : null, rule.getAsString().split("#").length > 1 ? rule.getAsString().split("#")[1] : null, rule.getAsString().split("#").length > 2 ? rule.getAsString().split("#")[2] : null));
		}
		return true;
	}

	private String makeSpeech(String speech, String callerSid) {
		return makeSpeech(speech, callerSid, false);
	}

	private String makeSpeech(String speech, String callerSid, Boolean endCall) {
		speech = variableSubsitutionFromMemory(speech, callerSid);
		if (endCall) {
			Say say = new Say.Builder(speech).build();
			VoiceResponse response = new VoiceResponse.Builder().say(say).build();
			return response.toXml();
		}
		Say say = new Say.Builder(speech).build();
		Gather gather = new Gather.Builder().inputs(Arrays.asList(Gather.Input.SPEECH)).action("/completed").method(HttpMethod.POST).speechTimeout("auto").say(say).build();
		Say say2 = new Say.Builder("We didn't receive any input. Goodbye!").build();
		VoiceResponse response = new VoiceResponse.Builder().gather(gather).say(say2).build();
		return response.toXml();
	}

	private String getSpeech(String[] speech, String callerSid) {
		String randomSpeech = speech[(int) (System.currentTimeMillis() % speech.length)];
		randomSpeech = variableSubsitutionFromMemory(randomSpeech, callerSid);
		return randomSpeech;
	}

	private String makeSpeech(String[] speech, String callerSid) {
		String randomSpeech = getSpeech(speech, callerSid);
		Say say = new Say.Builder(randomSpeech).build();
		Gather gather = new Gather.Builder().inputs(Arrays.asList(Gather.Input.SPEECH)).action("/completed").method(HttpMethod.POST).speechTimeout("auto").say(say).build();
		Say say2 = new Say.Builder("We didn't receive any input. Goodbye!").build();
		VoiceResponse response = new VoiceResponse.Builder().gather(gather).say(say2).build();
		return response.toXml();
	}

	public String valueSubsitutionForType(String input) {
		while (getNumber(input) != null) {
			input = input.replace(getNumber(input), "dnumber");
		}
		return input;
	}

	public String getNumber(String input) {
		Pattern pattern = Pattern.compile("[0-9]+");
		Matcher matcher = pattern.matcher(input);
		if (matcher.find())
			return matcher.group();
		else
			return null;
	}

	public void populateMemoryFromJSON(String callerSid, String json) {
		JsonElement jsonElement = parser.parse(json);
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			memoryElementService.put(new MemoryElement(callerSid, entry.getKey(), entry.getValue().getAsString()));
		}
	}

	public String addProtocolAndHost(String contextPath) {
		return "http://localhost:8000" + contextPath;
	}

	public String variableSubsitutionFromMemory(String input, String callerSid) {
		while (input.contains("[")) {
			String placeholder = input.substring(input.indexOf('['), input.indexOf(']') + 1);
			MemoryElement replacements = memoryElementService.get(callerSid, placeholder.substring(1, placeholder.length() - 1));
			if (replacements != null)
				input = input.replace(placeholder, replacements.getValue());
			else
				break;
		}
		return input;
	}

	@RequestMapping(value = "/getTestOutput", method = RequestMethod.GET)
	@ResponseBody
	public String getTestOutput() {
		List<TestIntent> testIntents = testIntentRepository.findAll();
		return testIntents.size() + " ";
	}

	private static String filter(String text) {
		// Remove multilple spaces (at begining)
		while (text.contains("  "))
			text = text.replaceAll("  ", " ");
		// Resolve contraction
		text = text.replaceAll("let's", "let us");
		text = text.replaceAll("he's", "he has");
		text = text.replaceAll("she's", "she has");
		text = text.replaceAll("won't", "will not");
		text = text.replaceAll("n't", " not");
		text = text.replaceAll("'m", " am");
		text = text.replaceAll("'s", " is");
		text = text.replaceAll("'ve", " have");
		text = text.replaceAll("'re", " are");
		text = text.replaceAll("'d", " had");
		text = text.replaceAll("'ll", " will");
		// text to digit
		text = text.replaceAll("one", "1");
		text = text.replaceAll("two", "2");
		text = text.replaceAll("three", "3");
		text = text.replaceAll("four", "4");
		text = text.replaceAll("five", "5");
		text = text.replaceAll("six", "6");
		text = text.replaceAll("seven", "7");
		text = text.replaceAll("eight", "8");
		text = text.replaceAll("nine", "9");
		text = text.replaceAll("zero", "0");
		// Symbol removal
		Matcher matcher = Pattern.compile("[^(a-z)(0-9)(A-Z)\\s]+").matcher(text);
		while (matcher.find()) {
			text = text.replace(matcher.group(0), "");
			matcher = Pattern.compile("[^(a-z)(0-9)(A-Z)\\s]+").matcher(text);
		}
		// Digit compaction
		matcher = Pattern.compile("\\d+\\s+\\d+").matcher(text);
		while (matcher.find()) {
			text = text.replace(matcher.group(0), matcher.group(0).replaceAll("\\s+", ""));
			matcher = Pattern.compile("\\d+\\s+\\d+").matcher(text);
		}
		// Remove multilple spaces (at end)
		while (text.contains("  "))
			text = text.replaceAll("  ", " ");

		return text;
	}

}
