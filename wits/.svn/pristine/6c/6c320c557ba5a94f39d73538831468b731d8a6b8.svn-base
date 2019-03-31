package nc.impl.wa.taxupgrade_tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.hr.utils.PubEnv;
import nc.itf.hr.wa.ITaxupgrade_toolQueryService;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa_tax.TaxupgradeDef;

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
public class Taxupgrade_toolQueryServiceImpl implements ITaxupgrade_toolQueryService {

	@Override
	public GeneralVO[] queryTargetClassInfo(String pk_group) throws BusinessException {
		Taxupgrade_toolDAO dao = new Taxupgrade_toolDAO();
		GeneralVO[] vos = dao.queryTargetClassInfo(pk_group, null, null, null);
		List<GeneralVO> genvos = new ArrayList<GeneralVO>();
		if (vos != null && vos.length > 0) {
			for (GeneralVO vo : vos) {
				Object obj = vo.getAttributeValue("done");
				if (obj == null) {
					genvos.add(vo);
				}
			}
		}
		return genvos.toArray(new GeneralVO[0]);
	}

	@Override
	public WaItemVO[] queryTaxItems(String pk_group) throws BusinessException {
		// TODO Auto-generated method stub

		WaItemVO[] vos = this.queryTaxUpgradeItems(pk_group);
		if (vos == null || vos.length == 0) {
			vos = getInitTaxItems(pk_group);
		} else {

			Map<String, WaItemVO> map = getInitTaxItemMap(pk_group);
			for (WaItemVO itemvo : vos) {
				if (TaxupgradeDef.TAXITEM_CODE_TAXABLE_INCOME_YZ1.equals(itemvo.getCode())) {
					itemvo.setName6(map.get(TaxupgradeDef.TAXITEM_CODE_TAXABLE_INCOME_YZ1).getName());
					Integer ifromFlag = itemvo.getIfromflag();
					itemvo.setTaxflag(ifromFlag==2?UFBoolean.TRUE:UFBoolean.FALSE);
				} else if (TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXABLE_INCOME_YZ2.equals(itemvo.getCode())) {
					Integer ifromFlag = itemvo.getIfromflag();
					itemvo.setTaxflag(ifromFlag==2?UFBoolean.TRUE:UFBoolean.FALSE);
					itemvo.setName6(map.get(TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXABLE_INCOME_YZ2).getName());
				} else if (TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXABLEAMT_YZ3.equals(itemvo.getCode())) {
					Integer ifromFlag = itemvo.getIfromflag();
					itemvo.setTaxflag(ifromFlag==2?UFBoolean.TRUE:UFBoolean.FALSE);
					itemvo.setName6(map.get(TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXABLEAMT_YZ3).getName());
				} else if (TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXEDAMT_YZ4.equals(itemvo.getCode())) {
					Integer ifromFlag = itemvo.getIfromflag();
					itemvo.setTaxflag(ifromFlag==2?UFBoolean.TRUE:UFBoolean.FALSE);
					itemvo.setName6(map.get(TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXEDAMT_YZ4).getName());
				} else if (TaxupgradeDef.TAXITEM_CODE_TAXABLEAMT_YZ5.equals(itemvo.getCode())) {
					// itemvo.setName6(map.get(TaxupgradeDef.TAXITEM_CODE_TAXABLEAMT_YZ5).getName());
				} else if (TaxupgradeDef.TAXITEM_CODE_TAX_RATE_YZ6.equals(itemvo.getCode())) {
					Integer ifromFlag = itemvo.getIfromflag();
					itemvo.setTaxflag(ifromFlag==2?UFBoolean.TRUE:UFBoolean.FALSE);
					itemvo.setName6(map.get(TaxupgradeDef.TAXITEM_CODE_TAX_RATE_YZ6).getName());
				} else if (TaxupgradeDef.TAXITEM_CODE_TAX_QUICK_DEDUCTION_YZ7.equals(itemvo.getCode())) {
					Integer ifromFlag = itemvo.getIfromflag();
					itemvo.setTaxflag(ifromFlag==2?UFBoolean.TRUE:UFBoolean.FALSE);
					itemvo.setName6(map.get(TaxupgradeDef.TAXITEM_CODE_TAX_QUICK_DEDUCTION_YZ7).getName());
				} else if (TaxupgradeDef.TAXITEM_CODE_TAX_BASIC_DEDUCTION_YZ8.equals(itemvo.getCode())) {
					Integer ifromFlag = itemvo.getIfromflag();
					itemvo.setTaxflag(ifromFlag==2?UFBoolean.TRUE:UFBoolean.FALSE);
					itemvo.setName6(map.get(TaxupgradeDef.TAXITEM_CODE_TAX_BASIC_DEDUCTION_YZ8).getName());
				} else if (TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ9.equals(itemvo.getCode())) {
					Integer ifromFlag = itemvo.getIfromflag();
					itemvo.setTaxflag(ifromFlag==2?UFBoolean.TRUE:UFBoolean.FALSE);
					itemvo.setName6(map.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ9).getName());
				} else if (TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ10.equals(itemvo.getCode())) {
					Integer ifromFlag = itemvo.getIfromflag();
					itemvo.setTaxflag(ifromFlag==2?UFBoolean.TRUE:UFBoolean.FALSE);
					itemvo.setName6(map.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ10).getName());
				} else if (TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ11.equals(itemvo.getCode())) {
					Integer ifromFlag = itemvo.getIfromflag();
					itemvo.setTaxflag(ifromFlag==2?UFBoolean.TRUE:UFBoolean.FALSE);
					itemvo.setName6(map.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ11).getName());
				} else if (TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ12.equals(itemvo.getCode())) {
					Integer ifromFlag = itemvo.getIfromflag();
					itemvo.setTaxflag(ifromFlag==2?UFBoolean.TRUE:UFBoolean.FALSE);
					itemvo.setName6(map.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ12).getName());
				} else if (TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ13.equals(itemvo.getCode())) {
					Integer ifromFlag = itemvo.getIfromflag();
					itemvo.setTaxflag(ifromFlag==2?UFBoolean.TRUE:UFBoolean.FALSE);
					itemvo.setName6(map.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ13).getName());
				} else if (TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ14.equals(itemvo.getCode())) {
					Integer ifromFlag = itemvo.getIfromflag();
					itemvo.setTaxflag(ifromFlag==2?UFBoolean.TRUE:UFBoolean.FALSE);
					itemvo.setName6(map.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ14).getName());
				} else if (TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ15.equals(itemvo.getCode())) {
					Integer ifromFlag = itemvo.getIfromflag();
					itemvo.setTaxflag(ifromFlag==2?UFBoolean.TRUE:UFBoolean.FALSE);
					itemvo.setName6(map.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ15).getName());
				}
				 else if (TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ16.equals(itemvo.getCode())) {
						Integer ifromFlag = itemvo.getIfromflag();
						itemvo.setTaxflag(ifromFlag==2?UFBoolean.TRUE:UFBoolean.FALSE);
						itemvo.setName6(map.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ16).getName());
					}
				 else if (TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ17.equals(itemvo.getCode())) {
						Integer ifromFlag = itemvo.getIfromflag();
						itemvo.setTaxflag(ifromFlag==2?UFBoolean.TRUE:UFBoolean.FALSE);
						itemvo.setName6(map.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ17).getName());
					}
			}
		}

		return vos;
	}

	@Override
	public WaItemVO[] queryTaxUpgradeItems(String pk_group) throws BusinessException {
		// TODO Auto-generated method stub
		String sbsql = " select * from wa_item where pk_group = '" + pk_group + "' and code in ('"
				+ TaxupgradeDef.TAXITEM_CODE_TAXABLE_INCOME_YZ1 + "'" + ", '"
				+ TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXABLE_INCOME_YZ2 + "'" + ", '"
				+ TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXABLEAMT_YZ3 + "'" + ", '"
				+ TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXEDAMT_YZ4 + "'" + ", '" + TaxupgradeDef.TAXITEM_CODE_TAX_RATE_YZ6
				+ "'" + ", '" + TaxupgradeDef.TAXITEM_CODE_TAX_QUICK_DEDUCTION_YZ7 + "'" + ", '"
				+ TaxupgradeDef.TAXITEM_CODE_TAX_BASIC_DEDUCTION_YZ8 + "'" + ", '"
				+ TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ9 + "'" + ", '"
				+ TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ11 + "'" + ", '"
				+ TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ12 + "'" + ", '"
				+ TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ13 + "'" + ", '"
				+ TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ14 + "'" + ", '"
				+ TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ15 + "'" + ", '"
				+ TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ16 + "'" + ", '"
				+ TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ17 + "'" + ", '"
				+ TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ10 + "'" + ", '"
				+ TaxupgradeDef.TAXITEM_CODE_TAXABLEAMT_YZ5 + "') ";

		WaItemVO[] vos = new Taxupgrade_toolDAO().executeQueryVOs(sbsql, WaItemVO.class);
		return vos;
	}

	@Override
	public WaItemVO[] getInitTaxItems(String pk_group) throws BusinessException {
		// TODO Auto-generated method stub
		List<WaItemVO> listItem = new ArrayList<WaItemVO>();

		/** 新税改_附加专项扣除合计本期 */
		WaItemVO waitemVO = this.makeWaItemVO(null, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ9,
				TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ9, 6, 2);
		waitemVO.setName6(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ9);
		listItem.add(waitemVO);

		/** 新税改_赡养老人 */
		waitemVO = this.makeWaItemVO(null, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ10,
				TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ10, 2, 2);
		waitemVO.setName6(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ10);
		waitemVO.setTaxflag(UFBoolean.TRUE);
		listItem.add(waitemVO);
		
		/** 新税改_子女教育 */
		waitemVO = this.makeWaItemVO(null, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ11,
				TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ11, 2, 2);
		waitemVO.setName6(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ11);
		waitemVO.setTaxflag(UFBoolean.TRUE);
		listItem.add(waitemVO);
		
		/** 新税改_大病医疗 */
		waitemVO = this.makeWaItemVO(null, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ12,
				TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ12, 2, 2);
		waitemVO.setName6(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ12);
		waitemVO.setTaxflag(UFBoolean.TRUE);
		listItem.add(waitemVO);
		
		/** 新税改_继续教育 */
		waitemVO = this.makeWaItemVO(null, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ13,
				TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ13, 2, 2);
		waitemVO.setName6(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ13);
		waitemVO.setTaxflag(UFBoolean.TRUE);
		listItem.add(waitemVO);
		
		/** 新税改_租房补贴 */
		waitemVO = this.makeWaItemVO(null, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ14,
				TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ14, 2, 2);
		waitemVO.setName6(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ14);
		waitemVO.setTaxflag(UFBoolean.TRUE);
		listItem.add(waitemVO);
		
		/** 新税改_住房补贴 */
		waitemVO = this.makeWaItemVO(null, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ15,
				TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ15, 2, 2);
		waitemVO.setName6(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ15);
		waitemVO.setTaxflag(UFBoolean.TRUE);
		listItem.add(waitemVO);

		/** 新税改_累计专项应扣*/
		waitemVO = this.makeWaItemVO(null, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ16,
				TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ16, 6, 2);
		waitemVO.setName6(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ16);
		listItem.add(waitemVO);
		
		/** 新税改_累计专项已扣 */
		waitemVO = this.makeWaItemVO(null, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ17,
				TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ17, 2, 2);
		waitemVO.setName6(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ17);
		listItem.add(waitemVO);
		
		// 应纳税所得额
		waitemVO = this.makeWaItemVO(null, TaxupgradeDef.TAXITEM_CODE_TAXABLE_INCOME_YZ1,
				TaxupgradeDef.TAXITEM_NAME_TAXABLE_INCOME_YZ_ZW1, 6, 2);
		waitemVO.setName6(TaxupgradeDef.TAXITEM_NAME_TAXABLE_INCOME_YZ_ZW1);
		listItem.add(waitemVO);

		// 累计应纳税所得额预制
		waitemVO = this.makeWaItemVO(null, TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXABLE_INCOME_YZ2,
				TaxupgradeDef.TAXITEM_NAME_TOTAL_TAXABLE_INCOME_YZ_ZW2, 6, 2);
		waitemVO.setName6(TaxupgradeDef.TAXITEM_NAME_TOTAL_TAXABLE_INCOME_YZ_ZW2);
		listItem.add(waitemVO);

		// 累计应纳税
		waitemVO = this.makeWaItemVO(null, TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXABLEAMT_YZ3,
				TaxupgradeDef.TAXITEM_NAME_TOTAL_TAXABLEAMT_YZ_ZW3, 6, 2);
		waitemVO.setName6(TaxupgradeDef.TAXITEM_NAME_TOTAL_TAXABLEAMT_YZ_ZW3);
		listItem.add(waitemVO);

		// 累计已扣税
		waitemVO = this.makeWaItemVO(null, TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXEDAMT_YZ4,
				TaxupgradeDef.TAXITEM_NAME_TOTAL_TAXEDAMT_YZ_ZW4, 6, 2);
		waitemVO.setName6(TaxupgradeDef.TAXITEM_NAME_TOTAL_TAXEDAMT_YZ_ZW4);
		listItem.add(waitemVO);

		// 适用税率
		waitemVO = this.makeWaItemVO(null, TaxupgradeDef.TAXITEM_CODE_TAX_RATE_YZ6,
				TaxupgradeDef.TAXITEM_NAME_TAX_RATE_YZ6, 6, 2);
		waitemVO.setName6(TaxupgradeDef.TAXITEM_NAME_TAX_RATE_YZ6);
		listItem.add(waitemVO);

		// /**新税改_速算扣除数*/
		waitemVO = this.makeWaItemVO(null, TaxupgradeDef.TAXITEM_CODE_TAX_QUICK_DEDUCTION_YZ7,
				TaxupgradeDef.TAXITEM_NAME_TAX_QUICK_DEDUCTION_YZ7, 6, 2);
		waitemVO.setName6(TaxupgradeDef.TAXITEM_NAME_TAX_QUICK_DEDUCTION_YZ7);
		listItem.add(waitemVO);

		// /**新税改_基本扣除数*/
		waitemVO = this.makeWaItemVO(null, TaxupgradeDef.TAXITEM_CODE_TAX_BASIC_DEDUCTION_YZ8,
				TaxupgradeDef.TAXITEM_NAME_TAX_BASIC_DEDUCTION_YZ8, 6, 2);
		waitemVO.setName6(TaxupgradeDef.TAXITEM_NAME_TAX_BASIC_DEDUCTION_YZ8);
		listItem.add(waitemVO);

		return listItem.toArray(new WaItemVO[0]);
	}

	public Map<String, WaItemVO> getInitTaxItemMap(String pk_group) throws BusinessException {
		Map<String, WaItemVO> map = new HashMap<String, WaItemVO>();
		WaItemVO[] waitemvos = getInitTaxItems(pk_group);
		for (WaItemVO itemvo : waitemvos) {
			map.put(itemvo.getCode(), itemvo);
		}

		return map;
	}

	/**
	 * 查询已经升级完的方案
	 * 
	 * @param pk_group
	 * @return
	 * @throws BusinessException
	 */
	public GeneralVO[] queryHasCopyClassInfo(String pk_group) throws BusinessException {
		Taxupgrade_toolDAO dao = new Taxupgrade_toolDAO();
		GeneralVO[] vos = dao.queryTargetClassInfo(pk_group, null, null, null);
		List<GeneralVO> genvos = new ArrayList<GeneralVO>();
		if (vos != null && vos.length > 0) {
			for (GeneralVO vo : vos) {
				Object obj = vo.getAttributeValue("done");
				if (obj != null && (obj + "").equals("1")) {
					genvos.add(vo);
				}
			}
		}
		return genvos.toArray(new GeneralVO[0]);
	}

	/**
	 * 构造预制项目
	 * 
	 * @param waitemVO
	 * @param itemvoMap
	 * @param code
	 * @param fromFlag
	 * @param formula
	 * @param formulastr
	 */
	public WaItemVO makeWaItemVO(Map<String, WaItemVO> itemvoMap, String code, String name, int fromFlag, int property) {
		WaItemVO waitemVO = new WaItemVO();
		waitemVO.setCategory_id(null);
		waitemVO.setClearflag(UFBoolean.FALSE);
		waitemVO.setCode(code);
		if (itemvoMap != null && itemvoMap.size() > 0) {
			name = itemvoMap.get(code).getName();
		}
		waitemVO.setName(name);
		waitemVO.setDefaultflag(UFBoolean.TRUE);
		waitemVO.setIdisplayseq(0);
		waitemVO.setIflddecimal(2);
		waitemVO.setIfldwidth(12);
		waitemVO.setIfromflag(fromFlag);
		waitemVO.setIitemtype(0);
		waitemVO.setIntotalitem(UFBoolean.FALSE);
		waitemVO.setIproperty(property);// 其他项目
		waitemVO.setMid(UFBoolean.TRUE);
		waitemVO.setPk_group(PubEnv.getPk_group());
		waitemVO.setPk_org(PubEnv.getPk_group());
		waitemVO.setPsnceilflag(UFBoolean.FALSE);
		waitemVO.setPsnfloorflag(UFBoolean.FALSE);
		waitemVO.setSumceilflag(UFBoolean.FALSE);
		waitemVO.setSumfloorflag(UFBoolean.FALSE);
		waitemVO.setTaxflag(UFBoolean.FALSE);
		waitemVO.setCreator(PubEnv.getPk_user());

		return waitemVO;
	}
}
