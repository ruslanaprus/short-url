package org.goit.urlshortener.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "urls")
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "url_seq")
    @SequenceGenerator(name = "url_seq", sequenceName = "seq_urls_id", allocationSize = 1)
    private Long id;

    @Column(name = "short_code", nullable = false, unique = true)
    @NotNull
    private String shortCode;

    @Column(name = "original_url", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Original URL cannot be null")
    @URL(message = "Original URL must be a valid URL")
    private String originalUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "click_count", nullable = false)
    @Builder.Default
    private Long clickCount = 0L;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void setCreationTimestamp() {
        createdAt = LocalDateTime.now();
    }
}
