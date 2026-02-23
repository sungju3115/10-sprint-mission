package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "file", matchIfMissing = true)
public class FileReadStatusRepository implements ReadStatusRepository {

    private final Path BASE_PATH;

    public FileReadStatusRepository(@Value("${discodeit.repository.path}") String directoryPath) {
        this.BASE_PATH = Path.of(directoryPath).resolve("readStatus");
        init(BASE_PATH);
    }

    private void init(Path path) {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (Exception e) {
            System.out.println("Directory creation failed: " + e.getMessage());
        }
    }

    private Path getFilePath(UUID id) {
        return BASE_PATH.resolve(id.toString() + ".ser");
    }

    private ReadStatus loadOne(UUID id) {
        Path filePath = getFilePath(id);
        if (!Files.exists(filePath)) return null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (ReadStatus) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Optional<ReadStatus> find(UUID readStatusID) {
        return Optional.ofNullable(loadOne(readStatusID));
    }

    @Override
    public List<ReadStatus> findByUserID(UUID userID) {
        // 모든 파일을 읽어서 userID가 일치하는 것만 필터링
        return findAll().stream()
                .filter(readStatus -> readStatus.getUserID().equals(userID))
                .toList();
    }

    @Override
    public List<ReadStatus> findAll() {
        List<ReadStatus> list = new ArrayList<>();
        try (var files = Files.list(BASE_PATH)) {
            files.filter(path -> path.toString().endsWith(".ser"))
                    .forEach(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            list.add((ReadStatus) ois.readObject());
                        } catch (Exception ignored) {}
                    });
        } catch (Exception e) {
            System.out.println("FindAll failed: " + e.getMessage());
        }
        return list;
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path filePath = getFilePath(readStatus.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(readStatus);
            return readStatus;
        } catch (Exception e) {
            throw new RuntimeException("Data save failed: " + e.getMessage());
        }
    }

    @Override
    public void delete(UUID readStatusID) {
        try {
            Files.deleteIfExists(getFilePath(readStatusID));
        } catch (Exception e) {
            throw new RuntimeException("Delete failed: " + e.getMessage());
        }
    }

    @Override
    public void deleteByChannelID(UUID channelID) {
        // 전체를 돌면서 channelID가 같은 파일을 삭제
        try (var files = Files.list(BASE_PATH)) {
            files.filter(path -> path.toString().endsWith(".ser"))
                    .forEach(path -> {
                        ReadStatus rs = loadByPath(path);
                        if (rs != null && rs.getChannelID().equals(channelID)) {
                            try { Files.delete(path); } catch (IOException ignored) {}
                        }
                    });
        } catch (Exception e) {
            System.out.println("DeleteByChannelID failed: " + e.getMessage());
        }
    }

    @Override
    public void deleteByChannelIDAndUserID(UUID channelID, UUID userID) {
        try (var files = Files.list(BASE_PATH)) {
            files.filter(path -> path.toString().endsWith(".ser"))
                    .forEach(path -> {
                        ReadStatus rs = loadByPath(path);
                        if (rs != null && rs.getChannelID().equals(channelID) && rs.getUserID().equals(userID)) {
                            try { Files.delete(path); } catch (IOException ignored) {}
                        }
                    });
        } catch (Exception e) {
            System.out.println("DeleteByChannelIDAndUserID failed: " + e.getMessage());
        }
    }

    // findAll이나 조건부 삭제를 위한 내부 헬퍼 메서드
    private ReadStatus loadByPath(Path path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (ReadStatus) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}