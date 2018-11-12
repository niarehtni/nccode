package nc.ui.hrjf.deptadj.action;

import java.awt.event.ActionEvent;
import java.util.Date;

import nc.bs.framework.common.NCLocator;
import nc.itf.om.IDeptAdjustService;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pubapp.uif2app.actions.DeleteAction;
import nc.ui.uif2.IShowMsgConstant;
import nc.vo.om.hrdept.AggHRDeptAdjustVO;
import nc.vo.om.hrdept.HRDeptAdjustVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;

public class DeptadjDeleteAction extends DeleteAction {
	

	/* （非 Javadoc）
	 * @see nc.ui.pubapp.uif2app.actions.DeleteAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void doAction(ActionEvent e) throws Exception {
		checkDataPermission();
		super.doAction(e);
	}

	@Override
	protected void checkDataPermission() throws Exception {
		Object[] objs = getUnDataPermissionData();
		if ((objs != null) && (objs.length > 0)) {
			throw new BusinessException(IShowMsgConstant.getDataPermissionInfo());
		}
		if (null != getModel().getSelectedData()) {
			objs = new Object[] { getModel().getSelectedData() };
		}else{
			throw new BusinessException("未选择任何单据"); 
		}
		AggHRDeptAdjustVO aggvo = (AggHRDeptAdjustVO) objs[0];
		// 调用接口校验
		 validatevo(aggvo.getParentVO());
	
	}

	private UFBoolean validatevo(HRDeptAdjustVO deptadjvo) throws BusinessException {
		IDeptAdjustService managequery = NCLocator.getInstance().lookup(IDeptAdjustService.class);
		UFBoolean deptflag = managequery.validateDel(deptadjvo);
		if (!deptflag.booleanValue()) {
			return UFBoolean.FALSE;
		}
		return UFBoolean.TRUE;
	}

}
