package net.starkenberg.logging.elastic.schedule;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import net.starkenberg.logging.elastic.ConfigProps;

/**
 * @author Brad Starkenberg Scheduled methods for this application to execute
 *         Cron schedule is UTC time
 */
@Component
public class ScheduledMethods {

	@Autowired
	private RestTemplate restTemplate;
	
	private ConfigProps config;
	private DateTimeFormatter dateFormat;
	
	private static final Logger log = LoggerFactory.getLogger(ScheduledMethods.class);

	@Autowired
	public ScheduledMethods(ConfigProps config) {
		this.config = config;
		this.dateFormat = DateTimeFormatter.ofPattern(config.getIndexDateFormat());
		log.debug("Index Prefixes : " + config.getIndexPrefixs());
	}

	/**
	 * create the index for tomorrow so elasticsearch isn't creating the index
	 * while also trying to index documents into that index
	 */
	@Scheduled(cron = "${app.repo.log.createCronExpression}")
	public void createTomorrowsIndex() {
		String date = LocalDate.now().plusDays(1).format(dateFormat);
		for (String logIndexPrefix : config.getIndexPrefixs()) {
			log.info(String.format("Creating Index: %s%s", logIndexPrefix, date));
			restTemplate.postForLocation(String.format("http://%s:%s/%s%s", config.getHost(), config.getPort(), logIndexPrefix, date), null);
		}
	}

	/**
	 * delete old and unwanted indices
	 */
	@Scheduled(cron = "${app.repo.log.deleteCronExpression}")
	public void deleteIndices() {
		List<String> indices = getIndicesToDelete();
		for (String index : indices) {
			try {
				deleteAnIndex(index);
			} catch (RestClientException e) {
				log.warn(String.format("Couldn't delete Index %s : %s", index, e.getLocalizedMessage()), e);
			}
		}
		log.debug("Run Complete");
	}

	/**
	 * Delete the index passed in
	 * 
	 * @param indexToDelete
	 * @throws RestClientException
	 */
	private void deleteAnIndex(String indexToDelete) throws RestClientException {
		log.info(String.format("DELETE : http://%s:%s/%s", config.getHost(), config.getPort(), indexToDelete));
		restTemplate.delete(String.format("http://%s:%s/%s", config.getHost(), config.getPort(), indexToDelete));
	}

	/**
	 * @return List of all indices in the cluster
	 */
	private List<String> getAllIndices() {
		List<String> indexList = new ArrayList<String>();
		for (String logIndexPrefix : config.getIndexPrefixs()) {

			String url = String.format("http://%s:%s/_cat/indices/%s*?h=index", config.getHost(), config.getPort(), logIndexPrefix);
			String indexString = "";
			try {
				indexString = restTemplate.getForObject(url, String.class);
			} catch (HttpClientErrorException e) {
				log.error(String.format("Error fetching indices from %s : %s", url, e.getLocalizedMessage()), e);
			}
			if(StringUtils.isNotEmpty(indexString)){
				indexList.addAll(Arrays.asList(indexString.replaceAll(" ", "").split("\n")));
			}
		}
		log.debug("IndexList:" + indexList);
		return indexList;
	}

	/**
	 * @return list of indices that we don't want in the cluster
	 */
	private List<String> getIndicesToDelete() {
		List<String> indices = getAllIndices();
		// remove tomorrows index if it has been created early
		for (String logIndexPrefix : config.getIndexPrefixs()) {
			indices.remove(String.format("%s%s", logIndexPrefix, LocalDate.now().plusDays(1).format(dateFormat)));
		}
		// remove the indices to keep
		for (String logIndexPrefix : config.getIndexPrefixs()) {
			for (int i = 0; i < config.getDays(); i++) {
				indices.remove(String.format("%s%s", logIndexPrefix, LocalDate.now().minusDays(i).format(dateFormat)));
			}
		}
		log.debug("Indices to delete : " + indices);
		return indices;
	}

}
