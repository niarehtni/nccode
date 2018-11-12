package nc.impl.pub.ace;
import nc.bs.twhr.groupgrade.ace.bp.AceGroupgradeBP;
import nc.impl.pubapp.pub.smart.SmartServiceImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.ISuperVO;
import nc.vo.twhr.groupinsurance.GroupInsuranceGradeVO;
import nc.vo.uif2.LoginContext;

public abstract class AceGroupgradePubServiceImpl extends SmartServiceImpl {
	public GroupInsuranceGradeVO[] pubquerybasedoc(IQueryScheme querySheme)
			throws nc.vo.pub.BusinessException {
		return new AceGroupgradeBP().queryByQueryScheme(querySheme);
	}
}