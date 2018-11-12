package nc.ui.wa.paydata.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IHRWAActionCode;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hrp.budgetitemcmp.view.AlarmAuditInfomationDlg;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.wa.paydata.model.WadataAppDataModel;
import nc.ui.wa.pub.WADelegator;
import nc.ui.wabm.view.dialog.CheckTypeChooseDialog;
import nc.vo.hrp.budgetmgt.BudgetWarnMessageVo;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.pub.WACLASSTYPE;
import nc.vo.wa.pub.WaLoginVOHelper;
import nc.vo.wa.pub.WaState;

import org.apache.commons.lang.StringUtils;

/**
 * 审核
 * 
 * @author zhangg
 * 
 */
public class CheckAction extends PayDataBaseAction
{
    
    private static final long serialVersionUID = 1L;
    
    public CheckAction()
    {
        super();
        putValue(INCAction.CODE, IHRWAActionCode.Approve);
        setBtnName(ResHelper.getString("common", "UC001-0000027")/* @res "审核" */);
        // setDisplayHotKey("s");
        putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("common", "UC001-0000027")/* @res "审核" */+ "(Ctrl+M)");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, Event.CTRL_MASK));
    }
    
    @Override
    public void doActionForExtend(ActionEvent e) throws Exception
    {
//    	20151103 xiejie3  NCdp205487703 校验员工是否调薪。begin
//		if (!getPaydataModel().getIsCompute()) {
//			if (showYesNoMessage(ResHelper.getString("60130paydata","060130paydata0332")/*@res "有员工调薪，应先进行计算,是否继续?"*/) != MessageDialog.ID_YES) {
//				return;
//			}
//		}
//		end
        final CheckTypeChooseDialog chooseDialog = new CheckTypeChooseDialog(getParentContainer(), true);
        chooseDialog.showModal();
        if (chooseDialog.getResult() != UIDialog.ID_OK)
        {
            putValue(this.MESSAGE_AFTER_ACTION, IShowMsgConstant.getCancelInfo());
            return;
        }
        new SwingWorker<Boolean, Void>()
        {
            BannerTimerDialog dialog = new BannerTimerDialog(getParentContainer());
            String error = null;
            
            @Override
            protected Boolean doInBackground() throws Exception
            {
                try
                {
                	// 2015-11-21 zhousze 处理这里的多语 begin
                    dialog.setStartText(ResHelper.getString("60130paydata", "060130paydata0556")/*
                                                                                              * @res
                                                                                              * "薪资计算过程中，请稍等..."
                                                                                              */);
                    // end
                    dialog.start();
                    boolean rangeAll = chooseDialog.getValue().booleanValue();
                    if (!rangeAll)
                    {
                        if (getPaydataModel().getData() == null || getPaydataModel().getData().size() < 1)
                        {
                            throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0339")/*
                                                                                                                 * @
                                                                                                                 * res
                                                                                                                 * "您没有选择人员！"
                                                                                                                 */);// "您没有选择人员！"
                        }
                    }
                    // 合并计税再次计算 (汇总方案不重新计算2012-08-07修改)
                    if (WACLASSTYPE.COLLECTCLASS != WaLoginVOHelper.getClassType(getWaLoginVO()))
                    {
                    	// guopeng 20151017 只有参与合并计税的薪资方案才需要重新计算
//                    	AggWaTaxGroupVO aggtaxgroupvo = NCLocator.getInstance().lookup(ITaxGroupQuery.class).queryAggWaTaxGroupVOByWaClassPk(getWaContext().getPk_wa_class());
//                    	if (aggtaxgroupvo != null)
                    	{
	                        // shenliangc 20140830 合并计税方案部分审核只计算界面上查询出来的人员数据，需要传入过滤条件。
	                        // 不在界面上的人员可能并不想现在审核，所以项目公式可能不合法，此时部分审核会报公式不合法错误。需要只重新计算界面上人员。
	                        if (!rangeAll)
	                        {
	                            // 部分审核，部分重新计算
	                            WADelegator.getPaydata().reCaculate(getWaContext(), ((WadataAppDataModel) getModel()).getWhereCondition());
	                        }
	                        else
	                        {
	                            // 全部审核，全部重新计算
	                            WADelegator.getPaydata().reCaculate(getWaContext(), null);
	                        }
                    	}
                    }
                    // 薪资项目预警
                    String keyName = ResHelper.getString("common", "UC001-0000027")/* @res "审核" */;// 审核
                    String[] files = getPaydataManager().getAlterFiles(keyName);
                    showAlertInfo(files);
                    
                    // 薪资总额预警
                    BudgetWarnMessageVo messagevo = getPaydataManager().getAuditCreondition(rangeAll);
                    if (messagevo != null && (messagevo.getLisCorpWarns().size() > 0 || messagevo.getLisDeptWarns().size() > 0))
                    {
                        AlarmAuditInfomationDlg aid = new AlarmAuditInfomationDlg(getParentContainer(), messagevo);
                        if (aid.showModal() != 1)
                        {
                            return false;// 取消了该操作！
                        }
                    }
                    
                    getPaydataManager().onCheck(rangeAll);
                    // 20150921 xiejie3 NCdp205495933,修改薪资方案保存后，再在薪资发放进行审核时，界面卡死
                    // e.getMessage()可能取不到值，导致异常被吃掉，先加上从e.getBusiObject().toString()取异常。
                }
                catch (Exception e)
                {
                    error = e.getMessage();
                    //20151103 shenliangc NCdp205490557 并发审核提示错误：LockFailed key:1001A810000000GVOV6V begin
                    if(e instanceof LockFailedException){
                    	error = ResHelper.getString("60130paydata","060130paydata0334")/*@res "你操作的数据正被他人修改！"*/;
                    }
                    //20151103 shenliangc NCdp205490557 并发审核提示错误：LockFailed key:1001A810000000GVOV6V end
                    if (StringUtils.isEmpty(error))
                    {
                        if(e instanceof VersionConflictException){
                        	error = ((VersionConflictException) e).getBusiObject().toString();
                        }
                    }
                }
                finally
                {
                    dialog.end();
                }
                return Boolean.TRUE;
            }
            
            /**
             * @author zhangg on 2010-7-7
             * @see javax.swing.SwingWorker#done()
             */
            @Override
            protected void done()
            {
                if (error != null)
                {
                    ShowStatusBarMsgUtil.showErrorMsg(ResHelper.getString("60130paydata", "审核过程存在错误")/*
                                                                                                      * @res
                                                                                                      * "计算过程存在错误"
                                                                                                      */, error, getContext());
                }
                else
                {
                    ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("60130paydata", "060130paydata0336")/*
                                                                                                                   * @
                                                                                                                   * res
                                                                                                                   * "在"
                                                                                                                   */+ dialog.getSecond()
                        + ResHelper.getString("60130paydata", "060130paydata0337")/* @res "秒内计算完成." */, getContext());
                }
            }
        }.execute();
        putValue(this.MESSAGE_AFTER_ACTION, ResHelper.getString("60130paydata", "060130paydata0522")/*
                                                                                                     * @res
                                                                                                     * "审核成功。"
                                                                                                     */);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected boolean isActionEnable()
    {
        if (super.isActionEnable())
        {
            // 如果界面没有数据或者没有已计算数据的话，审核不可用
            List<Object> datas = getPaydataModel().getData();
            if (datas == null || datas.size() < 1)
            {
                return false;
            }
            for (Object obj : datas)
            {
                PayfileVO vo = (PayfileVO) obj;
                if (vo.getCaculateflag().equals(UFBoolean.TRUE) && vo.getCheckflag().equals(UFBoolean.FALSE))
                {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    @Override
    public Set<WaState> getEnableStateSet()
    {
        if (waStateSet == null)
        {
            waStateSet = new HashSet<WaState>();
            waStateSet.add(WaState.CLASS_WITHOUT_RECACULATED);
            waStateSet.add(WaState.CLASS_RECACULATED_WITHOUT_CHECK);
            waStateSet.add(WaState.CLASS_PART_CHECKED);
        }
        return waStateSet;
        
    }
}
