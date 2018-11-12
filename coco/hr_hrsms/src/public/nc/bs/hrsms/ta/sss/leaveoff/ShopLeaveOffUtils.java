package nc.bs.hrsms.ta.sss.leaveoff;

import java.util.ArrayList;
import java.util.List;

import nc.hr.utils.ResHelper;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.FormElement;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.FieldSet;
import nc.uap.lfw.core.data.UnmodifiableMdField;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leaveoff.LeaveoffVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import uap.web.bd.pub.AppUtil;

public class ShopLeaveOffUtils {

	/**
	 * ���ÿ����ֶ�
	 * 
	 * @return
	 */
	public static final String[] getPageTimeDataFieldIDs() {
		String prefix = LeaveoffVO.PK_LEAVEREG + "_";

		List<String> timeDataFieldList = new ArrayList<String>();
		timeDataFieldList.add(LeaveoffVO.REALLYLEAVEHOUR);/* ʵ��ʱ�� */
		timeDataFieldList.add(LeaveoffVO.DIFFERENCEHOUR);/* ����ʱ�� */

		String[] regTimeDataFieldIds = new String[] { LeaveRegVO.LACTATIONHOUR,
				LeaveoffVO.REGLEAVEHOURCOPY,LeaveRegVO.LEAVEHOUR, LeaveRegVO.RESTEDDAYORHOUR,
				LeaveRegVO.REALDAYORHOUR, LeaveRegVO.RESTDAYORHOUR,
				LeaveRegVO.FREEZEDAYORHOUR, LeaveRegVO.USEFULDAYORHOUR };

		for (String fieldId : regTimeDataFieldIds) {
			timeDataFieldList.add(fieldId);
		}
		for (String fieldId : regTimeDataFieldIds) {
			timeDataFieldList.add(prefix + fieldId);
		}
		return timeDataFieldList.toArray(new String[0]);
	}
	
	/**
	 * ��ʱ���ֶ���ӵ�λ��ʾ
	 * 
	 * @param from
	 * @param elementId
	 * @param timeitemunit
	 * @param text
	 */
	public static void setFormElemTextByUnit(FormComp from, String elementId,Integer timeitemunit, String text) {
		FormElement element = from.getElementById(elementId);
		if (element == null) {
			return;
		}
		if (timeitemunit != null && TimeItemCopyVO.TIMEITEMUNIT_DAY == timeitemunit) {
			element.setText(text + ResHelper.getString("c_ta-res", "0c_ta-res0001")/* @ res "(��)"*/);
		} else if (timeitemunit != null && TimeItemCopyVO.TIMEITEMUNIT_HOUR == timeitemunit) {
			element.setText(text + ResHelper.getString("c_ta-res", "0c_ta-res0002")/* @ res "(Сʱ)" */);
		} else {
			element.setText(text);
		}
	}

	/**
	 * ����Form����ʾ/����ʾ
	 * 
	 * @param from
	 * @param elementId
	 * @param visible
	 */
	public static void setFormElementVisible(FormComp from, String elementId,
			boolean visible) {
		FormElement element = from.getElementById(elementId);
		
		if (element != null) {
			if(AppUtil.getAppAttr(elementId) == null){
				AppUtil.addAppAttr(elementId, UFBoolean.valueOf(element.isVisible()));
			}
			element.setVisible(visible && ((UFBoolean)AppUtil.getAppAttr(elementId)).booleanValue());
		}
	}

	/**
	 * ����Form���Ƿ��������
	 * 
	 * @param from
	 * @param elementId
	 * @param visible
	 */
	public static void setFormElementNullAble(FormComp from, String elementId,
			boolean nullAble) {
		FormElement element = from.getElementById(elementId);
		if (element != null) {
			element.setNullAble(nullAble);
		}
	}

	/**
	 * ����Dataset���Ƿ��������
	 * 
	 * @param from
	 * @param elementId
	 * @param visible
	 */
	public static void setDatasettNullAble(Dataset ds, String fieldId,
			boolean nullAble) {
		FieldSet fieldSet = ds.getFieldSet();
		Field field = fieldSet.getField(fieldId);
		if (field != null) {
			if (field instanceof UnmodifiableMdField)
				field = ((UnmodifiableMdField) field).getMDField();
			fieldSet.updateField(fieldId, field);
			field.setNullAble(nullAble);
		}
	}
}
