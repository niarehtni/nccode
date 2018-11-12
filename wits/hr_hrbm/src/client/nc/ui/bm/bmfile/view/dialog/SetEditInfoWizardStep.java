/**
 * @(#)SetEditInfoWizardStep.java 1.0 2018��1��17��
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.ui.bm.bmfile.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.bm.pub.BMDelegator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.ui.bm.rule.ref.BmRuleRefmodel;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.UITabbedPane;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pub.beans.wizard.WizardStepException;
import nc.ui.pub.beans.wizard.WizardStepValidateException;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.vo.bm.bmclass.BmClassVO;
import nc.vo.bm.data.BmDataVO;
import nc.vo.bm.pub.BmLoginContext;
import nc.vo.bm.pub.DefRefUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFLiteralDate;


import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author niehg
 * @since 6.3
 */
public class SetEditInfoWizardStep extends WizardStep implements IWizardStepListener {
	private UITabbedPane tabbedPanel;
	//private BillCardPanel classPanel = null;
	private BillCardPanel[] classPanel = null;
	private BmLoginContext loginContext;
	private BmClassVO[] classVOs = null;
	public BmLoginContext getLoginContext() {
		return loginContext;
	}
	public BmClassVO[] getClassVOs() {
		return classVOs;
	}

	public void setClassVOs(BmClassVO[] classVOs) {
		this.classVOs = classVOs;
	}
	public SetEditInfoWizardStep(BmLoginContext context) {
		super();
		setTitle(ResHelper.getString("60150bmfile", "060150bmfile0075"));
		setDescription(ResHelper.getString("60150bmfile", "060150bmfile0036")
		/* @res "������Ϣ" */);
		this.loginContext = context;
		try {
			classVOs = BMDelegator.getBmfileQueryService().queryBmClass(getBmLoginContext(), false, true);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		setComp(getScrollPanel());
		addListener(this);
	}

	private UIScrollPane scrollPanel;
	private UIRefPane agentcompRef;
	private UIRefPane compVenderRef;
	private UIRefPane psnVenderRef;
	
	// Ϊ����籣�������������򵼽����������������Ϣ����ʾ��������ҳǩ���������
	// ��Ҫ��Ϊҳǩ��ʾ������
	BmClassVO[] showBmClassVOs = null;

	public UIScrollPane getScrollPanel() {
		if (null == scrollPanel) {
			UIPanel headPanel = new UIPanel();
			headPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			UILabel lebel = new UILabel(ResHelper.getString("60150bmfile", "160150bmfile0031")); // �籣����˾
			headPanel.add(lebel);
			headPanel.add(getAgentcompRef());

			UIPanel bodyPanel = new UIPanel();
			bodyPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			UILabel comLebel = new UILabel(ResHelper.getString("60150bmfile", "160150bmfile0032")); // �籣��Ӧ�̴���(��˾)
			UILabel psnLebel = new UILabel(ResHelper.getString("60150bmfile", "160150bmfile0033")); // �籣��Ӧ�̴���(����)
			bodyPanel.add(comLebel);
			bodyPanel.add(getCompVenderRef());
			bodyPanel.add(psnLebel);
			bodyPanel.add(getPsnVenderRef());

			UIPanel panel = new UIPanel();
			panel.setLayout(new BorderLayout());
			panel.add(headPanel, BorderLayout.NORTH);
			//panel.add(bodyPanel, BorderLayout.CENTER);
			panel.add(getUICompont(), BorderLayout.CENTER);

			scrollPanel = new UIScrollPane(panel);
		}
		return scrollPanel;
	}

	public UIRefPane getAgentcompRef() {
		if (null == agentcompRef) {
			agentcompRef = new UIRefPane();
			agentcompRef.setRefNodeName(DefRefUtils.qryRefNameByDefCode(DefRefUtils.DEF_AGENTCOMP)); // "�籣����˾(�Զ��嵵��)"
			agentcompRef.setCacheEnabled(false);
			agentcompRef.setPreferredSize(new Dimension(120, 25));
		}
		return agentcompRef;
	}

	public UIRefPane getCompVenderRef() {
		if (null == compVenderRef) {
			compVenderRef = new UIRefPane();
			compVenderRef.setRefNodeName(DefRefUtils.qryRefNameByDefCode(DefRefUtils.DEF_VENDERCODE)); // "�籣��Ӧ�̴���(�Զ��嵵��)"
			compVenderRef.setCacheEnabled(false);
			compVenderRef.setPreferredSize(new Dimension(120, 25));
		}
		return compVenderRef;
	}

	public UIRefPane getPsnVenderRef() {
		if (null == psnVenderRef) {
			psnVenderRef = new UIRefPane();
			psnVenderRef.setRefNodeName(DefRefUtils.qryRefNameByDefCode(DefRefUtils.DEF_VENDERCODE)); // "�籣��Ӧ�̴���(�Զ��嵵��)"
			psnVenderRef.setCacheEnabled(false);
			psnVenderRef.setPreferredSize(new Dimension(120, 25));
		}
		return psnVenderRef;
	}
	
	// {�籣���������������޸��籣����˾�������籣��Ӧ�̴��루��˾���������籣��Ӧ�̴��루���ˣ�} kevin.nie 2018-01-17 end
	public UITabbedPane getUICompont() {
		if (tabbedPanel == null) {
			tabbedPanel = new UITabbedPane();
		}
		return tabbedPanel;
	}
	//by hepingyang ������Ϣ�������ģ��
	@Override
	public void stepActived(WizardStepEvent paramWizardStepEvent) throws WizardStepException {
		//getUICompont().removeAll();
		//getUICompont().addTab("�޸�",getClassPanel());
		// Ϊ����籣�������������򵼽����������������Ϣ����ʾ��������ҳǩ�������޸�
		
		showBmClassVOs = getShowBmClassVOs();
		classPanel = new BillCardPanel[classVOs.length];
		getUICompont().removeAll();

		for (int i = 0; i < classVOs.length; i++) {// 2014/07/18�޸���ѡ���ֽɽ��ز��մ���
													// zhoumxc
			for (int j = 0; j < showBmClassVOs.length; j++) {
				if (classVOs[i].getMultilangName().equals(showBmClassVOs[j].getMultilangName())) {
					getUICompont().addTab(classVOs[i].getMultilangName(), getClassPanel(i, classVOs[i].getPk_bm_class()));
				}
			}
		}
		
	}
	
	private BillCardPanel getClassPanel(int index, String name) {
		
		classPanel[index] = new BillCardPanel();
		classPanel[index].setBillType(getBmLoginContext().getNodeCode());
		classPanel[index].setBusiType(null);
		classPanel[index].setOperator(getBmLoginContext().getPk_loginUser());
		classPanel[index].setCorp(getBmLoginContext().getPk_org());
		classPanel[index].setName(name);
		BillTempletVO template = classPanel[index].getDefaultTemplet(classPanel[index].getBillType(), null,
				classPanel[index].getOperator(), classPanel[index].getCorp(), "bmfileba", null);

		if (template == null) {
			Logger.error("û���ҵ�nodekey��bmfileba ��Ӧ�Ŀ�Ƭģ��");
			throw new IllegalArgumentException(ResHelper.getString("60150bmfile", "060150bmfile0037")
			/* @res "û���ҵ����õĵ���ģ����Ϣ" */);
		}

		classPanel[index].setBillData(new BillData(template));
		classPanel[index].setPreferredSize(new Dimension(600, 400));
		UIRefPane panel = (UIRefPane) classPanel[index].getHeadItem(BmDataVO.PAYLOCATION).getComponent();
		BmRuleRefmodel refModel = (BmRuleRefmodel) panel.getRefModel();
		refModel.setPk_org(getBmLoginContext().getPk_org());
		refModel.setPk_bm_class(classVOs[index].getPk_bm_class());
		refModel.setEndperiod(classVOs[index].getCyear() + classVOs[index].getCperiod());
		return classPanel[index];
	}
	
	@Override
	public void stepDisactived(WizardStepEvent paramWizardStepEvent) throws WizardStepException {

		ArrayList<BmDataVO> list = new ArrayList<BmDataVO>();
		for (int i = 0; i < classPanel.length; i++) {
			if (classPanel[i] != null) {
				BmDataVO bmdata = (BmDataVO) classPanel[i].getBillData().getHeaderValueVO(BmDataVO.class.getName());
				bmdata.setPk_bm_class(classPanel[i].getName());
				list.add(bmdata);
			}
		}
		// MOD {�籣���������������޸��籣����˾�������籣��Ӧ�̴��루��˾���������籣��Ӧ�̴��루���ˣ�} kevin.nie
		// 2018-01-17
		// start
		String pk_agentcomp = getHeadBmOfAgentComp();
		BmDataVO[] dataVOs = (BmDataVO[]) getModel().getAttr("selectedBmData");
		//ͨ��pk_bm_dataȥ���ݿ��ѯdef1��def2��def3
        IUAPQueryBS iUAPQueryBS = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());   
     	   //��ѯ���е�bmdata
        List<Map<String,String>> custlist = null;
			try {
				custlist = (List<Map<String,String>>) iUAPQueryBS. executeQuery("select * from bm_data " +
						"where pk_org='"+getBmLoginContext().getPk_org()+"'" +
						"and cyear='"+getBmLoginContext().getCyear()+"' and cperiod='"+getBmLoginContext().getCperiod()+"'",new MapListProcessor());
			} catch (BusinessException e) {
				e.printStackTrace();
			}
	
		for(int i =0; i<dataVOs.length; i++){
			for(Map<String,String> datavo : custlist){
				if(datavo.get("pk_bm_data").equals(dataVOs[i].getPk_bm_data())){
					dataVOs[i].setDef1(datavo.get("def1"));
					dataVOs[i].setDef2(datavo.get("def2"));
					dataVOs[i].setDef3(datavo.get("def3"));
					if(null != datavo.get("dbegindate")){
						dataVOs[i].setDbegindate(new UFLiteralDate(datavo.get("dbegindate")));
					}
					dataVOs[i].setPaylocation(datavo.get("paylocation"));
				}
			}
		}
		
		//���޸ĵ��ֶθ���vo
		for(int i = 0; i<dataVOs.length; i++){
			for(BmDataVO bmdata: list){
				if(dataVOs[i].getPk_bm_class().equals(bmdata.getPk_bm_class())){
					//�籣����˾
					if(!StringUtils.isEmpty(pk_agentcomp)){
						dataVOs[i].setDef1(pk_agentcomp);
					}
					//
					if(!StringUtils.isEmpty(bmdata.getDef2())){
						dataVOs[i].setDef2(bmdata.getDef2());
					}
					//
					if(!StringUtils.isEmpty(bmdata.getDef3())){
						dataVOs[i].setDef3(bmdata.getDef3());
					}
					//
					if(null != bmdata.getDbegindate()){
						dataVOs[i].setDbegindate(bmdata.getDbegindate());
					}
					//
					if(!StringUtils.isEmpty(bmdata.getPaylocation())){
						dataVOs[i].setPaylocation(bmdata.getPaylocation());
					}
				}
			}
		}

		
		getModel().putAttr("selectedPsn", dataVOs);
	}
	public BillCardPanel getBillCardPanel() {
		return (BillCardPanel) getUICompont().getComponent(0);
	}

