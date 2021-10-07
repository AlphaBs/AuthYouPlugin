package pw.alphabeta.authyou.client

import pw.alphabeta.authyou.client.AuthYouPluginConfigLoader.Companion.load
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.lang.Exception
import java.lang.IllegalStateException

class AuthYouPlugin : JavaPlugin(), Listener {
    private var scheduler: BukkitScheduler? = null
    private var playerChecker: PlayerChecker? = null
    private var config: AuthYouPluginConfig? = null

    // config 파일 불러오기
    private fun loadConfig() {
        // host: 서버주소
        // passOnError: true/false 오류 발생시 접속 허용할지 여부
        // useDetailKickMessage: true/false 자세한 오류 메세지를 유저에게 알려줄지 여부
        // checkDelayTick: 100 접속 후 유저 확인까지 딜레이
        // kickMessage: 킥 메세지

        getConfig().addDefault("passOnError", false)
        getConfig().addDefault("useDetailKickMessage", false)
        getConfig().addDefault("checkDelayTick", 100)
        getConfig().addDefault("kickMessage", "[AuthYou] Unauthorized Player. Restart your client")
        getConfig().options().copyDefaults(true)
        saveConfig()
        config = load(getConfig())
    }

    override fun onLoad() {
        loadConfig()

        val host = getConfig().getString("host")
        logger.info("AuthYou Server: $host")

        scheduler = Bukkit.getScheduler()
        playerChecker = PlayerChecker(host)

        server.pluginManager.registerEvents(this, this)

        super.onLoad()
        logger.info("AuthYou Loaded")
    }

    override fun onDisable() {
        getConfig().options().copyDefaults(true)
        saveConfig()
        super.onDisable()
        logger.info("AuthYou Disabled")
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player

        // check player
        // 관리자인 경우 아래 메서드 호출 없이 메서드 종료

        checkAuthYouPlayer(player)
    }

    private fun checkAuthYouPlayer(player: Player) {
        if (scheduler == null || playerChecker == null || config == null )
            throw IllegalStateException()

        val ip = player.address.toString()
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
                    detailedKickMessage = checkResult.msg
                }
            } catch (e: Exception) {
                logger.warning("Failed to check player: $name")
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