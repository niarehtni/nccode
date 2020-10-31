package nc.ui.twhr.twhr_declaration.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.twhr.ITwhr_declarationMaintain;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.uif2.model.ModelDataDescriptor;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.twhr.twhr_declaration.AggDeclarationVO;
import nc.vo.twhr.twhr_declaration.DeclarationHVO;
import nc.vo.twhr.twhr_declaration.NonPartTimeBVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 二代健保历史数据导入
 * 
 * @author wangywt
 * @since 20190620
 * 
 */
public class DecImportAction extends HrAction {

	private static final long serialVersionUID = -1542523256471536559L;

	private nc.ui.twhr.glb.view.OrgPanel_Org primaryOrgPanel = null;
	private BillListView billListView;

	// 日期格式化器
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	// key是法人公司编码，value是法人公司
	private Map<String, OrgVO> corpMap = new HashMap<String, OrgVO>();

	// 集团pk放在缓存中，key是部门编码，value是pk_dept
	private Map<String, String> deptMap = new HashMap<String, String>();

	// key薪资方案编码，value薪资方案ok
	private Map<String, String> waclaMap = new HashMap<String, String>();

	// key薪资期间类型201907，value是薪资期间主键
	private Map<String, String> perMap = new HashMap<String, String>();

	// key是身份证号，value是pk_psndoc
	private Map<String, String> psnMap = new HashMap<String, String>();
	// 是否有投保记录 map <psndoc::pk_org,投保记录条数>
	private Map<String, Integer> psnOrgHealthMap = new HashMap<>();

	public DecImportAction() {
		super();
		super.setCode("DecImportAction");
		super.putValue(Action.SHORT_DESCRIPTION, "历史健保数据导入");
		setBtnName("导入");
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		DecImportDlg dlg = new DecImportDlg(getContext());
		if (1 == dlg.showModal()) {
			putValue(HrAction.MESSAGE_AFTER_ACTION, "");
			final String filePath = dlg.getFilePathPane().getText();
			final LoginContext waContext = getContext();
			new Thread(new Runnable() {
				@Override
				public void run() {
					IProgressMonitor progressMonitor = NCProgresses.createDialogProgressMonitor(DecImportAction.this
							.getEntranceUI());

					progressMonitor.beginTask(ResHelper.getString("6013dataitf_01", "dataitf-01-0042"), -1); // 导入数据...
					progressMonitor.setProcessInfo(ResHelper.getString("6013dataitf_01", "dataitf-01-0043")); // 数据导入中,请稍后......
					try {
						DecImportAction.this.dataImport(filePath, waContext);
						MessageDialog.showHintDlg(DecImportAction.this.getEntranceUI(), null,
								ResHelper.getString("6013dataitf_01", "dataitf-01-0044")); // 数据导入成功！
					} catch (Exception e) {
						Logger.error(e);
						MessageDialog.showErrorDlg(DecImportAction.this.getEntranceUI(), null, e.getMessage());
					} finally {
						progressMonitor.done();
					}
				}
			}).start();

		} else {
			putValue(HrAction.MESSAGE_AFTER_ACTION, "");
		}
	}

	@SuppressWarnings("restriction")
	protected void dataImport(String filePath, LoginContext context) throws Exception {
		if (StringUtils.isBlank(filePath)) {
			Logger.error("import filepath is bank!");
			// "导入数据文件为空!"
			throw new Exception("文件路徑為空");
		} else if (!isExcelFile(filePath)) {
			// "只能导入excel类型数据文件!"
			throw new Exception("不能導入非excel類型文件");
		}
		// 导入的文件
		File imfile = new File(filePath);
		InputStream filein = new FileInputStream(imfile);
		// 判断excel的版本
		Workbook wb = null;
		String exPath = null;
		String version = (filePath.endsWith(".xls") ? "2003" : "2007");
		if (version.equals("2003")) {
			wb = new HSSFWorkbook(filein);
			exPath = filePath + System.currentTimeMillis() + ".xls";
		} else if (version.equals("2007")) {
			wb = new XSSFWorkbook(filein);
			exPath = filePath + System.currentTimeMillis() + ".xlsx";
		}
		// 输出的文件
		File exfile = new File(exPath);
		FileOutputStream fileout = new FileOutputStream(exfile);
		try {
			// 非兼职人员补充保费数据
			Sheet sheet = wb.getSheetAt(0);
			List<AggDeclarationVO> aggvoList = new ArrayList<>();
			boolean flag = importDataNONPart(sheet, aggvoList);
			// 将工作薄写入文件输出流中
			wb.write(fileout);
			if (flag) {
				AggDeclarationVO[] retaggvo = NCLocator.getInstance().lookup(ITwhr_declarationMaintain.class)
						.insert(aggvoList.toArray(new AggDeclarationVO[0]), aggvoList.toArray(new AggDeclarationVO[0]));
				this.getModel().initModel(retaggvo, new ModelDataDescriptor());
			}
		} catch (Exception e) {
			Logger.error(e);
			MessageDialog.showErrorDlg(DecImportAction.this.getEntranceUI(), null, e.getMessage());
		} finally {
			// 文本文件流，释放资源
			fileout.close();
			filein.close();
		}
	}

