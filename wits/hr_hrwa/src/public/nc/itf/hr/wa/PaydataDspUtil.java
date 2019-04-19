package nc.itf.hr.wa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.paydata.ICommonAlterName;
import nc.vo.wa.paydata.WaPaydataDspVO;
import nc.vo.wa.pub.WaLoginContext;

import org.apache.commons.lang.ArrayUtils;

/**
 * н�ʵ�����ʾ���õĹ�����
 * @author erl
 *
 */
public class PaydataDspUtil {
	//н�ʷ��Ÿ���������Ŀ
	public static final String personalDsp = "1";
	//н�ʷ���ͨ��������Ŀ
	public static final String commonDsp = "0";
	//��ְ��нͨ��������Ŀ
	public static final String commonDsp4payleave = "2";
	//��ְ��н����������Ŀ
	public static final String PersonalDsp4payleave  = "3";
	
	 
	//н�ʵ���Ĭ����ʾ���ֶμ�˳��
	public final static String[] defaultShowColKey = new String[]{
		ICommonAlterName.PSNCODE/*"��Ա���� */,
		ICommonAlterName.CLERKCODE/*Ա����*/,
		DataVO.PSNNAME/*����*/,
		ICommonAlterName.PLSNAME/*��Ա���*/,
		ICommonAlterName.ORGNAME/*��ְ��֯*/,
		// 2015-07-30 zhousze NCdp205099799 н�ʷ��Ž�����Ӳ�����֯������ begin
		ICommonAlterName.FINANCEORG/* ������֯ */,
		ICommonAlterName.FINANCEDEPT/* ������ */,
		//zhaochxs˰����˰�걨��֯
		"taxorgname",/*��˰�걨��֯*/
		ICommonAlterName.LIABILITYORG/* �ɱ����� */,
		ICommonAlterName.LIABILITYDEPT/* �ɱ����� */,
		// end
		ICommonAlterName.DEPTNAME/*����*/,
		ICommonAlterName.POSTNAME/*��λ*/,
		DataVO.TAXTYPE,/*��˰��ʽ*/
		DataVO.CACULATEFLAG/*�����־*/,
		DataVO.CHECKFLAG/*��˱�־*/
	};
	
	//��ְ��нĬ����ʾ���ֶμ�˳��
		public final static String[] defaultShowColKey4PayLeave = new String[]{
			ICommonAlterName.PSNCODE/*"��Ա���� */,
			ICommonAlterName.CLERKCODE/*Ա����*/,
			DataVO.PSNNAME/*����*/,
			ICommonAlterName.PLSNAME/*��Ա���*/,
			ICommonAlterName.ORGNAME/*��ְ��֯*/,
			
			// 2015-11-03 xiejie3 ��ְ��н��ʾ������Ӳ�����֯������ begin
			ICommonAlterName.FINANCEORG/* ������֯ */,
			ICommonAlterName.FINANCEDEPT/* ������ */,
			//zhaochxs˰����˰�걨��֯
			"taxorgname",/*��˰�걨��֯*/
			ICommonAlterName.LIABILITYORG/* �ɱ����� */,
			ICommonAlterName.LIABILITYDEPT/* �ɱ����� */,
			// end
			
			ICommonAlterName.DEPTNAME/*����*/,
			ICommonAlterName.POSTNAME/*��λ*/,
			DataVO.TAXTYPE,/*��˰��ʽ*/
			DataVO.CACULATEFLAG/*�����־*/,
			DataVO.CHECKFLAG/*��˱�־*/,
			DataVO.PAYFLAG/*���ű�־*/,
			DataVO.LEAVEDATE/*��ְ����*/,
			DataVO.CPAYDATE/*��������*/,
			DataVO.VPAYCOMMENT/*����ԭ��*/
		};
	
