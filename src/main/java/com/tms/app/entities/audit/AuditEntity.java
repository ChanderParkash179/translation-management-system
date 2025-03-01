package com.tms.app.entities.audit;

import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.*;
import com.tms.app.entities.user.User;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Types;
import java.time.LocalDateTime;

@Setter
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditEntity {

    @Column(name = "is_active")
    private Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JdbcTypeCode(Types.VARCHAR)
    @JoinColumn(name = "created_by", updatable = false)
    private User createdBy;

    @ManyToOne
    @JdbcTypeCode(Types.VARCHAR)
    @JoinColumn(name = "updated_by", insertable = false)
    private User updatedBy;
}