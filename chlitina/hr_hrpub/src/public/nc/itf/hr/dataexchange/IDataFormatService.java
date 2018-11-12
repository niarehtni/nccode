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
}
