package com.sprint.mission.discodeit.mapper.binaryContent;

import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {
    // DTO -> Entity
    public BinaryContent toEntity(BinaryContentCreateRequest request){
        return new BinaryContent(request.fileName(), request.content(), request.contentType());
    }

    // Entity -> DTO
    public BinaryContentResponse toDTO(BinaryContent binaryContent){
        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getFileName(),
                binaryContent.getData().length,
                binaryContent.getContentType(),
                binaryContent.getData()
        );
    }
}
