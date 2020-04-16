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
 * @desc ��н�����̨����
 *
 */
public class CalculDaySalaryPlugin implements IBackgroundWorkPlugin{
	
	private BaseDAO dao;

	public BaseDAO getDao() {
		if (dao == null) {
			dao = new BaseDAO();
		}
		return dao;
	}

	public static final String KEY_CHECKRANGE = "checkrange";//�����z�鹠��
	public static final String KEY_RESERVED = "reserved";//��н���������r�g

	@Override
	public PreAlertObject executeTask(BgWorkingContext bgwc)
			throws BusinessException {
		//tank 2019��10��15��21:27:13 ��н��̨������ͣ ʵʱ����
		
		StringBuffer sendmsg=new StringBuffer();
		sendmsg.append("��н�]����ҪӋ��Ĕ���\n");
		PreAlertObject retObj = new PreAlertObject();
		retObj.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		retObj.setReturnObj(sendmsg.toString());
		return retObj;
		/*
		
		// �����z�鹠��
		int checkrange = 0;
		//��н���������r�g
		int reserved=31;
		// �����YԴ�M��
		String pk_hrorg = bgwc.getPk_orgs()[0];
		// ��������
		UFLiteralDate calculDate=new UFLiteralDate();

		LinkedHashMap<String, Object> keyMap = bgwc.getKeyMap();
		Set<String> keys = keyMap.keySet();

		for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
			String key = iter.next();
			if (key.equalsIgnoreCase(KEY_CHECKRANGE)) {
				if (key != null && !key.toString().equals("")) {
					Object obj = keyMap.get(key);
					if (obj != null) {
						checkrange =Integer.valueOf(obj.toString());
					}
				}
			}else if (key.equalsIgnoreCase(KEY_RESERVED)) {
				if (key != null && !key.toString().equals("")) {
					Object obj = keyMap.get(key);
					if (obj != null) {
						reserved =Integer.valueOf(obj.toString());
					}
				}
			}
		}
		if(checkrange>DaySalaryEnum.MAXCHECKRANGE){
			throw new BusinessException("��н�z�鹠��������S���^7��");
		}
		if(reserved<DaySalaryEnum.MINRESERVE||reserved>DaySalaryEnum.MAXRESERVE){
			throw new BusinessException("��̨��н���������r�g���L�����^90�죬���ٲ�С�31��");
		}
		Logger.error("******�M�룺��нӋ�� nc.bs.hrwa.pub.plugin.CalculDaySalaryPlugin*************************");
		long t1=System.currentTimeMillis();
		
		IWadaysalaryService waService=NCLocator.getInstance().lookup(IWadaysalaryService.class);
		waService.calculSalaryByHrorg(pk_hrorg, calculDate);//Ӌ�㮔����н
		waService.deleteDaySalary(pk_hrorg, calculDate, reserved);//�h��δ�ڱ������g�Ĕ���
		waService.checkDaySalaryAndCalculSalary(pk_hrorg, calculDate, checkrange);//�z����нӋ��Y��
		long t2=System.currentTimeMillis();
		Logger.error("******�Y������нӋ�� nc.bs.hrwa.pub.plugin.CalculDaySalaryPlugin*************************");
		Logger.error("�ĕr��"+(t2-t1)+"ms");
		OrgVO orgVO=(OrgVO) getDao().retrieveByPK(OrgVO.class, pk_hrorg);
		StringBuffer sendmsg=new StringBuffer();
		sendmsg.append("��нӋ����гɹ�\n");
		sendmsg.append("�M        ����"+orgVO.getName()+"("+orgVO.getCode()+")\n");
		sendmsg.append("Ӌ�����ڣ�"+calculDate.toStdString()+"\n");
		sendmsg.append("��       �r��"+(t2-t1)+"ms\n");
		sendmsg.append("��ɕr�g��"+new UFDateTime().toString());
		PreAlertObject retObj = new PreAlertObject();
		retObj.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		retObj.setMsgTitle("��нӋ����нY��");
		retObj.setReturnObj(sendmsg.toString());
		return retObj;*/
	}
}
