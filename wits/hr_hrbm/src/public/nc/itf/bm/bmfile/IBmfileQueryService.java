/**
 * 
 */
package nc.itf.bm.bmfile;

import java.util.Map;

import nc.vo.bm.bmclass.AssignclsVO;
import nc.vo.bm.bmclass.BmClassVO;
import nc.vo.bm.data.BmDataVO;
import nc.vo.bm.pub.BmLoginContext;
import nc.vo.hr.comp.trn.PsnTrnVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.uif2.LoginContext;

/**
 * @author duyao
 * 
 */
public interface IBmfileQueryService {

	/**
	 * 
	 * @return
	 * @throws BusinessException
	 */
	BmDataVO[] queryByCondition(LoginContext context, String condition) throws BusinessException;

	/**
	 * 
	 * @return
	 * @throws BusinessException
	 */
	BmDataVO[] queryByCondition(BmLoginContext context, String condition, String orderby) throws BusinessException;

	/***************************************************************************
	 * 按照查询条件返回查询到的pks <br>
	 * Created on 2012-9-14 下午3:07:31<br>
	 * 
	 * @param context
	 * @param condition
	 * @param orderSQL
	 * @return
	 * @throws BusinessException
	 * @author daicy
	 ***************************************************************************/
	public String[] queryPKSByCondition(BmLoginContext context, String condition, String orderSQL)
			throws BusinessException;

	/**
	 * 
	 * @return
	 * @throws BusinessException
	 */
	BmDataVO queryByPk(String pk) throws BusinessException;

	/**
	 * 查询薪资档案(按照pks 来查询)
	 * 
	 * @throws BusinessException
	 */
	BmDataVO[] queryPayfileVOByPKS(String[] pks) throws BusinessException;

	/**
	 * 得到开始期间与最新期间
	 * 
	 * @param pk_org
	 * @return Map<String, Object>
	 * @throws BusinessException
	 */
	public Map<String, Object> getAllPeriod(String pk_org) throws BusinessException;

	/**
	 * 得到开始期间与最新期间forRpt
	 * 
	 * @param whereSql
	 * @return Map<String, Object>
	 * @throws BusinessException
	 */
	public Map<String, Object> getAllPeriodForRpt(String whereSql) throws BusinessException;

	/**
	 * 查询人员
	 * 
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */

	public BmDataVO[] queryPsnForAdd(BmLoginContext loginContext, String condition) throws BusinessException;

	/**
	 * 查询人员
	 * 
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */

	public BmDataVO[] queryPsnForAdd(BmLoginContext loginContext, String[] strPks) throws BusinessException;

	/**
	 * 查询社保险种
	 * 
	 * @param loginContext
	 * @param isCheck
	 *            TODO
	 * @return
	 * @throws BusinessException
	 */
	public BmClassVO[] queryBmClass(BmLoginContext loginContext, boolean isGroup, boolean isCheck)
			throws BusinessException;

	/**
	 * 查询人员本组织该期间下所有险种信息
	 * 
	 * @param pk_org
	 * @param pk_psndoc
	 * @param cyear
	 * @param cperiod
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO[] queryBmDataByPsndoc(String pk_group, String pk_org, String pk_psndoc, String cyear, String cperiod)
			throws BusinessException;

	/**
	 * 查询社保档案
	 * 
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO[] queryBmfile(BmLoginContext loginContext, String condition) throws BusinessException;

	/**
	 * 查询社保档案
	 * 
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO[] queryBmfile(BmLoginContext loginContext, String[] strPks) throws BusinessException;

	/**
	 * 批量删除与转出时，查询社保档案 说明：同一个自然人，只查询出一条记录
	 * 
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO[] queryBmfile4TransferOut(BmLoginContext loginContext, String condition, String powerSql)
			throws BusinessException;

	/**
	 * 批量删除与转出时，查询社保档案 说明：同一个自然人，只查询出一条记录
	 * 
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO[] queryBmfile4Del(BmLoginContext loginContext, String condition, String powerSql)
			throws BusinessException;

	/**
	 * 查询社保档案
	 * 
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO[] queryBmfileForTrnIn(BmLoginContext loginContext, String condition) throws BusinessException;

	public BmDataVO[] queryBmfileForUnseal(BmLoginContext loginContext, String condition) throws BusinessException;

	/**
	 * 查询社保档案
	 * 
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO[] queryBmfileForTrnIn(BmLoginContext loginContext, String[] strPks) throws BusinessException;

	/**
	 * 根据工作记录查询新增人员信息
	 * 
	 * @param pk_psnjob
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO queryPsnByPsnjob(String pk_psnjob) throws BusinessException;

	/**
	 * 查询人员本组织该期间下所有为参加险种信息
	 * 
	 * @param pk_org
	 * @param pk_psndoc
	 * @param cyear
	 * @param cperiod
	 * @return
	 * @throws BusinessException
	 */
	public BmClassVO[] queryBmClassForAdd(String pk_org, String pk_psnjob, String cyear, String cperiod, String pk_group)
			throws BusinessException;

