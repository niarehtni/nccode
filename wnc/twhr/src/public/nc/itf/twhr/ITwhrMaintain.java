package nc.itf.twhr;

import java.util.Map;
import java.util.Set;

import nc.itf.pubapp.pub.smart.ISmartService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.twhr.nhicalc.BaoAccountVO;

public interface ITwhrMaintain extends ISmartService {
	/**
	 * ��Աδƥ�䵽��֯
	 */
	public static Integer ERROR_NO_MATCH_ORG = 1;

	public BaoAccountVO[] query(IQueryScheme queryScheme) throws BusinessException, Exception;

	/**
	 * �˽ӿ��ǵ����vo�����������vo����£�����ʹ���(�ͱ�)
	 * @return Map<Integer,Set<String>> <�������,<��Ա���֤��>>
	 * @throws BusinessException
	 */
	public Map<Integer,Set<String>> insertupdatelabor(BaoAccountVO[] baoaccountvos,String pk_legal_org,String waperiod) throws BusinessException;

	/**
	 * �˽ӿ��ǵ����vo�����������vo����£�����ʹ��루������
	 * @return Map<Integer,Set<String>> <�������,<��Ա���֤��>>
	 * @throws BusinessException
	 */
	public Map<Integer,Set<String>> insertupdatehealth(BaoAccountVO[] baoaccountvos,String pk_legal_org,String waperiod) throws BusinessException;

	/**
	 * �˽ӿ��ǵ����vo�����������vo����£�����ʹ��루���ˣ�
	 * @return Map<Integer,Set<String>> <�������,<��Ա���֤��>>
	 * @throws BusinessException
	 */
	public Map<Integer,Set<String>> insertupdateretire(BaoAccountVO[] baoaccountvos,String pk_legal_org,String waperiod) throws BusinessException;
	/**
	 * ��������
	 * @param baoaccountvos
	 * @param wa_period 
	 * @param pk_wa_class 
	 * @throws BusinessException
	 */
	void Settle(BaoAccountVO[] baoaccountvos, String pk_wa_class, String wa_period) throws BusinessException;
}