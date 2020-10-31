package nc.ui.hr.func;

import java.awt.event.ItemListener;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IWaClass;
import nc.ui.hr.itemsource.view.IParaPanel;
import nc.ui.pub.beans.UIAsteriskPanelWrapper;
import nc.ui.pub.beans.UIPanel;
import nc.ui.wa.ctymgt.CtymgtDelegator;
import nc.vo.hr.func.HrFormula;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.WaLoginContext;


/**
 *  н��ҵ�����Ĳ�������panel(���վ���ʱ���ȡ���˵�н��ͳ����)��
 *  ���ߣ�zhoucx
 */
public class WaParaPanel_jdno extends UIPanel implements ItemListener, IParaPanel,IRefPanel {

	private static final long serialVersionUID = -2865048741795672952L;
	private nc.ui.pub.beans.UIComboBox ivjcmbClass = null;
	private nc.ui.pub.beans.UIComboBox ivjcmbItem = null;
	private nc.ui.pub.beans.UITextField ivjtxtEPeriod = null;
	private nc.ui.pub.beans.UITextField ivjtxtSPeriod = null;
	private nc.ui.pub.beans.UILabel ivjUILabel1 = null;
	private nc.ui.pub.beans.UILabel ivjUILabel2 = null;
	private nc.ui.pub.beans.UILabel ivjUILabel4 = null;
	private nc.ui.pub.beans.UILabel ivjUILabel5 = null;
	private nc.ui.pub.beans.UILabel ivjUILabel6 = null;
	private WaClassVO[] classdata = null;//н�ʷ���
	private WaClassItemVO[] itemdata = null;//н����Ŀ
	private int datatype = 1;//������Ҫ���ص��������ͣ�Ĭ��������
	private nc.ui.pub.beans.UIComboBox ivjcmbFunc = null;
	private nc.ui.pub.beans.UILabel ivjUILabel7 = null;
	
	private WaClassUtil waClassUtil  =  WaClassUtil.getInstance();

	private WaLoginContext context = null;
	private HrFormula formular = null;

	public void setContext(WaLoginContext context) {
		this.context = context;
	}


	public WaLoginContext getContext() {
		return this.context;
	}

