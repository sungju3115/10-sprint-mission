package com.sprint.mission.discodeit.mapper.binaryContent;

import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {
    // DTO -> Entity
    public BinaryContent toEntity(BinaryContentCreateRequest request){
        return new BinaryContent(request.fileName(), request.contentType(), request.bytes());
    }

    // Entity -> DTO
    public BinaryContentResponse toDTO(BinaryContent binaryContent){
        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getFileName(),
                binaryContent.getBytes().length,
                binaryContent.getContentType(),
                binaryContent.getBytes()
        );
    }
}
