package nc.ui.wa.formular;

import java.awt.Dimension;
import java.util.Collection;

import javax.swing.SwingConstants;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.bd.ref.model.DefdocGridRefModel;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.hr.dataio.ConstEnumFactory;
import nc.vo.pub.BusinessException;
import nc.vo.ta.timeitem.LeaveTypeVO;

/**
 * #21266 ���պϼƿ��ڿۿ�������
 * 
 * @author ssx
 * @date 2019-4-7
 */
public class LeaveLeaveFeeFunctionEditor extends WaAbstractFunctionEditor {

	private static final long serialVersionUID = 6414923710103945313L;

	private UILabel islabel = null;
	private UILabel itemlabel = null;
	private UIComboBox yOrnCBox = null;
	private UIComboBox itemCBox = null;
	// н����Ŀ������� Ares.Tank 2019��1��20��21:17:47
	private UILabel groupLabel = null;
	private UIRefPane groupRef = null;

	@Override
	public void setModel(AbstractUIAppModel model) {
		// TODO Auto-generated method stub
		super.setModel(model);
		initData();
	}

	public void initData() {
		try {
			LeaveTypeVO[] items = getItems(getContext().getPk_org());
			getItemCBox().addItems(items);
			groupRef.setPk_org(getModel().getContext().getPk_org());
		} catch (Exception e) {
			handleException(e);
		}
	}

	/**
	 * WaParaPanel ������ע�⡣
	 */
	public LeaveLeaveFeeFunctionEditor() {
		super();
		initialize();
	}

	private static final String funcname = "@" + ResHelper.getString("6013commonbasic", "06013commonbasic0276") + "@";

	// "taxRate";
	@Override
	public String getFuncName() {
		// TODO Auto-generated method stub
		return funcname;
	}

	/**
	 * ��ʼ���ࡣ
	 */
	/* ���棺�˷������������ɡ� */
	private void initialize() {
		try {
			setLayout(null);
			setSize(300, 180);
			setTitle("Ո�x�񅢔�");

			add(getItemLabel(), getItemLabel().getName());
			add(getItemCBox(), getItemCBox().getName());

			add(getUILabel(), getUILabel().getName());
			add(getYOrnCBox(), getYOrnCBox().getName());

			add(getGroupLabel(), getGroupLabel().getName());
			add(getGroupRef(), getGroupRef().getName());

			add(getOkButton(), getOkButton().getName());
			add(getCancelButton(), getCancelButton().getName());

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}

		initConnection();

	}

