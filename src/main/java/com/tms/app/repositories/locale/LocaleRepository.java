package com.tms.app.repositories.locale;

import com.tms.app.entities.locale.Locale;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface LocaleRepository extends JpaRepository<Locale, UUID> {

    @Query(value = "SELECT * FROM locale WHERE LOWER(locale_code) = LOWER(:localeCode) AND is_active = TRUE", nativeQuery = true)
    Optional<Locale> findByLocaleCode(@Param("localeCode") String localeCode);
}