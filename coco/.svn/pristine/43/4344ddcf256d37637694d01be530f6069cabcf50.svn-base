package nc.ui.hi.employee.view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;

import nc.ui.hr.frame.dialog.HrDialog;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.pf.GroupLayout;
import nc.ui.wa.ref.WaClassRefModel;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.uif2.LoginContext;

/**
 * 薪資期間選擇對話框
 */
public class PeriodChooseDlg extends HrDialog implements PropertyChangeListener {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = -8311711296842851311L;
	private UILabel lblBaseWaClass = null;
	private UILabel lblEnableDate = null;
	// 基准日期
	private UILabel lblBaseDate = null;
	// 平均月数
	private UILabel lblAvgMonCount = null;
	private UIRefPane refBaseWaClass = null;
	private UIRefPane refBaseDate = null;
	private UIRefPane refEnableDate = null;
	// 平均月数参照
	private UIRefPane refAvgMonCount = null;
	private LoginContext context;

	private String[] pk_wa_class = null;
	// 基准日期
	private UFDate cBaseDate = null;
	// 平均月数
	private String avgMonCount = null;
	private UFDate dEffectiveDate = null;

	public String[] getPk_wa_class() {
		return pk_wa_class;
	}

	public UFDate getdEffectiveDate() {
		return dEffectiveDate;
	}

	public UFDate getcBaseDate() {
		return cBaseDate;
	}

	public String getAvgMonCount() {
		return avgMonCount;
	}

