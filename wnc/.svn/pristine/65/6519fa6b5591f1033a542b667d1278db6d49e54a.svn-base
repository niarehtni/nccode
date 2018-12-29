package nc.impl.hrwa.salarysmart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.ift.hrwa.salarysmart.ISalaryScalesSmartQuary;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.metadata.Field;
import nc.pub.smart.metadata.MetaData;
import nc.pub.smart.context.SmartContext;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
public class SalaryScalesSmartQuaryImpl implements ISalaryScalesSmartQuary {
	

	public DataSet quarySalaryScales(SmartContext context,boolean isEN)throws BusinessException{
		String org = (String) context.getParameterValue("org");
		String year = (String) context.getParameterValue("year");
		String month = (String) context.getParameterValue("month");
		DataSet result = new DataSet();
		//中英文字段名
		Map<String,String> newsRowNameMap = getNewsRowNameMap(isEN);
		
		String sqlStr = " SELECT "
				+ " wa_data.pk_psndoc pk_psndoc, "
				+ " bd_psndoc.code code, "
				+ (isEN ? " bd_psndoc.name3 name, ":" bd_psndoc.name name, ")
				+ " wa_waclass.pk_wa_class pk_wa_class, "
				+ " wa_data.pk_group pk_group, "
				+ " wa_data.pk_org pk_org, "
				+ " SALARY_DECRYPT ( wa_data.f_338 ) based_pay, "
				+ " SALARY_DECRYPT ( wa_data.f_342 ) job_allowance, "
				+ " SALARY_DECRYPT ( wa_data.f_419 ) full_attendance, "
				+ " SALARY_DECRYPT ( wa_data.f_339 ) meal_allowance, "
				+ " SALARY_DECRYPT ( wa_data.f_341 ) shift_allowance, "
				+ " SALARY_DECRYPT ( wa_data.f_219 ) other_pay, "
				+ " SALARY_DECRYPT ( wa_data.f_63 ) over_time_pay, "
				+ " SALARY_DECRYPT ( wa_data.f_56 ) over_time_pay_, "
				+ " ( 0 )  a_l_pay, "
				+ " SALARY_DECRYPT ( wa_data.f_64 ) a_l_pay_, "
				+ " ( 0 ) unleave_pay, "
				+ " SALARY_DECRYPT ( wa_data.f_65 ) unleave_pay_, "
				+ " SALARY_DECRYPT ( wa_data.f_57 ) working_day_o_t, "
				+ " SALARY_DECRYPT ( wa_data.f_59 ) rest_day_o_t, "
				+ " SALARY_DECRYPT ( wa_data.f_58 ) holiday_day_o_t, "
				+ " ( 0 )   natural_disaster, "
				+ " SALARY_DECRYPT ( wa_data.f_68 )  personal_leave, "
				+ " SALARY_DECRYPT ( wa_data.f_69 ) sick_leave, "
				+ " SALARY_DECRYPT ( wa_data.f_300 )  a_l, "
				+ " SALARY_DECRYPT ( wa_data.f_288 )  unleave, "
				+ " SALARY_DECRYPT ( wa_data.f_189 )  meals_fee, "
				+ " SALARY_DECRYPT ( wa_data.f_190 )   fruit_bar, "
				+ " ( 0 )  breakfast, "
				+ " SALARY_DECRYPT ( wa_data.f_44 ) tax, "
				+ " SALARY_DECRYPT ( wa_data.f_46 ) labor_insurance, "
				+ " SALARY_DECRYPT ( wa_data.f_47 )  health_insurance, "
				+ " SALARY_DECRYPT ( wa_data.f_48 )   health_family, "
				+ " SALARY_DECRYPT ( wa_data.f_280 )  fab_deduction, "
				+ " SALARY_DECRYPT ( wa_data.f_50 ) labor_retire_self, "
				+ " SALARY_DECRYPT ( wa_data.f_45 ) welfare_permium, "
				+ " ( SALARY_DECRYPT ( wa_data.f_85 ) + SALARY_DECRYPT ( wa_data.f_73 ) ) on_leve_deduction, "
				+ " SALARY_DECRYPT ( wa_data.f_210 )  second_generation_nhi, "
				+ " SALARY_DECRYPT ( wa_data.f_51 )  nanshaninsur, "
				+ " ( 0 )  second_account, "
				+ " SALARY_DECRYPT ( wa_data.f_1 ) due_amount, "
				+ " SALARY_DECRYPT ( wa_data.f_2 ) deduction, "
				+ " SALARY_DECRYPT ( wa_data.f_29 ) labor_retirecomp, "
				+ " SALARY_DECRYPT ( wa_data.f_3 ) total, "
				+ " wa_data_1.pk_bankaccbas1 pk_bankaccbas1, "
				+ " org_dept.code deptcode, "
				+ " org_dept.name deptname, "
				+ " SUBSTR ( wa_data_1.cpaydate, 1, 10 ) cpaydate "
				+ " FROM "
				+ " bd_psndoc bd_psndoc "
				+ " LEFT OUTER JOIN "
				+ " wa_data wa_data "
				+ " ON "
				+ " bd_psndoc.pk_psndoc = wa_data.pk_psndoc "
				+ " LEFT OUTER JOIN "
				+ " wa_waclass wa_waclass "
				+ " ON "
				+ " wa_data.pk_wa_class = wa_waclass.pk_wa_class "
				+ " LEFT OUTER JOIN "
				+ " wa_data wa_data_1 "
				+ " ON "
				+ " wa_data.pk_wa_data = wa_data_1.pk_wa_data "
				+ " LEFT OUTER JOIN "
				+ " hi_psnjob hi_psnjob "
				+ " ON "
				+ " wa_data.pk_psnjob = hi_psnjob.pk_psnjob "
				+ " LEFT OUTER JOIN "
				+ " org_dept org_dept "
				+ " ON	"
				+ " hi_psnjob.pk_dept = org_dept.pk_dept "
				+ " WHERE	"
				+ " ( wa_waclass.code = 'TWMONTH'	"
				+ " AND wa_data.pk_org = '"+org+"' "
				+ " AND wa_data.cyear = '"+year+"'	"
				+ " AND wa_data.cperiod = '"+month+"' ) ";
		List<Map<String,Object>> resultMapList = 
				(List<Map<String,Object>>)new BaseDAO().executeQuery(sqlStr, new MapListProcessor());
		if(resultMapList == null){
			return result;
		}
		MetaData metaData = builderMetaData();
		result.setMetaData(metaData);
		Object [][] objs = new Object[resultMapList.size()][78];
		int i = 0;
		for(Map<String,Object> row : resultMapList){
			objs[i][0] = row.get("pk_psndoc");
			objs[i][1] = row.get("code");
			objs[i][2] = row.get("name");
			objs[i][3] = row.get("pk_wa_class");
			objs[i][4] = row.get("pk_group");
			objs[i][5] = row.get("pk_org");
			objs[i][6] = row.get("meals_fee");
			objs[i][7] = row.get("fruit_bar");
			objs[i][8] = row.get("breakfast");
			objs[i][9] = row.get("nanshaninsur");
			objs[i][10] = row.get("due_amount");
			objs[i][11] = row.get("deduction");
			objs[i][12] = row.get("labor_retirecomp");
			objs[i][13] = row.get("total");
			objs[i][14] = row.get("pk_bankaccbas1");
			objs[i][15] = row.get("deptcode");
			objs[i][16] = row.get("deptname");
			objs[i][17] = row.get("cpaydate");
			//開始構造動態列
			//获取值
			List<Object> newsColunm = getNewsRow(row, newsRowNameMap);
			//进行动态列赋值
			int j = 0;
			for(Object value : newsColunm){
				objs[i][18 + j] = value;
				j++;
			}
			i++;
		}
		result.setDatas(objs);
		return result;
	}
	
