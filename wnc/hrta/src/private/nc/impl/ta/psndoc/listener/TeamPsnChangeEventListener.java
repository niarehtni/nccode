package nc.impl.ta.psndoc.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.businessevent.IEventType;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.itf.hi.IPsndocQryService;
import nc.itf.om.IAOSQueryService;
import nc.itf.ta.IMonthStatManageService;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.IPsnCalendarManageService;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.IDateScope;
import nc.itf.ta.algorithm.impl.DefaultDateScope;
import nc.pubitf.bd.team.team01.hr.ITeamQueryServiceForHR;
import nc.vo.bd.team.team01.entity.AggTeamVO;
import nc.vo.bd.team.team01.entity.TeamChangeEventVO;
import nc.vo.bd.team.team01.entity.TeamHeadVO;
import nc.vo.bd.team.team01.entity.TeamItemVO;
import nc.vo.bd.team.team01.enumeration.EventSourceForTeam;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hrta.tbmpsndoc.OvertimecontrolEunm;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psndoc.TBMPsndocCommonValue;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.psndoc.WorkWeekFormEnum;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * ������Ա�뿼�ڵ����Ĺ�ϵ:������Ա��Ч����,һ��Ҫ���ڿ��ڵ�����¼,�ҿ��ڵ�����pk_teamΪ��Ա�������� ������Ա��Ч����,�����п��ڵ���,Ҳ����û��
 * ���ڵ�����Ч����pk_team����,һ��Ҫ���ڰ�����Ա��¼,��pk_teamҪһ�� ���ڵ�����Ч������Ч����pk_team����,һ�������ڰ�����Ա��¼
 * 
 * ����������Ա�仯���¼�.���е���Ա����bd_psndoc�ļ�¼,û��hi_psnjob��hi_psnorg
 * ����ȷ��һ��:������Ա�仯Ӱ���HR��֯�ǰ�������ҵ��Ԫ������HR��֯ �����߼�:
 * ����������Ա:����֮��,������Ա�����ڷ�Χ����Ҫ��֤�п��ڵ�����¼,�Ҽ�¼�ϵ�pk_team�ֶ�Ҫ����(���ܻᵼ��һ�����ڵ��������)
 * �޸İ�����Ա�Ŀ�ʼ/��������: �Ƚ��޸�ǰ���޸ĺ�����ڷ�Χ:ǰ���е���,���ڵ�������;��������,Ҫ��֤�п��ڵ���,��pk_teamΪ��Ա�İ���;
 * ɾ������,���ڵ�����ɾ��,��pk_teamҪ����Ϊnull ɾ��������Ա:
 * �ҳ����ڵ�����,��˰�����Ա�����ڽ����ļ�¼,��������ڷ�Χ�ڵ�pk_team����Ϊnull(���ܻᵼ��һ�����ڵ��������)
 * 
 * ������Ա�Ϳ��ڵ���Ӧ����ͬ���Ĺ�ϵ,��������Ա�Ĳ���ҪӰ�쿼�ڵ���,���ڵ����Ĳ���ҪӰ�������Ա ���ڵ����԰�����Ա��Ӱ��(������һ���������):
 * �������ڵ���:��������ֶ�Ϊ��,���������ڵ��������ڷ�Χ��,�����а�����Ա�ļ�¼.������,�����ڷ�Χ��,������
 * ������Ա�ļ�¼,�Ұ�����Ա��������Ϊ���ڵ����İ��� �޸Ŀ��ڵ���:����޸�ǰ�����ڷ�Χ�������ֶζ�δ�䣬�����κδ������иı���
 * ���޸ĺ�����ֶ�Ϊ�գ���Ҫ��֤�޸ĺ�Ŀ��ڵ������ڷ�Χ���ް�����Ա��¼��
 * ���޸ĺ�����ֶβ��գ���Ҫ��֤�޸ĺ�Ŀ��ڵ������ڷ�Χ���ж�Ӧpk_team�İ�����Ա��¼ ɾ�����ڵ���:ɾ���Ŀ��ڵ����ķ�Χ��,�����а�����Ա�ļ�¼
 * 
 * @author zengcheng
 * 
 */
