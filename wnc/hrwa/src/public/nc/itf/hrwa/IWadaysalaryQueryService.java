package nc.itf.hrwa;

import java.util.Map;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;

/**
 * 
 * @author ward
 * @date 2018��9��13��21:46:13
 * @desc �ṩ���Ӱ��/��ٿۿ� ȡ�������н�ʽӿ�
 * 
 */
public interface IWadaysalaryQueryService {
	/**
	 * @param pk_psndocs
	 *            ��Ա����
	 * @param begindate
	 *            ��ʼ����
	 * @param enddate
	 *            ��������
	 * @param daySalaryEnum
	 *            ȡ����� �����nc.vo.hrwa.wadaysalary.DaySalaryEnum
	 * @param pk_item_group
	 *            н�ʷ���
	 * @return
	 * @throws BusinessException
	 * @�������� ȡ�̶���Χ�ڲ�ͬ��н���Ŀ���н�ʵĻ��� edit by Ares.Tank ���н�Y�Ŀ�ֽM @ tank ע�� ȥ�����÷���
	 */
	/*
	 * public Map<String, HashMap<UFLiteralDate, UFDouble>>
	 * getTotalTbmDaySalaryMap(String[] pk_psndocs, UFLiteralDate begindate,
	 * UFLiteralDate enddate, int daySalaryEnum, String pk_item_group) throws
	 * BusinessException;
	 */

	/**
	 * @param pk_psndocs
	 *            ��Ա����
	 * @param begindate
	 *            ��ʼ����
	 * @param enddate
	 *            ��������
	 * @param taxflag
	 *            �Ƿ��˰ true ��˰ false����˰
	 * @param daySalaryEnum
	 *            ȡ����� �����nc.vo.hrwa.wadaysalary.DaySalaryEnum
	 * @param pk_item_group
	 *            н�ʷ���
	 * @return <��Աpk,<����,��н���>>
	 * @throws BusinessException
	 * @�������� ȡ�̶���Χ�ڲ�ͬ��н���Ŀ���н�ʵĻ��� edit by Ares.Tank ���н�Y�Ŀ�ֽM
	 */
	public Map<String, Map<String, Double>> getTotalDaySalaryMapWithoutRecalculate(String pk_org, String[] pk_psndocs,
			UFLiteralDate[] allDates, boolean flag, String pk_item_group) throws BusinessException;

	/**
	 * �@ȡ������нӋ��r�g
	 * 
	 * @param pk_loginuser
	 * @return
	 */
	public Double getCalcuTime(String pk_loginuser);

	/**
	 * 
	 * @param pk_psndocs
	 *            ��Ա����
	 * @param pk_wa_class
	 *            н�ʷ���
	 * @param cyear
	 *            н����
	 * @param cperiod
	 *            н����
	 * @param pk_wa_item
	 *            н����Ŀ����
	 * @param pk_group_item
	 *            н�Y�Ŀ�ֽM
	 * @return
	 * @throws BusinessException
	 *             edit by Ares.Tank ���н�Y�Ŀ�ֽM edit tank �޵��÷���ע��
	 */
	/*
	 * public Map<String, UFDouble> getTotalDaySalaryMapByWaItem(String[]
	 * pk_psndocs, String pk_wa_class, String cyear, String cperiod, String
	 * pk_wa_item) throws BusinessException;
	 */

	/**
	 * 
	 * @param pk_psndocs
	 *            ��Ա����
	 * @param pk_wa_class
	 *            н�ʷ���
	 * @param cyear
	 *            н����
	 * @param cperiod
	 *            н����
	 * @param pk_wa_item
	 *            н����Ŀ����
	 * @param pk_group_item
	 *            н�Y�Ŀ�ֽM
	 * @return
	 * @throws BusinessException
	 *             edit by Ares.Tank ���н�Y�Ŀ�ֽM
	 */
	/*
	 * public Map<String, UFDouble> getTotalDaySalaryMapByWaItem(String[]
	 * pk_psndocs, String pk_wa_class, String cyear, String cperiod, String
	 * pk_wa_item, String pk_group_item) throws BusinessException;
	 */

	/**
	 * 
	 * @param pk_psndocs
	 *            ��Ա����
	 * @param pk_wa_class
	 *            н�ʷ���
	 * @param cyear
	 *            н����
	 * @param cperiod
	 *            н����
	 * @param pk_wa_item
	 *            н����Ŀ����
	 * @param pk_group_item
	 *            н�Y�Ŀ�ֽM
	 * @return <��Աpk,���ܽ��>
	 * @throws BusinessException
	 *             edit by Ares.Tank ���н�Y�Ŀ�ֽM
	 */
	public Map<String, UFDouble> getTotalDaySalaryMapByWaItemWithoutRecalculate(String pk_org, String[] pk_psndocs,
			String pk_wa_class, String cyear, String cperiod, String pk_wa_item, String pk_group_item)
			throws BusinessException;

	/**
	 * 
	 * @param pk_org
	 * @param pk_psndoc
	 * @param pk_item_group
	 * @param dateList
	 *            ��Ҫ��������ڼ���
	 * @return <����,ʱн>
	 */
	public Map<String, Double> getHourSalaryByPsn(String pk_org, String pk_psndoc, String pk_item_group,
			UFLiteralDate[] dateList) throws BusinessException;

	/**
	 * 
	 * @param pk_psndocs
	 *            ��Ա����
	 * @param pk_wa_class
	 *            н�ʷ���
	 * @param cyear
	 *            н����
	 * @param cperiod
	 *            н����
	 * @param pk_wa_item
	 *            н����Ŀ����
	 * @param pk_group_item
	 *            н�Y�Ŀ�ֽM
	 * @return
	 * @throws BusinessException
	 *             edit by Ares.Tank ���н�Y�Ŀ�ֽM edit tank ע�� �޵��÷���ע��
	 *             2019��10��16��14:46:23
	 */
	/*
	 * public Map<String, UFDouble>
	 * getTotalDaySalaryMapByWaItemWithoutRecalculate(String[] pk_psndocs,
	 * String pk_wa_class, String cyear, String cperiod, String pk_wa_item,
	 * String pk_group_item) throws BusinessException;
	 */
	/**
	 * 
	 * @param pk_psndocs
	 *            ��Ա����
	 * @param begindate
	 *            ��ʼ����
	 * @param enddate
	 *            ��������
	 * @param pk_wa_item
	 *            н����Ŀ����
	 * @return
	 * @throws BusinessException
	 * @�������� ȡ�̶���Χ�ڲ�ͬн����Ŀ����ʱн �˽ӿ�û�н���н����Ŀ��������,�ݲ�ʹ��
	 * @tank ע�� 2019��10��16��14:14:04 �޹������÷���
	 */
	/*
	 * public Map<String, HashMap<UFLiteralDate, UFDouble>>
	 * getTbmHourSalaryMapByWaItem(String[] pk_psndocs, UFLiteralDate begindate,
	 * UFLiteralDate enddate, String pk_wa_item) throws BusinessException;
	 */
}
