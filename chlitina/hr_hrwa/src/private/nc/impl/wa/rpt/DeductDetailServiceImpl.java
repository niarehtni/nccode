package nc.impl.wa.rpt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.InSQLCreator;
import nc.itf.hr.wa.IDeductDetailService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.hi.psndoc.DebtFileVO;
import nc.vo.hi.psndoc.DeductDetailsVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.func.WherePartUtil;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.pub.WaLoginVO;

public class DeductDetailServiceImpl implements IDeductDetailService {
	private BaseDAO baseDAO = new BaseDAO();

	public BaseDAO getBaseDAO() {
		return baseDAO;
	}

	/**
	 * 回写到扣款明细子集
	 */
	@Override
	public void rollbacktodeductdetail(WaLoginVO waLoginVO, String whereCondition, Boolean isRangeAll) {
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
		try {
			List<Map<String, String>> custlist = (List<Map<String, String>>) iUAPQueryBS.executeQuery(
					sqlBuffer.toString(), new MapListProcessor());
			List<String> pk_psndoclist = new ArrayList<String>();
			for (Map<String, String> custmap : custlist) {
				pk_psndoclist.add(custmap.get("pk_psndoc"));
			}
			// 校验债权档案中比率是否满足1
			validatedeductfileratio(waLoginVO, pk_psndoclist);
			// 回写子集
			rollback(waLoginVO, pk_psndoclist.toArray(new String[0]));
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void rollback(WaLoginVO waLoginVO, String[] pk_psndocs) {
		String cperiod = waLoginVO.getCperiod();
		String cyear = waLoginVO.getCyear();
		String pk_wa_class = waLoginVO.getPk_wa_class();
		InSQLCreator insql = new InSQLCreator();
		try {

			// 查询出薪资期间
			List<PeriodVO> periodlist = (List<PeriodVO>) this.baseDAO.retrieveByClause(PeriodVO.class,
					"pk_periodscheme = (select pk_periodscheme from WA_WACLASS " + " where pk_wa_class='" + pk_wa_class
							+ "') " + " and cyear='" + cyear + "' and cperiod='" + cperiod + "'");
			UFLiteralDate cbegindate = periodlist.get(0).getCstartdate();
			UFLiteralDate cenddate = (UFLiteralDate) (periodlist.get(0).getCenddate() == null ? "9999-12-31"
					: periodlist.get(0).getCenddate());
			// 查询出符合条件的债权档案
			List<DebtFileVO> debtfilevos = (List<DebtFileVO>) this.baseDAO.retrieveByClause(
					DebtFileVO.class,
					"pk_psndoc in(" + insql.getInSQL(pk_psndocs) + ") and stopflag <> 'Y' and  "
							+ " DFILENUMBER in (select filenumber from HI_PSNDOC_COURTDEDUCTION where pk_psndoc in("
							+ insql.getInSQL(pk_psndocs) + ") " + "and isstop <> 'Y' and cstartdate < '"
							+ cenddate.toString() + "')");

			// 查询出最小的剩余余额
			Map<String, UFDouble> map = new HashMap<String, UFDouble>();
			List<Map<String, String>> detailslist = (List<Map<String, String>>) this.baseDAO.executeQuery(
					"select hpd.dfilenumber, HPDE.pk_psndoc,HPDE.dcreditor,min(HPDE.remainmoney) as minremainmoney"
							+ " from HI_PSNDOC_DEDUCTDETAILS hpde, HI_PSNDOC_DEBTFILE hpd,"
							+ "HI_PSNDOC_COURTDEDUCTION hpc where  " + "HPDE.pk_debtfile=HPD.PK_PSNDOC_SUB and "
							+ "HPD.DFILENUMBER=HPC.FILENUMBER and HPC.ISSTOP <> 'Y' "
							+ "AND HPD.STOPFLAG <> 'Y' AND HPDE.pk_psndoc in (" + insql.getInSQL(pk_psndocs) + ") "
							+ "GROUP BY HPDE.pk_psndoc,HPDE.dcreditor ,hpd.dfilenumber", new MapListProcessor());
			if (detailslist.size() > 0) {
				for (Map<String, String> details : detailslist) {
					map.put(details.get("pk_psndoc") + ":" + details.get("dfilenumber") + ":"
							+ details.get("dcreditor"), new UFDouble(String.valueOf(details.get("minremainmoney"))));
				}
			}

			List<DeductDetailsVO> deductvs = new ArrayList<DeductDetailsVO>();
			// 需要更新状态的债权档案
			List<DebtFileVO> stopflags = new ArrayList<DebtFileVO>();
			// 当map大于0说明有历史回写记录
			if (map.size() > 0) {
				Map<String, UFDouble> etmap = new HashMap<String, UFDouble>();
				Map<String, UFDouble> ermap = new HashMap<String, UFDouble>();
				for (String pk_psndoc : map.keySet()) {
					for (DebtFileVO debtfilevo : debtfilevos) {
						if (pk_psndoc.split(":")[0].equals(debtfilevo.getPk_psndoc())
								&& debtfilevo.getAttributeValue("dfilenumber").equals(pk_psndoc.split(":")[1])
								&& pk_psndoc.split(":")[2].equals(debtfilevo.getAttributeValue("creditor"))) {
							if (new UFDouble(String.valueOf(map.get(pk_psndoc))).sub(
									new UFDouble(String.valueOf(debtfilevo.getAttributeValue("repaymentamount"))))
									.doubleValue() <= 0) {
								// 剩余出来的金额
								UFDouble extralamount = new UFDouble(String.valueOf(debtfilevo
										.getAttributeValue("repaymentamount"))).sub(new UFDouble(String.valueOf(map
										.get(pk_psndoc))));
								if (etmap.size() > 0
										&& etmap.containsKey(debtfilevo.getPk_psndoc()
												+ debtfilevo.getAttributeValue("dfilenumber"))) {
									etmap.put(
											debtfilevo.getPk_psndoc() + debtfilevo.getAttributeValue("dfilenumber"),
											etmap.get(
													debtfilevo.getPk_psndoc()
															+ debtfilevo.getAttributeValue("dfilenumber")).add(
													extralamount));
								} else {
									etmap.put(debtfilevo.getPk_psndoc() + debtfilevo.getAttributeValue("dfilenumber"),
											extralamount);
								}
								debtfilevo.setAttributeValue("repaymentamount",
										new UFDouble(String.valueOf(new UFDouble(String.valueOf(map.get(pk_psndoc))))));
								// 已经还清的债权的比例
								UFDouble extralratio = new UFDouble(String.valueOf(debtfilevo
										.getAttributeValue("repaymentratio")));
								if (ermap.size() > 0
										&& ermap.containsKey(debtfilevo.getPk_psndoc()
												+ debtfilevo.getAttributeValue("dfilenumber"))) {
									ermap.put(
											debtfilevo.getPk_psndoc() + debtfilevo.getAttributeValue("dfilenumber"),
											ermap.get(
													debtfilevo.getPk_psndoc()
															+ debtfilevo.getAttributeValue("dfilenumber")).add(
													extralratio));
								}
								{
									ermap.put(debtfilevo.getPk_psndoc() + debtfilevo.getAttributeValue("dfilenumber"),
											extralratio);
								}

							}
							debtfilevo.setAttributeValue("creditamount",
									new UFDouble(String.valueOf(new UFDouble(String.valueOf(map.get(pk_psndoc))))));
						}
					}
				}

				for (DebtFileVO debtfilevo : debtfilevos) {
					UFDouble money = UFDouble.ZERO_DBL;
					UFDouble ratio = UFDouble.ZERO_DBL;
					if (etmap.size() > 0) {
						for (String strmoney : etmap.keySet()) {
							if ((debtfilevo.getPk_psndoc() + debtfilevo.getAttributeValue("dfilenumber"))
									.equals(strmoney)
									&& !debtfilevo.getAttributeValue("creditamount").equals(
											debtfilevo.getAttributeValue("repaymentamount"))) {
								money = etmap.get(strmoney);
							}

						}
					}
					if (ermap.size() > 0) {
						for (String strratio : ermap.keySet()) {
							if ((debtfilevo.getPk_psndoc() + debtfilevo.getAttributeValue("dfilenumber"))
									.equals(strratio)
									&& !debtfilevo.getAttributeValue("creditamount").equals(
											debtfilevo.getAttributeValue("repaymentamount"))) {
								ratio = ermap.get(strratio);
							}
						}
					}
					debtfilevo.setAttributeValue("repaymentamount",
							new UFDouble(String.valueOf(debtfilevo.getAttributeValue("repaymentamount"))).add(money
									.multiply(new UFDouble(String.valueOf(debtfilevo
											.getAttributeValue("repaymentratio"))).div(UFDouble.ONE_DBL.sub(ratio)))));

					DeductDetailsVO deductdetail = new DeductDetailsVO();
					deductdetail.setAttributeValue("salaryyearmonth", cyear + cperiod);
					deductdetail.setAttributeValue("waclass", pk_wa_class);
					deductdetail.setAttributeValue("pk_psndoc", debtfilevo.getPk_psndoc());
					deductdetail.setAttributeValue("dcreditor", debtfilevo.getAttributeValue("creditor"));
					deductdetail.setAttributeValue("pk_debtfile", debtfilevo.getPk_psndoc_sub());
					deductdetail.setAttributeValue("deductmoney",
							new UFDouble(String.valueOf(debtfilevo.getAttributeValue("repaymentamount"))));
					deductdetail.setAttributeValue(
							"remainmoney",
							new UFDouble(String.valueOf(debtfilevo.getAttributeValue("creditamount"))).sub(
									new UFDouble(String.valueOf(debtfilevo.getAttributeValue("repaymentamount"))))
									.doubleValue() < 0 ? UFDouble.ZERO_DBL : new UFDouble(String.valueOf(debtfilevo
									.getAttributeValue("creditamount"))).sub(new UFDouble(String.valueOf(debtfilevo
									.getAttributeValue("repaymentamount")))));
					deductvs.add(deductdetail);
					if (new UFDouble(String.valueOf(deductdetail.getAttributeValue("remainmoney"))).doubleValue() == 0) {
						stopflags.add(debtfilevo);
					}
				}

			} else {
				// 第一次回写
				for (DebtFileVO debtfilevo : debtfilevos) {
					DeductDetailsVO deductdetail = new DeductDetailsVO();
					deductdetail.setAttributeValue("salaryyearmonth", cyear + cperiod);
					deductdetail.setAttributeValue("waclass", pk_wa_class);
					deductdetail.setAttributeValue("pk_psndoc", debtfilevo.getPk_psndoc());
					deductdetail.setAttributeValue("dcreditor", debtfilevo.getAttributeValue("creditor"));
					deductdetail.setAttributeValue("pk_debtfile", debtfilevo.getPk_psndoc_sub());
					deductdetail.setAttributeValue("deductmoney",
							new UFDouble(String.valueOf(debtfilevo.getAttributeValue("repaymentamount"))));
					UFDouble remainmoney = new UFDouble(String.valueOf(debtfilevo.getAttributeValue("creditamount")))
							.sub(new UFDouble(String.valueOf(debtfilevo.getAttributeValue("repaymentamount"))));
					deductdetail.setAttributeValue("remainmoney", remainmoney.doubleValue() < 0 ? UFDouble.ZERO_DBL
							: remainmoney);
					deductvs.add(deductdetail);
					if (remainmoney.doubleValue() == 0) {
						stopflags.add(debtfilevo);
					}
				}
			}
			// 持久化到数据库
			this.baseDAO.insertVOArray(deductvs.toArray(new DeductDetailsVO[0]));
			// 更新是否停止状态
			updateflag(stopflags);

		} catch (BusinessException e) {
			e.printStackTrace();
		}

	}

	private void updateflag(List<DebtFileVO> debtfilevos) {
		try {
			InSQLCreator insql = new InSQLCreator();
			List<String> pk_psndocs = new ArrayList<String>();
			List<String> filenumbers = new ArrayList<String>();
			List<String> listpsndoc = new ArrayList<String>();
			List<String> listfilenumber = new ArrayList<String>();
			List<String> pk_debtfiles = new ArrayList<String>();
			Map<String, String> map = new HashMap<String, String>();
			if (debtfilevos.size() <= 0) {
				return;
			}
			for (DebtFileVO devo : debtfilevos) {
				pk_psndocs.add(devo.getPk_psndoc());
				filenumbers.add(String.valueOf(devo.getAttributeValue("dfilenumber")));
				map.put(devo.getPk_psndoc() + ":" + devo.getAttributeValue("dfilenumber"), null);
				pk_debtfiles.add(devo.getPk_psndoc_sub());
			}
			this.getBaseDAO().executeUpdate(
					"update HI_PSNDOC_DEBTFILE set stopflag ='Y' where pk_psndoc_sub in ("
							+ insql.getInSQL(pk_debtfiles.toArray(new String[0])) + ");");
			// this.baseDAO.updateVOArray(debtfilevos.toArray(new
			// DebtFileVO[0]));

			List<Map<String, String>> list = (List<Map<String, String>>) this.baseDAO.executeQuery(
					"select PK_PSNDOC,dfilenumber,STOPFLAG from HI_PSNDOC_DEBTFILE where PK_PSNDOC in ("
							+ insql.getInSQL(pk_psndocs.toArray(new String[0])) + ") and dfilenumber in("
							+ insql.getInSQL(filenumbers.toArray(new String[0])) + ")"
							+ " GROUP BY PK_PSNDOC,dfilenumber,STOPFLAG", new MapListProcessor());
			for (String pks : map.keySet()) {
				int i = 0;
				for (Map<String, String> maps : list) {
					if (pks.equals(maps.get("pk_psndoc") + ":" + maps.get("dfilenumber"))) {
						i++;
					}
				}
				if (i == 1) {
					listpsndoc.add(pks.split(":")[0]);
					listfilenumber.add(pks.split(":")[1]);
				}
			}
			this.getBaseDAO().executeUpdate(
					"update HI_PSNDOC_COURTDEDUCTION set isstop ='Y' where PK_PSNDOC in("
							+ insql.getInSQL(listpsndoc.toArray(new String[0])) + ") and filenumber in("
							+ insql.getInSQL(listfilenumber.toArray(new String[0])) + ");");

		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}

	private void validatedeductfileratio(WaLoginVO waLoginVO, List<String> pk_psndoclist) {
		// 在债权档案里面查询出这些人的信息
		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		InSQLCreator insql = new InSQLCreator();
		try {
			String psndocsInSQL = insql.getInSQL(pk_psndoclist.toArray(new String[0]));
			List<Map<String, String>> delist = (List<Map<String, String>>) iUAPQueryBS.executeQuery(
					"select DFILENUMBER, pk_psndoc, SUM(repaymentratio) as sumratio from HI_PSNDOC_DEBTFILE "
							+ "where STOPFLAG <> 'Y' and pk_psndoc in(" + psndocsInSQL + ")  "
							+ "GROUP BY DFILENUMBER,pk_psndoc;", new MapListProcessor());
			// 根据pk查询人员code
			List<Map<String, String>> psndoclist = (List<Map<String, String>>) iUAPQueryBS.executeQuery(
					"select code, pk_psndoc from bd_psndoc " + "where pk_psndoc in(" + psndocsInSQL + ")",
					new MapListProcessor());
			List<String> errmaps = new ArrayList<String>();
			for (Map<String, String> psndocmap : psndoclist) {
				for (Map<String, String> demap : delist) {
					UFDouble sumratio = new UFDouble(String.valueOf(demap.get("sumratio")));
					// BigDecimal bg = new BigDecimal(sumratio.doubleValue());
					// double newsumratio = bg.setScale(2,
					// BigDecimal.ROUND_HALF_UP).doubleValue();
					String psndoccode = null;
					for (String str : psndocmap.keySet()) {
						if (demap.get("pk_psndoc").equals(psndocmap.get(str))) {
							psndoccode = psndocmap.get("code");
						}
					}
					if (sumratio != UFDouble.ONE_DBL) {
						String message = "再请重新分配員工编号: " + psndoccode + " 的档案编号: " + demap.get("dfilenumber") + " 欠款比率";
						errmaps.add(message);
					}
				}
			}
			if (errmaps.size() > 0) {
				throw new BusinessException(errmaps.toString());
			}

		} catch (BusinessException e) {
			e.printStackTrace();
		}

	}
}
