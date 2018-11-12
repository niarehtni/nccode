package nc.itf.ta;

import java.util.Map;

import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.bd.holiday.HolidayVO;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.teamcalendar.TeamInfoCalendarVO;

public interface IPsnCalendarManageService {

	/**
	 * �������պ��Űࡣ���ѭ���Ű�ʱ��ѡ�����������ȡ��������Щȡ�����Ű���Ҫ�����µļ����������
	 * @param pk_org
	 * @param pk_holiday
	 * @throws BusinessException
	 */
	void arrangeAfterHolidayInsert(String pk_org,String pk_holiday) throws BusinessException;
	
	/**
	 * �������պ��Űࡣ���ѭ���Ű�ʱ��ѡ�����������ȡ��������Щȡ�����Ű���Ҫ�����µļ����������
	 * @param orgVO
	 * @param newHoliday
	 * @throws BusinessException
	 */
	void arrangeAfterHolidayInsert(OrgVO orgVO,HolidayVO newHoliday) throws BusinessException;

	/**
	 * �޸ļ��պ��Ű�
	 * @param pk_org
	 * @param pk_holiday
	 * @throws BusinessException
	 */
	void arrangeAfterHolidayUpdate(String pk_org, HolidayVO oldHolidayVO,String pk_holiday) throws BusinessException;
	
	/**
	 * �޸ļ��պ��Ű�
	 * @param orgVO
	 * @param newHoliday
	 * @throws BusinessException
	 */
	void arrangeAfterHolidayUpdate(OrgVO orgVO, HolidayVO oldHolidayVO,HolidayVO newHoliday) throws BusinessException;

	/**
	 * ɾ������ǰ�Ű�
	 * @param pk_org
	 * @param pk_holiday
	 * @throws BusinessException
	 */
	void arrangeBeforeHolidayDelete(String pk_org,String pk_holiday) throws BusinessException;
	
	/**
	 * ɾ������ǰ�Ű�
	 * @param orgVO
	 * @param deleteHoliday
	 * @throws BusinessException
	 */
	void arrangeBeforeHolidayDelete(OrgVO orgVO,HolidayVO deleteHoliday) throws BusinessException;

	/**
	 * �Զ��Ű�
	 * @param pk_hrorg
	 * @throws BusinessException
	 */
	String autoArrange_RequiresNew(String pk_hrorg) throws BusinessException;

	/**
	 * ���¼��뿼�ڵ�������Ա�����Զ��Ű�
	 * @param pk_hrorg
	 * @param pk_psndoc
	 * @throws BusinessException
	 */
	void autoArrange(String pk_hrorg,String pk_psndoc) throws BusinessException;
	
	void autoArrange(String pk_hrorg, String pk_psndoc,TBMPsndocVO[] psndocVOs) throws BusinessException;

	/**
	 * ���¼��뿼�ڵ�������Ա�����Զ��Ű�
	 * @param pk_org
	 * @param pk_psndocs
	 * @throws BusinessException
	 */
	void autoArrange(String pk_hrorg,String[] pk_psndocs) throws BusinessException;
	
	/**
	 * ���൥������ͨ��������Űࡣ��Ϊ������������ı���˳���ڵ������֮ǰ��Ϊ�˱���ѭ��������������
	 * ����ΪAggregatedValueObject��������AggChangeClassVO
	 * @param pk_hrorg
	 * @param pk_changechasshs
	 * @throws BusinessException
	 */
	void arrangeAfterApprove(String pk_hrorg,AggregatedValueObject[] aggBills)throws BusinessException;
	
	/**
	 * �����������ǼǺ�����Ű࣬����������������Ǽ�
	 * ��Ϊ������������ı���˳���ڵ������֮ǰ��Ϊ�˱���ѭ������������������Ϊsupervo��������ChangeClassRegVO
	 * @param pk_hrorg
	 * @param regVOs
	 * @throws BusinessException
	 */
	void arrangeAfterRegister(String pk_hrorg,SuperVO[] regVOs)throws BusinessException;
	
	/**
	 * ��������ǼǺ�����Ű�
	 * @param pk_hrorg
	 * @param regVO
	 * @throws BusinessException
	 */
	void arrangeAfterRegister(String pk_hrorg,SuperVO regVO)throws BusinessException;
	
	/**
	 * �޸Ŀ��ڵ�������ǰ�����ô˷���
	 * @param pk_hrorg
	 * @param beforeUpdateVOs
	 * @throws BusinessException
	 */
	void processBeforeUpdateTBMPsndoc(String pk_hrorg,SuperVO[] updateVOs)throws BusinessException;
	
	/**
	 * ɾ�����ڵ���ǰ�����ô˷���
	 * @param pk_hrorg
	 * @param pk_tbmpsndocs
	 * @throws BusinessException
	 */
	void processBeforeDeleteTBMPsnodc(String pk_hrorg,String[] pk_tbmpsndocs)throws BusinessException;
	
	/**
	 * ��ɾ�����ǰ���ô˷���������ɾ�����ô˰�εĹ�������
	 * @param pk_shift
	 * @throws BusinessException
	 */
	void processBeforeDeleteShift(String pk_shift)throws BusinessException;
	
	/**
	 * ���޸İ�κ��޸Ĵ˷��������ڵ������ô˰�εĹ�������
	 * @param pk_org
	 * @param oldShiftVO
	 * @param pk_shift
	 * @throws BusinessException
	 */
	void processAfterUpdateShift(AggShiftVO oldShiftVO,String pk_shift)throws BusinessException;
	
	/**
	 * �������ڷ�Χ����Ա����ɾ����������
	 * @param pk_org
	 * @param fromWhereSQL
	 * @param beginDate
	 * @param endDate
	 * @throws BusinessException
	 */
	void deleteByCondAndDateScope(String pk_org,FromWhereSQL fromWhereSQL,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * ����Ա�Ĺ����������������ͬ�������ڰ����Ű�ڵ��ͬ����ť
	 * @param pk_team
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	PsnJobCalendarVO[] sync2TeamCalendar(String pk_hrorg, String pk_team,String[] pk_psndocs,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * ����ʹ�ã�ͬ��������������Ա
	 * @param pk_team
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	PsnJobCalendarVO[] sync2TeamCalendar(String pk_hrorg, String pk_team,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;

	/**
	 * �����Ű��޸ı����ͬ����Ա��������
	 * @param teamVOs
	 * @param syncPrivateDates���Ƿ�ͬ���ڰ����Ű�����޸��˵���Ա����
	 * @throws BusinessException
	 */
	void sync2TeamCalendarAfterSave(String pk_hrorg, TeamInfoCalendarVO[] teamVOs)throws BusinessException;
	
	/**
	 * ����ѭ���Ű��ͬ����Ա��������
	 * @param pk_org��ҵ��Ԫ����
	 * @param modifiedMap <��������, <����, �������>>
	 * @param beginDate
	 * @param endDate
	 * @param calendarPks
	 * @param isHolidayCancel
	 * @param syncPrivateDates���Ƿ�ͬ���ڰ����Ű�����޸��˵���Ա����
	 * @throws BusinessException
	 */
	void sync2TeamCalendarAfterCircularlyArrange(String pk_org,Map<String, Map<String, String>> modifiedMap,
			UFLiteralDate beginDate,UFLiteralDate endDate,boolean isHolidayCancel)throws BusinessException;

}
