package nc.ui.hr.tools.rtf.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hr.tools.rtf.Word;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.hr.tools.rtf.jacob.WordUtil;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIList;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRadioButton;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.UISpinBox;
import nc.ui.pub.beans.UISplitPane;
import nc.ui.pub.beans.UITabbedPane;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.UITree;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.hr.tools.rtf.TempletfieldVO;
import nc.vo.hr.tools.rtf.WordSetVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

/**
 * 信息项目对话框panel做成
 * @author sunxj
 * @version 2009-12-29
 */
public class ItemSetPnl extends UIPanel implements ChangeListener
{
    private WordSetActionListener actionListener = new WordSetActionListener(this);
    
    private UIPanel afterContentPanel;
    private UIPanel beforeContentPanel;
    private UIButton btnSetCondition;
    private UIPanel buttonPanel = null;// 按钮panel
    private UIButton cancelButton = null;
    private UIComboBox combobobx_dateformat = null;
    private UIComboBox combobox_order = null;
    private Map conditionMap;
    private LoginContext context;
    private ListNode curVar;
    // 日期格式
    private UIPanel dataFormatPanel;
    private UIPanel dictPanel = null;// 信息项Panel
    // 扩充文本
    private UIPanel extendContentPanel;
    // 信息项定位
    private UIPanel infoItemLocationPanel;
    private WordSetItemListener itemListener = new WordSetItemListener(this);
    private WordSetVO itemSetVO;
    private WordSetKeyListener keyListener = new WordSetKeyListener(this);
    private UILabel label_order = null;
    private UILabel labelAft;
    private UILabel labelPre;
    private UILabel lblDesc;
    private UILabel lblName;
    private UILabel lblTypeLocate;
    private WordSetListSelectionListener listSelectionListener = new WordSetListSelectionListener(this);
    private UIList listSysItem = null;
    private UIScrollPane lScrollPane = null;// 信息集
    // 监听
    private WordSetMouseListener mouseListener = new WordSetMouseListener(this);
    private UIButton okButton = null;
    // 字段选择
    private UIPanel panel = null;
    // 条件定位
    private UIPanel panel_condition = null;
    private UIPanel panel_order;// 序号定位
    private UIPanel panelOrientation;// 信息定位
    private UIPanel panelROrientation;
    // 高级设置
    private UIPanel pnlAdvSet;
    private UIPanel pnlContent;// 设置panel
    // 子集取数条件
    private UIPanel pnlInner;
    // 基本信息对话框
    private UIPanel pnlName;
    private UIPanel pnlOrientation;
    private UIRadioButton rdoBtn_condition = null; // 子集全部选择
    private UIRadioButton rdoBtn_order = null;
    // 子集行数
    private UIPanel rownoPanel;
    private UIScrollPane rScrollPane = null;// 信息项
    private UIPanel setPanel = null;// 信息集Panel
    private UISpinBox spinbox_order = null;
    private UISplitPane splitPane = null;// 分割信息集和信息项
    // 系统变量
    private UIScrollPane sysItemScrollPane = null;
    private UITabbedPane tabbedPane;
    private UITable tableProp = null;// 信息项内容
    private UITextField tfAfterContent;
    private UITextField tfBeforeContent;
    private UITextField tfDesc;
    private UITextField tfName;
    private UITextField tfQueryProp;
    private UITextField tfQueryType;
    private UITextField tfRowno;
    
    private UITree treeContent = null;// 信息集节点
    
    // private SetdictVO[] vos = null;// 信息集内容
    private InfoSetVO[] vos = null;// 信息集内容
    
    private Word word;
    
    private RegisterShowPanel wordShowPanel;
    
    public ItemSetPnl(LoginContext context, WordSetVO _itemSetVO, InfoSetVO[] _infosetVOs, Word word, int type)
    {
        super();
        this.context = context;
        this.itemSetVO = _itemSetVO;
        this.vos = _infosetVOs;
        this.word = word;
        setLayout(new BorderLayout());
        
        this.add(getPnlContent(), BorderLayout.CENTER);
        if (type == 1)
        {
            this.add(getButtonPanel(), BorderLayout.SOUTH);
        }
        
        this.setPreferredSize(new Dimension(235, 600));
        this.setMinimumSize(new Dimension(235, 600));
        this.setMaximumSize(new Dimension(235, 600));
        
        init();
    }
    
