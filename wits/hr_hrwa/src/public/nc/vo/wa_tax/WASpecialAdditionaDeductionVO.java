package nc.vo.wa_tax;

import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFLiteralDate;

public class WASpecialAdditionaDeductionVO extends SuperVO  implements Comparable<WASpecialAdditionaDeductionVO>{
	private static final long serialVersionUID = -1L;
	private String pk_deduction;
	private java.lang.String pk_psndoc;
	private String pk_psnjob;
	private java.lang.String userid;
	private java.lang.String type;
	private java.lang.String typename;
	private java.lang.String deptname;
	private UFLiteralDate apply_date;//申请日期
	private String begin_year;
	private String begin_period;
	private String end_year;
	private String end_period;
	private java.lang.Integer circle_period = 0;//0:month 1:period
	private Integer amount;
	private String amountimp;
	private String idcard;
	private nc.vo.pub.lang.UFDateTime ts;
	private java.lang.String pk_source;
	private java.lang.String def1;
	private java.lang.String def2;
	private java.lang.String def3;
	private java.lang.String def4;
	private java.lang.String def5;
	private java.lang.String creator;
	private nc.vo.pub.lang.UFDateTime creationtime;
	private java.lang.String modifier;
	private nc.vo.pub.lang.UFDateTime modifiedtime;
	private java.lang.Integer dr = 0;
	private String source;
	private String psncode;
	private String psnname;

	private String reason;
	private String importInfo;
	/** 表示是哪个项目出现的问题 */
	private Integer error_flag;
	public static final String PK_PSNJOB = "pk_psnjob";
	public static final String PK_PSNDOC = "pk_psndoc";
	public static final String USERID = "userid";
	public static final String TYPE = "type";
	public static final String TYPENAME = "typename";
	public static final String BEGIN_YEAR = "begin_year";
	public static final String END_YEAR = "end_year";
	public static final String BEGIN_PERIOD = "begin_period";
	public static final String END_PERIOD = "end_period";
	public static final String PK_DEDUCTION = "pk_deduction";
	public static final String CIRCLE_PERIOD = "circle_period";
	public static final String AMOUNT = "amount";
	public static final String TS = "ts";
	public static final String PK_SOURCE = "pk_source";
	public static final String IDCARD = "idcard";
	public static final String DEF1 = "def1";
	public static final String DEF2 = "def2";
	public static final String DEF3 = "def3";
	public static final String DEF4 = "def4";
	public static final String DEF5 = "def5";
	public static final String SOURCE = "source";
	public static final String AMOUNTIMP = "amountimp";
	public static final String DEPTNAME = "deptname";
	/** 数据导入使用属性 */
	public static final String REASON = "reason";
	public static final String IMPORTINFO = "importInfo";
	public static final String ERROR_FLAG = "error_flag";

	public java.lang.String getDeptname() {
		return deptname;
	}

	public void setDeptname(java.lang.String deptname) {
		this.deptname = deptname;
	}

	public String getAmountimp() {
		return amountimp;
	}

