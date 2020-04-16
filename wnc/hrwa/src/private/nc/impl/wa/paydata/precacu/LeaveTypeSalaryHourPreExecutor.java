package nc.impl.wa.paydata.precacu;

import nc.impl.wa.func.AbstractPreExcutorFormulaParse;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginContext;

public class LeaveTypeSalaryHourPreExecutor extends AbstractPreExcutorFormulaParse {
	private String pk_leaveitem;
	/**
	 * serail no
	 */
	private static final long serialVersionUID = -3139151556146041524L;

	public LeaveTypeSalaryHourPreExecutor(String pk_timeitem) {
		pk_leaveitem = pk_timeitem;
	}

	@Override
	public void excute(Object arg0, WaLoginContext arg1) throws BusinessException {
		// TODO 自動產生的方法 Stub

	}

}
