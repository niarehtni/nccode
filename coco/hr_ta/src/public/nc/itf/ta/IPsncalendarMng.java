package nc.itf.ta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;

public interface IPsncalendarMng {
	
	/**
	 * 店员工作日历插入数据库
	 * @param ImportvosMap  
	 * @param isnullclear
	 * @param psnDateShiftHolidayTypeMap
	 * @return
	 * @throws BusinessException
	 */
	public List<String>[] importDatasHdType(HashMap<String, ArrayList<GeneralVO>> ImportvosMap,boolean isnullclear,HashMap<String, HashMap<String, String>> psnDateShiftHolidayTypeMap, String psnjoborg) throws BusinessException;
	
	public String queryPsnJobOrgByName(String name);
	
}
