package nc.pubitf.ta.overtime;

import java.util.Map;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.AggSegRuleVO;

public interface ISegRuleService {
	/**
	 * 根據日期、員工取加班分段費率（批量）
	 * 
	 * 業務邏輯：根據給定的日期範圍及員工範圍，查詢所有引用了加班分段依據的加班登記單，返回人員、日期對應的加班分段依據Map
	 * 
	 * @param otDates
	 *            加班日期數組
	 * @param pk_psndocs
	 *            加班員工PK數組
	 * @return Map.Key=員工PK，Map.Value=(Map.Key=加班日期, Map.Value=加班分段依據VO)
	 * @throws BusinessException
	 */
	public Map<String, Map<UFLiteralDate, AggSegRuleVO>> querySegRulesByPsn(
			UFLiteralDate[] otDates, String[] pk_psndocs)
			throws BusinessException;

	/**
	 * 根據考勤期間、員工取加班分段費率（批量）
	 * 
	 * 業務邏輯：根據給定的考勤年及期間，取出該期間下所有日期，並同人員PK數組調用方法1，並返回方法1結果
	 * 
	 * @param pk_org
	 *            組織
	 * @param cyear
	 *            考勤年份
	 * @param cperiod
	 *            考勤期間編號
	 * @param pk_psndocs
	 *            加班員工PK數組
	 * @return Map.Key=員工PK，Map.Value=(Map.Key=加班日期, Map.Value=加班分段依據VO)
	 * @throws BusinessException
	 */
	public Map<String, Map<UFLiteralDate, AggSegRuleVO>> querySegRulesByPsn(
			String pk_org, String cyear, String cperiod, String[] pk_psndocs)
			throws BusinessException;

	/**
	 * 根據起止日期、員工取加班分段費率（批量）
	 * 
	 * 業務邏輯：根據給定的起止日期，並同人員PK數組調用方法1，並返回方法1結果
	 * 
	 * @param startDate
	 *            開始日期
	 * @param endDate
	 *            結束日期
	 * @param pk_psndocs
	 *            加班員工PK數組
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, Map<UFLiteralDate, AggSegRuleVO>> querySegRulesByPsn(
			UFLiteralDate startDate, UFLiteralDate endDate, String[] pk_psndocs)
			throws BusinessException;

}
