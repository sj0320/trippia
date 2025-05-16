package com.trippia.travel.domain.post.diaryplace;

import com.trippia.travel.TestConfig;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.location.place.Place;
import com.trippia.travel.domain.location.place.PlaceRepository;
import com.trippia.travel.domain.post.diary.Diary;
import com.trippia.travel.domain.post.diary.DiaryRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class DiaryPlaceRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private DiaryPlaceRepository diaryPlaceRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @DisplayName("요청한 장소가 포함된 모든 여행일지를 조회한다")
    @Test
    void findAllByPlace_PlaceId() {
        // given
        User user = createUser("email", "nick");
        User savedUser = userRepository.save(user);

        String placeId1 = "ChIJlaUrm3mZfDURv0xPXhWjoM4";
        String placeId2 = "ChIJ7xbMEwCZfDUR_HdWf-TNSDU";

        Diary diary1 = diaryRepository.save(createDiary(savedUser, "d1"));
        Diary diary2 = diaryRepository.save(createDiary(savedUser, "d2"));

        Place place1 = placeRepository.save(createPlace(placeId1, "p1"));
        Place place2 = placeRepository.save(createPlace(placeId2, "p2"));

        DiaryPlace diaryPlace1 = createDiaryPlace(diary1, place1);
        DiaryPlace diaryPlace2 = createDiaryPlace(diary1, place2);
        DiaryPlace diaryPlace3 = createDiaryPlace(diary2, place2);
        diaryPlaceRepository.saveAll(List.of(diaryPlace1, diaryPlace2, diaryPlace3));

        // when
        List<DiaryPlace> result1 = diaryPlaceRepository.findAllByPlace_PlaceId(placeId1);
        List<DiaryPlace> result2 = diaryPlaceRepository.findAllByPlace_PlaceId(placeId2);

        // then
        assertThat(result1).hasSize(1);
        assertThat(result2).hasSize(2);

    }

    private static DiaryPlace createDiaryPlace(Diary diary, Place place) {
        return DiaryPlace.builder()
                .diary(diary)
                .place(place)
                .build();
    }

    private static Place createPlace(String placeId, String name) {
        return Place.builder()
                .placeId(placeId)
                .name(name)
                .build();
    }

    private static Diary createDiary(User user, String title) {
        return Diary.builder()
                .user(user)
                .title(title)
                .build();
    }

    private User createUser(String email, String nickname) {
        return User.builder()
                .email(email)
                .password("password")
                .nickname(nickname)
                .loginType(LoginType.LOCAL)
                .role(Role.ROLE_USER)
                .build();
    }

}