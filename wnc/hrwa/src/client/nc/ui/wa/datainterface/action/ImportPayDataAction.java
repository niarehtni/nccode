package nc.ui.wa.datainterface.action;

import java.awt.event.ActionEvent;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.itf.hr.datainterface.IDataIOManageService;
import nc.pub.wa.datainterface.DataItfConst;
import nc.pub.wa.datainterface.DataItfFileReader;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.wa.datainterface.view.PayDataImportDlg;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.pub.SqlBuilder;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.datainterface.BonusOthBuckVO;
import nc.vo.wa.datainterface.ImpParamVO;
import nc.vo.wa.datainterface.MappingFieldVO;
import nc.vo.wa.datainterface.SalaryOthBuckVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.pub.WaLoginContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings({ "restriction", "serial" })
public class ImportPayDataAction extends HrAction {
	public ImportPayDataAction() {
		setCode("importPayData");
		setBtnName(ResHelper.getString("6013dataitf_01", "dataitf-01-0001")); // 批量期间导入
	}

	@Override
	public void doAction(ActionEvent evt) throws Exception {
		PayDataImportDlg dlg = new PayDataImportDlg(getContext());
		if (1 == dlg.showModal()) {
			putValue(HrAction.MESSAGE_AFTER_ACTION, "");
			final String filePath = dlg.getFilePathPane().getText();
			final Integer dataType = (Integer) dlg.getUiCbxDataType().getSelectdItemValue();
			final WaLoginContext waContext = (WaLoginContext) getContext();
			final BannerTimerDialog dialog = new BannerTimerDialog(ImportPayDataAction.this.getEntranceUI());
			new Thread(new Runnable() {
				@Override
				public void run() {
					dialog.setStartText(ResHelper.getString("6013dataitf_01", "dataitf-01-0043")); // 数据导入中,请稍后......
					dialog.start();
					try {
						ImportPayDataAction.this.payDataImport(filePath, waContext, dataType, dialog);
						MessageDialog.showHintDlg(ImportPayDataAction.this.getEntranceUI(), null,
								ResHelper.getString("6013dataitf_01", "dataitf-01-0044")); // 数据导入成功！
					} catch (Exception e) {
						Logger.error(e);
						MessageDialog.showErrorDlg(ImportPayDataAction.this.getEntranceUI(), null, e.getMessage());
					} finally {
						dialog.end();
					}
				}
			}).start();

		} else {
			putValue(HrAction.MESSAGE_AFTER_ACTION, "");
		}
	}

	@Override
	protected void checkDataPermission() throws BusinessException {
		// super.checkDataPermission();
	}

	@Override
	protected boolean isActionEnable() {
		boolean result = super.isActionEnable();
		WaLoginContext context = (WaLoginContext) getContext();

		return result && StringUtils.isNotBlank(context.getPk_org())
				&& StringUtils.isNotBlank(context.getPk_wa_class());

	}

	protected void payDataImport(String filePath, WaLoginContext waContext, Integer dataType, BannerTimerDialog dialog)
			throws Exception {
		if (null == dataType) {
			Logger.error("import data type is null!");
			// "导入数据类型为空!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0019"));
		}
		if (StringUtils.isBlank(filePath)) {
			Logger.error("import filepath is bank!");
			// "导入数据文件为空!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0020"));
		} else if (!DataItfFileReader.isExcelFile(filePath) && !DataItfFileReader.isCsvFile(filePath)) {
			// "只能导入excel或者csv类型数据文件!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0018"));
		}
		switch (dataType.intValue()) {
		case DataItfConst.VALUE_SALARY_DETAIL:
			importDataSD(filePath, waContext, dialog, MappingFieldVO.TYPE_SD);
			break;
		case DataItfConst.VALUE_SALARY_OTHERDEC:
			importDataSOD(filePath, waContext);
			break;
		case DataItfConst.VALUE_BONUS_DETAIL:
			// 導入201909的中秋獎金，出現找不到人員編碼的訊息，以7002009來看，任職歷史並無異常，再請協助確認 by George
			// 20200523
			// 這裡先拿好的 case DataItfConst.VALUE_SALARY_DETAIL: 的來用，之前用 WNC_缺陷Bug
			// #35316 批量期間導入 優化過改過能用
			importDataSD(filePath, waContext, dialog, MappingFieldVO.TYPE_BD);
			// importDataBD(filePath, waContext);
			break;
		case DataItfConst.VALUE_BONUS_OTHERDEC:
			importDataBOD(filePath, waContext);
			break;
		default:
			Logger.error("import data type is out of type arry combobox!");
			// "导入的数据类型超出定义的下列表!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0021"));
		}
	}

