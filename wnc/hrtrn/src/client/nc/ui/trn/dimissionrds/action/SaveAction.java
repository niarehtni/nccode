package nc.ui.trn.dimissionrds.action;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.IActionCode;
import nc.hr.utils.ResHelper;
import nc.itf.hi.IPersonRecordService;
import nc.itf.hi.IPsndocSubInfoService4JFS;
import nc.itf.trn.rds.IRdsManageService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.bill.BillModel;
import nc.ui.trn.dimissionrds.view.DateLarborDlg;
import nc.ui.trn.rds.action.RdsBaseAction;
import nc.ui.trn.rds.model.RdsPsninfoModel;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.TrialVO;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.trn.pub.TRNConst;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;

import org.apache.commons.lang.StringUtils;

public class SaveAction extends RdsBaseAction {

	private boolean isdisablepsn = Boolean.FALSE;

	private String refTransType; // ��ͣ�������
	private String refReturnType;// �}�������
	private String refChangeType = "1002Z710000000008GSN";// ת��

	public SaveAction() {

		super();
		ActionInitializer.initializeAction(this, IActionCode.SAVE);
		putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("6009tran", "06009tran0042")/*
																							 * @
																							 * res
																							 * "�Խ������ݽ��б���(Ctrl+S)"
																							 */);
	}

	/**
	 * У�鿪ʼ���ںϷ���
	 * 
	 * @param saveData
	 * @param curRow
	 * @return boolean
	 */
	public boolean checkBegindate(int saveType, SuperVO saveData, int currow) throws BusinessException {

		// ��ǰ�еĿ�ʼ���ں���ֹ����
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		UFLiteralDate endDate = (UFLiteralDate) saveData.getAttributeValue("enddate");
		// ǰһ����¼�Ŀ�ʼ����
		UFLiteralDate preRowBegindate = null;
		// ǰһ����¼�Ľ�������
		UFLiteralDate preRowEnddate = null;
		// ��һ����¼�Ŀ�ʼ����
		UFLiteralDate nextRowBegindate = null;
		// ������
		int iRowCount = getCurBillModel().getRowCount() - 1;
		if (currow != iRowCount) {
			nextRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow + 1, "begindate");
		}
		if (currow != 0) {
			if (getCurBillModel().getValueAt(currow - 1, "enddate") != null) {
				preRowEnddate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "enddate");
			} else {
				preRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "begindate");
				preRowEnddate = beginDate.getDateBefore(1);
				if (preRowBegindate != null && preRowBegindate.afterDate(preRowEnddate)) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0043")/*
																								 * @
																								 * res
																								 * "��ʼ���ڲ������ڵ�����һ��¼�Ŀ�ʼ���ڣ�"
																								 */);
				} else if (preRowBegindate == null) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0044")/*
																								 * @
																								 * res
																								 * "ǰһ����¼�Ŀ�ʼ���ڲ���Ϊ�գ�"
																								 */);
				}
			}
		}
		if (preRowEnddate != null && (preRowEnddate.compareTo(beginDate) == 0 || preRowEnddate.afterDate(beginDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0045")/*
																						 * @
																						 * res
																						 * "��ʼ���ڲ������ڵ�����һ��¼�Ľ������ڣ�"
																						 */);
		}
		if (endDate != null && nextRowBegindate != null
				&& (nextRowBegindate.compareTo(endDate) == 0 || nextRowBegindate.beforeDate(endDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0046")/*
																						 * @
																						 * res
																						 * "�������ڲ������ڻ������һ��¼�Ŀ�ʼ���ڣ�"
																						 */);
		}
		// �ù�˾��һ����ְ��¼ ��ְ���ںʹ�Ա����������˾����ְ�������Ƚϣ�У���Ƿ��н���
		if (currow == 0 && TRNConst.Table_NAME_DEPTCHG.equals(curTabCode)) {
			NCLocator
					.getInstance()
					.lookup(IPersonRecordService.class)
					.checkBeginDate(selData.getParentVO().getPsnJobVO(),
							(UFLiteralDate) saveData.getAttributeValue(PsnJobVO.BEGINDATE), null);
		}
		return true;
	}

	/**
	 * �����¼��Ŀ�ʼ��������
	 * 
	 * @param saveType
	 * @param strTabCode
	 * @throws BusinessException
	 */
	public void checkDataForTableType(int saveType, String strTabCode, SuperVO saveData) throws BusinessException {

		UFLiteralDate begindate = null;
		UFLiteralDate enddate = null;
		String tabCode = getListView().getCurrentTabCode();
		begindate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		enddate = (UFLiteralDate) saveData.getAttributeValue("enddate");
		if (begindate == null) {
			if (TRNConst.Table_NAME_PSNCHG.equals(strTabCode)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0047")/*
																							 * @
																							 * res
																							 * "�������ڲ���Ϊ�գ�"
																							 */);
			}
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0048")/*
																						 * @
																						 * res
																						 * "��ʼ���ڲ���Ϊ�գ�"
																						 */);
		}
		if (saveData.getAttributeValue("recordnum") != null
				&& ((Integer) saveData.getAttributeValue("recordnum")).intValue() != 0 && enddate == null) {
			if (TRNConst.Table_NAME_PSNCHG.equals(strTabCode)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0049")/*
																							 * @
																							 * res
																							 * "�뿪���ڲ���Ϊ�գ�"
																							 */);
			}
			if (!TRNConst.Table_NAME_TRIAL.equals(strTabCode)) {
				// ���ò��жϽ�������
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0050")/*
																							 * @
																							 * res
																							 * "�������ڲ���Ϊ�գ�"
																							 */);
			}
		}
		if (saveData.getAttributeValue("endflag") != null) {
			UFBoolean endflag = (UFBoolean) saveData.getAttributeValue("endflag");
			if (endflag.booleanValue() && enddate == null) {
				if (!TRNConst.Table_NAME_DIMISSION.equals(tabCode)) {
					if (TRNConst.Table_NAME_PSNCHG.equals(strTabCode)) {
						throw new BusinessException(ResHelper.getString("6009tran", "06009tran0049")/*
																									 * @
																									 * res
																									 * "�뿪���ڲ���Ϊ�գ�"
																									 */);
					}
					if (!TRNConst.Table_NAME_TRIAL.equals(strTabCode)) {
						// ���ò��жϽ�������
						throw new BusinessException(ResHelper.getString("6009tran", "06009tran0050")/*
																									 * @
																									 * res
																									 * "�������ڲ���Ϊ�գ�"
																									 */);
					}
				}
			}
		}
		if (enddate != null && begindate.afterDate(enddate)) {
			if (TRNConst.Table_NAME_PSNCHG.equals(strTabCode)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0051")/*
																							 * @
																							 * res
																							 * "�������ڲ��������뿪���ڣ�"
																							 */);
			}
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0052")/*
																						 * @
																						 * res
																						 * "��ʼ���ڲ������ڽ������ڣ�"
																						 */);
		}
		if (TRNConst.Table_NAME_TRIAL.equals(tabCode)) {
			// ����
			UFLiteralDate regulardate = (UFLiteralDate) saveData.getAttributeValue("regulardate");
			if (regulardate != null && begindate.afterDate(regulardate)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0053")/*
																							 * @
																							 * res
																							 * "��ʼ���ڲ�������ת�����ڣ�"
																							 */);
			}
		}
		BillModel curBillModel = getListView().getBillListPanel().getBodyBillModel(getListView().getCurrentTabCode());
		int editRow = curBillModel.getEditRow();
		if (TRNConst.Table_NAME_PART.equals(strTabCode)) {
			// ��ְ��У��Ǽ�¼���ʱ���ϵ,ֻ�ڼ�ְ���ʱУ��������¼���¼���ϵ
			if (getModel().getEditType() == RdsPsninfoModel.PARTCHG) {
				checkPartchg(saveData, editRow);
			} else if (getModel().getEditType() == RdsPsninfoModel.UPDATE) {
				// �޸ļ�ְ��¼,���⴦��
				checkPartEdit(saveData, editRow);
			}
			return;
		}
		if (TRNConst.Table_NAME_TRIAL.equals(strTabCode)) {
			// ���õ��ֶ�����һ������У��
			checkTrial(saveData, editRow);
			return;
		}
		if (TRNConst.Table_NAME_PSNCHG.equals(strTabCode)) {
			// ����������ֶ�����һ������У��
			checkPsnchg(saveData, editRow);
			return;
		}
		// WNC�Ϳ�,��������¼�����
		if (TRNConst.Table_NAME_DEPTCHG.equals(strTabCode) && saveType == RdsPsninfoModel.INSERT) {
			checkBegindateForInsertChg(saveType, saveData, editRow);
		} else {
			checkBegindate(saveType, saveData, editRow);
		}

	}

	private boolean checkBegindateForInsertChg(int saveType, SuperVO saveData, int currow) throws BusinessException {
		// ��ǰ�еĿ�ʼ���ں���ֹ����
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		UFLiteralDate endDate = (UFLiteralDate) saveData.getAttributeValue("enddate");
		// ǰһ����¼�Ŀ�ʼ����
		UFLiteralDate preRowBegindate = null;
		// ǰһ����¼�Ľ�������
		UFLiteralDate preRowEnddate = null;
		// ��һ����¼�Ŀ�ʼ����
		UFLiteralDate nextRowBegindate = null;
		// ������
		int iRowCount = getCurBillModel().getRowCount() - 1;
		if (currow != iRowCount) {
			nextRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow + 1, "begindate");
		}
		if (currow != 0) {
			preRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "begindate");
			preRowEnddate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "enddate");
			if (preRowBegindate == null) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0044")/*
																							 * @
																							 * res
																							 * "ǰһ����¼�Ŀ�ʼ���ڲ���Ϊ�գ�"
																							 */);
			}
			if (preRowEnddate == null) {
				// ��Մt�f�������������ǲ���
				throw new BusinessException("ǰһ�lӛ䛵ĽY�����ڲ��ܞ��!");
			}
			// ���딵����У�
			if (preRowBegindate != null && (preRowBegindate.isSameDate(beginDate)) || preRowBegindate.after(beginDate)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0043")/*
																							 * @
																							 * res
																							 * "��ʼ���ڲ������ڵ�����һ��¼�Ŀ�ʼ���ڣ�"
																							 */);
			}
			if (nextRowBegindate != null
					&& (!nextRowBegindate.after(preRowBegindate) || !nextRowBegindate.after(preRowEnddate))) {
				throw new BusinessException("�Y���e�`,��һ�l���_ʼ���ڲ���С춵��ǰһ�lӛ䛵��_ʼ��Y������!");
			}
			if (nextRowBegindate == null) {
				throw new BusinessException("��һ�lӛ䛵��_ʼ���ڲ��ܞ��!");
			}
			if (nextRowBegindate != null && (nextRowBegindate.isSameDate(beginDate))
					|| nextRowBegindate.before(beginDate)) {
				throw new BusinessException("�_ʼ���ڲ�����춵����һӛ䛵��_ʼ���ڣ�");
			}
			if (nextRowBegindate != null && (!nextRowBegindate.getDateBefore(1).isSameDate(endDate))) {
				throw new BusinessException("�Y������횞���һӛ��_ʼ���ڵ�ǰһ�죡");
			}
			return true;
		} else {
			throw new BusinessException("��������ǰ�����Y��!");
		}

	}

	private void checkPartchg(SuperVO saveData, int currow) throws BusinessException {

		// ��ǰ�еĿ�ʼ���ں���ֹ����
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		// ǰһ����¼�Ŀ�ʼ����
		UFLiteralDate preRowBegindate = null;
		// ǰһ����¼�Ľ�������
		UFLiteralDate preRowEnddate = null;
		// ������
		if (currow != 0) {
			if (getCurBillModel().getValueAt(currow - 1, "enddate") != null) {
				preRowEnddate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "enddate");
			} else {
				preRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "begindate");
				preRowEnddate = beginDate.getDateBefore(1);
				if (preRowBegindate != null && preRowBegindate.afterDate(preRowEnddate)) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0054")/*
																								 * @
																								 * res
																								 * "��ʼ���ڲ������ڻ������һ��¼�Ŀ�ʼ���ڣ�"
																								 */);
				} else if (preRowBegindate == null) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0044")/*
																								 * @
																								 * res
																								 * "ǰһ����¼�Ŀ�ʼ���ڲ���Ϊ�գ�"
																								 */);
				}
			}
		}
		if (preRowEnddate != null && (preRowEnddate.compareTo(beginDate) == 0 || preRowEnddate.afterDate(beginDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0055")/*
																						 * @
																						 * res
																						 * "��ʼ���ڲ������ڻ������һ��¼�Ľ������ڣ�"
																						 */);
		}
	}

	private void checkPartEdit(SuperVO saveData, int currow) throws BusinessException {

		// ��ǰ�еĿ�ʼ���ں���ֹ����
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		UFLiteralDate endDate = (UFLiteralDate) saveData.getAttributeValue("enddate");
		// ǰһ����¼�Ŀ�ʼ����
		UFLiteralDate preRowBegindate = null;
		// ǰһ����¼�Ľ�������
		UFLiteralDate preRowEnddate = null;
		// ��һ����¼�Ŀ�ʼ����
		UFLiteralDate nextRowBegindate = null;
		// ������
		int iRowCount = getCurBillModel().getRowCount() - 1;
		if (currow != iRowCount && saveData.getAttributeValue("lastflag") != null
				&& !((UFBoolean) saveData.getAttributeValue("lastflag")).booleanValue()) {
			// �޸ļ�ְ�������ʷ��¼ʱ�޸�У����һ���Ŀ�ʼʱ��
			nextRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow + 1, "begindate");
			if (endDate != null && nextRowBegindate != null
					&& (nextRowBegindate.compareTo(endDate) == 0 || nextRowBegindate.beforeDate(endDate))) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0046")/*
																							 * @
																							 * res
																							 * "�������ڲ������ڻ������һ��¼�Ŀ�ʼ���ڣ�"
																							 */);
			}
		}
		if (currow != 0) {
			// �õ���һ������,�����һ����lastflag='Y'������һ����ʱ��Ƚ�
			CircularlyAccessibleValueObject preVO = getCurBillModel().getBodyValueRowVO(currow - 1,
					getListView().getCurClassName());
			if (preVO != null && preVO.getAttributeValue("lastflag") != null
					&& !((UFBoolean) saveData.getAttributeValue("lastflag")).booleanValue()) {
				if (preVO.getAttributeValue("enddate") != null) {
					preRowEnddate = (UFLiteralDate) preVO.getAttributeValue("enddate");
				} else {
					preRowBegindate = (UFLiteralDate) preVO.getAttributeValue("begindate");
					preRowEnddate = beginDate.getDateBefore(1);
					if (preRowBegindate != null && preRowBegindate.afterDate(preRowEnddate)) {
						throw new BusinessException(ResHelper.getString("6009tran", "06009tran0043")/*
																									 * @
																									 * res
																									 * "��ʼ���ڲ������ڵ�����һ��¼�Ŀ�ʼ���ڣ�"
																									 */);
					} else if (preRowBegindate == null) {
						throw new BusinessException(ResHelper.getString("6009tran", "06009tran0044")/*
																									 * @
																									 * res
																									 * "ǰһ����¼�Ŀ�ʼ���ڲ���Ϊ�գ�"
																									 */);
					}
				}
				if (preRowEnddate != null
						&& (preRowEnddate.compareTo(beginDate) == 0 || preRowEnddate.afterDate(beginDate))) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0045")/*
																								 * @
																								 * res
																								 * "��ʼ���ڲ������ڵ�����һ��¼�Ľ������ڣ�"
																								 */);
				}
			}
		}
	}

	private void checkPsnchg(SuperVO saveData, int currow) throws BusinessException {

		// ��ǰ�еĿ�ʼ���ں���ֹ����
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		UFLiteralDate endDate = (UFLiteralDate) saveData.getAttributeValue("enddate");
		;
		// ǰһ����¼�Ŀ�ʼ����
		UFLiteralDate preRowBegindate = null;
		// ǰһ����¼�Ľ�������
		UFLiteralDate preRowEnddate = null;
		// ��һ����¼�Ŀ�ʼ����
		UFLiteralDate nextRowBegindate = null;
		// ������
		int iRowCount = getCurBillModel().getRowCount() - 1;
		if (currow != iRowCount) {
			nextRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow + 1, "begindate");
		}
		if (currow != 0) {
			if (getCurBillModel().getValueAt(currow - 1, "enddate") != null) {
				preRowEnddate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "enddate");
			} else {
				preRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "begindate");
				preRowEnddate = beginDate.getDateBefore(1);
				if (preRowBegindate != null && preRowBegindate.afterDate(preRowEnddate)) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0056")/*
																								 * @
																								 * res
																								 * "�������ڲ������ڵ�����һ��¼�Ľ������ڣ�"
																								 */);
				} else if (preRowBegindate == null) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0057")/*
																								 * @
																								 * res
																								 * "ǰһ����¼�Ľ������ڲ���Ϊ�գ�"
																								 */);
				}
			}
		}
		if (preRowEnddate != null && (preRowEnddate.compareTo(beginDate) == 0 || preRowEnddate.afterDate(beginDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0058")/*
																						 * @
																						 * res
																						 * "�������ڲ������ڵ�����һ��¼���뿪���ڣ�"
																						 */);
		}
		if (endDate != null && nextRowBegindate != null
				&& (nextRowBegindate.compareTo(endDate) == 0 || nextRowBegindate.beforeDate(endDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0059")/*
																						 * @
																						 * res
																						 * "�뿪���ڲ������ڻ������һ��¼�Ľ������ڣ�"
																						 */);
		}
	}

	private void checkTrial(SuperVO saveData, int currow) throws BusinessException {

		// ��ǰ�еĿ�ʼ���ں���ֹ����
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue(TrialVO.BEGINDATE);
		UFLiteralDate regulardate = (UFLiteralDate) saveData.getAttributeValue(TrialVO.REGULARDATE);
		TrialVO trial = (TrialVO) saveData;
		if (trial.getEndflag() != null && trial.getEndflag().booleanValue() && regulardate == null) {
			if (trial.getTrialresult() != null && trial.getTrialresult() == TRNConst.TRIALRESULT_PASS) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0060")/*
																							 * @
																							 * res
																							 * "ת�����ڲ���Ϊ�գ�"
																							 */);
			}
		}
		if (regulardate != null && beginDate.afterDate(regulardate)) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0053")/*
																						 * @
																						 * res
																						 * "��ʼ���ڲ�������ת�����ڣ�"
																						 */);
		}
		if (getModel().getEditType() == RdsPsninfoModel.INSERT && trial.getTrialresult() == null) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0061")/*
																						 * @
																						 * res
																						 * "���ý������Ϊ�գ�"
																						 */);
		}
		// ���¹�����¼�Ŀ�ʼ����
		UFLiteralDate jobBeginDate = selData.getParentVO().getPsnJobVO().getBegindate();
		if (trial.getEndflag() != null && trial.getEndflag().booleanValue() && jobBeginDate != null
				&& jobBeginDate.afterDate(beginDate)) {
			if (jobBeginDate != null && jobBeginDate.beforeDate(regulardate)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0208")/*
																							 * @
																							 * res
																							 * "��ʷת�����ڲ����������¹�����¼�Ŀ�ʼ����"
																							 */);
			}
		} else if (jobBeginDate != null && jobBeginDate.afterDate(beginDate)) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0062")/*
																						 * @
																						 * res
																						 * "��ʼ���ڲ����������¹�����¼�Ŀ�ʼ����"
																						 */);
		}
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {

		super.doAction(e);
		getListView().tableStopCellEditing(curTabCode);
		// @jiazhtb ִ����֤��ʽ����ʽ����
		boolean validateResult = getListView().getBillListPanel().getBodyBillModel(curTabCode)
				.execValidateForumlas(null, null, null);
		if (!validateResult) {
			return;
		}

		int editRow = getCurBillModel().getEditRow();

		// ssx modified on 2020-06-24
		// ǰ�_��݋���T�汾�ᣬͬ�����TPK
		PsnJobVO selectedVO = (PsnJobVO) getListView().getBodySelectVO();
		if (!StringUtils.isEmpty(selectedVO.getPk_dept_v())) {
			String pk_dept = (String) NCLocator
					.getInstance()
					.lookup(IUAPQueryBS.class)
					.executeQuery("select pk_dept from org_dept_v where pk_vid='" + selectedVO.getPk_dept_v() + "'",
							new ColumnProcessor());
			if (!StringUtils.isEmpty(pk_dept) && !pk_dept.equals(selectedVO.getPk_dept())) {
				selectedVO.setPk_dept(pk_dept);
				getListView().getBillListPanel().getBodyBillModel(curTabCode).setBodyRowVO(selectedVO, editRow);
				getListView().getBillListPanel().getBodyBillModel().loadLoadRelationItemValue();
			}
		}
		// end

		// �ǿ�У��
		getListView().dataNotNullValidate();
		// ��ʼ����ʱ���У��
		SuperVO vo = (SuperVO) getListView().getBillListPanel().getBodyBillModel(curTabCode)
				.getBodyValueRowVO(editRow, getListView().getCurClassName());
		checkDataForTableType(getModel().getEditType(), curTabCode, vo);

		// end
		// ��ͬ�Ӽ�������У��
		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) && getModel().getEditType() != RdsPsninfoModel.INSERT) {
			// ������������б���У��,����������ݵ�lastflag����YҲ�����б���У��
			// ����У��
			if (((PsnJobVO) vo).getLastflag().booleanValue()) {
				if (!validateBudget(getContext(), new PsnJobVO[] { selData.getParentVO().getPsnJobVO() },
						new PsnJobVO[] { (PsnJobVO) vo })) {
					return;
				}
			}
		}
		// end
		selData.setTableVO(curTabCode, new SuperVO[] { vo });

		if (getModel().getEditType() == RdsPsninfoModel.ADD) {
			// fengwei 2012-12-24 �������ְ��¼�ڵ㣬
			// ���ǡ���Ա��ְ��¼��ҳǩ������ʾ���Ƿ�ͬʱͣ�õ�ǰ��Ա����ֻ����������ʾ
			if (TRNConst.Table_NAME_DIMISSION.equals(curTabCode)) {

				int result = MessageDialog.showYesNoDlg(getEntranceUI(),
						ResHelper.getString("6009tran", "06009tran0206")/*
																		 * @ res
																		 * "ȷ��ͣ��"
																		 */,
						ResHelper.getString("6009tran", "06009tran0185")/*
																		 * @res
																		 * "�Ƿ�ͬʱͣ�õ�ǰ��Ա��"
																		 */);
				if (result == MessageDialog.ID_YES) {
					isdisablepsn = Boolean.TRUE;
				} else {
					isdisablepsn = Boolean.FALSE;
				}

			}

			// �ӱ����Ӻ󱣴�
			if (!saveAddData(selData, e)) {
				return;
			}
		} else if (getModel().getEditType() == RdsPsninfoModel.UPDATE) {
			// �ӱ��޸ĺ󱣴�
			if (!saveUpdateData(selData, e)) {
				return;
			}
		} else if (getModel().getEditType() == RdsPsninfoModel.INSERT) {
			// �ӱ����󱣴�
			if (!saveInsertData(selData)) {
				return;
			}
		} else if (getModel().getEditType() == RdsPsninfoModel.PARTCHG) {
			// ��ְ�������
			if (!savePartchg(selData)) {
				return;
			}
		}
		getListView().setMainPanelEnabled(false);
		getListView().getBillListPanel().setEnabled(false);
		getModel().setEditType(RdsPsninfoModel.UNCHANGE);
		// ���ܵ�repaint()
		getListView().repaint();
		getModel().setUiState(UIState.NOT_EDIT);
	}

	private BillModel getCurBillModel() {
		return getListView().getBillListPanel().getBodyBillModel(curTabCode);
	}

	private IRdsManageService getIRdsService() {
		return NCLocator.getInstance().lookup(IRdsManageService.class);
	}

	@Override
	protected boolean isActionEnable() {

		if (getContext().getPk_org() == null) {
			return false;
		}
		return getModel().getUiState() == UIState.ADD || getModel().getUiState() == UIState.EDIT;
	}

	private boolean saveAddData(PsndocAggVO aggVO, ActionEvent event) throws Exception {
		// ��ѯ���º���ǰ�Ĺ�����¼�ķ�����֯
		boolean isNewLegalOrg = false;
		// �¼�¼pk
		String oldPkOrg = selData.getParentVO().getPk_org();
		String newPkOrg = null;
		// ԭ��¼:
		SuperVO[] saveData = aggVO.getTableVO(curTabCode);

		for (SuperVO pjv : saveData) {
			if (pjv instanceof PsnJobVO) {
				PsnJobVO temp = (PsnJobVO) pjv;
				if (temp.getLastflag().booleanValue()) {
					newPkOrg = temp.getPk_org();
				}
			} else if (pjv instanceof TrialVO) {
				TrialVO temp = (TrialVO) pjv;
				if (temp.getLastflag().booleanValue()) {
					newPkOrg = temp.getPk_org();
				}
			}
		}
		if (null == newPkOrg) {
			isNewLegalOrg = false;
		} else {
			// ��ѯ��������֯�Ƿ�Ϊͬһ������֯

			String[] orgs = { oldPkOrg, newPkOrg };
			Map<String, String> resultMap = LegalOrgUtilsEX.getLegalOrgByOrgs(orgs);
			if (null != resultMap && null != resultMap.get(oldPkOrg) && null != resultMap.get(newPkOrg)
					&& !resultMap.get(newPkOrg).equals(resultMap.get(oldPkOrg))) {
				// ��ͬ�ķ�����֯
				isNewLegalOrg = true;
			}

		}

		// ssx modified on 2020-08-11
		// ���ӕr����Ҫ����WNC���Ěv��Ĭ�J�O��N��������ʾ
		boolean isSynWork = false;
		// boolean isSynWork = (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode)
		// || TRNConst.Table_NAME_PART
		// .equals(curTabCode))
		// && UIDialog.ID_YES == MessageDialog.showYesNoDlg(getEntranceUI(),
		// ResHelper.getString("6009tran", "06009tran0207")/*
		// * @ res
		// * "ȷ��ͬ��"
		// */,
		// ResHelper.getString("6009tran", "06009tran0063")/*
		// * @res
		// * "�Ƿ�ͬ������?"
		// */);

		if (TRNConst.Table_NAME_TRIAL.equals(curTabCode)) {
			TrialVO trail = (TrialVO) aggVO.getTableVO(curTabCode)[0];
			if (trail.getEndflag() != null && trail.getEndflag().booleanValue() && trail.getTrialresult() != null
					&& trail.getTrialresult() == TRNConst.TRIALRESULT_PASS) {
				isSynWork = false;
				// isSynWork = UIDialog.ID_YES ==
				// MessageDialog.showYesNoDlg(getEntranceUI(),
				// ResHelper.getString("6009tran", "06009tran0207")/*
				// * @ res
				// * "ȷ��ͬ��"
				// */,
				// ResHelper.getString("6009tran", "06009tran0063")/*
				// * @res
				// * "�Ƿ�ͬ������?"
				// */);
			}
		}
		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) || TRNConst.Table_NAME_DIMISSION.equals(curTabCode)) {
			// ��ְ��ְ����ʱҪ����ί�й�ϵ
			validateManageScope((PsnJobVO) aggVO.getTableVO(curTabCode)[0]);
			aggVO.setTableVO(PsnJobVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		} else if (TRNConst.Table_NAME_PART.equals(curTabCode)) {
			aggVO.setTableVO(PartTimeVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		}
		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode)) {

			// ��ͣ�������
			refTransType = SysInitQuery.getParaString(selData.getParentVO().getPk_org(), "TWHR11").toString();
			// �}�������
			refReturnType = SysInitQuery.getParaString(selData.getParentVO().getPk_org(), "TWHR12").toString();

			if (refTransType == null || refTransType.equals("~")) {
				throw new BusinessException("ϵ�y���� [TWHR11] δָ�������ͣ�Į�����͡�");
			}

			if (refReturnType == null || refReturnType.equals("~")) {
				throw new BusinessException("ϵ�y���� [TWHR12] δָ�������ͣ�}�Į�����͡�");
			}
			// ����������¼
			// �Ƿ�ͬ������
			PsndocAggVO retVO = getIRdsService().addPsnjob_RequiresNew(aggVO, curTabCode, isSynWork,
					getContext().getPk_org());

			// WNC ���_ ���� �{��ӛ� tank 2020��3��26��11:08:27
			if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) && getModel().getEditType() == RdsPsninfoModel.ADD) {
				if (retVO != null && retVO.getTableVO(curTabCode) != null && retVO.getTableVO(curTabCode).length > 0) {
					// ���딵������ֹ����
					UFLiteralDate begindate = ((PsnJobVO) aggVO.getTableVO(curTabCode)[0]).getBegindate();
					UFLiteralDate enddate = ((PsnJobVO) aggVO.getTableVO(curTabCode)[0]).getEnddate();
					// ����ֵ
					SuperVO[] rtnVOs = retVO.getTableVO(curTabCode);
					// �ҵ����딵��
					PsnJobVO psnjob = findInsertRecord(rtnVOs, begindate, enddate, 0);
					// �ҵ������ǰһ�l����
					PsnJobVO prejob = findInsertRecord(rtnVOs, begindate, enddate, -1);

					getIRdsService().doAfterInsertPsnjob(prejob, psnjob);
				}

			}
			// end WNC ���_ ���� �{��ӛ� tank

			// �ͽ�������--������֯��ͬ��ʱ�����
			//
			// isNewLegalOrg = true;

			if (retVO.getParentVO().getPsnJobVO().getTrnstype() != null
					&& retVO.getParentVO().getPsnJobVO().getTrnstype().equals(refTransType)) {
				// ��ͣн�r
				dealTransPNI();
			} else if (retVO.getParentVO().getPsnJobVO().getTrnstype() != null
					&& retVO.getParentVO().getPsnJobVO().getTrnstype().equals(refReturnType)) {
				// ��ͣ�}
				dealReturnPNI();
			} else if (retVO.getParentVO().getPsnJobVO().getTrnstype() != null
					&& retVO.getParentVO().getPsnJobVO().getTrnstype().equals(refChangeType)) {
				// ת��
				if (isNewLegalOrg) {
					dealChangePNI();
				}
			}

			Object obj = getModel().getTreeObject();
			String pkTreeObj = "";
			String pkPsn = "";
			if (obj != null && obj instanceof OrgVO) {
				pkTreeObj = ((OrgVO) obj).getPk_org();
				pkPsn = retVO.getParentVO().getPsnJobVO().getPk_org();
			} else if (obj != null && obj instanceof DeptVO) {
				pkTreeObj = ((DeptVO) obj).getPk_dept();
				pkPsn = retVO.getParentVO().getPsnJobVO().getPk_dept();
			}
			if (obj == null || pkPsn.equals(pkTreeObj)) {
				// ���ڵ������ͬ�����ڵ�
				setRetData(retVO, curTabCode);
			} else {
				// ���ӹ�����¼����֯���Ż���
				getModel().directlyDelete(aggVO);
			}
			// ���ӹ�����¼��Ҫˢ�½���
		} else if (TRNConst.Table_NAME_DIMISSION.equals(curTabCode)) {
			// �˱�����ȷ��
			// DateLarborDlg dlg = new DateLarborDlg(getEntranceUI(), "�˱����ڴ_�J",
			// "�˱�����");

			// dlg.initUI();
			// dlg.showModal();
			// UFDate delLarbolDate = dlg.getdEffectiveDate();
			UFLiteralDate delLarbolDate = null;
			for (SuperVO pjv : saveData) {
				if (pjv instanceof PsnJobVO) {
					PsnJobVO temp = (PsnJobVO) pjv;
					if (temp.getLastflag().booleanValue()) {
						newPkOrg = temp.getPk_org();
						delLarbolDate = temp.getBegindate().getDateBefore(1);
					}
				} else if (pjv instanceof TrialVO) {
					TrialVO temp = (TrialVO) pjv;
					if (temp.getLastflag().booleanValue()) {
						newPkOrg = temp.getPk_org();
					}
				}
			}

			UFLiteralDate endDate;
			if (null == delLarbolDate) {
				endDate = null;
			} else {
				endDate = new UFLiteralDate(delLarbolDate.toDate());
			}

			getIRdsService().addPsnjobDimissionWithDate(aggVO, curTabCode, getContext().getPk_org(), isdisablepsn,
					endDate);
			// TODO ��ԃԓ�ˆT�Ƿ�����Ч��Ͷ��ӛ�,����]�Єt��ʾ�e�`
			String sqlStr = "select pk_psndoc from " + PsndocDefTableUtil.getPsnLaborTablename()
					+ " WHERE ISNULL(glbdef15, '9999-12-31') > '" + endDate.toString() + "' AND dr=0 AND pk_psndoc = '"
					+ aggVO.getParentVO().getPk_psndoc() + "'" + " and lastflag = 'Y'";
			IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
			Object obj = iUAPQueryBS.executeQuery(sqlStr, new ColumnProcessor());

			sqlStr = "select pk_psndoc from " + PsndocDefTableUtil.getPsnHealthTablename()
					+ " WHERE ISNULL(glbdef15, '9999-12-31') > '" + endDate.toString() + "' AND dr=0 AND pk_psndoc = '"
					+ aggVO.getParentVO().getPk_psndoc() + "'" + " and lastflag = 'Y'";

			Object obj2 = iUAPQueryBS.executeQuery(sqlStr, new ColumnProcessor());

			if (null == obj && null == obj2) {
				MessageDialog.showHintDlg(getModel().getContext().getEntranceUI(), "��ʾ", "�ˆT���]��Ͷ���o�");
			}

			// ������ְ��¼��Ҫˢ�½���
			getModel().directlyDelete(aggVO);
		} else {
			PsndocAggVO retVO = getIRdsService().addSubRecord(aggVO, curTabCode, isSynWork, getContext().getPk_org());
			setRetData(retVO, curTabCode);
		}
		return true;
	}

	/**
	 * ��ͣ�}
	 * 
	 * @throws BusinessException
	 */
	private void dealReturnPNI() throws BusinessException {

		/*
		 * boolean is2AddPNI = UIDialog.ID_YES ==
		 * MessageDialog.showYesNoDlg(getEntranceUI(),
		 * ResHelper.getString("6009tran", "06009tran0207")
		 * 
		 * @ res "ȷ��ͬ��" , "��ͣ���Ƿ�ӱ�"); if (is2AddPNI) { // �ӱ�����ȷ�� DateLarborDlg
		 * dlg = new DateLarborDlg(getEntranceUI(), "�ӱ����ڴ_�J", "�ӱ�����");
		 * 
		 * dlg.initUI();
		 * 
		 * dlg.showModal(); UFDate delLarbolDate = dlg.getdEffectiveDate(); if
		 * (null == delLarbolDate) { return; } //
		 * selData.getParentVO().getPsnJobVO().getTrnstype(); try {
		 * IPsndocSubInfoService4JFS nhiService =
		 * NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class);
		 * nhiService.returnPsnNHI(new UFLiteralDate(delLarbolDate.toDate()),
		 * selData.getParentVO().getPsnJobVO());
		 * 
		 * } catch (BusinessException e) {
		 * MessageDialog.showHintDlg(getModel().getContext().getEntranceUI(),
		 * "��ʾ", "�ˆT���]��Ͷ���o�"); } }
		 */
		try {
			PsndocAggVO aggvo = selData;
			String oldPkOrg = selData.getParentVO().getPk_org();
			// ԭ��¼:
			SuperVO[] saveData = aggvo.getTableVO(curTabCode);
			PsnJobVO newtemp = null;
			for (SuperVO pjv : saveData) {
				PsnJobVO temp = (PsnJobVO) pjv;
				if (temp.getLastflag().booleanValue()) {
					newtemp = temp;
				}
			}
			UFLiteralDate beginDate = (UFLiteralDate) newtemp.getAttributeValue("begindate");
			// �����Ϳ���Ĭ�ϼӱ����ӱ�����Ϊ����
			IPsndocSubInfoService4JFS nhiService = NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class);
			nhiService.returnPsnNHI(newtemp.getBegindate(), selData.getParentVO().getPsnJobVO());
			// �����ű�
			String[] psnArray = { selData.getParentVO().getPk_psndoc() };
			try {
				nhiService.generateGroupIns4Return(selData.getParentVO().getPk_org(), psnArray, newtemp.getBegindate());
			} catch (BusinessException e) {
				MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(), "�F���ӱ�ʧ��!", e.getMessage()
						+ " Ո�ֹ��ӈF��!");
			}

		} catch (BusinessException e) {
			MessageDialog.showHintDlg(getModel().getContext().getEntranceUI(), "��ʾ", "�ˆT���]��Ͷ���o�");
		}
	}

	/**
	 * ��ְͣн
	 * 
	 * @throws BusinessException
	 */
	private void dealTransPNI() throws BusinessException {
		/*
		 * boolean is2DelPNI = UIDialog.ID_YES ==
		 * MessageDialog.showYesNoDlg(getEntranceUI(),
		 * ResHelper.getString("6009tran", "06009tran0207")
		 * 
		 * @ res "ȷ��ͬ��" , "��ͣн�Ƿ��˱�"); if (is2DelPNI) { // �˱�����ȷ�� DateLarborDlg
		 * dlg = new DateLarborDlg(getEntranceUI(), "�˱����ڴ_�J", "�˱�����");
		 * 
		 * dlg.initUI();
		 * 
		 * dlg.showModal(); UFDate delLarbolDate = dlg.getdEffectiveDate(); if
		 * (null == delLarbolDate) { return; } try { //
		 * selData.getParentVO().getPsnJobVO().getTrnstype();
		 * IPsndocSubInfoService4JFS nhiService =
		 * NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class);
		 * nhiService.transPsnNHI(new UFLiteralDate(delLarbolDate.toDate()),
		 * selData.getParentVO().getPsnJobVO());
		 * 
		 * } catch (BusinessException e) {
		 * MessageDialog.showHintDlg(getModel().getContext().getEntranceUI(),
		 * "��ʾ", e.getMessage()); }
		 * 
		 * }
		 */
		try {
			// �����Ϳ�,Ĭ���˱����˱�����Ϊ�����ǰһ��
			// selData.getParentVO().getPsnJobVO().getTrnstype();
			PsndocAggVO aggvo = selData;
			String oldPkOrg = selData.getParentVO().getPk_org();
			// ԭ��¼:
			SuperVO[] saveData = aggvo.getTableVO(curTabCode);
			PsnJobVO newtemp = null;
			for (SuperVO pjv : saveData) {
				PsnJobVO temp = (PsnJobVO) pjv;
				if (temp.getLastflag().booleanValue()) {
					newtemp = temp;
				}

			}
			UFLiteralDate beginDate = (UFLiteralDate) newtemp.getAttributeValue("begindate");
			IPsndocSubInfoService4JFS nhiService = NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class);
			// �����ͽ���
			nhiService.transPsnNHI(beginDate.getDateBefore(1), selData.getParentVO().getPsnJobVO());
			// �����ű�
			nhiService.dismissPsnGroupIns(selData.getParentVO().getPk_org(), selData.getParentVO().getPk_psndoc(),
					beginDate.getDateBefore(1));
			nhiService.deletePNI(selData.getParentVO().getPk_org(), beginDate);
		} catch (BusinessException e) {
			MessageDialog.showHintDlg(getModel().getContext().getEntranceUI(), "��ʾ", e.getMessage());
		}

	}

	private void dealChangePNI() throws BusinessException {
		boolean is2LegalOrg = UIDialog.ID_YES == MessageDialog.showYesNoDlg(getEntranceUI(),
				ResHelper.getString("6009tran", "06009tran0207")/*
																 * @ res "ȷ��ͬ��"
																 */, "�Ƿ�ͬ��Ͷ��ӛ����·��˽M��");
		// �˱�����ȷ��
		DateLarborDlg dlg = new DateLarborDlg(getEntranceUI(), "�˱����ڴ_�J", "�˱�����");

		dlg.initUI();

		dlg.showModal();
		UFDate delLarbolDate = dlg.getdEffectiveDate();
		if (null == delLarbolDate) {
			return;
		}
		// selData.getParentVO().getPsnJobVO().getTrnstype();
		try {

			IPsndocSubInfoService4JFS nhiService = NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class);
			nhiService.redeployPsnNHI(new UFLiteralDate(delLarbolDate.toDate()), selData.getParentVO().getPsnJobVO(),
					is2LegalOrg);
		} catch (BusinessException e) {
			MessageDialog.showHintDlg(getModel().getContext().getEntranceUI(), "��ʾ", e.getMessage());
		}

		/*
		 * // ssx added for Taiwan NHI on 2015-10-15 // add NHI Info //
		 * 2017-05-16 upgrade to V65, from JD code IPsndocSubInfoService4JFS
		 * nhiService =
		 * NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class);
		 * nhiService.dismissPsnNHI(dimission.getPk_org(),
		 * dimission.getPk_psndoc(), dimission.getBegindate().getDateBefore(1));
		 * // ssx added on 2017-12-19 // �،��F���Y������
		 * nhiService.dismissPsnGroupIns(dimission.getPk_org(),
		 * dimission.getPk_psndoc(), dimission.getBegindate().getDateBefore(1));
		 */

	}

	private boolean saveInsertData(PsndocAggVO aggVO) throws Exception {

		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) || TRNConst.Table_NAME_DIMISSION.equals(curTabCode)) {
			aggVO.setTableVO(PsnJobVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		} else if (TRNConst.Table_NAME_PART.equals(curTabCode)) {
			aggVO.setTableVO(PartTimeVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		}
		PsndocAggVO retVO = getIRdsService().insertSubRecord(aggVO, curTabCode);
		// WNC ���_ ���� �{��ӛ� tank 2020��3��26��11:08:27
		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) && getModel().getEditType() == RdsPsninfoModel.INSERT) {
			if (retVO != null && retVO.getTableVO(curTabCode) != null && retVO.getTableVO(curTabCode).length > 0) {
				// ���딵������ֹ����
				UFLiteralDate begindate = ((PsnJobVO) aggVO.getTableVO(curTabCode)[0]).getBegindate();
				UFLiteralDate enddate = ((PsnJobVO) aggVO.getTableVO(curTabCode)[0]).getEnddate();
				// ����ֵ
				SuperVO[] rtnVOs = retVO.getTableVO(curTabCode);
				// �ҵ����딵��
				PsnJobVO psnjob = findInsertRecord(rtnVOs, begindate, enddate, 0);
				// �ҵ������ǰһ�l����
				PsnJobVO prejob = findInsertRecord(rtnVOs, begindate, enddate, -1);

				getIRdsService().doAfterInsertPsnjob(prejob, psnjob);
			}

		}
		// end WNC ���_ ���� �{��ӛ� tank
		setRetData(retVO, curTabCode);
		return true;
	}

	/**
	 * �ҵ���Ҫ�Ĺ���ӛ�
	 * 
	 * @param rtnVOs
	 * @param begindate
	 * @param enddate
	 * @param flag
	 * @return
	 */
	private PsnJobVO findInsertRecord(SuperVO[] rtnVOs, UFLiteralDate begindate, UFLiteralDate enddate, int flag) {
		PsnJobVO rtn = null;
		int rtIndex = -1;
		if (rtnVOs != null) {
			for (int i = 0; i < rtnVOs.length; i++) {
				if (rtnVOs[i] instanceof PsnJobVO) {
					PsnJobVO jobvo = (PsnJobVO) rtnVOs[i];
					UFLiteralDate tpBeginDate = jobvo.getBegindate();
					UFLiteralDate tpEndDate = jobvo.getEnddate();
					if (tpBeginDate != null
							&& tpBeginDate.isSameDate(begindate)
							&& (tpEndDate != null && tpEndDate.isSameDate(enddate) || (tpEndDate == null && enddate == null))) {
						rtIndex = i;
						break;
					}
				}
			}
			if (rtIndex >= 0 && (rtIndex + flag) >= 0 && (rtIndex + flag) < rtnVOs.length) {
				rtn = (PsnJobVO) rtnVOs[(rtIndex + flag)];
			}
		}
		return rtn;
	}

	private boolean savePartchg(PsndocAggVO aggVO) throws BusinessException {
		// ssx modified on 2020-08-11
		// ���ӕr����Ҫ����WNC���Ěv��Ĭ�J�O��N��������ʾ
		boolean isSynWork = false;
		// boolean isSynWork = UIDialog.ID_YES == MessageDialog
		// .showYesNoDlg(getEntranceUI(), ResHelper.getString("6009tran",
		// "06009tran0207")/*
		// * @
		// * res
		// * "ȷ��ͬ��"
		// */,
		// ResHelper.getString("6009tran", "06009tran0063")/*
		// * @res
		// * "�Ƿ�ͬ������?"
		// */);
		// end
		aggVO.setTableVO(PartTimeVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		PsndocAggVO retVO = getIRdsService().savePartchg(aggVO, curTabCode, isSynWork, getContext().getPk_org());
		setRetData(retVO, curTabCode);
		return true;
	}

	private boolean saveUpdateData(PsndocAggVO aggVO, ActionEvent event) throws Exception {
		// ssx modified on 2020-08-11
		// ���ӕr����Ҫ����WNC���Ěv��Ĭ�J�O��N��������ʾ
		boolean isSynWork = false;
		// boolean isSynWork = (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode)
		// || TRNConst.Table_NAME_PART
		// .equals(curTabCode))
		// && UIDialog.ID_YES == MessageDialog.showYesNoDlg(getEntranceUI(),
		// ResHelper.getString("6009tran", "06009tran0207")/*
		// * @ res
		// * "ȷ��ͬ��"
		// */,
		// ResHelper.getString("6009tran", "06009tran0063")/*
		// * @res
		// * "�Ƿ�ͬ������?"
		// */);
		if (TRNConst.Table_NAME_TRIAL.equals(curTabCode)) {
			TrialVO trail = (TrialVO) aggVO.getTableVO(curTabCode)[0];
			if (trail.getEndflag() != null && trail.getEndflag().booleanValue() && trail.getTrialresult() != null
					&& trail.getTrialresult() == TRNConst.TRIALRESULT_PASS) {
				isSynWork = false;
				// isSynWork = UIDialog.ID_YES ==
				// MessageDialog.showYesNoDlg(getEntranceUI(),
				// ResHelper.getString("6009tran", "06009tran0207")/*
				// * @ res
				// * "ȷ��ͬ��"
				// */,
				// ResHelper.getString("6009tran", "06009tran0063")/*
				// * @res
				// * "�Ƿ�ͬ������?"
				// */);
				// end
			}
		}
		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) || TRNConst.Table_NAME_DIMISSION.equals(curTabCode)) {
			// ��ְ��ְ�޸�ʱҪ����ί�й�ϵ,ֻ���Ǳ�������һ����ʱ��
			PsnJobVO vo = (PsnJobVO) aggVO.getTableVO(curTabCode)[0];
			if (vo.getLastflag().booleanValue()) {
				validateManageScope(vo);
			}
			aggVO.setTableVO(PsnJobVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		} else if (TRNConst.Table_NAME_PART.equals(curTabCode)) {
			aggVO.setTableVO(PartTimeVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		}
		PsndocAggVO retVO = getIRdsService().updateSubRecord(aggVO, curTabCode, isSynWork, getContext().getPk_org());
		if (TRNConst.Table_NAME_DIMISSION.equals(curTabCode) || TRNConst.Table_NAME_DEPTCHG.equals(curTabCode)) {
			Object obj = getModel().getTreeObject();
			String pkTreeObj = "";
			String pkPsn = "";
			if (obj != null && obj instanceof OrgVO) {
				pkTreeObj = ((OrgVO) obj).getPk_org();
				pkPsn = retVO.getParentVO().getPsnJobVO().getPk_org();
			} else if (obj != null && obj instanceof DeptVO) {
				pkTreeObj = ((DeptVO) obj).getPk_dept();
				pkPsn = retVO.getParentVO().getPsnJobVO().getPk_dept();
			}
			if (obj == null || pkPsn.equals(pkTreeObj)) {
				// ���ڵ������ͬ�����ڵ�
				setRetData(retVO, curTabCode);
			} else {
				// ���ӹ�����¼����֯���Ż���
				getModel().directlyDelete(aggVO);
			}
		} else {
			setRetData(retVO, curTabCode);
		}
		return true;
	}

}
