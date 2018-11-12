package nc.ui.bm.bmfile.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
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
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.vo.bm.bmclass.BmClassVO;
import nc.vo.bm.data.BmDataVO;
import nc.vo.bm.pub.BmLoginContext;
import nc.vo.bm.pub.DefRefUtils;
import nc.vo.pub.bill.BillTempletVO;

import org.apache.commons.lang.ArrayUtils;

public class SetClassInfoWizardStep extends WizardStep implements IWizardStepListener {

	private UITabbedPane tabbedPanel;

	private BmClassVO[] classVOs = null;

	private BillCardPanel[] classPanel = null;

	public BmClassVO[] getClassVOs() {
		return classVOs;
	}

	public void setClassVOs(BmClassVO[] classVOs) {
		this.classVOs = classVOs;
	}

	private BmLoginContext loginContext;

	// 2014/08/14 zhoumxc 补丁合并
	// 为解决社保档案批量新增向导界面第三步“设置信息”显示所有险种页签的问题添加
	// 需要作为页签显示的险种
	BmClassVO[] showBmClassVOs = null;

	public SetClassInfoWizardStep(BmLoginContext loginContext1, BmClassVO[] classVOs1) {
		super();
		setTitle(ResHelper.getString("60150bmfile", "060150bmfile0035")
		/* @res "批量新增" */);
		setDescription(ResHelper.getString("60150bmfile", "060150bmfile0036")
		/* @res "设置信息" */);
		loginContext = loginContext1;
		classVOs = classVOs1;
		// MOD {社保档案批量新增、修改社保代理公司、险种社保供应商代码（公司）、险种社保供应商代码（个人）} kevin.nie
		// 2018-01-17
		// start
		// setComp(getUICompont());
		setComp(getScrollPanel());
		// {社保档案批量新增、修改社保代理公司、险种社保供应商代码（公司）、险种社保供应商代码（个人）} kevin.nie 2018-01-17
		// end
		addListener(this);

	}

	// MOD {社保档案批量新增、修改社保代理公司、险种社保供应商代码（公司）、险种社保供应商代码（个人）} kevin.nie 2018-01-17
	// start
	private UIScrollPane scrollPanel;
	private UIRefPane agentcompRef;

	public UIScrollPane getScrollPanel() {
		if (null == scrollPanel) {
			UIPanel headPanel = new UIPanel();
			headPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			UILabel lebel = new UILabel(ResHelper.getString("60150bmfile", "160150bmfile0031")); // 社保代理公司
			headPanel.add(lebel);
			headPanel.add(getAgentcompRef());

			UIPanel panel = new UIPanel();
			panel.setLayout(new BorderLayout());
			panel.add(headPanel, BorderLayout.NORTH);
			panel.add(getUICompont(), BorderLayout.CENTER);

			scrollPanel = new UIScrollPane(panel);
		}
		return scrollPanel;
	}

	public UIRefPane getAgentcompRef() {
		if (null == agentcompRef) {
			agentcompRef = new UIRefPane();
			agentcompRef.setRefNodeName(DefRefUtils.qryRefNameByDefCode(DefRefUtils.DEF_AGENTCOMP)); // "社保代理公司(自定义档案)"
			agentcompRef.setCacheEnabled(false);
			agentcompRef.setPreferredSize(new Dimension(120, 25));
		}
		return agentcompRef;
	}

	public String getHeadBmOfAgentComp() {
		return getAgentcompRef().getRefPK();
	}

	// {社保档案批量新增、修改社保代理公司、险种社保供应商代码（公司）、险种社保供应商代码（个人）} kevin.nie 2018-01-17 end
	public UITabbedPane getUICompont() {
		if (tabbedPanel == null) {
			tabbedPanel = new UITabbedPane();
		}
		return tabbedPanel;
	}

