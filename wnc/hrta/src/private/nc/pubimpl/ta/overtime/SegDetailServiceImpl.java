package nc.pubimpl.ta.overtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uap.oid.OidGenerator;
import nc.hr.utils.InSQLCreator;
import nc.impl.pub.OTLeaveBalanceUtils;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.overtime.ISegdetailMaintain;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hr.temptable.TempTableVO;
import nc.vo.hrwa.wadaysalary.DaySalaryEnum;
import nc.vo.ml.MultiLangUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leaveextrarest.LeaveExtraRestVO;
import nc.vo.ta.overtime.AggSegDetailVO;
import nc.vo.ta.overtime.AggSegRuleVO;
import nc.vo.ta.overtime.CalendarDateTypeEnum;
import nc.vo.ta.overtime.MonthStatOTCalcVO;
import nc.vo.ta.overtime.OTBalanceDetailVO;
import nc.vo.ta.overtime.OTBalanceLeaveVO;
import nc.vo.ta.overtime.OTLeaveBalanceVO;
import nc.vo.ta.overtime.OTSChainNode;
import nc.vo.ta.overtime.OvertimeLimitScopeEnum;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.overtime.OvertimeSettleTypeEnum;
import nc.vo.ta.overtime.QueryValueTypeEnum;
import nc.vo.ta.overtime.SegDetailConsumeVO;
import nc.vo.ta.overtime.SegDetailVO;
import nc.vo.ta.overtime.SegRuleTermVO;
import nc.vo.ta.overtime.SegRuleVO;
import nc.vo.ta.overtime.SoureBillTypeEnum;
import nc.vo.ta.overtime.TaxFlagEnum;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psncalendar.PsnCalendarVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.OverTimeTypeCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;
import nc.vo.wa.itemgroup.DayScopeEnum;
import nc.vo.wa.itemgroup.ItemGroupVO;

import org.apache.commons.lang.StringUtils;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Collections;

public class SegDetailServiceImpl implements ISegDetailService {
	private Map<String, OTSChainNode> allNode = null;
	private BaseDAO baseDao = null;
	private IPeriodQueryService periodService = null;
	private ISegdetailMaintain segMaintain = null;

	@Override
	public void regOvertimeSegDetail(OvertimeRegVO[] sortedOvertimeRegVOs) throws BusinessException {
		if (sortedOvertimeRegVOs != null && sortedOvertimeRegVOs.length > 0) {
			this.getAllNode().clear();

			for (OvertimeRegVO otRegVo : sortedOvertimeRegVOs) {
				UFBoolean isEnabled = new UFBoolean(SysInitQuery.getParaString(otRegVo.getPk_org(), "TBMOTSEG"));
				if (isEnabled == null || !isEnabled.booleanValue()) {
					return;
				}

				String pk_psndoc = otRegVo.getPk_psndoc();

				// 取加班類別
				OverTimeTypeCopyVO otType = (OverTimeTypeCopyVO) this.getBaseDao().retrieveByPK(
						OverTimeTypeCopyVO.class, otRegVo.getPk_overtimetypecopy());

				if (otType.getPk_segrule() != null) {
					// 根據加班類別取分段依據
					AggSegRuleVO ruleAggVO = getSegRuleAggVO(otType.getPk_segrule());

					// 取當前人員全節點（物理鏈表）
					if (!this.getAllNode().containsKey(pk_psndoc)) {
						OTSChainNode psnNode = OTSChainUtils.buildChainNodes(pk_psndoc, null, null, false, false,
								false, false, false);
						this.getAllNode().put(pk_psndoc, psnNode);
						OTSChainUtils.setCachedPsnChainNodes(psnNode);
					}

					try {
						// 校驗加班日是否已存在不同的加班分段依據
						OTSChainNode checkNode = this.getAllNode().get(pk_psndoc);

						// 取當前加班分段明細節點
						OTSChainNode curOTSegNode = getOvertimeSegChainNodeByOTReg(otRegVo, ruleAggVO, checkNode);

						while (checkNode != null) {
							if (checkNode.getNodeData().getRegdate()
									.isSameDate(curOTSegNode.getNodeData().getRegdate())) {
								// 已存在節點與當前加班日為同一天
								if (!otType.getPk_segrule().equals(checkNode.getNodeData().getPk_segrule())
										&& ((SegRuleVO) ruleAggVO.getParent()).getDatetype() != 5
										&& !isEventDayRule(checkNode.getNodeData().getPk_segrule())) {
									throw new BusinessException("系統中已存在使用不同類型的加班分段規則的加班登記單。");
								}
							}
							checkNode = checkNode.getNextNode();
						}

						OTSChainNode combinedNodes = OTSChainUtils.combineNodes(this.getAllNode().get(pk_psndoc),
								curOTSegNode);
						this.getAllNode().put(pk_psndoc, combinedNodes);
						OTSChainUtils.saveAll(this.getAllNode().get(pk_psndoc));
					} finally {
						OTSChainUtils.setCachedPsnChainNodes(null);
					}
				}
			}

		}
	}

	private boolean isEventDayRule(String pk_segrule) throws BusinessException {
		SegRuleVO ruleVO = (SegRuleVO) this.getBaseDao().retrieveByPK(SegRuleVO.class, pk_segrule);
		return ruleVO.getDatetype() == 5;
	}

	@Override
	public void updateOvertimeSegDetail(OvertimeRegVO[] overtimeRegVOs) throws BusinessException {
		if (overtimeRegVOs != null && overtimeRegVOs.length > 0) {
			deleteOvertimeSegDetail(overtimeRegVOs);
			regOvertimeSegDetail(overtimeRegVOs);
		}

	}

	@Override
	public void deleteOvertimeSegDetail(OvertimeRegVO[] overtimeRegVOs) throws BusinessException {
		if (overtimeRegVOs != null && overtimeRegVOs.length > 0) {

			for (OvertimeRegVO vo : overtimeRegVOs) {
				UFBoolean isEnabled = new UFBoolean(SysInitQuery.getParaString(vo.getPk_org(), "TBMOTSEG"));
				if (isEnabled == null || !isEnabled.booleanValue()) {
					continue;
				}

				OTSChainNode curNode = OTSChainUtils.buildChainNodes(vo.getPk_psndoc(), null, null,
						false, false, false, false, false);

				if (curNode == null) {
					throw new BusinessException("未找到加班登記單對應的未結算分段明細。");
				}

				boolean canDel = true;
				while (curNode != null) {
					if(!vo.getPk_overtimereg().equals(curNode.getNodeData().getPk_overtimereg())){
						curNode = curNode.getNextNode();
						continue;
					}
					if (curNode.getNodeData().getConsumedhours().doubleValue() > 0
							|| containsChild(curNode.getNodeData().getPk_segdetail())) {
						canDel = false;
						break;
					}
					
					OTSChainNode delete = curNode;
					curNode = curNode.getNextNode();
					// ssx modified on 2019-08-08
					// for 刪除當前節點並防止斷鏈
					OTSChainUtils.removeCurrentNode(delete, true);
					// end
				}

				if (!canDel) {
					throw new BusinessException("加班登記單對應的分段明細已被消耗");
				}

			}
		}
	}

	private boolean containsChild(String pk_segdetail) throws BusinessException {
		Collection children = this.getBaseDao().retrieveByClause(SegDetailConsumeVO.class,
				"pk_segdetail='" + pk_segdetail + "'");
		return (children != null && children.size() > 0);
	}

	private OTSChainNode getOvertimeSegChainNodeByOTReg(OvertimeRegVO otRegVO, AggSegRuleVO ruleAggVO,
			OTSChainNode allNode) throws BusinessException {
		SegRuleVO rule = ruleAggVO.getParentVO();
		SegRuleTermVO[] terms = (SegRuleTermVO[]) ruleAggVO.getChildrenVO();

		Map<String, UFDouble> originTermMap = new HashMap<String, UFDouble>();
		for (SegRuleTermVO termVO : terms) {
			UFDouble termStart = termVO.getStartpoint();
			UFDouble termEnd = termVO.getEndpoint() == null ? new UFDouble(24) : termVO.getEndpoint();
			originTermMap.put(termVO.getPk_segruleterm(), termEnd.sub(termStart));
		}

		UFDouble othours = otRegVO.getOvertimehour();
		UFDouble totalSegHours = UFDouble.ZERO_DBL; // 纍計生成分段時長
		OTSChainNode parentNode = null;
		UFLiteralDate realDate = getShiftRegDateByOvertime(otRegVO); // 獲取實際加班日期（刷卡開始時間段所屬工作日）
		UFLiteralDate maxLeaveDate = (otRegVO.getIstorest() == null || !otRegVO.getIstorest().booleanValue()) ? null
				: getMaxLeaveDate(otRegVO, realDate); // 獲取最長可休日期

		// ssx added on 2019-01-22
		// TODO 如果realDate所在期間內存在已審核未發放的薪資發放數據則報錯

		// TODO 如果maxLeaveDate之後存在已結算分段則報錯

		// end

		OTSChainNode firstNode = allNode;
		SegRuleTermVO beginTerm = getLastTermVO(firstNode, realDate, terms);

		for (SegRuleTermVO term : terms) {
			if (othours.equals(UFDouble.ZERO_DBL)) {
				break;
			}

			if (beginTerm != null) {
				if (!term.equals(beginTerm)) {
					continue;
				} else {
					beginTerm = null;
				}
			}

			UFDouble start = term.getStartpoint();
			UFDouble end = term.getEndpoint() == null ? new UFDouble(24) : term.getEndpoint();
			UFDouble taxfreerate = term.getTaxfreeotrate();
			UFDouble taxablerate = term.getTaxableotrate();
			UFDouble segRemainedHours = end.sub(start); // 分段剩余時長
			UFDouble curSegTotalHours = UFDouble.ZERO_DBL; // 當前分段總時長
			UFDouble curSegTaxfreeHours = UFDouble.ZERO_DBL; // 當前分段免稅時長
			UFDouble curSegTaxableHours = UFDouble.ZERO_DBL; // 當前分段應稅時長
			if (othours.doubleValue() >= segRemainedHours.doubleValue()) {
				// 加班時長大於分段長度，本次註冊時長取分段時長
				curSegTotalHours = segRemainedHours;
			} else {
				// 加班時長小於分段長度，本次註冊時長取加班時長
				curSegTotalHours = othours;
			}

			othours = othours.sub(curSegTotalHours);

			if (term.getIslimitscope() == null || !term.getIslimitscope().booleanValue()) {
				// 不計入加班上限統計，直接記為免稅時數
				curSegTaxfreeHours = curSegTotalHours;
				curSegTaxableHours = UFDouble.ZERO_DBL;
			} else {
				// 先檢查分段依據上，該分段的應免稅設置
				if (term.getTaxflag().equals(TaxFlagEnum.TAXABLE.toIntValue())) {
					curSegTaxfreeHours = UFDouble.ZERO_DBL;
					curSegTaxableHours = curSegTotalHours;
				} else {
					// 計入加班上限統計，檢查當日截止當日前一日的應稅加班時數
					Map<String, UFDouble[]> psnSeghours = this.calculateTaxableByDate(otRegVO.getPk_org(),
							new String[] { otRegVO.getPk_psndoc() }, realDate, realDate, curSegTotalHours, parentNode,
							null, false);
					curSegTaxfreeHours = psnSeghours.get(otRegVO.getPk_psndoc())[0];
					curSegTaxableHours = psnSeghours.get(otRegVO.getPk_psndoc())[1];
				}
			}

			OTSChainNode curNode = new OTSChainNode();
			SegDetailVO segvo = createNewSegDetail(otRegVO, realDate, rule, term,
					originTermMap.get(term.getPk_segruleterm()), taxfreerate, taxablerate, curSegTaxfreeHours,
					curSegTaxableHours, maxLeaveDate);

			curNode.setNodeData(segvo);

			if (parentNode == null) {
				curNode.setNextNode(null);
				curNode.setPriorNode(null);
				parentNode = curNode;
			} else {
				OTSChainUtils.appendNode(parentNode, curNode, true);
				// parentNode = curNode;
			}

			totalSegHours = totalSegHours.add(curSegTotalHours);// 生成分段時長纍加
		}
		return OTSChainUtils.getFirstNode(parentNode);
	}

	private SegRuleTermVO getLastTermVO(OTSChainNode firstNode, UFLiteralDate realDate, SegRuleTermVO[] terms) {
		SegRuleTermVO retTerm = null;
		if (firstNode != null) {
			OTSChainNode curNode = firstNode;
			String pk_lastterm = null;
			UFDouble lastHours = UFDouble.ZERO_DBL;
			boolean foundTerm = false;
			while (curNode != null) {
				if (curNode.getNodeData() != null) {
					if (curNode.getNodeData().getRegdate().equals(realDate)
							&& (curNode.getNextNode() == null || !curNode.getNextNode().getNodeData().getRegdate()
									.isSameDate(realDate))) {
						pk_lastterm = curNode.getNodeData().getPk_segruleterm();
						lastHours = curNode.getNodeData().getHours();
						boolean passCur = false;
						for (SegRuleTermVO term : terms) {
							if (term.getPk_segruleterm().equals(pk_lastterm)) {
								UFDouble ruleHours = term.getEndpoint() == null ? new UFDouble(24).sub(term
										.getStartpoint()) : term.getEndpoint().sub(term.getStartpoint());
								if (lastHours.doubleValue() < ruleHours.doubleValue()) {
									// 當前檢查節點的時數 < 規則總時數
									retTerm = term;
									retTerm.setStartpoint(retTerm.getStartpoint().add(lastHours));
									foundTerm = true;
									break;
								} else if (!passCur) {
									// 當前檢查節點的時數 = 規則總時數
									// （不可能大於），已過檢查點，下一個Term即為要查找的Term
									passCur = true;
								}
							} else if (passCur) {
								// 已過檢查點，遇到第一個不與前Term相同的Term即為要查找的Term
								retTerm = term;
								foundTerm = true;
								break;
							}
						}
						if (foundTerm) {
							break;
						}
					}
				}
				curNode = curNode.getNextNode();
			}
		}
		return retTerm;
	}

	@SuppressWarnings("unchecked")
	private UFLiteralDate getMaxLeaveDate(OvertimeRegVO otRegVO, UFLiteralDate otRealDate) throws BusinessException {
		int settleType = SysInitQuery.getParaInt(otRegVO.getPk_org(), "TWHRT09");
		Collection<TimeRuleVO> rules = this.getBaseDao().retrieveByClause(TimeRuleVO.class,
				"pk_org='" + otRegVO.getPk_org() + "'");
		TimeRuleVO timeRule = null;
		if (rules != null && rules.size() > 0) {
			timeRule = rules.toArray(new TimeRuleVO[0])[0];
		}

		if (timeRule == null) {
			throw new BusinessException("当前组织未定义有效的考勤规则，请检查后重试。");
		}

		UFLiteralDate maxLeaveDate = null;

		if (settleType == 0) {
			// 按加班日期往后N个月
			UFDouble monthAfter = timeRule.getMonthafterotdate();
			maxLeaveDate = otRealDate.getDateAfter(monthAfter.multiply(30).intValue());
		} else if (settleType == 1) {
			// 按审批日期往后N个月
			UFDouble monthAfter = timeRule.getMonthafterapproved();
			if (otRegVO.getApprove_time() != null) {
				maxLeaveDate = new UFLiteralDate(otRegVO.getApprove_time()
						.getDateTimeAfter(monthAfter.multiply(30).sub(1).intValue()).toString().substring(0, 10));
			} else {
				throw new BusinessException("加班可休日期按审核日期计算错误，请检查加班登记单审核日期。");
			}
		} else if (settleType == 2) {
			// 按固定周期（比对加班日期）
			String startYearMonth = timeRule.getStartcycleyearmonth();
			if (!StringUtils.isEmpty(startYearMonth) && startYearMonth.length() == 6) {
				String cyear = startYearMonth.substring(0, 4);
				String cmonth = startYearMonth.substring(4, 6);
				UFLiteralDate startDate = new UFLiteralDate(cyear + "-" + cmonth + "-01"); // 周期起始日
				maxLeaveDate = getCycleDate(timeRule.getMonthofcycle(), startDate);
			} else {
				throw new BusinessException("加班可休日期按固定周期计算错误，请检查考勤规则起算年月设定（格式：YYYYMM）。");
			}
		} else if (settleType == 3) {
			// 按年资起算日（比对加班日期）
			PsnOrgVO psnOrgVO = (PsnOrgVO) this.getBaseDao().retrieveByPK(PsnOrgVO.class, otRegVO.getPk_psnorg());
			UFLiteralDate workAgeStartDate = (UFLiteralDate) psnOrgVO.getAttributeValue("workagestartdate"); // 年资起算日
			if (workAgeStartDate != null) {
				if (workAgeStartDate.toString().substring(4).equals("-02-29")) {
					maxLeaveDate = new UFLiteralDate(String.valueOf(otRealDate.getYear()) + "-03-01").getDateBefore(1);
					if (maxLeaveDate.before(otRealDate)) {
						maxLeaveDate = new UFLiteralDate(String.valueOf(otRealDate.getYear() + 1) + "-03-01")
								.getDateBefore(1);
					}
				} else {
					maxLeaveDate = new UFLiteralDate(String.valueOf(otRealDate.getYear())
							+ workAgeStartDate.toString().substring(4)).getDateBefore(1);
					if (maxLeaveDate.before(otRealDate)) {
						maxLeaveDate = new UFLiteralDate(String.valueOf(otRealDate.getYear() + 1)
								+ workAgeStartDate.toString().substring(4)).getDateBefore(1);
					}
				}
			} else {
				throw new BusinessException("加班可休日期按年资起算日计算错误，请检查员工组织关系年资起算日设定。");
			}
		} else {
			throw new BusinessException("加班可休日期计算错误，请检查组织参数（TWHRT09）的设定。");
		}
		return maxLeaveDate;
	}