	/**
	 * 是不是excel文件
	 * 
	 * @param filePath
	 * @return
	 */
	private boolean isExcelFile(String filePath) {
		return filePath.toLowerCase().trim().endsWith(DataDecConst.SUFFIX_XLS)
				|| filePath.toLowerCase().trim().endsWith(DataDecConst.SUFFIX_XLSX);
	}

	/**
	 * 导入公司补充保费数据
	 * 
	 * @param filePath
	 *            文件路径
	 * @param context
	 */
	private void importDataCompany(Sheet sheet, AggDeclarationVO aggvo) {
		// TODO Auto-generated method stub

	}

	/**
	 * 导入执行业务所得补充保费数据
	 * 
	 * @param filePath
	 *            文件路径
	 * @param context
	 */
	private void importDataBusiness(Sheet sheet, AggDeclarationVO aggvo) {
		// TODO Auto-generated method stub

	}

	/**
	 * 导入兼职人员补充保费数据
	 * 
	 * @param filePath
	 *            文件路径
	 * @param context
	 */
	private void importDataPart(Sheet sheet, AggDeclarationVO aggvo) {
		// TODO Auto-generated method stub

	}

	/**
	 * 导入非兼职人员补充保费数据
	 * 
	 * @param aggvoList
	 * @param filePath
	 *            文件路径
	 * @param context
	 * @throws BusinessException
	 */
	private boolean importDataNONPart(Sheet sheet, List<AggDeclarationVO> aggvoList) throws BusinessException {
		if (sheet == null || sheet.getLastRowNum() == 0) {
			return false;
		}
		// 初始化缓存
		psnOrgHealthMap = new HashMap<>();
		List<NonPartTimeBVO> vos = new ArrayList<NonPartTimeBVO>();
		// 先循环行，逐行解析
		for (int curSeq = 1; curSeq <= sheet.getLastRowNum(); curSeq++) {
			Row row = sheet.getRow(curSeq);
			StringBuffer sb = new StringBuffer();
			NonPartTimeBVO bvo = new NonPartTimeBVO();
			// 法人公司, 必填
			if (row.getCell(0) != null) {
				row.getCell(0).setCellType(HSSFCell.CELL_TYPE_STRING);
			}

			String corpcode = this.getCellValue(row.getCell(0));
			if (corpcode == null || corpcode.length() == 0) {
				sb.append("法人公司不能為空；");
			} else {
				OrgVO corp = this.getCorpByCode(corpcode);
				if (corp == null) {
					sb.append("根據編碼：" + row.getCell(0) + "未找到相應法人公司;");
				} else {
					bvo.setPk_group(corp.getPk_group());
					bvo.setPk_org(corp.getPk_org());
					bvo.setPk_org_v(corp.getPk_vid());
					bvo.setVbdef1(corp.getPk_org());
				}
			}
			// 部门，必填
			if (row.getCell(1) != null) {
				row.getCell(1).setCellType(HSSFCell.CELL_TYPE_STRING);
			}
			String deptcode = this.getCellValue(row.getCell(1));
			if (deptcode != null && deptcode.length() > 0) {
				String dept = this.getDeptPK(deptcode);
				if (dept == null || dept.trim().length() == 0)
					sb.append("根據編碼：" + row.getCell(6) + "未找到相應部門;");
				else
					bvo.setPk_dept(dept);
			}
			// 薪资方案，必填
			if (row.getCell(2) != null) {
				row.getCell(2).setCellType(HSSFCell.CELL_TYPE_STRING);
			}
			String wacode = this.getCellValue(row.getCell(2));
			if (wacode != null && wacode.length() > 0) {
				String waclass = this.getWaClass(wacode);
				if (waclass == null || waclass.trim().length() == 0) {
					sb.append("根據編碼：" + row.getCell(2) + "未找到薪資方案;");
				} else {
					bvo.setVbdef2(waclass);
				}
			}
			// 薪资期间，必填
			if (row.getCell(3) != null) {
				row.getCell(3).setCellType(HSSFCell.CELL_TYPE_STRING);
			}
			String perdate = this.getCellValue(row.getCell(3));
			if (perdate != null && perdate.length() > 0 && wacode != null) {
				String period = this.getPeriod(perdate + wacode);
				if (period == null || period.trim().length() == 0) {
					sb.append("未找到薪資期間;");
				} else {
					bvo.setVbdef3(period);
				}
			}
			// 给付日期, 必填
			if (row.getCell(4) == null) {
				sb.append("給付日期不能為空；");
			} else {
				String pay_date = this.getCellValue(row.getCell(4));
				try {
					bvo.setPay_date(new UFDate(pay_date));
				} catch (Exception e) {
					sb.append("給付日期格式不對；");
				}
			}
			// 所得人身份证号, 必填
			row.getCell(5).setCellType(HSSFCell.CELL_TYPE_STRING);
			if (row.getCell(5) == null) {
				sb.append("身份證號不能為空；");
			} else {
				String id = this.getCellValue(row.getCell(5));
				String psn = getPsnByID(id);
				if (psn == null || psn.length() == 0) {
					sb.append("身份證號查不到人员；");
				} else {
					// 所得人姓名, 必填
					row.getCell(6).setCellType(HSSFCell.CELL_TYPE_STRING);
					String name = this.getCellValue(row.getCell(6));
					String[] split = psn.split("&");
					bvo.setBeneficiary_id(id);
					bvo.setVbdef4(split[0]);
					if (name == null) {
						row.getCell(6).setCellValue(split[1]);
					} else {
						if (!name.equals(split[1])) {
							sb.append("所得人姓名不符；");
						}
					}
					bvo.setBeneficiary_name(split[1]);
				}
			}
			// 单次所得给付奖金金额
			if (row.getCell(7) == null) {
				sb.append("單次所得給付獎金總額不能為空；");
			} else {
				String singlepay = this.getCellValue(row.getCell(7));
				try {
					bvo.setSingle_pay(new UFDouble(singlepay));
				} catch (Exception e) {
					sb.append("單次所得給付獎金總額填入非數字；");
				}
			}
			// 单次扣缴补充保费金额
			if (row.getCell(8) == null) {
				sb.append("單次扣繳補充保費金額不能為空；");
			} else {
				String withhold = this.getCellValue(row.getCell(8));
				try {
					bvo.setSingle_withholding(new UFDouble(withhold));
				} catch (Exception e) {
					sb.append("單次扣繳補充保費金額填入非數字；");
				}
			}
			// 投保单位代号
			if (row.getCell(9) != null) {
				row.getCell(9).setCellType(HSSFCell.CELL_TYPE_STRING);
			}
			if (row.getCell(9) == null) {
				sb.append("投保單位代號不能為空；");
			} else {
				String unitcode = this.getCellValue(row.getCell(9));
				bvo.setInsurance_unit_code(unitcode);
			}
			// 扣费当月投保金额
			if (row.getCell(10) == null) {
				sb.append("扣費當月投保金額不能為空；");
			} else {
				String monthpay = this.getCellValue(row.getCell(10));
				try {
					bvo.setDeductions_month_insure(new UFDouble(monthpay));
				} catch (Exception e) {
					sb.append("扣費當月投保金額填入非數字；");
				}
			}
			// 同年度累计金奖金金额
			if (row.getCell(11) == null) {
				sb.append("同年度累積金獎金金額不能為空；");
			} else {
				String totamny = this.getCellValue(row.getCell(11));
				try {
					bvo.setTotalbonusforyear(new UFDouble(totamny));
				} catch (Exception e) {
					sb.append("同年度累積金獎金金額填入非數字；");
				}
			}
			String error = check(bvo);
			if (!StringUtils.isEmpty(error)) {
				sb.append(error);
			}
			// 以上是各个字段导入的校验和封装
			// sb是空，证明该行数据验证通过
			if (sb.length() == 0) {
				vos.add(bvo);
			}
			// sb不为空的，证明校验和验证没通过，回写校验信息
			else {
				row.createCell(13);
				row.getCell(13).setCellValue(sb.toString());
			}
		}
		if (vos.size() > 0) {
			// 按法人组织分类
			Map<String, List<NonPartTimeBVO>> sortMap = new HashMap<>();
			// 区分法人组织
			for (NonPartTimeBVO bvo : vos) {
				if (sortMap.containsKey(bvo.getPk_org())) {
					sortMap.get(bvo.getPk_org()).add(bvo);
				} else {
					List<NonPartTimeBVO> bvoList = new ArrayList<>();
					bvoList.add(bvo);
					sortMap.put(bvo.getPk_org(), bvoList);
				}
			}
			for (String pk_org : sortMap.keySet()) {
				AggDeclarationVO aggvo = new AggDeclarationVO();
				DeclarationHVO parent = new DeclarationHVO();
				parent.setPk_group(this.getContext().getPk_group());
				parent.setPk_org(pk_org);
				aggvo.setParent(parent);
				aggvo.setChildrenVO(sortMap.get(pk_org).toArray(new NonPartTimeBVO[0]));
				aggvoList.add(aggvo);
			}
			return true;
		}
		return false;
	}

