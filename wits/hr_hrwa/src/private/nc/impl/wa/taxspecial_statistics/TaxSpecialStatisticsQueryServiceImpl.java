package nc.impl.wa.taxspecial_statistics;

import org.apache.commons.lang.ArrayUtils;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.InSQLCreator;
import nc.itf.hr.wa.IPaydataQueryService;
import nc.itf.hr.wa.ITaxSpecialStatisticsQueryService;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.AggPayDataVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.taxspecial_statistics.TaxSpecialStatisticsVO;

/**
 * 税改专项附加扣除统计
 * 
 * @author xuhw
 * 
 */
public class TaxSpecialStatisticsQueryServiceImpl implements ITaxSpecialStatisticsQueryService {

	/**
	 * 查询专项扣除数据
	 * 
	 * @param loginContext
	 * @param condition
	 * @param orderCondtion
	 * @return
	 * @throws BusinessException
	 */
	public TaxSpecialStatisticsVO[] queryVOsByCond(WaLoginContext loginContext, String condition, String orderCondtion)
			throws BusinessException {

		IPaydataQueryService queryService = NCLocator.getInstance().lookup(IPaydataQueryService.class);
		// 查询薪资数据
		AggPayDataVO aggDataVO = queryService.queryAggPayDataVOByCondition(loginContext, condition, orderCondtion);
		if (ArrayUtils.isEmpty(aggDataVO.getDataPKs())) {
			return new TaxSpecialStatisticsVO[0];
		}
		// 薪资数据
		DataVO[] datas = queryService.queryDataVOByPks(aggDataVO.getDataPKs());

		return new TaxSpecialStatisticsVO[0];
	}

	@Override
	public DataVO[] queryVOByPks(String[] pks) throws BusinessException {
		// TODO Auto-generated method stub
		IPaydataQueryService queryService = NCLocator.getInstance().lookup(IPaydataQueryService.class);

		if (pks == null || pks.length == 0) {
			return new DataVO[0];
		}
		InSQLCreator inSQLCreator = new InSQLCreator();
		String insql = inSQLCreator.getInSQL(pks);
		// 薪资数据
		DataVO[] datas = new TaxSpecialStatisticsDAO().queryByPKSCondition(insql, null);
		if (datas == null || datas.length == 0) {
			throw new BusinessException("没有数据异常!");
		}

		return datas;
	}

	public TaxSpecialStatisticsVO[] queryTaxVOByPKSCondition(String[] pks) throws BusinessException {

		if (pks == null || pks.length == 0) {
			return new TaxSpecialStatisticsVO[0];
		}
		// 薪资数据
		// DataVO[] datas = queryService.queryDataVOByPks(pks);
		TaxSpecialStatisticsVO[] datas = new TaxSpecialStatisticsDAO().queryTaxVOByPKSCondition(pks, null);
		if (datas == null || datas.length == 0) {
			return new TaxSpecialStatisticsVO[0];
		}

		return datas;
	}

}