	/**
	 * ��getColMap()��ѯ���Ľ����ת����List<WaPayfileDspVO>
	 * 
	 * @param colMap
	 * @return
	 */
	public static List<WaPaydataDspVO> queryDefaultDsp() {
		//��������ʾ������
		Map<String,String> showNameMap = initShowNameMap();
		List<WaPaydataDspVO> dspList = new ArrayList<WaPaydataDspVO>();
		for(int i=0;i<defaultShowColKey.length;i++){
			WaPaydataDspVO vo = new WaPaydataDspVO();
			vo.setItem_key(defaultShowColKey[i]);
			vo.setItemName(showNameMap.get(defaultShowColKey[i]));
			vo.setBshow(UFBoolean.TRUE);
			vo.setIsWaItem(UFBoolean.FALSE);
			dspList.add(vo);
		}
		
		return dspList;
	}
	
	
	/**
	 * ��getColMap()��ѯ���Ľ����ת����List<WaPayfileDspVO>
	 * 
	 * @param colMap
	 * @return
	 */
	public static List<WaPaydataDspVO> queryDefaultDsp4PayLeave() {
		//��������ʾ������
		Map<String,String> showNameMap4Payleave = initShowNameMap4PayLeave();
		List<WaPaydataDspVO> dspList = new ArrayList<WaPaydataDspVO>();
		for(int i=0;i<defaultShowColKey4PayLeave.length;i++){
			WaPaydataDspVO vo = new WaPaydataDspVO();
			vo.setItem_key(defaultShowColKey4PayLeave[i]);
			vo.setItemName(showNameMap4Payleave.get(defaultShowColKey4PayLeave[i]));
			vo.setBshow(UFBoolean.TRUE);
			vo.setIsWaItem(UFBoolean.FALSE);
			vo.setDisplayseq(i);
			dspList.add(vo);
		}
		
		return dspList;
	}
	
	
	/**
	 * �ѹ���н����Ŀת����н�ʷ��Žڵ����ʾ������Ŀ
	 * @param itemArray
	 * @return
	 */
	public static List<WaPaydataDspVO> convertWaItemVO(WaItemVO[] itemArray){
		List<WaPaydataDspVO> dspList = new ArrayList<WaPaydataDspVO>();
		if(!ArrayUtils.isEmpty(itemArray)){
			for(int i=0;i<itemArray.length;i++){
				dspList.add(convertWaItemVO(itemArray[i]));
			}
		}
		return dspList;
	}
	
	/**
	 * ��������ʾ˳��֮�󣬷��������¼ӵ���Ŀ����Ҫ���
	 * @param dspList
	 * @param itemArray
	 */
	public static void addNewlyDsiplayItem(List<WaPaydataDspVO> dspList,
			WaItemVO[] itemArray) {
		if (ArrayUtils.isEmpty(itemArray) || dspList == null) {
			return;
		}
		Map<String, String> dspItemMap = new HashMap<String, String>();
		for (WaPaydataDspVO dspVO : dspList) {
			if (dspVO.getIsWaItem().booleanValue()) {
				dspItemMap.put(dspVO.getItem_key(), dspVO.getItem_key());
			}
		}
		for (int i = 0; i < itemArray.length; i++) {
			if(!dspItemMap.containsKey(itemArray[i].getItemkey())){
				dspList.add(convertWaItemVO(itemArray[i]));
			}
		}

	}
	
	
//	20150728 xiejie3 �����ϲ�NCdp205395630,�˷���Ŀǰû���õ�,�����ڲ���,����д������,���Ժ���,��Ա�������ţ���Ա���ı䶯��н�ʵ�������ͬ��.begin
	/**
	 * ��������ʾ˳��֮�󣬷��������¼ӵ���Ŀ����Ҫ���
	 * @param dspList
	 * @param itemArray
	 */
	public static void addNewlyDsiplayItem4Fix(List<WaPaydataDspVO> dspList) {
		Map<String, String> dspItemMap = new HashMap<String, String>();
		for (WaPaydataDspVO dspVO : dspList) {
			if (!dspVO.getIsWaItem().booleanValue()) {
				dspItemMap.put(dspVO.getItem_key(), dspVO.getItem_key());
			}
		}
		
		if (!dspItemMap.containsKey(ICommonAlterName.FINANCEORG)) {
			WaPaydataDspVO dspVO = new WaPaydataDspVO();
			dspVO.setItem_key(ICommonAlterName.FINANCEORG);
			dspVO.setItemName(ResHelper.getString("60130payfile","160130payfile0026")/*@res "������֯"*/);
			dspVO.setBshow(UFBoolean.TRUE);
			dspVO.setIsWaItem(UFBoolean.FALSE);
			dspList.add(dspVO);
		}
		
		if (!dspItemMap.containsKey(ICommonAlterName.FINANCEDEPT)) {
			WaPaydataDspVO dspVO = new WaPaydataDspVO();
			dspVO.setItem_key(ICommonAlterName.FINANCEDEPT);
			dspVO.setItemName(ResHelper.getString("60130payfile","160130payfile0023")/*@res "������"*/);
			dspVO.setBshow(UFBoolean.TRUE);
			dspVO.setIsWaItem(UFBoolean.FALSE);
			dspList.add(dspVO);
		}
		
		if (!dspItemMap.containsKey(ICommonAlterName.LIABILITYORG)) {
			WaPaydataDspVO dspVO = new WaPaydataDspVO();
			dspVO.setItem_key(ICommonAlterName.LIABILITYORG);
			dspVO.setItemName(ResHelper.getString("60130payfile","160130payfile0024")/*@res "�ɱ�����"*/);
			dspVO.setBshow(UFBoolean.TRUE);
			dspVO.setIsWaItem(UFBoolean.FALSE);
			dspList.add(dspVO);
		}
		
		if (!dspItemMap.containsKey(ICommonAlterName.LIABILITYDEPT)) {
			WaPaydataDspVO dspVO = new WaPaydataDspVO();
			dspVO.setItem_key(ICommonAlterName.LIABILITYDEPT);
			dspVO.setItemName(ResHelper.getString("60130payfile","160130payfile0025")/*@res "�ɱ�����"*/);
			dspVO.setBshow(UFBoolean.TRUE);
			dspVO.setIsWaItem(UFBoolean.FALSE);
			dspList.add(dspVO);
		}

	}
//	end
	
	
	
	
	
