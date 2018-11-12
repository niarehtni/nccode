package nc.itf.hrsms.hi.entrymng;

import java.util.Hashtable;
import nc.vo.hi.entrymng.AggEntryapplyVO;
import nc.vo.hi.entrymng.ValidBudgetResultVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

public interface IEntrymngQueryService {
	/**
	 * 生成用户变量hash表
	 * 
	 * @param aggvos
	 *            单据VO数组
	 * @return Hashtable<String,String[]>
	 * @throws BusinessException
	 */
	Hashtable<String, String[]> createUserValue(AggregatedValueObject[] aggvos) throws BusinessException;

	/**
	 * 得到单据主键的sql片段
	 * 
	 * @param iBillStatus
	 *            待处理/已处理标志
	 * @param billType
	 *            单据类型
	 * @param billid
	 * @return String
	 */
	String getBillIdSql(int iBillStatus, String billType, String billid) throws BusinessException;

	/**
	 * 由条件查询单据信息
	 * 
	 * @param context
	 *            登陆信息
	 * @param condition
	 *            条件
	 * @return AggEntryapplyVO[]
	 * @throws BusinessException
	 */
	AggEntryapplyVO[] queryByCondition(LoginContext context, String condition) throws BusinessException;
	
	
	
	AggEntryapplyVO[] queryByCondition(LoginContext context, String[] psndocPKS) throws BusinessException;

	/**
	 * 根据主键查询单据信息
	 * 
	 * @param pk
	 *            主键
	 * @return AggEntryapplyVO
	 * @throws BusinessException
	 */
	AggEntryapplyVO queryByPk(String pk) throws BusinessException;

	/**
	 * 获得编制校验通过的单据信息
	 * 
	 * @param vos
	 *            要校验的单据VO数组
	 * @param context
	 *            登陆信息
	 * @return ValidBudgetResultVO 校验结果
	 * @throws BusinessException
	 */
	public ValidBudgetResultVO validateBudget(AggregatedValueObject[] vos, LoginContext context) throws BusinessException;

	/**
	 * @param aggVOClass
	 * @param billType
	 * @param approveSite
	 * @param context
	 * @return Object[]
	 * @throws BusinessException
	 */
	public Object[] queryWaitforBills(Class<? extends AggregatedValueObject> aggVOClass, String billType, boolean approveSite, LoginContext context)
			throws BusinessException;

	/**
	 * @param pks
	 * @return
	 */
	public String[] getPsndocPks(String[] pks) throws BusinessException;

	int getBillCount(String billtype, String whereOrg) throws BusinessException; 
	
	/**
	 * 对于不严格控制的编制进行校验，以便前台给出提示
	 * @param context 登陆信息
	 * @param billvos 入职单据VO
	 * @return	不严格控制的编制提示信息
	 * @throws BusinessException 
	 */
	public String validateValidBudget ( LoginContext context , AggEntryapplyVO[] billvos) throws BusinessException ;
	
}
