package dev.bokukoha.mCS3Backup.Backup;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class deleteBackup {
    private final JavaPlugin plugin;
    private final File backupFolder;

    public deleteBackup(JavaPlugin plugin) {
        this.plugin = plugin;
        this.backupFolder = new File(plugin.getDataFolder(), "backup");

        if (!backupFolder.exists()) {
            plugin.getLogger().warning("Backup folder does not exist: " + backupFolder.getAbsolutePath());
            return;
        }

        deleteOldBackups();
    }

    //backupフォルダ内の古いバックアップを削除（カスタム）
    private void deleteOldBackups() {
        File[] backups = backupFolder.listFiles((dir, name) -> name.endsWith(".zip"));

        if (backups == null || backups.length <= 1) {
            return;
        }

        // バックアップファイルを最終更新日時でソート（新しい順）
        Arrays.sort(backups, Comparator.comparing(File::lastModified).reversed());

        // 最新のバックアップを除いて削除
        for (int i = 1; i < backups.length; i++) {
            if (backups[i].delete()) {
                plugin.getLogger().info("Deleted old backup: " + backups[i].getName());
            } else {
                plugin.getLogger().warning("Failed to delete backup: " + backups[i].getName());
            }
        }
    }
}
