package nc.ui.wa.payfile.view;

import nc.ui.hr.comp.trn.PsnChangeDlg;
import nc.ui.wa.payfile.common.ShowChangePsn;
import nc.vo.hi.pub.CommonValue;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.pub.WaLoginContext;

import org.apache.commons.lang.StringUtils;

/**
 * 变动人员对话框
 * 
 * @author: liangxr
 * @date: 2010-4-1 下午02:20:15
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WaPsnChangeDia extends PsnChangeDlg {

	private static final long serialVersionUID = 7531876897531230834L;

	public WaPsnChangeDia(WaLoginContext context, UFLiteralDate beginDate, UFLiteralDate endDate) {
		super(context, beginDate, endDate);
	}

	public WaPsnChangeDia(WaLoginContext context, UFLiteralDate beginDate, UFLiteralDate endDate, String pk_usetype,
			String pk_trnstype, String pk_trnsreason) {
		super(context, beginDate, endDate, pk_usetype, pk_trnstype, pk_trnsreason);
	}

	@Override
	public String getAddWhere(int trnType) {
		String strWhere = ShowChangePsn.getAddWhere((WaLoginContext) getContext(), trnType);

		if (!StringUtils.isEmpty(strWhere)) {
			if (!StringUtils.isEmpty(getPk_usetype())) {
				strWhere += " and psnjob.jobglbdef8 in (" + getPk_usetype() + ") ";
			}

			if (!StringUtils.isEmpty(getPk_trnstype())) {
				strWhere += " and psnjob.trnstype='" + getPk_trnstype() + "' ";
			}

			if (!StringUtils.isEmpty(getPk_trnsreason())) {
				strWhere += " and psnjob.trnsreason='" + getPk_trnsreason() + "' ";
			}
		} else {
			if (!StringUtils.isEmpty(getPk_usetype())) {
				strWhere += " psnjob.jobglbdef8='" + getPk_usetype() + "' ";
			}

			if (!StringUtils.isEmpty(getPk_trnstype())) {
				if (!StringUtils.isEmpty(strWhere)) {
					strWhere += " and psnjob.trnstype='" + getPk_trnstype() + "' ";
				} else {
					strWhere += " psnjob.trnstype='" + getPk_trnstype() + "' ";
				}
			}

			if (!StringUtils.isEmpty(getPk_trnsreason())) {
				if (!StringUtils.isEmpty(strWhere)) {
					strWhere += " and psnjob.trnsreason='" + getPk_trnsreason() + "' ";
				} else {
					strWhere += " psnjob.trnsreason='" + getPk_trnsreason() + "' ";
				}
			}
		}

		return strWhere;
	}

	@Override
	public Integer[] getInitTabs() {
		return new Integer[] { CommonValue.TRN_ADD, CommonValue.TRN_SUB, CommonValue.TRN_PART_ADD,
				CommonValue.TRN_PART_SUB, CommonValue.TRN_POST_MOD };
	}
}
