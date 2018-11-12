package nc.pubitf.twhr;

import nc.vo.pub.BusinessException;
import nc.vo.twhr.basedoc.BaseDocVO;

public interface IBasedocPubQuery {
	/**
	 * ��ȡ���л�������
	 * 
	 * @return ��������
	 * @throws BusinessException
	 */
	public BaseDocVO[] queryAllBaseDoc(String pk_org) throws BusinessException;

	/**
	 * ����ָ����֯������ȡ��������
	 * 
	 * @param pk_org
	 *            ��֯PK
	 * @param strCode
	 *            ��������
	 * @return ����ֵ����
	 * @throws BusinessException
	 */
	public BaseDocVO queryBaseDocByCode(String pk_org, String strCode)
			throws BusinessException;
	/**
	 * ����ָ������ȡ��������
	 * 
	 * 
	 * @param strCode
	 *            ��������
	 * @return ����ֵ����
	 * @throws BusinessException
	 */
	public BaseDocVO queryBaseDocByCode(String strCode)
			throws BusinessException;
}