public class TeamPsnChangeEventListener implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		BusinessEvent be = (BusinessEvent) event;
		Object obj = be.getObject();
		if (obj == null) {
			return;
		}
		// ����仯ʱ���䶯����Ա��¼
		if (!(obj instanceof TeamChangeEventVO))
			return;
		TeamChangeEventVO evtVO = (TeamChangeEventVO) obj;
		// ����¼���HR�����ģ��ڴ˲���в��ٴ���
		if (EventSourceForTeam.HR.equals(evtVO.getEventsource()))
			return;
		TeamItemVO[] deleteItems = evtVO.getDeleteitemvos();
		if (IEventType.TYPE_DELETE_BEFORE.equals(event.getEventType())) {
			AggTeamVO[] aggvos = evtVO.getAggvos();
			if (ArrayUtils.isEmpty(aggvos))
				return;
			List<TeamItemVO> itemList = new ArrayList<TeamItemVO>();
			for (AggTeamVO aggvo : aggvos)
				CollectionUtils.addAll(itemList, aggvo.getChildrenVO());
			if (CollectionUtils.isEmpty(itemList))
				return;
			deleteItems = itemList.toArray(new TeamItemVO[0]);
		}
		// �õ����е��������޸�ǰ���޸ĺ�ɾ���İ�����Ա��¼���д���
		handleTeamPsnChange(evtVO.getAdditemvos(), evtVO.getOlditemvos(), evtVO.getNewitemvos(), deleteItems);
	}

	/**
	 * ���ڵ���ͬ��������Ա
	 * 
	 * @param insertTeamPsns
	 * @param oldupdTeamPsns
	 * @param newupdTeamPsns
	 * @param deleteTeamPsns
	 * @throws BusinessException
	 */
	public void handleTeamPsnChange(TeamItemVO[] insertTeamPsns, TeamItemVO[] oldupdTeamPsns,
			TeamItemVO[] newupdTeamPsns, TeamItemVO[] deleteTeamPsns) throws BusinessException {
		// ����������ȫ�������ݣ�û�б仯����Աû�б�Ҫ�ٴ���һ��(�������Ű�ܺ�ʱ)���ڴ˹��˵�
		if (!ArrayUtils.isEmpty(oldupdTeamPsns) && !ArrayUtils.isEmpty(newupdTeamPsns)) {
			for (TeamItemVO old : oldupdTeamPsns) {
				for (TeamItemVO ne : newupdTeamPsns) {
					if (old.getCworkmanid().equals(ne.getCworkmanid())
							&& old.getDstartdate().isSameDate(ne.getDstartdate())
							&& old.getDenddate().isSameDate(ne.getDenddate())) {
						oldupdTeamPsns = (TeamItemVO[]) ArrayUtils.removeElement(oldupdTeamPsns, old);
						newupdTeamPsns = (TeamItemVO[]) ArrayUtils.removeElement(newupdTeamPsns, ne);
					}
				}
			}
		}
		Map<String, TeamItemVO[]> insertTeamPsnMap = CommonUtils.group2ArrayByField(TeamItemVO.CWORKMANID,
				insertTeamPsns);
		Map<String, TeamItemVO[]> oldupdTeamPsnMap = CommonUtils.group2ArrayByField(TeamItemVO.CWORKMANID,
				oldupdTeamPsns);
		Map<String, TeamItemVO[]> newupdTeamPsnMap = CommonUtils.group2ArrayByField(TeamItemVO.CWORKMANID,
				newupdTeamPsns);
		Map<String, TeamItemVO[]> deleteTeamPsnMap = CommonUtils.group2ArrayByField(TeamItemVO.CWORKMANID,
				deleteTeamPsns);

		// ���м�¼
		TeamItemVO[] allTeamPsns = (TeamItemVO[]) ArrayUtils.addAll(ArrayUtils.addAll(insertTeamPsns, oldupdTeamPsns),
				ArrayUtils.addAll(newupdTeamPsns, deleteTeamPsns));
		if (ArrayUtils.isEmpty(allTeamPsns))
			return;
		String pk_team = allTeamPsns[0].getCteamid();
		String pk_org = allTeamPsns[0].getPk_org();
		String pk_group = allTeamPsns[0].getPk_group();
		// ������ TeamItemVO�о�Ȼû��pk_org��ֵ
		if (StringUtils.isBlank(pk_org)) {
			TeamHeadVO[] teamvos = NCLocator.getInstance().lookup(ITeamQueryServiceForHR.class)
					.queryBZbyPK(new String[] { pk_team });
			if (!ArrayUtils.isEmpty(teamvos)) {
				pk_org = teamvos[0].getPk_org();
				pk_group = teamvos[0].getPk_group();
			}
		}
		Set<String> psndocSet = new HashSet<String>();
		Map<String, List<String>> psnJobMap = new HashMap<String, List<String>>();
		List<String> allJobList = new ArrayList<String>();
		UFLiteralDate allBegindate = allTeamPsns[0].getDstartdate();
		UFLiteralDate allEnddate = allTeamPsns[0].getDenddate() == null ? allBegindate : allTeamPsns[0].getDenddate();
		for (TeamItemVO teamPsn : allTeamPsns) {
			psndocSet.add(teamPsn.getCworkmanid());
			if (CollectionUtils.isEmpty(psnJobMap.get(teamPsn.getCworkmanid()))) {
				List<String> jobList = new ArrayList<String>();
				jobList.add(teamPsn.getPk_psnjob());
				psnJobMap.put(teamPsn.getCworkmanid(), jobList);
			} else {
				psnJobMap.get(teamPsn.getCworkmanid()).add(teamPsn.getPk_psnjob());
			}
			allJobList.add(teamPsn.getPk_psnjob());

			if (teamPsn.getDstartdate().before(allBegindate)) {
				allBegindate = teamPsn.getDstartdate();
			}
			if (teamPsn.getDenddate() != null && teamPsn.getDenddate().after(allEnddate)) {
				allEnddate = teamPsn.getDenddate();
			}
		}

		ITBMPsndocQueryService psndocQueryService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		IPsndocQryService psnjobQueryService = NCLocator.getInstance().lookup(IPsndocQryService.class);
		OrgVO orgvo = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org);
		if (orgvo == null)
			return;
		String pk_hrorg = orgvo.getPk_org();

		if (CollectionUtils.isEmpty(allJobList)) {
			throw new BusinessException("���鶨�崫��ʱ��İ�����Ա�Ĺ�����¼Ϊ�գ�");
		}
		PsnJobVO[] allJobVOs = psnjobQueryService.queryPsnjobByPKs(allJobList.toArray(new String[0]));
		Map<String, List<PsnJobVO>> psnJobVOMap = new HashMap<String, List<PsnJobVO>>();
		for (PsnJobVO jobvo : allJobVOs) {
			List<PsnJobVO> jobVOList = psnJobVOMap.get(jobvo.getPk_psndoc());
			if (null == jobVOList || CollectionUtils.isEmpty(jobVOList)) {
				jobVOList = new ArrayList<PsnJobVO>();
				psnJobVOMap.put(jobvo.getPk_psndoc(), jobVOList);
			}
			jobVOList.add(jobvo);
		}

		Map<String, List<TBMPsndocVO>> tbmpsndocMap = psndocQueryService.queryTBMPsndocMapByPsndocs(null,
				psndocSet.toArray(new String[0]), allBegindate, allEnddate, false);
		// ��Աû�п��ڵ���ʱ�Զ���ӿ��ڵ��������ֱ�ӷ�����������������ά����ӽ�һ��������
		// if(MapUtils.isEmpty(tbmpsndocMap)){
		// return;
		// }

		List<TBMPsndocVO> haveTeamPsndocs = new ArrayList<TBMPsndocVO>();
		List<TBMPsndocVO> noneTeamPsndocs = new ArrayList<TBMPsndocVO>();
		List<TBMPsndocVO> oldTeamPsndocs = new ArrayList<TBMPsndocVO>();
		List<TBMPsndocVO> insertPsndocs = new ArrayList<TBMPsndocVO>();
		Map<String, PsnJobDateScope[]> jobMap = new HashMap<String, PsnJobDateScope[]>();
		// ����ѭ������
		for (String pk_psndoc : psndocSet) {
			List<IDateScope>[] teamPsnDateScope = formatTeamPsnToDatescope(insertTeamPsnMap == null ? null
					: insertTeamPsnMap.get(pk_psndoc),
					oldupdTeamPsnMap == null ? null : oldupdTeamPsnMap.get(pk_psndoc), newupdTeamPsnMap == null ? null
							: newupdTeamPsnMap.get(pk_psndoc),
					deleteTeamPsnMap == null ? null : deleteTeamPsnMap.get(pk_psndoc));
			// ������Ա�仯�󻹴����Լ����������ڶκͲ��ٴ��ڵ����ڶ�
			List<IDateScope> existDateScope = new ArrayList<IDateScope>();
			List<IDateScope> notExistDateScope = new ArrayList<IDateScope>();
			calculateTeamPsnDatescope(teamPsnDateScope, existDateScope, notExistDateScope);
			// ��Ա�仯�Ŀ�ʼ���ڡ���������
			UFLiteralDate[] dates = getBeginAndEnddate(existDateScope, notExistDateScope);
			UFLiteralDate begindate = dates[0];
			UFLiteralDate enddate = dates[1];
			// ���ݿ�ʼ�������ڲ�ѯ���ڵ����͹�����¼
			// ��Ϊ����ְ�����⣬���ڵ����Ͱ��������ͬ������֯���ܲ�ͬ�ˣ���˲�ѯ���ڵ�����ʱ������pk_hrorg������
			// List<TBMPsndocVO> psndocVOs =
			// psndocQueryService.queryTBMPsndocListByPsndoc(pk_hrorg,
			// pk_psndoc, begindate, enddate, false);
			// List<TBMPsndocVO> psndocVOs =
			// psndocQueryService.queryTBMPsndocListByPsndoc(null, pk_psndoc,
			// begindate, enddate, false);
			List<TBMPsndocVO> psndocVOs = (tbmpsndocMap == null ? null : tbmpsndocMap.get(pk_psndoc));
			// String[] pk_jobs = psnJobMap.get(pk_psndoc).toArray(new
			// String[0]);
			// PsnJobVO[] psnjobVOs =
			// psnjobQueryService.queryPsnjobVOByOrgAndDate(pk_org, pk_psndoc,
			// begindate, enddate);
			// PsnJobVO[] psnjobVOs =
			// psnjobQueryService.queryPsnjobByPKs(pk_jobs);
			List<PsnJobVO> list = psnJobVOMap.get(pk_psndoc);
			if (CollectionUtils.isEmpty(list)) {
				throw new BusinessException(CommonUtils.getPsnName(pk_psndoc)
						+ ResHelper.getString("6017psndoc", "06017psndoc0117")
				/* @res "�����ڷ�Χ��û����ְ��¼��" */);
			}
			PsnJobVO[] psnjobVOs = list.toArray(new PsnJobVO[0]);
			PsnJobDateScope[] psnjobScopes = formatPsnjobDateScope(psnjobVOs, begindate, enddate);
			jobMap.put(pk_psndoc, psnjobScopes);
			// ��ǰ��Ա���޸ĵĺ�����Ŀ��ڵ���
			List<TBMPsndocVO> haveTeamPsndocList = new ArrayList<TBMPsndocVO>();
			// ѭ���������п��ڵ������뻹���ڵ����ڶ��ཻ�������޸���������Ϊpk_team���벻�ٴ��ڵ����ڶ��ཻ�������޸���������Ϊnull,�����ཻ�Ĳ���ʹ��ԭ����
			// �˴������ʱ��ûȷ����֯�Ƿ�һ�£��п��ܵ����������ڵ�������֯�Ͱ������֯��һ�£�pk_jobҲ���ܲ�һ�£���insert��ʱ��һ�鴦��pk_org��
			calculateUpdatePsndocs(psndocVOs, existDateScope, notExistDateScope, haveTeamPsndocList, noneTeamPsndocs,
					oldTeamPsndocs);
			haveTeamPsndocs.addAll(haveTeamPsndocList);
			// ��������Ա������ʱ��ȥ���뿼�ڵ����ཻ���֣�ʣ������ڶ��빤����¼�ཻ�����µĿ��ڵ�������Щ���ڵ����Ĺ�����¼ʹ���ཻ�Ĺ�����¼
			calculateInsertPsndocs(psnjobScopes, existDateScope.toArray(new DefaultDateScope[0]),
					haveTeamPsndocList.toArray(new IDateScope[0]), insertPsndocs);
		}
		// �����ڵ���
		List<TBMPsndocVO> updatePsndocs = new ArrayList<TBMPsndocVO>();
		processPsndocList(pk_team, pk_group, pk_hrorg, haveTeamPsndocs, noneTeamPsndocs, oldTeamPsndocs, insertPsndocs,
				updatePsndocs);

		// calculateUpdatePsndocs�д����Ŀ��ڵ���û�п���pk_org��pk_job�Ƿ��pk_hrorg��psnjobһ��
		TBMPsndocVO[] insertVOs = insertPsndocs.toArray(new TBMPsndocVO[0]);
		TBMPsndocVO[] updateVOs = updatePsndocs.toArray(new TBMPsndocVO[0]);
		ensureJob(pk_hrorg, updateVOs, jobMap, allTeamPsns);
		ensureJob(pk_hrorg, insertVOs, jobMap, allTeamPsns);
		// ����
		updatePsndocVOs(pk_hrorg, updateVOs);
		insertPsndocVOs(pk_hrorg, insertVOs);
		// ������Զ��Ű�
		List<TBMPsndocVO> allPsndocs = new ArrayList<TBMPsndocVO>();
		if (!ArrayUtils.isEmpty(updateVOs)) {// ���ް������������Ű�
			for (TBMPsndocVO vo : updateVOs) {
				if (StringUtils.isNotBlank(vo.getPk_team())) {
					allPsndocs.add(vo);
				}
			}
		}
		// allPsndocs.addAll(updatePsndocs);
		allPsndocs.addAll(insertPsndocs);
		if (CollectionUtils.isEmpty(allPsndocs))
			return;

		NCLocator.getInstance().lookup(IPsnCalendarManageService.class)
				.autoArrange(pk_hrorg, getPKFormPsndocVOs(allPsndocs.toArray(new TBMPsndocVO[0])));
	}

	public void ensureJob(String pk_hrorg, TBMPsndocVO[] tbmpsnvos, Map<String, PsnJobDateScope[]> jobMap,
			TeamItemVO[] allTeamPsns) {
		if (ArrayUtils.isEmpty(tbmpsnvos) || MapUtils.isEmpty(jobMap))
			return;
		for (TBMPsndocVO vo : tbmpsnvos) {
			PsnJobDateScope[] psnJobDateScopes = jobMap.get(vo.getPk_psndoc());
			// ԭ���ڵ�����A��֯��B��֯�������һ��ʱ�䣬������������֮��Ŀ��ڵ���������B��֯��
			if (vo.getEnddate().toString().equals(TBMPsndocCommonValue.END_DATA)) {
				// ������֯�ĸ��£���Ĭ�ϵĹ�����֯���ܸ���
				// if(StringUtils.isBlank(vo.getPk_adminorg())
				// ||StringUtils.isBlank(vo.getPk_org())
				// ||vo.getPk_adminorg().equals(vo.getPk_org())){
				// vo.setPk_adminorg(pk_hrorg);
				// }
				// ��������Դ��֯�����˱仯����տ��ڿ��źͿ��ڵص�
				if (!vo.getPk_org().equals(pk_hrorg)) {
					vo.setTimecardid(null);
					vo.setSecondcardid(null);
					vo.setPk_place(null);
					vo.setPk_adminorg(pk_hrorg);
				}
				vo.setPk_org(pk_hrorg);// ��֯ͬ��
				for (PsnJobDateScope job : psnJobDateScopes) {
					if (DateScopeUtils.contains(job, vo)) {// ȷ��pk_jobһ��
						vo.setPk_psnjob(job.getPk_psnjob());
						vo.setPk_psnorg(job.getPk_psnorg());
					}
				}
				continue;
			}
			for (TeamItemVO teamitem : allTeamPsns) {
				// ���ڵ���Ҫ�Ͱ���ͬ����ʱ��� 2012-07-11 ��Ϊ���ڵ���ʱ���ٰ���ʱ����ڵĶ���Ҫͬ��
				// if(teamitem.getDstartdate().toString().equals(vo.getBegindate().toString())&&
				// ((teamitem.getDenddate()!=null&&teamitem.getDenddate().toString().equals(vo.getEnddate().toString()))||
				// teamitem.getDenddate()==null&&vo.getEnddate().toString().equals(TBMPsndocCommonValue.END_DATA))){
				if (!teamitem.getDstartdate().after(vo.getBegindate())
						&& (teamitem.getDenddate() == null || !teamitem.getDenddate().before(vo.getEnddate()))) {
					// ������֯�ĸ��£���Ĭ�ϵĹ�����֯���ܸ���
					// if(StringUtils.isBlank(vo.getPk_adminorg())
					// ||StringUtils.isBlank(vo.getPk_org())
					// ||vo.getPk_adminorg().equals(vo.getPk_org())){
					// vo.setPk_adminorg(pk_hrorg);
					// }
					// ��������Դ��֯�����˱仯����տ��ڿ��źͿ��ڵص�
					if (!vo.getPk_org().equals(pk_hrorg)) {
						vo.setTimecardid(null);
						vo.setSecondcardid(null);
						vo.setPk_place(null);
						vo.setPk_adminorg(pk_hrorg);
					}
					vo.setPk_org(pk_hrorg);// ��֯ͬ��
					for (PsnJobDateScope job : psnJobDateScopes) {
						if (DateScopeUtils.contains(job, vo)) {// ȷ��pk_jobһ��
							vo.setPk_psnjob(job.getPk_psnjob());
							vo.setPk_psnorg(job.getPk_psnorg());
						}
					}
				}
			}

			// //��ԭ����A��֯�İ����У�������ӵ�b��֯�İ����У�a��֯�Ŀ��ڵ����������ˣ�����jobMap�д洢����b��֯�Ĺ�����¼��a��֯�Ŀ��ڵ����ǲ��ܸ������ݵ�
			// if(vo.getEnddate()!=null&&!vo.getEnddate().toString().equals(TBMPsndocCommonValue.END_DATA)&&!vo.getPk_org().equals(pk_hrorg))
			// continue;
			// //������֯�ĸ��£���Ĭ�ϵĹ�����֯���ܸ���
			// if(StringUtils.isBlank(vo.getPk_adminorg())
			// ||StringUtils.isBlank(vo.getPk_org())
			// ||vo.getPk_adminorg().equals(vo.getPk_org())){
			// vo.setPk_adminorg(pk_hrorg);
			// }
			// vo.setPk_org(pk_hrorg);//��֯ͬ��
			// PsnJobDateScope[] psnJobDateScopes =
			// jobMap.get(vo.getPk_psndoc());
			// for(PsnJobDateScope job:psnJobDateScopes){
			// if(DateScopeUtils.contains(job, vo)){//ȷ��pk_jobһ��
			// vo.setPk_psnjob(job.getPk_psnjob());
			// vo.setPk_psnorg(job.getPk_psnorg());
			// }
			// }
		}
	}

	/**
	 * ����
	 * 
	 * @param pk_hrorg
	 * @param insertVOs
	 * @throws BusinessException
	 */
	private void insertPsndocVOs(String pk_hrorg, TBMPsndocVO[] insertVOs) throws BusinessException {
		if (ArrayUtils.isEmpty(insertVOs))
			return;
		// У�鿼���ڼ�
		NCLocator.getInstance().lookup(IPeriodQueryService.class).checkBeforeInsertTBMPsndoc(pk_hrorg, insertVOs);
		// ����
		new BaseDAO().insertVOArray(insertVOs);
		// �����±���ʼ��
		NCLocator.getInstance().lookup(IMonthStatManageService.class).processAfterInsertTBMPsndoc(pk_hrorg, insertVOs);
	}

	/**
	 * �޸�
	 * 
	 * @param pk_hrorg
	 * @param updateVOs
	 * @return
	 * @throws BusinessException
	 */
	private void updatePsndocVOs(String pk_hrorg, TBMPsndocVO[] updateVOs) throws BusinessException {
		if (ArrayUtils.isEmpty(updateVOs))
			return;
		IPsnCalendarManageService psncalendarService = NCLocator.getInstance().lookup(IPsnCalendarManageService.class);
		psncalendarService.processBeforeUpdateTBMPsndoc(pk_hrorg, updateVOs);
		// У�鿼���ڼ�
		NCLocator.getInstance().lookup(IPeriodQueryService.class).checkBeforeUpdateTBMPsndoc(pk_hrorg, updateVOs);
		// �����±���ʼ��
		NCLocator.getInstance().lookup(IMonthStatManageService.class).processBeforeUpdateTBMPsndoc(pk_hrorg, updateVOs);
		new BaseDAO().updateVOArray(updateVOs);
	}

	/**
	 * ȡ���ڵ����������� ���˵��ظ�������
	 * 
	 * @param vos
	 * @return
	 */
	private String[] getPKFormPsndocVOs(TBMPsndocVO[] vos) {
		String[] psndocs = SQLHelper.getStrArray(vos, TBMPsndocVO.PK_PSNDOC);
		Set<String> set = new HashSet<String>();
		CollectionUtils.addAll(set, psndocs);
		return set.toArray(new String[0]);
	}

	/**
	 * ���������Ŀ��ڵ�����Ϣ
	 * 
	 * @param pk_team
	 * @param pk_group
	 * @param pk_hrorg
	 * @param haveTeamPsndocs
	 * @param noneTeamPsndocs
	 * @param insertPsndocs
	 */
	private void processPsndocList(String pk_team, String pk_group, String pk_hrorg, List<TBMPsndocVO> haveTeamPsndocs,
			List<TBMPsndocVO> noneTeamPsndocs, List<TBMPsndocVO> oldTeamPsndocs, List<TBMPsndocVO> insertPsndocs,
			List<TBMPsndocVO> updatePsndocs) {
		// �������Ŀ��ڵ���
		if (!CollectionUtils.isEmpty(insertPsndocs)) {
			for (TBMPsndocVO psndocVO : insertPsndocs) {
				psndocVO.setPk_team(pk_team);
				psndocVO.setPk_group(pk_group);
				psndocVO.setPk_org(pk_hrorg);
				psndocVO.setPk_adminorg(pk_hrorg);
				psndocVO.setCreator(PubEnv.getPk_user());
				psndocVO.setCreationtime(PubEnv.getServerTime());
			}
		}
		// ���ڰ���Ŀ��ڵ���
		if (!CollectionUtils.isEmpty(haveTeamPsndocs)) {
			for (TBMPsndocVO psndocVO : haveTeamPsndocs) {
				psndocVO.setPk_team(pk_team);
				// ���ڵ�������֯Ҫ�Ͱ���������������Դ��֯����һ��
				psndocVO.setPk_group(pk_group);
				// ����������֯�������֯ҲҪ�ı�
				if (!psndocVO.getPk_org().equals(pk_hrorg)) {
					psndocVO.setPk_adminorg(pk_hrorg);
					// ��������֯���ڿ��š����ڵص㶼���
					psndocVO.setTimecardid(null);
					psndocVO.setSecondcardid(null);
					psndocVO.setPk_place(null);
				}
				psndocVO.setPk_org(pk_hrorg);
				setValueToList(psndocVO, insertPsndocs, updatePsndocs);
			}
		}
		// �����ÿյĿ��ڵ���
		if (!CollectionUtils.isEmpty(noneTeamPsndocs)) {
			for (TBMPsndocVO psndocVO : noneTeamPsndocs) {
				psndocVO.setPk_team(null);
				setValueToList(psndocVO, insertPsndocs, updatePsndocs);
			}
		}
		// �����ÿյĿ��ڵ���
		if (!CollectionUtils.isEmpty(oldTeamPsndocs)) {
			for (TBMPsndocVO psndocVO : oldTeamPsndocs)
				setValueToList(psndocVO, insertPsndocs, updatePsndocs);
		}
	}

	/**
	 * �����ڵ������뵽�����б� ����ڸ����б����Ѵ��ڴ˿��ڵ���,���������ÿղ��ŵ������б���
	 * ��Ϊ�����ڶν���ʱ���ܽ�һ��ԭ���ڵ������гɶ�������µĿ��ڵ���,�����ָ���,����ֻ���������κ�һ������,�������ʹ�������ķ�ʽ����
	 * 
	 * @param psndocVO
	 * @param insertPsndocs
	 * @param updatePsndocs
	 */
	private void setValueToList(TBMPsndocVO psndocVO, List<TBMPsndocVO> insertPsndocs, List<TBMPsndocVO> updatePsndocs) {
		if (updatePsndocs.contains(psndocVO)) {
			// ��ͬ�˵Ŀ��ڵ�������ʼ�������״̬Ϊ�޸ģ����״̬Ϊ����
			TBMPsndocVO tmpVO = updatePsndocs.get(updatePsndocs.indexOf(psndocVO));
			if (tmpVO.getBegindate().after(psndocVO.getBegindate())) {
				updatePsndocs.remove(tmpVO);
				updatePsndocs.add(psndocVO);
				tmpVO.setPk_tbm_psndoc(null);
				insertPsndocs.add(tmpVO);
			} else {
				psndocVO.setPk_tbm_psndoc(null);
				insertPsndocs.add(psndocVO);
			}
			return;
		}
		updatePsndocs.add(psndocVO);
	}

	/**
	 * ʹ�ù�����¼��Ϣ�������ڵ��� ��������Ա������ʱ��ȥ�����Ѹ��¿��ڵ������ཻ���� ʣ�����ڶ��빤����¼�Ľ�����ΪҪ�����Ŀ��ڵ���
	 * ��Щ���ڵ���ʹ�������ཻ�Ĺ�����¼������
	 * 
	 * @param psnjobScopes
	 * @param existDateScopes
	 * @param updatedPsndocs
	 * @param insertPsndocs
	 */
	private void calculateInsertPsndocs(PsnJobDateScope[] psnjobScopes, IDateScope[] existDateScopes,
			IDateScope[] updatedPsndocs, List<TBMPsndocVO> insertPsndocs) {
		IDateScope[] lastScope = DateScopeUtils.minusDateScopes(existDateScopes, updatedPsndocs);
		IDateScope[] insertScope = DateScopeUtils.intersectionDateScopes(psnjobScopes, lastScope);
		if (ArrayUtils.isEmpty(insertScope))
			return;
		// ��������¼���ڶ�ѭ������
		for (PsnJobDateScope psnjobScope : psnjobScopes) {
			TBMPsndocVO psndocVO = new TBMPsndocVO();
			psndocVO.setPk_psndoc(psnjobScope.getPk_psndoc());
			psndocVO.setPk_psnjob(psnjobScope.getPk_psnjob());
			psndocVO.setPk_psnorg(psnjobScope.getPk_psnorg());
			psndocVO.setTbm_prop(TBMPsndocVO.TBM_PROP_MACHINE);

			// MOD(PM26104)�����ɵĿ��ڵ����Զ�����Ĭ�ϵļӰ�ܿ�ģʽ����ʱ��̬
			// ssx added on 2019-06-14
			psndocVO.setOvertimecontrol(OvertimecontrolEunm.MANUAL_CHECK.toIntValue());
			psndocVO.setWeekform(WorkWeekFormEnum.ONEWEEK.toIntValue());
			// end MOD

			// ȡ��ǰ������¼���ڶ�����������ڶν���,�ù�����¼��Ϣ���������psndocVO
			IDateScope[] finalScope = DateScopeUtils.intersectionDateScopes(new IDateScope[] { psnjobScope },
					insertScope);
			createTBMPsndocVO(psndocVO, finalScope, insertPsndocs);
		}
	}

	/**
	 * ����Ҫ�޸ĵĿ��ڵ�����¼ ������ڵ����������Ա�仯�����ڶ��н���������Щ���ڵ��������޸� ��ʱ������pk_team�����������ݻ��ܺ�ͬʱ����
	 * 
	 * @param psndocVOs
	 *            ���ڵ�����¼
	 * @param existDateScope
	 * @param notExistDateScope
	 * @param haveTeamPsndocs
	 *            ʹ���°���Ŀ��ڵ���
	 * @param noneTeamPsndocs
	 *            �����ÿյĿ��ڵ���
	 * @param oldTeamPsndocs
	 *            ʹ��ԭ����Ŀ��ڵ������˲��ֿ��ڵ�������Ϊ�������Ա�仯���ڶ��޽��������Բ������仯��
	 *            ����ʼ���ںͽ������ڿ�����Ϊ���ж������仯��
	 */
	private void calculateUpdatePsndocs(List<TBMPsndocVO> psndocVOs, List<IDateScope> existDateScope,
			List<IDateScope> notExistDateScope, List<TBMPsndocVO> haveTeamPsndocs, List<TBMPsndocVO> noneTeamPsndocs,
			List<TBMPsndocVO> oldTeamPsndocs) {
		if (CollectionUtils.isEmpty(psndocVOs))
			return;
		DefaultDateScope[] existDateScopes = existDateScope.toArray(new DefaultDateScope[0]);
		DefaultDateScope[] notExistDateScopes = notExistDateScope.toArray(new DefaultDateScope[0]);
		for (TBMPsndocVO psndocVO : psndocVOs) {
			IDateScope[] psndocScope = new IDateScope[] { psndocVO };
			// �������Ա���ڵ����ڶ��ص��Ĵ���
			if (!ArrayUtils.isEmpty(existDateScopes) && DateScopeUtils.isCross(existDateScopes, psndocScope)) {
				// ȡ���ص����ֹ��쿼�ڵ���
				IDateScope[] crossScopes = DateScopeUtils.intersectionDateScopes(existDateScopes, psndocScope);
				// �˴�ʹ��ԭ���Ŀ��ڵ��������µĿ��ڵ�����û������֯�Ƿ����仯��Ҳû�п���pk_job�Ƿ�һ��
				createTBMPsndocVO(psndocVO, crossScopes, haveTeamPsndocs);
				// ���ڵ������ڶ�ȥ���ص�����
				psndocScope = DateScopeUtils.minusDateScopes(psndocScope, existDateScopes);
			}
			// �������Ա���ٴ��ڵ����ڶ��ص��Ĵ���
			if (!ArrayUtils.isEmpty(notExistDateScopes) && psndocScope != null
					&& DateScopeUtils.isCross(notExistDateScopes, psndocScope)) {
				// ȡ���ص����ֹ��쿼�ڵ���
				IDateScope[] crossScopes = DateScopeUtils.intersectionDateScopes(notExistDateScopes, psndocScope);
				createTBMPsndocVO(psndocVO, crossScopes, noneTeamPsndocs);
				// ���ڵ������ڶ�ȥ���ص�����
				psndocScope = DateScopeUtils.minusDateScopes(psndocScope, notExistDateScopes);
			}
			createTBMPsndocVO(psndocVO, psndocScope, oldTeamPsndocs);
		}
	}

	/**
	 * �������ڶ��������ɿ��ڵ���
	 * 
	 * @param psndocVO
	 * @param scopes
	 * @param psndocList
	 */
	private void createTBMPsndocVO(TBMPsndocVO psndocVO, IDateScope[] scopes, List<TBMPsndocVO> psndocList) {
		if (ArrayUtils.isEmpty(scopes))
			return;
		for (IDateScope scope : scopes) {
			TBMPsndocVO tmpPsndoc = (TBMPsndocVO) psndocVO.clone();
			tmpPsndoc.setBegindate(scope.getBegindate());
			tmpPsndoc.setEnddate(scope.getEnddate());
			psndocList.add(tmpPsndoc);
		}
	}

	/**
	 * ȡ���ڶ�������翪ʼ���ں������������
	 * 
	 * @param dateScope1
	 * @param dateScope2
	 * @return [0]����ʼ���� [1]����������
	 */
	private UFLiteralDate[] getBeginAndEnddate(List<IDateScope> dateScope1, List<IDateScope> dateScope2) {
		UFLiteralDate[] dates = new UFLiteralDate[2];
		for (IDateScope dateScope : dateScope1) {
			if (dates[0] == null || dates[0].after(dateScope.getBegindate()))
				dates[0] = dateScope.getBegindate();
			if (dates[1] == null || dates[1].before(dateScope.getEnddate()))
				dates[1] = dateScope.getEnddate();
		}
		for (IDateScope dateScope : dateScope2) {
			if (dates[0] == null || dates[0].after(dateScope.getBegindate()))
				dates[0] = dateScope.getBegindate();
			if (dates[1] == null || dates[1].before(dateScope.getEnddate()))
				dates[1] = dateScope.getEnddate();
		}
		return dates;
	}

	/**
	 * ��������Ա��¼ת�������ڶ�
	 * 
	 * @param insertTeamPsns
	 *            �����İ�����Ա
	 * @param oldupdTeamPsns
	 *            �޸�ǰ�İ�����Ա
	 * @param newupdTeamPsns
	 *            �޸ĺ�İ�����Ա
	 * @param deleteTeamPsns
	 *            ɾ���İ�����Ա
	 * @return ���ڶ����飬[0]���������ڶ�[1]���޸�ǰ���ڶ�[2]���޸ĺ����ڶ�[3]��ɾ�����ڶ�
	 */
	@SuppressWarnings("unchecked")
	private List<IDateScope>[] formatTeamPsnToDatescope(TeamItemVO[] insertTeamPsns, TeamItemVO[] oldupdTeamPsns,
			TeamItemVO[] newupdTeamPsns, TeamItemVO[] deleteTeamPsns) {
		List<DefaultDateScope> insertTeamList = new ArrayList<DefaultDateScope>();
		List<DefaultDateScope> oldupdTeamList = new ArrayList<DefaultDateScope>();
		List<DefaultDateScope> newupdTeamList = new ArrayList<DefaultDateScope>();
		List<DefaultDateScope> deleteTeamList = new ArrayList<DefaultDateScope>();
		if (!ArrayUtils.isEmpty(insertTeamPsns)) {
			for (TeamItemVO teamPsn : insertTeamPsns)
				insertTeamList.add(new DefaultDateScope(teamPsn.getDstartdate(), teamPsn.getDenddate()));
		}
		if (!ArrayUtils.isEmpty(oldupdTeamPsns)) {
			for (TeamItemVO teamPsn : oldupdTeamPsns)
				oldupdTeamList.add(new DefaultDateScope(teamPsn.getDstartdate(), teamPsn.getDenddate()));
		}
		if (!ArrayUtils.isEmpty(newupdTeamPsns)) {
			for (TeamItemVO teamPsn : newupdTeamPsns)
				newupdTeamList.add(new DefaultDateScope(teamPsn.getDstartdate(), teamPsn.getDenddate()));
		}
		if (!ArrayUtils.isEmpty(deleteTeamPsns)) {
			for (TeamItemVO teamPsn : deleteTeamPsns)
				deleteTeamList.add(new DefaultDateScope(teamPsn.getDstartdate(), teamPsn.getDenddate()));
		}
		return new List[] { insertTeamList, oldupdTeamList, newupdTeamList, deleteTeamList };
	}

	/**
	 * ���������Ա�仯�󻹴��ڵ����ڶκ�ɾ�������ڶ� �ڰ�����Ա����ʱӦ���Ѿ�У�����ڳ�ͻ�����Ի����ڵ����ڶο���ֱ�Ӽ�Ϊ���������ڶ�+�޸ĺ�����ڶ�
	 * ��������ڶμ���������Ҳ��Ӧ�ϲ�����Ϊ���ʾ�ڰ�����Ա������������ͬ�ļ�¼���ڿ��ڵ�����ҲӦΪ������ͬ�ļ�¼��
	 * ɾ�������ڶ���Ϊ������Աɾ�������ڶ�+�޸ĺ�����ڶ�-�仯�󻹴��ڵ����ڶ�
	 * ��ɾ�������ڶο��Ժϲ�����Ϊɾ�������ڶ��ڿ��ڵ������Ӧ�ļ�¼����ɾ����ֻ�ǽ�pk_team����Ϊnull��
	 * 
	 * @param teamPsnDateScope
	 * @param existDateScope
	 * @param notExistDateScope
	 */
	private void calculateTeamPsnDatescope(List<IDateScope>[] teamPsnDateScope, List<IDateScope> existDateScope,
			List<IDateScope> notExistDateScope) {
		List<IDateScope> insertTeamList = teamPsnDateScope[0];
		List<IDateScope> oldupdTeamList = teamPsnDateScope[1];
		List<IDateScope> newupdTeamList = teamPsnDateScope[2];
		List<IDateScope> deleteTeamList = teamPsnDateScope[3];
		// ���û����ڵ����ڶ�
		existDateScope.addAll(insertTeamList);
		existDateScope.addAll(newupdTeamList);
		// ����ɾ�������ڶ�
		List<IDateScope> deleteDateScope = new ArrayList<IDateScope>();
		deleteDateScope.addAll(deleteTeamList);
		deleteDateScope.addAll(oldupdTeamList);
		IDateScope[] finalScopes = DateScopeUtils.minusDateScopes(deleteDateScope.toArray(new DefaultDateScope[0]),
				existDateScope.toArray(new DefaultDateScope[0]));
		if (ArrayUtils.isEmpty(finalScopes))
			return;
		for (IDateScope finalScope : finalScopes)
			notExistDateScope.add(finalScope);
	}

	/**
	 * ��������¼��Ϣ��ʹ������¼��������ʱ���
	 * ���Ƿ�ʽ����ĳ��ʱ��û�й�����¼����Ӵ�ʱ����ǰ�ҹ�����¼����ǰһʱ�εĹ�����¼����ʱ���ӳ�����ʱ�ν���ʱ��
	 * ����ǰû�й�����¼���򽫺�һʱ�εĹ�����¼��ʼʱ���ӳ�����ʱ�ο�ʼʱ��
	 * 
	 * @param psnjobVO
	 *            ������¼��Ϣ���Ѱ��������� must not null
	 * @param begindate
	 * @param enddate
	 * @return
	 * @throws BusinessException
	 */
	private PsnJobDateScope[] formatPsnjobDateScope(PsnJobVO[] psnjobVOs, UFLiteralDate begindate, UFLiteralDate enddate)
			throws BusinessException {
		// ���칤����¼����ְ�ͼ�ְList
		List<PsnJobDateScope> mainJobList = new ArrayList<PsnJobDateScope>();
		List<PsnJobDateScope> notMainJobList = new ArrayList<PsnJobDateScope>();
		for (PsnJobVO psnjobVO : psnjobVOs) {
			PsnJobDateScope scope = new PsnJobDateScope();
			scope.setPk_psndoc(psnjobVO.getPk_psndoc());
			scope.setPk_psnjob(psnjobVO.getPk_psnjob());
			scope.setPk_psnorg(psnjobVO.getPk_psnorg());
			scope.setBegindate(psnjobVO.getBegindate());
			scope.setEnddate(psnjobVO.getEnddate() == null ? UFLiteralDate.getDate(TBMPsndocCommonValue.END_DATA)
					: psnjobVO.getEnddate());
			if (psnjobVO.getIsmainjob().booleanValue()) {
				mainJobList.add(scope);
				continue;
			}
			notMainJobList.add(scope);
		}

		PsnJobDateScope[] mainJob = mainJobList.toArray(new PsnJobDateScope[0]);
		// ����ְ�Ĺ�����¼���ڶ���ȥ������ְ���ڶη�Χ�ڵ����ڶ�
		List<PsnJobDateScope> tmpNotMainJobList = new ArrayList<PsnJobDateScope>();
		for (PsnJobDateScope psnjobDateScope : notMainJobList) {
			IDateScope[] finalScopes = DateScopeUtils.minusDateScopes(new IDateScope[] { psnjobDateScope }, mainJob);
			if (ArrayUtils.isEmpty(finalScopes))
				continue;
			for (IDateScope finalScope : finalScopes) {
				PsnJobDateScope scope = new PsnJobDateScope();
				scope.setPk_psndoc(psnjobDateScope.getPk_psndoc());
				scope.setPk_psnjob(psnjobDateScope.getPk_psnjob());
				scope.setPk_psnorg(psnjobDateScope.getPk_psnorg());
				scope.setBegindate(finalScope.getBegindate());
				scope.setEnddate(finalScope.getEnddate());
				tmpNotMainJobList.add(scope);
			}
		}
		// ���������������ļ�ְ���ڶ�����
		PsnJobDateScope[] tmpNotMainJob = tmpNotMainJobList.toArray(new PsnJobDateScope[0]);
		if (!ArrayUtils.isEmpty(tmpNotMainJob))
			Arrays.sort(tmpNotMainJob);
		// ��������������ļ�ְ���ڶ��н��棬ȥ�����沿��(�����ڶν������ڿ�ʼ�и�)
		List<PsnJobDateScope> finalNotMainJobList = new ArrayList<PsnJobDateScope>();
		for (int i = 0; i < tmpNotMainJob.length; i++) {
			// ���Ϊ���һ�����ڶβ���Ҫ����
			if (i == tmpNotMainJob.length - 1) {
				finalNotMainJobList.add(tmpNotMainJob[i]);
				continue;
			}
			// ����������ڶ���ȫ������һ���ڶΣ��򲻴�����һ���ڶ�
			if (tmpNotMainJob[i].getEnddate().compareTo(tmpNotMainJob[i + 1].getEnddate()) >= 0) {
				tmpNotMainJob[i + 1] = tmpNotMainJob[i];
				continue;
			}
			// ��������ڶν��棬��������������Ϊ��һ���ڶο�ʼ����ǰһ��
			if (tmpNotMainJob[i].getEnddate().compareTo(tmpNotMainJob[i + 1].getBegindate()) >= 0)
				tmpNotMainJob[i].setEnddate(tmpNotMainJob[i + 1].getBegindate().getDateBefore(1));
			// �޸Ľ������ں��п��ܵ��¿�ʼ�����ڽ�������֮��ȥ���������ڶ�
			if (tmpNotMainJob[i].getBegindate().after(tmpNotMainJob[i].getEnddate()))
				continue;
			finalNotMainJobList.add(tmpNotMainJob[i]);
		}

		// ����ְ���ڶκ����յļ�ְ���ںϲ�Ϊ������¼���ڶ�
		PsnJobDateScope[] psnjobScopes = (PsnJobDateScope[]) ArrayUtils.addAll(mainJob,
				finalNotMainJobList.toArray(new PsnJobDateScope[0]));
		Arrays.sort(psnjobScopes);
		for (int i = 0; i < psnjobScopes.length; i++) {
			// ��Ա��������¼�Ŀ�ʼʱ�����null�ж�
			if (psnjobScopes[i].getBegindate() == null) {
				throw new BusinessException(ResHelper.getString("6017psndoc", "06017psndoc0155"
				/* @res "{0}�Ĺ�����¼��ʼʱ�䲻��Ϊ��!" */, CommonUtils.getPsnName(psnjobScopes[i].pk_psndoc)));
			}
			// ���Ϊ��һʱ���ҿ�ʼ�������������ڶο�ʼ����֮����Ҫ����ʼ�����ӳ����������ڶο�ʼ����
			if (0 == i && begindate.before(psnjobScopes[i].getBegindate()))
				psnjobScopes[i].setBegindate(begindate);
			int j = i + 1;
			// ���Ϊ���ʱ���ҽ����������������ڶν�������֮ǰ����Ҫ�����������ӳ����������ڶν�������
			if (j == psnjobScopes.length) {
				if (enddate.after(psnjobScopes[i].getEnddate()))
					psnjobScopes[i].setEnddate(enddate);
				break;
			}
			// ����������ʱ�Σ���Ҫ����ǰʱ�ν��������ӳ�����һʱ�ο�ʼ���ڵ�ǰһ��
			psnjobScopes[i].setEnddate(psnjobScopes[j].getBegindate().getDateBefore(1));
		}
		return psnjobScopes;
	}

	protected class PsnJobDateScope implements IDateScope, Comparable<IDateScope> {

		UFLiteralDate begindate;
		UFLiteralDate enddate;
		String pk_psndoc;
		String pk_psnjob;
		String pk_psnorg;

		public String getPk_psndoc() {
			return pk_psndoc;
		}

		public void setPk_psndoc(String pk_psndoc) {
			this.pk_psndoc = pk_psndoc;
		}

		public String getPk_psnjob() {
			return pk_psnjob;
		}

		public void setPk_psnjob(String pk_psnjob) {
			this.pk_psnjob = pk_psnjob;
		}

		public String getPk_psnorg() {
			return pk_psnorg;
		}

		public void setPk_psnorg(String pk_psnorg) {
			this.pk_psnorg = pk_psnorg;
		}

		@Override
		public UFLiteralDate getBegindate() {
			return begindate;
		}

		@Override
		public UFLiteralDate getEnddate() {
			return enddate;
		}

		@Override
		public void setBegindate(UFLiteralDate beginDate) {
			this.begindate = beginDate;
		}

		@Override
		public void setEnddate(UFLiteralDate endDate) {
			this.enddate = endDate;
		}

		/**
		 * ͨ����ʼ���ڱȽ�
		 * 
		 * @param o
		 * @return
		 */
		@Override
		public int compareTo(IDateScope o) {
			return getBegindate().compareTo(o.getBegindate());
		}
	}

}