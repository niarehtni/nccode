package nc.hr.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.logging.Logger;
import nc.jdbc.framework.SQLParameter;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.uif2.LoginContext;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * 一些通用工具
 * 
 * @author wangxing
 */
public class CommonUtils {
	/**
	 * 将一个map的value进行cast操作 例如把Map<String,String[]>
	 * cast为Map<String,Object[]>，因为java不允许直接进行这样的强制类型转换转换，因此需要专门写一个转换方法
	 * 
	 * @param <K>
	 * @param <V>
	 * @param <T>
	 * @param map
	 * @return
	 */
	public static <K, V, T extends V> Map<K, V[]> castMap(Map<K, T[]> map) {
		if (MapUtils.isEmpty(map)) {
			return null;
		}
		Map<K, V[]> retMap = new HashMap<K, V[]>();
		for (K key : map.keySet()) {
			retMap.put(key, map.get(key));
		}
		return retMap;
	}

	/***************************************************************************
	 * 检查是否选择了主组织<br>
	 * Created on 2011-4-6 9:26:16<br>
	 * 
	 * @param context
	 * @throws BusinessException
	 * @author Rocex Wang
	 ***************************************************************************/
	public static void checkOrg(LoginContext context) throws BusinessException {
		if (context.getPk_org() == null) {
			throw new BusinessException(ResHelper.getString("6001pub", "06001pub0013")
			/* @res "请先选择组织!" */);
		}
	}

	/**
	 * 判断由beginDate1和endDate1组成的日期段是否包含由beginDate2和endDate2组成的日期段
	 * 参数可以是日期、日期时间、字符串等等
	 * 
	 * @param beginDate1
	 * @param endDate1
	 * @param beginDate2
	 * @param endDate2
	 * @return
	 */
	public static <T extends Comparable<T>> boolean contains(T beginDate1, T endDate1, T beginDate2, T endDate2) {
		return beginDate1.compareTo(beginDate2) <= 0 && endDate1.compareTo(endDate2) >= 0;
	}

	/**
	 * 转换数组类型
	 * 
	 * @param <T>
	 * @param objects
	 *            源数组实例
	 * @param clazz
	 *            将转换的数组类型
	 * @return
	 */
	public static <T> T[] convertArrayType(Object[] objects, Class<T> clazz) {
		if (objects == null) {
			return null;
		}
		T[] t = (T[]) Array.newInstance(clazz, objects.length);
		for (int i = 0; i < objects.length; i++) {
			t[i] = (T) objects[i];
		}
		return t;
	}

	public static UFLiteralDate[] createDateArray(String beginDate, String endDate) {
		return createDateArray(UFLiteralDate.getDate(beginDate), UFLiteralDate.getDate(endDate));
	}

	public static UFLiteralDate[] createDateArray(String beginDate, String endDate, int forwardDays, int backwardDays) {
		return createDateArray(UFLiteralDate.getDate(beginDate), UFLiteralDate.getDate(endDate), forwardDays,
				backwardDays);
	}

	/**
	 * 根据开始日期和结束日期，得到日期数组 比如开始日期为7.1，结束日期为7.4，则返回7.1,7.2,7.3,7.4
	 * 
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public static UFLiteralDate[] createDateArray(UFLiteralDate beginDate, UFLiteralDate endDate) {
		// 保证开始日期不晚于结束日期
		if (beginDate.after(endDate)) {
			UFLiteralDate tempDate = beginDate;
			beginDate = endDate;
			endDate = tempDate;
		}
		List<UFLiteralDate> retList = new ArrayList<UFLiteralDate>();
		UFLiteralDate date = beginDate;
		do {
			retList.add(date);
			date = date.getDateAfter(1);
		} while (!date.after(endDate));
		return retList.toArray(new UFLiteralDate[0]);
	}

	/**
	 * 根据开始日期和结束日期，得到日期数组
	 * 比如开始日期为7.1，结束日期为7.4，forwardDays为1，backwardDays为1，则返回6.30
	 * ,7.1,7.2,7.3,7.4,7.5。
	 * 
	 * @param beginDate
	 * @param endDate
	 * @param forwardDays
	 *            ，往前追溯的天数
	 * @param backwardDays
	 *            ，往后追溯的天数
	 * @return
	 */
	public static UFLiteralDate[] createDateArray(UFLiteralDate beginDate, UFLiteralDate endDate, int forwardDays,
			int backwardDays) {
		// 保证开始日期不晚于结束日期
		if (beginDate.after(endDate)) {
			UFLiteralDate tempDate = beginDate;
			beginDate = endDate;
			endDate = tempDate;
		}
		UFLiteralDate newBeginDate = forwardDays == 0 ? beginDate : beginDate.getDateBefore(forwardDays);
		UFLiteralDate newEndDate = backwardDays == 0 ? endDate : endDate.getDateAfter(backwardDays);
		return createDateArray(newBeginDate, newEndDate);
	}

