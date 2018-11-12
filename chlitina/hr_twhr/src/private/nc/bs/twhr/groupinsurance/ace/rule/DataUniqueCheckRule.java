package nc.bs.twhr.groupinsurance.ace.rule;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.impl.pubapp.pattern.pub.LockOperator;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.twhr.groupinsurance.GroupInsuranceSettingVO;

public class DataUniqueCheckRule implements IRule<BatchOperateVO> {

	@Override
	public void process(BatchOperateVO[] vos) {
		if (vos == null || vos.length == 0) {
			return;
		}
		Object[] oadd = vos[0].getAddObjs();
		Object[] oupd = vos[0].getUpdObjs();
		// 如果没有新增和修改的数据，则不需要校验
		GroupInsuranceSettingVO[] vosadd = null;
		if (oadd != null && oadd.length > 0) {
			vosadd = this.convertArrayType(oadd);
			this.checkDBUnique(vosadd);
			// return;
		}
		GroupInsuranceSettingVO[] vosupd = null;
		if (oupd != null && oupd.length > 0) {
			vosupd = this.convertArrayType(oupd);
			this.checkDBUnique(vosupd);
			// return;
		}
	}

	public void checkDBUnique(GroupInsuranceSettingVO[] bills) {
		if (bills == null || bills.length == 0) {
			return;
		}

		List<String> savingCombines = new ArrayList<String>();

		for (int j = 0; j < bills.length; j++) {
			GroupInsuranceSettingVO vo = bills[j];
			if (vo.getPrimaryKey() == null) {
				// 本次新增的數據
				IRowSet rowSet = new DataAccessUtils().query(this.getCheckSql(
						vo, "INS"));
				if (rowSet.size() > 0) {
					ExceptionUtils
							.wrappBusinessException(nc.vo.ml.NCLangRes4VoTransl
									.getNCLangRes().getStrByID("68J61035",
											"01035001-0005")/*
															 * @res 保存失败，
															 * 新增的数据中存在相同投保险种及投保身份组合的记录
															 * 。
															 */);
				}
			} else {
				// 本次更新的數據
				GroupInsuranceSettingVO[] dbvo = new VOQuery<GroupInsuranceSettingVO>(
						GroupInsuranceSettingVO.class).query(new String[] { vo
						.getPrimaryKey() });
				this.doLock(dbvo);
				IRowSet rowSet = new DataAccessUtils().query(this.getCheckSql(
						vo, "UPD"));
				if (rowSet.size() > 0) {
					ExceptionUtils
							.wrappBusinessException(nc.vo.ml.NCLangRes4VoTransl
									.getNCLangRes().getStrByID("68J61035",
											"01035001-0006")/*
															 * @res 保存失败，
															 * 修改的数据中存在相同投保险种及投保身份组合的记录
															 * 。
															 */);
				}
			}
			// 本次保存内容重复
			if (savingCombines.contains(vo.getCgrpinsid() + "|"
					+ vo.getCgrpinsrelid())) {
				ExceptionUtils
						.wrappBusinessException(nc.vo.ml.NCLangRes4VoTransl
								.getNCLangRes().getStrByID("68J61035",
										"01035001-0007")/*
														 * @res 保存失败，
														 * 本次保存的数据中存在相同投保险种及投保身份组合的记录
														 * 。
														 */);
			} else {
				savingCombines.add(vo.getCgrpinsid() + "|"
						+ vo.getCgrpinsrelid());
			}
		}
	}

	private GroupInsuranceSettingVO[] convertArrayType(Object[] vos) {
		GroupInsuranceSettingVO[] smartVOs = (GroupInsuranceSettingVO[]) Array
				.newInstance(GroupInsuranceSettingVO.class, vos.length);
		System.arraycopy(vos, 0, smartVOs, 0, vos.length);
		return smartVOs;
	}

	private void doLock(GroupInsuranceSettingVO[] bills) {
		List<String> lockobj = new ArrayList<String>();
		for (int i = 0; i < bills.length; i++) {
			lockobj.add("#cgrpinsid_cgrpinsrelid#");
		}
		LockOperator lock = new LockOperator();
		lock.lock(
				lockobj.toArray(new String[lockobj.size()]),
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
						"68J61035", "01035001-0008")/*
													 * @res
													 * 当前单据记录有其他用户在操作，请稍候刷新后再操作
													 */);
	}

	/**
	 * 拼接唯一性校验的sql
	 * 
	 * @param bill
	 * @return
	 */
	private String getCheckSql(GroupInsuranceSettingVO vo, String opr) {
		StringBuffer sql = new StringBuffer();
		sql.append("select cgrpinsid,cgrpinsrelid ");
		sql.append("  from ");
		sql.append(vo.getTableName());

		sql.append(" where ");
		sql.append(" (cgrpinsid ='");
		sql.append(vo.getCgrpinsid());
		sql.append("' ");
		sql.append(" and ");
		sql.append(" cgrpinsrelid='");
		sql.append(vo.getCgrpinsrelid());
		sql.append("' ");
		sql.append(") and dr=0 ");
		sql.append(" and pk_org='" + vo.getPk_org() + "' ");
		if (opr.equals("UPD")) {
			sql.append(" and id <> '" + vo.getPrimaryKey() + "'");
		}
		// sql.append(" group by code ");
		// sql.append(" having count(1) > 1;");
		return sql.toString();
	}

}