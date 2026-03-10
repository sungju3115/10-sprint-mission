package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPrivate;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPublic;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.response.ChannelDTO;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Transactional
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {
    // 필드
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository ReadStatusRepository;
    private final ChannelMapper channelMapper;

    // public Channel 생성
    @Transactional
    @Override
    public ChannelDTO createPublic(ChannelCreateRequestPublic request) {
        // 같은 이름 존재 check
        channelRepository.findAll().stream()
                .filter(ch -> "PUBLIC".equals(ch.getType()))
                .filter(ch -> ch.getName().equals(request.name()))
                .findFirst()
                .ifPresent(ch -> {
                    throw new IllegalArgumentException("Already Present name");
                });

        Channel channel = channelMapper.toEntity(request);
        // [저장]
        Channel savedChannel = channelRepository.save(channel);
        // 초기 channel 생성 시 빈 리스트, null 반환해주는 게 맞을려나
        return channelMapper.toDTO(savedChannel, new ArrayList<>(), null);
    }

    // private Channel 생성 : 이름, description 생략 채널 참여 유저 정보 생성 + 유저 별 readStatus 정보
    @Transactional
    @Override
    public ChannelDTO createPrivate(ChannelCreateRequestPrivate request) {
        // channel 생성
        Channel channel = channelMapper.toEntity(request);

        // private channel의 userList
        List<UUID> users = request.participantIds();

        // channel 영속화 -> readStatus 영속화
        Channel savedChannel = channelRepository.save(channel);

        // ReadStatus 생성 -> 저장 , ReadStatus = User의 Channel 목록
        for(UUID userId : users) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
            ReadStatus status = new ReadStatus(user, channel);
            ReadStatusRepository.save(status);
        }

        // 초기 생성 시에는 lastMessageAt은 null ??
        return channelMapper.toDTO(savedChannel, users, null);
    }

    @Transactional(readOnly = true)
    @Override
    public ChannelDTO find(UUID id) {
        // channel 조회
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + id));

        // 최근 메시지의 시간 -> channel에서 메시지 생성 안되어 있을 수도 있지 않나?
        Instant lastCreatedAt = messageRepository.findAllByChannel_Id(id).stream()
                .map(BaseEntity::getCreatedAt)
                .findFirst()
                .orElse(null);

        List<UUID> userIDs = new ArrayList<>();
        // private일 경우
        if (channel.getType() == ChannelType.PRIVATE){
            userIDs = userRepository.findAllByChannelId(id).stream()
                    .map(BaseEntity::getId)
                    .toList();
        }

        return channelMapper.toDTO(channel, userIDs, lastCreatedAt);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChannelDTO> findAllByUserID(UUID userID) {
        // ChannelRepo channel 전체 조회
        List<Channel> channels = channelRepository.findAllByUserId(userID);

        return channels.stream().map(channel -> {
            List<UUID> userIds = userRepository.findAllByChannelId(channel.getId()).stream()
                    .map(BaseEntity::getId).toList();
            Instant lastCreatedAt = messageRepository.findAllByChannel_Id(channel.getId()).stream()
                    .map(BaseEntity::getCreatedAt)
                    .findFirst().orElse(null);

            return channelMapper.toDTO(channel, userIds, lastCreatedAt);
        }).toList();
    }

    @Transactional
    @Override
    public ChannelDTO update(UUID channelID, ChannelUpdateRequest request) {
        // Private Channel일 경우 update 불가능
        Channel channel = channelRepository.findById(channelID)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelID));

        if(channel.getType() == ChannelType.PRIVATE) throw new IllegalArgumentException("Private Channel cannot be updated");

        // 필드 업데이트
        channel.updateName(request.newName());
        channel.updateDescription(request.newDescription());

        // DTO에 필요한 데이터 수집
        List<UUID> participantIds = userRepository.findAllByChannelId(channelID).stream()
                .map(BaseEntity::getId)
                .toList();

        Instant lastMessageAt = messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(channelID);

        // [저장]
        Channel savedChannel = channelRepository.save(channel);
        return channelMapper.toDTO(savedChannel, participantIds, lastMessageAt);
    }

    // channel 삭제
    @Transactional
    @Override
    public void deleteChannel(UUID channelID) {
        // 존재 확인
        Channel channel = channelRepository.findById(channelID)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelID));
        // [저장]
        channelRepository.deleteById(channelID);
    }

}
