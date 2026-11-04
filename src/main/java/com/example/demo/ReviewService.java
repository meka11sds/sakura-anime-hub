
package com.example.demo;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ShowRepository showRepository;
    private final AnimeService animeService;

    public ReviewService(
            ReviewRepository reviewRepository,
            UserRepository userRepository,
            ShowRepository showRepository,
            AnimeService animeService) {

        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.showRepository = showRepository;
        this.animeService = animeService;
    }

    // CREATE: добавить отзыв
    @Transactional
    public void addReview(Long showId, int rating, String comment) {

        validateReview(rating, comment);

        if (showId == null || showId <= 0) {
            throw new IllegalArgumentException("Invalid show ID");
        }

        String username = currentUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        // Ищем аниме в базе данных
        Show show = showRepository.findById(showId)
                .orElseGet(() -> createShow(showId));

        Review review = new Review();
        review.setUser(user);
        review.setShow(show);
        review.setRating(rating);
        review.setComment(comment);

        reviewRepository.save(review);
    }

    // Если аниме отсутствует в базе, получаем его из TVmaze
    private Show createShow(Long showId) {

        Map<String, Object> showDetails =
                animeService.getShowDetails(showId);

        if (showDetails == null ||
                showDetails.get("name") == null) {

            throw new IllegalArgumentException(
                    "Show not found in TVmaze");
        }

        Show show = new Show();
        show.setId(showId);
        show.setTitle(showDetails.get("name").toString());

        return showRepository.save(show);
    }

    // READ: посмотреть отзывы
    @Transactional(readOnly = true)
    public List<Review> getReviews(Long showId) {
        return reviewRepository.findByShowId(showId);
    }

    // UPDATE: изменить свой отзыв
    @Transactional
    public void updateReview(Long id, int rating, String comment) {

        validateReview(rating, comment);

        Review review = findOwnReview(id);

        review.setRating(rating);
        review.setComment(comment);

        reviewRepository.save(review);
    }

    // DELETE: удалить свой отзыв
    @Transactional
    public void deleteReview(Long id) {

        Review review = findOwnReview(id);

        reviewRepository.delete(review);
    }

    // Проверяем, что отзыв принадлежит пользователю
    private Review findOwnReview(Long id) {

        Review review = reviewRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Review not found"));

        if (!review.getUser().getUsername()
                .equals(currentUsername())) {

            throw new AccessDeniedException(
                    "You cannot modify another user's review");
        }

        return review;
    }

    // Проверка оценки и комментария
    private void validateReview(int rating, String comment) {

        if (rating < 1 || rating > 10) {
            throw new IllegalArgumentException(
                    "Rating must be between 1 and 10");
        }

        if (comment != null && comment.length() > 1000) {
            throw new IllegalArgumentException(
                    "Comment is too long");
        }
    }

    private String currentUsername() {

        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}
