package com.tcs.kitsvoice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcs.kitsvoice.model.ActionElement;

@Repository
public interface ActionElementRepository extends JpaRepository<ActionElement, Long> {

	List<ActionElement> findAll();

	List<ActionElement> findByCallerSidOrderByIdDesc(String callerSid);

	List<ActionElement> findByCallerSidAndIntent(String callerSid, String intent);

	List<ActionElement> findByCallerSidAndAction(String CallerSid, String action);

}
