package com.tcs.kitsvoice.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name = "call_log")
public class CallLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	long id;

	@Column(name = "caller_sid", nullable = false)
	String callerSid;

	@Column(name = "direction", nullable = false)
	String direction;

	@Column(name = "transcript", nullable = false)
	String transcript;

	@Column(name = "intent", nullable = false)
	String intent;

	@Column(name = "call_time", nullable = false)
	Timestamp callTime;

	public CallLog() {

	}

	public CallLog(String callerSid, String direction, String transcript, String intent, Timestamp callTime) {
		super();
		this.callerSid = callerSid;
		this.direction = direction;
		this.transcript = transcript;
		this.intent = intent;
		this.callTime = callTime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCallerSid() {
		return callerSid;
	}

	public void setCallerSid(String callerSid) {
		this.callerSid = callerSid;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getTranscript() {
		return transcript;
	}

	public void setTranscript(String transcript) {
		this.transcript = transcript;
	}

	public String getIntent() {
		return intent;
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}

	public Timestamp getCallTime() {
		return callTime;
	}

	public void setCallTime(Timestamp callTime) {
		this.callTime = callTime;
	}

}
