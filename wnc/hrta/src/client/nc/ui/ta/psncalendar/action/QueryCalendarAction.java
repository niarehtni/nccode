package nc.ui.ta.psncalendar.action;

import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;

import javax.swing.JViewport;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.hr.uif2.action.QueryAction;
import nc.ui.pub.beans.ExtTabbedPane;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.querytemplate.candidate.MetaDataCandidatePanelType2;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.filtereditor.DefaultFilterEditor;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.querytemplate.queryarea.IQueryExecutor;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.querytemplate.valueeditor.DefaultFieldValueEditor;
import nc.ui.ta.psncalendar.model.PsnCalendarAppModelDataManager;
import nc.ui.ta.psndoc.ref.TBMPsndocRefModel;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.QueryScopeEnum;

public class QueryCalendarAction extends QueryAction {

	private static final long serialVersionUID = 2380662049691680978L;

	PsnCalendarAppModelDataManager psnCalendarAppModelDataManager;

	public QueryCalendarAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doAction(ActionEvent evt) throws Exception {
		validate(null);
		PsnCalendarQueryDelegator delegator = (PsnCalendarQueryDelegator) getQueryDelegator();

		// ssx added on 2020-07-25
		// 啟碁啟用產線權限子集控制領班查詢權限（部門及人員查詢範圍）
		int hasGlbdef8 = -1;
		IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		try {
			hasGlbdef8 = (int) query.executeQuery(
					"select count(glbdef1) from HI_PSNDOC_GLBDEF8 where pk_psndoc = (select pk_psndoc from sm_user where cuserid = '"
							+ getModel().getContext().getPk_loginUser() + "')", new ColumnProcessor());
		} catch (BusinessException ex) {
			ex.printStackTrace();
		}

		if (hasGlbdef8 > 0) {
			String deptWherePart = "#DEPT_PK# in (select glbdef1 from HI_PSNDOC_GLBDEF8 where pk_psndoc = (select pk_psndoc from sm_user where cuserid = '"
					+ getModel().getContext().getPk_loginUser()
					+ "') and '"
					+ new UFLiteralDate().toString()
					+ "' between BEGINDATE and nvl(ENDDATE, '9999-12-31')) and (select count(pk_dept) from org_dept where pk_dept=#DEPT_PK# and isnull(HRCANCELED, 'N')='N') > 0";
			delegator.getQueryDlg().beforeShowModal();
			for (IFilterEditor edt : delegator.getQueryDlg().getSimpleEditorFilterEditors()) {
				IFilter flt = edt.getFilter();
				if (flt.getFilterMeta().getFieldCode().equals("pk_psnjob.pk_org")) {
					((DefaultFilterEditor) edt).setEnable(false);
				} else if (flt.getFilterMeta().getFieldCode().contains("clerkcode")) {
					TBMPsndocRefModel refModel = (TBMPsndocRefModel) ((UIRefPane) ((DefaultFieldValueEditor) ((DefaultFilterEditor) edt)
							.getFieldValueEditor()).getFieldValueElemEditor().getFieldValueElemEditorComponent())
							.getRefModel();

					if (refModel != null) {
						refModel.setClassWherePart(deptWherePart.replace("#DEPT_PK#", "orgdept.pk_orgdept"));
					}
				} else if (flt.getFilterMeta().getFieldCode().equals("pk_psnjob.pk_dept")) {
					AbstractRefModel refModel = ((UIRefPane) ((DefaultFieldValueEditor) ((DefaultFilterEditor) edt)
							.getFieldValueEditor()).getFieldValueElemEditor().getFieldValueElemEditorComponent())
							.getRefModel();

					if (refModel != null) {
						refModel.setWherePart(deptWherePart.replace("#DEPT_PK#", "org_dept.pk_dept"));
					}
				}
			}

			((JViewport) ((UIScrollPane) ((MetaDataCandidatePanelType2) ((ExtTabbedPane) delegator.getQueryDlg()
					.getQryCondEditor().getLeftTabbedPane()).getComponent(0)).getComponent(1)).getComponent(0))
					.getComponent(0).setEnabled(false);

			MouseListener[] mls = ((JViewport) ((UIScrollPane) ((MetaDataCandidatePanelType2) ((ExtTabbedPane) delegator
					.getQueryDlg().getQryCondEditor().getLeftTabbedPane()).getComponent(0)).getComponent(1))
					.getComponent(0)).getComponent(0).getMouseListeners();
			for (MouseListener ml : mls) {
				((JViewport) ((UIScrollPane) ((MetaDataCandidatePanelType2) ((ExtTabbedPane) delegator.getQueryDlg()
						.getQryCondEditor().getLeftTabbedPane()).getComponent(0)).getComponent(1)).getComponent(0))
						.getComponent(0).removeMouseListener(ml);
			}
		}

		try {
			delegator.doQuery(new IQueryExecutor() {
				public void doQuery(final IQueryScheme queryScheme) {
					new Thread() {
						@Override
						public void run() {
							IProgressMonitor progressMonitor = NCProgresses
									.createDialogProgressMonitor(getEntranceUI());
							try {
								progressMonitor.beginTask(ResHelper.getString("6001uif2", "06001uif20042")
								/* @res "查询数据..." */, IProgressMonitor.UNKNOWN_REMAIN_TIME);
								progressMonitor.setProcessInfo(ResHelper.getString("6001uif2", "06001uif20043")
								/* @res "数据查询中，请稍候....." */);
								executeQuery(queryScheme);
								progressMonitor.done(); // 进度任务结束
							} finally {
								setStatusBarMsg();
								ShowStatusBarMsgUtil.showStatusBarMsg((String) getValue(MESSAGE_AFTER_ACTION),
										getContext());
								queryExcuted = false;
							}
						}
					}.start();
				}
			});
		} finally {
			setStatusBarMsg();
			queryExcuted = false;
		}
	}

	public PsnCalendarAppModelDataManager getPsnCalendarAppModelDataManager() {
		return psnCalendarAppModelDataManager;
	}

	public void setPsnCalendarAppModelDataManager(PsnCalendarAppModelDataManager psnCalendarAppModelDataManager) {
		this.psnCalendarAppModelDataManager = psnCalendarAppModelDataManager;
	}

	protected void executeQuery(final IQueryScheme queryScheme) {
		PsnCalendarQueryDelegator delegator = (PsnCalendarQueryDelegator) getQueryDelegator();
		UFLiteralDate begindateDate = delegator.getBeginDate();
		UFLiteralDate endDateDate = delegator.getEndDate();
		QueryScopeEnum queryScopeEnum = delegator.getQueryScopeEnum();
		psnCalendarAppModelDataManager.initModelByFromWhereSQL(queryScheme.getTableJoinFromWhereSQL(), begindateDate,
				endDateDate, queryScopeEnum);
		queryExcuted = true;

	}

	@Override
	protected boolean isActionEnable() {
		return super.isActionEnable();
		// UIState state = getModel().getUiState();
		// return
		// (state==UIState.INIT||state==UIState.NOT_EDIT)&&!StringUtils.isEmpty(getModel().getContext().getPk_org());
	}
}
