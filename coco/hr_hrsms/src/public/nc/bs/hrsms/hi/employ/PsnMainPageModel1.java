package nc.bs.hrsms.hi.employ;

import java.util.ArrayList;
import java.util.List;

import nc.bs.hrsms.hi.hrsmsConstant;
import nc.bs.hrsms.hi.hrsmsUtil;
import nc.bs.hrss.hi.psninfo.PsninfoConsts;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.cata.CatagoryPanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.uap.lfw.core.common.EditorTypeConst;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.GridColumn;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.MenubarComp;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.event.conf.DatasetRule;
import nc.uap.lfw.core.event.conf.EventConf;
import nc.uap.lfw.core.event.conf.EventSubmitRule;
import nc.uap.lfw.core.event.conf.ViewRule;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIControl;
import nc.uap.lfw.jsp.uimeta.UILayoutPanel;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.uap.lfw.jsp.uimeta.UITabComp;
import nc.uap.lfw.jsp.uimeta.UITabItem;
import nc.uap.lfw.ra.render.pc.PCFormCompRender;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hrss.pub.SessionBean;

/**
 * 我的档案入口
 * 
 * @author lihha 2011-3-23 下午03:05:01
 */
public class PsnMainPageModel1 extends AdvancePageModel {
	protected String getFunCode() {
		return PsninfoConsts.PSNINFO_NODECODE;
	}

	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		String dept_psndoc_pk = getWebContext().getParameter("pk_psndoc");
		String ismainjob = getWebContext().getParameter("ismainjob");
		// 从部门人员信息节点过来的pk_psnjob(这个是在处理兼职人员的时候用到的)
		String pk_psnjob = getWebContext().getParameter("pk_psnjob");
		SessionBean session = SessionUtil.getSessionBean();
		session.setExtendAttribute(PsninfoConsts.DEPT_PSNDOC_PK, dept_psndoc_pk);
		hrsmsUtil.setBoOperatePsndocPK(dept_psndoc_pk);
		session.setExtendAttribute("ismainjob", ismainjob);
		session.setExtendAttribute("pk_psnjob", pk_psnjob);
		setPhoto();
		UIMeta uiMeta = (UIMeta) getUIMeta();
		UITabComp tabComp = (UITabComp) uiMeta.findChildById("tag2905");
		//设置左侧高度
		((nc.uap.lfw.jsp.uimeta.UIFlowvPanel)uiMeta.findChildById("panelv02890")).setHeight("460px");
		
		tabComp.setHideTabBar(true);
		
		// 动态的为每个需要查看详细信息的子集的grid上增加一个【查看】的超链接列
		LfwView widget = getPageMeta().getView("main");
		List<UILayoutPanel> itemList = tabComp.getPanelList();
		if(widget == null){
			return;
		}
		MenubarComp menubar = widget.getViewMenus().getMenuBar("mymenu");
		ArrayList<String> dsId = new ArrayList<String>();
		
		for (int i = 0; i < itemList.size(); i++) {
			UITabItem tab = (UITabItem) itemList.get(i);
			String componentId = null;
			if (tab.getElement() instanceof UIControl) {
				componentId = (String) tab.getElement().getAttribute("id");
				WebComponent comp = widget.getViewComponents().getComponent(componentId);
				if (comp != null) {
					 if (comp instanceof GridComp) {
							GridColumn column = new GridColumn();
							// 设置id
							column.setId("search");
							// 设置langDir
							column.setLangDir("hi_nodes");
							// 设置i18nName
							column.setI18nName("w_psninfo-000687");
							// 设置显示标题
							column.setText("查看");/* -=notranslate=- */
							// 设置关联Dataset的Field
							column.setField(null);
							// 设置列宽度,按实际宽度
							column.setWidth(70);
							// 设置渲染器
							column.setRenderType("NameLinkRender");
							// 设置编辑类型
							column.setEditorType(EditorTypeConst.SELFDEF);
							((GridComp)comp).insertColumn(0, column, true);
							dsId.add(((GridComp) comp).getDataset());
						}
					 if(comp instanceof FormComp){
						 dsId.add(((FormComp) comp).getDataset());
					 }
				}
			}
		}
		// 给【上移】、【下移】、【保存】添加提交规则
		addSubmitRuleForMenuItem(dsId,menubar);
		
	}

	

	private void setPhoto() {
		// 设置照片
		LfwView widget = getPageMeta().getView("main");
		Dataset ds = widget.getViewModels().getDataset("bd_psndoc");
		Field field = ds.getFieldSet().getField(PsndocVO.PHOTO);
		if(field != null){
			field.setExtendAttribute(PCFormCompRender.IMG_PK_FIELD, "patha");
			field.setExtendAttribute(PCFormCompRender.IMG_URL, "$REPLACE$");
		}
		
	}

	@Override
	protected String getQueryTempletKey() {
		return null;
	}

	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
		CatagoryPanel cp = new CatagoryPanel();
		//根据是员工节点与经理节点的判断，更改显示标题
		String title="我的信息";/* @res "我的信息" */
		if(getWebContext().getParameter("pk_psndoc")!=null){
			title="人员信息";/* @res "人员信息" */
		}
		cp.setTitle(title);
		cp.setDataProvider(new ShopPsninfoTabCatDataProvider(um,
				PsninfoConsts.TABLAYOUT_MAIN_ID, pm));
		return new IPagePanel[] { new CanvasPanel(), cp };
	}

	@Override
	protected String getRightPage() {
		return null;
	}
	
	/**
	 * 给【上移】、【下移】、【保存】添加提交规则
	 * 
	 * @param datasetId
	 * @param menubar
	 */
	private void addSubmitRuleForMenuItem(ArrayList<String> datasetIds, MenubarComp menubar) {
		MenuItem[] menuItems = new MenuItem[3];
		menuItems[0] = menubar.getItem("remove_up");
		menuItems[1] = menubar.getItem("remove_down");
		menuItems[2] = menubar.getItem("remove_save");

		for (MenuItem menuItem : menuItems) {
			// 提交规则
			EventSubmitRule sr = new EventSubmitRule();
			ViewRule wr = new ViewRule();
			wr.setId("main");
			for(String datasetId:  datasetIds){
				List<EventConf> eventConfList = menuItem.getEventConfList();
				DatasetRule dsr = new DatasetRule();
				dsr.setId(datasetId);
				dsr.setType(DatasetRule.TYPE_ALL_LINE);
				wr.addDsRule(dsr);

				EventConf itemEvent = eventConfList.get(0);

				sr.addViewRule(wr);
				itemEvent.setSubmitRule(sr);
			}
			
		}

	}
}