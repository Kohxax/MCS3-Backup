package dev.bokukoha.mCS3Backup;

import org.bukkit.plugin.java.JavaPlugin;

public final class MCS3Backup extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("MCS3Backup is now enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("MCS3Backup is now disabled!");
    }
}
