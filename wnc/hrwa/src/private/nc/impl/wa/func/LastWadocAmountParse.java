package nc.impl.wa.func;

import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;

public class LastWadocAmountParse extends AbstractWAFormulaParse {

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		String[] arguments = getArguments(formula.toString());
		String specificItem = arguments[0].replaceAll("\'", "");

		String strSQL = "select pk_wa_item from wa_item where code = '" + specificItem + "'";
		String pk_wa_item = (String) this.getDaoManager().getBaseDao().executeQuery(strSQL, new ColumnProcessor());

		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr("coalesce("
				+ "(select SALARY_DECRYPT(nmoney) nmoney from hi_psndoc_wadoc doc  inner join wa_cacu_psnjob on wa_cacu_psnjob.pk_psndoc = doc.pk_psndoc and wa_cacu_psnjob.pk_wa_class='"
				+ getContext().getPk_wa_class()
				+ "' and creator='"
				+ getContext().getPk_loginUser()
				+ "' where doc.pk_wa_item = '"
				+ pk_wa_item
				+ "' and doc.pk_psndoc = wa_cacu_data.pk_psndoc and wa_cacu_psnjob.lastdate between doc.begindate and isnull(doc.enddate, '9999-12-31'))"
				+ ", 0)");
		return fvo;
	}

}
