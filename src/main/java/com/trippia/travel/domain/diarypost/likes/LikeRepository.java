package com.trippia.travel.domain.diarypost.likes;

import com.trippia.travel.domain.diarypost.diary.Diary;
import com.trippia.travel.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Likes, Long> {

    boolean existsByUserAndDiary(User user, Diary diary);

    void deleteByUserAndDiary(User user, Diary diary);


    List<Likes> findAllByUserId(Long id);
}
