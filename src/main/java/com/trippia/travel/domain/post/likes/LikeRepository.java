package com.trippia.travel.domain.post.likes;

import com.trippia.travel.domain.post.diary.Diary;
import com.trippia.travel.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Likes, Long> {

    boolean existsByUserAndDiary(User user, Diary diary);

    void deleteByUserAndDiary(User user, Diary diary);

}
