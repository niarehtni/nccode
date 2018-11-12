package nc.impl.hrsms.hi.psndoc.qry;

import java.util.ArrayList;

import nc.bs.dao.BaseDAO;
import nc.itf.hrsms.hi.psndoc.qry.IPsndocQryservice;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.vo.bd.region.RegionVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;

public class PsndocQryServiceImpl implements IPsndocQryservice {
	
	private BaseDAO baseDao = new BaseDAO();
	
	private BaseDAO getDao(){
		return baseDao;
	}
	
	@Override
	public PsnJobVO[] queryPsnJobVOsByCondition(String sql) throws BusinessException {
		ArrayList<PsnJobVO> al = (ArrayList<PsnJobVO>) getDao().executeQuery(sql, new BeanListProcessor(PsnJobVO.class));
		return al == null?null:al.toArray(new PsnJobVO[0]);
	}

	@Override
	public PsndocVO queryPsndocByPk(String pk_psndoc) throws BusinessException {
		PsndocVO vo = (PsndocVO) getDao().retrieveByPK(PsndocVO.class,pk_psndoc);
		return vo;
	}
	

	@Override
	public PsnOrgVO queryPsnOrgVOByPkPsndoc(String pk_psndoc) throws BusinessException {
		String sql = "select * from hi_psnorg where pk_psndoc= '" + pk_psndoc + "'";// lastflag='Y'
		ArrayList<PsnOrgVO> al = (ArrayList<PsnOrgVO>) getDao().executeQuery(sql, new BeanListProcessor(PsnJobVO.class));
		return al == null?null:al.toArray(new PsnOrgVO[0])[0];
	}

	@Override
	public RegionVO queryRegionVOByPk(String pk_region) throws BusinessException {
		RegionVO vo = (RegionVO) getDao().retrieveByPK(RegionVO.class,pk_region);
		return vo;
	}

}
