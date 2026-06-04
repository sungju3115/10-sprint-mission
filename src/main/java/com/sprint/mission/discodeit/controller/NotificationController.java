package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public List<NotificationDto> getNotifications(@RequestHeader("Authorization") String token){
        return null;
    }

    // 알림 확인
    @DeleteMapping("/{notificationId}")
    public void deleteNotification(@PathVariable UUID notificationId, @RequestHeader("Authorization") String token){

    }
}
