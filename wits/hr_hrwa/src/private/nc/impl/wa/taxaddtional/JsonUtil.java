package nc.impl.wa.taxaddtional;


import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * @author changlei
 * json 数据的工具类
 *
 */
public class JsonUtil {
	
	
	public static String getJsonStringValue(JSONObject json,String key) throws JSONException {
		if(json==null|| json.get(key)==null){
			return "";
		}else{
			return (String)json.get(key);
		}
	}
	
	public static UFDateTime getJsonUFDateTimeValue(JSONObject json,String key) throws JSONException {
		if(json==null|| json.get(key)==null){
			return null;
		}else{
			return new UFDateTime((Long)json.get(key));
		}
	}
	
	public static UFDouble getJsonUFDoubleValue(JSONObject json,String key) throws JSONException {
		if(json==null|| json.get(key)==null ){
			return new UFDouble(0);
		}else if(json.get(key) instanceof Integer){
			return new UFDouble(json.getInt(key));
		}else{
			return new UFDouble((Double)json.get(key));
		}
	}
	
	public static UFBoolean getJsonUFBooleanValue(JSONObject json,String key) throws JSONException {
		if(json==null|| json.get(key)==null ){
			return new UFBoolean("N");
		}else{
			return new UFBoolean( (Character) json.get(key));
		}
	}
	
	public static Integer getJsonIntegerValue(JSONObject json,String key) throws JSONException {
		if(json==null|| json.get(key)==null ){
			return 0;
		}else{
			return new Integer( (Integer) json.get(key));
		}
	}
	
	public static UFLiteralDate getJsonUFLiteralDateValue(JSONObject json,String key) throws JSONException {
		if(json==null|| json.get(key)==null){
			return null;
		}else{
			return new UFLiteralDate((Long)json.get(key));
		}
	}
}
