package nc.impl.pub.ace;

import nc.bs.twhr.groupinsurance.bp.GroupinsuranceBP;
import nc.impl.pubapp.pub.smart.SmartServiceImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.twhr.groupinsurance.GroupInsuranceSettingVO;
import nc.vo.uif2.LoginContext;

public abstract class AceGroupinsurancePubServiceImpl extends SmartServiceImpl {
	public GroupInsuranceSettingVO[] pubquerybasedoc(IQueryScheme querySheme)
			throws nc.vo.pub.BusinessException {
		return new GroupinsuranceBP().queryByQueryScheme(querySheme);
	}

	@Override
	public ISuperVO[] queryByDataVisibilitySetting(LoginContext context,
			Class<? extends ISuperVO> clz) throws BusinessException {
		return super.queryByDataVisibilitySetting(context, clz);
	}

	@Override
	public ISuperVO[] selectByWhereSql(String whereSql,
			Class<? extends ISuperVO> clz) throws BusinessException {
		return super.selectByWhereSql(whereSql, clz);
	}

}