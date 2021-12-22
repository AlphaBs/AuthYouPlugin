package pw.alphabeta.authyou.client

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

data class AuthYouPluginConfig(
    val host                : String           = "",       // 서버주소
    val serverId            : String           = "",       // 서버아이디
    val requestTimeout      : Int              = 10000,    // 요청 타임아웃 (ms)
    val allowLocalIp        : Boolean          = false,    // 내부 IP, 루프백 IP 허용할 지
    val passOnError         : Boolean          = false,    // 오류 발생 시 접속 허용할 지
    val useDetailKickMessage: Boolean          = false,    // 자세한 오류 메세지 표시 여부
    val checkDelayTick      : Long             = 100,      // (기본값 100) 접속 후 유저 확인까지 딜레이
    val kickMessage         : String           = "",       // 킥 메세지
    val allowUser           : HashSet<String>  = HashSet() // 확인 없이 무조건 허용할 유저의 UUID 목록
) {
    companion object {
        val gson: Gson = GsonBuilder()
            .setPrettyPrinting()
            .create()

        fun load(plugin: JavaPlugin): AuthYouPluginConfig {
            val file = File(plugin.dataFolder.toString() + "/config.json")
            return if (file.exists()) {
                val content: String = file.readText()
                gson.fromJson<AuthYouPluginConfig>(content, AuthYouPluginConfig::class.java)
            } else {
                val empty = AuthYouPluginConfig()
                empty.save(plugin)
                empty
            }
        }
    }

    fun save(plugin: JavaPlugin) {
        val file = File(plugin.dataFolder.toString() + "/config.json")
        file.parentFile.mkdirs()
        val content: String = gson.toJson(this)
        file.writeText(content)
    }
}