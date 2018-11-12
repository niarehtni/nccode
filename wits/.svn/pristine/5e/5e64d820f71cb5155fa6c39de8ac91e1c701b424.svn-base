/*    */ package nc.ui.bd.psn.psndoc.ref.busi;
/*    */ 
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import nc.ui.bd.ref.UFRefGridTreeCommDataUI;
/*    */ import nc.ui.bd.ref.model.PsndocDefaultRefModel;
/*    */ import nc.ui.pub.beans.UICheckBox;
/*    */ import nc.ui.pub.beans.UIPanel;
/*    */ import nc.vo.ml.AbstractNCLangRes;
/*    */ import nc.vo.ml.NCLangRes4VoTransl;
/*    */ import net.miginfocom.swing.MigLayout;
/*    */ 
/*    */ public class PsndocRefModelWithLeavedSelectedPanel extends UIPanel implements ActionListener
/*    */ {
/*    */   private static final long serialVersionUID = -543942756825370068L;
/* 16 */   private UICheckBox isLeavedCheckBox = null;
/* 17 */   private UFRefGridTreeCommDataUI refDialog = null;
/*    */   
/*    */   public PsndocRefModelWithLeavedSelectedPanel(UFRefGridTreeCommDataUI refDialog) {
/* 20 */     this.refDialog = refDialog;
/* 21 */     initialize();
/*    */   }
/*    */   
/*    */   private void initialize() {
/* 25 */     setLayout(new MigLayout("insets 1", "", ""));
/* 26 */     add(getIsLeavedCheckBox());
/* 27 */     initListener();
/* 28 */     //getRefModel().setLeavePower(false);
/* 29 */     getRefModel().setDisabledDataShow(false);
/* 30 */     getRefModel().reset();
/*    */   }
/*    */   
/*    */   private void initListener() {
/* 34 */     getIsLeavedCheckBox().addActionListener(this);
/*    */   }
/*    */   
/*    */   private UICheckBox getIsLeavedCheckBox() {
/* 38 */     if (this.isLeavedCheckBox == null) {
/* 39 */       this.isLeavedCheckBox = new UICheckBox();
/* 40 */       this.isLeavedCheckBox.setName("showLeavedPerson");
/* 41 */       this.isLeavedCheckBox.setText(NCLangRes4VoTransl.getNCLangRes().getStrByID("10140psn", "110140psn0041"));
/*    */     }
/*    */     
/* 44 */     return this.isLeavedCheckBox;
/*    */   }
/*    */   
/*    */   private PsndocDefaultRefModel getRefModel() {
/* 48 */     return (PsndocDefaultRefModel)this.refDialog.getRefModel();
/*    */   }
/*    */   
/*    */   public void actionPerformed(ActionEvent e) {
/* 52 */     UICheckBox checkBox = ((PsndocDefaultRefModelDlg)this.refDialog).getChkSealedDataShow();
/* 53 */     if (getIsLeavedCheckBox().isSelected()) {
/* 54 */       getRefModel().setLeavePower(true);
/* 55 */       if (checkBox != null) {
/* 56 */         checkBox.setEnabled(false);
/* 57 */         checkBox.setSelected(true);
/* 58 */         getRefModel().setDisabledDataShow(true);
/*    */       }
/*    */     } else {
/* 61 */       getRefModel().setLeavePower(false);
/* 62 */       getRefModel().setDisabledDataShow(false);
/* 63 */       if (checkBox != null) {
/* 64 */         checkBox.setEnabled(true);
/* 65 */         checkBox.setSelected(false);
/* 66 */         if (checkBox.isSelected()) {
/* 67 */           getRefModel().setDisabledDataShow(true);
/*    */         } else {
/* 69 */           getRefModel().setDisabledDataShow(false);
/*    */         }
/*    */       }
/*    */     }
/* 73 */     getRefModel().reset();
/*    */     
/* 75 */     this.refDialog.onRefresh();
/*    */   }
/*    */ }

