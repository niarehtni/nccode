package nc.bs.hrsms.ta.sss.away.lsnr;

import java.util.Map;

import nc.bs.hrsms.ta.sss.away.ShopAwayApplyConsts;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.ta.common.TaApplyConsts;
import nc.bs.uif2.validation.ValidationFailure;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.itf.ta.IAwayApplyApproveManageMaintain;
import nc.itf.ta.IAwayApplyQueryMaintain;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.ta.away.AggAwayVO;
import nc.vo.ta.away.AwaybVO;
import nc.vo.ta.away.AwayhVO;
import nc.vo.ta.away.pf.validator.PFSaveAwayValidator;
import nc.vo.ta.bill.BillMutexException;

import org.apache.commons.lang.StringUtils;

public class ShopAwayApplySaveProcessor implements ISaveProcessor {

	/**
	 * ����ǰ����
	 *
	 * @param aggVo
	 * @return
	 * @throws HrssException
	 */
	@Override
	public void onBeforeVOSave(AggregatedValueObject aggVO) throws Exception {

	}

	/**
	 * ����ǰУ�����
	 *
	 * @param aggVo
	 * @return
	 * @throws HrssException
	 */
	@Override
	public boolean checkBeforeVOSave(AggregatedValueObject aggVO) throws Exception {

		AggAwayVO aggAwayVO = (AggAwayVO) aggVO;
		AwayhVO mainvo = aggAwayVO.getAwayhVO();
		//���ڵ����Ѿ���������Ա����������������ǰ������ʱpk_psnorg�ֶ�Ϊ�գ��޷���������
		PsnJobVO psnjobVO = null;
		try {
			psnjobVO = (PsnJobVO) ServiceLocator.lookup(IPersistenceRetrieve.class).retrieveByPk(PsnJobVO.class, mainvo.getPk_psnjob(), null);
		} catch (HrssException e1) {
			throw new Exception(e1);
		}
		mainvo.setPk_psnorg(psnjobVO.getPk_psnorg());
		AwaybVO[] vos = aggAwayVO.getAwaybVOs();
		for (AwaybVO subvo : vos) {
			subvo.setPk_psndoc(mainvo.getPk_psndoc());
			subvo.setPk_psnjob(mainvo.getPk_psnjob());
			subvo.setPk_psnorg(mainvo.getPk_psnorg());
			subvo.setPk_org(mainvo.getPk_org());
			subvo.setPk_group(mainvo.getPk_group());
			subvo.setPk_awaytype(mainvo.getPk_awaytype());
		}

		// ����Ƿ������־
		String confirmFlag = getLifeCycleContext().getParameter("isContinue");
		if (StringUtils.isEmpty(confirmFlag)) {
			// У��
			ValidationFailure failur = new PFSaveAwayValidator().validate(aggVO);
			if (failur != null) {
				throw new LfwRuntimeException(failur.getMessage());
			}
			// ����ǰ��У��һ�Σ�����е��ݳ�ͻ�������Ǳ�����ģ�����ʾ��Щ��ͻ���ݣ���ѯ���û��Ƿ񱣴�
			Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkResult = null;
			try {
				IAwayApplyQueryMaintain service = ServiceLocator.lookup(IAwayApplyQueryMaintain.class);
				checkResult = service.check((AggAwayVO) aggVO);
			} catch (HrssException e) {
				new HrssException(e).alert();
			} catch (BillMutexException e) {
				showConflictInfoList(((BillMutexException) e), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0007")/*@res "�����е�����ʱ���ͻ���������ܼ���"*/, TaApplyConsts.DIALOG_ALERT);
				throw new BillMutexException();
			}
			if (checkResult != null) {
				showConflictInfoList(new BillMutexException(null, checkResult), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0008")/*@res "�����е�����ʱ���ͻ���Ƿ񱣴�?"*/, TaApplyConsts.DIALOG_CONFIRM);
				throw new BillMutexException();
			}
		}
		return true;
	}

	/**
	 * ��ʾ�͵�ǰ�������ͻ�ĵ����б�
	 */
	public static void showConflictInfoList(BillMutexException e, String title, String type) {
		ApplicationContext appCxt = AppLifeCycleContext.current().getApplicationContext();
		Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> result = e.getMutexBillsMap();
		if (result == null || result.size() == 0) {
			return;
		}
		appCxt.addAppAttribute(TaApplyConsts.SESSION_BILLMUTEX_EXCEPTION, e);
		appCxt.addAppAttribute(TaApplyConsts.SESSION_BILLMUTEX_DIALOG_TYPE, type);
		// ����ģ̬�����URL
		String windowId = "BillMutexInfo";
		// ���ù���ģ�鴰����ʾ����
		CommonUtil.showWindowDialog(windowId, title, "900", "600", null, ApplicationContext.TYPE_DIALOG);
	}

	/**
	 * �������
	 *
	 * @param aggVo
	 * @return
	 */
	@Override
	public AggregatedValueObject onVOSave(AggregatedValueObject aggVO) throws Exception {

		AggAwayVO newAggVo = null;
		AggAwayVO aggAwayVO = (AggAwayVO) aggVO;
		String primaryKey = aggAwayVO.getAwayhVO().getPrimaryKey();

		try {
			IAwayApplyApproveManageMaintain service = ServiceLocator.lookup(IAwayApplyApproveManageMaintain.class);
			if (StringUtils.isEmpty(primaryKey)) {
				newAggVo = service.insertData(aggAwayVO);
			} else {
				newAggVo = service.updateData(aggAwayVO);
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
		applicationContext.closeWinDialog(ShopAwayApplyConsts.WIN_CARD_NAME);
		// ִ������ݲ�ѯ
		CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET, "closewindow"));
		return null;
	}

	private AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}

}
