package nc.bs.hrsms.hi.employ.state;

import nc.bs.hrss.hi.psninfo.PsninfoConsts;

import org.apache.commons.lang.StringUtils;

import uap.web.bd.pub.AppUtil;

/**
 * ��Ƭά����ť������ҵ��ϵͳ��ѡ����Ƭ�ɱ༭֮�����ʾ
 * 
 * @author lihha
 * 
 */
public class Photo_StateManager extends Psninfo_Base_StateManager {
	@Override
	protected State getStateByItem(String infoset, boolean needAudit, int data_status) {
		if (!PsninfoConsts.INFOSET_CODE_PSNDOC.equals(infoset))
			return State.HIDDEN;
		// ������Ƭ��ť��ʾ��ʶ
		Object photoFlag = (Object) AppUtil.getAppAttr("photoFlag");
		if(photoFlag != null && !StringUtils.isEmpty((String)photoFlag) && "true".equals(photoFlag)){
			return State.VISIBLE;
		}
		return State.HIDDEN;
		
	}
}
