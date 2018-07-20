package com.tcs.kitsvoice;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.tcs.kitsvoice.model.CallLog;
import com.tcs.kitsvoice.model.CallerID;
import com.tcs.kitsvoice.model.TestIntent;
import com.tcs.kitsvoice.repository.CallLogRepository;
import com.tcs.kitsvoice.repository.CallerIDRepository;
import com.tcs.kitsvoice.repository.TestIntentRepository;
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

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@ResponseBody
	public String greeting() {
		return "hello world";
	}

	@RequestMapping(value = "/attendCall", method = RequestMethod.POST, produces = { "application/xml" })
	@ResponseBody
	public String attendCall(@RequestParam("Called") String callerNumber, @RequestParam("CallSid") String callSid) {
		callerIDRepository.save(new CallerID(callerNumber, callSid, new Timestamp(System.currentTimeMillis())));
		Say say = new Say.Builder("Hello, How can I help you today?").build();
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
		callLogRepository.save(new CallLog(callerSid, "IN", "abcd", "xyz", new Timestamp(System.currentTimeMillis())));
		System.out.println("COMPLETE " + speechResult + " Confidence: " + confidence);
		RestTemplate restTemplate = new RestTemplate();
		String intents = restTemplate.getForObject("https://fec63322.ngrok.io/process/{speech}", String.class, speechResult);
		System.out.println(intents);
		//return "<Response><Say>You have said " + speechResult + ". Thank you</Say></Response>";
		Say say = new Say.Builder("Next sentence please").build();
		Gather gather = new Gather.Builder().inputs(Arrays.asList(Gather.Input.SPEECH)).action("/completed")/* .partialResultCallback("/partial") */.method(HttpMethod.POST).speechTimeout("auto").say(say).build();
		Say say2 = new Say.Builder("We didn't receive any input. Goodbye!").build();

		VoiceResponse response = new VoiceResponse.Builder().gather(gather).say(say2).build();

		try {
			System.out.println(response.toXml());
		} catch (TwiMLException e) {
			e.printStackTrace();
		}
		return response.toXml();
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
		String result = null;
		return testIntents.size() + " ";
	}

}
