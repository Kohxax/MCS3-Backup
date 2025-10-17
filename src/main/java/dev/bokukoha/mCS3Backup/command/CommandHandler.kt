package dev.bokukoha.mCS3Backup.command

import org.bukkit.command.*
import org.bukkit.plugin.java.JavaPlugin
import dev.bokukoha.mCS3Backup.Backup.makeBackup

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

                // 未テスト
                reloadConfig(sender)
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
    // 未テスト reloadでコンフィグを読んだ後にmakeBackupのスケジューラを走らせる処理（予定）
    // pluginインスタンスの渡しがよくわかってないからたぶん動かない

    private fun reloadConfig(sender: CommandSender) {
        plugin.reloadConfig()
        plugin.saveDefaultConfig()

        plugin.config.options().copyDefaults(true)
        plugin.saveConfig()

        sender.sendMessage("§a[MCS3-Backup]" + "§fconfig reloaded!")

        backup.cancelBackupSchedule()
        backup = makeBackup(plugin)
    }

    private fun helpMassage(sender: CommandSender) {
        sender.sendMessage("§f---- §a[MCS3-Backup] §f----")
        sender.sendMessage("§a/mcs3 help §f- Show this help message")
        sender.sendMessage("/mcs3 reload §f- Reload the configuration file")
    }
}