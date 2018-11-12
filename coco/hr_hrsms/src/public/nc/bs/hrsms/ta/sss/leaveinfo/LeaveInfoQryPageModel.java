package nc.bs.hrsms.ta.sss.leaveinfo;

import java.util.Map;

import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.mngdept.MngDeptPanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.bs.hrsms.ta.sss.leaveinfo.ctrl.LeaveInfoQryViewMain;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.FieldSet;
import nc.uap.lfw.core.data.UnmodifiableMdField;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.timerule.TimeRuleVO;

/**
 * 员工休假记录
 * 
 * @author qiaoxp
 * 
 */
public class LeaveInfoQryPageModel extends AdvancePageModel {

	/**
	 * 个性化设置
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		// 在applicationContext中添加属性考勤档案和考勤规则
		TaAppContextUtil.addTaAppContext();
		Map<String, String[]> periodMap = TBMPeriodUtil.getPeriodMap(TaAppContextUtil.getHROrg());
		TaAppContextUtil.setTBMPeriodVOMap(periodMap);
		// 主片段
		LfwView viewMain = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		// 根据考勤规则设置考勤数据的小时位数
		setTimeDatasPrecision(viewMain);
	}

	private void setTimeDatasPrecision(LfwView viewMain) {
		// 考勤数据
		String[] timeDatas = new String[] { LeaveBalanceVO.LASTDAYORHOUR, LeaveBalanceVO.CHANGELENGTH, LeaveBalanceVO.CURDAYORHOUR, LeaveBalanceVO.REALDAYORHOUR, LeaveBalanceVO.YIDAYORHOUR, LeaveBalanceVO.RESTDAYORHOUR,
				LeaveBalanceVO.FREEZEDAYORHOUR, LeaveBalanceVO.USEFULRESTDAYORHOUR };
		Dataset ds = viewMain.getViewModels().getDataset(LeaveInfoQryViewMain.DATASET_LEAVEINFO);
		if (ds == null) {
			return;
		}
		// 考勤位数
		int pointNum = getPointNum();
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

	// 默认精度
	public static final int DEFAULT_PRECISION = 2;

	/**
	 * 根据考勤规则获得考勤数据的精度
	 * 
	 * @return
	 */
	private int getPointNum() {
		TimeRuleVO timeRuleVO = TaAppContextUtil.getTimeRuleVO();
		if (timeRuleVO == null) {
			// 没有考勤规则的情况，设置默认值
			return DEFAULT_PRECISION;
		}
		int pointNum = Math.abs(timeRuleVO.getTimedecimal());
		return pointNum;
	}

	@Override
	protected String getFunCode() {
		return "E2060911";
	}

	@Override
	protected String getQueryTempletKey() {
		return null;
	}

	@Override
	protected String getRightPage() {
		return null;
	}

	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
		return new IPagePanel[] { new CanvasPanel(), new MngDeptPanel(), new SimpleQueryPanel() };
	}

}