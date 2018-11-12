package nc.itf.hrpub;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.hrpub.mdmapping.AggMDClassVO;
import nc.vo.pub.BusinessException;

public interface IMDMappingMaintain {

	public void delete(AggMDClassVO[] vos) throws BusinessException;

	public AggMDClassVO[] insert(AggMDClassVO[] vos) throws BusinessException;

	public AggMDClassVO[] update(AggMDClassVO[] vos) throws BusinessException;

	public AggMDClassVO[] query(IQueryScheme queryScheme)
			throws BusinessException;

}