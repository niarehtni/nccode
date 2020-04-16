package nc.bs.hrwa.pub.plugin;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.pa.PreAlertReturnType;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.itf.hrwa.IWadaysalaryService;
import nc.vo.hrwa.wadaysalary.DaySalaryEnum;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
/**
 * 
 * @author ward
 * @date 2018��9��12��15:09:42
 * @desc ��н����ָ��Ӌ�㹠����̨�΄�
 *
 */
public class CalculDaySalaryWithRangePlugin implements IBackgroundWorkPlugin{
	
	private BaseDAO dao;

	public BaseDAO getDao() {
		if (dao == null) {
			dao = new BaseDAO();
		}
		return dao;
	}

	public static final String KEY_BEGINDATE = "begindate";//��н���㿪ʼ����
	public static final String KEY_ENDDATE ="enddate";//��нӋ��Y������

	@Override
	public PreAlertObject executeTask(BgWorkingContext bgwc)
			throws BusinessException {
		//tank 2019��10��15��21:27:13 ��н��̨������ͣ ʵʱ����

		StringBuffer sendmsg = new StringBuffer();
		sendmsg.append("��н�]����ҪӋ��Ĕ���\n");
		PreAlertObject retObj = new PreAlertObject();
		retObj.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		retObj.setReturnObj(sendmsg.toString());
		return retObj;
		
		/*
		// �_ʼ����
		UFLiteralDate begindate = null;
		// �Y������
		UFLiteralDate enddate = null;
		// �����YԴ�M��
		String pk_hrorg = bgwc.getPk_orgs()[0];

		LinkedHashMap<String, Object> keyMap = bgwc.getKeyMap();
		Set<String> keys = keyMap.keySet();

		for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
			String key = iter.next();
			if (key.equalsIgnoreCase(KEY_BEGINDATE)) {
				if (key != null && !key.toString().equals("")) {
					Object obj = keyMap.get(key);
					if (obj != null) {
						begindate =new UFLiteralDate(obj.toString());
					}
				}
			}else if (key.equalsIgnoreCase(KEY_ENDDATE)) {
				if (key != null && !key.toString().equals("")) {
					Object obj = keyMap.get(key);
					if (obj != null) {
						enddate =new UFLiteralDate(obj.toString());
					}
				}
			}
		}
		int range=UFLiteralDate.getDaysBetween(begindate, enddate)+1;
		if(range>DaySalaryEnum.MAXRANGE){
			throw new BusinessException("ָ����нӋ�㹠������ܳ��^31��");
		}
		
		Logger.error("******�M�룺��нӋ�� nc.bs.hrwa.pub.plugin.CalculDaySalaryWithRangePlugin*************************");
		long t1=System.currentTimeMillis();
		IWadaysalaryService waService=NCLocator.getInstance().lookup(IWadaysalaryService.class);
		for (int i = 0; i < range; i++) {
			UFLiteralDate calculDate=begindate.getDateAfter(i);
			waService.calculSalaryByHrorg(pk_hrorg, calculDate);
		}
		long t2=System.currentTimeMillis();
		Logger.error("******�Y������нӋ�� nc.bs.hrwa.pub.plugin.CalculDaySalaryWithRangePlugin*************************");
		Logger.error("�ĕr��"+(t2-t1)+"ms");
		OrgVO orgVO=(OrgVO) getDao().retrieveByPK(OrgVO.class, pk_hrorg);
		StringBuffer sendmsg=new StringBuffer();
		sendmsg.append("��нӋ����гɹ�\n");
		sendmsg.append("�M        ����"+orgVO.getName()+"("+orgVO.getCode()+")\n");
		sendmsg.append("Ӌ�㹠����"+begindate.toStdString()+" ��  "+enddate.toStdString()+"\n");
		sendmsg.append("��       �r��"+(t2-t1)+"ms\n");
		sendmsg.append("��ɕr�g��"+new UFDateTime().toString());
		PreAlertObject retObj = new PreAlertObject();
		retObj.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		retObj.setMsgTitle("��нӋ����нY��");
		retObj.setReturnObj(sendmsg.toString());
		return retObj;*/
	}
}
