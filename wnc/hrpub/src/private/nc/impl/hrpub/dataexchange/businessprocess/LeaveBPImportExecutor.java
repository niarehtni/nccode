package nc.impl.hrpub.dataexchange.businessprocess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import nc.bs.bd.cache.CacheProxy;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.impl.hrpub.dataexchange.DataImportExecutor;
import nc.itf.hrpub.IDataExchangeExternalExecutor;
import nc.itf.ta.ILeaveBalanceManageService;
import nc.itf.ta.ILeaveOffManageMaintain;
import nc.itf.ta.ILeaveRegisterManageMaintain;
import nc.itf.ta.ILeaveRegisterQueryMaintain;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.algorithm.BillProcessHelper;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.IDateScope;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.LeaveCheckResult;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.SplitBillResult;
import nc.vo.ta.leaveoff.AggLeaveoffVO;
import nc.vo.ta.leaveoff.LeaveoffVO;
import nc.vo.ta.psncalendar.PsnCalendarVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

public class LeaveBPImportExecutor extends DataImportExecutor implements IDataExchangeExternalExecutor {
	private Map<String, LeaveRegVO> rowNCVO;
	private Map<String, String> rowLeaveType;

	public LeaveBPImportExecutor() throws BusinessException {
		super();
		// TODO 自动生成的构造函数存根
	}

