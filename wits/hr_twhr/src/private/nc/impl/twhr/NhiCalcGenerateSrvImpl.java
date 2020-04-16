package nc.impl.twhr;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.bd.util.DBAUtil;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.ml.NCLangResOnserver;
import nc.itf.hr.hi.WadocQueryVO;
import nc.itf.hr.hi.WadocQueryVOCutUtils;
import nc.itf.hr.hi.WadocQueryVOCutUtils.MoneyCalcTypeEnum;
import nc.itf.twhr.INhiCalcGenerateSrv;
import nc.itf.twhr.INhicalcMaintain;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pubitf.twhr.IRangetablePubQuery;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.hi.psndoc.PsndocDefVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.IAttributeMeta;
import nc.vo.pub.IVOMeta;
import nc.vo.pub.JavaType;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.twhr.nhicalc.NhiCalcVO;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;
import nc.vo.twhr.rangetable.RangeLineVO;
import nc.vo.twhr.rangetable.RangeTableAggVO;
import nc.vo.twhr.rangetable.RangeTableTypeEnum;
import nc.vo.wa.payfile.PayfileVO;

import org.apache.commons.lang.StringUtils;

public class NhiCalcGenerateSrvImpl implements INhiCalcGenerateSrv {
	private BaseDAO baseDao;
	// 级距表
	private RangeTableAggVO[] rangeTables = null;

	public void setRangeTables(RangeTableAggVO[] rangeTables) {
		this.rangeTables = rangeTables;
	}

	@Override
	public NhiCalcVO[] generateAdjustNHIData(String pk_org, String period) throws Exception {
		if (StringUtils.isEmpty(pk_org) || StringUtils.isEmpty(period)) {
			throw new BusinessException(NCLangResOnserver.getInstance().getStrByID("68861705",
					"NhiCalcGenerateSrvImpl-0000")/*
												 * 生成劳健保数据错误 ： 组织及期间不能为空 。
												 */);
		}

		checkPersonSettings(pk_org, period.split("-")[0], period.split("-")[1]);

		// 按組織、期間取本次生成人員列表
		List<String> psnList = findPersonList(pk_org, period.split("-")[0], period.split("-")[1]);
		String cyear = period.split("-")[0];
		String cperiod = period.split("-")[1];
		NhiCalcVO[] nhiFinalVOs = getAdjustNHIData(psnList, pk_org, cyear, cperiod);
		saveFinalVOs(nhiFinalVOs, pk_org, cyear, cperiod);
		this.setRangeTables(null);
		return nhiFinalVOs;
	}

	private void checkPersonSettings(String pk_org, String cyear, String cperiod) throws BusinessException {
		String strSQL = "";
		strSQL = " SELECT ";
		strSQL += "     psn.name + '('+psn.code+')' psnname, ";
		strSQL += "     heal.glbdef1 healname ";
		strSQL += " FROM ";
		strSQL += "     " + PsndocDefTableUtil.getPsnHealthTablename() + " heal ";
		strSQL += " INNER JOIN ";
		strSQL += "     bd_psndoc psn ";
		strSQL += " ON ";
		strSQL += "     heal.pk_psndoc = psn.pk_psndoc ";
		strSQL += " INNER JOIN ";
		strSQL += "     wa_data wa ";
		strSQL += " ON ";
		strSQL += "     wa.pk_psndoc = heal.pk_psndoc ";
		strSQL += " WHERE ";
		strSQL += "     ISNULL(heal.glbdef14, 'N')='Y' ";
		strSQL += " AND ISNULL(heal.glbdef3, '')='' ";
		strSQL += " AND heal.begindate<='" + this.getLastDayOfMonth(cyear, cperiod).asEnd() + "' ";
		strSQL += " AND CAST(ISNULL(heal.enddate, '9999-12-31') AS DATE) >= '"
				+ this.getFirstDayOfMonth(cyear, cperiod).asBegin() + "' ";
		strSQL += " AND wa.cyear='" + cyear + "' ";
		strSQL += " AND wa.cperiod='" + cperiod + "' ";
		strSQL += " AND wa.pk_org='" + pk_org + "'; ";

		List<Map> psnlist = (List<Map>) this.getBaseDao().executeQuery(strSQL, new MapListProcessor());

		StringBuilder strErrors = new StringBuilder();
		int i = 0;
		if (psnlist != null || psnlist.size() == 0) {
			for (Map psnmap : psnlist) {
				if (psnmap == null || psnmap.size() == 0) {
					break;
				}

				if (i++ <= 10) {
					strErrors.append(psnmap.get("psnname") + "之投保人" + psnmap.get("healname") + "，\r\n");
				} else {
					strErrors.append("……，\r\n");
					break;
				}
			}

			if (strErrors.length() > 0) {
				strErrors.append("身份證字號為空，請於員工資訊維護之健保資訊子集內補全後再試。");
				throw new BusinessException(strErrors.toString());
			}
		}
	}

	private void saveFinalVOs(NhiCalcVO[] nhiFinalVOs, String pk_org, String cyear, String cperiod)
			throws BusinessException {
		String strSQL = "UPDATE twhr_nhicalc SET dr=1 WHERE pk_org='" + pk_org + "' AND cyear='" + cyear
				+ "' AND cperiod='" + cperiod + "' AND dr=0 ";
		this.getBaseDao().executeUpdate(strSQL);

		INhicalcMaintain nhiSrv = NCLocator.getInstance().lookup(INhicalcMaintain.class);
		BatchOperateVO savedVOs = new BatchOperateVO();
		savedVOs.setAddObjs(nhiFinalVOs);
		nhiSrv.batchSave(savedVOs);
	}

	private Map<String, PsndocDefVO> laborSettings = new HashMap<String, PsndocDefVO>();
	private Map<String, PsndocDefVO> healthSettings = new HashMap<String, PsndocDefVO>();
	Map<String, UFBoolean> curLaborOutMap = new HashMap<String, UFBoolean>();
	Map<String, UFBoolean> curRetireOutMap = new HashMap<String, UFBoolean>();
	//3月一号进行加保的人员
	Set<String> adjustPersionSet = new HashSet();

