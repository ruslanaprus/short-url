package org.goit.urlshortener.repository;

import org.goit.urlshortener.model.Url;
import org.goit.urlshortener.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Page<Url> findByUserId(Long userId, Pageable pageable);

    Page<Url> findByUser(@Param("user") User user, Pageable pageable);

    Optional<Url> findByShortCode(String shortCode);

    Optional<Url> findByIdAndUser(@Param("id") Long id, @Param("user") User user);

    @Query("SELECT u.originalUrl FROM Url u WHERE u.shortCode = :shortCode")
    Optional<String> findOriginalUrlByShortCode(@Param("shortCode") String shortCode);

    boolean existsByShortCode(String shortCode);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
           "FROM Url u WHERE u.id = :urlId AND u.expiresAt > :now")
    boolean existsActiveUrlById(@Param("urlId") Long urlId, @Param("now") LocalDateTime now);
}