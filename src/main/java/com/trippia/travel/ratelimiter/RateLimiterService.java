package com.trippia.travel.ratelimiter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RateLimiterService {

    private final Map<String, Bucket> userRateLimitCache;

    public Bucket resolveBucket(String userIp){
        return userRateLimitCache.computeIfAbsent(userIp, key -> {
            Refill refill = Refill.intervally(5, Duration.ofSeconds(10)); // 10초에 5회
            Bandwidth limit = Bandwidth.classic(5, refill);
            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        });
    }

}