	@Override
	public Object getBizEntityID() throws BusinessException {
		// TODO 自动生成的方法存根
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void beforeUpdate() throws BusinessException {

		if (this.getNcValueObjects() != null && this.getNcValueObjects().size() > 0) {
			String rowNo = "";
			for (Map<String, Object> rowNCMap : this.getNcValueObjects()) {
				try {
					rowNo = rowNCMap.keySet().toArray(new String[0])[0].split(":")[0];
					Integer caseid = new UFDouble((String) rowNCMap.get("CASEID")).intValue();
					LeaveRegVO vo = new LeaveRegVO();

					vo.setBillsource(0);
					vo.setCreationtime(new UFDateTime());
					vo.setCreator(this.getCuserid());
					vo.setIslactation(UFBoolean.FALSE);
					// 去掉\r\n
					vo.setLeaveremark(replaceBlank((String) rowNCMap.get(rowNo + ":leaveremark")));
					vo.setLeavebegintime(new UFDateTime(getDateString((String) rowNCMap.get(rowNo + ":leavebegintime"))));
					vo.setLeavebegindate(new UFLiteralDate(getDateString((String) rowNCMap.get(rowNo
							+ ":leavebegintime"))));
					vo.setLeaveendtime(new UFDateTime(getDateString((String) rowNCMap.get(rowNo + ":leaveendtime"))));
					vo.setLeaveenddate(new UFLiteralDate(getDateString((String) rowNCMap.get(rowNo + ":leaveendtime"))));
					vo.setBacktime(rowNCMap.get(rowNo + ":backtime") == null ? null : new UFDateTime(
							getDateString((String) rowNCMap.get(rowNo + ":backtime"))));
					vo.setPk_psndoc((String) rowNCMap.get(rowNo + ":pk_psndoc"));
					vo.setLeaveindex(1);
					vo.setFreezedayorhour(UFDouble.ZERO_DBL);
					vo.setRealdayorhour(UFDouble.ZERO_DBL);
					vo.setRestdayorhour(UFDouble.ZERO_DBL);

					vo.setEffectivedate(BillProcessHelper.getShiftRegDateByLeave(vo));

					PsndocDismissedValidator dismChecker = new PsndocDismissedValidator(vo.getLeavebegintime());
					dismChecker.validate(vo.getPk_psndoc(), vo.getLeavebegindate());

					/*
					 * Collection<PeriodVO> periods =
					 * this.getBaseDAO().retrieveByClause(PeriodVO.class, "'" +
					 * vo.getLeavebegindate().toString() +
					 * "' between begindate and enddate"); if (periods != null
					 * && periods.size() > 0) {
					 * //vo.setLeaveyear(periods.toArray(new
					 * PeriodVO[0])[0].getTimeyear());
					 * //vo.setLeavemonth(periods.toArray(new
					 * PeriodVO[0])[0].getTimemonth()); }
					 */
					if (null != rowNCMap.get(rowNo + ":leavemonth")) {
						vo.setLeavemonth((String) rowNCMap.get(rowNo + ":leavemonth"));
					} else {
						vo.setLeavemonth(null);
					}

					vo.setLeaveyear((String) rowNCMap.get(rowNo + ":leaveyear"));
					vo.setPk_group(this.getPk_group());
					vo.setPk_org(this.getPk_org());
					vo.setPk_org_v(this.getPk_org_v());
					// 审核时间 ssx modified for Mapped Approve time
					vo.setApprove_time(new UFDateTime((String) rowNCMap.get(rowNo + ":approve_time")));

					// ssx added for Taiwan new law
					// request by ward on 2018-05-25
					// vo.setEffectivedate(new
					// UFLiteralDate().before(vo.getLeaveenddate()) ?
					// vo.getLeaveenddate()
					// : new UFLiteralDate());

					// PK_LEAVETYPE
					String leaveType = this.getTimeItemByCode(this.getRowLeaveType().get(rowNo));
					vo.setPk_leavetype(leaveType);

					//#33336 & #33620 補上哺乳假M11，帶入預設值 By Jimmy20200320
					if(leaveType != null){
						if(leaveType.equals("1002Z710000000021ZM3")){
							vo.setIslactation(UFBoolean.TRUE);
							vo.setLactationholidaytype(1);
							vo.setLactationhour(new UFDouble(60.0D));
						}
					}
					// PK_LEAVETYPECOPY
					String leaveTypeCopy = this.getTimeItemCopyByOrg(leaveType, this.getPk_org());
					// 通过休假类别查找休假时长的计量单位
					List<LeaveTypeCopyVO> leavetypevo = (List<LeaveTypeCopyVO>) this.getBaseDAO().retrieveByClause(
							LeaveTypeCopyVO.class, "pk_timeitemcopy='" + leaveTypeCopy + "'");
					// 判断计量单位是天还是小时(0-天，1-小时)
					if (null != leavetypevo && leavetypevo.get(0).getTimeitemunit() == 0) {
						vo.setLeavehour(new UFDouble((String) rowNCMap.get(rowNo + ":leavehour")).div(8));
					} else {
						vo.setLeavehour(new UFDouble((String) rowNCMap.get(rowNo + ":leavehour")));
					}
					vo.setLength(vo.getLeavehour());
					vo.setPk_leavetypecopy(leaveTypeCopy); 

					String startDate = vo.getLeavebegindate().toString();
					startDate = dismChecker.getShiftRegDateByDateTime(vo.getPk_psndoc(), vo.getLeavebegintime(),
							new UFLiteralDate(startDate)).toString();
					Map<String, Object> psnjob = this.getPsnjob(vo.getPk_psndoc(), startDate);
					if (psnjob != null && psnjob.size() > 0 && !StringUtils.isEmpty((String) psnjob.get("pk_psnjob"))) {
						vo.setPk_psnjob((String) psnjob.get("pk_psnjob"));
						vo.setPk_dept_v((String) psnjob.get("pk_dept_v"));
						vo.setPk_psnorg((String) psnjob.get("pk_psnorg"));
					} else {
						throw new BusinessException("未找到員工工作記錄");
					}

					this.getRowNCVO().put(rowNo + ":" + caseid, vo);
				} catch (Exception e) {
					this.getErrorMessages().put(rowNo, e.getMessage());
				}
			}
		}
	}

	@Override
	public void afterUpdate() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void afterConvert() throws BusinessException {
		if (this.getJsonValueObjects() != null && this.getJsonValueObjects().size() > 0) {
			for (Map<String, Object> jsonobj : this.getJsonValueObjects()) {
				String rowno = "";
				if (jsonobj != null && jsonobj.size() > 0) {
					for (Entry<String, Object> obj : jsonobj.entrySet()) {
						String jsonPropName = obj.getKey().toUpperCase();
						if (jsonPropName.equals("ROWNO")) {
							rowno = (String) obj.getValue();
						}
					}
				}
				for (Map<String, Object> rowNCMap : this.getNcValueObjects()) {
					Map<String, Object> ncObj = rowNCMap;
					String rowNo = rowNCMap.keySet().toArray(new String[0])[0].split(":")[0];
					if (rowno.equals(rowNo)) {
						ncObj.put("CASEID", jsonobj.get("CASEID"));
					}
				}
			}
		}
	}

	@Override
	public void beforeConvert() throws BusinessException {
		if (this.getJsonValueObjects() != null && this.getJsonValueObjects().size() > 0) {
			for (Map<String, Object> jsonobj : this.getJsonValueObjects()) {
				String rowNo = "";
				String rowType = "";
				for (Entry<String, Object> entry : jsonobj.entrySet()) {
					if (entry.getKey().equals("ROWNO")) {
						rowNo = (String) entry.getValue();
					} else if (entry.getKey().equals("HR_LEAVEID")) {
						rowType = (String) entry.getValue();
					}
				}

				if (StringUtils.isEmpty(rowNo)) {
					throw new BusinessException("匯入資料ROWNO不能為空");
				}

				this.getRowLeaveType().put(rowNo, rowType);
			}
		}
	}

	@Override
	public void beforeQuery() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void afterQuery() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void doUpdateByBP() throws BusinessException {
		if (this.getRowNCVO() != null && this.getRowNCVO().size() > 0) {
			List<String> arraylist = new ArrayList<String>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (Entry<String, LeaveRegVO> rowData : rowNCVO.entrySet()) {
				arraylist.add(rowData.getKey());
			}
			// 通过caseid进行排序
			ListSort(arraylist);
			for (String s : arraylist) {
				// for (Entry<String, LeaveRegVO> rowData :
				// this.getRowNCVO().entrySet()) {
				try {
					// 判断传入的数据是否是销假数据
					LeaveRegVO lvo = this.getRowNCVO().get(s);
					if (lvo.getLeavehour().doubleValue() < 0) {
						//#33695 查詢請假單，需要加入leavebegintime & leaveendtime作條件 By Jimmy20200318
						Collection<LeaveRegVO> leaveRegvos = this.getBaseDAO().retrieveByClause(LeaveRegVO.class,
								"pk_psndoc = '" + lvo.getPk_psndoc() + "' " + 
								"AND leavebegintime <= '" + lvo.getLeavebegintime().toString().replace("+", " ") + "' " + 
								"AND leaveendtime >= '" + lvo.getLeaveendtime().toString().replace("+", " ") + "' " 
								);
						//#33695 By Jimmy20200319
						if(leaveRegvos == null || leaveRegvos.size() == 0 ){
							throw new BusinessException("未找到期間內的休假登記單");
						}
						for (LeaveRegVO lgvo : leaveRegvos) {
							if (
									(
										lgvo.getLeavebegintime().toString().equals(lvo.getLeavebegintime().toString()) || 
										lgvo.getLeavebegintime().before(lvo.getLeavebegintime())
									) && 
									(
										lgvo.getLeaveendtime().toString().equals(lvo.getLeaveendtime().toString()) || 
										lgvo.getLeaveendtime().after(lvo.getLeavebegintime())
									) && 
									lgvo.getPk_leavetype().equals(lvo.getPk_leavetype()) && !lgvo.getIsleaveoff().booleanValue()
								) 
							{
								// 先在tbm_leaveoff中生成一张单据
								AggLeaveoffVO aggvo = new AggLeaveoffVO();
								aggvo.setIsBillLock(true);
								aggvo.setSendMessage(false);
								//
								LeaveoffVO leaveoff = new LeaveoffVO();
								leaveoff.setPk_psnjob(lvo.getPk_psnjob());
								leaveoff.setApply_date(new UFLiteralDate(lvo.getCreationtime().toString()));
								leaveoff.setLeaveenddate(lvo.getLeaveenddate());
								leaveoff.setPk_billtype("6406");
								leaveoff.setScope_start_datetime(lvo.getLeavebegintime());
								leaveoff.setPk_psnorg(lvo.getPk_psnorg());
								leaveoff.setRegbegintimecopy(lvo.getLeavebegintime());
								leaveoff.setRegenddatecopy(lvo.getEnddate());
								leaveoff.setApprove_state(1);
								leaveoff.setApprove_time(lvo.getApprove_time());
								leaveoff.setApprover(lgvo.getCreator());
								leaveoff.setBillmaker("NC_USER0000000000000");
								leaveoff.setRegendtimecopy(lvo.getLeaveendtime());
								leaveoff.setPk_group(lvo.getPk_group());
								leaveoff.setBegindate(lvo.getBegindate());
								leaveoff.setEnddate(lvo.getEnddate());
								leaveoff.setPk_psndoc(lvo.getPk_psndoc());
								leaveoff.setPk_leavereg(lgvo.getPk_leavereg());
								leaveoff.setPk_leavetypecopy(lvo.getPk_leavetypecopy());
								leaveoff.setIslactation(UFBoolean.FALSE);
								leaveoff.setLeaveendtime(lvo.getLeaveendtime());
								leaveoff.setPk_org(lvo.getPk_org());
								leaveoff.setScope_end_datetime(leaveoff.getLeaveendtime());
								leaveoff.setDr(0);
								leaveoff.setAppBill(false);
								leaveoff.setLeavebegintime(lvo.getLeavebegintime());
								leaveoff.setRegleavehourcopy(lgvo.getLeavehour());
								leaveoff.setLeavebegindate(lvo.getLeavebegindate());
								leaveoff.setContainsLastSecond(false);
								leaveoff.setRegbegindatecopy(lvo.getBegindate());
								leaveoff.setPk_leavetype(lvo.getPk_leavetype());
								leaveoff.setBillBeginDate(lvo.getBegindate());
								leaveoff.setReallyleavehour(lgvo.getLeavehour().add(lvo.getLeavehour()));
								leaveoff.setDifferencehour(leaveoff.getReallyleavehour().sub(lgvo.getLeavehour()));
								leaveoff.setLength(leaveoff.getReallyleavehour());
								leaveoff.setBill_code(UUID.randomUUID().toString().replace("-", "").toLowerCase()
										.substring(12, 32));
								if (lvo.getLeavebegintime().toString().equals(lgvo.getLeavebegintime().toString())
										&& ((lvo.getLeaveendtime().before(lgvo.getLeaveendtime()) || lvo
												.getLeaveendtime().toString().equals(lgvo.getLeaveendtime().toString())))) {
									leaveoff.setScope_start_datetime(lvo.getLeaveendtime());
									leaveoff.setLeavebegintime(lvo.getLeaveendtime());
									leaveoff.setScope_end_datetime(lgvo.getLeaveendtime());
									leaveoff.setLeaveendtime(lgvo.getLeaveendtime());
								} else if ((lvo.getLeavebegintime().toString()
										.equals(lgvo.getLeavebegintime().toString()) || lvo.getLeavebegintime().after(
										lgvo.getLeavebegintime()))
										&& (lvo.getLeaveendtime().toString().equals(lgvo.getLeaveendtime().toString()))) {
									leaveoff.setScope_start_datetime(lgvo.getLeavebegintime());
									leaveoff.setLeavebegintime(lgvo.getLeavebegintime());
									leaveoff.setScope_end_datetime(lvo.getLeavebegintime());
									leaveoff.setLeaveendtime(lvo.getLeavebegintime());
								} else {
									throw new BusinessException("销假日期有误");
								}
								aggvo.setParentVO(leaveoff);
								ILeaveOffManageMaintain leaveoffService = NCLocator.getInstance().lookup(
										ILeaveOffManageMaintain.class);
								leaveoffService.insertData(aggvo);

								// 销假开始日期=休假开始日期 && 销假结束日期<=休假结束日期

								if (lvo.getLeavebegintime().toString().equals(lgvo.getLeavebegintime().toString())
										&& ((lvo.getLeaveendtime().before(lgvo.getLeaveendtime()) || lvo
												.getLeaveendtime().toString().equals(lgvo.getLeaveendtime().toString())))) {
									ILeaveRegisterManageMaintain leaveregService = NCLocator.getInstance().lookup(
											ILeaveRegisterManageMaintain.class);
									leaveregService.leaveOff(lgvo, lvo.getLeaveendtime(), lgvo.getLeaveendtime());
								} else if ((lvo.getLeavebegintime().toString()
										.equals(lgvo.getLeavebegintime().toString()) || lvo.getLeavebegintime().after(
										lgvo.getLeavebegintime()))
										&& (lvo.getLeaveendtime().toString().equals(lgvo.getLeaveendtime().toString()))) {
									// 销假开始日期<=休假开始日期 && 销假结束日期=休假结束日期
									ILeaveRegisterManageMaintain leaveregService = NCLocator.getInstance().lookup(
											ILeaveRegisterManageMaintain.class);
									leaveregService.leaveOff(lgvo, lgvo.getLeavebegintime(), lvo.getLeavebegintime());
								} else {
									// 销假开始日期<休假开始日期 && 销假结束日期>休假结束日期
									// throw new
									// BusinessException(ResHelper.getString("6017psndoc",
									// "06017psndoc0131"));
									throw new BusinessException("销假日期有误");
								}

								// 销假单--在tbm_leavereg中更新数据
								lgvo.setLeaveremark(lgvo.getLeaveremark() + ";销假原因：" + lvo.getLeaveremark());
								lgvo.setBacktime(lvo.getApprove_time());
								lgvo.setLeavehour(leaveoff.getReallyleavehour());
								// 判断销假时间与休假时间的关系
								this.getBaseDAO().updateVO(lgvo);
							}else {
								//#33695 By Jimmy20200313
								throw new BusinessException("未找到對應的休假登記單");
							}
						}
					} else {
						// 休假单
						LeaveCheckResult<LeaveRegVO> checkResult = this.getVOQueryService().checkWhenSave(
								rowNCVO.get(s));
						SplitBillResult<LeaveRegVO> splitResult = checkResult.getSplitResult();
						LeaveRegVO[] vos = splitResult.getSplitResult();
						IDateScope maxDateScope = DateScopeUtils.getMaxRangeDateScope(vos);
						ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(
								ITBMPsndocQueryService.class);
						Map<String, List<TBMPsndocVO>> psndocMap = psndocService.queryTBMPsndocMapByPsndocs(null,
								StringPiecer.getStrArrayDistinct(vos, LeaveRegVO.PK_PSNDOC),
								maxDateScope.getBegindate(), maxDateScope.getEnddate(), false);

						if (MapUtils.isEmpty(psndocMap))
							continue;
						for (LeaveRegVO vo : vos) {
							if (vo.getLeaveindex() == null)
								vo.setLeaveindex(1);
							if (vo.getIsleaveoff() == null)
								vo.setIsleaveoff(UFBoolean.FALSE);

							PsndocDismissedValidator dismChecker = new PsndocDismissedValidator(vo.getLeavebegintime());
							String startDate = vo.getLeavebegindate().toString();
							startDate = dismChecker.getShiftRegDateByDateTime(vo.getPk_psndoc(),
									vo.getLeavebegintime(), new UFLiteralDate(startDate)).toString();
							Map<String, Object> psnjob = this.getPsnjob(vo.getPk_psndoc(), startDate);
							if (psnjob != null && psnjob.size() > 0
									&& !StringUtils.isEmpty((String) psnjob.get("pk_psnjob"))) {
								vo.setPk_psnjob((String) psnjob.get("pk_psnjob"));
								vo.setPk_dept_v((String) psnjob.get("pk_dept_v"));
								vo.setPk_psnorg((String) psnjob.get("pk_psnorg"));
							} else {
								throw new BusinessException("未找到員工工作記錄");
							}

							TBMPsndocVO psndocVO = TBMPsndocVO.findIntersectionVO(psndocMap.get(vo.getPk_psndoc()),
									getShiftRegDateByLeaveReg(vo).toString());
							if (psndocVO == null){
								throw new BusinessException(ResHelper.getString("6017psndoc", "06017psndoc0131",
										psndocService.getPsnName(vo.getPk_psndoc())));
							}else{
								vo.setPk_adminorg(psndocVO.getPk_adminorg());
								vo.setLeavehour(rowNCVO.get(s).getLeavehour());
							}
						}
						this.getBaseDAO().insertVOArray(vos);

						// insertLeaveRegHistoryVO(vos, VOStatus.UPDATED);// add
						// by
						// ward
						// 20180510
						// ssx added for Taiwan new Law
						NCLocator.getInstance().lookup(ISegDetailService.class).regOvertimeSegDetailConsume(vos);
						// end

						// 假期計算
						NCLocator.getInstance().lookup(ILeaveBalanceManageService.class)
								.queryAndCalLeaveBalanceVO(vos[0].getPk_org(), vos);
						CacheProxy.fireDataInserted(LeaveRegVO.getDefaultTableName());
					}

				} catch (Exception e) {
					this.getErrorMessages().put(s.split(":")[0], e.getMessage());
				}
			}
		}
		// }
	}

	/**
	 * 根據休假核定開始日期查詢休假實際歸屬班次的所屬日期
	 * 
	 * @param vo
	 *            加班登記單
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private UFLiteralDate getShiftRegDateByLeaveReg(LeaveRegVO vo) throws BusinessException {
		UFLiteralDate rtnDate = vo.getBegindate();

		Collection<PsnCalendarVO> psncals = new BaseDAO().retrieveByClause(PsnCalendarVO.class,
				"pk_psndoc='" + vo.getPk_psndoc() + "' and calendar between '"
						+ vo.getLeavebegindate().getDateBefore(3) + "' and '" + vo.getLeavebegindate().getDateAfter(3)
						+ "'");
		if (psncals != null && psncals.size() > 0) {
			for (PsnCalendarVO psncal : psncals) {
				if (psncal.getPk_shift() != null) {
					ShiftVO shiftvo = (ShiftVO) new BaseDAO().retrieveByPK(ShiftVO.class, psncal.getPk_shift());
					if (shiftvo != null) {
						// mod start tank 2019年8月21日17:04:24 前一日,後一日修復
						UFDateTime startDT = new UFDateTime(psncal.getCalendar()
								.getDateAfter(shiftvo.getTimebeginday()).toString()
								+ " " + shiftvo.getTimebegintime());

						UFDateTime endDT = new UFDateTime(psncal.getCalendar().getDateAfter(shiftvo.getTimeendday())
								.toString()
								+ " " + shiftvo.getTimeendtime());
						// end mod
						if (vo.getLeavebegintime().before(endDT) && vo.getLeavebegintime().after(startDT)) {
							rtnDate = psncal.getCalendar();
						}
					}
				}
			}
		}
		return rtnDate;
	}

	/**
	 * 
	 * 插入休假登记历史信息记录
	 * 
	 * @param regVO
	 * @date 20180510
	 * @author ward
	 * @throws BusinessException
	 */
	// private void insertLeaveRegHistoryVO(LeaveRegVO[] regVO, int status)
	// throws BusinessException {
	// for (int i = 0; i < regVO.length; i++) {
	// LeaveRegVO leaveRegVO = regVO[i];
	// LeaveRegHistoryVO regHistoryVO =
	// LeaveRegHistoryVO.toHistoryVO(leaveRegVO);
	// BaseDAO dao = new BaseDAO();
	// // 查询数据库历史记录
	// @SuppressWarnings("unchecked")
	// Collection<LeaveRegHistoryVO> collection =
	// dao.retrieveByClause(LeaveRegHistoryVO.class, "pk_leavereg='"
	// + leaveRegVO.getPk_leavereg() + "' and effectivedate='" +
	// leaveRegVO.getEffectivedate()
	// + "' and isnull(dr,0)=0");
	// if (collection == null || collection.size() < 1) {
	// dao.insertVO(regHistoryVO);
	// } else {
	// LeaveRegHistoryVO[] leaveRegHistoryVOs = collection.toArray(new
	// LeaveRegHistoryVO[0]);
	// for (int j = 0; j < leaveRegHistoryVOs.length; j++) {
	// LeaveRegHistoryVO leaveRegHistoryVO = leaveRegHistoryVOs[j];
	// if (leaveRegHistoryVO.getIscharge().booleanValue()) {
	// throw new BusinessException("今日請假扣款已結算，不允許修改！");
	// }
	// // 刪除原歷史記錄，然後插入新歷史記錄
	// leaveRegHistoryVO.setDr(1);
	// leaveRegHistoryVO.setStatus(VOStatus.UPDATED);
	// if (VOStatus.DELETED == status) {
	// leaveRegHistoryVO.setLeaveenddate(leaveRegHistoryVO.getLeavebegindate());
	// leaveRegHistoryVO.setLeaveendtime(leaveRegHistoryVO.getLeavebegintime());
	// leaveRegHistoryVO.setLeavehour(UFDouble.ZERO_DBL);
	// }
	// }
	// dao.updateVOArray(leaveRegHistoryVOs);
	// dao.insertVO(regHistoryVO);
	// }
	// }
	// }

	@Override
	public void beforeInsertOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void beforeUpdateOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO 自动生成的方法存根

	}

