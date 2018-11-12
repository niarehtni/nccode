/**
 *
 */
package com.ufida.iufo.table.areafomula;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.ufida.iufo.pub.tools.AppDebug;
import com.ufida.iufo.table.areafomula.cal.GetAreaFieldFuncDriver;
import com.ufida.report.anareport.FreeReportContextKey;
import com.ufida.report.anareport.model.AbsAnaReportModel;
import com.ufida.report.free.plugin.param.VarFuncDriver;
import com.ufsoft.iufo.func.excel.stat.ExcelStatCalcUtil;
import com.ufsoft.script.AreaFormulaCalUtil;
import com.ufsoft.script.AreaFormulaUtil;
import com.ufsoft.script.CmdProxy;
import com.ufsoft.script.base.AreaFormulaModel;
import com.ufsoft.script.base.CommonExprCalcEnv;
import com.ufsoft.script.base.FormulaVO;
import com.ufsoft.script.base.IParsed;
import com.ufsoft.script.datachannel.ITableData;
import com.ufsoft.script.exception.CmdException;
import com.ufsoft.script.expression.AreaExprCalcEnv;
import com.ufsoft.script.expression.UfoCmdLet;
import com.ufsoft.table.AreaPosition;
import com.ufsoft.table.CellPosition;
import com.ufsoft.table.IArea;

/**
 * @update liuchun at 2011-04-28 自由报表扩展时，对公式进行计算公式链构建的重新实现
 *
 * @author wanyonga
 * @created at 2010-7-12,下午09:34:32
 *
 */
public class FreeAreaFormulaCalUtil extends AreaFormulaCalUtil {

	// 主表单元公式计算链
	private Vector<Vector<IArea>> mainCellFmlList;

	Hashtable<IArea, FormulaVO> mainFmlHash;

	private AbsAnaReportModel anaModel;
	private AreaPosition m_area;
	
	private ITableData m_AreaDataChannel;

	public FreeAreaFormulaCalUtil(AbsAnaReportModel anaModel, AreaPosition area) {
		super();
		this.anaModel = anaModel;
		this.m_area = area;
		initExectuorEnv(anaModel.getDataModel());
		getExprCalcEnv().setExEnv(CommonExprCalcEnv.EXPR_EXEC_TIMEZONE, anaModel.getContext().getAttribute(FreeReportContextKey.REPORT_EXEC_TIMEZONE));
	}

	@Override
	protected AreaExprCalcEnv initAreaExprCalcEnv() {
		AreaExprCalcEnv env = super.initAreaExprCalcEnv();
		env.registerFuncDriver(new AreaFuncDriver(anaModel));
		env.registerFuncDriver(new CVSBatchFuncDriver(anaModel));
		// 增加参数公式,字段取值公式  add by yuyangi
		env.registerFuncDriver(new GetFuncDriver(anaModel));
		env.registerFuncDriver(new VarFuncDriver(anaModel));
		env.registerFuncDriver(new NcDataFormatFuncDriver(anaModel));
		//添加薪资解密函数  add by ward 2018-06-05
		env.registerFuncDriver(new SalaryDecryptFuncDriver(anaModel));
		env.registerFuncDriver(new GetAreaFieldFuncDriver(anaModel));
		//add by guohch 20130422
		ExcelStatCalcUtil.registExcelFuncDriver(env);
		// @edit by yuyangi at 2012-6-12,下午07:41:02 增加0/0忽略标记，0/0将没有提示
		env.setExEnv(CommonExprCalcEnv.EX_IS_CHECK, Boolean.TRUE);
		return env;
	}

	/**
	 * @update by yuyangi 修改成类变量，在计算时可以使用同一个实例
	 * @return 
	 */
	protected ITableData getDataChannel() {
		if(m_AreaDataChannel == null) {
			m_AreaDataChannel = new BIAreaDataChannel(getCellsModel());
		}
		return m_AreaDataChannel;
	}

	protected void setFormulaCmdLet(IArea area, FormulaVO fvo) {
		if (!m_area.contain(area))
			return;
		super.setFormulaCmdLet(area, fvo);
	}

	/**
	 * 功能：添加给定公式所在的区域和引用区域列表,将其加入到公式链中。
	 *
	 * @param area
	 * @param vect
	 */
	protected void addFormulaToAreaFormulaList(IArea area, Vector<IArea> vect) {
		if (vect == null || m_areaFormulaList == null)
			return;
		if (!m_area.contain(area))
			return;

		List<IArea> newList = m_areaFormulaList.canAddToListNew(area, vect);
		if (newList != null && newList.size() > 0) {
			m_areaFormulaList.addFormula(area, newList);
		}
	}

