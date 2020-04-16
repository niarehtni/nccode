package nc.ui.ta.psndoc.action;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.org.IPrimaryOrgQry;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hr.frame.dialog.HrDialog;
import nc.ui.hr.uif2.view.PrimaryOrgPanel;
import nc.ui.org.ref.HROrgDefaultRefModel;
import nc.ui.org.ref.OrgVOsDefaultRefModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.ta.region.action.RegionUtils;
import nc.ui.ta.region.ref.TBMRegionRefModel;
import nc.vo.cache.CacheManager;
import nc.vo.cache.ICache;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psndoc.TBMPsndocCommonValue;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class TBMPsndocBatchEditDlg extends HrDialog implements ActionListener {

	//������֯��refmodelΪ�˺�����֯�ı���һ�£����ǻ�����ʹ��orgPanel�е�refmodel������ᵼ�»���
	private OrgVOsDefaultRefModel refModel;
	private static final long serialVersionUID = 1L;
	private UIPanel ContentPanel = null;
	private UIPanel CenterPane = null;
	// ���ڷ�ʽ
	private UICheckBox chkboxTbmprop = null;
	private UIComboBox cmboxTbmprop = null;
	// �Ӱ�ܿ�
	private UICheckBox chkboxTbmotcontrol = null;
	private UIComboBox cmboxTbmotcontrol = null;
	// ��ʱ��̬
	private UICheckBox chkboxTbmweekform = null;
	private UIComboBox cmboxTbmweekform = null;
	// ��ʼ����
//	private UICheckBox chkboxBeginDate = null;
//	private UIRefPane refBeginDate = null;

	// ��������
	private UICheckBox chkboxEndDate = null;
	private UIRefPane refEndDate = null;

	// ������֯
	private UICheckBox chkboxAdminOrg = null;
	private UIRefPane refAdminOrg = null;

	// ���ڵص�
	private UICheckBox chkboxAddress = null;
	private UIRefPane refAddress = null;
	// �������� heqiaoa
	private UICheckBox chkboxRegion = null;
	private UIRefPane refRegion = null;
	private TBMRegionRefModel refRegionModel = null;
	
	// �������Ӳ�ͬ����Θ��R  by George 20200326 ���� #33851
	// ��ͬ����M�����Օ�
	private UICheckBox chkboxNotsyncal = null;
	

	// ��������
	private LoginContext context;
	// �����޸�ֵ
	private HashMap<String, Object> batchEditValue = null;

	//
	private Boolean workplaceflag = false;
	private String dateException;
	private TBMPsndocVO[] vos=null;
	public String getDateException() {
		return dateException;
	}

	public void setDateException(String dateException) {
		this.dateException = dateException;
	}

	public TBMPsndocBatchEditDlg(Container parent, LoginContext context, boolean workplaceflag,TBMPsndocVO[] vos) {
		super(parent,false);
		this.context = context;
		this.workplaceflag = workplaceflag;
		this.vos=vos;
		initUI();
		initialize();
	}

	private void initialize() {
		try {
			setName("TBMPsndocBatchEditDlg");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(330, 280);
			if (workplaceflag) {
				setSize(330, 320);
			}
			setTitle(ResHelper.getString("6017psndoc","06017psndoc0065")
/*@res "�����޸ĵ���"*/);
		//	setContentPane(getContentPanel());
			setComEditable();
			addActionListener();
		} catch (java.lang.Throwable ivjExc) {
		}
	}
	private void setComEditable() {
		// ���ø��ؼ����ɱ༭
		getCmboxTbmprop().setEnabled(false);
		getCmboxTbmweekform().setEnabled(false);
		getCmboxTbmotcontrol().setEnabled(false);
//		getRefBeginDate().setEnabled(false);
		getRefEndDate().setEnabled(false);
		//getRefEndDate().setBorder(new LineBorder( Color.white));
		getRefAdminOrg().setEnabled(false);
		//getRefAdminOrg().setBorder(new LineBorder( Color.white));
		getRefAddress().setEnabled(false);
		//getRefAddress().setBorder(new LineBorder( Color.white));
		getRefRegion().setEnabled(false);
	}
	

	private void addActionListener() {

		getBtnCancel().addActionListener(this);
		getChkboxTbmprop().addActionListener(this);
		getChkboxTbmweekform().addActionListener(this);
		getChkboxTbmotcontrol().addActionListener(this);
		getChkboxEndDate().addActionListener(this);
		getChkboxAddress().addActionListener(this);
		getChkboxAdminOrg().addActionListener(this);
		getChkboxRegion().addActionListener(this);
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == getBtnOk()) {
			if (checkOK()) {
				onOK();
			}
		} else if (e.getSource() == getBtnCancel()) {
			closeCancel();
		} else if (e.getSource() == getChkboxTbmprop()) {
				getCmboxTbmprop().setEnabled(getChkboxTbmprop().isSelected());

		} else if (e.getSource() == getChkboxTbmweekform()) {
			getCmboxTbmweekform().setEnabled(getChkboxTbmweekform().isSelected());

		} else if (e.getSource() == getChkboxTbmotcontrol()) {
			getCmboxTbmotcontrol().setEnabled(getChkboxTbmotcontrol().isSelected());

		}/**else if (e.getSource() == getChkboxBeginDate()) {
				getRefBeginDate().setEnabled(getChkboxBeginDate().isSelected());
		}*/ else if (e.getSource() == getChkboxEndDate()) {
				getRefEndDate().setEnabled(getChkboxEndDate().isSelected());
//				if(getChkboxEndDate().isSelected())
//				getRefEndDate().setBorder(new LineBorder( Color.black));
//				else{
//				getRefEndDate().setBorder(new LineBorder( Color.white));
//				}
		} else if (e.getSource() == getChkboxAddress()) {
				getRefAddress().setEnabled(getChkboxAddress().isSelected());
//				if(getChkboxAddress().isSelected())
//				getChkboxAddress().setBorder(new LineBorder( Color.black));
//				else{
//					getChkboxAddress().setBorder(new LineBorder( Color.white));
//				}
		} else if (e.getSource() == getChkboxAdminOrg()) {
				getRefAdminOrg().setEnabled(getChkboxAdminOrg().isSelected());
//				if(getChkboxAdminOrg().isSelected())
//				getChkboxAdminOrg().setBorder(new LineBorder( Color.black));
//				else{
//					getChkboxAdminOrg().setBorder(new LineBorder( Color.white));
//				}
		} else if(e.getSource() == getChkboxRegion()){
			getRefRegion().setEnabled(getChkboxRegion().isSelected());
		}
	}
	private void onOK() {
		batchEditValue = new HashMap<String, Object>();
		if (getChkboxTbmprop().isSelected()) {
			batchEditValue.put(TBMPsndocVO.TBM_PROP, Integer.valueOf(getCmboxTbmprop().getSelectedIndex()));
		}
		if (getChkboxTbmweekform().isSelected()) {
			batchEditValue.put(TBMPsndocVO.WEEKFORM, Integer.valueOf(getCmboxTbmweekform().getSelectedIndex()));
		}
		if (getChkboxTbmotcontrol().isSelected()) {
			batchEditValue.put(TBMPsndocVO.OVERTIMECONTROL, Integer.valueOf(getCmboxTbmotcontrol().getSelectedIndex()));
		}
//		if (getChkboxBeginDate().isSelected()) {
//			String date = getRefBeginDate().getText();
//			UFLiteralDate begindate = null;
//			begindate = UFLiteralDate.getDate(date.toString().substring(0, 10));
//			batchEditValue.put(TBMPsndocVO.BEGINDATE, begindate);
//		}
		if (getChkboxEndDate().isSelected()) {
//			String date = getRefEndDate().getText();ȡ���ĸ�ʽ��yyyymmdd
			String date = getRefEndDate().getValueObj().toString();//��ʽΪyyyy-mm-dd
			UFLiteralDate enddate = null;
			if (date != null && date.length() == 10) {
				enddate = UFLiteralDate.getDate(date.toString().substring(0, 10));
			} else {
				enddate = UFLiteralDate.getDate(TBMPsndocCommonValue.END_DATA);
			}
			batchEditValue.put(TBMPsndocVO.ENDDATE, enddate);
		}
		if(getChkboxAdminOrg().isSelected()) {
			String adminOrg = getRefAdminOrg().getRefPK();
			batchEditValue.put(TBMPsndocVO.PK_ADMINORG, adminOrg);
		}
		if (getChkboxAddress().isSelected()) {
			String addressid = getRefAddress().getRefPK();
			if (addressid != null && !addressid.trim().equals("")) {
				batchEditValue.put(TBMPsndocVO.PK_PLACE, addressid);
			}
		}
		if(getChkboxRegion().isSelected()){
			String pk_region = getRefRegion().getRefPK();
			if(StringUtils.isNotBlank(pk_region)){
				batchEditValue.put(TBMPsndocVO.PK_REGION, pk_region);
			}
		}
		
		// �������Ӳ�ͬ����Θ��R  by George 20200326 ���� #33851
		// �����޸�߉݋��λ�� ��ͬ����M�����Օ�(isNotsyncal) �Д�
		if(getChkboxNotsyncal().isSelected()){
			batchEditValue.put(TBMPsndocVO.NOTSYNCAL, new UFBoolean(true));
		} else {
			batchEditValue.put(TBMPsndocVO.NOTSYNCAL, new UFBoolean(false));
		}
		
		closeOK();
	}

	private boolean checkOK() {
		// �������Ӳ�ͬ����Θ��R  by George 20200326 ���� #33851
		// �������߉݋��λ�� ��ͬ����M�����Օ�(isNotsyncal) �Д࣬����һ�����޸ĵă��ݣ��@���Д����õ�
//		if (!getChkboxTbmotcontrol().isSelected() &&!getChkboxTbmweekform().isSelected() &&!getChkboxTbmprop().isSelected() && !getChkboxEndDate().isSelected()
//				&& !getChkboxAddress().isSelected() && !getChkboxAdminOrg().isSelected()
//				&& !getChkboxRegion().isSelected()) {
//			MessageDialog.showErrorDlg(this, null, ResHelper.getString("6017psndoc","06017psndoc0066")/*@res "û��Ҫ�޸ĵ����ݣ�����ѡ��Ҫ�޸ĵ����ݣ�"*/);
//			return false;
//		}
		
		if (getChkboxTbmprop().isSelected() && getCmboxTbmprop().getSelectedIndex() <= 0) {
			MessageDialog.showErrorDlg(this, null, ResHelper.getString("6017psndoc","06017psndoc0067")/*@res "��ѡ���ڷ�ʽ��"*/);
			return false;
		}
		if (getChkboxTbmweekform().isSelected() && getCmboxTbmweekform().getSelectedIndex() <= 0) {
			MessageDialog.showErrorDlg(this, null, ResHelper.getString("6017psndoc","2psndoc-00002020")/*@res "��ѡ��ʱ��̬��"*/);
			return false;
		}
		if (getChkboxTbmotcontrol().isSelected() && getCmboxTbmotcontrol().getSelectedIndex() <= 0) {
			MessageDialog.showErrorDlg(this, null, ResHelper.getString("6017psndoc","2psndoc-00002021")/*@res "��ѡ��Ӱ�ܿأ�"*/);
			return false;
		}
//		if (getChkboxBeginDate().isSelected() && StringUtils.isBlank(getRefBeginDate().getText())) {
//			MessageDialog.showErrorDlg(this, null, "������ʼ���ڲ���Ϊ�գ������뵵����ʼ���ڣ�");
//			return false;
//		}
		if(getChkboxAdminOrg().isSelected() && StringUtils.isBlank(getRefAdminOrg().getRefPK())){
			MessageDialog.showErrorDlg(this, null, ResHelper.getString("6017psndoc","06017psndoc0068")/*@res "������֯����Ϊ�գ���ѡ�������֯��"*/);
			return false;
		}
		if(getChkboxRegion().isSelected() && StringUtils.isBlank(getRefRegion().getRefPK())){
			MessageDialog.showErrorDlg(this, null, ResHelper.getString("6017hrta","06017hrta0074")
/*@res "����������Ϊ�գ���ѡ��������"*/);
			return false;
		}

		UFLiteralDate enddate=null;
		if (getChkboxEndDate().isSelected()) {
			String date = getRefEndDate().getValueObj().toString();
			 enddate = null;
			if (date != null && date.length() == 10) {
				enddate = UFLiteralDate.getDate(date.toString().substring(0, 10));
			} else {
				enddate = UFLiteralDate.getDate(TBMPsndocCommonValue.END_DATA);
			}
		}
		if(enddate!=null){
			for(TBMPsndocVO vo: vos){
				String checkInf = null;
				try {
					vo.setEnddate(enddate);
					checkInf = checkTBMPsndocDate(vo);
					if (checkInf != null){
						MessageDialog.showErrorDlg(this, null,MessageFormat.format(checkInf, getPsnName(vo.getPk_psndoc())));
						return false;
					}
				} catch (BusinessException e) {
					Logger.error(e.getMessage());
				}
			}
		}
		return true;
	}

	/**
	 * У�鿼�ڵ�������
	 * @param vo
	 * @return
	 */
	private String checkTBMPsndocDate(TBMPsndocVO vo) {
		if (vo.getEnddate() == null || vo.getEnddate().toString().startsWith(TBMPsndocCommonValue.END_DATA_PRE))
			return null;
		if (vo.getBegindate() == null) {
			return ResHelper.getString("6017psndoc","06017psndoc0125")/*@res "{0}�Ŀ��ڵ����Ŀ�ʼ���ڲ���Ϊ�գ�"*/;
		} else if (vo.getBegindate().compareTo(vo.getEnddate()) >= 0) {
			return ResHelper.getString("6017psndoc","06017psndoc0126")/*@res "{0}�Ŀ��ڵ����Ŀ�ʼ���ڲ�С�ڽ������ڣ�"*/;
		}
		return null;
	}

	//��ѯ��Ա
	private String getPsnName(String pk_psndoc) throws BusinessException {
		PsndocVO psndocvo =  (PsndocVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByPk(PsndocVO.class,pk_psndoc,
    			new String[]{PsndocVO.NAME,PsndocVO.NAME2,PsndocVO.NAME3,PsndocVO.NAME4,PsndocVO.NAME5,PsndocVO.NAME6});
		if(psndocvo == null)
			return null;
		return psndocvo.getMultiLangName();

	}

	private UIPanel getContentPanel() {
		if (ContentPanel == null) {
			try {
				ContentPanel = new UIPanel();
				ContentPanel.setName("ContentPanel");
				ContentPanel.setLayout(new java.awt.BorderLayout());
//				UIPanel top = new UIPanel();
//				top.setBackground(getBackground());
//				top.setSize(35, 35);
//				ContentPanel.add(top,BorderLayout.NORTH);
				ContentPanel.add(getCenterPane(), "Center");
				//ContentPanel.add(getSouthPane(), "South");

			} catch (java.lang.Throwable ivjExc) {
			}
		}
		return ContentPanel;
	}

	private UIPanel getCenterPane() {
		if (CenterPane == null) {
			try {
				CenterPane = new UIPanel();
				CenterPane.setName("CenterPane");
				FormLayout layout = new FormLayout(
//						"right:pref, 20dlu, left:pref,10dlu,right:pref, 10dlu, left:pref, 100dlu", "20,default");
						"10px,10px,130px,10px,100px,10px", "20,default");
				DefaultFormBuilder builder = new DefaultFormBuilder(layout, CenterPane);
				builder.setBorder(BorderFactory.createTitledBorder(""));
				builder.nextLine();

				builder.append("",getChkboxTbmprop());
				builder.append(getCmboxTbmprop());
				builder.append("",getChkboxTbmweekform());
				builder.append(getCmboxTbmweekform());
				builder.append("",getChkboxTbmotcontrol());
				builder.append(getCmboxTbmotcontrol());
				builder.nextLine();
//				builder.append("", getChkboxBeginDate());
//				builder.append("��ʼ����", getRefBeginDate());
//				builder.nextLine();
				builder.append("", getChkboxEndDate());
				builder.append(getRefEndDate());
				builder.nextLine();
				builder.append("", getChkboxAdminOrg());
				builder.append(getRefAdminOrg());
				builder.nextLine();
				builder.append("", getChkboxRegion());
				builder.append(getRefRegion());
				
				// �������Ӳ�ͬ����Θ��R  by George 20200326 ���� #33851
				// �����޸ď���ҕ��������߉݋��λ�� ��ͬ����M�����Օ�(isNotsyncal) 
				builder.nextLine();
				builder.append("", getChkboxNotsyncal());
				
				if (workplaceflag) {
					builder.nextLine();
					builder.append("", getChkboxAddress());
					builder.append( getRefAddress());

				}

			} catch (java.lang.Throwable ivjExc) {
				Logger.error(ivjExc.toString(), ivjExc);
			}
		}
		CenterPane.setBackground(null);
		return CenterPane;
	}

//	private UIPanel getSouthPane() {
//		if (SouthPane == null) {
//			try {
//				SouthPane = new UIPanel();
//				SouthPane.setName("SouthPane");
//				SouthPane.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
//				SouthPane.setPreferredSize(new Dimension(400, 50));
//				SouthPane.add(getBtnCancel(), getBtnCancel().getName());
//			} catch (java.lang.Throwable ivjExc) {
//			}
//		}
//		return SouthPane;
//	}

	public UICheckBox getChkboxTbmprop() {
		if (chkboxTbmprop == null) {
			chkboxTbmprop = new UICheckBox(ResHelper.getString("6017psndoc","06017psndoc0069")
					/*@res "���ڷ�ʽ"*/);
			chkboxTbmprop.setName("chkboxTbmprop");
//			chkboxTbmprop.setPreferredSize(new Dimension(30, 22));
		}
		return chkboxTbmprop;
	}
	
	/** 
	 * �������Ӳ�ͬ����Θ��R  by George 20200326 ���� #33851
	 * �����޸Ŀ��ڵ���������߉݋��λ�� ��ͬ����M�����Օ�(isNotsyncal) 
	 */
	public UICheckBox getChkboxNotsyncal() {
		if(chkboxNotsyncal == null) {
			chkboxNotsyncal = new UICheckBox("��ͬ����M�����Օ�");
			chkboxNotsyncal.addActionListener(this);
		}
		return chkboxNotsyncal;
	}

	@SuppressWarnings("unchecked")
	public UIComboBox getCmboxTbmprop() {
		if (cmboxTbmprop == null) {
			cmboxTbmprop = new UIComboBox();
			cmboxTbmprop.setName("cmboxTbmprop");
			cmboxTbmprop.addItem("");
			cmboxTbmprop.addItem(ResHelper.getString("6017psndoc","06017psndoc0072")
/*@res "�ֹ�����"*/);
			cmboxTbmprop.addItem(ResHelper.getString("6017psndoc","06017psndoc0073")
/*@res "��������"*/);
			cmboxTbmprop.setSelectedIndex(0);
//			cmboxTbmprop.setPreferredSize( new Dimension( 122 , 20 ) );
		}
		return cmboxTbmprop;
	}
	
	public UICheckBox getChkboxTbmotcontrol() {
		if (chkboxTbmotcontrol == null) {
			chkboxTbmotcontrol = new UICheckBox(ResHelper.getString("6017psndoc","2psndoc-00002012")
					/*@res "�Ӱ�ܿ�"*/);
			chkboxTbmotcontrol.setName("chkboxTbmotcontrol");
//			chkboxTbmprop.setPreferredSize(new Dimension(30, 22));
		}
		return chkboxTbmotcontrol;
	}
	//by he
	public UIComboBox getCmboxTbmotcontrol() {
		if (cmboxTbmotcontrol == null) {
			cmboxTbmotcontrol = new UIComboBox();
			cmboxTbmotcontrol.setName("cmboxTbmotcontrol");
			cmboxTbmotcontrol.addItem("");
			cmboxTbmotcontrol.addItem(ResHelper.getString("6017psndoc","2psndoc-00002013")
/*@res"һ����"*/);
			cmboxTbmotcontrol.addItem(ResHelper.getString("6017psndoc","2psndoc-00002014")
/*@res "������"*/);
			cmboxTbmotcontrol.setSelectedIndex(0);
//			cmboxTbmprop.setPreferredSize( new Dimension( 122 , 20 ) );
		}
		return cmboxTbmotcontrol;
	}
	//by he
	public UICheckBox getChkboxTbmweekform() {
		if (chkboxTbmweekform == null) {
			chkboxTbmweekform = new UICheckBox(ResHelper.getString("6017psndoc","2psndoc-00002015")
					/*@res "��ʱ��̬"*/);
			chkboxTbmweekform.setName("chkboxTbmweekform");
//				chkboxTbmprop.setPreferredSize(new Dimension(30, 22));
		}
		return chkboxTbmweekform;
	}
	public UIComboBox getCmboxTbmweekform() {
		if (cmboxTbmweekform == null) {
			cmboxTbmweekform = new UIComboBox();
			cmboxTbmweekform.setName("cmboxTbmweekform");
			cmboxTbmweekform.addItem("");
			cmboxTbmweekform.addItem(ResHelper.getString("6017psndoc","2psndoc-00002016")
/*@res "������ʱ"*/);
			cmboxTbmweekform.addItem(ResHelper.getString("6017psndoc","2psndoc-00002017")
/*@res "���ܱ���"*/);
			cmboxTbmweekform.addItem(ResHelper.getString("6017psndoc","2psndoc-00002018")
					/*@res "���ܱ���"*/);
			cmboxTbmweekform.addItem(ResHelper.getString("6017psndoc","2psndoc-00002019")
					/*@res "���ܱ���"*/);
			cmboxTbmweekform.setSelectedIndex(0);
//			cmboxTbmprop.setPreferredSize( new Dimension( 122 , 20 ) );
		}
		return cmboxTbmweekform;
	}
//	public UICheckBox getChkboxBeginDate() {
//		if (chkboxBeginDate == null) {
//			chkboxBeginDate = new UICheckBox();
//			chkboxBeginDate.setName("chkboxBeginDate");
//			chkboxBeginDate.setPreferredSize(new Dimension(30, 22));
//		}
//		return chkboxBeginDate;
//
//	}
//
//	public UIRefPane getRefBeginDate() {
//		if (refBeginDate == null) {
//			refBeginDate = new UIRefPane();
//			refBeginDate.setName("refBeginDate");
//			refBeginDate.setRefNodeName(IRefConst.REFNODENAME_LITERALCALENDAR);
//			refBeginDate.setPreferredSize(new Dimension(122, 20));
//		}
//		return refBeginDate;
//	}

	public UICheckBox getChkboxEndDate() {
		if (chkboxEndDate == null) {
			chkboxEndDate = new UICheckBox(ResHelper.getString("common","UC000-0003230")
					/*@res "��������"*/);
			chkboxEndDate.setName("chkboxEndDate");
//			chkboxEndDate.setPreferredSize(new Dimension(30, 22));
			chkboxEndDate.setBackground(null);
		}
		return chkboxEndDate;
	}

	public UIRefPane getRefEndDate() {
		if (refEndDate == null) {
			refEndDate = new UIRefPane();
			refEndDate.setName("refEndDate");
			refEndDate.setRefNodeName(IRefConst.REFNODENAME_LITERALCALENDAR);
			refEndDate.setPreferredSize(new Dimension(122, 20));
		}
		return refEndDate;
	}

	public UICheckBox getChkboxAdminOrg() {
		if(chkboxAdminOrg==null) {
			chkboxAdminOrg = new UICheckBox(ResHelper.getString("6017psndoc","06017psndoc0070")
					/*@res "������֯"*/);
			chkboxAdminOrg.setName("chkboxAdminOrg");
//			chkboxAdminOrg.setPreferredSize(new Dimension(30, 22));
			chkboxAdminOrg.setBackground(null);
		}
		return chkboxAdminOrg;
	}

	public UIRefPane getRefAdminOrg() {
		if (refAdminOrg == null) {
			refAdminOrg = new UIRefPane();
			refAdminOrg.setName("refAdminOrg");
			refAdminOrg.setRefModel(getRefModel());
			refAdminOrg.setPreferredSize(new Dimension(122, 20));
		}
		return refAdminOrg;
	}

	public UICheckBox getChkboxAddress() {
		if (chkboxAddress == null) {
			chkboxAddress = new UICheckBox(ResHelper.getString("6017psndoc","06017psndoc0071")
					/*@res "���ڵص�"*/);
			chkboxAddress.setName("chkboxAddress");
//			chkboxAddress.setPreferredSize(new Dimension(30, 22));
		}
		return chkboxAddress;
	}
	public UICheckBox getChkboxRegion(){
		if (chkboxRegion == null) {
			chkboxRegion = new UICheckBox(ResHelper.getString("6017hrta","06017hrta0075")
/*@res "��������"*/);
			chkboxRegion.setName("chkboxRegion");
			chkboxRegion.setEnabled(RegionUtils.isTBMMobileEnabled(context.getPk_org()));
		}
		return chkboxRegion;
	}
	/***************************************************************************
     * �õ���Ȩ�޵���֯<br>
     * Created on 2011-5-11 16:52:15<br>
     * @return OrgVO[]
     * @author Rocex Wang
     ***************************************************************************/
    protected OrgVO[] getPermOrgVOs()
    {
        String strCacheKey = PubEnv.getPk_group() + "." + PubEnv.getPk_user() + "." + context.getNodeCode();

        ICache cache = CacheManager.getInstance().getCache(PrimaryOrgPanel.class.getName());

        OrgVO[] orgVOs = (OrgVO[]) cache.get(strCacheKey);

        if (orgVOs == null || orgVOs.length == 0)
        {
            try
            {
                orgVOs = NCLocator.getInstance().lookup(IPrimaryOrgQry.class)
                .queryPrimaryOrgVOs(IPrimaryOrgQry.CONTROLTYPE_HRADMINORG, context.getFuncInfo().getFuncPermissionPkorgs());

                cache.put(strCacheKey, orgVOs);
            }
            catch (BusinessException e)
            {
                Logger.error(e);
            }
        }

        return orgVOs;
    }
    private TBMRegionRefModel getRegionRefModel(){
    	if(null == refRegionModel){
    		refRegionModel = new TBMRegionRefModel();
    		refRegionModel.setRefTitle(ResHelper.getString("6017hrta","06017hrta0075")
/*@res "��������"*/);
    		refRegionModel.setPk_hrorg(context.getPk_org());
    	}
		return refRegionModel;
	}
    /**
     * ������֯���գ�Ϊ�˺�����֯���ձ���һ��
     * @return
     */
    public OrgVOsDefaultRefModel getRefModel()
    {
        if (refModel != null)
        {
            return refModel;
        }

        OrgVO funcPermOrgVOs[] = getPermOrgVOs();

        refModel = new OrgVOsDefaultRefModel(funcPermOrgVOs)
        {
            @SuppressWarnings("unchecked")
			@Override
            public Vector reloadData()
            {
                ICache cache = CacheManager.getInstance().getCache(PrimaryOrgPanel.class.getName());

                cache.flush();

                try
                {
                    Field field = OrgVOsDefaultRefModel.class.getDeclaredField("vos");
                    field.setAccessible(true);

                    field.set(this, getPermOrgVOs());
                }
                catch (Exception ex)
                {
                    Logger.error(ex.getMessage(), ex);
                }

                return super.reloadData();
            }
        };

        refModel.setUseDataPower(true);
        refModel.setCacheEnabled(false);
        refModel.setRefNodeName(ResHelper.getString("6001uif2", "06001uif20053")
                /* @res "������Դ��֯" */);

        return refModel;
    }

	public UIRefPane getRefAddress() {
		if (refAddress == null) {
			refAddress = new UIRefPane();
			refAddress.setName("refAddress");
			refAddress.setRefNodeName("���ڵص�(�Զ��嵵��)");/*-=notranslate=-*/
			refAddress.setPreferredSize( new Dimension( 122 , 20 ) );
			refAddress.setPk_org(context.getPk_org());
		}
		return refAddress;
	}

	private UIRefPane getRefRegion(){
		if(null == refRegion){
			refRegion =  new UIRefPane();
			refRegion.setName("refRegion");
			refRegion.setRefModel(getRegionRefModel());
			refRegion.setPreferredSize( new Dimension( 122 , 20 ) );
			refRegion.setPk_org(context.getPk_org());
		}
		return refRegion;
	}
	public HashMap<String, Object> getBatchEditValue() {
		return batchEditValue;
	}

	@Override
	protected JComponent createCenterPanel() {
		return getContentPanel();
	}
}