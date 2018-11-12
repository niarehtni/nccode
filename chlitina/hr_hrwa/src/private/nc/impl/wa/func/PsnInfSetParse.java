package nc.impl.wa.func;

import nc.hr.utils.ResHelper;
import nc.impl.wa.paydata.precacu.PsnInfoPreExcutor;
import nc.vo.hr.formula.IFormulaConst;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.paydata.IFormula;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.pub.WaLoginContext;

/**
 *
 * @author: zhangg
 * @date: 2010-6-8 下午04:56:00
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class PsnInfSetParse  extends AbstractPreExcutorFormulaParse {

	private static final long serialVersionUID = 1L;


	private UFLiteralDate getDate(String date) throws BusinessException{
		//	l_date += IFormulaConst.splitSymbol + (getcmbDate().getSelectedIndex() == 0 ? "cstartdate" : "cenddate");
		String[] dates = date.split(IFormulaConst.splitSymbol);
		if(dates == null || (dates.length != 2 && dates.length != 3) ){
			throw new BusinessException(ResHelper.getString("6013salarypmt","06013salarypmt0267")/*@res "取信息集的日期不正确."*/);
		}
		if(dates.length == 2){
			Integer pre = new Integer(dates[0]);
			boolean isStart = dates[1].equalsIgnoreCase("cstartdate");
			return getAbsPeriod(pre, isStart);
		}else{
			return new UFLiteralDate(dates[0] + "-" + dates[1] + "-" + dates[2]);
		}
	}

	/**
	 * 通过相对期间找绝对期间，若找不到则返回null
	 * @param i int 相对偏移数（前几期间，》0时有效，否则直接返回当前；若找不到则返回{1900，01}）
	 * @exception javsql.SQLException 异常说明。
	 */
	private UFLiteralDate getAbsPeriod(int i, boolean isStart) throws BusinessException {


		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select wa_period.cstartdate, wa_period.cenddate "); // 1
		sqlB.append("  from wa_periodstate ");
		sqlB.append(" inner join wa_period on (wa_periodstate.pk_wa_period = ");
		sqlB.append("                         wa_period.pk_wa_period) ");
		sqlB.append(" where pk_wa_class = '" + getContext().getPk_wa_class() + "' ");
		sqlB.append("   and (cyear || cperiod) <= '" + getContext().getWaYear() + getContext().getWaPeriod() + "' ");
		sqlB.append(" order by  cyear||cperiod desc");

		PeriodVO[] periodVOs = getDaoManager().executeQueryVOs(sqlB.toString(), PeriodVO.class);
		if(periodVOs != null){
			for (int j = 0; j < periodVOs.length; j++) {
				if(j == i){//找到啦,则附值、退出
					if(isStart){
						return periodVOs[j].getCstartdate();
					}else{
						return periodVOs[j].getCenddate();
					}
				}
			}
		}
		return  null;
	}

	/**
	 * // table_code, item_code, code_name, date, ref_table, ref_item_code
	 * @author zhangg on 2010-6-21
	 * @see nc.vo.wa.paydata.IFormula#excute(java.lang.Object, nc.vo.wa.pub.WaLoginContext)
	 */
	@Override
	public void excute(Object formula, WaLoginContext context) throws BusinessException {

		String[] arguments = getArguments(formula.toString());
		//String table_code = arguments[0];
		// String item_code = arguments[1];
		// String code_name = arguments[2];
		String date = arguments[3];
		// String ref_table = arguments[4];
		// String ref_item_code = arguments[5];
		
		if(isNull(date)){
			date = null;
		}else{
			UFLiteralDate ufDate = getDate(date);
			date = ufDate == null? "9999-09-09": ufDate.toString();
		}

		arguments[3] = date;

		IFormula excutor = new PsnInfoPreExcutor();
		excutor.excute(arguments, getContext());



	}


}