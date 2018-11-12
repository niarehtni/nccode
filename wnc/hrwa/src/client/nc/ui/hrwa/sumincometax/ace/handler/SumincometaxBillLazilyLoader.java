package nc.ui.hrwa.sumincometax.ace.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.framework.common.NCLocator;
import nc.itf.pubapp.pub.lazilyload.IBillLazilyLoaderService;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;
import nc.vo.hrwa.sumincometax.CIncomeTaxVO;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.lazilyload.BillLazilyLoaderVO;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;

/**
 * 
 * @author Ares.Tank 申报明细汇总,懒加载类 2018-8-9 17:29:50
 */
@SuppressWarnings("restriction")
public class SumincometaxBillLazilyLoader extends DefaultBillLazilyLoader {

	@Override
	public void loadChildrenByClass(
			Map<IBill, List<Class<? extends ISuperVO>>> needLoadChildrenMap)
			throws Exception {
		try {
			IBillLazilyLoaderService service = NCLocator.getInstance().lookup(
					IBillLazilyLoaderService.class);

			Map<BillLazilyLoaderVO, List<Class<? extends ISuperVO>>> map = new HashMap<BillLazilyLoaderVO, List<Class<? extends ISuperVO>>>();
			for (Entry<IBill, List<Class<? extends ISuperVO>>> entry : needLoadChildrenMap
					.entrySet()) {
				String pk = entry.getKey().getParent().getPrimaryKey();
				String ts = entry.getKey().getParent().getAttributeValue("ts")
						.toString();
				Class<? extends IBill> billClass = entry.getKey().getClass();
				Class<SuperVO> parentClass = (Class<SuperVO>) entry.getKey()
						.getParent().getClass();

				BillLazilyLoaderVO loaderVO = new BillLazilyLoaderVO();
				loaderVO.setPk(pk);
				loaderVO.setTs(ts);
				loaderVO.setBillClass(billClass);
				loaderVO.setParentClass(parentClass);

				map.put(loaderVO, entry.getValue());
			}

			Map<String, Map<Class<? extends ISuperVO>, SuperVO[]>> resultMap = service
					.queryChildrenByParentID(map);

			for (Entry<IBill, List<Class<? extends ISuperVO>>> entry : needLoadChildrenMap
					.entrySet()) {
				for (Entry<String, Map<Class<? extends ISuperVO>, SuperVO[]>> resultEntry : resultMap
						.entrySet()) {
					IBill bill = entry.getKey();
					if (bill.getPrimaryKey().equals(resultEntry.getKey())) {
						fillBill(bill, entry.getValue(), resultEntry.getValue());
					}
				}
			}
		} catch (Exception e) {
			ExceptionUtils.wrappException(e);
		}
	}

	private void fillBill(IBill bill,
			List<Class<? extends ISuperVO>> needLoadChildrenList,
			Map<Class<? extends ISuperVO>, SuperVO[]> resultMap) {
		for (Class<? extends ISuperVO> childrenClass : needLoadChildrenList) {
			SuperVO[] itemVOs = resultMap.get(childrenClass);
			// 对数据进行解密
			itemVOs = salaryDecrypt((CIncomeTaxVO[]) itemVOs);
			bill.setChildren(childrenClass, itemVOs);
		}
	}

	/**
	 * 
	 * 对数据进行解密
	 * 
	 * @author Ares.Tank
	 * @param bills
	 * @return
	 */
	private SuperVO[] salaryDecrypt(CIncomeTaxVO[] vos) {
		for (int i = 0; i < vos.length; i++) {
			if (vos[i] != null) {
				if (vos[i].getTaxbase() != null) {
					vos[i].setTaxbase(new UFDouble(SalaryDecryptUtil
							.decrypt(vos[i].getTaxbase().getDouble())));
				}
				if (vos[i].getCacu_value() != null) {
					vos[i].setCacu_value(new UFDouble(SalaryDecryptUtil
							.decrypt(vos[i].getCacu_value().getDouble())));
				}
				if (vos[i].getNetincome() != null) {
					vos[i].setNetincome(new UFDouble(SalaryDecryptUtil
							.decrypt(vos[i].getNetincome().getDouble())));
				}
				if (vos[i].getPickedup() != null) {
					vos[i].setPickedup(new UFDouble(SalaryDecryptUtil
							.decrypt(vos[i].getPickedup().getDouble())));
				}
			}

		}
		return vos;
	}

}
