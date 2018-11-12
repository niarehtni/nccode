package nc.bs.hrss.ta.signcard.lsnr;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.utils.TBMPsndocUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.bs.uif2.validation.ValidationFailure;
import nc.hr.utils.ResHelper;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.itf.ta.ISignCardApplyApproveManageMaintain;
import nc.itf.ta.ISignCardRegisterManageMaintain;
import nc.itf.ta.ITBMPsndocQueryMaintain;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.signcard.AggSignVO;
import nc.vo.ta.signcard.SignCardBeyondTimeVO;
import nc.vo.ta.signcard.SignbVO;
import nc.vo.ta.signcard.SignhVO;
import nc.vo.ta.signcard.pf.validator.PFSaveSignCardValidator;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class SignCardSaveProcessor implements ISaveProcessor {
	// ǩ�����뱣��ʱ��ȷ�϶Ի����Id
	private String DIALOG_SAVE = "dlg_signcard_save";

	/**
	 * ����ǰ����
	 *
	 * @param aggVo
	 * @return
	 */
	public void onBeforeVOSave(AggregatedValueObject aggVO) {

	}

	/**
	 * ����ǰ����
	 *
	 * @param aggVo
	 * @return
	 */
	public boolean checkBeforeVOSave(AggregatedValueObject aggVO) throws Exception {
		AggSignVO aggSignVO = (AggSignVO) aggVO;
		SignhVO mainvo = aggSignVO.getSignhVO();
		SignbVO[] vos = aggSignVO.getSignbVOs();
		for (SignbVO subvo : vos) {
			subvo.setSigndate(UFLiteralDate.getDate(subvo.getSigntime().getDate().toString()));
			subvo.setPk_psndoc(mainvo.getPk_psndoc());
			subvo.setPk_psnjob(mainvo.getPk_psnjob());
			subvo.setPk_psnorg(mainvo.getPk_psnorg());
			subvo.setPk_org(mainvo.getPk_org());
			subvo.setPk_group(mainvo.getPk_group());
		}
		// У��
		ValidationFailure failur = new PFSaveSignCardValidator().validate(aggVO);
		if (failur != null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0169"),failur.getMessage());
		}
		
		// У��ǩ��ʱ���Ƿ��ڵ�ǰ���ڵ�������Чʱ����
		String sb = checkSignOrg(aggVO);
		if(!StringUtils.isEmpty(sb)){
			sb += nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0174")/**@res"���ڵ�ǰ���ڵ�������Ч���ڷ�Χ�ڣ�"*/;
			throw new LfwRuntimeException(sb);
		}
		
		// У��ǩ������,����У����Ϣ
		String errorMsg = checkSignCount(aggVO);
		if (!StringUtils.isEmpty(errorMsg)) {
			AppInteractionUtil.showConfirmDialog(DIALOG_SAVE,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0075")/*@res "ѯ��"*/, errorMsg, null);
			if (!AppInteractionUtil.getConfirmDialogResult(DIALOG_SAVE)) {
				return false;
			}
		}

		return true;
	}
	
	/**
	 * У��ǩ��ʱ���Ƿ��ڵ�ǰ���ڵ�������Чʱ����
	 * 
	 * @param aggVO
	 * @return
	 */
	private String checkSignOrg(AggregatedValueObject aggVO){
		
		AggSignVO aggSignVO = (AggSignVO) aggVO;
		SignbVO[] vos = aggSignVO.getSignbVOs();
		// ���µĿ��ڵ���
		//TBMPsndocVO tbmPsndocVO = TaAppContextUtil.getTBMPsndocVO();
		TBMPsndocVO tbmPsndocVO = null;
		try {
			tbmPsndocVO = NCLocator.getInstance().lookup(ITBMPsndocQueryMaintain.class)
					.queryByPsndocAndDate(
							aggSignVO.getSignhVO().getPk_psndoc(),
							(UFLiteralDate)aggSignVO.getSignhVO().getApply_date());
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		UFLiteralDate beginDate = tbmPsndocVO.getBegindate();
//		UFLiteralDate endDate = tbmPsndocVO.getEnddate();
		
		StringBuffer sb = new StringBuffer();
		UFLiteralDate signDate = null;
		Set<String> dateSet = new HashSet<String>();
		TBMPsndocVO tempPsndocvo = null;
		//String pk_psndoc = SessionUtil.getPk_psndoc();
		String pk_psndoc = aggSignVO.getSignhVO().getPk_psndoc();//ֱ�Ӷ�ȡ������������� Ares.Tank 2018-7-20 17:52:11
		for (SignbVO subvo : vos) {
			signDate = subvo.getSigndate();
			//2013-06-26 wangbine
//			signDate = new UFLiteralDate(subvo.getSigntime().toString());
//			if((beginDate.after(signDate)) || endDate != null && endDate.before(signDate)){
//				if(!dateList.contains(signDate.toString())){
//					dateList.add(signDate.toString());
//				}
//			}
			tempPsndocvo = TBMPsndocUtil.getTBMPsndoc(pk_psndoc, subvo.getSigntime());
			if(tempPsndocvo == null){
				dateSet.add(signDate.toString());
			}else{
				if(!tempPsndocvo.getPk_org().equals(tbmPsndocVO.getPk_org()))
					dateSet.add(signDate.toString());
			}
		}
		if(!dateSet.isEmpty()){
			for(String date : dateSet){
				sb.append(date + ",");
			}
		}
		return sb.length() == 0 ? null : sb.substring(0, sb.length()-1);
	}

	/**
	 * У��ǩ������,����У����Ϣ
	 *
	 * @param aggVO
	 * @return
	 */
	private String checkSignCount(AggregatedValueObject aggVO) {

		AggSignVO aggSignVO = (AggSignVO) aggVO;
		SignhVO mainvo = aggSignVO.getSignhVO();
		SignbVO[] vos = aggSignVO.getSignbVOs();
		for (SignbVO subvo : vos) {
			subvo.setPk_psndoc(mainvo.getPk_psndoc());
			subvo.setPk_psnjob(mainvo.getPk_psnjob());
			subvo.setPk_psnorg(mainvo.getPk_psnorg());
			subvo.setPk_org(mainvo.getPk_org());
			subvo.setPk_group(mainvo.getPk_group());
		}
		// ���������ڵ�Ĺ�������ӿ�
		ISignCardRegisterManageMaintain service = null;
		try {
			service = ServiceLocator.lookup(ISignCardRegisterManageMaintain.class);
		} catch (HrssException e) {
			e.alert();
		}
		// ��У��ǩ������
		SignCardBeyondTimeVO[] beyondVOs = null;
		try {
			beyondVOs = service.vldAndGetBydPrt(aggSignVO.getSignhVO().getPk_org(), aggSignVO.getSignbVOs());
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		if (ArrayUtils.isEmpty(beyondVOs)) {
			return null;
		}
		StringBuilder sb = new StringBuilder(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0108")/*@res "����Ա"*/);
		for (SignCardBeyondTimeVO vo : beyondVOs) {
			sb.append(MessageFormat.format(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0109")/*@res "�ڿ����ڼ䡾{0}��ǩ�����������˹涨��ǩ������{1}�Σ�"*/,
					vo.getPeriod(), TaAppContextUtil.getTimeRuleVO().getSigncounts().toString() ));
		}
		sb.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0110")/*@res "�Ƿ����?"*/);

		return sb.toString();

	}

	/**
	 * �������
	 *
	 * @param aggVo
	 * @return
	 */
	public AggregatedValueObject onVOSave(AggregatedValueObject aggVO) {
		AggSignVO newAggVo = null;
		AggSignVO aggSignVO = (AggSignVO) aggVO;
		String primaryKey = aggSignVO.getSignhVO().getPrimaryKey();
		//����곤�ڵ㵥��״̬��ʧ������,Ϊ����Ĭ��Ϊ2 Ares.Tank 2018-7-20 21:30:04 start
		SignbVO[] signbvos = aggSignVO.getSignbVOs();
		if(signbvos!=null&&signbvos.length>0){
			for(int i =0;i<signbvos.length;i++){
				if(signbvos[i]!=null&&signbvos[i].getSignstatus()==null){
					signbvos[i].setSignstatus(2);//(0Ϊ��,1Ϊ��)
				}
			}
		}
		//����곤�ڵ㵥��״̬��ʧ������,Ϊ����Ĭ��Ϊ2(0Ϊ��,1Ϊ��) Ares.Tank 2018-7-20 21:30:04 end
		// ���������ڵ�Ĺ�������ӿ�
		ISignCardApplyApproveManageMaintain service = null;
		try {
			service = ServiceLocator.lookup(ISignCardApplyApproveManageMaintain.class);
			if (StringUtils.isEmpty(primaryKey)) {
				newAggVo = service.insertData(aggSignVO);
			} else {
				newAggVo = service.updateData(aggSignVO);
			}
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			e.alert();
		}
		return newAggVo;
	}

	/**
	 * ��������
	 */
	public AggregatedValueObject onAfterVOSave(Dataset ds, Dataset[] dsDetails, AggregatedValueObject aggVO) {
		// �رյ���ҳ��
		CmdInvoker.invoke(new CloseWindowCmd());
		ApplicationContext applicationContext = AppLifeCycleContext.current().getApplicationContext();
		applicationContext.closeWinDialog("SignCardApply");
		// ִ������ݲ�ѯ
		CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET, "closewindow"));
		return null;
	}

}