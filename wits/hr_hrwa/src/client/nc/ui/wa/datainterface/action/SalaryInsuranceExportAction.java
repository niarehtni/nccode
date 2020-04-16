package nc.ui.wa.datainterface.action;

import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.UIDialog;
import nc.ui.wa.datainterface.AddInsuranceExportTextDlg;
import nc.ui.wa.util.LocalizationSysinitUtil;
import nc.vo.pub.BusinessException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

@SuppressWarnings("restriction")
public class SalaryInsuranceExportAction extends HrAction {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = -3937249333170798137L;

	String startPeriod = ""; // 起始期間

	public SalaryInsuranceExportAction() {
		super();
		super.setBtnName(ResHelper.getString("twhr_datainterface",
				"DataInterface-00081")); // 劳健退三合一薪调
		super.setCode("AddInsuranceExportAction");
		super.putValue(Action.SHORT_DESCRIPTION, ResHelper.getString(
				"twhr_datainterface", "DataInterface-00081")); // 劳健退三合一薪调
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		String pk_org = this.getModel().getContext().getPk_org();
		if(pk_org == null){
			throw new BusinessException(ResHelper.getString("twhr_datainterface", "DataInterface-00084"));//请选择组织
		}
		AddInsuranceExportTextDlg dlg = new AddInsuranceExportTextDlg();
		int isGen = dlg.showModal();
		if (isGen == UIDialog.ID_OK) {
			String filepath = AddInsuranceExportAction.getExportFilePath("三合一保薪調整.xls");
			if(null==filepath){
				return;
			}
			if(dlg.getDateTime() == null && "".equals(dlg.getDateTime())){
				throw new BusinessException(ResHelper.getString("twhr_datainterface", "DataInterface-00082"));//请选择日期
			}
			startPeriod = dlg.getDateTime().substring(0,10);
//			startPeriod = "2018-01-30";
			IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
			String sql ="select 3 as move, hr."+LocalizationSysinitUtil.getTwhrzzxx("TWHRZZXX01")+" as insnum,"
					+ " hr."+LocalizationSysinitUtil.getTwhrzzxx("TWHRZZXX02")+" as checkcode,"
					+ " hr."+LocalizationSysinitUtil.getTwhrzzxx("TWHRZZXX03")+" as unitcode, doc1.code as business, "
					+ " case doc2.code  "
					+ " when 'LT06' then 'Y' "
					+ " when 'LT13' then '1'  "
					+ " when 'LT14' then '2' "
					+ " else '' end as insforeign, "
					+ " ps.name2 as insname, ps.id as id, "
					+ " case doc2.code  "
					+ " when 'LT06' then ps.id "
					+ " when 'LT13' then ps.id  "
					+ " when 'LT14' then ps.id "
					+ " else '' end as wjid, "
					+ " ps.birthdate as birthdate, "
					+ " case when g2.glbdef2 is null then g3.glbdef16 "
					+ " else g2.glbdef2 end as salary, "
					+ " (SELECT top 1 glbdef16 FROM hi_psndoc_glbdef3 "
					// 三合一媒體檔保薪調整產出內容錯誤  20190730 George 缺陷Bug #25168
					// 健保調"前"投保欄位與健保調"後"投保欄位,兩個欄位數值不應皆為調整後級距
					// 不能以名子做判斷，以防更名，要以"本人"作為判斷依據 
					+ " WHERE pk_psndoc = ps.pk_psndoc and glbdef2 = '本人' and creationtime not in "
					+ " (SELECT top 1 creationtime FROM hi_psndoc_glbdef3 WHERE pk_psndoc = ps.pk_psndoc and glbdef2 = '本人' order by creationtime desc)"
					+ " order by creationtime desc) as oldsalary, "
					+ " g3.glbdef16 as newsalary, "
					+ " doc3.code as tssf "
					+ " from bd_psndoc ps "
					+ " left join org_orgs org on org.pk_org = ps.pk_org "
					+ " left join org_hrorg hr on org.code = hr.code "
					+ " left join bd_defdoc doc1 on hr."+LocalizationSysinitUtil.getTwhrzzxx("TWHRZZXX04")+" = doc1.pk_defdoc "
					+ " left join hi_psndoc_glbdef2 g2 on g2.pk_psndoc = ps.pk_psndoc  "
					+ " left join bd_defdoc doc2 on g2.glbdef1= doc2.pk_defdoc "
					+ " left join hi_psndoc_glbdef3 g3 on g3.pk_psndoc = ps.pk_psndoc  "
					+ " LEFT JOIN bd_defdoc doc3 ON g2.glbdef16 = doc3.pk_defdoc "
					+ " where  ps.pk_psndoc = g3.pk_psndoc "
					+ " and g3.glbdef2 = '本人'  "
					+ " and g2.begindate = g3.begindate  "
					+ " and (g3.recordnum=0  or (select max(recordnum) from hi_psndoc_glbdef3  where pk_psndoc=ps.pk_psndoc) >0) "
					+ " and (select count(1) from hi_psndoc_glbdef2 where pk_psndoc = ps.pk_psndoc)>1 "
					+ " and hr.pk_hrorg ='"+ pk_org +"' and  g2.begindate='" + startPeriod + "'";
			List<Map<String,Object>> list = (List<Map<String, Object>>) query.executeQuery(sql, new MapListProcessor());
			InputStream instream = new FileInputStream(getPath()+"salary.xls");
			HSSFWorkbook wb = new HSSFWorkbook(instream);
			HSSFSheet sheet = wb.getSheetAt(0);
			DecimalFormat dfInt = new DecimalFormat("0");
			if(list!=null && list.size()>0){
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> map = list.get(i);
					HSSFRow row = sheet.createRow(i+1);
					for (int j = 0; j < 14; j++) {
						HSSFCell cell = row.createCell(j);
						switch (j) {
						case 0:
							cell.setCellValue(map.get("move") == null ?"":map.get("move").toString());
							break;
						case 1:
							cell.setCellValue(map.get("insnum") == null ?"":map.get("insnum").toString());
							break;
						case 2:
							cell.setCellValue(map.get("checkcode") == null ?"":map.get("checkcode").toString());
							break;
						case 3:
							cell.setCellValue(map.get("unitcode") == null ?"":map.get("unitcode").toString());
							break;
						case 4:
							cell.setCellValue(map.get("business") == null ?"":map.get("business").toString());
							break;
						case 5:
							cell.setCellValue(map.get("insforeign") == null ?"":map.get("insforeign").toString());
							break;
						case 6:
							cell.setCellValue(map.get("insname") == null ?"":map.get("insname").toString());
							break;
						case 7:
							cell.setCellValue(map.get("id") == null ?"":map.get("id").toString());
							break;
						case 8:
							cell.setCellValue(map.get("wjid") == null ?"":map.get("wjid").toString());
							break;
						case 9:
							cell.setCellValue(AddInsuranceExportAction.adYearToRepublicOfChina(map.get("birthdate")));
							break;
						case 10:
							cell.setCellValue(map.get("salary") == null ?dfInt.format(0):dfInt.format(map.get("salary")));
							break;
						case 11:
							cell.setCellValue(map.get("oldsalary") == null ?dfInt.format(0):dfInt.format(map.get("oldsalary")));
							break;
						case 12:
							cell.setCellValue(map.get("newsalary") == null ?dfInt.format(0):dfInt.format(map.get("newsalary")));
							break;
						case 13:
							cell.setCellValue(map.get("tssf") == null ?"":map.get("tssf").toString());
							break;
						default:
							break;
						}
					}
				}
			}
			try {
				FileOutputStream outstream = new FileOutputStream(filepath);
				// 新建一个输出文件流
				wb.write(outstream);// 进行工作簿存储

				outstream.flush();
				outstream.close();
				Runtime CRuntime = Runtime.getRuntime();
				Process CProce = CRuntime.exec("cmd /c start " + filepath);
				// 关闭进程
				CProce.waitFor();
				CProce.destroy();
			} catch (Exception e) {
				Logger.error(e.getMessage());
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
						"leavebalance", "2leavebalance-000004")/*"發生錯誤，請檢查！"*/);
			}
		} else if (isGen == UIDialog.ID_CANCEL) {
			this.putValue("message_after_action", ResHelper.getString(
					"twhr_datainterface", "DataInterface-00083")); // 導出已取消
		}
	}

	@Override
	protected boolean isActionEnable() {
		return true;
	}
	 
	 public String getPath(){
		 return this.getClass().getResource("").getPath();
	 }
}
