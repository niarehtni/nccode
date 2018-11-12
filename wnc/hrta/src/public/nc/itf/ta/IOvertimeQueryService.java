package nc.itf.ta;

import java.util.List;
import java.util.Map;

import nc.itf.ta.bill.IApproveBillQueryService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.OvertimeCommonVO;
import nc.vo.ta.overtime.OvertimeRegVO;

/**
 * 加班服务组件,加班组件以外的所有组件都只用调用此组件来查询，包括考勤数据处理，包括日报计算
 * @author zengcheng
 *
 */
public interface IOvertimeQueryService extends IApproveBillQueryService<OvertimeRegVO,OvertimeCommonVO>{
	
	/**
	 * 查找加班单的归属日期
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	UFLiteralDate queryBelongToDate(OvertimeCommonVO vo)throws BusinessException;
	
	/**
	 * 查询人员范围、日期范围内的所有有效加班单据（审批通过+单据生成且生效+登记）
	 * @return，map，key是人员基本档案主键，value是map，key是加班单归属日，value是加班单数组
	 * @throws BusinessException
	 */
	Map<String, Map<UFLiteralDate, OvertimeRegVO[]>> queryOvertimeVOsByCondDate(String pk_org,String psnCond,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * 查询某个人员日期范围内的所有有效加班单据
	 * @param pk_org
	 * @param pk_psndoc
	 * @param beginDate
	 * @param endDate
	 * @return，map，key是加班单归属日，value是加班单数组
	 * @throws BusinessException
	 */
	Map<UFLiteralDate, OvertimeRegVO[]> queryOvertimeVOsByPsnDate(String pk_org,String pk_psndoc,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * 处理加班人员的所属业务单元，因为假日加班规则中使用的是业务单元不是hrorg
	 * @param vos
	 * @throws BusinessException
	 */
	public OvertimeCommonVO[] processJobOrg(OvertimeCommonVO[] vos)throws BusinessException;
	/**
	 * 自助使用，查询考勤期间的加班记录
	 * @param pk_org
	 * @param pk_psndoc
	 * @param tbmYear
	 * @param tbmMonth
	 * @param pk_timeitem
	 * @return
	 * @throws BusinessException
	 */
	public OvertimeRegVO[] queryRegByPsnAndDateScope(String pk_org,String pk_psndoc,String tbmYear,String tbmMonth,String pk_timeitem)throws BusinessException;
	
	Map<String, OvertimeRegVO[]> queryAllSuperVOByApproveDateByPsndocInSQLDate(String pk_org,String psndocInSQL,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	Map<String, OvertimeRegVO[]> queryAllSuperVOIncEffectiveByPsndocInSQLDateWithApprovedDate(String paramString1, String paramString2, UFLiteralDate paramUFLiteralDate1, UFLiteralDate paramUFLiteralDate2)
		    throws BusinessException;
//	// MOD(台灣新法令) ssx added on 2018-05-29
//	/**
//	 * 根據人員及時間範圍查詢加班類別
//	 * 
//	 * @param pk_org
//	 *            組織PK
//	 * @param pk_psndoc
//	 *            人員PK
//	 * @param overtimeDateTimes
//	 *            起迄日期列表
//	 * @param pk_timeitemcopy
//	 *            加班類別PK
//	 * 
//	 * @return 加班類別
//	 * @throws BusinessException
//	 */
	public String queryOvertimeTypeByPsnDates(String pk_org, String pk_psndoc, List<List<String>> overtimeDateTimes,
			String pk_timeitemcopy) throws BusinessException;
//	// end
}
