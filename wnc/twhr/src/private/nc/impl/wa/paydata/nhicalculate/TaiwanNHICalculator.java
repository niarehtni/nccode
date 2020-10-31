package nc.impl.wa.paydata.nhicalculate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.ml.NCLangResOnserver;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.itf.bd.defdoc.IDefdocQryService;
import nc.itf.bd.defdoc.IDefdoclistQryService;
import nc.itf.org.IOrgConst;
import nc.itf.twhr.INhicalcMaintain;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pubitf.twhr.IAllowancePubQuery;
import nc.pubitf.twhr.IBasedocPubQuery;
import nc.pubitf.twhr.IRangetablePubQuery;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.defdoc.DefdoclistVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.twhr.allowance.AllowanceTypeEnum;
import nc.vo.twhr.allowance.AllowanceVO;
import nc.vo.twhr.basedoc.BaseDocVO;
import nc.vo.twhr.basedoc.DocTypeEnum;
import nc.vo.twhr.nhicalc.EpyfamilyVO;
import nc.vo.twhr.nhicalc.NhiCalcUtils;
import nc.vo.twhr.nhicalc.NhiCalcVO;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;
import nc.vo.twhr.rangetable.RangeLineVO;
import nc.vo.twhr.rangetable.RangeTableAggVO;
import nc.vo.twhr.rangetable.RangeTableTypeEnum;
import nc.vo.twhr.rangetable.RangeTableVO;

import org.apache.commons.lang.StringUtils;

/**
 * 劳健保计算对象
 * 
 * @author SSX
 * @version 6.3.1
 * @since 2014-08-31
 * 
 */
public class TaiwanNHICalculator extends BaseDAOManager {
	// 参数列表 Map<preName_paramCode_pkLegalOrg, paramValue>
	private Map<String, Object> refList;
	// 人员列表
	private List<String> psnList;
	// 与人员对应的法人组织信息
	private List<String> legalOrgListWithPsnListOrder;
	// 本次计算所有的法人组织
	private Set<String> allLegalOrgSet;
	// 行政组织
	private String pk_org;
	// 计算年度
	private String calcYear;
	// 计算月份
	private String calcMonth;
	// 人员信息
	private PsnLaborDataVO[] psnLaborDataVOs;
	// 員工PK臨時表
	private String psnInSQL;
	// 计算上月劳健保员工列表
	private List<String> calcLastMonthPsnList;
	// 级距表
	private List<RangeTableAggVO> rangetables;
	// 当前对象是否上月
	private UFBoolean isLastMonth;
	// 全局组织pk
	private static String PK_GLB_ORG = "GLOBLE00000000000000";

	public final static UFDouble DAY = new UFDouble(30);

	public Map<String, Object> getRefList() {
		return refList;
	}

	public void setRefList(Map<String, Object> refList) {
		this.refList = refList;
	}

	public List<String> getPsnList() {
		return psnList;
	}

