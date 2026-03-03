package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
public class FileUserStatusRepository implements UserStatusRepository {

    private final Path BASE_PATH;

    public FileUserStatusRepository(@Value("${discodeit.repository.path}") String directoryPath) {
        // userStatus 전용 폴더 경로 설정
        this.BASE_PATH = Path.of(directoryPath).resolve("userStatus");
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

    // 단일 파일을 로드하는 내부 메서드
    private UserStatus loadOne(UUID id) {
        Path filePath = getFilePath(id);
        if (!Files.exists(filePath)) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (UserStatus) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Optional<UserStatus> find(UUID userStatusID) {
        return Optional.ofNullable(loadOne(userStatusID));
    }

    @Override
    public Optional<UserStatus> findByUserID(UUID userID) {
        // 모든 파일을 순회하며 userID가 일치하는 첫 번째 상태를 반환
        try (var files = Files.list(BASE_PATH)) {
            return files.filter(path -> path.toString().endsWith(".ser"))
                    .map(this::loadByPath)
                    .filter(status -> status != null && status.getUserID().equals(userID))
                    .findFirst();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<UserStatus> findAll() {
        List<UserStatus> list = new ArrayList<>();
        try (var files = Files.list(BASE_PATH)) {
            files.filter(path -> path.toString().endsWith(".ser"))
                    .forEach(path -> {
                        UserStatus status = loadByPath(path);
                        if (status != null) list.add(status);
                    });
        } catch (IOException e) {
            System.out.println("FindAll failed: " + e.getMessage());
        }
        return list;
    }

    @Override
    public void deleteUserStatus(UUID userStatusID) {
        try {
            Files.deleteIfExists(getFilePath(userStatusID));
        } catch (IOException e) {
            throw new RuntimeException("Delete failed: " + e.getMessage());
        }
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        Path filePath = getFilePath(userStatus.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(userStatus);
            return userStatus;
        } catch (IOException e) {
            throw new RuntimeException("Data save failed: " + e.getMessage());
        }
    }

    // 파일 경로를 통해 객체를 로드하는 헬퍼 메서드
    private UserStatus loadByPath(Path path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (UserStatus) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}
