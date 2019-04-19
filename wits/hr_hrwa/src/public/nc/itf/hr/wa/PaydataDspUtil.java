package nc.itf.hr.wa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.paydata.ICommonAlterName;
import nc.vo.wa.paydata.WaPaydataDspVO;
import nc.vo.wa.pub.WaLoginContext;

import org.apache.commons.lang.ArrayUtils;

/**
 * 薪资档案显示设置的工具类
 * @author erl
 *
 */
public class PaydataDspUtil {
	//薪资发放个人设置项目
	public static final String personalDsp = "1";
	//薪资发放通用设置项目
	public static final String commonDsp = "0";
	//离职结薪通用设置项目
	public static final String commonDsp4payleave = "2";
	//离职结薪个人设置项目
	public static final String PersonalDsp4payleave  = "3";
	
	 
	//薪资档案默认显示的字段及顺序
	public final static String[] defaultShowColKey = new String[]{
		ICommonAlterName.PSNCODE/*"人员编码 */,
		ICommonAlterName.CLERKCODE/*员工号*/,
		DataVO.PSNNAME/*姓名*/,
		ICommonAlterName.PLSNAME/*人员类别*/,
		ICommonAlterName.ORGNAME/*任职组织*/,
		// 2015-07-30 zhousze NCdp205099799 薪资发放界面添加财务组织财务部门 begin
		ICommonAlterName.FINANCEORG/* 财务组织 */,
		ICommonAlterName.FINANCEDEPT/* 财务部门 */,
		//zhaochxs税改纳税申报组织
		"taxorgname",/*纳税申报组织*/
		ICommonAlterName.LIABILITYORG/* 成本中心 */,
		ICommonAlterName.LIABILITYDEPT/* 成本部门 */,
		// end
		ICommonAlterName.DEPTNAME/*部门*/,
		ICommonAlterName.POSTNAME/*岗位*/,
		DataVO.TAXTYPE,/*扣税方式*/
		DataVO.CACULATEFLAG/*计算标志*/,
		DataVO.CHECKFLAG/*审核标志*/
	};
	
	//离职结薪默认显示的字段及顺序
		public final static String[] defaultShowColKey4PayLeave = new String[]{
			ICommonAlterName.PSNCODE/*"人员编码 */,
			ICommonAlterName.CLERKCODE/*员工号*/,
			DataVO.PSNNAME/*姓名*/,
			ICommonAlterName.PLSNAME/*人员类别*/,
			ICommonAlterName.ORGNAME/*任职组织*/,
			
			// 2015-11-03 xiejie3 离职结薪显示设置添加财务组织财务部门 begin
			ICommonAlterName.FINANCEORG/* 财务组织 */,
			ICommonAlterName.FINANCEDEPT/* 财务部门 */,
			//zhaochxs税改纳税申报组织
			"taxorgname",/*纳税申报组织*/
			ICommonAlterName.LIABILITYORG/* 成本中心 */,
			ICommonAlterName.LIABILITYDEPT/* 成本部门 */,
			// end
			
			ICommonAlterName.DEPTNAME/*部门*/,
			ICommonAlterName.POSTNAME/*岗位*/,
			DataVO.TAXTYPE,/*扣税方式*/
			DataVO.CACULATEFLAG/*计算标志*/,
			DataVO.CHECKFLAG/*审核标志*/,
			DataVO.PAYFLAG/*发放标志*/,
			DataVO.LEAVEDATE/*离职日期*/,
			DataVO.CPAYDATE/*发放日期*/,
			DataVO.VPAYCOMMENT/*发放原因*/
		};
	
