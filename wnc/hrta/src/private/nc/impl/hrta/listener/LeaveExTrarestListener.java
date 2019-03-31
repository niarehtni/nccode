package nc.impl.hrta.listener;

import java.util.ArrayList;
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
import nc.hr.utils.InSQLCreator;
import nc.itf.bd.psn.psndoc.IPsndocQueryService;
import nc.itf.hrta.ILeaveextrarestMaintain;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.vo.bd.holiday.HolidayVO;
import nc.vo.bd.psn.PsnjobVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.bd.team.team01.entity.TeamItemVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leaveextrarest.AggLeaveExtraRestVO;
import nc.vo.ta.leaveextrarest.LeaveExtraRestVO;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnCalendarVO;
import nc.vo.ta.pub.IMetaDataIDConst;

public class LeaveExTrarestListener implements IBusinessListener {
	
	private BaseDAO dao = new BaseDAO(); 

	@Override
	public void doAction(IBusinessEvent event)
			throws BusinessException {
		handleChange(event);

	}
	
	private void handleChange(IBusinessEvent event) throws BusinessException {
		BusinessEvent be = (BusinessEvent) event;
		Object obj = be.getObject();
		if (obj == null)
			return;
		// ����ҵ��仯��Ĺ�����¼�������ת����Ա��������仯ǰ�ͱ仯����ͬ
		if (obj instanceof AggPsnCalendar[] && event.getSourceID().equals(IMetaDataIDConst.PSNCALENDAR)
				&& event.getEventType().equals(IEventType.TYPE_INSERT_AFTER)) {
			AggPsnCalendar[] values = (AggPsnCalendar[]) obj;
			//С�ڵ�ǰϵͳʱ��ĵ���,������
			values = checkDate(values);
			//ɾ�����ʱ�����Ӳ��ݵľɵ���
			delOldDataLeaveDate(values);
			//����Ƿ�Ҫ���ɲ��ݵĵ���
			checkLeaveEx(values);
		}
		
	}
	

	/**
	 * ����ε�����С��ϵͳ���ڵ�ʱ��,���ٽ�����Ȳ��ݼٵĵ���
	 * ������a�ݼo���ӛ䛵�׃�����ڴ����ϵ�y���ڕr��
	 * �T���ſ����ݼ����ڣ������M��ʹ�ã����������{����Ρ�
	 * @param values
	 */
	private AggPsnCalendar[] checkDate(AggPsnCalendar[] values) throws BusinessException{
		if(null == values || values.length <= 0){
			return null;
		}
		List<AggPsnCalendar> result = new ArrayList<>();
		UFLiteralDate curDate = new UFLiteralDate();
		for(AggPsnCalendar vo : values){
			if(vo.getParentVO()!=null 
					&& ((PsnCalendarVO)vo.getParentVO()).getCalendar() != null
					&&((PsnCalendarVO)vo.getParentVO()).getCalendar().after((curDate))){
				result.add(vo);
			}
		}
		return result.toArray(new AggPsnCalendar[0]);
		
	}