	ILeaveRegisterQueryMaintain qryService = null;

	private ILeaveRegisterQueryMaintain getVOQueryService() {
		if (qryService == null) {
			qryService = (ILeaveRegisterQueryMaintain) NCLocator.getInstance()
					.lookup(ILeaveRegisterQueryMaintain.class);
		}
		return qryService;
	}

	public Map<String, String> getRowLeaveType() {
		if (rowLeaveType == null) {
			rowLeaveType = new HashMap<String, String>();
		}
		return rowLeaveType;
	}

	public void setRowLeaveType(Map<String, String> rowLeaveType) {
		this.rowLeaveType = rowLeaveType;
	}

	public Map<String, LeaveRegVO> getRowNCVO() {
		if (rowNCVO == null) {
			rowNCVO = new HashMap<String, LeaveRegVO>();
		}
		return rowNCVO;
	}

	public void setRowNCVO(Map<String, LeaveRegVO> rowNCVO) {
		this.rowNCVO = rowNCVO;
	}

	public static void ListSort(List<String> list) {
		Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if (Integer.parseInt(o1.split(":")[1]) > Integer.parseInt(o2.split(":")[1])) {
					return 1;
				}
				if (Integer.parseInt(o1.split(":")[1]) == Integer.parseInt(o2.split(":")[1])) {
					return 0;
				}
				return -1;
			}
		});
	}

	public static String replaceBlank(String str) {
		if (str != null) {
			str = str.replaceAll("\\\\r|\\\\n", "");
		}
		return str;
	}

	@Override
	public void doQueryByBP() throws BusinessException {
		// TODO 自動產生的方法 Stub

	}
}
