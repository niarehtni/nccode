package nc.ui.ta.teamcalendar.view;

import java.awt.BorderLayout;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.border.UITitledBorder;
import nc.ui.querytemplate.QueryConditionEditor;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.ta.pub.ICompWithValidateFunc;
import nc.ui.ta.pub.selpsn.DateScopePanel;
import nc.ui.ta.pub.selpsn.DateScopePanelWithBorder;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.querytemplate.TemplateInfo;

/**
 * 批改条件panel
 * @author yucheng
 *
 */
@SuppressWarnings("serial")
public class ConditionTeamCalendarPanel extends UIPanel implements AppEventListener, ICompWithValidateFunc {

	private AbstractUIAppModel model;

	private QueryConditionEditor queryEditor = null;

	//private DateScopePanel dateScopePanel = null;

	private String curPk_org;

	public ConditionTeamCalendarPanel() {
	}

	/**
	 * 构造方法不调用此方法，由此类的使用者主动调用init方法
	 * 此方法的作用是：
	 * 将查询框加载到界面上，并设置查询框里的一些默认条件，例如组织
	 */
	public void init(){
		setLayout(new BorderLayout());
		add(getQueryEditor(), BorderLayout.CENTER);
		//add(getDateScopePanel(), BorderLayout.SOUTH);
	}

/*	public UFLiteralDate getBeginDate() {
		return getDateScopePanel().getBeginDate();
	}

	public UFLiteralDate getEndDate(){
		return getDateScopePanel().getEndDate();
	}*/

	public FromWhereSQL getQuerySQL() {
		return queryEditor.getQueryScheme().getTableJoinFromWhereSQL();
	}

	/**
	 * 获取查询条件编辑器
	 *
	 */
	public QueryConditionEditor getQueryEditor() {
		if (queryEditor == null) {
			TemplateInfo tempInfo = new TemplateInfo();
			tempInfo.setFunNode(getModel().getContext().getNodeCode());
			tempInfo.setNodekey("teamcalendar");
			tempInfo.setCurrentCorpPk(getModel().getContext().getPk_org());
			tempInfo.setPk_Org(getModel().getContext().getPk_org());
			tempInfo.setUserid(getModel().getContext().getPk_loginUser());
			queryEditor = new QueryConditionEditor(tempInfo);
			queryEditor.showPanel();
			queryEditor.setBorder(new UITitledBorder(ResHelper.getString("6017teamcalendar","06017teamcalendar0031")
/*@res "班组范围"*/));
		}
		return queryEditor;
	}


	/**
	 * 重新设置条件编辑器
	 *
	 */
	public void resetQueryEditor() {
		remove(queryEditor);
		queryEditor = null;
		add(getQueryEditor(), BorderLayout.CENTER);
	}

	@Override
	public void validateData() throws ValidationException{
		//getDateScopePanel().validateData();
	}

	/*public DateScopePanel getDateScopePanel() {
		if(dateScopePanel==null){
			dateScopePanel = new DateScopePanelWithBorder();
			dateScopePanel.setModel(getModel());
			dateScopePanel.init();
		}
		return dateScopePanel;
	}*/

	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	@Override
	public void handleEvent(AppEvent event) {
		if(AppEventConst.MODEL_INITIALIZED.equals(event.getType())){
			String pk_org = getModel().getContext().getPk_org();
			if(pk_org!=null&&curPk_org!=null&&pk_org.equals(curPk_org))
				return;
			curPk_org = pk_org;
			resetQueryEditor();
		}
	}

}