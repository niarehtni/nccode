package nc.ui.wa.datainterface;

import java.awt.Container;
import java.util.ArrayList;

import javax.swing.border.Border;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.wa.datainterface.IReportExportService;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.ui.hr.frame.dialog.HrDialog;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.pf.GroupLayout;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;

/**
 * 個稅申報表生成申報檔對話框
 */
public class ExportTextDlg4TW extends HrDialog {

    /**
     * serial version
     */
    private static final long serialVersionUID = -4292706044705083569L;
    private String wa_class = ""; // 薪資方案
    private String[] wa_classes = null; // 薪資方案列表
    private String start_period = ""; // 起始期間
    private String end_period = ""; // 截止期間
    private String vouchType = "";// 憑單類型
    private String[] pk_orgs = null; // 組織清單
    private String vatNumber = null; // 統一編號
    private String cyear = null;// 薪資年度

    private UILabel lblStartPeriod;

    private UILabel lblEndPeriod;
    private UILabel lblWaClass;
    private UILabel lblAccYear;
    private UILabel lblVoucherType;
    private UILabel lblSumReport;
    private UILabel lblUniqCode;

    private UIComboBox cboEndPeriod;

    private UIComboBox cboStartPeriod;
    private UIComboBox cboAccYear;
    private UIComboBox cboVoucherType;
    private UIRefPane refWaClass;
    private UICheckBox chkSumReport;
    private UIComboBox cboUniqCode;

    private LoginContext context;
    private boolean isWaclass = true;

    /**
     * @param parent
     */
    public ExportTextDlg4TW(Container parent, String title, LoginContext context, boolean enableWaclass,
	    String initWaClass) {
	super(parent);
	super.setTitle(title);
	this.isWaclass = enableWaclass;
	this.refWaClass.setPK(initWaClass);
	setSize(300, 270);
	setContext(context);
    }

    private void loadComboBox() throws BusinessException {
	IReportExportService exportSrv = (IReportExportService) NCLocator.getInstance().lookup(
		IReportExportService.class);
	initSumComps(exportSrv);
	initWaCalss(getOrgsByVAT(exportSrv));
	initCboVoucherType();
	initCboYear();
	this.getRefWaClass().setVisible(this.isWaclass);
	this.getLblWaClass().setVisible(this.isWaclass);
    }

    private String[] getOrgsByVAT(IReportExportService exportSrv) throws BusinessException {
	String[] pk_orgs = null;
	if (this.getChkSumReport().isSelected()) {
	    if (!StringUtils.isEmpty((String) this.getCboUniqCode().getSelectdItemValue())) {
		pk_orgs = exportSrv.getAllOrgByVATNumber((String) this.getCboUniqCode().getSelectdItemValue());
	    }
	} else {
	    pk_orgs = new String[] { this.getContext().getPk_org() };
	}

	this.setPk_orgs(pk_orgs);
	return pk_orgs;
    }

    private void initWaCalss(String[] pk_orgs) {
	IUAPQueryBS qry = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());

	if (this.isWaclass) {
	    ((WaClassRefModel4ITR) this.getRefWaClass().getRefModel()).setPk_orgs(pk_orgs);
	    this.getRefWaClass().setMultiSelectedEnabled(true);
	}

