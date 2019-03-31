package nc.impl.wa.taxupgrade_tool;

import java.util.ArrayList;
import java.util.List;

import nc.bs.dao.DAOException;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.SQLHelper;
import nc.jdbc.framework.SQLParameter;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.wa.item.WaItemVO;

/**
 * 税改快速试试工具
 * 
 * @author: xuhw
 * @date: 2018-12-04
 * @since: eHR V6.5
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class Taxupgrade_toolDAO extends BaseDAOManager {

	public GeneralVO[] queryTargetClassInfo(String pk_group, String cyear, String cperiod, String unInclude_pk_wa_class)
			throws DAOException {
		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT ");
		sql.append(" (	SELECT "
				+ SQLHelper.getMultiLangNameColumn("name")
				+ "  FROM org_orgs WHERE pk_org = waclass.pk_org ) orgname ,waclass.code, waclass."
				+ SQLHelper.getMultiLangNameColumn("name")
				+ " , waclass.cyear, waclass.cperiod, waclass.pk_wa_class, waclass.pk_group, waclass.pk_org, periodstate.checkflag, ");
		sql.append(" (	SELECT");
		sql.append(" 		1 ");
		sql.append(" 	FROM");
		sql.append(" 		wa_classitem ");
		sql.append(" 	WHERE ");
		sql.append(" 		pk_wa_class = waclass.pk_wa_class AND ");
		sql.append(" 		cyear = waclass.cyear AND ");
		sql.append(" 		cperiod = waclass.cperiod AND ");
		sql.append(" 		pk_wa_item IN (	SELECT ");
		sql.append(" 							pk_wa_item ");
		sql.append(" 						FROM");
		sql.append(" 							wa_item waitem ");
		sql.append(" 						WHERE");
		sql.append(" 							code = 'taxable_income_yz'");
		sql.append(" 		)");
		sql.append(" ) done ");
		sql.append(" ,( select count(1)  from  wa_data where  wa_data.pk_wa_class = waclass.pk_wa_class AND");
		sql.append(" 		wa_data.cyear = waclass.cyear AND");
		sql.append(" 		wa_data.cperiod = waclass.cperiod AND");
		sql.append(" 		wa_data.checkflag = 'Y' ) hascheck ");
		sql.append(" ,( select count(1)  from  wa_data where  wa_data.pk_wa_class = waclass.pk_wa_class AND");
		sql.append(" 		wa_data.cyear = waclass.cyear AND");
		sql.append(" 		wa_data.cperiod = waclass.cperiod  ) hasdata ");
		sql.append(" FROM ");
		sql.append(" wa_waclass waclass inner join wa_periodscheme periodscheme on waclass.pk_periodscheme = periodscheme.pk_periodscheme ");
		sql.append("   inner join wa_periodstate periodstate on periodstate.pk_wa_class = waclass.pk_wa_class  ");
		sql.append("  inner join wa_period period on period.pk_wa_period = periodstate.pk_wa_period and period.cyear = waclass.cyear and period.cperiod = waclass.cperiod ");
		sql.append(" WHERE ");
		sql.append(" 	waclass.pk_group = ? AND ");
		sql.append(" 			periodstate.enableflag = 'Y' AND ");
		sql.append(" 			periodstate.checkflag <> 'Y' AND ");
		// sql.append(" 			periodstate.isapporve <> 'Y' AND ");
		sql.append(" 			periodstate.payoffflag <> 'Y' ");
		sql.append(" 			ORDER BY waclass.pk_org , waclass.cyear, waclass.cperiod, waclass.NAME");

		SQLParameter param = new SQLParameter();
		param.addParam(pk_group);

		GeneralVO[] genvos = executeQueryVOs(sql.toString(), param, GeneralVO.class);
		List<GeneralVO> unApprove = new ArrayList<GeneralVO>();

		// 查询薪资数据，是否有数据审核，有审核的跳过
		if (genvos != null && genvos.length > 0) {
			for (GeneralVO vo : genvos) {
				Integer intcheck = Integer.parseInt(vo.getAttributeValue("hascheck") + "");
				if (intcheck == 0) {
					unApprove.add(vo);
				}
			}
		}
		return unApprove.toArray(new GeneralVO[0]);
	}

}
