package net.starkenberg.logging.elastic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;

import net.starkenberg.logging.elastic.ElasticMaintenanceApplication;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ElasticMaintenanceApplication.class)
@WebAppConfiguration
public class ElasticMaintenanceApplicationTests {

	@Test
	public void contextLoads() {
	}

}
