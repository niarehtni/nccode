package nc.ui.hrwa.incometax.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.funcnode.ui.FuncletInitData;
import nc.funcnode.ui.FuncletLinkEvent;
import nc.funcnode.ui.FuncletLinkListener;
import nc.funcnode.ui.FuncletWindowLauncher;
import nc.hr.utils.ResHelper;
import nc.itf.hrwa.IGetAggIncomeTaxData;
import nc.itf.uap.bbd.func.IFuncRegisterQueryService;
import nc.sfbase.client.ClientToolKit;
import nc.ui.bd.pub.BDFuncletInitData;
import nc.ui.pub.linkoperate.ILinkMaintainData;
import nc.ui.pub.linkoperate.ILinkQueryData;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.hrwa.incometax.AggIncomeTaxVO;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.funcreg.FuncRegisterVO;

public class SumIncomeTaxLinkQueryAction extends NCAction {

	private static final long serialVersionUID = 6254882396104481382L;
	private AbstractAppModel model;
	private String soid;
	public static final String ENTRACE_TYPE = "FromLinkQuery";//定义单据来源

	public SumIncomeTaxLinkQueryAction() {
		this.setBtnName(ResHelper.getString("incometax", "2incometax-n-000011")/*"联查汇总信息"*/);
		super.setCode("linkquery");
		super.putValue("linkquery",ResHelper.getString("incometax", "2incometax-n-000011") );
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		Object object = getModel().getSelectedData();
		if (null == object) {
			return;
		}
		AggIncomeTaxVO aggvo = (AggIncomeTaxVO) object;
		IGetAggIncomeTaxData getService = NCLocator.getInstance().lookup(
				IGetAggIncomeTaxData.class);
		AggSumIncomeTaxVO aggSumIncomeTaxVO = getService
				.getAggSumIncomeTaxByIncome(aggvo.getParentVO()
						.getPk_incometax());
		if (aggSumIncomeTaxVO == null) {
			throw new BusinessException(ResHelper.getString("notice",
					"2notice-tw-000009")/* "单据已被修改，请刷新后重试！" */);
		}
		this.soid = aggSumIncomeTaxVO.getPrimaryKey();
		Component parent = WorkbenchEnvironment.getInstance().getWorkbench();
		IFuncRegisterQueryService service = NCLocator.getInstance().lookup(
				IFuncRegisterQueryService.class);
		FuncRegisterVO FrvO = service.queryFunctionByCode("60130sumincometax");
		FuncletWindowLauncher.openFuncNodeForceModalDialog(parent, FrvO,
				new FuncletInitData(3, new ILinkQueryData() {
					public Object getUserObject() {
						return new UFBoolean(true);
					}

					public String getBillID() {
						return soid;
					}

					@Override
					public String getBillType() {
						//Ares.Tank 2018-8-23 11:23:53 从联查按钮进入,给一个标识
						return ENTRACE_TYPE;
					}

					@Override
					public String getPkOrg() {
						return null;
					}
				}), null, false);
	}

	protected boolean isActionEnable() {
		Object object = getModel().getSelectedData();
		if (null == object) {
			return false;
		}
		AggIncomeTaxVO aggvo = (AggIncomeTaxVO) object;
		if (aggvo.getParentVO().getIsgather().booleanValue()) {
			return true;
		}
		return false;
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

}
