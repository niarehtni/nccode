package nc.ui.wa.taxupgrade_tool.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.IFunNodeClosingListener;
import nc.ui.uif2.editor.IEditor;
import nc.ui.wa.pub.UITableToTable;
import nc.ui.wa.taxupgrade_tool.model.Taxupgrade_toolAppModel;
import nc.ui.wa.taxupgrade_tool.model.Taxupgrade_toolModelService;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.end.WaClassEndVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.paydata.ICommonAlterName;

/**
 * 税改升级UI
 * 
 * @author: xuhw
 */
public class Taxupgrade_toolForm extends JPanel implements AppEventListener, ValueChangedListener, ActionListener,
		TreeSelectionListener, IEditor {

	private static final long serialVersionUID = 5963913715201828677L;

	private Taxupgrade_toolAppModel model;
	private UIPanel mainPanel;
	private UIPanel upMainPanel;
	private UILabel ivjlblLeft;
	private UILabel ivjlblRight;
	private UIPanel ivjUIPane1 = null;
	private UIPanel ivjUIPane2 = null;
	private BillScrollPane upPane = null;
	private BillScrollPane upRightPane = null;
	private UITableToTable listToList;

	public Taxupgrade_toolModelService getModelservice() {
		return (Taxupgrade_toolModelService)getModel().getService() ;
	}
 

	private UIPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new UIPanel();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			mainPanel.add(getUPMainPanel(), BorderLayout.NORTH);
			mainPanel.add(getDownListToList(), BorderLayout.CENTER);

		}
		return mainPanel;
	}



	private UIPanel getUPMainPanel() {
		if (upMainPanel == null) {
			upMainPanel = new UIPanel();
			upMainPanel.setLayout(new BorderLayout());
			upMainPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
//			upMainPanel.setLayout(null);
//			upMainPanel.setSize(800, 300);
			upMainPanel.add(getUIScrollPane1(), BorderLayout.WEST);
			upMainPanel.add(getUIScrollPane2(), BorderLayout.CENTER);

		}
		return upMainPanel;
	}

	private UILabel getlblLeft() {
		if (ivjlblLeft == null) {
				ivjlblLeft = new nc.ui.pub.beans.UILabel();
				ivjlblLeft.setName("lblLeft");
				ivjlblLeft.setText("UILabel1");
				ivjlblLeft.setPreferredSize(new Dimension(180, 30));
				ivjlblLeft.setHorizontalAlignment(SwingConstants.CENTER);
				ivjlblLeft.setILabelType(0/** 小标题 */);
				ivjlblLeft.setText("预制税改项目:下方列表中项目会被预制到集团公共薪资项目中，首次预制前可以修改默认名称");
				ivjlblLeft.setVisible(true);
//				Font f = new Font("宋体",Font.PLAIN,16);
//				ivjlblLeft.setFont(f);
				ivjlblLeft.setForeground(Color.red);
		}
		return ivjlblLeft;
	}
	
	private UILabel getlblRight() {
		if (ivjlblRight == null) {
				ivjlblRight = new nc.ui.pub.beans.UILabel();
				ivjlblRight.setName("lblRight");
				ivjlblRight.setText("UILabel2");
				ivjlblRight.setPreferredSize(new Dimension(180, 30));
				ivjlblRight.setHorizontalAlignment(SwingConstants.CENTER);
				ivjlblRight.setILabelType(0/** 小标题 */);
				ivjlblRight.setText("已升级方案列表:已经升级过的薪资方案列表");
				ivjlblRight.setVisible(true);
		}
		return ivjlblRight;
	}

	
	public BillScrollPane getBillScrollPane() {
		if (upPane == null) {
			upPane = new BillScrollPane();
			upPane.setPreferredSize(new Dimension(650, 250));
			LineBorder border = new LineBorder(Color.GRAY);
			upPane.setBorder(border);
			initTable();
		}
		return upPane;
	}
	
	private UIPanel getUIScrollPane1() {
		if (ivjUIPane1 == null) {
			ivjUIPane1 = new UIPanel();
			ivjUIPane1.setLayout(new BorderLayout());
			ivjUIPane1.setPreferredSize(new Dimension(750, 300));
			ivjUIPane1.add(getlblLeft(), BorderLayout.NORTH);
			ivjUIPane1.add(getBillScrollPane(), BorderLayout.CENTER);
		}
		return ivjUIPane1;
	}

	private UIPanel getUIScrollPane2() {
		if (ivjUIPane2 == null) {
			ivjUIPane2 = new UIPanel();
			ivjUIPane2.setLayout(new BorderLayout());
			ivjUIPane2.setPreferredSize(new Dimension(800, 300));
			ivjUIPane2.add(getlblRight(), BorderLayout.NORTH);
			ivjUIPane2.add(getUpRightBillScrollPane(), BorderLayout.CENTER);
		}
		return ivjUIPane2;
	}

	public BillScrollPane getUpRightBillScrollPane() {
		if (upRightPane == null) {
			upRightPane = new BillScrollPane();
			upRightPane.setPreferredSize(new Dimension(500, 250));
			LineBorder border = new LineBorder(Color.GRAY);
			upRightPane.setBorder(border);
			upRightPane.setTableModel(getHasBillModel());
		}
		return upRightPane;
	}

	public void addUpLeftBodyVOs(WaItemVO[] itemVOs) {
		getBillScrollPane().getTableModel().clearBodyData();
		getBillScrollPane().getTableModel().setBodyDataVO(itemVOs);
	}

	public void addUpRightBodyVOs(GeneralVO[] generalvos) {
		getUpRightBillScrollPane().getTableModel().clearBodyData();
		getUpRightBillScrollPane().getTableModel().setBodyDataVO(generalvos);
	}

	public void addDownLeftBodyVOs(GeneralVO[] generalvos) {
		getDownListToList().setLeftData(generalvos);
	}
	
	public UITableToTable getDownListToList() {
		if (listToList == null) {
			listToList = new UITableToTable();
			listToList.setDisplayTitle(true);
			listToList.setHorizontalAlignment(SwingConstants.LEFT);
			listToList.initLeftTable(getBillModel());
			listToList.initRightTable(getBillModel());
			listToList.setLeftText("待选薪资方案:未被升级的最新期间的薪资方案列表");
			listToList.setRightText("已选薪资方案:即将执行升级操作的薪资期间方案列表");
		}
		return listToList;
	}

	public BillModel getBillModel() {

		List<BillItem> abillBody = new ArrayList<BillItem>();
		BillItem billBody = null;

		billBody = new BillItem();
		billBody.setName(WaClassEndVO.PK_WA_CLASS);
		billBody.setKey(WaClassEndVO.PK_WA_CLASS);
		billBody.setDataType(BillItem.STRING);
		billBody.setEnabled(false);
		billBody.setEdit(false);
		billBody.setShow(false);
		billBody.setNull(false);
		abillBody.add(billBody);

		billBody = new BillItem();
		billBody.setName(WaClassEndVO.PK_ORG);
		billBody.setKey(WaClassEndVO.PK_ORG);
		billBody.setDataType(BillItem.STRING);
		billBody.setEnabled(false);
		billBody.setEdit(false);
		billBody.setShow(false);
		billBody.setNull(false);
		abillBody.add(billBody);

		billBody = new BillItem();
		billBody.setName(ResHelper.getString("6013mend", "16013payapv0007")/*
																			 * @res
																			 * "人力资源组织"
																			 */);
		billBody.setKey(ICommonAlterName.ORGNAME);
		billBody.setDataType(BillItem.STRING);
		billBody.setEnabled(false);
		billBody.setEdit(false);
		billBody.setShow(true);
		billBody.setNull(false);
		abillBody.add(billBody);

		billBody = new BillItem();
		billBody.setName(ResHelper.getString("6013mend", "06013mend0015")/*
																		 * @res
																		 * "薪资方案编码"
																		 */);
		billBody.setKey(WaClassEndVO.CODE);
		billBody.setDataType(BillItem.STRING);
		billBody.setEnabled(false);
		billBody.setEdit(false);
		billBody.setShow(true);
		billBody.setNull(false);
		abillBody.add(billBody);

		billBody = new BillItem();
		billBody.setName(ResHelper.getString("6013mend", "06013mend0016")/*
																		 * @res
																		 * "薪资方案名称"
																		 */);
		billBody.setKey(WaClassEndVO.NAME);
		billBody.setDataType(BillItem.STRING);
		billBody.setEnabled(false);
		billBody.setEdit(false);
		billBody.setShow(true);
		billBody.setNull(false);
		abillBody.add(billBody);

		billBody = new BillItem();
		billBody.setName(ResHelper.getString("6013mend", "06013mend0017")/*
																		 * @res
																		 * "最新年度"
																		 */);
		billBody.setKey(WaClassEndVO.CYEAR);
		billBody.setDataType(BillItem.STRING);
		billBody.setEnabled(false);
		billBody.setEdit(false);
		billBody.setShow(true);
		billBody.setNull(false);
		abillBody.add(billBody);

		billBody = new BillItem();
		billBody.setName(ResHelper.getString("6013mend", "06013mend0018")/*
																		 * @res
																		 * "最新期间"
																		 */);
		billBody.setKey(WaClassEndVO.CPERIOD);
		billBody.setDataType(BillItem.STRING);
		billBody.setEnabled(false);
		billBody.setEdit(false);
		billBody.setShow(true);
		billBody.setNull(false);
		abillBody.add(billBody);

		BillModel billModel = new BillModel();
		billModel.setBodyItems(abillBody.toArray(new BillItem[abillBody.size()]));
		return billModel;
	}

	

	public BillModel getHasBillModel() {

		List<BillItem> abillBody = new ArrayList<BillItem>();
		BillItem billBody = null;

		billBody = new BillItem();
		billBody.setName(WaClassEndVO.PK_WA_CLASS);
		billBody.setKey(WaClassEndVO.PK_WA_CLASS);
		billBody.setDataType(BillItem.STRING);
		billBody.setEnabled(false);
		billBody.setEdit(false);
		billBody.setShow(false);
		billBody.setNull(false);
		abillBody.add(billBody);

		billBody = new BillItem();
		billBody.setName(WaClassEndVO.PK_ORG);
		billBody.setKey(WaClassEndVO.PK_ORG);
		billBody.setDataType(BillItem.STRING);
		billBody.setEnabled(false);
		billBody.setEdit(false);
		billBody.setShow(false);
		billBody.setNull(false);
		abillBody.add(billBody);

		billBody = new BillItem();
		billBody.setName(ResHelper.getString("6013mend", "16013payapv0007")/*
																			 * @res
																			 * "人力资源组织"
																			 */);
		billBody.setKey(ICommonAlterName.ORGNAME);
		billBody.setDataType(BillItem.STRING);
		billBody.setEnabled(false);
		billBody.setEdit(false);
		billBody.setShow(true);
		billBody.setNull(false);
		billBody.setWidth(200);
		abillBody.add(billBody);

		billBody = new BillItem();
		billBody.setName(ResHelper.getString("6013mend", "06013mend0015")/*
																		 * @res
																		 * "薪资方案编码"
																		 */);
		billBody.setKey(WaClassEndVO.CODE);
		billBody.setDataType(BillItem.STRING);
		billBody.setEnabled(false);
		billBody.setEdit(false);
		billBody.setShow(true);
		billBody.setNull(false);
		billBody.setWidth(200);
		abillBody.add(billBody);

		billBody = new BillItem();
		billBody.setName(ResHelper.getString("6013mend", "06013mend0016")/*
																		 * @res
																		 * "薪资方案名称"
																		 */);
		billBody.setKey(WaClassEndVO.NAME);
		billBody.setDataType(BillItem.STRING);
		billBody.setEnabled(false);
		billBody.setEdit(false);
		billBody.setShow(true);
		billBody.setNull(false);
		billBody.setWidth(200);
		abillBody.add(billBody);

		billBody = new BillItem();
		billBody.setName(ResHelper.getString("6013mend", "06013mend0017")/*
																		 * @res
																		 * "最新年度"
																		 */);
		billBody.setKey(WaClassEndVO.CYEAR);
		billBody.setDataType(BillItem.STRING);
		billBody.setEnabled(false);
		billBody.setEdit(false);
		billBody.setShow(true);
		billBody.setNull(false);
		billBody.setWidth(100);
		abillBody.add(billBody);


		billBody = new BillItem();
		billBody.setName(ResHelper.getString("6013mend", "06013mend0018")/*
																		 * @res
																		 * "最新期间"
																		 */);
		billBody.setKey(WaClassEndVO.CPERIOD);
		billBody.setDataType(BillItem.STRING);
		billBody.setEnabled(false);
		billBody.setEdit(false);
		billBody.setShow(true);
		billBody.setNull(false);
		billBody.setWidth(200);
		abillBody.add(billBody);

		BillModel billModel = new BillModel();
		billModel.setBodyItems(abillBody.toArray(new BillItem[abillBody.size()]));
		return billModel;
	}

	
	private void initTable() {
		String[] colName = new String[] { "税改项目编码", "税改项目默认名称", "税改项目修改名称","手工输入"};
		String[] colKey = new String[] { "code", "name6", "name" ,"taxflag"};

		BillItem[] abillBody = new BillItem[colName.length];
		int showColNum = 7;
		for (int i = 0; i < colName.length; i++) {
			abillBody[i] = new BillItem();
			abillBody[i].setName(colName[i]);
			abillBody[i].setKey(colKey[i]);
			abillBody[i].setDataType(BillItem.STRING);
			abillBody[i].setWidth(i == 0 ? 150 : 200);
			// 设置显示格式
			if (i >= 2) {
				abillBody[i].setEnabled(true);
				abillBody[i].setEdit(true);
			} else {
				abillBody[i].setEnabled(false);
				abillBody[i].setEdit(false);
			}
			
			if (i == 3) {
				abillBody[i].setWidth(80);
				abillBody[i].setDataType(BillItem.BOOLEAN);
				((UICheckBox) (abillBody[i].getComponent())).setHorizontalAlignment(UICheckBox.CENTER);
			}
		}

		BillModel billModel = new BillModel();
		billModel.setBodyItems(abillBody);
		getBillScrollPane().setTableModel(billModel);
		getBillScrollPane().setRowNOShow(true);
		((UICheckBox) (abillBody[3].getComponent())).setHorizontalAlignment(UICheckBox.CENTER);
	}

	private String getPk_org() {
		return getModel().getContext().getPk_org();
	}

	public Taxupgrade_toolAppModel getModel() {
		return model;
	}

	public Taxupgrade_toolForm() {
		super();
	}

	@Override
	public void handleEvent(AppEvent event) {
		if (event.getType().equalsIgnoreCase("refreshtaxupgradetool")) {
		}
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
	}

	public void initUI() {
		setLayout(new BorderLayout());
		add(getMainPanel(), BorderLayout.CENTER);
		initData();
		WaItemVO[] vos = this.getGroupTaxItems();
		
		//升级过 不允许编辑
		if (vos != null && vos.length > 0) {
			getBillScrollPane().getTableModel().setEnabled(false);
		} else {
			getBillScrollPane().getTableModel().setEnabled(true);
		}

	}

	/**
	 * 初始化数据
	 */
	public void initData() {
		WaItemVO[] itemvos = null;
		try {
			itemvos = getModelservice().queryTaxItemVOs(getModel().getContext().getPk_group());
			addUpLeftBodyVOs(itemvos);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		addUpRightBodyVOs(this.getHasInitClassVOs());
		addDownLeftBodyVOs(this.getUnInitClassVOs());
		getDownListToList().setRightData(null);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	private void handleException(Throwable e) {
		Logger.error(e.getMessage(), e);
	}

	public void setModel(Taxupgrade_toolAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	@Override
	public void setValue(Object object) {

	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getValue() {
		return null;
	}
	
	public GeneralVO[] getSelectedClassVOs(){
		GeneralVO[] vos =(GeneralVO[]) getDownListToList().getRightData();
		if(vos == null || vos.length == 0){
			return null;
		}
		return vos;
	}

	public GeneralVO[] getUnSelectedClassVOs(){
		GeneralVO[] vos =(GeneralVO[]) getDownListToList().getLeftData();
		if(vos == null || vos.length == 0){
			return null;
		}
		return vos;
	}
	
	private GeneralVO[] getHasInitClassVOs(){
		GeneralVO[] generalvos = null;
		try {
			generalvos = getModelservice().queryHasInitClassVOs(PubEnv.getPk_group());
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return generalvos;
	}
	
	private GeneralVO[] getUnInitClassVOs(){
		GeneralVO[] generalvos = null;
		try {
			generalvos = getModelservice().queryUnInitClassVOs(PubEnv.getPk_group());
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return generalvos;
	}

	/**
	 * 查询该组织是否已经升级过
	 * @return
	 */
	private WaItemVO[] getGroupTaxItems(){
		WaItemVO[] generalvos = null;
		try {
			generalvos = getModelservice().queryTaxUpgradeItems(PubEnv.getPk_group());
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return generalvos;
	}
	public WaItemVO[] getTaxItems(){
		return (WaItemVO[]) getBillScrollPane().getTableModel().getBodyValueVOs(WaItemVO.class.getName());
	}
}