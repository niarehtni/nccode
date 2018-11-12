package com.ufida.iufo.table.exarea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.pub.smart.cache.SmartDefCache;
import nc.vo.smart.SmartDefVO;

import com.ufida.iufo.pub.tools.AppDebug;
import com.ufida.iufo.table.areafomula.AreaFuncDriver;
import com.ufida.iufo.table.areafomula.CVSBatchFuncDriver;
import com.ufida.iufo.table.areafomula.GetFuncDriver;
import com.ufida.iufo.table.areafomula.NcDataFormatFuncDriver;
import com.ufida.iufo.table.areafomula.SalaryDecryptFuncDriver;
import com.ufida.iufo.table.areafomula.cal.GetAreaFieldFuncDriver;
import com.ufida.report.anareport.model.AbsAnaReportModel;
import com.ufida.report.anareport.model.AnaRepField;
import com.ufida.report.anareport.util.AnaReportFieldUtil;
import com.ufida.report.anareport.util.AreaCrossSetInfoUtil;
import com.ufida.report.chart.model.ChartManager;
import com.ufida.report.free.plugin.param.VarFuncDriver;
import com.ufida.report.swchart.model.FrChartAreaModelMgr;
import com.ufida.zior.exception.ForbidedOprException;
import com.ufsoft.iufo.func.excel.stat.ExcelStatCalcUtil;
import com.ufsoft.script.AreaFormulaHandler;
import com.ufsoft.table.AreaPosition;
import com.ufsoft.table.Cell;
import com.ufsoft.table.CellPosition;
import com.ufsoft.table.CellsModel;
import com.ufsoft.table.UserUIEvent;

/**
 * 扩展区模型监听器，用于处理扩展区合并、分割、删除、缩小等事件
 *
 * @author wanyonga
 *
 */
@SuppressWarnings("deprecation")
public class ExtendAreaModelListener implements ExAreaModelListener {
	private static final long serialVersionUID = 5882121674767028223L;
	private transient ExtendAreaCell m_model;
	private transient CellsModel formatModel;
	private transient AbsAnaReportModel anaModel;
	private transient AreaFormulaHandler m_fromatFormulaHandler = null; // 格式态区域公式逻辑处理

	public ExtendAreaModelListener(AbsAnaReportModel anaModel) {
		this.anaModel = anaModel;
		this.formatModel = anaModel.getFormatModel();
	}

	public ExtendAreaModelListener() {

	}

	@Override
	public String isSupport(UserUIEvent e) throws ForbidedOprException {
		m_model = (ExtendAreaCell) e.getSource();
		switch (e.getEventType()) {
		case ExAreaModelListener.COMBINE:

			IExData[] newModels = (IExData[]) e.getNewValue();
			if (newModels != null) {
				for (IExData ex : newModels) {
					if (ex != null) {
						ExtendAreaCell exCell = ex.getExAreaCell();
						String ds = exCell.getAreaInfoSet().getSmartModelDefID();
						if (m_model.getAreaInfoSet().isCrossSet() || exCell.getAreaInfoSet().getCrossSetInfo() != null) {
							return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0", "01413006-0871")/*@res "存在交叉区域，无法合并"*/;
						} else if (ds != null
								&& (m_model.getAreaInfoSet().getSmartModelDefID() != null && !ds.equals(m_model
										.getAreaInfoSet().getSmartModelDefID()))) {
							return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0", "01413006-0872")/*@res "区域对应不同的语义模型，无法合并"*/;
						}
					}
				}
			}
			break;
		case ExAreaModelListener.SPLIT:
			ExtendAreaCell newArea = (ExtendAreaCell) e.getOldValue();
			if (newArea != null) {
				if (newArea.getAreaInfoSet().getCrossSetInfo() != null) {
					return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0", "01413006-0873")/*@res "存在交叉区域，无法拆分"*/;
				}
			}
			break;
		case ExAreaModelListener.REMOVE:
			break;
		case ExAreaModelListener.CHANGE_AREA:
			break;

		default:
			break;
		}
		return null;
	}

