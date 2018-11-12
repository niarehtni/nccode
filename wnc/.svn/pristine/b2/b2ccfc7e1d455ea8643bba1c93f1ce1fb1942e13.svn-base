package nc.impl.pub.ace;
import nc.bs.hrpub.iopermit.ace.bp.AceIopermitBP;
import nc.impl.pubapp.pub.smart.SmartServiceImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.ISuperVO;
import nc.vo.hrpub.mdmapping.IOPermitVO;
import nc.vo.uif2.LoginContext;

public abstract class AceIopermitPubServiceImpl extends SmartServiceImpl {
	public IOPermitVO[] pubquerybasedoc(IQueryScheme querySheme)
			throws nc.vo.pub.BusinessException {
		return new AceIopermitBP().queryByQueryScheme(querySheme);
	}
}