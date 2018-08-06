package com.tcs.kitsvoice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcs.kitsvoice.model.MemoryElement;

@Repository
public interface MemoryElementRepository extends JpaRepository<MemoryElement, Long> {

	List<MemoryElement> findAll();
	
	List<MemoryElement> findByCallerSidAndVariable(String callerSid, String variable);

}