	public HrFormula getFormular() {
		return formular;
	}
	public void setFormular(HrFormula formular) {
		this.formular = formular;
	}

/**
 * WaParaPanel ������ע�⡣
 */
public WaParaPanel_jdno() {
	super();
	initialize();
}
/**
 * WaParaPanel ������ע�⡣
 * @param p0 java.awt.LayoutManager
 */
public WaParaPanel_jdno(java.awt.LayoutManager p0) {
	super(p0);
}
/**
 * WaParaPanel ������ע�⡣
 * @param p0 java.awt.LayoutManager
 * @param p1 boolean
 */
public WaParaPanel_jdno(java.awt.LayoutManager p0, boolean p1) {
	super(p0, p1);
}
/**
 * WaParaPanel ������ע�⡣
 * @param p0 boolean
 */
public WaParaPanel_jdno(boolean p0) {
	super(p0);
}
/**
 *
 * @author xuanlt on 2010-6-1
 * @see nc.ui.hr.itemsource.view.IParaPanel#checkPara(int)
 */
public void checkPara(int dataType) throws Exception{
	try
	{
		//�жϷǿ�
		String nullstr="";
		if (getcmbClass().isVisible() && getcmbClass().getSelectedIndex()<=0)
			nullstr += ResHelper.getString("6013salaryctymgt","06013salaryctymgt0185")/*@res "н�ʷ���"*/;
		if (getcmbItem().isVisible() && getcmbItem().getSelectedIndex()<=0)
		{
			if (nullstr.length()>0) nullstr += ",";
			nullstr += ResHelper.getString("common","UC000-0003385")/*@res "н����Ŀ"*/;
		}

		if (gettxtSPeriod().isVisible() && (gettxtSPeriod().getText()==null || gettxtSPeriod().getText().trim().length()==0))
		{

			if (nullstr.length()>0) nullstr += ",";
			nullstr += ResHelper.getString("6013salaryctymgt","06013salaryctymgt0214")/*@res "��ʼ�ڼ�"*/;
		}


		if (gettxtEPeriod().isVisible() && (gettxtEPeriod().getText()==null || gettxtEPeriod().getText().trim().length()==0))
		{
			if (nullstr.length()>0) nullstr += ",";
			nullstr += ResHelper.getString("6013salaryctymgt","06013salaryctymgt0216")/*@res "��ֹ�ڼ�"*/;
		}
		if (nullstr.length()>0)
			throw new Exception(nullstr + ResHelper.getString("6013commonbasic","06013commonbasic0021")/*@res "����Ϊ�գ�"*/);

		checkPeriod(gettxtSPeriod().getText());

		checkPeriod(gettxtEPeriod().getText());


		//�ж�н����Ŀ��������
		WaItemVO item = CtymgtDelegator.getWaItem().queryWaItemVOByPk(itemdata[getcmbItem().getSelectedIndex()-1].getPk_wa_item());
		if (item.getIitemtype().intValue() != datatype)
			throw new Exception(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0143")/*@res "ѡ�е�н����Ŀ���Ͳ��Ϸ���"*/);
		if (datatype != new Integer(nc.vo.hr.global.CommonValue.DATATYPE_NUM).intValue()) {
					//�����֣������úϼƺ�ƽ������
			   if (getcmbFunc().getSelectedIndex() <= 1)
						throw new Exception(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0187")/*@res "���������ֶβ�֧��ѡ�еļ��㷽ʽ��"*/);
		  }
		//�ж��ڼ�����
		int speriod = new Integer(gettxtSPeriod().getText()).intValue();
		int eperiod = new Integer(gettxtEPeriod().getText()).intValue();
		//guoqt���ڼ�Ӧ�ÿ�����ͬ�������ֽҵ���д���Ŀ������
		if(eperiod<speriod){
			throw new Exception(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0218")/*@res "��ֹ�ڼ䲻��С����ʼ�ڼ䣡"*/);
		}

	}
		catch (Exception e)
		{
			handleException(e);
			throw e;
		}
}

public void checkPeriod(String period) throws BusinessException{
	UFDate date = PubEnv.getServerDate();
	String format = "";
	if(date.getMonth()<10){
		format = date.getYear()+ "0" + date.getMonth();
	}else{
		format = date.getYear()+ "" + date.getMonth();
	}
	//Ӧ����6λ����
	if(period.length() != 6){
		throw new BusinessException(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0224")/*@res "�ڼ�Ӧ����6λ�� ��ʽ��:"*/ +format);
	}else{
		try{
			Integer.parseInt(period);
		}catch(Exception e){
			throw new BusinessException(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0224")/*@res "�ڼ�Ӧ����6λ�� ��ʽ��:"*/ + format);
		}
	}
}
/**
 *
 * @author xuanlt on 2010-6-1
 * @see nc.ui.hr.itemsource.view.IParaPanel#clearDis()
 */

public void clearDis() {
	getcmbClass().setSelectedIndex(0);
	gettxtSPeriod().setText("");
	gettxtEPeriod().setText("");
}
/**
 *
 */

private nc.ui.pub.beans.UIComboBox getcmbClass() {
	if (ivjcmbClass == null) {
		try {
			ivjcmbClass = new nc.ui.pub.beans.UIComboBox();
			ivjcmbClass.setName("cmbClass");
			ivjcmbClass.setBounds(100, 6, 140, 22);

			ivjcmbClass.setTranslate(true);

		} catch (java.lang.Throwable ivjExc) {


			handleException(ivjExc);
		}
	}
	return ivjcmbClass;
}

UIAsteriskPanelWrapper  cmbClassUI  = null;
private UIAsteriskPanelWrapper getcmbClassUI(){
	if(cmbClassUI == null){
		cmbClassUI = new UIAsteriskPanelWrapper(getcmbClass());
		cmbClassUI.setBounds(93, 6, 147, 22);
		cmbClassUI.setMustInputItem(true);
	}
	return cmbClassUI;
}





    private UIAsteriskPanelWrapper  cmbItemUI = null;
private UIAsteriskPanelWrapper getcmbItemUI() {

	if(cmbItemUI==null){
		cmbItemUI = new UIAsteriskPanelWrapper(getcmbItem());
		cmbItemUI.setBounds(93, 34, 147, 22);
		cmbItemUI.setMustInputItem(true);
	}

	return cmbItemUI;

}



    private UIAsteriskPanelWrapper  cmbPeriodUI = null;
private UIAsteriskPanelWrapper gettxtSPeriodUI() {

	if(cmbPeriodUI==null){
		cmbPeriodUI = new UIAsteriskPanelWrapper(gettxtSPeriod());
		cmbPeriodUI.setBounds(93, 90, 147, 20);
		cmbPeriodUI.setMustInputItem(true);
	}

	return cmbPeriodUI;

}

private UIAsteriskPanelWrapper  txtEPeriodUI = null;
private UIAsteriskPanelWrapper gettxtEPeriodUI() {

	if(txtEPeriodUI==null){
		txtEPeriodUI = new UIAsteriskPanelWrapper(gettxtEPeriod());
		txtEPeriodUI.setBounds(93, 116, 147, 20);
		txtEPeriodUI.setMustInputItem(true);
	}

	return txtEPeriodUI;

}


/**
 *
 * @author xuanlt on 2010-6-1
 * @return
 * @return  nc.ui.pub.beans.UIComboBox
 */
private nc.ui.pub.beans.UIComboBox getcmbFunc() {
	if (ivjcmbFunc == null) {
		try {
			ivjcmbFunc = new nc.ui.pub.beans.UIComboBox();
			ivjcmbFunc.setName("cmbFunc");
			ivjcmbFunc.setBounds(100, 62, 140, 22);
			String[] ml = new String[4];
			ml[0] = ResHelper.getString("6013salaryctymgt","06013salaryctymgt0225")/*@res "ȡ�ϼ�"*/;

			ml[1] = ResHelper.getString("6013salaryctymgt","06013salaryctymgt0189")/*@res "ȡƽ��ֵ"*/;

			ml[2] = ResHelper.getString("6013salaryctymgt","06013salaryctymgt0190")/*@res "ȡ���ֵ"*/;

			ml[3] = ResHelper.getString("6013salaryctymgt","06013salaryctymgt0191")/*@res "ȡ��Сֵ"*/;

			String[] mlDefault = new String[]{ResHelper.getString("6013salaryctymgt","06013salaryctymgt0225")/*@res "ȡ�ϼ�"*/,ResHelper.getString("6013salaryctymgt","06013salaryctymgt0189")/*@res "ȡƽ��ֵ"*/,ResHelper.getString("6013salaryctymgt","06013salaryctymgt0190")/*@res "ȡ���ֵ"*/,ResHelper.getString("6013salaryctymgt","06013salaryctymgt0191")/*@res "ȡ��Сֵ"*/};/* -=notranslate=- */
			nc.hr.utils.PairFactory mPairFactory=new nc.hr.utils.PairFactory(ml, mlDefault );

			ivjcmbFunc.addItems(mPairFactory.getAllConstEnums());


		} catch (java.lang.Throwable ivjExc) {


			handleException(ivjExc);
		}
	}
	return ivjcmbFunc;
}
/**
 *
 * @author xuanlt on 2010-6-1
 * @return
 * @return  nc.ui.pub.beans.UIComboBox
 */
private nc.ui.pub.beans.UIComboBox getcmbItem() {
	if (ivjcmbItem == null) {
		try {
			ivjcmbItem = new nc.ui.pub.beans.UIComboBox();
			ivjcmbItem.setName("cmbItem");
			ivjcmbItem.setBounds(100, 34, 140, 22);

			ivjcmbItem.setTranslate(true);

		} catch (java.lang.Throwable ivjExc) {


			handleException(ivjExc);
		}
	}
	return ivjcmbItem;
}
public String getCurSelectClass() {
	String str=null;
	if(getcmbClass().getSelectedIndex()>0){
		str=classdata[getcmbClass().getSelectedIndex()-1].getPrimaryKey();
	}
	return  str;
}


/**
 *
 * @author xuanlt on 2010-6-1
 * @return
 * @return  int
 */
public int getDatatype() {
	return datatype;
}

/**
 * ������н�ʷ�����н����Ŀ��ͳ�Ʒ�ʽ����ʼ�ڼ䡢��ֹ�ڼ�
 * @author xuanlt on 2010-6-1
 * @see nc.ui.hr.itemsource.view.IParaPanel#getPara()
 */
public String[] getPara() throws Exception {
	String[] paras = new String[5];
	//���
	paras[0] = classdata[getcmbClass().getSelectedIndex()-1].getPrimaryKey();
	if (!getcmbItem().isVisible()) return new String[]{paras[0]};
	//��Ŀ		//�ж�н����Ŀ��������
	WaItemVO item = CtymgtDelegator.getWaItem().queryWaItemVOByPk(itemdata[getcmbItem().getSelectedIndex()-1].getPk_wa_item());


	paras[1] = item.getItemkey().toString().trim();
	//ͳ�Ʒ�ʽ
	int index = getcmbFunc().getSelectedIndex();
		if (index == 0){
			paras[2]= "sum";
		}else if (index == 1){
			paras[2]= "avg";
		}
		else if (index == 2){
			paras[2]= "max";
		}else{
			paras[2]= "min";
		}

		paras[3] = gettxtSPeriod().getText().trim();
		paras[4] = gettxtEPeriod().getText().trim();


	return paras;
}
///**
// *
// */
//public String getParaStr() {
//	String str="";
//	str += getcmbClass().getSelectedItem().toString().trim();
//	if (!getcmbItem().isVisible()) return str;
//	str +=",";
//	str += getcmbItem().getSelectedItem().toString().trim();
//	str += ",";
//	str += getcmbFunc().getSelectedItem().toString().trim();
//	str +=",";
//	str += gettxtSPeriod().getText().trim();
//	str +=",";
//	str += gettxtEPeriod().getText().trim();
//	return str;
//}
/**
 *
 * @author xuanlt on 2010-6-1
 * @return
 * @return  nc.ui.pub.beans.UITextField
 */
private nc.ui.pub.beans.UITextField gettxtEPeriod() {
	if (ivjtxtEPeriod == null) {
		try {
			ivjtxtEPeriod = new nc.ui.pub.beans.UITextField();
			ivjtxtEPeriod.setBounds(100, 116, 140, 20);
			ivjtxtEPeriod.setMaxLength(6);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjtxtEPeriod;
}
/**
 *
 * @author xuanlt on 2010-6-1
 * @return
 * @return  nc.ui.pub.beans.UITextField
 */
private nc.ui.pub.beans.UITextField gettxtSPeriod() {
	if (ivjtxtSPeriod == null) {
		try {
			ivjtxtSPeriod = new nc.ui.pub.beans.UITextField();
			ivjtxtSPeriod.setBounds(100, 90, 140, 20);
			ivjtxtSPeriod.setMaxLength(6);
		} catch (java.lang.Throwable ivjExc) {


			handleException(ivjExc);
		}
	}
	return ivjtxtSPeriod;
}
/**
 *
 * @author xuanlt on 2010-6-1
 * @return
 * @return  nc.ui.pub.beans.UILabel
 */

private nc.ui.pub.beans.UILabel getUILabel1() {
	if (ivjUILabel1 == null) {
		try {
			ivjUILabel1 = new nc.ui.pub.beans.UILabel();
			ivjUILabel1.setName("UILabel1");
			ivjUILabel1.setText(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0192")/*@res "н�ʷ�����"*/);
			ivjUILabel1.setBounds(10, 5, 80, 22);


		} catch (java.lang.Throwable ivjExc) {


			handleException(ivjExc);
		}
	}
	return ivjUILabel1;
}
/**
 *
 * @author xuanlt on 2010-6-1
 * @return
 * @return  nc.ui.pub.beans.UILabel
 */
private nc.ui.pub.beans.UILabel getUILabel2() {
	if (ivjUILabel2 == null) {
		try {
			ivjUILabel2 = new nc.ui.pub.beans.UILabel();
			ivjUILabel2.setName("UILabel2");
			ivjUILabel2.setText(ResHelper.getString("6013commonbasic","06013commonbasic0024")/*@res "н����Ŀ��"*/);
			ivjUILabel2.setBounds(10, 32, 80, 22);


		} catch (java.lang.Throwable ivjExc) {


			handleException(ivjExc);
		}
	}
	return ivjUILabel2;
}

/**
 *
 * @author xuanlt on 2010-6-1
 * @return
 * @return  nc.ui.pub.beans.UILabel
 */
private nc.ui.pub.beans.UILabel getUILabel4() {
	if (ivjUILabel4 == null) {
		try {
			ivjUILabel4 = new nc.ui.pub.beans.UILabel();
			ivjUILabel4.setName("UILabel4");
			ivjUILabel4.setText(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0220")/*@res "��ʼ�ڼ䣺"*/);
			ivjUILabel4.setBounds(10, 86, 80, 22);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUILabel4;
}
/**
 *
 * @author xuanlt on 2010-6-1
 * @return
 * @return  nc.ui.pub.beans.UILabel
 */
private nc.ui.pub.beans.UILabel getUILabel5() {
	if (ivjUILabel5 == null) {
		try {
			ivjUILabel5 = new nc.ui.pub.beans.UILabel();
			ivjUILabel5.setName("UILabel5");
			ivjUILabel5.setText(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0221")/*@res "��ֹ��ȣ�"*/);
			ivjUILabel5.setBounds(10, 140, 80, 22);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUILabel5;
}
/**
 * ���� UILabel6 ����ֵ��
 * @return nc.ui.pub.beans.UILabel
 */
/* ���棺�˷������������ɡ� */
private nc.ui.pub.beans.UILabel getUILabel6() {
	if (ivjUILabel6 == null) {
		try {
			ivjUILabel6 = new nc.ui.pub.beans.UILabel();
			ivjUILabel6.setName("UILabel6");
			ivjUILabel6.setText(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0222")/*@res "��ֹ�ڼ䣺"*/);
			ivjUILabel6.setBounds(10, 113, 80, 22);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUILabel6;
}
/**
 *
 * @author xuanlt on 2010-6-1
 * @return
 * @return  nc.ui.pub.beans.UILabel
 */
private nc.ui.pub.beans.UILabel getUILabel7() {
	if (ivjUILabel7 == null) {
		try {
			ivjUILabel7 = new nc.ui.pub.beans.UILabel();
			ivjUILabel7.setName("UILabel7");
			ivjUILabel7.setText(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0226")/*@res "ͳ�Ʒ�ʽ��"*/);
			ivjUILabel7.setBounds(10, 59, 80, 22);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUILabel7;
}
/**
 * ÿ�������׳��쳣ʱ������
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {
	Logger.error(exception.getMessage(),exception);
}

/**
 * ��ѯ����Ա��ǰ������Դ��֯�¿ɼ���н�ʷ���
 * @author xuanlt on 2010-2-25
 * @return  void
 */
public void initCmbClass() {
try{
	classdata =getVisibleWaclasz();
	if(classdata!=null && classdata.length>0){
		  getcmbClass().removeAllItems();
		  getcmbClass().addItem("");
		  for (int i = 0; i < classdata.length; i++) {
				getcmbClass().addItem(classdata[i].getMultilangName());
		  }
	}
  } catch (Exception e) {
	  Logger.error(e.getMessage(),e);
  }
}

/**
 * ���û��ڵ�ǰ��֯�¿��Կ���������н�ʷ���
 * @author xuanlt on 2010-2-24
 * @return  void
 */
private WaClassVO[] getVisibleWaclasz(){
	WaClassVO[] vos = new WaClassVO[0];
	try {
		vos =  CtymgtDelegator.getWaClass().queryWaClass(getContext());
	} catch (BusinessException e) {
		Logger.error(e.getMessage(), e);
	}
	return vos;
}
/**
 *
 * @author xuanlt on 2010-6-1
 * @return  void
 */
public void initData() {
	try
	{
		//��Ҫ����д����б�
		getcmbClass().removeItemListener(this);
		getcmbClass().removeAllItems();
		getcmbClass().addItem(" ");
		classdata =	NCLocator.getInstance().lookup(IWaClass.class).queryWaClassByOrg(this.getContext());
		if (classdata != null && classdata.length >0)
		{
			for (int i = 0; i < classdata.length; i ++)
				getcmbClass().addItem(classdata[i].getMultilangName());
		}
		getcmbClass().addItemListener(this);
		getcmbClass().setSelectedIndex(0);
	}catch (Exception e)
	{
		handleException(e);
	}
}
/**
 *
 * @author xuanlt on 2010-6-1
 * @return  void
 */
private void initialize() {
	try {
		setName("WaParaPanel");
		setLayout(null);
		setSize(240, 200);
		add(getUILabel1(), getUILabel1().getName());
		add(getcmbClassUI(), getcmbClass().getName());
		add(getUILabel2(), getUILabel2().getName());
		add(getcmbItemUI(), getcmbItem().getName());
		add(getUILabel4(), getUILabel4().getName());
		add(gettxtSPeriodUI(), gettxtSPeriod().getName());
		add(getUILabel6(), getUILabel6().getName());
		add(gettxtEPeriodUI(), gettxtEPeriod().getName());
		add(getUILabel7(), getUILabel7().getName());
		add(getcmbFunc(), getcmbFunc().getName());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	getcmbClass().addItemListener(this);
}
/**
 *
 * @author xuanlt on 2010-6-1
 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
 */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	if (e.getStateChange() != 2)
		return;
	if (e.getSource().equals(getcmbClass())) {
		int index = getcmbClass().getSelectedIndex();
		getcmbItem().removeAllItems();
		getcmbItem().addItem(" ");
		if (index > 0 && getcmbItem().isVisible()) {
			try {
				itemdata =CtymgtDelegator.getClassItemQueryService().queryAllClassItemsForFormular(classdata[index - 1].getPrimaryKey());
				if (itemdata != null && itemdata.length > 0)
					for (int i = 0; i < itemdata.length; i++)
						getcmbItem().addItem(itemdata[i].getMultilangName());
			} catch (Exception ex) {
				handleException(ex);
			}
			//Ĭ��ѡ�п���
			getcmbItem().setSelectedIndex(0);
		}
	}
}

/**
 *
 * @author xuanlt on 2010-6-1
 * @see nc.ui.hr.itemsource.view.IParaPanel#setDatatype(int)
 */
public void setDatatype(int newDatatype) {
	datatype = newDatatype;
}
	/**
	 *
	 * @author xuanlt on 2010-6-1
	 * @see nc.ui.hr.itemsource.view.IParaPanel#updateDis(nc.vo.hr.func.FunctableItemVO[])
	 */
public void updateDis(nc.vo.hr.func.FunctableItemVO[] paras) {
return;
}
/**
 *
 * @author xuanlt on 2010-6-1
 * @see nc.ui.hr.itemsource.view.IParaPanel#updateDis(int)
 */
public void updateDis(int index) {

	initData();
}
/**
 *
 * @author xuanlt on 2010-6-1
 * @see nc.ui.hr.itemsource.view.IParaPanel#updateDis(java.lang.String)
 */
public void updateDis(String funname) {

}


/**
 * @author zhangg on 2010-6-3
 * @see nc.ui.hr.itemsource.view.IParaPanel#setCurrentItemKey(java.lang.String)
 */
@Override
public void setCurrentItemKey(String itemKey) {
	// TODO Auto-generated method stub

}

}