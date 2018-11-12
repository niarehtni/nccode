package nc.itf.wa.datainterface;

import nc.vo.pub.BusinessException;

public interface IReportExportService {

	/**
	 * 生成申報格式數據
	 * 
	 * @param dataPKs
	 *            申報記錄PK
	 * @param iYear
	 *            申報年份（公曆）
	 * @param applyFormat
	 *            申報類型
	 * @param applyCount
	 *            重複申報次數
	 * @param applyReason
	 *            重複申報原因
	 * @param vatNumber
	 *            統一編號
	 * @param grantType
	 *            申報填發類型
	 * @param comLinkMan
	 *            聯絡人姓名
	 * @param comLinkTel
	 *            聯絡人電話
	 * @param comLinkEmail
	 *            申報單位電子郵箱
	 * @return  by hepingyang 
	 * @throws BusinessException
	 */
	public String[] getIITXTextReport(String[] dataPKs, int iYear, String[] applyFormat, String applyCount,
			String applyReason, String vatNumber, String grantType, String comLinkMan, String comLinkTel,
			String comLinkEmail) throws BusinessException;
	/**
	 * 生成申報格式數據
	 * 
	 * @param dataPKs
	 *            申報記錄PK
	 * @param iYear
	 *            申報年份（公曆）
	 * @param applyFormat
	 *            申報類型
	 * @param applyCount
	 *            重複申報次數
	 * @param applyReason
	 *            重複申報原因
	 * @param vatNumber
	 *            統一編號
	 * @param grantType
	 *            申報填發類型
	 * @param comLinkMan
	 *            聯絡人姓名
	 * @param comLinkTel
	 *            聯絡人電話
	 * @param comLinkEmail
	 *            申報單位電子郵箱
	 * @return
	 * @throws BusinessException
	 */
	public String[] getIITXTextReport(String[] dataPKs, int iYear, String applyFormat, String applyCount,
			String applyReason, String vatNumber, String grantType, String comLinkMan, String comLinkTel,
			String comLinkEmail) throws BusinessException;

	/**
	 * 獲取滙豐銀行（臺灣）報盤數據
	 * 
	 * @param pk_org
	 *            組織
	 * @param offerPeriod
	 *            報盤期間
	 * @param pk_wa_class
	 *            薪資方案
	 * @return
	 * @throws BusinessException
	 */
	public String[] getBankReportText(String pk_org, String offerPeriod, String pk_wa_class) throws BusinessException;

	/**
	 * 取公司統一編號
	 * 
	 * @param pk_org
	 *            組織
	 * @return
	 * @throws BusinessException
	 */
	public String getOrgVATNumber(String pk_org) throws BusinessException;

	/**
	 * 取所有組織統一編號列表
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public String[] getAllOrgVATNumber() throws BusinessException;

	/**
	 * 通過統一編號取所有組織
	 * 
	 * @param strVATNumber
	 *            統一編號
	 * @return
	 * @throws BusinessException
	 */
	public String[] getAllOrgByVATNumber(String strVATNumber) throws BusinessException;

	/**
	 * 檢查指定期間及薪資方案是否存在薪資發放數據
	 * 
	 * @param pk_org
	 *            組織
	 * @param wa_classes
	 *            薪資方案集
	 * @param startPeriod
	 *            起始期間
	 * @param endPeriod
	 *            結束期間
	 * @return 0-無任何資料，1-有完整資料
	 * @throws BusinessException
	 *             部份資料缺失
	 */
	public int checkPeriodWaDataExists(String pk_org, String[] wa_classes, String startPeriod, String endPeriod)
			throws BusinessException;
	
	/**
	 * 檢查指定組織集是否指定同一申報組織
	 * 
	 * @param pk_orgs
	 *            組織集
	 * @throws BusinessException
	 */
	public void checkExportOrg(String[] pk_orgs) throws BusinessException;

	/**
	 * 取申報信息
	 * 
	 * @param vatNumber
	 *            統一編號
	 * @param declareType
	 *            申報類型
	 * @param grantType
	 *            填發類型
	 * @param declareReason
	 *            重複申報原因
	 * @return [統一編號，申報類型Code，填發類型Code，重複申報原因Code]
	 * @throws BusinessException
	 */
	public String[] getIITXTInfo(String vatNumber, String grantType, String declareReason)
			throws BusinessException;
	
	/**
	 * 回寫申報明細檔
	 * 
	 * @param pks
	 *            回寫pks
	 * @throws BusinessException
	 */
	public abstract void writeBackFlags(String[] dataPKs) throws BusinessException;
	/**
	 * 回寫申報明細檔
	 * 
	 * @param declareReason
	 *            回寫pks
	 * @throws BusinessException
	 */
	public String[] getDeclaretype(String[] declaretypes) throws BusinessException;
}
