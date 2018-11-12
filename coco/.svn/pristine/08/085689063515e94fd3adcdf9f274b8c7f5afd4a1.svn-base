package nc.bs.hrsms.ta.sss.shopleave.prcss;

import nc.bs.hrss.pub.HrssConsts;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.data.Dataset;
import nc.vo.pub.AggregatedValueObject;

public class ShopLeaveSaveAddProcessor extends ShopLeaveSaveProcessor {

	/**
	 * 保存后操作
	 */
	@Override
	public AggregatedValueObject onAfterVOSave(Dataset ds, Dataset[] dsDetails, AggregatedValueObject aggVO) {
		// 执行左侧快捷查询
		CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET, "closewindow"));
		return null;
	}

}
