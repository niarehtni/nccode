package nc.impl.wa.taxaddtional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.itf.hr.wa.ITaxaddtionalManageService;
import nc.jdbc.framework.SQLParameter;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.uif2.LoginContext;
import nc.vo.wa_tax.TaxUpgradeHelper;
import nc.vo.wa_tax.WASpecialAdditionaDeductionVO;

import org.apache.commons.lang.StringUtils;

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
public class TaxAddtionalImpl implements ITaxaddtionalManageService {
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
	public void exportData(LoginContext context, String sqlWhere, String orderby) {
		// TODO Auto-generated method stub

	}

	@Override
	public WASpecialAdditionaDeductionVO[] importData(LoginContext context, WASpecialAdditionaDeductionVO[] generalVOs)
			throws BusinessException {
		// TODO Auto-generated method stub
		// 验证数据合法性
		// 1、人员是否合法(人员编码 + 人员姓名 + 证件号码）和系统对应上，对应不上给出哪一个对应不上
		// 2、相同人类型时间不能有重叠 时间校验
		// 3、数据校验 开始年 开始月 结束年 结束月 金额 类型校验，类型必须
		InSQLCreator ins = new InSQLCreator();
		String sql = ins.getInSQL(generalVOs, WASpecialAdditionaDeductionVO.IDCARD);
		WASpecialAdditionaDeductionVO[] datavos = this.getTaxDAO().queryPsndocExt(context, sql);
		Map<String, WASpecialAdditionaDeductionVO> idmaps = this.getTaxDAO().convertToMap(datavos,
				WASpecialAdditionaDeductionVO.IDCARD);
		// Map<String, WASpecialAdditionaDeductionVO> codemaps =
		// this.getTaxDAO().convertToMap(datavos, "psncode");
		// Map<String, WASpecialAdditionaDeductionVO> namemaps =
		// this.getTaxDAO().convertToMap(datavos, "psnname");
		StringBuffer erroinfo = new StringBuffer();
		List<WASpecialAdditionaDeductionVO> inserVOList = new ArrayList<WASpecialAdditionaDeductionVO>();
//		List<WASpecialAdditionaDeductionVO> inserAllVOList = new ArrayList<WASpecialAdditionaDeductionVO>();
		List<WASpecialAdditionaDeductionVO> errorVOList = new ArrayList<WASpecialAdditionaDeductionVO>();
		for (WASpecialAdditionaDeductionVO vo : generalVOs) {
			erroinfo = new StringBuffer();
			String idcard = vo.getIdcard();
			String name = vo.getPsnname();
			String code = vo.getPsncode();
			WASpecialAdditionaDeductionVO deductionvo = idmaps.get(idcard);
			// idcard不正确
			if (deductionvo == null) {
				erroinfo.append("证件号码与系统人员不匹配!");
				vo.setReason(erroinfo.toString());
				errorVOList.add(vo);
				continue;
			}
			if (!name.equals(deductionvo.getPsnname())) {
				erroinfo.append("人员姓名与系统人员不匹配!");
				vo.setReason(erroinfo.toString());
				errorVOList.add(vo);
				continue;
			}
			if (!code.equals(deductionvo.getPsncode())) {
				erroinfo.append("员工编码与系统人员不匹配!");
				vo.setReason(erroinfo.toString());
				errorVOList.add(vo);
				continue;
			}

			String begin_period = vo.getBegin_period();
			String begin_year = vo.getBegin_year();
			String end_year = StringUtils.isEmpty(vo.getEnd_year()) ? "6666" : vo.getEnd_year();
			String end_period = StringUtils.isEmpty(vo.getEnd_period()) ? "12" : vo.getEnd_period();

			if (Integer.parseInt(end_year + end_period) <= Integer.parseInt(begin_year + begin_period)) {
				erroinfo.append("结束时间不能大于开始时间!");
				vo.setAttributeValue("reason", erroinfo.toString());
				errorVOList.add(vo);
				continue;
			}
			vo.setPk_psndoc(deductionvo.getPk_psndoc());
			vo.setType(TaxUpgradeHelper.convertAdddeductionIntName2Int(vo.getTypename())+"");
			vo.setAmount(Integer.parseInt(vo.getAmountimp()));
			vo.setApply_date(new UFLiteralDate());
			vo.setCreator(PubEnv.getPk_user());
			vo.setCreationtime(new UFDateTime());
			vo.setSource("import");
			vo.setDef1(context.getPk_group());
			vo.setDef2(context.getPk_org());
			inserVOList.add(vo);
//			inserAllVOList.add(vo);
			if (inserVOList.size() >= 500) {
				deleteByCondition(inserVOList);
				this.getTaxDAO().insertVOs(inserVOList);
				inserVOList.clear();
			}
		}

		if (inserVOList.size() > 0) {
			deleteByCondition(inserVOList);
			this.getTaxDAO().insertVOs(inserVOList);
		}
		WASpecialAdditionaDeductionVO[] errorVos = new WASpecialAdditionaDeductionVO[0];
		if (errorVOList.size() > 0) {
			errorVos = errorVOList.toArray(new WASpecialAdditionaDeductionVO[0]);
		}
		return errorVos;
	}

	/**
	 * 一次处理一个纳税
	 * 
	 * @throws BusinessException
	 */
	private void volidatePsnInfo(LoginContext context, WASpecialAdditionaDeductionVO[] generalVOs)
			throws BusinessException {

	}

	// 删除相同人，相同类型，相同纳税开始年删除
	public void deleteByCondition(List<WASpecialAdditionaDeductionVO> list) throws BusinessException {
		StringBuffer sql = new StringBuffer();
		int cnt = 0;
		SQLParameter sqlp = new SQLParameter();
		for (WASpecialAdditionaDeductionVO vo : list) {
			if (sql.length() == 0) {
				sql.append(" (pk_psndoc = ? AND TYPE = ? AND begin_year = ?  ) ");
			} else {
				sql.append(" or (pk_psndoc = ? AND TYPE = ? AND begin_year = ? ) ");
			}
			sqlp.addParam(vo.getPk_psndoc());
			sqlp.addParam(vo.getType());
			sqlp.addParam(vo.getBegin_year());
			cnt++;
			if (cnt == 100) {
				StringBuffer delSql = new StringBuffer();
				delSql.append(" delete from wa_deduction where def1 = '"+PubEnv.getPk_group()+"' and ");
				delSql.append(sql.toString());
				this.getTaxDAO().getBaseDao().executeUpdate(delSql.toString(), sqlp);
				sqlp.clearParams();
				sql = new StringBuffer();
			}
		}
		
		//大于零的时候需要在执行
		if (sql.length() > 0) {
			StringBuffer delSql = new StringBuffer();
			delSql.append(" delete from wa_deduction where def1 = '"+PubEnv.getPk_group()+"' and ");
			delSql.append(sql.toString());
			this.getTaxDAO().getBaseDao().executeUpdate(delSql.toString(), sqlp);
			sqlp.clearParams();
			sql = new StringBuffer();
		}
	}
}