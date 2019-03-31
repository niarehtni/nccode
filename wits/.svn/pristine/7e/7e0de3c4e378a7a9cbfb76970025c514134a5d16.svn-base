package nc.itf.hr.wa;

import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;
import nc.vo.wa_tax.WASpecialAdditionaDeductionVO;

/**
 * 附加费用扣除
 * xuhw
 */
public interface ITaxaddtionalManageService
{
	/**
	 * 导入数据处理
	 * @param generalVOs
	 * @throws BusinessException 
	 */
	WASpecialAdditionaDeductionVO[]  importData(LoginContext context,WASpecialAdditionaDeductionVO[] generalVOs) throws BusinessException;
	
	/**
	 * 导出数据
	 * @param context
	 * @param sqlWhere
	 * @param orderby
	 */
	void exportData(LoginContext context, String sqlWhere, String orderby);
}