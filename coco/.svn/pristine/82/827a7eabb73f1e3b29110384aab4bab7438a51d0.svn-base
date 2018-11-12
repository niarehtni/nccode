package nc.bs.hrsms.ta.sss.away.pagemodel;

import nc.bs.hrsms.pub.advpanel.mngdept.MngShopPanel;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
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
import nc.vo.ta.away.AwayRegVO;

public class ShopAwayRegListPageModel extends AdvancePageModel{

	/**
	 * 初始化个性化设置
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		// 主片段
		LfwView viewMain = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		setTimeDatasPrecision(viewMain);
	}

	/**
	 * 设置左侧边栏
	 */
	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
		return new IPagePanel[] { new CanvasPanel(), new MngShopPanel(), new SimpleQueryPanel() };
	}

	/**
	 * 设置右侧边栏
	 */
	@Override
	protected String getRightPage() {
		return null;
	}

	
	@Override
	protected String getQueryTempletKey() {
		return null;
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
