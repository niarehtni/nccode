package nc.impl.ta.overtime.validator;

import nc.hr.frame.persistence.HrDefaultValidatorFactory;

public class OvertimeApplyValidatorFactory extends HrDefaultValidatorFactory {
	public void initValidator() {
		mapValidator.put("insert", new Class[] {
				OvertimeApplyApproveValidator.class,
				WITSOvertimeApplyValidator.class });
		mapValidator.put("update", new Class[] {
				OvertimeApplyApproveValidator.class,
				WITSOvertimeApplyValidator.class });
		mapValidator.put("batchinsertupdate", new Class[] {
				OvertimeApplyApproveValidator.class,
				WITSOvertimeApplyValidator.class });
	}
}
