package nc.ui.ta.psndocwadoc.view.labourjoin;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.wa.IPsndocwadocLabourService;
import nc.ui.hr.uif2.model.HRWizardModel;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pub.beans.wizard.WizardStepException;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.querytemplate.value.IFieldValueElement;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;

import org.springframework.util.CollectionUtils;

@SuppressWarnings("restriction")
public class ConfirmPsnStepListenerForAddJoin implements IWizardStepListener {

    @Override
    public void stepActived(WizardStepEvent event) throws WizardStepException {
	Debug.debug("multiselect step will be actived");
	ConfirmPsnStepForAddJoin currStep = (ConfirmPsnStepForAddJoin) event.getStep();
	HRWizardModel model = (HRWizardModel) currStep.getModel();
	if (!(model.getStepWhenAction() instanceof SelPsnStepForAddJoin))
	    return;
	SelPsnStepForAddJoin selPsnStep = (SelPsnStepForAddJoin) model.getSteps().get(0);
	SelPsnPanelForAddJoin selPsnPanel = selPsnStep.getSelPsnPanelForAddJoin();
	// FromWhereSQL fromWhereSQL = selPsnPanel.getQuerySQL();

	IQueryScheme scheme = selPsnPanel.getSelPsnPanelForAddJoin().getQueryEditor().getQueryScheme();
	List<IFieldValueElement> psndocList = null;

	// 面板选择的人员
	List<String> pk_psndocs = new ArrayList<String>();
	// 组织
	String pk_org = "";
	// 部门
	List<String> pk_depts = new ArrayList<>();
	// 劳保级距
	String laoJiJu = "";
	// 劳退级距
	String tuiJiJu = "";
	// 健保级距
	String jianJiJu = "";

	if (null != scheme) {
	    IFilter[] ifilter = (IFilter[]) scheme.get("filters");
	    for (IFilter filter : ifilter) {
		String code = filter.getFilterMeta().getFieldCode();
		if(null == filter.getFieldValue().getFieldValues() || filter.getFieldValue().getFieldValues().size() <=0){
			continue;
		}
		if ("pk_psndoc".equals(code)) {
		    psndocList = filter.getFieldValue().getFieldValues();
		    if (!CollectionUtils.isEmpty(psndocList)) {
			for (IFieldValueElement field : psndocList) {
			    String pk_psndoc = field.getSqlString();
			    pk_psndocs.add(pk_psndoc);
			}
		    }
		} else if ("pk_psnjob.pk_org".equals(code)) {
		    pk_org = filter.getFieldValue().getFieldValues().get(0).getSqlString();
		} else if ("pk_psnjob.pk_dept".equals(code)) {
			for(IFieldValueElement pk_dept : filter.getFieldValue().getFieldValues() ){
				pk_depts.add(pk_dept.getSqlString());
			}
		} else if ("pk_psndoc.hi_psndoc_glbdef8.glbdef4".equals(code)) {
		    laoJiJu = filter.getFieldValue().getFieldValues().get(0).getSqlString();
		} else if ("pk_psndoc.hi_psndoc_glbdef8.glbdef7".equals(code)) {
		    tuiJiJu = filter.getFieldValue().getFieldValues().get(0).getSqlString();
		} else if ("pk_psndoc.hi_psndoc_glbdef9.glbdef16".equals(code)) {
		    jianJiJu = filter.getFieldValue().getFieldValues().get(0).getSqlString();
		}
	    }
	}

	// UFLiteralDate beginDate = selPsnPanel.getBeginDate();
	// UFLiteralDate endDate = selPsnPanel.getEndDate();
	PsnJobVO[] vos = null;
	try {
	    // boolean isHROrg = !selPsnPanel.isNeedBURef();
	    // String
	    // pk_org=isHROrg?model.getModel().getContext().getPk_org():selPsnPanel.getPK_BU();
	    IPsndocwadocLabourService wadocQs = NCLocator.getInstance().lookup(IPsndocwadocLabourService.class);
	    vos = wadocQs.queryPsnJobVOsByConditionAndOverrideOrg(pk_psndocs, pk_org, pk_depts.toArray(new String[0]));
	    if (null == vos) {
	    	vos = new PsnJobVO[0];
	    }
	} catch (BusinessException e) {
	    Debug.error(e.getMessage(), e);
	    throw new BusinessRuntimeException(e.getMessage(), e);
	}
	currStep.getConfirmPsnPanelForAddJoin().setFormVOs(vos);
    }

    @Override
    public void stepDisactived(WizardStepEvent event) throws WizardStepException {

    }

}
