package nc.impl.pub.ace;

import nc.bs.twhr.diffinsurancea.ace.bp.AceDiffinsuranceaBP;
import nc.impl.pubapp.pub.smart.SmartServiceImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.twhr.diffinsurance.DiffinsuranceVO;

public abstract class AceDiffinsuranceaPubServiceImpl extends SmartServiceImpl {
	public DiffinsuranceVO[] pubquerybasedoc(IQueryScheme querySheme) throws nc.vo.pub.BusinessException {
		return new AceDiffinsuranceaBP().queryByQueryScheme(querySheme);
	}
}