package nc.itf.hrsms.hi.entrymng;

import java.util.ArrayList;
import java.util.HashMap;

import nc.vo.hi.entrymng.AggEntryapplyVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.uif2.LoginContext;

public interface IEntrymngManageService
{

	/**
	 * 批量保存入职申请单
	 * 
	 * @param aggvo
	 *            单据信息
	 * @param pkPsnjobs
	 *            入职人员工作记录主键
	 * @param context
	 *            登陆信息
	 * @param billCodes
	 *            单据号数组
	 * @param isShow
	 * @return AggEntryapplyVO[]
	 * @throws BusinessException
	 */
	public AggEntryapplyVO[] batchSaveBill(AggEntryapplyVO aggvo, ArrayList<String> pkPsnjobs, LoginContext context, String[] billCodes,
			boolean isShow) throws BusinessException;

	/**
	 * 批量删除单据
	 * 
	 * @param <T>
	 * @param billvos
	 * @throws BusinessException
	 */
	public <T extends AggregatedValueObject> void deleteBatchBill(T... billvos) throws BusinessException;

	/**
	 * 删除单据
	 * 
	 * @param <T>
	 * @param billvo
	 * @throws BusinessException
	 */
	public <T extends AggregatedValueObject> void deleteBill(T billvo) throws BusinessException;

	/**
	 * 执行单据
	 * 
	 * @param billvos
	 *            单据VO数组
	 * @param context
	 *            登陆信息
	 * @param isRunBackgroundTask
	 *            是否后台任务
	 * @return HashMap<String, Object> 执行结果
	 * @throws BusinessException
	 */
	public HashMap<String, Object> execBills(AggEntryapplyVO[] billvos, LoginContext context, boolean isRunBackgroundTask) throws BusinessException;
	
	/**
	 * 手动执行单据
	 * 
	 * @param bills
	 * @param context
	 * @param effectDate
	 * @return
	 * @throws BusinessException
	 */
	public HashMap<String, Object> manualExecBills(AggEntryapplyVO[] bills, LoginContext context, UFLiteralDate effectDate) throws BusinessException;

	/**
	 * 单据保存
	 * 
	 * @param <T>
	 * @param billvo
	 * @return <T extends AggregatedValueObject> T
	 * @throws BusinessException
	 */
	public <T extends AggregatedValueObject> T insertBill(T billvo) throws BusinessException;

	/**
	 * 批量保存单据
	 * 
	 * @param <T>
	 * @param billvos
	 * @return <T extends AggregatedValueObject> T[]
	 * @throws BusinessException
	 */
	public <T extends AggregatedValueObject> T[] saveBatchBill(T... billvos) throws BusinessException;

	/**
	 * 更新单据
	 * 
	 * @param <T>
	 * @param billvo
	 *            单据VO
	 * @return <T extends AggregatedValueObject> T
	 * @throws BusinessException
	 */
	public <T extends AggregatedValueObject> T updateBill(T billvo, boolean blChangeAuditInfo) throws BusinessException;

	/**
	 * 执行入职单
	 * 
	 * @param aggVO
	 *            单据VO
	 * @throws BusinessException
	 */
	public void doPerfromBill_RequiresNew(AggEntryapplyVO aggVO) throws BusinessException;

	/**
	 * 入职单据推其他单据
	 * 
	 * @param aggVO
	 *            单据VO
	 * @throws BusinessException
	 */
	public void doPushBill_RequiresNew(AggEntryapplyVO aggVO) throws BusinessException;

	/**
	 * 开启新事物，批量更新单据
	 * (为了支持消息模板上的审批和移动审批，将执行单据的操作移至后台处理 )
	 * @param vos
	 * @return AggEntryapplyVO[]
	 * @throws BusinessException
	 * @author heqiaoa 2014-11-18 
	 */
	public AggEntryapplyVO[] batchUpdateBill_RequiresNew(AggEntryapplyVO[] vos) throws BusinessException;

	public AggEntryapplyVO[] doDelete(AggEntryapplyVO[] vos) throws BusinessException;
}
