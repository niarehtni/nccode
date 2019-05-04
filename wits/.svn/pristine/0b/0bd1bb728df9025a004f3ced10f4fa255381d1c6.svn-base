package nc.ui.wa.payfile.wizard;

import java.awt.Dimension;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.wa.payfile.common.PayfileUtil;
import nc.ui.wa.payfile.model.PayfileAppModel;
import nc.ui.wa.payfile.model.PayfileModelDataManager;
import nc.ui.wa.payfile.model.PayfileWizardModel;
import nc.ui.wa.payfile.view.PayfileFormEditor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.payfile.PayfileConstant;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.payfile.Taxtype;
 
/**
 * 批量增加 第三步
 * @author: zhoucx
 * @date: 2009-12-1 下午02:37:06
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class BatchAddWizardThirdStep extends WizardStep implements IWizardStepListener,BillEditListener {

	private PayfileAppModel model = null;
	private BillCardPanel billCardPanel = null;
	private String nodekey = "payfileBatchadd";
	private boolean isTaxTableMust = false;
	
	private static String DF9A="1001ZZ1000000001NEGQ";//业务代号
	private static String DF9B="1001ZZ1000000001NEGR";//费用别代号
	private static String DF92="1001ZZ1000000001NEGT";//项目代号
	/**
	 * @author zhoucx on 2009-12-1
	 */
	public BatchAddWizardThirdStep(PayfileAppModel model, PayfileModelDataManager dataManager) {
		super();
		this.model = model;
		setTitle(ResHelper.getString("60130payfile","060130payfile0330")/*@res "批量增加"*/);
		setDescription(ResHelper.getString("60130payfile","060130payfile0296")/*@res "设置薪资档案信息"*/);
		setComp(getBillCardPanel());
		addListener(this);
		getBillCardPanel().addEditListener(this);
		BillPanelUtils.setPkorgToRefModel(getBillCardPanel(), model.getContext().getPk_org());
	}

	public BillCardPanel getBillCardPanel() {

		BillTempletVO template = null;
		if (billCardPanel == null) {
			billCardPanel = new BillCardPanel();
			billCardPanel.setBillType(model.getWaContext().getNodeCode());
			billCardPanel.setBusiType(null);
			billCardPanel.setOperator(model.getContext().getPk_loginUser());
			billCardPanel.setCorp(model.getContext().getPk_group());

			template = billCardPanel.getDefaultTemplet(billCardPanel.getBillType(), null, billCardPanel
					.getOperator(), billCardPanel.getCorp(),nodekey, null);

			if(template == null){
				Logger.error("没有找到nodekey：" + nodekey + "对应的卡片模板");
				throw new IllegalArgumentException(ResHelper.getString("60130payfile","060130payfile0272")/*@res "没有找到设置的单据模板信息"*/);
			}

			billCardPanel.setBillData(new BillData(template));
			billCardPanel.setPreferredSize(new Dimension(600,400));
			// 扣税方式为不扣税,税率表不可编辑
			BillItem taxtype=billCardPanel.getBillData().getHeadItem(PayfileConstant.NODE_TAXTYPE);
			BillItem taxtableid=billCardPanel.getBillData().getHeadItem("taxtableid");
            if(taxtype!=null && taxtableid!=null ){
            	if(taxtype.getDefaultValue().equals(PayfileConstant.TAXTYPE_FREETAX)){
            		taxtableid.setEdit(false);
            	}
             }
            // 把单据模板上设置的默认值再重新赋值到界面上 by wangqim
            BillItem[] items = billCardPanel.getBillData().getHeadTailItems();
            if (items != null)
            {
                for (BillItem item : items)
                {
                    Object value2 = item.getDefaultValueObject();
                    if (value2 != null)

                    {
                        item.setValue(value2);
                    }
                }
            }
		}
		
		// {MOD:个税申报}
		// begin
		IUAPQueryBS query = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String wa_class = model.getWaContext().getPk_wa_class();//getModel().getSelectedData();
		try {
			WaClassVO vo = (WaClassVO) query.retrieveByPK(WaClassVO.class, wa_class);
			String declaretype = vo==null?"":vo.getDeclareform();
			if(DF9A.equals(declaretype)){
				billCardPanel.getHeadItem("biztype").setEnabled(true);//业务代号
				billCardPanel.getHeadItem("feetype").setEnabled(false);//费用别代号
				billCardPanel.getHeadItem("projectcode").setEnabled(false);//项目代号
			}else if(DF9B.equals(declaretype)){
				billCardPanel.getHeadItem("biztype").setEnabled(false);//业务代号
				billCardPanel.getHeadItem("feetype").setEnabled(true);//费用别代号
				billCardPanel.getHeadItem("projectcode").setEnabled(false);//项目代号
			}else if(DF92.equals(declaretype)){
				billCardPanel.getHeadItem("biztype").setEnabled(false);//业务代号
				billCardPanel.getHeadItem("feetype").setEnabled(false);//费用别代号
				billCardPanel.getHeadItem("projectcode").setEnabled(true);//项目代号
			}else{
				billCardPanel.getHeadItem("biztype").setEnabled(false);//业务代号
				billCardPanel.getHeadItem("feetype").setEnabled(false);//费用别代号
				billCardPanel.getHeadItem("projectcode").setEnabled(false);//项目代号
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// end
		return billCardPanel;
	}

	/**
	 * @author zhoucx on 2009-12-28
	 * @see nc.ui.pub.beans.wizard.WizardStep#getModel()
	 */
	@Override
	public PayfileWizardModel getModel() {
		return (PayfileWizardModel) super.getModel();
	}

	@Override
	public void stepActived(WizardStepEvent event) {
		/*
		 * UIRefPane refpane = (UIRefPane)
		 * getBillCardPanel().getHeadItem(PayfileVO.TAXTABLEID).getComponent();
		 * refpane.getRefModel().setPk_org(model.getContext().getPk_org());
		 * 判断税率表是否必输 if (model.getWaContext().getPk_wa_class() == null) {
		 * getBillCardPanel().getHeadItem(PayfileVO.TAXTABLEID).setNull(false);
		 * }else{ try { isTaxTableMust =
		 * (model).queryIsTaxTableMust(model.getWaContext().getPk_wa_class()); }
		 * catch (BusinessException e) { Logger.error(e.getMessage()); } }
		 */
		getBillCardPanel().getHeadItem(PayfileVO.TAXTABLEID).setNull(true);
	}

	@Override
	public void stepDisactived(WizardStepEvent event) {
		PayfileVO vo=(PayfileVO) getBillCardPanel().getBillData().getHeaderValueVO(PayfileVO.class.getName());
		getModel().setBatchitemvo(vo);
	}

	/**
	 * 批增项目编辑状态控制
	 * @author liangxr on 2010-1-25
	 * @see nc.ui.pub.bill.BillEditListener#afterEdit(nc.ui.pub.bill.BillEditEvent)
	 */
	@Override
	public void afterEdit(BillEditEvent e) {
		PayfileUtil.afterEditAddMode(e, getBillCardPanel());
		if (PayfileVO.TAXTYPE.equals(e.getKey())){
			if (getBillCardPanel().getHeadItem(PayfileVO.TAXTYPE).getValue().equals(Taxtype.TAXFREE.toStringValue())) {
				getBillCardPanel().getHeadItem(PayfileVO.TAXTABLEID).setNull(false);
			}else{
				getBillCardPanel().getHeadItem(PayfileVO.TAXTABLEID).setNull(isTaxTableMust);
			}
		}
	}


	/**
	 * @author liangxr on 2010-1-25
	 * @see nc.ui.pub.bill.BillEditListener#bodyRowChange(nc.ui.pub.bill.BillEditEvent)
	 */
	@Override
	public void bodyRowChange(BillEditEvent e) {}
}