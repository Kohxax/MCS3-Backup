package dev.bokukoha.mCS3Backup.Backup;

import org.bukkit.plugin.java.JavaPlugin;

import dev.bokukoha.mCS3Backup.AWS.putObject;

import com.cronutils.model.Cron;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.cronutils.model.definition.CronDefinitionBuilder;

import static com.cronutils.model.CronType.UNIX;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class makeBackup {

    private final JavaPlugin plugin;
    private final File worldFolder;
    private final File backupFolder;

    // reloadされた後にタスクをキャンセルするためのID（未テスト）
    private int backupTaskId = -1;

    //コンストラクタ
    public makeBackup(JavaPlugin plugin) {
        this.plugin = plugin;
        this.worldFolder = new File(plugin.getServer().getWorldContainer(), "world");
        this.backupFolder = new File(plugin.getDataFolder(), "backup");

        if (!backupFolder.exists()) {
            backupFolder.mkdirs();
        }

        createBackupSchedule();
    }

    // バックアップスケジュールが既にある場合キャンセルしてから再作成

    public void cancelBackupSchedule() {
        if (backupTaskId != -1) {
            plugin.getServer().getScheduler().cancelTask(backupTaskId);
            backupTaskId = -1;
        }
    }

    //バックアップスケジュール作成
    public void createBackupSchedule() {

        //バックアップ時間を取得
        String backupTime = plugin.getConfig().getString("backup-time", "0 0 * * *");

        //CronParserを作成
        CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(UNIX));
        Cron cron = parser.parse(backupTime);
        ExecutionTime executionTime = ExecutionTime.forCron(cron);

        ZonedDateTime now = ZonedDateTime.now();
        Optional<ZonedDateTime> nextExecution = executionTime.nextExecution(now);

        if (nextExecution.isEmpty()) {
            plugin.getLogger().warning("Invalid cron expression: " + backupTime);
            return;
        }

        long delayMillis = nextExecution.get().toInstant().toEpochMilli() - System.currentTimeMillis();
        long IntervalMillis = 24 * 60 * 60 * 1000L;


        // ここテストまだ
        // CommandHandlerでreloadされたときに、既存のスケジューラをキャンセルする用

        backupTaskId = plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            performBackup();

            createBackupSchedule();
        }, delayMillis / 50L).getTaskId(); // Bukkitのスケジューラはティック単位なので50で割る
    }

    private void performBackup() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

        plugin.getServer().broadcastMessage("§a[MCS3-Backup]" + "§fワールドのバックアップを開始します。");
        plugin.getLogger().info("Starting backup at " + timeStamp);

        //ワールドリストをconfigからリスト取得
        List<String> worlds = plugin.getConfig().getStringList("backup-worlds");

        List<File> createdBackups = new ArrayList<>();

        for (String worldName : worlds) {
            File worldFolder = new File(plugin.getServer().getWorldContainer(), worldName);

            if (!worldFolder.exists() || !worldFolder.isDirectory()) {
                plugin.getLogger().warning("World folder does not exist or is not a directory: " + worldName);
                continue;
            }

            File zipFile = new File(backupFolder, worldName + "-" + timeStamp + ".zip");

            try (FileOutputStream fos = new FileOutputStream(zipFile);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {
                Path sourcePath = worldFolder.toPath();
                Files.walk(sourcePath).filter(Files::isRegularFile).forEach(path -> {
                    String relativePath = sourcePath.relativize(path).toString();

                    // session.lockをスキップ
                    if (relativePath.equalsIgnoreCase("session.lock")) {
                        return;
                    }

                    ZipEntry zipEntry = new ZipEntry(relativePath);
                    try {
                        zos.putNextEntry(zipEntry);
                        Files.copy(path, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        plugin.getLogger().severe("Failed to add file to backup: " + path + " - " + e.getMessage());
                    }
                });

                plugin.getLogger().info("Backup completed successfully: " + worldName + "->" + zipFile.getName());
                createdBackups.add(zipFile);

            } catch (Exception e) {
                plugin.getLogger().severe("Failed to create backup: " + e.getMessage());
                e.printStackTrace();
            }
        }

        plugin.getServer().broadcastMessage("§a[MCS3-Backup]" + "§fバックアップが正常に完了しました。");

        // 古いバックアップを削除
        new deleteBackup(plugin);

        //S3にputする部分
        String prefix = plugin.getConfig().getString("S3.upload-prefix", "backups/");

        if (!prefix.endsWith("/")) {
            prefix += "/";
        }

        for (File zipFile : createdBackups) {
            String objectKey = prefix + zipFile.getName();
            putObject.uploadToS3(plugin.getConfig(), objectKey, zipFile.toPath());
        }
    }
}
