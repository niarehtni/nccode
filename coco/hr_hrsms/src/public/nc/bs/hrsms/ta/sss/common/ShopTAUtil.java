package nc.bs.hrsms.ta.sss.common;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.bs.hrsms.ta.sss.calendar.WorkCalendarConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.calendar.CalendarUtils;
import nc.hr.utils.StringPiecer;
import nc.itf.hi.IPsndocQryService;
import nc.itf.ta.ITimeItemQueryService;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.FieldSet;
import nc.uap.lfw.core.data.UnmodifiableMdField;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.bd.shift.SingleCardTypeEnum;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hrss.ta.calendar.QryConditionVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.ta.leave.LeaveConst;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;

import org.apache.commons.lang.ArrayUtils;

public class ShopTAUtil {

	/**
	 * 设置数据集字段的小数位数
	 * @param ds 
	 * @param timedecimal 小数位数
	 * @param filedIds 字段
	 */
	public static void setPrecision(Dataset ds, Integer timedecimal, String[] filedIds){
		for (String filedId : filedIds) {
			int index = ds.getFieldSet().nameToIndex(filedId);
			if (index >= 0) {
				FieldSet fieldSet = ds.getFieldSet();
				Field field = fieldSet.getField(filedId);
				if(field instanceof UnmodifiableMdField) 
					field = ((UnmodifiableMdField) field).getMDField();
				fieldSet.updateField(filedId, field);
				field.setPrecision(String.valueOf(timedecimal));
			}
		}
	}
	
	/**
	 * 判断是否启用了哺乳假
	 * 
	 * @return
	 */
	public static boolean isLactationUnSeal(){
		String pk_leavetype = LeaveConst.LEAVETYPE_SUCKLE;
		// 最新考勤档案所在组织
		String pk_org = ShopTaAppContextUtil.getPk_tbm_org();
		TimeItemCopyVO timeItemCopyVO = ShopTAUtil.getTimeItemCopyVO(pk_org, pk_leavetype);
		if (timeItemCopyVO != null && timeItemCopyVO.getEnablestate() == IPubEnumConst.ENABLESTATE_ENABLE) {
			return true;
		}
		return false;
	}

	/**
	 * 将颜色转换为十六进制字符串
	 * 
	 * @param c
	 * @return
	 * @author haoy 2011-8-1
	 */
	public static String getHexDesc(Color c) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		StringBuilder buf = new StringBuilder("#");
		buf.append(r < 16 ? "0" + Integer.toHexString(r).toUpperCase() : Integer.toHexString(r).toUpperCase())
				.append(g < 16 ? "0" + Integer.toHexString(g).toUpperCase() : Integer.toHexString(g).toUpperCase())
				.append(b < 16 ? "0" + Integer.toHexString(b).toUpperCase() : Integer.toHexString(b).toUpperCase());
		return buf.toString();
	}
	
	/**
	 * 处理是否支持单次刷卡数据
	 * 
	 * @param shiftVO
	 * @return
	 */
	public static String transferBoolen2String(ShiftVO shiftVO) {
		UFBoolean issinglecard = shiftVO.getIssinglecard();
		if(issinglecard == null){
			return "";
		}
		if (!shiftVO.getIssinglecard().booleanValue()) {
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0028")/*@res"否"*/;
		}else{
			Integer cardType = shiftVO.getCardtype();
			if(SingleCardTypeEnum.ONLYBEGIN.toIntValue()==cardType){
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0162")/*@res"只刷上班卡"*/;
			}else if(SingleCardTypeEnum.ONLYEND.toIntValue()==cardType) {
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0163")/*@res"只刷下班卡"*/;
			}else {//SingleCardTypeEnum.ANYTIME.toIntValue()==cardType
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0164")/*@res"任意时间点"*/;
			}
		}
		
	}
	
	/**
	 * 根据休假类别PK和组织PK, 获得休假类别copy的PK
	 * 
	 * @param pk_org
	 * @param pk_leavetype
	 * @return
	 */
	public static TimeItemCopyVO getTimeItemCopyVO(String pk_org, String pk_leavetype) {
		TimeItemCopyVO timeItemCopyVO = null;
		// 查询休假类别copy的PK
		try {
			ITimeItemQueryService service = ServiceLocator.lookup(ITimeItemQueryService.class);
			timeItemCopyVO = service.queryCopyTypesByDefPK(pk_org, pk_leavetype, TimeItemVO.LEAVE_TYPE);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return timeItemCopyVO;
	}
	
	/**
	 * 查询部门下的人员
	 * 
	 * @return 
	 */
	public static List<PsnJobVO> queryPsnJobVOlist(boolean isContainSub){
		// 管理部门VO
		HRDeptVO mngDeptVO = SessionUtil.getMngDept();

		PsnJobCalendarVO[] psnvos = getPsnjobPks(new HRDeptVO[]{mngDeptVO}, isContainSub).toArray(new PsnJobCalendarVO[0]);

		String[] psnjobPks = StringPiecer.getStrArray(psnvos, PsnJobVO.PK_PSNJOB);

		/* 人员查询 */
		List<PsnJobVO> psnList = null;
		if(ArrayUtils.isEmpty(psnjobPks)){
			return null;
		}
		try {
			psnList = ServiceLocator.lookup(IPsndocQryService.class).queryPsninfoByPks(psnjobPks);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return psnList;
	}
	
	/**
	 * 查询部门下的人员的工作日历
	 * 
	 * @param pk_dept
	 * @param isContainSub
	 * @return
	 */
	public static List<PsnJobCalendarVO> getPsnjobPks(HRDeptVO[] deptVOs, boolean isContainSub) {
		
		QryConditionVO vo = (QryConditionVO) SessionUtil.getSessionBean().getExtendAttributeValue(WorkCalendarConsts.SESSION_QRY_CONDITIONS);
		
		PsnJobCalendarVO[] psnJobCalendarVOs = null;
		List<PsnJobCalendarVO> PsnJobCalendarVOList = new ArrayList<PsnJobCalendarVO>();
		
		// 查出管理部门下的员工工作日历数据
		if(!ArrayUtils.isEmpty(deptVOs)){
			for (HRDeptVO deptVO : deptVOs) {
				psnJobCalendarVOs = CalendarUtils.getDeptPsnCalendar(deptVO.getPk_dept(), isContainSub, vo.getBeginDate(), vo.getEndDate(), vo.getArrangeflag(), vo.getFromWhereSQL());
				if(!ArrayUtils.isEmpty(psnJobCalendarVOs)){
					PsnJobCalendarVOList.addAll(Arrays.asList(psnJobCalendarVOs));
				}
			}
		}
		return PsnJobCalendarVOList;
	}
	
	/**
	 * 根据管理部门构造查询条件
	 * 
	 * @return
	 */
	public static String getMngDeptWherePartSql() {
		HRDeptVO mngDept = SessionUtil.getMngDept();
		String mng_dept_innercode = null;
		String str = "";
		if (mngDept == null) {
			return null;
		}
		mng_dept_innercode = mngDept.getInnercode();
		str += " org_dept.innercode like '" + mng_dept_innercode + "%'";
		return str;

	}
}
