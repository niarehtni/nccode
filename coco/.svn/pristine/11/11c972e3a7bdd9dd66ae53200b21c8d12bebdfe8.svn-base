package nc.bs.hrsms.hi;

import nc.bs.hrss.pub.tool.SessionUtil;
import nc.vo.hrss.pub.SessionBean;


public class hrsmsUtil {
	
	
	/**
	 * 返回被操作人员主键
	 * 
	 * @return
	 */
	public static String getBoOperatePsndocPK() {
		SessionBean session = SessionUtil.getSessionBean();
		String beOperatedPsndocPk = (String) session.getExtendAttributeValue(hrsmsConstant.BEOPERATED_PSNDOC_PK);
		return beOperatedPsndocPk;
	}
	
	/**
	 * 返回被操作人员主键
	 */
	public static void setBoOperatePsndocPK(String beOperatedPsndocPk) {
		SessionBean session = SessionUtil.getSessionBean();
		session.setExtendAttribute(hrsmsConstant.BEOPERATED_PSNDOC_PK,beOperatedPsndocPk);
	}
}
