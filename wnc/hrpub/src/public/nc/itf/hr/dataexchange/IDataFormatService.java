package nc.itf.hr.dataexchange;

import java.util.List;
import java.util.Map;

import nc.vo.pub.BusinessException;

public interface IDataFormatService {
	/**
	 * 根据格式编码取导出格式定义
	 * 
	 * @param formatCode
	 *            格式编码
	 * @return
	 * @throws BusinessException
	 */
	public List<Map> getFormatByCode(String formatCode) throws BusinessException;

	/**
	 * 根据格式化SQL返回数据
	 * 
	 * @param strSQL
	 * @return
	 * @throws BusinessException
	 */
	public List<Map> getDataFormatSQL(String strSQL) throws BusinessException;

	/**
	 * 取薪資方案說明文字數據
	 * 
	 * @param pk_org
	 *            組織PK
	 * @param pk_wa_class
	 *            薪資方案PK
	 * @param cyear
	 *            年度
	 * @param cperiod
	 *            期間
	 * @param itemGroupCode
	 *            薪資項目分組
	 * @param colNumber
	 *            分欄數量
	 * @param colByteLength
	 *            每欄寬度
	 * @return
	 */
	public Map<String, String> getWaItemGroupColumnsByItemGroup(String pk_org, String pk_wa_class, String cyear,
			String cperiod, String itemGroupCode, int colNumber, int colByteLength) throws BusinessException;

	/**
	 * 根據薪資項目分組取薪資方案項目金額
	 * 
	 * @param pk_org
	 *            組織PK
	 * @param pk_wa_class
	 *            薪資方案PK
	 * @param cyear
	 *            年度
	 * @param cperiod
	 *            期間
	 * @param itemGroupCode
	 *            薪資項目分組
	 * @return
	 */
	public Map<String, Map<Integer, Map<String, Object>>> getWaItemByItemGroup(String pk_org, String pk_wa_class,
			String cyear, String cperiod, String itemGroupCode) throws BusinessException;
}
