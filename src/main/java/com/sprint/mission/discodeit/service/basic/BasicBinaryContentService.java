package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.binaryContent.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    @Transactional
    public BinaryContentDTO create(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(request.fileName(), request.contentType(), request.size());
        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);
        return binaryContentMapper.toDTO(savedBinaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentDTO find(UUID contentID) {
        BinaryContent binaryContent = binaryContentRepository.findById(contentID)
                .orElseThrow(() -> new IllegalArgumentException("BinaryContent not found: " + contentID));
        return binaryContentMapper.toDTO(binaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentDTO> findAllByIdIn(List<UUID> contentIDs) {
        if (contentIDs.isEmpty()) {
            return new ArrayList<>();
        }

        List<BinaryContent> binaryContents = binaryContentRepository.findAllById(contentIDs);

        return binaryContents.stream()
                .map(binaryContentMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public void delete(UUID contentID) {
        BinaryContent binaryContent = binaryContentRepository.findById(contentID)
                .orElseThrow(() -> new IllegalArgumentException("BinaryContent not found: " + contentID));
        binaryContentRepository.deleteById(binaryContent.getId());
    }

    @Override
    @Transactional
    public ResponseEntity<?> download(UUID binaryContentID){
        BinaryContent bt = binaryContentRepository.findById(binaryContentID)
                .orElseThrow(() -> new IllegalArgumentException("BinaryContent not found: " + binaryContentID));

        BinaryContentDTO dto = binaryContentMapper.toDTO(bt);
        return binaryContentStorage.download(dto);
    }
}
