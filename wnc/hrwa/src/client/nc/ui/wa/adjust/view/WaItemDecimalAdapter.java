package nc.ui.wa.adjust.view;

import java.util.HashMap;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.uap.IUAPQueryBS;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.ui.wa.psndocwadoc.model.PsndocwadocAppModel;
import nc.ui.wa.salaryadjmgt.WASalaryadjmgtDelegator;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.item.WaItemVO;

import org.apache.commons.lang.StringUtils;

/**
 * 薪资项目小数监听
 * 
 * @author liangxr
 * 
 */
public class WaItemDecimalAdapter implements IBillModelDecimalListener2 {
	private String strSource;
	private Integer specificDigits = null;

	public Integer getSpecificDigits() {
		return specificDigits;
	}

	public void setSpecificDigits(Integer specificDigits) {
		this.specificDigits = specificDigits;
	}

	private String[] strTargets;

	HashMap<String, Integer> itemMap = new HashMap<String, Integer>();

	public WaItemDecimalAdapter(String Source, String[] Target, String pk_group) {
		this.strSource = Source;
		this.strTargets = Target;
		// 获取当前组织所有item MAP
		if (pk_group != null) {
			WaItemVO[] itemvos = null;
			try {
				itemvos = WASalaryadjmgtDelegator.getItemQueryService().queryWaItemVOsByGroup(pk_group, null);
			} catch (BusinessException e) {
				Logger.error(e.getMessage(), e);
			}
			if (itemvos == null) {
				return;
			}
			for (WaItemVO vo : itemvos) {
				if (getSpecificDigits() != null) {
					itemMap.put(vo.getPk_wa_item(), getSpecificDigits());
				} else {
					itemMap.put(vo.getPk_wa_item(), vo.getIflddecimal());
				}
			}
		}

	}

	public WaItemDecimalAdapter(String Source, String[] Target, PsndocwadocAppModel model) {
		this.strSource = Source;
		this.strTargets = Target;
		// 获取当前组织所有item MAP
		WaItemVO[] itemvos = null;
		String pk_org = null;
		try {
			itemvos = WASalaryadjmgtDelegator.getItemQueryService().queryWaItemVOForWadoc(model.getContext());
			pk_org = model.getContext().getPk_org();
			model.getCachedWaItemVOs().put(pk_org, itemvos);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}

		if (!StringUtils.isEmpty(pk_org)) {
			IUAPQueryBS svc = NCLocator.getInstance().lookup(IUAPQueryBS.class);
			OrgVO orgvo = null;
			try {
				orgvo = (OrgVO) svc.retrieveByPK(OrgVO.class, pk_org);

				if (orgvo != null && "0001Z010000000079UJK".equals(orgvo.getCountryzone())) {
					setSpecificDigits(0);
				}
			} catch (BusinessException e) {
				Logger.error(e.getMessage(), e);
			}
		}

		if (itemvos == null) {
			return;
		}
		for (WaItemVO vo : itemvos) {
			if (getSpecificDigits() != null) {
				itemMap.put(vo.getPk_wa_item(), getSpecificDigits());
			} else {
				itemMap.put(vo.getPk_wa_item(), vo.getIflddecimal());
			}
		}

	}

	public WaItemDecimalAdapter(WaItemVO[] itemvos) {
		for (WaItemVO vo : itemvos) {
			if (getSpecificDigits() != null) {
				itemMap.put(vo.getPk_wa_item(), getSpecificDigits());
			} else {
				itemMap.put(vo.getPk_wa_item(), vo.getIflddecimal());
			}
		}
	}

	@Override
	public String[] getTarget() {
		return this.strTargets;
	}

	@Override
	public int getDecimalFromSource(int row, Object pkValue) {
		// 从map取当前项目对应的小数位数
		if (itemMap.get(pkValue) != null) {
			return itemMap.get(pkValue);
		}
		// 如果map里面没有，可能是新增项目，查询添加。
		try {
			WaItemVO item = WASalaryadjmgtDelegator.getItemQueryService().queryWaItemVOByPk((String) pkValue);
			if (item != null) {
				itemMap.put(item.getPk_wa_item(), item.getIflddecimal());
				return item.getIflddecimal();
			}
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		return -1;
	}

	@Override
	public String getSource() {
		return strSource;
	}

	@Override
	public boolean isTarget(BillItem item) {
		String itemKey = item.getKey();
		boolean isTarget = false;

		// 判定该单元格是否为要设置的单元格
		for (int i = 0; i < strTargets.length; i++) {
			if (strTargets[i].equals(itemKey)) {
				isTarget = true;
				break;
			}
		}
		return isTarget;
	}

}
