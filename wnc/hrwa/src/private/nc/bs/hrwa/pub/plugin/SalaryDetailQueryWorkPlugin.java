package nc.bs.hrwa.pub.plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.pa.PreAlertReturnType;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.hr.utils.InSQLCreator;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
/**
 * 薪資實發明細表 后台任务
 * 计算本月和上一个月的薪资发放明细
 * 
 * 如果是第一次运行,会运行一年的数据
 * @author Administrator
 *
 */
public class SalaryDetailQueryWorkPlugin implements IBackgroundWorkPlugin {

	private BaseDAO basedao = new BaseDAO();
	@Override
	public PreAlertObject executeTask(BgWorkingContext context)
			throws BusinessException {
		//删除一年以前本月和上月的数据,进行从新计算
		clearCacuDates();
		//查询前12个月的数据,如果没有,进行计算
		Set<String> needCacuDates = getNeedCacuDates();
		
		doCacuDates(needCacuDates);
		
		StringBuffer sendmsg=new StringBuffer();
		sendmsg.append("计算完成,缓存"+TABLE_NAME+"表");
		PreAlertObject retObj = new PreAlertObject();
		retObj.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		retObj.setReturnObj(sendmsg.toString());
		return retObj;
	}
	
	private void clearCacuDates() throws DAOException {
		Set<String> rsSet = new HashSet<>();
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //删除本月和上月数据
        for(int i=1;i<=2;i++){
        	
        	String year = String.valueOf(calendar.get(Calendar.YEAR));
        	int monthNum = calendar.get(Calendar.MONTH)+1;
        	String month = String.valueOf(monthNum);
        	if(monthNum<10){
        		month = "0"+month;
        	}
        	rsSet.add(year+month);
        	calendar.add(Calendar.MONTH, -1);
        }
        
        for(String cperiod: rsSet){
        	String year = cperiod.substring(0, 4);
			String month = cperiod.substring(4,6);
			String sql = "delete from "+TABLE_NAME+" where CYEAR = '"+year+"' and CPERIOD='"+month+"'  ";
			basedao.executeUpdate(sql);
        }
        //删除一年前的数据
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -14);
        UFLiteralDate ufLiteralDate = new UFLiteralDate(calendar.getTime());
        String sql = "delete from "+TABLE_NAME+" where CYEAR || CPERIOD<='"+ufLiteralDate.toStdString().replaceAll("-", "").substring(0,6)+"'  ";
		basedao.executeUpdate(sql);
		
	}

	private void doCacuDates(Set<String> needCacuDates) throws DAOException {
		
		for(String cperiod : needCacuDates){
			String year = cperiod.substring(0, 4);
			String month = cperiod.substring(4,6);
			UFDateTime curDate = new UFDateTime();
			String curDateStr = curDate.toStdString();
			String sql = INSERT_SQL +"("+getQuerySQL(curDateStr)+" where t_1.cyear = '"+year+"' AND t_1.cperiod = '"+month+"')";
			
			basedao.executeUpdate(sql);
		}
	}

	private String getQuerySQL(String curDateStr) {
		
		return QUERY_SQL_PRE+"'"+curDateStr+"'"+QUERY_SQL_END;
	}

	private Set<String> getNeedCacuDates() throws BusinessException {
		Set<String> rsSet = new HashSet<>();
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //前推14个月(比如现在是10月,直觉上..用户会选择去年的9月到今年的9月)
        for(int i=1;i<=14;i++){
        	
        	String year = String.valueOf(calendar.get(Calendar.YEAR));
        	int monthNum = calendar.get(Calendar.MONTH)+1;
        	String month = String.valueOf(monthNum);
        	if(monthNum<10){
        		month = "0"+month;
        	}
        	rsSet.add(year+month);
        	calendar.add(Calendar.MONTH, -1);
        }
        InSQLCreator insql = new InSQLCreator();
        String insqlStr = insql.getInSQL(rsSet.toArray(new String[0]));
        
        
		String sql = " select (CYEAR||CPERIOD) yearp,count(*) from "+TABLE_NAME+" where (CYEAR||CPERIOD) in ("+insqlStr+") group by CYEAR,CPERIOD";
		@SuppressWarnings("unchecked")
		Set<String> rs = (Set<String>)basedao.executeQuery(sql, new ResultSetProcessor() {
			private Set<String> rsSet = new HashSet<>();
			private static final long serialVersionUID = -7611173024792406304L;
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				while(rs.next()){
					String cyearPeriod = rs.getString(1);
					int nums = rs.getInt(2);
					if(nums>0){
						rsSet.add(cyearPeriod);
					}
				}
				return rsSet;
			}
		});
		rsSet.removeAll(rs);
		return rsSet;
	}

	private static final String TABLE_NAME = "cache_salary_detail";
	
	private static final String INSERT_SQL = "Insert into cache_salary_detail(cacutime,pk_org, PK_WA_CLASS, cyear, cperiod, name_3, bdcode, name_2, code, "
                                +" clerkcode, name4, name_5, ygxsname, code_1, name_1, benxin, hsjt, gwjt, "
                                +" bbjt, zwjj, qqjj, ysjbf, msjbf, pwjt, f_108, txdj, bxdj, f_66,  ysqtj, msqtj, "
                                +" ysqtq, msqtk, yshj, mshj, sfhj, ysjkk, msjkk, qqkk, lbf, jbf, ltzt, taunbaofei, flj, bcks, "
                                +" bcbf, prjbss, jrjbss, xsrjbss, f_456, txwxwss, sjss, jtzgjss, bjss, f_480, txss, f_286, ndbxwxwss, "
                                +" bxlcjss, bxcjts, f_303, qtjfs, jbbx, txzbj, lbgt, gtljj, jbgt, gttbf, ltgt, f_649,f_52) ";
	
	private static final String QUERY_SQL_PRE = " select ";
	
	private static final String QUERY_SQL_END = " ts,t_1.pk_org pk_org,t_1.pk_wa_class pk_wa_class,t_1.cyear cyear,t_1.cperiod cperiod, "
			+"        t_1.dept_acc_name name_3,t_1.deptcode bdcode,t_1.deptname name_2,t_1.doccode code,t_1.clerkcode clerkcode, "
			+"        t_1.docname name4,t_1.psnclname name_5,t_1.jobtypename ygxsname,t_1.classcode code_1,t_1.classname name_1, "
			+"        t_1.f_338 benxin,t_1.f_339 hsjt,t_1.f_340 gwjt,t_1.f_341 bbjt,t_1.f_342 zwjj,t_1.f_419 qqjj, "
			+"        t_1.f_56 ysjbf,t_1.f_358 msjbf,t_1.f_107 pwjt,t_1.f_108 f_108,t_1.f_64 txdj,t_1.f_65 bxdj,t_1.f_66 f_66, "
			+"        t_1.f_464 ysqtj,t_1.f_463 msqtj,t_1.f_466 ysqtq,t_1.f_465 msqtk,t_1.f_20 yshj,t_1.f_277 mshj, "
			+"        t_1.f_3 sfhj,t_1.f_278 ysjkk,t_1.f_279 msjkk,t_1.f_280 qqkk,t_1.f_46 lbf,t_1.f_47 jbf,t_1.f_50 ltzt, "
			+"        t_1.f_51 taunbaofei,t_1.f_45 flj,t_1.f_5 bcks,t_1.f_49 bcbf,t_1.f_57 prjbss,t_1.f_58 jrjbss, "
			+"        t_1.f_59 xsrjbss,t_1.f_456 f_456,t_1.f_300 txwxwss,t_1.f_68 sjss,t_1.f_307 jtzgjss,t_1.f_69 bjss, "
			+"        t_1.f_480 f_480,t_1.f_285 txss,t_1.f_286 f_286,t_1.f_479 ndbxwxwss,t_1.f_302 bxlcjss,t_1.f_301 bxcjts, "
			+"        t_1.f_303 f_303,t_1.f_71 qtjfs,t_1.f_289 jbbx,t_1.f_32 txzbj,t_1.f_27 lbgt,t_1.f_33 gtljj, "
			+"        t_1.f_28 jbgt,t_1.f_30 gttbf,t_1.f_29 ltgt,t_1.f_649 f_649,t_1.f_52 f_52 "
			+" FROM ( "
			+"          SELECT wd.pk_org      pk_org, "
			+"                 wc.pk_wa_class pk_wa_class, "
			+"                 wd.cyear       cyear, "
			+"                 wd.cperiod     cperiod, "
			+"                 dept_acc.name2 dept_acc_name, "
			+"                 dept.code      deptcode, "
			+"                 dept.name2     deptname, "
			+"                 doc.code       doccode, "
			+"                 job.clerkcode  clerkcode, "
			+"                 doc.name2      docname, "
			+"                 psncl.name     psnclname, "
			+"                 def_jobtype.name2 jobtypename, "
			+"                 wc.code        classcode, "
			+"                 wc.name        classname, "
			+"                 SALARY_DECRYPT(wd.f_338) f_338, "
			+"                 SALARY_DECRYPT(wd.f_339) f_339, "
			+"                 SALARY_DECRYPT(wd.f_340) f_340, "
			+"                 SALARY_DECRYPT(wd.f_341) f_341, "
			+"                 SALARY_DECRYPT(wd.f_342) f_342, "
			+"                 SALARY_DECRYPT(wd.f_419) f_419, "
			+"                 SALARY_DECRYPT(wd.f_56) + SALARY_DECRYPT(wd.f_411) f_56, "
			+"                 SALARY_DECRYPT(wd.f_358) + SALARY_DECRYPT(wd.f_63) f_358, "
			+"                 SALARY_DECRYPT(wd.f_107) f_107, "
			+"                 SALARY_DECRYPT(wd.f_108) f_108, "
			
			+"                 SALARY_DECRYPT(wd.f_64)  f_64, "
			+"                 SALARY_DECRYPT(wd.f_65)  f_65, "
			+"                 SALARY_DECRYPT(wd.f_66)  f_66, "
			+"                 SALARY_DECRYPT(wd.f_52)  f_52, "
			+"                 f_464.money    f_464, "
			+"                 f_463.money    f_463, "
			+"                 f_466.money    f_466, "
			+"                 f_465.money    f_465, "
			+"                 SALARY_DECRYPT(wd.f_20)  f_20, "
			+"                 (SALARY_DECRYPT(wd.f_358) + SALARY_DECRYPT(wd.f_63) + SALARY_DECRYPT(wd.f_64) + "
			+"                  SALARY_DECRYPT(wd.f_65) + SALARY_DECRYPT(wd.f_66) + f_463.money - f_465.money - "
			+"                  SALARY_DECRYPT(wd.f_279) - SALARY_DECRYPT(wd.f_46) - SALARY_DECRYPT(wd.f_47) - "
			+"                  SALARY_DECRYPT(wd.f_51) - SALARY_DECRYPT(wd.f_45) - SALARY_DECRYPT(wd.f_5) - SALARY_DECRYPT(wd.f_49) + "
			+"                  SALARY_DECRYPT(wd.f_339)) f_277, "
			+"                 SALARY_DECRYPT(wd.f_3)   f_3, "
			+"                 SALARY_DECRYPT(wd.f_278) f_278, "
			+"                 SALARY_DECRYPT(wd.f_279) f_279, "
			+"                 SALARY_DECRYPT(wd.f_280) f_280, "
			+"                 SALARY_DECRYPT(wd.f_46)  f_46, "
			+"                 SALARY_DECRYPT(wd.f_47)  f_47, "
			+"                 SALARY_DECRYPT(wd.f_50)  f_50, "
			+"                 (SALARY_DECRYPT(wd.f_51) - SALARY_DECRYPT(wd.f_52))   f_51, "
			+"                 SALARY_DECRYPT(wd.f_45)  f_45, "
			+"                 SALARY_DECRYPT(wd.f_44)  f_5, "
			+"                 SALARY_DECRYPT(wd.f_49)  f_49, "
			+"                 SALARY_DECRYPT(wd.f_57) + SALARY_DECRYPT(wd.f_482) + SALARY_DECRYPT(wd.f_490) f_57, "
			+"                 SALARY_DECRYPT(wd.f_488) f_58, "
			+"                 SALARY_DECRYPT(wd.f_59) + SALARY_DECRYPT(wd.f_483) + SALARY_DECRYPT(wd.f_489) f_59, "
			+"                 SALARY_DECRYPT(wd.f_456) f_456, "
			+"                 SALARY_DECRYPT(wd.f_459) f_300, "
			+"                 SALARY_DECRYPT(wd.f_68)  f_68, "
			+"                 SALARY_DECRYPT(wd.f_307) f_307, "
			+"                 SALARY_DECRYPT(wd.f_69)  f_69, "
			+"                 SALARY_DECRYPT(wd.f_487) f_480, "
			+"                 SALARY_DECRYPT(wd.f_285) f_285, "
			+"                 SALARY_DECRYPT(wd.f_286) f_286, "
			+"                 SALARY_DECRYPT(wd.f_479) f_479, "
			+"                 SALARY_DECRYPT(wd.f_302) f_302, "
			+"                 SALARY_DECRYPT(wd.f_301) f_301, "
			+"                 SALARY_DECRYPT(wd.f_303) f_303, "
			+"                 SALARY_DECRYPT(wd.f_71)  f_71, "
			+"                 SALARY_DECRYPT(wd.f_289) f_289, "
			+"                 SALARY_DECRYPT(wd.f_32)  f_32, "
			+"                 SALARY_DECRYPT(wd.f_27)  f_27, "
			+"                 SALARY_DECRYPT(wd.f_33)  f_33, "
			+"                 SALARY_DECRYPT(wd.f_28)  f_28, "
			+"                 SALARY_DECRYPT(wd.f_30)  f_30, "
			+"                 SALARY_DECRYPT(wd.f_29)  f_29, "
			+"                 SALARY_DECRYPT(wd.f_649) f_649 "
			+"          FROM wa_data wd "
			+"                   INNER JOIN bd_psndoc doc ON wd.pk_psndoc = doc.pk_psndoc "
			+"                   INNER JOIN wa_waclass wc ON wd.pk_wa_class = wc.pk_wa_class "
			+"                   LEFT outer JOIN org_dept dept ON wd.workdept = dept.pk_dept "
			+"                   LEFT outer JOIN org_dept dept_acc ON wd.workdept = dept_acc.pk_dept "
			+"                   LEFT outer JOIN hi_psnjob job ON wd.pk_psnjob = job.pk_psnjob "
			+"                   LEFT outer JOIN bd_defdoc def_jobtype ON job.jobglbdef8 = def_jobtype.pk_defdoc "
			+"                   LEFT outer JOIN bd_psncl psncl ON job.pk_psncl = psncl.pk_psncl "
			+"                   LEFT outer JOIN (SELECT wci.pk_wa_class     pk_wa_class, "
			+"                wci.cyear cyear, "
			+"                wci.cperiod cperiod, "
			+"                wd.pk_psndoc pk_psndoc, "
			+"                nvl(sum(SALARY_DECRYPT(\"Fun_getGLValue\"(wd.pk_wa_data, wi.itemkey))), 0) money "
			+"         FROM wa_data wd "
			+"                  INNER JOIN wa_classitem wci "
			+"   ON wd.pk_wa_class = wci.pk_wa_class AND wd.cyear = wci.cyear AND "
			+"      wd.cperiod = wci.cperiod "
			+"                  INNER JOIN wa_item wi ON wci.pk_wa_item = wi.pk_wa_item "
			+"                  INNER JOIN bd_defdoc defdoc ON defdoc.pk_defdoc = wci.category_id "
			+"         WHERE wi.iproperty IN (0) "
			+"           AND wci.taxflag = 'Y' "
			+"           AND defdoc.code IN "
			+"               ('5001', '6001', '6101', '6201', '6301', '6501', '7001', '7201', '7501', "
			+"                '8001') "
			+"         GROUP BY wd.pk_org, wci.pk_wa_class, wci.cyear, wci.cperiod, wd.pk_psndoc) f_464 "
			+"        ON f_464.pk_wa_class = wd.pk_wa_class AND f_464.cyear = wd.cyear AND "
			+"           f_464.cperiod = wd.cperiod AND f_464.pk_psndoc = wd.pk_psndoc "
			+"                   LEFT outer JOIN (SELECT wci.pk_wa_class     pk_wa_class, "
			+"                wci.cyear cyear, "
			+"                wci.cperiod cperiod, "
			+"                wd.pk_psndoc pk_psndoc, "
			+"                nvl(sum(SALARY_DECRYPT(\"Fun_getGLValue\"(wd.pk_wa_data, wi.itemkey))), 0) money "
			+"         FROM wa_data wd "
			+"                  INNER JOIN wa_classitem wci "
			+"   ON wd.pk_wa_class = wci.pk_wa_class AND wd.cyear = wci.cyear AND "
			+"      wd.cperiod = wci.cperiod "
			+"                  INNER JOIN wa_item wi ON wci.pk_wa_item = wi.pk_wa_item "
			+"         WHERE wi.iproperty IN (0) "
			+"           AND wci.taxflag = 'N' "
			+"           AND wi.itemkey NOT IN ('f_63', 'f_64', 'f_65', 'f_66', 'f_339') "
			+"         GROUP BY wd.pk_org, wci.pk_wa_class, wci.cyear, wci.cperiod, wd.pk_psndoc) f_463 "
			+"        ON f_463.pk_wa_class = wd.pk_wa_class AND f_463.cyear = wd.cyear AND "
			+"           f_463.cperiod = wd.cperiod AND f_463.pk_psndoc = wd.pk_psndoc "
			+"                   LEFT outer JOIN (SELECT wci.pk_wa_class     pk_wa_class, "
			+"                wci.cyear cyear, "
			+"                wci.cperiod cperiod, "
			+"                wd.pk_psndoc pk_psndoc, "
			+"                nvl(sum(SALARY_DECRYPT(\"Fun_getGLValue\"(wd.pk_wa_data, wi.itemkey))), 0) money "
			+"         FROM wa_data wd "
			+"                  INNER JOIN wa_classitem wci "
			+"   ON wd.pk_wa_class = wci.pk_wa_class AND wd.cyear = wci.cyear AND "
			+"      wd.cperiod = wci.cperiod "
			+"                  INNER JOIN wa_item wi ON wci.pk_wa_item = wi.pk_wa_item "
			+"                  INNER JOIN bd_defdoc defdoc ON defdoc.pk_defdoc = wci.category_id "
			+"         WHERE wi.iproperty IN (1) "
			+"           AND wci.taxflag = 'Y' "
			+"           AND defdoc.code IN "
			+"               ('5001', '6001', '6101', '6201', '6301', '6501', '7001', '7201', '7501', "
			+"                '8001') "
			+"         GROUP BY wd.pk_org, wci.pk_wa_class, wci.cyear, wci.cperiod, wd.pk_psndoc) f_466 "
			+"        ON f_466.pk_wa_class = wd.pk_wa_class AND f_466.cyear = wd.cyear AND "
			+"           f_466.cperiod = wd.cperiod AND f_466.pk_psndoc = wd.pk_psndoc "
			+"                   LEFT outer JOIN (SELECT wci.pk_wa_class     pk_wa_class, "
			+"                wci.cyear cyear, "
			+"                wci.cperiod cperiod, "
			+"                wd.pk_psndoc pk_psndoc, "
			+"                nvl(sum(SALARY_DECRYPT(\"Fun_getGLValue\"(wd.pk_wa_data, wi.itemkey))), 0) money "
			+"         FROM wa_data wd "
			+"                  INNER JOIN wa_classitem wci "
			+"   ON wd.pk_wa_class = wci.pk_wa_class AND wd.cyear = wci.cyear AND "
			+"      wd.cperiod = wci.cperiod "
			+"                  INNER JOIN wa_item wi ON wci.pk_wa_item = wi.pk_wa_item "
			+"                  INNER JOIN bd_defdoc defdoc ON defdoc.pk_defdoc = wci.category_id "
			+"         WHERE wi.iproperty IN (1) "
			+"           AND wci.taxflag = 'N' "
			+"           AND defdoc.code IN "
			+"               ('5001', '6001', '6101', '6201', '6301', '6501', '7001', '7201', '7501', "
			+"                '8001') "
			+"         GROUP BY wd.pk_org, wci.pk_wa_class, wci.cyear, wci.cperiod, wd.pk_psndoc) f_465 "
			+"        ON f_465.pk_wa_class = wd.pk_wa_class AND f_465.cyear = wd.cyear AND "
			+"           f_465.cperiod = wd.cperiod AND f_465.pk_psndoc = wd.pk_psndoc "
			+"      ) t_1  ";

	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
