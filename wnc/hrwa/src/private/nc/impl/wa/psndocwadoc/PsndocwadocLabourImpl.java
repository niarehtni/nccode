package nc.impl.wa.psndocwadoc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.filechooser.FileFilter;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.itf.hi.PsndocDefUtil;
import nc.itf.hr.wa.IPsndocwadocLabourService;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.ui.pub.beans.UIFileChooser;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocDefVO;
import nc.vo.hi.wadoc.BatchGroupInsuranceExitVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.util.CollectionUtils;

@SuppressWarnings({ "unchecked" })
public class PsndocwadocLabourImpl implements IPsndocwadocLabourService {

	private UIFileChooser fc;
	private BaseDAO basedao = null;
	private boolean hasError = false;

	@Override
	public PsnJobVO[] queryPsnJobVOsByConditionAndOverrideOrg(List<String> pk_psndocs, String pk_orgs, String[] pk_depts)
			throws BusinessException {
		// String pk_hrorg = null;
		String sql = " select job.pk_psndoc,dept.pk_vid,job.pk_psnjob "
				+ " from (select pk_psndoc,max(begindate)begindate,pk_dept,pk_psnjob "
				+ " from hi_psnjob where enddate is null and endflag = 'N' ";

		if (!CollectionUtils.isEmpty(pk_psndocs)) {
			InSQLCreator insql = new InSQLCreator();
			String psndocsInSQL = insql.getInSQL(pk_psndocs.toArray(new String[0]));
			sql += " and pk_psndoc in (" + psndocsInSQL + ")";
		}
		// 查找此组织下的所有法人组织(要有薪资委托关系的组织才能显示)
		if (null != pk_orgs && !"".equals(pk_orgs)) {
			List<String> orgs = LegalOrgUtilsEX.getRelationOrgWithSalary(pk_orgs);
			if (!orgs.isEmpty()) {
				InSQLCreator insql = new InSQLCreator();
				String orgInSQL = insql.getInSQL(orgs.toArray(new String[0]));
				sql += " and pk_org in (" + orgInSQL + ") ";
			}
		}
		// 部门多选修改
		if (null != pk_depts && pk_depts.length > 0) {
			InSQLCreator insql = new InSQLCreator();
			String deptInSQL = insql.getInSQL(pk_depts);
			sql += " and pk_dept in (" + deptInSQL + ") ";
		}

		sql += " group by pk_psndoc,pk_dept,pk_psnjob) job "
				+ " left join org_dept_v dept on job.pk_dept = dept.pk_dept ";

		List<Object[]> psndocList = (List<Object[]>) new BaseDAO().executeQuery(sql, new ArrayListProcessor());
		if (CollectionUtils.isEmpty(psndocList))
			return null;
		PsnJobVO[] returnVOs = new PsnJobVO[psndocList.size()];
		for (int i = 0; i < psndocList.size(); i++) {
			if (null != psndocList.get(i)) {
				String pk_psndoc = psndocList.get(i)[0] == null ? "" : psndocList.get(i)[0].toString();
				String pk_vid = psndocList.get(i)[1] == null ? "" : psndocList.get(i)[1].toString();
				String pk_psnjob = psndocList.get(i)[2] == null ? "" : psndocList.get(i)[2].toString();
				returnVOs[i] = new PsnJobVO();
				returnVOs[i].setPk_psndoc(pk_psndoc);
				returnVOs[i].setPk_psnjob(pk_psnjob);
				returnVOs[i].setPk_dept_v(pk_vid);
				// returnVOs[i].setPk_org_v(psndocvos[i].getPk_org_v());
			}
		}

		return returnVOs;
	}

