
package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WatchlistService {

    @Autowired
    private WatchItemRepository watchlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShowRepository showRepository;

    // Получение списка аниме по статусу для текущего пользователя
    public List<WatchItem> getItemsByStatus(String status) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        return watchlistRepository.findByUserUsernameAndStatus(username, status);
    }

    @Transactional
    // Добавление или обновление аниме в Watchlist
    public void saveItem(Long showId, String title,
                         String status, int currentEpisode) {

        if (status == null ||
                !List.of("watching", "planned", "completed", "dropped")
                        .contains(status)) {
            throw new IllegalArgumentException("Invalid watchlist status");
        }

        if (currentEpisode < 1) {
            throw new IllegalArgumentException(
                    "Episode number must be at least 1");
        }

        if (showId == null || showId <= 0) {
            throw new IllegalArgumentException("Invalid show ID");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Show title is required");
        }

// Сохраняем аниме в общий каталог, если его там ещё нет
        if (!showRepository.existsById(showId)) {
            Show show = new Show();
            show.setId(showId);
            show.setTitle(title);
            showRepository.save(show);
        }


        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + username));

        // Проверяем, есть ли уже такое аниме у пользователя
        WatchItem item = watchlistRepository.findByUserAndShowId(user, showId)
                .orElse(new WatchItem());

        // Заполняем данные
        item.setShowId(showId);
        item.setTitle(title);
        item.setStatus(status);
        item.setCurrentEpisode(currentEpisode);
        item.setUser(user);

        // Сохраняем или обновляем запись
        watchlistRepository.save(item);
    }

    // Безопасное удаление аниме из Watchlist
    public void deleteItem(Long id) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        // Находим запись по ID
        WatchItem item = watchlistRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Watchlist item not found"));

        // Проверяем, принадлежит ли запись текущему пользователю
        if (item.getUser() == null ||
                !username.equals(item.getUser().getUsername())) {

            throw new AccessDeniedException(
                    "You cannot delete another user's watchlist item");
        }

        // Удаляем только свою запись
        watchlistRepository.delete(item);
    }
}

