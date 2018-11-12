/**
 * @(#)PocFileReader.java 1.0 2017-09-16
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.vo.wa.pub.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.ResHelper;
import nc.itf.hrwa.IProjsalaryMaintain;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.projsalary.AggProjSalaryVO;
import nc.vo.wa.projsalary.ProjSalaryHVO;
import nc.vo.wa.pub.WaLoginContext;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;

/**
 * 读取文件的工具类
 * 
 * @author kevin.nie
 * @since 6.1
 */
public class FileReader {

	public static String IMP_DATA = "imp_data";
	public static String Err_MSG = "err_msg";
	public static String Err_SYS_EXIST = "err_sys_exist";
	public static String DEL_PKS = "del_pks";

	/*
	 * @Description: 相同行是否合并.
	 */
	private static boolean isSameComb = false;

	public static boolean isSameComb() {
		return isSameComb;
	}

	public void setSameComb(boolean isSameComb) {
		FileReader.isSameComb = isSameComb;
	}

	public static String[] getFieldNames() {
		List<String> listName = new ArrayList<String>();
		listName.add("psncode");
		listName.add("pjcode");
		// listName.add("pjname");
		listName.add("itemname");
		listName.add("itemamt");

		return ((String[]) listName.toArray(new String[0]));
	}

	public static String[] getFieldDisplayNames() {
		List<String> listName = new ArrayList<String>();
		listName.add(ResHelper.getString("projsalary", "0pjsalary-00027"));
		// ("projsalary", "0pjsalary-00028")
		listName.add(ResHelper.getString("60130projsalary", "160130projsalary-00001"));
		// listName.add(ResHelper.getString("60130projsalary",
		// "160130projsalary-00002"));
		listName.add(ResHelper.getString("projsalary", "0pjsalary-00029"));
		listName.add(ResHelper.getString("projsalary", "0pjsalary-00030"));

		return ((String[]) listName.toArray(new String[0]));
	}

	/**
	 * 从给定文件中读取需要替换的特殊字符
	 * 
	 * @param fileSource
	 * @return
	 */
	public static Map<String, String> readFilenameReplaceExcel(final File fileSource) {
		// list用来保持Excel文件中的行顺序
		final Map<String, String> mapFilenameReplace = new HashMap<String, String>();
		try {
			FileUtils.readExcel(fileSource.getPath(), new IExcelRowReader() {
				@Override
				public boolean readRow(int sheetNo, int rowNo, Row row) {
					// 如果第一个cell为null，则返回
					if (row == null || FileUtils.getCellString(row, 0) == null) {
						return false;
					}
					String key = FileUtils.getCellString(row, 0).trim();
					String value = FileUtils.getCellString(row, 1).trim();
					mapFilenameReplace.put(key, value);
					return true;
				}

			});
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}

		return mapFilenameReplace;
	}