	/**
	 * 把getColMap()查询出的结果，转换成List<WaPayfileDspVO>
	 * 
	 * @param colMap
	 * @return
	 */
	public static List<WaPaydataDspVO> queryDefaultDsp() {
		//界面上显示的名称
		Map<String,String> showNameMap = initShowNameMap();
		List<WaPaydataDspVO> dspList = new ArrayList<WaPaydataDspVO>();
		for(int i=0;i<defaultShowColKey.length;i++){
			WaPaydataDspVO vo = new WaPaydataDspVO();
			vo.setItem_key(defaultShowColKey[i]);
			vo.setItemName(showNameMap.get(defaultShowColKey[i]));
			vo.setBshow(UFBoolean.TRUE);
			vo.setIsWaItem(UFBoolean.FALSE);
			dspList.add(vo);
		}
		
		return dspList;
	}
	
	
	/**
	 * 把getColMap()查询出的结果，转换成List<WaPayfileDspVO>
	 * 
	 * @param colMap
	 * @return
	 */
	public static List<WaPaydataDspVO> queryDefaultDsp4PayLeave() {
		//界面上显示的名称
		Map<String,String> showNameMap4Payleave = initShowNameMap4PayLeave();
		List<WaPaydataDspVO> dspList = new ArrayList<WaPaydataDspVO>();
		for(int i=0;i<defaultShowColKey4PayLeave.length;i++){
			WaPaydataDspVO vo = new WaPaydataDspVO();
			vo.setItem_key(defaultShowColKey4PayLeave[i]);
			vo.setItemName(showNameMap4Payleave.get(defaultShowColKey4PayLeave[i]));
			vo.setBshow(UFBoolean.TRUE);
			vo.setIsWaItem(UFBoolean.FALSE);
			vo.setDisplayseq(i);
			dspList.add(vo);
		}
		
		return dspList;
	}
	
	
	/**
	 * 把公共薪资项目转换成薪资发放节点的显示设置项目
	 * @param itemArray
	 * @return
	 */
	public static List<WaPaydataDspVO> convertWaItemVO(WaItemVO[] itemArray){
		List<WaPaydataDspVO> dspList = new ArrayList<WaPaydataDspVO>();
		if(!ArrayUtils.isEmpty(itemArray)){
			for(int i=0;i<itemArray.length;i++){
				dspList.add(convertWaItemVO(itemArray[i]));
			}
		}
		return dspList;
	}
	
	/**
	 * 已设置显示顺序之后，方案若有新加的项目，需要添加
	 * @param dspList
	 * @param itemArray
	 */
	public static void addNewlyDsiplayItem(List<WaPaydataDspVO> dspList,
			WaItemVO[] itemArray) {
		if (ArrayUtils.isEmpty(itemArray) || dspList == null) {
			return;
		}
		Map<String, String> dspItemMap = new HashMap<String, String>();
		for (WaPaydataDspVO dspVO : dspList) {
			if (dspVO.getIsWaItem().booleanValue()) {
				dspItemMap.put(dspVO.getItem_key(), dspVO.getItem_key());
			}
		}
		for (int i = 0; i < itemArray.length; i++) {
			if(!dspItemMap.containsKey(itemArray[i].getItemkey())){
				dspList.add(convertWaItemVO(itemArray[i]));
			}
		}

	}
	
	
//	20150728 xiejie3 补丁合并NCdp205395630,此方法目前没有用到,来自于补丁,暂且写在这里,备以后用,人员发生部门，人员类别的变动后，薪资档案不会同步.begin
	/**
	 * 已设置显示顺序之后，方案若有新加的项目，需要添加
	 * @param dspList
	 * @param itemArray
	 */
	public static void addNewlyDsiplayItem4Fix(List<WaPaydataDspVO> dspList) {
		Map<String, String> dspItemMap = new HashMap<String, String>();
		for (WaPaydataDspVO dspVO : dspList) {
			if (!dspVO.getIsWaItem().booleanValue()) {
				dspItemMap.put(dspVO.getItem_key(), dspVO.getItem_key());
			}
		}
		
		if (!dspItemMap.containsKey(ICommonAlterName.FINANCEORG)) {
			WaPaydataDspVO dspVO = new WaPaydataDspVO();
			dspVO.setItem_key(ICommonAlterName.FINANCEORG);
			dspVO.setItemName(ResHelper.getString("60130payfile","160130payfile0026")/*@res "财务组织"*/);
			dspVO.setBshow(UFBoolean.TRUE);
			dspVO.setIsWaItem(UFBoolean.FALSE);
			dspList.add(dspVO);
		}
		
		if (!dspItemMap.containsKey(ICommonAlterName.FINANCEDEPT)) {
			WaPaydataDspVO dspVO = new WaPaydataDspVO();
			dspVO.setItem_key(ICommonAlterName.FINANCEDEPT);
			dspVO.setItemName(ResHelper.getString("60130payfile","160130payfile0023")/*@res "财务部门"*/);
			dspVO.setBshow(UFBoolean.TRUE);
			dspVO.setIsWaItem(UFBoolean.FALSE);
			dspList.add(dspVO);
		}
		
		if (!dspItemMap.containsKey(ICommonAlterName.LIABILITYORG)) {
			WaPaydataDspVO dspVO = new WaPaydataDspVO();
			dspVO.setItem_key(ICommonAlterName.LIABILITYORG);
			dspVO.setItemName(ResHelper.getString("60130payfile","160130payfile0024")/*@res "成本中心"*/);
			dspVO.setBshow(UFBoolean.TRUE);
			dspVO.setIsWaItem(UFBoolean.FALSE);
			dspList.add(dspVO);
		}
		
		if (!dspItemMap.containsKey(ICommonAlterName.LIABILITYDEPT)) {
			WaPaydataDspVO dspVO = new WaPaydataDspVO();
			dspVO.setItem_key(ICommonAlterName.LIABILITYDEPT);
			dspVO.setItemName(ResHelper.getString("60130payfile","160130payfile0025")/*@res "成本部门"*/);
			dspVO.setBshow(UFBoolean.TRUE);
			dspVO.setIsWaItem(UFBoolean.FALSE);
			dspList.add(dspVO);
		}

	}
//	end
	
	
	
	
	
