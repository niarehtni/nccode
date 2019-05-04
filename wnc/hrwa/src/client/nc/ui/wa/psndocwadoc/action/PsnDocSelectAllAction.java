package nc.ui.wa.psndocwadoc.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import nc.ui.ml.NCLangRes;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.uif2.NCAction;

/**
 * 定调资信息维护人员信息表格的表头右键弹出增加一个全选的按钮
 * 
 * @author wangywt
 * @since 2019-04-28
 */
public class PsnDocSelectAllAction extends NCAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String name = NCLangRes.getInstance().getStrByID("_bill", "UPP_Bill-000569");
	private BillScrollPane bsp = null;
	

	public PsnDocSelectAllAction(BillScrollPane bsp)
	{
	   this.bsp = bsp;
	   putValue("Name", this.name);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < this.bsp.getTableModel().getRowCount(); i++) {
			this.bsp.getTableModel().setValueAt(true, i, 0); 
		}
	}
	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
