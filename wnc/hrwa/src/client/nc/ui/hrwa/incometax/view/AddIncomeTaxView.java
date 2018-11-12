
package nc.ui.hrwa.incometax.view;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import nc.bs.logging.Logger;
import nc.bs.wa.util.LocalizationSysinitUtil;
import nc.hr.utils.ResHelper;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.wa.ref.WaPeriodRefTreeModel;
import nc.vo.pub.BusinessException;

/**
 * 
 * @author ward.wong
 * @date 20180126
 * @version v1.0
 * @date 20180227 �����P춸�ί�����{�θ�ʽ���^�V�l��
 * @�������� �걨��ϸ���������ɲ������ý���
 * 
 */
public class AddIncomeTaxView extends UIDialog implements ActionListener, ValueChangedListener, ItemListener {

	public AddIncomeTaxView() {
		super();
		initialize();
	}

	public AddIncomeTaxView(Container arg1, String arg2) {
		super(arg1, arg2);
	}

	public AddIncomeTaxView(Frame arg1) {
		super(arg1);
	}

	public AddIncomeTaxView(Frame arg1, String arg2) {
		super(arg1, arg2);
	}

	public AddIncomeTaxView(Container arg1) {
		super(arg1);
		initialize();
	}

	private void initialize() {
		try {
			setName("AddIncomeTaxView");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(400, 350);
			setContentPane(getUIDialogContentPane());
		} catch (Throwable ivjExc) {
			handleException(ivjExc);
		}

	}

