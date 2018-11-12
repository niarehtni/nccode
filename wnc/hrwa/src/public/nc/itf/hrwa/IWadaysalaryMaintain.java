package nc.itf.hrwa;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.wa.paydata.AggDaySalaryVO;
import nc.vo.wa.pub.WaDayLoginContext;
import nc.vo.pub.BusinessException;

public interface IWadaysalaryMaintain {

	public void delete(AggDaySalaryVO[] vos) throws BusinessException;

	public AggDaySalaryVO[] insert(AggDaySalaryVO[] vos) throws BusinessException;

	public AggDaySalaryVO[] update(AggDaySalaryVO[] vos) throws BusinessException;

	public AggDaySalaryVO[] query(IQueryScheme queryScheme)
			throws BusinessException;
	public AggDaySalaryVO[] query(IQueryScheme queryScheme, WaDayLoginContext context)
			throws BusinessException;

}