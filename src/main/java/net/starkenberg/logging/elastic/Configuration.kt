package net.starkenberg.logging.elastic

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
open class Configuration {

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}
