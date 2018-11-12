/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
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

/**
 * <b> �ڴ˴���Ҫ��������Ĺ��� </b>
 * <p>
 * �ڴ˴����Ӵ����������Ϣ
 * </p>
 * ��������:2010-09-14 20:34:15
 * 
 * @author
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
@DefVOPkFieldName(fieldName = "pk_timeitem")
@Table(tableName = "tbm_timeitemcopy")
@IDColumn(idColumn = "pk_timeitemcopy")
public abstract class TimeItemCopyVO extends SuperVO implements
		IBasedocCopyVO<TimeItemVO> {

	// ��١��Ӱ�ת���ݡ�����١���ͨ�Ӱࡢ���ݼӰࡢ���ռӰ಻��ɾ��
	public static final String[] NOTDELETECODEARRAY_LEAVE = new String[] {
			"105", "F01", "111" };
	public static final String[] NOTDELETECODEARRAY_OVERTIME = new String[] {
			"201", "202", "203" };

	public static final String OVERTIMETYPE_GX = "1002Z710000000021ZLV"; // ���ݼӰ�
	public static final String OVERTIMETYPE_NORMAL = "1002Z710000000021ZLT"; // ��ͨ�Ӱ�
	public static final String OVERTIMETYPE_HOLIDAY = "1002Z710000000021ZLX"; // ���ռӰ�

	public static final String LEAVETYPE_OVERTOREST = "1002Z710000000021ZM1"; // �Ӱ�ת����
	public static final String LEAVETYPE_LACTATION = "1002Z710000000021ZM3"; // �����

	public static final int LEAVE_TYPE = 0; // �ݼ�
	public static final int OVERTIME_TYPE = 1; // �Ӱ�
	public static final int AWAY_TYPE = 2; // ����
	public static final int SHUTDOWN_TYPE = 3; // ͣ������

	public static final int TIMEITEMUNIT_DAY = 0; // ������Ŀ�������
	public static final int TIMEITEMUNIT_HOUR = 1; // ������Ŀ��Сʱ����

	public static final int ROUNDMODE_UP = 0; // ����ȡ��
	public static final int ROUNDMODE_DOWN = 1; // ����ȡ��
	public static final int ROUNDMODE_MID = 2; // ��������

	public static final int LEAVESETPERIOD_MONTH = 0; // ���½���
	public static final int LEAVESETPERIOD_YEAR = 1; // �������
	public static final int LEAVESETPERIOD_DATE = 2; // ����ְ����
	// ssx added on 2018-03-16
	// for changes of Company age
	public static final int LEAVESETPERIOD_STARTDATE = 3; // �����Y������
	// /
	public static final int LEAVESETTLEMENT_DROP = 0; // ��������
	public static final int LEAVESETTLEMENT_NEXT = 1; // ת����
	public static final int LEAVESETTLEMENT_MONEY = 2; // ת����

	public static final int LEAVESCALE_YEAR = 0; // �������
	public static final int LEAVESCALE_MONTH = 1; // ���¼���

	public static final int GXCOMTYPE_NOTLEAVE = 0; // �����ݼ�
	public static final int GXCOMTYPE_TOLEAVE = 1; // ��Ϊ�ݼ�

	public static final int CONVERTRULE_DAY = 0; // ������������
	public static final int CONVERTRULE_TIME = 1; // �����ʱ������

	public static final int GXCOMTYPE_NOTAWAY = 0; // ���Ƴ���
	public static final int GXCOMTYPE_TOAWAY = 1; // ��Ϊ����

	public static final int CALCULATETYPE_TOHALF = 0; // ��12��Ϊ��ֱ��Ϊ����
	public static final int CALCULATETYPE_TODAY = 1; // �ֱ��Ϊһ��

	public static final String OVERTIMETOLEAVETYPE="F01";//�Ӱ�ת���ݱ���

	protected java.lang.String pk_timeitemcopy;
	protected java.lang.String pk_timeitem;
	private java.lang.String pk_group;
	private java.lang.String pk_org;
	private java.lang.Integer itemtype;// 0-�ݼ�,1-�Ӱ�,2-����
	private java.lang.String timeitemnote;
	private java.lang.Integer timeitemunit;// 1���죻2��Сʱ
	private nc.vo.pub.lang.UFBoolean isleavelimit;// �Ƿ�����ݼ�ʱ��
	private nc.vo.pub.lang.UFBoolean isrestrictlimit;// �Ƿ��ϸ�����ݼ�ʱ��
    private nc.vo.pub.lang.UFBoolean isleaveplan;// �Ƿ�ʹ���ݼټƻ�
	private java.lang.Integer leavesettlement;// 0���������ϣ�1��ת���ڣ�2��ת����,3����ת��
	private java.lang.Integer leavesetperiod;// ���꣬���ǰ��£����ǰ���ְ�ս���
	private java.lang.Integer calculatetype;// ���Ĭ��0 ��0:����12��Ϊ���Ϊ���죬1���ֱ��Ϊ1��
	private nc.vo.pub.lang.UFDouble timeunit;// ��Сʱ�������ӻ����죩���ڰ�Сʱ����ģ���λ�Ƿ��ӣ����ڰ������ģ���λ����
	private java.lang.Integer roundmode;// ȡ����ʽ
	private java.lang.Integer leavescale;// ���¼��㣬���ǰ�����㡣ֻ�н����������������ְ�յ�ʱ�����Ч
	private java.lang.Integer gxcomtype;// �����ռ��㷽ʽ��0���������ݼ٣��Ӱࣩ��1�������ݼ٣��Ӱࣩ��Ĭ��Ϊ0
	private java.lang.Integer convertrule;// 0����������ʱ�������ڹ����ж��壩���㣬1�������ʱ����������
	private java.lang.Integer leaveapptimelimit;// �ݼ�����ʱ����������
	private nc.vo.pub.lang.UFBoolean isleaveapptimelimit;
	private java.lang.Object formula;
	private java.lang.Object formulastr;
	private java.lang.Integer enablestate;
	private java.lang.Integer defenablestate;
	private nc.vo.pub.lang.UFBoolean issynchronized;
	private java.lang.String creator;
	private nc.vo.pub.lang.UFDateTime creationtime;
	private java.lang.String modifier;
	private nc.vo.pub.lang.UFDateTime modifiedtime;
	private Integer moncounts = 0;
	private Integer wayofresult = 0;
	public static final String WAYOFRESULT = "wayofresult";
	public Integer getWayofresult() {
		return wayofresult;
	}
	public void setWayofresult(Integer wayofresult) {
		this.wayofresult = wayofresult;
	}
	public static final String MONCOUNTS = "moncounts";
	
	public Integer getMoncounts() {
		return moncounts;
	}
	public void setMoncounts(Integer moncounts) {
		this.moncounts = moncounts;
	}
	private UFDouble overtimetorest;
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;
	private nc.vo.pub.lang.UFBoolean ispredef;// �Ƿ���Ԥ�����
	private nc.vo.pub.lang.UFBoolean islactation;// �Ƿ��ǲ����
	private nc.vo.pub.lang.UFBoolean isinterwt;// �Ƿ�ȡ�����ν���
	private nc.vo.pub.lang.UFBoolean isleave;// �Ƿ����ݼ�
	private java.lang.Integer leaveextendcount;// ��Ч������
	private java.lang.String pk_dependleavetypes;// ǰ�üٵ�pk���飬����pk1,pk2,pk3����
	private UFBoolean isleavetransfer;// �ݼٽ����Ƿ����֯ת��
	private UFBoolean ishrssshow;// �Ƿ���������ʾ����ʱ��
	private java.lang.Integer showorder;// �����ʾ˳���ݼ����ʹ��

	private String timeitemcode;
	private String timeitemname;
	private String timeitemname2;
	private String timeitemname3;
	private String timeitemname4;
	private String timeitemname5;
	private String timeitemname6;
	private java.lang.String pk_defgroup;
	private java.lang.String pk_deforg;
	private UFDateTime defTS;// ���岿�ֵ�ts
	private UFDouble leavemax;
    private nc.vo.pub.lang.UFBoolean isspecialrest;

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
	public static final String LEAVEMAX = "leavemax";

	// ssx added on 20180425
	// for Taiwan New Law Requirements
	private UFBoolean isstuffdecidecomp;
	public static final String ISSTUFFDECIDECOMP = "isstuffdecidecomp";
	private UFDouble daylimit;
	public static final String DAYLIMIT = "daylimit";
	private UFBoolean isincludewithlimit;
	public static final String ISINCLUDEWITHLIMIT = "isincludewithlimit";
	private UFDouble effectivehours;
	public static final String EFFECTIVEHOURS = "effectivehours";
	private UFDouble deductlowhours;
	public static final String DEDUCTLOWHOURS = "deductlowhours";
	private UFDouble deductminutes;
	public static final String DEDUCTMINUTES = "deductminutes";
	private String pk_segrule;
	public static final String PK_SEGRULE = "pk_segrule";
    private Integer date_type;
    public static final String DATE_TYPE = "date_type";
    private UFBoolean isdatetypedefault;
    public static final String ISDATETYPEDEFAULT = "isdatetypedefault";
    private static final String ISSPECIALREST = "isspecialrest";
    public UFBoolean getIsspecialrest() {
	return (UFBoolean) (isspecialrest == null ? UFBoolean.FALSE : isspecialrest);
    }
    public void setIsspecialrest(UFBoolean isspecialrest) {
	this.isspecialrest = (UFBoolean) (isspecialrest == null ? UFBoolean.FALSE : isspecialrest);
    }

	public UFBoolean getIsstuffdecidecomp() {
		return isstuffdecidecomp;
	}

	public void setIsstuffdecidecomp(UFBoolean isstuffdecidecomp) {
		this.isstuffdecidecomp = isstuffdecidecomp;
	}

	public UFDouble getDaylimit() {
		return daylimit;
	}

	public void setDaylimit(UFDouble daylimit) {
		this.daylimit = daylimit;
	}

	public UFBoolean getIsincludewithlimit() {
		return isincludewithlimit;
	}

	public void setIsincludewithlimit(UFBoolean isincludewithlimit) {
		this.isincludewithlimit = isincludewithlimit;
	}

	public UFDouble getEffectivehours() {
		return effectivehours;
	}

	public void setEffectivehours(UFDouble effectivehours) {
		this.effectivehours = effectivehours;
	}

	public UFDouble getDeductlowhours() {
		return deductlowhours;
	}

	public void setDeductlowhours(UFDouble deductlowhours) {
		this.deductlowhours = deductlowhours;
	}

	public UFDouble getDeductminutes() {
		return deductminutes;
	}

	public void setDeductminutes(UFDouble deductminutes) {
		this.deductminutes = deductminutes;
	}

	public String getPk_segrule() {
		return pk_segrule;
	}

	public void setPk_segrule(String pk_segrule) {
		this.pk_segrule = pk_segrule;
	}

    public Integer getDate_type() {
	return date_type;
    }
	//
    public void setDate_type(Integer date_type) {
	this.date_type = date_type;
    }

    public UFBoolean getIsdatetypedefault() {
	return isdatetypedefault;
    }
    public void setIsdatetypedefault(UFBoolean isdatetypedefault) {
	this.isdatetypedefault = isdatetypedefault;
    }
	/**
	 * ����pk_timeitemcopy��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_timeitemcopy() {
		return pk_timeitemcopy;
	}

	/**
	 * ����pk_timeitemcopy��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newPk_timeitemcopy
	 *            java.lang.String
	 */
	public void setPk_timeitemcopy(java.lang.String newPk_timeitemcopy) {
		this.pk_timeitemcopy = newPk_timeitemcopy;
	}

	/**
	 * ����pk_timeitem��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_timeitem() {
		return pk_timeitem;
	}

	/**
	 * ����pk_timeitem��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newPk_timeitem
	 *            java.lang.String
	 */
	public void setPk_timeitem(java.lang.String newPk_timeitem) {
		this.pk_timeitem = newPk_timeitem;
	}

	/**
	 * ����pk_group��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_group() {
		return pk_group;
	}

	/**
	 * ����pk_group��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newPk_group
	 *            java.lang.String
	 */
	public void setPk_group(java.lang.String newPk_group) {
		this.pk_group = newPk_group;
	}

	/**
	 * ����pk_org��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org() {
		return pk_org;
	}

	/**
	 * ����pk_org��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newPk_org
	 *            java.lang.String
	 */
	public void setPk_org(java.lang.String newPk_org) {
		this.pk_org = newPk_org;
	}

	/**
	 * ����itemtype��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getItemtype() {
		return itemtype;
	}

	/**
	 * ����itemtype��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newItemtype
	 *            java.lang.Integer
	 */
	public void setItemtype(java.lang.Integer newItemtype) {
		this.itemtype = newItemtype;
	}

	/**
	 * ����timeitemnote��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getTimeitemnote() {
		return timeitemnote;
	}

	/**
	 * ����timeitemnote��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newTimeitemnote
	 *            java.lang.String
	 */
	public void setTimeitemnote(java.lang.String newTimeitemnote) {
		this.timeitemnote = newTimeitemnote;
	}

	/**
	 * ����timeitemunit��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getTimeitemunit() {
		return timeitemunit;
	}

	/**
	 * ����timeitemunit��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newTimeitemunit
	 *            java.lang.Integer
	 */
	public void setTimeitemunit(java.lang.Integer newTimeitemunit) {
		this.timeitemunit = newTimeitemunit;
	}

	public int getTimeItemUnit() {
		return timeitemunit == null ? TIMEITEMUNIT_DAY : timeitemunit
				.intValue();
	}

	public boolean isLeaveLimit() {
		return isleavelimit != null && isleavelimit.booleanValue();
	}

	/**
	 * ����isleavelimit��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getIsLeavelimit() {
		return isleavelimit;
	}

	/**
	 * ����isleavelimit��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newLeavelimit
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIsLeavelimit(nc.vo.pub.lang.UFBoolean newLeavelimit) {
		this.isleavelimit = newLeavelimit;
	}

	public boolean isRestrictLimit() {
		return isrestrictlimit != null && isrestrictlimit.booleanValue();
	}

	/**
	 * ����isrestrictlimit��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getIsRestrictlimit() {
		return isrestrictlimit;
	}

	/**
	 * ����isrestrictlimit��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newLeavelimit
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIsRestrictlimit(nc.vo.pub.lang.UFBoolean newIsrestrictlimit) {
		this.isrestrictlimit = newIsrestrictlimit;
	}

	/**
	 * ����leavesettlement��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getLeavesettlement() {
		return leavesettlement;
	}

	/**
	 * ����leavesettlement��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newLeavesettlement
	 *            java.lang.Integer
	 */
	public void setLeavesettlement(java.lang.Integer newLeavesettlement) {
		this.leavesettlement = newLeavesettlement;
	}

	/**
	 * ����leavesetperiod��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getLeavesetperiod() {
		return leavesetperiod;
	}

	/**
	 * ����leavesetperiod��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newLeavesetperiod
	 *            java.lang.Integer
	 */
	public void setLeavesetperiod(java.lang.Integer newLeavesetperiod) {
		this.leavesetperiod = newLeavesetperiod;
	}

	/**
	 * ����calculatetype��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getCalculatetype() {
		return calculatetype;
	}

	/**
	 * ����calculatetype��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newCalculatetype
	 *            java.lang.Integer
	 */
	public void setCalculatetype(java.lang.Integer newCalculatetype) {
		this.calculatetype = newCalculatetype;
	}

	/**
	 * ����timeunit��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getTimeunit() {
		return timeunit;
	}

	/**
	 * ����timeunit��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newTimeunit
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setTimeunit(nc.vo.pub.lang.UFDouble newTimeunit) {
		this.timeunit = newTimeunit;
	}

	public double getTimeUnit() {
		return timeunit == null ? 0 : timeunit.doubleValue();
	}

	/**
	 * ����roundmode��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getRoundmode() {
		return roundmode;
	}

	/**
	 * ����roundmode��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newRoundmode
	 *            java.lang.Integer
	 */
	public void setRoundmode(java.lang.Integer newRoundmode) {
		this.roundmode = newRoundmode;
	}

	/**
	 * ����leavescale��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getLeavescale() {
		return leavescale;
	}

	/**
	 * ����leavescale��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newLeavescale
	 *            java.lang.Integer
	 */
	public void setLeavescale(java.lang.Integer newLeavescale) {
		this.leavescale = newLeavescale;
	}

	/**
	 * ����gxcomtype��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getGxcomtype() {
		return gxcomtype;
	}

	/**
	 * ����gxcomtype��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newGxcomtype
	 *            java.lang.Integer
	 */
	public void setGxcomtype(java.lang.Integer newGxcomtype) {
		this.gxcomtype = newGxcomtype;
	}

	/**
	 * ����convertrule��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getConvertrule() {
		return convertrule;
	}

	/**
	 * ����convertrule��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newConvertrule
	 *            java.lang.Integer
	 */
	public void setConvertrule(java.lang.Integer newConvertrule) {
		this.convertrule = newConvertrule;
	}

	/**
	 * ����formula��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.Object
	 */
	public java.lang.Object getFormula() {
		return formula;
	}

	/**
	 * ����formula��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newFormula
	 *            java.lang.Object
	 */
	public void setFormula(java.lang.Object newFormula) {
		this.formula = newFormula;
	}

	/**
	 * ����formulastr��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.Object
	 */
	public java.lang.Object getFormulastr() {
		return formulastr;
	}

	/**
	 * ����formulastr��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newFormulastr
	 *            java.lang.Object
	 */
	public void setFormulastr(java.lang.Object newFormulastr) {
		this.formulastr = newFormulastr;
	}

	public boolean isLeaveAppTimeLimit() {
		return isleaveapptimelimit != null
				&& isleaveapptimelimit.booleanValue();
	}

	public nc.vo.pub.lang.UFBoolean getIsleaveapptimelimit() {
		return isleaveapptimelimit;
	}

	public void setIsleaveapptimelimit(
			nc.vo.pub.lang.UFBoolean isleaveapptimelimit) {
		this.isleaveapptimelimit = isleaveapptimelimit;
	}

	public int getLeaveAppTimeLimit() {
		return leaveapptimelimit == null ? 0 : leaveapptimelimit.intValue();
	}

	public java.lang.Integer getLeaveapptimelimit() {
		return leaveapptimelimit;
	}

	public void setLeaveapptimelimit(java.lang.Integer leaveapptimelimit) {
		this.leaveapptimelimit = leaveapptimelimit;
	}

	/**
	 * ����enablestate��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getEnablestate() {
		return enablestate;
	}

	/**
	 * ����enablestate��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newEnablestate
	 *            java.lang.Integer
	 */
	public void setEnablestate(java.lang.Integer newEnablestate) {
		this.enablestate = newEnablestate;
	}

	/**
	 * ����issynchronized��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getIssynchronized() {
		return issynchronized;
	}

	/**
	 * ����issynchronized��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newIsmodified
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIssynchronized(nc.vo.pub.lang.UFBoolean newIssynchronized) {
		this.issynchronized = newIssynchronized;
	}

	public nc.vo.pub.lang.UFBoolean getIsinterwt() {
		return isinterwt;
	}

	public void setIsinterwt(nc.vo.pub.lang.UFBoolean isinterwt) {
		this.isinterwt = isinterwt;
	}

	/**
	 * ����creator��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCreator() {
		return creator;
	}

	/**
	 * ����creator��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newCreator
	 *            java.lang.String
	 */
	public void setCreator(java.lang.String newCreator) {
		this.creator = newCreator;
	}

	/**
	 * ����creationtime��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getCreationtime() {
		return creationtime;
	}

	/**
	 * ����creationtime��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newCreationtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime(nc.vo.pub.lang.UFDateTime newCreationtime) {
		this.creationtime = newCreationtime;
	}

	/**
	 * ����modifier��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getModifier() {
		return modifier;
	}

	/**
	 * ����modifier��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newModifier
	 *            java.lang.String
	 */
	public void setModifier(java.lang.String newModifier) {
		this.modifier = newModifier;
	}

	/**
	 * ����modifiedtime��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getModifiedtime() {
		return modifiedtime;
	}

	/**
	 * ����modifiedtime��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newModifiedtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime(nc.vo.pub.lang.UFDateTime newModifiedtime) {
		this.modifiedtime = newModifiedtime;
	}

	/**
	 * ����dr��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDr() {
		return dr;
	}

	/**
	 * ����dr��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newDr
	 *            java.lang.Integer
	 */
	public void setDr(java.lang.Integer newDr) {
		this.dr = newDr;
	}

	/**
	 * ����ts��Getter����. ��������:2010-09-14 20:34:15
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getTs() {
		return ts;
	}

	/**
	 * ����ts��Setter����. ��������:2010-09-14 20:34:15
	 * 
	 * @param newTs
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(nc.vo.pub.lang.UFDateTime newTs) {
		this.ts = newTs;
	}

	/**
	 * <p>
	 * ȡ�ø�VO�����ֶ�.
	 * <p>
	 * ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getParentPKFieldName() {
		return null;
	}

	/**
	 * <p>
	 * ȡ�ñ�����.
	 * <p>
	 * ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPKFieldName() {
		return "pk_timeitemcopy";
	}

	/**
	 * <p>
	 * ���ر�����.
	 * <p>
	 * ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "tbm_timeitemcopy";
	}

	/**
	 * <p>
	 * ���ر�����.
	 * <p>
	 * ��������:2010-09-14 20:34:15
	 * 
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "tbm_timeitemcopy";
	}

	/**
	 * ����Ĭ�Ϸ�ʽ����������.
	 * 
	 * ��������:2010-09-14 20:34:15
	 */
	public TimeItemCopyVO() {
		super();
	}

	public UFDouble getOvertimetorest() {
		return overtimetorest;
	}

	public void setOvertimetorest(UFDouble overtimetorest) {
		this.overtimetorest = overtimetorest;
	}

	@Override
	public String getPkDefVO() {
		return getPk_timeitem();
	}

	@Override
	public String getPk_defgroup() {
		return pk_defgroup;
	}

	@Override
	public String getPk_deforg() {
		return pk_deforg;
	}

	@Override
	public void setPkDefVO(String pk) {
		setPk_timeitem(pk);
	}

	@Override
	public void setPk_defgroup(String pk) {
		this.pk_defgroup = pk;
	}

	@Override
	public void setPk_deforg(String pk) {
		this.pk_deforg = pk;
	}

	@Override
	public void syncFromDefVO(TimeItemVO vo) {
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

	@Override
	public TimeItemVO toDefVO() {
		return null;
	}

	public String getTimeitemcode() {
		return timeitemcode;
	}

	public void setTimeitemcode(String timeitemcode) {
		this.timeitemcode = timeitemcode;
	}

	public String getTimeitemname() {
		return timeitemname;
	}

	public void setTimeitemname(String timeitemname) {
		this.timeitemname = timeitemname;
	}

	public String getTimeitemname2() {
		return timeitemname2;
	}

	public void setTimeitemname2(String timeitemname2) {
		this.timeitemname2 = timeitemname2;
	}

	public String getTimeitemname3() {
		return timeitemname3;
	}

	public void setTimeitemname3(String timeitemname3) {
		this.timeitemname3 = timeitemname3;
	}

	public String getTimeitemname4() {
		return timeitemname4;
	}

	public void setTimeitemname4(String timeitemname4) {
		this.timeitemname4 = timeitemname4;
	}

	public String getTimeitemname5() {
		return timeitemname5;
	}

	public void setTimeitemname5(String timeitemname5) {
		this.timeitemname5 = timeitemname5;
	}

	public String getTimeitemname6() {
		return timeitemname6;
	}

	public void setTimeitemname6(String timeitemname6) {
		this.timeitemname6 = timeitemname6;
	}

	public nc.vo.pub.lang.UFBoolean getIspredef() {
		return ispredef;
	}

	public void setIspredef(nc.vo.pub.lang.UFBoolean ispredef) {
		this.ispredef = ispredef;
	}

	public nc.vo.pub.lang.UFBoolean getIslactation() {
		return islactation;
	}

	public void setIslactation(nc.vo.pub.lang.UFBoolean islactation) {
		this.islactation = islactation;
	}

	public nc.vo.pub.lang.UFBoolean getIsleave() {
		return isleave;
	}

	public void setIsleave(nc.vo.pub.lang.UFBoolean isleave) {
		this.isleave = isleave;
	}

	public java.lang.Integer getLeaveextendcount() {
		return leaveextendcount;
	}

	public void setLeaveextendcount(java.lang.Integer leaveextendcount) {
		this.leaveextendcount = leaveextendcount;
	}

	public java.lang.String getPk_dependleavetypes() {
		return pk_dependleavetypes;
	}

	public void setPk_dependleavetypes(java.lang.String pk_dependleavetypes) {
		this.pk_dependleavetypes = pk_dependleavetypes;
	}

	public java.lang.Integer getDefenablestate() {
		return defenablestate;
	}

	public void setDefenablestate(java.lang.Integer defenablestate) {
		this.defenablestate = defenablestate;
	}

	public String getMultilangName() {
		int index = MultiLangUtil.getCurrentLangSeq();
		switch (index) {
		case 1:
			return timeitemname;
		case 2:
			return StringUtils.isEmpty(timeitemname2) ? timeitemname
					: timeitemname2;
		case 3:
			return StringUtils.isEmpty(timeitemname3) ? timeitemname
					: timeitemname3;
		case 4:
			return StringUtils.isEmpty(timeitemname4) ? timeitemname
					: timeitemname4;
		case 5:
			return StringUtils.isEmpty(timeitemname5) ? timeitemname
					: timeitemname5;
		case 6:
			return StringUtils.isEmpty(timeitemname6) ? timeitemname
					: timeitemname6;
		}
		return null;
	}

	@Override
	public String toString() {
		return getMultilangName();
	}

	@Override
	public IBasedocCopyVO<TimeItemVO> syncFromSuperVO(
			IBasedocCopyVO<TimeItemVO> vo) {
		TimeItemCopyVO superVO = (TimeItemCopyVO) vo;
		superVO.setPk_timeitemcopy(getPk_timeitemcopy());
		superVO.setPk_group(getPk_group());
		superVO.setPk_org(getPk_org());
		superVO.setIssynchronized(UFBoolean.TRUE);
		superVO.setEnablestate(getEnablestate());
		superVO.setTs(getTs());
		return superVO;
	}

	public static boolean contains(TimeItemCopyVO[] copyVOs,
			TimeItemCopyVO copyVO) {
		if (ArrayUtils.isEmpty(copyVOs))
			return false;
		for (TimeItemCopyVO vo : copyVOs) {
			if (vo.getTimeitemcode().equals(copyVO.getTimeitemcode())
					&& vo.getItemtype() == copyVO.getItemtype())
				return true;
		}
		return false;
	}

	public UFDateTime getDefTS() {
		return defTS;
	}

	public void setDefTS(UFDateTime defTS) {
		this.defTS = defTS;
	}

	public UFBoolean getIsleavetransfer() {
		return isleavetransfer;
	}

	public void setIsleavetransfer(UFBoolean isleavetransfer) {
		this.isleavetransfer = isleavetransfer;
	}

	public UFBoolean getIshrssshow() {
		return ishrssshow;
	}

	public void setIshrssshow(UFBoolean ishrssshow) {
		this.ishrssshow = ishrssshow;
	}

	public java.lang.Integer getShoworder() {
		return showorder;
	}

	public void setShoworder(java.lang.Integer showorder) {
		this.showorder = showorder;
	}

	public UFDouble getLeavemax() {
		return leavemax;
	}

	public void setLeavemax(UFDouble leavemax) {
		this.leavemax = leavemax;
	}
    public UFBoolean isLeavePlan() {
	return new UFBoolean(isleaveplan != null && isleaveplan.booleanValue());
    }
    public nc.vo.pub.lang.UFBoolean getIsLeaveplan() {
	return isleaveplan;
    }
    public void setIsLeaveplan(nc.vo.pub.lang.UFBoolean newIsleaveplan) {
	this.isleaveplan = newIsleaveplan;
    }
}