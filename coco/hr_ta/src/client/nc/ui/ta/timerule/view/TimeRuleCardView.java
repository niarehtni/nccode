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
    // ��һ��ҳǩ��panel
    private KqSetCard kqSetCard = null;

    // �ڶ���ҳǩ��panel�����ݹ���
    private DataRuleCard dataRuleCard = null;

    // ������ҳǩ��panel
    private DefaultRuleCard defaultRuleCard = null;

    // ���ĸ�ҳǩ����ͻ����ҳǩ��panel��
    private BillMutexRulePanel mutexRulePanel = null;
    // �����ҳǩ������ʱ������ҳǩ��panel��
    private TimeDataCalRuleConfigPanel timeDataCalRuleConfigPanel = null;

    // ���빫���ؼ���panel����������
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
     * �õ��������õĿ�Ƭ�������һ�ε������ʼ���ÿ�Ƭ�� 2010��10��20��12:54:14 caiyl
     * 
     * @return JPanel
     */
    private KqSetCard getKqSetCard() {
	if (kqSetCard == null) {
	    try {
		kqSetCard = new KqSetCard();
		kqSetCard.setName(ResHelper.getString("6017basedoc", "06017basedoc1594")
		/* @res "��������" */);
	    } catch (Exception e) {
		Logger.error(e.getMessage(), e);

	    }
	}
	return kqSetCard;
    }

    /**
     * �õ������ؼ���Ƭ
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
     * ���� UIPanel_Card2 ����ֵ��
     * 
     * @return UIPanel
     */
    /* ���棺�˷������������ɡ� */
    private DataRuleCard getDataRuleCard() {

	if (dataRuleCard == null) {
	    try {
		dataRuleCard = new DataRuleCard();
		dataRuleCard.setName(ResHelper.getString("6017basedoc", "06017basedoc1595")
		/* @res "���ݹ���" */);
	    } catch (Exception e) {
		Logger.error(e.getMessage());

	    }
	}
	return dataRuleCard;
    }

    /**
     * ���� "Ĭ�Ϲ���"��Ƭ������ֵ��
     * 
     * @return UIPanel
     */
    /* ���棺�˷������������ɡ� */
    private DefaultRuleCard getDefaultRuleCard() {
	if (defaultRuleCard == null) {
	    try {
		defaultRuleCard = new DefaultRuleCard();
		defaultRuleCard.setSize(200, 600);
		defaultRuleCard.setName(ResHelper.getString("6017basedoc", "06017basedoc1596")
		/* @res "Ĭ�Ϲ���" */);
	    } catch (Exception e) {
		Logger.error(e.getMessage());
	    }
	}
	return defaultRuleCard;
    }

    /**
     * ���� UITabbedPane1 ����ֵ��
     * 
     * @return UITabbedPane
     */
    /* ���棺�˷������������ɡ� */
    private UITabbedPane getUITabbedPane1() {
	if (ivjUITabbedPane1 == null) {
	    try {
		ivjUITabbedPane1 = new UITabbedPane();
		ivjUITabbedPane1.setName("UITabbedPane1");
		ivjUITabbedPane1.setForeground(java.awt.Color.black);
		ivjUITabbedPane1.insertTab(ResHelper.getString("6017basedoc", "06017basedoc1594")
		/* @res "��������" */, null, getKqSetCard(), null, 0);
		ivjUITabbedPane1.insertTab(ResHelper.getString("6017basedoc", "06017basedoc1595")
		/* @res "���ݹ���" */, null, getDataRuleCard(), null, 1);
		ivjUITabbedPane1.insertTab(ResHelper.getString("6017basedoc", "06017basedoc1596")
		/* @res "Ĭ�Ϲ���" */, null, getDefaultRuleCard(), null, 2);
		// getKqSetCard().getOrgShowPanel().setVisible(NODE_TYPE.ORG_NODE==getModel().getContext().getNodeType());
		// һ��С�ʵ������û������tabpane10��ʱ������ͻ����ҳǩ�ͳ���ʱ���������ҳǩ��ʾ��������ʵʩ��Աʹ��
		ivjUITabbedPane1.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 10 && ivjUITabbedPane1.getTabCount() == 3) {
			    ivjUITabbedPane1.insertTab(ResHelper.getString("6017basedoc", "06017basedoc1597")
			    /* @res "���ݳ�ͻ����" */, null, getMutexRulePanel(), null, 3);
			    ivjUITabbedPane1.insertTab(ResHelper.getString("6017basedoc", "06017basedoc1598")
			    /* @res "����ʱ���������" */, null, getTimeDataCalRuleConfigPanel(), null, 4);
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
     * ��ʼ���ࡣ
     */
    /* ���棺�˷������������ɡ� */
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
	// ������õȰ�ťʱ����
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
     * ȡ��ҳ����Ϣ �������ʵ��IEditor����Ϊ�����е�����õ�ǰ��ʱ�õ�IEditor������getValue()�������쳣��
     * ������������û���дһ����Ϣ��������Ϣ��Ĳ��Ϲ淶����������꽹��û���ƿ�������µ������
     * ��ʱ�ᱨParseException���󣬵�����쳣�������ϼ����׳���Ҳ����ֱ�ӵ����Ի�����ʾ��
     * ��Ϊ���㵯�������ĳ���Ҫ���У�����ֻ�ܴ�ӡ�����������ĺ�����������������£�ϵͳ�Ὣ������������ٱ���
     */
    @Override
    public Object getValue() {

	TimeRuleVO vo = new TimeRuleVO();

	try {
	    String pk_org = getModel().getContext().getPk_org();
	    String pk_group = getModel().getContext().getPk_group();
	    // ��ȡ������������
	    vo = getKqSetCard().getValue(vo);
	    // ��ȡ���ݹ�������
	    vo = getDataRuleCard().getValue(vo);
	    // ��ȡĬ�Ϲ�������
	    vo = getDefaultRuleCard().getValue(vo);
	    // ��ȡ���ݳ�ͻ����
	    vo.setBillmutexrule(getMutexRulePanel().getBillMutexRuleStr());
	    // ��ȡ����ʱ������
	    vo.setWorklenminusitems(getTimeDataCalRuleConfigPanel().getConfigStr());
	    // ��ȡ��������
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
	    // ��ȡ������������
	    getKqSetCard().setValue(vo);
	    // ��ȡ���ݹ�������
	    getDataRuleCard().setValue(vo);
	    // ��ȡĬ�Ϲ�������
	    getDefaultRuleCard().setValue(vo);
	    // ��ͻ����
	    getMutexRulePanel().setBillMutexRuleStr(vo.getBillmutexrule());
	    // ���ڼ������
	    getTimeDataCalRuleConfigPanel().setConfigStr(vo.getWorklenminusitems());
	    getPubPanel().setValue(vo);
	}
    }

    /**
     * �����пؼ���ֵ���
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