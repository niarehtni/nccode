package nc.bs.hrsms.ta.sss.common;

import nc.bs.hrsms.pub.advpanel.mngdept.MngShopPanel;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.FieldSet;
import nc.uap.lfw.core.data.MdDataset;
import nc.uap.lfw.core.data.UnmodifiableMdField;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.vo.ta.timerule.TimeRuleVO;

public class ShopTaListBasePageModel extends AdvancePageModel {
	// Ĭ�Ͼ���
	public static final int DEFAULT_PRECISION = 2;

	/**
	 * ��ʼ�����Ի�����
	 */
	@Override
	protected void initPageMetaStruct() {

		super.initPageMetaStruct();
		// ��applicationContext��������Կ��ڵ����Ϳ��ڹ���
		//TaAppContextUtil.addTaAppContext();
		// ��Ƭ��
		LfwView viewMain = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		// ���ݿ��ڹ������ÿ������ݵ�Сʱλ��
		setTimeDatasPrecision(viewMain);
	}

	/**
	 * ����������
	 */
	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
		return new IPagePanel[] { new CanvasPanel(), new MngShopPanel(), new SimpleQueryPanel() };
	}

	/**
	 * �����Ҳ����
	 */
	@Override
	protected String getRightPage() {
		return null;
	}

	/**
	 * ���ÿ������ݵ�Сʱλ��<br/>
	 * String[]�����õĿ��������ֶ�����<br/>
	 *
	 * @return
	 */
	protected String[] getTimeDataFields() {
		return null;
	}

	@Override
	protected String getQueryTempletKey() {
		return null;
	}

	/**
	 * ���ݿ��ڹ������ÿ������ݵ�Сʱλ��
	 * 
	 */
	private void setTimeDatasPrecision(LfwView viewMain) {
		// ��������
		String[] timeDatas = getTimeDataFields();
		if (timeDatas == null || timeDatas.length == 0) {
			return;
		}
		Dataset[] dss = viewMain.getViewModels().getDatasets();
		if (dss == null || dss.length == 0) {
			return;
		}
		// ����λ��
		int pointNum = getPointNum();
		for (Dataset ds : dss) {
			if (ds instanceof MdDataset) {
				for (String filedId : timeDatas) {
					int index = ds.getFieldSet().nameToIndex(filedId);
					if (index >= 0) {
						FieldSet fieldSet = ds.getFieldSet();
						Field field = fieldSet.getField(filedId);
						if(field instanceof UnmodifiableMdField) 
							field = ((UnmodifiableMdField) field).getMDField();
						fieldSet.updateField(filedId, field);
						
						field.setPrecision(String.valueOf(pointNum));
						
					}
				}
			}
		}
	}

	/**
	 * ��ÿ���λ��
	 * 
	 * @return
	 */
	private int getPointNum() {
		TimeRuleVO timeRuleVO = TaAppContextUtil.getTimeRuleVO();
		if (timeRuleVO == null) {
			// û�п��ڹ�������������Ĭ��ֵ
			return ShopTaListBasePageModel.DEFAULT_PRECISION;
		}
		int pointNum = Math.abs(timeRuleVO.getTimedecimal());
		return pointNum;
	}

}
