package nc.hihk.hrta.vo.importovertime;

import java.io.Serializable;
import java.util.TimeZone;

import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.ta.overtime.OvertimeRegVO;

/**
 * @author xiepch
 * �Ӱ�Ǽǵ���Excel���ݵ�vo��
 * */
public class ImportVO implements Serializable{
	private static final long serialVersionUID = 1L;
	private int row;//�к���Ϣ
	private String psncode;//��Ա���
	private String psnzhname;//��Ա��������
	private String psnenname;//��ԱӢ������
	private String overtimetype;//��Ա�Ӱ�����
	private String torest;//�Ƿ����
	private String voerdate;//�Ӱ�����
	private String projectname;//ר������
	private String voertimebegin;//�Ӱ࿪ʼʱ��
	private String voertimeend;//�Ӱ����ʱ��
	
	private boolean isFail = false;//�Ƿ���ʧ��
	private String errmsg;//������Ϣ
	private String pk_psndoc;//��Ա����
	private String pk_project;//ר������
	private nc.vo.pub.lang.UFDateTime overtimebegintime;//�Ӱ࿪ʼʱ��
	private nc.vo.pub.lang.UFDateTime overtimeendtime;//�Ӱ����ʱ��
	private String pk_group;//��ǰ����֯
	private String pk_org;//��ǰ����֯
	private UFBoolean istorest;//�Ƿ�ת����
	private String pk_overtimetype;//��Ա�Ӱ���������
	private String pk_overtimetypecopy;//��Ա�Ӱ�����Copy����
	private TimeZone clientTimeZone;
    private OvertimeRegVO[] returnTimeRegVOs;//����ɹ����صļӰ�Ǽ�VOs
	
	@Override
	public String toString() {
		String info = "row["+row+"]"+"errmsg["+errmsg+"]"+"psncode["+psncode+"]"+"psnzhname["+psnzhname+"]"+"psnenname["+psnenname+"]"
				+"overtimetype["+overtimetype+"]"+"torest["+torest+"]"+"voerdate["+voerdate+"]"+"projectname["+projectname+"]"
				+"voertimebegin["+voertimebegin+"]"+"voertimeend["+voertimeend+"]";
		return info;
	}
	/**
	 * У��string�Ƿ�Ϊ��
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
	 * �жϵ�������ǲ��ǿ���
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
			setErrmsg("��Ա����Ϊ��");
		}
		this.psncode = psncode;
	}
	public String getPsnzhname() {
		return psnzhname;
	}
	public void setPsnzhname(String psnzhname) {
		if(!isNotNull(psnzhname))
			setErrmsg("��������Ϊ��");
		this.psnzhname = psnzhname;
	}
	public String getPsnenname() {
		return psnenname;
	}
	public void setPsnenname(String psnenname) {
//		if(psnenname == null)
//			setErrmsg("Ӣ������Ϊ��");
		this.psnenname = psnenname;
	}
	public String getOvertimetype() {
		return overtimetype;
	}
	public void setOvertimetype(String overtimetype) {
		if(!isNotNull(overtimetype)){
			setErrmsg("�Ӱ����Ϊ��");
		}
		this.overtimetype = overtimetype;
	}
	public String getTorest() {
		return torest;
	}
	public void setTorest(String torest) {
		if(!isNotNull(torest))
			setIstorest(UFBoolean.FALSE);
//			setErrmsg("�Ƿ����Ϊ��");
		else if("Y".equals(torest) || "��".equals(torest)){
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
			setErrmsg("�Ӱ�����Ϊ��");
		this.voerdate = voerdate;
	}
	public String getProjectname() {
		return projectname;
	}
	public void setProjectname(String projectname) {
		if(!isNotNull(projectname))
			setErrmsg("ר������Ϊ��");
		this.projectname = projectname;
	}
	public String getVoertimebegin() {
		return voertimebegin;
	}
	public void setVoertimebegin(String voertimebegin) {
		if(!isNotNull(voertimebegin))
			setErrmsg("�Ӱ�ʱ����Ϊ��");
		else if(isNotNull(getVoerdate())){
			if(voertimebegin.length() < 6){
				voertimebegin += ":00";
			}
			try{
				UFDateTime begintime = new UFDateTime(getVoerdate()+" "+voertimebegin);
				setOvertimebegintime(begintime);
			}catch(Exception e){
				setErrmsg("�Ӱ����ڡ��Ӱ�ʱ���𲻷��ϸ�ʽҪ��");
			}
		}
		this.voertimebegin = voertimebegin;
	}
	public String getVoertimeend() {
		return voertimeend;
	}
	public void setVoertimeend(String voertimeend) {
		if(!isNotNull(voertimeend))
			setErrmsg("�Ӱ�ʱ����Ϊ��");
		else if(isNotNull(getVoerdate())){
			if(voertimeend.length() < 6){
				voertimeend += ":00";
			}
			try{
				UFDateTime endtime = new UFDateTime(getVoerdate()+" "+voertimeend);
				setOvertimeendtime(endtime);
			}catch( Exception e){
				setErrmsg("�Ӱ����ڡ��Ӱ�ʱ���������ϸ�ʽҪ��");
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
		if(overtimeendtime.compareTo(getOvertimebegintime()) <= 0){//����ʱ�����ڵ��ڿ�ʼʱ��
			setErrmsg("�Ӱ�ʱ�������ڼӰ�ʱ����");
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
