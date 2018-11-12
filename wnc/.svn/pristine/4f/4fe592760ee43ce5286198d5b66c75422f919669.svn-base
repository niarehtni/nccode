package nc.ui.bd.shift.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.border.TitledBorder;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.ta.algorithm.IRelativeTime;
import nc.itf.ta.algorithm.IRelativeTimeScope;
import nc.itf.ta.algorithm.RelativeTimeScopeUtils;
import nc.itf.ta.algorithm.RelativeTimeUtils;
import nc.itf.ta.algorithm.impl.DefaultRelativeTime;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.textfield.formatter.ParseException;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillListView;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.CapRTVO;
import nc.vo.bd.shift.RTVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.bd.shift.SingleCardTypeEnum;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFTime;
import nc.vo.trade.voutils.VOUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class WrTimeRulePanel extends UIPanel implements ActionListener,
		BillEditListener, ItemListener, BillEditListener2, ValueChangedListener {
	private AbstractAppModel model;
	private List<JComponent> shiftCompList;
	private List<JComponent> mustInputList;
	private List<JComponent> mayInputList;
	private BillListView listView;
	private UIPanel workPanel;
	private UILabel timebegindayLabel;
	private UIComboBox timebeginday;
	private UIRefPane timebegintime;
	private UILabel timeenddayLabel;
	private UIComboBox timeendday;
	private UIRefPane timeendtime;
	private UICheckBox isotflexible;
	private UILabel gzsjLabel;
	private UIRefPane gzsj;
	private UILabel gzscHourLabel;
	private UILabel begindayLabel;
	private UIComboBox beginday;
	private UIRefPane begintime;
	private UILabel enddayLabel;
	private UIComboBox endday;
	private UIRefPane endtime;
	private UIPanel legstTimePanel;
	private UILabel earliestbegindayLabel;
	private UIComboBox earliestbeginday;
	private UIRefPane earliestbegintime;
	private UILabel label12latestbegindayLabel;
	private UIComboBox latestbeginday;
	private UIRefPane latestbegintime;
	private UILabel earliestenddayLabel;
	private UIComboBox earliestendday;
	private UIRefPane earliestendtime;
	private UILabel latestenddayLabel;
	private UIComboBox latestendday;
	private UIRefPane latestendtime;
	private UICheckBox isrttimeflexible;
	private BillCardPanel billCardPanel;
	private UIPanel nightWorkPanel;
	private UICheckBox includenightshift;
	private UILabel nightbegindayLabel;
	private UIComboBox nightbeginday;
	private UIRefPane nightbegintime;
	private UILabel nightenddayLabel;
	private UIComboBox nightendday;
	private UIRefPane nightendtime;
	private UFBoolean ishredited;
	private Stack<String> timeStack;
	FormLayout layout = new FormLayout(
			"17dlu, 75dlu, 65dlu, 60dlu, 40dlu, 80dlu, 65dlu, 40dlu, 23dlu",
			"20dlu, 20dlu,default");

	private UICheckBox isSingleCard;

	private UIPanel singleCardPanel;

	private UIComboBox singleCardTime;

	private LeRulePanel leRulePanel;

	public WrTimeRulePanel(List<JComponent> shiftCompList,
			List<JComponent> mustInputList, BillListView listView,
			Stack<String> timeStack) {
		this.shiftCompList = shiftCompList;
		this.mustInputList = mustInputList;
		this.listView = listView;
		this.timeStack = timeStack;
	}

	public void initComponents(CellConstraints cc) {
		if (mayInputList == null) {
			mayInputList = new ArrayList();
		}
		String widths = "right:120px,10px,120px,10px,120px,10px,right:120px,10px,120px,10px,left:120px,60px";
		FormLayout layout = new FormLayout(widths, "");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
		builder.append(getTimebegindayLabel());
		builder.append(getTimebeginday());
		builder.append(gettimebegintime());
		builder.append(getTimeenddayLabel());
		builder.append(getTimeendday());
		builder.append(getTimeendtime());
		builder.nextLine();

		builder.append("");
		builder.append(getIsotflexible(), 3);
		builder.append(getGzsjLabel());
		builder.append(getGzsj());
		builder.append(getGzscHourLabel());
		builder.nextLine();

		builder.append(getBegindayLabel());
		builder.append(getBeginday());
		builder.append(getBegintime());
		builder.append(getEnddayLabel());
		builder.append(getEndday());
		builder.append(getEndtime());
		builder.nextLine();

		// ssx added for WNC workday overtime begin time
		builder.append(getWorkdayOvertmBeginTimeLable());
		builder.append(getWorkdaybeginday());
		builder.append(getWorkdayOvertmBeginTime());
		builder.nextLine();
		// end

		builder.append(getEarliestbegindayLabel());
		builder.append(getEarliestbeginday());
		builder.append(getEarliestbegintime());
		builder.append(getLabel12latestbegindayLabel());
		builder.append(getLatestbeginday());
		builder.append(getLatestbegintime());
		builder.nextLine();

		builder.append(getEarliestenddayLabel());
		builder.append(getEarliestendday());
		builder.append(getEarliestendtime());
		builder.append(getLatestenddayLabel());
		builder.append(getLatestendday());
		builder.append(getLatestendtime());
		builder.nextLine();

		builder.append("");
		builder.append(getIsSingleCard());
		builder.append(getSingleCardTime());
		builder.append("");
		builder.append(getIsrttimeflexible());
		builder.nextLine();

		builder.append("");
		getBillCardPanel().getBodyPanel().setPreferredSize(
				new Dimension(220, 120));
		builder.append(getBillCardPanel().getBodyPanel(), 10);
		builder.nextLine();

		builder.append("");
		builder.append(getIncludenightshift());
		builder.nextLine();

		builder.append(getNightbegindayLabel());
		builder.append(getNightbeginday());
		builder.append(getNightbegintime());
		builder.append(getNightenddayLabel());
		builder.append(getNightendday());
		builder.append(getNightendtime());
		builder.nextLine();
	}

	private UILabel workdayovertimelable;

	private UILabel getWorkdayOvertmBeginTimeLable() {
		if (workdayovertimelable == null) {
			workdayovertimelable = new UILabel();

			workdayovertimelable.setText("平日加班起始時間");
		}
		return workdayovertimelable;
	}

	private UIRefPane workdayovertime;

	private UIRefPane getWorkdayOvertmBeginTime() {
		if (workdayovertime == null) {
			workdayovertime = new UIRefPane();
			workdayovertime.setTextType("TextTime");
			workdayovertime.addValueChangedListener(this);
			shiftCompList.add(workdayovertime);
			workdayovertime.setName("wncovertmbegin");
			workdayovertime.setButtonVisible(false);
			workdayovertime.addValueChangedListener(this);
		}
		return workdayovertime;
	}

	private UIComboBox workdaybeginday;

	public UIComboBox getWorkdaybeginday() {
		if (workdaybeginday == null) {
			workdaybeginday = new UIComboBox();
			workdaybeginday.setPreferredSize(new Dimension(63, 20));
			workdaybeginday.addItems(getDay());
			workdaybeginday.setEnabled(false);
			workdaybeginday.addItemListener(this);
		}
		return workdaybeginday;
	}

	public AggShiftVO getValue(AggShiftVO aggvo) {
		ShiftVO mainVo = (ShiftVO) aggvo.getParentVO();
		mainVo.setTimebeginday((Integer) getTimebeginday()
				.getSelectdItemValue());
		if (gettimebegintime().getValueObj() != null) {
			mainVo.setTimebegintime(((UFTime) gettimebegintime().getValueObj())
					.toLocalString());
		}

		mainVo.setTimeendday((Integer) getTimeendday().getSelectdItemValue());
		if (getTimeendtime().getValueObj() != null) {

			mainVo.setTimeendtime(((UFTime) getTimeendtime().getValueObj())
					.toLocalString());
		}

		mainVo.setIsotflexible(UFBoolean
				.valueOf(getIsotflexible().isSelected()));

		// ssx added for WND workday overtime begin time
		if (getWorkdayOvertmBeginTime().getValueObj() != null) {

			mainVo.setWncovertmbegin(((UFTime) getWorkdayOvertmBeginTime()
					.getValueObj()).toLocalString());
		} else {
			mainVo.setWncovertmbegin(null);
		}
		// end

		if (getIsotflexible().isSelected()) {
			mainVo.setBeginday((Integer) getEarliestbeginday()
					.getSelectdItemValue());
			if (getEarliestbegintime().getValueObj() != null) {

				mainVo.setBegintime(((UFTime) getEarliestbegintime()
						.getValueObj()).toLocalString());
			}

			mainVo.setEndday((Integer) getLatestendday().getSelectdItemValue());
			if (getLatestendtime().getValueObj() != null) {

				mainVo.setEndtime(((UFTime) getLatestendtime().getValueObj())
						.toLocalString());
			} else {
				mainVo.setEndtime(null);
			}

			mainVo.setLatestbeginday((Integer) getLatestbeginday()
					.getSelectdItemValue());
			if (getLatestbegintime().getValueObj() != null) {

				mainVo.setLatestbegintime(((UFTime) getLatestbegintime()
						.getValueObj()).toLocalString());
			}

			mainVo.setEarliestendday((Integer) getEarliestendday()
					.getSelectdItemValue());
			if (getEarliestendtime().getValueObj() != null) {

				mainVo.setEarliestendtime(((UFTime) getEarliestendtime()
						.getValueObj()).toLocalString());
			}
		} else {
			mainVo.setBeginday((Integer) getBeginday().getSelectdItemValue());
			if (getBegintime().getValueObj() != null) {

				mainVo.setBegintime(((UFTime) getBegintime().getValueObj())
						.toLocalString());
			}

			mainVo.setEndday((Integer) getEndday().getSelectdItemValue());
			if (getEndtime().getValueObj() != null) {

				mainVo.setEndtime(((UFTime) getEndtime().getValueObj())
						.toLocalString());
			}

			mainVo.setLatestbeginday(mainVo.getBeginday());
			mainVo.setLatestbegintime(mainVo.getBegintime());
			mainVo.setEarliestendday(mainVo.getEndday());
			mainVo.setEarliestendtime(mainVo.getEndtime());
		}

		mainVo.setIssinglecard(UFBoolean
				.valueOf(getIsSingleCard().isSelected()));
		mainVo.setCardtype((Integer) getSingleCardTime().getSelectdItemValue());

		mainVo.setIsrttimeflexible(UFBoolean.valueOf(getIsrttimeflexible()
				.isSelected()));

		if ((getIsotflexible().isSelected())
				&& (!StringUtils.isEmpty(getBegintime().getText()))
				&& (!StringUtils.isEmpty(getLatestbegintime().getText()))
				&& (getBeginday().getSelectdItemValue()
						.equals(getLatestbeginday().getSelectdItemValue()))
				&& (!getLatestbegintime().getText().equals(
						getBegintime().getText()))) {

			mainVo.setIsotflexiblefinal(UFBoolean.TRUE);
		} else {
			mainVo.setIsotflexiblefinal(UFBoolean.FALSE);
		}

		mainVo.setGzsj((UFDouble) getGzsj().getValueObj());

		mainVo.setIncludenightshift(UFBoolean.valueOf(getIncludenightshift()
				.isSelected()));

		mainVo.setNightbeginday((Integer) getNightbeginday()
				.getSelectdItemValue());
		if (getNightbegintime().getValueObj() != null) {

			mainVo.setNightbegintime(((UFTime) getNightbegintime()
					.getValueObj()).toLocalString());
		}

		mainVo.setNightendday((Integer) getNightendday().getSelectdItemValue());
		if (getNightbegintime().getValueObj() != null) {

			mainVo.setNightendtime(((UFTime) getNightendtime().getValueObj())
					.toLocalString());
		}

		UFBoolean isrttimeflexiblefinal = UFBoolean.FALSE;
		BillModel model = getBillCardPanel().getBillModel();
		RTVO[] rtVos = (RTVO[]) (RTVO[]) model.getBodyValueVOs(RTVO.class
				.getName());
		List<RTVO> rtVoFinalList = new ArrayList();
		for (int i = 0; i < rtVos.length; i++) {
			RTVO rtvo = rtVos[i];
			if ((rtvo.getVirBegintime() != null)
					|| (rtvo.getVirEndtime() != null)
					|| (rtvo.getResttime() != null)) {

				if (rtvo.getVirBegintime() != null)
					rtvo.setBegintime(rtvo.getVirBegintime().toString());
				if (rtvo.getVirEndtime() != null)
					rtvo.setEndtime(rtvo.getVirEndtime().toString());
				rtvo.setContainsLastSecond(false);
				rtvo.setTimeid(Integer.valueOf(i));
				rtvo.setPk_org(mainVo.getPk_org());
				rtvo.setPk_group(mainVo.getPk_group());
				rtvo.setStatus(2);

				if ((getIsrttimeflexible().isSelected())
						&& (rtvo.getBeginday() != null)
						&& (!StringUtils.isEmpty(rtvo.getBegintime()))
						&& (rtvo.getEndday() != null)
						&& (!StringUtils.isEmpty(rtvo.getEndtime()))
						&& (!StringUtils.isEmpty(rtvo.getEndtime()))
						&& (rtvo.getResttime() != null)) {

					IRelativeTime elstStartTime = new DefaultRelativeTime();
					elstStartTime.setDate(rtvo.getBeginday().intValue());
					elstStartTime.setTime(rtvo.getBegintime());

					IRelativeTime ltstEndTime = new DefaultRelativeTime();
					ltstEndTime.setDate(rtvo.getEndday().intValue());
					ltstEndTime.setTime(rtvo.getEndtime());

					if (RelativeTimeUtils.getLength(elstStartTime, ltstEndTime) != rtvo
							.getResttime().longValue() * 60L) {
						rtvo.setIsflexible(UFBoolean.TRUE);
						isrttimeflexiblefinal = UFBoolean.TRUE;
					} else {
						rtvo.setIsflexible(UFBoolean.FALSE);
					}
				}
				rtVoFinalList.add(rtvo);
			}
		}
		RTVO[] newrtvos = (RTVO[]) rtVoFinalList.toArray(new RTVO[0]);
		aggvo.setTableVO("rt_sub", newrtvos);

		int rtAllSec = (int) (getRtMnuTime(newrtvos,
				mainVo.getIsrttimeflexible().booleanValue()).doubleValue() * 3600.0D);

		Integer gzsc = Integer.valueOf((int) (StringUtils.isEmpty(getGzsj()
				.getText()) ? 0.0D : ((UFDouble) getGzsj().getValueObj())
				.doubleValue() * 3600.0D));
		mainVo.setWorklen(Integer.valueOf(rtAllSec + gzsc.intValue()));

		mainVo.setIsrttimeflexiblefinal(isrttimeflexiblefinal);

		mainVo.setIsflexiblefinal(UFBoolean.valueOf((isrttimeflexiblefinal
				.booleanValue())
				|| (mainVo.getIsotflexiblefinal().booleanValue())));

		if ((mainVo.getIshredited() == null)
				|| (!mainVo.getIshredited().booleanValue())) {
			mainVo.setIshredited(getIshredited());
		}
		return aggvo;
	}

	public void setValue(AggShiftVO aggvo) {
		getTimeStack().push("setWrTimeValue");

		ShiftVO mainVo = aggvo.getShiftVO();

		getTimebeginday().setSelectedItem(mainVo.getTimebeginday());
		gettimebegintime().setValueObj(
				StringUtils.isEmpty(mainVo.getTimebegintime()) ? null
						: new UFTime(mainVo.getTimebegintime()));

		getTimeendday().setSelectedItem(mainVo.getTimeendday());
		getTimeendtime().setValueObj(
				StringUtils.isEmpty(mainVo.getTimeendtime()) ? null
						: new UFTime(mainVo.getTimeendtime()));

		getIsotflexible().setSelected(
				mainVo.getIsotflexible() == null ? false : mainVo
						.getIsotflexible().booleanValue());

		getGzsj().setValueObj(mainVo.getGzsj());

		getBeginday().setSelectedItem(mainVo.getBeginday());
		getBegintime().setValueObj(
				StringUtils.isEmpty(mainVo.getBegintime()) ? null : new UFTime(
						mainVo.getBegintime()));

		getEndday().setSelectedItem(mainVo.getEndday());
		getEndtime().setValueObj(
				StringUtils.isEmpty(mainVo.getEndtime()) ? null : new UFTime(
						mainVo.getEndtime()));

		// ssx added for WNC workday overtime begintime
			//Ares.Tank 2018-8-6 11:17:06  endday 日期枚举的规则为-1=前一日，0=当日，1=后一日 需要进行索引转换 start
		this.getWorkdaybeginday().setSelectedIndex(getIndexForEndDay(mainVo.getEndday()));
			//Ares.Tank 2018-8-6 11:17:06  endday 日期枚举的规则为-1=前一日，0=当日，1=后一日 需要进行索引转换 end
		this.getWorkdayOvertmBeginTime().setValueObj(
				StringUtils.isEmpty(mainVo.getWncovertmbegin()) ? null
						: new UFTime(mainVo.getWncovertmbegin()));
		// end

		getEarliestbeginday().setSelectedItem(mainVo.getBeginday());
		getEarliestbegintime().setValueObj(
				StringUtils.isEmpty(mainVo.getBegintime()) ? null : new UFTime(
						mainVo.getBegintime()));

		getLatestbeginday().setSelectedItem(mainVo.getLatestbeginday());
		getLatestbegintime().setValueObj(
				StringUtils.isEmpty(mainVo.getLatestbegintime()) ? null
						: new UFTime(mainVo.getLatestbegintime()));

		getEarliestendday().setSelectedItem(mainVo.getEarliestendday());
		getEarliestendtime().setValueObj(
				StringUtils.isEmpty(mainVo.getEarliestendtime()) ? null
						: new UFTime(mainVo.getEarliestendtime()));

		getLatestendday().setSelectedItem(mainVo.getEndday());
		getLatestendtime().setValueObj(
				StringUtils.isEmpty(mainVo.getEndtime()) ? null : new UFTime(
						mainVo.getEndtime()));

		getIsrttimeflexible().setSelected(
				mainVo.getIsrttimeflexible() == null ? false : mainVo
						.getIsrttimeflexible().booleanValue());

		getIncludenightshift().setSelected(
				mainVo.getIncludenightshift() == null ? false : mainVo
						.getIncludenightshift().booleanValue());

		getNightbeginday().setSelectedItem(mainVo.getNightbeginday());
		getNightbegintime().setValueObj(
				StringUtils.isEmpty(mainVo.getNightbegintime()) ? null
						: new UFTime(mainVo.getNightbegintime()));

		getNightendday().setSelectedItem(mainVo.getNightendday());
		getNightendtime().setValueObj(
				StringUtils.isEmpty(mainVo.getNightendtime()) ? null
						: new UFTime(mainVo.getNightendtime()));

		getIsSingleCard().setSelected(
				mainVo.getIssinglecard() == null ? false : mainVo
						.getIssinglecard().booleanValue());
		getSingleCardTime().setSelectedItem(mainVo.getCardtype());

		ishredited = mainVo.getIshredited();

		BillModel rtBillModel = getBillCardPanel().getBillModel();
		if (rtBillModel == null) {
			return;
		}
		RTVO[] rtVos = aggvo.getRTVOs();
		rtBillModel.clearBodyData();
		getBillCardPanel().getBodyPanel().addLine(3);

		if (rtVos == null) {
			addDefaultLineBatch(rtBillModel);

			getTimeStack().pop();
			setIsotflexibleEnable();
			return;
		}
		VOUtil.ascSort(rtVos, new String[] { "timeid" });

		for (int i = 0; i < 3; i++) {
			boolean hasThis = false;
			int timeid = -1;
			RTVO rtVo = null;

			for (int j = 0; j < rtVos.length; j++) {
				rtVo = rtVos[j];
				timeid = rtVo.getTimeid().intValue();

				if (timeid == i) {
					hasThis = true;
					break;
				}
			}

			if (!hasThis) {
				addDefaultLine(rtBillModel, i);
			} else {
				rtBillModel.setValueAt(rtVo.getPk_rt(), timeid, "pk_rt");
				rtBillModel.setValueAt(rtVo.getPk_shift(), timeid, "pk_shift");
				rtBillModel.setValueAt(rtVo.getPk_org(), timeid, "pk_org");
				rtBillModel.setValueAt(rtVo.getPk_group(), timeid, "pk_group");
				rtBillModel.setValueAt(rtVo.getBeginday(), timeid, "beginday");
				rtBillModel.setValueAt(rtVo.getBegintime(), timeid,
						"virBegintime");
				rtBillModel.setValueAt(rtVo.getEndday(), timeid, "endday");
				rtBillModel.setValueAt(rtVo.getEndtime(), timeid, "virEndtime");
				rtBillModel.setValueAt(rtVo.getTimeid(), timeid, "timeid");
				rtBillModel
						.setValueAt(rtVo.getCheckflag(), timeid, "checkflag");
				rtBillModel.setValueAt(rtVo.getIsflexible(), timeid,
						"isflexible");
				rtBillModel.setValueAt(rtVo.getResttime(), timeid, "resttime");
			}
		}

		getTimeStack().pop();
		setIsotflexibleEnable();
	}

	private void addDefaultLineBatch(BillModel rtBillModel) {
		for (int i = 0; i < 3; i++) {
			addDefaultLine(rtBillModel, i);
		}
	}

	private void addDefaultLine(BillModel rtBillModel, int rownum) {
		RTVO rtvo = new RTVO();
		rtvo.setBeginday(Integer.valueOf(0));
		rtvo.setEndday(Integer.valueOf(0));
		rtBillModel.setBodyRowVO(rtvo, rownum);
	}

	public UIPanel getWorkPanel() {
		if (workPanel == null) {
			workPanel = new UIPanel();
		}

		return workPanel;
	}

	public UILabel getTimebegindayLabel() {
		if (timebegindayLabel == null) {
			timebegindayLabel = new UILabel();
			timebegindayLabel.setText(ResHelper.getString("hrbd", "0hrbd0048"));
		}
		return timebegindayLabel;
	}

	public UIComboBox getTimebeginday() {
		if (timebeginday == null) {
			timebeginday = new UIComboBox();
			timebeginday.setPreferredSize(new Dimension(63, 20));
			timebeginday.addItems(getDay());
			timebeginday.addItemListener(this);
			shiftCompList.add(timebeginday);
		}
		return timebeginday;
	}

	public UIRefPane gettimebegintime() {
		if (timebegintime == null) {
			timebegintime = new UIRefPane();
			timebegintime.setName("timebegintime");
			timebegintime.setButtonVisible(false);
			timebegintime.setTextType("TextTime");
			timebegintime.addValueChangedListener(this);
			shiftCompList.add(timebegintime);
			mustInputList.add(timebegintime);
		}
		return timebegintime;
	}

	public UILabel getTimeenddayLabel() {
		if (timeenddayLabel == null) {
			timeenddayLabel = new UILabel();
			timeenddayLabel.setText(ResHelper.getString("hrbd", "0hrbd0049"));
		}
		return timeenddayLabel;
	}

	public UIComboBox getTimeendday() {
		if (timeendday == null) {
			timeendday = new UIComboBox();
			timeendday.setPreferredSize(new Dimension(63, 20));
			timeendday.addItems(getDay());
			timeendday.addItemListener(this);
			shiftCompList.add(timeendday);
		}
		return timeendday;
	}

	public UIRefPane getTimeendtime() {
		if (timeendtime == null) {
			timeendtime = new UIRefPane();
			timeendtime.setTextType("TextTime");
			shiftCompList.add(timeendtime);
			timeendtime.setName("timeendtime");
			timeendtime.setButtonVisible(false);
			mustInputList.add(timeendtime);
			timeendtime.addValueChangedListener(this);
		}
		return timeendtime;
	}

	public UIPanel getIsotflexblePanel() {
		UIPanel isotflexPanel = new UIPanel();
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(0);
		isotflexPanel.setLayout(layout);
		isotflexPanel.add(getIsotflexible());
		isotflexPanel
				.add(new UILabel(ResHelper.getString("hrbd", "0hrbd0189")));
		return isotflexPanel;
	}

	public UICheckBox getIsotflexible() {
		if (isotflexible == null) {
			isotflexible = new UICheckBox(ResHelper.getString("hrbd",
					"0hrbd0189"));
			isotflexible.addActionListener(this);
			shiftCompList.add(isotflexible);
		}
		return isotflexible;
	}

	public UILabel getGzsjLabel() {
		if (gzsjLabel == null) {
			gzsjLabel = new UILabel();
			gzsjLabel.setText(ResHelper.getString("shift", "1shift0001"));
		}
		return gzsjLabel;
	}

	public UIRefPane getGzsj() {
		if (gzsj == null) {
			gzsj = new UIRefPane();
			gzsj.setTextType("TextDbl");
			gzsj.setNumPoint(2);
			gzsj.setMinValue(new UFDouble(0).doubleValue());
			gzsj.setMaxValue(new UFDouble(72).doubleValue());
			gzsj.addValueChangedListener(this);
			shiftCompList.add(gzsj);
			gzsj.setName("gzsj");
			gzsj.setButtonVisible(false);
		}
		return gzsj;
	}

	public UILabel getGzscHourLabel() {
		if (gzscHourLabel == null) {
			gzscHourLabel = new UILabel();
			gzscHourLabel.setText(ResHelper.getString("hrbd", "0hrbd0164"));
			gzscHourLabel.setHorizontalAlignment(4);
		}
		return gzscHourLabel;
	}

	public UILabel getBegindayLabel() {
		if (begindayLabel == null) {
			begindayLabel = new UILabel();
			begindayLabel.setText(ResHelper
					.getString("hrbd", "2bdshift-000072"));
		}
		return begindayLabel;
	}

	public UIComboBox getBeginday() {
		if (beginday == null) {
			beginday = new UIComboBox();
			beginday.setPreferredSize(new Dimension(93, 20));
			beginday.addItems(getDay());
			beginday.addItemListener(this);
			shiftCompList.add(beginday);
		}
		return beginday;
	}

	public UIRefPane getBegintime() {
		if (begintime == null) {
			begintime = new UIRefPane();
			begintime.setTextType("TextTime");
			begintime.addValueChangedListener(this);
			shiftCompList.add(begintime);
			begintime.setName("begintime");
			begintime.setButtonVisible(false);
			mustInputList.add(begintime);
		}
		return begintime;
	}

	public UILabel getEnddayLabel() {
		if (enddayLabel == null) {
			enddayLabel = new UILabel();
			enddayLabel.setText(ResHelper.getString("hrbd", "2bdshift-000007"));
		}
		return enddayLabel;
	}

	public UIComboBox getEndday() {
		if (endday == null) {
			endday = new UIComboBox();
			endday.setPreferredSize(new Dimension(63, 20));
			endday.addItems(getDay());
			endday.addItemListener(this);
			shiftCompList.add(endday);
		}
		return endday;
	}

	public UIRefPane getEndtime() {
		if (endtime == null) {
			endtime = new UIRefPane();
			endtime.setTextType("TextTime");
			endtime.addValueChangedListener(this);
			shiftCompList.add(endtime);
			endtime.setName("begintime");
			endtime.setButtonVisible(false);
			mustInputList.add(endtime);
		}
		return endtime;
	}

	public UIPanel getLegstTimePanel() {
		if (legstTimePanel == null) {
			legstTimePanel = new UIPanel();
			legstTimePanel
					.setLayout(new FormLayout(
							"15dlu, 65dlu, 45dlu, 60dlu, 40dlu, 61dlu, 46dlu, 53dlu, 10dlu",
							"14dlu"));
		}

		return legstTimePanel;
	}

	public UILabel getEarliestbegindayLabel() {
		if (earliestbegindayLabel == null) {
			earliestbegindayLabel = new UILabel();
			earliestbegindayLabel.setText(ResHelper.getString("hrbd",
					"0hrbd0052"));
		}
		return earliestbegindayLabel;
	}

	public UIComboBox getEarliestbeginday() {
		if (earliestbeginday == null) {
			earliestbeginday = new UIComboBox();
			earliestbeginday.setEnabled(false);
			earliestbeginday.setPreferredSize(new Dimension(63, 20));
			earliestbeginday.addItems(getDay());
			earliestbeginday.addItemListener(this);
			shiftCompList.add(earliestbeginday);
		}
		return earliestbeginday;
	}

	public UIRefPane getEarliestbegintime() {
		if (earliestbegintime == null) {
			earliestbegintime = new UIRefPane();
			earliestbegintime.setEnabled(false);
			earliestbegintime.setTextType("TextTime");
			shiftCompList.add(earliestbegintime);
			earliestbegintime.setName("earliestbegintime");
			earliestbegintime.setButtonVisible(false);
			earliestbegintime.addValueChangedListener(this);
		}
		return earliestbegintime;
	}

	public UILabel getLabel12latestbegindayLabel() {
		if (label12latestbegindayLabel == null) {
			label12latestbegindayLabel = new UILabel();
			label12latestbegindayLabel.setText(ResHelper.getString("hrbd",
					"2bdshift-000056"));
		}
		return label12latestbegindayLabel;
	}

	public UIComboBox getLatestbeginday() {
		if (latestbeginday == null) {
			latestbeginday = new UIComboBox();
			latestbeginday.setEnabled(false);
			latestbeginday.setPreferredSize(new Dimension(63, 20));
			latestbeginday.addItems(getDay());
			latestbeginday.addItemListener(this);
			shiftCompList.add(latestbeginday);
		}
		return latestbeginday;
	}

	public UIRefPane getLatestbegintime() {
		if (latestbegintime == null) {
			latestbegintime = new UIRefPane();
			latestbegintime.setEnabled(false);
			latestbegintime.setTextType("TextTime");
			shiftCompList.add(latestbegintime);
			latestbegintime.setName("latestbegintime");
			latestbegintime.setButtonVisible(false);
			latestbegintime.addValueChangedListener(this);
		}
		return latestbegintime;
	}

	public UILabel getEarliestenddayLabel() {
		if (earliestenddayLabel == null) {
			earliestenddayLabel = new UILabel();
			earliestenddayLabel.setText(ResHelper.getString("hrbd",
					"2bdshift-000019"));
		}
		return earliestenddayLabel;
	}

	public UIComboBox getEarliestendday() {
		if (earliestendday == null) {
			earliestendday = new UIComboBox();
			earliestendday.setEnabled(false);
			earliestendday.setPreferredSize(new Dimension(63, 20));
			earliestendday.addItems(getDay());
			earliestendday.addItemListener(this);
			shiftCompList.add(earliestendday);
		}
		return earliestendday;
	}

	public UIRefPane getEarliestendtime() {
		if (earliestendtime == null) {
			earliestendtime = new UIRefPane();
			earliestendtime.setEnabled(false);
			earliestendtime.setTextType("TextTime");
			shiftCompList.add(earliestendtime);
			earliestendtime.setName("earliestendtime");
			earliestendtime.setButtonVisible(false);
			earliestendtime.addValueChangedListener(this);
		}
		return earliestendtime;
	}

	public UILabel getLatestenddayLabel() {
		if (latestenddayLabel == null) {
			latestenddayLabel = new UILabel();
			latestenddayLabel.setText(ResHelper.getString("hrbd", "0hrbd0053"));
		}
		return latestenddayLabel;
	}

	public UIComboBox getLatestendday() {
		if (latestendday == null) {
			latestendday = new UIComboBox();
			latestendday.setEnabled(false);
			latestendday.setPreferredSize(new Dimension(63, 20));
			latestendday.addItems(getDay());
			latestendday.addItemListener(this);
			shiftCompList.add(latestendday);
		}
		return latestendday;
	}

	public UIRefPane getLatestendtime() {
		if (latestendtime == null) {
			latestendtime = new UIRefPane();
			latestendtime.setEnabled(false);
			latestendtime.setTextType("TextTime");
			shiftCompList.add(latestendtime);
			latestendtime.setName("latestendtime");
			latestendtime.setButtonVisible(false);
			latestendtime.addValueChangedListener(this);
		}
		return latestendtime;
	}

	public UICheckBox getIsrttimeflexible() {
		if (isrttimeflexible == null) {
			isrttimeflexible = new UICheckBox(ResHelper.getString("hrbd",
					"0hrbd0192"));
			isrttimeflexible.addActionListener(this);
			shiftCompList.add(isrttimeflexible);
		}
		return isrttimeflexible;
	}

	public BillCardPanel getBillCardPanel() {
		if (billCardPanel == null) {
			BillTempletVO btv = getTempVO();
			billCardPanel = new BillCardPanel();
			BillTempletBodyVO[] bodyVos = btv.getBodyVO();
			List<BillTempletBodyVO> bodyVoList = new ArrayList();
			for (int i = 0; i < bodyVos.length; i++) {
				BillTempletBodyVO bodyvo = bodyVos[i];
				if ((bodyvo.getItemkey().equals("begintime"))
						&& (bodyvo.getPos().intValue() == 1)) {
					BillTempletBodyVO templetBodyVO = (BillTempletBodyVO) bodyvo
							.clone();
					templetBodyVO.setListshowflag(Boolean.valueOf(true));
					templetBodyVO.setDatatype(Integer.valueOf(8));
					templetBodyVO.setDefaultshowname(ResHelper.getString(
							"common", "UC000-0001894"));
					templetBodyVO.setEditflag(Boolean.valueOf(true));
					templetBodyVO.setItemkey("virBegintime");
					templetBodyVO.setIdcolname(null);
					templetBodyVO.setMetadataproperty(null);
					templetBodyVO.setMetadatapath(null);
					bodyVoList.add(templetBodyVO);

				} else if ((bodyvo.getItemkey().equals("endtime"))
						&& (bodyvo.getPos().intValue() == 1)) {
					BillTempletBodyVO templetBodyVO = (BillTempletBodyVO) bodyvo
							.clone();
					templetBodyVO.setListshowflag(Boolean.valueOf(true));
					templetBodyVO.setDatatype(Integer.valueOf(8));
					templetBodyVO.setDefaultshowname(ResHelper.getString(
							"common", "UC000-0003232"));
					templetBodyVO.setEditflag(Boolean.valueOf(true));
					templetBodyVO.setItemkey("virEndtime");
					templetBodyVO.setIdcolname(null);
					templetBodyVO.setMetadataproperty(null);
					templetBodyVO.setMetadatapath(null);
					templetBodyVO.setTable_code(bodyvo.getTable_code());
					templetBodyVO.setTable_name(bodyvo.getTable_name());
					bodyVoList.add(templetBodyVO);
				} else {
					bodyVoList.add(bodyvo);
				}
			}
			bodyVos = (BillTempletBodyVO[]) bodyVoList.toArray(bodyVos);
			btv.setChildrenVO(bodyVos);
			billCardPanel.setBillData(new BillData(btv));
			billCardPanel.addEditListener(this);
			billCardPanel.getBodyPanel().addEditListener2(this);
			billCardPanel.setBodyAutoAddLine(false);
			billCardPanel.setBodyMenuShow(false);
			billCardPanel.setBodyMultiSelect(false);
			billCardPanel.getBodyPanel().getTable().setSortEnabled(false);
			BillScrollPane rtCardPanel = billCardPanel.getBodyPanel();
			rtCardPanel.addLine(3);
			int[] columnWidths = { 50, 50, 50, 50, 50, 55 };
			rtCardPanel.getTable().setColumnWidth(columnWidths);
			rtCardPanel.getTable().setPreferredSize(new Dimension(629, 66));
			rtCardPanel.setBorder(new TitledBorder(null, ResHelper.getString(
					"hrbd", "0hrbd0193"), 4, 0, null, null));

			billCardPanel.getBillModel();
			shiftCompList.add(billCardPanel.getBodyPanel());
		}
		return billCardPanel;
	}

	public UIPanel getnightWorkPanel() {
		if (nightWorkPanel == null) {
			nightWorkPanel = new UIPanel();

			nightWorkPanel.setLayout(layout);
		}

		return nightWorkPanel;
	}

	public UICheckBox getIncludenightshift() {
		if (includenightshift == null) {
			includenightshift = new UICheckBox(ResHelper.getString("shift",
					"0shift0001"));
			includenightshift.addActionListener(this);
			shiftCompList.add(includenightshift);
		}
		return includenightshift;
	}

	public UILabel getNightbegindayLabel() {
		if (nightbegindayLabel == null) {
			nightbegindayLabel = new UILabel();
			nightbegindayLabel.setText(ResHelper.getString("common",
					"UC000-0001396"));
		}
		return nightbegindayLabel;
	}

	public UIComboBox getNightbeginday() {
		if (nightbeginday == null) {
			nightbeginday = new UIComboBox();
			nightbeginday.setPreferredSize(new Dimension(63, 20));
			nightbeginday.addItems(getDay());
			nightbeginday.addItemListener(this);
			shiftCompList.add(nightbeginday);
		}
		return nightbeginday;
	}

	public UIRefPane getNightbegintime() {
		if (nightbegintime == null) {
			nightbegintime = new UIRefPane();
			nightbegintime.setTextType("TextTime");
			shiftCompList.add(nightbegintime);
			nightbegintime.setName("nightbegintime");
			nightbegintime.setButtonVisible(false);
			mayInputList.add(nightbegintime);
			nightbegintime.addValueChangedListener(this);
		}

		return nightbegintime;
	}

	public UILabel getNightenddayLabel() {
		if (nightenddayLabel == null) {
			nightenddayLabel = new UILabel();
			nightenddayLabel.setText(ResHelper.getString("common",
					"UC000-0001398"));
		}
		return nightenddayLabel;
	}

	public UIComboBox getNightendday() {
		if (nightendday == null) {
			nightendday = new UIComboBox();
			nightendday.setPreferredSize(new Dimension(63, 20));
			nightendday.addItems(getDay());
			nightendday.addItemListener(this);
			shiftCompList.add(nightendday);
		}
		return nightendday;
	}

	public UIRefPane getNightendtime() {
		if (nightendtime == null) {
			nightendtime = new UIRefPane();
			nightendtime.setTextType("TextTime");
			shiftCompList.add(nightendtime);
			nightendtime.setName("nightendtime");
			nightendtime.setButtonVisible(false);
			mayInputList.add(nightendtime);
			nightendtime.addValueChangedListener(this);
		}
		return nightendtime;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(getIsotflexible())) {
			setIsotflexibleEnable();
			setSingleFlexibeEnabel();
		}

		if (e.getSource().equals(getIsrttimeflexible())) {
			setIsrttimeflexibleEnable();
			setSingleFlexibeEnabel();
		}

		if (e.getSource().equals(getIncludenightshift())) {
			setIncludenightshiftEnable();
		}

		if (e.getSource().equals(getIsSingleCard())) {
			setSingleFlexibeEnabel();
			if (!getIsSingleCard().isSelected()) {
				getSingleCardTime().setSelectedItem(null);
			}
			singleCardTypeChange();
		}

		setIshredited(UFBoolean.TRUE);
	}

	public void setIsotflexibleEnable() {
		boolean isflexible = getIsotflexible().isSelected();

		getGzsj().setEditable(isflexible);

		getIsrttimeflexible().setEnabled(
				(!isflexible) && (UIState.NOT_EDIT != getModel().getUiState()));

		getGzsj().getUITextField().setShowMustInputHint(isflexible);

		setOtflexibeVisible(isflexible);

		setWTVisible(!isflexible);

		getEarliestbegintime().getUITextField()
				.setShowMustInputHint(isflexible);
		getLatestbegintime().getUITextField().setShowMustInputHint(isflexible);

		getEarliestendday().setEnabled(false);
		getEarliestendtime().setEditable(false);
		getLatestendday().setEnabled(false);
		getLatestendtime().setEditable(false);

		if (isflexible) {
			setFlexEndTime();
		}
	}

	public void setOtflexibeVisible(boolean value) {
		getEarliestbegindayLabel().setVisible(value);
		getEarliestbeginday().setVisible(value);
		getEarliestbegintime().setVisible(value);
		getLabel12latestbegindayLabel().setVisible(value);
		getLatestbeginday().setVisible(value);
		getLatestbegintime().setVisible(value);
		getEarliestenddayLabel().setVisible(value);
		getEarliestendday().setVisible(value);
		getEarliestendtime().setVisible(value);
		getLatestenddayLabel().setVisible(value);
		getLatestendday().setVisible(value);
		getLatestendtime().setVisible(value);
	}

	public void setWTVisible(boolean value) {
		getBegindayLabel().setVisible(value);
		getBeginday().setVisible(value);
		getBegintime().setVisible(value);
		getEnddayLabel().setVisible(value);
		getEndday().setVisible(value);
		getEndtime().setVisible(value);
	}

	public void setLegstTimeVsb() {
		getLegstTimePanel().setVisible(getIsotflexible().isSelected());
	}

	public void setIsrttimeflexibleEnable() {
		boolean isrttimeflexible = getIsrttimeflexible().isSelected();

		getIsotflexible().setEnabled(
				(!isrttimeflexible)
						&& (UIState.NOT_EDIT != getModel().getUiState()));

		if (isrttimeflexible) {
			setRtCheckFlag(Boolean.valueOf(true));
		} else {
			BillModel model = getBillCardPanel().getBillModel();
			RTVO[] rtVos = (RTVO[]) (RTVO[]) model.getBodyValueVOs(RTVO.class
					.getName());
			for (int i = 0; i < rtVos.length; i++) {
				RTVO rtvo = rtVos[i];
				if ((rtvo.getBeginday() != null) || (rtvo.getEndday() != null)
						|| (rtvo.getVirBegintime() != null)
						|| (rtvo.getVirEndtime() != null)) {

					model.setValueAt(UFBoolean.FALSE, i, "isflexible");
				}
			}
		}

		computeTime();
	}

	private void setRtCheckFlag(Boolean flag) {
		BillModel model = getBillCardPanel().getBillModel();
		RTVO[] rtVos = (RTVO[]) (RTVO[]) model.getBodyValueVOs(RTVO.class
				.getName());
		for (int i = 0; i < rtVos.length; i++) {
			RTVO rtvo = rtVos[i];
			if ((rtvo.getVirBegintime() == null)
					&& (rtvo.getVirEndtime() == null)) {
				model.setValueAt(UFBoolean.FALSE, i, "checkflag");
			} else {
				model.setValueAt(flag, i, "checkflag");
			}
		}
	}

	public void setIncludenightshiftEnable() {
		boolean includenightshift = getIncludenightshift().isSelected();

		getNightbeginday().setEnabled(includenightshift);
		getNightbegintime().setEnabled(includenightshift);
		getNightendday().setEnabled(includenightshift);
		getNightendtime().setEnabled(includenightshift);

		if (CollectionUtils.isEmpty(mayInputList)) {
			return;
		}
		for (JComponent comp : mayInputList) {
			if ((comp instanceof UIRefPane)) {
				((UIRefPane) comp).getUITextField().setShowMustInputHint(
						includenightshift);
			}
		}
		repaint();
	}

	public void setEnabled(boolean enabled) {
		getBillCardPanel().getBillModel().setEnabled(enabled);
		setLegstTimeVsb();
	}

	public DefaultConstEnum[] getDay() {
		return new DefaultConstEnum[] {
				new DefaultConstEnum(Integer.valueOf(0), ResHelper.getString(
						"hrbd", "2bdshift-000030")),
				new DefaultConstEnum(Integer.valueOf(-1), ResHelper.getString(
						"hrbd", "2bdshift-000035")),
				new DefaultConstEnum(Integer.valueOf(1), ResHelper.getString(
						"hrbd", "2bdshift-000018")) };
	}

	public void afterEdit(BillEditEvent e) {
		if (isRtKey(e)) {
			computeTime();
			if (getIsrttimeflexible().isSelected()) {
				setRtCheckFlag(Boolean.valueOf(true));
			}
		}
		setIshredited(UFBoolean.TRUE);
	}

	public void computeTime() {
		if (getModel().getUiState() == UIState.NOT_EDIT)
			return;
		AggShiftVO editingData = getValue();

		getTimeStack().push("computeTime");
		computeShiftTime(editingData);
		getTimeStack().pop();
	}

	private void computeShiftTime(AggShiftVO aggvo) {
		if (aggvo == null) {
			return;
		}
		if (getIsotflexible().isSelected()) {
			setFlexEndTime();
			return;
		}
		ShiftVO mainVO = (ShiftVO) aggvo.getParentVO();
		RTVO[] rtVos = (RTVO[]) aggvo.getTableVO("rt_sub");

		UFDouble rtAll = getRtMnuTime(rtVos, mainVO.getIsrttimeflexible()
				.booleanValue());

		if (!canComputeWt(mainVO)) {

			return;
		}
		UFDouble worktime = new UFDouble(0);

		IRelativeTimeScope workScope = mainVO.toRelativeWorkScope();

		if (!mainVO.getIsotflexible().booleanValue()) {
			UFDouble worktimeHead = getWtMnsTime(workScope);

			if ((rtVos == null) || (rtVos.length <= 0)
					|| (rtAll.toDouble().doubleValue() <= 0.0D)) {
				worktime = worktimeHead;
			} else {
				worktime = worktimeHead.sub(rtAll);
			}
			getGzsj().setValueObj(
					worktime.doubleValue() >= 0.0D ? worktime : null);
		}
	}

	public void stopEditing() throws ParseException {
		if (CollectionUtils.isEmpty(shiftCompList))
			return;
		for (JComponent comp : shiftCompList) {
			if ((comp instanceof UIRefPane)) {
				((UIRefPane) comp).stopEditing();
			}
		}
		getBillCardPanel().tableStopCellEditing();
	}

	private AggShiftVO getValue() {
		try {
			stopEditing();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		AggShiftVO aggvo = new AggShiftVO();
		ShiftVO mainvo = new ShiftVO();
		aggvo.setParentVO(mainvo);
		return getValue(aggvo);
	}

	public void setFlexEndTime() {
		if (!getIsotflexible().isSelected()) {
			return;
		}

		double gzsc = 0.0D;
		try {
			getGzsj().stopEditing();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		if (getGzsj().getValueObj() != null) {
			gzsc = ((UFDouble) getGzsj().getValueObj()).doubleValue();
		} else {
			gzsc = 0.0D;
		}
		AggShiftVO editingData = getValue();
		ShiftVO mainvo = editingData.getShiftVO();
		RTVO[] rtVos = (RTVO[]) editingData.getTableVO("rt_sub");

		double rtAll = getRtMnuTime(rtVos,
				mainvo.getIsrttimeflexible().booleanValue()).doubleValue();

		if (getEarliestbegintime().getValueObj() != null) {
			IRelativeTime earlyStartTime = new DefaultRelativeTime();
			earlyStartTime
					.setDate(getEarliestbeginday().getSelectdItemValue() == null ? 0
							: ((Integer) getEarliestbeginday()
									.getSelectdItemValue()).intValue());
			earlyStartTime
					.setTime(getEarliestbegintime().getValueObj() == null ? null
							: getEarliestbegintime().getValueObj().toString());

			IRelativeTime elstEndTime = RelativeTimeUtils.add(earlyStartTime,
					(long) ((gzsc + rtAll) * 3600.0D));
			getEarliestendday().setSelectedItem(
					Integer.valueOf(elstEndTime.getDate()));
			getEarliestendtime().setValueObj(
					elstEndTime.getTime() == null ? null : new UFTime(
							elstEndTime.getTime()));
		} else {
			getEarliestendtime().setValueObj(null);
		}

		if (getLatestbegintime().getValueObj() != null) {
			IRelativeTime lastStartTime = new DefaultRelativeTime();
			lastStartTime
					.setDate(getLatestbeginday().getSelectdItemValue() == null ? 0
							: ((Integer) getLatestbeginday()
									.getSelectdItemValue()).intValue());
			lastStartTime
					.setTime(getLatestbegintime().getValueObj() == null ? null
							: getLatestbegintime().getValueObj().toString());

			IRelativeTime ltstEndTime = RelativeTimeUtils.add(lastStartTime,
					(long) ((gzsc + rtAll) * 3600.0D));
			getLatestendday().setSelectedItem(
					Integer.valueOf(ltstEndTime.getDate()));
			getLatestendtime().setValueObj(
					ltstEndTime.getTime() == null ? null : new UFTime(
							ltstEndTime.getTime()));
		} else {
			getLatestendtime().setValueObj(null);
		}
	}

	public void bodyRowChange(BillEditEvent e) {
	}

	public boolean beforeEdit(BillEditEvent e) {
		if ((e.getKey().equals("resttime"))
				&& (!getIsrttimeflexible().isSelected())) {
			return false;
		}

		if ((e.getKey().equals("checkflag"))
				&& (getIsrttimeflexible().isSelected())) {
			return false;
		}

		return true;
	}

	private UFDouble getWtMnsTime(IRelativeTimeScope workScope) {
		IRelativeTime wkStartTime = RelativeTimeScopeUtils
				.getStartTime(workScope);

		IRelativeTime wkEndTime = RelativeTimeScopeUtils.getEndTime(workScope);

		UFDouble worktimeHead = new UFDouble(RelativeTimeUtils.getLength(
				wkStartTime, wkEndTime) / 3600.0D);
		return worktimeHead;
	}

	private UFDouble getRtMnuTime(RTVO[] rtVos, boolean isrttimeflexible) {
		UFDouble rtTimeAll = new UFDouble(0);
		if (ArrayUtils.isEmpty(rtVos)) {
			return rtTimeAll;
		}
		BillModel model = getBillCardPanel().getBillModel();
		for (int i = 0; i < rtVos.length; i++) {
			RTVO rtVo = rtVos[i];

			UFDouble rttime = new UFDouble(0);
			if (canComputeRt(rtVo, isrttimeflexible)) {
				if (rtVo.getIsflexible().booleanValue()) {
					rttime = rtVo.getResttime();
					rtTimeAll = rtTimeAll.add(rtVo.getResttime());
				} else {
					IRelativeTime rtStartTime = RelativeTimeScopeUtils
							.getStartTime(rtVo);

					IRelativeTime rtEndTime = RelativeTimeScopeUtils
							.getEndTime(rtVo);

					UFDouble resttime = new UFDouble(
							RelativeTimeUtils.getLength(rtStartTime, rtEndTime) / 60.0D);
					if (resttime != null) {
						rttime = resttime;
						rtTimeAll = rtTimeAll.add(resttime);

						model.setValueAt(resttime, rtVo.getTimeid().intValue(),
								resttime.doubleValue() >= 0.0D ? "resttime"
										: null);
					}
				}

				IRelativeTime elstRtBeginTime = RelativeTimeScopeUtils
						.getStartTime(rtVo);

				IRelativeTime ltstRtEndTime = RelativeTimeScopeUtils
						.getEndTime(rtVo);

				IRelativeTime elstRtEndTime = RelativeTimeUtils.add(
						elstRtBeginTime, rttime.intValue() * 60);
				model.setValueAt(Integer.valueOf(elstRtEndTime.getDate()), rtVo
						.getTimeid().intValue(), "earliestendday");
				model.setValueAt(elstRtEndTime.getTime(), rtVo.getTimeid()
						.intValue(), "earliestendtime");

				IRelativeTime ltstStartTime = RelativeTimeUtils.minus(
						ltstRtEndTime, rttime.intValue() * 60);
				model.setValueAt(Integer.valueOf(ltstStartTime.getDate()), rtVo
						.getTimeid().intValue(), "latestbeginday");
				model.setValueAt(ltstStartTime.getTime(), rtVo.getTimeid()
						.intValue(), "latestbegintime");
			}
			if ((!isrttimeflexible)
					&& ((rtVo.getBegintime() == null) || (rtVo.getEndtime() == null))) {
				model.setValueAt(null, rtVo.getTimeid().intValue(), "resttime");
			}
		}

		rtTimeAll = rtTimeAll.div(60.0D);

		return rtTimeAll;
	}

	private boolean canComputeWt(ShiftVO mainVO) {
		Integer beginday = mainVO.getBeginday();
		String begintime = mainVO.getBegintime();
		Integer endday = mainVO.getEndday();
		String endtime = mainVO.getEndtime();
		return canCompute(beginday, begintime, endday, endtime);
	}

	private boolean canComputeRt(RTVO mainVO, boolean isrttimeflexible) {
		Integer begindate = mainVO.getBeginday();
		String begintime = mainVO.getBegintime();
		Integer enddate = mainVO.getEndday();
		String endtime = mainVO.getEndtime();
		UFDouble resttime = mainVO.getResttime();
		if (isrttimeflexible) {
			return (canCompute(begindate, begintime, enddate, endtime))
					&& (resttime != null);
		}
		return canCompute(begindate, begintime, enddate, endtime);
	}

	private boolean canCompute(Integer beginDate, String beginTime,
			Integer endDate, String endTime) {
		return (beginDate != null) && (beginTime != null) && (endDate != null)
				&& (endTime != null) && (!"".equals(beginTime))
				&& (!"".equals(endTime));
	}

	private boolean isRtKey(BillEditEvent e) {
		return ("beginday".equals(e.getKey()))
				|| ("virBegintime".equals(e.getKey()))
				|| ("endday".equals(e.getKey()))
				|| ("endtime".equals(e.getKey()))
				|| ("virEndtime".equals(e.getKey()))
				|| (("resttime".equals(e.getKey())) && (getIsrttimeflexible()
						.isSelected()));
	}

	private boolean isWtKey(ItemEvent e) {
		return (getBeginday().equals(e.getSource()))
				|| (getBegintime().equals(e.getSource()))
				|| (getEndday().equals(e.getSource()))
				|| (getEndtime().equals(e.getSource()))
				|| (getGzsj().equals(e.getSource()));
	}

	public boolean isUpdateEndTime(ItemEvent e) {
		return (getEarliestbeginday().equals(e.getSource()))
				|| (getEarliestbegintime().equals(e.getSource()))
				|| (getLatestbeginday().equals(e.getSource()))
				|| (getLatestbegintime().equals(e.getSource()));
	}

	public boolean isUpdateEndTime(ValueChangedEvent e) {
		return (getEarliestbeginday().equals(e.getSource()))
				|| (getEarliestbegintime().equals(e.getSource()))
				|| (getLatestbeginday().equals(e.getSource()))
				|| (getLatestbegintime().equals(e.getSource()))
				|| (getGzsj().equals(e.getSource()));
	}

	private boolean isWtKey(ValueChangedEvent e) {
		return (getBeginday().equals(e.getSource()))
				|| (getBegintime().equals(e.getSource()))
				|| (getEndday().equals(e.getSource()))
				|| (getEndtime().equals(e.getSource()));
	}

	public BillTempletVO getTempVO() {
		return getListView().getBillListPanel().getBillListData()
				.getBillTempletVO();
	}

	public BillListView getListView() {
		return listView;
	}

	public void valueChanged(ValueChangedEvent event) {
		if (isWtKey(event)) {
			computeTime();
		}
		if (isUpdateEndTime(event)) {
			setFlexEndTime();
		}

		setIshredited(UFBoolean.TRUE);
	}

	public void itemStateChanged(ItemEvent e) {
		if (isWtKey(e)) {
			computeTime();
			this.getWorkdaybeginday().setSelectedIndex(
					this.getEndday().getSelectedIndex());
		}
		if (isUpdateEndTime(e)) {
			setFlexEndTime();
		}

		setIshredited(UFBoolean.TRUE);
		if (e.getSource().equals(getSingleCardTime())) {
			singleCardTypeChange();
		}
	}

	public void SynchroData(AggShiftVO aggvo) {
		if (aggvo == null) {
			return;
		}
		ShiftVO shiftvo = aggvo.getShiftVO();
		if (shiftvo == null)
			return;
		if ((shiftvo.getIscapedited() != null)
				&& (shiftvo.getIscapedited().booleanValue())) {
			return;
		}

		if ((shiftvo.getIsotflexiblefinal().booleanValue())
				|| (getIsotflexible().isSelected())) {
			IRelativeTime begin1 = new DefaultRelativeTime();
			begin1.setDate(shiftvo.getBeginday().intValue());
			begin1.setTime(shiftvo.getBegintime());
			IRelativeTime begin2 = new DefaultRelativeTime();
			begin2.setDate(shiftvo.getLatestbeginday().intValue());
			begin2.setTime(shiftvo.getLatestbegintime());
			IRelativeTime begin = RelativeTimeUtils.getMidTime(begin1, begin2);

			shiftvo.setCapbeginday(Integer.valueOf(begin.getDate()));
			shiftvo.setCapbegintime(begin.getTime());

			IRelativeTime end1 = new DefaultRelativeTime();
			end1.setDate(shiftvo.getEndday().intValue());
			end1.setTime(shiftvo.getEndtime());
			IRelativeTime end2 = new DefaultRelativeTime();
			end2.setDate(shiftvo.getEarliestendday().intValue());
			end2.setTime(shiftvo.getEarliestendtime());
			IRelativeTime end = RelativeTimeUtils.getMidTime(end1, end2);

			shiftvo.setCapendday(Integer.valueOf(end.getDate()));
			shiftvo.setCapendtime(end.getTime());
		} else {
			shiftvo.setCapbeginday(shiftvo.getBeginday());
			shiftvo.setCapbegintime(shiftvo.getBegintime());
			shiftvo.setCapendday(shiftvo.getEndday());
			shiftvo.setCapendtime(shiftvo.getEndtime());
		}

		RTVO[] rtvos = aggvo.getRTVOs();
		if (ArrayUtils.isEmpty(rtvos))
			return;
		CapRTVO[] caprtvos = new CapRTVO[rtvos.length];
		for (int i = 0; i < rtvos.length; i++) {
			RTVO rt = rtvos[i];
			CapRTVO caprt = new CapRTVO();

			if (rt.getIsflexible().booleanValue()) {
				if (rt.getResttime() == null) {
					return;
				}

				if ((StringUtils.isEmpty(rt.getBegintime()))
						|| (StringUtils.isEmpty(rt.getEndtime()))) {
					return;
				}
				IRelativeTime elstStartTime = new DefaultRelativeTime();
				elstStartTime.setDate(rt.getBeginday() == null ? 0 : rt
						.getBeginday().intValue());
				elstStartTime.setTime(rt.getBegintime());

				IRelativeTime ltstEndTime = new DefaultRelativeTime();
				ltstEndTime.setDate(rt.getEndday() == null ? 0 : rt.getEndday()
						.intValue());
				ltstEndTime.setTime(rt.getEndtime());

				IRelativeTime midTime = RelativeTimeUtils.getMidTime(
						elstStartTime, ltstEndTime);

				IRelativeTime startTime = RelativeTimeUtils.minus(midTime, rt
						.getResttime().longValue() / 2L * 60L);

				IRelativeTime endTime = RelativeTimeUtils.add(midTime, rt
						.getResttime().longValue() / 2L * 60L);

				caprt.setBeginday(Integer.valueOf(startTime.getDate()));
				caprt.setBegintime(startTime.getTime());
				caprt.setEndday(Integer.valueOf(endTime.getDate()));
				caprt.setEndtime(endTime.getTime());
				caprt.setCapresttime(rt.getResttime());
			} else {
				caprt.setBeginday(rt.getBeginday());
				caprt.setBegintime(rt.getBegintime());
				caprt.setEndday(rt.getEndday());
				caprt.setEndtime(rt.getEndtime());
				caprt.setCapresttime(rt.getResttime());
			}
			caprt.setPk_shift(rt.getPk_shift());
			caprt.setPk_org(rt.getPk_org());
			caprt.setPk_group(rt.getPk_group());
			caprt.setStatus(2);
			caprt.setTimeid(rt.getTimeid());

			caprtvos[i] = caprt;
		}
		aggvo.setCAPRTVOs(caprtvos);
	}

	public UFBoolean getIshredited() {
		return ishredited;
	}

	public void setIshredited(UFBoolean ishredited) {
		if (getTimeStack().isEmpty()) {
			this.ishredited = ishredited;
		}
	}

	public void clearIshredited() {
		ishredited = UFBoolean.FALSE;
	}

	public Stack<String> getTimeStack() {
		return timeStack;
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
	}

	public UIPanel getSingleCardPanel() {
		if (singleCardPanel == null) {
			singleCardPanel = new UIPanel();
			FormLayout layout = new FormLayout(
					"17dlu, 75dlu, 65dlu, 60dlu, 40dlu, 80dlu, 65dlu, 40dlu, 23dlu",
					"20dlu,default");

			singleCardPanel.setLayout(layout);
		}
		return singleCardPanel;
	}

	public UICheckBox getIsSingleCard() {
		if (isSingleCard == null) {
			isSingleCard = new UICheckBox(ResHelper.getString("hrbd",
					"0hrbd0250"));
			isSingleCard.addActionListener(this);
			shiftCompList.add(isSingleCard);
		}
		return isSingleCard;
	}

	public UIComboBox getSingleCardTime() {
		if (singleCardTime == null) {
			singleCardTime = new UIComboBox();
			singleCardTime.setPreferredSize(new Dimension(63, 20));
			singleCardTime.addItems(getSingleType());
			singleCardTime.addItemListener(this);
			shiftCompList.add(singleCardTime);
		}
		return singleCardTime;
	}

	public DefaultConstEnum[] getSingleType() {
		return new DefaultConstEnum[] {
				new DefaultConstEnum(Integer.valueOf(1), ResHelper.getString(
						"hrbd", "0hrbd0251")),
				new DefaultConstEnum(Integer.valueOf(2), ResHelper.getString(
						"hrbd", "0hrbd0252")),
				new DefaultConstEnum(Integer.valueOf(3), ResHelper.getString(
						"hrbd", "0hrbd0253")) };
	}

	public void setSingleFlexibeEnabel() {
		if (UIState.NOT_EDIT == getModel().getUiState()) {
			return;
		}
		if (getIsotflexible().isSelected()) {
			getIsSingleCard().setEnabled(false);
			getIsrttimeflexible().setEnabled(false);
		} else if (getIsSingleCard().isSelected()) {
			getIsotflexible().setEnabled(false);
			getIsrttimeflexible().setEnabled(false);
		} else if (getIsrttimeflexible().isSelected()) {
			getIsotflexible().setEnabled(false);
			getIsSingleCard().setEnabled(false);
		} else {
			getIsotflexible().setEnabled(true);
			getIsrttimeflexible().setEnabled(true);
			getIsSingleCard().setEnabled(true);
		}

		getSingleCardTime().setEnabled(getIsSingleCard().isSelected());
		if (getIsSingleCard().isSelected()) {
			getLeRulePanel().getIsallowout().setSelected(true);
			getLeRulePanel().getIsallowout().setEnabled(false);

			setRtCheckFlag(Boolean.valueOf(false));
			getBillCardPanel().getBodyItem("checkflag").setEnabled(false);
		} else {
			getLeRulePanel().getIsallowout().setEnabled(true);
			getBillCardPanel().getBodyItem("checkflag").setEnabled(true);
		}
	}

	public void singleCardTypeChange() {
		if (UIState.NOT_EDIT == getModel().getUiState())
			return;
		getLeRulePanel().getLeTimePanel().getAllowearly().setEnabled(true);
		if (getLeRulePanel().getLeTimePanel().getAllowearly().getValueObj() == null) {
			getLeRulePanel().getLeTimePanel().getAllowearly()
					.setValueObj(Integer.valueOf(0));
		}
		getLeRulePanel().getLeTimePanel().getLargeearly().setEnabled(true);
		if (getLeRulePanel().getLeTimePanel().getLargeearly().getValueObj() == null) {
			getLeRulePanel().getLeTimePanel().getLargeearly()
					.setValueObj(Integer.valueOf(0));
		}
		getLeRulePanel().getLeTimePanel().getAllowlate().setEnabled(true);
		if (getLeRulePanel().getLeTimePanel().getAllowlate().getValueObj() == null) {
			getLeRulePanel().getLeTimePanel().getAllowlate()
					.setValueObj(Integer.valueOf(0));
		}
		getLeRulePanel().getLeTimePanel().getLargelate().setEnabled(true);
		if (getLeRulePanel().getLeTimePanel().getLargelate().getValueObj() == null) {
			getLeRulePanel().getLeTimePanel().getLargelate()
					.setValueObj(Integer.valueOf(0));
		}
		if (getSingleCardTime().getSelectdItemValue() == null)
			return;
		if (SingleCardTypeEnum.ONLYBEGIN.toIntValue() == ((Integer) getSingleCardTime()
				.getSelectdItemValue()).intValue()) {
			getLeRulePanel().getLeTimePanel().getAllowearly().setValueObj(null);
			getLeRulePanel().getLeTimePanel().getAllowearly().setEnabled(false);
			getLeRulePanel().getLeTimePanel().getLargeearly().setValueObj(null);
			getLeRulePanel().getLeTimePanel().getLargeearly().setEnabled(false);
		} else if (SingleCardTypeEnum.ONLYEND.toIntValue() == ((Integer) getSingleCardTime()
				.getSelectdItemValue()).intValue()) {
			getLeRulePanel().getLeTimePanel().getAllowlate().setValueObj(null);
			getLeRulePanel().getLeTimePanel().getAllowlate().setEnabled(false);
			getLeRulePanel().getLeTimePanel().getLargelate().setValueObj(null);
			getLeRulePanel().getLeTimePanel().getLargelate().setEnabled(false);
		} else if (SingleCardTypeEnum.ANYTIME.toIntValue() == ((Integer) getSingleCardTime()
				.getSelectdItemValue()).intValue()) {
			getLeRulePanel().getLeTimePanel().getAllowearly().setValueObj(null);
			getLeRulePanel().getLeTimePanel().getAllowearly().setEnabled(false);
			getLeRulePanel().getLeTimePanel().getLargeearly().setValueObj(null);
			getLeRulePanel().getLeTimePanel().getLargeearly().setEnabled(false);
			getLeRulePanel().getLeTimePanel().getAllowlate().setValueObj(null);
			getLeRulePanel().getLeTimePanel().getAllowlate().setEnabled(false);
			getLeRulePanel().getLeTimePanel().getLargelate().setValueObj(null);
			getLeRulePanel().getLeTimePanel().getLargelate().setEnabled(false);
		}
	}

	public LeRulePanel getLeRulePanel() {
		return leRulePanel;
	}

	public void setLeRulePanel(LeRulePanel leRulePanel) {
		this.leRulePanel = leRulePanel;
	}
	/**
	 * Ares.Tank 2018-8-6 11:05:45 
	 * endday 索引转换
	 * endday 枚举含义:-1=前一日，0=当日，1=后一日
	 * endday 下拉索引:0=当日,1=前一日,2=后一日
	 * @param main vo 中的endday枚举
	 * @return 界面中的下拉索引
	 */
	public int getIndexForEndDay(int index){
		switch(index){
		case -1: return 1;
		case 0:return 0;
		case 1: return 2;
			default:return 0;
		}
	}
}
