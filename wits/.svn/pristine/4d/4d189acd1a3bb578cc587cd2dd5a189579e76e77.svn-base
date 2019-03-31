package nc.impl.wa.func_tax;

import nc.bs.dao.BaseDAO;
import nc.impl.wa.func.AbstractWAFormulaParse;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.hr.tools.dbtool.util.db.DBUtil;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa_tax.TaxUpgradeHelper;

/**
 * Get (value) 新税改_附加专项扣除
 * 根据条件获取附加项目扣除额
 * String[] ml = {"0","1","2","3","4","5"};
		String[] mlDefault = new String[]{"全部","子女教育","继续教育","住房贷款利息","住房租金","赡养老人"};
 * 
 * 附加专项扣除
 * 
 * @author: xuhw
 * @date:
 * @since: eHR V6.5
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
@SuppressWarnings("serial")
public class WATaxFunSpecialAdditionaDeductionProcesser extends AbstractWAFormulaParse  {

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		WaLoginContext context = getContext();
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		String[] arguments = getArguments(formula);
		String itemkey = arguments[0];
		fvo.setAliTableName("wa_data");
		StringBuffer sqlBuffer = new StringBuffer();
		String key = TaxUpgradeHelper.convertAdddeductionIntKey2StrKey(Integer.valueOf(itemkey));
		 
		// --查询已经锁定的的对应数据
		sqlBuffer.append(" 	select "+key+" from wa_spe_statis where pk_wa_data = wa_data.pk_wa_data and tax_type = 2	    ");

		fvo.setReplaceStr(coalesce(sqlBuffer.toString()));

		return fvo;
	}

 
}
