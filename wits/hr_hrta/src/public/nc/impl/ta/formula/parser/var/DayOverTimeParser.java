package nc.impl.ta.formula.parser.var;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import nc.vo.ta.item.ItemVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.timeitem.TimeItemVO;


public class DayOverTimeParser extends TimeItemParser {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1135352000804195683L;
	
	public DayOverTimeParser() {
		super();
		setItemClass(ItemVO.ITEM_CLASS_DAY);
		setItemType(TimeItemVO.OVERTIME_TYPE);
	}

	public String parse(String pk_org,String formula,Object... params) {
		
		formula = super.parse(pk_org, formula, params);
		formula = parseOverToRest(pk_org, formula, params);
		
		formula = parseOTNotCurrentMonth(pk_org, formula, params);
		
		return parseEarliestLatestOvertime(pk_org, formula, params);
	}
	
	protected String parseOTNotCurrentMonth(String pk_org,String formula,Object... params){
		if(StringUtils.isEmpty(formula))
			return null;
		String tableName = "OVERTIMEDATA";
		p=Pattern.compile(tableName+".overtime_notcurrmonth_.([0-9a-zA-Z]{20})");
		p1=Pattern.compile(tableName+"._OVER_TO_RESTovertime_notcurrmonth_"+"[.]([0-9a-zA-Z]{20})");	
		
		Pattern w1=Pattern.compile(tableName+".overtime_notcurrmonth_.WorkFirst2H"+"[.]([0-9a-zA-Z]{20})");	
		Pattern w2=Pattern.compile(tableName+".overtime_notcurrmonth_.WorkLast2H"+"[.]([0-9a-zA-Z]{20})");	
		Pattern h1=Pattern.compile(tableName+".overtime_notcurrmonth_.HolidayFirst8H"+"[.]([0-9a-zA-Z]{20})");	
		Pattern h2=Pattern.compile(tableName+".overtime_notcurrmonth_.Holiday8To12H"+"[.]([0-9a-zA-Z]{20})");	
		Pattern h3=Pattern.compile(tableName+".overtime_notcurrmonth_.HolidayAfter12H"+"[.]([0-9a-zA-Z]{20})");	
		Pattern r1=Pattern.compile(tableName+".overtime_notcurrmonth_.RestFirst2H"+"[.]([0-9a-zA-Z]{20})");	
		Pattern r2=Pattern.compile(tableName+".overtime_notcurrmonth_.Rest2To8H"+"[.]([0-9a-zA-Z]{20})");	
		Pattern r3=Pattern.compile(tableName+".overtime_notcurrmonth_.RestAfter8H"+"[.]([0-9a-zA-Z]{20})");	
		
		Matcher m = p.matcher(formula);
		Matcher m1 = p1.matcher(formula);
		while(m.find()){
			int gc = m.groupCount();
			for(int i=1;i<=gc;i++){
				String group = m.group(i);
				if(StringUtils.isEmpty(group))
					return formula;
				
				String replaceStr = "isnull((select sum(hournum) from tbm_daystatb_notcurrmonth where "+
						"pk_timeitem='"+group+"' and tbm_daystatb_notcurrmonth.pk_daystat=tbm_daystat.pk_daystat),0)";
				formula = formula.replaceAll(tableName+".overtime_notcurrmonth_."+group, replaceStr);
			}
		}
		while(m1.find()){
			int gc = m1.groupCount();
			for(int i=1;i<=gc;i++){
				String group = m1.group(i);
				if(StringUtils.isEmpty(group))
					return formula;

				String replaceStr = "isnull((select sum(toresthour) from tbm_daystatb_notcurrmonth where "+
						"pk_timeitem='"+group+"' and tbm_daystatb_notcurrmonth.pk_daystat=tbm_daystat.pk_daystat),0)";

				formula = formula.replaceAll(tableName+"._OVER_TO_RESTovertime_notcurrmonth_."+group, replaceStr);
			}
		}
		
		Matcher mw1 = w1.matcher(formula);
		Matcher mw2 = w2.matcher(formula);
		Matcher mh1 = h1.matcher(formula);
		Matcher mh2 = h2.matcher(formula);
		Matcher mh3 = h3.matcher(formula);
		Matcher mr1 = r1.matcher(formula);
		Matcher mr2 = r2.matcher(formula);
		Matcher mr3 = r3.matcher(formula);
		
		while(mw1.find()){
			int gc = mw1.groupCount();
			for(int i=1;i<=gc;i++){
				String group = mw1.group(i);
				if(StringUtils.isEmpty(group))
					return formula;

				String replaceStr = "isnull((select sum(case when hournum<=2 then hournum else 2 end) from tbm_daystatb_notcurrmonth where "+
						"pk_timeitem='"+group+"' and tbm_daystatb_notcurrmonth.pk_daystat=tbm_daystat.pk_daystat),0)";

				formula = formula.replaceAll(tableName+".overtime_notcurrmonth_.WorkFirst2H."+group, replaceStr);
			}
		}
		while(mw2.find()){
			int gc = mw2.groupCount();
			for(int i=1;i<=gc;i++){
				String group = mw2.group(i);
				if(StringUtils.isEmpty(group))
					return formula;

				String replaceStr = "isnull((select sum(case when hournum<=2 then 0 else hournum-2 end) from tbm_daystatb_notcurrmonth where "+
						"pk_timeitem='"+group+"' and tbm_daystatb_notcurrmonth.pk_daystat=tbm_daystat.pk_daystat),0)";

				formula = formula.replaceAll(tableName+".overtime_notcurrmonth_.WorkLast2H."+group, replaceStr);
			}
		}
		
		while(mh1.find()){
			int gc = mh1.groupCount();
			for(int i=1;i<=gc;i++){
				String group = mh1.group(i);
				if(StringUtils.isEmpty(group))
					return formula;

				String replaceStr = "isnull((select sum(case when hournum<=8 then hournum else 8 end) from tbm_daystatb_notcurrmonth where "+
						"pk_timeitem='"+group+"' and tbm_daystatb_notcurrmonth.pk_daystat=tbm_daystat.pk_daystat),0)";

				formula = formula.replaceAll(tableName+".overtime_notcurrmonth_.HolidayFirst8H."+group, replaceStr);
			}
		}
		while(mh2.find()){
			int gc = mh2.groupCount();
			for(int i=1;i<=gc;i++){
				String group = mh2.group(i);
				if(StringUtils.isEmpty(group))
					return formula;

				String replaceStr = "isnull((select sum(case when hournum<=8 then 0 when hournum>=12 then 4 else hournum - 8 end) from tbm_daystatb_notcurrmonth where "+
						"pk_timeitem='"+group+"' and tbm_daystatb_notcurrmonth.pk_daystat=tbm_daystat.pk_daystat),0)";

				formula = formula.replaceAll(tableName+".overtime_notcurrmonth_.Holiday8To12H."+group, replaceStr);
			}
		}
		while(mh3.find()){
			int gc = mh3.groupCount();
			for(int i=1;i<=gc;i++){
				String group = mh3.group(i);
				if(StringUtils.isEmpty(group))
					return formula;

				String replaceStr = "isnull((select sum(case when hournum<=12 then 0 else hournum - 12 end) from tbm_daystatb_notcurrmonth where "+
						"pk_timeitem='"+group+"' and tbm_daystatb_notcurrmonth.pk_daystat=tbm_daystat.pk_daystat),0)";

				formula = formula.replaceAll(tableName+".overtime_notcurrmonth_.HolidayAfter12H."+group, replaceStr);
			}
		}
		
		while(mr1.find()){
			int gc = mr1.groupCount();
			for(int i=1;i<=gc;i++){
				String group = mr1.group(i);
				if(StringUtils.isEmpty(group))
					return formula;

				String replaceStr = "isnull((select sum(case when hournum<=2 then hournum else 2 end) from tbm_daystatb_notcurrmonth where "+
						"pk_timeitem='"+group+"' and tbm_daystatb_notcurrmonth.pk_daystat=tbm_daystat.pk_daystat),0)";

				formula = formula.replaceAll(tableName+".overtime_notcurrmonth_.RestFirst2H."+group, replaceStr);
			}
		}
		while(mr2.find()){
			int gc = mr2.groupCount();
			for(int i=1;i<=gc;i++){
				String group = mr2.group(i);
				if(StringUtils.isEmpty(group))
					return formula;

				String replaceStr = "isnull((select sum(case when hournum<=2 then 0 when hournum>=8 then 6 else hournum - 2 end) from tbm_daystatb_notcurrmonth where "+
						"pk_timeitem='"+group+"' and tbm_daystatb_notcurrmonth.pk_daystat=tbm_daystat.pk_daystat),0)";

				formula = formula.replaceAll(tableName+".overtime_notcurrmonth_.Rest2To8H."+group, replaceStr);
			}
		}
		while(mr3.find()){
			int gc = mr3.groupCount();
			for(int i=1;i<=gc;i++){
				String group = mr3.group(i);
				if(StringUtils.isEmpty(group))
					return formula;

				String replaceStr = "isnull((select sum(case when hournum<=8 then 0 else hournum - 8 end) from tbm_daystatb_notcurrmonth where "+
						"pk_timeitem='"+group+"' and tbm_daystatb_notcurrmonth.pk_daystat=tbm_daystat.pk_daystat),0)";

				formula = formula.replaceAll(tableName+".overtime_notcurrmonth_.RestAfter8H."+group, replaceStr);
			}
		}
		return formula;
	}

