package nc.vo.bd.shift;

import java.util.TimeZone;

import nc.hr.utils.MultiLangHelper;
import nc.itf.ta.algorithm.DateTimeUtils;
import nc.itf.ta.algorithm.IRelativeTime;
import nc.itf.ta.algorithm.IRelativeTimeScope;
import nc.itf.ta.algorithm.ITimeScope;
import nc.itf.ta.algorithm.RelativeTimeUtils;
import nc.itf.ta.algorithm.TimeScopeUtils;
import nc.itf.ta.algorithm.impl.DefaultRelativeTime;
import nc.itf.ta.algorithm.impl.DefaultRelativeTimeScope;
import nc.itf.ta.algorithm.impl.DefaultTimeScope;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

public class ShiftVO extends SuperVO {
	public static final String PK_GX = "0001Z7000000000000GX";
	private String pk_shift;
	private String pk_shifttype;
	private String pk_group;
	private String pk_org;
	private Integer enablestate;
	private UFDouble gzsj;
	private Integer worklen;
	private String memo;
	private Integer timebeginday;
	private String timebegintime;
	private Integer timeendday;
	private String timeendtime;
	private Integer beginday;
	private String begintime;
	private Integer endday;
	private String endtime;
	private UFBoolean includenightshift;
	private Integer nightbeginday;
	private String nightbegintime;
	private Integer nightendday;
	private String nightendtime;
	private UFDouble nightgzsj;
	private UFDouble allowlate;
	private UFDouble largelate;
	private UFDouble allowearly;
	private UFDouble largeearly;
	private UFBoolean isautokg;
	private UFDouble kghours;
	private UFBoolean useovertmrule;
	private UFDouble overtmbeyond;
	private UFDouble overtmbegin;
	private UFBoolean useontmrule;
	private UFDouble ontmbeyond;
	private UFDouble ontmend;
	private UFBoolean defaultflag;
	private UFBoolean isallowout;
	private UFBoolean isotflexible;
	private UFBoolean isrttimeflexible;
	private UFBoolean isotflexiblefinal;
	private UFBoolean isrttimeflexiblefinal;
	private UFBoolean isflexiblefinal;
	private String pk_sort;
	private String creator;
	private UFDateTime creationtime;
	private String modifier;
	private UFDateTime modifiedtime;
	private Integer latestbeginday;
	private String latestbegintime;
	private Integer earliestendday;
	private String earliestendtime;
	private String code;
	private String name;
	private String name2;
	private String name3;
	private String name4;
	private String name5;
	private String name6;
	private Integer dataoriginflag;
	private Integer dr = Integer.valueOf(0);

	private UFDateTime ts;

	private Integer capbeginday;

	private String capbegintime;

	private Integer capendday;

	private String capendtime;

	private UFBoolean ishredited;

	private UFBoolean iscapedited;

	private UFBoolean isturn;

	private UFDouble capgzsj;

	private UFBoolean issinglecard;
	private Integer cardtype;
	private IRelativeTimeScope workScope;
	private IRelativeTimeScope flexWorkScope;
	private IRelativeTimeScope kqScope;
	private IRelativeTimeScope capScope;

