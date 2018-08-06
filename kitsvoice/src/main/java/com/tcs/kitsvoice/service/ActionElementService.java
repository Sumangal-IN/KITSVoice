package com.tcs.kitsvoice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.kitsvoice.model.ActionElement;
import com.tcs.kitsvoice.repository.ActionElementRepository;

@Service
public class ActionElementService {

	@Autowired
	private ActionElementRepository actionElementRepository;

	public void push(ActionElement actionElmenet) {
		actionElementRepository.save(actionElmenet);
	}

	public ActionElement pop(String callerSid) {
		List<ActionElement> actionElement = actionElementRepository.findByCallerSidOrderByIdDesc(callerSid);
		actionElementRepository.delete(actionElement.get(0));
		return actionElement.get(0);
	}
	
	public ActionElement peek(String callerSid) {
		List<ActionElement> actionElement = actionElementRepository.findByCallerSidOrderByIdDesc(callerSid);
		return actionElement.get(0);
	}
}
