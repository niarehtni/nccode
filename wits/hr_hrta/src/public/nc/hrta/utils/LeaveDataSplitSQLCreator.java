package nc.hrta.utils;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.mw.sqltrans.TempTable;
import nc.hr.utils.PubEnv;
import nc.hr.utils.SQLHelper;
import nc.impl.ta.algorithm.BillProcessHelperAtServer;
import nc.impl.ta.timebill.BillMethods;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.ConnectionFactory;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.util.DBUtil;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.vo.cache.CacheManager;
import nc.vo.cache.ICache;
import nc.vo.cache.config.CacheConfig;
import nc.vo.hr.temptable.TempTableVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BeanHelper;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeaveSplitCalVO;
import nc.vo.ta.period.PeriodVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**************************************************************
 * 该类只能在后台使用<br>
 * Created on 2012-3-23 10:14:20<br>
 * @author zengcheng
 **************************************************************/
public class LeaveDataSplitSQLCreator
{
   
    
    private static final String strTempTableColumn = "";
    
    private static final String strTempTableName = "leave_temptable_cal_";
    
   
    
    
    private final TempTable tempTable;
    

    public LeaveDataSplitSQLCreator()
    {
        super();
        
        tempTable = new TempTable();
    }
    
    /**************************************************************
     * <br>
     * Created on 2012-3-23 9:47:23<br>
     * @throws BusinessException
     **************************************************************/
    public void clear() throws BusinessException
    {
    }
    
    /**************************************************************
     * <br>
     * Created on 2012-7-31 19:53:11<br>
     * @param ds
     * @return PersistenceManager
     * @throws DbException
     * @author Rocex Wang
     **************************************************************/
    protected PersistenceManager createPersistenceManager(String ds) throws DbException
    {
        PersistenceManager manager = PersistenceManager.getInstance(ds);
        
        manager.setMaxRows(100000);
        manager.setAddTimeStamp(true);
        
        return manager;
    }
    
   
    /**************************************************************
     * <br>
     * Created on 2013-7-5 16:10:29<br>
     * @param strPks 要插入的主键值
     * @return 临时表名
     * @throws BusinessException
     * @author Rocex Wang
     **************************************************************/
    public String createTempTable(PeriodVO currentPeriod, String pkLeaveItem) throws BusinessException
    {
        String strTempTable =
            createTempTable(createTempTableName(strTempTableName), "leavebegindate nchar(10) NOT NULL,leaveenddate nchar(10) NOT NULL,leavehour decimal(16,4) NOT NULL,pk_leavetype nchar(20) NOT NULL,pk_org nchar(20) NOT NULL,pk_psndoc nchar(20) NOT NULL,approve_time nchar(19) NULL,ts nchar(19) NULL",
                "pk_psndoc");
        
        insertLeaveSplitData(strTempTable, pkLeaveItem, currentPeriod);
        
        return strTempTable;
    }
    
