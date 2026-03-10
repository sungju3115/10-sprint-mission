package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.page.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.function.Function;

@Component
public class PageResponseMapper {
    // Slice로부터 DTO 생성 (전체 개수 모름 -> null)
    public <T> PageResponse<T> fromSlice(Slice<T> slice, Object nextCursor) {
        return new PageResponse<>(
                slice.getContent(),
                nextCursor,
                slice.getSize(),
                slice.hasNext(),
                null
        );
    }

    // Page로부터 DTO 생성 (전체 개수 포함)
    public <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                null,
                page.getSize(),
                page.hasNext(),
                page.getTotalElements()
        );
    }
}