	private BmLoginContext getBmLoginContext() {
		return loginContext;
	}
	public String getHeadBmOfAgentComp() {
		return getAgentcompRef().getRefPK();
	}
	/**
	 * 2018/09/01 hepy ��ȡѡ�е���Ա��Ϣ
	 * 
	 * @return BmClassVO[]
	 */
	private BmClassVO[] getShowBmClassVOs() {
		ArrayList<BmClassVO> showBmClassList = new ArrayList<BmClassVO>();
		//BmDataVO[] psnVOs = (BmDataVO[]) getModel().getAttr("selectedBmData");
		BmDataVO[] dataVOs = (BmDataVO[]) getModel().getAttr("selectedBmData");
		if (!ArrayUtils.isEmpty(dataVOs) && !ArrayUtils.isEmpty(this.classVOs)) {
			HashMap<BmClassVO, Object> selectedBmClassMap = new HashMap<BmClassVO, Object>();
			for (int i = 0; i < classVOs.length; i++) {
				String pk_bmclass = classVOs[i].getPk_bm_class();
				for (int j = 0; j < dataVOs.length; j++) {
					if(dataVOs[j].getPk_bm_class().equals(pk_bmclass)){
						selectedBmClassMap.put(classVOs[i], classVOs[i].getName());
					}
					
				}
			}
			for(BmClassVO key : selectedBmClassMap.keySet()){
				showBmClassList.add(key);
			}
		}
		return showBmClassList.toArray(new BmClassVO[0]);
	}
	/*public void validate() throws WizardStepValidateException {
		super.validate();
		String agentComPk = getAgentcompRef().getRefPK();
		String comVenderPK = getCompVenderRef().getRefPK();
		String psnVenderPK = getPsnVenderRef().getRefPK();
		if (StringUtils.isBlank(agentComPk)) {
			WizardStepValidateException e = new WizardStepValidateException();
			e.addMsg(ResHelper.getString("60150bmfile", "060150bmfile0014"),
					ResHelper.getString("60150bmfile", "060150bmfile0078"));
			throw e;
		}
		if (StringUtils.isBlank(comVenderPK)) {
			WizardStepValidateException e = new WizardStepValidateException();
			e.addMsg(ResHelper.getString("60150bmfile", "060150bmfile0014"),
					ResHelper.getString("60150bmfile", "060150bmfile0079"));
			throw e;
		}
		if (StringUtils.isBlank(psnVenderPK)) {
			WizardStepValidateException e = new WizardStepValidateException();
			e.addMsg(ResHelper.getString("60150bmfile", "060150bmfile0014"),
					ResHelper.getString("60150bmfile", "060150bmfile0080"));
			throw e;
		}
	}*/
}
