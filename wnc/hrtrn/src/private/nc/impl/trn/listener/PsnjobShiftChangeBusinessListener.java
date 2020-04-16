package nc.impl.trn.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.itf.hr.wa.IPsndocwadocManageService;
import nc.itf.ta.IPsnCalendarManageService;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.vo.bd.team.team01.entity.TeamHeadVO;
import nc.vo.bd.team.team01.entity.TeamItemVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.pub.HiBatchEventValueObject;
import nc.vo.hi.pub.HiEventValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

public class PsnjobShiftChangeBusinessListener implements IBusinessListener {
	private BaseDAO baseDao = null;
	private String refTransType; // 留停異動類型
	private String refReturnType;// 複職異動類型

	@Override
	public void doAction(IBusinessEvent eventObject) throws BusinessException {
		if (!(eventObject instanceof BusinessEvent)) {
			return;
		}

		BusinessEvent be = (BusinessEvent) eventObject;
		Object eventParams = be.getObject();

		if (eventParams == null) {
			return;
		}

		HiEventValueObject[] hiEventValueObjectArray = handleEventParamters(eventParams);

		dealPsnShift(hiEventValueObjectArray, eventObject);
		dealPsnWaDoc(hiEventValueObjectArray, eventObject);
	}

	private HiEventValueObject[] handleEventParamters(Object eventParams) {

		HiEventValueObject[] hiEventValueObjectArray = null;

		// 傳入參數處理
		if ((eventParams instanceof HiEventValueObject)) {
			hiEventValueObjectArray = new HiEventValueObject[] { (HiEventValueObject) eventParams };
		} else if ((eventParams instanceof HiEventValueObject[])) {
			hiEventValueObjectArray = (HiEventValueObject[]) eventParams;
		} else if ((eventParams instanceof HiBatchEventValueObject)) {
			HiBatchEventValueObject obj = (HiBatchEventValueObject) eventParams;
			HiEventValueObject[] eventArray = new HiEventValueObject[obj.getPk_hrorg().length];
			for (int i = 0; i < eventArray.length; i++) {
				eventArray[i] = new HiEventValueObject();
				eventArray[i].setPsnjob_before(obj.getPsnjobs_before()[i]);
				eventArray[i].setPsnjob_after(obj.getPsnjobs_after()[i]);
				eventArray[i].setPk_hrorg(obj.getPk_hrorg()[i]);
			}

			hiEventValueObjectArray = eventArray;
		} else {
			throw new IllegalArgumentException(eventParams.getClass().getName());
		}
		return hiEventValueObjectArray;
	}

	private void dealPsnWaDoc(HiEventValueObject[] eventVOs, IBusinessEvent eventObject) throws BusinessException {
		List<PsnJobVO> jobList = new ArrayList<>();

		for (HiEventValueObject vo : eventVOs) {
			jobList.add(vo.getPsnjob_after());
		}
		if (jobList.size() > 0) {
			NCLocator.getInstance().lookup(IPsndocwadocManageService.class)
					.generateByPsnJob(jobList.toArray(new PsnJobVO[0]));
		}
	}

