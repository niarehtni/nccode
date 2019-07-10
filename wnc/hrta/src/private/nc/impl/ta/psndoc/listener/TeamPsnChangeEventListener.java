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
 * 班组人员与考勤档案的关系:班组人员有效的天,一定要存在考勤档案记录,且考勤档案的pk_team为人员所属班组 班组人员无效的天,可以有考勤档案,也可以没有
 * 考勤档案有效且有pk_team的天,一定要存在班组人员记录,且pk_team要一致 考勤档案无效或者有效但无pk_team的天,一定不存在班组人员记录
 * 
 * 监听班组人员变化的事件.所有的人员都是bd_psndoc的记录,没有hi_psnjob和hi_psnorg
 * 首先确定一点:班组人员变化影响的HR组织是班组所属业务单元所属的HR组织 监听逻辑:
 * 新增班组人员:新增之后,新增人员的日期范围内需要保证有考勤档案记录,且记录上的pk_team字段要设置(可能会导致一条考勤档案被拆分)
 * 修改班组人员的开始/结束日期: 比较修改前和修改后的日期范围:前后都有的天,考勤档案不变;新增的天,要保证有考勤档案,且pk_team为人员的班组;
 * 删除的天,考勤档案不删除,但pk_team要设置为null 删除班组人员:
 * 找出考勤档案中,与此班组人员有日期交集的记录,将这段日期范围内的pk_team设置为null(可能会导致一条考勤档案被拆分)
 * 
 * 班组人员和考勤档案应该是同步的关系,即班组人员的操作要影响考勤档案,考勤档案的操作要影响班组人员 考勤档案对班组人员的影响(在另外一个类中完成):
 * 新增考勤档案:如果班组字段为空,则新增考勤档案的日期范围内,不能有班组人员的记录.若不空,则日期范围内,必须有
 * 班组人员的记录,且班组人员所属班组为考勤档案的班组 修改考勤档案:如果修改前后日期范围、班组字段都未变，则不做任何处理；若有改变则：
 * 若修改后班组字段为空，则要保证修改后的考勤档案日期范围内无班组人员记录；
 * 若修改后班组字段不空，则要保证修改后的考勤档案日期范围内有对应pk_team的班组人员记录 删除考勤档案:删除的考勤档案的范围内,不能有班组人员的记录
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
		// 班组变化时，变动的人员记录
		if (!(obj instanceof TeamChangeEventVO))
			return;
		TeamChangeEventVO evtVO = (TeamChangeEventVO) obj;
		// 如果事件是HR触发的，在此插件中不再处理
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
		// 得到所有的新增、修改前、修改后、删除的班组人员记录进行处理
		handleTeamPsnChange(evtVO.getAdditemvos(), evtVO.getOlditemvos(), evtVO.getNewitemvos(), deleteItems);
	}

	/**
	 * 考勤档案同步班组人员
	 * 
	 * @param insertTeamPsns
	 * @param oldupdTeamPsns
	 * @param newupdTeamPsns
	 * @param deleteTeamPsns
	 * @throws BusinessException
	 */
	public void handleTeamPsnChange(TeamItemVO[] insertTeamPsns, TeamItemVO[] oldupdTeamPsns,
			TeamItemVO[] newupdTeamPsns, TeamItemVO[] deleteTeamPsns) throws BusinessException {
		// 传过来的是全部的数据，没有变化的人员没有必要再处理一遍(会重新排班很耗时)，在此过滤掉
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

		// 所有记录
		TeamItemVO[] allTeamPsns = (TeamItemVO[]) ArrayUtils.addAll(ArrayUtils.addAll(insertTeamPsns, oldupdTeamPsns),
				ArrayUtils.addAll(newupdTeamPsns, deleteTeamPsns));
		if (ArrayUtils.isEmpty(allTeamPsns))
			return;
		String pk_team = allTeamPsns[0].getCteamid();
		String pk_org = allTeamPsns[0].getPk_org();
		String pk_group = allTeamPsns[0].getPk_group();
		// 郁闷了 TeamItemVO中竟然没有pk_org的值
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
			throw new BusinessException("班组定义传入时间的班组人员的工作记录为空！");
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
		// 人员没有考勤档案时自动添加考勤档案，如果直接返回则会造成自助班组维护添加进一条假数据
		// if(MapUtils.isEmpty(tbmpsndocMap)){
		// return;
		// }

		List<TBMPsndocVO> haveTeamPsndocs = new ArrayList<TBMPsndocVO>();
		List<TBMPsndocVO> noneTeamPsndocs = new ArrayList<TBMPsndocVO>();
		List<TBMPsndocVO> oldTeamPsndocs = new ArrayList<TBMPsndocVO>();
		List<TBMPsndocVO> insertPsndocs = new ArrayList<TBMPsndocVO>();
		Map<String, PsnJobDateScope[]> jobMap = new HashMap<String, PsnJobDateScope[]>();
		// 按人循环处理
		for (String pk_psndoc : psndocSet) {
			List<IDateScope>[] teamPsnDateScope = formatTeamPsnToDatescope(insertTeamPsnMap == null ? null
					: insertTeamPsnMap.get(pk_psndoc),
					oldupdTeamPsnMap == null ? null : oldupdTeamPsnMap.get(pk_psndoc), newupdTeamPsnMap == null ? null
							: newupdTeamPsnMap.get(pk_psndoc),
					deleteTeamPsnMap == null ? null : deleteTeamPsnMap.get(pk_psndoc));
			// 班组人员变化后还存在以及新增的日期段和不再存在的日期段
			List<IDateScope> existDateScope = new ArrayList<IDateScope>();
			List<IDateScope> notExistDateScope = new ArrayList<IDateScope>();
			calculateTeamPsnDatescope(teamPsnDateScope, existDateScope, notExistDateScope);
			// 人员变化的开始日期、结束日期
			UFLiteralDate[] dates = getBeginAndEnddate(existDateScope, notExistDateScope);
			UFLiteralDate begindate = dates[0];
			UFLiteralDate enddate = dates[1];
			// 根据开始结束日期查询考勤档案和工作记录
			// 因为主兼职的问题，考勤档案和班组的人相同但是组织可能不同了，因此查询考勤档案的时候不能用pk_hrorg过滤了
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
				/* @res "在日期范围内没有任职记录！" */);
			}
			PsnJobVO[] psnjobVOs = list.toArray(new PsnJobVO[0]);
			PsnJobDateScope[] psnjobScopes = formatPsnjobDateScope(psnjobVOs, begindate, enddate);
			jobMap.put(pk_psndoc, psnjobScopes);
			// 当前人员待修改的含班组的考勤档案
			List<TBMPsndocVO> haveTeamPsndocList = new ArrayList<TBMPsndocVO>();
			// 循环处理已有考勤档案，与还存在的日期段相交部分则修改所属班组为pk_team，与不再存在的日期段相交部分则修改所属班组为null,都不相交的部分使用原班组
			// 此处处理的时候没确认组织是否一致，有可能导致新增考勤档案的组织和班组的组织不一致，pk_job也可能不一致，在insert的时候一块处理pk_org吧
			calculateUpdatePsndocs(psndocVOs, existDateScope, notExistDateScope, haveTeamPsndocList, noneTeamPsndocs,
					oldTeamPsndocs);
			haveTeamPsndocs.addAll(haveTeamPsndocList);
			// 将班组人员还存在时段去掉与考勤档案相交部分，剩余的日期段与工作记录相交产生新的考勤档案，这些考勤档案的工作记录使用相交的工作记录
			calculateInsertPsndocs(psnjobScopes, existDateScope.toArray(new DefaultDateScope[0]),
					haveTeamPsndocList.toArray(new IDateScope[0]), insertPsndocs);
		}
		// 处理考勤档案
		List<TBMPsndocVO> updatePsndocs = new ArrayList<TBMPsndocVO>();
		processPsndocList(pk_team, pk_group, pk_hrorg, haveTeamPsndocs, noneTeamPsndocs, oldTeamPsndocs, insertPsndocs,
				updatePsndocs);

		// calculateUpdatePsndocs中创建的考勤档案没有考虑pk_org、pk_job是否和pk_hrorg和psnjob一致
		TBMPsndocVO[] insertVOs = insertPsndocs.toArray(new TBMPsndocVO[0]);
		TBMPsndocVO[] updateVOs = updatePsndocs.toArray(new TBMPsndocVO[0]);
		ensureJob(pk_hrorg, updateVOs, jobMap, allTeamPsns);
		ensureJob(pk_hrorg, insertVOs, jobMap, allTeamPsns);
		// 保存
		updatePsndocVOs(pk_hrorg, updateVOs);
		insertPsndocVOs(pk_hrorg, insertVOs);
		// 保存后自动排班
		List<TBMPsndocVO> allPsndocs = new ArrayList<TBMPsndocVO>();
		if (!ArrayUtils.isEmpty(updateVOs)) {// 若无班组则不用重新排班
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
			// 原考勤档案在A组织，B组织加入班组一段时间，则班组结束日期之后的考勤档案就留在B组织了
			if (vo.getEnddate().toString().equals(TBMPsndocCommonValue.END_DATA)) {
				// 管理组织的更新，非默认的管理组织不能更新
				// if(StringUtils.isBlank(vo.getPk_adminorg())
				// ||StringUtils.isBlank(vo.getPk_org())
				// ||vo.getPk_adminorg().equals(vo.getPk_org())){
				// vo.setPk_adminorg(pk_hrorg);
				// }
				// 若人力资源组织发生了变化则清空考勤卡号和考勤地点
				if (!vo.getPk_org().equals(pk_hrorg)) {
					vo.setTimecardid(null);
					vo.setSecondcardid(null);
					vo.setPk_place(null);
					vo.setPk_adminorg(pk_hrorg);
				}
				vo.setPk_org(pk_hrorg);// 组织同步
				for (PsnJobDateScope job : psnJobDateScopes) {
					if (DateScopeUtils.contains(job, vo)) {// 确保pk_job一致
						vo.setPk_psnjob(job.getPk_psnjob());
						vo.setPk_psnorg(job.getPk_psnorg());
					}
				}
				continue;
			}
			for (TeamItemVO teamitem : allTeamPsns) {
				// 考勤档案要和班组同步的时间段 2012-07-11 改为考勤档案时间再班组时间段内的都需要同步
				// if(teamitem.getDstartdate().toString().equals(vo.getBegindate().toString())&&
				// ((teamitem.getDenddate()!=null&&teamitem.getDenddate().toString().equals(vo.getEnddate().toString()))||
				// teamitem.getDenddate()==null&&vo.getEnddate().toString().equals(TBMPsndocCommonValue.END_DATA))){
				if (!teamitem.getDstartdate().after(vo.getBegindate())
						&& (teamitem.getDenddate() == null || !teamitem.getDenddate().before(vo.getEnddate()))) {
					// 管理组织的更新，非默认的管理组织不能更新
					// if(StringUtils.isBlank(vo.getPk_adminorg())
					// ||StringUtils.isBlank(vo.getPk_org())
					// ||vo.getPk_adminorg().equals(vo.getPk_org())){
					// vo.setPk_adminorg(pk_hrorg);
					// }
					// 若人力资源组织发生了变化则清空考勤卡号和考勤地点
					if (!vo.getPk_org().equals(pk_hrorg)) {
						vo.setTimecardid(null);
						vo.setSecondcardid(null);
						vo.setPk_place(null);
						vo.setPk_adminorg(pk_hrorg);
					}
					vo.setPk_org(pk_hrorg);// 组织同步
					for (PsnJobDateScope job : psnJobDateScopes) {
						if (DateScopeUtils.contains(job, vo)) {// 确保pk_job一致
							vo.setPk_psnjob(job.getPk_psnjob());
							vo.setPk_psnorg(job.getPk_psnorg());
						}
					}
				}
			}

			// //若原来在A组织的班组中，现在添加到b组织的班组中，a组织的考勤档案结束掉了，但是jobMap中存储的是b组织的工作记录，a组织的考勤档案是不能更新数据的
			// if(vo.getEnddate()!=null&&!vo.getEnddate().toString().equals(TBMPsndocCommonValue.END_DATA)&&!vo.getPk_org().equals(pk_hrorg))
			// continue;
			// //管理组织的更新，非默认的管理组织不能更新
			// if(StringUtils.isBlank(vo.getPk_adminorg())
			// ||StringUtils.isBlank(vo.getPk_org())
			// ||vo.getPk_adminorg().equals(vo.getPk_org())){
			// vo.setPk_adminorg(pk_hrorg);
			// }
			// vo.setPk_org(pk_hrorg);//组织同步
			// PsnJobDateScope[] psnJobDateScopes =
			// jobMap.get(vo.getPk_psndoc());
			// for(PsnJobDateScope job:psnJobDateScopes){
			// if(DateScopeUtils.contains(job, vo)){//确保pk_job一致
			// vo.setPk_psnjob(job.getPk_psnjob());
			// vo.setPk_psnorg(job.getPk_psnorg());
			// }
			// }
		}
	}

	/**
	 * 新增
	 * 
	 * @param pk_hrorg
	 * @param insertVOs
	 * @throws BusinessException
	 */
	private void insertPsndocVOs(String pk_hrorg, TBMPsndocVO[] insertVOs) throws BusinessException {
		if (ArrayUtils.isEmpty(insertVOs))
			return;
		// 校验考勤期间
		NCLocator.getInstance().lookup(IPeriodQueryService.class).checkBeforeInsertTBMPsndoc(pk_hrorg, insertVOs);
		// 保存
		new BaseDAO().insertVOArray(insertVOs);
		// 考勤月报初始化
		NCLocator.getInstance().lookup(IMonthStatManageService.class).processAfterInsertTBMPsndoc(pk_hrorg, insertVOs);
	}

	/**
	 * 修改
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
		// 校验考勤期间
		NCLocator.getInstance().lookup(IPeriodQueryService.class).checkBeforeUpdateTBMPsndoc(pk_hrorg, updateVOs);
		// 考勤月报初始化
		NCLocator.getInstance().lookup(IMonthStatManageService.class).processBeforeUpdateTBMPsndoc(pk_hrorg, updateVOs);
		new BaseDAO().updateVOArray(updateVOs);
	}

	/**
	 * 取考勤档案主键数组 过滤掉重复的主键
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
	 * 处理待保存的考勤档案信息
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
		// 待新增的考勤档案
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
		// 存在班组的考勤档案
		if (!CollectionUtils.isEmpty(haveTeamPsndocs)) {
			for (TBMPsndocVO psndocVO : haveTeamPsndocs) {
				psndocVO.setPk_team(pk_team);
				// 考勤档案的组织要和班组所属的人力资源组织保持一致
				psndocVO.setPk_group(pk_group);
				// 若更换了组织则管理组织也要改变
				if (!psndocVO.getPk_org().equals(pk_hrorg)) {
					psndocVO.setPk_adminorg(pk_hrorg);
					// 更换了组织则考勤卡号、考勤地点都清空
					psndocVO.setTimecardid(null);
					psndocVO.setSecondcardid(null);
					psndocVO.setPk_place(null);
				}
				psndocVO.setPk_org(pk_hrorg);
				setValueToList(psndocVO, insertPsndocs, updatePsndocs);
			}
		}
		// 班组置空的考勤档案
		if (!CollectionUtils.isEmpty(noneTeamPsndocs)) {
			for (TBMPsndocVO psndocVO : noneTeamPsndocs) {
				psndocVO.setPk_team(null);
				setValueToList(psndocVO, insertPsndocs, updatePsndocs);
			}
		}
		// 班组置空的考勤档案
		if (!CollectionUtils.isEmpty(oldTeamPsndocs)) {
			for (TBMPsndocVO psndocVO : oldTeamPsndocs)
				setValueToList(psndocVO, insertPsndocs, updatePsndocs);
		}
	}

	/**
	 * 将考勤档案加入到更新列表 如果在更新列表中已存在此考勤档案,则将其主键置空并放到新增列表中
	 * 因为在日期段交切时可能将一个原考勤档案交切成多个待更新的考勤档案,则会出现覆盖,所以只更新其中任何一个即可,其余的则使用新增的方式加入
	 * 
	 * @param psndocVO
	 * @param insertPsndocs
	 * @param updatePsndocs
	 */
	private void setValueToList(TBMPsndocVO psndocVO, List<TBMPsndocVO> insertPsndocs, List<TBMPsndocVO> updatePsndocs) {
		if (updatePsndocs.contains(psndocVO)) {
			// 相同人的考勤档案，开始日期早的状态为修改，晚的状态为新增
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
	 * 使用工作记录信息新增考勤档案 将班组人员还存在时段去掉与已更新考勤档案的相交部分 剩余日期段与工作记录的交集即为要新增的考勤档案
	 * 这些考勤档案使用与其相交的工作记录来构造
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
		// 按工作记录日期段循环处理
		for (PsnJobDateScope psnjobScope : psnjobScopes) {
			TBMPsndocVO psndocVO = new TBMPsndocVO();
			psndocVO.setPk_psndoc(psnjobScope.getPk_psndoc());
			psndocVO.setPk_psnjob(psnjobScope.getPk_psnjob());
			psndocVO.setPk_psnorg(psnjobScope.getPk_psnorg());
			psndocVO.setTbm_prop(TBMPsndocVO.TBM_PROP_MACHINE);

			// MOD(PM26104)新生成的考勤档案自动加入默认的加班管控模式及工时形态
			// ssx added on 2019-06-14
			psndocVO.setOvertimecontrol(OvertimecontrolEunm.MANUAL_CHECK.toIntValue());
			psndocVO.setWeekform(WorkWeekFormEnum.ONEWEEK.toIntValue());
			// end MOD

			// 取当前工作记录日期段与待新增日期段交集,用工作记录信息构造待新增psndocVO
			IDateScope[] finalScope = DateScopeUtils.intersectionDateScopes(new IDateScope[] { psnjobScope },
					insertScope);
			createTBMPsndocVO(psndocVO, finalScope, insertPsndocs);
		}
	}

	/**
	 * 生成要修改的考勤档案记录 如果考勤档案与班组人员变化的日期段有交集，则这些考勤档案都将修改 此时不设置pk_team，在所有数据汇总后同时处理
	 * 
	 * @param psndocVOs
	 *            考勤档案记录
	 * @param existDateScope
	 * @param notExistDateScope
	 * @param haveTeamPsndocs
	 *            使用新班组的考勤档案
	 * @param noneTeamPsndocs
	 *            班组置空的考勤档案
	 * @param oldTeamPsndocs
	 *            使用原班组的考勤档案（此部分考勤档案是因为与班组人员变化日期段无交集，所以不发生变化，
	 *            但开始日期和结束日期可能因为剪切而发生变化）
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
			// 与班组人员存在的日期段重叠的处理
			if (!ArrayUtils.isEmpty(existDateScopes) && DateScopeUtils.isCross(existDateScopes, psndocScope)) {
				// 取相重叠部分构造考勤档案
				IDateScope[] crossScopes = DateScopeUtils.intersectionDateScopes(existDateScopes, psndocScope);
				// 此处使用原来的考勤档案创建新的考勤档案，没考虑组织是否发生变化，也没有考虑pk_job是否一致
				createTBMPsndocVO(psndocVO, crossScopes, haveTeamPsndocs);
				// 考勤档案日期段去掉重叠部分
				psndocScope = DateScopeUtils.minusDateScopes(psndocScope, existDateScopes);
			}
			// 与班组人员不再存在的日期段重叠的处理
			if (!ArrayUtils.isEmpty(notExistDateScopes) && psndocScope != null
					&& DateScopeUtils.isCross(notExistDateScopes, psndocScope)) {
				// 取相重叠部分构造考勤档案
				IDateScope[] crossScopes = DateScopeUtils.intersectionDateScopes(notExistDateScopes, psndocScope);
				createTBMPsndocVO(psndocVO, crossScopes, noneTeamPsndocs);
				// 考勤档案日期段去掉重叠部分
				psndocScope = DateScopeUtils.minusDateScopes(psndocScope, notExistDateScopes);
			}
			createTBMPsndocVO(psndocVO, psndocScope, oldTeamPsndocs);
		}
	}

	/**
	 * 根据日期段重新生成考勤档案
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
	 * 取日期段里的最早开始日期和最晚结束日期
	 * 
	 * @param dateScope1
	 * @param dateScope2
	 * @return [0]：开始日期 [1]：结束日期
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
	 * 将班组人员记录转换成日期段
	 * 
	 * @param insertTeamPsns
	 *            新增的班组人员
	 * @param oldupdTeamPsns
	 *            修改前的班组人员
	 * @param newupdTeamPsns
	 *            修改后的班组人员
	 * @param deleteTeamPsns
	 *            删除的班组人员
	 * @return 日期段数组，[0]：新增日期段[1]：修改前日期段[2]：修改后日期段[3]：删除日期段
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
	 * 计算班组人员变化后还存在的日期段和删除的日期段 在班组人员保存时应该已经校验日期冲突，所以还存在的日期段可以直接记为新增的日期段+修改后的日期段
	 * （如果日期段间日期相连也不应合并，因为这表示在班组人员里这是两条不同的记录，在考勤档案中也应为两条不同的记录）
	 * 删除的日期段则为班组人员删除的日期段+修改后的日期段-变化后还存在的日期段
	 * （删除的日期段可以合并，因为删除的日期段在考勤档案里对应的记录并不删除，只是将pk_team设置为null）
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
		// 设置还存在的日期段
		existDateScope.addAll(insertTeamList);
		existDateScope.addAll(newupdTeamList);
		// 设置删除的日期段
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
	 * 处理工作记录信息，使工作记录覆盖整个时间段
	 * 覆盖方式：若某段时间没有工作记录，则从此时段往前找工作记录，将前一时段的工作记录结束时间延长到本时段结束时间
	 * 若往前没有工作记录，则将后一时段的工作记录开始时间延长到本时段开始时间
	 * 
	 * @param psnjobVO
	 *            工作记录信息（已按日期排序） must not null
	 * @param begindate
	 * @param enddate
	 * @return
	 * @throws BusinessException
	 */
	private PsnJobDateScope[] formatPsnjobDateScope(PsnJobVO[] psnjobVOs, UFLiteralDate begindate, UFLiteralDate enddate)
			throws BusinessException {
		// 构造工作记录的主职和兼职List
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
		// 将兼职的工作记录日期段中去掉在主职日期段范围内的日期段
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
		// 　将不包含主键的兼职日期段排序
		PsnJobDateScope[] tmpNotMainJob = tmpNotMainJobList.toArray(new PsnJobDateScope[0]);
		if (!ArrayUtils.isEmpty(tmpNotMainJob))
			Arrays.sort(tmpNotMainJob);
		// 如果不包含主键的兼职日期段有交叉，去掉交叉部分(从日期段结束日期开始切割)
		List<PsnJobDateScope> finalNotMainJobList = new ArrayList<PsnJobDateScope>();
		for (int i = 0; i < tmpNotMainJob.length; i++) {
			// 如果为最后一个日期段不需要处理
			if (i == tmpNotMainJob.length - 1) {
				finalNotMainJobList.add(tmpNotMainJob[i]);
				continue;
			}
			// 　如果本日期段完全包含下一日期段，则不处理下一日期段
			if (tmpNotMainJob[i].getEnddate().compareTo(tmpNotMainJob[i + 1].getEnddate()) >= 0) {
				tmpNotMainJob[i + 1] = tmpNotMainJob[i];
				continue;
			}
			// 如果有日期段交叉，将结束日期设置为下一日期段开始日期前一天
			if (tmpNotMainJob[i].getEnddate().compareTo(tmpNotMainJob[i + 1].getBegindate()) >= 0)
				tmpNotMainJob[i].setEnddate(tmpNotMainJob[i + 1].getBegindate().getDateBefore(1));
			// 修改结束日期后有可能导致开始日期在结束日期之后，去掉这种日期段
			if (tmpNotMainJob[i].getBegindate().after(tmpNotMainJob[i].getEnddate()))
				continue;
			finalNotMainJobList.add(tmpNotMainJob[i]);
		}

		// 将主职日期段和最终的兼职日期合并为工作记录日期段
		PsnJobDateScope[] psnjobScopes = (PsnJobDateScope[]) ArrayUtils.addAll(mainJob,
				finalNotMainJobList.toArray(new PsnJobDateScope[0]));
		Arrays.sort(psnjobScopes);
		for (int i = 0; i < psnjobScopes.length; i++) {
			// 对员工工作记录的开始时间进行null判断
			if (psnjobScopes[i].getBegindate() == null) {
				throw new BusinessException(ResHelper.getString("6017psndoc", "06017psndoc0155"
				/* @res "{0}的工作记录开始时间不能为空!" */, CommonUtils.getPsnName(psnjobScopes[i].pk_psndoc)));
			}
			// 如果为第一时段且开始日期在整个日期段开始日期之后，需要将开始日期延长到整个日期段开始日期
			if (0 == i && begindate.before(psnjobScopes[i].getBegindate()))
				psnjobScopes[i].setBegindate(begindate);
			int j = i + 1;
			// 如果为最后时段且结束日期在整个日期段结束日期之前，需要将结束日期延长到整个日期段结束日期
			if (j == psnjobScopes.length) {
				if (enddate.after(psnjobScopes[i].getEnddate()))
					psnjobScopes[i].setEnddate(enddate);
				break;
			}
			// 如果不是最后时段，需要将当前时段结束日期延长到下一时段开始日期的前一天
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
		 * 通过开始日期比较
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