package nc.ui.wa.paydata.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import nc.ui.pub.beans.UIDialog;
import javax.swing.SwingWorker;
import javax.swing.SwingUtilities;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IPaydataManageService;
import nc.itf.twhr.ICalculateTWNHI;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.ui.hr.caculate.view.RecacuTypeChooseDialog;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.wa.paydata.model.WadataModelDataManager;
import nc.ui.wa.paydata.view.DateLarborDelDlg;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.pub.WaState;
import nc.vo.hr.caculate.CaculateTypeVO;
import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
/**
 * ������������
 *
 * @author: Ares.Tank
 * @date: 2018-9-19 17:31:59
 * @since: eHR V6.5
 */
public class HealthCaculateAction extends PayDataBaseAction {
	private static final long serialVersionUID = 1L;
	private static final String WA = "wa";

	public HealthCaculateAction() {
		putValue("Code", "HealthCaculateAction");
		setBtnName(ResHelper.getString("60130paydata","60130paydata-001"));

		putValue("ShortDescription",
				ResHelper.getString("60130paydata","60130paydata-001")
						+ "(Ctrl+F)");
		putValue("AcceleratorKey", javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK));
	}

	@Override
	public void doActionForExtend(ActionEvent e) throws Exception {
		//PeriodStateVO periodStateVO = (PeriodStateVO) getEditor().getValue();

		// �AӋ�l������
		DateLarborDelDlg dlg = new DateLarborDelDlg(getEntranceUI(), "�AӋ�l������", "�l������");

		dlg.initUI();

		if(dlg.showModal()== 0){
			putValue("message_after_action",
					ResHelper.getString("60130paydata", "60130paydata-005"));
			return;
		}

		final UFDate payDate = dlg.getdEffectiveDate();
		//String payComment = periodStateVO.getVpaycomment();

		// ssx added on 2015-12-07
		// for SysInit code = TW07
		/*String payDateTWSetting = SysInitQuery.getParaString(this.getContext()
				.getPk_org(), "TWHR07");*/
		if (payDate == null) { // payDateTWSetting.equals("�l������") remarked by
								// ssx on 2017/7/4, for ���������������ưl�����ڲ��ܞ��
			MessageDialog.showErrorDlg(SwingUtilities
					.getWindowAncestor(getWaContext().getEntranceUI()),
					ResHelper.getString("60130paydata", "60130paydata-007"), ResHelper.getString(
									"60130paydata", "60130paydata-006")); // н�Y�l�����ڲ����S��ա�
			return;
		}
		//((WadataModelDataManager)getDataManager()).refresh();
		//getPaydataManager().refreshWithoutItem();
		//refreshWithoutItem();
		
		//����Ͷ���������ص�н����
		//final RecacuTypeChooseDialog chooseDialog = new RecacuTypeChooseDialog(getParentContainer(), WA);
		//chooseDialog.showModal();
	

			new SwingWorker<Boolean, Void>() {
				BannerTimerDialog dialog = new BannerTimerDialog(getParentContainer());
				String error = null;

				@Override
				protected Boolean doInBackground() throws Exception {
					try {
						
						dialog.setStartText("�����������������..."/*@res "н�ʼ�������У����Ե�..."*/);
						dialog.start();
						// ssx added for extend NHI on 2017/7/4
						ICalculateTWNHI nhiSrv = NCLocator.getInstance().lookup(
								ICalculateTWNHI.class);
						nhiSrv.updateExtendNHIInfo(getWaContext().getPk_group(), getWaContext()
								.getPk_org(), getWaContext().getPk_wa_class(), getWaContext()
								.getWaLoginVO().getPk_periodscheme(), getWaContext()
								.getWaLoginVO().getPeriodVO().getPk_wa_period(), payDate);
						
						CaculateTypeVO caculateTypeVO = new CaculateTypeVO();
						caculateTypeVO.setRange(UFBoolean.valueOf("Y"));
						caculateTypeVO.setType(UFBoolean.valueOf("Y"));
						List<SuperVO> payfileVos= getPaydataModel().getData();
						List<SuperVO> resutlVos = new ArrayList<>();
						//ֻ������н�ʼ����ʶ����Щ��,�����ʱû������,���Ժ��ܼ���ȫ��Ͳ�����Ա������Ż�
						for(SuperVO payfileVO : payfileVos){
							if(null != payfileVO){
								if(null != payfileVO.getAttributeValue(PayfileVO.CACULATEFLAG)
										&&((UFBoolean)(payfileVO.getAttributeValue(PayfileVO.CACULATEFLAG))).booleanValue()){
									resutlVos.add(payfileVO);
								}
							}
						}
						//���·����г���refresh�����п�������߳����⡣
						//���˼·��refresh�õ����߳�ִ����Ϻ�
						getPaydataManager().reCalculateRelationWaItem(caculateTypeVO,resutlVos.toArray(new SuperVO[0]));
					}catch (LockFailedException le) {
						error = ResHelper.getString("60130paydata","060130paydata0334")/*@res "��������������������޸ģ�"*/;
					}catch (VersionConflictException le) {
						throw new BusinessException(le.getBusiObject().toString(),le);
					}catch (Exception e) {
						error = e.getMessage();
					} finally {
						dialog.end();
					}
					return Boolean.TRUE;
				}

				/**
				 * @author zhangg on 2010-7-7
				 * @see javax.swing.SwingWorker#done()
				 */
				@Override
				protected void done() {
					if (error != null) {
						ShowStatusBarMsgUtil.showErrorMsg(ResHelper.getString("60130paydata","060130paydata0335")/*@res "������̴��ڴ���"*/,error, getContext());
						//MessageDialog.showErrorDlg(getParentContainer(), null, error);

					} else {
						ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("60130paydata","060130paydata0336")/*@res "��"*/ + dialog.getSecond() + ResHelper.getString("60130paydata","060130paydata0337")/*@res "���ڼ������."*/,
								getContext());
					}
				}
			}.execute();
			//((WadataModelDataManager)getDataManager()).refresh();
			// н����ĿԤ��
			String keyName = ResHelper.getString("60130paydata","060130paydata0331")/*@res "����"*/;
			String[] files = getPaydataManager().getAlterFiles(keyName);
			showAlertInfo(files);
		
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

	@Override
	protected boolean isActionEnable() {
		/*boolean enable = super.isActionEnable();
		if (enable) {
			//�������д�ı�����������
			try {
				enable = !getIsHealthCaculate();

			} catch (BusinessException e) {
				Debug.debug(e.getStackTrace());

				e.printStackTrace();
			}


		}
		//test TODO 
		//return enable;
		return true;*/
		//û��˾Ϳ��Ե�
		if(super.isActionEnable()){
			List<SuperVO> payfileVos= getPaydataModel().getData();
			if (payfileVos != null && payfileVos.size() > 0) {
				for (int i = 0; i < payfileVos.size(); i++) {
					if (!((DataVO)(payfileVos.get(i))).getCheckflag().booleanValue()) {
						return true;
					}
				}
				return false;
			}else{
				return false;
			}
		}
		return false;
	}

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
	}


}
