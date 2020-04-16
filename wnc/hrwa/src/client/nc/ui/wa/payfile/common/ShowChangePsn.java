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
 * ��ʾ�䶯��Ա�Ի���
 * 
 * @author: liangxr
 * @date: 2010-1-26 ����01:37:04
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class ShowChangePsn {

	private WaLoginContext context = null;

	public ShowChangePsn(WaLoginContext context) {
		this.context = context;
	}

	/**
	 * ��ȡ�䶯��Ա��ѯSqlƬ��
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
			// 20151216 xiejie3 ����xiaorongҪ��˴��ı䶯��Աҳǩ��������ԱȨ�޿��ƣ����籣�ı䶯ҳǩ����һ�¡�
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
	 * ��ʾ�䶯��Ա�Ի���
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
																								 * "û��ѡ����Ա��"
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
	 * ����Ա���ӵ�н�ʵ���
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
			// �������д���ݡ�
			PayfileVO[] selectVOs = getSelectedPsns(psnChangeVO);
			PayfileVO resultvo = dlg.getResultVO();
			for (PayfileVO selectvo : selectVOs) {
				selectvo.setStopflag(resultvo.getStopflag());// ͣ����־
				selectvo.setIsndebuct(resultvo.getIsndebuct());// ���ÿ۳���
				selectvo.setTaxtableid(resultvo.getTaxtableid());// ˰�ʱ�
				selectvo.setIsderate(resultvo.getIsderate());// �Ƿ����˰
				selectvo.setDerateptg(resultvo.getDerateptg());// ��˰����
				selectvo.setTaxtype(resultvo.getTaxtype());// ��˰��ʽ
				// selectvo.setPartflag(partflag);
			}
			// ���ӵ�н�ʵ���
			WADelegator.getPayfile().addPsnVOs(selectVOs);
		}
	}

	/**
	 * ��н�ʵ���ɾ����¼
	 * 
	 * @author liangxr on 2010-2-25
	 * @param psnChangeVO
	 * @throws BusinessException
	 */
	private void deleteSelectePsn(PsnChangeVO psnChangeVO) throws BusinessException {
		PayfileVO[] needDeletePsnVOs = getSelectedPsns(psnChangeVO);
		if (needDeletePsnVOs != null && needDeletePsnVOs.length > 0) {
			checkDataPermission(needDeletePsnVOs);

			// ����ɾ��
			WADelegator.getPayfile().delPsnVOs(needDeletePsnVOs);
		}
	}

	/***************************************************************************
	 * ����Ƿ���Ȩ�޲�������<br>
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
	 * ��ȡѡ�еı䶯��Ա
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
				// ��ְ��Ա�Ĺ�����֯ ��������
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