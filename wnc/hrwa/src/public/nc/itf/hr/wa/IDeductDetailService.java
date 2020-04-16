package nc.itf.hr.wa;

import java.util.List;

import nc.vo.hi.psndoc.DeductDetailsVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginVO;

public interface IDeductDetailService {
	/**
	 * 点击审核按钮回写法院强制扣款明细到子集
	 * 
	 * @param isRangeAll
	 * @param whereCondition
	 * @param waLoginVO
	 * @throws BusinessException 
	 */
	void rollbacktodeductdetail(WaLoginVO waLoginVO, String whereCondition, Boolean isRangeAll) throws BusinessException;
	
	/**
	 * 取消本薪資方案和期間的扣款明細(法扣)
	 * @param pk_wa_class
	 * @param waPeriod eg: 201909
	 * @throws BusinessException
	 */
	void cancelDeductdetail(String pk_wa_class,String waPeriod) throws BusinessException;
	/**
	 * 更新债券档案,法扣设定的相关信息
	 * @param ddvList 变动的扣款信息
	 * @param isUpdatenull 是否更新没有扣款记录的债券档案
	 * @throws BusinessException
	 */
	void updateDebtfile(List<DeductDetailsVO> ddvList, boolean isUpdatenull) throws BusinessException;
}
