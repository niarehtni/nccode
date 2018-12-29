package nc.ui.wa.psndocwadoc.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
import nc.itf.hi.IPsndocSubInfoService4JFS;
import nc.pubitf.para.SysInitQuery;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.pub.query.tools.ImageIconAccessor;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.wa.psndocwadoc.view.GroupInsAndDateChooseDialog;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;

public class NewGroupInsSettingAction extends PsndocWadocPubAction {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = 5740666043215823780L;
	private String errorMessage = "";
	private String pkGroupIns = null;
	private UFDate addDate = null;

	public NewGroupInsSettingAction() {
		putValue("Code", "GenerateNHI");
		setBtnName("團保新險种加保");
		putValue("SmallIcon", ImageIconAccessor.getIcon("update/export.gif"));
		putValue("ShortDescription", "為當前選定員工生成新險种團保投保設定資料" + "(Ctrl+Alt+G)");
		putValue("AcceleratorKey", KeyStroke.getKeyStroke(71, 10));
	}

	@SuppressWarnings("rawtypes")
	public void doAction(ActionEvent e) throws Exception {
		GroupInsAndDateChooseDialog dlg = new GroupInsAndDateChooseDialog(getEntranceUI(), "新險种加保"
				, "新險种加保", false, getContext().getPk_org());
		if(dlg.showModal() == 0){
			addDate = dlg.getAddDate();
			pkGroupIns = dlg.getPk_group_ins();
			new SwingWorker() {

				BannerTimerDialog dialog = new BannerTimerDialog(SwingUtilities.getWindowAncestor(getModel().getContext()
						.getEntranceUI()));
				String error = null;

				protected Boolean doInBackground() throws Exception {
					try {
						dialog.setStartText("正在生成新險种團保投保設定");
						dialog.start();

						String[] psnList = getMainComponent().getSelectedPsndocs();
						if (psnList.length >= 0) {
							// 獲取資料
							IPsndocSubInfoService4JFS service = NCLocator.getInstance().lookup(
									IPsndocSubInfoService4JFS.class);
							service.generateGroupInsWithNewIns(getModel().getContext().getPk_org(), psnList, new UFLiteralDate(
									"9999-12-31"), pkGroupIns, addDate);
						}
					} catch (LockFailedException le) {
						error = le.getMessage();
					} catch (VersionConflictException le) {
						throw new BusinessException(le.getBusiObject().toString(), le);
					} catch (Exception e) {
						error = e.getMessage();
					} finally {
						dialog.end();
					}
					return Boolean.TRUE;
				}

				protected void done() {
					if (error != null) {
						ShowStatusBarMsgUtil.showErrorMsg("生成團保设定發生錯誤", error, getModel().getContext());
					} else {
						ShowStatusBarMsgUtil.showStatusBarMsg("生成團保投保设定成功", getModel().getContext());
					}
				}
			}.execute();

			this.putValue("message_after_action", errorMessage);
		}
		
	}

	protected boolean isActionEnable() {
		boolean isTWEnabled = false;
		try {
			UFBoolean refVal = SysInitQuery.getParaBoolean(getContext().getPk_org(), "TWHR01");

			isTWEnabled = refVal == null ? false : refVal.booleanValue();

			UFBoolean TWHR02 = SysInitQuery.getParaBoolean(this.getContext().getPk_org(), "TWHR02");
			UFBoolean TWHR04 = SysInitQuery.getParaBoolean(this.getContext().getPk_org(), "TWHR04");

			isTWEnabled = isTWEnabled & !(TWHR02 == null ? false : TWHR02.booleanValue())
					& !(TWHR04 == null ? false : TWHR04.booleanValue());

		} catch (BusinessException e) {
		}

		return isTWEnabled
				&& ((getModel().getUiState() == UIState.NOT_EDIT) && (getModel().getSelectedData() != null) || (getModel()
						.getUiState() == UIState.EDIT)
						&& getMainComponent().getBillScrollPane().getTableModel().getRowCount() > 0);
	}
}
