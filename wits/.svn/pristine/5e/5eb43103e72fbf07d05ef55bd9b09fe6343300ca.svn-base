package nc.ui.hi.psninfo.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.infoset.IInfoSetQry;
import nc.ui.hi.listrep.view.ReportExportFileFilter;
import nc.ui.hi.psninfo.action.OkAction;
import nc.ui.hi.psninfo.model.ExportUtilForPsnInfo;
import nc.ui.hr.frame.dialog.HrDialog;
import nc.ui.hr.frame.util.IconUtils;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRadioButton;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class ExpPsnInfosDlg extends HrDialog {
	private UIPanel backGroundPanel = null;
	private UIPanel northPanel = null;

	private UIPanel conditionPanel = null;

	private UIRadioButton allRbtn;
	private UIRadioButton lastRbtn;
	private UIPanel pathPanel;
	private UITextField pathField = null;
	private UIButton appointPathBtn = null;

	private ListToListPanel listToListPanel = null;

	private ArrayList<Object> infoSetData = null;

	private static String oldPath = "C:\\";

	private final String[] psnPks;

	private final String pk_org;
	private int queryMode = 0;

	private AbstractUIAppModel model = null;
	private LoginContext context;
	private static BillTempletVO billTemletVO;
	private Container parent;

	public ExpPsnInfosDlg(Container parent, String[] psnPks, String pk_org,
			AbstractUIAppModel model) {
		super(parent);
		this.parent = parent;
		setTitle(ResHelper.getString("6007psn", "06007psn0443"));
		setSize(600, 450);
		setResizable(false);
		this.model = model;
		this.psnPks = psnPks;
		this.pk_org = pk_org;
	}

	public BillTempletVO getBillTemletVO() {
		return billTemletVO;
	}

	public static void setBillTemletVO(BillTempletVO TemletVO) {
		billTemletVO = TemletVO;
	}

	public LoginContext getContext() {
		return context;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}

	public void initUI() {
		super.initUI();
		((ListToListPanel) getListToListPanel()).setLeftData(getInfoSetVOs());
	}

	private InfoSetVO[] getInfoSetVOs() {
		InfoSetVO[] infoSetVOArray = null;
		ArrayList<InfoSetVO> infoVOList = new ArrayList();
		try {
			infoSetVOArray = ((IInfoSetQry) NCLocator.getInstance().lookup(
					IInfoSetQry.class)).queryInfoSet(getContext(),
					" pk_infoset_sort  =  '1001Z710000000002XPO' " +
					// ssx added on 20171005
					// PeriodVO do not support Export in ExNHI SubInfo
							" and pk_infoset !='TWHRA21000000000DEF5' "
							//
							+ "order by main_table_flag desc,showorder ");

			String[] infoPks = getInfosetPks(infoSetVOArray);
			HashMap<String, ArrayList<String>> hashMap = getShowItemMap(infoPks);

			for (int i = 0; i < infoSetVOArray.length; i++) {
				String meta = infoSetVOArray[i].getMeta_data();
				ArrayList<String> itemList = (ArrayList) hashMap.get(meta);

				if ((itemList != null) && (!itemList.isEmpty())) {
					infoVOList.add(infoSetVOArray[i]);
				}
			}
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}

		return (InfoSetVO[]) infoVOList
				.toArray(new InfoSetVO[infoVOList.size()]);
	}

	public void closeOK() {
		String filePath = ((UITextField) getPathPanel().getComponent(1))
				.getText();

		if (StringUtils.isEmpty(filePath)) {
			MessageDialog.showErrorDlg(this, null,
					ResHelper.getString("6007psn", "06007psn0444"));

			return;
		}
		if (!filePath.endsWith(".xls")) {
			MessageDialog.showErrorDlg(this, null,
					ResHelper.getString("6007psn", "06007psn0466"));

			return;
		}
		File file = new File(filePath);
		if ((file.exists()) && (file.isFile())) {
			if (4 != MessageDialog.showYesNoDlg(this,
					ResHelper.getString("6007psn", "06007psn0501"),
					ResHelper.getString("6007psn", "06007psn0462"))) {

				return;
			}
			if (!file.delete()) {
				MessageDialog.showErrorDlg(this, null,
						ResHelper.getString("6007psn", "06007psn0465"));

				return;
			}
		}
		String[] infoSetPks = getInfoSetPks();
		if ((infoSetPks == null) || (infoSetPks.length == 0)) {
			MessageDialog.showErrorDlg(this, null,
					ResHelper.getString("6007psn", "06007psn0445"));

			return;
		}
		HashMap<String, ArrayList<String>> tNameVScolumsMap = getShowItemMap(infoSetPks);

		setOkAction(new OkAction(parent, model, filePath, infoSetPks,
				tNameVScolumsMap, psnPks, pk_org, queryMode, billTemletVO));

		super.closeOK();
	}

	private String[] getInfoSetPks() {
		ArrayList<Object> rightList = ListToListPanel.getRightDataList();
		String[] pks = new String[rightList.size()];
		for (int i = 0; i < pks.length; i++) {
			pks[i] = ((InfoSetVO) rightList.get(i)).getPk_infoset();
		}
		rightList.clear();
		return pks;
	}

	private String[] getInfosetPks(InfoSetVO[] infoSetVOs) {
		String[] infoSetPks = new String[infoSetVOs.length];
		for (int i = 0; i < infoSetVOs.length; i++) {
			InfoSetVO inforSet = infoSetVOs[i];
			String pk = inforSet.getPk_infoset();
			infoSetPks[i] = pk;
		}
		return infoSetPks;
	}

	private void onAppointBtn() {
		String strFileName = null;
		UIFileChooser digFile = null;
		if (oldPath == null) {
			digFile = new UIFileChooser();
		} else {
			digFile = new UIFileChooser(oldPath);
		}
		digFile.setFileFilter(new ReportExportFileFilter("xls", ResHelper
				.getString("6007psn", "06007psn0442") + "(*.xls)"));

		int returnVal = digFile.showOpenDialog(this);
		if (returnVal == 0) {
			strFileName = digFile.getSelectedFile().getPath();
			if (!strFileName.substring(strFileName.length() - 4,
					strFileName.length()).equalsIgnoreCase(".xls")) {
				strFileName = strFileName + ".xls";
			}
			oldPath = strFileName;
		} else {
			strFileName = "";
		}

		((UITextField) getPathPanel().getComponent(1)).setText(strFileName);
	}

	public void actionPerformed(ActionEvent evt) {
		super.actionPerformed(evt);

		if (evt.getSource() == getPathPanel().getComponent(3)) {
			onAppointBtn();
		}
		if (evt.getSource() == allRbtn) {
			setQueryMode(0);
		}
		if (evt.getSource() == lastRbtn) {
			setQueryMode(1);
		}
	}

	public UIPanel getPathPanel() {
		if (pathPanel == null) {
			pathPanel = new UIPanel();
			FlowLayout flow = new FlowLayout();
			flow.setAlignment(0);
			pathPanel.setLayout(flow);

			FormLayout layout = new FormLayout(
					"right:pref, 23dlu, left:pref,10dlu,right:pref, 10dlu, left:pref, 100dlu",
					"");

			DefaultFormBuilder builder = new DefaultFormBuilder(layout,
					pathPanel);

			builder.setBorder(BorderFactory.createTitledBorder(ResHelper
					.getString("6007psn", "06007psn0448")));

			builder.append("", getPathfield());
			builder.append("", getAppointPathBtn());
		}
		return pathPanel;
	}

	public UITextField getPathfield() {
		if (pathField == null) {
			pathField = new UITextField();
			pathField.setPreferredSize(new Dimension(424, 20));
			pathField.setText(oldPath);
		}
		return pathField;
	}

	public int getQueryMode() {
		return queryMode;
	}

	public void setQueryMode(int queryMode) {
		this.queryMode = queryMode;
	}

	public void setPathField(UITextField pathField) {
		this.pathField = pathField;
	}

	public UIButton getAppointPathBtn() {
		if (appointPathBtn == null) {
			appointPathBtn = new UIButton(ResHelper.getString("6007psn",
					"06007psn0449"));

			appointPathBtn.addActionListener(this);
			appointPathBtn.setPreferredSize(new Dimension(50, 20));
		}
		return appointPathBtn;
	}

	public UIPanel getListToListPanel() {
		if (listToListPanel == null) {
			listToListPanel = new ListToListPanel();
			listToListPanel.setName("listToList");
			listToListPanel
					.getBnR()
					.setIcon(
							IconUtils
									.getInstance()
									.getIcon(
											"themeres/ui/toolbaricons/next_page_highlight.png"));

			listToListPanel.getBnRR().setIcon(
					IconUtils.getInstance().getIcon(
							"themeres/dialog/dialog_list/last_press.png"));

			listToListPanel.getBnL().setIcon(
					IconUtils.getInstance().getIcon(
							"themeres/ui/toolbaricons/pre_page_highlight.png"));

			listToListPanel.getBnLL().setIcon(
					IconUtils.getInstance().getIcon(
							"themeres/dialog/dialog_list/first_press.png"));
		}

		return listToListPanel;
	}

	public UIPanel getConditionPanel() {
		if (conditionPanel == null) {
			conditionPanel = new UIPanel();
			conditionPanel.setPreferredSize(new Dimension(560, 80));

			ButtonGroup btnGroup = new ButtonGroup();
			btnGroup.add(getAllRbtn());
			btnGroup.add(getLastRbtn());

			FormLayout layout = new FormLayout(
					"right:pref, 20dlu, left:pref,10dlu,right:pref, 10dlu, left:pref, 100dlu",
					"");

			DefaultFormBuilder builder = new DefaultFormBuilder(layout,
					conditionPanel);

			builder.setBorder(BorderFactory.createTitledBorder(ResHelper
					.getString("6007psn", "06007psn0450")));

			builder.append("", getAllRbtn());
			builder.nextLine();
			builder.append("", getLastRbtn());
			builder.nextLine();
		}
		return conditionPanel;
	}

	public UIRadioButton getAllRbtn() {
		if (allRbtn == null) {
			allRbtn = new UIRadioButton(ResHelper.getString("6007psn",
					"06007psn0451"));

			allRbtn.setPreferredSize(new Dimension(150, 20));
			allRbtn.setSelected(true);
			allRbtn.addActionListener(this);
		}
		return allRbtn;
	}

	public UIRadioButton getLastRbtn() {
		if (lastRbtn == null) {
			lastRbtn = new UIRadioButton(ResHelper.getString("6007psn",
					"06007psn0452"));

			lastRbtn.setPreferredSize(new Dimension(150, 20));
			lastRbtn.addActionListener(this);
		}
		return lastRbtn;
	}

	public UIPanel getBackGroundPanel() {
		if (backGroundPanel == null) {
			backGroundPanel = new UIPanel();
			backGroundPanel.setPreferredSize(new Dimension(560, 450));
			backGroundPanel.setLayout(new BorderLayout());
			backGroundPanel.add(getNorthPanel(), "North");
			backGroundPanel.add(getListToListPanel(), "Center");
		}
		return backGroundPanel;
	}

	public UIPanel getNorthPanel() {
		if (northPanel == null) {
			northPanel = new UIPanel();
			northPanel.setLayout(new BorderLayout());
			northPanel.setPreferredSize(new Dimension(560, 138));
			northPanel.add(getConditionPanel(), "North");
			northPanel.add(getPathPanel(), "Center");
		}
		return northPanel;
	}

	public ArrayList<Object> getInfoSetData() {
		return infoSetData;
	}

	public void setInfoSetData(ArrayList<Object> infoSetData) {
		this.infoSetData = infoSetData;
	}

	protected JComponent createCenterPanel() {
		return getBackGroundPanel();
	}

	Boolean isNotExistInTable(BillItem item, String tableCode) {
		String fullMetaDataName = item.getMetaDataProperty().getFullName();
		return Boolean.valueOf((!StringUtils.isEmpty(fullMetaDataName))
				&& (!fullMetaDataName.contains(tableCode)));
	}

	private ArrayList<String> filterItems(BillItem[] items, String tableCode) {
		ArrayList<String> list = new ArrayList();
		for (int j = 0; j < items.length; j++) {
			BillItem item = items[j];
			if ((item.isShow()) && (item.getKey() != null)
					&& (!item.getKey().equals("photo"))) {
				if (!isNotExistInTable(item, tableCode).booleanValue()) {
					String itemKey = item.getKey();
					if (("hi_psnorg".equals(tableCode))
							&& (itemKey.contains("hi_psnorg_"))) {

						itemKey = itemKey.substring(10);
					}
					String meta = tableCode + "." + itemKey;
					list.add(meta);
				}
			}
		}
		return list;
	}

	private HashMap<String, ArrayList<String>> getShowItemMap(
			String[] infosetPKs) {
		HashMap<String, ArrayList<String>> map = new HashMap();
		try {
			BillData billData = new BillData(billTemletVO);
			String[] metaDatas = getInfosetTCodebyPKs(infosetPKs);
			for (int i = 0; i < metaDatas.length; i++) {
				ArrayList<String> list = new ArrayList();
				String tableCode = metaDatas[i].split("\\.")[1];

				if (tableCode.equals("bd_psndoc")) {
					BillItem[] items = billData.getHeadItems(tableCode);
					if ((items != null) && (items.length != 0)) {

						for (int j = 0; j < items.length; j++) {
							BillItem item = items[j];
							if (item.isShow()) {
								String meta = tableCode + "." + item.getKey();

								if ((item.getKey() == null)
										|| (!item.getKey().equals("photo"))) {

									list.add(meta);
								}
							}
						}
						map.put(metaDatas[i], list);
					}
				} else {
					BillItem[] items = billData.getBodyItemsForTable(tableCode);
					if ((items != null) && (items.length > 0)) {
						for (int j = 0; j < items.length; j++) {
							BillItem item = items[j];
							if (item.isShow()) {
								item.getMetaDataProperty().getName();
								String fullMetaDataName = item
										.getMetaDataProperty().getFullName();
								if ((StringUtils.isEmpty(fullMetaDataName))
										|| (fullMetaDataName
												.contains(tableCode))) {

									String meta = tableCode + "."
											+ item.getKey();
									list.add(meta);
								}
							}
						}
						map.put(metaDatas[i], list);
					}
				}
			}
		} catch (BusinessException e) {
			MessageDialog.showErrorDlg(getParent(), null, e.getMessage());
		}
		return map;
	}

	private String[] getInfosetTCodebyPKs(String[] infosetPKs)
			throws BusinessException {
		String inSql = ExportUtilForPsnInfo.getInsql(infosetPKs);

		String strCondition = "pk_infoset in (" + inSql
				+ "  ) and meta_data is not null ";
		InfoSetVO[] infoSetVOs = (InfoSetVO[]) ((IPersistenceRetrieve) NCLocator
				.getInstance().lookup(IPersistenceRetrieve.class))
				.retrieveByClause(null, InfoSetVO.class, strCondition);

		String[] metaDatas = new String[infoSetVOs.length];
		for (int i = 0; i < metaDatas.length; i++) {
			metaDatas[i] = infoSetVOs[i].getMeta_data();
		}
		return metaDatas;
	}
}