	try {
	    initYearPeriods(pk_orgs, qry);
	} catch (BusinessException e) {
	    MessageDialog.showErrorDlg(this, ResHelper.getString("twhr_datainterface", "DataInterface-00060"),
		    e.getMessage()); // 錯誤
	}
    }

    private void initYearPeriods(String[] pk_orgs, IUAPQueryBS qry) throws BusinessException {
	String orgsInString = "";
	for (String org : pk_orgs) {
	    if (orgsInString != "") {
		orgsInString += ",'" + org + "'";
	    } else {
		orgsInString = "'" + org + "'";
	    }
	}
	String sql = "SELECT DISTINCT wadata.cyear + wadata.cperiod" + " FROM   wa_data AS wadata"
		+ "  INNER JOIN wa_periodstate AS stat ON stat.pk_wa_class = wadata.pk_wa_class "
		+ " WHERE  ( stat.pk_org in (" + orgsInString + ")) AND stat.payoffflag='Y' AND stat.cpaydate != ''"
		+ " ORDER BY wadata.cyear + wadata.cperiod";
	ArrayList<Object[]> rslt = (ArrayList<Object[]>) qry.executeQuery(sql, new ArrayListProcessor());
	if (rslt != null) {
	    for (Object[] r : rslt) {
		this.getCboStartPeriod().addItem(r[0]);
		this.getCboEndPeriod().addItem(r[0]);

		String cyear = ((String) r[0]).substring(0, 4);
		if (this.getCboAccYear().getItemIndexByName(cyear) == -1) {
		    this.getCboAccYear().addItem(cyear);
		}
	    }
	}
    }

    private void initSumComps(IReportExportService exportSrv) throws BusinessException {
	if (this.getChkSumReport().getListeners(CheckChangeHandler.class).length == 0) {
	    this.getChkSumReport().addActionListener(
		    new CheckChangeHandler(this.getCboUniqCode(), this.getContext().getPk_org(),
			    ((WaClassRefModel4ITR) this.getRefWaClass().getRefModel())));
	}

	if (this.getCboUniqCode().getListeners(VATChangeHandler.class).length == 0) {
	    this.getCboUniqCode().addActionListener(
		    new VATChangeHandler(this.getChkSumReport(), this.getContext().getPk_org(),
			    ((WaClassRefModel4ITR) this.getRefWaClass().getRefModel())));
	}

	if (StringUtils.isEmpty(this.getContext().getPk_org())) {
	    String[] vatnos = null;
	    vatnos = exportSrv.getAllOrgVATNumber();
	    this.getCboUniqCode().removeAllItems();
	    this.getChkSumReport().setSelected(true);
	    if (vatnos != null && vatnos.length > 0) {
		this.getCboUniqCode().addItems(vatnos);
	    }
	    this.getCboUniqCode().setEnabled(true);
	} else {
	    this.getCboUniqCode().removeAllItems();
	    this.getCboUniqCode().addItem(exportSrv.getOrgVATNumber(this.getContext().getPk_org()));
	    this.getCboUniqCode().setEnabled(false);
	    this.getChkSumReport().setSelected(false);
	}
    }

    private void initCboVoucherType() {
	if (this.getCboVoucherType().getItemCount() == 0) {
	    this.getCboVoucherType().addItem(ResHelper.getString("twhr_datainterface", "DataInterface-00065") + "[1]");// 免填發
	    this.getCboVoucherType().addItem(ResHelper.getString("twhr_datainterface", "DataInterface-00066") + "[2]");// 電子憑單
	    this.getCboVoucherType().addItem(ResHelper.getString("twhr_datainterface", "DataInterface-00067") + "[3]");// 紙本憑單
	}
    }

    private void initCboYear() {
	if (this.getCboAccYear().getListeners(YearChangeHandler.class).length == 0) {
	    this.getCboAccYear().addActionListener(
		    new YearChangeHandler(this.getCboStartPeriod(), this.getCboEndPeriod()));
	}

	if (this.getCboAccYear().getItemCount() > 0) {
	    this.getCboAccYear().setSelectedIndex(0);
	}
    }

    @Override
    public void closeOK() {
	start_period = StringUtils.leftPad(
		StringUtils.trimToEmpty(this.getCboStartPeriod().getSelectedItem().toString()), 4, "0");
	end_period = StringUtils.leftPad(StringUtils.trimToEmpty(this.getCboEndPeriod().getSelectedItem().toString()),
		4, "0");
	wa_class = this.getRefWaClass().getRefPK();
	wa_classes = this.getRefWaClass().getRefPKs();
	vouchType = String.valueOf(this.getCboVoucherType().getSelectedIndex() + 1);
	vatNumber = (String) this.getCboUniqCode().getSelectdItemValue();
	cyear = (String) this.getCboAccYear().getSelectdItemValue();

	IReportExportService checkSrv = NCLocator.getInstance().lookup(IReportExportService.class);
	// 檢驗申報組織是否唯一
	try {
	    checkSrv.checkExportOrg(this.getPk_orgs());
	} catch (BusinessException e1) {
	    MessageDialog.showErrorDlg(this, ResHelper.getString("twhr_datainterface", "DataInterface-00060"),
		    e1.getMessage());
	    return;
	}

	try {
	    // 校驗期間資料
	    int count = checkSrv.checkPeriodWaDataExists(this.getPk_orgs()[0], wa_classes, this.getStart_period(),
		    this.getEnd_period());

	    if (count == 0) {
		MessageDialog.showErrorDlg(this, ResHelper.getString("twhr_datainterface", "DataInterface-00060"),
			ResHelper.getString("twhr_datainterface", "DataInterface-00075"));
		return;
	    }
	} catch (BusinessException e) {
	    int btnRst = MessageDialog.showYesNoCancelDlg(this,
		    ResHelper.getString("twhr_datainterface", "DataInterface-00073"),
		    ResHelper.getString("twhr_datainterface", "DataInterface-00074") + e.getMessage());
	    if (btnRst == MessageDialog.ID_YES) {
		super.closeOK();
		return;
	    } else if (btnRst == MessageDialog.ID_CANCEL) {
		super.closeCancel();
		return;
	    } else {
		return;
	    }
	}
	super.closeOK();
    }

    protected UIPanel createCenterPanel() {
	UIPanel mypanel = new UIPanel();

	this.getRefWaClass().setRefModel(new WaClassRefModel4ITR());
	if (!StringUtils.isEmpty(this.getWa_class())) {
	    this.getRefWaClass().setPK(this.getWa_class());
	}
	this.getRefWaClass().setMultiSelectedEnabled(true);

	this.getLblWaClass().setText(ResHelper.getString("twhr_datainterface", "DataInterface-00026")); // 薪資方案
	this.getLblStartPeriod().setText(ResHelper.getString("twhr_datainterface", "DataInterface-00010"));// 起始期間
	this.getLblEndPeriod().setText(ResHelper.getString("twhr_datainterface", "DataInterface-00011"));// 截止期間
	this.getLblAccYear().setText(ResHelper.getString("twhr_datainterface", "DataInterface-00064"));
	this.getLblVoucherType().setText(ResHelper.getString("twhr_datainterface", "DataInterface-00068"));// 憑單填發方式
	this.getLblSumReport().setText("");
	this.getChkSumReport().setText(ResHelper.getString("twhr_datainterface", "DataInterface-00071"));// 法人合併申報
	this.getChkSumReport().setBorderPainted(false);
	this.getLblUniqCode().setText(ResHelper.getString("twhr_datainterface", "DataInterface-00072"));// 統一編號

	GroupLayout layout = new GroupLayout(mypanel);
	mypanel.setLayout(layout);

	layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.LEADING).add(
		layout.createSequentialGroup()
			.addContainerGap()
			.add(layout
				.createParallelGroup(GroupLayout.LEADING)
				.add(layout
					.createSequentialGroup()
					.add(this.getLblSumReport())
					.addPreferredGap(0)
					.add(this.getChkSumReport(), GroupLayout.PREFERRED_SIZE, 120,
						GroupLayout.PREFERRED_SIZE))
				.add(layout
					.createSequentialGroup()
					.add(this.getLblUniqCode())
					.addPreferredGap(0)
					.add(this.getCboUniqCode(), GroupLayout.PREFERRED_SIZE, 80,
						GroupLayout.PREFERRED_SIZE))
				.add(layout
					.createSequentialGroup()
					.add(this.getLblWaClass())
					.addPreferredGap(0)
					.add(this.getRefWaClass(), GroupLayout.PREFERRED_SIZE, 200,
						GroupLayout.PREFERRED_SIZE))
				.add(layout
					.createSequentialGroup()
					.add(this.getLblAccYear())
					.addPreferredGap(0)
					.add(this.getCboAccYear(), GroupLayout.PREFERRED_SIZE, 80,
						GroupLayout.PREFERRED_SIZE))
				.add(layout
					.createSequentialGroup()
					.add(this.getLblStartPeriod())
					.addPreferredGap(0)
					.add(this.getCboStartPeriod(), GroupLayout.PREFERRED_SIZE, 80,
						GroupLayout.PREFERRED_SIZE))
				.add(layout
					.createSequentialGroup()
					.add(this.getLblEndPeriod())
					.addPreferredGap(0)
					.add(this.getCboEndPeriod(), GroupLayout.PREFERRED_SIZE, 80,
						GroupLayout.PREFERRED_SIZE))
				.add(layout
					.createSequentialGroup()
					.add(this.getLblVoucherType())
					.addPreferredGap(0)
					.add(this.getCboVoucherType(), GroupLayout.PREFERRED_SIZE, 80,
						GroupLayout.PREFERRED_SIZE))).addContainerGap(220, Short.MAX_VALUE)));
	layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.LEADING).add(
		layout.createSequentialGroup()
			.addContainerGap()
			.add(layout
				.createParallelGroup(GroupLayout.BASELINE)
				.add(this.getLblSumReport())
				.add(this.getChkSumReport(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
					GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(0)
			.add(layout
				.createParallelGroup(GroupLayout.BASELINE)
				.add(this.getLblUniqCode())
				.add(this.getCboUniqCode(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
					GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(0)
			.add(layout
				.createParallelGroup(GroupLayout.BASELINE)
				.add(this.getLblWaClass())
				.add(this.getRefWaClass(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
					GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(0)
			.add(layout
				.createParallelGroup(GroupLayout.BASELINE)
				.add(this.getLblAccYear())
				.add(this.getCboAccYear(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
					GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(0)
			.add(layout
				.createParallelGroup(GroupLayout.BASELINE)
				.add(this.getLblStartPeriod())
				.add(this.getCboStartPeriod(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
					GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(0)
			.add(layout
				.createParallelGroup(GroupLayout.BASELINE)
				.add(this.getLblEndPeriod())
				.add(layout.createParallelGroup(GroupLayout.BASELINE).add(this.getCboEndPeriod(),
					GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
					GroupLayout.PREFERRED_SIZE)))
			.addPreferredGap(0)
			.add(layout
				.createParallelGroup(GroupLayout.BASELINE)
				.add(this.getLblVoucherType())
				.add(this.getCboVoucherType(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
					GroupLayout.PREFERRED_SIZE)).addContainerGap(242, Short.MAX_VALUE)));

	return mypanel;
    }

    public String getStart_period() {
	return start_period;
    }

    public void setStart_period(String start_period) {
	this.start_period = start_period;
    }

    public String getEnd_period() {
	return end_period;
    }

    public void setEnd_period(String end_period) {
	this.end_period = end_period;
    }

    public String getWa_class() {
	return wa_class;
    }

    public void setWa_class(String wa_class) {
	this.wa_class = wa_class;
    }

    public boolean isWaclass() {
	return isWaclass;
    }

    public void loadData() throws BusinessException {
	this.loadComboBox();
    }

    public String[] getWa_classes() {
	return wa_classes;
    }

    public LoginContext getContext() {
	return context;
    }

    public void setContext(LoginContext context) {
	this.context = context;
    }

    public UILabel getLblVoucherType() {
	if (this.lblVoucherType == null) {
	    this.lblVoucherType = new UILabel();
	}
	return lblVoucherType;
    }

    public void setLblVoucherType(UILabel lblVoucherType) {
	this.lblVoucherType = lblVoucherType;
    }

    public UIComboBox getCboVoucherType() {
	if (this.cboVoucherType == null) {
	    this.cboVoucherType = new UIComboBox();
	}
	return cboVoucherType;
    }

    public void setCboVoucherType(UIComboBox cboVoucherType) {
	this.cboVoucherType = cboVoucherType;
    }

    public String getVouchType() {
	return vouchType;
    }

    public void setVouchType(String vouchType) {
	this.vouchType = vouchType;
    }

    public UILabel getLblSumReport() {
	if (this.lblSumReport == null) {
	    this.lblSumReport = new UILabel();
	}
	return lblSumReport;
    }

    public void setLblSumReport(UILabel lblSumReport) {
	this.lblSumReport = lblSumReport;
    }

    public UILabel getLblUniqCode() {
	if (this.lblUniqCode == null) {
	    this.lblUniqCode = new UILabel();
	}
	return lblUniqCode;
    }

    public void setLblUniqCode(UILabel lblUniqCode) {
	this.lblUniqCode = lblUniqCode;
    }

    public UICheckBox getChkSumReport() {
	if (this.chkSumReport == null) {
	    this.chkSumReport = new UICheckBox() {
		@Override
		public void setBorder(Border border) {
		}
	    };
	}
	return chkSumReport;
    }

    public void setChkSumReport(UICheckBox chkSumReport) {
	this.chkSumReport = chkSumReport;
    }

    public UIComboBox getCboUniqCode() {
	if (this.cboUniqCode == null) {
	    this.cboUniqCode = new UIComboBox();
	}
	return cboUniqCode;
    }

    public void setCboUniqCode(UIComboBox cboUniqCode) {
	this.cboUniqCode = cboUniqCode;
    }

    public UILabel getLblAccYear() {
	if (this.lblAccYear == null) {
	    this.lblAccYear = new UILabel();
	}
	return lblAccYear;
    }

    public void setLblAccYear(UILabel lblAccYear) {
	this.lblAccYear = lblAccYear;
    }

    public UIComboBox getCboAccYear() {
	if (this.cboAccYear == null) {
	    this.cboAccYear = new UIComboBox();
	}
	return cboAccYear;
    }

    public void setCboAccYear(UIComboBox cboAccYear) {
	this.cboAccYear = cboAccYear;
    }

    public UIRefPane getRefWaClass() {
	if (this.refWaClass == null) {
	    this.refWaClass = new UIRefPane();
	}

	return refWaClass;
    }

    public void setRefWaClass(UIRefPane refWaClass) {
	this.refWaClass = refWaClass;
    }

    public String[] getPk_orgs() {
	return pk_orgs;
    }

    public void setPk_orgs(String[] pk_orgs) {
	this.pk_orgs = pk_orgs;
    }

    public UIComboBox getCboEndPeriod() {
	if (this.cboEndPeriod == null) {
	    this.cboEndPeriod = new UIComboBox();
	}
	return cboEndPeriod;
    }

    public void setCboEndPeriod(UIComboBox cboEndPeriod) {
	this.cboEndPeriod = cboEndPeriod;
    }

    public UIComboBox getCboStartPeriod() {
	if (this.cboStartPeriod == null) {
	    this.cboStartPeriod = new UIComboBox();
	}
	return cboStartPeriod;
    }

    public void setCboStartPeriod(UIComboBox cboStartPeriod) {
	this.cboStartPeriod = cboStartPeriod;
    }

    public UILabel getLblStartPeriod() {
	if (this.lblStartPeriod == null) {
	    this.lblStartPeriod = new UILabel();
	}
	return lblStartPeriod;
    }

    public void setLblStartPeriod(UILabel lblStartPeriod) {
	this.lblStartPeriod = lblStartPeriod;
    }

    public UILabel getLblEndPeriod() {
	if (this.lblEndPeriod == null) {
	    this.lblEndPeriod = new UILabel();
	}
	return lblEndPeriod;
    }

    public void setLblEndPeriod(UILabel lblEndPeriod) {
	this.lblEndPeriod = lblEndPeriod;
    }

    public UILabel getLblWaClass() {
	if (this.lblWaClass == null) {
	    this.lblWaClass = new UILabel();
	}
	return lblWaClass;
    }

    public void setLblWaClass(UILabel lblWaClass) {
	this.lblWaClass = lblWaClass;
    }

    public String getVatNumber() {
	return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
	this.vatNumber = vatNumber;
    }

    public String getCyear() {
	return cyear;
    }

    public void setCyear(String cyear) {
	this.cyear = cyear;
    }

}
