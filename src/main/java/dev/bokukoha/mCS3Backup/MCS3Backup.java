package dev.bokukoha.mCS3Backup;

import org.bukkit.plugin.java.JavaPlugin;
import dev.bokukoha.mCS3Backup.Backup.makeBackup;
import dev.bokukoha.mCS3Backup.command.*;

public final class MCS3Backup extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("MCS3Backup is now enabled!");
        getLogger().info("Backup will be created according to the schedule via config.yml.");

        //デフォルトコンフィグのロード
        saveDefaultConfig();

        //バックアップ作成、自動削除の呼び出し
        makeBackup backup = new  makeBackup(this);

        // コマンドの登録 makeBackupインスタンスの使いまわし用
        getCommand("mcs3").setExecutor(new CommandHandler(this, backup));

        // タブ補完の登録
        getCommand("mcs3").setTabCompleter(new TabCompleter());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("MCS3Backup is now disabled!");
    }
}