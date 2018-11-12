package nc.itf.om;

import nc.vo.hr.managescope.ManagescopeBusiregionEnum;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

/**
 * ������ ��ѯ
 * 
 * @since V6.0 2011-3-25 ����09:50:09
 */
public interface INaviQueryService {

	/**
	 * ��ѯ��֯�Ͳ���
	 * 
	 * @param isSeqControl
	 * @param context
	 * @param pk_org
	 * @param topOrg
	 * @param includeChildHR
	 * @param includeCancleDept
	 * @return
	 * @throws BusinessException
	 */
	public Object[] queryOrgAndDeptByOrgPK(boolean isSeqControl, LoginContext context, String pk_org, String topOrg,
			boolean includeChildHR, boolean includeCancleDept, boolean isIncludeDummyDept) throws BusinessException;

	/**
	 * ��ѯ��һ�㣬��һ�㲻һ�������ӽڵ�
	 * 
	 * @param context
	 * @param currentValue
	 * @param includeCancleDept
	 * @return
	 * @throws BusinessException
	 */
	public Object[] queryNextLayer(LoginContext context, Object currentValue, boolean includeCancleDept,
			boolean isIncludeDummyDept, ManagescopeBusiregionEnum busiregionEnum) throws BusinessException;

	/**
	 * ��ѯ��ǰHR��֯�µ�������ϵ
	 * 
	 * @param pk_org
	 * @param includeChildHR
	 * @param includeCancleDept
	 * @param includeDummyDept
	 * @param isSeqControl
	 * @return
	 * @throws BusinessException
	 */
	public Object[] queryAOSMembersByHROrgPK(String pk_org, boolean includeChildHR, boolean includeCancleDept,
			boolean includeDummyDept, String orgCondition, String deptCondition, boolean isSeqControl)
			throws BusinessException;

	/**
	 * ��ѯ��ǰHR��֯�µ�ָ��ҵ���ί����ϵ
	 * 
	 * @param pk_org
	 * @param includeCancleDept
	 * @param includeDummyDept
	 * @param busiRegionEnum
	 * @return
	 * @throws BusinessException
	 */
	public Object[] queryMSMembersByHROrgPK(String pk_org, boolean includeCancleDept, boolean includeDummyDept,
			ManagescopeBusiregionEnum busiRegionEnum) throws BusinessException;

	/**
	 * ��ѯ��ǰHR��֯���ڵ�������ϵ��νṹ
	 * 
	 * @param pk_org
	 * @param includeChildHR
	 * @param includeCancleDept
	 * @param includeDummyDept
	 * @param orgCondition
	 * @param deptCondition
	 * @return
	 * @throws BusinessException
	 */
	public Object[] queryAOSMembersCascadeByHROrgPK(String pk_org, boolean includeChildHR, boolean includeCancleDept,
			boolean includeDummyDept, boolean showHRFuturedFlag, String orgCondition, String deptCondition)
			throws BusinessException;
}
