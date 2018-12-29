package nc.impl.hr.dataio;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.validation.ValidationFailure;
import nc.hr.multithread.MultiThreadProcessor;
import nc.hr.utils.ResHelper;
import nc.hr.utils.TimerLogger;
import nc.itf.hr.dataio.IDataIO;
import nc.itf.hr.dataio.IDataIOHookPrivate;
import nc.vo.hr.dataio.DataIOConfigVO;
import nc.vo.hr.dataio.DataIOConst;
import nc.vo.hr.dataio.DataIOResult;
import nc.vo.hr.dataio.DefaultHookPrivate;
import nc.vo.pub.BusinessException;
import nc.vo.trade.pub.IExAggVO;

/**************************************************************
 * <br>
 * Created on 2013-1-4 14:45:51<br>
 * @author Rocex Wang
 **************************************************************/
public class DataIOImpl implements IDataIO
{
    private final TimerLogger timerLogger = new TimerLogger("nc.impl.hr.dataio.DataIOImpl", null);
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2013-6-25 19:41:12<br>
     * @see nc.itf.hr.dataio.IDataIO#importData(nc.vo.hr.dataio.DataIOConfigVO, nc.vo.trade.pub.IExAggVO[])
     ****************************************************************************/
    @Override
    public DataIOResult[] importData(DataIOConfigVO configVO, IExAggVO[] importAggVOs) throws BusinessException
    {
        if (importAggVOs == null)
        {
            return null;
        }
        
        timerLogger.clear();
        timerLogger.addLog(ResHelper.getString("6001dataimport", "06001dataimport0145")
        /* @res "开始批量导入数据..." */);
        
        DataIOResult[] resultVOs = new DataIOResult[importAggVOs.length];
        
        IDataIO dataIOService = NCLocator.getInstance().lookup(IDataIO.class);
        
        String strMainSheetName = configVO.getExcelVO().getSheetVO(configVO.getExcelVO().getMainSheetCode()).getSheetName();
        
        try
        {
            for (int i = 0; i < importAggVOs.length; i++)
            {
                timerLogger.addLog(ResHelper.getString("6001dataimport", "06001dataimport0148", i + 1)
                /* @res "开始导入第[{0}]条数据..." */);
                
                try
                {
                    resultVOs[i] = dataIOService.importData_RequiresNew(configVO, importAggVOs[i]);
                    
                    if (!resultVOs[i].getValidationFailure().isEmpty() && !configVO.getContinueOnError())
                    {
                        break;
                    }
                }
                catch (BusinessException ex)
                {
                    resultVOs[i] = new DataIOResult();
                    
                    resultVOs[i].addValidationFailure(strMainSheetName, configVO.getVOIndex().get(importAggVOs[i].getParentVO()), DataIOConst.CHECK_INFO,
                        new ValidationFailure(ex.getMessage()));
                    
                    Logger.error(ex);
                    
                    if (!configVO.getContinueOnError())
                    {
                        break;
                    }
                }
                finally
                {
                    timerLogger.addLog(ResHelper.getString("6001dataimport", "06001dataimport0149", i + 1)
                    /* @res "导入第[{0}]条数据结束！" */);
                }
            }
        }
        finally
        {
            IDataIOHookPrivate dataIOHook = DefaultHookPrivate.getInstance(configVO);
            
            timerLogger.addLog("Begin dataIOHook.afterSaveAllAggVO...");
            
            dataIOHook.afterSaveAllAggVO(configVO, importAggVOs);
            
            timerLogger.addLog("End dataIOHook.afterSaveAllAggVO!");
            
            if (configVO.getBatchIndex() == configVO.getBatchCount())
            {
                timerLogger.addLog("Begin dataIOHook.afterSaveAllBatchAggVO...");
                
                dataIOHook.afterSaveAllBatchAggVO(configVO);
                
                timerLogger.addLog("End dataIOHook.afterSaveAllBatchAggVO!");
            }
            
            timerLogger.addLog(ResHelper.getString("6001dataimport", "06001dataimport0146")
            /* @res "批量导入数据完毕！" */);
            
            timerLogger.log();
        }
        
        return resultVOs;
    }
    
    /**************************************************************
     * {@inheritDoc}<br>
     * Created on 2013-7-31 10:13:00<br>
     * @see nc.itf.hr.dataio.IDataIO#importData_RequiresNew(nc.vo.hr.dataio.DataIOConfigVO,
     *      nc.vo.trade.pub.IExAggVO)
     * @author Rocex Wang
     **************************************************************/
    @Override
    public DataIOResult importData_RequiresNew(DataIOConfigVO configVO, IExAggVO importAggVO) throws BusinessException
    {
        IDataIOHookPrivate dataIOHook = DefaultHookPrivate.getInstance(configVO);
        
        if (dataIOHook == null)
        {
            return null;
        }
        
        DataIOResult resultVO = new DataIOResult();
        
        dataIOHook.beforeSaveImportAggVO(configVO, resultVO, importAggVO);
        
        dataIOHook.handleVOByImportType(configVO, importAggVO);
        
        dataIOHook.saveImportAggVO(configVO, resultVO, importAggVO);
        
        dataIOHook.afterSaveImportAggVO(configVO, importAggVO);
        
        return resultVO;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2015-3-12 14:24:38<br>
     * @see nc.itf.hr.dataio.IDataIO#importDataMultiThread(nc.vo.hr.dataio.DataIOConfigVO,
     *      nc.vo.trade.pub.IExAggVO[])
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public DataIOResult[] importDataMultiThread(DataIOConfigVO configVO, IExAggVO[] importAggVOs) throws BusinessException
    {
        if (importAggVOs == null)
        {
            return null;
        }
        
        timerLogger.clear();
        timerLogger.addLog(ResHelper.getString("6001dataimport", "06001dataimport0145")
        /* @res "开始批量导入数据..." */);
        
        MultiThreadProcessor<IExAggVO, DataIOResult> multiThreadProcessor =
            new MultiThreadProcessor<IExAggVO, DataIOResult>(DataIOTask.class, configVO, importAggVOs);
        
        try
        {
            multiThreadProcessor.process();
        }
        finally
        {
            IDataIOHookPrivate dataIOHook = DefaultHookPrivate.getInstance(configVO);
            
            timerLogger.addLog("Begin dataIOHook.afterSaveAllAggVO...");
            
            dataIOHook.afterSaveAllAggVO(configVO, importAggVOs);
            
            timerLogger.addLog("End dataIOHook.afterSaveAllAggVO!");
            
            if (configVO.getBatchIndex() == configVO.getBatchCount())
            {
                timerLogger.addLog("Begin dataIOHook.afterSaveAllBatchAggVO...");
                
                dataIOHook.afterSaveAllBatchAggVO(configVO);
                
                timerLogger.addLog("End dataIOHook.afterSaveAllBatchAggVO!");
            }
            
            timerLogger.addLog(ResHelper.getString("6001dataimport", "06001dataimport0146")
            /* @res "批量导入数据完毕！" */);
            
            timerLogger.log();
            
            System.gc();
        }
        
        return multiThreadProcessor.getProcessResult();
    }
}