	/**
	 * 从给定文件中读取数据
	 * 
	 * @param fileSource
	 * @return
	 */
	public static Map<String, Object> readProjSalaryExcel(final File fileSource, WaLoginContext waContext) {
		// list用来保持Excel文件中的行顺序
		final Map<String, Object> resultMap = new HashMap<String, Object>();
		if (null == fileSource || null == waContext) {
			resultMap.put(Err_MSG, ResHelper.getString("projsalary", "0pjsalary-00023"));
			return resultMap;
		}

		StringBuilder baseWhere = new StringBuilder();
		baseWhere.append(" pk_org='").append(waContext.getPk_org()).append("' ");
		baseWhere.append(" and pk_wa_calss='").append(waContext.getClassPK()).append("' ");

		IProjsalaryMaintain service = NCLocator.getInstance().lookup(IProjsalaryMaintain.class);
		Map<String, ProjSalaryHVO> periodItemProjMap = new HashMap<String, ProjSalaryHVO>();
		Map<String, WaClassItemVO> itemMap = new HashMap<String, WaClassItemVO>();
		Map<String, DefdocVO> projectMap = new HashMap<String, DefdocVO>();
		Map<String, PsndocVO> psndocMap = new HashMap<String, PsndocVO>();
		try {
			StringBuilder pjsaWhere = new StringBuilder(baseWhere);
			pjsaWhere.append(" and cperiod='").append(waContext.getCyear()).append(waContext.getCperiod()).append("' ");
			periodItemProjMap = service.qryClassItemProjHVOMap(pjsaWhere.toString());
			StringBuilder periodCondition = new StringBuilder(baseWhere.toString().replaceAll("pk_wa_calss",
					"pk_wa_class"));
			periodCondition.append(" and cyear='").append(waContext.getCyear()).append("' ");
			periodCondition.append(" and cperiod='").append(waContext.getCperiod()).append("' ");
			itemMap = service.qryClassItemByPeriod(periodCondition.toString(),
					new String[] { "name" + MultiLangHelper.getLangIndex() });
			projectMap = service.qryProjectMap(null, new String[] { "code" });
			StringBuilder psnFilter = new StringBuilder(" pk_psndoc in (select distinct bd_psndoc.pk_psndoc ");
			psnFilter.append(" from bd_psndoc inner join hi_psnorg on hi_psnorg.pk_psndoc=bd_psndoc.pk_psndoc ");
			// psnFilter.append(" where hi_psnorg.indocflag='Y' and hi_psnorg.endflag='N' and hi_psnorg.lastflag='Y' )");
			psnFilter.append(" where hi_psnorg.indocflag='Y' )");
			psnFilter.append(" and pk_psndoc in ( ");
			psnFilter.append(" select pk_psndoc from wa_data where 1=1 ");
			psnFilter.append(" and pk_org='").append(waContext.getPk_org()).append("' ");
			psnFilter.append(" and pk_wa_class='").append(waContext.getClassPK()).append("' ");
			psnFilter.append(" and cyear='").append(waContext.getCyear()).append("' ");
			psnFilter.append(" and cperiod='").append(waContext.getCperiod()).append("' ) ");
			psndocMap = service.qryPsndocVOMap(psnFilter.toString(), new String[] { "code" });
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}

		final Map<String, PsndocVO> psndocVOMap = psndocMap;
		final Map<String, DefdocVO> projectVOMap = projectMap;
		final Map<String, WaClassItemVO> itemVOMap = itemMap;
		// map用来快速定位相同ID的记录
		final Map<String, AggProjSalaryVO> mapAggPjSaVO = new HashMap<String, AggProjSalaryVO>();
		// excel空值错误
		final List<String> emptyErrList = new ArrayList<String>();
		// excel系统值转换错误
		final List<String> transferErrList = new ArrayList<String>();
		// excel存在相同记录错误
		final Map<String, List<String>> sameRecordErrMap = new HashMap<String, List<String>>();

		try {
			FileUtils.readExcel(fileSource.getPath(), new IExcelRowReader() {
				@Override
				public boolean readRow(int sheetNo, int rowNo, Row row) {
					if (row == null) {
						return false;
					}
					if (rowNo == 0) {
						return true;
					}

					ProjSalaryHVO hvo = new ProjSalaryHVO();

					String exl_psncode = FileUtils.getCellString(row, 0);
					String exl_pjcode = FileUtils.getCellString(row, 1);
					String exl_itemname = FileUtils.getCellString(row, 2);
					String exl_itemamt = FileUtils.getCellString(row, 3);

					hvo.setSheetno(sheetNo);
					hvo.setRowno(rowNo);
					hvo.setExl_psncode(exl_psncode);
					hvo.setExl_pjcode(exl_pjcode);
					hvo.setExl_itemname(exl_itemname);
					hvo.setExl_amt(exl_itemamt);

					if (StringUtils.isEmpty(exl_psncode)) {
						// 行[{0},{1}],员工编号不能为空！
						emptyErrList.add(ResHelper.getString("projsalary", "0pjsalary-00010", null, new String[] {
								sheetNo + "", rowNo + "" }));
					} else {
						PsndocVO psnVO = psndocVOMap.get(exl_psncode);
						if (null == psnVO) {
							// 行[{0},{1}],员工编号在系统中不存在!
							transferErrList.add(ResHelper.getString("projsalary", "0pjsalary-00014", null,
									new String[] { sheetNo + "", rowNo + "" }));
						} else {
							hvo.setPk_psndoc(psnVO.getPk_psndoc());
						}
					}
					if (StringUtils.isEmpty(exl_pjcode)) {
						// 行[{0},{1}],专案代号不能为空！
						emptyErrList.add(ResHelper.getString("projsalary", "0pjsalary-00011", null, new String[] {
								sheetNo + "", rowNo + "" }));
					} else {
						DefdocVO projVO = projectVOMap.get(exl_pjcode);
						if (null == projVO) {
							// 行[{0},{1}],专案代号在系统中不存在!
							transferErrList.add(ResHelper.getString("projsalary", "0pjsalary-00015", null,
									new String[] { sheetNo + "", rowNo + "" }));
						} else {
							hvo.setDef1(projVO.getPk_defdoc());
							hvo.setPk_project(projVO.getPk_defdoc());
						}
					}
					if (StringUtils.isEmpty(exl_itemname)) {
						// 行[{0},{1}],薪资项目不能为空!
						emptyErrList.add(ResHelper.getString("projsalary", "0pjsalary-00012", null, new String[] {
								sheetNo + "", rowNo + "" }));
					} else {
						WaClassItemVO itemVO = itemVOMap.get(exl_itemname);
						if (null == itemVO) {
							// 行[{0},{1}],薪资项目不是专案分摊或者系统中不存在!
							transferErrList.add(ResHelper.getString("projsalary", "0pjsalary-00016", null,
									new String[] { sheetNo + "", rowNo + "" }));
						} else {
							hvo.setPk_classitem(itemVO.getPk_wa_classitem());
						}
					}
					if (StringUtils.isEmpty(exl_itemamt)) {
						// 行[{0},{1}],金额不能为空!
						emptyErrList.add(ResHelper.getString("projsalary", "0pjsalary-00013", null, new String[] {
								sheetNo + "", rowNo + "" }));
					} else {
						if (checkUFDouble(exl_itemamt)) {
							hvo.setSalaryamt(new UFDouble(exl_itemamt));
						} else {
							// 行[{0},{1}],金额数据类型错误!
							transferErrList.add(ResHelper.getString("projsalary", "0pjsalary-00017", null,
									new String[] { sheetNo + "", rowNo + "" }));
						}
					}

					AggProjSalaryVO aggProjSaVO = new AggProjSalaryVO();
					aggProjSaVO.setParentVO(hvo);

					if (hvo.excelImpCheck()) {
						String id = hvo.getID();
						if (mapAggPjSaVO.containsKey(id)) {
							if (isSameComb()) {
								UFDouble amt = getValueZero(mapAggPjSaVO.get(id).getParentVO().getSalaryamt()).add(
										getValueZero(hvo.getSalaryamt()));
								mapAggPjSaVO.get(id).getParentVO().setSalaryamt(amt);
							} else {
								// 存在相同人员[{1}]专案代码[{2}]薪资项目[{3}]的薪资数据!
								String key = ResHelper.getString("projsalary", "0pjsalary-00018", null, new String[] {
										exl_psncode, exl_pjcode, exl_itemname });
								List<String> sheetRows = sameRecordErrMap.get(key);
								if (null == sheetRows) {
									sheetRows = new ArrayList<String>();
									ProjSalaryHVO oldHvo = mapAggPjSaVO.get(id).getParentVO();
									sheetRows.add(oldHvo.getSheetRowNo());
									sameRecordErrMap.put(key, sheetRows);
								}
								sheetRows.add(hvo.getSheetRowNo());
							}

						} else {
							mapAggPjSaVO.put(id, aggProjSaVO);
						}
					}

					return true;
				}
			});
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}

		StringBuilder errMsg = new StringBuilder();
		List<String> delPkList = new ArrayList<String>();
		if (!emptyErrList.isEmpty()) {
			for (String err : emptyErrList) {
				errMsg.append(err);
			}
		}
		if (!transferErrList.isEmpty()) {
			for (String err : transferErrList) {
				errMsg.append(err);
			}
		}
		if (!sameRecordErrMap.isEmpty()) {
			for (String key : sameRecordErrMap.keySet()) {
				errMsg.append(key);
				List<String> sheetRows = sameRecordErrMap.get(key);
				StringBuilder sheetRowSB = new StringBuilder();
				for (String sheetrow : sheetRows) {
					sheetRowSB.append(sheetrow);
				}
				// 行{0},请确认后再导入！
				errMsg.append(ResHelper.getString("projsalary", "0pjsalary-00019", null,
						new String[] { sheetRowSB.toString() }));
			}
		}
		if (errMsg.length() == 0) {
			if (!periodItemProjMap.isEmpty()) {
				StringBuilder sysExist = new StringBuilder();
				for (String excelKey : mapAggPjSaVO.keySet()) {
					if (null != periodItemProjMap.get(excelKey)) {
						ProjSalaryHVO exlpjsHVO = mapAggPjSaVO.get(excelKey).getParentVO();
						ProjSalaryHVO dbpjsHVO = periodItemProjMap.get(excelKey);
						delPkList.add(dbpjsHVO.getPk_projsalary());

						dbpjsHVO.setSalaryamt(exlpjsHVO.getSalaryamt());
						dbpjsHVO.setPk_psndoc(exlpjsHVO.getPk_psndoc());
						mapAggPjSaVO.get(excelKey).setParentVO(dbpjsHVO);
						// {人员[{0}]专案代码[{1}]薪资项目[{2}]}
						sysExist.append(ResHelper.getString("projsalary", "0pjsalary-00022", null, new String[] {
								exlpjsHVO.getExl_psncode(), exlpjsHVO.getExl_pjcode(), exlpjsHVO.getExl_itemname() }));
					}
				}
				if (sysExist.length() > 0) {
					// 系统已存在相同专案代码的薪资数据！
					StringBuilder errSysExist = new StringBuilder(ResHelper.getString("projsalary", "0pjsalary-00020"));
					// {0},请确认是否进行覆盖！
					errSysExist.append(ResHelper.getString("projsalary", "0pjsalary-00021", null,
							new String[] { sysExist.toString() }));
					resultMap.put(Err_SYS_EXIST, errSysExist.toString());
				}
			}
		} else {
			resultMap.put(Err_MSG, errMsg.toString());
		}
		if (!delPkList.isEmpty()) {
			resultMap.put(DEL_PKS, delPkList.toArray(new String[0]));
		}
		resultMap.put(IMP_DATA, mapAggPjSaVO.values().toArray(new AggProjSalaryVO[0]));
		return resultMap;
	}

	private static UFDouble getValueZero(UFDouble ufd) {
		return ufd == null ? UFDouble.ZERO_DBL : ufd;
	}

	/**
	 * @Description: 检查是否金额类型。
	 * @param dValue
	 * @return boolean
	 */
	private static boolean checkUFDouble(String dValue) {
		try {
			@SuppressWarnings("unused")
			UFDouble amt = new UFDouble(dValue);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) {

	}
}
