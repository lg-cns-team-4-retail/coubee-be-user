package com.coubee.coubeebeuser.domain.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestUserInitializer {

    private final SiteUserRepository siteUserRepository;
    private final PasswordEncoder passwordEncoder;

//    @PostConstruct
//    @Transactional
//    public void init() {
//        log.info("asa");
//        siteUserRepository.findAll().forEach(siteUser -> {
//            siteUser.setPassword(passwordEncoder.encode(siteUser.getPassword()));
//        });
//    }

//    @Transactional
//    @org.springframework.context.event.EventListener(
//            org.springframework.boot.context.event.ApplicationReadyEvent.class
//    )
//    public void onReady() {
//        log.info("TestUserInitializer start (ApplicationReadyEvent)");
//        siteUserRepository.findAll().forEach(u -> {
//            String pw = u.getPassword();
//            // 이미 BCrypt면 스킵 (중복 인코딩 방지)
//            if (pw != null && !pw.startsWith("$2a$") && !pw.startsWith("$2b$")) {
//                u.setPassword(passwordEncoder.encode(pw));
//            }
//        });
//        log.info("TestUserInitializer done");
//    }
}