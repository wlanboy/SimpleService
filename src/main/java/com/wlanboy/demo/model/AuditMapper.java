package com.wlanboy.demo.model;

import org.springframework.security.crypto.bcrypt.BCrypt;

import com.wlanboy.demo.repository.AuditData;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AuditMapper {

    public AuditData toEntity(AuditLog log) {
        if (log == null) {
            return null;
        }

        return AuditData.builder()
                .id(log.getIdentifier())
                .target(log.getTarget())
                .status(log.getStatus())
                .hash(hash(log))
                .counter(log.getCounter() != null ? Long.valueOf(log.getCounter()) : null)
                .build();
    }

    public AuditLog toModel(AuditData data) {
        if (data == null) {
            return null;
        }

        return AuditLog.builder()
                .identifier(data.getId())
                .target(data.getTarget())
                .status(data.getStatus())
                .hash(data.getHash())
                .createDateTime(data.getCreateDateTime())
                .updateDateTime(data.getUpdateDateTime())
                .counter(data.getCounter())
                .build();
    }

    private String hash(AuditLog log) {
        String input = log.getTarget() + log.getStatus() + log.getCounter();
        return BCrypt.hashpw(input, BCrypt.gensalt());
    }
}
