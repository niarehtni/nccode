package nc.ui.wa.paydata.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.VersionConflictException;
import nc.itf.hr.wa.IPaydataManageService;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.ui.hr.uif2.model.IQueryInfo;
import nc.ui.uif2.UIState;
import nc.ui.uif2.components.pagination.BillManagePaginationDelegator;
import nc.ui.uif2.components.pagination.IPaginationModelListener;
import nc.ui.uif2.components.pagination.PaginationBar;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.DefaultAppModelDataManager;
import nc.ui.uif2.model.IAppModelDataManagerEx;
import nc.ui.wa.alert.HRAlertUserData;
import nc.ui.wa.pub.WADelegator;
import nc.vo.hr.caculate.CaculateTypeVO;
import nc.vo.hrp.budgetmgt.BudgetWarnMessageVo;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.paydata.DataSVO;
import nc.vo.wa.paydata.WaClassItemShowInfVO;
import nc.vo.wa.paydata.WaPaydataDspVO;
import nc.vo.wa.pub.HRWACommonConstants;
import nc.vo.wa.pub.ParaConstant;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * 薪资发放数据管理类
 * 
 * @author: zhangg
 * @date: 2009-11-23 下午01:20:57
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WadataModelDataManager extends DefaultAppModelDataManager implements IAppModelDataManagerEx,
		IPaydataModel, IQueryInfo, IPaginationModelListener {

	private String billtype = null;
	private PaginationModel paginationModel;
	private BillManagePaginationDelegator paginationDelegator;

	public PaginationModel getPaginationModel() {
		return paginationModel;
	}

	public void setPaginationModel(PaginationModel paginationModel) {
		this.paginationModel = paginationModel;

		paginationModel.addPaginationModelListener(this);
		paginationModel.setPageSize(HRWACommonConstants.PAGE_SIZE);
		paginationModel.setMaxPageSize(HRWACommonConstants.MAX_ROW_PER_PAGE);
		paginationModel.init();
	}

	private PaginationBar paginationBar = null;

	public PaginationBar getPaginationBar() {
		return paginationBar;
	}

	public void setPaginationBar(PaginationBar paginationBar) {
		this.paginationBar = paginationBar;
	}

	protected HashMap<String, String> hashPara = new HashMap<String, String>();
	protected int queryResultCount = 0;

	@Override
	public void initModel() {
		try {
			refreshPageSize();
			WaClassItemShowInfVO info = getPayService().getWaClassItemShowInfVO(getWaContext());
			getPaydataModel().setClassItemVOs(info.getWaClassItemVO());
			getPaydataModel().setWaPaydataDspVO(info.getWaPaydataDspVO());
			getModel().initModel(null);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-6-17 19:47:23<br>
	 * 
	 * @author Rocex Wang
	 * @return the paginationDelegator
	 ***************************************************************************/
	public BillManagePaginationDelegator getPaginationDelegator() {
		if (paginationDelegator == null) {
			paginationDelegator = new BillManagePaginationDelegator((BillManageModel) getModel(), getPaginationModel());
		}
		return paginationDelegator;
	}

	/**
	 * 审核
	 * 
	 * @author zhangg on 2009-12-2
	 * @param isRangeAll
	 * @return
	 * @throws BusinessException
	 */
	public void onCheck(boolean isRangeAll) throws BusinessException {
		getPayService().onCheck(getWaLoginVO(), getPaydataModel().getWhereCondition(), isRangeAll);
		refreshWithoutItem();
	}

	/**
	 * 取消审核
	 * 
	 * @author zhangg on 2009-12-2
	 * @throws BusinessException
	 */
	public void onUnCheck(boolean isRangeAll) throws BusinessException {
		getPayService().onUnCheck(getWaLoginVO(), getPaydataModel().getWhereCondition(), isRangeAll);
		refreshWithoutItem();
	}

	//

	public String[] getAlterFiles(String keyName) throws Exception {
		// 先查询是否有配置预警,如果有预警,则调预警的代码.
		// shenliangc 20140905 薪资发放节点个人薪资项目预警提示只显示界面上查询出来的人员。
		HRAlertUserData alertUserData = new HRAlertUserData(getWaContext(), (WadataAppDataModel) this.getModel());
		return alertUserData.showMessageAlertFileNameByButton(nc.ui.wa.alert.HRAlertEnter.getBtnKey("6013paydata",
				keyName));

	}

	/**
	 * 工资总额的预警设置在审核时要进行提示
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public BudgetWarnMessageVo getAuditCreondition(boolean rangeAll) throws BusinessException {
		String currentLimit = // " wa_data.pk_wa_data = wa_dataz.pk_wa_dataz "
		" wa_data.pk_wa_class=wa_waclass.pk_wa_class and wa_waclass.collectflag='N' "
				// + "and  wa_data.pk_org='" + getContext().getPk_org() + "' "
				+ "and wa_data.checkflag = 'N' and wa_data.caculateflag='Y' " + "and wa_data.pk_wa_class='"
				+ getPk_wa_class() + "' and wa_data.cyear='" + getWaYear() + "' and wa_data.cperiod='" + getWaPeriod()
				+ "'";
		String clwhere = "";
		String whereSql = getPaydataModel().getWhereCondition();
		if (!rangeAll) {
			clwhere = ((whereSql == null || whereSql.trim().length() == 0) ? " 1=1 "
					: "wa_data.pk_wa_data in (select pk_wa_data from wa_data where " + whereSql + ")");
		} else {
			clwhere = " 1=1 ";
		}

		return WADelegator.getPaydataQuery().budgetAlarm4Pay(getWaContext(), clwhere + " and " + currentLimit);
	}

	public WadataModelService getPayService() {
		return (WadataModelService) super.getService();
	}

	@Override
	public void refresh() {
		try {
			// 20140806 zhoumxc 补丁合并，修改刷新后焦点回到第一页问题。
			// refreshPageSize();
			WaClassItemShowInfVO info = getPayService().getWaClassItemShowInfVO(getWaContext());
			getPaydataModel().setClassItemVOs(info.getWaClassItemVO());
			getPaydataModel().setWaPaydataDspVO(info.getWaPaydataDspVO());
		} catch (BusinessException e) {
			Logger.error("未知错误", e);
		}

		initModelBySqlWhere(getPaydataModel().getWhereCondition());
	}

	public void refreshWithoutItem() {
		initModelBySqlWhere(getPaydataModel().getWhereCondition());
	}

	/**
	 * 
	 * @author zhangg on 2009-12-4
	 * @throws BusinessException
	 */
	public void onPay() throws BusinessException {
		getPayService().onPay(getWaContext());
		refreshWithoutItem();

	}

	public void onCaculate(CaculateTypeVO caculateTypeVO, SuperVO... superVOs) throws BusinessException {
		try {
			getPayService().onCaculate(getWaContext(), caculateTypeVO, getPaydataModel().getWhereCondition(), superVOs);
			refreshWithoutItem();

		} catch (VersionConflictException e) {
			throw new BusinessException(e.getBusiObject().toString(), e);
		}

	}

	/**
	 * 重新计算跟二代健保补充保费有关联的项目
	 * 
	 * @param caculateTypeVO
	 * @param superVOs
	 * @throws BusinessException
	 */
	public void reCalculateRelationWaItem(CaculateTypeVO caculateTypeVO, SuperVO... superVOs) throws BusinessException {
		try {
			NCLocator
					.getInstance()
					.lookup(IPaydataManageService.class)
					.reCalculateRelationWaItem(getWaContext(), caculateTypeVO, getPaydataModel().getWhereCondition(),
							superVOs);
			refreshWithoutItem();

		} catch (VersionConflictException e) {
			throw new BusinessException(e.getBusiObject().toString(), e);
		}

	}

	public void onUnPay() throws BusinessException {
		getPayService().onUnPay(getWaLoginVO());
		refreshWithoutItem();

	}

	public void onReplace(String whereCondition, WaClassItemVO replaceItem, String formula, SuperVO... superVOs)
			throws BusinessException {
		getPayService().onRepace(getWaLoginVO(), whereCondition, replaceItem, formula, superVOs);
		refreshWithoutItem();

	}

	public void onSaveDataSVOs(DataSVO[] dataSVOs) throws BusinessException {
		getPayService().onSaveDataSVOs(getWaLoginVO(), dataSVOs);
		getModel().setUiState(UIState.NOT_EDIT);
		refreshWithoutItem();
	}

	@Override
	public void setShowSealDataFlag(boolean showSealDataFlag) {
	}

	public WaLoginContext getWaContext() {
		return (WaLoginContext) super.getContext();
	}

	@Override
	public WadataAppDataModel getPaydataModel() {
		return (WadataAppDataModel) super.getModel();
	}

	/**
	 * 
	 * @author zhangg on 2009-12-2
	 * @return
	 */
	public WaLoginVO getWaLoginVO() {
		return getWaContext().getWaLoginVO();
	}

	/**
	 * 
	 * @author zhangg on 2009-12-2
	 * @return
	 */
	public String getPk_wa_class() {
		return getWaLoginVO().getPk_wa_class();
	}

	/**
	 * year
	 * 
	 * @author zhangg on 2009-12-2
	 * @return
	 */
	public String getWaYear() {
		return getWaLoginVO().getCyear();
	}

	/**
	 * period
	 * 
	 * @author zhangg on 2009-12-2
	 * @return
	 */
	public String getWaPeriod() {
		return getWaLoginVO().getCperiod();
	}

	public void setBilltype(String billtype) {
		this.billtype = billtype;
	}

	public String getBilltype() {
		return billtype;
	}

	@Override
	public int getQueryDataCount() {

		return queryResultCount;

	}

	@Override
	public void onDataReady() {
		getPaginationDelegator().onDataReady();
	}

	@Override
	public void onStructChanged() {
		// TODO Auto-generated method stub

	}

	public void refreshPageSize() {
		try {
			Integer pageSize = HRWACommonConstants.PAGE_SIZE;
			if (getContext().getPk_org() != null) {
				pageSize = SysinitAccessor.getInstance().getParaInt(getContext().getPk_org(),
						ParaConstant.DEFAULTPAGENUM);
			}
			if (pageSize == null) {
				pageSize = HRWACommonConstants.PAGE_SIZE;
			}
			paginationModel.setPageSize(pageSize);
			paginationModel.init();
			paginationBar.onStructChanged();
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}
	}

	/**
	 * 初始化显示设置弹出框中数据
	 */
	public void initDisplayData() {

		WaPaydataDspVO[] vos = getPaydataModel().getWaPaydataDspVO();
		List<WaPaydataDspVO> dspLeftList = new ArrayList<WaPaydataDspVO>();
		List<WaPaydataDspVO> dspRightList = new ArrayList<WaPaydataDspVO>();

		if (!ArrayUtils.isEmpty(vos)) {
			for (WaPaydataDspVO vo : vos) {
				if (vo.getBshow() != null && vo.getBshow().booleanValue()) {
					dspRightList.add(vo);
				} else {
					dspLeftList.add(vo);
				}
			}
		}

		getPaydataModel().setLeftItems(dspLeftList);
		getPaydataModel().setRightItems(dspRightList);

	}

	@Override
	public void initModelBySqlWhere(String sqlWhere) {
		// TODO Auto-generated method stub

	}

}
