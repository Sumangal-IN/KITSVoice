package com.tcs.kitsvoice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcs.kitsvoice.model.CallLog;

@Repository
public interface CallLogRepository extends JpaRepository<CallLog, Long> {

	List<CallLog> findAll();

	@SuppressWarnings("unchecked")
	CallLog save(CallLog callLog);

}