	public  Map<String, String> getNewsRowNameMap(boolean isEN) {
		Map<String,String> newsRowNameMap = new HashMap<>();
		if (isEN) {
			newsRowNameMap.put("based_pay", "Based pay");
			newsRowNameMap.put("job_allowance", "Job allowance");
			newsRowNameMap.put("full_attendance", "Full attendance");
			newsRowNameMap.put("meal_allowance", "Meal allowance");
			newsRowNameMap.put("shift_allowance", "Shift allowance");
			newsRowNameMap.put("other_pay", "Other pay");
			newsRowNameMap.put("over_time_pay", "Over time pay");
			newsRowNameMap.put("over_time_pay_", "Over time pay*");
			newsRowNameMap.put("a_l_pay", "A/L pay");
			newsRowNameMap.put("a_l_pay_", "A/L pay*");
			newsRowNameMap.put("unleave_pay", "Unleave pay");
			newsRowNameMap.put("unleave_pay_", "Unleave pay*");
			newsRowNameMap.put("working_day_o_t", "Working day O.T");
			newsRowNameMap.put("rest_day_o_t", "Rest day O.T");
			newsRowNameMap.put("holiday_day_o_t", "Holiday day O.T");
			newsRowNameMap.put("natural_disaster", "Natural Disaster");
			newsRowNameMap.put("personal_leave", "Personal leave");
			newsRowNameMap.put("sick_leave", "Sick leave");
			newsRowNameMap.put("a_l", "A/L");
			newsRowNameMap.put("unleave", "Unleave");
			newsRowNameMap.put("tax", "Tax");
			newsRowNameMap.put("labor_insurance", "Labor insurance");
			newsRowNameMap.put("health_insurance", "Health insurance");
			newsRowNameMap.put("health_family", "Health (family)");
			newsRowNameMap.put("fab_deduction", "Fab deduction");
			newsRowNameMap.put("labor_retire_self", "Labor Retire(Self)");
			newsRowNameMap.put("welfare_permium", "Welfare permium");
			newsRowNameMap.put("on_leve_deduction", "On leve deduction");
			newsRowNameMap.put("second_generation_nhi", "Second Generation_NHI");
			newsRowNameMap.put("second_account", "Second_account");
		} else {
			newsRowNameMap.put("based_pay", "本薪");
			newsRowNameMap.put("job_allowance", "職務加給");
			newsRowNameMap.put("full_attendance", "全勤獎金");
			newsRowNameMap.put("meal_allowance", "伙食津貼");
			newsRowNameMap.put("shift_allowance", "班別津貼");
			newsRowNameMap.put("other_pay", "其他給付");
			newsRowNameMap.put("over_time_pay", "免稅加班");
			newsRowNameMap.put("over_time_pay_", "應稅加班");
			newsRowNameMap.put("a_l_pay", "免稅特休");
			newsRowNameMap.put("a_l_pay_", "應稅特休");
			newsRowNameMap.put("unleave_pay", "免稅補休");
			newsRowNameMap.put("unleave_pay_", "應稅補休");
			newsRowNameMap.put("working_day_o_t", "平時加班");
			newsRowNameMap.put("rest_day_o_t", "休息日加班");
			newsRowNameMap.put("holiday_day_o_t", "國假日加班");
			newsRowNameMap.put("natural_disaster", "天災加班");
			newsRowNameMap.put("personal_leave", "事假時數");
			newsRowNameMap.put("sick_leave", "病假時數");
			newsRowNameMap.put("a_l", "特休時數");
			newsRowNameMap.put("unleave", "補休時數");
			newsRowNameMap.put("tax", "所得稅");
			newsRowNameMap.put("labor_insurance", "勞保費");
			newsRowNameMap.put("health_insurance", "健保費");
			newsRowNameMap.put("health_family", "健眷保費");
			newsRowNameMap.put("fab_deduction", "全勤扣款");
			newsRowNameMap.put("labor_retire_self", "勞退個提");
			newsRowNameMap.put("welfare_permium", "福利金");
			newsRowNameMap.put("on_leve_deduction", "請假扣款");
			newsRowNameMap.put("second_generation_nhi", "健保補充費");
			newsRowNameMap.put("second_account", "Second_account");
		}
		return newsRowNameMap;
	}
	
