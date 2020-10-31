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
 * ��������Ϣά�� �ӿ�
 * 
 * @author: xuhw
 * @date: 2009-12-26 ����09:27:04
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public interface IPsndocwadocManageService {

	/**
	 * ���Ӷ�����ά����¼
	 * 
	 * @author xuhw on 2010-1-7
	 * @param psndocWadoc
	 * @return
	 * @throws BusinessException
	 */
	public String insertPsndocWadocVO(PsndocWadocVO psndocWadoc) throws BusinessException;

	/**
	 * �����ݿ��в���һ��VO����
	 */
	public String[] insertArray4BatchImport(PsndocWadocVO[] psndocWadocs) throws BusinessException;

	/**
	 * ɾ��������ά����¼
	 * 
	 * @author xuhw on 2010-1-7
	 * @param vo
	 * @throws BusinessException
	 */
	public void deleteByPsndocWadocVO(PsndocWadocVO vo) throws BusinessException;

	/**
	 * �޸Ķ�����ά����¼
	 * 
	 * @author xuhw on 2010-1-7
	 * @param psndocWadoc
	 * @throws BusinessException
	 */
	public void updatePsndocWadoc(PsndocWadocVO psndocWadoc) throws BusinessException;

	/**
	 * �����ݿ��в���һ��VO����
	 */
	public String[] insertArray(PsndocWadocVO[] psndocWadocs) throws BusinessException;

	/**
	 * ���������ݳ־û�
	 * 
	 * @author xuhw on 2010-4-20
	 * @param psndocWadocVOs
	 * @return
	 * @throws BusinessException
	 */
	public PsndocWadocVO[] importData2DB(PsndocWadocVO[] psndocWadocVOs, LoginContext context) throws BusinessException;

	/**
	 * �������ݵ�Excel<BR>
	 * ���ݲ�ѯ������ѯ�����з��Ϲ������Ա��������ԱPK�ҳ���Ӧ�����±�־Ϊtrue�Ķ����ʼ�¼<BR>
	 * 
	 * @author xuhw on 2010-5-21
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public PsndocWadocVO[] exportData2Excel(PsndocWadocMainVO[] vos) throws BusinessException;

	/**
	 * н���յ�<BR>
	 * 1:��������н�ʱ�׼����н�ʽ�����<BR>
	 * 
	 * 2:�����𵵱����������<BR>
	 * 
	 * 3:�û��Լ�����<BR>
	 * 
	 * <BR>
	 * 
	 * @param - adjustwadocvo - н���յ���Ҫ��Ϣ
	 */
	public AdjustWadocVO[] batchAdjust(BatchAdjustVO adjustwadocvo, AdjustWadocVO[] adjustWadocPsnInfoVOs)
			throws BusinessException;

	/**
	 * н���յ� �յ�ǰ����Աн����Ϣ����
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
	 * н���յ���ɣ��־û��յ�����
	 * 
	 * @param adjustWadocPsnInfoVOs
	 * @throws BusinessException
	 */
	public void insertArray4Adjust(AdjustWadocVO[] adjustWadocPsnInfoVOs, BatchAdjustVO batchAdjustVO)
			throws BusinessException;

	/**
	 * н���յ�������
	 * 
	 * @param adjustWadocPsnInfoVOs
	 * @throws BusinessException
	 */
	public String limitBatchAdjust(AdjustWadocVO[] adjustWadocPsnInfoVOs) throws BusinessException;

	/**
	 * ����ȡ��н�ʱ�׼����<BR>
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
	 * ���ݹ�����¼�����µĶ�����
	 * 
	 * @throws BusinessException
	 */
	void generateByPsnJob(PsnJobVO[] newPsnJobs) throws BusinessException;

	/**
	 * ��������ӛ������µİ�M
	 * 
	 * @param newPsnJob
	 * @throws BusinessException
	 */
	void generateTeamItem(PsnJobVO newPsnJob) throws BusinessException;

	/**
	 * ��������ӛ�ͬ�����ٰ�M�����Օ�
	 * 
	 * @param newPsnJob
	 * @throws BusinessException
	 */
	void sync2TeamCalendar(PsnJobVO newPsnJob) throws BusinessException;

	/**
	 * ���°�M
	 * 
	 * @param headVO
	 * @param itemVOs
	 * @throws BusinessException
	 */
	void updateShiftGroup(TeamHeadVO headVO, Collection<TeamItemVO> itemVOs) throws BusinessException;

	/**
	 * �˷�����춲��빤��ӛ䛵���r,ֻ��ͬ���������Ǘl����ӛ䛕r�g�ε��Ű�
	 * 
	 * @param insertPsnJob
	 * @throws BusinessException
	 */
	void generateTeamItemForInsertPsn(PsnJobVO insertPsnJob) throws BusinessException;

	/**
	 * ��ȡ����Աĳ���춯���Ϳ�ʼ���ڵ�ǰһ��
	 * 
	 * @param pk_org
	 * @param begindate
	 *            ��׼����(��������������)
	 * @param refTransType
	 *            �춯����
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	UFLiteralDate getTransTypeEndDate(String pk_org, UFLiteralDate begindate, String refTransType, String pk_psndoc)
			throws BusinessException;

}
