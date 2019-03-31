package nc.itf.hr.wa;

import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginContext;


/**
 * ����ӿ�
 * 
 * @author: xuhw
 * @since: eHR V6.5
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public interface ITaxSpecialStatisticsManageService {

	void lockData(WaLoginContext context, String condition,String orderSQL) throws BusinessException;
	void unlockData(WaLoginContext context, String condition,String orderSQL) throws BusinessException;
	/**
	 * ��������
	 * @param context
	 * @param condition
	 * @param orderSQL
	 * @throws BusinessException 
	 */
	public void genData(WaLoginContext context, String condition,String orderSQL) throws BusinessException;
}
