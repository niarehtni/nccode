package nc.itf.hrsms.hi.psndoc.manage;

import nc.vo.hi.psndoc.CertVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.pub.BusinessException;
/**
 * 
 * @author shaochj
 * @date Jun 24, 2015
 * 接口
 */
public interface IPsndocManageServicePB {

	/**
	 * update PsndocAggVO
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public PsndocAggVO updateAggVO(PsndocAggVO vo) throws BusinessException;
	
	
	/**
	 * 根据pk删除工作记录
	 * 
	 * @param pk_psnjob
	 * @return
	 * @throws BusinessException
	 */
	public int deletePsnJob(String pk_psnjob) throws BusinessException;
	
	
	
	/**
	 * 根据pk_psndoc删除没有转档的组织关系
	 * 
	 * @param pk_psnorg
	 * @return
	 * @throws BusinessException
	 */
	public int deleteNotIndocPsnOrg(String pk_psndoc) throws BusinessException;
	
	/**
	 * 根据证件类型和证件id 查询ID
	 * 
	 * @param pk_psnorg
	 * @return
	 * @throws BusinessException
	 */
	public  CertVO[]  queryCertVO(String idtype,String id) throws BusinessException;
	
}
