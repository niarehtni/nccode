package nc.bs.hrsms.ta.sss.common;

import nc.bs.hrsms.pub.advpanel.mngdept.MngShopPanel;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.FieldSet;
import nc.uap.lfw.core.data.MdDataset;
import nc.uap.lfw.core.data.UnmodifiableMdField;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.vo.ta.timerule.TimeRuleVO;

public class ShopTaListBasePageModel extends AdvancePageModel {
	// 默认精度
	public static final int DEFAULT_PRECISION = 2;

	/**
	 * 初始化个性化设置
	 */
	@Override
	protected void initPageMetaStruct() {

		super.initPageMetaStruct();
		// 在applicationContext中添加属性考勤档案和考勤规则
		//TaAppContextUtil.addTaAppContext();
		// 主片段
		LfwView viewMain = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		// 根据考勤规则设置考勤数据的小时位数
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

	/**
	 * 设置考勤数据的小时位数<br/>
	 * String[]待设置的考勤数据字段数组<br/>
	 *
	 * @return
	 */
	protected String[] getTimeDataFields() {
		return null;
	}

	@Override
	protected String getQueryTempletKey() {
		return null;
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
		int pointNum = getPointNum();
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

	/**
	 * 获得考勤位数
	 * 
	 * @return
	 */
	private int getPointNum() {
		TimeRuleVO timeRuleVO = TaAppContextUtil.getTimeRuleVO();
		if (timeRuleVO == null) {
			// 没有考勤规则的情况，设置默认值
			return ShopTaListBasePageModel.DEFAULT_PRECISION;
		}
		int pointNum = Math.abs(timeRuleVO.getTimedecimal());
		return pointNum;
	}

}