	@Override
	public void userActionPerformed(UserUIEvent e) {
		switch (e.getEventType()) {
		case ExAreaModelListener.COMBINE:
			break;
		case ExAreaModelListener.SPLIT:
			ExtendAreaCell[] newCells = (ExtendAreaCell[]) e.getNewValue();
			if (newCells == null)
				return;
			for (ExtendAreaCell ex : newCells) {
				if (ex == null)
					continue;
				ex.getAreaInfoSet().setSmartModelDefID(m_model.getAreaInfoSet().getSmartModelDefID());
			}
			validateAreaModel(Arrays.asList(newCells));
			break;
		case ExAreaModelListener.REMOVE:
			// 删除扩展区专用的私有语义模型
			String oldDefPk = SmartDefCache.getInstance().getPkByCode(m_model.getExAreaPK());
			if (oldDefPk != null) {
				SmartDefVO smartDefVO = SmartDefCache.getInstance().getDef(oldDefPk);
				if (smartDefVO != null) {
					SmartDefCache.getInstance().remove(oldDefPk);
					//					SmartModelCache.getInstance().remove(oldDefPk);
				}
			}
			//clear(true);
			clearAll();//删除扩展区时，需要删除所有内容，包括格式信息
			break;
		case ExAreaModelListener.CHANGE_AREA:
			AreaPosition newArea = (AreaPosition) e.getNewValue();
			AreaPosition oldArea = m_model.getArea();
			changeCrossArea(newArea);
			List<CellPosition> cells = formatModel.getSeperateCellPos(oldArea);
			int size = cells.size();
			for (int i = size - 1; i >= 0; i--) {
				if (newArea.contain(cells.get(i)))
					cells.remove(i);
			}
			clearCells(cells, true);
			break;
		case ExAreaModelListener.CHANGE_POS:// add by wangyga 2008-9-27
			ExAreaModelListener listener = m_model.getAreaInfoSet().getCrossSetInfo();
			if (listener == null)
				return;
			UserUIEvent event = new UserUIEvent(this, e.getEventType(), e.getOldValue(), e.getNewValue());
			String error = null;
			try {
				error = listener.isSupport(event);
				if (error != null && error.length() > 0) {
					throw new IllegalArgumentException(error);
				}
				listener.userActionPerformed(event);

			} catch (ForbidedOprException ex) {
				throw new IllegalArgumentException(ex);
			} catch (Throwable t) {
				AppDebug.debug(t);
				throw new IllegalArgumentException(t.getMessage());
			}
			break;
		default:
			break;
		}
	}

	/*
	 * 清除数据模型
	 */
	public void clear(boolean clearText) {
		AreaPosition area = m_model.getArea();
		List<CellPosition> cells = formatModel.getSeperateCellPos(area);
		clearCells(cells, clearText);
		// @edit by wangyga at 2010-8-2,下午01:50:33 清除图表
		ChartManager.clearAreaChart(formatModel, area);
		m_model.getAreaInfoSet().setSmartModelDefID(null);
	}

	/**清除所有，包括单元格背景色啥的
	 * 此方法与上面的clear()基本一致，可考虑删除掉一个zhongkm
	 */
	private void clearAll() {
		AreaPosition area = m_model.getArea();
		List<CellPosition> cells = formatModel.getSeperateCellPos(area);

		AnaReportFieldUtil.removeFlds(anaModel, cells.toArray(new CellPosition[0]));
		for (CellPosition pos : cells) {
			Cell cell = formatModel.getCell(pos);
			if (cell != null) {
				formatModel.setCell(pos.getRow(), pos.getColumn(), null);
				//formatModel.setCellValue(pos, null);
				getFormatFormulaHandler(formatModel).clearFormula(pos);

			}
		}

		ChartManager.clearAreaChart(formatModel, area);
		FrChartAreaModelMgr.clearAreaChart(formatModel, area);

		m_model.getAreaInfoSet().setSmartModelDefID(null);
	}

	private void clearCells(List<CellPosition> cells, boolean clearText) {
		AnaReportFieldUtil.removeFlds(anaModel, cells.toArray(new CellPosition[0]));
		for (CellPosition pos : cells) {
			Cell cell = formatModel.getCell(pos);
			if (cell != null) {
				if (clearText) {
					formatModel.setCellValue(pos, null);
					getFormatFormulaHandler(formatModel).clearFormula(pos);
					//add by yanchm,2013.3.19,删除内容时也清空格式信息
					formatModel.clearCells(pos);
				}
			}
		}
	}

