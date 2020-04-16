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
 * ���Ų������У����<br>
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

		// �༭��У��������ڣ��������ڲ������ڲ��ű����ʷ��¼���κ�һ����¼����Ч���ڣ�����heqiaoa 2014-08-27
		UFLiteralDate createDate = deptVO.getCreatedate();
		DeptHistoryVO[] dhVOs = GenericBillVOHelper.getBodyValueVOs(getBillForm().getBillCardPanel(),
				DeptHistoryVO.BILLCARDNAME, DeptHistoryVO.class);
		for (DeptHistoryVO dhVO : dhVOs) {
			if (!DeptChangeType.ESTABLISH.equals(dhVO.getChangetype()) && createDate.after(dhVO.getEffectdate())) {
				// ssx modified on 2019-10-02
				// ����Ҫ��ȥ��ԓУ�
				Logger.error(ResHelper.getString("6005dept", "16005dept0008")/*
																			 * @res
																			 * "�����������ڲ������ڲ��ű����ʷ��������¼����Ч����"
																			 */);
				// end
			}
		}

		// У���ϼ�
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
																						 * "����ѡ���Ѿ������Ĳ�����Ϊ�ϼ�����"
																						 */);
		}
		if (deptVO.getPk_dept() != null && deptVO.getPk_dept().equals(deptVO.getPk_fatherorg())) {
			throw newValidationException(ResHelper.getString("6005dept", "06005dept0177")/*
																						 * @
																						 * res
																						 * "����ѡ��ǰ�޸Ĳ�����Ϊ�ϼ�����"
																						 */);
		}
	}

	/**
	 * ����У���쳣����<br>
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