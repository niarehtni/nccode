package nc.itf.twhr;

/**
 * @(#)DataItfFileReader.java 1.0 2018��1��30��
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nc.itf.wa.datainterface.IExcelRowReader;
import nc.pub.wa.datainterface.DataItfConst;
import nc.pub.wa.datainterface.FileUtils;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;
import nc.vo.twhr.nhicalc.BaoAccountVO;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.datainterface.DataItfFileVO;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import uap.iweb.log.Logger;

/**
 * @author niehg
 * @since 6.3
 */
public class DataItfFileReaderAccount {
	public static boolean isExcelFile(String filePath) {
		return filePath.toLowerCase().trim().endsWith(DataItfConst.SUFFIX_XLS)
				|| filePath.toLowerCase().trim().endsWith(DataItfConst.SUFFIX_XLSX);
	}

	public static boolean isTxtFile(String filePath) {
		return filePath.toLowerCase().trim().endsWith(DataItfConst.SUFFIX_TXT);
	}

	public static BaoAccountVO[] readFileSD(String pk_org, String filePath, LoginContext waContext) throws Exception {
		if (StringUtils.isNotBlank(filePath)) {
			BaoAccountVO[] datas = null;
			if (isExcelFile(filePath)) {
				datas = readExcelSD(pk_org, filePath, waContext);
			}
			return datas;
		}
		return null;
	}

	public static BaoAccountVO[] readExcelSD(final String pk_org, String filePath, final LoginContext waContext)
			throws Exception {
		final List<BaoAccountVO> voList = new ArrayList<BaoAccountVO>();
		final StringBuilder errImpMsg = new StringBuilder();

		FileUtils.readExcel(filePath, new IExcelRowReader() {

			@Override
			public boolean readRow(int sheetNo, int rowNo, Row row) throws Exception {
				if (null != row) {

					String idno = getCellValue(row.getCell(0)); // ����֤�ֺ�
					String labor_amount = getCellValue(row.getCell(1)); // �ͱ�Ͷ�����
					String labor_psnamount = getCellValue(row.getCell(2)); // �ͱ���˾�������
					String labor_orgamount = getCellValue(row.getCell(3)); // �ͱ���˾�������
					String retire_amount = getCellValue(row.getCell(4)); // �������н���ܶ�
					String retire_psnamount = getCellValue(row.getCell(5)); // ����Ա��������
					String retire_orgamount = getCellValue(row.getCell(6));// ���˹�����ɽ��
					String health_amount = getCellValue(row.getCell(7)); // ����Ա������
					String health_psnamount = getCellValue(row.getCell(8));// ����Ա������
					String health_orgamount = getCellValue(row.getCell(9));// ������������
					String pk_period = getCellValue(row.getCell(10));
					Logger.info(rowNo + "");

					BaoAccountVO dvo = new BaoAccountVO();
					voList.add(dvo);
					setMnyFieldValueZero(dvo);

					// if (voList.size() > paraVO.getLimitNum()) {
					// throw new Exception(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0045", null,
					// new String[] { paraVO.getLimitNum() + "" }));
					// }

					dvo.setIdno(idno);
					dvo.setLabor_amount(new UFDouble(labor_amount));
					dvo.setLabor_psnamount(new UFDouble(labor_psnamount));
					dvo.setLabor_orgamount(new UFDouble(labor_orgamount));
					dvo.setRetire_psnamount(new UFDouble(retire_psnamount));
					dvo.setRetire_orgamount(new UFDouble(retire_orgamount));

					dvo.setHealth_amount(new UFDouble(health_amount));
					dvo.setHealth_orgamount(new UFDouble(health_orgamount));
					dvo.setHealth_psnamount(new UFDouble(health_psnamount));
					dvo.setPk_period(pk_period);
					dvo.setPk_org(pk_org);
					dvo.setPk_group(waContext.getPk_group());
					// ��õ�ǰ�е�����
					int lastCellNum = row.getPhysicalNumberOfCells();
					for (int i = 5; i < lastCellNum; i++) {
						if (i == 32) {
							continue;
						}

					}
					// if (StringUtils.isEmpty(waContext.getPk_org())) {
					// waContext.setPk_org(pk_org);
					// }

				}
				return true;

			}
		});
		if (errImpMsg.length() > 0) {
			throw new Exception(errImpMsg.toString());
		}
		return voList.toArray(new BaoAccountVO[0]);
	}

