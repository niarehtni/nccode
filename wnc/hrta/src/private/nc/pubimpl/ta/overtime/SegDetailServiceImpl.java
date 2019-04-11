package nc.pubimpl.ta.overtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uap.oid.OidGenerator;
import nc.hr.utils.InSQLCreator;
import nc.impl.pub.OTLeaveBalanceUtils;
import nc.itf.hrwa.IWadaysalaryQueryService;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.overtime.ISegdetailMaintain;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocVO;
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

import org.apache.commons.lang.StringUtils;

public class SegDetailServiceImpl implements ISegDetailService {
	private Map<String, OTSChainNode> allNode = null;
	private BaseDAO baseDao = null;
	private IPeriodQueryService periodService = null;
	private ISegdetailMaintain segMaintain = null;

	@Override
	public void regOvertimeSegDetail(OvertimeRegVO[] overtimeRegVOs) throws BusinessException {
		if (overtimeRegVOs != null && overtimeRegVOs.length > 0) {
			this.getAllNode().clear();

			for (OvertimeRegVO otRegVo : overtimeRegVOs) {
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
			List<AggSegDetailVO> aggvos = new ArrayList<AggSegDetailVO>();

			for (OvertimeRegVO vo : overtimeRegVOs) {
				UFBoolean isEnabled = new UFBoolean(SysInitQuery.getParaString(vo.getPk_org(), "TBMOTSEG"));
				if (isEnabled == null || !isEnabled.booleanValue()) {
					continue;
				}

				OTSChainNode curNode = OTSChainUtils.buildChainNodes(vo.getPk_psndoc(), null, vo.getPk_overtimereg(),
						false, false, false, false, false);

				if (curNode == null) {
					throw new BusinessException("未找到加班登記單對應的未結算分段明細。");
				}

				boolean canDel = true;
				while (curNode != null) {
					if (curNode.getNodeData().getConsumedhours().doubleValue() > 0
							|| containsChild(curNode.getNodeData().getPk_segdetail())) {
						canDel = false;
						break;
					}

					AggSegDetailVO aggvo = new AggSegDetailVO();
					aggvo.setParent(curNode.getNodeData());
					aggvos.add(aggvo);

					curNode = curNode.getNextNode();
				}

				if (!canDel) {
					throw new BusinessException("加班登記單對應的分段明細已被消耗");
				}

			}

			if (aggvos.size() > 0) {
				getSegMaintain().delete(aggvos.toArray(new AggSegDetailVO[0]));
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
		UFLiteralDate realDate = getShiftRegDate(otRegVO); // 獲取實際加班日期（刷卡開始時間段所屬工作日）
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
							null);
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
		segvo.setRemainamount(segvo.getRemainamounttaxfree().add(segvo.getRemainamounttaxable())); // 剩餘金額=剩餘金額（免稅）+剩餘金額（應稅）
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
	private UFLiteralDate getShiftRegDate(OvertimeRegVO vo) throws BusinessException {
		UFLiteralDate rtnDate = vo.getBegindate();
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

						if (vo.getOvertimebegintime().before(endDT) && vo.getOvertimebegintime().after(startDT)) {
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
			hourlypay = getPsnHourPay(detailVO.getPk_psndoc(), detailVO.getRegdate(), daySalType, pk_item_group);
			detailVO.setHourlypay(hourlypay.setScale(1, UFDouble.ROUND_HALF_UP));
		}
		UFDouble amount = hourlypay.setScale(1, UFDouble.ROUND_HALF_UP).multiply(otRate).multiply(hours)
				.setScale(1, UFDouble.ROUND_HALF_UP);
		return amount;
	}

	private UFDouble getPsnHourPay(String pk_psndoc, UFLiteralDate overtimebegindate, int daySalType,
			String pk_item_group) throws BusinessException {
		IWadaysalaryQueryService dayPaySvc = NCLocator.getInstance().lookup(IWadaysalaryQueryService.class);

		Map<String, HashMap<UFLiteralDate, UFDouble>> dayPayMap = dayPaySvc.getTotalTbmDaySalaryMap(
				new String[] { pk_psndoc }, overtimebegindate, overtimebegindate, daySalType, pk_item_group);
		if (dayPayMap == null || dayPayMap.size() == 0 || dayPayMap.get(pk_psndoc) == null
				|| dayPayMap.get(pk_psndoc).get(overtimebegindate) == null) {
			// throw new BusinessException("人員時薪取值錯誤：人員時薪為空");
			return UFDouble.ZERO_DBL; // 未取到日薪的，暫時返回0
		}
		return dayPayMap.get(pk_psndoc).get(overtimebegindate);
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
	public void regOvertimeSegDetailConsume(LeaveRegVO[] leaveRegVOs) throws BusinessException {
		if (leaveRegVOs != null && leaveRegVOs.length > 0) {

			for (LeaveRegVO vo : leaveRegVOs) {
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
			int counter = 0;
			for (String pk_psndoc : pk_psndocs) {
				long start = System.currentTimeMillis();
				counter++;
				PeriodVO[] threePeriods = null;
				PeriodVO periodCurrent = getPeriodService().queryByDate(pk_org, startDate);
				if (periodCurrent == null) {
					throw new BusinessException("取當前期間錯誤");
				}

				// 取該員工結算週期
				OvertimeLimitScopeEnum curStatScope = getPsnStatScope(pk_psndoc, startDate, endDate);

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

				String strSQL = "SELECT  regdate, sum(remainhourstaxfree * taxfreerate)  dayhourstaxfree,"
						+ " sum(remainhourstaxable * taxablerate + extrahourstaxable * extrataxablerate)  dayhourstaxable  FROM  hrta_segdetail"
						+ " WHERE  ((iscompensation='N' AND regdate BETWEEN '" + sumStartDate + "' AND '" + sumEndDate
						+ "')  OR  ( iscompensation='Y'" + (isLeave ? "" : "AND issettled = 'Y' ")
						+ " AND expirydate BETWEEN '" + sumStartDate + "' AND '" + sumEndDate + "')) AND pk_psndoc = '"
						+ pk_psndoc + "'" + " group by pk_psndoc, regdate order by pk_psndoc, regdate";
				List<Map<String, Object>> dayHoursMapList = (List<Map<String, Object>>) this.getBaseDao().executeQuery(
						strSQL, new MapListProcessor());

				UFDouble amountTaxfree = UFDouble.ZERO_DBL;
				UFDouble amountTaxable = UFDouble.ZERO_DBL;
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
							hourPay = this
									.getPsnHourPay(pk_psndoc, regDate, DaySalaryEnum.TBMHOURSALARY, pk_item_group);
						}
						UFDouble otAmountTaxfree = hourPay.setScale(1, UFDouble.ROUND_HALF_UP)
								.multiply(new UFDouble(String.valueOf(dayHoursMap.get("dayhourstaxfree"))))
								.setScale(1, UFDouble.ROUND_HALF_UP);
						UFDouble otAmountTaxable = hourPay.setScale(1, UFDouble.ROUND_HALF_UP)
								.multiply(new UFDouble(String.valueOf(dayHoursMap.get("dayhourstaxable"))))
								.setScale(1, UFDouble.ROUND_HALF_UP);
						amountTaxfree = otAmountTaxfree.add(amountTaxfree);
						amountTaxable = otAmountTaxable.add(amountTaxable);
					}
				}

				ret.put(pk_psndoc,
						new UFDouble[] { amountTaxfree.setScale(0, UFDouble.ROUND_HALF_UP),
								amountTaxable.setScale(0, UFDouble.ROUND_HALF_UP) });

				long end = System.currentTimeMillis();
				Logger.error(" --- OvertimeFee PSN (" + counter + "/" + pk_psndocs.length + "):" + pk_psndoc
						+ " --- CalculateDuration:" + String.valueOf(end - start));
			}

			// 清空缓存
			// ssx added on 2019-04-10
			psndocMap = new HashMap<String, List<TBMPsndocVO>>();
		}
		return ret;
	}

	@Override
	public Map<String, UFDouble[]> calculateTaxableByDate(String pk_org, String[] pk_psndocs, UFLiteralDate startDate,
			UFLiteralDate endDate, UFDouble curNodeHours, OTSChainNode unSavedNodes, String pk_item_group)
			throws BusinessException {
		Map<String, UFDouble[]> ret = new HashMap<String, UFDouble[]>();
		if (pk_psndocs != null && pk_psndocs.length > 0) {

			initOvertimeLimitScopeMap(pk_org, pk_psndocs, endDate); // MOD
																	// 薪資計算效率優化
																	// by ssx
																	// added on
																	// 2019-02-01

			Map<String, OTSChainNode> psnNodeMap = OTSChainUtils.buildChainPsnNodeMap(pk_psndocs, endDate, null, false,
					true, true, true, false);

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
					while (curNode != null) {
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
												totalTaxFreeAmount = totalTaxFreeAmount.add(getOTAmount(curNode
														.getNodeData().getTaxfreerate(), curNode.getNodeData()
														.getHourlypay(), curNode.getNodeData().getRemainhourstaxfree(),
														curNode.getNodeData(), DaySalaryEnum.TBMHOURSALARY,
														pk_item_group));
											}
										} else {
											// 在免稅時數範圍內的，纍計到免稅加班時數
											totalTaxFreeHours = totalTaxFreeHours.add(curNode.getNodeData()
													.getHourstaxfree());
											// 在免稅時數範圍內的，纍計到免稅加班費
											totalTaxFreeAmount = totalTaxFreeAmount.add(getOTAmount(curNode
													.getNodeData().getTaxfreerate(), curNode.getNodeData()
													.getHourlypay(), curNode.getNodeData().getRemainhourstaxfree(),
													curNode.getNodeData(), DaySalaryEnum.TBMHOURSALARY, pk_item_group));
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
												totalTaxableAmount = totalTaxableAmount.add(getOTAmount(curNode
														.getNodeData().getTaxablerate(), curNode.getNodeData()
														.getHourlypay(), curNode.getNodeData().getRemainhourstaxable(),
														curNode.getNodeData(), DaySalaryEnum.TBMHOURSALARY,
														pk_item_group));
												totalTaxFreeAmount = totalTaxFreeAmount.add(getOTAmount(curNode
														.getNodeData().getTaxfreerate(), curNode.getNodeData()
														.getHourlypay(), curNode.getNodeData().getRemainhourstaxfree(),
														curNode.getNodeData(), DaySalaryEnum.TBMHOURSALARY,
														pk_item_group));
											}
										} else {
											totalTaxableAmount = totalTaxableAmount.add(getOTAmount(curNode
													.getNodeData().getTaxablerate(), curNode.getNodeData()
													.getHourlypay(), curNode.getNodeData().getRemainhourstaxable(),
													curNode.getNodeData(), DaySalaryEnum.TBMHOURSALARY, pk_item_group));
											totalTaxFreeAmount = totalTaxFreeAmount.add(getOTAmount(curNode
													.getNodeData().getTaxfreerate(), curNode.getNodeData()
													.getHourlypay(), curNode.getNodeData().getRemainhourstaxfree(),
													curNode.getNodeData(), DaySalaryEnum.TBMHOURSALARY, pk_item_group));
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
											totalTaxableAmount = totalTaxableAmount.add(getOTAmount(curNode
													.getNodeData().getTaxablerate(), curNode.getNodeData()
													.getHourlypay(), curNode.getNodeData().getRemainhourstaxable(),
													curNode.getNodeData(), DaySalaryEnum.TBMHOURSALARY, pk_item_group));
										}
									} else {
										totalTaxableAmount = totalTaxableAmount.add(getOTAmount(curNode.getNodeData()
												.getTaxablerate(), curNode.getNodeData().getHourlypay(), curNode
												.getNodeData().getRemainhourstaxable(), curNode.getNodeData(),
												DaySalaryEnum.TBMHOURSALARY, pk_item_group));
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
										totalTaxableAmount = totalTaxableAmount.add(getOTAmount(curNode.getNodeData()
												.getTaxablerate(), curNode.getNodeData().getHourlypay(), curNode
												.getNodeData().getRemainhourstaxable(), curNode.getNodeData(),
												DaySalaryEnum.TBMHOURSALARY, pk_item_group));
										totalTaxFreeAmount = totalTaxFreeAmount.add(getOTAmount(curNode.getNodeData()
												.getTaxfreerate(), curNode.getNodeData().getHourlypay(), curNode
												.getNodeData().getRemainhourstaxfree(), curNode.getNodeData(),
												DaySalaryEnum.TBMHOURSALARY, pk_item_group));
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

					if (curNodeHours != null) {
						retValues[0] = totalTaxFreeHours;
						retValues[1] = totalTaxableHours;
					} else {
						retValues[0] = totalTaxFreeAmount.setScale(0, UFDouble.ROUND_HALF_UP);
						retValues[1] = totalTaxableAmount.setScale(0, UFDouble.ROUND_HALF_UP);
					}
				} else {
					if (curNodeHours != null) {
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
						retValues[0] = totalTaxFreeHours;
						retValues[1] = totalTaxableHours;
					}
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
						throw new BusinessException("無法找到員工 [" + psn.getCode() + "] 的加班控管");
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
		throw new BusinessException("無法找到員工 [" + psn.getCode() + "] 的加班上限統計範圍");
	}

	@SuppressWarnings("unchecked")
	private void initOvertimeLimitScopeMap(String pk_org, String[] pk_psndocs, UFLiteralDate endDate)
			throws BusinessException {
		String cond = " pk_org='" + pk_org + "' and pk_psndoc in (" + new InSQLCreator().getInSQL(pk_psndocs) + ")";

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
				period.getEnddate(), null, null, pk_item_group);

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
				period.getEnddate(), null, null, pk_item_group);

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
				OTSChainNode curNode = OTSChainUtils.buildChainNodes(pk_psndoc, null, null, true, false, true, false,
						false); // 按人员加载加班转调休分段明细

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
								remainAmount = remainAmount.add(OTLeaveBalanceUtils.getUFDouble(curNode.getNodeData()
										.getRemainhours()));// 剩余
								frozenAmount = frozenAmount.add(OTLeaveBalanceUtils.getUFDouble(curNode.getNodeData()
										.getFrozenhours()));// 冻结
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
												segDetailVO.getConsumedhours()));
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
			strCondition += " and pk_psndoc in (" + insql.getInSQL(pk_psndocs) + ")";
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
}
