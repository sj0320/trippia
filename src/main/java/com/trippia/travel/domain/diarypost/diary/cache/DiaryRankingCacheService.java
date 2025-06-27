package com.trippia.travel.domain.diarypost.diary.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trippia.travel.controller.dto.diary.response.DiaryThumbnailResponse;
import com.trippia.travel.domain.diarypost.diary.Diary;
import com.trippia.travel.domain.diarypost.diary.DiaryRepository;
import com.trippia.travel.exception.diary.DiaryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryRankingCacheService {

    private final RedisTemplate<String, byte[]> byteRedisTemplate;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final DiaryRepository diaryRepository;

    private static final String TOP_DIARIES_KEY = "top_diaries";
    private static final int MAX_RANKING_SIZE = 10;
    private static final Duration DIARY_CACHE_TTL = Duration.ofDays(1); // 1일 TTL

//    public List<DiaryThumbnailResponse> getTopDiaries() {
//        Set<String> diaryKeys = redisTemplate.opsForZSet().reverseRange(TOP_DIARIES_KEY, 0, -1);
//        if (diaryKeys == null || diaryKeys.isEmpty()) {
//            return List.of();
//        }
//
//        List<String> jsonList = redisTemplate.opsForValue().multiGet(new ArrayList<>(diaryKeys));
//        List<DiaryThumbnailResponse> result = new ArrayList<>();
//
//        if (jsonList != null) {
//            for (String json : jsonList) {
//                if (json != null) {
//                    try {
//                        result.add(objectMapper.readValue(json, DiaryThumbnailResponse.class));
//                    } catch (JsonProcessingException e) {
//                        log.warn("역직렬화 실패", e);
//                    }
//                }
//            }
//        }
//
//        return result;
//    }

    public List<DiaryThumbnailResponse> getTopDiaries() {
        Set<String> diaryKeys = redisTemplate.opsForZSet().reverseRange(TOP_DIARIES_KEY, 0, -1);
        if (diaryKeys == null || diaryKeys.isEmpty()) {
            return List.of();
        }

        List<byte[]> packedList = byteRedisTemplate.opsForValue().multiGet(new ArrayList<>(diaryKeys));
        List<DiaryThumbnailResponse> result = new ArrayList<>();

        if (packedList != null) {
            for (byte[] packed : packedList) {
                if (packed != null) {
                    try {
                        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(packed);
                        Long id = unpacker.unpackLong();
                        String title = unpacker.unpackString();
                        String thumbnail = unpacker.unpackString();
                        unpacker.close();

                        result.add(DiaryThumbnailResponse.builder()
                                .id(id)
                                .title(title)
                                .thumbnail(thumbnail)
                                .build());
                    } catch (IOException e) {
                        log.warn("역직렬화 실패", e);
                    }
                }
            }
        }

        return result;
    }

    public void updateRankingOnLike(Long diaryId, int likeCount) {
        String diaryKey = "diary:" + diaryId;

        // top_diaries 에 속하는 경우 score 를 update 한다.
        if (redisTemplate.opsForZSet().rank(TOP_DIARIES_KEY, diaryKey) != null) {
            redisTemplate.opsForZSet().add(TOP_DIARIES_KEY, diaryKey, likeCount);
            return;
        }

        // top_diaries 가 비어있거나, size < 10이면 그냥 추가
        Long currentSize = redisTemplate.opsForZSet().zCard(TOP_DIARIES_KEY);
        if (currentSize == null || currentSize < MAX_RANKING_SIZE) {
            redisTemplate.opsForZSet().add(TOP_DIARIES_KEY, diaryKey, likeCount);
            cacheDiaryThumbnailIfNotExists(diaryId, diaryKey);
            return;
        }

        // top_diaries 의 최하위 score 조회
        Set<ZSetOperations.TypedTuple<String>> lowestEntrySet = redisTemplate.opsForZSet().rangeWithScores(TOP_DIARIES_KEY, 0, 0);

        // 최하위 score 와 비교
        if (lowestEntrySet != null && !lowestEntrySet.isEmpty()) {
            ZSetOperations.TypedTuple<String> lowest = lowestEntrySet.iterator().next();
            Double lowestScore = lowest.getScore();

            if (lowestScore != null && likeCount > lowestScore) {
                redisTemplate.opsForZSet().remove(TOP_DIARIES_KEY, lowest.getValue());
                redisTemplate.opsForZSet().add(TOP_DIARIES_KEY, diaryKey, likeCount);

                // size 10 유지
                redisTemplate.opsForZSet().removeRange(TOP_DIARIES_KEY, 0, -MAX_RANKING_SIZE - 1);
                cacheDiaryThumbnailIfNotExists(diaryId, diaryKey);
            }
        }
    }

    public void updateCacheIfTopDiary(Long diaryId, String title, String thumbnail) throws JsonProcessingException {
        String diaryKey = "diary:" + diaryId;

        // top_diaries 에 속하는 diary 라면 캐시 업데이트
        if (redisTemplate.opsForZSet().rank(TOP_DIARIES_KEY, diaryKey) != null) {
            Diary diary = getDiary(diaryId);
            boolean titleChanged = !Objects.equals(diary.getTitle(), title);
            boolean thumbnailChanged = !Objects.equals(diary.getThumbnail(), thumbnail);
            if (titleChanged || thumbnailChanged) {
                DiaryThumbnailResponse response = DiaryThumbnailResponse.from(diary);
                String diaryJson = objectMapper.writeValueAsString(response);
                redisTemplate.opsForValue().set(diaryKey, diaryJson, DIARY_CACHE_TTL);
            }
        }
    }

    public void removeCacheIfTopDiary(Long diaryId) {
        String diaryKey = "diary:" + diaryId;
        if (redisTemplate.opsForZSet().rank(TOP_DIARIES_KEY, diaryKey) != null) {
            redisTemplate.opsForZSet().remove(TOP_DIARIES_KEY, diaryKey);
            redisTemplate.delete(diaryKey);
        }
    }

//    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
//    public void refreshTopDiariesCache() throws JsonProcessingException {
//        redisTemplate.delete("top_diaries");
//
//        List<Diary> topDiaries = diaryRepository.findTopDiaries(PageRequest.of(0, 10));
//        for (Diary diary : topDiaries) {
//            String key = "diary:" + diary.getId();
//            String value = objectMapper.writeValueAsString(DiaryThumbnailResponse.from(diary));
//            redisTemplate.opsForValue().set(key, value, DIARY_CACHE_TTL);
//            redisTemplate.opsForZSet().add("top_diaries", key, diary.getLikeCount());
//        }
//    }

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
    public void refreshTopDiariesCache() throws IOException {
        // 정렬된 집합 삭제
        redisTemplate.delete("top_diaries");

        List<Diary> topDiaries = diaryRepository.findTopDiaries(PageRequest.of(0, 10));
        for (Diary diary : topDiaries) {
            String key = "diary:" + diary.getId();

            // DiaryThumbnailResponse 객체 생성
            DiaryThumbnailResponse dto = DiaryThumbnailResponse.from(diary);

            // MessagePack 직렬화
            MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
            packer.packLong(dto.getId());
            packer.packString(dto.getTitle());
            packer.packString(dto.getThumbnail());
            packer.close();

            byte[] value = packer.toByteArray();

            // 바이너리 RedisTemplate로 저장
            byteRedisTemplate.opsForValue().set(key, value, DIARY_CACHE_TTL);

            // 정렬된 집합에는 문자열 키만 저장
            redisTemplate.opsForZSet().add("top_diaries", key, diary.getLikeCount());
        }
    }


    private void cacheDiaryThumbnailIfNotExists(Long diaryId, String diaryKey) {
        Boolean exists = redisTemplate.hasKey(diaryKey);
        if (Boolean.FALSE.equals(exists)) {
            Diary diary = getDiary(diaryId);
            DiaryThumbnailResponse response = DiaryThumbnailResponse.from(diary);
            try {
                String diaryJson = objectMapper.writeValueAsString(response);
                redisTemplate.opsForValue().set(diaryKey, diaryJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Diary getDiary(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException("여행일지를 찾을 수 없습니다."));
    }

}
