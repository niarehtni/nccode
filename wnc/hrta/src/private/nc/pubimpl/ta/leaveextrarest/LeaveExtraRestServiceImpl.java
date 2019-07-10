package nc.pubimpl.ta.leaveextrarest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.logging.Logger;
import nc.hr.utils.InSQLCreator;
import nc.impl.pub.OTLeaveBalanceUtils;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.ta.leaveextrarest.ILeaveExtraRestService;
import nc.vo.hr.temptable.TempTableVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leaveextrarest.LeaveExtraRestVO;
import nc.vo.ta.overtime.OTBalanceDetailVO;
import nc.vo.ta.overtime.OTLeaveBalanceVO;
import nc.vo.ta.overtime.SoureBillTypeEnum;

import org.apache.commons.lang.StringUtils;

public class LeaveExtraRestServiceImpl implements ILeaveExtraRestService {

	private BaseDAO baseDao = null;

	public void setBaseDao(BaseDAO basedao) {
		baseDao = basedao;
	}

	public BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}

	@Override
	public void settledByExpiryDate(String pk_org, String[] pk_psndocs, UFLiteralDate settleDate, Boolean isForce)
			throws BusinessException {
		String strCondition = " dr=0 and isnull(issettled,'N')='N' ";

		if (isForce) {
			// 如果為強制結算，必須指定人員列表，不允許整個組織強制結算
			if (pk_psndocs == null || pk_psndocs.length == 0) {
				throw new BusinessException("強制結算錯誤：未指定結算人員。");
			}
		} else {
			// 非強制結算時，結算日期不能為空
			if (settleDate == null) {
				throw new BusinessException("結算錯誤：結算日期不能為空。");
			}

			strCondition += " and expiredate <='" + settleDate.toString() + "' ";
		}

		// 以人為先：指定人員的，按指定人員進行結算，否則按整個組織進行結算
		if (pk_psndocs == null || pk_psndocs.length == 0) {
			if (StringUtils.isEmpty(pk_org)) {
				throw new BusinessException("結算錯誤：結算組織和結算人員不能同時為空。");
			}

			strCondition += " and pk_org = '" + pk_org + "'";
		} else {
			InSQLCreator insql = new InSQLCreator();
			String strTmpTable = "";
			if (pk_psndocs.length >= 200) {
				strTmpTable = insql.recreateTempTable();
			}
			String inpsndocsql = "";
			if (pk_psndocs.length >= 200) {
				inpsndocsql = "select " + TempTableVO.IN_PK + " from " + insql.createTempTable(strTmpTable, pk_psndocs);
			} else {
				inpsndocsql = insql.getInSQL(pk_psndocs);
			}
			strCondition += " and pk_psndoc in (" + inpsndocsql + ")";
		}

		this.getBaseDao().executeUpdate(
				"update " + LeaveExtraRestVO.getDefaultTableName() + " set " + LeaveExtraRestVO.ISSETTLED + "='Y', "
						+ LeaveExtraRestVO.SETTLEDATE + "='" + settleDate.toString() + "' where " + strCondition);

	}

	@Override
	public OTLeaveBalanceVO[] getLeaveExtHoursByType(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate, String pk_leavetypecopy, boolean isSettled, boolean isLeave)
			throws BusinessException {

		List<OTLeaveBalanceVO> headvos = new ArrayList<OTLeaveBalanceVO>();

		pk_psndocs = OTLeaveBalanceUtils.getPsnListByDateScope(pk_org, pk_psndocs, beginDate, endDate);

		Map<String, UFLiteralDate> psnWorkStartDate = OTLeaveBalanceUtils.getPsnOrgMapByPsnBeginDate(pk_psndocs,
				beginDate, endDate);

		UFDouble dayHoursRate = getDayHourRate(pk_org, pk_leavetypecopy);

		if (pk_psndocs != null && pk_psndocs.length > 0) {
			for (String pk_psndoc : pk_psndocs) {
				if (!psnWorkStartDate.containsKey(pk_psndoc) || psnWorkStartDate.get(pk_psndoc) == null) {
					Logger.error("員工年資起算日為空：PSN [" + pk_psndoc + "] ");
				} else {
					// 根據期間開始時間定位員工年資起算期間
					UFLiteralDate psnBeginDate = OTLeaveBalanceUtils.getBeginDateByWorkAgeStartDate(
							String.valueOf(beginDate.getYear()), psnWorkStartDate.get(pk_psndoc));
					UFLiteralDate psnEndDate = OTLeaveBalanceUtils.getEndDateByWorkAgeStartDate(
							String.valueOf(beginDate.getYear()), psnWorkStartDate.get(pk_psndoc));
					if (psnBeginDate.after(beginDate)) {
						psnBeginDate = OTLeaveBalanceUtils.getBeginDateByWorkAgeStartDate(
								String.valueOf(beginDate.getYear() - 1), psnWorkStartDate.get(pk_psndoc));
						psnEndDate = OTLeaveBalanceUtils.getEndDateByWorkAgeStartDate(
								String.valueOf(beginDate.getYear() - 1), psnWorkStartDate.get(pk_psndoc));
					}
					// end

					// MOD 離職計算期間後至當月最後一天的加班費
					// added by ssx on 2018-04-05
					if (isLeave) {
						psnBeginDate = endDate.getDateAfter(1);
						psnEndDate = psnBeginDate.getDateAfter(psnBeginDate.getDaysMonth() - psnBeginDate.getDay()); // 月末最後一天

						// 離職日期在期間後至當月最後一天之間，否則不計算
						int isleavepsn = (int) this.getBaseDao().executeQuery(
								"select count(*) from hi_psnjob where trnsevent=4 and pk_psndoc = '" + pk_psndoc
										+ "' and begindate between '" + psnBeginDate.toString() + "' and '"
										+ psnEndDate.toString() + "';", new ColumnProcessor());
						if (isleavepsn <= 0) {
							continue;
						}
					}
					// end MOD

					UFDouble totalAmount = UFDouble.ZERO_DBL; // 享有
					UFDouble spentAmount = UFDouble.ZERO_DBL;// 已休
					UFDouble remainAmount = UFDouble.ZERO_DBL;// 剩余
					UFDouble frozenAmount = UFDouble.ZERO_DBL;// 冻结
					UFDouble useableAmount = UFDouble.ZERO_DBL; // 可用
					UFDouble unusableAmount = UFDouble.ZERO_DBL;// 不可用

					UFLiteralDate settleddate = null;

					LeaveExtraRestVO[] extvos = getLeaveExtraRestVOsByPsnDate(pk_org, pk_psndoc, psnBeginDate,
							psnEndDate, isSettled, isLeave);
					LeaveRegVO[] leaveRegVOs = OTLeaveBalanceUtils.getLeaveRegByPsnDate(pk_org, pk_psndoc,
							psnBeginDate, psnEndDate, pk_leavetypecopy);
					// 享有
					for (LeaveExtraRestVO vo : extvos) {
						UFDouble curAmount = OTLeaveBalanceUtils.getUFDouble(vo.getChangedayorhour()).multiply(
								dayHoursRate);
						totalAmount = totalAmount.add(curAmount);

						if (UFBoolean.TRUE.equals(vo.getIssettled())) {
							unusableAmount = unusableAmount.add(curAmount);
							settleddate = vo.getSettledate();
						}
					}

					if (!totalAmount.equals(UFDouble.ZERO_DBL)) {
						// 已休
						if (leaveRegVOs != null && leaveRegVOs.length > 0) {
							for (LeaveRegVO lrvo : leaveRegVOs) {
								spentAmount = spentAmount.add(OTLeaveBalanceUtils.getUFDouble(lrvo.getLeavehour())
										.multiply(dayHoursRate));
							}
						}

						if (unusableAmount.doubleValue() > 0) {
							unusableAmount = unusableAmount.sub(spentAmount);
						}
						// 剩餘
						remainAmount = remainAmount.add(totalAmount.sub(spentAmount));
						// 可用
						useableAmount = useableAmount.add(totalAmount.sub(spentAmount).sub(frozenAmount)).sub(
								unusableAmount);

						if (!totalAmount.equals(UFDouble.ZERO_DBL) || !spentAmount.equals(UFDouble.ZERO_DBL)
								|| !remainAmount.equals(UFDouble.ZERO_DBL) || !frozenAmount.equals(UFDouble.ZERO_DBL)
								|| !useableAmount.equals(UFDouble.ZERO_DBL)) {
							// 创建表头
							OTLeaveBalanceVO headVo = OTLeaveBalanceUtils.createNewHeadVO(pk_org, pk_psndoc,
									totalAmount, spentAmount, remainAmount, frozenAmount, useableAmount);
							headVo.setQstartdate(psnBeginDate);
							headVo.setQenddate(psnEndDate);
							headVo.setSettleddate(settleddate);
							headvos.add(headVo);
						}
					}
				}
			}
		}

		return headvos.toArray(new OTLeaveBalanceVO[0]);
	}

	@SuppressWarnings("unchecked")
	private LeaveExtraRestVO[] getLeaveExtraRestVOsByPsnDate(String pk_org, String pk_psndoc, UFLiteralDate beginDate,
			UFLiteralDate endDate, boolean isSettled, boolean isLeave) throws BusinessException {
		String strSQL = "pk_org='" + pk_org + "' and pk_psndoc='" + pk_psndoc + "' and expiredate between '"
				+ beginDate.toString() + "' and '" + endDate.toString() + "' and dr=0 "
				+ (isLeave ? "" : (isSettled ? " and isnull(issettled,'~')='Y' " : ""));
		Collection<LeaveExtraRestVO> vos = this.getBaseDao().retrieveByClause(LeaveExtraRestVO.class, strSQL);

		if (vos != null && vos.size() > 0) {
			return vos.toArray(new LeaveExtraRestVO[0]);
		}

		return new LeaveExtraRestVO[0];
	}

	@Override
	public OTBalanceDetailVO[] getLeaveExtByType(String pk_org, String pk_psndoc, String queryYear,
			UFLiteralDate beginDate, UFLiteralDate endDate, String pk_leavetypecopy, boolean isSettled)
			throws BusinessException {
		List<OTBalanceDetailVO> detailVOs = new ArrayList<OTBalanceDetailVO>();

		if (!StringUtils.isEmpty(pk_psndoc)) {
			LeaveExtraRestVO[] extvos = getLeaveExtraRestVOsByPsnDate(pk_org, pk_psndoc, beginDate, endDate, isSettled,
					false);
			LeaveRegVO[] leaveRegVOs = OTLeaveBalanceUtils.getLeaveRegByPsnDate(pk_org, pk_psndoc, beginDate, endDate,
					pk_leavetypecopy);
			String pk_otleavetype = SysInitQuery.getParaString(pk_org, "TWHRT10");
			// 已休
			UFDouble spentAmount = UFDouble.ZERO_DBL;
			if (pk_otleavetype != null && pk_otleavetype.equals(pk_leavetypecopy)) {
				if (leaveRegVOs != null && leaveRegVOs.length > 0) {
					for (LeaveRegVO lrvo : leaveRegVOs) {
						spentAmount = spentAmount.add(lrvo.getLeavehour());
					}
				}
			}

			// 创建表行
			for (LeaveExtraRestVO vo : extvos) {
				OTBalanceDetailVO detailVo = new OTBalanceDetailVO();
				detailVo.setPk_otleavebalance(pk_psndoc);
				detailVo.setSourcetype(SoureBillTypeEnum.EXTRALEAVE.toIntValue());
				detailVo.setPk_sourcebill(vo.getPk_extrarest());
				detailVo.setCalendar(vo.getBilldate());
				detailVo.setBillhours(vo.getChangedayorhour());
				if (spentAmount.doubleValue() >= vo.getChangedayorhour().doubleValue()) {
					detailVo.setConsumedhours(vo.getChangedayorhour());
					spentAmount.sub(vo.getChangedayorhour());
				} else {
					detailVo.setConsumedhours(spentAmount);
					spentAmount = UFDouble.ZERO_DBL;
				}

				detailVo.setFrozenhours(UFDouble.ZERO_DBL);
				detailVo.setCloseflag(new UFBoolean(detailVo.getBillhours().doubleValue() == detailVo
						.getConsumedhours().doubleValue()));
				detailVo.setPk_balancedetail(vo.getPk_extrarest());
				detailVOs.add(detailVo);
			}
		}
		return detailVOs.toArray(new OTBalanceDetailVO[0]);
	}

	@Override
	public void unSettleByPsn(String pk_psndoc) throws BusinessException {
		if (StringUtils.isEmpty(pk_psndoc)) {
			throw new BusinessException("反結算錯誤：未指定進行反結算的人員。");
		}

		// 反結算日期
		String lastSettledDate = (String) this.getBaseDao().executeQuery(
				"select max(" + LeaveExtraRestVO.EXPIREDATE + ") from " + LeaveExtraRestVO.getDefaultTableName()
						+ " where " + LeaveExtraRestVO.ISSETTLED + "='Y' and " + LeaveExtraRestVO.PK_PSNDOC + "='"
						+ pk_psndoc + "'", new ColumnProcessor());
		if (StringUtils.isEmpty(lastSettledDate)) {
			throw new BusinessException("反結算錯誤：未找到指定人員的可反結算外加補休記錄。");
		}

		// 反結算
		this.getBaseDao().executeUpdate(
				"update " + LeaveExtraRestVO.getDefaultTableName() + " set " + LeaveExtraRestVO.ISSETTLED + "='N', "
						+ LeaveExtraRestVO.SETTLEDATE + "=null where  " + LeaveExtraRestVO.PK_PSNDOC + "='" + pk_psndoc
						+ "' and " + LeaveExtraRestVO.EXPIREDATE + "= '" + lastSettledDate + "'");
	}

	@Override
	public OTLeaveBalanceVO[] getLeaveExtHoursByType(String pk_org, String[] pk_psndocs, String queryYear,
			String pk_leavetypecopy, boolean isSettled) throws BusinessException {
		List<OTLeaveBalanceVO> headvos = new ArrayList<OTLeaveBalanceVO>();

		Map<String, UFLiteralDate> psnWorkStartDate = OTLeaveBalanceUtils.getPsnWorkStartDateMap(pk_org, pk_psndocs,
				queryYear, pk_leavetypecopy);

		UFDouble dayHoursRate = getDayHourRate(pk_org, pk_leavetypecopy);

		if (psnWorkStartDate != null && psnWorkStartDate.keySet().size() > 0) {
			for (String pk_psndoc : psnWorkStartDate.keySet()) {
				if (psnWorkStartDate.get(pk_psndoc) != null) {

					UFLiteralDate beginDate = null;
					UFLiteralDate endDate = null;
					UFLiteralDate settleddate = null;

					beginDate = OTLeaveBalanceUtils.getBeginDateByWorkAgeStartDate(queryYear, psnWorkStartDate,
							pk_psndoc);
					endDate = OTLeaveBalanceUtils.getEndDateByWorkAgeStartDate(queryYear, psnWorkStartDate, pk_psndoc);

					UFDouble totalAmount = UFDouble.ZERO_DBL; // 享有
					UFDouble spentAmount = UFDouble.ZERO_DBL;// 已休
					UFDouble remainAmount = UFDouble.ZERO_DBL;// 剩余
					UFDouble frozenAmount = UFDouble.ZERO_DBL;// 冻结
					UFDouble useableAmount = UFDouble.ZERO_DBL; // 可用
					UFDouble unusableAmount = UFDouble.ZERO_DBL;// 不可用

					LeaveExtraRestVO[] extvos = getLeaveExtraRestVOsByPsnDate(pk_org, pk_psndoc, beginDate, endDate,
							isSettled, false);
					LeaveRegVO[] leaveRegVOs = OTLeaveBalanceUtils.getLeaveRegByPsnYear(pk_org, pk_psndoc, queryYear,
							pk_leavetypecopy);
					// 享有
					for (LeaveExtraRestVO vo : extvos) {
						UFDouble curAmount = OTLeaveBalanceUtils.getUFDouble(vo.getChangedayorhour()).multiply(
								dayHoursRate);
						totalAmount = totalAmount.add(curAmount);

						if (UFBoolean.TRUE.equals(vo.getIssettled())) {
							unusableAmount = unusableAmount.add(curAmount);
							settleddate = vo.getSettledate();
						}
					}

					if (!totalAmount.equals(UFDouble.ZERO_DBL)) {
						// 已休
						if (leaveRegVOs != null && leaveRegVOs.length > 0) {
							for (LeaveRegVO lrvo : leaveRegVOs) {
								spentAmount = spentAmount.add(OTLeaveBalanceUtils.getUFDouble(lrvo.getLeavehour())
										.multiply(dayHoursRate));
							}
						}

						if (unusableAmount.doubleValue() > 0) {
							unusableAmount = unusableAmount.sub(spentAmount);
						}

						// 剩餘
						remainAmount = remainAmount.add(totalAmount.sub(spentAmount));
						// 可用
						useableAmount = useableAmount.add(totalAmount.sub(spentAmount).sub(frozenAmount)).sub(
								unusableAmount);

						if (!totalAmount.equals(UFDouble.ZERO_DBL) || !spentAmount.equals(UFDouble.ZERO_DBL)
								|| !remainAmount.equals(UFDouble.ZERO_DBL) || !frozenAmount.equals(UFDouble.ZERO_DBL)
								|| !useableAmount.equals(UFDouble.ZERO_DBL)) {
							// 创建表头
							OTLeaveBalanceVO headVo = OTLeaveBalanceUtils.createNewHeadVO(pk_org, pk_psndoc,
									totalAmount, spentAmount, remainAmount, frozenAmount, useableAmount);
							headVo.setQstartdate(beginDate);
							headVo.setQenddate(endDate);
							headVo.setSettleddate(settleddate);
							headvos.add(headVo);
						}
					}
				}
			}
		}

		return headvos.toArray(new OTLeaveBalanceVO[0]);
	}

	@SuppressWarnings("unchecked")
	private UFDouble getDayHourRate(String pk_org, String pk_leavetypecopy) throws DAOException {
		// 按休假類型定義的單位決定按日或按小時計算
		// Collection<TimeRuleVO> timerule =
		// this.getBaseDao().retrieveByClause(TimeRuleVO.class,
		// "pk_org='" + pk_org + "'");
		UFDouble dayHoursRate = new UFDouble(1);
		// if (timerule != null && timerule.size() > 0) {
		// LeaveTypeCopyVO leavetype = (LeaveTypeCopyVO)
		// this.getBaseDao().retrieveByPK(LeaveTypeCopyVO.class,
		// pk_leavetypecopy);
		// dayHoursRate = leavetype.getTimeitemunit() == 1 ?
		// timerule.toArray(new TimeRuleVO[0]).clone()[0]
		// .getDaytohour() : new UFDouble(1);
		// }
		return dayHoursRate;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public OTLeaveBalanceVO[] getLeaveExtHoursByType_MT(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate, String pk_leavetypecopy, boolean isSettled, boolean isLeave)
			throws BusinessException {
		// 返回值
		List<OTLeaveBalanceVO> mt_LEAVEEXT_ReturnValues = new ArrayList<OTLeaveBalanceVO>();

		if (pk_psndocs != null && pk_psndocs.length > 0) {
			// 由调用线程处处理该任务-移交给主线程执行
			RejectedExecutionHandler CallerRunsPolicyHandler = new ThreadPoolExecutor.CallerRunsPolicy();

			// 線程池
			ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 20, 20, TimeUnit.MINUTES,
					new SynchronousQueue(), new LeaveExtCalculatorThreadFactory(), CallerRunsPolicyHandler);

			// 返回值池
			List<Future<OTLeaveBalanceVO[]>> futureDatas = new ArrayList<Future<OTLeaveBalanceVO[]>>();

			int count = 1;
			for (String pk_psndoc : pk_psndocs) {
				RunnableLeaveExtCalculator calculator = new RunnableLeaveExtCalculator();
				calculator.setPk_org(pk_org);
				calculator.setPk_psndoc(pk_psndoc);
				calculator.setBeginDate(beginDate);
				calculator.setEndDate(endDate);
				calculator.setPk_leavetypecopy(pk_leavetypecopy);
				calculator.setSettled(isSettled);
				calculator.setLeave(isLeave);
				calculator.setBaseDao(this.getBaseDao());
				calculator.setProxyAgent(InvocationInfoProxy.getInstance());
				calculator.setCurrentcount(count++);
				calculator.setTotalcount(pk_psndocs.length);

				Logger.error("---WNC-MULTIPUL-THREADS-LEAVEEXT-THREAD-POOL-SIZE: ["
						+ String.valueOf(threadPoolExecutor.getPoolSize()) + "]-SERIALS ["
						+ calculator.getCurrentcount() + "/" + pk_psndocs.length + "]--");

				// 提交服務，註冊返回值池
				futureDatas.add(threadPoolExecutor.submit(calculator));
			}
			try {

				for (Future<OTLeaveBalanceVO[]> futureData : futureDatas) {
					OTLeaveBalanceVO[] data = futureData.get();

					if (data != null && data.length > 0) {
						for (OTLeaveBalanceVO vo : data) {
							mt_LEAVEEXT_ReturnValues.add(vo);
						}
					}
				}
			} catch (InterruptedException | ExecutionException e) {
				throw new BusinessException(e.getMessage());
			} finally {
				threadPoolExecutor.shutdown();
			}
		}

		return mt_LEAVEEXT_ReturnValues.toArray(new OTLeaveBalanceVO[0]);
	}

	static class LeaveExtCalculatorThreadFactory implements ThreadFactory {
		private AtomicInteger threadNum = new AtomicInteger(0);

		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setName("LBTC-THREAD-NO: [" + String.valueOf(threadNum.incrementAndGet()) + "]");
			return t;
		}
	}

	class RunnableLeaveExtCalculator implements Callable<OTLeaveBalanceVO[]> {
		private String pk_org;
		private String pk_psndoc;
		private UFLiteralDate beginDate;
		private UFLiteralDate endDate;
		private String pk_leavetypecopy;
		private boolean isSettled;
		private boolean isLeave;
		private BaseDAO baseDao;
		private int currentcount;
		private int totalcount;

		@Override
		public OTLeaveBalanceVO[] call() throws Exception {
			OTLeaveBalanceVO[] ret = null;

			try {
				Logger.error("---WNC-MULTIPUL-THREADS-LEAVEEXT-THREAD-[" + Thread.currentThread().getName()
						+ "]-COUNT [" + String.valueOf(currentcount) + " / " + String.valueOf(totalcount) + "]-START--");
				LeaveExtraRestServiceImpl newImpl = new LeaveExtraRestServiceImpl();
				newImpl.setBaseDao(this.getBaseDao());
				ret = newImpl.getLeaveExtHoursByType(this.getPk_org(), new String[] { this.getPk_psndoc() },
						this.getBeginDate(), this.getEndDate(), this.getPk_leavetypecopy(), this.isSettled(),
						this.isLeave());

			} catch (BusinessException e) {
				Logger.error("---WNC-MULTIPUL-THREADS-LEAVEEXT-THREAD-[" + Thread.currentThread().getName()
						+ "]-ERROR---");
				Logger.error(e.toString());
			} finally {
				Logger.error("---WNC-MULTIPUL-THREADS-LEAVEEXT-THREAD-[" + Thread.currentThread().getName()
						+ "]-COUNT [" + String.valueOf(currentcount) + " / " + String.valueOf(totalcount) + "]-END--");
			}

			if (ret == null || ret.length == 0) {
				ret = new OTLeaveBalanceVO[] { null };
			}

			return ret;
		}

		public void setProxyAgent(InvocationInfoProxy proxyAgent) {
			InvocationInfoProxy.getInstance().setBizCenterCode(proxyAgent.getBizCenterCode());
			InvocationInfoProxy.getInstance().setBizDateTime(proxyAgent.getBizDateTime());
			InvocationInfoProxy.getInstance().setBusiAction(proxyAgent.getBusiAction());
			InvocationInfoProxy.getInstance().setCallId(proxyAgent.getCallId());
			InvocationInfoProxy.getInstance().setDeviceId(proxyAgent.getDeviceId());
			InvocationInfoProxy.getInstance().setGroupId(proxyAgent.getGroupId());
			InvocationInfoProxy.getInstance().setGroupNumber(proxyAgent.getGroupNumber());
			InvocationInfoProxy.getInstance().setHyCode(proxyAgent.getHyCode());
			InvocationInfoProxy.getInstance().setLangCode(proxyAgent.getLangCode());
			InvocationInfoProxy.getInstance().setLogLevel(proxyAgent.getLogLevel());
			InvocationInfoProxy.getInstance().setRunAs(proxyAgent.getRunAs());
			InvocationInfoProxy.getInstance().setSysid(proxyAgent.getSysid());
			InvocationInfoProxy.getInstance().setTimeZone(proxyAgent.getTimeZone());
			InvocationInfoProxy.getInstance().setUserCode(proxyAgent.getUserCode());
			InvocationInfoProxy.getInstance().setUserDataSource(proxyAgent.getUserDataSource());
			InvocationInfoProxy.getInstance().setUserId(proxyAgent.getUserId());
		}

		public String getPk_org() {
			return pk_org;
		}

		public void setPk_org(String pk_org) {
			this.pk_org = pk_org;
		}

		public String getPk_psndoc() {
			return pk_psndoc;
		}

		public void setPk_psndoc(String pk_psndoc) {
			this.pk_psndoc = pk_psndoc;
		}

		public UFLiteralDate getBeginDate() {
			return beginDate;
		}

		public void setBeginDate(UFLiteralDate beginDate) {
			this.beginDate = beginDate;
		}

		public UFLiteralDate getEndDate() {
			return endDate;
		}

		public void setEndDate(UFLiteralDate endDate) {
			this.endDate = endDate;
		}

		public String getPk_leavetypecopy() {
			return pk_leavetypecopy;
		}

		public void setPk_leavetypecopy(String pk_leavetypecopy) {
			this.pk_leavetypecopy = pk_leavetypecopy;
		}

		public boolean isSettled() {
			return isSettled;
		}

		public void setSettled(boolean isSettled) {
			this.isSettled = isSettled;
		}

		public boolean isLeave() {
			return isLeave;
		}

		public void setLeave(boolean isLeave) {
			this.isLeave = isLeave;
		}

		public BaseDAO getBaseDao() {
			return baseDao;
		}

		public void setBaseDao(BaseDAO baseDao) {
			this.baseDao = baseDao;
		}

		public int getCurrentcount() {
			return currentcount;
		}

		public void setCurrentcount(int currentcount) {
			this.currentcount = currentcount;
		}

		public int getTotalcount() {
			return totalcount;
		}

		public void setTotalcount(int totalcount) {
			this.totalcount = totalcount;
		}

	}

}