	/**
	 * 获取动态列的值,不为空且不为0才获取,否则舍弃
	 * @param row
	 * @return
	 */
	private  List<Object> getNewsRow(Map<String, Object> row ,Map<String,String> newsRowNameMap) {
		List<Object> newsColunm = new ArrayList<>();
		if(row.get("based_pay")!=null 
				&& new UFDouble(String.valueOf(row.get("based_pay"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("based_pay"));
			newsColunm.add(row.get("based_pay"));
		}
		if(row.get("job_allowance")!=null 
				&& new UFDouble(String.valueOf(row.get("job_allowance"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("job_allowance"));
			newsColunm.add(row.get("job_allowance"));
		}
		if(row.get("full_attendance")!=null 
				&& new UFDouble(String.valueOf(row.get("full_attendance"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("full_attendance"));
			newsColunm.add(row.get("full_attendance"));
		}
		if(row.get("meal_allowance")!=null 
				&& new UFDouble(String.valueOf(row.get("meal_allowance"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("meal_allowance"));
			newsColunm.add(row.get("meal_allowance"));
		}
		if(row.get("shift_allowance")!=null 
				&& new UFDouble(String.valueOf(row.get("shift_allowance"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("shift_allowance"));
			newsColunm.add(row.get("shift_allowance"));
		}
		if(row.get("other_pay")!=null 
				&& new UFDouble(String.valueOf(row.get("other_pay"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("other_pay"));
			newsColunm.add(row.get("other_pay"));
		}
		if(row.get("over_time_pay")!=null 
				&& new UFDouble(String.valueOf(row.get("over_time_pay"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("over_time_pay"));
			newsColunm.add(row.get("over_time_pay"));
		}
		if(row.get("over_time_pay_")!=null 
				&& new UFDouble(String.valueOf(row.get("over_time_pay_"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("over_time_pay_"));
			newsColunm.add(row.get("over_time_pay_"));
		}
		if(row.get("a_l_pay")!=null 
				&& new UFDouble(String.valueOf(row.get("a_l_pay"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("a_l_pay"));
			newsColunm.add(row.get("a_l_pay"));
		}
		if(row.get("a_l_pay_")!=null 
				&& new UFDouble(String.valueOf(row.get("a_l_pay_"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("a_l_pay_"));
			newsColunm.add(row.get("a_l_pay_"));
		}
		if(row.get("unleave_pay")!=null 
				&& new UFDouble(String.valueOf(row.get("unleave_pay"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("unleave_pay"));
			newsColunm.add(row.get("unleave_pay"));
		}
		if(row.get("unleave_pay_")!=null 
				&& new UFDouble(String.valueOf(row.get("unleave_pay_"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("unleave_pay_"));
			newsColunm.add(row.get("unleave_pay_"));
		}
		if(row.get("working_day_o_t")!=null 
				&& new UFDouble(String.valueOf(row.get("working_day_o_t"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("working_day_o_t"));
			newsColunm.add(row.get("working_day_o_t"));
		}
		if(row.get("rest_day_o_t")!=null 
				&& new UFDouble(String.valueOf(row.get("rest_day_o_t"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("rest_day_o_t"));
			newsColunm.add(row.get("rest_day_o_t"));
		}
		if(row.get("holiday_day_o_t")!=null 
				&& new UFDouble(String.valueOf(row.get("holiday_day_o_t"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("holiday_day_o_t"));
			newsColunm.add(row.get("holiday_day_o_t"));
		}
		if(row.get("natural_disaster")!=null 
				&& new UFDouble(String.valueOf(row.get("natural_disaster"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("natural_disaster"));
			newsColunm.add(row.get("natural_disaster"));
		}
		if(row.get("personal_leave")!=null 
				&& new UFDouble(String.valueOf(row.get("personal_leave"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("personal_leave"));
			newsColunm.add(row.get("personal_leave"));
		}
		if(row.get("sick_leave")!=null 
				&& new UFDouble(String.valueOf(row.get("sick_leave"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("sick_leave"));
			newsColunm.add(row.get("sick_leave"));
		}
		if(row.get("a_l")!=null 
				&& new UFDouble(String.valueOf(row.get("a_l"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("a_l"));
			newsColunm.add(row.get("a_l"));
		}
		if(row.get("unleave")!=null 
				&& new UFDouble(String.valueOf(row.get("unleave"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("unleave"));
			newsColunm.add(row.get("unleave"));
		}
		if(row.get("tax")!=null 
				&& new UFDouble(String.valueOf(row.get("tax"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("tax"));
			newsColunm.add(row.get("tax"));
		}
		if(row.get("labor_insurance")!=null 
				&& new UFDouble(String.valueOf(row.get("labor_insurance"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("labor_insurance"));
			newsColunm.add(row.get("labor_insurance"));
		}
		if(row.get("health_insurance")!=null 
				&& new UFDouble(String.valueOf(row.get("health_insurance"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("health_insurance"));
			newsColunm.add(row.get("health_insurance"));
		}
		if(row.get("health_family")!=null 
				&& new UFDouble(String.valueOf(row.get("health_family"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("health_family"));
			newsColunm.add(row.get("health_family"));
		}
		if(row.get("fab_deduction")!=null 
				&& new UFDouble(String.valueOf(row.get("fab_deduction"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("fab_deduction"));
			newsColunm.add(row.get("fab_deduction"));
		}
		if(row.get("labor_retire_self")!=null 
				&& new UFDouble(String.valueOf(row.get("labor_retire_self"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("labor_retire_self"));
			newsColunm.add(row.get("labor_retire_self"));
		}
		if(row.get("welfare_permium")!=null 
				&& new UFDouble(String.valueOf(row.get("welfare_permium"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("welfare_permium"));
			newsColunm.add(row.get("welfare_permium"));
		}
		if(row.get("on_leve_deduction")!=null 
				&& new UFDouble(String.valueOf(row.get("on_leve_deduction"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("on_leve_deduction"));
			newsColunm.add(row.get("on_leve_deduction"));
		}
		if(row.get("second_generation_nhi")!=null 
				&& new UFDouble(String.valueOf(row.get("second_generation_nhi"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("second_generation_nhi"));
			newsColunm.add(row.get("second_generation_nhi"));
		}
		if(row.get("second_account")!=null 
				&& new UFDouble(String.valueOf(row.get("second_account"))).abs().doubleValue() > 0.0001){
			newsColunm.add(newsRowNameMap.get("second_account"));
			newsColunm.add(row.get("second_account"));
		}
		
		return newsColunm;
	}
	
	private MetaData builderMetaData() {
		MetaData metaData = new MetaData();
		
		Field fld = new Field();
		fld.setCaption("人員基本信息");
		fld.setFldname("pk_psndoc");
		fld.setDbColumnType(12);
		fld.setPrecision(100);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("人員編碼");
		fld.setFldname("code");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("姓名");
		fld.setFldname("name");
		fld.setPrecision(300);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("薪資方案主鍵");
		fld.setFldname("pk_wa_class");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("所屬集團");
		fld.setFldname("pk_group");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("所屬組織");
		fld.setFldname("pk_org");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("代收付餐費");
		fld.setFldname("Meals_Fee");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("代收付咖啡吧");
		fld.setFldname("Fruit_Bar");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("Breakfast");
		fld.setFldname("Breakfast");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("團報-南山人壽");
		fld.setFldname("NanShanInsur");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("應發合計");
		fld.setFldname("Due_Amount");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("應扣合計");
		fld.setFldname("Deduction");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("勞退公司提發");
		fld.setFldname("Labor_RetireComp");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("實發合計");
		fld.setFldname("TOTAL");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("銀行賬號1");
		fld.setFldname("pk_bankaccbas1");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("編碼");
		fld.setFldname("deptcode");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("名稱");
		fld.setFldname("deptname");
		fld.setPrecision(300);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("發放日期");
		fld.setFldname("cpaydate");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱01");
		fld.setFldname("glbdef01");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value01");
		fld.setFldname("glbdefvalue01");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱02");
		fld.setFldname("glbdef02");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value02");
		fld.setFldname("glbdefvalue02");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱03");
		fld.setFldname("glbdef03");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value03");
		fld.setFldname("glbdefvalue03");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱04");
		fld.setFldname("glbdef04");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value04");
		fld.setFldname("glbdefvalue04");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱05");
		fld.setFldname("glbdef05");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value05");
		fld.setFldname("glbdefvalue05");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱06");
		fld.setFldname("glbdef06");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value06");
		fld.setFldname("glbdefvalue06");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱07");
		fld.setFldname("glbdef07");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value07");
		fld.setFldname("glbdefvalue07");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱08");
		fld.setFldname("glbdef08");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value08");
		fld.setFldname("glbdefvalue08");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱09");
		fld.setFldname("glbdef09");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value09");
		fld.setFldname("glbdefvalue09");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱10");
		fld.setFldname("glbdef10");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value10");
		fld.setFldname("glbdefvalue10");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱11");
		fld.setFldname("glbdef11");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value11");
		fld.setFldname("glbdefvalue11");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱12");
		fld.setFldname("glbdef12");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value12");
		fld.setFldname("glbdefvalue12");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱13");
		fld.setFldname("glbdef13");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value13");
		fld.setFldname("glbdefvalue13");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱14");
		fld.setFldname("glbdef14");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value14");
		fld.setFldname("glbdefvalue14");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱15");
		fld.setFldname("glbdef15");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value15");
		fld.setFldname("glbdefvalue15");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱16");
		fld.setFldname("glbdef16");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value16");
		fld.setFldname("glbdefvalue16");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱17");
		fld.setFldname("glbdef17");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value17");
		fld.setFldname("glbdefvalue17");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱18");
		fld.setFldname("glbdef18");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value18");
		fld.setFldname("glbdefvalue18");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱19");
		fld.setFldname("glbdef19");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value19");
		fld.setFldname("glbdefvalue19");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱20");
		fld.setFldname("glbdef20");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value20");
		fld.setFldname("glbdefvalue20");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱21");
		fld.setFldname("glbdef21");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value21");
		fld.setFldname("glbdefvalue21");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱22");
		fld.setFldname("glbdef22");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value22");
		fld.setFldname("glbdefvalue22");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱23");
		fld.setFldname("glbdef23");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value23");
		fld.setFldname("glbdefvalue23");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱24");
		fld.setFldname("glbdef24");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value24");
		fld.setFldname("glbdefvalue24");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱25");
		fld.setFldname("glbdef25");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value25");
		fld.setFldname("glbdefvalue25");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱26");
		fld.setFldname("glbdef26");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value26");
		fld.setFldname("glbdefvalue26");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱27");
		fld.setFldname("glbdef27");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value27");
		fld.setFldname("glbdefvalue27");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱28");
		fld.setFldname("glbdef28");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value28");
		fld.setFldname("glbdefvalue28");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱29");
		fld.setFldname("glbdef29");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value29");
		fld.setFldname("glbdefvalue29");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項名稱30");
		fld.setFldname("glbdef30");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("動態薪資項value30");
		fld.setFldname("glbdefvalue30");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		
		return metaData;
	}
	

}
