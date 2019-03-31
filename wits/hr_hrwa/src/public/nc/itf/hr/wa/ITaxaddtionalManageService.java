package nc.itf.hr.wa;

import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;
import nc.vo.wa_tax.WASpecialAdditionaDeductionVO;

/**
 * ���ӷ��ÿ۳�
 * xuhw
 */
public interface ITaxaddtionalManageService
{
	/**
	 * �������ݴ���
	 * @param generalVOs
	 * @throws BusinessException 
	 */
	WASpecialAdditionaDeductionVO[]  importData(LoginContext context,WASpecialAdditionaDeductionVO[] generalVOs) throws BusinessException;
	
	/**
	 * ��������
	 * @param context
	 * @param sqlWhere
	 * @param orderby
	 */
	void exportData(LoginContext context, String sqlWhere, String orderby);
}