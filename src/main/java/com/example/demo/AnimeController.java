
package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class AnimeController {

    @Autowired
    private AnimeService animeService;

    @Autowired
    private WatchlistService watchlistService;

    @GetMapping("/search-page")
    public String searchPage(
            @RequestParam(name = "q", required = false) String query,
            Model model) {

        if (query != null && !query.isEmpty()) {
            List<Map<String, Object>> searchResults =
                    animeService.searchAnime(query);

            model.addAttribute("results", searchResults);
        }

        return "search";
    }

    @GetMapping("/movie-details")
    public String movieDetails(
            @RequestParam("id") Long id,
            Model model) {

        Map<String, Object> show = animeService.getShowDetails(id);

        model.addAttribute("show", show);

        return "details";
    }

    @GetMapping("/watchlist")
    public String viewWatchlist(Model model) {

        model.addAttribute("watchingList",
                watchlistService.getItemsByStatus("watching"));

        model.addAttribute("plannedList",
                watchlistService.getItemsByStatus("planned"));

        model.addAttribute("completedList",
                watchlistService.getItemsByStatus("completed"));

        model.addAttribute("droppedList",
                watchlistService.getItemsByStatus("dropped"));

        return "watchlist";
    }

    @PostMapping("/watchlist/add")
    public String addToWatchlist(
            @RequestParam Long showId,
            @RequestParam String title,
            @RequestParam String status,
            @RequestParam(defaultValue = "1") int currentEpisode) {

        watchlistService.saveItem(showId, title, status, currentEpisode);

        return "redirect:/watchlist";
    }

    @PostMapping("/watchlist/delete")
    public String deleteFromWatchlist(@RequestParam Long id) {

        watchlistService.deleteItem(id);

        return "redirect:/watchlist";
    }
}
