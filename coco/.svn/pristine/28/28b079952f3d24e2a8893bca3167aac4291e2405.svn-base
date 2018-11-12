package nc.ui.ta.psndocwadoc.view.labourjoin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.bd.defdoc.IDefdoclistQryService;
import nc.pubitf.twhr.IRangetablePubQuery;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.ta.pub.BUPanel;
import nc.ui.ta.pub.selpsn.ConditionSelPsnPanel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.IAppModel;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("restriction")
public class JoinSetPanelForQuitJoin extends UIPanel implements ValueChangedListener, AppEventListener, ActionListener {

    private static final long serialVersionUID = 749514391008736111L;

    IAppModel model = null;

    ConditionSelPsnPanel selPsnPanel;
    UIPanel QuitPanel;
    UIRefPane dateRefPane;
    UIComboBox yiDongRefPane;
    UICheckBox baoCheckBox;
    UICheckBox tuiCheckBox;
    UICheckBox jianCheckBox;
    private BUPanel buPanel = null;
    private UIPanel topPanel = null;
    private String pk_org;
    IDefdoclistQryService defService = null;
    IRangetablePubQuery rangeService = null;
    UFLiteralDate newDate = new UFLiteralDate();

    public JoinSetPanelForQuitJoin() {
    }

    public void init() {
	// 显示两个radiobutton的panel
	UIPanel topSubPanel1 = new UIPanel();
	topSubPanel1.setLayout(new FlowLayout());
	topSubPanel1.add(getQuitPanel());
	topSubPanel1.setSize(new Dimension(400, 300));
	topPanel = new UIPanel();
	topPanel.setLayout(new BorderLayout());
	topPanel.add(topSubPanel1, BorderLayout.NORTH);
	setLayout(new BorderLayout());
	add(topPanel, BorderLayout.NORTH);
    }

    protected ConditionSelPsnPanel getSelPsnPanel() {
	if (selPsnPanel == null) {
	    selPsnPanel = new ConditionSelPsnPanel();
	    selPsnPanel.setModel((AbstractUIAppModel) getModel());
	    selPsnPanel.init();
	}
	return selPsnPanel;
    }

    public BUPanel getBuPanel() {
	if (buPanel == null) {
	    buPanel = new BUPanel();
	    buPanel.setModel((AbstractUIAppModel) getModel());
	    buPanel.init();
	    buPanel.getBuRef().addValueChangedListener(this);
	}
	return buPanel;
    }

    public IAppModel getModel() {
	return model;
    }

    public void setModel(IAppModel model) {
	this.model = model;
	model.addAppEventListener(this);
    }

    @Override
    public void handleEvent(AppEvent event) {

    }

    public UIPanel getQuitPanel() {
	if (QuitPanel == null) {
	    QuitPanel = new UIPanel();
	    FormLayout layout = new FormLayout("left:50px, 10px, fill:100px, 15px,left:100px, 10px, fill:100px, 15px,"
		    + "left:100px, 10px, fill:100px, 15px,left:100px, 10px, fill:100px, 15px",
		    "50px, 50px, 50px, 50px, 50px");
	    DefaultFormBuilder builder = new DefaultFormBuilder(layout, QuitPanel);
	    builder.append("结束日期");
	    builder.append(getRefBeginDate());
	    builder.nextLine();

	    builder.append(getBaoCheckBox());
	    builder.append("劳保");
	    builder.nextLine();

	    builder.append(getJianCheckBox());
	    builder.append("健保");
	    builder.nextLine();
	    //
	    builder.append(getTuiCheckBox());
	    builder.append("劳退");
	    builder.nextLine();

	    builder.append("异动类型");
	    builder.append(getYiDongRefPane());
	}
	return QuitPanel;
    }

    public UICheckBox getBaoCheckBox() {
	if (baoCheckBox == null) {
	    baoCheckBox = new UICheckBox();
	    baoCheckBox.setSelected(false);
	    baoCheckBox.addActionListener(this);
	}
	return baoCheckBox;
    }

    public UICheckBox getTuiCheckBox() {
	if (tuiCheckBox == null) {
	    tuiCheckBox = new UICheckBox();
	    tuiCheckBox.setSelected(false);
	    tuiCheckBox.addActionListener(this);
	}
	return tuiCheckBox;
    }

    public UICheckBox getJianCheckBox() {
	if (jianCheckBox == null) {
	    jianCheckBox = new UICheckBox();
	    jianCheckBox.setSelected(false);
	    jianCheckBox.addActionListener(this);
	}
	return jianCheckBox;
    }

    public UIComboBox getYiDongRefPane() {
	if (yiDongRefPane == null) {
	    yiDongRefPane = new UIComboBox();
	    yiDongRefPane.addItem("退保");
	}
	return yiDongRefPane;
    }

    protected UIRefPane getRefBeginDate() {
	if (dateRefPane == null) {
	    dateRefPane = new UIRefPane();
	    dateRefPane.setName("refBeginDate");
	    dateRefPane.setRefNodeName("日期");
	    dateRefPane.getUITextField().setFormatShow(true);
	    dateRefPane.setValueObj(getBusDate());
	}
	return dateRefPane;
    }

    private UFLiteralDate getBusDate() {
	UFDate busDate = WorkbenchEnvironment.getInstance().getBusiDate();
	return UFLiteralDate.getDate(busDate.toString().substring(0, 10));
    }

    public String getBeginDatePk() {
	return getRefBeginDate().getRefPK();
    }

    public void setPK_BU(String pk_org) {
	UIRefPane refPane = (UIRefPane) getRefBeginDate();
	this.pk_org = pk_org;
    }

    public String getPK_org() {
	return pk_org;
    }

    @Override
    public void valueChanged(ValueChangedEvent event) {
	String pk_org = getBuPanel().getPK_BU();
	if (StringUtils.isEmpty(pk_org))
	    return;
	getRefBeginDate().getRefModel().setPk_org(pk_org);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {

    }
}