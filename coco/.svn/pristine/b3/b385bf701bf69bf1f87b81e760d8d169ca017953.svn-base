package nc.bs.hrsms.ta.sss.shopleave.common;

import nc.bs.hrss.pub.tool.SessionUtil;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.refnode.RefNode;
import nc.uap.lfw.reference.ILfwRefModel;
import nc.uap.lfw.reference.app.AppReferenceController;
import nc.vo.hrss.pub.SessionBean;

import org.apache.commons.lang.StringUtils;

public class ShopQueryCondLeaveTypeRefCtrl extends AppReferenceController {

	@Override
	protected void processSelfWherePart(Dataset ds, RefNode rfnode,
			String filterValue, ILfwRefModel refModel) {
		setPk_org(refModel);
	}

	@Override
	protected void processTreeSelWherePart(Dataset ds, RefNode rfnode,
			ILfwRefModel refModel) {
		setPk_org(refModel);
	}
	
	private void setPk_org(ILfwRefModel refModel){
		String funCode = (String)SessionUtil.getParentFunCode();
		if(StringUtils.isEmpty(funCode)){
			funCode = (String)SessionUtil.getCurrentFunCode();
		}
		funCode = "E20600908";//休假申请
		if(StringUtils.isEmpty(funCode)){
			return;
		}
		SessionBean bean = SessionUtil.getSessionBean();
		if(bean == null){
			// 取不到pk_org的时候设置为全局
			refModel.setPk_org("GLOBLE00000000000000");
			return;
		}
		String pk_org = SessionUtil.getHROrg(funCode, false);
		// 集团
		String pk_group = null;
		if (SessionUtil.isMngFunc(funCode)) {
			// 经理自助节点-管理部门所在集团
			pk_group = SessionUtil.getPk_mng_group();
		} else {
			// 员工自助节点-员工主职所在所在集团
			pk_group = SessionUtil.getPk_group();
		}
		/* 设置参照的组织和集团 */
		refModel.setPk_org(pk_org);
		refModel.setPk_group(pk_group);
	}
}
