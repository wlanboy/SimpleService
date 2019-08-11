package com.wlanboy.demo.model;

import org.springframework.hateoas.ResourceSupport;

@SuppressWarnings("unused")
public class HelloParameters extends ResourceSupport {
	private Long identifier;
	private String target;
	private String status;

	public HelloParameters(Long identifier, String target, String status) {
		this.identifier = identifier;
		this.target = target;
		this.status = status;
	}

	public HelloParameters() {

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
	
}
