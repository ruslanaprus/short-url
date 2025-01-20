package org.goit.urlshortener.repository;

import org.goit.urlshortener.model.Url;
import org.goit.urlshortener.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    Page<Url> findByUser(@Param("user") User user, Pageable pageable);

    Optional<Url> findByShortCode(String shortCode);

    Optional<Url> findByIdAndUser(@Param("id") Long id, @Param("user") User user);

    boolean existsByShortCode(String shortCode);

    @Query("SELECT u FROM Url u WHERE u.user = :user AND (u.expiresAt IS NULL OR u.expiresAt > CURRENT_TIMESTAMP)")
    Page<Url> findActiveUrlsByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT u FROM Url u WHERE u.user = :user AND u.expiresAt <= CURRENT_TIMESTAMP")
    Page<Url> findExpiredUrlsByUser(@Param("user") User user, Pageable pageable);
}