	private UFLiteralDate getCycleDate(UFDouble monthOfCycle, UFLiteralDate startDate) {
		UFLiteralDate maxLeaveDate;
		int wholeDays = new UFLiteralDate().getDaysAfter(startDate); // 周期起始日到加班当日天数
		UFDouble daysInOneCycle = monthOfCycle.multiply(30); // 单周期日数
		int passedCycles = wholeDays / daysInOneCycle.intValue(); // 下一周期所在周期数
		startDate = startDate.getDateAfter(daysInOneCycle.multiply(passedCycles).intValue()); // 下一周期的起始日
		maxLeaveDate = startDate.getDateBefore(startDate.getDay() - 1).getDateBefore(1);
		return maxLeaveDate;
	}

	private SegDetailVO createNewSegDetail(OvertimeRegVO vo, UFLiteralDate realRegDate, SegRuleVO rule,
			SegRuleTermVO term, UFDouble seghours, UFDouble taxfreerate, UFDouble taxablerate, UFDouble hourstaxfree,
			UFDouble hourstaxable, UFLiteralDate maxLeaveDate) throws BusinessException {
		if (vo.getApprove_time() == null) {
			throw new BusinessException("加班登記審核日期錯誤，請檢查加班登記。");
		}
		SegDetailVO segvo = new SegDetailVO();
		segvo.setStatus(VOStatus.NEW);
		segvo.setPk_segdetail(OidGenerator.getInstance().nextOid());
		segvo.setPk_group(vo.getPk_group());
		segvo.setPk_org(vo.getPk_org());
		segvo.setPk_org_v(vo.getPk_org_v());
		segvo.setPk_overtimereg(vo.getPk_overtimereg());
		segvo.setPk_psndoc(vo.getPk_psndoc());
		segvo.setPk_segrule(rule.getPk_segrule());
		segvo.setPk_segruleterm(term.getPk_segruleterm());
		segvo.setConsumedhours(UFDouble.ZERO_DBL);
		segvo.setConsumedhourstaxfree(UFDouble.ZERO_DBL);
		segvo.setConsumedhourstaxable(UFDouble.ZERO_DBL);
		segvo.setFrozenhours(UFDouble.ZERO_DBL);
		segvo.setFrozenhourstaxfree(UFDouble.ZERO_DBL);
		segvo.setFrozenhourstaxable(UFDouble.ZERO_DBL);
		// UFDouble hourpay = getPsnHourPay(vo.getPk_psndoc(),
		// vo.getOvertimebegindate(), DaySalaryEnum.TBMHOURSALARY);
		// segvo.setHourlypay(hourpay == null ? UFDouble.ZERO_DBL : hourpay);
		segvo.setHourstaxfree(hourstaxfree);
		segvo.setHourstaxable(hourstaxable);
		segvo.setHours(segvo.getHourstaxfree().add(segvo.getHourstaxable()));
		segvo.setIscanceled(UFBoolean.FALSE);
		segvo.setIscompensation(vo.getIstorest());
		segvo.setHourstorest(vo.getToresthour());
		segvo.setIsconsumed(UFBoolean.FALSE);
		segvo.setIssettled(UFBoolean.FALSE);
		segvo.setMaketime(new UFDate());
		// mod Ares.Tank 2018-10-15 12:40:19 增加审批时间
		segvo.setApproveddate(new UFLiteralDate(vo.getApprove_time() == null ? null : vo.getApprove_time().toString()));

		// end
		PsndocVO psnVo = (PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class, vo.getPk_psndoc());
		segvo.setNodecode(psnVo.getCode() + OTSChainUtils.SPLT + realRegDate.toString() + OTSChainUtils.SPLT
				+ rule.getCode() + OTSChainUtils.SPLT + String.valueOf(String.format("%02d", term.getSegno())));
		segvo.setNodename(MultiLangUtil.getSuperVONameOfCurrentLang(psnVo, PsndocVO.NAME, psnVo.getName())
				+ OTSChainUtils.SPLT + realRegDate.toString().replace("-", "") + OTSChainUtils.SPLT
				+ MultiLangUtil.getSuperVONameOfCurrentLang(rule, SegRuleVO.NAME, rule.getName()) + OTSChainUtils.SPLT
				+ String.valueOf(term.getSegno()));
		segvo.setTaxfreerate(taxfreerate == null ? UFDouble.ZERO_DBL : taxfreerate);
		segvo.setTaxablerate(taxfreerate == null ? UFDouble.ZERO_DBL : taxfreerate);
		segvo.setExtrataxablerate(taxablerate == null ? UFDouble.ZERO_DBL : taxablerate);
		segvo.setExtrahourstaxable(segvo.getHours());
		// segvo.setExtraamounttaxable(getOTAmount(segvo.getExtrataxablerate(),
		// segvo.getHourlypay(),
		// segvo.getExtrahourstaxable(), null, -1));
		segvo.setRegdate(realRegDate);
		segvo.setExpirydate(maxLeaveDate);
		segvo.setRemainhours(segvo.getHours());
		segvo.setRemainhourstaxfree(hourstaxfree);
		segvo.setRemainhourstaxable(hourstaxable);
		// segvo.setRemainamounttaxfree(getOTAmount(segvo.getTaxfreerate(),
		// segvo.getHourlypay(),
		// segvo.getRemainhourstaxfree(), null, -1));
		// segvo.setRemainamounttaxable(getOTAmount(segvo.getTaxablerate(),
		// segvo.getHourlypay(),
		// segvo.getRemainhourstaxable(), null, -1));
		segvo.setRemainamount((segvo.getRemainamounttaxfree() == null ? UFDouble.ZERO_DBL : segvo
				.getRemainamounttaxfree()).add(segvo.getRemainamounttaxable() == null ? UFDouble.ZERO_DBL : segvo
				.getRemainamounttaxable())); // 剩餘金額=剩餘金額（免稅）+剩餘金額（應稅）
		segvo.setRulehours(seghours);
		return segvo;
	}

	/**
	 * 根據加班核定開始日期查詢加班實際歸屬班次的所屬日期
	 * 
	 * @param vo
	 *            加班登記單
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private UFLiteralDate getShiftRegDateByOvertime(OvertimeRegVO vo) throws BusinessException {
		UFLiteralDate rtnDate = vo.getBegindate();
		UFLiteralDate vestDate = vo.getVestdate();
		if (vestDate != null) {
			return vestDate;
		}
		Collection<PsnCalendarVO> psncals = this.getBaseDao().retrieveByClause(
				PsnCalendarVO.class,
				"pk_psndoc='" + vo.getPk_psndoc() + "' and calendar between '"
						+ vo.getOvertimebegindate().getDateBefore(3) + "' and '"
						+ vo.getOvertimebegindate().getDateAfter(3) + "'");
		if (psncals != null && psncals.size() > 0) {
			for (PsnCalendarVO psncal : psncals) {
				if (psncal.getPk_shift() != null) {
					ShiftVO shiftvo = (ShiftVO) this.getBaseDao().retrieveByPK(ShiftVO.class, psncal.getPk_shift());
					if (shiftvo != null) {
						//mod start tank 2019年8月21日17:04:24 前一日,後一日修復
						UFDateTime startDT = new UFDateTime(psncal.getCalendar().getDateAfter(shiftvo.getTimebeginday()).toString() + " "
								+ shiftvo.getTimebegintime());
						
						UFDateTime endDT = new UFDateTime(psncal.getCalendar().getDateAfter(shiftvo.getTimeendday()).toString() + " "
								+ shiftvo.getTimeendtime());
						//end mod
						if (vo.getOvertimebegintime().before(endDT) && vo.getOvertimebegintime().after(startDT)) {
							rtnDate = psncal.getCalendar();
						}
					}
				}
			}
		}
		return rtnDate;
	}

	@SuppressWarnings("unchecked")
	private UFLiteralDate getShiftRegDateByLeave(LeaveRegVO vo) throws BusinessException {
		UFLiteralDate rtnDate = vo.getBegindate();
		Collection<PsnCalendarVO> psncals = this.getBaseDao().retrieveByClause(
				PsnCalendarVO.class,
				"pk_psndoc='" + vo.getPk_psndoc() + "' and calendar between '"
						+ vo.getLeavebegindate().getDateBefore(3) + "' and '" + vo.getLeavebegindate().getDateAfter(3)
						+ "'");
		if (psncals != null && psncals.size() > 0) {
			for (PsnCalendarVO psncal : psncals) {
				if (psncal.getPk_shift() != null) {
					ShiftVO shiftvo = (ShiftVO) this.getBaseDao().retrieveByPK(ShiftVO.class, psncal.getPk_shift());
					if (shiftvo != null) {
						UFDateTime startDT = null;
						if (shiftvo.getTimebeginday() == 0) {
							startDT = new UFDateTime(psncal.getCalendar().toString() + " " + shiftvo.getTimebegintime());
						} else {
							startDT = new UFDateTime(psncal.getCalendar().getDateAfter(1).toString() + " "
									+ shiftvo.getTimebegintime());
						}

						UFDateTime endDT = null;
						if (shiftvo.getTimeendday() == 0) {
							endDT = new UFDateTime(psncal.getCalendar().toString() + " " + shiftvo.getTimeendtime());
						} else {
							endDT = new UFDateTime(psncal.getCalendar().getDateAfter(1).toString() + " "
									+ shiftvo.getTimeendtime());
						}

						if (vo.getLeavebegintime().before(endDT) && vo.getLeavebegintime().after(startDT)) {
							rtnDate = psncal.getCalendar();
						}
					}
				}
			}
		}
		return rtnDate;
	}

	private UFDouble getOTAmount(UFDouble otRate, UFDouble hourlypay, UFDouble hours, SegDetailVO detailVO,
			int daySalType, String pk_item_group) throws BusinessException {
		otRate = otRate == null ? UFDouble.ZERO_DBL : otRate;
		hourlypay = hourlypay == null ? UFDouble.ZERO_DBL : hourlypay;
		hours = hours == null ? UFDouble.ZERO_DBL : hours;
		if (detailVO != null) {
			hourlypay = getPsnHourPay(detailVO.getPk_org(), detailVO.getPk_psndoc(), detailVO.getRegdate(),
					pk_item_group);
			detailVO.setHourlypay(hourlypay.setScale(1, UFDouble.ROUND_HALF_UP));
		}
		UFDouble amount = hourlypay.setScale(1, UFDouble.ROUND_HALF_UP).multiply(otRate).multiply(hours)
				.setScale(1, UFDouble.ROUND_HALF_UP);
		return amount;
	}

	private UFDouble getPsnHourPay(String pk_hrorg, String pk_psndoc, UFLiteralDate overtimebegindate,
			String pk_item_group) throws BusinessException {
		String strSQL = "select isnull(nmoney,0) nmoney, pk_wa_data from hi_psndoc_wadoc "
				+ " LEFT JOIN wa_cacu_decryptedpk wd on wd.pk_wa_data = hi_psndoc_wadoc.pk_psndoc_sub "
				+ " where pk_psndoc = '"
				+ pk_psndoc
				+ "' and '"
				+ overtimebegindate.toString()
				+ "' between begindate and isnull(enddate, '9999-12-31') and pk_wa_item in (select pk_wa_item from wa_itemgroupmember where pk_itemgroup = '"
				+ pk_item_group + "' and dr=0) and waflag='Y'";

		List<Map> payList = (List<Map>) this.getBaseDao().executeQuery(strSQL, new MapListProcessor());
		ItemGroupVO igvo = (ItemGroupVO) this.getBaseDao().retrieveByPK(ItemGroupVO.class, pk_item_group);

		UFDouble totalPay = UFDouble.ZERO_DBL;
		if (payList != null && payList.size() > 0) {
			for (Map pay : payList) {
				UFDouble monthPay = new UFDouble(String.valueOf(pay.get("nmoney")));
				String pk_wa_data = (String) pay.get("pk_wa_data");
				if (StringUtils.isEmpty(pk_wa_data)) {
					monthPay = SalaryDecryptUtil.decrypt(monthPay);
				}

				totalPay = totalPay.add(monthPay == null ? UFDouble.ZERO_DBL : monthPay);
			}
		}

		double days = getTbmSalaryNum(pk_hrorg, overtimebegindate, igvo.getDaysource());
		return totalPay.div(days * 8);
		//
		//
		// IWadaysalaryQueryService dayPaySvc =
		// NCLocator.getInstance().lookup(IWadaysalaryQueryService.class);
		//
		// Map<String, HashMap<UFLiteralDate, UFDouble>> dayPayMap =
		// dayPaySvc.getTotalTbmDaySalaryMap(
		// new String[] { pk_psndoc }, overtimebegindate, overtimebegindate,
		// daySalType, pk_item_group);
		// if (dayPayMap == null || dayPayMap.size() == 0 ||
		// dayPayMap.get(pk_psndoc) == null
		// || dayPayMap.get(pk_psndoc).get(overtimebegindate) == null) {
		// // throw new BusinessException("人員時薪取值錯誤：人員時薪為空");
		// return UFDouble.ZERO_DBL; // 未取到日薪的，暫時返回0
		// }
		// return dayPayMap.get(pk_psndoc).get(overtimebegindate);
	}

	/**
	 * 取考勤时薪天数取值方式时间
	 * 
	 * @param pk_hrorg
	 * @param calculDate
	 * @param tbmnumtype
	 * @return
	 * @throws BusinessException
	 */
	private double getTbmSalaryNum(String pk_hrorg, UFLiteralDate calculDate, int tbmnumtype) throws BusinessException {
		if (tbmnumtype == DayScopeEnum.FIX30.toIntValue()) {
			return DaySalaryEnum.TBMSALARYNUM01;// //固定值30天
		}
		if (tbmnumtype == DayScopeEnum.FIX22.toIntValue()) {
			return DaySalaryEnum.TBMSALARYNUM02;// 固定21.75天
		}
		if (tbmnumtype == DayScopeEnum.TPDAYS.toIntValue()) {
			// 查询考勤期间
			String sqlsys = "SELECT\n" + "	begindate,\n" + "	enddate\n" + "FROM\n" + "	tbm_period\n" + "WHERE\n"
					+ "	begindate <= '" + calculDate + "'\n" + "AND enddate >= '" + calculDate + "'\n"
					+ "AND pk_org = '" + pk_hrorg + "'\n" + "AND isnull(dr, 0) = 0";
			@SuppressWarnings("unchecked")
			List<HashMap<String, Object>> listMaptemp = (ArrayList<HashMap<String, Object>>) getBaseDao().executeQuery(
					sqlsys.toString(), new MapListProcessor());
			String begindate = null;
			String enddate = null;
			if (listMaptemp != null && listMaptemp.size() > 0) {
				HashMap<String, Object> hashMap = listMaptemp.get(0);
				begindate = hashMap.get("begindate").toString();
				enddate = hashMap.get("enddate").toString();
			} else {
				StringBuffer message = new StringBuffer();
				message.append("組織：" + pk_hrorg + "\n");
				message.append("計算日期：" + calculDate.toStdString() + "\n");
				message.append("為維護考勤期間");
				throw new BusinessException(message.toString());
			}
			return UFLiteralDate.getDaysBetween(new UFLiteralDate(begindate), new UFLiteralDate(enddate)) + 1;
		}
		return DaySalaryEnum.TBMSALARYNUM01;// 固定值30天;
	}

	@SuppressWarnings("unchecked")
	private AggSegRuleVO getSegRuleAggVO(String pk_segrule) throws BusinessException {
		SegRuleVO ruleVO = (SegRuleVO) this.getBaseDao().retrieveByPK(SegRuleVO.class, pk_segrule);

		if (ruleVO == null) {
			throw new BusinessException("數據錯誤：加班分段規則已被刪除");
		}

		Collection<SegRuleTermVO> segTerms = this.getBaseDao().retrieveByClause(SegRuleTermVO.class,
				"pk_segrule='" + pk_segrule + "' and dr=0", "segno");
		if (segTerms == null || segTerms.size() == 0) {
			throw new BusinessException("數據錯誤：加班分段規則明細已被刪除");
		}

		AggSegRuleVO aggVo = new AggSegRuleVO();
		aggVo.setParent(ruleVO);
		aggVo.setChildrenVO(segTerms.toArray(new SegRuleTermVO[0]));

		return aggVo;
	}

	@Override
	public void regOvertimeSegDetailConsume(LeaveRegVO[] sortedLeaveRefVOs) throws BusinessException {
		if (sortedLeaveRefVOs != null && sortedLeaveRefVOs.length > 0) {

			for (LeaveRegVO vo : sortedLeaveRefVOs) {
				UFBoolean isEnabled = SysInitQuery.getParaBoolean(vo.getPk_org(), "TBMOTSEG");
				if (isEnabled == null || !isEnabled.booleanValue()) {
					return;
				}

				if (vo.getLeavebegindate().before(new UFLiteralDate("2018-03-01"))) {
					// 2018-03-01開始實施加班分段
					continue;
				}

				// 取業務參數定義的加班分段休假類別（加班轉補休）
				LeaveTypeCopyVO lvTypeVO = getLeaveTypeVOs(vo.getPk_org());

				if (lvTypeVO.getPk_timeitemcopy().equals(vo.getPk_leavetypecopy())) {
					// 取已註冊未消耗的加班分段明細
					// 取當前人員過濾節點（邏輯鏈表：轉補休，未作廢，未核消完畢，未結算）
					OTSChainNode psnNode = OTSChainUtils.buildChainNodes(vo.getPk_psndoc(), null, null, true, false,
							true, true, true);
					SegDetailVO[] segDetailBeConsumed = OTSChainUtils.getAllNodeData(psnNode);

					if (segDetailBeConsumed == null || segDetailBeConsumed.length == 0) {
						throw new BusinessException("消耗加班分段明細失敗：未找到可用的加班分段明細。");
					}

					consumeSegDetailHours(segDetailBeConsumed, vo);
				}
			}

		}
	}

