package com.tcs.kitsvoice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcs.kitsvoice.model.CallerID;
import com.tcs.kitsvoice.model.CallerIDKey;

@Repository
public interface CallerIDRepository extends JpaRepository<CallerID, CallerIDKey> {

	List<CallerID> findAll();

	@SuppressWarnings("unchecked")
	CallerID save(CallerID callerID);

}