	public static final String PK_SHIFT = "pk_shift";
	public static final String PK_SHIFTTYPE = "pk_shifttype";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String ENABLESTATE = "enablestate";
	public static final String GZSJ = "gzsj";
	public static final String WORKLEN = "worklen";
	public static final String MEMO = "memo";
	public static final String TIMEBEGINDAY = "timebeginday";
	public static final String TIMEBEGINTIME = "timebegintime";
	public static final String TIMEENDDAY = "timeendday";
	public static final String TIMEENDTIME = "timeendtime";
	public static final String BEGINDAY = "beginday";
	public static final String BEGINTIME = "begintime";
	public static final String ENDDAY = "endday";
	public static final String ENDTIME = "endtime";
	public static final String INCLUDENIGHTSHIFT = "includenightshift";
	public static final String NIGHTBEGINDAY = "nightbeginday";
	public static final String NIGHTBEGINTIME = "nightbegintime";
	public static final String NIGHTENDDAY = "nightendday";
	public static final String NIGHTENDTIME = "nightendtime";
	public static final String NIGHTGZSJ = "nightgzsj";
	public static final String ALLOWLATE = "allowlate";
	public static final String LARGELATE = "largelate";
	public static final String ALLOWEARLY = "allowearly";
	public static final String LARGEEARLY = "largeearly";
	public static final String ISAUTOKG = "isautokg";
	public static final String KGHOURS = "kghours";
	public static final String USEOVERTMRULE = "useovertmrule";
	public static final String OVERTMBEYOND = "overtmbeyond";
	public static final String OVERTMBEGIN = "overtmbegin";
	public static final String USEONTMRULE = "useontmrule";
	public static final String ONTMBEYOND = "ontmbeyond";
	public static final String ONTMEND = "ontmend";
	public static final String DEFAULTFLAG = "defaultflag";
	public static final String ISALLOWOUT = "isallowout";
	public static final String ISOTFLEXIBLE = "isotflexible";
	public static final String ISRTTIMEFLEXIBLE = "isrttimeflexible";
	public static final String ISOTFLEXIBLEFINAL = "isotflexiblefinal";
	public static final String ISRTTIMEFLEXIBLEFINAL = "isrttimeflexiblefinal";
	public static final String ISFLEXIBLEFINAL = "isflexiblefinal";
	public static final String PK_SORT = "pk_sort";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String LATESTBEGINDAY = "latestbeginday";
	public static final String LATESTBEGINTIME = "latestbegintime";
	public static final String EARLIESTENDDAY = "earliestendday";
	public static final String EARLIESTENDTIME = "earliestendtime";
	public static final String CODE = "code";
	public static final String NAME = "name";
	public static final String NAME2 = "name2";
	public static final String NAME3 = "name3";
	public static final String NAME4 = "name4";
	public static final String NAME5 = "name5";
	public static final String NAME6 = "name6";
	public static final String DATAORIGINFLAG = "dataoriginflag";
	public static final String CAPBEGINDAY = "capbeginday";
	public static final String CAPBEGINTIME = "capbegintime";
	public static final String CAPENDDAY = "capendday";
	public static final String CAPENDTIME = "capendtime";
	public static final String ISHREDITED = "ishredited";
	public static final String ISCAPEDITED = "iscapedited";
	public static final String ISTURN = "isturn";
	public static final String CAPGZSJ = "capgzsj";
	public static final String ISSINGLECARD = "issinglecard";
	public static final String CARDTYPE = "cardtype";

	// ssx added for WNC Interface on 2017-12-20
	private String wncovertmbegin;
	public static final String WNCOVERTMBEGIN = "wncovertmbegin";

	public String getWncovertmbegin() {
		return wncovertmbegin;
	}

	public void setWncovertmbegin(String wncovertmbegin) {
		this.wncovertmbegin = wncovertmbegin;
	}

	//

	public String getPk_shift() {
		return pk_shift;
	}

	public void setPk_shift(String newPk_shift) {
		pk_shift = newPk_shift;
	}

	public String getPk_shifttype() {
		return pk_shifttype;
	}

	public void setPk_shifttype(String newPk_shifttype) {
		pk_shifttype = newPk_shifttype;
	}

	public String getPk_group() {
		return pk_group;
	}

	public void setPk_group(String newPk_group) {
		pk_group = newPk_group;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String newPk_org) {
		pk_org = newPk_org;
	}

	public Integer getEnablestate() {
		return enablestate;
	}

	public void setEnablestate(Integer newEnablestate) {
		enablestate = newEnablestate;
	}

	public UFDouble getGzsj() {
		return gzsj;
	}

	public void setGzsj(UFDouble newGzsj) {
		gzsj = newGzsj;
	}

	public Integer getWorklen() {
		return worklen;
	}

	public void setWorklen(Integer newWorklen) {
		worklen = newWorklen;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String newMemo) {
		memo = newMemo;
	}

	public Integer getTimebeginday() {
		return timebeginday;
	}

	public void setTimebeginday(Integer newTimebeginday) {
		timebeginday = newTimebeginday;
	}

	public String getTimebegintime() {
		return timebegintime;
	}

	public void setTimebegintime(String newTimebegintime) {
		timebegintime = newTimebegintime;
	}

