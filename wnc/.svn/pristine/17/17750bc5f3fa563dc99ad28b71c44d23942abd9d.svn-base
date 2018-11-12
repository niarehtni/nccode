package nc.ui.hi.employee.view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import nc.ref.twhr.refmodel.TWHRPeriodRefModel;
import nc.ui.hr.frame.dialog.HrDialog;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.pf.GroupLayout;
import nc.ui.wa.ref.WaClassRefModel;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;
import org.docx4j.wml.Color;

/**
 * 同步团保级距選擇對話框
 */
public class GinsPeriodChooseDlg extends HrDialog implements PropertyChangeListener {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = -8311711296802851311L;
	//生效日期
	private UILabel lblBaseDate = null;
	private UIRefPane refBaseDate = null;
	//生效日期
	private UFDate cBaseDate = null;
	private LoginContext context;

	public UFDate getcBaseDate(){
		return cBaseDate;
	}

	/**
	 * 生效日期
	 * @return
	 */
	public UILabel getLblBaseDate() {
		if (lblBaseDate == null) {
			lblBaseDate = new UILabel();

			lblBaseDate.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("twhr_personalmgt", "068J61035-0023")/*
																	 * @res 生效日期
																	 */);
		}

		return lblBaseDate;
	}
	
	/**
	 * 生效日期参照
	 * @return
	 */
	public UIRefPane getRefBaseDate() {
		if (refBaseDate == null) {
			refBaseDate = new UIRefPane("日期");
			refBaseDate.setVisible(true);
			refBaseDate.setPreferredSize(new Dimension(200, 20));
			refBaseDate.setButtonFireEvent(true);
			refBaseDate.addPropertyChangeListener(this);
		}

		return refBaseDate;
	}
	
	
	/**
	 * @param parent
	 */
	public GinsPeriodChooseDlg(Container parent, String title) {
		super(parent, title, false);
	}
	@SuppressWarnings("all")
	@Override
	public void closeOK() {
	
		if (StringUtils.isEmpty( this.getRefBaseDate().getValueObj()
				.toString())) {
			MessageDialog.showErrorDlg(
					this.getOwner(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"twhr_personalmgt", "068J61035-0026")/* 提示 */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"twhr_personalmgt", "068J61035-0023")/* 生效日期 */
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("twhr_personalmgt",
											"068J61035-0027")/* 不能为空 */);
		} else {
			cBaseDate = new UFDate(this.getRefBaseDate().getValueObj()
					.toString());

			super.closeOK();
		}
	}

	@Override
	public void initUI() {
		super.initUI();
		setSize(300, 150);
	}
	@SuppressWarnings("all")
	@Override
	protected JComponent createCenterPanel() {
		JPanel mypanel = new JPanel();

		GroupLayout layout = new GroupLayout(mypanel);
		mypanel.setLayout(layout);

		layout.setHorizontalGroup(layout
				.createParallelGroup(GroupLayout.LEADING)
				.add(layout
						.createSequentialGroup()
						.addContainerGap()
						.add(layout
								.createParallelGroup(GroupLayout.LEADING)
								.add(layout
										.createSequentialGroup()
										.add(this.getLblBaseDate())
										.addPreferredGap(0)
										.add(this.getRefBaseDate(),
												GroupLayout.PREFERRED_SIZE,
												200, GroupLayout.PREFERRED_SIZE))
								)
						.addContainerGap(220, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.LEADING)
				.add(layout
						.createSequentialGroup()
						.addContainerGap()
					.add(layout
							.createParallelGroup(GroupLayout.BASELINE)
							.add(this.getLblBaseDate())
							.add(this.getRefBaseDate(),
									GroupLayout.PREFERRED_SIZE,
									GroupLayout.DEFAULT_SIZE,
									GroupLayout.PREFERRED_SIZE))
				.addContainerGap(242, Short.MAX_VALUE)));

		return mypanel;
	}


	public LoginContext getContext() {
		return context;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}

	private void setOkEnabled() {
		if (StringUtils.isEmpty(this.getRefBaseDate().getRefPK())) {
			this.getBtnOk().setEnabled(false);
		} else {
			this.getBtnOk().setEnabled(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
	}

	public void loadPeriod() {
		if (this.getContext() != null
				&& !StringUtils.isEmpty(this.getContext().getPk_org())) {
		}

		setOkEnabled();
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		try {
			this.setOkEnabled();
		} catch (Exception ex) {
			ExceptionUtils.wrappBusinessException(ex.getMessage());
		}
	}
}