	/**
	 * 薪资方案
	 */
	public UILabel getLblBaseWaClass() {
		if (lblBaseWaClass == null) {
			lblBaseWaClass = new UILabel();

			lblBaseWaClass.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
					"068J61035-0022")/*
										 * @res 薪資方案
										 */);
		}

		return lblBaseWaClass;
	}

	/**
	 * 基准日期
	 * 
	 * @return
	 */
	public UILabel getLblBaseDate() {
		if (lblBaseDate == null) {
			lblBaseDate = new UILabel();

			lblBaseDate.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
					"068J61035-0088")/*
										 * @res 基准日期
										 */);
		}

		return lblBaseDate;
	}

	public UILabel getLblEnableDate() {
		if (lblEnableDate == null) {
			lblEnableDate = new UILabel();

			lblEnableDate.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
					"068J61035-0023")/*
										 * @res 生效日期
										 */);
		}

		return lblEnableDate;
	}

	/**
	 * 平均月数
	 */
	public UILabel getLblAvgMonCount() {
		if (lblAvgMonCount == null) {
			lblAvgMonCount = new UILabel();

			lblAvgMonCount.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
					"068J61035-0089")/*
										 * @res 平均月数
										 */);
		}

		return lblAvgMonCount;
	}

	/**
	 * 薪资方案
	 */
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

	/**
	 * 平均月数
	 */
	public UIRefPane getRefAvgMonCount() {
		if (refAvgMonCount == null) {
			refAvgMonCount = new UIRefPane("平均月数(自定义档案)");

			refAvgMonCount.setRefType(3);
			refAvgMonCount.setVisible(true);
			refAvgMonCount.setPreferredSize(new Dimension(200, 20));
			refAvgMonCount.setButtonFireEvent(true);
			refAvgMonCount.addPropertyChangeListener(this);
		}

		return refAvgMonCount;
	}

	/**
	 * 基准日期参照
	 * 
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

	@SuppressWarnings("all")
	@Override
	public void closeOK() {

		if (StringUtils.isEmpty(this.getRefBaseWaClass().getRefModel().getPkValue())) {
			MessageDialog.showErrorDlg(this.getOwner(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
							"068J61035-0026")/* 提示 */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
							"068J61035-0022")/* 薪资方案 */
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
									"068J61035-0027")/* 不能为空 */);
		} else if (this.getRefEnableDate().getValueObj() == null
				|| StringUtils.isEmpty(this.getRefEnableDate().getValueObj().toString())) {
			MessageDialog.showErrorDlg(this.getOwner(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
							"068J61035-0026")/* 提示 */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
							"068J61035-0023")/* 生效日期 */
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
									"068J61035-0027")/* 不能为空 */);
		} else if (StringUtils.isEmpty(this.getRefBaseDate().getValueObj().toString())) {
			MessageDialog.showErrorDlg(this.getOwner(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
							"068J61035-0026")/* 提示 */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
							"068J61035-0033")/* 基准日期 */
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
									"068J61035-0027")/* 不能为空 */);
		} /*
			 * else if (StringUtils.isEmpty( (String)
			 * this.getRefAvgMonCount().getRefModel().getValue("0"))) {
			 * MessageDialog.showErrorDlg( this.getOwner(),
			 * nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
			 * "twhr_personalmgt", "068J61035-0026") 提示 ,
			 * nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
			 * "twhr_personalmgt", "068J61035-0034") 平均月数 +
			 * nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
			 * .getStrByID("twhr_personalmgt", "068J61035-0027") 不能为空 ); }
			 */else {
			pk_wa_class = this.getRefBaseWaClass().getRefModel().getPkValues();
			dEffectiveDate = new UFDate(this.getRefEnableDate().getValueObj().toString());
			cBaseDate = new UFDate(this.getRefBaseDate().getValueObj().toString());
			/*
			 * avgMonCount = (String) this.getRefAvgMonCount().getRefModel()
			 * .getValue("0");
			 */

			super.closeOK();
		}
	}

	@Override
	public void initUI() {
		super.initUI();
		setSize(300, 190);
	}

	@SuppressWarnings("all")
	@Override
	protected JComponent createCenterPanel() {
		JPanel mypanel = new JPanel();

		GroupLayout layout = new GroupLayout(mypanel);
		mypanel.setLayout(layout);

		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.LEADING).add(layout.createSequentialGroup()
				.addContainerGap()
				.add(layout.createParallelGroup(GroupLayout.LEADING)
						.add(layout.createSequentialGroup().add(this.getLblBaseWaClass()).addPreferredGap(0).add(
								this.getRefBaseWaClass(), GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))
						.add(layout.createSequentialGroup().add(this.getLblBaseDate()).addPreferredGap(0).add(
								this.getRefBaseDate(), GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))
						.add(layout.createSequentialGroup().add(this.getLblAvgMonCount()).addPreferredGap(0).add(
								this.getRefAvgMonCount(), GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))
						.add(layout.createSequentialGroup().add(this.getLblEnableDate()).addPreferredGap(0).add(
								this.getRefEnableDate(), GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)))
				.addContainerGap(220, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.LEADING)
				.add(layout.createSequentialGroup().addContainerGap()
						.add(layout.createParallelGroup(GroupLayout.BASELINE).add(this.getLblBaseWaClass()).add(
								this.getRefBaseWaClass(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))
						.addContainerGap(242, Short.MAX_VALUE)
						.add(layout.createParallelGroup(GroupLayout.BASELINE).add(this.getLblBaseDate()).add(
								this.getRefBaseDate(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))
						.addContainerGap(242, Short.MAX_VALUE)
						.add(layout.createParallelGroup(GroupLayout.BASELINE).add(this.getLblAvgMonCount()).add(
								this.getRefAvgMonCount(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))
						.addContainerGap(242, Short.MAX_VALUE)
						.add(layout.createParallelGroup(GroupLayout.BASELINE).add(this.getLblEnableDate()).add(
								this.getRefEnableDate(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
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
		if (StringUtils.isEmpty(this.getRefBaseWaClass().getRefPK())
				|| StringUtils.isEmpty(this.getRefBaseDate().getRefPK())
				|| StringUtils.isEmpty(this.getRefEnableDate().getRefPK())
				|| StringUtils.isEmpty(this.getRefAvgMonCount().getRefShowCode())) {
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
		if (this.getContext() != null && !StringUtils.isEmpty(this.getContext().getPk_org())) {
			/*
			 * this.getRefBaseWaClass().getRefModel()
			 * .setPk_org(this.getContext().getPk_org());
			 * 
			 * this.getRefStartPeriod().getRefModel()
			 * .setPk_org(this.getContext().getPk_org());
			 * this.getRefStartPeriod().getRefModel().setWherePart(
			 * "sealflag='N'");
			 * 
			 * this.getRefEndPeriod().getRefModel()
			 * .setPk_org(this.getContext().getPk_org());
			 * this.getRefEndPeriod().getRefModel().setWherePart("sealflag='N'")
			 * ;
			 */
			this.getRefBaseWaClass().getRefModel().setPk_org(this.getContext().getPk_org());
			this.getRefAvgMonCount().getRefModel().setPk_org(this.getContext().getPk_org());
		}

		setOkEnabled();
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		try {
			/*
			 * if (e.getSource() == this.getRefBaseWaClass()) {
			 * ((TWHRPeriodRefModel) this.getRefStartPeriod().getRefModel())
			 * .setPk_wa_classes(this.getRefBaseWaClass()
			 * .getRefModel().getPkValues()); ((TWHRPeriodRefModel)
			 * this.getRefEndPeriod().getRefModel())
			 * .setPk_wa_classes(this.getRefBaseWaClass()
			 * .getRefModel().getPkValues()); }
			 */

			this.setOkEnabled();
		} catch (Exception ex) {
			ExceptionUtils.wrappBusinessException(ex.getMessage());
		}
	}
}
