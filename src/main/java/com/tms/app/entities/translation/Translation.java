package com.tms.app.entities.translation;

import com.tms.app.entities.locale.Locale;
import com.tms.app.entities.translationTag.TranslationTag;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "`translation`", indexes = @Index(columnList = "translation_key, locale_id"))
public class Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @Column(name = "translation_key")
    private String translationKey;

    @Column(name = "content")
    @JdbcTypeCode(Types.LONGNVARCHAR)
    private String content;

    @ManyToOne
    @JdbcTypeCode(Types.VARCHAR)
    @JoinColumn(name = "locale_id")
    private Locale locale;

    @Column(name = "is_active")
    private Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "modified_at", insertable = false)
    private LocalDateTime modifiedAt;

    @OneToMany(mappedBy = "translation", cascade = CascadeType.ALL)
    private List<TranslationTag> translationTag;
}