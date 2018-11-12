package nc.ui.wa.psndocwadoc.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
import nc.ui.wa.psndocwadoc.model.PsndocwadocAppModel;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

public class GenerateNHISettingAction extends PsndocWadocPubAction {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = 5740666043215823780L;

	public GenerateNHISettingAction() {
		putValue("Code", "GenerateNHI");
		setBtnName("�ڽ����ӱ�");
		putValue("SmallIcon", ImageIconAccessor.getIcon("update/export.gif"));
		putValue("ShortDescription", "�鮔ǰ�x���T�������׹P�ڱ������˼�����Ͷ���O���Y��" + "(Ctrl+Alt+G)");
		putValue("AcceleratorKey", KeyStroke.getKeyStroke(71, 10));
	}

	List<String> selectedPsndoc = null;

	public void doAction(ActionEvent e) throws Exception {
		PsndocwadocAppModel appmodel = (PsndocwadocAppModel) getModel();

		new SwingWorker() {

			BannerTimerDialog dialog = new BannerTimerDialog(SwingUtilities.getWindowAncestor(getModel().getContext()
					.getEntranceUI()));
			String error = null;

			protected Boolean doInBackground() throws Exception {
				try {
					dialog.setStartText("�������Ʉڽ���Ͷ���O��");
					dialog.start();

					List<String> psnList = getSelectedRows();
					if (psnList.size() > 0) {
						// �@ȡ�Y��
						IPsndocSubInfoService4JFS service = NCLocator.getInstance().lookup(
								IPsndocSubInfoService4JFS.class);
						service.generatePsnNHI(getModel().getContext().getPk_org(), psnList.toArray(new String[0]));
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
					ShowStatusBarMsgUtil.showErrorMsgWithClear("����Ͷ���趨�l���e�`", error, getContext());
				}
			}
		}.execute();

	}

	private List<String> getSelectedRows() {
		selectedPsndoc = new ArrayList<String>();
		for (int i = 0; i < this.getMainComponent().getBillScrollPane().getTableModel().getRowCount(); i++) {
			Vector billdata = this.getMainComponent().getBillScrollPane().getTableModel().getBillModelData();
			if (billdata.size() > 1) {
				Vector rowdata = (Vector) billdata.get(1);
				if (rowdata.size() > 0) {
					for (int j = 0; j < rowdata.size(); j++) {
						Vector data = (Vector) rowdata.get(j);
						if (data.get(0) == null ? false : ((Boolean) data.get(0))) {
							selectedPsndoc.add((String) data.get(1));
						}
					}
				}
			}
		}
		return selectedPsndoc;
	}

	protected boolean isActionEnable() {
		boolean isTWEnabled = false;
		try {
			isTWEnabled = SysInitQuery.getParaBoolean(getContext().getPk_org(), "TWHR01").booleanValue();

			UFBoolean TWHR02 = SysInitQuery.getParaBoolean(this.getContext().getPk_org(), "TWHR02");
			UFBoolean TWHR04 = SysInitQuery.getParaBoolean(this.getContext().getPk_org(), "TWHR04");

			isTWEnabled = isTWEnabled & !TWHR02.booleanValue() & !TWHR04.booleanValue();

		} catch (BusinessException e) {
			this.putValue("message_after_action", "ȡ̨���ڽ��������l���e�`��" + e.getMessage());
		}

		return isTWEnabled && (getModel().getUiState() == UIState.NOT_EDIT) && (getModel().getSelectedData() != null);
	}
}
