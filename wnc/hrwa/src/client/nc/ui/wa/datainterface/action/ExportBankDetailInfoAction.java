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
		super.setBtnName("匯出銀行明細媒體檔");
		super.setCode("ExportBankDetailInfoAction");
		super.putValue(Action.SHORT_DESCRIPTION, "匯出銀行明細媒體檔");
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

		final String psnCls = (String) MessageDialog.showSelectDlg(parentUi, 0, "人員類別", "請選擇產生媒體檔的人員類別", new String[] {
				"DL", "IDL" }, 2);
		if (StringUtils.isEmpty(psnCls)) {
			this.putValue(MESSAGE_AFTER_ACTION, "操作已取消");
			return;
		}

		new SwingWorker<Boolean, Void>() {

			BannerTimerDialog dialog = new BannerTimerDialog(getModel().getContext().getEntranceUI());
			String error = null;

			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					dialog.setStartText("正在生成明細媒體檔資料...");
					dialog.start();
					String[] textArr = getService().getBankDetailReportText(getPk_org(), getYear() + getPeriod(),
							getPk_wa_class(), psnCls);

					if (textArr != null && textArr.length >= 2) {
						UIFileChooser fileChooser = new UIFileChooser();
						fileChooser.setDialogTitle("請指定要匯出的文檔名稱");
						TextFileFilter4TW filter = new TextFileFilter4TW();
						filter.setFilterString("*.TXT");
						filter.setDescription("匯出銀行明細媒體檔");
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
								// 讀取報表文本
								FileUtils.writeStringToFile(fileToSave, sb.toString(), FormatHelper.TEXTENCODING);
							}

						} else {
							error = "匯出已取消";
						}
					} else {
						error = "選定的期間內沒找到可匯出的資料。";
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
					ShowStatusBarMsgUtil.showStatusBarMsg("生成成功。", getContext());
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
			Logger.error("检查期间数据时发生错误：" + e.getMessage());
		}

		return false;
	}
}
