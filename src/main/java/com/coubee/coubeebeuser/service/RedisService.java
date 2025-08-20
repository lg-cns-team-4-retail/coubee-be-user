package com.coubee.coubeebeuser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void save() {
        redisTemplate.opsForValue().set("name", "soonwook");
    }

    public Object get() {
        return redisTemplate.opsForValue().get("name");
    }
}