	public void setPsnList(List<String> psnList) {
		this.psnList = psnList;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
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

	/**
	 * 健保類型
	 * 
	 * @author ssx
	 */
	private enum HIType {
		/**
		 * 被保險人同月有退保記錄（非當月最後一日），則不計收全月保險費
		 */
		NOT_LAST_OUT,
		/**
		 * 被保險人全月在保，當月最後一天轉出，則計收全月保險費
		 */
		LAST_OUT,
		/**
		 * 被保險人同月有投保（轉入）記錄，無退保，則計收全月保險費
		 */
		NOT_OUT,
		/**
		 * 被保險人同月有投保（轉入）記錄及退保（最後一天轉出）記錄，則計收全月保險費
		 */
		SAME_OUT, OTHER
	}

	private HIType getHITypeByDate(UFDate begindate, UFDate enddate) {
		UFDate firstdate = this.getFirstDayOfMonth(this.getCalcYear(), this.getCalcMonth());
		UFDate lastdate = this.getLastDayOfMonth(this.getCalcYear(), this.getCalcMonth());

		// 1. 被保險人同月有退保記錄（非當月最後一日），則不計收全月保險費
		if (enddate.toUFLiteralDate(ICalendar.BASE_TIMEZONE).before(lastdate.toUFLiteralDate(ICalendar.BASE_TIMEZONE))) {
			return HIType.NOT_LAST_OUT;
		}
		// 2. 被保險人全月在保，當月最後一天轉出，則計收全月保險費
		else if ((begindate.toUFLiteralDate(ICalendar.BASE_TIMEZONE).before(
				firstdate.toUFLiteralDate(ICalendar.BASE_TIMEZONE)) || begindate.toUFLiteralDate(
				ICalendar.BASE_TIMEZONE).isSameDate(firstdate.toUFLiteralDate(ICalendar.BASE_TIMEZONE)))
				&& enddate.toUFLiteralDate(ICalendar.BASE_TIMEZONE).isSameDate(
						lastdate.toUFLiteralDate(ICalendar.BASE_TIMEZONE))) {
			return HIType.LAST_OUT;
		}
		// 3. 被保險人同月有投保（轉入）記錄，無退保，則計收全月保險費
		else if ((begindate.toUFLiteralDate(ICalendar.BASE_TIMEZONE).after(
				firstdate.toUFLiteralDate(ICalendar.BASE_TIMEZONE)) || begindate.toUFLiteralDate(
				ICalendar.BASE_TIMEZONE).isSameDate(firstdate.toUFLiteralDate(ICalendar.BASE_TIMEZONE)))
				&& begindate.toUFLiteralDate(ICalendar.BASE_TIMEZONE).before(
						lastdate.toUFLiteralDate(ICalendar.BASE_TIMEZONE))
				&& enddate.toUFLiteralDate(ICalendar.BASE_TIMEZONE).after(
						lastdate.toUFLiteralDate(ICalendar.BASE_TIMEZONE))) {
			return HIType.NOT_OUT;
		}
		// 4. 被保險人同月有投保（轉入）記錄及退保（最後一天轉出）記錄，則計收全月保險費
		else if ((begindate.toUFLiteralDate(ICalendar.BASE_TIMEZONE).after(
				firstdate.toUFLiteralDate(ICalendar.BASE_TIMEZONE)) || begindate.toUFLiteralDate(
				ICalendar.BASE_TIMEZONE).isSameDate(firstdate.toUFLiteralDate(ICalendar.BASE_TIMEZONE)))
				&& (enddate.toUFLiteralDate(ICalendar.BASE_TIMEZONE).isSameDate(lastdate
						.toUFLiteralDate(ICalendar.BASE_TIMEZONE)))) {
			return HIType.SAME_OUT;
		}

		// 如有其它情況可在此延展
		return HIType.OTHER;
	}

	/**
	 * 初始化劳健保计算基础对象
	 * 
	 * @param pk_org
	 *            计算行政组织
	 * @param year
	 *            计算年度
	 * @param month
	 *            计算月份
	 * @throws BusinessException
	 */
	public TaiwanNHICalculator(String pk_org, String year, String month) throws BusinessException {
		this.setRefList(new HashMap<String, Object>());
		this.setPsnList(new ArrayList<String>());
		this.setPk_org(pk_org);
		this.setCalcYear(year);
		this.setCalcMonth(month);
		this.setIsLastMonth(UFBoolean.FALSE);

		// 通过人员查询本次计算的所有的法人组织
		dealPsnLegalOrg();

		loadBasedocs();
		loadAllowances();
		loadRangeTables();
		loadLaborType();
	}

	/**
	 * 获取人员中的所有法人组织
	 * 
	 * @author Ares.Tank
	 * @date 2018年10月3日 下午5:47:53
	 * @description
	 */
	private void dealPsnLegalOrg() {
		try {
			findPersonList(this.getPk_org(), this.getCalcYear(), this.getCalcMonth()); // 查找需计算劳健保的员工
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}

		// 查询人员列表里的所有法人组织
		for (String pk_legalOrg : legalOrgListWithPsnListOrder) {
			if (null != pk_legalOrg) {
				getAllLegalOrgSet().add(pk_legalOrg);
			}
		}

	}

	private void loadRangeTables() throws BusinessException {
		IRangetablePubQuery qry = (IRangetablePubQuery) NCLocator.getInstance().lookup(IRangetablePubQuery.class);
		RangeTableAggVO[] rangeTables = qry.queryRangetableByType(this.getPk_org(), -1,
				this.getFirstDayOfMonth(this.getCalcYear(), this.getCalcMonth()));
		if (rangeTables == null || rangeTables.length == 0) {
			throw new BusinessException(NCLangResOnserver.getInstance().getStrByID("twhr_paydata",
					"TaiwanNHICalculator-0000")/*
												 * 级距表加载错误 。
												 */);
		} else {
			for (RangeTableAggVO aggvo : rangeTables) {
				this.getRangtables().add(aggvo);
			}
		}
	}

	/**
	 * 初始化劳健保计算基础对象（用于计算上月劳健保）
	 * 
	 * @param pk_org
	 *            计算行政组织
	 * @param year
	 *            计算年度
	 * @param month
	 *            计算月份
	 * @param psnList
	 *            员工列表
	 * @param refList
	 *            Map<pre_paramCode_pkLegalOrg, paramValue> 参数列表
	 */
	public TaiwanNHICalculator(String pk_org, String year, String month, List<String> psnList,
			Map<String, Object> refList) {
		this.setPk_org(pk_org);
		this.setCalcYear(year);
		this.setCalcMonth(month);
		this.setPsnList(psnList);
		this.setRefList(refList);
		this.setIsLastMonth(UFBoolean.TRUE);
	}

	/**
	 * 加载劳保身份、健保身份注记档案
	 * 
	 * @throws BusinessException
	 */
	private void loadLaborType() throws BusinessException {
		IDefdoclistQryService defdoclistSrv = (IDefdoclistQryService) NCLocator.getInstance().lookup(
				IDefdoclistQryService.class);
		DefdoclistVO[] lists = defdoclistSrv.queryDefdoclistVOsByCondition(pk_org,
				"code = 'TWHR006' or code = 'TWHR007' ");

		if (lists == null || lists.length != 2) {
			throw new BusinessException(NCLangResOnserver.getInstance().getStrByID("twhr_paydata",
					"TaiwanNHICalculator-0001")/*
												 * 加载劳保身份及健保身份档案错误 。
												 */);
		}
		IDefdocQryService defdocSrv = (IDefdocQryService) NCLocator.getInstance().lookup(IDefdocQryService.class);
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();

		for (DefdoclistVO listvo : lists) {
			DefdocVO[] defdocVOs = defdocSrv.queryDefdocVOsByDoclistPk(listvo.getPk_defdoclist(), this.getPk_org(),
					pk_group);
			if (defdocVOs == null || defdocVOs.length == 0) {
				throw new BusinessException(NCLangResOnserver.getInstance().getStrByID("twhr_paydata",
						"TaiwanNHICalculator-0002")/*
													 * 加载劳保身份及健保身份档案明细错误 。
													 */);
			}
			for (DefdocVO vo : defdocVOs) {
				this.getRefList().put(getKey(vo, vo.getPk_defdoc(), PK_GLB_ORG), vo.getCode());
			}
		}
	}

	/**
	 * 加载基础参数
	 * 
	 * @throws BusinessException
	 */
	private void loadBasedocs() throws BusinessException {
		IBasedocPubQuery baseQry = (IBasedocPubQuery) NCLocator.getInstance().lookup(IBasedocPubQuery.class.getName());
		for (String pk_legalOrg : getAllLegalOrgSet()) {
			BaseDocVO[] baseVOs = baseQry.queryAllBaseDoc(pk_legalOrg);
			if (baseVOs != null && baseVOs.length > 0) {
				for (BaseDocVO vo : baseVOs) {
					if (!this.getRefList().containsKey(getKey(vo, "code", pk_legalOrg))) {
						Object value = null;
						if (DocTypeEnum.AMOUNT.toIntValue() == vo.getDoctype()) {
							value = vo.getNumbervalue();
						} else if (DocTypeEnum.RATE.toIntValue() == vo.getDoctype()) {
							value = vo.getNumbervalue().div(100);
							// } else if (DocTypeEnum.WAGEITEM.toIntValue() ==
							// vo.getDoctype()) {
							// value = vo.getWaitemvalue();
						} else if (DocTypeEnum.REF.toIntValue() == vo.getDoctype()) {
							value = vo.getRefvalue();
						} else if (DocTypeEnum.TEXT.toIntValue() == vo.getDoctype()) {
							value = vo.getTextvalue();
						} else if (DocTypeEnum.LOGIC.toIntValue() == vo.getDoctype()) {
							value = vo.getLogicvalue();
						}
						this.getRefList().put(getKey(vo, "code", vo.getPk_org()), value);
					}
				}
			}
		}

	}

	private String getKey(SuperVO vo, String propertyName, String pk_legalorg) {
		return vo.getClass().getName()
				+ "_"
				+ ((vo.getAttributeValue(propertyName) == null ? propertyName : vo.getAttributeValue(propertyName))
						+ "_" + pk_legalorg);
	}

	/**
	 * 加载身心残障及投保身份
	 * 
	 * @throws BusinessException
	 */
	private void loadAllowances() throws BusinessException {
		IAllowancePubQuery allowanceQry = (IAllowancePubQuery) NCLocator.getInstance().lookup(
				IAllowancePubQuery.class.getName());

		AllowanceVO[] allowanceVOs = allowanceQry.queryAllowanceList(PK_GLB_ORG);
		if (allowanceVOs != null && allowanceVOs.length > 0) {
			for (AllowanceVO vo : allowanceVOs) {
				if (!this.getRefList().containsKey(getKey(vo, "pk_allowance", PK_GLB_ORG))) {
					this.getRefList().put(getKey(vo, "pk_allowance", PK_GLB_ORG), vo);
				}
			}
		}

	}

	private void findPersonList(String pk_org, String year, String month) throws BusinessException {
		String strSQL = "select pk_psndoc,pk_corp from twhr_nhicalc where pk_org = '" + pk_org
				+ "' and dr = 0 and cyear='" + year + "' and cperiod='" + month
				+ "' AND (iscalculated IS NULL OR iscalculated = 'N')";
		List<Map<String, String>> psnMapList = (List<Map<String, String>>) this.getBaseDao().executeQuery(strSQL,
				new MapListProcessor());
		List<String> psnList = new ArrayList<>();
		List<String> legalOrgListWithPsnListOrder = new ArrayList<>();

		if (null != psnMapList) {
			for (Map<String, String> rowMap : psnMapList) {
				if (null != rowMap && null != rowMap.get("pk_psndoc") && null != rowMap.get("pk_corp")) {
					psnList.add(rowMap.get("pk_psndoc"));
					legalOrgListWithPsnListOrder.add(rowMap.get("pk_corp"));
				}
			}
		}
		this.setPsnList(psnList);
		this.setLegalOrgListWithPsnListOrder(legalOrgListWithPsnListOrder);
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

	public void Calculate() throws BusinessException {
		try {
			// 初始化计算对象
			List<PsnLaborDataVO> laborCurrentMonthData = initCalculateLaborBase();
			// 计算劳保劳退
			laborCurrentMonthData = appendPsnLaborData(laborCurrentMonthData);

			// 根据劳保信息补齐劳健保明细对象列表
			List<PsnLaborDataVO> laborLastMonthData = new ArrayList<PsnLaborDataVO>();// CalculateLastMonth();

			// 计算健保
			PsnHealthDataVO[] healthVOs = CalculateHealthIns(laborCurrentMonthData);

			// 保存勞健保計算結果
			saveNhiData(laborCurrentMonthData, laborLastMonthData, healthVOs);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private void saveNhiData(List<PsnLaborDataVO> laborCurrentMonthData, List<PsnLaborDataVO> laborLastMonthData,
			PsnHealthDataVO[] healthVOs) throws BusinessException {
		SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
		service.setLazyLoad(true);
		String strCondition = "(begindate >= '"
				+ this.getFirstDayOfMonth(this.getCalcYear(), this.getCalcMonth()).toString() + "') AND (enddate <= '"
				+ this.getLastDayOfMonth(this.getCalcYear(), this.getCalcMonth()).toString()
				+ "') AND (iscalculated = 'N') AND (dr=0)";
		// NhiCalcVO[] nhiVOs = service.queryByCondition(NhiCalcVO.class,
		// strCondition);
		List<NhiCalcVO> nhivoList = (List<NhiCalcVO>) getBaseDao().retrieveByClause(NhiCalcVO.class, strCondition);
		NhiCalcVO[] nhiVOExists = nhivoList.toArray(new NhiCalcVO[0]);
		NhiCalcVO[] savedVOs = this.getLaborSaveVOs(laborCurrentMonthData, laborLastMonthData, nhiVOExists);
		savedVOs = this.getHealthSaveVOs(healthVOs, savedVOs, nhiVOExists);

		for (NhiCalcVO vo : savedVOs) {
			vo.setIscalculated(UFBoolean.TRUE);
			vo.setIsaudit(UFBoolean.FALSE);
		}

		INhicalcMaintain nhiSrv = NCLocator.getInstance().lookup(INhicalcMaintain.class);
		nhiSrv.updateNhiCalcVO(savedVOs);
	}

	private NhiCalcVO[] getHealthSaveVOs(PsnHealthDataVO[] healthVOs, NhiCalcVO[] nhiVOWithLabors,
			NhiCalcVO[] nhiVOExists) {
		List<NhiCalcVO> nhiCalcVOList = new ArrayList<NhiCalcVO>();
		// 健保
		if (healthVOs != null && healthVOs.length > 0) {
			for (PsnHealthDataVO vo : healthVOs) {
				NhiCalcVO[] newVO = getNhiCalcVOByPsn(nhiVOWithLabors, vo.getPk_psndoc(), true);
				if (newVO.length == 0) {
					newVO = getNhiCalcVOByPsn(nhiVOExists, vo.getPk_psndoc(), true);
				}
				if (newVO.length > 0) {
					newVO[0].setHealthstuff(vo.getHealthAmount_Psn() == null ? UFDouble.ZERO_DBL : vo
							.getHealthAmount_Psn()); // 個人承擔
					newVO[0].setHealthhirer(vo.getHealthAmount_Org() == null ? UFDouble.ZERO_DBL : vo
							.getHealthAmount_Org()); // 僱主承擔
					newVO[0].setHealthgov(vo.getGovnHelpAmount() == null ? UFDouble.ZERO_DBL : vo.getGovnHelpAmount()); // 政府承擔
					newVO[0].setHealthstuffact(vo.getHealthAmount() == null ? UFDouble.ZERO_DBL : vo.getHealthAmount()); // 個人實際承擔
					newVO[0].setDependentcount(vo.getPersonCount() == null ? 0 : vo.getPersonCount().intValue()); // 眷屬人數
					newVO[0].setHealthcommitamount(vo.getHealthAmount_Family() == null ? UFDouble.ZERO_DBL : vo
							.getHealthAmount_Family()); // 眷属承担
					newVO[0].setHealthpersamount(newVO[0].getHealthstuff().add(newVO[0].getHealthcommitamount()));
					// if (newVO[0].getIncludelastmonth().booleanValue()) {
					// newVO[0].setLastmonthhealthgov(newVO[0].getHealthgov());
					// newVO[0].setLastmonthhealthhirer(newVO[0].getHealthhirer());
					// newVO[0].setLastmonthhealthstuff(newVO[0].getHealthstuff());
					// newVO[0].setLastmonthhealthstuffact(newVO[0].getHealthstuffact());
					// }
					nhiCalcVOList.add(newVO[0]);
				}
			}
		}

		List<NhiCalcVO> appendVOs = new ArrayList<NhiCalcVO>();

		dealNoHealthVO(nhiVOWithLabors, nhiCalcVOList, appendVOs);
		dealNoHealthVO(nhiVOExists, nhiCalcVOList, appendVOs);

		if (appendVOs.size() > 0) {
			nhiCalcVOList.addAll(appendVOs);
		}
		return nhiCalcVOList.toArray(new NhiCalcVO[0]);
	}

	private void dealNoHealthVO(NhiCalcVO[] nhiVOWithLabors, List<NhiCalcVO> nhiCalcVOList, List<NhiCalcVO> appendVOs) {
		// 補齊有勞保無健保的人員
		for (NhiCalcVO nhivo : nhiVOWithLabors) {
			boolean contains = false;
			for (NhiCalcVO healthvo : nhiCalcVOList) {
				if (nhivo.getPk_nhicalc().equals(healthvo.getPk_nhicalc())) {
					contains = true;
					break;
				}
			}
			if (!contains) {
				nhivo.setHealthstuff(UFDouble.ZERO_DBL); // 個人承擔
				nhivo.setHealthhirer(UFDouble.ZERO_DBL); // 僱主承擔
				nhivo.setHealthgov(UFDouble.ZERO_DBL); // 政府承擔
				nhivo.setHealthstuffact(UFDouble.ZERO_DBL); // 個人實際承擔
				nhivo.setDependentcount(0); // 眷屬人數

				nhivo.setLastmonthhealthgov(UFDouble.ZERO_DBL);
				nhivo.setLastmonthhealthhirer(UFDouble.ZERO_DBL);
				nhivo.setLastmonthhealthstuff(UFDouble.ZERO_DBL);
				nhivo.setLastmonthhealthstuffact(UFDouble.ZERO_DBL);

				nhivo.setIsaudit(UFBoolean.FALSE);
				nhivo.setIscalculated(UFBoolean.TRUE);
				appendVOs.add(nhivo);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private PsnHealthDataVO[] CalculateHealthIns(List<PsnLaborDataVO> laborData) throws BusinessException {
		String strSQL = "";
		strSQL += " SELECT         DISTINCT def.pk_psndoc_sub,  nhi.pk_psndoc, nhi.begindate, nhi.enddate, def.glbdef5 AS psntype, ISNULL(nhi.healthrange, 0) AS healthrange, ";
		strSQL += "                             def.glbdef8 AS gvmhelp1, def.glbdef9 AS gvmhelp2, def.glbdef10 AS gvmhelp3, def.glbdef2  as relation,";
		strSQL += "                             def.glbdef11 AS gvmhelp4, def.glbdef12 AS gvmhelp5, isnull(def.glbdef13, 'N') AS iscontainlastmonth, ";
		strSQL += "                             isnull(def.glbdef15, 'N') AS istaxfeed, nhi.pk_corp, nhi.pk_nhicalc, def.pk_psndoc_sub, def.glbdef1 name, nhi.pk_org,";
		strSQL += "                            def.begindate AS healthbegindate, isnull(def.enddate, '9999-12-31') AS healthenddate, psndoc.id AS psndocid, def.glbdef3 AS healthid, psndoc.code AS code ";
		strSQL += " FROM              twhr_nhicalc AS nhi INNER JOIN ";
		strSQL += "                             bd_psndoc AS psndoc ON nhi.pk_psndoc = psndoc.pk_psndoc LEFT OUTER JOIN ";
		strSQL += PsndocDefTableUtil.getPsnHealthTablename()
				+ " AS def ON def.pk_psndoc = nhi.pk_psndoc AND nhi.begindate <= isnull(def.enddate, '9999-12-31') AND ";
		strSQL += "                             nhi.enddate >= def.begindate ";
		strSQL += " WHERE          (nhi.begindate >= '"
				+ this.getFirstDayOfMonth(this.getCalcYear(), this.getCalcMonth()).toString()
				+ "') AND (nhi.enddate <= '"
				+ this.getLastDayOfMonth(this.getCalcYear(), this.getCalcMonth()).toString()
				+ "') AND (def.glbdef14 IS NOT NULL AND def.glbdef14 = N'Y') AND ";
		strSQL += " (nhi.dr=0) AND (nhi.pk_psndoc in (" + this.getPsnInSQL() + ")) AND ISNULL(nhi.healthrange, 0) != 0";

		List<Map> healthData = (List<Map>) this.getBaseDao().executeQuery(strSQL, new MapListProcessor());
		return generateHealthVOs(healthData, laborData);
	}

	private PsnHealthDataVO[] generateHealthVOs(List<Map> healthData, List<PsnLaborDataVO> laborData)
			throws BusinessException {
		List<PsnHealthDataVO> healthDataList = new ArrayList<PsnHealthDataVO>();
		for (String pk_psndoc : this.getPsnList()) {
			List<Map> healDataByPsn = getMapDataByPsn(healthData, pk_psndoc);
			PsnHealthDataVO vo = calculateHealthDataByPsndoc(healDataByPsn, pk_psndoc, laborData);
			if (vo != null) {
				healthDataList.add(vo);
			}
		}
		return healthDataList.toArray(new PsnHealthDataVO[0]);
	}

	private boolean ifHealthByDate(Map healDataByPsnSub) {
		// boolean ifHeal = false;
		if (healDataByPsnSub != null && healDataByPsnSub.size() > 0) {

			UFDate begindate = UFDate.getDate((String) healDataByPsnSub.get("healthbegindate"));
			UFDate enddate = UFDate.getDate((String) healDataByPsnSub.get("healthenddate"));

			HIType hiType = this.getHITypeByDate(begindate, enddate);

			// 1. 被保險人全月在保，當月最後一天轉出，則計收全月保險費
			if (hiType.equals(HIType.LAST_OUT)) {
				return true;
			}
			// 2. 被保險人同月有投保（轉入）記錄，無退保，則計收全月保險費
			else if (hiType.equals(HIType.NOT_OUT)) {
				return true;
			}
			// 3. 被保險人同月有退保記錄（非當月最後一日），無投保（轉入）記錄，則不計收全月保險費
			else if (hiType.equals(HIType.NOT_LAST_OUT)) {
				return false;
			}
			// 4. 被保險人同月有投保（轉入）記錄及退保（轉出）記錄，則計收全月保險費
			else if (hiType.equals(HIType.SAME_OUT)) {
				return true;
			}
			// 如有其它情況可在此延展
			else if (hiType.equals(HIType.OTHER)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param healDataByPsn
	 * @param pk_psndoc
	 * @param laborData
	 * @return
	 * @throws BusinessException
	 */
	private PsnHealthDataVO calculateHealthDataByPsndoc(List<Map> healDataByPsn, String pk_psndoc,
			List<PsnLaborDataVO> laborData) throws BusinessException {
		PsnHealthDataVO healVO = new PsnHealthDataVO();
		Map<String, UFDouble> nhiHelpAmountMap = new HashMap<String, UFDouble>();
		Map<String, String[]> nhiHelpAllowranceMap = new HashMap<String, String[]>();

		if (healDataByPsn != null && healDataByPsn.size() > 0) {
			healVO.setPk_psndoc(pk_psndoc);
			healVO.setPersonCount(UFDouble.ZERO_DBL);

			UFDate begindate = null;
			UFDate enddate = null;
			for (Map healmap : healDataByPsn) {
				if (healmap.get("healthid").equals(healmap.get("psndocid"))) {
					begindate = UFDate.getDate((String) healmap.get("healthbegindate"));
					enddate = UFDate.getDate((String) healmap.get("healthenddate"));
					break;
				}
			}

			if (begindate != null && enddate != null
					&& !HIType.NOT_LAST_OUT.equals(getHITypeByDate(begindate, enddate))) {

				Map<String, UFDouble[]> idHelps = getIDHelps(healDataByPsn);
				Map<String, UFDouble[]> maxThreeHelps = getMaxThreeHelps((String) healDataByPsn.get(0).get("psndocid"),
						(String) healDataByPsn.get(0).get("code"), idHelps);

				for (Map data : healDataByPsn) {
					// 人员类型
					String psnType = (String) data.get("psntype");
					// 投保身份证号
					String healthid = (String) data.get("healthid");
					if (ifHealthByDate(data)) {
						if (!((String) data.get("psndocid")).equals(healthid)) {
							// 非投保人本人
							healVO.setPersonCount(healVO.getPersonCount().add(1));
						} else {
							// 雇主
							if ("HT02".equals(this.getRefList().get(getKey(new DefdocVO(), psnType, PK_GLB_ORG)))) {
								healVO.setHealthAmount_Org(UFDouble.ZERO_DBL);
							}
							// 低收入戶
							else if ("HT03".equals(this.getRefList().get(getKey(new DefdocVO(), psnType, PK_GLB_ORG)))) {
								healVO.setHealthAmount(UFDouble.ZERO_DBL);
								healVO.setHealthAmount_Org(UFDouble.ZERO_DBL);
								healVO.setHealthAmount_Psn(UFDouble.ZERO_DBL);
							}
							// 研替
							else if ("HT05".equals(this.getRefList().get(getKey(new DefdocVO(), psnType, PK_GLB_ORG)))) {
								healVO.setPersonCount(UFDouble.ZERO_DBL);
								healVO.setHealthAmount(UFDouble.ZERO_DBL);
								healVO.setHealthAmount_Org(UFDouble.ZERO_DBL);
								healVO.setHealthAmount_Psn(UFDouble.ZERO_DBL);
								break;
							}
							// 留停
							else if ("HT07".equals(this.getRefList().get(getKey(new DefdocVO(), psnType, PK_GLB_ORG)))) {
								healVO.setHealthAmount(UFDouble.ZERO_DBL);
								healVO.setHealthAmount_Org(UFDouble.ZERO_DBL);
								healVO.setHealthAmount_Psn(UFDouble.ZERO_DBL);
								break;
							}
						}

						healVO.setIsContainLastMonth(new UFBoolean((String) data.get("iscontainlastmonth")));
						// 健保身份为雇主时，无政府补助
						UFDouble helpRate = new UFDouble(1);
						if (isEmployer(psnType, pk_psndoc, laborData)) {
							helpRate = UFDouble.ZERO_DBL;
						}

						// 比例类补助
						UFDouble rateHelp = UFDouble.ZERO_DBL;
						// 金额类补助
						UFDouble amountHelp = UFDouble.ZERO_DBL;

						if (((String) data.get("psndocid")).equals(healthid) || idHelps.size() <= 4) {
							// 員工本人或投保眷屬不超過3人
							rateHelp = idHelps.get(healthid)[0];
							amountHelp = idHelps.get(healthid)[1];
						} else {
							// 投保超過3個眷屬時
							if (maxThreeHelps.containsKey(healthid)) {
								// 當前眷屬在補助前三
								rateHelp = maxThreeHelps.get(healthid)[0];
								amountHelp = maxThreeHelps.get(healthid)[1];
							} else {
								// 當前眷屬未在補助前三
								rateHelp = new UFDouble(1);
							}
						}

						RangeLineVO line = this.findRangeLineByRangeRate(RangeTableTypeEnum.NHI_RANGETABLE,
								(new UFDouble(getDoubleValue(data.get("healthrange")))), null);
						if (line != null) {
							if (isEmployer(psnType, pk_psndoc, laborData)) {
								healVO.setHealthAmount_Org(UFDouble.ZERO_DBL);
							} else {
								healVO.setHealthAmount_Org(line.getEmployeramount().setScale(0, UFDouble.ROUND_HALF_UP));
							}
							// 政府补助金额
							UFDouble gvmHelpAmount = amountHelp.add(
									line.getEmployeeamount().multiply(rateHelp).setScale(0, UFDouble.ROUND_UP))
									.multiply(helpRate);
							// 补助金额超过应付健保费
							if (gvmHelpAmount.longValue() >= line.getEmployeeamount().longValue()) {
								// 应付健保费=政府补助金额
								healVO.setHealthAmount(healVO.getHealthAmount().add(gvmHelpAmount)
										.setScale(0, UFDouble.ROUND_HALF_UP));
								// 应付健保费个人承担部分为0
								healVO.setHealthAmount_Psn(healVO.getHealthAmount_Psn().add(UFDouble.ZERO_DBL)
										.setScale(0, UFDouble.ROUND_HALF_UP));

								// 不是本人時，健保費為0
								if (!((String) data.get("psndocid")).equals(healthid)) {
									dealFamilyData(nhiHelpAmountMap, nhiHelpAllowranceMap, healVO, data, healthid,
											line.getEmployeeamount(), line.getEmployeeamount());
								} else {
									if (!nhiHelpAllowranceMap.containsKey((String) data.get("psndocid"))) {
										nhiHelpAllowranceMap.put((String) data.get("psndocid"), new String[] {
												(String) data.get("gvmhelp1"), (String) data.get("gvmhelp2") });
									}
								}
							} else {
								if (isEmployer(psnType, pk_psndoc, laborData)) {
									// 雇主完全个人承担，没有公司承担部分
									// 即应将公司承担部分加到个人承担
									// 應付保費
									UFDouble rangeLevel = line.getRangeupper();
									UFDouble empRate = (UFDouble) this.getRefList().get(
											getKey(new BaseDocVO(), "TWNP0001", (String) data.get("pk_org")));
									UFDouble empAmount = rangeLevel.multiply(empRate).setScale(0,
											UFDouble.ROUND_HALF_UP);
									healVO.setHealthAmount(healVO.getHealthAmount().add(empAmount)
											.setScale(0, UFDouble.ROUND_HALF_UP));
									// 应付健保费个人承担部分
									healVO.setHealthAmount_Psn(healVO.getHealthAmount_Psn().add(empAmount)
											.setScale(0, UFDouble.ROUND_HALF_UP));

									// 不是本人時，健保費為個人承擔部分
									if (!((String) data.get("psndocid")).equals(healthid)) {
										dealFamilyData(nhiHelpAmountMap, nhiHelpAllowranceMap, healVO, data, healthid,
												empAmount, UFDouble.ZERO_DBL);
									} else {
										if (!nhiHelpAllowranceMap.containsKey((String) data.get("psndocid"))) {
											nhiHelpAllowranceMap.put((String) data.get("psndocid"), new String[] {
													(String) data.get("gvmhelp1"), (String) data.get("gvmhelp2") });
										}
									}
								} else {
									// 應付保費
									healVO.setHealthAmount(healVO.getHealthAmount().add(line.getEmployeeamount())
											.setScale(0, UFDouble.ROUND_HALF_UP));

									// 应付健保费个人承担部分
									// mod 个人健保费不算眷属 tank 2019年5月30日11:20:05
									// 勞健保拆分問題 by George 20200514 缺陷Bug #35078
									// 從本地化合併代碼過來WNC，個人健保費算到眷屬的，應區分
									if (((String) data.get("psndocid")).equals(healthid)) {
										healVO.setHealthAmount_Psn(healVO.getHealthAmount_Psn()
												.add(line.getEmployeeamount().sub(gvmHelpAmount))
												.setScale(0, UFDouble.ROUND_HALF_UP));
									}

									// 不是本人時，健保費為應付保費
									if (!((String) data.get("psndocid")).equals(healthid)) {
										dealFamilyData(nhiHelpAmountMap, nhiHelpAllowranceMap, healVO, data, healthid,
												line.getEmployeeamount(), gvmHelpAmount);
									} else {
										if (!nhiHelpAllowranceMap.containsKey((String) data.get("psndocid"))) {
											nhiHelpAllowranceMap.put((String) data.get("psndocid"), new String[] {
													(String) data.get("gvmhelp1"), (String) data.get("gvmhelp2") });
										}
									}
								}
							}
						}
					}
				}

				if (healVO.getPersonCount().intValue() > 0) {
					if (healVO.getHealthRange() != null) {
						healVO.setPersonCount(healVO.getPersonCount());
					}
				}

				saveFamilyVOs(healDataByPsn, idHelps, maxThreeHelps, nhiHelpAmountMap, nhiHelpAllowranceMap);
			}
		}

		return healVO;
	}

	/**
	 * 处理眷属数据
	 * 
	 * @param nhiHelpAmountMap
	 *            计算主表与补助金额对应关系
	 * @param nhiHelpAllowranceMap
	 *            计算主表与补助类项目对应关系
	 * @param healVO
	 *            健保计算对象
	 * @param data
	 *            数据库对象
	 * @param healthid
	 *            投保人身份证号码
	 * @param healthAmount
	 *            投保金额
	 * @param gvmHelpAmount
	 *            政府补助金额
	 */
	private void dealFamilyData(Map<String, UFDouble> nhiHelpAmountMap, Map<String, String[]> nhiHelpAllowranceMap,
			PsnHealthDataVO healVO, Map data, String healthid, UFDouble healthAmount, UFDouble gvmHelpAmount) {
		if (ifHealthByDate(data)) {
			if (!nhiHelpAmountMap.containsKey(healthid)) {
				nhiHelpAmountMap.put(healthid, healthAmount.sub(gvmHelpAmount));
			}
			nhiHelpAmountMap.put("healthid", nhiHelpAmountMap.get(healthid).add(healthAmount));

			if (!nhiHelpAllowranceMap.containsKey(healthid)) {
				nhiHelpAllowranceMap.put(healthid,
						new String[] { (String) data.get("gvmhelp1"), (String) data.get("gvmhelp2") });
			}
			healVO.setHealthAmount_Family(healVO.getHealthAmount_Family().add(healthAmount.sub(gvmHelpAmount)));
			healVO.setGovnHelpAmount(healVO.getGovnHelpAmount().add(gvmHelpAmount));
		}
	}

	List<String> insertedSubPK = new ArrayList<String>();

	@SuppressWarnings("unchecked")
	private void saveFamilyVOs(List<Map> healDataByPsn, Map<String, UFDouble[]> idHelps,
			Map<String, UFDouble[]> maxThreeHelps, Map<String, UFDouble> nhiHelpAmountMap,
			Map<String, String[]> nhiHelpAllowranceMap) throws BusinessException {

		Map<String, String> subHealidMap = new HashMap<String, String>();

		if (nhiHelpAmountMap.size() > 0) {
			if (idHelps.size() < 4) {
				maxThreeHelps = idHelps;
			}
			List<EpyfamilyVO> insList = new ArrayList<EpyfamilyVO>();
			for (Entry<String, UFDouble[]> help : maxThreeHelps.entrySet()) {
				for (Map<String, Object> healdata : healDataByPsn) {
					if (!insertedSubPK.contains((String) healdata.get("pk_psndoc_sub"))) {
						String healid = (String) healdata.get("healthid");

						// 勞健保拆分問題 by George 20200515 缺陷Bug #35078
						// 條件要過濾"本人"，計算勞健保底下不應有稱謂="本人"
						if (help.getKey().equals(healid) && !"本人".equals(healdata.get("relation"))) {
							insertedSubPK.add((String) healdata.get("pk_psndoc_sub"));
							String pk_nhicalc = (String) healdata.get("pk_nhicalc");
							EpyfamilyVO newvo = new EpyfamilyVO();
							newvo.setPk_nhicalc(pk_nhicalc);
							newvo.setPk_psndoc((String) healdata.get("pk_psndoc"));
							newvo.setPk_psndoc_sub((String) healdata.get("pk_psndoc_sub"));
							newvo.setName((String) healdata.get("name"));
							newvo.setSub_identity1(nhiHelpAllowranceMap.get(healid)[0]);
							newvo.setSub_identity2(nhiHelpAllowranceMap.get(healid)[1]);
							if (null != healdata.get("healthid") && null != healdata.get("psndocid")
									&& healdata.get("healthid").equals(healdata.get("psndocid"))) {
								newvo.setHealthamount(nhiHelpAmountMap.get("healthid"));
							} else {
								newvo.setHealthamount(nhiHelpAmountMap.get(healid));
							}
							newvo.setAppellation((String) healdata.get("relation"));
							subHealidMap.put(newvo.getPk_psndoc_sub(), newvo.getSub_identity1());
							insList.add(newvo);
						}
					}
				}
			}
			if (insList.size() > 0) {
				String[] pks = this.getBaseDao().insertVOList(insList);
				for (String pk : pks) {
					EpyfamilyVO vo = (EpyfamilyVO) this.getBaseDao().retrieveByPK(EpyfamilyVO.class, pk);
					for (Map<String, Object> healdata : healDataByPsn) {
						if (vo.getPk_psndoc_sub().equals(healdata.get("pk_psndoc_sub"))) {
							this.getBaseDao().executeUpdate(
									"update twhr_nhicalc_family set code = '"
											+ subHealidMap.get(healdata.get("pk_psndoc_sub"))
											+ "' where pk_psndoc_sub='" + healdata.get("pk_psndoc_sub")
											+ "' and pk_nhicalc='" + healdata.get("pk_nhicalc") + "'");
						}
					}
				}
			}
		}
	}

	private Map<String, UFDouble[]> getMaxThreeHelps(String psndocid, String psncode, Map<String, UFDouble[]> idHelps)
			throws BusinessException {
		// 排序：第一個固定為員工本人，後面按眷屬補助排序
		Map<String, UFDouble[]> tmpHelps = new HashMap<String, UFDouble[]>();

		if (idHelps.get(psndocid) == null) {
			throw new BusinessException(ResHelper.getString("twhr", "NhicalcMaintainImpl-0004", psncode)); // 未找到員工本人的健保投保設定資訊。
		}

		tmpHelps.put(psndocid, idHelps.get(psndocid));
		idHelps.remove(psndocid);

		if (idHelps.size() < 3) {
			tmpHelps.putAll(idHelps);
		} else {
			while (tmpHelps.size() <= idHelps.size() && tmpHelps.size() < 4) {
				String maxID = "";
				UFDouble maxNumber = UFDouble.ZERO_DBL;
				// 遍歷所有眷屬，找到補助量的
				for (Map.Entry<String, UFDouble[]> entry : idHelps.entrySet()) {
					if (!tmpHelps.containsKey(entry.getKey())) {
						if ((entry.getValue()[0].add(entry.getValue()[1])).getDouble() >= maxNumber.getDouble()) {
							maxID = entry.getKey();
							maxNumber = entry.getValue()[0].add(entry.getValue()[1]);
						}
					}
				}

				if (maxID == "") {
					break;
				}
				tmpHelps.put(maxID, idHelps.get(maxID));
			}
		}
		idHelps.put(psndocid, tmpHelps.get(psndocid));
		tmpHelps.remove(psndocid);

		return tmpHelps;

	}

	private Map<String, UFDouble[]> getIDHelps(List<Map> healDataByPsn) throws BusinessException {
		Map<String, UFDouble[]> idHelps = new HashMap<String, UFDouble[]>();
		String psndocid = "";
		for (Map data : healDataByPsn) {
			if (ifHealthByDate(data)) {
				psndocid = (String) data.get("psndocid");
				// 比例类补助
				UFDouble rateHelp = UFDouble.ZERO_DBL;
				// 金额类补助
				UFDouble amountHelp = UFDouble.ZERO_DBL;

				for (int i = 1; i < 6; i++) {
					String helpid = (String) data.get("gvmhelp" + String.valueOf(i));
					if (!StringUtils.isEmpty(helpid)) {
						AllowanceVO helpVO = (AllowanceVO) this.getRefList().get(
								getKey(new AllowanceVO(), helpid, IOrgConst.GLOBEORG));
						if (AllowanceTypeEnum.AMOUNT.toIntValue() == helpVO.getAllowancetype()) {
							amountHelp = amountHelp.add(helpVO.getAllowanceamount());
						} else if (AllowanceTypeEnum.RATE.toIntValue() == helpVO.getAllowancetype()) {
							rateHelp = rateHelp.add(helpVO.getAllowanceamount().div(100));
						}
					}
				}

				if (data.get("healthid") == null) {
					throw new BusinessException(NCLangResOnserver.getInstance().getStrByID("twhr_paydata",
							"TaiwanNHICalculator-0003")/*
														 * 员工 [
														 */
							+ (String) data.get("code")
							+ NCLangResOnserver.getInstance().getStrByID("twhr_paydata", "TaiwanNHICalculator-0006")/*
																													 * ]
																													 * 健保投保資訊中存在未填寫身份證號碼的設定
																													 * ，
																													 * 請在員工資訊維護中補全
																													 * 。
																													 */);
				}
				idHelps.put((String) data.get("healthid"), new UFDouble[] { rateHelp, amountHelp });
			}
		}

		return idHelps;
	}

	private boolean isEmployer(String psnType, String pk_psndoc, List<PsnLaborDataVO> laborData) {
		if (laborData != null && laborData.size() > 0) {
			for (PsnLaborDataVO vo : laborData) {
				if (pk_psndoc.equals(vo.getPk_psndoc())) {
					if ("LT01".equals(this.getRefList().get(getKey(new DefdocVO(), vo.getNhiPsnType(), PK_GLB_ORG)))) {
						if ("HT02".equals(this.getRefList().get(getKey(new DefdocVO(), psnType, PK_GLB_ORG)))) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public List<PsnLaborDataVO> initCalculateLaborBase() throws BusinessException {
		if (!this.getIsLastMonth().booleanValue()) {
			// 创建员工PK临时表
			InSQLCreator insql = new InSQLCreator();
			this.setPsnInSQL(insql.getInSQL(this.getPsnList().toArray(new String[0])));
		}
		List<PsnLaborDataVO> laborData = initPsnLaborDataVO();// 初始化劳健保明细对象列表
		return laborData;
	}

	public List<PsnLaborDataVO> CalculateLastMonth() throws BusinessException {
		UFDate lastMonth = this.getFirstDayOfMonth(this.getCalcYear(), this.getCalcMonth()).getDateBefore(1);
		TaiwanNHICalculator lastMonthCalculator = new TaiwanNHICalculator(this.getPk_org(), String.valueOf(lastMonth
				.getYear()), String.valueOf(lastMonth.getMonth()), this.getCalcLastMonthPsnList(), this.getRefList());

		List<PsnLaborDataVO> lastMonthLaborData = lastMonthCalculator.initCalculateLaborBase();
		// 刷新级距
		// lastMonthCalculator.refreshRange(this.getStrTempTableName());
		return lastMonthCalculator.appendPsnLaborData(lastMonthLaborData);// 根据劳保信息补齐劳健保明细对象列表
	}

	private NhiCalcVO[] getLaborSaveVOs(List<PsnLaborDataVO> laborThisMonthData,
			List<PsnLaborDataVO> laborLastMonthData, NhiCalcVO[] nhiVOExists) throws BusinessException {
		List<NhiCalcVO> nhiCalcVOList = new ArrayList<NhiCalcVO>();
		// 上月劳保劳退
		if (laborLastMonthData != null && laborLastMonthData.size() > 0) {
			for (PsnLaborDataVO vo : laborLastMonthData) {
				NhiCalcVO[] newVO = getNhiCalcVOByPsn(nhiVOExists, vo.getPk_nhicalc(), false);
				if (newVO.length > 0) {
					for (NhiCalcVO setVO : newVO) {
						if (setVO.getOldlaborrange() != null && setVO.getOldlaborrange().equals(vo.getLaborRange())) {
							setVO.setLastmonthcomstuff(vo.getCommonAccAmount_Psn().setScale(0, UFDouble.ROUND_HALF_UP));// 上月普通事故保险费承担金额(个人)
							setVO.setLastmonthcomhirer(vo.getCommonAccAmount_Org().setScale(0, UFDouble.ROUND_HALF_UP));// 上月普通事故保险费承担金额(雇主)
							setVO.setLastmonthdisstuff(vo.getOccAccAmount_Psn().setScale(0, UFDouble.ROUND_HALF_UP));// 上月职业灾害保险费承担金额(个人)
							setVO.setLastmonthdishirer(vo.getOccAccAmount_Org().setScale(0, UFDouble.ROUND_HALF_UP));// 上月职业灾害保险费承担金额(雇主)
							setVO.setLastmonthempstuff(vo.getEmpInsAmount_Psn().setScale(0, UFDouble.ROUND_HALF_UP));// 上月就业保险费承担金额(个人)
							setVO.setLastmonthemphirer(vo.getEmpInsAmount_Org().setScale(0, UFDouble.ROUND_HALF_UP));// 上月就业保险费承担金额(雇主)
							setVO.setLastmonthretirestuff(vo.getLaborInsAmount_Psn()
									.setScale(0, UFDouble.ROUND_HALF_UP));// 上月劳保承担金额(个人)
							setVO.setLastmonthretirehirer(vo.getLaborInsAmount_Org()
									.setScale(0, UFDouble.ROUND_HALF_UP));// 上月劳保承担金额(雇主)
							setVO.setLastmonthretirestuff(vo.getRetireWthAmount_Psn().setScale(0,
									UFDouble.ROUND_HALF_UP));// 上月劳退金提缴金额(个人)
							setVO.setLastmonthretirehirer(vo.getRetireWthAmount_Org().setScale(0,
									UFDouble.ROUND_HALF_UP));// 上月劳退金提缴金额(雇主)
							// setVO.setLastmonthlabordays(vo.getValidLaborDays()
							// .intValue());// 上月劳保有效天数
							setVO.setLastmonthrepayfund(vo.getRepayFundAmount().setScale(0, UFDouble.ROUND_HALF_UP));// 上月薪資墊償
							setVO.setIncludelastmonth(UFBoolean.FALSE);// 是否包含上月

							nhiCalcVOList.add(setVO);
						}
					}
				}
			}
		}
		// 本月劳保劳退
		if (laborThisMonthData != null && laborThisMonthData.size() > 0) {
			for (PsnLaborDataVO vo : laborThisMonthData) {
				NhiCalcVO[] newVO = getNhiCalcVOByPsn(nhiVOExists, vo.getPk_nhicalc(), false);
				if (newVO.length > 0) {
					for (NhiCalcVO setVO : newVO) {
						if (setVO.getLaborrange() != null && setVO.getLaborrange().equals(vo.getLaborRange())) {
							setVO.setComstuff(vo.getCommonAccAmount_Psn() == null ? UFDouble.ZERO_DBL : vo
									.getCommonAccAmount_Psn().setScale(0, UFDouble.ROUND_HALF_UP));// 普通事故保险费承担金额(个人)
							setVO.setComhirer(vo.getCommonAccAmount_Org() == null ? UFDouble.ZERO_DBL : vo
									.getCommonAccAmount_Org().setScale(0, UFDouble.ROUND_HALF_UP));// 普通事故保险费承担金额(雇主)
							setVO.setDisstuff(vo.getOccAccAmount_Psn() == null ? UFDouble.ZERO_DBL : vo
									.getOccAccAmount_Psn().setScale(0, UFDouble.ROUND_HALF_UP));// 职业灾害保险费承担金额(个人)
							setVO.setDishirer(vo.getOccAccAmount_Org() == null ? UFDouble.ZERO_DBL : vo
									.getOccAccAmount_Org().setScale(0, UFDouble.ROUND_HALF_UP));// 职业灾害保险费承担金额(雇主)
							setVO.setEmpstuff(vo.getEmpInsAmount_Psn() == null ? UFDouble.ZERO_DBL : vo
									.getEmpInsAmount_Psn().setScale(0, UFDouble.ROUND_HALF_UP));// 就业保险费承担金额(个人)
							setVO.setEmphirer(vo.getEmpInsAmount_Org() == null ? UFDouble.ZERO_DBL : vo
									.getEmpInsAmount_Org().setScale(0, UFDouble.ROUND_HALF_UP));// 就业保险费承担金额(雇主)
							setVO.setLaborstuff(vo.getLaborInsAmount_Psn() == null ? UFDouble.ZERO_DBL : vo
									.getLaborInsAmount_Psn().setScale(0, UFDouble.ROUND_HALF_UP));// 劳保承担金额(个人)
							setVO.setLaborhirer(vo.getLaborInsAmount_Org() == null ? UFDouble.ZERO_DBL : vo
									.getLaborInsAmount_Org().setScale(0, UFDouble.ROUND_HALF_UP));// 劳保承担金额(雇主)
							if (vo.getIfLabor().booleanValue()) {
								setVO.setLabordays(vo.getValidLaborDays() == null ? 0 : vo.getValidLaborDays()
										.intValue());// 劳保有效天数
							} else {
								setVO.setLabordays(0);
							}

							setVO.setRepayfund(vo.getRepayFundAmount() == null ? UFDouble.ZERO_DBL : vo
									.getRepayFundAmount().setScale(0, UFDouble.ROUND_HALF_UP));// 薪資墊償基金金額
							setVO.setIncludelastmonth(vo.getIfCalculateLastMonth() == null ? UFBoolean.FALSE : vo
									.getIfCalculateLastMonth());// 是否包含上月
						}

						if (setVO.getRetirerange() != null && setVO.getRetirerange().equals(vo.getRetireRange())) {
							setVO.setRetirestuff(vo.getRetireWthAmount_Psn() == null ? UFDouble.ZERO_DBL : vo
									.getRetireWthAmount_Psn().setScale(0, UFDouble.ROUND_HALF_UP));// 劳退金提缴金额(个人)
							setVO.setRetirehirer(vo.getRetireWthAmount_Org() == null ? UFDouble.ZERO_DBL : vo
									.getRetireWthAmount_Org().setScale(0, UFDouble.ROUND_HALF_UP));// 劳退金提缴金额(雇主)

							if (vo.getIfRetire().booleanValue() && !vo.getIfOldRetire().booleanValue()) {
								setVO.setRetiredays(vo.getRetireDays() == null ? 0 : vo.getRetireDays().intValue());// 勞退有效天數
							} else {
								setVO.setRetiredays(0);
							}

						}

						nhiCalcVOList.add(setVO);
					}
				}
			}
		}
		return nhiCalcVOList.toArray(new NhiCalcVO[0]);
	}

	private NhiCalcVO[] getNhiCalcVOByPsn(NhiCalcVO[] nhiCalcVOs, String pkBeCompared, boolean isHealth) {
		List<NhiCalcVO> retList = new ArrayList<NhiCalcVO>();
		if (nhiCalcVOs != null && nhiCalcVOs.length > 0) {
			for (NhiCalcVO vo : nhiCalcVOs) {
				if (isHealth) {
					if (vo.getPk_psndoc().equals(pkBeCompared)
							&& (vo.getHealthrange() != null && vo.getHealthrange().doubleValue() > 0)) {
						retList.add(vo);
					}
				} else {
					if (vo.getPk_nhicalc().equals(pkBeCompared)) {
						retList.add(vo);
					}
				}

			}
		}

		return retList.toArray(new NhiCalcVO[0]);
	}

	@SuppressWarnings("unchecked")
	private List<PsnLaborDataVO> appendPsnLaborData(List<PsnLaborDataVO> basicLaborData) throws BusinessException {
		String strSQL = "";
		strSQL += " SELECT  nhi.pk_psndoc, nhi.begindate, nhi.enddate, def.glbdef1 labortype, ISNULL(nhi.laborsalary, 0) basesalary, ";
		strSQL += " def.begindate laborbegindate, def.enddate laborenddate,  def.glbdef14 retirebegindate, def.glbdef15 retireenddate,";
		strSQL += "         0 adjustalary, nhi.laborrange laborrange, def.glbdef5 disabledtype, ISNULL(def.glbdef6, 'N') ";
		strSQL += "         iscalclastmonth, nhi.retirerange retirerange, ISNULL(def.glbdef8, CAST(0.0 AS decimal)) ";
		strSQL += "         retireamountbypsn, ISNULL(def.glbdef9, 'N') isoldretire, psndoc.code, psndoc.name, isnull(nhi.labordays, ";
		strSQL += "         CAST(0.0 AS decimal)) labordays, ISNULL(nhi.retiredays, CAST(0.0 AS decimal)) retiredays, nhi.pk_org, nhi.pk_corp, ";
		strSQL += "        nhi.pk_nhicalc, case when nhi.laborrange is null then 'N' else 'Y' end iflabor , ";
		strSQL += "	    case when nhi.retirerange is null then 'N' else 'Y' end ifretire";
		strSQL += " FROM              twhr_nhicalc  nhi INNER JOIN  ";
		strSQL += "                             bd_psndoc  psndoc ON nhi.pk_psndoc = psndoc.pk_psndoc INNER JOIN ";
		strSQL += PsndocDefTableUtil.getPsnLaborTablename() + "  def ON def.pk_psndoc = nhi.pk_psndoc ";
		strSQL += "AND ((nhi.begindate <=  NVL(def.enddate,'9999-12-31')||' 00:00:00' AND nhi.enddate >= def.begindate || ' 00:00:00') OR  (nhi.begindate <=  NVL(def.glbdef15,'9999-12-31') AND nhi.enddate >= def.glbdef14))";
		// strSQL +=
		// "and ((nhi.begindate >= def.begindate||' 00:00:00' and nhi.enddate <= isnull(def.enddate,'9999-12-31')|| ' 00:00:00') or (nhi.begindate >= def.glbdef14 and nhi.enddate <= isnull(def.glbdef15,'9999-12-31')))";
		strSQL += " WHERE          (nhi.begindate >= '"
				+ this.getFirstDayOfMonth(this.getCalcYear(), this.getCalcMonth()).toString()
				+ "') AND (nhi.enddate <= '"
				+ this.getLastDayOfMonth(this.getCalcYear(), this.getCalcMonth()).toString()
				+ "') AND (def.glbdef1 IS NOT NULL) AND (nhi.iscalculated != 'Y') AND (nhi.dr = 0) AND (nhi.pk_psndoc in ("
				+ this.getPsnInSQL() + "))";

		List<Map> laborData = (List<Map>) this.getBaseDao().executeQuery(strSQL, new MapListProcessor());
		laborData = reMatchLaborData(laborData);
		for (Map data : laborData) {
			data.put("basesalary", getDoubleValue(data.get("basesalary")));
			data.put("adjustsalary", getDoubleValue(data.get("adjustsalary")));
			data.put("retireamountbypsn", getDoubleValue(data.get("retireamountbypsn")));
			data.put("laborrange", getDoubleValue(data.get("laborrange")));
			data.put("retirerange", getDoubleValue(data.get("retirerange")));
			data.put("labordays", getDoubleValue(data.get("labordays")));
			data.put("retiredays", getDoubleValue(data.get("retiredays")));
		}
		List<PsnLaborDataVO> newLaborData = new ArrayList<PsnLaborDataVO>();

		if (laborData != null && laborData.size() > 0) {
			// for (String pk_psndoc : this.getPsnList()) {
			for (Map data : laborData) {
				// List<Map> laborDataByPsn = getMapDataByPsn(laborData,
				// pk_psndoc);
				List<Map> laborDataByPsn = new ArrayList<>();
				laborDataByPsn.add(data);
				newLaborData.addAll(getLaborDetailDataByPsndoc(basicLaborData, laborDataByPsn,
						(String) data.get("pk_psndoc")));
			}
			// }
		}

		return calculateLaborAmounts(newLaborData);
	}

	private List<Map> reMatchLaborData(List<Map> laborData) {
		List<Map> ret = new ArrayList<>();
		if (laborData != null && laborData.size() > 0) {
			for (Map labor : laborData) {
				UFLiteralDate nhibegindate = new UFLiteralDate(new UFDate((String) labor.get("begindate")).toDate());
				UFLiteralDate nhienddate = new UFLiteralDate(new UFDate((String) labor.get("enddate")).toDate());
				UFLiteralDate laborbegindate = labor.get("laborbegindate") == null ? null : new UFLiteralDate(
						(String) labor.get("laborbegindate"));
				UFLiteralDate laborenddate = labor.get("laborenddate") == null ? nhienddate : (new UFLiteralDate(
						(String) labor.get("laborenddate")).after(nhienddate) ? nhienddate : new UFLiteralDate(
						(String) labor.get("laborenddate")));
				UFLiteralDate retirebegindate = labor.get("retirebegindate") == null ? null : new UFLiteralDate(
						(String) labor.get("retirebegindate"));
				UFLiteralDate retireenddate = labor.get("retireenddate") == null ? nhienddate : (new UFLiteralDate(
						(String) labor.get("retireenddate")).after(nhienddate) ? nhienddate : new UFLiteralDate(
						(String) labor.get("retireenddate")));
				if (NhiCalcUtils.isInScope(nhibegindate, nhienddate, laborbegindate, laborenddate, false)
						|| NhiCalcUtils.isInScope(nhibegindate, nhienddate, retirebegindate, retireenddate, false)) {
					ret.add(labor);
				}
			}
		}
		return ret;
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

	private List<PsnLaborDataVO> calculateLaborAmounts(List<PsnLaborDataVO> newLaborData) throws BusinessException {
		if (newLaborData != null && newLaborData.size() > 0) {
			for (PsnLaborDataVO vo : newLaborData) {
				// 是否投保勞保
				if (!vo.getIfLabor().booleanValue()) {
					vo.setCommonAccAmount_Psn(UFDouble.ZERO_DBL);
					vo.setCommonAccAmount_Org(UFDouble.ZERO_DBL);
					vo.setEmpInsAmount_Psn(UFDouble.ZERO_DBL);
					vo.setEmpInsAmount_Org(UFDouble.ZERO_DBL);
					vo.setOccAccAmount_Psn(UFDouble.ZERO_DBL);
					vo.setOccAccAmount_Org(UFDouble.ZERO_DBL);
					vo.setRepayFundAmount(UFDouble.ZERO_DBL);
					vo.setLaborInsAmount_Psn(UFDouble.ZERO_DBL);
					vo.setLaborInsAmount_Org(UFDouble.ZERO_DBL);
					vo.setLaborDays(UFDouble.ZERO_DBL);
				} else {
					// 根据残障程度取政府补助比例
					// ssx modified on 2019-12-01,
					// 要嚴格按照比例乘，四捨五入再加減的順序，否則會導致某一個方向上的數字產生1塊錢問題
					// 自缴比例 = 政府补助比例
					UFDouble disabledRate;
					if (vo.getDisableType() == null) {
						disabledRate = UFDouble.ZERO_DBL;
					} else {
						disabledRate = ((AllowanceVO) getBaseDocValueByKey(
								AllowanceVO.class.getName() + "_" + vo.getDisableType(), vo.getPk_legalOrg()))
								.getAllowanceamount().div(100);
					}

					// 级距表中取出的是普通+就业
					// 结果仍需计算，故先注释掉
					// RangeLineVO rangeLn = this.findRangeLineByRangeRate(
					// RangeTableTypeEnum.LABOR_RANGETABLE,
					// vo.getLaborRange(), vo.getValidLaborDays());

					// 普通事故-个人
					UFDouble commonAccTotal = vo.getLaborRange().multiply(vo.getCommonAccRate());
					UFDouble tmpAmount = UFDouble.ZERO_DBL;
					if (commonAccTotal.multiply(vo.getCommonAccRate_Psn()).multiply(vo.getValidLaborDays().div(30))
							.equals(UFDouble.ZERO_DBL)) {
						vo.setCommonAccAmount_Psn(UFDouble.ZERO_DBL);
					} else {
						tmpAmount = commonAccTotal.multiply(vo.getCommonAccRate_Psn())
								.multiply(vo.getValidLaborDays().div(30)).setScale(0, UFDouble.ROUND_HALF_UP);

						vo.setCommonAccAmount_Psn(tmpAmount.sub(tmpAmount.multiply(disabledRate).setScale(0,
								UFDouble.ROUND_HALF_UP)));
					}
					// 普通事故-公司
					vo.setCommonAccAmount_Org(commonAccTotal.multiply(vo.getCommonAccRate_Org())
							.multiply(vo.getValidLaborDays()).div(30).setScale(0, UFDouble.ROUND_HALF_UP));
					// 就业-个人
					UFDouble empInsTotal = vo.getLaborRange().multiply(vo.getEmpInsRate());
					if (empInsTotal.multiply(vo.getEmpInsRate_Psn()).multiply(vo.getValidLaborDays()).div(30)
							.equals(UFDouble.ZERO_DBL)) {
						vo.setEmpInsAmount_Psn(UFDouble.ZERO_DBL);
					} else {
						tmpAmount = empInsTotal.multiply(vo.getEmpInsRate_Psn()).multiply(vo.getValidLaborDays())
								.div(30).setScale(0, UFDouble.ROUND_HALF_UP);
						vo.setEmpInsAmount_Psn(tmpAmount.sub(tmpAmount.multiply(disabledRate).setScale(0,
								UFDouble.ROUND_HALF_UP)));
					}
					// 就业-公司
					vo.setEmpInsAmount_Org(empInsTotal.multiply(vo.getEmpInsRate_Org())
							.multiply(vo.getValidLaborDays()).div(30).setScale(0, UFDouble.ROUND_HALF_UP));
					// 职灾-个人
					UFDouble occAccTotal = vo.getLaborRange().multiply(vo.getOccAccRate());
					if (occAccTotal.multiply(vo.getOccAccRate_Psn()).multiply(vo.getValidLaborDays()).div(30)
							.equals(UFDouble.ZERO_DBL)) {
						vo.setOccAccAmount_Psn(UFDouble.ZERO_DBL);
					} else {
						tmpAmount = occAccTotal.multiply(vo.getOccAccRate_Psn()).multiply(vo.getValidLaborDays())
								.div(30).setScale(0, UFDouble.ROUND_HALF_UP);
						vo.setOccAccAmount_Psn(tmpAmount.sub(tmpAmount.multiply(disabledRate).setScale(0,
								UFDouble.ROUND_HALF_UP)));
					}
					// 职灾-公司
					vo.setOccAccAmount_Org(occAccTotal.multiply(vo.getOccAccRate_Org())
							.multiply(vo.getValidLaborDays()).div(30).setScale(0, UFDouble.ROUND_HALF_UP));

					// 工資墊償基金
					vo.setRepayFundAmount(vo.getLaborRange().multiply(vo.getRepayFundRate())
							.multiply(vo.getValidLaborDays()).div(30).setScale(0, UFDouble.ROUND_HALF_UP));

					// 按投保身份處理保費
					dealLaborInsWithPsnType(vo);

					// 處理合計
					// 劳保个人负担合计（不含职灾）
					vo.setLaborInsAmount_Psn(vo.getCommonAccAmount_Psn().add(vo.getEmpInsAmount_Psn()));
					// 劳保公司负担合计（含职灾）
					vo.setLaborInsAmount_Org(vo.getCommonAccAmount_Org().add(vo.getEmpInsAmount_Org())
							.add(vo.getOccAccAmount_Org()));
				}

				// 是否投保勞退
				if (!vo.getIfRetire().booleanValue()) {
					vo.setRetireWthAmount_Psn(UFDouble.ZERO_DBL);
					vo.setRetireWthAmount_Org(UFDouble.ZERO_DBL);
					vo.setRetireDays(UFDouble.ZERO_DBL);
				} else {
					// 如不投保勞保，則勞保計算起迄時間取勞退起迄時間
					if (!vo.getIfLabor().booleanValue()) {
						vo.setNhiBeginDate(vo.getRetireBegin());
						vo.setNhiEndDate(vo.getRetireEnd());
					}
					// remarked by ssx on 2020-07-08
					// Jessie要求全部舊制邏輯都拿掉 for Redmine#36383
					// // 舊制
					// if (vo.getIfOldRetire().booleanValue()) {
					// vo.setRetireWthAmount_Psn(UFDouble.ZERO_DBL);
					// vo.setRetireWthAmount_Org(UFDouble.ZERO_DBL);
					// vo.setRetireDays(UFDouble.ZERO_DBL);
					// } else {

					// 現制
					// vo.getRetireWthAmount_Psn() = 員工自身設定的：勞退自提比例(%)
					// vo.getRetireWthRate_Psn() = 參數指定員工自提比例（作為上限使用）
					if (vo.getRetireWthAmount_Psn() == null) {
						vo.setRetireWthAmount_Psn(UFDouble.ZERO_DBL);
					} else {
						if (SalaryDecryptUtil.decrypt(vo.getRetireWthAmount_Psn().doubleValue()) > vo
								.getRetireWthRate_Psn().multiply(100).doubleValue()) {
							throw new BusinessException(NCLangResOnserver.getInstance().getStrByID(
									"twhr_paydata",
									"TaiwanNHICalculator-0013",
									null,
									new String[] {
											vo.getPsnCode(),
											vo.getRetireWthRate_Psn().multiply(100).setScale(2, UFDouble.ROUND_HALF_UP)
													.toString() })/*
																 * 員工xxx設定的自提比例超出法規允許的最大比例xx
																 */
							);
						} else {
							// MOD 重新封装针对UFDouble的解密方法 by ssx on 2019-01-15
							// MOD 补充解密方法的调用 by Andy on 2019-01-14
							vo.setRetireWthAmount_Psn(vo.getRetireRange()
									.multiply(SalaryDecryptUtil.decrypt(vo.getRetireWthAmount_Psn()).div(100))
									.multiply(vo.getRetireDays()).div(30).setScale(0, UFDouble.ROUND_HALF_UP));
						}
					}

					vo.setRetireWthAmount_Org(vo.getRetireRange().multiply(vo.getRetireWthRate_Org())
							.multiply(vo.getRetireDays()).div(30).setScale(0, UFDouble.ROUND_HALF_UP));
					// } end for Redmine#36383
				}
			}
		}
		return newLaborData;
	}

	private void dealLaborInsWithPsnType(PsnLaborDataVO vo) {
		if ("LT01".equals(this.getRefList().get(getKey(new DefdocVO(), vo.getNhiPsnType(), PK_GLB_ORG)))) { // 雇主
			// 就业
			vo.setEmpInsAmount_Org(UFDouble.ZERO_DBL);
			vo.setEmpInsAmount_Psn(UFDouble.ZERO_DBL);
		} else if ("LT04".equals(this.getRefList().get(getKey(new DefdocVO(), vo.getNhiPsnType(), PK_GLB_ORG)))) { // 职灾劳工
			// 普通事故
			vo.setCommonAccAmount_Org(UFDouble.ZERO_DBL);
			vo.setCommonAccAmount_Psn(UFDouble.ZERO_DBL);
			// 就业
			vo.setEmpInsAmount_Org(UFDouble.ZERO_DBL);
			vo.setEmpInsAmount_Psn(UFDouble.ZERO_DBL);
			// 职灾
			vo.setOccAccAmount_Psn(UFDouble.ZERO_DBL);
		} else if ("LT06".equals(this.getRefList().get(getKey(new DefdocVO(), vo.getNhiPsnType(), PK_GLB_ORG)))) { // 外籍劳工
			vo.setLaborInsAmount_Psn(vo.getLaborInsAmount_Psn().sub(vo.getEmpInsAmount_Psn())); // 劳保个人负担合计（不含就业）
			vo.setLaborInsAmount_Org(vo.getLaborInsAmount_Org().sub(vo.getEmpInsAmount_Org())); // 劳保公司负担合计（不含就业）
			vo.setEmpInsAmount_Psn(UFDouble.ZERO_DBL); // 就业
			vo.setEmpInsAmount_Org(UFDouble.ZERO_DBL); // 就业k
		} else if ("LT10".equals(this.getRefList().get(getKey(new DefdocVO(), vo.getNhiPsnType(), PK_GLB_ORG)))) { // 育婴留停
			// 公司承担部分全为0
			vo.setCommonAccAmount_Org(UFDouble.ZERO_DBL); // 普通事故
			vo.setEmpInsAmount_Org(UFDouble.ZERO_DBL); // 就业
			vo.setOccAccAmount_Org(UFDouble.ZERO_DBL); // 职灾
			vo.setLaborInsAmount_Org(UFDouble.ZERO_DBL);// 劳保公司负担合计（不含职灾）
		} else if ("LT11".equals(this.getRefList().get(getKey(new DefdocVO(), vo.getNhiPsnType(), PK_GLB_ORG)))) { // 建教生
			// 个人及公司只缴纳普通事故（就业全为0）
			vo.setLaborInsAmount_Psn(vo.getLaborInsAmount_Psn().sub(vo.getEmpInsAmount_Psn())); // 劳保个人负担合计（不含就业）
			vo.setLaborInsAmount_Org(vo.getLaborInsAmount_Org().sub(vo.getEmpInsAmount_Org())); // 劳保公司负担合计（不含就业）
			vo.setEmpInsAmount_Psn(UFDouble.ZERO_DBL); // 就业
			vo.setEmpInsAmount_Org(UFDouble.ZERO_DBL); // 就业
		} else if ("LT12".equals(this.getRefList().get(getKey(new DefdocVO(), vo.getNhiPsnType(), PK_GLB_ORG)))) { // 不參加就業保險
			vo.setEmpInsAmount_Psn(UFDouble.ZERO_DBL); // 就业
			vo.setEmpInsAmount_Org(UFDouble.ZERO_DBL); // 就业
		} else if ("LT13".equals(this.getRefList().get(getKey(new DefdocVO(), vo.getNhiPsnType(), PK_GLB_ORG)))) { // 外籍配偶
			// 按一般勞工，不做特殊處理
		} else if ("LT14".equals(this.getRefList().get(getKey(new DefdocVO(), vo.getNhiPsnType(), PK_GLB_ORG)))) { // 陸籍配偶
			// 按一般勞工，不做特殊處理
		}
	}

	private RangeLineVO findRangeLineByRangeRate(RangeTableTypeEnum tableType, UFDouble rate, UFDouble daysCount) {
		if (rate == null || rate.equals(UFDouble.ZERO_DBL)) {
			return null;
		}

		if (this.getRangtables() != null && this.getRangtables().size() > 0) {
			for (RangeTableAggVO aggvo : this.getRangtables()) {
				if (tableType.toIntValue() == ((RangeTableVO) aggvo.getParent()).getTabletype()) {
					for (ISuperVO line : aggvo.getChildren(RangeLineVO.class)) {
						UFDouble low = ((RangeLineVO) line).getRangelower();
						UFDouble up = ((RangeLineVO) line).getRangeupper().equals(UFDouble.ZERO_DBL) ? new UFDouble(
								Double.MAX_VALUE) : ((RangeLineVO) line).getRangeupper();
						if (daysCount == null) {
							if (rate.doubleValue() >= low.doubleValue() && rate.doubleValue() <= up.doubleValue()) {
								return (RangeLineVO) line;
							}
						} else {
							if (rate.doubleValue() >= low.doubleValue()
									&& rate.doubleValue() <= up.doubleValue()
									&& daysCount.doubleValue() == (getDoubleValue(((RangeLineVO) line).getRangegrade()))) {
								return (RangeLineVO) line;
							}
						}
					}
				}
			}
		}
		return null;
	}

	private List<PsnLaborDataVO> getLaborDetailDataByPsndoc(List<PsnLaborDataVO> basicLaborData,
			List<Map> laborDataByPsn, String pk_psndoc) throws BusinessException {
		List<PsnLaborDataVO> returnVOs = new ArrayList<PsnLaborDataVO>();
		for (Map data : laborDataByPsn) {
			PsnLaborDataVO newVO = getNewPsnLaborDataVO((String) data.get("pk_org"), pk_psndoc,
					(String) data.get("pk_corp"));
			newVO.setNhiBeginDate(new UFDate("0000-01-01"));
			newVO.setNhiEndDate(new UFDate("9999-12-31"));
			newVO.setPsnCode((String) data.get("code"));
			newVO.setPsnName((String) data.get("name"));
			newVO.setPk_nhicalc((String) data.get("pk_nhicalc"));
			// 劳保投保开始日期
			if (new UFDate((String) data.get("begindate")).after(newVO.getNhiBeginDate())) {
				newVO.setNhiBeginDate(new UFDate((String) data.get("begindate")));
			}
			// 劳保投保结束日期
			if (new UFDate((String) data.get("enddate")).before(newVO.getNhiEndDate())) {
				newVO.setNhiEndDate(new UFDate((String) data.get("enddate")));
			}

			// 勞退投保開始日期
			if (data.get("retirebegindate") == null) {
				newVO.setRetireBegin(newVO.getNhiBeginDate());
			} else {
				newVO.setRetireBegin(new UFDate((String) data.get("retirebegindate")));
			}

			// 勞退投保結束日期
			if (data.get("retireenddate") == null) {
				newVO.setRetireEnd(newVO.getNhiEndDate());
			} else {
				newVO.setRetireEnd(new UFDate((String) data.get("retireenddate")));
			}

			newVO.setBasicSalary(new UFDouble((Double) data.get("basesalary")).add(new UFDouble((Double) data
					.get("adjustsalary")))); // 基本薪资
			newVO.setNhiPsnType((String) data.get("labortype"));// 投保身份注记
			newVO.setDisableType((String) data.get("disabledtype"));// 身心残障程度
			newVO.setIfCalculateLastMonth(UFBoolean.valueOf((String) data.get("iscalclastmonth")));// 是否计算上月劳保
			newVO.setLaborRange(new UFDouble((Double) data.get("laborrange")));// 劳保级距
			newVO.setLaborDays(new UFDouble((Double) data.get("labordays")));// 劳保有效天数
			newVO.setValidLaborDays(new UFDouble((Double) data.get("labordays")));// 劳保有效天数
			newVO.setRetireDays(new UFDouble((Double) data.get("retiredays"))); // 勞退有效天數
			newVO.setRetireRange(new UFDouble((Double) data.get("retirerange")));// 劳退级距
			newVO.setIfOldRetire(UFBoolean.valueOf((String) data.get("isoldretire")));// 是否勞退舊制
			newVO.setIfLabor(UFBoolean.valueOf((String) data.get("iflabor"))); // 是否投保劳保
			newVO.setIfRetire(UFBoolean.valueOf((String) data.get("ifretire"))); // 是否投保劳退
			newVO.setRetireWthAmount_Psn(new UFDouble(getDoubleValue(data.get("retireamountbypsn")))); // 員工勞退自提比例
			returnVOs.add(newVO);

			if (UFBoolean.valueOf((String) data.get("iscalclastmonth")).booleanValue()) {
				if (!this.getCalcLastMonthPsnList().contains(newVO.getPk_psndoc())) {
					this.getCalcLastMonthPsnList().add(newVO.getPk_psndoc());
				}
			} else {
				if (this.getCalcLastMonthPsnList().contains(newVO.getPk_psndoc())) {
					throw new BusinessException(NCLangResOnserver.getInstance().getStrByID("twhr_paydata",
							"TaiwanNHICalculator-0003")/*
														 * 员工 [
														 */
							+ (String) data.get("name")
							+ NCLangResOnserver.getInstance().getStrByID("twhr_paydata", "TaiwanNHICalculator-0004")/*
																													 * ]
																													 * 劳保劳退信息中是否计算上月健保设置冲突
																													 * 。
																													 */);
				}
			}
		}

		return returnVOs;
	}

	private List<Map> getMapDataByPsn(List<Map> laborData, String pk_psndoc) {
		List<Map> returnLines = new ArrayList<Map>();
		if (laborData != null && laborData.size() > 0) {
			for (Map line : laborData) {
				if (pk_psndoc.equals(line.get("pk_psndoc"))) {
					returnLines.add(line);
				}
			}
		}
		return returnLines;
	}

	private List<PsnLaborDataVO> initPsnLaborDataVO() throws BusinessException {
		List<PsnLaborDataVO> nhiData = new ArrayList<PsnLaborDataVO>();
		if (this.getPsnList() != null && this.getPsnList().size() > 0) {
			for (int i = 0; i < this.getPsnList().size(); i++) {
				PsnLaborDataVO data = getNewPsnLaborDataVO(this.getPk_org(), this.getPsnList().get(i),
						getLegalOrgListWithPsnListOrder().get(i));
				nhiData.add(data);
			}
		}

		return nhiData;
	}

	private PsnLaborDataVO getNewPsnLaborDataVO(String pk_org, String pk_psndoc, String pk_legalorg)
			throws BusinessException {
		PsnLaborDataVO data = new PsnLaborDataVO();
		data.setPk_org(pk_org);
		data.setPk_psndoc(pk_psndoc);
		data.setPk_legalOrg(pk_legalorg);
		data.setYear(Integer.valueOf(this.getCalcYear()));
		data.setMonth(Integer.valueOf(this.getCalcMonth()));
		data.setIsMainMonth(UFBoolean.TRUE);
		data.setCommonAccRate((UFDouble) getBaseDocValueByKey(BaseDocVO.class.getName() + "_TWLP0001",
				data.getPk_legalOrg())); // 普通事故保险费率
		data.setCommonAccRate_Psn((UFDouble) getBaseDocValueByKey(BaseDocVO.class.getName() + "_TWLP0002",
				data.getPk_legalOrg())); // 普通事故保险费承担比例(个人)
		data.setCommonAccRate_Org((UFDouble) getBaseDocValueByKey(BaseDocVO.class.getName() + "_TWLP0003",
				data.getPk_legalOrg())); // 普通事故保险费承担比例(雇主)
		data.setEmpInsRate((UFDouble) getBaseDocValueByKey(BaseDocVO.class.getName() + "_TWLP0004",
				data.getPk_legalOrg()));// 就业保险费率
		data.setEmpInsRate_Psn((UFDouble) getBaseDocValueByKey(BaseDocVO.class.getName() + "_TWLP0005",
				data.getPk_legalOrg())); // 就业保险费承担比例(个人)
		data.setEmpInsRate_Org((UFDouble) getBaseDocValueByKey(BaseDocVO.class.getName() + "_TWLP0006",
				data.getPk_legalOrg()));// 就业保险费承担比例(雇主)
		data.setOccAccRate((UFDouble) getBaseDocValueByKey(BaseDocVO.class.getName() + "_TWLP0007",
				data.getPk_legalOrg()));// 职业灾害保险费率(本业)
		data.setOccAccRate_Psn((UFDouble) getBaseDocValueByKey(BaseDocVO.class.getName() + "_TWLP0008",
				data.getPk_legalOrg()));// 职业灾害保险费承担比例(个人)
		data.setOccAccRate_Org((UFDouble) getBaseDocValueByKey(BaseDocVO.class.getName() + "_TWLP0009",
				data.getPk_legalOrg()));// 职业灾害保险费承担比例(雇主)
		data.setRetireWthRate_Psn((UFDouble) getBaseDocValueByKey(BaseDocVO.class.getName() + "_TWLP0017",
				data.getPk_legalOrg()));// 勞退金員工自願提繳率
		data.setRetireWthRate_Org((UFDouble) getBaseDocValueByKey(BaseDocVO.class.getName() + "_TWLP0011",
				data.getPk_legalOrg()));// 劳退金雇主提缴率
		data.setRepayFundRate((UFDouble) getBaseDocValueByKey(BaseDocVO.class.getName() + "_TWLP0013",
				data.getPk_legalOrg())); // 工資墊償基金費率
		data.setCommonHeaRate((UFDouble) getBaseDocValueByKey(BaseDocVO.class.getName() + "_TWNP0001",
				data.getPk_legalOrg())); // 普通健保費率
		data.setCommonHeaRate_Psn((UFDouble) getBaseDocValueByKey(BaseDocVO.class.getName() + "_TWNP0002",
				data.getPk_legalOrg()));// 普通健保費承擔比例(個人)
		data.setCommonHeaRate_Org((UFDouble) getBaseDocValueByKey(BaseDocVO.class.getName() + "_TWNP0003",
				data.getPk_legalOrg()));// 普通健保費承擔比例(僱主)
		data.setAveNum((UFDouble) getBaseDocValueByKey(BaseDocVO.class.getName() + "_TWNP0004", data.getPk_legalOrg())); // 平均眷口數
		return data;
	}

	private Object getBaseDocValueByKey(String baseDocCode, String pk_legalorg) throws BusinessException {
		if (this.getRefList() != null && this.getRefList().size() > 0) {
			if (null != getRefList().get(baseDocCode + "_" + pk_legalorg)) {
				return getRefList().get(baseDocCode + "_" + pk_legalorg);
			}
			return getRefList().get(baseDocCode + "_" + PK_GLB_ORG);
		} else {
			throw new BusinessException(NCLangResOnserver.getInstance().getStrByID("twhr_paydata",
					"TaiwanNHICalculator-0005", null, new String[] { baseDocCode.split("_")[1] })/*
																								 * 无法找到台湾劳健保基本參數
																								 * 。
																								 */);
		}
	}

	public PsnLaborDataVO[] getPsnLaborDataVOs() {
		return psnLaborDataVOs;
	}

	public void setPsnLaborDataVOs(PsnLaborDataVO[] psnLaborDataVOs) {
		this.psnLaborDataVOs = psnLaborDataVOs;
	}

	public List<String> getCalcLastMonthPsnList() {
		if (calcLastMonthPsnList == null) {
			calcLastMonthPsnList = new ArrayList<String>();
		}
		return calcLastMonthPsnList;
	}

	public void setCalcLastMonthPsnList(List<String> calcLastMonthPsnList) {
		this.calcLastMonthPsnList = calcLastMonthPsnList;
	}

	public List<RangeTableAggVO> getRangtables() {
		if (rangetables == null) {
			rangetables = new ArrayList<RangeTableAggVO>();
		}
		return rangetables;
	}

	public void setRangtables(List<RangeTableAggVO> rangtables) {
		this.rangetables = rangtables;
	}

	public UFBoolean getIsLastMonth() {
		return isLastMonth;
	}

	public void setIsLastMonth(UFBoolean isLastMonth) {
		this.isLastMonth = isLastMonth;
	}

	public List<String> getLegalOrgListWithPsnListOrder() {
		return legalOrgListWithPsnListOrder;
	}

	public void setLegalOrgListWithPsnListOrder(List<String> legalOrgListWithPsnListOrder) {
		this.legalOrgListWithPsnListOrder = legalOrgListWithPsnListOrder;
	}

	private Set<String> getAllLegalOrgSet() {
		if (null == allLegalOrgSet) {
			allLegalOrgSet = new HashSet<>();
		}
		return allLegalOrgSet;
	}

	private void setAllLegalOrgSet(Set<String> allLegalOrgSet) {
		this.allLegalOrgSet = allLegalOrgSet;
	}

	public String getPsnInSQL() {
		return psnInSQL;
	}

	public void setPsnInSQL(String psnInSQL) {
		this.psnInSQL = psnInSQL;
	}

}
