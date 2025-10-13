package dev.bokukoha.mCS3Backup.AWS;

//AWS SDK for Java v2
import org.bukkit.configuration.file.FileConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
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

    public static void uploadToS3(FileConfiguration config, String objectKey, Path filePath) {

        // config.ymlから設定を取得
        Boolean enableS3 = config.getBoolean("S3.enable", false);
        String regionName = config.getString("S3.region", "ap-northeast-1");
        String bucketName = config.getString("S3.bucket", "your-bucket-name");
        String storageClassName = config.getString("S3.storage-class", "STANDARD");
        String accessKey = config.getString("S3.access-key");
        String secretKey = config.getString("S3.secret-key");

        if (!enableS3) {
            logger.info("S3 upload is disabled in config.yml");
            return;
        }

        if (bucketName == null || accessKey == null || secretKey == null) {
            logger.error("S3 configuration is missing in config.yml");
            return;
        }

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
        Region region = Region.of(regionName);

        try (S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build()) {

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .storageClass(StorageClass.fromValue(storageClassName))
                    .build();

            PutObjectResponse response = s3.putObject(request, RequestBody.fromFile(filePath));
            logger.info("File uploaded to S3 with ETag: " + response.eTag());

        } catch (S3Exception e) {
            logger.error("Failed to upload to S3: " + e.awsErrorDetails().errorMessage());
        }
        catch (Exception e) {
            logger.error("Unexpected error: " + e.getMessage());
        }
    }
}