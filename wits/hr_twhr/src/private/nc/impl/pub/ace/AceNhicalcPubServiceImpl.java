package nc.impl.pub.ace;

import nc.bs.twhr.nhicalc.bp.NhicalcBP;
import nc.impl.pubapp.pub.smart.SmartServiceImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.twhr.nhicalc.NhiCalcVO;
import nc.vo.uif2.LoginContext;

public abstract class AceNhicalcPubServiceImpl extends SmartServiceImpl {
	public NhiCalcVO[] pubquerybasedoc(IQueryScheme querySheme)
			throws nc.vo.pub.BusinessException {
		return new NhicalcBP().queryByQueryScheme(querySheme);
	}

	@Override
	public ISuperVO[] queryByDataVisibilitySetting(LoginContext context,
			Class<? extends ISuperVO> clz) throws BusinessException {
		return null;
	}

	@Override
	public ISuperVO[] selectByWhereSql(String whereSql,
			Class<? extends ISuperVO> clz) throws BusinessException {
		return super.selectByWhereSql(whereSql, clz);
	}

}