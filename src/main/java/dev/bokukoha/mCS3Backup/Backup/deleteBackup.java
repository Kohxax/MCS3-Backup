package dev.bokukoha.mCS3Backup.Backup;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

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

    // backupフォルダ内の古いバックアップを削除（カスタム）
    private void deleteOldBackups() {
        File[] backups = backupFolder.listFiles((dir, name) -> name.endsWith(".zip"));

        if (backups == null || backups.length == 0) {
            return;
        }

        Map<String, List<File>> backupsByWorld = new HashMap<>();

        // ワールド名ソート
        for (File file : backups) {
            String name = file.getName();
            int dashIndex = name.indexOf('-');
            if (dashIndex == -1) {
                continue; // 無効なファイル名はスキップ
            }

            String worldName = name.substring(0, dashIndex);
            backupsByWorld.computeIfAbsent(worldName, k -> new ArrayList<>()).add(file);
        }

        // 各ワールドごとにバックアップを削除
        for (Map.Entry<String, List<File>> entry : backupsByWorld.entrySet()) {
            List<File> files = entry.getValue();

            files.sort(Comparator.comparingLong(File::lastModified).reversed());

            for (int i = 0; i < files.size(); i++) {
                File oldfile = files.get(i);

                // configから保持するバックアップの数を取得
                int keepCount = plugin.getConfig().getInt("backup-keep-count", 1);

                if (i >= keepCount) {
                    // 最新のバックアップを除いて削除
                    if (oldfile.delete()) {
                        plugin.getLogger().info("Deleted old backup: " + oldfile.getName());
                    } else {
                        plugin.getLogger().warning("Failed to delete backup: " + oldfile.getName());
                    }
                }
            }
        }
    }
}
