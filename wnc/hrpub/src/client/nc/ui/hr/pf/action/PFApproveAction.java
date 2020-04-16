package nc.ui.hr.pf.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.IActionCode;
import nc.itf.hi.IPsndocSubInfoService4JFS;
import nc.ui.hr.comp.pf.DirectApproveDialog;
import nc.ui.pf.workitem.beside.BesideApproveContext;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.trade.businessaction.IPFACTION;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.components.widget.IBesideApproveContext;
import nc.vo.pf.change.PfUtilBaseTools;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.trn.transmng.AggStapply;
import nc.vo.uap.pf.PFBatchExceptionInfo;
import nc.vo.uap.pf.PfProcessBatchRetObject;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/***************************************************************************
 * <br>
 * Created on 2011-3-20 13:37:37<br>
 * 
 * @author sunpenga
 ***************************************************************************/
public class PFApproveAction extends PFBaseAction implements IBesideApproveContext {
	private BesideApproveContext besideApproveContext;

	/***************************************************************************
	 * Created on 2011-3-20 13:37:34<br>
	 * 
	 * @author sunpenga
	 ***************************************************************************/
	public PFApproveAction() {
		super();

		ActionInitializer.initializeAction(this, IActionCode.APPROVE);
	}

	/***************************************************************************
	 * ������У�� ����������У��ĵ�����д�˷���<br>
	 * Created on 2011-3-20 13:37:46<br>
	 * 
	 * @return PfProcessBatchRetObject
	 * @throws Exception
	 * @author sunpenga
	 ***************************************************************************/
	protected PfProcessBatchRetObject approveValidate() throws Exception {
		return getIHrPf().approveValidation(getOperateCode(), getMdOperateCode(), getResourceCode(), selectData);
	}

	/***************************************************************************
	 * <br>
	 * Created on 2011-3-20 13:37:48<br>
	 * 
	 * @param retObj
	 *            PfProcessBatchRetObject
	 * @throws BusinessException
	 * @author sunpenga
	 * @return PfProcessBatchRetObject
	 ***************************************************************************/
	protected PfProcessBatchRetObject directApprove(PfProcessBatchRetObject retObj) throws BusinessException {
		int iApproveResult;
		String strApproveNote;

		if (getBesideApproveContext() != null) {
			strApproveNote = getBesideApproveContext().getCheckNote();
			iApproveResult = Integer.valueOf(getBesideApproveContext().getApproveResult());
		} else {
			DirectApproveDialog approveDialog = new DirectApproveDialog(getContext().getEntranceUI());

			iApproveResult = approveDialog.showModal();
			strApproveNote = approveDialog.getApproveNote();
		}

		// û�е���������ťʱ��������
		if (!ArrayUtils.contains(new int[] { DirectApproveDialog.PF_APPROVE_APPROVED,
				DirectApproveDialog.PF_APPROVE_REJECTED, DirectApproveDialog.PF_APPROVE_RETURN }, iApproveResult)) {
			setCancelMsg();

			return null;
		}

		return getModel().doBatchDirectApprove(iApproveResult, strApproveNote, retObj);
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2011-3-20 13:35:21<br>
	 * 
	 * @see nc.ui.hr.pf.action.PFBaseAction#doAction(java.awt.event.ActionEvent)
	 * @author sunpenga
	 ****************************************************************************/
	@Override
	public void doAction(ActionEvent evt) throws Exception {
		super.doAction(evt);

		PfProcessBatchRetObject validateRetObj = approveValidate();

		if (validateRetObj == null) {
			return;
		}

		if (validateRetObj.getRetObj() == null || validateRetObj.getRetObj().length == 0) {
			throw new ValidationException(validateRetObj.getExceptionMsg());
		}

		PfProcessBatchRetObject apprRetObj = null;

		if (isDirectApprove(validateRetObj.getRetObj())) {
			apprRetObj = directApprove(validateRetObj);// ��ֱ��
		} else {
			try {
				apprRetObj = pfApprove(validateRetObj);// ������
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);

				if (validateRetObj.getRetObj().length > 1) {
					throw new ValidationException(e.getMessage());
				}

				// ����һ��������������쳣Ҫ��������
				PFBatchExceptionInfo errinfo = new PFBatchExceptionInfo();
				errinfo.putErrorMessage(1, validateRetObj.getRetObj()[0], e.getMessage());
				apprRetObj = new PfProcessBatchRetObject(new Object[0], errinfo);
			}
		}
		// ���apprRetObj��Ϊ�գ����д�������ڵ���Ա��Ϣ�Ӽ�������Ϣ���ͽ����Ӽ� he
		if (null != apprRetObj) {
			AggStapply[] aggvos = (AggStapply[]) apprRetObj.getRetObj();

			NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class).finishInsurance(aggvos);
		}

