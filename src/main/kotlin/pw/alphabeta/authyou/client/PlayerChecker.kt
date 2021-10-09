package pw.alphabeta.authyou.client

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class PlayerChecker(
    private val host: String,
    private val serverId: String) {

    companion object {
        const val ProtocolVersion: String = "a1"
    }

    fun checkPlayer(ip: String, uuid: String): PlayerCheckResult {
        val url = "$host/auth/$serverId/check?ver=$ProtocolVersion"
        val body = Gson().toJson(
            AuthYouPlayerData(
                ip = ip,
                uuid = uuid
            )
        )

        val res = post(url, body)
        return Gson().fromJson(res, PlayerCheckResult::class.java)
    }

    // http post request
    private fun post(urlStr: String, body: String): String {
        val url = URL(urlStr)
        with (url.openConnection() as HttpURLConnection) {
            requestMethod = "POST"

            val wr = OutputStreamWriter(outputStream)
            wr.write(body)
            wr.flush()

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }

                return response.toString()
            }
        }
    }
}