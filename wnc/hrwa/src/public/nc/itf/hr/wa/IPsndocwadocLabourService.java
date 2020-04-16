package nc.itf.hr.wa;

import java.util.List;
import java.util.Map;

import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.wadoc.BatchGroupInsuranceExitVO;
import nc.vo.pub.BusinessException;

public interface IPsndocwadocLabourService {

	// MOD �ź� ��Ա������Ϣ 2018/9/15 mod Ares.Tank ���Ŷ�ѡ,��֯�ɲ�ѯ������Դ���µ����з�����֯
	public PsnJobVO[] queryPsnJobVOsByConditionAndOverrideOrg(List<String> pk_psndocs, String pk_org, String[] pk_dept)
			throws BusinessException;

	// MOD �ź� ��ѯ�ͱ��˱����� 2018/9/15
	public Map<String, String[]> queryLabour(List<String> pk_psndocs, String laoJiJu, String tuiJiJu, String jianJiJu)
			throws BusinessException;

	// MOD �ź� ����code�ҵ��Զ��嵵�����յ�nameֵ 2018/9/17
	public String queryRefNameByCode(String code) throws BusinessException;

	/**
	 * �F�������˱�
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public BatchGroupInsuranceExitVO[] batchGroupInsuranceExit(List<BatchGroupInsuranceExitVO> dataVO)
			throws BusinessException;

}
