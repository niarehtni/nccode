package nc.itf.twhr;

import java.util.Collection;

import nc.itf.pubapp.pub.smart.ISmartService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.twhr.groupinsurance.GroupInsuranceSettingVO;

public interface IGroupinsuranceMaintain extends ISmartService {
	public GroupInsuranceSettingVO[] query(IQueryScheme queryScheme)
			throws BusinessException, Exception;

	public GroupInsuranceSettingVO[] queryByCondition(String pk_org,
			Collection<String[]> grouppsnpair) throws BusinessException;

	public GroupInsuranceSettingVO[] queryOnDuty(String pk_org)
			throws BusinessException;

	public boolean isExistsGroupInsuranceSettingRef(GroupInsuranceSettingVO vo)
			throws BusinessException;

	public boolean isExistsApprovedWaClassByGroupInsurance(String cgroupinsid,
			String cgroupinspsntypeid) throws BusinessException;
}
