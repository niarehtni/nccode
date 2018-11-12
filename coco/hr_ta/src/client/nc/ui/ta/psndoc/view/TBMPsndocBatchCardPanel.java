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
 * 考勤档案批量新增和变动人员时的考勤档案卡片页面
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
	private UIComboBox tbmPropComboBox;	//考勤方式
	private UICheckBox postDateChcBx;	//到职日期
	//by he
	private UIComboBox tbmWeekFormComboBox;	//工时形态
	private UIComboBox tbmOtControComboBox;	//加班控管
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
/*@res "考勤方式"*/));
        builder.append(getTbmPropComboBox());
        builder.append(getPostDateChcBx());
        builder.nextLine();
        builder.append(new UILabel(ResHelper.getString("common","UC000-0001892")
/*@res "开始日期"*/));
        builder.append(getRefBeginDate());
        getRefBeginDate().getUITextField().setShowMustInputHint(!getPostDateChcBx().isSelected());
        builder.nextLine();
        builder.append(new UILabel(ResHelper.getString("6017psndoc","2psndoc-00002015")
        		/*@res "工时形态"*/));
        		        builder.append(getTbmWeekFormComboBox());
        builder.append(new UILabel(ResHelper.getString("6017psndoc","2psndoc-00002012")
        		/*@res "加班控管"*/));
        		        builder.append(getTbmOtControComboBox());
        //如果启用考勤地点异常，则加入考勤地点
        if(isCheckinPermit()) {
        	builder.append(new UILabel(ResHelper.getString("6017psndoc","06017psndoc0071")
/*@res "考勤地点"*/));
        	builder.append(getRefPlace());
        }

	}
	/**
	 * 加班控管
	 * @return
	 */
	public UIComboBox getTbmOtControComboBox() {
		if(tbmOtControComboBox == null) {
			tbmOtControComboBox = new UIComboBox();
			tbmOtControComboBox.addItem(new DefaultConstEnum(OvertimecontrolEunm.MANUAL_CHECK.value(),ResHelper.getString("6017psndoc","2psndoc-00002013")
/*@res "一个月"*/));
			tbmOtControComboBox.addItem(new DefaultConstEnum(OvertimecontrolEunm.MACHINE_CHECK.value(),ResHelper.getString("6017psndoc","2psndoc-00002014")
/*@res "三个月"*/));
		}
		return tbmOtControComboBox;
	}
	/**
	 * 工时形态
	 * @return
	 */
	public UIComboBox getTbmWeekFormComboBox() {
		if(tbmWeekFormComboBox == null) {
			tbmWeekFormComboBox = new UIComboBox();
			tbmWeekFormComboBox.addItem(new DefaultConstEnum(WorkWeekFormEnum.ONEWEEK.value(),ResHelper.getString("6017psndoc","2psndoc-00002016")
/*@res "法定工时"*/));
			tbmWeekFormComboBox.addItem(new DefaultConstEnum(WorkWeekFormEnum.TWOWEEKS.value(),ResHelper.getString("6017psndoc","2psndoc-00002017")
/*@res "二周变形"*/));
			tbmWeekFormComboBox.addItem(new DefaultConstEnum(WorkWeekFormEnum.FOURWEEKS.value(),ResHelper.getString("6017psndoc","2psndoc-00002018")
					/*@res "四周变形"*/));
			tbmWeekFormComboBox.addItem(new DefaultConstEnum(WorkWeekFormEnum.EIGHTWEEKS.value(),ResHelper.getString("6017psndoc","2psndoc-00002019")
					/*@res "八周变形"*/));
		}
		return tbmWeekFormComboBox;
	}

	/**
	 * 考勤地点是否异常
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
	 * 开始日期
	 */
	protected UIRefPane getRefBeginDate() {
		if (refBeginDate == null) {
			refBeginDate = new UIRefPane();
			refBeginDate.setName("refBeginDate");
			refBeginDate.setRefNodeName(IRefConst.REFNODENAME_LITERALCALENDAR);
			refBeginDate.getUITextField().setFormatShow(true);
			//开始日期默认为指定日期，所以默认当前日期
			refBeginDate.setValueObj(getBusDate());
		}
		return refBeginDate;
	}
	/**
	 * 考勤地点
	 */
	protected UIRefPane getRefPlace() {
		if (refPlace == null) {
			refPlace = new UIRefPane();
			refPlace.setName("refPlace");
			refPlace.setRefNodeName("考勤地点(自定义档案)");/*-=notranslate=-*/
			refPlace.setPk_org(getModel().getContext().getPk_org());
		}
		return refPlace;
	}
	/**
	 * 取得考勤地点的值
	 * @return
	 */
	public String getPkPlace() {
		return getRefPlace().getRefPK();
	}
	/**
	 * 取得开始日期的值
	 * @return
	 */
	public UFLiteralDate getBeginDate() {
		return (UFLiteralDate)getRefBeginDate().getValueObj();
	}

	public UIComboBox getTbmPropComboBox() {
		if(tbmPropComboBox == null) {
			tbmPropComboBox = new UIComboBox();
			tbmPropComboBox.addItem(new DefaultConstEnum(TbmPropEnum.MACHINE_CHECK.value(),ResHelper.getString("6017psndoc","06017psndoc0073")
/*@res "机器考勤"*/));
			tbmPropComboBox.addItem(new DefaultConstEnum(TbmPropEnum.MANUAL_CHECK.value(),ResHelper.getString("6017psndoc","06017psndoc0072")
/*@res "手工考勤"*/));
		}
		return tbmPropComboBox;
	}

	public UICheckBox getPostDateChcBx() {
		if(postDateChcBx == null) {
			postDateChcBx = new UICheckBox(ResHelper.getString("common","UC000-0000632")
/*@res "到职日期"*/);
			postDateChcBx.addActionListener(this);
		}
		return postDateChcBx;
	}

	private UFLiteralDate getBusDate() {
		// 取当前日期
		UFDate busDate = WorkbenchEnvironment.getInstance().getBusiDate();
		return UFLiteralDate.getDate(busDate.toString().substring(0, 10));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(getPostDateChcBx())) {

			//如果按到职日期，则开始日期
			getRefBeginDate().setEnabled(!getPostDateChcBx().isSelected());
			//如果不是按照到职日期，则开始日期为必输
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