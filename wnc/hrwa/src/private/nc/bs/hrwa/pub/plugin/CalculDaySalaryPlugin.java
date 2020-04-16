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
 * @desc 日薪计算后台任务
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

	public static final String KEY_CHECKRANGE = "checkrange";//數據檢查範圍
	public static final String KEY_RESERVED = "reserved";//日薪數據保留時間

	@Override
	public PreAlertObject executeTask(BgWorkingContext bgwc)
			throws BusinessException {
		//tank 2019年10月15日21:27:13 日薪后台任务暂停 实时计算
		
		StringBuffer sendmsg=new StringBuffer();
		sendmsg.append("日薪沒有需要計算的數據\n");
		PreAlertObject retObj = new PreAlertObject();
		retObj.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		retObj.setReturnObj(sendmsg.toString());
		return retObj;
		/*
		
		// 數據檢查範圍
		int checkrange = 0;
		//日薪數據保留時間
		int reserved=31;
		// 人力資源組織
		String pk_hrorg = bgwc.getPk_orgs()[0];
		// 计算日期
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
			throw new BusinessException("日薪檢查範圍最大不允許超過7天");
		}
		if(reserved<DaySalaryEnum.MINRESERVE||reserved>DaySalaryEnum.MAXRESERVE){
			throw new BusinessException("後台日薪數據保留時間最長不超過90天，最少不小於31天");
		}
		Logger.error("******進入：日薪計算 nc.bs.hrwa.pub.plugin.CalculDaySalaryPlugin*************************");
		long t1=System.currentTimeMillis();
		
		IWadaysalaryService waService=NCLocator.getInstance().lookup(IWadaysalaryService.class);
		waService.calculSalaryByHrorg(pk_hrorg, calculDate);//計算當日日薪
		waService.deleteDaySalary(pk_hrorg, calculDate, reserved);//刪除未在保留期間的數據
		waService.checkDaySalaryAndCalculSalary(pk_hrorg, calculDate, checkrange);//檢查日薪計算結果
		long t2=System.currentTimeMillis();
		Logger.error("******結束：日薪計算 nc.bs.hrwa.pub.plugin.CalculDaySalaryPlugin*************************");
		Logger.error("耗時："+(t2-t1)+"ms");
		OrgVO orgVO=(OrgVO) getDao().retrieveByPK(OrgVO.class, pk_hrorg);
		StringBuffer sendmsg=new StringBuffer();
		sendmsg.append("日薪計算執行成功\n");
		sendmsg.append("組        織："+orgVO.getName()+"("+orgVO.getCode()+")\n");
		sendmsg.append("計算日期："+calculDate.toStdString()+"\n");
		sendmsg.append("耗       時："+(t2-t1)+"ms\n");
		sendmsg.append("完成時間："+new UFDateTime().toString());
		PreAlertObject retObj = new PreAlertObject();
		retObj.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		retObj.setMsgTitle("日薪計算執行結果");
		retObj.setReturnObj(sendmsg.toString());
		return retObj;*/
	}
}
