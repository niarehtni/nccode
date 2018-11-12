package nc.ui.ta.timeitem.view;

import org.apache.commons.lang.StringUtils;
import java.awt.BorderLayout;

import nc.bs.logging.Logger;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.ta.timeitem.model.TimeItemAppModel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.IEditor;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.MultiLangText;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.basedoc.RefDefVOWrapper;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;

/**
 * �������cardView ���������Ķ��塢����������cardPanel���������ȡ
 * 
 * @author yucheng
 * 
 */
@SuppressWarnings("serial")
public class TimeItemCardView extends UIPanel implements AppEventListener,
		IEditor {

	private TimeItemAppModel model;
	private TimeItemCardPanel cardPanel;

	public void initUI() {
		initialize();
	}

	public void initialize() {
		try {
			UIScrollPane topPane = new UIScrollPane();
			topPane.setViewportView(getCardPanel());
			getCardPanel().setModel(getModel());
			setLayout(new BorderLayout());
			add(topPane, BorderLayout.CENTER);
			setSize(400, 500);
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	}

	@Override
	public void handleEvent(AppEvent event) {
		if ((AppEventConst.MODEL_INITIALIZED == event.getType())) {
			getCardPanel().setEnabled(false);
			synchronizeDataFromModel();
		}
		// ��������Ȱ�ťʱ����
		else if (AppEventConst.UISTATE_CHANGED == event.getType()) {
			if (model.getUiState() == UIState.EDIT) {
				// onEdit();
				onUIStateChange(model.getContext());
			} else if (model.getUiState() == UIState.ADD) {
				onAdd();
				onUIStateChange(model.getContext());
			} else {
				getCardPanel().setEnabled(false);
				synchronizeDataFromModel();
			}

		}
		// ���б�ѡ���¼�
		else if (AppEventConst.SELECTION_CHANGED == event.getType()) {
			synchronizeDataFromModel();
		}
	}

	/**
	 * ��������
	 */
	public void onAdd() {
		getCardPanel().setEnabled(true);
		setDefaultValue();
		// ����ʱ����Ĭ���ڱ��봦
		getCardPanel().getTimeitemcode().requestFocus();
	}

	/**
	 * �༭����
	 */
	public void onEdit(boolean isRefed) {
		getCardPanel().setEnabled(true);
		checkBeforeEdit(model.getSelectedData());
		// �������𱻵��ݻ������������
		if (!isRefed)
			return;
		processCompEnableStatusWhenUsed();
	}

	/**
	 * �������Ѿ������õ�ʱ����Щ�ؼ��ǲ����޸ĵģ����簴�컹�ǰ�Сʱ
	 */
	protected void processCompEnableStatusWhenUsed() {
		getCardPanel().getTimeitemunit().setEnabled(false);
	}

	/**
	 * ����Ƿ�����
	 * 
	 * @param obj
	 */
	public void checkBeforeEdit(Object obj) {
		if (!(obj instanceof TimeItemCopyVO))
			return;

		TimeItemCopyVO vo = (TimeItemCopyVO) obj;
		// ������֯�ж��Ƿ����� �� ΪϵͳԤ��
		if ((!vo.getPk_deforg().equals(vo.getPk_org())) || vo.getIspredef().booleanValue())
			getCardPanel().setDefDisEnabled();
	}

	/**
	 * ���ò������ڸ�������ʵ��
	 * 
	 * @param objs
	 * @return
	 * @throws BusinessException
	 */
	public TimeItemCopyVO[] onRef(RefDefVOWrapper<TimeItemVO> objs) throws BusinessException {
		return null;
	}

	/**
	 * ����context��ѯ������Ϣ���ڸ�������ʵ��
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public RefDefVOWrapper<TimeItemVO> queryRefDefVOs() throws BusinessException {
		return null;
	}

	/**
	 * ��ǰ��֯���������ÿ������ϼ�ͬ���������ڸ�������ʵ��(��ʱδʹ�ã�Ԥ��)
	 * 
	 * @throws BusinessException
	 */
	public void onSynchronize() throws BusinessException {

	}

	// MOD (̨���·���) ssx added on 2018-06-25
	public void onUIStateChange(LoginContext context) {

	} // end

	protected void synchronizeDataFromModel() {
		Object selectedData = model.getSelectedData();
		setValue(selectedData);
	}

	/**
	 * ����ʱ����Ĭ��ֵ���ڸ�������ʵ��
	 */
	public void setDefaultValue(){}
	

	/**
	 * ȡ��������Ĭ��ֵ ��ָ�����͵�copyVO������
	 * 
	 * @param vo
	 * @return
	 */
	public TimeItemCopyVO getPubDefaultValue(TimeItemCopyVO vo) {
		vo.setOvertimetorest(new UFDouble(100));
		vo.setEnablestate(IPubEnumConst.ENABLESTATE_ENABLE);
		vo.setTimeitemunit(TimeItemCopyVO.TIMEITEMUNIT_DAY);
		vo.setLeavesetperiod(TimeItemCopyVO.LEAVESETPERIOD_YEAR);
		vo.setLeavesettlement(TimeItemCopyVO.LEAVESETTLEMENT_DROP);
		vo.setLeavescale(TimeItemCopyVO.LEAVESCALE_YEAR);
		vo.setRoundmode(TimeItemCopyVO.ROUNDMODE_MID);
		vo.setTimeunit(new UFDouble(0));
		vo.setGxcomtype(TimeItemCopyVO.GXCOMTYPE_NOTLEAVE);
		vo.setCalculatetype(TimeItemCopyVO.CALCULATETYPE_TOHALF);
		vo.setIsLeavelimit(UFBoolean.FALSE);
		vo.setIsRestrictlimit(UFBoolean.FALSE);
		vo.setIsLeaveplan(UFBoolean.FALSE);
		vo.setIsleaveapptimelimit(UFBoolean.FALSE);
		vo.setIsinterwt(UFBoolean.FALSE);
		vo.setLeaveapptimelimit(0);
		vo.setLeaveextendcount(0);
		vo.setConvertrule(TimeItemCopyVO.CONVERTRULE_DAY);
		vo.setPk_defgroup(getModel().getContext().getPk_group());
		vo.setPk_deforg(getModel().getContext().getPk_org());
		vo.setDefenablestate(IPubEnumConst.ENABLESTATE_ENABLE);
		vo.setIslactation(UFBoolean.FALSE);
		vo.setIspredef(UFBoolean.FALSE);
		vo.setIsleave(UFBoolean.FALSE);
		vo.setIsspecialrest(UFBoolean.FALSE);

		// ssx added on 20180501
		// for Taiwan New Law Requirements
		// �Ƿ�����Ա�����о���ת��
		vo.setIsstuffdecidecomp(UFBoolean.FALSE);
		// ���ռӰ�����?Сʱ
		vo.setDaylimit(UFDouble.ZERO_DBL);
		// �Ƿ������¼Ӱ�����ͳ��
		vo.setIsincludewithlimit(UFBoolean.FALSE);
		// �׶μӰ����ʱ��
		vo.setEffectivehours(UFDouble.ZERO_DBL);
		// �Ӱ೬��?Сʱ,�۳�?Сʱ
		vo.setDeductlowhours(UFDouble.ZERO_DBL);
		vo.setDeductminutes(UFDouble.ZERO_DBL);
		// �Ӱ�ֶ�����
		vo.setPk_segrule(null);
		//

		return vo;
	}

	/**
	 * ȡ�ڽ��������õ�ֵ
	 */
	@Override
	public Object getValue() {
		return null;
	}

	/**
	 * ȡ���������ڽ��������õ�ֵ ��ָ�����͵�copyVO������
	 * 
	 * @param vo
	 * @return
	 */
	public TimeItemCopyVO getPubValue(TimeItemCopyVO vo) {
		TimeItemCardPanel cardPanel = getCardPanel();
		vo.setTimeitemcode(cardPanel.getTimeitemcode().getText());
		MultiLangText text = cardPanel.getTimeitemname().getMultiLangText();
		if (text != null) {
			vo.setTimeitemname(text.getText());
			vo.setTimeitemname2(text.getText2());
			vo.setTimeitemname3(text.getText3());
			vo.setTimeitemname4(text.getText4());
			vo.setTimeitemname5(text.getText5());
			vo.setTimeitemname6(text.getText6());
		}
		vo.setEnablestate((Integer) cardPanel.getEnablestate().getSelectdItemValue());
		vo.setTimeitemnote(cardPanel.getTimeitemnote().getText());
		vo.setTimeitemunit((Integer) cardPanel.getTimeitemunit().getSelectdItemValue());
		vo.setRoundmode((Integer) cardPanel.getRoundmode().getSelectdItemValue());

		vo.setPk_timeitemcopy(cardPanel.getPk_timeitemcopy());
		vo.setPk_timeitem(cardPanel.getPk_timeitem());
		vo.setPk_group(getModel().getContext().getPk_group());
		vo.setPk_org(getModel().getContext().getPk_org());
		vo.setPk_defgroup(cardPanel.getPk_defgroup());
		vo.setPk_deforg(cardPanel.getPk_deforg());
		vo.setDefenablestate(cardPanel.getDefenablestate());
		vo.setIspredef(cardPanel.getIspredef());
		vo.setIslactation(cardPanel.getIslactation());
		vo.setCreator(cardPanel.getCreator().getRefPK());
		vo.setModifier(cardPanel.getModifier().getRefPK());
		if (!StringUtils.isBlank(cardPanel.getCreationtime().getText())) {
			vo.setCreationtime(new UFDateTime(cardPanel.getCreationtime().getText()));
		}
		if (!StringUtils.isBlank(cardPanel.getModifiedtime().getText())) {
			vo.setModifiedtime(new UFDateTime(cardPanel.getModifiedtime().getText()));
		}
		vo.setIssynchronized(UFBoolean.TRUE);
		vo.setIsLeavelimit(UFBoolean.FALSE);
		vo.setIsRestrictlimit(UFBoolean.FALSE);
		vo.setIsLeaveplan(UFBoolean.FALSE);
		vo.setIsspecialrest(UFBoolean.FALSE);
		vo.setIsinterwt(UFBoolean.FALSE);
		vo.setIsleaveapptimelimit(UFBoolean.FALSE);
		vo.setIsleave(UFBoolean.FALSE);
		vo.setGxcomtype(0);
		vo.setTimeunit(UFDouble.ZERO_DBL);
		vo.setLeaveextendcount(0);

		vo.setTs(cardPanel.getTs());
		vo.setDefTS(cardPanel.getDefTS());

		return vo;
	}

	/**
	 * ����һЩ����������cardPanel�е���ʾֵ
	 */
	@Override
	public void setValue(Object object) {
		if (!(object instanceof TimeItemCopyVO)) {
			setDefaultValue();
			return;
		}
		TimeItemCardPanel cardPanel = getCardPanel();
		cardPanel.clearData();

		TimeItemCopyVO vo = (TimeItemCopyVO) object;

		cardPanel.setPk_timeitem(vo.getPk_timeitem());
		cardPanel.setPk_timeitemcopy(vo.getPk_timeitemcopy());
		cardPanel.getTimeitemcode().setText(vo.getTimeitemcode());
		MultiLangText text = new MultiLangText();
		text.setText(vo.getTimeitemname());
		text.setText2(vo.getTimeitemname2());
		text.setText3(vo.getTimeitemname3());
		text.setText4(vo.getTimeitemname4());
		text.setText5(vo.getTimeitemname5());
		text.setText6(vo.getTimeitemname6());
		cardPanel.getTimeitemname().setMultiLangText(text);
		cardPanel.getEnablestate().setSelectedItem(vo.getEnablestate());
		cardPanel.getTimeitemnote().setText(vo.getTimeitemnote());
		cardPanel.getTimeitemunit().setSelectedItem(vo.getTimeitemunit());
		cardPanel.getRoundmode().setSelectedItem(vo.getRoundmode());

		cardPanel.setPk_defgroup(vo.getPk_defgroup());
		cardPanel.setPk_deforg(vo.getPk_deforg());
		cardPanel.setDefenablestate(vo.getDefenablestate());
		cardPanel.setIspredef(vo.getIspredef());
		cardPanel.setIslactation(vo.getIslactation());
		cardPanel.getCreator().setPK(vo.getCreator());
		cardPanel.getModifier().setPK(vo.getModifier());
		cardPanel.setTs(vo.getTs());
		cardPanel.setDefTS(vo.getDefTS());
		if (vo.getCreationtime() != null)
			cardPanel.getCreationtime().setText(vo.getCreationtime().toString());
		if (vo.getModifiedtime() != null)
			cardPanel.getModifiedtime().setText(vo.getModifiedtime().toString());
	}

	public TimeItemAppModel getModel() {
		return model;
	}

	public void setModel(TimeItemAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public TimeItemCardPanel getCardPanel() {
		return cardPanel;
	}

	public void setCardPanel(TimeItemCardPanel cardPanel) {
		this.cardPanel = cardPanel;
	}
}