	private BillCardPanel getClassPanel(int index) {
		classPanel[index] = new BillCardPanel();
		classPanel[index].setBillType(getBmLoginContext().getNodeCode());
		classPanel[index].setBusiType(null);
		classPanel[index].setOperator(getBmLoginContext().getPk_loginUser());
		classPanel[index].setCorp(getBmLoginContext().getPk_org());
		BillTempletVO template = classPanel[index].getDefaultTemplet(classPanel[index].getBillType(), null,
				classPanel[index].getOperator(), classPanel[index].getCorp(), "bmfileba", null);

		if (template == null) {
			Logger.error("没有找到nodekey：bmfileba 对应的卡片模板");
			throw new IllegalArgumentException(ResHelper.getString("60150bmfile", "060150bmfile0037")
			/* @res "没有找到设置的单据模板信息" */);
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
	public void stepActived(WizardStepEvent event) throws WizardStepException {

		// 2014/08/14 zhoumxc 补丁合并
		// 为解决社保档案批量新增向导界面第三步“设置信息”显示所有险种页签的问题修改
		showBmClassVOs = getShowBmClassVOs();
		classPanel = new BillCardPanel[classVOs.length];
		getUICompont().removeAll();

		for (int i = 0; i < classVOs.length; i++) {// 2014/07/18修改所选险种缴交地参照错误
													// zhoumxc
			for (int j = 0; j < showBmClassVOs.length; j++) {
				if (classVOs[i].getMultilangName().equals(showBmClassVOs[j].getMultilangName())) {
					getUICompont().addTab(classVOs[i].getMultilangName(), getClassPanel(i));
				}
			}
		}
	}

	/**
	 * 2014/08/14 zhoumxc 补丁合并 为解决社保档案批量新增向导界面第三步“设置信息”显示所有险种页签的问题， 新添加的方法。
	 * 
	 * @return BmClassVO[]
	 */
	private BmClassVO[] getShowBmClassVOs() {
		ArrayList<BmClassVO> showBmClassList = new ArrayList<BmClassVO>();
		BmDataVO[] psnVOs = (BmDataVO[]) getModel().getAttr("selectedPsn");
		if (!ArrayUtils.isEmpty(psnVOs) && !ArrayUtils.isEmpty(this.classVOs)) {
			for (int i = 0; i < classVOs.length; i++) {
				String pk_bmclass = classVOs[i].getPk_bm_class();
				for (int j = 0; j < psnVOs.length; j++) {
					HashMap<String, Object> selectedBmClassMap = psnVOs[j].appValueHashMap;
					Iterator<Entry<String, Object>> iter = selectedBmClassMap.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry<String, Object> entry = ((Entry<String, Object>) iter.next());
						if (entry.getKey().equalsIgnoreCase(pk_bmclass)
								&& entry.getValue().toString().equalsIgnoreCase("y")
								&& !showBmClassList.contains(classVOs[i])) {
							showBmClassList.add(classVOs[i]);
							break;
						}
					}
					// 这里之所以不用selectedBmClassMap.containsKey(pk_bmclass)方法
					// 判断方案主键是否在Map中是因为：
					// Map中的key所包含的英文字母都是小写，而方案主键包含的英文字母都是大写。
					// if(selectedBmClassMap.containsKey(pk_bmclass)
					// && selectedBmClassMap.get(pk_bmclass).equals("Y")){
					// showBmClassList.add(classVOs[i]);
					// }
				}
			}
		}

		return showBmClassList.toArray(new BmClassVO[0]);
	}

	@Override
	public void stepDisactived(WizardStepEvent event) throws WizardStepException {
		// 为解决社保档案批量新增向导界面第三步“设置信息”显示所有险种页签的问题修改
		// 修改上一步空指针问题 zhoumxc 2014.08.14 补丁合并
		ArrayList list = new ArrayList();
		for (int i = 0; i < classPanel.length; i++) {
			if (classPanel[i] != null) {
				BmDataVO bmdata = (BmDataVO) classPanel[i].getBillData().getHeaderValueVO(BmDataVO.class.getName());
				bmdata.setPk_bm_class(this.classVOs[i].getPk_bm_class());
				list.add(bmdata);
			}
		}
		// MOD {社保档案批量新增、修改社保代理公司、险种社保供应商代码（公司）、险种社保供应商代码（个人）} kevin.nie
		// 2018-01-17
		// start
		String pk_agentcomp = getHeadBmOfAgentComp();
		BmDataVO[] vos = new BmDataVO[list.size()];
		for (int i = 0; i < list.size(); i++) {
			vos[i] = (BmDataVO) list.get(i);
			vos[i].setDef1(pk_agentcomp);
		}

		getModel().putAttr("classInfo", vos);

		BmDataVO[] psnVOs = (BmDataVO[]) getModel().getAttr("selectedPsn");
		for (BmDataVO vo : psnVOs) {
			vo.setDef1(pk_agentcomp);
		}
		getModel().putAttr("selectedPsn", psnVOs);
		// {社保档案批量新增、修改社保代理公司、险种社保供应商代码（公司）、险种社保供应商代码（个人）} kevin.nie 2018-01-17
		// end

	}

	public BillCardPanel getBillCardPanel(int index) {
		return (BillCardPanel) getUICompont().getComponent(index);
	}

	private BmLoginContext getBmLoginContext() {
		return loginContext;
	}
}