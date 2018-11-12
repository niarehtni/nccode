package nc.ui.hrwa.wadaysalary.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import bsh.StringUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.hrwa.IWadaysalaryMaintain;
import nc.itf.hrwa.IWadaysalaryQueryService;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.hrwa.wadaysalary.ace.serviceproxy.AceWadaysalaryMaintainProxy;
import nc.ui.hrwa.wadaysalary.model.WadaysalaryAppModel;
import nc.ui.org.alldata.ref.HROrgDefaultRefModel;
import nc.ui.pub.beans.RefEditEvent;
import nc.ui.pub.beans.RefEditListener;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.ui.querytemplate.querytree.QueryScheme;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.uap.rbac.profile.FunctionPermProfileManager;
import nc.vo.wa.paydata.AggDaySalaryVO;
import nc.vo.wa.pub.WaDayLoginContext;
import nc.vo.wa.pub.WaLoginVO;

/**
 * 
 * @author: zhoulp
 * @function ����֯ѡ��Panel: ��֯������
 * @date: 2016��10��31��15:27:20
 */
@SuppressWarnings("serial")
public class WadaysalaryHeadPanel extends UIPanel implements ValueChangedListener,
		AppEventListener {

	private WadaysalaryAppModel model;

	private AceWadaysalaryMaintainProxy service;

	public static String PK_ORG = WaLoginVO.PK_ORG;


	private UILabel hROrgLabel = null;
	private UILabel wadateLabel = null;
	private UIRefPane hROrgDefaultRefPane;
	private UIRefPane wadateRefPane;
	private HROrgDefaultRefModel hROrgDefaultRefModel = null;
	private WaDayLoginContext context;

	public WadaysalaryHeadPanel() {
		super();
	}

	public void initUI() {
		addComponent();
		try {
			initOrgPanel();
		} catch (BusinessException e) {
			Logger.error(e);
		}
	}



	public HROrgDefaultRefModel getHROrgDefaultRefModel() {
		if (hROrgDefaultRefModel == null) {
			hROrgDefaultRefModel = new HROrgDefaultRefModel();
		}
		return hROrgDefaultRefModel;
	}

	@Override
	public void handleEvent(AppEvent event) {
		if (AppEventConst.UISTATE_CHANGED == event.getType()) {
			if (getModel().getUiState() == UIState.ADD
					|| getModel().getUiState() == UIState.EDIT
					|| getModel().getUiState() == UIState.DISABLE) {
				for (Component comp : this.getComponents()) {
					comp.setEnabled(false);
				}
			} else {
				for (Component comp : this.getComponents()) {
					comp.setEnabled(true);
				}
			}
		}
	}

	protected void initLisner() {
		getHROrgDefaultRefPane().addRefEditListener(new RefEditListener() {
			@Override
			public boolean beforeEdit(RefEditEvent e) {
				// �����û���֯Ȩ��
				String[] pkorgs = FunctionPermProfileManager
						.getInstance()
						.getProfile(
								WorkbenchEnvironment.getInstance()
										.getLoginUser().getUser_code())
						.getPermPkorgs();
				getHROrgDefaultRefPane().getRefModel().setFilterPks(pkorgs);
				return true;
			}

		});
		getHROrgDefaultRefPane().addValueChangedListener(this);
		getWadateRefPane().addValueChangedListener(this);
	}

	public void addComponent() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(getHROrgLabelLabel());
		this.add(getHROrgDefaultRefPane());
		this.add(getWadateLabel());
		this.add(getWadateRefPane());
		BillPanelUtils.setButtonPreferredWidth(this);
		initLisner();
	}

	public void initOrgPanel() throws BusinessException {

		String pk_org = getContext().getPk_org();
		if(StringUtils.isEmpty(pk_org)&&!ArrayUtils.isEmpty(getContext().getPkorgs())){
			pk_org=getContext().getPkorgs()[0];
		}
		getContext().setPk_hrorg(pk_org);
		getHROrgDefaultRefPane().setPK(pk_org);
		getWadateRefPane().setText(new UFDate().toString());
	}

	/**
	 * ������Դ��֯��ǩ
	 * 
	 */
	protected UILabel getHROrgLabelLabel() {
		if (hROrgLabel == null) {
			hROrgLabel = new UILabel();
			hROrgLabel.setVisible(true);
			hROrgLabel.setText("������Դ��֯");
			hROrgLabel.setSize(new Dimension(100, 22));
		}
		return hROrgLabel;
	}

	/**
	 * н���ڼ��ǩ
	 * 
	 */
	protected UILabel getWadateLabel() {
		if (wadateLabel == null) {
			wadateLabel = new UILabel();
			wadateLabel.setVisible(true);
			wadateLabel.setText("��н����");
			wadateLabel.setSize(new Dimension(200, 22));
		}
		return wadateLabel;
	}

	/**
	 * ������Դ��֯����
	 * 
	 */
	public UIRefPane getHROrgDefaultRefPane() {
		if (hROrgDefaultRefPane == null) {
			hROrgDefaultRefPane = new UIRefPane();
			hROrgDefaultRefPane.setVisible(true);
			hROrgDefaultRefPane.setPreferredSize(new Dimension(200, 20));
			final UITextField uiTf = hROrgDefaultRefPane.getUITextField();
			uiTf.setShowMustInputHint(uiTf.isEnabled());
			hROrgDefaultRefPane.setButtonFireEvent(true);
			HROrgDefaultRefModel refmodel = getHROrgDefaultRefModel();
			refmodel.setPk_org(getModel().getContext().getPk_group());
			hROrgDefaultRefPane.setRefModel(refmodel);
		}
		return hROrgDefaultRefPane;
	}

	/**
	 * н���ڼ�pane
	 * 
	 */
	public UIRefPane getWadateRefPane() {
		if (wadateRefPane == null) {
			wadateRefPane = new UIRefPane();
			wadateRefPane.setVisible(true);
			wadateRefPane.setPreferredSize(new Dimension(200, 20));
			wadateRefPane.setRefNodeName("����");
		}
		return wadateRefPane;
	}

	public WadaysalaryAppModel getModel() {
		return model;
	}

	public void setModel(WadaysalaryAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public AceWadaysalaryMaintainProxy getService() {
		return service;
	}

	public void setService(AceWadaysalaryMaintainProxy service) {
		this.service = service;
	}

	public WaDayLoginContext getContext() {
		return context;
	}

	public void setContext(WaDayLoginContext context) {
		this.context = context;
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		if (event.getSource() == getHROrgDefaultRefPane()) {
			String hROrg = getHROrgDefaultRefPane().getRefPK() == null ? null
					: getHROrgDefaultRefPane().getRefPK().toString();
			getContext().setPk_hrorg(hROrg);
		}
		if (event.getSource() == getWadateRefPane()) {
			if(null==getWadateRefPane().getValueObj()){
				getContext().setCalculdate(new UFLiteralDate());
				getWadateRefPane().setText(new UFLiteralDate().toString());
			}else{
				String curdate = getWadateRefPane().getValueObj().toString();
				getContext().setCalculdate(new UFLiteralDate(curdate));
			}
			
		}
		//��ʼ��ʱ������ǰ������������
		String condition=" wa_daysalary.pk_hrorg='"+getContext().getPk_hrorg()+"' and wa_daysalary.salarydate='"+getContext().getCalculdate()+"'";
		IWadaysalaryQueryService wadaysalaryQueryService=NCLocator.getInstance().lookup(IWadaysalaryQueryService.class);
		AggDaySalaryVO[] aggDaySalaryVOs = null;
		//Ares.Tank ���ػ��������� 2018��10��19��16:49:28
		/*try {
			aggDaySalaryVOs = wadaysalaryQueryService.queryByCondition(condition);
		} catch (BusinessException e) {
			Logger.error(e);
		}*/
		getModel().initModel(aggDaySalaryVOs);
		
	}

}