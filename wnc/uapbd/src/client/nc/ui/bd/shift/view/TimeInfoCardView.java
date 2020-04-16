package nc.ui.bd.shift.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.swing.JComponent;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ui.bd.pub.CardAreaPanel;
import nc.ui.pub.beans.UIMultiLangCombox;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.textfield.formatter.ParseException;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.uif2.editor.BillListView;
import nc.ui.uif2.editor.IEditor;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;

import org.apache.commons.collections.CollectionUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * 班次的考勤信息页签
 * 
 * @author wangdca
 * 
 */
public class TimeInfoCardView extends UIPanel implements IEditor {

	/**
	 *
	 */
	private static final long serialVersionUID = 1353673878611994485L;

	private AbstractAppModel model;

	// 单据模板的listview，在配置文件中配置
	private BillListView listView;

	// 所有控件班次空间,这里不包括panel
	private List<JComponent> shiftCompList;
	private List<JComponent> mustInputList; // 需要必输的组件
	// 作息时间规则的title--分组panel
	private CardAreaPanel wrTRTitlePanel;
	// 作息时间规则panel
	private WrTimeRulePanel wrTimeRulePanel;

	// 迟到早退规则panel--分组panel
	private CardAreaPanel leRuleTitlePanel;
	// 迟到早退规则panel
	private LeRulePanel leRulePanel;
	// 加班规则title的panel--分组panel
	private CardAreaPanel overTitlePanel;
	// 加班规则panel
	private OverPanel overPanel;

	// ssx added on 2019-12-24
	private CardAreaPanel annLeaveTitlePanel;
	private AnnLeavePanel annLeavePanel;
	// end

	// formlayout画笔
	private CellConstraints cc;

	// 系统触发事件标志堆栈 在系统触发事件的地方push（例如setvalue时，clearData时等），若为空则说明事件的触发是由用户编辑造成的
	private Stack<String> timeStack;

	public TimeInfoCardView(List<JComponent> shiftCompList, AbstractAppModel model, List<JComponent> mustInputList,
			CellConstraints cc, BillListView listView) {
		this.shiftCompList = shiftCompList;
		this.mustInputList = mustInputList;
		this.model = model;
		this.cc = cc;
		this.listView = listView;
	}

