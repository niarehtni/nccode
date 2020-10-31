package nc.itf.hrpub.util;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.math.NumberUtils;

public class MapKeyComparator<T> implements Comparator<String> {
	@Override
	public int compare(String str1, String str2) {
		if (NumberUtils.isDigits(str1) && (NumberUtils.isDigits(str2))) {
			return NumberUtils.toInt(str1) - NumberUtils.toInt(str2);
		} else {
			return str1.compareTo(str2);
		}
	}

	/**
	 * 使用 Map按key进行排序
	 * 
	 * @param unSortedMap
	 * @return
	 */
	public Map<String, T> sortMapByKey(Map<String, T> unSortedMap) {
		if (unSortedMap == null || unSortedMap.isEmpty()) {
			return null;
		}

		Map<String, T> sortMap = new TreeMap<String, T>(new MapKeyComparator());

		sortMap.putAll(unSortedMap);

		return sortMap;
	}
}
