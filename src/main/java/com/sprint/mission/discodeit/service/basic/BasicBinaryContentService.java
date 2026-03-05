package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.binaryContent.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(request.fileName(), request.content(), request.contentType());
        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);
        return new BinaryContentResponse(
                savedBinaryContent.getId(),
                savedBinaryContent.getCreatedAt(),
                savedBinaryContent.getFileName(),
                savedBinaryContent.getData().length,
                savedBinaryContent.getContentType(),
                savedBinaryContent.getData());
    }

    @Override
    public BinaryContentResponse find(UUID contentID) {
        BinaryContent binaryContent = binaryContentRepository.find(contentID)
                .orElseThrow(() -> new IllegalArgumentException("BinaryContent not found: " + contentID));
        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getFileName(),
                binaryContent.getData().length,
                binaryContent.getContentType(),
                binaryContent.getData()
        );
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> contentIDs) {
        if (contentIDs.isEmpty()) {
            return null;
        }

        List<BinaryContent> contents = new ArrayList<>();

        for (UUID contentID : contentIDs) {
            BinaryContent content = binaryContentRepository.find(contentID)
                    .orElseThrow(() -> new IllegalArgumentException("BinaryContent not found: " + contentID));
            contents.add(content);
        }

        return contents.stream()
                .map(ct -> new BinaryContentResponse(
                        ct.getId(),
                        ct.getCreatedAt(),
                        ct.getFileName(),
                        ct.getData().length,
                        ct.getContentType(),
                        ct.getData()
                ))
                .toList();
    }

    @Override
    public void delete(UUID contentID) {
        BinaryContent binaryContent = binaryContentRepository.find(contentID)
                .orElseThrow(() -> new IllegalArgumentException("BinaryContent not found: " + contentID));
        binaryContentRepository.delete(binaryContent.getId());
    }

    @Override
    Resource download(UUID binaryContentID){
        BinaryContent bt = binaryContentRepository.find(binaryContentID)
                .orElseThrow(() -> new IllegalArgumentException("BinaryContent not found: " + binaryContentID));
        BinaryContentResponse dto = binaryContentMapper.toDTO(bt);
        return binaryContentRepository.download(dto);
    }
}
