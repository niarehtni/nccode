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
 * 人员信息/变动子集记录公共操作接口
 */
public interface IPersonRecordService
{
	/**
	 * 离职操作
	 * 
	 * @param dimission
	 *            离职工作记录
	 * @param pkHrorg
	 *            离职后HR组织
	 * @param isDisablePsn 是否停用离职人员
	 * @return PsnJobVO
	 * @throws BusinessException
	 */
	public PsnJobVO addNewDimission(PsnJobVO dimission, String pkHrorg, boolean isDisablePsn) throws BusinessException;
	/**
	 * 离职操作
	 * 
	 * @param dimission
	 *            离职工作记录
	 * @param pkHrorg
	 *            离职后HR组织
	 * @param isDisablePsn 是否停用离职人员
	 * @param Date 退保日期
	 * @author Ares.Tank 2018-9-15 22:30:29
	 * @return PsnJobVO
	 * 
	 * @throws BusinessException
	 */
	public PsnJobVO addNewDimissionWithDate(PsnJobVO dimission, String pkHrorg, boolean isDisablePsn,UFLiteralDate endDate) throws BusinessException;
	
	/**
     * 离职操作----为了离职时生成合同委托 add by yanglt 2015-08-05
     * 
     * @param dimission
     *            离职工作记录
     * @param pkHrorg
     *            离职后HR组织
     * @param isDisablePsn 是否停用离职人员
     * @return PsnJobVO
     * @throws BusinessException
     */
    public PsnJobVO addNewDimission(PsnJobVO dimission, String pkHrorg, boolean isDisablePsn, String pk_hrcm_org) throws BusinessException;

	/**
     * 调配操作
     * 
     * @param psnjob
     *            调配工作记录
     * @param isSynWork
     *            是否同步工作履历
     * @param pk_hrorg
     *            调配后HR组织
     * @return PsnJobVO
     * @throws BusinessException
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    public PsnJobVO addNewPsnjob(PsnJobVO psnjob, boolean isSynWork, String pk_hrorg) throws BusinessException;
    
    /**
     * 调配操作-跨组织调动
     * 
     * @param psnjob
     *            调配工作记录
     * @param isSynWork
     *            是否同步工作履历
     * @param pk_hrorg
     *            调配后HR组织
     * @return PsnJobVO
     * @throws BusinessException
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    public PsnJobVO addNewPsnjobTran(PsnJobVO psnjob, boolean isSynWork, boolean isFinshPart, String pk_hrorg) throws BusinessException;
	
	/**
	 * 调配操作----为了调配时生成合同委托 add by yanglt 2015-08-05
	 * 
	 * @param psnjob
	 *            调配工作记录
	 * @param isSynWork
	 *            是否同步工作履历
	 * @param pk_hrorg
	 *            调配后HR组织
	 * @return PsnJobVO
	 * @throws BusinessException
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public PsnJobVO addNewPsnjob(PsnJobVO psnjob, boolean isSynWork, String pk_hrorg, String pk_hrcm_org) throws BusinessException;

	/**
	 * 根据工作/兼职记录增加履历记录
	 * 
	 * @param psnjobVO
	 *            工作/兼职记录
	 * @throws BusinessException
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public void addWork(PsnJobVO psnjobVO) throws BusinessException;

	/**
	 * 根据工作/兼职记录增加履历记录,不同步基本信息
	 * 
	 * @param psnjobVO
	 *            工作/兼职记录
	 * @throws BusinessException
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public void addWorkWithoutSynPsndoc(PsnJobVO retVO) throws BusinessException;

	/**
	 * 结束某条工作记录对应的履历记录
	 * 
	 * @param pk_psnjob
	 *            工作记录主键
	 * @param endDate
	 *            结束日期
	 * @throws BusinessException
	 */
	public void endWork(String pk_psnjob, UFLiteralDate endDate) throws BusinessException;

	/**
	 * 结束工作履历,不同步基本信息
	 * 
	 * @param pk_psnjob
	 * @param endDate
	 * @throws BusinessException
	 */
	public void endWorkWithoutSynPsndoc(String pk_psnjob, UFLiteralDate endDate) throws BusinessException;

	/**
	 * 根据组织关系主键与任职ID得到某子集的最新记录
	 * 
	 * @param <T>
	 * @param className
	 *            子集class
	 * @param pk_psnorg
	 *            组织关系主键
	 * @param assgid
	 *            任职ID 主职=1 兼职>1 其他为null
	 * @return < T extends SuperVO > T
	 * @throws BusinessException
	 */
	public <T extends SuperVO> T getLastVO(Class<T> className, String pk_psnorg, Integer assgid) throws BusinessException;

