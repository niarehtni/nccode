package com.ufida.report.frquery.popupdlg.comp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import nc.ui.plaf.basic.UIRefButtonUI;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.queryarea.util.QueryAreaColor;
import nc.ui.querytemplate.QueryDialogColor;
import nc.ui.querytemplate.filtereditor.FilterLabelBorder;

import com.ufida.report.frquery.ComboBoxUI;
import com.ufida.report.frquery.base.DValue;
import com.ufida.report.frquery.base.Operator;
import com.ufida.report.frquery.comp.FrQueryAbstractComp;
import com.ufida.report.frquery.comp.FrQueryCalendarComp;
import com.ufida.report.frquery.comp.FrQueryCheckBoxComp;
import com.ufida.report.frquery.comp.FrQueryComboBoxComp;
import com.ufida.report.frquery.comp.FrQueryOpersCBox;
import com.ufida.report.frquery.comp.FrQueryRefComp;
import com.ufida.report.frquery.comp.FrQueryTextFieldComp;
import com.ufida.report.frquery.model.FrFilterCaseDefine;
import com.ufida.report.frquery.model.FrQueryItem;
import com.ufida.zior.util.ResourceManager;

/**
 * 报表查询对话框-查询条件编辑组件
 * @author yanchm
 */
public abstract class FrQueryDlgAbstractComp extends UIPanel  implements ActionListener{
	private static final long serialVersionUID = 1L;

	public static final String LWQUERYPANEL_CHANGED = "frQueryPanelChanged";

	/**
	 *  查询项
	 */
	private FrQueryItem frQueryItem = null;
	
	/**
	 * 查询项对应的label
	 */
	private UILabel frQueryItemLbl = null;

	/**
	 * 操作符下拉列表
	 */
	private FrQueryOpersCBox frQueryOpersCBox = null;
	
	/**
	 * 清空值小按钮
	 */
	private UIButton clearValueBtn = null;
	
	/**
	 * 操作符面板：包括操作符下拉框和清空值小按钮
	 */
	private UIPanel operatorPanel = null;
	
	/**
	 * 值面板(不含清空值小按钮)
	 */
	protected UIPanel valuePanel = null;
	
	/** 固定条件面板（包含一个设置固定条件的按钮和一个隐藏的label），liws0*/
	private JPanel fixedPanel = null;
	/** 设置固定条件的按钮*/
	private JButton fixedButton = null;
	/** 表示是否设置为固定条件的label*/
	private JLabel fixedLabel = null;
	private Icon fixedIcon = null;
	private Icon unFixedIcon = null;
	/** 设置固定条件的按钮所用的图片，表示是固定条件*/
	private Icon getFixedIcon(){
		if(fixedIcon == null){
			fixedIcon = ResourceManager.createIcon("images/frquery/fixed.png");
		}
		return fixedIcon;
	}
	/** 设置固定条件的按钮所用的图片，表示不是固定条件*/
	private Icon getUnFixedIcon(){
		if(unFixedIcon == null){
			unFixedIcon = ResourceManager.createIcon("images/frquery/unfixed.png");
		}
		return unFixedIcon;
	}
	/** 设置固定条件的按钮  绑定的监听器*/
	private FixBtnListener fixBtnListener = null;

	/**
	 * 构造方法
	 * @param frQueryItem
	 */
	public FrQueryDlgAbstractComp(FrQueryItem frQueryItem) {
		if (frQueryItem == null) {
			return;
		}
		this.frQueryItem = frQueryItem;
		initComponents();// 初始化组件
	}

	/**
	 * 初始化组件
	 * 
	 */
	private void initComponents() {
		setOpaque(true);
		//设置布局为查询条件编辑组件布局
		setLayout(new FilterEditorLayout());
		setPreferredSize(new Dimension(340,37));
		this.add(FilterEditorLayout.FIXEDDITOR , getFixedPanel());//add by liws0，添加固定条件面板
		this.add(FilterEditorLayout.FIELD , getFrQueryItemLbl());
		this.add(FilterEditorLayout.OPERATORS ,getOperatorPanel());
		getFrQueryOpersCBox().addActionListener(this);

	}

