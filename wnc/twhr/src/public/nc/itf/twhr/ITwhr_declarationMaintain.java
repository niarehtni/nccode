package nc.itf.twhr;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.twhr.twhr_declaration.AggDeclarationVO;
import nc.vo.hi.psndoc.PTCostVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

public interface ITwhr_declarationMaintain {

	public void delete(AggDeclarationVO[] clientFullVOs,
			AggDeclarationVO[] originBills) throws BusinessException;

	public AggDeclarationVO[] insert(AggDeclarationVO[] clientFullVOs,
			AggDeclarationVO[] originBills) throws BusinessException;

	public AggDeclarationVO[] update(AggDeclarationVO[] clientFullVOs,
			AggDeclarationVO[] originBills) throws BusinessException;

	public AggDeclarationVO[] query(IQueryScheme queryScheme)
			throws BusinessException;

	public AggDeclarationVO[] save(AggDeclarationVO[] clientFullVOs,
			AggDeclarationVO[] originBills) throws BusinessException;

	public AggDeclarationVO[] unsave(AggDeclarationVO[] clientFullVOs,
			AggDeclarationVO[] originBills) throws BusinessException;

	public AggDeclarationVO[] approve(AggDeclarationVO[] clientFullVOs,
			AggDeclarationVO[] originBills) throws BusinessException;

	public AggDeclarationVO[] unapprove(AggDeclarationVO[] clientFullVOs,
			AggDeclarationVO[] originBills) throws BusinessException;
	/**
	 * ����������д�߼�--н�ʷ���
	 * @param clientFullVOs
	 * @param originBills
	 * @return
	 * @throws BusinessException
	 * @author Ares.Tank 
	 * @date 2018��9��25�� ����3:30:17
	 * @description
	 */
	public void writeBack4HealthCaculate(AggDeclarationVO originBills) throws BusinessException;
	/**
	 * ����߉݋
	 * @param clientFullVOs
	 * @param originBills
	 * @return
	 * @throws BusinessException
	 * @author Ares.Tank 
	 * @date 2018��9��25�� ����3:30:17
	 * @description
	 */
	public void generatCompanyBVO(UFDate Date,String pk_org,String pkGroup) throws BusinessException;
	
	/**
	 * ����������д�߼�--�ڄ��M��
	 * @param clientFullVOs
	 * @param originBills
	 * @return
	 * @throws BusinessException
	 * @author Ares.Tank 
	 * @date 2018��9��26��22:59:53
	 * @description
	 */
	public void writeBack4PTCost(PTCostVO ptvo,String pk_org,String pk_group) throws BusinessException;
	
	/**
	 * ����code��ѯpk
	 * @param code ����
	 * @param flag ��ʶ
	 * @return
	 * @throws BusinessException
	 */
	public Object getPkByCode(String code,String flag) throws BusinessException;
	
	
}