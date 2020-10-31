package nc.ui.hrwa.sumincometax.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.wa.datainterface.IReportExportService;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.query2.model.IModelDataManager;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.wa.datainterface.TextFileFilter4TW;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

import org.apache.commons.io.FileUtils;

public class ExportApplyAction extends HrAction {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = 5099334079859001851L;
	private IReportExportService service = null;
	List<String> dataPKs = new ArrayList<String>();
	private IModelDataManager dataManager;
	private ShowUpableBillForm billForm;
	private String applyFormat;
	private String applyCount;
	private String applyReason;
	private String applyYear;
	private String grantType;
	private String vatNumber;
	private String comLinkMan;
	private String comLinkTel;
	private String comLineEmail;

	public ExportApplyAction() {
		super();
		super.setBtnName("�������n");
		super.setCode("ExportApplyAction");
		super.putValue(Action.SHORT_DESCRIPTION, "�������n");
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		JComponent parentUi = getModel().getContext().getEntranceUI();
		checkData();
		String twYear = String.valueOf(Integer.valueOf(this.getApplyYear()) - 1911);

		String schemaCode = "IITR_FMT_TW_2020";
		IUAPQueryBS qry = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		String charSetCode = (String) qry.executeQuery("select CHARSETCODE from wa_expformat_head where code = '"
				+ schemaCode + "'", new ColumnProcessor());

		String fileExt = null;
		if (charSetCode.equals("UTF-8")) {
			fileExt = "U8";
		} else {
			charSetCode = "Big5-HKSCS";
		}

		String[] textArr = getService().getIITXTextReport(dataPKs.toArray(new String[0]),
				Integer.valueOf(this.getApplyYear()), this.getApplyFormat(), this.getApplyCount(),
				this.getApplyReason(), this.getVatNumber(), this.getGrantType(), this.getComLinkMan(),
				this.getComLinkTel(), this.getComLineEmail(), schemaCode, charSetCode);

		if (textArr != null && textArr.length > 2) {
			UIFileChooser fileChooser = new UIFileChooser();
			fileChooser.setDialogTitle("Ոָ��Ҫ�R�����ęn���Q");
			TextFileFilter4TW filter = new TextFileFilter4TW();
			filter.setFilterString("*." + fileExt);
			filter.setDescription("���ɾC�������n");
			fileChooser.addChoosableFileFilter(filter);
			fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory().getAbsolutePath() + "\\"
					+ this.getVatNumber() + "." + twYear + (fileExt == null ? "" : "." + fileExt)));
			int userSelection = fileChooser.showSaveDialog(parentUi);

			String filename = "";
			File fileToSave = null;
			if (userSelection == JFileChooser.APPROVE_OPTION) {
				if (!fileChooser.getSelectedFile().getAbsoluteFile().toString().toUpperCase()
						.endsWith((fileExt == null ? "." + twYear : "." + fileExt))) {
					filename = fileChooser.getSelectedFile().getAbsolutePath()
							+ (fileExt == null ? "." + twYear : "." + fileExt);
				} else {
					filename = fileChooser.getSelectedFile().getAbsolutePath();
				}

				fileToSave = new File(filename);
				// ����ˢ�®���
				// this.getDataManager().refresh();
			} else {
				this.putValue("message_after_action", "�������n���I��ȡ����");
				return;
			}

			StringBuilder sb = new StringBuilder();
			for (String txt : textArr) {
				sb.append(txt + "\r\n");
			}

			if (fileToSave != null) {
				// �xȡ����ı�
				FileUtils.writeStringToFile(fileToSave, sb.toString(), charSetCode);
			}

			// �����،�עӛ��ӛ
			// this.getService().writeBackFlags(dataPKs.toArray(new String[0]));

