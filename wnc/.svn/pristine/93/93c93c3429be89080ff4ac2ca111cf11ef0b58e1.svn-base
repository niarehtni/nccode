package nc.ui.hrwa.sumincometax.action;

import java.awt.Container;

import nc.ui.hr.frame.dialog.HrDialog;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.pf.GroupLayout;
import nc.vo.uif2.LoginContext;

/**
 * 個稅申報表生成申報檔對話框
 */
public class ExportIndividualTaxDlg extends HrDialog {

	/**
	 * serial version
	 */
	private static final long serialVersionUID = -4292706023705083569L;

	private int applyCount;
	private String applyReason;
	private String applyFormat;

	private UILabel lblApplyCount;
	private UILabel lblApplyReason;
	private UILabel lblApplyFormat;

	private UITextField txtApplyCount;
	private UIComboBox cboApplyReason;
	private UIComboBox cboApplyFormat;

	private LoginContext context;

	public ExportIndividualTaxDlg(Container parent, LoginContext context) {
		super(parent);
		setSize(300, 270);
		setContext(context);
	}

	protected UIPanel createCenterPanel() {
		UIPanel mypanel = new UIPanel();

		this.getLblApplyFormat().setText("　　申報格式");
		this.getLblApplyCount().setText("重複申報次數");
		this.getLblApplyReason().setText("重複申報原因");

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
										.add(this.getLblApplyFormat())
										.addPreferredGap(0)
										.add(this.getCboApplyFormat(),
												GroupLayout.PREFERRED_SIZE,
												200, GroupLayout.PREFERRED_SIZE))
								.add(layout
										.createSequentialGroup()
										.add(this.getLblApplyCount())
										.addPreferredGap(0)
										.add(this.getTxtApplyCount(),
												GroupLayout.PREFERRED_SIZE,
												200, GroupLayout.PREFERRED_SIZE))
								.add(layout
										.createSequentialGroup()
										.add(this.getLblApplyReason())
										.addPreferredGap(0)
										.add(this.getCboApplyReason(),
												GroupLayout.PREFERRED_SIZE,
												200, GroupLayout.PREFERRED_SIZE)))
						.addContainerGap(100, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.LEADING)
				.add(layout
						.createSequentialGroup()
						.addContainerGap()
						.add(layout
								.createParallelGroup(GroupLayout.BASELINE)
								.add(this.getLblApplyFormat())
								.add(this.getCboApplyFormat(),
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(0)
						.add(layout
								.createParallelGroup(GroupLayout.BASELINE)
								.add(this.getLblApplyCount())
								.add(this.getTxtApplyCount(),
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(0)
						.add(layout
								.createParallelGroup(GroupLayout.BASELINE)
								.add(this.getLblApplyReason())
								.add(this.getCboApplyReason(),
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addContainerGap(100, Short.MAX_VALUE)));

		return mypanel;
	}

	public int getApplyCount() {
		return applyCount;
	}

	public void setApplyCount(int applyCount) {
		this.applyCount = applyCount;
	}

	public String getApplyReason() {
		return applyReason;
	}

	public void setApplyReason(String applyReason) {
		this.applyReason = applyReason;
	}

	public String getApplyFormat() {
		return applyFormat;
	}

	public void setApplyFormat(String applyFormat) {
		this.applyFormat = applyFormat;
	}

	public UILabel getLblApplyCount() {
		if (lblApplyCount == null) {
			lblApplyCount = new UILabel();
		}
		return lblApplyCount;
	}

	public void setLblApplyCount(UILabel lblApplyCount) {
		this.lblApplyCount = lblApplyCount;
	}

	public UILabel getLblApplyReason() {
		if (lblApplyReason == null) {
			lblApplyReason = new UILabel();
		}
		return lblApplyReason;
	}

	public void setLblApplyReason(UILabel lblApplyReason) {
		this.lblApplyReason = lblApplyReason;
	}

	public UILabel getLblApplyFormat() {
		if (lblApplyFormat == null) {
			lblApplyFormat = new UILabel();
		}
		return lblApplyFormat;
	}

	public void setLblApplyFormat(UILabel lblApplyFormat) {
		this.lblApplyFormat = lblApplyFormat;
	}

	public UITextField getTxtApplyCount() {
		if (txtApplyCount == null) {
			txtApplyCount = new UITextField();
		}
		return txtApplyCount;
	}

	public void setTxtApplyCount(UITextField txtApplyCount) {
		this.txtApplyCount = txtApplyCount;
	}

	public UIComboBox getCboApplyReason() {
		if (cboApplyReason == null) {
			cboApplyReason = new UIComboBox();
			if (cboApplyReason.getItemCount() == 0) {
				cboApplyReason.addItem("無重複申報情形");
				cboApplyReason.addItem("申報單位發現錯誤主動更正");
				cboApplyReason.addItem("稽徵機關發現錯誤更正");
			}
		}
		return cboApplyReason;
	}

	public void setCboApplyReason(UIComboBox cboApplyReason) {
		this.cboApplyReason = cboApplyReason;
	}

	public UIComboBox getCboApplyFormat() {
		if (cboApplyFormat == null) {
			cboApplyFormat = new UIComboBox();
			if (cboApplyFormat.getItemCount() == 0) {
				cboApplyFormat.addItem("50");
				cboApplyFormat.addItem("9A");
				cboApplyFormat.addItem("9B");
				cboApplyFormat.addItem("91");
				cboApplyFormat.addItem("92");
			}
		}
		return cboApplyFormat;
	}

	public void setCboApplyFormat(UIComboBox cboApplyFormat) {
		this.cboApplyFormat = cboApplyFormat;
	}

	public LoginContext getContext() {
		return context;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}

}
