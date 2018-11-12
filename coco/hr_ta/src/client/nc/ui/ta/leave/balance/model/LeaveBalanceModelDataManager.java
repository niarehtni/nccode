package nc.ui.ta.leave.balance.model;

import nc.bs.logging.Logger;
import nc.pubitf.para.SysInitQuery;
import nc.ui.hr.uif2.model.IQueryInfo;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.uif2.components.pagination.BillManagePaginationDelegator;
import nc.ui.uif2.components.pagination.IPaginationModelListener;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.IAppModelDataManagerEx;
import nc.ui.uif2.model.IAppModelService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.ta.psndoc.TBMPsndocCommonValue;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class LeaveBalanceModelDataManager implements IAppModelDataManagerEx, IQueryInfo, IPaginationModelListener {

    /** 上一次查询的条件 */
    private FromWhereSQL lastWhereSql;

    IAppModelService service;

    private AbstractAppModel hierachicalModel;
    private AbstractUIAppModel managerAppModel;

    LoginContext context;
    protected int iQueryDataCount = 0;

    private BillManagePaginationDelegator paginationDelegator;
    private PaginationModel paginationModel;

    public void initModel() {
	initHierachicalModel();
    }

    public void initManagerModelByType() {
	// modified by zengcheng 20120810,与需求讨论后确定，切换休假类别时，要按之前的查询条件重新查询
	// getManagerAppModel().initModel(null);
	refresh();

    }

    public void initHierachicalModel() {
	try {
	    Object[] objs = null;
	    if (getContext().getPk_org() != null) {
		// MOD(加班轉調休休假類別參數檢查)
		// 當組織參數中設定了該參數(TWHRT08)，則在假期計算中不顯示選中的休假類別
		// 參考文檔：《NC65-6501-LocalizationLSLV1-SA01_勞基法改動.docx》 S6.5.1
		// ssx modified on 2018-09-16
		// begin
		String strAddWhere = "";
		UFBoolean twEnabled = SysInitQuery.getParaBoolean(this.getContext().getPk_org(), "TWHR01");// 啟用臺灣本地化
		if (twEnabled != null && twEnabled.booleanValue()) {
		    String pk_leavetypecopy = SysInitQuery.getParaString(this.getContext().getPk_org(), "TWHRT08");// 加班轉調休休假類別
		    if (!StringUtils.isEmpty(pk_leavetypecopy)) {
			strAddWhere = " and pk_timeitemcopy <> '" + pk_leavetypecopy + "'";
		    }
		}
		objs = ((LeaveBalanceModelService) getService()).queryLeaveCopyTypesByOrg(getContext(),
			" pk_timeitem in (select pk_timeitem from tbm_timeitem where islactation='N') " + strAddWhere);
		// end
	    }
	    getHierachicalModel().initModel(objs);
	    initManagerModelByType();
	} catch (BusinessException e) {
	    Logger.debug("初始化休假类型失败!");
	}
    }

    @Override
    public void initModelBySqlWhere(String sqlWhere) {

    }

    public void initModelByFromWhere(FromWhereSQL lastWhereSql) {
	this.lastWhereSql = lastWhereSql;
	refresh();
    }

    @Override
    public void refresh() {

	Object[] objs = null;

	Object obj = getHierachicalModel().getSelectedData();

	LeaveBalanceAppModel model = (LeaveBalanceAppModel) getManagerAppModel();
	// 没有选中休假类别，查询条件，组织
	if (obj == null || lastWhereSql == null || StringUtils.isBlank(getContext().getPk_org())
		|| model.getYear() == null) {
	    iQueryDataCount = ArrayUtils.getLength(objs);
	    getManagerAppModel().initModel(objs);
	    return;
	}
	LeaveTypeCopyVO typeVO = (LeaveTypeCopyVO) obj;

	try {
	    // objs =
	    // ((LeaveBalanceModelService)getService()).queryByCond(context,typeVO.getPk_timeitem(),
	    // model.getYear(),model.getMonth(),lastWhereSql);
	    // iQueryDataCount = ArrayUtils.getLength(objs);
	    // getManagerAppModel().initModel(objs);

	    // 采用分页
	    String[] pk_psndocs = getModeService().queryPsnPksByCond(context, typeVO.getPk_timeitem(), model.getYear(),
		    model.getMonth(), lastWhereSql);
	    getModeService().setContext(context);
	    getModeService().setPk_timeitem(typeVO.getPk_timeitem());
	    getModeService().setYear(model.getYear());
	    getModeService().setMonth(model.getMonth());
	    iQueryDataCount = ArrayUtils.getLength(pk_psndocs);
	    getPaginationModel().setObjectPks(pk_psndocs);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}

    }

    public LeaveBalanceModelService getModeService() {
	return (LeaveBalanceModelService) getService();
    }

    public IAppModelService getService() {
	return service;
    }

    public void setService(IAppModelService service) {
	this.service = service;
    }

    public LoginContext getContext() {
	return context;
    }

    public void setContext(LoginContext context) {
	this.context = context;
    }

    @Override
    public void setShowSealDataFlag(boolean showSealDataFlag) {

    }

    public AbstractAppModel getHierachicalModel() {
	return hierachicalModel;
    }

    public void setHierachicalModel(AbstractAppModel hierachicalModel) {
	this.hierachicalModel = hierachicalModel;
    }

    public AbstractUIAppModel getManagerAppModel() {
	return managerAppModel;
    }

    public void setManagerAppModel(AbstractUIAppModel managerAppModel) {
	this.managerAppModel = managerAppModel;
    }

    @Override
    public int getQueryDataCount() {
	return iQueryDataCount;
    }

    public PaginationModel getPaginationModel() {
	return paginationModel;
    }

    public void setPaginationModel(PaginationModel paginationModel) {
	this.paginationModel = paginationModel;
	paginationModel.addPaginationModelListener(this);
	paginationModel.setPageSize(((LeaveBalanceAppModel) getManagerAppModel()).getPaginationSize());
	paginationModel.setMaxPageSize(TBMPsndocCommonValue.MAX_ROW_PER_PAGE);
	paginationModel.init();
    }

    public BillManagePaginationDelegator getPaginationDelegator() {
	if (paginationDelegator == null) {
	    paginationDelegator = new BillManagePaginationDelegator((BillManageModel) getManagerAppModel(),
		    getPaginationModel());
	}
	return paginationDelegator;
    }

    public void setPaginationDelegator(BillManagePaginationDelegator paginationDelegator) {
	this.paginationDelegator = paginationDelegator;
    }

    @Override
    public void onStructChanged() {
	// TODO Auto-generated method stub

    }

    @Override
    public void onDataReady() {
	getPaginationDelegator().onDataReady();
    }
}
