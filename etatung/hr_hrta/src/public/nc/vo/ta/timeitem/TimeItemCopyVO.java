package nc.vo.ta.timeitem;

import nc.vo.ml.MultiLangUtil;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.annotation.IDColumn;
import nc.vo.ta.annotation.Table;
import nc.vo.ta.basedoc.IBasedocCopyVO;
import nc.vo.ta.basedoc.annotation.DefVOPkFieldName;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

@DefVOPkFieldName(fieldName="pk_timeitem")
@Table(tableName="tbm_timeitemcopy")
@IDColumn(idColumn="pk_timeitemcopy")
public abstract class TimeItemCopyVO
  extends SuperVO
  implements IBasedocCopyVO<TimeItemVO>
{
  public static final String[] NOTDELETECODEARRAY_LEAVE = { "105", "110", "111" };
  public static final String[] NOTDELETECODEARRAY_OVERTIME = { "201", "202", "203" };
  public static final String OVERTIMETYPE_GX = "1002Z710000000021ZLV";
  public static final String OVERTIMETYPE_NORMAL = "1002Z710000000021ZLT";
  public static final String OVERTIMETYPE_HOLIDAY = "1002Z710000000021ZLX";
  public static final String LEAVETYPE_OVERTOREST = "1002Z710000000021ZM1";
  public static final String LEAVETYPE_LACTATION = "1002Z710000000021ZM3";
  public static final int LEAVE_TYPE = 0;
  public static final int OVERTIME_TYPE = 1;
  public static final int AWAY_TYPE = 2;
  public static final int SHUTDOWN_TYPE = 3;
  public static final int TIMEITEMUNIT_DAY = 0;
  public static final int TIMEITEMUNIT_HOUR = 1;
  public static final int ROUNDMODE_UP = 0;
  public static final int ROUNDMODE_DOWN = 1;
  public static final int ROUNDMODE_MID = 2;
  public static final int LEAVESETPERIOD_MONTH = 0;
  public static final int LEAVESETPERIOD_YEAR = 1;
  public static final int LEAVESETPERIOD_DATE = 2;
  
  //BEGIN 张恒    将65年资起算日的整合到63   2018/8/28
  //ssx added on 2018-03-16
  // for changes of Company age
  public static final int LEAVESETPERIOD_STARTDATE = 3; // 按年資起算日
  //END 张恒    将65年资起算日的整合到63   2018/8/28
  
  
  public static final int LEAVESETTLEMENT_DROP = 0;
  public static final int LEAVESETTLEMENT_NEXT = 1;
  public static final int LEAVESETTLEMENT_MONEY = 2;
  public static final int LEAVESCALE_YEAR = 0;
  public static final int LEAVESCALE_MONTH = 1;
  public static final int GXCOMTYPE_NOTLEAVE = 0;
  public static final int GXCOMTYPE_TOLEAVE = 1;
  public static final int CONVERTRULE_DAY = 0;
  public static final int CONVERTRULE_TIME = 1;
  public static final int GXCOMTYPE_NOTAWAY = 0;
  public static final int GXCOMTYPE_TOAWAY = 1;
  public static final int CALCULATETYPE_TOHALF = 0;
  public static final int CALCULATETYPE_TODAY = 1;
  public static final String OVERTIMETOLEAVETYPE = "110";
  protected String pk_timeitemcopy;
  protected String pk_timeitem;
  private String pk_group;
  private String pk_org;
  private Integer itemtype;
  private String timeitemnote;
  private Integer timeitemunit;
  private UFBoolean isleavelimit;
  private UFBoolean isrestrictlimit;
  private Integer leavesettlement;
  private Integer leavesetperiod;
  private Integer calculatetype;
  private UFDouble timeunit;
  private Integer roundmode;
  private Integer leavescale;
  private Integer gxcomtype;
  private Integer convertrule;
  private Integer leaveapptimelimit;
  private UFBoolean isleaveapptimelimit;
  private Object formula;
  private Object formulastr;
  private Integer enablestate;
  private Integer defenablestate;
  private UFBoolean issynchronized;
  private String creator;
  private UFDateTime creationtime;
  private String modifier;
  private UFDateTime modifiedtime;
  private UFDouble overtimetorest;
  private Integer dr = Integer.valueOf(0);
  private UFDateTime ts;
  private UFBoolean ispredef;
  private UFBoolean islactation;
  private UFBoolean isinterwt;
  private UFBoolean isleave;
  private Integer leaveextendcount;
  private String pk_dependleavetypes;
  private UFBoolean isleavetransfer;
  private UFBoolean ishrssshow;
  private Integer showorder;
  private String timeitemcode;
  private String timeitemname;
  private String timeitemname2;
  private String timeitemname3;
  private String timeitemname4;
  private String timeitemname5;
  private String timeitemname6;
  private String pk_defgroup;
  private String pk_deforg;
  private UFDateTime defTS;
  public static final String PK_DEPENDLEAVETYPES = "pk_dependleavetypes";
  public static final String LEAVEEXTENDCOUNT = "leaveextendcount";
  public static final String PK_TIMEITEMCOPY = "pk_timeitemcopy";
  public static final String PK_TIMEITEM = "pk_timeitem";
  public static final String PK_GROUP = "pk_group";
  public static final String PK_ORG = "pk_org";
  public static final String ITEMTYPE = "itemtype";
  public static final String TIMEITEMNOTE = "timeitemnote";
  public static final String TIMEITEMUNIT = "timeitemunit";
  public static final String LEAVELIMIT = "leavelimit";
  public static final String LEAVESETTLEMENT = "leavesettlement";
  public static final String LEAVESETPERIOD = "leavesetperiod";
  public static final String CALCULATETYPE = "calculatetype";
  public static final String TIMEUNIT = "timeunit";
  public static final String ROUNDMODE = "roundmode";
  public static final String LEAVESCALE = "leavescale";
  public static final String GXCOMTYPE = "gxcomtype";
  public static final String CONVERTRULE = "convertrule";
  public static final String FORMULA = "formula";
  public static final String FORMULASTR = "formulastr";
  public static final String ENABLESTATE = "enablestate";
  public static final String ISSYNCHRONIZED = "issynchronized";
  public static final String CREATOR = "creator";
  public static final String CREATIONTIME = "creationtime";
  public static final String MODIFIER = "modifier";
  public static final String MODIFIEDTIME = "modifiedtime";
  public static final String OVERTIMETOREST = "overtimetorest";
  public static final String ISPREDEF = "ispredef";
  public static final String ISLACTATION = "islactation";
  public static final String ISLEAVEAPPTIMELIMIT = "isleaveapptimelimit";
  public static final String LEAVEAPPTIMELIMIT = "leaveapptimelimit";
  public static final String ISLEAVE = "isleave";
  public static final String LEAVETOMONTH = "leavetomonth";
  public static final String LEAVETODAY = "leavetoday";
  public static final String ISLEAVETRANSFER = "isleavetransfer";
  public static final String ISHRSSSHOW = "ishrssshow";
  public static final String SHOWORDER = "showorder";
  
  public String getPk_timeitemcopy()
  {
    return this.pk_timeitemcopy;
  }
  
  public void setPk_timeitemcopy(String newPk_timeitemcopy)
  {
    this.pk_timeitemcopy = newPk_timeitemcopy;
  }
  
  public String getPk_timeitem()
  {
    return this.pk_timeitem;
  }
  
  public void setPk_timeitem(String newPk_timeitem)
  {
    this.pk_timeitem = newPk_timeitem;
  }
  
  public String getPk_group()
  {
    return this.pk_group;
  }
  
  public void setPk_group(String newPk_group)
  {
    this.pk_group = newPk_group;
  }
  
  public String getPk_org()
  {
    return this.pk_org;
  }
  
  public void setPk_org(String newPk_org)
  {
    this.pk_org = newPk_org;
  }
  
  public Integer getItemtype()
  {
    return this.itemtype;
  }
  
  public void setItemtype(Integer newItemtype)
  {
    this.itemtype = newItemtype;
  }
  
  public String getTimeitemnote()
  {
    return this.timeitemnote;
  }
  
  public void setTimeitemnote(String newTimeitemnote)
  {
    this.timeitemnote = newTimeitemnote;
  }
  
  public Integer getTimeitemunit()
  {
    return this.timeitemunit;
  }
  
  public void setTimeitemunit(Integer newTimeitemunit)
  {
    this.timeitemunit = newTimeitemunit;
  }
  
  public int getTimeItemUnit()
  {
    return this.timeitemunit == null ? 0 : this.timeitemunit.intValue();
  }
  
  public boolean isLeaveLimit()
  {
    return (this.isleavelimit != null) && (this.isleavelimit.booleanValue());
  }
  
  public UFBoolean getIsLeavelimit()
  {
    return this.isleavelimit;
  }
  
  public void setIsLeavelimit(UFBoolean newLeavelimit)
  {
    this.isleavelimit = newLeavelimit;
  }
  
  public boolean isRestrictLimit()
  {
    return (this.isrestrictlimit != null) && (this.isrestrictlimit.booleanValue());
  }
  
  public UFBoolean getIsRestrictlimit()
  {
    return this.isrestrictlimit;
  }
  
  public void setIsRestrictlimit(UFBoolean newIsrestrictlimit)
  {
    this.isrestrictlimit = newIsrestrictlimit;
  }
  
  public Integer getLeavesettlement()
  {
    return this.leavesettlement;
  }
  
  public void setLeavesettlement(Integer newLeavesettlement)
  {
    this.leavesettlement = newLeavesettlement;
  }
  
  public Integer getLeavesetperiod()
  {
    return this.leavesetperiod;
  }
  
  public void setLeavesetperiod(Integer newLeavesetperiod)
  {
    this.leavesetperiod = newLeavesetperiod;
  }
  
  public Integer getCalculatetype()
  {
    return this.calculatetype;
  }
  
  public void setCalculatetype(Integer newCalculatetype)
  {
    this.calculatetype = newCalculatetype;
  }
  
  public UFDouble getTimeunit()
  {
    return this.timeunit;
  }
  
  public void setTimeunit(UFDouble newTimeunit)
  {
    this.timeunit = newTimeunit;
  }
  
  public double getTimeUnit()
  {
    return this.timeunit == null ? 0.0D : this.timeunit.doubleValue();
  }
  
  public Integer getRoundmode()
  {
    return this.roundmode;
  }
  
  public void setRoundmode(Integer newRoundmode)
  {
    this.roundmode = newRoundmode;
  }
  
  public Integer getLeavescale()
  {
    return this.leavescale;
  }
  
  public void setLeavescale(Integer newLeavescale)
  {
    this.leavescale = newLeavescale;
  }
  
  public Integer getGxcomtype()
  {
    return this.gxcomtype;
  }
  
  public void setGxcomtype(Integer newGxcomtype)
  {
    this.gxcomtype = newGxcomtype;
  }
  
  public Integer getConvertrule()
  {
    return this.convertrule;
  }
  
  public void setConvertrule(Integer newConvertrule)
  {
    this.convertrule = newConvertrule;
  }
  
  public Object getFormula()
  {
    return this.formula;
  }
  
  public void setFormula(Object newFormula)
  {
    this.formula = newFormula;
  }
  
  public Object getFormulastr()
  {
    return this.formulastr;
  }
  
  public void setFormulastr(Object newFormulastr)
  {
    this.formulastr = newFormulastr;
  }
  
  public boolean isLeaveAppTimeLimit()
  {
    return (this.isleaveapptimelimit != null) && (this.isleaveapptimelimit.booleanValue());
  }
  
  public UFBoolean getIsleaveapptimelimit()
  {
    return this.isleaveapptimelimit;
  }
  
  public void setIsleaveapptimelimit(UFBoolean isleaveapptimelimit)
  {
    this.isleaveapptimelimit = isleaveapptimelimit;
  }
  
  public int getLeaveAppTimeLimit()
  {
    return this.leaveapptimelimit == null ? 0 : this.leaveapptimelimit.intValue();
  }
  
  public Integer getLeaveapptimelimit()
  {
    return this.leaveapptimelimit;
  }
  
  public void setLeaveapptimelimit(Integer leaveapptimelimit)
  {
    this.leaveapptimelimit = leaveapptimelimit;
  }
  
  public Integer getEnablestate()
  {
    return this.enablestate;
  }
  
  public void setEnablestate(Integer newEnablestate)
  {
    this.enablestate = newEnablestate;
  }
  
  public UFBoolean getIssynchronized()
  {
    return this.issynchronized;
  }
  
  public void setIssynchronized(UFBoolean newIssynchronized)
  {
    this.issynchronized = newIssynchronized;
  }
  
  public UFBoolean getIsinterwt()
  {
    return this.isinterwt;
  }
  
  public void setIsinterwt(UFBoolean isinterwt)
  {
    this.isinterwt = isinterwt;
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
    return "pk_timeitemcopy";
  }
  
  public String getTableName()
  {
    return "tbm_timeitemcopy";
  }
  
  public static String getDefaultTableName()
  {
    return "tbm_timeitemcopy";
  }
  
  public UFDouble getOvertimetorest()
  {
    return this.overtimetorest;
  }
  
  public void setOvertimetorest(UFDouble overtimetorest)
  {
    this.overtimetorest = overtimetorest;
  }
  
  public String getPkDefVO()
  {
    return getPk_timeitem();
  }
  
  public String getPk_defgroup()
  {
    return this.pk_defgroup;
  }
  
  public String getPk_deforg()
  {
    return this.pk_deforg;
  }
  
  public void setPkDefVO(String pk)
  {
    setPk_timeitem(pk);
  }
  
  public void setPk_defgroup(String pk)
  {
    this.pk_defgroup = pk;
  }
  
  public void setPk_deforg(String pk)
  {
    this.pk_deforg = pk;
  }
  
  public void syncFromDefVO(TimeItemVO vo)
  {
    setTimeitemname(vo.getTimeitemname());
    setTimeitemname2(vo.getTimeitemname2());
    setTimeitemname3(vo.getTimeitemname3());
    setTimeitemname4(vo.getTimeitemname4());
    setTimeitemname5(vo.getTimeitemname5());
    setTimeitemname6(vo.getTimeitemname6());
    setTimeitemcode(vo.getTimeitemcode());
    setIslactation(vo.getIslactation());
    setIspredef(vo.getIspredef());
    setPk_defgroup(vo.getPk_group());
    setPk_deforg(vo.getPk_org());
    setPk_timeitem(vo.getPk_timeitem());
    setDefTS(vo.getTs());
    setDefenablestate(vo.getEnablestate());
  }
  
  public TimeItemVO toDefVO()
  {
    return null;
  }
  
  public String getTimeitemcode()
  {
    return this.timeitemcode;
  }
  
  public void setTimeitemcode(String timeitemcode)
  {
    this.timeitemcode = timeitemcode;
  }
  
  public String getTimeitemname()
  {
    return this.timeitemname;
  }
  
  public void setTimeitemname(String timeitemname)
  {
    this.timeitemname = timeitemname;
  }
  
  public String getTimeitemname2()
  {
    return this.timeitemname2;
  }
  
  public void setTimeitemname2(String timeitemname2)
  {
    this.timeitemname2 = timeitemname2;
  }
  
  public String getTimeitemname3()
  {
    return this.timeitemname3;
  }
  
  public void setTimeitemname3(String timeitemname3)
  {
    this.timeitemname3 = timeitemname3;
  }
  
  public String getTimeitemname4()
  {
    return this.timeitemname4;
  }
  
  public void setTimeitemname4(String timeitemname4)
  {
    this.timeitemname4 = timeitemname4;
  }
  
  public String getTimeitemname5()
  {
    return this.timeitemname5;
  }
  
  public void setTimeitemname5(String timeitemname5)
  {
    this.timeitemname5 = timeitemname5;
  }
  
  public String getTimeitemname6()
  {
    return this.timeitemname6;
  }
  
  public void setTimeitemname6(String timeitemname6)
  {
    this.timeitemname6 = timeitemname6;
  }
  
  public UFBoolean getIspredef()
  {
    return this.ispredef;
  }
  
  public void setIspredef(UFBoolean ispredef)
  {
    this.ispredef = ispredef;
  }
  
  public UFBoolean getIslactation()
  {
    return this.islactation;
  }
  
  public void setIslactation(UFBoolean islactation)
  {
    this.islactation = islactation;
  }
  
  public UFBoolean getIsleave()
  {
    return this.isleave;
  }
  
  public void setIsleave(UFBoolean isleave)
  {
    this.isleave = isleave;
  }
  
  public Integer getLeaveextendcount()
  {
    return this.leaveextendcount;
  }
  
  public void setLeaveextendcount(Integer leaveextendcount)
  {
    this.leaveextendcount = leaveextendcount;
  }
  
  public String getPk_dependleavetypes()
  {
    return this.pk_dependleavetypes;
  }
  
  public void setPk_dependleavetypes(String pk_dependleavetypes)
  {
    this.pk_dependleavetypes = pk_dependleavetypes;
  }
  
  public Integer getDefenablestate()
  {
    return this.defenablestate;
  }
  
  public void setDefenablestate(Integer defenablestate)
  {
    this.defenablestate = defenablestate;
  }
  
  public String getMultilangName()
  {
    int index = MultiLangUtil.getCurrentLangSeq();
    switch (index)
    {
    case 1: 
      return this.timeitemname;
    case 2: 
      return StringUtils.isEmpty(this.timeitemname2) ? this.timeitemname : this.timeitemname2;
    case 3: 
      return StringUtils.isEmpty(this.timeitemname3) ? this.timeitemname : this.timeitemname3;
    case 4: 
      return StringUtils.isEmpty(this.timeitemname4) ? this.timeitemname : this.timeitemname4;
    case 5: 
      return StringUtils.isEmpty(this.timeitemname5) ? this.timeitemname : this.timeitemname5;
    case 6: 
      return StringUtils.isEmpty(this.timeitemname6) ? this.timeitemname : this.timeitemname6;
    }
    return null;
  }
  
  public String toString()
  {
    return getMultilangName();
  }
  
  public IBasedocCopyVO<TimeItemVO> syncFromSuperVO(IBasedocCopyVO<TimeItemVO> vo)
  {
    TimeItemCopyVO superVO = (TimeItemCopyVO)vo;
    superVO.setPk_timeitemcopy(getPk_timeitemcopy());
    superVO.setPk_group(getPk_group());
    superVO.setPk_org(getPk_org());
    superVO.setIssynchronized(UFBoolean.TRUE);
    superVO.setEnablestate(getEnablestate());
    superVO.setTs(getTs());
    return superVO;
  }
  
  public static boolean contains(TimeItemCopyVO[] copyVOs, TimeItemCopyVO copyVO)
  {
    if (ArrayUtils.isEmpty(copyVOs)) {
      return false;
    }
    for (TimeItemCopyVO vo : copyVOs) {
      if ((vo.getTimeitemcode().equals(copyVO.getTimeitemcode())) && (vo.getItemtype() == copyVO.getItemtype())) {
        return true;
      }
    }
    return false;
  }
  
  public UFDateTime getDefTS()
  {
    return this.defTS;
  }
  
  public void setDefTS(UFDateTime defTS)
  {
    this.defTS = defTS;
  }
  
  public UFBoolean getIsleavetransfer()
  {
    return this.isleavetransfer;
  }
  
  public void setIsleavetransfer(UFBoolean isleavetransfer)
  {
    this.isleavetransfer = isleavetransfer;
  }
  
  public UFBoolean getIshrssshow()
  {
    return this.ishrssshow;
  }
  
  public void setIshrssshow(UFBoolean ishrssshow)
  {
    this.ishrssshow = ishrssshow;
  }
  
  public Integer getShoworder()
  {
    return this.showorder;
  }
  
  public void setShoworder(Integer showorder)
  {
    this.showorder = showorder;
  }
}
