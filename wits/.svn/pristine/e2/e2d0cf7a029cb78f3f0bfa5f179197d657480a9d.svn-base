/**
 * @(#)UpdatePaydataStateRule.java 1.0 2017Äê9ÔÂ18ÈÕ
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.bs.hrwa.projsalary.ace.rule;

import java.util.HashSet;
import java.util.Set;

import nc.hr.frame.persistence.AppendBaseDAO;
import nc.impl.pub.util.db.InSqlManager;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.ecpubapp.pattern.exception.ExceptionUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.projsalary.AggProjSalaryVO;
import nc.vo.wa.projsalary.ProjSalaryHVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * @author niehg
 * @since 6.3
 */
public class UpdatePaydataStateRule implements IRule<AggProjSalaryVO> {

	@Override
	public void process(AggProjSalaryVO[] bills) {
		if (ArrayUtils.isEmpty(bills)) {
			return;
		}
		ProjSalaryHVO hvo = bills[0].getParentVO();
		StringBuilder baseConditon = new StringBuilder();
		baseConditon.append(" pk_org='").append(hvo.getPk_org()).append("' ");
		baseConditon.append(" and pk_wa_class='").append(hvo.getPk_wa_calss()).append("' ");

		StringBuilder periodstateWhere = new StringBuilder(baseConditon);
		periodstateWhere.append(" and pk_wa_period in (select pk_wa_period from wa_period where ");
		periodstateWhere.append(" cyear='").append(hvo.getCperiod().substring(0, 4)).append("' ");
		periodstateWhere.append(" and cperiod='").append(hvo.getCperiod().substring(4, 6)).append("') ");

		StringBuilder paydataWhere = new StringBuilder(baseConditon);
		paydataWhere.append(" and cyearperiod='").append(hvo.getCperiod()).append("' ");
		paydataWhere.append(" and checkflag='N' and stopflag='N' and caculateflag='Y' ");
		Set<String> psndocPkSet = new HashSet<String>();
		for (AggProjSalaryVO aggVO : bills) {
			psndocPkSet.add(aggVO.getParentVO().getPk_psndoc());
		}
		paydataWhere.append(" and pk_psndoc in ").append(InSqlManager.getInSQLValue(psndocPkSet));

		AppendBaseDAO appBaseDao = new AppendBaseDAO();
		try {
			appBaseDao.updateTableByColKey("wa_data", "caculateflag", UFBoolean.FALSE, paydataWhere.toString());
			appBaseDao.updateTableByColKey("wa_periodstate", "caculateflag", UFBoolean.FALSE,
					periodstateWhere.toString());
			// appBaseDao.updateTableByColKey("wa_periodstate", "checkflag",
			// UFBoolean.FALSE, periodstateWhere.toString());
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}

	}

}
