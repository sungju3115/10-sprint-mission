package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentException;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import com.sprint.mission.discodeit.exception.login.LoginException;
import com.sprint.mission.discodeit.exception.message.MessageException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusException;
import com.sprint.mission.discodeit.exception.user.UserException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // User 엔티티 예외
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException ex) {
        return ResponseEntity.status(ex.getErrorCode().getStatus())
                .body(ErrorResponse.from(ex));
    }

    // Channel 엔티티 예외
    @ExceptionHandler(ChannelException.class)
    public ResponseEntity<ErrorResponse> handleChannelException(ChannelException ex) {
        return ResponseEntity.status(ex.getErrorCode().getStatus())
                .body(ErrorResponse.from(ex));
    }

    // Message 엔티티 예외
    @ExceptionHandler(MessageException.class)
    public ResponseEntity<ErrorResponse> handleMessageException(MessageException ex) {
        return ResponseEntity.status(ex.getErrorCode().getStatus())
                .body(ErrorResponse.from(ex));
    }

    // Login 예외
    @ExceptionHandler(LoginException.class)
    public ResponseEntity<ErrorResponse> handleLoginException(LoginException ex){
        return ResponseEntity.status(ex.getErrorCode().getStatus())
                .body(ErrorResponse.from(ex));
    }

    // BinaryContent 예외
    @ExceptionHandler(BinaryContentException.class)
    public ResponseEntity<ErrorResponse> handleBinaryContentException(BinaryContentException ex){
        return ResponseEntity.status(ex.getErrorCode().getStatus())
                .body(ErrorResponse.from(ex));
    }

    // UserStatus 예외
    @ExceptionHandler(UserStatusException.class)
    public ResponseEntity<ErrorResponse> handleUserStatusException(UserStatusException ex){
        return ResponseEntity.status(ex.getErrorCode().getStatus())
                .body(ErrorResponse.from(ex));
    }

    // ReadStatus 예외
    @ExceptionHandler(ReadStatusException.class)
    public ResponseEntity<ErrorResponse> handleReadStatusException(ReadStatusException ex){
        return ResponseEntity.status(ex.getErrorCode().getStatus())
                .body(ErrorResponse.from(ex));
    }

    // 클라이언트 요청 오류
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.from(HttpStatus.BAD_REQUEST, ex));
    }

    // 처리 불가 오류
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.from(HttpStatus.CONFLICT, ex));
    }

    // Content-Type 불일치
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ErrorResponse.from(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex));
    }

    // 서버 내부 문제
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.from(HttpStatus.INTERNAL_SERVER_ERROR, ex));
    }
}