	private static WaPaydataDspVO convertWaItemVO(WaItemVO itemVO){
		WaPaydataDspVO dspVO = new WaPaydataDspVO();
		dspVO.setItem_key(itemVO.getItemkey());
		dspVO.setItemName(itemVO.getMultilangName());
		//显示与否应根据权限设置，待修改薪资项目权限后再修改。
		dspVO.setBshow(UFBoolean.TRUE);
		dspVO.setIsWaItem(UFBoolean.TRUE);
		return dspVO;
	}
	
	/**
	 * 薪资发放显示设置，设置页面显示的多语名称
	 * @param dspList
	 * @return
	 */
	//2014/05/23 shenliangc为解决薪资发放节点显示设置对话框中项目名称与本期间发放项目名称不同步问题而修改。
	//名称不同步的原因是查询项目名称逻辑没有添加年度期间限制，全部取方案起始期间的发放项目名称。
	public static List<WaPaydataDspVO> setPaydataDisplayName(List<WaPaydataDspVO> dspList, WaLoginContext context) {
		//界面上显示的名称
		//界面上显示的名称
		Map<String,String> showNameMap = initShowNameMap();
		IClassItemQueryService citemService = NCLocator.getInstance().lookup(IClassItemQueryService.class);
		
		//20150918 shenliangc 解决上海医药薪资发放刷新效率问题。批量查询发放项目信息，减少SQL数量。
		String condition = "pk_wa_class = '" + context.getPk_wa_class() + "' and cyear = '" + context.getCyear() + "' and cperiod = '" + context.getCperiod() + "' ";
		WaClassItemVO[] classItemVOs = null;
		try {
			classItemVOs = citemService.queryClassItemByCondition(context, condition);
		} catch (BusinessException e1) {
			Logger.error(e1.getMessage(), e1);
		}
		HashMap<String,String> classItemNameMap = new HashMap<String,String>();
		if(!ArrayUtils.isEmpty(classItemVOs)){
			for(WaClassItemVO classItemVO : classItemVOs){
				classItemNameMap.put(classItemVO.getItemkey(), classItemVO.getMultilangName());
			}
		}
		
		
		for (int i = 0; i < dspList.size(); i++) {
			WaPaydataDspVO dspVO = dspList.get(i);
			if(dspVO.getIsWaItem().booleanValue() && !classItemNameMap.isEmpty()){
					//薪资方案主键context.getPk_wa_class()这样的取法是为了处理多次发薪每次发放项目名称不同的情况。
					//发放项目名称根据当前界面选中的子方案主键和年度期间查询发放项目表。
//					WaClassItemVO itemVO = citemService.queryItemWithItemkeyAndPK_wa_class(dspVO.getItem_key(), context.getPk_wa_class(), context.getCyear(), context.getCperiod());
//					dspList.get(i).setItemName(itemVO.getMultilangName());
					dspList.get(i).setItemName(classItemNameMap.get(dspList.get(i).getItem_key()));
			}else{
				dspList.get(i).setItemName(showNameMap.get(dspList.get(i).getItem_key()));
			}
		}
		return dspList;
	}
	
	