	// 以下方法由liuchun添加，公式链构建的重新实现

    public void calcAllFormulaByOrder() throws CmdException {

		// 取得所有公式
		mainFmlHash = getFormulaModel().getMainFmls();
		if(mainFmlHash == null || mainFmlHash.isEmpty()) {
			return;
		}

		// 构建公式链
		setupMainCellFmlList();
		if (mainCellFmlList == null)
			return;

		// 根据公式链决定计算顺序
		Vector<IArea> areaList = new Vector<IArea>();
		for (Vector<IArea> vec : mainCellFmlList) {
			if (vec != null) {
				areaList.addAll(vec);
			}
		}

		// 根据特定计算顺序取相应公式
		ArrayList<UfoCmdLet> list = new ArrayList<UfoCmdLet>();
		for (IArea fmlArea : areaList) {
			FormulaVO oFormula = mainFmlHash.get(fmlArea);
			list.add((UfoCmdLet) oFormula.getLet());
		}

		// 执行计算
		UfoCmdLet[] objLets = list.toArray(new UfoCmdLet[0]);
		try {
			CmdProxy.preCalcExtFunc(objLets, null, getExprCalcEnv(), 3);
		} catch (Exception e) {
			AppDebug.debug(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-0843")/*@res "预批量计算错误"*/, e);
		}

		calcAllFormula(objLets);
	}

    private void calcAllFormula(UfoCmdLet[] objLets) {
    	if(objLets == null || objLets.length <= 0) {
    		return;
    	}
    	for(UfoCmdLet let : objLets) {
    		try {
    			let.exec(getCmdExecutor().getEnv());
    		} catch (CmdException e) {
    			AppDebug.error(e.getMessage());
    		}
    	}
	}

	private void setupMainCellFmlList() {
		// 初始化公式链，公式链置为空集
		if (getMainCellFormList() == null) {
			mainCellFmlList = new Vector<Vector<IArea>>();
		} else {
			getMainCellFormList().clear();
		}

		// 设置所有公式的层次级别
		initMainFormulaLevel(mainFmlHash, true);

		// 构建有层次结构的公式链
		setupGradeFmlList(mainFmlHash, mainCellFmlList);
	}

	private void setupGradeFmlList(Map<IArea, FormulaVO> mapMainFormula,
			Vector<Vector<IArea>> mainCellFmlList) {
		Iterator<Map.Entry<IArea, FormulaVO>> iter = mapMainFormula.entrySet()
				.iterator();
		IArea areaKey = null;
		FormulaVO fVO = null;
		while (iter.hasNext()) {
			Map.Entry<IArea, FormulaVO> entry = iter.next();
			areaKey = entry.getKey();
			fVO = entry.getValue();
			fVO.setChecked(false);

			int level = fVO.getFmlLevel();
			if (mainCellFmlList.size() < level + 1) {
				mainCellFmlList.setSize(level + 1);
			}
			Vector<IArea> areaVector = mainCellFmlList.get(level);
			if (areaVector == null) {
				areaVector = new Vector<IArea>();
				mainCellFmlList.setElementAt(areaVector, level);
			}
			areaVector.add(areaKey);
		}
	}

	private void initMainFormulaLevel(Map<IArea, FormulaVO> mapMainFormula,
			boolean isInstantFml) {
		if (mapMainFormula == null || mapMainFormula.size() == 0)
			return;

		// 初始化所有公式在公式链中的层次
		initformulaListLevel(mapMainFormula, false, isInstantFml);
	}

	private void initformulaListLevel(Map<IArea, FormulaVO> mapFormula,
			boolean isDynArea, boolean isInstantFml)
	{
		if (mapFormula == null || mapFormula.size() == 0)
			return;

		// 对所有公式，遍历所有公式，建立公式链表
		Iterator<Map.Entry<IArea, FormulaVO>> iter = mapFormula.entrySet().iterator();
		// 用于校验循环公式，存储格式态的位置
		Vector<String> areaVec = new Vector<String>();
		IArea areaKey = null;
		FormulaVO fVO = null;

		while (iter.hasNext()) {
			Map.Entry<IArea, FormulaVO> entry = iter.next();
			areaKey = entry.getKey();
			fVO = entry.getValue();

			// 公式区域不在扩展子区域内
			if (!m_area.contain(areaKey)){
				continue;
			}

			try {
				// 如果公式解析为空，则解析公式,并设置公式解析结果
				if (fVO.getLet() == null) {
					IParsed parsedLet = (UfoCmdLet) (getCmdExecutor().parseFormula(areaKey, fVO.getContent()));
					fVO.setLet(parsedLet);
				}
			} catch (Exception e) {
				AppDebug.debug(fVO.getContent() + " is wrong", e);
			}
		}

		iter = mapFormula.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<IArea, FormulaVO> entry = iter.next();
			areaKey = entry.getKey();
			fVO = entry.getValue();

			// 公式区域不在扩展子区域内
			if (!m_area.contain(areaKey)){
				continue;
			}

			if (!fVO.isChecked()){
				int level = getFormulaLevel(areaKey, fVO, mapFormula, isDynArea, areaVec, isInstantFml);
				fVO.setFmlLevel(level);
			}
		}

	}

