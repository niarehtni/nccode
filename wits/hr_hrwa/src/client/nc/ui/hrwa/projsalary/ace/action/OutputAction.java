/**
 * @(#)OutputAction.java 1.0 2017年9月13日
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.ui.hrwa.projsalary.ace.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import nc.hr.utils.ResHelper;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hrwa.projsalary.ace.model.ProjSalaryModel;
import nc.ui.hrwa.pub.util.ExportDataUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.bill.BillModel;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.uif2.IShowMsgConstant;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVOHelper;
import nc.vo.wa.pub.WaState;
import nc.vo.wa.pub.util.FileReader;

import org.apache.commons.lang.StringUtils;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings({ "restriction", "serial" })
public class OutputAction extends HrAction {
	public UIFileChooser fileChooser;
	public String strFileName;
	private BillListView listView = null;

	public OutputAction() {
		setCode("export");
		setBtnName(ResHelper.getString("projsalary", "0pjsalary-00005")); // 导出
	}

	public BillListView getListView() {
		return listView;
	}

	public void setListView(BillListView listView) {
		this.listView = listView;
	}

	@Override
	public void doAction(ActionEvent paramActionEvent) throws Exception {
		if (((BillManageModel) getModel()).getSelectedOperaDatas() == null) {
			// ExceptionUtils.wrappBusinessException(ResHelper.getString("projsalary",
			// "0pjsalary-00024")
			// /* @res "请选择要导出的数据！" */);
			// return;
			// 无数据或者没有选择数据，确认导出空模板吗？
			if (MessageDialog.ID_YES != MessageDialog.showYesNoDlg(getEntranceUI(), null,
					ResHelper.getString("projsalary", "0pjsalary-00033"))) {
				putValue(MESSAGE_AFTER_ACTION, IShowMsgConstant.getCancelInfo());
				return;
			}
		}

		fileChooser = getFileChooser();
		int returnVal = fileChooser.showSaveDialog(getEntranceUI());
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			putValue(MESSAGE_AFTER_ACTION, IShowMsgConstant.getCancelInfo());
			return;
		}
		strFileName = fileChooser.getSelectedFile().getPath();
		if (StringUtils.isBlank(strFileName)) {
			ExceptionUtils.wrappBusinessException(ResHelper.getString("projsalary", "0pjsalary-00025")
			/* @res "输出文件不能为空！" */);
			return;
		}
		FileFilter fileFilter = fileChooser.getFileFilter();

		if (fileFilter != null) {
			if (fileFilter.getDescription().contains(".xlsx") && !strFileName.toUpperCase().endsWith("XLSX")) {
				strFileName = strFileName + ".xlsx";
			} else if (fileFilter.getDescription().contains(".xls") && !strFileName.toUpperCase().endsWith("XLS")) {
				strFileName = strFileName + ".xls";
			} else if (fileFilter.getDescription().contains(".txt") && !strFileName.toUpperCase().endsWith("TXT")) {
				strFileName = strFileName + ".txt";
			}
		}
		if (!strFileName.toUpperCase().endsWith("TXT") && !strFileName.toUpperCase().endsWith("XLSX")
				&& !strFileName.toUpperCase().endsWith("XLS")) {
			strFileName = strFileName + ".xls";
		}
		File file = new File(strFileName);
		if (file.exists()) {
			if (MessageDialog.ID_YES != MessageDialog.showYesNoDlg(getEntranceUI(), null,
					ResHelper.getString("projsalary", "0pjsalary-00026")
			/* @res "要导出的文件已存在或导出文件的路径、名称非法，继续执行吗?" */)) {
				putValue(MESSAGE_AFTER_ACTION, IShowMsgConstant.getCancelInfo());
				return;
			}
		}
		// 以下进行业务操作
		doExport();
	}

	public void doExport() throws Exception {
		ExportDataUtil exportUtil = new ExportDataUtil();
		GeneralVO[] exportDataVOs = getExportValues();
		exportUtil.setFieldNames(FileReader.getFieldNames());
		exportUtil.setFieldDisplayNames(FileReader.getFieldDisplayNames());
		exportUtil.exportToFile(strFileName, exportDataVOs);

	}

	public GeneralVO[] getExportValues() {
		// WaLoginContext waContext = (WaLoginContext) getModel().getContext();
		// StringBuilder whereCondition = new StringBuilder();
		// whereCondition.append(" pk_org='").append(waContext.getPk_org()).append("' ");
		// whereCondition.append(" and pk_wa_class='").append(waContext.getClassPK()).append("' ");
		// whereCondition.append(" and cyear='").append(waContext.getCyear()).append("' ");
		// whereCondition.append(" and cperiod='").append(waContext.getCperiod()).append("' ");
		// IProjsalaryMaintain service =
		// NCLocator.getInstance().lookup(IProjsalaryMaintain.class);
		// Map<String, DefdocVO> projVOMap = new HashMap<String, DefdocVO>();
		// Map<String, WaClassItemVO> itemVOMap = new HashMap<String,
		// WaClassItemVO>();
		// try {
		// projVOMap = service.qryProjectMap(null, new String[] { "pk_defdoc"
		// });
		// itemVOMap = service.qryClassItemByPeriod(whereCondition.toString(),
		// new String[] { "pk_wa_classitem" });
		//
		// } catch (BusinessException e) {
		// ExceptionUtils.wrappException(e);
		// }
		BillModel billModel = getListView().getBillListPanel().getHeadBillModel();
		Integer[] selRows = ((BillManageModel) getModel()).getSelectedOperaRows();
		ArrayList<GeneralVO> exportDataVOs = new ArrayList<GeneralVO>();
		for (int i = 0; i < selRows.length; i++) {
			GeneralVO vo = new GeneralVO();
			vo.setAttributeValue("psncode", billModel.getValueAt(selRows[i], "pk_psndoc.code"));
			// String pjname = (String) billModel.getValueAt(selRows[i],
			// "def1");
			String pjcode = (String) billModel.getValueAt(selRows[i], "def2");
			String pk_classitem = (String) billModel.getValueAt(selRows[i], "pk_classitem");
			// String pjcode = null;
			// String itemname = null;
			// if (null != projVOMap.get(pk_project)) {
			// pjcode = projVOMap.get(pk_project).getCode();
			// }
			// if (null != itemVOMap.get(pk_classitem)) {
			// itemname = itemVOMap.get(pk_classitem).getName2();
			// }
			vo.setAttributeValue("pjcode", pjcode);
			// vo.setAttributeValue("pjname", pjname);
			vo.setAttributeValue("itemname", pk_classitem);
			vo.setAttributeValue("itemamt", billModel.getValueAt(selRows[i], "salaryamt"));
			exportDataVOs.add(vo);
		}
		return exportDataVOs.toArray(new GeneralVO[0]);
	}

	public UIFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new UIFileChooser();
			fileChooser.setFileFilter(new FileFilter() {
				String ext = ".xls";

				@Override
				public boolean accept(File f) {
					if (f.isDirectory()) {
						return true;
					}
					if (((f.getName().toLowerCase()).endsWith(ext))) {
						return true;
					} else {
						return false;
					}
				}

				@Override
				public String getDescription() {
					return ResHelper.getString("6029collec", "06029collec0028")
					/* @res "EXCEL文件(*.xls)" */;
				}

			});
			fileChooser.setFileFilter(new FileFilter() {
				String ext = ".xlsx";

				@Override
				public boolean accept(File f) {
					if (f.isDirectory()) {
						return true;
					}
					if (((f.getName().toLowerCase()).endsWith(ext))) {
						return true;
					} else {
						return false;
					}
				}

				@Override
				public String getDescription() {
					return ResHelper.getString("6029collec", "06029collec0063")
					/* @res "EXCEL文件(*.xlsx)" */;
				}

			});
		}
		return fileChooser;
	}

	@Override
	protected boolean isActionEnable() {
		((ProjSalaryModel) getModel()).setCurrWaStatus(null);
		WaLoginContext waLoginContext = (WaLoginContext) getContext();
		if (!waLoginContext.isContextNotNull()) {
			return false;
		}
		if (null != waLoginContext.getWaState() && null != getEnableStateSet()
				&& getEnableStateSet().contains(waLoginContext.getWaState())) {
			return false;
		}
		if ((null != waLoginContext.getWaLoginVO().getBatch())
				&& (waLoginContext.getWaLoginVO().getBatch().intValue() > 100)) {
			return false;
		}
		if (WaLoginVOHelper.isMultiClass(waLoginContext.getWaLoginVO())) {
			return false;
		}
		// if (((ProjSalaryModel) getModel()).isPayDataApproved()) {
		// return false;
		// }
		return super.isActionEnable();
	}

	public Set<WaState> getEnableStateSet() {
		if (waStateSet == null) {
			waStateSet = new HashSet<WaState>();
			waStateSet.add(WaState.CLASS_IS_APPROVED);
			waStateSet.add(WaState.CLASS_WITHOUT_PAY);

			waStateSet.add(WaState.CLASS_PART_CHECKED);
			waStateSet.add(WaState.CLASS_CHECKED_WITHOUT_PAY);

			waStateSet.add(WaState.CLASS_ALL_PAY);

			waStateSet.add(WaState.CLASS_MONTH_END);
		}
		return waStateSet;
	}

	protected Set<WaState> waStateSet = null;

}
