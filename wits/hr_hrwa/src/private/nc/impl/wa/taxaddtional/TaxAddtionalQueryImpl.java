package nc.impl.wa.taxaddtional;

import java.util.ArrayList;
import java.util.List;

import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.itf.hr.wa.ITaxaddtionalQueryService;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;
import nc.vo.wa_tax.WASpecialAdditionaDeductionVO;

/**
 * TaxAddtionalImpl
 * 
 * @author: xuhw
 * @since: eHR V6.5
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class TaxAddtionalQueryImpl implements ITaxaddtionalQueryService {
	private final String DOC_NAME = "TaxAddtionalImpl";

	private SimpleDocServiceTemplate serviceTemplate;

	private SimpleDocServiceTemplate getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new SimpleDocServiceTemplate(DOC_NAME);
		}
		return serviceTemplate;
	}

	private TaxAddtionalDAO taxDAO;

	private TaxAddtionalDAO getTaxDAO() {
		if (taxDAO == null) {
			taxDAO = new TaxAddtionalDAO();
		}
		return taxDAO;
	}

	@Override
	public WASpecialAdditionaDeductionVO[] queryDataByCondition(LoginContext context, String sqlWhere, String orderby)
			throws BusinessException {
		// TODO Auto-generated method stub
		return getTaxDAO().queryDataByCondition(context, sqlWhere, null);
	}

	public String[] queryDataPksByCondition(LoginContext context, String sqlWhere, String orderby)
			throws BusinessException {
		// TODO Auto-generated method stub
		WASpecialAdditionaDeductionVO[] vos = getTaxDAO().queryDataByCondition(context, sqlWhere, null);
		List<String> list = new ArrayList();
		if (vos == null || vos.length == 0) {
			return new String[0];
		}
		for (WASpecialAdditionaDeductionVO vo : vos) {
			list.add(vo.getPk_deduction());
		}
		
		return list.toArray(new String[0]);
	}

	
	@Override
	public String[] queryVOsByCondition(LoginContext context, String sqlWhere, String orderby) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public WASpecialAdditionaDeductionVO[] exportData(LoginContext context, String sqlWhere, String orderby, String taxyear) throws BusinessException {
		// TODO Auto-generated method stub
		return this.getTaxDAO().queryExportDataByCondition(context, sqlWhere, orderby, taxyear);
	}

 
	@Override
	public WASpecialAdditionaDeductionVO[] queryDataByPKs(String[] pks) throws BusinessException {
		// TODO Auto-generated method stub
		return this.getTaxDAO().queryDataByPks(pks);
	}
}