	private void dealPsnShift(HiEventValueObject[] eventVOs, IBusinessEvent eventObject) throws BusinessException {
		for (HiEventValueObject vo : eventVOs) {
			PsnJobVO oldPsnJob = vo.getPsnjob_before();
			PsnJobVO newPsnJob = vo.getPsnjob_after();

			// 留停異動類型
			refTransType = SysInitQuery.getParaString(newPsnJob.getPk_hrorg(), "TWHR11").toString();
			// 複職異動類型
			refReturnType = SysInitQuery.getParaString(newPsnJob.getPk_hrorg(), "TWHR12").toString();

			// ssx added on 2019-11-16
			// 如果修改的不是最新的記錄則不影響班次等記錄
			if (newPsnJob.getRecordnum() != 0) {
				return;
			}

			// 離職情況不處理
			if (newPsnJob.getTrnsevent() == 4) {
				return;
			}

			String pk_hrorg = vo.getPk_hrorg();

			String oldShift = getOldShift(eventObject, oldPsnJob);
			String newShift = newPsnJob.getAttributeValue("jobglbdef7") == null ? "" : (String) newPsnJob
					.getAttributeValue("jobglbdef7");

			// 未轉入人員檔案的不能生成定調資數據
			PsnOrgVO psnorg = (PsnOrgVO) this.getBaseDao().retrieveByPK(PsnOrgVO.class, newPsnJob.getPk_psnorg());
			if (psnorg.getIndocflag() == null || !psnorg.getIndocflag().booleanValue()) {
				continue;
			}
			// oldShift和newShift不相同，即為發生班組異動
			if (!oldShift.equals(newShift)
					|| (newPsnJob.getTrnstype() != null && newPsnJob.getTrnstype().equals(refReturnType))) {
				if (StringUtils.isEmpty(newShift) && oldPsnJob != null) {
					// 同步班次
					((IPsnCalendarManageService) NCLocator.getInstance().lookup(IPsnCalendarManageService.class))
							.sync2TeamCalendar(
									pk_hrorg,
									oldShift,
									new String[] { newPsnJob.getPk_psndoc() },
									oldPsnJob.getBegindate(),
									findEndDate(oldShift, newPsnJob.getBegindate() == null ? "9999-12-31" : newPsnJob
											.getBegindate().getDateBefore(1).toString()));
				} else {
					// 同步班次
					((IPsnCalendarManageService) NCLocator.getInstance().lookup(IPsnCalendarManageService.class))
							.sync2TeamCalendar(
									pk_hrorg,
									(StringUtils.isEmpty(newShift) ? null : newShift),
									new String[] { newPsnJob.getPk_psndoc() },
									newPsnJob.getBegindate(),
									findEndDate(newShift, newPsnJob.getEnddate() == null ? "9999-12-31" : newPsnJob
											.getEnddate().toString()));
				}
			}

			// ssx moved from upper if block, on 2019-10-26
			// Fix bug: no shift info in tbm_psndoc when psnjob shift was not
			// changed
			changeShiftGroup(pk_hrorg, oldPsnJob, newPsnJob, eventObject); // 修改班组
			// end
		}
	}

	public String getOldShift(IBusinessEvent eventObject, PsnJobVO oldPsnJob) {
		return (oldPsnJob == null || oldPsnJob.getAttributeValue("jobglbdef7") == null || (eventObject.getEventType()
				.equals("600702") && eventObject.getSourceID().equals("218971f0-e5dc-408b-9a32-56529dddd4db"))) ? ""
				: (String) oldPsnJob.getAttributeValue("jobglbdef7");
	}

	private UFLiteralDate findEndDate(String cteamid, String psnjobEnddate) throws BusinessException {
		// 取班组已排班最后一日
		// 取人员在职日最后一日
		// 取日期较小的一个
		String strSQL = "select calendar from tbm_psncalendar where pk_psndoc in (select pk_psndoc from hi_psnjob where pk_psnjob in (select pk_psnjob from bd_team_b where cteamid = '"
				+ cteamid + "')) and calendar<='" + psnjobEnddate + "' order by calendar desc";
		String maxDate = (String) this.getBaseDao().executeQuery(strSQL, new ColumnProcessor());
		return new UFLiteralDate(StringUtils.isEmpty(maxDate) ? psnjobEnddate : maxDate);
	}

	private void changeShiftGroup(String pk_hrorg, PsnJobVO oldPsnJob, PsnJobVO newPsnJob, IBusinessEvent eventObject)
			throws BusinessException {
		TeamItemVO[] updatedTeamItems = null;

		// 結束舊班組
		if (oldPsnJob != null) {
			updatedTeamItems = finishOldShift(oldPsnJob, newPsnJob, eventObject);
		}
		// 開始新班組
		if (!existsOverlapTeam(newPsnJob, updatedTeamItems) && !isExit(newPsnJob)) {
			NCLocator.getInstance().lookup(IPsndocwadocManageService.class).generateTeamItem(newPsnJob);
		}
	}

	private boolean isExit(PsnJobVO newPsnJob) throws BusinessException {
		// MOD 取组织级参数“留职停薪”异动类型
		// ssx added on 2019-04-11
		String pk_trnstype_TWHR11 = SysInitQuery.getParaString(newPsnJob.getPk_org(), "TWHR11");
		if (pk_trnstype_TWHR11 == null) {
			pk_trnstype_TWHR11 = "";
		}
		// end MOD
		return pk_trnstype_TWHR11.equals(newPsnJob.getTrnstype());
	}

