package pw.alphabeta.authyouvelocity

import com.google.inject.Inject
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger

@Plugin(
    id = "AuthYouPluginVelocity",
    name = "AuthYouPluginVelocity",
    version = "0.1.0-SNAPSHOT",
    url = "https://github.com/AlphaBs/AuthYouPlugin",
    description = "AuthYou plugin for Velocity",
    authors = ["AlphaBs"]
)
class AuthYouPlugin @Inject constructor(private val server: ProxyServer, logger: Logger) {
    init {
        logger.info("Hello there! I made my first plugin with Velocity.")
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {

    }
}

