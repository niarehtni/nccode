package nc.hihk.hrta.vo.importovertime;

import java.io.Serializable;
import java.util.TimeZone;

import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.ta.overtime.OvertimeRegVO;

/**
 * @author xiepch
 * 加班登记导入Excel数据的vo类
 * */
public class ImportVO implements Serializable{
	private static final long serialVersionUID = 1L;
	private int row;//行号信息
	private String psncode;//人员编号
	private String psnzhname;//人员中文姓名
	private String psnenname;//人员英文姓名
	private String overtimetype;//人员加班类型
	private String torest;//是否抵休
	private String voerdate;//加班日期
	private String projectname;//专案名称
	private String voertimebegin;//加班开始时间
	private String voertimeend;//加班结束时间
	
	private boolean isFail = false;//是否导入失败
	private String errmsg;//错误信息
	private String pk_psndoc;//人员主键
	private String pk_project;//专案主键
	private nc.vo.pub.lang.UFDateTime overtimebegintime;//加班开始时间
	private nc.vo.pub.lang.UFDateTime overtimeendtime;//加班结束时间
	private String pk_group;//当前主组织
	private String pk_org;//当前主组织
	private UFBoolean istorest;//是否转调休
	private String pk_overtimetype;//人员加班类型主键
	private String pk_overtimetypecopy;//人员加班类型Copy主键
	private TimeZone clientTimeZone;
    private OvertimeRegVO[] returnTimeRegVOs;//保存成功返回的加班登记VOs
	
