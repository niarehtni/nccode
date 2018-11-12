package nc.ui.ta.teamcalendar.view.batchchangedaytype;



import java.awt.BorderLayout;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.ta.pub.BUPanel;
import nc.ui.ta.pub.ICompWithValidateFunc;
import nc.ui.ta.teamcalendar.model.TeamCalendarAppModel;
import nc.ui.ta.teamcalendar.view.ConditionDateScopePanel;
import nc.ui.ta.teamcalendar.view.ConditionTeamCalendarPanel;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

/**
 * 班组循环排班条件查询界面
 * @author yucheng
 *
 */
@SuppressWarnings("serial")
public class SelectTeamPanelForBatchChangeDayType extends UIPanel implements ICompWithValidateFunc, ValueChangedListener {

	private TeamCalendarAppModel model;
	//选择班组范围及日期范围的panel
	private ConditionTeamCalendarPanel teamCalendarPanel = null;

	private BUPanel buPanel=null;

	public void init(){
		setLayout(new BorderLayout());
		add(getBuPanel(),BorderLayout.NORTH);
		add(getTeamCalendarPanel(), BorderLayout.CENTER);
		getBuPanel().getBuRef().addValueChangedListener(this);
		//add(getOverrideExistCalendarCheck(), BorderLayout.SOUTH);
	}

	public FromWhereSQL getQuerySQL(){
		return getTeamCalendarPanel().getQuerySQL();
	}

	/*public UFLiteralDate getBeginDate(){
		return getDatePanel().getBeginDate();
	}

	public UFLiteralDate getEndDate(){
		return getDatePanel().getEndDate();
	}*/

	/*public boolean isOverride(){
		return getOverrideExistCalendarCheck().isSelected();
	}*/

	public String getPK_BU(){
		return getBuPanel().getPK_BU();
	}

	@Override
	public void validateData() throws ValidationException {
		if(StringUtils.isBlank(getPK_BU()))
			throw new ValidationException(ResHelper.getString("6017teamcalendar","06017teamcalendar0010")
/*@res "业务单元不能为空！"*/);
		getTeamCalendarPanel().validateData();
	}

	public ConditionTeamCalendarPanel getTeamCalendarPanel() {
		if(teamCalendarPanel == null){
			teamCalendarPanel = new ConditionTeamCalendarPanel();
			teamCalendarPanel.setModel(getModel());
			teamCalendarPanel.init();
		}
		return teamCalendarPanel;
	}

	public BUPanel getBuPanel() {
		if(buPanel==null){
			buPanel = new BUPanel();
			buPanel.setModel(getModel());
			buPanel.init();
		}
		return buPanel;
	}

	public TeamCalendarAppModel getModel() {
		return model;
	}

	public void setModel(TeamCalendarAppModel model) {
		this.model = model;
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		model.buChanged();
	}

}