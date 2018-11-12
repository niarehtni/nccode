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
		funCode = "E20600908";//�ݼ�����
		if(StringUtils.isEmpty(funCode)){
			return;
		}
		SessionBean bean = SessionUtil.getSessionBean();
		if(bean == null){
			// ȡ����pk_org��ʱ������Ϊȫ��
			refModel.setPk_org("GLOBLE00000000000000");
			return;
		}
		String pk_org = SessionUtil.getHROrg(funCode, false);
		// ����
		String pk_group = null;
		if (SessionUtil.isMngFunc(funCode)) {
			// ���������ڵ�-���������ڼ���
			pk_group = SessionUtil.getPk_mng_group();
		} else {
			// Ա�������ڵ�-Ա����ְ�������ڼ���
			pk_group = SessionUtil.getPk_group();
		}
		/* ���ò��յ���֯�ͼ��� */
		refModel.setPk_org(pk_org);
		refModel.setPk_group(pk_group);
	}
}