	/**
	 * ���Ű��Ю��ӕr�����ӵ������c�T������a���씵�o��е�׃���������nͻ����r�£�
	 * �Ԅӄh��ԓ����a���씵�ļo䛡�
	 * @param values
	 */
	private void delOldDataLeaveDate(AggPsnCalendar[] values) throws BusinessException{
		if(null == values || values.length <= 0){
			return;
		}
		//����������С����,����Ա�б�
		// �������ݵó�,ÿ���˵�ÿ�������������,�Լ��˴��Ű�Ŀ�ʼ�ͽ���ʱ�� map<pk_psndoc,map<date,shift>>
		UFLiteralDate maxDate = new UFLiteralDate("1970-01-01 00:00:00");
		UFLiteralDate minDate = new UFLiteralDate("9999-12-31 23:59:59");
		Set<String> psnSet = new HashSet<>();
		for (AggPsnCalendar value : values) {
			PsnCalendarVO hVO = (PsnCalendarVO) value.getParentVO();
			if (hVO.getPk_psndoc() != null && hVO.getCalendar() != null) {
				// �ó��˴ε����ڷ�Χ
				psnSet.add(hVO.getPk_psndoc());
				if (hVO.getCalendar().after(maxDate)) {
					maxDate = hVO.getCalendar();
				}
				if (hVO.getCalendar().before(minDate)) {
					minDate = hVO.getCalendar();
				}
			}
		}
		if(psnSet.size() <= 0){
			return ;
		}
		//�ҳ������������Щ��Ա�ĵ���Ӳ��ݼ�¼
		InSQLCreator insql = new InSQLCreator();
        String psndocsInSQL = insql.getInSQL(psnSet.toArray(new String[0]));

		String sqlStr = "select * from tbm_extrarest "
				+ " where pk_psndoc in ("+psndocsInSQL+") "
				+ " and '"+minDate.toStdString()+"' <= DATEBEFORECHANGE "
				+ " and DATEBEFORECHANGE <='"+maxDate.toStdString()+"'"
				+ " and dr = 0 ";
		@SuppressWarnings("unchecked")
		List<LeaveExtraRestVO> oldLeaveExVOs = 
				(List<LeaveExtraRestVO>) dao.executeQuery(sqlStr , new BeanListProcessor(LeaveExtraRestVO.class));
		//del��
		if(null == oldLeaveExVOs || oldLeaveExVOs.size() <= 0){
			return;
		}
		List<AggLeaveExtraRestVO> delList = new ArrayList<>();
		for(LeaveExtraRestVO vo : oldLeaveExVOs){
			AggLeaveExtraRestVO aggVO = new AggLeaveExtraRestVO();
			aggVO.setParentVO(vo);
			delList.add(aggVO);
		}
		NCLocator.getInstance().lookup(ILeaveextrarestMaintain.class).delete(delList.toArray(new AggLeaveExtraRestVO[0]));
	}

	/**
	 * ����T���Ŀ��ڙn�������O�ð�M���K��ԓ��M�й��xʹ������a���ߣ�
	 * �����ն��x�еļ������˹��ݰ�Σ��t�Ԅ���������a���씵�ļo䛡�
	 * @param values
	 */
	@SuppressWarnings("unchecked")
	private void checkLeaveEx(AggPsnCalendar[] values) throws BusinessException{
		if(null == values || values.length <= 0){
			return;
		}
		// �������ݵó�,ÿ���˵�ÿ�������������,�Լ��˴��Ű�Ŀ�ʼ�ͽ���ʱ�� map<pk_psndoc,map<date,shift>>
		UFLiteralDate maxDate = new UFLiteralDate("1970-01-01 00:00:00");
		UFLiteralDate minDate = new UFLiteralDate("9999-12-31 23:59:59");
		Set<String> psnSet = new HashSet<>();
		for (AggPsnCalendar value : values) {
			PsnCalendarVO hVO = (PsnCalendarVO) value.getParentVO();
			if (hVO.getPk_psndoc() != null && hVO.getCalendar() != null) {
				// �ó��˴ε����ڷ�Χ
				psnSet.add(hVO.getPk_psndoc());
				if (hVO.getCalendar().after(maxDate)) {
					maxDate = hVO.getCalendar();
				}
				if (hVO.getCalendar().before(minDate)) {
					minDate = hVO.getCalendar();
				}
			}
		}
		if (psnSet.size() <= 0) {
			return;
		}
		//�����Щ����ָ�����ڷ�Χ�ڵİ�����Ϣ,��Ϊÿ���˵�ÿ��İ�����ܶ���һ��,���Ա�������Ϊ��λ�����ж�
		InSQLCreator insql = new InSQLCreator();
        String psndocsInSQL = insql.getInSQL(psnSet.toArray(new String[0]));
		//��ѯ��Ч�İ�����Ϣ
		String sqlStr = "select CWORKMANID, "
				+ " dstartdate,isnull(denddate,'9999-12-31') denddate "
				+ " from bd_team_b tb "
				+ " left join bd_team t on tb.cteamid = t.cteamid "
				+ " where CWORKMANID in ("+psndocsInSQL+") "
				+ " and (dstartdate<= '"+maxDate.toStdString()+"' and '"
				+ minDate.toStdString()+"' <= isnull(denddate,'9999-12-31') ) "
				+ " and isnull(t.annualrestflag,'N') = 'Y' "
				+ " and t.dr = 0 ";
		List<TeamItemVO> teamItemTempVOList = 
					(List<TeamItemVO>) dao.executeQuery(sqlStr,new BeanListProcessor(TeamItemVO.class));
		//����ʹ����Ȳ��ݵİ���,�ٽ�����Ӳ��ݵĲ���
		if(teamItemTempVOList != null && teamItemTempVOList.size() > 0){
			//map<pk_psndoc,List<date>>  ->  list<date> : list[0] ��ʼ����  list[1] ��������,list[2] ��ʼ����  list[3] ��������...����
			Map<String,List<UFLiteralDate>> psnDateListMap = new HashMap<>();
			
			//����������
			for(TeamItemVO vo : teamItemTempVOList){
				if(vo != null && vo.getCworkmanid() != null){
					String pk_psndoc = vo.getCworkmanid();
					List<UFLiteralDate> dateList = psnDateListMap.get(pk_psndoc);
					if(dateList != null){
						dateList.add(vo.getDstartdate());
						dateList.add(vo.getDenddate());
						psnDateListMap.put(pk_psndoc,dateList);
					}else{
						dateList = new ArrayList<>();
						dateList.add(vo.getDstartdate());
						dateList.add(vo.getDenddate());
						psnDateListMap.put(pk_psndoc,dateList);
					}
				}
			}
			List<PsnCalendarVO> needAddLeaveEx = new ArrayList<>();
			//�����Ű�VO���Ƿ���Ҫ������Ӳ���
			for(AggPsnCalendar vo : values){
				if(vo == null || vo.getPk_psndoc() == null || vo.getDate() == null){
					continue;
				}
				//����T���Ŀ��ڙn�������O�ð�M���K��ԓ��M�й��xʹ������a���ߣ�
				boolean hasTeamAndUseAnnalLeaveEx = 
						checkTeamAndUseAnnalLeaveEx(psnDateListMap,vo.getPk_psndoc(),vo.getDate());
				if(hasTeamAndUseAnnalLeaveEx){
					//�����ն��x�еļ������˹��ݰ�Σ��t�Ԅ���������a���씵�ļo䛡�
					PsnCalendarVO hVO = (PsnCalendarVO)vo.getParentVO();
					if(hVO.getPk_shift()!=null && hVO.getPk_shift().equals(ShiftVO.PK_GX)
							&& hVO.getDate_daytype()!=null && hVO.getDate_daytype() == HolidayVO.DAY_TYPE_HOLIDAY){
						needAddLeaveEx.add(hVO);
					}
				}
				
			}
			addLeaveEx(needAddLeaveEx);
		}
		
	}
	
