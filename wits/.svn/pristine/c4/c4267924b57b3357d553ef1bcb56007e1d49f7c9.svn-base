package nc.ui.wa.taxspecial_statistics.view;

import nc.ui.hi.ref.PsndocRefModel;
import nc.ui.hi.ref.PsnjobRefModel;
import nc.ui.org.ref.BusinessUnitDefaultRefModel;
import nc.ui.org.ref.HROrgDefaultRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.CriteriaChangedEvent;
import nc.ui.querytemplate.filtereditor.FilterEditorWrapper;
import nc.ui.wa.pub.WaQueryDelegator;
import nc.vo.wa.pub.WaLoginContext;

public class TaxSpecialStatisticsQueryDelegator extends WaQueryDelegator {
	@Override
	protected void initCondition(CriteriaChangedEvent event) {
		FilterEditorWrapper wrapper = new FilterEditorWrapper(event.getFiltereditor());

		if (!(wrapper.getFieldValueElemEditorComponent() instanceof UIRefPane)) {
			return;
		}
		UIRefPane ref = (UIRefPane) wrapper.getFieldValueElemEditorComponent();
		StringBuffer condition = new StringBuffer();
		WaLoginContext context = (WaLoginContext) getContext();
		if (ref.getRefModel() != null) {
			if (ref.getRefModel() instanceof HROrgDefaultRefModel
					|| ref.getRefModel() instanceof BusinessUnitDefaultRefModel) {

				ref.getRefModel().setPk_org(getContext().getPk_group());
			} else {
				ref.getRefModel().setPk_org(getContext().getPk_org());
			}
			if (ref.getRefModel() instanceof PsndocRefModel) {
				condition.append(" and bd_psndoc.pk_psndoc in ");
				condition.append("(select pk_psndoc from wa_data where PK_WA_CLASS = '"
						+ context.getWaLoginVO().getPk_wa_class() + "'");
				condition.append(" and CYEAR = '" + context.getWaLoginVO().getCyear() + "'");
				condition.append(" and CPERIOD = '" + context.getWaLoginVO().getCperiod() + "')");
				ref.getRefModel().addWherePart(condition.toString());
			} else if (ref.getRefModel() instanceof PsnjobRefModel) {
				condition.append(" and pk_psnjob in ");
				condition.append("(select pk_psnjob from wa_data where PK_WA_CLASS = '"
						+ context.getWaLoginVO().getPk_wa_class() + "'");
				condition.append(" and CYEAR = '" + context.getWaLoginVO().getCyear() + "'");
				condition.append(" and CPERIOD = '" + context.getWaLoginVO().getCperiod() + "')");
				ref.getRefModel().addWherePart(condition.toString());
			}
		}
	}
}
