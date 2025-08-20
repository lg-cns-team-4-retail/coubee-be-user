package com.coubee.coubeebeuser.domain.mapper;

import com.coubee.coubeebeuser.domain.CoubeeUser;
import com.coubee.coubeebeuser.domain.dto.SiteUserSuDto;

public class UserMapper {

    public static SiteUserSuDto fromEntity(CoubeeUser user){
        SiteUserSuDto dto = new SiteUserSuDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setRole(user.getRole());
        return dto;
    }
}
