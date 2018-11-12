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

/**
 * 薪資期間選擇對話框
 */
public class PeriodChooseDlg extends HrDialog implements PropertyChangeListener {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = -8311711296842851311L;
	private UILabel lblBaseWaClass = null;
	private UILabel lblStartPeriod = null;
	private UILabel lblEndPeriod = null;
	private UILabel lblEnableDate = null;
	private UIRefPane refBaseWaClass = null;
	private UIRefPane refStartPeriod = null;
	private UIRefPane refEndPeriod = null;
	private UIRefPane refEnableDate = null;
	private LoginContext context;

	private String pk_wa_class = null;
	private String cStartPeriod = null;
	private String cEndPeriod = null;
	private UFDate dEffectiveDate = null;

	public String getPk_wa_class() {
		return pk_wa_class;
	}

	public String getcStartPeriod() {
		return cStartPeriod;
	}

	public String getcEndPeriod() {
		return cEndPeriod;
	}

	public UFDate getdEffectiveDate() {
		return dEffectiveDate;
	}

	public UILabel getLblBaseWaClass() {
		if (lblBaseWaClass == null) {
			lblBaseWaClass = new UILabel();

			lblBaseWaClass.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("twhr_personalmgt", "068J61035-0022")/*
																	 * @res 薪資方案
																	 */);
		}

