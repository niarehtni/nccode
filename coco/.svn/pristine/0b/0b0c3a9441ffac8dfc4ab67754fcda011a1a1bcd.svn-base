package nc.itf.hrsms.hi.psndoc.qry;

import nc.vo.bd.region.RegionVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;

public interface IPsndocQryservice {

	 /**
	  * 按sql语句查询 工作记录
	 * @param sql
	 * @return
	 * @throws BusinessException
	 */
	public PsnJobVO[] queryPsnJobVOsByCondition(String sql)throws BusinessException;
	
	public PsndocVO queryPsndocByPk(String pk_psndoc)throws BusinessException;
	
	public PsnOrgVO queryPsnOrgVOByPkPsndoc(String pk_psndoc)throws BusinessException;
	
	public RegionVO queryRegionVOByPk(String pk_region)throws BusinessException;
}
