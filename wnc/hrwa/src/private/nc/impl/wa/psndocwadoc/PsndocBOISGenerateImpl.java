package nc.impl.wa.psndocwadoc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.logging.Logger;
import nc.itf.wa.psndocwadoc.IPsndocBOISGenerateService;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.ms.tb.pub.ListResultSetProcessor;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pub.encryption.util.SalaryEncryptionUtil;
import nc.vo.hi.psndoc.Glbdef1VO;
import nc.vo.hi.psndoc.Glbdef2VO;
import nc.vo.hi.psndoc.PsndocDefVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @since 2019-06-04
 * @author Connie.ZH
 * 
 */

public class PsndocBOISGenerateImpl implements IPsndocBOISGenerateService {

	private final int SPLIT_SIZE = 1000;
	private final String LABOR_TABLE = getLaborTable();// 劳保
	private final String LABOR_EXIT_TABLE = getLaborTable();// 劳退
	private final String HEALTH_TABLE = getHealthTable(); // 健保
	private final List<String> rangeColumnList = Arrays.asList(new String[] { "glbdef4", "glbdef7", "glbdef16" });// 级距字段列表
	private final List<String> salaryColumnList = Arrays.asList(new String[] { "glbdef2", "glbdef2", "glbdef6" });// 薪资字段列表
	private final String insuranceform = "insuranceform";// 投保型態
	private final Integer keepSalaryAdjust = 3;// 保薪調整
	private final String isLabor = "glbdef10";
	private final String isLaborExit = "glbdef11";
	private final String isHealth = "glbdef14";
	private final String pk = "pk_psndoc_sub";
	private final String calFlag = "isnhiitem_30";
	private final String waItemTable = "twhr_waitem_30";
	private final String sql4PsnLabor = " select " + pk + " from " + LABOR_TABLE
			+ " where dr=0 and (enddate is null or enddate = '9999-12-31') and " + isLabor
			+ " = 'Y' and pk_psndoc in #psns#";
	private final String sql4PsnLaborExit = " select " + pk + " from " + LABOR_EXIT_TABLE
			+ " where dr=0 and (glbdef15 is null or glbdef15 = '9999-12-31') and " + isLaborExit
			+ " = 'Y' and pk_psndoc in #psns#";
	// 查询本人投保的人员名单
	private final String sql4PsnHealth_PSNDOC = " select pk_psndoc from " + HEALTH_TABLE
			+ " where glbdef2 = '本人' and dr=0 and (enddate is null or enddate = '9999-12-31') and " + isHealth
			+ " = 'Y' and pk_psndoc in #psns#";
	// 根据人员名单查找主键
	private final String sql4PsnHealth = " select " + pk + " from " + HEALTH_TABLE
			+ " where  dr=0 and (enddate is null or enddate = '9999-12-31') and " + isHealth
			+ " = 'Y' and pk_psndoc in #psns#";

	// 勞健保投保薪资项
	private final String sql4WaItem = " select pk_wa_item from " + waItemTable + " where dr=0 and " + calFlag
			+ " = 'Y'";

	// 查询每个人的金额
	private final String sql4Sum = " select  doc.pk_psndoc pk_psndoc, doc.nmoney nmoney from hi_psndoc_wadoc doc "
			+ " where isnull(dr, 0)=0 " + " and doc.begindate <= '#effectiveDate#' "
			+ " and (doc.enddate is null or doc.enddate >=  '#effectiveDate#' )" + " and doc.pk_psndoc in #psns#"
			+ " and doc.pk_wa_item in #calItemsSql#"; // ssx remarked for salary
														// encrypt +
														// " group by doc.pk_psndoc ";

	private final int LABOR_TYPE = 1; // 劳保
	private final int LABOR_EXIT_TYPE = 2; // 劳退
	private final int HEALTH_TYPE = 3; // 健保

	// 查询级距
	private final String sql4Range = " select range.tabletype,line.rangelower lower_money from twhr_rangetable range inner join twhr_rangeline line on range.pk_rangetable=line.pk_rangetable"
			+ " where"
			+ " (range.startdate <= '"
			+ "#effectiveDate#"
			+ " 00:00:00' and range.enddate >= '"
			+ "#effectiveDate#"
			+ "  00:00:00')"
			+ " and range.dr = 0"
			+ " group by range.tabletype,line.rangelower "
			+ " order by range.tabletype,line.rangelower ";

	private final BaseDAO dao = new BaseDAO();

