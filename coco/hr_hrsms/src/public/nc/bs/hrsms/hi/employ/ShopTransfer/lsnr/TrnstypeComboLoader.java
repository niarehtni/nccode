package nc.bs.hrsms.hi.employ.ShopTransfer.lsnr;

import nc.bs.hrss.pub.Logger;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.itf.hi.ITrnstypeQueryService;
import nc.uap.lfw.core.combodata.CombItem;
import nc.uap.lfw.core.combodata.DynamicComboDataConf;
import nc.vo.hi.trnstype.TrnstypeVO;
import org.apache.commons.lang.ArrayUtils;

import nc.vo.hrss.pub.SessionBean;
import nc.vo.ml.MultiLangUtil;
import nc.vo.pub.BusinessException;
import nc.vo.trn.pub.TRNConst;
/**
 * 调配异动类型下拉列表Loader
 * @author lihha
 *
 */
public class TrnstypeComboLoader extends DynamicComboDataConf {
	private static final long serialVersionUID = -3385214549914317493L;

	@Override
	public CombItem[] getAllCombItems() {
		SessionBean session = SessionUtil.getSessionBean();
		TrnstypeVO[] trnstypeVOs;
		CombItem[] items = null;
		try {
			ITrnstypeQueryService service = ServiceLocator.lookup(ITrnstypeQueryService.class);
			trnstypeVOs = service.queryByCondition(session.getContext(), "trnsevent='3"//+getTrnsevent()+"'"
					+"' and ishrss='Y'","trnsevent, trnstypecode");
			if (!ArrayUtils.isEmpty(trnstypeVOs)) {
				items = new CombItem[trnstypeVOs.length];
				for (int i = 0; i < trnstypeVOs.length; i++) {
					CombItem item = new CombItem();
					item.setText(MultiLangUtil.getSuperVONameOfCurrentLang(trnstypeVOs[i], TrnstypeVO.TRNSTYPENAME, null));
					item.setValue(trnstypeVOs[i].getPk_trnstype());
					items[i] = item;
				}
			}
		} catch (BusinessException e1) {
			Logger.error(e1.getMessage(), e1);
		} catch (HrssException e1) {
			Logger.error(e1.getMessage(), e1);
		} catch (RuntimeException e){
			Logger.error(e.getMessage(), e);
		}
		return items;
	}
	
	protected int getTrnsevent(){
		return TRNConst.TRNSEVENT_TRANS;
	}
}
