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
 * @update liuchun at 2011-04-28 ���ɱ�����չʱ���Թ�ʽ���м��㹫ʽ������������ʵ��
 *
 * @author wanyonga
 * @created at 2010-7-12,����09:34:32
 *
 */
public class FreeAreaFormulaCalUtil extends AreaFormulaCalUtil {

	// ����Ԫ��ʽ������
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
		// ���Ӳ�����ʽ,�ֶ�ȡֵ��ʽ  add by yuyangi
		env.registerFuncDriver(new GetFuncDriver(anaModel));
		env.registerFuncDriver(new VarFuncDriver(anaModel));
		env.registerFuncDriver(new NcDataFormatFuncDriver(anaModel));
		//���н�ʽ��ܺ���  add by ward 2018-06-05
		env.registerFuncDriver(new SalaryDecryptFuncDriver(anaModel));
		env.registerFuncDriver(new GetAreaFieldFuncDriver(anaModel));
		//add by guohch 20130422
		ExcelStatCalcUtil.registExcelFuncDriver(env);
		// @edit by yuyangi at 2012-6-12,����07:41:02 ����0/0���Ա�ǣ�0/0��û����ʾ
		env.setExEnv(CommonExprCalcEnv.EX_IS_CHECK, Boolean.TRUE);
		return env;
	}

	/**
	 * @update by yuyangi �޸ĳ���������ڼ���ʱ����ʹ��ͬһ��ʵ��
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
	 * ���ܣ���Ӹ�����ʽ���ڵ���������������б�,������뵽��ʽ���С�
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

	// ���·�����liuchun��ӣ���ʽ������������ʵ��

    public void calcAllFormulaByOrder() throws CmdException {

		// ȡ�����й�ʽ
		mainFmlHash = getFormulaModel().getMainFmls();
		if(mainFmlHash == null || mainFmlHash.isEmpty()) {
			return;
		}

		// ������ʽ��
		setupMainCellFmlList();
		if (mainCellFmlList == null)
			return;

		// ���ݹ�ʽ����������˳��
		Vector<IArea> areaList = new Vector<IArea>();
		for (Vector<IArea> vec : mainCellFmlList) {
			if (vec != null) {
				areaList.addAll(vec);
			}
		}

		// �����ض�����˳��ȡ��Ӧ��ʽ
		ArrayList<UfoCmdLet> list = new ArrayList<UfoCmdLet>();
		for (IArea fmlArea : areaList) {
			FormulaVO oFormula = mainFmlHash.get(fmlArea);
			list.add((UfoCmdLet) oFormula.getLet());
		}

		// ִ�м���
		UfoCmdLet[] objLets = list.toArray(new UfoCmdLet[0]);
		try {
			CmdProxy.preCalcExtFunc(objLets, null, getExprCalcEnv(), 3);
		} catch (Exception e) {
			AppDebug.debug(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-0843")/*@res "Ԥ�����������"*/, e);
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
		// ��ʼ����ʽ������ʽ����Ϊ�ռ�
		if (getMainCellFormList() == null) {
			mainCellFmlList = new Vector<Vector<IArea>>();
		} else {
			getMainCellFormList().clear();
		}

		// �������й�ʽ�Ĳ�μ���
		initMainFormulaLevel(mainFmlHash, true);

		// �����в�νṹ�Ĺ�ʽ��
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

		// ��ʼ�����й�ʽ�ڹ�ʽ���еĲ��
		initformulaListLevel(mapMainFormula, false, isInstantFml);
	}

	private void initformulaListLevel(Map<IArea, FormulaVO> mapFormula,
			boolean isDynArea, boolean isInstantFml)
	{
		if (mapFormula == null || mapFormula.size() == 0)
			return;

		// �����й�ʽ���������й�ʽ��������ʽ����
		Iterator<Map.Entry<IArea, FormulaVO>> iter = mapFormula.entrySet().iterator();
		// ����У��ѭ����ʽ���洢��ʽ̬��λ��
		Vector<String> areaVec = new Vector<String>();
		IArea areaKey = null;
		FormulaVO fVO = null;

		while (iter.hasNext()) {
			Map.Entry<IArea, FormulaVO> entry = iter.next();
			areaKey = entry.getKey();
			fVO = entry.getValue();

			// ��ʽ��������չ��������
			if (!m_area.contain(areaKey)){
				continue;
			}

			try {
				// �����ʽ����Ϊ�գ��������ʽ,�����ù�ʽ�������
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

			// ��ʽ��������չ��������
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
			// ����λ�ü��뵽vector�У������ж�ѭ��
			addAreaIntoVector(areaVec, areaKey);

			IParsed parsedLet = fVO.getLet();
			if (parsedLet == null) {
				parsedLet = (UfoCmdLet) (getCmdExecutor().parseFormula(areaKey, fVO.getContent()));
			}
			if (isCorrect == true) {
				level = getFormulaLevel(parsedLet, fVO, mapFormula, isDynArea, areaVec, areaKey, isInstantFml);
				fVO.setFmlLevel(level);
			}

			// �˳�֮�󣬽�λ����Ϣ����
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
		// ѭ�����á�������У��
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
					if(areaVec.contains(cell.toString())){//����ѭ������
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
		// ָ���ĵ�Ԫ�񷵻�CellPosition���ͣ����ѡ�������ϵ�Ԫ���򷵻���ȷ��ѡ������ ��
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