	private static WaPaydataDspVO convertWaItemVO(WaItemVO itemVO){
		WaPaydataDspVO dspVO = new WaPaydataDspVO();
		dspVO.setItem_key(itemVO.getItemkey());
		dspVO.setItemName(itemVO.getMultilangName());
		//��ʾ���Ӧ����Ȩ�����ã����޸�н����ĿȨ�޺����޸ġ�
		dspVO.setBshow(UFBoolean.TRUE);
		dspVO.setIsWaItem(UFBoolean.TRUE);
		return dspVO;
	}
	
	/**
	 * н�ʷ�����ʾ���ã�����ҳ����ʾ�Ķ�������
	 * @param dspList
	 * @return
	 */
	//2014/05/23 shenliangcΪ���н�ʷ��Žڵ���ʾ���öԻ�������Ŀ�����뱾�ڼ䷢����Ŀ���Ʋ�ͬ��������޸ġ�
	//���Ʋ�ͬ����ԭ���ǲ�ѯ��Ŀ�����߼�û���������ڼ����ƣ�ȫ��ȡ������ʼ�ڼ�ķ�����Ŀ���ơ�
	public static List<WaPaydataDspVO> setPaydataDisplayName(List<WaPaydataDspVO> dspList, WaLoginContext context) {
		//��������ʾ������
		//��������ʾ������
		Map<String,String> showNameMap = initShowNameMap();
		IClassItemQueryService citemService = NCLocator.getInstance().lookup(IClassItemQueryService.class);
		
		//20150918 shenliangc ����Ϻ�ҽҩн�ʷ���ˢ��Ч�����⡣������ѯ������Ŀ��Ϣ������SQL������
		String condition = "pk_wa_class = '" + context.getPk_wa_class() + "' and cyear = '" + context.getCyear() + "' and cperiod = '" + context.getCperiod() + "' ";
		WaClassItemVO[] classItemVOs = null;
		try {
			classItemVOs = citemService.queryClassItemByCondition(context, condition);
		} catch (BusinessException e1) {
			Logger.error(e1.getMessage(), e1);
		}
		HashMap<String,String> classItemNameMap = new HashMap<String,String>();
		if(!ArrayUtils.isEmpty(classItemVOs)){
			for(WaClassItemVO classItemVO : classItemVOs){
				classItemNameMap.put(classItemVO.getItemkey(), classItemVO.getMultilangName());
			}
		}
		
		
		for (int i = 0; i < dspList.size(); i++) {
			WaPaydataDspVO dspVO = dspList.get(i);
			if(dspVO.getIsWaItem().booleanValue() && !classItemNameMap.isEmpty()){
					//н�ʷ�������context.getPk_wa_class()������ȡ����Ϊ�˴����η�нÿ�η�����Ŀ���Ʋ�ͬ�������
					//������Ŀ���Ƹ��ݵ�ǰ����ѡ�е��ӷ�������������ڼ��ѯ������Ŀ��
//					WaClassItemVO itemVO = citemService.queryItemWithItemkeyAndPK_wa_class(dspVO.getItem_key(), context.getPk_wa_class(), context.getCyear(), context.getCperiod());
//					dspList.get(i).setItemName(itemVO.getMultilangName());
					dspList.get(i).setItemName(classItemNameMap.get(dspList.get(i).getItem_key()));
			}else{
				dspList.get(i).setItemName(showNameMap.get(dspList.get(i).getItem_key()));
			}
		}
		return dspList;
	}
	
	

