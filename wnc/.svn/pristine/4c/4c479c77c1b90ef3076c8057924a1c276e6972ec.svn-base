package nc.itf.twhr;

import nc.itf.pubapp.pub.smart.ISmartService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.twhr.nhicalc.NhiCalcVO;

public interface INhicalcMaintain extends ISmartService {
	public NhiCalcVO[] query(IQueryScheme queryScheme)
			throws BusinessException, Exception;

	public boolean isAudit(String pk_org, String cyear, String cperiod)
			throws BusinessException;

	public void audit(String pk_org, String cyear, String cperiod)
			throws BusinessException;

	public void unAudit(String pk_org, String cyear, String cperiod)
			throws BusinessException;
}
