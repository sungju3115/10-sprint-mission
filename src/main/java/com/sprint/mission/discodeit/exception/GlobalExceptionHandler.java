package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentException;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import com.sprint.mission.discodeit.exception.login.LoginException;
import com.sprint.mission.discodeit.exception.message.MessageException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusException;
import com.sprint.mission.discodeit.exception.user.UserException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 모든 커스텀 예외 통합 처리
    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException ex) {
        HttpStatus status = ex.getErrorCode().getStatus();
        // 5XX 일 경우 error로 로그 남김
        if (status.is5xxServerError()){
            log.error("[{}] {} - details: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex.getDetails());
        }else{
            log.warn("[{}] {} - details: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex.getDetails());
        }
        return ResponseEntity.status(status).body(ErrorResponse.from(ex));
    }

    // 클라이언트 요청 오류
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("[IllegalArgumentException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.from(HttpStatus.BAD_REQUEST, ex));
    }

    // 처리 불가 오류
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        log.warn("[IllegalStateException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.from(HttpStatus.CONFLICT, ex));
    }

    // Content-Type 불일치
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        log.warn("[HttpMediaTypeNotSupportedException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ErrorResponse.from(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex));
    }

    // 서버 내부 문제
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("[Exception] 예상치 못한 오류 발생 ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.from(HttpStatus.INTERNAL_SERVER_ERROR, ex));
    }
}