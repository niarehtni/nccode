package nc.itf.hrwa;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.hrwa.incometax.AggIncomeTaxVO;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;
import nc.vo.pub.BusinessException;
/**
 * 
 * @author ward.wong
 * @date 20180126
 * @功能描述 申报明细档汇总接口
 *
 */
public interface ISumincometaxMaintain {

	public void delete(AggSumIncomeTaxVO[] vos) throws BusinessException;

	public AggSumIncomeTaxVO[] insert(AggSumIncomeTaxVO[] vos) throws BusinessException;

	public AggSumIncomeTaxVO[] update(AggSumIncomeTaxVO[] vos) throws BusinessException;

	public AggSumIncomeTaxVO[] query(IQueryScheme queryScheme)
			throws BusinessException;
	
}