	/**
	 * 一些统一校验
	 * 
	 * @param bvo
	 * @throws BusinessException
	 */
	private String check(NonPartTimeBVO bvo) throws BusinessException {

		StringBuilder sb = new StringBuilder();
		// 投保人必须要有该法人组织的健保投保记录
		String errorStr = checkHealth(bvo.getVbdef1(), bvo.getVbdef4());
		if (!StringUtils.isEmpty(errorStr)) {
			sb.append(errorStr);
		}
		return sb.toString();
	}

	private String checkHealth(String pk_org, String pk_psndoc) throws BusinessException {
		Integer num = 0;
		if (psnOrgHealthMap.containsKey(pk_psndoc + "::" + pk_org)) {
			num = psnOrgHealthMap.get(pk_psndoc + "::" + pk_org);
		} else {
			String sql = "select count(*) from hi_psndoc_glbdef2 where pk_psndoc = '" + pk_psndoc
					+ "' and legalpersonorg = '" + pk_org + "' and dr = 0";
			IUAPQueryBS bs = NCLocator.getInstance().lookup(IUAPQueryBS.class);
			num = (Integer) bs.executeQuery(sql, new ColumnProcessor());
		}

		if (null == num || num <= 0) {
			return "人员在该法人组织下没有健保投保记录；";
		}
		return null;
	}

