package com.coubee.coubeebeuser.service;

import com.coubee.coubeebeuser.common.exception.BadParameter;
import com.coubee.coubeebeuser.common.exception.NotFound;
import com.coubee.coubeebeuser.common.type.ActionAndId;
import com.coubee.coubeebeuser.domain.CoubeeUser;
import com.coubee.coubeebeuser.domain.CoubeeUserInfo;
import com.coubee.coubeebeuser.domain.NotificationToken;
import com.coubee.coubeebeuser.domain.Role;
import com.coubee.coubeebeuser.domain.dto.*;
import com.coubee.coubeebeuser.domain.event.SiteUserInfoEvent;
import com.coubee.coubeebeuser.domain.mapper.UserMapper;
import com.coubee.coubeebeuser.domain.repository.CoubeeUserInfoRepository;
import com.coubee.coubeebeuser.domain.repository.SiteUserRepository;
import com.coubee.coubeebeuser.event.producer.KafkaMessageProducer;
import com.coubee.coubeebeuser.secret.jwt.TokenGenerator;
import com.coubee.coubeebeuser.secret.jwt.dto.TokenDto;
import com.coubee.coubeebeuser.util.FileUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SiteUserService {
    private final SiteUserRepository siteUserRepository;
    private final TokenGenerator tokenGenerator;
    private final KafkaMessageProducer kafkaMessageProducer;
    private final CoubeeUserInfoRepository coubeeUserInfoRepository;
    private final FileUploader fileUploader;
    private static final String PREFIX = "notificationToken:";
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ActionAndId registerUserAndNotify(SiteUserRegisterDto registerDto) {
        CoubeeUser coubeeUser = registerDto.toEntity();
        Optional<CoubeeUser> existedUsername = siteUserRepository.findByUsername(coubeeUser.getUsername());
        Optional<CoubeeUser> existedNickName = siteUserRepository.findByNickname(coubeeUser.getNickname());
        if(existedUsername.isPresent()){
            throw new BadParameter("사용하고 있는 아이디입니다");
        }
        if(existedNickName.isPresent()){
            throw new BadParameter("사용하고 있는 닉네임입니다");
        }
        coubeeUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        siteUserRepository.save(coubeeUser);
        SiteUserInfoEvent event = SiteUserInfoEvent.fromEntity("Create", coubeeUser);
        kafkaMessageProducer.send(SiteUserInfoEvent.Topic, event);
        return ActionAndId.of("Create", coubeeUser.getUserId());
    }

    @Transactional
    public ActionAndId updateEmailAndNotify() {
        return ActionAndId.of("Update", 0L);
    }
//    @Transactional(readOnly = true)
//    public TokenDto.AccessRefreshToken login(SiteUserLoginDto loginDto) {
//        CoubeeUser user = siteUserRepository.findByUsername(loginDto.getUsername())
//                .orElseThrow(() -> new NotFound("아이디 또는 비밀번호를 확인하세요."));
//        if (!SecureHashUtils.matches(loginDto.getPassword(), user.getPassword())) {
//            throw new BadParameter("아이디 또는 비밀번호를 확인하세요.");
//        }
//        return tokenGenerator.generateAccessRefreshToken(user, "WEB");
//    }

    @Transactional(readOnly = true)
    public SiteUserLoginResponseDto login(SiteUserLoginDto loginDto) {
        CoubeeUser user = siteUserRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new BadParameter("아이디 또는 비밀번호를 확인하세요."));
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BadParameter("아이디 또는 비밀번호를 확인하세요.");
        }
        CoubeeUserInfo coubeeUserInfo = coubeeUserInfoRepository.findByUserId(user.getUserId()).orElse(null);

        SiteUserLoginResponseDto loginResponseDto = new SiteUserLoginResponseDto();

        loginResponseDto.setAccessRefreshToken(tokenGenerator.generateAccessRefreshToken(user, "WEB"));
        loginResponseDto.setUserInfo(SiteUserInfoDto.fromEntity(user,coubeeUserInfo));

        return loginResponseDto;
    }

    @Transactional(readOnly = true)
    public TokenDto.AccessToken refresh(SiteUserRefreshDto refreshDto) {
        String username = tokenGenerator.validateJwtToken(refreshDto.getToken());
        if (username == null) {
            throw new BadParameter("토큰이 유효하지 않습니다.");
        }
        CoubeeUser user = siteUserRepository.findByUsername(username)
                .orElseThrow(() -> new NotFound("사용자를 찾을 수 없습니다."));
        return tokenGenerator.generateAccessToken(user, "WEB");
    }
    @Transactional
    public SiteUserInfoDto registerUserInfo(Long userId,SiteUserInfoRegisterDto dto){
        CoubeeUser targetUser = siteUserRepository.findById(userId).orElseThrow(()->
                new NotFound("사용자를 찾을 수 없습니다."));
        CoubeeUserInfo newInfo = CoubeeUserInfo.builder()
                .userId(userId)
                .name(dto.getName())
                .email(dto.getEmail())
                .age(dto.getAge())
                .gender(dto.getGender())
                .phoneNum(dto.getPhoneNum())
                .profileImageUrl(dto.getProfileImageUrl())
                .build();
        CoubeeUserInfo saved = coubeeUserInfoRepository.save(newInfo);

        return SiteUserInfoDto.fromEntity(targetUser,saved);
    }

    @Transactional(readOnly = true)
    public SiteUserInfoDto userInfo(String username) {
        CoubeeUser user = siteUserRepository.findByUsername(username)
                .orElseThrow(() -> new NotFound("사용자를 찾을 수 없습니다."));
        CoubeeUserInfo coubeeUserInfo = coubeeUserInfoRepository.findByUserId(user.getUserId()).orElse(null);
        return SiteUserInfoDto.fromEntity(user,coubeeUserInfo);
    }

    public String uploadProfile(MultipartFile file) {
        String profileImageUrl = fileUploader.upload(file,"user/profile");
        return profileImageUrl;
    }

    public void saveNotificationToken(String username, NotificationTokenDto dto) {
        if (redisTemplate == null) {
            log.warn("RedisTemplate is not available in this profile.");
            return;
        }

        CoubeeUser user = siteUserRepository.findByUsername(username)
                .orElseThrow(() -> new NotFound("유저 없음"));

        String tokenKey = PREFIX + dto.getNotificationToken();
        String indexKey = "userTokenIndex:" + user.getUserId();

        NotificationToken tokenObj = NotificationToken.builder()
                .userId(user.getUserId())
                .token(dto.getNotificationToken())
                .build();

        // 1) 토큰 기준 저장
        redisTemplate.opsForValue().set(tokenKey, tokenObj);

        // 2) userId → 토큰 인덱스 저장
        redisTemplate.opsForSet().add(indexKey, dto.getNotificationToken());
    }
    public void deleteNotificationToken(String token) {
        if (redisTemplate == null) {
            log.warn("RedisTemplate is not available in this profile.");
            return;
        }

        String tokenKey = PREFIX + token;
        NotificationToken tokenObj = (NotificationToken) redisTemplate.opsForValue().get(tokenKey);

        if (tokenObj != null) {
            String indexKey = "userTokenIndex:" + tokenObj.getUserId();
            redisTemplate.opsForSet().remove(indexKey, token);
            redisTemplate.delete(tokenKey);
        }
    }

    public List<String> getNotificationTokenListByUserId(Long userId) {
        if (redisTemplate == null) {
            log.warn("RedisTemplate is not available in this profile.");
            return List.of();
        }
        String indexKey = "userTokenIndex:" + userId;
        Set<Object> tokens = redisTemplate.opsForSet().members(indexKey);
        if (tokens == null) return List.of();

        return tokens.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .toList();
    }

    /// 관리자 기능
    public List<SiteUserSuDto> getAllSiteUserList(String username,String role) {
        if(role==null||role.isBlank()){
            return siteUserRepository.findAllByUsernameContainingIgnoreCaseOrderByUserIdAsc(username).stream().map(UserMapper::fromEntity).toList();
        }else{
            return switch (role) {
                case "admin" ->
                        siteUserRepository.findAllByUsernameContainingIgnoreCaseAndRoleOrderByUserIdAsc(username, Role.ROLE_ADMIN).stream().map(UserMapper::fromEntity).toList();
                case "user" ->
                        siteUserRepository.findAllByUsernameContainingIgnoreCaseAndRoleOrderByUserIdAsc(username, Role.ROLE_USER).stream().map(UserMapper::fromEntity).toList();
                default -> siteUserRepository.findAllByUsernameContainingIgnoreCaseOrderByUserIdAsc(username).stream().map(UserMapper::fromEntity).toList();
            };
        }
    }
    @Transactional
    public void passwordReset(Long userId){
        siteUserRepository.findById(userId).ifPresent(siteUser -> {siteUser.setPassword(passwordEncoder.encode("1234"));});
    }
}
