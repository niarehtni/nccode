package nc.itf.hrwa;


/**
 * ��н�ع�,������н�ϲ�,���಻��ʹ��
 * @author tank
 * 2019��10��16��14:34:43
 */
@Deprecated
public interface IWaTbmdaysalaryService {
	
	/**
	 * ͨ�^�����YԴ�M����Ӌ�㿼����н
	 * @param pk_hrorg 
	 * @param calculDate
	 * @return
	 * @throws BusinessException
	 * <a> ��̨���񷽷��ݲ�ʹ�� tank 2019��10��15��21:35:19 </a>
	 * @see nc.itf.hrwa.IWadaysalaryService.calculSalaryByHrorg(String, UFLiteralDate)
	 */
	//public void calculTbmSalaryByHrorg(String pk_hrorg,UFLiteralDate calculDate) throws BusinessException;
	/**
	 * ���㲿����Ա�Ŀ�����н
	 * @param pk_psnorg
	 * @param calculDate
	 * @param pk_psndoc
	 * @param pk_wa_items
	 * @throws BusinessException
	 * <a> ��̨���񷽷��ݲ�ʹ�� tank 2019��10��15��21:35:19 </a>
	 */
	//public void calculTbmSalaryByWaItem(String pk_hrorg,UFLiteralDate calculDate,String pk_psndoc,String[] pk_wa_items) throws BusinessException;
	/**
	 * �h��ָ�����ڵ���н����
	 * @param pk_hrorg
	 * @param calculdate
	 * @param continueTime
	 * @throws BusinessException
	 * <a> ��̨���񷽷��ݲ�ʹ�� tank 2019��10��15��21:35:19 </a>
	 */
	//public void deleteTbmDaySalary(String pk_hrorg,UFLiteralDate calculdate,int continueTime)throws BusinessException;
	/**
	 * �z�ָ�������ȵ���н�Ƿ�Ӌ��ɹ�����δӋ�㣬�t����Ӌ��
	 * @param pk_hrorg
	 * @param calculdate
	 * @param checkrange
	 * @throws BusinessException
	 * <a> ��̨���񷽷��ݲ�ʹ�� tank 2019��10��15��21:35:19 </a>
	 */
	//public void checkTbmDaySalaryAndCalculSalary(String pk_hrorg,UFLiteralDate calculdate,int checkrange) throws BusinessException;
	/**
	 * �z�ָ�������ȵĿ���н���Ƿ�Ӌ��ɹ�����δӋ�㣬�t����Ӌ�㣬�����������Ҳ���¼���
	 * @param pk_psndocs
	 * @param begindate
	 * @param enddate
	 * @throws BusinessException
	 * @ ������н�Ѿ��Ͷ�������н�ϲ�
	 */
	/*public void checkTbmDaysalaryAndRecalculate(String pk_org,String[] pk_psndocs,UFLiteralDate begindate,UFLiteralDate enddate,
			String pk_item_group) throws BusinessException;*/

	/**
	 * tank 2019��10��16��14:17:41 ��н�ع�
	 */
	//public double getTbmSalaryNum(String pk_hrorg, UFLiteralDate calculDate, int tbmnumtype) throws BusinessException;
}
