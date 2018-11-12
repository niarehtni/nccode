/**
 * @(#)PayDataImportDlg.java 1.0 2018年1月29日
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.ui.wa.datainterface.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang.StringUtils;

import nc.hr.utils.ResHelper;
import nc.pub.wa.datainterface.DataItfConst;
import nc.ui.hr.frame.dialog.HrDialog;
import nc.ui.org.ref.OrgVOsDefaultRefModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.wa.ref.WaClassRefModel;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.pub.WaLoginContext;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings("serial")
public class PayDataImportDlg extends HrDialog {
	private LoginContext context;

	public PayDataImportDlg(LoginContext context) {
		this(context, ResHelper.getString("6013dataitf_01", "dataitf-01-0001")); // 批量期间导入
	}

	public PayDataImportDlg(LoginContext context, String title) {
		super(context.getEntranceUI(), title);
		this.context = context;
		setSize(420, 220);
		initDefaultData();
	}

	private void initDefaultData() {
		if (null != context && context instanceof WaLoginContext) {
			WaLoginContext waContext = (WaLoginContext) context;
			OrgVOsDefaultRefModel refModel = new OrgVOsDefaultRefModel(waContext.getPkorgs());
			refModel.setCacheEnabled(false);
			getOrgRefPane().setRefModel(refModel);
			getOrgRefPane().setPK(waContext.getPk_org());

			getWaClassRefPane().getRefModel().setPk_org(waContext.getPk_org());
			getWaClassRefPane().setPK(waContext.getClassPK());

			getOrgRefPane().setEnabled(false);
			getWaClassRefPane().setEnabled(false);
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
		if (StringUtils.isBlank(filepath)) {
			MessageDialog.showWarningDlg(this, null, ResHelper.getString("6013dataitf_01", "dataitf-01-0011"));
			return;
		}
		super.closeOK();
	}

	@Override
	protected JComponent createCenterPanel() {
		UIPanel contentPanel = new UIPanel();
		FormLayout layout = new FormLayout("right:100,10,200,10,50,10,default", "20,default,5,default,5,default,pref");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout, contentPanel);
		builder.nextLine();
		builder.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0005"));

		builder.append(getFilePathPane());
		builder.append(getPreviewButton());
		builder.nextLine();
		builder.nextLine();

		builder.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0006"));

		builder.append(getUiCbxDataType());
		builder.nextLine();
		builder.nextLine();

		builder.append(ResHelper.getString("6013commonbasic", "06013commonbasic0013"));

		builder.append(getOrgRefPane());
		builder.nextLine();
		builder.nextLine();

		builder.append(ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0185"));

		builder.append(getWaClassRefPane());
		builder.nextLine();
		builder.nextLine();

		return contentPanel;
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
					PayDataImportDlg.this.handleSelectFile();
				}
			});
		}
		return this.previewButton;
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

	public UIRefPane getWaClassRefPane() {
		if (this.waClassRefPane == null) {
			this.waClassRefPane = new UIRefPane();
			this.waClassRefPane.setPreferredSize(new Dimension(200, 20));
			this.waClassRefPane.setButtonFireEvent(true);
			this.waClassRefPane.setRefModel(new WaClassRefModel());
		}
		return this.waClassRefPane;
	}

	public UIRefPane getOrgRefPane() {
		if (this.orgRefPane == null) {
			this.orgRefPane = new UIRefPane();
			this.orgRefPane.setPreferredSize(new Dimension(200, 20));
			this.orgRefPane.setButtonFireEvent(true);
			this.orgRefPane.setRefModel(new OrgVOsDefaultRefModel(new String[0]));
		}
		return this.orgRefPane;
	}

	private UIRefPane filePathPane;
	private UIButton previewButton;
	private UIComboBox uiCbxDataType;
	private UIRefPane orgRefPane;
	private UIRefPane waClassRefPane;
}