	/**
	 * 离职结薪显示设置，设置页面显示的多语名称
	 * @param dspList
	 * @return
	 */
	//2014/05/23 shenliangc为解决离职结薪节点显示设置对话框中项目名称与本期间发放项目名称不同步问题而修改。
	//名称不同步的原因是查询项目名称逻辑没有添加年度期间限制，全部取方案起始期间的发放项目名称。
	public static List<WaPaydataDspVO> setPaydataDisplayName4PayLeave(List<WaPaydataDspVO> dspList, WaLoginContext context) {
		//界面上显示的名称
		Map<String,String> showNameMap4Payleave = initShowNameMap4PayLeave();
//		IItemQueryService itemService = NCLocator.getInstance().lookup(IItemQueryService.class);
		IClassItemQueryService citemService = NCLocator.getInstance().lookup(IClassItemQueryService.class);
		
		String condition = "pk_wa_class = '" + context.getPk_wa_class() + "' and cyear = '" + context.getCyear() + "' and cperiod = '" + context.getCperiod() + "' ";
		WaClassItemVO[] classItemVOs = null;
		try {
			classItemVOs = citemService.queryClassItemByCondition(context, condition);
		} catch (BusinessException e1) {
			Logger.error(e1.getMessage(), e1);
		}
		HashMap<String,String> classItemNameMap = new HashMap<String,String>();
		if(!ArrayUtils.isEmpty(classItemVOs)){
			for(WaClassItemVO classItemVO : classItemVOs){
				classItemNameMap.put(classItemVO.getItemkey(), classItemVO.getMultilangName());
			}
		}
		
		for (int i = 0; i < dspList.size(); i++) {
			WaPaydataDspVO dspVO = dspList.get(i);
			if(dspVO.getIsWaItem().booleanValue()&& !classItemNameMap.isEmpty()){
//					WaItemVO itemVO = itemService.queryByItemkey(dspVO.getItem_key());
//					WaClassItemVO itemVO = citemService.queryItemWithItemkeyAndPK_wa_class(dspVO.getItem_key(), context.getPk_wa_class(), context.getCyear(), context.getCperiod());
					dspList.get(i).setItemName(classItemNameMap.get(dspList.get(i).getItem_key()));
			}else{
				dspList.get(i).setItemName(showNameMap4Payleave.get(dspList.get(i).getItem_key()));
			}
		}
		return dspList;
	}
	
	
	
	
	private static Map<String,String> initShowNameMap(){
		Map<String,String> nameMap = new HashMap<String, String>();
		nameMap.put(ICommonAlterName.PSNCODE, ResHelper.getString("common", "UC000-0000147")/*@res "人员编码 */);
		nameMap.put(ICommonAlterName.CLERKCODE, ResHelper.getString("60130paydata","060130paydata0503")/*@res "员工号"*/);
		nameMap.put(DataVO.PSNNAME, ResHelper.getString("60130paydata","060130paydata0504")/*@res "姓名"*/);
		nameMap.put(ICommonAlterName.PLSNAME, ResHelper.getString("60130paydata","060130paydata0505")/*@res "人员类别"*/);
		nameMap.put(ICommonAlterName.ORGNAME, ResHelper.getString("60130paydata","060130paydata0506")/*@res "任职组织"*/);
		// 2015-07-30 zhousze NCdp205099799 薪资发放界面添加财务组织财务部门 begin
		nameMap.put(ICommonAlterName.FINANCEORG, ResHelper.getString("60130payfile", "160130payfile0026")/*@res 财务组织 */);
		nameMap.put(ICommonAlterName.FINANCEDEPT, ResHelper.getString("60130payfile", "160130payfile0023")/*@res 财务部门 */);
		nameMap.put(ICommonAlterName.LIABILITYORG, ResHelper.getString("60130payfile", "160130payfile0024")/*@res 成本中心 */);
		nameMap.put(ICommonAlterName.LIABILITYDEPT, ResHelper.getString("60130payfile", "160130payfile0025")/*@res 成本部门 */);
		// end
		nameMap.put(ICommonAlterName.DEPTNAME, ResHelper.getString("60130paydata","060130paydata0507")/*@res "部门"*/);
		nameMap.put(ICommonAlterName.POSTNAME, ResHelper.getString("60130paydata","060130paydata0508")/*@res "岗位"*/);
		nameMap.put(DataVO.TAXTYPE, ResHelper.getString("60130paydata","060130paydata0509")/*@res "扣税方式"*/);
		nameMap.put(DataVO.CACULATEFLAG, ResHelper.getString("60130paydata","060130paydata0510")/*@res "计算标志"*/);
		nameMap.put(DataVO.CHECKFLAG, ResHelper.getString("60130paydata","060130paydata0511")/*@res "审核标志"*/);
		nameMap.put("taxorgname", "纳税申报组织");
		return nameMap;
	}
	