	public static TimeZone ensureTimeZone(TimeZone timeZone) {
		return timeZone != null ? timeZone : ICalendar.BASE_TIMEZONE;
	}

	/**
	 * 在已排序的数组下根据要过滤的属性名称以及对应的值查询SuperVO 数组
	 * （按过滤属性条件排序的VO数组查询可采用此防范（符合条件的VO排列在一起），以减少循环遍历次数，比findByVOArray更高效）
	 * 
	 * @param <T>
	 * @param arrayVOs
	 * @param attriNames
	 * @param attriValues
	 * @return
	 */
	public static <T extends SuperVO> T[] findBySortVOArray(T[] arrayVOs, String[] attriNames, Object[] attriValues) {
		if (attriNames.length != attriValues.length) {
			// 过滤属性名称数量与过滤属性值数必须一致！
			return null;
		}
		List<T> resultVOs = new ArrayList<T>();
		StringBuilder filterValue = new StringBuilder();
		for (Object value : attriValues) {
			filterValue.append(value).append("_");
		}
		if (StringUtils.isEmpty(filterValue.toString())) {
			return null;
		}
		boolean equaled = false;// 是否匹配到
		for (T vo : arrayVOs) {
			if (vo == null) {
				continue;
			}
			StringBuilder keyValue = new StringBuilder();
			for (String attName : attriNames) {
				keyValue.append(vo.getAttributeValue(attName)).append("_");
			}
			if (filterValue.toString().equals(keyValue.toString())) {
				resultVOs.add(vo);
				equaled = true;
			} else {
				if (equaled) { // 最后一笔不匹配的直接退出遍历查询
					break;
				}
			}
		}
		if (resultVOs.size() == 0) {
			return null;
		} else {
			T[] returnVO = (T[]) Array.newInstance(arrayVOs[0].getClass(), resultVOs.size());
			for (int i = 0; i < resultVOs.size(); i++) {
				returnVO[i] = resultVOs.get(i);
			}
			return returnVO;
		}
	}

	/**
	 * 在已排序的数组下根据要过滤的属性名称以及对应的值查询SuperVO 数组
	 * （按过滤属性条件排序的VO数组查询可采用此防范（符合条件的VO排列在一起），以减少循环遍历次数，比findByVOArray更高效）
	 * 
	 * @param <T>
	 * @param listVO
	 * @param attriNames
	 * @param attriValues
	 * @return
	 */
	public static <T extends SuperVO> List<T> findBySortVOList(List<T> listVO, String[] attriNames, Object[] attriValues) {
		if (attriNames.length != attriValues.length) {
			// 过滤属性名称数量与过滤属性值数必须一致！
			return null;
		}
		if (listVO == null || listVO.size() == 0) {
			return null;
		}
		List<T> resultVOs = new ArrayList<T>();
		StringBuilder filterValue = new StringBuilder();
		for (Object value : attriValues) {
			filterValue.append(value).append("_");
		}
		if (StringUtils.isEmpty(filterValue.toString())) {
			return null;
		}
		boolean equaled = false;// 是否匹配到
		for (T vo : listVO) {
			if (vo == null) {
				continue;
			}
			StringBuilder keyValue = new StringBuilder();
			for (String attName : attriNames) {
				keyValue.append(vo.getAttributeValue(attName)).append("_");
			}
			if (filterValue.toString().equals(keyValue.toString())) {
				resultVOs.add(vo);
				equaled = true;
			} else {
				if (equaled) { // 最后一笔不匹配的直接退出遍历查询
					break;
				}
			}
		}
		if (resultVOs.size() == 0) {
			return null;
		} else {
			return resultVOs;
		}
	}

