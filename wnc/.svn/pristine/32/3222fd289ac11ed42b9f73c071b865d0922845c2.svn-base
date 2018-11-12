package nc.ui.wa.item.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ui.hr.uif2.action.SaveAction;
import nc.ui.hr.uif2.action.SaveAddAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.para.SysInitBO_Client;
import nc.ui.wa.item.util.WaExpandTableUtil;
import nc.ui.wa.item.view.ItemBillFormEditor;
import nc.ui.wa.item.view.custom.ItemDataSourcePanel;
import nc.ui.wa.pub.DefaultWaActionInterceptor;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.item.FromEnumVO;
import nc.vo.wa.item.PropertyEnumVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.ParaConstant;

import org.apache.commons.lang.StringUtils;

public class SaveItemInterceptor extends DefaultWaActionInterceptor {

	@Override
	public boolean beforeDoAction(Action action, ActionEvent e) {

		
		WaItemVO vo = null;;
		if(action instanceof SaveAction) {
			 vo =(WaItemVO) ((SaveAction)action).getEditor().getValue();
		}else  {
		    vo =(WaItemVO) ((SaveAction)((SaveAddAction)action).getSaveAction()).getEditor().getValue();
		}
		
		if(vo!=null){
			if(vo.getIitemtype().equals(TypeEnumVO.FLOATTYPE.value())){
				if(vo.getIflddecimal()==null){
					showErrorDlg(ResHelper.getString("60130glbitem","060130glbitem0011")/*@res "数值型的小数位数不能为空"*/);
					return false;
				}
//				if (vo.getIflddecimal().intValue() >= vo.getIfldwidth().intValue()) {
//				    showErrorDlg(ResHelper.getString("60130glbitem","060130glbitem0012")/*@res "薪资项目的小数位数需要小于数据长度。"*/);
//					return false;
//				}
				try {
					if(itemMustInTotalItem() && StringUtils.isBlank(vo.getTotalitem())){
						showErrorDlg(ResHelper.getString("60130glbitem","060130glbitem0013")/*@res "公共薪资项目必须纳入预算,请选择预算项目"*/);
						return false;
					}
				} catch (BusinessException e2) {
					Logger.error(e2.getMessage(),e2);
					showErrorDlg(ResHelper.getString("60130glbitem","060130glbitem0014")/*@res "判断公共薪资项目是否必须纳入预算失败"*/);
					return false;
				}
			}
		}
		if(vo!=null && vo.getIproperty()==PropertyEnumVO.SYSTEM.value()){
			showErrorDlg(ResHelper.getString("60130glbitem","060130glbitem0015")/*@res "增减属性不能是:系统项"*/);
			return false;
		}

		if(vo.getFromEnumVO().equals(FromEnumVO.FORMULA)){
			if(StringUtil.isEmpty(vo.getVformula())){
				showErrorDlg(ResHelper.getString("60130glbitem","060130glbitem0016")/*@res "公式不能为空"*/);
				return false;
			}
		}else if(vo.getFromEnumVO().equals(FromEnumVO.HI) ){
			if(StringUtil.isEmpty(vo.getVformula())){
				showErrorDlg(ResHelper.getString("60130glbitem","060130glbitem0017")/*@res "人事函数不能为空"*/);
				return false;
			}
		}else if(vo.getFromEnumVO().equals(FromEnumVO.WAORTHER)){
			if(StringUtil.isEmpty(vo.getVformula())){
				showErrorDlg(ResHelper.getString("60130glbitem","060130glbitem0018")/*@res "薪资函数不能为空"*/);
				return false;
			}
		}else if(vo.getFromEnumVO().equals(FromEnumVO.OTHERSOURCE)){
			if(StringUtil.isEmpty(vo.getVformula())){
				showErrorDlg("外部数据源不能为空");
				return false;
			}
		}else if(vo.getFromEnumVO().equals(FromEnumVO.WA_WAGEFORM) ){
			if(StringUtil.isEmpty(vo.getVformula())){
				showErrorDlg(ResHelper.getString("60130glbitem","060130glbitem0019")/*@res "薪资规则表不能为空"*/);
				return false;
			}
			  // liangxr如果来源是薪资规则表，判断项目的小数位数是否
			ItemDataSourcePanel idp = null;
			if(action instanceof SaveAction) {
				idp = ((ItemBillFormEditor)((SaveAction)action).getEditor()).getItemDataSourcePanel();
			}else  {
				idp = ((ItemBillFormEditor)((SaveAction)((SaveAddAction)action).getSaveAction()).getEditor()).getItemDataSourcePanel();
			}
			
			int ruleDecimal =(Integer)idp.getWageForm().getRefModel().getValue("iflddecimal");
			int itemDecimal = vo.getIflddecimal();
			if (itemDecimal < ruleDecimal) {
			    String msg = ResHelper.getString("60130glbitem","060130glbitem0020")/*@res "薪资项目的小数位数小于薪资规则表的小数位数，是否继续?"*/;
			    if (MessageDialog.showYesNoDlg(getContainer(), ResHelper.getString("60130glbitem","060130glbitem0021")/*@res "提示"*/, msg) == UIDialog.ID_NO) {
				return false;
			    }
			}
		    //end

		}else if(vo.getFromEnumVO().equals(FromEnumVO.WA_GRADE) ){
			if(StringUtil.isEmpty(vo.getVformula())){
				showErrorDlg(ResHelper.getString("60130glbitem","060130glbitem0022")/*@res "薪资标准表不能为空"*/);
				return false;
			}
		}

		if(isAddSave(vo)){
			//核查预置项目数是否够用 ，进行扩展
			WaExpandTableUtil util = new WaExpandTableUtil(getContainer(),vo);
			try {
				if(!util.expandTable()){
					showErrorDlg(ResHelper.getString("60130glbitem","060130glbitem0023")/*@res "薪资发放表扩展失败"*/);
					return false;
				}

			} catch (BusinessException e2) {
				showErrorDlg(ResHelper.getString("60130glbitem","060130glbitem0023")/*@res "薪资发放表扩展失败"*/);
				return false;
			}

		}

		return super.beforeDoAction( action,  e);

	}


	private  boolean isAddSave(WaItemVO vo){
		return StringUtils.isBlank(vo.getPk_wa_item());

	}


	/**
	 * 判断集团是否允许下级增加发放项目
	 * @return
	 * @throws BusinessException
	 */
	private boolean itemMustInTotalItem() throws BusinessException{
		//
		UFBoolean must = UFBoolean.TRUE;
		must = SysInitBO_Client.getParaBoolean(getModel().getContext().getPk_group(), ParaConstant.ITEM_INTO_AMOUNT);
		//must = NCLocator.getInstance().lookup(IHrPara.class).getBooleanValue(getModel().getContext().getPk_group(), ParaConstant.ITEM_INTO_AMOUNT, null, null);

		return must.booleanValue();

	}

}