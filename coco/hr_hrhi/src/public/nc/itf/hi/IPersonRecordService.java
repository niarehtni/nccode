package nc.itf.hi;

import java.util.HashMap;
import java.util.Map;

import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.hi.psndoc.AssVO;
import nc.vo.hi.psndoc.BminfoVO;
import nc.vo.hi.psndoc.CapaVO;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.WainfoVO;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFLiteralDate;

/**
 * ��Ա��Ϣ/�䶯�Ӽ���¼���������ӿ�
 */
public interface IPersonRecordService
{
	/**
	 * ��ְ����
	 * 
	 * @param dimission
	 *            ��ְ������¼
	 * @param pkHrorg
	 *            ��ְ��HR��֯
	 * @param isDisablePsn �Ƿ�ͣ����ְ��Ա
	 * @return PsnJobVO
	 * @throws BusinessException
	 */
	public PsnJobVO addNewDimission(PsnJobVO dimission, String pkHrorg, boolean isDisablePsn) throws BusinessException;
	/**
	 * ��ְ����
	 * 
	 * @param dimission
	 *            ��ְ������¼
	 * @param pkHrorg
	 *            ��ְ��HR��֯
	 * @param isDisablePsn �Ƿ�ͣ����ְ��Ա
	 * @param Date �˱�����
	 * @author Ares.Tank 2018-9-15 22:30:29
	 * @return PsnJobVO
	 * 
	 * @throws BusinessException
	 */
	public PsnJobVO addNewDimissionWithDate(PsnJobVO dimission, String pkHrorg, boolean isDisablePsn,UFLiteralDate endDate) throws BusinessException;
	
	/**
     * ��ְ����----Ϊ����ְʱ���ɺ�ͬί�� add by yanglt 2015-08-05
     * 
     * @param dimission
     *            ��ְ������¼
     * @param pkHrorg
     *            ��ְ��HR��֯
     * @param isDisablePsn �Ƿ�ͣ����ְ��Ա
     * @return PsnJobVO
     * @throws BusinessException
     */
    public PsnJobVO addNewDimission(PsnJobVO dimission, String pkHrorg, boolean isDisablePsn, String pk_hrcm_org) throws BusinessException;

	/**
     * �������
     * 
     * @param psnjob
     *            ���乤����¼
     * @param isSynWork
     *            �Ƿ�ͬ����������
     * @param pk_hrorg
     *            �����HR��֯
     * @return PsnJobVO
     * @throws BusinessException
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    public PsnJobVO addNewPsnjob(PsnJobVO psnjob, boolean isSynWork, String pk_hrorg) throws BusinessException;
    
    /**
     * �������-����֯����
     * 
     * @param psnjob
     *            ���乤����¼
     * @param isSynWork
     *            �Ƿ�ͬ����������
     * @param pk_hrorg
     *            �����HR��֯
     * @return PsnJobVO
     * @throws BusinessException
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    public PsnJobVO addNewPsnjobTran(PsnJobVO psnjob, boolean isSynWork, boolean isFinshPart, String pk_hrorg) throws BusinessException;
	
	/**
	 * �������----Ϊ�˵���ʱ���ɺ�ͬί�� add by yanglt 2015-08-05
	 * 
	 * @param psnjob
	 *            ���乤����¼
	 * @param isSynWork
	 *            �Ƿ�ͬ����������
	 * @param pk_hrorg
	 *            �����HR��֯
	 * @return PsnJobVO
	 * @throws BusinessException
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public PsnJobVO addNewPsnjob(PsnJobVO psnjob, boolean isSynWork, String pk_hrorg, String pk_hrcm_org) throws BusinessException;

	/**
	 * ���ݹ���/��ְ��¼����������¼
	 * 
	 * @param psnjobVO
	 *            ����/��ְ��¼
	 * @throws BusinessException
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public void addWork(PsnJobVO psnjobVO) throws BusinessException;

	/**
	 * ���ݹ���/��ְ��¼����������¼,��ͬ��������Ϣ
	 * 
	 * @param psnjobVO
	 *            ����/��ְ��¼
	 * @throws BusinessException
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public void addWorkWithoutSynPsndoc(PsnJobVO retVO) throws BusinessException;

	/**
	 * ����ĳ��������¼��Ӧ��������¼
	 * 
	 * @param pk_psnjob
	 *            ������¼����
	 * @param endDate
	 *            ��������
	 * @throws BusinessException
	 */
	public void endWork(String pk_psnjob, UFLiteralDate endDate) throws BusinessException;

	/**
	 * ������������,��ͬ��������Ϣ
	 * 
	 * @param pk_psnjob
	 * @param endDate
	 * @throws BusinessException
	 */
	public void endWorkWithoutSynPsndoc(String pk_psnjob, UFLiteralDate endDate) throws BusinessException;

