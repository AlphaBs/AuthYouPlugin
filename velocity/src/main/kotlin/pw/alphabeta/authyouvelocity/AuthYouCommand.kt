package pw.alphabeta.authyouvelocity

import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.command.SimpleCommand.Invocation
import net.kyori.adventure.text.Component
import java.util.*

class AuthYouCommand(val plugin: AuthYouPlugin): SimpleCommand {
    override fun execute(invocation: Invocation) {
        val source = invocation.source()
        val args = invocation.arguments()
        
        if (args.isEmpty()) {
            source.sendMessage(Component.text("명령어 사용 방법: https://github.com/AlphaBs/AuthYouPlugin/blob/main/README.md#commands"))
            return
        }

        val cmd = args[0]
        if (args.size == 1) {
            if (cmd == "list") {
                val count = plugin.config!!.allowUser.size
                source.sendMessage(Component.text("allowUser list: $count items"))
                source.sendMessage(Component.text(plugin.config!!.allowUser.joinToString("\n")))
                return
            }
        }
        else if (args.size == 2) {
            val arg = args[1]

            if (cmd == "addname") {
                val playerUUID = plugin.getPlayerId(arg)
                if (playerUUID == null) {
                    source.sendMessage(Component.text("Cannot find user: $arg"))
                    return
                }

                addAllowUserUUID(playerUUID)
                plugin.saveConfig()
                source.sendMessage(Component.text("$arg ($playerUUID) was added"))
                return
            }
            else if (cmd == "removename") {
                val playerUUID = plugin.getPlayerId(arg)
                if (playerUUID == null) {
                    source.sendMessage(Component.text("Cannot find user: $arg"))
                    return
                }

                if (plugin.config!!.allowUser.contains(playerUUID)) {
                    removeAllowUserUUID(playerUUID)
                    plugin.saveConfig()
                    source.sendMessage(Component.text("$arg ($playerUUID) was removed"))
                }
                else {
                    source.sendMessage(Component.text("Cannot find user: $playerUUID"))
                }

                return
            }
            else if (cmd == "adduuid") {
                if (!Util.isValidUUID(arg)) {
                    source.sendMessage(Component.text("Invalid UUID"))
                    return
                }

                addAllowUserUUID(arg)
                plugin.saveConfig()
                source.sendMessage(Component.text("$arg was added"))
                return
            }
            else if (cmd == "removeuuid") {
                if (!Util.isValidUUID(arg)) {
                    source.sendMessage(Component.text("Invalid UUID"))
                    return
                }

                removeAllowUserUUID(arg)
                plugin.saveConfig()
                source.sendMessage(Component.text("$arg was removed"))
                return
            }
        }

        source.sendMessage(Component.text("Unknown command"))
    }

    private fun addAllowUserUUID(uuid: String) {
        plugin.config!!.allowUser.add(uuid.lowercase(Locale.getDefault()))
    }

    private fun removeAllowUserUUID(uuid: String) {
        plugin.config!!.allowUser.remove(uuid.lowercase(Locale.getDefault()))
    }

    override fun hasPermission(invocation: Invocation?): Boolean {
        return invocation?.source()?.hasPermission("authyou.command") ?: false
    }
}