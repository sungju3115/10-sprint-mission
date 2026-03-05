package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/*
* 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델
* 수정 불가능한 도메인 모델
* */
@Entity
@Table(name = "binary_contents")
@RequiredArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class BinaryContent extends BaseEntity {
    @Column(nullable = false)
    private String fileName;
    @Column(nullable = false)
    private Long size;
    @Column(nullable = false)
    private String contentType;
    @Column(nullable = false, columnDefinition = "BYTEA")
    private byte[] bytes;

    public BinaryContent(String fileName, Long size, String contentType, byte[] bytes) {
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.bytes = bytes;
    }
}
