package com.trippia.travel.domain.post;

import com.trippia.travel.domain.theme.Theme;
import jakarta.persistence.*;

@Entity
public class PostTheme {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
