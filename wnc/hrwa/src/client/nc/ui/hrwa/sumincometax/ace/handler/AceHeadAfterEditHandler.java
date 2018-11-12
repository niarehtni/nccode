package nc.ui.hrwa.sumincometax.ace.handler;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;
import nc.vo.pub.lang.UFDouble;

/**
 * ���ݱ����ֶα༭���¼�
 * 
 */
public class AceHeadAfterEditHandler implements
		IAppEventHandler<CardHeadTailAfterEditEvent> {

	@Override
	public void handleAppEvent(CardHeadTailAfterEditEvent e) {
		BillCardPanel panel = e.getBillCardPanel();
		String key = e.getKey();
		Object value = e.getValue();
		// �û��޸Ľ�����ʱ�������������¼���
		if (key.equals("taxbase") || key.equals("taxbaseadjust")
				|| key.equals("cacu_value") || key.equals("cacu_valueadjust")) {
			UFDouble taxbase = panel.getHeadItem("taxbase").getValueObject() != null ? new UFDouble(
					String.valueOf(panel.getHeadItem("taxbase")
							.getValueObject())) : UFDouble.ZERO_DBL;
			UFDouble taxbaseadjust = panel.getHeadItem("taxbaseadjust") != null ? new UFDouble(
					String.valueOf(panel.getHeadItem("taxbaseadjust")
							.getValueObject())) : UFDouble.ZERO_DBL;
			UFDouble cacu_value = panel.getHeadItem("cacu_value")
					.getValueObject() != null ? new UFDouble(
					String.valueOf(panel.getHeadItem("cacu_value")
							.getValueObject())) : UFDouble.ZERO_DBL;
			UFDouble cacu_valueadjust = panel.getHeadItem("cacu_valueadjust")
					.getValueObject() != null ? new UFDouble(
					String.valueOf(panel.getHeadItem("cacu_valueadjust")
							.getValueObject())) : UFDouble.ZERO_DBL;
			UFDouble netincome = taxbase.add(taxbaseadjust).sub(cacu_value)
					.sub(cacu_valueadjust);
			panel.setHeadItem("netincome", netincome);// ��������
		}
	}

}
