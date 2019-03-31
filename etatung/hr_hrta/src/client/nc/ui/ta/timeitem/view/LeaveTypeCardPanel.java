package nc.ui.ta.timeitem.view;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ITimeItemQueryMaintain;
import nc.ui.hr.formula.FormulaAppModelContainer;
import nc.ui.hr.formula.HRFormulaEditorDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.beans.UITextAreaScrollPane;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.border.UITitledBorder;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.util.ColumnLayout;
import nc.ui.pub.bill.BillListData;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.tools.BannerDialog;
import nc.ui.ta.pub.standardpsntemplet.PsnTempletUtils;
import nc.ui.ta.timeitem.model.TimeItemAppModel;
import nc.ui.uif2.UIState;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.MultiLangText;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.basedoc.RefDefVOWrapper;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;
import nc.vo.uif2.LoginContext;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class LeaveTypeCardPanel extends TimeItemCardPanel {
	private UIComboBox leavesetperiod;
	private UIComboBox leavesettlement;
	private UIComboBox leavescale;
	private UILabel timeUnitLabel;
	private UITextField timeunit;
	private UIComboBox gxcomtype;
	private UICheckBox isleavelimit;
	private UICheckBox isrestrictlimit;
	private UICheckBox isleaveapptimelimit;
	private UITextField leaveapptimelimit;
	private UICheckBox isleave;
	private UITextField leaveextendcount;
	private UIButton formulaset;
	private UIButton dependset;
	private UITextAreaScrollPane formulastr;
	private String formula;
	private UIComboBox convertrule;
	private HRFormulaEditorDialog editorDialog;
	private EditDependDialog editDependDialog;
	private BillListPanel dependPanel;
	private UIPanel ruleUnionPanel;
	private ITimeItemQueryMaintain queryMaintain;
	private UIPanel basicPanel;
	private UIPanel controlPanel;
	private UICheckBox isleaveTransfer;
	private UICheckBox ishrssshow;
	private UITextField showorder;
	private UILabel convertRuleLabel;
	private UILabel leavescaleLabel;
	private UIPanel formulaSetPanel;
	private UIPanel dependSetPanel;
	private UILabel timeItemUnitLabel;

	public LeaveTypeCardPanel() {
		buildPanel();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == getIsleaveapptimelimit()) {
			getLeaveapptimelimit().setEnabled(
					getIsleaveapptimelimit().isSelected());
			return;
		}
		if (e.getSource() == getIsleave()) {
			getLeaveextendcount().setEnabled(getIsleave().isSelected());
			return;
		}
		if (e.getSource() == getIsLeavelimit()) {
			getIsrestrictlimit().setSelected(false);
			getIsrestrictlimit().setEnabled(getIsLeavelimit().isSelected());
			return;
		}
		if (e.getSource() == getFormulaset()) {
			doFormulaSet();
			return;
		}
		if (e.getSource() == getDependset()) {
			doDependSet();
			return;
		}
	}

	public void clearData() {
		super.clearData();
		setFormula(null);
		getFormulastr().setText(null);
		getDependPanel().getHeadBillModel().clearBodyData();
	}

	public void doFormulaSet() {
		final BannerDialog dialog = new BannerDialog(getModel().getContext()
				.getEntranceUI());
		SwingWorker<Object, Object> worker = new SwingWorker() {
			HRFormulaEditorDialog dlg = null;

			protected void done() {
				dialog.end();
				this.dlg.showModal();
				if (this.dlg.getResult() == 1) {
					LeaveTypeCardPanel.this.getFormulastr().setText(
							this.dlg.getBusinessLang());
					LeaveTypeCardPanel.this
							.setFormula(this.dlg.getScriptLang());
				}
			}

			protected Object doInBackground() throws Exception {
				try {
					this.dlg = LeaveTypeCardPanel.this.getEditorDialog();
					this.dlg.setFormulaDesc(LeaveTypeCardPanel.this
							.getFormulastr().getText());
				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
					dialog.end();
					throw e;
				}
				return null;
			}
		};
		worker.execute();
		if (!worker.isDone()) {
			dialog.start();
		}
	}

	public HRFormulaEditorDialog getEditorDialog() {
		if (this.editorDialog == null) {
			FormulaAppModelContainer.setModel(getModel());
			this.editorDialog = new HRFormulaEditorDialog(this,
					"nc/ui/ta/timeitem/leaverule_formula.xml");
		}
		return this.editorDialog;
	}

	private void doDependSet() {
		RefDefVOWrapper<TimeItemVO> wrapper = null;
		try {
			wrapper = getQueryMaintain().queryDependLeaveDefVOs(
					getModel().getContext(), getDependleavetypes(),
					getPk_timeitemcopy(),
					getLeavesetperiod().getSelectedIndex());
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}
		getEditDependDialog().setLeftAndRightData(wrapper);
		getEditDependDialog().showModal();
	}

	public EditDependDialog getEditDependDialog() {
		if (this.editDependDialog == null) {
			this.editDependDialog = new EditDependDialog(this);
			this.editDependDialog.setLeavePanel(this);
			this.editDependDialog.initUI();
		}
		return this.editDependDialog;
	}

	public ITimeItemQueryMaintain getQueryMaintain() {
		if (this.queryMaintain == null) {
			this.queryMaintain = ((ITimeItemQueryMaintain) NCLocator
					.getInstance().lookup(ITimeItemQueryMaintain.class));
		}
		return this.queryMaintain;
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == getTimeitemunit()) {
			getTimeUnitLabel().setText(
					PublicLangRes.MINTIMEUNIT(getTimeitemunit()
							.getSelectedIndex()));
			getTimeunit().setValue(UFDouble.ZERO_DBL);
			if (getTimeitemunit().getSelectedIndex() == 0) {
				getTimeItemUnitLabel().setText(
						PublicLangRes.MINTIMEUNIT(getTimeitemunit()
								.getSelectedIndex()));
				getTimeunit().setMaxValue(1);
				if (getModel().getUiState() != UIState.NOT_EDIT) {
					getConvertrule().setEnabled(true);
				}
				return;
			}
			getTimeItemUnitLabel().setText(
					PublicLangRes.MINTIMEUNIT(getTimeitemunit()
							.getSelectedIndex()));
			getTimeunit().setMaxValue(1440);

			getConvertrule().setEnabled(false);
			getConvertrule().setSelectedItem(null);
			return;
		}
		if (e.getSource() == getLeavesetperiod()) {
			if (getLeavesetperiod().getSelectedIndex() == 0) {
				getLeavescale().setEnabled(false);
				getLeavescale().setSelectedItem(Integer.valueOf(1));
			} else if (getModel().getUiState() != UIState.NOT_EDIT) {
				getLeavescale().setEnabled(true);
			}
			return;
		}
	}

	public UIPanel getUpPanel() {
		UIPanel upPanel = new UIPanel();
		upPanel.setLayout(new ColumnLayout());
		FormLayout layout = new FormLayout("left:pref", "");

		DefaultFormBuilder builder = new DefaultFormBuilder(layout, upPanel);

		builder.append(getBasicPanel());
		builder.nextLine();
		builder.append(getControlPanel());
		builder.nextLine();
		builder.append(getNotePanel());
		builder.nextLine();

		builder.append(getRuleUnionPanel());
		getShoworder();
		return upPanel;
	}

	public UIPanel getBasicPanel() {
		if (this.basicPanel == null) {
			this.basicPanel = new UIPanel();

			FormLayout layout = new FormLayout(this.colwidth, "");
			DefaultFormBuilder builder = new DefaultFormBuilder(layout,
					this.basicPanel);

			builder.append(PublicLangRes.CODE());
			builder.append(getTimeitemcode());

			builder.append(PublicLangRes.NAME());
			builder.append(getTimeitemname());

			builder.append(ResHelper.getString("common", "UC001-0000118"));
			builder.append(getEnablestate());

			builder.append(PublicLangRes.COMPUNIT());
			builder.append(getTimeitemunit());
		}
		return this.basicPanel;
	}

	public UIPanel getControlPanel() {
		if (this.controlPanel == null) {
			this.controlPanel = new UIPanel();
			FormLayout layout = new FormLayout(this.colwidth, "");
			DefaultFormBuilder builder = new DefaultFormBuilder(layout,
					this.controlPanel);

			builder.append(PublicLangRes.CONVERTRULE());
			builder.append(getConvertrule());

			builder.append(ResHelper.getString("6017basedoc",
					"06017basedoc1882"));
			builder.append(getLeavesetperiod());

			UILabel settlementLabel = new UILabel(ResHelper.getString(
					"6017basedoc", "06017basedoc1928"));
			builder.append(settlementLabel);
			builder.append(getLeavesettlement());

			UILabel leaveCalLabek = new UILabel(ResHelper.getString(
					"6017basedoc", "06017basedoc1517"));
			builder.append(leaveCalLabek);
			builder.append(getLeavescale());

			builder.append(getTimeUnitLabel());
			builder.append(getTimeunit());

			builder.append(PublicLangRes.ROUNDMODE());
			builder.append(getRoundmode());

			UILabel gxLabel = new UILabel(PublicLangRes.GXCOMPTYPE());
			builder.append(gxLabel);
			builder.append(getGxcomtype());

			builder.append("");
			builder.append(getIsleaveapptimelimit(), 3);
			builder.append(getLeaveapptimelimit());
			builder.nextLine();

			builder.append("");
			builder.append(getIsLeavelimit(), 3);
			builder.append(getIsrestrictlimit());

			builder.append("");
			builder.append(getIsLeaveTransfer());
			builder.nextLine();

			builder.append("");
			builder.append(getIsleave(), 3);
			builder.append(getLeaveextendcount());
			builder.nextLine();

			builder.append("");
			builder.append(getIshrssshow(), 4);
		}
		return this.controlPanel;
	}

	public UILabel getTimeUnitLabel() {
		if (this.timeUnitLabel == null) {
			this.timeUnitLabel = new UILabel(PublicLangRes.MINTIMEUNIT());
		}
		return this.timeUnitLabel;
	}

	public UILabel getConvertRuleLabel() {
		if (this.convertRuleLabel == null) {
			this.convertRuleLabel = new UILabel(PublicLangRes.CONVERTRULE());
		}
		return this.convertRuleLabel;
	}

	public UILabel getLeavescaleLabel() {
		if (this.leavescaleLabel == null) {
			this.leavescaleLabel = new UILabel(ResHelper.getString(
					"6017basedoc", "06017basedoc1517"));
		}
		return this.leavescaleLabel;
	}

	public UIPanel getRuleUnionPanel() {
		if (this.ruleUnionPanel == null) {
			this.ruleUnionPanel = new UIPanel();
			FormLayout layout = new FormLayout("left:pref", "");

			DefaultFormBuilder builder = new DefaultFormBuilder(layout,
					this.ruleUnionPanel);

			builder.append(getFormulaSetPanel());
			builder.nextLine();
			builder.append(getDependSetPanel());
			builder.nextLine();
		}
		return this.ruleUnionPanel;
	}

	public UIPanel getFormulaSetPanel() {
		if (this.formulaSetPanel == null) {
			this.formulaSetPanel = new UIPanel();
			this.formulaSetPanel.setBorder(new UITitledBorder(ResHelper
					.getString("6017basedoc", "06017basedoc1518")));

			String colwidth = "right:105px,5px,fill:185px,5px,right:130px,5px,fill:185px,5px,right:130px,5px,fill:185px,5px";
			FormLayout layout = new FormLayout(colwidth, "");
			DefaultFormBuilder builder = new DefaultFormBuilder(layout,
					this.formulaSetPanel);

			builder.append(getFormulaset());
			builder.append(getFormulastr(), 9);
		}
		return this.formulaSetPanel;
	}

	public UIPanel getDependSetPanel() {
		if (this.dependSetPanel == null) {
			this.dependSetPanel = new UIPanel();
			this.dependSetPanel.setBorder(new UITitledBorder(ResHelper
					.getString("6017basedoc", "06017basedoc1519")));

			String colwidth = "right:105px,5px,fill:185px,5px,right:130px,5px,fill:185px,5px,right:130px,5px,fill:185px,5px";
			FormLayout layout = new FormLayout(colwidth, "");
			DefaultFormBuilder builder = new DefaultFormBuilder(layout,
					this.dependSetPanel);

			builder.append(getDependset());
			builder.append(getDependPanel(), 9);
			builder.nextLine();
		}
		return this.dependSetPanel;
	}

	public BillListPanel getDependPanel() {
		if (this.dependPanel == null) {
			this.dependPanel = new BillListPanel();
			List<BillTempletBodyVO> retList = new ArrayList();
			int order = 1000;

			BillTempletBodyVO bodyVO = PsnTempletUtils
					.createDefaultBillTempletBodyVO(0, order++);
			retList.add(bodyVO);
			bodyVO.setDatatype(Integer.valueOf(0));
			bodyVO.setNullflag(Boolean.valueOf(false));
			bodyVO.setShowflag(Boolean.valueOf(false));
			bodyVO.setItemkey("pk_timeitem");
			bodyVO.setMetadataproperty(null);
			bodyVO.setMetadatapath(null);

			bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(0, order++);
			retList.add(bodyVO);
			bodyVO.setDatatype(Integer.valueOf(0));
			bodyVO.setDefaultshowname(ResHelper.getString("6017basedoc",
					"06017basedoc1520"));

			bodyVO.setNullflag(Boolean.valueOf(false));
			bodyVO.setItemkey("timeitemcode");
			bodyVO.setMetadataproperty(null);
			bodyVO.setMetadatapath(null);

			bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(0, order++);
			retList.add(bodyVO);
			bodyVO.setDatatype(Integer.valueOf(17));
			bodyVO.setDefaultshowname(ResHelper.getString("6017basedoc",
					"06017basedoc1521"));

			bodyVO.setNullflag(Boolean.valueOf(false));
			bodyVO.setItemkey("timeitemname");
			bodyVO.setMetadataproperty(null);
			bodyVO.setMetadatapath(null);

			BillTempletVO btv = new BillTempletVO();
			btv.setChildrenVO((CircularlyAccessibleValueObject[]) retList
					.toArray(new BillTempletBodyVO[0]));

			this.dependPanel.setListData(new BillListData(btv));
			this.dependPanel.getChildListPanel().setBBodyMenuShow(false);
			this.dependPanel.getHeadTable().setColumnWidth(
					new int[] { 110, 110 });
			this.dependPanel.setPreferredSize(new Dimension(270, 110));
			this.dependPanel.getHeadTable().setSortEnabled(false);
		}
		return this.dependPanel;
	}

	public UILabel getTimeItemUnitLabel() {
		if (this.timeItemUnitLabel == null) {
			this.timeItemUnitLabel = new UILabel(PublicLangRes.MINTIMEUNIT());
		}
		return this.timeItemUnitLabel;
	}

	public UIComboBox getLeavesetperiod() {
		if (this.leavesetperiod == null) {
			this.leavesetperiod = new UIComboBox();

			this.leavesetperiod.addItem(new DefaultConstEnum(
					Integer.valueOf(0), ResHelper.getString("6017basedoc",
							"06017basedoc1929")));

			this.leavesetperiod.addItem(new DefaultConstEnum(
					Integer.valueOf(1), ResHelper.getString("6017basedoc",
							"06017basedoc1930")));

			this.leavesetperiod.addItem(new DefaultConstEnum(
					Integer.valueOf(2), ResHelper.getString("6017basedoc",
							"06017basedoc1931")));
			
			//BEGIN 张恒    将65年资起算日的整合到63   2018/8/28
			// ssx added on 2018-03-16
			// for changes of start date of company age
			this.leavesetperiod.addItem(new DefaultConstEnum(
					TimeItemCopyVO.LEAVESETPERIOD_STARTDATE, "按特休起算日結算"));
			//END 张恒    将65年资起算日的整合到63   2018/8/28

			getComponentList().add(this.leavesetperiod);
			this.leavesetperiod.addItemListener(this);
		}
		return this.leavesetperiod;
	}

	public UIComboBox getLeavesettlement() {
		if (this.leavesettlement == null) {
			this.leavesettlement = new UIComboBox();
			this.leavesettlement.addItem(new DefaultConstEnum(Integer
					.valueOf(0), ResHelper.getString("6017basedoc",
					"06017basedoc1523")));

			this.leavesettlement.addItem(new DefaultConstEnum(Integer
					.valueOf(1), ResHelper.getString("6017basedoc",
					"06017basedoc1524")));

			this.leavesettlement.addItem(new DefaultConstEnum(Integer
					.valueOf(2), ResHelper.getString("6017basedoc",
					"06017basedoc1525")));

			getComponentList().add(this.leavesettlement);
		}
		return this.leavesettlement;
	}

	public UIComboBox getLeavescale() {
		if (this.leavescale == null) {
			this.leavescale = new UIComboBox();
			this.leavescale.addItem(new DefaultConstEnum(Integer.valueOf(0),
					ResHelper.getString("6017basedoc", "06017basedoc1526")));

			this.leavescale.addItem(new DefaultConstEnum(Integer.valueOf(1),
					ResHelper.getString("6017basedoc", "06017basedoc1527")));

			getComponentList().add(this.leavescale);
		}
		return this.leavescale;
	}

	public UIComboBox getGxcomtype() {
		if (this.gxcomtype == null) {
			this.gxcomtype = new UIComboBox();
			this.gxcomtype.addItem(new DefaultConstEnum(Integer.valueOf(0),
					ResHelper.getString("6017basedoc", "06017basedoc1528")));

			this.gxcomtype.addItem(new DefaultConstEnum(Integer.valueOf(1),
					ResHelper.getString("6017basedoc", "06017basedoc1529")));

			getComponentList().add(this.gxcomtype);
		}
		return this.gxcomtype;
	}

	public UICheckBox getIsLeavelimit() {
		if (this.isleavelimit == null) {
			this.isleavelimit = new UICheckBox(ResHelper.getString(
					"6017basedoc", "06017basedoc1530"));

			this.isleavelimit.addActionListener(this);
			getComponentList().add(this.isleavelimit);
		}
		return this.isleavelimit;
	}

	public UICheckBox getIsrestrictlimit() {
		if (this.isrestrictlimit == null) {
			this.isrestrictlimit = new UICheckBox(ResHelper.getString(
					"6017basedoc", "06017basedoc1531"));

			getComponentList().add(this.isrestrictlimit);
		}
		return this.isrestrictlimit;
	}

	public UICheckBox getIsleaveapptimelimit() {
		if (this.isleaveapptimelimit == null) {
			this.isleaveapptimelimit = new UICheckBox(ResHelper.getString(
					"6017basedoc", "06017basedoc1823"));

			getComponentList().add(this.isleaveapptimelimit);
			this.isleaveapptimelimit.addActionListener(this);
		}
		return this.isleaveapptimelimit;
	}

	public UITextField getLeaveapptimelimit() {
		if (this.leaveapptimelimit == null) {
			this.leaveapptimelimit = new UITextField();
			this.leaveapptimelimit.setTextType("TextInt");
			this.leaveapptimelimit.setMinValue(0);
			getComponentList().add(this.leaveapptimelimit);
		}
		return this.leaveapptimelimit;
	}

	public UICheckBox getIsleave() {
		if (this.isleave == null) {
			this.isleave = new UICheckBox(ResHelper.getString("6017basedoc",
					"06017basedoc1533"));

			getComponentList().add(this.isleave);
			this.isleave.addActionListener(this);
		}
		return this.isleave;
	}

	public UIComboBox getConvertrule() {
		if (this.convertrule == null) {
			this.convertrule = new UIComboBox();
			this.convertrule.addItem(new DefaultConstEnum(Integer.valueOf(0),
					ResHelper.getString("6017basedoc", "06017basedoc1534")));

			this.convertrule.addItem(new DefaultConstEnum(Integer.valueOf(1),
					ResHelper.getString("6017basedoc", "06017basedoc1535")));

			getComponentList().add(this.convertrule);
		}
		return this.convertrule;
	}

	public UITextField getTimeunit() {
		if (this.timeunit == null) {
			this.timeunit = new UITextField();
			this.timeunit.setTextType("TextDbl");
			this.timeunit.setNumPoint(2);
			this.timeunit.setMinValue(0);
			getComponentList().add(this.timeunit);
		}
		return this.timeunit;
	}

	public UITextField getLeaveextendcount() {
		if (this.leaveextendcount == null) {
			this.leaveextendcount = new UITextField();
			this.leaveextendcount.setTextType("TextInt");
			this.leaveextendcount.setMinValue(0);
			this.leaveextendcount.setMaxValue(1000);
			getComponentList().add(this.leaveextendcount);
		}
		return this.leaveextendcount;
	}

	public UIButton getDependset() {
		if (this.dependset == null) {
			this.dependset = new UIButton();
			this.dependset.setText(PublicLangRes.SET());

			getComponentList().add(this.dependset);
			this.dependset.addActionListener(this);
		}
		return this.dependset;
	}

	public UIButton getFormulaset() {
		if (this.formulaset == null) {
			this.formulaset = new UIButton();

			this.formulaset.setText(PublicLangRes.SET());

			getComponentList().add(this.formulaset);
			this.formulaset.addActionListener(this);
		}
		return this.formulaset;
	}

	public UITextAreaScrollPane getFormulastr() {
		if (this.formulastr == null) {
			this.formulastr = new UITextAreaScrollPane();
			this.formulastr.setPreferredSize(new Dimension(500, 40));
			this.formulastr.setEnabled(false);
		}
		return this.formulastr;
	}

	public void setFormulaDesc(String formula) {
		if (StringUtils.isBlank(formula)) {
			return;
		}
		String formulaDesc = getEditorDialog().getBusinessLang(formula);
		getFormulastr().setText(formulaDesc);
	}

	public String getFormula() {
		return this.formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getDependleavetypes() {
		BillModel billModel = getDependPanel().getHeadBillModel();
		int rowCount = billModel.getRowCount();
		if (rowCount <= 0) {
			return null;
		}
		StringBuffer dependBuffer = new StringBuffer();
		for (int i = 0; i < rowCount; i++) {
			String pk_timeitem = (String) billModel
					.getValueAt(i, "pk_timeitem");
			dependBuffer.append(pk_timeitem).append(",");
		}
		return dependBuffer.substring(0, dependBuffer.length() - 1).toString();
	}

	public void setDependleavetypes(TimeItemVO[] dependleavetypes) {
		BillModel billModel = getDependPanel().getHeadBillModel();
		billModel.clearBodyData();
		if (ArrayUtils.isEmpty(dependleavetypes)) {
			return;
		}
		billModel.addLine(dependleavetypes.length);
		for (int i = 0; i < dependleavetypes.length; i++) {
			billModel.setValueAt(dependleavetypes[i].getPk_timeitem(), i,
					"pk_timeitem");
			billModel.setValueAt(dependleavetypes[i].getTimeitemcode(), i,
					"timeitemcode");
			MultiLangText text = new MultiLangText();
			text.setText(dependleavetypes[i].getTimeitemname());
			text.setText2(dependleavetypes[i].getTimeitemname2());
			text.setText3(dependleavetypes[i].getTimeitemname3());
			text.setText4(dependleavetypes[i].getTimeitemname4());
			text.setText5(dependleavetypes[i].getTimeitemname5());
			text.setText6(dependleavetypes[i].getTimeitemname6());
			billModel.setValueAt(text, i, "timeitemname");
		}
	}

	public UICheckBox getIsLeaveTransfer() {
		if (this.isleaveTransfer == null) {
			this.isleaveTransfer = new UICheckBox(ResHelper.getString(
					"6017basedoc", "06017basedoc1883"));
			getComponentList().add(this.isleaveTransfer);
		}
		return this.isleaveTransfer;
	}

	public UICheckBox getIshrssshow() {
		if (this.ishrssshow == null) {
			this.ishrssshow = new UICheckBox(ResHelper.getString("6017basedoc",
					"06017basedoc1926"));
			getComponentList().add(this.ishrssshow);
		}
		return this.ishrssshow;
	}

	public UITextField getShoworder() {
		if (this.showorder == null) {
			this.showorder = new UITextField();
			this.showorder.setTextType("TextInt");
			this.showorder.setMinValue(0);
			this.showorder.setMaxValue(1000);
			getComponentList().add(this.showorder);
		}
		return this.showorder;
	}
}
