package nc.ui.wa.payfile.wizard;

import java.awt.Dimension;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IPayfileQueryService;
import nc.itf.uap.IUAPQueryBS;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pub.beans.wizard.WizardStepValidateException;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.wa.payfile.model.PayfileAppModel;
import nc.ui.wa.payfile.model.PayfileModelDataManager;
import nc.ui.wa.payfile.model.PayfileWizardModel;
import nc.ui.wabm.file.refmodel.CostDeptVersionRefModel;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.payfile.Taxtype;

/**
 * 批量修改 第三步
 *
 * @author: liangxr
 * @date: 2009-12-1 下午02:37:06
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class BatchEditWizardThirdStep extends WizardStep implements IWizardStepListener, BillEditListener {

	private PayfileAppModel model = null;

	private BillCardPanel billCardPanel = null;

	private String nodekey = "payfileBatchedit";
	
	private static String DF9A="1001ZZ1000000001NEGQ";//业务代号
	private static String DF9B="1001ZZ1000000001NEGR";//费用别代号
	private static String DF92="1001ZZ1000000001NEGT";//项目代号

	/**
	 * @author liangxr on 2009-12-1
	 */
	public BatchEditWizardThirdStep(PayfileAppModel model, PayfileModelDataManager dataManager) {
		super();
		this.model = model;
		setTitle(ResHelper.getString("60130payfile","060130payfile0331")/*@res "批量修改"*/);
		setDescription(ResHelper.getString("60130payfile","060130payfile0296")/*@res "设置薪资档案信息"*/);
		setComp(getBillCardPanel());
		addListener(this);
		getBillCardPanel().addEditListener(this);
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
					.getOperator(), billCardPanel.getCorp(), nodekey, null);

			if (template == null) {
				Logger.error("没有找到nodekey：" + nodekey + "对应的卡片模板");
				throw new IllegalArgumentException(ResHelper.getString("60130payfile","060130payfile0272")/*@res "没有找到设置的单据模板信息"*/);
			}

			billCardPanel.setBillData(new BillData(template));
			billCardPanel.setPreferredSize(new Dimension(600, 400));
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
	 * 复选框改变状态时，相关项目可操作
	 *
	 * @author liangxr on 2010-1-25
	 * @see nc.ui.pub.bill.BillEditListener#afterEdit(nc.ui.pub.bill.BillEditEvent)
	 */
	@Override
	public void afterEdit(BillEditEvent e) {
		if (e.getKey().equals("taxedit")) {
			if (((UICheckBox) e.getSource()).isSelected()) {
				getHeadItem(PayfileVO.TAXTYPE).setEnabled(true);
			} else {
				getHeadItem(PayfileVO.TAXTYPE).setEnabled(false);
				getHeadItem(PayfileVO.ISNDEBUCT).setEnabled(false);
				getHeadItem(PayfileVO.TAXTABLEID).setEnabled(false);
				getHeadItem(PayfileVO.TAXTYPE).setValue(null);
				getHeadItem(PayfileVO.ISNDEBUCT).setValue(UFBoolean.FALSE);
				getHeadItem(PayfileVO.TAXTABLEID).setValue(UFBoolean.FALSE);
				getHeadItem("taxfreeedit").setEnabled(true);
				if ((Boolean) getBillCardPanel().getHeadItem("taxfreeedit").getValueObject()) {
					getHeadItem(PayfileVO.ISDERATE).setEnabled(true);
				}
			}


		} else if (e.getKey().equals("taxfreeedit")) {
			if (((UICheckBox) e.getSource()).isSelected()) {
				getHeadItem(PayfileVO.ISDERATE).setEnabled(true);
			} else {
				getHeadItem(PayfileVO.ISDERATE).setEnabled(false);
				getHeadItem(PayfileVO.DERATEPTG).setEnabled(false);
				getHeadItem(PayfileVO.ISDERATE).setValue(UFBoolean.FALSE);
				getHeadItem(PayfileVO.DERATEPTG).setValue(null);
			}

		} else if (e.getKey().equals("stopedit")) {
			if (((UICheckBox) e.getSource()).isSelected()) {
				getHeadItem(PayfileVO.STOPFLAG).setEnabled(true);
			} else {
				getHeadItem(PayfileVO.STOPFLAG).setEnabled(false);
				getHeadItem(PayfileVO.STOPFLAG).setValue(UFBoolean.FALSE);
			}
		} else if (e.getKey().equals(PayfileVO.TAXTYPE)) {
			Integer type = (Integer) ((UIComboBox) e.getSource()).getSelectdItemValue();
			if (Taxtype.WITHHOLDING.equalsValue(type) || Taxtype.REMITTING.equalsValue(type)) {
				getHeadItem(PayfileVO.TAXTABLEID).setEnabled(true);
				getHeadItem(PayfileVO.ISNDEBUCT).setEnabled(true);
				getHeadItem("taxfreeedit").setEnabled(true);
				if ((Boolean) getBillCardPanel().getHeadItem("taxfreeedit").getValueObject()) {
					getHeadItem(PayfileVO.ISDERATE).setEnabled(true);
				}
			} else {
				getHeadItem(PayfileVO.TAXTABLEID).setEnabled(false);
				getHeadItem(PayfileVO.ISNDEBUCT).setEnabled(false);
				getHeadItem(PayfileVO.TAXTABLEID).setValue(null);
				getHeadItem(PayfileVO.ISNDEBUCT).setValue(UFBoolean.FALSE);

				getHeadItem("taxfreeedit").setEnabled(false);
				getHeadItem(PayfileVO.ISDERATE).setEnabled(false);
				getHeadItem(PayfileVO.DERATEPTG).setEnabled(false);
				getHeadItem("taxfreeedit").setValue(UFBoolean.TRUE);
				getHeadItem(PayfileVO.ISDERATE).setValue(UFBoolean.FALSE);
				getHeadItem(PayfileVO.DERATEPTG).setValue(null);

			}
		} else if (e.getKey().equals(PayfileVO.ISDERATE)) {

			if (((UICheckBox) e.getSource()).isSelected()) {
				getHeadItem(PayfileVO.DERATEPTG).setEnabled(true);
			} else {
				getHeadItem(PayfileVO.DERATEPTG).setEnabled(false);
				getHeadItem(PayfileVO.DERATEPTG).setValue(null);
			}
		}else if (e.getKey().equals(PayfileVO.FIPEDIT)) {
			if (((UICheckBox) e.getSource()).isSelected()) {
				getHeadItem(PayfileVO.FIPORGVID).setEnabled(true);
				getHeadItem(PayfileVO.FIPDEPTVID).setEnabled(true);
			} else {
				getHeadItem(PayfileVO.FIPORGVID).setEnabled(false);
				getHeadItem(PayfileVO.FIPORGVID).setEnabled(false);
			}
		}else if (e.getKey().equals(PayfileVO.LIBEDIT)) {
			if (((UICheckBox) e.getSource()).isSelected()) {
				((UIRefPane) billCardPanel.getHeadItem(PayfileVO.PK_LIABILITYORG)
						.getComponent()).setMultiCorpRef(true);
				((UIRefPane) billCardPanel.getHeadItem(PayfileVO.PK_LIABILITYORG)
						.getComponent()).getRefModel().setPk_org(model.getContext().getPk_org());

				String pk_costcenter = (String) getHeadItem(PayfileVO.PK_LIABILITYORG).getValueObject();

				//根据选择的成本中心获取关联的部门，如果成本中心置空那么所属成本部门置空
				if(pk_costcenter!=null){
					((CostDeptVersionRefModel) getRefPane(PayfileVO.LIBDEPTVID)
							.getRefModel()).setPk_costcenter(pk_costcenter);
				}else{
					getRefPane(PayfileVO.LIBDEPTVID).setWhereString(" 1=2 ");
				}

				getHeadItem(PayfileVO.PK_LIABILITYORG).setEnabled(true);
				getHeadItem(PayfileVO.LIBDEPTVID).setEnabled(true);
			} else {
				getHeadItem(PayfileVO.PK_LIABILITYORG).setEnabled(false);
				getHeadItem(PayfileVO.LIBDEPTVID).setEnabled(false);
			}
		}/*
		 * else if (PayfileVO.PK_LIABILITYORG.equals(e.getKey())) { String
		 * pk_costcenter = (String)
		 * getHeadItem(PayfileVO.PK_LIABILITYORG).getValueObject();
		 * 
		 * //根据选择的成本中心获取关联的部门，如果成本中心置空那么所属成本部门置空 if(pk_costcenter!=null){
		 * ((CostDeptVersionRefModel
		 * )getRefPane(PayfileVO.PK_LIABILITYDEPT).getRefModel
		 * ()).setPk_costcenter(pk_costcenter); }else{
		 * getRefPane(PayfileVO.PK_LIABILITYDEPT).setWhereString(" 1=2 "); }
		 * getRefPane(PayfileVO.PK_LIABILITYDEPT).setPK(null);
		 * 
		 * }else if (PayfileVO.PK_FINANCEORG.equals(e.getKey())) { Object pk_org
		 * =getHeadItem(PayfileVO.PK_FINANCEORG).getValueObject();
		 * if(pk_org!=null){
		 * getRefPane(PayfileVO.PK_FINANACEDEPT).setPk_org(pk_org.toString());
		 * }else{ getRefPane(PayfileVO.PK_FINANACEDEPT).setPk_org(null); } }
		 */
		else if (PayfileVO.PK_LIABILITYORG.equals(e.getKey())) {
			String pk_costcenter = (String) getRefPane(
					PayfileVO.PK_LIABILITYORG).getRefModel().getValue(
							"pk_costcenter");
			// 根据选择的成本中心获取关联的部门，如果成本中心置空那么所属成本部门置空
			if (pk_costcenter != null) {
				getRefPane(PayfileVO.LIBDEPTVID).setWhereString("");
				((CostDeptVersionRefModel) getRefPane(PayfileVO.LIBDEPTVID)
						.getRefModel()).setPk_costcenter(pk_costcenter);
			} else {
				getRefPane(PayfileVO.LIBDEPTVID).setWhereString(" 1=2 ");
			}

			getRefPane(PayfileVO.LIBDEPTVID).setPK(null);
			getRefPane(PayfileVO.PK_LIABILITYDEPT).setPK(null);
			billCardPanel.getHeadItem(PayfileVO.LIBDEPTVID).setEnabled(true);
		} else if (PayfileVO.LIBDEPTVID.equals(e.getKey())) {
			Object pk_dept = getRefPane(PayfileVO.LIBDEPTVID).getRefModel()
					.getValue("pk_dept");
			if (pk_dept != null) {
				getRefPane(PayfileVO.PK_LIABILITYDEPT)
				.setPK(pk_dept.toString());
			} else {
				getRefPane(PayfileVO.PK_LIABILITYDEPT).setPK(null);
			}
		} else if (PayfileVO.FIPORGVID.equals(e.getKey())) {
			Object pk_org = getRefPane(PayfileVO.FIPORGVID).getRefModel()
					.getValue("pk_financeorg");
			billCardPanel.getHeadItem(PayfileVO.PK_FINANCEORG).setValue(pk_org);
			if (pk_org != null) {
				getRefPane(PayfileVO.FIPDEPTVID).setPk_org(pk_org.toString());
			} else {
				getRefPane(PayfileVO.FIPDEPTVID).setPk_org(null);
			}
			getRefPane(PayfileVO.FIPDEPTVID).setPK(null);
			getRefPane(PayfileVO.PK_FINANACEDEPT).setPK(null);
			billCardPanel.getHeadItem(PayfileVO.FIPDEPTVID).setEnabled(true);
		} else if (PayfileVO.FIPDEPTVID.equals(e.getKey())) {
			Object pk_dept = getRefPane(PayfileVO.FIPDEPTVID).getRefModel()
					.getValue("pk_dept");
			if (pk_dept != null) {
				getRefPane(PayfileVO.PK_FINANACEDEPT).setPK(pk_dept.toString());
			} else {
				getRefPane(PayfileVO.PK_FINANACEDEPT).setPK(null);
			}
		}

	}

	/**
	 * 校验是否有修改项目
	 *
	 * @author liangxr on 2010-1-25
	 * @see nc.ui.pub.beans.wizard.WizardStep#validate()
	 */
	@Override
	public void validate() throws WizardStepValidateException {
		super.validate();
		Boolean taxedit = (Boolean) getHeadItem(PayfileVO.TAXEDIT).getValueObject();
		Boolean taxfreeedit = (Boolean) getHeadItem(PayfileVO.TAXFREEEDIT).getValueObject();
		Boolean stopedit = (Boolean) getHeadItem(PayfileVO.STOPEDIT).getValueObject();
		Boolean fipedit = (Boolean) getHeadItem(PayfileVO.FIPEDIT).getValueObject();
		Boolean libedit = (Boolean) getHeadItem(PayfileVO.LIBEDIT).getValueObject();
		
		// {MOD:个税申报}
		String feetype = (String)getHeadItem(PayfileVO.FEETYPE).getValueObject();
		String pj = (String)getHeadItem(PayfileVO.PROJECTCODE).getValueObject();
		String biz = (String)getHeadItem(PayfileVO.BIZTYPE).getValueObject();
		WizardStepValidateException e = new WizardStepValidateException();
		if (!taxedit && !taxfreeedit && !stopedit&&!fipedit&&!libedit && feetype==null && pj==null && biz==null) {
			e.addMsg(ResHelper.getString("60130payfile","060130payfile0256")/*@res "错误"*/, ResHelper.getString("60130payfile","060130payfile0297")/*@res "没有要修改的薪资项目！"*/);
		}
		if (taxedit) {
			Object taxtypeObj = getHeadItem(PayfileVO.TAXTYPE).getValueObject();
			if (taxtypeObj == null) {
				e.addMsg(ResHelper.getString("60130payfile","060130payfile0245")/*@res "提示"*/, ResHelper.getString("60130payfile","060130payfile0298")/*@res "请选择扣税方式！"*/);
			} else if (!Taxtype.TAXFREE.equalsValue(taxtypeObj)) {
				if (getHeadItem(PayfileVO.TAXTABLEID).getValueObject() == null) {
					e.addMsg(ResHelper.getString("60130payfile","060130payfile0245")/*@res "提示"*/, ResHelper.getString("60130payfile","060130payfile0299")/*@res "请选择税率表！"*/);
				}
			}
		}
		if (taxfreeedit && (Boolean)getHeadItem(PayfileVO.ISDERATE).getValueObject()) {
			if (getHeadItem(PayfileVO.DERATEPTG).getValueObject() == null) {
				e.addMsg(ResHelper.getString("60130payfile","060130payfile0245")/*@res "提示"*/, ResHelper.getString("60130payfile","060130payfile0274")/*@res "请输入减税比例！"*/);
			} else {
				UFDouble dera = (UFDouble) getHeadItem(PayfileVO.DERATEPTG).getValueObject();
				if (UFDouble.ZERO_DBL.compareTo(dera) > 0 || new UFDouble(100f).compareTo(dera) < 0) {
					e.addMsg(ResHelper.getString("60130payfile","060130payfile0256")/*@res "错误"*/, ResHelper.getString("60130payfile","060130payfile0300")/*@res "减税比例必须在0-100之间！"*/);
				}
			}
		}
		if (e.getMsgs().size() > 0) {
			throw e;
		}
	}

	@Override
	public void stepActived(WizardStepEvent event) {
		UIRefPane refpane = (UIRefPane) getBillCardPanel().getHeadItem(PayfileVO.TAXTABLEID).getComponent();
		refpane.getRefModel().setPk_org(model.getContext().getPk_org());
	}

	@Override
	public void stepDisactived(WizardStepEvent event) {
		// 缓存修改项目
		PayfileVO vo = (PayfileVO) getBillCardPanel().getBillData().getHeaderValueVO(
				PayfileVO.class.getName());
		getModel().setBatchitemvo(vo);
	}

	/**
	 * @author zhoucx on 2009-12-28
	 * @see nc.ui.pub.beans.wizard.WizardStep#getModel()
	 */
	@Override
	public PayfileWizardModel getModel() {
		return (PayfileWizardModel) super.getModel();
	}

	private BillItem getHeadItem(String strKey) {
		return getBillCardPanel().getHeadItem(strKey);
	}

	/**
	 * @author liangxr on 2010-1-25
	 * @see nc.ui.pub.bill.BillEditListener#bodyRowChange(nc.ui.pub.bill.BillEditEvent)
	 */
	@Override
	public void bodyRowChange(BillEditEvent e) {
	}

	private UIRefPane getRefPane(String itemCode) {
		return (UIRefPane) billCardPanel.getHeadItem(itemCode).getComponent();
	}

	private IPayfileQueryService orgQuery ;
	public IPayfileQueryService getOrgQuery(){
		if(orgQuery==null){
			orgQuery = NCLocator.getInstance().lookup(IPayfileQueryService.class);
		}
		return orgQuery;
	}

}