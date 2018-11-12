package nc.impl.ta.dataprocess;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.hr.utils.InSQLCreator;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.message.util.MessageSender;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

/**
 * 員工考勤異常提醒
 * 1.	參數名稱：【截止到系統日前的日數】參數值【1-31】
 * 2.	參數名稱：【發送天數範圍】參數值【1-31】
 * 3.	推送對象：  員工本人、部門主管、上級主管
 * 4.	推送對象的郵箱
 * @author Ares.Tank
 * @date 2018-10-8 11:28:24
 *
 */
public class TimeDataAlterPlugin implements IBackgroundWorkPlugin{
	// 截止到系統日前的日數   參數值 1-31
	private static final String NUM_UNTIL_TODAY = "num_until_today";
	// 發送天數範圍  參數值 1-31
	private static final String NUM_OF_RANGE = "num_of_range";
	//推送人员
	private static final String PUSH_STAFF = "push_staff";
	//推送人员--员工本人
	private static final String PUSH_STAFF_PSN = "1001ZZ10000000017GVO";
	//推送人员--部门主管
	private static final String PUSH_STAFF_PRINCIPAL = "1001ZZ10000000017GVP";
	//推送人员--上级主管
	private static final String PUSH_STAFF_UP_PRINCIPAL = "1001ZZ10000000017GVQ";
	
