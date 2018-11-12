package nc.ui.om.psnnavi.view;

import nc.ui.om.psnnavi.model.TreeAppModel;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.org.OrgVO;

/**
 * 基于管理范围的行政体系树构建策略
 * 
 * @since V6.0 2011-3-24 17:13:34
 */
public class MsAOSNaviTreeCreateStrategy extends TreeCreateStrategy {
	private TreeAppModel model;

	public MsAOSNaviTreeCreateStrategy() {
	}

	public TreeAppModel getModel() {
		return model;
	}

	@Override
	public Object getNodeId(Object obj) {
		if (obj instanceof OrgVO) {
			return ((OrgVO) obj).getPk_org();
		} else if (obj instanceof HRDeptVO) {
			return ((HRDeptVO) obj).getPk_dept();
		}

		return null;
	}

	@Override
	public Object getParentNodeId(Object obj) {
		if (obj instanceof OrgVO) {
			return ((OrgVO) obj).getPk_fatherorg();
		} else if (obj instanceof HRDeptVO) {
			HRDeptVO deptVO = (HRDeptVO) obj;
			if (deptVO.getPk_fatherorg() != null) {
				if (isInModel(deptVO.getPk_fatherorg())) {
					return deptVO.getPk_fatherorg();
				}
				return deptVO.getPk_org();
			}

			return deptVO.getPk_org();
		}

		return null;
	}

	private boolean isInModel(String id) {
		if (getModel().getAllDatas() == null || getModel().getAllDatas().length == 0) {
			return false;
		}
		Object[] objs = getModel().getAllDatas();
		for (int i = 0; i < objs.length; i++) {
			String pk = "";
			// MOD ssx 功能節點打開效率優化 on 2018-08-07
			// 凡有組織樹構建的節點此處要重復執行N次
			// N=Model中所有數據的總數 ^ 2
			// 反向創建對象的方法不可取
			if (objs[i] instanceof OrgVO) {
				pk = ((OrgVO) objs[i]).getPk_org();
			} else if (objs[i] instanceof HRDeptVO) {
				pk = ((HRDeptVO) objs[i]).getPk_dept();
			} else {
				pk = (String) getFactory().createBDObject(objs[i]).getId(); // 該方法在大量執行時效率極低
			}
			//
			if (id.equals(pk)) {
				return true;
			}
		}

		return false;
	}

	public void setModel(TreeAppModel model) {
		this.model = model;
	}
}
