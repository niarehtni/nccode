package nc.impl.wa.othersource;

import nc.bs.dao.BaseDAO;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.itf.hr.wa.IOtherSourceManageService;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.wa.othersource.OtherSourceVO;

public class OtherSourceServiceImpl implements IOtherSourceManageService {
	private final String DOC_NAME = "wa_otherdatasource";
	private SimpleDocServiceTemplate serviceTemplate;
	private SimpleDocServiceTemplate getServiceTemplate()
	{
		if (serviceTemplate == null)
		{
			serviceTemplate = new SimpleDocServiceTemplate(DOC_NAME);
		}
		return serviceTemplate;
	}
	@Override
	public OtherSourceVO insert(OtherSourceVO vo) throws BusinessException {
		return getServiceTemplate().insert(vo);
	}

	@Override
	public void delete(OtherSourceVO vo) throws BusinessException {
		getServiceTemplate().delete(vo);

	}

	@Override
	public OtherSourceVO update(OtherSourceVO vo) throws BusinessException {
		return getServiceTemplate().update(vo, false);
	}

    /*********************************************************************************************************
     * 执行一个查询的SQL语句<br>
     * Created on 2006-9-4 12:57:18<br>
     * @author Rocex Wang
     * @see nc.itf.hr.frame.IPersistenceHome#executeQuery(java.lang.String,
     *      nc.jdbc.framework.processor.ResultSetProcessor)
     ********************************************************************************************************/
    public Object executeQuery(String arg0, ResultSetProcessor arg1) throws BusinessException
    {
        return new BaseDAO().executeQuery(arg0, arg1);
    }
}
