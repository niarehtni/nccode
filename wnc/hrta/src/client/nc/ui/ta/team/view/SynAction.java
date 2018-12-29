package nc.ui.ta.team.view;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
import nc.itf.bd.team.team01.ITeamMaintainService;
import nc.itf.ta.IPsnCalendarManageService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.dcm.chnlrplstrct.maintain.action.MessageDialog;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.bd.team.team01.entity.AggTeamVO;
import nc.vo.bd.team.team01.entity.TeamHeadVO;
import nc.vo.bd.team.team01.entity.TeamItemVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

public class SynAction extends HrAction {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = 5025380082445063329L;

	public SynAction() {
		this.setBtnName("修复空班组人员");
		this.setCode("SynAction");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void doAction(ActionEvent e) throws Exception {
		new SwingWorker() {

			BannerTimerDialog dialog = new BannerTimerDialog(SwingUtilities.getWindowAncestor(getModel().getContext().getEntranceUI()));
			String error = null;
			StringBuffer strMessage = new StringBuffer();

			@SuppressWarnings("unchecked")
			protected Boolean doInBackground() throws Exception {
				try {
					dialog.setStartText("正在查找工作记录班组为空的人员");
					dialog.start();
					Logger.error("-----開始班組為空人員班次同步------");
					strMessage.append(new UFDate().toString() + ": 查找工作記錄班組為空的員工。\r\n");
					IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
					Collection<PsnJobVO> jobvos =
					// query.retrieveByClause(PsnJobVO.class,
					// "pk_psnjob='0001W110000000063YNJ'");
					query.retrieveByClause(PsnJobVO.class, "pk_psnjob in (" + "select pk_psnjob from hi_psnjob job inner join bd_shift sft on job.jobglbdef1 = sft.pk_shift "
							+ "where nvl(jobglbdef1, '~')<>'~' and ismainjob='Y' " + "and begindate>='2018-02-01' and nvl(enddate, '9999-12-31')>'2018-02-01' " + ")");

					Map<String, Integer> psnCount = new HashMap<String, Integer>();
					for (PsnJobVO vo : jobvos) {
						if (!psnCount.containsKey(vo.getPk_psndoc())) {
							psnCount.put(vo.getPk_psndoc(), 0);
						}

						psnCount.put(vo.getPk_psndoc(), psnCount.get(vo.getPk_psndoc()) + 1);
					}

					Map<String, Integer> psnRun = new HashMap<String, Integer>();
					Integer totalCount = 0;
					Map<String, String[]> oldShiftMap = new HashMap<String, String[]>();
					if (jobvos != null && jobvos.size() > 0) {
						String pk_org = "0001A110000000000XBZ";
						for (PsnJobVO vo : jobvos) {
							totalCount++;
							dialog.setStartText("正在处理：" + vo.getClerkcode());
							strMessage.append(new UFDate().toString() + ": 處理員工 [" + vo.getClerkcode() + "]\r\n");
							if (!psnRun.containsKey(vo.getPk_psndoc())) {
								psnRun.put(vo.getPk_psndoc(), 0);
							}
							psnRun.put(vo.getPk_psndoc(), psnRun.get(vo.getPk_psndoc()) + 1);

							String pk_shift = (String) vo.getAttributeValue("jobglbdef1");
							ShiftVO shiftvo = (ShiftVO) query.retrieveByPK(ShiftVO.class, pk_shift);
							Collection<TeamHeadVO> tvos = query.retrieveByClause(TeamHeadVO.class, " (vteamcode='" + getTeamCodeByShiftCode(shiftvo.getCode()) + "') and pk_org='" + pk_org + "'");

							if (tvos != null && tvos.size() == 1) {
								String newTeamID = tvos.toArray(new TeamHeadVO[0])[0].getCteamid();
								if (!StringUtils.isEmpty(newTeamID)) {
									strMessage.append(new UFDate().toString() + ":同步員工 [" + vo.getClerkcode() + "]\r\n");
									dialog.setStartText("正在处理(" + totalCount + "/" + jobvos.size() + ")：" + vo.getClerkcode() + " (" + psnRun.get(vo.getPk_psndoc()) + "/"
											+ psnCount.get(vo.getPk_psndoc()) + ")");

									try {
										finishOldShift(query, oldShiftMap, vo);
										createNewShift(query, oldShiftMap, vo, newTeamID);

										// 同步班次
										((IPsnCalendarManageService) NCLocator.getInstance().lookup(IPsnCalendarManageService.class)).sync2TeamCalendar(pk_org, (StringUtils.isEmpty(newTeamID) ? null
												: newTeamID), new String[] { vo.getPk_psndoc() }, vo.getBegindate(),
												findEndDate(newTeamID, vo.getEnddate() == null ? "9999-12-31" : vo.getEnddate().toString(), query));
									} catch (Exception e) {
										strMessage.append(new UFDate().toString() + ": 員工信息 [" + vo.getClerkcode() + "] 出現錯誤\\r\n" + e.getMessage() + "\r\n");
									}
								} else {
									strMessage.append(new UFDate().toString() + ": 跳過無班組員工 [" + vo.getClerkcode() + "]\r\n");
								}
							}
						}
					}

				} catch (LockFailedException le) {
					error = le.getMessage();
				} catch (VersionConflictException le) {
					throw new BusinessException(le.getBusiObject().toString(), le);
				} catch (Exception e) {
					error = e.getMessage();
				} finally {
					Logger.error("-----班組為空人員班次同步結束------");
					Logger.error(strMessage.toString());
					MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(), "ERROR", strMessage.toString());
					dialog.end();
				}
				return Boolean.TRUE;
			}

			Map<String, String> shiftTeamCodeMap = null;

			private String getTeamCodeByShiftCode(String code) {
				if (shiftTeamCodeMap == null) {
					shiftTeamCodeMap = new HashMap<String, String>();
					shiftTeamCodeMap.put("W", "W");
					shiftTeamCodeMap.put("Y", "Y");
					shiftTeamCodeMap.put("Z", "Z");
					shiftTeamCodeMap.put("a", "a");
					shiftTeamCodeMap.put("b", "b");
					shiftTeamCodeMap.put("c", "c");
					shiftTeamCodeMap.put("d", "DA");
					shiftTeamCodeMap.put("e", "EA");
					shiftTeamCodeMap.put("f", "FA");
					shiftTeamCodeMap.put("g", "GA");
					shiftTeamCodeMap.put("h", "h");
					shiftTeamCodeMap.put("i", "i");
					shiftTeamCodeMap.put("j", "j");
					shiftTeamCodeMap.put("o", "o");
					shiftTeamCodeMap.put("p", "p");
					shiftTeamCodeMap.put("q", "QA");
					shiftTeamCodeMap.put("s", "SA");
					shiftTeamCodeMap.put("t", "t");
					shiftTeamCodeMap.put("u", "u");
					shiftTeamCodeMap.put("w", "w");
				}

				return shiftTeamCodeMap.get(code);
			}

			private void createNewShift(IUAPQueryBS query, Map<String, String[]> oldShiftMap, PsnJobVO vo, String newTeamID) throws BusinessException {
				// 同步班組
				TeamHeadVO headVO = (TeamHeadVO) query.retrieveByPK(TeamHeadVO.class, newTeamID);
				Collection<TeamItemVO> insertItemVOs = new ArrayList<TeamItemVO>();

				// 构造新成员TeamItemVO，加入itemVOs
				TeamItemVO newMemberVO = new TeamItemVO();
				newMemberVO.setPk_group(headVO.getPk_group());
				newMemberVO.setPk_org(headVO.getPk_org());
				newMemberVO.setPk_org_v(headVO.getPk_org_v());
				newMemberVO.setPk_dept(vo.getPk_dept());
				newMemberVO.setPk_psncl(vo.getPk_psncl());
				newMemberVO.setPk_psnjob(vo.getPk_psnjob());
				newMemberVO.setCworkmanid(vo.getPk_psndoc());
				newMemberVO.setCteamid(headVO.getCteamid());
				newMemberVO.setBmanager(UFBoolean.FALSE);
				newMemberVO.setDr(0);
				newMemberVO.setDstartdate(vo.getBegindate());
				newMemberVO.setDenddate(vo.getEnddate());
				newMemberVO.setStatus(VOStatus.NEW);
				insertItemVOs.add(newMemberVO);
				updateShiftGroup(headVO, insertItemVOs);

				oldShiftMap.put(vo.getPk_psndoc(), new String[] { vo.getPk_psnjob(), newTeamID });
			}

			private void finishOldShift(IUAPQueryBS query, Map<String, String[]> oldShiftMap, PsnJobVO vo) throws BusinessException {
				// 结束旧班组
				// 舊班組為空時，不做結束動作
				String oldShift = oldShiftMap.containsKey(vo.getPk_psndoc()) ? oldShiftMap.get(vo.getPk_psndoc())[1] : "";
				if (!StringUtils.isEmpty(oldShift)) {
					TeamHeadVO headVO = (TeamHeadVO) query.retrieveByPK(TeamHeadVO.class, oldShift);
					Collection<TeamItemVO> itemVOs = query.retrieveByClause(TeamItemVO.class, "cteamid='" + oldShift + "'");

					Collection<TeamItemVO> updateItemVOs = new ArrayList<TeamItemVO>();

					for (TeamItemVO teamvo : itemVOs) {
						if (teamvo.getPk_psnjob().equals(oldShiftMap.get(vo.getPk_psndoc())[0])) {
							teamvo.setDenddate(vo.getBegindate().getDateBefore(1));
							teamvo.setStatus(VOStatus.UPDATED);
							updateItemVOs.add(teamvo);
						}
					}

					if (updateItemVOs.size() > 0) {
						updateShiftGroup(headVO, itemVOs);
					}
				}
			}

			private UFLiteralDate findEndDate(String cteamid, String psnjobEnddate, IUAPQueryBS query) throws BusinessException {
				// 取班组已排班最后一日
				// 取人员在职日最后一日
				// 取日期较小的一个
				String strSQL = "select calendar from tbm_psncalendar where pk_psndoc in (select pk_psndoc from hi_psnjob where pk_psnjob in (select pk_psnjob from bd_team_b where cteamid = '"
						+ cteamid + "')) and calendar<='" + psnjobEnddate + "' order by calendar desc";
				String maxDate = (String) query.executeQuery(strSQL, new ColumnProcessor());
				return new UFLiteralDate(StringUtils.isEmpty(maxDate) ? psnjobEnddate : maxDate);
			}

			private void updateShiftGroup(TeamHeadVO headVO, Collection<TeamItemVO> itemVOs) throws BusinessException {
				AggTeamVO aggVO = new AggTeamVO();
				aggVO.setParent(headVO);
				aggVO.setChildrenVO(itemVOs.toArray(new TeamItemVO[0]));
				aggVO.getParentVO().setStatus(1);

				NCLocator.getInstance().lookup(ITeamMaintainService.class).update(new AggTeamVO[] { aggVO });
			}

			protected void done() {
				if (error != null) {
					ShowStatusBarMsgUtil.showErrorMsg("同步數據發生錯誤：", error, getModel().getContext());
				} else {
					ShowStatusBarMsgUtil.showStatusBarMsg("同步已完成。", getModel().getContext());
				}
			}
		}.execute();
	}
}
