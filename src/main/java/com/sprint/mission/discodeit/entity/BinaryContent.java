package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델
 * 수정 불가능한 도메인 모델
 * */
@Entity
@Table(name = "binary_contents")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class BinaryContent extends BaseUpdatableEntity {
    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String contentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BinaryContentStatus status;

    public BinaryContent(String fileName, String contentType, long size) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.status = BinaryContentStatus.PROCESSING;
    }
}
