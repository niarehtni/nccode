package nc.itf.hr.wa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.hr.utils.ResHelper;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.payfile.WaPayfileDspVO;

/**
 * 薪资档案显示设置的工具类
 * @author erl
 *
 */
public class PayfileDspUtil {
	public static final String personalDsp = "1";
	public static final String commonDsp = "0";
	
	//薪资档案默认显示的字段及顺序     code和单据模板的itemcode保持一致
	private final static String[] defaultShowColKey = new String[]{
		"pk_psndoc"/*人员编码*/,
		"pk_psnjob"/*员工号*/,
		"pk_psnjob.pk_psndoc.name"/*姓名*/,
		"pk_psnjob.pk_psncl"/*人员类别*/,
		"workorgvid"/*任职组织*/,
		"workdeptvid"/*部门*/,
		"pk_psnjob.pk_post"/*岗位*/,
		"fiporgvid",/*财务组织*/
		"fipdeptvid",/*财务部门*/
		"taxorg",/*纳税申报组织*/
		"pk_liabilityorg",/*成本中心*/
		"libdeptvid",/*成本部门*/
		"taxtype",/*扣税方式*/
		"taxtableid",/*税率表*/
		"isndebuct",/*使用附加费用扣除额*/
		"isderate",/*减免税*/
		"derateptg",/*减税比例*/
		"stopflag",/*停发*/
		"partflag",/*兼职*/
		"pk_bankaccbas1",/*主卡账号*/
		"pk_banktype1",/*主卡银行名称*/
		"pk_bankaccbas2",/*副卡1账号*/
		"pk_banktype2",/*副卡1银行名称*/
		"pk_bankaccbas3",/*副卡2账号*/
		"pk_banktype3"/*副卡2银行名称*/
	};
	

	/**
	 * 把getColMap()查询出的结果，转换成List<WaPayfileDspVO>
	 * 
	 * @return
	 */
	public static List<WaPayfileDspVO> getDefaultDsp() {
		List<WaPayfileDspVO> dspList = new ArrayList<WaPayfileDspVO>();
		Map<String,String> showNameMap = initShowNameMap();
		for(int i=0;i<defaultShowColKey.length;i++) {
			WaPayfileDspVO vo = new WaPayfileDspVO();
			vo.setItem_key(defaultShowColKey[i]);
			vo.setItemName(showNameMap.get(defaultShowColKey[i]));
			vo.setBshow(UFBoolean.TRUE);
			dspList.add(vo);
		}
		return dspList;
	}
	
	/**
	 * 设置表头多语名称
	 * @param dspList
	 * @return
	 */
	public static List<WaPayfileDspVO> setPayfileDisplayName(List<WaPayfileDspVO> dspList) {
		Map<String,String> showNameMap = initShowNameMap();
		for (int i = 0; i < dspList.size(); i++) {
			dspList.get(i).setItemName(showNameMap.get(dspList.get(i).getItem_key()));
		}
		return dspList;
	}
	
	private static Map<String,String> initShowNameMap(){
		Map<String,String> nameMap = new HashMap<String, String>();
		nameMap.put("pk_psndoc", ResHelper.getString("common", "UC000-0000147")/*@res "人员编码 */);
		nameMap.put("pk_psnjob", ResHelper.getString("60130payfile","060130payfile0333")/*@res "员工号"*/);
        nameMap.put("pk_psnjob.pk_psndoc.name", ResHelper.getString("60130payfile","060130payfile0339")/*@res "姓名"*/);
        nameMap.put("pk_psnjob.pk_psncl", ResHelper.getString("60130payfile","060130payfile0340")/*@res "人员类别"*/);
        nameMap.put("workorgvid", ResHelper.getString("60130payfile","160130payfile0009")/*@res "任职组织"*/);
        nameMap.put("workdeptvid", ResHelper.getString("60130payfile","060130payfile0341")/*@res "部门"*/);
        nameMap.put("pk_psnjob.pk_post", ResHelper.getString("60130payfile","060130payfile0342")/*@res "岗位"*/);
        nameMap.put("fiporgvid", ResHelper.getString("60130payfile","160130payfile0026")/*@res "财务组织"*/);
        nameMap.put("fipdeptvid", ResHelper.getString("60130payfile","160130payfile0023")/*@res "财务部门"*/);
        nameMap.put("pk_liabilityorg", ResHelper.getString("60130payfile","160130payfile0024")/*@res "成本中心"*/);
        nameMap.put("libdeptvid", ResHelper.getString("60130payfile","160130payfile0025")/*@res "成本部门"*/);
        nameMap.put("taxtype", ResHelper.getString("60130payfile","060130payfile0310")/*@res "扣税方式"*/);
        nameMap.put("taxtableid", ResHelper.getString("60130payfile","060130payfile0343")/*@res "税率表"*/);
        nameMap.put("isndebuct", ResHelper.getString("60130payfile","060130payfile0344")/*@res "使用附加费用扣除额"*/);
        nameMap.put("isderate", ResHelper.getString("60130payfile","160130payfile0005")/*@res "减免税"*/);
        nameMap.put("derateptg", ResHelper.getString("60130payfile","160130payfile0001")/*@res "减税比例"*/);
        nameMap.put("stopflag", ResHelper.getString("60130payfile","060130payfile0345")/*@res "停发"*/);
        nameMap.put("partflag", ResHelper.getString("60130payfile","060130payfile0346")/*@res "兼职"*/);
        nameMap.put("taxorg", "纳税申报组织");
        nameMap.put("pk_bankaccbas1", ResHelper.getString("60130payfile","160130payfile0006")/*@res "主卡账号"*/);
        nameMap.put("pk_banktype1", ResHelper.getString("60130payfile","160130payfile0011")/*@res "主卡银行名称"*/);
        nameMap.put("pk_bankaccbas2", ResHelper.getString("60130payfile","160130payfile0008")/*@res "副卡1账号"*/);
        nameMap.put("pk_banktype2", ResHelper.getString("60130payfile","160130payfile0012")/*@res "副卡1银行名称"*/);
        nameMap.put("pk_bankaccbas3", ResHelper.getString("60130payfile","160130payfile0007")/*@res "副卡2账号"*/);
        nameMap.put("pk_banktype3", ResHelper.getString("60130payfile","160130payfile0010")/*@res "副卡2银行名称"*/);
		return nameMap;
	}

}
