package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

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
public class FileChannelRepository implements ChannelRepository {

    private final Path BASE_PATH;

    public FileChannelRepository(@Value("${discodeit.repository.path}") String directoryPath) {
        // 채널 전용 폴더 경로 설정
        this.BASE_PATH = Path.of(directoryPath).resolve("channel");
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

    // 단일 파일을 로드하는 메서드 (기존 loadData 대체)
    private Channel loadOne(UUID id) {
        Path filePath = getFilePath(id);
        if (!Files.exists(filePath)) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (Channel) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    // 단일 파일을 저장하는 메서드 (기존 saveData 대체)
    private void saveOne(Channel channel) {
        Path filePath = getFilePath(channel.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(channel);
        } catch (Exception e) {
            throw new RuntimeException("Data save failed: " + e.getMessage());
        }
    }

    @Override
    public Channel find(UUID channelID) {
        Channel channel = loadOne(channelID);
        if (channel == null) {
            throw new IllegalArgumentException("Channel not found: " + channelID);
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> channels = new ArrayList<>();
        try (var files = Files.list(BASE_PATH)) {
            files.filter(path -> path.toString().endsWith(".ser"))
                    .forEach(path -> {
                        // 파일 이름에서 UUID 추출 (옵션) 혹은 그냥 로드
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            channels.add((Channel) ois.readObject());
                        } catch (Exception ignored) {}
                    });
        } catch (Exception e) {
            System.out.println("FindAll failed: " + e.getMessage());
        }
        return channels;
    }

    @Override
    public void deleteChannel(UUID channelID) {
        try {
            Files.deleteIfExists(getFilePath(channelID));
        } catch (Exception e) {
            throw new RuntimeException("Delete failed: " + e.getMessage());
        }
    }

    @Override
    public Channel save(Channel channel) {
        saveOne(channel);
        return channel;
    }
}
