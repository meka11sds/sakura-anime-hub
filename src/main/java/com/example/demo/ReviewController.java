
package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // READ: страница отзывов
    @GetMapping
    public String listReviews(
            @RequestParam Long showId,
            Model model) {

        model.addAttribute("reviews",
                reviewService.getReviews(showId));
        model.addAttribute("showId", showId);

        model.addAttribute("currentUsername",
                org.springframework.security.core.context
                        .SecurityContextHolder.getContext()
                        .getAuthentication().getName());

        return "reviews";
    }

    // CREATE: добавить отзыв
    @PostMapping("/add")
    public String addReview(
            @RequestParam Long showId,
            @RequestParam int rating,
            @RequestParam(required = false) String comment) {

        reviewService.addReview(showId, rating, comment);
        return "redirect:/reviews?showId=" + showId;
    }

    // UPDATE: изменить отзыв
    @PostMapping("/update")
    public String updateReview(
            @RequestParam Long id,
            @RequestParam Long showId,
            @RequestParam int rating,
            @RequestParam(required = false) String comment) {

        reviewService.updateReview(id, rating, comment);
        return "redirect:/reviews?showId=" + showId;
    }

    // DELETE: удалить отзыв
    @PostMapping("/delete")
    public String deleteReview(
            @RequestParam Long id,
            @RequestParam Long showId) {

        reviewService.deleteReview(id);
        return "redirect:/reviews?showId=" + showId;
    }
}
