package nc.itf.hr.wa;

import java.util.Collection;
import java.util.List;

import nc.vo.bd.team.team01.entity.TeamHeadVO;
import nc.vo.bd.team.team01.entity.TeamItemVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.wadoc.PsndocWadocMainVO;
import nc.vo.hi.wadoc.PsndocWadocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.adjust.AdjustWadocVO;
import nc.vo.wa.adjust.BatchAdjustVO;

/**
 * 定调资信息维护 接口
 * 
 * @author: xuhw
 * @date: 2009-12-26 上午09:27:04
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public interface IPsndocwadocManageService {

	/**
	 * 增加定调资维护记录
	 * 
	 * @author xuhw on 2010-1-7
	 * @param psndocWadoc
	 * @return
	 * @throws BusinessException
	 */
	public String insertPsndocWadocVO(PsndocWadocVO psndocWadoc) throws BusinessException;

	/**
	 * 向数据库中插入一批VO对象。
	 */
	public String[] insertArray4BatchImport(PsndocWadocVO[] psndocWadocs) throws BusinessException;

	/**
	 * 删除定调资维护记录
	 * 
	 * @author xuhw on 2010-1-7
	 * @param vo
	 * @throws BusinessException
	 */
	public void deleteByPsndocWadocVO(PsndocWadocVO vo) throws BusinessException;

	/**
	 * 修改定调资维护记录
	 * 
	 * @author xuhw on 2010-1-7
	 * @param psndocWadoc
	 * @throws BusinessException
	 */
	public void updatePsndocWadoc(PsndocWadocVO psndocWadoc) throws BusinessException;

	/**
	 * 向数据库中插入一批VO对象。
	 */
	public String[] insertArray(PsndocWadocVO[] psndocWadocs) throws BusinessException;

	/**
	 * 定调资数据持久化
	 * 
	 * @author xuhw on 2010-4-20
	 * @param psndocWadocVOs
	 * @return
	 * @throws BusinessException
	 */
	public PsndocWadocVO[] importData2DB(PsndocWadocVO[] psndocWadocVOs, LoginContext context) throws BusinessException;

	/**
	 * 导出数据到Excel<BR>
	 * 根据查询条件查询出所有符合规则的人员，根据人员PK找出相应的最新标志为true的定调资记录<BR>
	 * 
	 * @author xuhw on 2010-5-21
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public PsndocWadocVO[] exportData2Excel(PsndocWadocMainVO[] vos) throws BusinessException;

	/**
	 * 薪资普调<BR>
	 * 1:“按最新薪资标准调整薪资金额”规则<BR>
	 * 
	 * 2:“级别档别调整”规则：<BR>
	 * 
	 * 3:用户自己调整<BR>
	 * 
	 * <BR>
	 * 
	 * @param - adjustwadocvo - 薪资普调主要信息
	 */
	public AdjustWadocVO[] batchAdjust(BatchAdjustVO adjustwadocvo, AdjustWadocVO[] adjustWadocPsnInfoVOs)
			throws BusinessException;

	/**
	 * 薪资普调 普调前后人员薪资信息处理
	 * 
	 * @param pkcorp
	 * @param queryStr
	 * @param tableCodes
	 * @param strItemPK
	 * @param strGrdPK
	 * @return
	 * @throws nc.vo.pub.BusinessException
	 */
	public abstract AdjustWadocVO[] queryWadocMainData4AdjustInfo(LoginContext loginContext, String queryStr,
			List<String> tableCodes, BatchAdjustVO batchadjustVO) throws nc.vo.pub.BusinessException;

	/**
	 * 薪资普调完成，持久化普调数据
	 * 
	 * @param adjustWadocPsnInfoVOs
	 * @throws BusinessException
	 */
	public void insertArray4Adjust(AdjustWadocVO[] adjustWadocPsnInfoVOs, BatchAdjustVO batchAdjustVO)
			throws BusinessException;

	/**
	 * 薪资普调金额调整
	 * 
	 * @param adjustWadocPsnInfoVOs
	 * @throws BusinessException
	 */
	public String limitBatchAdjust(AdjustWadocVO[] adjustWadocPsnInfoVOs) throws BusinessException;

	/**
	 * 批量取得薪资标准名称<BR>
	 * <BR>
	 * 
	 * @author xuhw on 2010-1-2
	 * @param strPkCrt
	 * @param ismultsec
	 * @return
	 * @throws BusinessException
	 */
	public PsndocWadocVO[] getCrtName(PsndocWadocVO[] PsndocWadocVOs) throws BusinessException;

	/**
	 * 根据工作记录生成新的定调资
	 * 
	 * @throws BusinessException
	 */
	void generateByPsnJob(PsnJobVO[] newPsnJobs) throws BusinessException;

	/**
	 * 根據工作記錄生成新的班組
	 * 
	 * @param newPsnJob
	 * @throws BusinessException
	 */
	void generateTeamItem(PsnJobVO newPsnJob) throws BusinessException;

	/**
	 * 根據工作記錄同步所屬班組工作日曆
	 * 
	 * @param newPsnJob
	 * @throws BusinessException
	 */
	void sync2TeamCalendar(PsnJobVO newPsnJob) throws BusinessException;

	/**
	 * 更新班組
	 * 
	 * @param headVO
	 * @param itemVOs
	 * @throws BusinessException
	 */
	void updateShiftGroup(TeamHeadVO headVO, Collection<TeamItemVO> itemVOs) throws BusinessException;

	/**
	 * 此方法用於插入工作記錄的情況,只會同步新增的那條工作記錄時間段的排班
	 * 
	 * @param insertPsnJob
	 * @throws BusinessException
	 */
	void generateTeamItemForInsertPsn(PsnJobVO insertPsnJob) throws BusinessException;

	/**
	 * 获取该人员某种异动类型开始日期的前一天
	 * 
	 * @param pk_org
	 * @param begindate
	 *            基准日期(离该日期最最近的)
	 * @param refTransType
	 *            异动类型
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	UFLiteralDate getTransTypeEndDate(String pk_org, UFLiteralDate begindate, String refTransType, String pk_psndoc)
			throws BusinessException;

}
