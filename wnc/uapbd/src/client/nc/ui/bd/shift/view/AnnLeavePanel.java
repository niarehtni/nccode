package nc.ui.bd.shift.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Stack;

import javax.swing.JComponent;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang.StringUtils;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * 年度补休规则
 * 
 * @author sunsx
 * @since 2019-12-24
 * 
 */
public class AnnLeavePanel extends UIPanel implements ActionListener, ValueChangedListener {
	private static int BASECOL = 2;
	private static int BASEROW = 1;
	private List<JComponent> bclbCompList;
	private Stack<String> timeStack;
	private UICheckBox usePLMeetNL;
	private UIRefPane nextDayCount;
	private UILabel autoLeaveLable;
	private UIRefPane leaveHours;
	private UILabel leaveHourLable;
	private UFBoolean ishredited;

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(getUsePLMeetNL())) {
			setUsePLMeetNLEnable();
		}
		setIshredited(UFBoolean.TRUE);
	}

	private void setUsePLMeetNLEnable() {
		boolean isUse = getUsePLMeetNL().isSelected();
		getDayCount().setEnabled(isUse);
		getDayCount().getUITextField().setShowMustInputHint(isUse);
		getLeaveHours().setEnabled(isUse);
		getLeaveHours().getUITextField().setShowMustInputHint(isUse);
		repaint();
	}

	public void setEnabled() {
		setUsePLMeetNLEnable();
	}

	public void setIshredited(UFBoolean ishredited) {
		if (timeStack.isEmpty())
			this.ishredited = ishredited;
	}

	public void valueChanged(ValueChangedEvent event) {
		setIshredited(UFBoolean.TRUE);
	}

	public UFBoolean getIshredited() {
		return ishredited;
	}

	public void clearIshredited() {
		ishredited = UFBoolean.FALSE;
	}

	public void initComponents(CellConstraints cc) {
		setLayout(new FormLayout("20dlu,100dlu, 25dlu, 5dlu,40dlu, 25dlu,5dlu, pref", "default,5px, default"));

		int overRow = BASEROW;
		int overCol = BASECOL;

		add(getUsePLMeetNL(), cc.xy(overCol++, overRow));
		add(getDayCount(), cc.xy(overCol++, overRow));
		overCol++;
		add(getAutoLeaveLable(), cc.xy(overCol++, overRow));
		add(getLeaveHours(), cc.xy(overCol++, overRow));
		overCol++;
		add(getLeaveHourLable(), cc.xy(overCol++, overRow));
		// overCol = BASECOL;
		// overRow++;
		// overRow++;
	}

	public UILabel getLeaveHourLable() {
		if (leaveHourLable == null) {
			leaveHourLable = new UILabel();
			leaveHourLable.setText(ResHelper.getString("hrbd", "wncshiftres-000000")); // 小时
		}
		return leaveHourLable;
	}

	public UILabel getAutoLeaveLable() {
		if (autoLeaveLable == null) {
			autoLeaveLable = new UILabel();
			autoLeaveLable.setText(ResHelper.getString("hrbd", "wncshiftres-000001"));// 日自动给假
		}
		return autoLeaveLable;
	}

	public AnnLeavePanel(List<JComponent> bclbCompList, Stack<String> stack) {
		this.bclbCompList = bclbCompList;
		timeStack = stack;
	}

	public UIRefPane getDayCount() {
		if (nextDayCount == null) {
			nextDayCount = new UIRefPane();
			nextDayCount.setTextType("TextInt");
			nextDayCount.setMaxLength(9);
			nextDayCount.setMinValue(0.0D);
			nextDayCount.setName("nextDayCount");
			nextDayCount.setButtonVisible(false);
			bclbCompList.add(nextDayCount);
			nextDayCount.addValueChangedListener(this);
		}
		return nextDayCount;
	}

	public UIRefPane getLeaveHours() {
		if (leaveHours == null) {
			leaveHours = new UIRefPane();
			leaveHours.setTextType("TextDbl");
			leaveHours.setMaxLength(9);
			leaveHours.setMinValue(0.0D);
			leaveHours.setName("leaveHours");
			leaveHours.setButtonVisible(false);
			bclbCompList.add(leaveHours);
			leaveHours.addValueChangedListener(this);
		}
		return leaveHours;
	}

	public UICheckBox getUsePLMeetNL() {
		if (usePLMeetNL == null) {
			usePLMeetNL = new UICheckBox(ResHelper.getString("hrbd", "wncshiftres-000002"));// 公休日遇国定假日时，于隔
			usePLMeetNL.addActionListener(this);
			bclbCompList.add(usePLMeetNL);
		}
		return usePLMeetNL;
	}

	public void getValue(ShiftVO mainVo) {
		mainVo.setIsUsePLMeetNL(UFBoolean.valueOf(getUsePLMeetNL().isSelected()));
		if (!StringUtils.isEmpty(getDayCount().getText())) {
			mainVo.setAutoNextDays(new Integer(getDayCount().getText().trim()));
		}
		if (!StringUtils.isEmpty(getLeaveHours().getText())) {
			mainVo.setAnnLeaveHours(new UFDouble(getLeaveHours().getText().trim()));
		}

		if ((mainVo.getIshredited() == null) || (!mainVo.getIshredited().booleanValue()))
			mainVo.setIshredited(getIshredited());
	}

	public void setValue(ShiftVO mainVo) {
		timeStack.push("PLMEETNL");

		getUsePLMeetNL().setSelected(
				mainVo.getIsUsePLMeetNL() == null ? false : mainVo.getIsUsePLMeetNL().booleanValue());
		getDayCount().setValueObj(mainVo.getAutoNextDays());
		getLeaveHours().setValueObj(mainVo.getAnnLeaveHours());

		ishredited = mainVo.getIshredited();
		timeStack.pop();
	}
}
