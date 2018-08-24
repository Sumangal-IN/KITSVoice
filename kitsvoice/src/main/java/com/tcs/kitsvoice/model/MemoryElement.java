package com.tcs.kitsvoice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name = "memory_stick")
public class MemoryElement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	long id;

	@Column(name = "caller_Sid", nullable = false)
	String callerSid;

	@Column(name = "variable", nullable = false)
	String variable;

	@Column(name = "type", nullable = false)
	String type;

	@Column(name = "value", nullable = false)
	String value;

	public MemoryElement() {

	}

	public MemoryElement(String callerSid, String variable, String type, String value) {
		super();
		this.callerSid = callerSid;
		this.variable = variable;
		this.type = type;
		this.value = value;
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

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "MemoryElement [id=" + id + ", callerSid=" + callerSid + ", variable=" + variable + ", type=" + type + ", value=" + value + "]";
	}

}
