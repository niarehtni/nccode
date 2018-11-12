package nc.itf.trn.rds;

import java.util.Hashtable;

import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.trn.transmng.AggStapply;
import nc.vo.uif2.LoginContext;

public interface IRdsManageService {

	/**
	 * 保存"新增"的工作记录-调配记录
	 * @param aggVO  要保存的新增工作记录,包括人员基本信息、人员组织关系
	 * @param curTabCode  当前页签
	 * @param isSynWork  是否同步履历记录
	 * @param pk_hrorg  当前HR组织
	 * @throws BusinessException
	 */
	public PsndocAggVO addPsnjob(PsndocAggVO aggVO, String curTabCode, boolean isSynWork, String pk_hrorg) throws BusinessException;
	/**
	 * 保存"新增"的工作记录-调配记录
	 * @param aggVO  要保存的新增工作记录,包括人员基本信息、人员组织关系
	 * @param curTabCode  当前页签
	 * @param isSynWork  是否同步履历记录
	 * @param pk_hrorg  当前HR组织
	 * @author Ares.Tank 方便同时同步劳健保级距
	 * @throws BusinessException
	 */
	public PsndocAggVO addPsnjob_RequiresNew(PsndocAggVO aggVO, String curTabCode, boolean isSynWork, String pk_hrorg) throws BusinessException;
	
	
	
	   /**
     * 保存"新增"的工作记录-调配记录-跨组织调动
     * @param aggVO  要保存的新增工作记录,包括人员基本信息、人员组织关系
     * @param curTabCode  当前页签
     * @param isSynWork  是否同步履历记录
     * @param pk_hrorg  当前HR组织
     * @throws BusinessException
     */
    public PsndocAggVO addPsnjobTranster(PsndocAggVO aggVO, String curTabCode, boolean isSynWork, boolean isFinshPart, String pk_hrorg) throws BusinessException;

	/**
	 * 保存"新增"的工作记录-离职记录
	 * @param aggVO 要保存的新增 离职记录,包括人员基本信息、人员组织关系
	 * @param curTabCode 当前页签
	 * @param pk_hrorg  当前HR组织
	 * @param isDisablePsn 是否停用离职人员
	 * @throws BusinessException
	 */
	public void addPsnjobDimission(PsndocAggVO aggVO, String curTabCode, String pk_hrorg, boolean isDisablePsn) throws BusinessException;

	/**
	 * 保存"新增"的工作记录-离职记录
	 * @param aggVO 要保存的新增 离职记录,包括人员基本信息、人员组织关系
	 * @param curTabCode 当前页签
	 * @param pk_hrorg  当前HR组织
	 * @param isDisablePsn 是否停用离职人员
	 * @param date  劳保退保日期 
	 * @author Ares.Tank
	 * @throws BusinessException
	 */
	public void addPsnjobDimissionWithDate(PsndocAggVO aggVO, String curTabCode, String pk_hrorg, boolean isDisablePsn,UFLiteralDate endDate) throws BusinessException;

	/**
	 * 增加一条子集记录,并返回所有记录
	 * @param aggVO 要保存的新增字表记录,包括人员基本信息、人员组织关系
	 * @param curTabCode 当前页签
	 * @param isSynWork  只对兼职时有效
	 * @param pk_hrorg  当前HR组织
	 * @return PsndocAggVO
	 * @throws BusinessException
	 */
	public PsndocAggVO addSubRecord(PsndocAggVO aggVO, String curTabCode, boolean isSynWork, String pk_hrorg) throws BusinessException;

	/**
	 * 加入黑名单
	 * @param psn 黑名单信息
	 * @param addReason 
	 * @param pk_org 
	 * @throws BusinessException
	 */
	public void addToBlacklist(PsndocAggVO psn, String addReason, String pk_org) throws BusinessException;

	/**
	 * 记录节点批量修改
	 * @param aggs 人员基本信息主键
	 * @param pkPsnorgs 
	 * @param tabName 修改的子表名
	 * @param result 修改结果
	 * @param context 登陆信息
	 * @throws BusinessException
	 */
	public void batchUpdate(PsndocAggVO[] aggs, String[] pkPsnorgs, String tabName, Hashtable<String, Object> result, LoginContext context) throws BusinessException;

	/**
	 * 删除一条子集记录,并返回剩余数据
	 * @param aggVO aggVO 要删除的字表记录,包括人员基本信息、人员组织关系
	 * @param curTabCode 当前页签
	 * @param pk_hrorg HR组织
	 * @return PsndocAggVO
	 * @throws BusinessException
	 */
	public PsndocAggVO deleteSubRecord(PsndocAggVO aggVO, String curTabCode, String pk_hrorg) throws BusinessException;