    public ItemSetPnl(WordSetVO _itemSetVO, InfoSetVO[] _infosetVOs, Word word, int type)
    {
        super();
        this.itemSetVO = _itemSetVO;
        this.vos = _infosetVOs;
        this.word = word;
        setLayout(new BorderLayout());
        
        this.add(getPnlContent(), BorderLayout.CENTER);
        if (type == 1)
        {
            this.add(getButtonPanel(), BorderLayout.SOUTH);
        }
        
        this.setPreferredSize(new Dimension(235, 600));
        this.setMinimumSize(new Dimension(235, 600));
        this.setMaximumSize(new Dimension(235, 600));
        
        init();
    }
    
    public ItemSetPnl(WordSetVO _itemSetVO, Word _word, InfoSetVO[] _infosetVOs)
    {
        super();
        itemSetVO = _itemSetVO;
        word = _word;
        vos = _infosetVOs;
        setLayout(new BorderLayout());
        setSize(400, 724);
        this.add(getPnlName(), BorderLayout.NORTH);// 基本信息
        add(getPanel());// 字段选择
        add(getPnlAdvSet(), BorderLayout.SOUTH);// 高级设置
        init();
    }
    
    private ListModel createListModel()
    {
        DefaultListModel listModel = new DefaultListModel();
        TempletfieldVO[] vos = null;
        String names[] = WordPrintEnv.getSysVars();
        String toolTipes[] = WordPrintEnv.sysVarsTip;
        int[] dataType = WordPrintEnv.sysVarsDataType;
        vos = new TempletfieldVO[names.length];
        for (int i = 0; i < vos.length; i++)
        {
            TempletfieldVO tVo = new TempletfieldVO();
            tVo.setTable_Code("");
            tVo.setTable_Name("");
            tVo.setField_code(toolTipes[i]);
            tVo.setField_name(names[i]);
            tVo.setData_type(dataType[i]);
            vos[i] = tVo;
        }
        if (vos != null && vos.length != 0)
        {
            for (int i = 0; i < vos.length; i++)
            {
                ListNode no01 = new ListNode(vos[i]);
                no01.setShowName(vos[i].getField_name());
                listModel.addElement(no01);
            }
        }
        return listModel;
    }
    
    private TreeModel createTreeModel() throws BusinessException
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(ResHelper.getString("6001rtf", "06001rtf0013")
        /* @res "信息集" */);
        
