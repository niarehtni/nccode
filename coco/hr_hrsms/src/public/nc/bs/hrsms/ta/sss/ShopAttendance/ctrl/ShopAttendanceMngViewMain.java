package nc.bs.hrsms.ta.sss.ShopAttendance.ctrl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.hrsms.ta.sss.ShopAttendance.lsnr.ShopAttendanceUtil;
import nc.bs.hrsms.ta.sss.ShopAttendance.pagemodel.ShopAttForEmpPageModel;
import nc.bs.hrsms.ta.sss.ShopAttendance.pagemodel.ShopAttForMngPageModel;
import nc.bs.hrsms.ta.sss.leaveinfo.ctrl.LeaveInfoQryQueryCtrl;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.qry.QueryUtil;
import nc.bs.hrss.ta.utils.ComboDataUtil;
import nc.bs.hrss.ta.utils.TAUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.bs.hrss.ta.utils.TBMPsndocUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.itf.ta.ILateEarlyQueryMaintain;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeDataQueryMaintain;
import nc.md.model.type.IType;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.bm.ButtonStateManager;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.combodata.ComboData;
import nc.uap.lfw.core.common.DataTypeTranslator;
import nc.uap.lfw.core.common.StringDataTypeConst;
import nc.uap.lfw.core.comp.GridColumn;
import nc.uap.lfw.core.comp.GridColumnGroup;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.IGridColumn;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.PaginationInfo;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.ui.ta.dataprocess.view.TimeDataColorUtils;
import nc.ui.ta.lateearly.view.LateEarlyColorUtils;
import nc.ui.ta.pub.IColorConst;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.dataprocess.TimeDataVO;
import nc.vo.ta.lateearly.LateEarlyVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psndoc.TBMPsndocVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.informix.util.dateUtil;

public class ShopAttendanceMngViewMain implements IController{
	
	// ���ڵ������ݼ�
		public static final String DS_TBMPSNDOC = "dsTBMPsndoc";
		// ���ڵ������
		public static final String TBL_TBMPSNDOC = "tblTBMPsndoc";
		// ����ڼ��Ƿ�仯�Ĳ���
		public static final String SESSION_DATE_CHANGE = "isDateChange";
		// ��Ⱦ����
		private static final String RENDER_TYEP = "TimeDataMngRender";
		// ��Ⱦ����
		
