/**
 * @(#)BatchEditBmfileAction.java 1.0 2018年1月16日
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.ui.bm.bmfile.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;


import javax.swing.KeyStroke;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.bm.pub.BMDelegator;
import nc.ui.bm.bmfile.model.BmfileAppModel;
import nc.ui.bm.bmfile.view.dialog.SearchPsnWizardStep;
import nc.ui.bm.bmfile.view.dialog.SelectBmWizardStep;
import nc.ui.bm.bmfile.view.dialog.SetEditInfoWizardStep;
import nc.ui.pub.beans.wizard.WizardActionException;
import nc.ui.pub.beans.wizard.WizardDialog;
import nc.ui.pub.beans.wizard.WizardEvent;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.vo.bm.bmclass.BmClassVO;
import nc.vo.bm.data.BmDataVO;
import nc.vo.pub.BusinessException;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings({ "restriction", "serial" })
public class BatchEditBmfileAction extends BmfileWizardAction {

	public BatchEditBmfileAction() {
		setCode("BatchEdit");
		setBtnName(ResHelper.getString("60150bmfile", "060150bmfile0075"));
		putValue("AcceleratorKey", KeyStroke.getKeyStroke(69, 10));
		putValue("ShortDescription", ResHelper.getString("60150bmfile", "060150bmfile0076"));
	}
	@Override
	public void doAction(ActionEvent e) throws Exception {
		
		WizardDialog wizardDialog = new WizardDialog(getModel().getContext().getEntranceUI(), getWizardModel(),
				getSteps(), null);

		wizardDialog.setWizardDialogListener(this);
		wizardDialog.setResizable(true);
		wizardDialog.setSize(new Dimension(800, 560));
		if (1 != wizardDialog.showModal()) {
			setCancelMsg();
			return;
		}
	}

	@Override
	protected List<WizardStep> getSteps() {
		List<WizardStep> list = new ArrayList<WizardStep>();
		list.add(new SearchPsnWizardStep(getBtnName(), getModel()));
		list.add(new SelectBmWizardStep(getBmLoginContext()));
		list.add(new SetEditInfoWizardStep(getBmLoginContext()));
		return list;
	}
	//判断按钮是否可用 by hepingyang
     protected boolean isActionEnable()
     {
       BmDataVO[] bmdatavos = null;
      boolean flag = false;
       BmDataVO bmdatavo = (BmDataVO)getBmfileAppModel().getSelectedData();
       String pk_psndoc = null;
       if (null != bmdatavo) {
         pk_psndoc = bmdatavo.getPk_psndoc();
       } else {
       return false;
      }
      List<BmDataVO> bmdataListvos = getBmfileAppModel().getData();
       List<BmDataVO> selectbmdatalist = new ArrayList();
       for (BmDataVO bmdatavo1 : bmdataListvos) {
        if ((!StringUtils.isEmpty(pk_psndoc)) && (pk_psndoc.equals(bmdatavo1.getPk_psndoc()))) {
           selectbmdatalist.add(bmdatavo1);
        }
     }
    bmdatavos = (BmDataVO[])selectbmdatalist.toArray(new BmDataVO[0]);
       if (ArrayUtils.isEmpty(bmdatavos)) {
        return false;
     }
     for (BmDataVO vo : bmdatavos)
      {
         boolean flag2 = true;
	
	    BmClassVO classVO = null;
	        boolean isCurrentPeriod = false;
	         if ((vo.getAccountstate().intValue() == 1) || (vo.getAccountstate().intValue() == 2))
	      {
	          String pk_bm_class = vo.getPk_bm_class();
	         try {
	            classVO = ((BmfileAppModel)getModel()).queryBmClassVOByPK(pk_bm_class, vo.getCyear(), vo.getCperiod());
	         
	            if (classVO != null) {
	              isCurrentPeriod = (classVO.getCyear().equals(vo.getCyear())) && (classVO.getCperiod().equals(vo.getCperiod()));
	            }
	           }
	           catch (Exception e) {
	             Logger.error(e.getMessage());
	          }
	        }
	         if ((vo.getCheckflag().booleanValue()) || (((vo.getAccountstate().intValue() == 1) || (vo.getAccountstate().intValue() == 2)) && (!isCurrentPeriod)))
	         {
	          flag2 = false;
	       }
	        flag = (flag) || (flag2);
	        
	         if (flag) {
	           break;
	        }
	      }
	      if (!flag) {
	        return flag;
	      }
	       return super.isActionEnable();
	     }
	     
	     private BmfileAppModel getBmfileAppModel()
	        {
	        return (BmfileAppModel)getModel();
	      }

	@Override
	public void wizardFinish(WizardEvent event) throws WizardActionException {
		super.wizardFinish(event);
	}

	@Override
	public void doProcess(BmDataVO[] bmDataVOs) throws BusinessException {
		BMDelegator.getBmfileQueryService().batchEditBmData(bmDataVOs, getBmLoginContext());
	}

	public void checkDataPermission(BmDataVO[] psnVOs) throws BusinessException {

	}
}
