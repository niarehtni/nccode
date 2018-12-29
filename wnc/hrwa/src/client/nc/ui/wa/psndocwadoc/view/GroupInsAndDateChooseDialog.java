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
 *  * �Զ���Ի��� �Ի������һ��label��һ���ı����2����ť�������ҪʵʱУ������ֵ��ô��ʵ* * ��:  PropertyChangeListener,
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

	// ��ť
	JButton okButton;
	JButton cancelButton;

	/**
	 * ���캯��
	 * @param conter �ϲ㸸����
	 * @param title ��Ŀ
	 * @param desc ����
	 * @param t ÿ�δ��Ƿ�ˢ�´���?
	 */
	public GroupInsAndDateChooseDialog(Container conter,String title,String desc, boolean t,String pk_org) {
		// ���Label�������ı���
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
		//3������
		p1.setLayout(gridLayout);
		
		p1.add(new UILabel());
		p1.add(new UILabel());
		
		p1.add(getGroupInsLabor(),BorderLayout.WEST);
		p1.add(getGroupInsRef(),BorderLayout.WEST);
		
		p1.add(getAddDateLaber(),BorderLayout.CENTER);
		p1.add(getAddDateRef(),BorderLayout.CENTER);
		
		getContentPane().add("Center", p1);

		// ���ȷ����ȡ����ť
		JPanel p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.RIGHT));
		p2.add(getBtnOk());
		p2.add(getCancelButton());
		getContentPane().add("South", p2);

		// �����Ի��򲼾ִ�С
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
					MessageDialog.showErrorDlg(conter, "�e�`", "������ʽ�e�`!");
				}
			}else{
				MessageDialog.showErrorDlg(conter, "�e�`", "�U�ֻ�ӱ��r�g���ܞ��!");
			}
			return;
		}
		this.dispose();
	}

	public JButton getBtnOk(){
		if(null == okButton){
			okButton = new JButton("ȷ ��");
			okButton.addActionListener(this);
			okButton.setEnabled(false);
		}
		return okButton;
	}
	public JButton getCancelButton(){
		if(null == cancelButton){
			cancelButton = new JButton("ȡ ��");
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
			laberGroupIns.setText("    �����U��");
		}
		return laberGroupIns;
	}
	/**
	 * �Զ������
	 * @return
	 */
	private UIRefPane getGroupInsRef() {
		if (refGroupIns == null) {
			refGroupIns = new UIRefPane();
			DefdocGridRefModel model = new DefdocGridRefModel();
			//����bd_refinfo�е�refclass
			refGroupIns.setRefModel(model);
			//��������(Ҫ��bd_refinfo�е�һ��)
			refGroupIns.setRefNodeName("�ű�����(�Զ��嵵��)");
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
			laberAddDate.setText("    �ӱ�����");
		}
		return laberAddDate;
	}

	public UIRefPane getAddDateRef() {
		if (refAddDate == null) {
			refAddDate = new UIRefPane("����");
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