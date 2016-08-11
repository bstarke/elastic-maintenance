package net.starkenberg.logging.elastic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * @author Brad Starkenberg
 * main application class
 */
@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(ConfigProps.class)
public class ElasticMaintenanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticMaintenanceApplication.class, args);
    }
    
    @Bean(name="restTemplate")
    public RestTemplate restTemplate() {
    	return new RestTemplate();
    }
    
}
