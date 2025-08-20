package com.coubee.coubeebeuser.domain.dto;

import com.coubee.coubeebeuser.domain.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SiteUserSuDto {

    private Long userId;

    private String username;

    private String nickname;

    private Role role;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
