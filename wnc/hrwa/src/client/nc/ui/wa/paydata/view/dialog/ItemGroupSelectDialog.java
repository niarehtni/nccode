package nc.ui.wa.paydata.view.dialog;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JPanel;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.vo.pub.BusinessException;
import nc.vo.wa.itemgroup.ItemGroupVO;

public class ItemGroupSelectDialog extends UIDialog implements ActionListener {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = -3212170858580618278L;
	private UILabel lblItemGroup = null;
	private UIComboBox cboItemGroup = null;
	private UIButton cmdOK;
	private UIButton cmdCancel;
	private ItemGroupVO selectedItemGroup = null;

	public ItemGroupSelectDialog(Container content, String title, boolean reset) throws BusinessException {
		super(content, reset);
		setTitle(title);

		JPanel panelMain = new JPanel();
		panelMain.add(getLblItemGroup());
		panelMain.add(getCboItemGroup());
		getContentPane().add("Center", panelMain);

		JPanel panelButton = new JPanel();
		panelButton.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelButton.add(getCmdOK());
		panelButton.add(getCmdCancel());
		getContentPane().add("South", panelButton);

		pack();
		setSize(300, 70);
		this.setModal(true);
		init();
	}

	@SuppressWarnings("unchecked")
	private void init() throws BusinessException {
		if (this.getCboItemGroup().getItemCount() > 0) {
			this.getCboItemGroup().removeAllItems();
		}

		IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		Collection<ItemGroupVO> itemGroups = query.retrieveByClause(ItemGroupVO.class,
				"groupcode like 'PAYSLIP_%' and isenabled = 'Y'");

		if (itemGroups != null && itemGroups.size() > 0) {
			for (ItemGroupVO vo : itemGroups) {
				DefaultConstEnum item = new DefaultConstEnum(vo, vo.getGroupname());
				this.getCboItemGroup().addItem(item);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if ((source == this.getCmdOK())) {
			this.setSelectedItemGroup((ItemGroupVO) this.getCboItemGroup().getSelectdItemValue());
			this.closeOK();
		} else if (source == this.getCmdCancel()) {
			this.closeCancel();
			this.dispose();
		}
	}

	public UILabel getLblItemGroup() {
		if (lblItemGroup == null) {
			lblItemGroup = new UILabel();
			lblItemGroup.setText("薪資項目分組");
		}

		return lblItemGroup;
	}

	public UIComboBox getCboItemGroup() {
		if (cboItemGroup == null) {
			cboItemGroup = new UIComboBox();
			cboItemGroup.setVisible(true);
			cboItemGroup.setPreferredSize(new Dimension(200, 20));
		}
		return cboItemGroup;
	}

	public UIButton getCmdOK() {
		if (cmdOK == null) {
			cmdOK = new UIButton();
			cmdOK.setText("發送");
			cmdOK.addActionListener(this);
		}

		return cmdOK;
	}

	public UIButton getCmdCancel() {
		if (cmdCancel == null) {
			cmdCancel = new UIButton();
			cmdCancel.setText("取消");
			cmdCancel.addActionListener(this);
		}
		return cmdCancel;
	}

	public ItemGroupVO getSelectedItemGroup() {
		return selectedItemGroup;
	}

	public void setSelectedItemGroup(ItemGroupVO selectedItemGroup) {
		this.selectedItemGroup = selectedItemGroup;
	}

}
