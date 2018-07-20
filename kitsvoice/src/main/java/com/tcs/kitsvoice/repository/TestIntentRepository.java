package com.tcs.kitsvoice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcs.kitsvoice.model.TestIntent;

@Repository
public interface TestIntentRepository extends JpaRepository<TestIntent, Long> {

	List<TestIntent> findAll();

}
