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
	 * ����"����"�Ĺ�����¼-�����¼
	 * @param aggVO  Ҫ���������������¼,������Ա������Ϣ����Ա��֯��ϵ
	 * @param curTabCode  ��ǰҳǩ
	 * @param isSynWork  �Ƿ�ͬ��������¼
	 * @param pk_hrorg  ��ǰHR��֯
	 * @throws BusinessException
	 */
	public PsndocAggVO addPsnjob(PsndocAggVO aggVO, String curTabCode, boolean isSynWork, String pk_hrorg) throws BusinessException;
	/**
	 * ����"����"�Ĺ�����¼-�����¼
	 * @param aggVO  Ҫ���������������¼,������Ա������Ϣ����Ա��֯��ϵ
	 * @param curTabCode  ��ǰҳǩ
	 * @param isSynWork  �Ƿ�ͬ��������¼
	 * @param pk_hrorg  ��ǰHR��֯
	 * @author Ares.Tank ����ͬʱͬ���ͽ�������
	 * @throws BusinessException
	 */
	public PsndocAggVO addPsnjob_RequiresNew(PsndocAggVO aggVO, String curTabCode, boolean isSynWork, String pk_hrorg) throws BusinessException;
	
	
	
	   /**
     * ����"����"�Ĺ�����¼-�����¼-����֯����
     * @param aggVO  Ҫ���������������¼,������Ա������Ϣ����Ա��֯��ϵ
     * @param curTabCode  ��ǰҳǩ
     * @param isSynWork  �Ƿ�ͬ��������¼
     * @param pk_hrorg  ��ǰHR��֯
     * @throws BusinessException
     */
    public PsndocAggVO addPsnjobTranster(PsndocAggVO aggVO, String curTabCode, boolean isSynWork, boolean isFinshPart, String pk_hrorg) throws BusinessException;

	/**
	 * ����"����"�Ĺ�����¼-��ְ��¼
	 * @param aggVO Ҫ��������� ��ְ��¼,������Ա������Ϣ����Ա��֯��ϵ
	 * @param curTabCode ��ǰҳǩ
	 * @param pk_hrorg  ��ǰHR��֯
	 * @param isDisablePsn �Ƿ�ͣ����ְ��Ա
	 * @throws BusinessException
	 */
	public void addPsnjobDimission(PsndocAggVO aggVO, String curTabCode, String pk_hrorg, boolean isDisablePsn) throws BusinessException;

	/**
	 * ����"����"�Ĺ�����¼-��ְ��¼
	 * @param aggVO Ҫ��������� ��ְ��¼,������Ա������Ϣ����Ա��֯��ϵ
	 * @param curTabCode ��ǰҳǩ
	 * @param pk_hrorg  ��ǰHR��֯
	 * @param isDisablePsn �Ƿ�ͣ����ְ��Ա
	 * @param date  �ͱ��˱����� 
	 * @author Ares.Tank
	 * @throws BusinessException
	 */
	public void addPsnjobDimissionWithDate(PsndocAggVO aggVO, String curTabCode, String pk_hrorg, boolean isDisablePsn,UFLiteralDate endDate) throws BusinessException;

	/**
	 * ����һ���Ӽ���¼,���������м�¼
	 * @param aggVO Ҫ����������ֱ��¼,������Ա������Ϣ����Ա��֯��ϵ
	 * @param curTabCode ��ǰҳǩ
	 * @param isSynWork  ֻ�Լ�ְʱ��Ч
	 * @param pk_hrorg  ��ǰHR��֯
	 * @return PsndocAggVO
	 * @throws BusinessException
	 */
	public PsndocAggVO addSubRecord(PsndocAggVO aggVO, String curTabCode, boolean isSynWork, String pk_hrorg) throws BusinessException;

	/**
	 * ���������
	 * @param psn ��������Ϣ
	 * @param addReason 
	 * @param pk_org 
	 * @throws BusinessException
	 */
	public void addToBlacklist(PsndocAggVO psn, String addReason, String pk_org) throws BusinessException;

	/**
	 * ��¼�ڵ������޸�
	 * @param aggs ��Ա������Ϣ����
	 * @param pkPsnorgs 
	 * @param tabName �޸ĵ��ӱ���
	 * @param result �޸Ľ��
	 * @param context ��½��Ϣ
	 * @throws BusinessException
	 */
	public void batchUpdate(PsndocAggVO[] aggs, String[] pkPsnorgs, String tabName, Hashtable<String, Object> result, LoginContext context) throws BusinessException;

	/**
	 * ɾ��һ���Ӽ���¼,������ʣ������
	 * @param aggVO aggVO Ҫɾ�����ֱ��¼,������Ա������Ϣ����Ա��֯��ϵ
	 * @param curTabCode ��ǰҳǩ
	 * @param pk_hrorg HR��֯
	 * @return PsndocAggVO
	 * @throws BusinessException
	 */
	public PsndocAggVO deleteSubRecord(PsndocAggVO aggVO, String curTabCode, String pk_hrorg) throws BusinessException;

	/**
	 * ��ʴ���
	 * @param isInjob �Ƿ���ְ
	 * @param isPsnclChanged ��Ա����Ƿ�仯
	 * @param context ��½��Ϣ
	 * @param psndocVO ��Ա��Ϣ
	 * @param resourceID �¼�ԴID
	 * @param curTabCode 
	 * @param isDisablePsn TODO
	 * @return PsndocAggVO
	 * @throws BusinessException
	 */
	public PsndocAggVO dieOperation(boolean isInjob, boolean isPsnclChanged, LoginContext context, PsndocVO psndocVO, String resourceID, String curTabCode, boolean isDisablePsn) throws BusinessException;

	/**
	 * ����һ���Ӽ���¼,��������������
	 * @param aggVO aggVO Ҫ������ֱ��¼,������Ա������Ϣ����Ա��֯��ϵ
	 * @param curTabCode ��ǰҳǩ
	 * @return PsndocAggVO
	 * @throws BusinessException
	 */
	public PsndocAggVO insertSubRecord(PsndocAggVO aggVO, String curTabCode) throws BusinessException;

	/**
	 * ���䵥��ִ��
	 * @param bill
	 * @throws BusinessException
	 */
	public Object perfromStaff_RequiresNew(AggStapply bill, boolean isqueryctrt) throws BusinessException;

	/**
	 * ��ְ����ִ��
	 * @param bill
	 * @throws BusinessException
	 */
	public Object perfromTurnOver_RequiresNew(AggStapply bill, boolean isqueryctrt) throws BusinessException;

	/**
	 * �����ְ���
	 * @param aggVO aggVO Ҫ����ļ�ְ�����¼,������Ա������Ϣ����Ա��֯��ϵ
	 * @param curTabCode ��ǰҳǩ
	 * @param isSynWork �Ƿ�ͬ����������
	 * @param pk_hrorg HR��֯
	 * @return PsndocAggVO
	 * @throws BusinessException
	 */
	public PsndocAggVO savePartchg(PsndocAggVO aggVO, String curTabCode, boolean isSynWork, String pk_hrorg) throws BusinessException;
	/**
	 * �����ְ���
	 * @param partTimeVO partTimeVO Ҫ����ļ�ְ�����¼
	 * @param curTabCode ��ǰҳǩ
	 * @param isSynWork �Ƿ�ͬ����������
	 * @param pk_hrorg HR��֯
	 * @return PartTimeVO
	 * @throws BusinessException
	 */
	public PartTimeVO savePartchgInf(PartTimeVO partTimeVO, String curTabCode, boolean isSynWork, String pk_hrorg) throws BusinessException;

	/**
	 * �޸�һ���Ӽ���¼,ͬ����������,��������������
	 * @param aggVO aggVO Ҫ�޸ĵ��ֱ��¼,������Ա������Ϣ����Ա��֯��ϵ
	 * @param curTabCode ��ǰҳǩ
	 * @param isSynWork  �Ƿ�ͬ��������������ְ�빤����¼ʹ�õ�
	 * @param pk_hrorg hr��֯
	 * @return PsndocAggVO
	 * @throws BusinessException
	 */
	public PsndocAggVO updateSubRecord(PsndocAggVO aggVO, String curTabCode, boolean isSynWork, String pk_hrorg) throws BusinessException;

	/**
	 * ����/��ְ�����ε���
	 * @param billtype ��������
	 * @param bill ������Ϣ
	 * @throws BusinessException
	 */
	public void pushWorkflow_RequiresNew(String billtype, AggStapply bill) throws BusinessException;
	
	/**
	 * ��ְ��ת��
	 * @param psns Ҫת�Ƶ���Ա��Ϣ
	 * @param psnjob ת�Ժ�Ĺ�����Ϣ
	 * @throws BusinessException
	 */
	public void dimissionTrans(PsndocAggVO[] psns, PsnJobVO psnjob) throws BusinessException;
	
	/**
	 * ��ְ��ת��
	 * @param psnjob ת�Ժ�Ĺ�����Ϣ
	 * @throws BusinessException
	 */
	public void doTransDimission(PsnJobVO psnjob) throws BusinessException ;
	
	/**
	 * ������ְ��ת��-���T�D��ʹ��
	 * @param psnjob ת�Ժ�Ĺ�����Ϣ
	 * @throws BusinessException
	 */
	public void doTransDimissions(PsnJobVO[] psnjob, String pk_hrorg) throws BusinessException;

	public PsndocAggVO[] fillSubData(PsndocAggVO[] aggs, String[] pkPsnorgs, String tabName) throws BusinessException;

	public void validateAddToBlacklist(PsndocAggVO psn, String pk_org) throws BusinessException;
	
	public String checkCtrt(AggStapply[] bill) throws BusinessException;
}
