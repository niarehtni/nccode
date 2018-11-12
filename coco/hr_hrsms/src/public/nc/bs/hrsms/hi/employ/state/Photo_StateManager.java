package nc.bs.hrsms.hi.employ.state;

import nc.bs.hrss.hi.psninfo.PsninfoConsts;

import org.apache.commons.lang.StringUtils;

import uap.web.bd.pub.AppUtil;

/**
 * 照片维护按钮：当在业务系统勾选了照片可编辑之后才显示
 * 
 * @author lihha
 * 
 */
public class Photo_StateManager extends Psninfo_Base_StateManager {
	@Override
	protected State getStateByItem(String infoset, boolean needAudit, int data_status) {
		if (!PsninfoConsts.INFOSET_CODE_PSNDOC.equals(infoset))
			return State.HIDDEN;
		// 更改照片按钮显示标识
		Object photoFlag = (Object) AppUtil.getAppAttr("photoFlag");
		if(photoFlag != null && !StringUtils.isEmpty((String)photoFlag) && "true".equals(photoFlag)){
			return State.VISIBLE;
		}
		return State.HIDDEN;
		
	}
}
