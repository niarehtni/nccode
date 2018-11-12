package com.ufida.iufo.table.areafomula;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pub.smart.metadata.DataTypeConstant;
import nc.pub.smart.metadata.Field;
import nc.pub.smart.model.SmartModel;
import nc.vo.pub.lang.UFDouble;

import com.ufida.iufo.pub.tools.AppDebug;
import com.ufida.iufo.table.exarea.ExtendAreaCell;
import com.ufida.iufo.table.exarea.ExtendAreaConstants;
import com.ufida.iufo.table.exarea.ExtendAreaModel;
import com.ufida.report.anareport.data.GroupDataIndex;
import com.ufida.report.anareport.expand.AreaPrivateMap;
import com.ufida.report.anareport.model.AbsAnaReportModel;
import com.ufida.report.anareport.model.AnaRepField;
import com.ufida.report.anareport.util.AreaGroupDSUtil;
import com.ufsoft.script.base.ICalcEnv;
import com.ufsoft.script.base.UfoVal;
import com.ufsoft.script.datachannel.ITableData;
import com.ufsoft.script.exception.CmdException;
import com.ufsoft.script.expression.AreaExprCalcEnv;
import com.ufsoft.script.expression.UfoExpr;
import com.ufsoft.script.expression.UfoFullArea;
import com.ufsoft.script.function.ExtFunc;
import com.ufsoft.script.function.IAbsFunc;
import com.ufsoft.table.Cell;
import com.ufsoft.table.CellPosition;
import com.ufsoft.table.ExtDataModel;
import com.ufsoft.table.ICellValue;

/**
 * 
 * @author ward
 * @date 2018-06-05
 * @功能描述：薪资解密函数[SALARYDECRYPT]
 * 
 */
public class SalaryDecryptFunc extends ExtFunc implements IAbsFunc {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4166207135516678517L;

	protected static final String PARAM_FIELD = "paranamexpres";
	/**
	 * 分析模型
	 */
	private AbsAnaReportModel anaModel;

	@SuppressWarnings("rawtypes")
	public SalaryDecryptFunc(short nFuncID, String strFuncName,
			java.util.ArrayList params, String strDriver, byte bRtnType,
			AbsAnaReportModel anaModel)
			throws com.ufsoft.script.exception.UfoCmdException {
		super(nFuncID, strFuncName, params, strDriver, bRtnType);
		this.anaModel = anaModel;
	}

	protected Object getFieldParam(ArrayList<Object> param) {
		return param.get(0);
	}

	protected String getNCFuncName() {
		return "SALARYDECRYPT";
	}

	@SuppressWarnings("rawtypes")
	protected String getExpress(ICalcEnv env) {
		ArrayList param = this.getParams();
		if (param.size() <= 0)
			return null;
		String paramExprs = getFieldHashCodeNames();
		String express = getNCFuncName() + "(" + paramExprs + ")";
		return express;
	}

	@SuppressWarnings({ "rawtypes" })
	protected CellPosition getCellPosition() {
		ArrayList param = this.getParams();
		if (param.size() <= 0)
			return null;
		UfoFullArea fullarea = (UfoFullArea) param.get(1);
		CellPosition pos = fullarea.getArea().getStart();
		return pos;
	}

	@SuppressWarnings("unchecked")
	protected CellPosition[] getCellArea() {
		ArrayList<Object> param = this.getParams();
		if (param.size() <= 0)
			return null;
		UfoFullArea fullarea = (UfoFullArea) param.get(1);
		return fullarea.getArea().split();
	}

	@SuppressWarnings("rawtypes")
	protected String getIDFieldName(ICalcEnv env) {
		ArrayList param = this.getParams();
		if (param.size() <= 0)
			return null;
		Object p4Obj = param.get(0);
		String p4 = null;
		if (p4Obj != null && p4Obj instanceof UfoExpr) {
			try {
				UfoVal[] val = ((UfoExpr) p4Obj).getValue(env);
				if (val != null) {
					Object o = val[0].getValue();
					p4 = o.toString();
				}
			} catch (CmdException e) {
				AppDebug.debug(e);
			}
		} else {
			p4 = param.get(0).toString();
		}
		return p4;
	}

	protected String getFieldHashCodeNames() {
		@SuppressWarnings("unchecked")
		ArrayList<Object> param = this.getParams();
		if (param.size() <= 0)
			return null;
		String fieldname = param.get(0).toString();
		return PARAM_FIELD + getIntNegString(fieldname.hashCode());
	}

