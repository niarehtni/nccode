package nc.bs.hrsms.ta.empleavereg4store;

import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.mngdept.MngDeptPanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.FieldSet;
import nc.uap.lfw.core.data.MdDataset;
import nc.uap.lfw.core.data.UnmodifiableMdField;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.vo.ta.leave.LeaveRegVO;


/**
 * @author renyp
 * @date 2015-4-24
 * @ClassName功能名称：店员休假查询model类
 * @Description功能描述：功能是
 * 
 */


public class EmpLeaveReg4StorePageModel extends AdvancePageModel {
	// 默认精度
	public static final int DEFAULT_PRECISION = 2;
	/**
	 * @author renyp
	 * @date 2015-4-24
	 * @Description方法功能描述：作用是个性化设置
	 * 
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		// 主片段
		LfwView viewMain = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		setTimeDatasPrecision(viewMain);
	}




	@Override
	protected String getFunCode() {
//		return "E20400904";
//		return "E2060510";
		return "E20600909";
//		return null;
	}

	@Override
	protected String getQueryTempletKey() {
//		return null;
		return "E20600909";
//		return "E2060510";	
		}

	@Override
	protected String getRightPage() {
		return null;
	}
/**
 * @author renyp
 * @date 2015-4-24
 * @Description方法功能描述：作用是返回左侧栏， 部门选择面板 ，查询面板
 * 
 */
	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
		return new IPagePanel[] { new CanvasPanel(), new MngDeptPanel(), new SimpleQueryPanel() };
//		return new IPagePanel[] { new CanvasPanel(),  new SimpleQueryPanel() };
	}

	/**
	 * 设置考勤数据的小时位数<br/>
	 * String[]待设置的考勤数据字段数组<br/>
	 * 
	 * @return
	 */
	protected String[] getTimeDataFields() {
		return new String[] { LeaveRegVO.LEAVEHOUR, LeaveRegVO.LACTATIONHOUR};
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