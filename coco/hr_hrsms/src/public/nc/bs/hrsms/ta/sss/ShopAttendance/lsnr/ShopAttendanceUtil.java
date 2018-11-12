package nc.bs.hrsms.ta.sss.ShopAttendance.lsnr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.hrsms.ta.sss.ShopAttendance.pagemodel.ShopAttForEmpPageModel;
import nc.bs.hrsms.ta.sss.ShopAttendance.pagemodel.ShopAttendanceForBatchPageModel;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.utils.TAUtil;
import nc.bs.hrss.ta.utils.TBMPsndocUtil;
import nc.hr.utils.InSQLCreator;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.uap.lfw.core.comp.GridColumn;
import nc.uap.lfw.core.comp.GridColumnGroup;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.uap.lfw.jsp.uimeta.UILayoutPanel;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.uap.lfw.jsp.uimeta.UITabComp;
import nc.uap.lfw.jsp.uimeta.UITabItem;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.ta.dailydata.view.DisplayUtils;
import nc.ui.ta.pub.IColorConst;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.dailydata.IMidOutData;
import nc.vo.ta.dailydata.IWTCount;
import nc.vo.ta.dataprocess.TimeDataVO;
import nc.vo.ta.lateearly.LateEarlyUtils;
import nc.vo.ta.lateearly.LateEarlyVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.psndoc.TbmPropEnum;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class ShopAttendanceUtil {

	// �ؼ������ڱ�񣺷��飺��һʱ��
	public static final String COMP_GRID_GROUP_ONE = "one";
	// �ؼ������ڱ�񣺷��飺�ڶ�ʱ��
	public static final String COMP_GRID_GROUP_TWO = "two";
	// �ؼ������ڱ�񣺷��飺����ʱ��
	public static final String COMP_GRID_GROUP_THREE = "three";
	// �ؼ������ڱ�񣺷��飺����ʱ��
	public static final String COMP_GRID_GROUP_FOUR = "four";

	// ��������������ҳǩ
	public static final String TAB_TIME_DATA = "tabTimeData";
	// ҳǩ���������ݣ���������
	public static final String TAB_ITEM_MACHINE = "tabMachine";
	// ҳǩ���������ݣ��ֹ�����
	public static final String TAB_ITEM_MANUAL = "tabManual";

	/**
	 * ���ҳ�濼������
	 * 
	 * @param ctx
	 *            ҳ��������
	 * @param tvos
	 *            ������������
	 * @param lvos
	 *            �ֹ���������
	 * @author haoy 2011-6-13
	 */
	public static void fillData(AppLifeCycleContext ctx, TimeDataVO[] tvos, LateEarlyVO[] lvos, int tbm_prop) {

		LfwView viewMain = ctx.getViewContext().getView();
        
		
		// ����ҳǩ����ʾ
		setTabLayoutDisplay(ctx, viewMain, tvos, lvos, tbm_prop);
		GridComp gridMachine = (GridComp) viewMain.getViewComponents().getComponent(ShopAttForEmpPageModel.COMP_GRID_MACHINE_DATA);
		GridComp gridManual = (GridComp) viewMain.getViewComponents().getComponent(ShopAttForEmpPageModel.COMP_GRID_MANUAL_DATA);
		
	
		
		// ������ʾ��
		adjustWorkTimeColumn(tvos, gridMachine);
		adjustWorkTimeColumn(lvos, gridManual);

		// Ϊ���������ɫ
//		ctx.getApplicationContext().getClientSession().setAttribute(TimeDataForEmpPageModel.CSES_COLOR_MAP,TimeDataForEmpPageModel.getTimeDataColorInJSON(tvos, lvos));
//		ctx.getApplicationContext().addBeforeExecScript("window.colorMap = null");
		// ���ɱ༭�ĵ�Ԫ����Ϣ���ݵ�ǰ̨
//		ctx.getApplicationContext().getClientSession().setAttribute(TimeDataForEmpPageModel.CSES_EDIT_LIST, getTimeDataEditable(lvos));
//		ctx.getApplicationContext().addBeforeExecScript("window.editList = null");

	}

	/**
	 * ȡ�ÿɱ༭�ĵ�Ԫ��
	 * 
	 * @param laData
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getTimeDataEditable(LateEarlyVO[] laData) {
		StringBuilder buf = new StringBuilder("");
		if (null != laData && 0 < laData.length) {
			// ��ȡ���еİ����
			HashMap<String, ShiftVO> map = new HashMap<String, ShiftVO>();
			List<String> pk_shift_List = new ArrayList<String>();
			for (LateEarlyVO vo : laData) {
				if (StringUtil.isEmpty(vo.getPk_shift()))
					continue;
				if (!pk_shift_List.contains(vo.getPk_shift())) {
					pk_shift_List.add(vo.getPk_shift());
				}
			}
			String condition = "";
			Collection<ShiftVO> shiftScope = null;
			
//			LfwView viewMain = AppLifeCycleContext.current().getWindowContext().getCurrentViewContext().getView();
//					
//			GridComp gridManual = (GridComp) viewMain.getViewComponents().getComponent(ShopAttForEmpPageModel.COMP_GRID_MANUAL_DATA);
//			if(pk_shift_List.size()==0){
//				// �������ݵ�ʱ��η���
////				String[] groups = new String[] { COMP_GRID_GROUP_ONE, COMP_GRID_GROUP_TWO};
//				((GridColumnGroup) gridManual.getColumnById("one")).setVisible(false);
//				((GridColumnGroup) gridManual.getColumnById("two")).setVisible(false);
//			}
//			else{
//				
//				((GridColumnGroup) gridManual.getColumnById("one")).setVisible(true);
//				((GridColumnGroup) gridManual.getColumnById("two")).setVisible(true);
//				
//			}
			
			
			try {
				condition = LateEarlyVO.PK_SHIFT + " in ("
						+ (new InSQLCreator().getInSQL(pk_shift_List.toArray(new String[0]))) + ")";
				shiftScope = new BaseDAO().retrieveByClause(ShiftVO.class, condition);
			} catch (DAOException e) {
				new HrssException(e).deal();
			} catch (BusinessException e1) {
				new HrssException(e1).deal();
			}
			for(ShiftVO shiftVO : shiftScope){
				map.put(shiftVO.getPk_shift(), shiftVO);
			}
			// �����пɱ༭�ĵ�Ԫ����
			String[] attrs = laData[0].getAttributeNames();
			for (LateEarlyVO vo : laData) {
				ShiftVO bclb = map.get(vo.getPk_shift());
				for (String attr : attrs) {
					boolean editable = LateEarlyUtils.isEditable(vo, bclb, attr);
					if (editable){
						if (buf.length() > 0)
							buf.append(",");
						buf.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toStdString()).append(attr).append("\":\"").append("right\"");
					}
				}
			}
		}
		return "{" + buf.toString() + "}";
	}

	/**
	 * ����ҳǩ����ʾ.<br/>
	 * 
	 * @param ctx
	 *            ҳ��������
	 * @param viewMain
	 * @param tvos
	 *            ������������
	 * @param lvos
	 *            �ֹ���������
	 */
	private static void setTabLayoutDisplay(AppLifeCycleContext ctx, LfwView viewMain, TimeDataVO[] tvos,
			LateEarlyVO[] lvos, int tbm_prop) {

		UIMeta um = (UIMeta) ctx.getViewContext().getUIMeta();
		UITabComp tabComp = (UITabComp) um.findChildById(TAB_TIME_DATA);
		List<UILayoutPanel> itemList = tabComp.getPanelList();

		int item_index_machine = 0;
		int item_index_manual = 0;
		for (int i = 0; i < itemList.size(); i++) {
			UITabItem item = (UITabItem) itemList.get(i);
			if (TAB_ITEM_MACHINE.endsWith(item.getId())) {// ��������
				if (null != tvos && tvos.length > 0) {
					Dataset dsMachine = viewMain.getViewModels().getDataset(ShopAttForEmpPageModel.DATASET_MACHINE);
					TimeDataVO[] curPageTvos = DatasetUtil.paginationMethod(dsMachine, tvos);
					new SuperVO2DatasetSerializer().serialize(curPageTvos, dsMachine, Row.STATE_NORMAL);
					dsMachine.setRowSelectIndex(0);
					item.setVisible(true);
					ctx.getApplicationContext().getClientSession().setAttribute(ShopAttForEmpPageModel.CSES_COLOR_MAP,ShopAttForEmpPageModel.getTimeDataColorInJSON(curPageTvos, null));
					ctx.getApplicationContext().addBeforeExecScript("setColorMap();");
				} else {
					if (null != lvos && lvos.length > 0) {// �޻������������ֹ����ڣ������ػ�������ҳǩ
						item.setVisible(false);
					} else {
						if(tbm_prop == TbmPropEnum.MANUAL_CHECK.toIntValue()){
							item.setVisible(false); //�޻������ڡ����ֹ������ҿ��ڵ������ڷ�ʽΪ�ֹ����ڣ������ػ�������ҳǩ
						}else{
							item.setVisible(true);// �޻������ڡ����ֹ������ҿ��ڵ������ڷ�ʽ��Ϊ�ֹ����ڣ�����ʾ��������ҳǩ
						}
						
					}
				}
			} else {// �ֹ�����
				if (null != lvos && lvos.length > 0) {
					item_index_manual = i;
					Dataset dsManual = viewMain.getViewModels().getDataset(ShopAttForEmpPageModel.DATASET_MANUAL);
					SessionBean bean = SessionUtil.getSessionBean();
					UFLiteralDate begindate=(UFLiteralDate)bean.getExtendAttributeValue(ShopAttendanceForBatchPageModel.FLD_BEGIN);
					UFLiteralDate enddate=(UFLiteralDate)bean.getExtendAttributeValue(ShopAttendanceForBatchPageModel.FLD_END);
					String newClass=(String)bean.getExtendAttributeValue(ShopAttendanceForBatchPageModel.FLD_NEW_CLASS);
					String editdate=(String)bean.getExtendAttributeValue(ShopAttendanceForBatchPageModel.FLD_EDITDATE);
					String datestatus=(String)bean.getExtendAttributeValue(ShopAttendanceForBatchPageModel.FLD_DATESTATUS);
					boolean isBatchEdit=(Boolean) bean.getExtendAttributeValue(ShopAttendanceForBatchPageModel.ISBATCHEDIT);
				
					
					for(int j=0;j<lvos.length;j++){
						if(isBatchEdit){
							if(begindate!=null||enddate!=null){
								if(lvos[j].getDate().before(enddate.getDateAfter(1))&&lvos[j].getDate().after(begindate.getDateBefore(1))){
									if(lvos[j].getPk_shift().equals(newClass)){
										if(editdate.equals("����ʱ��")){
											if(datestatus.equals("����")){
												lvos[j].setOnebeginstatus(0);
												lvos[j].setTwoendstatus(0);
											}else if(datestatus.equals("�ٵ�������")){
												lvos[j].setOnebeginstatus(1);
												lvos[j].setTwoendstatus(1);
												
											}else if(datestatus.equals("δ����")){
												lvos[j].setOnebeginstatus(2);
												lvos[j].setTwoendstatus(2);
											}
										}
									}
								}
							}
						}
							
						
						
						//������������Ĭ������Ϊδ����
						if(lvos[j].getOnebeginstatus()==null||lvos[j].getOnebeginstatus()==-1){
							if(ShiftVO.PK_GX.equals(lvos[j].getPk_shift())){
								lvos[j].setOnebeginstatus(0);
							}else{
								lvos[j].setOnebeginstatus(2);
							}
						} 
						if(lvos[j].getTwoendstatus()==null||lvos[j].getTwoendstatus()==-1){
							if(ShiftVO.PK_GX.equals(lvos[j].getPk_shift())){
								lvos[j].setTwoendstatus(0);
							}else{
								lvos[j].setTwoendstatus(2);
							}
						}
					}
					
					
					LateEarlyVO[] curPageLvos = DatasetUtil.paginationMethod(dsManual, lvos);
					new SuperVO2DatasetSerializer().serialize(curPageLvos, dsManual, Row.STATE_NORMAL);
					dsManual.setRowSelectIndex(0);
					dsManual.setEnabled(true);
					item.setVisible(true);
					ctx.getApplicationContext().getClientSession().setAttribute(ShopAttForEmpPageModel.CSES_COLOR_MAP,ShopAttForEmpPageModel.getTimeDataColorInJSON(null, curPageLvos));
					ctx.getApplicationContext().getClientSession().setAttribute(ShopAttForEmpPageModel.CSES_EDIT_LIST, getTimeDataEditable(curPageLvos));
					ctx.getApplicationContext().addBeforeExecScript("setColorMap();setEditList()");
				} else {
					if (null != tvos && tvos.length > 0) { //���ֹ��������л������ڣ��������ֹ�����
						item.setVisible(false);
					}else{
						if(tbm_prop == TbmPropEnum.MANUAL_CHECK.toIntValue()){
							item.setVisible(true);
						}else{
							item.setVisible(false);
						}
					}
					
				}
			}
		}
		// ��λҳǩ
		String currentItem = (String) ctx.getApplicationContext().getAppAttribute("HrssCurrentItem");
		if(!StringUtils.isEmpty(currentItem) && "1".equals(currentItem)){
			ctx.getApplicationContext().removeAppAttribute("HrssCurrentItem");
		}else{
			if (ArrayUtils.isEmpty(tvos) && !ArrayUtils.isEmpty(lvos)) {// �޻������������ֹ����ڵ������ʾ�ֹ�����
				tabComp.setCurrentItem(String.valueOf(item_index_manual));
			} else {
				tabComp.setCurrentItem(String.valueOf(item_index_machine));
			}
		}

	}

	/**
	 * ���û���/�ֹ��������ݵ�ʱ���\��;���\���ڵص��쳣����ʾ����ʾ.
	 * 
	 * @param vos
	 *            ����/�ֹ���������
	 * @param grid
	 *            ����/�ֹ����ڱ��
	 */
	public static void adjustWorkTimeColumn(IWTCount[] vos, GridComp grid) {

		boolean isManual = ShopAttForEmpPageModel.COMP_GRID_MANUAL_DATA.equals(grid.getId());
		if (vos == null || vos.length == 0) {
			return;
		}
		// �������ݵ�ʱ��η���
//		String[] groups = new String[] { COMP_GRID_GROUP_ONE, COMP_GRID_GROUP_TWO};
//				, COMP_GRID_GROUP_THREE,
//				COMP_GRID_GROUP_FOUR 
//		// Ĭ��ʱ��η��鶼����ʾ
		
//		for (int i = 0; i <groups.length ; i++) {
//			((GridColumnGroup) grid.getColumnById(groups[i])).setVisible(true);
//		}
		
		// ������Ҫ��ʾ��ʱ��η���
		
//		boolean ma=((GridColumnGroup) grid.getColumnById(groups[0])).isVisible();
//		boolean ma1=((GridColumnGroup) grid.getColumnById(groups[1])).isVisible();
//		boolean[][] dispSeg = DisplayUtils.getMaxWtDispInfo(vos);
//	
//		for (int i = 0; i < groups.length; i++) {
//			if (i < dispSeg.length){
//				if(!(dispSeg[i][0] || dispSeg[i][1])){
//					((GridColumnGroup) grid.getColumnById(groups[i])).setVisible(false);
//				}
//				else
//					((GridColumnGroup) grid.getColumnById(groups[i])).setVisible(true);
//				
//			}else {
//				((GridColumnGroup) grid.getColumnById(groups[i])).setVisible(false);
//			}
//		}
		

		
		
//		for (int i = 0; null != dispSeg && i < dispSeg.length/2; i++) {
//			if (!(dispSeg[i][0] || dispSeg[i][1]))
//				continue;
//			GridColumnGroup col = (GridColumnGroup) grid.getColumnById(groups[i]);
//			col.setVisible(dispSeg[i][0]||dispSeg[i][1]);
//			
//		}
		if (isManual) { // �ֹ��������ݲ�����������
			return;
		}
		/* ��;��� */
		boolean dispOut = (null != vos && 0 < vos.length && vos[0] instanceof IMidOutData && DisplayUtils
				.isDispMidOut((IMidOutData[]) vos));
		GridColumnGroup colMidWayOut = (GridColumnGroup) grid.getColumnById("out");
		colMidWayOut.setVisible(dispOut);

		/* ˢ�����쳣 */
		String pk_org = SessionUtil.getHROrg();
		// ��ȡ���ڹ���
		TimeRuleVO ruleVO = TBMPsndocUtil.getTimeRuleVOByOrg(pk_org);
		if (null != ruleVO.getWorkplaceflag() && ruleVO.getWorkplaceflag().booleanValue()) {
			GridColumn colPlace = (GridColumn) grid.getColumnById("placeabnormal");
			if (null != colPlace) {
				colPlace.setVisible(true);
			}
		}
	}

	/**
	 * ��ȡ�����������ɫ˵��html
	 * 
	 * @return
	 * @author haoy 2011-8-23
	 */
	public static String getColorBrief(boolean isMachine) {
		String[][] cMap = null;
		String[] borders = null;
		if (isMachine) {// ��������
			String pk_org = SessionUtil.getHROrg();
			/* ˢ�����쳣 */
			TimeRuleVO ruleVO = null;
			if(!StringUtil.isEmptyWithTrim(pk_org)){
				ruleVO = TBMPsndocUtil.getTimeRuleVOByOrg(pk_org);
			}
			if (ruleVO != null && null != ruleVO.getWorkplaceflag() && ruleVO.getWorkplaceflag().booleanValue()) {
				cMap = new String[][] {						
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0122")/*@res "δ����"*/, TAUtil.getHexDesc(IColorConst.COLOR_ABSENT) },
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0121")/*@res "�ٵ�����"*/, TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY) },
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0126")/*@res "���ڵص��쳣"*/, TAUtil.getHexDesc(IColorConst.COLOR_PLACEEXCEPTION) },
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0125")/*@res "��;���"*/, TAUtil.getHexDesc(IColorConst.COLOR_MIDOUT) },
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0124")/*@res "δ���ɿ�������"*/, TAUtil.getHexDesc(IColorConst.COLOR_NOTIMEDATA) },
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0123")/*@res "δ���ù�������"*/, TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR) }};
				borders = new String[] { 
						TAUtil.getHexDesc(IColorConst.COLOR_ABSENT_BORDER)/* δ����*/,
						TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY_BORDER)/* �ٵ�����*/,
						TAUtil.getHexDesc(IColorConst.COLOR_PLACEEXCEPTION_BORDER) /* ���ڵص��쳣*/,
						TAUtil.getHexDesc(IColorConst.COLOR_MIDOUT_BORDER)/* ��;��� */,
						TAUtil.getHexDesc(IColorConst.COLOR_NOTIMEDATA_BORDER)/* δ���ɿ�������*/,
						TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR_BORDER)/* δ���ù������� */};
			} else {
				cMap = new String[][] {						
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0122")/*@res "δ����"*/, TAUtil.getHexDesc(IColorConst.COLOR_ABSENT) },
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0121")/*@res "�ٵ�����"*/, TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY) },
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0125")/*@res "��;���"*/, TAUtil.getHexDesc(IColorConst.COLOR_MIDOUT) },
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0124")/*@res "δ���ɿ�������"*/, TAUtil.getHexDesc(IColorConst.COLOR_NOTIMEDATA) },
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0123")/*@res "δ���ù�������"*/, TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR) }};
				borders = new String[] { 
						TAUtil.getHexDesc(IColorConst.COLOR_ABSENT_BORDER)/* δ����*/,
						TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY_BORDER)/* �ٵ�����*/,
						TAUtil.getHexDesc(IColorConst.COLOR_MIDOUT_BORDER)/* ��;��� */,
						TAUtil.getHexDesc(IColorConst.COLOR_NOTIMEDATA_BORDER)/* δ���ɿ�������*/,
						TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR_BORDER)/* δ���ù������� */};
			}
		} else {// �ֹ�����
			cMap = new String[][] {
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0122")/*@res "δ����"*/, TAUtil.getHexDesc(IColorConst.COLOR_ABSENT) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0121")/*@res "�ٵ�����"*/, TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0123")/*@res "δ���ù�������"*/, TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR) }};
			borders = new String[] { 
					TAUtil.getHexDesc(IColorConst.COLOR_ABSENT_BORDER)/* δ����*/,
					TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY_BORDER)/* �ٵ�����*/,
					TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR_BORDER)/* δ���ù������� */};
		}
		StringBuffer buf = new StringBuffer();
		buf.append("<table height=\"40px\" cellpadding =\"0\" cellspacing=\"0\" align=\"center\">");
		buf.append("<tr>");
		for (int i = 0; i < cMap.length; i++) {
			buf.append("<td><span style=\"width:20px;height:20px;margin-right:5px;border:1px solid ").append(borders[i]).append(";background-color:");
			buf.append(cMap[i][1]);
			buf.append("\">&nbsp;&nbsp;</span>");
			buf.append("<span style=\"margin-right:20px;\">");
			buf.append(cMap[i][0]);
			buf.append("</span></td>");
		}
		buf.append("</tr>");
		buf.append("</table>");
		return buf.toString();
	}
	
	/**
	 * ��ȡ�����������ɫ˵��html
	 * 
	 * @return
	 * @author haoy 2011-8-23
	 */
	public static String getMngColorBrief() {
		String[][] cMap = null;
		String[] borders = null;
		String pk_org = SessionUtil.getHROrg();
		/* ˢ�����쳣 */
		TimeRuleVO ruleVO = null;
		if(!StringUtil.isEmptyWithTrim(pk_org)){
			ruleVO = TBMPsndocUtil.getTimeRuleVOByOrg(pk_org);
		}
		if (ruleVO != null && null != ruleVO.getWorkplaceflag() && ruleVO.getWorkplaceflag().booleanValue()) {
			cMap = new String[][] {
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0122")/*@res "δ����"*/, TAUtil.getHexDesc(IColorConst.COLOR_ABSENT) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0121")/*@res "�ٵ�����"*/, TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0126")/*@res "���ڵص��쳣"*/, TAUtil.getHexDesc(IColorConst.COLOR_PLACEEXCEPTION) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0125")/*@res "��;���"*/, TAUtil.getHexDesc(IColorConst.COLOR_MIDOUT) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0037")/*@res "�޿��ڵ���"*/, TAUtil.getHexDesc(IColorConst.COLOR_NONTBMPSNDOC) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0124")/*@res "δ���ɿ�������"*/, TAUtil.getHexDesc(IColorConst.COLOR_NOTIMEDATA) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0123")/*@res "δ���ù�������"*/, TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR) }};
			borders = new String[] { 
					TAUtil.getHexDesc(IColorConst.COLOR_ABSENT_BORDER)/* δ����*/,
					TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY_BORDER)/* �ٵ�����*/,
					TAUtil.getHexDesc(IColorConst.COLOR_PLACEEXCEPTION_BORDER) /* ���ڵص��쳣*/,
					TAUtil.getHexDesc(IColorConst.COLOR_MIDOUT_BORDER)/* ��;��� */,
					TAUtil.getHexDesc(IColorConst.COLOR_NONTBMPSNDOC_BORDER)/* �޿��ڵ��� */,
					TAUtil.getHexDesc(IColorConst.COLOR_NOTIMEDATA_BORDER)/* δ���ɿ�������*/,
					TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR_BORDER)/* δ���ù������� */};

		} else {
			cMap = new String[][] {
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0122")/*@res "δ����"*/, TAUtil.getHexDesc(IColorConst.COLOR_ABSENT) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0121")/*@res "�ٵ�����"*/, TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0125")/*@res "��;���"*/, TAUtil.getHexDesc(IColorConst.COLOR_MIDOUT) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0037")/*@res "�޿��ڵ���"*/, TAUtil.getHexDesc(IColorConst.COLOR_NONTBMPSNDOC) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0124")/*@res "δ���ɿ�������"*/, TAUtil.getHexDesc(IColorConst.COLOR_NOTIMEDATA) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0123")/*@res "δ���ù�������"*/, TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR) }};
			borders = new String[] { 
					TAUtil.getHexDesc(IColorConst.COLOR_ABSENT_BORDER)/* δ����*/,
					TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY_BORDER)/* �ٵ�����*/,
					TAUtil.getHexDesc(IColorConst.COLOR_MIDOUT_BORDER)/* ��;��� */,
					TAUtil.getHexDesc(IColorConst.COLOR_NONTBMPSNDOC_BORDER)/* �޿��ڵ��� */,
					TAUtil.getHexDesc(IColorConst.COLOR_NOTIMEDATA_BORDER)/* δ���ɿ�������*/,
					TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR_BORDER)/* δ���ù������� */};
		}
		StringBuffer buf = new StringBuffer();
		buf.append("<table height=\"40px\" cellpadding =\"0\" cellspacing=\"0\" align=\"center\">");
		buf.append("<tr>");
		for (int i = 0; i < cMap.length; i++) {
			buf.append("<td><span style=\"width:20px;height:20px;margin-right:5px;border:1px solid ").append(borders[i]).append(";background-color:");
			buf.append(cMap[i][1]);
			buf.append("\">&nbsp;&nbsp;</span>");
			buf.append("<span style=\"margin-right:20px;\">");
			buf.append(cMap[i][0]);
			buf.append("</span></td>");
		}
		buf.append("</tr>");
		buf.append("</table>");
		return buf.toString();
	}

	/**
	 * ��ѯ���ŵ���Ա
	 * 
	 * @param fws
	 * @param begin
	 * @param end
	 * @param pk_dept
	 * @param includeSubDept
	 * @return
	 */
	public static TBMPsndocVO[] getTbmPsndoc(FromWhereSQL fws, UFLiteralDate begin, UFLiteralDate end, String pk_dept,
			boolean includeSubDept) {
		TBMPsndocVO[] psn = null;
		ITBMPsndocQueryService pqs;
		try {
			pqs = ServiceLocator.lookup(ITBMPsndocQueryService.class);
			psn = pqs.queryLatestByConditionAndDept(fws, begin, end, pk_dept, includeSubDept);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return (null == psn) ? new TBMPsndocVO[0] : psn;
	}

	/**
	 * �������²�ѯ�ڼ�
	 * 
	 * @param pk_org
	 * @param year
	 * @param month
	 */
	public static PeriodVO queryPeriodVOByYearMonth(String pk_org, String year, String month) {
		PeriodVO periodVO = null;
		try {
			IPeriodQueryService service = ServiceLocator.lookup(IPeriodQueryService.class);
			periodVO = service.queryByYearMonth(pk_org, year, month);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			e.alert();
		}

		return periodVO;
	}
}