package nc.impl.hrwa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.hr.utils.CommonUtils;
import nc.itf.hrwa.IWadaysalaryQueryService;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.hrwa.wadaysalary.DaySalaryEnum;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
/**
 * 关于日薪的查询,原则上,都应该走这个类,才方便整理
 * @author tank
 *
 */
public class WadaysalaryQueryServiceImpl implements IWadaysalaryQueryService {
	
	@Override
	public Map<String, UFDouble> getTotalDaySalaryMapByWaItemWithoutRecalculate(String pk_org, String[] pk_psndocs,
			String pk_wa_class, String cyear, String cperiod, String pk_wa_item,String pk_item_group
			) throws BusinessException {
		String pk_loginuser = InvocationInfoProxy.getInstance().getUserId();
		// 查询薪资方案对应的薪资期间的开始日期与结束日期
		String qrySql = "SELECT\n" + "	period.cstartdate,\n" + "	period.cenddate\n" + "FROM\n"
				+ "	wa_waclass waclass\n"
				+ "LEFT JOIN wa_period period ON period.pk_periodscheme = waclass.pk_periodscheme\n" + "WHERE\n"
				+ "	waclass.pk_wa_class = '" + pk_wa_class + "'\n" + "AND period.caccyear = '" + cyear + "'\n"
				+ "AND period.caccperiod = '" + cperiod + "'";
		@SuppressWarnings("unchecked")
		List<HashMap<String, Object>> listMaptemp = (ArrayList<HashMap<String, Object>>) getDao().executeQuery(qrySql,
				new MapListProcessor());
		if (listMaptemp.size() < 1) {
			return null;
		}
		HashMap<String, Object> hashMap = listMaptemp.get(0);
		String begindate = hashMap.get("cstartdate").toString();
		String enddate = hashMap.get("cenddate").toString();

		return retrievePsnSalaryMap(pk_loginuser,pk_org,pk_psndocs, pk_wa_item, begindate, enddate,pk_item_group);
	}
	
	
	@Override
	public Map<String, Map<String, Double>> getTotalDaySalaryMapWithoutRecalculate(
			String pk_org, String[] pk_psndocs, UFLiteralDate[] allDates, boolean flag,
			String pk_item_group) throws BusinessException {
		
		// 参数校验
		if (pk_psndocs == null || pk_psndocs.length < 1) {
			throw new BusinessException("人員信息主鍵為空");
		}
		if (null == pk_item_group) {
			throw new BusinessException("薪資項目分組不允許為空");
		}
		Map<String, Map<String, Double>> rsMap = new HashMap<>(pk_psndocs.length);
		if(allDates==null || allDates.length <= 0){
			return rsMap;
		}
		
		for(String pk_psndoc : pk_psndocs){
			Map<String,Double> rs = getHourSalaryByPsn(pk_org, pk_psndoc,pk_item_group,allDates);
			rsMap.put(pk_psndoc, rs);
		}
		
		return rsMap;
	}

	

	private BaseDAO getDao() {
		if (null == dao) {
			dao = new BaseDAO();
		}
		return dao;
	}

	private BaseDAO dao;

	