	/**
	 * 根据要过滤的属性名称以及对应的值查询SuperVO 数组
	 * 
	 * @param
	 * @param attriNames
	 * @param attriValues
	 * @return
	 */
	public static <T extends SuperVO> T[] findByVOArray(T[] arrayVOs, String[] attriNames, Object[] attriValues) {
		if (ArrayUtils.isEmpty(arrayVOs)) {
			return null;
		}
		if (attriNames.length != attriValues.length) {
			// 过滤属性名称数量与过滤属性值数必须一致！
			return null;
		}

		List<T> resultVOs = new ArrayList<T>();
		StringBuilder filterValue = new StringBuilder();
		for (Object value : attriValues) {
			filterValue.append(value).append("_");
		}
		if (StringUtils.isEmpty(filterValue.toString())) {
			return null;
		}
		for (T vo : arrayVOs) {
			if (vo == null) {
				continue;
			}
			StringBuilder keyValue = new StringBuilder();
			for (String attName : attriNames) {
				keyValue.append(vo.getAttributeValue(attName)).append("_");
			}
			if (filterValue.toString().equals(keyValue.toString())) {
				resultVOs.add(vo);
			}
		}
		if (resultVOs.size() == 0) {
			return null;
		} else {
			T[] returnVO = (T[]) Array.newInstance(arrayVOs[0].getClass(), resultVOs.size());
			for (int i = 0; i < resultVOs.size(); i++) {
				returnVO[i] = resultVOs.get(i);
			}
			return returnVO;
		}
	}

	/**
	 * 根据要过滤的属性名称以及对应的值查询SuperVO List
	 * 
	 * @param
	 * @param attriNames
	 * @param attriValues
	 * @return
	 */
	public static <T extends SuperVO> List<T> findByVOList(List<T> listVO, String[] attriNames, Object[] attriValues) {
		if (attriNames.length != attriValues.length) {
			// 过滤属性名称数量与过滤属性值数必须一致！
			return null;
		}
		if (listVO == null || listVO.size() == 0) {
			return null;
		}
		List<T> resultVOs = new ArrayList<T>();
		StringBuilder filterValue = new StringBuilder();
		for (Object value : attriValues) {
			filterValue.append(value).append("_");
		}
		if (StringUtils.isEmpty(filterValue.toString())) {
			return null;
		}
		for (T vo : listVO) {
			if (vo == null) {
				continue;
			}
			StringBuilder keyValue = new StringBuilder();
			for (String attName : attriNames) {
				keyValue.append(vo.getAttributeValue(attName)).append("_");
			}
			if (filterValue.toString().equals(keyValue.toString())) {
				resultVOs.add(vo);
			}
		}
		if (resultVOs.size() == 0) {
			return null;
		} else {
			return resultVOs;
		}
	}

	/**
	 * 根据要过滤的属性名称以及对应的值查询第一笔符合条件的SuperVO
	 * 
	 * @param
	 * @param attriNames
	 * @param attriValues
	 * @return
	 */
	public static <T extends SuperVO> T findFirstVOByVOArray(T[] arrayVOs, String[] attriNames, Object[] attriValues) {
		if (attriNames.length != attriValues.length) {
			// 过滤属性名称数量与过滤属性值数必须一致！
			return null;
		}
		T resultVO = null;
		StringBuilder filterValue = new StringBuilder();
		for (Object value : attriValues) {
			filterValue.append(value).append("_");
		}
		if (StringUtils.isEmpty(filterValue.toString())) {
			return null;
		}
		for (T vo : arrayVOs) {
			if (vo == null) {
				continue;
			}
			StringBuilder keyValue = new StringBuilder();
			for (String attName : attriNames) {
				keyValue.append(vo.getAttributeValue(attName)).append("_");
			}
			if (filterValue.toString().equals(keyValue.toString())) {
				resultVO = vo;
				break;// 找到第一笔符合条件的VO则返回
			}
		}
		return resultVO;
	}

	/**
	 * 根据要过滤的属性名称以及对应的值查询第一笔符合条件的SuperVO
	 * 
	 * @param
	 * @param attriNames
	 * @param attriValues
	 * @return
	 */
	public static <T extends SuperVO> T findFirstVOByVOList(List<T> listVO, String[] attriNames, Object[] attriValues) {
		if (attriNames.length != attriValues.length) {
			// 过滤属性名称数量与过滤属性值数必须一致！
			return null;
		}
		T resultVO = null;
		StringBuilder filterValue = new StringBuilder();
		for (Object value : attriValues) {
			filterValue.append(value).append("_");
		}
		if (StringUtils.isEmpty(filterValue.toString())) {
			return null;
		}
		for (T vo : listVO) {
			if (vo == null) {
				continue;
			}
			StringBuilder keyValue = new StringBuilder();
			for (String attName : attriNames) {
				keyValue.append(vo.getAttributeValue(attName)).append("_");
			}
			if (filterValue.toString().equals(keyValue.toString())) {
				resultVO = vo;
				break;// 找到第一笔符合条件的VO则返回
			}
		}
		return resultVO;
	}

