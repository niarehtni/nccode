package nc.itf.hr.wa;

import java.util.HashMap;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.adjust.AdjustWadocVO;
import nc.vo.wa.adjust.PsnappaproveBVO;
import nc.vo.wa.grade.AggWaGradeVO;
import nc.vo.wa.grade.WaCriterionVO;
import nc.vo.wa.grade.WaGradeVO;
import nc.vo.wa.grade.WaGradeVerVO;
import nc.vo.wa.grade.WaPsnhiBVO;

/**
 * н�ʱ�׼����Ӧ�÷���<BR>
 * <BR>
 * 
 * @author: xuhw
 * @date: 2009-11-10
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public interface IWaGradeService {
	/**
	 * ɾ��
	 * 
	 * @author xuhw on 2009-11-11
	 * @param vo
	 * @throws BusinessException
	 */
	public abstract void deleteWaGradeVO(AggWaGradeVO vo) throws BusinessException;

	/**
	 * ����
	 * 
	 * @author xuhw on 2009-11-11
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public abstract AggWaGradeVO insertWaGradeVO(AggWaGradeVO vo) throws BusinessException;

	/**
	 * ����
	 * 
	 * @author xuhw on 2009-11-11
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public abstract AggWaGradeVO updateWaGradeVO(AggWaGradeVO vo) throws BusinessException;

	/**
	 * ����н�ʱ�׼ֵ��
	 * 
	 * @author xuhw on 2009-12-1
	 * @param wagradevo
	 * @param criterions
	 * @return
	 * @throws BusinessException
	 */
	public Object[] updateCriterionArray(WaGradeVO wagradevo, WaCriterionVO[] criterions) throws BusinessException;

	/**
	 * ����н����Ա�����������ֵ��
	 * 
	 * @author xuhw on 2009-12-1
	 * @param wagradevo
	 * @param criterions
	 * @return
	 * @throws BusinessException
	 */
	public Object[] updateStdHiVOArray(WaGradeVO wagradevo, WaPsnhiBVO[] waStdHiVOs, int classType)
			throws BusinessException;

	/**
	 * 
	 * @param strPKPrmlv
	 * @param strPKSeclv
	 * @return
	 * @throws BusinessException
	 */
	public WaCriterionVO getCrierionVOByPrmSec(String strPKPrmlv, String strPKSeclv) throws BusinessException;

	/**
	 * �����z�����ڌ����汾ȡн�Y�˜�
	 * 
	 * @param checkDate
	 * @param strPKPrmlv
	 * @param strPKSeclv
	 * @return
	 * @throws BusinessException
	 * 
	 * @author sunsx
	 * @since 2020-01-16
	 */
	public WaCriterionVO getCrierionVOByPrmSec(UFLiteralDate checkDate, String strPKPrmlv, String strPKSeclv)
			throws BusinessException;

	// end

	/**
	 * 
	 * @param AdjustWadocVO
	 *            []
	 * @return
	 * @throws BusinessException
	 */
	public HashMap<String, WaCriterionVO> getCrierionVOMapByPrmSec(AdjustWadocVO[] adjustWadocPsnInfoVOs)
			throws BusinessException;

	/**
	 * 
	 * @param PsnappaproveVO
	 *            []
	 * @return
	 * @throws BusinessException
	 */
	public HashMap<String, WaCriterionVO> getCrierionVOMapByPrmSec(PsnappaproveBVO[] psnappaproveBVOs, boolean isApprove)
			throws BusinessException;

	/**
	 * ����н�ʱ�׼������
	 * 
	 * @param vervo
	 * @return
	 * @throws BusinessException
	 */
	public WaGradeVerVO processCriterionArray(WaGradeVO gradevo, WaGradeVerVO vervo) throws BusinessException;

	/**
	 * ���ƹ���-����н�ʱ�׼������
	 * 
	 * @param gradevo
	 * @param vervo
	 * @return
	 * @throws BusinessException
	 */
	public WaGradeVerVO processCriterionArray4Copy(WaGradeVO gradevo, WaGradeVerVO vervo) throws BusinessException;

	/**
	 * ɾ��н�ʱ�׼��汾���ݣ�����ɾ����Ӧн�ʱ�׼��
	 * 
	 * @param vervo
	 * @return
	 * @throws BusinessException
	 */
	public void deleteGradeVerVO(WaGradeVerVO vervo) throws BusinessException;

	/**
	 * ��ѯн�ʱ�׼�汾��Ϣ BY gradePK
	 */
	public WaGradeVerVO[] queryGradeVerByGradePK(String strGradePk) throws BusinessException;

	/**
	 * ��ѯ��Ч��н�ʱ�׼�汾��Ϣ BY gradePK
	 */
	public WaGradeVerVO queryEffectGradeVerByGradePK(String strGradePk) throws BusinessException;

	/**
	 * ����н�ʱ�׼���������ѯн�ʱ�׼�汾�����汾��
	 * 
	 * @param strPKWaGrd
	 * @return
	 * @throws BusinessException
	 */
	public UFDouble getMaxVerNum(String strPKWaGrd) throws BusinessException;

	/**
	 * ɾ��н�ʱ�׼�汾��¼����PK
	 * 
	 * @param strGradeVerPK
	 * @throws BusinessException
	 */
	public void deleteGradeVerByPk(String strGradeVerPK) throws BusinessException;

	/**
	 * н�ʱ�׼������Ӽ��𵵱�ʱ�����֤
	 * 
	 * @param gradeVO
	 * @throws BusinessException
	 */
	public void validatorHasGradeVer(String strGradePK) throws BusinessException;

	/**
	 * ����н�ʱ�׼���PK��ѯ��Ч��н�ʱ�׼����
	 * 
	 * @param strPKGrade
	 * @return
	 * @throws BusinessException
	 */
	public WaCriterionVO[] getEffectCrierionsVOByGradePK(String strPKGrade) throws BusinessException;

	/**
	 * У��н�ʱ�׼����Ƿ񱻺���ҵ�����ã��汾��Ч��־�Ƿ����Ϊ��У�飩
	 * 
	 * @param strGradepk
	 * @throws BusinessException
	 */
	public String validateGradeHaveReferenceByBusiness(String strGradepk) throws BusinessException;

	/**
	 * У��н�ʱ�׼����Ƿ�����Ч�汾
	 * 
	 * @param strGradepk
	 * @throws BusinessException
	 */
	public boolean validateEffectVersion(String strGradepk) throws BusinessException;

	/**
	 * ɾ�����еļ�����Ա���Ժ͵�����Ա����(�޸ļ�����Ա���Ժ͵�����Ա����)
	 * 
	 * @param strGradepk
	 * @throws BusinessException
	 */
	public void deleteWaPsnhiByPK(String strGradepk, int classType) throws BusinessException;

	/**
	 * ɾ����ʷδ��Ч�İ汾��Ϣ(�޸�ʱ���Ƿ�൵�����仯������͵�����������ɾ��ʱ)
	 * 
	 * @param strGradepk
	 * @throws BusinessException
	 */
	public void deleteVersionByGradePK(String strGradepk) throws BusinessException;

	/**
	 * ������Ա������������(���Ʊ���)
	 * 
	 * @param strGradepk
	 * @throws BusinessException
	 */
	public void insertCopyStdHiVOArray(WaPsnhiBVO[] waStdHiVOs) throws BusinessException;

}
