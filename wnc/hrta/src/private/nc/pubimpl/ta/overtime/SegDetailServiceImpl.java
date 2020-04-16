package nc.pubimpl.ta.overtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import nc.bs.bd.pub.distribution.util.BDDistTokenUtil;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfo;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uap.oid.OidGenerator;
import nc.hr.utils.InSQLCreator;
import nc.impl.pub.OTLeaveBalanceUtils;
import nc.impl.ta.overtime.CheckDateScope;
import nc.itf.hrwa.IWadaysalaryQueryService;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.algorithm.BillProcessHelper;
import nc.itf.ta.overtime.ISegdetailMaintain;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.ta.overtime.ICheckDateScope;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hrta.tbmpsndoc.OvertimecontrolEunm;
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

				// ssx added on 2019-08-28
				// 傳入負數單據時刪除對應正時數的加班單分段
				if (unRegByNagetiveOvertime(otRegVo)) {
					continue;
				}
				// end

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

	@SuppressWarnings("rawtypes")
	private boolean unRegByNagetiveOvertime(OvertimeRegVO otRegVo) throws BusinessException {
		if (otRegVo != null && otRegVo.getOvertimehour().intValue() < 0) {
			Map oldRegs = (Map) this.getBaseDao().executeQuery(
					"select sum(overtimehour) othour, max(pk_overtimereg) maxpk from tbm_overtimereg where pk_psndoc = '"
							+ otRegVo.getPk_psndoc() + "' and overtimebegintime = '"
							+ otRegVo.getOvertimebegintime().toString() + "' and overtimeendtime = '"
							+ otRegVo.getOvertimeendtime() + "' and pk_overtimetype='" + otRegVo.getPk_overtimetype()
							+ "' and pk_overtimereg<>'" + otRegVo.getPk_overtimereg() + "' and approve_time < '"
							+ otRegVo.getApprove_time().toString() + "'"
							+ " group by pk_psndoc, overtimebegintime, overtimeendtime ", new MapProcessor());

			// 無返回值或返回時數與當前時數不同的，均認為與源單不匹配
			if (oldRegs == null
					|| !oldRegs.containsKey("othour")
					|| !otRegVo.getOvertimehour().multiply(-1)
							.equals(new UFDouble(String.valueOf(oldRegs.get("othour"))))) {
				throw new BusinessException("未找到對應的加班登記單。");
			} else {
				OvertimeRegVO otDel = (OvertimeRegVO) this.getBaseDao().retrieveByPK(OvertimeRegVO.class,
						(String) oldRegs.get("maxpk"));
				if (otDel.getOvertimehour().equals(otRegVo.getOvertimehour().multiply(-1))) {
					this.deleteOvertimeSegDetail(new OvertimeRegVO[] { otDel });
				}
			}
			return true;
		}
		return false;
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

				OTSChainNode curNode = OTSChainUtils.buildChainNodes(vo.getPk_psndoc(), null, null, false, false,
						false, false, false);

				if (curNode == null) {
					throw new BusinessException("未找到加班登記單對應的未結算分段明細。");
				}

				boolean canDel = true;
				OTSChainNode nodeToSave = null;
				List<String> needReregOTs = new ArrayList<String>();
				UFLiteralDate removedDate = null;
				while (curNode != null) {
					if (!vo.getPk_overtimereg().equals(curNode.getNodeData().getPk_overtimereg())) {
						nodeToSave = curNode;

						// ssx modified on 2019-12-19
						// for 刪除某節點後，後續如果有同一天的分段，應全部重建，否則分段歸屬及費率會有錯
						if (removedDate != null) {
							if (removedDate.isSameDate(curNode.getNodeData().getRegdate())) {
								if (!needReregOTs.contains(curNode.getNodeData().getPk_overtimereg())) {
									needReregOTs.add(curNode.getNodeData().getPk_overtimereg());
								}
							}
						}
						// end

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

					// ssx modified on 2019-12-19
					// for 刪除某節點後，後續如果有同一天的分段，應全部重建，否則分段歸屬及費率會有錯
					removedDate = delete.getNodeData().getRegdate();
					// end

					// ssx modified on 2019-08-08
					// for 刪除當前節點並防止斷鏈
					OTSChainUtils.removeCurrentNode(delete, true);
					// end
				}

				if (nodeToSave != null) {
					OTSChainUtils.saveAll(nodeToSave);
				}

				if (!canDel) {
					throw new BusinessException("加班登記單對應的分段明細已被消耗");
				}

				// ssx modified on 2019-12-19
				// for 刪除某節點後，後續如果有同一天的分段，應全部重建，否則分段歸屬及費率會有錯
				if (needReregOTs.size() > 0) {
					for (String pk_overtimereg : needReregOTs) {
						OvertimeRegVO needToDelOT = (OvertimeRegVO) this.getBaseDao().retrieveByPK(OvertimeRegVO.class,
								pk_overtimereg);
						deleteOvertimeSegDetail(new OvertimeRegVO[] { needToDelOT });
						regOvertimeSegDetail(new OvertimeRegVO[] { needToDelOT });
					}
				}
				// end
			}
		}
	}

	@SuppressWarnings("rawtypes")
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
		UFLiteralDate realDate = BillProcessHelper.getShiftRegDateByOvertime(otRegVO); // 獲取實際加班日期（刷卡開始時間段所屬工作日）
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
							new String[] { otRegVO.getPk_psndoc() }, realDate, null, curSegTotalHours, parentNode,
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
			UFDouble cachedHours = UFDouble.ZERO_DBL;
			while (curNode != null) {
				if (curNode.getNodeData() != null) {
					// 當前節點與要註冊節點為同一天
					if (curNode.getNodeData().getRegdate().equals(realDate)) {
						if (curNode.getNextNode() != null
								&& curNode.getNextNode().getNodeData().getRegdate().isSameDate(realDate)) {
							// 下一節點仍為同一天
							if (curNode.getNodeData().getPk_segruleterm()
									.equals(curNode.getNextNode().getNodeData().getPk_segruleterm())) {
								// 下一節點分段與當前節點為同一分段時才累加緩存段起點
								lastHours = curNode.getNodeData().getHours();
								cachedHours = cachedHours.add(lastHours);
							} else {
								// 同一天不同分段意味著還有後續分段，從後續分段開始累加，當然緩存清零
								cachedHours = UFDouble.ZERO_DBL;
							}
						} else if (curNode.getNextNode() == null
								|| !curNode.getNextNode().getNodeData().getRegdate().isSameDate(realDate)) {
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
										retTerm.setStartpoint(retTerm.getStartpoint().add(lastHours.add(cachedHours)));
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
			String pk_psnorg = otRegVO.getPk_psnorg();
			// 按年资起算日（比对加班日期）
			maxLeaveDate = getMaxLeaveDateByWorkAgeDay(otRealDate, pk_psnorg);
		} else {
			throw new BusinessException("加班可休日期计算错误，请检查组织参数（TWHRT09）的设定。");
		}
		return maxLeaveDate;
	}

	public UFLiteralDate getMaxLeaveDateByWorkAgeDay(UFLiteralDate realDate, String pk_psnorg) throws DAOException,
			BusinessException {
		PsnOrgVO psnOrgVO = (PsnOrgVO) this.getBaseDao().retrieveByPK(PsnOrgVO.class, pk_psnorg);
		UFLiteralDate workAgeStartDate = (UFLiteralDate) psnOrgVO.getAttributeValue("workagestartdate"); // 年资起算日
		UFLiteralDate maxLeaveDate = null;
		if (workAgeStartDate != null) {
			if (workAgeStartDate.toString().substring(4).equals("-02-29")) {
				maxLeaveDate = new UFLiteralDate(String.valueOf(realDate.getYear()) + "-03-01").getDateBefore(1);
				if (maxLeaveDate.before(realDate)) {
					maxLeaveDate = new UFLiteralDate(String.valueOf(realDate.getYear() + 1) + "-03-01")
							.getDateBefore(1);
				}
			} else {
				maxLeaveDate = new UFLiteralDate(String.valueOf(realDate.getYear())
						+ workAgeStartDate.toString().substring(4)).getDateBefore(1);
				if (maxLeaveDate.before(realDate)) {
					maxLeaveDate = new UFLiteralDate(String.valueOf(realDate.getYear() + 1)
							+ workAgeStartDate.toString().substring(4)).getDateBefore(1);
				}
			}
		} else {
			throw new BusinessException("加班可休日期按年资起算日计算错误，请检查员工组织关系年资起算日设定。");
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
					OTSChainNode psnNode = OTSChainUtils.buildChainNodes(vo.getPk_psndoc(), null, null, false, false,
							false, false, false);
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

	@SuppressWarnings("unchecked")
	private void consumeSegDetailHours(SegDetailVO[] segDetailVOs, LeaveRegVO vo) throws BusinessException {
		List<AggSegDetailVO> aggvos = new ArrayList<AggSegDetailVO>();
		UFDouble unConsumedLeaveHours = vo.getLeavehour();
		UFLiteralDate realLeaveDate = BillProcessHelper.getShiftRegDateByLeave(vo);
		UFLiteralDate maxLeaveDate = this.getMaxLeaveDateByWorkAgeDay(realLeaveDate, vo.getPk_psnorg());
		for (SegDetailVO segDetail : segDetailVOs) {
			// ssx added on 2019-08-29
			// 為了避免髒寫，手工判斷是否消耗完畢，消耗完畢的跳過
			if (segDetail.getRemainhours().doubleValue() == 0) {
				continue;
			}

			// 為了避免髒寫，手工判斷是否轉調休，非轉調休的跳過
			if (!segDetail.getIscompensation().booleanValue()) {
				continue;
			}

			// 為了避免髒寫，手工判斷是否結算，已結算的跳過
			if (segDetail.getIssettled().booleanValue()) {
				continue;
			}

			// 為了避免髒寫，手工判斷是否作廢，已作廢的跳過
			if (segDetail.getIscanceled().booleanValue()) {
				continue;
			}
			// end

			// ssx added on 2019-07-08
			// 已超過最長可休日期的加班分段不能參與消耗
			if (!maxLeaveDate.isSameDate(segDetail.getExpirydate())
					|| vo.getLeavebegindate().after(segDetail.getExpirydate())) {
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
			} else {
				break;
			}
		}

		if (unConsumedLeaveHours.doubleValue() > 0) {
			throw new BusinessException("加班轉補休分段時數已不足，剩餘 [" + String.valueOf(unConsumedLeaveHours.doubleValue())
					+ "] 小時無法消耗。");
		}

		if (aggvos.size() > 0) {
			getSegMaintain().update(aggvos.toArray(new AggSegDetailVO[0]));
		}
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

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, UFDouble[]> calculateOvertimeFeeByDate(String pk_org, String[] pk_psndocs,
			UFLiteralDate startDate, UFLiteralDate endDate, UFDouble curNodeHours, OTSChainNode unSavedNodes,
			String pk_item_group, boolean isLeave) throws BusinessException {
		Map<String, UFDouble[]> ret = new HashMap<String, UFDouble[]>();
		if (pk_psndocs != null && pk_psndocs.length > 0) {
			// PeriodVO[] threePeriods = null;
			Collection<PeriodVO> pds = this.getBaseDao().retrieveByClause(PeriodVO.class,
					"pk_org='" + pk_org + "' and '" + startDate.toString() + "' between begindate and enddate");
			PeriodVO periodCurrent = (pds == null || pds.size() == 0) ? null : pds.toArray(new PeriodVO[0])[0];
			if (periodCurrent == null) {
				throw new BusinessException("取當前期間錯誤");
			}
			Set<String> psnSet = new HashSet<>();
			psnSet.addAll(Arrays.asList(pk_psndocs));
			// 獲取加班管控
			Map<String, Integer> psnOtControMap = getPsnOvertimecontrol(psnSet, pk_org, startDate, endDate);
			// 加班校验月份控制
			String checkMonthType = SysInitQuery.getParaString(pk_org, "TWHRT18");
			// 获取本组织所有的考勤期间--只含开始和结束日期和考勤月
			Map<String, PeriodVO> datePeriodVOMap = getPeriodFromOrg(pk_org);
			// 周期缓存
			Map<String, ICheckDateScope> scopeCacheMap = new HashMap<>();
			for (String pk_psndoc : pk_psndocs) {
				long start = System.currentTimeMillis();
				// 取該員工結算週期
				ICheckDateScope dateScope = getCheckScopeWithCache(pk_org, pk_psndoc, psnOtControMap, checkMonthType,
						datePeriodVOMap, startDate, scopeCacheMap);
				if (dateScope == null || dateScope.getBegindate() == null || dateScope.getEnddate() == null) {
					continue;
				}

				UFLiteralDate sumStartDate = dateScope.getBegindate();
				UFLiteralDate sumEndDate = dateScope.getEnddate();

				if (endDate != null) {
					sumEndDate = sumEndDate.after(endDate) ? endDate : sumEndDate;
				}

				// MOD 離職計算期間後至當月最後一天的加班費
				// added by ssx on 2018-04-05
				if (isLeave) {
					sumStartDate = endDate.getDateAfter(1);
					sumEndDate = sumStartDate.getDateAfter(sumStartDate.getDaysMonth() - sumStartDate.getDay()); // 月末最後一天

					// 離職/留停日期在期間後至當月最後一天之間，否則不計算
					int isleavepsn = (int) this.getBaseDao().executeQuery(
							"select count(*) from hi_psnjob where trnsevent in (3,4) and pk_psndoc = '" + pk_psndoc
									+ "' and begindate between '" + sumStartDate.toString() + "' and '"
									+ sumEndDate.getDateAfter(1).toString() + "';", new ColumnProcessor());
					if (isleavepsn <= 0) {
						ret.put(pk_psndoc, new UFDouble[] { UFDouble.ZERO_DBL, UFDouble.ZERO_DBL, UFDouble.ZERO_DBL,
								UFDouble.ZERO_DBL });
						continue;
					}
				}
				// end MOD

				// ssx modified on 2019-11-18
				// 休假及加班日期統計範圍
				// (( 歸屬日 BETWEEN 期間起 AND 期間迄 AND 核准日 <= 本期自然月最後一天) OR
				// (歸屬日 < 期間起 AND 核准日 BETWEEN 本期自然月第一天 AND 本期自然月最後一天))
				String noCompTimeScope = "iscompensation='N' AND (( regdate BETWEEN '" + sumStartDate + "' AND '"
						+ sumEndDate + "' AND approveddate <= '"
						+ sumEndDate.getDateAfter(sumEndDate.getDaysMonth() - sumEndDate.getDay())
						+ "') OR (regdate < '" + sumStartDate + "' AND approveddate BETWEEN '"
						+ sumStartDate.getDateAfter(sumStartDate.getDaysMonth() - sumStartDate.getDay() + 1)
						+ "' AND '" + sumEndDate.getDateAfter(sumEndDate.getDaysMonth() - sumEndDate.getDay()) + "'))";
				//

				String strSQL = "SELECT  regdate, " + " case when (" + "( iscompensation='Y'"
						+ (isLeave ? "" : "AND issettled = 'Y' ")
						+ " AND settledate BETWEEN '"
						+ sumStartDate
						+ "' AND '"
						+ sumEndDate
						+ "')"
						+ ") then sum(remainhourstaxfree * taxfreerate) else 0 end  dayhourstaxfree_comp,"
						+ " case"
						// ssx added on 2019-12-31
						// 本期取消往期加班單時，要返回負數
						+ " WHEN (dr=1) "
						+ " THEN (-1) * SUM(remainhourstaxfree * taxfreerate)"
						// end
						+ " when ("
						+ noCompTimeScope
						+ ") then sum(remainhourstaxfree * taxfreerate) else 0 end  dayhourstaxfree_nocomp,"
						+ " case when ("
						+ "( iscompensation='Y'"
						+ (isLeave ? "" : "AND issettled = 'Y' ")
						+ " AND settledate BETWEEN '"
						+ sumStartDate
						+ "' AND '"
						+ sumEndDate
						+ "')"
						+ ") then sum(remainhourstaxable * taxablerate + extrahourstaxable * extrataxablerate) else 0 end  dayhourstaxable_comp, "
						+ " case "
						// ssx added on 2019-12-31
						// 本期取消往期加班單時，要返回負數
						+ " WHEN (dr=1) "
						+ " THEN (-1) * SUM(remainhourstaxable * taxablerate + extrahourstaxable * extrataxablerate) "
						// end
						+ "when ("
						+ noCompTimeScope
						+ ") then sum(remainhourstaxable * taxablerate + extrahourstaxable * extrataxablerate) else 0 end  dayhourstaxable_nocomp  "
						+ " FROM  hrta_segdetail"
						+ " WHERE ((dr=0 and "
						+ "(("
						+ noCompTimeScope
						+ ")  OR  ( iscompensation='Y'"
						+ (isLeave ? "" : "AND issettled = 'Y' ")
						+ " AND settledate BETWEEN '"
						+ sumStartDate
						+ "' AND '"
						+ sumEndDate
						+ "'))) "
						// ssx added on 2019-12-31
						// 本期取消往期加班單時，要返回負數
						+ "  OR  ( dr=1  AND ( iscompensation='N' AND (  regdate < '"
						+ sumStartDate
						+ "' AND approveddate < '"
						+ sumStartDate.getDateAfter(sumStartDate.getDaysMonth() - sumStartDate.getDay() + 1)
						+ "'  AND (   SELECT COUNT(pk_overtimereg) FROM tbm_overtimereg "
						+ "WHERE pk_psndoc = hrta_segdetail.pk_psndoc AND vestdate= hrta_segdetail.regdate AND overtimehour<0  AND SUBSTR(approve_time,0, 10) BETWEEN '"
						+ sumStartDate.getDateAfter(sumStartDate.getDaysMonth() - sumStartDate.getDay() + 1)
						+ "' AND '"
						+ sumEndDate.getDateAfter(sumEndDate.getDaysMonth() - sumEndDate.getDay())
						+ "' ) > 0)) ) ) "
						// end
						+ " and pk_psndoc = '"
						+ pk_psndoc
						+ "' group by pk_psndoc, regdate, approveddate,iscompensation, issettled, expirydate,settledate,dr order by pk_psndoc, regdate";
				List<Map<String, Object>> dayHoursMapList = (List<Map<String, Object>>) this.getBaseDao().executeQuery(
						strSQL, new MapListProcessor());

				UFDouble amountTaxfree_comp = UFDouble.ZERO_DBL;
				UFDouble amountTaxfree_nocomp = UFDouble.ZERO_DBL;
				UFDouble amountTaxable_comp = UFDouble.ZERO_DBL;
				UFDouble amountTaxable_nocomp = UFDouble.ZERO_DBL;
				if (dayHoursMapList != null && dayHoursMapList.size() > 0) {
					// 获取加班费 <calender,当天的时薪> 应免税该怎么区分?不区分应免税,直接返回该分组下所有的薪资项目
					UFLiteralDate[] allDates = createDateArray(dayHoursMapList);
					Map<String, Double> date2SalaryMap = NCLocator.getInstance().lookup(IWadaysalaryQueryService.class)
							.getHourSalaryByPsn(pk_org, pk_psndoc, pk_item_group, allDates);
					for (Map<String, Object> dayHoursMap : dayHoursMapList) {
						UFLiteralDate regDate = new UFLiteralDate((String) dayHoursMap.get("regdate"));
						UFDouble hourPay = UFDouble.ZERO_DBL;
						// 日薪重构start tank 2019年10月19日23:02:04
						Double hourPayDouble = date2SalaryMap.get(regDate.toString());
						if (hourPayDouble != null) {
							hourPay = new UFDouble(hourPayDouble);
						}
						// 日薪重构end tank 2019年10月19日23:02:18

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
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Integer> getPsnOvertimecontrol(Set<String> psnSet, String pk_org,
			UFLiteralDate periodBegindate, UFLiteralDate periodEnddate) throws BusinessException {
		InSQLCreator insql = new InSQLCreator();
		String psnInsql = insql.getInSQL(psnSet.toArray(new String[0]));
		// 查詢人員的加班控管
		String sql = "select tbm_psndoc.pk_psndoc pk_psndoc ,tbm_psndoc.overtimecontrol overtimecontrol,tbm_psndoc.begindate begindate from tbm_psndoc tbm_psndoc "
				+ " left outer join hi_psnjob T1 ON T1.pk_psnjob = tbm_psndoc.pk_psnjob  "
				+ " where tbm_psndoc.dr = 0 and tbm_psndoc.pk_org = '"
				+ pk_org
				+ "'  "
				+ " and ( tbm_psndoc.pk_psndoc in ( "
				+ psnInsql
				+ " ) "
				+ " AND T1.pk_org = '"
				+ pk_org
				+ "' )  "
				+ " and ( tbm_psndoc.pk_tbm_psndoc in ( "
				+ " select pk_tbm_psndoc from tbm_psndoc where pk_org = '"
				+ pk_org
				// 只要考勤档案在范围之内,都符合要求,然后取日期较大的那笔
				+ "' and ( begindate <= '" + periodEnddate + "' and enddate >= '" + periodBegindate + "' ) ) ) ";
		List<Object> tbmList = (List<Object>) baseDao.executeQuery(sql, new BeanListProcessor(TBMPsndocVO.class));
		Map<String, Integer> rtn = new HashMap<>();
		// 人员-考勤档案开始日期
		Map<String, UFLiteralDate> psnDateCacheMap = new HashMap<>();
		if (tbmList != null && tbmList.size() > 0) {
			for (Object obj : tbmList) {
				if (obj != null) {
					TBMPsndocVO tvo = (TBMPsndocVO) obj;
					// 当期间内有两条考勤档案时,以最新的那条考勤档案为准 tank 2020年2月4日13:51:31
					if (psnDateCacheMap.get(tvo.getPk_psndoc()) == null
							|| (psnDateCacheMap.get(tvo.getPk_psndoc()).before(tvo.getBegindate()))) {
						rtn.put(tvo.getPk_psndoc(), tvo.getOvertimecontrol());
						psnDateCacheMap.put(tvo.getPk_psndoc(), tvo.getBegindate());
					}

				}
			}
		}
		return rtn;
	}

	private Map<String, PeriodVO> getPeriodFromOrg(String pk_org) throws BusinessException {
		Map<String, PeriodVO> rsMap = new HashMap<>();

		String sql = "select timemonth,timeyear,begindate,enddate from tbm_period " + " where pk_org = '" + pk_org
				+ "' and dr = 0";
		@SuppressWarnings("unchecked")
		List<PeriodVO> periodVOList = (List<PeriodVO>) baseDao.executeQuery(sql, new BeanListProcessor(PeriodVO.class));
		if (periodVOList != null) {
			for (PeriodVO vo : periodVOList) {
				if (vo != null) {
					rsMap.put(vo.getTimeyear() + "-" + vo.getTimemonth(), vo);
				}
			}
		}

		return rsMap;
	}

	private UFLiteralDate[] createDateArray(List<Map<String, Object>> dayHoursMapList) {
		List<UFLiteralDate> retList = new ArrayList<UFLiteralDate>();
		for (Map<String, Object> dayHour : dayHoursMapList) {
			UFLiteralDate regDate = new UFLiteralDate((String) dayHour.get("regdate"));
			if (!retList.contains(regDate)) {
				retList.add(regDate);
			}
		}
		return retList.toArray(new UFLiteralDate[0]);
	}

	@Override
	public Map<String, UFDouble[]> calculateTaxableByDate(String pk_org, String[] pk_psndocs, UFLiteralDate startDate,
			UFLiteralDate endDate, UFDouble curNodeHours, OTSChainNode unSavedNodes, String pk_item_group,
			boolean isToRest) throws BusinessException {
		Map<String, UFDouble[]> ret = new HashMap<String, UFDouble[]>();
		if (pk_psndocs != null && pk_psndocs.length > 0) {
			Set<String> psnSet = new HashSet<>();
			psnSet.addAll(Arrays.asList(pk_psndocs));
			// 獲取加班管控
			Map<String, Integer> psnOtControMap = getPsnOvertimecontrol(psnSet, pk_org, startDate, endDate);
			// 加班校验月份控制
			String checkMonthType = SysInitQuery.getParaString(pk_org, "TWHRT18");
			// 获取本组织所有的考勤期间--只含开始和结束日期和考勤月
			Map<String, PeriodVO> datePeriodVOMap = getPeriodFromOrg(pk_org);
			// 周期缓存
			Map<String, ICheckDateScope> scopeCacheMap = new HashMap<>();
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

				// MOD by ssx on 2020-03-21
				// 在預存未保存分段時數時，也要分清是否受加班管控影響，否則可能造成應免稅臨界錯誤
				if (unSavedNodes != null) {
					OTSChainNode curUnSavedNode = unSavedNodes;
					do {
						totalHours = totalHours.add(getHoursInScope(curUnSavedNode.getNodeData()));
						curUnSavedNode = curUnSavedNode.getNextNode();
					} while (curUnSavedNode != null);
				}

				// 考勤規則
				TimeRuleVO timerule = getTimeRule(pk_org);

				// 一個週期內加班不能超過的時數
				UFDouble taxFreeLimitHours = timerule.getCtrlothours();
				// 一或三個週期內加班不能超過的總時數
				UFDouble totalTaxFreeLimitHours = taxFreeLimitHours;

				// PeriodVO[] threePeriods = null;
				PeriodVO periodCurrent = getPeriodService().queryByDate(pk_org, startDate);
				if (periodCurrent == null) {
					throw new BusinessException("取當前期間錯誤");
				}

				// 是否免稅
				boolean isTaxFree = true;

				// 取該員工結算週期
				ICheckDateScope dateScope = getCheckScopeWithCache(pk_org, pk_psndoc, psnOtControMap, checkMonthType,
						datePeriodVOMap, startDate, scopeCacheMap);
				UFLiteralDate sumStartDate = dateScope.getBegindate();
				UFLiteralDate sumEndDate = dateScope.getEnddate();

				Map<String, OTSChainNode> psnNodeMap = OTSChainUtils.buildChainPsnNodeMap(new String[] { pk_psndoc },
						sumStartDate, sumEndDate, null, isToRest, false, true, true, false);
				OTSChainNode curNode = psnNodeMap.get(pk_psndoc);

				if (curNode != null) {
					if (endDate != null) {
						sumEndDate = sumEndDate.after(endDate) ? endDate : sumEndDate;
					}

					boolean needRound = false; // 是否需要取整，當前計算日期與上次取的是否同一天，如果是則不取整

					curNode = OTSChainUtils.getFirstNode(curNode);
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

								if (curHours.doubleValue() > 0) {
									// 纍加加班時數
									totalHours = totalHours.add(curHours);
									if (isTaxFree) {
										if (totalHours.doubleValue() <= totalTaxFreeLimitHours.doubleValue()) {
											// 補休統計已打結算標記且最長可休日期在範圍內的，其餘的統計未打結算標記的
											if (curNode.getNodeData().getIscompensation().booleanValue()) {
												if (curNode.getNodeData().getIssettled().booleanValue()
														&& (curNode.getNodeData().getExpirydate()
																.isSameDate(sumStartDate) || curNode.getNodeData()
																.getExpirydate().after(sumStartDate)
																&& (curNode.getNodeData().getExpirydate()
																		.isSameDate(sumEndDate) || curNode
																		.getNodeData().getExpirydate()
																		.before(sumEndDate)))) {
													// 在免稅時數範圍內的，纍計到免稅加班時數
													totalTaxFreeHours = totalTaxFreeHours.add(curNode.getNodeData()
															.getHourstaxfree());
													// 在免稅時數範圍內的，纍計到免稅加班費
													UFDouble otAmount = UFDouble.ZERO_DBL;
													if (!StringUtils.isEmpty(pk_item_group)) {
														otAmount = getOTAmount(curNode.getNodeData().getTaxfreerate(),
																curNode.getNodeData().getHourlypay(), curNode
																		.getNodeData().getRemainhourstaxfree(),
																curNode.getNodeData(), DaySalaryEnum.TBMHOURSALARY,
																pk_item_group);
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
													otAmount = getOTAmount(curNode.getNodeData().getTaxfreerate(),
															curNode.getNodeData().getHourlypay(), curNode.getNodeData()
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
														&& (curNode.getNodeData().getExpirydate()
																.isSameDate(sumStartDate) || curNode.getNodeData()
																.getExpirydate().after(sumStartDate)
																&& (curNode.getNodeData().getExpirydate()
																		.isSameDate(sumEndDate) || curNode
																		.getNodeData().getExpirydate()
																		.before(sumEndDate)))) {

													UFDouble otAmount = UFDouble.ZERO_DBL;
													if (!StringUtils.isEmpty(pk_item_group)) {
														otAmount = getOTAmount(curNode.getNodeData().getTaxablerate(),
																curNode.getNodeData().getHourlypay(), curNode
																		.getNodeData().getRemainhourstaxable(),
																curNode.getNodeData(), DaySalaryEnum.TBMHOURSALARY,
																pk_item_group);
													}
													totalTaxableAmount = totalTaxableAmount.add(otAmount);

													if (!StringUtils.isEmpty(pk_item_group)) {
														otAmount = getOTAmount(curNode.getNodeData().getTaxfreerate(),
																curNode.getNodeData().getHourlypay(), curNode
																		.getNodeData().getRemainhourstaxfree(),
																curNode.getNodeData(), DaySalaryEnum.TBMHOURSALARY,
																pk_item_group);
													}
													totalTaxFreeAmount = totalTaxFreeAmount.add(otAmount);
												}
											} else {
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
											isTaxFree = false;
										}
									} else {
										totalTaxableHours = totalTaxableHours.add(curNode.getNodeData()
												.getHourstaxable());
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
													otAmount = getOTAmount(curNode.getNodeData().getTaxablerate(),
															curNode.getNodeData().getHourlypay(), curNode.getNodeData()
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
	public Map<String, UFDouble> getOvertimeHoursByType(String pk_org, String[] pk_psndocs, String[] pk_depts,
			UFBoolean isTermLeave, UFLiteralDate overtimeDate, String pk_overtimetype) throws BusinessException {
		return getOvertimeHours(pk_org, pk_psndocs, pk_depts, isTermLeave, overtimeDate, pk_overtimetype, false);
	}

	@Override
	public Map<String, UFDouble> getOvertimeToRestHoursByType(String pk_org, String[] pk_psndocs, String[] pk_depts,
			UFBoolean isTermLeave, UFLiteralDate overtimeDate, String pk_overtimetype) throws BusinessException {
		return getOvertimeHours(pk_org, pk_psndocs, pk_depts, isTermLeave, overtimeDate, pk_overtimetype, true);
	}

	private Map<String, UFDouble> getOvertimeHours(String pk_org, String[] pk_psndocs, String[] pk_depts,
			UFBoolean isTermLeave, UFLiteralDate overtimeDate, String pk_overtimetype, boolean isToRest)
			throws BusinessException {
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
	public OTLeaveBalanceVO[] getOvertimeToRestHoursByType(String pk_org, String[] pk_psndocs, String[] pk_depts,
			UFBoolean isTermLeave, UFLiteralDate beginDate, UFLiteralDate endDate, String pk_overtimetype)
			throws BusinessException {
		List<OTLeaveBalanceVO> headvos = new ArrayList<OTLeaveBalanceVO>();

		pk_psndocs = OTLeaveBalanceUtils.getPsnListByDateScope(pk_org, pk_psndocs, pk_depts, isTermLeave, beginDate,
				endDate);

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
						headvos.add(headVo);
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
									if (curNode.getNodeData().getSettledate() == null) {
										totalAmount = totalAmount.add(OTLeaveBalanceUtils.getUFDouble(curNode
												.getNodeData().getHours()));// 享有
										spentAmount = spentAmount.add(OTLeaveBalanceUtils.getUFDouble(curNode
												.getNodeData().getConsumedhours()));// 已休
										remainAmount = remainAmount.add(OTLeaveBalanceUtils.getUFDouble(curNode
												.getNodeData().getRemainhours()));// 剩余
										frozenAmount = frozenAmount.add(OTLeaveBalanceUtils.getUFDouble(curNode
												.getNodeData().getFrozenhours()));// 冻结
									}
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
		boolean isClose = UFDouble.ZERO_DBL.equals(curNode.getNodeData().getRemainhours())
				|| curNode.getNodeData().getSettledate() != null;
		if (!otIsClosedMap.containsKey(pk_overtimereg)) {
			otIsClosedMap.put(pk_overtimereg, new UFBoolean(isClose));
		} else {
			otIsClosedMap.put(pk_overtimereg, new UFBoolean((otIsClosedMap.get(pk_overtimereg) == null ? false
					: otIsClosedMap.get(pk_overtimereg).booleanValue()) && isClose));
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void settleByExpiryDate(String pk_org, String[] pk_psndocs, UFLiteralDate settleDate, Boolean isForce)
			throws BusinessException {
		String strCondition = " dr=0 and (isnull(issettled,'N')='N' or issettled='~') ";
		String strSQL = "";

		if (isForce) {
			// 如果為強制結算，必須指定人員列表，不允許整個組織強制結算
			if (pk_psndocs == null || pk_psndocs.length == 0) {
				throw new BusinessException("強制結算錯誤：未指定結算人員。");
			}
		}

		// 結算日期不能為空
		if (settleDate == null) {
			throw new BusinessException("結算錯誤：結算日期不能為空。");
		}

		if (pk_psndocs == null || pk_psndocs.length == 0) {
			if (StringUtils.isEmpty(pk_org)) {
				throw new BusinessException("結算錯誤：結算組織和結算人員不能同時為空。");
			} else {
				strSQL = "select distinct pk_psndoc from " + SegDetailVO.getDefaultTableName() + " where "
						+ strCondition;
				List<String> psnpks = (List<String>) this.getBaseDao().executeQuery(strSQL, new ColumnListProcessor());
				pk_psndocs = psnpks.toArray(new String[0]);
			}
		}

		List<String> psnBeSettled = new ArrayList<String>();
		for (String pk_psndoc : pk_psndocs) {
			// 取未结算加班分段明细（PK，注册日期，最长可休日期）
			strSQL = "select pk_segdetail, regdate, expirydate from "
					+ SegDetailVO.getDefaultTableName()
					+ " where isnull(dr, 0) = 0 and (isnull(issettled, 'N') = 'N' or issettled='~') and ISCOMPENSATION='Y' and pk_psndoc = '"
					+ pk_psndoc + "'";
			List<Map> segs = (List<Map>) this.getBaseDao().executeQuery(strSQL, new MapListProcessor());

			if (segs == null || segs.size() == 0) {
				continue;
			}

			// 取考勤档案（pk_psnorg, 开始日期，结束日期）
			strSQL = "select pk_psnorg, begindate, enddate from " + TBMPsndocVO.getDefaultTableName()
					+ " where pk_psndoc = '" + pk_psndoc + "' and dr=0;";
			List<Map> tbmPsndocs = (List<Map>) this.getBaseDao().executeQuery(strSQL, new MapListProcessor());

			if (tbmPsndocs == null || tbmPsndocs.size() == 0) {
				continue;
			}

			// 是否离职: 如果已经没有生效中考勤档案,那么結算日可結算.
			boolean isleave = true;
			for (Map tbmPsndocVo : tbmPsndocs) {
				UFLiteralDate psnBeginDate = new UFLiteralDate((String) tbmPsndocVo.get("begindate"));
				UFLiteralDate psnEndDate = new UFLiteralDate((String) tbmPsndocVo.get("enddate"));
				if ((settleDate.isSameDate(psnBeginDate) || settleDate.after(psnBeginDate))
						&& (settleDate.isSameDate(psnEndDate) || settleDate.before(psnEndDate))) {
					isleave = false;
				}
			}

			for (Map seg : segs) {
				if (isleave) {
					psnBeSettled.add((String) seg.get("pk_segdetail"));
					continue;
				}
				// MOD by ssx on 2020-04-16
				// 作废：業務日期發生的考勤期間，考勤檔案已關閉的，一定要可結算
				// 改判断业务日期为判断审核日期为年度可休日期之后一日
				// UFLiteralDate regDate = new UFLiteralDate((String)
				// seg.get("regdate"));
				// for (Map tbmPsndoc : tbmPsndocs) {
				// UFLiteralDate psnBeginDate = new UFLiteralDate((String)
				// tbmPsndoc.get("begindate"));
				// UFLiteralDate psnEndDate = new UFLiteralDate((String)
				// tbmPsndoc.get("enddate"));
				// if ((regDate.isSameDate(psnBeginDate) ||
				// regDate.after(psnBeginDate))
				// && (regDate.isSameDate(psnEndDate) ||
				// regDate.before(psnEndDate))) {
				// // 考勤期間已結束，結束日期早於結算日
				// if ((psnEndDate.isSameDate(settleDate) ||
				// psnEndDate.before(settleDate))
				// && psnEndDate.before(new UFLiteralDate("9999-12-01"))) {
				// // 考勤檔案已結束
				// psnBeSettled.add((String) seg.get("pk_segdetail"));
				// } else {
				// 未結束的要看最長可休日期早於結算日
				UFLiteralDate expiryDate = new UFLiteralDate((String) seg.get("expirydate"));
				if (expiryDate.before(settleDate)) {
					psnBeSettled.add((String) seg.get("pk_segdetail"));
				}
				// }
				// }
				// }
				// end
			}

		}

		if (psnBeSettled.size() > 0) {
			this.getBaseDao().executeUpdate(
					"update " + SegDetailVO.getDefaultTableName() + " set issettled='Y', settledate='"
							+ settleDate.toString() + "', modifier='" + InvocationInfoProxy.getInstance().getUserId()
							+ "', modifiedtime='" + new UFDateTime() + "' where pk_segdetail in ("
							+ new InSQLCreator().getInSQL(psnBeSettled.toArray(new String[0])) + ");");
		}
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
	public OTLeaveBalanceVO[] getOvertimeToRestHoursByType(String pk_org, String[] pk_psndocs, String[] pk_depts,
			UFBoolean isTermLeave, String queryYear, String pk_overtimetype) throws BusinessException {
		List<OTLeaveBalanceVO> headvos = new ArrayList<OTLeaveBalanceVO>();
		Map<String, UFLiteralDate> psnWorkStartDate = OTLeaveBalanceUtils.getPsnWorkStartDateMap(pk_org, pk_psndocs,
				pk_depts, isTermLeave, queryYear, pk_overtimetype);

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
	public void forceRebuildSegDetailByPsn(String pk_psndoc, UFLiteralDate startDate) throws BusinessException {
		// 如指定了起始日期，只能重建未被消耗的分段
		String strSQL = "";

		if (startDate != null) {
			strSQL = "select consumedhours from hrta_segdetail where pk_psndoc='" + pk_psndoc + "' and regdate >= '"
					+ startDate + "' and rownum=1";
			Object val = this.getBaseDao().executeQuery(strSQL, new ColumnProcessor());
			UFDouble consumedHours = new UFDouble(String.valueOf(val == null ? 0 : val));
			if (consumedHours.doubleValue() > 0) {
				throw new BusinessException("員工 ["
						+ ((PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class, pk_psndoc)).getCode()
						+ "] 的加班分段已被消耗，請清空起始日期重建所有分段。");
			}
		}

		strSQL = "select max(settledate) from hrta_segdetail where pk_psndoc = '"
				+ pk_psndoc
				+ "' "
				+ (startDate == null ? "" : " and regdate >= '" + startDate.toString()
						+ "' and isnull(settledate, '0001-01-01') >= '" + startDate.toString() + "'");
		String maxSettleDate = (String) this.getBaseDao().executeQuery(strSQL, new ColumnProcessor());
		if (!StringUtils.isEmpty(maxSettleDate)) {
			throw new BusinessException("員工 ["
					+ ((PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class, pk_psndoc)).getCode() + "] 的加班已結算 ["
					+ startDate.toString() + "]，請反結算後重試。");
		}

		strSQL = "delete from hrta_segdetailconsume where pk_segdetail in (select pk_segdetail from hrta_segdetail where pk_psndoc = '"
				+ pk_psndoc + "' " + (startDate == null ? "" : " and regdate>='" + startDate + "'") + ")";
		this.getBaseDao().executeUpdate(strSQL);

		strSQL = "delete from hrta_segdetail where  pk_psndoc = '" + pk_psndoc + "' "
				+ (startDate == null ? "" : " and regdate>='" + startDate + "'");
		this.getBaseDao().executeUpdate(strSQL);

		Collection<OvertimeRegVO> otList = null;
		Collection<LeaveRegVO> lvList = null;

		otList = this.getBaseDao().retrieveByClause(
				OvertimeRegVO.class,
				"pk_psndoc='" + pk_psndoc + "' "
						+ (startDate == null ? "" : " and overtimebegindate >= '" + startDate.getDateBefore(7) + "'"));
		lvList = this.getBaseDao().retrieveByClause(
				LeaveRegVO.class,
				"pk_psndoc='" + pk_psndoc + "'"
						+ (startDate == null ? "" : " and leavebegindate >= '" + startDate.getDateBefore(7) + "'"));

		// 重建加班數據
		rebuildOvertimeRecords(otList);

		// 重建休假數據
		rebuildLeaveRecords(lvList);

		// 補結算
		// if (!StringUtils.isEmpty(maxSettleDate)) {
		// ISegDetailService otSegSettleSvc =
		// NCLocator.getInstance().lookup(ISegDetailService.class);
		// otSegSettleSvc
		// .settleByExpiryDate(null, new String[] { pk_psndoc }, new
		// UFLiteralDate(maxSettleDate), false);
		// }
	}

	public void rebuildOvertimeRecords(Collection<OvertimeRegVO> otList) {
		if (otList != null && otList.size() > 0) {
			List<OvertimeRegVO> list = new ArrayList<OvertimeRegVO>();
			list.addAll(otList);
			Collections.sort(list, new Comparator<OvertimeRegVO>() {

				@Override
				public int compare(OvertimeRegVO ot1, OvertimeRegVO ot2) {
					if (ot1.getApprove_time().before(ot2.getApprove_time())) {
						return -1;
					} else if (ot1.getApprove_time().after(ot2.getApprove_time())) {
						return 1;
					} else {
						return 0;
					}
				}
			});

			for (OvertimeRegVO vo : list.toArray(new OvertimeRegVO[0])) {
				try {
					int count = (int) this.getBaseDao().executeQuery(
							"select count(pk_segdetail) from hrta_segdetail where pk_overtimereg='"
									+ vo.getPk_overtimereg() + "'", new ColumnProcessor());
					if (count == 0) {
						this.regOvertimeSegDetail(new OvertimeRegVO[] { vo });
					}
				} catch (BusinessException ex) {
					Logger.error("加班單 [" + vo.getOvertimebegintime().toString() + "] " + ex.getMessage());
				}
			}
		}
	}

	public void rebuildLeaveRecords(Collection<LeaveRegVO> lvList) {
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
				try {
					int count = (int) this.getBaseDao().executeQuery(
							"select count(pk_segdetailconsume) from hrta_segdetailconsume where pk_leavereg='"
									+ vo.getPk_leavereg() + "'", new ColumnProcessor());
					if (count == 0) {
						this.regOvertimeSegDetailConsume(new LeaveRegVO[] { vo });
					}
				} catch (BusinessException ex) {
					Logger.error("休假單 [" + vo.getLeavebegintime().toString() + "] " + ex.getMessage());
				}
			}
		}
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
					+ " AND og.workagestartdate IS NOT NULL AND psn.pk_psndoc = '" + pk_psndoc + "'";
			// AND ( "
			// +
			// " SELECT  COUNT(pk_segdetail)  FROM hrta_segdetail  WHERE  pk_psndoc = psn.pk_psndoc "
			// + " AND expirydate >= '" + cyear +
			// "' || RIGHT(og.workagestartdate, 6) AND expirydate < '"
			// + String.valueOf(Integer.valueOf(cyear) + 1) +
			// "' || RIGHT(og.workagestartdate, 6)) > 0"

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
					+ beginDate.toString() + "' and '" + endDate.toString() + "'";
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
						UFLiteralDate realDate = BillProcessHelper.getShiftRegDateByOvertime(vos[i]);
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
			if (lvList != null && lvList.size() > 0) {
				LeaveRegVO[] vos = lvList.toArray(new LeaveRegVO[0]);
				for (int i = 0; i < lvList.size(); i++) {
					// 只看前後三天內的單據，以免耗時過長
					if (UFLiteralDate.getDaysBetween(vos[i].getLeavebegindate(), beginDate) <= 3
							|| UFLiteralDate.getDaysBetween(vos[i].getLeavebegindate(), endDate) <= 3) {
						UFLiteralDate realDate = BillProcessHelper.getShiftRegDateByLeave(vos[i]);
						if (realDate.before(beginDate) || realDate.after(endDate)) {
							lvList.remove(vos[i]);
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
		rebuildOvertimeRecords(otList);

		// 重建休假數據
		rebuildLeaveRecords(lvList);
	}

	/**
	 * 获取此张加班单的校验周期
	 * 
	 * @param cvo
	 * @param checkMonthType
	 *            加班月份控制 0自然月/1考勤月
	 * @param checkStartMoth
	 *            加班起始校验月
	 * @param 期间缓存
	 * @param psnOtControMap
	 *            加班控管缓存 当组织(加班起始校验月,加班月份控制),日期,加班控管相同时,返回的期间应该是一样的
	 * @return ICheckDateScope 期间数据 XXX:缓存和逻辑分离
	 * @throws BusinessException
	 */
	private ICheckDateScope getCheckScopeWithCache(String pk_org, String pk_psndoc,
			Map<String, Integer> psnOtControMap, String checkMonthType, Map<String, PeriodVO> datePeriodVOMap,
			UFLiteralDate vesDate, Map<String, ICheckDateScope> scopeCacheMap) throws BusinessException {
		Calendar cal = Calendar.getInstance();

		// 加班管控
		if (!psnOtControMap.containsKey(pk_psndoc)) {
			String code = (String) baseDao.executeQuery("select code from bd_psndoc where pk_psndoc = '" + pk_psndoc
					+ "'", new ColumnProcessor());
			Logger.error("未找到人員 [" + code + "] 的有效考勤檔案");
			return null;
			// tank 注释 2020年2月4日19:54:15 返回null即可,如果报错,会影响薪资计算
			// throw new BusinessException("未找到人員 [" + code + "] 的有效考勤檔案");
		}

		Integer otContro = psnOtControMap.get(pk_psndoc);
		int monthCheck = -1;
		if (otContro == null) {
			// ssx modified on 2020-02-01
			// 有考勤檔案但加班管控未選擇的默認為一個月（啟碁）

			// String code = (String)
			// baseDao.executeQuery("select code from bd_psndoc where pk_psndoc = '"
			// + pk_psndoc
			// + "'", new ColumnProcessor());
			// Logger.error("未找到人員:[" + code + "],加班管控!");
			// return null;
			otContro = OvertimecontrolEunm.MANUAL_CHECK.toIntValue();
		}

		if (OvertimecontrolEunm.MACHINE_CHECK.toIntValue() == otContro) {
			// 三个月
			monthCheck = 3;
		} else if (OvertimecontrolEunm.MANUAL_CHECK.toIntValue() == otContro) {
			// 一个月
			monthCheck = 1;
		} else {
			String code = (String) baseDao.executeQuery("select code from bd_psndoc where pk_psndoc = '" + pk_psndoc
					+ "'", new ColumnProcessor());
			Logger.error("未找到人員 [" + code + "] 的加班管控設定");
			return null;
		}
		// 读取缓存
		String key = pk_org + vesDate.toStdString() + String.valueOf(monthCheck);
		if (scopeCacheMap.containsKey(key)) {
			return scopeCacheMap.get(key);
		}

		UFLiteralDate startCheckDate = null;
		if ("1".equals(checkMonthType)) {
			// 考勤月需要去查詢考勤期间的开始日期
			String checkStartMothCode = (String) baseDao.executeQuery("select begindate from tbm_period "
					+ " where pk_period = (select value from pub_sysinit " + " where initcode  = 'TWHRT17' "
					+ " and dr = 0 and pk_org = '" + pk_org + "') and dr = 0 ", new ColumnProcessor());
			try {
				startCheckDate = new UFLiteralDate(checkStartMothCode);
			} catch (Exception e) {
				throw new BusinessException("參數[TWHRT17]读取失败,请检查参数值!");
			}
			// 从起始期间开始,匹配加班归属期间
			// 匹配加班期間 一百年
			for (int i = 1; i < 1200; i++) {
				cal.setTime(startCheckDate.toDate());

				cal.add(Calendar.MONTH, monthCheck * (i - 1));
				UFLiteralDate beginMacthDateKey = new UFLiteralDate(cal.getTime());
				UFLiteralDate beginMacthDate = null;
				// ssx modified on 2020-02-07, 匹配期間不能用日期前7位，邊緣日期會導致期間錯誤
				PeriodVO beginMatchPeriodVO = getPeriodByDate(datePeriodVOMap, beginMacthDateKey);
				// end
				// 如果开始期间为空,则继续往下匹配一个周期
				int backPeriodTimes = 0;
				if (null == beginMatchPeriodVO) {
					for (backPeriodTimes = 1; backPeriodTimes <= monthCheck && beginMatchPeriodVO != null; backPeriodTimes++) {
						cal.add(Calendar.MONTH, 1);
						beginMacthDateKey = new UFLiteralDate(cal.getTime());
						// ssx modified on 2020-02-07, 匹配期間不能用日期前7位，邊緣日期會導致期間錯誤
						beginMatchPeriodVO = getPeriodByDate(datePeriodVOMap, beginMacthDateKey);
						// end
					}
					if (null == beginMatchPeriodVO) {
						return null;
					}
				} else {
					beginMacthDate = beginMatchPeriodVO.getBegindate();
				}

				cal.add(Calendar.MONTH, monthCheck - backPeriodTimes);
				// ssx modified on 2020-02-07, 結束日期應前後一月的前一天，以免與下一月第一天重疊
				UFLiteralDate endMacthDateKey = new UFLiteralDate(cal.getTime()).getDateBefore(1);
				// end
				UFLiteralDate endMacthDate = null;
				// ssx modified on 2020-02-07, 匹配期間不能用日期前7位，邊緣日期會導致期間錯誤
				PeriodVO endMatchPeriodVO = getPeriodByDate(datePeriodVOMap, endMacthDateKey);
				// end
				if (null == endMatchPeriodVO) {
					cal.add(Calendar.MONTH, -1);
					// 如果最后一个期间不足加班控管的月数,那么往前推 一个加班控管期间,直到有月数为止
					for (int j = 1; j < monthCheck - backPeriodTimes; j++) {
						cal.add(Calendar.MONTH, -1);
						endMacthDateKey = new UFLiteralDate(cal.getTime());
						// ssx modified on 2020-02-07, 匹配期間不能用日期前7位，邊緣日期會導致期間錯誤
						endMatchPeriodVO = getPeriodByDate(datePeriodVOMap, endMacthDateKey);
						// end
						if (endMatchPeriodVO != null) {
							endMacthDate = endMatchPeriodVO.getEnddate();
							break;
						}
					}
				} else {
					endMacthDate = endMatchPeriodVO.getEnddate();
				}

				boolean isMatch = vesDate.isSameDate(endMacthDate) || vesDate.isSameDate(beginMacthDate)
						|| (vesDate.after(beginMacthDate) && vesDate.before(endMacthDate));
				if (isMatch) {
					ICheckDateScope rs = new CheckDateScope(beginMacthDate, endMacthDate);

					if (3 == monthCheck) {
						// 填入3个期间,再多需要用反射修改循环和ICheckDateScope接口
						rs = new CheckDateScope(beginMacthDate, endMacthDate);
						rs.setScopeNumber(3);
						rs.setScopeOneBeginDate(beginMatchPeriodVO.getBegindate());
						rs.setScopeOneEndDate(beginMatchPeriodVO.getEnddate());

						cal.setTime(beginMacthDateKey.toDate());

						cal.add(Calendar.MONTH, 1);
						UFLiteralDate tempDate = new UFLiteralDate(cal.getTime());
						// ssx modified on 2020-02-07, 匹配期間不能用日期前7位，邊緣日期會導致期間錯誤
						PeriodVO tempPeriodVO = getPeriodByDate(datePeriodVOMap, tempDate);
						// end
						if (null != tempPeriodVO && backPeriodTimes <= 2) {
							rs.setScopeTwoBeginDate(tempPeriodVO.getBegindate());
							rs.setScopeTwoEndDate(tempPeriodVO.getEnddate());
							cal.add(Calendar.MONTH, 1);
							tempDate = new UFLiteralDate(cal.getTime());
							// ssx modified on 2020-02-07,
							// 匹配期間不能用日期前7位，邊緣日期會導致期間錯誤
							tempPeriodVO = getPeriodByDate(datePeriodVOMap, tempDate);
							// end
							if (null != tempPeriodVO && backPeriodTimes <= 1) {
								rs.setScopeTriBeginDate(tempPeriodVO.getBegindate());
								rs.setScopeTriEndDate(tempPeriodVO.getEnddate());
							} else {
								rs.setScopeNumber(2);
							}
						} else {
							rs.setScopeNumber(1);
						}
					}
					scopeCacheMap.put(key, rs);
					return rs;
				}
			}
		} else if ("0".equals(checkMonthType)) {
			// 自然月直接通过当月的第一天为开始日期
			String checkStartMothCode = (String) baseDao.executeQuery(
					"select timeyear||'-'||timemonth from tbm_period "
							+ " where pk_period = (select value from pub_sysinit " + " where initcode  = 'TWHRT17' "
							+ " and dr = 0 and pk_org = '" + pk_org + "') and dr = 0", new ColumnProcessor());
			try {
				startCheckDate = new UFLiteralDate(checkStartMothCode + "-01");
			} catch (Exception e) {
				throw new BusinessException("參數[TWHRT17]读取失败,请检查参数值!");
			}

			// 匹配加班期間 一百年
			for (int i = 1; i < 1200; i++) {

				cal.setTime(startCheckDate.toDate());
				cal.add(Calendar.MONTH, monthCheck * (i - 1));
				UFLiteralDate beginMacthDate = new UFLiteralDate(cal.getTime());

				cal.add(Calendar.MONTH, monthCheck);
				UFLiteralDate endMacthDate = new UFLiteralDate(cal.getTime());
				endMacthDate = endMacthDate.getDateBefore(1);

				boolean isMatch = vesDate.isSameDate(endMacthDate) || vesDate.isSameDate(beginMacthDate)
						|| (vesDate.after(beginMacthDate) && vesDate.before(endMacthDate));
				if (isMatch) {
					ICheckDateScope rs = new CheckDateScope(beginMacthDate, endMacthDate);
					if (3 == monthCheck) {
						// 填入3个期间
						cal.setTime(beginMacthDate.toDate());
						rs.setScopeOneBeginDate(beginMacthDate);
						rs.setScopeOneEndDate(beginMacthDate.getDateAfter(beginMacthDate.getDaysMonth() - 1));

						cal.add(Calendar.MONTH, 1);
						UFLiteralDate tempDate = new UFLiteralDate(cal.getTime());
						rs.setScopeTwoBeginDate(tempDate);
						rs.setScopeTwoEndDate(tempDate.getDateAfter(tempDate.getDaysMonth() - 1));

						cal.add(Calendar.MONTH, 1);
						tempDate = new UFLiteralDate(cal.getTime());
						rs.setScopeTriBeginDate(tempDate);
						rs.setScopeTriEndDate(tempDate.getDateAfter(tempDate.getDaysMonth() - 1));

						rs.setScopeNumber(3);
					}
					scopeCacheMap.put(key, rs);
					return rs;
				}
			}
		}
		scopeCacheMap.put(key, null);
		return null;

	}

	/**
	 * 根據檢查日期及期間起迄匹配正確的期間
	 * 
	 * @param datePeriodVOMap
	 * @param matchDate
	 * @return
	 * @since 2020-02-07
	 */
	private PeriodVO getPeriodByDate(Map<String, PeriodVO> datePeriodVOMap, UFLiteralDate matchDate) {
		for (Entry<String, PeriodVO> entryPeriod : datePeriodVOMap.entrySet()) {
			UFLiteralDate periodBegin = entryPeriod.getValue().getBegindate();
			UFLiteralDate periodEnd = entryPeriod.getValue().getEnddate();
			if ((matchDate.isSameDate(periodBegin) || matchDate.after(periodBegin))
					&& (matchDate.isSameDate(periodEnd) || matchDate.before(periodEnd))) {
				return entryPeriod.getValue();
			}
		}
		return null;
	}

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
			ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>(), new OvertimeFeeCalculatorThreadFactory(),
					CallerRunsPolicyHandler);

			// 返回值池
			List<Future<Map<String, UFDouble[]>>> futureDatas = new ArrayList<Future<Map<String, UFDouble[]>>>();
			final InvocationInfo invocationInfo = BDDistTokenUtil.getInvocationInfo();

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
				calculator.setProxyAgent(invocationInfo);
				calculator.setCurrentcount(count++);
				calculator.setTotalcount(pk_psndocs.length);

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
		private InvocationInfo callerInvocationInfo;

		@Override
		public Map<String, UFDouble[]> call() throws BusinessException {
			Map<String, UFDouble[]> ret = new HashMap<String, UFDouble[]>();
			// 线程中环境信息会丢失，主动的设置一下
			BDDistTokenUtil.setInvocationInfo(callerInvocationInfo);
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

		public void setProxyAgent(InvocationInfo invocationInfo) {
			callerInvocationInfo = invocationInfo;
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

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, UFDouble> calculateOvertimeHoursByType(String pk_org, String[] pk_psndocs,
			UFLiteralDate startDate, UFLiteralDate endDate, CalendarDateTypeEnum dateType) throws BusinessException {
		Map<String, UFDouble> ret = new HashMap<String, UFDouble>();
		if (pk_psndocs != null && pk_psndocs.length > 0) {
			Collection<PeriodVO> pds = this.getBaseDao().retrieveByClause(PeriodVO.class,
					"pk_org='" + pk_org + "' and '" + startDate.toString() + "' between begindate and enddate");
			PeriodVO periodCurrent = (pds == null || pds.size() == 0) ? null : pds.toArray(new PeriodVO[0])[0];
			if (periodCurrent == null) {
				throw new BusinessException("取當前期間錯誤");
			}
			Set<String> psnSet = new HashSet<>();
			psnSet.addAll(Arrays.asList(pk_psndocs));
			// 獲取加班管控
			Map<String, Integer> psnOtControMap = getPsnOvertimecontrol(psnSet, pk_org, startDate, endDate);
			// 加班校验月份控制
			String checkMonthType = SysInitQuery.getParaString(pk_org, "TWHRT18");
			// 获取本组织所有的考勤期间--只含开始和结束日期和考勤月
			Map<String, PeriodVO> datePeriodVOMap = getPeriodFromOrg(pk_org);
			// 周期缓存
			Map<String, ICheckDateScope> scopeCacheMap = new HashMap<>();
			for (String pk_psndoc : pk_psndocs) {
				// 取該員工結算週期
				ICheckDateScope dateScope = getCheckScopeWithCache(pk_org, pk_psndoc, psnOtControMap, checkMonthType,
						datePeriodVOMap, startDate, scopeCacheMap);
				if (dateScope == null || dateScope.getBegindate() == null || dateScope.getEnddate() == null) {
					continue;
				}

				UFLiteralDate sumStartDate = dateScope.getBegindate();
				UFLiteralDate sumEndDate = dateScope.getEnddate();

				if (endDate != null) {
					sumEndDate = sumEndDate.after(endDate) ? endDate : sumEndDate;
				}

				// MOD 離職計算期間後至當月最後一天的加班費
				// added by ssx on 2018-04-05
				boolean isLeavePsn = false;
				UFLiteralDate leaveStartDate = endDate.getDateAfter(1);
				UFLiteralDate leaveEndDate = leaveStartDate.getDateAfter(leaveStartDate.getDaysMonth()
						- leaveStartDate.getDay()); // 月末最後一天

				// 離職/留停日期在期間後至當月最後一天之間，要累加
				int isleavepsn = (int) this.getBaseDao().executeQuery(
						"select count(*) from hi_psnjob where trnsevent in (3,4) and pk_psndoc = '" + pk_psndoc
								+ "' and begindate between '" + leaveStartDate.toString() + "' and '"
								+ leaveEndDate.getDateAfter(1).toString() + "';", new ColumnProcessor());
				if (isleavepsn <= 0) {
					isLeavePsn = false;
				} else {
					isLeavePsn = true;
				}
				// end MOD

				// ssx modified on 2019-11-18
				// 休假及加班日期統計範圍
				// (( 歸屬日 BETWEEN 期間起 AND 期間迄 AND 核准日 <= 本期自然月最後一天) OR
				// (歸屬日 < 期間起 AND 核准日 BETWEEN 本期自然月第一天 AND 本期自然月最後一天))
				String noCompTimeScope = "seg.iscompensation='N' AND (( seg.regdate BETWEEN '" + sumStartDate
						+ "' AND '" + sumEndDate + "' AND seg.approveddate <= '"
						+ sumEndDate.getDateAfter(sumEndDate.getDaysMonth() - sumEndDate.getDay())
						+ "') OR (seg.regdate < '" + sumStartDate + "' AND seg.approveddate BETWEEN '"
						+ sumStartDate.getDateAfter(sumStartDate.getDaysMonth() - sumStartDate.getDay() + 1)
						+ "' AND '" + sumEndDate.getDateAfter(sumEndDate.getDaysMonth() - sumEndDate.getDay()) + "'))";

				String noCompTimeScopeLeave = "seg.iscompensation='N' AND (( seg.regdate BETWEEN '" + leaveStartDate
						+ "' AND '" + leaveEndDate + "' AND seg.approveddate <= '"
						+ leaveEndDate.getDateAfter(leaveEndDate.getDaysMonth() - leaveEndDate.getDay())
						+ "') OR (seg.regdate < '" + leaveStartDate + "' AND seg.approveddate BETWEEN '"
						+ leaveStartDate.getDateAfter(leaveStartDate.getDaysMonth() - leaveStartDate.getDay() + 1)
						+ "' AND '" + leaveEndDate.getDateAfter(leaveEndDate.getDaysMonth() - leaveEndDate.getDay())
						+ "'))";
				//

				String strSQL = "select sum(seg.remainhours) remainhours from hrta_segdetail seg inner join tbm_overtimereg ot on seg.pk_overtimereg = ot.pk_overtimereg inner join tbm_timeitemcopy tp on ot.pk_overtimetypecopy = tp.pk_timeitemcopy inner join hrta_segrule rl on tp.pk_segrule = rl.pk_segrule where rl.datetype="
						+ dateType.toIntValue() + " and seg.pk_psndoc='" + pk_psndoc + "' and " + noCompTimeScope;

				if (isLeavePsn) {
					strSQL = "select sum(remainhours) remainhours from ("
							+ strSQL
							+ " union all "
							+ "select sum(seg.remainhours) remainhours from hrta_segdetail seg inner join tbm_overtimereg ot on seg.pk_overtimereg = ot.pk_overtimereg inner join tbm_timeitemcopy tp on ot.pk_overtimetypecopy = tp.pk_timeitemcopy inner join hrta_segrule rl on tp.pk_segrule = rl.pk_segrule where rl.datetype="
							+ dateType.toIntValue() + " and seg.pk_psndoc='" + pk_psndoc + "' and "
							+ noCompTimeScopeLeave + ") tmp";
				}

				Object retValue = this.getBaseDao().executeQuery(strSQL, new ColumnProcessor());

				if (retValue != null) {
					UFDouble psnOTHours = new UFDouble(String.valueOf(retValue));
					ret.put(pk_psndoc, psnOTHours);
				} else {
					ret.put(pk_psndoc, UFDouble.ZERO_DBL);
				}
			}
		}
		return ret;
	}
}
