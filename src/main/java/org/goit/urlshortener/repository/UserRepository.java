package org.goit.urlshortener.repository;

import org.goit.urlshortener.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
