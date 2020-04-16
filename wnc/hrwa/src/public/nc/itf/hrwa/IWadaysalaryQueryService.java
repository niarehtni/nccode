package nc.itf.hrwa;

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
	 * @param pk_psndocs
	 *            人员主键
	 * @param begindate
	 *            开始日期
	 * @param enddate
	 *            结束日期
	 * @param daySalaryEnum
	 *            取数类别 详见：nc.vo.hrwa.wadaysalary.DaySalaryEnum
	 * @param pk_item_group
	 *            薪资分组
	 * @return
	 * @throws BusinessException
	 * @功能描述 取固定范围内不同计薪类别的考勤薪资的汇总 edit by Ares.Tank 添加薪資項目分組 @ tank 注释 去除调用方法
	 */
	/*
	 * public Map<String, HashMap<UFLiteralDate, UFDouble>>
	 * getTotalTbmDaySalaryMap(String[] pk_psndocs, UFLiteralDate begindate,
	 * UFLiteralDate enddate, int daySalaryEnum, String pk_item_group) throws
	 * BusinessException;
	 */

	/**
	 * @param pk_psndocs
	 *            人员主键
	 * @param begindate
	 *            开始日期
	 * @param enddate
	 *            结束日期
	 * @param taxflag
	 *            是否扣税 true 扣税 false不扣税
	 * @param daySalaryEnum
	 *            取数类别 详见：nc.vo.hrwa.wadaysalary.DaySalaryEnum
	 * @param pk_item_group
	 *            薪资分组
	 * @return <人员pk,<日历,日薪金额>>
	 * @throws BusinessException
	 * @功能描述 取固定范围内不同计薪类别的考勤薪资的汇总 edit by Ares.Tank 添加薪資項目分組
	 */
	public Map<String, Map<String, Double>> getTotalDaySalaryMapWithoutRecalculate(String pk_org, String[] pk_psndocs,
			UFLiteralDate[] allDates, boolean flag, String pk_item_group) throws BusinessException;

	/**
	 * 獲取本次日薪計算時間
	 * 
	 * @param pk_loginuser
	 * @return
	 */
	public Double getCalcuTime(String pk_loginuser);

	/**
	 * 
	 * @param pk_psndocs
	 *            人员数组
	 * @param pk_wa_class
	 *            薪资方案
	 * @param cyear
	 *            薪资年
	 * @param cperiod
	 *            薪资月
	 * @param pk_wa_item
	 *            薪资项目主键
	 * @param pk_group_item
	 *            薪資項目分組
	 * @return
	 * @throws BusinessException
	 *             edit by Ares.Tank 添加薪資項目分組 edit tank 无调用方法注释
	 */
	/*
	 * public Map<String, UFDouble> getTotalDaySalaryMapByWaItem(String[]
	 * pk_psndocs, String pk_wa_class, String cyear, String cperiod, String
	 * pk_wa_item) throws BusinessException;
	 */

	/**
	 * 
	 * @param pk_psndocs
	 *            人员数组
	 * @param pk_wa_class
	 *            薪资方案
	 * @param cyear
	 *            薪资年
	 * @param cperiod
	 *            薪资月
	 * @param pk_wa_item
	 *            薪资项目主键
	 * @param pk_group_item
	 *            薪資項目分組
	 * @return
	 * @throws BusinessException
	 *             edit by Ares.Tank 添加薪資項目分組
	 */
	/*
	 * public Map<String, UFDouble> getTotalDaySalaryMapByWaItem(String[]
	 * pk_psndocs, String pk_wa_class, String cyear, String cperiod, String
	 * pk_wa_item, String pk_group_item) throws BusinessException;
	 */

	/**
	 * 
	 * @param pk_psndocs
	 *            人员数组
	 * @param pk_wa_class
	 *            薪资方案
	 * @param cyear
	 *            薪资年
	 * @param cperiod
	 *            薪资月
	 * @param pk_wa_item
	 *            薪资项目主键
	 * @param pk_group_item
	 *            薪資項目分組
	 * @return <人员pk,汇总金额>
	 * @throws BusinessException
	 *             edit by Ares.Tank 添加薪資項目分組
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
	 *            需要计算的日期集合
	 * @return <日期,时薪>
	 */
	public Map<String, Double> getHourSalaryByPsn(String pk_org, String pk_psndoc, String pk_item_group,
			UFLiteralDate[] dateList) throws BusinessException;

	/**
	 * 
	 * @param pk_psndocs
	 *            人员数组
	 * @param pk_wa_class
	 *            薪资方案
	 * @param cyear
	 *            薪资年
	 * @param cperiod
	 *            薪资月
	 * @param pk_wa_item
	 *            薪资项目主键
	 * @param pk_group_item
	 *            薪資項目分組
	 * @return
	 * @throws BusinessException
	 *             edit by Ares.Tank 添加薪資項目分組 edit tank 注释 无调用方法注释
	 *             2019年10月16日14:46:23
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
	 *            人员主键
	 * @param begindate
	 *            开始日期
	 * @param enddate
	 *            结束日期
	 * @param pk_wa_item
	 *            薪资项目主键
	 * @return
	 * @throws BusinessException
	 * @功能描述 取固定范围内不同薪资项目考勤时薪 此接口没有进行薪资项目分组适配,暂不使用
	 * @tank 注释 2019年10月16日14:14:04 无关联调用方法
	 */
	/*
	 * public Map<String, HashMap<UFLiteralDate, UFDouble>>
	 * getTbmHourSalaryMapByWaItem(String[] pk_psndocs, UFLiteralDate begindate,
	 * UFLiteralDate enddate, String pk_wa_item) throws BusinessException;
	 */
}
