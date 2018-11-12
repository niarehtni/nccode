package nc.ui.overtime.otleavebalance.model;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.ta.ITimeItemQueryMaintain;
import nc.pubitf.para.SysInitQuery;
import nc.ui.overtime.otleavebalance.view.OTLeaveBalanceOrgPanel;
import nc.ui.pubapp.uif2app.query2.model.ModelDataManager;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.ta.pub.TALoginContext;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;

import org.apache.commons.lang.StringUtils;

/**
 * 补休假期计算数据管理员
 * 
 * @author ssx
 * @since 2018-10-12
 * @version NC65 TWLC 3.2.2
 */
public class OTLeaveBalanceModeDataManager extends ModelDataManager {
    private TALoginContext context = null;
    private AbstractAppModel hierachicalModel;
    private ITimeItemQueryMaintain timeItemQueryService;
    private OTLeaveBalanceOrgPanel orgpanel;

    public TALoginContext getContext() {
	return context;
    }

    public void setContext(TALoginContext context) {
	this.context = context;
    }

    public AbstractAppModel getHierachicalModel() {
	return hierachicalModel;
    }

    public void setHierachicalModel(AbstractAppModel hierachicalModel) {
	this.hierachicalModel = hierachicalModel;
    }

    public OTLeaveBalanceOrgPanel getOrgpanel() {
	return orgpanel;
    }

    public void setOrgpanel(OTLeaveBalanceOrgPanel orgpanel) {
	this.orgpanel = orgpanel;
    }

    public void initHierachicalModel() {
	try {
	    Object[] objs = null;
	    if (getContext().getPk_org() != null) {
		// ssx modified on 2018-09-16
		// begin
		String strAddWhere = " and 1=2";
		UFBoolean twEnabled = SysInitQuery.getParaBoolean(this.getContext().getPk_org(), "TWHR01");// 啟用臺灣本地化
		if (twEnabled != null && twEnabled.booleanValue()) {
		    String pk_leavetypecopy = SysInitQuery.getParaString(this.getContext().getPk_org(), "TWHRT08");// 加班轉調休休假類別
		    if (!StringUtils.isEmpty(pk_leavetypecopy)) {
			strAddWhere = " and pk_timeitemcopy = '" + pk_leavetypecopy + "'";
		    }
		}
		objs = ((ITimeItemQueryMaintain) getTimeItemQueryService()).queryLeaveCopyTypesByOrg(getContext()
			.getPk_org(), " pk_timeitem in (select pk_timeitem from tbm_timeitem where islactation='N') "
			+ strAddWhere);
		// end
	    }
	    getHierachicalModel().initModel(objs);
	    refresh();
	} catch (BusinessException e) {
	    Logger.debug("初始化休假类型失败!");
	}
    }

    private ITimeItemQueryMaintain getTimeItemQueryService() {
	if (timeItemQueryService == null) {
	    timeItemQueryService = ((ITimeItemQueryMaintain) NCLocator.getInstance().lookup(
		    ITimeItemQueryMaintain.class));
	}

	return timeItemQueryService;
    }

    @Override
    public void refresh() {
	clearUIData();

	if (this.getHierachicalModel().getSelectedData() != null) {
	    if (this.getHierachicalModel().getSelectedData() instanceof LeaveTypeCopyVO) {
		LeaveTypeCopyVO selectedLeaveType = (LeaveTypeCopyVO) this.getHierachicalModel().getSelectedData();
		refreshDataByLeaveType(selectedLeaveType);
	    }
	}
    }

    private void refreshDataByLeaveType(LeaveTypeCopyVO selectedLeaveType) {
	if (this.getOrgpanel().getRefBeginDate().getValueObj() != null
		&& this.getOrgpanel().getRefEndDate().getValueObj() != null) {
	}
    }

    private void clearUIData() {
	getModel().initModel(null);
    }
}
