package nc.vo.org;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;














public class JobVO extends SuperVO
{
  private String pk_job;
  private String jobcode;
  private String jobname;
  private String jobname2;
  private String jobname3;
  private String jobname4;
  private String jobname5;
  private String jobname6;
  private String jobdesc;
  private String pk_jobtype;
  private String pk_grade_source;
  private String pk_org;
  private String reqedu;
  private String reqsex;
  private String reqyold;
  private String reqpro;
  private String reqexp;
  private String reqtlimit;
  private String reqother;
  private String originaldocid;
  private UFBoolean redefineflag;
  private Integer enablestate = Integer.valueOf(1);





  private Integer dataoriginflag = Integer.valueOf(0);

  private UFBoolean pvtjobgrade = UFBoolean.FALSE;
  private Integer dr = Integer.valueOf(0);
private UFDateTime creationtime;
private String modifier;
private String creator;
private String pk_group;
private UFDateTime modifiedtime;
private UFDateTime ts;
private String defaultjobrank;

private UFDouble				parttimejob;
private UFDouble				principaljob;



































  public String getPk_job()
  {
    return this.pk_job;
  }




  public void setPk_job(String newPk_job)
  {
    this.pk_job = newPk_job;
  }




  public String getJobcode()
  {
    return this.jobcode;
  }




  public void setJobcode(String newJobcode)
  {
    this.jobcode = newJobcode;
  }




  public String getJobname()
  {
    return this.jobname;
  }




  public void setJobname(String newJobname)
  {
    this.jobname = newJobname;
  }




  public String getJobname2()
  {
    return this.jobname2;
  }




  public void setJobname2(String newJobname2)
  {
    this.jobname2 = newJobname2;
  }




  public String getJobname3()
  {
    return this.jobname3;
  }




  public void setJobname3(String newJobname3)
  {
    this.jobname3 = newJobname3;
  }




  public String getJobname4()
  {
    return this.jobname4;
  }




  public void setJobname4(String newJobname4)
  {
    this.jobname4 = newJobname4;
  }




  public String getJobname5()
  {
    return this.jobname5;
  }




  public void setJobname5(String newJobname5)
  {
    this.jobname5 = newJobname5;
  }




  public String getJobname6()
  {
    return this.jobname6;
  }




  public void setJobname6(String newJobname6)
  {
    this.jobname6 = newJobname6;
  }




  public String getJobdesc()
  {
    return this.jobdesc;
  }




  public void setJobdesc(String newJobdesc)
  {
    this.jobdesc = newJobdesc;
  }




  public String getPk_jobtype()
  {
    return this.pk_jobtype;
  }




  public void setPk_jobtype(String newPk_jobtype)
  {
    this.pk_jobtype = newPk_jobtype;
  }




  public String getPk_grade_source()
  {
    return this.pk_grade_source;
  }




  public void setPk_grade_source(String newPk_grade_source)
  {
    this.pk_grade_source = newPk_grade_source;
  }




  public String getPk_org()
  {
    return this.pk_org;
  }




  public void setPk_org(String newPk_org)
  {
    this.pk_org = newPk_org;
  }




  public String getReqedu()
  {
    return this.reqedu;
  }




  public void setReqedu(String newReqedu)
  {
    this.reqedu = newReqedu;
  }




  public String getReqsex()
  {
    return this.reqsex;
  }




  public void setReqsex(String newReqsex)
  {
    this.reqsex = newReqsex;
  }




  public String getReqyold()
  {
    return this.reqyold;
  }




  public void setReqyold(String newReqyold)
  {
    this.reqyold = newReqyold;
  }




  public String getReqpro()
  {
    return this.reqpro;
  }




  public void setReqpro(String newReqpro)
  {
    this.reqpro = newReqpro;
  }




  public String getReqexp()
  {
    return this.reqexp;
  }




  public void setReqexp(String newReqexp)
  {
    this.reqexp = newReqexp;
  }




  public String getReqtlimit()
  {
    return this.reqtlimit;
  }




  public void setReqtlimit(String newReqtlimit)
  {
    this.reqtlimit = newReqtlimit;
  }




  public String getReqother()
  {
    return this.reqother;
  }




  public void setReqother(String newReqother)
  {
    this.reqother = newReqother;
  }




  public String getOriginaldocid()
  {
    return this.originaldocid;
  }




  public void setOriginaldocid(String newOriginaldocid)
  {
    this.originaldocid = newOriginaldocid;
  }




  public UFBoolean getRedefineflag()
  {
    return this.redefineflag;
  }




  public void setRedefineflag(UFBoolean newRedefineflag)
  {
    this.redefineflag = newRedefineflag;
  }




  public Integer getEnablestate()
  {
    return this.enablestate;
  }




  public void setEnablestate(Integer newEnablestate)
  {
    this.enablestate = newEnablestate;
  }




  public String getCreator()
  {
    return this.creator;
  }




  public void setCreator(String newCreator)
  {
    this.creator = newCreator;
  }




  public UFDateTime getCreationtime()
  {
    return this.creationtime;
  }




  public void setCreationtime(UFDateTime newCreationtime)
  {
    this.creationtime = newCreationtime;
  }




  public String getModifier()
  {
    return this.modifier;
  }




  public void setModifier(String newModifier)
  {
    this.modifier = newModifier;
  }




  public UFDateTime getModifiedtime()
  {
    return this.modifiedtime;
  }




  public void setModifiedtime(UFDateTime newModifiedtime)
  {
    this.modifiedtime = newModifiedtime;
  }




  public String getPk_group()
  {
    return this.pk_group;
  }




  public void setPk_group(String newPk_group)
  {
    this.pk_group = newPk_group;
  }




  public Integer getDataoriginflag()
  {
    return this.dataoriginflag;
  }




  public void setDataoriginflag(Integer newDataoriginflag)
  {
    this.dataoriginflag = newDataoriginflag;
  }




  public String getDefaultjobrank()
  {
    return this.defaultjobrank;
  }




  public void setDefaultjobrank(String newDefaultjobrank)
  {
    this.defaultjobrank = newDefaultjobrank;
  }




  public UFBoolean getPvtjobgrade()
  {
    return this.pvtjobgrade;
  }




  public void setPvtjobgrade(UFBoolean newPvtjobgrade)
  {
    this.pvtjobgrade = newPvtjobgrade;
  }




  public Integer getDr()
  {
    return this.dr;
  }




  public void setDr(Integer newDr)
  {
    this.dr = newDr;
  }




  public UFDateTime getTs()
  {
    return this.ts;
  }




  public void setTs(UFDateTime newTs)
  {
    this.ts = newTs;
  }






  public String getParentPKFieldName()
  {
    return null;
  }






  public String getPKFieldName()
  {
    return "pk_job";
  }






  public String getTableName()
  {
    return "om_job";
  }






  public static String getDefaultTableName()
  {
    return "om_job";
  }
  
  
  public UFDouble getParttimejob()
	{
		return parttimejob;
	}
	
	
	public void setParttimejob(UFDouble newparttimejob)
	{
		this.parttimejob = newparttimejob;
	}

	public UFDouble getPrincipaljob()
	{
		return principaljob;
	}

	public void setPrincipaljob(UFDouble newprincipaljob)
	{
		this.principaljob = newprincipaljob;
	}
}

