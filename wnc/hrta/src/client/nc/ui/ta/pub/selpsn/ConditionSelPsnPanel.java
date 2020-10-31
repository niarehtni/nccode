package nc.ui.ta.pub.selpsn;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.awt.event.MouseListener;

import javax.swing.JViewport;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.border.UITitledBorder;
import nc.ui.querytemplate.QueryConditionEditor;
import nc.ui.querytemplate.candidate.MetaDataCandidatePanelType2;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.filtereditor.DefaultFilterEditor;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.valueeditor.DefaultFieldValueEditor;
import nc.ui.ta.psndoc.ref.TBMPsndocRefModelReturnsPsndoc;
import nc.ui.ta.pub.QueryEditorListener;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.querytemplate.TemplateInfo;

/**
 * ѡ����Ա�����Ĺ���panel
 * 
 * @author zengcheng
 * 
 */
public class ConditionSelPsnPanel extends UIPanel implements AppEventListener {

	private static final long serialVersionUID = 1L;

	private AbstractUIAppModel model;

	public UIPanel uiAddPanel = null;

	private QueryConditionEditor queryEditor = null;

	private String curPk_org;// ��¼��ǰ��֯�������ַ�����������AppEventConst.MODEL_INITIALIZED�¼�����ʱ���ж��Ƿ�����֯�ı��ˣ���Ϊ�������е�AppEventConst.MODEL_INITIALIZED�¼�������֯�ı�

	private String orgPermissionSQL;// ��֯Ȩ�޵�SQL
	private String deptPermissionSQL;// ����Ȩ�޵�SQL
	private String psnclPermissionSQL;// ��Ա���Ȩ�޵�SQL

	public ConditionSelPsnPanel() {
	}

	public ConditionSelPsnPanel(LayoutManager p0) {
		super(p0);
	}

	public ConditionSelPsnPanel(boolean p0) {
		super(p0);
	}

	public ConditionSelPsnPanel(LayoutManager p0, boolean p1) {
		super(p0, p1);
	}

	/**
	 * ���췽�������ô˷������ɴ����ʹ������������init���� �˷����������ǣ� ����ѯ����ص������ϣ������ò�ѯ�����һЩĬ��������������֯
	 */
	public void init() {
		setLayout(new BorderLayout());
		initPermissionSQL();
		add(getQueryEditor(), BorderLayout.CENTER);
		if (getAddPanel() != null)
			add(getAddPanel(), BorderLayout.SOUTH);
	}

	public FromWhereSQL getQuerySQL() {
		return queryEditor.getQueryScheme().getTableJoinFromWhereSQL();
	}

