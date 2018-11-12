package nc.ui.ta.timeitem.view;

import nc.hr.utils.CommonUtils;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.basedoc.RefDefVOWrapper;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;

import org.apache.commons.lang.StringUtils;

/**
 * �ݼ���� cardView
 * @author yucheng
 *
 */
@SuppressWarnings("serial")
public class LeaveTypeCardView extends TimeItemCardView {

	private LeaveTypeCardPanel cardPanel;

	@Override
	public TimeItemCopyVO[] onRef(RefDefVOWrapper<TimeItemVO> objs) throws BusinessException {
		return getModel().refLeaveTypes(getModel().getContext(), objs);
	}

	@Override
	public void onSynchronize() throws BusinessException {
		getModel().synchronizeLeaveTypes(getModel().getContext());
	}
	
	@Override
	public RefDefVOWrapper<TimeItemVO> queryRefDefVOs() throws BusinessException {
		return getModel().queryLeaveRefDefVOs(getModel().getContext());
	}
	
	@Override
	public void checkBeforeEdit(Object obj){
		super.checkBeforeEdit(obj);
		if(!(obj instanceof LeaveTypeCopyVO))
			return;
		LeaveTypeCopyVO vo = (LeaveTypeCopyVO)obj;
		//�ж��Ƿ����
		if(vo.getIslactation().booleanValue())
			getCardPanel().getTimeitemunit().setEnabled(false);
		processBeforeInit(vo);
	}
	
	@Override
	public void setDefaultValue() {
		LeaveTypeCopyVO vo = new LeaveTypeCopyVO();
		vo = (LeaveTypeCopyVO)getPubDefaultValue(vo);
		setValue(vo);
		//�����Ƿ�����������ʾĬ����ʾ
		vo.setIshrssshow(UFBoolean.TRUE);
		processBeforeInit(vo);
	}
	
	/**
	 * ���������޸�ʱ�Բ���������пɱ༭�Գ�ʼ��
	 * @param vo
	 */
	private void processBeforeInit(LeaveTypeCopyVO vo){
		//�Ƿ�����ݼ���������
		getCardPanel().getLeaveapptimelimit().setEnabled(vo.getIsleaveapptimelimit().booleanValue());
		//�Ƿ�����ݼ�ʱ��
		getCardPanel().getIsrestrictlimit().setEnabled(vo.getIsLeavelimit().booleanValue());
		if(null != vo.getIsLeaveplan()){
			if(vo.getIsLeaveplan().booleanValue()){
				getCardPanel().getFormulaset().setEnabled(false);
			}else{
				getCardPanel().getFormulaset().setEnabled(true);
			}
		}else{
			getCardPanel().getFormulaset().setEnabled(true);
		}
		// �������ݼ�ʱ��Ҳ�������ù��� 2011-03-23
//		getCardPanel().getFormulaset().setEnabled(vo.getIsLeavelimit().booleanValue());
		// �Ƿ������Чʱ��
		getCardPanel().getLeaveextendcount().setEnabled(vo.getIsleave().booleanValue());

		//��֯���Ľ����Ƿ�ת�Ʋ��ɱ༭
		if(getModel().getContext().getNodeType().equals(NODE_TYPE.ORG_NODE))
			getCardPanel().getIsLeaveTransfer().setEnabled(false);
	}

