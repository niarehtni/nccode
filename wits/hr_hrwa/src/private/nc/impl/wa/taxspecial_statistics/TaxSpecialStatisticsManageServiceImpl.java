package nc.impl.wa.taxspecial_statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.InSQLCreator;
import nc.itf.hr.wa.IPaydataQueryService;
import nc.itf.hr.wa.ITaxSpecialStatisticsManageService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.wa.paydata.AggPayDataVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.taxspecial_statistics.TaxSpecialStatisticsVO;
import nc.vo.wa_tax.TaxUpgradeHelper;
import nc.vo.wa_tax.TaxupgradeDef;

import org.apache.commons.lang.ArrayUtils;

/**
 * 专项附加扣除汇总服务
 * 
 * @author xuhw
 */
public class TaxSpecialStatisticsManageServiceImpl implements ITaxSpecialStatisticsManageService {

	private TaxSpecialStatisticsDAO dao;

	public TaxSpecialStatisticsDAO getDao() {
		if (dao == null) {
			dao = new TaxSpecialStatisticsDAO();
		}
		return dao;
	}

	@Override
	public void lockData(WaLoginContext context, String condition, String orderSQL) throws BusinessException {
		// TODO Auto-generated method stub
		// 只要有一个没被锁定就可以再次使用
		// TODO Auto-generated method stub
		// 1、 根据薪资数据和申请数据生成专项附加费用扣除累计应扣，累计已扣，本期扣除
		// 累计应扣的逻辑
		// 获得员工的发薪期间， 例如 201901， 201902
		// 判断是否已经被审核了，如果有人被审核了则不能
		String pk_condtion = this.getConditionWaDataPks(context, condition);
		this.getDao().lockType(context, pk_condtion);
	}

