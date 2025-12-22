package com.wlanboy.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "audit_log")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditLog extends RepresentationModel<AuditLog> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long identifier;

	@NotBlank
    @Column(nullable = false)
    private String target;

	@NotBlank
    @Column(nullable = false)
    private String status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    private LocalDateTime updateDateTime;

    private String hash;

    @Column(nullable = false, name = "previous_hash")
    private String previousHash;

    private Long counter;
}
