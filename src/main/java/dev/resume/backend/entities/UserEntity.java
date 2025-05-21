package dev.resume.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table(name = "users")
@Entity(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Email(message = "Email is not valid")
    private String email;

    @Length(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
