package nc.itf.hr.wa;

import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.item.WaItemVO;
/**
 * ˰�Ŀ������Թ���
 * @author: xuhw 
 * @date: 2018-12-04  
 * @since: eHR V6.5
 * @�߲���: 
 * @�߲�����: 
 * @�޸���: 
 * @�޸�����: 
 */
public interface ITaxupgrade_toolQueryService
{
	/**
	 * ��ѯ���д����Ʒ���
	 * unInclude_pk_wa_class - �ų�����
	 * @return
	 * @throws BusinessException 
	 */
	GeneralVO[] queryTargetClassInfo(String pk_group) throws BusinessException;
	
	WaItemVO[] queryTaxItems(String pk_group) throws BusinessException;
	
	/**
	 * ��ѯĳ�����ŵĳ�ʼ������˰����Ŀ
	 * 
	 * @param pk_group
	 * @return
	 * @throws BusinessException
	 */
	public WaItemVO[] queryTaxUpgradeItems(String pk_group) throws BusinessException;
	
	/**
	 * ��ȡ˰����Ŀ
	 * ��ʼ5����Ŀ
	 * 
	 */
	WaItemVO[] getInitTaxItems(String pk_group) throws BusinessException;
	
	/**
	 * ��ѯ�Ѿ�������ķ���
	 * @param pk_group
	 * @return
	 * @throws BusinessException
	 */
	GeneralVO[] queryHasCopyClassInfo(String pk_group) throws BusinessException;
}