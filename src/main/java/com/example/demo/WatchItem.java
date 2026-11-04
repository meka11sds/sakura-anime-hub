package com.example.demo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "watch_items")
@Getter
@Setter
public class WatchItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long showId;
    private String title;
    private String status; // watching, planned, completed, dropped
    private int currentEpisode = 1;

    // Привязка к пользователю (имя поля "user" должно совпадать с mappedBy = "user" в классе User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}