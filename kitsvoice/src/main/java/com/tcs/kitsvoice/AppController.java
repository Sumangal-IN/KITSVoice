package com.tcs.kitsvoice;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.tcs.kitsvoice.model.CallLog;
import com.tcs.kitsvoice.model.CallerID;
import com.tcs.kitsvoice.model.MemoryElement;
import com.tcs.kitsvoice.model.TestIntent;
import com.tcs.kitsvoice.repository.CallLogRepository;
import com.tcs.kitsvoice.repository.CallerIDRepository;
import com.tcs.kitsvoice.repository.TestIntentRepository;
import com.tcs.kitsvoice.service.ActionElementService;
import com.tcs.kitsvoice.service.MemoryElementService;

import com.twilio.http.HttpMethod;
import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Gather;
import com.twilio.twiml.voice.Say;

@EnableAutoConfiguration
@Controller
public class AppController {
	@Autowired
	private CallerIDRepository callerIDRepository;

	@Autowired
	private CallLogRepository callLogRepository;

	@Autowired
	private TestIntentRepository testIntentRepository;

	@Autowired
	private ActionElementService actionElementService;

	@Autowired
	private MemoryElementService memoryElementService;

	JsonParser parser = new JsonParser();

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@ResponseBody
	public String greeting() {
		return "hello world";
	}

	@RequestMapping(value = "/attendCall", method = RequestMethod.POST, produces = { "application/xml" })
	@ResponseBody
	public String attendCall(@RequestParam("Called") String callerNumber, @RequestParam("CallSid") String callSid) {
		callerIDRepository.save(new CallerID(callerNumber, callSid, new Timestamp(System.currentTimeMillis())));
		Say say = new Say.Builder("Hello, Welcome to virtual call center").build();
		Gather gather = new Gather.Builder().inputs(Arrays.asList(Gather.Input.SPEECH)).action("/completed")/* .partialResultCallback("/partial") */.method(HttpMethod.POST).speechTimeout("auto").say(say).build();
		Say say2 = new Say.Builder("We didn't receive any input. Goodbye!").build();

		VoiceResponse response = new VoiceResponse.Builder().gather(gather).say(say2).build();

		try {
			System.out.println(response.toXml());
		} catch (TwiMLException e) {
			e.printStackTrace();
		}
		return response.toXml();

		// Contact number: 448081689319
	}

	@RequestMapping(value = "/completed", method = RequestMethod.POST, produces = { "application/xml" })
	@ResponseBody
	public String finalresult(@RequestParam("SpeechResult") String speechResult, @RequestParam("Confidence") double confidence, @RequestParam("CallSid") String callerSid) {
		System.out.println("speechResult: " + speechResult);
		speechResult = filterTranscript(speechResult);
		callLogRepository.save(new CallLog(callerSid, "IN", "abcd", "xyz", new Timestamp(System.currentTimeMillis())));
		System.out.println("COMPLETE " + speechResult + " Confidence: " + confidence);
		RestTemplate restTemplate = new RestTemplate();
		String intents = restTemplate.getForObject("https://fec63322.ngrok.io/getIntent/{speech}", String.class, valueSubsitutionForType(speechResult));
		JsonElement intent = parser.parse(intents);
		System.out.println(intent.getAsJsonArray().get(0).getAsString());
		return processIntent(callerSid, intent.getAsJsonArray().get(0).getAsString(), speechResult);
	}

	private String filterTranscript(String speechResult) {
		speechResult=speechResult.replaceAll("\\+", "");
		speechResult=speechResult.replaceAll("  ", " ");
		speechResult=speechResult.replaceAll("one ", "1");
		speechResult=speechResult.replaceAll("two ", "2");
		speechResult=speechResult.replaceAll("three ", "3");
		speechResult=speechResult.replaceAll("four ", "4");
		speechResult=speechResult.replaceAll("five ", "5");
		speechResult=speechResult.replaceAll("six ", "6");
		speechResult=speechResult.replaceAll("seven ", "7");
		speechResult=speechResult.replaceAll("eight ", "8");
		speechResult=speechResult.replaceAll("nine ", "9");
		speechResult=speechResult.replaceAll("zero ", "0");
		speechResult=speechResult.replaceAll("five", "5");
		speechResult=speechResult.replaceAll("-", "");
		System.out.println(speechResult);
		return speechResult;
	}

