package nc.itf.hrwa;

import java.util.HashMap;
import java.util.Map;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
/**
 * 
 * @author ward
 * @date 2018年9月13日21:46:13
 * @desc 提供给加班费/请假扣款 取考勤相关薪资接口
 *
 */
public interface IWadaysalaryQueryService {
	/**
	 * @param pk_psndocs 人员主键
	 * @param begindate 开始日期
	 * @param enddate 结束日期
	 * @param daySalaryEnum 取数类别     详见：nc.vo.hrwa.wadaysalary.DaySalaryEnum
	 * @return
	 * @throws BusinessException
	 * @功能描述 取固定范围内不同计薪类别的考勤薪资的汇总
	 */
	public Map<String, HashMap<UFLiteralDate, UFDouble>> getTotalTbmDaySalaryMap(String[] pk_psndocs,UFLiteralDate begindate,UFLiteralDate enddate,int daySalaryEnum) throws BusinessException;
	/**
	 * 
	 * @param pk_psndocs 人员数组
	 * @param pk_wa_class 薪资方案
	 * @param cyear 薪资年
	 * @param cperiod 薪资月
	 * @param pk_wa_item 薪资项目主键
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, UFDouble> getTotalDaySalaryMapByWaItem(String[] pk_psndocs,String pk_wa_class,String cyear,String cperiod,String pk_wa_item)
			throws BusinessException;
	/**
	 * 
	 * @param pk_psndocs 人员主键
	 * @param begindate 开始日期
	 * @param enddate 结束日期
	 * @param pk_wa_item  薪资项目主键
	 * @return 
	 * @throws BusinessException
	 * @功能描述 取固定范围内不同薪资项目考勤时薪
	 */
	public Map<String, HashMap<UFLiteralDate, UFDouble>> getTbmHourSalaryMapByWaItem(String[] pk_psndocs,UFLiteralDate begindate,UFLiteralDate enddate,String pk_wa_item)
			throws BusinessException;
}
