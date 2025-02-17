package com.Gen2Play.AuthorizationService.model.entity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID accountId;
    @NotNull
    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;
    private String password;
    private String userName;

    @NotNull
    private boolean status;

    private LocalDateTime createdAt;

    private UUID createdBy;

    private LocalDateTime updatedAt;

    private UUID updatedBy;

    private LocalDateTime deletedAt;

    private UUID deletedBy;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "roleId", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<OAuthRefreshToken> refreshTokens;

    
}