	@Override
	public void updateOvertimeSegDetailConsume(LeaveRegVO[] leaveRegVOs) throws BusinessException {
		if (leaveRegVOs != null && leaveRegVOs.length > 0) {
			deleteOvertimeSegDetailConsume(leaveRegVOs);
			regOvertimeSegDetailConsume(leaveRegVOs);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteOvertimeSegDetailConsume(LeaveRegVO[] leaveRegVOs) throws BusinessException {
		if (leaveRegVOs != null && leaveRegVOs.length > 0) {

			for (LeaveRegVO vo : leaveRegVOs) {
				Collection<SegDetailConsumeVO> consumeVOs = this.getBaseDao().retrieveByClause(
						SegDetailConsumeVO.class, "pk_leavereg='" + vo.getPk_leavereg() + "'");
				for (SegDetailConsumeVO consumeVO : consumeVOs) {
					SegDetailVO detailVO = (SegDetailVO) this.getBaseDao().retrieveByPK(SegDetailVO.class,
							consumeVO.getPk_segdetail());
					// Map<String, UFDouble[]> taxhourssplit =
					// calculateTaxableByDate(vo.getPk_org(),
					// new String[] { vo.getPk_psndoc() },
					// detailVO.getRegdate(), detailVO.getRegdate()
					// .getDateBefore(1), vo.getLeavehour());
					// UFDouble[] values = taxhourssplit.get(vo.getPk_psndoc());
					detailVO.setRemainhourstaxfree(detailVO.getRemainhourstaxfree().add(
							consumeVO.getConsumedhourstaxfree()));
					detailVO.setRemainhourstaxable(detailVO.getRemainhourstaxable().add(
							consumeVO.getConsumedhourstaxable()));
					detailVO.setRemainhours(detailVO.getRemainhourstaxfree().add(detailVO.getRemainhourstaxable()));

					// detailVO.setRemainamounttaxfree(getOTAmount(detailVO.getTaxfreerate(),
					// detailVO.getHourlypay(),
					// detailVO.getRemainhourstaxfree(), detailVO,
					// DaySalaryEnum.TBMHOURSALARY));
					// detailVO.setRemainamounttaxable(getOTAmount(detailVO.getTaxablerate(),
					// detailVO.getHourlypay(),
					// detailVO.getRemainhourstaxable(), detailVO,
					// DaySalaryEnum.TBMHOURSALARY));
					// detailVO.setRemainamount(detailVO.getRemainamounttaxfree().add(detailVO.getRemainamounttaxable()));

					detailVO.setConsumedhourstaxfree(detailVO.getConsumedhourstaxfree().sub(
							consumeVO.getConsumedhourstaxfree()));
					detailVO.setConsumedhourstaxable(detailVO.getConsumedhourstaxable().sub(
							consumeVO.getConsumedhourstaxable()));
					detailVO.setConsumedhours(detailVO.getConsumedhourstaxfree()
							.add(detailVO.getConsumedhourstaxable()));

					detailVO.setIsconsumed(new UFBoolean(detailVO.getRemainhours().doubleValue() <= 0));

					this.getBaseDao().updateVO(detailVO);
					this.getBaseDao().deleteVO(consumeVO);
				}
			}

		}
	}

	private void consumeSegDetailHours(SegDetailVO[] segDetailVOs, LeaveRegVO vo) throws BusinessException {
		List<AggSegDetailVO> aggvos = new ArrayList<AggSegDetailVO>();
		UFDouble unConsumedLeaveHours = vo.getLeavehour();
		for (SegDetailVO segDetail : segDetailVOs) {
			// ssx added on 2019-07-08
			// 已超過最長可休日期的加班分段不能參與消耗
			if (vo.getLeavebegindate().after(segDetail.getExpirydate())) {
				continue;
			}
			//

			// 先進先出匹配
			if (!unConsumedLeaveHours.equals(UFDouble.ZERO_DBL)) {
				// 本筆核銷時數
				UFDouble curConsumedHours = UFDouble.ZERO_DBL;
				if (segDetail.getRemainhours().doubleValue() >= unConsumedLeaveHours.doubleValue()) {
					// 剩餘小時數大於等於本次核銷時數=本次核銷全部在本條明細完成
					curConsumedHours = unConsumedLeaveHours;
					unConsumedLeaveHours = UFDouble.ZERO_DBL;
				} else {
					// 剩餘小時數小於本次核銷時數=本次核銷在本條明細完成部分
					curConsumedHours = segDetail.getRemainhours();
					// 餘下部分繼續向後核銷
					unConsumedLeaveHours = unConsumedLeaveHours.sub(segDetail.getRemainhours());
				}

				SegDetailConsumeVO consumeVO = getNewConsumeVO(vo, segDetail); // 生成核銷明細基本信息
				consumeSegDetailHours(curConsumedHours, segDetail, consumeVO); // 處理本筆核銷，填充核销明细时数
				AggSegDetailVO aggvo = new AggSegDetailVO();
				segDetail.setStatus(VOStatus.UPDATED);
				aggvo.setParent(segDetail);
				consumeVO.setStatus(VOStatus.NEW);
				Collection<SegDetailConsumeVO> lstChildVOs = getBaseDao().retrieveByClause(SegDetailConsumeVO.class,
						"pk_segdetail='" + segDetail.getPk_segdetail() + "'");
				lstChildVOs.add(consumeVO);
				aggvo.setChildrenVO(lstChildVOs.toArray(new SegDetailConsumeVO[0]));
				segDetail.setPk_segdetailconsume(lstChildVOs.toArray(new SegDetailConsumeVO[0]));
				aggvos.add(aggvo);
			}
		}

		if (unConsumedLeaveHours.doubleValue() > 0) {
			throw new BusinessException("加班轉補休分段時數已不足，剩餘 [" + String.valueOf(unConsumedLeaveHours.doubleValue())
					+ "] 小時無法消耗。");
		}

		if (aggvos.size() > 0) {
			getSegMaintain().update(aggvos.toArray(new AggSegDetailVO[0]));
			// for (AggSegDetailVO aggvo : aggvos) {
			// updateHead((SegDetailVO) aggvo.getParentVO());
			//
			// for (CircularlyAccessibleValueObject childvo :
			// aggvo.getAllChildrenVO()) {
			// insertChild((SegDetailConsumeVO) childvo);
			// }
			// }
		}
	}

	private void updateHead(SegDetailVO parentVO) throws BusinessException {
		String strSQL = "update " + SegDetailVO.getDefaultTableName() + " set ";
		strSQL += SegDetailVO.CONSUMEDHOURS + " = " + parentVO.getConsumedhours() + ",";
		strSQL += SegDetailVO.CONSUMEDHOURSTAXABLE + " = " + parentVO.getConsumedhourstaxable() + ",";
		strSQL += SegDetailVO.CONSUMEDHOURSTAXFREE + " = " + parentVO.getConsumedhourstaxfree() + ",";
		strSQL += SegDetailVO.REMAINHOURS + " = " + parentVO.getRemainhours() + ",";
		strSQL += SegDetailVO.REMAINHOURSTAXABLE + " = " + parentVO.getRemainhourstaxable() + ",";
		strSQL += SegDetailVO.REMAINHOURSTAXFREE + " = " + parentVO.getRemainhourstaxfree() + ",";
		strSQL += SegDetailVO.REMAINAMOUNT + " = " + parentVO.getRemainamount() + ",";
		strSQL += SegDetailVO.REMAINAMOUNTTAXABLE + " = " + parentVO.getRemainamounttaxable() + ",";
		strSQL += SegDetailVO.REMAINAMOUNTTAXFREE + " = " + parentVO.getRemainamounttaxfree();
		strSQL += " where " + SegDetailVO.PK_SEGDETAIL + " = '" + parentVO.getPk_segdetail() + "'";
		this.getBaseDao().executeUpdate(strSQL);
	}

	private void insertChild(SegDetailConsumeVO childvo) throws BusinessException {
		String strSQL = "insert into " + SegDetailConsumeVO.getDefaultTableName() + "(";
		strSQL += SegDetailConsumeVO.PK_SEGDETAILCONSUME + ",";
		strSQL += SegDetailConsumeVO.PK_GROUP + ",";
		strSQL += SegDetailConsumeVO.PK_ORG + ",";
		strSQL += SegDetailConsumeVO.PK_ORG_V + ",";
		strSQL += SegDetailConsumeVO.CREATOR + ",";
		strSQL += SegDetailConsumeVO.CREATIONTIME + ",";
		strSQL += SegDetailConsumeVO.MODIFIER + ",";
		strSQL += SegDetailConsumeVO.MODIFIEDTIME + ",";
		strSQL += SegDetailConsumeVO.ROWNO + ",";
		strSQL += SegDetailConsumeVO.BIZDATE + ",";
		strSQL += SegDetailConsumeVO.PK_LEAVEREG + ",";
		strSQL += SegDetailConsumeVO.BIZTYPE + ",";
		strSQL += SegDetailConsumeVO.CONSUMEDHOURS + ",";
		strSQL += SegDetailConsumeVO.CONSUMEDHOURSTAXFREE + ",";
		strSQL += SegDetailConsumeVO.CONSUMEDHOURSTAXABLE + ",";
		strSQL += SegDetailConsumeVO.REVERSEDHOURS + ",";
		strSQL += SegDetailConsumeVO.REVERSEDHOURSTAXFREE + ",";
		strSQL += SegDetailConsumeVO.REVERSEDHOURSTAXABLE + ",";
		strSQL += SegDetailConsumeVO.PK_SEGDETAIL + ",";
		strSQL += "ts,dr";
		strSQL += ") values (";
		strSQL += "'" + OidGenerator.getInstance().nextOid() + "',";
		strSQL += childvo.getPk_group() == null ? "~" : ("'" + childvo.getPk_group() + "',");
		strSQL += childvo.getPk_org() == null ? "~" : ("'" + childvo.getPk_org() + "',");
		strSQL += childvo.getPk_org_v() == null ? "~" : ("'" + childvo.getPk_org_v() + "',");
		strSQL += "'" + InvocationInfoProxy.getInstance().getUserId() + "',";
		strSQL += "'" + new UFDateTime().toString() + "',";
		strSQL += "null,";
		strSQL += "null,";
		strSQL += childvo.getRowno() == null ? "null," : ("'" + childvo.getRowno() + "',");
		strSQL += "null,";
		strSQL += "'" + childvo.getPk_leavereg() + "',";
		strSQL += childvo.getBiztype() + ",";
		strSQL += childvo.getConsumedhours().toString() + ",";
		strSQL += childvo.getConsumedhourstaxfree().toString() + ",";
		strSQL += childvo.getConsumedhourstaxable().toString() + ",";
		strSQL += childvo.getReversedhours().toString() + ",";
		strSQL += childvo.getReversedhourstaxfree().toString() + ",";
		strSQL += childvo.getReversedhourstaxable().toString() + ",";
		strSQL += "'" + childvo.getPk_segdetail() + "',";
		strSQL += "'" + new UFDateTime().toString() + "',0";
		strSQL += ")";

		this.getBaseDao().executeUpdate(strSQL);
	}

	private SegDetailConsumeVO getNewConsumeVO(LeaveRegVO vo, SegDetailVO segDetail) {
		SegDetailConsumeVO consumeVO = new SegDetailConsumeVO();
		consumeVO.setPk_group(segDetail.getPk_group());
		consumeVO.setPk_org(segDetail.getPk_org());
		consumeVO.setPk_org_v(segDetail.getPk_org_v());
		consumeVO.setPk_segdetail(segDetail.getPk_segdetail());
		consumeVO.setPk_leavereg(vo.getPk_leavereg());
		consumeVO.setBizdate(vo.getLeavebegindate());
		consumeVO.setBiztype(vo.getBillType());
		consumeVO.setConsumedhours(UFDouble.ZERO_DBL);
		consumeVO.setConsumedhourstaxable(UFDouble.ZERO_DBL);
		consumeVO.setConsumedhourstaxfree(UFDouble.ZERO_DBL);
		consumeVO.setReversedhours(UFDouble.ZERO_DBL);
		consumeVO.setReversedhourstaxable(UFDouble.ZERO_DBL);
		consumeVO.setReversedhourstaxfree(UFDouble.ZERO_DBL);
		return consumeVO;
	}

	// 先消耗免稅部分，再消耗應稅部分
	private void consumeSegDetailHours(UFDouble unConsumedHours, SegDetailVO segDetail, SegDetailConsumeVO consumedVO)
			throws BusinessException {
		SegRuleTermVO term = (SegRuleTermVO) this.getBaseDao().retrieveByPK(SegRuleTermVO.class,
				segDetail.getPk_segruleterm());

		// 免稅部分
		if (segDetail.getRemainhourstaxfree().doubleValue() > 0) {
			if (unConsumedHours.doubleValue() <= segDetail.getRemainhourstaxfree().doubleValue()) {
				// 只消耗免稅部分
				segDetail.setRemainhourstaxfree(segDetail.getRemainhourstaxfree().sub(unConsumedHours));
				segDetail.setConsumedhourstaxfree(segDetail.getConsumedhourstaxfree().add(unConsumedHours));
				segDetail.setRemainhours(segDetail.getRemainhours().sub(unConsumedHours));
				segDetail.setConsumedhours(segDetail.getConsumedhours().add(unConsumedHours));

				consumedVO.setConsumedhourstaxfree(unConsumedHours);
				consumedVO.setConsumedhourstaxable(UFDouble.ZERO_DBL);
			} else {
				// 消耗免稅全部後，剩餘消耗應稅部分
				UFDouble tmpHours = unConsumedHours.sub(segDetail.getRemainamounttaxfree());
				segDetail.setRemainhourstaxfree(segDetail.getRemainhourstaxfree().sub(tmpHours));
				segDetail.setConsumedhourstaxfree(segDetail.getConsumedhourstaxfree().add(tmpHours));
				segDetail.setRemainhours(segDetail.getRemainhours().sub(tmpHours));
				segDetail.setConsumedhours(segDetail.getConsumedhours().add(tmpHours));
				consumedVO.setConsumedhourstaxfree(tmpHours);

				tmpHours = unConsumedHours.sub(tmpHours);
				segDetail.setRemainhourstaxable(segDetail.getRemainhourstaxable().sub(tmpHours));
				segDetail.setConsumedhourstaxable(segDetail.getConsumedhourstaxable().add(tmpHours));
				segDetail.setRemainhours(segDetail.getRemainhours().sub(tmpHours));
				segDetail.setConsumedhours(segDetail.getConsumedhours().add(tmpHours));
				consumedVO.setConsumedhourstaxable(tmpHours);
			}
		} else {
			// 只消耗應稅部分
			segDetail.setRemainhourstaxable(segDetail.getRemainhourstaxable().sub(unConsumedHours));
			segDetail.setConsumedhourstaxable(segDetail.getConsumedhourstaxable().add(unConsumedHours));
			segDetail.setRemainhours(segDetail.getRemainhours().sub(unConsumedHours));
			segDetail.setConsumedhours(segDetail.getConsumedhours().add(unConsumedHours));

			consumedVO.setConsumedhourstaxfree(UFDouble.ZERO_DBL);
			consumedVO.setConsumedhourstaxable(unConsumedHours);
		}

		// 由于未对剩余的外加应税时数进行记录，所以无论什么情况下，外加应税时数 = 剩余免税加班时数
		segDetail.setExtrahourstaxable(segDetail.getRemainhourstaxfree());

		consumedVO.setConsumedhours(consumedVO.getConsumedhourstaxfree().add(consumedVO.getConsumedhourstaxable()));

		// // 計算加班費
		// segDetail.setRemainamounttaxfree(getOTAmount(term.getTaxfreeotrate(),
		// segDetail.getHourlypay(),
		// segDetail.getRemainhourstaxfree(), segDetail,
		// DaySalaryEnum.TBMHOURSALARY));
		// segDetail.setRemainamounttaxable(getOTAmount(term.getTaxfreeotrate(),
		// segDetail.getHourlypay(),
		// segDetail.getRemainhourstaxable(), segDetail,
		// DaySalaryEnum.TBMHOURSALARY));
		// segDetail.setRemainamount(segDetail.getRemainamounttaxfree().add(segDetail.getRemainamounttaxable()));
		//
		// segDetail.setExtraamounttaxable(getOTAmount(term.getTaxableotrate(),
		// segDetail.getHourlypay(),
		// segDetail.getExtrahourstaxable(), segDetail,
		// DaySalaryEnum.TBMHOURSALARY));

		unConsumedHours = UFDouble.ZERO_DBL;
		// 設置已消耗完畢
		segDetail.setIsconsumed(new UFBoolean(segDetail.getRemainhours().doubleValue() == 0));
	}

	Map<String, LeaveTypeCopyVO> leaveTypeVOMap = null;

	private LeaveTypeCopyVO getLeaveTypeVOs(String pk_org) throws BusinessException {
		if (leaveTypeVOMap == null) {
			leaveTypeVOMap = new HashMap<String, LeaveTypeCopyVO>();
		}

		if (!leaveTypeVOMap.containsKey(pk_org)) {
			String pk_leavetypecopy = SysInitQuery.getParaString(pk_org, "TWHRT08");

			if (StringUtils.isEmpty(pk_leavetypecopy)) {
				throw new BusinessException("參數取值失敗：請在業務參數設置-組織中設定本組織 [加班補休指定假別] 參數。");
			}
			LeaveTypeCopyVO vo = (LeaveTypeCopyVO) this.getBaseDao().retrieveByPK(LeaveTypeCopyVO.class,
					pk_leavetypecopy);
			leaveTypeVOMap.put(pk_org, vo);
		}

		return leaveTypeVOMap.get(pk_org);
	}

	@SuppressWarnings("unchecked")
	private TimeRuleVO getTimeRule(String pk_org) throws BusinessException {
		// 取該員工考勤規則
		Collection<TimeRuleVO> timerule = this.getBaseDao().retrieveByClause(TimeRuleVO.class,
				"pk_org='" + pk_org + "' and dr=0");
		if (timerule == null) {
			throw new BusinessException("取考勤規則錯誤：指定組織下未定義考勤規則");
		}

		return timerule.toArray(new TimeRuleVO[0])[0];
	}

	Map<String, Integer> sysManageScope = new HashMap<String, Integer>();
	Map<String, List<TBMPsndocVO>> psndocMap = new HashMap<String, List<TBMPsndocVO>>();

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, UFDouble[]> calculateOvertimeFeeByDate(String pk_org, String[] pk_psndocs,
			UFLiteralDate startDate, UFLiteralDate endDate, UFDouble curNodeHours, OTSChainNode unSavedNodes,
			String pk_item_group, boolean isLeave) throws BusinessException {
		Map<String, UFDouble[]> ret = new HashMap<String, UFDouble[]>();
		if (pk_psndocs != null && pk_psndocs.length > 0) {
			initOvertimeLimitScopeMap(pk_org, pk_psndocs, endDate);
			// int counter = 0;
			for (String pk_psndoc : pk_psndocs) {
				long start = System.currentTimeMillis();
				// counter++;
				PeriodVO[] threePeriods = null;
				Collection<PeriodVO> pds = this.getBaseDao().retrieveByClause(PeriodVO.class,
						"pk_org='" + pk_org + "' and '" + startDate.toString() + "' between begindate and enddate");
				PeriodVO periodCurrent = (pds == null || pds.size() == 0) ? null : pds.toArray(new PeriodVO[0])[0];
				if (periodCurrent == null) {
					throw new BusinessException("取當前期間錯誤");
				}

				// 取該員工結算週期
				OvertimeLimitScopeEnum curStatScope = getPsnStatScope(pk_psndoc, startDate, endDate);
				if (curStatScope == null) {
					continue;
				}

				if (curStatScope.equals(OvertimeLimitScopeEnum.THREEMONTH)) {
					threePeriods = getThreePeriodVOs(pk_org, startDate, pk_psndoc, periodCurrent);
				} else {
					threePeriods = new PeriodVO[1];
					threePeriods[0] = periodCurrent;
				}

				UFLiteralDate sumStartDate = threePeriods[0].getBegindate();
				UFLiteralDate sumEndDate = threePeriods.length == 3 ? threePeriods[2].getEnddate() : threePeriods[0]
						.getEnddate();

				if (endDate != null) {
					sumEndDate = sumEndDate.after(endDate) ? endDate : sumEndDate;
				}

				// MOD 離職計算期間後至當月最後一天的加班費
				// added by ssx on 2018-04-05
				if (isLeave) {
					sumStartDate = endDate.getDateAfter(1);
					sumEndDate = sumStartDate.getDateAfter(sumStartDate.getDaysMonth() - sumStartDate.getDay()); // 月末最後一天

					// 離職日期在期間後至當月最後一天之間，否則不計算
					int isleavepsn = (int) this.getBaseDao().executeQuery(
							"select count(*) from hi_psnjob where trnsevent=4 and pk_psndoc = '" + pk_psndoc
									+ "' and begindate between '" + sumStartDate.toString() + "' and '"
									+ sumEndDate.toString() + "';", new ColumnProcessor());
					if (isleavepsn <= 0) {
						continue;
					}
				}
				// end MOD

				String strSQL = "SELECT  regdate, " + " case when (" + "( iscompensation='Y'"
						+ (isLeave ? "" : "AND issettled = 'Y' ")
						+ " AND expirydate BETWEEN '"
						+ sumStartDate
						+ "' AND '"
						+ sumEndDate
						+ "')"
						+ ") then sum(remainhourstaxfree * taxfreerate) else 0 end  dayhourstaxfree_comp,"
						+ " case when ("
						+ "(iscompensation='N' AND regdate BETWEEN '"
						+ sumStartDate
						+ "' AND '"
						+ sumEndDate
						+ "')"
						+ ") then sum(remainhourstaxfree * taxfreerate) else 0 end  dayhourstaxfree_nocomp,"
						+ " case when ("
						+ "( iscompensation='Y'"
						+ (isLeave ? "" : "AND issettled = 'Y' ")
						+ " AND expirydate BETWEEN '"
						+ sumStartDate
						+ "' AND '"
						+ sumEndDate
						+ "')"
						+ ") then sum(remainhourstaxable * taxablerate + extrahourstaxable * extrataxablerate) else 0 end  dayhourstaxable_comp, "
						+ " case when ("
						+ "(iscompensation='N' AND regdate BETWEEN '"
						+ sumStartDate
						+ "' AND '"
						+ sumEndDate
						+ "')"
						+ ") then sum(remainhourstaxable * taxablerate + extrahourstaxable * extrataxablerate) else 0 end  dayhourstaxable_nocomp  "
						+ " FROM  hrta_segdetail"
						+ " WHERE "
						+ "((iscompensation='N' AND regdate BETWEEN '"
						+ sumStartDate
						+ "' AND '"
						+ sumEndDate
						+ "')  OR  ( iscompensation='Y'"
						+ (isLeave ? "" : "AND issettled = 'Y' ")
						+ " AND expirydate BETWEEN '"
						+ sumStartDate
						+ "' AND '"
						+ sumEndDate
						+ "'))"
						+ " and pk_psndoc = '"
						+ pk_psndoc
						+ "'"
						+ " group by pk_psndoc, regdate,iscompensation, issettled, expirydate order by pk_psndoc, regdate";
				List<Map<String, Object>> dayHoursMapList = (List<Map<String, Object>>) this.getBaseDao().executeQuery(
						strSQL, new MapListProcessor());

				UFDouble amountTaxfree_comp = UFDouble.ZERO_DBL;
				UFDouble amountTaxfree_nocomp = UFDouble.ZERO_DBL;
				UFDouble amountTaxable_comp = UFDouble.ZERO_DBL;
				UFDouble amountTaxable_nocomp = UFDouble.ZERO_DBL;
				if (dayHoursMapList != null && dayHoursMapList.size() > 0) {
					strSQL = "select salarydate, sum(hoursalary) hoursalary from wa_daysalary where pk_psndoc = '"
							+ pk_psndoc + "' and pk_group_item = '" + pk_item_group + "' and salarydate between '"
							+ sumStartDate + "' and '" + sumEndDate + "' group by salarydate";
					List<Map<String, Object>> hourPayMapList = (List<Map<String, Object>>) this.getBaseDao()
							.executeQuery(strSQL, new MapListProcessor());

					for (Map<String, Object> dayHoursMap : dayHoursMapList) {
						UFLiteralDate regDate = new UFLiteralDate((String) dayHoursMap.get("regdate"));
						UFDouble hourPay = UFDouble.ZERO_DBL;
						for (Map<String, Object> hourPayMap : hourPayMapList) {
							if (regDate.toString().equals(hourPayMap.get("salarydate"))) {
								hourPay = new UFDouble(String.valueOf(hourPayMap.get("hoursalary")));
							}
						}

						if (hourPay.equals(UFDouble.ZERO_DBL)) {
							hourPay = this.getPsnHourPay(pk_org, pk_psndoc, regDate, pk_item_group);
						}
						UFDouble otAmountTaxfree_comp = hourPay.setScale(1, UFDouble.ROUND_HALF_UP)
								.multiply(new UFDouble(String.valueOf(dayHoursMap.get("dayhourstaxfree_comp"))))
								.setScale(1, UFDouble.ROUND_HALF_UP);
						UFDouble otAmountTaxfree_nocomp = hourPay.setScale(1, UFDouble.ROUND_HALF_UP)
								.multiply(new UFDouble(String.valueOf(dayHoursMap.get("dayhourstaxfree_nocomp"))))
								.setScale(1, UFDouble.ROUND_HALF_UP);
						UFDouble otAmountTaxable_comp = hourPay.setScale(1, UFDouble.ROUND_HALF_UP)
								.multiply(new UFDouble(String.valueOf(dayHoursMap.get("dayhourstaxable_comp"))))
								.setScale(1, UFDouble.ROUND_HALF_UP);
						UFDouble otAmountTaxable_nocomp = hourPay.setScale(1, UFDouble.ROUND_HALF_UP)
								.multiply(new UFDouble(String.valueOf(dayHoursMap.get("dayhourstaxable_nocomp"))))
								.setScale(1, UFDouble.ROUND_HALF_UP);
						amountTaxfree_comp = otAmountTaxfree_comp.add(amountTaxfree_comp);
						amountTaxfree_nocomp = otAmountTaxfree_nocomp.add(amountTaxfree_nocomp);
						amountTaxable_comp = otAmountTaxable_comp.add(amountTaxable_comp);
						amountTaxable_nocomp = otAmountTaxable_nocomp.add(amountTaxable_nocomp);
					}
				}

				ret.put(pk_psndoc,
						new UFDouble[] { amountTaxfree_comp.setScale(0, UFDouble.ROUND_HALF_UP),
								amountTaxfree_nocomp.setScale(0, UFDouble.ROUND_HALF_UP),
								amountTaxable_comp.setScale(0, UFDouble.ROUND_HALF_UP),
								amountTaxable_nocomp.setScale(0, UFDouble.ROUND_HALF_UP) });

				long end = System.currentTimeMillis();
				// Logger.error(" --- OvertimeFee PSN (" + counter + "/" +
				// pk_psndocs.length + "):" + pk_psndoc
				// + " --- CalculateDuration:" + String.valueOf(end - start));
				Logger.error(" --- OvertimeFee PSN:" + pk_psndoc + " --- CalculateDuration:"
						+ String.valueOf(end - start));
			}

			// 清空缓存
			// ssx added on 2019-04-10
			psndocMap = new HashMap<String, List<TBMPsndocVO>>();
		}
		return ret;
	}

	@Override
	public Map<String, UFDouble[]> calculateTaxableByDate(String pk_org, String[] pk_psndocs, UFLiteralDate startDate,
			UFLiteralDate endDate, UFDouble curNodeHours, OTSChainNode unSavedNodes, String pk_item_group,
			boolean isToRest) throws BusinessException {
		Map<String, UFDouble[]> ret = new HashMap<String, UFDouble[]>();
		if (pk_psndocs != null && pk_psndocs.length > 0) {

			initOvertimeLimitScopeMap(pk_org, pk_psndocs, endDate); // MOD
																	// 薪資計算效率優化
																	// by ssx
																	// added on
																	// 2019-02-01

			Map<String, OTSChainNode> psnNodeMap = OTSChainUtils.buildChainPsnNodeMap(pk_psndocs, endDate, null,
					isToRest, false, true, true, false);

			for (String pk_psndoc : pk_psndocs) {
				long start = System.currentTimeMillis();

				UFDouble[] retValues = new UFDouble[2];
				retValues[0] = UFDouble.ZERO_DBL;
				retValues[1] = UFDouble.ZERO_DBL;
				UFDouble totalTaxFreeHours = UFDouble.ZERO_DBL;
				UFDouble totalTaxableHours = UFDouble.ZERO_DBL;
				UFDouble totalTaxFreeAmount = UFDouble.ZERO_DBL;
				UFDouble totalTaxableAmount = UFDouble.ZERO_DBL;

				UFDouble totalHours = UFDouble.ZERO_DBL;

				UFDouble curUnSaveTaxFreeHours = UFDouble.ZERO_DBL;
				UFDouble curUnSaveTaxableHours = UFDouble.ZERO_DBL;

				if (unSavedNodes != null) {
					OTSChainNode curSavedNode = unSavedNodes;
					do {
						curUnSaveTaxFreeHours = curUnSaveTaxFreeHours.add(curSavedNode.getNodeData().getHourstaxfree());
						curUnSaveTaxableHours = curUnSaveTaxableHours.add(curSavedNode.getNodeData().getHourstaxable());
						curSavedNode = curSavedNode.getNextNode();
					} while (curSavedNode != null);
				}

				totalHours = totalHours.add(curUnSaveTaxFreeHours).add(curUnSaveTaxableHours);

				OTSChainNode curNode = psnNodeMap.get(pk_psndoc);

				// 考勤規則
				TimeRuleVO timerule = getTimeRule(pk_org);

				// 一個週期內加班不能超過的時數
				UFDouble taxFreeLimitHours = timerule.getCtrlothours();
				// 一或三個週期內加班不能超過的總時數
				UFDouble totalTaxFreeLimitHours = taxFreeLimitHours;

				PeriodVO[] threePeriods = null;
				PeriodVO periodCurrent = getPeriodService().queryByDate(pk_org, startDate);
				if (periodCurrent == null) {
					throw new BusinessException("取當前期間錯誤");
				}

				// 是否免稅
				boolean isTaxFree = true;
				if (curNode != null) {
					// 取該員工結算週期
					OvertimeLimitScopeEnum curStatScope = getPsnStatScope(pk_psndoc,
							curNode.getNodeData().getRegdate(), curNode.getNodeData().getRegdate());

					if (curStatScope == null) {
						continue;
					}

					if (curStatScope.equals(OvertimeLimitScopeEnum.THREEMONTH)) {
						taxFreeLimitHours = timerule.getCtrlothours3();
						totalTaxFreeLimitHours = timerule.getCtrlothours3();
						threePeriods = getThreePeriodVOs(pk_org, startDate, pk_psndoc, periodCurrent);
					} else {
						threePeriods = new PeriodVO[1];
						threePeriods[0] = periodCurrent;
					}

					UFLiteralDate sumStartDate = threePeriods[0].getBegindate();
					UFLiteralDate sumEndDate = threePeriods.length == 3 ? threePeriods[2].getEnddate()
							: threePeriods[0].getEnddate();

					if (endDate != null) {
						sumEndDate = sumEndDate.after(endDate) ? endDate : sumEndDate;
					}

					boolean needRound = false; // 是否需要取整，當前計算日期與上次取的是否同一天，如果是則不取整
					while (curNode != null && curNode.getNodeData() != null) {
						// ssx added for 按日取整及總計取整
						UFLiteralDate curDate = curNode.getNodeData().getRegdate();

						// 當前節點時數不為空，當前節點明細為空的，即為最後一次進入
						if (curNode.getNodeData() == null && curNodeHours != null) {
							if (!isTaxFree) {
								// 已進入應稅範圍
								totalTaxableHours = totalTaxableHours.add(curNodeHours);
							} else {
								if (totalHours.add(curNodeHours).doubleValue() <= totalTaxFreeLimitHours.doubleValue()) {
									// 加總後仍在免稅時數範圍內
									totalTaxFreeHours = totalTaxFreeHours.add(curNodeHours);
									totalTaxableHours = UFDouble.ZERO_DBL;
								} else {
									// 正好超過免稅時數
									totalTaxableHours = totalHours.add(curNodeHours).sub(totalTaxFreeLimitHours);
									totalTaxFreeHours = totalTaxFreeHours.add(curNodeHours.sub(totalTaxableHours));
								}
							}
							break;
						}

						if (curNode.getNodeData().getPk_org().equals(pk_org)) {
							// 註冊日在日期範圍內，納入加班費統計
							if (curNode.getNodeData().getRegdate().isSameDate(sumStartDate)
									|| curNode.getNodeData().getRegdate().after(sumStartDate)
									&& (curNode.getNodeData().getRegdate().isSameDate(sumEndDate) || curNode
											.getNodeData().getRegdate().before(sumEndDate))) {

								// ssx added for 按日取整及總計取整
								needRound = !curNode.getNodeData().getRegdate().isSameDate(curDate);

								if (needRound) {
									totalTaxableAmount = totalTaxableAmount.setScale(1, UFDouble.ROUND_HALF_UP);
									totalTaxFreeAmount = totalTaxFreeAmount.setScale(1, UFDouble.ROUND_HALF_UP);
								}

								curDate = curNode.getNodeData().getRegdate();

								UFDouble curHours = getHoursInScope(curNode.getNodeData());

								// 纍加加班時數
								totalHours = totalHours.add(curHours);
								if (isTaxFree) {
									if (totalHours.doubleValue() <= totalTaxFreeLimitHours.doubleValue()) {
										// 補休統計已打結算標記且最長可休日期在範圍內的，其餘的統計未打結算標記的
										if (curNode.getNodeData().getIscompensation().booleanValue()) {
											if (curNode.getNodeData().getIssettled().booleanValue()
													&& (curNode.getNodeData().getExpirydate().isSameDate(sumStartDate) || curNode
															.getNodeData().getExpirydate().after(sumStartDate)
															&& (curNode.getNodeData().getExpirydate()
																	.isSameDate(sumEndDate) || curNode.getNodeData()
																	.getExpirydate().before(sumEndDate)))) {
												// 在免稅時數範圍內的，纍計到免稅加班時數
												totalTaxFreeHours = totalTaxFreeHours.add(curNode.getNodeData()
														.getHourstaxfree());
												// 在免稅時數範圍內的，纍計到免稅加班費
												UFDouble otAmount = UFDouble.ZERO_DBL;
												if (!StringUtils.isEmpty(pk_item_group)) {
													otAmount = getOTAmount(curNode.getNodeData().getTaxfreerate(),
															curNode.getNodeData().getHourlypay(), curNode.getNodeData()
																	.getRemainhourstaxfree(), curNode.getNodeData(),
															DaySalaryEnum.TBMHOURSALARY, pk_item_group);
												}
												totalTaxFreeAmount = totalTaxFreeAmount.add(otAmount);
											}
										} else {
											// 在免稅時數範圍內的，纍計到免稅加班時數
											totalTaxFreeHours = totalTaxFreeHours.add(curNode.getNodeData()
													.getHourstaxfree());
											// 在免稅時數範圍內的，纍計到免稅加班費
											UFDouble otAmount = UFDouble.ZERO_DBL;
											if (!StringUtils.isEmpty(pk_item_group)) {
												otAmount = getOTAmount(curNode.getNodeData().getTaxfreerate(), curNode
														.getNodeData().getHourlypay(), curNode.getNodeData()
														.getRemainhourstaxfree(), curNode.getNodeData(),
														DaySalaryEnum.TBMHOURSALARY, pk_item_group);
											}
											totalTaxFreeAmount = totalTaxFreeAmount.add(otAmount);
										}
									} else {
										// 正好超過免稅時數
										totalTaxableHours = totalHours.sub(totalTaxFreeLimitHours);
										totalTaxFreeHours = curHours.sub(totalHours.sub(totalTaxFreeLimitHours));
										// 補休統計已打結算標記且最長可休日期在範圍內的，其餘的統計未打結算標記的
										if (curNode.getNodeData().getIscompensation().booleanValue()) {
											if (curNode.getNodeData().getIssettled().booleanValue()
													&& (curNode.getNodeData().getExpirydate().isSameDate(sumStartDate) || curNode
															.getNodeData().getExpirydate().after(sumStartDate)
															&& (curNode.getNodeData().getExpirydate()
																	.isSameDate(sumEndDate) || curNode.getNodeData()
																	.getExpirydate().before(sumEndDate)))) {

												UFDouble otAmount = UFDouble.ZERO_DBL;
												if (!StringUtils.isEmpty(pk_item_group)) {
													otAmount = getOTAmount(curNode.getNodeData().getTaxablerate(),
															curNode.getNodeData().getHourlypay(), curNode.getNodeData()
																	.getRemainhourstaxable(), curNode.getNodeData(),
															DaySalaryEnum.TBMHOURSALARY, pk_item_group);
												}
												totalTaxableAmount = totalTaxableAmount.add(otAmount);

												if (!StringUtils.isEmpty(pk_item_group)) {
													otAmount = getOTAmount(curNode.getNodeData().getTaxfreerate(),
															curNode.getNodeData().getHourlypay(), curNode.getNodeData()
																	.getRemainhourstaxfree(), curNode.getNodeData(),
															DaySalaryEnum.TBMHOURSALARY, pk_item_group);
												}
												totalTaxFreeAmount = totalTaxFreeAmount.add(otAmount);
											}
										} else {
											UFDouble otAmount = UFDouble.ZERO_DBL;
											if (!StringUtils.isEmpty(pk_item_group)) {
												otAmount = getOTAmount(curNode.getNodeData().getTaxablerate(), curNode
														.getNodeData().getHourlypay(), curNode.getNodeData()
														.getRemainhourstaxable(), curNode.getNodeData(),
														DaySalaryEnum.TBMHOURSALARY, pk_item_group);
											}
											totalTaxableAmount = totalTaxableAmount.add(otAmount);

											if (!StringUtils.isEmpty(pk_item_group)) {
												otAmount = getOTAmount(curNode.getNodeData().getTaxfreerate(), curNode
														.getNodeData().getHourlypay(), curNode.getNodeData()
														.getRemainhourstaxfree(), curNode.getNodeData(),
														DaySalaryEnum.TBMHOURSALARY, pk_item_group);
											}
											totalTaxFreeAmount = totalTaxFreeAmount.add(otAmount);
										}
										isTaxFree = false;
									}
								} else {
									totalTaxableHours = totalTaxableHours.add(curNode.getNodeData().getHourstaxable());
									// 超過免稅時數範圍的，纍計到應稅加班費
									// 補休統計已打結算標記且最長可休日期在範圍內的，其餘的統計未打結算標記的
									if (curNode.getNodeData().getIscompensation().booleanValue()) {
										if (curNode.getNodeData().getIssettled().booleanValue()
												&& (curNode.getNodeData().getExpirydate().isSameDate(sumStartDate) || curNode
														.getNodeData().getExpirydate().after(sumStartDate)
														&& (curNode.getNodeData().getExpirydate()
																.isSameDate(sumEndDate) || curNode.getNodeData()
																.getExpirydate().before(sumEndDate)))) {
											UFDouble otAmount = UFDouble.ZERO_DBL;
											if (!StringUtils.isEmpty(pk_item_group)) {
												otAmount = getOTAmount(curNode.getNodeData().getTaxablerate(), curNode
														.getNodeData().getHourlypay(), curNode.getNodeData()
														.getRemainhourstaxable(), curNode.getNodeData(),
														DaySalaryEnum.TBMHOURSALARY, pk_item_group);
											}
											totalTaxableAmount = totalTaxableAmount.add(otAmount);
										}
									} else {
										UFDouble otAmount = UFDouble.ZERO_DBL;
										if (!StringUtils.isEmpty(pk_item_group)) {
											otAmount = getOTAmount(curNode.getNodeData().getTaxablerate(), curNode
													.getNodeData().getHourlypay(), curNode.getNodeData()
													.getRemainhourstaxable(), curNode.getNodeData(),
													DaySalaryEnum.TBMHOURSALARY, pk_item_group);
										}
										totalTaxableAmount = totalTaxableAmount.add(otAmount);
									}
								}
							} else {
								// 註冊日不在日期範圍內的，要看補休統計已打結算標記且最長可休日期在範圍內
								if (curNode.getNodeData().getIscompensation().booleanValue()) {
									if (curNode.getNodeData().getIssettled().booleanValue()
											&& (curNode.getNodeData().getExpirydate().isSameDate(sumStartDate) || curNode
													.getNodeData().getExpirydate().after(sumStartDate)
													&& (curNode.getNodeData().getExpirydate().isSameDate(sumEndDate) || curNode
															.getNodeData().getExpirydate().before(sumEndDate)))) {
										UFDouble otAmount = UFDouble.ZERO_DBL;
										if (!StringUtils.isEmpty(pk_item_group)) {
											otAmount = getOTAmount(curNode.getNodeData().getTaxablerate(), curNode
													.getNodeData().getHourlypay(), curNode.getNodeData()
													.getRemainhourstaxable(), curNode.getNodeData(),
													DaySalaryEnum.TBMHOURSALARY, pk_item_group);
										}
										totalTaxableAmount = totalTaxableAmount.add(otAmount);

										if (!StringUtils.isEmpty(pk_item_group)) {
											otAmount = getOTAmount(curNode.getNodeData().getTaxfreerate(), curNode
													.getNodeData().getHourlypay(), curNode.getNodeData()
													.getRemainhourstaxfree(), curNode.getNodeData(),
													DaySalaryEnum.TBMHOURSALARY, pk_item_group);
										}
										totalTaxFreeAmount = totalTaxFreeAmount.add(otAmount);
									}
								}
							}
							curNode = curNode.getNextNode();

							if (curNodeHours != null && curNode == null) {
								curNode = new OTSChainNode();
							}

						}
					}

					totalTaxableAmount = totalTaxableAmount.setScale(1, UFDouble.ROUND_HALF_UP);
					totalTaxFreeAmount = totalTaxFreeAmount.setScale(1, UFDouble.ROUND_HALF_UP);

					if (curNodeHours == null) {
						retValues[0] = totalTaxFreeAmount.setScale(0, UFDouble.ROUND_HALF_UP);
						retValues[1] = totalTaxableAmount.setScale(0, UFDouble.ROUND_HALF_UP);
					}
				}

				if (curNodeHours != null) {
					if (!isTaxFree) {
						// 已進入應稅範圍
						totalTaxFreeHours = UFDouble.ZERO_DBL;
						totalTaxableHours = curNodeHours;
					} else {
						if (totalHours.add(curNodeHours).doubleValue() <= totalTaxFreeLimitHours.doubleValue()) {
							// 加總後仍在免稅時數範圍內
							totalTaxFreeHours = curNodeHours;
							totalTaxableHours = UFDouble.ZERO_DBL;
						} else {
							// 正好超過免稅時數
							totalTaxableHours = totalHours.add(curNodeHours).sub(totalTaxFreeLimitHours);
							totalTaxFreeHours = curNodeHours.sub(totalTaxableHours);
						}
					}
					retValues[0] = totalTaxFreeHours;
					retValues[1] = totalTaxableHours;
				}

				ret.put(pk_psndoc, retValues);

				long end = System.currentTimeMillis();
				Logger.error(" --- OvertimeFee PSN:" + pk_psndoc + " --- CalculateDuration:"
						+ String.valueOf(end - start));
			}

		}
		return ret;
	}

	private PeriodVO[] getThreePeriodVOs(String pk_org, UFLiteralDate startDate, String pk_psndoc,
			PeriodVO periodCurrent) throws BusinessException {
		PeriodVO[] threePeriods;
		String startPeriod = "";
		// 起算期間
		try {
			startPeriod = SysInitQuery.getParaString(pk_org, "TWHRT07"); // 加班校驗起始年月
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}

		String strWhere = "pk_org='" + pk_org + "' ";

		if (!StringUtils.isEmpty(startPeriod)) {
			strWhere += " and timeyear='" + startPeriod.substring(0, 4) + "' and timemonth='"
					+ startPeriod.substring(4, 6) + "'";
		} else {
			strWhere += " and '" + startDate.toString() + "' between begindate and enddate ";
		}
		Collection<PeriodVO> periodStart = this.getBaseDao().retrieveByClause(PeriodVO.class, strWhere);
		if (periodStart == null || periodStart.size() == 0) {
			throw new BusinessException("無法確定加班上限統計起算期間。");
		}

		int startMonth = Integer.valueOf(periodStart.toArray(new PeriodVO[0])[0].getTimemonth());
		int currentMonth = startDate.getMonth();

		threePeriods = new PeriodVO[3];
		if (startMonth % 3 == currentMonth % 3) {
			// 0:0 1:1 2:2 後補2個
			// 當前期間為起始期間，後補兩個期間
			threePeriods[0] = periodCurrent;
			threePeriods[1] = this.getPeriodService().queryNextPeriod(pk_org, threePeriods[0].getBegindate());
			threePeriods[2] = this.getPeriodService().queryNextPeriod(pk_org, threePeriods[1].getBegindate());
		} else if (((currentMonth % 3) - (startMonth % 3) == 1) || (currentMonth % 3 + 3) - (startMonth % 3) == 1) {
			// 0:1 1:2 2:0 前後各1
			// 當前期間為中間期間，前後各補一個期間
			threePeriods[1] = periodCurrent;
			threePeriods[0] = this.getPeriodService().queryPreviousPeriod(pk_org, threePeriods[1].getBegindate());
			threePeriods[2] = this.getPeriodService().queryNextPeriod(pk_org, threePeriods[1].getBegindate());
		} else {
			// 0:2 1:0 2:1 前補2個
			// 當前期間為最後一個期間，前補兩個期間
			threePeriods[2] = periodCurrent;
			threePeriods[1] = this.getPeriodService().queryPreviousPeriod(pk_org, threePeriods[2].getBegindate());
			threePeriods[0] = this.getPeriodService().queryPreviousPeriod(pk_org, threePeriods[1].getBegindate());
		}
		return threePeriods;
	}

	Map<String, SegRuleTermVO> mapSegTerm = new HashMap<String, SegRuleTermVO>();

	private UFDouble getHoursInScope(SegDetailVO segdetail) throws BusinessException {
		SegRuleTermVO term = null;
		if (!mapSegTerm.containsKey(segdetail.getPk_segruleterm())) {
			term = (SegRuleTermVO) this.getBaseDao().retrieveByPK(SegRuleTermVO.class, segdetail.getPk_segruleterm());
		} else {
			term = mapSegTerm.get(segdetail.getPk_segruleterm());
		}

		UFDouble rtn = UFDouble.ZERO_DBL;
		if (term.getIslimitscope() != null && term.getIslimitscope().booleanValue()) {
			// 計入加班上限統計
			rtn = segdetail.getHours();
		}
		return rtn;
	}

	private OvertimeLimitScopeEnum getPsnStatScope(String pk_psndoc, UFLiteralDate startDate, UFLiteralDate endDate)
			throws BusinessException {
		int chkScope = -1;
		if (psndocMap.containsKey(pk_psndoc)) {
			// 校验一下加班管控 by he
			for (TBMPsndocVO vo : psndocMap.get(pk_psndoc)) {
				if ((vo.getBegindate().isSameDate(endDate) || vo.getBegindate().before(endDate))
						&& (vo.getEnddate() == null || vo.getEnddate().isSameDate(startDate) || vo.getEnddate().after(
								startDate))) {
					if (null == vo.getOvertimecontrol() || vo.getOvertimecontrol().equals("~")) {
						PsndocVO psn = (PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class, pk_psndoc);
						Logger.error("無法找到員工 [" + psn.getCode() + "] 的加班控管");
						return null;
					}
					chkScope = vo.getOvertimecontrol();
				}
			}
		}

		if (chkScope == 1) {
			return OvertimeLimitScopeEnum.ONEMONTH;
		} else if (chkScope == 2) {
			return OvertimeLimitScopeEnum.THREEMONTH;
		}

		PsndocVO psn = (PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class, pk_psndoc);
		Logger.error("無法找到員工 [" + psn.getCode() + "] 的加班上限統計範圍");
		return null;
	}

	@SuppressWarnings("unchecked")
	private void initOvertimeLimitScopeMap(String pk_org, String[] pk_psndocs, UFLiteralDate endDate)
			throws BusinessException {
		String cond = "";
		if (pk_psndocs != null && pk_psndocs.length < 200) {
			cond = " pk_org='" + pk_org + "' and pk_psndoc in (" + (new InSQLCreator()).getInSQL(pk_psndocs) + ")";
		}

		if (StringUtils.isEmpty(cond)) {
			InSQLCreator insql = new InSQLCreator();
			String strTempTable = insql.recreateTempTable();
			insql.createTempTable(strTempTable, pk_psndocs);
			cond = " pk_org='" + pk_org + "' and pk_psndoc in (select " + TempTableVO.IN_PK + " from " + strTempTable
					+ ")";
		}

		Collection<TBMPsndocVO> psndoc = this.getBaseDao().retrieveByClause(TBMPsndocVO.class, cond);
		if (psndoc != null && psndoc.size() > 0) {
			for (TBMPsndocVO vo : psndoc.toArray(new TBMPsndocVO[0])) {
				if (!psndocMap.containsKey(vo.getPk_psndoc())) {
					psndocMap.put(vo.getPk_psndoc(), new ArrayList<TBMPsndocVO>());
				}
				psndocMap.get(vo.getPk_psndoc()).add(vo);
			}
		}
	}

	@Override
	public Map<String, UFDouble> calculateTaxFreeAmountByPeriod(String pk_org, String[] pk_psndocs, String cyear,
			String cperiod, String pk_item_group) throws BusinessException {
		PeriodVO period = getPeriodService().queryByYearMonth(pk_org, cyear, cperiod);

		Map<String, UFDouble[]> taxAmounts = calculateTaxableByDate(pk_org, pk_psndocs, period.getBegindate(),
				period.getEnddate(), null, null, pk_item_group, true);

		Map<String, UFDouble> ret = new HashMap<String, UFDouble>();
		if (taxAmounts != null && taxAmounts.size() > 0) {
			for (Entry<String, UFDouble[]> amount : taxAmounts.entrySet()) {
				ret.put(amount.getKey(), amount.getValue()[0]);
			}
		}
		return ret;
	}

	@Override
	public Map<String, UFDouble> calculateTaxableAmountByPeriod(String pk_org, String[] pk_psndocs, String cyear,
			String cperiod, String pk_item_group) throws BusinessException {
		PeriodVO period = getPeriodService().queryByYearMonth(pk_org, cyear, cperiod);

		Map<String, UFDouble[]> taxAmounts = calculateTaxableByDate(pk_org, pk_psndocs, period.getBegindate(),
				period.getEnddate(), null, null, pk_item_group, true);

		Map<String, UFDouble> ret = new HashMap<String, UFDouble>();
		if (taxAmounts != null && taxAmounts.size() > 0) {
			for (Entry<String, UFDouble[]> amount : taxAmounts.entrySet()) {
				ret.put(amount.getKey(), amount.getValue()[1]);
			}
		}
		return ret;
	}

	@Override
	public Map<String, UFDouble> getHoursToRestByScope(UFLiteralDate startDate, UFLiteralDate endDate,
			String[] pk_psndocs) throws BusinessException {
		return getSegDetailSummary(pk_psndocs, startDate, endDate, SegDetailVO.HOURSTOREST, null);
	}

	@Override
	public Map<String, UFDouble> getHoursToRestByPeriod(String pk_org, String cyear, String cperiod, String[] pk_psndocs)
			throws BusinessException {
		Map<String, UFDouble> ret = new HashMap<String, UFDouble>();
		if (pk_psndocs != null && pk_psndocs.length > 0) {
			PeriodVO period = getPeriodService().queryByYearMonth(pk_org, cyear, cperiod);

			if (period != null) {
				ret = getHoursToRestByScope(period.getBegindate(), period.getEnddate(), pk_psndocs);
			}
		}
		return ret;
	}

	@Override
	public Map<String, Map<QueryValueTypeEnum, UFDouble>> getOvertimeHours(String pk_org, String[] pk_psndocs,
			UFLiteralDate overtimeDate) throws BusinessException {
		Map<String, Map<QueryValueTypeEnum, UFDouble>> ret = new HashMap<String, Map<QueryValueTypeEnum, UFDouble>>();

		if (pk_psndocs != null) {
			for (String pk_psndoc : pk_psndocs) {
				OTSChainNode curNode = OTSChainUtils.buildChainNodes(pk_psndoc, overtimeDate, null, false, true, true,
						false, true);

				UFDouble normalHours = UFDouble.ZERO_DBL;
				UFDouble offdayHours = UFDouble.ZERO_DBL;
				UFDouble holidayHours = UFDouble.ZERO_DBL;
				UFDouble nationalHours = UFDouble.ZERO_DBL;
				UFDouble totalFee = UFDouble.ZERO_DBL;

				while (curNode != null) {
					if (curNode.getNodeData().getPk_org().equals(pk_org)) {
						SegRuleVO rule = (SegRuleVO) this.getBaseDao().retrieveByPK(SegRuleVO.class,
								curNode.getNodeData().getPk_segrule());
						if (CalendarDateTypeEnum.HOLIDAY.toIntValue() == rule.getDatetype()) {
							// 例假日
							holidayHours = holidayHours.add(curNode.getNodeData().getRemainhours());
						} else if (CalendarDateTypeEnum.OFFDAY.toIntValue() == rule.getDatetype()) {
							// 休息日
							offdayHours = offdayHours.add(curNode.getNodeData().getRemainhours());
						} else if (CalendarDateTypeEnum.NORMAL.toIntValue() == rule.getDatetype()) {
							// 平日
							normalHours = normalHours.add(curNode.getNodeData().getRemainhours());
						} else if (CalendarDateTypeEnum.NATIONALDAY.toIntValue() == rule.getDatetype()) {
							// 國定假日
							nationalHours = nationalHours.add(curNode.getNodeData().getRemainhours());
						}
						totalFee = totalFee.add(curNode.getNodeData().getRemainamount());
					}
					curNode = curNode.getNextNode();
				}

				Map<QueryValueTypeEnum, UFDouble> hoursMap = new HashMap<QueryValueTypeEnum, UFDouble>();
				hoursMap.put(QueryValueTypeEnum.HOLIDAY, holidayHours);
				hoursMap.put(QueryValueTypeEnum.OFFDAY, offdayHours);
				hoursMap.put(QueryValueTypeEnum.NATIONALDAY, nationalHours);
				hoursMap.put(QueryValueTypeEnum.NORMAL, normalHours);
				hoursMap.put(QueryValueTypeEnum.ALL, holidayHours.add(offdayHours).add(nationalHours).add(normalHours));
				hoursMap.put(QueryValueTypeEnum.TOTALFEE, totalFee);
				ret.put(pk_psndoc, hoursMap);
			}
		}
		return ret;
	}

	public void setBaseDao(BaseDAO basedao) {
		baseDao = basedao;
	}

	public BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}

	public IPeriodQueryService getPeriodService() {
		if (periodService == null) {
			periodService = (IPeriodQueryService) NCLocator.getInstance().lookup(IPeriodQueryService.class);
		}
		return periodService;
	}

	public Map<String, OTSChainNode> getAllNode() {
		if (allNode == null) {
			allNode = new HashMap<String, OTSChainNode>();
		}
		return allNode;
	}

	@Override
	public Map<String, UFDouble> getOvertimeHoursByType(String pk_org, String[] pk_psndocs, UFLiteralDate overtimeDate,
			String pk_overtimetype) throws BusinessException {
		return getOvertimeHours(pk_org, pk_psndocs, overtimeDate, pk_overtimetype, false);
	}

	@Override
	public Map<String, UFDouble> getOvertimeToRestHoursByType(String pk_org, String[] pk_psndocs,
			UFLiteralDate overtimeDate, String pk_overtimetype) throws BusinessException {
		return getOvertimeHours(pk_org, pk_psndocs, overtimeDate, pk_overtimetype, true);
	}

	private Map<String, UFDouble> getOvertimeHours(String pk_org, String[] pk_psndocs, UFLiteralDate overtimeDate,
			String pk_overtimetype, boolean isToRest) throws BusinessException {
		Map<String, UFDouble> ret = new HashMap<String, UFDouble>();
		if (pk_psndocs != null && pk_psndocs.length > 0) {
			for (String pk_psndoc : pk_psndocs) {
				OTSChainNode curNode = OTSChainUtils.buildChainNodes(pk_psndoc, overtimeDate, null, isToRest, false,
						true, true, true);
				UFDouble otHours = UFDouble.ZERO_DBL;
				Map<String, String> pkTypes = new HashMap<String, String>();
				if (curNode != null) {
					while (curNode != null) {
						// 當前節點時數不為空，當前節點明細為空的，即為最後一次進入
						if (curNode.getNodeData() != null) {
							if (!pkTypes.containsKey(curNode.getNodeData().getPk_overtimereg())) {
								OvertimeRegVO otvo = (OvertimeRegVO) this.getBaseDao().retrieveByPK(
										OvertimeRegVO.class, curNode.getNodeData().getPk_overtimereg());
								if (otvo != null) {
									pkTypes.put(otvo.getPk_overtimereg(), otvo.getPk_overtimetype());
								}
							}

							if (pkTypes.containsKey(curNode.getNodeData().getPk_overtimereg())) {
								if (pk_overtimetype.equals(pkTypes.get(curNode.getNodeData().getPk_overtimereg()))) {
									otHours = otHours.add(curNode.getNodeData().getHours());
								}
							}
						}
						curNode = curNode.getNextNode();
					}
				}
				ret.put(pk_psndoc, otHours);
			}
		}
		return ret;
	}

	@Override
	public Map<String, UFDouble> getOvertimeTaxfreeAmount(String[] pk_psndocs, UFLiteralDate startDate,
			UFLiteralDate endDate) throws BusinessException {
		return getSegDetailSummary(pk_psndocs, startDate, endDate, SegDetailVO.REMAINAMOUNTTAXFREE, null);
	}

	@Override
	public Map<String, UFDouble> getOvertimeTaxableAmount(String[] pk_psndocs, UFLiteralDate startDate,
			UFLiteralDate endDate) throws BusinessException {
		return getSegDetailSummary(pk_psndocs, startDate, endDate, SegDetailVO.REMAINAMOUNTTAXABLE,
				SegDetailVO.EXTRAAMOUNTTAXABLE);
	}

	private Map<String, UFDouble> getSegDetailSummary(String[] pk_psndocs, UFLiteralDate startDate,
			UFLiteralDate endDate, String digitPropName, String digitPropName2) throws BusinessException {
		Map<String, UFDouble> ret = new HashMap<String, UFDouble>();
		if (pk_psndocs != null && pk_psndocs.length > 0) {
			for (String pk_psndoc : pk_psndocs) {
				UFLiteralDate overtimeDate = startDate;
				OTSChainNode curNode = OTSChainUtils.buildChainNodes(pk_psndoc, overtimeDate, null, false, false, true,
						true, true);
				if (curNode != null) {
					while (curNode != null) {
						// 當前節點不為空，當前節點明細為空的，即為最後一次進入
						if (curNode.getNodeData() != null) {
							// 按最长可休日期结算加班费
							if ((curNode.getNodeData().getExpirydate().isSameDate(overtimeDate) || curNode
									.getNodeData().getExpirydate().after(overtimeDate))
									&& curNode.getNodeData().getExpirydate().isSameDate(endDate)
									|| curNode.getNodeData().getExpirydate().before(endDate)) {
								if (!ret.containsKey(pk_psndoc)) {
									ret.put(pk_psndoc, UFDouble.ZERO_DBL);
								}

								// // 重新取考勤時薪
								// UFDouble hourpay = getPsnHourPay(pk_psndoc,
								// curNode.getNodeData().getRegdate(),
								// DaySalaryEnum.TBMHOURSALARY);
								// // 與已記錄考勤時薪不同時重新計算剩余加班費金額
								// if
								// (!hourpay.equals(curNode.getNodeData().getHourlypay()))
								// {
								// curNode.getNodeData().setRemainamounttaxfree(
								// this.getOTAmount(curNode.getNodeData().getTaxfreerate(),
								// hourpay, curNode
								// .getNodeData().getRemainhourstaxfree(), null,
								// -1));
								// curNode.getNodeData().setRemainamounttaxable(
								// this.getOTAmount(curNode.getNodeData().getTaxablerate(),
								// hourpay, curNode
								// .getNodeData().getRemainhourstaxable(), null,
								// -1));
								// curNode.getNodeData().setRemainamount(
								// curNode.getNodeData().getRemainamounttaxfree()
								// .add(curNode.getNodeData().getRemainamounttaxable()));
								// OTSChainUtils.save(curNode);
								// }

								ret.get(pk_psndoc)
										.add(curNode.getNodeData().getAttributeValue(digitPropName) == null ? UFDouble.ZERO_DBL
												: (UFDouble) curNode.getNodeData().getAttributeValue(digitPropName))
										.add(StringUtils.isEmpty(digitPropName2) ? UFDouble.ZERO_DBL
												: (UFDouble) curNode.getNodeData().getAttributeValue(digitPropName2));
							}
						}
						curNode = curNode.getNextNode();
					}
				}
			}
		}
		return ret;
	}

	@Override
	public MonthStatOTCalcVO[] getOvertimeSalaryHoursByTBMPeriodSource(String pk_org, String[] pk_psndocs,
			String cyear, String cperiod, CalendarDateTypeEnum dateType, OvertimeSettleTypeEnum settleType)
			throws BusinessException {
		List<MonthStatOTCalcVO> ret = new ArrayList<MonthStatOTCalcVO>();
		if (pk_psndocs != null && pk_psndocs.length > 0) {
			PeriodVO period = getPeriodService().queryByYearMonth(pk_org, cyear, cperiod);
			for (String pk_psndoc : pk_psndocs) {
				List<MonthStatOTCalcVO> vos = getCurrentMonthSalaryHoursByDate(pk_psndoc, period.getBegindate(),
						period.getEnddate(), dateType, settleType);
				for (MonthStatOTCalcVO vo : vos) {
					vo.setPk_org(pk_org);
					vo.setCyear(cyear);
					vo.setCperiod(cperiod);
					vo.setPk_psndoc(pk_psndoc);
				}
				ret.addAll(vos);
			}
		}
		return ret.toArray(new MonthStatOTCalcVO[0]);
	}

	private List<MonthStatOTCalcVO> getCurrentMonthSalaryHoursByDate(String pk_psndoc, UFLiteralDate beginDate,
			UFLiteralDate endDate, CalendarDateTypeEnum dateType, OvertimeSettleTypeEnum settleType)
			throws BusinessException {
		List<MonthStatOTCalcVO> vos = new ArrayList<MonthStatOTCalcVO>();

		OTSChainNode curNode = OTSChainUtils.buildChainNodes(pk_psndoc, null, null, false, false, true, true, true);

		Map<OvertimeSettleTypeEnum, UFDouble> workDayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>(); // 平日
		Map<OvertimeSettleTypeEnum, UFDouble> holidayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>(); // 例假日
		Map<OvertimeSettleTypeEnum, UFDouble> offdayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>();// 休息日
		Map<OvertimeSettleTypeEnum, UFDouble> nationalDayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>();// 国假
		Map<OvertimeSettleTypeEnum, UFDouble> totalWorkAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>();// 平日合计
		Map<OvertimeSettleTypeEnum, UFDouble> totalHolidayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>();// 例假日合计
		Map<OvertimeSettleTypeEnum, UFDouble> totalOffdayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>();// 休息日合计
		Map<OvertimeSettleTypeEnum, UFDouble> totalNationalDayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>();// 国假合计

		if (curNode != null) {
			Map<String, SegRuleVO> ruleMap = new HashMap<String, SegRuleVO>();
			while (curNode != null) {
				// 當前節點不為空，當前節點明細為空的，即為最後一次進入
				if (curNode.getNodeData() != null) {
					String pk_segrule = curNode.getNodeData().getPk_segrule();
					// 加班日期在期间内
					if (!curNode.getNodeData().getRegdate().after(endDate)
							&& !curNode.getNodeData().getRegdate().before(beginDate)) {
						if (!ruleMap.containsKey(pk_segrule)) {
							// 缓存加班分段依据
							ruleMap.put(pk_segrule,
									(SegRuleVO) this.getBaseDao().retrieveByPK(SegRuleVO.class, pk_segrule));
						}

						if (dateType != null) {
							if (CalendarDateTypeEnum.NORMAL.equals(dateType)) {
								// 统计平日
								addInPeriodAmount(dateType, curNode, workDayAmount, ruleMap, pk_segrule);
							} else if (CalendarDateTypeEnum.HOLIDAY.equals(dateType)) {
								// 统计例假日
								addInPeriodAmount(dateType, curNode, holidayAmount, ruleMap, pk_segrule);
							} else if (CalendarDateTypeEnum.OFFDAY.equals(dateType)) {
								// 统计休息日
								addInPeriodAmount(dateType, curNode, offdayAmount, ruleMap, pk_segrule);
							} else if (CalendarDateTypeEnum.NATIONALDAY.equals(dateType)) {
								// 统计国假
								addInPeriodAmount(dateType, curNode, nationalDayAmount, ruleMap, pk_segrule);
							}
						} else {
							// 统计平日
							addInPeriodAmount(CalendarDateTypeEnum.NORMAL, curNode, workDayAmount, ruleMap, pk_segrule);
							// 统计例假日
							addInPeriodAmount(CalendarDateTypeEnum.HOLIDAY, curNode, holidayAmount, ruleMap, pk_segrule);
							// 统计休息日
							addInPeriodAmount(CalendarDateTypeEnum.OFFDAY, curNode, offdayAmount, ruleMap, pk_segrule);
							// 统计国假
							addInPeriodAmount(CalendarDateTypeEnum.NATIONALDAY, curNode, nationalDayAmount, ruleMap,
									pk_segrule);
						}
					}
					// 审核日期在范围内
					else if (!curNode.getNodeData().getApproveddate().after(endDate)
							&& !curNode.getNodeData().getApproveddate().before(beginDate)) {
						if (dateType != null && !curNode.getNodeData().getIscompensation().booleanValue()) {
							if (CalendarDateTypeEnum.NORMAL.equals(dateType)) {
								// 统计平日
								addInMap(workDayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode.getNodeData()
										.getRemainhours());
							} else if (CalendarDateTypeEnum.HOLIDAY.equals(dateType)) {
								// 统计例假日
								addInMap(holidayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode.getNodeData()
										.getRemainhours());
							} else if (CalendarDateTypeEnum.OFFDAY.equals(dateType)) {
								// 统计休息日
								addInMap(offdayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode.getNodeData()
										.getRemainhours());
							} else if (CalendarDateTypeEnum.NATIONALDAY.equals(dateType)) {
								// 统计国假
								addInMap(nationalDayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode
										.getNodeData().getRemainhours());
							}
						} else {
							if (!curNode.getNodeData().getIscompensation().booleanValue()) {
								// 统计平日
								addInMap(workDayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode.getNodeData()
										.getRemainhours());
								// 统计例假日
								addInMap(holidayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode.getNodeData()
										.getRemainhours());
								// 统计休息日
								addInMap(offdayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode.getNodeData()
										.getRemainhours());
								// 统计国假
								addInMap(nationalDayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode
										.getNodeData().getRemainhours());
							}
						}
					} else {
						// 超过截止时间即为结束
						break;
					}
				}
				curNode = curNode.getNextNode();
			}

			// 处理合计
			// 平日
			addInMap(totalWorkAmount, OvertimeSettleTypeEnum.TOTAL,
					workDayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOSALARY));
			addInMap(totalWorkAmount, OvertimeSettleTypeEnum.TOTAL,
					workDayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOREST));
			addInMap(totalWorkAmount, OvertimeSettleTypeEnum.TOTAL,
					workDayAmount.get(OvertimeSettleTypeEnum.OTHER_TOSALARY));
			// 例假日
			addInMap(totalHolidayAmount, OvertimeSettleTypeEnum.TOTAL,
					holidayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOSALARY));
			addInMap(totalHolidayAmount, OvertimeSettleTypeEnum.TOTAL,
					holidayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOREST));
			addInMap(totalHolidayAmount, OvertimeSettleTypeEnum.TOTAL,
					holidayAmount.get(OvertimeSettleTypeEnum.OTHER_TOSALARY));
			// 休息日
			addInMap(totalOffdayAmount, OvertimeSettleTypeEnum.TOTAL,
					offdayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOSALARY));
			addInMap(totalOffdayAmount, OvertimeSettleTypeEnum.TOTAL,
					offdayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOREST));
			addInMap(totalOffdayAmount, OvertimeSettleTypeEnum.TOTAL,
					offdayAmount.get(OvertimeSettleTypeEnum.OTHER_TOSALARY));
			// 国假
			addInMap(totalNationalDayAmount, OvertimeSettleTypeEnum.TOTAL,
					nationalDayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOSALARY));
			addInMap(totalNationalDayAmount, OvertimeSettleTypeEnum.TOTAL,
					nationalDayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOREST));
			addInMap(totalNationalDayAmount, OvertimeSettleTypeEnum.TOTAL,
					nationalDayAmount.get(OvertimeSettleTypeEnum.OTHER_TOSALARY));
		}

		convertToVO(vos, CalendarDateTypeEnum.NORMAL, workDayAmount);
		convertToVO(vos, CalendarDateTypeEnum.HOLIDAY, holidayAmount);
		convertToVO(vos, CalendarDateTypeEnum.OFFDAY, offdayAmount);
		convertToVO(vos, CalendarDateTypeEnum.NATIONALDAY, nationalDayAmount);

		convertToVO(vos, CalendarDateTypeEnum.NORMAL, totalWorkAmount);
		convertToVO(vos, CalendarDateTypeEnum.HOLIDAY, totalHolidayAmount);
		convertToVO(vos, CalendarDateTypeEnum.OFFDAY, totalOffdayAmount);
		convertToVO(vos, CalendarDateTypeEnum.NATIONALDAY, totalNationalDayAmount);

		return vos;
	}

	private void convertToVO(List<MonthStatOTCalcVO> vos, CalendarDateTypeEnum dateType,
			Map<OvertimeSettleTypeEnum, UFDouble> amountMap) {
		for (Entry<OvertimeSettleTypeEnum, UFDouble> amount : amountMap.entrySet()) {
			MonthStatOTCalcVO vo = new MonthStatOTCalcVO();
			vo.setSettleType(amount.getKey());
			vo.setHours(amount.getValue());
			vo.setDateType(dateType);
			vos.add(vo);
		}
	}

	private void addInPeriodAmount(CalendarDateTypeEnum dateType, OTSChainNode curNode,
			Map<OvertimeSettleTypeEnum, UFDouble> workDayAmount, Map<String, SegRuleVO> ruleMap, String pk_segrule) {
		if (ruleMap.get(pk_segrule).getDatetype() == dateType.toIntValue()) {
			if (!curNode.getNodeData().getIscompensation().booleanValue()) {
				// 本期转薪
				addInMap(workDayAmount, OvertimeSettleTypeEnum.PERIOD_TOSALARY, curNode.getNodeData().getRemainhours());
			} else {
				// 本期转休
				addInMap(workDayAmount, OvertimeSettleTypeEnum.PERIOD_TOSALARY, curNode.getNodeData().getRemainhours());
			}
		}
	}

	private void addInMap(Map<OvertimeSettleTypeEnum, UFDouble> dataMap, OvertimeSettleTypeEnum key, UFDouble value) {
		if (!dataMap.containsKey(key)) {
			dataMap.put(key, UFDouble.ZERO_DBL);
		}

		dataMap.put(key, dataMap.get(key).add(value == null ? UFDouble.ZERO_DBL : value));
	}

	@Override
	public OTLeaveBalanceVO[] getOvertimeToRestHoursByType(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate, String pk_overtimetype) throws BusinessException {
		List<OTLeaveBalanceVO> headvos = new ArrayList<OTLeaveBalanceVO>();

		pk_psndocs = OTLeaveBalanceUtils.getPsnListByDateScope(pk_org, pk_psndocs, beginDate, endDate);

		Map<String, UFLiteralDate> psnWorkStartDate = OTLeaveBalanceUtils.getPsnOrgMapByPsnBeginDate(pk_psndocs,
				beginDate, endDate);

		if (pk_psndocs != null && pk_psndocs.length > 0) {
			for (String pk_psndoc : pk_psndocs) {
				if (!psnWorkStartDate.containsKey(pk_psndoc) || psnWorkStartDate.get(pk_psndoc) == null) {
					Logger.error("員工年資起算日為空：PSN [" + pk_psndoc + "] ");
				} else {
					// 根據期間開始時間定位員工年資起算期間
					UFLiteralDate psnBeginDate = OTLeaveBalanceUtils.getBeginDateByWorkAgeStartDate(
							String.valueOf(beginDate.getYear()), psnWorkStartDate.get(pk_psndoc));
					UFLiteralDate psnendDate = OTLeaveBalanceUtils.getEndDateByWorkAgeStartDate(
							String.valueOf(beginDate.getYear()), psnWorkStartDate.get(pk_psndoc));
					if (psnBeginDate.after(beginDate)) {
						psnBeginDate = OTLeaveBalanceUtils.getBeginDateByWorkAgeStartDate(
								String.valueOf(beginDate.getYear() - 1), psnWorkStartDate.get(pk_psndoc));
						psnendDate = OTLeaveBalanceUtils.getEndDateByWorkAgeStartDate(
								String.valueOf(beginDate.getYear() - 1), psnWorkStartDate.get(pk_psndoc));
					}
					//

					UFDouble totalAmount = UFDouble.ZERO_DBL; // 享有
					UFDouble spentAmount = UFDouble.ZERO_DBL;// 已休
					UFDouble remainAmount = UFDouble.ZERO_DBL;// 剩余
					UFDouble frozenAmount = UFDouble.ZERO_DBL;// 冻结
					UFDouble useableAmount = UFDouble.ZERO_DBL; // 可用
					Map<String, UFLiteralDate> otDateMap = new HashMap<String, UFLiteralDate>();// 加班日期
					Map<String, UFDouble> otTotalHoursMap = new HashMap<String, UFDouble>(); // 加班享有
					Map<String, UFDouble> otSpentHoursMap = new HashMap<String, UFDouble>();// 加班已休
					Map<String, UFDouble> otFrozenHoursMap = new HashMap<String, UFDouble>();// 加班冻结
					Map<String, UFBoolean> otClosedMap = new HashMap<String, UFBoolean>();// 是否结束
					List<String> otList = new ArrayList<String>();
					OTSChainNode curNode = OTSChainUtils.buildChainNodes(pk_psndoc, null, null, true, false, true,
							false, false); // 按人员加载加班转调休分段明细

					OTLeaveBalanceVO headVo = null;
					if (curNode == null) {
						headVo = OTLeaveBalanceUtils.createNewHeadVO(pk_org, pk_psndoc, totalAmount, spentAmount,
								remainAmount, frozenAmount, useableAmount);
						continue;
					}
					curNode = OTSChainUtils.getFirstNode(curNode); // 取第一个节点
					while (curNode != null) {
						if (curNode.getNodeData() != null) {
							UFLiteralDate expireDate = curNode.getNodeData().getExpirydate();
							String pk_overtimereg = curNode.getNodeData().getPk_overtimereg();
							if (expireDate != null) {
								if ((expireDate.isSameDate(psnBeginDate) || expireDate.after(psnBeginDate))
										&& (expireDate.isSameDate(psnendDate) || expireDate.before(psnendDate))) {
									totalAmount = totalAmount.add(OTLeaveBalanceUtils.getUFDouble(curNode.getNodeData()
											.getHours()));// 享有
									spentAmount = spentAmount.add(OTLeaveBalanceUtils.getUFDouble(curNode.getNodeData()
											.getConsumedhours()));// 已休
									remainAmount = remainAmount.add(OTLeaveBalanceUtils.getUFDouble(curNode
											.getNodeData().getRemainhours()));// 剩余
									frozenAmount = frozenAmount.add(OTLeaveBalanceUtils.getUFDouble(curNode
											.getNodeData().getFrozenhours()));// 冻结
									collectOvertimeData(otDateMap, otTotalHoursMap, otSpentHoursMap, otFrozenHoursMap,
											otClosedMap, otList, curNode, pk_overtimereg); // 统计加班数据
								}
							}
						}
						curNode = curNode.getNextNode();
					}
					useableAmount = remainAmount.sub(frozenAmount);

					if (!totalAmount.equals(UFDouble.ZERO_DBL) || !spentAmount.equals(UFDouble.ZERO_DBL)
							|| !remainAmount.equals(UFDouble.ZERO_DBL) || !frozenAmount.equals(UFDouble.ZERO_DBL)
							|| !useableAmount.equals(UFDouble.ZERO_DBL)) {
						// 创建表头
						headVo = OTLeaveBalanceUtils.createNewHeadVO(pk_org, pk_psndoc, totalAmount, spentAmount,
								remainAmount, frozenAmount, useableAmount);
						headVo.setQstartdate(psnBeginDate);
						headVo.setQenddate(psnendDate);
						headvos.add(headVo);
					}
				}
			}
		}

		return headvos.toArray(new OTLeaveBalanceVO[0]);
	}

	private void collectOvertimeData(Map<String, UFLiteralDate> otDateMap, Map<String, UFDouble> otTotalHoursMap,
			Map<String, UFDouble> otSpentHoursMap, Map<String, UFDouble> otFrozenHoursMap,
			Map<String, UFBoolean> otIsClosedMap, List<String> otList, OTSChainNode curNode, String pk_overtimereg) {
		if (!otList.contains(pk_overtimereg)) {
			otList.add(pk_overtimereg);
		}
		// 加班日期
		if (!otDateMap.containsKey(pk_overtimereg)) {
			otDateMap.put(pk_overtimereg, curNode.getNodeData().getRegdate());
		}

		// 加班享有
		if (!otTotalHoursMap.containsKey(pk_overtimereg)) {
			otTotalHoursMap.put(pk_overtimereg, OTLeaveBalanceUtils.getUFDouble(curNode.getNodeData().getHours()));
		} else {
			otTotalHoursMap.put(
					pk_overtimereg,
					OTLeaveBalanceUtils.getUFDouble(otTotalHoursMap.get(pk_overtimereg).add(
							OTLeaveBalanceUtils.getUFDouble(curNode.getNodeData().getHours()))));
		}

		// 加班已休
		if (!otSpentHoursMap.containsKey(pk_overtimereg)) {
			otSpentHoursMap.put(pk_overtimereg,
					OTLeaveBalanceUtils.getUFDouble(curNode.getNodeData().getConsumedhours()));
		} else {
			otSpentHoursMap.put(
					pk_overtimereg,
					OTLeaveBalanceUtils.getUFDouble(otSpentHoursMap.get(pk_overtimereg).add(
							OTLeaveBalanceUtils.getUFDouble(curNode.getNodeData().getConsumedhours()))));
		}

		// 加班冻结
		if (!otFrozenHoursMap.containsKey(pk_overtimereg)) {
			otFrozenHoursMap.put(pk_overtimereg,
					OTLeaveBalanceUtils.getUFDouble(curNode.getNodeData().getFrozenhours()));
		} else {
			otFrozenHoursMap.put(
					pk_overtimereg,
					OTLeaveBalanceUtils.getUFDouble(otFrozenHoursMap.get(pk_overtimereg).add(
							OTLeaveBalanceUtils.getUFDouble(curNode.getNodeData().getFrozenhours()))));
		}

		// 是否结束
		if (!otIsClosedMap.containsKey(pk_overtimereg)) {
			otIsClosedMap.put(pk_overtimereg,
					new UFBoolean(UFDouble.ZERO_DBL.equals(curNode.getNodeData().getRemainhours())));
		} else {
			otIsClosedMap.put(
					pk_overtimereg,
					new UFBoolean((otIsClosedMap.get(pk_overtimereg) == null ? false : otIsClosedMap
							.get(pk_overtimereg).booleanValue())
							&& UFDouble.ZERO_DBL.equals(curNode.getNodeData().getRemainhours())));
		}
	}

	@Override
	public OTBalanceDetailVO[] getOvertimeToRestByType(String pk_org, String pk_psndoc, UFLiteralDate beginDate,
			UFLiteralDate endDate, String pk_overtimetype) throws BusinessException {
		List<OTBalanceDetailVO> detailVOs = new ArrayList<OTBalanceDetailVO>();

		if (!StringUtils.isEmpty(pk_psndoc)) {
			Map<String, UFLiteralDate> otDateMap = new HashMap<String, UFLiteralDate>();// 加班日期
			Map<String, UFDouble> otTotalHoursMap = new HashMap<String, UFDouble>(); // 加班享有
			Map<String, UFDouble> otSpentHoursMap = new HashMap<String, UFDouble>();// 加班已休
			Map<String, UFDouble> otFrozenHoursMap = new HashMap<String, UFDouble>();// 加班冻结
			Map<String, UFBoolean> otClosedMap = new HashMap<String, UFBoolean>();// 是否结束
			List<String> otList = new ArrayList<String>();
			OTSChainNode curNode = OTSChainUtils
					.buildChainNodes(pk_psndoc, null, null, true, false, true, false, false); // 按人员加载加班转调休分段明细

			if (curNode != null) {
				curNode = OTSChainUtils.getFirstNode(curNode); // 取第一个节点
				while (curNode != null) {
					if (curNode.getNodeData() != null) {
						UFLiteralDate expireDate = curNode.getNodeData().getExpirydate();
						String pk_overtimereg = curNode.getNodeData().getPk_overtimereg();
						if (expireDate != null) {
							if ((expireDate.isSameDate(beginDate) || expireDate.after(beginDate))
									&& (expireDate.isSameDate(endDate) || expireDate.before(endDate))) {
								collectOvertimeData(otDateMap, otTotalHoursMap, otSpentHoursMap, otFrozenHoursMap,
										otClosedMap, otList, curNode, pk_overtimereg); // 统计加班数据
							}
						}
					}
					curNode = curNode.getNextNode();
				}

				// 创建表行
				for (String pk_overtimereg : otList) {
					OTBalanceDetailVO detailVo = new OTBalanceDetailVO();
					detailVo.setPk_otleavebalance(pk_psndoc);
					detailVo.setSourcetype(SoureBillTypeEnum.OVERTIMEREG.toIntValue());
					detailVo.setPk_sourcebill(pk_overtimereg);
					detailVo.setCalendar(otDateMap.get(pk_overtimereg));
					detailVo.setBillhours(otTotalHoursMap.get(pk_overtimereg));
					detailVo.setConsumedhours(otSpentHoursMap.get(pk_overtimereg));
					detailVo.setFrozenhours(otFrozenHoursMap.get(pk_overtimereg));
					detailVo.setCloseflag(otClosedMap.get(pk_overtimereg));
					detailVo.setPk_balancedetail(pk_overtimereg);
					detailVOs.add(detailVo);
				}
			}
		}
		return detailVOs.toArray(new OTBalanceDetailVO[0]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public OTBalanceLeaveVO[] getLeaveRegBySourceBill(int sourceType, String pk_sourceBill, String queryYear,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		Map<String, UFLiteralDate> leaveDateMap = new HashMap<String, UFLiteralDate>();
		Map<String, UFDouble> leaveHoursMap = new HashMap<String, UFDouble>();
		Map<String, UFDouble> consumedHoursMap = new HashMap<String, UFDouble>();
		if (sourceType == 1) {
			// 加班轉補休
			Collection<SegDetailVO> segDetailVOs = this.getBaseDao().retrieveByClause(SegDetailVO.class,
					"pk_overtimereg = '" + pk_sourceBill + "'");

			if (segDetailVOs != null && segDetailVOs.size() > 0) {
				for (SegDetailVO segDetailVO : segDetailVOs) {
					Collection<SegDetailConsumeVO> consumeVOs = this.getBaseDao().retrieveByClause(
							SegDetailConsumeVO.class, "pk_segdetail = '" + segDetailVO.getPk_segdetail() + "'");
					if (consumeVOs != null && consumeVOs.size() > 0) {
						for (SegDetailConsumeVO consumeVO : consumeVOs) {
							LeaveRegVO leaveRegVO = (LeaveRegVO) this.getBaseDao().retrieveByPK(LeaveRegVO.class,
									consumeVO.getPk_leavereg());
							if (leaveRegVO != null) {
								if (!leaveHoursMap.containsKey(leaveRegVO.getPk_leavereg())) {
									leaveDateMap.put(leaveRegVO.getPk_leavereg(), leaveRegVO.getLeavebegindate());
									leaveHoursMap.put(leaveRegVO.getPk_leavereg(), leaveRegVO.getLeavehour());
								}

								if (!consumedHoursMap.containsKey(leaveRegVO.getPk_leavereg())) {
									consumedHoursMap.put(leaveRegVO.getPk_leavereg(), UFDouble.ZERO_DBL);
								}

								consumedHoursMap.put(
										leaveRegVO.getPk_leavereg(),
										consumedHoursMap.get(leaveRegVO.getPk_leavereg()).add(
												consumeVO.getConsumedhours()));
							}
						}
					}
				}
			}
		} else if (sourceType == 2) {
			// 外加補休
			LeaveExtraRestVO leaveExtraVO = (LeaveExtraRestVO) this.getBaseDao().retrieveByPK(LeaveExtraRestVO.class,
					pk_sourceBill);
			String pk_exleavetype = SysInitQuery.getParaString(leaveExtraVO.getPk_org(), "TWHRT10");
			if (pk_exleavetype == null) {
				throw new BusinessException("請檢查參數:[TWHRT10]設置!");
			}
			LeaveRegVO[] leaveRegVOs = StringUtils.isEmpty(queryYear) ? OTLeaveBalanceUtils.getLeaveRegByPsnDate(
					leaveExtraVO.getPk_org(), leaveExtraVO.getPk_psndoc(), beginDate, endDate, pk_exleavetype)
					: OTLeaveBalanceUtils.getLeaveRegByPsnYear(leaveExtraVO.getPk_org(), leaveExtraVO.getPk_psndoc(),
							queryYear, pk_exleavetype);
			if (leaveRegVOs != null && leaveRegVOs.length > 0) {
				for (LeaveRegVO leaveRegVO : leaveRegVOs) {
					if (leaveRegVO != null) {
						leaveDateMap.put(leaveRegVO.getPk_leavereg(), leaveRegVO.getLeavebegindate());
						leaveHoursMap.put(leaveRegVO.getPk_leavereg(), leaveRegVO.getLeavehour());
						consumedHoursMap.put(leaveRegVO.getPk_leavereg(), leaveRegVO.getLeavehour());

					}

				}
			}
		}

		List<OTBalanceLeaveVO> ret = new ArrayList<OTBalanceLeaveVO>();
		if (leaveHoursMap.size() > 0 || consumedHoursMap.size() > 0) {
			for (String pk_leavereg : leaveHoursMap.keySet()) {
				OTBalanceLeaveVO item = new OTBalanceLeaveVO();
				item.setPk_leavereg(pk_leavereg);
				item.setLeavedate(leaveDateMap.get(pk_leavereg));
				item.setLeavehours(leaveHoursMap.get(pk_leavereg));
				item.setSpenthours(consumedHoursMap.get(pk_leavereg));
				ret.add(item);
			}
		}
		return ret.toArray(new OTBalanceLeaveVO[0]);
	}

	@Override
	public void settleByExpiryDate(String pk_org, String[] pk_psndocs, UFLiteralDate settleDate, Boolean isForce)
			throws BusinessException {
		String strCondition = " dr=0 and (isnull(issettled,'N')='N' or issettled='~') ";

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

			strCondition += " and expirydate <'" + settleDate.toString() + "' ";
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
				"update " + SegDetailVO.getDefaultTableName() + " set issettled='Y', settledate='"
						+ settleDate.toString() + "' where " + strCondition);
	}

	@Override
	public void unSettleByPsn(String pk_psndoc) throws BusinessException {
		if (StringUtils.isEmpty(pk_psndoc)) {
			throw new BusinessException("反結算錯誤：未指定進行反結算的人員。");
		}

		// 反結算日期
		String lastSettledDate = (String) this.getBaseDao().executeQuery(
				"select max(" + SegDetailVO.EXPIRYDATE + ") from " + SegDetailVO.getDefaultTableName() + " where "
						+ SegDetailVO.ISSETTLED + "='Y' and " + SegDetailVO.PK_PSNDOC + "='" + pk_psndoc + "'",
				new ColumnProcessor());
		if (StringUtils.isEmpty(lastSettledDate)) {
			throw new BusinessException("反結算錯誤：未找到指定人員的可反結算分段明細記錄。");
		}

		// 反結算
		this.getBaseDao().executeUpdate(
				"update " + SegDetailVO.getDefaultTableName() + " set " + SegDetailVO.ISSETTLED + "='N', "
						+ SegDetailVO.SETTLEDATE + "=null where  " + SegDetailVO.PK_PSNDOC + "='" + pk_psndoc
						+ "' and " + SegDetailVO.EXPIRYDATE + "= '" + lastSettledDate + "'");
	}

	@Override
	public OTLeaveBalanceVO[] getOvertimeToRestHoursByType(String pk_org, String[] pk_psndocs, String queryYear,
			String pk_overtimetype) throws BusinessException {
		List<OTLeaveBalanceVO> headvos = new ArrayList<OTLeaveBalanceVO>();
		Map<String, UFLiteralDate> psnWorkStartDate = OTLeaveBalanceUtils.getPsnWorkStartDateMap(pk_org, pk_psndocs,
				queryYear, pk_overtimetype);

		if (psnWorkStartDate != null && psnWorkStartDate.keySet().size() > 0) {
			for (String pk_psndoc : psnWorkStartDate.keySet()) {
				if (psnWorkStartDate.get(pk_psndoc) != null) {
					UFLiteralDate settleddate = null;
					UFLiteralDate beginDate = null;
					UFLiteralDate endDate = null;
					beginDate = OTLeaveBalanceUtils.getBeginDateByWorkAgeStartDate(queryYear, psnWorkStartDate,
							pk_psndoc);
					endDate = OTLeaveBalanceUtils.getEndDateByWorkAgeStartDate(queryYear, psnWorkStartDate, pk_psndoc);

					UFDouble totalAmount = UFDouble.ZERO_DBL; // 享有
					UFDouble spentAmount = UFDouble.ZERO_DBL;// 已休
					UFDouble remainAmount = UFDouble.ZERO_DBL;// 剩余
					UFDouble frozenAmount = UFDouble.ZERO_DBL;// 冻结
					UFDouble useableAmount = UFDouble.ZERO_DBL; // 可用
					Map<String, UFLiteralDate> otDateMap = new HashMap<String, UFLiteralDate>();// 加班日期
					Map<String, UFDouble> otTotalHoursMap = new HashMap<String, UFDouble>(); // 加班享有
					Map<String, UFDouble> otSpentHoursMap = new HashMap<String, UFDouble>();// 加班已休
					Map<String, UFDouble> otFrozenHoursMap = new HashMap<String, UFDouble>();// 加班冻结
					Map<String, UFBoolean> otClosedMap = new HashMap<String, UFBoolean>();// 是否结束
					List<String> otList = new ArrayList<String>();
					OTSChainNode curNode = OTSChainUtils.buildChainNodes(pk_psndoc, null, null, true, false, true,
							false, false); // 按人员加载加班转调休分段明细

					OTLeaveBalanceVO headVo = null;
					if (curNode == null) {
						headVo = OTLeaveBalanceUtils.createNewHeadVO(pk_org, pk_psndoc, totalAmount, spentAmount,
								remainAmount, frozenAmount, useableAmount);
						continue;
					}
					curNode = OTSChainUtils.getFirstNode(curNode); // 取第一个节点
					while (curNode != null) {
						if (curNode.getNodeData() != null) {
							UFLiteralDate expireDate = curNode.getNodeData().getExpirydate();
							String pk_overtimereg = curNode.getNodeData().getPk_overtimereg();
							if (expireDate != null) {
								if ((expireDate.isSameDate(beginDate) || expireDate.after(beginDate))
										&& (expireDate.isSameDate(endDate) || expireDate.before(endDate))) {
									totalAmount = totalAmount.add(OTLeaveBalanceUtils.getUFDouble(curNode.getNodeData()
											.getHours()));// 享有
									spentAmount = spentAmount.add(OTLeaveBalanceUtils.getUFDouble(curNode.getNodeData()
											.getConsumedhours()));// 已休
									remainAmount = remainAmount.add(OTLeaveBalanceUtils.getUFDouble(curNode
											.getNodeData().getRemainhours()));// 剩余
									frozenAmount = frozenAmount.add(OTLeaveBalanceUtils.getUFDouble(curNode
											.getNodeData().getFrozenhours()));// 冻结
									collectOvertimeData(otDateMap, otTotalHoursMap, otSpentHoursMap, otFrozenHoursMap,
											otClosedMap, otList, curNode, pk_overtimereg); // 统计加班数据

									if (curNode.getNodeData().getIssettled().booleanValue()) {
										settleddate = curNode.getNodeData().getSettledate();
									}
								}
							}
						}
						curNode = curNode.getNextNode();
					}
					useableAmount = remainAmount.sub(frozenAmount);

					if (!totalAmount.equals(UFDouble.ZERO_DBL) || !spentAmount.equals(UFDouble.ZERO_DBL)
							|| !remainAmount.equals(UFDouble.ZERO_DBL) || !frozenAmount.equals(UFDouble.ZERO_DBL)
							|| !useableAmount.equals(UFDouble.ZERO_DBL)) {
						// 创建表头
						headVo = OTLeaveBalanceUtils.createNewHeadVO(pk_org, pk_psndoc, totalAmount, spentAmount,
								remainAmount, frozenAmount, useableAmount);
						headVo.setQstartdate(beginDate);
						headVo.setQenddate(endDate);
						headVo.setSettleddate(settleddate);
						headvos.add(headVo);
					}
				}
			}
		}
		return headvos.toArray(new OTLeaveBalanceVO[0]);
	}

	public ISegdetailMaintain getSegMaintain() {
		if (segMaintain == null) {
			segMaintain = NCLocator.getInstance().lookup(ISegdetailMaintain.class);
		}
		return segMaintain;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void rebuildSegDetailByPsn(String pk_psndoc, String cyear) throws BusinessException {
		// 取人員重建未結算起點
		String strSQL = "select sg.pk_overtimereg from hrta_segdetail sg where sg.pk_psndoc='"
				+ pk_psndoc
				+ "' and sg.nodecode = (select min(nodecode) from hrta_segdetail where pk_psndoc=sg.pk_psndoc and issettled = 'N')";
		String pk_overtimereg = (String) this.getBaseDao().executeQuery(strSQL, new ColumnProcessor());
		Collection<OvertimeRegVO> otList = null;
		Collection<LeaveRegVO> lvList = null;
		if (StringUtils.isEmpty(pk_overtimereg)) {
			// 沒找到未結算起點，重建所有分段
			strSQL = "SELECT DISTINCT og.workagestartdate FROM tbm_psndoc psn "
					+ " INNER JOIN hi_psnorg og ON  psn.pk_psnorg = og.pk_psnorg " + " WHERE psn.enddate >= '" + cyear
					+ "' || RIGHT(og.workagestartdate, 6) AND psn.begindate < '"
					+ String.valueOf(Integer.valueOf(cyear) + 1) + "' || RIGHT(og.workagestartdate, 6) "
					+ " AND og.workagestartdate IS NOT NULL AND psn.pk_psndoc = '" + pk_psndoc + "' AND ( "
					+ " SELECT  COUNT(pk_segdetail)  FROM hrta_segdetail  WHERE  pk_psndoc = psn.pk_psndoc "
					+ " AND expirydate >= '" + cyear + "' || RIGHT(og.workagestartdate, 6) AND expirydate < '"
					+ String.valueOf(Integer.valueOf(cyear) + 1) + "' || RIGHT(og.workagestartdate, 6)) > 0";

			String psnWorkStartDate = (String) this.getBaseDao().executeQuery(strSQL, new ColumnProcessor());
			UFLiteralDate beginDate = OTLeaveBalanceUtils.getBeginDateByWorkAgeStartDate(cyear, new UFLiteralDate(
					psnWorkStartDate));
			UFLiteralDate endDate = OTLeaveBalanceUtils.getEndDateByWorkAgeStartDate(cyear, new UFLiteralDate(
					psnWorkStartDate));

			// 清理數據
			strSQL = "delete from hrta_segdetailconsume where pk_segdetail in (select pk_segdetail from hrta_segdetail where pk_psndoc = '"
					+ pk_psndoc
					+ "' and regdate between '"
					+ beginDate.toString()
					+ "' and '"
					+ endDate.toString()
					+ "')";
			this.getBaseDao().executeUpdate(strSQL);

			strSQL = "delete from hrta_segdetail where pk_psndoc = '" + pk_psndoc + "' and regdate between '"
					+ beginDate.toString() + "' and '" + endDate.toString() + "')";
			this.getBaseDao().executeUpdate(strSQL);
			//

			strSQL = "pk_psndoc = '" + pk_psndoc + "' and dr=0 and overtimebegindate between '" + beginDate.toString()
					+ "' and '" + endDate.getDateAfter(1).toString() + "'";
			otList = this.getBaseDao().retrieveByClause(OvertimeRegVO.class, strSQL);
			if (otList != null && otList.size() > 0) {
				OvertimeRegVO[] vos = otList.toArray(new OvertimeRegVO[0]);
				for (int i = 0; i < otList.size(); i++) {
					// 只看前後三天內的單據，以免耗時過長
					if (UFLiteralDate.getDaysBetween(vos[i].getOvertimebegindate(), beginDate) <= 3
							|| UFLiteralDate.getDaysBetween(vos[i].getOvertimebegindate(), endDate) <= 3) {
						UFLiteralDate realDate = getShiftRegDateByOvertime(vos[i]);
						if (realDate.before(beginDate) || realDate.after(endDate)) {
							otList.remove(vos[i]);
						}
					}
				}
			}

			// 取人員區間休假單
			strSQL = "pk_psndoc = '" + pk_psndoc + "' and dr=0 and leavebegindate  between '" + beginDate.toString()
					+ "' and '" + endDate.getDateAfter(1).toString() + "'";
			lvList = this.getBaseDao().retrieveByClause(LeaveRegVO.class, strSQL);
			lvList = this.getBaseDao().retrieveByClause(OvertimeRegVO.class, strSQL);
			if (lvList != null && lvList.size() > 0) {
				LeaveRegVO[] vos = lvList.toArray(new LeaveRegVO[0]);
				for (int i = 0; i < lvList.size(); i++) {
					// 只看前後三天內的單據，以免耗時過長
					if (UFLiteralDate.getDaysBetween(vos[i].getLeavebegindate(), beginDate) <= 3
							|| UFLiteralDate.getDaysBetween(vos[i].getLeavebegindate(), endDate) <= 3) {
						UFLiteralDate realDate = getShiftRegDateByLeave(vos[i]);
						if (realDate.before(beginDate) || realDate.after(endDate)) {
							otList.remove(vos[i]);
						}
					}
				}
			}

		} else {
			// 清理未結算數據
			strSQL = "delete from hrta_segdetailconsume where pk_segdetail in (select pk_segdetail from hrta_segdetail where pk_psndoc = '"
					+ pk_psndoc + "' and issettled = 'N');";
			this.getBaseDao().executeUpdate(strSQL);

			strSQL = "delete from hrta_segdetail where pk_psndoc = '" + pk_psndoc + "' and issettled = 'N'";
			this.getBaseDao().executeUpdate(strSQL);

			// 取人員未結算區間加班單
			strSQL = " overtimebegintime >= (select overtimebegintime from tbm_overtimereg ot where pk_overtimereg = '"
					+ pk_overtimereg + "')  and pk_psndoc = '" + pk_psndoc + "' ";
			otList = this.getBaseDao().retrieveByClause(OvertimeRegVO.class, strSQL);

			// 取人員未結算區間休假單
			strSQL = " pk_leavereg not in (select pk_leavereg from hrta_segdetailconsume sc inner join hrta_segdetail sd on sc.pk_segdetail = sd.pk_segdetail where sd.pk_psndoc =  '"
					+ pk_psndoc
					+ "')  and leavebegintime > (select overtimebegintime from tbm_overtimereg where pk_overtimereg = '"
					+ pk_overtimereg + "') " + " and pk_psndoc = '" + pk_psndoc + "'";
			lvList = this.getBaseDao().retrieveByClause(LeaveRegVO.class, strSQL);
		}

		// 重建加班數據
		if (otList != null && otList.size() > 0) {
			List<OvertimeRegVO> list = new ArrayList<OvertimeRegVO>();
			list.addAll(otList);
			Collections.sort(list, new Comparator<OvertimeRegVO>() {

				@Override
				public int compare(OvertimeRegVO ot1, OvertimeRegVO ot2) {
					if (ot1.getOvertimebegintime().before(ot2.getOvertimebegintime())) {
						return -1;
					} else if (ot1.getOvertimebegintime().after(ot2.getOvertimebegintime())) {
						return 1;
					} else {
						return 0;
					}
				}
			});

			for (OvertimeRegVO vo : list.toArray(new OvertimeRegVO[0])) {
				this.regOvertimeSegDetail(new OvertimeRegVO[] { vo });
			}
		}

		// 重建休假數據
		if (lvList != null && lvList.size() > 0) {
			List<LeaveRegVO> list = new ArrayList<LeaveRegVO>();
			list.addAll(lvList);
			Collections.sort(list, new Comparator<LeaveRegVO>() {

				@Override
				public int compare(LeaveRegVO ot1, LeaveRegVO ot2) {
					if (ot1.getLeavebegintime().before(ot2.getLeavebegintime())) {
						return -1;
					} else if (ot1.getLeavebegintime().after(ot2.getLeavebegintime())) {
						return 1;
					} else {
						return 0;
					}
				}
			});

			for (LeaveRegVO vo : list.toArray(new LeaveRegVO[0])) {
				this.regOvertimeSegDetailConsume(new LeaveRegVO[] { vo });
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String, UFDouble[]> calculateOvertimeFeeByDate_MT(String pk_org, String[] pk_psndocs,
			UFLiteralDate startDate, UFLiteralDate endDate, UFDouble curNodeHours, OTSChainNode unSavedNodes,
			String pk_item_group, boolean isLeave) throws BusinessException {
		// 返回值
		Map<String, UFDouble[]> mt_OTFT_ReturnValues = new HashMap<String, UFDouble[]>();

		if (pk_psndocs != null && pk_psndocs.length > 0) {
			// 由调用线程处处理该任务-移交给主线程执行
			RejectedExecutionHandler CallerRunsPolicyHandler = new ThreadPoolExecutor.CallerRunsPolicy();

			// 線程池
			ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 20, 20, TimeUnit.MINUTES,
					new SynchronousQueue(), new OvertimeFeeCalculatorThreadFactory(), CallerRunsPolicyHandler);

			// 返回值池
			List<Future<Map<String, UFDouble[]>>> futureDatas = new ArrayList<Future<Map<String, UFDouble[]>>>();

			int count = 1;
			for (String pk_psndoc : pk_psndocs) {
				RunnableOvertimeFeeCalculator calculator = new RunnableOvertimeFeeCalculator();
				calculator.setPk_org(pk_org);
				calculator.setPk_psndoc(pk_psndoc);
				calculator.setStartDate(startDate);
				calculator.setEndDate(endDate);
				calculator.setCurNodeHours(curNodeHours);
				calculator.setUnSavedNodes(unSavedNodes);
				calculator.setPk_item_group(pk_item_group);
				calculator.setLeave(isLeave);
				calculator.setBaseDao(this.getBaseDao());
				calculator.setProxyAgent(InvocationInfoProxy.getInstance());
				calculator.setCurrentcount(count++);
				calculator.setTotalcount(pk_psndocs.length);

				Logger.error("---WNC-MULTIPUL-THREADS-OVERTIME-FEE-THREAD-POOL-SIZE: ["
						+ String.valueOf(threadPoolExecutor.getPoolSize()) + "]-SERIELS["
						+ calculator.getCurrentcount() + "/" + pk_psndocs.length + "]--");

				// 提交服務，註冊返回值池
				futureDatas.add(threadPoolExecutor.submit(calculator));
			}
			try {

				for (Future<Map<String, UFDouble[]>> futureData : futureDatas) {
					mt_OTFT_ReturnValues.putAll(futureData.get());
				}
			} catch (InterruptedException | ExecutionException e) {
				throw new BusinessException(e.getMessage());
			} finally {
				threadPoolExecutor.shutdown();
			}
		}

		return mt_OTFT_ReturnValues;
	}

	static class OvertimeFeeCalculatorThreadFactory implements ThreadFactory {
		private AtomicInteger threadNum = new AtomicInteger(0);

		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setName("OTFC-THREAD-NO: [" + String.valueOf(threadNum.incrementAndGet()) + "]");
			return t;
		}
	}

	class RunnableOvertimeFeeCalculator implements Callable<Map<String, UFDouble[]>> {

		private String pk_org;
		private String pk_psndoc;
		private UFLiteralDate startDate;
		private UFLiteralDate endDate;
		private UFDouble curNodeHours;
		private OTSChainNode unSavedNodes;
		private String pk_item_group;
		private boolean isLeave;
		private BaseDAO baseDao;
		private int currentcount;
		private int totalcount;

		@Override
		public Map<String, UFDouble[]> call() throws BusinessException {
			Map<String, UFDouble[]> ret = new HashMap<String, UFDouble[]>();
			try {
				Logger.error("---WNC-MULTIPUL-THREADS-OVERTIME-FEE-THREAD-[" + Thread.currentThread().getName()
						+ "]-COUNT [" + String.valueOf(currentcount) + " / " + String.valueOf(totalcount) + "]-START--");
				SegDetailServiceImpl newImpl = new SegDetailServiceImpl();
				newImpl.setBaseDao(this.getBaseDao());
				ret = newImpl.calculateOvertimeFeeByDate(this.getPk_org(), new String[] { this.getPk_psndoc() },
						this.getStartDate(), this.getEndDate(), this.getCurNodeHours(), this.getUnSavedNodes(),
						this.getPk_item_group(), this.isLeave());
			} catch (BusinessException e) {
				Logger.error("---WNC-MULTIPUL-THREADS-OVERTIME-FEE-THREAD-[" + Thread.currentThread().getName()
						+ "]-ERROR---");
				Logger.error(e.toString());
			} finally {
				Logger.error("---WNC-MULTIPUL-THREADS-OVERTIME-FEE-THREAD-[" + Thread.currentThread().getName()
						+ "]-COUNT [" + String.valueOf(currentcount) + " / " + String.valueOf(totalcount) + "]-END--");
			}

			if (ret == null || ret.size() == 0) {
				ret.put(this.getPk_psndoc(), new UFDouble[] { UFDouble.ZERO_DBL, UFDouble.ZERO_DBL });
			}

			return ret;
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

		public UFLiteralDate getStartDate() {
			return startDate;
		}

		public void setStartDate(UFLiteralDate startDate) {
			this.startDate = startDate;
		}

		public UFLiteralDate getEndDate() {
			return endDate;
		}

		public void setEndDate(UFLiteralDate endDate) {
			this.endDate = endDate;
		}

		public UFDouble getCurNodeHours() {
			return curNodeHours;
		}

		public void setCurNodeHours(UFDouble curNodeHours) {
			this.curNodeHours = curNodeHours;
		}

		public OTSChainNode getUnSavedNodes() {
			return unSavedNodes;
		}

		public void setUnSavedNodes(OTSChainNode unSavedNodes) {
			this.unSavedNodes = unSavedNodes;
		}

		public String getPk_item_group() {
			return pk_item_group;
		}

		public void setPk_item_group(String pk_item_group) {
			this.pk_item_group = pk_item_group;
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