	protected void importDataSD(String filePath, WaLoginContext waContext, BannerTimerDialog dialog, int type)
			throws Exception {
		Map<String, MappingFieldVO> indexItemKeyMap = DataItfFileReader.getMappingByType(type);
		int numlimit = DataItfFileReader.getBatchNumLimit();

		DataVO[] vos = null;
		int count = 0;
		ImpParamVO paraVO = new ImpParamVO(null, null, null, null, indexItemKeyMap, null, numlimit, count);
		try {
			dialog.setStartText("正在讀入Excel文件...");
			vos = DataItfFileReader.readFileSD(filePath, waContext, paraVO, type);
			if (!ArrayUtils.isEmpty(vos)) {
				dialog.setStartText("正在校驗並保存至資料庫...");
				getService().importPayDataSD(vos);
			}
		} catch (Exception e) {
			// "成功导入[{0}]条记录!"
			StringBuffer errormsg = new StringBuffer();
			if (paraVO.getStartIndex() > 0) {
				errormsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0046", null,
						new String[] { paraVO.getStartIndex() + "" }));
			}
			errormsg.append(e.getMessage());
			throw new Exception(errormsg.toString());
		}
	}

	protected void importDataSOD(String filePath, WaLoginContext waContext) throws Exception {
		Map<String, HRDeptVO> codeDeptVOMap = DataItfFileReader.getCodeDeptInfo(waContext);
		Map<String, PsndocVO> codePsnVOMap = DataItfFileReader.getCodePsnInfo(waContext);
		int numlimit = DataItfFileReader.getBatchNumLimit();

		SalaryOthBuckVO[] vos = null;
		int count = 0;
		ImpParamVO paraVO = new ImpParamVO(codeDeptVOMap, codePsnVOMap, null, null, null, null, numlimit, count);
		try {
			do {
				vos = DataItfFileReader.readFileSOD(filePath, waContext, paraVO);
				if (!ArrayUtils.isEmpty(vos)) {
					SalaryOthBuckVO[] dbVos = null;
					// dbVos =
					// getService().queryDataByConditon(getFilterCondition(waContext),
					// SalaryOthBuckVO.class);
					// if (!ArrayUtils.isEmpty(dbVos)) {
					// // 组织薪资方案内的薪资其他加扣项明细数据已存在! + 确认替换吗?
					// StringBuilder msg = new
					// StringBuilder(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0012"));
					// msg.append(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0014"));
					// int result =
					// MessageDialog.showOkCancelDlg(getModel().getContext().getEntranceUI(),
					// null,
					// msg.toString());
					// if (UIDialog.ID_CANCEL == result) {
					// return;
					// }
					// if (UIDialog.ID_NO == result) {
					// dbVos = null;
					// }
					// }
					getService().insertPayDetail(vos, dbVos);

					count += vos.length;
					paraVO.setStartIndex(count);
				}
			} while (ArrayUtils.isEmpty(vos));
		} catch (Exception e) {
			// "成功导入[{0}]条记录!"
			StringBuffer errormsg = new StringBuffer();
			if (paraVO.getStartIndex() > 0) {
				errormsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0046", null,
						new String[] { paraVO.getStartIndex() + "" }));
			}
			errormsg.append(e.getMessage());
			throw new Exception(errormsg.toString());
		}
	}

	protected void importDataBD(String filePath, WaLoginContext waContext) throws Exception {
		Map<String, HRDeptVO> codeDeptVOMap = DataItfFileReader.getCodeDeptInfo(waContext);
		Map<String, PsndocVO> codePsnVOMap = DataItfFileReader.getCodePsnInfo(waContext);
		Map<String, PsndocVO> codeDeptPsnVOMap = DataItfFileReader.getCodeDeptPsnInfo(waContext);
		Map<String, MappingFieldVO> indexItemKeyMap = DataItfFileReader.getMappingByType(MappingFieldVO.TYPE_BD);
		int numlimit = DataItfFileReader.getBatchNumLimit();

		DataVO[] vos = null;
		int count = 0;
		ImpParamVO paraVO = new ImpParamVO(codeDeptVOMap, codePsnVOMap, codeDeptPsnVOMap, null, indexItemKeyMap, null,
				numlimit, count);
		try {
			do {
				vos = DataItfFileReader.readFileBD(filePath, waContext, paraVO);
				if (!ArrayUtils.isEmpty(vos)) {
					String[] psnPks = StringPiecer.getStrArrayDistinct(vos, DataVO.PK_PSNDOC);
					StringBuilder condition = new StringBuilder(getFilterCondition(waContext));
					BonusOthBuckVO[] BODVos = getService().queryDataByConditon(condition.toString(), psnPks,
							BonusOthBuckVO.class);
					// if (ArrayUtils.isEmpty(BODVos)) {
					// // 组织薪资方案内的奖金其他加扣项明细数据不存在! + 确认继续导入吗?
					// StringBuilder msg = new
					// StringBuilder(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0016"));
					// msg.append(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0017"));
					// if (UIDialog.ID_YES !=
					// MessageDialog.showYesNoDlg(getModel().getContext().getEntranceUI(),
					// null, msg.toString())) {
					// return;
					// }
					// }
					getService().importPayDataBD(vos, BODVos);

					count += vos.length;
					paraVO.setStartIndex(count);
				}
			} while (ArrayUtils.isEmpty(vos));
		} catch (Exception e) {
			// "成功导入[{0}]条记录!"
			StringBuffer errormsg = new StringBuffer();
			if (paraVO.getStartIndex() > 0) {
				errormsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0046", null,
						new String[] { paraVO.getStartIndex() + "" }));
			}
			errormsg.append(e.getMessage());
			throw new Exception(errormsg.toString());
		}
	}

	protected void importDataBOD(String filePath, WaLoginContext waContext) throws Exception {
		Map<String, HRDeptVO> codeDeptVOMap = DataItfFileReader.getCodeDeptInfo(waContext);
		Map<String, PsndocVO> codePsnVOMap = DataItfFileReader.getCodePsnInfo(waContext);
		Map<String, WaClassVO> codeWaclassVOMap = DataItfFileReader.getCodeWaClassInfo(waContext);
		int numlimit = DataItfFileReader.getBatchNumLimit();

		BonusOthBuckVO[] vos = null;
		int count = 0;
		ImpParamVO paraVO = new ImpParamVO(codeDeptVOMap, codePsnVOMap, null, null, null, codeWaclassVOMap, numlimit,
				count);
		try {
			do {
				vos = DataItfFileReader.readFileBOD(filePath, waContext, paraVO);
				if (!ArrayUtils.isEmpty(vos)) {
					BonusOthBuckVO[] dbVos = null;
					// dbVos =
					// getService().queryDataByConditon(getFilterCondition(waContext),
					// BonusOthBuckVO.class);
					// if (!ArrayUtils.isEmpty(dbVos)) {
					// // 组织薪资方案内的奖金其他加扣项明细数据已存在! + 确认替换吗?
					// StringBuilder msg = new
					// StringBuilder(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0013"));
					// msg.append(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0014"));
					// int result =
					// MessageDialog.showYesNoCancelDlg(getModel().getContext().getEntranceUI(),
					// null,
					// msg.toString());
					// if (UIDialog.ID_CANCEL == result) {
					// return;
					// }
					// if (UIDialog.ID_NO == result) {
					// dbVos = null;
					// }
					// }
					getService().insertPayDetail(vos, dbVos);

					count += vos.length;
					paraVO.setStartIndex(count);
				}
			} while (ArrayUtils.isEmpty(vos));
		} catch (Exception e) {
			// "成功导入[{0}]条记录!"
			StringBuffer errormsg = new StringBuffer();
			if (paraVO.getStartIndex() > 0) {
				errormsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0046", null,
						new String[] { paraVO.getStartIndex() + "" }));
			}
			errormsg.append(e.getMessage());
			throw new Exception(errormsg.toString());
		}
	}

	protected String getFilterCondition(WaLoginContext waContext) {
		SqlBuilder condition = new SqlBuilder();
		condition.append("pk_group", waContext.getPk_group());
		condition.append("and pk_org", waContext.getPk_org());
		condition.append("and pk_wa_class", waContext.getClassPK());
		return condition.toString();
	}

	private IDataIOManageService service;

	public IDataIOManageService getService() {
		if (null == service) {
			service = NCLocator.getInstance().lookup(IDataIOManageService.class);
		}
		return service;
	}

}
