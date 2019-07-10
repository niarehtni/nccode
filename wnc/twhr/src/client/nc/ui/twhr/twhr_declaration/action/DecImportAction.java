package nc.ui.twhr.twhr_declaration.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.twhr.ITwhr_declarationMaintain;
import nc.pub.wa.datainterface.DataItfConst;
import nc.pub.wa.datainterface.DataItfFileReader;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.twhr.twhr.action.ImportAccountAction;
import nc.ui.uif2.model.ModelDataDescriptor;
import nc.ui.wa.datainterface.action.ImportPayDataAction;
import nc.ui.wa.datainterface.view.PayDataImportDlg;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.para.DeptInnercodeVO;
import nc.vo.twhr.twhr_declaration.AggDeclarationVO;
import nc.vo.twhr.twhr_declaration.DeclarationHVO;
import nc.vo.twhr.twhr_declaration.NonPartTimeBVO;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.pub.WaLoginContext;

/**
 * ����������ʷ���ݵ���
 * @author wangywt 
 * @since 20190620
 *
 */
public class DecImportAction  extends HrAction{

	private static final long serialVersionUID = -1542523256471536559L;
	
	//���ڸ�ʽ����
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	//key�Ƿ��˹�˾���룬value�Ƿ��˹�˾
	private Map<String,OrgVO> corpMap = new HashMap<String,OrgVO>();
	
	//����pk���ڻ����У�key�ǲ��ű��룬value��pk_dept
	private Map<String,String> deptMap = new HashMap<String,String>();
	
	//keyн�ʷ������룬valueн�ʷ���ok
	private Map<String,String> waclaMap = new HashMap<String,String>();
	
	//keyн���ڼ�����201907��value��н���ڼ�����
	private Map<String,String> perMap = new HashMap<String,String>();
	
	//key�����֤�ţ�value��pk_psndoc
	private Map<String,String> psnMap = new HashMap<String,String>();
	
