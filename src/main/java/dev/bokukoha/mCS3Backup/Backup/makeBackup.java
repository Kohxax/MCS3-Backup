package dev.bokukoha.mCS3Backup.Backup;

import org.bukkit.plugin.java.JavaPlugin;

import com.cronutils.model.Cron;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.cronutils.model.definition.CronDefinitionBuilder;
import static com.cronutils.model.CronType.UNIX;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.io.*;

public class makeBackup {

    private final JavaPlugin plugin;
    private final File worldFolder;
    private final File backupFolder;

    //コンストラクタ
    public makeBackup(JavaPlugin plugin) {
        this.plugin = plugin;
        this.worldFolder = new File(plugin.getServer().getWorldContainer(), "world");
        this.backupFolder = new File(plugin.getDataFolder(), "backup");

        if (!backupFolder.exists()) {
            backupFolder.mkdirs();
        }
    }

    //バックアップスケジュール作成
    private void createBackupSchedule() {
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

        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            performBackup();

            createBackupSchedule();
        }, delayMillis / 50L); // Bukkitのスケジューラはティック単位なので50で割る
    }

    private void performBackup() {
    }
}
