package dev.bokukoha.mCS3Backup;

import org.bukkit.plugin.java.JavaPlugin;
import dev.bokukoha.mCS3Backup.Backup.makeBackup;

public final class MCS3Backup extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("MCS3Backup is now enabled!");
        getLogger().info("Backup will be created according to the schedule via config.yml.");

        //デフォルトコンフィグのロード
        saveDefaultConfig();

        //バックアップ作成、自動削除の呼び出し
        new makeBackup(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("MCS3Backup is now disabled!");
    }
}