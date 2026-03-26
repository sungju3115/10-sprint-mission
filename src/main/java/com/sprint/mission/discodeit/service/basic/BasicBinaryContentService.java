package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFound;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public BinaryContentDTO create(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(request.fileName(), request.contentType(), (long) request.bytes().length);
        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

        binaryContentStorage.put(savedBinaryContent.getId(), request.bytes());

        log.info("파일 업로드 성공 - fileId: {}, fileName: {}", savedBinaryContent.getId(), savedBinaryContent.getFileName());
        return binaryContentMapper.toDTO(savedBinaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentDTO find(UUID contentID) {
        log.debug("파일 조회 요청 - fileId: {}", contentID);
        BinaryContent binaryContent = binaryContentRepository.findById(contentID)
                .orElseThrow(() -> new BinaryContentNotFound(contentID));
        log.debug("파일 조회 성공 - fileId: {}", binaryContent.getId());
        return binaryContentMapper.toDTO(binaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentDTO> findAllByIdIn(List<UUID> contentIDs) {
        log.debug("파일 다건 조회 요청 - contentIDs: {}", contentIDs);

        List<BinaryContent> binaryContents = binaryContentRepository.findAllById(contentIDs);

        return binaryContents.stream()
                .map(binaryContentMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public void delete(UUID contentID) {
        log.debug("파일 삭제 요청 - contentID: {}", contentID);
        BinaryContent binaryContent = binaryContentRepository.findById(contentID)
                .orElseThrow(() -> new BinaryContentNotFound(contentID));
        binaryContentRepository.deleteById(binaryContent.getId());
    }

    @Override
    @Transactional
    public ResponseEntity<?> download(UUID binaryContentID){
        log.debug("파일 다운로드 요청 - binaryContentID: {}", binaryContentID);
        BinaryContent bt = binaryContentRepository.findById(binaryContentID)
                .orElseThrow(() -> new BinaryContentNotFound(binaryContentID));

        BinaryContentDTO dto = binaryContentMapper.toDTO(bt);
        return binaryContentStorage.download(dto);
    }
}