	/**
	 * 取两个set的交集
	 * 
	 * @param <T>
	 * @param set1
	 * @param set2
	 * @return
	 */
	public static <T> Set<T> getIntersectionSet(Set<T> set1, Set<T> set2) {
		if (CollectionUtils.isEmpty(set1) || CollectionUtils.isEmpty(set2)) {
			return null;
		}
		if (set1.containsAll(set2) && set2.containsAll(set1)) {
			return set1;
		}
		Set<T> set = new HashSet<T>();
		for (T next : set1) {
			if (set2.contains(next)) {
				set.add(next);
			}
		}
		return set;
	}

	/**
	 * 获得ParentVO<br>
	 * 
	 * @param aggVOs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends CircularlyAccessibleValueObject> T[] getParentVOArrayFromAggVOs(Class<T> clazz,
			AggregatedValueObject[] aggVOs) {
		if (ArrayUtils.isEmpty(aggVOs)) {
			return null;
		}

		T[] parents = (T[]) Array.newInstance(clazz, aggVOs.length);
		for (int i = 0; i < aggVOs.length; i++) {
			parents[i] = (T) aggVOs[i].getParentVO();
		}

		return parents;
	}

	/**
	 * 经常会有需要提示人员姓名的场景，用工具方法提供
	 * 
	 * @param pk_psndoc
	 * @throws DAOException
	 */
	public static String getPsnName(String pk_psndoc) throws DAOException {
		PsndocVO psn = (PsndocVO) new BaseDAO().retrieveByPK(PsndocVO.class, pk_psndoc, new String[] { PsndocVO.NAME,
				PsndocVO.NAME2, PsndocVO.NAME3, PsndocVO.NAME4, PsndocVO.NAME5, PsndocVO.NAME6 });
		if (psn == null) {
			return null;
		}
		return psn.getMultiLangName();
	}

	/**
	 * 查询多个人员的人名，用逗号分隔.返回的人名与pk_psndocs一致，但如果有重复人员主键，只显示一次
	 * 
	 * @param pk_psndocs
	 * @return
	 * @throws BusinessException
	 */
	public static String getPsnNames(String[] pk_psndocs) throws BusinessException {
		Map<String, String> nameMap = getPsnNamesMap(pk_psndocs);
		if (MapUtils.isEmpty(nameMap)) {
			return null;
		}
		StringBuilder nameSB = new StringBuilder();
		Set<String> displayedPk = new HashSet<String>();
		for (String pk_psndoc : pk_psndocs) {
			if (StringUtils.isEmpty(pk_psndoc) || displayedPk.contains(pk_psndoc)) {
				continue;
			}
			displayedPk.add(pk_psndoc);
			nameSB.append(nameMap.get(pk_psndoc)).append(",");

		}
		nameSB.deleteCharAt(nameSB.length() - 1);
		return nameSB.toString();
	}

	/**
	 * 查询多个人员的人名，<pk_psndoc,name>
	 * 
	 * @param pk_psndocs
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getPsnNamesMap(String[] pk_psndocs) throws BusinessException {
		// 去掉重复的人员
		pk_psndocs = new HashSet<String>(Arrays.asList(pk_psndocs)).toArray(new String[0]);
		InSQLCreator isc = null;
		Map<String, String> nameMap = new HashMap<String, String>();
		try {
			isc = new InSQLCreator();
			String inSQL = isc.getInSQL(pk_psndocs);
			Collection<PsndocVO> c = new BaseDAO().retrieveByClause(PsndocVO.class, PsndocVO.PK_PSNDOC + " in(" + inSQL
					+ ")", new String[] { PsndocVO.PK_PSNDOC, PsndocVO.NAME, PsndocVO.NAME2, PsndocVO.NAME3,
					PsndocVO.NAME4, PsndocVO.NAME5, PsndocVO.NAME6 });
			PsndocVO[] psnVOs = toArray(PsndocVO.class, c);
			if (ArrayUtils.isEmpty(psnVOs)) {
				return null;
			}
			for (PsndocVO psndocVO : psnVOs) {
				nameMap.put(psndocVO.getPk_psndoc(), psndocVO.getMultiLangName());
			}
			return nameMap;
		} finally {
			if (isc != null) {
				isc.clear();
			}
		}
	}

	/**
	 * 返回一个双层map里面value的个数
	 * 
	 * @param <E>
	 * @param <T>
	 * @param <V>
	 * @param map
	 * @return
	 */
	public static <E, T, V> int getValueCount(Map<E, Map<T, V>> map) {
		if (MapUtils.isEmpty(map)) {
			return 0;
		}
		int count = 0;
		for (E e : map.keySet()) {
			Map<T, V> map1 = map.get(e);
			if (MapUtils.isEmpty(map1)) {
				continue;
			}
			count += map1.values().size();
		}
		return count;
	}

