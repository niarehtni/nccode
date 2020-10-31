package nc.ui.wa.datainterface.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pubitf.twhr.IBasedocPubQuery;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.wa.datainterface.AddInsuranceExportTextDlg;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.twhr.basedoc.BaseDocVO;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

@SuppressWarnings("restriction")
public class AddInsuranceExportAction extends HrAction {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = -3937249333170798137L;
	private static String baseFilePath = ""; // �洢�ļ���·��
	String startPeriod = ""; // ����
	String hrOrg = "";// �����YԴ�M��
	String legalOrg = "";// ���˽M��

	public AddInsuranceExportAction() {
		super();
		super.setBtnName(ResHelper.getString("twhr_datainterface", "DataInterface-00079")); // �ͽ�������һ�ӱ�
		super.setCode("AddInsuranceExportAction");
		super.putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("twhr_datainterface", "DataInterface-00079")); // �ͽ�������һ�ӱ�
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		String pk_org = this.getModel().getContext().getPk_org();
		if (pk_org == null) {
			throw new BusinessException(ResHelper.getString("twhr_datainterface", "DataInterface-00084"));// ��ѡ����֯
		}
		AddInsuranceExportTextDlg dlg = new AddInsuranceExportTextDlg();
		int isGen = dlg.showModal();
		if (isGen == UIDialog.ID_OK) {
			String filepath = getExportFilePath("����һ�ӱ�.xls");
			if (null == filepath) {
				return;
			}
			if (dlg.getDateTime() == null && "".equals(dlg.getDateTime())) {
				throw new BusinessException(ResHelper.getString("twhr_datainterface", "DataInterface-00082"));// ��ѡ������
			} else if (null == dlg.getLegalOrg() && null == dlg.getHrOrg()) {
				throw new BusinessException(ResHelper.getString("twhr_datainterface", "DataInterface-00085"));// ��ѡ��������Դ��֯������֯
			}
			startPeriod = dlg.getDateTime().substring(0, 10);
			hrOrg = dlg.getHrOrg();
			legalOrg = dlg.getLegalOrg();

			Set<String> pkRealOrgSet = new HashSet<>();
			if (null != hrOrg) {
				pkRealOrgSet = LegalOrgUtilsEX.getOrgsByLegal(hrOrg, this.getModel().getContext().getPk_group());
			} else {
				// legalOrg = dlg.getLegalOrg();
				pkRealOrgSet.add(legalOrg);
			}
			baseFilePath = getExportFileBasePath();
			StringBuilder errorMsgSb = new StringBuilder();
			// ������֯��Ϣ
			Map<String, OrgVO> orgMap = LegalOrgUtilsEX.getOrgInfo(pkRealOrgSet.toArray(new String[0]));
			for (String pk_legalOrg : pkRealOrgSet) {
				OrgVO org = orgMap.get(pk_legalOrg);
				if (null == org) {
					continue;
				}
				filepath = getExportFilePath("����һ�ӱ�_" + org.getCode() + ".xls");
				if (null == filepath) {
					return;
				}
				// ������ز���:
				// �ڹ����U�C̖(8λ����)
				BaseDocVO TWNHHI01 = NCLocator.getInstance().lookup(IBasedocPubQuery.class)
						.queryBaseDocByCode(pk_legalOrg, "TWNHHI01");
				// �ڹ����U�C̖�z��a(1λӢ����ĸ)
				BaseDocVO TWNHHI02 = NCLocator.getInstance().lookup(IBasedocPubQuery.class)
						.queryBaseDocByCode(pk_legalOrg, "TWNHHI02");
				// ����Ͷ����λ��̖
				BaseDocVO TWNHHI03 = NCLocator.getInstance().lookup(IBasedocPubQuery.class)
						.queryBaseDocByCode(pk_legalOrg, "TWNHHI03");
				// �����I�սM�e
				BaseDocVO TWNHHI04 = NCLocator.getInstance().lookup(IBasedocPubQuery.class)
						.queryBaseDocByCode(pk_legalOrg, "TWNHHI04");
				if (null == TWNHHI01 || null == TWNHHI01.getTextvalue()) {
					errorMsgSb.append(" �����^�M��:").append(org.getName()).append(".ԭ��,δ�O�Å���TWNHHI01.\n");
					continue;
				} else if (null == TWNHHI02 || null == TWNHHI02.getTextvalue()) {
					errorMsgSb.append(" �����^�M��:").append(org.getName()).append(".ԭ��,δ�O�Å���TWNHHI02.\n");
					continue;
				} else if (null == TWNHHI03 || null == TWNHHI03.getTextvalue()) {
					errorMsgSb.append(" �����^�M��:").append(org.getName()).append(".ԭ��,δ�O�Å���TWNHHI03.\n");
					continue;
				} else if (null == TWNHHI04 || null == TWNHHI04.getRefvalue()) {
					errorMsgSb.append(" �����^�M��:").append(org.getName()).append(".ԭ��,δ�O�Å���TWNHHI04.\n");
					continue;
				}

				// startPeriod = "2018-01-30";
				IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
				String sql = "select 4 as move, '"
						+ TWNHHI01.getTextvalue()
						+ "' as insnum,"
						+ " '"
						+ TWNHHI02.getTextvalue()
						+ "' as checkcode, "
						+ " '"
						+ TWNHHI03.getTextvalue()
						+ "' as unitcode, "
						+ " case when g3.glbdef2='����'then 2 else 3 end as inssort, "
						+ " case when g3.glbdef2='����'then 1 else 2 end as insured, "
						+ " case doc.code  "
						+ " when 'LT06' then 'Y' "
						+ " when 'LT13' then '1'  "
						+ " when 'LT14' then '2' "
						+ " else '' end as insforeign, "
						+ " a.id as id,a.name as insname,a.birthdate as birthdate, "
						+ " g2.glbdef2 as wage,"
						+ " case when g3.glbdef16 is null "
						+ " then  "
						+ " (select h.glbdef16 from "
						+ PsndocDefTableUtil.getPsnHealthTablename()
						+ " h left join bd_psndoc b on b.pk_psndoc=h.pk_psndoc "
						+ " where b.pk_psndoc=a.pk_psndoc and b.name2=a.name2 and rownum=1) "
						+ " else g3.glbdef16 end  AS inssalary,"
						+ " doc4.code as special,"
						+ " case when doc2.code = 3 then '0' else '' end  as lfspecial , "
						+ " case when g3.glbdef2='����' then '' "
						+ " when g3.glbdef2 is null then '' "
						+ " else g3.glbdef3 "
						+ " end  as jsid, "
						+ " case when g3.glbdef2='����' then '' "
						+ " when g3.glbdef2 is null then '' "
						+ " else g3.glbdef1 "
						+ " end  as jsname, "
						+ " case when g3.glbdef2='����' then '' "
						+ " when g3.glbdef2 is null then '' "
						+ " else substring(g3.glbdef4,0,11) "
						+ " end  as jsbirthdate, "
						+ " case g3.glbdef2  "
						+ " when '��ż' then 1  "
						+ " when '��ĸ' then 2  "
						+ " when '��Ů' then 3  "
						+ " when '�游ĸ' then 4  "
						+ " when '�O��Ů' then 5  "
						+ " when '���游ĸ' then 6  "
						+ " when '��O��Ů' then 7  "
						+ " when '���游ĸ' then 8  "
						+ " when '�����游ĸ' then 9  "
						+ " end as title, "
						+ " d.code as business,doc3.code as reason ,g3.begindate as fsdate, "
						+ " case when  ISNULL(g2.glbdef11,'N')='N'  then '' else case doc.code  "
						+ " when 'LT06' then case a.sex when 1 then 'M'when 2 then 'F'end  "
						+ " when 'LT13' then case a.sex when 1 then 'M'when 2 then 'F'end  "
						+ " when 'LT14' then case a.sex when 1 then 'M'when 2 then 'F'end  "
						+ " end end as sex, "
						// û������Ͷ��ʱ,����ʾ����
						+ " CASE "
						+ " WHEN g3.glbdef2 = '����' THEN case when ISNULL(g2.glbdef11,'N')='N'   then '' else doc2.code end "
						+ " ELSE '' "
						+ " END   as tjid, "
						// û������Ͷ��ʱ,����ʾ����
						+ " case when g3.glbdef2='����' then case when  ISNULL(g2.glbdef11,'N')='N'  then '' else  '6' end else ''end  as gztjl, "
						+ " case when  ISNULL(g2.glbdef11,'N')='N'  then null else case when g3.glbdef2='����' then "
						+ " case when g2.glbdef8 =0 then null "
						+ " else g2.glbdef8 end "
						+ "  else null end end as grtjl,  "
						+ " case when  ISNULL(g2.glbdef11,'N')='N'  then '' else case when g2.begindate = g2.glbdef14 then '' else g2.glbdef14 end end as lttjdate "
						+ " from bd_psndoc a  "
						+ " left join org_orgs org on org.pk_org = a.pk_org "
						+ " left join org_hrorg hr on org.code = hr.code "
						+ " left join bd_defdoc d on d.pk_defdoc =   '"
						+ TWNHHI04.getRefvalue()
						+ "' "
						+ " left join "
						+ PsndocDefTableUtil.getPsnHealthTablename()
						+ " g3 on g3.pk_psndoc = a.pk_psndoc "
						+ " left join bd_defdoc doc3 on g3.glbdef17= doc3.pk_defdoc "
						+ " left join "
						+ PsndocDefTableUtil.getPsnLaborTablename()
						+ " g2 on g2.pk_psndoc = a.pk_psndoc  "
						+ " left join bd_defdoc doc on g2.glbdef1= doc.pk_defdoc "
						+ " left join bd_defdoc doc2 on g2.glbdef17= doc2.pk_defdoc "
						+ " left join bd_defdoc doc4 on g2.glbdef16 = doc4.pk_defdoc "
						// +" where a.code in('TP1006008','TP1712008')"
						// tank
						// ����begindate��ʽ��ȡ���µĹ�����¼,��ʱ����������,lastflag='Y'�ļ�¼ͬһ���˻�������
						+ " INNER JOIN  hi_psnjob psnjob ON psnjob.PK_PSNDOC = a.PK_PSNDOC "
						+
						// MOD by ssx on 2020-04-30
						// ͬ����ְ��ְ���˵���ְ��¼
						" and psnjob.trnsevent<>4 "
						// end MOD
						+ " and psnjob.BEGINDATE = (select max(BEGINDATE) from hi_psnjob where  pk_org = '"
						+ pk_legalOrg + "' and trnsevent<>4 and "

						+ "dr = 0 and PK_PSNDOC = a.PK_PSNDOC) "
						+ " inner join bd_defdoc dfc on  dfc.PK_DEFDOC=psnjob.JOBGLBDEF8 " + " where g2.begindate = '"
						+ startPeriod + "'"
						+ " and g2.begindate=(select max(gl.begindate) from bd_psndoc ps left join "
						+ PsndocDefTableUtil.getPsnLaborTablename()
						+ " gl on gl.pk_psndoc = ps.pk_psndoc where ps.pk_psndoc=a.pk_psndoc) "
						+ " and g2.begindate = g3.begindate " + " and g3.glbdef14='Y' and g2.glbdef10 = 'Y' "
						// Ares.Tank 2018-9-13 11:34:34 �⼮��Ա�ͽ�����Ա û������,ҲҪ�ӱ�
						+ " and (g2.glbdef11 = 'Y' or  dfc.CODE = 'F' or dfc.CODE = 'G') "
						// �Է�����֯���й���,��������,�����ڱ��˺���
						+ " and g2.legalpersonorg ='" + pk_legalOrg + "' order by a.pk_psndoc ,inssalary DESC";
				List<Map<String, Object>> list = (List<Map<String, Object>>) query.executeQuery(sql,
						new MapListProcessor());
				InputStream instream = new FileInputStream(getPath() + "coverage.xls");
				HSSFWorkbook wb = new HSSFWorkbook(instream);
				HSSFSheet sheet = wb.getSheetAt(0);
				HSSFDataFormat format = wb.createDataFormat();
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						Map<String, Object> map = list.get(i);
						HSSFRow row = sheet.createRow(i + 1);
						DecimalFormat df = new DecimalFormat("######0.00");
						DecimalFormat dfInt = new DecimalFormat("0");
						for (int j = 0; j < 26; j++) {
							HSSFCell cell = row.createCell(j);
							switch (j) {
							case 0:
								cell.setCellValue(map.get("move") == null ? "" : map.get("move").toString());
								break;
							case 1:
								cell.setCellValue(map.get("insnum") == null ? "" : map.get("insnum").toString());
								break;
							case 2:
								cell.setCellValue(map.get("checkcode") == null ? "" : map.get("checkcode").toString());
								break;
							case 3:
								cell.setCellValue(map.get("unitcode") == null ? "" : map.get("unitcode").toString());
								break;
							case 4:
								cell.setCellValue(map.get("inssort") == null ? "" : map.get("inssort").toString());
								break;
							case 5:
								cell.setCellValue(map.get("insured") == null ? "" : map.get("insured").toString());
								break;
							case 6:
								cell.setCellValue(map.get("insforeign") == null ? "" : map.get("insforeign").toString());
								break;
							case 7:
								cell.setCellValue(map.get("id") == null ? "" : map.get("id").toString());
								break;
							case 8:
								cell.setCellValue(map.get("insname") == null ? "" : map.get("insname").toString());
								break;
							case 9:
								cell.setCellValue(adYearToRepublicOfChina(map.get("birthdate")));
								break;
							case 10:
								cell.setCellValue((map.get("wage") == null ? dfInt.format(0) : dfInt
										.format(getDecryptedDecimalValue(map.get("wage")))));
								break;
							case 11:
								cell.setCellValue((map.get("inssalary") == null ? "" : dfInt.format(
										getDecryptedDecimalValue(map.get("inssalary"))).equals("0") ? "" : dfInt
										.format(getDecryptedDecimalValue(map.get("inssalary")))));
								break;
							case 12:
								cell.setCellValue(map.get("special") == null ? "" : map.get("special").toString());
								break;
							case 13:
								cell.setCellValue(map.get("lfspecial") == null ? "" : map.get("lfspecial").toString());
								break;
							case 14:
								cell.setCellValue(map.get("jsid") == null ? "" : map.get("jsid").toString());
								break;
							case 15:
								cell.setCellValue(map.get("jsname") == null ? "" : map.get("jsname").toString());
								break;
							case 16:
								cell.setCellValue(adYearToRepublicOfChina(map.get("jsbirthdate")));
								break;
							case 17:
								cell.setCellValue(map.get("title") == null ? "" : map.get("title").toString());
								break;
							case 18:
								cell.setCellValue(map.get("business") == null ? "" : map.get("business").toString());
								break;
							case 19:
								cell.setCellValue(map.get("reason") == null ? "" : map.get("reason").toString());
								break;
							case 20:
								cell.setCellValue(adYearToRepublicOfChina(map.get("fsdate")));
								break;
							case 21:
								cell.setCellValue(map.get("sex") == null ? "" : map.get("sex").toString());
								break;
							case 22:
								cell.setCellValue(map.get("tjid") == null ? "" : map.get("tjid").toString());
								break;
							case 23:
								cell.setCellValue(map.get("gztjl") == null ? "" : map.get("gztjl").toString());
								break;
							case 24:
								cell.setCellValue(map.get("grtjl") == null ? "" : df
										.format(getDecryptedDecimalValue(map.get("grtjl"))));
								break;
							case 25:
								cell.setCellValue(adYearToRepublicOfChina(map.get("lttjdate")));
								break;
							default:
								break;
							}
						}
					}
				}
				try {
					FileOutputStream outstream = new FileOutputStream(filepath);
					// �½�һ������ļ���
					wb.write(outstream);// ���й������洢

					outstream.flush();
					outstream.close();
					// Runtime CRuntime = Runtime.getRuntime();
					// Process CProce = CRuntime.exec("cmd /c start " +
					// filepath);
					// �رս���
					// CProce.waitFor();
					// CProce.destroy();
				} catch (Exception e) {
					Logger.error(e.getMessage());
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"twhr_datainterface", "DataInterface-00086")/* "�l���e�`��Ո�z�飡" */);
				}
			}
			if (errorMsgSb != null && errorMsgSb.length() > 0) {
				MessageDialog.showHintDlg(null, "��ʾ", errorMsgSb.toString());
			}
		} else if (isGen == UIDialog.ID_CANCEL) {
			this.putValue("message_after_action", ResHelper.getString("twhr_datainterface", "DataInterface-00083")); // �C�������n������ȡ��
		}
	}

	private BigDecimal getDecryptedDecimalValue(Object number) {
		return BigDecimal.valueOf((number == null) ? 0
				: SalaryDecryptUtil.decrypt((new UFDouble(String.valueOf(number)).doubleValue())));
	}

	@Override
	protected boolean isActionEnable() {
		return true;
	}

	/**
	 * ��Ԫ��ת����� ��ʽ ��2018-01-01 to 1070101
	 * 
	 * @param string
	 * @return
	 */
	public static String adYearToRepublicOfChina(Object string) {
		String date = "";
		if (string != null && !"".equals(string)) {
			String str = string.toString();
			String[] split = str.split("-");
			String year = String.valueOf(Integer.parseInt(split[0]) - 1911);
			if (year.length() == 1) {
				year = "00" + year;
			} else if (year.length() == 2) {
				year = "0" + year;
			}
			date = year + split[1] + split[2];
		}
		return date;
	}

	public static String getExportFileBasePath() {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("*.xls", "xls");
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(filter);
		fc.setMultiSelectionEnabled(false);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = fc.showSaveDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			// baseFilePath = file.getPath();
			return file.getPath();
		}
		return null;
	}

	public static String getExportFilePath(String fileName) {

		return baseFilePath + java.io.File.separator + fileName;
	}

	public String getPath() {
		return this.getClass().getResource("").getPath();
	}
}
