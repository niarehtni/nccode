package nc.ui.wa.taxaddtional.model;

import java.util.HashSet;

import org.apache.commons.lang.StringUtils;

import nc.ui.hr.uif2.HrQueryDelegator;
import nc.ui.hr.uif2.HrQueryDelegator.IQueryProcessorEx;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.querytemplate.filtereditor.FilterEditorWrapper;
import nc.ui.querytemplate.queryarea.IQueryExecutor;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.wa.taxaddtional.action.TaxExcelImportEHD;
import nc.ui.wa.taxaddtional.view.DataImportDia;

public class TaxaddtionalQueryDelegator extends HrQueryDelegator {

	@Override
	public QueryConditionDLG getQueryDlg() {

		if (queryDlg == null) {
			queryDlg = super.getQueryDlg();
			// 设置常用查询模板
			NormalQueryPanel nqp = new NormalQueryPanel();
			nqp.setModel((TaxaddtionalModel) getModel());
			queryDlg.setNormalPanel(nqp);
		}
		return queryDlg;
	}

	@SuppressWarnings("restriction")
	@Override
	protected void initCondition(CriteriaChangedEvent event) {
		super.initCondition(event);
		String code = event.getFieldCode();
		FilterEditorWrapper wapper = new FilterEditorWrapper(event.getFiltereditor());
		String pkOrg = getContext().getPk_org();

		if (!(wapper.getFieldValueElemEditorComponent() instanceof UIRefPane)) {
			// 不是参照就不设条件
			return;
		}
	}

	public void doQuery(IQueryExecutor executor) throws Exception {
		if (getQueryDlg().showModal() != UIDialog.ID_OK) {
			return;
		}
		String strWhere = getWhereCondition();
		String taxyear = ((NormalQueryPanel) this.queryDlg.getNormalPanel()).getSelectTaxYearValue();
		((TaxaddtionalModel) this.getModel()).setTaxyear(taxyear);
		((TaxaddtionalModel) this.getModel()).setExportQuerySql(strWhere);
		if (executor != null) {
			if (executor instanceof IQueryProcessorEx) {
				HashSet tablenames = getQueryDlg().getUsedTableName();
				((IQueryProcessorEx) executor).processQueryCondition(strWhere, tablenames);
			} else {
				processQuery(executor);
				// executor.processQueryCondition(strWhere);
			}
		}
		
		((TaxaddtionalModel) getModel()).setFromHeadOut(true);
		TaxExcelImportEHD importEHD = new TaxExcelImportEHD(new DataImportDia(getModel().getContext(), getModel()));
		importEHD.doExport();

		((TaxaddtionalModel) getModel()).setFromHeadOut(false);
		getModel().setUiState(UIState.NOT_EDIT);
		//
		ShowStatusBarMsgUtil.showStatusBarMsg("操作完成", getContext());
//		putValue(HrAction.MESSAGE_AFTER_ACTION, importEHD.getImporter().getDetailMessage());
	}

}
