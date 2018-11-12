package nc.bs.hrsms.ta.sss.dailyreport;

import java.util.Map;

import nc.bs.hrsms.pub.advpanel.mngdept.MngShopPanel;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.cata.ICatagoryDataProvider;
import nc.bs.hrss.pub.advpanel.cata.TestCatagoryDataProvider;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.utils.TAUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.vo.ta.monthstat.MonthWorkVO;
import nc.vo.ta.timerule.TimeRuleVO;

/**
 * 员工考勤日报PageModel
 * 
 * @author liuhongd
 * 
 */
public class DailyReportForClePageModel extends AdvancePageModel {

	@Override
	protected String getFunCode() {
		return "E20600919";
	}

	@Override
	protected void initPageMetaStruct() {
		SessionUtil.getAppSession().setAttribute(ICatagoryDataProvider.SID_CATAGORY_PROVIDER,
				TestCatagoryDataProvider.class.getName());
		//
		super.initPageMetaStruct();
		// 在applicationContext中添加属性考勤档案和考勤规则
		TaAppContextUtil.addTaAppContext();
		Map<String, String[]> periodMap = TBMPeriodUtil.getPeriodMap(TaAppContextUtil.getHROrg());
		TaAppContextUtil.setTBMPeriodVOMap(periodMap);
		// 设置小数位数
		setPrecision();
	}
	
	/**
	 * 根据考勤规则设置字段保留小数位数
	 * 
	 */
	private void setPrecision(){
		TimeRuleVO timeRuleVO = TaAppContextUtil.getTimeRuleVO();
		if(timeRuleVO == null){
			return;
		}
		String[] fields = new String[]{MonthWorkVO.ACTUALWORKDAYS, MonthWorkVO.ACTUALWORKHOURS, MonthWorkVO.WORKDAYS, MonthWorkVO.WORKHOURS};
		Integer mreportdecimal = timeRuleVO.getMreportdecimal();
		LfwView view = getPageMeta().getView("DailyReportDetail");
		Dataset dsMthDetail = ViewUtil.getDataset(view, "dsDayDetail");
		TAUtil.setPrecision(dsMthDetail, mreportdecimal, fields);
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
		return new IPagePanel[] { new CanvasPanel(), new MngShopPanel(), new SimpleQueryPanel() };
	}

}
