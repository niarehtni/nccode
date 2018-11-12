
package nc.ui.wa.formular;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.hr.dataio.ConstEnumFactory;
/**
 * #21266
 * ���պϼƼӰ�Ѻ������
 * @author yejk
 * @date 2018-9-7
 */
@SuppressWarnings({ "restriction" })
public class OverTimeFeeFunctionEditor extends WaAbstractFunctionEditor {

	private static final long serialVersionUID = 6414923710103945313L;

	private UILabel label = null;	
	private UIComboBox yOrnCBox = null;


	@Override
	public void setModel(AbstractUIAppModel model) {
		// TODO Auto-generated method stub
		super.setModel(model);
	}
	/**
	 * WaParaPanel ������ע�⡣
	 */
	public OverTimeFeeFunctionEditor() {
		super();
		initialize();
	}
	
	private static final String funcname = "@"+ResHelper.getString("6013commonbasic","06013commonbasic0268")+"@";
	//"taxRate";
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
			setSize(300, 300);
			
			add(getUILabel(), getUILabel().getName());
			add(getYOrnCBox(), getYOrnCBox().getName());
			
			add(getOkButton(),getOkButton().getName());
			add(getCancelButton(),getCancelButton().getName());
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	
		initConnection();
	
	}
	
	/**
	 * ���� UILabel3 ����ֵ��
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* ���棺�˷������������ɡ� */
	private UILabel getUILabel() {
		if (label == null) {
			try {
				label = new UILabel();
				label.setName("label");
				label.setText(ResHelper.getString("6013commonbasic","06013commonbasic0269")/*@res "�Ƿ���˰"*/);
				label.setBounds(10, 30, 80, 22);
	
			} catch (java.lang.Throwable labExc) {
				handleException(labExc);
			}
		}
		return label;
	}
	
	/**
	 * ���� refPeriodItem ����ֵ��
	 * @return nc.ui.pub.beans.UIRefPane
	 */
	/* ���棺�˷������������ɡ� */
	private UIComboBox getYOrnCBox() {
		if (yOrnCBox == null) {
			try {
				yOrnCBox = new UIComboBox();
				String[] ml = new String[2];
				ml[0] =ResHelper.getString("6013commonbasic","06013commonbasic0270")/*@res "��"*/;
				ml[1] = ResHelper.getString("6013commonbasic","06013commonbasic0271")/*@res "��"*/;
	
				Integer[] mlDefault = new Integer[]{0,1};
				ConstEnumFactory<Integer> mPairFactory = new ConstEnumFactory<Integer>(ml, mlDefault );
				yOrnCBox.addItems(mPairFactory.getAllConstEnums());
				yOrnCBox.setBounds(100, 30, 140, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return yOrnCBox;
	}

	/**
	 * /**
	 *  * ���������Ϸ���У��
	 *  * @param		����˵��
	 *  * @return		����ֵ
	 *  * @exception �쳣����
	 *  * @see		��Ҫ�μ�����������
	 *  * @since		�������һ���汾���˷�������ӽ���������ѡ��
	 *  * @deprecated�÷����������һ���汾���Ѿ������������滻������ѡ��
	 * *-/
	 *
	 * @return java.lang.String
	 */
	public boolean checkPara(int dataType) {
		try {
			//�жϷǿ�
			String nullstr="";
			if (getYOrnCBox().getSelectedIndex()< 0)
			{
				if (nullstr.length()>0) nullstr += ",";
				nullstr += ResHelper.getString("6013commonbasic","06013commonbasic0269")/*@res "�Ƿ���˰"*/;
			}
			if (nullstr.length()>0)
				throw new Exception(nullstr + ResHelper.getString("6013commonbasic","06013commonbasic0021")/*@res "����Ϊ�գ�"*/);
			return true;
	
		} catch (Exception e) {
			handleException(e);
			showErrMsg(e.getMessage());
			return false;
		}
	}

	/**
	���ڼ���Ŀ����ȡָ������µ�н�����ݣ���������ǰ����µ��ڼ䣬н�ʷ�����н����Ŀ��
	
	 *
	 * @return java.lang.String
	 */
	public String[] getPara() {
		String[] paras = new String[1];
		//�Ƿ���˰  0��  1��
		paras[0] = getYOrnCBox().getSelectdItemValue().toString();
		
		return paras;
	}

}