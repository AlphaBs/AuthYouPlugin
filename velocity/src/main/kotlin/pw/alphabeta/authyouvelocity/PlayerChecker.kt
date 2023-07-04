package pw.alphabeta.authyouvelocity

import com.google.gson.Gson
import pw.alphabeta.authyouvelocity.PlayerCheckResult
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


class PlayerChecker(
    private val host: String,
    private val serverId: String) {

    companion object {
        const val ProtocolVersion: String = "a1"
    }

    var timeout: Int = 10000
    
    fun checkPlayer(ip: String, uuid: String): PlayerCheckResult {
        val url = "$host/$serverId/auth/check?ver=$ProtocolVersion"
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
            doOutput = true
            readTimeout = timeout
            connectTimeout = timeout
            setRequestProperty("Content-Type", "application/json")
            
            val wr = OutputStreamWriter(outputStream)
            wr.write(body)
            wr.flush()

            var responseString: String?
            try {
                responseString = readInputStream(inputStream)
            } catch (e: IOException) {
                // This means that an error occurred, read the error from the ErrorStream
                try {
                    responseString = readInputStream(errorStream)
                } catch (e1: IOException) {
                    throw IllegalStateException("Unable to read error body.", e)
                }
            }
            
            return responseString ?: ""
        }
    }
    
    private fun readInputStream(inputStream: InputStream): String {
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