	private Map<String, UFDouble> retrievePsnSalaryMap(String pk_loginuser, String pk_org, String[] pk_psndocs,
			 String pk_wa_item, String begindate, String enddate, String pk_group_item)
			throws BusinessException, DAOException {
		Map<String, UFDouble> psnSalaryMap = new HashMap<>(DaySalaryCache.MAX_PSN_IN_ORG);
		//获取缓存  <日历天,<人员PK,日薪>>
		Map<String, Map<String, Double>> cacheMap = DaySalaryCache.getInstance().get(pk_loginuser, pk_org, pk_group_item,
				pk_wa_item);
		Set<String> allDateSet = getAllDateStr(begindate,enddate);
		Set<String> allPsnSet = new HashSet<>(Arrays.asList(pk_psndocs));
		
		//循环需要统计的日期
		for(String calender : allDateSet){
			Map<String, Double> cachePsnSalaryMap = cacheMap.get(calender);
			if(cachePsnSalaryMap!=null){
				//所有需要计算的人
				for(String pk_psndoc : allPsnSet){
					Double salary = cachePsnSalaryMap.get(pk_psndoc);
					if(salary!=null){
						UFDouble sum = psnSalaryMap.get(pk_psndoc);
						if(sum==null){
							sum = UFDouble.ZERO_DBL;
						}
						psnSalaryMap.put(pk_psndoc, sum.add(salary));
					}
				}
			}
		}
		return psnSalaryMap;
	}
	/**
	 * 按人获取某些日期的日薪,如果某些日期没有被计算,则重新计算,如果已经计算,那么则从缓存中直接读取.
	 * @param pk_org
	 * @param pk_psndoc
	 * @param pk_item_group
	 * @param dateList 需要计算的日期集合
	 * @return <日期,时薪>
	 * @throws BusinessException 
	 */
	@Override
	public Map<String, Double> getHourSalaryByPsn(String pk_org, String pk_psndoc, String pk_item_group,
			UFLiteralDate[] dateListAll) throws BusinessException {
		
		if(dateListAll==null || dateListAll.length <=0){
			return new HashMap<>();
		}
		
		//过滤重复日期
		Set<UFLiteralDate> dateSetFilter = new HashSet<>();
		dateSetFilter.addAll(Arrays.asList(dateListAll));
		dateListAll = dateSetFilter.toArray(new UFLiteralDate[0]);
		
		// 获取该薪资项目分组下,所有公共薪资项目
		String sql = "select pk_waitem from wa_itemgroupmember mb " + " inner join wa_item item on (item.dr = 0 "
				+ " and item.pk_wa_item = mb.pk_waitem) "
				+ " where mb.dr = 0 and mb.pk_itemgroup = '" + pk_item_group + "' ";
		// 获取该薪资项目分组下,所有扣税,或者不扣税的公共薪资项目
		@SuppressWarnings("unchecked")
		Set<String> waItemSet = (Set<String>) getDao().executeQuery(sql, new ResultSetProcessor() {
			private static final long serialVersionUID = 1L;
			private Set<String> rsSet = new HashSet<>();

			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				while (rs.next()) {
					if (rs.getString(1) != null) {
						rsSet.add(rs.getString(1));
					}
				}
				return rsSet;
			}
		});
		if(waItemSet.size() <=0){
			return new HashMap<>();
		}
		Map<String, Double> rsMap = new HashMap<>();
		Map<String,Set<UFLiteralDate>> waItem2DateMap = new HashMap<>();
		String pk_loginuser = InvocationInfoProxy.getInstance().getUserId();
		//先从缓存中获取已经有的:
		for(String pk_wa_item : waItemSet){
			//<日历天,<人员PK,日薪>>
			Map<String, Map<String, Double>> datePsnSalaryMap = 
					DaySalaryCache.getInstance().get(pk_loginuser, pk_org, pk_item_group, pk_wa_item);
			//没有的日期列表:
			if(datePsnSalaryMap!=null){
				//循环获取缓存中的数据
				for(UFLiteralDate date : dateListAll){
					if(datePsnSalaryMap.get(date.toStdString())!=null 
							&& datePsnSalaryMap.get(date.toStdString()).get(pk_psndoc) != null){
						Double sum = rsMap.get(date.toStdString());
						if(sum == null){
							sum = 0.0d;
						}
						sum = sum+(datePsnSalaryMap.get(date.toStdString()).get(pk_psndoc)/DaySalaryEnum.HOURSALARYNUM);
						rsMap.put(date.toStdString(), sum);
					}else{
						//需要进行运算
						Set<UFLiteralDate> dateSet = waItem2DateMap.get(pk_wa_item);
						if(dateSet==null){
							dateSet = new HashSet<>();
						}
						dateSet.add(date);
						waItem2DateMap.put(pk_wa_item, dateSet);
					}
				}
			}else{
				//需要进行运算
				Set<UFLiteralDate> dateSet = waItem2DateMap.get(pk_wa_item);
				if(dateSet==null){
					dateSet = new HashSet<>();
				}
				dateSet.addAll(Arrays.asList(dateListAll));
				waItem2DateMap.put(pk_wa_item, dateSet);
			}
		}
		if(waItem2DateMap.size() <= 0){
			return rsMap;
		}
		//
		UFDateTime starttime = new UFDateTime();
		DaySalaryCommonCalculator calculator = new DaySalaryCommonCalculator();
		Map<String, Double> calcMap = calculator.doCalculDaySalaryWithDateList(
				pk_loginuser, pk_org, pk_psndoc, waItem2DateMap, pk_item_group);
		//
		UFDateTime endtime = new UFDateTime();
		//本次时间
		Double sumTime = DaySalaryCache.getInstance().getCaculateTime(pk_loginuser);
		if(sumTime==null){
			sumTime = 0.0d;
		}
		DaySalaryCache.getInstance().setCaculateTime(pk_loginuser, sumTime+(endtime.getMillis()-starttime.getMillis())/1000.0);
		if(calcMap!=null && calcMap.size() > 0){
			Set<String> dateSet = calcMap.keySet();
			for(String date : dateSet){
				Double sum = rsMap.get(date);
				if(sum==null){
					sum = 0.0d;
				}
				rsMap.put(date,sum+(calcMap.get(date)/DaySalaryEnum.HOURSALARYNUM));
			}
		}
		
		
		return rsMap;
	
	}

	private Set<String> getAllDateStr(String begindate, String enddate) {
		Set<String> dateStrSet = new HashSet<>(50);
		UFLiteralDate[] allDates = CommonUtils.createDateArray(new UFLiteralDate(begindate), new UFLiteralDate(enddate));
		if(allDates!=null && allDates.length > 0){
			for(UFLiteralDate date : allDates){
				dateStrSet.add(date.toStdString());
			}
		}
		return dateStrSet;
	}


	@Override
	public Double getCalcuTime(String pk_loginuser) {
		return DaySalaryCache.getInstance().getCaculateTime(pk_loginuser);
	}

}
