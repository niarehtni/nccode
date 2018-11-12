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
	 * ����������ְ���뵥
	 * 
	 * @param aggvo
	 *            ������Ϣ
	 * @param pkPsnjobs
	 *            ��ְ��Ա������¼����
	 * @param context
	 *            ��½��Ϣ
	 * @param billCodes
	 *            ���ݺ�����
	 * @param isShow
	 * @return AggEntryapplyVO[]
	 * @throws BusinessException
	 */
	public AggEntryapplyVO[] batchSaveBill(AggEntryapplyVO aggvo, ArrayList<String> pkPsnjobs, LoginContext context, String[] billCodes,
			boolean isShow) throws BusinessException;

	/**
	 * ����ɾ������
	 * 
	 * @param <T>
	 * @param billvos
	 * @throws BusinessException
	 */
	public <T extends AggregatedValueObject> void deleteBatchBill(T... billvos) throws BusinessException;

	/**
	 * ɾ������
	 * 
	 * @param <T>
	 * @param billvo
	 * @throws BusinessException
	 */
	public <T extends AggregatedValueObject> void deleteBill(T billvo) throws BusinessException;

	/**
	 * ִ�е���
	 * 
	 * @param billvos
	 *            ����VO����
	 * @param context
	 *            ��½��Ϣ
	 * @param isRunBackgroundTask
	 *            �Ƿ��̨����
	 * @return HashMap<String, Object> ִ�н��
	 * @throws BusinessException
	 */
	public HashMap<String, Object> execBills(AggEntryapplyVO[] billvos, LoginContext context, boolean isRunBackgroundTask) throws BusinessException;
	
	/**
	 * �ֶ�ִ�е���
	 * 
	 * @param bills
	 * @param context
	 * @param effectDate
	 * @return
	 * @throws BusinessException
	 */
	public HashMap<String, Object> manualExecBills(AggEntryapplyVO[] bills, LoginContext context, UFLiteralDate effectDate) throws BusinessException;

	/**
	 * ���ݱ���
	 * 
	 * @param <T>
	 * @param billvo
	 * @return <T extends AggregatedValueObject> T
	 * @throws BusinessException
	 */
	public <T extends AggregatedValueObject> T insertBill(T billvo) throws BusinessException;

	/**
	 * �������浥��
	 * 
	 * @param <T>
	 * @param billvos
	 * @return <T extends AggregatedValueObject> T[]
	 * @throws BusinessException
	 */
	public <T extends AggregatedValueObject> T[] saveBatchBill(T... billvos) throws BusinessException;

	/**
	 * ���µ���
	 * 
	 * @param <T>
	 * @param billvo
	 *            ����VO
	 * @return <T extends AggregatedValueObject> T
	 * @throws BusinessException
	 */
	public <T extends AggregatedValueObject> T updateBill(T billvo, boolean blChangeAuditInfo) throws BusinessException;

	/**
	 * ִ����ְ��
	 * 
	 * @param aggVO
	 *            ����VO
	 * @throws BusinessException
	 */
	public void doPerfromBill_RequiresNew(AggEntryapplyVO aggVO) throws BusinessException;

	/**
	 * ��ְ��������������
	 * 
	 * @param aggVO
	 *            ����VO
	 * @throws BusinessException
	 */
	public void doPushBill_RequiresNew(AggEntryapplyVO aggVO) throws BusinessException;

	/**
	 * ����������������µ���
	 * (Ϊ��֧����Ϣģ���ϵ��������ƶ���������ִ�е��ݵĲ���������̨���� )
	 * @param vos
	 * @return AggEntryapplyVO[]
	 * @throws BusinessException
	 * @author heqiaoa 2014-11-18 
	 */
	public AggEntryapplyVO[] batchUpdateBill_RequiresNew(AggEntryapplyVO[] vos) throws BusinessException;

	public AggEntryapplyVO[] doDelete(AggEntryapplyVO[] vos) throws BusinessException;
}
