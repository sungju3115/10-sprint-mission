package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.binaryContent.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.JPABinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {
    private final JPABinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(request.fileName(), request.contentType(), request.bytes());
        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);
        return binaryContentMapper.toDTO(savedBinaryContent);
    }

    @Override
    public BinaryContentResponse find(UUID contentID) {
        BinaryContent binaryContent = binaryContentRepository.findById(contentID)
                .orElseThrow(() -> new IllegalArgumentException("BinaryContent not found: " + contentID));
        return binaryContentMapper.toDTO(binaryContent);
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> contentIDs) {
        if (contentIDs.isEmpty()) {
            return null;
        }

        List<BinaryContent> binaryContents = binaryContentRepository.findAllById(contentIDs);

        return binaryContents.stream()
                .map(binaryContentMapper::toDTO)
                .toList();
    }

    @Override
    public void delete(UUID contentID) {
        BinaryContent binaryContent = binaryContentRepository.findById(contentID)
                .orElseThrow(() -> new IllegalArgumentException("BinaryContent not found: " + contentID));
        binaryContentRepository.deleteById(binaryContent.getId());
    }

    @Override
    Resource download(UUID binaryContentID){
        BinaryContent bt = binaryContentRepository.findById(binaryContentID)
                .orElseThrow(() -> new IllegalArgumentException("BinaryContent not found: " + binaryContentID));
        BinaryContentResponse dto = binaryContentMapper.toDTO(bt);
        return binaryContentRepository.download(dto);
    }
}
