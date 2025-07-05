package com.trippia.travel.config;

import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimiterConfig {

    @Bean
    public Map<String, Bucket> userRateLimitCache() {
        return new ConcurrentHashMap<>();
    }

}
