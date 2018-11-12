package nc.impl.wa.func;

import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.YearPeriodSeperatorVO;

/**
 * 取指定类别指定期间的薪资数据
 * 
 * @author: zhangg
 * @date: 2010-5-10 下午03:36:14
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
@SuppressWarnings("serial")
public class PayDataPD extends AbstractWAFormulaParse {

	private String pk_wa_class;

	public String getPk_wa_class() {
		return pk_wa_class;
	}

	public void setPk_wa_class(String pk_wa_class) {
		this.pk_wa_class = pk_wa_class;
	}

	/**
	 * （参数：薪资方案，薪资项目，薪资年，薪资期间） (为空时默认相同项目) 在设置时就赋予相同的项目, 使其不会出现这种情况
	 * 
	 * @author zhangg on 2010-5-10
	 * @throws BusinessException
	 * @see nc.impl.wa.func.AbstractWAFormulaParse#getReplaceStr(java.lang.String)
	 */
	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		String[] arguments = getArguments(formula);

		FunctionReplaceVO fvo = new FunctionReplaceVO();
		String classpk = arguments[0];

		String itemid = arguments[1];
		String period = arguments[2];
		classpk = trans2OrgPk(classpk);
		setPk_wa_class(arguments[0]);
		YearPeriodSeperatorVO yearPeriodSeperatorVO = trans2YearPeriodSeperatorVO(period);

		fvo.setAliTableName("wa_data");
		StringBuffer sqlBuffer = new StringBuffer();

		if (period.equals("1")) {
			sqlBuffer.append(getLastPeriodSQL(getPk_wa_class(), itemid, yearPeriodSeperatorVO));
		} else {
			// 當前期間
			sqlBuffer.append("select data_source." + itemid + " "); // 1
			sqlBuffer.append("  from wa_data data_source ");
			sqlBuffer.append(" where data_source.pk_wa_class = '" + getPk_wa_class() + "' ");
			sqlBuffer.append("   and data_source.cyear = '" + yearPeriodSeperatorVO.getYear() + "' ");
			sqlBuffer.append("   and data_source.cperiod = '" + yearPeriodSeperatorVO.getPeriod() + "' ");
			sqlBuffer.append("   and data_source.pk_psndoc = wa_data.pk_psndoc and  data_source.stopflag = 'N' ");
		}

		fvo.setReplaceStr(coalesce(sqlBuffer.toString()));
		return fvo;

	}

	@Override
	public YearPeriodSeperatorVO getAbsPeriod(int i) throws BusinessException {
		if (i == 0) {
			return getSamePeriod();// 返回当前期间
		}
		YearPeriodSeperatorVO yearPeriodSeperatorVO = new YearPeriodSeperatorVO("190001");

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select (cyear || cperiod) yearperiod "); // 1
		sqlB.append("  from wa_periodstate ");
		sqlB.append(" inner join wa_period on (wa_periodstate.pk_wa_period = ");
		sqlB.append("                         wa_period.pk_wa_period) ");
		// guoqt取薪资方案
		sqlB.append(" where pk_wa_class = '" + getPk_wa_class() + "' ");
		sqlB.append("   and (cyear || cperiod) <= '" + getContext().getWaYear() + getContext().getWaPeriod() + "' ");
		sqlB.append(" order by yearperiod desc");

		YearPeriodSeperatorVO[] periodSeperatorVOs = getDaoManager().executeQueryVOs(sqlB.toString(),
				YearPeriodSeperatorVO.class);
		if (periodSeperatorVOs != null) {
			for (int j = 0; j < periodSeperatorVOs.length; j++) {
				if (j == i) {// 找到啦,则附值、退出
					return periodSeperatorVOs[j];
				}
			}
		}
		return yearPeriodSeperatorVO;
	}

	/**
	 * guoqt 跨薪资方案取值上一期间 20141015
	 * 
	 * @return
	 */
	@Override
	public void setClassforPeriod(String pk_wa_class) throws BusinessException {
		setPk_wa_class(pk_wa_class);
	}
}