	private List<String> findPersonList(String pk_org, String cyear, String cperiod) throws BusinessException {
		String strSQL = "";
		strSQL = "pk_org = '" + pk_org + "' and cyear='" + cyear + "' and cperiod='" + cperiod + "' and dr=0";

		List<String> pk_psndocs = new ArrayList<String>();
		// 根據組織及期間取薪資檔案表中的所有員工
		Collection<PayfileVO> payfiles = this.getBaseDao().retrieveByClause(PayfileVO.class, strSQL);

		if (payfiles != null && payfiles.size() > 0) {
			for (PayfileVO vo : payfiles) {
				if (!pk_psndocs.contains(vo.getPk_psndoc())) {
					pk_psndocs.add(vo.getPk_psndoc());
				}
			}
		}
		String tmpTableName = this.createPsndocTempTable(pk_psndocs);
		UFLiteralDate beginDateOfMonth = this.getFirstDayOfMonth(cyear, cperiod).toUFLiteralDate(
				ICalendar.BASE_TIMEZONE);
		UFLiteralDate endDateOfMonth = this.getLastDayOfMonth(cyear, cperiod).toUFLiteralDate(ICalendar.BASE_TIMEZONE);

		laborSettings = getLaborSettings(tmpTableName, beginDateOfMonth, endDateOfMonth, curLaborOutMap,
				curRetireOutMap);
		this.getLaborSettings(tmpTableName, beginDateOfMonth, endDateOfMonth, curLaborOutMap, curRetireOutMap);
		healthSettings = getHealthSettings(tmpTableName, beginDateOfMonth, endDateOfMonth);

		this.getBaseDao().executeUpdate("drop table " + tmpTableName);

		List<String> ret = new ArrayList<String>();
		for (String pk_psndoc : pk_psndocs) {
			PsndocDefVO laborSetting = laborSettings.get(pk_psndoc);
			PsndocDefVO healthSetting = healthSettings.get(pk_psndoc);

			UFBoolean isLabor = (laborSetting == null || laborSetting.getAttributeValue("glbdef10") == null) ? UFBoolean.FALSE
					: ((UFBoolean) laborSetting.getAttributeValue("glbdef10"));
			UFBoolean isRetire = (laborSetting == null || laborSetting.getAttributeValue("glbdef11") == null) ? UFBoolean.FALSE
					: ((UFBoolean) laborSetting.getAttributeValue("glbdef11"));
			UFBoolean isHealth = (healthSetting == null || healthSetting.getAttributeValue("glbdef14") == null) ? UFBoolean.FALSE
					: ((UFBoolean) healthSetting.getAttributeValue("glbdef14"));
			if (!isLabor.booleanValue() && !isRetire.booleanValue() && !isHealth.booleanValue()) {
				// 删除该人员
				if (laborSettings != null) {
					laborSettings.remove(pk_psndoc);
					curLaborOutMap.remove(pk_psndoc);
					curRetireOutMap.remove(pk_psndoc);
				}
				if (laborSettings != null) {
					healthSettings.remove(pk_psndoc);
				}
			} else {
				ret.add(pk_psndoc);
			}

		}
		return ret;
	}

	private String createPsndocTempTable(List<String> psnList) throws BusinessException {
		String tblName = DBAUtil.createTempTable("twhr_nhicalctmptable", "pk_psndoc nchar(20) NOT NULL", null);
		List<String> strSQLs = new ArrayList<String>();
		for (String pk_psndoc : psnList) {
			strSQLs.add(" insert into " + tblName + " (pk_psndoc) values('" + pk_psndoc + "'); ");
		}
		DBAUtil.execBatchSql(strSQLs.toArray(new String[0]));
		return tblName;
	}

