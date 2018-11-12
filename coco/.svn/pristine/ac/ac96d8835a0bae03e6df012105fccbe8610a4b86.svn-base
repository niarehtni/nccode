package nc.itf.hr.pub.query;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.trn.transmng.StapplyVO;

/**
 * @author changlei
 *
 * 为校验调动的人员
 *
 */
public interface ITransferApplyService {

	/**
	 * 由pk 确定一个人，查看这个人是否有开始日期大于等于生效日期
	 * 
	 * @param EffectDate
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	public boolean isHasPsnJobVOByEffectDate(UFLiteralDate EffectDate,String pk_psndoc)throws BusinessException;
	
	/**
	 * 查看此人是否有在途的调动单据  标准产品的调动申请 
	 * 
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	public StapplyVO[] onNcPrepareRute(String pk_psndoc)throws BusinessException;
	
	/**
	 * 查看此人是否有在途的调动单据    自助端的调动申请
	 * 
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	public StapplyVO[] onPortalPrepareRute(String pk_psndoc)throws BusinessException;
	
	/**
	 * 查看此人是否有在途的调动单据    自助端的调动申请
	 * 
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	public StapplyVO[] onPortalAndNcPrepareRute(String pk_psndoc)throws BusinessException;
}
