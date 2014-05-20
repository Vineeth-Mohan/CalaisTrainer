package com.vineeth.CalaisTrainer;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import mx.bigdata.jcalais.CalaisClient;
import mx.bigdata.jcalais.CalaisConfig;
import mx.bigdata.jcalais.CalaisObject;
import mx.bigdata.jcalais.CalaisResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CalaisExtractor implements Runnable {

	private String content;
	private String docId;
	private CalaisClient calaisClient;
	private CalaisConfig config;
	private static Logger logger = Logger.getLogger(CalaisExtractor.class);
	private Client esClient;
	private static String index = "algotree";
	private static String type = "news";

	public CalaisExtractor(String content, String docId,
			CalaisClient calaisClient, CalaisConfig config, Client esClient) {
		super();
		this.content = content;
		this.docId = docId;
		this.calaisClient = calaisClient;
		this.config = config;
		this.esClient = esClient;
	}

	public void run() {
		try {
			content = strippedHtmlTags(content);
			CalaisResponse calaisResponse = null;
			long diff = 0;
			try {
				Date start = new Date();
				calaisResponse = calaisClient.analyze(content, config);
				Date end = new Date();
				diff = end.getTime() - start.getTime();
				diff /= 1000;
			} catch (Exception e) {
				logger.warn("Exception at Calais");
				logger.warn("Content is " + content);
				logger.warn(BaseHelper.stackTraceToString(e));
				markFeedasBad(docId, diff);
				// Thread.sleep(1000 * 2);
				return;
			}
			if (calaisResponse == null) {
				return;
			}
			JSONObject calaisTags = getObject(calaisResponse);
			updateFeed(docId, calaisTags, diff);
		} catch (Exception e) {
			logger.warn("Exception globally");
			logger.warn("Content is " + content);
			logger.warn(BaseHelper.stackTraceToString(e));
		}
	}

	private JSONObject getObject(CalaisResponse calaisResponse)
			throws JSONException {
		JSONObject json = new JSONObject();
		json.put("Entities", getElements(calaisResponse.getEntities()));
		json.put("Events", getElements(calaisResponse.getRelations()));
		json.put("SocialTags", getElements(calaisResponse.getSocialTags()));
		json.put("Topics", getElements(calaisResponse.getTopics()));
		JSONObject root = new JSONObject();
		root.put("CalaisMeta", json);
		return root;
	}

	private JSONArray getElements(Iterable<CalaisObject> elements)
			throws JSONException {
		JSONArray array = new JSONArray();
		for (CalaisObject entity : elements) {
			JSONObject element = new JSONObject();
			for (String field : entity.getFieldNames()) {
				if (entity.getList(field) != null) {
					JSONArray instances = new JSONArray();
					for (Object s : entity.getList(field)) {
						if (s instanceof String) {
							instances.put(s);
						} else {
							instances.put(MapListToJSON.getJSON(s));
						}
					}
					element.put(field, instances);
				} else {
					element.put(field, entity.getField(field));
				}
			}
			array.put(element);
		}
		return array;
	}

	private void updateFeed(String id, JSONObject calaisTags, long diff)
			throws InterruptedException, ExecutionException, JSONException {
		logger.info("Updating Calais tags  for " + id + " processTime=" + diff);
		calaisTags.put("isCalaisProcessed", true);
		UpdateRequest updateRequest = new UpdateRequest(index, type, id);
		updateRequest = updateRequest.doc(calaisTags.toString());
		ActionFuture<UpdateResponse> response = esClient.update(updateRequest);
		// response.get();
	}

	private void markFeedasBad(String id, long diff)
			throws InterruptedException, ExecutionException, JSONException {
		logger.info("Marking as bad " + id + " processTime=" + diff);
		JSONObject calaisTags = new JSONObject();
		calaisTags.put("isCalaisProcessed", "bad");
		UpdateRequest updateRequest = new UpdateRequest(index, type, id);
		updateRequest = updateRequest.doc(calaisTags.toString());
		ActionFuture<UpdateResponse> response = esClient.update(updateRequest);
		// response.get();
	}

	public String strippedHtmlTags(String linkText) {
		String regexHtml = "\\<.*?\\>";
		if (linkText != null && !linkText.isEmpty()) {
			linkText = linkText.replaceAll(regexHtml, "");
		}
		return StringEscapeUtils.unescapeHtml4(linkText);
	}

}
