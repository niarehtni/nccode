package nc.bs.hrsms.ta.sss.away.ctrl;

import java.util.List;

import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.itf.hrss.ta.timeapply.IQueryOrgOrDeptVid;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.vo.pub.BusinessException;
import nc.vo.ta.psndoc.TBMPsndocVO;

public class ShopAwayAfterDataChange {

	/**
	 * 
	 */
	public static void onAfterDataChange(Dataset ds, Row row){
		
		TBMPsndocVO tbmPsndocVO = ShopTaAppContextUtil.getTBMPsndocVO();
		if(tbmPsndocVO == null){
//			throw new LfwRuntimeException("����Ա��û�����ÿ��ڵ��������ܽ�������������");
			CommonUtil.showMessageDialog("��ǰ��Ա�Ŀ��ڵ����Ѿ�������ֻ��������������ǰ�����ݣ�");
			return;
		}
		String pk_psnjob = tbmPsndocVO.getPk_psnjob();
		String pk_psnorg = tbmPsndocVO.getPk_psnorg();
		
		// ��Ա��ְ����
		row.setValue(ds.nameToIndex("pk_psnjob"), pk_psnjob);
		// ��Ա��֯��ϵ���
		row.setValue(ds.nameToIndex("pk_psnorg"), pk_psnorg);
		
		List<String> list = getVersionIds(pk_psnjob);
		if (list != null && list.size() > 0) {
			// ��Ա��ְ����ҵ��Ԫ�İ汾id
			row.setValue(ds.nameToIndex("pk_org_v"), list.get(0));
			// ��Ա��ְ�������ŵİ汾pk_dept_v
			row.setValue(ds.nameToIndex("pk_dept_v"), list.get(1));
		}
	}
	
	/**
	 * �����Ա��ְ����ҵ��Ԫ/���ŵİ汾id
	 * 
	 * @param pk_psnjob
	 * @return
	 */
	private static List<String> getVersionIds(String pk_psnjob) {
		List<String> list = null;
		IQueryOrgOrDeptVid service;
		try {
			service = ServiceLocator.lookup(IQueryOrgOrDeptVid.class);
			list = service.getOrgOrDeptVidByPsnjob(pk_psnjob);
		} catch (HrssException ex) {
			ex.alert();
		} catch (BusinessException ex) {
			new HrssException(ex).deal();
		}
		return list;
	}
}