	/**
	 * ������֯��ϵ��������ְID�õ�ĳ�Ӽ������¼�¼
	 * 
	 * @param <T>
	 * @param className
	 *            �Ӽ�class
	 * @param pk_psnorg
	 *            ��֯��ϵ����
	 * @param assgid
	 *            ��ְID ��ְ=1 ��ְ>1 ����Ϊnull
	 * @return < T extends SuperVO > T
	 * @throws BusinessException
	 */
	public <T extends SuperVO> T getLastVO(Class<T> className, String pk_psnorg, Integer assgid) throws BusinessException;

	/**
	 * ������Ա������Ϣ����pk_psndoc���Ҽ�ְ��¼����ְID
	 * 
	 * @param pk_psndoc
	 *            ��Ա������Ϣ����
	 * @return int
	 * @throws BusinessException
	 */
	public int getMaxAssgidByPsndoc(String pk_psndoc) throws BusinessException;

	/**
	 * ͨ���ϵ�pk_psnjob�õ����µ�pk_psnjob
	 * 
	 * @param oldPkpsnjob
	 *            �͵Ĺ�����¼����
	 * @return String
	 * @throws BusinessException
	 */
	public String getNewPkpsnjob(String oldPkpsnjob) throws BusinessException;

	/**
	 * ������Ա������Ϣ����pk_psndoc���ҵ�ǰ������¼����pk_psnjob
	 * 
	 * @param pk_psndoc
	 *            ��Ա������Ϣ����
	 * @return String
	 * @throws BusinessException
	 */
	public String getPsnjobByPsndoc(String pk_psndoc) throws BusinessException;

	/**
	 * ������Ա������Ϣ����pk_psndoc���ҵ�ǰ��֯��ϵ����pk_psnorg
	 * 
	 * @param pk_psndoc
	 *            ��Ա������Ϣ����
	 * @return String
	 * @throws BusinessException
	 */
	public String getPsnorgByPsndoc(String pk_psndoc) throws BusinessException;

	/**
	 * ������Ա������Ϣ����pk_psndoc���ҵ�ǰ����Ա��������֯��ϵ����pk_psnorg
	 * 
	 * @param pk_psndoc
	 * @return String
	 * @throws BusinessException
	 */
	public String getEmpPsnorgByPsndoc(String pk_psndoc) throws BusinessException;
	
	/**
	 * ������Ա������Ϣ����pk_psndoc���ҵ�ǰԱ�����е���֯��ϵ����pk_psnorg
	 * 
	 * @param pk_psndoc
	 * @return String
	 * @throws BusinessException
	 */
	public HashMap<String, String[]> getAllPsnorgByPsndoc(String[] pk_psndocs) throws BusinessException;
	
	/**
	 * ������Ա������Ϣ����pk_psndoc���ҵ�ǰ����Ա���������¹�����¼����pk_psnjob
	 * 
	 * @param pk_psndoc
	 * @return String
	 * @throws BusinessException
	 */
	public String getEmpPsnjobByPsndoc(String pk_psndoc) throws BusinessException;

	/**
	 * ����һ����¼
	 * 
	 * @param <T>
	 * @param vo
	 *            ������ļ�¼
	 * @param infoset
	 *            �ü�¼��Ӧ����Ϣ��
	 * @return < T extends SuperVO > T
	 * @throws BusinessException
	 */
	public <T extends SuperVO> T insertRecord(T vo, InfoSetVO infoset) throws BusinessException;

	/**
	 * ͬ��bd_psndoc.pk_org
	 * 
	 * @param pk_org
	 *            ��֯PK
	 * @param pk_psndoc
	 *            ��Ա������Ϣ����
	 * @throws BusinessException
	 */
	public void synPkorgOfPsndoc(String pk_org, String pk_psndoc) throws BusinessException;

	/**
	 * �������м�¼��recordnum��lastflag
	 * 
	 * @param vos
	 *            ��������recordnum˳������ 012345
	 * @throws BusinessException
	 */
	public void updateAllRecordnumAndLastflag(SuperVO[] vos) throws BusinessException;

	/**
	 * ��ĳһʱ�����һ����¼
	 * 
	 * @param vo
	 *            ��¼��Ϣ
	 * @param date
	 *            ��������
	 * @return SuperVO
	 * @throws BusinessException
	 */
	public SuperVO updateEndflagAndEnddate(SuperVO vo, UFLiteralDate date) throws BusinessException;

	/**
	 * ����һ����¼
	 * 
	 * @param <T>
	 * @param vo
	 * @return < T extends SuperVO > T
	 * @throws BusinessException
	 */
	public <T extends SuperVO> T updateRecord(T vo) throws BusinessException;

	/**
	 * ������ʷ��¼��recordnum��lastflag
	 * 
	 * @param vos
	 *            ��ʷ��¼VO����
	 * @throws BusinessException
	 */
	public void updateRecordnumAndLastflag(SuperVO[] vos) throws BusinessException;

	/**
	 * ͬ���������
	 * 
	 * @param psnjobVO
	 * @throws BusinessException
	 */
	public void addPsnChg(PsnJobVO psnjobVO, boolean isSyncPsndoc) throws BusinessException;

