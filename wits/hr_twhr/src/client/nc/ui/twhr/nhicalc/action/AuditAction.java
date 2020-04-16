package nc.ui.twhr.nhicalc.action;

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
import nc.itf.twhr.INhicalcMaintain;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BatchBillTable;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.apache.commons.lang.StringUtils;

public class AuditAction extends NCAction {
	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 3690234657179448172L;

	private BatchBillTableModel model = null;

	private BatchBillTable editor = null;

	private NhiOrgHeadPanel orgpanel = null;

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		if (MessageDialog.showYesNoDlg(
				getModel().getContext().getEntranceUI(),
				NCLangRes.getInstance().getStrByID("68861705",
						"AuditAction-0000")/* 提示 */, NCLangRes.getInstance()
						.getStrByID("68861705", "AuditAction-0001")/*
																	 * 审核操作后将无法进行选定期间的劳健保数据生成
																	 * 、
																	 * 修改和计算，是否继续？
																	 */) == UIDialog.ID_YES) {
			
			// 勞健保計算異常警示  by George 20190812 缺陷Bug #29455
			// 點擊審核按鈕後，系統將人員勞健退子集內當前投保級距與計算級距進行匹配
			String strSQL_range = "select code, name from bd_psndoc where bd_psndoc.pk_psndoc in "
					+ " ( select twhr_nhicalc.pk_psndoc from (select * from twhr_nhicalc where twhr_nhicalc.dr = 0 "
					+ " and pk_org = N'" + getOrgpanel().getRefPane().getRefPK() + "' "
					+ " and cyear = N'" + getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[0] + "' "
					+ " and cperiod = N'" + getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[1] + "') twhr_nhicalc "
					+ " left join ( SELECT * FROM hi_psndoc_glbdef3 WHERE dr = 0 and glbdef14 = N'Y' "
					+ " and begindate <= '" + getLastDayOfMonth(getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[0], 
							getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[1]).toString().substring(0, 10) + "' "
					+ " and (enddate >= '" + getFirstDayOfMonth(getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[0],
							getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[1]).toString().substring(0, 10) + "' "
					+ " or enddate is null) and glbdef2 = '本人' "
					+ " and pk_psndoc in (select pk_psndoc from twhr_nhicalc where twhr_nhicalc.dr = 0 "
					+ " and pk_org = N'" + getOrgpanel().getRefPane().getRefPK() + "' "
					+ " and cyear = N'" + getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[0] + "' "
					+ " and cperiod = N'" + getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[1] + "' )) "
					+ " hi_psndoc_glbdef3 on hi_psndoc_glbdef3.pk_psndoc = twhr_nhicalc.pk_psndoc "
					+ " left join ( SELECT * FROM hi_psndoc_glbdef2 WHERE dr = 0 and ( glbdef10 = N'Y' or glbdef11 = N'Y' ) "
					+ " and begindate <= '" + getLastDayOfMonth(getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[0], 
							getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[1]).toString().substring(0, 10) + "' "
					+ " and (enddate >= '" + getFirstDayOfMonth(getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[0],
							getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[1]).toString().substring(0, 10) + "' "
					+ " or enddate is null) and pk_psndoc in (select pk_psndoc  from twhr_nhicalc where twhr_nhicalc.dr = 0 "
					+ " and pk_org = N'" + getOrgpanel().getRefPane().getRefPK() + "' "
					+ " and cyear = N'" + getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[0] + "' "
					+ "and cperiod = N'" + getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[1] + "')) "
					+ " hi_psndoc_glbdef2 on hi_psndoc_glbdef2.pk_psndoc = twhr_nhicalc.pk_psndoc where "
					+ " (twhr_nhicalc.oldhealthrange != hi_psndoc_glbdef3.glbdef16) "
					+ " or (twhr_nhicalc.oldlaborrange != hi_psndoc_glbdef2.glbdef4) "
					+ " or (twhr_nhicalc.oldretirerange != hi_psndoc_glbdef2.glbdef7)) order by code ";
			
			IUAPQueryBS iUAPQueryBS = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());   
			List<Map> rangechecks = (List<Map>) iUAPQueryBS.executeQuery(strSQL_range, new MapListProcessor());
			
			// 顯示字串分行處裡
			StringBuilder str_rangechecks = new StringBuilder();
			for (Map rangecheck : rangechecks) {
				if ("".equals(rangecheck)) {
					continue;
				}
				str_rangechecks.append(rangecheck.toString().substring(6, 15)).append("\t");
				str_rangechecks.append(StringUtils.substringBeforeLast(rangecheck.toString().substring(22), "}")).append("\n");
			}
			
