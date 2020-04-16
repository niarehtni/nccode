package nc.itf.hrwa;

import java.util.Map;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

public interface IWadaysalaryService {
	/**
	 * ͨ�^�����YԴ�M����Ӌ����н
	 * 
	 * @param pk_hrorg
	 * @param calculDate
	 * @return
	 * @throws BusinessException
	 * <a> ��̨���񷽷��ݲ�ʹ�� tank 2019��10��15��21:35:19 </a>
	 */
	//public void calculSalaryByHrorg(String pk_hrorg, UFLiteralDate calculDate) throws BusinessException;

	/**
	 * ���㲿����Ա����н
	 * 
	 * @param pk_psnorg
	 * @param calculDate
	 * @param pk_psndoc
	 * @param pk_wa_items
	 * @throws BusinessException
	 * <a> ��̨���񷽷��ݲ�ʹ�� tank 2019��10��15��21:35:19 </a>
	 */
//	public void calculSalaryByWaItem(String pk_hrorg, String pk_wa_class, UFLiteralDate calculDate, String pk_psndoc,
//			String[] pk_wa_items) throws BusinessException;

	/**
	 * �h��ָ�����ڵ���н����
	 * 
	 * @param pk_hrorg
	 * @param calculdate
	 * @param continueTime
	 * @throws BusinessException
	 * <a> ��̨���񷽷��ݲ�ʹ�� tank 2019��10��15��21:35:19 </a>
	 */
	//public void deleteDaySalary(String pk_hrorg, UFLiteralDate calculdate, int continueTime) throws BusinessException;

	/**
	 * �z�ָ�������ȵ���н�Ƿ�Ӌ��ɹ�����δӋ�㣬�t����Ӌ��
	 * 
	 * @param pk_hrorg
	 * @param calculdate
	 * @param checkrange
	 * @throws BusinessException
	 * <a> ��̨���񷽷��ݲ�ʹ�� tank 2019��10��15��21:35:19 </a>
	 */
//	public void checkDaySalaryAndCalculSalary(String pk_hrorg, UFLiteralDate calculdate, int checkrange)
//			throws BusinessException;

	/**
	 * �z�ָ�������ȵ���н�Ƿ�Ӌ��ɹ�����δӋ�㣬�t����Ӌ�㣬�����������Ҳ���¼���
	 * 
	 * @param pk_psndocs
	 * @param begindate
	 * @param enddate
	 * @param pk_wa_item
	 *            н����Ŀ
	 * @param pk_group_item
	 *            н����Ŀ����
	 * @throws BusinessException
	 * @version ϸ������,����н����Ŀ���м���
	 * @ tank 2019��10��16��14:27:45 �ع���н
	 */
	/*public void checkDaySalaryAndCalculSalary(String pk_wa_class, String[] pk_psndocs, UFLiteralDate begindate,
			UFLiteralDate enddate, String pk_wa_item, String pk_group_item) throws BusinessException;*/

	/**
	 * ��н�ʷ�������Ա������н
	 * 
	 * @param pk_wa_class
	 * @param psnWhere
	 * @throws BusinessException
	 * @version ϸ������,����н����Ŀ���м���,Ŀǰ����н�ʼ����Ԥ����
	 */
	void calculDaySalaryWithWaClass(String pk_wa_class, String[] pk_psndocs, String cyear, String cperiod)
			throws BusinessException;
	
	/**
	 * �yӋĳһ�·������ˆT��Ո�ٿۿ�
	 * 
	 * @param pk_psndoc
	 * @param cyear
	 * @param cperiod
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, UFDouble> statisticLeavecharge(String pk_psndoc[], String cyear, String cperiod)
			throws BusinessException;
}
