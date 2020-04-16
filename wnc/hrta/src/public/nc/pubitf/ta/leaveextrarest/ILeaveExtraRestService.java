package nc.pubitf.ta.leaveextrarest;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.OTBalanceDetailVO;
import nc.vo.ta.overtime.OTLeaveBalanceVO;

public interface ILeaveExtraRestService {
	/**
	 * 按給定固定時薪及起迄時間結算（批量）
	 * 
	 * 業務邏輯：根據給定的日期、人員，將所有最長可休日期 = 結算日期的、未結算的員工外加補休進行結算
	 * 
	 * @param pk_org
	 *            組織
	 * @param pk_psndocs
	 *            人員列表
	 * @param settleDate
	 *            結算日期
	 * @param isForce
	 *            強制結算：TRUE時不檢查結算日期，強制結算指定所有人的通常用於離職結算
	 * @throws BusinessException
	 */
	void settledByExpiryDate(String pk_org, String[] pk_psndocs, UFLiteralDate settleDate, Boolean isForce)
			throws BusinessException;

	/**
	 * 根據人員及起迄日期取外加補休OTLeaveBalanceVO集合
	 * 
	 * @param pk_org
	 *            組織PK
	 * @param pk_psndocs
	 *            人員PK數組
	 * @param pk_depts
	 *            部門PK數組
	 * @param isTermLeave
	 *            是否留停
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            截止日期
	 * @param pk_leavetypecopy
	 *            休假類別
	 * @param isSettled
	 *            是否結算
	 * @param isLeave
	 *            是否离职
	 * @return
	 * @throws BusinessException
	 */
	OTLeaveBalanceVO[] getLeaveExtHoursByType(String pk_org, String[] pk_psndocs, String[] pk_depts,
			UFBoolean isTermLeave, UFLiteralDate beginDate, UFLiteralDate endDate, String pk_leavetypecopy,
			boolean isSettled, boolean isLeave) throws BusinessException;

	/**
	 * 多線程調用getLeaveExtHoursByType計算外加補休集合
	 * 
	 * @param pk_org
	 *            組織PK
	 * @param pk_psndocs
	 *            人員PK數組
	 * @param pk_depts
	 *            部門PK數組
	 * @param isTermLeave
	 *            是否留停
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            截止日期
	 * @param pk_leavetypecopy
	 *            休假類別
	 * @param isSettled
	 *            是否結算
	 * @param isLeave
	 *            是否离职
	 * @return
	 * @throws BusinessException
	 */
	OTLeaveBalanceVO[] getLeaveExtHoursByType_MT(String pk_org, String[] pk_psndocs, String[] pk_depts,
			UFBoolean isTermLeave, UFLiteralDate beginDate, UFLiteralDate endDate, String pk_leavetypecopy,
			boolean isSettled, boolean isLeave) throws BusinessException;

	/**
	 * 根據人員及年度取外加補休OTLeaveBalanceVO集合
	 * 
	 * @param pk_org
	 *            組織PK
	 * @param pk_psndocs
	 *            人員PK數組
	 * @param isTermLeave
	 *            是否留停
	 * @param queryYear
	 *            統計年度
	 * @param pk_leavetypecopy
	 *            休假類別
	 * @param isSettled
	 *            是否結算
	 * @return
	 * @throws BusinessException
	 */
	OTLeaveBalanceVO[] getLeaveExtHoursByType(String pk_org, String[] pk_psndocs, String[] pk_depts,
			UFBoolean isTermLeave, String queryYear, String pk_leavetypecopy, boolean isSettled)
			throws BusinessException;

	/**
	 * 根據人員及起迄日期取外加補休OTBalanceDetailVO集合
	 * 
	 * @param pk_org
	 * @param pk_psndoc
	 * @param queryYear
	 * @param beginDate
	 * @param endDate
	 * @param pk_leavetypecopy
	 * @param isSettled
	 * @return
	 * @throws BusinessException
	 */
	OTBalanceDetailVO[] getLeaveExtByType(String pk_org, String pk_psndoc, String queryYear, UFLiteralDate beginDate,
			UFLiteralDate endDate, String pk_leavetypecopy, boolean isSettled) throws BusinessException;

	/**
	 * 按給定人員進行外加補休反結算
	 * 
	 * @param pk_psndoc
	 *            人員
	 * @throws BusinessException
	 */
	void unSettleByPsn(String pk_psndoc) throws BusinessException;

	/**
	 * 按指定組織和日期補充公休遇國假補休
	 * 
	 * @param pk_org
	 * @param checkDate
	 * @throws BusinessException
	 */
	void autoIncreaseExtraLeave(String pk_org, UFLiteralDate checkDate) throws BusinessException;
}
