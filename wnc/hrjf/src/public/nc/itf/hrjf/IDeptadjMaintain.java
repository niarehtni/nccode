package nc.itf.hrjf;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.om.hrdept.AggHRDeptAdjustVO;
import nc.vo.pub.BusinessException;

public interface IDeptadjMaintain {

	public void delete(AggHRDeptAdjustVO[] vos) throws BusinessException;

	public AggHRDeptAdjustVO[] insert(AggHRDeptAdjustVO[] vos) throws BusinessException;

	public AggHRDeptAdjustVO[] update(AggHRDeptAdjustVO[] vos) throws BusinessException;

	public AggHRDeptAdjustVO[] query(IQueryScheme queryScheme)
			throws BusinessException;

}
