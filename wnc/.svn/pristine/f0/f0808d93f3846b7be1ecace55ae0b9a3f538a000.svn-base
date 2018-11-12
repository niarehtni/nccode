package nc.ui.bd.workcalendrule.actions;

import java.awt.event.ActionEvent;
import nc.ui.bd.workcalendar.model.WorkCalendarTreeModel;
import nc.ui.bd.workcalendar.pub.WorkCalendarTool;
import nc.ui.bd.workcalendrule.view.WorkCalendarRuleEditor;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.SaveAction;
import nc.ui.uif2.editor.IEditor;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.bd.workcalendar.WorkCalendarDateVO;
import nc.vo.bd.workcalendar.WorkCalendarVO;
import nc.vo.bd.workcalendrule.WorkCalendarRuleVO;
import nc.vo.pub.lang.UFLiteralDate;

public class WorkCalendarInRuleNodeSaveAction extends SaveAction {
	private static final long serialVersionUID = 1944158142682744533L;
	private WorkCalendarRuleEditor ruleEditor = null;

	@SuppressWarnings("restriction")
	public void doAction(ActionEvent e) throws Exception {
		WorkCalendarRuleVO rule = (WorkCalendarRuleVO) getRuleEditor()
				.getValue();
		String pk_WorkCalendRule = rule.getPk_workcalendrule();
		Object value = getEditor().getValue();
		((WorkCalendarVO) value).setPk_workcalendrule(pk_WorkCalendRule);
		validate(value);
		WorkCalendarVO calendarVO = (WorkCalendarVO) value;
		UFLiteralDate beginDate = calendarVO.getBegindate();
		UFLiteralDate endDate = calendarVO.getEnddate();
		WorkCalendarTool restDaysTool = new WorkCalendarTool(pk_WorkCalendRule,
				beginDate, endDate, calendarVO.getPk_holidayrule());

		WorkCalendarDateVO[] calendarDateVOs = restDaysTool.getWorkCalendarDateVOs();
		calendarVO.setCalendardates(calendarDateVOs);
		((WorkCalendarTreeModel) getModel())
				.addFromWorkCalendarRuleNode(calendarVO);
		showSuccessInfo();
		getModel().setUiState(UIState.NOT_EDIT);
	}

	public WorkCalendarRuleEditor getRuleEditor() {
		return ruleEditor;
	}

	public void setRuleEditor(WorkCalendarRuleEditor ruleEditor) {
		this.ruleEditor = ruleEditor;
	}
}