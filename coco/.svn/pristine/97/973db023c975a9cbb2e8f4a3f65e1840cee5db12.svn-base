package nc.ui.twhr.twhr_declaration.ace.view;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JPanel;

import nc.ui.bd.twhr.view.HealthMonthSelectDlg;
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
 * 日期選擇對話框
 */
public class DateGeneraDlg extends HrDialog implements PropertyChangeListener {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = -8311711296842851311L;


	
	private UILabel lblEnableDateMonth = null;


	
	private UIRefPane refEnableDateMonth = null;


	
	private Object dEffectiveDateMonth = null;
	
	//private String desc = "";




	public Object getdEffectiveDateMonth() {
		return dEffectiveDateMonth;
	}

	



	public UILabel getLblEnableDateMonth() {
		if (lblEnableDateMonth == null) {
			lblEnableDateMonth = new UILabel();

			lblEnableDateMonth.setText("月份");
		}

		return lblEnableDateMonth;
	}



	
	public UIRefPane getRefEnableDateMonth() {
		if (refEnableDateMonth == null) {
			refEnableDateMonth = new UIRefPane();
			refEnableDateMonth.setRefModel(new HealthMonthSelectDlg());
			refEnableDateMonth.setVisible(true);
			refEnableDateMonth.setPreferredSize(new Dimension(200, 40));
			refEnableDateMonth.setButtonFireEvent(true);
			refEnableDateMonth.addPropertyChangeListener(this);
		}

		return refEnableDateMonth;
	}

	/**
	 * @param parent
	 */
	public DateGeneraDlg(Container parent, String title,String desc) {
		super(parent, title, false);
		//this.desc = desc;
	}

	@Override
	public void closeOK() {
		 if (this.getRefEnableDateMonth().getValueObj() == null
				|| StringUtils.isEmpty(this.getRefEnableDateMonth().getValueObj()
						.toString())) {
			MessageDialog.showErrorDlg(
					this.getOwner(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"twhr_personalmgt", "068J61035-0026")/* 提示 */,
					"月份"
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("twhr_personalmgt",
											"068J61035-0027")/* 不能为空 */);
		} else {
			
			dEffectiveDateMonth = ((String[])(this.getRefEnableDateMonth().getValueObj()))[0];
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
										.add(this.getLblEnableDateMonth())
										.addPreferredGap(0)
										.add(this.getRefEnableDateMonth(),
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
								.add(this.getLblEnableDateMonth())
								.add(this.getRefEnableDateMonth(),
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addContainerGap(242, Short.MAX_VALUE)));

		return mypanel;
	}

	


	private void setOkEnabled() {
		if ( StringUtils.isEmpty(this.getRefEnableDateMonth().getRefPK())) {
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
