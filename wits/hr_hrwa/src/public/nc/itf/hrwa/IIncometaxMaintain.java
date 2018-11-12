package nc.itf.hrwa;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.hrwa.incometax.AggIncomeTaxVO;
import nc.vo.pub.BusinessException;
/**
 * 
 * @author ward.wong
 * @date 20180126
 * @功能描述 申报明细档接口
 *
 */
public interface IIncometaxMaintain {

	public void delete(AggIncomeTaxVO[] clientFullVOs,
			AggIncomeTaxVO[] originBills) throws BusinessException;

	public AggIncomeTaxVO[] insert(AggIncomeTaxVO[] clientFullVOs,
			AggIncomeTaxVO[] originBills) throws BusinessException;

	public AggIncomeTaxVO[] update(AggIncomeTaxVO[] clientFullVOs,
			AggIncomeTaxVO[] originBills) throws BusinessException;

	public AggIncomeTaxVO[] query(IQueryScheme queryScheme)
			throws BusinessException;
}