	/**
	 * ͬ���������(ת��ʱ��)
	 * 
	 * @param psnjobVO
	 * @throws BusinessException
	 */
	public void addPsnChgWhenIntoDoc(PsnJobVO psnjobVO) throws BusinessException;

	/**
	 * �������������Ա������¼,���ڲ��źϲ�������ת��,���м�¼�Ŀ�ʼ���ڶ���һ����
	 * 
	 * @param psnJobVO
	 * @throws BusinessException
	 */
	public void addPoiPsnjobs(PsnJobVO[] psnJobVO) throws BusinessException;

	/**
	 * �����Ա����һ��������¼,���ڲ��źϲ�
	 * 
	 * @param psnJobVO
	 * @throws BusinessException
	 */
	public void addPoiPsnjob(PsnJobVO psnJobVO) throws BusinessException;

	/**
	 * �����ְ�����Ϣ
	 * 
	 * @param partTimeVO
	 * @param isSynWork
	 * @param pk_hrorg
	 * @return
	 * @throws BusinessException
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public PartTimeVO savePartchgInf(PartTimeVO partTimeVO, boolean isSynWork, String pk_hrorg) throws BusinessException;

	/**
	 * @param part
	 * @param isSyncWork
	 */
	public void savePartchgInfo(PartTimeVO[] part, boolean isSyncWork) throws BusinessException;

	/**
	 * ͬ��psnorg�е�pk_hrorg
	 * 
	 * @param pk_psnorg
	 * @param pk_hrorg
	 * @throws BusinessException
	 */
	public void synPkhrorgOfPsnorg(String pk_psnorg, String pk_hrorg) throws BusinessException;

	/**
	 * �������
	 * 
	 * @param psnjobVO
	 *            ��ǰ��֯��ϵ�µ����¹�����¼
	 * @param jobDate
	 *            ������¼��ʼ����
	 * @param orgDate
	 *            ��֯��ϵ��ʼ����
	 * @throws BusinessException
	 */
	public void checkBeginDate(PsnJobVO psnjobVO, UFLiteralDate jobDate, UFLiteralDate orgDate) throws BusinessException;

	public void checkClerkCodeUnique(PsnJobVO psnjobVO) throws BusinessException;

	public void checkDeptPostCanceled(PsnJobVO psnjobVO) throws BusinessException;

	/**
	 * �������ӹ�����¼
	 * 
	 * @param array
	 * @param isSyncWork
	 * @param array2
	 */
	public void addNewPsnjobs(PsnJobVO[] psnjobs, boolean isSyncWork, String[] hrOrgs) throws BusinessException;

	public void checkPsnorg(String pk_psnjob) throws BusinessException;

	/**
	 * ͬ����Աн����Ϣ����ȡ��ȫ���ǵķ�ʽ����ɾ��ԭ�еģ����뵱ǰ��
	 * 
	 * @param wainfo key ��Ա�������� value н����ϢVO����
	 * @throws BusinessException
	 */
	public void syncPsnWaInfo(Map<String, WainfoVO[]> wainfo) throws BusinessException;

	/**
	 * ͬ����Ա�籣��Ϣ��
	 * 
	 * @param bminfo key ��Ա�������� value �籣��ϢVO����
	 * @throws BusinessException
	 */
	/**
	 * ͬ����Ա�籣��Ϣ��������Ա������ ͬ����ǰ���ֵ���Ϣ
	 * 
	 * @param bminfo key ��Ա�������� value �籣��ϢVO ��
	 *            һ��һ��VO�������ǰ��Աû�и�������Ϣ,��null��ɾ�����Ѿ����ڵ���Ϣ
	 * @param pk_insurance ����pk
	 * @throws BusinessException
	 */
	public void syncPsnBmInfo(Map<String, BminfoVO> bminfo, String pk_insurance) throws BusinessException;

	/**
	 * ͬ����Ա����������Ϣ
	 * 
	 * @param capainfo key ��Ա������¼���� value ����������ϢVO����
	 * @throws BusinessException
	 */
	public void syncPsnCapaInfo(Map<String, CapaVO[]> capainfo) throws BusinessException;

	/**
	 * ͬ����Ա������Ϣ
	 * 
	 * @param peinfo key ��Ա������¼���� value ���˽����ϢVO����
	 * @throws BusinessException
	 */
	public void syncPsnPeInfo(Map<String, AssVO[]> peinfo) throws BusinessException;

	public void syncPsnQulifyInfo(BatchOperateVO batchVO) throws BusinessException;

	/**
	 * �����ⶳʱ��ɾ���÷��������Ŀ��˼�¼
	 * 
	 * @param pk_scheme ��������
	 * @throws BusinessException
	 */
	public void delPsnPeInfo(String pk_scheme) throws BusinessException;

	/**
	 * ������Ϣ��˺���� �����Ӽ���lastflag �� recordnum;
	 * 
	 * @param map key ��Ա���� value �������Ϣ����������
	 * @throws BusinessException
	 */
	public void updateRecordnumAndLastflagForHrss(Map<String, String[]> map) throws BusinessException;
}
