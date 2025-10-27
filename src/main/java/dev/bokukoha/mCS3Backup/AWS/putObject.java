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
import software.amazon.awssdk.http.apache.ApacheHttpClient;

//Javaのやつ
import java.nio.file.Path;

//S3にファイルをアップロードする
public class putObject {
    public static final Logger logger = LoggerFactory.getLogger(putObject.class);

    private static boolean enableS3;
    private static String regionName;
    private static String bucketName;
    private static String storageClassName;
    private static String accessKey;
    private static String secretKey;

    // reload時に呼ぶ
    public static void loadAWSConfig(FileConfiguration config) {
        // config.ymlから設定を取得
        enableS3 = config.getBoolean("S3.enabled", false);
        regionName = config.getString("S3.region", "ap-northeast-1");
        bucketName = config.getString("S3.bucket", "your-bucket-name");
        storageClassName = config.getString("S3.storage-class", "STANDARD");
        accessKey = config.getString("S3.access-key");
        secretKey = config.getString("S3.secret-access-key");

        if (accessKey == null || secretKey == null) {
            logger.error("S3 configuration is missing in config.yml");
            return;
        }

        logger.info("S3 configuration loaded successfully");
    }

    public static void uploadToS3(FileConfiguration config, String objectKey, Path filePath) {

        if (!enableS3) {
            logger.info("S3 upload skipped because S3 is disabled in config.");
            return;
        }

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
        Region region = Region.of(regionName);

        // S3クライアントを作ってアップロードする部分
        // ApacheHttpClient使わないとエラー出る

        try (S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .httpClient(ApacheHttpClient.builder().build())
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