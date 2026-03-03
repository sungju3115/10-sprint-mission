package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "file", matchIfMissing = true)
public class FileMessageRepository implements MessageRepository {

    private final Path BASE_PATH;

    public FileMessageRepository(@Value("${discodeit.repository.path}") String directoryPath) {
        // 메시지 전용 폴더 경로 설정
        this.BASE_PATH = Path.of(directoryPath).resolve("message");
        init(BASE_PATH);
    }

    private void init(Path path) {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            System.out.println("Directory creation failed: " + e.getMessage());
        }
    }

    // 파일 경로를 생성하는 헬퍼 메서드
    private Path getFilePath(UUID id) {
        return BASE_PATH.resolve(id.toString() + ".ser");
    }

    // 단일 파일을 로드하는 메서드
    private Message loadOne(UUID id) {
        Path filePath = getFilePath(id);
        if (!Files.exists(filePath)) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (Message) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Optional<Message> find(UUID messageID) {
        // 맵에서 찾는 대신 해당 파일만 바로 로드
        return Optional.ofNullable(loadOne(messageID));
    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        try (var files = Files.list(BASE_PATH)) {
            files.filter(path -> path.toString().endsWith(".ser"))
                    .forEach(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            messages.add((Message) ois.readObject());
                        } catch (Exception ignored) {}
                    });
        } catch (IOException e) {
            System.out.println("FindAll failed: " + e.getMessage());
        }
        return messages;
    }

    @Override
    public void deleteMessage(UUID messageID) {
        try {
            // 맵에서 삭제하는 대신 파일 삭제
            Files.deleteIfExists(getFilePath(messageID));
        } catch (IOException e) {
            throw new RuntimeException("Delete failed: " + e.getMessage());
        }
    }

    @Override
    public Message save(Message message) {
        Path filePath = getFilePath(message.getId());
        // 맵에 넣고 통째로 저장하는 대신, 개별 파일로 저장
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(message);
            return message;
        } catch (IOException e) {
            throw new RuntimeException("Data save failed: " + e.getMessage());
        }
    }
}