	/**
	 * 设置固定条件的按钮  绑定的监听器类
	 * @author liws0
	 *
	 */
	class FixBtnListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			FrQueryAbstractComp valueEditComp = getValueEditComp();
			if(getFixedLabel().getText().equals(FrFilterCaseDefine.IS_FIXED)){
				//本来是固定条件，改为非固定条件的处理
				doUnFixed(valueEditComp, false);
			}else if(getFixedLabel().getText().equals(FrFilterCaseDefine.IS_NOT_FIXED)){
				//本来是非固定条件，改为固定条件的处理
				doFixed(valueEditComp, false);
			}
		}
		
		/**
		 * 设置为固定条件的处理
		 * @param valueEditComp
		 */
		public void doFixed(FrQueryAbstractComp valueEditComp, boolean isPub) {
			//TODO 1、设置lable值
			getFixedLabel().setText(FrFilterCaseDefine.IS_FIXED);
			//2、改变fixedButton中的icon图片为 闭锁
			getFixedButton().setIcon(getFixedIcon());
			getFixedButton().setToolTipText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-1883")/*@res "取消固定设置"*/);
			//3、将相关组件置为 不可用状态
			getFrQueryOpersCBox().setEnabled(false);
			getClearValueBtn().setEnabled(false);
			valueEditComp.setCompEnable(false);
			//add by liws0. 如果是发布后节点中编辑器
			if(isPub){
				getFixedButton().setEnabled(false);
			}
		}

		/**
		 * 设置为非固定条件的处理
		 * @param valueEditComp
		 */
		public void doUnFixed(FrQueryAbstractComp valueEditComp, boolean isPub) {
			//TODO 1、设置lable值
			getFixedLabel().setText(FrFilterCaseDefine.IS_NOT_FIXED);
			//2、改变fixedButton中的icon图片为 开锁
			getFixedButton().setIcon(getUnFixedIcon());
			getFixedButton().setToolTipText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-1884")/*@res "设置为固定条件"*/);
			//3、将相关组件置为 可用状态
			getFrQueryOpersCBox().setEnabled(true);
			getClearValueBtn().setEnabled(true);
			valueEditComp.setCompEnable(true);
			//add by liws0. 如果是发布后节点中编辑器
			if(isPub){
				getFixedButton().setEnabled(false);
			}
		}
		
	}
	
	/**
	 * 获得  设置固定条件的按钮所绑定的监听器
	 * @author liws0
	 */
	private FixBtnListener getFixBtnListener() {
		if(fixBtnListener == null){
			fixBtnListener = new FixBtnListener();
		}
		return fixBtnListener;
	}
	
	/**
	 * 设置为固定条件的处理
	 * @author liws0
	 */
	public void doFixed(boolean isPub){
		getFixBtnListener().doFixed(getValueEditComp(), isPub);
	}
	
	/**
	 * 设置为非固定条件的处理
	 * @author liws0
	 */
	public void doUnFixed(boolean isPub){
		getFixBtnListener().doUnFixed(getValueEditComp(), isPub);
	}
	
	
	/**
	 * 获得  固定条件面板
	 * @return
	 */
	private JPanel getFixedPanel(){
		if(fixedPanel == null){
			fixedPanel = new JPanel();
			fixedPanel.setLayout(new BorderLayout());
			fixedPanel.add(getFixedButton(), BorderLayout.CENTER);
			fixedPanel.add(getFixedLabel(), BorderLayout.EAST);
			getFixedButton().addActionListener(getFixBtnListener());
		}
		return fixedPanel;
	}
	
	/**
	 * 设置固定条件的按钮
	 * @return
	 */
	private JButton getFixedButton(){
		if(fixedButton == null){
			fixedButton = new JButton(getUnFixedIcon());
			fixedButton.setContentAreaFilled(false);	//设置按钮透明！
		}
		return fixedButton;
	}
	
	/**
	 * 表示是否设置为固定条件的label
	 * @return
	 */
	public JLabel getFixedLabel(){
		if(fixedLabel == null){
			fixedLabel = new JLabel(FrFilterCaseDefine.IS_NOT_FIXED);//默认为非固定条件
			fixedLabel.setVisible(false); //隐藏label
		}
		return fixedLabel;
	}
	
	/**
	 * 得到操作符下拉列表
	 * @return UIComboBox
	 */
	public UIComboBox getFrQueryOpersCBox() {
		if (frQueryOpersCBox == null) {
			frQueryOpersCBox = new FrQueryOpersCBox();
			frQueryOpersCBox.setPreferredSize(new Dimension(80, 22));
			frQueryOpersCBox.setOpaque(false);
			frQueryOpersCBox.setUI(new ComboBoxUI());
			frQueryOpersCBox.setBorder(new EmptyBorder(0, 0, 0, 0));
			//更新操作符下拉列表
			updateFrQueryOpersCBoxItems();
		}
		return frQueryOpersCBox;
	}

	/**
	 * 更新操作符下拉列表
	 */
	public void updateFrQueryOpersCBoxItems() {
		frQueryOpersCBox.removeAllItems();
		frQueryOpersCBox.addItems(getSupportedOpers());
	}
	
	/**
	 * 获得该查询项支持的操作符列表
	 */
	public Operator[] getSupportedOpers() {
		Operator[] suppoprs = frQueryItem.getSupportedOpers();
		if (suppoprs == null) {
			suppoprs = new Operator[] { Operator.IN };
		}
		return suppoprs;
	}
	
	/**
	 * 设置当前选中的操作符
	 * @param curOper：当前操作符
	 */
	public void setOperator(Operator curOper){
		getFrQueryOpersCBox().setSelectedItem(curOper);
	}
	
	/**
	 * 设置查询值
	 * @param values：查询值列表
	 */
	public abstract void setDvalues(Operator curOper, List<DValue> values);
	
	/**
	 * 获得查询项对应的标签
	 */
	public UILabel getFrQueryItemLbl(){
		if(frQueryItemLbl == null){
			String myText = frQueryItem.getCaption();
			if (frQueryItem.isNecessary()) {
				 myText = "<html>"+myText+"<FONT COLOR=RED><b> *</b></FONT><FONT COLOR="
						+ getForeground().hashCode() + ">" 
						+ "</FONT></html>";
			}
			frQueryItemLbl = new UILabel(myText);
			frQueryItemLbl.setPreferredSize(new Dimension(80,22));
			frQueryItemLbl.setOpaque(true);
			frQueryItemLbl.setBorder(new CompoundBorder(new FilterLabelBorder(),BorderFactory.createEmptyBorder(0, 7, 0, 0)));
			frQueryItemLbl.setBackground(QueryAreaColor.BKGRD_COLOR_DEFAULT);
		}
		return frQueryItemLbl;
	}
	
	/**
	 * 获得操作符面板：包括操作符下拉框和清楚按钮
	 */
	protected UIPanel getOperatorPanel(){
		if(operatorPanel == null ){
			operatorPanel =new UIPanel();
			operatorPanel.setLayout(new BorderLayout());
			operatorPanel.add(getFrQueryOpersCBox(),BorderLayout.CENTER);
			operatorPanel.add(getClearValueBtn(),BorderLayout.EAST);
			operatorPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, QueryDialogColor.FILTEREDITOR_BORDER_COLOR));
		}
		return operatorPanel;
	}
	
	protected UIPanel getValuePanel(){
		valuePanel = getValueEditComp().getValueComp(getOperator());
		// 查询项增加绑定属性监听器
		getValueEditComp().getFrQueryItem().addPropertyChangeListener(getValueEditComp());
		return valuePanel;
	}
	
	protected abstract FrQueryAbstractComp getValueEditComp ();

	private UIButton getClearValueBtn(){
		if(clearValueBtn == null){
			clearValueBtn = new UIButton();
			// 清除图标
			Icon icon = ResourceManager
					.createIcon("/images/frquery/eraser.gif");
			clearValueBtn.setIcon(icon);
			clearValueBtn.setPreferredSize(new Dimension(20,22));
			clearValueBtn.setUI(new UIRefButtonUI());
			clearValueBtn.setToolTipText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("1413006_0", "01413006-1411")/* @res "清空值" */);
			clearValueBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					//清空查询组件所录入的查询数据
					clearValue();
				}
			});
			
		}
		return clearValueBtn;
	}
	
	public void clearValue(){
		getValueEditComp().clearSelValues();
	}
	/**
	 * 获得查询项
	 */
	public FrQueryItem getFrQueryItem() {
		return frQueryItem;
	}

	/**
	 * 获得当前选择的操作符
	 */
	public Operator getOperator() {
		Operator o = (Operator) getFrQueryOpersCBox().getSelectedItem() ;
		if(o != null){
			return o ;
		}
		return Operator.IN;
	}
	
	/**
	 * 获得输入的查询条件值
	 */
	public abstract DValue [] getDValue();
	

	/**
	 * 查询条件编辑组件布局，按照组件类型添加组件
	 * @author yanchm
	 *
	 */
	public static class FilterEditorLayout implements LayoutManager
	{
		/**
		 * 查询项名称区域
		 */
		private Component cmp_field = null;
		/**
		 * 操作符
		 */
		private Component cmp_operators = null;
		/**
		 * 值编辑组件
		 */
		private Component cmp_valueEditor = null;
		/**
		 * 固定条件设置组件,liws0
		 */
		private Component cmp_fixedEditor = null;
		
		public static final String FIELD = "field"; 
		public static final String OPERATORS = "operators"; 
		public static final String VALUEEDITOR = "valueeditor"; 
		public static final String FIXEDDITOR = "fixededitor";
		
		/**
		 * 根据组件类别添加组件
		 */
		public void addLayoutComponent(String name, Component comp) {
			if(FIELD.equals(name))
			{
				cmp_field = comp;
			}
			else if(OPERATORS.equals(name))
			{
				cmp_operators = comp;
			}
			else if(VALUEEDITOR.equals(name))
			{
				cmp_valueEditor = comp;
			}
			else if(FIXEDDITOR.equals(name)){
				cmp_fixedEditor = comp;
			}
		}

		public void layoutContainer(Container target) {
		      synchronized (target.getTreeLock()) {
		    		Insets insets = target.getInsets();
		    		int top = insets.top+6;
		    		int left = insets.left;
		    		int right = target.getWidth() - insets.right;
		    		int comp_height = 22;
		    		
		    		//重新计算规则,operatorwidth = 70.其它两个共同分配,2/5;3/5
		    		int pad = 8;
		    		int operatorwidth = 100;
		    		int fixedWidth = 25;
		    		int width = right-left-3*pad;
		    		int fldwidth  = 2*((width-operatorwidth-fixedWidth)/5);
		    		int editorwidth  = 3*((width-operatorwidth-fixedWidth)/5);
		    		
		    		cmp_fixedEditor.setBounds(left, top, fixedWidth, comp_height);
		    		left += fixedWidth +5;
					cmp_field.setBounds(left, top, fldwidth, comp_height);
		    		left +=fldwidth;
		    		cmp_operators.setBounds(left,top,operatorwidth,comp_height);
		    		left +=operatorwidth;
		    		cmp_valueEditor.setBounds(left, top, editorwidth, comp_height);
		      }
		}

		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(400,37);
		}

		public Dimension preferredLayoutSize(Container parent) {
			return new Dimension(400,37);
		}

		public void removeLayoutComponent(Component comp) {
			
		}
	}
	
	/**
	 * 事件监听
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(getFrQueryOpersCBox())){
			//获得值面板
			getValuePanel();
		}
	}

}