	/**
	 * 将一个数组按某个字段分组，将分组包装为数组放入map
	 * 
	 * @param <E>
	 * @param <T>
	 * @param fieldName
	 * @param vos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E, T extends SuperVO> Map<E, T[]> group2ArrayByField(String fieldName, T[] vos) {
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		}
		return transferListMap2ArrayMap((Class<T>) vos.getClass().getComponentType(),
				(Map<E, List<T>>) group2ListByField(fieldName, vos));
	}

	public static <E, N, F, T extends SuperVO> Map<E, Map<N, Map<F, T[]>>> group2ArrayByFields(String fieldNameE,
			String fieldNameN, String fieldNameF, T[] vos) {
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		}
		Map<E, T[]> tempMap = group2ArrayByField(fieldNameE, vos);
		Map<E, Map<N, Map<F, T[]>>> retMap = new HashMap<E, Map<N, Map<F, T[]>>>();
		for (E e : tempMap.keySet()) {
			T[] tempVOs = tempMap.get(e);
			Map<N, Map<F, T[]>> subMap = group2ArrayByFields(fieldNameN, fieldNameF, tempVOs);
			retMap.put(e, subMap);
		}
		return retMap;
	}

	@SuppressWarnings("unchecked")
	public static <E, N, T extends SuperVO> Map<E, Map<N, T[]>> group2ArrayByFields(String fieldNameE,
			String fieldNameN, T[] vos) {
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		}
		Map<E, Map<N, List<T>>> tempMap = group2ListByFields(fieldNameE, fieldNameN, vos);
		Class<T> clz = (Class<T>) vos.getClass().getComponentType();
		return transferListMap2ArrayMap2(clz, tempMap);
	}

	/**
	 * 将一个数组根据某个字段分组，将分组包装为list装入map
	 * 
	 * @param <E>
	 * @param <T>
	 * @param fieldName
	 * @param vos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E, T extends SuperVO> Map<E, List<T>> group2ListByField(String fieldName, T[] vos) {
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		}
		Map<E, List<T>> retMap = new HashMap<E, List<T>>();
		for (T vo : vos) {
			E fieldVal = (E) vo.getAttributeValue(fieldName);
			List<T> list = retMap.get(fieldVal);
			if (list == null) {
				list = new ArrayList<T>();
				retMap.put(fieldVal, list);
			}
			list.add(vo);
		}
		return retMap;
	}

	public static <E, N, F, T extends SuperVO> Map<E, Map<N, Map<F, List<T>>>> group2ListByFields(String fieldNameE,
			String fieldNameN, String fieldNameF, T[] vos) {
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		}
		Map<E, T[]> tempMap = group2ArrayByField(fieldNameE, vos);
		Map<E, Map<N, Map<F, List<T>>>> retMap = new HashMap<E, Map<N, Map<F, List<T>>>>();
		for (E e : tempMap.keySet()) {
			T[] tempVOs = tempMap.get(e);
			Map<N, Map<F, List<T>>> subMap = group2ListByFields(fieldNameN, fieldNameF, tempVOs);
			retMap.put(e, subMap);
		}
		return retMap;
	}

	/**
	 * 将一个数组根据两个字段分组：字段1为大组，字段2为小组，将分组包装为list放入map
	 * 
	 * @param <E>
	 * @param <N>
	 * @param <T>
	 * @param fieldNameE
	 * @param fieldNameN
	 * @param vos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E, N, T extends SuperVO> Map<E, Map<N, List<T>>> group2ListByFields(String fieldNameE,
			String fieldNameN, T[] vos) {
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		}
		Map<E, Map<N, List<T>>> retMap = new HashMap<E, Map<N, List<T>>>();
		for (T vo : vos) {
			E fieldValE = (E) vo.getAttributeValue(fieldNameE);
			Map<N, List<T>> map = retMap.get(fieldValE);
			if (map == null) {
				map = new HashMap<N, List<T>>();
				retMap.put(fieldValE, map);
			}
			N fieldValN = (N) vo.getAttributeValue(fieldNameN);
			List<T> list = map.get(fieldValN);
			if (list == null) {
				list = new ArrayList<T>();
				map.put(fieldValN, list);
			}
			list.add(vo);
		}
		return retMap;
	}

	/**
	 * 将map里面的数组拉平组成一个大数组
	 * 
	 * @param <T>
	 * @param clz
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] mapVal2Array(Class<T> clz, Map<?, T[]> map) {
		if (MapUtils.isEmpty(map)) {
			return null;
		}
		List<T> retList = new ArrayList<T>();
		for (Object key : map.keySet()) {
			T[] array = map.get(key);
			retList.addAll(Arrays.asList(array));
		}
		T[] array = (T[]) Array.newInstance(clz, 0);
		return retList.toArray(array);
	}

	/**
	 * 将两个数组合并，oldArray中的vo肯定有主键。如果oldArray中的某个vo的主键在newArray中出现了，
	 * 则oldArray中的此vo不出现在最终合并的数组中， 因为newArray中对应的vo的数据要更新一些
	 * 
	 * @param <T>
	 * @param oldArray
	 * @param newArray
	 */
	@SuppressWarnings("unchecked")
	public static <T extends SuperVO> T[] merge2Array(T[] oldArray, T[] newArray) {
		if (ArrayUtils.isEmpty(oldArray)) {
			return newArray;
		}
		if (ArrayUtils.isEmpty(newArray)) {
			return oldArray;
		}
		Set<String> newPkSet = new HashSet<String>();
		for (T newVO : newArray) {
			if (!StringUtils.isBlank(newVO.getPrimaryKey())) {
				newPkSet.add(newVO.getPrimaryKey());
			}
		}
		List<T> list = new ArrayList<T>();
		for (T oldVO : oldArray) {
			if (!newPkSet.contains(oldVO.getPrimaryKey())) {
				list.add(oldVO);
			}
		}
		if (list.size() == 0) {
			return newArray;
		}
		list.addAll(Arrays.asList(newArray));
		return list.toArray((T[]) Array.newInstance(newArray.getClass().getComponentType(), 0));
	}

