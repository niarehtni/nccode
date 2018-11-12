package nc.bs.hrsms.hi.psninfo;

import java.util.ArrayList;
import java.util.List;

import nc.bs.hrss.hi.psninfo.PsninfoConsts;
import nc.bs.hrss.pub.PageModel;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.UIElementUtil;
import nc.pub.tools.HRCMCommonValue;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.event.PageEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.jsp.uimeta.UIFlowvLayout;
import nc.uap.lfw.jsp.uimeta.UILayoutPanel;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.uap.lfw.jsp.uimeta.UITabComp;
import nc.vo.hi.psndoc.CtrtVO;
import nc.vo.pub.lang.UFDate;

public class ShopPsnDetailPageModel extends PageModel {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 7532916478964732880L;

	public void sysWindowClosed(PageEvent event) {
		LfwRuntimeEnvironment.getWebContext().destroyWebSession();
	}

	@Override
	protected void initPageMetaStruct() {
		String voMeta = getWebContext().getParameter(PsninfoConsts.CURR_DATASET);
		LfwView widget = getPageMeta().getView("main");
		UIMeta uiMeta = (UIMeta)getUIMeta();
		UITabComp tabComp = (UITabComp) uiMeta.findChildById("tag6499");
		// 把tabComp选中的页签设置为第一个(避免用户在自定义表单时修改选中的页签，导致程序出错)
		tabComp.setCurrentItem("0");
		// 把tabComp选中的单个页签是否显示设置成不显示(避免用户在自定义表单时修改，导致程序出错)
		tabComp.setOneTabHide(0);
		
		List<UILayoutPanel> itemList = tabComp.getPanelList();
		List<UILayoutPanel> copyItemList = new ArrayList<UILayoutPanel>();
		for(UILayoutPanel item: itemList)
			copyItemList.add(item);
		SessionUtil.setAttribute("DETAIL_CURR_DATASET", null);
		for (int i = 0; i < copyItemList.size(); i++) {
			UILayoutPanel tab = copyItemList.get(i);
			if(tab.getElement() == null){
				tabComp.removePanel(tab);
				continue;
			}
			String formId = (String) tab.getElement().getAttribute("id");
			String currdataset = null;
			if(formId != null && !"flowvlayout3756".equals(formId)){
				WebComponent comp = widget.getViewComponents().getComponent(formId);
				if (comp instanceof FormComp) {
					currdataset = ((FormComp) comp).getDataset();
				} else if (comp instanceof GridComp) {
					currdataset = ((GridComp) comp).getDataset();
				}
			} else {
				currdataset = "hi_psndoc_ctrt";
				UIFlowvLayout panel =  (UIFlowvLayout) UIElementUtil.findUIElementByID(copyItemList.get(i),"flowvlayout3756");
				List<UILayoutPanel> panelList = panel.getPanelList();
				Integer conttype = (Integer) SessionUtil.getAttribute(CtrtVO.CONTTYPE);
				if(conttype == null){
					// 当conttype==null，表示当前选中的数据集不是合同信息
					conttype = -12;
				}
				for(UILayoutPanel panelTemp : panelList){
					switch(conttype){
					case HRCMCommonValue.CONTTYPE_MAKE :
						// 
						if(!panelTemp.getId().equals("panelv08875")){
							panelTemp.setVisible(false);
						}
						formId = "hi_psndoc_ctrtForm";
						break;
					case HRCMCommonValue.CONTTYPE_EXTEND :
						// 
						if(!panelTemp.getId().equals("panelv11875")){
							panelTemp.setVisible(false);
						}
						formId = "hi_psndoc_ctrt_form_extend";
						break;
					case HRCMCommonValue.CONTTYPE_CHANGE :
						if(!panelTemp.getId().equals("panelv12875")){
							panelTemp.setVisible(false);
						}
						formId = "hi_psndoc_ctrt_form_change";
						break;
					case HRCMCommonValue.CONTTYPE_RELEASE :
						if(!panelTemp.getId().equals("panelv09875")){
							panelTemp.setVisible(false);
						}
						formId = "hi_psndoc_ctrt_form_Release";
						break;
					case HRCMCommonValue.CONTTYPE_FINISH :
						if(!panelTemp.getId().equals("panelv10875")){
							panelTemp.setVisible(false);
						}
						formId = "hi_psndoc_ctrt_form_finish";
						break;
					default:
						if(!panelTemp.getId().equals("panelv08875")){
							panelTemp.setVisible(false);
						}
						formId = "hi_psndoc_ctrtForm";
						break;
					}
				}
			}
			
			Dataset ds = widget.getViewModels().getDataset(currdataset);
			if(ds == null) {
				tabComp.removePanel(tab);
				continue;
			}
			if(voMeta.equals(ds.getVoMeta())){
				SessionUtil.setAttribute("formId", formId);
				SessionUtil.setAttribute("DETAIL_CURR_DATASET", ds.getId());
				continue;
			}
			tabComp.removePanel(tab);
		}
		super.initPageMetaStruct();
	}

	@Override
	protected String getFunCode() {
		return "E20206005";
	}
	
	//现在页面都加缓存了，如果etag值没有变化,页面就会从浏览器缓存读取
	@Override
	public String getBusinessEtag(){
		return (new UFDate()).toString();
	}

}
