package nc.ui.hrwa.projsalary.ace.handler;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.billform.AddEvent;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.pubapp.AppContext;
import nc.vo.wa.pub.WaLoginContext;

@SuppressWarnings("restriction")
public class AceAddHandler implements IAppEventHandler<AddEvent> {

	@Override
	public void handleAppEvent(AddEvent e) {
		WaLoginContext context = (WaLoginContext) e.getContext();
		String pk_group = context.getPk_group();
		String pk_org = context.getPk_org();
		String pk_wa_class = context.getClassPK();
		String cyear = context.getCyear();
		String cperiod = context.getCperiod();
		BillCardPanel panel = e.getBillForm().getBillCardPanel();
		// 设置主组织默认值
		panel.setHeadItem("pk_group", pk_group);
		panel.setHeadItem("pk_org", pk_org);
		// 设置单据状态、单据业务日期默认值
		panel.setHeadItem("fstatusflag", BillStatusEnum.FREE.value());
		panel.setHeadItem("dbilldate", AppContext.getInstance().getBusiDate());

		panel.setHeadItem("pk_wa_calss", pk_wa_class);
		panel.setHeadItem("cperiod", cyear + cperiod);
	}
}