	private BaseDAO baseDAO;
	
	
	@Override
	public PreAlertObject executeTask(BgWorkingContext ctx) throws BusinessException {
		
		//獲取組織
		String[] pk_orgs = ctx.getPk_orgs();
		int num2Today,numOfRange = 0;
		//獲取參數值
		try{
			num2Today = Integer.parseInt((String)ctx.getKeyMap().get(NUM_UNTIL_TODAY));
			numOfRange = Integer.parseInt((String)ctx.getKeyMap().get(NUM_OF_RANGE));
			if (num2Today < 1 || num2Today > 31) {
				throw new BusinessException("參數:'截止到系統日前的日數',未在1~31範圍內");
			}
			if (numOfRange < 1 || numOfRange > 31) {
				throw new BusinessException("參數:'發送天數範圍',未在1~31範圍內");
			}
		}catch(BusinessException e){
			throw e;
		}catch(Exception e){
			throw new BusinessException ("參數獲取失敗,請檢查參數設置!");
		}
		//查找有異常記錄
		List<MsgDTO> msgList = findWarningRecond(pk_orgs, num2Today, numOfRange);
		if(null != msgList && msgList.size() >0){
		
			//获取参数--推送人员
			String pushStaff = (String)ctx.getKeyMap().get(PUSH_STAFF);
			if(null != pushStaff){
				//根据推送对象,发送邮件
				sendMsgAll(msgList, pushStaff);
			}
		}
		return null;
	}
	//根据推送对象推送邮件
	private void sendMsgAll(List<MsgDTO> msgList, String pushStaff) throws BusinessException {
		if(null == pushStaff){
			return;
		}
		//整理要发送的人员<pk_psndoc,Set<MsgDTO>>
		Map<String,List<MsgDTO>> msgSetMap = new HashMap<>();
		if(PUSH_STAFF_PSN.equals(pushStaff)){
			for(MsgDTO msgDTO : msgList){
				if(null != msgDTO.getPkPsndoc() && !msgDTO.getPkPsndoc().equals("~") && null != msgDTO.getPsnEmail()){
					List<MsgDTO> psnTempMsgDTO = msgSetMap.get(msgDTO.getPkPsndoc()+"@@"+msgDTO.getPsnEmail());
					if(null == psnTempMsgDTO){
						psnTempMsgDTO = new ArrayList<>();
						psnTempMsgDTO.add(msgDTO);
						msgSetMap.put(msgDTO.getPkPsndoc()+"@@"+msgDTO.getPsnEmail(), psnTempMsgDTO);
					}else{
						psnTempMsgDTO.add(msgDTO);
					}
				}
			}
		}else if(PUSH_STAFF_PRINCIPAL.equals(pushStaff)){
			for(MsgDTO msgDTO : msgList){
				if(null != msgDTO.getPkPrincipal() && !msgDTO.getPkPrincipal().equals("~") && null != msgDTO.getPrincipalEmail()){
					List<MsgDTO> psnTempMsgDTO = msgSetMap.get(msgDTO.getPkPrincipal()+"@@"+msgDTO.getPrincipalEmail());
					if(null == psnTempMsgDTO){
						psnTempMsgDTO = new ArrayList<>();
						psnTempMsgDTO.add(msgDTO);
						msgSetMap.put(msgDTO.getPkPrincipal()+"@@"+msgDTO.getPrincipalEmail(), psnTempMsgDTO);
					}else{
						psnTempMsgDTO.add(msgDTO);
					}
				}
				
			}
		}else if(PUSH_STAFF_UP_PRINCIPAL.equals(pushStaff)){
			for(MsgDTO msgDTO : msgList){
				if(null != msgDTO.getPkUpPrincipal() && !msgDTO.getPkUpPrincipal().equals("~") && null != msgDTO.getUpPrincipalEmail()){
					List<MsgDTO> psnTempMsgDTO = msgSetMap.get(msgDTO.getPkUpPrincipal()+"@@"+msgDTO.getUpPrincipalEmail());
					if(null == psnTempMsgDTO){
						psnTempMsgDTO = new ArrayList<>();
						psnTempMsgDTO.add(msgDTO);
						msgSetMap.put(msgDTO.getPkUpPrincipal()+"@@"+msgDTO.getUpPrincipalEmail(), psnTempMsgDTO);
					}else{
						psnTempMsgDTO.add(msgDTO);
					}
				}
			}
		}
		if(msgSetMap.size() > 0){
			//封装邮件格式,发送邮件
			try {
				sendEmailByMsg(msgSetMap);
			} catch (Exception e) {
				throw new BusinessException(e.getMessage());
			}
			
		}
	}
	/**
	 * 数据封装完毕,开始发送邮件
	 * 
	 * @author Ares.Tank
	 * @date 2018-10-8 13:00:57
	 * @param msgSetMap <email,Set<MsgDTO>>
	 * @throws Exception 
	 */
	private void sendEmailByMsg(Map<String, List<MsgDTO>> msgSetMap) throws Exception{
		
		
		
		//邮件主题
		String subject = "【員工考勤異常提醒】";
		//邮件内容
		//String content = "【若於月底前未完成考勤異常申請，將一律以曠職扣薪論。】\n";
		//附件数组
		//Attachment[] attachments = null;
		/*try {
			MessageSender.sendPlainTextEmail(mailAddress, subject, content, attachments);
		} catch (Exception e) {
			Debug.debug(e.getMessage());
			e.printStackTrace();
		}*/
		StringBuilder sb = new StringBuilder();

		//<psndoc@@email,Set<MsgDTO>>
		for(Map.Entry<String, List<MsgDTO>> entry : msgSetMap.entrySet()){
			String psnAndEmail = entry.getKey();
			if(null == psnAndEmail){
				continue;
			}
			String[] psnAndEmails = psnAndEmail.split("@@");
			if(null == psnAndEmails || psnAndEmails.length <2 || null == psnAndEmails[1]){
				continue;
			}
			sb.setLength(0);
			sb.append("<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>【員工考勤異常提醒】</title>")
			.append("</head><body><h4>【若於月底前未完成考勤異常申請，將一律以曠職扣薪論。】")
			.append("</h4><table border=\"1\" cellspacing=\"0\" ")
			.append("<tr><td>日期</td><td>員工號</td><td>姓名</td><td>出勤狀態</td></tr>");
			
			//邮件接收人地址数组
			String[] mailAddress = {psnAndEmails[1]};
			if(entry!=null && entry.getValue() != null && entry.getValue().size() > 0){
				for(MsgDTO msg : entry.getValue()){
					sb.append("<tr align=\"center\">");
					
					sb.append("<td>").append(msg.getCalendar()).append("</td>");
					sb.append("<td>").append(msg.getPsnCode()).append("</td>");
					sb.append("<td>").append(msg.getName()).append("</td>");
					sb.append("<td>").append(msg.getState()).append("</td>");
					
					sb.append("</tr>");
				}
			}
			sb.append("</table></body></html>");
			
			
			MessageSender.sendHtmlEmail(mailAddress, subject, sb.toString(), null);
			
		}
		
		
		
	}
	/**
	 * 查找异常记录
	 * @param pk_orgs
	 * @param num2Today 截止到系統日前的日數   參數值 1-31
	 * @param numOfRange 發送天數範圍  參數值 1-31
	 * @author Ares.Tank
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private List<MsgDTO> findWarningRecond(String[] pk_orgs, Integer num2Today, Integer numOfRange) 
			throws BusinessException{
		InSQLCreator insql = new InSQLCreator();
		String orgsInSql = insql.getInSQL(pk_orgs);
		List<MsgDTO> result = new ArrayList<>();
		if(null != pk_orgs && pk_orgs.length > 0){
			
			
			//處理時間
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DAY_OF_MONTH, 0-num2Today);
			UFLiteralDate endDate = new UFLiteralDate(calendar.getTime());
			//結束時間str:
			String endDateStr = endDate.getYear() + "-" 
				+ endDate.getStrMonth() + "-" 
				+ endDate.getStrDay();
			calendar.add(Calendar.DAY_OF_MONTH, 0-numOfRange);
			//開始時間
			UFLiteralDate startDate = new UFLiteralDate(calendar.getTime());
			String startDateStr = startDate.getYear() + "-" 
					+ startDate.getStrMonth() + "-" 
					+ startDate.getStrDay();
			
			//搜索异常考勤人员以及部门主管,以及上级部门主管
			String sqlStr = " SELECT timedt.calendar, psn.code, psn.name, timedt.tbmstatus, psn.pk_psndoc, deptp.principal pkprincipal, "
							+ " deptup.principal pkupprincipal, psn.email psnemail, pripsn.email priemail, uppripsn.email uppripsnemail "
							+ " FROM tbm_timedata timedt "
							+ " LEFT JOIN bd_psndoc psn ON psn.pk_psndoc = timedt.pk_psndoc "
							+ " left join tbm_psndoc  tbmpsn on (tbmpsn.enddate >= timedt.calendar "
							+ " and timedt.calendar >=tbmpsn.begindate and tbmpsn.dr = 0 and psn.pk_psndoc = tbmpsn.pk_psndoc) "
							+ " left join hi_psnjob job on job.pk_psnjob = tbmpsn.pk_psnjob "
							+ " left join org_dept deptp on deptp.pk_dept = job.pk_dept "
							+ " left join org_dept deptup on deptup.pk_dept = deptp.pk_fatherorg " 
							+ " left join bd_psndoc pripsn on pripsn.pk_psndoc = deptp.principal "
							+ " left join bd_psndoc uppripsn on uppripsn.pk_psndoc = deptup.principal "
							+ " left join tbm_period period on ( period.begindate <= timedt.calendar "
							+ " and timedt.calendar <= period.enddate and period.pk_org = job.pk_org and period.dr = 0) "
							+ " WHERE timedt.calendar >= '"+startDateStr+"' "
							+ " AND timedt.calendar <='"+endDateStr+"' "
							+ " AND job.pk_org IN( "+orgsInSql+" ) "
							+ " AND ISNULL(timedt.tbmstatus,'') <> '' "
							+ " and period.sealflag = 'N' "
							+ " AND timedt.dr=0 order by timedt.calendar ";
			List<Map<String,Object>> qResult 
							= (List<Map<String, Object>>)getBaseDAO().executeQuery(sqlStr, new MapListProcessor());
			if(null != qResult && qResult.size() > 0){
				for(Map<String,Object> rowMap : qResult){
					if(rowMap == null){
						continue;
					}
					MsgDTO msg = new MsgDTO();
					//日期
					msg.setCalendar(new UFLiteralDate((String)rowMap.get("calendar")));
					//员工号
					msg.setPsnCode((String)rowMap.get("code"));
					//姓名
					msg.setName((String)rowMap.get("name"));
					//考勤异常类型
					msg.setState((String)rowMap.get("tbmstatus"));
					//员工pk
					msg.setPkPsndoc((String)rowMap.get("pk_psndoc"));
					//主管pk
					msg.setPkPrincipal((String)rowMap.get("pkprincipal"));
					//上级主管pk
					msg.setPkUpPrincipal((String)rowMap.get("pkupprincipal"));
					//员工pk
					msg.setPsnEmail((String)rowMap.get("psnemail"));
					//主管pk
					msg.setPrincipalEmail((String)rowMap.get("priemail"));
					//上级主管pk
					msg.setUpPrincipalEmail((String)rowMap.get("uppripsnemail"));
					result.add(msg);
				}
			}
			
		}else{
			throw new BusinessException("獲取提醒組織失敗,請檢查輸入組織");
		}
		return result;
		
		
	}

	public BaseDAO getBaseDAO() {
		if(null == baseDAO){
			baseDAO = new BaseDAO();
		}
		return baseDAO;
	}
	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}

}
//很明顯,這是一個臨時DTO
class MsgDTO{
	//日期
	private UFLiteralDate calendar;
	//員工號
	private String psnCode;
	// 員工姓名
	private String name;
	//出勤狀態
	private String state;
	//員工pk
	private String pkPsndoc;
	//部門主管pk
	private String pkPrincipal;
	//上級部門主管pk
	private String pkUpPrincipal;
	//员工email
	private String psnEmail;
	//部门主管email
	private String principalEmail;
	//上级部门主管email
	private String upPrincipalEmail;
	
	public UFLiteralDate getCalendar() {
		return calendar;
	}
	public void setCalendar(UFLiteralDate calendar) {
		this.calendar = calendar;
	}
	public String getPsnCode() {
		return psnCode;
	}
	public void setPsnCode(String psnCode) {
		this.psnCode = psnCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPkPrincipal() {
		return pkPrincipal;
	}
	public void setPkPrincipal(String pkPrincipal) {
		this.pkPrincipal = pkPrincipal;
	}
	public String getPkUpPrincipal() {
		return pkUpPrincipal;
	}
	public void setPkUpPrincipal(String pkUpPrincipal) {
		this.pkUpPrincipal = pkUpPrincipal;
	}
	public String getPkPsndoc() {
		return pkPsndoc;
	}
	public void setPkPsndoc(String pkPsndoc) {
		this.pkPsndoc = pkPsndoc;
	}
	public String getPrincipalEmail() {
		return principalEmail;
	}
	public void setPrincipalEmail(String principalEmail) {
		this.principalEmail = principalEmail;
	}
	public String getUpPrincipalEmail() {
		return upPrincipalEmail;
	}
	public void setUpPrincipalEmail(String upPrincipalEmail) {
		this.upPrincipalEmail = upPrincipalEmail;
	}
	public String getPsnEmail() {
		return psnEmail;
	}
	public void setPsnEmail(String psnEmail) {
		this.psnEmail = psnEmail;
	}

}














