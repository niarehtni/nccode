package nc.bs.hrsms.ta.sss.ShopAttendance.pagemodel;
import java.util.Map;

import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.cata.ICatagoryDataProvider;
import nc.bs.hrss.pub.advpanel.cata.TestCatagoryDataProvider;
import nc.bs.hrss.pub.advpanel.mngdept.MngDeptPanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;

public class ShopAttendanceMngPageModel extends AdvancePageModel{
	
	// 店员出勤情况————店长自助节点code
		public static final String TIMEDATAMNG_FUNCODE = "E20600907";

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
		}
		
		@Override
		protected String getQueryTempletKey() {
			return null;
		}

		@Override
		protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
			return new IPagePanel[] { new CanvasPanel(), new MngDeptPanel(), new SimpleQueryPanel() };
		}

		@Override
		protected String getRightPage() {
			return null;
		}

		@Override
		protected String getFunCode() {
			return TIMEDATAMNG_FUNCODE;
		}
	
	
	
	
}