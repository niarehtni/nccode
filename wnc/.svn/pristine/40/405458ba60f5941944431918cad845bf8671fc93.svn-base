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
import nc.ui.pub.beans.UIDialog;
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

/**
 * 背景板是backgroudPanel 背景面板放置三个panel 从上到下依次是: 导出范围：conditionPanel
 * 选择文件路径：pathPanel 信息集：listToListPanel
 * 
 * @author yunana
 */
public class ExpPsnInfosDlg extends HrDialog {
	private UIPanel backGroundPanel = null;
	private UIPanel northPanel = null;

	private UIPanel conditionPanel = null;
	private UIRadioButton allRbtn; // 导出全部，默认选中
	private UIRadioButton lastRbtn;

	private UIPanel pathPanel;
	private UITextField pathField = null;
	private UIButton appointPathBtn = null;

	private ListToListPanel listToListPanel = null;

	private ArrayList<Object> infoSetData = null;

	private static String oldPath = "C:\\";

	private final String[] psnPks;

	private final String pk_org; // 当前人力资源组织
	private int queryMode = 0; // 用于指定lastflag的值，queryMode==1，lastflag='Y';queryMode==0,则查询全部

	private AbstractUIAppModel model = null;
	private LoginContext context;
	private static BillTempletVO billTemletVO;
	private Container parent;

