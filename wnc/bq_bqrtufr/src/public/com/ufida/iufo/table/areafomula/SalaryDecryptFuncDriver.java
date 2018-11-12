package com.ufida.iufo.table.areafomula;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.vo.pub.lang.UFDouble;

import com.ufida.report.anareport.model.AbsAnaReportModel;
import com.ufsoft.iufo.util.parser.IFuncType;
import com.ufsoft.iufo.util.parser.UfoParseException;
import com.ufsoft.iufo.util.parser.UfoSimpleObject;
import com.ufsoft.script.base.AbstractParser;
import com.ufsoft.script.base.ICalcEnv;
import com.ufsoft.script.base.IOprParser;
import com.ufsoft.script.base.IUfoTokenConsts;
import com.ufsoft.script.base.ParseResultStatus;
import com.ufsoft.script.base.Token;
import com.ufsoft.script.base.UfoTokenMgr;
import com.ufsoft.script.datachannel.ITableData;
import com.ufsoft.script.exception.CmdException;
import com.ufsoft.script.exception.ParseException;
import com.ufsoft.script.expression.AreaExprCalcEnv;
import com.ufsoft.script.expression.UfoAreaOprParser;
import com.ufsoft.script.expression.UfoExpr;
import com.ufsoft.script.expression.UfoExprParser;
import com.ufsoft.script.expression.UfoFullArea;
import com.ufsoft.script.function.ExtFunc;
import com.ufsoft.script.function.IUFO2FuncDriver;
import com.ufsoft.script.function.IUfo2BatchCalcFunc;
import com.ufsoft.script.function.IUfoFuncParamParser;
import com.ufsoft.script.function.UfoFuncInfo;
import com.ufsoft.script.function.UfoFuncList;
import com.ufsoft.table.CellPosition;

/**
 * 
 * @author ward
 * @date 2018-06-05
 * @功能描述：薪资解密函数Driver
 * 
 */
public class SalaryDecryptFuncDriver implements IUFO2FuncDriver, IUfoFuncParamParser, IUfo2BatchCalcFunc {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9125268698484929592L;

	private static final String SALARYDECRYPT = "SALARYDECRYPT";

