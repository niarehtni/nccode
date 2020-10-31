package nc.impl.wa.rpt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.InSQLCreator;
import nc.impl.wa.func.CourtDeductParse;
import nc.itf.hr.wa.IDeductDetailService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.hi.psndoc.DeductDetailsVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.func.WherePartUtil;
import nc.vo.wa.pub.WaLoginVO;

public class DeductDetailServiceImpl implements IDeductDetailService {
	private BaseDAO baseDAO = new BaseDAO();

	public BaseDAO getBaseDAO() {
		return baseDAO;
	}

	/**
	 * 回写到扣款明细子集
	 * 
	 * @throws BusinessException
	 */
	@Override
	public void rollbacktodeductdetail(WaLoginVO waLoginVO, String whereCondition, Boolean isRangeAll)
			throws BusinessException {
		String cyear = waLoginVO.getCyear();
		String cperiod = waLoginVO.getCperiod();
		String pk_wa_class = waLoginVO.getPk_wa_class();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_data.pk_psndoc ");
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" where wa_data.checkflag = 'Y' ");
		sqlBuffer.append("   and wa_data.stopflag = 'N' ");
		sqlBuffer.append("   and wa_data.pk_wa_class ='" + pk_wa_class + "' ");
		sqlBuffer.append("   and wa_data.cyear ='" + cyear + "' ");
		sqlBuffer.append("   and wa_data.cperiod ='" + cperiod + "' ");
		if (!isRangeAll) {
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(whereCondition));
		}
		// 所有pk_psndoc
		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		@SuppressWarnings("unchecked")
		List<Map<String, String>> custlist = (List<Map<String, String>>) iUAPQueryBS.executeQuery(sqlBuffer.toString(),
				new MapListProcessor());
		List<String> pk_psndoclist = new ArrayList<String>();
		for (Map<String, String> custmap : custlist) {
			pk_psndoclist.add(custmap.get("pk_psndoc"));
		}
		// tank 这两个方法疯狂报错,没在工作,于是重写 2019年12月4日21:53:43
		// 校验债权档案中比率
		validatedeductfileratio(waLoginVO, pk_psndoclist);
		// 回写子集
		rollback(waLoginVO, pk_psndoclist.toArray(new String[0]));

	}

	private void rollback(WaLoginVO waLoginVO, String[] pk_psndocs) throws BusinessException {
		String cperiod = waLoginVO.getCperiod();
		String cyear = waLoginVO.getCyear();
		String pk_wa_class = waLoginVO.getPk_wa_class();
		// 查詢此薪資方案下所有需要回寫的數據
		String sql = "select * from " + CourtDeductParse.TEMP_DEDUCT_TABLE_NAME + " tb " + " where tb.waclass = '"
				+ pk_wa_class + "' and tb.salaryyearmonth = '" + cyear + "" + cperiod + "'";
		@SuppressWarnings("unchecked")
		List<DeductDetailsVO> ddvList = (List<DeductDetailsVO>) getBaseDAO().executeQuery(sql,
				new BeanListProcessor(DeductDetailsVO.class));
		if (ddvList != null && ddvList.size() > 0) {

			for (DeductDetailsVO vo : ddvList) {
				vo.setPk_psndoc_sub(null);
				vo.setStatus(VOStatus.NEW);
			}
			// 持久化到数据库
			getBaseDAO().insertVOArray(ddvList.toArray(new DeductDetailsVO[0]));

			// 更新rownum
			sql = " MERGE INTO hi_psndoc_deductdetails a "
					+ " USING ( SELECT pk_psndoc_sub, ROW_NUMBER() OVER (partition BY pk_psndoc ORDER BY ts DESC)  RN "
					+ " FROM hi_psndoc_deductdetails) b ON ( " + " a.pk_psndoc_sub = b.pk_psndoc_sub) WHEN MATCHED "
					+ " THEN UPDATE SET a.recordnum = b.RN ";
			getBaseDAO().executeUpdate(sql);
			// 更新债券档案
			updateDebtfile(ddvList, false);
		}
	}

	/**
	 * 更新债券档案-此方法需要在扣款明细子集更新后执行
	 * 
	 * @param ddvList
	 *            变动的扣款明细vo
	 * @throws BusinessException
	 */
	@Override
	public void updateDebtfile(List<DeductDetailsVO> ddvList, boolean isUpdatenull) throws BusinessException {

		if (ddvList != null && ddvList.size() > 0) {
			// 提取扣款明细信息 key pk_psndoc::fileNumber::creditor 用于找出所有相关联的债券档案
			Set<String> debInfoSet = new HashSet<String>();
			// 人员::档案编号 pk_psndoc::fileNumber
			Set<String> fileNumberSet = new HashSet<String>();
			// 人员
			Set<String> pk_psndocSet = new HashSet<String>();
			for (DeductDetailsVO vo : ddvList) {
				String pk_psndoc = vo.getPk_psndoc();
				String fileNumber = vo.getAttributeValue("filenumber") == null ? null : String.valueOf(vo
						.getAttributeValue("filenumber"));
				String dcreditor = vo.getAttributeValue("dcreditor") == null ? null : String.valueOf(vo
						.getAttributeValue("dcreditor"));
				if (pk_psndoc != null && fileNumber != null && dcreditor != null) {
					debInfoSet.add(pk_psndoc + "::" + fileNumber + "::" + dcreditor);
					fileNumberSet.add(pk_psndoc + "::" + fileNumber);
					pk_psndocSet.add(pk_psndoc);
				}
			}
			InSQLCreator insql = new InSQLCreator();
			String psnFileCreditorInsql = insql.getInSQL(debInfoSet.toArray(new String[0]));
			if (debInfoSet.size() > 0) {
				// 找出所有相关的债券档案
				@SuppressWarnings("unchecked")
				Set<String> pkDebtfileSet = (Set<String>) getBaseDAO().executeQuery(
						"select pk_psndoc_sub from hi_psndoc_debtfile debfile where debfile.dr = 0 "
								+ " and (debfile.pk_psndoc||'::'||debfile.dfilenumber||'::'||creditor) in ("
								+ psnFileCreditorInsql + ")", new ResultSetProcessor() {
							private static final long serialVersionUID = 7469495167226795395L;
							private Set<String> rsSet = new HashSet<>();

							@Override
							public Object handleResultSet(ResultSet rs) throws SQLException {
								while (rs.next()) {
									if (rs.getString(1) != null) {
										rsSet.add(rs.getString(1));
									}
								}
								return rsSet;
							}
						});
				// 是否更新没有扣款记录的债券档案
				if (isUpdatenull) {
					String sql = "select debfile.pk_psndoc_sub,debfile.pk_psndoc,debfile.dfilenumber from hi_psndoc_debtfile debfile "
							+ " left join hi_psndoc_deductdetails details on "
							+ " (debfile.pk_psndoc = details.pk_psndoc and debfile.dfilenumber = details.filenumber "
							+ " and debfile.creditor = details.dcreditor) "
							+ " where details.pk_psndoc_sub is null and debfile.pk_psndoc in ("
							+ (new InSQLCreator()).getInSQL(pk_psndocSet.toArray(new String[0])) + ")";
					@SuppressWarnings("unchecked")
					Map<String, String> newFileMap = (Map<String, String>) getBaseDAO().executeQuery(sql,
							new ResultSetProcessor() {
								private static final long serialVersionUID = 5402157412328745715L;
								private Map<String, String> newFileMap = new HashMap<>();

								@Override
								public Object handleResultSet(ResultSet rs) throws SQLException {
									while (rs.next()) {
										if (rs.getString(1) != null) {
											newFileMap.put(rs.getString(1), rs.getString(2) + "::" + rs.getString(3));
										}
									}
									return newFileMap;
								}
							});
					if (newFileMap != null && newFileMap.size() > 0) {
						pkDebtfileSet.addAll(newFileMap.keySet());
						fileNumberSet.addAll(newFileMap.values());
					}
				}
				String pkDebtfileInsql = insql.getInSQL(pkDebtfileSet.toArray(new String[0]));

				String psnFileInsql = insql.getInSQL(fileNumberSet.toArray(new String[0]));
				// 更新之
				String updateRestSql = "MERGE INTO hi_psndoc_debtfile a "
						+ " USING ( SELECT debfile.pk_psndoc_sub pk_psndoc_sub, "
						+ " (nvl(debfile.creditamount,0)-nvl(sumdeb.sumde,0)) restmoneycal "
						+ " FROM hi_psndoc_debtfile debfile LEFT JOIN "
						+ " ( SELECT pk_psndoc, filenumber, dcreditor, SUM(deductmoney) sumde "
						+ " FROM hi_psndoc_deductdetails WHERE dr = 0 "
						+ " GROUP BY pk_psndoc, filenumber, dcreditor ) sumdeb "
						+ " ON ( debfile.pk_psndoc = sumdeb.pk_psndoc AND debfile.dfilenumber = sumdeb.filenumber "
						+ " AND debfile.creditor = sumdeb.dcreditor )  WHERE " + " debfile.pk_psndoc_sub IN ("
						+ pkDebtfileInsql + ") ) b " + " ON (a.pk_psndoc_sub = b.pk_psndoc_sub) WHEN MATCHED "
						+ " THEN UPDATE SET a.restmoney = b.restmoneycal";
				getBaseDAO().executeUpdate(updateRestSql);

				// 如果债券档案的剩余金额已经小于等于0,那么就结束
				String stopFlagUpdateSql = "update hi_psndoc_debtfile set stopflag = 'Y' where "
						+ " dr = 0 and isnull(restmoney,0) <= 0 and pk_psndoc_sub in (" + pkDebtfileInsql + ")";
				getBaseDAO().executeUpdate(stopFlagUpdateSql);
				// 如果债券档案的剩余金额已经大于0,那么不结束
				stopFlagUpdateSql = "update hi_psndoc_debtfile set stopflag = 'N' where "
						+ " dr = 0 and isnull(restmoney,0) > 0 and pk_psndoc_sub in (" + pkDebtfileInsql + ") ";
				getBaseDAO().executeUpdate(stopFlagUpdateSql);

				// 如果法扣已经没有未结束的债券档案,那么就结束
				String updateIsStopFlagSql = "update hi_psndoc_courtdeduction set isstop = 'Y' "
						+ " where (pk_psndoc||'::'||filenumber) in (select pk_psndoc||'::'||dfilenumber from ( "
						+ " select pk_psndoc,dfilenumber,count(*) countn from hi_psndoc_debtfile debfile "
						+ " where  (debfile.pk_psndoc||'::'||debfile.dfilenumber) in (" + psnFileInsql + ") "
						+ " and dr = 0 and isnull(debfile.stopflag,'N') = 'N' "
						+ " group by pk_psndoc,dfilenumber ) tb where countn = 0 ) ";
				getBaseDAO().executeUpdate(updateIsStopFlagSql);
				// 如果法扣还有未结束的债券档案,那么不结束
				updateIsStopFlagSql = "update hi_psndoc_courtdeduction set isstop = 'N' "
						+ " where (pk_psndoc||'::'||filenumber) in (select pk_psndoc||'::'||dfilenumber from ( "
						+ " select pk_psndoc,dfilenumber,count(*) countn from hi_psndoc_debtfile debfile "
						+ " where  (debfile.pk_psndoc||'::'||debfile.dfilenumber) in (" + psnFileInsql + ") "
						+ " and dr = 0 and isnull(debfile.stopflag,'N') = 'N' "
						+ " group by pk_psndoc,dfilenumber ) tb where countn > 0 ) ";
				getBaseDAO().executeUpdate(updateIsStopFlagSql);

			}

		}

	}

	private void validatedeductfileratio(WaLoginVO waLoginVO, List<String> pk_psndoclist) throws BusinessException {
		// 校验1.债券档案的方法比率要大于1
		// 在债权档案里面查询出这些人的信息
		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		InSQLCreator insql = new InSQLCreator();

		String psndocsInSQL = insql.getInSQL(pk_psndoclist.toArray(new String[0]));
		@SuppressWarnings("unchecked")
		List<Map<String, String>> delist = (List<Map<String, String>>) iUAPQueryBS
				.executeQuery(
						"select court.filenumber filenumber, court.pk_psndoc, SUM(isnull(dfile.repaymentratio,0)) sumratio from hi_psndoc_courtdeduction court "
								+ " left join hi_psndoc_debtfile dfile on (dfile.dfilenumber = court.filenumber and court.pk_psndoc = dfile.pk_psndoc) "
								+ " where court.pk_psndoc in ("
								+ psndocsInSQL
								+ ") and court.dr = 0 and isnull(court.isstop,'N') = 'N'and isnull(dfile.stopflag,'N') = 'N' "
								+ " GROUP BY court.filenumber,court.pk_psndoc", new MapListProcessor());
		List<String> errmaps = new ArrayList<String>();

		for (Map<String, String> demap : delist) {
			UFDouble sumratio = new UFDouble(String.valueOf(demap.get("sumratio")));
			if (Math.abs(sumratio.sub(1.0).doubleValue()) > 0.000001) {
				String message = "請先進行員工["
						+ (String) iUAPQueryBS.executeQuery(
								"select code from bd_psndoc where pk_psndoc = '" + demap.get("pk_psndoc") + "' ",
								new ColumnProcessor()) + "],檔案號[" + demap.get("filenumber") + "] 的法院強制扣款債權比率的調整。";
				errmaps.add(message);
			}
		}

		if (errmaps.size() > 0) {
			throw new BusinessException(errmaps.toString());
		}

	}

	@Override
	public void cancelDeductdetail(String pk_wa_class, String waPeriod) throws BusinessException {
		// 先获取到需要删除的VO
		String sql = "select * from hi_psndoc_deductdetails" + " where waclass = '" + pk_wa_class
				+ "' and salaryyearmonth = '" + waPeriod + "' and dr = 0";
		@SuppressWarnings("unchecked")
		List<DeductDetailsVO> ddvList = (List<DeductDetailsVO>) getBaseDAO().executeQuery(sql,
				new BeanListProcessor(DeductDetailsVO.class));
		// 删除VO
		sql = "delete from hi_psndoc_deductdetails" + " where waclass = '" + pk_wa_class + "' and salaryyearmonth = '"
				+ waPeriod + "'";
		getBaseDAO().executeUpdate(sql);
		// 更新flag
		updateDebtfile(ddvList, false);

	}
}
