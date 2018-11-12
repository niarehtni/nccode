package nc.bs.hrsms.ta.sss.overtime.lsnr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyConsts;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.ta.away.lsnr.AwaySaveProcessor;
import nc.bs.uif2.validation.ValidationFailure;
import nc.hr.utils.ResHelper;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.itf.ta.IOvertimeApplyApproveManageMaintain;
import nc.itf.ta.IOvertimeApplyQueryMaintain;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.ta.bill.BillMutexException;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.ta.overtime.OvertimebVO;
import nc.vo.ta.overtime.OvertimehVO;
import nc.vo.ta.overtime.pf.validator.PFSaveOvertimeValidator;

import org.apache.commons.lang.StringUtils;

public class ShopOverTimeSaveProcessor implements ISaveProcessor {

	// �Ӱ����뱣��ʱ��ȷ�϶Ի����Id
		private final String DIALOG_SAVE = "dlg_overtime_save";
		private final String DIALOG_SAVE_1 = "dlg_overtime_save_1";
		// �Ӱ൥û��ѡ�мӰ�У���ʶ��ʾУ��Ի���ID
		private final String DIALOG_CHECK_ISNEED = "dlg_overtime_isneed";

		/**
		 * ����ǰ����
		 * 
		 * @param aggVo
		 * @return
		 */
		@Override
		public void onBeforeVOSave(AggregatedValueObject aggVO) throws Exception {

		}

		/**
		 * ����У�����
		 */
		@Override
		public boolean checkBeforeVOSave(AggregatedValueObject aggVO) throws Exception {
			// ����ֵ1
			Boolean result = AppInteractionUtil.getConfirmDialogResult(DIALOG_SAVE);
			if (result != null && Boolean.FALSE.equals(result)) {
				return false;
			}
			
			Boolean resultIsNeedCheck = AppInteractionUtil.getConfirmDialogResult(DIALOG_CHECK_ISNEED);
			if (resultIsNeedCheck != null && Boolean.FALSE.equals(resultIsNeedCheck)) {
				return false;
			}

			// ��ȡ�ӱ���Ϣ
			OvertimehVO headVO = (OvertimehVO) aggVO.getParentVO();
			//���ڵ����Ѿ���������Ա����������������ǰ������ʱpk_psnorg�ֶ�Ϊ�գ��޷���������
			PsnJobVO psnjobVO = null;
			try {
				psnjobVO = (PsnJobVO) ServiceLocator.lookup(IPersistenceRetrieve.class).retrieveByPk(PsnJobVO.class, headVO.getPk_psnjob(), null);
			} catch (HrssException e1) {
				throw new Exception(e1);
			}
			headVO.setPk_psnorg(psnjobVO.getPk_psnorg());
			/* ��������ʱ��ʾ,�޸ı��治��ʾ */
			Boolean resultHoliday = Boolean.FALSE;
			// ����ֵ2
			resultHoliday = AppInteractionUtil.getConfirmDialogResult(DIALOG_SAVE_1);
			if (resultHoliday != null && Boolean.FALSE.equals(resultHoliday)) {
				return false;
			}
			OvertimebVO[] vos = (OvertimebVO[]) aggVO.getChildrenVO();
			List<OvertimebVO> subVOs = new ArrayList<OvertimebVO>();
			for (OvertimebVO subVO : vos) {
				subVO.setPk_group(headVO.getPk_group());
				// ���õ���������֯����
				subVO.setPk_org(headVO.getPk_org());
				// ������Ա����
				subVO.setPk_psndoc(headVO.getPk_psndoc());
				// ��Ա��ְ����
				subVO.setPk_psnjob(headVO.getPk_psnjob());
				// ��֯��ϵ����
				subVO.setPk_psnorg(headVO.getPk_psnorg());
				// �Ӱ����
				subVO.setPk_overtimetype(headVO.getPk_overtimetype());
				// �Ӱ����Copy
				subVO.setPk_overtimetypecopy(headVO.getPk_overtimetypecopy());
				subVOs.add(subVO);
			}
			Integer state = headVO.getApprove_state();
			if (IPfRetCheckInfo.NOSTATE != state) {
				return true;
			}

			IOvertimeApplyQueryMaintain service = null;
			try {
				service = ServiceLocator.lookup(IOvertimeApplyQueryMaintain.class);
			} catch (HrssException e) {
				e.alert();
			}
			// ����Ƿ������־
			String confirmFlag = getLifeCycleContext().getParameter("isContinue");
			if (StringUtils.isEmpty(confirmFlag)) {
				if (!Boolean.TRUE.equals(result)) {
					// ����ǰ����֤
					ValidationFailure failur = new PFSaveOvertimeValidator().validate(aggVO);
					if (failur != null) {
						CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0169"), failur.getMessage());
					}
					// У�鿼�ڹ������ڼ��ڼӰ�ʱ������
					String errorMsg = service.checkOvertimeLength(headVO.getPk_org(), subVOs.toArray(new OvertimebVO[0]));

					if (!StringUtils.isEmpty(errorMsg)) {
						// ���ϸ���� �Ӱ�Сʱ��
						AppInteractionUtil.showConfirmDialog(DIALOG_SAVE, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0075")/*
																																							 * @
																																							 * res
																																							 * "ѯ��"
																																							 */, errorMsg, null);
						if (!AppInteractionUtil.getConfirmDialogResult(DIALOG_SAVE)) {
							return false;
						}
					}

				}
				AggOvertimeVO aggOvertimeVO = (AggOvertimeVO) aggVO;
				OvertimehVO mainvo = aggOvertimeVO.getOvertimehVO();
				if (!Boolean.TRUE.equals(resultIsNeedCheck)) {
					// �Ӱ൥û��ѡ�мӰ�У���ʶ��ʾУ��
					IOvertimeApplyQueryMaintain sevice = NCLocator.getInstance().lookup(IOvertimeApplyQueryMaintain.class);
					String checkIsNeed = sevice.checkIsNeed(mainvo.getPk_org(), subVOs.toArray(new OvertimebVO[0]));
					if (!StringUtils.isEmpty(checkIsNeed)) {
						AppInteractionUtil.showConfirmDialog(DIALOG_CHECK_ISNEED, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0075")/*
																																									 * @
																																									 * res
																																									 * "ѯ��"
																																									 */, checkIsNeed, null);
						if (!AppInteractionUtil.getConfirmDialogResult(DIALOG_CHECK_ISNEED)) {
							return false;
						}
					}
				}
				if (IPfRetCheckInfo.NOSTATE == state && !Boolean.TRUE.equals(resultHoliday)) {
					// �����յļӰ����ͺͼӰ������ļӰ����͵�һ����У��
					Map<String, String[]> holidayMap = service.checkOverTimeHoliday(headVO.getPk_org(), subVOs.toArray(new OvertimebVO[0]));
					if (holidayMap != null && holidayMap.size() > 0) {
						String psnNames = holidayMap.keySet().toArray(new String[0])[0];
						String errorMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("6017overtime", "06017overtime0053"/*
																																	 * @
																																	 * res
																																	 * "������Ա�Ӱ��������ռӰ����һ�£�"
																																	 */) + psnNames;

						if (!StringUtils.isEmpty(errorMsg)) {
							AppInteractionUtil.showConfirmDialog(DIALOG_SAVE_1, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0075")/*
																																								 * @
																																								 * res
																																								 * "ѯ��"
																																								 */, errorMsg, null);
							if (!AppInteractionUtil.getConfirmDialogResult(DIALOG_SAVE_1)) {
								return false;
							}

						}
					}

				}

				// ����ǰ��У��һ�Σ�����е��ݳ�ͻ�������Ǳ�����ģ�����ʾ��Щ��ͻ���ݣ���ѯ���û��Ƿ񱣴�
				Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkResult = null;
				try {
					checkResult = service.check((AggOvertimeVO) aggVO);
				} catch (BillMutexException e) {
					AwaySaveProcessor.showConflictInfoList((e), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0007")/*
																																				 * @
																																				 * res
																																				 * "�����е�����ʱ���ͻ���������ܼ���"
																																				 */, ShopTaApplyConsts.DIALOG_ALERT);
					throw new BillMutexException();
				}
				if (checkResult != null) {
					AwaySaveProcessor.showConflictInfoList(new BillMutexException(null, checkResult), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0008")/*
																																														 * @
																																														 * res
																																														 * "�����е�����ʱ���ͻ���Ƿ񱣴�?"
																																														 */, ShopTaApplyConsts.DIALOG_CONFIRM);
					throw new BillMutexException();
				}
			}
			return true;
		}

