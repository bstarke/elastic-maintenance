package net.starkenberg.logging.elastic

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * @author Brad Starkenberg
 * main application class
 */
@EnableAutoConfiguration
@ComponentScan
@EnableScheduling
@EnableConfigurationProperties(ConfigProps::class)
class ElasticMaintenanceApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(ElasticMaintenanceApplication::class.java, *args)
        }
    }

}
