package nc.ui.hi.psndoc.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.hi.IPsndocSubInfoService4JFS;
import nc.pub.tools.HiCacheUtils;
import nc.ui.hi.psndoc.model.PsndocModel;
import nc.ui.hr.uif2.action.ActionConst;
import nc.ui.hr.uif2.action.DelLineAction;
import nc.ui.pub.bill.BillModel;
import nc.ui.uif2.UIState;
import nc.vo.hi.psndoc.AssVO;
import nc.vo.hi.psndoc.BminfoVO;
import nc.vo.hi.psndoc.CapaVO;
import nc.vo.hi.psndoc.CtrtVO;
import nc.vo.hi.psndoc.KeyPsnVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.TrainVO;
import nc.vo.hi.psndoc.WainfoVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;

/***************************************************************************
 * <br>
 * Created on 2010-2-23 9:14:09<br>
 * 
 * @author Rocex Wang
 ***************************************************************************/
public class DelSubSetAction extends DelLineAction {
    @Override
    public void doAction(ActionEvent e) throws Exception {

	int[] rows = getCardPanel().getBillCardPanel().getBodyPanel().getTable().getSelectedRows();

	if (rows == null || rows.length == 0) {
	    putValue(MESSAGE_AFTER_ACTION, ResHelper.getString("6007psn", "06007psn0107")/*
											  * @
											  * res
											  * "请选择要删除的行"
											  */);
	    return;
	}
	putValue(MESSAGE_AFTER_ACTION, null);
	if (HICommonValue.FUNC_CODE_REGISTER.equals(getContext().getNodeCode())
		&& PsnJobVO.getDefaultTableName().equals(getValue(ActionConst.HR_ACTION_TABKEY))
		&& getModel().getUiState() == UIState.EDIT) {

	    // 最新的任职记录不能在子表中删除
	    PsndocAggVO psndocAggVO = (PsndocAggVO) getModel().getSelectedData();
	    BillModel billModel = getCardPanel().getBillCardPanel().getBillModel(PsnJobVO.getDefaultTableName());
	    if (rows.length == 1
		    && ObjectUtils.equals(psndocAggVO.getParentVO().getPsnJobVO().getPrimaryKey(),
			    billModel.getValueAt(rows[0], PsnJobVO.PK_PSNJOB))) {
		throw new ValidationException(ResHelper.getString("6007psn", "06007psn0108")/*
											     * @
											     * res
											     * "最新任职记录不能删除"
											     */);
	    }

	    ArrayList<Integer> al = new ArrayList<Integer>();
	    for (int row : rows) {
		if (ObjectUtils.equals(psndocAggVO.getParentVO().getPsnJobVO().getPrimaryKey(),
			billModel.getValueAt(row, PsnJobVO.PK_PSNJOB))) {
		    continue;
		}
		al.add(row);
	    }
	    int[] delRows = new int[al.size()];
	    for (int i = 0; i < delRows.length; i++) {
		delRows[i] = al.get(i);
	    }
	    getCardPanel().getBillCardPanel().getBodyPanel().delLine(delRows);
	    return;
	} else
	// ssx added for Group Insurance on 2017-09-08
	if (HICommonValue.FUNC_CODE_REGISTER.equals(getContext().getNodeCode())
		&& getCardPanel().getBillCardPanel().getBodyPanel().getTable().getName()
			.equals(PsndocDefTableUtil.getGroupInsuranceTablename())
		&& getModel().getUiState() == UIState.EDIT) {
	    PsndocAggVO psndocAggVO = (PsndocAggVO) getModel().getSelectedData();
	    IPsndocSubInfoService4JFS srv = NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class);
	    if (srv.isExistsGroupInsCalculateResults(psndocAggVO.getParentVO().getPk_psndoc(), (String) psndocAggVO
		    .getTableVO(PsndocDefTableUtil.getGroupInsuranceTablename())[0].getAttributeValue("pk_psndoc_sub"))) {
		throw new BusinessException("已存在团保计算结果，不能删除。");
	    }

	}

	super.doAction(e);

	if (CtrtVO.getDefaultTableName().equals(getValue(ActionConst.HR_ACTION_TABKEY))) {
	    // 处理合同子集增加的第一条记录必须是签订
	    int rowCount = getCardPanel().getBillCardPanel().getBillTable(CtrtVO.getDefaultTableName()).getRowCount();
	    if (rowCount > 0) {
		getCardPanel().getBillCardPanel().getBillModel(CtrtVO.getDefaultTableName())
			.setValueAt(1, 0, CtrtVO.CONTTYPE);
	    }
	}
    }

    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-6-24 18:50:13<br>
     * 
     * @see nc.ui.hr.uif2.action.HrAction#getModel()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public PsndocModel getModel() {
	return (PsndocModel) super.getModel();
    }

    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-19 9:40:59<br>
     * 
     * @see nc.ui.hr.uif2.action.BodyTabAction#isActionEnable()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    protected boolean isActionEnable() {
	// 员工信息维护、查询节点不能对业务子集进行维护
	if (ArrayUtils.contains(new String[] { HICommonValue.FUNC_CODE_EMPLOYEE, HICommonValue.FUNC_CODE_PSN_INFO,
		HICommonValue.FUNC_CODE_KEYPSN }, getModel().getNodeCode())
		&& getModel().getBusinessInfoSet().contains(getValue(ActionConst.HR_ACTION_TABKEY))) {
	    if ((CtrtVO.getDefaultTableName().equals(getValue(ActionConst.HR_ACTION_TABKEY)) && !HiCacheUtils
		    .isModuleStarted("6011"))
		    || (CapaVO.getDefaultTableName().equals(getValue(ActionConst.HR_ACTION_TABKEY)) && !HiCacheUtils
			    .isModuleStarted("6004"))
		    || (TrainVO.getDefaultTableName().equals(getValue(ActionConst.HR_ACTION_TABKEY)) && !HiCacheUtils
			    .isModuleStarted("6025"))
		    || (AssVO.getDefaultTableName().equals(getValue(ActionConst.HR_ACTION_TABKEY)) && !HiCacheUtils
			    .isModuleStarted("6029"))) {
		return getModel().getUiState() == UIState.EDIT || getModel().getUiState() == UIState.ADD;
	    }

	    if (HICommonValue.FUNC_CODE_KEYPSN.equals(getModel().getNodeCode())
		    && KeyPsnVO.getDefaultTableName().equals(getValue(ActionConst.HR_ACTION_TABKEY))) {
		return getModel().getUiState() == UIState.EDIT || getModel().getUiState() == UIState.ADD;
	    }

	    return false;
	}

	if (!HICommonValue.FUNC_CODE_KEYPSN.equals(getModel().getNodeCode())
		&& KeyPsnVO.getDefaultTableName().equals(getValue(ActionConst.HR_ACTION_TABKEY))) {
	    return false;
	}

	return super.isActionEnable() && ActionHelper.isBusiSetStarted(this)
		&& !WainfoVO.getDefaultTableName().equals(getValue(ActionConst.HR_ACTION_TABKEY))
		&& !BminfoVO.getDefaultTableName().equals(getValue(ActionConst.HR_ACTION_TABKEY));
    }

    /***************************************************************************
     * <br>
     * Created on 2010-6-24 18:50:27<br>
     * 
     * @param model
     * @author Rocex Wang
     ***************************************************************************/
    public void setModel(PsndocModel model) {
	super.setModel(model);
    }
}
