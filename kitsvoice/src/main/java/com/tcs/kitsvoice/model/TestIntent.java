package com.tcs.kitsvoice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name = "test_intent")
public class TestIntent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	long id;

	@Column(name = "sentence", nullable = false)
	String sentence;

	@Column(name = "expected_intent", nullable = false)
	String expectedIntent;

	public TestIntent() {

	}

	public TestIntent(long id, String sentence, String expectedIntent) {
		super();
		this.id = id;
		this.sentence = sentence;
		this.expectedIntent = expectedIntent;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public String getExpectedIntent() {
		return expectedIntent;
	}

	public void setExpectedIntent(String expected_intent) {
		this.expectedIntent = expected_intent;
	}

}
