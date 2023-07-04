package pw.alphabeta.authyoubungee.client

import com.google.common.util.concurrent.AbstractScheduledService
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler
import java.lang.Exception
import java.lang.IllegalStateException
import java.util.concurrent.TimeUnit

class AuthYouPlugin : Plugin(), Listener {
    private var playerChecker: PlayerChecker? = null
    var config: AuthYouPluginConfig? = null

    override fun onEnable() {
        val config = AuthYouPluginConfig.load(dataFolder.toString())
        this.config = config

        if (config.host.isBlank() || config.serverId.isBlank()) {
            throw NullPointerException("config.host / config.serverId was null.")
        }

        val playerChecker = PlayerChecker(config.host, config.serverId)
        playerChecker.timeout = config.requestTimeout
        this.playerChecker = playerChecker

        super.onEnable()

        proxy.pluginManager.registerListener(this, this)
        proxy.pluginManager.registerCommand(this, AuthYouCommand(this))

        logger.info("AuthYou Enabled: ${config.host}, ${config.serverId}")
    }

    override fun onDisable() {
        super.onDisable()
        logger.info("AuthYou Disabled")
    }

    fun saveConfig() {
        config!!.save(dataFolder.toString())
    }

    @EventHandler
    fun onPostLogin(e: PostLoginEvent) {
        val player = e.player

        if (config != null) {
            // allow loopback ip and private network ip (like 192.168.x.x)
            if (config!!.allowLocalIp && checkLocalPlayer(player)) {
                logger.info("Allow local user: " + player.name)
                return
            }

            // 관리자인 경우
            if (config!!.allowUser.contains(player.uniqueId.toString())) {
                logger.info("Allow whitelisted user: " + player.name)
                return
            }
        }

        // 일반 유저
        checkAuthYouPlayer(player)
    }
    
    private fun checkLocalPlayer(player: ProxiedPlayer): Boolean {
        val address = player.address.address
        return address.isLoopbackAddress || address.isSiteLocalAddress
    }

    private fun checkAuthYouPlayer(player: ProxiedPlayer) {
        if (playerChecker == null || config == null )
            throw IllegalStateException()

        val ip = player.address.hostName
        val uuid = player.uniqueId.toString()
        val name = player.name

        // DO NOT access bukkit APIs here
        val scheduler = proxy.scheduler
        scheduler.schedule(this, {
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
                scheduler.run { player.disconnect(TextComponent(finalMsg)) }
            }
        }, config!!.checkDelaySec, TimeUnit.SECONDS)
    }
}