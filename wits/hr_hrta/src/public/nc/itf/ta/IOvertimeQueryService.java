package nc.itf.ta;

import java.util.Map;

import nc.itf.ta.bill.IApproveBillQueryService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.overtime.OvertimeCommonVO;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.period.PeriodVO;

/**
 * �Ӱ�������,�Ӱ������������������ֻ�õ��ô��������ѯ�������������ݴ��������ձ�����
 * @author zengcheng
 *
 */
public interface IOvertimeQueryService extends IApproveBillQueryService<OvertimeRegVO,OvertimeCommonVO>{
	
	/**
	 * ���ҼӰ൥�Ĺ�������
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	UFLiteralDate queryBelongToDate(OvertimeCommonVO vo)throws BusinessException;
	
	/**
	 * ��ѯ��Ա��Χ�����ڷ�Χ�ڵ�������Ч�Ӱ൥�ݣ�����ͨ��+������������Ч+�Ǽǣ�
	 * @return��map��key����Ա��������������value��map��key�ǼӰ൥�����գ�value�ǼӰ൥����
	 * @throws BusinessException
	 */
	Map<String, Map<UFLiteralDate, OvertimeRegVO[]>> queryOvertimeVOsByCondDate(String pk_org,String psnCond,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * ��ѯĳ����Ա���ڷ�Χ�ڵ�������Ч�Ӱ൥��
	 * @param pk_org
	 * @param pk_psndoc
	 * @param beginDate
	 * @param endDate
	 * @return��map��key�ǼӰ൥�����գ�value�ǼӰ൥����
	 * @throws BusinessException
	 */
	Map<UFLiteralDate, OvertimeRegVO[]> queryOvertimeVOsByPsnDate(String pk_org,String pk_psndoc,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * ����Ӱ���Ա������ҵ��Ԫ����Ϊ���ռӰ������ʹ�õ���ҵ��Ԫ����hrorg
	 * @param vos
	 * @throws BusinessException
	 */
	public OvertimeCommonVO[] processJobOrg(OvertimeCommonVO[] vos)throws BusinessException;
	/**
	 * ����ʹ�ã���ѯ�����ڼ�ļӰ��¼
	 * @param pk_org
	 * @param pk_psndoc
	 * @param tbmYear
	 * @param tbmMonth
	 * @param pk_timeitem
	 * @return
	 * @throws BusinessException
	 */
	public OvertimeRegVO[] queryRegByPsnAndDateScope(String pk_org,String pk_psndoc,String tbmYear,String tbmMonth,String pk_timeitem)throws BusinessException;
	
	Map<String, OvertimeRegVO[]> queryAllSuperVOByApproveDateByPsndocInSQLDate(String pk_org,String psndocInSQL,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	Map<String, OvertimeRegVO[]> queryAllSuperVOIncEffectiveByPsndocInSQLDateWithApprovedDate(String paramString1, String paramString2, UFLiteralDate paramUFLiteralDate1, UFLiteralDate paramUFLiteralDate2)
		    throws BusinessException;
}
