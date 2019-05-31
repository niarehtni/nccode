package nc.impl.wa.func;

import nc.bs.dao.BaseDAO;
import nc.itf.ta.ItemServiceFacade;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.ta.timeitem.LeaveTypeVO;

public class TaHolidayLeftDaysParse extends TaMonthRecordParse {
	private static final long serialVersionUID = 2270792010930117410L;

	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		String[] arguments = getArguments(formula);
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		String accyear = getAccyear();
		String accperiod = getAccperiod();
		String jointable = " wa_cacu_data ";
		String joinpkpsndoc = " wa_cacu_data.pk_psndoc ";
		String joinyear = " wa_cacu_data.tayear ";
		String joinperiod = " wa_cacu_data.taperiod ";
		String joinhrorg = " wa_cacu_data.pk_ta_org ";

		LeaveTypeVO timeItem = new LeaveTypeVO();
		timeItem.setPk_timeitem(arguments[0]);
		String updateTAItemData = ItemServiceFacade.updateTAItemData(accyear, accperiod, "wa_cacu_data.workorg",
				jointable, getDataBaseType());

		if (isFirstupdateTa(updateTAItemData)) {
			new BaseDAO().executeUpdate(updateTAItemData);
		}
		String sql = ItemServiceFacade.proYearLeave(timeItem, joinpkpsndoc, joinhrorg, joinyear, joinperiod, jointable);
		fvo.setAliTableName("wa_data");
		// mod by Connie.ZH
		// 2019-05-25 started
		// wage calculation efficiency
		// fvo.setReplaceStr(coalesce(sql));
		fvo.setReplaceStr(sql);
		// 2019-05-25 ended
		return fvo;
	}
}
