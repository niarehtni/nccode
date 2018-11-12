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
	 * 初始化个性化设置
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		
		// 主片段
		LfwView viewMain = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
//		// 设置参照Controller
		setRefnodesDsListener(viewMain);
//		// 页面特殊设置
//		setPageSepcial(viewMain);
		setTimeDatasPrecision(viewMain);
	}

	/**
	 * 设置参照Controller
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
	 * 设置考勤数据的小时位数<br/>
	 * String[]待设置的考勤数据字段数组<br/>
	 * 
	 * @return
	 */
	protected String[] getTimeDataFields() {
		return new String[] { AwayRegVO.AWAYHOUR };
	}
	/**
	 * 根据考勤规则设置考勤数据的小时位数
	 * 
	 */
	private void setTimeDatasPrecision(LfwView viewMain) {
		// 考勤数据
		String[] timeDatas = getTimeDataFields();
		if (timeDatas == null || timeDatas.length == 0) {
			return;
		}
		Dataset[] dss = viewMain.getViewModels().getDatasets();
		if (dss == null || dss.length == 0) {
			return;
		}
		// 考勤位数
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
