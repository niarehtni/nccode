package nc.ui.wa.psndocwadoc.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

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
import nc.ui.uif2.UIState;
import nc.ui.wa.psndocwadoc.model.PsndocwadocAppModel;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

public class GenerateGroupInsSettingAction extends PsndocWadocPubAction {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = 5740666043215823780L;
	private String errorMessage = "";

	public GenerateGroupInsSettingAction() {
		putValue("Code", "GenerateNHI");
		setBtnName("團保加保");
		putValue("SmallIcon", ImageIconAccessor.getIcon("update/export.gif"));
		putValue("ShortDescription", "為當前選定員工生成首筆團保投保設定資料" + "(Ctrl+Alt+G)");
		putValue("AcceleratorKey", KeyStroke.getKeyStroke(71, 10));
	}

	public void doAction(ActionEvent e) throws Exception {
		PsndocwadocAppModel appmodel = (PsndocwadocAppModel) getModel();

		new SwingWorker() {

			BannerTimerDialog dialog = new BannerTimerDialog(
					SwingUtilities.getWindowAncestor(getModel().getContext()
							.getEntranceUI()));
			String error = null;

			protected Boolean doInBackground() throws Exception {
				try {
					dialog.setStartText("正在生成團保投保設定");
					dialog.start();

					List<String> psnList = new ArrayList();
					int selectRow = getMainComponent().getComponent()
							.getBillCardPanel().getBillTable().getSelectedRow();
					if (selectRow >= 0) {
						psnList.add((String) getMainComponent().getComponent()
								.getBillCardPanel().getBillModel()
								.getValueAt(selectRow, "pk_psndoc"));
						// 獲取資料
						IPsndocSubInfoService4JFS service = NCLocator
								.getInstance().lookup(
										IPsndocSubInfoService4JFS.class);
						service.generateGroupIns(getModel().getContext()
								.getPk_org(), psnList.toArray(new String[0]));
					}
				} catch (LockFailedException le) {
					error = le.getMessage();
				} catch (VersionConflictException le) {
					throw new BusinessException(le.getBusiObject().toString(),
							le);
				} catch (Exception e) {
					error = e.getMessage();
				} finally {
					dialog.end();
				}
				return Boolean.TRUE;
			}

			protected void done() {
				errorMessage = error;
			}
		}.execute();

		if (errorMessage != null) {
			putValue("message_after_action", "生成團保设定發生錯誤：" + errorMessage);
		} else {
			putValue("message_after_action", "生成團保投保设定成功。");
		}
	}

	protected boolean isActionEnable() {
		boolean isTWEnabled = false;
		try {
			isTWEnabled = SysInitQuery.getParaBoolean(getContext().getPk_org(),
					"TWHR01").booleanValue();

			UFBoolean TWHR02 = SysInitQuery.getParaBoolean(this.getContext()
					.getPk_org(), "TWHR02");
			UFBoolean TWHR04 = SysInitQuery.getParaBoolean(this.getContext()
					.getPk_org(), "TWHR04");

			isTWEnabled = isTWEnabled & !TWHR02.booleanValue()
					& !TWHR04.booleanValue();

		} catch (BusinessException e) {
			this.putValue("message_after_action",
					"取臺灣勞健保參數發生錯誤：" + e.getMessage());
		}

		return isTWEnabled && (getModel().getUiState() == UIState.NOT_EDIT)
				&& (getModel().getSelectedData() != null);
	}
}
