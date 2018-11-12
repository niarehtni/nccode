package com.ufida.report.free.plugin.formula;

import java.util.EventObject;
import java.util.List;

import javax.swing.JTextField;

import com.ufida.iufo.pub.tools.AppDebug;
import com.ufida.iufo.table.areafomula.AreaFuncDriver;
import com.ufida.iufo.table.areafomula.CVSBatchFuncDriver;
import com.ufida.iufo.table.areafomula.GetFuncDriver;
import com.ufida.iufo.table.areafomula.NcDataFormatFuncDriver;
import com.ufida.iufo.table.areafomula.SalaryDecryptFuncDriver;
import com.ufida.iufo.table.areafomula.cal.GetAreaFieldFuncDriver;
import com.ufida.report.anadesigner.AbsAnaReportDesigner;
import com.ufida.report.anareport.FreeReportContextKey;
import com.ufida.report.anareport.model.AbsAnaReportModel;
import com.ufida.report.anareport.model.AnaReportModel;
import com.ufida.report.free.plugin.param.VarFuncDriver;
import com.ufida.report.sysplugin.insertdelete.DeleteInsertDialog;
import com.ufida.zior.exception.ForbidedOprException;
import com.ufida.zior.plugin.AbstractPlugin;
import com.ufida.zior.plugin.IPluginAction;
import com.ufsoft.iufo.func.excel.stat.ExcelStatCalcUtil;
import com.ufsoft.report.IufoFormat;
import com.ufsoft.report.constant.PropertyType;
import com.ufsoft.report.edit.EditConstants;
import com.ufsoft.report.edit.EditParameter;
import com.ufsoft.script.AreaFmlExecutor;
import com.ufsoft.script.AreaFormulaUtil;
import com.ufsoft.script.base.AreaFormulaModel;
import com.ufsoft.script.base.CommonExprCalcEnv;
import com.ufsoft.script.base.FormulaVO;
import com.ufsoft.script.exception.ParseException;
import com.ufsoft.table.AreaPosition;
import com.ufsoft.table.CellPosition;
import com.ufsoft.table.CellsModel;
import com.ufsoft.table.IArea;
import com.ufsoft.table.UserActionListner;
import com.ufsoft.table.UserUIEvent;
import com.ufsoft.table.event.HeaderEvent;
import com.ufsoft.table.event.HeaderModelListener;
import com.ufsoft.table.event.PropertyChangeEventAdapter;
import com.ufsoft.table.format.TableConstant;
import com.ufsoft.table.re.CellRenderAndEditor;

/**
 * 分析报表区域公式定义插件
 * 
 * @author wanyonga
 * @created at 2010-7-7,上午10:59:34
 * 
 */
