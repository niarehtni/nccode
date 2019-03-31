package nc.pubitf.ta.leaveextrarest;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.OTBalanceDetailVO;
import nc.vo.ta.overtime.OTLeaveBalanceVO;

public interface ILeaveExtraRestService {
	/**
	 * ���o���̶��rн�������r�g�Y�㣨������
	 * 
	 * �I��߉݋�������o�������ڡ��ˆT�����������L�������� = �Y�����ڵġ�δ�Y��ĆT������a���M�нY��
	 * 
	 * @param pk_org
	 *            �M��
	 * @param pk_psndocs
	 *            �ˆT�б�
	 * @param settleDate
	 *            �Y������
	 * @param isForce
	 *            ���ƽY�㣺TRUE�r���z��Y�����ڣ����ƽY��ָ�������˵�ͨ������x�Y��
	 * @throws BusinessException
	 */
	void settledByExpiryDate(String pk_org, String[] pk_psndocs, UFLiteralDate settleDate, Boolean isForce)
			throws BusinessException;

	/**
	 * �����ˆT����������ȡ����a��OTLeaveBalanceVO����
	 * 
	 * @param pk_org
	 *            �M��PK
	 * @param pk_psndocs
	 *            �ˆTPK���M
	 * @param beginDate
	 *            ��ʼ����
	 * @param endDate
	 *            ��ֹ����
	 * @param pk_leavetypecopy
	 *            �ݼ�e
	 * @param isSettled
	 *            �Ƿ�Y��
	 * @return
	 * @throws BusinessException
	 */
	OTLeaveBalanceVO[] getLeaveExtHoursByType(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate, String pk_leavetypecopy, boolean isSettled) throws BusinessException;

	/**
	 * �����ˆT�����ȡ����a��OTLeaveBalanceVO����
	 * 
	 * @param pk_org
	 *            �M��PK
	 * @param pk_psndocs
	 *            �ˆTPK���M
	 * @param queryYear
	 *            �yӋ���
	 * @param pk_leavetypecopy
	 *            �ݼ�e
	 * @param isSettled
	 *            �Ƿ�Y��
	 * @return
	 * @throws BusinessException
	 */
	OTLeaveBalanceVO[] getLeaveExtHoursByType(String pk_org, String[] pk_psndocs, String queryYear,
			String pk_leavetypecopy, boolean isSettled) throws BusinessException;

	/**
	 * �����ˆT����������ȡ����a��OTBalanceDetailVO����
	 * 
	 * @param pk_org
	 * @param pk_psndoc
	 * @param queryYear
	 * @param beginDate
	 * @param endDate
	 * @param pk_leavetypecopy
	 * @param isSettled
	 * @return
	 * @throws BusinessException
	 */
	OTBalanceDetailVO[] getLeaveExtByType(String pk_org, String pk_psndoc, String queryYear, UFLiteralDate beginDate,
			UFLiteralDate endDate, String pk_leavetypecopy, boolean isSettled) throws BusinessException;

	/**
	 * ���o���ˆT�M������a�ݷ��Y��
	 * 
	 * @param pk_psndoc
	 *            �ˆT
	 * @throws BusinessException
	 */
	void unSettleByPsn(String pk_psndoc) throws BusinessException;

}