	/**
	 * 根據所選的人員與生效日期，重新生成勞健保的級距數據 如果要拆开劳保与劳退，需要修改step4&step5的逻辑
	 */
	@SuppressWarnings({ "unchecked", "serial" })
	@Override
	public void generateBOISData(String effectiveDate, String[] rangePsns) throws BusinessException {
		// step0 split person when beyond 500
		if (ArrayUtils.isEmpty(rangePsns) || StringUtils.isEmpty(effectiveDate)) {
			return;
		}
		List<String> calItems = (List<String>) dao.executeQuery(sql4WaItem, new ListResultSetProcessor());
		String calItemsSql = " ('";
		if (!CollectionUtils.isEmpty(calItems)) {
			calItemsSql += StringUtils.join(calItems.toArray(new String[0]), "','");
		}
		calItemsSql += "')";
		int size = rangePsns.length;
		int loopLength = 1 + size / SPLIT_SIZE;
		for (int i = 0; i < loopLength; i++) {
			String psns = "('";
			if (i == loopLength - 1) {
				psns += StringUtils.join(ArrayUtils.subarray(rangePsns, SPLIT_SIZE * i, size), "','");
			} else {
				psns += StringUtils.join(ArrayUtils.subarray(rangePsns, SPLIT_SIZE * i, SPLIT_SIZE * (i + 1)), ",");
			}
			psns = psns + "')";
			List<String> laborList = new ArrayList<String>(); // 劳保人员
			List<String> laborExitList = new ArrayList<String>();// 劳退人员
			List<String> healthList = new ArrayList<String>(); // 健保人员
			List<String> healthPsnList = new ArrayList<String>(); // 健保人员主键
			// step1 篩選人員（根據勞健保的是否投保）
			laborList = (List<String>) dao.executeQuery(sql4PsnLabor.replaceAll("#psns#", psns),
					new ListResultSetProcessor());
			laborExitList = (List<String>) dao.executeQuery(sql4PsnLaborExit.replaceAll("#psns#", psns),
					new ListResultSetProcessor());
			healthPsnList = (List<String>) dao.executeQuery(sql4PsnHealth_PSNDOC.replaceAll("#psns#", psns),
					new ListResultSetProcessor());
			healthList = (List<String>) dao.executeQuery(
					sql4PsnHealth.replaceAll("#psns#",
							"('" + StringUtils.join(healthPsnList.toArray(new String[0]), "','") + "')"),
					new ListResultSetProcessor());
			// step2 查詢人員的投保金額
			HashMap<String, Double> psnMoneyMap = (HashMap<String, Double>) dao.executeQuery(
					sql4Sum.replaceAll("#effectiveDate#", effectiveDate).replaceAll("#calItemsSql#", calItemsSql)
							.replaceAll("#psns#", psns), new ResultSetProcessor() {
						@Override
						public Object handleResultSet(ResultSet rs) throws SQLException {
							HashMap<String, Double> ret = new HashMap<String, Double>();
							while (rs.next()) {
								String pk_psndoc = rs.getString(1);
								if (!ret.containsKey(pk_psndoc)) {
									ret.put(pk_psndoc, 0.0);
								}
								ret.put(pk_psndoc, ret.get(pk_psndoc) + SalaryDecryptUtil.decrypt(rs.getDouble(2)));
							}
							return ret;
						}
					});
			// step3計算人員的投保級別
			HashMap<String, List<Double>> rangeLineMap = (HashMap<String, List<Double>>) dao.executeQuery(
					sql4Range.replaceAll("#effectiveDate#", effectiveDate), new ResultSetProcessor() {
						@Override
						public Object handleResultSet(ResultSet rs) throws SQLException {
							HashMap<String, List<Double>> ret = new HashMap<String, List<Double>>();
							while (rs.next()) {
								if (!ret.containsKey(rs.getString(1))) {
									List<Double> list = new ArrayList<Double>();
									list.add(rs.getDouble(2));
									ret.put(rs.getString(1), list);
								} else {
									ret.get(rs.getString(1)).add(rs.getDouble(2));
								}
							}
							return ret;
						}
					});
			HashMap<String, Double[]> psnRangeMap = new HashMap<String, Double[]>();
			for (String pk_psndoc : psnMoneyMap.keySet()) {
				Double money = psnMoneyMap.get(pk_psndoc);
				psnRangeMap.put(pk_psndoc, calRangeNum(rangeLineMap, money));
			}
			// step4查詢歷史記錄
			List<nc.vo.hi.psndoc.Glbdef1VO> laborHistorys = (List<Glbdef1VO>) dao.retrieveByClause(
					nc.vo.hi.psndoc.Glbdef1VO.class,
					pk + " in ('" + StringUtils.join(laborList.toArray(new String[0]), "','") + "')");
			List<nc.vo.hi.psndoc.Glbdef1VO> laborExitHistorys = (List<Glbdef1VO>) dao.retrieveByClause(
					nc.vo.hi.psndoc.Glbdef1VO.class,
					pk + " in ('" + StringUtils.join(laborExitList.toArray(new String[0]), ",") + "')");
			List<nc.vo.hi.psndoc.Glbdef2VO> healthHistorys = (List<Glbdef2VO>) dao.retrieveByClause(
					nc.vo.hi.psndoc.Glbdef2VO.class,
					pk + " in ('" + StringUtils.join(healthList.toArray(new String[0]), "','") + "')");
			HashMap<Integer, List> map = new HashMap<Integer, List>();
			map.put(LABOR_TYPE, laborHistorys);
			map.put(LABOR_EXIT_TYPE, laborExitHistorys);
			map.put(HEALTH_TYPE, healthHistorys);
			HashMap<Integer, List> modMap = new HashMap<Integer, List>();
			HashMap<Integer, List> insertMap = new HashMap<Integer, List>();
			UFLiteralDate beginDate = new UFLiteralDate(effectiveDate);
			UFLiteralDate lastEndDate = new UFLiteralDate(effectiveDate).getDateBefore(1);
			// step5產生需要新增的記錄及修改歷史記錄
			for (int cnt = 1; cnt < 4; cnt++) {
				List tmpList = map.get(cnt);
				Iterator itr = tmpList.iterator();
				List<nc.vo.hi.psndoc.PsndocDefVO> mod = new ArrayList<nc.vo.hi.psndoc.PsndocDefVO>();
				List<nc.vo.hi.psndoc.PsndocDefVO> insert = new ArrayList<nc.vo.hi.psndoc.PsndocDefVO>();
				while (itr.hasNext()) {
					PsndocDefVO vo = (PsndocDefVO) itr.next();
					for (String pk_psndoc : psnRangeMap.keySet()) {
						if (vo.getPk_psndoc().equals(pk_psndoc)) {
							Double[] psnRangeArr = psnRangeMap.get(pk_psndoc);
							Double money = psnMoneyMap.get(pk_psndoc);
							if (needInsertRecord(vo, psnRangeArr[cnt - 1], cnt)) {
								PsndocDefVO newAddLabor = (PsndocDefVO) vo.clone();
								vo.setEnddate(lastEndDate);
								if (LABOR_EXIT_TYPE == cnt) {
									vo.setAttributeValue("glbdef15", lastEndDate);
								}
								vo.setLastflag(UFBoolean.FALSE);
								mod.add(vo);
								newAddLabor.setBegindate(beginDate);
								if (LABOR_EXIT_TYPE == cnt) {
									newAddLabor.setAttributeValue("glbdef14", beginDate);
									newAddLabor.setAttributeValue("glbdef15", new UFLiteralDate("9999-12-31"));
								} else if (HEALTH_TYPE == cnt) {
									// clear the reasons
									newAddLabor.setAttributeValue("glbdef17", null);
									newAddLabor.setAttributeValue("glbdef18", null);
									newAddLabor.setAttributeValue("glbdef19", null);
								}
								newAddLabor.setRecordnum(0);
								newAddLabor.setLastflag(UFBoolean.TRUE);
								newAddLabor.setEnddate(new UFLiteralDate("9999-12-31"));
								newAddLabor.setAttributeValue(salaryColumnList.get(cnt - 1),
										SalaryEncryptionUtil.encryption(money));
								newAddLabor.setAttributeValue(rangeColumnList.get(cnt - 1), new UFDouble(
										SalaryEncryptionUtil.encryption(psnRangeArr[cnt - 1])));
								newAddLabor.setAttributeValue(insuranceform, keepSalaryAdjust);
								insert.add(newAddLabor);
							}
						}
					}
				}
				modMap.put(cnt, mod);
				insertMap.put(cnt, insert);
			}

			// combine labor and labor exit 合并劳保与劳退
			List insertLabor = insertMap.get(LABOR_TYPE);
			List insertLaborExit = insertMap.get(LABOR_EXIT_TYPE);
			List combineInsertLabor = new ArrayList();
			for (Object laborExit : insertLaborExit) {
				String pk_psndoc = ((PsndocDefVO) laborExit).getPk_psndoc();
				for (Object labor : insertLabor) {
					if (pk_psndoc.equals(((PsndocDefVO) labor).getPk_psndoc())) {
						((PsndocDefVO) laborExit).setAttributeValue(salaryColumnList.get(LABOR_TYPE - 1),
								((PsndocDefVO) labor).getAttributeValue(salaryColumnList.get(LABOR_TYPE - 1)));
						((PsndocDefVO) laborExit).setAttributeValue(rangeColumnList.get(LABOR_TYPE - 1),
								((PsndocDefVO) labor).getAttributeValue(rangeColumnList.get(LABOR_TYPE - 1)));
					}
				}
				combineInsertLabor.add(laborExit);
			}
			for (Object labor : insertLabor) {
				String pk_psndoc = ((PsndocDefVO) labor).getPk_psndoc();
				boolean flag = false;
				for (Object combine : combineInsertLabor) {
					if (pk_psndoc.equals(((PsndocDefVO) combine).getPk_psndoc())) {
						flag = true;
						break;
					}
				}
				if (!flag) {
					combineInsertLabor.add(labor);
				}
			}
			// reset record num
			insertMap.remove(LABOR_EXIT_TYPE);
			insertMap.put(LABOR_EXIT_TYPE, combineInsertLabor);
			// clear pk infos for insert
			for (Integer tmpCnt : insertMap.keySet()) {
				List voLists = insertMap.get(tmpCnt);
				for (Object vo : voLists) {
					((PsndocDefVO) vo).setPk_psndoc_sub(null);
				}
			}
			// step6插入&更新
			for (Integer tmpCnt : modMap.keySet()) {
				List voLists = modMap.get(tmpCnt);
				if (CollectionUtils.isEmpty(voLists)) {
					continue;
				}
				dao.updateVOList(voLists);
			}
			for (Integer tmpCnt : insertMap.keySet()) {
				if (LABOR_TYPE == tmpCnt) {
					// 合并了，只需要插入合并
					continue;
				}
				List voLists = insertMap.get(tmpCnt);
				if (CollectionUtils.isEmpty(voLists)) {
					continue;
				}
				dao.insertVOList(voLists);

				// step7 reset record num
				for (Object vo : voLists) {
					reRangeOrderByPsn((String) ((PsndocDefVO) vo).getAttributeValue("pk_psndoc"), LABOR_TABLE);
					reRangeOrderByPsn((String) ((PsndocDefVO) vo).getAttributeValue("pk_psndoc"), HEALTH_TABLE);
				}
			}
		}

	}

