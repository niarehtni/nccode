package nc.itf.hrsms.ta;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psndoc.TBMPsndocVO;

public interface ITBMPsndocQryService {

	 /**
	  * 查询这个人在60天之内是否有考勤档案
	  * 
	 * @param paramString
	 * @param paramUFLiteralDate
	 * @return
	 * @throws BusinessException
	 */
	public abstract TBMPsndocVO queryByPsndocAndWithin60Days(String pk_psndoc, UFLiteralDate date)throws BusinessException;
	
	
	/**
	  * 查询某个人在在某个时间是否在这部门有考勤
	  * 
	 * @param paramString
	 * @param paramUFLiteralDate
	 * @return
	 * @throws BusinessException
	 */
	public abstract TBMPsndocVO queryByPsndocDeptAndDate(String pk_psndoc,String pk_dept, UFLiteralDate startDate, UFLiteralDate endDate)throws BusinessException;
}
