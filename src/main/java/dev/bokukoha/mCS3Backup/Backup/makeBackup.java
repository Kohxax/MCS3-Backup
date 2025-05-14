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
    public void createBackupSchedule() {
        //バックアップ時間を取得
        String backupTime = plugin.getConfig().getString("backup-time", "0 0 * * *");


    }
}
