package com.wlanboy.demo.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Einzelner Eintrag im unveränderlichen Audit-Log mit SHA-256-Hash-Kette")
public class AuditLog extends RepresentationModel<AuditLog> {

    @Schema(description = "Eindeutige Datenbank-ID des Eintrags", accessMode = Schema.AccessMode.READ_ONLY, example = "42")
    private Long identifier;

    @NotBlank(message = "Target must not be blank")
    @Size(max = 255, message = "Target must not exceed 255 characters")
    @Schema(description = "Bezeichner des Zielsystems oder der Komponente, die den Eintrag erzeugt hat", example = "user-service")
    private String target;

    @NotBlank(message = "Status must not be blank")
    @Size(max = 255, message = "Status must not exceed 255 characters")
    @Schema(description = "Status des protokollierten Ereignisses", example = "SUCCESS")
    private String status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Zeitstempel der Erstellung (wird serverseitig gesetzt)", accessMode = Schema.AccessMode.READ_ONLY, example = "2026-02-23T10:15:30.123456")
    private LocalDateTime createDateTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Zeitstempel der letzten Änderung (wird serverseitig gesetzt)", accessMode = Schema.AccessMode.READ_ONLY, example = "2026-02-23T10:15:30.123456")
    private LocalDateTime updateDateTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "SHA-256-Hash dieses Eintrags (Hex-kodiert, 64 Zeichen)", accessMode = Schema.AccessMode.READ_ONLY, example = "a3f5b2c1d4e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2")
    private String hash;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "SHA-256-Hash des vorherigen Eintrags in der Kette (Hex-kodiert, 64 Zeichen)", accessMode = Schema.AccessMode.READ_ONLY, example = "0000000000000000000000000000000000000000000000000000000000000000")
    private String previousHash;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Laufende Nummer des Eintrags in der Hash-Kette (0-basiert)", accessMode = Schema.AccessMode.READ_ONLY, example = "0")
    private Long counter;
}
