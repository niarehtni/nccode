package nc.ui.overtime.otleavebalance.action;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.ui.dcm.chnlrplstrct.maintain.action.MessageDialog;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModeDataManager;
import nc.ui.overtime.otleavebalance.view.OTLeaveBalanceOrgPanel;
import nc.ui.overtime.otleavebalance.view.PsnListViewPanel;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.editor.IBillListPanelView;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.ml.MultiLangUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;

import org.apache.commons.lang.StringUtils;

public class ForceRebuildPsnSegDetailAction extends HrAction {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 6101162527113648376L;
	private IBillListPanelView listView;
	private OTLeaveBalanceOrgPanel orgpanel;
	private String strError;

	public ForceRebuildPsnSegDetailAction() {
		setCode("REBUILDPSNSEG");
		setBtnName("全部重建");
	}

	public Container getParentContainer() {
		return SwingUtilities.getWindowAncestor(getContext().getEntranceUI());
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		PsnListViewPanel psnSelPanel = new PsnListViewPanel();
		psnSelPanel.setPk_org(getContext().getPk_org());
		if (psnSelPanel.showModal() == UIDialog.ID_OK) {
			if (psnSelPanel.getSelectedPsndocPKs() == null) {
				throw new BusinessException("未選擇要重建的人員");
			}

			final UFLiteralDate startDate = psnSelPanel.getStartDate();
			final UFBoolean createCurrentTerm = psnSelPanel.getCreateCurrentTerm();
			String message = "";
			if (startDate == null) {
				message = "是否重建已選定員工全部分段資料？";
			} else {
				message = "是否重建已選定員工自 [" + startDate + "] 起之分段資料";
			}
			if (MessageDialog.showOkCancelDlg(this.getEntranceUI(), "確認", message) == MessageDialog.ID_OK) {
				if (psnSelPanel.getSelectedPsndocPKs() != null && psnSelPanel.getSelectedPsndocPKs().length > 0) {
					final String[] pks = psnSelPanel.getSelectedPsndocPKs();

					new SwingWorker<Boolean, Void>() {
						BannerTimerDialog dialog = new BannerTimerDialog(getParentContainer());

						@Override
						protected Boolean doInBackground() throws Exception {
							try {
								IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
								ISegDetailService svc = NCLocator.getInstance().lookup(ISegDetailService.class);
								dialog.start();
								for (String pk_psndoc : pks) {
									PsndocVO psnvo = (PsndocVO) query.retrieveByPK(PsndocVO.class, pk_psndoc);

									if (psnvo != null) {
										dialog.setStartText("<html>正在重建員工下列員工的"
												+ (createCurrentTerm.booleanValue() ? "當期" : "全部")
												+ "分段資料：<br />"
												+ MultiLangUtil.getSuperVONameOfCurrentLang(psnvo, PsndocVO.NAME,
														PsndocVO.NAME) + " [" + psnvo.getCode() + "]</html>");
										svc.forceRebuildSegDetailByPsn(getContext().getPk_org(), pk_psndoc, startDate,
												createCurrentTerm);
									} else {
										throw new BusinessException("未找到員工檔案。");
									}
								}

							} catch (Exception e) {
								setStrError(e.getMessage() == null ? e.toString() : e.getMessage());
							} finally {
								dialog.end();
							}

							return Boolean.FALSE;
						}

						@Override
						protected void done() {
							if (!StringUtils.isEmpty(getStrError())) {
								ShowStatusBarMsgUtil.showErrorMsg("錯誤", getStrError(), getContext());
							} else {
								ShowStatusBarMsgUtil.showStatusBarMsg("重建分段完畢。", getContext());
							}
						}
					}.execute();
				}
			}
		}

	}

	public IBillListPanelView getListView() {
		return listView;
	}

	public void setListView(IBillListPanelView listView) {
		this.listView = listView;
	}

	protected boolean isActionEnable() {
		LeaveTypeCopyVO ltvo = (LeaveTypeCopyVO) ((OTLeaveBalanceModeDataManager) getOrgpanel().getDataManager())
				.getHierachicalModel().getSelectedData();
		String pk_leavetypecopy = "";
		try {
			pk_leavetypecopy = SysInitQuery.getParaString(getOrgpanel().getRefPane().getRefPK(), "TWHRT08");
		} catch (BusinessException e) {

		}
		return getOrgpanel().getRefPane().getValueObj() != null
				&& (getOrgpanel().getCboYear().getSelectdItemValue() != null || getOrgpanel().getRefBeginDate()
						.getValueObj() != null && getOrgpanel().getRefEndDate().getValueObj() != null)
				&& pk_leavetypecopy != null && ltvo != null && pk_leavetypecopy.equals(ltvo.getPk_timeitemcopy());
	}

	public OTLeaveBalanceOrgPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(OTLeaveBalanceOrgPanel orgpanel) {
		this.orgpanel = orgpanel;
	}

	/**
	 * @return strError
	 */
	public String getStrError() {
		return strError;
	}

	/**
	 * @param strError
	 *            要設定的 strError
	 */
	public void setStrError(String strError) {
		this.strError = strError;
	}

}
