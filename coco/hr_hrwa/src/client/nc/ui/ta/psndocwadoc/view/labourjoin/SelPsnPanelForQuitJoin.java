package nc.ui.ta.psndocwadoc.view.labourjoin;

import java.awt.BorderLayout;

import org.apache.commons.lang.StringUtils;

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
import nc.ui.wa.psndocwadoc.model.PsndocwadocAppModel;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFLiteralDate;

public class SelPsnPanelForQuitJoin extends UIPanel implements ICompWithValidateFunc,AppEventListener, ValueChangedListener {
	
	/**
	 *
	 */
	private static final long serialVersionUID = -7304690044562845055L;

	private PsndocwadocAppModel model;

	//选择人员范围panel
	private ConditionSelPsnPanel selPanel = null;
	//是否需要选择业务单元参照
	private boolean needBURef=true;
	private BUPanel buPanel=null;

	public SelPsnPanelForQuitJoin() {

	}

	public void init(){
		setLayout(new BorderLayout());
		if(needBURef){
			add(getBuPanel(),BorderLayout.NORTH);
			getBuPanel().getBuRef().addValueChangedListener(this);
		}
		add(getSelPsnPanelForQuitJoin(), BorderLayout.CENTER);
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

//	public UFLiteralDate getBeginDate(){
//		return selPanel.getBeginDate();
//	}
//
//	public UFLiteralDate getEndDate(){
//		return selPanel.getEndDate();
//	}

	protected ConditionSelPsnPanel getSelPsnPanelForQuitJoin(){
		if(selPanel == null){
			selPanel = new ConditionSelPsnPanel();
			selPanel.setModel(getModel());
			selPanel.init();
		}
		return selPanel;
	}


	public PsndocwadocAppModel getModel() {
		return model;
	}

	public void setModel(PsndocwadocAppModel model) {
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
