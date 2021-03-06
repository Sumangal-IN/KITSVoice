package com.tcs.kitsvoice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.kitsvoice.model.MemoryElement;
import com.tcs.kitsvoice.repository.MemoryElementRepository;

@Service
public class MemoryElementService {

	@Autowired
	private MemoryElementRepository memoryElementRepository;

	public void put(MemoryElement memoryElement) {
		List<MemoryElement> memoryElements = memoryElementRepository.findByCallerSidAndVariable(memoryElement.getCallerSid(), memoryElement.getVariable());
		if (!memoryElements.isEmpty()) {
			delete(memoryElement.getCallerSid(), memoryElement.getVariable());
		}
		memoryElementRepository.save(memoryElement);
	}

	public MemoryElement get(String callerSid, String variable) {
		List<MemoryElement> memoryElements = memoryElementRepository.findByCallerSidAndVariable(callerSid, variable);
		if (!memoryElements.isEmpty())
			return memoryElements.get(0);
		return null;
	}

	public void delete(String callerSid, String variable) {
		List<MemoryElement> memoryElementList = memoryElementRepository.findByCallerSidAndVariable(callerSid, variable);
		if (!memoryElementList.isEmpty()) {
			memoryElementRepository.delete(memoryElementList.get(0));
		}
	}

}
