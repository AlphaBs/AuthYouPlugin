package pw.alphabeta.authyouvelocity

import java.util.regex.Pattern

class Util {
    companion object {
        private val PATTERN_UUID = Pattern.compile(
            "^\\{?[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\\}?\$")

        fun isValidUUID(uuid: String): Boolean {
            return PATTERN_UUID.matcher(uuid).matches()
        }
    }
}