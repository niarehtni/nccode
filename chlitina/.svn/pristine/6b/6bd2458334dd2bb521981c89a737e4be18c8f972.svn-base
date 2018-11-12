package nc.ui.ta.timerule.view;

import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.UITabbedPane;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.IActionContributor;
import nc.ui.uif2.actions.IActionContributorListener;
import nc.ui.uif2.editor.IEditor;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.logging.Debug;
import nc.vo.ta.timerule.TimeRuleVO;

public class TimeRuleCardView extends UIPanel implements AppEventListener, IEditor, IActionContributor {

    /**
	 *
	 */
    private static final long serialVersionUID = -418428506048201483L;

    private UITabbedPane ivjUITabbedPane1 = null;
    // 第一个页签的panel
    private KqSetCard kqSetCard = null;

    // 第二个页签的panel：数据规则
    private DataRuleCard dataRuleCard = null;

    // 第三个页签的panel
    private DefaultRuleCard defaultRuleCard = null;

    // 第四个页签（冲突规则页签的panel）
    private BillMutexRulePanel mutexRulePanel = null;
    // 第五个页签（出勤时长设置页签的panel）
    private TimeDataCalRuleConfigPanel timeDataCalRuleConfigPanel = null;

    // 放入公共控件的panel：如主键等
    private TimeRulePubPanel pubPanel;

    private AbstractUIAppModel model;

    public TimeRuleCardView() {
	// TODO Auto-generated constructor stub
    }

    public TimeRuleCardView(LayoutManager p0) {
	super(p0);
	// TODO Auto-generated constructor stub
    }

    public TimeRuleCardView(boolean p0) {
	super(p0);
	// TODO Auto-generated constructor stub
    }

    public TimeRuleCardView(LayoutManager p0, boolean p1) {
	super(p0, p1);
	// TODO Auto-generated constructor stub
    }

    /**
     * 得到考勤设置的卡片，如果第一次调用则初始化该卡片。 2010年10月20日12:54:14 caiyl
     * 
     * @return JPanel
     */
    private KqSetCard getKqSetCard() {
	if (kqSetCard == null) {
	    try {
		kqSetCard = new KqSetCard();
		kqSetCard.setName(ResHelper.getString("6017basedoc", "06017basedoc1594")
		/* @res "考勤设置" */);
	    } catch (Exception e) {
		Logger.error(e.getMessage(), e);

	    }
	}
	return kqSetCard;
    }

    /**
     * 得到公共控件卡片
     * 
     * @return
     */
    private TimeRulePubPanel getPubPanel() {
	if (pubPanel == null) {
	    pubPanel = new TimeRulePubPanel();
	}
	return pubPanel;
    }

    /**
     * 返回 UIPanel_Card2 特性值。
     * 
     * @return UIPanel
     */
    /* 警告：此方法将重新生成。 */
    private DataRuleCard getDataRuleCard() {

	if (dataRuleCard == null) {
	    try {
		dataRuleCard = new DataRuleCard();
		dataRuleCard.setName(ResHelper.getString("6017basedoc", "06017basedoc1595")
		/* @res "数据规则" */);
	    } catch (Exception e) {
		Logger.error(e.getMessage());

	    }
	}
	return dataRuleCard;
    }

    /**
     * 返回 "默认规则"卡片的特性值。
     * 
     * @return UIPanel
     */
    /* 警告：此方法将重新生成。 */
    private DefaultRuleCard getDefaultRuleCard() {
	if (defaultRuleCard == null) {
	    try {
		defaultRuleCard = new DefaultRuleCard();
		defaultRuleCard.setSize(200, 600);
		defaultRuleCard.setName(ResHelper.getString("6017basedoc", "06017basedoc1596")
		/* @res "默认规则" */);
	    } catch (Exception e) {
		Logger.error(e.getMessage());
	    }
	}
	return defaultRuleCard;
    }

