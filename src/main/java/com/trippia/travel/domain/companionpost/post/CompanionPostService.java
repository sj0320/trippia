package com.trippia.travel.domain.companionpost.post;

import com.trippia.travel.controller.dto.post.request.*;
import com.trippia.travel.controller.dto.post.response.CompanionPostDetailsResponse;
import com.trippia.travel.controller.dto.post.response.CompanionPostEditFormResponse;
import com.trippia.travel.controller.dto.post.response.CompanionPostListResponse;
import com.trippia.travel.controller.dto.post.response.CompanionPostSummaryResponse;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.city.CityRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.companionpost.CompanionPostException;
import com.trippia.travel.exception.user.UserException;
import com.trippia.travel.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static com.trippia.travel.exception.ErrorMessageSource.START_DATE_AFTER_END_DATE;
import static com.trippia.travel.exception.ErrorMessageSource.START_DATE_BEFORE_TODAY;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CompanionPostService {

    private final CompanionPostRepository companionPostRepository;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final FileService fileService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String VIEW_POST_REDIS_KEY = "view:post:%s:%s";


    @Transactional
    public Long savePost(String email, CompanionPostSaveRequest request, MultipartFile thumbnail) {
        User user = getUser(email);
        validateDate(request.getStartDate(), request.getEndDate());
        City city = getCity(request.getCityId());

        String thumbnailUrl = getThumbnailUrl(thumbnail, city);
        CompanionPost companionPost = request.toEntity(user, city, thumbnailUrl);
        companionPostRepository.save(companionPost);
        log.info("[동행글 저장 요청] email={}, cityId={}, title={}", email, request.getCityId(), request.getTitle());
        return companionPost.getId();
    }

    public CompanionPostDetailsResponse getPostDetails(Long postId) {
        log.info("[동행글 상세 조회] postId={}", postId);
        return CompanionPostDetailsResponse.from(getPost(postId));
    }

    public Slice<CompanionPostListResponse> searchPostList(PostSearchCondition condition, CursorData cursorData, Pageable pageable) {
        Slice<CompanionPost> posts = companionPostRepository.searchDiariesWithConditions(condition, cursorData, pageable);
        List<CompanionPostListResponse> content = CompanionPostListResponse.from(posts.getContent());
        log.info("[동행글 리스트 검색] 조건={}, 커서={}, 페이지 정보={}", condition, cursorData, pageable);
        return new SliceImpl<>(content, pageable, posts.hasNext());
    }

    @Transactional
    public void addViewCount(Long postId, String ip, String userAgent) {
        String hash = DigestUtils.md5DigestAsHex((ip + userAgent).getBytes());
        String redisKey = String.format(VIEW_POST_REDIS_KEY, postId, hash);
        if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
            CompanionPost post = getPost(postId);
            post.addViewCount();
            log.info("[동행글 조회수 증가] postId={}, ip={}, userAgent={}", postId, ip, userAgent);
            redisTemplate.opsForValue().set(redisKey, "viewed", Duration.ofHours(24));
        }
    }

    public CompanionPostEditFormResponse getPostEditForm(String email, Long postId) {
        User user = getUser(email);
        CompanionPost post = getPost(postId);
        user.validateAuthorOf(post);
        return CompanionPostEditFormResponse.from(post);
    }

    @Transactional
    public void editPost(String email, Long postId, CompanionPostUpdateRequest request, MultipartFile thumbnail) {
        User user = getUser(email);
        CompanionPost post = getPost(postId);
        user.validateAuthorOf(post);
        String thumbnailUrl = getUpdatedThumbnailUrl(thumbnail, post.getThumbnailUrl());
        City city = getCity(request.getCityId());

        UpdatePostDto updatePostDto = getUpdatePostDto(request, thumbnailUrl, city);
        post.update(updatePostDto);
    }

    @Transactional
    public void deletePost(String email, Long postId) {
        User user = getUser(email);
        CompanionPost post = getPost(postId);
        user.validateAuthorOf(post);
        companionPostRepository.deleteById(postId);
        log.info("[동행글 삭제 요청] email={}, postId={}", email, postId);
    }

    public List<CompanionPostListResponse> searchLatestPostList(int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.Direction.DESC, "createdAt");
        Page<CompanionPost> posts = companionPostRepository.findAll(pageable);

        return CompanionPostListResponse.from(posts.getContent());
    }


    public List<CompanionPostSummaryResponse> getPostSummaryByUser(String email) {
        User user = getUser(email);
        List<CompanionPost> posts = companionPostRepository.findAllWithCommentsByUserId(user.getId());
        return posts.stream()
                .map(CompanionPostSummaryResponse::from)
                .toList();
    }

    private UpdatePostDto getUpdatePostDto(CompanionPostUpdateRequest request, String thumbnailUrl, City city) {
        return UpdatePostDto.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .genderRestriction(request.getGenderRestriction())
                .recruitmentCount(request.getRecruitmentCount())
                .city(city)
                .thumbnailUrl(thumbnailUrl)
                .build();
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다."));
    }

    private City getCity(Long cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() -> new CompanionPostException("도시 정보를 찾을 수 없습니다."));
    }

    private CompanionPost getPost(Long id) {
        return companionPostRepository.findById(id)
                .orElseThrow(() -> new CompanionPostException("게시물을 찾을 수 없습니다."));
    }

    private void validateDate(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new CompanionPostException("startDate", START_DATE_AFTER_END_DATE);
        }

        if (startDate.isBefore(LocalDate.now()) || endDate.isBefore(LocalDate.now())) {
            throw new CompanionPostException("startDate", START_DATE_BEFORE_TODAY);
        }
    }

    private String getThumbnailUrl(MultipartFile thumbnail, City city) {
        if (!thumbnail.isEmpty()) {
            return fileService.uploadFile(thumbnail).getUrl();
        }
        return city.getImageUrl();
    }

    private String getUpdatedThumbnailUrl(MultipartFile newThumbnail, String existingThumbnail) {
        if (newThumbnail != null && !newThumbnail.isEmpty()) {
            return fileService.uploadFile(newThumbnail).getUrl();
        }
        return existingThumbnail;
    }

}
