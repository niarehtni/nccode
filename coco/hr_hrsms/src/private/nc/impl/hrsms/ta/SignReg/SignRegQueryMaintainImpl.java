package nc.impl.hrsms.ta.SignReg;



import nc.hr.frame.persistence.HrBatchService;
import nc.itf.hrsms.ta.SignReg.ISignRegQueryMaintain;
import nc.vo.pub.BusinessException;
import nc.vo.ta.signcard.AggSignVO;
import nc.vo.ta.signcard.SignRegVO;
import nc.vo.uif2.LoginContext;

public class SignRegQueryMaintainImpl implements ISignRegQueryMaintain{

	private HrBatchService serviceTemplate;

	public static final String DOC_NAME = "5b53e23c-9bd9-4eef-bea7-0b4eb38fe120";
	private HrBatchService getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new HrBatchService(DOC_NAME);
		}
		return serviceTemplate;
	}
	


	@Override
	public SignRegVO[] queryVOsByCondition(LoginContext context,
			String condition) throws BusinessException {

		return getServiceTemplate().queryByCondition(SignRegVO.class, condition);
	}

	@Override
	public AggSignVO queryByPk(String pk_signreg) throws BusinessException {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public AggSignVO[] queryByCondition(LoginContext context, String condition)
			throws BusinessException {
		// TODO 自动生成的方法存根
		return null;
	}

}
