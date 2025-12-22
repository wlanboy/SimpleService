package com.wlanboy.demo.model;

import org.springframework.security.crypto.bcrypt.BCrypt;

import com.wlanboy.demo.repository.AuditData;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AuditMapper {

    public AuditData toEntity(AuditLog log, String previousHash) {
        if (log == null) {
            return null;
        }

        return AuditData.builder()
                .target(log.getTarget())
                .status(log.getStatus())
                .previousHash(previousHash)
                .hash(hash(log, previousHash))
                .counter(log.getCounter() != null ? log.getCounter() : 0L)
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
                .previousHash(data.getPreviousHash())
                .createDateTime(data.getCreateDateTime())
                .updateDateTime(data.getUpdateDateTime())
                .counter(data.getCounter())
                .build();
    }

    private String hash(AuditLog log, String previousHash) {
        Long counter = log.getCounter() != null ? log.getCounter() : 0L;
        String input = log.getTarget() + log.getStatus() + counter + previousHash;
        return BCrypt.hashpw(input, BCrypt.gensalt());
    }
}