	@Override
	public String toString() {
		String info = "row["+row+"]"+"errmsg["+errmsg+"]"+"psncode["+psncode+"]"+"psnzhname["+psnzhname+"]"+"psnenname["+psnenname+"]"
				+"overtimetype["+overtimetype+"]"+"torest["+torest+"]"+"voerdate["+voerdate+"]"+"projectname["+projectname+"]"
				+"voertimebegin["+voertimebegin+"]"+"voertimeend["+voertimeend+"]";
		return info;
	}
	/**
	 * 校验string是否为空
	 * @param str
	 * @return
	 */
	private boolean isNotNull(String str){
		if(str == null || "".equals(str) || "".equals(str.trim())){
			return false;
		}
		return true;
	}
	/**
	 * 判断导入的行是不是空行
	 * @return
	 */
	public boolean isEmpty(){
		if(!isNotNull(psncode) && !isNotNull(psnzhname) && !isNotNull(psnenname) 
				&& !isNotNull(overtimetype) &&!isNotNull(torest) &&!isNotNull(torest) 
				&&!isNotNull(voerdate) &&!isNotNull(projectname) &&!isNotNull(voertimebegin) &&!isNotNull(voertimeend)){
			return true;
		}
		return false;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public String getPsncode() {
		return psncode;
	}
	public void setPsncode(String psncode) {
		if(!isNotNull(psncode)){
			setErrmsg("人员编码为空");
		}
		this.psncode = psncode;
	}
	public String getPsnzhname() {
		return psnzhname;
	}
	public void setPsnzhname(String psnzhname) {
		if(!isNotNull(psnzhname))
			setErrmsg("中文名称为空");
		this.psnzhname = psnzhname;
	}
	public String getPsnenname() {
		return psnenname;
	}
	public void setPsnenname(String psnenname) {
//		if(psnenname == null)
//			setErrmsg("英文名称为空");
		this.psnenname = psnenname;
	}
	public String getOvertimetype() {
		return overtimetype;
	}
	public void setOvertimetype(String overtimetype) {
		if(!isNotNull(overtimetype)){
			setErrmsg("加班类别为空");
		}
		this.overtimetype = overtimetype;
	}
	public String getTorest() {
		return torest;
	}
	public void setTorest(String torest) {
		if(!isNotNull(torest))
			setIstorest(UFBoolean.FALSE);
//			setErrmsg("是否抵休为空");
		else if("Y".equals(torest) || "是".equals(torest)){
			setIstorest(UFBoolean.TRUE);
		}else {
			setIstorest(UFBoolean.FALSE);
		}
		this.torest = torest;
	}
	public String getVoerdate() {
		return voerdate;
	}
	public void setVoerdate(String voerdate) {
		if(!isNotNull(voerdate))
			setErrmsg("加班日期为空");
		this.voerdate = voerdate;
	}
	public String getProjectname() {
		return projectname;
	}
	public void setProjectname(String projectname) {
		if(!isNotNull(projectname))
			setErrmsg("专案名称为空");
		this.projectname = projectname;
	}
	public String getVoertimebegin() {
		return voertimebegin;
	}
	public void setVoertimebegin(String voertimebegin) {
		if(!isNotNull(voertimebegin))
			setErrmsg("加班时间起为空");
		else if(isNotNull(getVoerdate())){
			if(voertimebegin.length() < 6){
				voertimebegin += ":00";
			}
			try{
				UFDateTime begintime = new UFDateTime(getVoerdate()+" "+voertimebegin);
				setOvertimebegintime(begintime);
			}catch(Exception e){
				setErrmsg("加班日期、加班时间起不符合格式要求");
			}
		}
		this.voertimebegin = voertimebegin;
	}
	public String getVoertimeend() {
		return voertimeend;
	}
	public void setVoertimeend(String voertimeend) {
		if(!isNotNull(voertimeend))
			setErrmsg("加班时间迄为空");
		else if(isNotNull(getVoerdate())){
			if(voertimeend.length() < 6){
				voertimeend += ":00";
			}
			try{
				UFDateTime endtime = new UFDateTime(getVoerdate()+" "+voertimeend);
				setOvertimeendtime(endtime);
			}catch( Exception e){
				setErrmsg("加班日期、加班时间迄不符合格式要求");
			}
		}
		this.voertimeend = voertimeend;
	}
	public boolean isFail() {
		return isFail;
	}
	public void setFail(boolean isFail) {
		this.isFail = isFail;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		isFail = true;
		this.errmsg = (this.errmsg==null? "["+errmsg+"]": this.errmsg+"["+errmsg+"]");
	}
	public String getPk_psndoc() {
		return pk_psndoc;
	}
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}
	public String getPk_project() {
		return pk_project;
	}
	public void setPk_project(String pk_project) {
		this.pk_project = pk_project;
	}
	public nc.vo.pub.lang.UFDateTime getOvertimebegintime() {
		return overtimebegintime;
	}
	public void setOvertimebegintime(nc.vo.pub.lang.UFDateTime overtimebegintime) {
		this.overtimebegintime = overtimebegintime;
	}
	public nc.vo.pub.lang.UFDateTime getOvertimeendtime() {
		return overtimeendtime;
	}
	public void setOvertimeendtime(nc.vo.pub.lang.UFDateTime overtimeendtime) {
		if(overtimeendtime.compareTo(getOvertimebegintime()) <= 0){//结束时间早于等于开始时间
			setErrmsg("加班时间迄早于加班时间起");
		}else
			this.overtimeendtime = overtimeendtime;
	}
	public String getPk_org() {
		return pk_org;
	}
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}
	
	public String getPk_group() {
		return pk_group;
	}
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}
	public String getPk_overtimetype() {
		return pk_overtimetype;
	}
	public void setPk_overtimetype(String pk_overtimetype) {
		this.pk_overtimetype = pk_overtimetype;
	}
	public String getPk_overtimetypecopy() {
		return pk_overtimetypecopy;
	}
	public void setPk_overtimetypecopy(String pk_overtimetypecopy) {
		this.pk_overtimetypecopy = pk_overtimetypecopy;
	}
	public TimeZone getClientTimeZone() {
		return clientTimeZone;
	}
	public void setClientTimeZone(TimeZone clientTimeZone) {
		this.clientTimeZone = clientTimeZone;
	}
	public UFBoolean getIstorest() {
		return istorest;
	}
	public void setIstorest(UFBoolean istorest) {
		this.istorest = istorest;
	}
	public OvertimeRegVO[] getReturnTimeRegVOs() {
		return returnTimeRegVOs;
	}
	public void setReturnTimeRegVOs(OvertimeRegVO[] returnTimeRegVOs) {
		this.returnTimeRegVOs = returnTimeRegVOs;
	}
}