    /**
     * 返回 UITabbedPane1 特性值。
     * 
     * @return UITabbedPane
     */
    /* 警告：此方法将重新生成。 */
    private UITabbedPane getUITabbedPane1() {
	if (ivjUITabbedPane1 == null) {
	    try {
		ivjUITabbedPane1 = new UITabbedPane();
		ivjUITabbedPane1.setName("UITabbedPane1");
		ivjUITabbedPane1.setForeground(java.awt.Color.black);
		ivjUITabbedPane1.insertTab(ResHelper.getString("6017basedoc", "06017basedoc1594")
		/* @res "考勤设置" */, null, getKqSetCard(), null, 0);
		ivjUITabbedPane1.insertTab(ResHelper.getString("6017basedoc", "06017basedoc1595")
		/* @res "数据规则" */, null, getDataRuleCard(), null, 1);
		ivjUITabbedPane1.insertTab(ResHelper.getString("6017basedoc", "06017basedoc1596")
		/* @res "默认规则" */, null, getDefaultRuleCard(), null, 2);
		// getKqSetCard().getOrgShowPanel().setVisible(NODE_TYPE.ORG_NODE==getModel().getContext().getNodeType());
		// 一个小彩蛋：当用户鼠标点击tabpane10次时，将冲突规则页签和出勤时长计算规则页签显示出来，供实施人员使用
		ivjUITabbedPane1.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 10 && ivjUITabbedPane1.getTabCount() == 3) {
			    ivjUITabbedPane1.insertTab(ResHelper.getString("6017basedoc", "06017basedoc1597")
			    /* @res "单据冲突规则" */, null, getMutexRulePanel(), null, 3);
			    ivjUITabbedPane1.insertTab(ResHelper.getString("6017basedoc", "06017basedoc1598")
			    /* @res "出勤时长计算规则" */, null, getTimeDataCalRuleConfigPanel(), null, 4);
			    return;
			}
		    }
		});
		// user code end
	    } catch (Exception e) {
		// user code begin {2}
		// user code end
		Logger.error(e.getMessage());
	    }
	}
	return ivjUITabbedPane1;
    }

    /**
     * 初始化类。
     */
    /* 警告：此方法将重新生成。 */
    private void initialize() {
	try {
	    setBorder(null);
	    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	    setSize(500, 460);
	    UIScrollPane scrollPane = new UIScrollPane();
	    scrollPane.setViewportView(getUITabbedPane1());
	    add(scrollPane);
	    // getUIChkbovertmrule().addItemListener(this);
	} catch (Exception e) {
	    Logger.error(e.getMessage());
	}
    }

    public void initUI() {
	initialize();
    }

    public BillMutexRulePanel getMutexRulePanel() {
	if (mutexRulePanel == null) {
	    mutexRulePanel = new BillMutexRulePanel();
	    mutexRulePanel.setName("mutexRulePanel");
	    mutexRulePanel.setLayout(null);
	}
	return mutexRulePanel;
    }

    public void setModel(AbstractUIAppModel model) {
	this.model = model;
	model.addAppEventListener(this);
    }

    public AbstractUIAppModel getModel() {
	return model;
    }

    @Override
    public void handleEvent(AppEvent event) {
	if ((AppEventConst.MODEL_INITIALIZED == event.getType())) {
	    onNotEdit();
	    synchronizeDataFromModel();
	}
	// 点击设置等按钮时调用
	else if (AppEventConst.UISTATE_CHANGED == event.getType()) {
	    if (model.getUiState() == UIState.EDIT) {
		onEdit();
	    } else {
		onNotEdit();
		synchronizeDataFromModel();
	    }
	}

    }

    protected void synchronizeDataFromModel() {
	Logger.debug("entering synchronizeDataFromModel");
	Object selectedData = model.getSelectedData();
	setValue(selectedData);
	Logger.debug("leaving synchronizeDataFromModel");
    }

    /**
     * 取得页面信息 这里必须实现IEditor，因为后面有的类调用当前类时用的IEditor，所以getValue()不能抛异常，
     * 这样导致如果用户填写一项信息，这项信息填的不合规范，并且在鼠标焦点没有移开的情况下点击保存
     * 这时会报ParseException错误，但这个异常不能向上继续抛出，也不能直接弹出对话框提示，
     * 因为就算弹出后后面的程序还要运行，所以只能打印。这样带来的后果就是在上述操作下，系统会将错误数据清空再保存
     */
    @Override
    public Object getValue() {

	TimeRuleVO vo = new TimeRuleVO();

	try {
	    String pk_org = getModel().getContext().getPk_org();
	    String pk_group = getModel().getContext().getPk_group();
	    // 获取考勤设置数据
	    vo = getKqSetCard().getValue(vo);
	    // 获取数据规则数据
	    vo = getDataRuleCard().getValue(vo);
	    // 获取默认规则数据
	    vo = getDefaultRuleCard().getValue(vo);
	    // 获取单据冲突规则
	    vo.setBillmutexrule(getMutexRulePanel().getBillMutexRuleStr());
	    // 获取出勤时长减项
	    vo.setWorklenminusitems(getTimeDataCalRuleConfigPanel().getConfigStr());
	    // 获取公共数据
	    vo = getPubPanel().getValue(vo, pk_org, pk_group);

	} catch (Exception e) {
	    Debug.error(e.getMessage(), e);
	}

	return vo;
    }

    public void setEditable(boolean editable) {
	getKqSetCard().setEnabled(editable);
	getKqSetCard().setTWUIState(getModel().getContext().getPk_org());
	getDataRuleCard().setEnabled(editable);
	getDefaultRuleCard().setEnabled(editable);
	getMutexRulePanel().setEditable(editable);
	getTimeDataCalRuleConfigPanel().setEditable(editable);

    }

    protected void onNotEdit() {
	setEditable(false);
    }

    protected void onEdit() {
	setOrgAndGroup();
	setEditable(true);
    }

    private void setOrgAndGroup() {

    }

    @Override
    public void setValue(Object object) {
	if (object == null)
	    clearViewValue();
	else {
	    TimeRuleVO vo = (TimeRuleVO) object;
	    // 获取考勤设置数据
	    getKqSetCard().setValue(vo);
	    // 获取数据规则数据
	    getDataRuleCard().setValue(vo);
	    // 获取默认规则数据
	    getDefaultRuleCard().setValue(vo);
	    // 冲突规则
	    getMutexRulePanel().setBillMutexRuleStr(vo.getBillmutexrule());
	    // 出勤计算减项
	    getTimeDataCalRuleConfigPanel().setConfigStr(vo.getWorklenminusitems());
	    getPubPanel().setValue(vo);
	}
    }

    /**
     * 将所有控件的值清空
     */
    private void clearViewValue() {
	kqSetCard.clearViewValue();
	dataRuleCard.clearViewValue();
	defaultRuleCard.clearViewValue();
    }

    @Override
    public void addActionContributorListener(IActionContributorListener l) {
	// TODO Auto-generated method stub

    }

    @Override
    public boolean isActived() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void removeActionContributorListener(IActionContributorListener l) {
	// TODO Auto-generated method stub

    }

    @Override
    public void setActived(boolean actived) {
	// TODO Auto-generated method stub

    }

    @Override
    public void addAction(Action action) {
	// TODO Auto-generated method stub

    }

    @Override
    public List<Action> getActions() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public JComponent getContainerUI() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void removeAction(Action action) {
	// TODO Auto-generated method stub

    }

    @Override
    public void setActions(List<Action> actions) {
	// TODO Auto-generated method stub

    }

    public TimeDataCalRuleConfigPanel getTimeDataCalRuleConfigPanel() {
	if (timeDataCalRuleConfigPanel == null) {
	    timeDataCalRuleConfigPanel = new TimeDataCalRuleConfigPanel();
	    timeDataCalRuleConfigPanel.init();
	    timeDataCalRuleConfigPanel.setEditable(false);
	}
	return timeDataCalRuleConfigPanel;
    }
}