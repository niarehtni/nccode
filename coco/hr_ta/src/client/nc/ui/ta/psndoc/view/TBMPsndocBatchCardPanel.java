package nc.ui.ta.psndoc.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ITimeRuleQueryService;
import nc.ui.bd.ref.IRefConst;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.hrta.tbmpsndoc.OvertimecontrolEunm;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psndoc.TbmPropEnum;
import nc.vo.ta.psndoc.WorkWeekFormEnum;
import nc.vo.ta.timerule.TimeRuleVO;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * ���ڵ������������ͱ䶯��Աʱ�Ŀ��ڵ�����Ƭҳ��
 * @author caiyl
 *
 */
public class TBMPsndocBatchCardPanel extends UIPanel implements ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = -2370770492582735483L;

	private AbstractUIAppModel model;

	private UIRefPane refBeginDate = null;
	private UIRefPane refPlace = null;
//	private UIRefPane refEndDate = null;
	private UIComboBox tbmPropComboBox;	//���ڷ�ʽ
	private UICheckBox postDateChcBx;	//��ְ����
	//by he
	private UIComboBox tbmWeekFormComboBox;	//��ʱ��̬
	private UIComboBox tbmOtControComboBox;	//�Ӱ�ع�
	public void init() {
		 UIPanel mainPanel = new UIPanel();
        setLayout(new BorderLayout());
		add(mainPanel,BorderLayout.CENTER);
		UIPanel leftPanel = new UIPanel();
		leftPanel.setPreferredSize(new Dimension(150,100));
		UIPanel upPanel = new UIPanel();
		upPanel.setPreferredSize(new Dimension(150,50));
		add(leftPanel, BorderLayout.WEST);
		add(upPanel, BorderLayout.NORTH);
        FormLayout layout = new FormLayout(
        		  "left:100px, 10px, fill:100px, 15px",
        		  "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout,mainPanel);
        builder.append(new UILabel(ResHelper.getString("6017psndoc","06017psndoc0069")
/*@res "���ڷ�ʽ"*/));
        builder.append(getTbmPropComboBox());
        builder.append(getPostDateChcBx());
        builder.nextLine();
        builder.append(new UILabel(ResHelper.getString("common","UC000-0001892")
/*@res "��ʼ����"*/));
        builder.append(getRefBeginDate());
        getRefBeginDate().getUITextField().setShowMustInputHint(!getPostDateChcBx().isSelected());
        builder.nextLine();
        builder.append(new UILabel(ResHelper.getString("6017psndoc","2psndoc-00002015")
        		/*@res "��ʱ��̬"*/));
        		        builder.append(getTbmWeekFormComboBox());
        builder.append(new UILabel(ResHelper.getString("6017psndoc","2psndoc-00002012")
        		/*@res "�Ӱ�ع�"*/));
        		        builder.append(getTbmOtControComboBox());
        //������ÿ��ڵص��쳣������뿼�ڵص�
        if(isCheckinPermit()) {
        	builder.append(new UILabel(ResHelper.getString("6017psndoc","06017psndoc0071")
/*@res "���ڵص�"*/));
        	builder.append(getRefPlace());
        }

	}
	/**
	 * �Ӱ�ع�
	 * @return
	 */
	public UIComboBox getTbmOtControComboBox() {
		if(tbmOtControComboBox == null) {
			tbmOtControComboBox = new UIComboBox();
			tbmOtControComboBox.addItem(new DefaultConstEnum(OvertimecontrolEunm.MANUAL_CHECK.value(),ResHelper.getString("6017psndoc","2psndoc-00002013")
/*@res "һ����"*/));
			tbmOtControComboBox.addItem(new DefaultConstEnum(OvertimecontrolEunm.MACHINE_CHECK.value(),ResHelper.getString("6017psndoc","2psndoc-00002014")
/*@res "������"*/));
		}
		return tbmOtControComboBox;
	}
	/**
	 * ��ʱ��̬
	 * @return
	 */
	public UIComboBox getTbmWeekFormComboBox() {
		if(tbmWeekFormComboBox == null) {
			tbmWeekFormComboBox = new UIComboBox();
			tbmWeekFormComboBox.addItem(new DefaultConstEnum(WorkWeekFormEnum.ONEWEEK.value(),ResHelper.getString("6017psndoc","2psndoc-00002016")
/*@res "������ʱ"*/));
			tbmWeekFormComboBox.addItem(new DefaultConstEnum(WorkWeekFormEnum.TWOWEEKS.value(),ResHelper.getString("6017psndoc","2psndoc-00002017")
/*@res "���ܱ���"*/));
			tbmWeekFormComboBox.addItem(new DefaultConstEnum(WorkWeekFormEnum.FOURWEEKS.value(),ResHelper.getString("6017psndoc","2psndoc-00002018")
					/*@res "���ܱ���"*/));
			tbmWeekFormComboBox.addItem(new DefaultConstEnum(WorkWeekFormEnum.EIGHTWEEKS.value(),ResHelper.getString("6017psndoc","2psndoc-00002019")
					/*@res "���ܱ���"*/));
		}
		return tbmWeekFormComboBox;
	}

	/**
	 * ���ڵص��Ƿ��쳣
	 * @return
	 */
	protected boolean isCheckinPermit() {
		TimeRuleVO timerule = null;
        try {
        	timerule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(getModel().getContext().getPk_org());
        } catch(Exception e) {
        	Logger.error(e.getMessage(), e);
        }

        if(timerule == null || timerule.getWorkplaceflag() == null || !timerule.getWorkplaceflag().booleanValue())
        	return false;
        return true;
	}

	/**
	 * ��ʼ����
	 */
	protected UIRefPane getRefBeginDate() {
		if (refBeginDate == null) {
			refBeginDate = new UIRefPane();
			refBeginDate.setName("refBeginDate");
			refBeginDate.setRefNodeName(IRefConst.REFNODENAME_LITERALCALENDAR);
			refBeginDate.getUITextField().setFormatShow(true);
			//��ʼ����Ĭ��Ϊָ�����ڣ�����Ĭ�ϵ�ǰ����
			refBeginDate.setValueObj(getBusDate());
		}
		return refBeginDate;
	}
	/**
	 * ���ڵص�
	 */
	protected UIRefPane getRefPlace() {
		if (refPlace == null) {
			refPlace = new UIRefPane();
			refPlace.setName("refPlace");
			refPlace.setRefNodeName("���ڵص�(�Զ��嵵��)");/*-=notranslate=-*/
			refPlace.setPk_org(getModel().getContext().getPk_org());
		}
		return refPlace;
	}
	/**
	 * ȡ�ÿ��ڵص��ֵ
	 * @return
	 */
	public String getPkPlace() {
		return getRefPlace().getRefPK();
	}
	/**
	 * ȡ�ÿ�ʼ���ڵ�ֵ
	 * @return
	 */
	public UFLiteralDate getBeginDate() {
		return (UFLiteralDate)getRefBeginDate().getValueObj();
	}

	public UIComboBox getTbmPropComboBox() {
		if(tbmPropComboBox == null) {
			tbmPropComboBox = new UIComboBox();
			tbmPropComboBox.addItem(new DefaultConstEnum(TbmPropEnum.MACHINE_CHECK.value(),ResHelper.getString("6017psndoc","06017psndoc0073")
/*@res "��������"*/));
			tbmPropComboBox.addItem(new DefaultConstEnum(TbmPropEnum.MANUAL_CHECK.value(),ResHelper.getString("6017psndoc","06017psndoc0072")
/*@res "�ֹ�����"*/));
		}
		return tbmPropComboBox;
	}

	public UICheckBox getPostDateChcBx() {
		if(postDateChcBx == null) {
			postDateChcBx = new UICheckBox(ResHelper.getString("common","UC000-0000632")
/*@res "��ְ����"*/);
			postDateChcBx.addActionListener(this);
		}
		return postDateChcBx;
	}

	private UFLiteralDate getBusDate() {
		// ȡ��ǰ����
		UFDate busDate = WorkbenchEnvironment.getInstance().getBusiDate();
		return UFLiteralDate.getDate(busDate.toString().substring(0, 10));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(getPostDateChcBx())) {

			//�������ְ���ڣ���ʼ����
			getRefBeginDate().setEnabled(!getPostDateChcBx().isSelected());
			//������ǰ��յ�ְ���ڣ���ʼ����Ϊ����
			getRefBeginDate().getUITextField().setShowMustInputHint(!getPostDateChcBx().isSelected());
			getRefBeginDate().setValueObj(getPostDateChcBx().isSelected()?null:getBusDate());
		}
	}

	public void stopEditing() {
		getRefBeginDate().stopEditing();
		getRefPlace().stopEditing();
	}

	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
	}
}