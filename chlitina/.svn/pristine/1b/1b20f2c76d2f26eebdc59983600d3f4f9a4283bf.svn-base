package nc.impl.ta.overtime;

import java.util.Map;
import java.util.Map.Entry;

import nc.impl.pub.OTLeaveBalancePubServiceImpl;
import nc.itf.ta.overtime.IOTLeaveBalanceMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.query2.sql.process.QueryCondition;
import nc.vo.ta.overtime.OTLeaveBalanceVO;

public class OTLeaveBalanceMaintainImpl extends OTLeaveBalancePubServiceImpl implements IOTLeaveBalanceMaintain {
    @Override
    public OTLeaveBalanceVO[] query(IQueryScheme queryScheme, String pk_leavetypecopy, String beginDate, String endDate)
	    throws BusinessException {
	OTLeaveBalanceVO[] bills = null;
	try {
	    Map<String, QueryCondition> conds = (Map<String, QueryCondition>) queryScheme.get("all_condition");
	    String pk_org = null;
	    UFBoolean isfrozen = null;
	    String[] pk_psndocs = null;
	    String[] pk_depts = null;
	    String maxdate = "";
	    if (conds != null && conds.size() > 0) {
		for (Entry<String, QueryCondition> cond : conds.entrySet()) {
		    String strKey = cond.getKey();
		    QueryCondition condition = cond.getValue();
		    if (strKey.equals("pk_org")) {
			if (condition.getValues() != null && condition.getValues().length > 0) {
			    pk_org = condition.getValues()[0];
			}
		    } else if (strKey.equals("isfrozen")) {
			if (condition.getValues() != null && condition.getValues().length > 0) {
			    isfrozen = new UFBoolean(condition.getValues()[0]);
			}
		    } else if (strKey.equals("pk_psndoc")) {
			if (condition.getValues() != null && condition.getValues().length > 0) {
			    pk_psndocs = condition.getValues();
			}
		    } else if (strKey.equals("pk_dept")) {
			if (condition.getValues() != null && condition.getValues().length > 0) {
			    pk_depts = condition.getValues();
			}
		    } else if (strKey.equals("maxdate")) {
			if (condition.getValues() != null && condition.getValues().length > 0) {
			    maxdate = condition.getValues()[0];
			}
		    }
		}

		bills = queryOTLeaveAggvos(pk_org, pk_psndocs, pk_depts, pk_leavetypecopy, maxdate, beginDate, endDate);
	    }
	} catch (Exception e) {
	    ExceptionUtils.wrappException(e);
	}
	return bills;
    }
}
