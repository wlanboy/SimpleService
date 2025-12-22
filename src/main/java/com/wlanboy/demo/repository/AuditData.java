package com.wlanboy.demo.repository;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.*;

@Entity
@Table(name = "tbl_audit")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String target;

    @Column(nullable = false)
    private String status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    private LocalDateTime updateDateTime;

    private String hash;

    @Column(name = "previous_hash")
    private String previousHash;    

    private Long counter;
}
