/**
 * @(#)PocFileDialog.java 1.0 2013-9-28
 *
 * Copyright (c) 2013, YONYOU. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.ui.hrwa.projsalary.ace.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITextField;

/**
 * 
 * @author xinyu.chen
 * @since 6.1
 */
@SuppressWarnings({ "restriction", "serial" })
public class FileImportDialog extends UIDialog implements ActionListener {

	private UIButton btnOk = null;
	private UIButton btnCancel = null;

	private UIButton btnFile = null;
	private UITextField tfFilePath = null;

	private UIFileChooser fileChooser = null;

	@SuppressWarnings("deprecation")
	public FileImportDialog() {
		// 导入文件选择
		setTitle(ResHelper.getString("projsalary", "0pjsalary-00007"));
		initUI();

		btnOk.addActionListener(this);
		btnCancel.addActionListener(this);
		btnFile.addActionListener(this);

		fileChooser = new UIFileChooser();
		fileChooser.setFileSelectionMode(UIFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(Boolean.FALSE);
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

	private void initUI() {
		getContentPane().setLayout(new BorderLayout());
		UIPanel pnlCenter = new UIPanel();
		UIPanel pnlSouth = new UIPanel();
		pnlSouth.setPreferredSize(new Dimension(10, 36));
		pnlSouth.setBorder(new EtchedBorder());
		// 文件路径
		UILabel lbFilePath = new UILabel(ResHelper.getString("projsalary", "0pjsalary-00008"));
		lbFilePath.setHorizontalAlignment(SwingConstants.RIGHT);
		tfFilePath = new UITextField();
		tfFilePath.setPreferredSize(new Dimension(250, 22));
		btnFile = new UIButton("...");
		btnFile.setPreferredSize(new Dimension(40, 22));

		pnlCenter.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 5, 10);
		pnlCenter.add(lbFilePath, c);
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 0);
		pnlCenter.add(tfFilePath, c);
		c.gridx = 2;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 0);
		pnlCenter.add(btnFile, c);

		btnOk = new UIButton();
		btnOk.setPreferredSize(new Dimension(80, 30));
		btnOk.setText(ResHelper.getString("common", "UC001-0000044"));

		btnCancel = new UIButton();
		btnCancel.setPreferredSize(new Dimension(80, 30));
		btnCancel.setText(ResHelper.getString("common", "UC001-0000008"));

		pnlSouth.add(btnOk);
		pnlSouth.add(btnCancel);

		getContentPane().add(pnlCenter, BorderLayout.CENTER);
		getContentPane().add(pnlSouth, BorderLayout.SOUTH);

		setSize(400, 100);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == btnOk) {
			closeOK();
		} else if (event.getSource() == btnCancel) {
			closeCancel();
		} else if (event.getSource() == btnFile) {
			if (fileChooser.showOpenDialog(this) == UIFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if (null != file) {
					tfFilePath.setText(file.getPath());
				}
			}
		}
	}

	public File getFilePath() {
		if (getResult() == ID_OK && tfFilePath.getText() != null) {
			File file = new File(tfFilePath.getText());
			if (file.exists())
				return file;
		}
		return null;
	}
	
}
