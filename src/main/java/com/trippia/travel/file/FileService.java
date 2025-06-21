package com.trippia.travel.file;

import com.trippia.travel.exception.file.FileException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.url}")
    private String s3Url;

    private static final String[] ALLOWED_CONTENT_TYPES = {"image/jpeg", "image/png", "image/gif"};
    private static final String UPLOAD_FILE_SUCCESS_MESSAGE = "파일이 성공적으로 업로드 되었습니다.";

    private final S3Client s3Client;

    public FileResponse uploadFile(MultipartFile uploadFile) {
        validateFile(uploadFile);
        String uniqueFilename = generateUniqueFileName(uploadFile.getOriginalFilename());
        uploadToS3(uploadFile, uniqueFilename);
        String fileUrl = s3Url + uniqueFilename;
        return FileResponse.builder()
                .isUploaded(true)
                .url(fileUrl)
                .message(UPLOAD_FILE_SUCCESS_MESSAGE)
                .build();
    }


    private void uploadToS3(MultipartFile file, String uniqueFileName) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(uniqueFileName)
                    .contentType(file.getContentType())
                    .build();

            // 파일을 S3에 업로드
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        } catch (Exception e) {
            throw new FileException("파일 업로드 중 오류가 발생했습니다.");
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }



    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileException("파일이 존재하지 않습니다.");
        }
        boolean isValidContentType = checkContentType(file);
        if(!isValidContentType){
            throw new FileException("지원하지 않는 파일 형식입니다.");
        }
    }

    private static boolean checkContentType(MultipartFile file) {
        boolean isValidContentType = false;
        for (String allowedContentType : ALLOWED_CONTENT_TYPES) {
            if (allowedContentType.equals(file.getContentType())) {
                isValidContentType = true;
            }
        }
        return isValidContentType;
    }

}