	/**
	 * 根据id查询人员
	 * 
	 * @param id
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private String getPsnByID(String id) throws BusinessException {
		if (this.psnMap.size() == 0) {
			this.psnMap = (Map<String, String>) NCLocator.getInstance().lookup(ITwhr_declarationMaintain.class)
					.getPkByCode(null, "psn", this.getContext());
		}
		return this.psnMap.get(id);
	}

	/**
	 * 获取薪资期间
	 * 
	 * @param perdate
	 * @return
	 * @throws BusinessException
	 */
	private String getPeriod(String perdate) throws BusinessException {
		if (perMap.get(perdate) != null) {
			return perMap.get(perdate);
		}
		String perpk = (String) NCLocator.getInstance().lookup(ITwhr_declarationMaintain.class)
				.getPkByCode(perdate, "period", this.getContext());
		if (perpk != null) {
			perMap.put(perdate, perpk);
		}
		return perpk;
	}

	/**
	 * 薪资方案
	 * 
	 * @param wacode
	 * @return
	 * @throws BusinessException
	 */
	private String getWaClass(String wacode) throws BusinessException {
		if (waclaMap.get(wacode) != null) {
			return waclaMap.get(wacode);
		}
		String wapk = (String) NCLocator.getInstance().lookup(ITwhr_declarationMaintain.class)
				.getPkByCode(wacode, "waclass", this.getContext());
		if (wapk != null) {
			waclaMap.put(wacode, wapk);
		}
		return wapk;
	}

