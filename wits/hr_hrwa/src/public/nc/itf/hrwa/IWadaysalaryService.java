package nc.itf.hrwa;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

public interface IWadaysalaryService {
	/**
	 * ͨ�^�����YԴ�M����Ӌ����н
	 * 
	 * @param pk_hrorg
	 * @param calculDate
	 * @return
	 * @throws BusinessException
	 */
	public void calculSalaryByHrorg(String pk_hrorg, UFLiteralDate calculDate) throws BusinessException;

	/**
	 * ���㲿����Ա����н
	 * 
	 * @param pk_psnorg
	 * @param calculDate
	 * @param pk_psndoc
	 * @param pk_wa_items
	 * @throws BusinessException
	 */
	public void calculSalaryByWaItem(String pk_hrorg, String pk_wa_class, UFLiteralDate calculDate, String pk_psndoc,
			String[] pk_wa_items) throws BusinessException;

	/**
	 * �h��ָ�����ڵ���н����
	 * 
	 * @param pk_hrorg
	 * @param calculdate
	 * @param continueTime
	 * @throws BusinessException
	 */
	public void deleteDaySalary(String pk_hrorg, UFLiteralDate calculdate, int continueTime) throws BusinessException;

	/**
	 * �z�ָ�������ȵ���н�Ƿ�Ӌ��ɹ�����δӋ�㣬�t����Ӌ��
	 * 
	 * @param pk_hrorg
	 * @param calculdate
	 * @param checkrange
	 * @throws BusinessException
	 */
	public void checkDaySalaryAndCalculSalary(String pk_hrorg, UFLiteralDate calculdate, int checkrange)
			throws BusinessException;

	/**
	 * �z�ָ�������ȵ���н�Ƿ�Ӌ��ɹ�����δӋ�㣬�t����Ӌ�㣬�����������Ҳ���¼���
	 * 
	 * @param pk_psndocs
	 * @param begindate
	 * @param enddate
	 * @throws BusinessException
	 */
	public void checkDaySalaryAndCalculSalary(String pk_wa_class, String[] pk_psndocs, UFLiteralDate begindate,
			UFLiteralDate enddate, String pk_wa_item) throws BusinessException;

	/**
	 * н�YӋ��ǰ�z�ָ�������ȵ���н�Ƿ�Ӌ��ɹ�����δӋ�㣬�t����Ӌ�㣬�����������Ҳ���¼���
	 * 
	 * @param pk_wa_class
	 * @param psnWhere
	 * @throws BusinessException
	 * @version ϸ������,����н����Ŀ���м���
	 */
	public void checkDaySalaryAndCalculSalary(String pk_wa_class, String[] pk_psndocs, String cyear, String cperiod)
			throws BusinessException;

}