			// 取得上一個月的日期
			Calendar c = Calendar.getInstance();
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");   
	        Date date = null;   
	        date = sdf.parse(this.getOrgpanel().getPeriodRefModel().getRefNameValue());
	        c.setTime(date);
	        c.add(Calendar.MONTH, -1);
	        String strDate = sdf.format(c.getTime()); 
	        
	        // 當人員實際投保級距與計算級距相同，且人員當前期間級距與上一期間級距相同時，
	        // 比對當前期間與上一期間的健保承擔金額(個人)、健保承擔金額(雇主)進行匹配
	        String strSQL_healths = "select code, name from bd_psndoc where pk_psndoc in ( select twhr_now.pk_psndoc from "
	        		+ " ( select * from twhr_nhicalc where twhr_nhicalc.dr = 0 "
	        		+ " and pk_org = N'" + getOrgpanel().getRefPane().getRefPK() + "' "
	        		+ " and cyear = N'" + getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[0] + "' "
	        		+ " and cperiod = N'" + getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[1] + "' ) "
	        		+ " twhr_now inner join ( select * from twhr_nhicalc where twhr_nhicalc.dr = 0 "
	        		+ " and pk_org = N'" + getOrgpanel().getRefPane().getRefPK() + "' "
	        		+ " and cyear = N'" + strDate.substring(0, 4) + "' and cperiod = N'" + strDate.substring(5, 7) + "' ) "
	        		+ " twhr_before on twhr_now.pk_psndoc = twhr_before.pk_psndoc "
	        		+ " and twhr_now.oldhealthrange = twhr_before.oldhealthrange "
	        		+ " and twhr_now.oldlaborrange = twhr_before.oldlaborrange "
	        		+ " and twhr_now.oldretirerange = twhr_before.oldretirerange "
	        		+ " where twhr_now.healthstuff != twhr_before.healthstuff "
	        		+ " or twhr_now.healthhirer != twhr_before.healthhirer ) order by code "; 
			
	        
			List<Map> healthchecks = (List<Map>) iUAPQueryBS.executeQuery(strSQL_healths, new MapListProcessor());
			
			// 顯示字串分行處裡
			StringBuilder str_healthchecks = new StringBuilder();
			for (Map healthcheck : healthchecks) {
				if ("".equals(healthcheck)) {
					continue;
				}
				str_healthchecks.append(healthcheck.toString().substring(6, 15)).append("\t");
				str_healthchecks.append(StringUtils.substringBeforeLast(healthcheck.toString().substring(22), "}")).append("\n");
			}
	        
