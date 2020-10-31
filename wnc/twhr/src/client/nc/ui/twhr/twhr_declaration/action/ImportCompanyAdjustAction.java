package nc.ui.twhr.twhr_declaration.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.filechooser.FileFilter;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.pub.wa.datainterface.BigExcelReader;
import nc.pub.wa.datainterface.DataItfConst;
import nc.pub.wa.datainterface.DataItfFileReader;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;

/**
 * 二代健保历史数据导入
 * 
 * @author wangywt
 * @since 20190620
 * 
 */
public class ImportCompanyAdjustAction extends HrAction {
	private nc.ui.twhr.glb.view.OrgPanel_Org primaryOrgPanel = null;
	private ShowUpableBillForm billForm;
	private BillListView billListView;
	/**
	 * serial no
	 */
	private static final long serialVersionUID = -6366726775578961640L;

	public ImportCompanyAdjustAction() {
		super();
		super.setCode("ImportCompanyAdjustAction");
		super.putValue(Action.SHORT_DESCRIPTION, "導入調整數據");
		setBtnName("導入調整數據");
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {

		UIFileChooser fc = new UIFileChooser();
		fc.setFileSelectionMode(UIFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				return (f.getName().toLowerCase().endsWith(DataItfConst.SUFFIX_CSV));
			}

			public String getDescription() {
				return ResHelper.getString("6013dataitf_01", "dataitf-01-0002");
			}
		});
		fc.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				return (f.getName().toLowerCase().endsWith(DataItfConst.SUFFIX_XLS));
			}

			public String getDescription() {
				return ResHelper.getString("6013dataitf_01", "dataitf-01-0003");
			}
		});
		fc.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				return (f.getName().toLowerCase().endsWith(DataItfConst.SUFFIX_XLSX));
			}

			public String getDescription() {
				return ResHelper.getString("6013dataitf_01", "dataitf-01-0004");
			}
		});
		if (0 == fc.showOpenDialog(this.getEntranceUI())) {
			File file = fc.getSelectedFile();

			if (file == null) {
				return;
			}
			putValue(HrAction.MESSAGE_AFTER_ACTION, "");
			final String filePath = file.getPath();
			final LoginContext waContext = (LoginContext) getContext();
			final BannerTimerDialog dialog = new BannerTimerDialog(ImportCompanyAdjustAction.this.getEntranceUI());
			new Thread(new Runnable() {
				@Override
				public void run() {
					dialog.setStartText(ResHelper.getString("6013dataitf_01", "dataitf-01-0043")); // 数据导入中,请稍后......
					dialog.start();
					try {
						ImportCompanyAdjustAction.this.importData(filePath, waContext, dialog);
						MessageDialog.showHintDlg(ImportCompanyAdjustAction.this.getEntranceUI(), null,
								ResHelper.getString("6013dataitf_01", "dataitf-01-0044")); // 数据导入成功！
					} catch (Exception e) {
						Logger.error(e);
						MessageDialog.showErrorDlg(ImportCompanyAdjustAction.this.getEntranceUI(), null, e.getMessage());
					} finally {
						dialog.end();
					}
				}
			}).start();
		}

	}

	protected void importData(String filePath, LoginContext waContext, BannerTimerDialog dialog) throws Exception {
		if (StringUtils.isBlank(filePath)) {
			Logger.error("import filepath is bank!");
			// "导入数据文件为空!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0020"));
		} else if (!DataItfFileReader.isExcelFile(filePath)) {
			// "只能导入excel或者csv类型数据文件!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0018"));
		}

		final List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		BigExcelReader reader = new BigExcelReader(filePath) {
			@Override
			protected void outputRow(String[] datas, int[] rowTypes, int rowIndex) {
				if (null != datas && rowIndex > 0) {
					Map<String, Object> dataObj = new HashMap<String, Object>();
					dataObj.put("OrgCode", datas[0]);
					dataObj.put("AdjDate", datas[1]);
					dataObj.put("PsnCode", datas[2]);
					dataObj.put("Amount", datas[4]);
					dataObj.put("Reason", datas[5]);
					dataList.add(dataObj);
				}
			}
		};

		reader.parse();

		if (dataList.size() != 0) {
			for (Map<String, Object> data : dataList) {
				this.getBillForm().getBillCardPanel().getBodyPanel().addLine();
				this.getBillForm()
						.getBillCardPanel()
						.setBodyValueAt(this.getContext().getPk_group(),
								this.getBillForm().getBillCardPanel().getBodyPanel().getTableModel().getRowCount() - 1,
								"pk_group");
				this.getBillForm()
						.getBillCardPanel()
						.setBodyValueAt(this.getModel().getContext().getPk_org(),
								this.getBillForm().getBillCardPanel().getBodyPanel().getTableModel().getRowCount() - 1,
								"pk_org");
				this.getBillForm()
						.getBillCardPanel()
						.setBodyValueAt(new UFLiteralDate((String) data.get("AdjDate")),
								this.getBillForm().getBillCardPanel().getBodyPanel().getTableModel().getRowCount() - 1,
								"adjustdate");
				this.getBillForm()
						.getBillCardPanel()
						.setBodyValueAt(getPk_psndoc((String) data.get("PsnCode")),
								this.getBillForm().getBillCardPanel().getBodyPanel().getTableModel().getRowCount() - 1,
								"pk_psndoc");
				this.getBillForm()
						.getBillCardPanel()
						.setBodyValueAt(new UFDouble((String) data.get("Amount")),
								this.getBillForm().getBillCardPanel().getBodyPanel().getTableModel().getRowCount() - 1,
								"adjustamount");
				this.getBillForm()
						.getBillCardPanel()
						.setBodyValueAt((String) data.get("Reason"),
								this.getBillForm().getBillCardPanel().getBodyPanel().getTableModel().getRowCount() - 1,
								"adjustreason");
			}

			this.getBillForm().getBillCardPanel().getBillModel().loadLoadRelationItemValue();
		} else {
			throw new BusinessException("導入的資料為空。");
		}

	}

	IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);

	private String getPk_psndoc(String psnCode) throws BusinessException {
		String pk_psndoc = (String) query.executeQuery(
				"select pk_psndoc from bd_psndoc where code = '" + psnCode + "'", new ColumnProcessor());

		if (StringUtils.isEmpty(psnCode)) {
			throw new BusinessException("員工 [" + psnCode + "] 檔案無法找到。");
		}
		return pk_psndoc;
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

	public BillListView getBillListView() {
		return billListView;
	}

	public void setBillListView(BillListView billListView) {
		this.billListView = billListView;
	}

}
