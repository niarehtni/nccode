package nc.itf.hrwa;

import java.util.List;
import java.util.Map;

import nc.vo.hrwa.incometax.AggIncomeTaxVO;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

/**
 * 
 * @author ward.wong
 * @date 20180126
 * @�������� �걨��ϸ�����ݹ���ӿ�
 * 
 */
public interface IGetAggIncomeTaxData {
	/**
	 * @�������� �������������걨��ϸ��
	 * @param isForeignMonth
	 *            �Ƿ��⼮Ա�������걨
	 * @param unifiednumber
	 *            ͳһ���
	 * @param declaretype
	 *            �걨ƾ����ʽ
	 * @param waclass
	 *            н�ʷ���
	 * @param year
	 *            н�����
	 * @param beginMonth
	 *            ��ʼʱ��
	 * @param endMonth
	 *            ��ֹʱ��
	 * @param offBeginDate
	 *            �x�_ʼ����
	 * @param offEndDate
	 *            �x�Y������
	 * @return
	 * @throws BusinessException
	 */
	public List<AggIncomeTaxVO> getAggIncomeTaxData(boolean isForeignDeparture, boolean isForeignMonth,
			String unifiednumber, String[] declaretype, String[] waclass, String year, String beginMonth,
			String endMonth, UFLiteralDate offBeginDate, UFLiteralDate offEndDate) throws BusinessException;

	/**
	 * @�������� �����������������Ա�걨��ϸ��
	 * @param isForeignMonth
	 *            �Ƿ��⼮Ա�������걨
	 * @param unifiednumber
	 *            ͳһ���
	 * @param declaretype
	 *            �걨ƾ����ʽ
	 * @param waclass
	 *            н�ʷ���
	 * @param year
	 *            н�����
	 * @param beginMonth
	 *            ��ʼʱ��
	 * @param endMonth
	 *            ��ֹʱ��
	 * @return
	 * @throws BusinessException
	 */
	public List<AggIncomeTaxVO> getPsnIncomeTaxData(String unifiednumber, String[] declaretype, String year,
			String beginMonth, String endMonth) throws BusinessException;

	/**
	 * ������Ա�����ж�֤���
	 * 
	 * @param pk_psndoc
	 *            pk_wa_data н�����ݱ�����
	 * @return
	 * @throws BusinessException
	 */
	public String getIdtypeno(String pk_psndoc, String pk_wa_data, String cyearperiod) throws BusinessException;

	/**
	 * �����걨��ϸ��������ɾ��������Ϣ
	 */
	public void deleteSumIncomeTax(String[] pk_incometax) throws BusinessException;

	/**
	 * ע���걨��ϸ��
	 */
	public void markIncomeTaxVO(String[] pk_incometax) throws BusinessException;

	/**
	 * ȡ��ע���걨��ϸ��
	 */
	public void unMarkIncomeTaxVO(String[] pk_incometax) throws BusinessException;

	/**
	 * �걨��ϸ��ȡ������
	 */
	public void cancleGather(String[] pk_incometax) throws BusinessException;

	/**
	 * �����걨��ϸ����ѯ�걨����
	 */
	public AggSumIncomeTaxVO getAggSumIncomeTaxByIncome(String pk_incometax) throws BusinessException;

	/**
	 * �����걨���ܵ���������ѯaggvo
	 */
	public AggSumIncomeTaxVO getAggSumIncomeTaxByPK(String pk) throws BusinessException;

	/**
	 * ��ȡ��Ա����
	 */
	public Map<String, String> getPsnNameByPks(String[] pks) throws BusinessException;

	/**
	 * ��ȡн�ʷ�������
	 */
	public Map<String, String> getWaClassName(String[] pks) throws BusinessException;

	/**
	 * ���¼���̨���ͽ�������
	 * 
	 * @throws BusinessException
	 */
	public void reloadLocalizationRefs() throws BusinessException;
}