	public Integer getTimeendday() {
		return timeendday;
	}

	public void setTimeendday(Integer newTimeendday) {
		timeendday = newTimeendday;
	}

	public String getTimeendtime() {
		return timeendtime;
	}

	public void setTimeendtime(String newTimeendtime) {
		timeendtime = newTimeendtime;
	}

	public Integer getBeginday() {
		return beginday;
	}

	public void setBeginday(Integer newBeginday) {
		beginday = newBeginday;
	}

	public String getBegintime() {
		return begintime;
	}

	public void setBegintime(String newBegintime) {
		begintime = newBegintime;
	}

	public Integer getEndday() {
		return endday;
	}

	public void setEndday(Integer newEndday) {
		endday = newEndday;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String newEndtime) {
		endtime = newEndtime;
	}

	public UFBoolean getIncludenightshift() {
		return includenightshift;
	}

	public void setIncludenightshift(UFBoolean newIncludenightshift) {
		includenightshift = newIncludenightshift;
	}

	public Integer getNightbeginday() {
		return nightbeginday;
	}

	public void setNightbeginday(Integer newNightbeginday) {
		nightbeginday = newNightbeginday;
	}

	public String getNightbegintime() {
		return nightbegintime;
	}

	public void setNightbegintime(String newNightbegintime) {
		nightbegintime = newNightbegintime;
	}

	public Integer getNightendday() {
		return nightendday;
	}

	public void setNightendday(Integer newNightendday) {
		nightendday = newNightendday;
	}

	public String getNightendtime() {
		return nightendtime;
	}

	public void setNightendtime(String newNightendtime) {
		nightendtime = newNightendtime;
	}

	public UFDouble getNightgzsj() {
		return nightgzsj;
	}

	public void setNightgzsj(UFDouble newNightgzsj) {
		nightgzsj = newNightgzsj;
	}

	public UFDouble getAllowlate() {
		return allowlate;
	}

	public double getAllowlateseconds() {
		return allowlate == null ? 0.0D : allowlate.doubleValue() * 60.0D;
	}

	public void setAllowlate(UFDouble newAllowlate) {
		allowlate = newAllowlate;
	}

	public UFDouble getLargelate() {
		return largelate;
	}

	public double getLargelateseconds() {
		return largelate == null ? 0.0D : largelate.doubleValue() * 60.0D;
	}

	public void setLargelate(UFDouble newLargelate) {
		largelate = newLargelate;
	}

	public UFDouble getAllowearly() {
		return allowearly;
	}

	public double getAllowearlyseconds() {
		return allowearly == null ? 0.0D : allowearly.doubleValue() * 60.0D;
	}

	public void setAllowearly(UFDouble newAllowearly) {
		allowearly = newAllowearly;
	}

	public UFDouble getLargeearly() {
		return largeearly;
	}

	public double getLargeearlyseconds() {
		return largeearly == null ? 0.0D : largeearly.doubleValue() * 60.0D;
	}

	public void setLargeearly(UFDouble newLargeearly) {
		largeearly = newLargeearly;
	}

	public UFBoolean getIsautokg() {
		return isautokg;
	}

	public void setIsautokg(UFBoolean newIsautokg) {
		isautokg = newIsautokg;
	}

	public UFDouble getKghours() {
		return kghours;
	}

	public void setKghours(UFDouble newKghours) {
		kghours = newKghours;
	}

	public UFBoolean getUseovertmrule() {
		return useovertmrule;
	}

	public void setUseovertmrule(UFBoolean newUseovertmrule) {
		useovertmrule = newUseovertmrule;
	}

	public UFDouble getOvertmbeyond() {
		return overtmbeyond;
	}

	public void setOvertmbeyond(UFDouble newOvertmbeyond) {
		overtmbeyond = newOvertmbeyond;
	}

	public UFDouble getOvertmbegin() {
		return overtmbegin;
	}

	public void setOvertmbegin(UFDouble newOvertmbegin) {
		overtmbegin = newOvertmbegin;
	}

	public UFBoolean getUseontmrule() {
		return useontmrule;
	}

	public void setUseontmrule(UFBoolean newUseontmrule) {
		useontmrule = newUseontmrule;
	}

