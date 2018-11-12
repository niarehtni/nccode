/**
 * @(#)WaProjSalary.java 1.0 2017Äê9ÔÂ20ÈÕ
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.impl.wa.func;

import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings({ "serial", "restriction" })
public class WaProjSalary extends AbstractWAFormulaParse {

	protected static final String joinField = "wa_data.pk_psndoc";

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		String[] arguments = getArguments(formula);
		String itemkey = arguments[0];
		String pk_wa_classitem = getClassItemVO(itemkey).getPk_wa_classitem();

		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("wa_data");

		StringBuffer replaceStr = new StringBuffer();
		replaceStr.append(" select sum(isnull(salaryamt,0)) from wa_projsalary ");
		replaceStr.append(" where pk_org='").append(getContext().getPk_org()).append("' ");
		replaceStr.append(" and pk_wa_calss='").append(getContext().getClassPK()).append("' ");
		replaceStr.append(" and cperiod='").append(getContext().getCyear()).append(getContext().getCperiod())
				.append("' and dr <> 1 ");
		replaceStr.append(" and pk_classitem='").append(pk_wa_classitem).append("' and pk_psndoc=").append(joinField);

		fvo.setReplaceStr(coalesce(replaceStr.toString()));

		// fvo.setAliTableName("");
		// fvo.setReplaceStr("");
		return fvo;
	}

}
