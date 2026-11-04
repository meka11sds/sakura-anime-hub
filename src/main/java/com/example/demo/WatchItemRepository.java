package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WatchItemRepository extends JpaRepository<WatchItem, Long> {
    List<WatchItem> findByUserUsernameAndStatus(String username, String status);

    // Метод для поиска конкретного аниме у конкретного пользователя
    Optional<WatchItem> findByUserAndShowId(User user, Long showId);
}