	/**
	 * 函数类型
	 */
	private static final byte DATAFORMATFUNC = 1;
	/**
	 * 区域参数名称
	 */
	private static final String AREA_PARAM_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0",
			"01413006-0809")/* @res "区域" */;

	/**
	 * 函数模块名称
	 */
	private static final String MODULE_NAME = "薪資函數";

	/**
	 * 分析模型
	 */
	private AbsAnaReportModel anaModel;

	private java.util.Hashtable<String, BatchFuncInfo> m_funcValues = null;

	public static final UfoFuncInfo[] FUNCLIST = { new UfoFuncInfo(SALARYDECRYPT, DATAFORMATFUNC, new String[] {
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0", "01413006-0477")/*
																								 * @
																								 * res
																								 * "字段名"
																								 */, AREA_PARAM_NAME },
			new byte[] { UfoFuncList.STRING, UfoFuncList.AREA }, (byte) IFuncType.STRING, "將加密過後的薪資數據進行解密",
			new String[] { "com.ufida.report.free.plugin.formula.AnaFormulaSmartModelRefProcessor", "" }) };

	public SalaryDecryptFuncDriver(AbsAnaReportModel anaModel) {
		this.anaModel = anaModel;
	}

	public SalaryDecryptFuncDriver() {
	}

	public void setAnaModel(AbsAnaReportModel anaModel) {
		this.anaModel = anaModel;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ExtFunc createExtFunc(short funcID, String strFuncName, ArrayList alParaList, String strDriverName,
			byte rtnType, ICalcEnv objEnv, boolean userDef) throws UfoParseException {
		try {
			if (strFuncName != null) {
				if (strFuncName.equalsIgnoreCase(SALARYDECRYPT)) {
					return new SalaryDecryptFunc(UfoFuncList.EXTFUNCID, strFuncName, alParaList, strDriverName,
							rtnType, anaModel);
				}
			}
		} catch (CmdException e) {
			throw new UfoParseException(e);
		}
		throw new UfoParseException(UfoParseException.ERR_EXTFUNCCREATE, 0);
	}

	public UfoFuncInfo[] getFuncList() {
		return FUNCLIST;
	}

	public String[] getCategoryList() {
		return new String[] { MODULE_NAME };
	}

	@Override
	public Object callFunc(String arg0, String arg1) {

		return null;
	}

	@Override
	public String checkFunc(String arg0, String arg1) throws UfoParseException {

		return null;
	}

	@Override
	public String doFuncRefer(String arg0) {

		return null;
	}

	@Override
	public boolean getDefaultParaFlag() {

		return false;
	}

	@Override
	public int getFuncCount(int modulePos) {
		int nCount = 0;
		if (modulePos > 0) {
			UfoFuncInfo[] flist = getFuncList();
			if (flist != null && flist.length > 0) {
				for (int i = 0; i < flist.length; i++) {
					if (flist[i].getFuncType() == modulePos) {
						nCount++;
					}
				}
			}
		}
		return nCount;
	}

	@Override
	public String getFuncDesc(String strFuncName) {
		UfoFuncInfo objFuncInfo = getFuncInfo(strFuncName);
		if (objFuncInfo != null) {
			return objFuncInfo.getFuncDescription();
		}
		return null;
	}

	@Override
	public String getFuncForm(String strFuncName) {
		UfoFuncInfo objFuncInfo = getFuncInfo(strFuncName);
		if (objFuncInfo != null) {
			return objFuncInfo.getFuncFormat();
		}
		return null;
	}

	@Override
	public UfoSimpleObject[] getFuncList(int iModulePos) {
		int nLen = getFuncCount(iModulePos);
		if (nLen == 0) {
			return null;
		}
		UfoSimpleObject[] objRet = new UfoSimpleObject[nLen];
		UfoFuncInfo[] flist = getFuncList();
		int nCount = 0;
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].getFuncType() == iModulePos) {
				objRet[nCount++] = new UfoSimpleObject(nCount, flist[i].getFuncName());
			}
		}
		return objRet;
	}

	@Override
	public String getFuncName(int iModulePos, int iFuncPos, String[] strChName) {
		if (iModulePos < 1) {
			return null;
		}

		// 在函数列表中查找
		UfoFuncInfo[] flist = getFuncList();
		if (flist != null) {
			int nCount = 0;
			for (int i = 0; i < flist.length; i++) {
				if (flist[i].getFuncType() == iModulePos) {
					nCount++;
					if (nCount == iFuncPos) {
						return flist[i].getFuncName();
					}
				}
			}
		}

		return null;
	}

	@Override
	public byte getFuncType(String strFuncName) {
		UfoFuncInfo objFuncInfo = getFuncInfo(strFuncName);
		if (objFuncInfo != null) {
			return objFuncInfo.getReturnType();
		}
		return IFuncType.VALUE;
	}

	@Override
	public int getModuleCount() {
		return 1;
	}

	@Override
	public String getModuleDesc(int pos) {
		String[] strCatList = getCategoryList();
		if (strCatList != null && pos <= strCatList.length) {
			return strCatList[pos - 1];
		}
		return null;
	}

	@Override
	public UfoSimpleObject[] getModuleList() {
		String[] strCatList = getCategoryList();
		if (strCatList != null) {
			UfoSimpleObject[] objRet = new UfoSimpleObject[strCatList.length];
			for (int i = 0; i < strCatList.length; i++) {
				objRet[i] = new UfoSimpleObject(i + 1, strCatList[i]);
			}
			return objRet;
		}
		return null;
	}

	@Override
	public int getVersion() {

		return 0;
	}

	@Override
	public boolean hasReferDlg(String arg0) {

		return false;
	}

	@Override
	public boolean isModuleFunc(String strFuncName) {
		return getFuncInfo(strFuncName) != null;
	}

	@Override
	public String setCalEnv(String[] arg0) {

		return null;
	}

	@Override
	public boolean setDefaultPara() {

		return false;
	}

	@Override
	public Object parseIufoParam(String strParam, byte paramType, boolean userDef, int pos, ICalcEnv objEnv)
			throws UfoParseException {
		return null;
	}

	@Override
	public Object callFunc(String strFuncName, Object[] objParams, ICalcEnv env) throws CmdException {
		// TODO
		return null;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public ArrayList checkFunc(String strFuncName, String strParam, ICalcEnv objEnv, boolean userDef, int beginPos)
			throws ParseException {
		if (objEnv == null || !(objEnv instanceof AreaExprCalcEnv)) {
			throw new UfoParseException(UfoParseException.ERR_MEASENV, beginPos);
		}
		String msg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "miufocalc000515", null,
				new String[] { strFuncName, strParam });
		throw new UfoParseException(msg, beginPos);
	}

	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	private ArrayList parseParamList(String strParam, boolean bUserDef, ICalcEnv objEnv, byte[] ptypes, int nBeginPos)
			throws ParseException {
		java.util.ArrayList plist;
		int m, i;
		if (ptypes != null) { // 准备参数列表
			m = ptypes.length;
			plist = new java.util.ArrayList(m);
		} else {
			m = 0;
			plist = null;
		}
		byte flag = 0; // 设置参数省略标记
		UfoTokenMgr objTokenMgr = new UfoTokenMgr(strParam);
		Token objToken = objTokenMgr.getToken();
		for (i = 1; i <= m; i++) {
			short nTokenID = objToken.getKind();
			byte paratype = ptypes[i - 1]; // 得到参数类型
			if ((paratype & 0x80) != 0) { // 参数可以缺省
				if (nTokenID == IUfoTokenConsts.TKN_COMMA) { // 参数被省略
					plist.add(null);
					objToken = objTokenMgr.getToken();
					flag = 1; // 至少后面应有一个参数不能被省略
					continue;
				} else if (nTokenID == IUfoTokenConsts.TKN_INPUTEND) { // 函数结束
					break;
				}
			}
			plist.add(parseParam(objTokenMgr, objToken, bUserDef, objEnv, (byte) (paratype & 0x7f), nBeginPos));
			if (flag == 1) {
				flag = 0; // 清除标记
			}
			objToken = objTokenMgr.getToken();
			if (i < m) {
				if (objToken.getKind() == IUfoTokenConsts.TKN_INPUTEND
						|| objToken.getKind() == IUfoTokenConsts.TKN_RPARAM) { // 函数结束
					i++;
					break;
				}
				// 匹配","
				AbstractParser.matchToken(objToken.getKind(), IUfoTokenConsts.TKN_COMMA, objToken.getPos());
				objToken = objTokenMgr.getToken();
			}
		}
		// 2.检查参数完整性
		if (flag != 0) {
			AbstractParser.genErr(UfoParseException.ERR_PARA, objToken.getPos());
		}
		while (i <= m) { // 对省略的参数
			if ((ptypes[i - 1] & 0x80) != 0) {
				plist.add(null);
				i++;
			} else {
				AbstractParser.genErr(UfoParseException.ERR_PARA_TYPE, objToken.getPos());
			}
		}
		AbstractParser.matchToken(objToken.getKind(), IUfoTokenConsts.TKN_INPUTEND, objToken.getPos());
		return plist;
	}

	@Override
	public UfoFuncInfo getFuncInfo(String strFuncName) {
		UfoFuncInfo[] flist = getFuncList();
		for (UfoFuncInfo info : flist) {
			if (info.getFuncName().equalsIgnoreCase(strFuncName)) {
				return info;
			}
		}
		return null;
	}

	@Override
	public boolean isCheckEnable(String arg0) {

		return false;
	}

	/**
	 * 功 能：根据提供的参数类型分析函数的参数。并预读下一个词串。
	 */
	private Object parseParam(UfoTokenMgr objTokenMgr, Token objToken, boolean bUserDefined, ICalcEnv objEnv,
			byte ptype, int nBeginPos) throws ParseException {
		// 参数在整个公式串中的位置
		int iParamPos = objToken.getPos() + nBeginPos;
		UfoExprParser objExprParser = (UfoExprParser) objEnv.getOprParserByName(UfoExprParser.class.getName());
		UfoExpr objExpr = null;
		ParseResultStatus objStatus = null;
		objStatus = new ParseResultStatus();
		switch (ptype) {
		case UfoFuncList.BOOL:
			objExpr = (UfoExpr) objExprParser.parseOperand(objToken, objTokenMgr, objStatus, bUserDefined, objEnv);
			return objExpr;
		case UfoFuncList.STRING:
		case UfoFuncList.FLOAT:
		case UfoFuncList.INT:
		case UfoFuncList.VALUE: // 匹配表达式
			objStatus = new ParseResultStatus();
			objStatus.setDimens(0);
			objExpr = objExprParser.parseMathExprOperand(objToken, objTokenMgr, objStatus, bUserDefined, objEnv);
			return objExpr;
		case UfoFuncList.PURESTRING:
			if (objToken.getKind() != UfoTokenMgr.TKN_STRING) {
				AbstractParser.genErr(UfoParseException.ERR_PARA_TYPE, iParamPos);
			}
			String name1 = objToken.getImage();
			if (name1 == null || (name1 = name1.trim()).length() == 0) {
				AbstractParser.genErr(UfoParseException.ERR_PARA, iParamPos);
			}
			return name1;
			// 2002-5-16修改结束
		case UfoFuncList.AREA:
			return genFullArea(objTokenMgr, objToken, bUserDefined, objEnv, objStatus);
		default:
			AbstractParser.genErr(UfoParseException.ERR_PARA_TYPE, iParamPos);
		}
		return null;

	}

	private UfoFullArea genFullArea(UfoTokenMgr objTokenMgr, Token objToken, boolean bUserDefined, ICalcEnv objEnv,
			ParseResultStatus objStatus) throws ParseException {
		UfoFullArea fullArea = null;
		IOprParser objAreaOprParser = objEnv.getOprParserByName(UfoAreaOprParser.class.getName());
		;
		fullArea = (UfoFullArea) objAreaOprParser.parseOperand(objToken, objTokenMgr, objStatus, bUserDefined, objEnv);
		if (!fullArea.getArea().isCell()) {
			throw new UfoParseException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0",
					"01413006-0823")/* @res "区域参数必须为单个单元" */);
		}
		if (fullArea.hasReport()) {
			throw new UfoParseException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0",
					"01413006-0824")/* @res "区域不允许指定报表编码" */);
		}
		return fullArea;
	}

	@Override
	public String calcFuncValues(ExtFunc[] objExtFuncs, ICalcEnv objEnv) throws CmdException {

		int expressLen = objExtFuncs.length;
		Map<String, String> cvsInParam = new HashMap<String, String>();
		List<String> varParam = new ArrayList<String>();
		for (int i = 0; i < expressLen; i++) {
			ExtFunc objFunc = objExtFuncs[i];
			if (!(objFunc instanceof SalaryDecryptFunc))
				continue;
			SalaryDecryptFunc func = (SalaryDecryptFunc) objFunc;
			boolean hasCvsInCvs = hasCvsInCvs(func, objEnv, cvsInParam, varParam);
			BatchFuncInfo info = getNCFuncInfo(func, objEnv);
			// 加入函数列表
			CellPosition[] poses = func.getCellArea();
			Object[] varrs = new Object[poses.length][1];
			if (!hasCvsInCvs) {
				varrs = func.getParmas(objEnv);
			}

			info.addOneFormulaByArea(poses, varrs);

		}

		String[] forms = m_funcValues.keySet().toArray(new String[0]);
		forms = resetFuncExpr(forms, cvsInParam);
		Object[][] datas = new Object[forms.length][];
		for (String key : forms) {
			int j = 0;
			BatchFuncInfo info = m_funcValues.get(key);
			Object[] varrs = info.getVarrs(0);
			Object[] result = new Object[varrs.length];
			for (int i = 0; i < varrs.length; i++) {
				UFDouble flag = varrs[i] != null ? new UFDouble(String.valueOf(varrs[i])) : UFDouble.ZERO_DBL;
				result[i] = SalaryDecryptUtil.decrypt(flag.toDouble());
			}
			datas[j] = result;
			j++;
		}

		// 结果的获取和保存
		if (datas == null || datas.length == 0) {
			return null;
		}
		for (int i = 0; i < datas.length; i++) {
			Object[] oneData = datas[i];
			BatchFuncInfo info = m_funcValues.get(forms[i]);
			info.setResults(oneData);
		}

		BIAreaDataChannel biData = null;
		if (objEnv instanceof AreaExprCalcEnv) {
			ITableData dataChannel = ((AreaExprCalcEnv) objEnv).getDataChannel();
			if (dataChannel instanceof BIAreaDataChannel) {
				biData = (BIAreaDataChannel) dataChannel;
				biData.setBatchFuncValues(m_funcValues);
			}
		}
		return null;
	}

	private String[] resetFuncExpr(String[] oldFuncs, Map<String, String> funcParams) {
		if (funcParams.isEmpty()) {
			return oldFuncs;
		}
		for (int i = 0; i < oldFuncs.length; i++) {
			String func = oldFuncs[i];
			func = func.substring(func.indexOf("->") + 2);
			String paramExpr = funcParams.get(func);
			if (paramExpr != null) {
				String exprs = paramExpr.substring(paramExpr.indexOf("->") + 2);
				int index = getfuncIndex(oldFuncs, exprs);
				if (index >= 0) {
					getFuncValues().put(paramExpr, getFuncValues().get(oldFuncs[index]));
					getFuncValues().remove(oldFuncs[index]);
					oldFuncs[index] = paramExpr;
				}
			}
		}
		return oldFuncs;
	}

	private int getfuncIndex(String[] funcs, String func) {
		for (int i = 0; i < funcs.length; i++) {
			if (func.equals(funcs[i])) {
				return i;
			}
		}
		return -1;
	}

	private boolean hasCvsInCvs(SalaryDecryptFunc func, ICalcEnv objEnv, Map<String, String> cvsInParam,
			List<String> varParam) {
		@SuppressWarnings("unchecked")
		SalaryDecryptFunc paraFunc = getCvsFunc(func.getFieldParam(func.getParams()));
		if (paraFunc != null) {
			String expr = func.getExpress(objEnv);
			String paramIds = func.getFieldHashCodeNames();
			if (!cvsInParam.containsKey(paramIds)) {
				cvsInParam.put(expr, paramIds + "->" + paraFunc.getExpress(objEnv));
				varParam.add(paramIds);
				return true;
			}
		}
		return false;
	}

	private SalaryDecryptFunc getCvsFunc(Object funcObj) {
		if (funcObj instanceof UfoExpr) {
			UfoExpr expr = (UfoExpr) funcObj;
			if (expr.getElementLength() == 1 && expr.getElements()[0].getObj() instanceof SalaryDecryptFunc)
				return (SalaryDecryptFunc) expr.getElements()[0].getObj();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ufsoft.script.function.IUfo2BatchCalcFunc#clearPreCalcData()
	 */
	@Override
	public void clearPreCalcData() {
		getFuncValues().clear();
	}

	private BatchFuncInfo getNCFuncInfo(SalaryDecryptFunc func, ICalcEnv objEnv) {
		String express = func.getExpress(objEnv);
		if (!getFuncValues().containsKey(express)) {
			BatchFuncInfo info = new BatchFuncInfo();
			info.setIDNames(new String[] { func.getFieldHashCodeNames() });
			m_funcValues.put(express, info);
		}
		return m_funcValues.get(express);
	}

	private Hashtable<String, BatchFuncInfo> getFuncValues() {
		if (m_funcValues == null)
			m_funcValues = new java.util.Hashtable<String, BatchFuncInfo>();
		return m_funcValues;
	}

}