	public UFDouble getOntmbeyond() {
		return ontmbeyond;
	}

	public void setOntmbeyond(UFDouble newOntmbeyond) {
		ontmbeyond = newOntmbeyond;
	}

	public UFDouble getOntmend() {
		return ontmend;
	}

	public void setOntmend(UFDouble newOntmend) {
		ontmend = newOntmend;
	}

	public UFBoolean getDefaultflag() {
		return defaultflag;
	}

	public void setDefaultflag(UFBoolean newDefaultflag) {
		defaultflag = newDefaultflag;
	}

	public UFBoolean getIsallowout() {
		return isallowout;
	}

	public void setIsallowout(UFBoolean newIsallowout) {
		isallowout = newIsallowout;
	}

	public UFBoolean getIsotflexible() {
		return isotflexible;
	}

	public void setIsotflexible(UFBoolean newIsotflexible) {
		isotflexible = newIsotflexible;
	}

	public UFBoolean getIsrttimeflexible() {
		return isrttimeflexible;
	}

	public void setIsrttimeflexible(UFBoolean newIsrttimeflexible) {
		isrttimeflexible = newIsrttimeflexible;
	}

	public UFBoolean getIsotflexiblefinal() {
		return isotflexiblefinal;
	}

	public void setIsotflexiblefinal(UFBoolean newIsotflexiblefinal) {
		isotflexiblefinal = newIsotflexiblefinal;
	}

	public UFBoolean getIsrttimeflexiblefinal() {
		return isrttimeflexiblefinal;
	}

	public void setIsrttimeflexiblefinal(UFBoolean newIsrttimeflexiblefinal) {
		isrttimeflexiblefinal = newIsrttimeflexiblefinal;
	}

	public UFBoolean getIsflexiblefinal() {
		return isflexiblefinal;
	}

	public void setIsflexiblefinal(UFBoolean newIsflexiblefinal) {
		isflexiblefinal = newIsflexiblefinal;
	}

	public String getPk_sort() {
		return pk_sort;
	}

	public void setPk_sort(String newPk_sort) {
		pk_sort = newPk_sort;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String newCreator) {
		creator = newCreator;
	}

	public UFDateTime getCreationtime() {
		return creationtime;
	}

	public void setCreationtime(UFDateTime newCreationtime) {
		creationtime = newCreationtime;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String newModifier) {
		modifier = newModifier;
	}

	public UFDateTime getModifiedtime() {
		return modifiedtime;
	}

	public void setModifiedtime(UFDateTime newModifiedtime) {
		modifiedtime = newModifiedtime;
	}

	public Integer getLatestbeginday() {
		return latestbeginday;
	}

	public void setLatestbeginday(Integer newLatestbeginday) {
		latestbeginday = newLatestbeginday;
	}

	public String getLatestbegintime() {
		return latestbegintime;
	}

	public void setLatestbegintime(String newLatestbegintime) {
		latestbegintime = newLatestbegintime;
	}

	public Integer getEarliestendday() {
		return earliestendday;
	}

	public void setEarliestendday(Integer newEarliestendday) {
		earliestendday = newEarliestendday;
	}

	public String getEarliestendtime() {
		return earliestendtime;
	}

	public void setEarliestendtime(String newEarliestendtime) {
		earliestendtime = newEarliestendtime;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String newCode) {
		code = newCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		name = newName;
	}

	public String getName2() {
		return name2;
	}

	public void setName2(String newName2) {
		name2 = newName2;
	}

	public String getName3() {
		return name3;
	}

	public void setName3(String newName3) {
		name3 = newName3;
	}

	public String getName4() {
		return name4;
	}

	public void setName4(String newName4) {
		name4 = newName4;
	}

	public String getName5() {
		return name5;
	}

	public void setName5(String newName5) {
		name5 = newName5;
	}

	public String getName6() {
		return name6;
	}

	public void setName6(String newName6) {
		name6 = newName6;
	}

	public Integer getDataoriginflag() {
		return dataoriginflag;
	}

	public void setDataoriginflag(Integer newDataoriginflag) {
		dataoriginflag = newDataoriginflag;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer newDr) {
		dr = newDr;
	}

