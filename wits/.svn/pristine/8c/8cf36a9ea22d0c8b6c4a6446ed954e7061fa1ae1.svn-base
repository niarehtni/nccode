package nc.impl.twhr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.ml.NCLangResOnserver;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.impl.pub.ace.AceNhicalcPubServiceImpl;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.itf.hi.PsndocDefUtil;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.hi.psndoc.PsndocDefVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.twhr.nhicalc.NhiCalcVO;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;

public class NhicalcMaintainImpl extends AceNhicalcPubServiceImpl implements
		nc.itf.twhr.INhicalcMaintain {
	private BaseDAO baseDao;

	private String pk_org;
	private String calcYear;
	private String calcMonth;
	private List<NhiCalcVO> nhiDataList;

	@Override
	public NhiCalcVO[] query(IQueryScheme queryScheme) throws BusinessException {
		return super.pubquerybasedoc(queryScheme);
	}

	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO)
			throws BusinessException {
		BatchSaveAction<NhiCalcVO> saveAction = new BatchSaveAction<NhiCalcVO>();
		BatchOperateVO retData = saveAction.batchSave(batchVO);
		return retData;
	}

	@Override
	public void audit(String pk_org, String cyear, String cperiod)
			throws BusinessException {
		if (isAudit(pk_org, cyear, cperiod)) {
			throw new BusinessException(NCLangResOnserver.getInstance()
					.getStrByID("68861705", "NhicalcMaintainImpl-0001")/*
																		 * 选定期间已经审核。
																		 */);
		}

		this.setPk_org(pk_org);
		this.setCalcYear(cyear);
		this.setCalcMonth(cperiod);

		// 加載勞健保計算結果
		loadNhiCalcResults();

		// 更新員工檔案勞健保自定義表
		updatePsnDefs();

		String strSQL = "UPDATE twhr_nhicalc SET isaudit='Y' WHERE pk_org='"
				+ pk_org + "' AND cyear='" + cyear + "' AND cperiod='"
				+ cperiod + "' AND dr=0";
		this.getBaseDao().executeUpdate(strSQL);
	}

	private void loadNhiCalcResults() throws BusinessException {
		String strSQL = " (pk_org = '" + this.getPk_org() + "') AND (cyear = '"
				+ this.getCalcYear() + "') AND (cperiod = '"
				+ this.getCalcMonth() + "') AND (dr = 0) AND isaudit='N'";

		this.setNhiDataList((List<NhiCalcVO>) this.getBaseDao()
				.retrieveByClause(NhiCalcVO.class, strSQL));
	}

	private void updatePsnDefs() throws BusinessException {
		// 更新員工檔案：勞健保計算明細表
		SaveNHIDetail();
		// 更新員工檔案：勞健保計算彙總表
		SaveNHITotal();
	}

	// 保存明細表
	private void SaveNHIDetail() throws BusinessException {
		SimpleDocServiceTemplate service = new SimpleDocServiceTemplate(
				"TWHRGLBDEF");
		List<PsndocDefVO> nhiTotalVOs = getNHIDetailSaveVOs();
		if (nhiTotalVOs != null && nhiTotalVOs.size() > 0) {
			for (PsndocDefVO vo : nhiTotalVOs) {
				service.insert(vo);
			}
		}
	}

	/**
	 * @return
	 * @throws BusinessException
	 */
	private List<PsndocDefVO> getNHIDetailSaveVOs() throws BusinessException {
		List<PsndocDefVO> psnLaborInfoVOs = new ArrayList<PsndocDefVO>();
		if (this.getNhiDataList() != null && this.getNhiDataList().size() > 0) {
			for (NhiCalcVO vo : this.getNhiDataList()) {
				PsndocDefVO newVO = PsndocDefUtil.getPsnNHIDetailVO();
				newVO.setBegindate(UFLiteralDate.getDate(vo.getBegindate()
						.toString()));
				newVO.setEnddate(UFLiteralDate.getDate(vo.getEnddate()
						.toString()));
				newVO.setPk_psndoc(vo.getPk_psndoc());
				newVO.setDr(0);

				// glbdef1,身心殘障程度
				newVO.setAttributeValue("glbdef1", vo.getDisablegrade());
				// glbdef2,勞保身份注記
				newVO.setAttributeValue("glbdef2", vo.getLabortype());
				// glbdef3,勞保級距
				newVO.setAttributeValue("glbdef3", vo.getOldlaborrange());
				// glbdef4,普通事故保險費率
				newVO.setAttributeValue("glbdef4", vo.getComrate());
				// glbdef5,普通事故保險費承擔比例(個人)
				newVO.setAttributeValue("glbdef5", vo.getComstuffrate());
				// glbdef6,普通事故保險費承擔金額(個人)
				newVO.setAttributeValue("glbdef6", vo.getComstuff());
				// glbdef7,普通事故保險費承擔比例(僱主)
				newVO.setAttributeValue("glbdef7", vo.getComhirerrate());
				// glbdef8,普通事故保險費承擔金額(僱主)
				newVO.setAttributeValue("glbdef8", vo.getComhirer());
				// glbdef9,職業災害保險費率
				newVO.setAttributeValue("glbdef9", vo.getDisrate());
				// glbdef10,職業災害保險費承擔比例(個人)
				newVO.setAttributeValue("glbdef10", vo.getDisstuffrate());
				// glbdef11,職業災害保險費承擔金額(個人)
				newVO.setAttributeValue("glbdef11", vo.getDisstuff());
				// glbdef12,職業災害保險費承擔比例(僱主)
				newVO.setAttributeValue("glbdef12", vo.getDishirerrate());
				// glbdef13,職業災害保險費承擔金額(僱主)
				newVO.setAttributeValue("glbdef13", vo.getDishirer());
				// glbdef14,就業保險費率
				newVO.setAttributeValue("glbdef14", vo.getEmprate());
				// glbdef15,就業保險費承擔比例(個人)
				newVO.setAttributeValue("glbdef15", vo.getEmpstuffrate());
				// glbdef16,就業保險費承擔金額(個人)
				newVO.setAttributeValue("glbdef16", vo.getEmpstuff());
				// glbdef17,就業保險費承擔比例(僱主)
				newVO.setAttributeValue("glbdef17", vo.getEmphirerrate());
				// glbdef18,就業保險費承擔金額(僱主)
				newVO.setAttributeValue("glbdef18", vo.getEmphirer());
				// glbdef19,勞保承擔金額(個人)
				newVO.setAttributeValue("glbdef19", vo.getLaborstuff());
				// glbdef20,勞保承擔金額(僱主)
				newVO.setAttributeValue("glbdef20", vo.getLaborhirer());
				// glbdef21,勞退級距
				newVO.setAttributeValue("glbdef21", vo.getOldretirerange());
				// glbdef22,勞退金提繳比例(個人)
				newVO.setAttributeValue("glbdef22", vo.getRetirestuffrate());
				// glbdef23,勞退金提繳金額(個人)
				newVO.setAttributeValue("glbdef23", vo.getRetirestuff());
				// glbdef24,勞退金提繳比例(僱主)
				newVO.setAttributeValue("glbdef24", vo.getRetirehirerrate());
				// glbdef25,勞退金提繳金額(僱主)
				newVO.setAttributeValue("glbdef25", vo.getRetirehirer());
				// glbdef26,勞保有效天數
				newVO.setAttributeValue("glbdef26", vo.getLabordays());
				// glbdef27,上月普通事故保險費承擔金額(個人)
				newVO.setAttributeValue("glbdef27", vo.getLastmonthcomstuff());
				// glbdef28,上月普通事故保險費承擔金額(僱主)
				newVO.setAttributeValue("glbdef28", vo.getLastmonthcomhirer());
				// glbdef29,上月職業災害保險費承擔金額(個人)
				newVO.setAttributeValue("glbdef29", vo.getLastmonthdisstuff());
				// glbdef30,上月職業災害保險費承擔金額(僱主)
				newVO.setAttributeValue("glbdef30", vo.getLastmonthdishirer());
				// glbdef31,上月就業保險費承擔金額(個人)
				newVO.setAttributeValue("glbdef31", vo.getLastmonthempstuff());
				// glbdef32,上月就業保險費承擔金額(僱主)
				newVO.setAttributeValue("glbdef32", vo.getLastmonthemphirer());
				// glbdef33,上月勞保承擔金額(個人)
				newVO.setAttributeValue("glbdef33",
						vo.getLastmonthllaborstuff());
				// glbdef34,上月勞保承擔金額(僱主)
				newVO.setAttributeValue("glbdef34", vo.getLastmonthlaborhirer());
				// glbdef35,上月勞退金提繳金額(個人)
				newVO.setAttributeValue("glbdef35",
						vo.getLastmonthretirestuff());
				// glbdef36,上月勞退金提繳金額(僱主)
				newVO.setAttributeValue("glbdef36",
						vo.getLastmonthretirehirer());
				// glbdef37,上月勞保有效天數
				newVO.setAttributeValue("glbdef37", vo.getLastmonthlabordays());
				// glbdef38,是否包含上月
				newVO.setAttributeValue("glbdef38", vo.getIncludelastmonth());
				// glbdef39,基本薪資
				UFDouble baseSalary = vo.getLaborsalary();
				if (baseSalary == null || baseSalary.equals(UFDouble.ZERO_DBL)) {
					baseSalary = vo.getHealthsalary();
				}
				newVO.setAttributeValue("glbdef39", baseSalary);
				// glbdef40,年度
				newVO.setAttributeValue("glbdef40", vo.getCyear());
				// glbdef41,月份
				newVO.setAttributeValue("glbdef41", vo.getCperiod());
				// glbdef42,人力資源組織
				newVO.setAttributeValue("glbdef42", vo.getPk_org());
				// glbdef43 勞退有效天數
				newVO.setAttributeValue("glbdef43", vo.getRetiredays());
				// glbdef44,墊償薪資基金金額
				newVO.setAttributeValue("glbdef44", vo.getRepayfund());
				// glbdef45,上月墊償薪資基金金額
				newVO.setAttributeValue("glbdef45", vo.getLastmonthrepayfund());
				psnLaborInfoVOs.add(newVO);
			}
		}

		return psnLaborInfoVOs;
	}

	// 保存彙總表
	private void SaveNHITotal() throws BusinessException {
		SimpleDocServiceTemplate service = new SimpleDocServiceTemplate(
				"TWHRGLBDEF");
		List<PsndocDefVO> nhiTotalVOs = getNHITotalSaveVOs();
		if (nhiTotalVOs != null && nhiTotalVOs.size() > 0) {
			for (PsndocDefVO vo : nhiTotalVOs) {
				PsndocDefVO[] existsVOs = service.queryByCondition(
						vo.getClass(), " pk_psndoc='" + vo.getPk_psndoc()
								+ "' and dr=0 ");
				if (existsVOs != null && existsVOs.length > 0) {
					// 更新已存在資料
					for (PsndocDefVO exvo : existsVOs) {
						exvo.setRecordnum(exvo.getRecordnum() + 1);
						exvo.setLastflag(UFBoolean.FALSE);
						service.update(exvo, true);
					}
				}
				service.insert(vo);
			}
		}
	}

	private List<PsndocDefVO> getNHITotalSaveVOs() throws BusinessException {
		List<PsndocDefVO> psnLaborInfoVOs = new ArrayList<PsndocDefVO>();
		if (this.getNhiDataList() != null && this.getNhiDataList().size() > 0) {
			for (NhiCalcVO vo : this.getNhiDataList()) {
				PsndocDefVO newVO = PsndocDefUtil.getPsnNHISumVO();
				newVO.setBegindate(UFLiteralDate.getDate(vo.getBegindate()
						.toString()));
				newVO.setEnddate(UFLiteralDate.getDate(vo.getEnddate()
						.toString()));
				newVO.setPk_psndoc(vo.getPk_psndoc());
				newVO.setDr(0);
				newVO.setLastflag(UFBoolean.TRUE);
				newVO.setRecordnum(0);

				// glbdef1,投保年份
				newVO.setAttributeValue("glbdef1", vo.getCyear());
				// glbdef2,投保月份
				newVO.setAttributeValue("glbdef2", vo.getCperiod());
				// glbdef3,基本薪资
				UFDouble baseSalary = vo.getLaborsalary();
				if (baseSalary == null || baseSalary.equals(UFDouble.ZERO_DBL)) {
					baseSalary = vo.getHealthsalary();
				}
				newVO.setAttributeValue("glbdef3", baseSalary);
				// glbdef4,劳保级距
				newVO.setAttributeValue("glbdef4", vo.getOldlaborrange());
				// glbdef5,劳保投保天数
				newVO.setAttributeValue(
						"glbdef5",
						(vo.getLabordays() == null ? 0 : vo.getLabordays())
								+ (vo.getLastmonthlabordays() == null ? 0 : vo
										.getLastmonthlabordays()));
				// glbdef6,普通事故保险费承担金额(个人)
				newVO.setAttributeValue(
						"glbdef6",
						vo.getComstuff()
								.add(vo.getLastmonthcomstuff() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthcomstuff()));
				// glbdef7,普通事故保险费承担金额(雇主)
				newVO.setAttributeValue(
						"glbdef7",
						vo.getComhirer()
								.add(vo.getLastmonthcomhirer() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthcomhirer()));
				// glbdef8,职业灾害保险费承担金额(个人)
				newVO.setAttributeValue(
						"glbdef8",
						vo.getDisstuff()
								.add(vo.getLastmonthdisstuff() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthdisstuff()));
				// glbdef9,职业灾害保险费承担金额(雇主)
				newVO.setAttributeValue(
						"glbdef9",
						vo.getDishirer()
								.add(vo.getLastmonthdishirer() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthdishirer()));
				// glbdef10,就业保险费承担金额(个人)
				newVO.setAttributeValue(
						"glbdef10",
						vo.getEmpstuff()
								.add(vo.getLastmonthempstuff() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthempstuff()));
				// glbdef11,就业保险费承担金额(雇主)
				newVO.setAttributeValue(
						"glbdef11",
						vo.getEmphirer()
								.add(vo.getLastmonthemphirer() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthemphirer()));
				// glbdef12,劳保承担金额(个人)
				newVO.setAttributeValue(
						"glbdef12",
						vo.getLaborstuff()
								.add(vo.getLastmonthllaborstuff() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthllaborstuff()));
				// glbdef13,劳保承担金额(雇主)
				newVO.setAttributeValue(
						"glbdef13",
						vo.getLaborhirer()
								.add(vo.getLastmonthlaborhirer() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthlaborhirer()));
				// glbdef14,劳退级距
				newVO.setAttributeValue("glbdef14", vo.getOldretirerange());
				// glbdef15,劳退金提缴金额(个人)
				newVO.setAttributeValue(
						"glbdef15",
						vo.getRetirestuff()
								.add(vo.getLastmonthretirestuff() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthretirestuff()));
				// glbdef16,劳退金提缴金额(雇主)
				newVO.setAttributeValue(
						"glbdef16",
						vo.getRetirehirer()
								.add(vo.getLastmonthretirehirer() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthretirehirer()));
				if (vo.getHealthstuff() != null && vo.getHealthhirer() != null) {
					// glbdef17,健保级距
					newVO.setAttributeValue("glbdef17", vo.getOldhealthrange());
					// glbdef18,眷属人数(含本人)
					newVO.setAttributeValue("glbdef18", vo.getDependentcount());
					// glbdef19,健保费承担金额(个人)
					newVO.setAttributeValue(
							"glbdef19",
							vo.getHealthstuff()
									.add(vo.getLastmonthhealthstuff() == null ? UFDouble.ZERO_DBL
											: vo.getLastmonthhealthstuff()));
					// glbdef20,健保费承担金额(雇主)
					newVO.setAttributeValue(
							"glbdef20",
							vo.getHealthhirer()
									.add(vo.getLastmonthhealthhirer() == null ? UFDouble.ZERO_DBL
											: vo.getLastmonthhealthhirer()));
					// glbdef21,政府补助健保费
					newVO.setAttributeValue(
							"glbdef21",
							vo.getHealthgov()
									.add(vo.getLastmonthhealthgov() == null ? UFDouble.ZERO_DBL
											: vo.getLastmonthhealthgov()));
					// glbdef22,健保费应缴金额
					newVO.setAttributeValue(
							"glbdef22",
							vo.getHealthstuffact()
									.add(vo.getLastmonthhealthstuffact() == null ? UFDouble.ZERO_DBL
											: vo.getLastmonthhealthstuffact()));
					// glbdef24,是否包含上月健保
					newVO.setAttributeValue("glbdef24",
							vo.getIncludelastmonth());
				}
				// glbdef23,是否包含上月劳保
				newVO.setAttributeValue("glbdef23", vo.getIncludelastmonth());
				// glbdef25,劳退投保天数
				newVO.setAttributeValue(
						"glbdef25",
						(vo.getRetiredays() == null ? 0 : vo.getRetiredays())
								+ (vo.getLastmonthretiredays() == null ? 0 : vo
										.getLastmonthretiredays()));
				// glbdef26,积欠薪资垫偿基金金额
				newVO.setAttributeValue("glbdef26", vo.getRepayfund());

				psnLaborInfoVOs.add(newVO);
			}
		}

		return psnLaborInfoVOs;
	}

	private String getPsndocWherePart() {
		String strWherePart = " where  def.pk_psndoc in ( select  distinct pk_psndoc ";
		strWherePart += "                        from    twhr_nhicalc where dr=0 and pk_org='"
				+ this.getPk_org()
				+ "' and cyear='"
				+ this.getCalcYear()
				+ "' and cperiod='"
				+ this.getCalcMonth()
				+ "' and isaudit='Y' ) ";
		strWherePart += "         and def.dr = 0 ";
		strWherePart += "         and cast(def.enddate as date) >= '"
				+ this.getFirstDayOfMonth(this.getCalcYear(),
						this.getCalcMonth()).toString() + "' ";
		strWherePart += "         and def.begindate <= '"
				+ this.getLastDayOfMonth(this.getCalcYear(),
						this.getCalcMonth()).toString() + "' ";
		return strWherePart;
	}

	private UFDate getFirstDayOfMonth(String year, String month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.valueOf(year), Integer.valueOf(month) - 1, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return new UFDate(calendar.getTime()).asBegin();
	}

	private UFDate getLastDayOfMonth(String year, String month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.valueOf(year), Integer.valueOf(month) - 1, 1);
		int lastday = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, lastday);
		return new UFDate(calendar.getTime()).asEnd();
	}

	@Override
	public void unAudit(String pk_org, String cyear, String cperiod)
			throws BusinessException {
		if (!isAudit(pk_org, cyear, cperiod)) {
			throw new BusinessException(NCLangResOnserver.getInstance()
					.getStrByID("68861705", "NhicalcMaintainImpl-0002")/*
																		 * 选定期间尚未审核。
																		 */);
		}

		this.setPk_org(pk_org);
		this.setCalcYear(cyear);
		this.setCalcMonth(cperiod);

		String strSQL = " delete def from "
				+ PsndocDefTableUtil.getPsnNHISumTablename() + " def ";
		strSQL += getPsndocWherePart();
		this.getBaseDao().executeUpdate(strSQL);

		strSQL = " delete def from "
				+ PsndocDefTableUtil.getPsnNHIDetailTablename() + " def ";
		strSQL += getPsndocWherePart();
		strSQL += " AND def.glbdef42='" + pk_org + "'";
		this.getBaseDao().executeUpdate(strSQL);

		// strSQL = " UPDATE def ";
		// strSQL += " SET def.recordnum = def.recordnum - 1 ";
		// strSQL += " FROM " + PsndocDefTableUtil.getPsnNHISumTablename()
		// + " def ";
		// this.getBaseDao().executeUpdate(strSQL);
		//
		// strSQL = " UPDATE def ";
		// strSQL +=
		// " SET lastflag = (CASE WHEN recordnum = 0 THEN 'Y' ELSE 'N' END) ";
		// strSQL += " FROM " + PsndocDefTableUtil.getPsnNHISumTablename()
		// + " def ";
		strSQL = " WITH UpdateData ";
		strSQL += " AS ( ";
		strSQL += " 	SELECT ROW_NUMBER() OVER ( ";
		strSQL += " 			PARTITION BY pk_psndoc ORDER BY begindate DESC ";
		strSQL += " 				,ISNULL(enddate, '9999-12-31') DESC ";
		strSQL += " 			) - 1 AS recnumber ";
		strSQL += " 		,pk_psndoc_sub ";
		strSQL += " 	FROM " + PsndocDefTableUtil.getPsnNHISumTablename()
				+ " def ";
		strSQL += getPsndocWherePart();
		strSQL += " 	) ";
		strSQL += " UPDATE def ";
		strSQL += " SET recordnum = UpdateData.recnumber ";
		strSQL += " FROM " + PsndocDefTableUtil.getPsnNHISumTablename()
				+ " def ";
		strSQL += " INNER JOIN UpdateData ON UpdateData.pk_psndoc_sub = def.pk_psndoc_sub ";
		strSQL += getPsndocWherePart();
		this.getBaseDao().executeUpdate(strSQL);

		strSQL = " UPDATE def ";
		strSQL += " SET lastflag = ( ";
		strSQL += " 		CASE  ";
		strSQL += " 			WHEN recordnum = 0 ";
		strSQL += " 				THEN 'Y' ";
		strSQL += " 			ELSE 'N' ";
		strSQL += " 			END ";
		strSQL += " 		) ";
		strSQL += " FROM " + PsndocDefTableUtil.getPsnNHISumTablename()
				+ " def ";
		strSQL += getPsndocWherePart();
		this.getBaseDao().executeUpdate(strSQL);

		strSQL = "UPDATE twhr_nhicalc SET isaudit='N' WHERE pk_org='" + pk_org
				+ "' AND cyear='" + cyear + "' AND cperiod='" + cperiod
				+ "' AND dr=0";
		this.getBaseDao().executeUpdate(strSQL);
	}

	@Override
	public boolean isAudit(String pk_org, String cyear, String cperiod)
			throws BusinessException {
		String strSQL = "SELECT COUNT(*) FROM twhr_nhicalc WHERE pk_org='"
				+ pk_org + "' AND cyear='" + cyear + "' AND cperiod='"
				+ cperiod + "' AND dr=0 AND isaudit='Y'";
		int count = (Integer) this.getBaseDao().executeQuery(strSQL,
				new ColumnProcessor());
		return count > 0;
	}

	public BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}

	public String getCalcYear() {
		return calcYear;
	}

	public void setCalcYear(String calcYear) {
		this.calcYear = calcYear;
	}

	public String getCalcMonth() {
		return calcMonth;
	}

	public void setCalcMonth(String calcMonth) {
		this.calcMonth = calcMonth;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public List<NhiCalcVO> getNhiDataList() {
		return nhiDataList;
	}

	public void setNhiDataList(List<NhiCalcVO> nhiDataList) {
		this.nhiDataList = nhiDataList;
	}

}
