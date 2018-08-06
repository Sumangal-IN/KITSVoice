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
		memoryElementRepository.save(memoryElement);
	}

	public List<MemoryElement> get(String callerSid, String variable) {
		return memoryElementRepository.findByCallerSidAndVariable(callerSid, variable);
	}

}
