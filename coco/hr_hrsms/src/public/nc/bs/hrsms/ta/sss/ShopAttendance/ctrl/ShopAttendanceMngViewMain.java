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
	
	// 考勤档案数据集
		public static final String DS_TBMPSNDOC = "dsTBMPsndoc";
		// 考勤档案表格
		public static final String TBL_TBMPSNDOC = "tblTBMPsndoc";
		// 年度期间是否变化的参数
		public static final String SESSION_DATE_CHANGE = "isDateChange";
		// 渲染类型
		private static final String RENDER_TYEP = "TimeDataMngRender";
		// 渲染类型
		
		// 设置靠左靠右居中
		private static final String TEXT_ALIGN = "center";
		// 字段前缀
		private static final String COLUMN_FIELD_HEAD = "col_";
		// 多表头前缀
		private static final String GROUP_FIELD_HEAD = "group_";
		// 按人员、日期查看页面
//		public static final String PAGE_TIMEDATAFORMNGAPP = "/app/TimeDataMngApp";
		// 按人员、日期查看页面
		public static final String PAGE_SHOPATTFORMNGAPP ="/app/ShopAttendanceMngApp?nodecode=E20600977";
		
		
		// 人员编码
		public static final String TIMEDATE_PSNCODE = "pk_psnjob_pk_psndoc_code";
		// 前缀-数据集字段
		private static final String COLOR_PREFIX = "color_";
		
		
		/**
		 * 数据集加载事件
		 * 
		 * @param dataLoadEvent
		 * @throws BusinessException 
		 */
		public void onDataLoad_dsTBMPsndoc(DataLoadEvent dataLoadEvent) throws BusinessException {
			Dataset ds = dataLoadEvent.getSource();
			if (isPagination(ds)) { // 分页操作
				// 查询条件-部门
				String pk_dept = (String) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_PK_DEPT);
				// 查证条件-开始日期
				UFLiteralDate beginDate = (UFLiteralDate) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_BEGINDATE);
				// 查询条件-结束日期
				UFLiteralDate endDate = (UFLiteralDate) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_ENDDATE);
				// 查询条件-onlyShowException
				UFBoolean onlyShowException = (UFBoolean) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_ONLY_SHOW_EXCEPTION);
				boolean ose = onlyShowException.booleanValue();
				// 查询条件-FromWhereSQL
				FromWhereSQL fromWhereSQL = (FromWhereSQL) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_FROMWHERESQL);
				boolean includeSubDept = SessionUtil.isIncludeSubDept();
				initData(ds, fromWhereSQL, beginDate, endDate, pk_dept, includeSubDept, ose);

			} else {// 初始化操作
				CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
			}

		}

		/**
		 * 分页操作标志
		 * 
		 * @param ds
		 * @return
		 */
		private boolean isPagination(Dataset ds) {
			PaginationInfo pg = ds.getCurrentRowSet().getPaginationInfo();
			return pg.getRecordsCount() > 0;
		}

		/**
		 * 管理部门变更
		 * 
		 * @param keys
		 * @throws BusinessException 
		 */
		public void pluginDeptChange(Map<String, Object> keys) throws BusinessException {
			TaAppContextUtil.addTaAppContext();
			ApplicationContext appCxt = AppLifeCycleContext.current().getApplicationContext();
			appCxt.addAppAttribute(ShopAttendanceMngViewMain.SESSION_DATE_CHANGE, UFBoolean.TRUE);
			// 当前考勤期间
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
				// 部门发生变更、年度未发生变更
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

			// 初始化操作
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());

		}

		/**
		 * 搜索
		 * 
		 * @param keys
		 * @throws BusinessException 
		 */
		public void pluginSearch(Map<String, Object> keys) throws BusinessException {
			TBMPsndocUtil.checkTimeRuleVO();
			
			LfwView viewMain = getLifeCycleContext().getViewContext().getView();
			Dataset ds = viewMain.getViewModels().getDataset(DS_TBMPSNDOC);
			// 清空数据集
			DatasetUtil.clearData(ds);
			// 管理部门
			String pk_dept = SessionUtil.getPk_mng_dept();
			if (StringUtils.isEmpty(pk_dept)) {
				return;
			}
			// 是否包含下级部门
			boolean includeSubDept = SessionUtil.isIncludeSubDept();
			// 组织
			String pk_org = SessionUtil.getHROrg();
			nc.uap.ctrl.tpl.qry.FromWhereSQLImpl whereSql = (nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys.get(HrssConsts.PO_SEARCH_WHERESQL);
			// 查询条件-FromWhereSQL
			FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) CommonUtil.getUAPFromWhereSQL(whereSql);
			// 获得自定义条件
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
																																 * "查询失败"
																																 */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0143")/*
																																																						 * @
																																																						 * res
																																																						 * "年考勤期间未定义！"
																																																						 */);
				return;
			}
			if (StringUtil.isEmptyWithTrim(month)) {
				removeCol(viewMain, ds);
				CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pub-res", "0c_pub-res0168")/*
																																 * @
																																 * res
																																 * "查询失败"
																																 */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0144")/*
																																																						 * @
																																																						 * res
																																																						 * "月考勤期间未定义！"
																																																						 */);
				return;
			}
			// 显示异常
			String onlyShowException = selfDefMap.get(ShopAttForMngPageModel.FIELD_ONLY_SHOW_EXCEPTION);
			UFBoolean ose = UFBoolean.valueOf(onlyShowException);
			// 根据年度期间查询期间VO
			PeriodVO periodVO = ShopAttendanceUtil.queryPeriodVOByYearMonth(pk_org, year, month);
			if (periodVO == null) {
				return;
			}
			// 开始日期
			UFLiteralDate beginDate = periodVO.getBegindate();
			// 结束日期
			UFLiteralDate endDate = periodVO.getEnddate();

			getLifeCycleContext().getApplicationContext().addAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_BEGINDATE, beginDate);
			getLifeCycleContext().getApplicationContext().addAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_ENDDATE, endDate);
			getLifeCycleContext().getApplicationContext().addAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_FROMWHERESQL, fromWhereSQL);
			getLifeCycleContext().getApplicationContext().addAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_ONLY_SHOW_EXCEPTION, ose);
			getLifeCycleContext().getApplicationContext().addAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_PK_DEPT, pk_dept);

			// 构造表格
			initDsAndGrid(viewMain, ds, beginDate, endDate);
			getApplicationContext().addBeforeExecScript("window.colorMap = null");
			getApplicationContext().addBeforeExecScript("window.editList = null");
			// 加载数据
			initData(ds, fromWhereSQL, beginDate, endDate, pk_dept, includeSubDept, ose.booleanValue());

		}

		public void initData(Dataset ds, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, String pk_dept, boolean includeSubDept, boolean onlyShowException) {
			// 查询考勤数据
			List<String> psnList = loadTimeDataByDate(fromWhereSQL, beginDate, endDate, pk_dept, includeSubDept, onlyShowException);
			String[] psndocs = psnList.toArray(new String[0]);
			if (StringUtils.isEmpty(ds.getCurrentKey())) {
				ds.setCurrentKey(Dataset.MASTER_KEY);
			}
			PaginationInfo pinfo = ds.getCurrentRowSet().getPaginationInfo();
			if (ArrayUtils.isEmpty(psndocs)) {
				DatasetUtil.clearData(ds);
				pinfo.setRecordsCount(0);
				// 更新菜单状态
				ds.setEnabled(false);
				ButtonStateManager.updateButtons();
				return;
			}
			// 每页记录条数
			int pageSize = pinfo.getPageSize();
			if (pageSize == -1) {
				return;
			}
			// 总记录数
			int recordsCount = psndocs.length;
			// 页数
			int pageIndex = pinfo.getPageIndex();
			pinfo.setRecordsCount(recordsCount);
			int PagebeignSize = pageIndex * pageSize;

			int length = (recordsCount - PagebeignSize) < pageSize ? (recordsCount - PagebeignSize) : pageSize;
			int pageMaxSize = PagebeignSize + length;
			String[] result = new String[length];
			for (int j = 0, i = PagebeignSize; i < pageMaxSize; i++, j++) {
				result[j] = psndocs[i];
			}

			for (int i = 0; i < result.length; i++) { // 按人员循环
				Row row = ds.getEmptyRow();
				row.setValue(ds.nameToIndex(TBMPsndocVO.PK_PSNDOC), result[i]);
				ds.addRow(row);
			}
		}

		/**
		 * 重构表格时去掉不要的列
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
			// 删除原有COLUMNS
			for (Iterator<IGridColumn> it = grid.getColumnList().iterator(); it.hasNext();) {
				col = it.next();
				if (col instanceof GridColumnGroup) {
					groupList.add(col);
				}
			}
			grid.removeColumns(groupList, true);
		}

		/**
		 * 根据开始日期和结束日期,构造数据集和日期表格
		 * 
		 * @param beginDate
		 * @param endDate
		 */
		private void initDsAndGrid(LfwView viewMain, Dataset dsTbmPsndoc, UFLiteralDate beginDate, UFLiteralDate endDate) {

			// 查询条件开始日期和结束日期没有发生变化,不用重构造DS/Grid！
			UFBoolean dateChangeFlag = (UFBoolean) getApplicationContext().getAppAttribute(SESSION_DATE_CHANGE);
			// 重置状态
			getApplicationContext().addAppAttribute(SESSION_DATE_CHANGE, UFBoolean.FALSE);
			if (dateChangeFlag != null && !dateChangeFlag.booleanValue()) {
				return;
			}

			removeCol(viewMain, dsTbmPsndoc);
			GridComp grid = (GridComp) viewMain.getViewComponents().getComponent(TBL_TBMPSNDOC);

			// 新增新的COLUMNS
			List<IGridColumn> groupList = new ArrayList<IGridColumn>();

			Map<String, GridColumnGroup> columnGroupMap = new HashMap<String, GridColumnGroup>();

			// 获得间隔天数
			int days = UFLiteralDate.getDaysBetween(beginDate, endDate) + 1;
			for (int i = 0; i < days; i++) {
				// 日期
				UFLiteralDate curDate = beginDate.getDateAfter(i);
				// 创建列表数据集字段
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
			// 暂时只支持跨两个月
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
		 * 创建Field字段
		 * 
		 * @param viewMain
		 * @param dsCalendar
		 * @param dateAfter
		 */
		private void buildDatasetField(LfwView widget, Dataset ds, UFLiteralDate curDate, Map<String, GridColumnGroup> columnGroupMap) {
			/** 创建字段 */
			Field writeField = buildField(curDate);
			/** 数据集加入字段 */
			ds.getFieldSet().addField(writeField);
			/** 生成单元格颜色字段 */
			Field colorField = buildColorField(writeField.getId());
			ds.getFieldSet().addField(colorField);
			/** GridColumn */
			buildGridGroup(ds, writeField, curDate, columnGroupMap);
		}

		/**
		 * 创建字段
		 * 
		 * @param curDate
		 * @return
		 */
		private Field buildField(UFLiteralDate curDate) {
			// 班级字段
			Field readField = new Field();
			// 设置字段ID
			readField.setId(curDate.toPersisted().replace("-", ""));
			// 设置数据类型
			readField.setDataType(DataTypeTranslator.translateInt2String(IType.REF));
			// 与VO对应字段(属性)
			readField.setField(null);
			readField.setText(String.valueOf(curDate.toPersisted().replace("-", "")));
			return readField;
		}

		/**
		 * 记录单元格显示颜色的字段
		 * 
		 * @param nameFieldId
		 * @return
		 */
		private Field buildColorField(String nameFieldId) {
			Field colorField = new Field();
			// 设置字段ID
			colorField.setId(COLOR_PREFIX + nameFieldId);// 格式：color_20120701_name
			// 设置数据类型
			colorField.setDataType(StringDataTypeConst.STRING);
			// 与VO对应字段(属性)
			colorField.setField(null);
			return colorField;
		}

		/**
		 * 创建表格项
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
			// 设置id
			column.setId(COLUMN_FIELD_HEAD + writeField.getId());
			// 设置关联Dataset的Field
			column.setField(writeField.getId());
			// 设置列宽度,按实际宽度
			column.setWidth(50);
			// 设置渲染器
			column.setRenderType(RENDER_TYEP);
			// 设置显示标题
			column.setText(writeField.getText());
			column.setTextAlign(TEXT_ALIGN);

			String yearAndMonth = writeField.getId().substring(0, 6);
			if (columnGroupMap.get(yearAndMonth) == null) {
				// 创建Grid多表头配置类
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
		 * 跳转到按人员查看页面
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
			// 人员主键
			String pk_psndoc = (String) selRow.getValue(ds.nameToIndex(PsndocVO.PK_PSNDOC));
			// 人员姓名
			String pk_psndoc_name = (String) selRow.getValue(ds.nameToIndex(ShopAttForMngPageModel.FIELD_TIME_DATA_PSNNAME));
			SessionBean bean = SessionUtil.getSessionBean();
			// 把人员主键放入Session中
			bean.setExtendAttribute(PsndocVO.PK_PSNDOC, pk_psndoc);
			bean.setExtendAttribute(ShopAttForMngPageModel.FIELD_TIME_DATA_PSNNAME, pk_psndoc_name);
			bean.setExtendAttribute(ShopAttForMngPageModel.APP_STATUS, ShopAttForMngPageModel.STATUS_BYNAME_BROWSE);
			// 跳转到按人员查看页面
			sendRedirect(PAGE_SHOPATTFORMNGAPP);
		}

		/**
		 * 按日期查看
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
			// 把人员主键放入Session中
			bean.setExtendAttribute("headerDivTextValue", headerDivTextValue);
			bean.setExtendAttribute(ShopAttForMngPageModel.APP_STATUS, ShopAttForMngPageModel.STATUS_BYDATE_BROWSE);
			// 跳转到按日期查看页面
			sendRedirect(PAGE_SHOPATTFORMNGAPP);
		}

		/**
		 * 跳转页面
		 */
		public static void sendRedirect(String app) {
			ApplicationContext appCtx = AppLifeCycleContext.current().getApplicationContext();
			String url = LfwRuntimeEnvironment.getRootPath() + app;
			appCtx.sendRedirect(url);
		}

		/**
		 * 查询员工考勤数据
		 * 
		 * @param fromWhereSQL
		 * @param beginDate
		 * @param endDate
		 * @param pk_dept
		 * @param containsSubDepts
		 * @return
		 */
		public List<String> loadTimeDataByDate(FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, String pk_dept, boolean containsSubDepts, boolean onlyShowException) {
			// 机器考勤数据
			TimeDataVO[] machineDataVOs = null;
			// 手工考勤数据
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
			// 为数据填充颜色
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
		 * 获取单元格颜色描述的字符串
		 * 
		 * @param tiData
		 * @param laData
		 * @return
		 */
		private static String getTimeDataColorInJSON(TimeDataVO[] tiData, LateEarlyVO[] laData) {
			StringBuilder sb = new StringBuilder("");
			List<Color> colorList = null;

			// 手工考勤数据处理
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

					// 包含未出勤，则显示为未出勤
					if (colorList.contains(IColorConst.COLOR_ABSENT)) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_ABSENT)).append("\"");
					}
					// 迟到早退
					else if (colorList.contains(IColorConst.COLOR_LATEEARLY)) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY)).append("\"");
					}
					// 无工作日历
					else if (colorList.contains(IColorConst.COLOR_NONPSNCALENDAR)) {
						if (sb.length() > 0)
							sb.append(",");
						// if (checkTBMPsndocTime(vo.getPk_org(), vo.getPk_psndoc(),
						// vo.getCalendar(), vo.getCalendar())) {
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR)).append("\"");
						// } else {
						// // 未设置考勤档案
						// sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-",
						// "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_NONTBMPSNDOC)).append("\"");
						// }

					}
					// 正常
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
//							// 未发生的手工考勤，这里和公休一样，什么背景色都不用
//							sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append("holiday").append("\"");
//						}
					}
					// 公休
					else if (ShiftVO.PK_GX.equals(vo.getPk_shift())) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append("holiday").append("\"");
					}

				}
			}

			// 机器考勤数据处理
			if (null != tiData && tiData.length > 0) {
				String[] attr = tiData[0].getAttributeNames();
				for (TimeDataVO vo : tiData) {
					if ((vo.getIsmidoutabnormal()!=null&&vo.getIsmidoutabnormal().booleanValue()
							||vo.getMidwayoutcount()!=null&&vo.getMidwayoutcount()<0)//加或者是为了升级时显示设置的
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

					// 包含未出勤，则显示为未出勤
					if (colorList.contains(IColorConst.COLOR_ABSENT)) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_ABSENT)).append("\"");
					}
					// 迟到早退
					else if (colorList.contains(IColorConst.COLOR_LATEEARLY)) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY)).append("\"");
					}
					// 中途处出
					else if (colorList.contains(IColorConst.COLOR_MIDOUT)) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_MIDOUT)).append("\"");
					}
					// 无工作日历
					else if (colorList.contains(IColorConst.COLOR_NONPSNCALENDAR)) {
						if (sb.length() > 0)
							sb.append(",");
						// if (checkTBMPsndocTime(vo.getPk_org(), vo.getPk_psndoc(),
						// vo.getCalendar(), vo.getCalendar())) {
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR)).append("\"");
						// } else {
						// // 未设置考勤档案
						// sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-",
						// "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_NONTBMPSNDOC)).append("\"");
						// }

					}
					// 未生成考勤数据
					else if (colorList.contains(IColorConst.COLOR_NOTIMEDATA)) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_NOTIMEDATA)).append("\"");
					}
					// 刷卡地点异常
					else if (colorList.contains(IColorConst.COLOR_PLACEEXCEPTION)) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append(TAUtil.getHexDesc(IColorConst.COLOR_PLACEEXCEPTION)).append("\"");
					}
					// 正常
					else if (!ShiftVO.PK_GX.equals(vo.getPk_shift())) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toPersisted().replace("-", "")).append("\":\"").append("right").append("\"");
					}
					// 公休
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
		 * 校验日期的合法性 判断人员的考勤档案是否包含给定的日期范围
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