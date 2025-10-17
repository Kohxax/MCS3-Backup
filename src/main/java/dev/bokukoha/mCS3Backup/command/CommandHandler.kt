package dev.bokukoha.mCS3Backup.command

import org.bukkit.command.*
import org.bukkit.plugin.java.JavaPlugin
import dev.bokukoha.mCS3Backup.Backup.makeBackup
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CommandHandler(private val plugin: JavaPlugin, private var backup: makeBackup) : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : Boolean {

        // 一要素目チェック
        if (!command.name.equals("mcs3", ignoreCase = true)) return false

        // パーミッション
        if (!sender.hasPermission("MCS3Backup.commands.mcs3")) {
            havePermission(sender)
            return true
        }

        val subCommand = args.firstOrNull()?.lowercase()

        if (subCommand == null) {
            unknownCommand(sender)
            return true
        }

        when (subCommand) {
            "reload" -> {
                reloadConfig(sender)
            }

            "next" -> {
                showNextBackupTime(sender)
            }

            "help" -> {
                helpMassage(sender)
            }

            else -> unknownCommand(sender)
        }

        return true
    }

    private fun havePermission(sender: CommandSender) {
        sender.sendMessage("§a[MCS3-Backup]" + "§4You do not have permission!")
    }

    private fun unknownCommand(sender: CommandSender) {
        sender.sendMessage("§a[MCS3-Backup]" + "§fUnknown Command! /mcs3 help for command list")
    }

    // reloadコマンド
    // うごいた

    private fun reloadConfig(sender: CommandSender) {
        plugin.reloadConfig()
        plugin.saveDefaultConfig()

        plugin.config.options().copyDefaults(true)
        plugin.saveConfig()

        sender.sendMessage("§a[MCS3-Backup]" + " §fconfig reloaded!")

        backup.cancelBackupSchedule()
        backup = makeBackup(plugin)
    }

    private fun helpMassage(sender: CommandSender) {
        sender.sendMessage("§f---- §a[MCS3-Backup] §f----")
        sender.sendMessage("§a/mcs3 help §f- Show this help message")
        sender.sendMessage("§a/mcs3 reload §f- Reload the configuration file")
        sender.sendMessage("§a/mcs3 next §f- Show the next scheduled backup time")
    }

    // makeBackupから次回スケジュール呼び出して表示
    private fun showNextBackupTime(sender: CommandSender) {
        val nextBackupTime = backup.getNextBackupTime()

        // makeBackupでnull弾くようにしてるけど念のため
        if (nextBackupTime == null) {
            sender.sendMessage("§a[MCS3-Backup] §fNo backup scheduled.")
            return
        }

        val now = ZonedDateTime.now()
        val duration = Duration.between(now, nextBackupTime)

        val hours = duration.toHours()
        val minutes = duration.toMinutesPart()

        val formatted = nextBackupTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"))

        sender.sendMessage("§a[MCS3-Backup] §fNext backup scheduled at: §e$formatted")
        sender.sendMessage("§a[MCS3-Backup] §fTime remaining: §e$hours hours and $minutes minutes")
    }
}