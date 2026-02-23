package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file", matchIfMissing = true)
public class FileBinaryContentRepository implements BinaryContentRepository {

    private final Path BASE_PATH;

    public FileBinaryContentRepository(@Value("${discodeit.repository.path}") String directoryPath) {
        // binaryContent 전용 폴더 경로 설정
        this.BASE_PATH = Path.of(directoryPath).resolve("binaryContent");
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

    // 파일 경로를 생성하는 헬퍼 메서드
    private Path getFilePath(UUID id) {
        return BASE_PATH.resolve(id.toString() + ".ser");
    }

    // 단일 파일을 로드하는 메서드
    private BinaryContent loadOne(UUID id) {
        Path filePath = getFilePath(id);
        if (!Files.exists(filePath)) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (BinaryContent) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Optional<BinaryContent> find(UUID contentID) {
        // 매번 전체 데이터를 로드할 필요 없이 해당 파일만 확인
        return Optional.ofNullable(loadOne(contentID));
    }

    @Override
    public List<BinaryContent> findAll() {
        List<BinaryContent> contents = new ArrayList<>();
        try (var files = Files.list(BASE_PATH)) {
            files.filter(path -> path.toString().endsWith(".ser"))
                    .forEach(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            contents.add((BinaryContent) ois.readObject());
                        } catch (Exception ignored) {}
                    });
        } catch (Exception e) {
            System.out.println("FindAll failed: " + e.getMessage());
        }
        return contents;
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        Path filePath = getFilePath(binaryContent.getId());
        // 리스트 순회 없이 바로 파일 쓰기 (덮어쓰기 포함)
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(binaryContent);
            return binaryContent;
        } catch (Exception e) {
            throw new RuntimeException("Data save failed: " + e.getMessage());
        }
    }

    @Override
    public void delete(UUID contentID) {
        try {
            Files.deleteIfExists(getFilePath(contentID));
        } catch (Exception e) {
            throw new RuntimeException("Delete failed: " + e.getMessage());
        }
    }
}