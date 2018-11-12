package nc.bs.hrsms.hi.employ.ShopTransfer.lsnr;

import nc.uap.lfw.core.combodata.CombItem;
import nc.uap.lfw.core.combodata.DynamicComboDataConf;
import nc.vo.trn.pub.TRNConst;

/**
 * 调配方式下拉列表Loader
 *
 * @author lihha
 *
 */
public class StapplyModeComboLoader extends DynamicComboDataConf {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public CombItem[] getAllCombItems() {
		CombItem[] items = new CombItem[2];
		items[0] = new CombItem();
		items[0].setValue("" + TRNConst.TRANSMODE_INNER);
		items[0].setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","组织内调配")/*@res "0c_trn-res0020"*/);
		items[1] = new CombItem();
		items[1].setValue("" + TRNConst.TRANSMODE_CROSS_OUT);
		items[1].setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","调出")/*@res "0c_trn-res0021"*/);
		return items;
	}
}