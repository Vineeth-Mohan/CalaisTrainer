package com.vineeth.CalaisTrainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mx.bigdata.jcalais.CalaisClient;
import mx.bigdata.jcalais.CalaisConfig;
import mx.bigdata.jcalais.CalaisConfig.ConnParam;
import mx.bigdata.jcalais.rest.CalaisRestClient;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

public class CalaisDataExtractor {
	private static String index = "algotree";
	private static String type = "news";
	private static String calaisToken = "7vgy3s9bzfhpzy73v679enjn";
	private static String esHost = "localhost";
	private static Integer esPort = 9300;
	private static Integer timeOut = 5000 * 100;
	private static String cluster = "dataware";
	private static String contentField = "Content";
	private static final Integer CALAIS_UPPER_LIMIT = 50000;
	private static final String MAP_FILE = "map.ser";

	private static Client esClient;

	private static Logger logger = Logger.getLogger(CalaisDataExtractor.class);
	private static Map<String, Integer> countSoFar;

	public static void main(String[] args) throws Exception {
		CalaisClient calaisClient = new CalaisRestClient(calaisToken);
		CalaisConfig config = new CalaisConfig();
		config.set(ConnParam.READ_TIMEOUT, timeOut);
		config.set(ConnParam.CONNECT_TIMEOUT, timeOut);

		Settings s = ImmutableSettings.settingsBuilder()
				.put("client.transport.sniff", true)
				.put("cluster.name", cluster)
				.put("discovery.zen.ping.multicast.enabled", false)
				.put("discovery.zen.ping.unicast.enabled", true).build();

		esClient = new TransportClient(s)
				.addTransportAddress(new InetSocketTransportAddress(esHost,
						esPort));
		Boolean isUpperLimit = readProgress();

		if (isUpperLimit) {
			logger.error("Todays  limit reached , so exiting");
			System.exit(0);
		}

		ExecutorService executor = Executors.newFixedThreadPool(2);

		SearchResponse scrollResp = esClient
				.prepareSearch(index)
				.addField("Content")
				.setSearchType(SearchType.SCAN)
				.setQuery(
						QueryBuilders
								.queryString("_missing_:isCalaisProcessed"))
				.setScroll(new TimeValue(60000)).setSize(100).execute()
				.actionGet();
		scrollResp = esClient.prepareSearchScroll(scrollResp.getScrollId())
				.setScroll(new TimeValue(600000)).execute().actionGet();
		int count = 0;
		logger.info(" Started " + scrollResp.getHits().getTotalHits());
		while (true) {
			if (scrollResp.getHits().hits().length == 0) {
				break;
			}
			for (SearchHit hit : scrollResp.getHits()) {
				count++;
				String docId = hit.getId();
				logger.info("Processing " + docId + "@" + count);
				String content = hit.field(contentField).getValue();
				if (content == null || content.isEmpty()) {
					continue;
				}
				CalaisExtractor worker = new CalaisExtractor(content, docId,
						calaisClient, config, esClient);
				executor.execute(worker);
				if (updateProgress()) {
					logger.error("Todays  limit reached , so exiting");
					System.exit(0);
				}
			}

			logger.info("Fetching next batch of feeds");
			scrollResp = esClient.prepareSearchScroll(scrollResp.getScrollId())
					.setScroll(new TimeValue(600000)).execute().actionGet();
		}

	}

	private static Boolean readProgress() throws Exception {
		if (!new File(MAP_FILE).exists()) {
			countSoFar = new HashMap<String, Integer>();
			return false;
		}
		FileInputStream fis = new FileInputStream(MAP_FILE);
		ObjectInputStream ois = new ObjectInputStream(fis);
		countSoFar = (Map<String, Integer>) ois.readObject();
		ois.close();
		String nowDate = getDate();
		Integer count = countSoFar.get(nowDate);
		if (count == null) {
			count = 0;
		}
		count = countSoFar.get(nowDate);
		if (count >= CALAIS_UPPER_LIMIT) {
			return true;
		}
		return false;
	}

	private static boolean updateProgress() throws IOException {

		String nowDate = getDate();
		Integer count = countSoFar.get(nowDate);
		if (count == null) {
			count = 0;
		}
		count++;
		countSoFar.put(nowDate, count);
		FileOutputStream fos = new FileOutputStream(MAP_FILE);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(countSoFar);
		oos.close();
		if (count >= CALAIS_UPPER_LIMIT) {
			return true;
		}
		return false;
	}

	private static String getDate() {
		Date now = new Date();
		String nowDate = " " + now.getYear() + "-" + now.getMonth() + "-"
				+ now.getDay();
		return nowDate;
	}

}