		// ���ÿ����Ҿ���
		private static final String TEXT_ALIGN = "center";
		// �ֶ�ǰ׺
		private static final String COLUMN_FIELD_HEAD = "col_";
		// ���ͷǰ׺
		private static final String GROUP_FIELD_HEAD = "group_";
		// ����Ա�����ڲ鿴ҳ��
//		public static final String PAGE_TIMEDATAFORMNGAPP = "/app/TimeDataMngApp";
		// ����Ա�����ڲ鿴ҳ��
		public static final String PAGE_SHOPATTFORMNGAPP ="/app/ShopAttendanceMngApp?nodecode=E20600977";
		
		
		// ��Ա����
		public static final String TIMEDATE_PSNCODE = "pk_psnjob_pk_psndoc_code";
		// ǰ׺-���ݼ��ֶ�
		private static final String COLOR_PREFIX = "color_";
		
		
		/**
		 * ���ݼ������¼�
		 * 
		 * @param dataLoadEvent
		 * @throws BusinessException 
		 */
		public void onDataLoad_dsTBMPsndoc(DataLoadEvent dataLoadEvent) throws BusinessException {
			Dataset ds = dataLoadEvent.getSource();
			if (isPagination(ds)) { // ��ҳ����
				// ��ѯ����-����
				String pk_dept = (String) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_PK_DEPT);
				// ��֤����-��ʼ����
				UFLiteralDate beginDate = (UFLiteralDate) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_BEGINDATE);
				// ��ѯ����-��������
				UFLiteralDate endDate = (UFLiteralDate) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_ENDDATE);
				// ��ѯ����-onlyShowException
				UFBoolean onlyShowException = (UFBoolean) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_ONLY_SHOW_EXCEPTION);
				boolean ose = onlyShowException.booleanValue();
				// ��ѯ����-FromWhereSQL
				FromWhereSQL fromWhereSQL = (FromWhereSQL) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_FROMWHERESQL);
				boolean includeSubDept = SessionUtil.isIncludeSubDept();
				initData(ds, fromWhereSQL, beginDate, endDate, pk_dept, includeSubDept, ose);

			} else {// ��ʼ������
				CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
			}

		}

		/**
		 * ��ҳ������־
		 * 
		 * @param ds
		 * @return
		 */
		private boolean isPagination(Dataset ds) {
			PaginationInfo pg = ds.getCurrentRowSet().getPaginationInfo();
			return pg.getRecordsCount() > 0;
		}

		/**
		 * �����ű��
		 * 
		 * @param keys
		 * @throws BusinessException 
		 */
		public void pluginDeptChange(Map<String, Object> keys) throws BusinessException {
			TaAppContextUtil.addTaAppContext();
			ApplicationContext appCxt = AppLifeCycleContext.current().getApplicationContext();
			appCxt.addAppAttribute(ShopAttendanceMngViewMain.SESSION_DATE_CHANGE, UFBoolean.TRUE);
			// ��ǰ�����ڼ�
			PeriodVO latestPeriodVO = TaAppContextUtil.getLatestPeriodVO();

			Map<String, String[]> periodMap = TBMPeriodUtil.getPeriodMap(TaAppContextUtil.getHROrg());
			TaAppContextUtil.setTBMPeriodVOMap(periodMap);
			LfwView simpQryView = getLifeCycleContext().getWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
			Dataset dsSearch = simpQryView.getViewModels().getDataset(HrssConsts.DS_SIMPLE_QUERY);

			if (dsSearch != null) {
				Row selRow = dsSearch.getSelectedRow();
				String old_year = null;
				String accyear = null;
				if (dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMYEAR) > -1) {
					old_year = selRow.getString(dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMYEAR));
					if (latestPeriodVO != null) {
						selRow.setValue(dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMYEAR), latestPeriodVO.getTimeyear());
					} else {
						if (periodMap != null && periodMap.size() > 0) {
							String[] years = periodMap.keySet().toArray(new String[0]);
							if (!ArrayUtils.isEmpty(years)) {
								accyear = years[0];
								selRow.setValue(dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMYEAR), accyear);
							}
						} else {
							selRow.setValue(dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMYEAR), null);
						}
						// selRow.setValue(dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMYEAR),
						// null);
					}
					accyear = selRow.getString(dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMYEAR));
				}
				// ���ŷ�����������δ�������
				if (accyear != null && accyear.equals(old_year)) {
					ComboData monthData = simpQryView.getViewModels().getComboData("comb_tbmmonth_value");
					String[] months = null;
					if (periodMap != null && periodMap.size() > 0) {
						months = periodMap.get(accyear);
					}
					ComboDataUtil.addCombItemsAfterClean(monthData, months);
					if (ArrayUtils.isEmpty(months)) {
						selRow.setValue(dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMMONTH), null);
						return;
					}
					if (latestPeriodVO != null && !StringUtils.isEmpty(latestPeriodVO.getTimeyear()) && latestPeriodVO.getAccyear().equals(accyear)) {
						String accmonth = latestPeriodVO.getTimemonth();
						for (String month : months) {
							if (month.equals(accmonth)) {
								selRow.setValue(dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMMONTH), accmonth);
								break;
							}
						}
					} else {
						selRow.setValue(dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMMONTH), months[0]);
					}
				} else if (accyear == null && old_year == null) {
					String[] years = null;
					ComboData yearData = simpQryView.getViewModels().getComboData("comb_tbmyear_value");
					if (periodMap != null && periodMap.size() > 0) {
						years = periodMap.keySet().toArray(new String[0]);
						if (years != null && years.length > 1) {
							Arrays.sort(years);
							Collections.reverse(Arrays.asList(years));
						}
					}
					ComboDataUtil.addCombItemsAfterClean(yearData, years);
				}
			}

			// ��ʼ������
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());

		}

		/**
		 * ����
		 * 
		 * @param keys
		 * @throws BusinessException 
		 */
		public void pluginSearch(Map<String, Object> keys) throws BusinessException {
			TBMPsndocUtil.checkTimeRuleVO();
			
			LfwView viewMain = getLifeCycleContext().getViewContext().getView();
			Dataset ds = viewMain.getViewModels().getDataset(DS_TBMPSNDOC);
			// ������ݼ�
			DatasetUtil.clearData(ds);
			// ������
			String pk_dept = SessionUtil.getPk_mng_dept();
			if (StringUtils.isEmpty(pk_dept)) {
				return;
			}
			// �Ƿ�����¼�����
			boolean includeSubDept = SessionUtil.isIncludeSubDept();
			// ��֯
			String pk_org = SessionUtil.getHROrg();
			nc.uap.ctrl.tpl.qry.FromWhereSQLImpl whereSql = (nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys.get(HrssConsts.PO_SEARCH_WHERESQL);
			// ��ѯ����-FromWhereSQL
			FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) CommonUtil.getUAPFromWhereSQL(whereSql);
			// ����Զ�������
			Map<String, String> selfDefMap = whereSql.getFieldAndSqlMap();

			String psnScopeSqlPart;
			try {
				psnScopeSqlPart = QueryUtil.getDeptPsnCondition();
				if (fromWhereSQL != null && !StringUtils.isEmpty(psnScopeSqlPart)) {
					(fromWhereSQL).setWhere(fromWhereSQL.getWhere() + " and tbm_psndoc.pk_psndoc in (" + psnScopeSqlPart + ") ");
				}
			} catch (BusinessException e) {
//				new HrssException(e).deal();
			}

			String year = null;
			String month = null;
			LfwView simpQryView = getLifeCycleContext().getWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
			Dataset dsSearch = simpQryView.getViewModels().getDataset(HrssConsts.DS_SIMPLE_QUERY);
			if (dsSearch != null) {
				Row row = dsSearch.getSelectedRow();
				year = row.getString(dsSearch.nameToIndex(ShopAttendanceMngQueryCtrl.FS_TBMYEAR));
				month = row.getString(dsSearch.nameToIndex(ShopAttendanceMngQueryCtrl.FS_TBMMONTH));
			}
			if (StringUtil.isEmptyWithTrim(year)) {
				removeCol(viewMain, ds);
				CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pub-res", "0c_pub-res0168")/*
																																 * @
																																 * res
																																 * "��ѯʧ��"
																																 */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0143")/*
																																																						 * @
																																																						 * res
																																																						 * "�꿼���ڼ�δ���壡"
																																																						 */);
				return;
			}
			if (StringUtil.isEmptyWithTrim(month)) {
				removeCol(viewMain, ds);
				CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pub-res", "0c_pub-res0168")/*
																																 * @
																																 * res
																																 * "��ѯʧ��"
																																 */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0144")/*
																																																						 * @
																																																						 * res
																																																						 * "�¿����ڼ�δ���壡"
																																																						 */);
				return;
			}
			// ��ʾ�쳣
			String onlyShowException = selfDefMap.get(ShopAttForMngPageModel.FIELD_ONLY_SHOW_EXCEPTION);
			UFBoolean ose = UFBoolean.valueOf(onlyShowException);
			// ��������ڼ��ѯ�ڼ�VO
			PeriodVO periodVO = ShopAttendanceUtil.queryPeriodVOByYearMonth(pk_org, year, month);
			if (periodVO == null) {
				return;
			}
			// ��ʼ����
			UFLiteralDate beginDate = periodVO.getBegindate();
			// ��������
			UFLiteralDate endDate = periodVO.getEnddate();

			getLifeCycleContext().getApplicationContext().addAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_BEGINDATE, beginDate);
			getLifeCycleContext().getApplicationContext().addAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_ENDDATE, endDate);
			getLifeCycleContext().getApplicationContext().addAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_FROMWHERESQL, fromWhereSQL);
			getLifeCycleContext().getApplicationContext().addAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_ONLY_SHOW_EXCEPTION, ose);
			getLifeCycleContext().getApplicationContext().addAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_PK_DEPT, pk_dept);

			// ������
			initDsAndGrid(viewMain, ds, beginDate, endDate);
			getApplicationContext().addBeforeExecScript("window.colorMap = null");
			getApplicationContext().addBeforeExecScript("window.editList = null");
			// ��������
			initData(ds, fromWhereSQL, beginDate, endDate, pk_dept, includeSubDept, ose.booleanValue());

		}

		public void initData(Dataset ds, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, String pk_dept, boolean includeSubDept, boolean onlyShowException) {
			// ��ѯ��������
			List<String> psnList = loadTimeDataByDate(fromWhereSQL, beginDate, endDate, pk_dept, includeSubDept, onlyShowException);
			String[] psndocs = psnList.toArray(new String[0]);
			if (StringUtils.isEmpty(ds.getCurrentKey())) {
				ds.setCurrentKey(Dataset.MASTER_KEY);
			}
			PaginationInfo pinfo = ds.getCurrentRowSet().getPaginationInfo();
			if (ArrayUtils.isEmpty(psndocs)) {
				DatasetUtil.clearData(ds);
				pinfo.setRecordsCount(0);
				// ���²˵�״̬
				ds.setEnabled(false);
				ButtonStateManager.updateButtons();
				return;
			}
			// ÿҳ��¼����
			int pageSize = pinfo.getPageSize();
			if (pageSize == -1) {
				return;
			}
			// �ܼ�¼��
			int recordsCount = psndocs.length;
			// ҳ��
			int pageIndex = pinfo.getPageIndex();
			pinfo.setRecordsCount(recordsCount);
			int PagebeignSize = pageIndex * pageSize;

			int length = (recordsCount - PagebeignSize) < pageSize ? (recordsCount - PagebeignSize) : pageSize;
			int pageMaxSize = PagebeignSize + length;
			String[] result = new String[length];
			for (int j = 0, i = PagebeignSize; i < pageMaxSize; i++, j++) {
				result[j] = psndocs[i];
			}

			for (int i = 0; i < result.length; i++) { // ����Աѭ��
				Row row = ds.getEmptyRow();
				row.setValue(ds.nameToIndex(TBMPsndocVO.PK_PSNDOC), result[i]);
				ds.addRow(row);
			}
		}

		/**
		 * �ع����ʱȥ����Ҫ����
		 * 
		 * @param viewMain
		 * @param dsTbmPsndoc
		 */
		private void removeCol(LfwView viewMain, Dataset dsTbmPsndoc) {
			Field[] fields = dsTbmPsndoc.getFieldSet().getFields();
			for (Field field : fields) {
				String[] ids = field.getId().split("_");
				if (ids == null || ids.length == 0 || StringUtils.isEmpty(ids[0])) {
					continue;
				}
				if (dateUtil.isValidDate(ids[0])) {
					dsTbmPsndoc.getFieldSet().removeField(field);
				}
			}
			GridComp grid = (GridComp) viewMain.getViewComponents().getComponent(TBL_TBMPSNDOC);

			List<IGridColumn> groupList = new ArrayList<IGridColumn>();
			IGridColumn col = null;
			// ɾ��ԭ��COLUMNS
			for (Iterator<IGridColumn> it = grid.getColumnList().iterator(); it.hasNext();) {
				col = it.next();
				if (col instanceof GridColumnGroup) {
					groupList.add(col);
				}
			}
			grid.removeColumns(groupList, true);
		}

		/**
		 * ���ݿ�ʼ���ںͽ�������,�������ݼ������ڱ��
		 * 
		 * @param beginDate
		 * @param endDate
		 */
		private void initDsAndGrid(LfwView viewMain, Dataset dsTbmPsndoc, UFLiteralDate beginDate, UFLiteralDate endDate) {

			// ��ѯ������ʼ���ںͽ�������û�з����仯,�����ع���DS/Grid��
			UFBoolean dateChangeFlag = (UFBoolean) getApplicationContext().getAppAttribute(SESSION_DATE_CHANGE);
			// ����״̬
			getApplicationContext().addAppAttribute(SESSION_DATE_CHANGE, UFBoolean.FALSE);
			if (dateChangeFlag != null && !dateChangeFlag.booleanValue()) {
				return;
			}

			removeCol(viewMain, dsTbmPsndoc);
			GridComp grid = (GridComp) viewMain.getViewComponents().getComponent(TBL_TBMPSNDOC);

			// �����µ�COLUMNS
			List<IGridColumn> groupList = new ArrayList<IGridColumn>();

			Map<String, GridColumnGroup> columnGroupMap = new HashMap<String, GridColumnGroup>();

			// ��ü������
			int days = UFLiteralDate.getDaysBetween(beginDate, endDate) + 1;
			for (int i = 0; i < days; i++) {
				// ����
				UFLiteralDate curDate = beginDate.getDateAfter(i);
				// �����б����ݼ��ֶ�
				buildDatasetField(viewMain, dsTbmPsndoc, curDate, columnGroupMap);
			}
			for (Iterator<GridColumnGroup> it = columnGroupMap.values().iterator(); it.hasNext();) {
				groupList.add(it.next());
			}
			IGridColumn[] groups = groupList.toArray(new IGridColumn[0]);
			IGridColumn gridColumn = null;
			int firstYear;
			int firstMonth;
			int secondYear;
			int secondMonth;
			// ��ʱֻ֧�ֿ�������
			for (int i = 0; i < groups.length - 1; i++) {
				firstYear = Integer.parseInt(groups[i].getId().substring(6, 10));
				firstMonth = Integer.parseInt(groups[i].getId().substring(10, 12));
				secondYear = Integer.parseInt(groups[i + 1].getId().substring(6, 10));
				secondMonth = Integer.parseInt(groups[i + 1].getId().substring(10, 12));
				if (firstYear == secondYear) {
					if (firstMonth > secondMonth) {
						gridColumn = groups[i];
						groups[i] = groups[i + 1];
						groups[i + 1] = gridColumn;
					}
				} else if (firstYear > secondYear) {
					gridColumn = groups[i];
					groups[i] = groups[i + 1];
					groups[i + 1] = gridColumn;
				}
			}
			grid.addColumns(Arrays.asList(groups), true);
		}

		/**
		 * ����Field�ֶ�
		 * 
		 * @param viewMain
		 * @param dsCalendar
		 * @param dateAfter
		 */
		private void buildDatasetField(LfwView widget, Dataset ds, UFLiteralDate curDate, Map<String, GridColumnGroup> columnGroupMap) {
			/** �����ֶ� */
			Field writeField = buildField(curDate);
			/** ���ݼ������ֶ� */
			ds.getFieldSet().addField(writeField);
			/** ���ɵ�Ԫ����ɫ�ֶ� */
			Field colorField = buildColorField(writeField.getId());
			ds.getFieldSet().addField(colorField);
			/** GridColumn */
			buildGridGroup(ds, writeField, curDate, columnGroupMap);
		}

		/**
		 * �����ֶ�
		 * 
		 * @param curDate
		 * @return
		 */
		private Field buildField(UFLiteralDate curDate) {
			// �༶�ֶ�
			Field readField = new Field();
			// �����ֶ�ID
			readField.setId(curDate.toPersisted().replace("-", ""));
			// ������������
			readField.setDataType(DataTypeTranslator.translateInt2String(IType.REF));
			// ��VO��Ӧ�ֶ�(����)
			readField.setField(null);
			readField.setText(String.valueOf(curDate.toPersisted().replace("-", "")));
			return readField;
		}

		/**
		 * ��¼��Ԫ����ʾ��ɫ���ֶ�
		 * 
		 * @param nameFieldId
		 * @return
		 */
		private Field buildColorField(String nameFieldId) {
			Field colorField = new Field();
			// �����ֶ�ID
			colorField.setId(COLOR_PREFIX + nameFieldId);// ��ʽ��color_20120701_name
			// ������������
			colorField.setDataType(StringDataTypeConst.STRING);
			// ��VO��Ӧ�ֶ�(����)
			colorField.setField(null);
			return colorField;
		}

		/**
		 * ���������
		 * 
		 * @param ds
		 * @param readFieldId
		 * @param writeFieldnnn
		 * @param curDate
		 * @param refnodeId
		 * @return
		 */
		public static void buildGridGroup(Dataset ds, Field writeField, UFLiteralDate curDate, Map<String, GridColumnGroup> columnGroupMap) {

			GridColumn column = new GridColumn();
			// ����id
			column.setId(COLUMN_FIELD_HEAD + writeField.getId());
			// ���ù���Dataset��Field
			column.setField(writeField.getId());
			// �����п��,��ʵ�ʿ��
			column.setWidth(50);
			// ������Ⱦ��
			column.setRenderType(RENDER_TYEP);
			// ������ʾ����
			column.setText(writeField.getText());
			column.setTextAlign(TEXT_ALIGN);

			String yearAndMonth = writeField.getId().substring(0, 6);
			if (columnGroupMap.get(yearAndMonth) == null) {
				// ����Grid���ͷ������
				GridColumnGroup group = new GridColumnGroup();
				group.setId(GROUP_FIELD_HEAD + yearAndMonth);
				group.setText(String.valueOf(curDate.getYear()) + "-" + String.valueOf(curDate.getMonth()));
				group.addColumn(column);
				columnGroupMap.put(yearAndMonth, group);
			} else {
				GridColumnGroup columnGroup = columnGroupMap.get(yearAndMonth);
				columnGroup.addColumn(column);
			}
		}

		/**
		 * ��ת������Ա�鿴ҳ��
		 * 
		 * @param scriptEvent
		 */
		public void showDetail(ScriptEvent scriptEvent) {

			String rowId = getLifeCycleContext().getParameter("dsMain_rowId");
			String dsId = getLifeCycleContext().getParameter("dsMain_id");
			Dataset ds = getLifeCycleContext().getViewContext().getView().getViewModels().getDataset(dsId);
			Row selRow = ds.getRowById(rowId);
			if (selRow == null) {
				return;
			}
			// ��Ա����
			String pk_psndoc = (String) selRow.getValue(ds.nameToIndex(PsndocVO.PK_PSNDOC));
			// ��Ա����
			String pk_psndoc_name = (String) selRow.getValue(ds.nameToIndex(ShopAttForMngPageModel.FIELD_TIME_DATA_PSNNAME));
			SessionBean bean = SessionUtil.getSessionBean();
			// ����Ա��������Session��
			bean.setExtendAttribute(PsndocVO.PK_PSNDOC, pk_psndoc);
			bean.setExtendAttribute(ShopAttForMngPageModel.FIELD_TIME_DATA_PSNNAME, pk_psndoc_name);
			bean.setExtendAttribute(ShopAttForMngPageModel.APP_STATUS, ShopAttForMngPageModel.STATUS_BYNAME_BROWSE);
			// ��ת������Ա�鿴ҳ��
			sendRedirect(PAGE_SHOPATTFORMNGAPP);
		}

		/**
		 * �����ڲ鿴
		 * 
		 * @param scriptEvent
		 */
		public void showDateDetail(ScriptEvent scriptEvent) {

			String headerDivTextValue = getLifeCycleContext().getParameter("headerDivTextValue");
			if (StringUtil.isEmptyWithTrim(headerDivTextValue)) {
				return;
			}
			UFLiteralDate beginDate = (UFLiteralDate) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_BEGINDATE);
			UFLiteralDate endDate = (UFLiteralDate) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_ENDDATE);
			if (beginDate == null || endDate == null) {
				return;
			}
			UFLiteralDate curDate = (UFLiteralDate) beginDate.clone();
			do {
				if ((curDate.toPersisted().replace("-", "")).equals(headerDivTextValue)) {
					headerDivTextValue = curDate.toString();
					break;
				}
				curDate = curDate.getDateAfter(1);
			} while (curDate.before(endDate) || curDate.equals(endDate));

			SessionBean bean = SessionUtil.getSessionBean();
			// ����Ա��������Session��
			bean.setExtendAttribute("headerDivTextValue", headerDivTextValue);
			bean.setExtendAttribute(ShopAttForMngPageModel.APP_STATUS, ShopAttForMngPageModel.STATUS_BYDATE_BROWSE);
			// ��ת�������ڲ鿴ҳ��
			sendRedirect(PAGE_SHOPATTFORMNGAPP);
		}

		/**
		 * ��תҳ��
		 */
		public static void sendRedirect(String app) {
			ApplicationContext appCtx = AppLifeCycleContext.current().getApplicationContext();
			String url = LfwRuntimeEnvironment.getRootPath() + app;
			appCtx.sendRedirect(url);
		}

		/**
		 * ��ѯԱ����������
		 * 
		 * @param fromWhereSQL
		 * @param beginDate
		 * @param endDate
		 * @param pk_dept
		 * @param containsSubDepts
		 * @return
		 */
		public List<String> loadTimeDataByDate(FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, String pk_dept, boolean containsSubDepts, boolean onlyShowException) {
			// ������������
			TimeDataVO[] machineDataVOs = null;
			// �ֹ���������
			LateEarlyVO[] manualVOs = null;
			try {
				ITimeDataQueryMaintain tqs = ServiceLocator.lookup(ITimeDataQueryMaintain.class);
				ILateEarlyQueryMaintain lqs = ServiceLocator.lookup(ILateEarlyQueryMaintain.class);
				if(onlyShowException){
					machineDataVOs = tqs.queryByCondDateAndDeptShowExceptionAll(fromWhereSQL, beginDate, endDate, pk_dept, containsSubDepts);
					manualVOs = lqs.queryByCondDateAndDeptShowExceptionAll(fromWhereSQL, beginDate, endDate, pk_dept, containsSubDepts);
				}else{
					machineDataVOs = tqs.queryByCondDateAndDept(fromWhereSQL, beginDate, endDate, false, pk_dept, containsSubDepts);
					manualVOs = lqs.queryByCondDateAndDept(fromWhereSQL, beginDate, endDate, false, pk_dept, containsSubDepts);
				}
			} catch (HrssException e) {
				e.alert();
			} catch (BusinessException e) {
				new HrssException(e).deal();
			}
			// Ϊ���������ɫ
			getApplicationContext().getClientSession().setAttribute(ShopAttForEmpPageModel.CSES_COLOR_MAP, getTimeDataColorInJSON(machineDataVOs, manualVOs));
			getApplicationContext().addBeforeExecScript("setColorMap();");

			List<String> psnList = new ArrayList<String>();
			String pk_psndoc = null;
			if (!ArrayUtils.isEmpty(machineDataVOs)) {
				for (TimeDataVO timeDataVO : machineDataVOs) {
					pk_psndoc = timeDataVO.getPk_psndoc();
					if (!psnList.contains(pk_psndoc)) {
						psnList.add(pk_psndoc);
					}

				}
			}
			if (!ArrayUtils.isEmpty(manualVOs)) {
				for (LateEarlyVO lateEarlyVO : manualVOs) {
					pk_psndoc = lateEarlyVO.getPk_psndoc();
					if (!psnList.contains(pk_psndoc)) {
						psnList.add(pk_psndoc);
					}
				}
			}
			return psnList;

		}

		/**
		 * ��ȡ��Ԫ����ɫ�������ַ���
		 * 
		 * @param tiData
		 * @param laData
		 * @return
		 */
		private static String getTimeDataColorInJSON(TimeDataVO[] tiData, LateEarlyVO[] laData) {
			StringBuilder sb = new StringBuilder("");
			List<Color> colorList = null;

			// �ֹ��������ݴ���
			if (null != laData && laData.length > 0) {
				String[] attr = laData[0].getAttributeNames();
				UFLiteralDate today = null;
				for (LateEarlyVO vo : laData) {
					colorList = new ArrayList<Color>();
					for (String s : attr) {
						Color c = LateEarlyColorUtils.getColor(vo, s);
						if (null != c) {
							if (!colorList.contains(c)) {
								colorList.add(c);
							}
						}
					}

					// ����δ���ڣ�����ʾΪδ����
					if (colorList.contains(IColorConst.COLOR_ABSENT)) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_ABSENT)).append("\"");
					}
					// �ٵ�����
					else if (colorList.contains(IColorConst.COLOR_LATEEARLY)) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY)).append("\"");
					}
					// �޹�������
					else if (colorList.contains(IColorConst.COLOR_NONPSNCALENDAR)) {
						if (sb.length() > 0)
							sb.append(",");
						// if (checkTBMPsndocTime(vo.getPk_org(), vo.getPk_psndoc(),
						// vo.getCalendar(), vo.getCalendar())) {
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR)).append("\"");
						// } else {
						// // δ���ÿ��ڵ���
						// sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-",
						// "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_NONTBMPSNDOC)).append("\"");
						// }

					}
					// ����
					else if (!ShiftVO.PK_GX.equals(vo.getPk_shift())) {
						if((vo.getOnebeginstatus()==null||vo.getOnebeginstatus()==-1) 
								&& (vo.getTwoendstatus()==null||vo.getTwoendstatus()==-1)){
							if (sb.length() > 0)
								sb.append(",");
							sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_ABSENT)).append("\"");
						}else{
							if (sb.length() > 0)
								sb.append(",");
							sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append("holiday").append("\"");
						}
