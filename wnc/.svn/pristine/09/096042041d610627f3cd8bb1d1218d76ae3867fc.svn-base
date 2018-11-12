package nc.ui.om.hrdept.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.om.IAOSQueryService;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.om.hrdept.AggHRDeptVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.om.pub.SuperVOHelper;
import nc.vo.org.AdminOrgVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.MultiLangText;
import nc.vo.pub.lang.UFDate;

/**
 * AppModel实现类<br>
 * 
 * @author zhangdd
 */
public class DeptAppModel extends BillManageModel {

	/** HR组织缓存 */
	private Map<String, String> hrOrgMap = new HashMap<String, String>();

	private String pk_org;

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	@Override
	public void initModel(Object data) {
		// TODO Auto-generated method stub
		super.initModel(data);
	}

	/**
	 * 是否可以新增<br>
	 * 
	 * @return
	 */
	public boolean canAdd() {
		return getPk_org() != null;
	}

	/**
	 * 是否可以编辑<br>
	 * 
	 * @return
	 */
	public boolean canEdit() {
		Object selectedData = getSelectedData();
		if (selectedData == null || !(selectedData instanceof AggHRDeptVO)) {
			return false;
		}
		// '部门信息'节点不允许修改未生效的部门信息(成立时间大于当前时间 )by he
		if (null != selectedData) {
			HRDeptVO deptvo = (HRDeptVO) ((AggHRDeptVO) selectedData).getParentVO();
			UFDate createdate = new UFDate(String.valueOf(deptvo.getCreatedate()));
			if (createdate != null && createdate.after(new UFDate(new Date()))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否可以删除<br>
	 * 
	 * @return
	 */
	public boolean canDelete() {
		// return getSelectedNode().getChildCount() == 0;
		AggHRDeptVO vo = (AggHRDeptVO) getSelectedData();
		try {
			return ((DeptModelService) getService()).hasChildDept((HRDeptVO) vo.getParentVO());
		} catch (BusinessException ex) {
			Logger.error(ex.getMessage(), ex);
		}
		return false;
	}

	public void cacheHROrgPK() {
		// 利用缓存减少连接数
		if (hrOrgMap.containsKey(getContext().getPk_org())) {
			return;
		}
		try {
			// 查询组织单元属于的HR组织
			OrgVO hrOrgVO = getAOSQueryService().queryHROrgByOrgPK(getContext().getPk_org());
			if (hrOrgVO != null) {
				hrOrgMap.put(getContext().getPk_org(), hrOrgVO.getPk_org());
			}
		} catch (BusinessException e) {
			throw new BusinessRuntimeException(ResHelper.getString("6005dept", "06005dept0164")/*
																								 * @
																								 * res
																								 * "查询业务单元所属的HR组织时发生错误"
																								 */);
		}
	}

	public void directlyDelete(Object obj) {
		// DefaultMutableTreeNode nodeToBeSelected =
		// toBeSelectedNodeWhenDelete();
		// DefaultMutableTreeNode selectedNode = findNodeByBusinessObject(obj);
		// getTree().removeNodeFromParent(selectedNode);
		// fireEvent(new AppEvent(AppEventConst.DATA_DELETED, this, obj));
		// setSelectedNode(nodeToBeSelected);
	}

	public String getPk_hrorg() {
		return hrOrgMap.get(getContext().getPk_org());
	}

	private IAOSQueryService getAOSQueryService() {
		return NCLocator.getInstance().lookup(IAOSQueryService.class);
	}

	public Object[] getDatas() {
		// DefaultMutableTreeNode root = (DefaultMutableTreeNode) getTree()
		// .getRoot();
		//
		// ArrayList<AggHRDeptVO> al = new ArrayList<AggHRDeptVO>();
		// al = getChild(root, al);
		// return al.toArray(new AggHRDeptVO[0]);
		return getSelectedOperaDatas();
	}

	private ArrayList<AggHRDeptVO> getChild(DefaultMutableTreeNode node, ArrayList<AggHRDeptVO> al) {
		if (node.getChildCount() > 0) {
			for (int i = 0; i < node.getChildCount(); i++) {
				DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) node.getChildAt(i);
				AggHRDeptVO aggHRDeptVO = (AggHRDeptVO) dmt.getUserObject();
				if (dmt.getChildCount() > 0) {
					getChild(dmt, al);
				}
				al.add(aggHRDeptVO);
			}
		}
		return al;
	}

	public MultiLangText getOrgName(String pk_Org) {
		try {
			MultiLangText deptName = null;
			// 查询部门所属组织单元
			AdminOrgVO orgVO = getAOSQueryService().queryByPK(pk_Org);
			if (orgVO == null) {
				return null;
			} else {
				return SuperVOHelper.getMultiLangAttribute(orgVO, HRDeptVO.NAME);
			}

		} catch (BusinessException e) {
			throw new BusinessRuntimeException(ResHelper.getString("6005dept", "16005dept0022")/*
																								 * @
																								 * res
																								 * "查询部门所属业务单元名称时报错！"
																								 */);
		}
	}
}