	@SuppressWarnings("unchecked")
	private boolean existsOverlapTeam(PsnJobVO newPsnJob, TeamItemVO[] updatedTeamItems) throws BusinessException {
		String strSQL = "select cteam_bid from bd_team_b where cworkmanid = '" + newPsnJob.getPk_psndoc()
				+ "' and dstartdate <= '" + (newPsnJob.getEnddate() == null ? "9999-12-31" : newPsnJob.getEnddate())
				+ "' and isnull(denddate, '9999-12-31') >= '" + newPsnJob.getBegindate() + "' and dr=0";

		List<String> itempks = (List<String>) this.getBaseDao().executeQuery(strSQL, new ColumnListProcessor());

		boolean exists = false;

		if (itempks != null && itempks.size() > 0) {
			exists = true;
		} else if (updatedTeamItems != null && updatedTeamItems.length > 0) {
			for (TeamItemVO updateditem : updatedTeamItems) {
				if (updateditem.getCteamid().equals(newPsnJob.getAttributeValue("jobglbdef7"))
						&& (updateditem.getDstartdate().isSameDate(newPsnJob.getBegindate()) || updateditem
								.getDstartdate().before(newPsnJob.getBegindate()))
						&& (updateditem.getDenddate() == null
								|| updateditem.getDenddate().isSameDate(newPsnJob.getBegindate()) || updateditem
								.getDenddate().after(newPsnJob.getBegindate()))) {
					exists = true;
					break;
				}

				if (exists) {
					break;
				}
			}
		}

		return exists;
	}

	@SuppressWarnings("unchecked")
	private TeamItemVO[] finishOldShift(PsnJobVO oldPsnJob, PsnJobVO newPsnJob, IBusinessEvent eventObject)
			throws BusinessException {
		String oldShift = getOldShift(eventObject, oldPsnJob);
		Collection<TeamItemVO> updateItemVOs = new ArrayList<TeamItemVO>();

		// 舊班組為空時，不做結束動作
		if (!StringUtils.isEmpty(oldShift)) {
			Collection<TeamItemVO> itemVOs = this.getBaseDao().retrieveByClause(TeamItemVO.class,
					"cteamid='" + oldShift + "' and isnull(dr,0)=0 and cworkmanid = '"+newPsnJob.getPk_psndoc()+"'");

			for (TeamItemVO vo : itemVOs) {
				if (vo.getCworkmanid().equals(newPsnJob.getPk_psndoc())) {
					// ssx added on 2019-08-18
					// 在班組同一個人有多條記錄時，不能所有都設定結束日期
					UFLiteralDate originEnddate = vo.getDenddate() == null ? new UFLiteralDate("9999-12-31") : vo
							.getDenddate();
					UFLiteralDate newJobEnddate = newPsnJob.getEnddate() ==null?new UFLiteralDate("9999-12-31"):newPsnJob.getEnddate();
					
					String oldTeamId = (oldPsnJob==null || oldPsnJob.getAttributeValue("jobglbdef7")==null)?
							"":(String)oldPsnJob.getAttributeValue("jobglbdef7");
					String newTeamId = (newPsnJob==null || newPsnJob.getAttributeValue("jobglbdef7")==null)?
							"":(String)newPsnJob.getAttributeValue("jobglbdef7");
					if (vo.getDstartdate().before(newPsnJob.getBegindate().getDateBefore(1))
							&& originEnddate.after(newPsnJob.getBegindate().getDateBefore(1))) {
						vo.setDenddate(newPsnJob.getBegindate().getDateBefore(1));
						vo.setStatus(VOStatus.UPDATED);
						updateItemVOs.add(vo);
					}else if(vo.getDstartdate().isSameDate(newPsnJob.getBegindate())
						&& originEnddate.isSameDate(newJobEnddate)
						&& !oldTeamId.equals(newTeamId)){
					    	//tank 异动的时候,只修改班组的情况
					    	vo.setStatus(VOStatus.DELETED);
					    	updateItemVOs.add(vo);
					}
					
				}
			}

			if (updateItemVOs.size() > 0) {
				TeamHeadVO headVO = (TeamHeadVO) this.getBaseDao().retrieveByPK(TeamHeadVO.class, oldShift);
				NCLocator.getInstance().lookup(IPsndocwadocManageService.class).updateShiftGroup(headVO, updateItemVOs);
			}
		}

		return updateItemVOs.toArray(new TeamItemVO[0]);
	}

	public BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}

	public void setBaseDao(BaseDAO baseDao) {
		this.baseDao = baseDao;
	}

}
