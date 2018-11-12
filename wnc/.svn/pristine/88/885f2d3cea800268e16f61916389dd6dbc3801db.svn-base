package nc.itf.hrpub.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonUtil {

	private static List<Map<String, Object>> json2List(Object json) {
		JSONArray jsonArr = (JSONArray) json;
		List<Map<String, Object>> arrList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < jsonArr.size(); ++i) {
			arrList.add(json2Map(jsonArr.getString(i)));
		}
		return arrList;
	}

	public static Map<String, Object> json2Map(String json) {
		JSONObject jsonObject = JSONObject.parseObject(json);
		Map<String, Object> resMap = new HashMap<String, Object>();
		Iterator<Entry<String, Object>> it = jsonObject.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> param = (Map.Entry<String, Object>) it
					.next();
			if (param.getValue() instanceof JSONObject) {
				resMap.put(param.getKey(),
						json2Map(param.getValue().toString()));
			} else if (param.getValue() instanceof JSONArray) {
				resMap.put(param.getKey(), json2List(param.getValue()));
			} else {
				String value = JSONObject.toJSONString(param.getValue(),
						SerializerFeature.WriteClassName).trim();
				if (value.substring(0, 1).equals("\"")
						&& value.substring(value.length() - 1, value.length())
								.equals("\"")) {
					value = value.substring(1, value.length() - 1);
				}
				resMap.put(param.getKey(), value);
			}
		}
		return resMap;
	}

	public static String map2JSON(List<Map<String, Object>> listMapObject) {
		StringBuffer retString = new StringBuffer();

		if (listMapObject != null && listMapObject.size() > 0) {
			retString.append("{");
			for (Map<String, Object> vo : listMapObject) { // ÖðÐÐ
				if (vo != null && vo.size() > 0) {
					for (Map.Entry<String, Object> prop : vo.entrySet()) {
						retString.append("\"" + prop.getKey() + "\":");
						if (prop.getValue() instanceof List) {
							int rowno = 0;
							retString.append("[");
							for (Object objOfList : (List) prop.getValue()) {
								rowno++;
								List<Map<String, Object>> innerMaps = new ArrayList<Map<String, Object>>();
								innerMaps.add((Map<String, Object>) objOfList);
								String innerJson = map2JSON(innerMaps);
								if (!innerJson.equals("{}")) {
									retString.append(innerJson);
									if (rowno != ((List) prop.getValue())
											.size()) {
										retString.append(",");
									}
								}
							}
							if (retString.lastIndexOf(",") == retString
									.length() - 1) {
								retString.replace(retString.lastIndexOf(","),
										retString.lastIndexOf(",") + 1, "");
							}
							retString.append("]");
						} else {
							if (prop.getValue() == null) {
								retString.append("null");
							} else {
								retString.append("\""
										+ String.valueOf(prop.getValue())
										+ "\"");
							}
						}
						retString.append(",");
					}
				}
			}

			if (retString.length() == 1) {
				retString.append("}");
			} else {
				retString.replace(retString.lastIndexOf(","),
						retString.lastIndexOf(",") + 1, "}");
			}
		}
		return retString.toString();
	}
}