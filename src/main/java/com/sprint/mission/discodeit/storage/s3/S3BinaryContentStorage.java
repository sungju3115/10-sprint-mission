package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentDTO;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.event.StorageFailedEvent;
import com.sprint.mission.discodeit.exception.storage.FileStorageException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;
    private final long presignedUrlExpiration;
    private final ApplicationEventPublisher applicationEventPublisher;

    public S3BinaryContentStorage(
            @Value("${discodeit.storage.s3.access-key}") String accessKey,
            @Value("${discodeit.storage.s3.secret-key}") String secretKey,
            @Value("${discodeit.storage.s3.region}") String region,
            @Value("${discodeit.storage.s3.bucket}") String bucket,
            @Value("${discodeit.storage.s3.presigned-url-expiration}") long presignedUrlExpiration,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        // 임시 credentials
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
        );
        // region, client, presigner, bucket, presignedUrlExpiration
        Region awsRegion = Region.of(region);
        s3Client = S3Client.builder()
                .region(awsRegion)
                .credentialsProvider(credentialsProvider)
                .build();
        s3Presigner = S3Presigner.builder()
                .region(awsRegion)
                .credentialsProvider(credentialsProvider)
                .build();
        bucketName = bucket;
        this.presignedUrlExpiration = presignedUrlExpiration;
        this.applicationEventPublisher = applicationEventPublisher;
    }
    @Override
    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 2.0
            )
    )
    public UUID put(UUID contentID, byte[] bytes) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(contentID.toString())
                        .build(),
                RequestBody.fromBytes(bytes)
        );
        return contentID;
    }

    @Override
    public InputStream get(UUID contentID) {
        return s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(contentID.toString())
                        .build()
        );
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDTO content) {
        String presignedUrl = s3Presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .getObjectRequest(r -> r
                                .bucket(bucketName)
                                .key(content.id().toString())
                        ).build()
        ).url().toString();
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, presignedUrl)
                .build();
    }

    @Recover
    public UUID recover(Exception ex, UUID id, byte[] bytes) {
        String requestId = MDC.get("requestId");
        String title = "S3 파일 업로드 실패";
        String message = String.format(
                "RequestId: %s\nBinaryContentId: %s\nError: %s",
                requestId, id, ex.getMessage()
        );

        applicationEventPublisher.publishEvent(new StorageFailedEvent(title, message));
        throw new FileStorageException(id.toString());
    }
}