	private AreaFormulaHandler getFormatFormulaHandler(CellsModel cells) {
		if (m_fromatFormulaHandler == null) {
			m_fromatFormulaHandler = new AreaFormulaHandler(cells);
			m_fromatFormulaHandler.registerFuncDriver(new AreaFuncDriver());
			m_fromatFormulaHandler.registerFuncDriver(new CVSBatchFuncDriver());
			m_fromatFormulaHandler.registerFuncDriver(new GetFuncDriver());
			// add by yuyangi 参数公式
			m_fromatFormulaHandler.registerFuncDriver(new VarFuncDriver());
			// @edit by yuyangi at 2012-6-25,下午04:15:51 nc数据格式公式
			m_fromatFormulaHandler.registerFuncDriver(new NcDataFormatFuncDriver(anaModel));
			//添加薪资解密函数  add by ward 2018-06-05
			m_fromatFormulaHandler.registerFuncDriver(new SalaryDecryptFuncDriver(anaModel));
			m_fromatFormulaHandler.registerFuncDriver(new GetAreaFieldFuncDriver(anaModel));
			ExcelStatCalcUtil.registExcelFuncDriver(m_fromatFormulaHandler.getCalcEnv());
		}
		return m_fromatFormulaHandler;
	}

	/**
	 * 处理由于扩展区域的拆分、合并引起的交叉区域改变
	 *
	 * @create by guogang at Jan 4, 2009,3:37:53 PM
	 *
	 * @param newArea
	 */
	private void changeCrossArea(AreaPosition newArea) {
		if (m_model.getAreaInfoSet().getCrossSetInfo() != null) {
			AreaPosition oldCrossArea = m_model.getAreaInfoSet().getCrossSetInfo().getCrossArea();
			CellPosition oldCrossPoint = m_model.getAreaInfoSet().getCrossSetInfo().getCrossPoint();
			AreaPosition newCrossArea = oldCrossArea.interArea(newArea);
			AreaCrossSetInfo newcross = null;
			if (newCrossArea != null) {
				if (newCrossArea.contain(oldCrossPoint)) {
					newcross = new AreaCrossSetInfo(newCrossArea, oldCrossPoint);
				} else {
					newcross = new AreaCrossSetInfo(newCrossArea, newCrossArea.getStart());
				}
			}
			m_model.getAreaInfoSet().setCrossSetInfo(newcross);
			if (newcross == null) {
				AreaCrossSetInfoUtil.cancelCrossSet(anaModel, m_model.getAreaInfoSet().getCrossSetInfo(), m_model
						.getAreaInfoSet().getSmartModel());
			}
		}
	}

	/**
	 *
	 * @create by guogang at Jan 12, 2009,11:26:17 AM
	 *
	 * @param al_areas
	 *            分析表要验证的扩展区域
	 */
	public void validateAreaModel(List<ExtendAreaCell> al_areas) {
		if (al_areas != null && al_areas.size() > 0) {
			ArrayList<String> exPKs = new ArrayList<String>();
			for (ExtendAreaCell ex : al_areas) {
				if (exPKs.contains(ex.getBaseInfoSet().getExAreaPK()))
					continue;
				exPKs.add(ex.getBaseInfoSet().getExAreaPK());
				if (!hasAnyField(ex.getArea())) {
					if (ex.getAreaInfoSet().getCrossSetInfo() == null)// modify
						// by
						// wangyga
						// 2008-9-18
						// 扩展区域没有字段，设置数据集PK为null
						ex.getAreaInfoSet().setDataModel(null);
					ex.getAreaInfoSet().setSmartModelDefID(null);
					// @edit by wangyga at 2009-2-12,下午03:06:15 设置扩展区域类型为默认的
					ex.getBaseInfoSet().setExAreaType(ExtendAreaConstants.EX_TYPE_NONE);
				}
			}
		}
	}

	/**
	 *
	 * @create by guogang at Jan 12, 2009,11:15:20 AM
	 *
	 * @param area
	 * @return
	 */
	private boolean hasAnyField(AreaPosition area) {
		List<CellPosition> al_pos = formatModel.getSeperateCellPos(area);
		for (CellPosition pos : al_pos) {
			Cell cell = formatModel.getCell(pos);
			if (cell != null && cell.getExtFmt(AnaRepField.EXKEY_FIELDINFO) != null)
				return true;
		}
		return false;
	}
}