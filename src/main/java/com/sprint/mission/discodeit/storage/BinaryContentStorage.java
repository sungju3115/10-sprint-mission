package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentDTO;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.UUID;


public interface BinaryContentStorage {
    UUID put(UUID contentID, byte[] bytes);
    InputStream get(UUID contentID);
    ResponseEntity<?> download(BinaryContentDTO content);
}