	private static String getHealthTable() {
		String tablename = "";
		try {
			tablename = PsndocDefTableUtil.getPsnHealthTablename();
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}
		return tablename;
	}

	private static String getLaborTable() {
		String tablename = "";
		try {
			tablename = PsndocDefTableUtil.getPsnLaborTablename();
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}
		return tablename;
	}

	@SuppressWarnings("unchecked")
	private void reRangeOrderByPsn(String pk_psndoc, String tableName) throws BusinessException {
		List<String> pk_psndoc_subs = (List<String>) dao.executeQuery("select pk_psndoc_sub from " + tableName
				+ " where pk_psndoc='" + pk_psndoc + "' order by begindate, ts", new ColumnListProcessor());

		if (pk_psndoc_subs != null && pk_psndoc_subs.size() > 0) {
			int size = pk_psndoc_subs.size() - 1;
			for (String pk_psndoc_sub : pk_psndoc_subs) {
				dao.executeUpdate("update " + tableName + " set recordnum=" + size + " where pk_psndoc_sub='"
						+ pk_psndoc_sub + "'");
				size -= 1;
			}
		}
	}

	private boolean needInsertRecord(PsndocDefVO vo, Double rangeMoney, int type) {
		nc.vo.pub.lang.UFDouble oldRange = (nc.vo.pub.lang.UFDouble) vo
				.getAttributeValue(rangeColumnList.get(type - 1));
		if (null == oldRange) {
			return true;
		} else if (oldRange.intValue() != rangeMoney) {
			return true;
		}
		return false;

	}

	private Double[] calRangeNum(HashMap<String, List<Double>> rangeLineMap, Double money) throws BusinessException {
		Double[] calRange = new Double[3];
		for (int i = 0; i < 3; i++) {
			List<Double> tmpList = rangeLineMap.get(String.valueOf(i + 1));
			if (CollectionUtils.isEmpty(tmpList)) {
				throw new BusinessException("Range Line is empty!");
			}
			int size = tmpList.size();
			if (money >= tmpList.get(size - 1)) {
				calRange[i] = tmpList.get(size - 1) - 1;
				continue;
			}
			for (int k = 0; k < size - 1; k++) {
				if (money >= tmpList.get(k) && money <= tmpList.get(k + 1) - 1) {
					calRange[i] = tmpList.get(k + 1) - 1;
					break;
				}
			}
		}
		return calRange;
	}
}
