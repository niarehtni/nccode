package nc.ui.overtime.otleavebalance.action;

import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;

import javax.swing.JViewport;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModeDataManager;
import nc.ui.overtime.otleavebalance.view.OTLeaveBalanceOrgPanel;
import nc.ui.pub.beans.ExtTabbedPane;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pubapp.uif2app.query2.QueryConditionDLGDelegator;
import nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction;
import nc.ui.querytemplate.candidate.MetaDataCandidatePanelType2;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.filtereditor.DefaultFilterEditor;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.querytemplate.value.DefaultFieldValue;
import nc.ui.querytemplate.value.DefaultFieldValueElement;
import nc.ui.querytemplate.value.RefValueObject;
import nc.ui.querytemplate.valueeditor.DefaultFieldValueEditor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

public class QueryAction extends DefaultQueryAction {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 1303344321512365087L;

	private OTLeaveBalanceOrgPanel orgpanel;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		DefaultFieldValue orgValue = new DefaultFieldValue();
		RefValueObject vobj = new RefValueObject();
		vobj.setCode(orgpanel.getRefPane().getRefCode());
		vobj.setName(orgpanel.getRefPane().getRefName());
		vobj.setPk(orgpanel.getRefPane().getRefPK());
		vobj.setMultiLang(true);
		vobj.setSubIncluded(false);

		DefaultFieldValueElement ele = new DefaultFieldValueElement(orgpanel.getRefPane().getRefName(), orgpanel
				.getRefPane().getRefPK(), vobj);
		orgValue.add(ele);
		QueryConditionDLGDelegator dlg = getQryDLGDelegator();
		dlg.getQueryConditionDLG().beforeShowModal();

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
			// end

			for (IFilterEditor edt : dlg.getSimpleEditorFilterEditors()) {
				IFilter flt = edt.getFilter();
				if (flt.getFilterMeta().getFieldCode().equals("pk_org")) {
					((DefaultFieldValueEditor) ((DefaultFilterEditor) edt).getFieldValueEditor()).setValue(orgValue);
					flt.setFieldValue(orgValue);
					((DefaultFilterEditor) edt).setEnable(false);
				} else if (flt.getFilterMeta().getFieldCode().contains("clerkcode")
						|| flt.getFilterMeta().getFieldCode().equals("pk_dept")) {
					AbstractRefModel refModel = ((UIRefPane) ((DefaultFieldValueEditor) ((DefaultFilterEditor) edt)
							.getFieldValueEditor()).getFieldValueElemEditor().getFieldValueElemEditorComponent())
							.getRefModel();
					refModel.setPk_org(orgpanel.getRefPane().getRefPK());

					// ssx added on 2020-07-25
					// 啟碁啟用產線權限子集控制領班查詢權限（部門及人員查詢範圍）
					refModel.setWherePart((StringUtils.isEmpty(refModel.getWherePart()) ? ""
							: (refModel.getWherePart() + " and "))
							+ deptWherePart.replace("#DEPT_PK#", "org_dept.pk_dept"));
					// end
				}
			}

			((JViewport) ((UIScrollPane) ((MetaDataCandidatePanelType2) ((ExtTabbedPane) dlg.getQueryConditionDLG()
					.getQryCondEditor().getLeftTabbedPane()).getComponent(0)).getComponent(1)).getComponent(0))
					.getComponent(0).setEnabled(false);
			MouseListener[] mls = ((JViewport) ((UIScrollPane) ((MetaDataCandidatePanelType2) ((ExtTabbedPane) dlg
					.getQueryConditionDLG().getQryCondEditor().getLeftTabbedPane()).getComponent(0)).getComponent(1))
					.getComponent(0)).getComponent(0).getMouseListeners();
			for (MouseListener ml : mls) {
				((JViewport) ((UIScrollPane) ((MetaDataCandidatePanelType2) ((ExtTabbedPane) dlg.getQueryConditionDLG()
						.getQryCondEditor().getLeftTabbedPane()).getComponent(0)).getComponent(1)).getComponent(0))
						.getComponent(0).removeMouseListener(ml);
			}
		} else {
			for (IFilterEditor edt : dlg.getSimpleEditorFilterEditors()) {
				IFilter flt = edt.getFilter();
				if (flt.getFilterMeta().getFieldCode().equals("pk_org")) {
					((DefaultFieldValueEditor) ((DefaultFilterEditor) edt).getFieldValueEditor()).setValue(orgValue);
					flt.setFieldValue(orgValue);
					((DefaultFilterEditor) edt).setEnable(false);
				} else if (flt.getFilterMeta().getFieldCode().contains("clerkcode")
						|| flt.getFilterMeta().getFieldCode().equals("pk_dept")) {
					AbstractRefModel refModel = ((UIRefPane) ((DefaultFieldValueEditor) ((DefaultFilterEditor) edt)
							.getFieldValueEditor()).getFieldValueElemEditor().getFieldValueElemEditorComponent())
							.getRefModel();
					refModel.setPk_org(orgpanel.getRefPane().getRefPK());
				}
			}

		}
		super.doAction(e);
	}

	public OTLeaveBalanceOrgPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(OTLeaveBalanceOrgPanel orgpanel) {
		this.orgpanel = orgpanel;
	}

	@Override
	protected boolean isActionEnable() {
		return getOrgpanel().getRefPane().getValueObj() != null
				&& (getOrgpanel().getCboYear().getSelectdItemValue() != null || getOrgpanel().getRefBeginDate()
						.getValueObj() != null && getOrgpanel().getRefEndDate().getValueObj() != null)
				&& ((OTLeaveBalanceModeDataManager) getOrgpanel().getDataManager()).getHierachicalModel()
						.getSelectedData() != null;
	}
}
