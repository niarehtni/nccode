package nc.ui.om.hrdept.validator;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.validation.IValidationService;
import nc.bs.uif2.validation.ValidationException;
import nc.bs.uif2.validation.ValidationFailure;
import nc.hr.utils.ResHelper;
import nc.itf.om.IDeptQueryService;
import nc.ui.om.pub.GenericBillVOHelper;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.uif2.editor.BillForm;
import nc.vo.om.hrdept.AggHRDeptVO;
import nc.vo.om.hrdept.DeptChangeType;
import nc.vo.om.hrdept.DeptHistoryVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

/**
 * 部门插入更新校验类<br>
 * 
 * @author zhangdd
 * 
 */
public class DeptInsertUpdateValidator implements IValidationService {

	BillForm billForm = null;

	public BillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(BillForm billForm) {
		this.billForm = billForm;
	}

	@SuppressWarnings("unused")
	private UIRefPane getHeadRefPane(String itemName) {
		return (UIRefPane) getBillForm().getBillCardPanel().getHeadItem(itemName).getComponent();
	}

	@Override
	public void validate(Object obj) throws ValidationException {
		// UIRefPane fatherOrgPane = getHeadRefPane("pk_fatherorg");
		// String fatherOrgCanceled = (String)
		// fatherOrgPane.getRefModel().getValue("org_dept.hrcanceled");

		if (obj == null) {
			return;
		}
		AggHRDeptVO aggDeptVO = (AggHRDeptVO) obj;
		HRDeptVO deptVO = (HRDeptVO) aggDeptVO.getParentVO();

		// 编辑后，校验成立日期（成立日期不能晚于部门变更历史记录中任何一条记录的生效日期）——heqiaoa 2014-08-27
		UFLiteralDate createDate = deptVO.getCreatedate();
		DeptHistoryVO[] dhVOs = GenericBillVOHelper.getBodyValueVOs(getBillForm().getBillCardPanel(),
				DeptHistoryVO.BILLCARDNAME, DeptHistoryVO.class);
		for (DeptHistoryVO dhVO : dhVOs) {
			if (!DeptChangeType.ESTABLISH.equals(dhVO.getChangetype()) && createDate.after(dhVO.getEffectdate())) {
				// ssx modified on 2019-10-02
				// 啟碁要求去掉該校驗
				Logger.error(ResHelper.getString("6005dept", "16005dept0008")/*
																			 * @res
																			 * "部门设立日期不能晚于部门变更历史中其他记录的生效日期"
																			 */);
				// end
			}
		}

		// 校验上级
		if (StringUtils.isEmpty(deptVO.getPk_fatherorg())) {
			return;
		}
		AggHRDeptVO fatherAggVO = null;
		try {
			fatherAggVO = (AggHRDeptVO) NCLocator.getInstance().lookup(IDeptQueryService.class)
					.queryByPk(deptVO.getPk_fatherorg());
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		if (fatherAggVO != null && ((HRDeptVO) fatherAggVO.getParentVO()).getHrcanceled().booleanValue()) {
			throw newValidationException(ResHelper.getString("6005dept", "06005dept0176")/*
																						 * @
																						 * res
																						 * "不能选择已经撤销的部门作为上级部门"
																						 */);
		}
		if (deptVO.getPk_dept() != null && deptVO.getPk_dept().equals(deptVO.getPk_fatherorg())) {
			throw newValidationException(ResHelper.getString("6005dept", "06005dept0177")/*
																						 * @
																						 * res
																						 * "不能选择当前修改部门作为上级部门"
																						 */);
		}
	}

	/**
	 * 返回校验异常对象<br>
	 * 
	 * @param msg
	 * @return
	 */
	private ValidationException newValidationException(String msg) {
		ValidationFailure failure = new ValidationFailure(msg);
		List<ValidationFailure> failureList = new ArrayList<ValidationFailure>();
		failureList.add(failure);
		ValidationException e = new ValidationException(failureList);
		return e;
	}

}