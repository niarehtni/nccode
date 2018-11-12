package nc.vo.wa.paydata;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;

public class DaysalaryRecordVO extends SuperVO{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5356175483044781244L;
	private String pk_daysalaryrecord;
	private String BackgroundWorkPlugin;
	private String pk_hrorg;
	private UFLiteralDate salarydate;
	private Integer daysalarycount;
	private UFBoolean issuccess;
	private String failreason;
	private UFDateTime ts;
	private Integer dr=Integer.valueOf(0);
	
	
	
	public String getPk_daysalaryrecord() {
		return pk_daysalaryrecord;
	}

	public void setPk_daysalaryrecord(String pk_daysalaryrecord) {
		this.pk_daysalaryrecord = pk_daysalaryrecord;
	}

	public String getBackgroundWorkPlugin() {
		return BackgroundWorkPlugin;
	}

	public void setBackgroundWorkPlugin(String backgroundWorkPlugin) {
		BackgroundWorkPlugin = backgroundWorkPlugin;
	}

	public String getPk_hrorg() {
		return pk_hrorg;
	}

	public void setPk_hrorg(String pk_hrorg) {
		this.pk_hrorg = pk_hrorg;
	}

	public UFLiteralDate getSalarydate() {
		return salarydate;
	}

	public void setSalarydate(UFLiteralDate salarydate) {
		this.salarydate = salarydate;
	}

	public Integer getDaysalarycount() {
		return daysalarycount;
	}

	public void setDaysalarycount(Integer daysalarycount) {
		this.daysalarycount = daysalarycount;
	}

	public UFBoolean getIssuccess() {
		return issuccess;
	}

	public void setIssuccess(UFBoolean issuccess) {
		this.issuccess = issuccess;
	}

	public String getFailreason() {
		return failreason;
	}

	public void setFailreason(String failreason) {
		this.failreason = failreason;
	}

	public UFDateTime getTs() {
		return ts;
	}

	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:${vmObject.createdDate}
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
	  return "pk_daysalaryrecord";
	}
  
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:${vmObject.createdDate}
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "wa_daysalaryrecord";
	}    
	
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:${vmObject.createdDate}
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "wa_daysalaryrecord";
	}
}