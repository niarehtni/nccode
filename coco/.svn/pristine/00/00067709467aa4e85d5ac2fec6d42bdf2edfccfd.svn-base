package nc.bs.hrsms.ta.sss.ShopAttendance.pagemodel;

import java.awt.Color;

import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.utils.TAUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.ui.ta.dataprocess.view.TimeDataColorUtils;
import nc.ui.ta.pub.IColorConst;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.dataprocess.TimeDataVO;
import nc.vo.ta.lateearly.LateEarlyVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 我的出勤情况的PageMode
 * 
 * @author mayif
 * 
 */
public class ShopAttForEmpPageModel extends AdvancePageModel {

	// 状态：机器考勤浏览
	public static final int STATUS_MACHINE = 1;
	// 状态：手工考勤浏览
	public static final int STATUS_MANUAL = 2;

	// 数据集：机器考勤
	public static final String DATASET_MACHINE = "dsMachineData";
	// 数据集：手工考勤
	public static final String DATASET_MANUAL = "dsManualData";

	// 控件：机器考勤表格
	public static final String COMP_GRID_MACHINE_DATA = "tblMachineData";
	// 控件：手工考勤表格
	public static final String COMP_GRID_MANUAL_DATA = "tblManualData";

	// 字段：查询条件：起始日期
	public static final String FIELD_BEGIN_DATE = "begindate";
	// 字段：查询条件：终止日期
	public static final String FIELD_END_DATE = "enddate";
	// 字段：查询条件：仅查询异常数据
	public static final String FIELD_ONLY_SHOW_EXCEPTION = "onlyShowException";

	// 字段：机器考勤：人员主键
	public static final String FIELD_MACHINE_PERSON_PK = "pk_psndoc";
	// 字段：机器考勤：日期
	public static final String FIELD_MACHINE_CALENDAR = "calendar";
	// 字段：机器考勤：组织
	public static final String FIELD_MACHINE_PKORG = "pk_org";

	// session变量：单元格颜色
	public static final String CSES_COLOR_MAP = "colorMap";
	// session变量：单元格是否可编辑
	public static final String CSES_EDIT_LIST = "editList";

	@Override
	protected String getFunCode() {
		return "E20600907";
	}

	/**
	 * 初始化个性化设置
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		// 在applicationContext中添加属性考勤档案和考勤规则
		TaAppContextUtil.addTaAppContext();
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
		String[] timeDatas = null;
		Integer timedecimal = timeRuleVO.getTimedecimal();
		LfwView view = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		Dataset dsMachine = ViewUtil.getDataset(view, DATASET_MACHINE);
		timeDatas = new String[]{TimeDataVO.MIDWAYOUTTIME};
		TAUtil.setPrecision(dsMachine, timedecimal, timeDatas);
		Dataset dsManual = ViewUtil.getDataset(view, DATASET_MANUAL);
		timeDatas = new String[]{LateEarlyVO.LATELENGTH, LateEarlyVO.EARLYLENGTH, LateEarlyVO.ABSENTHOUR, LateEarlyVO.NIGHTABSENTHOUR};
		TAUtil.setPrecision(dsManual, timedecimal, timeDatas);
		
	}
	
	@Override
	public String getBusinessEtag() {
		return String.valueOf(Math.random());
	}

	/**
	 * 获取单元格颜色描述的字符串
	 * 
	 * @param tiData
	 * @param laData
	 * @return
	 * @author haoy 2011-7-7
	 */
	public static String getTimeDataColorInJSON(TimeDataVO[] tiData, LateEarlyVO[] laData) {
		StringBuilder buf = new StringBuilder("");
		if (null != tiData && tiData.length > 0) {
			String[] attr = tiData[0].getAttributeNames();
			for (TimeDataVO vo : tiData) {
				if ((vo.getIsmidoutabnormal()!=null&&vo.getIsmidoutabnormal().booleanValue()
						||vo.getMidwayoutcount()!=null&&vo.getMidwayoutcount()<0)//加或者是为了升级时显示设置的
						&&vo.getIsmidwayout()==1) {
					if (buf.length() > 0)
						buf.append(",");
					buf.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toStdString()).append(TimeDataVO.ISMIDOUTABNORMAL).append("\":\"").append("Y").append("\"");
				}
				for (String s : attr) {
					Color c = TimeDataColorUtils.getColor(vo, s);
					if (null != c) {
						if (buf.length() > 0)
							buf.append(",");
						buf.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toStdString()).append(s).append("\":\"").append(TAUtil.getHexDesc(c)).append("\"");
					}
				}
			}

		}
		if (null != laData && laData.length > 0) {
			String[] attr = laData[0].getAttributeNames();
			for (LateEarlyVO vo : laData) {
				for (String s : attr) {
//					Color c = LateEarlyColorUtils.getColor(vo, s);
					Color c = getColor(vo, s);
					if (null != c) {
						if (buf.length() > 0)
							buf.append(",");
						buf.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toStdString()).append(s).append("\":\"").append(TAUtil.getHexDesc(c)).append("\"");
					}
				}
			}
		}
		return "{" + buf.toString() + "}";
	}

	private static Color getColor(LateEarlyVO vo, String key ) {
		
		       //班次为空
				if(StringUtils.isBlank(vo.getPk_shift())){
					return IColorConst.COLOR_NONPSNCALENDAR;
				}
				//公休
//				if(ShiftVO.PK_GX.equals(vo.getPk_shift())){
//					return null;
//				}
				//如果是时段字段
				if(ArrayUtils.contains(LateEarlyVO.STATUS_ARRAY, key)){
					Integer status = (Integer) vo.getAttributeValue(key);
					if(status==null)
						return null;
					switch(status){
					case LateEarlyVO.STATUS_LATEOREARLY:return IColorConst.COLOR_LATEEARLY;	//迟到或早退
					case LateEarlyVO.STATUS_ABSENT:return IColorConst.COLOR_ABSENT;			//旷工
					default: return null;
					}
				}
				//如果是时长字段
				if(ArrayUtils.contains(LateEarlyVO.LENTH_ARRAY, key)||LateEarlyVO.NIGHTABSENTHOUR.equals(key)){
					UFDouble length = (UFDouble) vo.getAttributeValue(key);
					if(length!=null&&length.doubleValue()>0){
						//如果为迟到或早退
						if(LateEarlyVO.LATELENGTH.equals(key)||LateEarlyVO.EARLYLENGTH.equals(key))
							return IColorConst.COLOR_LATEEARLY;
						//否则为旷工或夜班旷工
						return IColorConst.COLOR_ABSENT;
					}
					return null;
				}
				return null;
			}
		
	@Override
	protected String getQueryTempletKey() {
		return null;
	}

	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
		return new IPagePanel[] { new CanvasPanel(), new SimpleQueryPanel() };
	}

	@Override
	protected String getRightPage() {
		return null;
	}

}