package nc.itf.hr.dataexchange;

import java.util.List;
import java.util.Map;

import nc.vo.pub.BusinessException;

public interface IDataFormatService {
	/**
	 * ���ݸ�ʽ����ȡ������ʽ����
	 * 
	 * @param formatCode
	 *            ��ʽ����
	 * @return
	 * @throws BusinessException
	 */
	public List<Map> getFormatByCode(String formatCode) throws BusinessException;

	/**
	 * ���ݸ�ʽ��SQL��������
	 * 
	 * @param strSQL
	 * @return
	 * @throws BusinessException
	 */
	public List<Map> getDataFormatSQL(String strSQL) throws BusinessException;

	/**
	 * ȡн�Y�����f�����֔���
	 * 
	 * @param pk_org
	 *            �M��PK
	 * @param pk_wa_class
	 *            н�Y����PK
	 * @param cyear
	 *            ���
	 * @param cperiod
	 *            ���g
	 * @param itemGroupCode
	 *            н�Y�Ŀ�ֽM
	 * @param colNumber
	 *            �֙ڔ���
	 * @param colByteLength
	 *            ÿ�ڌ���
	 * @return
	 */
	public Map<String, String> getWaItemGroupColumnsByItemGroup(String pk_org, String pk_wa_class, String cyear,
			String cperiod, String itemGroupCode, int colNumber, int colByteLength) throws BusinessException;

	/**
	 * ����н�Y�Ŀ�ֽMȡн�Y�����Ŀ���~
	 * 
	 * @param pk_org
	 *            �M��PK
	 * @param pk_wa_class
	 *            н�Y����PK
	 * @param cyear
	 *            ���
	 * @param cperiod
	 *            ���g
	 * @param itemGroupCode
	 *            н�Y�Ŀ�ֽM
	 * @return
	 */
	public Map<String, Map<Integer, Map<String, Object>>> getWaItemByItemGroup(String pk_org, String pk_wa_class,
			String cyear, String cperiod, String itemGroupCode) throws BusinessException;
}
