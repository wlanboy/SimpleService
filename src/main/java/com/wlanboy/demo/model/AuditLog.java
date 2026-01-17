package com.wlanboy.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true) 
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "audit_log", indexes = {
    @Index(name = "idx_audit_target", columnList = "target"),
    @Index(name = "idx_audit_counter", columnList = "counter")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditLog extends RepresentationModel<AuditLog> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long identifier;

    @NotBlank(message = "Target must not be blank")
    @Column(nullable = false)
    private String target;

    @NotBlank(message = "Status must not be blank")
    @Column(nullable = false)
    private String status;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updateDateTime;

    @NotBlank
    @Column(nullable = false, length = 64) // SHA-256 Länge
    private String hash;

    @NotBlank
    @Column(nullable = false, name = "previous_hash", length = 64)
    private String previousHash;

    @NotNull
    @Column(nullable = false)
    private Long counter;
}