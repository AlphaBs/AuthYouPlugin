package pw.alphabeta.authyoubungee.client

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.Plugin
import java.util.*

class AuthYouCommand(val plugin: AuthYouPlugin) : Command("authyou") {
    override fun execute(sender: CommandSender, args: Array<out String>) {
        if (!sender.hasPermission("authyou.command")) {
            sender.sendMessage(TextComponent("You don't have a permission to use this command"))
            return
        }

        if (args.isEmpty()) {
            sender.sendMessage(TextComponent("명령어 사용 방법: https://github.com/AlphaBs/AuthYouPlugin/blob/main/README.md#commands"))
            return
        }

        val cmd = args[0]

        if (args.size == 1) {
            if (cmd == "list") {
                val count = plugin.config!!.allowUser.size
                sender.sendMessage(TextComponent("allowUser list: $count items"))
                sender.sendMessage(TextComponent(plugin.config!!.allowUser.joinToString("\n")))
                return
            }
        }
        else if (args.size == 2) {
            val arg = args[1]

            if (cmd == "addname") {
                val target = plugin.proxy.getPlayer(arg)
                if (target == null) {
                    sender.sendMessage(TextComponent("Cannot find user: $arg"))
                    return
                }

                val playerUUID = target.uniqueId.toString()
                addAllowUserUUID(playerUUID)
                plugin.saveConfig()
                sender.sendMessage(TextComponent("$arg ($playerUUID) was added"))
                return
            }
            else if (cmd == "removename") {
                val target = plugin.proxy.getPlayer(arg)
                if (target == null) {
                    sender.sendMessage(TextComponent("Cannot find user: $arg"))
                    return
                }

                val playerUUID = target.uniqueId.toString()
                if (plugin.config!!.allowUser.contains(playerUUID)) {
                    removeAllowUserUUID(playerUUID)
                    plugin.saveConfig()
                    sender.sendMessage(TextComponent("$arg ($playerUUID) was removed"))
                }
                else {
                    sender.sendMessage(TextComponent("Cannot find user: $playerUUID"))
                }

                return
            }
            else if (cmd == "adduuid") {
                if (!Util.isValidUUID(arg)) {
                    sender.sendMessage(TextComponent("Invalid UUID"))
                    return
                }

                addAllowUserUUID(arg)
                plugin.saveConfig()
                sender.sendMessage(TextComponent("$arg was added"))
                return
            }
            else if (cmd == "removeuuid") {
                if (!Util.isValidUUID(arg)) {
                    sender.sendMessage(TextComponent("Invalid UUID"))
                    return
                }

                removeAllowUserUUID(arg)
                plugin.saveConfig()
                sender.sendMessage(TextComponent("$arg was removed"))
                return
            }
        }

        sender.sendMessage(TextComponent("Unknown command"))
    }

    private fun addAllowUserUUID(uuid: String) {
        plugin.config!!.allowUser.add(uuid.lowercase(Locale.getDefault()))
    }

    private fun removeAllowUserUUID(uuid: String) {
        plugin.config!!.allowUser.remove(uuid.lowercase(Locale.getDefault()))
    }
}