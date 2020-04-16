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
 * @date 2018年9月12日15:09:42
 * @desc 日薪计算指定計算範圍後台任務
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

	public static final String KEY_BEGINDATE = "begindate";//日薪计算开始日期
	public static final String KEY_ENDDATE ="enddate";//日薪計算結束日期

	@Override
	public PreAlertObject executeTask(BgWorkingContext bgwc)
			throws BusinessException {
		//tank 2019年10月15日21:27:13 日薪后台任务暂停 实时计算

		StringBuffer sendmsg = new StringBuffer();
		sendmsg.append("日薪沒有需要計算的數據\n");
		PreAlertObject retObj = new PreAlertObject();
		retObj.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		retObj.setReturnObj(sendmsg.toString());
		return retObj;
		
		/*
		// 開始日期
		UFLiteralDate begindate = null;
		// 結束日期
		UFLiteralDate enddate = null;
		// 人力資源組織
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
			throw new BusinessException("指定日薪計算範圍最大不能超過31天");
		}
		
		Logger.error("******進入：日薪計算 nc.bs.hrwa.pub.plugin.CalculDaySalaryWithRangePlugin*************************");
		long t1=System.currentTimeMillis();
		IWadaysalaryService waService=NCLocator.getInstance().lookup(IWadaysalaryService.class);
		for (int i = 0; i < range; i++) {
			UFLiteralDate calculDate=begindate.getDateAfter(i);
			waService.calculSalaryByHrorg(pk_hrorg, calculDate);
		}
		long t2=System.currentTimeMillis();
		Logger.error("******結束：日薪計算 nc.bs.hrwa.pub.plugin.CalculDaySalaryWithRangePlugin*************************");
		Logger.error("耗時："+(t2-t1)+"ms");
		OrgVO orgVO=(OrgVO) getDao().retrieveByPK(OrgVO.class, pk_hrorg);
		StringBuffer sendmsg=new StringBuffer();
		sendmsg.append("日薪計算執行成功\n");
		sendmsg.append("組        織："+orgVO.getName()+"("+orgVO.getCode()+")\n");
		sendmsg.append("計算範圍："+begindate.toStdString()+" 到  "+enddate.toStdString()+"\n");
		sendmsg.append("耗       時："+(t2-t1)+"ms\n");
		sendmsg.append("完成時間："+new UFDateTime().toString());
		PreAlertObject retObj = new PreAlertObject();
		retObj.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		retObj.setMsgTitle("日薪計算執行結果");
		retObj.setReturnObj(sendmsg.toString());
		return retObj;*/
	}
}
