package com.tcs.kitsvoice.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@Table(name = "caller_ID")
@IdClass(CallerIDKey.class)
public class CallerID {

	@Id
	@Column(name = "caller_number", nullable = false)
	String callerNumber;

	@Id
	@Column(name = "caller_sid", nullable = false)
	String callerSid;

	@Column(name = "call_time", nullable = false)
	Timestamp callTime;

	public CallerID() {

	}

	public CallerID(String callerNumber, String callSid, Timestamp callTime) {
		super();
		this.callerNumber = callerNumber;
		this.callerSid = callSid;
		this.callTime = callTime;
	}

	public String getCallerNumber() {
		return callerNumber;
	}

	public void setCallerNumber(String callerNumber) {
		this.callerNumber = callerNumber;
	}

	public String getCallSid() {
		return callerSid;
	}

	public void setCallSid(String callSid) {
		this.callerSid = callSid;
	}

	public Timestamp getCallTime() {
		return callTime;
	}

	public void setCallTime(Timestamp callTime) {
		this.callTime = callTime;
	}

	@Override
	public String toString() {
		return "CallerID [callerNumber=" + callerNumber + ", callerSid=" + callerSid + ", callTime=" + callTime + "]";
	}

}
