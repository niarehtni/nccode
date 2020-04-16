package nc.itf.hrwa;


/**
 * 日薪重构,考勤日薪合并,此类不再使用
 * @author tank
 * 2019年10月16日14:34:43
 */
@Deprecated
public interface IWaTbmdaysalaryService {
	
	/**
	 * 通過人力資源組織，計算考勤日薪
	 * @param pk_hrorg 
	 * @param calculDate
	 * @return
	 * @throws BusinessException
	 * <a> 后台任务方法暂不使用 tank 2019年10月15日21:35:19 </a>
	 * @see nc.itf.hrwa.IWadaysalaryService.calculSalaryByHrorg(String, UFLiteralDate)
	 */
	//public void calculTbmSalaryByHrorg(String pk_hrorg,UFLiteralDate calculDate) throws BusinessException;
	/**
	 * 重算部分人员的考勤日薪
	 * @param pk_psnorg
	 * @param calculDate
	 * @param pk_psndoc
	 * @param pk_wa_items
	 * @throws BusinessException
	 * <a> 后台任务方法暂不使用 tank 2019年10月15日21:35:19 </a>
	 */
	//public void calculTbmSalaryByWaItem(String pk_hrorg,UFLiteralDate calculDate,String pk_psndoc,String[] pk_wa_items) throws BusinessException;
	/**
	 * 刪除指定日期的日薪數據
	 * @param pk_hrorg
	 * @param calculdate
	 * @param continueTime
	 * @throws BusinessException
	 * <a> 后台任务方法暂不使用 tank 2019年10月15日21:35:19 </a>
	 */
	//public void deleteTbmDaySalary(String pk_hrorg,UFLiteralDate calculdate,int continueTime)throws BusinessException;
	/**
	 * 檢驗指定範圍內的日薪是否計算成功，如未計算，則重新計算
	 * @param pk_hrorg
	 * @param calculdate
	 * @param checkrange
	 * @throws BusinessException
	 * <a> 后台任务方法暂不使用 tank 2019年10月15日21:35:19 </a>
	 */
	//public void checkTbmDaySalaryAndCalculSalary(String pk_hrorg,UFLiteralDate calculdate,int checkrange) throws BusinessException;
	/**
	 * 檢驗指定範圍內的考勤薪资是否計算成功，如未計算，則重新計算，如计算结果错误，也重新计算
	 * @param pk_psndocs
	 * @param begindate
	 * @param enddate
	 * @throws BusinessException
	 * @ 考勤日薪已经和定调资日薪合并
	 */
	/*public void checkTbmDaysalaryAndRecalculate(String pk_org,String[] pk_psndocs,UFLiteralDate begindate,UFLiteralDate enddate,
			String pk_item_group) throws BusinessException;*/

	/**
	 * tank 2019年10月16日14:17:41 日薪重构
	 */
	//public double getTbmSalaryNum(String pk_hrorg, UFLiteralDate calculDate, int tbmnumtype) throws BusinessException;
}
