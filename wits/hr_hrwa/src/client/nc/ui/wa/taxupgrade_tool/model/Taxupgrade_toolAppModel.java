package nc.ui.wa.taxupgrade_tool.model;

import nc.ui.uif2.model.BillManageModel;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.wa.end.WaClassEndVO;

public class Taxupgrade_toolAppModel extends BillManageModel {
	private String pk_wa_class;
	private String cyear;
	private String cperiod;
	
	GeneralVO[] selectClassEndVOs;//ѡ���н�����
	GeneralVO[] unselectClassEndVOs;//��ѡ���н�ʷ���
	
	
}
