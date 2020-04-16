package nc.ui.overtime.otleavebalance.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import nc.bs.logging.Logger;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener2;
import nc.vo.pub.lang.UFLiteralDate;

public class PsnListViewPanel extends UIDialog implements BillEditListener2, ActionListener, PropertyChangeListener,
		ValueChangedListener {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = 1559930529059842575L;
	private boolean isInit = false;
	private JPanel ivjUIDialogContentPane = null;
	private UIPanel bnPanel = null;
	private UIButton okButton = null;
	private UIButton cancelButton = null;
	private JPanel ivjUIDialogContentPane1;
	private UIPanel ivjUIPanel1 = null;
	private UIRefPane psnRefField = null;// 人員參照
	private UILabel psnRefLable = null;// 人員參照標籤
	private UIRefPane calendarRefField = null;// 日期參照
	private UILabel calendarRefLable = null;// 日期參照標籤

	private String pk_org;
	private UFLiteralDate startDate;
	private String[] selectedPsndocPKs;

	@SuppressWarnings("deprecation")
	public PsnListViewPanel() {
		initialize();
	}

	private void initialize() {
		this.setTitle("重建分段人員");
		this.setLayout(new BorderLayout());
		this.setSize(new Dimension(350, 160));
		this.setContentPane(getUIDialogContentPane());
		isInit = true;
	}

	private Container getUIDialogContentPane() {
		if (ivjUIDialogContentPane == null) {
			try {
				ivjUIDialogContentPane = new javax.swing.JPanel();
				ivjUIDialogContentPane.setName("ivjUIDialogContentPane");
				ivjUIDialogContentPane.setLayout(new java.awt.BorderLayout());
				getUIDialogContentPane().add(getUIDialogContentPane1());
				getUIDialogContentPane().add(getBnPanel(), BorderLayout.SOUTH);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIDialogContentPane;

	}

	private JPanel getUIDialogContentPane1() {
		if (ivjUIDialogContentPane1 == null) {
			ivjUIDialogContentPane1 = new javax.swing.JPanel();
			ivjUIDialogContentPane1.setName("ivjUIDialogContentPane1");
			ivjUIDialogContentPane1.setLayout(null);
			getUIDialogContentPane1().add(getUIPanel1());
		}
		return ivjUIDialogContentPane1;
	}

	private UIPanel getUIPanel1() {
		if (ivjUIPanel1 == null) {
			try {
				ivjUIPanel1 = new UIPanel();
				ivjUIPanel1.setName("ivjUIPanel1");
				ivjUIPanel1.setLayout(null);
				ivjUIPanel1.add(getPsnRefLable());
				ivjUIPanel1.add(getPsnRefField());
				ivjUIPanel1.add(getCalendarRefLable());
				ivjUIPanel1.add(getCalendarRefField());
				ivjUIPanel1.setBounds(10, 10, 320, 60);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIPanel1;
	}

	private UIRefPane getPsnRefField() {
		if (psnRefField == null) {
			psnRefField = new UIRefPane("HR人员(全职树)");
			psnRefField.setPk_org(getPk_org());
			psnRefField.setMultiSelectedEnabled(true);
			psnRefField.setVisible(true);
			psnRefField.setBounds(90, 38, 220, 50);
			psnRefField.setButtonFireEvent(true);
			psnRefField.addPropertyChangeListener(this);
			psnRefField.getRefModel().setMutilLangNameRef(false);
			psnRefField.addValueChangedListener(this);
		}
		return psnRefField;
	}

	private UILabel getPsnRefLable() {
		if (psnRefLable == null) {
			try {
				psnRefLable = new UILabel();
				psnRefLable.setName("psnRefLable");
				psnRefLable.setText("人員列表");
				psnRefLable.setBounds(10, 35, 80, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return psnRefLable;
	}

	public UIRefPane getCalendarRefField() {
		if (calendarRefField == null) {
			calendarRefField = new UIRefPane("日历");
			calendarRefField.setVisible(true);
			calendarRefField.setBounds(90, 8, 220, 50);
			calendarRefField.setButtonFireEvent(true);
			calendarRefField.addPropertyChangeListener(this);
			calendarRefField.addValueChangedListener(this);
		}
		return calendarRefField;
	}

	public UILabel getCalendarRefLable() {
		if (calendarRefLable == null) {
			try {
				calendarRefLable = new UILabel();
				calendarRefLable.setName("calendarRefLable");
				calendarRefLable.setText("起始日期");
				calendarRefLable.setBounds(10, 5, 80, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return calendarRefLable;
	}

	private UIPanel getBnPanel() {
		if (this.bnPanel == null) {
			this.bnPanel = new UIPanel();
			this.bnPanel.setLayout(new FlowLayout());
			this.bnPanel.setPreferredSize(new Dimension(500, 60));
			this.bnPanel.add(getOkButton(), null);
			this.bnPanel.add(getCancelButton(), null);
		}
		return this.bnPanel;
	}

	private UIButton getOkButton() {
		if (this.okButton == null) {
			this.okButton = new UIButton();
			this.okButton.setBounds(new Rectangle(30, 5, 30, 20));
			this.okButton.setText(NCLangRes.getInstance().getStrByID("common", "UC001-0000044"));
			this.okButton.setPreferredSize(new Dimension(70, 22));
			this.okButton.addActionListener(this);
		}
		return this.okButton;
	}

	private UIButton getCancelButton() {
		if (this.cancelButton == null) {
			this.cancelButton = new UIButton();
			this.cancelButton.setBounds(new Rectangle(220, 5, 30, 20));
			this.cancelButton.setText(NCLangRes.getInstance().getStrByID("common", "UC001-0000008"));
			this.cancelButton.setPreferredSize(new Dimension(70, 22));
			this.cancelButton.addActionListener(this);
		}
		return this.cancelButton;
	}

	private void handleException(Throwable exception) {
		Logger.error("--------- 未捕捉到的异常 ---------");
		MessageDialog.showErrorDlg(this, null, exception.getMessage());
		exception.printStackTrace();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(getOkButton())) {
			this.setSelectedPsndocPKs(this.getPsnRefField().getRefPKs());
			this.setStartDate(this.getCalendarRefField().getValueObj() == null ? null : new UFLiteralDate(String
					.valueOf(this.getCalendarRefField().getValueObj())));
			setResult(UIDialog.ID_OK);
			dispose();
		} else if (e.getSource().equals(getCancelButton())) {
			setResult(UIDialog.ID_CANCEL);
			dispose();
		}
	}

	@Override
	public boolean beforeEdit(BillEditEvent arg0) {
		// TODO 自動產生的方法 Stub
		return false;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (isInit) {

		}
	}

	@Override
	public void valueChanged(ValueChangedEvent arg0) {
		if (isInit) {

		}
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String[] getSelectedPsndocPKs() {
		return selectedPsndocPKs;
	}

	public void setSelectedPsndocPKs(String[] selectedPsndocPKs) {
		this.selectedPsndocPKs = selectedPsndocPKs;
	}

	public UFLiteralDate getStartDate() {
		return startDate;
	}

	public void setStartDate(UFLiteralDate startDate) {
		this.startDate = startDate;
	}

}
