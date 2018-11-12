package nc.itf.hrta;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.ta.leaveextrarest.AggLeaveExtraRestVO;
import nc.vo.pub.BusinessException;

public interface ILeaveextrarestMaintain {

	public void delete(AggLeaveExtraRestVO[] vos) throws BusinessException;

	public AggLeaveExtraRestVO[] insert(AggLeaveExtraRestVO[] vos) throws BusinessException;

	public AggLeaveExtraRestVO[] update(AggLeaveExtraRestVO[] vos) throws BusinessException;

	public AggLeaveExtraRestVO[] query(IQueryScheme queryScheme)
			throws BusinessException;

}