	private String processIntent(String callerSid, String intent, String speechResult) {
		RestTemplate restTemplate = new RestTemplate();
		String rules = restTemplate.getForObject("https://fec63322.ngrok.io/executionRule/{intent}", String.class, intent);
		JsonElement ruleSet = parser.parse(rules);
		for (JsonElement rule : ruleSet.getAsJsonArray()) {
			actionElementService.push(new ActionElement(callerSid, intent, rule.getAsString().split("#")[0], rule.getAsString().split("#")[1]));
			System.out.println(rule.getAsString());
		}
		while (true) {
			ActionElement actionElement = actionElementService.peek(callerSid);
			String parameter = null;
			String variable = null;
			String value = null;
			String speech[] = null;
			String context_path = null;
			String randomSpeech = null;
			switch (actionElement.getAction()) {
			case "VARIABLE_REQUIRED":
				parameter = actionElement.getParameter();
				variable = parameter.split("\\|")[0];
				speech = parameter.split("\\|")[1].split(":");
				if (memoryElementService.get(callerSid, variable).isEmpty()) {
					randomSpeech = speech[(int) (System.currentTimeMillis() % speech.length)];
					Say say = new Say.Builder(randomSpeech).build();
					Gather gather = new Gather.Builder().inputs(Arrays.asList(Gather.Input.SPEECH)).action("/completed").method(HttpMethod.POST).speechTimeout("auto").say(say).build();
					Say say2 = new Say.Builder("We didn't receive any input. Goodbye!").build();
					VoiceResponse response = new VoiceResponse.Builder().gather(gather).say(say2).build();
					return response.toXml();
				} else {
					actionElementService.pop(callerSid);
				}
				break;
			case "EXECUTE":
				if (actionElement.getParameter().contains("Cancel") || actionElement.getParameter().contains("Status")) {
					actionElementService.pop(callerSid);
					break;
				}
				context_path = actionElement.getParameter();
				context_path = variableSubsitutionFromMemory(context_path, callerSid);
				String serviceURL = addProtocolAndHost(context_path);
				System.out.println(serviceURL);
				String resultAsJSON = restTemplate.getForObject(serviceURL, String.class);
				populateMemoryFromJSON(callerSid, resultAsJSON);
				actionElementService.pop(callerSid);
				break;
			case "PLAY_RANDOM":
				speech = actionElement.getParameter().split(":");
				randomSpeech = speech[(int) (System.currentTimeMillis() % speech.length)];
				randomSpeech = variableSubsitutionFromMemory(randomSpeech, callerSid);
				Say say = new Say.Builder(randomSpeech).build();
				Gather gather = new Gather.Builder().inputs(Arrays.asList(Gather.Input.SPEECH)).action("/completed").method(HttpMethod.POST).speechTimeout("auto").say(say).build();
				Say say2 = new Say.Builder("We didn't receive any input. Goodbye!").build();
				VoiceResponse response = new VoiceResponse.Builder().gather(gather).say(say2).build();
				actionElementService.pop(callerSid);
				return response.toXml();
			case "EVALUATE_VARIABLE":
				variable = actionElement.getParameter();
				value = getNumber(speechResult);
				memoryElementService.put(new MemoryElement(callerSid, variable, value));
				actionElementService.pop(callerSid);
				break;
			case "POP_LAST_ACTION":
				actionElementService.pop(callerSid);
			}
		}
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
			String placeholder = input.substring(input.indexOf("["), input.indexOf("]") + 1);
			List<MemoryElement> replacements = memoryElementService.get(callerSid, placeholder.substring(1, placeholder.length() - 1));
			if (!replacements.isEmpty())
				input = input.replace(placeholder, replacements.get(0).getValue());
			else
				break;
		}
		return input;
	}

	@RequestMapping(value = "/partial", method = RequestMethod.POST, produces = { "application/xml" })
	@ResponseBody
	public void partialresult(@RequestParam("UnstableSpeechResult") String speechResult, @RequestParam("Stability") double stability) {
		System.out.println("PARTIAL " + speechResult + " Stability: " + stability);
	}

	@RequestMapping(value = "/getTestOutput", method = RequestMethod.GET)
	@ResponseBody
	public String getTestOutput() {
		List<TestIntent> testIntents = testIntentRepository.findAll();
		return testIntents.size() + " ";
	}

}