	/**
	 * ��ȡ��ѯ�����༭��
	 * 
	 */
	public QueryConditionEditor getQueryEditor() {
		if (queryEditor == null) {
			TemplateInfo tempInfo = new TemplateInfo();
			tempInfo.setFunNode("60170psndocqry");
			tempInfo.setNodekey("6017psndocqry");
			tempInfo.setCurrentCorpPk(getModel().getContext().getPk_org());
			tempInfo.setPk_Org(getModel().getContext().getPk_org());
			tempInfo.setUserid(getModel().getContext().getPk_loginUser());
			queryEditor = new QueryConditionEditor(tempInfo);
			queryEditor.setVisibleQSTreePanel(false);// ����ʾ��ѯ����
			QueryEditorListener listener = new QueryEditorListener();
			listener.setModel(getModel());
			listener.setQueryConditionEditor(queryEditor);
			queryEditor.showPanel();
			queryEditor.setBorder(new UITitledBorder(ResHelper.getString("6017basedoc", "06017basedoc1476")
			/* @res "��Ա��Χ" */));
		}

		// ssx added on 2020-07-25
		// �������îa�������Ӽ������I���ԃ���ޣ����T���ˆT��ԃ������
		int hasGlbdef8 = -1;
		IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		try {
			hasGlbdef8 = (int) query.executeQuery(
					"select count(glbdef1) from HI_PSNDOC_GLBDEF8 where pk_psndoc = (select pk_psndoc from sm_user where cuserid = '"
							+ getModel().getContext().getPk_loginUser() + "')", new ColumnProcessor());
		} catch (BusinessException e) {
			e.printStackTrace();
		}

		if (hasGlbdef8 > 0) {
			String deptWherePart = "#DEPT_PK# in (select glbdef1 from HI_PSNDOC_GLBDEF8 where pk_psndoc = (select pk_psndoc from sm_user where cuserid = '"
					+ getModel().getContext().getPk_loginUser()
					+ "') and '"
					+ new UFLiteralDate().toString()
					+ "' between BEGINDATE and nvl(ENDDATE, '9999-12-31')) and (select count(pk_dept) from org_dept where pk_dept=#DEPT_PK# and isnull(HRCANCELED, 'N')='N') > 0";

			for (IFilterEditor edt : queryEditor.getSimpleEditorFilterEditors()) {
				IFilter flt = edt.getFilter();
				if (flt.getFilterMeta().getFieldCode().equals("pk_psnjob.pk_org")) {
					((DefaultFilterEditor) edt).setEnable(false);
				} else if (flt.getFilterMeta().getFieldCode().contains("pk_psndoc")) {
					TBMPsndocRefModelReturnsPsndoc refModel = (TBMPsndocRefModelReturnsPsndoc) ((UIRefPane) ((DefaultFieldValueEditor) ((DefaultFilterEditor) edt)
							.getFieldValueEditor()).getFieldValueElemEditor().getFieldValueElemEditorComponent())
							.getRefModel();

					if (refModel != null) {
						refModel.setClassWherePart(deptWherePart.replace("#DEPT_PK#", "orgdept.pk_orgdept"));
					}
				} else if (flt.getFilterMeta().getFieldCode().equals("pk_psnjob.pk_dept")) {
					AbstractRefModel refModel = ((UIRefPane) ((DefaultFieldValueEditor) ((DefaultFilterEditor) edt)
							.getFieldValueEditor()).getFieldValueElemEditor().getFieldValueElemEditorComponent())
							.getRefModel();

					if (refModel != null) {
						refModel.setWherePart(deptWherePart.replace("#DEPT_PK#", "org_dept.pk_dept"));
					}
				}
			}

			((JViewport) ((UIScrollPane) ((MetaDataCandidatePanelType2) queryEditor.getLeftTabbedPane()
					.getSelectedComponent()).getComponent(1)).getComponent(0)).getComponent(0).setEnabled(false);

			MouseListener[] mls = ((JViewport) ((UIScrollPane) ((MetaDataCandidatePanelType2) queryEditor
					.getLeftTabbedPane().getSelectedComponent()).getComponent(1)).getComponent(0)).getComponent(0)
					.getMouseListeners();
			for (MouseListener ml : mls) {
				((JViewport) ((UIScrollPane) ((MetaDataCandidatePanelType2) queryEditor.getLeftTabbedPane()
						.getSelectedComponent()).getComponent(1)).getComponent(0)).getComponent(0).removeMouseListener(
						ml);
			}
		}
		// end

		return queryEditor;
	}

	/**
	 * �������������༭��
	 * 
	 */
	public void resetQueryEditor() {
		remove(queryEditor);
		queryEditor = null;
		add(getQueryEditor(), BorderLayout.CENTER);
	}

	public UIPanel getAddPanel() {
		return uiAddPanel;
	}

	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	/*
	 * �˷���������֯�仯�¼��������ò�ѯ����ϲ��յķ�Χ (non-Javadoc)
	 * 
	 * @see nc.ui.uif2.AppEventListener#handleEvent(nc.ui.uif2.AppEvent)
	 */
	@Override
	public void handleEvent(AppEvent event) {
		if (AppEventConst.MODEL_INITIALIZED.equals(event.getType())) {
			getCurPk_org();
			String pk_org = getModel().getContext().getPk_org();
			if (pk_org != null && curPk_org != null && pk_org.equals(curPk_org))
				return;
			setCurPk_org(pk_org);
			resetQueryEditor();
		}
	}

	/**
	 * ��ʼ��Ȩ��SQL ��ΪȨ��ֻ���û��йأ����ȡһ�μ���
	 */
	protected void initPermissionSQL() {
		// String[] permissionSQLs =
		// DataPermissionUtils.getDataPermissionSQLWherePart(new
		// String[]{"","",""}, new
		// String[]{IRefConst.DATAPOWEROPERATION_CODE,IRefConst.DATAPOWEROPERATION_CODE,IRefConst.DATAPOWEROPERATION_CODE});
		// if(!org.apache.commons.lang.ArrayUtils.isEmpty(permissionSQLs)){
		// orgPermissionSQL = permissionSQLs[0];
		// deptPermissionSQL = permissionSQLs[1];
		// psnclPermissionSQL = permissionSQLs[2];
		// }
	}

	protected String getOrgPermissionSQL() {
		return orgPermissionSQL;
	}

	protected String getDeptPermissionSQL() {
		return deptPermissionSQL;
	}

	protected String getPsnclPermissionSQL() {
		return psnclPermissionSQL;
	}

	public String getCurPk_org() {
		if (curPk_org == null && getModel().getContext().getPk_org() != null)
			this.curPk_org = getModel().getContext().getPk_org();
		return curPk_org;
	}

	public void setCurPk_org(String curPk_org) {
		this.curPk_org = curPk_org;
	}
}