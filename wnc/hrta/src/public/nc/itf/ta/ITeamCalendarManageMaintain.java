package nc.itf.ta;
 
import java.util.List;

import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.teamcalendar.AggTeamCalendarVO;
import nc.vo.ta.teamcalendar.TeamInfoCalendarVO;

public interface ITeamCalendarManageMaintain {
	/**
	 * ��������
	 * @param pk_org,HR��֯����
	 * @param beginDate
	 * @param endDate
	 * @param condition
	 * @param oldShift��ԭ��Ρ����Ϊ�գ��򽫷������������а�ζ�����Ϊ�°�Σ�����ֻ�ǽ�������������ԭ���ΪoldShift�ĵ���Ϊ�°��
	 * @param newShift���°��
	 * @param withOldShift,�Ƿ���ԭ��Ρ�Ϊtrue����ʾԭ���ΪoldShift�Ĳŵ�����Ϊfalse����ʾ����ԭ�����ʲô��������
	 */
	void batchChangeShift(String pk_org,FromWhereSQL fromWhereSQL,UFLiteralDate beginDate,UFLiteralDate endDate,boolean withOldShift,String oldShift,String newShift) throws BusinessException;
	
	//�޸ĺ���򵼵���������
	void batchChangeShiftNew(String pk_org, String[] bz_pks,
			UFLiteralDate beginDate, UFLiteralDate endDate,
			boolean withOldShift, String oldShift, String newShift)
			throws BusinessException;
	
	/**
	 * ѭ���Ű�
	 * @param pk_org,HR��֯����
	 * @param beginDate����ʼ����
	 * @param endDate����������
	 * @param condition��ѭ���Ű���Ա����
	 * @param calendarPks�������������
	 * @param isHolidayCancel���������Ű��Ƿ�ȡ����trueȡ����false�վ�
	 * @return
	 */
	TeamInfoCalendarVO[]
	circularArrange(String pk_org,
			String[] pk_teams,UFLiteralDate beginDate,UFLiteralDate endDate,String[] calendarPks,boolean isHolidayCancel,boolean overrideExistCalendar, boolean needLog) throws BusinessException;
	/**
	 * ���湤�����������ڹ����������׽��汣��
	 * ,TeamInfoCalendarVO[]��ֻ�洢�仯�˵İ���
	 * TeamInfoCalendarVO��map��ֻ�洢�仯�˵�����
	 * 
	 * @param pk_org,HR��֯����
	 * @param busilog,�Ƿ��¼ҵ����־
	 */
	TeamInfoCalendarVO[]   
	save(String pk_org,TeamInfoCalendarVO[] vos,boolean busilog) throws BusinessException;

	
	/**
	 * ���湤�����������ڰ��鹤���������ýڵ���޸ı���
	 * ,TeamInfoCalendarVO[]��ֻ�洢�仯�˵İ���
	 * TeamInfoCalendarVO��map��ֻ�洢�仯�˵�����</br>
	 * �ڰ��鹤���������ýڵ㣬�����ǵ���޸İ�ť��ֻ�޸İ����Ű棬���޸�����������</br>
	 * �˷�������Save��������ͬ���Ǵ˷����Ѱ��鹤��������ԭ�����������������²����˱��
	 * �൱�ڲ���������������
	 * @param pk_org,HR��֯����
	 * @param busilog,�Ƿ��¼ҵ����־
	 */
	TeamInfoCalendarVO[]   
			saveNODateype(String pk_org,TeamInfoCalendarVO[] vos,boolean busilog) throws BusinessException;
	
	
	/**
	 * ���湤�����������ڰ������Ű�
	 * @param pk_org,HR��֯����
	 * TeamInfoCalendarVO��map��ֻ�洢�仯�˵�����
	 */
	TeamInfoCalendarVO
	save(String pk_org,TeamInfoCalendarVO vo) throws BusinessException;
	
	/**
	 * ����������������Ű�
	 * @param psndocs ������Ϣ
	 * @param firstDate ��������1
	 * @param secondDate ��������2
	 * @author Ares.Tank 2018-9-6 15:15:10
	 */
	void batchChangeDateType(String pk_hrorg,String[] pkTeams,UFLiteralDate firstDate,UFLiteralDate secondDate) throws BusinessException ;
	/**
	 * ����������������Ű�
	 * @param psndocs ������Ϣ
	 * @param firstDate ��������1
	 * @param secondDate ��������2
	 * @author he 2018-9-6 15:15:10
	 */
	List<AggTeamCalendarVO>changeDateType(String pk_hrorg,String[] pkTeams,UFLiteralDate firstDate,UFLiteralDate secondDate) throws BusinessException ;
	/**
	 * ���������������Ű�
	 * @param psndocs ������Ϣ
	 * @param date ��Ҫ���������
	 * @param ����������,@see HolidayVo 
	 * @author Ares.Tank 2018-9-6 15:15:10
	 */
	void batchChangeDateType4OneDay(String pk_hrorg,String[] pkTeams,UFLiteralDate ChangeDate,Integer dateType) throws BusinessException ;
	


	
}
