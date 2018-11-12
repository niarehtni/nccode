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
	// 签卡申请保存时的确认对话框的Id
	private String DIALOG_SAVE = "dlg_signcard_save";

	/**
	 * 保存前操作
	 *
	 * @param aggVo
	 * @return
	 */
	public void onBeforeVOSave(AggregatedValueObject aggVO) {

	}

	/**
	 * 保存前操作
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
		// 校验
		ValidationFailure failur = new PFSaveSignCardValidator().validate(aggVO);
		if (failur != null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0169"),failur.getMessage());
		}
		
		// 校验签卡时间是否在当前考勤档案的有效时间内
		String sb = checkSignOrg(aggVO);
		if(!StringUtils.isEmpty(sb)){
			sb += nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0174")/**@res"不在当前考勤档案的有效日期范围内！"*/;
			throw new LfwRuntimeException(sb);
		}
		
		// 校验签卡次数,返回校验信息
		String errorMsg = checkSignCount(aggVO);
		if (!StringUtils.isEmpty(errorMsg)) {
			AppInteractionUtil.showConfirmDialog(DIALOG_SAVE,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0075")/*@res "询问"*/, errorMsg, null);
			if (!AppInteractionUtil.getConfirmDialogResult(DIALOG_SAVE)) {
				return false;
			}
		}

		return true;
	}
	
	/**
	 * 校验签卡时间是否在当前考勤档案的有效时间内
	 * 
	 * @param aggVO
	 * @return
	 */
	private String checkSignOrg(AggregatedValueObject aggVO){
		
		AggSignVO aggSignVO = (AggSignVO) aggVO;
		SignbVO[] vos = aggSignVO.getSignbVOs();
		// 最新的考勤档案
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
		String pk_psndoc = aggSignVO.getSignhVO().getPk_psndoc();//直接读取单据里面的姓名 Ares.Tank 2018-7-20 17:52:11
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
	 * 校验签卡次数,返回校验信息
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
		// 申请审批节点的管理操作接口
		ISignCardRegisterManageMaintain service = null;
		try {
			service = ServiceLocator.lookup(ISignCardRegisterManageMaintain.class);
		} catch (HrssException e) {
			e.alert();
		}
		// 先校验签卡次数
		SignCardBeyondTimeVO[] beyondVOs = null;
		try {
			beyondVOs = service.vldAndGetBydPrt(aggSignVO.getSignhVO().getPk_org(), aggSignVO.getSignbVOs());
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		if (ArrayUtils.isEmpty(beyondVOs)) {
			return null;
		}
		StringBuilder sb = new StringBuilder(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0108")/*@res "该人员"*/);
		for (SignCardBeyondTimeVO vo : beyondVOs) {
			sb.append(MessageFormat.format(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0109")/*@res "在考勤期间【{0}】签卡次数超过了规定的签卡次数{1}次，"*/,
					vo.getPeriod(), TaAppContextUtil.getTimeRuleVO().getSigncounts().toString() ));
		}
		sb.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0110")/*@res "是否继续?"*/);

		return sb.toString();

	}

	/**
	 * 保存操作
	 *
	 * @param aggVo
	 * @return
	 */
	public AggregatedValueObject onVOSave(AggregatedValueObject aggVO) {
		AggSignVO newAggVo = null;
		AggSignVO aggSignVO = (AggSignVO) aggVO;
		String primaryKey = aggSignVO.getSignhVO().getPrimaryKey();
		//解决店长节点单据状态丢失的问题,为空则默认为2 Ares.Tank 2018-7-20 21:30:04 start
		SignbVO[] signbvos = aggSignVO.getSignbVOs();
		if(signbvos!=null&&signbvos.length>0){
			for(int i =0;i<signbvos.length;i++){
				if(signbvos[i]!=null&&signbvos[i].getSignstatus()==null){
					signbvos[i].setSignstatus(2);//(0为进,1为出)
				}
			}
		}
		//解决店长节点单据状态丢失的问题,为空则默认为2(0为进,1为出) Ares.Tank 2018-7-20 21:30:04 end
		// 申请审批节点的管理操作接口
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
	 * 保存后操作
	 */
	public AggregatedValueObject onAfterVOSave(Dataset ds, Dataset[] dsDetails, AggregatedValueObject aggVO) {
		// 关闭弹出页面
		CmdInvoker.invoke(new CloseWindowCmd());
		ApplicationContext applicationContext = AppLifeCycleContext.current().getApplicationContext();
		applicationContext.closeWinDialog("SignCardApply");
		// 执行左侧快捷查询
		CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET, "closewindow"));
		return null;
	}

}