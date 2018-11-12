package nc.bs.hrsms.ta.sss.away.pagemodel;

import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.PageModel;
import nc.bs.hrsms.ta.common.ctrl.ShopTaRegRefController;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.FieldSet;
import nc.uap.lfw.core.data.MdDataset;
import nc.uap.lfw.core.data.UnmodifiableMdField;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.refnode.NCRefNode;
import nc.vo.ta.away.AwayRegVO;

public class ShopAwayRegCardPageModel extends PageModel{
	
	/**
	 * ��ʼ�����Ի�����
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		
		// ��Ƭ��
		LfwView viewMain = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
//		// ���ò���Controller
		setRefnodesDsListener(viewMain);
//		// ҳ����������
//		setPageSepcial(viewMain);
		setTimeDatasPrecision(viewMain);
	}

	/**
	 * ���ò���Controller
	 */
	private void setRefnodesDsListener(LfwView viewMain) {
		NCRefNode refNode = (NCRefNode) viewMain.getViewModels().getRefNode("refnode_hrtaawayreg_pk_awaytype_timeitemname");
		refNode.setDataListener(ShopTaRegRefController.class.getName());
		
		NCRefNode agentpsnrefNode = (NCRefNode) viewMain.getViewModels().getRefNode("refnode_hrtaawayreg_pk_agentpsn_pk_psndoc_name");
		agentpsnrefNode.setDataListener(ShopTaRegRefController.class.getName());

		NCRefNode refNode1 = (NCRefNode) viewMain.getViewModels().getRefNode("refnode_hrtaawayreg_pk_psnjob_clerkcode");
		refNode1.setDataListener(ShopTaRegRefController.class.getName());
	}
	/**
	 * ���ÿ������ݵ�Сʱλ��<br/>
	 * String[]�����õĿ��������ֶ�����<br/>
	 * 
	 * @return
	 */
	protected String[] getTimeDataFields() {
		return new String[] { AwayRegVO.AWAYHOUR };
	}
	/**
	 * ���ݿ��ڹ������ÿ������ݵ�Сʱλ��
	 * 
	 */
	private void setTimeDatasPrecision(LfwView viewMain) {
		// ��������
		String[] timeDatas = getTimeDataFields();
		if (timeDatas == null || timeDatas.length == 0) {
			return;
		}
		Dataset[] dss = viewMain.getViewModels().getDatasets();
		if (dss == null || dss.length == 0) {
			return;
		}
		// ����λ��
		int pointNum = 2;
		for (Dataset ds : dss) {
			if (ds instanceof MdDataset) {
				for (String filedId : timeDatas) {
					int index = ds.getFieldSet().nameToIndex(filedId);
					if (index >= 0) {
						FieldSet fieldSet = ds.getFieldSet();
						Field field = fieldSet.getField(filedId);
						if(field instanceof UnmodifiableMdField) 
							field = ((UnmodifiableMdField) field).getMDField();
						fieldSet.updateField(filedId, field);
						
						field.setPrecision(String.valueOf(pointNum));
						
					}
				}
			}
		}
	}
}
