package com.coubee.coubeebeuser.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NotificationToken implements Serializable {
    private Long userId;
    private String token;

    @Builder
    public NotificationToken(Long userId, String token) {
        this.userId = userId;
        this.token = token;
    }
}