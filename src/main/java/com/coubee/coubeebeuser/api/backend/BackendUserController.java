package com.coubee.coubeebeuser.api.backend;

import com.coubee.coubeebeuser.common.dto.ApiResponseDto;
import com.coubee.coubeebeuser.domain.dto.SiteUserInfoDto;
import com.coubee.coubeebeuser.service.BackendUserService;
import com.coubee.coubeebeuser.service.SiteUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/backend/user", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BackendUserController {
    private final SiteUserService siteUserService;
    private final BackendUserService backendUserService;

    @GetMapping(value = "/user/{userId}")
    public ApiResponseDto<SiteUserInfoDto> userInfo(@PathVariable String userId) {
        SiteUserInfoDto userInfoDto = siteUserService.userInfo(userId);
        return ApiResponseDto.createOk(userInfoDto);
    }

    @GetMapping(value = "/info/{userId}")
    public ApiResponseDto<SiteUserInfoDto> userInfo(@PathVariable Long userId) {
        SiteUserInfoDto userInfoDto = backendUserService.getSiteUserInfo(userId);
        return ApiResponseDto.createOk(userInfoDto);
    }
}