	/**
	 * ����T���Ŀ��ڙn�������O�ð�M���K��ԓ��M�й��xʹ������a����,����true
	 * @param psnDateListMap ��Ȳ��ݵ�ʱ�䷶Χ 
	 * //map<pk_psndoc,List<date>>  ->  list<date> : list[0] ��ʼ����  list[1] ��������,list[2] ��ʼ����  list[3] ��������...����
			
	 * @param pk_psndoc
	 * @param date
	 * @return
	 */
	private boolean checkTeamAndUseAnnalLeaveEx(
			Map<String, List<UFLiteralDate>> psnDateListMap, String pk_psndoc,
			UFLiteralDate date) {
		List<UFLiteralDate> dateInfo = psnDateListMap.get(pk_psndoc);
		if(dateInfo != null && dateInfo.size() >= 0){
			for(int i = 0 ; i< dateInfo.size() ;i+=2){
				if(dateInfo.get(i)!= null && dateInfo.get(i+1)!=null){
					UFLiteralDate startDate = dateInfo.get(i);
					UFLiteralDate endDate = dateInfo.get(i);
					if(date.compareTo(startDate) >= 0 && startDate.compareTo(endDate) <= 0){
						//������Ӳ��ݵİ����ʱ�䷶Χ֮��
						return true;
					}
				}
			}
		}
		return false;
	}

