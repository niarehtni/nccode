package nc.bs.hrss.ta.signcard.ctrl;

import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.SignReg.signreg.SignRegConsts;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyConsts;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.common.ctrl.TaListBaseView;
import nc.bs.hrss.ta.signcard.SignConsts;
import nc.bs.hrss.ta.signcard.lsnr.SignCardCommitProcessor;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.itf.hrsms.ta.common.ILFWLicenceCheck;
import nc.itf.hrss.pub.cmd.prcss.ICommitProcessor;
import nc.itf.ta.ISignCardApplyQueryMaintain;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.WindowContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.signcard.AggSignVO;
import nc.vo.ta.signcard.SignhVO;

public class SignCardListView extends TaListBaseView implements IController {
	protected String getDatasetId() {
		return "hrtasigncardh";
	}

	protected String getBillTypeCode() {
		return "6402";
	}

	protected Class<? extends AggregatedValueObject> getAggVOClazz() {
		return SignConsts.CLASS_NAME_AGGVO;
	}

	protected Class<? extends SuperVO> getMainEntityClazz() {
		return SignhVO.class;
	}

	protected String getPopWindowTitle(String operateflag) {
		if ("add".equals(operateflag)) {
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0103");
		}

		if ("edit".equals(operateflag)) {
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0104");
		}

		if ("view".equals(operateflag)) {
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0105");
		}

		if ("copy".equals(operateflag)) {
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0106");
		}

		return null;
	}

	protected String getPopWindowId() {
		return "SignCardApply";
	}

	public void onDataLoad_hrtasigncardh(DataLoadEvent dataLoadEvent) {
		super.onDataLoad(dataLoadEvent);
	}

	protected AggregatedValueObject[] getAggVOs(FromWhereSQL fromWhereSQL) {
		AggSignVO[] aggVOs = null;
		try {
			ISignCardApplyQueryMaintain queryServ = (ISignCardApplyQueryMaintain) ServiceLocator
					.lookup(ISignCardApplyQueryMaintain.class);
			aggVOs = (AggSignVO[]) queryServ.queryByPsndoc(SessionUtil.getPk_psndoc(), fromWhereSQL, getEtraConds());
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return aggVOs;
	}

	protected Class<? extends ICommitProcessor> getCommitPrcss() {
		return SignCardCommitProcessor.class;
	}

	/**
	 * 部门切换
	 * 
	 * @param keys
	 * @throws BusinessException
	 */
	public void pluginDeptChange(Map<String, Object> keys) throws BusinessException {
		((ILFWLicenceCheck) NCLocator.getInstance().lookup(ILFWLicenceCheck.class))
				.checkLicence(ShopTaApplyConsts.PRODUCTNAME);
		LfwView simpQryView = getCurrentWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
		Dataset dsSearch = simpQryView.getViewModels().getDataset("mainds");
		if (dsSearch != null) {
			Row selRow = dsSearch.getSelectedRow();
			// 管理部门所在的HR组织
			String pk_hr_org = SessionUtil.getPsndocVO().getPk_group();
			UFLiteralDate[] dates = TBMPeriodUtil.getDefaultBeginEndDateByPkOrg(pk_hr_org);
			if (dsSearch.nameToIndex(SignRegConsts.FD_BEGINDATE) > -1) {
				// 开始日期
				selRow.setValue(dsSearch.nameToIndex(SignRegConsts.FD_BEGINDATE), String.valueOf(dates[0]));
			}
			if (dsSearch.nameToIndex(SignRegConsts.FD_ENDDATE) > -1) {
				// 结束日期
				selRow.setValue(dsSearch.nameToIndex(SignRegConsts.FD_ENDDATE), String.valueOf(dates[1]));
			}

			// 清空保存的查询条件
			SessionUtil.getSessionBean().setExtendAttribute(SignRegConsts.SESSION_QRY_CONDITIONS, null);
			// 执行左侧快捷查询
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		}
	}

	/**
	 * 获得当前WindowContext
	 */
	private WindowContext getCurrentWindowContext() {
		return AppLifeCycleContext.current().getWindowContext();
	}
}
