package nc.itf.hr.wa;

import java.util.List;

import nc.vo.hi.psndoc.DeductDetailsVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginVO;

public interface IDeductDetailService {
	/**
	 * �����˰�ť��д��Ժǿ�ƿۿ���ϸ���Ӽ�
	 * 
	 * @param isRangeAll
	 * @param whereCondition
	 * @param waLoginVO
	 * @throws BusinessException 
	 */
	void rollbacktodeductdetail(WaLoginVO waLoginVO, String whereCondition, Boolean isRangeAll) throws BusinessException;
	
	/**
	 * ȡ����н�Y���������g�Ŀۿ�����(����)
	 * @param pk_wa_class
	 * @param waPeriod eg: 201909
	 * @throws BusinessException
	 */
	void cancelDeductdetail(String pk_wa_class,String waPeriod) throws BusinessException;
	/**
	 * ����ծȯ����,�����趨�������Ϣ
	 * @param ddvList �䶯�Ŀۿ���Ϣ
	 * @param isUpdatenull �Ƿ����û�пۿ��¼��ծȯ����
	 * @throws BusinessException
	 */
	void updateDebtfile(List<DeductDetailsVO> ddvList, boolean isUpdatenull) throws BusinessException;
}
