package nc.bs.hrsms.ta.sss.common;

import nc.bs.hrss.pub.exception.HrssException;
import nc.itf.ta.PeriodServiceFacade;
import nc.itf.ta.algorithm.IDateScope;
import nc.vo.pub.BusinessException;
/**
 * 校验考勤期间是否封存工具类
 * @author shaochj
 *
 */
public class ShopTaPeriodValUtils {

	/**
	 * 
	 * @param pk_org
	 * @param dateScopes
	 */
	public static final void getPeriodVal(String pk_org,IDateScope dateScopes[]){
		try {
			PeriodServiceFacade.checkDateScope(pk_org, dateScopes);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			new HrssException(e.getMessage()).alert();
			e.printStackTrace();
		}
	}
}
