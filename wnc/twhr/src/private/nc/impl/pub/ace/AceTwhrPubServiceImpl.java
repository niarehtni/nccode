package nc.impl.pub.ace;
import nc.bs.twhr.twhr.ace.bp.AceTwhrBP;
import nc.impl.pubapp.pub.smart.SmartServiceImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.ISuperVO;
import nc.vo.twhr.nhicalc.BaoAccountVO;
import nc.vo.uif2.LoginContext;

public abstract class AceTwhrPubServiceImpl extends SmartServiceImpl {
	public BaoAccountVO[] pubquerybasedoc(IQueryScheme querySheme)
			throws nc.vo.pub.BusinessException {
		return new AceTwhrBP().queryByQueryScheme(querySheme);
	}
}