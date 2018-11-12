package nc.ui.wa.paydata.action;


import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.twhr.ICalculateTWNHI;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.ui.wa.paydata.model.WadataModelDataManager;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaState;

/**
 * ��������ȡ������
 *
 * @author: Ares.Tank
 * @date: 2018-9-19 17:31:59
 * @since: eHR V6.5
 */
public class HealthUnCaculateAction extends PayDataBaseAction {
	private static final long serialVersionUID = 1L;

	public HealthUnCaculateAction() {
		putValue("Code", "HealthUnCaculateAction");
		setBtnName(ResHelper.getString("60130paydata","60130paydata-005"));

		putValue("ShortDescription",
				ResHelper.getString("60130paydata","60130paydata-005")
						+ "(Ctrl+K)");
		putValue("AcceleratorKey", javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_K, Event.CTRL_MASK));
	}

	@Override
	public void doActionForExtend(ActionEvent e) throws Exception {



		ICalculateTWNHI nhiSrv = NCLocator.getInstance().lookup(
				ICalculateTWNHI.class);
		nhiSrv.delExtendNHIInfo(getContext().getPk_group(), getContext().getPk_org(), getWaContext().getPk_wa_class()
				,getWaContext().getWaLoginVO().getPeriodVO().getPk_wa_period());
		((WadataModelDataManager)getDataManager()).refresh();
		//
	}
	/**
	 * @author zhangg on 2009-12-1
	 * @see nc.ui.wa.paydata.action.PayDataBaseAction#getEnableStateSet()
	 */

	@Override
	protected boolean isActionEnable() {

		boolean enable = super.isActionEnable();
		if(null == getWaContext().getPk_wa_class() ||
				null == getWaContext().getWaLoginVO().getPeriodVO().getPk_wa_period()||
				null == getWaContext().getPk_org()){
			return false;
		}
		if (enable) {
			//�������д�ı�����������
			try {
				//�����ݾ���ȡ������
				enable = getIsHealthCaculate();

			} catch (BusinessException e) {
				Debug.debug(e.getStackTrace());

				e.printStackTrace();
			}
		}
		return enable;
	}
	/**
	 * н�Y�l����ɲ���Ӌ���������--�a�䱣�M��Ҫ
	 */
	@Override
	public Set<WaState> getEnableStateSet() {
		if (waStateSet == null) {
			waStateSet = new HashSet<WaState>();
			waStateSet.add(WaState.CLASS_WITHOUT_RECACULATED);
			waStateSet.add(WaState.CLASS_RECACULATED_WITHOUT_CHECK);
			waStateSet.add(WaState.CLASS_PART_CHECKED);

		}
		return waStateSet;
	}

	/**
	 *
	 * @author Ares.Tank
	 * @throws BusinessException
	 * @date 2018��9��27�� ����12:52:21
	 * @return pk_declaration
	 * @description
	 */
	/**
	 *
	 * @author Ares.Tank
	 * @throws BusinessException
	 * @date 2018��9��27�� ����12:52:21
	 * @return pk_declaration �Ƿ��Ѿ����ڼ�������
	 * @description
	 */
	public boolean getIsHealthCaculate() throws BusinessException {
		//����������Դ�µ����з�����֯
		Set<String> legalOrgs
			= LegalOrgUtilsEX.getOrgsByLegal(getContext().getPk_org(), getContext().getPk_group());
		int sum = 0;
		for(String pk_org : legalOrgs){
			//��ѯ���������֯���ӱ�����������

			String sqlStr = "select count(*) as sumb from  declaration_business main "
					+ " where main.vbdef1 = '"+pk_org+"'"
					+ " and main.vbdef2 = '"+getWaContext().getPk_wa_class()+"'"
					+ " and main.vbdef3 = '"+getWaContext().getWaLoginVO().getPeriodVO().getPk_wa_period()
					+ "' and dr = 0";
			List<Object> resultList;
			try {
				IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
				resultList = (List) iUAPQueryBS.executeQuery(sqlStr, new ColumnListProcessor());
			} catch (BusinessException e) {
				Debug.debug(e.getMessage());
				resultList = new ArrayList<>();
			}
			for(Object obj : resultList){
				if(null!=obj){
					int result =  (int)obj;
					if(result>=0){
						sum += result;
					}
				}
			}

			sqlStr = "select count(*) as sumb from  declaration_company main "
					+ " where main.vbdef1 = '"+pk_org+"'"
					+ " and main.vbdef2 = '"+getWaContext().getPk_wa_class()+"'"
					+ " and main.vbdef3 = '"+getWaContext().getWaLoginVO().getPeriodVO().getPk_wa_period()
					+ "' and dr = 0";

			try {
				IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
				resultList = (List) iUAPQueryBS.executeQuery(sqlStr, new ColumnListProcessor());
			} catch (BusinessException e) {
				Debug.debug(e.getMessage());
				resultList = new ArrayList<>();
			}
			for(Object obj : resultList){
				if(null!=obj){
					int result =  (int)obj;
					if(result>=0){
						sum += result;
					}
				}
			}
			sqlStr = "select count(*) as sumb from  declaration_nonparttime main "
					+ " where main.vbdef1 = '"+pk_org+"'"
					+ " and main.vbdef2 = '"+getWaContext().getPk_wa_class()+"'"
					+ " and main.vbdef3 = '"+getWaContext().getWaLoginVO().getPeriodVO().getPk_wa_period()
					+ "' and dr = 0";

			try {
				IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
				resultList = (List) iUAPQueryBS.executeQuery(sqlStr, new ColumnListProcessor());
			} catch (BusinessException e) {
				Debug.debug(e.getMessage());
				resultList = new ArrayList<>();
			}
			for(Object obj : resultList){
				if(null!=obj){
					int result =  (int)obj;
					if(result>=0){
						sum += result;
					}
				}
			}
			sqlStr = "select count(*) as sumb from  declaration_parttime main "
					+ " where main.vbdef1 = '"+pk_org+"'"
					+ " and main.vbdef2 = '"+getWaContext().getPk_wa_class()+"'"
					+ " and main.vbdef3 = '"+getWaContext().getWaLoginVO().getPeriodVO().getPk_wa_period()
					+ "' and dr = 0";

			try {
				IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
				resultList = (List) iUAPQueryBS.executeQuery(sqlStr, new ColumnListProcessor());
			} catch (BusinessException e) {
				Debug.debug(e.getMessage());
				resultList = new ArrayList<>();
			}
			for(Object obj : resultList){
				if(null!=obj){
					int result =  (int)obj;
					if(result>=0){
						sum += result;
					}
				}
			}

		}
		if(sum > 0){
			return true;
		}else{
			return false;
		}

	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
		putValue("message_after_action",
				ResHelper.getString("60130paydata", "60130paydata-005"));
	}

}