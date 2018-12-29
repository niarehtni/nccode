package nc.ui.wa.psndocwadoc.view;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import nc.ui.bd.ref.model.DefdocGridRefModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIRefPane;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/**
 *  * 自定义对话框 对话框包括一个label、一个文本框和2个按钮。如果需要实时校验输入值那么再实* * 现:  PropertyChangeListener,
 */
public class GroupInsAndDateChooseDialog extends UIDialog implements PropertyChangeListener,ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Container conter;
	
	private String pk_org;

	String reason;
	
	
	private UILabel laberGroupIns = null;
	
	private UIRefPane refGroupIns = null;
	
	private UILabel laberAddDate = null;

	private UIRefPane refAddDate = null;
	
	
	private UFDate addDate = new UFDate();
	
	private String pk_group_ins = null;
	
	//private boolean clossflag = true;

	// 按钮
	JButton okButton;
	JButton cancelButton;

	/**
	 * 构造函数
	 * @param conter 上层父窗口
	 * @param title 题目
	 * @param desc 描述
	 * @param t 每次打开是否刷新窗口?
	 */
	public GroupInsAndDateChooseDialog(Container conter,String title,String desc, boolean t,String pk_org) {
		// 添加Label和输入文本框
		super(conter, t);
		this.conter = conter;
		this.pk_org = pk_org;
		setTitle(title);
		setReason(desc);
		initPanel();
	}

	private void initPanel() {
		JPanel p1 = new JPanel();
		GridLayout gridLayout = new GridLayout(3,2);
		//3行两列
		p1.setLayout(gridLayout);
		
		p1.add(new UILabel());
		p1.add(new UILabel());
		
		p1.add(getGroupInsLabor(),BorderLayout.WEST);
		p1.add(getGroupInsRef(),BorderLayout.WEST);
		
		p1.add(getAddDateLaber(),BorderLayout.CENTER);
		p1.add(getAddDateRef(),BorderLayout.CENTER);
		
		getContentPane().add("Center", p1);

		// 添加确定和取消按钮
		JPanel p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.RIGHT));
		p2.add(getBtnOk());
		p2.add(getCancelButton());
		getContentPane().add("South", p2);

		// 调整对话框布局大小
		pack();
		setSize(250, 100);
		this.setModal(true);
		
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		try {
			if( getGroupInsRef().getValueObj() != null 
					&& getAddDateRef().getValueObj() != null){
				new UFDate(String.valueOf(getAddDateRef().getValueObj()));
				getBtnOk().setEnabled(true);
				return;
			}
			
		} catch (Exception ex) {
			ExceptionUtils.wrappBusinessException(ex.getMessage());
		}
		getBtnOk().setEnabled(false);
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if(source == getBtnOk()){
			if(getGroupInsRef().getValueObj() != null || getAddDateRef().getValueObj() != null){
				try{
					setAddDate(new UFDate(String.valueOf(getAddDateRef().getValueObj())));
					setPk_group_ins(String.valueOf(((String [])getGroupInsRef().getValueObj())[0]));
					this.dispose();
				}catch(Exception e){
					MessageDialog.showErrorDlg(conter, "錯誤", "數據格式錯誤!");
				}
			}else{
				MessageDialog.showErrorDlg(conter, "錯誤", "險种或加保時間不能為空!");
			}
			return;
		}
		this.dispose();
	}

	public JButton getBtnOk(){
		if(null == okButton){
			okButton = new JButton("确 定");
			okButton.addActionListener(this);
			okButton.setEnabled(false);
		}
		return okButton;
	}
	public JButton getCancelButton(){
		if(null == cancelButton){
			cancelButton = new JButton("取 消");
			cancelButton.addActionListener(this);
		}
		return cancelButton;
	}
	
	

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	private UILabel getGroupInsLabor() {
		if (laberGroupIns == null) {
			laberGroupIns = new UILabel();
			laberGroupIns.setText("    新增險种");
		}
		return laberGroupIns;
	}
	/**
	 * 自定义参照
	 * @return
	 */
	private UIRefPane getGroupInsRef() {
		if (refGroupIns == null) {
			refGroupIns = new UIRefPane();
			DefdocGridRefModel model = new DefdocGridRefModel();
			//设置bd_refinfo中的refclass
			refGroupIns.setRefModel(model);
			//设置名称(要和bd_refinfo中的一致)
			refGroupIns.setRefNodeName("团保险种(自定义档案)");
			refGroupIns.setPk_org(pk_org);
			//refGroupIns.set
			refGroupIns.setVisible(true);
			refGroupIns.setPreferredSize(new Dimension(50, 20));
			refGroupIns.setButtonFireEvent(true);
			refGroupIns.addPropertyChangeListener(this);
		}
		return refGroupIns;
	}

	private UILabel getAddDateLaber() {
		if (laberAddDate == null) {
			laberAddDate = new UILabel();
			laberAddDate.setText("    加保日期");
		}
		return laberAddDate;
	}

	public UIRefPane getAddDateRef() {
		if (refAddDate == null) {
			refAddDate = new UIRefPane("日期");
			refAddDate.setVisible(true);
			refAddDate.setValue(new UFDate().toStdString());
			refAddDate.setPreferredSize(new Dimension(50, 20));
			refAddDate.setButtonFireEvent(true);
			refAddDate.addPropertyChangeListener(this);
		}
		return refAddDate;
	}

	public UFDate getAddDate() {
		return addDate;
	}

	public void setAddDate(UFDate addDate) {
		this.addDate = addDate;
	}

	public String getPk_group_ins() {
		return pk_group_ins;
	}

	public void setPk_group_ins(String pk_group_ins) {
		this.pk_group_ins = pk_group_ins;
	}

}