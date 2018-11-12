package nc.bs.hrsms.ta.sss.overtime.ctrl;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.itf.ta.ICheckTimeQueryService;
import nc.itf.ta.algorithm.ICheckTime;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.ButtonComp;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

public class ShopOverTimeCardInfoViewMain implements IController{

	/**
	 * 获得当前Application
	 */
	private ApplicationContext getCurrentApplication() {
		return AppLifeCycleContext.current().getApplicationContext();
	}
	
	public void onDataLoad_dsCardInfo(DataLoadEvent dataLoadEvent) {
		String pk_overtimereg = (String) getCurrentApplication().getAppAttribute("CardInfo_pk_overtimereg");
		getCurrentApplication().removeAppAttribute("CardInfo_pk_overtimereg");
		LfwView view = ViewUtil.getCurrentView();
		Dataset dsCardInfo = view.getViewModels().getDataset("ds_cardinfo");
		try {
			ICheckTime[] checkTimes = NCLocator.getInstance().lookup(ICheckTimeQueryService.class).queryCheckTimesByOverRegister(pk_overtimereg);
			if(checkTimes!=null){
				 for(int i=0;i<checkTimes.length;i++){
					 Row row = dsCardInfo.getEmptyRow();
					 if(StringUtils.isNotEmpty(checkTimes[i].getTimecardid())){
						 row.setValue(dsCardInfo.nameToIndex("timecardid"), checkTimes[i].getTimecardid());
					 }
					 row.setValue(dsCardInfo.nameToIndex("datetime"), checkTimes[i].getDatetime());
					 row.setValue(dsCardInfo.nameToIndex("timeflag"), checkTimes[i].getTimeflag());
					 row.setValue(dsCardInfo.nameToIndex("checkflag"), checkTimes[i].getCheckflag());
					 row.setValue(dsCardInfo.nameToIndex("pk_machine"), checkTimes[i].getPk_machine());
					 row.setValue(dsCardInfo.nameToIndex("pk_place"), checkTimes[i].getPk_place());
					 row.setValue(dsCardInfo.nameToIndex("placeabnormal"), checkTimes[i].getPlaceabnormal());
					 row.setValue(dsCardInfo.nameToIndex("placeabnormal"), checkTimes[i].getPlaceabnormal());
					 row.setValue(dsCardInfo.nameToIndex("signreason"), checkTimes[i].getSignreason());
					 row.setValue(dsCardInfo.nameToIndex("creator"), checkTimes[i].getCreator());
					 row.setValue(dsCardInfo.nameToIndex("creationtime"), checkTimes[i].getCreationtime());
					 dsCardInfo.addRow(row);
				 }
				 dsCardInfo.setEnabled(false);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭
	 * @param mouseEvent
	 */
	public void onClose(MouseEvent<ButtonComp> mouseEvent) {
		// 关闭弹出页面
		CmdInvoker.invoke(new CloseWindowCmd());
		
	}
}