	@Override
	public Map<String, String[]> queryLabour(List<String> pk_psndocs, String laborRange, String retireRange,
			String healthRange) throws BusinessException {
		Map<String, String[]> labourMap = new HashMap<String, String[]>();
		if (CollectionUtils.isEmpty(pk_psndocs)) {
			return labourMap;
		}

		String nowDate = new UFLiteralDate(InvocationInfoProxy.getInstance().getBizDateTime()).toString();
		InSQLCreator insql = new InSQLCreator();
		String sql = " select pk_psndoc, sum(laborrange) laborrange, sum(retirerange) retirerange, sum(healthrange) healthrange from (";
		sql += " select pk_psndoc, isnull(glbdef4, 0) laborrange, 0 retirerange, 0 healthrange from "
				+ PsndocDefTableUtil.getPsnLaborTablename() + " where isnull(enddate, '9999-12-31') >= '" + nowDate
				+ "'";
		sql += " union ";
		sql += " select pk_psndoc, 0 laborrange, isnull(glbdef7, 0) retirerange, 0 healthrange from "
				+ PsndocDefTableUtil.getPsnLaborTablename() + " where isnull(glbdef15, '9999-12-31') >= '" + nowDate
				+ "' ";
		sql += " union ";
		sql += " select pk_psndoc, 0 laborrange, 0 retirerange, isnull(glbdef16, 0) healthrange from "
				+ PsndocDefTableUtil.getPsnHealthTablename() + " where isnull(enddate, '9999-12-31') >= '" + nowDate
				+ "' ";
		sql += " ) tmp ";
		sql += " where pk_psndoc in (" + insql.getInSQL(pk_psndocs.toArray(new String[0])) + ") ";
		sql += " group by pk_psndoc";
		List<Object[]> labourList = (List<Object[]>) new BaseDAO().executeQuery(sql, new ArrayListProcessor());
		if (CollectionUtils.isEmpty(labourList))
			return labourMap;
		for (int i = 0; i < labourList.size(); i++) {
			if (null != labourList.get(i)) {
				String pk_psndoc = labourList.get(i)[0] == null ? "" : labourList.get(i)[0].toString();
				String glbdef4 = labourList.get(i)[1] == null ? "" : labourList.get(i)[1].toString();
				String glbdef7 = labourList.get(i)[2] == null ? "" : labourList.get(i)[2].toString();
				String glbdef16 = labourList.get(i)[3] == null ? "" : labourList.get(i)[3].toString();
				labourMap.put(pk_psndoc, new String[] { glbdef4, glbdef7, glbdef16 });
			}
		}
		return labourMap;
	}

	// MOD 张恒 根据code找到自定义档案参照的name值 2018/9/17
	@Override
	public String queryRefNameByCode(String code) throws BusinessException {
		// TODO Auto-generated method stub
		String sql = " select pk_defdoclist from bd_defdoclist where code = '" + code + "'";
		String pk_defdoclist = new BaseDAO().executeQuery(sql, new ColumnProcessor()) == null ? "" : new BaseDAO()
				.executeQuery(sql, new ColumnProcessor()).toString();
		return pk_defdoclist;
	}

	private BaseDAO getBaseDao() {
		if (basedao == null) {
			basedao = new BaseDAO();
		}

		return basedao;
	}

	// MOD Jimmy 2020/02/20
	// 需求更新的VO
	private List<PsndocDefVO> needUpdatedVOs = new ArrayList<PsndocDefVO>();

	@Override
	public BatchGroupInsuranceExitVO[] batchGroupInsuranceExit(List<BatchGroupInsuranceExitVO> dataVO)
			throws BusinessException {
		List<BatchGroupInsuranceExitVO> returnedVOMap = new ArrayList<BatchGroupInsuranceExitVO>();
		this.setHasError(false);

		if (dataVO != null && dataVO.size() > 0) {
			// 按員工編碼分組大小
			int groupSize = 100;
			// //Map<員工編碼, VOList>
			Map<String, List<BatchGroupInsuranceExitVO>> groupedImportDataVOMap = new HashMap<String, List<BatchGroupInsuranceExitVO>>();
			int curSize = 0;
			// 分組：按員工編碼分組
			for (BatchGroupInsuranceExitVO vo : dataVO) {
				String psnCode = vo.getPsncode();
				if (!groupedImportDataVOMap.containsKey(psnCode)) {
					groupedImportDataVOMap.put(psnCode, new ArrayList<BatchGroupInsuranceExitVO>());
				}
				groupedImportDataVOMap.get(psnCode).add(vo);
				curSize++;

				if (curSize == groupSize) {
					// 達到分組大小的開始處理退保
					returnedVOMap.addAll(dealGroupInsuranceExit(groupedImportDataVOMap));
					curSize = 0;
					groupedImportDataVOMap = new HashMap<String, List<BatchGroupInsuranceExitVO>>();
				}
			}

			if (curSize > 0) {
				// 最後一批但未達分組大小的處理退保
				returnedVOMap.addAll(dealGroupInsuranceExit(groupedImportDataVOMap));
			}

			if (!this.isHasError()) {
				if (needUpdatedVOs != null && needUpdatedVOs.size() > 0) {
					PersistenceManager sessionManager = null;
					try {
						sessionManager = PersistenceManager.getInstance();
						JdbcSession session = sessionManager.getJdbcSession();
						for (PsndocDefVO vo : needUpdatedVOs) {
							String strSQL = "update " + PsndocDefTableUtil.getGroupInsuranceTablename()
									+ " set enddate='" + vo.getEnddate().toString() + "', ts='"
									+ new UFDateTime().toString() + "', modifier='"
									+ InvocationInfoProxy.getInstance().getUserId() + "', modifiedtime='"
									+ new UFDateTime().toString() + "', glbdef7='Y' where pk_psndoc_sub='"
									+ vo.getPk_psndoc_sub() + "'";
							session.addBatch(strSQL, new SQLParameter());
						}
						session.executeBatch();
					} catch (DbException e) {
						e.printStackTrace();
					} finally {
						if (sessionManager != null) {
							sessionManager.release();
						}
					}
				}
			}
		}

		return returnedVOMap.toArray(new BatchGroupInsuranceExitVO[0]);
	}