	public void initComponents() {
		if (shiftCompList == null) {
			shiftCompList = new ArrayList<JComponent>();
		}
		if (mustInputList == null) {
			mustInputList = new ArrayList<JComponent>();
		}
		{
			// 整体页面布局
			// 用ColumnLayout的原因：对于分组panel的收缩与扩展可以用panel.setVisible(boolean)来实现
			// 不需要其他的设置;

			UIPanel mainPanel = new UIPanel();
			// mainPanel.setLayout(new ColumnLayout());
			FormLayout layout = new FormLayout("pref", "");
			DefaultFormBuilder builder = new DefaultFormBuilder(layout, mainPanel);

			// this.setLayout(new ColumnLayout());
			// this.add(getBaseTitlePanel());
			// this.add(getBaseInfoPanel());

			builder.append(getWrTRTitlePanel());
			builder.nextLine();
			builder.append(getWrTimeRulePanel());
			builder.nextLine();
			builder.append(getLeRuleTitlePanel());
			builder.nextLine();
			builder.append(getLeRulePanel());
			builder.nextLine();
			builder.append(getOverTitlePanel());
			builder.nextLine();
			builder.append(getOverPanel());

			// ssx added on 2019-12-24
			builder.nextLine();
			builder.append(getAnnLeaveTitlePanel());
			builder.nextLine();
			builder.append(getAnnLeavePanel());
			// end

			// //======== 第三行：作息时间规则 ========
			// mainPanel.add(getWrTRTitlePanel()/*, cc.xy(1, 3)*/);
			// {
			// //======== 刷卡时间、工作时间panel:workPanel ========
			//
			// }
			// //注册至分组panel中
			getWrTRTitlePanel().register(getWrTimeRulePanel());
			// //======== 第五行：作息时间规则panelpanel：WrTimeRulePanel ========
			// mainPanel.add(getWrTimeRulePanel());
			//
			// //======== 第五行：迟到早退title分组panel：LeRuleTitlePanel ========
			// mainPanel.add(getLeRuleTitlePanel());
			//
			// //======== 第六行：迟到早退panel：leRulePanel ========
			getLeRuleTitlePanel().register(getLeRulePanel());
			// mainPanel.add(getLeRulePanel());
			//
			// //======== 加班规则title ========
			// mainPanel.add(getOverTitlePanel());
			getOverTitlePanel().register(getOverPanel());
			// //======== 加班规则panel ========
			// mainPanel.add(getOverPanel());

			// ssx added on 2019-12-24
			getAnnLeaveTitlePanel().register(getAnnLeavePanel());
			// end

			setLayout(new BorderLayout());
			add(mainPanel, BorderLayout.CENTER);
			UIPanel leftPanel = new UIPanel();
			leftPanel.setPreferredSize(new Dimension(70, 50));
			UIPanel upPanel = new UIPanel();
			upPanel.setPreferredSize(new Dimension(100, 10));
			add(leftPanel, BorderLayout.WEST);
			add(upPanel, BorderLayout.NORTH);

		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (shiftCompList != null) {
			for (Iterator<JComponent> iterator = shiftCompList.iterator(); iterator.hasNext();) {
				JComponent shiftComp = iterator.next();
				if (shiftComp instanceof BillScrollPane) {
					BillScrollPane bsp = (BillScrollPane) shiftComp;
					bsp.getTableModel().setEnabled(enabled);
					continue;
				}
				shiftComp.setEnabled(enabled);
			}
		}

		getWrTimeRulePanel().setEnabled(enabled);
		if (enabled) {
			getWrTimeRulePanel().setIsotflexibleEnable();
			getWrTimeRulePanel().setIsrttimeflexibleEnable();
			getWrTimeRulePanel().setIncludenightshiftEnable();

			getWrTimeRulePanel().setSingleFlexibeEnabel();
			// 加班规则
			getOverPanel().setEnabled();
			// 是否自动统计旷工工时
			getLeRulePanel().setEnabled();

			// ssx added on 2019-12-24
			getAnnLeavePanel().setEnabled();
			// end
		} else {
			// 如果不可编辑，则要根据上班时间是否弹性来决定是否显示最晚上班、最早下班时间
			// getWrTimeRulePanel().setIsotFlexibleText();
			// 如果是浏览态，则不显示所有控件的必输符号
			if (CollectionUtils.isEmpty(shiftCompList))
				return;
			// 如果是非空，则要加入非空符号
			for (JComponent comp : shiftCompList) {
				if (comp instanceof UIRefPane) {
					((UIRefPane) comp).getUITextField().setShowMustInputHint(enabled);
				}
			}
		}
		if (CollectionUtils.isEmpty(mustInputList))
			return;
		// 如果是非空，则要加入非空符号
		for (JComponent comp : mustInputList) {
			if (comp instanceof UIRefPane) {
				((UIRefPane) comp).getUITextField().setShowMustInputHint(enabled);
			}
			if (comp instanceof UIMultiLangCombox) {
				((UIMultiLangCombox) comp).setShowMustInputHint(enabled);
			}
		}
	}

	/**
	 * 强制鼠标焦点移开
	 */
	private void stopEditing() throws ParseException {
		if (CollectionUtils.isEmpty(shiftCompList))
			return;
		for (JComponent comp : shiftCompList) {
			if (!(comp instanceof UITextField))
				continue;
			try {
				((UITextField) comp).stopEditing();
			} catch (ParseException e) {
				((UITextField) comp).setValue(null);
				((UITextField) comp).ShowErrToolTip(e.getMessage());
				Logger.error(e.getMessage(), e);
			}
		}
		// 单独设置是因为工休列表是billCardPanel(不再)，需要单独再设置
		getWrTimeRulePanel().stopEditing();
	}

	public WrTimeRulePanel getWrTimeRulePanel() {
		if (wrTimeRulePanel == null) {
			wrTimeRulePanel = new WrTimeRulePanel(shiftCompList, mustInputList, listView, getTimeStack());
			wrTimeRulePanel.initComponents(cc);
			wrTimeRulePanel.setLeRulePanel(getLeRulePanel());
		}
		return wrTimeRulePanel;
	}

	public DefaultConstEnum[] getEnableStateEnums() {
		return new DefaultConstEnum[] { new DefaultConstEnum(2, ResHelper.getString("hrbd", "0hrbd0151")
		/* @res "已启用" */), new DefaultConstEnum(1, ResHelper.getString("hrbd", "0hrbd0152")
		/* @res "未启用" */), new DefaultConstEnum(3, ResHelper.getString("hrbd", "0hrbd0148")
		/* @res "已停用" */) };
	}

	public CardAreaPanel getWrTRTitlePanel() {
		if (wrTRTitlePanel == null) {
			wrTRTitlePanel = new CardAreaPanel(ResHelper.getString("hrbd", "0hrbd0186")
			/* @res "作息时间规则" */, new FormLayout("600"), this, true);
		}
		return wrTRTitlePanel;
	}

	public CardAreaPanel getLeRuleTitlePanel() {
		if (leRuleTitlePanel == null) {
			leRuleTitlePanel = new CardAreaPanel(ResHelper.getString("hrbd", "0hrbd0187")
			/* @res "迟到早退规则" */, new FormLayout("600"), this, true);
		}
		return leRuleTitlePanel;
	}

	public LeRulePanel getLeRulePanel() {
		if (leRulePanel == null) {
			leRulePanel = new LeRulePanel(shiftCompList, mustInputList, cc, getTimeStack());
			leRulePanel.initComponents(cc);
		}
		return leRulePanel;
	}

	public CardAreaPanel getOverTitlePanel() {
		if (overTitlePanel == null) {
			overTitlePanel = new CardAreaPanel(ResHelper.getString("hrbd", "0hrbd0188")
			/* @res "加班规则" */, new FormLayout("600"), this, true);
		}
		return overTitlePanel;
	}

	public OverPanel getOverPanel() {
		if (overPanel == null) {
			overPanel = new OverPanel(shiftCompList, getTimeStack());
			overPanel.initComponents(cc);
		}
		return overPanel;
	}

	// ssx added on 2019-12-24
	public CardAreaPanel getAnnLeaveTitlePanel() {
		if (annLeaveTitlePanel == null) {
			annLeaveTitlePanel = new CardAreaPanel("年度补休规则", new FormLayout("600"), this, true);
		}
		return annLeaveTitlePanel;
	}

	public AnnLeavePanel getAnnLeavePanel() {
		if (annLeavePanel == null) {
			annLeavePanel = new AnnLeavePanel(shiftCompList, getTimeStack());
			annLeavePanel.initComponents(cc);
		}
		return annLeavePanel;
	}

	// end

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
	}

