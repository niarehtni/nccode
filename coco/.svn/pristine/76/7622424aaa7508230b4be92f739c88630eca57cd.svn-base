package nc.bs.hrsms.ta.sss.ShopAttendance.pagemodel;

import nc.bs.hrsms.ta.common.ctrl.BURefController;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.core.refnode.NCRefNode;
import nc.uap.lfw.jsp.uimeta.UIMeta;

public class ShopAttendanceForBatchPageModel extends AdvancePageModel {
	/* 人员信息集 */
//	public static final String DS_PERSON = "dsPerson";
//
//	public static final String DS_DEPT = "dsDept";
	/* 班别变更条件信息集 */
	public static final String DS_CLASS_INFO = "dsChangeClassInfo";

	/* 是否按原班别调整 */
	public static final String FLD_BY_ORI_CLASS = "byOriClass";
	/* 起始日期 */
	public static final String FLD_BEGIN = "begindate";
	/* 终止日期 */
	public static final String FLD_END = "enddate";
	/* 修改时段 */
	public static final String FLD_EDITDATE = "editdate";
	/* 时段状态*/
	public static final String FLD_DATESTATUS = "datestatus";
	/* 是否批量*/
	public static final String ISBATCHEDIT="isBatchEdit";
	/* 新班别 */
	public static final String FLD_NEW_CLASS = "newClass";
	/* 人员基本信息主键 */
	public static final String FLD_PK_PSNDOC = "pk_psndoc";

	/* 调班条件表单 */
	public static final String COMP_FRM_CHANGE_CLASS_INFO = "frmChangeClassInfo";
	/* 调班条件：原班别名称 form element id */
	public static final String COMP_FE_ORI_CLASS_NAME = "oriClassName";
	/* 确认按钮 */
	public static final String COMP_BTN_OK = "btnOK";
	/* 取消按钮 */
	public static final String COMP_BTN_CANCEL = "btnCancel";

	/* 确定按钮的监听器 */
	public static final String LSNR_BTN_OK = "l_btn_ok";
	/* 取消按钮的监听器 */
	public static final String LSNR_BTN_CANCEL = "l_btn_cancel";

	/* 原班别参照 */
	public static final String REF_ORI_CLASS = "refOriClass";
	/* 新班别参照 */
	public static final String REF_NEW_CLASS = "refNewClass";

	/* 父页面的人员信息 */
	public static final String WSES_PSN_KEYS = "psnKeys";

	/*按人员调班页面*/
	public static final String PAGE_PSNLIST_WIDGET = "psnList";

	/*按部门调班页面*/
	public static final String PAGE_DEPTLIST_WIDGET = "deptList";

	/*选中调班的人*/
	public static final String SELECTED_PSN = "selectedPsn";
	/*选中调班的部门*/
	public static final String SELECTED_DEPT = "selectedDept";
	/*pluginID*/
	public static final String PLUGINID_PSN = "psnList";
	/*pluginID*/
	public static final String PLUGINID_DEPT = "deptList";

	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		setShiftRefNode();
	}
	
	/**
	 * 设置班次参照
	 * 
	 */
	public void setShiftRefNode(){
		LfwView widget = LfwRuntimeEnvironment.getWebContext().getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);

		// 班次参照参照控制
		NCRefNode newShiftRfnodeGrade = (NCRefNode) widget.getViewModels().getRefNode("refNewClass");
		newShiftRfnodeGrade.setDataListener(BURefController.class.getName());
		
//		
	}

//	public static void refreshPsnLink() {
//		refreshLinkInfo(DS_PERSON, "lnkPsn", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0029")/*@res "请选择调班人员"*/);
//	}

//	public static void refreshDeptLink() {
//		refreshLinkInfo(DS_DEPT, "lnkDept", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0030")/*@res "请选择调班部门"*/);
//	}

//	private static void refreshLinkInfo( String dsID, String linkID, String defaultLabel) {
//		LfwView wdtMain = LfwRuntimeEnvironment.getWebContext().getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
//		Dataset ds = wdtMain.getViewModels().getDataset(dsID);
//		int rowCount = ds.getRowCount();
//		LinkComp lnk = (LinkComp) wdtMain.getViewComponents().getComponent(linkID);
//		if (0 < rowCount) {
//			String[] names = new String[rowCount];
//			FieldSet fs = ds.getFieldSet();
//			int idx = 0;
//			if(dsID.equals(DS_PERSON)){
//				idx = fs.nameToIndex("pk_psndoc_name");
//			}else{
//				idx = fs.nameToIndex("name");
//			}
//
//			for (int i = 0; i < rowCount; i++) {
//				names[i] = (String) ds.getCurrentRowData().getRow(i).getValue(idx);
//			}
//			String label = StringUtils.join(names, ",");
//			lnk.setI18nName(label);
//		} else {
//			lnk.setI18nName(defaultLabel);
//		}
//
//	}
	
	@Override
	protected String getFunCode() {
		return "E20600977";
	}

	@Override
	protected String getQueryTempletKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getRightPage() {
		// TODO Auto-generated method stub
		return null;
	}

}
