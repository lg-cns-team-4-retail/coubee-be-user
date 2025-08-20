package com.coubee.coubeebeuser.api.open;

import com.coubee.coubeebeuser.common.dto.ApiResponseDto;
import com.coubee.coubeebeuser.domain.CoubeeUser;
import com.coubee.coubeebeuser.domain.dto.SiteUserSuDto;
import com.coubee.coubeebeuser.service.SiteUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/user/su", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserSuController {

    private final SiteUserService siteUserService;

    @GetMapping("/list")
    public ApiResponseDto<List<SiteUserSuDto>> getAllSiteUserList(@RequestParam(required = false) String username, @RequestParam(required = false) String role){
        log.info("test");
        return ApiResponseDto.readOk(siteUserService.getAllSiteUserList(username.trim(),role));
    }
    @PostMapping("/pwd")
    public ApiResponseDto<String> passwordReset(@RequestParam Long userId){
        siteUserService.passwordReset(userId);
        return ApiResponseDto.defaultOk();
    }
}