	public ExpPsnInfosDlg(Container parent, String[] psnPks, String pk_org, AbstractUIAppModel model) {
		super(parent);
		this.parent = parent;
		setTitle(ResHelper.getString("6007psn", "06007psn0443")/* "选择导出信息集" */);
		setSize(600, 450);
		setResizable(false);
		this.model = model;
		this.psnPks = psnPks;
		this.pk_org = pk_org;

		// setOkAction(new OkAction(parent, model));
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

	@Override
	public void initUI() {
		super.initUI();
		((ListToListPanel) getListToListPanel()).setLeftData(this.getInfoSetVOs());
	}

	/**
	 * 取得可以被导出的信息集，过滤掉界面上不显示的信息集 2013-1-15 下午04:27:03 yunana
	 * 
	 * @return
	 */
	private InfoSetVO[] getInfoSetVOs() {
		InfoSetVO[] infoSetVOArray = null;
		ArrayList<InfoSetVO> infoVOList = new ArrayList<InfoSetVO>();
		try {
			infoSetVOArray = ((IInfoSetQry) NCLocator.getInstance().lookup(IInfoSetQry.class)).queryInfoSet(
					getContext(), " pk_infoset_sort  =  '1001Z710000000002XPO' " +
					// ssx added on 20171005
					// PeriodVO do not support Export in ExNHI SubInfo
							" and pk_infoset !='TWHRA21000000000DEF5' "
							//
							+ "order by main_table_flag desc,showorder ");

			String[] infoPks = this.getInfosetPks(infoSetVOArray);
			HashMap<String, ArrayList<String>> hashMap = this.getShowItemMap(infoPks);
			// HashMap<String, ArrayList<String>> hashMap =
			// this.getShowItemMap();
			for (int i = 0; i < infoSetVOArray.length; i++) {
				String meta = infoSetVOArray[i].getMeta_data();
				ArrayList<String> itemList = hashMap.get(meta);
				// 过滤掉界面上不显示的信息集
				if (itemList != null && !itemList.isEmpty()) {
					infoVOList.add(infoSetVOArray[i]);
				}
			}
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		// return infoSetVOArray;
		return infoVOList.toArray(new InfoSetVO[infoVOList.size()]);
	}

	@Override
	public void closeOK() {
		// 得到输出文件路径
		String filePath = ((UITextField) this.getPathPanel().getComponent(1)).getText();
		if (StringUtils.isEmpty(filePath)) {
			MessageDialog.showErrorDlg(this, null, ResHelper.getString("6007psn", "06007psn0444")/* "请选择保存文件路径" */);
			return;
		}
		if (!filePath.endsWith(".xls")) {
			MessageDialog
					.showErrorDlg(this, null, ResHelper.getString("6007psn", "06007psn0466")/* "导出文件只能为excel文档，即文件扩展名应为.xls" */);
			return;
		}
		File file = new File(filePath);
		if (file.exists() && file.isFile()) {
			if (UIDialog.ID_YES != MessageDialog.showYesNoDlg(this, ResHelper.getString("6007psn", "06007psn0501")/*
																												 * @
																												 * res
																												 * "确认覆盖"
																												 */,
					ResHelper.getString("6007psn", "06007psn0462")/*
																 * @res
																 * "文件已存在，是否覆盖？"
																 */)) {
				// putValue("message_after_action",
				// ResHelper.getString("6007psn","06007psn0039")/*@res
				// "已取消！"*/);
				return;
			}
			if (!file.delete()) {
				MessageDialog
						.showErrorDlg(this, null, ResHelper.getString("6007psn", "06007psn0465")/* "当前文件处于编辑状态，无法覆盖。请关闭相关程序，或者选择其他文件路径" */);
				return;
			}
		}
		String[] infoSetPks = this.getInfoSetPks();
		if (infoSetPks == null || infoSetPks.length == 0) {
			MessageDialog.showErrorDlg(this, null, ResHelper.getString("6007psn", "06007psn0445")/* "请选择要导出的信息集" */);
			return;
		}
		HashMap<String, ArrayList<String>> tNameVScolumsMap = this.getShowItemMap(infoSetPks);
		// HashMap<String, ArrayList<String>> tNameVScolumsMap =
		// this.getShowItemMap();
		// 为加蒙版用，OkAction继承HrACTION必须是在同一线程中，并且执行时间不能过短的情况下，蒙版才会出现,因此下面的线程也不需要了
		setOkAction(new OkAction(parent, model, filePath, infoSetPks, tNameVScolumsMap, psnPks, pk_org, queryMode,
				billTemletVO));
		// ExportThread thread = new ExportThread(filePath, infoSetPks,
		// tNameVScolumsMap);
		// thread.start();
		super.closeOK();

	}

	// class ExportThread extends Thread {
	//
	// String filePath;
	// String[] infoSetPks;
	// HashMap<String, ArrayList<String>> tNameVScolumsMap;
	//
	// ExportThread(String filePath, String[] infoSetPks,
	// HashMap<String, ArrayList<String>> tNameVScolumsMap) {
	// this.filePath = filePath;
	// this.infoSetPks = infoSetPks;
	// this.tNameVScolumsMap = tNameVScolumsMap;
	//
	// }
	//
	// @Override
	// public void run() {
	// // IProgressMonitor progressMonitor = NCProgresses
	// // .createDialogProgressMonitor(getParent());
	// // progressMonitor.beginTask(
	// // ResHelper.getString("6007psn", "06007psn0021")/* "提示" */,
	// // IProgressMonitor.UNKNOWN_REMAIN_TIME);
	// // progressMonitor.setProcessInfo(ResHelper.getString("6007psn",
	// // "06007psn0446")/* "正在导出" */);
	// try {
	// Thread.sleep(10000);
	// HashMap<String, GeneralVO[]> generalVOsMap = NCLocator
	// .getInstance()
	// .lookup(IPsndocQryService.class)
	// .queryPsnInfo(psnPks, infoSetPks,
	// ExpPsnInfosDlg.this.getQueryMode(), pk_org,
	// tNameVScolumsMap);
	// new ExportUtilForPsnInfo(billTemletVO).exportVOs(generalVOsMap,
	// filePath, infoSetPks);
	// } catch (BusinessException e) {
	// MessageDialog.showErrorDlg(getParent(), null, e.getMessage());
	// Logger.error(e.getMessage(), e);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// Logger.error(e.getMessage(), e);
	// }
	// //progressMonitor.done(); // 进度任务结束
	// ShowStatusBarMsgUtil.showStatusBarMsg(
	// ResHelper.getString("6007psn", "06007psn0447")/* "导出成功" */,
	// getContext());
	// }
	// }

	/**
	 * 获得右边信息集PK 由于取得数据之后清空list，所以这个方法只能被调用一次 2013-1-15 下午04:14:47 yunana
	 * 
	 * @return
	 */
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
		digFile.setFileFilter(new ReportExportFileFilter("xls", ResHelper.getString("6007psn", "06007psn0442")/* 导出 */
				+ "(*.xls)"));
		int returnVal = digFile.showOpenDialog(this);
		if (returnVal == UIFileChooser.APPROVE_OPTION) {
			strFileName = digFile.getSelectedFile().getPath();
			if (!strFileName.substring(strFileName.length() - 4, strFileName.length()).equalsIgnoreCase(".xls")) {
				strFileName += ".xls";
			}
			oldPath = strFileName;
		} else {
			strFileName = "";
		}

		// this.getPathPanel().getComponent(1)返回的的pathField的实体，奇怪的地方在于不能直接通过this.getPathfield()得到pathField
		((UITextField) this.getPathPanel().getComponent(1)).setText(strFileName);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		super.actionPerformed(evt);
		// this.getPathPanel().getComponent(3)得到的是appointBtn(即选择保存文件路径按钮)
		if (evt.getSource() == this.getPathPanel().getComponent(3)) {
			onAppointBtn();
		}
		if (evt.getSource() == allRbtn) {
			this.setQueryMode(0);
		}
		if (evt.getSource() == lastRbtn) {
			this.setQueryMode(1);
		}
	}

	/**
	 * 初始中间选择文件路径的panel，
	 * 
	 * @return
	 */
	public UIPanel getPathPanel() {
		if (pathPanel == null) {
			pathPanel = new UIPanel();
			FlowLayout flow = new FlowLayout();
			flow.setAlignment(FlowLayout.LEFT);
			pathPanel.setLayout(flow);
			// pathPanel.setPreferredSize(new Dimension(560, 80));

			FormLayout layout = new FormLayout(
					"right:pref, 23dlu, left:pref,10dlu,right:pref, 10dlu, left:pref, 100dlu", "");
			DefaultFormBuilder builder = new DefaultFormBuilder(layout, pathPanel);
			builder.setBorder(BorderFactory.createTitledBorder(ResHelper.getString("6007psn", "06007psn0448")/* "导出文件" */));
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
			appointPathBtn = new UIButton(ResHelper.getString("6007psn", "06007psn0449")/* "文件路径" */);
			appointPathBtn.addActionListener(this);
			appointPathBtn.setPreferredSize(new Dimension(50, 20));
		}
		return appointPathBtn;
	}

	/**
	 * 初始化信息集panel
	 * 
	 * @return
	 */
	public UIPanel getListToListPanel() {
		if (listToListPanel == null) {
			listToListPanel = new ListToListPanel();
			listToListPanel.setName("listToList");
			listToListPanel.getBnR().setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_TO_RIGHT));
			listToListPanel.getBnRR().setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_ALL_TO_RIGHT));
			listToListPanel.getBnL().setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_TO_LEFT));
			listToListPanel.getBnLL().setIcon(IconUtils.getInstance().getIcon(IconUtils.ICON_ALL_TO_LEFT));
		}
		return listToListPanel;
	}

	/**
	 * 初始化导出范围Panel
	 * 
	 * @return
	 */
	public UIPanel getConditionPanel() {
		if (conditionPanel == null) {
			conditionPanel = new UIPanel();
			conditionPanel.setPreferredSize(new Dimension(560, 80));

			ButtonGroup btnGroup = new ButtonGroup();
			btnGroup.add(getAllRbtn());
			btnGroup.add(getLastRbtn());

			FormLayout layout = new FormLayout(
					"right:pref, 20dlu, left:pref,10dlu,right:pref, 10dlu, left:pref, 100dlu", "");
			DefaultFormBuilder builder = new DefaultFormBuilder(layout, conditionPanel);
			builder.setBorder(BorderFactory.createTitledBorder(ResHelper.getString("6007psn", "06007psn0450")/* "数据导出范围" */));
			builder.append("", getAllRbtn());
			builder.nextLine();
			builder.append("", getLastRbtn());
			builder.nextLine();
		}
		return conditionPanel;
	}

	public UIRadioButton getAllRbtn() {
		if (allRbtn == null) {
			allRbtn = new UIRadioButton(ResHelper.getString("6007psn", "06007psn0451")/* "全部" */);
			allRbtn.setPreferredSize(new Dimension(150, 20));
			allRbtn.setSelected(true);
			allRbtn.addActionListener(this);
		}
		return allRbtn;
	}

	public UIRadioButton getLastRbtn() {
		if (lastRbtn == null) {
			lastRbtn = new UIRadioButton(ResHelper.getString("6007psn", "06007psn0452")/* "最新记录" */);
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
			backGroundPanel.add(getNorthPanel(), BorderLayout.NORTH);
			backGroundPanel.add(getListToListPanel(), BorderLayout.CENTER);
		}
		return backGroundPanel;
	}

	@Override
	public UIPanel getNorthPanel() {
		if (northPanel == null) {
			northPanel = new UIPanel();
			northPanel.setLayout(new BorderLayout());
			northPanel.setPreferredSize(new Dimension(560, 138));
			northPanel.add(getConditionPanel(), BorderLayout.NORTH);
			northPanel.add(getPathPanel(), BorderLayout.CENTER);
		}
		return northPanel;
	}

	public ArrayList<Object> getInfoSetData() {
		return infoSetData;
	}

	public void setInfoSetData(ArrayList<Object> infoSetData) {
		this.infoSetData = infoSetData;
	}

	@Override
	protected JComponent createCenterPanel() {
		// TODO Auto-generated method stub
		return getBackGroundPanel();
	}

	/**
	 * 将所选信息集的元数据上被隐藏的信息项过滤掉
	 * 
	 * @param infosetPKs
	 * @return HashMap K:元数据属性 V：此表所持有的列集合
	 */
	// private HashMap<String,ArrayList<String>> getShowItemMap(){
	// HashMap<String, ArrayList<String>> map = new HashMap<String,
	// ArrayList<String>>();
	// BillTempletBodyVO[] billTempletBodyVOs = billTemletVO.getBodyVO();
	// for (int i = 0; i < billTempletBodyVOs.length; i++) {
	// BillTempletBodyVO billTempletBodyVO = billTempletBodyVOs[i];
	// String tableCode = billTempletBodyVO.getTable_code();
	// String metaDatas = "hrhi."+tableCode;
	// if(!map.keySet().contains(metaDatas)){
	// if(billTempletBodyVO.getShowflag()){
	// ArrayList<String> list = new ArrayList<String>();
	// // 去掉【照片】字段
	// if (billTempletBodyVO.getItemkey().equals("photo")) {
	// continue;
	// }
	// String meta = tableCode+"."+billTempletBodyVO.getItemkey();
	// list.add(meta);
	// map.put(metaDatas, list);
	// }
	// }else{
	// if(billTempletBodyVO.getShowflag()){
	// ArrayList<String> list = map.get(metaDatas);
	// // 去掉【照片】字段
	// if (billTempletBodyVO.getItemkey().equals("photo")) {
	// continue;
	// }
	// String meta = tableCode+"."+billTempletBodyVO.getItemkey();
	// list.add(meta);
	// map.put(metaDatas, list);
	// }
	// }
	// }
	// return map;
	// }

	/**
	 * 判断单据模板中字段，是否在表中存在
	 * 
	 * @param item
	 * @param tableCode
	 * @author heqiaoa
	 * @return Boolean
	 */
	Boolean isNotExistInTable(BillItem item, String tableCode) {
		String fullMetaDataName = item.getMetaDataProperty().getFullName();
		return !StringUtils.isEmpty(fullMetaDataName) && !fullMetaDataName.contains(tableCode);
	}

	/**
	 * 过滤字段
	 * 
	 * @author heqiaoa
	 * @return
	 */
	private ArrayList<String> filterItems(BillItem[] items, String tableCode) {
		ArrayList<String> list = new ArrayList<String>();
		for (int j = 0; j < items.length; j++) {
			BillItem item = items[j];
			if (item.isShow() && item.getKey() != null && !item.getKey().equals("photo")) { // 去掉【照片】字段
				if (!isNotExistInTable(item, tableCode)) {
					String itemKey = item.getKey();
					if ("hi_psnorg".equals(tableCode)// 單獨處理表頭組織關係中的字段
							&& itemKey.contains("hi_psnorg_")) {
						// 由于单据模板组织关系页签中的字段的itemkey加了hi_psnorg_前缀，这里将其去掉
						itemKey = itemKey.substring(10);
					}
					String meta = tableCode + "." + itemKey;
					list.add(meta);
				}
			}
		}
		return list;
	}

	/**
	 * 通过信息集主键，将所选信息集的元数据上被隐藏的信息项过滤掉
	 * 
	 * @param infosetPKs
	 * @return HashMap K:元数据属性 V：此表所持有的列集合
	 */
	private HashMap<String, ArrayList<String>> getShowItemMap(String[] infosetPKs) {
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

		try {
			BillData billData = new BillData(billTemletVO);
			String[] metaDatas = this.getInfosetTCodebyPKs(infosetPKs);
			for (int i = 0; i < metaDatas.length; i++) {
				ArrayList<String> list = new ArrayList<String>();
				String tableCode = metaDatas[i].split("\\.")[1];
				// 表头
				if (tableCode.equals("bd_psndoc")) {
					BillItem[] items = billData.getHeadItems(tableCode);
					if (items == null || items.length == 0) {
						continue;
					}
					for (int j = 0; j < items.length; j++) {
						BillItem item = items[j];
						if (item.isShow()) {
							String meta = tableCode + "." + item.getKey();
							// 去掉【照片】字段
							if (item.getKey() != null && item.getKey().equals("photo")) {
								continue;
							}
							// 去掉【推荐人工号】
							// 2018-08-22 by chenming11 去掉不在信息集中没有的推荐人工号
							// 注：该项目中推荐人工号只为显示，是在单据模板初始化中建的
							if (item.getKey() != null && item.getKey().equals("glbdef3.code")) {
								continue;
							}
							list.add(meta);
						}
					}
					map.put(metaDatas[i], list);
					continue;
				}
				// 表体
				BillItem[] items = billData.getBodyItemsForTable(tableCode);
				if (items != null && items.length > 0) {
					for (int j = 0; j < items.length; j++) {
						BillItem item = items[j];
						if (item.isShow()) {
							item.getMetaDataProperty().getName();
							String fullMetaDataName = item.getMetaDataProperty().getFullName();
							if (!StringUtils.isEmpty(fullMetaDataName) && !fullMetaDataName.contains(tableCode)) {
								// 单据模板上存在，但是表中不存在的字段不导出
								continue;
							}
							String meta = tableCode + "." + item.getKey();
							list.add(meta);
						}
					}
					map.put(metaDatas[i], list);
				}
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			MessageDialog.showErrorDlg(getParent(), null, e.getMessage());
		}
		return map;
	}

	/**
	 * 通过信息集主键pk_infoset得到其对应元数据属性
	 * 
	 * @param infosetPKs
	 * @return
	 * @throws BusinessException
	 */
	private String[] getInfosetTCodebyPKs(String[] infosetPKs) throws BusinessException {
		// InSQLCreator isc = new InSQLCreator();
		// String inSql = isc.getInSQL(infosetPKs);
		String inSql = ExportUtilForPsnInfo.getInsql(infosetPKs);
		// 过滤增加信息集没有同步元数据的信息集
		String strCondition = "pk_infoset in (" + inSql + "  ) and meta_data is not null ";
		InfoSetVO[] infoSetVOs = (InfoSetVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
				.retrieveByClause(null, InfoSetVO.class, strCondition);
		String[] metaDatas = new String[infoSetVOs.length];
		for (int i = 0; i < metaDatas.length; i++) {
			metaDatas[i] = infoSetVOs[i].getMeta_data();
		}
		return metaDatas;
	}

}
