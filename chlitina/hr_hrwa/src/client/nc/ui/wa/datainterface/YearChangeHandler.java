package nc.ui.wa.datainterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import nc.ui.pub.beans.UIComboBox;

/**
 * 選擇年，自動確定期間
 * 
 * @author ssx
 * 
 */
public class YearChangeHandler implements ActionListener {
	UIComboBox cboStart;
	UIComboBox cboEnd;

	YearChangeHandler(UIComboBox cboStartPeriod, UIComboBox cboEndPeriod) {
		cboStart = cboStartPeriod;
		cboEnd = cboEndPeriod;
	}

	public void actionPerformed(ActionEvent e) {
		UIComboBox cboYear = (UIComboBox) e.getSource();
		if (e.getActionCommand() == "comboBoxChanged") {
			if (cboYear.getSelectedIndex() >= 0) {
				int minIndex = Integer.MIN_VALUE;
				int minStart = Integer.MAX_VALUE;
				for (int i = 0; i < this.cboStart.getItemCount(); i++) {
					String startPeriod = this.cboStart.getItemAt(i).toString();
					if (startPeriod.subSequence(0, 4).equals(
							cboYear.getSelectdItemValue())) {
						if (Integer.valueOf(startPeriod.substring(4)) < minStart) {
							minStart = Integer
									.valueOf(startPeriod.substring(4));
							minIndex = i;
						}
					}
				}
				cboStart.setSelectedIndex(minIndex);
			}

			if (cboYear.getSelectedIndex() >= 0) {
				int maxIndex = Integer.MIN_VALUE;
				int maxStart = Integer.MIN_VALUE;
				for (int i = 0; i < this.cboEnd.getItemCount(); i++) {
					String startPeriod = this.cboEnd.getItemAt(i).toString();
					if (startPeriod.subSequence(0, 4).equals(
							cboYear.getSelectdItemValue())) {
						if (Integer.valueOf(startPeriod.substring(4)) > maxStart) {
							maxStart = Integer
									.valueOf(startPeriod.substring(4));
							maxIndex = i;
						}
					}
				}
				cboEnd.setSelectedIndex(maxIndex);
			}
		}
	}
}