	private static Map<String,String> initShowNameMap4PayLeave(){
		Map<String,String> nameMap = new HashMap<String, String>();
		nameMap.put(ICommonAlterName.PSNCODE, ResHelper.getString("common", "UC000-0000147")/*@res "人员编码 */);
		nameMap.put(ICommonAlterName.CLERKCODE, ResHelper.getString("60130paydata","060130paydata0503")/*@res "员工号"*/);
		nameMap.put(DataVO.PSNNAME, ResHelper.getString("60130paydata","060130paydata0504")/*@res "姓名"*/);
		nameMap.put(ICommonAlterName.PLSNAME, ResHelper.getString("60130paydata","060130paydata0505")/*@res "人员类别"*/);
		nameMap.put(ICommonAlterName.ORGNAME, ResHelper.getString("60130paydata","060130paydata0506")/*@res "任职组织"*/);
		
		// 2015-11-03 xiejie3 离职结薪添加财务组织财务部门 begin
		nameMap.put(ICommonAlterName.FINANCEORG, ResHelper.getString("60130payfile", "160130payfile0026")/*@res 财务组织 */);
		nameMap.put(ICommonAlterName.FINANCEDEPT, ResHelper.getString("60130payfile", "160130payfile0023")/*@res 财务部门 */);
		nameMap.put(ICommonAlterName.LIABILITYORG, ResHelper.getString("60130payfile", "160130payfile0024")/*@res 成本中心 */);
		nameMap.put(ICommonAlterName.LIABILITYDEPT, ResHelper.getString("60130payfile", "160130payfile0025")/*@res 成本部门 */);
		// end
		
		nameMap.put(ICommonAlterName.DEPTNAME, ResHelper.getString("60130paydata","060130paydata0507")/*@res "部门"*/);
		nameMap.put(ICommonAlterName.POSTNAME, ResHelper.getString("60130paydata","060130paydata0508")/*@res "岗位"*/);
		nameMap.put(DataVO.TAXTYPE, ResHelper.getString("60130paydata","060130paydata0509")/*@res "扣税方式"*/);
		nameMap.put(DataVO.CACULATEFLAG, ResHelper.getString("60130paydata","060130paydata0510")/*@res "计算标志"*/);
		nameMap.put(DataVO.CHECKFLAG, ResHelper.getString("60130paydata","060130paydata0511")/*@res "审核标志"*/);
		nameMap.put(DataVO.PAYFLAG, ResHelper.getString("60130paydata","060130paydata0512")/*@res "发放标志"*/);
		nameMap.put(DataVO.LEAVEDATE, ResHelper.getString("60130paydata","060130paydata0513")/*@res "离职日期"*/);
		nameMap.put(DataVO.CPAYDATE, ResHelper.getString("60130paydata","060130paydata0514")/*@res "发放日期"*/);
		nameMap.put(DataVO.VPAYCOMMENT, ResHelper.getString("60130paydata","060130paydata0515")/*@res "发放原因"*/);
		nameMap.put("taxorgname", "纳税申报组织");
		
		return nameMap;
	}
	
}