	private JPanel getUIDialogContentPane() {
		if (ivjUIDialogContentPane == null) {
			try {
				ivjUIDialogContentPane = new javax.swing.JPanel();
				ivjUIDialogContentPane.setName("UIDialogContentPane");
				ivjUIDialogContentPane.setLayout(new java.awt.BorderLayout());
				getUIDialogContentPane().add(getUIPanel1(), "Center");
				getUIDialogContentPane().add(getUIPanel2(), "South");
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIDialogContentPane;
	}

	private UIPanel getUIPanel1() {
		if (ivjUIPanel1 == null) {
			try {
				ivjUIPanel1 = new UIPanel();
				ivjUIPanel1.setName("UIPanel1");
				ivjUIPanel1.setLayout(null);
				// �Ƿ��⼮Ա�������걨
				ivjUIPanel1.add(getisForeignMonthDec());
				// ͳһ���
				ivjUIPanel1.add(getUnifiednumberLabel());
				ivjUIPanel1.add(getUnifiednumberRefPane());
				// �걨��ʽ
				ivjUIPanel1.add(getDeclaretypeLabel());
				ivjUIPanel1.add(getDeclaretypeRefPane());
				// н�ʷ���
				ivjUIPanel1.add(getWaclassLabel());
				ivjUIPanel1.add(getWaclassRefPane());
				// н�����
				ivjUIPanel1.add(getWayearLabel());
				ivjUIPanel1.add(getWayearRefPane());
				// ��ʼʱ��
				ivjUIPanel1.add(getWabeginmonthLabel());
				ivjUIPanel1.add(getWabeginmonthRefPane());
				// ����ʱ��
				ivjUIPanel1.add(getWaendmonthLabel());
				ivjUIPanel1.add(getWaendmonthRefPane());

			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIPanel1;
	}

	private nc.ui.pub.beans.UIPanel getUIPanel2() {
		if (ivjUIPanel2 == null) {
			try {
				ivjUIPanel2 = new nc.ui.pub.beans.UIPanel();
				ivjUIPanel2.setName("UIPanel2");
				ivjUIPanel2.setBackground(new java.awt.Color(204, 204, 204));
				getUIPanel2().add(getBtnOK(), getBtnOK().getName());
				getUIPanel2().add(getBtnCancel(), getBtnCancel().getName());
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIPanel2;
	}

	private JCheckBox getisForeignMonthDec() {
		if (isForeignMonthDec == null) {
			isForeignMonthDec = new JCheckBox(
					ResHelper.getString("incometax", "2incometax-n-000013")/* "�Ƿ��⼮Ա�������걨" */);
			isForeignMonthDec.setBounds(200, 10, 200, 30);
			isForeignMonthDec.addItemListener(this);
		}
		return isForeignMonthDec;
	}

	private UILabel getUnifiednumberLabel() {
		if (unifiednumberLabel == null) {
			try {
				unifiednumberLabel = new UILabel();
				unifiednumberLabel.setName("unifiednumberLabel");
				unifiednumberLabel.setText(ResHelper.getString("incometax", "2incometax-n-000014")/* "ͳһ���" */);
				unifiednumberLabel.setBounds(0, 50, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return unifiednumberLabel;
	}

	private UILabel getDeclaretypeLabel() {
		if (declaretypeLabel == null) {
			try {
				declaretypeLabel = new UILabel();
				declaretypeLabel.setName("declaretypeLabel");
				declaretypeLabel.setText(ResHelper.getString("incometax", "2incometax-n-000015")/* "�걨ƾ����ʽ" */);
				declaretypeLabel.setBounds(0, 90, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return declaretypeLabel;
	}

	private UILabel getWaclassLabel() {
		if (waclassLabel == null) {
			try {
				waclassLabel = new UILabel();
				waclassLabel.setName("waclassLabel");
				waclassLabel.setText(ResHelper.getString("incometax", "2incometax-n-000016")/* "н�ʷ���" */);
				waclassLabel.setBounds(0, 130, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return waclassLabel;
	}

	private UILabel getWayearLabel() {
		if (wayearLabel == null) {
			try {
				wayearLabel = new UILabel();
				wayearLabel.setName("wayearLabel");
				wayearLabel.setText(ResHelper.getString("incometax", "2incometax-n-000017")/* "н�����" */);
				wayearLabel.setBounds(0, 170, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return wayearLabel;
	}

	private UILabel getWabeginmonthLabel() {
		if (wabeginmonthLabel == null) {
			try {
				wabeginmonthLabel = new UILabel();
				wabeginmonthLabel.setName("wabeginmonthLabel");
				wabeginmonthLabel.setText(ResHelper.getString("incometax", "2incometax-n-000018")/* "��ʼʱ��" */);
				wabeginmonthLabel.setBounds(0, 210, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return wabeginmonthLabel;
	}

	private UILabel getWaendmonthLabel() {
		if (waendmonthLabel == null) {
			try {
				waendmonthLabel = new UILabel();
				waendmonthLabel.setName("waendmonthLabel");
				waendmonthLabel.setText(ResHelper.getString("incometax", "2incometax-n-000019")/* "����ʱ��" */);
				waendmonthLabel.setBounds(0, 250, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return waendmonthLabel;
	}

	/**
	 * ͳһ���
	 * 
	 * @return
	 */
	private UIRefPane getUnifiednumberRefPane() {
		if (unifiednumberRefPane == null) {
			try {
				unifiednumberRefPane = new UIRefPane();
				unifiednumberRefPane.setName("unifiednumberRefPane");
				unifiednumberRefPane.setRefNodeName("�yһ��̖(�Զ��嵵��)");
				unifiednumberRefPane.setBounds(200, 50, 150, 50);
				unifiednumberRefPane.addValueChangedListener(this);
				unifiednumberRefPane.setButtonFireEvent(true);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return unifiednumberRefPane;
	}

	/**
	 * �걨��ʽ
	 * 
	 * @return
	 */
	private UIRefPane getDeclaretypeRefPane() {
		if (declaretypeRefPane == null) {
			try {
				declaretypeRefPane = new UIRefPane();
				declaretypeRefPane.setName("declaretypeRefPane");
				declaretypeRefPane.setRefNodeName("����ʽ(�Զ��嵵��)");
				declaretypeRefPane.addValueChangedListener(this);
				declaretypeRefPane.setBounds(200, 90, 150, 50);
				declaretypeRefPane.setMultiSelectedEnabled(true);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return declaretypeRefPane;
	}

	/**
	 * н�ʷ���
	 * 
	 * @return
	 */
	private UIRefPane getWaclassRefPane() {
		if (this.waClassRefPane == null) {
			this.waClassRefPane = new UIRefPane();
			this.waClassRefPane.setVisible(this.isShow);
			waClassRefPane.setBounds(200, 130, 150, 50);
			waClassRefPane.addValueChangedListener(this);
			waClassRefPane.setButtonFireEvent(true);
			WaClassRefModel refmodel = getClassRefModel();
			this.waClassRefPane.setRefModel(refmodel);
			this.waClassRefPane.setMultiSelectedEnabled(true);
		}
		return this.waClassRefPane;
	}

	public WaClassRefModel getClassRefModel() {
		if (this.classRefModel == null) {
			this.classRefModel = new nc.ui.hrwa.incometax.view.WaClassRefModel();
		}

		return this.classRefModel;
	}

	/**
	 * н�����
	 * 
	 * @return
	 */
	private UIRefPane getWayearRefPane() {
		if (this.wayearRefPane == null) {
			try {
				wayearRefPane = new UIRefPane();
				wayearRefPane.setName("wayearRefPane");
				wayearRefPane.setRefNodeName("��");
				wayearRefPane.setBounds(200, 170, 150, 50);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}

		}
		return this.wayearRefPane;
	}

	/**
	 * ��ʼʱ��
	 * 
	 * @return
	 */
	private UIRefPane getWabeginmonthRefPane() {
		if (this.wabeginmonthRefPane == null) {
			try {
				wabeginmonthRefPane = new UIRefPane();
				wabeginmonthRefPane.setName("wabeginmonthRefPane");
				wabeginmonthRefPane.setRefNodeName("��");
				wabeginmonthRefPane.setBounds(200, 210, 150, 50);
				wabeginmonthRefPane.addValueChangedListener(this);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}

		}
		return this.wabeginmonthRefPane;
	}

	/**
	 * ����ʱ��
	 * 
	 * @return
	 */
	private UIRefPane getWaendmonthRefPane() {
		if (this.waendmonthRefPane == null) {
			try {
				waendmonthRefPane = new UIRefPane();
				waendmonthRefPane.setName("waendmonthRefPane");
				waendmonthRefPane.setRefNodeName("��");
				waendmonthRefPane.setBounds(200, 250, 150, 50);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}

		}
		return this.waendmonthRefPane;
	}

	/**
	 * ȷ������
	 * 
	 * @throws Exception
	 */
	private void onButtonOKClicked() {
		this.isForeignMonth = getisForeignMonthDec().isSelected();
		this.unifiednumber = getUnifiednumberRefPane().getRefPK();
		if (null == this.unifiednumber || "".equals(this.unifiednumber)) {
			MessageDialog.showWarningDlg(this, ResHelper.getString("incometax", "2incometax-n-000004"),
					ResHelper.getString("incometax", "2incometax-n-000020") /* "ͳһ��Ų���Ϊ��" */);
			return;
		}
		this.declaretype = getDeclaretypeRefPane().getRefPKs();
		if (null == this.declaretype || "".equals(this.declaretype)) {
			MessageDialog.showWarningDlg(this, ResHelper.getString("incometax", "2incometax-n-000004"),
					ResHelper.getString("incometax", "2incometax-n-000021") /* "�걨ƾ����ʽ����Ϊ��" */);
			return;
		}
		this.waclass = getWaclassRefPane().getRefPKs();
		if (null == this.waclass || this.waclass.length < 1) {
			MessageDialog.showWarningDlg(this, ResHelper.getString("incometax", "2incometax-n-000004"),
					ResHelper.getString("incometax", "2incometax-n-000022")/* "н�ʷ�������Ϊ��" */);
			return;
		}
		this.year = getWayearRefPane().getRefPK();
		if (null == this.year || "".equals(this.year)) {
			MessageDialog.showWarningDlg(this, ResHelper.getString("incometax", "2incometax-n-000004"),
					ResHelper.getString("incometax", "2incometax-n-000023")/* "н����Ȳ���Ϊ��" */);
			return;
		}
		this.beginMonth = getWabeginmonthRefPane().getRefPK();
		if (null == this.beginMonth || "".equals(this.beginMonth)) {
			MessageDialog.showWarningDlg(this, ResHelper.getString("incometax", "2incometax-n-000004"),
					ResHelper.getString("incometax", "2incometax-n-000024")/* "��ʼʱ�䲻��Ϊ��" */);
			return;
		}
		this.endMonth = getWaendmonthRefPane().getRefPK();
		if (this.isForeignMonth) {
			this.endMonth = this.beginMonth;
		} else if (null == this.endMonth || "".equals(this.endMonth)) {
			MessageDialog.showWarningDlg(this, ResHelper.getString("incometax", "2incometax-n-000004"),
					ResHelper.getString("incometax", "2incometax-n-000025")/* "����ʱ�䲻��Ϊ��" */);
			return;
		} else if (Integer.valueOf(this.beginMonth) > Integer.valueOf(this.endMonth)) {
			MessageDialog.showWarningDlg(this, ResHelper.getString("incometax", "2incometax-n-000004"),
					ResHelper.getString("incometax", "2incometax-n-000026") /* "��ʼʱ�䲻��С�ڽ���ʱ��" */);
			return;
		}
		this.closeOK();

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == this.getBtnOK()) {
			new Thread() {
				@Override
				public void run() {

					IProgressMonitor progressMonitor = NCProgresses.createDialogProgressMonitor(getParent());

					try {
						progressMonitor.beginTask("�R����...", IProgressMonitor.UNKNOWN_REMAIN_TIME);
						progressMonitor.setProcessInfo("�R���У�Ո�Ժ�.....");
						onButtonOKClicked();
					} finally {

						progressMonitor.done(); // �����������
					}
				}
			}.start();
		}
		if (e.getSource() == this.getBtnCancel()) {
			this.closeCancel();
		}

	}

	public void valueChanged(ValueChangedEvent e) {
		if (e.getSource() == getUnifiednumberRefPane()) {
			// ����ͳһ��Ż�ȡ�ñ���¶�Ӧ����֯������ԃԓ�yһ
			String unifiednumber = getUnifiednumberRefPane().getRefPK();
			if (null != unifiednumber && !"".equals(unifiednumber)) {
				String qrySql = "select pk_hrorg from org_hrorg where "
						+ LocalizationSysinitUtil.getTwhrlOrg("TWHRLORG03") + " = '" + unifiednumber + "' or "
						+ LocalizationSysinitUtil.getTwhrlOrg("TWHRLORG15") + " = '" + unifiednumber + "'";
				String qryPid = "select pid from bd_defdoc where pk_defdoc='" + unifiednumber + "'";
				try {
					@SuppressWarnings("unchecked")
					List<String> orgs = (ArrayList<String>) LocalizationSysinitUtil.getUAPQueryBS().executeQuery(qrySql,
							new ColumnListProcessor());
					// Ares.Tank �Ӹ���ʾ�� 2018-9-5 22:00:38
					if (null == orgs || orgs.size() <= 0) {
						MessageDialog.showWarningDlg(this, ResHelper.getString("incometax", "2incometax-n-000004"),
								ResHelper.getString("incometax",
										"2incometax-n-000052")/* "�����yһ��̖�o����ԃ����Ч�ĽM���YӍ��Ո�z��M������TWHRLORG03��TWHRLORG15�Ƿ��O�����_��" */);
						return;
					}
					((WaClassRefModel) getWaclassRefPane().getRefModel()).setPk_orgs(orgs.toArray(new String[0]));
					Object pid = LocalizationSysinitUtil.getUAPQueryBS().executeQuery(qryPid, new ColumnProcessor());
					if ("1001ZZ1000000001PX6Q".equals(String.valueOf(pid))) {// ��ͨ
						((WaClassRefModel) getWaclassRefPane().getRefModel()).setIsferry("N");
					} else if ("1001ZZ1000000001PX6S".equals(String.valueOf(pid))) {// ��ί
						((WaClassRefModel) getWaclassRefPane().getRefModel()).setIsferry("Y");
					}
				} catch (BusinessException e1) {
					handleException(e1);
				}
				getWaclassRefPane().setPK(null);
			}
		}
		if (e.getSource() == getDeclaretypeRefPane()) {
			((WaClassRefModel) getWaclassRefPane().getRefModel()).setDeclareform(getDeclaretypeRefPane().getRefPKs());
		}

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// ��ȡ�ı�ĸ�ѡ����
		Object source = e.getItemSelectable();
		if (source == getisForeignMonthDec()) {
			// ����ѡ�Ƿ��⼮Ա�������걨ʱ��н�ʷ�����ѡ������ʱ�䲻�����༭
			if (getisForeignMonthDec().isSelected()) {
				getWaclassRefPane().setMultiSelectedEnabled(false);
				getWaendmonthRefPane().setEnabled(false);
			} else {
				getWaclassRefPane().setMultiSelectedEnabled(true);
				getWaendmonthRefPane().setEnabled(true);
			}
			getWaclassRefPane().setPK(null);
			getWaendmonthRefPane().setPK(null);
		}
	}

	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	protected void finalize() throws Throwable {
		super.finalize();
	}

	protected LayoutManager getLayoutManager() {
		return new GridLayout(2, 2);
	}

	/**
	 * ���ɽ�������ɢ���롣 �˷�����Ҫ��ɢ�б� ֧�֣��� java.util ���ṩ����?�ɢ�б���@return ������������ɢ����
	 */
	public int hashCode() {
		return super.hashCode();
	}

	private UIButton getBtnOK() {
		if (btnOK == null) {
			try {
				btnOK = new UIButton();
				btnOK.setName("btnOK");
				btnOK.setText(ResHelper.getString("incometax", "2incometax-n-000027")/* "ȷ��(Y)" */);
				btnOK.addActionListener(this);
				btnOK.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.ALT_MASK),
						JComponent.WHEN_IN_FOCUSED_WINDOW);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return btnOK;
	}

	private UIButton getBtnCancel() {
		if (btnCancel == null) {
			try {
				btnCancel = new UIButton();
				btnCancel.setName("btnCancel");
				btnCancel.setText(ResHelper.getString("incometax", "2incometax-n-000028")/* "ȡ��(C)" */);
				btnCancel.addActionListener(this);
				btnCancel.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK),
						JComponent.WHEN_IN_FOCUSED_WINDOW);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return btnCancel;
	}

	private void handleException(Throwable exception) {
		Logger.error("--------- δ��׽�����쳣 ---------");
		MessageDialog.showErrorDlg(this, null, exception.getMessage());
		exception.printStackTrace();
	}

	private UIButton btnOK = null;// ȷ����ť
	private UIButton btnCancel = null;// ȡ����ť
	private JPanel ivjUIDialogContentPane = null;
	private UIPanel ivjUIPanel1 = null;
	private UIPanel ivjUIPanel2 = null;
	private JCheckBox isForeignMonthDec;// �Ƿ��⼮Ա�������걨
	private UILabel unifiednumberLabel;// ͳһ���
	private UILabel declaretypeLabel = null;// �걨ƾ����ʽ
	private UILabel waclassLabel = null;// н�ʷ���
	private UILabel wayearLabel = null;// н�����
	private UILabel wabeginmonthLabel = null;// ��ʼʱ��
	private UILabel waendmonthLabel = null;// ��ֹʱ��

	private UIRefPane unifiednumberRefPane = null;// ͳһ���
	private UIRefPane declaretypeRefPane = null;// �걨ƾ����ʽ
	private UIRefPane waClassRefPane = null;// н�ʷ���
	private UIRefPane wayearRefPane = null;// н�����
	private UIRefPane wabeginmonthRefPane = null;// ��ʼʱ��
	private UIRefPane waendmonthRefPane = null;// ����ʱ��
	private WaClassRefModel classRefModel = null;
	private WaPeriodRefTreeModel periodRefModel = null;
	protected boolean isShow = true;

	private boolean isForeignMonth = false;
	private String unifiednumber;
	private String [] declaretype;
	private String[] waclass;
	private String year;
	private String beginMonth;
	private String endMonth;

	public boolean isForeignMonth() {
		return isForeignMonth;
	}

	public void setForeignMonth(boolean isForeignMonth) {
		this.isForeignMonth = isForeignMonth;
	}

	public String getUnifiednumber() {
		return unifiednumber;
	}

	public void setUnifiednumber(String unifiednumber) {
		this.unifiednumber = unifiednumber;
	}

	public String[] getDeclaretype() {
		return declaretype;
	}

	public void setDeclaretype(String[] declaretype) {
		this.declaretype = declaretype;
	}

	public String[] getWaclass() {
		return waclass;
	}

	public void setWaclass(String[] waclass) {
		this.waclass = waclass;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getBeginMonth() {
		return beginMonth;
	}

	public void setBeginMonth(String beginMonth) {
		this.beginMonth = beginMonth;
	}

	public String getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}

}