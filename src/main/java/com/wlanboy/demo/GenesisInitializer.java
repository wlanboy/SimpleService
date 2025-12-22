package com.wlanboy.demo;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wlanboy.demo.repository.AuditData;
import com.wlanboy.demo.repository.AuditRepositorySimple;

@Configuration
public class GenesisInitializer {

    private static final String GENESIS_HASH = "GENESIS";

    @Bean
    public ApplicationRunner createGenesisBlock(AuditRepositorySimple auditDB) {
        return args -> {
            if (auditDB.count() == 0) {
                AuditData genesis = AuditData.builder()
                        .target("GENESIS")
                        .status("INIT")
                        .counter(0L)
                        .previousHash("NONE")
                        .hash(GENESIS_HASH)
                        .build();

                auditDB.save(genesis);
                System.out.println("Genesis block created at startup.");
            }
        };
    }
}
