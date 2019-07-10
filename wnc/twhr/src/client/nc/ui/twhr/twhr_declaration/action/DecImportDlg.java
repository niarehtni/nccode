package nc.ui.twhr.twhr_declaration.action;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.filechooser.FileFilter;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import nc.hr.utils.ResHelper;
import nc.pub.wa.datainterface.DataItfConst;
import nc.ui.hr.frame.dialog.HrDialog;
import nc.ui.org.ref.OrgVOsDefaultRefModel;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.wa.datainterface.view.PayDataImportDlg;
import nc.ui.wa.ref.WaClassRefModel;
import nc.vo.uif2.LoginContext;

/**
 * 二代健保历史数据导入
 * @author wangywt 
 * @since 20190620
 *
 */
public class DecImportDlg extends HrDialog {
	private UIRefPane filePathPane;
	private UIButton previewButton;
	private LoginContext context;
	private UIComboBox uiCbxDataType;
	public DecImportDlg(LoginContext context) {
		this(context, "二代健保数据导入"); 
	}

	public DecImportDlg(LoginContext context, String title) {
		super(context.getEntranceUI(), title);
		this.context = context;
		setSize(420, 180);
		initDefaultData();
	}

	private void initDefaultData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected JComponent createCenterPanel() {
		UIPanel contentPanel = new UIPanel();
		FormLayout layout = new FormLayout("right:100,10,200,10,50,10,default", "20,default,5,default,5,default,pref");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout, contentPanel);
		builder.nextLine();
		builder.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0005"));//数据文件
		builder.append(getFilePathPane());
		builder.append(getPreviewButton());
		builder.nextLine();
		builder.nextLine();
//		builder.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0006"));
		
//		builder.append(getUiCbxDataType());
	
		return contentPanel;
	}
	
/*	public UIComboBox getUiCbxDataType() {
		if (this.uiCbxDataType == null) {
			this.uiCbxDataType = new UIComboBox();
			this.uiCbxDataType.addItem(new DefaultConstEnum(Integer.valueOf(DataDecConst.PREMIUM_NOTPARTJOB_PERSON),
					DataDecConst.DESC_NOTPARTJOB_PERSON));
			this.uiCbxDataType.addItem(new DefaultConstEnum(Integer.valueOf(DataDecConst.PREMIUM_PARTJOB_PERSON),
					DataDecConst.DESC_PARTJOB_PERSON));
			this.uiCbxDataType.addItem(new DefaultConstEnum(Integer.valueOf(DataDecConst.PREMIUM_BUSINESS_INCOME),
					DataDecConst.DESC_BUSINESS_INCOME));
			this.uiCbxDataType.addItem(new DefaultConstEnum(Integer.valueOf(DataDecConst.PREMIUM_COMPANY_ADD),
					DataDecConst.DESC_COMPANY_ADD));
			this.uiCbxDataType.setSelectedIndex(0);
		}
		return this.uiCbxDataType;
	}
*/
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
			this.previewButton.setText(ResHelper.getString("common", "UC001-0000021"));//浏览

			this.previewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DecImportDlg.this.handleSelectFile();
				}
			});
		}
		return this.previewButton;
	}
	protected void handleSelectFile() {
		UIFileChooser fc = new UIFileChooser();
		fc.setFileSelectionMode(UIFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				return (f.getName().toLowerCase().endsWith(DataItfConst.SUFFIX_XLS));
			}

			public String getDescription() {
				return ResHelper.getString("6013dataitf_01", "dataitf-01-0003");//Excel文件(.xls)
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
				return ResHelper.getString("6013dataitf_01", "dataitf-01-0004");//Excel文件(.xlsx)
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
}
