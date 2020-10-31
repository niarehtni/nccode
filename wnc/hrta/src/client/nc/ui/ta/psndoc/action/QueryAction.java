package nc.ui.ta.psndoc.action;

import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;

import javax.swing.JViewport;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.hr.uif2.HrQueryDelegator2;
import nc.ui.pub.beans.ExtTabbedPane;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.querytemplate.candidate.MetaDataCandidatePanelType2;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.filtereditor.DefaultFilterEditor;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.querytemplate.valueeditor.DefaultFieldValueEditor;
import nc.ui.ta.psndoc.model.TbmPsndocAppModelDataManager;
import nc.ui.ta.psndoc.ref.TBMPsndocRefModel;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

public class QueryAction extends nc.ui.hr.uif2.action.QueryAction {

	private static final long serialVersionUID = 1L;

	public void doAction(ActionEvent e) throws Exception {
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

		HrQueryDelegator2 delegator = (HrQueryDelegator2) getQueryDelegator();

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

		super.doAction(e);
	}

	@Override
	protected boolean isActionEnable() {
		if (getModel().getContext().getPk_org() == null) {
			return false;
		}
		return super.isActionEnable();
	}

	@Override
	protected void executeQuery(IQueryScheme queryScheme) {
		((TbmPsndocAppModelDataManager) getDataManager()).initModelBySqlWhere(queryScheme.getTableJoinFromWhereSQL());
		queryExcuted = true;
	}
}
