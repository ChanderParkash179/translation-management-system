package com.tms.app.entities.roleFeature;

import com.tms.app.entities.audit.AuditEntity;
import com.tms.app.entities.feature.Feature;
import com.tms.app.entities.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role_feature")
public class RoleFeature extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.VARCHAR)
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "feature_id")
    @JdbcTypeCode(Types.VARCHAR)
    private Feature feature;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id")
    @JdbcTypeCode(Types.VARCHAR)
    private Role role;
}