package nc.impl.twhr;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.bd.util.DBAUtil;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.ml.NCLangResOnserver;
import nc.hr.utils.InSQLCreator;
import nc.itf.hr.hi.WadocQueryVO;
import nc.itf.hr.hi.WadocQueryVOCutUtils;
import nc.itf.hr.hi.WadocQueryVOCutUtils.MoneyCalcTypeEnum;
import nc.itf.twhr.INhiCalcGenerateSrv;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pubitf.twhr.IRangetablePubQuery;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
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
import nc.vo.twhr.nhicalc.EpyfamilyVO;
import nc.vo.twhr.nhicalc.NhiCalcUtils;
import nc.vo.twhr.nhicalc.NhiCalcVO;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;
import nc.vo.twhr.rangetable.RangeLineVO;
import nc.vo.twhr.rangetable.RangeTableAggVO;
import nc.vo.twhr.rangetable.RangeTableTypeEnum;
import nc.vo.wa.payfile.PayfileVO;

import org.apache.commons.lang.StringUtils;

public class NhiCalcGenerateSrvImpl implements INhiCalcGenerateSrv {
	private BaseDAO baseDao;
	// �����
	private RangeTableAggVO[] rangeTables = null;
	private String splitter = "::";

	public void setRangeTables(RangeTableAggVO[] rangeTables) {
		this.rangeTables = rangeTables;
	}

	@Override
	public NhiCalcVO[] generateAdjustNHIData(String pk_org, String period) throws Exception {
		if (StringUtils.isEmpty(pk_org) || StringUtils.isEmpty(period)) {
			throw new BusinessException(NCLangResOnserver.getInstance().getStrByID("68861705",
					"NhiCalcGenerateSrvImpl-0000")/*
												 * �����ͽ������ݴ��� �� ��֯���ڼ䲻��Ϊ�� ��
												 */);
		}

		checkPersonSettings(pk_org, period.split("-")[0], period.split("-")[1]);

		// ���M�������gȡ���������ˆT�б�
		List<String> psnList = findPersonList(pk_org, period.split("-")[0], period.split("-")[1], true);
		String cyear = period.split("-")[0];
		String cperiod = period.split("-")[1];
		// ɾ��֮ǰ���ɵ�����
		delete(psnList, cyear, cperiod);
		NhiCalcVO[] nhiFinalVOs = getNhiData(pk_org, psnList, cyear, cperiod);
		// getAdjustNHIData(psnList, pk_org, cyear, cperiod);
		if (nhiFinalVOs == null || nhiFinalVOs.length == 0) {
			throw new BusinessException("����ָ���l��δ�l�F��Ч�������o�����Ʉڽ���������");
		}
		this.getBaseDao().insertVOArray(nhiFinalVOs);
		this.setRangeTables(null);
		return nhiFinalVOs;
	}

	private void delete(List<String> psnList, String cyear, String cperiod) throws BusinessException {
		InSQLCreator insql = new InSQLCreator();
		String psndocsInSQL = insql.getInSQL(psnList.toArray(new String[0]));
		getBaseDao().deleteByClause(NhiCalcVO.class,
				"pk_psndoc in(" + psndocsInSQL + ")  and cyear='" + cyear + "' and cperiod='" + cperiod + "'");
		getBaseDao().deleteByClause(EpyfamilyVO.class, "pk_nhicalc not in (select pk_nhicalc from twhr_nhicalc)");
	}

	private void checkPersonSettings(String pk_org, String cyear, String cperiod) throws BusinessException {
		String strSQL = "";
		int dbType = this.getBaseDao().getDBType();
		switch (dbType) {
		case 2: // SQL
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
			break;
		case 1: // ORACLE
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
			strSQL += " AND to_date(heal.begindate, 'YYYY-MM-DD')<= to_date('"
					+ this.getLastDayOfMonth(cyear, cperiod).asEnd() + "', 'YYYY-MM-DD HH24:MI:SS') ";
			strSQL += " AND to_date(ISNULL(heal.enddate, '9999-12-31'), 'YYYY-MM-DD') >= to_date('"
					+ this.getFirstDayOfMonth(cyear, cperiod).asBegin() + "', 'YYYY-MM-DD HH24:MI:SS') ";
			strSQL += " AND wa.cyear='" + cyear + "' ";
			strSQL += " AND wa.cperiod='" + cperiod + "' ";
			strSQL += " AND wa.pk_org='" + pk_org + "'; ";
			break;
		}
		List<Map> psnlist = (List<Map>) this.getBaseDao().executeQuery(strSQL, new MapListProcessor());

		StringBuilder strErrors = new StringBuilder();
		int i = 0;
		if (psnlist != null || psnlist.size() == 0) {
			for (Map psnmap : psnlist) {
				if (psnmap == null || psnmap.size() == 0) {
					break;
				}

				if (i++ <= 10) {
					strErrors.append(psnmap.get("psnname") + "֮Ͷ����" + psnmap.get("healname") + "��\r\n");
				} else {
					strErrors.append("������\r\n");
					break;
				}
			}

			if (strErrors.length() > 0) {
				strErrors.append("�����C��̖��գ�Ո춆T���YӍ�S�o֮�����YӍ�Ӽ����aȫ����ԇ��");
				throw new BusinessException(strErrors.toString());
			}
		}
	}

	private Map<String, PsndocDefVO> laborSettings = new HashMap<String, PsndocDefVO>();
	private Map<String, PsndocDefVO> healthSettings = new HashMap<String, PsndocDefVO>();
	Map<String, UFBoolean> curLaborOutMap = new HashMap<String, UFBoolean>();
	Map<String, UFBoolean> curRetireOutMap = new HashMap<String, UFBoolean>();

