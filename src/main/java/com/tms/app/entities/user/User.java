package com.tms.app.entities.user;

import com.tms.app.entities.role.Role;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.swagger.v3.oas.annotations.media.Schema;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "`user`", indexes = @Index(columnList = "email", unique = true))
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.BINARY)
    @Schema(description = "User ID", example = "")
    private UUID id;

    @Column(name = "username", unique = true)
    @Schema(description = "User name", example = "John123")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true)
    @Schema(description = "User email", example = "john@example.com")
    private String email;

    @Column(name = "first_name")
    @Schema(description = "User firstname", example = "John")
    private String firstName;

    @Column(name = "last_name")
    @Schema(description = "User firstname", example = "Doe")
    private String lastName;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id")
    @JdbcTypeCode(Types.VARCHAR)
    private Role role;

    @Column(name = "is_active")
    private Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "modified_at", insertable = false)
    private LocalDateTime modifiedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.of(role)
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getFullName() {
        return this.getFirstName() + " " + ((this.getLastName() != null) ? this.getLastName() : "");
    }
}