    public void insertLeaveSplitData(String strTempTable, String pkLeaveItem, PeriodVO currentPeriod ) throws BusinessException
	{
		double allSum=0.0;
		Collection<LeaveSplitCalVO> list = new ArrayList<LeaveSplitCalVO>();
		String sql = "pk_leavetype='"+pkLeaveItem+"' "
				+ "and approve_time>='"+currentPeriod.getBegindate().toString()+"' and approve_time<='"+currentPeriod.getEnddate().toString()
				+"'  and leaveenddate<'"+currentPeriod.getBegindate().toString()+"' and dr=0 and pk_org='"+currentPeriod.getPk_org()+"'";
		IUAPQueryBS queryBS = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		
		Collection<LeaveRegVO> vos = queryBS.retrieveByClause(LeaveRegVO.class, sql);	
		for(LeaveRegVO vo : vos){
			LeaveSplitCalVO calVO=new LeaveSplitCalVO();
			calVO.setApprove_time(vo.getApprove_time());
			calVO.setLeavebegindate(vo.getLeavebegindate());
			calVO.setLeaveenddate(vo.getLeaveenddate());
			calVO.setLeavehour(vo.getLeavehour());
			calVO.setPk_leavetype(vo.getPk_leavetype());
			calVO.setPk_org(vo.getPk_org());
			calVO.setPk_psndoc(vo.getPk_psndoc());
			list.add(calVO);
		}
			
		
		
		String whereSql = "pk_leavetype='"+pkLeaveItem+"' "
				+ "and approve_time>='"+currentPeriod.getBegindate().toString()+"' and approve_time<='"+currentPeriod.getEnddate().toString()
				+"'  and leaveenddate>='"+currentPeriod.getBegindate().toString()+"' and leavebegindate<'"
				+currentPeriod.getBegindate().toString()+"' and dr=0 and pk_org='"+currentPeriod.getPk_org()+"'";
		
		
		
			Collection<LeaveRegVO> vos1 = queryBS.retrieveByClause(LeaveRegVO.class, whereSql);
			
			String calEndDate=currentPeriod.getBegindate().getDateBefore(1).toString().replace('/', '-');
			for(LeaveRegVO vo : vos1)
			{
				String endTimeSql="select endtime from bd_shift a, tbm_psncalendar b where a.pk_shift=b.pk_shift and  pk_psndoc ='"+vo.getPk_psndoc()+"' and calendar='"+calEndDate+"'";
				
				Object endTime = new BaseDAO().executeQuery(endTimeSql, new ColumnProcessor());
				if(endTime !=null && !StringUtil.isEmpty((String)endTime)){
					UFDateTime newEndDate= new UFDateTime(calEndDate+" "+(String)endTime);
					LeaveRegVO newVo= (LeaveRegVO)vo.clone();					
					newVo.setLeaveenddate(currentPeriod.getBegindate().getDateBefore(1));
					newVo.setLeaveendtime(newEndDate);
					newVo=calculate(newVo);
					
					LeaveSplitCalVO calVO=new LeaveSplitCalVO();
					calVO.setApprove_time(newVo.getApprove_time());
					calVO.setLeavebegindate(newVo.getLeavebegindate());
					calVO.setLeaveenddate(newVo.getLeaveenddate());
					calVO.setLeavehour(newVo.getLeavehour());
					calVO.setPk_leavetype(newVo.getPk_leavetype());
					calVO.setPk_org(newVo.getPk_org());
					calVO.setPk_psndoc(newVo.getPk_psndoc());
					list.add(calVO);
				}
			}
		
		insertSplitData(strTempTable,list);
	}
	public LeaveRegVO calculate(LeaveRegVO vo)
		    throws BusinessException
		  {
			TimeZone clientTimeZone = Calendar.getInstance().getTimeZone();
			
		    if (vo != null)

		    {
		      if ((vo.getIslactation() == null) || (!(vo.getIslactation().booleanValue())))
		      {
		        BillMethods.processBeginEndDatePkJobOrgTimeZone(new LeaveRegVO[] { vo });
		        IPsnCalendarQueryService psncalendarService = (IPsnCalendarQueryService)NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
		        vo = (LeaveRegVO)psncalendarService.calculatePsnDefaultOnOffDutyTime(vo, clientTimeZone);
		        BillMethods.processBeginEndDateExistsTimeZone(new LeaveRegVO[] { vo });
		      }

		      BillProcessHelperAtServer.calLeaveLength(vo);
		    }
		    return vo;
		  }
    