	@SuppressWarnings("unchecked")
	private List<String> findPersonList(String pk_org, String cyear, String cperiod, boolean onlyQueryPsn)
			throws BusinessException {
		String strSQL = "";
		strSQL = "pk_org = '" + pk_org + "' and cyear='" + cyear + "' and cperiod='" + cperiod + "' and dr=0";

		List<String> pk_psndocs = new ArrayList<String>();
		// �����M�������gȡн�Y�n�����е����ІT��
		Collection<PayfileVO> payfiles = this.getBaseDao().retrieveByClause(PayfileVO.class, strSQL);

		if (payfiles != null && payfiles.size() > 0) {
			for (PayfileVO vo : payfiles) {
				if (!pk_psndocs.contains(vo.getPk_psndoc())) {
					pk_psndocs.add(vo.getPk_psndoc());
				}
			}
		}

		if (onlyQueryPsn) {
			return pk_psndocs;
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
				// ɾ������Ա
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
		String tblName = DBAUtil.createTempTable("twhr_nhicalctmptable", "pk_psndoc char(20) NOT NULL", null);
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
		String strTmpTableName = this.createPsndocTempTable(psnList); // ����Ա��PK��ʱ��

		// �Ķ��{нӋ��Ͷ������н�Y <pk_psndoc, WadocQueryVO[]>
		Map<String, WadocQueryVO[]> adjMap = new HashMap<String, WadocQueryVO[]>();
		if (psnList != null && psnList.size() > 0) {
			WadocQueryVO[] psnAdjData = getPersonNHIInfo(strTmpTableName, pk_org, cyear, cperiod, psnList);
			for (String pk_psndoc : psnList) {
				if (!adjMap.containsKey(pk_psndoc)) {
					adjMap.put(pk_psndoc, getPsnAdjData(psnAdjData, pk_psndoc));
				}
			}
		}

		// ���M�������g���ˆTȡ�ڽ����O��
		NhiCalcVO[] nhiVOs = getPsnNhiData(pk_org, cyear, cperiod, psnList);

		// ���Ȅڽ����c���{н�Y��
		NhiCalcVO[] nhiFinalVOs = compareNhiData(nhiVOs, adjMap, psnList, pk_org, cyear, cperiod);
		this.setRangeTables(null);
		return nhiFinalVOs;
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
						this.setRangeTables(null);
						PsndocVO psnvo = (PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class, pk_psndoc);
						throw new BusinessException("δ�ҵ��T�� [" + psnvo.getCode() + "] �����g�� [" + cyear + "-" + cperiod
								+ "] ����Ч�ڱ����ࡣ");
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
						this.setRangeTables(null);
						PsndocVO psnvo = (PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class, pk_psndoc);
						throw new BusinessException("δ�ҵ��T�� [" + psnvo.getCode() + "] �����g�� [" + cyear + "-" + cperiod
								+ "] ����Ч���˼��ࡣ");
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
						this.setRangeTables(null);
						PsndocVO psnvo = (PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class, pk_psndoc);
						throw new BusinessException("δ�ҵ��T�� [" + psnvo.getCode() + "] �����g�� [" + cyear + "-" + cperiod
								+ "] ����Ч�������ࡣ");
					}
					if (isNew) {
						tmpvo.setPk_psndoc(pk_psndoc);
						tmpvo.setPk_group(InvocationInfoProxy.getInstance().getGroupId());
						tmpvo.setPk_org(pk_org);
						tmpvo.setCyear(cyear);
						tmpvo.setCperiod(cperiod);
						tmpvo.setBegindate(wadoc.getBegindate().asBegin());
						tmpvo.setEnddate(wadoc.getEnddate() == null ? null : wadoc.getEnddate().asEnd());
						tmpvo.setPk_corp(pk_org);// ����ҷ�����֯
						tmpVOList.add(tmpvo);
					}
				}
			}

			rtnVOs.addAll(tmpVOList);
		}
		this.setRangeTables(null);
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
				throw new BusinessException("δ�ҵ��T�� [" + psnvo.getCode() + "] �Ąڽ����ӱ��O����");
			}

			boolean buildLabor = false;
			boolean buildHealth = false;
			// NhiCalcVO nhivo = getExistsNhiVO(rtnVOList, pk_psndoc);
			// if (nhivo == null) {
			// // �粻���ڄt�½�
			NhiCalcVO nhivo = getNewCalcVO(pk_org, cyear, cperiod, pk_psndoc);
			// } else if (!nhivo.getOldlaborrange().equals(
			// getDoubleValue(laborSetting.getAttributeValue("glbdef4")))) {
			// // ���Ѵ��ڵ����಻ͬ�t�½�
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
								.getAttributeValue("REALLABORENDDATE")), beginDateOfMonth, endDateOfMonth);

				if (((UFBoolean) laborSetting.getAttributeValue("glbdef10")).booleanValue()) {
					if (laborSetting.getAttributeValue("glbdef12") != null
							&& getDoubleValue(laborSetting.getAttributeValue("glbdef12")).intValue() != 0) {
						countDays = getDoubleValue(laborSetting.getAttributeValue("glbdef12")).intValue();
					}
					if (nhivo.getLabordays() == null || nhivo.getLabordays() == 0) {
						// �씵��ջ����tӋ�㮔ǰ�씵
						nhivo.setLabordays(countDays);
					} else {
						// �씵������Ҳ�����t�ۼӮ�ǰ�씵
						nhivo.setLabordays((nhivo.getLabordays() + countDays) > 30 ? 30
								: (nhivo.getLabordays() + countDays));
					}

					nhivo.setOldlaborsalary(new UFDouble(getDoubleValue(laborSetting.getAttributeValue("glbdef2"))
							+ getDoubleValue(laborSetting.getAttributeValue("glbdef3"))));
					nhivo.setOldlaborrange(new UFDouble(getDoubleValue(laborSetting.getAttributeValue("glbdef4"))));
				}

				countDays = getRetireCountDays(beginDateOfMonth, endDateOfMonth,
						(laborSetting.getAttributeValue("glbdef14") == null ? beginDateOfMonth
								: ((UFLiteralDate) laborSetting.getAttributeValue("glbdef14"))),
						(laborSetting.getAttributeValue("glbdef15") == null ? beginDateOfMonth
								: ((UFLiteralDate) laborSetting.getAttributeValue("glbdef15"))));

				if (((UFBoolean) laborSetting.getAttributeValue("glbdef11")).booleanValue()) {
					if (laborSetting.getAttributeValue("glbdef13") != null
							&& getDoubleValue(laborSetting.getAttributeValue("glbdef13")).intValue() != 0) {
						countDays = getDoubleValue(laborSetting.getAttributeValue("glbdef13")).intValue();
					}

					if (nhivo.getRetiredays() == null || nhivo.getRetiredays() == 0) {
						// �씵��ջ����tӋ�㮔ǰ�씵
						nhivo.setRetiredays(countDays);
					} else {
						// �씵������Ҳ�����t�ۼӮ�ǰ�씵
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
			}

			if (buildLabor || buildHealth) {
				rtnVOList.add(nhivo);
			}
		}

		return rtnVOList.toArray(new NhiCalcVO[0]);
	}

	private Map<String, PsndocDefVO> getHealthSettings(String psntmptable, UFLiteralDate beginDateOfMonth,
			UFLiteralDate endDateOfMonth) throws BusinessException {
		// �ڱ�Ͷ�������Ͷ����Y
		Map<String, PsndocDefVO> healthSettings = new HashMap<String, PsndocDefVO>();

		Collection setRst = (Collection) this.getBaseDao().retrieveByClause(PsndocDefTableUtil.getPsnHealthClass(),
				"pk_psndoc in (select pk_psndoc from " + psntmptable + ") and dr=0 and glbdef14='Y'");

		if (setRst != null && setRst.size() > 0) {
			for (Object set : setRst) {
				PsndocDefVO vo = (PsndocDefVO) set;

				// ��ǿ �����ʻ�д���ݼ��� 2018-1-19 14:43:05 start
				for (String nos : HEALTHINS) {
					UFDouble acc = new UFDouble(0);
					if (vo.getAttributeValue(nos) != null) {
						acc = (UFDouble) (vo.getAttributeValue(nos) == null ? UFDouble.ZERO_DBL : vo
								.getAttributeValue(nos));
					}
					vo.setAttributeValue(nos, new UFDouble(SalaryDecryptUtil.decrypt(acc.doubleValue())));
				}
				// ��ǿ �����ʻ�д���ݼ��� 2018-1-19 14:43:05 end
				UFLiteralDate dtSetBegin = vo.getBegindate() == null ? new UFLiteralDate("9999-12-31") : vo
						.getBegindate();
				UFLiteralDate dtSetEnd = vo.getEnddate() == null ? new UFLiteralDate("9999-12-31") : vo.getEnddate();
				// �O�����څ^�g�c��ǰ���g�����������دB��������Ч����Ͷ���O��
				if (NhiCalcUtils.isInScope(beginDateOfMonth, endDateOfMonth, dtSetBegin, dtSetEnd, true)) {
					if (StringUtils.isEmpty((String) vo.getAttributeValue("glbdef2"))) {
						throw new BusinessException(
								"�T�� ["
										+ ((PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class, vo.getPk_psndoc()))
												.getCode() + "] �Ľ����O���������g��Ч��δָ���Q�^��ӛ䛣�Ո�a���Y����ԇ");
					}

					if (StringUtils.isEmpty((String) vo.getAttributeValue("glbdef3"))) {
						throw new BusinessException(
								"�T�� ["
										+ ((PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class, vo.getPk_psndoc()))
												.getCode() + "] �Ľ����O���������g��Ч��δָ�������C��̖��ӛ䛣�Ո�a���Y����ԇ");
					}

					if ("����".equals(vo.getAttributeValue("glbdef2"))) {
						if (!healthSettings.containsKey(vo.getPk_psndoc())) {
							healthSettings.put(vo.getPk_psndoc(), vo);
							if (beginDateOfMonth.after(dtSetBegin)) {
								vo.setBegindate(beginDateOfMonth);
							}

							if (endDateOfMonth.before(dtSetEnd)) {
								vo.setEnddate(endDateOfMonth);
							}
							vo.setAttributeValue("glbdef13", UFBoolean.FALSE);// ����Ӌ������
						} else {
							throw new BusinessException(
									"�T�� ["
											+ ((PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class,
													vo.getPk_psndoc())).getCode() + "] �����O�����}");
						}
					}
				}
			}
		} else {
			// throw new BusinessException("δ�ҵ��T������Ͷ���O��");
		}

		return healthSettings;
	}

	private String[] LABORINS = new String[] { "glbdef2", "glbdef3", "glbdef4", "glbdef7" };
	private String[] HEALTHINS = new String[] { "glbdef6", "glbdef7", "glbdef16" };

	private Map<String, PsndocDefVO> getLaborSettings(String psntmptable, UFLiteralDate beginDateOfMonth,
			UFLiteralDate endDateOfMonth, Map<String, UFBoolean> curLaborOutMap, Map<String, UFBoolean> curRetireOutMap)
			throws BusinessException {
		// �ڱ�Ͷ�������Ͷ����Y
		Map<String, PsndocDefVO> laborSettings = new HashMap<String, PsndocDefVO>();
		Collection setRst = (Collection) this.getBaseDao().retrieveByClause(PsndocDefTableUtil.getPsnLaborClass(),
				"pk_psndoc in (select pk_psndoc from " + psntmptable + ") and dr=0 and (glbdef10='Y' or glbdef11='Y')");
		if (setRst != null && setRst.size() > 0) {
			for (Object set : setRst) {
				PsndocDefVO vo = (PsndocDefVO) set;

				// ��ǿ �����ʻ�д���ݼ��� 2018-1-19 14:43:05 start
				for (String nos : LABORINS) {
					UFDouble acc = new UFDouble(0);
					if (vo.getAttributeValue(nos) != null) {
						acc = (UFDouble) (vo.getAttributeValue(nos) == null ? UFDouble.ZERO_DBL : vo
								.getAttributeValue(nos));
					}
					vo.setAttributeValue(nos, new UFDouble(SalaryDecryptUtil.decrypt(acc.doubleValue())));
				}
				// ��ǿ �����ʻ�д���ݼ��� 2018-1-19 14:43:05 end
				UFLiteralDate dtLaborBegin = vo.getBegindate() == null ? new UFLiteralDate("9999-12-31") : vo
						.getBegindate();
				UFLiteralDate dtLaborEnd = vo.getEnddate() == null ? new UFLiteralDate("9999-12-31") : vo.getEnddate();

				UFLiteralDate dtRetireBegin = vo.getAttributeValue("glbdef14") == null ? new UFLiteralDate("9999-12-31")
						: (UFLiteralDate) vo.getAttributeValue("glbdef14");
				UFLiteralDate dtRetireEnd = vo.getAttributeValue("glbdef15") == null ? new UFLiteralDate("9999-12-31")
						: (UFLiteralDate) vo.getAttributeValue("glbdef15");
				// �O�����څ^�g�c��ǰ���g�����������دB��������Ч�ڱ�Ͷ���O��
				if (NhiCalcUtils.isInScope(beginDateOfMonth, endDateOfMonth, dtLaborBegin, dtLaborEnd, false)
						|| NhiCalcUtils.isInScope(beginDateOfMonth, endDateOfMonth, dtRetireBegin, dtRetireEnd, false))
					if (!laborSettings.containsKey(vo.getPk_psndoc())) {
						// Ͷ���ͱ�
						if (UFBoolean.TRUE.equals(vo.getAttributeValue("glbdef10"))) {
							laborSettings.put(vo.getPk_psndoc(), vo);

							// �Ƿ����˱�
							if (endDateOfMonth.after(dtLaborEnd) || endDateOfMonth.isSameDate(dtLaborEnd)) {
								curLaborOutMap.put(vo.getPk_psndoc(), UFBoolean.TRUE);
							} else {
								curLaborOutMap.put(vo.getPk_psndoc(), UFBoolean.FALSE);
							}

							// �����ͱ�����ʹ�õ�ʱ���
							if (beginDateOfMonth.after(dtLaborBegin)) {
								vo.setBegindate(beginDateOfMonth);
								vo.setAttributeValue("REALLABORBEGINDATE", beginDateOfMonth);
							} else {
								vo.setAttributeValue("REALLABORBEGINDATE", dtLaborBegin);
							}

							if (endDateOfMonth.before(dtLaborEnd)) {
								vo.setEnddate(endDateOfMonth);
								vo.setAttributeValue("REALLABORENDDATE", endDateOfMonth);
								curLaborOutMap.put(vo.getPk_psndoc(), UFBoolean.FALSE);
							} else {
								vo.setAttributeValue("REALLABORENDDATE", dtLaborEnd);
							}
						}

						// Ͷ������
						if (UFBoolean.TRUE.equals(vo.getAttributeValue("glbdef11"))) {
							// �Ƿ����˱�
							if (endDateOfMonth.after(dtRetireEnd) || endDateOfMonth.isSameDate(dtRetireEnd)) {
								curRetireOutMap.put(vo.getPk_psndoc(), UFBoolean.TRUE);
							} else {
								curRetireOutMap.put(vo.getPk_psndoc(), UFBoolean.FALSE);
							}

							// �������˼���ʹ�õ�ʱ���
							if (beginDateOfMonth.after(dtRetireBegin)) {
								vo.setAttributeValue("REALRETIREBEGINDATE", beginDateOfMonth);
							} else {
								vo.setAttributeValue("REALRETIREBEGINDATE", dtRetireBegin);
							}

							if (endDateOfMonth.before(dtRetireEnd)) {
								vo.setAttributeValue("REALRETIREENDDATE", endDateOfMonth);
							} else {
								vo.setAttributeValue("REALRETIREENDDATE", dtRetireEnd);
							}

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
						if (NhiCalcUtils.isInScope(lastVO.getBegindate(), lastVO.getEnddate(), dtLaborBegin,
								dtLaborEnd, false)) {
							throw new BusinessException(
									"�T�� ["
											+ ((PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class,
													vo.getPk_psndoc())).getCode() + "] �ڱ������O�����}");
						} else {
							if (UFBoolean.TRUE.equals(vo.getAttributeValue("glbdef10"))) {
								// labor_begindate
								if (lastVO.getBegindate().after(dtLaborBegin)) {
									lastVO.setBegindate(dtLaborBegin);
								}
								// labor_enddate
								if (lastVO.getEnddate().before(dtLaborEnd)) {
									// �Ƿ����˱�
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
									// �Ƿ����˱�
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

							lastVO.setAttributeValue("glbdef6", UFBoolean.FALSE); // ����Ӌ������
						}
					}
			}
		} else {
			// throw new BusinessException("δ�ҵ��T���ڱ�����Ͷ���O��");
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

	public int getLaborCountDays(UFLiteralDate beginDate, UFLiteralDate endDate, UFLiteralDate beginDateOfMonth,
			UFLiteralDate endDateOfMonth) {
		if (beginDate.after(endDate)) {
			return 0;
		}

		// �������
		int calcuBase = -1;

		if (endDate.isSameDate(endDateOfMonth)) {
			// ��������һ���˱����������=����������
			calcuBase = endDateOfMonth.getDay();
			calcuBase = calcuBase == 31 ? 30 : calcuBase;
		} else if (endDate.after(endDateOfMonth)) {
			// δ�ˣ��������=30
			calcuBase = 30;
		} else {
			// ���·�����һ���˱����������=�˱���
			calcuBase = endDate.getDay();
		}

		beginDate = beginDate.before(beginDateOfMonth) ? beginDateOfMonth : beginDate;
		beginDate = beginDate.getDay() == 31 ? beginDate.getDateBefore(1) : beginDate;

		// �ͱ��ӱ����� = ������� - �ӱ��� + 1
		return calcuBase - beginDate.getDay() + 1;
	}

	private int getRetireCountDays(UFLiteralDate beginDateOfMonth, UFLiteralDate endDateOfMonth,
			UFLiteralDate beginDate, UFLiteralDate endDate) {
		if (beginDate.after(endDate)) {
			return 0;
		}

		// �������
		int calcuBase = -1;

		if (endDate.isSameDate(endDateOfMonth)) {
			// ��������һ���˱����������=30
			calcuBase = 30;
		} else if (endDate.after(endDateOfMonth)) {
			// δ�ˣ��������=30
			calcuBase = 30;
		} else {
			// ���·�����һ���˱����������=�˱���
			calcuBase = endDate.getDay();
		}

		beginDate = beginDate.before(beginDateOfMonth) ? beginDateOfMonth : beginDate;
		beginDate = beginDate.getDay() == 31 ? beginDate.getDateBefore(1) : beginDate;

		// �ͱ��ӱ����� = ������� - �ӱ��� + 1
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
		int dbType = this.getBaseDao().getDBType();
		String beginDateOfMonth = "";
		String endDateOfMonth = "";
		String wadocBeginDate = "";
		String wadocEndDate = "";
		String strSQL = "";

		switch (dbType) {
		case 2: // SQL
			beginDateOfMonth = "'" + this.getFirstDayOfMonth(cyear, cperiod).toString() + "'";
			endDateOfMonth = "'" + this.getLastDayOfMonth(cyear, cperiod).toString() + "'";
			wadocBeginDate = "wadoc." + WadocQueryVO.BEGINDATE;
			wadocEndDate = "wadoc." + WadocQueryVO.ENDDATE;

			strSQL += " SELECT  wadoc." + WadocQueryVO.PK_PSNDOC + " ,";
			strSQL += "         case when (" + wadocBeginDate + " <= " + beginDateOfMonth + " AND (" + wadocEndDate
					+ " IS NULL OR " + wadocEndDate + " > " + beginDateOfMonth + ")) then " + beginDateOfMonth
					+ " else " + wadocBeginDate + " end " + WadocQueryVO.BEGINDATE + " , ";
			strSQL += "        case when (" + wadocBeginDate + " < " + endDateOfMonth + " AND (" + wadocEndDate
					+ " >= " + endDateOfMonth + " OR " + wadocEndDate + " IS NULL)) then " + endDateOfMonth + " else "
					+ wadocEndDate + " end " + WadocQueryVO.ENDDATE + " , ";
			strSQL += "         SUM(wadoc." + WadocQueryVO.NMONEY + ") " + WadocQueryVO.NMONEY;
			strSQL += " FROM    hi_psndoc_wadoc wadoc";
			strSQL += "         INNER JOIN wa_item waitem ON wadoc.pk_wa_item = waitem.pk_wa_item";
			strSQL += "                                          AND waitem.dr = 0";
			strSQL += "         INNER JOIN twhr_waitem_30 waitemex ON waitem.pk_wa_item = waitemex.pk_wa_item";
			strSQL += "                                                   AND waitemex.dr = 0";
			// /strSQL += " WHERE   ";
			strSQL += " WHERE   waitemex.isnhiitem_30 = N'Y' AND ";
			strSQL += " " + wadocBeginDate + "<=" + beginDateOfMonth + " AND ";
			strSQL += " (" + wadocEndDate + " IS NULL OR " + wadocEndDate + ">=" + endDateOfMonth + ") ";
			strSQL += "	AND wadoc." + WadocQueryVO.PK_PSNDOC + " IN (SELECT pk_psndoc FROM " + psntmptable + ")";
			strSQL += "         AND wadoc.waflag='Y' AND wadoc.dr = 0";
			strSQL += " GROUP BY wadoc." + WadocQueryVO.PK_PSNDOC + " ,";
			strSQL += "         case when (" + wadocBeginDate + " <= " + beginDateOfMonth + " AND (" + wadocEndDate
					+ " IS NULL OR " + wadocEndDate + " > " + beginDateOfMonth + ")) then " + beginDateOfMonth
					+ " else " + wadocBeginDate + " end , ";
			strSQL += "        case when (" + wadocBeginDate + " < " + endDateOfMonth + " AND (" + wadocEndDate
					+ " >= " + endDateOfMonth + " OR " + wadocEndDate + " IS NULL)) then " + endDateOfMonth + " else "
					+ wadocEndDate + " end ";
			strSQL += " ORDER BY ";
			strSQL += "         case when (" + wadocBeginDate + " <= " + beginDateOfMonth + " AND (" + wadocEndDate
					+ " IS NULL OR " + wadocEndDate + " > " + beginDateOfMonth + ")) then " + beginDateOfMonth
					+ " else " + wadocBeginDate + " end , ";
			strSQL += "        case when (" + wadocBeginDate + " < " + endDateOfMonth + " AND (" + wadocEndDate
					+ " >= " + endDateOfMonth + " OR " + wadocEndDate + " IS NULL)) then " + endDateOfMonth + " else "
					+ wadocEndDate + " end ";
			break;
		case 1: // ORACLE
			beginDateOfMonth = "to_date('" + this.getFirstDayOfMonth(cyear, cperiod).toString()
					+ "', 'YYYY-MM-DD HH24:MI:SS')";
			endDateOfMonth = "to_date('" + this.getLastDayOfMonth(cyear, cperiod).toString()
					+ "', 'YYYY-MM-DD HH24:MI:SS')";
			wadocBeginDate = "to_date(wadoc." + WadocQueryVO.BEGINDATE + ", 'YYYY-MM-DD')";
			wadocEndDate = "to_date(wadoc." + WadocQueryVO.ENDDATE + ", 'YYYY-MM-DD')";

			strSQL += " SELECT  wadoc." + WadocQueryVO.PK_PSNDOC + " ,";
			strSQL += "         case when (" + wadocBeginDate + " <= " + beginDateOfMonth + " AND (" + wadocEndDate
					+ " IS NULL OR " + wadocEndDate + " > " + beginDateOfMonth + ")) then to_char(" + beginDateOfMonth
					+ ", 'YYYY-MM-DD HH24:MI:SS') else to_char(" + wadocBeginDate + ", 'YYYY-MM-DD HH24:MI:SS') end "
					+ WadocQueryVO.BEGINDATE + " , ";
			strSQL += "        case when (" + wadocBeginDate + " < " + endDateOfMonth + " AND (" + wadocEndDate
					+ " >= " + endDateOfMonth + " OR " + wadocEndDate + " IS NULL)) then to_char(" + endDateOfMonth
					+ ", 'YYYY-MM-DD HH24:MI:SS') else to_char(" + wadocEndDate + ", 'YYYY-MM-DD HH24:MI:SS') end "
					+ WadocQueryVO.ENDDATE + " , ";
			strSQL += "         SUM(wadoc." + WadocQueryVO.NMONEY + ") " + WadocQueryVO.NMONEY;
			strSQL += " FROM    hi_psndoc_wadoc wadoc";
			strSQL += "         INNER JOIN wa_item waitem ON wadoc.pk_wa_item = waitem.pk_wa_item";
			strSQL += "                                          AND waitem.dr = 0";
			strSQL += "         INNER JOIN twhr_waitem_30 waitemex ON waitem.pk_wa_item = waitemex.pk_wa_item";
			strSQL += "                                                   AND waitemex.dr = 0";
			// /strSQL += " WHERE   ";
			strSQL += " WHERE   waitemex.isnhiitem_30 = N'Y' AND ";
			strSQL += " " + wadocBeginDate + "<=" + beginDateOfMonth + " AND ";
			strSQL += " (" + wadocEndDate + " IS NULL OR " + wadocEndDate + ">=" + endDateOfMonth + ") ";
			strSQL += "	AND wadoc." + WadocQueryVO.PK_PSNDOC + " IN (SELECT pk_psndoc FROM " + psntmptable + ")";
			strSQL += "         AND wadoc.waflag='Y' AND wadoc.dr = 0";
			strSQL += " GROUP BY wadoc." + WadocQueryVO.PK_PSNDOC + " ,";
			strSQL += "         case when (" + wadocBeginDate + " <= " + beginDateOfMonth + " AND (" + wadocEndDate
					+ " IS NULL OR " + wadocEndDate + " > " + beginDateOfMonth + ")) then to_char(" + beginDateOfMonth
					+ ", 'YYYY-MM-DD HH24:MI:SS') else to_char(" + wadocBeginDate + ", 'YYYY-MM-DD HH24:MI:SS') end , ";
			strSQL += "        case when (" + wadocBeginDate + " < " + endDateOfMonth + " AND (" + wadocEndDate
					+ " >= " + endDateOfMonth + " OR " + wadocEndDate + " IS NULL)) then to_char(" + endDateOfMonth
					+ ", 'YYYY-MM-DD HH24:MI:SS') else to_char(" + wadocEndDate + ", 'YYYY-MM-DD HH24:MI:SS') end ";
			strSQL += " ORDER BY ";
			strSQL += "         case when (" + wadocBeginDate + " <= " + beginDateOfMonth + " AND (" + wadocEndDate
					+ " IS NULL OR " + wadocEndDate + " > " + beginDateOfMonth + ")) then to_char(" + beginDateOfMonth
					+ ", 'YYYY-MM-DD HH24:MI:SS') else to_char(" + wadocBeginDate + ", 'YYYY-MM-DD HH24:MI:SS') end , ";
			strSQL += "        case when (" + wadocBeginDate + " < " + endDateOfMonth + " AND (" + wadocEndDate
					+ " >= " + endDateOfMonth + " OR " + wadocEndDate + " IS NULL)) then to_char(" + endDateOfMonth
					+ ", 'YYYY-MM-DD HH24:MI:SS') else to_char(" + wadocEndDate + ", 'YYYY-MM-DD HH24:MI:SS') end ";
			break;
		}

		WadocQueryVO[] wadocVOs = this.executeQueryVOs(strSQL, WadocQueryVO.class);
		// danqiang3 decrypt the data from the result of querysql 2018-6-1
		// 11:34:15
		for (WadocQueryVO vo : wadocVOs) {
			UFDouble result = new UFDouble(0);
			if (vo.getNmoney() != null) {
				double d = SalaryDecryptUtil.decrypt(vo.getNmoney().doubleValue());
				result = new UFDouble(d);
			}
			vo.setNmoney(result);
		}
		// danqiang3 decrypt the data from the result of querysql 2018-6-1
		// 11:34:15
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
		// �Ķ��{нӋ��Ͷ������н�Y <pk_psndoc, WadocQueryVO[]>
		Map<String, WadocQueryVO[]> adjMap = new HashMap<String, WadocQueryVO[]>();
		WadocQueryVO[] psnAdjData = getPersonNHIInfo(pk_psndoc, pk_org, inDutyDate);
		adjMap.put(pk_psndoc, getPsnAdjData(psnAdjData, pk_psndoc));

		// ���Ȅڽ����c���{н�Y��
		NhiCalcVO[] nhiFinalVOs = compareNhiData(new NhiCalcVO[0], adjMap, Arrays.asList(pk_psndoc), pk_org,
				String.valueOf(inDutyDate.getYear()), String.valueOf(inDutyDate.getMonth()));

		this.setRangeTables(null);
		return nhiFinalVOs;
	}

	private WadocQueryVO[] getPersonNHIInfo(String pk_psndoc, String pk_org, UFLiteralDate inDutyDate)
			throws BusinessException {
		int dbType = this.getBaseDao().getDBType();
		String strSQL = " SELECT pk_psndoc, '" + inDutyDate.toStdString() + "' begindate, null enddate, "
				+ "    SUM(wadoc.nmoney) nmoney " + " FROM " + "     hi_psndoc_wadoc wadoc " + " INNER JOIN "
				+ "     wa_item waitem " + " ON " + "     wadoc.pk_wa_item = waitem.pk_wa_item "
				+ " AND waitem.dr = 0 " + " INNER JOIN " + "     twhr_waitem_30 waitemex " + " ON "
				+ "     waitem.pk_wa_item = waitemex.pk_wa_item " + " AND waitemex.dr = 0 " + " WHERE "
				+ "     waitemex.isnhiitem_30 = N'Y' ";
		switch (dbType) {
		case 2: // SQL:
			strSQL += " AND left(isnull(wadoc.begindate, '9999-12-31'),10) <= '" + inDutyDate.toStdString() + "' ";
			strSQL += " AND left(isnull(wadoc.enddate, '9999-12-31'),10) >='" + inDutyDate.toStdString() + "' ";
			break;
		case 1: // ORACLE
			strSQL += " AND to_date(isnull(wadoc.begindate, '9999-12-31'), 'YYYY-MM-DD') <=to_date('"
					+ inDutyDate.toStdString() + "', 'YYYY-MM-DD HH24:MI:SS') ";
			strSQL += " AND to_date(isnull(wadoc.enddate, '9999-12-31'), 'YYYY-MM-DD') >=to_date('"
					+ inDutyDate.toStdString() + "', 'YYYY-MM-DD HH24:MI:SS') ";
		}
		strSQL += " AND wadoc.pk_psndoc='" + pk_psndoc + "' " + " AND wadoc.waflag='Y' " + " AND wadoc.dr = 0 "
				+ " GROUP BY " + "     wadoc.pk_psndoc ";
		WadocQueryVO[] wadocVOs = this.executeQueryVOs(strSQL, WadocQueryVO.class);

		if (wadocVOs == null || wadocVOs.length == 0) {
			PsndocVO psnvo = (PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class, pk_psndoc);
			throw new BusinessException("�T�� [" + psnvo.getCode() + "] ��Ч�Ķ��{�Y�Ŀ���ܞ�գ��ڽ����ӱ���ͬ�r�M�����l����\r\n"
					+ "  1.  ���{н�Y�Ŀ�Ǆڽ���Ӌ��������\r\n" + "  2. �������ڶ��{�Y�Ŀ�������r�g�����ȣ�\r\n" + "  3. ���{н�Y�Ŀ�l�Ř�ӛ�ѹ��x��");
		}
		// danqiang3 decrypt the data from the result of querysql 2018-6-1
		// 11:34:15
		for (WadocQueryVO vo : wadocVOs) {
			UFDouble result = new UFDouble(0);
			if (vo.getNmoney() != null) {
				double d = SalaryDecryptUtil.decrypt(vo.getNmoney().doubleValue());
				result = new UFDouble(d);
			}
			vo.setNmoney(result);
		}
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

	@Override
	public NhiCalcVO[] getNhiData(String pk_org, List<String> psnList, String cyear, String cperiod)
			throws BusinessException {
		InSQLCreator inSQLCrt = new InSQLCreator();
		String psndocInSQL = "pk_psndoc in (" + inSQLCrt.getInSQL(psnList.toArray(new String[0])) + ") ";

		List<String> orgManaged = LegalOrgUtilsEX.getRelationOrgWithSalary(pk_org);
		String orgInSQL = "legalpersonorg in (" + inSQLCrt.getInSQL(orgManaged.toArray(new String[0])) + ")";

		UFLiteralDate beginDate = new UFLiteralDate(this.getFirstDayOfMonth(cyear, cperiod).toStdString()
				.substring(0, 10));
		UFLiteralDate endDate = new UFLiteralDate(this.getLastDayOfMonth(cyear, cperiod).toStdString().substring(0, 10));

		// ����Աȡ�ͱ������趨�������˹�˾��
		List<PsndocDefVO> laborSettings = getLaborRetireSettings(psndocInSQL, orgInSQL, beginDate, endDate);

		// ����Աȡ�����趨�������˹�˾��
		List<PsndocDefVO> healSettings = getHealSettings(psndocInSQL, orgInSQL, endDate);

		Map<String, NhiCalcVO> nhiCalcMap = new HashMap<String, NhiCalcVO>();
		for (PsndocDefVO vo : laborSettings) {
			// Keyά�ȣ���Ա+���˹�˾+��������+�ͱ�����+���˼���+�����������
			String strKey = vo.getPk_psndoc() + splitter + vo.getAttributeValue("legalpersonorg") + splitter
					+ vo.getAttributeValue("glbdef16") + splitter + vo.getAttributeValue("glbdef4") + splitter
					+ vo.getAttributeValue("glbdef7") + splitter + vo.getAttributeValue("glbdef8");

			if (!nhiCalcMap.containsKey(strKey)) {
				NhiCalcVO nhivo = getNewNhiVO(pk_org, cyear, cperiod, vo);
				nhiCalcMap.put(strKey, nhivo);
			}

			if (((UFBoolean) vo.getAttributeValue("glbdef10")).booleanValue()
					&& NhiCalcUtils.isInScope(beginDate, endDate, vo.getBegindate(), vo.getEnddate(), false)) {
				generateLaborRecord(beginDate, endDate, nhiCalcMap, vo, strKey);
			}

			if (((UFBoolean) vo.getAttributeValue("glbdef11")).booleanValue()
					&& NhiCalcUtils.isInScope(beginDate, endDate, (UFLiteralDate) vo.getAttributeValue("glbdef14"),
							(UFLiteralDate) vo.getAttributeValue("glbdef15"), false)) {
				generateRetireRecord(beginDate, endDate, nhiCalcMap, vo, strKey);
			}
		}

		for (PsndocDefVO vo : healSettings) {
			if (((UFBoolean) vo.getAttributeValue("glbdef14")).booleanValue()
					&& NhiCalcUtils.isInScope(beginDate, endDate, vo.getBegindate(), vo.getEnddate(), true)) {
				generateHealthRecord(pk_org, cyear, cperiod, beginDate, endDate, nhiCalcMap, vo);
			}
		}

		return nhiCalcMap.size() > 0 ? nhiCalcMap.values().toArray(new NhiCalcVO[0]) : new NhiCalcVO[0];
	}

	private void generateHealthRecord(String pk_org, String cyear, String cperiod, UFLiteralDate beginDate,
			UFLiteralDate endDate, Map<String, NhiCalcVO> nhiCalcMap, PsndocDefVO vo) throws BusinessException {
		// Keyά�ȣ���Ա+���˹�˾+��������+�ͱ�����+���˼���+�����������
		String strKey = vo.getPk_psndoc() + splitter + vo.getAttributeValue("legalpersonorg") + splitter + null
				+ splitter + null + splitter + null + splitter + null;

		UFLiteralDate hlBeginDate = vo.getBegindate();
		UFLiteralDate hlEndDate = vo.getEnddate() == null ? new UFLiteralDate("9999-12-31") : vo.getEnddate();

		// ������Ա��������֯���������һ������ͽ��������¼
		NhiCalcVO nhivo = findNhiCalcVO(nhiCalcMap, vo.getPk_psndoc(), (String) vo.getAttributeValue("legalpersonorg"),
				endDate);

		if (nhivo == null) {
			nhivo = getNewNhiVO(pk_org, cyear, cperiod, vo);
			nhiCalcMap.put(strKey, nhivo);
		}
		// ��ʼ����
		nhivo.setBegindate(this.getEarlier(nhivo.getBegindate(), new UFDate(hlBeginDate.toDate()),
				new UFDate(beginDate.toDate())));
		// ��������
		nhivo.setEnddate(this.getLater(nhivo.getEnddate(), new UFDate(hlEndDate.toDate()), new UFDate(endDate.toDate())));
		// ��������
		nhivo.setHealthrange(this.getUFDouble(vo.getAttributeValue("glbdef16")));
		// н�ʱ�׼
		nhivo.setHealthsalary(this.getUFDouble(vo.getAttributeValue("glbdef6")));
		// н�ʱ�׼����
		nhivo.setHealthsalaryextend(this.getUFDouble(vo.getAttributeValue("glbdef7")));
	}

	private void generateRetireRecord(UFLiteralDate beginDate, UFLiteralDate endDate,
			Map<String, NhiCalcVO> nhiCalcMap, PsndocDefVO vo, String strKey) {
		UFLiteralDate rtBeginDate = (UFLiteralDate) vo.getAttributeValue("glbdef14");
		UFLiteralDate rtEndDate = vo.getAttributeValue("glbdef15") == null ? new UFLiteralDate("9999-12-31")
				: (UFLiteralDate) vo.getAttributeValue("glbdef15");
		// ��ʼ����
		nhiCalcMap.get(strKey).setBegindate(
				this.getEarlier(nhiCalcMap.get(strKey).getBegindate(), new UFDate(rtBeginDate.toDate()), new UFDate(
						beginDate.toDate())));
		// ��������
		nhiCalcMap.get(strKey).setEnddate(
				this.getLater(nhiCalcMap.get(strKey).getEnddate(), new UFDate(rtEndDate.toDate()),
						new UFDate(endDate.toDate())));
		// ����Ͷ���������趨���а��趨ֵ��û������ֹʱ�����
		int retireDays = vo.getAttributeValue("retiredays") == null
				|| ((Integer) vo.getAttributeValue("retiredays")) == 0 ? this.getRetireCountDays(beginDate, endDate,
				rtBeginDate, rtEndDate) : (Integer) vo.getAttributeValue("retiredays");
		nhiCalcMap.get(strKey).setRetiredays(nhiCalcMap.get(strKey).getRetiredays() + retireDays);
		// ���˼���
		nhiCalcMap.get(strKey).setRetirerange(this.getUFDouble(vo.getAttributeValue("glbdef7")));
		// н�ʱ�׼
		nhiCalcMap.get(strKey).setLaborsalary(this.getUFDouble(vo.getAttributeValue("glbdef2")));
		// н�ʱ�׼����
		nhiCalcMap.get(strKey).setLaborsalaryextend(this.getUFDouble(vo.getAttributeValue("glbdef3")));
	}

	private void generateLaborRecord(UFLiteralDate beginDate, UFLiteralDate endDate, Map<String, NhiCalcVO> nhiCalcMap,
			PsndocDefVO vo, String strKey) {
		UFLiteralDate lbBeginDate = vo.getBegindate();
		UFLiteralDate lbEndDate = vo.getEnddate() == null ? new UFLiteralDate("9999-12-31") : vo.getEnddate();
		// ��ʼ����
		nhiCalcMap.get(strKey).setBegindate(
				this.getEarlier(nhiCalcMap.get(strKey).getBegindate(), new UFDate(lbBeginDate.toDate()), new UFDate(
						beginDate.toDate())));
		// ��������
		nhiCalcMap.get(strKey).setEnddate(
				this.getLater(nhiCalcMap.get(strKey).getEnddate(), new UFDate(lbEndDate.toDate()),
						new UFDate(endDate.toDate())));
		// �ͱ�Ͷ���������趨���а��趨ֵ��û������ֹʱ�����
		int laborDays = vo.getAttributeValue("labordays") == null || ((Integer) vo.getAttributeValue("labordays")) == 0 ? this
				.getLaborCountDays(lbBeginDate, lbEndDate, beginDate, endDate) : (Integer) vo
				.getAttributeValue("labordays");
		nhiCalcMap.get(strKey).setLabordays(nhiCalcMap.get(strKey).getLabordays() + laborDays);
		// �ͱ�����
		nhiCalcMap.get(strKey).setLaborrange(this.getUFDouble(vo.getAttributeValue("glbdef4")));
		// н�ʱ�׼
		nhiCalcMap.get(strKey).setLaborsalary(this.getUFDouble(vo.getAttributeValue("glbdef2")));
		// н�ʱ�׼����
		nhiCalcMap.get(strKey).setLaborsalaryextend(this.getUFDouble(vo.getAttributeValue("glbdef3")));
	}

	private NhiCalcVO findNhiCalcVO(Map<String, NhiCalcVO> nhiCalcMap, String pk_psndoc, String legalpersonorg,
			UFLiteralDate endDate) throws BusinessException {
		NhiCalcVO ret = null;
		if (StringUtils.isEmpty(legalpersonorg)) {
			PsndocVO psnvo = (PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class, pk_psndoc);
			throw new BusinessException("�T�� [" + psnvo.getCode() + "] ����Ͷ�����˹�˾���ܞ�ա�");
		}
		if (nhiCalcMap != null && nhiCalcMap.size() > 0) {
			for (String strKey : nhiCalcMap.keySet()) {
				// Keyά�ȣ���Ա+���˹�˾+��������+�ͱ�����+���˼���+�����������
				String[] keys = strKey.split(splitter);
				if (pk_psndoc.equals(keys[0]) && legalpersonorg.equals(keys[1])
						&& nhiCalcMap.get(strKey).getEnddate().isSameDate(new UFDate(endDate.toDate()))) {
					ret = nhiCalcMap.get(strKey);
					break;
				}
			}
		}
		return ret;
	}

	private NhiCalcVO getNewNhiVO(String pk_org, String cyear, String cperiod, PsndocDefVO vo) {
		NhiCalcVO nhivo = new NhiCalcVO();
		nhivo.setPk_psndoc(vo.getPk_psndoc());
		nhivo.setPk_corp((String) vo.getAttributeValue("legalpersonorg"));
		nhivo.setLabordays(0);
		nhivo.setRetiredays(0);
		nhivo.setIscalculated(UFBoolean.FALSE);
		nhivo.setIsaudit(UFBoolean.FALSE);
		nhivo.setPk_group(InvocationInfoProxy.getInstance().getGroupId());
		nhivo.setPk_org(pk_org);
		nhivo.setCyear(cyear);
		nhivo.setCperiod(cperiod);
		return nhivo;
	}

	private List<PsndocDefVO> getHealSettings(String psndocInSQL, String orgInSQL, UFLiteralDate endDate)
			throws DAOException, BusinessException {
		List<PsndocDefVO> healSettings = new ArrayList<PsndocDefVO>();
		Collection<?> hlSettings = this.getBaseDao().retrieveByClause(PsndocDefTableUtil.getPsnHealthClass(),
				psndocInSQL + " and isnull(dr,0)=0 and glbdef14='Y' and  glbdef2='����' and " + orgInSQL);
		if (hlSettings != null && hlSettings.size() > 0) {
			for (Object hl : hlSettings) {
				PsndocDefVO vo = (PsndocDefVO) hl;

				// ssx added for decrypt
				// on 2018-11-06
				for (String fn : NhiCalcUtils.getHealthInsEncryptionAttributes()) {
					vo.setAttributeValue(
							fn,
							new UFDouble(SalaryDecryptUtil.decrypt(((UFDouble) vo.getAttributeValue(fn)).doubleValue())));
				}
				// end

				// �Ƿ񽡱�Ͷ��
				if (((UFBoolean) vo.getAttributeValue("glbdef14")).booleanValue()) {
					if (vo.getBegindate() == null) {
						PsndocVO psnvo = (PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class, vo.getPk_psndoc());
						throw new BusinessException("�T�� [" + psnvo.getCode() + "] �����ӱ�ӛ��e�`���oЧ�Ľ����_ʼ�r�g��");
					}
					// �жϽ���Ͷ�������Ƿ������������һ��
					UFLiteralDate hlBeginDate = vo.getBegindate();
					UFLiteralDate hlEndDate = vo.getEnddate() == null ? new UFLiteralDate("9999-12-31") : vo
							.getEnddate();

					if ((hlBeginDate.isSameDate(endDate) || hlBeginDate.before(endDate))
							&& (hlEndDate.isSameDate(endDate) || hlEndDate.after(endDate))) {
						// �ڷ�Χ��
						healSettings.add(vo);
					}
				}
			}
		}
		return healSettings;
	}

	private List<PsndocDefVO> getLaborRetireSettings(String psndocInSQL, String orgInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws DAOException, BusinessException {
		List<PsndocDefVO> laborSettings = new ArrayList<PsndocDefVO>();
		Collection<?> lbSettings = this.getBaseDao().retrieveByClause(PsndocDefTableUtil.getPsnLaborClass(),
				psndocInSQL + " and isnull(dr,0)=0 and (glbdef10='Y' or glbdef11='Y') and " + orgInSQL);
		if (lbSettings != null && lbSettings.size() > 0) {
			for (Object lb : lbSettings) {
				PsndocDefVO vo = (PsndocDefVO) lb;

				// ssx added for decrypt
				// on 2018-11-06
				for (String fn : NhiCalcUtils.getLaborInsEncryptionAttributes()) {
					vo.setAttributeValue(
							fn,
							new UFDouble(SalaryDecryptUtil.decrypt(((UFDouble) vo.getAttributeValue(fn)).doubleValue())));
				}
				// end

				// �Ƿ��ͱ��ӱ�
				if (((UFBoolean) vo.getAttributeValue("glbdef10")).booleanValue()) {
					if (vo.getBegindate() == null) {
						PsndocVO psnvo = (PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class, vo.getPk_psndoc());
						throw new BusinessException("�T�� [" + psnvo.getCode() + "] �ڱ��ӱ�ӛ��e�`���oЧ�Ąڱ��_ʼ�r�g��");
					}
					// �ж��ͱ������Ƿ���ʱ�䷶Χ��
					UFLiteralDate lbBeginDate = vo.getBegindate();
					UFLiteralDate lbEndDate = vo.getEnddate() == null ? new UFLiteralDate("9999-12-31") : vo
							.getEnddate();

					if ((beginDate.isSameDate(lbEndDate) || beginDate.before(lbEndDate))
							&& (endDate.isSameDate(lbBeginDate) || endDate.after(lbBeginDate))) {
						// �ڷ�Χ��
						laborSettings.add(vo);
					}
				}

				// �Ƿ����˼ӱ�
				if (((UFBoolean) vo.getAttributeValue("glbdef11")).booleanValue()) {
					if (vo.getAttributeValue("glbdef14") == null) {
						PsndocVO psnvo = (PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class, vo.getPk_psndoc());
						throw new BusinessException("�T�� [" + psnvo.getCode() + "] ���˼ӱ�ӛ��e�`���oЧ�Ą����_ʼ�r�g��");
					}
					// �ж��ͱ������Ƿ���ʱ�䷶Χ��
					UFLiteralDate rtBeginDate = (UFLiteralDate) vo.getAttributeValue("glbdef14");
					UFLiteralDate rtEndDate = vo.getAttributeValue("glbdef15") == null ? new UFLiteralDate("9999-12-31")
							: (UFLiteralDate) vo.getAttributeValue("glbdef15");

					if ((beginDate.isSameDate(rtEndDate) || beginDate.before(rtEndDate))
							&& (endDate.isSameDate(rtBeginDate) || endDate.after(rtBeginDate))) {
						// �ڷ�Χ��
						if (!laborSettings.contains(vo)) {
							laborSettings.add(vo);
						}
					}
				}
			}
		}
		return laborSettings;
	}
}