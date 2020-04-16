package nc.ui.wa.formular;

import nc.hr.utils.ResHelper;
import nc.vo.pub.lang.UFDate;

/**
 * 
 * @author: ssx
 * @date: 2020-02-21
 * @since: V65
 */
public class TermWageDaysFunctionEditor extends WaAbstractFunctionEditor {
	private static final long serialVersionUID = -8508440434951788569L;
	private nc.ui.pub.beans.UIRefPane beginDateRefpane = null;
	private nc.ui.pub.beans.UIRefPane endDateRefpane = null;
	private nc.ui.pub.beans.UILabel ivjUILabel = null;
	private nc.ui.pub.beans.UILabel ivjUILabel2 = null;

	private static final String funcname = "@" + ResHelper.getString("6013commonbasic", "06013commonbasic0284") + "@";

	// "wageDays";

	/**
	 * WaParaPanel ������ע�⡣
	 */
	public TermWageDaysFunctionEditor() {
		super();

		initialize();
	}

	@Override
	public String getFuncName() {
		// TODO Auto-generated method stub
		return funcname;
	}

	/**
	 * ��ʼ���ࡣ
	 */
	/* ���棺�˷������������ɡ� */
	private void initialize() {
		try {
			setLayout(null);
			setSize(300, 300);

			add(getUILabel(), getUILabel().getName());
			add(getBeginDateRefpane(), getBeginDateRefpane().getName());

			add(getUILabel2(), getUILabel2().getName());
			add(getEndDateRefPane(), getEndDateRefPane().getName());

			add(getOkButton(), getOkButton().getName());
			add(getCancelButton(), getOkButton().getName());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}

		initConnection();

	}

	@Override
	public boolean checkPara(int dataType) {

		return true;
	}

	@Override
	public String[] getPara() {
		String[] paras = new String[2];
		String beginDate = "";
		String endDate = "";
		if (getBeginDateRefpane().getValueObj() != null) {
			beginDate = ((UFDate) getBeginDateRefpane().getValueObj()).toStdString();
		}
		if (getEndDateRefPane().getValueObj() != null) {
			endDate = ((UFDate) getEndDateRefPane().getValueObj()).toStdString();
		}
		paras[0] = "\"" + beginDate + "\"";

		paras[1] = "\"" + endDate + "\"";

		return paras;
	}

	/**
	 * ���� UIRefPane11 ����ֵ��
	 * 
	 * @return nc.ui.pub.beans.UIRefPane
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UIRefPane getBeginDateRefpane() {
		if (beginDateRefpane == null) {
			try {
				beginDateRefpane = new nc.ui.pub.beans.UIRefPane();
				beginDateRefpane.setName("beginDate");
				beginDateRefpane.setBounds(100, 30, 140, 22);
				beginDateRefpane.setRefNodeName("����");
				beginDateRefpane.setVisible(true);

			} catch (java.lang.Throwable ivjExc) {

				handleException(ivjExc);
			}
		}
		return beginDateRefpane;
	}

	/**
	 * ���� UIRefPane11 ����ֵ��
	 * 
	 * @return nc.ui.pub.beans.UIRefPane
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UIRefPane getEndDateRefPane() {
		if (endDateRefpane == null) {
			try {
				endDateRefpane = new nc.ui.pub.beans.UIRefPane();
				endDateRefpane.setName("endDate");
				endDateRefpane.setBounds(100, 70, 140, 22);
				endDateRefpane.setRefNodeName("����");
				endDateRefpane.setVisible(true);

			} catch (java.lang.Throwable ivjExc) {

				handleException(ivjExc);
			}
		}
		return endDateRefpane;
	}

	/**
	 * ���� UILabel3 ����ֵ��
	 * 
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UILabel getUILabel() {
		if (ivjUILabel == null) {
			try {
				ivjUILabel = new nc.ui.pub.beans.UILabel();
				ivjUILabel.setName("UILabel");
				ivjUILabel.setText(ResHelper.getString("common", "UC000-0001892")
				/* @res "��ʼ����" */);
				ivjUILabel.setBounds(10, 30, 80, 22);

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabel;
	}

	/**
	 * ���� UILabel2 ����ֵ��
	 * 
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UILabel getUILabel2() {
		if (ivjUILabel2 == null) {
			try {
				ivjUILabel2 = new nc.ui.pub.beans.UILabel();
				ivjUILabel2.setName("UILabel2");
				ivjUILabel2.setText(ResHelper.getString("common", "UC000-0003232")
				/* @res "����ʱ��" */);
				ivjUILabel2.setBounds(10, 70, 80, 22);

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabel2;
	}
}