	public static <E, T, V extends T, K extends T> Map<E, T[]> mergeGroupedMap(Class<T> clz, Map<E, V[]> map1,
			Map<E, K[]> map2) {
		return mergeGroupedMap(clz, map1, map2, null);
	}

	@SuppressWarnings("unchecked")
	public static <E, T, V extends T, K extends T> Map<E, T[]> mergeGroupedMap(Class<T> clz, Map<E, V[]> map1,
			Map<E, K[]> map2, Comparator comparator) {
		if (MapUtils.isEmpty(map1) && MapUtils.isEmpty(map2)) {
			return null;
		}
		Set<E> keyset1 = MapUtils.isEmpty(map1) ? null : map1.keySet();
		Set<E> keyset2 = MapUtils.isEmpty(map2) ? null : map2.keySet();
		Set<E> allKeyset = null;
		if (keyset1 == null) {
			allKeyset = keyset2;
		} else if (keyset2 == null) {
			allKeyset = keyset1;
		} else {
			allKeyset = new HashSet<E>();
			allKeyset.addAll(keyset1);
			allKeyset.addAll(keyset2);
		}
		Map<E, T[]> retMap = new HashMap<E, T[]>();
		for (E key : allKeyset) {
			V[] array1 = map1 == null ? null : map1.get(key);
			K[] array2 = map2 == null ? null : map2.get(key);
			if (ArrayUtils.isEmpty(array1) && ArrayUtils.isEmpty(array2)) {
				continue;
			}
			if (ArrayUtils.isEmpty(array1)) {
				retMap.put(key, array2);
				continue;
			}
			if (ArrayUtils.isEmpty(array2)) {
				retMap.put(key, array1);
				continue;
			}
			T[] mergeArray = (T[]) ArrayHelper.addAll(array1, array2, clz);
			if (comparator != null) {
				Arrays.sort(mergeArray, comparator);
			} else if (Comparable.class.isAssignableFrom(clz)) {
				Arrays.sort(mergeArray);
			}
			retMap.put(key, mergeArray);
		}
		return retMap;
	}