	private void addLeaveEx(List<PsnCalendarVO> needAddLeaveEx) throws BusinessException{
		// ��������a�݆Γ�
		List<AggLeaveExtraRestVO> extraRestList = new ArrayList<AggLeaveExtraRestVO>();
		UFDouble changedayorhour = new UFDouble(1.00);
		for (PsnCalendarVO psnCalendarVO : needAddLeaveEx) {
			String pk_hrorg = psnCalendarVO.getPk_org();
			UFLiteralDate changeDate = psnCalendarVO.getCalendar();
			String psndocStr = psnCalendarVO.getPk_psndoc();
			int dateType = psnCalendarVO.getDate_daytype();
			
			if (changedayorhour.compareTo(UFDouble.ZERO_DBL) > 0) {
				if (null != changeDate) {
					AggLeaveExtraRestVO saveVO = new AggLeaveExtraRestVO();
					LeaveExtraRestVO extraRestVO = new LeaveExtraRestVO();
					extraRestVO.setPk_psndoc(psndocStr);
					extraRestVO.setPk_org(pk_hrorg);
					Map<String, String> baseInfo = getPsnBaseInfo(psndocStr,
							pk_hrorg);
					extraRestVO.setPk_org_v(baseInfo.get("pk_org_v"));
					extraRestVO.setPk_dept_v(baseInfo.get("pk_dept_v"));
					extraRestVO.setPk_group(baseInfo.get("pk_group"));
					extraRestVO.setBilldate(new UFLiteralDate());
					extraRestVO.setDatebeforechange(changeDate);
					extraRestVO.setTypebeforechange(dateType);
					extraRestVO.setDateafterchange(changeDate);
					extraRestVO.setTypeafterchange(dateType);
					extraRestVO
							.setChangetype(extraRestVO.getTypebeforechange());
					extraRestVO.setChangedayorhour(changedayorhour);
					extraRestVO.setCreationtime(new UFDateTime());
					extraRestVO.setExpiredate(getExpiredate(psndocStr,
							changeDate, extraRestVO.getBilldate()));
					saveVO.setParent(extraRestVO);
					extraRestList.add(saveVO);
				}
			}
		}
		
		NCLocator.getInstance().lookup(ILeaveextrarestMaintain.class)
				.insert(extraRestList.toArray(new AggLeaveExtraRestVO[0]));
	}
	
	
	private UFLiteralDate getExpiredate(String psndocStr,
			UFLiteralDate firstDate,UFLiteralDate billDate) throws BusinessException {
		UFLiteralDate maxLeaveDate = 
				NCLocator.getInstance().lookup(ILeaveextrarestMaintain.class)
		.calculateExpireDateByWorkAge(psndocStr,firstDate,billDate);
		return maxLeaveDate;
	}
	/**
	 * ��ԃһ�»�������Ϣ
	 * @param psndocStr
	 * @param pk_hrorg  
	 * @return pk_org_v pk_dept_v pk_group
	 * @throws BusinessException 
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> getPsnBaseInfo(String psndocStr, String pk_hrorg) throws BusinessException {
		Map<String,String> resultMap = new HashMap<>();
		IPsndocQueryService psnQuery = NCLocator.getInstance().lookup(IPsndocQueryService.class);
		PsnjobVO psnjobvo = psnQuery.queryPsnJobVOByPsnDocPK(psndocStr);
		String pk_dept = psnjobvo.getPk_dept();
		String sqlStr = "select dept.pk_vid pk_dept_v, orgs.pk_vid pk_org_v ,orgs.pk_group pk_group"
				+ " from org_dept dept "
				+ " left join org_orgs orgs on orgs.pk_org = '"+pk_hrorg+"' where dept.pk_dept = '"+pk_dept+"'";
		resultMap = (Map<String,String>)new BaseDAO().executeQuery(sqlStr, new MapProcessor());
		
		return resultMap==null?new HashMap<String,String>():resultMap;
	}
	/**
	 * ��Ա�İ�����Ϣ
	 * @author 91967
	 *
	 */
	class TeamItemTemp{
		//��Ա
		private String pk_psndoc;
		//�˰���Ŀ�ʼʱ��
		private UFLiteralDate dstartdate;
		//�˰���Ľ�������
		private UFLiteralDate denddate;
		
		public String getPk_psndoc() {
			return pk_psndoc;
		}
		public void setPk_psndoc(String pk_psndoc) {
			this.pk_psndoc = pk_psndoc;
		}
		public UFLiteralDate getDstartdate() {
			return dstartdate;
		}
		public void setDstartdate(UFLiteralDate dstartdate) {
			this.dstartdate = dstartdate;
		}
		public UFLiteralDate getDenddate() {
			return denddate;
		}
		public void setDenddate(UFLiteralDate denddate) {
			this.denddate = denddate;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
