package nc.bs.hrsms.ta.sss.overtime.lsnr;

import nc.bs.hrss.pub.HrssConsts;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.data.Dataset;
import nc.vo.pub.AggregatedValueObject;


public class ShopOverTimeSaveAddProcessor extends ShopOverTimeSaveProcessor{

	/**
	 * ��������
	 */
	@Override
	public AggregatedValueObject onAfterVOSave(Dataset ds, Dataset[] dsDetails, AggregatedValueObject aggVO) {
		// ִ������ݲ�ѯ
		CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET, "closewindow"));
		return null;
	}
}