	private int getFormulaLevel(IArea areaKey, FormulaVO fVO,
			Map<IArea, FormulaVO> mapFormula, boolean isDynArea, Vector<String> areaVec, boolean isInstantFml)
	{
		String content = fVO.getContent();
		fVO.setChecked(true);
		if (content == null || content.length() == 0) {
			fVO.setErrorFml(false);
			return 0;
		}
		boolean isCorrect = true;
		int level = 0;
		try {
			// 将该位置加入到vector中，用于判断循环
			addAreaIntoVector(areaVec, areaKey);

			IParsed parsedLet = fVO.getLet();
			if (parsedLet == null) {
				parsedLet = (UfoCmdLet) (getCmdExecutor().parseFormula(areaKey, fVO.getContent()));
			}
			if (isCorrect == true) {
				level = getFormulaLevel(parsedLet, fVO, mapFormula, isDynArea, areaVec, areaKey, isInstantFml);
				fVO.setFmlLevel(level);
			}

			// 退出之后，将位置信息清理
			removeAreaFromVector(areaVec, areaKey);
		} catch (Exception e) {
			AppDebug.debug(fVO.getContent() + " is wrong", e);
		}

		return level;
	}

	@SuppressWarnings("unchecked")
	private int getFormulaLevel(IParsed parsedLet, FormulaVO fVO,
			Map<IArea, FormulaVO> mapFormula, boolean isDynArea, Vector<String> areaVec,IArea areaKey, boolean isInstantFml)
	{
		Vector<IArea> alist = AreaFormulaUtil.getAreaFuncRefArea(areaKey, parsedLet);
		if (alist == null || alist.isEmpty()) {
			return 0;
		}
		int level = 0;
		// 循环引用、自引用校验
		for (IArea area : alist) {
			if (area == null) {
				continue;
			}
			CellPosition[] cells = null;
			if (area instanceof CellPosition) {
				cells = new CellPosition[] { (CellPosition) area };
			} else {
				cells = area.split();
			}
			if (cells != null && cells.length > 0) {
				for (CellPosition cell : cells) {
					FormulaVO formula = getFormulaByArea(cell, isInstantFml);
					if(areaVec.contains(cell.toString())){//存在循环引用
						fVO.setErrorFml(true);
					}
					if (formula == null) {
						continue;
					}
					if (formula.isChecked()) {
						int tempLevel = formula.getFmlLevel();
						if (tempLevel > level)
						{
							level = tempLevel;
						}
					} else {
						int tempLevel = getFormulaLevel(cell, formula,
								mapFormula, isDynArea, areaVec, isInstantFml);
						if (tempLevel > level) {
							level = tempLevel;
						}
					}
				}
			}
		}

		return level + 1;
	}

	private FormulaVO getFormulaByArea(CellPosition selPos, boolean isInstantFml)
	{
		// 指定的单元格返回CellPosition类型，如果选择的是组合单元，则返回正确的选择区域 而
		IArea selArea = anaModel.getDataModel().getArea(selPos);
		IArea realFmlArea = getFormulaModel().getRelatedFmlArea(selArea);
		if (realFmlArea == null) {
			return null;
		}

		FormulaVO formulaVO = mainFmlHash.get(realFmlArea);
		return formulaVO;
	}

	private void addAreaIntoVector(Vector<String> areaVec,IArea area){
		if(area != null){
			CellPosition[] cells = area.split();
			for(CellPosition cell : cells){
				areaVec.add(cell.toString());
			}
		}
	}

	private void removeAreaFromVector(Vector<String> areaVec,IArea area){
		if(area != null){
			CellPosition[] cells = area.split();
			for(CellPosition cell : cells){
				if(areaVec.contains(cell.toString())){
					int index = areaVec.lastIndexOf(cell.toString());
					if(index >= 0){
						areaVec.remove(index);
					}
				}
			}
		}
	}

	private Vector<Vector<IArea>> getMainCellFormList()
	{
		return mainCellFmlList;
	}

	public AreaFormulaModel getFormulaModel() {
		AreaFormulaModel formulaModel = AreaFormulaModel.getInstance(anaModel.getDataModel());
		return formulaModel;
	}
}