		return lblBaseWaClass;
	}

	public UILabel getLblEndPeriod() {
		if (lblEndPeriod == null) {
			lblEndPeriod = new UILabel();

			lblEndPeriod.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("twhr_personalmgt", "068J61035-0021")/*
																	 * @res 截止期間
																	 */);
		}

		return lblEndPeriod;
	}

	public UILabel getLblEnableDate() {
		if (lblEnableDate == null) {
			lblEnableDate = new UILabel();

			lblEnableDate.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("twhr_personalmgt", "068J61035-0023")/*
																	 * @res 生效日期
																	 */);
		}

		return lblEnableDate;
	}

	public UIRefPane getRefBaseWaClass() {
		if (refBaseWaClass == null) {
			refBaseWaClass = new UIRefPane();
			refBaseWaClass.setRefModel(new WaClassRefModel());
			refBaseWaClass.setVisible(true);
			refBaseWaClass.setPreferredSize(new Dimension(200, 20));
			refBaseWaClass.setButtonFireEvent(true);
			refBaseWaClass.addPropertyChangeListener(this);
			refBaseWaClass.setMultiSelectedEnabled(true);
			refBaseWaClass.getRefModel().setMutilLangNameRef(false);
		}

		return refBaseWaClass;
	}

	public UIRefPane getRefStartPeriod() {
		if (refStartPeriod == null) {
			refStartPeriod = new UIRefPane();
			TWHRPeriodRefModel refModel = new TWHRPeriodRefModel();
			refModel.setShowHistoryPeriod(true);
			refStartPeriod.setRefModel(refModel);
			refStartPeriod.setVisible(true);
			refStartPeriod.setPreferredSize(new Dimension(200, 20));
			refStartPeriod.setButtonFireEvent(true);
			refStartPeriod.addPropertyChangeListener(this);
			refStartPeriod.getRefModel().setMutilLangNameRef(false);
		}

		return refStartPeriod;
	}

	public UIRefPane getRefEndPeriod() {
		if (refEndPeriod == null) {
			refEndPeriod = new UIRefPane();
			TWHRPeriodRefModel refModel = new TWHRPeriodRefModel();
			refModel.setShowHistoryPeriod(true);
			refEndPeriod.setRefModel(refModel);
			refEndPeriod.setVisible(true);
			refEndPeriod.setPreferredSize(new Dimension(200, 20));
			refEndPeriod.setButtonFireEvent(true);
			refEndPeriod.addPropertyChangeListener(this);
			refEndPeriod.getRefModel().setMutilLangNameRef(false);
		}

		return refEndPeriod;
	}

	public UIRefPane getRefEnableDate() {
		if (refEnableDate == null) {
			refEnableDate = new UIRefPane("日期");
			refEnableDate.setVisible(true);
			refEnableDate.setPreferredSize(new Dimension(200, 20));
			refEnableDate.setButtonFireEvent(true);
			refEnableDate.addPropertyChangeListener(this);
		}

		return refEnableDate;
	}

	/**
	 * @param parent
	 */
	public PeriodChooseDlg(Container parent, String title) {
		super(parent, title, false);
	}

	@Override
	public void closeOK() {
		if (StringUtils.isEmpty(this.getRefBaseWaClass().getRefModel()
				.getPkValue())) {
			MessageDialog.showErrorDlg(
					this.getOwner(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"twhr_personalmgt", "068J61035-0026")/* 提示 */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"twhr_personalmgt", "068J61035-0022")/* 薪资方案 */
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("twhr_personalmgt",
											"068J61035-0027")/* 不能为空 */);
		} else if (StringUtils.isEmpty((String) this.getRefStartPeriod()
				.getRefModel().getValue("cyear||'-'||cperiod"))) {
			MessageDialog.showErrorDlg(
					this.getOwner(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"twhr_personalmgt", "068J61035-0026")/* 提示 */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"twhr_personalmgt", "068J61035-0000")/* 起始期间 */
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("twhr_personalmgt",
											"068J61035-0027")/* 不能为空 */);
		} else if (StringUtils.isEmpty((String) this.getRefEndPeriod()
				.getRefModel().getValue("cyear||'-'||cperiod"))) {
			MessageDialog.showErrorDlg(
					this.getOwner(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"twhr_personalmgt", "068J61035-0026")/* 提示 */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"twhr_personalmgt", "068J61035-0021")/* 截止期间 */
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("twhr_personalmgt",
											"068J61035-0027")/* 不能为空 */);
		} else if (this.getRefEnableDate().getValueObj() == null
				|| StringUtils.isEmpty(this.getRefEnableDate().getValueObj()
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
			pk_wa_class = (String) this.getRefBaseWaClass().getRefModel()
					.getPkValue();
			cStartPeriod = (String) this.getRefStartPeriod().getRefModel()
					.getValue("cyear||'-'||cperiod");
			cEndPeriod = (String) this.getRefEndPeriod().getRefModel()
					.getValue("cyear||'-'||cperiod");
			dEffectiveDate = new UFDate(this.getRefEnableDate().getValueObj()
					.toString());

			super.closeOK();
		}
	}

	@Override
	public void initUI() {
		super.initUI();
		setSize(300, 190);
	}

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
										.add(this.getLblBaseWaClass())
										.addPreferredGap(0)
										.add(this.getRefBaseWaClass(),
												GroupLayout.PREFERRED_SIZE,
												200, GroupLayout.PREFERRED_SIZE))
								.add(layout
										.createSequentialGroup()
										.add(this.getLblStartPeriod())
										.addPreferredGap(0)
										.add(this.getRefStartPeriod(),
												GroupLayout.PREFERRED_SIZE,
												200, GroupLayout.PREFERRED_SIZE))
								.add(layout
										.createSequentialGroup()
										.add(this.getLblEndPeriod())
										.addPreferredGap(0)
										.add(this.getRefEndPeriod(),
												GroupLayout.PREFERRED_SIZE,
												200, GroupLayout.PREFERRED_SIZE))
								.add(layout
										.createSequentialGroup()
										.add(this.getLblEnableDate())
										.addPreferredGap(0)
										.add(this.getRefEnableDate(),
												GroupLayout.PREFERRED_SIZE,
												200, GroupLayout.PREFERRED_SIZE)))
						.addContainerGap(220, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.LEADING)
				.add(layout
						.createSequentialGroup()
						.addContainerGap()
						.add(layout
								.createParallelGroup(GroupLayout.BASELINE)
								.add(this.getLblBaseWaClass())
								.add(this.getRefBaseWaClass(),
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(0)
						.add(layout
								.createParallelGroup(GroupLayout.BASELINE)
								.add(this.getLblStartPeriod())
								.add(this.getRefStartPeriod(),
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(0)
						.add(layout
								.createParallelGroup(GroupLayout.BASELINE)
								.add(this.getLblEndPeriod())
								.add(this.getRefEndPeriod(),
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(0)
						.add(layout
								.createParallelGroup(GroupLayout.BASELINE)
								.add(this.getLblEnableDate())
								.add(this.getRefEnableDate(),
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addContainerGap(242, Short.MAX_VALUE)));

		return mypanel;
	}

	private UILabel getLblStartPeriod() {
		if (lblStartPeriod == null) {
			lblStartPeriod = new UILabel();

			lblStartPeriod.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("twhr_personalmgt", "068J61035-0000")/*
																	 * @res 期間
																	 */);
		}

		return lblStartPeriod;
	}

	public LoginContext getContext() {
		return context;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}

	private void setOkEnabled() {
		if (StringUtils.isEmpty(this.getRefStartPeriod().getRefPK())
				|| StringUtils.isEmpty(this.getRefEndPeriod().getRefPK())
				|| StringUtils.isEmpty(this.getRefBaseWaClass().getRefPK())
				|| StringUtils.isEmpty(this.getRefEnableDate().getRefPK())) {
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
			this.getRefBaseWaClass().getRefModel()
					.setPk_org(this.getContext().getPk_org());

			this.getRefStartPeriod().getRefModel()
					.setPk_org(this.getContext().getPk_org());
			this.getRefStartPeriod().getRefModel().setWherePart("sealflag='N'");

			this.getRefEndPeriod().getRefModel()
					.setPk_org(this.getContext().getPk_org());
			this.getRefEndPeriod().getRefModel().setWherePart("sealflag='N'");
		}

		setOkEnabled();
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		try {
			if (e.getSource() == this.getRefBaseWaClass()) {
				((TWHRPeriodRefModel) this.getRefStartPeriod().getRefModel())
						.setPk_wa_classes(this.getRefBaseWaClass()
								.getRefModel().getPkValues());
				((TWHRPeriodRefModel) this.getRefEndPeriod().getRefModel())
						.setPk_wa_classes(this.getRefBaseWaClass()
								.getRefModel().getPkValues());
			}

			this.setOkEnabled();
		} catch (Exception ex) {
			ExceptionUtils.wrappBusinessException(ex.getMessage());
		}
	}
}
