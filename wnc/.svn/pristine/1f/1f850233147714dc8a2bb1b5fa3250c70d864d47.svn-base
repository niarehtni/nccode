package nc.itf.hr.wa;

import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.wa.othersource.OtherSourceVO;

public interface IOtherSourceManageService {
	public abstract  OtherSourceVO insert(OtherSourceVO vo)  throws nc.vo.pub.BusinessException;

	public abstract  void delete(OtherSourceVO vo)  throws nc.vo.pub.BusinessException;
	
	public abstract  OtherSourceVO update(OtherSourceVO vo)  throws nc.vo.pub.BusinessException;

	 /*********************************************************************************************************
     * 执行一个查询的SQL语句<br>
     * Created on 2006-9-4 12:53:45<br>
     * @param strSQL
     * @param resultProcessor
     * @return Object
     * @throws nc.vo.pub.BusinessException
     ********************************************************************************************************/
	public abstract Object executeQuery(String strSQL, ResultSetProcessor resultProcessor) throws BusinessException;
}