	public static <E, T, V> Map<E, Map<T, V>> putAll(Map<E, Map<T, V>> fatherMap, Map<E, Map<T, V>> childMap) {
		if (MapUtils.isEmpty(childMap)) {
			return fatherMap;
		}
		if (MapUtils.isEmpty(fatherMap)) {
			return childMap;
		}
		// 将child塞入father
		for (E e : childMap.keySet()) {
			Map<T, V> m = childMap.get(e);
			if (MapUtils.isEmpty(m)) {
				continue;
			}
			Map<T, V> fatherM = fatherMap.get(e);
			if (MapUtils.isEmpty(fatherM)) {
				fatherMap.put(e, m);
				continue;
			}
			fatherM.putAll(m);
		}
		return fatherMap;
	}

	/**
	 * 根据主键查询，返回的顺序与pks一致
	 * 
	 * @param <T>
	 * @param clz
	 * @param pks
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static <T extends SuperVO> T[] queryByPks(Class<T> clz, String[] pks) throws BusinessException {
		if (ArrayUtils.isEmpty(pks)) {
			return null;
		}
		// 执行查询
		InSQLCreator isc = new InSQLCreator();
		T[] vos = null;
		try {
			String inSQL = isc.getInSQL(pks);
			T t = clz.newInstance();
			vos = (T[]) toArray(clz, new BaseDAO().retrieveByClause(clz, t.getPKFieldName() + " in(" + inSQL + ")"));
			if (ArrayUtils.isEmpty(vos)) {
				return null;
			}

		} catch (InstantiationException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			isc.clear();
		}
		Map<String, T> map = new HashMap<String, T>();
		for (T t : vos) {
			map.put(t.getPrimaryKey(), t);
		}
		T[] retVOs = (T[]) Array.newInstance(clz, pks.length);
		for (int i = 0; i < pks.length; i++) {
			retVOs[i] = map.get(pks[i]);
		}
		return retVOs;
	}

	public static <T> T[] retrieveByClause(Class<T> clz, BaseDAO dao, String condition) throws DAOException {
		return retrieveByClause(clz, dao, condition, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] retrieveByClause(Class<T> clz, BaseDAO dao, String condition, SQLParameter para)
			throws DAOException {
		return (T[]) toArray(clz, dao.retrieveByClause(clz, condition, para));
	}

	public static <T> T[] retrieveByClause(Class<T> clz, String condition) throws DAOException {
		return retrieveByClause(clz, condition, null);
	}

	public static <T> T[] retrieveByClause(Class<T> clz, String condition, SQLParameter para) throws DAOException {
		BaseDAO dao = new BaseDAO();
		return retrieveByClause(clz, dao, condition, para);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Class<T> clz, Collection<T> col) {
		if (org.springframework.util.CollectionUtils.isEmpty(col)) {
			return null;
		}
		T[] array = (T[]) Array.newInstance(clz, 0);
		return col.toArray(array);
	}

	@SuppressWarnings("unchecked")
	public static <E, V, T extends SuperVO> Map<E, Map<V, T>> toMap(String fieldName1, String fieldName2, T[] vos) {
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		}
		Map<E, Map<V, T>> map = new HashMap<E, Map<V, T>>();
		for (T vo : vos) {
			if (vo != null) {
				E key1 = (E) vo.getAttributeValue(fieldName1);
				Map<V, T> map2 = map.get(key1);
				if (map2 == null) {
					map2 = new HashMap<V, T>();
					map.put(key1, map2);
				}
				map2.put((V) vo.getAttributeValue(fieldName2), vo);
			}
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public static <E, T extends SuperVO> Map<E, T> toMap(String fieldName, T[] vos) {
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		}
		Map<E, T> map = new HashMap<E, T>();
		for (T vo : vos) {
			if (vo != null) {
				map.put((E) vo.getAttributeValue(fieldName), vo);
			}
		}
		return map;
	}

	/**
	 * 将一个object数组转换为同长度的string数组
	 * 
	 * @param objArray
	 * @return
	 */
	public static String[] toStringArray(Object[] objArray) {
		if (objArray == null || objArray.length == 0) {
			return null;
		}
		return toStringArray(objArray, 0, objArray.length - 1);
	}

