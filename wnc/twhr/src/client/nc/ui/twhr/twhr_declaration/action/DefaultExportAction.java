package nc.ui.twhr.twhr_declaration.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.twhr.IDeclarationExportService;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hr.uif2.view.PrimaryOrgPanel;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.pubapp.uif2app.query2.model.IModelDataManager;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.wa.datainterface.TextFileFilter4TW;

import org.apache.commons.io.FileUtils;

public class DefaultExportAction extends HrAction {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = 5099334079859001851L;
	private IDeclarationExportService service = null;
	private IModelDataManager dataManager;
	private ShowUpableBillForm billForm;
	private ShowTaxDig showTaxDig;
	private nc.ui.twhr.glb.view.OrgPanel_Org primaryOrgPanel = null;

	public DefaultExportAction() {
		super();
		super.setCode("DefaultExportAction");
		super.putValue(Action.SHORT_DESCRIPTION, "二代健保申报");
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		if (showTaxDig == null) {
			showTaxDig = new ShowTaxDig();
			showTaxDig.setTitle(ResHelper.getString("secondtax", "268861025-0000011")/* "二代健保申报" */);
		}
		if (showTaxDig.showModal() == 1) {
			new Thread() {
				@Override
				public void run() {

					IProgressMonitor progressMonitor = NCProgresses.createDialogProgressMonitor(getModel().getContext()
							.getEntranceUI());

					try {
						progressMonitor.beginTask("匯出中...", IProgressMonitor.UNKNOWN_REMAIN_TIME);
						progressMonitor.setProcessInfo("匯出中，請稍候.....");

						// 做导出操作
						JComponent parentUi = getModel().getContext().getEntranceUI();
						String pk_org = getPrimaryOrgPanel().getRefPane().getRefPK();
						String pk_group = getModel().getContext().getPk_group();

						List<Map<String, String[]>> textArr = getService().getIITXTextReport(pk_group, pk_org,
								showTaxDig.getStartdate(), showTaxDig.getEnddate(), showTaxDig.getContactName(),
								showTaxDig.getContactEmail(), showTaxDig.getContactTel(),showTaxDig.getHandle());

						if (textArr != null) {
							UIFileChooser fileChooser = new UIFileChooser();
							fileChooser.setDialogTitle("請指定要匯出的文檔名稱");

							TextFileFilter4TW filter = new TextFileFilter4TW();
							filter.setFilterString("*.txt");
							filter.setDescription("生成二代健保申報檔");
							fileChooser.addChoosableFileFilter(filter);
							fileChooser.setFileSelectionMode(fileChooser.DIRECTORIES_ONLY);
							fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory().getAbsolutePath()));
							int userSelection = fileChooser.showSaveDialog(parentUi);

							String filename = "";
							File fileToSave = null;
							if (userSelection == JFileChooser.APPROVE_OPTION) {
								for (Map<String, String[]> text : textArr) {
									for (String txt : text.keySet()) {
										/*
										 * fileChooser.setSelectedFile(new
										 * File(fileChooser
										 * .getCurrentDirectory(
										 * ).getAbsolutePath() +
										 * "\\"+txt+".txt"));
										 */
										if (null != txt) {
											if (!fileChooser.getSelectedFile().getAbsoluteFile().toString()
													.toUpperCase().endsWith(".txt")) {
												filename = fileChooser.getSelectedFile().getAbsolutePath() + "\\" + txt
														+ ".txt";

											} else {
												filename = fileChooser.getSelectedFile().getAbsolutePath();
											}
										}

										fileToSave = new File(filename);
										// 暫不刷新畫面
										// this.getDataManager().refresh();
										StringBuilder sb = new StringBuilder();
										for (String str : text.get(txt)) {
											sb.append(str + "\r\n");
										}

										if (fileToSave != null) {
											// 讀取報表文本
											FileUtils.writeStringToFile(fileToSave, sb.toString(), "Big5");
										}
									}
								}

							} else {
								// this.putValue("message_after_action",
								// "生成申報檔作業已取消。");
								return;
							}

							// 暫不回寫注記標記
							// this.getService().writeBackFlags(dataPKs.toArray(new
							// String[0]));

							// this.putValue("message_after_action", "");
						} else {
							// this.putValue("message_after_action",
							// "指定的條件沒找到可生成的申報資料。");
						}
					} catch (Exception e) {
						e.printStackTrace();
						ShowStatusBarMsgUtil.showErrorMsg(
								ResHelper.getString("incometax", "2incometax-n-000004")/* "提示" */, e.getMessage(),
								getModel().getContext());
					} finally {
						progressMonitor.done(); // 进度任务结束
					}
				}
			}.start();
		}
	}

	public ShowTaxDig getShowTaxDig() {
		return showTaxDig;
	}

	public void setShowTaxDig(ShowTaxDig showTaxDig) {
		this.showTaxDig = showTaxDig;
	}

	private boolean isSame(String value1, String value2) {
		if ((value1 == null && value2 == null) || (value1 != null && value1.equals(value2))
				|| (value2 != null && value2.equals(value1))) {
			return true;
		}

		return false;
	}

	private IDeclarationExportService getService() {
		if (service == null) {
			service = (IDeclarationExportService) NCLocator.getInstance().lookup(IDeclarationExportService.class);
		}
		return service;
	}

	public IModelDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IModelDataManager dataManager) {
		this.dataManager = dataManager;
	}

	public ShowUpableBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(ShowUpableBillForm billForm) {
		this.billForm = billForm;
	}

	public nc.ui.twhr.glb.view.OrgPanel_Org getPrimaryOrgPanel() {
		return primaryOrgPanel;
	}

	public void setPrimaryOrgPanel(nc.ui.twhr.glb.view.OrgPanel_Org primaryOrgPanel) {
		this.primaryOrgPanel = primaryOrgPanel;
	}

	@Override
	protected boolean isActionEnable() {
		if (null == getModel().getSelectedData()) {
			return false;
		} else {
			return true;
		}
	}

}