	/**
	 * 根据人员基本信息主键pk_psndoc查找兼职记录的任职ID
	 * 
	 * @param pk_psndoc
	 *            人员基本信息主键
	 * @return int
	 * @throws BusinessException
	 */
	public int getMaxAssgidByPsndoc(String pk_psndoc) throws BusinessException;

	/**
	 * 通过老的pk_psnjob得到最新的pk_psnjob
	 * 
	 * @param oldPkpsnjob
	 *            就的工作记录主键
	 * @return String
	 * @throws BusinessException
	 */
	public String getNewPkpsnjob(String oldPkpsnjob) throws BusinessException;

	/**
	 * 根据人员基本信息主键pk_psndoc查找当前工作记录主键pk_psnjob
	 * 
	 * @param pk_psndoc
	 *            人员基本信息主键
	 * @return String
	 * @throws BusinessException
	 */
	public String getPsnjobByPsndoc(String pk_psndoc) throws BusinessException;

	/**
	 * 根据人员基本信息主键pk_psndoc查找当前组织关系主键pk_psnorg
	 * 
	 * @param pk_psndoc
	 *            人员基本信息主键
	 * @return String
	 * @throws BusinessException
	 */
	public String getPsnorgByPsndoc(String pk_psndoc) throws BusinessException;

	/**
	 * 根据人员基本信息主键pk_psndoc查找当前最新员工类型组织关系主键pk_psnorg
	 * 
	 * @param pk_psndoc
	 * @return String
	 * @throws BusinessException
	 */
	public String getEmpPsnorgByPsndoc(String pk_psndoc) throws BusinessException;
	
	/**
	 * 根据人员基本信息主键pk_psndoc查找当前员工所有的组织关系主键pk_psnorg
	 * 
	 * @param pk_psndoc
	 * @return String
	 * @throws BusinessException
	 */
	public HashMap<String, String[]> getAllPsnorgByPsndoc(String[] pk_psndocs) throws BusinessException;
	
	/**
	 * 根据人员基本信息主键pk_psndoc查找当前最新员工类型最新工作记录主键pk_psnjob
	 * 
	 * @param pk_psndoc
	 * @return String
	 * @throws BusinessException
	 */
	public String getEmpPsnjobByPsndoc(String pk_psndoc) throws BusinessException;

	/**
	 * 插入一条记录
	 * 
	 * @param <T>
	 * @param vo
	 *            待插入的记录
	 * @param infoset
	 *            该记录对应的信息集
	 * @return < T extends SuperVO > T
	 * @throws BusinessException
	 */
	public <T extends SuperVO> T insertRecord(T vo, InfoSetVO infoset) throws BusinessException;

	/**
	 * 同步bd_psndoc.pk_org
	 * 
	 * @param pk_org
	 *            组织PK
	 * @param pk_psndoc
	 *            人员基本信息主键
	 * @throws BusinessException
	 */
	public void synPkorgOfPsndoc(String pk_org, String pk_psndoc) throws BusinessException;

	/**
	 * 更新所有记录的recordnum与lastflag
	 * 
	 * @param vos
	 *            必须是以recordnum顺序排列 012345
	 * @throws BusinessException
	 */
	public void updateAllRecordnumAndLastflag(SuperVO[] vos) throws BusinessException;

	/**
	 * 以某一时间结束一条记录
	 * 
	 * @param vo
	 *            记录信息
	 * @param date
	 *            结束日期
	 * @return SuperVO
	 * @throws BusinessException
	 */
	public SuperVO updateEndflagAndEnddate(SuperVO vo, UFLiteralDate date) throws BusinessException;

	/**
	 * 更新一条记录
	 * 
	 * @param <T>
	 * @param vo
	 * @return < T extends SuperVO > T
	 * @throws BusinessException
	 */
	public <T extends SuperVO> T updateRecord(T vo) throws BusinessException;

	/**
	 * 更新历史记录的recordnum与lastflag
	 * 
	 * @param vos
	 *            历史记录VO数组
	 * @throws BusinessException
	 */
	public void updateRecordnumAndLastflag(SuperVO[] vos) throws BusinessException;

