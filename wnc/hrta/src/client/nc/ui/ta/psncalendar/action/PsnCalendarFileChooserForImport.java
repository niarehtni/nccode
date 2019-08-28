package nc.ui.ta.psncalendar.action;

import java.awt.Dimension;

import javax.swing.JPanel;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.beans.UILabel;

@SuppressWarnings("serial")
public class PsnCalendarFileChooserForImport extends UIFileChooser {

	private UICheckBox clearNull = null;

	public PsnCalendarFileChooserForImport(){
		super();
		JPanel jPanel = (JPanel) ((JPanel) super.getComponent(3)).getComponent(2);

		UILabel aUILabel = new UILabel(ResHelper.getString("6017psncalendar","06017psncalendar0050")
/*@res "     文件中为空的工作日历是否清空"*/);

		clearNull = new UICheckBox();
		clearNull.setName("clearNull");
		clearNull.setPreferredSize(new Dimension(20, 20));
		clearNull.setSelected(false);
		clearNull.setSize(20, 20);

		jPanel.add(aUILabel);
		jPanel.add(clearNull);
	}

	public boolean isClearNull(){
		return clearNull == null ? false : clearNull.isSelected();
	}
}