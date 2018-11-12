/*     */ package nc.ui.bd.psnbankacc.view;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.Action;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.SwingUtilities;
/*     */ import nc.bs.framework.common.NCLocator;
/*     */ import nc.bs.logging.Logger;
/*     */ import nc.bs.uif2.BusinessExceptionAdapter;
/*     */ import nc.desktop.ui.WorkbenchEnvironment;
/*     */ import nc.itf.bd.bankdoc.IBankdocQueryService;
/*     */ import nc.itf.uap.IUAPQueryBS;
/*     */ import nc.ui.bd.bankacc.iban.BankaccnumRefPane;
/*     */ import nc.ui.bd.bankacc.iban.IbanUtil;
/*     */ import nc.ui.bd.psnbankacc.model.PsnBankaccAppModel;
/*     */ import nc.ui.bd.ref.AbstractRefModel;
/*     */ import nc.ui.bd.ref.model.BankDocDefaultRefTreeModel;
/*     */ import nc.ui.bd.ref.model.NetbankTemplateDefaultRefModel;
/*     */ import nc.ui.bd.ref.model.PsndocDefaultRefModel;
/*     */ import nc.ui.pub.beans.UIButton;
/*     */ import nc.ui.pub.beans.UIRefPane;
/*     */ import nc.ui.pub.beans.UITextField;
/*     */ import nc.ui.pub.bill.BillCardPanel;
/*     */ import nc.ui.pub.bill.BillData;
/*     */ import nc.ui.pub.bill.BillEditEvent;
/*     */ import nc.ui.pub.bill.BillEditListener;
/*     */ import nc.ui.pub.bill.BillItem;
/*     */ import nc.ui.pub.bill.BillModel;
/*     */ import nc.ui.pub.bill.BillScrollPane;
/*     */ import nc.ui.uif2.AppEvent;
/*     */ import nc.ui.uif2.IFunNodeClosingListener;
/*     */ import nc.ui.uif2.components.AutoShowUpEventSource;
/*     */ import nc.ui.uif2.components.IAutoShowUpComponent;
/*     */ import nc.ui.uif2.components.IAutoShowUpEventListener;
/*     */ import nc.ui.uif2.components.IComponentWithActions;
/*     */ import nc.ui.uif2.components.ITabbedPaneAwareComponent;
/*     */ import nc.ui.uif2.components.ITabbedPaneAwareComponentListener;
/*     */ import nc.ui.uif2.components.TabbedPaneAwareCompnonetDelegate;
/*     */ import nc.ui.uif2.editor.BillForm;
/*     */ import nc.ui.uif2.model.AbstractAppModel;
/*     */ import nc.vo.bd.bankaccount.BankAccbasVO;
/*     */ import nc.vo.bd.bankdoc.BankdocVO;
/*     */ import nc.vo.bd.banktype.BanktypeCodeConst;
/*     */ import nc.vo.bd.psnbankacc.PsnBankaccUnionVO;
/*     */ import nc.vo.org.GroupVO;
/*     */ import nc.vo.org.OrgQueryUtil;
/*     */ import nc.vo.org.OrgVO;
/*     */ import nc.vo.pub.BusinessException;
/*     */ import nc.vo.pub.ValidationException;
/*     */ import nc.vo.pub.lang.UFDateTime;
/*     */ import nc.vo.uap.rbac.FuncSubInfo;
/*     */ import nc.vo.uif2.LoginContext;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PsnBankaccEditor
/*     */   extends BillForm
/*     */   implements IAutoShowUpComponent, ITabbedPaneAwareComponent, BillEditListener, IComponentWithActions
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private IAutoShowUpComponent autoShowUpComponent;
/*     */   private ITabbedPaneAwareComponent tabbedPaneAwareComponent;
/*     */   private IFunNodeClosingListener closingListener;
/*     */   private IBankdocQueryService bankDocQueryService;
/*     */   private IUAPQueryBS queryService;
/*  69 */   private List<Action> actions = null;
/*     */   
/*  71 */   private Map<String, String> map = new HashMap();
/*     */   
/*  73 */   private BankaccnumRefPane ibanRefPane = null;
/*     */   
/*     */   public PsnBankaccEditor()
/*     */   {
/*  77 */     this.autoShowUpComponent = new AutoShowUpEventSource(this);
/*  78 */     this.tabbedPaneAwareComponent = new TabbedPaneAwareCompnonetDelegate();
/*     */   }
/*     */   
/*     */   public void initUI()
/*     */   {
/*  83 */     super.initUI();
/*  84 */     resetAccnumComponent();
/*  85 */     this.billCardPanel.addEditListener(this);
/*     */     
/*  87 */     UIRefPane areacodeRef = (UIRefPane)this.billCardPanel.getHeadItem("a.areacode").getComponent();
/*     */     
/*  89 */     areacodeRef.setNotLeafSelectedEnabled(false);
/*  90 */     UIRefPane bankaccbasRef = (UIRefPane)this.billCardPanel.getHeadItem("pk_bankaccbas").getComponent();
/*     */     
/*     */ 
/*  93 */     bankaccbasRef.getRefModel().setMutilLangNameRef(false);
/*  94 */     initOrgRef();
/*  95 */     getBillCardPanel().setBillData(getBillCardPanel().getBillData());
/*     */   }
/*     */   
/*     */   private void resetAccnumComponent() {
/*  99 */     UIRefPane ibanRef = getIbanRefPane();
/* 100 */     ibanRef.getUITextField().setTextType("TextAccountStr");
/* 101 */     ibanRef.getUIButton().setEnabled(false);
/* 102 */     getBillCardPanel().getHeadItem("a.accnum").setComponent(ibanRef);
/*     */     
/* 104 */     getBillCardPanel().getHeadItem("a.accnum").setEdit(false);
/*     */   }
/*     */   
/*     */   private void initOrgRef() {
/* 108 */     UIRefPane orgPane = (UIRefPane)getBillCardPanel().getHeadItem("pk_org").getComponent();
/*     */     
/*     */ 
/* 111 */     orgPane.getRefModel().setUseDataPower(true);
/*     */     
/* 113 */     String[] pkorgs = OrgQueryUtil.filterPkorgsByOrgType("ADMINORGTYPE00000000", getModel().getContext().getFuncInfo().getFuncPermissionPkorgs());
/*     */     
/*     */ 
/* 116 */     orgPane.getRefModel().setFilterPks(pkorgs);
/*     */   }
/*     */   
/*     */   protected void setDefaultValue()
/*     */   {
/* 121 */     String pk_org = getModel().getContext().getPk_org();
/* 122 */     String pk_group = WorkbenchEnvironment.getInstance().getGroupVO().getPrimaryKey();
/*     */     
/*     */ 
/* 125 */     this.billCardPanel.setHeadItem("pk_org", pk_org);
/*     */     
/* 127 */     this.billCardPanel.setHeadItem("a.pk_org", pk_org);
/*     */     
/* 129 */     this.billCardPanel.setHeadItem("a.pk_group", pk_group);
/*     */     
/* 131 */     this.billCardPanel.setHeadItem("pk_group", pk_group);
/*     */     
/* 133 */     this.billCardPanel.setHeadItem("a.accopendate", WorkbenchEnvironment.getServerTime().getDate());
/*     */     
/*     */ 
/* 136 */     if (!StringUtils.isBlank(getModel().getContext().getPk_org()))
/*     */     {
/* 138 */       setRequestFocus(false);
/* 139 */       SwingUtilities.invokeLater(new Runnable()
/*     */       {
/*     */         public void run() {
/* 142 */           BillItem psndoc = PsnBankaccEditor.this.getBillCardPanel().getHeadItem("pk_psndoc");
/* 143 */           psndoc.setEnabled(true);
/* 144 */           psndoc.getComponent().requestFocus(true);
/* 145 */           PsnBankaccEditor.this.setRequestFocus(true);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void onAdd()
/*     */   {
/* 155 */     super.onAdd();
/*     */     
/* 157 */     showMeUp();
/*     */     
/*     */ 
/* 160 */     UIRefPane psndocRefPane = (UIRefPane)this.billCardPanel.getHeadItem("pk_psndoc").getComponent();
/*     */     
/* 162 */     if (((PsnBankaccAppModel)getModel()).isPsndoc()) {
/* 163 */       psndocRefPane.setRefNodeName("人员");
/*     */     }
/*     */     else {
/* 166 */       psndocRefPane.setRefNodeName("HR相关人员");
/*     */     }
             ((PsndocDefaultRefModel)psndocRefPane.getRefModel()).setLeavePowerUI(true);
             ((PsndocDefaultRefModel)psndocRefPane.getRefModel()).setLeavePower(true);
/* 168 */     setBankaccNumEdit(true);
/* 169 */     setRefitemsWhenAdd();
/*     */     
/* 171 */     setAccnumRefVisible();
/*     */     
/* 173 */     boolean isAutoAddLine = this.billCardPanel.getBodyPanel().isAutoAddLine();
/* 174 */     this.billCardPanel.setBodyAutoAddLine(false);
/* 175 */     this.billCardPanel.addLine();
/* 176 */     this.billCardPanel.setBodyAutoAddLine(isAutoAddLine);
/*     */   }
/*     */   
/*     */   protected void onNotEdit()
/*     */   {
/* 181 */     super.onNotEdit();
/*     */     
/*     */ 
/* 184 */     resetPsnRefWherePart();
/*     */   }
/*     */   
/*     */   private void setRefitemsWhenAdd() {
/* 188 */     String pk_org = (String)getBillCardPanel().getHeadItem("pk_org").getValueObject();
/*     */     
/* 190 */     if (!StringUtils.isBlank(pk_org)) {
/* 191 */       setRefwhereByOrg(pk_org);
/*     */     }
/*     */     
/* 194 */     UIRefPane bankdocRef = (UIRefPane)this.billCardPanel.getHeadItem("a.pk_bankdoc").getComponent();
/*     */     
/*     */ 
/* 197 */     ((BankDocDefaultRefTreeModel)bankdocRef.getRefModel()).setClassWherePart(BanktypeCodeConst.BANKTYPEREFWHEREPART);
/*     */     
/*     */ 
/*     */ 
/* 201 */     UIRefPane banktypeRef = (UIRefPane)this.billCardPanel.getHeadItem("a.pk_banktype").getComponent();
/*     */     
/*     */ 
/* 204 */     banktypeRef.setWhereString("(" + BanktypeCodeConst.BANKTYPEREFWHEREPART + ")");
/*     */   }
/*     */   
/*     */   private void setRefwhereByOrg(String pk_org)
/*     */   {
/* 209 */     UIRefPane psndocRef = (UIRefPane)this.billCardPanel.getHeadItem("pk_psndoc").getComponent();
/*     */     
/* 211 */     psndocRef.getRefModel().setPk_org(pk_org);
/* 212 */     if (psndocRef.getRefNodeName().equals("人员")) {
/* 213 */       ((PsndocDefaultRefModel)psndocRef.getRefModel()).setWherePart("bd_psnjob.pk_org='" + pk_org + "'");
/*     */       
/*     */ 
/* 216 */       ((PsndocDefaultRefModel)psndocRef.getRefModel()).setMatchPkWithWherePart(true);
/*     */     }
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
/*     */   private void resetPsnRefWherePart()
/*     */   {
/* 230 */     UIRefPane psndocRef = (UIRefPane)this.billCardPanel.getHeadItem("pk_psndoc").getComponent();
/*     */     
/*     */ 
/* 233 */     if (psndocRef.getRefNodeName().equals("人员")) {
/* 234 */       ((PsndocDefaultRefModel)psndocRef.getRefModel()).setWherePart(null);
/* 235 */       ((PsndocDefaultRefModel)psndocRef.getRefModel()).setMatchPkWithWherePart(false);
/*     */     }
/*     */     
/*     */ 
/* 239 */     if (!((PsnBankaccAppModel)getModel()).isPsndoc()) {
/* 240 */       psndocRef.setRefNodeName("人员");
/*     */     }
/*     */   }
/*     */   
/*     */   protected void onEdit()
/*     */   {
/* 246 */     super.onEdit();
/* 247 */     showMeUp();
/* 248 */     setRefitemsWhenEdit();
/* 249 */     this.billCardPanel.getHeadItem("pk_org").setEnabled(false);
/* 250 */     setRefwhereByOrg(this.billCardPanel.getHeadItem("pk_org").getValue());
/* 251 */     if (1 != ((PsnBankaccUnionVO)getModel().getSelectedData()).getBankaccbasVO().getEnablestate().intValue())
/*     */     {
/* 253 */       this.billCardPanel.getHeadItem("a.accnum").setEnabled(false);
/* 254 */       this.billCardPanel.getHeadItem("pk_psndoc").setEnabled(false);
/*     */     }
/* 256 */     setAccnumRefVisible();
/* 257 */     setBankaccNumEdit(false);
/*     */   }
/*     */   
/*     */   private void setRefitemsWhenEdit() {
/* 261 */     PsnBankaccUnionVO uninVO = (PsnBankaccUnionVO)getModel().getSelectedData();
/* 262 */     String pk_banktype = uninVO.getBankaccbasVO().getPk_banktype();
/* 263 */     if (pk_banktype != null) {
/* 264 */       UIRefPane netBankTemplet = (UIRefPane)this.billCardPanel.getHeadItem("a.pk_netbankinftp").getComponent();
/*     */       
/*     */ 
/* 267 */       netBankTemplet.setWhereString("pk_banktype='" + pk_banktype + "'");
/*     */     }
/*     */     
/*     */ 
/* 271 */     UIRefPane bankdocRef = (UIRefPane)this.billCardPanel.getHeadItem("a.pk_bankdoc").getComponent();
/*     */     
/*     */ 
/* 274 */     ((BankDocDefaultRefTreeModel)bankdocRef.getRefModel()).setClassWherePart(BanktypeCodeConst.BANKTYPEREFWHEREPART);
/*     */     
/*     */ 
/*     */ 
/* 278 */     UIRefPane banktypeRef = (UIRefPane)this.billCardPanel.getHeadItem("a.pk_banktype").getComponent();
/*     */     
/*     */ 
/* 281 */     banktypeRef.setWhereString("(" + BanktypeCodeConst.BANKTYPEREFWHEREPART + ")");
/*     */   }
/*     */   
/*     */ 
/*     */   public void handleEvent(AppEvent event)
/*     */   {
/* 287 */     super.handleEvent(event);
/* 288 */     if (event.getType().equals("Show_Editor")) {
/* 289 */       showMeUp();
/*     */     }
/*     */   }
/*     */   
/*     */   private void setBankaccNumEdit(boolean isAdd) {
/* 294 */     if (isAdd) {
/* 295 */       this.billCardPanel.getHeadItem("a.accnum").setEdit(true);
/*     */     }
/*     */     else {
/* 298 */       int enablestate = ((Integer)this.billCardPanel.getHeadItem("a.enablestate").getValueObject()).intValue();
/*     */       
/* 300 */       if (1 != enablestate) {
/* 301 */         this.billCardPanel.getHeadItem("a.accnum").setEdit(false);
/*     */       }
/*     */       else {
/* 304 */         this.billCardPanel.getHeadItem("a.accnum").setEdit(true);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void setAutoShowUpEventListener(IAutoShowUpEventListener l) {
/* 310 */     this.autoShowUpComponent.setAutoShowUpEventListener(l);
/*     */   }
/*     */   
/*     */   public void showMeUp()
/*     */   {
/* 315 */     PsnBankaccAppModel appModel = (PsnBankaccAppModel)getModel();
/* 316 */     appModel.setViewType("editor");
/* 317 */     this.autoShowUpComponent.showMeUp();
/*     */   }
/*     */   
/*     */ 
/*     */   public void addTabbedPaneAwareComponentListener(ITabbedPaneAwareComponentListener l)
/*     */   {
/* 323 */     this.tabbedPaneAwareComponent.addTabbedPaneAwareComponentListener(l);
/*     */   }
/*     */   
/*     */   public boolean canBeHidden()
/*     */   {
/* 328 */     if (this.closingListener != null) {
/* 329 */       return this.closingListener.canBeClosed();
/*     */     }
/* 331 */     return this.tabbedPaneAwareComponent.canBeHidden();
/*     */   }
/*     */   
/*     */   public boolean isComponentVisible()
/*     */   {
/* 336 */     return this.tabbedPaneAwareComponent.isComponentVisible();
/*     */   }
/*     */   
/*     */   public void setComponentVisible(boolean visible)
/*     */   {
/* 341 */     this.tabbedPaneAwareComponent.setComponentVisible(visible);
/*     */   }
/*     */   
/*     */   public void afterEdit(BillEditEvent e)
/*     */   {
/* 346 */     if (e.getKey().equals("pk_org"))
/*     */     {
/* 348 */       String pk_org = (String)this.billCardPanel.getHeadItem("pk_org").getValueObject();
/*     */       
/* 350 */       if (!StringUtils.isBlank(pk_org)) {
/* 351 */         setRefwhereByOrg(pk_org);
/*     */         
/* 353 */         UIRefPane bankdocRef = (UIRefPane)this.billCardPanel.getHeadItem("a.pk_bankdoc").getComponent();
/*     */         
/*     */ 
/* 356 */         bankdocRef.setPk_org(pk_org);
/* 357 */         if (!StringUtils.isBlank((String)this.map.get(pk_org))) {
/* 358 */           this.billCardPanel.getBillModel().setValueAt(this.map.get(pk_org), 0, "pk_currtype_ID");
/*     */           
/* 360 */           this.billCardPanel.getBillModel().loadLoadRelationItemValue(0, "pk_currtype");
/*     */         }
/*     */         else
/*     */         {
/*     */           try {
/* 365 */             OrgVO vo = (OrgVO)getQueryBS().retrieveByPK(OrgVO.class, pk_org, new String[] { "pk_currtype" });
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 370 */             if ((vo != null) && (vo.getPk_currtype() != null)) {
/* 371 */               this.billCardPanel.getBillModel().setValueAt(vo.getPk_currtype(), 0, "pk_currtype_ID");
/*     */               
/* 373 */               this.billCardPanel.getBillModel().loadLoadRelationItemValue(0, "pk_currtype");
/*     */             }
/*     */           }
/*     */           catch (BusinessException ex)
/*     */           {
/* 378 */             Logger.error(ex);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 384 */         String pk_psndoc = (String)this.billCardPanel.getHeadItem("pk_psndoc").getValueObject();
/*     */         
/*     */ 
/* 387 */         String psndoc_name = this.billCardPanel.getHeadItem("pk_psndoc.name").getValue();
/*     */         
/* 389 */         if (!StringUtils.isBlank(pk_psndoc)) {
/* 390 */           this.billCardPanel.getHeadItem("pk_psndoc").setValue(null);
/*     */         }
/* 392 */         if (!StringUtils.isBlank(psndoc_name)) {
/* 393 */           this.billCardPanel.getHeadItem("pk_psndoc.name").setValue(null);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 399 */       if (getBillCardPanel().getBillData().isImporting()) {
/* 400 */         UIRefPane psndocRef = (UIRefPane)this.billCardPanel.getHeadItem("pk_psndoc").getComponent();
/*     */         
/* 402 */         PsndocDefaultRefModel psnRefModel = (PsndocDefaultRefModel)psndocRef.getRefModel();
/*     */         
/* 404 */         psnRefModel.setPk_org(pk_org);
/* 405 */         String classWhere = " org_dept.pk_org='" + pk_org + "'";
/* 406 */         psnRefModel.setClassWherePart(classWhere);
/* 407 */         psnRefModel.setMatchPkWithWherePart(true);
/* 408 */         ((PsndocDefaultRefModel)psndocRef.getRefModel()).setWherePart("bd_psnjob.pk_org='" + pk_org + "'");
/*     */       }
/*     */       
/*     */ 
/*     */     }
/* 413 */     else if ("a.pk_bankdoc".equals(e.getKey())) {
/* 414 */       setAccnumRefVisible();
/* 415 */       Object bankdocObj = this.billCardPanel.getHeadItem("a.pk_bankdoc").getValueObject();
/*     */       
/* 417 */       UIRefPane bankdocRef = (UIRefPane)this.billCardPanel.getHeadItem("a.pk_bankdoc").getComponent();
/*     */       
/*     */ 
/* 420 */       BillItem banktypeItem = this.billCardPanel.getHeadItem("a.pk_banktype");
/* 421 */       if (bankdocObj != null) {
/* 422 */         Object pk_banktype = bankdocRef.getRefValue("pk_banktype");
/* 423 */         banktypeItem.setValue(pk_banktype);
/* 424 */         banktypeItem.setEnabled(false);
/*     */         try
/*     */         {
/* 427 */           BankdocVO bankdocVO = getBankDocService().getBankdocVOByPk(bankdocObj.toString());
/*     */           
/* 429 */           if (bankdocVO != null) {
/* 430 */             this.billCardPanel.getHeadItem("a.areacode").setValue(bankdocVO.getAreacode());
/*     */             
/* 432 */             this.billCardPanel.getHeadItem("a.combinenum").setValue(bankdocVO.getCombinenum());
/*     */             
/* 434 */             this.billCardPanel.getHeadItem("a.orgnumber").setValue(bankdocVO.getOrgnumber());
/*     */             
/* 436 */             this.billCardPanel.getHeadItem("a.bankarea").setValue(bankdocVO.getBankarea());
/*     */             
/* 438 */             this.billCardPanel.getHeadItem("a.province").setValue(bankdocVO.getProvince());
/*     */             
/* 440 */             this.billCardPanel.getHeadItem("a.city").setValue(bankdocVO.getCity());
/*     */             
/* 442 */             this.billCardPanel.getHeadItem("a.customernumber").setValue(bankdocVO.getCustomernumber());
/*     */             
/* 444 */             this.billCardPanel.getHeadItem("a.issigned").setValue(bankdocVO.getIssigned());
/*     */           }
/*     */         }
/*     */         catch (BusinessException be)
/*     */         {
/* 449 */           Logger.error(be.getMessage(), be);
/*     */         }
/*     */       }
/*     */       else {
/* 453 */         banktypeItem.setValue(null);
/* 454 */         banktypeItem.setEnabled(true);
/*     */         
/* 456 */         this.billCardPanel.getHeadItem("a.areacode").setValue(null);
/* 457 */         this.billCardPanel.getHeadItem("a.combinenum").setValue(null);
/* 458 */         this.billCardPanel.getHeadItem("a.orgnumber").setValue(null);
/* 459 */         this.billCardPanel.getHeadItem("a.bankarea").setValue(null);
/* 460 */         this.billCardPanel.getHeadItem("a.province").setValue(null);
/* 461 */         this.billCardPanel.getHeadItem("a.city").setValue(null);
/* 462 */         this.billCardPanel.getHeadItem("a.customernumber").setValue(null);
/* 463 */         this.billCardPanel.getHeadItem("a.issigned").setValue(null);
/*     */       }
/*     */       
/*     */     }
/* 467 */     else if ("a.pk_banktype".equals(e.getKey())) {
/* 468 */       BillItem bankdocItem = getBillCardPanel().getHeadTailItem("a.pk_bankdoc");
/* 469 */       BillItem banknetItem = getBillCardPanel().getHeadTailItem("a.pk_netbankinftp");
/*     */       
/* 471 */       Object banktypeObj = this.billCardPanel.getHeadItem("a.pk_banktype").getValueObject();
/*     */       
/* 473 */       UIRefPane bankdocRef = (UIRefPane)bankdocItem.getComponent();
/* 474 */       UIRefPane netBankRef = (UIRefPane)banknetItem.getComponent();
/* 475 */       if (banktypeObj != null) {
/* 476 */         String pk_banktype = banktypeObj.toString();
/* 477 */         banknetItem.setValue(null);
/* 478 */         bankdocItem.setValue(null);
/* 479 */         ((BankDocDefaultRefTreeModel)bankdocRef.getRefModel()).setClassWherePart(" bd_banktype.pk_banktype='" + pk_banktype + "'");
/*     */         
/* 481 */         ((NetbankTemplateDefaultRefModel)netBankRef.getRefModel()).setWherePart("bd_netbankinftp.pk_banktype='" + pk_banktype + "'");
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 486 */         ((BankDocDefaultRefTreeModel)bankdocRef.getRefModel()).setClassWherePart(null);
/*     */         
/* 488 */         ((NetbankTemplateDefaultRefModel)netBankRef.getRefModel()).setWherePart(null);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void bodyRowChange(BillEditEvent e) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public IFunNodeClosingListener getClosingListener()
/*     */   {
/* 501 */     return this.closingListener;
/*     */   }
/*     */   
/*     */   public void setClosingListener(IFunNodeClosingListener closingListener) {
/* 505 */     this.closingListener = closingListener;
/*     */   }
/*     */   
/*     */   private IBankdocQueryService getBankDocService() {
/* 509 */     if (null == this.bankDocQueryService) {
/* 510 */       this.bankDocQueryService = ((IBankdocQueryService)NCLocator.getInstance().lookup(IBankdocQueryService.class));
/*     */     }
/* 512 */     return this.bankDocQueryService;
/*     */   }
/*     */   
/*     */   private IUAPQueryBS getQueryBS() {
/* 516 */     if (this.queryService == null)
/* 517 */       this.queryService = ((IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class));
/* 518 */     return this.queryService;
/*     */   }
/*     */   
/*     */   public List<Action> getActions()
/*     */   {
/* 523 */     return this.actions;
/*     */   }
/*     */   
/*     */   public void setActions(List<Action> actions) {
/* 527 */     this.actions = actions;
/*     */   }
/*     */   
/*     */   private BankaccnumRefPane getIbanRefPane() {
/* 531 */     if (this.ibanRefPane == null) {
/* 532 */       this.ibanRefPane = new BankaccnumRefPane(this, "a.pk_bankdoc");
/*     */       
/* 534 */       this.ibanRefPane.setFuncode(getModel().getContext().getFuncInfo().getFuncode());
/*     */     }
/*     */     
/* 537 */     return this.ibanRefPane;
/*     */   }
/*     */   
/*     */   private void setAccnumRefVisible() {
/* 541 */     IbanUtil.setAccnumRefVisible(this, "a.accnum", "a.pk_bankdoc");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void beforeGetValue()
/*     */   {
/* 549 */     super.beforeGetValue();
/*     */     try {
/* 551 */       this.billCardPanel.dataNotNullValidate();
/*     */     }
/*     */     catch (ValidationException e) {
/* 554 */       throw new BusinessExceptionAdapter(e);
/*     */     }
/*     */   }
/*     */ }

