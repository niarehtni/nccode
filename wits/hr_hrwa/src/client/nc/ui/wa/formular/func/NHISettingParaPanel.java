package nc.ui.wa.formular.func;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.PairFactory;
import nc.pubitf.twhr.IBasedocPubQuery;
import nc.ui.hr.func.IRefPanel;
import nc.ui.hr.itemsource.view.IParaPanel;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.vo.hr.func.FunctableItemVO;
import nc.vo.hr.tools.pub.Pair;
import nc.vo.twhr.basedoc.BaseDocVO;
import nc.vo.wa.pub.WaLoginContext;

public class NHISettingParaPanel extends UIPanel implements IParaPanel,
		IRefPanel, ItemListener {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = 4756108596728276527L;
	private UILabel ivjlblRefdata = null;
	private UIComboBox ivjcmbRefdata = null;
	private WaLoginContext context = null;
	private String retCode = null;

	private void initialize() {
		try {
			setName("NHISettingParaPanel");
			setLayout(null);
			setSize(240, 200);
			add(getlblRefdata(), getlblRefdata().getName());
			add(getcmbRefdata(), getcmbRefdata().getName());
		} catch (Throwable ivjExc) {
			handleException(ivjExc);
		}
		getcmbRefdata().addItemListener(this);
	}

	@Override
	public void checkPara(int paramInt) throws Exception {
	}

	@Override
	public void clearDis() {
	}

	@Override
	public Object[] getPara() throws Exception {
		return new String[] { retCode };
	}

	@Override
	public void setCurrentItemKey(String paramString) {
	}

	@Override
	public void setDatatype(int paramInt) {
	}

	@Override
	public void updateDis(int paramInt) {
	}

	@Override
	public void updateDis(FunctableItemVO[] paramArrayOfFunctableItemVO) {
	}

	@Override
	public void updateDis(String newFuncName) {
		initialize();
	}

	private UILabel getlblRefdata() {
		if (ivjlblRefdata == null) {
			try {
				ivjlblRefdata = new UILabel();
				ivjlblRefdata.setName("lblRefdata");
				ivjlblRefdata.setText("ßx“ñ…¢”µ");
				ivjlblRefdata.setBounds(2, 5, 50, 22);
				ivjlblRefdata.setHorizontalAlignment(4);
				ivjlblRefdata.setILabelType(0);
			} catch (Throwable ex) {
				handleException(ex);
			}
		}
		return ivjlblRefdata;
	}

	private UIComboBox getcmbRefdata() {
		if (ivjcmbRefdata == null) {
			try {
				ivjcmbRefdata = new UIComboBox();
				ivjcmbRefdata.setName("cmbRefdata");
				ivjcmbRefdata.setBounds(60, 5, 180, 22);
				ivjcmbRefdata.setTranslate(true);

				IBasedocPubQuery baseDocQuery = NCLocator.getInstance().lookup(
						IBasedocPubQuery.class);
				BaseDocVO[] vos = baseDocQuery.queryAllBaseDoc(getContext()
						.getPk_org());

				List<String> names = new ArrayList<String>();
				List<String> codes = new ArrayList<String>();

				if (vos != null && vos.length > 0) {
					names.add("");
					codes.add("");
					for (BaseDocVO vo : vos) {
						if (vo.getDoctype() < 3) {
							names.add(vo.getName());
							codes.add(vo.getCode());
						}
					}
				}

				PairFactory mPairFactory = new PairFactory(
						names.toArray(new String[0]),
						codes.toArray(new String[0]));

				ivjcmbRefdata.addItems(mPairFactory.getAllConstEnums());
				ivjcmbRefdata.setSelectedIndex(0);

			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjcmbRefdata;
	}

	private void handleException(Throwable exception) {
		nc.bs.logging.Logger.error(exception.getMessage(), exception);
	}

	@Override
	public void setContext(WaLoginContext paramWaLoginContext) {
		context = paramWaLoginContext;
	}

	@Override
	public WaLoginContext getContext() {
		return context;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getItem() != null) {
			if (e.getItem() instanceof Pair) {
				retCode = (String) ((Pair) e.getItem()).getValue();
			}
		}
	}
}
