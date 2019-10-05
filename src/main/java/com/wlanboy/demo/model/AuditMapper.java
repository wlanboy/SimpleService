package com.wlanboy.demo.model;

import org.springframework.security.crypto.bcrypt.BCrypt;

import com.wlanboy.demo.repository.AuditData;

public final class AuditMapper {

	public static AuditData getAuditData(AuditLog entity) {
		AuditData copy = new AuditData();
		copy.setId(entity.getIdentifier());
		copy.setStatus(entity.getStatus());
		copy.setTarget(entity.getTarget());
		copy.setHash(BCrypt.hashpw(entity.getTarget() + entity.getStatus() + entity.getCounter(), BCrypt.gensalt()));
		return copy;
	}

	public static AuditLog getAuditLog(AuditData entity) {
		AuditLog copy = new AuditLog();

		copy.setIdentifier(entity.getId());
		copy.setTarget(entity.getTarget());
		copy.setStatus(entity.getStatus());
		copy.setHash(entity.getHash());
		copy.setCreateDateTime(entity.getCreateDateTime());
		copy.setUpdateDateTime(entity.getUpdateDateTime());
		return copy;
	}
}
