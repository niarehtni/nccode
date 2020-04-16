package nc.itf.hr.wa;

import java.util.HashMap;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.adjust.AdjustWadocVO;
import nc.vo.wa.adjust.PsnappaproveBVO;
import nc.vo.wa.grade.AggWaGradeVO;
import nc.vo.wa.grade.WaCriterionVO;
import nc.vo.wa.grade.WaGradeVO;
import nc.vo.wa.grade.WaGradeVerVO;
import nc.vo.wa.grade.WaPsnhiBVO;

/**
 * 薪资标准设置应用服务<BR>
 * <BR>
 * 
 * @author: xuhw
 * @date: 2009-11-10
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public interface IWaGradeService {
	/**
	 * 删除
	 * 
	 * @author xuhw on 2009-11-11
	 * @param vo
	 * @throws BusinessException
	 */
	public abstract void deleteWaGradeVO(AggWaGradeVO vo) throws BusinessException;

	/**
	 * 新增
	 * 
	 * @author xuhw on 2009-11-11
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public abstract AggWaGradeVO insertWaGradeVO(AggWaGradeVO vo) throws BusinessException;

	/**
	 * 更新
	 * 
	 * @author xuhw on 2009-11-11
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public abstract AggWaGradeVO updateWaGradeVO(AggWaGradeVO vo) throws BusinessException;

	/**
	 * 更新薪资标准值表
	 * 
	 * @author xuhw on 2009-12-1
	 * @param wagradevo
	 * @param criterions
	 * @return
	 * @throws BusinessException
	 */
	public Object[] updateCriterionArray(WaGradeVO wagradevo, WaCriterionVO[] criterions) throws BusinessException;

	/**
	 * 更新薪资人员相关属性设置值表
	 * 
	 * @author xuhw on 2009-12-1
	 * @param wagradevo
	 * @param criterions
	 * @return
	 * @throws BusinessException
	 */
	public Object[] updateStdHiVOArray(WaGradeVO wagradevo, WaPsnhiBVO[] waStdHiVOs, int classType)
			throws BusinessException;

	/**
	 * 
	 * @param strPKPrmlv
	 * @param strPKSeclv
	 * @return
	 * @throws BusinessException
	 */
	public WaCriterionVO getCrierionVOByPrmSec(String strPKPrmlv, String strPKSeclv) throws BusinessException;

	/**
	 * 根據檢查日期對應版本取薪資標準
	 * 
	 * @param checkDate
	 * @param strPKPrmlv
	 * @param strPKSeclv
	 * @return
	 * @throws BusinessException
	 * 
	 * @author sunsx
	 * @since 2020-01-16
	 */
	public WaCriterionVO getCrierionVOByPrmSec(UFLiteralDate checkDate, String strPKPrmlv, String strPKSeclv)
			throws BusinessException;

	// end

	/**
	 * 
	 * @param AdjustWadocVO
	 *            []
	 * @return
	 * @throws BusinessException
	 */
	public HashMap<String, WaCriterionVO> getCrierionVOMapByPrmSec(AdjustWadocVO[] adjustWadocPsnInfoVOs)
			throws BusinessException;

	/**
	 * 
	 * @param PsnappaproveVO
	 *            []
	 * @return
	 * @throws BusinessException
	 */
	public HashMap<String, WaCriterionVO> getCrierionVOMapByPrmSec(PsnappaproveBVO[] psnappaproveBVOs, boolean isApprove)
			throws BusinessException;

	/**
	 * 处理薪资标准表数据
	 * 
	 * @param vervo
	 * @return
	 * @throws BusinessException
	 */
	public WaGradeVerVO processCriterionArray(WaGradeVO gradevo, WaGradeVerVO vervo) throws BusinessException;

	/**
	 * 复制功能-处理薪资标准表数据
	 * 
	 * @param gradevo
	 * @param vervo
	 * @return
	 * @throws BusinessException
	 */
	public WaGradeVerVO processCriterionArray4Copy(WaGradeVO gradevo, WaGradeVerVO vervo) throws BusinessException;

	/**
	 * 删除薪资标准表版本数据，连带删除对应薪资标准表
	 * 
	 * @param vervo
	 * @return
	 * @throws BusinessException
	 */
	public void deleteGradeVerVO(WaGradeVerVO vervo) throws BusinessException;

	/**
	 * 查询薪资标准版本信息 BY gradePK
	 */
	public WaGradeVerVO[] queryGradeVerByGradePK(String strGradePk) throws BusinessException;

	/**
	 * 查询生效的薪资标准版本信息 BY gradePK
	 */
	public WaGradeVerVO queryEffectGradeVerByGradePK(String strGradePk) throws BusinessException;

	/**
	 * 跟据薪资标准类别主键查询薪资标准版本的最大版本号
	 * 
	 * @param strPKWaGrd
	 * @return
	 * @throws BusinessException
	 */
	public UFDouble getMaxVerNum(String strPKWaGrd) throws BusinessException;

	/**
	 * 删除薪资标准版本记录根据PK
	 * 
	 * @param strGradeVerPK
	 * @throws BusinessException
	 */
	public void deleteGradeVerByPk(String strGradeVerPK) throws BusinessException;

	/**
	 * 薪资标准类别增加级别档别时候的验证
	 * 
	 * @param gradeVO
	 * @throws BusinessException
	 */
	public void validatorHasGradeVer(String strGradePK) throws BusinessException;

	/**
	 * 根据薪资标准类别PK查询生效的薪资标准表集合
	 * 
	 * @param strPKGrade
	 * @return
	 * @throws BusinessException
	 */
	public WaCriterionVO[] getEffectCrierionsVOByGradePK(String strPKGrade) throws BusinessException;

	/**
	 * 校验薪资标准类别是否被后续业务引用（版本生效标志是否可以为空校验）
	 * 
	 * @param strGradepk
	 * @throws BusinessException
	 */
	public String validateGradeHaveReferenceByBusiness(String strGradepk) throws BusinessException;

	/**
	 * 校验薪资标准类别是否有生效版本
	 * 
	 * @param strGradepk
	 * @throws BusinessException
	 */
	public boolean validateEffectVersion(String strGradepk) throws BusinessException;

	/**
	 * 删除现有的级别人员属性和档别人员属性(修改级别人员属性和档别人员属性)
	 * 
	 * @param strGradepk
	 * @throws BusinessException
	 */
	public void deleteWaPsnhiByPK(String strGradepk, int classType) throws BusinessException;

	/**
	 * 删除历史未生效的版本信息(修改时：是否多档发生变化，级别和档别新增或者删除时)
	 * 
	 * @param strGradepk
	 * @throws BusinessException
	 */
	public void deleteVersionByGradePK(String strGradepk) throws BusinessException;

	/**
	 * 插入人员属性设置数据(复制保存)
	 * 
	 * @param strGradepk
	 * @throws BusinessException
	 */
	public void insertCopyStdHiVOArray(WaPsnhiBVO[] waStdHiVOs) throws BusinessException;

}