	private List<BatchGroupInsuranceExitVO> dealGroupInsuranceExit(
			Map<String, List<BatchGroupInsuranceExitVO>> groupedImportDataVOs) throws BusinessException {
		List<BatchGroupInsuranceExitVO> returnedVOList = new ArrayList<BatchGroupInsuranceExitVO>();
		String insql = new InSQLCreator().getInSQL(groupedImportDataVOs.keySet().toArray(new String[0]));

		Collection<PsndocDefVO> groupInsVOList = getBaseDao().retrieveByClause(
				PsndocDefUtil.getGroupInsuranceVO().getClass(),
				"pk_psndoc in (select pk_psndoc from bd_psndoc where code in (" + insql + "))");

		List<Map<String, Object>> codeMapList = (List<Map<String, Object>>) this.getBaseDao().executeQuery(
				"select code, pk_psndoc from bd_psndoc where code in (" + insql + ")", new MapListProcessor());

		for (Entry<String, List<BatchGroupInsuranceExitVO>> entryGrouped : groupedImportDataVOs.entrySet()) {
			String pk_psndoc = getPk_psndoc(entryGrouped.getKey(), codeMapList);
			PsndocDefVO[] groupInsVOs = getGroupInsVOByPsn(groupInsVOList, pk_psndoc);
			for (BatchGroupInsuranceExitVO importVO : entryGrouped.getValue()) {
				boolean dealed = false;
				for (PsndocDefVO groupInsVO : groupInsVOs) {
					if (groupInsVO.getAttributeValue("glbdef2") != null
							&& groupInsVO.getAttributeValue("glbdef2").equals(importVO.getIdno())) { // 身份證
						if (getGroupInsCode((String) groupInsVO.getAttributeValue("glbdef5")).equals(
								importVO.getInsurancecode())) { // 險種
							if (groupInsVO.getEnddate() == null
									|| groupInsVO.getEnddate().toString().startsWith("9999")) {
								// 員工PK相同，身份證相同，險種相同視為找到投保記錄
								// 處理投保結束日期
								groupInsVO.setEnddate(importVO.getExitdate());
								groupInsVO.setStatus(VOStatus.UPDATED);
								needUpdatedVOs.add(groupInsVO);
								dealed = true;
								break;
							} else if (groupInsVO.getEnddate().isSameDate(importVO.getExitdate())) {
								importVO.setErrmessage("員工號 [" + importVO.getClerkcode() + "] ，其退保人已於文檔指定日期退保，本次作業忽略。");
								returnedVOList.add(importVO);
								dealed = true;
								break;
							}
							// else if
							// (groupInsVO.getEnddate().after(importVO.getExitdate()))
							// {
							// importVO.setErrmessage("員工號 [" +
							// importVO.getClerkcode() + "] ，其退保人退保日期 ["
							// + groupInsVO.getEnddate().toString() +
							// "] 晚於文檔指定日期 ["
							// + importVO.getExitdate().toString() + "]。");
							// this.setHasError(true);
							// returnedVOList.add(importVO);
							// break;
							// } else if
							// (groupInsVO.getEnddate().before(importVO.getExitdate()))
							// {
							// importVO.setErrmessage("員工號 [" +
							// importVO.getClerkcode() + "] ，其退保人退保日期 ["
							// + groupInsVO.getEnddate().toString() +
							// "] 早於文檔指定日期 ["
							// + importVO.getExitdate().toString() + "]。");
							// this.setHasError(true);
							// returnedVOList.add(importVO);
							// break;
							// }
						}
					}
				}

				if (!dealed) {
					importVO.setErrmessage("員工號 [" + importVO.getClerkcode() + "] ，其退保人不存在有效加保資料。");
					returnedVOList.add(importVO);
					this.setHasError(true);
				}
			}
		}

		return returnedVOList;
	}

	private String getPk_psndoc(String code, List<Map<String, Object>> codeMapList) {
		String pk_psndoc = null;
		if (codeMapList != null && codeMapList.size() > 0) {
			for (Map<String, Object> codeMap : codeMapList) {
				if (code.equals(codeMap.get("code"))) {
					pk_psndoc = (String) codeMap.get("pk_psndoc");
				}
			}
		}
		return pk_psndoc;
	}