	public UFDateTime getTs() {
		return ts;
	}

	public void setTs(UFDateTime newTs) {
		ts = newTs;
	}

	public String getParentPKFieldName() {
		return null;
	}

	public String getPKFieldName() {
		return "pk_shift";
	}

	public String getTableName() {
		return "bd_shift";
	}

	public static String getDefaultTableName() {
		return "bd_shift";
	}

	public boolean isFlexibleFinal() {
		return (isflexiblefinal != null) && (isflexiblefinal.booleanValue());
	}

	public boolean isOtFlexibleFinal() {
		return (isotflexiblefinal != null)
				&& (isotflexiblefinal.booleanValue());
	}

	public boolean isRttimeFlexibleFinal() {
		return (isrttimeflexiblefinal != null)
				&& (isrttimeflexiblefinal.booleanValue());
	}

	public Integer getCapbeginday() {
		return capbeginday;
	}

	public void setCapbeginday(Integer capbeginday) {
		this.capbeginday = capbeginday;
	}

	public String getCapbegintime() {
		return capbegintime;
	}

	public void setCapbegintime(String capbegintime) {
		this.capbegintime = capbegintime;
	}

	public Integer getCapendday() {
		return capendday;
	}

	public void setCapendday(Integer capendday) {
		this.capendday = capendday;
	}

	public String getCapendtime() {
		return capendtime;
	}

	public void setCapendtime(String capendtime) {
		this.capendtime = capendtime;
	}

	public UFBoolean getIshredited() {
		return ishredited;
	}

	public void setIshredited(UFBoolean ishredited) {
		this.ishredited = ishredited;
	}

	public UFBoolean getIscapedited() {
		return iscapedited;
	}

	public void setIscapedited(UFBoolean iscapedited) {
		this.iscapedited = iscapedited;
	}

	public UFBoolean getIsturn() {
		return isturn;
	}

	public void setIsturn(UFBoolean isturn) {
		this.isturn = isturn;
	}

	public UFDouble getCapgzsj() {
		return capgzsj;
	}

	public void setCapgzsj(UFDouble capgzsj) {
		this.capgzsj = capgzsj;
	}

	public IRelativeTimeScope toRelativeWorkScope() {
		if (workScope == null) {
			workScope = new DefaultRelativeTimeScope();
		}

		workScope.setScopeStartDate(getBeginday().intValue());

		workScope.setScopeStartTime(getBegintime());

		workScope.setScopeEndDate(getEndday().intValue());

		workScope.setScopeEndTime(getEndtime());

		workScope.setContainsLastSecond(false);
		return workScope;
	}

	public IRelativeTimeScope toRelativeCapScope() {
		if (capScope == null) {
			capScope = new DefaultRelativeTimeScope();
		}

		capScope.setScopeStartDate(getCapbeginday().intValue());

		capScope.setScopeStartTime(getCapbegintime());

		capScope.setScopeEndDate(getCapendday().intValue());

		capScope.setScopeEndTime(getCapendtime());

		capScope.setContainsLastSecond(false);
		return capScope;
	}

	public ITimeScope toWorkScope(String date, TimeZone timeZone) {
		return TimeScopeUtils
				.toTimeScope(toRelativeWorkScope(), date, timeZone);
	}

	public ITimeScope toCapScope(String date, TimeZone timeZone) {
		return TimeScopeUtils.toTimeScope(toRelativeCapScope(), date, timeZone);
	}

	public IRelativeTimeScope toRelativeFlexWorkScope() {
		if (flexWorkScope == null) {
			flexWorkScope = new DefaultRelativeTimeScope();
		}

		flexWorkScope.setScopeStartDate(getLatestbeginday().intValue());

		flexWorkScope.setScopeStartTime(getLatestbegintime());

		flexWorkScope.setScopeEndDate(getEarliestendday().intValue());

		flexWorkScope.setScopeEndTime(getEarliestendtime());

		flexWorkScope.setContainsLastSecond(false);
		return flexWorkScope;
	}