	protected String parseOverToRest(String pk_org,String formula,Object... params){
		if(StringUtils.isEmpty(formula))
			return null;
		String tableName = "OVERTIMEDATA";
		String overtorest = "_OVER_TO_REST";
		p=Pattern.compile(tableName+"[.]"+overtorest+"([0-9a-zA-Z]{20})");
		Matcher m = p.matcher(formula);
		int itemClass = getItemClass();
		String childTable = itemClass == ItemVO.ITEM_CLASS_DAY?"tbm_daystatb":"tbm_monthstatb";
		String parentTable = itemClass == ItemVO.ITEM_CLASS_DAY?"tbm_daystat":"tbm_monthstat";
		String joinField = itemClass == ItemVO.ITEM_CLASS_DAY?"pk_daystat":"pk_monthstat";
		while(m.find()){
			int gc = m.groupCount();
			for(int i=1;i<=gc;i++){
				String group = m.group(i);
				if(StringUtils.isEmpty(group))
					return formula;
				String replaceStr = "isnull((select toresthour from "+childTable+" where "+childTable+
				".pk_timeitem='"+group+"' and "+childTable+"."+joinField+"="+parentTable+"."+joinField+"),0)";
				formula = formula.replaceAll(tableName+"[.]"+overtorest+group, replaceStr);
			}
		}
		return formula;
	}

	protected String parseEarliestLatestOvertime(String pk_org,String formula,Object... params){
		return formula.replaceAll("OVERTIMEDATA.OVERTIMEBEGINTIME", getStr(true))
				.replaceAll("OVERTIMEDATA.OVERTIMEENDTIME", getStr(false));
	}
	
	private String getStr(boolean isBegin){
		return "(select "+(isBegin?"min(overtimebegintime)":"max(overtimeendtime)")+" from tbm_overtimebelong where tbm_overtimebelong.pk_org=tbm_daystat.pk_org and tbm_overtimebelong.pk_psndoc=tbm_daystat.pk_psndoc and tbm_overtimebelong.belongtodate=tbm_daystat.calendar)";
	}
}