	Map<String, String> groupInsCodeMap = null;

	private String getGroupInsCode(String pk_groupins) throws BusinessException {
		if (groupInsCodeMap == null) {
			Collection<DefdocVO> groupInsVOList = getBaseDao().retrieveByClause(DefdocVO.class,
					"pk_defdoclist=(select pk_defdoclist from bd_defdoclist where code = 'TWHR009')");

			if (groupInsVOList != null && groupInsVOList.size() > 0) {
				groupInsCodeMap = new HashMap<String, String>();

				for (DefdocVO groupInsVO : groupInsVOList) {
					groupInsCodeMap.put(groupInsVO.getPk_defdoc(), groupInsVO.getCode());
				}
			}
		}
		return groupInsCodeMap.containsKey(pk_groupins) ? groupInsCodeMap.get(pk_groupins) : StringUtils.EMPTY;
	}

	private PsndocDefVO[] getGroupInsVOByPsn(Collection<PsndocDefVO> groupInsVOList, String pk_psndoc) {
		List<PsndocDefVO> returnVOList = new ArrayList<PsndocDefVO>();
		if (groupInsVOList != null && groupInsVOList.size() > 0) {
			for (PsndocDefVO vo : groupInsVOList) {
				if (vo.getPk_psndoc().equals(pk_psndoc)) {
					returnVOList.add(vo);
				}
			}
		}
		return returnVOList.toArray(new PsndocDefVO[0]);
	}

	private void export(String path) throws BusinessException {
		File cresteFile = new File(path);
		if (cresteFile.exists()) {
		}
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(path);
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();

			HSSFCellStyle cs = wb.createCellStyle();
			HSSFFont littleFont = wb.createFont();
			littleFont.setFontName("SimSun");
			littleFont.setFontHeightInPoints((short) 10);
			cs.setFont(littleFont);
			cs.setAlignment((short) 2);
			cs.setVerticalAlignment((short) 1);
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = row.createCell((short) 0);
			cell.setCellValue(new HSSFRichTextString("人員編碼"));
			cell.setCellStyle(cs);
			cell = row.createCell((short) 1);
			cell.setCellValue(new HSSFRichTextString("員工號"));
			cell.setCellStyle(cs);
			cell = row.createCell((short) 2);
			cell.setCellValue(new HSSFRichTextString("退保人身份證號"));
			cell.setCellStyle(cs);
			cell = row.createCell((short) 3);
			cell.setCellValue(new HSSFRichTextString("險種編號"));
			cell.setCellStyle(cs);
			cell = row.createCell((short) 4);
			cell.setCellValue(new HSSFRichTextString("退保日期"));
			cell.setCellStyle(cs);

			HSSFCellStyle cs2 = wb.createCellStyle();
			HSSFFont littleFont2 = wb.createFont();
			littleFont2.setFontName("SimSun");
			littleFont2.setFontHeightInPoints((short) 10);
			cs2.setFont(littleFont2);
			cs2.setAlignment((short) 2);
			cs2.setVerticalAlignment((short) 1);

			wb.write(fileOut);

			// ShowStatusBarMsgUtil.showStatusBarMsg(
			// ResHelper.getString("6007psn", "06007psn0447"),
			// getContext());
			return;
		} catch (Exception ex) {
			throw new BusinessException(ex.getMessage());
		} finally {
			try {
				if (fileOut != null) {
					fileOut.close();
				}
			} catch (Exception ex) {
			}
		}
	}

	class ExportThread extends Thread {
		String path;

		ExportThread(String path) {
			this.path = path;
		}

		public void run() {
			try {
				PsndocwadocLabourImpl.this.export(path);
			} catch (Exception ex) {
			}
		}
	}

	public UIFileChooser getFc() {
		if (fc == null) {
			fc = new UIFileChooser();
			fc.setAcceptAllFileFilterUsed(false);
			fc.setFileFilter(new FileFilter() {
				String type = "*.xls";
				String ext = ".xls";

				public boolean accept(File f) {
					if (f.isDirectory()) {
						return true;
					}
					return f.getName().endsWith(ext);
				}

				public String getDescription() {
					if (type.equals("*.xls")) {
						return ResHelper.getString("6007psn", "06007psn0392");
					}
					return null;
				}
			});
		}
		return fc;
	}

	/**
	 * @return hasError
	 */
	public boolean isHasError() {
		return hasError;
	}

	/**
	 * @param hasError
	 *            要設定的 hasError
	 */
	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}
}