	public IRelativeTimeScope toRelativeKqScope() {
		if (kqScope == null) {
			kqScope = new DefaultRelativeTimeScope();
		}

		kqScope.setScopeStartDate(getTimebeginday().intValue());

		kqScope.setScopeStartTime(getTimebegintime());

		kqScope.setScopeEndDate(getTimeendday().intValue());

		kqScope.setScopeEndTime(getTimeendtime());

		kqScope.setContainsLastSecond(false);
		return kqScope;
	}

	public ITimeScope toKqScope(String date, TimeZone timeZone) {
		return TimeScopeUtils.toTimeScope(toRelativeKqScope(), date, timeZone);
	}

	public static ITimeScope toKqScope(ShiftVO curVO, ShiftVO preVO,
			ShiftVO nextVO, String curdate, TimeZone curTimeZone,
			TimeZone preTimeZone, TimeZone nextTimeZone) {
		if ((curVO != null)
				&& (!"0001Z7000000000000GX".equals(curVO.getPk_shift())))
			return curVO.toKqScope(curdate, curTimeZone);
		UFLiteralDate curDate = UFLiteralDate.getDate(curdate);
		UFDateTime beginTime = null;
		if ((preVO == null)
				|| ("0001Z7000000000000GX".equals(preVO.getPk_shift()))) {
			beginTime = new UFDateTime(curdate + " 00:00:00", curTimeZone);
		} else {
			UFLiteralDate preDate = curDate.getDateBefore(1);
			beginTime = RelativeTimeUtils.toDateTime(preVO.getTimeendday()
					.intValue(), preVO.getTimeendtime(), preDate.toString(),
					preTimeZone);
		}
		boolean containsLastSecond = false;
		UFDateTime endTime = null;
		if ((nextVO == null)
				|| ("0001Z7000000000000GX".equals(nextVO.getPk_shift()))) {
			endTime = new UFDateTime(curdate + " 23:59:59", curTimeZone);
			containsLastSecond = true;
		} else {
			UFLiteralDate nextDate = curDate.getDateAfter(1);
			endTime = RelativeTimeUtils.toDateTime(nextVO.getTimebeginday()
					.intValue(), nextVO.getTimebegintime(),
					nextDate.toString(), nextTimeZone);
		}
		return new DefaultTimeScope(beginTime, endTime, containsLastSecond);
	}

	public static UFDateTime getBoundTimeBetweenTwoShift4OvertimeCheck(
			ShiftVO preVO, ShiftVO nextVO, UFLiteralDate preDate,
			UFLiteralDate nextDate, TimeZone preTimeZone, TimeZone nextTimeZone) {
		boolean isNextGx = (nextVO == null)
				|| ("0001Z7000000000000GX".equals(nextVO.getPk_shift()));
		if (!isNextGx) {
			return RelativeTimeUtils.toDateTime(nextVO.getTimebeginday()
					.intValue(), nextVO.getTimebegintime(),
					nextDate.toString(), nextTimeZone);
		}
		boolean isPreGx = (preVO == null)
				|| ("0001Z7000000000000GX".equals(preVO.getPk_shift()));
		if (isPreGx) {
			return DateTimeUtils.toZeroOClock(nextDate, preTimeZone);
		}
		UFDateTime preEndTime = RelativeTimeUtils.toDateTime(preVO
				.getTimeendday().intValue(), preVO.getTimeendtime(), preDate
				.toString(), preTimeZone);
		UFLiteralDate preEndDate = preEndTime.getDate().toUFLiteralDate(
				preTimeZone);
		if (preEndDate.after(preDate))
			return preEndTime;
		return DateTimeUtils.toZeroOClock(nextDate, preTimeZone);
	}

	public static ITimeScope toKqScope4OvertimeCheckAndGen(ShiftVO curVO,
			ShiftVO preVO, ShiftVO nextVO, String curdate,
			TimeZone curTimeZone, TimeZone preTimeZone, TimeZone nextTimeZone) {
		UFLiteralDate curDate = UFLiteralDate.getDate(curdate);
		UFDateTime beginTime = getBoundTimeBetweenTwoShift4OvertimeCheck(preVO,
				curVO, curDate.getDateBefore(1), curDate, preTimeZone,
				curTimeZone);
		UFDateTime endTime = getBoundTimeBetweenTwoShift4OvertimeCheck(curVO,
				nextVO, curDate, curDate.getDateAfter(1), curTimeZone,
				nextTimeZone);
		return new DefaultTimeScope(beginTime, endTime);
	}

