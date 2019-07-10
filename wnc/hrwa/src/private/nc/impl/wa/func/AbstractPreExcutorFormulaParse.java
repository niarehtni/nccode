package nc.impl.wa.func;

import java.util.Map;

import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.exception.DbException;
import nc.vo.dataitem.pub.DataVOUtils;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.paydata.IFormula;

/**
 * 在wa_cacu_data提前计算，为更新数据作准备
 * 
 * @author: zhangg
 * @date: 2010-5-14 上午08:59:21
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
@SuppressWarnings("serial")
public abstract class AbstractPreExcutorFormulaParse extends AbstractWAFormulaParse implements IFormula {

	/**
	 * @author zhangg on 2010-5-12
	 * @see nc.impl.wa.func.AbstractWAFormulaParse#getReplaceStr(java.lang.String)
	 */
	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {

		excute(formula, getContext());

		FunctionReplaceVO fvo = new FunctionReplaceVO();

		boolean digits = true;
		Object object = getContext().getInitData();
		if (object != null && object instanceof WaClassItemVO) {
			WaClassItemVO itemVO = (WaClassItemVO) object;
			digits = DataVOUtils.isDigitsAttribute(itemVO.getItemkey());
		}
		fvo.setAliTableName("wa_cacu_data");
		if (digits) {
			fvo.setReplaceStr("coalesce(wa_cacu_data.cacu_value, 0)");
		} else {
			fvo.setReplaceStr(" wa_cacu_data.char_value ");
		}
		return fvo;
	}

	protected void writeToTempData(String pk_wa_class, String creator, Map<String, UFDouble[]> ovtFeeResult) {
		// 批量更新
		PersistenceManager sessionManager = null;
		try {
			sessionManager = PersistenceManager.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			for (String pk_psndoc : ovtFeeResult.keySet()) {
				UFDouble amountTaxFree = (ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult.get(pk_psndoc).length < 1) ? UFDouble.ZERO_DBL
						: ovtFeeResult.get(pk_psndoc)[0];
				UFDouble amountTaxable = (ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult.get(pk_psndoc).length < 3) ? UFDouble.ZERO_DBL
						: ovtFeeResult.get(pk_psndoc)[2];
				session.addBatch("delete from wa_cacu_overtimefee where pk_wa_class='" + pk_wa_class
						+ "' and creator='" + creator + "' and pk_psndoc='" + pk_psndoc + "' and intcomp=0;");
				session.addBatch("insert into wa_cacu_overtimefee (pk_wa_class, creator, pk_psndoc, intcomp, amounttaxfree, amounttaxable) values ('"
						+ pk_wa_class
						+ "','"
						+ creator
						+ "','"
						+ pk_psndoc
						+ "',"
						+ String.valueOf(0)
						+ ","
						+ amountTaxFree.toString() + "," + amountTaxable.toString() + ");");

				amountTaxFree = (ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult.get(pk_psndoc).length < 2) ? UFDouble.ZERO_DBL
						: ovtFeeResult.get(pk_psndoc)[1];
				amountTaxable = (ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult.get(pk_psndoc).length < 4) ? UFDouble.ZERO_DBL
						: ovtFeeResult.get(pk_psndoc)[3];
				session.addBatch("delete from wa_cacu_overtimefee where pk_wa_class='" + pk_wa_class
						+ "' and creator='" + creator + "' and pk_psndoc='" + pk_psndoc + "' and intcomp=1;");
				session.addBatch("insert into wa_cacu_overtimefee (pk_wa_class, creator, pk_psndoc, intcomp, amounttaxfree, amounttaxable) values ('"
						+ pk_wa_class
						+ "','"
						+ creator
						+ "','"
						+ pk_psndoc
						+ "',"
						+ String.valueOf(1)
						+ ","
						+ amountTaxFree.toString() + "," + amountTaxable.toString() + ");");

				amountTaxFree = ((ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult.get(pk_psndoc).length < 1) ? UFDouble.ZERO_DBL
						: ovtFeeResult.get(pk_psndoc)[0]).add((ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult
						.get(pk_psndoc).length < 2) ? UFDouble.ZERO_DBL : ovtFeeResult.get(pk_psndoc)[1]);
				amountTaxable = ((ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult.get(pk_psndoc).length < 3) ? UFDouble.ZERO_DBL
						: ovtFeeResult.get(pk_psndoc)[2]).add((ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult
						.get(pk_psndoc).length < 4) ? UFDouble.ZERO_DBL : ovtFeeResult.get(pk_psndoc)[3]);
				session.addBatch("delete from wa_cacu_overtimefee where pk_wa_class='" + pk_wa_class
						+ "' and creator='" + creator + "' and pk_psndoc='" + pk_psndoc + "' and intcomp=2;");
				session.addBatch("insert into wa_cacu_overtimefee (pk_wa_class, creator, pk_psndoc, intcomp, amounttaxfree, amounttaxable) values ('"
						+ pk_wa_class
						+ "','"
						+ creator
						+ "','"
						+ pk_psndoc
						+ "',"
						+ String.valueOf(2)
						+ ","
						+ amountTaxFree.toString() + "," + amountTaxable.toString() + ");");
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
