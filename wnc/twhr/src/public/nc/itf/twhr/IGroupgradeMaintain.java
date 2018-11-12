package nc.itf.twhr;

import nc.itf.pubapp.pub.smart.ISmartService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.twhr.groupinsurance.GroupInsuranceGradeVO;

public interface IGroupgradeMaintain extends ISmartService {

	public GroupInsuranceGradeVO[] query(IQueryScheme queryScheme)
			throws BusinessException, Exception;

	public GroupInsuranceGradeVO[] queryOnDuty(String pk_org)
			throws BusinessException;
}