public class AnaAreaFormulaDefPlugin extends AbstractPlugin implements
		HeaderModelListener, UserActionListner
{
	public static final String GROUP = "miufo1000909";// 单元公式

	private CellsModel getCellsModel()
	{
		if (getAnaReportModel() != null)
		{
			return getAnaReportModel().getFormatModel();
		}
		return null;
	}

	private AbsAnaReportDesigner getAnaDesigner()
	{
		if (getMainboard().getCurrentView() instanceof AbsAnaReportDesigner)
		{
			return (AbsAnaReportDesigner) getMainboard().getCurrentView();
		}
		return null;
	}

	private AbsAnaReportModel getAnaReportModel()
	{
		AbsAnaReportDesigner anaDesigner = getAnaDesigner();
		if (anaDesigner != null)
		{
			return anaDesigner.getAnaReportModel();
		}
		return null;
	}

	public AreaFmlExecutor getFmlExecutor()
	{
		AreaFormulaModel fromulaModel = AreaFormulaModel
				.getInstance(getAnaReportModel().getFormatModel());
		AreaFmlExecutor executor = fromulaModel.getAreaFmlExecutor();
		if (executor == null)
		{
			executor = new AreaFmlExecutor(getAnaReportModel().getContext(),getAnaReportModel().getFormatModel());
		}
		executor.getParserProxy().getEnv().setExEnv(CommonExprCalcEnv.EXPR_EXEC_TIMEZONE,
				getAnaReportModel().getContext().getAttribute(FreeReportContextKey.REPORT_EXEC_TIMEZONE));
		return executor;
	}

	@Override
	protected IPluginAction[] createActions()
	{
		return new IPluginAction[] { new AnaAreaFormulaDefAction(),
				new AnaAreaFormulaToolBarAction() };
	}

	@Override
	public void shutdown()
	{

	}

	@Override
	public void startup()
	{
		getEventManager().addListener(this);
		CellRenderAndEditor.getInstance(getMainboard()).registExtSheetRenderer(
				new AnaFormulaRenderer());
		CellRenderAndEditor.getInstance(getMainboard()).registExtSheetEditor(
				new AnaFormulaDefEditor(new JTextField()));
		regFormula();
	}

	private void regFormula() {
		CellsModel fmtModel = getAnaModel().getFormatModel();
		AreaFormulaModel areaFormulaModel = AreaFormulaModel.getInstance(fmtModel);
		AreaFmlExecutor fmtExecutor = areaFormulaModel.getAreaFmlExecutor();
		if (areaFormulaModel.getAreaFmlExecutor() == null) {
			fmtExecutor = new AreaFmlExecutor(getAnaModel().getContext(), fmtModel);
			areaFormulaModel.setAreaFmlExecutor(fmtExecutor);
		}
		AreaFuncDriver driver = (AreaFuncDriver) fmtExecutor.getFuncListInst().getExtDriver(
				AreaFuncDriver.class.getName());
		if (driver == null) {
			driver = new AreaFuncDriver(AnaReportModel.getInstance(fmtModel));
			fmtExecutor.registerFuncDriver(driver);
		}
		CVSBatchFuncDriver driver2 = (CVSBatchFuncDriver) fmtExecutor.getFuncListInst().getExtDriver(
				CVSBatchFuncDriver.class.getName());
		if (driver2 == null) {
			driver2 = new CVSBatchFuncDriver(AnaReportModel.getInstance(fmtModel));
			fmtExecutor.registerFuncDriver(driver2);
		}
		// add by yuyangi 增加参数公式注册
		VarFuncDriver varFuncDriver = (VarFuncDriver)fmtExecutor.getFuncListInst().getExtDriver(
				VarFuncDriver.class.getName());
		if(varFuncDriver == null) {
			varFuncDriver = new VarFuncDriver(AnaReportModel.getInstance(fmtModel));
			fmtExecutor.registerFuncDriver(varFuncDriver);
		}
		// 按字段取值函数
		GetFuncDriver getDriver = (GetFuncDriver)fmtExecutor.getFuncListInst().getExtDriver(
				GetFuncDriver.class.getName());
		if(getDriver == null) {
			getDriver = new GetFuncDriver(AnaReportModel.getInstance(fmtModel));
			fmtExecutor.registerFuncDriver(getDriver);
		}
		// nc数据格式公式驱动
		NcDataFormatFuncDriver ncDataFormatFuncDriver = (NcDataFormatFuncDriver)fmtExecutor.getFuncListInst().getExtDriver(
				NcDataFormatFuncDriver.class.getName());
		if(ncDataFormatFuncDriver == null) {
			ncDataFormatFuncDriver = new NcDataFormatFuncDriver(AnaReportModel.getInstance(fmtModel));
			fmtExecutor.registerFuncDriver(ncDataFormatFuncDriver);
		}
		//添加薪资解密函数  add by ward 2018-06-05
		SalaryDecryptFuncDriver  salaryDecryptFuncDriver = (SalaryDecryptFuncDriver)fmtExecutor.getFuncListInst().getExtDriver(
				SalaryDecryptFuncDriver.class.getName());
		if(salaryDecryptFuncDriver == null) {
			salaryDecryptFuncDriver = new SalaryDecryptFuncDriver(AnaReportModel.getInstance(fmtModel));
			fmtExecutor.registerFuncDriver(salaryDecryptFuncDriver);
		}
		
		GetAreaFieldFuncDriver getAreaFunc = (GetAreaFieldFuncDriver)fmtExecutor.getFuncListInst().getExtDriver(
				GetAreaFieldFuncDriver.class.getName());
		if(getAreaFunc == null) {
			getAreaFunc = new GetAreaFieldFuncDriver(getAnaModel());
			fmtExecutor.registerFuncDriver(getAreaFunc);
		}
		
		ExcelStatCalcUtil.registExcelFuncDriver(fmtExecutor);
	}
	
	public AbsAnaReportModel getAnaModel()
	{
		if (getAnaDesigner() != null)
		{
			return getAnaDesigner().getAnaReportModel();
		}
		return null;
	}
	
	@Override
	public String isSupport(int source, EventObject e)
			throws ForbidedOprException
	{
		return null;
	}

	@Override
	public int getPriority()
	{
		return 0;
	}

	@Override
	public void headerCountChanged(HeaderEvent e)
	{
		// 0 行删除， 1 列删除； 2行插入， 3 列插入
		int oper;
		if (e.isRow())
		{
			if (e.isHeaderAdd())
			{
				oper = 2;
			} else
			{
				oper = 0;
			}
		} else
		{
			if (e.isHeaderAdd())
			{
				oper = 3;
			} else
			{
				oper = 1;
			}
		}

		// 公式链处理及相对区域公式更改
		getFmlExecutor().getFormulaModel().headerCountChanged(e);
		getFmlExecutor().updateFormulas(e.getStartIndex(), e.getCount(), oper);
	}

	@Override
	public void headerPropertyChanged(PropertyChangeEventAdapter e)
	{
	}

	@Override
	public void userActionPerformed(UserUIEvent e)
	{
		switch (e.getEventType())
		{
		case UserUIEvent.PASTE:
			processPasteEvent(e);
			break;
		case UserUIEvent.CLEAR:
			processClearEvent(e);
			break;
		case UserUIEvent.COMBINECELL:
			AreaPosition area1 = (AreaPosition) e.getOldValue();
			processCombineCellEvent(area1);
			break;
		case UserUIEvent.UNCOMBINECELL:
			AreaPosition area2 = (AreaPosition) e.getOldValue();
			processUnCombineCellEvent(area2);
			break;
		case UserUIEvent.DELETECELL:
			processDeleteCellEvent(e);
			break;
		case UserUIEvent.INSERTCELL:
			processInsertCellEvent(e);
			break;
		case UserUIEvent.MODEL_CHANGED:
			// fmlExecutor = null;
			// getFmlExecutor();
			break;
		default:
			break;
		}
	}

	protected void processPasteEvent(UserUIEvent e)
	{
		// 和56一致，数据态粘贴不粘贴公式
		if (!getAnaReportModel().isFormatState())
		{
			return;
		}
		if (e == null)
		{
			return;
		}
		Object object = e.getNewValue();
		EditParameter parameter = null;
		if (object instanceof EditParameter)
		{
			parameter = (EditParameter) object;
		}
		// 获得源区域
		IArea areaSrc = parameter.getCopyArea();
		int iPasteType = parameter.getClipType();
		pasteFormulas(areaSrc, iPasteType, true);
	}

	private void pasteFormulas(IArea areaSrc, int iPasteType,
			boolean isInstantFml)
	{
		if (areaSrc == null)
		{
			return;
		}
		// 得到当前选中表页的焦点单元。
		CellPosition target = getCellsModel().getSelectModel().getAnchorCell();
		Object[][] srcFormulas = getFmlExecutor().getFormulaModel()
				.getRelatedAllFml(areaSrc);// 获得公式和单元位置的数组

		// 粘贴单元公式
		if (srcFormulas == null)
		{
			return;
		}
		for (int i = 0, size = srcFormulas.length; i < size; i++)
		{
			if (srcFormulas[i] == null || srcFormulas[i].length < 2)
				continue;
			IArea srcAreaTemp = (IArea) srcFormulas[i][0];// 获得区域
			FormulaVO fmlTemp = (FormulaVO) srcFormulas[i][1];// 获得公式
			IArea destAreaTemp = getDestArea(srcAreaTemp, areaSrc, target);
			String strFormula = AreaFormulaUtil.modifyFormula(srcAreaTemp,
					destAreaTemp,
					fmlTemp == null ? null : fmlTemp.getContent(),
					getCellsModel().getMaxRow(), getCellsModel().getMaxCol());

			StringBuffer showErrMessage = new StringBuffer();
			try
			{
				getFmlExecutor().addDbDefFormula(showErrMessage, destAreaTemp,
						strFormula, null, isInstantFml);
			} catch (ParseException e1)
			{
				AppDebug.debug(e1);
			}
			if (iPasteType == EditConstants.CELL_CONTENT)
				pasteCellType(srcAreaTemp, destAreaTemp);

		}
	}

	/**
	 * 粘贴源单元类型。要求目标与源区域大小一样
	 * 
	 * @param srcArea
	 * @param destArea
	 */
	private void pasteCellType(IArea srcArea, IArea destArea)
	{
		if (srcArea == null || destArea == null)
			return;
		List<CellPosition> listCell = getCellsModel().getSeperateCellPos(
				destArea);
		if (listCell == null)
			return;
		CellPosition cell = null;
		for (int i = 0, size = listCell.size(); i < size; i++)
		{
			cell = listCell.get(i);
			if (cell == null)
				continue;
			IufoFormat format = (IufoFormat) getCellsModel()
					.getCellFormat(cell);
			if (format == null
					|| format.getCellType() == TableConstant.CELLTYPE_SAMPLE)
			{
				// 获得粘贴源的单元格类型
				IufoFormat srcFormat = getSrcCellFormat(srcArea, destArea, cell);
				if (srcFormat != null
						&& srcFormat.getCellType() != TableConstant.CELLTYPE_SAMPLE)
					getCellsModel().setCellProperty(cell,
							PropertyType.DataType, srcFormat.getCellType());
			}
		}
	}

	private IufoFormat getSrcCellFormat(IArea srcArea, IArea destArea,
			CellPosition destCell)
	{
		CellPosition srcStartCell = srcArea.getStart();
		int iWidth = destCell.getRow() - destArea.getStart().getRow();
		int iHeight = destCell.getColumn() - destArea.getStart().getColumn();
		if (iWidth < 0 || iHeight < 0)
			return null;

		CellPosition srcCell = CellPosition.getInstance(srcStartCell.getRow()
				+ iWidth, srcStartCell.getColumn() + iHeight);
		return (IufoFormat) getCellsModel().getCellFormat(srcCell);
	}

	/**
	 * 获得粘贴目标区域
	 * 
	 * @param areaCurrent
	 * @param srcArea
	 * @param destCell
	 * @return
	 */
	private IArea getDestArea(IArea srcAreaCurrent, IArea srcArea,
			CellPosition destCell)
	{
		int srcRowStart = srcArea.getStart().getRow();
		int srcColStart = srcArea.getStart().getColumn();

		int rowStart = destCell.getRow();
		int colStart = destCell.getColumn();

		int iOffRow = srcAreaCurrent.getStart().getRow() - srcRowStart;
		int iOffCol = srcAreaCurrent.getStart().getColumn() - srcColStart;

		IArea destAreaTemp = AreaPosition.getInstance(rowStart + iOffRow,
				colStart + iOffCol, srcAreaCurrent.getWidth(), srcAreaCurrent
						.getHeigth());

		return destAreaTemp;

	}

	/**
	 * 处理清除事件.
	 */
	private void processClearEvent(UserUIEvent e)
	{
		AreaPosition[] aimArea = (AreaPosition[]) e.getNewValue();
		if (aimArea == null || aimArea.length == 0)
		{
			return;
		}
		for (int i = 0; i < aimArea.length; i++)
		{
			AreaPosition area = aimArea[i];
			getFmlExecutor().clearFormula(area);
		}
	}

	/**
	 * 删除非首单元格的公式。
	 * 
	 * @param area
	 */
	private void processUnCombineCellEvent(AreaPosition area)
	{
		processCombineCellEvent(area);
	}

	/**
	 * 删除非首单元格的公式。
	 * 
	 * @param area
	 */
	private void processCombineCellEvent(AreaPosition area)
	{
		CellPosition startPos = area.getStart();
		for (int dRow = 0; dRow < area.getHeigth(); dRow++)
		{
			for (int dCol = 0; dCol < area.getWidth(); dCol++)
			{
				CellPosition each = (CellPosition) startPos.getMoveArea(dRow,
						dCol);
				if (dRow != 0 || dCol != 0)
				{// 非首单元格
					getFmlExecutor().clearFormula(each);
				}
			}
		}

		// 如果首单元格定义有公式，则根据组合单元更新公式定义
		FormulaVO fmlVO = getFmlExecutor().getFormulaModel().getDirectFml(
				startPos);
		if (fmlVO != null)
		{
			getFmlExecutor().updateFormulaAreaPos(startPos, area, fmlVO, true,
					true);
		}

	}

	private void processInsertCellEvent(UserUIEvent e)
	{
		int insertType = (Integer) e.getOldValue();
		AreaPosition aimArea = (AreaPosition) e.getNewValue();
		if (insertType == DeleteInsertDialog.CELL_MOVE_RIGHT)
		{// 单元格右移
			getFmlExecutor().getFormulaModel().insertOrDeleteCell(true, true,
					aimArea);
		} else
		{// 单元格下移
			getFmlExecutor().getFormulaModel().insertOrDeleteCell(true, false,
					aimArea);
		}
	}

	/**
	 * userActionPerformed中处理UserUIEvent.DELETECELL事件的实际处理方法 处理删除单元格时对单元格上公式的删除
	 * 
	 * @param e
	 */
	private void processDeleteCellEvent(UserUIEvent e)
	{
		int delType = (Integer) e.getOldValue();
		AreaPosition aimArea = (AreaPosition) e.getNewValue();
		if (delType == DeleteInsertDialog.CELL_MOVE_LEFT)
		{// 单元格左移
			getFmlExecutor().getFormulaModel().insertOrDeleteCell(false, true,
					aimArea);
		} else
		{// 单元格上移
			getFmlExecutor().getFormulaModel().insertOrDeleteCell(false, false,
					aimArea);
		}
	}

}
