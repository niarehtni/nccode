package nc.ui.hrjf.deptadj.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.om.IDeptAdjustService;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.vo.om.hrdept.AggHRDeptAdjustVO;
import nc.vo.om.hrdept.HRDeptAdjustVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

public class DeptadjExecuteAction extends HrAction {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 5491707190681628739L;

	public DeptadjExecuteAction() {
		setCode("DeptadjExecuteAction");
		setBtnName("執行版本化");
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object[] objs = null;
		if (null != getModel().getSelectedData()) {
			objs = new Object[] { getModel().getSelectedData() };
		} else {
			throw new BusinessException("未選擇任何單據。");
		}
		AggHRDeptAdjustVO aggvo = (AggHRDeptAdjustVO) objs[0];

		IDeptAdjustService svc = NCLocator.getInstance().lookup(IDeptAdjustService.class);
		String msg = svc.executeDeptVersion(new HRDeptAdjustVO[] { (HRDeptAdjustVO) aggvo.getParent() },
				(UFLiteralDate) aggvo.getParent().getAttributeValue("effectivedate"));

		if (!StringUtils.isEmpty(msg)) {
			throw new BusinessException(msg);
		}
	}

	protected boolean isActionEnable() {
		if (null != getModel().getSelectedData()) {
			Object[] objs = ((BillManageModel) getModel()).getSelectedOperaDatas();
			if ((objs != null) && (objs.length > 0)) {
				return true;
			}
		}
		return false;
	}
}
