package nc.impl.wa.func;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.pub.WaLoginContext;

/**
 * #21266 按日合计加班费函数解析器
 * 
 * @author yejk
 * @date 2018-9-7
 */
@SuppressWarnings({ "serial", "restriction" })
public class OvertimeFeeParse extends AbstractPreExcutorFormulaParse {

    /**
     * @Description: 执行解析
     * @author yejk
     * @date 2018-9-7
     * @param formula
     * @param waLoginContext
     * @throws BusinessException
     * @return
     */
    @Override
    public void excute(Object formula, WaLoginContext waLoginContext) throws BusinessException {
	BaseDAO basedao = new BaseDAO();
	// 薪资方案主键
	String pk_wa_class = waLoginContext.getWaLoginVO().getPk_wa_class();
	// 组织
	String pk_org = waLoginContext.getPk_org();
	// 薪资期间
	// UFLiteralDate startDate =
	// waLoginContext.getWaLoginVO().getPeriodVO().getCstartdate();
	// UFLiteralDate endDate =
	// waLoginContext.getWaLoginVO().getPeriodVO().getCenddate();
	// 薪资期间年份
	String caccyear = waLoginContext.getWaLoginVO().getPeriodVO().getCaccyear();
	// 薪资期间月份
	String cperiod = waLoginContext.getWaLoginVO().getPeriodVO().getCperiod();

	/* 通过薪资期间获取考勤期间的起止日期 start */
	String queryDateSql = "select tbm_period.begindate,tbm_period.enddate from tbm_period where tbm_period.accyear = ?  and tbm_period.accmonth  = ? and tbm_period.pk_org = ?";
	SQLParameter params = new SQLParameter();
	params.addParam(caccyear);
	params.addParam(cperiod);
	params.addParam(pk_org);
	@SuppressWarnings("unchecked")
	List<Map<String, Object>> dateListMap = (List<Map<String, Object>>) basedao.executeQuery(queryDateSql, params,
		new MapListProcessor());
	if (null == dateListMap) {
	    throw new BusinessException("通过薪资期间获取考勤期间起止日期为空");
	}
	UFLiteralDate startDate = new UFLiteralDate(dateListMap.get(0).get("begindate").toString());
	UFLiteralDate endDate = new UFLiteralDate(dateListMap.get(0).get("enddate").toString());
	/* 通过薪资期间获取考勤期间的起止日期 end */

	Pattern p = Pattern.compile("\\d");
	Matcher m = p.matcher(formula.toString());
	// 是否免税 0否 1是
	int flag = 0;
	if (m.find()) {
	    flag = Integer.valueOf(m.group());
	}
	/* 获取计算人员集合 start */
	String psndocsSql = "select wa_cacu_data.pk_psndoc from wa_cacu_data where wa_cacu_data.pk_wa_class = '"
		+ pk_wa_class + "'";

	List<String> psndocList = new ArrayList<String>();
	@SuppressWarnings("unchecked")
	List<Map<String, Object>> result = (List<Map<String, Object>>) basedao.executeQuery(psndocsSql,
		new MapListProcessor());
	if (null == result) {
	    throw new BusinessException("应税(免税)加班费计算-获取人员pk为空");
	}
	for (int i = 0; i < result.size(); i++) {
	    Map<String, Object> map = result.get(i);
	    String pk_psndoc1 = map.get("pk_psndoc").toString();
	    psndocList.add(pk_psndoc1);
	}
	String[] psndocArr = psndocList.toArray(new String[0]);
	/* 获取计算人员集合 end */

	// 调用接口返回应税或免税加班费
	ISegDetailService segDetailService = NCLocator.getInstance().lookup(ISegDetailService.class);
	Map<String, UFDouble[]> ovtFeeResult = segDetailService.calculateTaxableByDate(pk_org, psndocArr, startDate,
		endDate, null);
	if (null == ovtFeeResult) {
	    throw new BusinessException("调用接口ISegDetailService获取应税(免税)加班费为空");
	}
	// 批量更新
	PersistenceManager sessionManager = null;
	try {
	    sessionManager = PersistenceManager.getInstance();
	    JdbcSession session = sessionManager.getJdbcSession();
	    for (int i = 0; i < psndocArr.length; i++) {
		String updateSql = "update wa_cacu_data set cacu_value = ? where pk_wa_class = ? and pk_psndoc = ?";
		SQLParameter parameter = new SQLParameter();
		if (flag == 1) {// 1是免税 加班费
		    parameter.addParam((ovtFeeResult.get(psndocArr[i])[0]).getDouble());
		} else {// 否则 应税加班费
		    parameter.addParam((ovtFeeResult.get(psndocArr[i])[1]).getDouble());
		}
		parameter.addParam(pk_wa_class);
		parameter.addParam(psndocArr[i]);
		session.addBatch(updateSql, parameter);
	    }
	    session.executeBatch();
	} catch (DbException e) {
	    e.printStackTrace();
	} finally {
	    if (sessionManager != null) {
		sessionManager.release();
	    }
	}
    }
}
