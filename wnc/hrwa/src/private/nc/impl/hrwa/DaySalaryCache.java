package nc.impl.hrwa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.vo.hrwa.wadaysalary.DaySalaryVO;

/**
 * 日薪缓存
 * @author tank
 * @Discript 所有的日薪数据都将被缓存到这个类 
 * @Warnning 1.此类多用户下有并发问题,所有出入口都应加锁,同用户下以单线程运算薪资,理论上不会有并发问题
 * 			2.使用原则,使用前请调整以下参数,最大程度避免map,set等进行resize操作 
 * 2019年10月17日20:20:38
 */
public class DaySalaryCache {
	/**
	 * 参数调优-原则最大程度避免resize
	 */
	//map负载 因子
	public final static double LOADER_FACTOR = 0.75;
	//人力资源组织最大数量
	public final static int MAX_ORG_NUM = 20;
	//最大同时在线算薪用户数
	public final static int MAX_ONLINE_NUM = MAX_ORG_NUM*2;
	//薪资分组最大数量
	public final static int MAX_ITEM_GROUP_NUM = 10;
	//每个薪资分组内公共薪资项目的最大值
	public final static int MAX_ITEM_IN_GROUP_NUM = 10;
	//天数读取最大值(时间间隔) 31+14
	public final static int MAX_DAY_NUM = 45;
	//每个组织人员最大值
	public final static int MAX_PSN_IN_ORG = 2500;

	private DaySalaryCache() {
	}
	
	private static DaySalaryCache ins = new DaySalaryCache();
	
	public static DaySalaryCache getInstance(){
		return ins;
	}
	/**
	 * 主缓存->薪资计算前放入,薪资计算后清空
	 * <pk_login_user,<pk_org,<pk_item_group,<pk_waitem,<calenderStr,<pk_psndoc,double>>>>>> cacheMap
	 * <当前登录用户PK,<组织,<薪资项目分组,<公共薪资项目,<日历天,<人员PK,日薪>>>>>> 缓存Map
	 * 最顶层不同user之间会有线程安全问题,以下的操作不会有线程安全问题(同个user)
	 */
	private Map<String,Map<String,Map<String,Map<String,Map<String,Map<String,Double>>>>>> cacheMap = 
			new HashMap<>((int)Math.ceil(MAX_ONLINE_NUM/LOADER_FACTOR));
	
	/**
	* <pk_login_user,time>
	*/
	private Map<String,Double> calculeTimeCache = new HashMap<>(MAX_ONLINE_NUM);
	
	public synchronized double getCaculateTime(String pk_login_user){
		Double time = calculeTimeCache.get(pk_login_user);
		return time==null?0:time;
	}
	public synchronized void setCaculateTime(String pk_login_user,Double time){
		calculeTimeCache.put(pk_login_user, time);
	}
	/**
	 * put操作 map层次建立操作
	 * @param userPk
	 * @param pk_psndoc
	 * @param pk_item_group
	 * @param pk_org
	 * @param calender
	 * @param pk_wa_item
	 * @param salary
	 */
	public synchronized void put(String userPk, String pk_org, String pk_item_group, String pk_wa_item, String calender,
			String pk_psndoc, double salary) {
		checkAndbuildStruct(userPk,pk_org,pk_item_group,pk_wa_item,calender,pk_psndoc);
		cacheMap.get(userPk).get(pk_org).get(pk_item_group).get(pk_wa_item).get(calender).put(pk_psndoc, salary);
	}

	
	/**
	 * 日薪vo插入缓存
	 * @param listTbmDaySalaryVOs
	 * @param userPk
	 */
	public synchronized void put(List<DaySalaryVO> listTbmDaySalaryVOs, String userPk) {
		if (listTbmDaySalaryVOs != null && listTbmDaySalaryVOs.size() > 0) {
			for (DaySalaryVO daySalaryVO : listTbmDaySalaryVOs) {
				put(userPk, daySalaryVO.getPk_hrorg(), daySalaryVO.getPk_group_item(), daySalaryVO.getPk_wa_item(),
						daySalaryVO.getSalarydate().toStdString(), daySalaryVO.getPk_psndoc(), daySalaryVO
								.getDaysalary().doubleValue());
			}
		}
	}
	
	/**
	 * 建立缓存结构
	 * @param userPk
	 * @param pk_psndoc
	 * @param pk_item_group
	 * @param pk_org
	 * @param calender
	 * @param pk_wa_item
	 */
	private void checkAndbuildStruct(String userPk, String pk_org, String pk_item_group, String pk_wa_item, String calender,
			String pk_psndoc){
		if(userPk==null){
			return;
		}
		if (cacheMap.containsKey(userPk)) {
			Map<String, Map<String, Map<String, Map<String, Map<String, Double>>>>> orgKeyMap = cacheMap.get(userPk);
			if(orgKeyMap==null){
				orgKeyMap = new HashMap<>();
				cacheMap.put(userPk, orgKeyMap);
			}
			if(pk_org==null){
				return;
			}
			if(orgKeyMap.containsKey(pk_org)){
				Map<String, Map<String, Map<String, Map<String, Double>>>> itemGroupKeyMap = orgKeyMap.get(pk_org);
				if(itemGroupKeyMap==null){
					itemGroupKeyMap = new HashMap<>();
					orgKeyMap.put(pk_org, itemGroupKeyMap);
				}
				if(pk_item_group==null){
					return;
				}
				if(itemGroupKeyMap.containsKey(pk_item_group)){
					Map<String, Map<String, Map<String, Double>>> waitemKeyMap = itemGroupKeyMap.get(pk_item_group);
					if(waitemKeyMap==null){
						waitemKeyMap = new HashMap<>();
						itemGroupKeyMap.put(pk_item_group, waitemKeyMap);
					}
					if(pk_wa_item==null){
						return;
					}
					if(waitemKeyMap.containsKey(pk_wa_item)){
						Map<String, Map<String, Double>> calenderKeyMap = waitemKeyMap.get(pk_wa_item);
						if(calenderKeyMap==null){
							calenderKeyMap = new HashMap<>();
							waitemKeyMap.put(pk_wa_item, calenderKeyMap);
						}
						if(calender==null){
							return;
						}
						if(calenderKeyMap.containsKey(calender)){
							//已经有此结构
							;
						}else{
							Map<String, Double> psnKeyMap = new HashMap<>((int) Math.ceil(MAX_PSN_IN_ORG / LOADER_FACTOR));
							
							calenderKeyMap.put(calender, psnKeyMap);
						}
					}else{
						Map<String, Map<String, Double>> calenderKeyMap = new HashMap<>(
								(int) Math.ceil(MAX_DAY_NUM / LOADER_FACTOR));
						Map<String, Double> psnKeyMap = new HashMap<>((int) Math.ceil(MAX_PSN_IN_ORG / LOADER_FACTOR));
						
						calenderKeyMap.put(calender, psnKeyMap);
						waitemKeyMap.put(pk_wa_item, calenderKeyMap);
					}
				}else{
					Map<String, Map<String, Map<String, Double>>> waitemKeyMap = new HashMap<>(
							(int) Math.ceil(MAX_ITEM_IN_GROUP_NUM / LOADER_FACTOR));
					Map<String, Map<String, Double>> calenderKeyMap = new HashMap<>(
							(int) Math.ceil(MAX_DAY_NUM / LOADER_FACTOR));
					Map<String, Double> psnKeyMap = new HashMap<>((int) Math.ceil(MAX_PSN_IN_ORG / LOADER_FACTOR));
					
					calenderKeyMap.put(calender, psnKeyMap);
					waitemKeyMap.put(pk_wa_item, calenderKeyMap);
					itemGroupKeyMap.put(pk_item_group, waitemKeyMap);
				}
			}else{
				Map<String, Map<String, Map<String, Map<String, Double>>>> itemGroupKeyMap = new HashMap<>(
						(int) Math.ceil(MAX_ITEM_GROUP_NUM / LOADER_FACTOR));
				Map<String, Map<String, Map<String, Double>>> waitemKeyMap = new HashMap<>(
						(int) Math.ceil(MAX_ITEM_IN_GROUP_NUM / LOADER_FACTOR));
				Map<String, Map<String, Double>> calenderKeyMap = new HashMap<>(
						(int) Math.ceil(MAX_DAY_NUM / LOADER_FACTOR));
				Map<String, Double> psnKeyMap = new HashMap<>((int) Math.ceil(MAX_PSN_IN_ORG / LOADER_FACTOR));
				
				calenderKeyMap.put(calender, psnKeyMap);
				waitemKeyMap.put(pk_wa_item, calenderKeyMap);
				itemGroupKeyMap.put(pk_item_group, waitemKeyMap);
				orgKeyMap.put(pk_org, itemGroupKeyMap);
			}
		} else {
			Map<String, Map<String, Map<String, Map<String, Map<String, Double>>>>> orgKeyMap = new HashMap<>(
					(int) Math.ceil(MAX_ORG_NUM / LOADER_FACTOR));
			Map<String, Map<String, Map<String, Map<String, Double>>>> itemGroupKeyMap = new HashMap<>(
					(int) Math.ceil(MAX_ITEM_GROUP_NUM / LOADER_FACTOR));
			Map<String, Map<String, Map<String, Double>>> waitemKeyMap = new HashMap<>(
					(int) Math.ceil(MAX_ITEM_IN_GROUP_NUM / LOADER_FACTOR));
			Map<String, Map<String, Double>> calenderKeyMap = new HashMap<>(
					(int) Math.ceil(MAX_DAY_NUM / LOADER_FACTOR));
			Map<String, Double> psnKeyMap = new HashMap<>((int) Math.ceil(MAX_PSN_IN_ORG / LOADER_FACTOR));
			
			calenderKeyMap.put(calender, psnKeyMap);
			waitemKeyMap.put(pk_wa_item, calenderKeyMap);
			itemGroupKeyMap.put(pk_item_group, waitemKeyMap);
			orgKeyMap.put(pk_org, itemGroupKeyMap);
			cacheMap.put(userPk, orgKeyMap);
		}
	}
	/**
	 * 
	 * @param pk_loginuser
	 * @param pk_org
	 * @param pk_item_group
	 * @param pk_wa_item
	 * @return <日历天,<人员PK,日薪>>
	 */
	public synchronized Map<String, Map<String, Double>> get(String pk_loginuser, String pk_org, String pk_item_group,
			String pk_wa_item) {
		checkAndbuildStruct(pk_loginuser, pk_org, pk_item_group, pk_wa_item, null, null);
		return cacheMap.get(pk_loginuser).get(pk_org).get(pk_item_group).get(pk_wa_item);

	}

	/**
	 * 按用户清理日薪数据
	 * @param pk_loginUser
	 */
	public void clearByUser(String pk_loginUser) {
		synchronized(this){
			cacheMap.put(pk_loginUser, null);
		}
		System.gc();
	}
	
	
	/**
	 * 
	 * @param pk_loginuser
	 * @param pk_org
	 * @param pk_item_group
	 * @param pk_wa_item
	 * @return <公共薪资项目,<日历天,<人员PK,日薪>>>
	 */
/*	public synchronized Map<String, Map<String, Map<String, Double>>> get(String pk_loginuser, String pk_org,
			String pk_item_group) {
		checkAndbuildStruct(pk_loginuser, pk_org, pk_item_group, null, null, null);
		return cacheMap.get(pk_loginuser).get(pk_org).get(pk_item_group);

	}*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