	/**
	 * 同步流动情况
	 * 
	 * @param psnjobVO
	 * @throws BusinessException
	 */
	public void addPsnChg(PsnJobVO psnjobVO, boolean isSyncPsndoc) throws BusinessException;

	/**
	 * 同步流动情况(转档时用)
	 * 
	 * @param psnjobVO
	 * @throws BusinessException
	 */
	public void addPsnChgWhenIntoDoc(PsnJobVO psnjobVO) throws BusinessException;

	/**
	 * 批量增加相关人员工作记录,用于部门合并、部门转移,所有记录的开始日期都是一样的
	 * 
	 * @param psnJobVO
	 * @throws BusinessException
	 */
	public void addPoiPsnjobs(PsnJobVO[] psnJobVO) throws BusinessException;

	/**
	 * 相关人员增加一条工作记录,用于部门合并
	 * 
	 * @param psnJobVO
	 * @throws BusinessException
	 */
	public void addPoiPsnjob(PsnJobVO psnJobVO) throws BusinessException;

	/**
	 * 保存兼职变更信息
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
	 * 同步psnorg中的pk_hrorg
	 * 
	 * @param pk_psnorg
	 * @param pk_hrorg
	 * @throws BusinessException
	 */
	public void synPkhrorgOfPsnorg(String pk_psnorg, String pk_hrorg) throws BusinessException;

	/**
	 * 检查日期
	 * 
	 * @param psnjobVO
	 *            当前组织关系下的最新工作记录
	 * @param jobDate
	 *            工作记录开始日期
	 * @param orgDate
	 *            组织关系开始日期
	 * @throws BusinessException
	 */
	public void checkBeginDate(PsnJobVO psnjobVO, UFLiteralDate jobDate, UFLiteralDate orgDate) throws BusinessException;

	public void checkClerkCodeUnique(PsnJobVO psnjobVO) throws BusinessException;

	public void checkDeptPostCanceled(PsnJobVO psnjobVO) throws BusinessException;

	/**
	 * 批量增加工作记录
	 * 
	 * @param array
	 * @param isSyncWork
	 * @param array2
	 */
	public void addNewPsnjobs(PsnJobVO[] psnjobs, boolean isSyncWork, String[] hrOrgs) throws BusinessException;

	public void checkPsnorg(String pk_psnjob) throws BusinessException;

	/**
	 * 同步人员薪资信息，采取完全覆盖的方式，即删除原有的，插入当前的
	 * 
	 * @param wainfo key 人员档案主键 value 薪资信息VO数组
	 * @throws BusinessException
	 */
	public void syncPsnWaInfo(Map<String, WainfoVO[]> wainfo) throws BusinessException;

	/**
	 * 同步人员社保信息，
	 * 
	 * @param bminfo key 人员档案主键 value 社保信息VO数组
	 * @throws BusinessException
	 */
	/**
	 * 同步人员社保信息，按照人员、险种 同步当前险种的信息
	 * 
	 * @param bminfo key 人员档案主键 value 社保信息VO ，
	 *            一人一个VO，如果当前人员没有该险种信息,则传null，删除其已经存在的信息
	 * @param pk_insurance 险种pk
	 * @throws BusinessException
	 */
	public void syncPsnBmInfo(Map<String, BminfoVO> bminfo, String pk_insurance) throws BusinessException;

	/**
	 * 同步人员能力素质信息
	 * 
	 * @param capainfo key 人员工作记录主键 value 能力素质信息VO数组
	 * @throws BusinessException
	 */
	public void syncPsnCapaInfo(Map<String, CapaVO[]> capainfo) throws BusinessException;

	/**
	 * 同步人员考核信息
	 * 
	 * @param peinfo key 人员工作记录主键 value 考核结果信息VO数组
	 * @throws BusinessException
	 */
	public void syncPsnPeInfo(Map<String, AssVO[]> peinfo) throws BusinessException;

	public void syncPsnQulifyInfo(BatchOperateVO batchVO) throws BusinessException;

	/**
	 * 方案解冻时，删除该方案关联的考核记录
	 * 
	 * @param pk_scheme 方案主键
	 * @throws BusinessException
	 */
	public void delPsnPeInfo(String pk_scheme) throws BusinessException;

	/**
	 * 自助信息审核后调用 处理子集的lastflag 和 recordnum;
	 * 
	 * @param map key 人员主键 value 变更的信息集主键数组
	 * @throws BusinessException
	 */
	public void updateRecordnumAndLastflagForHrss(Map<String, String[]> map) throws BusinessException;
}