	@Override
	public UfoVal[] getValue(ICalcEnv env) throws CmdException {
		BIAreaDataChannel biData = null;
		if (env instanceof AreaExprCalcEnv) {
			ITableData dataChannel = ((AreaExprCalcEnv) env).getDataChannel();
			if (dataChannel instanceof BIAreaDataChannel)
				biData = (BIAreaDataChannel) dataChannel;
		}

		// boolean hasBatchCalc = false;
		if (biData != null && biData.getBatchFuncValues() != null) {
			// 从批量计算结果中获取数据
			UfoVal[] result = getResultFromBatch(env, biData);
			if (result != null)
				return result;
		}
		// if (!hasBatchCalc) {//进行单独计算
		return calcOneValue(env);
		// }
	}

	private UfoVal[] getResultFromBatch(ICalcEnv env, BIAreaDataChannel biData) {
		String express = getExpress(env);
		BatchFuncInfo info = biData.getBatchFuncValues().get(express);
		if (info == null)
			return null;
		Object[] obj = info.getCellValueArray(getCellArea());
		return change2UfoValArray(obj);
	}

	private UfoVal[] calcOneValue(ICalcEnv env) throws CmdException {
		String express = getExpress(env);
		String idHashCodeNames = getFieldHashCodeNames();
		Object varrs[] = getParmas(env);
		Object[] oneData = new Object[varrs.length];
		if (varrs != null) {
			for (int i = 0; i < varrs.length; i++) {
				// 循环为函数表达式中的“字段名”参数赋值，参数名使用约定好的参数名
				UFDouble flag = varrs[i] != null ? new UFDouble(
						String.valueOf(varrs[i])) : UFDouble.ZERO_DBL;
				oneData[i] = SalaryDecryptUtil.decrypt(flag.toDouble());
			}
		}
		if (oneData == null || oneData.length == 0)
			return null;

		return new UfoVal[] { change2UfoVal(oneData[0]) };
	}

	protected String getIntNegString(int i) {
		if (i > 0) {
			return i + "";
		} else {
			return Integer.toString(i).substring(1) + "n";
		}
	}

	protected UfoVal change2UfoVal(Object data) {
		int fieldDataType = getFieldDataType();
		if (data == null && fieldDataType == DataTypeConstant.STRING) {
			data = "";
		}
		return UfoVal.createVal(data);
	}

	/**
	 * @return 字段数据类型
	 */
	private int getFieldDataType() {
		CellPosition[] poses = getCellArea();
		if (poses == null || poses.length == 0)
			return DataTypeConstant.UNKNOW_TYPE;
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(anaModel
				.getDataModel());
		CellPosition dataPos = poses[0];
		ExtendAreaCell exCell = exModel.getExArea(dataPos);
		if (exCell == null)
			return DataTypeConstant.UNKNOW_TYPE;
		AnaRepField field = getAnaField(anaModel, dataPos);
		if (field == null)
			return DataTypeConstant.UNKNOW_TYPE;
		return field.getFieldDataType();
	}

	@SuppressWarnings("unchecked")
	private AnaRepField getAnaField(AbsAnaReportModel anaModel,
			CellPosition dataPos) {
		ExtDataModel cpRefModel = (ExtDataModel) anaModel.getDataModel()
				.getExtProp(ExtendAreaConstants.CELLPOS_REFER_MAP);
		if (cpRefModel == null) {
			return null;
		}
		Hashtable<CellPosition, CellPosition> cpRef = (Hashtable<CellPosition, CellPosition>) cpRefModel
				.getValue();
		if (cpRef != null) {
			CellPosition pos = cpRef.get(dataPos);
			AnaRepField field = null;
			if (pos != null) {
				Cell cell = anaModel.getFormatModel().getCell(pos);
				if (cell == null) {
					return null;
				} else {
					field = (AnaRepField) cell
							.getExtFmt(ExtendAreaConstants.FIELD_INFO);
				}
			}
			return field;
		}
		return null;
	}

	protected UfoVal[] change2UfoValArray(Object[] data) {
		UfoVal[] results = new UfoVal[data.length];
		// int fieldDataType = getFieldDataType();
		Object obj = null;
		for (int i = 0; i < results.length; i++) {
			obj = data[i];
			if (obj == null) {
				obj = "";
			}
			results[i] = UfoVal.createVal(obj);
		}
		return results;
	}