    /**************************************************************
     * <br>
     * Created on 2012-10-22 15:51:36<br>
     * @return String 临时表表名
     * @author Rocex Wang
     * @param strTableName
     * @param strColumn
     * @param strIndexColumn
     * @throws BusinessException
     **************************************************************/
    public String createTempTable(String strTableName, String strColumn, String strIndexColumn) throws BusinessException
    {
        PubEnv.checkRunOnServer();
        
        Connection connection = null;
        
        try
        {
            connection = ConnectionFactory.getConnection();
            
            strTableName = tempTable.createTempTable(connection, strTableName, strColumn, strIndexColumn);
        }
        catch (SQLException ex)
        {
            // 如果创建临时表失败，就试图再创建一次
            try
            {
                if (connection == null)
                {
                    connection = ConnectionFactory.getConnection();
                }
                
                strTableName = tempTable.createTempTable(connection, strTableName, strColumn, strIndexColumn);
            }
            catch (SQLException ex1)
            {
                Logger.error(ex.getMessage(), ex);
            }
            
            Logger.error(ex.getMessage(), ex);
        }
        finally
        {
            DBUtil.closeConnection(connection);
        }
        
     
        
        return strTableName;
    }
    
    /**************************************************************
     * <br>
     * Created on 2012-11-2 9:18:09<br>
     * @param strPreTableName
     * @return 增加了序号的临时表名
     * @author Rocex Wang
     **************************************************************/
    protected String createTempTableName(String strPreTableName)
    {
        ICache cache = CacheManager.getInstance().getCache(new CacheConfig(getClass().getName(), false, -1, 10000, CacheConfig.CacheType.LRU, 0));
        
        String strCallId = InvocationInfoProxy.getInstance().getCallId();
        
        // 自助端没有维护callid，所以需要取自助的一些请求级的id
        if (strCallId == null)
        {
            strCallId = String.valueOf(LfwRuntimeEnvironment.getWebContext().getRequest().hashCode());
        }
        
        Integer iTempTableCounter = (Integer) cache.get(strCallId);
        
        if (iTempTableCounter == null)
        {
            iTempTableCounter = 0;
        }
        
        cache.put(strCallId, iTempTableCounter + 1);
        
        Logger.error(MessageFormat.format("nc.hr.utils.LeaveDataSplitSQLCreator.createTempTableName():strPreTableName->{0},strCallId->{1},iTempTableCounter->{2}",
            strPreTableName, strCallId, iTempTableCounter));
        
        return strPreTableName + iTempTableCounter;
    }
    
   
   
    
    /**************************************************************
     * <br>
     * Created on 2012-7-31 20:01:48<br>
     * @param strTableName
     * @param strPks
     * @param strPkColumn
     * @throws BusinessException
     * @author Rocex Wang
     **************************************************************/
    protected void insertSplitData(String strTableName, Collection<LeaveSplitCalVO> list) throws BusinessException
    {
        if (list ==null || list.size()<1)
        {
            return;
        }
        
        String strSQL = null;
        
       
        strSQL = "insert into {0}(leavebegindate,leaveenddate,leavehour,pk_leavetype,pk_org,pk_psndoc,approve_time) values(?,?,?,?,?,?,?)";
        
        strSQL = MessageFormat.format(strSQL, strTableName);
      
        
        List<SQLParameter> listPara = new ArrayList<SQLParameter>();
        
        for (LeaveSplitCalVO calVO : list)
        {   
            SQLParameter paras = new SQLParameter();
            
            paras.addParam(calVO.getLeavebegindate());
            paras.addParam(calVO.getLeaveenddate());
            paras.addParam(calVO.getLeavehour());
            paras.addParam(calVO.getPk_leavetype());
            paras.addParam(calVO.getPk_org());
            paras.addParam(calVO.getPk_psndoc());
            paras.addParam(calVO.getApprove_time());
            
            listPara.add(paras);
        }
        
        PersistenceManager manager = null;
        
        try
        {
            manager = createPersistenceManager(null);
            JdbcSession session = manager.getJdbcSession();
            session.addBatch(strSQL, listPara.toArray(new SQLParameter[0]));
            
            session.executeBatch();
        }
        catch (DbException ex)
        {
            throw new BusinessException(ex.getMessage(), ex);
        }
        finally
        {
            if (manager != null)
            {
                manager.release();
            }
        }
    }
    
  
}