        for (InfoSetVO temp : vos)
        {
            if (temp.getInfoset_code().equals("hi_psndoc_parttime"))
            {
                // 兼职记录试用hi_psndoc_parttime,生成卡片时在后台处理
                temp.setTable_code("hi_psndoc_parttime");
            }
            root.add(new DefaultMutableTreeNode(temp));
        }
        TreeModel treeMode = new DefaultTreeModel(root);
        return treeMode;
    }
    
    /**
     * 后缀
     */
    public UIPanel getAfterContentPanel()
    {
        if (afterContentPanel == null)
        {
            afterContentPanel = new UIPanel();
            afterContentPanel.setName("afterContentPanel");
            
            FlowLayout fl = new FlowLayout();
            fl.setAlignment(FlowLayout.LEFT);
            fl.setVgap(1);
            fl.setHgap(2);
            afterContentPanel.setLayout(fl);
            afterContentPanel.setPreferredSize(new Dimension(215, 22));
            
            afterContentPanel.add(getLabelAft(), getLabelAft().getName());
            afterContentPanel.add(getTfAfterContent(), getTfAfterContent().getName());
        }
        return afterContentPanel;
    }
    
    public UIPanel getBeforeContentPanel()
    {
        if (beforeContentPanel == null)
        {
            beforeContentPanel = new UIPanel();
            beforeContentPanel.setName("beforeContentPanel");
            
            FlowLayout fl = new FlowLayout();
            fl.setAlignment(FlowLayout.LEFT);
            fl.setVgap(1);
            fl.setHgap(2);
            beforeContentPanel.setLayout(fl);
            beforeContentPanel.setPreferredSize(new Dimension(215, 22));
            
            beforeContentPanel.add(getLabelPre(), getLabelPre().getName());
            beforeContentPanel.add(getTfBeforeContent(), getTfBeforeContent().getName());
        }
        return beforeContentPanel;
    }
    
    protected UIButton getBtnSetCondition()
    {
        if (btnSetCondition == null)
        {
            btnSetCondition = new UIButton();
            btnSetCondition.setText(ResHelper.getString("6001rtf", "06001rtf0014")
            /* @res "子集取数条件" */);
            btnSetCondition.setBounds(5, 5, 175, 25);
            btnSetCondition.setToolTipText(ResHelper.getString("6001rtf", "06001rtf0015")
            /* @res "设置选中选元格信息项的条件" */);
        }
        return btnSetCondition;
    }
    
    private UIPanel getButtonPanel()
    {
        if (buttonPanel == null)
        {
            buttonPanel = new UIPanel();
            buttonPanel.setLayout(new BorderLayout());
            UIPanel temp = new UIPanel(new FlowLayout());
            temp.add(getOKButton());
            temp.add(getCancelButton());
            // temp.add(getSaveAsButton());
            buttonPanel.add(temp);
        }
        return buttonPanel;
    }
    
    public UIButton getCancelButton()
    {
        if (cancelButton == null)
        {
            cancelButton = new UIButton();
            cancelButton.setText(ResHelper.getString("common", "UC001-0000008")
            /* @res "取消" */);
        }
        return cancelButton;
    }
    
    /**
     * @return the combobobx_dateformat
     */
    public UIComboBox getCombobobx_dateformat()
    {
        if (combobobx_dateformat == null)
        {
            combobobx_dateformat = new UIComboBox();
            combobobx_dateformat.setName("combobobx_dateformat");
            // combobobx_dateformat.setBounds(50, 120, 0, 0);
            combobobx_dateformat.setPreferredSize(new Dimension(120, 22));
            
            // 加入选择条件
            for (int i = 0; i < nc.vo.hr.tools.rtf.CommonValue.DATEFORMAT_NAMES.length; i++)
            {
                combobobx_dateformat.addItem(nc.vo.hr.tools.rtf.CommonValue.DATEFORMAT_NAMES[i]);
            }// end for
        }
        return combobobx_dateformat;
    }
    
    /**
     * @return the combobox_order
     */
    public UIComboBox getCombobox_order()
    {
        if (combobox_order == null)
        {
            combobox_order = new UIComboBox();
            combobox_order.setName("combobox_order");
            combobox_order.setPreferredSize(new Dimension(80, 20));
            
            combobox_order.addItem(ResHelper.getString("6001rtf", "06001rtf0016")
            /* @res "最近第" */);
            combobox_order.addItem(ResHelper.getString("6001rtf", "06001rtf0017")
            /* @res "最近" */);
            combobox_order.addItem(ResHelper.getString("6001rtf", "06001rtf0018")
            /* @res "最初第" */);
            combobox_order.addItem(ResHelper.getString("6001rtf", "06001rtf0019")
            /* @res "最初" */);
            
            combobox_order.setSelectedIndex(0);
            // combobox_order.addActionListener(this);
        }
        return combobox_order;
    }
    
    public Map getConditionMap()
    {
        return conditionMap;
    }
    
    public LoginContext getContext()
    {
        return context;
    }
    
    public ListNode getCurVar()
    {
        return curVar;
    }
    
    /**
     * 日期格式
     */
    public UIPanel getDataFormatPanel()
    {
        if (dataFormatPanel == null)
        {
            dataFormatPanel = new UIPanel();
            dataFormatPanel.setName("dataFormatPanel");
            Border border1 = BorderFactory.createEtchedBorder();
            TitledBorder titledBorder1 = new TitledBorder(border1, ResHelper.getString("common", "UC000-0002313")
            /* @res "日期" */);
            dataFormatPanel.setBorder(titledBorder1);
            dataFormatPanel.setBounds(3, 150, 345, 60);
            UILabel l1 = new UILabel(ResHelper.getString("6001rtf", "06001rtf0020")
            /* @res "日期格式" */);
            FlowLayout fl = new FlowLayout();
            fl.setAlignment(FlowLayout.LEFT);
            fl.setVgap(1);
            fl.setHgap(2);
            dataFormatPanel.setLayout(fl);
            dataFormatPanel.setPreferredSize(new Dimension(215, 22));
            l1.setPreferredSize(new Dimension(75, 22));
            dataFormatPanel.add(l1);
            dataFormatPanel.add(getCombobobx_dateformat());
        }
        return dataFormatPanel;
    }
    
    /**
     * 扩充文本
     */
    public UIPanel getExtendContentPanel()
    {
        if (extendContentPanel == null)
        {
            extendContentPanel = new UIPanel();
            extendContentPanel.setName("extendContentPanel");
            Border border1 = BorderFactory.createEtchedBorder();
            TitledBorder titledBorder1 = new TitledBorder(border1, ResHelper.getString("6001rtf", "06001rtf0021")
            /* @res "扩充文本" */);
            extendContentPanel.setBorder(titledBorder1);
            extendContentPanel.setBounds(3, 210, 345, 80);
            FlowLayout fl = new FlowLayout();
            fl.setAlignment(FlowLayout.LEFT);
            fl.setVgap(1);
            fl.setHgap(2);
            extendContentPanel.setLayout(fl);
            extendContentPanel.setPreferredSize(new Dimension(215, 22));
            extendContentPanel.add(getBeforeContentPanel(), BorderLayout.NORTH);
            extendContentPanel.add(getAfterContentPanel(), BorderLayout.SOUTH);
        }
        return extendContentPanel;
    }
    
    public UIPanel getInfoItemLocationPanel()
    {
        if (infoItemLocationPanel == null)
        {
            infoItemLocationPanel = new UIPanel();
            infoItemLocationPanel.setName("InfoItemLocationPanel");
            Border border = BorderFactory.createEtchedBorder();
            TitledBorder titledBorder1 = new TitledBorder(border, ResHelper.getString("6001rtf", "06001rtf0022")
            /* @res "信息项定位" */);
            infoItemLocationPanel.setBorder(titledBorder1);
            infoItemLocationPanel.setBounds(3, 3, 345, 80);
            // infoItemLocationPanel.setPreferredSize(new Dimension(3,30));
            
            BorderLayout bl = new BorderLayout();
            // fl.setAlignment(FlowLayout.LEFT);
            infoItemLocationPanel.setLayout(bl);
            infoItemLocationPanel.add(getPanel_order(), BorderLayout.SOUTH);
            infoItemLocationPanel.add(getPanel_condition(), BorderLayout.NORTH);
            
            // 加入BUTTON GROUP
            ButtonGroup bg = new ButtonGroup();
            bg.add(getRdoBtn_order());
            bg.add(getRdoBtn_condition());
            // getRdoBtn_condition().setSelected(true);
        }
        return infoItemLocationPanel;
    }
    
    /**
     * @return the label_order
     */
    public UILabel getLabel_order()
    {
        if (label_order == null)
        {
            label_order = new UILabel();
            label_order.setName("label_order");
            label_order.setPreferredSize(new Dimension(40, 20));
            label_order.setText(ResHelper.getString("6001rtf", "06001rtf0023")
            /* @res "条" */);
        }
        return label_order;
    }
    
    protected UILabel getLabelAft()
    {
        if (labelAft == null)
        {
            labelAft = new UILabel();
            labelAft.setText(ResHelper.getString("6001rtf", "06001rtf0024")
            /* @res "后缀" */);
            labelAft.setPreferredSize(new Dimension(75, 20));
        }
        return labelAft;
    }
    
    /**
     * 前缀
     */
    protected UILabel getLabelPre()
    {
        if (labelPre == null)
        {
            labelPre = new UILabel();
            labelPre.setText(ResHelper.getString("6001rtf", "06001rtf0025")
            /* @res "前缀" */);
            labelPre.setPreferredSize(new Dimension(75, 20));
        }
        return labelPre;
    }
    
    protected UILabel getLblDesc()
    {
        if (lblDesc == null)
        {
            lblDesc = new UILabel();
            lblDesc.setText(ResHelper.getString("common", "UC000-0001155")
            /* @res "名称" */+ ":");
            // lblDesc.setBounds(200, 20, 60, 20);
            BillPanelUtils.setPreferredWidth(lblDesc);
            Double lblDou = lblDesc.getSize().getWidth();
            int width = Integer.parseInt(lblDou.toString().substring(0, lblDou.toString().indexOf(".")));
            
            int startPoint = getLblName().getWidth() + getTfName().getWidth();
            lblDesc.setBounds(startPoint + 10, 20, width, 20);
        }
        return lblDesc;
    }
    
    protected UILabel getLblName()
    {
        if (lblName == null)
        {
            lblName = new UILabel();
            lblName.setText(ResHelper.getString("common", "UC000-0003279")
            /* @res "编码" */+ ":");
            BillPanelUtils.setPreferredWidth(lblName);
            Double lblDou = lblName.getSize().getWidth();
            int width = Integer.parseInt(lblDou.toString().substring(0, lblDou.toString().indexOf(".")));
            lblName.setBounds(5, 20, width, 20);
        }
        return lblName;
    }
    
    private UILabel getLblTypeLocate()
    {
        if (lblTypeLocate == null)
        {
            lblTypeLocate = new UILabel(ResHelper.getString("6001rtf", "06001rtf0026")
            /* @res "定位" */);
        }
        return lblTypeLocate;
    }
    
    protected UIPanel getLComponent()
    {
        if (setPanel == null)
        {
            setPanel = new UIPanel();
            setPanel.setLayout(new BorderLayout());
            setPanel.add(getPanelOrientation(), BorderLayout.NORTH);
            setPanel.add(getLScrollPane());
        }
        return setPanel;
    }
    
    public UIList getListSysItem()
    {
        if (listSysItem == null)
        {
            listSysItem = new UIList();
            listSysItem.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        }
        return listSysItem;
    }
    
    protected UIScrollPane getLScrollPane()
    {
        if (lScrollPane == null)
        {
            lScrollPane = new UIScrollPane();
            lScrollPane.setViewportView(getTreeContent());
        }
        return lScrollPane;
    }
    
    public UIButton getOKButton()
    {
        if (okButton == null)
        {
            okButton = new UIButton();
            okButton.setText(ResHelper.getString("common", "UC001-0000044")
            /* @res "确定" */);
        }
        return okButton;
    }
    
    /*************** 字段选择部分 ************************************/
    /**
     * @return 变量选择Panel
     */
    protected UIPanel getPanel()
    {
        if (panel == null)
        {
            panel = new UIPanel();
            panel.setBorder(new TitledBorder(null, ResHelper.getString("6001rtf", "06001rtf0027")
            /* @res "字段选择" */, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
            panel.setLayout(new BorderLayout());
            panel.add(getTabbedPane());
        }
        return panel;
    }
    
    /**
     * 子集全部选择
     */
    public UIPanel getPanel_condition()
    {
        if (panel_condition == null)
        {
            panel_condition = new UIPanel();
            panel_condition.setName("panel_condition");
            
            FlowLayout fl = new FlowLayout();
            fl.setAlignment(FlowLayout.LEFT);
            fl.setVgap(1);
            fl.setHgap(2);
            panel_condition.setLayout(fl);
            panel_condition.setPreferredSize(new Dimension(215, 22));
            
            panel_condition.add(getRdoBtn_condition(), getRdoBtn_condition().getName());
        }
        return panel_condition;
    }
    
    /**
     * 序号定位
     */
    public UIPanel getPanel_order()
    {
        if (panel_order == null)
        {
            panel_order = new UIPanel();
            panel_order.setName("panel_order");
            
            FlowLayout fl = new FlowLayout();
            fl.setAlignment(FlowLayout.LEFT);
            fl.setVgap(1);
            fl.setHgap(2);
            panel_order.setLayout(fl);
            panel_order.setPreferredSize(new Dimension(215, 22));
            
            panel_order.add(getRdoBtn_order(), getRdoBtn_order().getName());
            panel_order.add(getCombobox_order(), getCombobox_order().getName());
            panel_order.add(getSpinbox_order(), getSpinbox_order().getName());
            panel_order.add(getLabel_order(), getLabel_order().getName());
        }
        return panel_order;
    }
    
    /**
     * 定位信息
     */
    protected UIPanel getPanelOrientation()
    {
        if (panelOrientation == null)
        {
            panelOrientation = new UIPanel();
            panelOrientation.setLayout(new FlowLayout());
            panelOrientation.setPreferredSize(new Dimension(0, 30));
            panelOrientation.add(getLblTypeLocate());
            panelOrientation.add(getTfQueryType());
        }
        return panelOrientation;
    }
    
    protected UIPanel getPanelROrientation()
    {
        if (panelROrientation == null)
        {
            panelROrientation = new UIPanel();
            panelROrientation.setPreferredSize(new Dimension(0, 30));
            panelROrientation.add(getTfQueryProp());
        }
        return panelROrientation;
    }
    
    /*************** 高级设置部分 ************************************/
    protected UIPanel getPnlAdvSet()
    {
        if (pnlAdvSet == null)
        {
            pnlAdvSet = new UIPanel();
            pnlAdvSet.setLayout(new BorderLayout());
            pnlAdvSet.setPreferredSize(new Dimension(0, 350));
            pnlAdvSet.setBorder(new TitledBorder(null, ResHelper.getString("6001rtf", "06001rtf0028")
            /* @res "高级设置" */, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
            //由于卡片设计时<子集取数条件>这一功能问题较多且不易修改，65版本经和需求讨论后将此功能隐藏  20151017 yanglt
            pnlAdvSet.add(getPnlInner(), BorderLayout.NORTH);
            pnlAdvSet.add(getpnlOrientation());
        }
        return pnlAdvSet;
    }
    
    protected UIPanel getPnlContent()
    {
        if (pnlContent == null)
        {
            pnlContent = new UIPanel();
            pnlContent.setLayout(new BorderLayout());
            pnlContent.add(getPnlName(), BorderLayout.NORTH);
            pnlContent.add(getPanel(), BorderLayout.CENTER);
            pnlContent.add(getPnlAdvSet(), BorderLayout.SOUTH);
        }
        return pnlContent;
    }
    
    protected UIPanel getPnlInner()
    {
        if (pnlInner == null)
        {
            pnlInner = new UIPanel();
            pnlInner.setPreferredSize(new Dimension(300, 30));
            pnlInner.setLayout(null);
            pnlInner.add(getBtnSetCondition());
        }
        return pnlInner;
    }
    
    /*************** 基本信息部分 ************************************/
    protected UIPanel getPnlName()
    {
        if (pnlName == null)
        {
            pnlName = new UIPanel();
            pnlName.setPreferredSize(new Dimension(0, 50));
            pnlName.setLayout(null);
            
            pnlName.add(getLblName());
            pnlName.add(getTfName());
            pnlName.add(getLblDesc());
            pnlName.add(getTfDesc());
            
            pnlName.setBorder(new TitledBorder(ResHelper.getString("common", "UC001-0000068")
            /* @res "基本信息" */));
        }
        return pnlName;
    }
    
    /**
     * 信息项定位
     */
    protected UIPanel getpnlOrientation()
    {
        if (pnlOrientation == null)
        {
            pnlOrientation = new UIPanel();
            pnlOrientation.setLayout(null);
            pnlOrientation.add(getInfoItemLocationPanel());
            pnlOrientation.add(getRownoPanel());
            pnlOrientation.add(getDataFormatPanel());
            pnlOrientation.add(getExtendContentPanel());
        }
        return pnlOrientation;
    }
    
    protected UIPanel getRComponent()
    {
        if (dictPanel == null)
        {
            dictPanel = new UIPanel();
            dictPanel.setLayout(new BorderLayout());
            dictPanel.add(getPanelROrientation(), BorderLayout.NORTH);
            dictPanel.add(getRScrollPane());
        }
        return dictPanel;
    }
    
    /**
     * @return the rdoBtn_condition
     */
    public UIRadioButton getRdoBtn_condition()
    {
        if (rdoBtn_condition == null)
        {
            rdoBtn_condition = new UIRadioButton();
            rdoBtn_condition.setName("rdoBtn_condition");
            rdoBtn_condition.setPreferredSize(new Dimension(75, 20));
            rdoBtn_condition.setText(ResHelper.getString("6001rtf", "06001rtf0029")
            /* @res "全部选择" */);
            
            // rdoBtn_condition.addActionListener(this);
        }
        return rdoBtn_condition;
    }
    
    /**
     * @return the rdoBtn_order
     */
    public UIRadioButton getRdoBtn_order()
    {
        if (rdoBtn_order == null)
        {
            rdoBtn_order = new UIRadioButton();
            rdoBtn_order.setName("rdoBtn_order");
            rdoBtn_order.setPreferredSize(new Dimension(100, 20));
            rdoBtn_order.setText(ResHelper.getString("6001rtf", "06001rtf0030")
            /* @res "序号定位" */);
            
            // rdoBtn_order.addActionListener(this);
        }
        return rdoBtn_order;
    }
    
    /**
     * 子集显示行数
     */
    public UIPanel getRownoPanel()
    {
        if (rownoPanel == null)
        {
            rownoPanel = new UIPanel();
            rownoPanel.setName("rownoPanel");
            Border border1 = BorderFactory.createEtchedBorder();
            TitledBorder titledBorder1 = new TitledBorder(border1, ResHelper.getString("6001rtf", "06001rtf0031")
            /* @res "子集显示行数" */);
            rownoPanel.setBorder(titledBorder1);
            rownoPanel.setBounds(3, 90, 345, 60);
            UILabel l1 = new UILabel(ResHelper.getString("6001rtf", "06001rtf0031")
            /* @res "子集显示行数" */);
            FlowLayout f1 = new FlowLayout();
            f1.setAlignment(FlowLayout.LEFT);
            f1.setVgap(1);
            f1.setHgap(2);
            rownoPanel.setLayout(f1);
            rownoPanel.setPreferredSize(new Dimension(215, 22));
            l1.setPreferredSize(new Dimension(140, 22));
            rownoPanel.add(l1);
            rownoPanel.add(getTfRowno());
        }
        return rownoPanel;
    }
    
    protected UIScrollPane getRScrollPane()
    {
        if (rScrollPane == null)
        {
            rScrollPane = new UIScrollPane();
            rScrollPane.setViewportView(getTableProp());
        }
        return rScrollPane;
    }
    
    private InfoSetVO getSelectionObject()
    {
        return (InfoSetVO) ((DefaultMutableTreeNode) getTreeContent().getLastSelectedPathComponent()).getUserObject();
    }
    
    public UIRadioButton getSelectRadio()
    {
        if (getRdoBtn_condition().isSelected())
        {
            return getRdoBtn_condition();
        }
        else if (getRdoBtn_order().isSelected())
        {
            return getRdoBtn_order();
        }
        return null;
    }
    
    /**
     * @return the spinbox_order
     */
    public UISpinBox getSpinbox_order()
    {
        if (spinbox_order == null)
        {
            spinbox_order = new UISpinBox();
            spinbox_order.setName("spinbox_order");
            spinbox_order.setPreferredSize(new Dimension(40, 20));
            spinbox_order.setValue(1);
            spinbox_order.setEditable(true);
            // spinbox_order.addValueChangedListener(this);
        }
        return spinbox_order;
    }
    
    protected UISplitPane getSplitPane()
    {
        if (splitPane == null)
        {
            splitPane = new UISplitPane();
            splitPane.setDividerSize(5);
            splitPane.setDividerLocation(150);
            splitPane.setLeftComponent(getLComponent());
            splitPane.setRightComponent(getRComponent());
        }
        return splitPane;
    }
    
    /**
     * 系统变量选择Panel
     */
    public UIScrollPane getSysItemScrollPane()
    {
        if (sysItemScrollPane == null)
        {
            sysItemScrollPane = new UIScrollPane();
            sysItemScrollPane.setViewportView(getListSysItem());
        }
        return sysItemScrollPane;
    }
    
    public UITabbedPane getTabbedPane()
    {
        if (tabbedPane == null)
        {
            tabbedPane = new UITabbedPane();
            tabbedPane.add(getSplitPane(), ResHelper.getString("6001rtf", "06001rtf0013")
            /* @res "信息集" */);
            tabbedPane.add(getSysItemScrollPane(), ResHelper.getString("6001rtf", "06001rtf0032")
            /* @res "系统变量" */);
        }
        return tabbedPane;
    }
    
    protected UITable getTableProp()
    {
        if (tableProp == null)
        {
            tableProp = new UITable(new DefaultTableModel()
            {
                @Override
                public boolean isCellEditable(int row, int column)
                {
                    return false;
                }
            });
            tableProp.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        return tableProp;
    }
    
    protected UITextField getTfAfterContent()
    {
        if (tfAfterContent == null)
        {
            tfAfterContent = new UITextField();
            tfAfterContent.setPreferredSize(new Dimension(121, 20));
        }
        return tfAfterContent;
    }
    
    protected UITextField getTfBeforeContent()
    {
        if (tfBeforeContent == null)
        {
            tfBeforeContent = new UITextField();
            tfBeforeContent.setPreferredSize(new Dimension(121, 20));
        }
        return tfBeforeContent;
    }
    
    protected UITextField getTfDesc()
    {
        if (tfDesc == null)
        {
            tfDesc = new UITextField();
            // tfDesc.setBounds(265, 20, 100, 20);
            int width = getLblName().getWidth() + getTfName().getWidth() + getLblDesc().getWidth();
            tfDesc.setBounds(width, 20, 100, 20);
            tfDesc.setMaxLength(200);
            tfDesc.setText(itemSetVO.getModelName());
            tfDesc.setEditable(false);
        }
        return tfDesc;
    }
    
    protected UITextField getTfName()
    {
        if (tfName == null)
        {
            tfName = new UITextField();
            tfName.setBounds(getLblName().getWidth(), 20, 100, 20);
            tfName.setMaxLength(50);
            tfName.setText(itemSetVO.getModelCode());
            tfName.setEditable(false);
        }
        return tfName;
    }
    
    protected UITextField getTfQueryProp()
    {
        if (tfQueryProp == null)
        {
            tfQueryProp = new UITextField();
            tfQueryProp.setPreferredSize(new Dimension(100, 22));
        }
        return tfQueryProp;
    }
    
    protected UITextField getTfQueryType()
    {
        if (tfQueryType == null)
        {
            tfQueryType = new UITextField();
            tfQueryType.setPreferredSize(new Dimension(100, 22));
        }
        return tfQueryType;
    }
    
    public UITextField getTfRowno()
    {
        if (tfRowno == null)
        {
            tfRowno = new UITextField();
            tfRowno.setPreferredSize(new Dimension(121, 20));
        }
        return tfRowno;
    }
    
    protected UITree getTreeContent()
    {
        if (treeContent == null)
        {
            treeContent = new UITree();
            treeContent.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        }
        return treeContent;
    }
    
    public Word getWord()
    {
        return word;
    }
    
    // public void refreshData() {
    // initData();
    // this.setConditionMap(itemSetVO.getConditionMap());
    // getWordSetUI().refreshData();
    // }
    //
    // private void initData() {
    // try {
    // setOldWordVO((WordVO) cloneObject(getWordVO()));
    // } catch (CloneNotSupportedException e) {
    // throw new RuntimeException("备份数据错误！");
    // }
    // }
    //
    // private WordVO getOldWordVO() {
    // return oldWordVO;
    // }
    //
    // private void setOldWordVO(WordVO wordVO) {
    // this.oldWordVO = wordVO;
    // }
    //
    // private WordVO getWordVO() {
    // WordVO wordVO = getRFTemplateVO().getTemplate_content();
    // if (wordVO == null) {
    // wordVO = new WordVO();
    // getRFTemplateVO().setTemplate_content(wordVO);
    // }
    // return wordVO;
    // }
    
    public WordSetVO getWordSetVO()
    {
        return itemSetVO;
    }
    
    public RegisterShowPanel getWordShowPanel()
    {
        return wordShowPanel;
    }
    
    public void init()
    {
        initListener();
        try
        {
            refreshSysItem();
            refreshData();
            
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage());
            word.close();
            WordUtil.deleteAllTempFile();
            setWord(null);
            // throw new BusinessException("参数配置初始化错误!");
        }
    }
    
    private void initListener()
    {
        this.addMouseListener(mouseListener);
        
        // 系统变量
        getListSysItem().addMouseListener(mouseListener);// 监听双击事件
        getListSysItem().addListSelectionListener(listSelectionListener);// 监听单击事件
        
        // 信息项
        getTableProp().addMouseListener(mouseListener);// 监听双击事件
        getTableProp().getSelectionModel().addListSelectionListener(listSelectionListener);// 监听单击事件
        getTreeContent().addTreeSelectionListener(new WordSetTreeSelectionListener(this)); // 监听选中树右边选项的变化
        
        getBtnSetCondition().addActionListener(actionListener);
        
        getRdoBtn_condition().addItemListener(itemListener);
        getRdoBtn_order().addItemListener(itemListener);
        
        getTfQueryType().addKeyListener(keyListener);
        getTfQueryProp().addKeyListener(keyListener);
        
        getTabbedPane().addChangeListener(this);
    }
    
    /**
     * 判断选中的信息项的类型是否是日期
     * @author fengwei on 2010-01-22
     * @param templetvo
     * @return
     */
    private boolean isDateType(TempletfieldVO templetvo)
    {
        return templetvo.getData_type().intValue() == 20 || templetvo.getData_type().intValue() == 15;
    }
    
    /**
     * 刷新高级设置中的信息项的类型 <br>
     * 1.如果选中的信息项的类型是日期，则日期格式可选，双击该信息项后，可以带出选中的日期格式； <br>
     * 2.如果选中的信息项的类型是参照，则
     * @author fengwei on 2010-01-22
     */
    public void refreshAdvSetData()
    {
        TempletfieldVO tempVO = (TempletfieldVO) getCurVar().getObject();
        boolean isDateType = isDateType(tempVO);
        getDataFormatPanel().setEnabled(isDateType);
        getCombobobx_dateformat().setEnabled(isDateType);
    }
    
    /**
     * 刷新信息项
     */
    public void refreshData() throws BusinessException
    {
        refreshNode();
        this.setConditionMap(itemSetVO.getConditionMap());
    }
    
    private void refreshNode() throws BusinessException
    {
        getTreeContent().setModel(createTreeModel());
        getTreeContent().setSelectionRow(1);
    }
    
    private void refreshSysItem()
    {
        getListSysItem().setModel(createListModel());
    }
    
    public void setConditionMap(Map conditionMap)
    {
        this.conditionMap = conditionMap;
    }
    
    public void setContext(LoginContext context)
    {
        this.context = context;
    }
    
    public void setCurVar(ListNode curVar)
    {
        this.curVar = curVar;
    }
    
    public void setWord(Word word)
    {
        this.word = word;
    }
    
    public void setWordShowPanel(RegisterShowPanel wordShowPanel)
    {
        this.wordShowPanel = wordShowPanel;
    }
    
    /*
     * (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    @Override
    public void stateChanged(ChangeEvent e)
    {
        int index = getTabbedPane().getSelectedIndex();
        if (index == 1)
        {
            getBtnSetCondition().setEnabled(false);
            getRdoBtn_condition().setEnabled(false);
            getRdoBtn_order().setEnabled(false);
            getTfRowno().setEnabled(false);
        }
        else
        {
            String table_code = getSelectionObject().getTable_code();
            if (table_code.equalsIgnoreCase("bd_psndoc") || table_code.equalsIgnoreCase("om_job") || table_code.equalsIgnoreCase("om_post"))
            {
                getBtnSetCondition().setEnabled(false);
                getRdoBtn_condition().setEnabled(false);
                getRdoBtn_order().setEnabled(false);
                getTfRowno().setEnabled(false);
            }
            else
            {
                getBtnSetCondition().setEnabled(true);
                getRdoBtn_condition().setEnabled(true);
                getRdoBtn_order().setEnabled(true);
                getTfRowno().setEnabled(true);
            }
        }
    }
}
