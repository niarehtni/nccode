package nc.impl.pub;

import nc.bs.dao.BaseDAO;
import nc.bs.overtime.otleavebalance.bp.OTLeaveBalanceDeleteBP;
import nc.bs.overtime.otleavebalance.bp.OTLeaveBalanceInsertBP;
import nc.bs.overtime.otleavebalance.bp.OTLeaveBalanceUpdateBP;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.pubimpl.ta.overtime.SegDetailServiceImpl;
import nc.pubitf.para.SysInitQuery;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.overtime.AggOTLeaveBalanceVO;

public class OTLeaveBalancePubServiceImpl {
    private BaseDAO baseDAO = null;

    // 新增
    public AggOTLeaveBalanceVO[] pubinsertBills(AggOTLeaveBalanceVO[] vos) throws BusinessException {
	try {
	    // 数据库中数据和前台传递过来的差异VO合并后的结果
	    BillTransferTool<AggOTLeaveBalanceVO> transferTool = new BillTransferTool<AggOTLeaveBalanceVO>(vos);
	    AggOTLeaveBalanceVO[] mergedVO = transferTool.getClientFullInfoBill();

	    // 调用BP
	    OTLeaveBalanceInsertBP action = new OTLeaveBalanceInsertBP();
	    AggOTLeaveBalanceVO[] retvos = action.insert(mergedVO);
	    // 构造返回数据
	    return transferTool.getBillForToClient(retvos);
	} catch (Exception e) {
	    ExceptionUtils.marsh(e);
	}
	return null;
    }

    // 删除
    public void pubdeleteBills(AggOTLeaveBalanceVO[] vos) throws BusinessException {
	try {
	    // 加锁 比较ts
	    BillTransferTool<AggOTLeaveBalanceVO> transferTool = new BillTransferTool<AggOTLeaveBalanceVO>(vos);
	    AggOTLeaveBalanceVO[] fullBills = transferTool.getClientFullInfoBill();
	    OTLeaveBalanceDeleteBP deleteBP = new OTLeaveBalanceDeleteBP();
	    deleteBP.delete(fullBills);
	} catch (Exception e) {
	    ExceptionUtils.marsh(e);
	}
    }

    // 修改
    public AggOTLeaveBalanceVO[] pubupdateBills(AggOTLeaveBalanceVO[] vos) throws BusinessException {
	try {
	    // 加锁 + 检查ts
	    BillTransferTool<AggOTLeaveBalanceVO> transTool = new BillTransferTool<AggOTLeaveBalanceVO>(vos);
	    // 补全前台VO
	    AggOTLeaveBalanceVO[] fullBills = transTool.getClientFullInfoBill();
	    // 获得修改前vo
	    AggOTLeaveBalanceVO[] originBills = transTool.getOriginBills();
	    // 调用BP
	    OTLeaveBalanceUpdateBP bp = new OTLeaveBalanceUpdateBP();
	    AggOTLeaveBalanceVO[] retBills = bp.update(fullBills, originBills);
	    // 构造返回数据
	    retBills = transTool.getBillForToClient(retBills);
	    return retBills;
	} catch (Exception e) {
	    ExceptionUtils.marsh(e);
	}
	return null;
    }

    /**
     * 根据条件计算补休假数据
     * 
     * @param pk_org
     *            人力资源组织PK
     * @param pk_psndocs
     *            人员PK数组
     * @param pk_depts
     *            部门数组
     * @param pk_leavetypecopy
     *            休假类别PK
     * @param maxdate
     *            最晚可休假日期
     * @param beginDate
     *            起始日期
     * @param endDate
     *            截止日期
     * @return
     * @throws BusinessException
     */
    public AggOTLeaveBalanceVO[] queryOTLeaveAggvos(String pk_org, String[] pk_psndocs, String[] pk_depts,
	    String pk_leavetypecopy, String maxdate, String beginDate, String endDate) throws BusinessException {
	AggOTLeaveBalanceVO[] ret = null;
	String initleavetype = SysInitQuery.getParaString(pk_org, "TWHRT08");
	if (pk_leavetypecopy.equals(initleavetype)) {
	    // 加载加班單
	    // 按日期范围查找加班分段明细
	    // 日期范围限制的不是来源单据的日期，而是产生的假期最长可休日期在该区间内
	    // ISegDetailService service =
	    // NCLocator.getInstance().lookup(ISegDetailService.class);
	    ret = new SegDetailServiceImpl().getOvertimeToRestHoursByType(pk_org, pk_psndocs, new UFLiteralDate(
		    beginDate), new UFLiteralDate(endDate), null);
	}
	return ret;
    }

    public BaseDAO getBaseDAO() {
	if (baseDAO == null) {
	    baseDAO = new BaseDAO();
	}
	return baseDAO;
    }

}