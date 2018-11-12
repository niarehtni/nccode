package nc.pubimpl.twhr;

import java.util.Collection;

import nc.bs.dao.BaseDAO;
import nc.pubitf.twhr.IAllowancePubQuery;
import nc.vo.pub.BusinessException;
import nc.vo.twhr.allowance.AllowanceVO;
import nc.vo.twhr.basedoc.BaseDocVO;

public class AllowancePubQueryImpl implements IAllowancePubQuery {

	@SuppressWarnings("unchecked")
	@Override
	public AllowanceVO[] queryAllowanceList(String pk_org)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		Collection<BaseDocVO> result = dao.retrieveByClause(AllowanceVO.class,
				"dr=0");
		if (result == null) {
			return null;
		}
		return result.toArray(new AllowanceVO[0]);
	}

}
