package dev.bokukoha.mCS3Backup.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class TabCompleter : TabCompleter{
    override  fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {

        if (!command.name.equals("mcs3", ignoreCase = true)) {
            return mutableListOf()
        }

        val completions = mutableListOf<String>()

        if (args.size == 1) {
            val subCommands = listOf("reload", "next", "help")
            completions.addAll(subCommands.filter { it.startsWith(args[0], ignoreCase = true)})
        }

        return completions
    }
}
