/**
 * 
 */
package nc.itf.hr.datainterface;

import java.util.Map;

import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hr.datainterface.AggHrIntfaceVO;
import nc.vo.hr.datainterface.HrIntfaceVO;
import nc.vo.hr.tools.pub.HRAggVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.datainterface.BonusOthBuckVO;
import nc.vo.wa.datainterface.MappingFieldVO;
import nc.vo.wa.datainterface.SalaryOthBuckVO;
import nc.vo.wa.paydata.DataVO;

/**
 * 批量维护 Service接口
 * 
 * @author xuanlt
 * 
 */
public interface IDataIOManageService {

	// /**
	// * 批量保存
	// * @param batchVO
	// * @return
	// * @throws BusinessException
	// */
	// BatchOperateVO batchSave(BatchOperateVO batchVO) throws
	// BusinessException;
	//

	HrIntfaceVO add(HRAggVO vo) throws BusinessException;

	HrIntfaceVO update(HRAggVO vo) throws BusinessException;

	// SuperVO[] updatePayfileVOs(SuperVO[] vos) throws BusinessException;
	/**
	 * 删除<br>
	 * 
	 * @param vo
	 */
	void delete(AggHrIntfaceVO vo) throws BusinessException;

	/**
	 * 增加<br>
	 * 
	 * @param vo
	 * @return
	 */
	AggHrIntfaceVO insert(AggHrIntfaceVO vo) throws BusinessException;

	/**
	 * 修改<br>
	 * 
	 * @param vo
	 * @return
	 */
	AggHrIntfaceVO update(AggHrIntfaceVO vo) throws BusinessException;

	/**
	 * 查询
	 * 
	 * @param context
	 * @param condition
	 * @param strOrderBy
	 * @return
	 * @throws BusinessException
	 */
	AggHrIntfaceVO[] queryByCondition(LoginContext context, String condition,
			String strOrderBy) throws BusinessException;

	// MOD {薪资、奖金明细代扣项导入} kevin.nie 2018-01-30 start
	<T extends SuperVO> T[] queryDataByConditon(String conditon,
			String[] psnPks, Class<T> classz) throws BusinessException;

	<T extends SuperVO> T[] insertPayDetail(T[] vos, T[] delVos)
			throws BusinessException;

	void importPayDataSD(DataVO[] SDVos, SalaryOthBuckVO[] SODVos)
			throws BusinessException;

	void importPayDataBD(DataVO[] BDVos, BonusOthBuckVO[] BODVos)
			throws BusinessException;

	Map<String, PsndocVO> queryPsnByOrgConditionn(String pk_org,
			String condition, boolean includeJob) throws BusinessException;

	Map<Integer, MappingFieldVO[]> qryImpFieldMappingVO(String conditon)
			throws BusinessException;
	// {薪资、奖金明细代扣项导入} kevin.nie 2018-01-30 end
}
