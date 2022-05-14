package pw.alphabeta.authyou.plugin

import pw.alphabeta.authyou.client.AuthYouPluginConfig

interface IBukkitPlugin {
    fun logInformation(msg: String)
    fun logError(msg: String)

    fun loadConfig()
    fun saveConfig()

    fun onCommand()
    fun onPlayerJoin()
}