package nc.bs.hrsms.ta.sss.common;

import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.MenubarComp;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;

public class ShopSetMenuItemVisible {

	public static void setMenuItemVisible(String menubarID,String itemID){
		MenubarComp items = (MenubarComp) AppLifeCycleContext.current().getViewContext().getView().getViewMenus().getMenuBar(menubarID);
		MenuItem item = items.getItem(itemID);
		if(item == null){
			return;
		}
		item.setVisible(false);
	}
	
	
}
