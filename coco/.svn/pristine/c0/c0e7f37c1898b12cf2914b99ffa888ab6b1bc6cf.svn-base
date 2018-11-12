package nc.itf.ta;

import java.util.ArrayList;
import java.util.HashMap;

import nc.vo.bd.team.team01.entity.TeamItemVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psndoc.AssignCardDescriptor;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.uif2.LoginContext;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;

public interface ITBMPsndocManageMaintain {
	/**
	 * 分配考勤卡号；分配完的记录以数组返回
	 * 
	 * @param vos
	 * @param acd
	 * @return
	 * @throws BusinessException
	 */
	TBMPsndocVO[] assignCardNo(TBMPsndocVO[] vos, AssignCardDescriptor acd) throws BusinessException;
	/**
	 *  批量新增考勤档案
	 * @param context 
	 * @param vos ：批量新增列表页面选择的人员
	 * @param tbm_prop	： 考勤方式
	 * @param beginDate	：开始日期
	 * @param pk_place	：考勤地点
	 * @param isUpdatePsnCalendar:是否更新工作日历
	 * @return
	 * @throws BusinessException
	 */
	TBMPsndocVO[] batchInsert(LoginContext context,TBMPsndocVO[] vos, int tbm_prop, UFLiteralDate beginDate,
			String pk_place,boolean isUpdatePsnCalendar)
			throws BusinessException;
	/**
	 *  批量新增考勤档案
	 * @param context 
	 * @param vos ：批量新增列表页面选择的人员
	 * @param tbm_prop	： 考勤方式
	 * @param beginDate	：开始日期
	 * @param pk_place	：考勤地点
	 * @param isUpdatePsnCalendar:是否更新工作日历
	 * @return
	 * @throws BusinessException by he 
	 */
	TBMPsndocVO[] batchInsert(LoginContext context,TBMPsndocVO[] vos, int tbm_prop, UFLiteralDate beginDate,
			String pk_place, int tbm_weekform,int tbm_otcontrol,boolean isUpdatePsnCalendar)
			throws BusinessException;
	/**
	 * 批量更新考勤档案
	 * @param vos
	 * @param batchEditValue
	 * @return 
	 * @throws BusinessException
	 */
	public void batchUpdate(String pk_hrorg, TBMPsndocVO[] vos, HashMap<String, Object> batchEditValue)
			throws BusinessException;
	/**
	 * 删除考勤档案
	 * @param vo
	 */
	void delete(TBMPsndocVO vo) throws BusinessException;
	
	/**
	 * 删除考勤档案(批量)
	 * @param vo
	 */
	void delete(TBMPsndocVO[] vos) throws BusinessException;
	/**
	 * 新增考勤档案
	 * @param vo
	 * @param isUpdatePsnCalendar:是否更新工作日历
	 */
	TBMPsndocVO insert(TBMPsndocVO vo,boolean isUpdatePsnCalendar) throws BusinessException;
	/**
	 * 批量新增考勤档案
	 * @param vos
	 * @param isUpdatePsnCalendar是否更新工作日历
	 * @return
	 * @throws BusinessException
	 */
	TBMPsndocVO[] insert(TBMPsndocVO[] vos,boolean isUpdatePsnCalendar) throws BusinessException;
	
	/**
	 * 更新考勤档案
	 * 
	 * @param vos
	 */
	TBMPsndocVO[] update(TBMPsndocVO[] vos,boolean isUpdatePsnCalendar) throws BusinessException;
	/**
	 * 导入更新考勤卡号
	 * @param pk_hrorg
	 * @param vos
	 * @param isOverRide
	 * @return
	 * @throws BusinessException
	 */
	ArrayList<String>[] updateTbmCard(String pk_hrorg, GeneralVO[] vos, boolean isOverRide) throws BusinessException;
	
	/**
	 * 校验考勤档案
	 * @param vos
	 * @throws BusinessException
	 */
	void check(TBMPsndocVO[] vos) throws BusinessException;
	/**
	 * 校验考勤档案
	 * @param vo
	 * @throws BusinessException
	 */
	void check(TBMPsndocVO vo) throws BusinessException;
	
	/**
	 * 安装时间管理时同步班组人员到考勤档案
	 * @throws BusinessException
	 */
	void syncTeamPsn(TeamItemVO[] vos) throws BusinessException;
	
	 String checkTBMPsndocDate(TBMPsndocVO vo) throws BusinessException;
	 
	 TBMPsndocVO insert(PsndocVO[] psndocvos,TBMPsndocVO vos,PsnJobVO vo,boolean isUpdatePsnCalendar,boolean isNew) throws BusinessException;
	 
	/**
	 * 更新date_daytype字段
	 * 
	 */
	 int update(String date,String pk_psndoc,int type,String pk_org) throws BusinessException;
}
