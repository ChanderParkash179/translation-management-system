package com.tms.app.repositories.translationTag;

import com.tms.app.entities.translationTag.TranslationTag;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface TranslationTagRepository extends JpaRepository<TranslationTag, UUID> {

    @Modifying
    @Transactional
    @Query("DELETE FROM TranslationTag tt WHERE tt.translation.id = :translationId AND tt.isActive = TRUE")
    void deleteByTranslationId(UUID translationId);
}