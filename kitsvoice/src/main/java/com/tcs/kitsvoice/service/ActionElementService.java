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
		List<ActionElement> actionElements = actionElementRepository.findByCallerSidOrderByIdDesc(callerSid);
		actionElementRepository.delete(actionElements.get(0));
		return actionElements.get(0);
	}
	
	public ActionElement peek(String callerSid) {
		List<ActionElement> actionElements = actionElementRepository.findByCallerSidOrderByIdDesc(callerSid);
		return actionElements.get(0);
	}
	
	public Boolean isEmpty(String callerSid) {
		List<ActionElement> actionElements = actionElementRepository.findByCallerSidOrderByIdDesc(callerSid);
		return actionElements.isEmpty();
	}

	public void remove(String callerSid, String type, String element) {
		List<ActionElement> actionElements=null;
		switch(type)
		{
		case "intent":
			actionElements= actionElementRepository.findByCallerSidAndIntent(callerSid,element);
			break;
		case "action":
			actionElements = actionElementRepository.findByCallerSidAndAction(callerSid,element);
			break;
		}
		
		for(ActionElement actionElement:actionElements)
		{
			actionElementRepository.delete(actionElement);
		}
	}
}