	Object[] getParmas(ICalcEnv env) {
		@SuppressWarnings("unchecked")
		ArrayList<Object> param = this.getParams();
		if (param.size() <= 0)
			return null;

		CellPosition[] pos = getCellArea();
		if (pos == null)
			return null;
		String idName = getIDFieldName(env);
		if (idName == null)
			return null;
		Object[] varrs = new Object[1];
		Object[] cellValue = getValueArraybyArea(pos, idName);
		varrs[0] = cellValue;
		return varrs;
	}

	@SuppressWarnings("unchecked")
	private Object[] getValueArraybyArea(CellPosition[] poses,
			String idFieldName) {
		if (poses == null || poses.length == 0)
			return null;
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(anaModel
				.getDataModel());
		CellPosition dataPos = poses[0];
		ExtendAreaCell exCell = exModel.getExArea(dataPos);
		if (exCell == null)
			return null;
		AnaRepField field = getAnaField(anaModel, dataPos);
		if (idFieldName != null) {
			// SmartModel smart =
			// anaModel.getSmartUtil().getSmartModel(exCell.getAreaInfoSet().getSmartModelDefID());
			SmartModel smart = exCell.getAreaInfoSet().getSmartModel();
			if (smart != null) {
				// Field orialFld = smart.getMetaData().getField(idFieldName);
				Field orialFld = getField(exCell, idFieldName);
				if (orialFld != null) {
					orialFld = (Field) orialFld.clone();
					orialFld.setExpression(orialFld.getExpression());
					field = new AnaRepField(orialFld, orialFld.getDataType(),
							smart.getId());
				} else {
					return new Object[] { idFieldName };
				}
			}
		}
		if (field == null)
			return null;

		ExtDataModel cpRefModel = (ExtDataModel) anaModel.getDataModel()
				.getExtProp(ExtendAreaConstants.AREA_INFO_MAP);
		AreaPrivateMap areaMap = ((Hashtable<String, AreaPrivateMap>) cpRefModel
				.getValue()).get(exCell.getExAreaPK());
		if (areaMap == null) {
			return null;
		}
		Hashtable<Integer, GroupDataIndex> dataIndexRef = areaMap
				.getDataIndexRef();

		GroupDataIndex[] dataIndexes = new GroupDataIndex[poses.length];
		for (int i = 0; i < poses.length; i++) {
			int key = 0;
			if (exCell.isRowDirection()) {
				key = poses[i].getRow() - exCell.getArea().getStart().getRow();
			} else {
				key = poses[i].getColumn()
						- exCell.getArea().getStart().getColumn();
			}
			GroupDataIndex currIndex = dataIndexRef.get(key);
			dataIndexes[i] = currIndex;
		}
		AreaGroupDSUtil groupUtil = new AreaGroupDSUtil(
				anaModel.getAreaData(exCell.getExAreaPK()));
		Object[] value = groupUtil.getValueArrayByRow(dataIndexes, field, 0);
		if (value != null) {
			for (int i = 0; i < value.length; i++) {
				value[i] = getRealValue(value[i]);
			}
		}
		return value;

	}

	public Object getRealValue(Object cellValue) {
		if (cellValue instanceof ICellValue) {
			Object realValue = ((ICellValue) cellValue).getCellValue();
			return getRealValue(realValue);
		} else {
			return cellValue;
		}

	}

	private Field getField(ExtendAreaCell exCell, String fieldid) {
		ExtDataModel cpRefModel = (ExtDataModel) anaModel.getDataModel()
				.getExtProp(ExtendAreaConstants.CELLPOS_REFER_MAP);
		if (cpRefModel == null) {
			return null;
		}
		@SuppressWarnings("unchecked")
		Hashtable<CellPosition, CellPosition> cpRef = (Hashtable<CellPosition, CellPosition>) cpRefModel
				.getValue();

		if (cpRef != null) {
			for (CellPosition pos : exCell.getArea().split()) {
				CellPosition pos2 = cpRef.get(pos);
				if (pos2 != null) {
					pos = pos2;
				}
				Cell cell = anaModel.getFormatModel().getCell(pos);
				if (cell != null) {
					AnaRepField repField = (AnaRepField) cell
							.getExtFmt(AnaRepField.EXKEY_FIELDINFO);
					if (repField != null) {
						if (repField.getField().getFldname().equals(fieldid)) {
							return repField.getField();
						}
					}
				}
			}
		}
		return null;
	}

}
