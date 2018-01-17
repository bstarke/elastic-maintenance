package net.starkenberg.logging.elastic.schedule

import net.starkenberg.logging.elastic.ConfigProps
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * @author Brad Starkenberg Scheduled methods for this application to execute
 * Cron schedule is UTC time
 */
@Component
class ScheduledMethods(private val config: ConfigProps, private val restTemplate: RestTemplate) {
    private val dateFormat = DateTimeFormatter.ofPattern(config.indexDateFormat!!)

    /**
     * @return List of all indices in the cluster
     */
    private val allIndices: MutableList<String>
        get() {
            val indexList = ArrayList<String>()
            for (logIndexPrefix in config.indexPrefixs) {

                val url = String.format("http://%s:%s/_cat/indices/%s*?h=index", config.host, config.port, logIndexPrefix)
                var indexString = ""
                try {
                    indexString = restTemplate.getForObject(url, String::class.java)
                } catch (e: HttpClientErrorException) {
                    log.error(String.format("Error fetching indices from %s : %s", url, e.localizedMessage), e)
                }

                if (StringUtils.isNotEmpty(indexString)) {
                    indexList.addAll(Arrays.asList(*indexString.replace(" ".toRegex(), "").split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
                }
            }
            log.debug("IndexList:" + indexList)
            return indexList
        }

    /**
     * @return list of indices that we don't want in the cluster
     */
    // remove tomorrows index if it has been created early
    // remove the indices to keep
    val indicesToDelete: List<String>
        get() {
            val indices = allIndices
            for (logIndexPrefix in config.indexPrefixs) {
                indices.remove(String.format("%s%s", logIndexPrefix, LocalDate.now().plusDays(1).format(dateFormat)))
            }
            for (logIndexPrefix in config.indexPrefixs) {
                for (i in 0 until config.days) {
                    indices.remove(String.format("%s%s", logIndexPrefix, LocalDate.now().minusDays(i.toLong()).format(dateFormat)))
                }
            }
            log.debug("Indices to delete : " + indices)
            return indices
        }

    /**
     * create the index for tomorrow so elasticsearch isn't creating the index
     * while also trying to index documents into that index
     */
    @Scheduled(cron = "\${app.repo.log.createCronExpression}")
    fun createTomorrowsIndex() {
        val date = LocalDate.now().plusDays(1).format(dateFormat)
        for (logIndexPrefix in config.indexPrefixs) {
            log.info(String.format("Creating Index: %s%s", logIndexPrefix, date))
            restTemplate.postForLocation(String.format("http://%s:%s/%s%s", config.host, config.port, logIndexPrefix, date), null)
        }
    }

    /**
     * delete old and unwanted indices
     */
    @Scheduled(cron = "\${app.repo.log.deleteCronExpression}")
    fun deleteIndices() {
        val indices = indicesToDelete
        for (index in indices) {
            try {
                deleteAnIndex(index)
            } catch (e: RestClientException) {
                log.warn(String.format("Couldn't delete Index %s : %s", index, e.localizedMessage), e)
            }

        }
        log.debug("Run Complete")
    }

    /**
     * Delete the index passed in
     *
     * @param indexToDelete
     * @throws RestClientException
     */
    @Throws(RestClientException::class)
    fun deleteAnIndex(indexToDelete: String) {
        log.info(String.format("DELETE : http://%s:%s/%s", config.host, config.port, indexToDelete))
        restTemplate.delete(String.format("http://%s:%s/%s", config.host, config.port, indexToDelete))
    }

    companion object {

        private val log = LoggerFactory.getLogger(ScheduledMethods::class.java)
    }

}
