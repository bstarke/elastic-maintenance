package net.starkenberg.logging.elastic;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Brad Starkenberg
 *
 */
@ConfigurationProperties(prefix="app.repo.log")
public class ConfigProps {

	private int days;
	private String host;
	private int port;
	private List<String> indexPrefixs = new ArrayList<String>();
	private String indexDateFormat;
	
	/**
	 * @return the days
	 */
	public int getDays() {
		return days;
	}
	/**
	 * @param days the days to set
	 */
	public void setDays(int days) {
		this.days = days;
	}
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	/**
	 * @return the indexPrefixs
	 */
	public List<String> getIndexPrefixs() {
		return indexPrefixs;
	}
	/**
	 * @param indexPrefixs the indexPrefixs to set
	 */
	public void setIndexPrefixs(List<String> indexPrefixs) {
		this.indexPrefixs = indexPrefixs;
	}
	/**
	 * @return the indexDateFormat
	 */
	public String getIndexDateFormat() {
		return indexDateFormat;
	}
	/**
	 * @param indexDateFormat the indexDateFormat to set
	 */
	public void setIndexDateFormat(String indexDateFormat) {
		this.indexDateFormat = indexDateFormat;
	}
}