package nc.pubitf.ta.overtime;

import java.util.Map;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.AggSegRuleVO;

public interface ISegRuleService {
	/**
	 * �������ڡ��T��ȡ�Ӱ�ֶ��M�ʣ�������
	 * 
	 * �I��߉݋�������o�������ڹ������T����������ԃ���������˼Ӱ�ֶ������ļӰ��ӛ�Σ������ˆT�����ڌ����ļӰ�ֶ�����Map
	 * 
	 * @param otDates
	 *            �Ӱ����ڔ��M
	 * @param pk_psndocs
	 *            �Ӱ��T��PK���M
	 * @return Map.Key=�T��PK��Map.Value=(Map.Key=�Ӱ�����, Map.Value=�Ӱ�ֶ�����VO)
	 * @throws BusinessException
	 */
	public Map<String, Map<UFLiteralDate, AggSegRuleVO>> querySegRulesByPsn(
			UFLiteralDate[] otDates, String[] pk_psndocs)
			throws BusinessException;

	/**
	 * �����������g���T��ȡ�Ӱ�ֶ��M�ʣ�������
	 * 
	 * �I��߉݋�������o���Ŀ����꼰���g��ȡ��ԓ���g���������ڣ��Kͬ�ˆTPK���M�{�÷���1���K���ط���1�Y��
	 * 
	 * @param pk_org
	 *            �M��
	 * @param cyear
	 *            �������
	 * @param cperiod
	 *            �������g��̖
	 * @param pk_psndocs
	 *            �Ӱ��T��PK���M
	 * @return Map.Key=�T��PK��Map.Value=(Map.Key=�Ӱ�����, Map.Value=�Ӱ�ֶ�����VO)
	 * @throws BusinessException
	 */
	public Map<String, Map<UFLiteralDate, AggSegRuleVO>> querySegRulesByPsn(
			String pk_org, String cyear, String cperiod, String[] pk_psndocs)
			throws BusinessException;

	/**
	 * ������ֹ���ڡ��T��ȡ�Ӱ�ֶ��M�ʣ�������
	 * 
	 * �I��߉݋�������o������ֹ���ڣ��Kͬ�ˆTPK���M�{�÷���1���K���ط���1�Y��
	 * 
	 * @param startDate
	 *            �_ʼ����
	 * @param endDate
	 *            �Y������
	 * @param pk_psndocs
	 *            �Ӱ��T��PK���M
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, Map<UFLiteralDate, AggSegRuleVO>> querySegRulesByPsn(
			UFLiteralDate startDate, UFLiteralDate endDate, String[] pk_psndocs)
			throws BusinessException;

}
