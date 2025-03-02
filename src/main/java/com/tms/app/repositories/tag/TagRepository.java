package com.tms.app.repositories.tag;

import com.tms.app.entities.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {

    @Query("SELECT t FROM Tag t WHERE t.isActive = TRUE AND t.tagName = :tagName")
    Optional<Tag> findByTagName(String tagName);
}