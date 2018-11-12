package nc.itf.hr.wa;

import nc.vo.hr.caculate.CaculateTypeVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.paydata.DataSVO;
import nc.vo.wa.paydata.PsndocWaVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;

/**
 * н�ʷ��Žӿ���
 * 
 * @author: zhangg
 * @date: 2009-11-23 ����01:19:34
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public interface IPaydataManageService {

	public void update(Object vo, WaLoginVO waLoginVO) throws BusinessException;

	/**
	 * ������ʾ����
	 * 
	 * @author liangxr on 2010-5-26
	 * @param classItemVOs
	 * @return
	 * @throws BusinessException
	 */
	public int updateClassItemVOsDisplayFlg(WaClassItemVO[] classItemVOs) throws BusinessException;

	/**
	 * ���
	 * 
	 * @author liangxr on 2010-6-18
	 * @param waLoginVO
	 * @param whereCondition
	 * @param isRangeAll
	 * @throws nc.vo.pub.BusinessException
	 */
	public void onCheck(WaLoginVO waLoginVO, String whereCondition, Boolean isRangeAll)
			throws nc.vo.pub.BusinessException;

	/**
	 * ȡ�����
	 * 
	 * @author liangxr on 2010-6-18
	 * @param waLoginVO
	 * @throws nc.vo.pub.BusinessException
	 */
	public void onUnCheck(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll)
			throws nc.vo.pub.BusinessException;

	/**
	 * ����
	 */
	public void onPay(WaLoginContext loginContext) throws nc.vo.pub.BusinessException;

	/**
	 * н�ʷ��Ŵ������ݻ��� ���μ�˰��������ÿ����Ա�����һ�η��������б��μ�˰������ֵ�� �ѿ�˰�������ѿ�˰����0��
	 * ������ֵ����Ŀ�������з��Ŵ����и���Ŀ�ĺϼ�ֵ�� �ַ��ͺ���������Ŀ����ÿ����Ա���һ�η��������и���Ŀ��ֵ��
	 * 
	 * @param waLoginVO
	 * @throws BusinessException
	 */
	public void collectWaTimesData(WaLoginVO waLoginVO) throws BusinessException;

	/**
	 * ȡ������
	 * 
	 * @author liangxr on 2010-5-26
	 * @param waLoginVO
	 * @throws nc.vo.pub.BusinessException
	 */
	public void onUnPay(WaLoginVO waLoginVO) throws nc.vo.pub.BusinessException;

	public void onSaveDataSVOs(WaLoginVO waLoginVO, DataSVO[] dataSVOs) throws BusinessException;

	/**
	 * �滻
	 * 
	 * @author liangxr on 2010-5-26
	 * @param waLoginVO
	 * @param whereCondition
	 * @param replaceItem
	 * @param formula
	 * @throws BusinessException
	 */
	public void onReplace(WaLoginVO waLoginVO, String whereCondition, WaClassItemVO replaceItem, String formula,
			SuperVO... superVOs) throws BusinessException;

	/**
	 * ����
	 * 
	 * @author liangxr on 2010-5-26
	 * @param aRecaVO
	 * @throws nc.vo.pub.BusinessException
	 */
	void reTotal(WaLoginVO waLoginVO) throws nc.vo.pub.BusinessException;

	/**
	 * ����
	 * 
	 * @author liangxr on 2010-7-7
	 * @param loginContext
	 * @param caculateTypeVO
	 * @param conditiupdateCalFlag4OnTimeon
	 * @throws BusinessException
	 */
	public void onCaculate(WaLoginContext loginContext, CaculateTypeVO caculateTypeVO, String condition,
			SuperVO... superVOs) throws BusinessException;

	/**
	 * ���¼�������������йص�н����Ŀ
	 * 
	 * @author Ares.Tank 2018-10-23 17:06:49
	 * @param loginContext
	 * @param caculateTypeVO
	 * @param conditiupdateCalFlag4OnTimeon
	 * @throws BusinessException
	 */
	public void reCalculateRelationWaItem(WaLoginContext loginContext, CaculateTypeVO caculateTypeVO, String condition,
			SuperVO... superVOs) throws BusinessException;

	/**
	 * wa_data����״̬�޸�Ϊȫ��δ���� wa_data���״̬��Ϊȫ��δ��� wa_periodstate �޸�Ϊδ����,δ���
	 * 
	 * @author liangxr on 2010-7-7
	 * @param waLoginVO
	 * @throws nc.vo.pub.BusinessException
	 */
	void updatePaydataFlag(String pk_wa_class, String cyear, String cperiod) throws nc.vo.pub.BusinessException;

	/**
	 * ������н�ʷ��� ��ҵ���ڼ䣩 ���ĳ����Ŀ�� wadata�е�����
	 */
	void clearClassItemData(WaClassItemVO vo) throws nc.vo.pub.BusinessException;

	/**
	 * 
	 * ɾ����ʾ����
	 * 
	 * @param context
	 * @param type
	 *            ��ʾ�������ͣ�ͨ�����á���������
	 * @throws BusinessException
	 */
	public void deleteDisplayInfo(WaLoginContext context, String type) throws BusinessException;

	/**
	 * ���¼���ϲ���˰
	 * 
	 * @param loginContext
	 *            WaLoginContext
	 * @throws BusinessException
	 */
	// shenliangc 20140830 �ϲ���˰�����������ֻ��������ϲ�ѯ��������Ա���ݣ���Ҫ�������������
	public void reCaculate(WaLoginContext loginContext, String whereCondition) throws BusinessException;

	/**
	 * shenliangc 20140826 ʱ��н�ʼ��㱣��֮������������Ӧ��Ա�ķ������ݼ����־
	 * 
	 * @param psndocWaVOs
	 * @throws BusinessException
	 */
	public void updateCalFlag4OnTime(PsndocWaVO[] psndocWaVOs) throws BusinessException;

}