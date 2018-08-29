package com.wlanboy.demo.model;

import org.springframework.hateoas.ResourceSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("unused")
@Data
@EqualsAndHashCode(callSuper=false)
public class HelloParameters extends ResourceSupport {
	private Long identifier;
	private String target;
	private String status;

	public HelloParameters(Long identifier, String target, String status) {
		this.identifier = identifier;
		this.target = target;
		this.status = status;
	}
}
