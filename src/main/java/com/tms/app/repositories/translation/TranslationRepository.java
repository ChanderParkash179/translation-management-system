package com.tms.app.repositories.translation;

import com.tms.app.entities.translation.Translation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TranslationRepository extends JpaRepository<Translation, UUID> {

    @Query("SELECT t FROM Translation t WHERE t.translationKey = :key AND t.locale.localeCode = :localeCode AND t.isActive = true")
    Optional<Translation> findByKeyAndLocaleCode(String key, String localeCode);

    @Query("SELECT t FROM Translation t JOIN t.locale l WHERE l.localeCode = :localeCode AND t.isActive = true")
    List<Translation> findAllByLocaleCode(String localeCode);

    @Query("SELECT t FROM Translation t JOIN t.locale l WHERE l.localeCode = :localeCode AND t.isActive = true")
    Page<Translation> findAllByLocaleCodePaginated(String localeCode, Pageable pageable);

    Page<Translation> findByTranslationKeyContainingAndIsActiveTrue(String key, Pageable pageable);

    @Query("""
            SELECT t FROM Translation t
            JOIN Locale l ON t.locale.id = l.id
            JOIN TranslationTag tt ON t.id = tt.translation.id
            JOIN Tag tag ON tag.id = tt.tag.id
            WHERE tag.tagName IN :tags
            AND l.localeCode = :localeCode AND t.isActive = true
            """)
    Page<Translation> findByTagsAndLocaleCode(List<String> tags, String localeCode, Pageable pageable);
}
