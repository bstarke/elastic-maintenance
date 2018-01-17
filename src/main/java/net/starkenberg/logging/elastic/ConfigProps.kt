package net.starkenberg.logging.elastic

import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.*

/**
 * @author Brad Starkenberg
 */
@ConfigurationProperties(prefix = "app.repo.log")
class ConfigProps {
    var days: Int = 0
    var host: String? = null
    var port: Int = 0
    var indexPrefixs: List<String> = ArrayList()
    var indexDateFormat: String? = null
}