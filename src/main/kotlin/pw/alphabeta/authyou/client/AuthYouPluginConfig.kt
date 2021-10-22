package pw.alphabeta.authyou.client

import org.bukkit.configuration.file.FileConfiguration

data class AuthYouPluginConfig(
    val host: String,
    val serverId: String,
    val requestTimeout: Int,
    val allowLocalIp: Boolean,
    val passOnError: Boolean,
    val useDetailKickMessage: Boolean,
    val checkDelayTick: Long,
    val kickMessage: String,
    val allowUser: ArrayList<String>
) {
    companion object {
        fun load(config: FileConfiguration): AuthYouPluginConfig{

            // host: 서버주소
            // serverId: 서버아이디
            // requestTimeout: 요청 타임아웃 (ms)
            // allowInternalIp: 내부 IP, 루프백 IP 허용할 지
            // passOnError: true/false 오류 발생시 접속 허용할지 여부
            // useDetailKickMessage: true/false 자세한 오류 메세지를 유저에게 알려줄지 여부
            // checkDelayTick: 100 접속 후 유저 확인까지 딜레이
            // kickMessage: 킥 메세지

            config.addDefault("host", "")
            config.addDefault("serverId", "")
            config.addDefault("requestTimeout", 10000)
            config.addDefault("allowLocalIp", false)
            config.addDefault("passOnError", false)
            config.addDefault("useDetailKickMessage", false)
            config.addDefault("checkDelayTick", 100)
            config.addDefault("kickMessage", "[AuthYou] Unauthorized Player. Restart your client")
            config.addDefault("allowUser", ArrayList<String>())
            config.options().copyDefaults(true)

            return AuthYouPluginConfig(
                host = config.getString("host"),
                serverId = config.getString("serverId"),
                requestTimeout = config.getInt("requestTimeout"),
                allowLocalIp = config.getBoolean("allowLocalIp"),
                passOnError = config.getBoolean("passOnError"),
                useDetailKickMessage = config.getBoolean("useDetailKickMessage"),
                checkDelayTick = config.getLong("checkDelayTick"),
                kickMessage = config.getString("kickMessage"),
                allowUser = config.getStringList("allowUser") as ArrayList<String>
            )
        }
    }
}