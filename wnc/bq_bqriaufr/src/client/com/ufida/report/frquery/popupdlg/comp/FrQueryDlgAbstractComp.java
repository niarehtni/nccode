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
 * �����ѯ�Ի���-��ѯ�����༭���
 * @author yanchm
 */
public abstract class FrQueryDlgAbstractComp extends UIPanel  implements ActionListener{
	private static final long serialVersionUID = 1L;

	public static final String LWQUERYPANEL_CHANGED = "frQueryPanelChanged";

	/**
	 *  ��ѯ��
	 */
	private FrQueryItem frQueryItem = null;
	
	/**
	 * ��ѯ���Ӧ��label
	 */
	private UILabel frQueryItemLbl = null;

	/**
	 * �����������б�
	 */
	private FrQueryOpersCBox frQueryOpersCBox = null;
	
	/**
	 * ���ֵС��ť
	 */
	private UIButton clearValueBtn = null;
	
	/**
	 * ��������壺��������������������ֵС��ť
	 */
	private UIPanel operatorPanel = null;
	
	/**
	 * ֵ���(�������ֵС��ť)
	 */
	protected UIPanel valuePanel = null;
	
	/** �̶�������壨����һ�����ù̶������İ�ť��һ�����ص�label����liws0*/
	private JPanel fixedPanel = null;
	/** ���ù̶������İ�ť*/
	private JButton fixedButton = null;
	/** ��ʾ�Ƿ�����Ϊ�̶�������label*/
	private JLabel fixedLabel = null;
	private Icon fixedIcon = null;
	private Icon unFixedIcon = null;
	/** ���ù̶������İ�ť���õ�ͼƬ����ʾ�ǹ̶�����*/
	private Icon getFixedIcon(){
		if(fixedIcon == null){
			fixedIcon = ResourceManager.createIcon("images/frquery/fixed.png");
		}
		return fixedIcon;
	}
	/** ���ù̶������İ�ť���õ�ͼƬ����ʾ���ǹ̶�����*/
	private Icon getUnFixedIcon(){
		if(unFixedIcon == null){
			unFixedIcon = ResourceManager.createIcon("images/frquery/unfixed.png");
		}
		return unFixedIcon;
	}
	/** ���ù̶������İ�ť  �󶨵ļ�����*/
	private FixBtnListener fixBtnListener = null;

	/**
	 * ���췽��
	 * @param frQueryItem
	 */
	public FrQueryDlgAbstractComp(FrQueryItem frQueryItem) {
		if (frQueryItem == null) {
			return;
		}
		this.frQueryItem = frQueryItem;
		initComponents();// ��ʼ�����
	}

	/**
	 * ��ʼ�����
	 * 
	 */
	private void initComponents() {
		setOpaque(true);
		//���ò���Ϊ��ѯ�����༭�������
		setLayout(new FilterEditorLayout());
		setPreferredSize(new Dimension(340,37));
		this.add(FilterEditorLayout.FIXEDDITOR , getFixedPanel());//add by liws0����ӹ̶��������
		this.add(FilterEditorLayout.FIELD , getFrQueryItemLbl());
		this.add(FilterEditorLayout.OPERATORS ,getOperatorPanel());
		getFrQueryOpersCBox().addActionListener(this);

	}

