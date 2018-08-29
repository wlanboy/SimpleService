package com.wlanboy.demo.model;

import org.springframework.hateoas.ResourceSupport;

public class HelloParameters extends ResourceSupport {
	private Long identifier;
	private String target;
	private String status;
	
	public HelloParameters() {
		
	}

	public HelloParameters(Long counter, String target, String status) {
		this.identifier = counter;
		this.target = target;
		this.status = status;
	}	
	
	public HelloParameters(Vorgang vorgang) {
		this.identifier = vorgang.getId();
		this.target = vorgang.getName();
		this.status = vorgang.getStatus();
		
	}	

	public Long getIdentifier() {
		return identifier;
	}
	public void setIdentifier(Long identifier) {
		this.identifier = identifier;
	}

	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "HelloParameters [Identifier=" + identifier + ",target=" + target + ", status=" + status + "]";
	}

}
