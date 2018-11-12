package nc.impl.pub.ace;
import nc.bs.hrpub.ioschema.ace.bp.AceIOSchemaBP;
import nc.impl.pubapp.pub.smart.SmartServiceImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.ISuperVO;
import nc.vo.hrpub.mdmapping.IOSchemaVO;
import nc.vo.uif2.LoginContext;

public abstract class AceIOSchemaPubServiceImpl extends SmartServiceImpl {
	public IOSchemaVO[] pubquerybasedoc(IQueryScheme querySheme)
			throws nc.vo.pub.BusinessException {
		return new AceIOSchemaBP().queryByQueryScheme(querySheme);
	}
}