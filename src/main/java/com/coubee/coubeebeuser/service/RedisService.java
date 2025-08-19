package com.coubee.coubeebeuser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Profile("local")
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void save() {
        redisTemplate.opsForValue().set("name", "soonwook");
    }

    public Object get() {
        return redisTemplate.opsForValue().get("name");
    }
}