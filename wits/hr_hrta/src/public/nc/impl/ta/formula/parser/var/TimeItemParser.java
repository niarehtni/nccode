package nc.impl.ta.formula.parser.var;

import java.util.Collection;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hrta.utils.LeaveDataSplitSQLCreator;
import nc.impl.hr.formula.parser.IFormulaParser;
import nc.impl.ta.algorithm.BillProcessHelperAtServer;
import nc.impl.ta.timebill.BillMethods;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.formula.TimeItemProcessorUtils;
import nc.vo.ta.item.ItemVO;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.period.PeriodVO;

import org.apache.commons.lang.StringUtils;



public class TimeItemParser implements IFormulaParser {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1135352000804195683L;

	Pattern p = null;
	Pattern p1=null;
	private int itemType;//是休假类别还是出差类别还是加班类别
	private int itemClass;//日报还是月报
	
	public String parse(String pk_org,String formula,Object... params) {
		if(StringUtils.isEmpty(formula))
			return null;
		String tableName = TimeItemProcessorUtils.transferTimeItemClass2FormulaStr(itemType);
		if(StringUtils.isEmpty(tableName))
			return formula;
		p=Pattern.compile(tableName+"[.]([0-9a-zA-Z]{20})");
		p1=Pattern.compile(tableName+".leave_notcurrmonth_.([0-9a-zA-Z]{20})"+"");
		Matcher m = p.matcher(formula);
		Matcher m1 = p1.matcher(formula);
		String childTable = itemClass == ItemVO.ITEM_CLASS_DAY?"tbm_daystatb":"tbm_monthstatb";
		String parentTable = itemClass == ItemVO.ITEM_CLASS_DAY?"tbm_daystat":"tbm_monthstat";
		String joinField = itemClass == ItemVO.ITEM_CLASS_DAY?"pk_daystat":"pk_monthstat";
		while(m.find()){
			int gc = m.groupCount();
			for(int i=1;i<=gc;i++){
				String group = m.group(i);
				if(StringUtils.isEmpty(group))
					return formula;
				String replaceStr = "isnull((select hournum from "+childTable+" where "+childTable+
				".pk_timeitem='"+group+"' and "+childTable+"."+joinField+"="+parentTable+"."+joinField+"),0)";
				formula = formula.replaceAll(tableName+"[.]"+group, replaceStr);
			}
		}
		
		while(m1.find()){
			int gc = m1.groupCount();
			for(int i=1;i<=gc;i++){
				String group = m1.group(i);
				if(StringUtils.isEmpty(group))
					return formula;				
				String replaceStr = "isnull((select sum(hournum) from tbm_daystatb_notcurrmonth where "+
						"pk_timeitem='"+group+"' and tbm_daystatb_notcurrmonth.pk_daystat=tbm_daystat.pk_daystat),0)";
				//String replaceStr =getLeaveSumHour(group,currentPeriod);
				formula = formula.replaceAll(tableName+".leave_notcurrmonth_."+group, replaceStr);
				
			}
		
		}
		
		return formula;
	}
	
	
	public int getItemClass() {
		return itemClass;
	}

	public void setItemClass(int itemClass) {
		this.itemClass = itemClass;
	}


	public int getItemType() {
		return itemType;
	}


	public void setItemType(int itemType) {
		this.itemType = itemType;
	}
	
	

}