	/**
	 * ���ý����ʼֵ
	 * �������������н��沿������ɼ��Ե�����
	 */
	private Object rool;
	private Object fixed;
	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object object) {
		super.setValue(object);
		if(!(object instanceof LeaveTypeCopyVO))
			return;
		LeaveTypeCopyVO vo = (LeaveTypeCopyVO)object;
		LeaveTypeCardPanel cardPanel = getCardPanel();
		cardPanel.getLeavesetperiod().setSelectedItem(vo.getLeavesetperiod());
		cardPanel.getLeavesettlement().setSelectedItem(vo.getLeavesettlement());
		cardPanel.getLeavescale().setSelectedItem(vo.getLeavescale());
		cardPanel.getTimeunit().setValue(vo.getTimeunit());
		cardPanel.getLeavemax().setValue(vo.getLeavemax());//ת��������
		cardPanel.getGxcomtype().setSelectedItem(vo.getGxcomtype());
		cardPanel.getIsLeavelimit().setSelected(vo.getIsLeavelimit().booleanValue());
		cardPanel.getIsrestrictlimit().setSelected(vo.getIsRestrictlimit().booleanValue());
		if(null != vo.getIsLeaveplan()){
			if(vo.getIsLeaveplan().booleanValue()){
				cardPanel.getIsleaveplan().setSelected(true);
			}else{
				cardPanel.getIsleaveplan().setSelected(false);
			}
		}else{
			cardPanel.getIsleaveplan().setSelected(false);
		}
		cardPanel.getIsleaveapptimelimit().setSelected(vo.getIsleaveapptimelimit().booleanValue());
		cardPanel.getLeaveapptimelimit().setText(vo.getLeaveapptimelimit().toString());
		
		cardPanel.getShoworder().setText(vo.getShoworder()==null?null:vo.getShoworder().toString());//�����ֶεĸ�ֵ
		
//		cardPanel.getFormulastr().setText(CommonUtils.toStringObject(vo.getFormulastr()));//���ܰ���ǰ���ַ��빫ʽ��Ϊ����ķ���
		cardPanel.setFormulaDesc(CommonUtils.toStringObject(vo.getFormulastr()));
		
		cardPanel.setFormula(CommonUtils.toStringObject(vo.getFormula()));
		cardPanel.getConvertrule().setSelectedItem(vo.getConvertrule());
		cardPanel.getIsleave().setSelected(vo.getIsleave().booleanValue());
		cardPanel.getIsspecialrest().setSelected(vo.getIsspecialrest().booleanValue());
		cardPanel.getLeaveextendcount().setText(vo.getLeaveextendcount()==null?null:vo.getLeaveextendcount().toString());
		cardPanel.setDependleavetypes(vo.getDependVOs());
		cardPanel.getIsLeaveTransfer().setSelected(vo.getIsleavetransfer()==null?false:vo.getIsleavetransfer().booleanValue());
		cardPanel.getIshrssshow().setSelected(vo.getIshrssshow() == null?false:vo.getIshrssshow().booleanValue());
		//add by ward 20180505 �����Ƿ�ۿ�ۿ�����ֶ� begin
		cardPanel.getischarge().setSelected(vo.getIscharge() == null?false:vo.getIscharge().booleanValue());
		cardPanel.getrate().setValue(vo.getRate());
		//add by ward 20180505 �����Ƿ�ۿ�ۿ�����ֶ� end
		
		cardPanel.getMonthunit().setSelectedItem(vo.getMoncounts()==null?0:vo.getMoncounts());
		cardPanel.getWayofresult().setSelectedItem(vo.getWayofresult()==null?0:vo.getWayofresult());
		//���ݽ��㷽ʽ�ж��Ƿ���ʾ���ڽ��㷽ʽ���Ƿ���������Ч��
//		cardPanel.getLeavePanel().setVisible(!(TimeItemCopyVO.LEAVESETPERIOD_MONTH==vo.getLeavesetperiod().intValue()));
		if(TimeItemCopyVO.LEAVESETPERIOD_MONTH==vo.getLeavesetperiod().intValue()){
			cardPanel.getLeavescale().setEnabled(false);
			cardPanel.getLeavescale().setSelectedItem(TimeItemCopyVO.LEAVESCALE_MONTH);
		}
		
		if(1==vo.getWayofresult().intValue()||2==vo.getWayofresult().intValue()){
			cardPanel.getLeavescale().setEnabled(false);
			cardPanel.getLeavesetperiod().setEnabled(false);
			cardPanel.getLeavescale().setSelectedItem(TimeItemCopyVO.LEAVESCALE_MONTH);
			cardPanel.getLeavescale().setSelectedItem(TimeItemCopyVO.LEAVESETPERIOD_MONTH);
		}
		
		boolean isLactation = vo.getIslactation()!=null&&vo.getIslactation().booleanValue();
		boolean isOvertorest = vo.getTimeitemcode() != null && vo.getTimeitemcode().equals(TimeItemCopyVO.OVERTIMETOLEAVETYPE);
		//����ٽ�������,�������ڣ����㷽ʽ����Сʱ�䵥λ����Ϊ��ʽ�������ռ��ݼ٣���Ч���ӳ�ʱ�ޣ�ǰ�ü٣��ݼٹ��򶼲���ʾ
		if(isLactation){
//			cardPanel.getSettlementPanel().setVisible(false);
//			cardPanel.getScalePanel().setVisible(false);
//			cardPanel.getDownPanel().setVisible(false);
//			cardPanel.getLeavePanel().setVisible(false);
			cardPanel.getDependSetPanel().setVisible(false);
			cardPanel.getFormulaSetPanel().setVisible(false);
			cardPanel.getControlPanel().setVisible(false);
//			cardPanel.getTimeitemunit().setSelectedItem(TimeItemCopyVO.TIMEITEMUNIT_HOUR);
		}
		else if(isOvertorest){//�Ӱ�ת���ݽ������ã��ݼٹ�����ʾ����Ϊ�Ӱ�ת���ݵ�ʱ���ǴӼӰ�ת����
//			cardPanel.getSettlementPanel().setVisible(true);
//			cardPanel.getScalePanel().setVisible(vo.getLeavesetperiod().intValue()!=LeaveTypeCopyVO.LEAVESETPERIOD_MONTH);
//			cardPanel.getDownPanel().setVisible(true);
//			cardPanel.getLeavePanel().setVisible(true);
			cardPanel.getDependSetPanel().setVisible(true);
			cardPanel.getFormulaSetPanel().setVisible(false);
			cardPanel.getControlPanel().setVisible(true);
		}
		else{//��������𣬶���ʾ
//			cardPanel.getSettlementPanel().setVisible(true);
//			cardPanel.getScalePanel().setVisible(vo.getLeavesetperiod().intValue()!=LeaveTypeCopyVO.LEAVESETPERIOD_MONTH);
//			cardPanel.getDownPanel().setVisible(true);
//			cardPanel.getLeavePanel().setVisible(true);
			cardPanel.getDependSetPanel().setVisible(true);
			cardPanel.getFormulaSetPanel().setVisible(true);
			cardPanel.getControlPanel().setVisible(true);
			
		}
		
	}
	
	@Override
	public Object getValue() {
		LeaveTypeCopyVO vo = new LeaveTypeCopyVO();
		vo = (LeaveTypeCopyVO)getPubValue(vo);
		LeaveTypeCardPanel cardPanel = getCardPanel();
		cardPanel.stopEditing();
		vo.setLeavesetperiod((Integer)cardPanel.getLeavesetperiod().getSelectdItemValue());
		vo.setLeavesettlement((Integer)cardPanel.getLeavesettlement().getSelectdItemValue());
		vo.setLeavescale((Integer)cardPanel.getLeavescale().getSelectdItemValue());
		//���ݸ�ʽ--��С����ʾ��ʽ��ȡ�����ַ����޷�ת��Ϊdouble����
		vo.setTimeunit(cardPanel.getTimeunit().getValue()==null ? UFDouble.ZERO_DBL:(UFDouble) cardPanel.getTimeunit().getValue());
		vo.setLeavemax(cardPanel.getLeavemax()==null ? null:(UFDouble) cardPanel.getLeavemax().getValue());
		vo.setGxcomtype((Integer)cardPanel.getGxcomtype().getSelectdItemValue());
		vo.setIsLeavelimit(UFBoolean.valueOf(cardPanel.getIsLeavelimit().isSelected()));
		vo.setIsRestrictlimit(UFBoolean.valueOf(cardPanel.getIsrestrictlimit().isSelected()));
		vo.setIsLeaveplan(UFBoolean.valueOf(cardPanel.getIsleaveplan().isSelected()));
		vo.setIsleaveapptimelimit(UFBoolean.valueOf(cardPanel.getIsleaveapptimelimit().isSelected()));
		String leaveApptimeLimit = cardPanel.getLeaveapptimelimit().getText();
		vo.setLeaveapptimelimit(StringUtils.isBlank(leaveApptimeLimit)?0:Integer.parseInt(leaveApptimeLimit));
		vo.setFormulastr(cardPanel.getFormulastr().getText());
		vo.setFormula(cardPanel.getFormula());
		vo.setConvertrule((Integer)cardPanel.getConvertrule().getSelectdItemValue());
		vo.setIsleave(UFBoolean.valueOf(cardPanel.getIsleave().isSelected()));
		vo.setIsspecialrest(UFBoolean.valueOf(cardPanel.getIsspecialrest().isSelected()));
		vo.setPk_dependleavetypes(cardPanel.getDependleavetypes());
		String leaveExtendCount = cardPanel.getLeaveextendcount().getText();
		vo.setLeaveextendcount(StringUtils.isBlank(leaveExtendCount)?0:Integer.parseInt(leaveExtendCount));
		vo.setItemtype(TimeItemCopyVO.LEAVE_TYPE);
		vo.setIsleavetransfer(UFBoolean.valueOf(cardPanel.getIsLeaveTransfer().isSelected()));
		vo.setIshrssshow(UFBoolean.valueOf(cardPanel.getIshrssshow().isSelected()));
		//add by ward 20180505 �����Ƿ�ۿ�ۿ�����ֶ� begin
		vo.setIscharge(UFBoolean.valueOf(cardPanel.getischarge().isSelected()));
		vo.setRate(cardPanel.getrate().getValue()!=null?(Integer)cardPanel.getrate().getValue():null);
		//add by ward 20180505 �����Ƿ�ۿ�ۿ�����ֶ� end
		vo.setWayofresult((Integer)cardPanel.getWayofresult().getSelectdItemValue());
		if((Integer)cardPanel.getWayofresult().getSelectdItemValue()==1||(Integer)cardPanel.getWayofresult().getSelectdItemValue()==2){
			vo.setMoncounts(cardPanel.getMonthunit()==null?0:Integer.valueOf(cardPanel.getMonthunit().getSelectdItemValue().toString()));//̨�屾�ػ�  ���̶��������·ݼ������ʹ�����
		}else{
			vo.setMoncounts(0);//̨�屾�ػ�  ���̶��������·ݼ������ʹ�����
		}
		String order = cardPanel.getShoworder().getText();
		vo.setShoworder(StringUtils.isBlank(order)?null:Integer.parseInt(order));
		
		return vo;
	}

	public LeaveTypeCardPanel getCardPanel() {
		if(cardPanel==null)
			cardPanel = new LeaveTypeCardPanel();
		return cardPanel;
	}

	public void setCardPanel(LeaveTypeCardPanel cardPanel) {
		this.cardPanel = cardPanel;
	}
	
	@Override
	protected void processCompEnableStatusWhenUsed(){
		super.processCompEnableStatusWhenUsed();
		((LeaveTypeCardPanel)getCardPanel()).getLeavesetperiod().setEnabled(false);
	}
}