	public UILabel getGroupLabel() {
		if (this.groupLabel == null) {
			try {
				this.groupLabel = new UILabel();
				this.groupLabel.setName("UILabel4");
				this.groupLabel.setText("н����Ŀ����");
				this.groupLabel.setBounds(10, 80, 100, 22);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return this.groupLabel;
	}

	public UIRefPane getGroupRef() {
		if (groupRef == null) {
			groupRef = new UIRefPane();
			DefdocGridRefModel model = new DefdocGridRefModel();
			// ����bd_refinfo�е�refclass
			groupRef.setRefModel(model);
			// ��������(Ҫ��bd_refinfo�е�һ��)
			groupRef.setRefNodeName("н����Ŀ����");
			groupRef.getRefModel().setMutilLangNameRef(false);
			groupRef.setPk_org(null);
			// refGroupIns.set
			groupRef.setVisible(true);
			groupRef.setPreferredSize(new Dimension(50, 20));
			groupRef.setButtonFireEvent(true);
			groupRef.setName("groupRef");
			groupRef.setBounds(120, 80, 150, 22);
		}
		return groupRef;
	}

	/* ���棺�˷������������ɡ� */
	private UILabel getUILabel() {
		if (islabel == null) {
			try {
				islabel = new UILabel();
				islabel.setName("islabel");
				islabel.setText(ResHelper.getString("6013commonbasic", "06013commonbasic0269")/*
																							 * @
																							 * res
																							 * "�Ƿ���˰"
																							 */);
				islabel.setBounds(10, 50, 100, 22);
				islabel.setHorizontalAlignment(SwingConstants.RIGHT);
			} catch (java.lang.Throwable labExc) {
				handleException(labExc);
			}
		}
		return islabel;
	}

	private UILabel getItemLabel() {
		if (itemlabel == null) {
			try {
				itemlabel = new UILabel();
				itemlabel.setName("itemlabel");
				itemlabel.setText(ResHelper.getString("6013commonbasic", "06013commonbasic0273")/*
																								 * @
																								 * res
																								 * "�ݼ����"
																								 */);
				itemlabel.setBounds(10, 20, 100, 22);
				itemlabel.setHorizontalAlignment(SwingConstants.RIGHT);
			} catch (java.lang.Throwable labExc) {
				handleException(labExc);
			}
		}
		return itemlabel;
	}

	/* ���棺�˷������������ɡ� */
	private UIComboBox getYOrnCBox() {
		if (yOrnCBox == null) {
			try {
				yOrnCBox = new UIComboBox();
				String[] ml = new String[2];
				ml[0] = ResHelper.getString("6013commonbasic", "06013commonbasic0270")/*
																					 * @
																					 * res
																					 * "��"
																					 */;
				ml[1] = ResHelper.getString("6013commonbasic", "06013commonbasic0271")/*
																					 * @
																					 * res
																					 * "��"
																					 */;

				Integer[] mlDefault = new Integer[] { 0, 1 };
				ConstEnumFactory<Integer> mPairFactory = new ConstEnumFactory<Integer>(ml, mlDefault);
				yOrnCBox.addItems(mPairFactory.getAllConstEnums());
				yOrnCBox.setBounds(120, 50, 150, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return yOrnCBox;
	}

	private UIComboBox getItemCBox() {
		if (itemCBox == null) {
			try {
				itemCBox = new UIComboBox();
				itemCBox.setName("itemCBox");
				itemCBox.setBounds(120, 20, 150, 22);
				itemCBox.setTranslate(true);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return itemCBox;
	}

	/**
	 * /** * ���������Ϸ���У�� * @param ����˵�� * @return ����ֵ * @exception �쳣���� * @see
	 * ��Ҫ�μ����������� * @since �������һ���汾���˷�������ӽ���������ѡ�� *
	 * 
	 * @deprecated�÷����������һ���汾���Ѿ������������滻������ѡ�� *-/
	 * 
	 * @return java.lang.String
	 */
	public boolean checkPara(int dataType) {
		try {
			// �жϷǿ�
			String nullstr = "";
			if (getYOrnCBox().getSelectedIndex() < 0) {
				if (nullstr.length() > 0)
					nullstr += ",";
				nullstr += ResHelper.getString("6013commonbasic", "06013commonbasic0269")/*
																						 * @
																						 * res
																						 * "�Ƿ���˰"
																						 */;
			}
			if (nullstr.length() > 0)
				throw new Exception(nullstr + ResHelper.getString("6013commonbasic", "06013commonbasic0021")/*
																											 * @
																											 * res
																											 * "����Ϊ�գ�"
																											 */);
			if (getGroupRef().getRefPK() == null) {
				throw new Exception("Ո�x��һ��н�Y�Ŀ�ֽM!");
			}
			return true;

		} catch (Exception e) {
			handleException(e);
			showErrMsg(e.getMessage());
			return false;
		}
	}

	/**
	 * ���ڼ���Ŀ����ȡָ������µ�н�����ݣ���������ǰ����µ��ڼ䣬н�ʷ�����н����Ŀ��
	 * 
	 * 
	 * @return java.lang.String
	 */
	public String[] getPara() {
		String[] paras = new String[3];

		LeaveTypeVO itemVO = (LeaveTypeVO) getItemCBox().getSelectdItemValue();
		// �����±���Ŀ����
		paras[0] = "\"" + itemVO.getPk_timeitem().trim() + "\"";

		// �Ƿ���˰ 0�� 1��
		paras[1] = "\"" + getYOrnCBox().getSelectdItemValue().toString() + "\"";

		paras[2] = "\"" + getGroupRef().getRefPK() + "\"";

		return paras;
	}

	@SuppressWarnings("unused")
	private LeaveTypeVO[] getItems(String pk_org) throws BusinessException {
		// VOQuery<LeaveTypeVO> query = new
		// VOQuery<LeaveTypeVO>(LeaveTypeVO.class);
		// return
		// query.queryWithWhereKeyWord("where pk_org = '"+pk_org+"' and itemtype = '0'",
		// null);

		// Collection<WaClassItemVO> c = (Collection)new
		// BaseDAO().executeQuery(sql, par, new
		// BeanListProcessor(WaClassItemVO.class));

		IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		// String sql = "select * from tbm_timeitem where pk_org = '" + pk_org +
		// "' and itemtype = '0'";
		String sql = "select tbm_timeitem.timeitemcode,tbm_timeitem.timeitemname,tbm_timeitem.pk_timeitem,tbm_timeitem.itemtype,tbm_timeitemcopy.pk_timeitemcopy,tbm_timeitemcopy.pk_org,tbm_timeitemcopy.timeitemunit,tbm_timeitemcopy.leavesetperiod "
				+ "from tbm_timeitem inner join tbm_timeitemcopy on tbm_timeitem.pk_timeitem = tbm_timeitemcopy.pk_timeitem "
				+ "and tbm_timeitemcopy.pk_org = '"
				+ pk_org
				+ "' and islactation = 'N' and (tbm_timeitem.itemtype = 0 and tbm_timeitemcopy.enablestate = 2)";
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Collection<LeaveTypeVO> c = (Collection) query.executeQuery(sql, new BeanListProcessor(LeaveTypeVO.class));
		LeaveTypeVO[] leaveTypeVo = (LeaveTypeVO[]) c.toArray(new LeaveTypeVO[0]);

		return leaveTypeVo;

	}

	public UIButton getOkButton() {
		if (okButton == null) {
			okButton = new UIButton(ResHelper.getString("common", "UC001-0000044"));
			okButton.setBounds(48, 128, 80, 22);
		}
		return okButton;
	}

	public UIButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new UIButton(ResHelper.getString("common", "UC001-0000008"));
			cancelButton.setBounds(172, 128, 80, 22);
		}
		return cancelButton;
	}
}