package org.goit.urlshortener.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@ToString(exclude = "urls")
@Data
@NoArgsConstructor
public class User {
    @Id
    @SequenceGenerator(allocationSize = 1, name = "users_seq", sequenceName = "seq_users_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @Setter(AccessLevel.PRIVATE)
    @Column(updatable = false, nullable = false)
    private Long id;


    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email cannot be blank")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$",
            message = "Invalid email format"
    )
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Password cannot be blank")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = """
                    Password must meet the following requirements:
                    - At least 8 characters in length
                    - At least one lowercase letter
                    - At least one uppercase letter
                    - At least one number
                    """
    )
    private String password;


    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Url> urls;


    @Builder
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }


    @PrePersist
    protected void setCreationTimestamp() {
        createdAt = LocalDateTime.now();
    }
}