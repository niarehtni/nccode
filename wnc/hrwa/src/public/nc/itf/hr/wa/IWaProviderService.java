/**
 * 
 */
package nc.itf.hr.wa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.DataVO;

/**
 * 报表使用
 * @author zhangliangb
 *
 * @since V6.0 2010-9-10
 */
public interface IWaProviderService {


	public List<HashMap<String, Object>> query(String sql) throws BusinessException;

	public DataVO[] query4VO(String sql) throws BusinessException;

	public GeneralVO[] getAllTmItem(String pkGroup,String pkOrg, String accyear) throws BusinessException;

	public Object getReportVar(String id,int type)  throws BusinessException;

	public GeneralVO[] getPsnjobsBySql(String pk_org, String whereSql)
			throws BusinessException;
	
	public void update4D(String pk_wa_class, String cyearperiods, String condition) throws BusinessException;
	
	public void update4E(String pk_wa_class, String cyearperiods, String condition) throws BusinessException;
}
