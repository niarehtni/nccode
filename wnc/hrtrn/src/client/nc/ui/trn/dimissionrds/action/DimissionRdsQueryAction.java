package nc.ui.trn.dimissionrds.action;

import nc.bs.logging.Logger;
import nc.ui.hr.uif2.action.QueryAction;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.ModelDataDescriptor;

import org.apache.commons.lang.StringUtils;

public class DimissionRdsQueryAction extends QueryAction {
	protected void executeQuery(String strWhere) {
		String condition = " and 1=1 ";
		if (!StringUtils.isBlank(strWhere)) {
			condition = " and pk_psnjob in ( " + strWhere + " )";
		}

		String msSql = " and pk_hrorg = '" + getContext().getPk_org() + "' ";
		condition = condition + msSql;

		super.executeQuery(condition);
	}

	protected boolean isActionEnable() {
		return (getContext().getPk_org() != null)
				&& ((getModel().getUiState() == UIState.INIT) || (getModel()
						.getUiState() == UIState.NOT_EDIT));
	}

	protected void executeQuery(IQueryScheme queryScheme) {
		FromWhereSQL fromWhereSQL = queryScheme.getTableJoinFromWhereSQL();
		String whereSql = fromWhereSQL.getWhere();
		String fromSql = fromWhereSQL.getFrom();

		fromSql = StringUtils.isBlank(fromSql) ? " bd_psndoc inner join hi_psnjob on bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc "
				: fromSql;

		fromSql = fromSql.indexOf("hi_psnjob") < 0 ? fromSql
				+ " inner join hi_psnjob on bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc "
				: fromSql;

		if (StringUtils.isBlank(whereSql)) {
			whereSql = " 1 = 1 ";
		}

		String alias = fromWhereSQL.getTableAliasByAttrpath("hi_psnjob");
		alias = StringUtils.isBlank(alias) ? "hi_psnjob" : alias;

		whereSql = whereSql + " and " + alias.trim() + ".lastflag = 'Y' and "
				+ alias.trim() + ".ismainjob = 'Y' and " + alias.trim()
				+ ".pk_hrorg = '" + getContext().getPk_org() + "'   ";

		String sql = " select " + alias + ".pk_psnjob from " + fromSql
				+ " where " + whereSql;

		Logger.debug("SQL=[" + sql + "]");

		getModel().initModel(null,
				new ModelDataDescriptor(queryScheme.getName()));

		executeQuery(sql);
	}
}
