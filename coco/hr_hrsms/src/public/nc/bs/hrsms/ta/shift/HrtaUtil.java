package nc.bs.hrsms.ta.shift;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.itf.hrsms.ta.ITBMPsndocQryService;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psndoc.TBMPsndocVO;

public class HrtaUtil {

	public static void checkTbmPsndoc(String pk_psndoc,UFLiteralDate[]  beginDate,UFLiteralDate[] endDate){
		String pk_dept = SessionUtil.getPk_mng_dept();
		ITBMPsndocQryService service;
			service = NCLocator.getInstance().lookup(ITBMPsndocQryService.class);
		for(int i = 0;i<beginDate.length;i++){
			TBMPsndocVO tbmPsndocVO;
			try {
				tbmPsndocVO = service.queryByPsndocDeptAndDate(pk_psndoc, pk_dept, beginDate[i], endDate[i]);
			} catch (BusinessException e) {
				throw new LfwRuntimeException(e.getMessage(),e.getCause());
			}
			if(null == tbmPsndocVO){
				throw new LfwRuntimeException("此人在"+beginDate[i]+"至"+endDate[i]+"没有在该部门");
			}
		}
}
}
