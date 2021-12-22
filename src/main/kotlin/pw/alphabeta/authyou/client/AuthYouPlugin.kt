package pw.alphabeta.authyou.client

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.Configuration
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.lang.Exception
import java.lang.IllegalStateException

class AuthYouPlugin : JavaPlugin(), Listener, CommandExecutor {
    private var scheduler: BukkitScheduler? = null
    private var playerChecker: PlayerChecker? = null
    private var config: AuthYouPluginConfig? = null

    override fun onEnable() {
        val config = AuthYouPluginConfig.load(this)
        this.config = config
        saveConfig()

        if (config.host.isBlank() || config.serverId.isBlank()) {
            throw NullPointerException("config.host / config.serverId was null.")
        }

        val playerChecker = PlayerChecker(config.host, config.serverId)
        playerChecker.timeout = config.requestTimeout
        this.playerChecker = playerChecker

        super.onEnable()

        scheduler = Bukkit.getScheduler()
        server.pluginManager.registerEvents(this, this)
        getCommand("authyou").executor = this

        logger.info("AuthYou Enabled: ${config.host}, ${config.serverId}")
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if(args.size != 2) return false
        val str = args[0]
        if(!sender.isOp) return false
        val p = Bukkit.getOfflinePlayer(args[1]) ?: return false
        val uuid = p.uniqueId.toString()
        if(str == "add") {
            if(config!!.allowUser.contains(uuid)) return false
            config!!.allowUser.add(uuid)
            config!!.save(this)
            sender.sendMessage("allowUser add!")
        } else if(str == "remove") {
            if(!config!!.allowUser.contains(uuid)) return false
            config!!.allowUser.remove(uuid)
            config!!.save(this)
            sender.sendMessage("allowUser remove!")
        }
        return true
    }

    override fun onDisable() {
        super.onDisable()
        logger.info("AuthYou Disabled")
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player

        if (config != null) {
            // allow loopback ip and private network ip (like 192.168.x.x)
            if (config!!.allowLocalIp && checkLocalPlayer(player)) {
                logger.info("Allow local user: " + player.name)
                return
            }

            // 관리자인 경우
            if (config!!.allowUser.contains(player.uniqueId.toString()))
                return
        }
        
        // 일반 유저
        checkAuthYouPlayer(player)
    }
    
    private fun checkLocalPlayer(player: Player): Boolean {
        val address = player.address.address
        return address.isLoopbackAddress || address.isSiteLocalAddress
    }

    private fun checkAuthYouPlayer(player: Player) {
        if (scheduler == null || playerChecker == null || config == null )
            throw IllegalStateException()

        val ip = player.address.hostName
        val uuid = player.uniqueId.toString()
        val name = player.name

        // DO NOT access bukkit APIs here
        scheduler!!.runTaskLaterAsynchronously(this, {
            var isSuccess = true
            var detailedKickMessage = ""

            try {
                val checkResult = playerChecker!!.checkPlayer(ip, uuid)
                if (!checkResult.result) {
                    isSuccess = false
                    detailedKickMessage = checkResult.msg ?: "(no msg)"
                    logger.info("Unauthorized player: $name, $detailedKickMessage")
                }
            } catch (e: Exception) {
                logger.warning("Exception on request: $name")
                logger.warning(e.toString())

                if (!config!!.passOnError) {
                    isSuccess = false
                    detailedKickMessage = e.message ?: e::class.simpleName ?: "exception"
                }
            }

            if (!isSuccess) {
                var msg = config!!.kickMessage
                if (config!!.useDetailKickMessage)
                    msg += "\n" + detailedKickMessage

                val finalMsg = msg
                scheduler!!.runTask(this) { player.kickPlayer(finalMsg) }
            }
        }, config!!.checkDelayTick)
    }
}