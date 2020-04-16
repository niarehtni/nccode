package nc.ui.wa.payfile.common;

import java.util.ArrayList;
import java.util.List;

import nc.bs.logging.Logger;
import nc.bs.uif2.IActionCode;
import nc.bs.uif2.validation.ValidationException;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.itf.hr.wa.WaPowerSqlHelper;
import nc.ui.hr.comp.trn.PsnChangeDlg;
import nc.ui.hr.util.HrDataPermHelper;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.wa.payfile.view.AddPayfileDlg;
import nc.ui.wa.payfile.view.WaPsnChangeDia;
import nc.ui.wa.pub.WADelegator;
import nc.vo.hi.pub.CommonValue;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.comp.trn.PsnChangeVO;
import nc.vo.hr.comp.trn.PsnTrnVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.payfile.PsnChangeHelper;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wabm.util.HRWADateConvertor;

import org.apache.commons.lang.StringUtils;

/**
 * 显示变动人员对话框
 * 
 * @author: liangxr
 * @date: 2010-1-26 下午01:37:04
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class ShowChangePsn {

	private WaLoginContext context = null;

	public ShowChangePsn(WaLoginContext context) {
		this.context = context;
	}

	/**
	 * 获取变动人员查询Sql片断
	 * 
	 * @author liangxr on 2010-4-2
	 * @param context
	 * @param trnType
	 * @return
	 * @throws BusinessException
	 */
	public static String getAddWhere(WaLoginContext context, int trnType) {

		try {
			String sqlwhere = WADelegator.getPayfileQuery().getAddWhere(context, trnType, true);
			// 20151216 xiejie3 测试xiaorong要求此处的变动人员页签不进行人员权限控制，和社保的变动页签保持一致。
			String powerSql = null;
			if (CommonValue.TRN_POST_MOD != trnType) {
				powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB,
						IHRWADataResCode.WADEFAULT, "psnjob");
			}
			// end
			if (!StringUtils.isBlank(powerSql)) {
				sqlwhere = sqlwhere + " and " + powerSql;
			}
			return sqlwhere;

		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			MessageDialog.showErrorDlg(context.getEntranceUI(), null, e.getMessage());
		}

		return " ( 1=2 )";
	}

	/**
	 * 显示变动人员对话框
	 * 
	 * @author liangxr on 2010-4-7
	 * @param beginDate
	 * @param endDate
	 * @throws BusinessException
	 */
	public int showChangePsnDialog(UFDate beginDate, UFDate endDate) throws BusinessException {
		UFLiteralDate beginLDate = HRWADateConvertor.toUFLiteralDate(beginDate);
		UFLiteralDate endLDate = HRWADateConvertor.toUFLiteralDate(endDate);
		PsnChangeDlg changeDia = new WaPsnChangeDia(context, beginLDate, endLDate);
		changeDia.showModal();

		if (changeDia.getResult() == UIDialog.ID_OK) {
			PsnChangeVO psnChangeVO = changeDia.getSelectedPks();
			dealWithChangePsn(psnChangeVO);
		}
		return changeDia.getResult();
	}

	public int showChangePsnDialog(UFDate beginDate, UFDate endDate, String pk_usetype, String pk_trnstype,
			String pk_trnsreason) throws BusinessException {
		UFLiteralDate beginLDate = HRWADateConvertor.toUFLiteralDate(beginDate);
		UFLiteralDate endLDate = HRWADateConvertor.toUFLiteralDate(endDate);
		PsnChangeDlg changeDia = new WaPsnChangeDia(context, beginLDate, endLDate, pk_usetype, pk_trnstype,
				pk_trnsreason);
		changeDia.showModal();

		if (changeDia.getResult() == UIDialog.ID_OK) {
			PsnChangeVO psnChangeVO = changeDia.getSelectedPks();
			dealWithChangePsn(psnChangeVO);
		}
		return changeDia.getResult();
	}

	public void dealWithChangePsn(PsnChangeVO psnChangeVO) throws BusinessException {
		if (psnChangeVO.getPsnVOlist() == null || psnChangeVO.getPsnVOlist().size() == 0) {
			throw new BusinessException(ResHelper.getString("60130payfile", "060130payfile0258")/*
																								 * @
																								 * res
																								 * "没有选择人员！"
																								 */);
		}

		int state = psnChangeVO.getState();

		if (PsnChangeHelper.isExists(state)) {
			deleteSelectePsn(psnChangeVO);
		} else {
			addSelectedPsn(psnChangeVO, state);
		}
	}

	/**
	 * 将人员增加到薪资档案
	 * 
	 * @author liangxr on 2010-2-25
	 * @param psnChangeVO
	 * @param state
	 * @throws BusinessException
	 */
	private void addSelectedPsn(PsnChangeVO psnChangeVO, int state) throws BusinessException {

		AddPayfileDlg dlg = new AddPayfileDlg(context);
		dlg.showModal();
		if (dlg.getResult() == UIDialog.ID_OK) {
			// UFBoolean partflag = UFBoolean.FALSE;
			// if(state==3){
			// partflag = UFBoolean.TRUE;
			// }
			// 界面的填写数据。
			PayfileVO[] selectVOs = getSelectedPsns(psnChangeVO);
			PayfileVO resultvo = dlg.getResultVO();
			for (PayfileVO selectvo : selectVOs) {
				selectvo.setStopflag(resultvo.getStopflag());// 停发标志
				selectvo.setIsndebuct(resultvo.getIsndebuct());// 费用扣除额
				selectvo.setTaxtableid(resultvo.getTaxtableid());// 税率表
				selectvo.setIsderate(resultvo.getIsderate());// 是否减免税
				selectvo.setDerateptg(resultvo.getDerateptg());// 减税比例
				selectvo.setTaxtype(resultvo.getTaxtype());// 扣税方式
				// selectvo.setPartflag(partflag);
			}
			// 增加到薪资档案
			WADelegator.getPayfile().addPsnVOs(selectVOs);
		}
	}

	/**
	 * 从薪资档案删除记录
	 * 
	 * @author liangxr on 2010-2-25
	 * @param psnChangeVO
	 * @throws BusinessException
	 */
	private void deleteSelectePsn(PsnChangeVO psnChangeVO) throws BusinessException {
		PayfileVO[] needDeletePsnVOs = getSelectedPsns(psnChangeVO);
		if (needDeletePsnVOs != null && needDeletePsnVOs.length > 0) {
			checkDataPermission(needDeletePsnVOs);

			// 批量删除
			WADelegator.getPayfile().delPsnVOs(needDeletePsnVOs);
		}
	}

	/***************************************************************************
	 * 检查是否有权限操作数据<br>
	 * Created on 2011-5-5 11:02:09<br>
	 * 
	 * @throws BusinessException
	 * @author Rocex Wang
	 ***************************************************************************/
	protected void checkDataPermission(Object objData) throws BusinessException {

		ValidationException ex = HrDataPermHelper.checkDataPermission(IHRWADataResCode.PAYFILE, IActionCode.DELETE,
				IActionCode.DELETE, objData, context);

		HrDataPermHelper.dealValidationException(ex);
	}

	/**
	 * 获取选中的变动人员
	 */
	private PayfileVO[] getSelectedPsns(PsnChangeVO psnChangeVO) {
		List<PayfileVO> list = new ArrayList<PayfileVO>();
		if (psnChangeVO.getPsnVOlist() != null) {
			PayfileVO psnVO = null;
			for (PsnTrnVO trnVO : psnChangeVO.getPsnVOlist()) {
				psnVO = new PayfileVO();
				psnVO.setPk_wa_data(trnVO.getPk_wa_data());
				psnVO.setPk_psnjob(trnVO.getPk_psnjob());
				psnVO.setPk_group(context.getPk_group());
				psnVO.setPk_org(context.getPk_org());
				psnVO.setPk_psndoc(trnVO.getPk_psndoc());
				psnVO.setPk_psnorg(trnVO.getPk_psnorg());
				psnVO.setPk_wa_class(context.getPk_wa_class());
				psnVO.setPk_prnt_class(context.getPk_prnt_class());
				psnVO.setCyear(context.getWaYear());
				psnVO.setCperiod(context.getWaPeriod());
				psnVO.setCyearperiod(context.getCyear() + context.getCperiod());

				psnVO.setCaculateflag(UFBoolean.valueOf(false));
				psnVO.setCheckflag(UFBoolean.valueOf(false));
				psnVO.setRedtotal(new UFDouble(0));

				psnVO.setPartflag(UFBoolean.TRUE.equals(trnVO.getIsmainjob()) ? UFBoolean.FALSE : UFBoolean.TRUE);
				psnVO.setAssgid(trnVO.getAssgid());
				// 兼职人员的工作组织 工作部门
				psnVO.setWorkdept(trnVO.getWorkdept());
				psnVO.setWorkdeptvid(trnVO.getWorkdeptvid());
				psnVO.setWorkorg(trnVO.getWorkorg());
				psnVO.setWorkorgvid(trnVO.getWorkorgvid());
				list.add(psnVO);
			}

		}
		return list.toArray(new PayfileVO[list.size()]);

	}

}