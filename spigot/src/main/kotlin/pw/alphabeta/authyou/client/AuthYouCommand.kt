package pw.alphabeta.authyou.client

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import pw.alphabeta.authyou.plugin.IBukkitPlugin
import java.util.*

class AuthYouCommand(val plugin: AuthYouPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command?, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("authyou.command")) {
            sender.sendMessage("You don't have a permission to use this command")
            return false
        }

        val cmd = args[0]

        if (args.size == 1) {
            if (cmd == "list") {
                val count = plugin.config!!.allowUser.size
                sender.sendMessage("allowUser list: $count items")
                sender.sendMessage(plugin.config!!.allowUser.joinToString("\n"))
                return false
            }
        }
        else if (args.size == 2) {
            val arg = args[1]

            if (cmd == "addname") {
                val target = Bukkit.getPlayer(arg)
                if (target == null) {
                    sender.sendMessage("Cannot find user: $arg")
                    return false
                }

                val playerUUID = target.uniqueId.toString()
                addAllowUserUUID(playerUUID)
                plugin.saveConfig()
                sender.sendMessage("$arg ($playerUUID) was added")
                return true
            }
            else if (cmd == "removename") {
                val target = Bukkit.getPlayer(arg)
                if (target == null) {
                    sender.sendMessage("Cannot find user: $arg")
                    return false
                }

                val playerUUID = target.uniqueId.toString()
                if (plugin.config!!.allowUser.contains(playerUUID)) {
                    removeAllowUserUUID(playerUUID)
                    plugin.saveConfig()
                    sender.sendMessage("$arg ($playerUUID) was removed")
                }
                else {
                    sender.sendMessage("Cannot find user: $playerUUID")
                }

                return true
            }
            else if (cmd == "adduuid") {
                if (!Util.isValidUUID(arg)) {
                    sender.sendMessage("Invalid UUID")
                    return false
                }

                addAllowUserUUID(arg)
                sender.sendMessage("$arg was added")
                return true
            }
            else if (cmd == "removeuuid") {
                if (!Util.isValidUUID(arg)) {
                    sender.sendMessage("Invalid UUID")
                    return false
                }

                removeAllowUserUUID(arg)
                sender.sendMessage("$arg was removed")
                return true
            }
        }

        sender.sendMessage("Unknown command")
        return false
    }

    private fun addAllowUserUUID(uuid: String) {
        plugin.config!!.allowUser.add(uuid.lowercase(Locale.getDefault()))
    }

    private fun removeAllowUserUUID(uuid: String) {
        plugin.config!!.allowUser.remove(uuid.lowercase(Locale.getDefault()))
    }
}