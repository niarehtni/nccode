/**
 * @(#)ImportAction.java 1.0 2017年9月13日
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.ui.hrwa.projsalary.ace.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.hrwa.IProjsalaryMaintain;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hrwa.projsalary.ace.model.ProjSalaryModel;
import nc.ui.hrwa.projsalary.ace.view.FileImportDialog;
import nc.ui.pub.beans.MessageDialog;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.projsalary.AggProjSalaryVO;
import nc.vo.wa.projsalary.ProjSalaryHVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVOHelper;
import nc.vo.wa.pub.WaState;
import nc.vo.wa.pub.util.FileReader;

import org.apache.commons.lang.StringUtils;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings({ "restriction", "serial" })
public class ImportAction extends HrAction {

	public ImportAction() {
		setCode("import");
		setBtnName(ResHelper.getString("projsalary", "0pjsalary-00004")); // 导入
	}

	@Override
	public void doAction(ActionEvent paramActionEvent) throws Exception {
		FileImportDialog dlg = new FileImportDialog();
		if (1 == dlg.showModal()) {
			File file = dlg.getFilePath();
			if (null == file) {
				// 没有选择导入文件
				ExceptionUtils.wrappBusinessException(ResHelper.getString("projsalary", "0pjsalary-00009"));
				return;
			}
			WaLoginContext waContext = (WaLoginContext) getModel().getContext();
			Map<String, Object> excelMap = FileReader.readProjSalaryExcel(file, waContext);
			String errMsg = (String) excelMap.get(FileReader.Err_MSG);
			if (StringUtils.isNotEmpty(errMsg)) {
				ExceptionUtils.wrappBusinessException(errMsg);
			}
			String eysExistMsg = (String) excelMap.get(FileReader.Err_SYS_EXIST);
			if (StringUtils.isNotEmpty(eysExistMsg)) {
				if (MessageDialog.ID_OK != MessageDialog.showOkCancelDlg(getEntranceUI(), null, eysExistMsg)) {
					putValue(MESSAGE_AFTER_ACTION, null);
					return;
				}
			}
			String[] delPks = (String[]) excelMap.get(FileReader.DEL_PKS);
			AggProjSalaryVO[] impBills = (AggProjSalaryVO[]) excelMap.get(FileReader.IMP_DATA);
			setOtherValueForImp(impBills, waContext);
			IProjsalaryMaintain service = NCLocator.getInstance().lookup(IProjsalaryMaintain.class);
			impBills = service.importProjSalary(impBills, delPks);
			getModel().initModel(impBills);
			putValue(MESSAGE_AFTER_ACTION, ResHelper.getString("6001uif2", "06001uif20002"));
		} else {
			putValue(MESSAGE_AFTER_ACTION, null);
		}
	}

	/**
	 * @Description: 填充导入数据的其他必要属性.
	 * @param impBills
	 * @param waContext
	 */
	protected void setOtherValueForImp(AggProjSalaryVO[] impBills, WaLoginContext waContext) {
		for (AggProjSalaryVO aggVO : impBills) {
			ProjSalaryHVO hvo = aggVO.getParentVO();
			hvo.setPk_group(waContext.getPk_group());
			hvo.setPk_org(waContext.getPk_org());
			hvo.setPk_wa_calss(waContext.getClassPK());
			hvo.setCperiod(waContext.getCyear() + waContext.getCperiod());
			hvo.setDbilldate(AppContext.getInstance().getBusiDate());
			hvo.setFstatusflag(BillStatusEnum.FREE.toIntValue());
			hvo.setPk_project(hvo.getDef1());
			hvo.setStatus(VOStatus.NEW);
			hvo.setTs(null);
		}
	}

	@Override
	protected boolean isActionEnable() {
		((ProjSalaryModel) getModel()).setCurrWaStatus(null);
		WaLoginContext waLoginContext = (WaLoginContext) getContext();
		if (!waLoginContext.isContextNotNull()) {
			return false;
		}
		if (null != waLoginContext.getWaState() && null != getEnableStateSet()
				&& getEnableStateSet().contains(waLoginContext.getWaState())) {
			return false;
		}
		if ((null != waLoginContext.getWaLoginVO().getBatch())
				&& (waLoginContext.getWaLoginVO().getBatch().intValue() > 100)) {
			return false;
		}
		if (WaLoginVOHelper.isMultiClass(waLoginContext.getWaLoginVO())) {
			return false;
		}
		// if (((ProjSalaryModel) getModel()).isPayDataApproved()) {
		// return false;
		// }
		return super.isActionEnable();
	}

	public Set<WaState> getEnableStateSet() {
		if (waStateSet == null) {
			waStateSet = new HashSet<WaState>();
			waStateSet.add(WaState.CLASS_IS_APPROVED);
			waStateSet.add(WaState.CLASS_WITHOUT_PAY);

			waStateSet.add(WaState.CLASS_PART_CHECKED);
			waStateSet.add(WaState.CLASS_CHECKED_WITHOUT_PAY);

			waStateSet.add(WaState.CLASS_ALL_PAY);

			waStateSet.add(WaState.CLASS_MONTH_END);
		}
		return waStateSet;
	}

	protected Set<WaState> waStateSet = null;
}
