
package com.example.demo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "shows")
@Getter
@Setter
public class Show {

    @Id
    private Long id; // ID аниме из TVmaze

    @Column(nullable = false)
    private String title;
}
