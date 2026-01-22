package com.ohgiraffers.backendapi.global.s3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.backendapi.global.error.CustomException;
import com.ohgiraffers.backendapi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

/**
 * AWS S3 파일 업로드/삭제 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Client s3Client;
    private final ObjectMapper objectMapper;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    /**
     * 파일을 S3에 업로드하고 URL 반환
     */
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.FILE_NOT_FOUND);
        }

        try {
            // 유니크한 파일명 생성
            String originalFilename = file.getOriginalFilename();
            String storeFileName = "chapters/" + UUID.randomUUID() + "_" + originalFilename;

            // S3 업로드 요청 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storeFileName)
                    .contentType(file.getContentType())
                    .build();

            // 파일 업로드
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(
                    file.getInputStream(), file.getSize()));

            // S3 URL 생성 및 반환
            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                    bucketName, region, storeFileName);

            log.info("S3에 파일 업로드 완료: {}", fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("S3 파일 업로드 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR, e.getMessage());
        }
    }

    /**
     * S3에서 파일 삭제
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            // URL에서 S3 key 추출
            String key = extractKeyFromUrl(fileUrl);

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("S3에서 파일 삭제 완료: {}", key);

        } catch (Exception e) {
            log.warn("S3 파일 삭제 실패 (무시됨): {}", e.getMessage());
        }
    }

    /**
     * JSON 파일에서 paragraph 개수 카운팅 (content 배열 내 id 개수)
     */
    public int countParagraphsFromFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return -1;
        }

        try {
            JsonNode rootNode = objectMapper.readTree(file.getInputStream());

            // content 배열이 있는지 확인
            if (rootNode.has("content") && rootNode.get("content").isArray()) {
                JsonNode contentArray = rootNode.get("content");
                int count = 0;

                // content 배열 내의 각 요소에서 id가 있으면 카운트
                for (JsonNode item : contentArray) {
                    if (item.has("id")) {
                        count++;
                    }
                }

                log.debug("JSON 파일 paragraph 개수: {}", count);
                return count;
            }

            return -1;

        } catch (IOException e) {
            log.warn("paragraph 카운팅 실패: {}", e.getMessage());
            return -1;
        }
    }

    /**
     * S3 URL에서 key 추출
     */
    private String extractKeyFromUrl(String fileUrl) {
        // https://bucket.s3.region.amazonaws.com/key 형식에서 key 추출
        String prefix = String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);
        if (fileUrl.startsWith(prefix)) {
            return fileUrl.substring(prefix.length());
        }

        // 다른 형식 처리 (https://s3.region.amazonaws.com/bucket/key)
        String altPrefix = String.format("https://s3.%s.amazonaws.com/%s/", region, bucketName);
        if (fileUrl.startsWith(altPrefix)) {
            return fileUrl.substring(altPrefix.length());
        }

        // key 형식이 아닌 경우 그대로 반환
        return fileUrl;
    }
}
