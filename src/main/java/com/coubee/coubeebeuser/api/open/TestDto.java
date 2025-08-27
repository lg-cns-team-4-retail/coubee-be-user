package com.coubee.coubeebeuser.api.open;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class TestDto {
    private String eventId;
    private String notificationType; // PAYED,CANCELLED_USER,CANCELLED_ADMIN,PREPARING(주문수락),PREPARED(준비완료),RECEIVED(수령완료)
    private String orderId;
    private Long userId;
    private String title;
    private String message;
    private LocalDateTime timestamp;


    @Builder
    public TestDto(String eventId, String notificationType, String orderId, Long userId, String title, String message) {
        this.eventId = eventId;
        this.notificationType = notificationType;
        this.orderId = orderId;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