	/**
	 * 身故处理
	 * @param isInjob 是否在职
	 * @param isPsnclChanged 人员类别是否变化
	 * @param context 登陆信息
	 * @param psndocVO 人员新息
	 * @param resourceID 事件源ID
	 * @param curTabCode 
	 * @param isDisablePsn TODO
	 * @return PsndocAggVO
	 * @throws BusinessException
	 */
	public PsndocAggVO dieOperation(boolean isInjob, boolean isPsnclChanged, LoginContext context, PsndocVO psndocVO, String resourceID, String curTabCode, boolean isDisablePsn) throws BusinessException;

	/**
	 * 插入一条子集记录,并返回所有数据
	 * @param aggVO aggVO 要插入的字表记录,包括人员基本信息、人员组织关系
	 * @param curTabCode 当前页签
	 * @return PsndocAggVO
	 * @throws BusinessException
	 */
	public PsndocAggVO insertSubRecord(PsndocAggVO aggVO, String curTabCode) throws BusinessException;

	/**
	 * 调配单据执行
	 * @param bill
	 * @throws BusinessException
	 */
	public Object perfromStaff_RequiresNew(AggStapply bill, boolean isqueryctrt) throws BusinessException;

	/**
	 * 离职单据执行
	 * @param bill
	 * @throws BusinessException
	 */
	public Object perfromTurnOver_RequiresNew(AggStapply bill, boolean isqueryctrt) throws BusinessException;

	/**
	 * 保存兼职变更
	 * @param aggVO aggVO 要保存的兼职变更记录,包括人员基本信息、人员组织关系
	 * @param curTabCode 当前页签
	 * @param isSynWork 是否同步工作履历
	 * @param pk_hrorg HR组织
	 * @return PsndocAggVO
	 * @throws BusinessException
	 */
	public PsndocAggVO savePartchg(PsndocAggVO aggVO, String curTabCode, boolean isSynWork, String pk_hrorg) throws BusinessException;
	/**
	 * 保存兼职变更
	 * @param partTimeVO partTimeVO 要保存的兼职变更记录
	 * @param curTabCode 当前页签
	 * @param isSynWork 是否同步工作履历
	 * @param pk_hrorg HR组织
	 * @return PartTimeVO
	 * @throws BusinessException
	 */
	public PartTimeVO savePartchgInf(PartTimeVO partTimeVO, String curTabCode, boolean isSynWork, String pk_hrorg) throws BusinessException;

	/**
	 * 修改一条子集记录,同步工作履历,并返回所有数据
	 * @param aggVO aggVO 要修改的字表记录,包括人员基本信息、人员组织关系
	 * @param curTabCode 当前页签
	 * @param isSynWork  是否同步工作履历给兼职与工作记录使用的
	 * @param pk_hrorg hr组织
	 * @return PsndocAggVO
	 * @throws BusinessException
	 */
	public PsndocAggVO updateSubRecord(PsndocAggVO aggVO, String curTabCode, boolean isSynWork, String pk_hrorg) throws BusinessException;

	/**
	 * 调配/离职推下游单据
	 * @param billtype 单据类型
	 * @param bill 单据信息
	 * @throws BusinessException
	 */
	public void pushWorkflow_RequiresNew(String billtype, AggStapply bill) throws BusinessException;
	
	/**
	 * 离职后转移
	 * @param psns 要转移的人员信息
	 * @param psnjob 转以后的工作信息
	 * @throws BusinessException
	 */
	public void dimissionTrans(PsndocAggVO[] psns, PsnJobVO psnjob) throws BusinessException;
	
	/**
	 * 离职后转移
	 * @param psnjob 转以后的工作信息
	 * @throws BusinessException
	 */
	public void doTransDimission(PsnJobVO psnjob) throws BusinessException ;
	
	/**
	 * 批量离职后转移-部門轉移使用
	 * @param psnjob 转以后的工作信息
	 * @throws BusinessException
	 */
	public void doTransDimissions(PsnJobVO[] psnjob, String pk_hrorg) throws BusinessException;

	public PsndocAggVO[] fillSubData(PsndocAggVO[] aggs, String[] pkPsnorgs, String tabName) throws BusinessException;

	public void validateAddToBlacklist(PsndocAggVO psn, String pk_org) throws BusinessException;
	
	public String checkCtrt(AggStapply[] bill) throws BusinessException;
}
