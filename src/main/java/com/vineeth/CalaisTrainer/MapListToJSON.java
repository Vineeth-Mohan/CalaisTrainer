package com.vineeth.CalaisTrainer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapListToJSON {

	private static Object getJSONObject(Object obj) throws JSONException {
		if (obj instanceof Map) {
			JSONObject _map = new JSONObject();
			Map<String, Object> _objMap = (Map<String, Object>) obj;
			for (Entry<String, Object> entity : _objMap.entrySet()) {
				_map.put(entity.getKey(), getJSONObject(entity.getValue()));
			}
			return _map;
		} else if (obj instanceof List) {
			JSONArray _list = new JSONArray();
			List<Object> _objList = (List<Object>) obj;
			for (Object entity : _objList) {
				_list.put(getJSONObject(entity));
			}
			return _list;
		} else {
			return obj;
		}
	}

	public static JSONObject getJSON(Object obj) throws JSONException {
		return (JSONObject) getJSONObject(obj);
	}

	public static void main(String args[]) throws JSONException {
		Map<String, Object> _map = new HashMap<String, Object>();
		List<String> _list = Arrays.asList("one", "two");
		_map.put("vin", "mohan");
		_map.put("num", _list);
		MapListToJSON converter = new MapListToJSON();
		System.out.println(converter.getJSON(_map));
	}

}
