package com.wlanboy.demo.model;

import org.springframework.hateoas.ResourceSupport;

public class HelloParameters extends ResourceSupport {
	private Integer counter;
	private String target;
	private String status;
	
	public HelloParameters() {
		
	}

	public HelloParameters(Integer counter, String target, String status) {
		this.counter = counter;
		this.target = target;
		this.status = status;
	}	

	public Integer getCounter() {
		return counter;
	}
	public void setCounter(Integer counter) {
		this.counter = counter;
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
		return "HelloParameters [Counter=" + counter + ",target=" + target + ", status=" + status + "]";
	}

}
