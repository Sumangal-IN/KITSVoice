package com.tcs.kitsvoice;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Say;

@Controller
public class AppController {

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@ResponseBody
	public String greeting() {
		return "hello world";
	}

	@RequestMapping(value = "/attendCall", method = RequestMethod.POST)
	@ResponseBody
	public String attendCall() {
		Say say = new Say.Builder("ultimatix.net server IP address could not be found.").build();
		VoiceResponse voiceResponse = new VoiceResponse.Builder().say(say).build();
		return voiceResponse.toXml();
	}

}
