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
 * �б�ҳѡ������ģʽΪ����checkbox��ѡ
 * ���ɾ����ťʱ���жϵ�ǰ�����Ƿ񱻹�ѡ���ж���
 * �������ڵ��ݵǼ�ҳ��
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
		//������б�ҳ�棬��ֻҪ�б������ݣ�����ʾɾ����ť
		if(isCurlistView()){
			return super.isActionEnable();
		}
		//���ڿ�Ƭҳ�棬��Ҫ�жϵ�ǰ�����Ƿ��������룬����������룬��ť������
		Object vo = getModel().getSelectedData();
		Integer billsource = (Integer)((SuperVO) vo).getAttributeValue(ICommonConst.BILL_SOURCE);
		if(null == getContext().getPk_org() || (billsource!=null&&(ICommonConst.BILL_SOURCE_REG != billsource.intValue())))
			return false;
		//�ж��Ƿ���Ȩɾ��
		boolean hasDelPermission = hasPermission(vo);
		if(!hasDelPermission)
			return false;
		return super.isActionEnable();
	}

	/**
	 * ������б�ҳ�棬��ѡ������ݱ��붼�����ԵǼǲ���ɾ��
	 * ��Ƭ������������⣬��Ϊ����������룬��ť�û�
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
				throw new Exception(ResHelper.getString("6017basedoc","06017basedoc1475")/*@res "����Ȩ��ѡ�е�����ִ��ɾ������!"*/);
			if(billSource!=null&&ICommonConst.BILL_SOURCE_REG != ((Integer)billSource).intValue()){}
//				throw new Exception(ResHelper.getString("6017basedoc","06017basedoc1474")/*@res "ֻ��ɾ���Ǽǽڵ�����������!"*/);
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