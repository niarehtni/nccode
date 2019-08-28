package nc.ui.wa.psndocwadoc.action;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.itf.wa.psndocwadoc.IPsndocBOISGenerateService;
import nc.pubitf.para.SysInitQuery;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.pub.query.tools.ImageIconAccessor;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.wa.psndocwadoc.view.EffectiveDateChooseDialog;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * �ڽ�����н�{��
 * 
 * @author Connie.ZH
 * 
 */

public class AdjustBOLIAction extends PsndocWadocPubAction {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = 5740666043215823780L;
	private String errorMessage = "";
	private UFDate effectiveDate = null;

	public AdjustBOLIAction() {
		putValue("Code", "AdjustBOLI");
		setBtnName("�ڽ�����н�{��");
		putValue("SmallIcon", ImageIconAccessor.getIcon("update/export.gif"));
		putValue("AcceleratorKey", KeyStroke.getKeyStroke(71, 10));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void doAction(ActionEvent e) throws Exception {
		final String[] psnArrays = getMainComponent().getSelectedPsndocs();
		if (ArrayUtils.isEmpty(psnArrays)) {
			ShowStatusBarMsgUtil.showErrorMsg("���Ʉڽ�����н�{���e�`", "�빴ѡ��Ա���ٲ�����", getModel().getContext());
		}
		EffectiveDateChooseDialog dlg = new EffectiveDateChooseDialog(getEntranceUI(), "�ڽ�����н�{����Ч��������",
				"�ڽ�����н�{����Ч��������", false, getContext().getPk_org());
		if (dlg.showModal() == 1) {// OK
			effectiveDate = dlg.getEffectiveDate();
			new SwingWorker() {

				BannerTimerDialog dialog = new BannerTimerDialog(SwingUtilities.getWindowAncestor(getModel()
						.getContext().getEntranceUI()));
				String error = null;

				protected Boolean doInBackground() throws Exception {
					try {
						dialog.setStartText("�������Ʉڽ�����н�{��..Ո�Ժ�..");
						dialog.start();
						JComponent parentUi = getModel().getContext().getEntranceUI();

						if (!ArrayUtils.isEmpty(psnArrays)) {
							// �@ȡ�Y��
							IPsndocBOISGenerateService service = NCLocator.getInstance().lookup(
									IPsndocBOISGenerateService.class);
							service.generateBOISData(effectiveDate.toLocalString(), psnArrays);
						}
					} catch (BusinessException le) {
						error = le.getMessage();
					} catch (Exception e) {
						error = e.getMessage();
					} finally {
						dialog.end();
					}
					return Boolean.TRUE;
				}

				protected void done() {
					if (!StringUtils.isEmpty(error)) {
						ShowStatusBarMsgUtil.showErrorMsg("���Ʉڽ�����н�{���e�`", error, getModel().getContext());
					} else {
						ShowStatusBarMsgUtil.showStatusBarMsg("���Ʉڽ�����н�{���ɹ�", getModel().getContext());
					}
				}
			}.execute();

			this.putValue("message_after_action", errorMessage);
		}

	}

	@Override
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