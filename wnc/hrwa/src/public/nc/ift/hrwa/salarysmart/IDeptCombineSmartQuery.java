package nc.ift.hrwa.salarysmart;

import nc.pub.smart.data.DataSet;
import nc.vo.pub.BusinessException;

public interface IDeptCombineSmartQuery {
	/**
	 * 部門合併
	 * @param pk_org 組織
	 * @param pk_wa_class 薪資方案
	 * @param startPeriod 開始期間
	 * @param endPeriod 結束期間
	 * @param source 來源(0,薪資,1,二代健保)
	 * @param psnType 人員類別 IDL,DL
	 * @return
	 * @throws BusinessException
	 * 1.最小單位為「部」(部門級別大於70者，一律往上合併至部級，如無部級則再往上合併)，此動作完成後，才進行人數合併。
	 *  2.小於等於2人的部門要往上合併，大於2人的部門則不再往上合併，會留在原本的位置。
	 *  3.DL、IDL分開合併 (舉例:若該部門DL有25人、IDL有2人，IDL要往上合併)
	 *  4.全部合併好後，合併完的表中，從上至下(部門級別最小到最大)檢查，如有小於等於2人的部門，則其人數與金額合併至下一層同級之部門中，部門編碼最小且人數/金額不等於0的部門，如下一層部門全部都為0，則再往下一層。
	 */
	DataSet deptCombine(String pk_org,String pk_wa_class,String startPeriod,String endPeriod,Double source,String psnType) throws BusinessException;
}
