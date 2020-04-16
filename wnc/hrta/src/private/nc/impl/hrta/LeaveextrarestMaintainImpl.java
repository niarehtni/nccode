package nc.impl.hrta;

import java.util.Collection;

import nc.bs.dao.BaseDAO;
import nc.impl.pub.ace.AceLeaveextrarestPubServiceImpl;
import nc.itf.hrta.ILeaveextrarestMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leaveextrarest.AggLeaveExtraRestVO;

public class LeaveextrarestMaintainImpl extends AceLeaveextrarestPubServiceImpl implements ILeaveextrarestMaintain {

	@Override
	public void delete(AggLeaveExtraRestVO[] vos) throws BusinessException {
		super.pubdeleteBills(vos);
	}

	@Override
	public AggLeaveExtraRestVO[] insert(AggLeaveExtraRestVO[] vos) throws BusinessException {
		return super.pubinsertBills(vos);
	}

	@Override
	public AggLeaveExtraRestVO[] update(AggLeaveExtraRestVO[] vos) throws BusinessException {
		return super.pubupdateBills(vos);
	}

	@Override
	public AggLeaveExtraRestVO[] query(IQueryScheme queryScheme) throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public UFLiteralDate calculateExpireDateByWorkAge(String pk_psndoc, UFLiteralDate dateBeforeChange,
			UFLiteralDate billDate) throws BusinessException {
		UFLiteralDate maxLeaveDate = null;
		BaseDAO basedao = new BaseDAO();
		Collection<PsnJobVO> jobvo = basedao.retrieveByClause(PsnJobVO.class, "pk_psndoc='" + pk_psndoc + "' and '"
				+ dateBeforeChange + "' between begindate and isnull(enddate, '9999-12-31') and trnsevent<>4");
		if (jobvo != null && jobvo.size() > 0) {
			PsnOrgVO psnOrgVO = (PsnOrgVO) basedao.retrieveByPK(PsnOrgVO.class,
					jobvo.toArray(new PsnJobVO[0])[0].getPk_psnorg());
			UFLiteralDate workAgeStartDate = (UFLiteralDate) psnOrgVO.getAttributeValue("workagestartdate"); // 年资起算日

			if (workAgeStartDate != null) {
				if (workAgeStartDate.toString().substring(4).equals("-02-29")) {
					maxLeaveDate = new UFLiteralDate(String.valueOf(dateBeforeChange.getYear()) + "-02-28");
					if (maxLeaveDate.before(billDate)) {
						maxLeaveDate = new UFLiteralDate(String.valueOf(dateBeforeChange.getYear() + 1) + "-02-28");
					}
				} else {
					maxLeaveDate = new UFLiteralDate(String.valueOf(dateBeforeChange.getYear())
							+ workAgeStartDate.toString().substring(4)).getDateBefore(1);
					if (maxLeaveDate.before(billDate)) {
						maxLeaveDate = new UFLiteralDate(String.valueOf(dateBeforeChange.getYear() + 1)
								+ workAgeStartDate.toString().substring(4)).getDateBefore(1);
					}
				}
			}
			return maxLeaveDate;
		} else {
			throw new BusinessException("员工 [" + ((PsndocVO) basedao.retrieveByPK(PsndocVO.class, pk_psndoc)).getCode()
					+ "] 外加补休日期按年资起算日计算错误，请检查员工组织关系年资起算日设定。");
		}

	}

}