	private BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}

	@Override
	public NhiCalcVO[] getAdjustNHIData(List<String> psnList, String pk_org, String cyear, String cperiod)
			throws BusinessException {
		String strTmpTableName = this.createPsndocTempTable(psnList); // 创建员工PK临时表

		// 從定調薪計算投保基準薪資 <pk_psndoc, WadocQueryVO[]>
		Map<String, WadocQueryVO[]> adjMap = new HashMap<String, WadocQueryVO[]>();
		if (psnList != null && psnList.size() > 0) {
			WadocQueryVO[] psnAdjData = getPersonNHIInfo(strTmpTableName, pk_org, cyear, cperiod, psnList);
			for (String pk_psndoc : psnList) {
				if (!adjMap.containsKey(pk_psndoc)) {
					adjMap.put(pk_psndoc, getPsnAdjData(psnAdjData, pk_psndoc));
				}
			}
		}

		// 按組織、期間、人員取勞健保設定
		NhiCalcVO[] nhiVOs = getPsnNhiData(pk_org, cyear, cperiod, psnList);

		checkNhiVOs(nhiVOs);

		NhiCalcVO[] nhiFinalVOs = compareNhiData(nhiVOs, adjMap, psnList, pk_org, cyear, cperiod);
		this.setRangeTables(null);
		return nhiFinalVOs;
	}

	private void checkNhiVOs(NhiCalcVO[] nhiVOs) throws BusinessException {
		// TODO 自動產生的方法 Stub

	}

	private NhiCalcVO[] compareNhiData(NhiCalcVO[] nhiVOs, Map<String, WadocQueryVO[]> adjMap, List<String> psnList,
			String pk_org, String cyear, String cperiod) throws BusinessException {
		List<NhiCalcVO> rtnVOs = new ArrayList<NhiCalcVO>();
		for (String pk_psndoc : psnList) {
			WadocQueryVO[] wadocs = adjMap.get(pk_psndoc);
			NhiCalcVO[] nhivos = getNhiCalcVOs(nhiVOs, pk_psndoc);
			List<NhiCalcVO> tmpVOList = new ArrayList<NhiCalcVO>();
			for (NhiCalcVO vo : nhivos) {
				tmpVOList.add(vo);
			}
			if (wadocs != null && wadocs.length > 0) {
				for (WadocQueryVO wadoc : wadocs) {
					NhiCalcVO tmpvo = null;
					boolean isNew = true;
					for (NhiCalcVO nhivo : tmpVOList) {
						if (wadoc.getBegindate() != null && wadoc.getEnddate() != null) {
							tmpvo = nhivo;
							isNew = false;
							break;
						}
					}

					if (isNew) {
						tmpvo = new NhiCalcVO();
						tmpvo.setLaborsalaryextend(UFDouble.ZERO_DBL);
						tmpvo.setHealthsalaryextend(UFDouble.ZERO_DBL);
					}

					tmpvo.setLaborsalary(wadoc.getNmoney());

					RangeLineVO laborLine = findRangeLine(this.getRangeTables(pk_org, cyear, cperiod),
							RangeTableTypeEnum.LABOR_RANGETABLE.toIntValue(), tmpvo.getLaborsalary());
					if (laborLine != null) {
						if (UFDouble.ZERO_DBL.equals(laborLine.getRangeupper())) {
							tmpvo.setLaborrange(new UFDouble(laborLine.getRangelower().sub(1)));
						} else {
							tmpvo.setLaborrange(laborLine.getRangeupper());
						}
					} else {
						tmpvo.setLaborrange(UFDouble.ZERO_DBL);
					}

					RangeLineVO retireLine = findRangeLine(this.getRangeTables(pk_org, cyear, cperiod),
							RangeTableTypeEnum.RETIRE_RANGETABLE.toIntValue(), tmpvo.getLaborsalary());
					if (retireLine != null) {
						if (UFDouble.ZERO_DBL.equals(retireLine.getRangeupper())) {
							tmpvo.setRetirerange(retireLine.getRangelower().sub(1));
						} else {
							tmpvo.setRetirerange(retireLine.getRangeupper());
						}
					} else {
						tmpvo.setRetirerange(UFDouble.ZERO_DBL);
					}

					tmpvo.setHealthsalary(wadoc.getNmoney());
					RangeLineVO healLine = findRangeLine(this.getRangeTables(pk_org, cyear, cperiod),
							RangeTableTypeEnum.NHI_RANGETABLE.toIntValue(), tmpvo.getHealthsalary());
					if (healLine != null) {
						if (UFDouble.ZERO_DBL.equals(healLine.getRangeupper())) {
							tmpvo.setHealthrange(healLine.getRangelower().sub(1));
						} else {
							tmpvo.setHealthrange(healLine.getRangeupper());
						}
					} else {
						tmpvo.setHealthrange(UFDouble.ZERO_DBL);
					}
					if (isNew) {
						tmpvo.setPk_psndoc(pk_psndoc);
						tmpvo.setPk_group(InvocationInfoProxy.getInstance().getGroupId());
						tmpvo.setPk_org(pk_org);
						tmpvo.setCyear(cyear);
						tmpvo.setCperiod(cperiod);
						tmpvo.setBegindate(wadoc.getBegindate().asBegin());
						tmpvo.setEnddate(wadoc.getEnddate() == null ? null : wadoc.getEnddate().asEnd());
						tmpVOList.add(tmpvo);
					}
				}
			}

			rtnVOs.addAll(tmpVOList);
		}

		return rtnVOs.toArray(new NhiCalcVO[0]);
	}

	private NhiCalcVO[] getNhiCalcVOs(NhiCalcVO[] nhiVOs, String pk_psndoc) {
		List<NhiCalcVO> vos = new ArrayList<NhiCalcVO>();
		for (NhiCalcVO vo : nhiVOs) {
			if (pk_psndoc.equals(vo.getPk_psndoc())) {
				vos.add(vo);
			}
		}
		return vos.toArray(new NhiCalcVO[0]);
	}

	private NhiCalcVO[] getPsnNhiData(String pk_org, String cyear, String cperiod, List<String> psnlist)
			throws BusinessException {
		UFLiteralDate beginDateOfMonth = this.getFirstDayOfMonth(cyear, cperiod).toUFLiteralDate(
				ICalendar.BASE_TIMEZONE);
		UFLiteralDate endDateOfMonth = this.getLastDayOfMonth(cyear, cperiod).toUFLiteralDate(ICalendar.BASE_TIMEZONE);

		List<NhiCalcVO> rtnVOList = new ArrayList<NhiCalcVO>();

		for (String pk_psndoc : psnlist) {
			PsndocDefVO laborSetting = laborSettings.get(pk_psndoc);
			PsndocDefVO healthSetting = healthSettings.get(pk_psndoc);

			if (laborSetting == null && healthSetting == null) {
				PsndocVO psnvo = (PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class, pk_psndoc);
				throw new BusinessException("未找到員工 [" + psnvo.getCode() + "] 的勞健保加保設定。");
			}

			boolean buildLabor = false;
			boolean buildHealth = false;
			// NhiCalcVO nhivo = getExistsNhiVO(rtnVOList, pk_psndoc);
			// if (nhivo == null) {
			// // 如不存在則新建
			NhiCalcVO nhivo = getNewCalcVO(pk_org, cyear, cperiod, pk_psndoc);
			// } else if (!nhivo.getOldlaborrange().equals(
			// getDoubleValue(laborSetting.getAttributeValue("glbdef4")))) {
			// // 如已存在但級距不同則新建
			// nhivo = getNewCalcVO(pk_org, cyear, cperiod, pk_psndoc);
			// }
			if (laborSetting != null) {
				buildLabor = true;
				nhivo.setBegindate(getEarlier(nhivo.getBegindate(), new UFDate(laborSetting.getBegindate().toDate()),
						new UFDate(beginDateOfMonth.toDate())));
				nhivo.setEnddate(getLater(nhivo.getEnddate(), new UFDate(laborSetting.getEnddate().toDate()),
						new UFDate(endDateOfMonth.toDate())));

				int countDays = getLaborCountDays(
						(UFLiteralDate) (laborSetting.getAttributeValue("REALLABORBEGINDATE") == null ? nhivo
								.getBegindate().toUFLiteralDate(ICalendar.BASE_TIMEZONE)
								: laborSetting.getAttributeValue("REALLABORBEGINDATE")),
						(UFLiteralDate) (laborSetting.getAttributeValue("REALLABORENDDATE") == null ? nhivo
								.getEnddate().toUFLiteralDate(ICalendar.BASE_TIMEZONE) : laborSetting
								.getAttributeValue("REALLABORENDDATE")), beginDateOfMonth, endDateOfMonth,laborSetting.getPk_psndoc());

				if (((UFBoolean) laborSetting.getAttributeValue("glbdef10")).booleanValue()) {
					if (laborSetting.getAttributeValue("glbdef12") != null
							&& getDoubleValue(laborSetting.getAttributeValue("glbdef12")).intValue() != 0) {
						countDays = getDoubleValue(laborSetting.getAttributeValue("glbdef12")).intValue();
					}
					if (nhivo.getLabordays() == null || nhivo.getLabordays() == 0) {
						// 天數為空或為零則計算當前天數
						nhivo.setLabordays(countDays);
					} else {
						// 天數不為空且不為零則累加當前天數
						nhivo.setLabordays((nhivo.getLabordays() + countDays) > 30 ? 30
								: (nhivo.getLabordays() + countDays));
					}

					nhivo.setOldlaborsalary(new UFDouble(getDoubleValue(laborSetting.getAttributeValue("glbdef2"))
							+ getDoubleValue(laborSetting.getAttributeValue("glbdef3"))));
					nhivo.setOldlaborrange(new UFDouble(getDoubleValue(laborSetting.getAttributeValue("glbdef4"))));
				}

				countDays = getRetireCountDays(
						beginDateOfMonth, endDateOfMonth,
						(laborSetting.getAttributeValue("glbdef14") == null ? nhivo.getBegindate().toUFLiteralDate(ICalendar.BASE_TIMEZONE): 
								((UFLiteralDate) laborSetting.getAttributeValue("glbdef14"))),
						(laborSetting.getAttributeValue("glbdef15") == null ? nhivo.getEnddate().toUFLiteralDate(ICalendar.BASE_TIMEZONE) : 
								((UFLiteralDate) laborSetting.getAttributeValue("glbdef15")))
								);

				if (((UFBoolean) laborSetting.getAttributeValue("glbdef11")).booleanValue()) {
					if (laborSetting.getAttributeValue("glbdef13") != null
							&& getDoubleValue(laborSetting.getAttributeValue("glbdef13")).intValue() != 0) {
						countDays = getDoubleValue(laborSetting.getAttributeValue("glbdef13")).intValue();
					}

					if (nhivo.getRetiredays() == null || nhivo.getRetiredays() == 0) {
						// 天數為空或為零則計算當前天數
						nhivo.setRetiredays(countDays);
					} else {
						// 天數不為空且不為零則累加當前天數
						nhivo.setRetiredays((nhivo.getRetiredays() + countDays) > 30 ? 30
								: (nhivo.getRetiredays() + countDays));
					}

					nhivo.setOldretirerange(new UFDouble(getDoubleValue(laborSetting.getAttributeValue("glbdef7"))));
				}
			}

			if (healthSetting != null) {
				if (((UFBoolean) healthSetting.getAttributeValue("glbdef14")).booleanValue()) {
					buildHealth = true;

					if (!buildLabor) {
						nhivo.setBegindate(getEarlier(nhivo.getBegindate(), new UFDate(healthSetting.getBegindate()
								.toDate()), new UFDate(beginDateOfMonth.toDate())));
						nhivo.setEnddate(getLater(nhivo.getEnddate(), new UFDate(healthSetting.getEnddate().toDate()),
								new UFDate(endDateOfMonth.toDate())));
					}

					nhivo.setOldhealthsalary(new UFDouble(getDoubleValue(healthSetting.getAttributeValue("glbdef6"))
							+ getDoubleValue(healthSetting.getAttributeValue("glbdef7"))));
					nhivo.setOldhealthrange(new UFDouble(getDoubleValue(healthSetting.getAttributeValue("glbdef16"))));
				}
			} else {
				// 定調資信息維護的勞健保加保按鈕，對應的健保級距是healthrange by George 20190805 缺陷Bug #28256
				// 計算勞健保節點的健保級距，對應的是oldhealthrange
				// 人員當月健保金額為0時，健保級距也需要顯示人員當月加保級距
				String strSQL = "SELECT glbdef16 FROM hi_psndoc_glbdef3 WHERE pk_psndoc = '" + nhivo.getPk_psndoc() + "' "
						+ " and dr = 0 and glbdef14 = N'Y' and glbdef2 = '本人' "
						+ " and enddate >= '" + beginDateOfMonth + "' and enddate <= '" + endDateOfMonth + "'"; 
				
				BigDecimal bd=(BigDecimal) new BaseDAO().executeQuery(strSQL, new ColumnProcessor());
				
				if (null != bd) {
					bd=bd.setScale(0, UFDouble.ROUND_DOWN);
					String Oldhealthrange = bd.toString();
					nhivo.setOldhealthrange(new UFDouble(Oldhealthrange));
				}
			}

			if (buildLabor || buildHealth) {
				rtnVOList.add(nhivo);
			}
		}

		return rtnVOList.toArray(new NhiCalcVO[0]);
	}

	private Map<String, PsndocDefVO> getHealthSettings(String psntmptable, UFLiteralDate beginDateOfMonth,
			UFLiteralDate endDateOfMonth) throws BusinessException {
		// 勞保投保或勞退投保為Y
		Map<String, PsndocDefVO> healthSettings = new HashMap<String, PsndocDefVO>();

		Collection setRst = (Collection) this.getBaseDao().retrieveByClause(PsndocDefTableUtil.getPsnHealthClass(),
				"pk_psndoc in (select pk_psndoc from " + psntmptable + ") and dr=0 and glbdef14='Y'");

		if (setRst != null && setRst.size() > 0) {
			for (Object set : setRst) {
				PsndocDefVO vo = (PsndocDefVO) set;

				UFLiteralDate dtSetBegin = vo.getBegindate() == null ? new UFLiteralDate("9999-12-31") : vo
						.getBegindate();
				UFLiteralDate dtSetEnd = vo.getEnddate() == null ? new UFLiteralDate("9999-12-31") : vo.getEnddate();
				// 設定日期區間與當前期間起迄日期有重疊，即為有效健保投保設定
				if (isInScope(beginDateOfMonth, endDateOfMonth, dtSetBegin, dtSetEnd, true)) {
					if (StringUtils.isEmpty((String) vo.getAttributeValue("glbdef2"))) {
						throw new BusinessException(
								"員工 ["
										+ ((PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class, vo.getPk_psndoc()))
												.getCode() + "] 的健保設定存在期間生效但未指定稱謂的記錄，請補合資料重試");
					}

					if (StringUtils.isEmpty((String) vo.getAttributeValue("glbdef3"))) {
						throw new BusinessException(
								"員工 ["
										+ ((PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class, vo.getPk_psndoc()))
												.getCode() + "] 的健保設定存在期間生效但未指定身份證字號的記錄，請補合資料重試");
					}

					if ("本人".equals(vo.getAttributeValue("glbdef2"))) {
						if (!healthSettings.containsKey(vo.getPk_psndoc())) {
							healthSettings.put(vo.getPk_psndoc(), vo);
							if (beginDateOfMonth.after(dtSetBegin)) {
								vo.setBegindate(beginDateOfMonth);
							}

							if (endDateOfMonth.before(dtSetEnd)) {
								vo.setEnddate(endDateOfMonth);
							}
							vo.setAttributeValue("glbdef13", UFBoolean.FALSE);// 暫不計算上月
						} else {
							throw new BusinessException(
									"員工 ["
											+ ((PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class,
													vo.getPk_psndoc())).getCode() + "] 健保設定重複");
						}
					}
				}
			}
		} else {
			// throw new BusinessException("未找到員工健保投保設定");
		}

		return healthSettings;
	}

	private Map<String, PsndocDefVO> getLaborSettings(String psntmptable, UFLiteralDate beginDateOfMonth,
			UFLiteralDate endDateOfMonth, Map<String, UFBoolean> curLaborOutMap, Map<String, UFBoolean> curRetireOutMap)
			throws BusinessException {
		boolean isNeedAdust = false;
		UFLiteralDate nextMonthFirstDay = endDateOfMonth.getDateAfter(1);
		//如果是二月份,则需要缓存那些1/3号加保的人员,在后面进行天数计算的时候,来判定是否在2月底进行了调保操作.
		if(endDateOfMonth.getMonth()==2){
			isNeedAdust = true;
		}
		// 勞保投保或勞退投保為Y
		Map<String, PsndocDefVO> laborSettings = new HashMap<String, PsndocDefVO>();
		Collection setRst = (Collection) this.getBaseDao().retrieveByClause(PsndocDefTableUtil.getPsnLaborClass(),
				"pk_psndoc in (select pk_psndoc from " + psntmptable + ") and dr=0 and (glbdef10='Y' or glbdef11='Y')");
		if (setRst != null && setRst.size() > 0) {
			for (Object set : setRst) {
				PsndocDefVO vo = (PsndocDefVO) set;

				UFLiteralDate dtLaborBegin = vo.getBegindate() == null ? new UFLiteralDate("9999-12-31") : vo
						.getBegindate();
				if(isNeedAdust&&nextMonthFirstDay.isSameDate(dtLaborBegin)){
					//3月一号进行加保的人员
					adjustPersionSet.add(vo.getPk_psndoc());
				}
				UFLiteralDate dtLaborEnd = vo.getEnddate() == null ? new UFLiteralDate("9999-12-31") : vo.getEnddate();

				UFLiteralDate dtRetireBegin = vo.getAttributeValue("glbdef14") == null ? new UFLiteralDate("9999-12-31")
						: (UFLiteralDate) vo.getAttributeValue("glbdef14");
				UFLiteralDate dtRetireEnd = vo.getAttributeValue("glbdef15") == null ? new UFLiteralDate("9999-12-31")
						: (UFLiteralDate) vo.getAttributeValue("glbdef15");
				// 設定日期區間與當前期間起迄日期有重疊，即為有效勞保投保設定
				if (isInScope(beginDateOfMonth, endDateOfMonth, dtLaborBegin, dtLaborEnd, false)
						|| isInScope(beginDateOfMonth, endDateOfMonth, dtRetireBegin, dtRetireEnd, false))
					if (!laborSettings.containsKey(vo.getPk_psndoc())) {
						// 投保劳保
						if (UFBoolean.TRUE.equals(vo.getAttributeValue("glbdef10"))) {
							laborSettings.put(vo.getPk_psndoc(), vo);

							// 是否当月退保
							if (endDateOfMonth.after(dtLaborEnd) || endDateOfMonth.isSameDate(dtLaborEnd)) {
								curLaborOutMap.put(vo.getPk_psndoc(), UFBoolean.TRUE);
							} else {
								curLaborOutMap.put(vo.getPk_psndoc(), UFBoolean.FALSE);
							}

							// 缓存劳保计算使用的时间段 
							//mod start 2019年2月26日17:00:47 投保的开始结束时间不修改成期间的开始和结束时间 Tank
							if (beginDateOfMonth.after(dtLaborBegin)) {
								vo.setBegindate(beginDateOfMonth);
							} 
							vo.setAttributeValue("REALLABORBEGINDATE", dtLaborBegin);
							
							if (endDateOfMonth.before(dtLaborEnd)) {
								vo.setEnddate(endDateOfMonth);
								curLaborOutMap.put(vo.getPk_psndoc(), UFBoolean.FALSE);
							}
							vo.setAttributeValue("REALLABORENDDATE", dtLaborEnd);
							//mod end 2019年2月26日17:00:47 投保的开始结束时间不修改成期间的开始和结束时间 Tank
						}

						// 投保劳退
						if (UFBoolean.TRUE.equals(vo.getAttributeValue("glbdef11"))) {
							// 是否当月退保
							if (endDateOfMonth.after(dtRetireEnd) || endDateOfMonth.isSameDate(dtRetireEnd)) {
								curRetireOutMap.put(vo.getPk_psndoc(), UFBoolean.TRUE);
							} else {
								curRetireOutMap.put(vo.getPk_psndoc(), UFBoolean.FALSE);
							}

							// 缓存劳退计算使用的时间段
							//mod start 劳退的开始和结束时间不受期间的开始和结束时间影响 Tank 2019年2月26日17:03:21
							
							vo.setAttributeValue("REALRETIREBEGINDATE", dtRetireBegin);
							
							vo.setAttributeValue("REALRETIREENDDATE", dtRetireEnd);
							
							//end 劳退的开始和结束时间不受期间的开始和结束时间影响 Tank 2019年2月26日17:03:21
							if (!laborSettings.containsKey(vo.getPk_psndoc())) {
								laborSettings.put(vo.getPk_psndoc(), vo);
							} else {
								PsndocDefVO laborvo = laborSettings.get(vo.getPk_psndoc());

								if (beginDateOfMonth.after(dtRetireBegin)) {
									laborvo.setBegindate(beginDateOfMonth);
								}

								if (endDateOfMonth.before(dtRetireEnd)) {
									laborvo.setEnddate(endDateOfMonth);
									curRetireOutMap.put(vo.getPk_psndoc(), UFBoolean.FALSE);
								}
							}
						}
					} else {
						PsndocDefVO lastVO = laborSettings.get(vo.getPk_psndoc());
						if (isInScope(lastVO.getBegindate(), lastVO.getEnddate(), dtLaborBegin, dtLaborEnd, false)) {
							throw new BusinessException(
									"員工 ["
											+ ((PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class,
													vo.getPk_psndoc())).getCode() + "] 勞保勞退設定重複");
						} else {
							if (UFBoolean.TRUE.equals(vo.getAttributeValue("glbdef10"))) {
								// labor_begindate
								if (lastVO.getBegindate().after(dtLaborBegin)) {
									lastVO.setBegindate(dtLaborBegin);
								}
								// labor_enddate
								if (lastVO.getEnddate().before(dtLaborEnd)) {
									// 是否当月退保
									if (lastVO.getEnddate().after(dtLaborEnd)
											|| lastVO.getEnddate().isSameDate(dtLaborEnd)) {
										curLaborOutMap.put(vo.getPk_psndoc(), UFBoolean.TRUE);
									} else {
										curLaborOutMap.put(vo.getPk_psndoc(), UFBoolean.FALSE);
									}

									lastVO.setEnddate(dtLaborEnd);
								}
								// labor_basesalary
								lastVO.setAttributeValue("glbdef2", getUFDouble(lastVO.getAttributeValue("glbdef2"))
										.add(getUFDouble(vo.getAttributeValue("glbdef2"))));
								// labor_adjustsalary
								lastVO.setAttributeValue("glbdef3", getUFDouble(lastVO.getAttributeValue("glbdef3"))
										.add(getUFDouble(vo.getAttributeValue("glbdef3"))));
								// labor_range
								lastVO.setAttributeValue(
										"glbdef4",
										getMaxUFDouble(lastVO.getAttributeValue("glbdef4"),
												vo.getAttributeValue("glbdef4")));
								// labor_days
								lastVO.setAttributeValue(
										"glbdef12",
										(lastVO.getAttributeValue("glbdef12") == null ? 0 : (Integer) lastVO
												.getAttributeValue("glbdef12"))
												+ (vo.getAttributeValue("glbdef12") == null ? 0 : (Integer) vo
														.getAttributeValue("glbdef12")));
								if (30 < ((Integer) (lastVO.getAttributeValue("glbdef12") == null ? 0 : lastVO
										.getAttributeValue("glbdef12")))) {
									lastVO.setAttributeValue("glbdef12", 30);
								}
							}

							if (UFBoolean.TRUE.equals(vo.getAttributeValue("glbdef11"))) {
								// retire_begindate
								if (((UFLiteralDate) lastVO.getAttributeValue("glbdef14")).after(dtRetireBegin)) {
									lastVO.setAttributeValue("glbdef14", dtRetireBegin);
								}
								// retire_enddate
								if (lastVO.getAttributeValue("glbdef15") != null
										&& ((UFLiteralDate) lastVO.getAttributeValue("glbdef15")).before(dtRetireEnd)) {
									// 是否当月退保
									if (((UFLiteralDate) lastVO.getAttributeValue("glbdef15")).after(dtRetireEnd)
											|| ((UFLiteralDate) lastVO.getAttributeValue("glbdef15"))
													.isSameDate(dtRetireEnd)) {
										curRetireOutMap.put(vo.getPk_psndoc(), UFBoolean.TRUE);
									} else {
										curRetireOutMap.put(vo.getPk_psndoc(), UFBoolean.FALSE);
									}

									lastVO.setAttributeValue("glbdef15", dtRetireEnd);
								}
								// retire_range
								lastVO.setAttributeValue(
										"glbdef7",
										getMaxUFDouble(lastVO.getAttributeValue("glbdef7"),
												vo.getAttributeValue("glbdef7")));
								// retire_days
								lastVO.setAttributeValue(
										"glbdef13",
										(lastVO.getAttributeValue("glbdef13") == null ? 0 : (Integer) lastVO
												.getAttributeValue("glbdef13"))
												+ (vo.getAttributeValue("glbdef13") == null ? 0 : (Integer) vo
														.getAttributeValue("glbdef13")));
								if (30 < ((Integer) (lastVO.getAttributeValue("glbdef13") == null ? 0 : lastVO
										.getAttributeValue("glbdef13")))) {
									lastVO.setAttributeValue("glbdef13", 30);
								}
							}

							lastVO.setAttributeValue("glbdef6", UFBoolean.FALSE); // 暫不計算上月
						}
					}
			}
		} else {
			// throw new BusinessException("未找到員工勞保勞退投保設定");
		}

		return laborSettings;
	}

	private UFDouble getMaxUFDouble(Object amount1, Object amount2) {
		UFDouble d1 = getUFDouble(amount1);
		UFDouble d2 = getUFDouble(amount2);
		return (d1.compareTo(d2)) == 1 ? d1 : d2;
	}

	private UFDouble getUFDouble(Object amount) {
		if (amount == null) {
			return UFDouble.ZERO_DBL;
		} else {
			return (UFDouble) amount;
		}
	}

	private boolean isInScope(UFLiteralDate scopeBegin, UFLiteralDate scopeEnd, UFLiteralDate checkBegin,
			UFLiteralDate checkEnd, boolean isHealth) {
		scopeBegin = scopeBegin == null ? new UFLiteralDate("0000-01-01") : scopeBegin;
		scopeEnd = scopeEnd == null ? new UFLiteralDate("9999-12-31") : scopeEnd;
		checkBegin = checkBegin == null ? new UFLiteralDate("0000-01-01") : checkBegin;
		checkEnd = checkEnd == null ? new UFLiteralDate("9999-12-31") : checkEnd;
		boolean ret = (scopeEnd.after(checkBegin) || scopeEnd.isSameDate(checkBegin))
				&& (scopeBegin.before(checkEnd) || scopeBegin.isSameDate(checkEnd));

		// 如果是健保還需要多加一層判斷
		// 即檢查期間是否包含範圍期間的最後一天
		// 即健保設定中如果有兩條資料都包含了當月設定，
		// 但只有一條可以包含本月最後一天
		// 由於健保是全月在保，所以最後一天在保才為有效設定
		if (isHealth) {
			ret = (scopeEnd.isSameDate(checkEnd) || scopeEnd.before(checkEnd))
					&& (scopeEnd.isSameDate(checkBegin) || scopeEnd.after(checkBegin));
		}

		return ret;
	}

	public int getLaborCountDays(UFLiteralDate beginDate, UFLiteralDate endDate, UFLiteralDate beginDateOfMonth,
			UFLiteralDate endDateOfMonth,String pk_psndoc) {
		if (beginDate.after(endDate)) {
			return 0;
		}

		// 计算基数
		int calcuBase = -1;

		if (endDate.isSameDate(endDateOfMonth)) {
			
			if(adjustPersionSet.contains(pk_psndoc)){
				//此人在2月28/29日退保,在3月1日加保,则为算30天
				calcuBase = 30;
			}else{
				// 當月最後一日退保，计算基数=计算月天数
				calcuBase = endDateOfMonth.getDay();
				calcuBase = calcuBase == 31 ? 30 : calcuBase;
			}
			
		} else if (endDate.after(endDateOfMonth)) {
			// 未退，计算基数=30
			calcuBase = 30;
		} else {
			// 當月非最後一日退保：计算基数=退保日
			calcuBase = endDate.getDay();
		}

		beginDate = beginDate.before(beginDateOfMonth) ? beginDateOfMonth : beginDate;
		beginDate = beginDate.getDay() == 31 ? beginDate.getDateBefore(1) : beginDate;

		// 劳保加保天数 = 计算基数 - 加保日 + 1
		return calcuBase - beginDate.getDay() + 1;
	}

	private int getRetireCountDays(UFLiteralDate beginDateOfMonth, UFLiteralDate endDateOfMonth,
			UFLiteralDate beginDate, UFLiteralDate endDate) {
		if (beginDate.after(endDate)) {
			return 0;
		}

		// 计算基数
		int calcuBase = -1;

		if (endDate.isSameDate(endDateOfMonth)) {
			// 當月最後一日退保，计算基数=30
			calcuBase = 30;
		} else if (endDate.after(endDateOfMonth)) {
			// 未退，计算基数=30
			calcuBase = 30;
		} else {
			// 當月非最後一日退保：计算基数=退保日
			calcuBase = endDate.getDay();
		}

		beginDate = beginDate.before(beginDateOfMonth) ? beginDateOfMonth : beginDate;
		beginDate = beginDate.getDay() == 31 ? beginDate.getDateBefore(1) : beginDate;

		// 劳保加保天数 = 计算基数 - 加保日 + 1
		return calcuBase - beginDate.getDay() + 1;
	}

	private NhiCalcVO getNewCalcVO(String pk_org, String cyear, String cperiod, String pk_psndoc) {
		NhiCalcVO nhivo = new NhiCalcVO();
		resetVO(nhivo);
		nhivo.setPk_psndoc(pk_psndoc);
		nhivo.setPk_group(InvocationInfoProxy.getInstance().getGroupId());
		nhivo.setPk_org(pk_org);
		nhivo.setCyear(cyear);
		nhivo.setCperiod(cperiod);
		return nhivo;
	}

	private void resetVO(NhiCalcVO nhivo) {
		IVOMeta meta = nhivo.getMetaData();
		for (String name : nhivo.getAttributeNames()) {
			IAttributeMeta att = meta.getAttribute(name);
			if (att.getJavaType() == JavaType.UFDouble) {
				nhivo.setAttributeValue(name, UFDouble.ZERO_DBL);
			} else if (att.getJavaType() == JavaType.Integer) {
				nhivo.setAttributeValue(name, 0);
			} else if (att.getJavaType() == JavaType.UFBoolean) {
				nhivo.setAttributeValue(name, UFBoolean.FALSE);
			}
		}
	}

	private UFDate getLater(UFDate originDate, UFDate newDate, UFDate limitDate) {
		UFDate ret = null;
		if (originDate == null) {
			ret = newDate;
		} else if (newDate == null) {
			ret = new UFDate("9999-12-31");
		} else if (newDate.after(originDate)) {
			ret = newDate;
		} else {
			ret = originDate;
		}

		if (ret.after(limitDate)) {
			ret = limitDate;
		}

		return ret;
	}

	private UFDate getEarlier(UFDate originDate, UFDate newDate, UFDate limitDate) {
		UFDate ret = null;
		if (originDate == null) {
			ret = newDate;
		} else if (newDate == null) {
			ret = new UFDate("9999-12-31");
		} else if (newDate.before(originDate)) {
			ret = newDate;
		} else {
			ret = originDate;
		}

		if (ret.before(limitDate)) {
			ret = limitDate;
		}
		return ret;
	}

	private RangeTableAggVO[] getRangeTables(String pk_org, String cyear, String cperiod) throws BusinessException {
		if (rangeTables == null) {
			IRangetablePubQuery qry = (IRangetablePubQuery) NCLocator.getInstance().lookup(IRangetablePubQuery.class);

			rangeTables = qry.queryRangetableByType(pk_org, -1, this.getFirstDayOfMonth(cyear, cperiod));
		}
		return rangeTables;
	}

	private RangeLineVO findRangeLine(RangeTableAggVO[] rtAggVOs, int rangeType, UFDouble salAmount) {
		for (RangeTableAggVO agg : rtAggVOs) {
			if (agg.getParentVO().getTabletype().equals(rangeType)) {
				RangeLineVO[] lines = (RangeLineVO[]) agg.getChildren(RangeLineVO.class);
				for (RangeLineVO line : lines) {
					UFDouble stdUpperValue = ((RangeLineVO) line).getRangeupper();
					if (stdUpperValue == null || stdUpperValue.equals(UFDouble.ZERO_DBL)) {
						stdUpperValue = new UFDouble(Double.MAX_VALUE);
					}
					UFDouble stdLowerValue = ((RangeLineVO) line).getRangelower();
					if (stdLowerValue == null) {
						stdLowerValue = UFDouble.ZERO_DBL;
					}
					if (salAmount.doubleValue() >= stdLowerValue.doubleValue()
							&& salAmount.doubleValue() <= stdUpperValue.doubleValue()) {
						if ((RangeTableTypeEnum.LABOR_RANGETABLE.toIntValue() == rangeType && Integer.valueOf(line
								.getRangegrade()) == 30)
								|| RangeTableTypeEnum.LABOR_RANGETABLE.toIntValue() != rangeType) {
							return (RangeLineVO) line;
						}
					}
				}
			}
		}
		return null;
	}

	private Double getDoubleValue(Object object) {
		UFDouble ret = UFDouble.ZERO_DBL;
		if (object != null) {
			String strVal = object.toString();
			if (strVal.contains("E")) {
				String[] strVals = strVal.split("E");
				ret = new UFDouble(Integer.valueOf(strVals[0]) * Math.log(Integer.valueOf(strVals[1])));
			} else {
				ret = new UFDouble(strVal);
			}
		}
		return ret.doubleValue();
	}

	private WadocQueryVO[] getPsnAdjData(WadocQueryVO[] psnAdjData, String pk_psndoc) {
		if (psnAdjData != null && psnAdjData.length > 0) {
			List<WadocQueryVO> rtnVOs = new ArrayList<WadocQueryVO>();
			for (WadocQueryVO vo : psnAdjData) {
				if (pk_psndoc.equals(vo.getPk_psndoc())) {
					if (!rtnVOs.contains(vo)) {
						rtnVOs.add(vo);
					}
				}
			}
			return rtnVOs.toArray(new WadocQueryVO[0]);
		}

		return null;
	}

	private WadocQueryVO[] getPersonNHIInfo(String psntmptable, String pk_org, String cyear, String cperiod,
			List<String> psnList) throws BusinessException {
		String beginDateOfMonth = this.getFirstDayOfMonth(cyear, cperiod).toString();
		String endDateOfMonth = this.getLastDayOfMonth(cyear, cperiod).toString();
		String strSQL = "";
		strSQL += " SELECT  wadoc." + WadocQueryVO.PK_PSNDOC + " ,";
		strSQL += "         IIF(wadoc." + WadocQueryVO.BEGINDATE + " <= '" + beginDateOfMonth + "' AND (wadoc."
				+ WadocQueryVO.ENDDATE + " IS NULL OR wadoc." + WadocQueryVO.ENDDATE + " > '" + beginDateOfMonth
				+ "'), '" + beginDateOfMonth + "', wadoc." + WadocQueryVO.BEGINDATE + ") " + WadocQueryVO.BEGINDATE
				+ " , ";
		strSQL += "        IIF(wadoc." + WadocQueryVO.BEGINDATE + " < '" + endDateOfMonth + "' AND (wadoc."
				+ WadocQueryVO.ENDDATE + " >= '" + endDateOfMonth + "' OR wadoc." + WadocQueryVO.ENDDATE
				+ " IS NULL) , '" + endDateOfMonth + "', wadoc." + WadocQueryVO.ENDDATE + ") " + WadocQueryVO.ENDDATE
				+ " , ";
		strSQL += "         SUM(wadoc." + WadocQueryVO.NMONEY + ") " + WadocQueryVO.NMONEY;
		strSQL += " FROM    hi_psndoc_wadoc wadoc";
		strSQL += "         INNER JOIN wa_item waitem ON wadoc.pk_wa_item = waitem.pk_wa_item";
		strSQL += "                                          AND waitem.dr = 0";
		strSQL += "         INNER JOIN twhr_waitem_30 waitemex ON waitem.pk_wa_item = waitemex.pk_wa_item";
		strSQL += "                                                   AND waitemex.dr = 0";
		// /strSQL += " WHERE   ";
		strSQL += " WHERE   waitemex.isnhiitem_30 = N'Y' AND ";
		strSQL += " wadoc." + WadocQueryVO.BEGINDATE + "<='" + beginDateOfMonth + "' AND ";
		strSQL += " (wadoc." + WadocQueryVO.ENDDATE + " IS NULL OR wadoc." + WadocQueryVO.ENDDATE + ">='"
				+ beginDateOfMonth + "') ";
		strSQL += "	AND wadoc." + WadocQueryVO.PK_PSNDOC + " IN (SELECT pk_psndoc FROM " + psntmptable + ")";
		strSQL += "         AND wadoc.waflag='Y' AND wadoc.dr = 0";
		strSQL += " GROUP BY wadoc." + WadocQueryVO.PK_PSNDOC + " ,";
		strSQL += "         IIF(wadoc." + WadocQueryVO.BEGINDATE + " <= '" + beginDateOfMonth + "' AND (wadoc."
				+ WadocQueryVO.ENDDATE + " IS NULL OR wadoc." + WadocQueryVO.ENDDATE + " > '" + beginDateOfMonth
				+ "'), '" + beginDateOfMonth + "', wadoc." + WadocQueryVO.BEGINDATE + ") , ";
		strSQL += "        IIF(wadoc." + WadocQueryVO.BEGINDATE + " < '" + endDateOfMonth + "' AND (wadoc."
				+ WadocQueryVO.ENDDATE + " >= '" + endDateOfMonth + "' OR wadoc." + WadocQueryVO.ENDDATE
				+ " IS NULL) , '" + endDateOfMonth + "', wadoc." + WadocQueryVO.ENDDATE + ") ";
		strSQL += " ORDER BY ";
		strSQL += "         IIF(wadoc." + WadocQueryVO.BEGINDATE + " <= '" + beginDateOfMonth + "' AND (wadoc."
				+ WadocQueryVO.ENDDATE + " IS NULL OR wadoc." + WadocQueryVO.ENDDATE + " > '" + beginDateOfMonth
				+ "'), '" + beginDateOfMonth + "', wadoc." + WadocQueryVO.BEGINDATE + ") , ";
		strSQL += "        IIF(wadoc." + WadocQueryVO.BEGINDATE + " < '" + endDateOfMonth + "' AND (wadoc."
				+ WadocQueryVO.ENDDATE + " >= '" + endDateOfMonth + "' OR wadoc." + WadocQueryVO.ENDDATE
				+ " IS NULL) , '" + endDateOfMonth + "', wadoc." + WadocQueryVO.ENDDATE + ") ";
		WadocQueryVO[] wadocVOs = this.executeQueryVOs(strSQL, WadocQueryVO.class);

		List<WadocQueryVO> finalQueryVOs = new ArrayList<WadocQueryVO>();
		if (wadocVOs.length > 0) {
			for (String pk_psndoc : psnList) {
				WadocQueryVO[] psnWadocVOs = getWadocVOsByPsn(wadocVOs, pk_psndoc);
				if (psnWadocVOs != null && psnWadocVOs.length > 0) {
					WadocQueryVO[] tempWadocVOs = new WadocQueryVO[] { psnWadocVOs[0] };
					if (psnWadocVOs.length == 1) {
						finalQueryVOs.add(psnWadocVOs[0]);
					} else {
						for (int i = 1; i < psnWadocVOs.length; i++) {
							tempWadocVOs = WadocQueryVOCutUtils.getCombinedVOs(tempWadocVOs, psnWadocVOs[i],
									MoneyCalcTypeEnum.SUM);
						}
						finalQueryVOs.addAll(new ArrayList<WadocQueryVO>(Arrays.asList(tempWadocVOs)));
					}
				}
			}
		}

		return finalQueryVOs.toArray(new WadocQueryVO[0]);
	}

	private WadocQueryVO[] getWadocVOsByPsn(WadocQueryVO[] wadocVOs, String pk_psndoc) {
		List<WadocQueryVO> wadocvos = new ArrayList<WadocQueryVO>();
		if (wadocVOs != null && wadocVOs.length > 0) {
			for (WadocQueryVO vo : wadocVOs) {
				if (pk_psndoc.equals(vo.getPk_psndoc())) {
					wadocvos.add(vo);
				}
			}
		}
		return wadocvos.toArray(new WadocQueryVO[0]);
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

	private <T> T[] executeQueryVOs(String sql, Class<T> voClass) throws DAOException {
		List<T> list = (List<T>) this.getBaseDao().executeQuery(sql, new BeanListProcessor(voClass));
		if (list == null || list.size() == 0) {
			return (T[]) Array.newInstance(voClass, 0);
		}

		return list.toArray((T[]) Array.newInstance(voClass, list.size()));
	}

	@Override
	public NhiCalcVO[] getAdjustNHIData(String pk_psndoc, String pk_org, UFLiteralDate inDutyDate)
			throws BusinessException {
		// 從定調薪計算投保基準薪資 <pk_psndoc, WadocQueryVO[]>
		Map<String, WadocQueryVO[]> adjMap = new HashMap<String, WadocQueryVO[]>();
		WadocQueryVO[] psnAdjData = getPersonNHIInfo(pk_psndoc, pk_org, inDutyDate);
		adjMap.put(pk_psndoc, getPsnAdjData(psnAdjData, pk_psndoc));

		// 對比勞健保與定調薪資料
		NhiCalcVO[] nhiFinalVOs = compareNhiData(new NhiCalcVO[0], adjMap, Arrays.asList(pk_psndoc), pk_org,
				String.valueOf(inDutyDate.getYear()), String.valueOf(inDutyDate.getMonth()));

		this.setRangeTables(null);
		return nhiFinalVOs;
	}

	private WadocQueryVO[] getPersonNHIInfo(String pk_psndoc, String pk_org, UFLiteralDate inDutyDate)
			throws BusinessException {
		String strSQL = " SELECT pk_psndoc, '" + inDutyDate.toStdString() + "' begindate, null enddate, "
				+ "    SUM(wadoc.nmoney) nmoney " + " FROM " + "     hi_psndoc_wadoc wadoc " + " INNER JOIN "
				+ "     wa_item waitem " + " ON " + "     wadoc.pk_wa_item = waitem.pk_wa_item "
				+ " AND waitem.dr = 0 " + " INNER JOIN " + "     twhr_waitem_30 waitemex " + " ON "
				+ "     waitem.pk_wa_item = waitemex.pk_wa_item " + " AND waitemex.dr = 0 " + " WHERE "
				+ "     waitemex.isnhiitem_30 = N'Y' " + " AND ISNULL(wadoc.enddate, '9999-12-31 23:59:59') >='"
				+ inDutyDate.toStdString() + "' " + " AND wadoc.pk_psndoc='" + pk_psndoc + "' "
				+ " AND wadoc.waflag='Y' " + " AND wadoc.dr = 0 " + " GROUP BY " + "     wadoc.pk_psndoc ";
		WadocQueryVO[] wadocVOs = this.executeQueryVOs(strSQL, WadocQueryVO.class);

		List<WadocQueryVO> finalQueryVOs = new ArrayList<WadocQueryVO>();
		if (wadocVOs.length > 0) {
			if (wadocVOs != null && wadocVOs.length > 0) {
				WadocQueryVO[] tempWadocVOs = new WadocQueryVO[] { wadocVOs[0] };
				if (wadocVOs.length == 1) {
					finalQueryVOs.add(wadocVOs[0]);
				} else {
					for (int i = 1; i < wadocVOs.length; i++) {
						tempWadocVOs = WadocQueryVOCutUtils.getCombinedVOs(tempWadocVOs, wadocVOs[i],
								MoneyCalcTypeEnum.SUM);
					}
					finalQueryVOs.addAll(new ArrayList<WadocQueryVO>(Arrays.asList(tempWadocVOs)));
				}
			}
		}

		return finalQueryVOs.toArray(new WadocQueryVO[0]);
	}

}
