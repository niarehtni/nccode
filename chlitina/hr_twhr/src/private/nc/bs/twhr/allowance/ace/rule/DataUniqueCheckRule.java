package nc.bs.twhr.allowance.ace.rule;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import nc.bs.ml.NCLangResOnserver;
import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.impl.pubapp.pattern.pub.LockOperator;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.twhr.allowance.AllowanceVO;

public class DataUniqueCheckRule implements IRule<BatchOperateVO> {

	@Override
	public void process(BatchOperateVO[] vos) {
		if (vos == null || vos.length == 0) {
			return;
		}
		Object[] oadd = vos[0].getAddObjs();
		Object[] oupd = vos[0].getUpdObjs();
		// 如果没有新增和修改的数据，则不需要校验
		AllowanceVO[] vosadd = null;
		if (oadd != null && oadd.length > 0) {
			vosadd = this.convertArrayType(oadd);
			this.checkDBUnique(vosadd);
			// return;
		}
		AllowanceVO[] vosupd = null;
		if (oupd != null && oupd.length > 0) {
			vosupd = this.convertArrayType(oupd);
			this.checkDBUnique(vosupd);
			// return;
		}
	}

	private List<String> codeList = new ArrayList<String>();
	private List<String> nameList = new ArrayList<String>();

	public void checkDBUnique(AllowanceVO[] bills) {
		if (bills == null || bills.length == 0) {
			return;
		}

		for (int j = 0; j < bills.length; j++) {
			AllowanceVO vo = bills[j];
			if (vo.getPrimaryKey() == null) {
				// 本次新增的數據
				IRowSet rowSet = new DataAccessUtils().query(this.getCheckSql(
						vo, "INS"));
				if (rowSet.size() > 0) {
					ExceptionUtils
							.wrappBusinessException(NCLangResOnserver.getInstance().getStrByID("68860545", "DataUniqueCheckRule-0000", null, new String[]{vo.getCode(),vo.getName()})/*保存失败，当前新增的数据已经存在编码[{0}]或名称[{1}]相同的记录。*/);
				}
			} else {
				// 本次更新的數據
				AllowanceVO[] dbvo = new VOQuery<AllowanceVO>(AllowanceVO.class)
						.query(new String[] { vo.getPrimaryKey() });
				this.doLock(dbvo);
				IRowSet rowSet = new DataAccessUtils().query(this.getCheckSql(
						vo, "UPD"));
				if (rowSet.size() > 0) {
					ExceptionUtils
							.wrappBusinessException(NCLangResOnserver.getInstance().getStrByID("68860545", "DataUniqueCheckRule-0001", null, new String[]{vo.getCode(),vo.getName()})/*保存失败，当前修改的数据已经存在编码[{0}]或名称[{1}]相同的记录。*/);
				}
			}
			// 本次保存内容重复
			if (codeList.contains(vo.getCode())
					|| nameList.contains(vo.getName())) {
				ExceptionUtils.wrappBusinessException(NCLangResOnserver.getInstance().getStrByID("68860545", "DataUniqueCheckRule-0002", null, new String[]{vo.getCode(),vo.getName()})/*保存失败，当前维护的数据已经存在编码[{0}]或名称[{1}]相同的记录。*/);
			} else {
				codeList.add(vo.getCode());
				nameList.add(vo.getName());
			}
		}
	}

	private AllowanceVO[] convertArrayType(Object[] vos) {
		AllowanceVO[] smartVOs = (AllowanceVO[]) Array.newInstance(
				AllowanceVO.class, vos.length);
		System.arraycopy(vos, 0, smartVOs, 0, vos.length);
		return smartVOs;
	}

	private void doLock(AllowanceVO[] bills) {
		List<String> lockobj = new ArrayList<String>();
		for (int i = 0; i < bills.length; i++) {
			lockobj.add("#code_name#");
		}
		LockOperator lock = new LockOperator();
		lock.lock(lockobj.toArray(new String[lockobj.size()]),
				NCLangResOnserver.getInstance().getStrByID("68860545", "DataUniqueCheckRule-0003")/*当前单据记录有其他用户在操作，请稍候刷新后再操作*/);
	}

	/**
	 * 拼接唯一性校验的sql
	 * 
	 * @param bill
	 * @param opr
	 * @return
	 */
	private String getCheckSql(AllowanceVO vo, String opr) {
		StringBuffer sql = new StringBuffer();
		sql.append("select code,name ");
		sql.append("  from ");
		sql.append(vo.getTableName());

		sql.append(" where dr=0 ");
		sql.append(" and ");
		sql.append(" (code ='");
		sql.append(vo.getCode());
		sql.append("' ");
		sql.append(" or ");
		sql.append(" name='");
		sql.append(vo.getName());
		sql.append("' ");
		sql.append(") and dr=0 ");
		if (opr.equals("UPD")) {
			sql.append(" and id <> '" + vo.getPrimaryKey() + "'");
		}
		// sql.append(" group by code ");
		// sql.append(" having count(1) > 1;");
		return sql.toString();
	}

}
