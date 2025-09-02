package com.coubee.coubeebeuser.service;

import com.coubee.coubeebeuser.domain.CoubeeUser;
import com.coubee.coubeebeuser.domain.CoubeeUserInfo;
import com.coubee.coubeebeuser.domain.dto.SiteUserInfoDto;
import com.coubee.coubeebeuser.domain.repository.CoubeeUserInfoRepository;
import com.coubee.coubeebeuser.domain.repository.SiteUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BackendUserService {

    private final SiteUserRepository siteUserRepository;
    private final CoubeeUserInfoRepository coubeeUserInfoRepository;

    public SiteUserInfoDto getSiteUserInfo(Long userId){
        Optional<CoubeeUser> user = siteUserRepository.findById(userId);
        CoubeeUserInfo byUserId = coubeeUserInfoRepository.findByUserId(userId).orElse(null);
        return user.map(coubeeUser -> SiteUserInfoDto.fromEntity(coubeeUser, byUserId)).orElse(null);
    }
}
