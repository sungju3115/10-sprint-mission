package com.sprint.mission.discodeit.mapper.binaryContent;

import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {
    BinaryContent toEntity(BinaryContentCreateRequest request);

    @Mapping(target = "size", expression = "java(binaryContent.getBytes().length)")
    BinaryContentDTO toDTO(BinaryContent binaryContent);
}
