package nc.ui.hrjf.deptadj.action;

import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.om.IDeptAdjustService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pubapp.uif2app.actions.DifferentVOSaveAction;
import nc.ui.uif2.UIState;
import nc.vo.om.hrdept.AggHRDeptAdjustVO;
import nc.vo.om.hrdept.HRDeptAdjustVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.vo.pubapp.pattern.model.transfer.bill.ClientBillCombinServer;
import nc.vo.pubapp.pattern.model.transfer.bill.ClientBillToServer;

public class DeptadjSaveAction extends DifferentVOSaveAction {

	@Override
	protected void doAddSave(Object value) throws Exception {
		IBill[] clientVOs = { (IBill) value };

		ClientBillToServer tool = new ClientBillToServer();
		String pk_org = getModel().getContext().getPk_org();
		IBill[] lightVOs = tool.constructInsert(clientVOs);
		AggHRDeptAdjustVO aggvo = (AggHRDeptAdjustVO) lightVOs[0];
		aggvo.getParentVO().setBilldate(new UFDate());
		if (null == aggvo.getParentVO().getPk_dept()) {
			MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(), "報錯信息", "部门信息不能為空!");
			return;
		}
		String message = null;
		try {
			UFBoolean flag = validatevo(aggvo.getParentVO());

			if (!flag.booleanValue()) {
				return;
			}
		} catch (BusinessException e) {
			message = e.getMessage();
		}
		if (null != message) {
			MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(), "報錯信息", message);
			return;
		}
		if (null == aggvo.getParentVO().getPk_org()) {
			aggvo.getParentVO().setPk_org(pk_org);
		}
		if (null == aggvo.getParentVO().getPk_group()) {
			aggvo.getParentVO().setPk_group(getModel().getContext().getPk_group());
		}
		// 必填判空
		if (null == aggvo.getParentVO().getCode()) {
			throw new BusinessException("編碼不能为空。");
		}
		if (null == aggvo.getParentVO().getName()) {
			throw new BusinessException("名稱不能为空。");
		}
		if (null == aggvo.getParentVO().getDepttype()) {
			throw new BusinessException("部門类型不能为空。");
		}
		if (null == aggvo.getParentVO().getCreatedate()) {
			throw new BusinessException("成立時間不能为空。");
		}
		IBill[] afterUpdateVOs = null;

		if (getService() == null) {
			throw new BusinessException("service不能为空。");
		}
		afterUpdateVOs = getService().insert(lightVOs);

		new ClientBillCombinServer().combine(clientVOs, afterUpdateVOs);

		getModel().directlyAdd(clientVOs[0]);
		getModel().setUiState(UIState.NOT_EDIT);
	}

	private UFBoolean validatevo(HRDeptAdjustVO deptadjvo) throws BusinessException {
		IDeptAdjustService managequery = NCLocator.getInstance().lookup(IDeptAdjustService.class);
		UFBoolean deptflag = managequery.validateDept(deptadjvo);
		UFBoolean psnflag = managequery.validatePsn(deptadjvo);
		if (deptflag.booleanValue() && psnflag.booleanValue()) {
			return UFBoolean.TRUE;
		} else {
			return UFBoolean.FALSE;
		}
	}

	@Override
	protected void doEditSave(Object value) throws Exception {
		IBill[] clientVOs = { (IBill) value };
		String pk_org = getModel().getContext().getPk_org();
		ClientBillToServer tool = new ClientBillToServer();

		IBill[] oldVO = { (IBill) getModel().getSelectedData() };

		IBill[] lightVOs = tool.construct(oldVO, clientVOs);
		AggHRDeptAdjustVO aggvo = (AggHRDeptAdjustVO) lightVOs[0];
		// 给org赋值
		String pk_org_v = getorgs(aggvo.getParentVO().getPk_org() == null ? pk_org
				: aggvo.getParentVO().getPk_org());
		aggvo.getParentVO().setPk_org_v(pk_org_v);
		String message = null;
		try {
			UFBoolean flag = validatevo(aggvo.getParentVO());
			if (!flag.booleanValue()) {
				return;
			}
		} catch (BusinessException e) {
			message = e.getMessage();
		}
		if (null != message) {
			MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(), "報錯信息", message);
			return;
		}
		if (null == aggvo.getParentVO().getBilldate()) {
			aggvo.getParentVO().setBilldate(new UFDate());
		}
		if (null == aggvo.getParentVO().getPk_dept()) {
			MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(), "報錯信息", "部门信息不能為空!");
			return;
		}
		if (null == aggvo.getParentVO().getPk_org()) {
			aggvo.getParentVO().setPk_org(pk_org);
		}
		if (null == aggvo.getParentVO().getPk_group()) {
			aggvo.getParentVO().setPk_group(getModel().getContext().getPk_group());
		}
		// 必填判空 
		if(null == aggvo.getParentVO().getCode()){
			throw new BusinessException("部门编码不能为空。");
		}
		if(null == aggvo.getParentVO().getName()){
			throw new BusinessException("部门名称不能为空。");
		}
		if(null == aggvo.getParentVO().getDepttype()){
			throw new BusinessException("部門类型不能为空。");
		}
		if(null == aggvo.getParentVO().getCreatedate()){
			throw new BusinessException("成立時間不能为空。");
		}
		IBill[] afterUpdateVOs = null;

		if (getService() == null) {
			throw new BusinessException("service不能为空。");
		}
		afterUpdateVOs = getService().update(lightVOs);

		new ClientBillCombinServer().combine(clientVOs, afterUpdateVOs);

		getModel().directlyUpdate(clientVOs[0]);
		getModel().setUiState(UIState.NOT_EDIT);
	}
	private String getorgs(String pk_org) {
		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		List<Map<String, String>> orgvs = null;
		try {
			orgvs = (List<Map<String, String>>) iUAPQueryBS.executeQuery("select pk_vid from "
					+ "org_orgs where pk_org='" + pk_org + "' and dr=0", new MapListProcessor());
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		String pk_org_v = null;
		if (null != orgvs) {
			for (Map<String, String> orgv : orgvs) {
				pk_org_v = orgv.get("pk_vid");
			}
		}
		return pk_org_v;
	}

}