	public int hashCode() {
		return super.hashCode();
	}

	public boolean equals(Object obj) {
		if ((obj == null) || (!(obj instanceof ShiftVO)))
			return false;
		if ((StringUtils.isEmpty(pk_shift))
				|| (StringUtils.isEmpty(((ShiftVO) obj).getPk_shift())))
			return false;
		return pk_shift.equals(((ShiftVO) obj).getPk_shift());
	}

	public String getMultiLangName() {
		return MultiLangHelper.getName(this);
	}

	public UFDateTime getMidOnDutyTime(String date, TimeZone timeZone) {
		if (!isOtFlexibleFinal())
			return RelativeTimeUtils.toDateTime(getBeginday().intValue(),
					getBegintime(), date, timeZone);
		IRelativeTime earliestOnDutyTime = new DefaultRelativeTime(
				getBeginday().intValue(), getBegintime());
		IRelativeTime latestOnDutyTime = new DefaultRelativeTime(
				getLatestbeginday().intValue(), getLatestbegintime());
		return RelativeTimeUtils.toDateTime(RelativeTimeUtils.getMidTime(
				earliestOnDutyTime, latestOnDutyTime), date, timeZone);
	}

	public ITimeScope[] getFlexibleScopes(String date, TimeZone timeZone) {
		if (!isOtFlexibleFinal()) {
			return null;
		}
		ITimeScope scope1 = new DefaultTimeScope(RelativeTimeUtils.toDateTime(
				getBeginday().intValue(), getBegintime(), date, timeZone),
				RelativeTimeUtils.toDateTime(getLatestbeginday().intValue(),
						getLatestbegintime(), date, timeZone));

		ITimeScope scope2 = new DefaultTimeScope(RelativeTimeUtils.toDateTime(
				getEarliestendday().intValue(), getEarliestendtime(), date,
				timeZone), RelativeTimeUtils.toDateTime(getEndday().intValue(),
				getEndtime(), date, timeZone));

		return TimeScopeUtils
				.mergeTimeScopes(new ITimeScope[] { scope1, scope2 });
	}

	public ITimeScope getOvertimeRuleScope(UFDateTime offOnDutyTime,
			boolean isAfterOffDuty) {
		UFBoolean isNeedProcess = isAfterOffDuty ? getUseovertmrule()
				: getUseontmrule();
		if ((isNeedProcess == null) || (!isNeedProcess.booleanValue()))
			return null;
		UFDouble beyond = isAfterOffDuty ? getOvertmbeyond() : getOntmbeyond();
		UFDouble beginEnd = isAfterOffDuty ? getOvertmbegin() : getOntmend();
		int beyondSeconds = beyond == null ? 0 : beyond.intValue() * 60;
		int beginEndSeconds = beginEnd == null ? 0 : beginEnd.intValue() * 60;
		UFDateTime beyondTime = isAfterOffDuty ? DateTimeUtils
				.getDateTimeAfterMills(offOnDutyTime, beyondSeconds * 1000)
				: DateTimeUtils.getDateTimeBeforeMills(offOnDutyTime,
						beyondSeconds * 1000);
		UFDateTime beginEndTime = isAfterOffDuty ? DateTimeUtils
				.getDateTimeAfterMills(offOnDutyTime, beginEndSeconds * 1000)
				: DateTimeUtils.getDateTimeBeforeMills(offOnDutyTime,
						beginEndSeconds * 1000);
		return isAfterOffDuty ? new DefaultTimeScope(beginEndTime, beyondTime)
				: new DefaultTimeScope(beyondTime, beginEndTime);
	}

	public UFBoolean getIssinglecard() {
		return issinglecard;
	}

	public void setIssinglecard(UFBoolean issinglecard) {
		this.issinglecard = issinglecard;
	}

	public boolean isSingleCard() {
		return (issinglecard != null) && (issinglecard.booleanValue());
	}

	public Integer getCardtype() {
		return cardtype;
	}

	public void setCardtype(Integer cardtype) {
		this.cardtype = cardtype;
	}
}
