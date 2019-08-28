package nc.ui.wa.psndocwadoc.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIRefPane;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class EffectiveDateChooseDialog extends UIDialog implements PropertyChangeListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Container conter;

	private String pk_org;

	private UILabel labelEffectiveDate = null;

	private UIRefPane refEffectiveDate = null;

	private UFDate effectiveDate = new UFDate();

	// 按钮
	JButton okButton;
	JButton cancelButton;

	/**
	 * 构造函数
	 * 
	 * @param conter
	 *            上层父窗口
	 * @param title
	 *            题目
	 * @param desc
	 *            描述
	 * @param t
	 *            每次打开是否刷新窗口?
	 */
	public EffectiveDateChooseDialog(Container conter, String title, String desc, boolean t, String pk_org) {
		// 添加Label和输入文本框
		super(conter, t);
		this.conter = conter;
		this.pk_org = pk_org;
		setTitle(title);
		initPanel();
	}

	private void initPanel() {
		JPanel p1 = new JPanel();
		GridLayout gridLayout = new GridLayout(3, 2);
		// 3行两列
		p1.setLayout(gridLayout);

		p1.add(new UILabel());
		p1.add(new UILabel());

		p1.add(getEffectiveDateLabel(), BorderLayout.CENTER);
		p1.add(getRefEffectiveDateRef(), BorderLayout.CENTER);

		getContentPane().add("Center", p1);

		// 添加确定和取消按钮
		JPanel p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.RIGHT));
		p2.add(getBtnOk());
		p2.add(getCancelButton());
		getContentPane().add("South", p2);

		// 调整对话框布局大小
		pack();
		setSize(250, 100);
		this.setModal(true);

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		try {
			if (getRefEffectiveDateRef().getValueObj() != null) {
				new UFDate(String.valueOf(getRefEffectiveDateRef().getValueObj()));
				getBtnOk().setEnabled(true);
				return;
			}

		} catch (Exception ex) {
			ExceptionUtils.wrappBusinessException(ex.getMessage());
		}
		getBtnOk().setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == getBtnOk()) {
			if (getRefEffectiveDateRef().getValueObj() != null) {
				try {
					setEffectiveDate(new UFDate(String.valueOf(getRefEffectiveDateRef().getValueObj())));
					this.setResult(1);
					this.dispose();
				} catch (Exception e) {
					MessageDialog.showErrorDlg(conter, "錯誤", "數據格式錯誤!");
				}
			} else {
				MessageDialog.showErrorDlg(conter, "錯誤", "生效日期不能為空!");
			}
			return;
		} else if (source == getCancelButton()) {
			this.setResult(0);
			this.dispose();
			return;
		}
		this.dispose();
	}

	public JButton getBtnOk() {
		if (null == okButton) {
			okButton = new JButton("确 定");
			okButton.addActionListener(this);
			okButton.setEnabled(false);
		}
		return okButton;
	}

	public JButton getCancelButton() {
		if (null == cancelButton) {
			cancelButton = new JButton("取 消");
			cancelButton.addActionListener(this);
		}
		return cancelButton;
	}

	private UILabel getEffectiveDateLabel() {
		if (labelEffectiveDate == null) {
			labelEffectiveDate = new UILabel();
			labelEffectiveDate.setBounds(100, 130, 50, 20);
			labelEffectiveDate.setText("           生效日期");
		}
		return labelEffectiveDate;
	}

	public UIRefPane getRefEffectiveDateRef() {
		if (refEffectiveDate == null) {
			refEffectiveDate = new UIRefPane("日期");
			refEffectiveDate.setVisible(true);
			refEffectiveDate.setValue(new UFDate().toStdString());
			refEffectiveDate.setPreferredSize(new Dimension(50, 20));
			refEffectiveDate.setButtonFireEvent(true);
			refEffectiveDate.addPropertyChangeListener(this);
			refEffectiveDate.setBounds(100, 130, 50, 20);
		}
		return refEffectiveDate;
	}

	public UFDate getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(UFDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

}