//						today = new UFLiteralDate();
//						if ((today).after(vo.getCalendar())) {
//							if (sb.length() > 0)
//								sb.append(",");
//							sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append("right").append("\"");
//						}else{
//							if (sb.length() > 0)
//								sb.append(",");
//							// δ�������ֹ����ڣ�����͹���һ����ʲô����ɫ������
//							sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append("holiday").append("\"");
//						}
					}
					// ����
					else if (ShiftVO.PK_GX.equals(vo.getPk_shift())) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append("holiday").append("\"");
					}

				}
			}

			// �����������ݴ���
			if (null != tiData && tiData.length > 0) {
				String[] attr = tiData[0].getAttributeNames();
				for (TimeDataVO vo : tiData) {
					if ((vo.getIsmidoutabnormal()!=null&&vo.getIsmidoutabnormal().booleanValue()
							||vo.getMidwayoutcount()!=null&&vo.getMidwayoutcount()<0)//�ӻ�����Ϊ������ʱ��ʾ���õ�
							&&vo.getIsmidwayout()==1) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append(TimeDataVO.ISMIDOUTABNORMAL).append("\":\"").append("Y").append("\"");
					}
					colorList = new ArrayList<Color>();
					for (String s : attr) {
						Color c = TimeDataColorUtils.getColor(vo, s);
						if (null != c) {
							if (!colorList.contains(c)) {
								colorList.add(c);
							}
						}
					}

					// ����δ���ڣ�����ʾΪδ����
					if (colorList.contains(IColorConst.COLOR_ABSENT)) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_ABSENT)).append("\"");
					}
					// �ٵ�����
					else if (colorList.contains(IColorConst.COLOR_LATEEARLY)) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY)).append("\"");
					}
					// ��;����
					else if (colorList.contains(IColorConst.COLOR_MIDOUT)) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_MIDOUT)).append("\"");
					}
					// �޹�������
					else if (colorList.contains(IColorConst.COLOR_NONPSNCALENDAR)) {
						if (sb.length() > 0)
							sb.append(",");
						// if (checkTBMPsndocTime(vo.getPk_org(), vo.getPk_psndoc(),
						// vo.getCalendar(), vo.getCalendar())) {
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR)).append("\"");
						// } else {
						// // δ���ÿ��ڵ���
						// sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-",
						// "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_NONTBMPSNDOC)).append("\"");
						// }

					}
					// δ���ɿ�������
					else if (colorList.contains(IColorConst.COLOR_NOTIMEDATA)) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_NOTIMEDATA)).append("\"");
					}
					// ˢ���ص��쳣
					else if (colorList.contains(IColorConst.COLOR_PLACEEXCEPTION)) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_PLACEEXCEPTION)).append("\"");
					}
					// ����
					else if (!ShiftVO.PK_GX.equals(vo.getPk_shift())) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append("right").append("\"");
					}
					// ����
					else if (ShiftVO.PK_GX.equals(vo.getPk_shift())) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append("holiday").append("\"");
					}
				}
			}

			return "{" + sb.toString() + "}";
		}

		/**
		 * У�����ڵĺϷ��� �ж���Ա�Ŀ��ڵ����Ƿ�������������ڷ�Χ
		 * 
		 * @param pk_hrorg
		 * @param pk_psndoc
		 * @param beginDate
		 * @param endDate
		 * @return
		 */
		public static boolean checkTBMPsndocTime(String pk_hrorg, String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate) {
			boolean hasTBMPsndocTime = false;
			try {
				ITBMPsndocQueryService service = ServiceLocator.lookup(ITBMPsndocQueryService.class);
				hasTBMPsndocTime = service.checkTBMPsndocDate(pk_hrorg, pk_psndoc, beginDate, endDate);
			} catch (HrssException e) {
				e.deal();
			} catch (BusinessException e) {
				new HrssException(e).deal();
			}
			return hasTBMPsndocTime;
		}

		protected ApplicationContext getApplicationContext() {
			return AppLifeCycleContext.current().getApplicationContext();
		}

		private AppLifeCycleContext getLifeCycleContext() {
			return AppLifeCycleContext.current();
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	
	
	
  }