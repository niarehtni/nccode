package nc.ui.wa.formular;

import java.awt.Dimension;

import nc.hr.utils.ResHelper;
import nc.ui.bd.ref.model.DefdocGridRefModel;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.uif2.model.AbstractUIAppModel;

/**
 * 06013commonbasic0002=Ӧ˰�Ӱ�н��
 * 
 * @author Ares
 * @date 2019��1��25��11:25:24
 */
@SuppressWarnings({ "restriction" })
public class LeavePaysFunctionEditor extends WaAbstractFunctionEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2625416048712727809L;

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
			groupRef.setPk_org(getModel().getContext().getPk_org());
		} catch (Exception e) {
			handleException(e);
		}
	}

	/**
	 * WaParaPanel ������ע�⡣
	 */
	public LeavePaysFunctionEditor() {
		super();
		initialize();
	}

	private static final String funcname = "@"
			+ ResHelper.getString("6013commonbasicadd20180426", "06013commonbasic0002") + "@";

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
			setSize(300, 150);
			setTitle("Ո�x�񅢔�");

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
				this.groupLabel.setBounds(10, 70, 80, 22);
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
			groupRef.setPk_org(null);
			// refGroupIns.set
			groupRef.setVisible(true);
			groupRef.setPreferredSize(new Dimension(50, 20));
			groupRef.setButtonFireEvent(true);
			groupRef.setName("groupRef");
			groupRef.setBounds(100, 70, 140, 22);
		}
		return groupRef;
	}

	/**
	 * /** * ���������Ϸ���У�� * @param ����˵�� * @return ����ֵ * @exception �쳣���� * @see
	 * ��Ҫ�μ����������� * @since �������һ���汾���˷��������ӽ���������ѡ�� *
	 * 
	 * @deprecated�÷����������һ���汾���Ѿ������������滻������ѡ�� *-/
	 * 
	 * @return java.lang.String
	 */
	public boolean checkPara(int dataType) {
		try {
			// �жϷǿ�
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
		String[] paras = new String[1];

		paras[0] = "\"" + getGroupRef().getRefPK() + "\"";

		return paras;
	}

	public UIButton getOkButton() {
		if (okButton == null) {
			okButton = new UIButton(ResHelper.getString("common", "UC001-0000044"));
			okButton.setBounds(48, 98, 80, 22);
		}
		return okButton;
	}

	public UIButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new UIButton(ResHelper.getString("common", "UC001-0000008"));
			cancelButton.setBounds(172, 98, 80, 22);
		}
		return cancelButton;
	}
}