	public BillListView getListView() {
		return listView;
	}

	public void setListView(BillListView listView) {
		this.listView = listView;
	}

	public AggShiftVO getValue(AggShiftVO aggVo) {
		try {
			stopEditing(); // 鼠标焦点移开后，计算相应信息后，再取值
		} catch (ParseException e) {
			Logger.error(e.getMessage(), e);
		}
		ShiftVO mainVo = aggVo.getShiftVO();
		if (getModel() != null) {
			mainVo.setPk_group(getModel().getContext().getPk_group());
		}

		// 作息时间规则
		getWrTimeRulePanel().getValue(aggVo);
		// 迟到早退规则
		getLeRulePanel().getValue(mainVo);
		// 加班规则
		getOverPanel().getValue(mainVo);

		// ssx added on 2019-12-24
		getAnnLeavePanel().getValue(mainVo);
		// end
		return aggVo;
	}

	// 新增时清除原页面中用户是否编辑过的标志的数据
	public void clearHrEditData() {
		getWrTimeRulePanel().clearIshredited();
		getLeRulePanel().getLeTimePanel().clearIshredited();
		getLeRulePanel().getHdlWhPanel().clearIshredited();
		getLeRulePanel().initIshredited();
		getOverPanel().clearIshredited();

		// ssx added on 2019-12-24
		getAnnLeavePanel().clearIshredited();
		// end
	}

	@Override
	public void setValue(Object object) {

		if (object == null) {
			return;
		}
		if (!(object instanceof AggShiftVO)) {
			return;
		}

		AggShiftVO aggVo = (AggShiftVO) object;
		ShiftVO mainVo = (ShiftVO) aggVo.getParentVO();
		// 作息时间
		getWrTimeRulePanel().setValue(aggVo);
		// ---- 迟到早退规则----
		getLeRulePanel().setValue(mainVo);
		// ----加班规则
		getOverPanel().setValue(mainVo);

		// ssx added on 2019-12-24
		getAnnLeavePanel().setValue(mainVo);
		// end
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTimeStack(Stack<String> timeStack) {
		this.timeStack = timeStack;
	}

	public Stack<String> getTimeStack() {
		if (timeStack == null)
			timeStack = new Stack<String>();
		return timeStack;
	}
}