	public static String[] toStringArray(Object[] objArray, int beginIndex, int endIndex) {
		if (objArray == null) {
			return null;
		}
		if (objArray instanceof String[]) {
			return (String[]) objArray;
		}
		String[] retArray = new String[endIndex - beginIndex + 1];
		for (int i = 0; i < retArray.length; i++) {
			if (objArray[beginIndex + i] == null) {
				continue;
			}
			retArray[i] = objArray[beginIndex + i].toString();
		}
		return retArray;
	}

	/**
	 * 将Object转换为String
	 * 
	 * @param obj
	 * @return
	 */
	public static String toStringObject(Object obj) {
		return obj == null || (obj.getClass().isArray() && 0 == ArrayUtils.getLength(obj)) ? "" : obj.toString();
	}

	@SuppressWarnings("unchecked")
	public static <H extends CircularlyAccessibleValueObject, A extends AggregatedValueObject> Map<String, H> transferAggMap2HeadMap(
			Map<String, A> map) {
		if (MapUtils.isEmpty(map)) {
			return null;
		}
		Map<String, H> retMap = new HashMap<String, H>();
		for (String key : map.keySet()) {
			retMap.put(key, (H) map.get(key).getParentVO());
		}
		return retMap;
	}

	/**
	 * 把list分组转换为array分组
	 * 
	 * @param <E>
	 * @param <T>
	 * @param clz
	 * @param listMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E, T> Map<E, T[]> transferListMap2ArrayMap(Class<T> clz, Map<E, List<T>> listMap) {
		if (MapUtils.isEmpty(listMap)) {
			return null;
		}
		Map<E, T[]> retMap = new HashMap<E, T[]>();
		for (E fieldVal : listMap.keySet()) {
			List<T> list = listMap.get(fieldVal);
			if (CollectionUtils.isEmpty(list)) {
				continue;
			}
			T[] array = (T[]) Array.newInstance(clz, 0);
			retMap.put(fieldVal, list.toArray(array));
		}
		return retMap;
	}

	/**
	 * 把list分组转换为array分组
	 * 
	 * @param <E>
	 * @param <T>
	 * @param <V>
	 * @param clz
	 * @param listMap
	 * @return
	 */
	public static <E, T, V> Map<E, Map<T, V[]>> transferListMap2ArrayMap2(Class<V> clz, Map<E, Map<T, List<V>>> listMap) {
		if (MapUtils.isEmpty(listMap)) {
			return null;
		}
		Map<E, Map<T, V[]>> retMap = new HashMap<E, Map<T, V[]>>();
		for (E key1 : listMap.keySet()) {
			Map<T, List<V>> map = listMap.get(key1);
			if (MapUtils.isEmpty(map)) {
				continue;
			}
			retMap.put(key1, transferListMap2ArrayMap(clz, map));
		}
		return retMap;
	}

	/**
	 * 将一个双重的map的第一key和第二key交换位置 假设Map<String, Map<String,
	 * ShiftVO>>是一个存储人员的班次的map，第一个string是人员主键，第二个key是日期
	 * 那么经过此方法的调用后，返回的map中，第一个string是日期，第二个key是人员主键
	 * 其实这个方法的实质是修改map的分组方式：原来的map是先以V分组，然后每个分组内又以K分组，用此方法处理后 map先以K分组，后以V分组
	 * 
	 * @param <K>
	 * @param <V>
	 * @param <T>
	 * @param map
	 * @return
	 */
	public static <K, V, T> Map<K, Map<V, T>> transferMap(Map<V, Map<K, T>> map) {
		if (map == null || map.size() == 0) {
			return null;
		}
		Map<K, Map<V, T>> retMap = new HashMap<K, Map<V, T>>();
		for (V v : map.keySet()) {
			Map<K, T> originalValue = map.get(v);
			if (originalValue == null || originalValue.size() == 0) {
				continue;
			}
			for (K k : originalValue.keySet()) {
				Map<V, T> map2 = null;
				if (retMap.containsKey(k)) {
					map2 = retMap.get(k);
				} else {
					map2 = new HashMap<V, T>();
					retMap.put(k, map2);
				}
				map2.put(v, originalValue.get(k));
			}
		}
		return retMap;
	}

	/**
	 * 私有构造函数
	 */
	private CommonUtils() {
	}
}
