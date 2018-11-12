package nc.ui.overtime.otleavebalance.model;

import nc.bs.framework.common.NCLocator;
import nc.itf.ta.overtime.IOTLeaveBalanceMaintain;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.ui.uif2.model.IAppModelService;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

public class OTLeaveBalanceModelService implements IAppModelService, IPaginationQueryService {
    private IOTLeaveBalanceMaintain manageMaintain;
    private LoginContext context;
    private String beginDate;
    private String endDate;

    public void delete(Object object) throws Exception {
    }

    public Object insert(Object object) throws Exception {
	return null;
    }

    public Object[] queryByDataVisibilitySetting(LoginContext context) throws Exception {
	return null;
    }

    public Object update(Object object) throws Exception {
	return null;
    }

    public IOTLeaveBalanceMaintain getManageMaintain() {
	if (manageMaintain == null) {
	    manageMaintain = ((IOTLeaveBalanceMaintain) NCLocator.getInstance().lookup(IOTLeaveBalanceMaintain.class));
	}
	return manageMaintain;
    }

    public Object[] queryObjectByPks(String[] pks) throws BusinessException {
	return null;
    }

    public LoginContext getContext() {
	return context;
    }

    public void setContext(LoginContext context) {
	this.context = context;
    }

    public String getBeginDate() {
	return beginDate;
    }

    public void setBeginDate(String beginDate) {
	this.beginDate = beginDate;
    }

    public String getEndDate() {
	return endDate;
    }

    public void setEndDate(String endDate) {
	this.endDate = endDate;
    }

}