	/**
	 * 查询变动人员
	 * 
	 * @param context
	 * @param beginDate
	 * @param endDate
	 * @param trnType
	 * @return
	 * @throws BusinessException
	 */
	public PsnTrnVO[] queryTrnPsnInfo(BmLoginContext context, UFLiteralDate beginDate, UFLiteralDate endDate,
			int trnType) throws BusinessException;

	/**
	 * 查询已转出人员
	 * 
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO[] queryBmfileForTrnOutCancel(BmLoginContext loginContext, String condition)
			throws BusinessException;

	/**
	 * 查询已转出人员
	 * 
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO[] queryBmfileForTrnOutCancel(BmLoginContext loginContext, String[] strPks) throws BusinessException;

	/**
	 * 得到查询SQL
	 * 
	 * @param itemid
	 * @param period
	 * @param cyear
	 * @param cperiod
	 * @return
	 */
	public String getSqlForFormular(String pk_bm_class, String itemid, String period, String cyear, String cperiod,
			String pk_psndocRef);

	/**
	 * 根据成本中心PK获取关联的部门
	 */
	public String[] queryDeptsByCostCenter(String pk_costcenter) throws BusinessException;

	/**
	 * 根据工作记录PK获取对应的财务组织PK
	 * 
	 * @param BmDataVO
	 *            []
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO[] getPkFinanceOrg(BmDataVO[] array) throws BusinessException;

	/**
	 * 通过classid查询集团分配险种信息
	 * 
	 * @param classInfo
	 * @return
	 * @throws BusinessException
	 */
	public AssignclsVO[] queryAssignclsVOsByClassVOs(BmDataVO[] classInfo) throws BusinessException;

	/**
	 * 验证是否该人员同一期间内是否在其他组织中添加过该险种
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	public void checkRepeatBmClass(BmDataVO[] vos, boolean isTransferIn) throws BusinessException;

	// 20150831 xiejie3 补丁合并NCdp205306564人员变动社保档案人员变动不能做转出 begin
	// shenliangc
	/**
	 * 根据变动人员面板上选中的人员基本信息主键和变动起止时间 查询变动人员在起止时间段内最新工作记录主键。
	 * 
	 * @param psnDocPks
	 * @param beginLDate
	 * @param endLDate
	 * @return String[]
	 * @throws BusinessException
	 */
	public String[] queryCurrentPkPsnJob(String[] psnDocPks, UFLiteralDate beginLDate, UFLiteralDate endLDate)
			throws BusinessException;

	// end

	// MOD {社保档案批量新增、修改社保代理公司、险种社保供应商代码（公司）、险种社保供应商代码（个人）}
	// kevin.nie
	// 2018-01-17
	// start
	BmDataVO[] queryBmForEdit(BmLoginContext loginContext, String condition) throws BusinessException;

	BmDataVO[] batchEditBmData(BmDataVO[] bmDataVOs, BmLoginContext loginContext) throws BusinessException;
	// {社保档案批量新增、修改社保代理公司、险种社保供应商代码（公司）、险种社保供应商代码（个人）} kevin.nie
	// 2018-01-17
	// end
}
