package nc.itf.ta;

import java.util.HashMap;
import java.util.Map;

import nc.jdbc.framework.SQLParameter;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.uif2.LoginContext;

public interface ITBMPsndocQueryMaintain {
	/**
	 * 根据条件查询考勤档案
	 * @param context
	 * @param condition
	 * @return TBMPsndocVO[]
	 * @throws BusinessException
	 */
	public TBMPsndocVO[] queryByCondition(LoginContext context, String condition) throws BusinessException;
	/**
	 * 根据条件查询考勤档案
	 * @param context
	 * @param fromWhereSQL
	 * @return TBMPsndocVO[]
	 * @throws BusinessException
	 */
	TBMPsndocVO[] queryByCondition(LoginContext context, FromWhereSQL fromWhereSQL) throws BusinessException;

	/**
	 * 校验是否已经存在考勤卡号，如有已经存在的，以数组返回
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	TBMPsndocVO[] checkCardNo(TBMPsndocVO[] vos) throws BusinessException;
	
	/**
	 * 根据人员查询
	 * @param context
	 * @param fromWhereSQL
	 * @return
	 * @throws BusinessException
	 */
	TBMPsndocVO[] queryByPsnInfo(LoginContext context, FromWhereSQL fromWhereSQL) throws BusinessException;
	
	/**
	 * 查询该人员未结束的考勤
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	TBMPsndocVO queryUnFinishPsnDoc(String pk_psndoc) throws BusinessException;
	
	/**
	 * 查询该人员未结束的考勤
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	Map<String, TBMPsndocVO> queryUnFinishPsnDoc(String[] pk_psndocs) throws BusinessException; 
	
	/**
	 * 根据条件查询
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	TBMPsndocVO[] queryByCondition(String condition) throws BusinessException;
	/**
	 * 根据条件查询
	 * @param condition
	 * @param params
	 * @return
	 * @throws BusinessException
	 */
	public TBMPsndocVO[] queryByCondition(String condition, SQLParameter params) throws BusinessException;
	/**
	 * 根据主键查询考勤档案信息
	 * @param pks
	 * @return
	 * @throws BusinessException
	 */
	public Object[] queryPsndocVOByPks(String[] pks) throws BusinessException;
	
	String[] queryPsndocPks(LoginContext context, String[] strSelectFields, String strFromPart, String strWhere, String strOrder,
			HashMap<String, String> hashMap, String resourceCode) throws BusinessException;
	
	/***************************************************************************
	 * 根据条件查询人员信息主键数组<br>
	 * @param context 登陆信息
	 * @param fromWhereSQL 查询条件
	 * @param condition 其它条件
	 * @throws BusinessException
	 ***************************************************************************/
	public String[] queryPsndocPks(LoginContext context, FromWhereSQL fromWhereSQL, String condition) throws BusinessException;
	
	/**
	 * 简单地取传入的UFDateTime的前10位作为日期，然后往前推一天，往后推一天，然后查询此pk_psndoc在这三天范围内有效的考勤档案记录。
	 * 如果没有，返回null；有的话，返回最新的一条
	 * @param pk_psndoc
	 * @param dateTime
	 * @return
	 * @throws BusinessException
	 */
	public TBMPsndocVO queryByPsndocAndDateTime(String pk_psndoc,UFDateTime dateTime)throws BusinessException;
	
	/**
	 * 只查询当天的考勤档案
	 * @param pk_psndoc
	 * @param dateTime
	 * @return
	 * @throws BusinessException
	 */
	public TBMPsndocVO queryByPsndocAndDate(String pk_psndoc,UFLiteralDate date)throws BusinessException;
	
	/**
	 * 取得到职日期
	 * @param pk_psnjob
	 * @param busLitDate：当前业务时间
	 * @param pk_org：当前业务时间
	 * @return
	 */
	public UFLiteralDate getIndutyDate(String pk_psnjob,String pk_org);
	
	/**
	 * 查询工作记录开始日期
	 * @param pk_psnjob
	 * @return
	 */
	public UFLiteralDate queryBeginDateFromPsnjob(String pk_psnjob);
	
}
	