	/**
	 * ���ù̶������İ�ť  �󶨵ļ�������
	 * @author liws0
	 *
	 */
	class FixBtnListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			FrQueryAbstractComp valueEditComp = getValueEditComp();
			if(getFixedLabel().getText().equals(FrFilterCaseDefine.IS_FIXED)){
				//�����ǹ̶���������Ϊ�ǹ̶������Ĵ���
				doUnFixed(valueEditComp, false);
			}else if(getFixedLabel().getText().equals(FrFilterCaseDefine.IS_NOT_FIXED)){
				//�����Ƿǹ̶���������Ϊ�̶������Ĵ���
				doFixed(valueEditComp, false);
			}
		}
		
		/**
		 * ����Ϊ�̶������Ĵ���
		 * @param valueEditComp
		 */
		public void doFixed(FrQueryAbstractComp valueEditComp, boolean isPub) {
			//TODO 1������lableֵ
			getFixedLabel().setText(FrFilterCaseDefine.IS_FIXED);
			//2���ı�fixedButton�е�iconͼƬΪ ����
			getFixedButton().setIcon(getFixedIcon());
			getFixedButton().setToolTipText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-1883")/*@res "ȡ���̶�����"*/);
			//3������������Ϊ ������״̬
			getFrQueryOpersCBox().setEnabled(false);
			getClearValueBtn().setEnabled(false);
			valueEditComp.setCompEnable(false);
			//add by liws0. ����Ƿ�����ڵ��б༭��
			if(isPub){
				getFixedButton().setEnabled(false);
			}
		}

		/**
		 * ����Ϊ�ǹ̶������Ĵ���
		 * @param valueEditComp
		 */
		public void doUnFixed(FrQueryAbstractComp valueEditComp, boolean isPub) {
			//TODO 1������lableֵ
			getFixedLabel().setText(FrFilterCaseDefine.IS_NOT_FIXED);
			//2���ı�fixedButton�е�iconͼƬΪ ����
			getFixedButton().setIcon(getUnFixedIcon());
			getFixedButton().setToolTipText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-1884")/*@res "����Ϊ�̶�����"*/);
			//3������������Ϊ ����״̬
			getFrQueryOpersCBox().setEnabled(true);
			getClearValueBtn().setEnabled(true);
			valueEditComp.setCompEnable(true);
			//add by liws0. ����Ƿ�����ڵ��б༭��
			if(isPub){
				getFixedButton().setEnabled(false);
			}
		}
		
	}
	
	/**
	 * ���  ���ù̶������İ�ť���󶨵ļ�����
	 * @author liws0
	 */
	private FixBtnListener getFixBtnListener() {
		if(fixBtnListener == null){
			fixBtnListener = new FixBtnListener();
		}
		return fixBtnListener;
	}
	
	/**
	 * ����Ϊ�̶������Ĵ���
	 * @author liws0
	 */
	public void doFixed(boolean isPub){
		getFixBtnListener().doFixed(getValueEditComp(), isPub);
	}
	
	/**
	 * ����Ϊ�ǹ̶������Ĵ���
	 * @author liws0
	 */
	public void doUnFixed(boolean isPub){
		getFixBtnListener().doUnFixed(getValueEditComp(), isPub);
	}
	
	
	/**
	 * ���  �̶��������
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
	 * ���ù̶������İ�ť
	 * @return
	 */
	private JButton getFixedButton(){
		if(fixedButton == null){
			fixedButton = new JButton(getUnFixedIcon());
			fixedButton.setContentAreaFilled(false);	//���ð�ť͸����
		}
		return fixedButton;
	}
	
	/**
	 * ��ʾ�Ƿ�����Ϊ�̶�������label
	 * @return
	 */
	public JLabel getFixedLabel(){
		if(fixedLabel == null){
			fixedLabel = new JLabel(FrFilterCaseDefine.IS_NOT_FIXED);//Ĭ��Ϊ�ǹ̶�����
			fixedLabel.setVisible(false); //����label
		}
		return fixedLabel;
	}
	
	/**
	 * �õ������������б�
	 * @return UIComboBox
	 */
	public UIComboBox getFrQueryOpersCBox() {
		if (frQueryOpersCBox == null) {
			frQueryOpersCBox = new FrQueryOpersCBox();
			frQueryOpersCBox.setPreferredSize(new Dimension(80, 22));
			frQueryOpersCBox.setOpaque(false);
			frQueryOpersCBox.setUI(new ComboBoxUI());
			frQueryOpersCBox.setBorder(new EmptyBorder(0, 0, 0, 0));
			//���²����������б�
			updateFrQueryOpersCBoxItems();
		}
		return frQueryOpersCBox;
	}

	/**
	 * ���²����������б�
	 */
	public void updateFrQueryOpersCBoxItems() {
		frQueryOpersCBox.removeAllItems();
		frQueryOpersCBox.addItems(getSupportedOpers());
	}
	
	/**
	 * ��øò�ѯ��֧�ֵĲ������б�
	 */
	public Operator[] getSupportedOpers() {
		Operator[] suppoprs = frQueryItem.getSupportedOpers();
		if (suppoprs == null) {
			suppoprs = new Operator[] { Operator.IN };
		}
		return suppoprs;
	}
	
	/**
	 * ���õ�ǰѡ�еĲ�����
	 * @param curOper����ǰ������
	 */
	public void setOperator(Operator curOper){
		getFrQueryOpersCBox().setSelectedItem(curOper);
	}
	
	/**
	 * ���ò�ѯֵ
	 * @param values����ѯֵ�б�
	 */
	public abstract void setDvalues(Operator curOper, List<DValue> values);
	
	/**
	 * ��ò�ѯ���Ӧ�ı�ǩ
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
	 * ��ò�������壺����������������������ť
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
		// ��ѯ�����Ӱ����Լ�����
		getValueEditComp().getFrQueryItem().addPropertyChangeListener(getValueEditComp());
		return valuePanel;
	}
	
	protected abstract FrQueryAbstractComp getValueEditComp ();

	private UIButton getClearValueBtn(){
		if(clearValueBtn == null){
			clearValueBtn = new UIButton();
			// ���ͼ��
			Icon icon = ResourceManager
					.createIcon("/images/frquery/eraser.gif");
			clearValueBtn.setIcon(icon);
			clearValueBtn.setPreferredSize(new Dimension(20,22));
			clearValueBtn.setUI(new UIRefButtonUI());
			clearValueBtn.setToolTipText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("1413006_0", "01413006-1411")/* @res "���ֵ" */);
			clearValueBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					//��ղ�ѯ�����¼��Ĳ�ѯ����
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
	 * ��ò�ѯ��
	 */
	public FrQueryItem getFrQueryItem() {
		return frQueryItem;
	}

	/**
	 * ��õ�ǰѡ��Ĳ�����
	 */
	public Operator getOperator() {
		Operator o = (Operator) getFrQueryOpersCBox().getSelectedItem() ;
		if(o != null){
			return o ;
		}
		return Operator.IN;
	}
	
	/**
	 * �������Ĳ�ѯ����ֵ
	 */
	public abstract DValue [] getDValue();
	

	/**
	 * ��ѯ�����༭������֣������������������
	 * @author yanchm
	 *
	 */
	public static class FilterEditorLayout implements LayoutManager
	{
		/**
		 * ��ѯ����������
		 */
		private Component cmp_field = null;
		/**
		 * ������
		 */
		private Component cmp_operators = null;
		/**
		 * ֵ�༭���
		 */
		private Component cmp_valueEditor = null;
		/**
		 * �̶������������,liws0
		 */
		private Component cmp_fixedEditor = null;
		
		public static final String FIELD = "field"; 
		public static final String OPERATORS = "operators"; 
		public static final String VALUEEDITOR = "valueeditor"; 
		public static final String FIXEDDITOR = "fixededitor";
		
		/**
		 * ����������������
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
		    		
		    		//���¼������,operatorwidth = 70.����������ͬ����,2/5;3/5
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
	 * �¼�����
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(getFrQueryOpersCBox())){
			//���ֵ���
			getValuePanel();
		}
	}

}
