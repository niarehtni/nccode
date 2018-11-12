/*      */ package nc.impl.hi.psndoc;
/*      */ 
/*      */ import java.text.MessageFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import nc.bs.framework.common.NCLocator;
/*      */ import nc.bs.sec.esapi.NCESAPI;
/*      */ import nc.bs.uif2.validation.ValidationFailure;
/*      */ import nc.bs.uif2.validation.Validator;
/*      */ import nc.hr.utils.MultiLangHelper;
/*      */ import nc.hr.utils.ResHelper;
/*      */ import nc.itf.hi.IPersonRecordService;
/*      */ import nc.itf.hr.frame.IPersistenceRetrieve;
/*      */ import nc.pub.tools.VOUtils;
/*      */ import nc.vo.bd.psnid.PsnIdtypeVO;
/*      */ import nc.vo.cp.cpindi.CPIndiVO;
/*      */ import nc.vo.hi.psndoc.CapaVO;
/*      */ import nc.vo.hi.psndoc.CertVO;
/*      */ import nc.vo.hi.psndoc.CtrtVO;
/*      */ import nc.vo.hi.psndoc.EduVO;
/*      */ import nc.vo.hi.psndoc.KeyPsnGrpVO;
/*      */ import nc.vo.hi.psndoc.KeyPsnVO;
/*      */ import nc.vo.hi.psndoc.NationDutyVO;
/*      */ import nc.vo.hi.psndoc.PartTimeVO;
/*      */ import nc.vo.hi.psndoc.PsnChgVO;
/*      */ import nc.vo.hi.psndoc.PsnJobVO;
/*      */ import nc.vo.hi.psndoc.PsnOrgVO;
/*      */ import nc.vo.hi.psndoc.PsndocAggVO;
/*      */ import nc.vo.hi.psndoc.PsndocVO;
/*      */ import nc.vo.hi.psndoc.RetireVO;
/*      */ import nc.vo.hi.psndoc.SpeItemVO;
/*      */ import nc.vo.hi.psndoc.TitleVO;
/*      */ import nc.vo.hi.psndoc.TrialVO;
/*      */ import nc.vo.hi.psndoc.enumeration.PsnType;
/*      */ import nc.vo.hr.infoset.InfoItemVO;
/*      */ import nc.vo.hr.infoset.InfoSetHelper;
/*      */ import nc.vo.hr.infoset.InfoSetVO;
/*      */ import nc.vo.hr.validator.CommnonValidator;
/*      */ import nc.vo.ml.AbstractNCLangRes;
/*      */ import nc.vo.ml.NCLangRes4VoTransl;
/*      */ import nc.vo.org.DeptVO;
/*      */ import nc.vo.org.OrgVO;
/*      */ import nc.vo.pub.BusinessException;
/*      */ import nc.vo.pub.SuperVO;
/*      */ import nc.vo.pub.lang.UFBoolean;
/*      */ import nc.vo.pub.lang.UFLiteralDate;
/*      */ import org.apache.commons.lang.ArrayUtils;
/*      */ import org.apache.commons.lang.ObjectUtils;
/*      */ import org.apache.commons.lang.StringUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class PsndocValidator
/*      */   implements Validator
/*      */ {
/*      */   private IPersistenceRetrieve retrieve;
/*      */   
/*      */   protected void adjustCtrt(PsndocAggVO psndocAggVO)
/*      */     throws BusinessException
/*      */   {
/*   75 */     if (psndocAggVO == null)
/*      */     {
/*   77 */       return;
/*      */     }
/*   79 */     CtrtVO[] ctrtVOs = (CtrtVO[])psndocAggVO.getTableVO(CtrtVO.getDefaultTableName());
/*   80 */     if ((ctrtVOs == null) || (ctrtVOs.length == 0))
/*      */     {
/*   82 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  102 */     int idx = 1;
/*  103 */     String msg = "";
/*  104 */     for (int i = 0; i < ctrtVOs.length; i++)
/*      */     {
/*  106 */       if (3 != ctrtVOs[i].getStatus())
/*      */       {
/*      */ 
/*      */ 
/*  110 */         ctrtVOs[i].setIsrefer(UFBoolean.TRUE);
/*  111 */         if (0 == ctrtVOs[i].getStatus())
/*      */         {
/*  113 */           ctrtVOs[i].setStatus(1);
/*      */         }
/*      */         
/*      */ 
/*  117 */         String cond = " contractnum = '" + ctrtVOs[i].getContractnum() + "' ";
/*  118 */         if (psndocAggVO.getParentVO().getPk_psndoc() != null)
/*      */         {
/*  120 */           cond = cond + " and pk_psndoc <> '" + psndocAggVO.getParentVO().getPk_psndoc() + "' ";
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  126 */         int count = getRetrieve().getCountByCondition(CtrtVO.getDefaultTableName(), cond);
/*  127 */         if (count > 0)
/*      */         {
/*  129 */           msg = msg + ResHelper.getString("6007psn", "06007psn0237", new String[] { idx + "" }) + '\n';
/*  130 */           idx++;
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*  136 */           String termtype = ctrtVOs[i].getTermtype();
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  143 */           if (!StringUtils.isBlank(termtype))
/*      */           {
/*      */ 
/*      */ 
/*  147 */             if ("CM01".equals(termtype))
/*      */             {
/*      */ 
/*  150 */               if (ctrtVOs[i].getEnddate() == null)
/*      */               {
/*  152 */                 msg = msg + ResHelper.getString("6007psn", "06007psn0238", new String[] { idx + "" }) + '\n';
/*  153 */                 idx++;
/*  154 */                 continue;
/*      */               }
/*      */             }
/*  157 */             if ((ctrtVOs[i].getEnddate() != null) && (ctrtVOs[i].getEnddate().compareTo(ctrtVOs[i].getBegindate()) <= 0))
/*      */             {
/*  159 */               msg = msg + ResHelper.getString("6007psn", "06007psn0239", new String[] { idx + "" }) + '\n';
/*  160 */               idx++;
/*      */ 
/*      */             }
/*  163 */             else if ((ctrtVOs[i].getProbenddate() != null) && (ctrtVOs[i].getProbegindate() != null) && (ctrtVOs[i].getProbenddate().compareTo(ctrtVOs[i].getProbegindate()) <= 0))
/*      */             {
/*      */ 
/*  166 */               msg = msg + ResHelper.getString("6007psn", "06007psn0240", new String[] { idx + "" }) + '\n';
/*  167 */               idx++;
/*      */             }
/*      */             else {
/*  170 */               idx++;
/*      */             } } } } }
/*  172 */     if (!StringUtils.isBlank(msg))
/*      */     {
/*  174 */       throw new BusinessException(msg);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void adjustPartTime(PsndocAggVO psndocAggVO)
/*      */     throws BusinessException
/*      */   {
/*  188 */     if (psndocAggVO == null)
/*      */     {
/*  190 */       return;
/*      */     }
/*  192 */     PartTimeVO[] partTimeVOs = (PartTimeVO[])psndocAggVO.getTableVO(PartTimeVO.getDefaultTableName());
/*  193 */     if ((partTimeVOs == null) || (partTimeVOs.length == 0))
/*      */     {
/*  195 */       return;
/*      */     }
/*      */     
/*  198 */     int idx = 1;
/*  199 */     String msg = "";
/*  200 */     for (int i = 0; i < partTimeVOs.length; i++)
/*      */     {
/*  202 */       if (3 == partTimeVOs[i].getStatus())
/*      */       {
/*  204 */         partTimeVOs[i].setLastflag(UFBoolean.FALSE);
/*      */       }
/*      */       else {
/*  207 */         if ((partTimeVOs[i].getEndflag() != null) && (partTimeVOs[i].getEndflag().booleanValue()) && (partTimeVOs[i].getEnddate() == null))
/*      */         {
/*      */ 
/*  210 */           msg = msg + '\n' + ResHelper.getString("6007psn", "06007psn0241", new String[] { idx + "" });
/*      */         }
/*  212 */         if ((partTimeVOs[i].getEndflag() == null) && (!partTimeVOs[i].getEndflag().booleanValue()))
/*      */         {
/*      */ 
/*  215 */           String pk_dept = partTimeVOs[i].getPk_dept();
/*  216 */           DeptVO dept = StringUtils.isBlank(pk_dept) ? null : (DeptVO)getRetrieve().retrieveByPk(null, DeptVO.class, pk_dept);
/*  217 */           if ((dept != null) && (dept.getHrcanceled() != null) && (dept.getHrcanceled().booleanValue()))
/*      */           {
/*  219 */             msg = msg + '\n' + ResHelper.getString("6007psn", "06007psn0321", new String[] { idx + "" });
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  225 */           String pk_post = partTimeVOs[i].getPk_post();
/*  226 */           nc.vo.om.post.PostVO post = StringUtils.isBlank(pk_post) ? null : (nc.vo.om.post.PostVO)getRetrieve().retrieveByPk(null, nc.vo.om.post.PostVO.class, pk_post);
/*      */           
/*      */ 
/*  229 */           if ((post != null) && (post.getHrcanceled() != null) && (post.getHrcanceled().booleanValue()))
/*      */           {
/*  231 */             msg = msg + '\n' + ResHelper.getString("6007psn", "06007psn0322", new String[] { idx + "" });
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  237 */         idx++;
/*      */       } }
/*  239 */     if (!StringUtils.isBlank(msg))
/*      */     {
/*  241 */       throw new BusinessException(msg);
/*      */     }
/*      */     
/*      */ 
/*  245 */     int i = 0; for (int iIndex = 2; i < partTimeVOs.length; i++)
/*      */     {
/*  247 */       if (3 == partTimeVOs[i].getStatus())
/*      */       {
/*  249 */         partTimeVOs[i].setLastflag(UFBoolean.FALSE);
/*      */       }
/*      */       else {
/*  252 */         if (StringUtils.isBlank(partTimeVOs[i].getClerkcode()))
/*      */         {
/*  254 */           throw new BusinessException(ResHelper.getString("6007psn", "06007psn0242"));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  259 */         if (StringUtils.isBlank(partTimeVOs[i].getPk_org()))
/*      */         {
/*  261 */           throw new BusinessException(ResHelper.getString("6007psn", "06007psn0243"));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  266 */         if (StringUtils.isBlank(partTimeVOs[i].getPk_dept()))
/*      */         {
/*  268 */           throw new BusinessException(ResHelper.getString("6007psn", "06007psn0244"));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  273 */         if (StringUtils.isBlank(partTimeVOs[i].getPk_psncl()))
/*      */         {
/*  275 */           throw new BusinessException(ResHelper.getString("6007psn", "06007psn0245"));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  281 */         partTimeVOs[i].setRecordnum(Integer.valueOf(0));
/*  282 */         partTimeVOs[i].setAssgid(Integer.valueOf(iIndex++));
/*  283 */         partTimeVOs[i].setLastflag(UFBoolean.TRUE);
/*  284 */         if (0 == partTimeVOs[i].getStatus())
/*      */         {
/*  286 */           partTimeVOs[i].setStatus(1);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void adjustPoi(PsndocAggVO psndocAggVO)
/*      */     throws BusinessException
/*      */   {
/*  301 */     PsndocVO psndocVO = psndocAggVO.getParentVO();
/*  302 */     Integer iPsnType = psndocVO.getPsnOrgVO().getPsntype();
/*  303 */     if (!ObjectUtils.equals(PsnType.POI.value(), iPsnType))
/*      */     {
/*  305 */       return;
/*      */     }
/*  307 */     PsnJobVO[] psnJobVOs = (PsnJobVO[])psndocAggVO.getTableVO(PsnJobVO.getDefaultTableName());
/*  308 */     if ((psnJobVOs == null) || (psnJobVOs.length == 0))
/*      */     {
/*  310 */       throw new BusinessException(ResHelper.getString("6007psn", "06007psn0246"));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  315 */     PsnJobVO psnJobVO = null;
/*  316 */     for (int i = 0; i < psnJobVOs.length; i++)
/*      */     {
/*  318 */       if ((3 != psnJobVOs[i].getStatus()) && (psnJobVOs[i].getLastflag() != null) && (psnJobVOs[i].getLastflag().booleanValue()))
/*      */       {
/*      */ 
/*  321 */         psnJobVO = psnJobVOs[i];
/*      */       }
/*      */       else {
/*  324 */         psnJobVOs[i].setEndflag(UFBoolean.TRUE);
/*  325 */         psnJobVOs[i].setShoworder(psnJobVOs[i].getRecordnum());
/*      */       } }
/*  327 */     if (psnJobVO == null)
/*      */     {
/*  329 */       throw new BusinessException(ResHelper.getString("6007psn", "06007psn0246"));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  335 */     if ((psndocVO.getPsnOrgVO().getEndflag() != null) && (psndocVO.getPsnOrgVO().getEndflag().booleanValue()) && (psndocVO.getPsnOrgVO().getEnddate() == null))
/*      */     {
/*      */ 
/*  338 */       throw new BusinessException(ResHelper.getString("6007psn", "06007psn0335"));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  345 */     psnJobVO.setEndflag(psndocVO.getPsnOrgVO() == null ? UFBoolean.FALSE : psndocVO.getPsnOrgVO().getEndflag());
/*  346 */     psndocVO.setPsnJobVO(psnJobVO);
/*      */     
/*      */ 
/*  349 */     if (psndocAggVO.getParentVO().getPk_psndoc() == null)
/*      */     {
/*      */ 
/*  352 */       psndocVO.setPk_org(psnJobVO.getPk_org());
/*      */     }
/*  354 */     else if (psndocAggVO.getParentVO().getPsnOrgVO().getPk_psnorg() == null)
/*      */     {
/*      */ 
/*  357 */       psndocVO.setPk_org(psnJobVO.getPk_org());
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  362 */       int orgCount = getRetrieve().getCountByCondition(PsnOrgVO.getDefaultTableName(), " pk_psndoc = '" + psndocAggVO.getParentVO().getPk_psndoc() + "' ");
/*      */       
/*      */ 
/*  365 */       if (orgCount <= 0)
/*      */       {
/*  367 */         psndocVO.setPk_org(psnJobVO.getPk_org());
/*      */       }
/*      */       else
/*      */       {
/*  371 */         String pk_psnjob = ((IPersonRecordService)NCLocator.getInstance().lookup(IPersonRecordService.class)).getEmpPsnjobByPsndoc(psndocAggVO.getParentVO().getPk_psndoc());
/*      */         
/*      */ 
/*  374 */         if (pk_psnjob == null)
/*      */         {
/*      */ 
/*  377 */           psndocVO.setPk_org(psnJobVO.getPk_org());
/*      */ 
/*      */ 
/*      */         }
/*  381 */         else if (pk_psnjob.equals(psnJobVO.getPk_psnjob()))
/*      */         {
/*  383 */           psndocVO.setPk_org(psnJobVO.getPk_org());
/*      */         }
/*      */         else
/*      */         {
/*  387 */           PsnJobVO tmpVO = (PsnJobVO)getRetrieve().retrieveByPk(null, PsnJobVO.class, pk_psnjob);
/*  388 */           psndocVO.setPk_org(tmpVO.getPk_org());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  394 */     if (psndocVO.getPsnOrgVO().getStatus() == 2)
/*      */     {
/*  396 */       psndocVO.getPsnOrgVO().setPk_org(psnJobVO.getPk_org());
/*      */     }
/*  398 */     ArrayList<PsnJobVO> al = new ArrayList();
/*  399 */     for (int i = 0; i < psnJobVOs.length; i++)
/*      */     {
/*  401 */       if (3 != psnJobVOs[i].getStatus())
/*      */       {
/*  403 */         al.add(psnJobVOs[i]);
/*      */       }
/*      */     }
/*  406 */     for (int i = 1; i < al.size(); i++)
/*      */     {
/*  408 */       if (0 == ((PsnJobVO)al.get(i - 1)).getStatus())
/*      */       {
/*  410 */         ((PsnJobVO)al.get(i - 1)).setStatus(1);
/*      */       }
/*      */     }
/*      */     
/*  414 */     if (2 == psndocVO.getStatus())
/*      */     {
/*  416 */       psndocVO.getPsnOrgVO().setBegindate(psndocVO.getPsnJobVO().getBegindate());
/*      */     }
/*      */     
/*  419 */     UFLiteralDate enddate = psndocVO.getPsnOrgVO().getEnddate();
/*      */     
/*  421 */     if ((enddate != null) && (psndocVO.getPsnOrgVO().getPk_psndoc() != null))
/*      */     {
/*  423 */       PsnOrgVO[] nextOrgVO = (PsnOrgVO[])getRetrieve().retrieveByClause(null, PsnOrgVO.class, " pk_psndoc = '" + psndocVO.getPsnOrgVO().getPk_psndoc() + "' and orgrelaid = " + (psndocVO.getPsnOrgVO().getOrgrelaid().intValue() + 1));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  429 */       if ((nextOrgVO != null) && (nextOrgVO.length > 0))
/*      */       {
/*  431 */         UFLiteralDate nextOrgBegin = nextOrgVO[0].getBegindate();
/*  432 */         if (enddate != null)
/*      */         {
/*      */ 
/*  435 */           if (enddate.compareTo(nextOrgBegin) >= 0)
/*      */           {
/*  437 */             throw new BusinessException(ResHelper.getString("6007psn", "06007psn0336"));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void adjustEmp(PsndocAggVO psndocAggVO)
/*      */     throws BusinessException
/*      */   {
/*  455 */     PsndocVO psndocVO = psndocAggVO.getParentVO();
/*  456 */     Integer iPsnType = psndocVO.getPsnOrgVO().getPsntype();
/*  457 */     if (!ObjectUtils.equals(PsnType.EMPLOYEE.value(), iPsnType))
/*      */     {
/*  459 */       return;
/*      */     }
/*  461 */     PsnJobVO[] psnJobVOs = (PsnJobVO[])psndocAggVO.getTableVO(PsnJobVO.getDefaultTableName());
/*      */     
/*  463 */     if ((psnJobVOs == null) || (psnJobVOs.length == 0))
/*      */     {
/*  465 */       return;
/*      */     }
/*      */     
/*  468 */     PsnJobVO psnJobVO = null;
/*  469 */     for (int i = 0; i < psnJobVOs.length; i++)
/*      */     {
/*  471 */       if ((3 != psnJobVOs[i].getStatus()) && (psnJobVOs[i].getLastflag() != null) && (psnJobVOs[i].getLastflag().booleanValue()))
/*      */       {
/*      */ 
/*  474 */         psnJobVO = psnJobVOs[i];
/*  475 */         break;
/*      */       }
/*      */     }
/*      */     
/*  479 */     if (psnJobVO == null)
/*      */     {
/*  481 */       return;
/*      */     }
/*      */     
/*      */ 
/*  485 */     if (psndocAggVO.getParentVO().getPk_psndoc() == null)
/*      */     {
/*      */ 
/*  488 */       psndocVO.setPk_org(psnJobVO.getPk_org());
/*      */     }
/*  490 */     else if ((psndocAggVO.getParentVO().getPsnOrgVO().getPk_psnorg() == null) && (psndocAggVO.getParentVO().getPsnOrgVO().getIndocflag().booleanValue()))
/*      */     {
/*      */ 
/*      */ 
/*  494 */       psndocVO.setPk_org(psnJobVO.getPk_org());
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  499 */       int orgCount = getRetrieve().getCountByCondition(PsnOrgVO.getDefaultTableName(), " pk_psndoc = '" + psndocAggVO.getParentVO().getPk_psndoc() + "' ");
/*      */       
/*      */ 
/*  502 */       if (orgCount <= 0)
/*      */       {
/*  504 */         psndocVO.setPk_org(psnJobVO.getPk_org());
/*      */       }
/*      */       else
/*      */       {
/*  508 */         String pk_psnjob = ((IPersonRecordService)NCLocator.getInstance().lookup(IPersonRecordService.class)).getEmpPsnjobByPsndoc(psndocAggVO.getParentVO().getPk_psndoc());
/*      */         
/*      */ 
/*  511 */         if (pk_psnjob == null)
/*      */         {
/*  513 */           psndocVO.setPk_org(psnJobVO.getPk_org());
/*      */ 
/*      */ 
/*      */         }
/*  517 */         else if (pk_psnjob.equals(psnJobVO.getPk_psnjob()))
/*      */         {
/*  519 */           psndocVO.setPk_org(psnJobVO.getPk_org());
/*      */         }
/*      */         else
/*      */         {
/*  523 */           PsnJobVO tmpVO = (PsnJobVO)getRetrieve().retrieveByPk(null, PsnJobVO.class, pk_psnjob);
/*  524 */           psndocVO.setPk_org(tmpVO.getPk_org());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void adjustPsndocByEdu(PsndocAggVO psndocAggVO)
/*      */     throws BusinessException
/*      */   {
/*  541 */     if (psndocAggVO == null)
/*      */     {
/*  543 */       return;
/*      */     }
/*  545 */     EduVO[] eduVOs = (EduVO[])psndocAggVO.getTableVO(EduVO.getDefaultTableName());
/*  546 */     if ((eduVOs == null) || (eduVOs.length == 0))
/*      */     {
/*  548 */       return;
/*      */     }
/*  550 */     int iLastEducation = 0;
/*      */     
/*  552 */     for (EduVO eduVO : eduVOs)
/*      */     {
/*  554 */       if (3 == eduVO.getStatus())
/*      */       {
/*  556 */         eduVO.setLastflag(UFBoolean.FALSE);
/*      */       }
/*      */       else {
/*  559 */         if ((eduVO.getLasteducation() != null) && (eduVO.getLasteducation().booleanValue()))
/*      */         {
/*  561 */           iLastEducation++;
/*      */         }
/*  563 */         eduVO.setLastflag(UFBoolean.TRUE);
/*  564 */         if (0 == eduVO.getStatus())
/*      */         {
/*  566 */           eduVO.setStatus(1); }
/*      */       }
/*      */     }
/*  569 */     if (iLastEducation == 0)
/*      */     {
/*  571 */       return;
/*      */     }
/*  573 */     if (iLastEducation > 1)
/*      */     {
/*  575 */       throw new BusinessException(ResHelper.getString("6007psn", "06007psn0247"));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void adjustNationDuty(PsndocAggVO psndocAggVO)
/*      */     throws BusinessException
/*      */   {
/*  588 */     if (psndocAggVO == null)
/*      */     {
/*  590 */       return;
/*      */     }
/*  592 */     NationDutyVO[] dutyVOs = (NationDutyVO[])psndocAggVO.getTableVO(NationDutyVO.getDefaultTableName());
/*  593 */     if ((dutyVOs == null) || (dutyVOs.length == 0))
/*      */     {
/*  595 */       return;
/*      */     }
/*  597 */     int iLastEducation = 0;
/*      */     
/*  599 */     for (NationDutyVO dutyVO : dutyVOs)
/*      */     {
/*  601 */       if (3 != dutyVO.getStatus())
/*      */       {
/*      */ 
/*      */ 
/*  605 */         if ((dutyVO.getIstop() != null) && (dutyVO.getIstop().booleanValue()))
/*      */         {
/*  607 */           iLastEducation++;
/*      */         }
/*  609 */         if (0 == dutyVO.getStatus())
/*      */         {
/*  611 */           dutyVO.setStatus(1); }
/*      */       }
/*      */     }
/*  614 */     if (iLastEducation == 0)
/*      */     {
/*  616 */       return;
/*      */     }
/*  618 */     if (iLastEducation > 1)
/*      */     {
/*  620 */       throw new BusinessException(ResHelper.getString("6007psn", "06007psn0248"));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void adjustTitle(PsndocAggVO psndocAggVO)
/*      */     throws BusinessException
/*      */   {
/*  633 */     if (psndocAggVO == null)
/*      */     {
/*  635 */       return;
/*      */     }
/*  637 */     TitleVO[] titleVOs = (TitleVO[])psndocAggVO.getTableVO(TitleVO.getDefaultTableName());
/*  638 */     if ((titleVOs == null) || (titleVOs.length == 0))
/*      */     {
/*  640 */       return;
/*      */     }
/*  642 */     int iLastEducation = 0;
/*      */     
/*  644 */     for (TitleVO titleVO : titleVOs)
/*      */     {
/*  646 */       if (3 != titleVO.getStatus())
/*      */       {
/*      */ 
/*      */ 
/*  650 */         if ((titleVO.getTiptop_flag() != null) && (titleVO.getTiptop_flag().booleanValue()))
/*      */         {
/*  652 */           iLastEducation++;
/*      */         }
/*  654 */         if (0 == titleVO.getStatus())
/*      */         {
/*  656 */           titleVO.setStatus(1); }
/*      */       }
/*      */     }
/*  659 */     if (iLastEducation == 0)
/*      */     {
/*  661 */       return;
/*      */     }
/*  663 */     if (iLastEducation > 1)
/*      */     {
/*  665 */       throw new BusinessException(ResHelper.getString("6007psn", "06007psn0249"));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void adjustPsnjobByTrial(PsndocAggVO psndocAggVO)
/*      */     throws BusinessException
/*      */   {
/*  682 */     if (psndocAggVO == null)
/*      */     {
/*  684 */       return;
/*      */     }
/*  686 */     TrialVO[] trialVOs = (TrialVO[])psndocAggVO.getTableVO(TrialVO.getDefaultTableName());
/*  687 */     if ((trialVOs == null) || (trialVOs.length == 0))
/*      */     {
/*  689 */       TrialVO lastTrialVO = (TrialVO)((IPersonRecordService)NCLocator.getInstance().lookup(IPersonRecordService.class)).getLastVO(TrialVO.class, psndocAggVO.getParentVO().getPsnOrgVO().getPk_psnorg(), null);
/*      */       
/*      */ 
/*  692 */       if ((lastTrialVO == null) || ((lastTrialVO.getEndflag() != null) && (lastTrialVO.getEndflag().booleanValue())))
/*      */       {
/*  694 */         psndocAggVO.getParentVO().getPsnJobVO().setTrial_flag(UFBoolean.FALSE);
/*  695 */         psndocAggVO.getParentVO().getPsnJobVO().setTrial_type(null);
/*      */       }
/*      */       else
/*      */       {
/*  699 */         psndocAggVO.getParentVO().getPsnJobVO().setTrial_flag(UFBoolean.TRUE);
/*  700 */         psndocAggVO.getParentVO().getPsnJobVO().setTrial_type(lastTrialVO.getTrial_type());
/*      */       }
/*  702 */       return;
/*      */     }
/*      */     
/*      */ 
/*  706 */     UFLiteralDate jobBeginDate = psndocAggVO.getParentVO().getPsnJobVO().getBegindate();
/*  707 */     String msg = "";
/*      */     
/*  709 */     String psnName = MultiLangHelper.getName(psndocAggVO.getParentVO());
/*  710 */     for (int i = 0; i < trialVOs.length; i++)
/*      */     {
/*  712 */       if (3 != trialVOs[i].getStatus())
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  717 */         if ((jobBeginDate != null) && (jobBeginDate.afterDate(trialVOs[i].getBegindate())) && (trialVOs[i].getEndflag() != null) && (!trialVOs[i].getEndflag().booleanValue()))
/*      */         {
/*      */ 
/*      */ 
/*  721 */           msg = msg + '\n' + ResHelper.getString("6007psn", "06007psn0250", new String[] { psnName, trialVOs[i].getBegindate() + "" });
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  727 */         UFLiteralDate begindate = trialVOs[i].getBegindate();
/*  728 */         if (begindate != null)
/*      */         {
/*      */ 
/*  731 */           UFLiteralDate regDate = trialVOs[i].getRegulardate();
/*  732 */           if ((regDate != null) && (begindate.afterDate(regDate)))
/*      */           {
/*  734 */             msg = msg + '\n' + ResHelper.getString("6007psn", "06007psn0251", new String[] { psnName, trialVOs[i].getBegindate() + "" });
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  742 */     if (!StringUtils.isBlank(msg))
/*      */     {
/*  744 */       throw new BusinessException(msg);
/*      */     }
/*      */     
/*  747 */     TrialVO lastTrialVO = null;
/*  748 */     for (TrialVO trialVO : trialVOs)
/*      */     {
/*  750 */       if (3 != trialVO.getStatus())
/*      */       {
/*      */ 
/*      */ 
/*  754 */         if (trialVO.getTrial_type() == null)
/*      */         {
/*  756 */           throw new BusinessException(ResHelper.getString("6007psn", "06007psn0252"));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  761 */         if ((trialVO.getRecordnum() != null) && (trialVO.getRecordnum().intValue() == 0))
/*      */         {
/*  763 */           lastTrialVO = trialVO;
/*      */         }
/*  765 */         if (0 == trialVO.getStatus())
/*      */         {
/*  767 */           trialVO.setStatus(1); }
/*      */       }
/*      */     }
/*  770 */     psndocAggVO.setTableVO(TrialVO.getDefaultTableName(), trialVOs);
/*  771 */     if ((lastTrialVO != null) && ((lastTrialVO.getEndflag() == null) || (!lastTrialVO.getEndflag().booleanValue())))
/*      */     {
/*  773 */       psndocAggVO.getParentVO().getPsnJobVO().setTrial_flag(UFBoolean.TRUE);
/*  774 */       psndocAggVO.getParentVO().getPsnJobVO().setTrial_type(lastTrialVO.getTrial_type());
/*      */     }
/*      */     else
/*      */     {
/*  778 */       psndocAggVO.getParentVO().getPsnJobVO().setTrial_flag(UFBoolean.FALSE);
/*  779 */       psndocAggVO.getParentVO().getPsnJobVO().setTrial_type(null);
/*      */     }
/*      */     
/*  782 */     if (0 == psndocAggVO.getParentVO().getPsnJobVO().getStatus())
/*      */     {
/*  784 */       psndocAggVO.getParentVO().getPsnJobVO().setStatus(1);
/*      */     }
/*      */     
/*  787 */     if ((psndocAggVO.getTableVO(PsnJobVO.getDefaultTableName()) == null) || (psndocAggVO.getTableVO(PsnJobVO.getDefaultTableName()).length == 0))
/*      */     {
/*      */ 
/*  790 */       psndocAggVO.setTableVO(PsnJobVO.getDefaultTableName(), new PsnJobVO[] { psndocAggVO.getParentVO().getPsnJobVO() });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void adjustRecordNumLastFlag(PsndocAggVO psndocAggVO)
/*      */   {
/*  813 */     if (psndocAggVO == null)
/*      */     {
/*  815 */       return;
/*      */     }
/*  817 */     String[] strTableCodes = psndocAggVO.getTableCodes();
/*  818 */     if ((strTableCodes == null) || (strTableCodes.length == 0))
/*      */     {
/*  820 */       return;
/*      */     }
/*  822 */     for (String strTableCode : strTableCodes)
/*      */     {
/*      */ 
/*  825 */       if (!PsnOrgVO.getDefaultTableName().equals(strTableCode))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  831 */         SuperVO[] superVOs = psndocAggVO.getTableVO(strTableCode);
/*  832 */         if ((superVOs != null) && (superVOs.length != 0))
/*      */         {
/*      */ 
/*      */ 
/*  836 */           int iIndex = 0;
/*  837 */           for (int i = superVOs.length - 1; i > -1; i--)
/*      */           {
/*  839 */             if (3 == superVOs[i].getStatus())
/*      */             {
/*  841 */               superVOs[i].setAttributeValue("lastflag", UFBoolean.FALSE);
/*      */             }
/*      */             else {
/*  844 */               superVOs[i].setAttributeValue("recordnum", Integer.valueOf(iIndex));
/*  845 */               superVOs[i].setAttributeValue("lastflag", iIndex == 0 ? UFBoolean.TRUE : UFBoolean.FALSE);
/*  846 */               iIndex++;
/*  847 */               if (0 == superVOs[i].getStatus())
/*      */               {
/*  849 */                 superVOs[i].setStatus(1);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ValidationFailure validate(Object obj)
/*      */   {
/*  863 */     if ((obj == null) || (!(obj instanceof PsndocAggVO)))
/*      */     {
/*  865 */       return new ValidationFailure(ResHelper.getString("6007psn", "06007psn0253"));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  870 */     PsndocAggVO psndocAggVO = (PsndocAggVO)obj;
/*  871 */     PsndocVO psndocVO = psndocAggVO.getParentVO();
/*  872 */     PsnOrgVO psnOrgVO = null;
/*  873 */     PsnJobVO psnJobVO = null;
/*  874 */     if ((psndocVO == null) || ((psnOrgVO = psndocVO.getPsnOrgVO()) == null) || ((psnJobVO = psndocVO.getPsnJobVO()) == null))
/*      */     {
/*  876 */       return new ValidationFailure(ResHelper.getString("6007psn", "06007psn0254"));
/*      */     }
/*      */     
/*      */     try
/*      */     {
/*  881 */       adjustPsndoc(psndocAggVO);
/*      */     }
/*      */     catch (BusinessException e)
/*      */     {
/*  885 */       return new ValidationFailure(e.getMessage());
/*      */     }
/*      */     
/*      */ 
/*  889 */     if ((psndocAggVO.getAllChildrenVO() == null) || (psndocAggVO.getAllChildrenVO().length == 0))
/*      */     {
/*  891 */       return null;
/*      */     }
/*      */     try
/*      */     {
/*  895 */       adjustRecordNumLastFlag(psndocAggVO);
/*  896 */       adjustPsndocByEdu(psndocAggVO);
/*      */       
/*  898 */       adjustPsndocByCert(psndocAggVO);
/*  899 */       adjustPsnjobByTrial(psndocAggVO);
/*  900 */       adjustCtrt(psndocAggVO);
/*  901 */       adjustPartTime(psndocAggVO);
/*  902 */       adjustPoi(psndocAggVO);
/*  903 */       adjustEmp(psndocAggVO);
/*  904 */       adjustPsnjob(psndocAggVO);
/*  905 */       adjustNationDuty(psndocAggVO);
/*  906 */       adjustTitle(psndocAggVO);
/*  907 */       adjustKeyPsn(psndocAggVO);
/*  908 */       adjustCapa(psndocAggVO);
/*      */       
/*  910 */       validateData(psndocAggVO);
/*      */     }
/*      */     catch (BusinessException ex)
/*      */     {
/*  914 */       return new ValidationFailure(ex.getMessage());
/*      */     }
/*  916 */     String[] strTabCodes = psndocAggVO.getTableCodes();
/*  917 */     for (String strTabCode : strTabCodes)
/*      */     {
/*      */ 
/*  920 */       if (PsndocAggVO.hashBusinessInfoSet.contains(strTabCode))
/*      */       {
/*  922 */         validateBusnessInfoSet(psndocAggVO, psndocVO, psnOrgVO, psnJobVO, psndocAggVO.getTableVO(strTabCode));
/*      */       }
/*      */       else
/*      */       {
/*  926 */         validateUnBusnessInfoSet(psndocAggVO, psndocVO, psnOrgVO, psnJobVO, psndocAggVO.getTableVO(strTabCode));
/*      */       }
/*      */     }
/*  929 */     return null;
/*      */   }
/*      */   
/*      */   private void adjustCapa(PsndocAggVO psndocAggVO) throws BusinessException
/*      */   {
/*  934 */     if (psndocAggVO == null)
/*      */     {
/*  936 */       return;
/*      */     }
/*  938 */     CapaVO[] capaVOs = (CapaVO[])psndocAggVO.getTableVO(CapaVO.getDefaultTableName());
/*  939 */     if ((capaVOs == null) || (capaVOs.length == 0))
/*      */     {
/*  941 */       return;
/*      */     }
/*      */     
/*      */ 
/*  945 */     HashMap<String, ArrayList<CapaVO>> hm = new HashMap();
/*  946 */     for (CapaVO capaVO : capaVOs)
/*      */     {
/*  948 */       if (3 == capaVO.getStatus())
/*      */       {
/*  950 */         capaVO.setLastflag(UFBoolean.FALSE);
/*      */       }
/*      */       else {
/*  953 */         if (hm.get(capaVO.getPk_pe_indi()) == null)
/*      */         {
/*  955 */           hm.put(capaVO.getPk_pe_indi(), new ArrayList());
/*      */         }
/*  957 */         ((ArrayList)hm.get(capaVO.getPk_pe_indi())).add(capaVO);
/*      */       }
/*      */     }
/*  960 */     String msg = "";
/*  961 */     for (String key : hm.keySet())
/*      */     {
/*  963 */       if (((ArrayList)hm.get(key)).size() > 1)
/*      */       {
/*  965 */         CPIndiVO indi = (CPIndiVO)getRetrieve().retrieveByPk(null, CPIndiVO.class, key);
/*  966 */         msg = msg + "," + MultiLangHelper.getName(indi);
/*      */       }
/*      */     }
/*  969 */     if (StringUtils.isNotBlank(msg))
/*      */     {
/*  971 */       throw new BusinessException(ResHelper.getString("6007psn", "06007psn0418") + msg.substring(1));
/*      */     }
/*      */     
/*      */ 
/*  975 */     for (CapaVO capaVO : capaVOs)
/*      */     {
/*  977 */       if (3 == capaVO.getStatus())
/*      */       {
/*  979 */         capaVO.setLastflag(UFBoolean.FALSE);
/*      */       }
/*      */       else {
/*  982 */         capaVO.setLastflag(UFBoolean.TRUE);
/*  983 */         capaVO.setRecordnum(Integer.valueOf(0));
/*  984 */         if (0 == capaVO.getStatus())
/*      */         {
/*  986 */           capaVO.setStatus(1);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void adjustKeyPsn(PsndocAggVO psndocAggVO) throws BusinessException {
/*  993 */     if (psndocAggVO == null)
/*      */     {
/*  995 */       return;
/*      */     }
/*  997 */     KeyPsnVO[] keypsn = (KeyPsnVO[])psndocAggVO.getTableVO(KeyPsnVO.getDefaultTableName());
/*  998 */     if ((keypsn == null) || (keypsn.length == 0))
/*      */     {
/* 1000 */       return;
/*      */     }
/*      */     
/* 1003 */     ArrayList<String> al = new ArrayList();
/* 1004 */     for (int i = 0; i < keypsn.length; i++)
/*      */     {
/* 1006 */       if (keypsn[i].getStatus() != 3)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1011 */         if ((keypsn[i].getEndflag() == null) || (!keypsn[i].getEndflag().booleanValue()))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1016 */           String pk_psnorg = psndocAggVO.getParentVO().getPsnOrgVO().getPk_psnorg();
/*      */           
/* 1018 */           String sql = " pk_psnorg = '" + pk_psnorg + "' and pk_keypsn_grp = '" + keypsn[i].getPk_keypsn_grp() + "' and endflag <> 'Y' ";
/* 1019 */           if (keypsn[i].getPk_psndoc_sub() != null)
/*      */           {
/* 1021 */             sql = sql + " and pk_psndoc_sub <> '" + keypsn[i].getPk_psndoc_sub() + "' ";
/*      */           }
/* 1023 */           int count = getRetrieve().getCountByCondition(KeyPsnVO.getDefaultTableName(), sql);
/* 1024 */           if (count > 0)
/*      */           {
/* 1026 */             KeyPsnGrpVO grp = (KeyPsnGrpVO)getRetrieve().retrieveByPk(null, KeyPsnGrpVO.class, keypsn[i].getPk_keypsn_grp());
/* 1027 */             throw new BusinessException(MessageFormat.format(ResHelper.getString("6007psn", "06007psn0369"), new Object[] { MultiLangHelper.getName(grp) }));
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1035 */           if (!al.contains(keypsn[i].getPk_keypsn_grp()))
/*      */           {
/* 1037 */             al.add(keypsn[i].getPk_keypsn_grp());
/*      */           }
/*      */           else
/*      */           {
/* 1041 */             KeyPsnGrpVO grp = (KeyPsnGrpVO)getRetrieve().retrieveByPk(null, KeyPsnGrpVO.class, keypsn[i].getPk_keypsn_grp());
/* 1042 */             throw new BusinessException(MessageFormat.format(ResHelper.getString("6007psn", "06007psn0369"), new Object[] { MultiLangHelper.getName(grp) }));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void adjustPsndocByCert(PsndocAggVO psndocAggVO)
/*      */     throws BusinessException
/*      */   {
/* 1057 */     if (psndocAggVO == null)
/*      */     {
/* 1059 */       return;
/*      */     }
/* 1061 */     CertVO[] certVOs = (CertVO[])psndocAggVO.getTableVO(CertVO.getDefaultTableName());
/* 1062 */     ArrayList<CertVO> al = new ArrayList();
/* 1063 */     if ((certVOs == null) || (certVOs.length == 0))
/*      */     {
/*      */ 
/* 1066 */       CertVO[] vos = (CertVO[])new PsndocDAO().queryByCondition(CertVO.class, " pk_psndoc = '" + psndocAggVO.getParentVO().getPk_psndoc() + "' and iseffect = 'Y' order by recordnum desc ");
/*      */       
/*      */ 
/* 1069 */       for (int i = 0; (vos != null) && (i < vos.length); i++)
/*      */       {
/* 1071 */         al.add(vos[i]);
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1078 */       for (CertVO certVO : certVOs)
/*      */       {
/* 1080 */         if (3 != certVO.getStatus())
/*      */         {
/*      */ 
/*      */ 
/* 1084 */           if ((certVO.getIseffect() != null) && (certVO.getIseffect().booleanValue()))
/*      */           {
/* 1086 */             al.add(certVO);
/*      */           }
/*      */         }
/*      */       }
/* 1090 */       if (al.size() == 0)
/*      */       {
/* 1092 */         throw new BusinessException(ResHelper.getString("6007psn", "06007psn0255"));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1098 */       int isDefault = 0;
/*      */       
/* 1100 */       CertVO defaultVO = null;
/* 1101 */       for (CertVO certVO : certVOs)
/*      */       {
/* 1103 */         if (3 == certVO.getStatus())
/*      */         {
/* 1105 */           certVO.setLastflag(UFBoolean.FALSE);
/*      */         }
/*      */         else {
/* 1108 */           if ((certVO.getIsstart() != null) && (certVO.getIsstart().booleanValue()))
/*      */           {
/* 1110 */             defaultVO = certVO;
/* 1111 */             isDefault++;
/*      */           }
/* 1113 */           certVO.setLastflag(UFBoolean.TRUE);
/* 1114 */           if (0 == certVO.getStatus())
/*      */           {
/* 1116 */             certVO.setStatus(1); }
/*      */         }
/*      */       }
/* 1119 */       if (isDefault == 0)
/*      */       {
/* 1121 */         throw new BusinessException(ResHelper.getString("6007psn", "06007psn0256"));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1126 */       if (isDefault > 1)
/*      */       {
/* 1128 */         throw new BusinessException(ResHelper.getString("6007psn", "06007psn0257"));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1133 */       if ((defaultVO.getIseffect() == null) || (!defaultVO.getIseffect().booleanValue()))
/*      */       {
/* 1135 */         throw new BusinessException(ResHelper.getString("6007psn", "06007psn0258"));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1142 */       for (CertVO certVO : certVOs)
/*      */       {
/* 1144 */         if (3 != certVO.getStatus())
/*      */         {
/*      */ 
/*      */ 
/* 1148 */           if ((certVO.getIsstart() != null) && (certVO.getIsstart().booleanValue()))
/*      */           {
/* 1150 */             psndocAggVO.getParentVO().setIdtype(certVO.getIdtype());
/* 1151 */             psndocAggVO.getParentVO().setId(certVO.getId());
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1156 */       for (int i = 0; i < al.size(); i++)
/*      */       {
/* 1158 */         for (int j = i + 1; j < al.size(); j++)
/*      */         {
/* 1160 */           if ((((CertVO)al.get(i)).getIdtype() == ((CertVO)al.get(j)).getIdtype()) && (((CertVO)al.get(i)).getId().equals(((CertVO)al.get(j)).getId())))
/*      */           {
/* 1162 */             throw new BusinessException(ResHelper.getString("6007psn", "06007psn0259"));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1172 */     checkIDUnique(psndocAggVO, al);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkIDUnique(PsndocAggVO psndocAggVO, ArrayList<CertVO> al)
/*      */     throws BusinessException
/*      */   {
/* 1186 */     if ((al == null) || (al.size() == 0))
/*      */     {
/* 1188 */       return;
/*      */     }
/* 1190 */     ArrayList<String> listField = new ArrayList();
/* 1191 */     String[] strPsndocFieldCodes = (null != psndocAggVO) && (null != psndocAggVO.getParentVO()) ? psndocAggVO.getParentVO().getAttributeNames() : new PsndocVO().getAttributeNames();
/*      */     
/*      */ 
/* 1194 */     for (int i = 0; i < strPsndocFieldCodes.length; i++)
/*      */     {
/* 1196 */       listField.add("bd_psndoc." + strPsndocFieldCodes[i] + " as bd_psndoc_" + strPsndocFieldCodes[i]);
/*      */     }
/* 1198 */     String[] strPsnJobFieldCodes = new PsnJobVO().getAttributeNames();
/* 1199 */     for (int i = 0; i < strPsnJobFieldCodes.length; i++)
/*      */     {
/* 1201 */       listField.add("hi_psnjob." + strPsnJobFieldCodes[i] + " as hi_psnjob_" + strPsnJobFieldCodes[i]);
/*      */     }
/*      */     
/* 1204 */     String strBaseWhere = "(";
/*      */     
/* 1206 */     strBaseWhere = strBaseWhere + " ( hi_psndoc_cert.id = '" + NCESAPI.sqlEncode(((CertVO)al.get(0)).getId()) + "' and hi_psndoc_cert.idtype = '" + ((CertVO)al.get(0)).getIdtype() + "' ) ";
/* 1207 */     for (int i = 1; i < al.size(); i++)
/*      */     {
/*      */ 
/* 1210 */       strBaseWhere = strBaseWhere + " or ( hi_psndoc_cert.id = '" + NCESAPI.sqlEncode(((CertVO)al.get(i)).getId()) + "' and hi_psndoc_cert.idtype = '" + ((CertVO)al.get(i)).getIdtype() + "'  ) ";
/*      */     }
/*      */     
/*      */ 
/* 1214 */     strBaseWhere = strBaseWhere + ") and hi_psndoc_cert.iseffect = 'Y' ";
/*      */     
/* 1216 */     if (psndocAggVO.getParentVO().getName().startsWith("'"))
/*      */     {
/* 1218 */       strBaseWhere = strBaseWhere + " and bd_psndoc.name = '" + NCESAPI.sqlEncode(psndocAggVO.getParentVO().getName()) + "'";
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1223 */       strBaseWhere = strBaseWhere + " and bd_psndoc.name = '" + NCESAPI.sqlEncode(psndocAggVO.getParentVO().getName()) + "'";
/*      */     }
/*      */     
/* 1226 */     if ((null != psndocAggVO) && (null != psndocAggVO.getParentVO()) && (null != psndocAggVO.getParentVO().getPk_psndoc()))
/*      */     {
/* 1228 */       strBaseWhere = strBaseWhere + " and bd_psndoc.pk_psndoc <> '" + NCESAPI.sqlEncode(psndocAggVO.getParentVO().getPk_psndoc()) + "'";
/*      */     }
/* 1230 */     PsndocVO[] psndocVOs = new PsndocDAO().queryPsndocVOByCondition(null, listField, new String[] { "hi_psndoc_cert" }, strBaseWhere, null);
/*      */     
/* 1232 */     if ((psndocVOs != null) && (psndocVOs.length > 0))
/*      */     {
/* 1234 */       PsndocVO doc = psndocVOs[0];
/*      */       
/* 1236 */       CertVO sameVO = new CertVO();
/* 1237 */       sameVO.setIdtype(doc.getIdtype());
/* 1238 */       sameVO.setId(doc.getId());
/* 1239 */       String orgname = VOUtils.getDocName(OrgVO.class, doc.getPsnJobVO().getPk_org());
/* 1240 */       orgname = StringUtils.isBlank(orgname) ? ResHelper.getString("6007psn", "06007psn0148") : orgname;
/* 1241 */       String deptname = VOUtils.getDocName(DeptVO.class, doc.getPsnJobVO().getPk_dept());
/* 1242 */       deptname = StringUtils.isBlank(deptname) ? ResHelper.getString("6007psn", "06007psn0148") : deptname;
/*      */       
/*      */ 
/*      */ 
/* 1246 */       String postname = VOUtils.getDocName(nc.vo.org.PostVO.class, doc.getPsnJobVO().getPk_post());
/* 1247 */       postname = StringUtils.isBlank(postname) ? ResHelper.getString("6007psn", "06007psn0148") : postname;
/*      */       
/*      */ 
/*      */ 
/* 1251 */       PsnIdtypeVO psnIdtypeVO = (PsnIdtypeVO)((IPersistenceRetrieve)NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).retrieveByPk(null, PsnIdtypeVO.class, sameVO.getIdtype());
/*      */       
/*      */ 
/* 1254 */       String msg = ResHelper.getString("6007psn", "06007psn0260") + '\n' + ResHelper.getString("6007psn", "06007psn0261", new String[] { VOUtils.getNameByVO(psnIdtypeVO), sameVO.getId(), doc.getName(), doc.getCode(), orgname, deptname, postname });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1262 */       throw new BusinessException(msg);
/*      */     }
/*      */   }
/*      */   
/*      */   protected void adjustPsnjob(PsndocAggVO psndocAggVO) throws BusinessException
/*      */   {
/* 1268 */     if (psndocAggVO == null)
/*      */     {
/* 1270 */       return;
/*      */     }
/* 1272 */     PsnJobVO[] psnjob = (PsnJobVO[])psndocAggVO.getTableVO(PsnJobVO.getDefaultTableName());
/* 1273 */     if ((psnjob == null) || (psnjob.length == 0))
/*      */     {
/* 1275 */       return;
/*      */     }
/* 1277 */     for (int i = 0; i < psnjob.length; i++)
/*      */     {
/* 1279 */       if (3 != psnjob[i].getStatus())
/*      */       {
/*      */ 
/*      */ 
/* 1283 */         if (StringUtils.isBlank(psnjob[i].getClerkcode()))
/*      */         {
/* 1285 */           throw new BusinessException(ResHelper.getString("6007psn", "06007psn0262"));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1290 */         if (StringUtils.isBlank(psnjob[i].getPk_org()))
/*      */         {
/* 1292 */           throw new BusinessException(ResHelper.getString("6007psn", "06007psn0263"));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1297 */         if ((StringUtils.isBlank(psnjob[i].getPk_dept())) && (!ObjectUtils.equals(PsnType.POI.value(), psnjob[i].getPsntype())))
/*      */         {
/* 1299 */           throw new BusinessException(ResHelper.getString("6007psn", "06007psn0264"));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1304 */         if (StringUtils.isBlank(psnjob[i].getPk_psncl()))
/*      */         {
/* 1306 */           throw new BusinessException(ResHelper.getString("6007psn", "06007psn0265"));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1311 */         if ((psnjob[i].getTrnsevent() == null) && (!ObjectUtils.equals(PsnType.POI.value(), psnjob[i].getPsntype())) && (psnjob[i].getIsmainjob().booleanValue()))
/*      */         {
/*      */ 
/* 1314 */           throw new BusinessException(ResHelper.getString("6007psn", "06007psn0478"));
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void adjustPsndoc(PsndocAggVO psndocAggVO)
/*      */     throws BusinessException
/*      */   {
/* 1324 */     if (psndocAggVO == null)
/*      */     {
/* 1326 */       return;
/*      */     }
/* 1328 */     PsndocVO psndoc = psndocAggVO.getParentVO();
/* 1329 */     if (psndoc == null)
/*      */     {
/* 1331 */       return;
/*      */     }
/* 1333 */     if (StringUtils.isBlank(psndoc.getCode()))
/*      */     {
/* 1335 */       throw new BusinessException(ResHelper.getString("6007psn", "06007psn0266"));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1340 */     if (StringUtils.isBlank(psndoc.getName()))
/*      */     {
/* 1342 */       throw new BusinessException(ResHelper.getString("6007psn", "06007psn0267"));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1348 */     setPreviewPhoto(psndoc);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setPreviewPhoto(PsndocVO psndoc)
/*      */   {
/* 1360 */     if (psndoc.getPhoto() == null)
/*      */     {
/* 1362 */       psndoc.setPreviewphoto(null);
/* 1363 */       return;
/*      */     }
/*      */     
/* 1366 */     byte[] imgData = VOUtils.transPreviewPhoto((byte[])psndoc.getPhoto());
/* 1367 */     psndoc.setPreviewphoto(imgData);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void validateBusnessInfoSet(PsndocAggVO psndocAggVO, PsndocVO psndocVO, PsnOrgVO psnOrgVO, PsnJobVO psnJobVO, SuperVO[] superVOs) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void validateData(PsndocAggVO psndocAggVO)
/*      */     throws BusinessException
/*      */   {
/* 1397 */     CommnonValidator.validateLiteralDate(psndocAggVO.getParentVO().getPsnOrgVO(), "begindate", "enddate", ResHelper.getString("6007psn", "06007psn0268"));
/*      */     
/*      */ 
/* 1400 */     CommnonValidator.validateLiteralDate(psndocAggVO.getParentVO().getPsnJobVO(), "begindate", "enddate", ResHelper.getString("6007psn", "06007psn0269"));
/*      */     
/*      */ 
/* 1403 */     String[] busiSet = { PsnOrgVO.getDefaultTableName(), PsnJobVO.getDefaultTableName(), TrialVO.getDefaultTableName(), PsnChgVO.getDefaultTableName(), CtrtVO.getDefaultTableName(), RetireVO.getDefaultTableName() };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1412 */     String[] checkSet = { PsnJobVO.getDefaultTableName(), PsnChgVO.getDefaultTableName(), RetireVO.getDefaultTableName() };
/*      */     
/* 1414 */     String[] tabCodes = psndocAggVO.getTableCodes();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1423 */     HashMap<String, InfoSetVO> infoSetMap = InfoSetHelper.getInfoSetByCode("1001Z710000000002XPO", tabCodes);
/* 1424 */     InfoSetVO[] infoset = (InfoSetVO[])infoSetMap.values().toArray(new InfoSetVO[0]);
/* 1425 */     for (int i = 0; (tabCodes != null) && (i < tabCodes.length); i++)
/*      */     {
/* 1427 */       if ((psndocAggVO.getTableVO(tabCodes[i]) != null) && (psndocAggVO.getTableVO(tabCodes[i]).length != 0))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1432 */         if (!CtrtVO.getDefaultTableName().equals(tabCodes[i]))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1437 */           boolean isBusinessSub = ArrayUtils.contains(busiSet, tabCodes[i]);
/* 1438 */           boolean isCheckBtwRds = ArrayUtils.contains(checkSet, tabCodes[i]);
/* 1439 */           String tableName = getTableName(tabCodes[i], infoset);
/* 1440 */           InfoSetVO infoSetVO = (InfoSetVO)infoSetMap.get(tabCodes[i]);
/* 1441 */           if (infoSetVO != null)
/*      */           {
/*      */ 
/*      */ 
/* 1445 */             if (SpeItemVO.getDefaultTableName().equals(tabCodes[i]))
/*      */             {
/* 1447 */               String beginName = getItemName(tabCodes[i], "spebegin", infoSetVO);
/* 1448 */               String endName = getItemName(tabCodes[i], "speend", infoSetVO);
/* 1449 */               CommnonValidator.validateLiteralDate(psndocAggVO.getTableVO(tabCodes[i]), "spebegin", beginName, "speend", endName, tableName, isBusinessSub, isCheckBtwRds);
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/* 1454 */               String beginName = getItemName(tabCodes[i], "begindate", infoSetVO);
/* 1455 */               String endName = getItemName(tabCodes[i], "enddate", infoSetVO);
/* 1456 */               CommnonValidator.validateLiteralDate(psndocAggVO.getTableVO(tabCodes[i]), "begindate", beginName, "enddate", endName, tableName, isBusinessSub, isCheckBtwRds);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String getItemName(String tabCode, String itemKey, InfoSetVO infoset)
/*      */     throws BusinessException
/*      */   {
/* 1471 */     HashMap<String, InfoItemVO> infoItemMap = (HashMap)InfoSetHelper.getInfoItemByCode(infoset.getPk_infoset(), new String[] { itemKey });
/*      */     
/* 1473 */     if ((infoItemMap != null) && (infoItemMap.size() > 0))
/*      */     {
/* 1475 */       InfoItemVO infoItemVO = (InfoItemVO)infoItemMap.get(itemKey);
/* 1476 */       return NCLangRes4VoTransl.getNCLangRes().getStrByID(infoItemVO.getRespath(), infoItemVO.getItem_name(), infoItemVO.getResid(), null);
/*      */     }
/*      */     
/*      */ 
/* 1480 */     return null;
/*      */   }
/*      */   
/*      */   private String getTableName(String tabCode, InfoSetVO[] infoset)
/*      */   {
/* 1485 */     for (int i = 0; i < infoset.length; i++)
/*      */     {
/* 1487 */       if (tabCode.equals(infoset[i].getInfoset_code()))
/*      */       {
/* 1489 */         return StringUtils.isBlank(infoset[i].getRespath()) ? infoset[i].getInfoset_name() : ResHelper.getString(infoset[i].getRespath(), infoset[i].getResid());
/*      */       }
/*      */     }
/*      */     
/* 1493 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void validateUnBusnessInfoSet(PsndocAggVO psndocAggVO, PsndocVO psndocVO, PsnOrgVO psnOrgVO, PsnJobVO psnJobVO, SuperVO[] superVOs) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private IPersistenceRetrieve getRetrieve()
/*      */   {
/* 1514 */     if (retrieve == null)
/*      */     {
/* 1516 */       retrieve = ((IPersistenceRetrieve)NCLocator.getInstance().lookup(IPersistenceRetrieve.class));
/*      */     }
/* 1518 */     return retrieve;
/*      */   }
/*      */ }

/* Location:           E:\TaiWan\home_20180109\home\modules\hrhi\META-INF\lib\hrhi_personnelmgt.jar
 * Qualified Name:     nc.impl.hi.psndoc.PsndocValidator
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */