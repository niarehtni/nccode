/*     */ package nc.impl.ta.leave;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import nc.bs.dao.BaseDAO;
/*     */ import nc.bs.framework.common.NCLocator;
/*     */ import nc.hr.frame.persistence.SimpleDocServiceTemplate;
/*     */ import nc.hr.utils.CommonUtils;
/*     */ import nc.hr.utils.FromWhereSQLUtils;
/*     */ import nc.hr.utils.PubEnv;
/*     */ import nc.hr.utils.ResHelper;
/*     */ import nc.hr.utils.SQLHelper;
/*     */ import nc.impl.ta.algorithm.BillValidatorAtServer;
/*     */ import nc.impl.ta.leave.validator.LeaveApplyApproveValidator;
/*     */ import nc.impl.ta.timebill.BillMethods;
/*     */ import nc.itf.hr.pf.HrPfHelper;
/*     */ import nc.itf.ta.ILeaveApplyQueryMaintain;
/*     */ import nc.itf.ta.ILeaveBalanceManageService;
/*     */ import nc.itf.ta.ITBMPsndocQueryService;
/*     */ import nc.itf.ta.ITimeItemQueryService;
/*     */ import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
/*     */ import nc.jdbc.framework.processor.ColumnListProcessor;
/*     */ import nc.md.persist.framework.IMDPersistenceQueryService;
/*     */ import nc.md.persist.framework.MDPersistenceService;
/*     */ import nc.mddb.baseutil.MDDAOUtil;
/*     */ import nc.ui.querytemplate.querytree.FromWhereSQL;
/*     */ import nc.vo.hr.pf.PFQueryParams;
/*     */ import nc.vo.pub.AggregatedValueObject;
/*     */ import nc.vo.pub.BusinessException;
/*     */ import nc.vo.pub.lang.UFBoolean;
/*     */ import nc.vo.pub.lang.UFDateTime;
/*     */ import nc.vo.pub.lang.UFDouble;
/*     */ import nc.vo.pub.lang.UFLiteralDate;
/*     */ import nc.vo.ta.bill.BillMutexException;
/*     */ import nc.vo.ta.bill.IDateScopeBillBodyVO;
/*     */ import nc.vo.ta.leave.AggLeaveVO;
/*     */ import nc.vo.ta.leave.LeaveBactchParam;
/*     */ import nc.vo.ta.leave.LeaveCheckLengthResult;
/*     */ import nc.vo.ta.leave.LeaveCheckResult;
/*     */ import nc.vo.ta.leave.LeaveCommonVO;
/*     */ import nc.vo.ta.leave.LeaveRegVO;
/*     */ import nc.vo.ta.leave.LeavebVO;
/*     */ import nc.vo.ta.leave.LeavehVO;
/*     */ import nc.vo.ta.leave.SplitBillResult;
/*     */ import nc.vo.ta.leavebalance.LeaveBalanceVO;
/*     */ import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
/*     */ import nc.vo.ta.psndoc.TBMPsndocVO;
/*     */ import nc.vo.ta.pub.TAPFBillQueryParams;
/*     */ import nc.vo.ta.pub.TaNormalQueryUtils;
/*     */ import nc.vo.ta.timeitem.LeaveTypeCopyVO;
/*     */ import nc.vo.uap.pf.PFBatchExceptionInfo;
/*     */ import nc.vo.uap.pf.PfProcessBatchRetObject;
/*     */ import nc.vo.uif2.LoginContext;
/*     */ import org.apache.commons.collections.CollectionUtils;
/*     */ import org.apache.commons.collections.MapUtils;
/*     */ import org.apache.commons.lang.ArrayUtils;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ 
/*     */ public class LeaveApplyQueryMaintainImpl implements ILeaveApplyQueryMaintain
/*     */ {
/*     */   private SimpleDocServiceTemplate serviceTemplate;
/*     */   
/*     */   public AggLeaveVO[] queryByCond(LoginContext context, FromWhereSQL fromWhereSQL, Object extraConds) throws BusinessException
/*     */   {
/*  66 */     return queryByCond(context, fromWhereSQL, extraConds, false);
/*     */   }
/*     */   
/*     */   protected AggLeaveVO[] queryByCond(LoginContext context, FromWhereSQL fromWhereSQL, Object extraConds, boolean blApproveSite)
/*     */     throws BusinessException
/*     */   {
/*  72 */     return queryByCond(context.getPk_org(), null, fromWhereSQL, extraConds, blApproveSite);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private AggLeaveVO[] queryByCond(String pk_org, String pk_psndoc, FromWhereSQL fromWhereSQL, Object extraConds, boolean blApproveSite)
/*     */     throws BusinessException
/*     */   {
/* 106 */     String strNormalSQL = getSQLCondByFromWhereSQL(pk_org, pk_psndoc, fromWhereSQL, extraConds, blApproveSite);
/* 107 */     AggLeaveVO[] aggVOs = (AggLeaveVO[])getServiceTemplate().queryByCondition(AggLeaveVO.class, strNormalSQL);
/* 108 */     syncHeadToBody(aggVOs);
/* 109 */     return aggVOs;
/*     */   }
/*     */   
/*     */   public AggLeaveVO[] queryByWhereSQL(LoginContext context, String whereSql)
/*     */     throws BusinessException
/*     */   {
/* 115 */     AggLeaveVO[] aggVOs = (AggLeaveVO[])getServiceTemplate().queryByCondition(context, AggLeaveVO.class, whereSql);
/* 116 */     syncHeadToBody(aggVOs);
/* 117 */     return aggVOs;
/*     */   }
/*     */   
/*     */   public AggLeaveVO queryByPk(String pk) throws BusinessException
/*     */   {
/* 122 */     AggLeaveVO aggVO = (AggLeaveVO)MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPK(AggLeaveVO.class, pk, false);
/* 123 */     syncHeadToBody(aggVO);
/* 124 */     return aggVO;
/*     */   }
/*     */   
/*     */   private void syncHeadToBody(AggLeaveVO aggVO) {
/* 128 */     if (aggVO == null)
/* 129 */       return;
/* 130 */     LeavebVO[] bvos = aggVO.getBodyVOs();
/* 131 */     if (ArrayUtils.isEmpty(bvos))
/* 132 */       return;
/* 133 */     LeavehVO hvo = aggVO.getHeadVO();
/* 134 */     for (LeavebVO bvo : bvos) {
/* 135 */       bvo.setPk_psndoc(hvo.getPk_psndoc());
/* 136 */       bvo.setPk_psnorg(hvo.getPk_psnorg());
/* 137 */       bvo.setPk_psnjob(hvo.getPk_psnjob());
/* 138 */       bvo.setLeaveyear(hvo.getLeaveyear());
/* 139 */       bvo.setLeavemonth(hvo.getLeavemonth());
/* 140 */       bvo.setLeaveindex(hvo.getLeaveindex());
/* 141 */       bvo.setPk_leavetype(hvo.getPk_leavetype());
/* 142 */       bvo.setPk_leavetypecopy(hvo.getPk_leavetypecopy());
/*     */     }
/*     */   }
/*     */   
/*     */   private void syncHeadToBody(AggLeaveVO[] aggVOs) {
/* 147 */     if (ArrayUtils.isEmpty(aggVOs))
/* 148 */       return;
/* 149 */     for (AggLeaveVO aggVO : aggVOs) {
/* 150 */       syncHeadToBody(aggVO);
/*     */     }
/*     */   }
/*     */   
/*     */   public AggLeaveVO[] defaultQuery(LoginContext context) throws BusinessException
/*     */   {
/* 156 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private SimpleDocServiceTemplate getServiceTemplate()
/*     */   {
/* 169 */     if (serviceTemplate == null) {
/* 170 */       serviceTemplate = new SimpleDocServiceTemplate("tbm_leaveh");
/*     */     }
/* 172 */     return serviceTemplate;
/*     */   }
/*     */   
/*     */ 
/*     */   @Deprecated
/*     */   public Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> check(AggLeaveVO aggVO)
/*     */     throws BusinessException, BillMutexException
/*     */   {
/* 180 */     BillValidatorAtServer.checkLeavePara(aggVO);
/* 181 */     return BillValidatorAtServer.checkLeave(aggVO);
/*     */   }
/*     */   
/*     */   public Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> check(String pk_org, LeavebVO[] vos)
/*     */     throws BusinessException, BillMutexException
/*     */   {
/* 187 */     return BillValidatorAtServer.checkLeave(pk_org, vos);
/*     */   }
/*     */   
/*     */   public LeaveCommonVO[] queryTBMPsndocs(LeaveBactchParam param) throws BusinessException
/*     */   {
/* 192 */     String pk_org = param.getPk_org();
/*     */     
/* 194 */     UFLiteralDate maxBeginDate = null;
/* 195 */     UFLiteralDate maxEndDate = null;
/* 196 */     for (int i = 0; i < param.getLength(); i++) {
/* 197 */       UFLiteralDate beginDate = new UFLiteralDate(param.getBeginTimes()[i].toString().substring(0, 10));
/* 198 */       UFLiteralDate endDate = new UFLiteralDate(param.getEndTimes()[i].toString().substring(0, 10));
/* 199 */       if ((maxBeginDate == null) || (beginDate.before(maxBeginDate)))
/* 200 */         maxBeginDate = beginDate;
/* 201 */       if ((maxEndDate == null) || (endDate.after(maxEndDate))) {
/* 202 */         maxEndDate = endDate;
/*     */       }
/*     */     }
/* 205 */     FromWhereSQL fromWhereSQL = param.getFromWhereSQL();
/* 206 */     if (fromWhereSQL == null) {
/* 207 */       fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(param.getPsndocRefPks());
/*     */     }
/* 209 */     fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
/*     */     
/* 211 */     ITBMPsndocQueryService psndocService = (ITBMPsndocQueryService)NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
/* 212 */     Map<String, List<TBMPsndocVO>> psndocMap = psndocService.queryTBMPsndocMapByCondition(pk_org, fromWhereSQL, maxBeginDate, maxEndDate, false);
/* 213 */     if (MapUtils.isEmpty(psndocMap))
/* 214 */       return null;
/* 215 */     List<LeaveCommonVO> vos = new ArrayList();
/* 216 */     UFLiteralDate beginDate; UFLiteralDate endDate; for (int i = 0; i < param.getLength(); i++) {
/* 217 */       beginDate = new UFLiteralDate(param.getBeginTimes()[i].toString().substring(0, 10));
/* 218 */       endDate = new UFLiteralDate(param.getEndTimes()[i].toString().substring(0, 10));
/* 219 */       for (String pk_psndoc : psndocMap.keySet())
/*     */       {
/* 221 */         TBMPsndocVO psndocVO = TBMPsndocVO.findIntersectionVO((List)psndocMap.get(pk_psndoc), endDate.toStdString());
/* 222 */         if (psndocVO == null)
/*     */         {
/* 224 */           psndocVO = TBMPsndocVO.findIntersectionVO((List)psndocMap.get(pk_psndoc), beginDate.toStdString());
/* 225 */           if (psndocVO == null) {}
/*     */         }
/*     */         else {
/* 228 */           LeaveCommonVO vo = param.isFromApp() ? new LeavebVO() : new LeaveRegVO();
/* 229 */           if ((param.getTranstypeids() != null) && (param.getTranstypeids().length > i)) vo.setAttributeValue("transtypeid", param.getTranstypeids()[i]);
/* 230 */           if ((param.getTranstypes() != null) && (param.getTranstypes().length > i)) vo.setAttributeValue("transtype", param.getTranstypes()[i]);
/* 231 */           vo.setPk_leavetype(param.getPk_leaveTypes()[i]);
/* 232 */           vo.setPk_leavetypecopy(param.getPk_leaveTypeCopys()[i]);
/* 233 */           vo.setLeaveyear(param.getYears()[i]);
/* 234 */           vo.setLeavemonth(param.getMonths()[i]);
/* 235 */           vo.setLeavebegintime(param.getBeginTimes()[i]);
/* 236 */           vo.setLeaveendtime(param.getEndTimes()[i]);
/* 237 */           vo.setLeavebegindate(beginDate);
/* 238 */           vo.setLeaveenddate(endDate);
/* 239 */           vo.setPk_psndoc(psndocVO.getPk_psndoc());
/* 240 */           vo.setPk_psnjob(psndocVO.getPk_psnjob());
/* 241 */           vo.setPk_psnorg(psndocVO.getPk_psnorg());
/* 242 */           vo.setPk_org_v(psndocVO.getPk_org_v());
/* 243 */           vo.setPk_dept_v(psndocVO.getPk_dept_v());
/* 244 */           vo.setLeaveremark(param.getRemarks()[i]);
/* 245 */           vo.setPk_org(pk_org);
/* 246 */           vo.setPk_group(PubEnv.getPk_group());
/*     */           
/* 248 */           vos.add(vo);
/*     */         }
/*     */       }
/*     */     }
/* 252 */     LeaveCommonVO[] newvos = (LeaveCommonVO[])BillMethods.compareAndCutDate(param.getPk_org(), (IDateScopeBillBodyVO[])vos.toArray(param.isFromApp() ? new LeavebVO[0] : new LeaveRegVO[0]));
/*     */     
/*     */ 
/* 255 */     LeaveCommonVO[] splitVOs = SplitLeaveBillUtils.split(param.getPk_org(), null, newvos);
/* 256 */     return splitVOs;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PFBatchExceptionInfo checkBeforeCommit(AggLeaveVO[] leaveVOs)
/*     */     throws BusinessException
/*     */   {
/* 285 */     return null;
/*     */   }
/*     */   
/*     */   public LeaveCheckResult<AggLeaveVO> checkWhenSave(AggLeaveVO aggVO)
/*     */     throws BusinessException
/*     */   {
/* 291 */     LeaveApplyApproveValidator validator = new LeaveApplyApproveValidator();
/* 292 */     validator.validateAggVOs(new AggLeaveVO[] { aggVO });
/* 293 */     LeaveCheckResult<AggLeaveVO> checkResult = new LeaveCheckResult();
/* 294 */     checkResult.setMutexCheckResult(BillValidatorAtServer.checkLeave(aggVO));
/* 295 */     SplitBillResult<AggLeaveVO> splitResult = SplitLeaveBillUtils.split(aggVO);
/* 296 */     checkResult.setSplitResult(splitResult);
/* 297 */     return checkResult;
/*     */   }
/*     */   
/*     */ 
/*     */   public AggLeaveVO[] queryByPsndoc(String pk_psndoc, FromWhereSQL fromWhereSQL, Object extraConds)
/*     */     throws BusinessException
/*     */   {
/* 304 */     return queryByCond(null, pk_psndoc, fromWhereSQL, extraConds, false);
/*     */   }
/*     */   
/*     */   public LeaveCheckLengthResult checkLength(String pk_org, AggLeaveVO[] aggvos) throws BusinessException
/*     */   {
/* 309 */     if (ArrayUtils.isEmpty(aggvos))
/* 310 */       return null;
/* 311 */     syncHeadToBody(aggvos);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 324 */     Map<String, LeaveBalanceVO> balanceMap = ((ILeaveBalanceManageService)NCLocator.getInstance().lookup(ILeaveBalanceManageService.class)).queryAndCalLeaveBalanceVO(pk_org, aggvos);
/* 325 */     Map<String, LeaveTypeCopyVO> typeMap = ((ITimeItemQueryService)NCLocator.getInstance().lookup(ITimeItemQueryService.class)).queryLeaveCopyTypeMapByOrg(pk_org);
/*     */     
/* 327 */     List<String> overPsnList = new ArrayList();
/*     */     
/* 329 */     List<String> billcodeList = new ArrayList();
/*     */     
/* 331 */     List<String> errorPsnList = new ArrayList();
/* 332 */     for (AggLeaveVO aggVO : aggvos) {
/* 333 */       LeavehVO hvo = aggVO.getLeavehVO();
/*     */       
/* 335 */       if (!hvo.getIslactation().booleanValue())
/*     */       {
/* 337 */         String pk_leavetype = hvo.getPk_leavetype();
/* 338 */         LeaveTypeCopyVO typeVO = (LeaveTypeCopyVO)typeMap.get(pk_leavetype);
/* 339 */         String scopePeriodStr = hvo.getLeaveyear() + hvo.getLeavemonth();
/* 340 */         String key = hvo.getPk_psnorg() + hvo.getPk_timeitem() + scopePeriodStr;
/* 341 */         LeaveBalanceVO balanceVO = (LeaveBalanceVO)balanceMap.get(key);
/* 342 */         UFDouble usefuldayorhour = balanceVO.getUsefulrestdayorhour();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 348 */         UFBoolean exceedLimit = UFBoolean.valueOf((typeVO.isLeaveLimit()) && (aggVO.getHeadVO().getSumhour().compareTo(usefuldayorhour) > 0));
/*     */         
/* 350 */         if (exceedLimit.booleanValue()) {
/* 351 */           if (typeVO.isRestrictLimit())
/*     */           {
/* 353 */             errorPsnList.add(hvo.getPk_psndoc());
/*     */           } else {
/* 355 */             overPsnList.add(hvo.getPk_psndoc());
/*     */             
/* 357 */             billcodeList.add(hvo.getBill_code());
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 364 */         balanceVO.setFreezedayorhour(balanceVO.getFreezedayorhour() == null ? aggVO.getHeadVO().getSumhour() : balanceVO.getFreezedayorhour().add(aggVO.getHeadVO().getSumhour()));
/*     */       } }
/* 366 */     if (CollectionUtils.isNotEmpty(errorPsnList)) {
/* 367 */       String errorPsnNames = CommonUtils.getPsnNames((String[])errorPsnList.toArray(new String[0]));
/* 368 */       String msg = ResHelper.getString("6017leave", "06017leave0259") + errorPsnNames;
/* 369 */       throw new BusinessException(msg);
/*     */     }
/* 371 */     if (CollectionUtils.isEmpty(billcodeList))
/* 372 */       return null;
/* 373 */     String psnNames = CommonUtils.getPsnNames((String[])overPsnList.toArray(new String[0]));
/* 374 */     LeaveCheckLengthResult result = new LeaveCheckLengthResult();
/* 375 */     result.setBillcodeList(billcodeList);
/* 376 */     result.setPsnNames(psnNames);
/* 377 */     return result;
/*     */   }
/*     */   
/*     */   public PfProcessBatchRetObject checkLength4web(String pk_org, AggLeaveVO[] aggvos)
/*     */     throws BusinessException
/*     */   {
/* 383 */     if (ArrayUtils.isEmpty(aggvos))
/* 384 */       return null;
/* 385 */     ArrayList<AggLeaveVO> successObj = new ArrayList();
/* 386 */     PFBatchExceptionInfo exInfo = new PFBatchExceptionInfo();
/* 387 */     for (int i = 0; i < aggvos.length; i++) {
/*     */       try {
/* 389 */         LeaveCheckLengthResult checkLength = checkLength(pk_org, new AggLeaveVO[] { aggvos[i] });
/* 390 */         if ((checkLength != null) && (CollectionUtils.isNotEmpty(checkLength.getBillcodeList()))) {
/* 391 */           String msg = ResHelper.getString("6017leave", "06017leave0259") + checkLength.getPsnNames();
/* 392 */           throw new BusinessException(msg);
/*     */         }
/* 394 */         successObj.add(aggvos[i]);
/*     */       } catch (BusinessException e) {
/* 396 */         exInfo.putErrorMessage(i, aggvos[i], e.getMessage());
/*     */       }
/*     */     }
/* 399 */     return new PfProcessBatchRetObject(successObj.toArray(new AggregatedValueObject[0]), exInfo);
/*     */   }
/*     */   
/*     */   public String[] queryPKsByFromWhereSQL(LoginContext context, FromWhereSQL fromWhereSQL, Object etraConds, boolean approveSite)
/*     */     throws BusinessException
/*     */   {
/* 405 */     String cond = getSQLCondByFromWhereSQL(context.getPk_org(), null, fromWhereSQL, etraConds, approveSite);
/* 406 */     String alias = FromWhereSQLUtils.getMainTableAlias(fromWhereSQL, LeavehVO.getDefaultTableName());
/* 407 */     List<String> result = excuteQueryPksBycond(cond, alias);
/* 408 */     return CollectionUtils.isEmpty(result) ? null : (String[])result.toArray(new String[0]);
/*     */   }
/*     */   
/*     */   private String getSQLCondByFromWhereSQL(String pk_org, String pk_psndoc, FromWhereSQL fromWhereSQL, Object extraConds, boolean blApproveSite)
/*     */     throws BusinessException
/*     */   {
/* 414 */     if (extraConds == null)
/* 415 */       extraConds = TAPFBillQueryParams.getDefaultParams(blApproveSite);
/* 416 */     if (!(extraConds instanceof PFQueryParams))
/* 417 */       return null;
/* 418 */     String alias = FromWhereSQLUtils.getMainTableAlias(fromWhereSQL, LeavehVO.getDefaultTableName());
/* 419 */     String strNormalSQL = HrPfHelper.getQueryCondition(AggLeaveVO.class, alias, blApproveSite, pk_org, blApproveSite ? ((PFQueryParams)extraConds).getBillState() : ((TAPFBillQueryParams)extraConds).getStateCode().intValue());
/*     */     
/*     */ 
/* 422 */     if (!StringUtils.isEmpty(pk_psndoc)) {
/* 423 */       strNormalSQL = SQLHelper.appendExtraCond(strNormalSQL, alias + "." + "pk_psndoc" + "='" + pk_psndoc + "'");
/*     */     }
/*     */     
/* 426 */     String dateFilter = blApproveSite ? TaNormalQueryUtils.getApproveDatePeriod(HrPfHelper.getFlowBizItf(AggLeaveVO.class), alias, ((PFQueryParams)extraConds).getApproveDateParam(), ((PFQueryParams)extraConds).getBillState()) : TaNormalQueryUtils.getDateScopeSql(alias, (TAPFBillQueryParams)extraConds);
/*     */     
/*     */ 
/* 429 */     strNormalSQL = SQLHelper.appendExtraCond(strNormalSQL, dateFilter);
/*     */     
/*     */ 
/* 432 */     String othercond = "";
/* 433 */     if (StringUtils.isNotEmpty(pk_org)) {
/* 434 */       othercond = " and " + alias + ".pk_org='" + pk_org + "' ";
/*     */     }
/* 436 */     String order = " order by apply_date desc, bill_code";
/* 437 */     if ((fromWhereSQL == null) || (fromWhereSQL.getWhere() == null)) {
/* 438 */       return strNormalSQL + othercond + order;
/*     */     }
/* 440 */     String sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, LeavehVO.getDefaultTableName(), new String[] { "pk_leaveh" }, null, null, null, null);
/*     */     
/* 442 */     strNormalSQL = strNormalSQL + " and pk_leaveh in ( " + sql + " )" + othercond + order;
/* 443 */     return strNormalSQL;
/*     */   }
/*     */   
/*     */   private List<String> excuteQueryPksBycond(String cond, String alias) throws BusinessException
/*     */   {
/* 448 */     String sql = "select " + (StringUtils.isEmpty(alias) ? "" : new StringBuilder().append(alias).append(".").toString()) + "pk_leaveh" + " from " + LeavehVO.getDefaultTableName() + " " + (StringUtils.isEmpty(alias) ? "" : alias);
/*     */     
/* 450 */     if (!StringUtils.isEmpty(cond))
/* 451 */       sql = sql + " where " + cond;
/* 452 */     List<String> result = (List)new BaseDAO().executeQuery(sql, new ColumnListProcessor());
/* 453 */     return result;
/*     */   }
/*     */   
/*     */   public AggLeaveVO[] queryByPks(String[] pks)
/*     */     throws BusinessException
/*     */   {
/* 459 */     if (ArrayUtils.isEmpty(pks))
/* 460 */       return null;
/* 461 */     String order = " order by apply_date desc, bill_code";
/* 462 */     String insql = "pk_leaveh in " + MDDAOUtil.getInSql(pks);
/* 463 */     return (AggLeaveVO[])getServiceTemplate().queryByCondition(AggLeaveVO.class, insql + order);
/*     */   }
/*     */ }

/* Location:           E:\TaiWan\home\home\modules\hrta\META-INF\lib\hrta_hrtaleave.jar
 * Qualified Name:     nc.impl.ta.leave.LeaveApplyQueryMaintainImpl
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */