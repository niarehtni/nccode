package nc.impl.hrwa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.vo.hrwa.wadaysalary.DaySalaryVO;

/**
 * ��н����
 * @author tank
 * @Discript ���е���н���ݶ��������浽����� 
 * @Warnning 1.������û����в�������,���г���ڶ�Ӧ����,ͬ�û����Ե��߳�����н��,�����ϲ����в�������
 * 			2.ʹ��ԭ��,ʹ��ǰ��������²���,���̶ȱ���map,set�Ƚ���resize���� 
 * 2019��10��17��20:20:38
 */
public class DaySalaryCache {
	/**
	 * ��������-ԭ�����̶ȱ���resize
	 */
	//map���� ����
	public final static double LOADER_FACTOR = 0.75;
	//������Դ��֯�������
	public final static int MAX_ORG_NUM = 20;
	//���ͬʱ������н�û���
	public final static int MAX_ONLINE_NUM = MAX_ORG_NUM*2;
	//н�ʷ����������
	public final static int MAX_ITEM_GROUP_NUM = 10;
	//ÿ��н�ʷ����ڹ���н����Ŀ�����ֵ
	public final static int MAX_ITEM_IN_GROUP_NUM = 10;
	//������ȡ���ֵ(ʱ����) 31+14
	public final static int MAX_DAY_NUM = 45;
	//ÿ����֯��Ա���ֵ
	public final static int MAX_PSN_IN_ORG = 2500;

	private DaySalaryCache() {
	}
	
	private static DaySalaryCache ins = new DaySalaryCache();
	
	public static DaySalaryCache getInstance(){
		return ins;
	}
	/**
	 * ������->н�ʼ���ǰ����,н�ʼ�������
	 * <pk_login_user,<pk_org,<pk_item_group,<pk_waitem,<calenderStr,<pk_psndoc,double>>>>>> cacheMap
	 * <��ǰ��¼�û�PK,<��֯,<н����Ŀ����,<����н����Ŀ,<������,<��ԱPK,��н>>>>>> ����Map
	 * ��㲻ͬuser֮������̰߳�ȫ����,���µĲ����������̰߳�ȫ����(ͬ��user)
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
	 * put���� map��ν�������
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
	 * ��нvo���뻺��
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
	 * ��������ṹ
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
							//�Ѿ��д˽ṹ
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
	 * @return <������,<��ԱPK,��н>>
	 */
	public synchronized Map<String, Map<String, Double>> get(String pk_loginuser, String pk_org, String pk_item_group,
			String pk_wa_item) {
		checkAndbuildStruct(pk_loginuser, pk_org, pk_item_group, pk_wa_item, null, null);
		return cacheMap.get(pk_loginuser).get(pk_org).get(pk_item_group).get(pk_wa_item);

	}

	/**
	 * ���û�������н����
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
	 * @return <����н����Ŀ,<������,<��ԱPK,��н>>>
	 */
/*	public synchronized Map<String, Map<String, Map<String, Double>>> get(String pk_loginuser, String pk_org,
			String pk_item_group) {
		checkAndbuildStruct(pk_loginuser, pk_org, pk_item_group, null, null, null);
		return cacheMap.get(pk_loginuser).get(pk_org).get(pk_item_group);

	}*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