			this.putValue("message_after_action", "");
		} else {
			this.putValue("message_after_action", "ָ���ėl���]�ҵ������ɵ�����Y�ϡ�");
		}
	}

	public String getApplyFormat() {
		return applyFormat;
	}

	public void setApplyFormat(String applyFormat) {
		this.applyFormat = applyFormat;
	}

	public String getApplyCount() {
		return applyCount;
	}

	public void setApplyCount(String applyCount) {
		this.applyCount = applyCount;
	}

	public String getApplyReason() {
		return applyReason;
	}

	public void setApplyReason(String applyReason) {
		this.applyReason = applyReason;
	}

	public String getVatNumber() {
		return vatNumber;
	}

	public void setVatNumber(String vatNumber) {
		this.vatNumber = vatNumber;
	}

	public String getGrantType() {
		return grantType;
	}

	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}

	public String getApplyYear() {
		return applyYear;
	}

	public void setApplyYear(String applyYear) {
		this.applyYear = applyYear;
	}

	public String getComLinkMan() {
		return comLinkMan;
	}

	public void setComLinkMan(String comLinkMan) {
		this.comLinkMan = comLinkMan;
	}

	public String getComLinkTel() {
		return comLinkTel;
	}

	public void setComLinkTel(String comLinkTel) {
		this.comLinkTel = comLinkTel;
	}

	public String getComLineEmail() {
		return comLineEmail;
	}

	public void setComLineEmail(String comLineEmail) {
		this.comLineEmail = comLineEmail;
	}

	private boolean checkData() throws BusinessException {
		Object[] data = ((BillManageModel) this.getModel()).getSelectedOperaDatas();
		if (data != null && data.length > 0) {
			AggSumIncomeTaxVO firstData = (AggSumIncomeTaxVO) data[0];
			// �{�θ�ʽ
			String strFormat = firstData.getParentVO().getDeclaretype();
			// �{����l��ʽ
			String strGranttype = firstData.getParentVO().getGranttype();
			// ���Δ�
			String strCount = firstData.getParentVO().getDeclarenum();
			// ���}���ԭ��
			String strReason = firstData.getParentVO().getReason();
			// �j������
			String strName = firstData.getParentVO().getContactname();
			// �Ԓ
			String strPhone = firstData.getParentVO().getContacttel();
			// ����]��
			String strEmail = firstData.getParentVO().getContactemail();
			// �yһ��̖
			String strVatNo = firstData.getParentVO().getUnifiednumber();

			String strYear = firstData.getParentVO().getBeginperiod().substring(0, 4);

			dataPKs.clear();
			for (Object line : data) {
				AggSumIncomeTaxVO aggvo = (AggSumIncomeTaxVO) line;

				if (UFBoolean.TRUE.equals(aggvo.getParentVO().getIsforeignmonthdec())) {
					continue;
				}

				if (UFBoolean.TRUE.equals(aggvo.getParentVO().getIsmanualdec())) {
					continue;
				}

				if (UFBoolean.TRUE.equals(aggvo.getParentVO().getIsdeclare())) {
					throw new BusinessException("�T�� [" + aggvo.getParentVO().getCode() + "] �C�����Y������󣬲������}���");
				}

				/*
				 * if (!isSame(strFormat, aggvo.getParentVO().getDeclaretype()))
				 * { throw new BusinessException("�T�� [" +
				 * aggvo.getParentVO().getCode() + "] ������Y�ϑ{�θ�ʽ��Ψһ�������M�����"); }
				 */

				if (!isSame(strGranttype, aggvo.getParentVO().getGranttype())) {
					throw new BusinessException("�T�� [" + aggvo.getParentVO().getCode() + "] ������Y�ϑ{����l��ʽ��Ψһ�������M�����");
				}

				if (!isSame(strCount, aggvo.getParentVO().getDeclarenum())) {
					throw new BusinessException("�T�� [" + aggvo.getParentVO().getCode() + "] ������Y�����Δ���Ψһ�������M�����");
				}

				if (!isSame(strReason, aggvo.getParentVO().getReason())) {
					throw new BusinessException("�T�� [" + aggvo.getParentVO().getCode() + "] ������Y�����}���ԭ��Ψһ�������M�����");
				}

				if (!isSame(strName, aggvo.getParentVO().getContactname())) {
					throw new BusinessException("�T�� [" + aggvo.getParentVO().getCode() + "] ������Y���M��������Ψһ�������M�����");
				}

				if (!isSame(strPhone, aggvo.getParentVO().getContacttel())) {
					throw new BusinessException("�T�� [" + aggvo.getParentVO().getCode() + "] ������Y���M���Ԓ��Ψһ�������M�����");
				}

				if (!isSame(strEmail, aggvo.getParentVO().getContactemail())) {
					throw new BusinessException("�T�� [" + aggvo.getParentVO().getCode() + "] ������Y���M���Ԓ��Ψһ�������M�����");
				}

				if (!isSame(strVatNo, aggvo.getParentVO().getUnifiednumber())) {
					throw new BusinessException("�T�� [" + aggvo.getParentVO().getCode() + "] ������Y�Ͻyһ��̖��Ψһ�������M�����");
				}

				dataPKs.add(aggvo.getPrimaryKey());
			}

			// [�yһ��̖��������Code����l���Code�����}���ԭ��Code]
			String[] rtn = this.getService().getIITXTInfo(strVatNo, strFormat, strGranttype, strReason);
			this.setVatNumber(rtn[0]);
			this.setApplyFormat(rtn[1]);
			this.setGrantType(rtn[2]);
			this.setApplyReason(rtn[3]);
			this.setApplyCount(strCount);
			this.setApplyYear(strYear);
			this.setComLinkMan(strName);
			this.setComLinkTel(strPhone);
			this.setComLineEmail(strEmail);
		} else {
			throw new BusinessException("Ո���xҪ�a�����n���ˆT");
		}
		return true;
	}

	private boolean isSame(String value1, String value2) {
		if ((value1 == null && value2 == null) || (value1 != null && value1.equals(value2))
				|| (value2 != null && value2.equals(value1))) {
			return true;
		}

		return false;
	}

	private IReportExportService getService() {
		if (service == null) {
			service = (IReportExportService) NCLocator.getInstance().lookup(IReportExportService.class);
		}
		return service;
	}

	protected boolean isActionEnable() {
		Object[] data = ((BillManageModel) this.getModel()).getSelectedOperaDatas();
		return data != null && data.length > 0;
	}

	public IModelDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IModelDataManager dataManager) {
		this.dataManager = dataManager;
	}

	public ShowUpableBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(ShowUpableBillForm billForm) {
		this.billForm = billForm;
	}
}