	public static void setDataByContext(DataItfFileVO vo, LoginContext waContext) {
		if (null != vo && null != waContext) {
			vo.setStatus(VOStatus.NEW);
			vo.setDataid(null);
			vo.setPk_group(waContext.getPk_group());
			vo.setPk_org(waContext.getPk_org());

		}
	}

	public static String getCellValue(Cell cell) throws Exception {
		String cellValue = "";
		if (cell == null) {
			return cellValue;
		}
		// if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
		// cell.setCellType(Cell.CELL_TYPE_STRING);
		// }
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC: // ����
			if (DateUtil.isCellDateFormatted(cell)) {
				cellValue = DataItfConst.SDF_STR_DATE.format(cell.getDateCellValue());
			} else {
				DecimalFormat df = new DecimalFormat("0");
				cellValue = df.format(cell.getNumericCellValue());
			}
			break;
		case Cell.CELL_TYPE_STRING: // �ַ���
			cellValue = String.valueOf(cell.getStringCellValue());
			break;
		case Cell.CELL_TYPE_BOOLEAN: // Boolean
			cellValue = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA: // ��ʽ
			cellValue = String.valueOf(cell.getCellFormula());
			break;
		case Cell.CELL_TYPE_BLANK: // ��ֵ
			cellValue = "";
			break;
		default:
			throw new Exception("Excel Data cell type is err! can not transter context!");
		}
		return cellValue;
	}

	public static String getStr(String str) {
		String s = StringUtils.isBlank(str) ? "" : str.trim();
		return s;
	}

	public static UFDouble getNumber(String strNum) {
		UFDouble ufd = StringUtils.isBlank(strNum) ? UFDouble.ZERO_DBL : new UFDouble(strNum);
		ufd.setScale(-2, UFDouble.ROUND_UP);
		return ufd;
	}

	public static void setMnyFieldValueZero(BaoAccountVO vo) {
		// String[] fields = vo.getAttributeNames();
		// if (!ArrayUtils.isEmpty(fields)) {
		// for (String fd : fields) {
		// if (fd.toLowerCase().startsWith("f_")) {
		// vo.setAttributeValue(fd, 0);
		// }
		// }
		// }

		for (int i = 1; i <= 280; i++) {
			vo.setAttributeValue("f_" + i, 0);
		}
	}

	public static BaoAccountVO[] readhealthTxtFileSD(String pk_org, String waperiod, String filePath,
			LoginContext waContext) {
		List<BaoAccountVO> baoaccountlist = txthealthString(filePath, waperiod, waContext);
		return baoaccountlist.toArray(new BaoAccountVO[baoaccountlist.size()]);
	}

	public static BaoAccountVO[] readTxtFileSD(String pk_org, String waperiod, String filePath, LoginContext waContext) {
		List<BaoAccountVO> baoaccountlist = txtlaborString(filePath, waperiod, waContext);
		return baoaccountlist.toArray(new BaoAccountVO[baoaccountlist.size()]);
	}

	public static BaoAccountVO[] readRetireTxtFileSD(String pk_org, String waperiod, String filePath,
			LoginContext waContext) {
		List<BaoAccountVO> baoaccountlist = txtretireString(filePath, waContext, waperiod);
		return baoaccountlist.toArray(new BaoAccountVO[baoaccountlist.size()]);
	}

	private static List<BaoAccountVO> txtlaborString(String file, String waperiod, LoginContext waContext) {
		List<BaoAccountVO> list = new ArrayList<BaoAccountVO>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "BIG5"));
			// BufferedReader br = new BufferedReader(new FileReader(file));//
			// ����һ��BufferedReader������ȡ�ļ�
			String str = null;
			while ((str = br.readLine()) != null) {// ʹ��readLine������һ�ζ�һ��
				String[] strs = str.split(",");
				if (strs.length == 6) {
					BaoAccountVO dvo = new BaoAccountVO();
					dvo.setIdno(strs[2]);// ����֤�ֺ�
					dvo.setLabor_psnamount(new UFDouble(strs[4]));// �ͱ�Ա���������
					dvo.setLabor_orgamount(new UFDouble(strs[5]));// �ͱ���˾�������
					// // �ͱ�Ͷ�����
					dvo.setName(strs[1]);
					dvo.setPk_period(waperiod);
					dvo.setPk_org(waContext.getPk_org());
					dvo.setPk_group(waContext.getPk_group());
					dvo.setDr(0);
					list.add(dvo);
				}

			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	public static List<BaoAccountVO> txthealthString(String file, String waperiod, LoginContext waContext) {
		StringBuilder result = new StringBuilder();
		// ����֤���������ʽ /^[A-Z\d]+$/ ��ֻ������д��ĸ�����֣�
		List<BaoAccountVO> list = new ArrayList<BaoAccountVO>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "BIG5"));
			// BufferedReader br = new BufferedReader(new FileReader(file));//
			// ����һ��BufferedReader������ȡ�ļ�
			String str = null;
			while ((str = br.readLine()) != null) {// ʹ��readLine������һ�ζ�һ��
				if (str.length() > 30) {
					String id = str.substring(20, 30);
					String regEx = "^[0-9A-Z]+$";
					Pattern pattern = Pattern.compile(regEx);
					Matcher matcher = pattern.matcher(id);
					boolean rs = matcher.matches();
					if (rs) {
						BaoAccountVO dvo = new BaoAccountVO();
						dvo.setIdno(id);// ����֤�ֺ�
						dvo.setHealth_orgamount(new UFDouble(str.substring(69, 78).replace(" ", "")));// ������˾�������
						dvo.setHealth_psnamount(new UFDouble(str.substring(60, 69).replace(" ", "")));// ����Ա������
						dvo.setName(str.substring(7, 15).replace(" ", ""));
						dvo.setPk_period(waperiod);
						dvo.setPk_org(waContext.getPk_org());
						dvo.setPk_group(waContext.getPk_group());
						dvo.setDr(0);
						list.add(dvo);
					}
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<BaoAccountVO> txtretireString(String file, LoginContext waContext, String waperiod) {
		List<BaoAccountVO> list = new ArrayList<BaoAccountVO>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "BIG5"));
			// BufferedReader br = new BufferedReader(new FileReader(file));//
			// ����һ��BufferedReader������ȡ�ļ�
			String str = null;
			while ((str = br.readLine()) != null) {// ʹ��readLine������һ�ζ�һ��
				String[] strs = str.split(",");
				if (strs.length >= 16) {
					BaoAccountVO dvo = new BaoAccountVO();
					dvo.setIdno(strs[6]);// ����֤�ֺ�
					if (strs[3].equals("91")) {
						// 91����
						dvo.setRetire_orgamount(new UFDouble(strs[9].replaceAll(" ", "")));
					} else {
						// 92����
						dvo.setRetire_psnamount(new UFDouble(strs[9].replaceAll(" ", "")));
					}
					// // ������Ա����
					dvo.setName(strs[4].replaceAll(" ", ""));
					dvo.setPk_period(waperiod);
					dvo.setPk_org(waContext.getPk_org());
					dvo.setPk_group(waContext.getPk_group());
					dvo.setDr(0);
					list.add(dvo);
				}

			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

}