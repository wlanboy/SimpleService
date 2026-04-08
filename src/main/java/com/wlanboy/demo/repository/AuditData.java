package com.wlanboy.demo.repository;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.*;

@Entity
@Table(name = "tbl_audit", indexes = {
    @Index(name = "idx_audit_target", columnList = "target"),
    @Index(name = "idx_audit_counter", columnList = "counter")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String target;

    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    private LocalDateTime updateDateTime;

    @Column(nullable = false, length = 64)
    private String hash;

    @Column(name = "previous_hash", nullable = false, length = 64)
    private String previousHash;

    @Column(nullable = false)
    private Long counter;
}
