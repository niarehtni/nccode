package nc.bs.hrsms.ta.sss.away.lsnr;

import nc.bs.hrsms.ta.sss.shopleave.prcss.ShopTaBaseAddProcessor;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.away.AwayhVO;

public class ShopAwayApplyAddProcessor extends ShopTaBaseAddProcessor{

	/**
	 * 新增行前的操作
	 */
	@Override
	public void onBeforeRowAdd(Dataset ds, Row row, String billTypeCode) {
		super.onBeforeRowAdd(ds, row, billTypeCode);
		// 合计时长
		row.setValue(ds.nameToIndex(AwayhVO.SUMHOUR), UFDouble.ZERO_DBL);
		// 预支费用合计
		row.setValue(ds.nameToIndex(AwayhVO.SUMAHEADFEE), UFDouble.ZERO_DBL);
		// 实际支出合计
		row.setValue(ds.nameToIndex(AwayhVO.SUMFACTFEE), UFDouble.ZERO_DBL);
	}
}
