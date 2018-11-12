package nc.ui.wa.paydata.view;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JPanel;

import nc.ui.hr.frame.dialog.HrDialog;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.pf.GroupLayout;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;

/**
 * 劳健保日期選擇對話框
 */
public class DateLarborDelDlg extends HrDialog implements PropertyChangeListener {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = -8311711296842851311L;

	private UILabel lblEnableDate = null;

	private UIRefPane refEnableDate = null;

	private UFDate dEffectiveDate = null;
	
	private String desc = "";



	public UFDate getdEffectiveDate() {
		return dEffectiveDate;
	}

	

	public UILabel getLblEnableDate() {
		if (lblEnableDate == null) {
			lblEnableDate = new UILabel();

			lblEnableDate.setText(desc);
		}

		return lblEnableDate;
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
	public DateLarborDelDlg(Container parent, String title,String desc) {
		super(parent, title, false);
		this.desc = desc;
	}

	@Override
	public void closeOK() {
		if (this.getRefEnableDate().getValueObj() == null
				|| StringUtils.isEmpty(this.getRefEnableDate().getValueObj()
						.toString())) {
			MessageDialog.showErrorDlg(
					this.getOwner(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"twhr_personalmgt", "068J61035-0026")/* 提示 */,
					desc
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("twhr_personalmgt",
											"068J61035-0027")/* 不能为空 */);
		} else {

			dEffectiveDate = new UFDate(this.getRefEnableDate().getValueObj()
					.toString());

			super.closeOK();
		}
	}

	@Override
	public void initUI() {
		super.initUI();
		setSize(300, 100);
		
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
								.add(this.getLblEnableDate())
								.add(this.getRefEnableDate(),
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addContainerGap(242, Short.MAX_VALUE)));

		return mypanel;
	}

	


	private void setOkEnabled() {
		if ( StringUtils.isEmpty(this.getRefEnableDate().getRefPK())) {
			this.getBtnOk().setEnabled(false);
		} else {
			this.getBtnOk().setEnabled(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
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