		// ������Ϣ������,����������ִ�д���
		String errMsg = "";
		String msg1 = validateRetObj.getExceptionMsg();
		String msg2 = apprRetObj == null ? "" : apprRetObj.getExceptionMsg();

		if (StringUtils.isNotBlank(msg1) || StringUtils.isNotBlank(msg2)) {
			errMsg = StringUtils.isBlank(msg1) ? msg2 : (StringUtils.isBlank(msg2) ? msg1 : (msg1 + '\n' + msg2));
			errMsg = StringUtils.removeEnd(errMsg, "\n");
			// errMsg = (StringUtils.isBlank(msg1) ? "" : msg1) + '\n' +
			// (StringUtils.isBlank(msg2) ? "" : msg2);
		}

		if (apprRetObj != null && apprRetObj.getRetObj() != null && apprRetObj.getRetObj().length > 0) {
			// ������浥��
			ArrayList<Object> al = new ArrayList<Object>();
			Object[] objs = apprRetObj.getRetObj();
			for (int i = 0; i < objs.length; i++) {
				if (objs[i] != null) {
					al.add(objs[i]);
				}
			}

			if (al.size() > 0) {
				getModel().directlyUpdate(al.toArray(new Object[0]));
				try {
					getModel().saveAfterBatchApprove(al.toArray(new Object[0]));
				} catch (BusinessException ex) {
					errMsg += '\n' + ex.getMessage();
				}
			}

			// ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getApproveSuccessInfo(),
			// getModel().getContext());

		}
		if (apprRetObj != null && StringUtils.isNotBlank(errMsg)) {
			throw new ValidationException(errMsg);
		}
	}

	@Override
	public boolean getActionEnabled() {
		return this.isActionEnable();
	}

	@Override
	public BesideApproveContext getBesideApproveContext() {
		return this.besideApproveContext;
	}

	/**
	 * @param bills
	 * @return �жϵ�ǰ��������ʽ
	 * @throws BusinessException
	 */
	protected boolean isDirectApprove(Object[] bills) throws BusinessException {
		return getModel().isDirectApprove();
	}

	/***************************************************************************
	 * ����������<br>
	 * Created on 2011-3-20 13:35:26<br>
	 * 
	 * @param retObj
	 * @throws Exception
	 * @author sunpenga
	 * @return PfProcessBatchRetObject
	 ***************************************************************************/
	protected PfProcessBatchRetObject pfApprove(PfProcessBatchRetObject retObj) throws Exception {
		try {
			HashMap parm = new HashMap();
			if (getBesideApproveContext() != null) {
				parm.put(PfUtilBaseTools.PARAM_BESIDEAPPROVE, getBesideApproveContext());
			}
			return PfUtilClient.runBatchNew(getModel().getContext().getEntranceUI(), IPFACTION.APPROVE
					+ getContext().getPk_loginUser(), getModel().getBillType(),
					(AggregatedValueObject[]) retObj.getRetObj(), null, null, parm);
		} finally {
			// ��Ҫ��besideApproveContext��գ������´ξͲ��ᵯ�������Ի�����
			setBesideApproveContext(null);
		}
	}

	@Override
	public void setBesideApproveContext(BesideApproveContext besideApproveContext) {
		this.besideApproveContext = besideApproveContext;
	}
}
