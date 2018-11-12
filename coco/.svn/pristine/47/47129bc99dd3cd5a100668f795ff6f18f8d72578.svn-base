package nc.bs.hrss.ta.signcard.ctrl;

import java.util.List;

import nc.bs.hrsms.ta.SignReg.ctrl.SignRegAfterDataChange;
import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.ta.common.ctrl.TaApplyBaseView;
import nc.bs.hrss.ta.signcard.SignConsts;
import nc.bs.hrss.ta.signcard.lsnr.AutoAddCmd;
import nc.bs.hrss.ta.signcard.lsnr.SignCardCommitProcessor;
import nc.bs.hrss.ta.signcard.lsnr.SignCardLineAddProcessor;
import nc.bs.hrss.ta.signcard.lsnr.SignCardSaveAddProcessor;
import nc.bs.hrss.ta.signcard.lsnr.SignCardSaveProcessor;
import nc.itf.hrss.pub.cmd.prcss.ICommitProcessor;
import nc.itf.hrss.pub.cmd.prcss.ILineInsertProcessor;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.MenubarComp;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.DatasetEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.ta.signcard.SignRegVO;

/**
 * 签卡申请PopWindow
 * 
 * @author qiaoxp
 * 
 */

public class SignCardApplyView extends TaApplyBaseView implements IController {
	private static final long serialVersionUID = 1L;

	/**
	 * 单据类型
	 * 
	 * @return
	 */
	@Override
	protected String getBillType() {
		return SignConsts.BILL_TYPE_CODE;
	}

	/**
	 * 数据集ID
	 * 
	 * @return
	 */
	@Override
	protected String getDatasetId() {
		return SignConsts.DS_MAIN_NAME;
	}

	@Override
	protected String getDetailDsId() {
		return SignConsts.DS_SUB_NAME;
	}

	/**
	 * 单据聚合类型VO
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends AggregatedValueObject> getAggVoClazz() {
		return SignConsts.CLASS_NAME_AGGVO;
	}

	/**
	 * 保存的PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ISaveProcessor> getSavePrcss() {
		return SignCardSaveProcessor.class;
	}

	/**
	 * 保存并新增的PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ISaveProcessor> getSaveAddPrcss() {
		return SignCardSaveAddProcessor.class;
	}

	/**
	 * 提交的操作类
	 */
	@Override
	protected Class<? extends ICommitProcessor> getCommitPrcss() {
		return SignCardCommitProcessor.class;
	}
	
	/**
	 * 保存并提交的操作类
	 */
	@Override
	protected Class<? extends ICommitProcessor> getSaveCommitPrcss() {
		return null;
	}

	/**
	 * 行新增的PROCESSOR
	 */
	@Override
	protected Class<? extends ILineInsertProcessor> getLineAddPrcss() {
		return SignCardLineAddProcessor.class;
	}
	
	/**
	 * onAfterDataChange事件
	 * 处理值变化
	 * Ares 
	 * 2018-7-23 15:39:42
	 * @param datasetEvent
	 */
	public void onAfterDataChange(DatasetCellEvent datasetCellEvent){
//		SignRegVO.PK_PSNJOB;
		Dataset ds = datasetCellEvent.getSource();
		// 字段顺序
		/*int colIndex = datasetCellEvent.getColIndex();
		if(colIndex != ds.nameToIndex(SignRegVO.PK_PSNJOB)){
			return;
		}*/
		Row row = ds.getSelectedRow();
		if(row == null ){
			return;
		}
		SignRegAfterDataChange.onAfterDataPsnChange(ds, row ,datasetCellEvent.getColIndex());
		/*if(colIndex == ds.nameToIndex(SignRegVO.PK_PSNJOB)){
			
			String pk_psndoc  = (String) row.getValue(ds.nameToIndex("pk_psndoc"));
			ShopTaAppContextUtil.addTaAppForTransferContext(pk_psndoc);
			SignRegAfterDataChange.onAfterDataChange(ds, row);
		}*/
	}

	/**
	 * 数据加载事件
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad_signcardh(DataLoadEvent dataLoadEvent) {
		String operateStatus = (String) getApplicationContext().getAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS);
		// 区分签卡申请/自动签卡申请
		if (HrssConsts.POPVIEW_OPERATE_AUTO_ADD.equals(operateStatus)) {
			LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
			MenubarComp menuOperate = widget.getViewMenus().getMenuBar("menu_operate");
			if (menuOperate != null) {
				List<MenuItem> menuList = menuOperate.getMenuList();
				if (menuList != null && menuList.size() > 0) {
					for (MenuItem item : menuList) {
						if (item.getId().equals("btnSaveCommit")) {
//							continue;
						}
						if("btnSaveAdd".equals(item.getId())){
							continue;
						}
						item.setVisible(true);
					}
				}
			}
			AutoAddCmd cmd = new AutoAddCmd(getDatasetId(), getBillType(), null);
			CmdInvoker.invoke(cmd);
		} else {
			super.onDataLoad(dataLoadEvent);
		}
	}

}