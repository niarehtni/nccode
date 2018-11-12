package nc.impl.twhr;

import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.impl.pub.ace.AceGroupgradePubServiceImpl;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.itf.twhr.IGroupgradeMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.pub.BusinessException;
import nc.vo.twhr.groupinsurance.GroupInsuranceGradeVO;

public class GroupgradeMaintainImpl extends AceGroupgradePubServiceImpl
		implements IGroupgradeMaintain {

	@Override
	public GroupInsuranceGradeVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybasedoc(queryScheme);
	}

	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO)
			throws BusinessException {
		BatchSaveAction<GroupInsuranceGradeVO> saveAction = new BatchSaveAction<GroupInsuranceGradeVO>();
		BatchOperateVO retData = saveAction.batchSave(batchVO);
		return retData;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GroupInsuranceGradeVO[] queryOnDuty(String pk_org)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		List<GroupInsuranceGradeVO> sets = (List<GroupInsuranceGradeVO>) dao
				.retrieveByClause(GroupInsuranceGradeVO.class, "pk_org='"
						+ pk_org + "' and dr=0 ");
		return sets.toArray(new GroupInsuranceGradeVO[0]);
	}
}
