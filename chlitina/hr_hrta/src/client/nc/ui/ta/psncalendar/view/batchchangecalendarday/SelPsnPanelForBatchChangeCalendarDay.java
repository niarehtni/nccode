package nc.ui.ta.psncalendar.view.batchchangecalendarday;

import java.awt.BorderLayout;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.ta.psncalendar.model.PsnCalendarAppModel;
import nc.ui.ta.pub.BUPanel;
import nc.ui.ta.pub.ICompWithValidateFunc;
import nc.ui.ta.pub.selpsn.ConditionSelPsnDateScopePanel;
import nc.ui.ta.pub.selpsn.ConditionSelPsnPanel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

/**
 * 选择人员的panel
 * @author Ares.Tank
 * @date 2018-8-15 15:08:23
 * mod Ares.Tank 2018-10-3 19:47:34
 * 业务逻辑变更,这个panel已经被废弃,不应该有方法会走到这来
 */
public class SelPsnPanelForBatchChangeCalendarDay extends UIPanel implements ICompWithValidateFunc,AppEventListener, ValueChangedListener{

	/**
	 *
	 */
	private static final long serialVersionUID = -7304690044562845055L;

	private PsnCalendarAppModel model;

	//选择人员范围的panel
	private ConditionSelPsnPanel selPanel = null;
	//是否需要选择业务单元参照
	private boolean needBURef=true;
	private BUPanel buPanel=null;

	public SelPsnPanelForBatchChangeCalendarDay() {

	}

	public void init(){
		setLayout(new BorderLayout());
		if(needBURef){
			add(getBuPanel(),BorderLayout.NORTH);
			getBuPanel().getBuRef().addValueChangedListener(this);
		}
		add(getSelPanelForBatchChange(), BorderLayout.CENTER);
	}

	public BUPanel getBuPanel() {
		if(buPanel==null){
			buPanel = new BUPanel();
			buPanel.setModel(getModel());
			buPanel.init();
		}
		return buPanel;
	}
	public FromWhereSQL getQuerySQL(){
		return selPanel.getQuerySQL();
	}



	protected ConditionSelPsnPanel getSelPanelForBatchChange(){
		if(selPanel == null){
			selPanel = new ConditionSelPsnPanel();
			selPanel.setModel(getModel());
			selPanel.init();
		}
		return selPanel;
	}


	public PsnCalendarAppModel getModel() {
		return model;
	}

	public void setModel(PsnCalendarAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}
	@Override
	public void validateData() throws ValidationException{
		if(needBURef && StringUtils.isBlank(getPK_BU()))
			throw new ValidationException(ResHelper.getString("6017psncalendar","06017psncalendar0058")
/*@res "业务单元不能为空！"*/);
	}

	public boolean isNeedBURef() {
		return needBURef;
	}

	public void setNeedBURef(boolean needBURef) {
		this.needBURef = needBURef;
	}

	public String getPK_BU(){
		if(needBURef)
			return getBuPanel().getPK_BU();
		return null;
	}

	@Override
	public void handleEvent(AppEvent event) {


	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		model.buChanged();
	}
}