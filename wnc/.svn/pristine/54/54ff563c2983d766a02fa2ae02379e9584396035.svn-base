package nc.itf.om;

import nc.vo.hr.managescope.ManagescopeBusiregionEnum;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

/**
 * 导航树 查询
 * 
 * @since V6.0 2011-3-25 上午09:50:09
 */
public interface INaviQueryService {

	/**
	 * 查询组织和部门
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
	 * 查询下一层，下一层不一定是其子节点
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
	 * 查询当前HR组织下的行政体系
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
	 * 查询当前HR组织下的指定业务的委托体系
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
	 * 查询当前HR组织所在的行政体系层次结构
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
