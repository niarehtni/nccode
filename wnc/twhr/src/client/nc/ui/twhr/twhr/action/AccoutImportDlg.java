/**
 * @(#)PayDataImportDlg.java 1.0 2018年1月29日
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.ui.twhr.twhr.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.filechooser.FileFilter;

import nc.hr.utils.ResHelper;
import nc.pub.wa.datainterface.DataItfConst;
import nc.ui.hr.frame.dialog.HrDialog;
import nc.ui.org.ref.LPOrgDefaultRefModel;
import nc.ui.org.ref.OrgVOsDefaultRefModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.twhr.twhr.view.PeriodRefModel;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings("serial")
public class AccoutImportDlg extends HrDialog {
	private LoginContext context;

	public AccoutImportDlg(LoginContext context) {
		this(context, ResHelper.getString("6013dataitf_01", "dataitf-01-0001")); // 批量期间导入
	}

	public AccoutImportDlg(LoginContext context, String title) {
		super(context.getEntranceUI(), title);
		this.context = context;
		setSize(420, 220);
		initDefaultData();
	}

	private void initDefaultData() {
		if (null != context && context instanceof LoginContext) {
			LoginContext waContext = (LoginContext) context;
			OrgVOsDefaultRefModel refModel = new OrgVOsDefaultRefModel(waContext.getPkorgs());
			refModel.setCacheEnabled(false);
		}
	}

	public void handleSelectFile() {
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
		// .txt文件
		fc.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				return (f.getName().toLowerCase().endsWith(DataItfConst.SUFFIX_TXT));
			}

			public String getDescription() {
				return ResHelper.getString("6013dataitf_01", "dataitf-01-0047");
			}
		});
		if (0 == fc.showOpenDialog(this)) {
			File file = fc.getSelectedFile();

			if (file == null) {
				return;
			}
			String path = file.getPath();
			getFilePathPane().getUITextField().setText(path);
		}
	}

	@Override
	public void closeOK() {
		String filepath = getFilePathPane().getUITextField().getText();
		String pk_legal_org = getLegalOrgPanl().getUITextField().getText();
		String waperiod = getPeriodPanel().getUITextField().getText();
		if (StringUtils.isBlank(pk_legal_org)) {
			MessageDialog.showWarningDlg(this, null, "法人組織不能為空!");
			return;
		}
		if (StringUtils.isBlank(waperiod)) {
			MessageDialog.showWarningDlg(this, null, "期間不能為空!");
			return;
		}
		if (StringUtils.isBlank(filepath)) {
			MessageDialog.showWarningDlg(this, null, ResHelper.getString("6013dataitf_01", "dataitf-01-0011"));
			return;
		}

		super.closeOK();
	}

	@Override
	protected JComponent createCenterPanel() {
		UIPanel contentPanel = new UIPanel();
		FormLayout layout = new FormLayout("right:100,10,200,10,50,10,default", "20,default,40,default,40,default,pref");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout, contentPanel);
		builder.nextLine();
		builder.append("法人組織");
		builder.add(getLegalOrgPanl());

		builder.nextLine();
		builder.append("期間");
		builder.add(getPeriodPanel());

		builder.nextLine();
		builder.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0005"));

		builder.append(getFilePathPane());
		builder.append(getPreviewButton());
		builder.nextLine();
		builder.nextLine();

		return contentPanel;
	}

	@SuppressWarnings("unchecked")
	public UIComboBox getUiCbxDataType() {
		if (this.uiCbxDataType == null) {
			this.uiCbxDataType = new UIComboBox();
			this.uiCbxDataType.addItem(new DefaultConstEnum(Integer.valueOf(DataItfConst.VALUE_SALARY_DETAIL),
					DataItfConst.DESC_SALARY_DETAIL));
			this.uiCbxDataType.addItem(new DefaultConstEnum(Integer.valueOf(DataItfConst.VALUE_SALARY_OTHERDEC),
					DataItfConst.DESC_SALARY_OTHERDEC));
			this.uiCbxDataType.addItem(new DefaultConstEnum(Integer.valueOf(DataItfConst.VALUE_BONUS_DETAIL),
					DataItfConst.DESC_BONUS_DETAIL));
			this.uiCbxDataType.addItem(new DefaultConstEnum(Integer.valueOf(DataItfConst.VALUE_BONUS_OTHERDEC),
					DataItfConst.DESC_BONUS_OTHERDEC));
			this.uiCbxDataType.setSelectedIndex(0);
		}
		return this.uiCbxDataType;
	}

	public UIRefPane getFilePathPane() {
		if (this.filePathPane == null) {
			this.filePathPane = new UIRefPane();
			this.filePathPane.setName("filePathPane");
			this.filePathPane.setButtonVisible(false);
			this.filePathPane.getUITextField().setShowMustInputHint(true);
		}
		return this.filePathPane;
	}

	public UIButton getPreviewButton() {
		if (this.previewButton == null) {
			this.previewButton = new UIButton();
			this.previewButton.setName("previewButton");
			this.previewButton.setText(ResHelper.getString("common", "UC001-0000021"));

			this.previewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AccoutImportDlg.this.handleSelectFile();
				}
			});
		}
		return this.previewButton;
	}

	public UIRefPane getLegalOrgPanl() {
		if (this.legalOrgPanl == null) {
			legalOrgPanl = new UIRefPane();
			legalOrgPanl.setVisible(true);
			legalOrgPanl.setPreferredSize(new Dimension(200, 20));

			legalOrgPanl.setButtonFireEvent(true);
			LPOrgDefaultRefModel refmodel = new LPOrgDefaultRefModel();
			legalOrgPanl.setRefModel(refmodel);
			legalOrgPanl.setNotLeafSelectedEnabled(false);
			legalOrgPanl.getUITextField().setShowMustInputHint(true);
		}
		return this.legalOrgPanl;
	}

	public UIRefPane getPeriodPanel() {
		if (this.periodPanel == null) {
			periodPanel = new UIRefPane();
			periodPanel.setVisible(true);
			periodPanel.setPreferredSize(new Dimension(200, 20));

			periodPanel.setButtonFireEvent(true);
			PeriodRefModel refmodel = new PeriodRefModel();
			periodPanel.setRefModel(refmodel);
			periodPanel.setNotLeafSelectedEnabled(false);
			periodPanel.getUITextField().setShowMustInputHint(true);
		}
		return this.periodPanel;
	}

	private UIRefPane periodPanel;
	private UIRefPane legalOrgPanl;
	private UIRefPane filePathPane;
	private UIButton previewButton;
	private UIComboBox uiCbxDataType;
}