			// 沒有差異時，不顯示提示視窗直接審核
			if ("".equals(str_rangechecks.toString()) && "".equals(str_healthchecks.toString())) {

				new SwingWorker() {
	
					BannerTimerDialog dialog = new BannerTimerDialog(
							SwingUtilities.getWindowAncestor(getModel()
									.getContext().getEntranceUI()));
					String error = null;
	
					protected Boolean doInBackground() throws Exception {
						try {
							dialog.setStartText("正在同步勞健保計算結果");
							dialog.start();
	
							INhicalcMaintain nhiSrv = NCLocator.getInstance()
									.lookup(INhicalcMaintain.class);
							nhiSrv.audit(getOrgpanel().getRefPane().getRefPK(),
									getOrgpanel().getPeriodRefModel()
											.getRefNameValue().split("-")[0],
									getOrgpanel().getPeriodRefModel()
											.getRefNameValue().split("-")[1]);
	
							getOrgpanel().getDataManager()
									.initModelBySqlWhere(
											" and pk_org='"
													+ getOrgpanel().getRefPane()
															.getRefPK()
													+ "' and cyear='"
													+ getOrgpanel()
															.getPeriodRefModel()
															.getRefNameValue()
															.split("-")[0]
													+ "' and cperiod='"
													+ getOrgpanel()
															.getPeriodRefModel()
															.getRefNameValue()
															.split("-")[1] + "'");
	
							getModel().setUiState(UIState.NOT_EDIT);
						} catch (LockFailedException le) {
							error = le.getMessage();
						} catch (VersionConflictException le) {
							throw new BusinessException(le.getBusiObject()
									.toString(), le);
						} catch (Exception e) {
							error = e.getMessage();
						} finally {
							dialog.end();
						}
						return Boolean.TRUE;
					}
	
					protected void done() {
						if (error != null) {
							ShowStatusBarMsgUtil.showErrorMsg("審覈發生錯誤：", error,
									getModel().getContext());
						} else {
							ShowStatusBarMsgUtil.showStatusBarMsg(
									"勞健保資料已審覈並同步至員工資料子集。", getModel().getContext());
						}
					}
				}.execute();	
			} else {
				// 有差異時，顯示提示視窗
			    if ( MessageDialog.showYesNoDlg(getModel().getContext().getEntranceUI(), 
						NCLangRes.getInstance().getStrByID("68861705","AuditAction-0000")/* 提示 */,
						NCLangRes.getInstance().getStrByID("68861705","AuditAction-0002")/*
						                                                                  * 此次計算存在級距與金額異常人員，
						                                                                  * 請確認是否繼續?\n\n
						                                                                  * 投保級距差異人員如下:\n
						                                                                  */
						+ str_rangechecks.toString()
						+ NCLangRes.getInstance().getStrByID("68861705","AuditAction-0003")/* \n健保代扣金額差異人員如下:\n */
						+ str_healthchecks.toString()) == UIDialog.ID_YES) {

					new SwingWorker() {
		
						BannerTimerDialog dialog = new BannerTimerDialog(
								SwingUtilities.getWindowAncestor(getModel()
										.getContext().getEntranceUI()));
						String error = null;
		
						protected Boolean doInBackground() throws Exception {
							try {
								dialog.setStartText("正在同步勞健保計算結果");
								dialog.start();
		
								INhicalcMaintain nhiSrv = NCLocator.getInstance()
										.lookup(INhicalcMaintain.class);
								nhiSrv.audit(getOrgpanel().getRefPane().getRefPK(),
										getOrgpanel().getPeriodRefModel()
												.getRefNameValue().split("-")[0],
										getOrgpanel().getPeriodRefModel()
												.getRefNameValue().split("-")[1]);
		
								getOrgpanel().getDataManager()
										.initModelBySqlWhere(
												" and pk_org='"
														+ getOrgpanel().getRefPane()
																.getRefPK()
														+ "' and cyear='"
														+ getOrgpanel()
																.getPeriodRefModel()
																.getRefNameValue()
																.split("-")[0]
														+ "' and cperiod='"
														+ getOrgpanel()
																.getPeriodRefModel()
																.getRefNameValue()
																.split("-")[1] + "'");
		
								getModel().setUiState(UIState.NOT_EDIT);
							} catch (LockFailedException le) {
								error = le.getMessage();
							} catch (VersionConflictException le) {
								throw new BusinessException(le.getBusiObject()
										.toString(), le);
							} catch (Exception e) {
								error = e.getMessage();
							} finally {
								dialog.end();
							}
							return Boolean.TRUE;
						}
		
						protected void done() {
							if (error != null) {
								ShowStatusBarMsgUtil.showErrorMsg("審覈發生錯誤：", error,
										getModel().getContext());
							} else {
								ShowStatusBarMsgUtil.showStatusBarMsg(
										"勞健保資料已審覈並同步至員工資料子集。", getModel().getContext());
							}
						}
					}.execute();
		        }
			}
		}
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

	public BatchBillTableModel getModel() {
		return model;
	}

	public void setModel(BatchBillTableModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public BatchBillTable getEditor() {
		return editor;
	}

	public void setEditor(BatchBillTable editor) {
		this.editor = editor;
	}

	public boolean isEnabled() {
		INhicalcMaintain nhiSrv = NCLocator.getInstance().lookup(
				INhicalcMaintain.class);
		boolean isaudit = false;
		try {
			if (!StringUtils.isEmpty(getOrgpanel().getRefPane().getRefPK())
					&& !StringUtils.isEmpty(this.getOrgpanel()
							.getPeriodRefModel().getRefNameValue())) {
				isaudit = nhiSrv.isAudit(getOrgpanel().getRefPane().getRefPK(),
						this.getOrgpanel().getPeriodRefModel()
								.getRefNameValue().split("-")[0], this
								.getOrgpanel().getPeriodRefModel()
								.getRefNameValue().split("-")[1]);
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}

		return (!isaudit) && model.getUiState() == UIState.NOT_EDIT
				&& !model.getRows().isEmpty();
	}

	public NhiOrgHeadPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(NhiOrgHeadPanel orgpanel) {
		this.orgpanel = orgpanel;
	}
}
