package nc.ui.ta.psncalendar.view.batchchangecalendarday;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.PairFactory;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapProcessor;
import nc.ui.bd.ref.IRefConst;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.beans.border.UITitledBorder;
import nc.ui.ta.calendar.pub.CalendarAppEventConst;

import nc.ui.ta.pub.BUPanel;
import nc.ui.ta.pub.selpsn.ConditionSelPsnDateScopePanel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.IAppModel;
import nc.vo.bd.workcalendar.CalendarDateType;
import nc.vo.bd.workcalendar.WorkCalendarDateVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

/**
 * 
 * @author Ares.Tank ���õ����������panel
 *
 */
public class ShiftSetPanelForBatchChangeCalendarDay extends UIPanel
		implements ValueChangedListener, AppEventListener, ActionListener {

	private static final long serialVersionUID = 749514391008736111L;

	IAppModel model = null;

	ConditionSelPsnDateScopePanel selPsnPanel;
	UIPanel dateTypePanel;// ԭ����°�ε�panel
	UIRefPane firstDateRefPane;// ��һ�����ڲ���
	UIRefPane secondDateRefPane;// �ڶ������ڲ���
	UIRefPane firstDateRefText;// ��һ����������������
	UIRefPane secondDateRefText;// �ڶ�����������������
	UIRefPane changeDateRefPane;// ������ڲ���
	UIRefPane beforeChangeDateRefText;// ���ǰ��������������
	UIComboBox afterChangeDateRefText;// �������������������
	UIRefPane supplementDayNum;// ��������
	UICheckBox changeDayTypeCheckBox;// �Ƿ��ǵ���������
	// UICheckBox oldShiftCheckBox;
	private BUPanel buPanel = null;
	private UIPanel topPanel = null;
	private String pk_org;

	// private IAppModel model = null;

	public ShiftSetPanelForBatchChangeCalendarDay() {

	}

	public void init() {
		// ��ʾ����radiobutton��panel
		UIPanel topSubPanel1 = new UIPanel();
		topSubPanel1.setLayout(new FlowLayout());
		topSubPanel1.add(getDayTypePanel());
		topSubPanel1.setSize(new Dimension(600, 300));
		topPanel = new UIPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add(topSubPanel1, BorderLayout.NORTH);
		topPanel.setSize(800, 600);
		setLayout(new BorderLayout());
		add(topPanel, BorderLayout.NORTH);
		getSupplementDayNumPanel().setValueObj("0.00");
	}

	protected ConditionSelPsnDateScopePanel getSelPsnPanel() {
		if (selPsnPanel == null) {
			selPsnPanel = new ConditionSelPsnDateScopePanel();
			selPsnPanel.setModel((AbstractUIAppModel) getModel());
			selPsnPanel.init();
		}
		return selPsnPanel;
	}

	public BUPanel getBuPanel() {
		if (buPanel == null) {
			buPanel = new BUPanel();
			buPanel.setModel((AbstractUIAppModel) getModel());
			buPanel.init();
			buPanel.getBuRef().addValueChangedListener(this);
		}
		return buPanel;
	}

	public IAppModel getModel() {
		return model;
	}

	public void setModel(IAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	@Override
	public void handleEvent(AppEvent event) {
		if (event.getType().equals(CalendarAppEventConst.BU_CHANGED)) {
			getDayTypePanelFirst().setValueObj(null);
			getDayTypePanelSecond().setValueObj(null);
		}

	}

	public UIPanel getDayTypePanel() {
		if (dateTypePanel == null) {
			dateTypePanel = new UIPanel();
			FormLayout layout = new FormLayout(
					"right:max(40dlu;pref), 3dlu, 80dlu, 7dlu, " 
							+ "right:max(40dlu;pref), 3dlu, 80dlu, 7dlu, " 
							
					, "");
			DefaultFormBuilder builder = new DefaultFormBuilder(layout, dateTypePanel);
			// builder.append(getOldShiftCheckBox());
			//builder.append(new UILabel(" "));
			builder.append(getChangeDayTypeCheckBox());
			builder.append(ResHelper.getString("twhr_psncalendar", "psncalendar-0007")
			/* @res "�Ƿ����������" */);
			
			builder.nextLine();
			builder.append(new UILabel(" "));
			builder.nextLine();
			builder.append(ResHelper.getString("twhr_psncalendar", "psncalendar-0005")
			/* @res "��������" */);
			//builder.append(new UILabel(" "));
			builder.append(getDayTypePanelFirst());

			builder.append(ResHelper.getString("twhr_psncalendar", "psncalendar-0006")
			/* @res "����������" */);
			builder.append(getDayTypeTextFirst());

			builder.nextLine();
			
			// builder.append(new UILabel(" "));
			builder.append(ResHelper.getString("twhr_psncalendar", "psncalendar-0005")
			/* @res "��������" */);
			//builder.append(new UILabel(" "));
			builder.append(getDayTypePanelSecond());
			builder.append(ResHelper.getString("twhr_psncalendar", "psncalendar-0006")
			/* @res "����������" */);
			builder.append(getDayTypeTextSecond());
			builder.nextLine();
			builder.append(new UILabel(" "));
			builder.nextLine();
			builder.append(ResHelper.getString("twhr_psncalendar", "psncalendar-0008")
			/* @res "�������" */);
			//builder.append(new UILabel(" "));
			builder.append(getChangeDayTypePanel());
			builder.append(ResHelper.getString("twhr_psncalendar", "psncalendar-0009")
			/* @res "���ǰ������" */);
			builder.append(getBeforeChangeDateRefText());
			builder.nextLine();
			builder.append(new UILabel(" "));
			builder.append(new UILabel(" "));
			//builder.append(new UILabel(" "));
			builder.append(ResHelper.getString("twhr_psncalendar", "psncalendar-0010")
			/* @res "�����������" */);
			builder.append(getAfterChangeDateRefText());
			builder.nextLine();
			builder.append(new UILabel(" "));
			builder.append(new UILabel(" "));
			//builder.append(new UILabel(" "));
			builder.append(ResHelper.getString("twhr_psncalendar", "psncalendar-0011")
			/* @res "ת������ʱ(����)" */);
			builder.append(getSupplementDayNumPanel());
			builder.nextLine();
			builder.append(new UILabel(" "));
			builder.nextLine();
			
			
			dateTypePanel.setBorder(new UITitledBorder(ResHelper.getString("twhr_psncalendar", "psncalendar-0004")
			/* @res "������Ϣ" */));
		}
		return dateTypePanel;
	}

	protected UICheckBox getChangeDayTypeCheckBox() {
		if (changeDayTypeCheckBox == null) {
			changeDayTypeCheckBox = new UICheckBox();
			changeDayTypeCheckBox.setSelected(true);
			changeDayTypeCheckBox.addActionListener(this);
		}
		return changeDayTypeCheckBox;
	}

	protected UIRefPane getDayTypePanelFirst() {
		if (firstDateRefPane == null) {
			firstDateRefPane = new UIRefPane();
			firstDateRefPane.setName("refFirstDate");
			firstDateRefPane.setRefNodeName(IRefConst.REFNODENAME_LITERALCALENDAR);
			firstDateRefPane.getUITextField().setFormatShow(true);
			firstDateRefPane.addValueChangedListener(this);
			firstDateRefPane.getUITextField().setShowMustInputHint(true);
		}

		return firstDateRefPane;
	}

	protected UIRefPane getDayTypePanelSecond() {
		if (secondDateRefPane == null) {
			secondDateRefPane = new UIRefPane();
			secondDateRefPane.setName("refSecondDate");
			secondDateRefPane.setRefNodeName(IRefConst.REFNODENAME_LITERALCALENDAR);
			secondDateRefPane.addValueChangedListener(this);
			secondDateRefPane.getUITextField().setShowMustInputHint(true);// ����
		}
		return secondDateRefPane;
	}

	protected UIRefPane getChangeDayTypePanel() {
		if (changeDateRefPane == null) {
			changeDateRefPane = new UIRefPane();
			changeDateRefPane.setName("refChangeDate");
			changeDateRefPane.setRefNodeName(IRefConst.REFNODENAME_LITERALCALENDAR);
			changeDateRefPane.addValueChangedListener(this);
			changeDateRefPane.getUITextField().setShowMustInputHint(false);
			changeDateRefPane.setRefEditable(false);
		}
		return changeDateRefPane;
	}

	protected UIRefPane getDayTypeTextFirst() {
		if (firstDateRefText == null) {
			firstDateRefText = new UIRefPane();
			firstDateRefText.setName("TextFirstDate");
			firstDateRefText.setRefNodeName(IRefConst.REFNODENAME_TEXTFIELD);
			firstDateRefText.getUITextField().setFormatShow(true);
			firstDateRefText.addValueChangedListener(this);
			firstDateRefText.getUITextField().setShowMustInputHint(false);
			// firstDateRefText.setButtonVisible(false);
			firstDateRefText.setRefEditable(false);
		}

		return firstDateRefText;
	}

	protected UIRefPane getDayTypeTextSecond() {
		if (secondDateRefText == null) {
			secondDateRefText = new UIRefPane();
			secondDateRefText.setName("TextSecondDate");
			secondDateRefText.setRefNodeName(IRefConst.REFNODENAME_TEXTFIELD);
			secondDateRefText.addValueChangedListener(this);
			secondDateRefText.getUITextField().setShowMustInputHint(false);
			// secondDateRefText.setButtonVisible(false);
			secondDateRefText.setRefEditable(false);
		}
		return secondDateRefText;
	}

	protected UIRefPane getBeforeChangeDateRefText() {
		if (beforeChangeDateRefText == null) {
			beforeChangeDateRefText = new UIRefPane();
			beforeChangeDateRefText.setName("TextBeforeChangeDate");
			
			beforeChangeDateRefText.setRefNodeName(IRefConst.REFNODENAME_TEXTFIELD);
			beforeChangeDateRefText.addValueChangedListener(this);
			beforeChangeDateRefText.getUITextField().setShowMustInputHint(false);
			// secondDateRefText.setButtonVisible(false);
			beforeChangeDateRefText.setRefEditable(false);
		}
		return beforeChangeDateRefText;
	}

	protected UIComboBox getAfterChangeDateRefText() {
		if (afterChangeDateRefText == null) {

			afterChangeDateRefText = new UIComboBox();
			afterChangeDateRefText.setName("TextAfterChangeDate");
			afterChangeDateRefText.setBounds(100, 0, 140, 22);
			afterChangeDateRefText.setVisible(true);
			afterChangeDateRefText.setTranslate(true);
			afterChangeDateRefText.setShowMustInputHint(false);
			// afterChangeDateRefText.setEditable(false);
			afterChangeDateRefText.setEnabled(false);

			// set values
			String[] names = new String[CalendarDateType.values().length];
			String[] values = new String[CalendarDateType.values().length];
			
			for (int i = 0; i < CalendarDateType.values().length; i++) {
				names[i] = CalendarDateType.values()[i].getName();
				values[i] = String.valueOf(CalendarDateType.values()[i].getIndex());
			}
			
			PairFactory mpf = new PairFactory(names, values);
			// set items
			afterChangeDateRefText.addItems(mpf.getAllConstEnums());

		}
		return afterChangeDateRefText;
	}
	/**
	 * �������������͵�ö��ֵ 
	 * Ա����������	��������ȫ��
	*  ������		->  	������   0
	*�ǹ�����	->  	��Ϣ��   1
	*��������	->  	�ڼ���   2
	*������		->		������   4
	 * @return
	 */
	@SuppressWarnings("finally")
	public Integer getafterChangeDateType(){
		Integer dayType = 0;
		try{
			dayType =  Integer.parseInt(getAfterChangeDateRefText().getSelectdItemValue().toString());
		}catch(Exception e){
			
			dayType = 0;
		}finally{
			switch(dayType){
			case 0:return 0;
			case 1:return 99;//��������δ����
			case 2:return 2;
			case 3:return 4;
			case 4:return 1;
			default:return 0;
			}
		}
		
		
	}

	protected UIRefPane getSupplementDayNumPanel() {
		if (supplementDayNum == null) {
			supplementDayNum = new UIRefPane();
			supplementDayNum.setName("SupplementDayNum");
			supplementDayNum.setValueObj("0.00");
			supplementDayNum.setRefNodeName(IRefConst.REFNODENAME_TEXTFIELD);
			supplementDayNum.addValueChangedListener(this);
			supplementDayNum.getUITextField().setShowMustInputHint(false);
			// secondDateRefText.setButtonVisible(false);
			supplementDayNum.setRefEditable(false);
			supplementDayNum.setButtonVisible(false);
		}
		return supplementDayNum;
	}

	public UFLiteralDate getFirstDate() {
		if (StringUtils.isEmpty(getDayTypePanelFirst().getText()))
			return null;
		return new UFLiteralDate(getDayTypePanelFirst().getValueObj().toString());
	}

	public void setFirstDate(UFLiteralDate beginDate) {
		if (beginDate == null)
			return;
		getDayTypePanelFirst().setValueObj(beginDate);
	}

	public void setSecondDate(UFLiteralDate endDate) {
		if (endDate == null)
			return;
		getDayTypePanelSecond().setValueObj(endDate);
	}

	public UFLiteralDate getSecondDate() {
		if (StringUtils.isEmpty(getDayTypePanelSecond().getText()))
			return null;
		return new UFLiteralDate(getDayTypePanelSecond().getValueObj().toString());
	}

	public UFLiteralDate getChangeDate() {
		if (StringUtils.isEmpty(getChangeDayTypePanel().getText()))
			return null;
		return new UFLiteralDate(getChangeDayTypePanel().getValueObj().toString());
	}

	public String getSupplementDayNum() {
		if (StringUtils.isEmpty(getSupplementDayNumPanel().getText()))
			return "0.00";
		return getSupplementDayNumPanel().getText();
	}

	public void setSupplementDayNum(String supplementDayNum) {
		getSupplementDayNumPanel().setValueObj(supplementDayNum);
	}

	/**
	 * ת������ʱУ���߼�
	 * 
	 * @return
	 */
	public boolean supplementDayNumValidator() {
		String inputValue = getSupplementDayNum();
		// ��ֵΪ����������,��ȥ�ж�
		if (getSupplementDayNumPanel().getUITextField().isShowMustInputHint()) {
			// ֵΪ��,������Ϊ��
			if (StringUtils.isEmpty(inputValue)) {
				return false;
			}

			try {
				Double.parseDouble(inputValue);
			} catch (Exception e) {
				return false;
			}

			/*
			 * TODO ->�ڲ������ýڵ��ѯ�����ݼٵ���� ->���ݼ����ڵ��ȡ���ݼ�������Ϣ
			 * ->���ݼ�����λ����Сʱ�䵥λУ���������Ϣ
			 */

		}

		return true;

	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		if (event.getSource() == getDayTypePanelFirst()) {// ����û������˿�ʼ���ڲ���
			UFLiteralDate beginDate = getFirstDate();
			if (beginDate == null)
				return;
			/*
			 * UFLiteralDate beginDate = getFirstDate();
			 * if(beginDate!=null&&beginDate.after(endDate)){//
			 * ����û��ѿ�ʼ���ڵ����˽�������֮������������Զ���ɿ�ʼ����
			 * getDayTypePanelSecond().setValueObj(beginDate); }
			 */
			getDayTypeTextFirst().setValue(getCalendarType(beginDate));
			return;
		}
		if (event.getSource() == getDayTypePanelSecond()) {// ����û������˽������ڲ���
			UFLiteralDate endDate = getSecondDate();
			if (endDate == null)
				return;
			/*
			 * UFLiteralDate endDate = getSecondDate();
			 * if(endDate!=null&&beginDate.after(endDate)){//����û��ѽ������ڵ����˿�ʼ����֮ǰ��
			 * ��ʼ�����Զ���ɽ������� getDayTypePanelFirst().setValueObj(endDate); }
			 */
			getDayTypeTextSecond().setValueObj(getCalendarType(endDate));
			return;
		}
		if (event.getSource() == getChangeDayTypePanel()) {// ����û������˽������ڲ���
			UFLiteralDate changeDate = getChangeDate();
			if (changeDate == null)
				return;
			/*
			 * UFLiteralDate endDate = getSecondDate();
			 * if(endDate!=null&&beginDate.after(endDate)){//����û��ѽ������ڵ����˿�ʼ����֮ǰ��
			 * ��ʼ�����Զ���ɽ������� getDayTypePanelFirst().setValueObj(endDate); }
			 */
			getBeforeChangeDateRefText().setValueObj(getCalendarType(changeDate));
			return;
		}
		// System.out.println("�Զ���������������");
		if (event.getSource() == getSupplementDayNumPanel()) {
			DecimalFormat df = new DecimalFormat("#.00");
			try{
				
				String oldData = getSupplementDayNumPanel().getUITextField().getValue().toString();
				String newData = df.format(Double.parseDouble(oldData));
				
				if(!newData.equals(oldData)){
					getSupplementDayNumPanel().setValueObj(newData);
				}
				
			}catch(Exception e){
				getSupplementDayNumPanel().setValueObj("0.00");
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		// ���û���ѡ'����������'ʱ:�ûұ������
		if (evt.getSource() == getChangeDayTypeCheckBox()) {
			if (getChangeDayTypeCheckBox().isSelected()) {
				getDayTypePanelFirst().setRefEditable(true);
				getDayTypePanelSecond().setRefEditable(true);
				getDayTypePanelFirst().getUITextField().setShowMustInputHint(true);
				getDayTypePanelSecond().getUITextField().setShowMustInputHint(true);

				getChangeDayTypePanel().setRefEditable(false);
				getChangeDayTypePanel().getUITextField().setShowMustInputHint(false);

				getAfterChangeDateRefText().setShowMustInputHint(false);
				getAfterChangeDateRefText().setEnabled(false);

				getSupplementDayNumPanel().setRefEditable(false);
				getSupplementDayNumPanel().getUITextField().setShowMustInputHint(false);

			} else {
				getDayTypePanelFirst().setRefEditable(false);
				getDayTypePanelSecond().setRefEditable(false);
				getDayTypePanelFirst().getUITextField().setShowMustInputHint(false);
				getDayTypePanelSecond().getUITextField().setShowMustInputHint(false);

				getChangeDayTypePanel().setRefEditable(true);
				getChangeDayTypePanel().getUITextField().setShowMustInputHint(true);

				getAfterChangeDateRefText().setShowMustInputHint(true);
				getAfterChangeDateRefText().setEnabled(true);

				getSupplementDayNumPanel().setRefEditable(true);
				getSupplementDayNumPanel().getUITextField().setShowMustInputHint(true);
			}
		}
		// System.out.println("�Զ���������������");
				
		
		/*
		 * Object source = evt.getSource(); if(source != getOldShiftCheckBox()
		 * ){ // super.actionPerformed(evt); return; }
		 * getOldShiftRefPane().setEnabled(getOldShiftCheckBox().isSelected());
		 */
		// System.out.println("У���߼�......................................");
	}

	public void setPK_BU(String pk_org) {
		this.pk_org = pk_org;

	}

	public String getPk_Org() {
		return pk_org;
	}

	/**
	 * �����û���ѡ��ʱ������ȡ��һ�������������
	 * 
	 * @return ���������Ͷ���
	 */
	@SuppressWarnings("unchecked")
	public String getCalendarType(UFLiteralDate date) {
		String returnResult = "";
		if (null == date) {
			return returnResult;
		}
		String sqlPuls = "SELECT bd_workcalendardate.datetype" + " FROM bd_workcalendardate"
				+ " WHERE bd_workcalendardate.pk_workcalendar =" + " (SELECT bd_workcalendar.pk_workcalendar"
				+ " FROM bd_workcalendar" + " WHERE bd_workcalendar.isdefaultcalendar = 'Y'" + " AND dr = 0)"
				+ " and bd_workcalendardate.calendardate  = '" + date.toPersisted() + "'";
		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		Map<String, Object> result = null;
		try {
			result = (HashMap<String, Object>) iUAPQueryBS.executeQuery(sqlPuls, new MapProcessor());
		} catch (BusinessException e) {
			return returnResult;
		}
		if (null != result && result.size() > 0) {
			if (null != result.get(WorkCalendarDateVO.DATETYPE)) {
				try {
					String index = result.get(WorkCalendarDateVO.DATETYPE).toString();
					returnResult = CalendarDateType.getName(Integer.parseInt(index));
				} catch (Exception e) {
					return returnResult;
				}
			}
		}
		return returnResult;
	}

}