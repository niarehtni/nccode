package nc.itf.hrpub;

import nc.jdbc.framework.SQLParameter;
import nc.vo.pub.BusinessException;

public interface IMDExchangePersistService {
	public void executeQueryWithNoCMT(String[] strSQL) throws BusinessException;

	public void executeQueryWithNoCMT(String strSQL, SQLParameter paramList) throws BusinessException;
}
