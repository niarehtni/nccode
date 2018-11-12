package nc.itf.hrsms.hi.psndoc.manage;

import nc.vo.hi.psndoc.CertVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.pub.BusinessException;
/**
 * 
 * @author shaochj
 * @date Jun 24, 2015
 * �ӿ�
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
	 * ����pkɾ��������¼
	 * 
	 * @param pk_psnjob
	 * @return
	 * @throws BusinessException
	 */
	public int deletePsnJob(String pk_psnjob) throws BusinessException;
	
	
	
	/**
	 * ����pk_psndocɾ��û��ת������֯��ϵ
	 * 
	 * @param pk_psnorg
	 * @return
	 * @throws BusinessException
	 */
	public int deleteNotIndocPsnOrg(String pk_psndoc) throws BusinessException;
	
	/**
	 * ����֤�����ͺ�֤��id ��ѯID
	 * 
	 * @param pk_psnorg
	 * @return
	 * @throws BusinessException
	 */
	public  CertVO[]  queryCertVO(String idtype,String id) throws BusinessException;
	
}
