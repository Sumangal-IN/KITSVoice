package com.tcs.kitsvoice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name = "action_element")
public class ActionElement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	long id;

	@Column(name = "caller_Sid", nullable = false)
	String callerSid;

	@Column(name = "intent", nullable = false)
	String intent;

	@Column(name = "action", nullable = false)
	String action;

	@Column(name = "parameter", nullable = false)
	String parameter;
	
	@Column(name = "expected_intent", nullable = false)
	String expectedIntent;

	public ActionElement() {

	}

	public ActionElement(String callerSid, String intent, String action, String parameter, String expectedIntent) {
		super();
		this.callerSid = callerSid;
		this.intent = intent;
		this.action = action;
		this.parameter = parameter;
		this.expectedIntent=expectedIntent;
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

	public String getIntent() {
		return intent;
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getExpectedIntent() {
		return expectedIntent;
	}

	public void setExpectedIntent(String expectedIntent) {
		this.expectedIntent = expectedIntent;
	}

	@Override
	public String toString() {
		return "ActionElement [id=" + id + ", callerSid=" + callerSid + ", intent=" + intent + ", action=" + action + ", parameter=" + parameter + ", expectedIntent=" + expectedIntent + "]";
	}

}
