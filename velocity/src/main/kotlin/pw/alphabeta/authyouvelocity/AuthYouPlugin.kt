package pw.alphabeta.authyouvelocity

import com.google.inject.Inject
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.ResultedEvent
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.text.Component
import org.slf4j.Logger
import java.nio.file.Path
import kotlin.jvm.optionals.getOrNull

@Plugin(
    id = "authyoupluginvelocity",
    name = "AuthYouPluginVelocity",
    version = "0.1.0-SNAPSHOT",
    url = "https://github.com/AlphaBs/AuthYouPlugin",
    description = "AuthYou plugin for Velocity",
    authors = ["AlphaBs"]
)
class AuthYouPlugin @Inject constructor(
    server: ProxyServer, logger: Logger, @DataDirectory dataDirectory: Path) {

    private val server = server
    private val logger = logger
    private val dataDirectory = dataDirectory
    private var playerChecker: PlayerChecker? = null
    var config: AuthYouPluginConfig? = null

    @Subscribe
    private fun onProxyInitialization(event: ProxyInitializeEvent) {
        val config = AuthYouPluginConfig.load(dataDirectory.toString())
        this.config = config

        if (config.host.isBlank() || config.serverId.isBlank()) {
            throw NullPointerException("config.host / config.serverId was null.")
        }

        val playerChecker = PlayerChecker(config.host, config.serverId)
        playerChecker.timeout = config.requestTimeout
        this.playerChecker = playerChecker

        val commandManager = server.commandManager
        val commandMeta = commandManager.metaBuilder("authyou")
            .plugin(this)
            .build()
        commandManager.register(commandMeta, AuthYouCommand(this))

        logger.info("AuthYou Enabled: ${config.host}, ${config.serverId}")
    }

    @Subscribe
    private fun onProxyShutdown(event: ProxyShutdownEvent) {
        saveConfig()
    }

    fun getPlayerId(username: String): String? {
        val player = server.getPlayer(username).getOrNull()
        return player?.uniqueId?.toString()
    }

    fun saveConfig() {
        config?.save(dataDirectory.toString())
    }

    @Subscribe(order = PostOrder.EARLY)
    private fun onLogin(event: LoginEvent) {
        val player = event.player

        if (config != null) {
            // allow loopback ip and private network ip (like 192.168.x.x)
            if (config!!.allowLocalIp && checkLocalPlayer(player)) {
                logger.info("Allow local user: " + player.username)
                return
            }

            // 관리자인 경우
            if (config!!.allowUser.contains(player.uniqueId.toString())) {
                logger.info("Allow whitelisted user: " + player.username)
                return
            }
        }

        // 일반 유저
        var isSuccess = true
        var detailedKickMessage = ""

        try {
            val checkResult = playerChecker!!.checkPlayer(player.remoteAddress.hostName, player.uniqueId.toString())
            if (!checkResult.result) {
                isSuccess = false
                detailedKickMessage = checkResult.msg ?: "(no msg)"
                logger.info("Unauthorized player: ${player.username}, $detailedKickMessage")
            }
        } catch (e: Exception) {
            logger.warn("Exception on request: ${player.username}")
            logger.warn(e.toString())

            if (!config!!.passOnError) {
                isSuccess = false
                detailedKickMessage = e.message ?: e::class.simpleName ?: "exception"
            }
        }

        if (!isSuccess) {
            var msg = config!!.kickMessage
            if (config!!.useDetailKickMessage)
                msg += "\n" + detailedKickMessage

            val msgComponent = Component.text(msg)
            event.result = ResultedEvent.ComponentResult.denied(msgComponent)
        }
    }

    private fun checkLocalPlayer(player: Player): Boolean {
        val address = player.remoteAddress.address
        return address.isLoopbackAddress || address.isSiteLocalAddress
    }
}