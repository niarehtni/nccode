package nc.itf.twhr;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.hi.psndoc.PTCostVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.twhr.twhr_declaration.AggDeclarationVO;
import nc.vo.twhr.twhr_declaration.CompanyAdjustBVO;
import nc.vo.uif2.LoginContext;

public interface ITwhr_declarationMaintain {

	public void delete(AggDeclarationVO[] clientFullVOs, AggDeclarationVO[] originBills) throws BusinessException;

	public AggDeclarationVO[] insert(AggDeclarationVO[] clientFullVOs, AggDeclarationVO[] originBills)
			throws BusinessException;

	public AggDeclarationVO[] update(AggDeclarationVO[] clientFullVOs, AggDeclarationVO[] originBills)
			throws BusinessException;

	public AggDeclarationVO[] query(IQueryScheme queryScheme) throws BusinessException;

	public AggDeclarationVO[] save(AggDeclarationVO[] clientFullVOs, AggDeclarationVO[] originBills)
			throws BusinessException;

	public AggDeclarationVO[] unsave(AggDeclarationVO[] clientFullVOs, AggDeclarationVO[] originBills)
			throws BusinessException;

	public AggDeclarationVO[] approve(AggDeclarationVO[] clientFullVOs, AggDeclarationVO[] originBills)
			throws BusinessException;

	public AggDeclarationVO[] unapprove(AggDeclarationVO[] clientFullVOs, AggDeclarationVO[] originBills)
			throws BusinessException;

	public String[] insertCompanyAdjustVOs(String pk_org, CompanyAdjustBVO[] vos) throws BusinessException;

	public int updateCompanyAdjustVOs(String pk_org, CompanyAdjustBVO[] vos) throws BusinessException;

	public void deleteCompanyAdjustVOs(CompanyAdjustBVO[] vos) throws BusinessException;

	/**
	 * 二代健保回写逻辑--薪资发放
	 * 
	 * @param clientFullVOs
	 * @param originBills
	 * @return
	 * @throws BusinessException
	 * @author Ares.Tank
	 * @date 2018年9月25日 下午3:30:17
	 * @description
	 */
	public void writeBack4HealthCaculate(AggDeclarationVO originBills) throws BusinessException;

	/**
	 * 生成邏輯
	 * 
	 * @param clientFullVOs
	 * @param originBills
	 * @return
	 * @throws BusinessException
	 * @author Ares.Tank
	 * @date 2018年9月25日 下午3:30:17
	 * @description
	 */
	public void generatCompanyBVO(UFDate Date, String pk_org, String pkGroup) throws BusinessException;

	/**
	 * 二代健保回写逻辑--勞務費用
	 * 
	 * @param clientFullVOs
	 * @param originBills
	 * @return
	 * @throws BusinessException
	 * @author Ares.Tank
	 * @date 2018年9月26日22:59:53
	 * @description
	 */
	public void writeBack4PTCost(PTCostVO ptvo, String pk_org, String pk_group) throws BusinessException;

	/**
	 * 根据code查询pk
	 * 
	 * @param code
	 *            编码
	 * @param flag
	 *            标识
	 * @param LoginContext
	 *            context
	 * @return
	 * @throws BusinessException
	 */
	public Object getPkByCode(String code, String flag, LoginContext context) throws BusinessException;

}