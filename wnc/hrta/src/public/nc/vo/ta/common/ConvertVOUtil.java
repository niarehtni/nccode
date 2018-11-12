package nc.vo.ta.common;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;
/**
 * 用于类型转换的辅助类
 * @author ward
 * @date 20180514
 *
 */
public class ConvertVOUtil {
	
	public static <T> Map<String, String> getVOFieldType(Class<T> classzz) {
		Map<String, String> fieldMap = new HashMap<String, String>();
		Field[] fields = classzz.getDeclaredFields();
		for (Field f : fields) {
			f.setAccessible(true);
			fieldMap.put(f.getName(), f.getType().toString());
		}
		return fieldMap;
	}

	/**
	 * @Description: 根据字段类型设置vo字段值.
	 */
	@SuppressWarnings("deprecation")
	public static void setVoFieldValueByType(SuperVO vo, String fieldType, String filedName, String fieldValue) {
		if (StringUtils.isNotBlank(fieldValue)) {
			if (fieldType.endsWith("String")) {
				vo.setAttributeValue(filedName, fieldValue);
			} else if (fieldType.endsWith("UFDateTime")) {
				Date date = getDateFormatStr(fieldValue);
				UFDateTime dateTime = null;
				if (null != date) {
					dateTime = new UFDateTime(date);
				} else {
					dateTime = new UFDateTime(fieldValue);
				}
				vo.setAttributeValue(filedName, dateTime);
			} else if (fieldType.endsWith("UFLiteralDate")) {
				Date date = getDateFormatStr(fieldValue);
				UFLiteralDate dateTime = null;
				if (null != date) {
					dateTime = new UFLiteralDate(date);
				} else {
					dateTime = new UFLiteralDate(fieldValue);
				}
				vo.setAttributeValue(filedName, dateTime);
			} else if (fieldType.endsWith("UFDouble")) {
				UFDouble ufdoublevalue = new UFDouble(fieldValue);
				ufdoublevalue = ufdoublevalue.setScale(-2, UFDouble.ROUND_HALF_UP);
				vo.setAttributeValue(filedName, ufdoublevalue);
			} else if (fieldType.endsWith("UFDate")) {
				Date date = getDateFormatStr(fieldValue);
				UFDate ufDate = null;
				if (null != date) {
					ufDate = new UFDate(date);
				} else {
					ufDate = new UFDate(fieldValue);
				}
				vo.setAttributeValue(filedName, ufDate);
			} else if (fieldType.endsWith("UFBoolean")) {
				vo.setAttributeValue(filedName, new UFBoolean(fieldValue));
			} else if (fieldType.endsWith("int") || fieldType.endsWith("Integer")) {
				vo.setAttributeValue(filedName, new Integer(fieldValue));
			} else if (fieldType.endsWith("double") || fieldType.endsWith("Double")) {
				vo.setAttributeValue(filedName, Math.round(new Double(fieldValue)));
			} else if (fieldType.endsWith("byte") || fieldType.endsWith("Byte")) {
				vo.setAttributeValue(filedName, new Byte(fieldValue));
			} else if (fieldType.endsWith("short") || fieldType.endsWith("Short")) {
				vo.setAttributeValue(filedName, new Short(fieldValue));
			} else if (fieldType.endsWith("long") || fieldType.endsWith("Long")) {
				vo.setAttributeValue(filedName, new Long(fieldValue));
			} else if (fieldType.endsWith("float") || fieldType.endsWith("Float")) {
				vo.setAttributeValue(filedName, new Float(fieldValue));
			} else if (fieldType.endsWith("boolean") || fieldType.endsWith("Boolean")) {
				vo.setAttributeValue(filedName, new Boolean(fieldValue));
			} else if (fieldType.endsWith("char") || fieldType.endsWith("Char")) {
				vo.setAttributeValue(filedName, fieldValue.charAt(0));
			} else if (fieldType.endsWith("Date")) {
				Date date = getDateFormatStr(fieldValue);
				if (date == null) {
					date = new Date(fieldValue);
				}
				vo.setAttributeValue(filedName, date);
			}
		}
	}

	public static Date getDateFormatStr(String dateStr, SimpleDateFormat[] sdfs) {
		Date date = null;
		if (StringUtils.isNotBlank(dateStr)) {
			boolean falg = true;
			for (SimpleDateFormat sdf : sdfs) {
				falg = true;
				try {
					date = sdf.parse(dateStr);
				} catch (ParseException e) {
					falg = false;
				}
				if (falg) {
					return date;
				}
			}
		}

		return date;
	}

	public static Date getDateFormatStr(String dateStr) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf4 = new SimpleDateFormat("yyyyMMddHHmmss");

		List<SimpleDateFormat> sdflist = new ArrayList<SimpleDateFormat>();
		sdflist.add(sdf1);
		sdflist.add(sdf2);
		sdflist.add(sdf3);
		sdflist.add(sdf4);
		return getDateFormatStr(dateStr, sdflist.toArray(new SimpleDateFormat[0]));
	}

	public static String getStringValue(Object obj) {
		return (null == obj ? "" : obj.toString());
	}
}
