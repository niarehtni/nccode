package nc.ui.wa.datainterface.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.IActionCode;
import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
import nc.bs.uif2.validation.ValidationException;
import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.ResHelper;
import nc.hr.wa.log.WaBusilogUtil;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.itf.hr.wa.IPaydataManageService;
import nc.itf.hr.wa.IPaydataQueryService;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.datainterface.view.InputItemSelDlg;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hr.util.HrDataPermHelper;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.wa.datainterface.model.DataIOAppModel;
import nc.ui.wa.datainterface.model.DefaultImportDB;
import nc.ui.wa.datainterface.model.ImportDBStrategy;
import nc.ui.wa.datainterface.model.WaDrawItemsStrategy;
import nc.ui.wa.datainterface.view.DataIOPanel;
import nc.ui.wa.datainterface.view.DefaultDataIOHeadPanel;
import nc.ui.wa.pub.WADelegator;
import nc.vo.hr.append.AppendableVO;
import nc.vo.hr.caculate.CaculateTypeVO;
import nc.vo.hr.datainterface.AggHrIntfaceVO;
import nc.vo.hr.datainterface.FormatItemVO;
import nc.vo.hr.datainterface.HrIntfaceVO;
import nc.vo.om.pub.SuperVOHelper;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.datainterface.DataIOconstant;
import nc.vo.wa.item.FromEnumVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.pub.HRWAMetadataConstants;
import nc.vo.wa.pub.WaLoginContext;

