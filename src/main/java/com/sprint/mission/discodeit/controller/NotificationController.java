package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    // 알림 조회
    @GetMapping
    public List<NotificationDto> getNotifications(@AuthenticationPrincipal DiscodeitUserDetails userDetails){
        UUID receiverId = userDetails.getUserDTO().id();
        return notificationService.findAll(receiverId);
    }

    // 알림 확인
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID notificationId,
                                                   @AuthenticationPrincipal DiscodeitUserDetails userDetails){
        UUID receiverId = userDetails.getUserDTO().id();
        notificationService.delete(notificationId, receiverId);
        return ResponseEntity.noContent().build();
    }
}
