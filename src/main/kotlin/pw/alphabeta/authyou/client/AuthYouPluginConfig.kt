package pw.alphabeta.authyou.client

import org.bukkit.configuration.file.FileConfiguration

data class AuthYouPluginConfig(
    val host: String,
    val passOnError: Boolean,
    val useDetailKickMessage: Boolean,
    val checkDelayTick: Long,
    val kickMessage: String
)

class AuthYouPluginConfigLoader {
    companion object {
        fun load(config: FileConfiguration): AuthYouPluginConfig = AuthYouPluginConfig(
            host = config.getString("host"),
            passOnError = config.getBoolean("passOnError"),
            useDetailKickMessage = config.getBoolean("useDetailKickMessage"),
            checkDelayTick = config.getLong("checkDelayTick"),
            kickMessage = config.getString("kickMessage")
        )
    }
}