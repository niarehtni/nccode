package nc.bs.hrsms.ta.sss.away.lsnr;

import nc.bs.hrsms.ta.sss.away.ctrl.ShopAwayApplyCardView;
import nc.itf.hrss.pub.cmd.prcss.IEditProcessor;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;

public class ShopAwayApplyEditProcessor implements IEditProcessor {

	@Override
	public void onBeforeEdit(Dataset ds) {
	}

	/**
	 * 设置考勤单位相关内容的显示
	 */
	@Override
	public void onAfterEdit(Dataset ds) {
		Row selRow = ds.getSelectedRow();
		ShopAwayApplyCardView.setTimeUnitText(ds, selRow);
	}

}
