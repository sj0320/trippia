package com.trippia.travel.domain.post.diarytheme;

import com.trippia.travel.domain.post.diary.Diary;
import com.trippia.travel.domain.theme.Theme;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryTheme {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_theme_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    public static DiaryTheme createDiaryTheme(Theme theme, Diary diary){
        return DiaryTheme.builder()
                .theme(theme)
                .diary(diary)
                .build();
    }

}