	/**
	 * ��ְ��н��ʾ���ã�����ҳ����ʾ�Ķ�������
	 * @param dspList
	 * @return
	 */
	//2014/05/23 shenliangcΪ�����ְ��н�ڵ���ʾ���öԻ�������Ŀ�����뱾�ڼ䷢����Ŀ���Ʋ�ͬ��������޸ġ�
	//���Ʋ�ͬ����ԭ���ǲ�ѯ��Ŀ�����߼�û���������ڼ����ƣ�ȫ��ȡ������ʼ�ڼ�ķ�����Ŀ���ơ�
	public static List<WaPaydataDspVO> setPaydataDisplayName4PayLeave(List<WaPaydataDspVO> dspList, WaLoginContext context) {
		//��������ʾ������
		Map<String,String> showNameMap4Payleave = initShowNameMap4PayLeave();
//		IItemQueryService itemService = NCLocator.getInstance().lookup(IItemQueryService.class);
		IClassItemQueryService citemService = NCLocator.getInstance().lookup(IClassItemQueryService.class);
		
		String condition = "pk_wa_class = '" + context.getPk_wa_class() + "' and cyear = '" + context.getCyear() + "' and cperiod = '" + context.getCperiod() + "' ";
		WaClassItemVO[] classItemVOs = null;
		try {
			classItemVOs = citemService.queryClassItemByCondition(context, condition);
		} catch (BusinessException e1) {
			Logger.error(e1.getMessage(), e1);
		}
		HashMap<String,String> classItemNameMap = new HashMap<String,String>();
		if(!ArrayUtils.isEmpty(classItemVOs)){
			for(WaClassItemVO classItemVO : classItemVOs){
				classItemNameMap.put(classItemVO.getItemkey(), classItemVO.getMultilangName());
			}
		}
		
		for (int i = 0; i < dspList.size(); i++) {
			WaPaydataDspVO dspVO = dspList.get(i);
			if(dspVO.getIsWaItem().booleanValue()&& !classItemNameMap.isEmpty()){
//					WaItemVO itemVO = itemService.queryByItemkey(dspVO.getItem_key());
//					WaClassItemVO itemVO = citemService.queryItemWithItemkeyAndPK_wa_class(dspVO.getItem_key(), context.getPk_wa_class(), context.getCyear(), context.getCperiod());
					dspList.get(i).setItemName(classItemNameMap.get(dspList.get(i).getItem_key()));
			}else{
				dspList.get(i).setItemName(showNameMap4Payleave.get(dspList.get(i).getItem_key()));
			}
		}
		return dspList;
	}
	
	
	
	
	private static Map<String,String> initShowNameMap(){
		Map<String,String> nameMap = new HashMap<String, String>();
		nameMap.put(ICommonAlterName.PSNCODE, ResHelper.getString("common", "UC000-0000147")/*@res "��Ա���� */);
		nameMap.put(ICommonAlterName.CLERKCODE, ResHelper.getString("60130paydata","060130paydata0503")/*@res "Ա����"*/);
		nameMap.put(DataVO.PSNNAME, ResHelper.getString("60130paydata","060130paydata0504")/*@res "����"*/);
		nameMap.put(ICommonAlterName.PLSNAME, ResHelper.getString("60130paydata","060130paydata0505")/*@res "��Ա���"*/);
		nameMap.put(ICommonAlterName.ORGNAME, ResHelper.getString("60130paydata","060130paydata0506")/*@res "��ְ��֯"*/);
		// 2015-07-30 zhousze NCdp205099799 н�ʷ��Ž�����Ӳ�����֯������ begin
		nameMap.put(ICommonAlterName.FINANCEORG, ResHelper.getString("60130payfile", "160130payfile0026")/*@res ������֯ */);
		nameMap.put(ICommonAlterName.FINANCEDEPT, ResHelper.getString("60130payfile", "160130payfile0023")/*@res ������ */);
		nameMap.put(ICommonAlterName.LIABILITYORG, ResHelper.getString("60130payfile", "160130payfile0024")/*@res �ɱ����� */);
		nameMap.put(ICommonAlterName.LIABILITYDEPT, ResHelper.getString("60130payfile", "160130payfile0025")/*@res �ɱ����� */);
		// end
		nameMap.put(ICommonAlterName.DEPTNAME, ResHelper.getString("60130paydata","060130paydata0507")/*@res "����"*/);
		nameMap.put(ICommonAlterName.POSTNAME, ResHelper.getString("60130paydata","060130paydata0508")/*@res "��λ"*/);
		nameMap.put(DataVO.TAXTYPE, ResHelper.getString("60130paydata","060130paydata0509")/*@res "��˰��ʽ"*/);
		nameMap.put(DataVO.CACULATEFLAG, ResHelper.getString("60130paydata","060130paydata0510")/*@res "�����־"*/);
		nameMap.put(DataVO.CHECKFLAG, ResHelper.getString("60130paydata","060130paydata0511")/*@res "��˱�־"*/);
		nameMap.put("taxorgname", "��˰�걨��֯");
		return nameMap;
	}
	