import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author: xuanlt
 * @date: 2010-1-14 上午09:18:11
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class DataIntoDBAction extends HrAction
{
	private DefaultDataIOHeadPanel ioHeadPanel;
	private DataIOPanel ioMainPanel;
	private InputItemSelDlg inputItemSelDlg = null;
//	// 得到关联字段（人员编码） 所对应的数值
//	private static final String relatedfiled = "bd_psndoc.psncode";
//	// 用户选择的导入项目
//	private final FormatItemVO[] importItems = null;

	/**
	 * @author xuanlt on 2010-1-14
	 */
	public DataIntoDBAction()
	{
		super();
		this.setBtnName(ResHelper.getString("6013bnkitf","06013bnkitf0036")/*@res "导入数据库"*/);
		this.putValue(INCAction.CODE, "ImportData");
		this.putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("6013bnkitf","06013bnkitf0037")/*@res "导入数据库(Ctrl+I)"*/);
	}

	/**
	 * @author xuanlt on 2010-1-14
	 * @see nc.ui.uif2.NCAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void doAction(ActionEvent e) throws Exception
	{
		final WaLoginContext context = (WaLoginContext) getModel().getContext();
		/*查询期间是否审核*/
		if (context != null
				&& context.getWaLoginVO() != null
				&& context.getWaLoginVO().getPeriodVO() != null
				&& context.getWaLoginVO().getPeriodVO().getCheckflag()!=null
				&& context.getWaLoginVO().getPeriodVO().getCheckflag().booleanValue()) {
			throw new BusinessException(ResHelper.getString("6013bnkitf","06013bnkitf0152")/*@res "该期间数据已经审核，不能导入数据！"*/);
		}
		Object obj = this.getModel().getSelectedData();
		AggHrIntfaceVO aggVO = (AggHrIntfaceVO) obj;
		HrIntfaceVO vo = (HrIntfaceVO) aggVO.getParentVO();
		// ArrayList<HashMap<String, Object>> datas = ((DataIOAppModel)
		// getModel()).getResults().get(vo.getPk_dataio_intface());
		AppendableVO[] appendVOs = (AppendableVO[]) ((DataIOAppModel) getModel()).getBillModelMap().get(vo.getPk_dataio_intface()).getBodyValueVOs(AppendableVO.class.getName());
		FormatItemVO[] itemVOs = (FormatItemVO[]) aggVO.getTableVO(DataIOconstant.HR_DATAINTFACE_B);
		//IClassItemQueryService service = NCLocator.getInstance().lookup(IClassItemQueryService.class);
		//WaClassItemVO[] items = service.queryAllClassItemInfos((WaLoginContext) getModel().getContext());
		WaClassItemVO[] items = WADelegator.getPaydataQuery().getUserClassItemVOs(context);
		// 验证数据是否为空
		if (ArrayUtils.isEmpty(appendVOs))
		{
			throw new BusinessException(ResHelper.getString("6013bnkitf","06013bnkitf0038")/*@res "请先组织数据!"*/);
		}
		// 在保存数据前校验，并且得到可以编辑的项目
		FormatItemVO[] aftFilteritemVOs = filterFormatItems(itemVOs, items);
		getInputItemSelDlg().setBodyVOs(aftFilteritemVOs);

		// 让用户选择要导入的项目
		getInputItemSelDlg().showModal();
		if (getInputItemSelDlg().getResult() == UIDialog.ID_OK)
		{
			itemVOs = getInputItemSelDlg().getSelItem();
			if (itemVOs == null)
			{
				throw new BusinessException(ResHelper.getString("6013bnkitf","06013bnkitf0039")/*@res "没有选择导入项目！"*/);
			}
		}
		else
		{
			return;
		}

		final IPaydataManageService mgrService = NCLocator.getInstance().lookup(IPaydataManageService.class);
		IPaydataQueryService queryService = NCLocator.getInstance().lookup(IPaydataQueryService.class);
		DataVO[] dbVOs = queryService.queryDataVOsByCond(context, null, null);

		if (dbVOs == null)
		{
			return;
		}
		final List<DataVO> list = new ArrayList<DataVO>();
		Map<String, AppendableVO> map = new HashMap<String, AppendableVO>();
		String colAsName = vo.getVcol().equals("id") ? "id" : "psncode";
		for (int j = 0; appendVOs != null && j < appendVOs.length; j++)
		{
			if (colAsName.equals("id")) {
				if (appendVOs[j].getAttributeValue("bd_psndocid") != null && appendVOs[j].getAttributeValue("bd_psndocidtype") != null)
				{
					String id ;
					String idtype ;
					String inputid = (String)appendVOs[j].getAttributeValue("bd_psndocid");
					String inputidtype = (String)appendVOs[j].getAttributeValue("bd_psndocidtype");
					for (int i = 0; dbVOs != null && i < dbVOs.length; i++)
					{

						if (dbVOs[i].getAttributeValue("id") == null || dbVOs[i].getAttributeValue("idtype") == null)
						{
							continue;
						}
						id = (String)dbVOs[i].getAttributeValue("id");
						idtype = (String)dbVOs[i].getAttributeValue("idtype");
						if ((id+idtype).equals(inputid+inputidtype))
						{
							if(map.containsKey(inputid+inputidtype)){
								throw new BusinessException(ResHelper.getString("6013bnkitf","06013bnkitf0040")/*@res "数据中存在重复的人员，导入出错！"*/);
							}else{
								map.put(inputid+inputidtype, appendVOs[j]);
							}
							DataVO datavo = new DataVO();
							datavo.setPk_wa_data(dbVOs[i].getPk_wa_data());
							datavo.setTs(dbVOs[i].getTs());
							datavo.setCheckflag(dbVOs[i].getCheckflag());
							datavo.setCpaydate(dbVOs[i].getCpaydate());
							datavo.setVpaycomment(dbVOs[i].getVpaycomment());
							for (int k = 0; itemVOs != null && k < itemVOs.length; k++)
							{

								Object value = appendVOs[j].getAttributeValue(itemVOs[k].getVcontent().replace(".", ""));
								int beginIndex = itemVOs[k].getVcontent().indexOf(".");
								String name = itemVOs[k].getVcontent().substring(
										beginIndex + 1);
								datavo.setAttributeValue(name, value);
							}
							list.add(datavo);
							break;
						}
					}
				} else {
					throw new BusinessException(ResHelper.getString("6013bnkitf",
							"06013bnkitf0041")/* @res "没有设置关联项目或者关联项目的值为空，不能导入！" */);
				}
			}else{
				if (appendVOs[j].getAttributeValue("bd_psndoc" + vo.getVcol()) != null)
				{
					for (int i = 0; dbVOs != null && i < dbVOs.length; i++)
					{
						
						if (dbVOs[i].getAttributeValue(colAsName) == null)
						{
							continue;
						}
						if (dbVOs[i].getAttributeValue(colAsName).equals(appendVOs[j].getAttributeValue("bd_psndoc" + vo.getVcol())))
						{
							if(map.containsKey(appendVOs[j].getAttributeValue("bd_psndoc" + vo.getVcol()))){
								throw new BusinessException(ResHelper.getString("6013bnkitf","06013bnkitf0040")/*@res "数据中存在重复的人员，导入出错！"*/);
							}else{
								map.put((String) appendVOs[j]
										.getAttributeValue("bd_psndoc"
												+ vo.getVcol()), appendVOs[j]);
							}
							DataVO datavo = new DataVO();
							datavo.setPk_wa_data(dbVOs[i].getPk_wa_data());
							datavo.setTs(dbVOs[i].getTs());
							datavo.setCheckflag(dbVOs[i].getCheckflag());
							datavo.setCpaydate(dbVOs[i].getCpaydate());
							datavo.setVpaycomment(dbVOs[i].getVpaycomment());
							for (int k = 0; itemVOs != null && k < itemVOs.length; k++)
							{
								
								Object value = appendVOs[j].getAttributeValue(itemVOs[k].getVcontent().replace(".", ""));
								int beginIndex = itemVOs[k].getVcontent().indexOf(".");
								String name = itemVOs[k].getVcontent().substring(
										beginIndex + 1);
								datavo.setAttributeValue(name, value);
							}
							list.add(datavo);
							break;
						}
					}
				} else {
					throw new BusinessException(ResHelper.getString("6013bnkitf",
							"06013bnkitf0041")/* @res "没有设置关联项目或者关联项目的值为空，不能导入！" */);
				}
			}
		}
		//判断导入数据中有没有不在该期间内的
		String strErrorMessage = "";
		int importCount = 0;
		String inputid;
		String inputidtype;
		if (colAsName.equals("id")) {
			for(int j = 0; appendVOs != null && j < appendVOs.length; j++){
				inputid = appendVOs[j].getAttributeValue("bd_psndocid") == null? "" : (String)appendVOs[j].getAttributeValue("bd_psndocid");
				inputidtype = appendVOs[j].getAttributeValue("bd_psndocidtype") == null? "" : (String)appendVOs[j].getAttributeValue("bd_psndocidtype");
				if(map.keySet().contains(inputid+inputidtype)){
					importCount ++;
					continue;
				}else{
					strErrorMessage += ResHelper.getString("6013bnkitf","06013bnkitf0165")/*@res " 证件类型+证件号码："*/+ inputidtype+"+"+inputid + " ";
				}
			}
		}else{
			for(int j = 0; appendVOs != null && j < appendVOs.length; j++){
				if (map.keySet().contains(appendVOs[j].getAttributeValue("bd_psndoc" + vo.getVcol()))){
					importCount ++;
					continue;
				}else{
					strErrorMessage += ResHelper.getString("6013bnkitf","06013bnkitf0138")/*@res "人员编码："*/+ appendVOs[j].getAttributeValue("bd_psndoc" + vo.getVcol())+"\n";
				}
			}
		}
		if(strErrorMessage!=""){
			strErrorMessage += ResHelper.getString("6013bnkitf","06013bnkitf0139")/*@res "不在该期间内，无法导入，是否要继续？"*/;
			if(MessageDialog.showOkCancelDlg(ioMainPanel, ResHelper.getString("60130bankitf","060130bankitf0006")
					/*@res "提示信息"*/, strErrorMessage
					)==2){
				setCancelMsg();
				return;
			}
		}
		//        ValidationException ex = HrDataPermHelper.checkDataPermission(
		//				IHRWADataResCode.PAYFILE, null, IActionCode.EDIT,
		//				list.toArray(), getContext());
		//		HrDataPermHelper.dealValidationException(ex);
		//checkDataPermission(list.toArray());
		new SwingWorker() {
			BannerTimerDialog dialog = new BannerTimerDialog(getEntranceUI());
			String error = null;
			@Override
			protected Boolean doInBackground() throws Exception {
				try{
					dialog.setStartText(ResHelper.getString("60130paydata","60130paydata-008")/*@res "薪资计算过程中，请稍等..."*/);
					dialog.start();
					mgrService.update(list.toArray(), context.getWaLoginVO());
				}catch (LockFailedException le) {
					error = ResHelper.getString("60130paydata","060130paydata0334")/*@res "你操作的数据正被他人修改！"*/;
				}catch (VersionConflictException le) {
					throw new BusinessException(le.getBusiObject().toString(),le);
				}catch (Exception e) {
					error = e.getMessage();
				} finally {
					dialog.end();
				}
				return Boolean.TRUE;
			}

			@Override
			protected void done() {
			}
		}.execute();
		WaLoginContext loginContext = (WaLoginContext) getModel()
				.getContext();

		WaBusilogUtil.writeWaClassBusiLog(loginContext,HRWAMetadataConstants.INPUTDATA,HRWAMetadataConstants.WA_CLASS_INPUTDATA_ID);
		
		//shenliangc 20140718 
		//信发集团有限公司  数据接口提示不详细
		//06013bnkitf0154=成功{0}条，失败{1}条。
		putValue(HrAction.MESSAGE_AFTER_ACTION, ResHelper.getString("6013bnkitf", "06013bnkitf0042") 
				+ "\n" 
				+ ResHelper.getString("6013bnkitf", "06013bnkitf0154", 
						String.valueOf(importCount), String.valueOf(appendVOs.length - importCount)) 
				);
		// MessageDialog.showHintDlg(this.getEntranceUI(), null,
		// ResHelper.getString("6013bnkitf","06013bnkitf0042")/*@res
		// "数据导入成功！仅修改尚未审核数据！"*/);
	}

	/**
	 * 过滤系统预置的项目和手工输入项目
	 *
	 * @author xuhw on 2010-8-3
	 * @param itemVOs
	 */
	private FormatItemVO[] filterFormatItems(FormatItemVO[] itemVOs, WaClassItemVO[] classItemVOs)
	{
		Map<String, WaClassItemVO> classItemCache = SuperVOHelper.buildAttributeToVOMap(WaClassItemVO.ITEMKEY, classItemVOs);
		List<FormatItemVO> lisNewFIVO = new ArrayList<FormatItemVO>();
		for (FormatItemVO itemVO : itemVOs)
		{
			if (!ArrayUtils.contains(WaDrawItemsStrategy.getSystemItems(), itemVO.getVcontent()))
			{
				String itemcode = itemVO.getVcontent().replace("wa_data.", "");
				WaClassItemVO classvo = classItemCache.get(itemcode);
				if (classvo != null
						&& !"1001Z7100000000WA001".equals(classvo
								.getCategory_id())
								&& classvo.getFromEnumVO().getEnumValue().getValue().equals(FromEnumVO.USER_INPUT.getEnumValue().getValue())
								&& classvo.getEditflag().booleanValue()) {
					lisNewFIVO.add(itemVO);
				}

			}
		}
		return lisNewFIVO.toArray(new FormatItemVO[0]);
	}

	public InputItemSelDlg getInputItemSelDlg()
	{
		if (inputItemSelDlg == null)
		{
			inputItemSelDlg = new InputItemSelDlg(getModel().getContext().getEntranceUI());
		}

		return inputItemSelDlg;
	}

	// private FormatItemVO[] transferItem(String[] itemsindex)
	// {
	// FormatItemVO[] tempitems = new FormatItemVO[itemsindex.length];
	// FormatItemVO[] items = getFormatVOs();
	//
	// for (int index = 0; index < itemsindex.length; index++)
	// {
	// tempitems[index] = items[Integer.parseInt(itemsindex[index])];
	// }
	// return tempitems;
	// }

	// private FormatItemVO[] getFormatVOs()
	// {
	// return ((DataIODisplayPanel)
	// getFrameUI().getDataIOPanel()).getFormatVOs();
	// }

	protected ImportDBStrategy getImporter()
	{
		return new DefaultImportDB();
	}

	public DefaultDataIOHeadPanel getIoHeadPanel()
	{
		return ioHeadPanel;
	}

	public void setIoHeadPanel(DefaultDataIOHeadPanel ioHeadPanel)
	{
		this.ioHeadPanel = ioHeadPanel;
	}

	public DataIOPanel getIoMainPanel()
	{
		return ioMainPanel;
	}

	public void setIoMainPanel(DataIOPanel ioMainPanel)
	{
		this.ioMainPanel = ioMainPanel;
	}

	/***************************************************************************
	 * 检查是否有权限操作数据<br>
	 * Created on 2011-5-5 11:02:09<br>
	 * 
	 * @throws BusinessException
	 * @author Rocex Wang
	 ***************************************************************************/
	protected void checkDataPermission(Object objData) throws BusinessException {

		ValidationException ex = HrDataPermHelper.checkDataPermission(
				IHRWADataResCode.WADATA, IActionCode.EDIT, IActionCode.EDIT,
				objData, getContext());

		HrDataPermHelper.dealValidationException(ex);
	}

}