	@Override
	public void unlockData(WaLoginContext context, String condition, String orderSQL) throws BusinessException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		// 1、 根据薪资数据和申请数据生成专项附加费用扣除累计应扣，累计已扣，本期扣除
		// 累计应扣的逻辑
		// 获得员工的发薪期间， 例如 201901， 201902
		// 判断是否已经被审核了，如果有人被审核了则不能
		String pk_condtion = this.getConditionWaDataPks(context, condition);
		// 只要有一个被锁定就可以再次使用
		this.getDao().unLockType(context, pk_condtion);
	}

	/**
	 * 根据条件生成数据
	 * 
	 * @throws BusinessException
	 */
	@Override
	public void genData(WaLoginContext context, String condition, String orderSQL) throws BusinessException {

		// TODO Auto-generated method stub
		// 1、 根据薪资数据和申请数据生成专项附加费用扣除累计应扣，累计已扣，本期扣除
		// 累计应扣的逻辑
		// 获得员工的发薪期间， 例如 201901， 201902
		// 判断是否已经被审核了，如果有人被审核了则不能
		String pk_condtion = this.getConditionWaDataPks(context, condition);
		// 薪资数据
		DataVO[] datas = getDao().queryByPKSCondition(pk_condtion, null);

		DataVO[] datasTotalable = this.getDao().queryTotalableAmts(context, condition, orderSQL);
		Map<String, DataVO> datasTotalableMap = this.getDao().convertToMap(datasTotalable, PayfileVO.PK_PSNDOC);

		DataVO[] datasTotalableAll = this.getDao().queryTotalableAllAmts(context, condition, orderSQL);
		Map<String, DataVO> datasTotalableAllMap = this.getDao().convertToMap(datasTotalableAll, PayfileVO.PK_PSNDOC);
		DataVO[] datasTotalabled = this.getDao().queryTotaledAllAmts(context, condition, orderSQL);
		Map<String, DataVO> datasTotalabledMap = this.getDao().convertToMap(datasTotalabled, PayfileVO.PK_PSNDOC);

		// 先删除所有
		this.getDao().deleteByCondition(context, pk_condtion);
		List<TaxSpecialStatisticsVO> inserVO = new ArrayList<TaxSpecialStatisticsVO>();
		DataVO dataTotalableVo = null;// 累计应扣
		DataVO dataTotalableAllVO = null;// 累计所有应扣
		DataVO dataTotalabledVo = null;// 累计已扣
		int cnt = 0;
		Integer intAble = 0;// 应扣
		Integer intAbled = 0;// 已扣
		Integer alltotalbleAmt = 0;//全部累计应扣
		Integer alltotalbledAmt = 0;//全部累计已扣
		for (DataVO datavo : datas) {
			String strTaxType = datavo.getAttributeValue("tax_type")+"";
			if (strTaxType.equals(TaxupgradeDef.TAXTYPE_LOCKED+"")) {
				//已经锁定的返回
				continue;
			}
			alltotalbleAmt = 0;//全部累计应扣
			alltotalbledAmt = 0;//全部累计已扣
			TaxSpecialStatisticsVO vo = new TaxSpecialStatisticsVO();
			// dataTotalableVo = datasTotalableMap.get(datavo.getPk_psndoc());
			dataTotalableAllVO = datasTotalableAllMap.get(datavo.getPk_psndoc());
			dataTotalabledVo = datasTotalabledMap.get(datavo.getPk_psndoc());
			vo.setPk_wa_data(datavo.getPk_wa_data());
			vo.setPk_wa_class(datavo.getPk_wa_class());
			vo.setCyear(datavo.getCyear());
			vo.setCperiod(datavo.getCperiod());
			vo.setTaxyear(datavo.getAttributeValue(TaxSpecialStatisticsVO.TAXYEAR) + "");
			vo.setTaxperiod(datavo.getAttributeValue(TaxSpecialStatisticsVO.TAXPERIOD) + "");
			vo.setTax_type(TaxupgradeDef.TAXTYPE_GENED);// 已生成
			vo.setPk_psndoc(datavo.getPk_psndoc());// 人员pK存一下
			// 处理累计应该
			dataTotalableVo = datasTotalableMap.get(datavo.getPk_psndoc()
					+ TaxUpgradeHelper.convertAdddeductionStrKey2IntKey(TaxSpecialStatisticsVO.PARENTFEE));
			alltotalbleAmt = objectToInt(dataTotalableVo, "amount");
			vo.setAttributeValue(TaxSpecialStatisticsVO.PARENTFEETOTAL, alltotalbleAmt);
			dataTotalableVo = datasTotalableMap.get(datavo.getPk_psndoc()
					+ TaxUpgradeHelper.convertAdddeductionStrKey2IntKey(TaxSpecialStatisticsVO.CHILDFEE));
			alltotalbleAmt = alltotalbleAmt + objectToInt(dataTotalableVo, "amount");
			vo.setAttributeValue(TaxSpecialStatisticsVO.CHILDFEETOTAL, objectToInt(dataTotalableVo, "amount"));
			dataTotalableVo = datasTotalableMap.get(datavo.getPk_psndoc()
					+ TaxUpgradeHelper.convertAdddeductionStrKey2IntKey(TaxSpecialStatisticsVO.HOURSEFEE));
			alltotalbleAmt = alltotalbleAmt + objectToInt(dataTotalableVo, "amount");
			vo.setAttributeValue(TaxSpecialStatisticsVO.HOURSEFEETOTAL, objectToInt(dataTotalableVo, "amount"));

			dataTotalableVo = datasTotalableMap.get(datavo.getPk_psndoc()
					+ TaxUpgradeHelper.convertAdddeductionStrKey2IntKey(TaxSpecialStatisticsVO.HOURSEZUFEE));
			alltotalbleAmt = alltotalbleAmt + objectToInt(dataTotalableVo, "amount");
			vo.setAttributeValue(TaxSpecialStatisticsVO.HOURSEZUFEETOTAL, objectToInt(dataTotalableVo, "amount"));

			dataTotalableVo = datasTotalableMap.get(datavo.getPk_psndoc()
					+ TaxUpgradeHelper.convertAdddeductionStrKey2IntKey(TaxSpecialStatisticsVO.EDUCATIONFEE));
			alltotalbleAmt = alltotalbleAmt + objectToInt(dataTotalableVo, "amount");
			vo.setAttributeValue(TaxSpecialStatisticsVO.EDUCATIONFEETOTAL, objectToInt(dataTotalableVo, "amount"));

			dataTotalableVo = datasTotalableMap.get(datavo.getPk_psndoc()
					+ TaxUpgradeHelper.convertAdddeductionStrKey2IntKey(TaxSpecialStatisticsVO.HEALTHYFEE));
			alltotalbleAmt = alltotalbleAmt + objectToInt(dataTotalableVo, "amount");
			vo.setAttributeValue(TaxSpecialStatisticsVO.HEALTHYFEETOTAL, objectToInt(dataTotalableVo, "amount"));

			// 处理累计应该合计
			vo.setAttributeValue(TaxSpecialStatisticsVO.ALLFEETOTAL, alltotalbleAmt);

			// 处理累计已经
			alltotalbledAmt = objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.PARENTFEE);
			vo.setAttributeValue(TaxSpecialStatisticsVO.PARETNFEEDTOTAL,
					objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.PARENTFEE));
			alltotalbledAmt = alltotalbledAmt + objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.CHILDFEE);
			vo.setAttributeValue(TaxSpecialStatisticsVO.CHILDFEEDTOTAL,
					objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.CHILDFEE));
			alltotalbledAmt = alltotalbledAmt + objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.HOURSEFEE);
			vo.setAttributeValue(TaxSpecialStatisticsVO.HOURSEFEEDTOTAL,
					objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.HOURSEFEE));
			alltotalbledAmt = alltotalbledAmt + objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.HOURSEZUFEE);
			vo.setAttributeValue(TaxSpecialStatisticsVO.HOURSEZUFEEDTOTAL,
					objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.HOURSEZUFEE));
			alltotalbledAmt = alltotalbledAmt + objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.EDUCATIONFEE);
			vo.setAttributeValue(TaxSpecialStatisticsVO.EDUCATIONFEEDTOTAL,
					objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.EDUCATIONFEE));
			alltotalbledAmt = alltotalbledAmt + objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.HEALTHYFEE);
			vo.setAttributeValue(TaxSpecialStatisticsVO.HEALTHYFEEDTOTAL,
					objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.HEALTHYFEE));
			vo.setAttributeValue(TaxSpecialStatisticsVO.ALLFEEDTOTAL, alltotalbledAmt);

			// 处理本期 累计应扣 - 累计已扣
			intAble = objectToInt(vo, TaxSpecialStatisticsVO.PARENTFEETOTAL);
			intAbled = objectToInt(vo, TaxSpecialStatisticsVO.PARETNFEEDTOTAL);
			vo.setAttributeValue(TaxSpecialStatisticsVO.PARENTFEE, intAble - intAbled);

			intAble = objectToInt(vo, TaxSpecialStatisticsVO.CHILDFEETOTAL);
			intAbled = objectToInt(vo, TaxSpecialStatisticsVO.CHILDFEEDTOTAL);
			vo.setAttributeValue(TaxSpecialStatisticsVO.CHILDFEE, intAble - intAbled);

			intAble = objectToInt(vo, TaxSpecialStatisticsVO.HOURSEFEETOTAL);
			intAbled = objectToInt(vo, TaxSpecialStatisticsVO.HOURSEFEEDTOTAL);
			vo.setAttributeValue(TaxSpecialStatisticsVO.HOURSEFEE, intAble - intAbled);

			intAble = objectToInt(vo, TaxSpecialStatisticsVO.HOURSEZUFEETOTAL);
			intAbled = objectToInt(vo, TaxSpecialStatisticsVO.HOURSEZUFEEDTOTAL);
			vo.setAttributeValue(TaxSpecialStatisticsVO.HOURSEZUFEE, intAble - intAbled);

			intAble = objectToInt(vo, TaxSpecialStatisticsVO.EDUCATIONFEETOTAL);
			intAbled = objectToInt(vo, TaxSpecialStatisticsVO.EDUCATIONFEEDTOTAL);
			vo.setAttributeValue(TaxSpecialStatisticsVO.EDUCATIONFEE, intAble - intAbled);

			intAble = objectToInt(vo, TaxSpecialStatisticsVO.HEALTHYFEETOTAL);
			intAbled = objectToInt(vo, TaxSpecialStatisticsVO.HEALTHYFEEDTOTAL);
			vo.setAttributeValue(TaxSpecialStatisticsVO.HEALTHYFEE, intAble - intAbled);

			intAble = objectToInt(vo, TaxSpecialStatisticsVO.ALLFEETOTAL);
			intAbled = objectToInt(vo, TaxSpecialStatisticsVO.ALLFEEDTOTAL);
			vo.setAttributeValue(TaxSpecialStatisticsVO.ALLFEE, intAble - intAbled);
			inserVO.add(vo);
			cnt++;
			if (cnt == 500) {
				this.getDao().insertVOs(inserVO);
				inserVO.clear();
			}
		}

		if (inserVO.size() > 0) {
			this.getDao().insertVOs(inserVO);
		}
	}

	private int objectToInt(SuperVO superVO, String key) {
		if (superVO == null) {
			return 0;
		}
		if (key == null) {
			return 0;
		}
		return Integer.valueOf(superVO.getAttributeValue(key) + "");
	}

	/**
	 * 获取业务操作范围
	 * 
	 * @param context
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	private String getConditionWaDataPks(WaLoginContext context, String condition) throws BusinessException {
		IPaydataQueryService queryService = NCLocator.getInstance().lookup(IPaydataQueryService.class);
		AggPayDataVO aggDataVO;
		try {
			aggDataVO = queryService.queryAggPayDataVOByCondition(context, condition, null);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			return null;
		}
		if (ArrayUtils.isEmpty(aggDataVO.getDataPKs())) {
			return null;
		}
		InSQLCreator inSQLCreator = new InSQLCreator();
		String insql = inSQLCreator.getInSQL(aggDataVO.getDataPKs());
		return insql;
	}
}