	public void setAmountimp(String amountimp) {
		this.amountimp = amountimp;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getImportInfo() {
		return importInfo;
	}

	public void setImportInfo(String importInfo) {
		this.importInfo = importInfo;
	}

	public Integer getError_flag() {
		return error_flag;
	}

	public void setError_flag(Integer error_flag) {
		this.error_flag = error_flag;
	}

	public java.lang.String getCreator() {
		return creator;
	}

	public void setCreator(java.lang.String creator) {
		this.creator = creator;
	}

	public nc.vo.pub.lang.UFDateTime getCreationtime() {
		return creationtime;
	}

	public void setCreationtime(nc.vo.pub.lang.UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	public java.lang.String getModifier() {
		return modifier;
	}

	public void setModifier(java.lang.String modifier) {
		this.modifier = modifier;
	}

	public nc.vo.pub.lang.UFDateTime getModifiedtime() {
		return modifiedtime;
	}

	public void setModifiedtime(nc.vo.pub.lang.UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	public java.lang.Integer getDr() {
		return dr;
	}

	public void setDr(java.lang.Integer dr) {
		this.dr = dr;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getPsncode() {
		return psncode;
	}

	public void setPsncode(String psncode) {
		this.psncode = psncode;
	}

	public String getPsnname() {
		return psnname;
	}

	public void setPsnname(String psnname) {
		this.psnname = psnname;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getPk_deduction() {
		return pk_deduction;
	}

	public void setPk_deduction(String pk_deduction) {
		this.pk_deduction = pk_deduction;
	}

	public String getPk_psnjob() {
		return pk_psnjob;
	}

	public void setPk_psnjob(String pk_psnjob) {
		this.pk_psnjob = pk_psnjob;
	}

	public java.lang.String getPk_psndoc() {
		return pk_psndoc;
	}

	public void setPk_psndoc(java.lang.String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	public java.lang.String getUserid() {
		return userid;
	}

	public void setUserid(java.lang.String userid) {
		this.userid = userid;
	}

	public java.lang.String getType() {
		return type;
	}

	public void setType(java.lang.String type) {
		this.type = type;
	}

	public java.lang.String getTypename() {
		return typename;
	}

	public void setTypename(java.lang.String typename) {
		this.typename = typename;
	}
 
	public UFLiteralDate getApply_date() {
		return apply_date;
	}

	public void setApply_date(UFLiteralDate apply_date2) {
		this.apply_date = apply_date2;
	}

	public String getBegin_year() {
		return begin_year;
	}

	public void setBegin_year(String begin_year) {
		this.begin_year = begin_year;
	}

	public String getBegin_period() {
		return begin_period;
	}

	public void setBegin_period(String begin_period) {
		this.begin_period = begin_period;
	}

	public String getEnd_year() {
		return end_year;
	}

	public void setEnd_year(String end_year) {
		this.end_year = end_year;
	}

	public String getEnd_period() {
		return end_period;
	}

	public void setEnd_period(String end_period) {
		this.end_period = end_period;
	}

	public java.lang.Integer getCircle_period() {
		return circle_period;
	}

	public void setCircle_period(java.lang.Integer circle_period) {
		this.circle_period = circle_period;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public java.lang.String getPk_source() {
		return pk_source;
	}

	public void setPk_source(java.lang.String pk_source) {
		this.pk_source = pk_source;
	}

	public java.lang.String getDef1() {
		return def1;
	}

	public void setDef1(java.lang.String def1) {
		this.def1 = def1;
	}

	public java.lang.String getDef2() {
		return def2;
	}

	public void setDef2(java.lang.String def2) {
		this.def2 = def2;
	}

	public java.lang.String getDef3() {
		return def3;
	}

	public void setDef3(java.lang.String def3) {
		this.def3 = def3;
	}

	public java.lang.String getDef4() {
		return def4;
	}

	public void setDef4(java.lang.String def4) {
		this.def4 = def4;
	}

	public java.lang.String getDef5() {
		return def5;
	}

	public void setDef5(java.lang.String def5) {
		this.def5 = def5;
	}

	public java.lang.String getParentPKFieldName() {
		return "pk_deduction";
	}   
 
	public java.lang.String getPKFieldName() {
	  return "pk_deduction";
	}
 
	public java.lang.String getTableName() {
		return "wa_deduction";
	}    
 
	public static java.lang.String getDefaultTableName() {
		return "wa_deduction";
	}    
    
    public nc.vo.pub.lang.UFDateTime getTs() {
		return ts;
	}

	public void setTs(nc.vo.pub.lang.UFDateTime ts) {
		this.ts = ts;
	}

	/**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2009-08-25 20:27:43
	  */
     public WASpecialAdditionaDeductionVO() {
		super();	
	}

	@Override
	public int compareTo(WASpecialAdditionaDeductionVO o) {
		// TODO Auto-generated method stub
		int intpsnCode = psncode.compareTo(o.getPsncode());
		int intitem = typename.compareTo(o.getTypename());
		int intbegindate = this.getBegin_period().compareTo(o.getBegin_period());

		if (intpsnCode == 0)
		{
			if (intitem == 0)
			{
				if (intbegindate == 0)
				{
					return 0;
				}
				else if (intbegindate < 0)
				{
					return -1;
				}
			}
			else if (intitem < 0)
			{
				return -1;
			}
		}
		else if (intpsnCode < 0)
		{
			return -1;
		}

		return 1;
	}    
}
