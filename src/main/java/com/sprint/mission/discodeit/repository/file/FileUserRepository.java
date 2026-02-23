package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "file", matchIfMissing = true)
public class FileUserRepository implements UserRepository {

    private final Path BASE_PATH;

    public FileUserRepository(@Value("${discodeit.repository.path}") String directoryPath) {
        // user 전용 폴더 경로 설정
        this.BASE_PATH = Path.of(directoryPath).resolve("user");
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
    private User loadOne(UUID id) {
        Path filePath = getFilePath(id);
        if (!Files.exists(filePath)) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (User) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Optional<User> find(UUID userID) {
        // 매번 리스트 로드 없이 특정 유저 파일만 확인
        return Optional.ofNullable(loadOne(userID));
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (var files = Files.list(BASE_PATH)) {
            files.filter(path -> path.toString().endsWith(".ser"))
                    .forEach(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            users.add((User) ois.readObject());
                        } catch (Exception ignored) {}
                    });
        } catch (IOException e) {
            System.out.println("FindAll failed: " + e.getMessage());
        }
        return users;
    }

    @Override
    public void deleteUser(User user) {
        try {
            // 유저 객체에서 ID를 추출하여 파일 삭제
            Files.deleteIfExists(getFilePath(user.getId()));
        } catch (IOException e) {
            throw new RuntimeException("Delete failed: " + e.getMessage());
        }
    }

    @Override
    public User save(User user) {
        Path filePath = getFilePath(user.getId());
        // 리스트를 돌면서 덮어씌울 필요 없이 바로 파일 저장 (UUID가 같으면 덮어쓰기됨)
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(user);
            return user;
        } catch (IOException e) {
            throw new RuntimeException("Data save failed: " + e.getMessage());
        }
    }
}