	public DecImportAction() {
		super();
		super.setCode("DecImportAction");
		super.putValue(Action.SHORT_DESCRIPTION, "��ʷ�������ݵ���");
		setBtnName("����");
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		DecImportDlg dlg = new DecImportDlg(getContext());
		if (1 == dlg.showModal()) {
			putValue(HrAction.MESSAGE_AFTER_ACTION, "");
			final String filePath = dlg.getFilePathPane().getText();
			final LoginContext waContext =  getContext();
			new Thread(new Runnable() {
				@Override
				public void run() {
					IProgressMonitor progressMonitor = NCProgresses
							.createDialogProgressMonitor(DecImportAction.this
									.getEntranceUI());

					progressMonitor.beginTask(ResHelper.getString(
							"6013dataitf_01", "dataitf-01-0042"), -1); // ��������...
					progressMonitor.setProcessInfo(ResHelper.getString(
							"6013dataitf_01", "dataitf-01-0043")); // ���ݵ�����,���Ժ�......
					try {
						DecImportAction.this.dataImport(filePath, waContext);
						MessageDialog.showHintDlg(DecImportAction.this
								.getEntranceUI(), null, ResHelper.getString(
								"6013dataitf_01", "dataitf-01-0044")); // ���ݵ���ɹ���
					} catch (Exception e) {
						Logger.error(e);
						MessageDialog.showErrorDlg(
								DecImportAction.this.getEntranceUI(), null,
								e.getMessage());
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
	protected void dataImport(String filePath, LoginContext context) throws Exception  {
		if (StringUtils.isBlank(filePath)) {
			Logger.error("import filepath is bank!");
			// "���������ļ�Ϊ��!"
			throw new Exception("�ļ�·�����");
		} else if (!isExcelFile(filePath)) {
			// "ֻ�ܵ���excel���������ļ�!"
			throw new Exception("���܌����excel����ļ�");
		}
		//������ļ�
		File imfile = new File(filePath);
		InputStream filein = new FileInputStream(imfile);
		//�ж�excel�İ汾
		Workbook wb =null;
		String exPath = null;
		String version = (filePath.endsWith(".xls") ? "2003" : "2007");
		if (version.equals("2003")) {
			wb = new HSSFWorkbook(filein);
			exPath = filePath+System.currentTimeMillis()+".xls";
		} else if (version.equals("2007")) {
			wb = new XSSFWorkbook(filein);
			exPath = filePath+System.currentTimeMillis()+".xlsx";
		}
		//������ļ�
		File exfile = new File(exPath);
		FileOutputStream fileout = new FileOutputStream(exfile);
		try {
		AggDeclarationVO aggvo = new AggDeclarationVO();
		DeclarationHVO parent = new DeclarationHVO();
		parent.setPk_group(this.getContext().getPk_group());
		parent.setPk_org(this.getContext().getPkorgs()[0]);
		aggvo.setParent(parent);
		//�Ǽ�ְ��Ա���䱣������
		Sheet sheet = wb.getSheetAt(0);
		boolean flag = importDataNONPart(sheet,aggvo);
        //��������д���ļ��������
        wb.write(fileout);
        if(flag){
        	AggDeclarationVO[] retaggvo = NCLocator.getInstance().lookup(ITwhr_declarationMaintain.class).
        			insert(new AggDeclarationVO[]{aggvo}, new AggDeclarationVO[]{aggvo});
        	this.getModel().initModel(retaggvo, new ModelDataDescriptor());
        }
		} catch (IOException e) {
			Logger.error(e);
			MessageDialog.showErrorDlg(DecImportAction.this.getEntranceUI(), null, e.getMessage());
		} finally{
			//�ı��ļ������ͷ���Դ
			fileout.close();
			filein.close();
		}
	}
	

	/**
	 * �ǲ���excel�ļ�
	 * @param filePath
	 * @return
	 */
	private boolean isExcelFile(String filePath){
		return filePath.toLowerCase().trim().endsWith(DataDecConst.SUFFIX_XLS)
				||filePath.toLowerCase().trim().endsWith(DataDecConst.SUFFIX_XLSX);
	}
	/**
	 * ���빫˾���䱣������
	 * @param filePath �ļ�·��
	 * @param context
	 */
	private void importDataCompany(Sheet sheet,AggDeclarationVO aggvo) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * ����ִ��ҵ�����ò��䱣������
	 * @param filePath �ļ�·��
	 * @param context
	 */
	private void importDataBusiness(Sheet sheet,AggDeclarationVO aggvo) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * �����ְ��Ա���䱣������
	 * @param filePath �ļ�·��
	 * @param context
	 */
	private void importDataPart(Sheet sheet,AggDeclarationVO aggvo) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * ����Ǽ�ְ��Ա���䱣������
	 * @param filePath �ļ�·��
	 * @param context
	 * @throws BusinessException 
	 */
	private boolean importDataNONPart(Sheet sheet,AggDeclarationVO aggvo) throws BusinessException {
		if(sheet ==null||sheet.getLastRowNum()==0){
			return false;
		}
		List<NonPartTimeBVO> vos = new ArrayList<NonPartTimeBVO>();
		//��ѭ���У����н���
		for (int curSeq=1; curSeq <= sheet.getLastRowNum(); curSeq++) {
			Row row = sheet.getRow(curSeq);
			StringBuffer sb = new StringBuffer();
			NonPartTimeBVO bvo = new NonPartTimeBVO();
			//���˹�˾, ����
			String corpcode = this.getCellValue(row.getCell(0));
			if(corpcode==null||corpcode.length()==0){
				sb.append("���˹�˾���ܞ�գ�");
			}else{
				OrgVO corp = this.getCorpByCode(corpcode);
				if(corp==null){
					sb.append("�������a��"+row.getCell(0)+"δ�ҵ��������˹�˾;");
				}
				else{
					bvo.setPk_group(corp.getPk_group());
					bvo.setPk_org(corp.getPk_org());
					bvo.setPk_org_v(corp.getPk_vid());
					bvo.setVbdef1(corp.getPk_org());
				}
			}
			//���ţ��Ǳ���
			String deptcode = this.getCellValue(row.getCell(1));
			if(deptcode!=null && deptcode.length()>0){
				String dept = this.getDeptPK(deptcode);
				if(dept==null||dept.trim().length()==0)
					sb.append("�������a��"+row.getCell(6)+"δ�ҵ��������T;");
				else
					bvo.setPk_dept(dept);
			}
			//н�ʷ������Ǳ���
			String wacode = this.getCellValue(row.getCell(2));
			if(wacode != null && wacode.length() > 0){
				String waclass = this.getWaClass(wacode);
				if(waclass==null || waclass.trim().length()==0){
					sb.append("�������a��"+row.getCell(2)+"δ�ҵ�н�Y����;");
				}else{
					bvo.setVbdef2(waclass);
				}
			}
			//н���ڼ䣬�Ǳ���
			String perdate = this.getCellValue(row.getCell(3));
			if(perdate != null && perdate.length() > 0 && wacode != null){
				String period  = this.getPeriod(perdate+wacode);
				if(period==null || period.trim().length()==0){
					sb.append("δ�ҵ�н�Y���g;");
				}else{
					bvo.setVbdef3(period);
				}
			}
			//��������, ����
			if(row.getCell(4)==null){
				sb.append("�o�����ڲ��ܞ�գ�");
			}else{
				String pay_date = this.getCellValue(row.getCell(4));
				try {
					bvo.setPay_date(new UFDate(pay_date));
				} catch (Exception e) {
					sb.append("�o�����ڸ�ʽ������");
				}
			}
			//���������֤��, ����
			if(row.getCell(5)==null){
				sb.append("����C̖���ܞ�գ�");
			}else{
				String id = this.getCellValue(row.getCell(5));
				String psn = getPsnByID(id);
				if(psn==null || psn.length()==0){
					sb.append("����C̖�鲻����Ա��");
				}else{
					//����������, �Ǳ���
					String name = this.getCellValue(row.getCell(6));
					String[] split = psn.split("&");
					bvo.setBeneficiary_id(id);
					bvo.setVbdef4(split[0]);
					if(name==null){
						row.getCell(6).setCellValue(split[1]);
					}else{
						if(!name.equals(split[1])){
							sb.append("����������������");
						}
					}
					bvo.setBeneficiary_name(split[1]);
				}
			}
			//�������ø���������
			if(row.getCell(7)==null){
				sb.append("�δ����ýo�������~���ܞ�գ�");
			}else{
				String singlepay = this.getCellValue(row.getCell(7));
				try {
					bvo.setSingle_pay(new UFDouble(singlepay));
				} catch (Exception e) {
					sb.append("�δ����ýo�������~����ǔ��֣�");
				}
			}
			//���ο۽ɲ��䱣�ѽ��
			if(row.getCell(8)==null){
				sb.append("�δο��U�a�䱣�M���~���ܞ�գ�");
			}else{
				String withhold = this.getCellValue(row.getCell(8));
				try {
					bvo.setSingle_withholding(new UFDouble(withhold));
				} catch (Exception e) {
					sb.append("�δο��U�a�䱣�M���~����ǔ��֣�");
				}
			}
			//Ͷ����λ����
			if(row.getCell(9)==null){
				sb.append("Ͷ����λ��̖���ܞ�գ�");
			}else{
				String unitcode = this.getCellValue(row.getCell(9));
				bvo.setInsurance_unit_code(unitcode);
			}
			//�۷ѵ���Ͷ�����
			if(row.getCell(10)==null){
				sb.append("���M����Ͷ�����~���ܞ�գ�");
			}else{
				String monthpay = this.getCellValue(row.getCell(10));
				try {
					bvo.setDeductions_month_insure(new UFDouble(monthpay));
				} catch (Exception e) {
					sb.append("���M����Ͷ�����~����ǔ��֣�");
				}
			}
			//ͬ����ۼƽ𽱽���
			if(row.getCell(11)==null){
				sb.append("ͬ����۷e�𪄽���~���ܞ�գ�");
			}else{
				String totamny = this.getCellValue(row.getCell(11));
				try {
					bvo.setTotalbonusforyear(new UFDouble(totamny));
				} catch (Exception e) {
					sb.append("ͬ����۷e�𪄽���~����ǔ��֣�");
				}
			}
			//�����Ǹ����ֶε����У��ͷ�װ
			//sb�ǿգ�֤������������֤ͨ��
			if(sb.length()==0){
				vos.add(bvo);
			}
			//sb��Ϊ�յģ�֤��У�����֤ûͨ������дУ����Ϣ
			else{
				row.createCell(13);
				row.getCell(13).setCellValue(sb.toString());
			}
		}
		if(vos.size()>0){
			aggvo.setChildrenVO(vos.toArray(new NonPartTimeBVO[0]));
			return true;
		}
		return false;
	}


	/**
	 * ����id��ѯ��Ա
	 * @param id
	 * @return
	 * @throws BusinessException 
	 */
	@SuppressWarnings("unchecked")
	private String getPsnByID(String id) throws BusinessException {
		if(this.psnMap.size()==0){
			this.psnMap = (Map<String, String>) NCLocator.getInstance().lookup(ITwhr_declarationMaintain.class)
					.getPkByCode(null, "psn");
		}
		return this.psnMap.get(id);
	}

	/**
	 * ��ȡн���ڼ�
	 * @param perdate
	 * @return
	 * @throws BusinessException 
	 */
	private String getPeriod(String perdate) throws BusinessException {
		if(perMap.get(perdate)!=null){
			return perMap.get(perdate);
		}
		String perpk= (String) NCLocator.getInstance().lookup(ITwhr_declarationMaintain.class)
				.getPkByCode(perdate, "period");
		if(perpk!=null){
			perMap.put(perdate,perpk);
		}
		return perpk;
	}

	/**
	 * н�ʷ���
	 * @param wacode
	 * @return
	 * @throws BusinessException
	 */
	private String getWaClass(String wacode) throws BusinessException {
		if(waclaMap.get(wacode)!=null){
			return waclaMap.get(wacode);
		}
		String wapk = (String) NCLocator.getInstance().lookup(ITwhr_declarationMaintain.class)
				.getPkByCode(wacode, "waclass");
		if(wapk!=null){
			waclaMap.put(wacode, wapk);
		}
		return wapk;
	}

	/**
	 * ��ȡ���˹�˾
	 * @param code
	 * @return
	 * @throws BusinessException 
	 */
	private OrgVO getCorpByCode(String code) throws BusinessException {
		if(corpMap.get(code)!=null){
			return corpMap.get(code);
		}
		//���˹�˾�����ѯ���˹�˾vo
		OrgVO corp =(OrgVO) NCLocator.getInstance().lookup(ITwhr_declarationMaintain.class)
				.getPkByCode(code, "corp");
		if(corp!=null)
			corpMap.put(code, corp);
		return corp;
	}

	/**
	 * ���ݲ��ű����ѯ����pk
	 * @param deptCode
	 * @return
	 * @throws BusinessException 
	 */
	private String getDeptPK(String deptCode) throws BusinessException{
		if(deptMap.get(deptCode)!=null){
			return deptMap.get(deptCode);
		}
		//���ݲ��ű����ѯ��������
		String pk_dept = (String) NCLocator.getInstance().lookup(ITwhr_declarationMaintain.class)
				.getPkByCode(deptCode, "dept");
		if(pk_dept!=null){
			deptMap.put(deptCode, pk_dept);
		}
		return pk_dept;
	}
	
	/**
	 * ��ȡ��Ԫ��cell��ֵ
	 * @param cell ��Ԫ��
	 * @return
	 */
	private String getCellValue(Cell cell){
	    if (cell == null)
	     {
	       return "";
	     }
	     int type = cell.getCellType();
	     String value = "";
	     switch (type){
	     case 0: 
	       if (HSSFDateUtil.isCellDateFormatted(cell)){
	        value = sdf.format(cell.getDateCellValue());
	       }
	       else {
	         value = String.valueOf(cell.getNumericCellValue());
	         if (value.indexOf("E") > 0){
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
	 * �������͵����ó�������
	 * @param strDigt
	 * @return
	 */
	private String parseDouble(String strDigt){
	     int pos = strDigt.indexOf("E");
	     int dotPos = strDigt.indexOf(".");
	     String betweenValue = strDigt.substring(dotPos + 1, pos);
	     String temp = "";
	     int power = Integer.parseInt(strDigt.substring(pos + 1));
	     if (power > 0){
	       StringBuilder sbd = new StringBuilder();
	       sbd.append(strDigt.substring(0, dotPos));
	       for (int index = 0; index < power; index++)
	       {
	         if (index < betweenValue.length()){
	           sbd.append(betweenValue.charAt(index));
	         }
	         else{
	           sbd.append("0");
	         }
	       }
	       if (power < betweenValue.length()){
	         temp = betweenValue.substring(power);
	         sbd.append("." + temp);
	       }
	       return sbd.toString();
	     }
	     return strDigt;
	   }
}
