package nc.ui.ta.pub.action;

import java.awt.event.ActionEvent;

import nc.hr.utils.ResHelper;
import nc.uap.rbac.core.dataperm.DataPermissionFacade;
import nc.ui.uif2.components.ITabbedPaneAwareComponent;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.pub.SuperVO;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.pub.ICommonConst;

import org.apache.commons.lang.ArrayUtils;


/**
 * 列表页选择数据模式为：用checkbox多选
 * 点击删除按钮时，判断当前数据是否被勾选的判断类
 * 仅适用于单据登记页面
 * @author caiyl
 *
 */
public class DeleteAction extends nc.ui.hr.uif2.action.DeleteAction {
	private static final long serialVersionUID = 1L;
	private ITabbedPaneAwareComponent listView;
    private ITabbedPaneAwareComponent cardView;

	@Override
	public void doAction(ActionEvent evt) throws Exception {
		if(isCurlistView()) {
			if(ArrayUtils.isEmpty(((BillManageModel)getModel()).getSelectedOperaRows())) {
				throw new Exception(PublicLangRes.SELECTDATA());
			}
		}
		befroreDelete();
		super.doAction(evt);
	}



	@Override
	protected boolean isActionEnable() {
		if(getModel().getContext().getPk_org() == null || getModel().getSelectedData() == null)
			return false;
		//如果是列表页面，则只要列表有数据，就显示删除按钮
		if(isCurlistView()){
			return super.isActionEnable();
		}
		//对于卡片页面，需要判断当前数据是否来自申请，如果来自申请，则按钮不可用
		Object vo = getModel().getSelectedData();
		Integer billsource = (Integer)((SuperVO) vo).getAttributeValue(ICommonConst.BILL_SOURCE);
		if(null == getContext().getPk_org() || (billsource!=null&&(ICommonConst.BILL_SOURCE_REG != billsource.intValue())))
			return false;
		//判断是否有权删除
		boolean hasDelPermission = hasPermission(vo);
		if(!hasDelPermission)
			return false;
		return super.isActionEnable();
	}

	/**
	 * 如果是列表页面，则选择的数据必须都是来自登记才能删除
	 * 卡片不存在这个问题，因为如果来自申请，则按钮置灰
	 */
	protected void befroreDelete() throws Exception {
		if(isCurcardView())
			return;
		Object[] selDatas = ((BillManageModel)getModel()).getSelectedOperaDatas();
		if(ArrayUtils.isEmpty(selDatas))
			return;
		for(Object selData : selDatas) {
			Object billSource = ((SuperVO) selData).getAttributeValue(ICommonConst.BILL_SOURCE);
			if(!hasPermission(selData))
				throw new Exception(ResHelper.getString("6017basedoc","06017basedoc1475")/*@res "您无权对选中的数据执行删除操作!"*/);
			if(billSource!=null&&ICommonConst.BILL_SOURCE_REG != ((Integer)billSource).intValue()){}
//				throw new Exception(ResHelper.getString("6017basedoc","06017basedoc1474")/*@res "只能删除登记节点新增的数据!"*/);
		}
	}

	public boolean isCurlistView(){
	        return listView.isComponentVisible();
	}

	public boolean isCurcardView(){
	        return cardView.isComponentVisible();
	}


	public ITabbedPaneAwareComponent getListView() {
		return listView;
	}


	public void setListView(ITabbedPaneAwareComponent listView) {
		this.listView = listView;
	}


	public ITabbedPaneAwareComponent getCardView() {
		return cardView;
	}


	public void setCardView(ITabbedPaneAwareComponent cardView) {
		this.cardView = cardView;
	}

	protected boolean hasPermission(Object obj){
		return DataPermissionFacade.isUserHasPermissionByMetaDataOperation(
				this.getContext().getPk_loginUser(), getResourceCode(), getMdOperateCode(), this.getContext().getPk_group(), obj);
	}

}