package dev.bokukoha.mCS3Backup.AWS;

//AWS SDK for Java v2
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.StorageClass;

//Javaのやつ
import java.nio.file.Path;

//S3にファイルをアップロードする
public class putObject {
    public static final Logger logger = LoggerFactory.getLogger(putObject.class);

    public static void uploadToS3(String bucketName, String objectKey, Path filePath) {
        // S3クライアントを作成
        S3Client s3 = S3Client.builder()
                .region(Region.AP_NORTHEAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        // アップロードリクエストを作成
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .storageClass(StorageClass.DEEP_ARCHIVE)
                .build();

        try {
            // ファイルをアップロード
            PutObjectResponse response = s3.putObject(putObjectRequest, RequestBody.fromFile(filePath));
            logger.info("File uploaded successfully. ETag: " + response.eTag());
        } catch (S3Exception e) {
            logger.error("Failed to upload file to S3: " + e.awsErrorDetails().errorMessage());
        } finally {
            // S3クライアントを閉じる
            s3.close();
        }
    }
}