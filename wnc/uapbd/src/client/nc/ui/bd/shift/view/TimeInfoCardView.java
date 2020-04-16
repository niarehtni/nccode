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
 * ��εĿ�����Ϣҳǩ
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

	// ����ģ���listview���������ļ�������
	private BillListView listView;

	// ���пؼ���οռ�,���ﲻ����panel
	private List<JComponent> shiftCompList;
	private List<JComponent> mustInputList; // ��Ҫ��������
	// ��Ϣʱ������title--����panel
	private CardAreaPanel wrTRTitlePanel;
	// ��Ϣʱ�����panel
	private WrTimeRulePanel wrTimeRulePanel;

	// �ٵ����˹���panel--����panel
	private CardAreaPanel leRuleTitlePanel;
	// �ٵ����˹���panel
	private LeRulePanel leRulePanel;
	// �Ӱ����title��panel--����panel
	private CardAreaPanel overTitlePanel;
	// �Ӱ����panel
	private OverPanel overPanel;

	// ssx added on 2019-12-24
	private CardAreaPanel annLeaveTitlePanel;
	private AnnLeavePanel annLeavePanel;
	// end

	// formlayout����
	private CellConstraints cc;

	// ϵͳ�����¼���־��ջ ��ϵͳ�����¼��ĵط�push������setvalueʱ��clearDataʱ�ȣ�����Ϊ����˵���¼��Ĵ��������û��༭��ɵ�
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
			// ����ҳ�沼��
			// ��ColumnLayout��ԭ�򣺶��ڷ���panel����������չ������panel.setVisible(boolean)��ʵ��
			// ����Ҫ����������;

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

			// //======== �����У���Ϣʱ����� ========
			// mainPanel.add(getWrTRTitlePanel()/*, cc.xy(1, 3)*/);
			// {
			// //======== ˢ��ʱ�䡢����ʱ��panel:workPanel ========
			//
			// }
			// //ע��������panel��
			getWrTRTitlePanel().register(getWrTimeRulePanel());
			// //======== �����У���Ϣʱ�����panelpanel��WrTimeRulePanel ========
			// mainPanel.add(getWrTimeRulePanel());
			//
			// //======== �����У��ٵ�����title����panel��LeRuleTitlePanel ========
			// mainPanel.add(getLeRuleTitlePanel());
			//
			// //======== �����У��ٵ�����panel��leRulePanel ========
			getLeRuleTitlePanel().register(getLeRulePanel());
			// mainPanel.add(getLeRulePanel());
			//
			// //======== �Ӱ����title ========
			// mainPanel.add(getOverTitlePanel());
			getOverTitlePanel().register(getOverPanel());
			// //======== �Ӱ����panel ========
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
			// �Ӱ����
			getOverPanel().setEnabled();
			// �Ƿ��Զ�ͳ�ƿ�����ʱ
			getLeRulePanel().setEnabled();

			// ssx added on 2019-12-24
			getAnnLeavePanel().setEnabled();
			// end
		} else {
			// ������ɱ༭����Ҫ�����ϰ�ʱ���Ƿ����������Ƿ���ʾ�����ϰࡢ�����°�ʱ��
			// getWrTimeRulePanel().setIsotFlexibleText();
			// ��������̬������ʾ���пؼ��ı������
			if (CollectionUtils.isEmpty(shiftCompList))
				return;
			// ����Ƿǿգ���Ҫ����ǿշ���
			for (JComponent comp : shiftCompList) {
				if (comp instanceof UIRefPane) {
					((UIRefPane) comp).getUITextField().setShowMustInputHint(enabled);
				}
			}
		}
		if (CollectionUtils.isEmpty(mustInputList))
			return;
		// ����Ƿǿգ���Ҫ����ǿշ���
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
	 * ǿ����꽹���ƿ�
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
		// ������������Ϊ�����б���billCardPanel(����)����Ҫ����������
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
		/* @res "������" */), new DefaultConstEnum(1, ResHelper.getString("hrbd", "0hrbd0152")
		/* @res "δ����" */), new DefaultConstEnum(3, ResHelper.getString("hrbd", "0hrbd0148")
		/* @res "��ͣ��" */) };
	}

	public CardAreaPanel getWrTRTitlePanel() {
		if (wrTRTitlePanel == null) {
			wrTRTitlePanel = new CardAreaPanel(ResHelper.getString("hrbd", "0hrbd0186")
			/* @res "��Ϣʱ�����" */, new FormLayout("600"), this, true);
		}
		return wrTRTitlePanel;
	}

	public CardAreaPanel getLeRuleTitlePanel() {
		if (leRuleTitlePanel == null) {
			leRuleTitlePanel = new CardAreaPanel(ResHelper.getString("hrbd", "0hrbd0187")
			/* @res "�ٵ����˹���" */, new FormLayout("600"), this, true);
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
			/* @res "�Ӱ����" */, new FormLayout("600"), this, true);
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
			annLeaveTitlePanel = new CardAreaPanel("��Ȳ��ݹ���", new FormLayout("600"), this, true);
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
			stopEditing(); // ��꽹���ƿ��󣬼�����Ӧ��Ϣ����ȡֵ
		} catch (ParseException e) {
			Logger.error(e.getMessage(), e);
		}
		ShiftVO mainVo = aggVo.getShiftVO();
		if (getModel() != null) {
			mainVo.setPk_group(getModel().getContext().getPk_group());
		}

		// ��Ϣʱ�����
		getWrTimeRulePanel().getValue(aggVo);
		// �ٵ����˹���
		getLeRulePanel().getValue(mainVo);
		// �Ӱ����
		getOverPanel().getValue(mainVo);

		// ssx added on 2019-12-24
		getAnnLeavePanel().getValue(mainVo);
		// end
		return aggVo;
	}

	// ����ʱ���ԭҳ�����û��Ƿ�༭���ı�־������
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
		// ��Ϣʱ��
		getWrTimeRulePanel().setValue(aggVo);
		// ---- �ٵ����˹���----
		getLeRulePanel().setValue(mainVo);
		// ----�Ӱ����
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