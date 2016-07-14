package net.starkenberg.logging.elastic.schedule;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @author Brad Starkenberg
 * Scheduled methods for this application to execute
 * Cron schedule is UTC time
 */
@Component
public class ScheduledMethods {

	@Autowired
	private RestTemplate restTemplate;
	@Value("${app.repo.log.days}")
	private Integer logDays;
	@Value("${app.repo.log.index.prefix}")
	private String logIndexPrefix;
	@Value("${app.repo.log.index.date.format}")
	private SimpleDateFormat dateFormat;
	@Value("${app.repo.log.host}")
	private String host;
	@Value("${app.repo.log.port}")
	private Integer port;
	private static final Logger log = LoggerFactory.getLogger(ScheduledMethods.class);

	/**
	 * create the index for tomorrow so elasticsearch isn't creating the 
	 * index while also trying to index documents into that index
	 */
	@Scheduled(cron = "30 21 19 * * *")
	public void createTomorrowsIndex() {
		String date = dateFormat.format(LocalDate.now().plusDays(1));
		log.info(String.format("Creating Index: %s%s", logIndexPrefix, date));
		restTemplate.postForLocation(String.format("http://%s:%s/%s%s", host, port, logIndexPrefix, date), null);
	}

	/**
	 * delete old and unwanted indices
	 */
	@Scheduled(cron = "0 */3 * * * *")
	public void deleteIndices() {
		List<String> indices = getIndicesToDelete();
		for (String index : indices) {
			try {
				deleteAnIndex(index);
			} catch (RestClientException e) {
				log.warn(String.format("Couldn't delete Index %s : %s", index, e.getLocalizedMessage()),e);
			}
		}
	}

	/**
	 * Delete the index passed in
	 * @param indexToDelete
	 * @throws RestClientException
	 */
	private void deleteAnIndex(String indexToDelete) throws RestClientException {
		restTemplate.delete(String.format("http://%s:%s/%s", host, port, indexToDelete));
	}
	
	/**
	 * @return List of all indices in the cluster
	 */
	private List<String> getAllIndices() {
		String url = String.format("http://%s:%s/_cat/indices/%s*?h=index", host, port, logIndexPrefix);
		String indexString = "";
		try {
			indexString = restTemplate.getForObject(url, String.class);
		} catch (RestClientException e) {
			log.error(String.format("Error fetching indices from %s : %s", url, e.getLocalizedMessage()), e);
		}
		String[] indexArray = indexString.replaceAll(" ", "").split("\n");
		return new ArrayList<String>(Arrays.asList(indexArray));
	}

	/**
	 * @return list of indices that we don't want in the cluster
	 */
	private List<String> getIndicesToDelete() {
		List<String> indices = getAllIndices();
		//remove tomorrows index if it has been created early
		indices.remove(String.format("%s%s", logIndexPrefix, dateFormat.format(LocalDate.now().plusDays(1))));
		//remove the indices to keep
		for (int i = 0; i < logDays; i++) {
			indices.remove(String.format("%s%s", logIndexPrefix, dateFormat.format(LocalDate.now().minusDays(i))));
		}
		return indices;
	}

}
