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
 * ˰������UI
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
				ivjlblLeft.setILabelType(0/** С���� */);
				ivjlblLeft.setText("Ԥ��˰����Ŀ:�·��б�����Ŀ�ᱻԤ�Ƶ����Ź���н����Ŀ�У��״�Ԥ��ǰ�����޸�Ĭ������");
				ivjlblLeft.setVisible(true);
//				Font f = new Font("����",Font.PLAIN,16);
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
				ivjlblRight.setILabelType(0/** С���� */);
				ivjlblRight.setText("�����������б�:�Ѿ���������н�ʷ����б�");
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
			listToList.setLeftText("��ѡн�ʷ���:δ�������������ڼ��н�ʷ����б�");
			listToList.setRightText("��ѡн�ʷ���:����ִ������������н���ڼ䷽���б�");
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
																			 * "������Դ��֯"
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
																		 * "н�ʷ�������"
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
																		 * "н�ʷ�������"
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
																		 * "�������"
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
																		 * "�����ڼ�"
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
																			 * "������Դ��֯"
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
																		 * "н�ʷ�������"
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
																		 * "н�ʷ�������"
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
																		 * "�������"
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
																		 * "�����ڼ�"
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
		String[] colName = new String[] { "˰����Ŀ����", "˰����ĿĬ������", "˰����Ŀ�޸�����","�ֹ�����"};
		String[] colKey = new String[] { "code", "name6", "name" ,"taxflag"};

		BillItem[] abillBody = new BillItem[colName.length];
		int showColNum = 7;
		for (int i = 0; i < colName.length; i++) {
			abillBody[i] = new BillItem();
			abillBody[i].setName(colName[i]);
			abillBody[i].setKey(colKey[i]);
			abillBody[i].setDataType(BillItem.STRING);
			abillBody[i].setWidth(i == 0 ? 150 : 200);
			// ������ʾ��ʽ
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
		
		//������ ������༭
		if (vos != null && vos.length > 0) {
			getBillScrollPane().getTableModel().setEnabled(false);
		} else {
			getBillScrollPane().getTableModel().setEnabled(true);
		}

	}

	/**
	 * ��ʼ������
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
	 * ��ѯ����֯�Ƿ��Ѿ�������
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