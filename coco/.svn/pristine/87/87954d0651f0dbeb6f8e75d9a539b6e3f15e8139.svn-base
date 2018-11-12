package nc.impl.hrsms.hi.psndoc.manage;

import java.util.ArrayList;

import nc.bs.dao.BaseDAO;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.itf.hrsms.hi.psndoc.manage.IPsndocManageServicePB;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.vo.hi.psndoc.CertVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.pub.BusinessException;
/**
 * 
 * @author shaochj
 * @date Jun 24, 2015
 * 接口实现
 */
public class PsndocManageServicePBImpl implements IPsndocManageServicePB{

	private SimpleDocServiceTemplate serviceTemplate;

	private SimpleDocServiceTemplate getServiceTemplate() {
		if (serviceTemplate == null)
			// 参数是 元数据id
			serviceTemplate = new SimpleDocServiceTemplate("218971f0-e5dc-408b-9a32-56529dddd4db");
		return serviceTemplate;
	}
	
	@Override
	public PsndocAggVO updateAggVO(PsndocAggVO vo) throws BusinessException {
		return getServiceTemplate().update(vo, true);
	}

	@Override
	public int deletePsnJob(String pk_psnjob) throws BusinessException {
		if(pk_psnjob == null){
			return -1;
		}else{
			BaseDAO dao = new BaseDAO();
			dao.deleteByClause(nc.vo.hi.psndoc.PsnJobVO.class, "pk_psnjob = '"+pk_psnjob+"'" );
			return 1;
		}
		
	}

	@Override
	public int deleteNotIndocPsnOrg(String pk_psnorg) throws BusinessException {
		if(pk_psnorg == null){
			return -1;
		}else{
			BaseDAO dao = new BaseDAO();
			dao.deleteByClause(PsnOrgVO.class, "pk_psndoc = '"+pk_psnorg+"' and indocflag = 'N'" );
			return 1;
		}
	}

	@Override
	public CertVO[] queryCertVO(String idtype, String id)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		ArrayList<CertVO> al = (ArrayList<CertVO>) dao.executeQuery(" select pk_psndoc_sub from hi_psndoc_cert where iseffect = 'Y' and idtype = '"+idtype+"' and id = '"+id+"'", new BeanListProcessor(CertVO.class));
		return al == null ? new CertVO[0] : al.toArray(new CertVO[0]);
	}

}
