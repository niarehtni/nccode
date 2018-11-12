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
	 * ����������ѯ���ڵ���
	 * @param context
	 * @param condition
	 * @return TBMPsndocVO[]
	 * @throws BusinessException
	 */
	public TBMPsndocVO[] queryByCondition(LoginContext context, String condition) throws BusinessException;
	/**
	 * ����������ѯ���ڵ���
	 * @param context
	 * @param fromWhereSQL
	 * @return TBMPsndocVO[]
	 * @throws BusinessException
	 */
	TBMPsndocVO[] queryByCondition(LoginContext context, FromWhereSQL fromWhereSQL) throws BusinessException;

	/**
	 * У���Ƿ��Ѿ����ڿ��ڿ��ţ������Ѿ����ڵģ������鷵��
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	TBMPsndocVO[] checkCardNo(TBMPsndocVO[] vos) throws BusinessException;
	
	/**
	 * ������Ա��ѯ
	 * @param context
	 * @param fromWhereSQL
	 * @return
	 * @throws BusinessException
	 */
	TBMPsndocVO[] queryByPsnInfo(LoginContext context, FromWhereSQL fromWhereSQL) throws BusinessException;
	
	/**
	 * ��ѯ����Աδ�����Ŀ���
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	TBMPsndocVO queryUnFinishPsnDoc(String pk_psndoc) throws BusinessException;
	
	/**
	 * ��ѯ����Աδ�����Ŀ���
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	Map<String, TBMPsndocVO> queryUnFinishPsnDoc(String[] pk_psndocs) throws BusinessException; 
	
	/**
	 * ����������ѯ
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	TBMPsndocVO[] queryByCondition(String condition) throws BusinessException;
	/**
	 * ����������ѯ
	 * @param condition
	 * @param params
	 * @return
	 * @throws BusinessException
	 */
	public TBMPsndocVO[] queryByCondition(String condition, SQLParameter params) throws BusinessException;
	/**
	 * ����������ѯ���ڵ�����Ϣ
	 * @param pks
	 * @return
	 * @throws BusinessException
	 */
	public Object[] queryPsndocVOByPks(String[] pks) throws BusinessException;
	
	String[] queryPsndocPks(LoginContext context, String[] strSelectFields, String strFromPart, String strWhere, String strOrder,
			HashMap<String, String> hashMap, String resourceCode) throws BusinessException;
	
	/***************************************************************************
	 * ����������ѯ��Ա��Ϣ��������<br>
	 * @param context ��½��Ϣ
	 * @param fromWhereSQL ��ѯ����
	 * @param condition ��������
	 * @throws BusinessException
	 ***************************************************************************/
	public String[] queryPsndocPks(LoginContext context, FromWhereSQL fromWhereSQL, String condition) throws BusinessException;
	
	/**
	 * �򵥵�ȡ�����UFDateTime��ǰ10λ��Ϊ���ڣ�Ȼ����ǰ��һ�죬������һ�죬Ȼ���ѯ��pk_psndoc�������췶Χ����Ч�Ŀ��ڵ�����¼��
	 * ���û�У�����null���еĻ����������µ�һ��
	 * @param pk_psndoc
	 * @param dateTime
	 * @return
	 * @throws BusinessException
	 */
	public TBMPsndocVO queryByPsndocAndDateTime(String pk_psndoc,UFDateTime dateTime)throws BusinessException;
	
	/**
	 * ֻ��ѯ����Ŀ��ڵ���
	 * @param pk_psndoc
	 * @param dateTime
	 * @return
	 * @throws BusinessException
	 */
	public TBMPsndocVO queryByPsndocAndDate(String pk_psndoc,UFLiteralDate date)throws BusinessException;
	
	/**
	 * ȡ�õ�ְ����
	 * @param pk_psnjob
	 * @param busLitDate����ǰҵ��ʱ��
	 * @param pk_org����ǰҵ��ʱ��
	 * @return
	 */
	public UFLiteralDate getIndutyDate(String pk_psnjob,String pk_org);
	
	/**
	 * ��ѯ������¼��ʼ����
	 * @param pk_psnjob
	 * @return
	 */
	public UFLiteralDate queryBeginDateFromPsnjob(String pk_psnjob);
	
}
	

