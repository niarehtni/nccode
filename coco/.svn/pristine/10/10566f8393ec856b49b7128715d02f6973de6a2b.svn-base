package nc.bs.hrsms.ta.sss.calendar;

import java.util.ArrayList;

import nc.bs.hrss.pub.advpanel.cata.CatagoryInfo;
import nc.bs.hrss.pub.advpanel.cata.ICatagoryDataProvider;
import nc.newinstall.util.StringUtil;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;

public class WorkCalendarListPanel implements ICatagoryDataProvider {

	@Override
	public CatagoryInfo[] getCatagoryData() {
		ArrayList<CatagoryInfo> list = new ArrayList<CatagoryInfo>();
		setCatagoryInfo(list, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0032")/*@res "按时段查看"*/, WorkCalendarConsts.WORKCALENDARAPP_TIME);
		setCatagoryInfo(list, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0033")/*@res "按日历查看"*/, WorkCalendarConsts.WORKCALENDARAPP_CAL);
		return list.toArray(new CatagoryInfo[0]);
	}

	/**
	 * 构造简单树的节点
	 *
	 * @param list
	 * @param title
	 * @param pageId
	 */
	private void setCatagoryInfo(ArrayList<CatagoryInfo> list, String title, String pk_node) {

		CatagoryInfo info = new CatagoryInfo();
		// 节点ID
		info.setPk_node(pk_node);
		// 父节点ID
		info.setPk_parent(null);
		// 树标题
		info.setTitle(title);
		// 附加参数
		info.setParam(null);
		String appId = AppLifeCycleContext.current().getApplicationContext().getAppId();
		if (!StringUtil.isEmpty(appId) && appId.equals(pk_node)) {
			info.setDefault(true);
		}
		list.add(info);
	}
}