		/**
		 * �������
		 * 
		 * @param aggVo
		 * @return
		 */
		@Override
		public AggregatedValueObject onVOSave(AggregatedValueObject aggVO) throws Exception {

			AggOvertimeVO newAggVo = null;
			AggOvertimeVO aggOverTimeVO = (AggOvertimeVO) aggVO;
			String primaryKey = aggOverTimeVO.getOvertimehVO().getPrimaryKey();

			try {
				IOvertimeApplyApproveManageMaintain service = ServiceLocator.lookup(IOvertimeApplyApproveManageMaintain.class);
				if (StringUtils.isEmpty(primaryKey)) {
					newAggVo = service.insertData(aggOverTimeVO);
				} else {
					newAggVo = service.updateData(aggOverTimeVO);
				}
			} catch (HrssException e) {
				new HrssException(e).alert();
			} catch (BusinessException e) {
				new HrssException(e).deal();
			}

			return newAggVo;
		}

		/**
		 * ��������
		 */
		@Override
		public AggregatedValueObject onAfterVOSave(Dataset ds, Dataset[] dsDetails, AggregatedValueObject aggVO) throws Exception {
			// �رյ���ҳ��
			CmdInvoker.invoke(new CloseWindowCmd());
			ApplicationContext applicationContext = getLifeCycleContext().getApplicationContext();
			applicationContext.closeWinDialog("OverTimeApply");
			// ִ������ݲ�ѯ
			CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET, "closewindow"));
			return null;
		}

		private AppLifeCycleContext getLifeCycleContext() {
			return AppLifeCycleContext.current();
		}

}
