package nc.itf.hrpub;

import nc.itf.pubapp.pub.smart.ISmartService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.hrpub.mdmapping.IOPermitVO;
import nc.vo.pub.BusinessException;

public interface IIopermitMaintain extends ISmartService {

	public IOPermitVO[] query(IQueryScheme queryScheme)
			throws BusinessException, Exception;
}