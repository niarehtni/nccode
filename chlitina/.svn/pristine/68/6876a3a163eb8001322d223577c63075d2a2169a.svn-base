package nc.impl.wa.func;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.InSQLCreator;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pubitf.twhr.IBasedocPubQuery;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.hi.psndoc.CourtDeductionSetting;
import nc.vo.hi.psndoc.DebtFileVO;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.twhr.basedoc.BaseDocVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.pub.WaLoginContext;

public class CourtDeductParse extends AbstractWAFormulaParse {
	private BaseDAO baseDao;

	public BaseDAO getBaseDao() {
		if (this.baseDao == null) {
			this.baseDao = new BaseDAO();
		}
		return this.baseDao;
	}

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {

		List<Map<String, String>> custlist = (List<Map<String, String>>) this.getBaseDao().executeQuery(
				"select pk_psndoc from wa_cacu_data where pk_wa_class='" + context.getPk_wa_class()
						+ "' and creator = '" + context.getPk_loginUser() + "';", new MapListProcessor());
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr(" wa_cacu_data.cacu_value ");
		getdetails(getContext(), custlist);
		return fvo;

	}

	private void getdetails(WaLoginContext context, List<Map<String, String>> custlist) {
		// 通过人员将所有的人员的法扣查询出来
		List<String> pk_psndocs = new ArrayList<String>();
		for (Map<String, String> pk_psndoc : custlist) {
			pk_psndocs.add(pk_psndoc.get("pk_psndoc"));
		}
		// Map<String, Map<String, Integer>> pkpsndoc_code = new HashMap<String,
		// Map<String, Integer>>();
		// 获取档案编号
		List<String> filenumbers = new ArrayList<String>();
		InSQLCreator insql = new InSQLCreator();
		List<String> deflist = new ArrayList<String>();
		try {
			// 查找薪资期间的开始和结束时间
			List<PeriodVO> periodlist = (List<PeriodVO>) this.baseDao.retrieveByClause(
					PeriodVO.class,
					"pk_periodscheme = (select pk_periodscheme from WA_WACLASS " + " where pk_wa_class='"
							+ context.getPk_wa_class() + "') " + " and cyear='" + context.getCyear()
							+ "' and cperiod='" + context.getCperiod() + "'");
			UFLiteralDate cbegindate = periodlist.get(0).getCstartdate();
			UFLiteralDate cenddate = (UFLiteralDate) (periodlist.get(0).getCenddate() == null ? "9999-12-31"
					: periodlist.get(0).getCenddate());
			// 查询符合条件的法院扣款设置数据
			List<CourtDeductionSetting> cdsettings = (List<CourtDeductionSetting>) this.baseDao.retrieveByClause(
					CourtDeductionSetting.class, "pk_psndoc in(" + insql.getInSQL(pk_psndocs.toArray(new String[0]))
							+ ") and dr=0 and isstop <> 'Y' and cstartdate < '" + cenddate.toString() + "'");
			if (cdsettings.size() <= 0) {
				return;
			}
			for (String pk_psndoc : pk_psndocs) {
				Map<String, Integer> fcode = new HashMap<String, Integer>();
				for (CourtDeductionSetting setting : cdsettings) {
					if (pk_psndoc.equals(setting.getAttributeValue("pk_psndoc"))) {
						fcode.put((String) setting.getAttributeValue("filenumber"),
								(Integer) setting.getAttributeValue("courtdeductways"));
						if (null != setting.getAttributeValue("mindeductcountry")) {
							deflist.add(setting.getAttributeValue("mindeductcountry").toString());
						}
						filenumbers.add(String.valueOf(setting.getAttributeValue("filenumber")));
					}
				}
				// pkpsndoc_code.put(pk_psndoc, fcode);
			}
			// 通过pk查询最低限市别money
			List<DefdocVO> defvolist = null;
			if (deflist.size() > 0) {
				defvolist = (List<DefdocVO>) this.baseDao.retrieveByClause(DefdocVO.class,
						"pk_defdoc in(" + insql.getInSQL(deflist.toArray(new String[0])) + ")");
			}
			// 通过year,month和pk_psndocs 查询出wa_data
			List<Map<String, String>> wadatalist = (List<Map<String, String>>) this.baseDao.executeQuery(
					"select 00007, pk_psndoc from wa_data where pk_psndoc in("
							+ insql.getInSQL(pk_psndocs.toArray(new String[0])) + ") and pk_wa_class='"
							+ context.getPk_wa_class() + "' " + "and cyear='" + context.getCyear() + "' and cperiod='"
							+ context.getCperiod() + "'", new MapListProcessor());
			// 判断这个月剩余还款金额是否小于还款金额
			List<Map<String, String>> deductmoneylist = (List<Map<String, String>>) this.baseDao
					.executeQuery(
							"select hpc.pk_psndoc, HPC.FILENUMBER, sum(HPDE.remainmoney) as sumremainmoney,HPC.monthexecutamount,HPC.MONTHEXECUTRATE,HPC.TOTALAMOUNTOWED  from HI_PSNDOC_COURTDEDUCTION hpc,"
									+ " HI_PSNDOC_DEBTFILE hpd, HI_PSNDOC_DEDUCTDETAILS hpde where "
									+ " HPC.FILENUMBER=HPD.DFILENUMBER and hpc.pk_psndoc=hpd.pk_psndoc and "
									+ " HPD.CREDITOR=HPDE.DCREDITOR and hpc.cstartdate < '"
									+ cenddate.toString()
									+ "'"
									+ " and hpd.pk_psndoc=hpde.pk_psndoc and HPC.ISSTOP <> 'Y' and HPD.STOPFLAG <> 'Y'"
									+ " group by HPC.FILENUMBER, HPC.MONTHEXECUTAMOUNT,HPC.MONTHEXECUTRATE,HPC.TOTALAMOUNTOWED,hpc.pk_psndoc",
							new MapListProcessor());
			// 在参数设定里面取出需要扣款的薪资项
			BaseDocVO basevo = NCLocator.getInstance().lookup(IBasedocPubQuery.class)
					.queryBaseDocByCode(pk_org, "TWEFCDDTN");
			String item = null;
			if (null == basevo) {
				return;
			}
			if (null != basevo.getRefvalue()) {
				List<WaItemVO> waitem = (List<WaItemVO>) this.baseDao.retrieveByClause(WaItemVO.class, "pk_wa_item='"
						+ basevo.getRefvalue() + "'");
				item = waitem.get(0).getCode();
			} else {
				item = basevo.getTextvalue();
			}
			List<CourtDeductionSetting> cdsettinglists = new ArrayList<CourtDeductionSetting>();
			for (Map<String, String> payfile : wadatalist) {
				UFDouble sumratiomoney = UFDouble.ZERO_DBL;
				UFDouble summoney = UFDouble.ZERO_DBL;
				UFDouble summincountrymoney = UFDouble.ZERO_DBL;

				for (CourtDeductionSetting settings : cdsettings) {
					if (payfile.get("pk_psndoc").equals(settings.getPk_psndoc())) {
						UFDouble sumremainmoney = UFDouble.ZERO_DBL;
						for (Map<String, String> dmap : deductmoneylist) {
							if (dmap.get("pk_psndoc").equals(settings.getPk_psndoc())) {
								if (dmap.get("filenumber").equals(settings.getAttributeValue("filenumber"))) {
									sumremainmoney = dmap.get("sumremainmoney") == null ? UFDouble.ZERO_DBL
											: new UFDouble(String.valueOf(dmap.get("sumremainmoney")));
								}
							}
						}
						// 判断扣款形式
						if ((Integer) settings.getAttributeValue("courtdeductways") == 1) {
							UFDouble ratio = settings.getAttributeValue("monthexecutrate") == null ? UFDouble.ZERO_DBL
									: new UFDouble((String.valueOf(settings.getAttributeValue("monthexecutrate"))));

							if (sumremainmoney.doubleValue() != 0
									&& ratio.multiply(
											payfile.get(item) == null ? UFDouble.ZERO_DBL : new UFDouble(String
													.valueOf(payfile.get(item)))).sub(sumremainmoney).doubleValue() > 0) {
								settings.setAttributeValue("monthexecutamount", sumremainmoney);
								sumratiomoney = sumratiomoney.add(sumremainmoney);
							} else {
								sumratiomoney = sumratiomoney
										.add(ratio.multiply(payfile.get(item) == null ? UFDouble.ZERO_DBL
												: new UFDouble(String.valueOf(payfile.get(item)))));
								// 更新法扣子集的月执行金额
								settings.setAttributeValue("monthexecutamount",
										ratio.multiply(payfile.get(item) == null ? UFDouble.ZERO_DBL : new UFDouble(
												String.valueOf(payfile.get(item)))));
							}
						} else if ((Integer) settings.getAttributeValue("courtdeductways") == 2) {
							if (sumremainmoney.doubleValue() != 0
									&& (settings.getAttributeValue("monthexecutamount") == null ? UFDouble.ZERO_DBL
											: new UFDouble((String.valueOf(settings
													.getAttributeValue("monthexecutamount")))).sub(sumremainmoney))
											.doubleValue() > 0) {
								summoney = summoney.add(sumremainmoney);
								settings.setAttributeValue("monthexecutamount", sumremainmoney);
							} else {
								summoney = summoney
										.add(settings.getAttributeValue("monthexecutamount") == null ? UFDouble.ZERO_DBL
												: new UFDouble((String.valueOf(settings
														.getAttributeValue("monthexecutamount")))));
							}

						} else {

							for (DefdocVO defvo : defvolist) {
								if (defvo.getPk_defdoc()
										.equals((String) settings.getAttributeValue("mindeductcountry"))) {
									summincountrymoney = new UFDouble(String.valueOf(defvo.getMemo()));
									// 更新法扣子集的月执行金额
									settings.setAttributeValue("monthexecutamount", summincountrymoney);
								}
							}
							if (sumremainmoney.doubleValue() != 0
									&& sumremainmoney.sub(summincountrymoney).doubleValue() < 0) {
								summincountrymoney = sumremainmoney;
								// 更新法扣子集的月执行金额
								settings.setAttributeValue("monthexecutamount", sumremainmoney);
							}
						}
					}
				}
				UFDouble cacuvalue = sumratiomoney.add(summoney)
						.add(((payfile.get(item) == null ? UFDouble.ZERO_DBL : new UFDouble(String.valueOf(payfile
								.get(item)))).sub(summincountrymoney).doubleValue() < 0 ? UFDouble.ZERO_DBL : payfile
								.get(item) == null ? UFDouble.ZERO_DBL
								: new UFDouble(String.valueOf(payfile.get(item)))).sub(summincountrymoney));

				String strsql = "update wa_cacu_data set  cacu_value=" + cacuvalue + " " + "where pk_psndoc='"
						+ payfile.get("pk_psndoc") + "' and pk_wa_class= '" + context.getPk_wa_class()
						+ "' and creator = '" + context.getPk_loginUser() + "'";
				this.baseDao.executeUpdate(strsql);
			}
			// 更新法扣
			// this.baseDao.updateVOArray(cdsettinglists.toArray(new
			// CourtDeductionSetting[0]));

			// 查询出债权档案
			List<DebtFileVO> debtlist = (List<DebtFileVO>) this.baseDao.retrieveByClause(DebtFileVO.class,
					"pk_psndoc in(" + insql.getInSQL(pk_psndocs.toArray(new String[0])) + ") and stopflag <> 'Y'"
							+ " and dfilenumber in(" + insql.getInSQL(filenumbers.toArray(new String[0])) + ")");
			// 更新债权档案
			List<DebtFileVO> debtvolist = new ArrayList<DebtFileVO>();
			for (CourtDeductionSetting cdsetting : cdsettinglists) {
				List<DebtFileVO> dfvos = new ArrayList<DebtFileVO>();
				UFDouble extralmoney = UFDouble.ZERO_DBL;
				UFDouble extralratio = UFDouble.ZERO_DBL;
				for (DebtFileVO debtfile : debtlist) {
					if (cdsetting.getPk_psndoc().equals(debtfile.getPk_psndoc())) {
						if (cdsetting.getAttributeValue("filenumber").equals(debtfile.getAttributeValue("dfilenumber"))) {
							UFDouble repaymentamount = new UFDouble(String.valueOf(debtfile
									.getAttributeValue("repaymentratio"))).multiply(new UFDouble(String
									.valueOf(cdsetting.getAttributeValue("monthexecutamount"))));
							UFDouble creditamount = debtfile.getAttributeValue("creditamount") == null ? UFDouble.ZERO_DBL
									: new UFDouble(String.valueOf(debtfile.getAttributeValue("creditamount")));
							if (repaymentamount.sub(creditamount).doubleValue() > 0) {
								debtfile.setAttributeValue("repaymentamount", creditamount);
								extralmoney = extralmoney.add(repaymentamount.sub(creditamount));
								extralratio = extralratio.add(new UFDouble(String.valueOf(debtfile
										.getAttributeValue("repaymentratio"))));
							} else {
								debtfile.setAttributeValue("repaymentamount", repaymentamount);
							}
							dfvos.add(debtfile);
						}
					}
				}
				for (DebtFileVO debtfile : dfvos) {
					if (cdsetting.getPk_psndoc().equals(debtfile.getPk_psndoc())) {
						if (cdsetting.getAttributeValue("filenumber").equals(debtfile.getAttributeValue("dfilenumber"))) {
							UFDouble repaymentamount = new UFDouble(String.valueOf(debtfile
									.getAttributeValue("repaymentamount")));
							UFDouble creditamount = debtfile.getAttributeValue("creditamount") == null ? UFDouble.ZERO_DBL
									: new UFDouble(String.valueOf(debtfile.getAttributeValue("creditamount")));
							if (creditamount.sub(repaymentamount).doubleValue() != 0) {
								repaymentamount = repaymentamount.add(extralmoney.multiply(new UFDouble(String
										.valueOf(debtfile.getAttributeValue("repaymentratio"))).div(UFDouble.ONE_DBL
										.sub(extralratio))));
							}

							debtfile.setAttributeValue("repaymentamount", repaymentamount);

							debtvolist.add(debtfile);
						}
					}
				}
			}
			// 更新债权档案
			this.baseDao.updateVOArray(debtvolist.toArray(new DebtFileVO[0]));

		} catch (BusinessException e) {
			e.printStackTrace();
		}

	}
}
