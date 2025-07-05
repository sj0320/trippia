package com.trippia.travel.controller.api;

import com.trippia.travel.GlobalControllerAdvice;
import com.trippia.travel.domain.location.place.PlaceService;
import com.trippia.travel.ratelimiter.RateLimiterService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PlaceApiController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = GlobalControllerAdvice.class
                )
        }
)
@AutoConfigureMockMvc(addFilters = false)
class PlaceApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaceService placeService;

    @MockitoBean
    private RateLimiterService rateLimiterService;

    @Test
    void testGlobalAutocompletePlace() throws Exception {
        // given
        Bucket mockBucket = Bucket4j.builder()
                .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofSeconds(10))))
                .build();

        given(rateLimiterService.resolveBucket(anyString())).willReturn(mockBucket);
        given(placeService.getAutocompletePlace("서울")).willReturn(Set.of());

        // when & then
        mockMvc.perform(get("/api/places/recommend")
                        .param("query", "서울")
                        .header("X-Forwarded-For", "123.123.123.123"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testRateLimitExceeded() throws Exception {
        // given
        Bucket mockBucket = mock(Bucket.class);
        given(rateLimiterService.resolveBucket(anyString())).willReturn(mockBucket);
        given(mockBucket.tryConsume(1)).willReturn(false);

        // when & then
        mockMvc.perform(get("/api/places/recommend")
                        .param("query", "서울")
                        .header("X-Forwarded-For", "123.123.123.123")) // IP 설정
                .andExpect(status().isTooManyRequests())
                .andExpect(content().string("요청이 너무 많습니다. 잠시 후 다시 시도해주세요."));
    }

}