	/**
	 * 获取法人公司
	 * 
	 * @param code
	 * @return
	 * @throws BusinessException
	 */
	private OrgVO getCorpByCode(String code) throws BusinessException {
		if (corpMap.get(code) != null) {
			return corpMap.get(code);
		}
		// 法人公司编码查询法人公司vo
		OrgVO corp = (OrgVO) NCLocator.getInstance().lookup(ITwhr_declarationMaintain.class)
				.getPkByCode(code, "corp", this.getContext());
		if (corp != null)
			corpMap.put(code, corp);
		return corp;
	}

	/**
	 * 根据部门编码查询部门pk
	 * 
	 * @param deptCode
	 * @return
	 * @throws BusinessException
	 */
	private String getDeptPK(String deptCode) throws BusinessException {
		if (deptMap.get(deptCode) != null) {
			return deptMap.get(deptCode);
		}
		// 根据部门编码查询部门主键
		String pk_dept = (String) NCLocator.getInstance().lookup(ITwhr_declarationMaintain.class)
				.getPkByCode(deptCode, "dept", this.getContext());
		if (pk_dept != null) {
			deptMap.put(deptCode, pk_dept);
		}
		return pk_dept;
	}

	/**
	 * 获取单元格cell的值
	 * 
	 * @param cell
	 *            单元格
	 * @return
	 */
	private String getCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		int type = cell.getCellType();
		String value = "";
		switch (type) {
		case 0:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				value = sdf.format(cell.getDateCellValue());
			} else {
				value = String.valueOf(cell.getNumericCellValue());
				if (value.indexOf("E") > 0) {
					value = parseDouble(value);
				}
			}
			break;
		case 1:
			value = cell.getStringCellValue();
			break;
		case 4:
			value = String.valueOf(cell.getBooleanCellValue());
			break;
		}
		return value;
	}

	/**
	 * 数字类型单独拿出来处理
	 * 
	 * @param strDigt
	 * @return
	 */
	private String parseDouble(String strDigt) {
		int pos = strDigt.indexOf("E");
		int dotPos = strDigt.indexOf(".");
		String betweenValue = strDigt.substring(dotPos + 1, pos);
		String temp = "";
		int power = Integer.parseInt(strDigt.substring(pos + 1));
		if (power > 0) {
			StringBuilder sbd = new StringBuilder();
			sbd.append(strDigt.substring(0, dotPos));
			for (int index = 0; index < power; index++) {
				if (index < betweenValue.length()) {
					sbd.append(betweenValue.charAt(index));
				} else {
					sbd.append("0");
				}
			}
			if (power < betweenValue.length()) {
				temp = betweenValue.substring(power);
				sbd.append("." + temp);
			}
			return sbd.toString();
		}
		return strDigt;
	}

	protected boolean isActionEnable() {
		String tableCode = null;
		String pk_org = null;
		try {
			tableCode = getBillListView().getBillListPanel().getBodyTabbedPane().getSelectedTableCode();
			pk_org = getPrimaryOrgPanel().getRefPane().getRefPK();
		} catch (Exception e) {
			pk_org = null;
			tableCode = null;
		}
		if (pk_org != null && tableCode != null && !tableCode.equals("companyadjust")) {
			return true;
		} else {
			return false;
		}
	}

	public BillListView getBillListView() {
		return billListView;
	}

	public void setBillListView(BillListView billListView) {
		this.billListView = billListView;
	}

	public nc.ui.twhr.glb.view.OrgPanel_Org getPrimaryOrgPanel() {
		return primaryOrgPanel;
	}

	public void setPrimaryOrgPanel(nc.ui.twhr.glb.view.OrgPanel_Org primaryOrgPanel) {
		this.primaryOrgPanel = primaryOrgPanel;
	}
}
