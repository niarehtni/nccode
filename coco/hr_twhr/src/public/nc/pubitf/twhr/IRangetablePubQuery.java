package nc.pubitf.twhr;
import java.util.Map;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.twhr.rangetable.RangeLineVO;
import nc.vo.twhr.rangetable.RangeTableAggVO;

public interface IRangetablePubQuery {
	/**
	 * ����֯�����ͺ����ڲ�ѯ�����
	 * 
	 * @param pk_org
	 *            ��֯ID
	 * @param tableType
	 *            ��������ͣ�1.�ͱ�,2.����,3.����,4.����˰��
	 * @param queryDate
	 *            ��ѯ����
	 * @return �����
	 * @Deprecated �Ѿ���������֯,��Ϊȫ�ֽڵ�
	 * @throws BusinessException
	 */
	@Deprecated
	public RangeTableAggVO[] queryRangetableByType(String pk_org,
			int tableType, UFDate queryDate) throws BusinessException;

	/**
	 * ����֯�����͡����ڼ�����ѯ����
	 * 
	 * @param pk_org
	 *            ��֯ID
	 * @param tableType
	 *            ��������ͣ�1.�ͱ�,2.����,3.����,4.����˰��
	 * @param queryDate
	 *            ��ѯ����
	 * @param amount
	 *            ��ѯ���
	 * @return ������ϸ��
	 * @Deprecated �Ѿ���������֯,��Ϊȫ�ֽڵ�
	 * @throws BusinessException
	 */
	@Deprecated
	public RangeLineVO queryRangeLineByAmount(String pk_org, int tableType,
			UFDate queryDate, UFDouble amount) throws BusinessException;

	/**
	 * ����֯�����͡����ڼ�����б�������ѯ����
	 * 
	 * @param pk_org
	 *            ��֯ID
	 * @param tableType
	 *            ��������ͣ�1.�ͱ�,2.����,3.����,4.����˰��
	 * @param queryDate
	 *            ��ѯ����
	 * @param amounts
	 *            ��ѯ����б�
	 * @return
	 * @Deprecated �Ѿ���������֯,��Ϊȫ�ֽڵ�
	 * @throws BusinessException
	 */
	@Deprecated
	public Map<UFDouble, RangeLineVO> batchQueryRangeLineByAmount(
			String pk_org, int tableType, UFDate queryDate, UFDouble[] amounts)
			throws BusinessException;
	//start ����������Ҫ��֯ Ares.Tank 2018-7-25 18:09:17
	/**
	 * �����ͺ����ڲ�ѯ�����
	 * 
	 * @param tableType
	 *            ��������ͣ�1.�ͱ�,2.����,3.����,4.����˰��
	 * @param queryDate
	 *            ��ѯ����
	 * @return �����
	 * @throws BusinessException
	 */
	public RangeTableAggVO[] queryRangetableByType(
			int tableType, UFDate queryDate) throws BusinessException;

	/**
	 * �����͡����ڼ�����ѯ����
	 * 
	 * @param tableType
	 *            ��������ͣ�1.�ͱ�,2.����,3.����,4.����˰��
	 * @param queryDate
	 *            ��ѯ����
	 * @param amount
	 *            ��ѯ���
	 * @return ������ϸ��
	 * @throws BusinessException
	 */
	public RangeLineVO queryRangeLineByAmount(int tableType,
			UFDate queryDate, UFDouble amount) throws BusinessException;

	/**
	 * �����͡����ڼ�����б�������ѯ����
	 * 
	 * @param tableType
	 *            ��������ͣ�1.�ͱ�,2.����,3.����,4.����˰��
	 * @param queryDate
	 *            ��ѯ����
	 * @param amounts
	 *            ��ѯ����б�
	 * @return
	 * @throws BusinessException
	 */
	public Map<UFDouble, RangeLineVO> batchQueryRangeLineByAmount(
			int tableType, UFDate queryDate, UFDouble[] amounts)
			throws BusinessException;
	//end ����������Ҫ��֯ Ares.Tank 2018-7-25 18:09:17
}
