package nc.ui.wa.datainterface.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.hr.dataexchange.export.FormatHelper;
import nc.bs.logging.Logger;
import nc.itf.wa.datainterface.IReportExportService;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.wa.datainterface.TextFileFilter4TW;
import nc.vo.pub.BusinessException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class ExportBankDetailInfoAction extends HrAction {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = 5099164079859001851L;

	public ExportBankDetailInfoAction() {
		super();
		super.setBtnName("�R���y������ý�w�n");
		super.setCode("ExportBankDetailInfoAction");
		super.putValue(Action.SHORT_DESCRIPTION, "�R���y������ý�w�n");
	}

	private IReportExportService service = null;
	private String year = "";
	private String period = "";
	private String pk_wa_class = "";
	private String pk_org = "";

	private IReportExportService getService() {
		if (service == null) {
			service = (IReportExportService) NCLocator.getInstance().lookup(IReportExportService.class);
		}
		return service;
	}

	public String getYear() {
		return ((nc.ui.wa.datainterface.model.DataIOAppModel) this.getModel()).getWaLoginContext().getWaYear();
	}

	public String getPeriod() {
		return ((nc.ui.wa.datainterface.model.DataIOAppModel) this.getModel()).getWaLoginContext().getWaPeriod();
	}

	public String getPk_wa_class() {
		if (StringUtils.isEmpty(pk_wa_class)) {
			pk_wa_class = ((nc.ui.wa.datainterface.model.DataIOAppModel) this.getModel()).getWaLoginContext()
					.getPk_wa_class();
		}
		return pk_wa_class;
	}

	public String getPk_org() {
		if (StringUtils.isEmpty(pk_org)) {
			pk_org = getModel().getContext().getPk_org();
		}
		return pk_org;
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		JComponent parentUi = getModel().getContext().getEntranceUI();

		final String psnCls = (String) MessageDialog.showSelectDlg(parentUi, 0, "�ˆTe", "Ո�x��a��ý�w�n���ˆTe", new String[] {
				"DL", "IDL" }, 2);
		if (StringUtils.isEmpty(psnCls)) {
			this.putValue(MESSAGE_AFTER_ACTION, "������ȡ��");
			return;
		}

		new SwingWorker<Boolean, Void>() {

			BannerTimerDialog dialog = new BannerTimerDialog(getModel().getContext().getEntranceUI());
			String error = null;

			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					dialog.setStartText("������������ý�w�n�Y��...");
					dialog.start();
					String[] textArr = getService().getBankDetailReportText(getPk_org(), getYear() + getPeriod(),
							getPk_wa_class(), psnCls);

					if (textArr != null && textArr.length >= 2) {
						UIFileChooser fileChooser = new UIFileChooser();
						fileChooser.setDialogTitle("Ոָ��Ҫ�R�����ęn���Q");
						TextFileFilter4TW filter = new TextFileFilter4TW();
						filter.setFilterString("*.TXT");
						filter.setDescription("�R���y������ý�w�n");
						fileChooser.addChoosableFileFilter(filter);
						fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory().getAbsolutePath()
								+ "\\BANK_DETAIL_RPT_" + getYear() + getPeriod() + ".TXT"));
						int userSelection = fileChooser.showSaveDialog(getModel().getContext().getEntranceUI());

						String filename = "";
						File fileToSave = null;
						if (userSelection == JFileChooser.APPROVE_OPTION) {
							if (!fileChooser.getSelectedFile().getAbsoluteFile().toString().toUpperCase()
									.endsWith(".TXT")) {
								filename = fileChooser.getSelectedFile().getAbsolutePath() + ".TXT";
							} else {
								filename = fileChooser.getSelectedFile().getAbsolutePath();
							}
							fileToSave = new File(filename);

							StringBuilder sb = new StringBuilder();
							for (String txt : textArr) {
								sb.append(txt + "\r\n");
							}

							if (fileToSave != null) {
								// �xȡ����ı�
								FileUtils.writeStringToFile(fileToSave, sb.toString(), FormatHelper.TEXTENCODING);
							}

						} else {
							error = "�R����ȡ��";
						}
					} else {
						error = "�x�������g�ț]�ҵ��ɅR�����Y�ϡ�";
					}
				} catch (Throwable e) {
					error = e.getMessage();
				} finally {
					dialog.end();
				}

				return Boolean.TRUE;
			}

			@Override
			protected void done() {
				if (!StringUtils.isEmpty(error)) {
					ShowStatusBarMsgUtil.showErrorMsg("", error, getContext());
				} else {
					ShowStatusBarMsgUtil.showStatusBarMsg("���ɳɹ���", getContext());
				}
			}
		}.execute();

		putValue("message_after_action", "");
	}

	@Override
	protected boolean isActionEnable() {
		try {
			if (getService().checkPeriodWaDataExists(this.getPk_org(), new String[] { this.getPk_wa_class() },
					this.getYear() + this.getPeriod(), this.getYear() + this.getPeriod()) == 0) {
				return true;
			}
		} catch (BusinessException e) {
			Logger.error("����ڼ�����ʱ��������" + e.getMessage());
		}

		return false;
	}
}