	private static Map<String,String> initShowNameMap4PayLeave(){
		Map<String,String> nameMap = new HashMap<String, String>();
		nameMap.put(ICommonAlterName.PSNCODE, ResHelper.getString("common", "UC000-0000147")/*@res "��Ա���� */);
		nameMap.put(ICommonAlterName.CLERKCODE, ResHelper.getString("60130paydata","060130paydata0503")/*@res "Ա����"*/);
		nameMap.put(DataVO.PSNNAME, ResHelper.getString("60130paydata","060130paydata0504")/*@res "����"*/);
		nameMap.put(ICommonAlterName.PLSNAME, ResHelper.getString("60130paydata","060130paydata0505")/*@res "��Ա���"*/);
		nameMap.put(ICommonAlterName.ORGNAME, ResHelper.getString("60130paydata","060130paydata0506")/*@res "��ְ��֯"*/);
		
		// 2015-11-03 xiejie3 ��ְ��н��Ӳ�����֯������ begin
		nameMap.put(ICommonAlterName.FINANCEORG, ResHelper.getString("60130payfile", "160130payfile0026")/*@res ������֯ */);
		nameMap.put(ICommonAlterName.FINANCEDEPT, ResHelper.getString("60130payfile", "160130payfile0023")/*@res ������ */);
		nameMap.put(ICommonAlterName.LIABILITYORG, ResHelper.getString("60130payfile", "160130payfile0024")/*@res �ɱ����� */);
		nameMap.put(ICommonAlterName.LIABILITYDEPT, ResHelper.getString("60130payfile", "160130payfile0025")/*@res �ɱ����� */);
		// end
		
		nameMap.put(ICommonAlterName.DEPTNAME, ResHelper.getString("60130paydata","060130paydata0507")/*@res "����"*/);
		nameMap.put(ICommonAlterName.POSTNAME, ResHelper.getString("60130paydata","060130paydata0508")/*@res "��λ"*/);
		nameMap.put(DataVO.TAXTYPE, ResHelper.getString("60130paydata","060130paydata0509")/*@res "��˰��ʽ"*/);
		nameMap.put(DataVO.CACULATEFLAG, ResHelper.getString("60130paydata","060130paydata0510")/*@res "�����־"*/);
		nameMap.put(DataVO.CHECKFLAG, ResHelper.getString("60130paydata","060130paydata0511")/*@res "��˱�־"*/);
		nameMap.put(DataVO.PAYFLAG, ResHelper.getString("60130paydata","060130paydata0512")/*@res "���ű�־"*/);
		nameMap.put(DataVO.LEAVEDATE, ResHelper.getString("60130paydata","060130paydata0513")/*@res "��ְ����"*/);
		nameMap.put(DataVO.CPAYDATE, ResHelper.getString("60130paydata","060130paydata0514")/*@res "��������"*/);
		nameMap.put(DataVO.VPAYCOMMENT, ResHelper.getString("60130paydata","060130paydata0515")/*@res "����ԭ��"*/);
		nameMap.put("taxorgname", "��˰�걨��֯");
		
		return nameMap;
	}
	
}
