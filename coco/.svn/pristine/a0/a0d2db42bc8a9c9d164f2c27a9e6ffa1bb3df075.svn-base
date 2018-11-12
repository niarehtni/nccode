package nc.ui.ta.timeitem.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ui.bd.pub.CardAreaPanel;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIMultiLangCombox;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRadioButton;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITextAreaScrollPane;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.textfield.formatter.ParseException;
import nc.ui.pub.beans.util.ColumnLayout;
import nc.ui.ta.timeitem.model.TimeItemAppModel;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.timeitem.TimeItemCopyVO;

import org.apache.commons.collections.CollectionUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * 考勤类别 cardPanel
 * 
 * @author yucheng
 * 
 */
@SuppressWarnings("serial")
public class TimeItemCardPanel extends UIPanel implements ActionListener,
		ItemListener {

	// 编码
	private UIRefPane timeitemcode;

	// 名称
	private UIMultiLangCombox timeitemname;

	// 启用状态
	private UIComboBox enablestate;

	// 类别说明
	private UITextAreaScrollPane timeitemnote;

	// 类别说明
	private UIPanel notePanel;

	// 计算单位
	private UIComboBox timeitemunit;

	// 舍位方式
	private UIComboBox roundmode;

	// 创建人
	private UIRefPane creator;

	// 创建时间
	private UITextField creationtime;

	// 修改人
	private UIRefPane modifier;

	// 修改时间
	private UITextField modifiedtime;

	private String pk_timeitem;
	private String pk_timeitemcopy;
	private String pk_defgroup;
	private String pk_deforg;
	private Integer defenablestate;
	private UFBoolean ispredef;
	private UFBoolean islactation;
	private UFDateTime ts;// 实体部分的ts
	private UFDateTime defTS;// 定义部分的ts
	private TimeItemAppModel model;

	private List<JComponent> componentList = new ArrayList<JComponent>();

	// 列宽定义
	public String colwidth = "right:110px,5px,fill:185px,5px,right:130px,5px,fill:185px,5px,right:130px,5px,fill:185px,5px";

	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {

	}

	@Override
	public void setEnabled(boolean enabled) {
		if (getComponentList() != null) {
			for (JComponent itemComp : getComponentList()) {
				itemComp.setEnabled(enabled);
			}
		}
		getTimeitemcode().getUITextField().setShowMustInputHint(enabled);
		getTimeitemname().setShowMustInputHint(enabled);
	}

	public void setDefDisEnabled() {
		getTimeitemcode().setEnabled(false);
		getTimeitemname().setEnabled(false);
	}

	public void clearData() {
		if (getComponentList() != null) {
			for (JComponent itemComp : getComponentList()) {
				if (itemComp instanceof UITextField)
					((UITextField) itemComp).setText(null);
				else if (itemComp instanceof UIMultiLangCombox)
					((UIMultiLangCombox) itemComp).setMultiLangText(null);
				else if (itemComp instanceof UIComboBox)
					((UIComboBox) itemComp).setSelectedIndex(-1);
				else if (itemComp instanceof UIRefPane) {
					((UIRefPane) itemComp).setPK(null);
					((UIRefPane) itemComp).setValue(null);
				} else if (itemComp instanceof UIRadioButton)
					((UIRadioButton) itemComp).setSelected(false);
				else if (itemComp instanceof UITextAreaScrollPane)
					((UITextAreaScrollPane) itemComp).setText(null);
				else if (itemComp instanceof UICheckBox)
					((UICheckBox) itemComp).setSelected(false);
			}
		}
		getCreator().setPK(null);
		getModifier().setPK(null);
		getCreationtime().setText(null);
		getModifiedtime().setText(null);
	}

	protected void stopEditing() {
		if (CollectionUtils.isEmpty(componentList))
			return;
		for (JComponent comp : componentList) {
			if (!(comp instanceof UITextField))
				continue;
			try {
				((UITextField) comp).stopEditing();
			} catch (ParseException e) {
				((UITextField) comp).setValue(null);
				((UITextField) comp).ShowErrToolTip(e.getMessage());
				Logger.error(e.getMessage(), e);
				throw new RuntimeException(e.getMessage());
			}
		}
	}

	public void buildPanel() {
		UIPanel mainPanel = new UIPanel();
		setLayout(new ColumnLayout());
		add(mainPanel, BorderLayout.CENTER);

		UIPanel leftPanel = new UIPanel();
		leftPanel.setPreferredSize(new Dimension(20, 20));
		add(leftPanel, BorderLayout.WEST);

		FormLayout layout = new FormLayout("default",
				"5px,default,5px,default,5px,default");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout, mainPanel);

		builder.nextLine();
		builder.append(getUpPanel());
		builder.nextLine();
		builder.nextLine();

		builder.append(getAreaPanel());
		builder.nextLine();
		builder.nextLine();

		builder.append(getAuditPanel());
		// builder.nextLine();
	}

	// 档案panel，在子类中实现
	public UIPanel getUpPanel() {
		return null;
	}

	// 审计panel
	private UIPanel auditPanel;

	public UIPanel getAuditPanel() {
		if (auditPanel == null) {
			auditPanel = new UIPanel();
			// FormLayout layout = new FormLayout(
			// "20px,5px,right:85px, 5px, fill:180px, 15px,right:125px, 5px, fill:180px",
			// "");
			FormLayout layout = new FormLayout(
					"20px,10px,left:pref, 2px, fill:175px, "
							+ "15px,left:pref, 2px, fill:175px, "
							+ "15px,left:pref, 2px, fill:175px, "
							+ "15px,left:pref, 2px, fill:175px", "");
			DefaultFormBuilder builder = new DefaultFormBuilder(layout,
					auditPanel);

			builder.append(new UILabel(""));
			builder.append(ResHelper.getString("common", "UC001-0000091")
			/* @res "创建人" */);
			builder.append(getCreator());
			builder.append(ResHelper.getString("common", "UC001-0000092")
			/* @res "创建时间" */);
			builder.append(getCreationtime());
			// builder.nextLine();
			// builder.append(new UILabel(""));
			builder.append(ResHelper.getString("common", "UC001-0000093")
			/* @res "修改人" */);
			builder.append(getModifier());
			builder.append(ResHelper.getString("common", "UC001-0000094")
			/* @res "修改时间" */);
			builder.append(getModifiedtime());
			// builder.nextLine();
		}
		return auditPanel;
	}

	private CardAreaPanel areaPanel;

	public CardAreaPanel getAreaPanel() {
		if (areaPanel == null) {
			areaPanel = new CardAreaPanel(ResHelper.getString("common",
					"UC001-0000095")
			/* @res "审计信息" */, this, false);
			areaPanel.register(getAuditPanel());
			areaPanel.setViewStatus(CardAreaPanel.MAX);
			areaPanel.swithViewStatus();
		}
		return areaPanel;
	}

	public UIRefPane getTimeitemcode() {
		if (timeitemcode == null) {
			timeitemcode = new UIRefPane();
			timeitemcode.setMaxLength(20);
			timeitemcode.setButtonVisible(false);
			getComponentList().add(timeitemcode);
		}
		return timeitemcode;
	}

	public UIMultiLangCombox getTimeitemname() {
		if (timeitemname == null) {
			timeitemname = new UIMultiLangCombox();
			timeitemname.setMaxLength(100);
			getComponentList().add(timeitemname);
		}
		return timeitemname;
	}

	@SuppressWarnings("unchecked")
	public UIComboBox getEnablestate() {
		if (enablestate == null) {
			enablestate = new UIComboBox();
			enablestate.addItem(new DefaultConstEnum(
					IPubEnumConst.ENABLESTATE_INIT, PublicLangRes.NOTENABLE()));
			enablestate.addItem(new DefaultConstEnum(
					IPubEnumConst.ENABLESTATE_ENABLE, PublicLangRes.ENABLED()));
			enablestate
					.addItem(new DefaultConstEnum(
							IPubEnumConst.ENABLESTATE_DISABLE, PublicLangRes
									.DISABLED()));
			enablestate.setEnabled(false);
		}
		return enablestate;
	}

	public UITextAreaScrollPane getTimeitemnote() {
		if (timeitemnote == null) {
			timeitemnote = new UITextAreaScrollPane();
			timeitemnote.getUITextArea().setMaxLength(512);
			timeitemnote.setPreferredSize(new Dimension(400, 50));
			getComponentList().add(timeitemnote);
		}
		return timeitemnote;
	}

	@SuppressWarnings("unchecked")
	public UIComboBox getTimeitemunit() {
		if (timeitemunit == null) {
			timeitemunit = new UIComboBox();
			timeitemunit.addItem(new DefaultConstEnum(
					TimeItemCopyVO.TIMEITEMUNIT_DAY, PublicLangRes.DAY()));
			timeitemunit.addItem(new DefaultConstEnum(
					TimeItemCopyVO.TIMEITEMUNIT_HOUR, PublicLangRes.HOUR()));
			getComponentList().add(timeitemunit);
			timeitemunit.addItemListener(this);
		}
		return timeitemunit;
	}

	@SuppressWarnings("unchecked")
	public UIComboBox getRoundmode() {
		if (roundmode == null) {
			roundmode = new UIComboBox();
			roundmode.addItem(new DefaultConstEnum(TimeItemCopyVO.ROUNDMODE_UP,
					PublicLangRes.ROUNDMODE_UP()));
			roundmode.addItem(new DefaultConstEnum(
					TimeItemCopyVO.ROUNDMODE_DOWN, PublicLangRes
							.ROUNDMODE_DOWN()));
			roundmode
					.addItem(new DefaultConstEnum(TimeItemCopyVO.ROUNDMODE_MID,
							PublicLangRes.ROUNDMODE_MID()));
			getComponentList().add(roundmode);
		}
		return roundmode;
	}

	public UIRefPane getCreator() {
		if (creator == null) {
			creator = new UIRefPane();
			creator.setRefModel(new nc.ui.bd.ref.busi.AllUserDefaultRefModel());
			creator.setEnabled(false);
		}
		return creator;
	}

	public UITextField getCreationtime() {
		if (creationtime == null) {
			creationtime = new UITextField();
			creationtime.setEnabled(false);
		}
		return creationtime;
	}

	public UIRefPane getModifier() {
		if (modifier == null) {
			modifier = new UIRefPane();
			modifier.setRefModel(new nc.ui.bd.ref.busi.AllUserDefaultRefModel());
			modifier.setEnabled(false);
		}
		return modifier;
	}

	public UITextField getModifiedtime() {
		if (modifiedtime == null) {
			modifiedtime = new UITextField();
			modifiedtime.setEnabled(false);
		}
		return modifiedtime;
	}

	public List<JComponent> getComponentList() {
		return componentList;
	}

	public void setComponentList(List<JComponent> componentList) {
		this.componentList = componentList;
	}

	public String getPk_timeitem() {
		return pk_timeitem;
	}

	public void setPk_timeitem(String pk_timeitem) {
		this.pk_timeitem = pk_timeitem;
	}

	public String getPk_timeitemcopy() {
		return pk_timeitemcopy;
	}

	public void setPk_timeitemcopy(String pk_timeitemcopy) {
		this.pk_timeitemcopy = pk_timeitemcopy;
	}

	public String getPk_defgroup() {
		return pk_defgroup;
	}

	public void setPk_defgroup(String pk_defgroup) {
		this.pk_defgroup = pk_defgroup;
	}

	public String getPk_deforg() {
		return pk_deforg;
	}

	public void setPk_deforg(String pk_deforg) {
		this.pk_deforg = pk_deforg;
	}

	public Integer getDefenablestate() {
		return defenablestate;
	}

	public void setDefenablestate(Integer defenablestate) {
		this.defenablestate = defenablestate;
	}

	public UFBoolean getIspredef() {
		return ispredef;
	}

	public void setIspredef(UFBoolean ispredef) {
		this.ispredef = ispredef;
	}

	public UFBoolean getIslactation() {
		return islactation;
	}

	public void setIslactation(UFBoolean islactation) {
		this.islactation = islactation;
	}

	public TimeItemAppModel getModel() {
		return model;
	}

	public void setModel(TimeItemAppModel model) {
		this.model = model;
	}

	public UFDateTime getTs() {
		return ts;
	}

	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	public UFDateTime getDefTS() {
		return defTS;
	}

	public void setDefTS(UFDateTime defTS) {
		this.defTS = defTS;
	}

	public UIPanel getNotePanel() {
		if (notePanel == null) {
			notePanel = new UIPanel();
			FormLayout layout = new FormLayout(colwidth, "");
			DefaultFormBuilder builder = new DefaultFormBuilder(layout,
					notePanel);
			UIPanel nPanel = new UIPanel();
			FormLayout nlayout = new FormLayout(colwidth, "25px,25px");
			DefaultFormBuilder nbuilder = new DefaultFormBuilder(nlayout,
					nPanel);
			nbuilder.append(PublicLangRes.TYPEDESC());
			nbuilder.nextLine();
			builder.append(nPanel);
			builder.append(getTimeitemnote(), 9);
		}
		return notePanel;
	}
}