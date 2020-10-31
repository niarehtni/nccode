package nc.ui.hi.psndoc.view;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.HRCMTermUnitUtils;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hi.IPsndocService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.md.MDBaseQueryFacade;
import nc.md.data.access.DASFacade;
import nc.md.data.access.NCObject;
import nc.md.model.IBusinessEntity;
import nc.md.model.IComponent;
import nc.md.model.access.javamap.BeanStyleEnum;
import nc.md.model.type.IEnumType;
import nc.pub.tools.HiCacheUtils;
import nc.pub.tools.HiSQLHelper;
import nc.pub.tools.PinYinHelper;
import nc.pubitf.para.SysInitQuery;
import nc.ref.twhr.refmodel.TWHIFamilyRefModel;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.BatchMatchContext;
import nc.ui.bd.ref.model.RegionDefaultRefTreeModel;
import nc.ui.cp.cpindi.ref.CPindiGradeRefModel;
import nc.ui.hi.psndoc.model.PsndocDataManager;
import nc.ui.hi.psndoc.model.PsndocModel;
import nc.ui.hi.pub.EvalUtils;
import nc.ui.hi.ref.FileNumberRefModel;
import nc.ui.hr.tools.supervalidator.SuperFormEditorValidatorUtil;
import nc.ui.hr.tools.uilogic.FieldRelationUtil;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.hr.uif2.view.HrPsnclTemplateContainer;
import nc.ui.om.ref.HRDeptRefModel;
import nc.ui.om.ref.JobGradeRefModel2;
import nc.ui.om.ref.JobRankRefModel;
import nc.ui.om.ref.PostRefModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.beans.UITable.SortItem;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.beans.table.IMutilSortableTableModel;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillTabbedPane;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.vo.cp.cpindi.CPIndiGradeVO;
import nc.vo.cp.cpindi.CPIndiVO;
import nc.vo.hi.psndoc.AssVO;
import nc.vo.hi.psndoc.CapaVO;
import nc.vo.hi.psndoc.CertVO;
import nc.vo.hi.psndoc.CtrtVO;
import nc.vo.hi.psndoc.KeyPsnVO;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnChgVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.QulifyVO;
import nc.vo.hi.psndoc.ReqVO;
import nc.vo.hi.psndoc.RetireVO;
import nc.vo.hi.psndoc.TrainVO;
import nc.vo.hi.psndoc.TrialVO;
import nc.vo.hi.psndoc.enumeration.TrnseventEnum;
import nc.vo.hr.psnclrule.PsnclinfosetVO;
import nc.vo.hr.validator.CommnonValidator;
import nc.vo.ml.MultiLangUtil;
import nc.vo.om.job.JobVO;
import nc.vo.om.joblevelsys.FilterTypeEnum;
import nc.vo.om.post.PostVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.lang.MultiLangText;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.para.SysInitVO;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

public class PsndocFormEditor extends HrBillFormEditor implements BillCardBeforeEditListener, BillEditListener2,
		FocusListener {
	private PsndocDataManager dataManger;
	private FieldRelationUtil fieldRelationUtil;
	private final String[] strBusiFieldInJobs = { "pk_dept", "pk_post", "pk_job", "series", "pk_jobgrade",
			"pk_jobrank", "pk_postseries" };

	private String strPk_psncl;

	private static final String Is_Foreign_Key = "glbdef7";
	private static final String Residence_Due_Date = "glbdef16";

	private SuperFormEditorValidatorUtil superValidator;

	private final String[] ctrtTrialFlds = { "promonth", "probegindate", "probenddate", "probsalary", "startsalary",
			"prop_unit" };

	private final String[] ctrtFlds = { "termmonth", "begindate", "enddate", "cont_unit" };

	private HashSet<String> hashSubHaveLoad = new HashSet();

	private final String[] fldBlastList = { "ishiskeypsn", PsnJobVO.getDefaultTableName() + "_" + "pk_psndoc",
			PsnJobVO.getDefaultTableName() + "_" + "pk_dept_v", PsnJobVO.getDefaultTableName() + "_" + "pk_org_v",
			"pk_dept_v", "pk_org_v", "pk_psndoc" };

	private int selectedRow = 0;

	private boolean isEditBeginDate;

	private boolean isEditEndDate;

	private IPersistenceRetrieve retrieveService;

	private void afterBodyChange(BillEditEvent evt) {
		try {
			if ((PartTimeVO.getDefaultTableName().equals(evt.getTableCode())) && ("pk_group".equals(evt.getKey()))) {

				clearBodyItemValue(evt.getTableCode(), evt.getRow(), new String[] { "pk_org", "pk_dept" });

				Object obj = getBodyItemValue(evt.getTableCode(), "pk_post", evt.getRow());
				if (obj != null) {
					clearBodyItemValue(evt.getTableCode(), evt.getRow(), new String[] { "pk_post", "pk_postseries",
							"pk_job", "series", "pk_jobgrade", "pk_jobrank" });
				}

			} else if ((ArrayUtils.contains(
					new String[] { PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName() },
					evt.getTableCode()))
					&& ("pk_org".equals(evt.getKey()))) {

				clearBodyItemValue(evt.getTableCode(), evt.getRow(), new String[] { "pk_dept" });

				Object obj = getBodyItemValue(evt.getTableCode(), "pk_post", evt.getRow());
				if (obj != null) {
					clearBodyItemValue(evt.getTableCode(), evt.getRow(), new String[] { "pk_post", "pk_postseries",
							"pk_job", "series", "pk_jobgrade", "pk_jobrank" });
				}

			} else if ((ArrayUtils.contains(
					new String[] { PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName() },
					evt.getTableCode()))
					&& ("pk_dept".equals(evt.getKey()))) {

				Object obj = getBodyItemValue(evt.getTableCode(), "pk_post", evt.getRow());
				if (obj != null) {
					clearBodyItemValue(evt.getTableCode(), evt.getRow(), new String[] { "pk_post", "pk_postseries",
							"pk_job", "series", "pk_jobgrade", "pk_jobrank" });
				}

			} else if ((ArrayUtils.contains(
					new String[] { PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName() },
					evt.getTableCode()))
					&& ("pk_post".equals(evt.getKey()))) {

				String pk_post = getStrValue(evt.getValue());
				PostVO post = pk_post == null ? null : (PostVO) getService().queryByPk(PostVO.class, pk_post, true);
				if (post != null) {
					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_dept", post.getPk_dept());
					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_postseries", post.getPk_postseries());
					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_job", post.getPk_job());
					JobVO jobVO = post.getPk_job() == null ? null : (JobVO) getService().queryByPk(JobVO.class,
							post.getPk_job(), true);
					if (jobVO != null) {
						setBodyValue(evt.getTableCode(), evt.getRow(), "series", jobVO.getPk_jobtype());
					}
					if (post.getEmployment() != null) {
						setBodyValue(evt.getTableCode(), evt.getRow(), "occupation", post.getEmployment());
					}
					if (post.getWorktype() != null) {
						setBodyValue(evt.getTableCode(), evt.getRow(), "worktype", post.getWorktype());
					}

					String defaultlevel = "";
					String defaultrank = "";
					Map<String, String> resultMap = ((IPsndocQryService) NCLocator.getInstance().lookup(
							IPsndocQryService.class)).getDefaultLevelRank(null, null, null, pk_post, null);

					if (!resultMap.isEmpty()) {
						defaultlevel = (String) resultMap.get("defaultlevel");
						defaultrank = (String) resultMap.get("defaultrank");
					}

					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_jobgrade", defaultlevel);
					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_jobrank", defaultrank);
				} else {
					setBodyValue(evt.getTableCode(), evt.getRow(), "series", null);
					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_job", null);
					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_postseries", null);
				}

				if (post == null) {
					BillEditEvent event = new BillEditEvent(getBillCardPanel().getBodyItem(evt.getTableCode(),
							"pk_post"), post == null ? null : post.getPk_job(), "pk_job", evt.getRow(), evt.getPos());

					event.setTableCode(evt.getTableCode());
					afterBodyChange(event);
				}
			} else if ((ArrayUtils.contains(
					new String[] { PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName() },
					evt.getTableCode()))
					&& ("series".equals(evt.getKey()))) {

				String series = getStrValue(evt.getValue());
				String pk_job = (String) getBodyItemValue(evt.getTableCode(), "pk_job_ID", evt.getRow());
				String pk_post = (String) getBodyItemValue(evt.getTableCode(), "pk_post_ID", evt.getRow());
				if ((StringUtils.isBlank(pk_job)) && (StringUtils.isNotBlank(series))) {
					String defaultlevel = "";
					String defaultrank = "";
					Map<String, String> resultMap = ((IPsndocQryService) NCLocator.getInstance().lookup(
							IPsndocQryService.class)).getDefaultLevelRank(series, pk_job, null, pk_post, null);

					if (!resultMap.isEmpty()) {

						defaultlevel = (String) resultMap.get("defaultlevel");
						defaultrank = (String) resultMap.get("defaultrank");
					}

					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_jobgrade", defaultlevel);
					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_jobrank", defaultrank);
				} else if ((StringUtils.isBlank(pk_job)) && (StringUtils.isBlank(series))) {
					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_jobgrade", null);
					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_jobrank", null);
				}
			} else if ((ArrayUtils.contains(
					new String[] { PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName() },
					evt.getTableCode()))
					&& ("pk_postseries".equals(evt.getKey()))) {

				String pk_postseries = getStrValue(evt.getValue());
				String series = (String) getBodyItemValue(evt.getTableCode(), "series_ID", evt.getRow());
				String pk_job = (String) getBodyItemValue(evt.getTableCode(), "pk_job_ID", evt.getRow());
				String pk_post = (String) getBodyItemValue(evt.getTableCode(), "pk_post_ID", evt.getRow());
				if ((StringUtils.isBlank(pk_job)) && (StringUtils.isBlank(series)) && (StringUtils.isBlank(pk_post))
						&& (StringUtils.isNotBlank(pk_postseries))) {

					String defaultlevel = "";
					String defaultrank = "";
					Map<String, String> resultMap = ((IPsndocQryService) NCLocator.getInstance().lookup(
							IPsndocQryService.class)).getDefaultLevelRank(series, pk_job, pk_postseries, pk_post, null);

					if (!resultMap.isEmpty()) {
						defaultlevel = (String) resultMap.get("defaultlevel");
						defaultrank = (String) resultMap.get("defaultrank");
					}

					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_jobgrade", defaultlevel);
					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_jobrank", defaultrank);
				} else if ((StringUtils.isBlank(pk_job)) && (StringUtils.isBlank(series))
						&& (StringUtils.isBlank(pk_post)) && (StringUtils.isBlank(pk_postseries))) {

					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_jobgrade", null);
					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_jobrank", null);
				}
			} else if ((ArrayUtils.contains(
					new String[] { PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName() },
					evt.getTableCode()))
					&& ("pk_job".equals(evt.getKey()))) {

				String pk_job = getStrValue(evt.getValue());
				String pk_post = (String) getBodyItemValue(evt.getTableCode(), "pk_post_ID", evt.getRow());
				JobVO job = pk_job == null ? null : (JobVO) getService().queryByPk(JobVO.class, pk_job, true);
				if (job != null) {
					String defaultlevel = "";
					String defaultrank = "";
					Map<String, String> resultMap = ((IPsndocQryService) NCLocator.getInstance().lookup(
							IPsndocQryService.class)).getDefaultLevelRank(null, pk_job, null, pk_post, null);

					if (!resultMap.isEmpty()) {
						defaultlevel = (String) resultMap.get("defaultlevel");
						defaultrank = (String) resultMap.get("defaultrank");
					}

					setBodyValue(evt.getTableCode(), evt.getRow(), "series", job.getPk_jobtype());
					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_jobgrade", defaultlevel);
					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_jobrank", defaultrank);
				} else {
					setBodyValue(evt.getTableCode(), evt.getRow(), "series", null);
					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_jobgrade", null);
					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_jobrank", null);
				}
			} else if ((ArrayUtils.contains(
					new String[] { PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName() },
					evt.getTableCode()))
					&& ("pk_jobgrade".equals(evt.getKey()))) {

				String pk_jobgrage = getStrValue(evt.getValue());
				if (StringUtils.isNotBlank(pk_jobgrage)) {
					String series = (String) getBodyItemValue(evt.getTableCode(), "series_ID", evt.getRow());
					String pk_job = (String) getBodyItemValue(evt.getTableCode(), "pk_job_ID", evt.getRow());
					String pk_postseries = (String) getBodyItemValue(evt.getTableCode(), "pk_postseries_ID",
							evt.getRow());

					String pk_post = (String) getBodyItemValue(evt.getTableCode(), "pk_post_ID", evt.getRow());
					String defaultrank = "";
					Map<String, String> resultMap = ((IPsndocQryService) NCLocator.getInstance().lookup(
							IPsndocQryService.class)).getDefaultLevelRank(series, pk_job, pk_postseries, pk_post,
							pk_jobgrage);

					if (!resultMap.isEmpty()) {
						defaultrank = (String) resultMap.get("defaultrank");
					}
					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_jobrank", defaultrank);
				} else {
					setBodyValue(evt.getTableCode(), evt.getRow(), "pk_jobrank", null);
				}
			} else if ((ArrayUtils.contains(
					new String[] { PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName() },
					evt.getTableCode()))
					&& ("trnsevent".equals(evt.getKey()))) {

				afterTrnsEventChange(evt);
			} else if ((ReqVO.getDefaultTableName().equals(evt.getTableCode()))
					&& ("pk_postrequire_h".equals(evt.getKey()))) {

				clearBodyItemValue(evt.getTableCode(), evt.getRow(), new String[] { "pk_postrequire_b" });
			} else if (CapaVO.getDefaultTableName().equals(evt.getTableCode())) {
				if ("pk_pe_indi".equals(evt.getKey())) {

					clearBodyItemValue(evt.getTableCode(), evt.getRow(), new String[] { "pk_pe_scogrditem" });
					if (evt.getValue() == null) {
						clearBodyItemValue(evt.getTableCode(), evt.getRow(), new String[] { "indicode" });
						clearBodyItemValue(evt.getTableCode(), evt.getRow(), new String[] { "pk_indi_type" });
						clearBodyItemValue(evt.getTableCode(), evt.getRow(), new String[] { "scorestandard" });
					} else {
						CPIndiVO indi = (CPIndiVO) ((IPersistenceRetrieve) NCLocator.getInstance().lookup(
								IPersistenceRetrieve.class))
								.retrieveByPk(null, CPIndiVO.class, (String) evt.getValue());

						setBodyValue(evt.getTableCode(), evt.getRow(), "indicode", indi.getIndicode());
						setBodyValue(evt.getTableCode(), evt.getRow(), "pk_indi_type", indi.getPk_indi_type());
						setBodyValue(evt.getTableCode(), evt.getRow(), "scorestandard", indi.getScorestandard());
					}
				} else if ("pk_pe_scogrditem".equals(evt.getKey())) {
					if (evt.getValue() == null) {
						clearBodyItemValue(evt.getTableCode(), evt.getRow(), new String[] { "score" });
					} else {
						CPIndiGradeVO grade = (CPIndiGradeVO) ((IPersistenceRetrieve) NCLocator.getInstance().lookup(
								IPersistenceRetrieve.class)).retrieveByPk(null, CPIndiGradeVO.class,
								(String) evt.getValue());

						setBodyValue(evt.getTableCode(), evt.getRow(), "score", grade.getGradeseq());
					}
				}
			} else if ((TrialVO.getDefaultTableName().equals(evt.getTableCode()))
					&& ("trialresult".equals(evt.getKey()))) {

				Integer trialResult = (Integer) evt.getValue();
				int rowCount = getBillCardPanel().getBillTable(TrialVO.getDefaultTableName()).getRowCount();
				int editRow = evt.getRow();
				if ((editRow < rowCount - 1) && ((trialResult == null) || (trialResult.intValue() == 2))) {

					MessageDialog.showWarningDlg(getModel().getContext().getEntranceUI(), null,
							ResHelper.getString("6007psn", "06007psn0164"));

					getBillCardPanel().getBillModel(TrialVO.getDefaultTableName()).setValueAt(evt.getOldValue(),
							evt.getRow(), "trialresult");

					return;
				}

				getBillCardPanel().getBillModel(TrialVO.getDefaultTableName()).setValueAt(
						UFBoolean.valueOf((trialResult != null)
								&& ((trialResult.intValue() == 1) || (trialResult.intValue() == 3))), evt.getRow(),
						"endflag");

			} else if (CtrtVO.getDefaultTableName().equals(evt.getTableCode())) {
				afterCtrtEdit(evt);
			} else if (PartTimeVO.getDefaultTableName().equals(evt.getTableCode())) {
				if ("endflag".equals(evt.getKey())) {

					Boolean endflag = (Boolean) evt.getValue();
					if ((endflag != null) && (endflag.booleanValue())) {
						getBillCardPanel().getBillModel(evt.getTableCode()).setValueAt(UFBoolean.FALSE, evt.getRow(),
								"poststat");
					}
				}
				// by he
			} else if ("hi_psndoc_courtdeduction".equals(evt.getTableCode()) && "courtdeductways".equals(evt.getKey())) {
				if ("courtdeductways".equals(evt.getKey())) {
					setBodyValue(evt.getTableCode(), evt.getRow(), "monthexecutrate", null);
					setBodyValue(evt.getTableCode(), evt.getRow(), "mindeductcountry", null);
					setBodyValue(evt.getTableCode(), evt.getRow(), "monthexecutamount", null);
				} else if ("minimumlifeways".equals(evt.getKey())) {
					Object value = getBodyItemValue(evt.getTableCode(), "minimumlifeways", evt.getRow());
					if (value != null) {
						setBodyValue(evt.getTableCode(), evt.getRow(), "monthexecutrate", 1);
					}

				}

			} else if (PsndocDefTableUtil.getPsnLaborTablename().equals(evt.getTableCode())) {
				if ("begindate".equals(evt.getKey())) {
					setBodyValue(evt.getTableCode(), evt.getRow(), "glbdef10", UFBoolean.TRUE); // 是否劳保投保
					setBodyValue(evt.getTableCode(), evt.getRow(), "glbdef11", UFBoolean.TRUE); // 是否劳退投保
				}
			} else if (PsndocDefTableUtil.getPsnHealthTablename().equals(evt.getTableCode())) {
				if ("begindate".equals(evt.getKey())) {
					setBodyValue(evt.getTableCode(), evt.getRow(), "glbdef14", UFBoolean.TRUE); // 是否投保
				}
			}

			getBodyItemValue(evt.getTableCode(), "pk_jobgrade_ID", evt.getRow());
			String series = (String) getBodyItemValue(evt.getTableCode(), "series", evt.getRow());
			String postseries = (String) getBodyItemValue(evt.getTableCode(), "pk_postseries", evt.getRow());
			String pk_job = (String) getBodyItemValue(evt.getTableCode(), "pk_job", evt.getRow());
			String pk_post = (String) getBodyItemValue(evt.getTableCode(), "pk_post", evt.getRow());
			if ((StringUtils.isBlank(series)) && (StringUtils.isBlank(postseries)) && (StringUtils.isBlank(pk_job))
					&& (StringUtils.isBlank(pk_post))) {

				setBodyItemEdit(evt.getTableCode(), evt.getRow(), false, new String[] { "pk_jobgrade" });
			} else {
				setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, new String[] { "pk_jobgrade" });
				setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, new String[] { "pk_job" });
			}
			getBillCardPanel().getBillModel(evt.getTableCode()).loadLoadRelationItemValue();

			getBodyItemValue(evt.getTableCode(), "pk_jobgrade_ID", evt.getRow());
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
	}

	private void afterCtrtEdit(BillEditEvent evt) {
		if ((ArrayUtils.contains(ctrtTrialFlds, evt.getKey())) || ("ifprop".equals(evt.getKey()))) {
			String unitName = (String) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(),
					"prop_unit");
			Integer trialtype = HRCMTermUnitUtils.getTermUnit(unitName);

			trialtype = Integer.valueOf(trialtype == null ? HRCMTermUnitUtils.TERMUNIT_MONTH : trialtype.intValue());

			float days = HRCMTermUnitUtils.getDaysByUnit(trialtype.intValue());
			UFLiteralDate begindate = (UFLiteralDate) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(
					evt.getRow(), "probegindate");

			UFLiteralDate enddate = (UFLiteralDate) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(
					evt.getRow(), "probenddate");

			Integer promonth = (Integer) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(),
					"promonth");
			if ("ifprop".equals(evt.getKey())) {
				if (!((Boolean) evt.getValue()).booleanValue()) {
					clearBodyItemValue(evt.getTableCode(), evt.getRow(), ctrtTrialFlds);
				} else {
					getBillCardPanel().getBillModel(CtrtVO.getDefaultTableName()).setValueAt(
							Integer.valueOf(HRCMTermUnitUtils.TERMUNIT_MONTH), evt.getRow(), "prop_unit");

					getBillCardPanel().getBodyItem(CtrtVO.getDefaultTableName(), "promonth").setLength(
							HRCMTermUnitUtils.TERMUNIT_MONTH_LENGTH);
				}

			} else if (evt.getKey().equals("promonth")) {
				if (promonth == null) {
					if ((begindate != null) && (enddate != null)) {
						getBillCardPanel().getBillModel().setValueAt(
								Integer.valueOf(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days)),
								evt.getRow(), "promonth");
					}

				} else if (begindate != null) {
					getBillCardPanel().getBillModel().setValueAt(
							HRCMTermUnitUtils.getDateAfterMonth(begindate, promonth.intValue(), trialtype),
							evt.getRow(), "probenddate");

				} else if (enddate != null) {

					getBillCardPanel().getBillModel().setValueAt(
							HRCMTermUnitUtils.getDateBeforeMonth(begindate, promonth.intValue(), trialtype),
							evt.getRow(), "probegindate");
				}

			} else if (evt.getKey().equals("probegindate")) {
				if (promonth != null) {
					getBillCardPanel().getBillModel().setValueAt(
							HRCMTermUnitUtils.getDateAfterMonth(begindate, promonth.intValue(), trialtype),
							evt.getRow(), "probenddate");

				} else if (enddate != null) {
					getBillCardPanel().getBillModel().setValueAt(
							Integer.valueOf(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days)),
							evt.getRow(), "promonth");
				}

			} else if (evt.getKey().equals("probenddate")) {
				if (enddate == null) {
					if ((begindate != null) && (promonth != null)) {
						getBillCardPanel().getBillModel().setValueAt(
								HRCMTermUnitUtils.getDateAfterMonth(begindate, promonth.intValue(), trialtype),
								evt.getRow(), "probenddate");
					}

				} else if (begindate != null) {
					getBillCardPanel().getBillModel().setValueAt(
							Integer.valueOf(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days)),
							evt.getRow(), "promonth");

				} else {
					getBillCardPanel().getBillModel().setValueAt(
							HRCMTermUnitUtils.getDateBeforeMonth(enddate, promonth.intValue(), trialtype),
							evt.getRow(), "probegindate");
				}

			} else if (evt.getKey().equals("prop_unit")) {

				Integer termUnit = (Integer) evt.getValue();
				getBillCardPanel().getBodyItem(CtrtVO.getDefaultTableName(), "promonth").setLength(
						HRCMTermUnitUtils.getLengthByTermUnit(termUnit.intValue()).intValue());

				if (promonth == null) {
					if ((begindate != null) && (enddate != null)) {
						getBillCardPanel().getBillModel().setValueAt(
								Integer.valueOf(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days)),
								evt.getRow(), "promonth");
					}

				} else if (begindate == null) {
					if ((enddate != null) && (promonth != null)) {
						getBillCardPanel().getBillModel().setValueAt(
								HRCMTermUnitUtils.getDateBeforeMonth(enddate, promonth.intValue(), trialtype),
								evt.getRow(), "probegindate");
					}

				} else {
					getBillCardPanel().getBillModel().setValueAt(
							HRCMTermUnitUtils.getDateAfterMonth(begindate, promonth.intValue(), trialtype),
							evt.getRow(), "probenddate");
				}

			}
		} else if ((ArrayUtils.contains(ctrtFlds, evt.getKey())) || ("termtype".equals(evt.getKey()))) {
			String unitName = (String) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(),
					"cont_unit");
			Integer trialtype = HRCMTermUnitUtils.getTermUnit(unitName);

			trialtype = Integer.valueOf(trialtype == null ? HRCMTermUnitUtils.TERMUNIT_MONTH : trialtype.intValue());

			float days = HRCMTermUnitUtils.getDaysByUnit(trialtype.intValue());
			UFLiteralDate begindate = (UFLiteralDate) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(
					evt.getRow(), "begindate");

			UFLiteralDate enddate = (UFLiteralDate) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(
					evt.getRow(), "enddate");

			Integer termmonth = (Integer) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(),
					"termmonth");

			if ("termtype".equals(evt.getKey())) {
				refreshTermTypeStat(evt);
				if ((termmonth != null) && (begindate != null)) {
					getBillCardPanel().getBillModel().setValueAt(
							HRCMTermUnitUtils.getDateAfterMonth(begindate, termmonth.intValue(), trialtype),
							evt.getRow(), "enddate");
				}

			} else if (evt.getKey().equals("termmonth")) {
				if (termmonth == null) {
					if ((begindate != null) && (enddate != null)) {
						getBillCardPanel().getBillModel().setValueAt(
								Integer.valueOf(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days)),
								evt.getRow(), "termmonth");
					}

				} else if (begindate != null) {

					getBillCardPanel().getBillModel().setValueAt(
							HRCMTermUnitUtils.getDateAfterMonth(begindate, termmonth.intValue(), trialtype),
							evt.getRow(), "enddate");

				} else if (enddate != null) {

					getBillCardPanel().getBillModel().setValueAt(
							HRCMTermUnitUtils.getDateBeforeMonth(enddate, termmonth.intValue(), trialtype),
							evt.getRow(), "begindate");
				}

			} else if (evt.getKey().equals("begindate")) {
				if (termmonth != null) {
					if (enddate == null) {
						getBillCardPanel().getBillModel().setValueAt(
								HRCMTermUnitUtils.getDateAfterMonth(begindate, termmonth.intValue(), trialtype),
								evt.getRow(), "enddate");

					} else if ((begindate != null)
							&& ((begindate.beforeDate(enddate)) || (begindate.compareTo(enddate) == 0))) {
						getBillCardPanel().getBillModel().setValueAt(
								Integer.valueOf(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days)),
								evt.getRow(), "termmonth");

					} else {
						MessageDialog.showWarningDlg(this, null, ResHelper.getString("6007psn", "06007psn0351"));
						getBillCardPanel().getBillModel().setValueAt(null, evt.getRow(), "begindate");
					}

				} else if (enddate != null) {
					getBillCardPanel().getBillModel().setValueAt(
							Integer.valueOf(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days)),
							evt.getRow(), "termmonth");
				}

			} else if (evt.getKey().equals("enddate")) {
				if (enddate == null) {
					if ((begindate == null) || (termmonth == null)) {
					}

				} else if (begindate != null) {
					if (termmonth == null) {
						getBillCardPanel().getBillModel().setValueAt(
								Integer.valueOf(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days)),
								evt.getRow(), "termmonth");
					}

				} else {
					getBillCardPanel().getBillModel().setValueAt(
							HRCMTermUnitUtils.getDateBeforeMonth(enddate, termmonth.intValue(), trialtype),
							evt.getRow(), "begindate");
				}

			} else if (evt.getKey().equals("cont_unit")) {

				Integer termUnit = (Integer) evt.getValue();
				getBillCardPanel().getBodyItem(CtrtVO.getDefaultTableName(), "termmonth").setLength(
						HRCMTermUnitUtils.getLengthByTermUnit(termUnit.intValue()).intValue());

				if (termmonth == null) {
					if ((begindate != null) && (enddate != null)) {
						getBillCardPanel().getBillModel().setValueAt(
								Integer.valueOf(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days)),
								evt.getRow(), "termmonth");
					}

				} else if (begindate == null) {
					if ((enddate != null) && (termmonth != null)) {
						getBillCardPanel().getBillModel().setValueAt(
								HRCMTermUnitUtils.getDateBeforeMonth(enddate, termmonth.intValue(), trialtype),
								evt.getRow(), "begindate");
					}

				} else {
					getBillCardPanel().getBillModel().setValueAt(
							HRCMTermUnitUtils.getDateAfterMonth(begindate, termmonth.intValue(), trialtype),
							evt.getRow(), "enddate");
				}
			}
		}
	}

	private void refreshTermTypeStat(BillEditEvent evt) {
		String termtype = (String) evt.getValue();

		if ("CM02".equals(termtype)) {
			clearBodyItemValue(evt.getTableCode(), evt.getRow(), new String[] { "termmonth", "enddate" });
			setBodyItemEdit(evt.getTableCode(), evt.getRow(), false, new String[] { "termmonth", "enddate" });
			setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, new String[] { "ifprop" });
		} else if ("CM03".equals(termtype)) {
			setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, new String[] { "termmonth", "enddate" });
			setBodyItemEdit(evt.getTableCode(), evt.getRow(), false, new String[] { "ifprop" });
			getBillCardPanel().getBillModel(evt.getTableCode()).setValueAt(UFBoolean.FALSE, evt.getRow(), "ifprop");
			clearBodyItemValue(evt.getTableCode(), evt.getRow(), ctrtTrialFlds);
		} else {
			setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, new String[] { "termmonth", "enddate", "ifprop" });
		}
	}

	private void setBodyItemEdit(String strTabCode, int iRowIndex, boolean isEdit, String... strBodyItemKeys) {
		if ((strBodyItemKeys == null) || (strBodyItemKeys.length == 0)) {
			return;
		}
		BillModel billModel = strTabCode == null ? getBillCardPanel().getBillModel() : getBillCardPanel().getBillModel(
				strTabCode);
		if (billModel == null) {
			return;
		}
		for (String strItemKey : strBodyItemKeys) {
			billModel.setCellEditable(iRowIndex, strItemKey, isEdit);
		}
	}

	public void afterEdit(BillEditEvent evt) {
		if (IBillItem.HEAD == evt.getPos()) {
			afterHeadChange(evt);
		} else if (IBillItem.BODY == evt.getPos()) {
			afterBodyChange(evt);

			BillItem item = this.getBillCardPanel().getBodyItem(evt.getTableCode(), evt.getKey());
			if (item != null) {
				// enddate->dateadd( begindate, glbdef1, "D")
				this.getBillCardPanel().getBillModel(evt.getTableCode())
						.execFormula(evt.getRow(), item.getEditFormulas());
				// this.getBillCardPanel().execBodyFormulas(evt.getRow(),
				// item.getEditFormulas());
			}
			// ssx added for Taiwan NHI on 2017-12-20
			try {
				if (PsndocDefTableUtil.getGroupInsuranceTablename() == null ? false : PsndocDefTableUtil
						.getGroupInsuranceTablename().endsWith(evt.getTableCode())) {
					// ssx added for Group Insurance on 2017-09-13
					if (evt.getKey().equals("glbdef4")) { // 投保身份
						this.getBillCardPanel().getBillModel().setValueAt(null, evt.getRow(), "glbdef5"); // 投保身份编辑后清空投保险种
						this.getBillCardPanel().getBillModel().setValueAt(null, evt.getRow(), "insurancecompany_ID"); // 投保身份编辑后清空保险公司
						this.getBillCardPanel().getBillModel().setValueAt(null, evt.getRow(), "insurancecompany");
					} else if (evt.getKey().equals("glbdef5")) {
						// 投保险种编辑后，刷新保险公司
						String pk_grprel = (String) this.getBillCardPanel().getBillModel()
								.getValueAt(evt.getRow(), "glbdef4_ID");
						String pk_grpins = (String) this.getBillCardPanel().getBillModel()
								.getValueAt(evt.getRow(), "glbdef5_ID");

						if (!StringUtils.isEmpty(pk_grpins) && !StringUtils.isEmpty(pk_grprel)) {
							IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
							String inscompany = (String) query.executeQuery(
									"select insurancecompany from twhr_groupinsurancesetting where cgrpinsid = '"
											+ pk_grpins + "' and cgrpinsrelid = '" + pk_grprel + "'",
									new ColumnProcessor());
							String inscompanyname = (String) query.executeQuery(
									"select name from bd_defdoc where pk_defdoc = '" + inscompany + "'",
									new ColumnProcessor());
							this.getBillCardPanel().getBillModel()
									.setValueAt(inscompany, evt.getRow(), "insurancecompany_ID");
							this.getBillCardPanel().getBillModel()
									.setValueAt(inscompanyname, evt.getRow(), "insurancecompany");
						} else {
							this.getBillCardPanel().getBillModel()
									.setValueAt(null, evt.getRow(), "insurancecompany_ID"); // 投保身份编辑后清空保险公司
							this.getBillCardPanel().getBillModel().setValueAt(null, evt.getRow(), "insurancecompany");
						}
					}
				}
			} catch (BusinessException e) {
				Logger.error(e.getMessage());
			}
		}
		super.afterEdit(evt);

	}

	private void afterHeadChange(BillEditEvent evt) {
		// mod by Connie.ZH
		// 2019-05-28 started
		// set residence due date column's state after is foreign edited
		if (Is_Foreign_Key.equals(evt.getKey())) {
			// ssx modified on 2019-12-01
			// 外籍人士自動帶出扣稅核算入境日期為到職日
			// 居留證到期日不可以必輸，因為入職時居留證還沒有發
			// getBillCardPanel().getHeadItem(Residence_Due_Date).setNull((Boolean)
			// evt.getValue());
			getBillCardPanel().getHeadItem(Residence_Due_Date).setEnabled((Boolean) evt.getValue());
			getBillCardPanel().getHeadItem("glbdef9").setEnabled((Boolean) evt.getValue());
			if (!(Boolean) evt.getValue()) {
				getBillCardPanel().getHeadItem(Residence_Due_Date).setValue(null);
				getBillCardPanel().getHeadItem("glbdef9").setValue(null);
			} else {
				getBillCardPanel().getHeadItem("glbdef9").setValue(
						getBillCardPanel().getHeadItem("hi_psnorg_joinsysdate").getValueObject());
			}
			// end ssx
			return;
		}
		// 2019-05-28 ended
		try {
			if ("name".equals(evt.getKey())) {
				MultiLangText multiLangText = (MultiLangText) evt.getValue();
				BillItem item = getBillCardPanel().getHeadItem("shortname");
				if ((item != null) && (multiLangText != null)) {
					item.setValue(PinYinHelper.getPinYinHeadChar(multiLangText.getText(multiLangText.getCurrLangIndex())));
				}
			} else if ((PsnJobVO.getDefaultTableName() + "_" + "pk_org").equals(evt.getKey())) {

				clearHeadItemValue(new String[] { PsnJobVO.getDefaultTableName() + "_" + "pk_dept" });

				Object obj = getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_post");
				if (obj != null) {
					clearHeadItemValue(new String[] { PsnJobVO.getDefaultTableName() + "_" + "pk_post",
							PsnJobVO.getDefaultTableName() + "_" + "pk_postseries",
							PsnJobVO.getDefaultTableName() + "_" + "pk_job",
							PsnJobVO.getDefaultTableName() + "_" + "series",
							PsnJobVO.getDefaultTableName() + "_" + "pk_jobgrade",
							PsnJobVO.getDefaultTableName() + "_" + "pk_jobrank" });

				}

			} else if ((PsnJobVO.getDefaultTableName() + "_" + "pk_dept").equals(evt.getKey())) {
				// String dept = evt.getValue() == null ? "" : ((String[])
				// evt.getValue())[0];
				// by he
				// Boolean isSupervisor = (Boolean)
				// getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" +
				// "jobglbdef2");
				// setHeadItemValue("glbdef11", getCluster(dept));
				// ssx remarked on 2020-06-08
				// 取消直屬主管自動賦值
				// setHeadItemValue("hi_psnjob_jobglbdef9", getPrincipal(dept,
				// isSupervisor));
				// end
				Object obj = getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_post");
				if (obj != null) {
					clearHeadItemValue(new String[] { PsnJobVO.getDefaultTableName() + "_" + "pk_post",
							PsnJobVO.getDefaultTableName() + "_" + "pk_job",
							PsnJobVO.getDefaultTableName() + "_" + "series",
							PsnJobVO.getDefaultTableName() + "_" + "pk_jobgrade",
							PsnJobVO.getDefaultTableName() + "_" + "pk_jobrank",
							PsnJobVO.getDefaultTableName() + "_" + "pk_postseries" });

				}

			} else if ((PsnJobVO.getDefaultTableName() + "_" + "pk_post").equals(evt.getKey())) {

				String pk_post = getStrValue(evt.getValue());
				PostVO post = pk_post == null ? null : (PostVO) getService().queryByPk(PostVO.class, pk_post, true);
				if (post != null) {
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_dept", post.getPk_dept());
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_postseries", post.getPk_postseries());
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_job", post.getPk_job());
					JobVO jobVO = post.getPk_job() == null ? null : (JobVO) getService().queryByPk(JobVO.class,
							post.getPk_job(), true);
					if (jobVO != null) {
						setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "series", jobVO.getPk_jobtype());
					}
					if (post.getEmployment() != null) {
						setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "occupation", post.getEmployment());
					}
					if (post.getWorktype() != null) {
						setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "worktype", post.getWorktype());
					}

					String defaultlevel = "";
					String defaultrank = "";
					Map<String, String> resultMap = ((IPsndocQryService) NCLocator.getInstance().lookup(
							IPsndocQryService.class)).getDefaultLevelRank(null, null, null, pk_post, null);

					if (!resultMap.isEmpty()) {
						defaultlevel = (String) resultMap.get("defaultlevel");
						defaultrank = (String) resultMap.get("defaultrank");
					}

					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobgrade", defaultlevel);
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobrank", defaultrank);

					getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "pk_jobgrade").setEnabled(
							true);

					getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "pk_jobrank")
							.setEnabled(true);
					getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "pk_postseries").setEnabled(
							false);
				} else {
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_postseries", null);
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_job", null);
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobgrade", null);
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobrank", null);
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "series", null);
					getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "pk_job").setEnabled(true);
					getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "pk_postseries").setEnabled(
							true);
				}

				if (post == null) {
					BillEditEvent event = new BillEditEvent(getBillCardPanel().getHeadItem(
							PsnJobVO.getDefaultTableName() + "_" + "pk_job"), post == null ? null : post.getPk_job(),
							PsnJobVO.getDefaultTableName() + "_" + "pk_job", evt.getRow(), evt.getPos());

					event.setTableCode(evt.getTableCode());
					afterHeadChange(event);
				}
			} else if ((PsnJobVO.getDefaultTableName() + "_" + "pk_job").equals(evt.getKey())) {

				String pk_job = getStrValue(evt.getValue());
				String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_post");
				JobVO job = pk_job == null ? null : (JobVO) getService().queryByPk(JobVO.class, pk_job, true);
				if (job != null) {
					String defaultlevel = "";
					String defaultrank = "";
					Map<String, String> resultMap = ((IPsndocQryService) NCLocator.getInstance().lookup(
							IPsndocQryService.class)).getDefaultLevelRank(null, pk_job, null, pk_post, null);

					if (!resultMap.isEmpty()) {
						defaultlevel = (String) resultMap.get("defaultlevel");
						defaultrank = (String) resultMap.get("defaultrank");
					}

					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "series", job.getPk_jobtype());
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobgrade", defaultlevel);
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobrank", defaultrank);

					getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "series").setEnabled(false);
				} else {
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "series", null);
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobgrade", null);
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobrank", null);
					getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "series").setEnabled(true);
				}
				getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "pk_jobrank").setEnabled(true);
				getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "pk_jobgrade").setEnabled(true);
			} else if ((PsnJobVO.getDefaultTableName() + "_" + "series").equals(evt.getKey())) {

				String series = getStrValue(evt.getValue());
				String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_job");
				String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_post");
				if ((StringUtils.isBlank(pk_job)) && (StringUtils.isNotBlank(series))) {
					String defaultlevel = "";
					String defaultrank = "";
					Map<String, String> resultMap = ((IPsndocQryService) NCLocator.getInstance().lookup(
							IPsndocQryService.class)).getDefaultLevelRank(series, pk_job, null, pk_post, null);

					if (!resultMap.isEmpty()) {
						defaultlevel = (String) resultMap.get("defaultlevel");
						defaultrank = (String) resultMap.get("defaultrank");
					}

					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobgrade", defaultlevel);
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobrank", defaultrank);
				} else if ((StringUtils.isBlank(pk_job)) && (StringUtils.isBlank(series))) {
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobgrade", null);
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobrank", null);
				}
			} else if ((PsnJobVO.getDefaultTableName() + "_" + "pk_postseries").equals(evt.getKey())) {

				String pk_postseries = getStrValue(evt.getValue());
				String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_job");
				String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_post");
				String series = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "series");
				if ((StringUtils.isBlank(pk_job)) && (StringUtils.isBlank(series)) && (StringUtils.isBlank(pk_post))
						&& (StringUtils.isNotBlank(pk_postseries))) {

					String defaultlevel = "";
					String defaultrank = "";
					Map<String, String> resultMap = ((IPsndocQryService) NCLocator.getInstance().lookup(
							IPsndocQryService.class)).getDefaultLevelRank(series, pk_job, pk_postseries, pk_post, null);

					if (!resultMap.isEmpty()) {
						defaultlevel = (String) resultMap.get("defaultlevel");
						defaultrank = (String) resultMap.get("defaultrank");
					}

					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobgrade", defaultlevel);
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobrank", defaultrank);
				} else if ((StringUtils.isBlank(pk_job)) && (StringUtils.isBlank(series))
						&& (StringUtils.isBlank(pk_post)) && (StringUtils.isBlank(pk_postseries))) {

					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobgrade", null);
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobrank", null);
				}
			} else if ((PsnJobVO.getDefaultTableName() + "_" + "pk_jobgrade").equals(evt.getKey())) {

				String pk_jobgrage = getStrValue(evt.getValue());
				if (StringUtils.isNotBlank(pk_jobgrage)) {
					String pk_postseries = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_"
							+ "pk_postseries");
					String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_job");
					String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_post");
					String series = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "series");
					String defaultrank = "";
					Map<String, String> resultMap = ((IPsndocQryService) NCLocator.getInstance().lookup(
							IPsndocQryService.class)).getDefaultLevelRank(series, pk_job, pk_postseries, pk_post,
							pk_jobgrage);

					if (!resultMap.isEmpty()) {
						defaultrank = (String) resultMap.get("defaultrank");
					}
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobrank", defaultrank);

				} else {
					setHeadValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobrank", null);
					getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "pk_jobrank")
							.setEnabled(true);
				}
			} else if ((PsnJobVO.getDefaultTableName() + "_" + "trnsevent").equals(evt.getKey())) {

				Object objValue = getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "trnsevent");
				BillItem item = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "trnstype");
				if (item != null) {
					item.clearViewData();
					((UIRefPane) item.getComponent()).getRefModel().setWherePart("trnsevent=" + objValue);
				}
			} else if ((PsnJobVO.getDefaultTableName() + "_" + "endflag").equals(evt.getKey())) {

				Object objValue = getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "endflag");
				BillItem item = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "poststat");
				if (item != null) {
					item.setValue(UFBoolean.valueOf(!((Boolean) objValue).booleanValue()));
				}
				item = getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + "endflag");
				if (item != null) {
					item.setValue(objValue);
				}
			} else if ((PsnJobVO.getDefaultTableName() + "_" + "trial_flag").equals(evt.getKey())) {

				Object objValue = getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "trial_flag");
				BillItem item = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "trial_type");
				if (item != null) {
					item.clearViewData();
					item.setEdit((objValue instanceof Boolean) ? ((Boolean) objValue).booleanValue() : false);
				}

			} else if ("id".equals(evt.getKey())) {

				Object obj = getHeadItemValue("id");

				Object objidtype = getHeadItemValue("idtype");
				HashMap<String, Object> map = generateGenderAndBirthdayFromID((String) obj, (String) objidtype);
				if (map != null) {
					setHeadItemValue("sex", map.get("sex"));
					setHeadItemValue("birthdate", map.get("birthday"));
				}
			} else if ("idtype".equals(evt.getKey())) {

				Object obj = getHeadItemValue("idtype");

				Object objid = getHeadItemValue("id");
				HashMap<String, Object> map = generateGenderAndBirthdayFromID((String) objid, (String) obj);
				if (map != null) {
					setHeadItemValue("sex", map.get("sex"));
					setHeadItemValue("birthdate", map.get("birthday"));
				}
			} else if ("hi_psnorg_begindate".equals(evt.getKey())) {
				// 年资起算日和入职日期相同 by he
				Object objValue = getHeadItemValue("hi_psnorg_begindate");
				BillItem item = getBillCardPanel().getHeadItem("hi_psnorg_workagestartdate");
				BillItem workageremaindays = getBillCardPanel().getHeadItem("hi_psnorg_workageremaindays");
				BillItem totalleavedays = getBillCardPanel().getHeadItem("hi_psnorg_totalleavedays");
				// 判断年资其算日是否勾选
				SysInitVO workflags = SysInitQuery.querySysinitVO(getModel().getContext().getPk_org(), "TWHR13");
				if (workflags != null && workflags.getValue().equals("Y")) {
					if (!StringUtils.isEmpty(workageremaindays.getValue())) {
						int days = (new UFDouble(workageremaindays.getValue().toString())).toDouble().intValue();
						objValue = getWorkagedate(objValue, 0 - days);
					}
				}
				// 判断累计留停天数是否勾选
				SysInitVO workflag = SysInitQuery.querySysinitVO(getModel().getContext().getPk_org(), "TWHR10");
				if (workflag != null && workflag.getValue().equals("Y")) {
					if (null != totalleavedays.getValue()) {
						int days = (new UFDouble(totalleavedays.getValue().toString())).toDouble().intValue();
						objValue = getWorkagedate(objValue, days);
					}
				}
				if (item != null) {
					item.setValue(objValue);
				}
				item = getBillCardPanel().getHeadItem("hi_psnorg_workagestartdate");
				if (item != null) {
					item.setValue(objValue);
				}
			} else if ("hi_psnorg_workageremaindays".equals(evt.getKey())) {
				Object objValue = getHeadItemValue("hi_psnorg_workageremaindays");
				BillItem begindate = getBillCardPanel().getHeadItem("hi_psnorg_begindate");
				Object date = null;
				BillItem workagestartdate = getBillCardPanel().getHeadItem("hi_psnorg_workagestartdate");
				BillItem totalleavedays = getBillCardPanel().getHeadItem("hi_psnorg_totalleavedays");
				// 判断年资其算日是否勾选
				SysInitVO workflags = SysInitQuery.querySysinitVO(getModel().getContext().getPk_org(), "TWHR13");
				if (workflags != null && workflags.getValue().equals("Y")) {
					if (!StringUtils.isEmpty(begindate.getValue())) {
						int days = Integer.parseInt(objValue.toString());
						date = getWorkagedate(begindate.getValue(), 0 - days);
					}
				}
				// 判断累计留停天数是否勾选
				SysInitVO workflag = SysInitQuery.querySysinitVO(getModel().getContext().getPk_org(), "TWHR10");
				if (workflag != null && workflag.getValue().equals("Y")) {
					if (null != totalleavedays.getValue()) {
						int days = (new UFDouble(totalleavedays.getValue().toString())).toDouble().intValue();
						date = getWorkagedate(date, days);
					}
				}
				if (workagestartdate != null) {
					workagestartdate.setValue(date);
				}
				workagestartdate = getBillCardPanel().getHeadItem("hi_psnorg_workagestartdate");
				if (workagestartdate != null) {
					workagestartdate.setValue(date);
				}
			}
			// ssx remarded on 2020-07-12
			// 刪除直屬主管相關代碼
			// else if ("hi_psnjob_jobglbdef2".equals(evt.getKey())) {
			// Boolean isSupervisor = (Boolean) (evt.getValue() == null ?
			// "false" : ((Boolean) evt.getValue()));
			// String directSupervisor = (String)
			// getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" +
			// "jobglbdef9");
			// String dept = (String)
			// getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" +
			// "pk_dept");
			// if (null != directSupervisor && null != dept) {
			// setHeadItemValue("hi_psnjob_jobglbdef9", getPrincipal(dept,
			// isSupervisor));
			// }
			// }
			// end

			String series = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "series");
			String postseries = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_postseries");
			String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_job");
			String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_post");
			if ((StringUtils.isBlank(series)) && (StringUtils.isBlank(postseries)) && (StringUtils.isBlank(pk_job))
					&& (StringUtils.isBlank(pk_post))) {

				getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "pk_jobgrade").setEnabled(false);
			} else {
				getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "pk_jobgrade").setEnabled(true);
			}

		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex);
		}
	}

	private Object getWorkagedate(Object objValue, Integer days) {
		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd"); // 字符串转换
		Date newdate = null;
		try {
			newdate = formatDate.parse(objValue.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar c = Calendar.getInstance();

		c.setTime(newdate);
		if (null != days) {
			c.add(Calendar.DATE, days);
		} else {
			c.add(Calendar.DATE, 0);
		}
		String dateday = formatDate.format(c.getTime());
		return dateday;
	}

	private HashMap<String, Object> generateGenderAndBirthdayFromID(String id, String idtype) {
		HashMap<String, Object> map = new HashMap();
		if ((id == null) || (id.length() < 1) || (idtype == null) || (!idtype.equals("1001Z01000000000AI36"))) {
			return null;
		}
		map.put("sex", getSex(id));
		UFLiteralDate birthday = null;
		try {
			birthday = getBirthdate(id) == null ? null : UFLiteralDate.getDate(getBirthdate(id));
		} catch (Exception e) {
			birthday = null;
		}
		map.put("birthday", birthday);
		return map;
	}

	private Integer getSex(String ID) {
		if ((ID.length() != 15) && (ID.length() != 18)) {
			return null;
		}
		int isex = 2;
		isex = ID.length() == 15 ? Integer.parseInt(ID.substring(14)) : Integer.parseInt(ID.substring(16, 17));
		return Integer.valueOf(isex % 2 == 0 ? 2 : 1);
	}

	private String getBirthdate(String ID) {
		if ((ID.length() != 15) && (ID.length() != 18)) {

			return null;
		}
		String birth = ID.length() == 15 ? "19" + ID.substring(6, 12) : ID.substring(6, 14);
		String year = birth.substring(0, 4);
		String month = birth.substring(4, 6);
		String date = birth.substring(6);
		return year + "-" + month + "-" + date;
	}

	protected void setHeadValue(String itemKey, Object value) {
		getBillCardPanel().getHeadItem(itemKey).setValue(null);
		if (value != null) {
			getBillCardPanel().getHeadItem(itemKey).setValue(value);
		}
	}

	protected void setBodyValue(String tabCode, int row, String itemKey, Object value) {
		getBillCardPanel().getBillModel(tabCode).setValueAt(null, row, itemKey);
		if (value != null) {
			getBillCardPanel().getBillModel(tabCode).setValueAt(value, row, itemKey);
		}
	}

	private String getStrValue(Object value) {
		if (value == null) {
			return null;
		}
		if ((value instanceof String)) {
			return (String) value;
		}
		if ((value instanceof String[])) {
			return ((String[]) (String[]) value)[0];
		}
		return value.toString();
	}

	private void afterTrnsEventChange(BillEditEvent evt) {
		Object objValue = evt.getValue();

		if (ArrayUtils.contains(new Object[] { TrnseventEnum.DISMISSION.value(), TrnseventEnum.TRANSAFTERDIS.value() },
				objValue)) {
			String msg = ResHelper.getString("6007psn", "06007psn0165");

			ShowStatusBarMsgUtil.showErrorMsgWithClear(msg, msg, getModel().getContext());
			getBillCardPanel().getBillModel(evt.getTableCode())
					.setValueAt(evt.getOldValue(), evt.getRow(), "trnsevent");
			return;
		}
		BillItem item = getBillCardPanel().getBodyItem(evt.getTableCode(), "trnstype");
		if (item != null) {
			getBillCardPanel().setBodyValueAt(null, evt.getRow(), "trnstype", evt.getTableCode());
			((UIRefPane) item.getComponent()).getRefModel().setWherePart("trnsevent=" + objValue);
		}
	}

	public boolean beforeEdit(BillEditEvent evt) {
		BillItem billItemSource = (BillItem) evt.getSource();
		if (("60070register".equals(getModel().getContext().getNodeCode()))
				&& (PsnJobVO.getDefaultTableName().equals(evt.getTableCode()))) {

			PsndocAggVO psndocAggVO = (PsndocAggVO) getModel().getSelectedData();
			BillModel billModel = getBillCardPanel().getBillModel(PsnJobVO.getDefaultTableName());
			if (((psndocAggVO == null) || (psndocAggVO.getParentVO() == null)
					|| (psndocAggVO.getParentVO().getPsnJobVO() == null) || (billModel == null))
					&& (getModel().getUiState() != UIState.ADD)) {

				return false;
			}
			int roeCount = getBillCardPanel().getBillTable(evt.getTableCode()).getRowCount();
			if ((roeCount > 0) && (evt.getRow() == roeCount - 1) && (getModel().getUiState() != UIState.ADD)) {

				return false;
			}
		}

		if ((PartTimeVO.getDefaultTableName().equals(evt.getTableCode())) && ("pk_org".equals(evt.getKey()))) {

			beforePartTimePk_OrgEdit(evt);
		} else if ((PsnJobVO.getDefaultTableName().equals(evt.getTableCode())) && ("pk_org".equals(evt.getKey()))) {

			BillItem item = (BillItem) evt.getSource();
			if (item != null) {
				String enableSql = " and pk_adminorg in (select pk_adminorg from org_admin_enable) ";
				String powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "60050orginfo", "default",
						"org_orgs");

				if (!StringUtils.isBlank(powerSql)) {
					enableSql = enableSql + " and pk_adminorg in ( select pk_org from org_orgs where " + powerSql + ")";
				}

				try {
					String gkSql = ((IPsndocService) NCLocator.getInstance().lookup(IPsndocService.class))
							.queryControlSql("@@@@Z710000000006M1Y", getModel().getContext().getPk_org(), true);

					if (!StringUtils.isEmpty(gkSql)) {
						enableSql = enableSql + " and org_adminorg.pk_adminorg in ( " + gkSql + " )";
					}
				} catch (BusinessException e1) {
					Logger.error(e1.getMessage(), e1);
				}
				((UIRefPane) item.getComponent()).getRefModel().setUseDataPower(false);
				((UIRefPane) item.getComponent()).getRefModel().addWherePart(enableSql);
			}
		} else if ((ArrayUtils.contains(
				new String[] { PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName() }, evt.getTableCode()))
				&& ("pk_dept".equals(evt.getKey()))) {

			beforePkDeptEdit(evt, evt.getTableCode());
		} else if ((ArrayUtils.contains(
				new String[] { PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName() }, evt.getTableCode()))
				&& ("trnstype".equals(evt.getKey()))) {

			beforeTrnsEventEdit(evt);
		} else if ((ArrayUtils.contains(
				new String[] { PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName() }, evt.getTableCode()))
				&& ("pk_post".equals(evt.getKey()))) {

			beforePkPostEdit(evt);
		} else if ((ArrayUtils.contains(
				new String[] { PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName() }, evt.getTableCode()))
				&& ("pk_job".equals(evt.getKey()))) {

			PsnJobVO psnjob = (PsnJobVO) getBillCardPanel().getBillModel(evt.getTableCode()).getBodyValueRowVO(
					evt.getRow(), PsnJobVO.class.getName());

			beforePkJobEdit(evt);
		} else if ((ArrayUtils.contains(
				new String[] { PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName() }, evt.getTableCode()))
				&& ("series".equals(evt.getKey()))) {

			String pk_job = (String) getBodyItemValue(evt.getTableCode(), "pk_job_ID", evt.getRow());
			String pk_post = (String) getBodyItemValue(evt.getTableCode(), "pk_post_ID", evt.getRow());
			if ((StringUtils.isNotBlank(pk_job)) || (StringUtils.isNotBlank(pk_post))) {
				return false;
			}
		} else if ((ArrayUtils.contains(
				new String[] { PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName() }, evt.getTableCode()))
				&& ("pk_postseries".equals(evt.getKey()))) {

			String pk_post = (String) getBodyItemValue(evt.getTableCode(), "pk_post_ID", evt.getRow());
			if (StringUtils.isNotBlank(pk_post)) {
				return false;
			}
		} else if ((ArrayUtils.contains(
				new String[] { PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName() }, evt.getTableCode()))
				&& ("pk_jobgrade".equals(evt.getKey()))) {

			beforePkJobGradeEdit(evt);
		} else if ((ArrayUtils.contains(
				new String[] { PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName() }, evt.getTableCode()))
				&& ("pk_jobrank".equals(evt.getKey()))) {

			BillItem item = (BillItem) evt.getSource();
			String pk_jobrank = (String) getBodyItemValue(evt.getTableCode(), "pk_jobrank_ID", evt.getRow());
			if (StringUtils.isBlank(pk_jobrank)) {
				((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_joblevel("");
				return true;
			}

			String pk_jobgrade = (String) getBodyItemValue(evt.getTableCode(), "pk_jobgrade_ID", evt.getRow());
			String pk_job = (String) getBodyItemValue(evt.getTableCode(), "pk_job_ID", evt.getRow());
			String pk_post = (String) getBodyItemValue(evt.getTableCode(), "pk_post_ID", evt.getRow());
			String pk_postseries = (String) getBodyItemValue(evt.getTableCode(), "pk_postseries_ID", evt.getRow());

			String pk_jobtype = (String) getBodyItemValue(evt.getTableCode(), "series_ID", evt.getRow());
			if (item != null) {
				FilterTypeEnum filterType = null;
				String gradeSource = "";
				Map<String, Object> resultMap = null;
				try {
					resultMap = ((IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class))
							.getLevelRankCondition(pk_jobtype, pk_job, pk_postseries, pk_post);

				} catch (BusinessException e) {

					Logger.error(e.getMessage(), e);
				}

				if (!resultMap.isEmpty()) {
					filterType = (FilterTypeEnum) resultMap.get("filterType");
					gradeSource = (String) resultMap.get("gradeSource");
				}
				((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_joblevel(pk_jobgrade);
				((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(gradeSource,
						filterType);
			}
		} else if ((ReqVO.getDefaultTableName().equals(evt.getTableCode()))
				&& ("pk_postrequire_b".equals(evt.getKey()))) {

			beforePkPostRequire_b(evt);
		} else if ((CapaVO.getDefaultTableName().equals(evt.getTableCode()))
				&& ("pk_pe_scogrditem".equals(evt.getKey()))) {

			BillItem item = (BillItem) evt.getSource();

			Object objValue = getBodyItemValue(evt.getTableCode(), "pk_pe_indi_ID", evt.getRow());
			if (objValue != null) {
				((CPindiGradeRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_indi((String) objValue);
			}
		} else if ((PsnChgVO.getDefaultTableName().endsWith(evt.getTableCode())) && ("pk_corp".equals(evt.getKey()))) {

			((UIRefPane) ((BillItem) evt.getSource()).getComponent()).getRefModel().setPk_group(PubEnv.getPk_group());
			String powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "60050orginfo", "default", "org_orgs");

			((UIRefPane) ((BillItem) evt.getSource()).getComponent()).getRefModel().setUseDataPower(false);
			if (!StringUtils.isBlank(powerSql)) {
				((UIRefPane) ((BillItem) evt.getSource()).getComponent()).getRefModel().addWherePart(
						" and pk_adminorg in ( select pk_org from org_orgs where " + powerSql + ")");
			}

			String where = " and pk_adminorg in ( select pk_adminorg from org_admin_enable ) and pk_adminorg in ( select pk_corp from org_corp where enablestate = 2 )";

			((UIRefPane) ((BillItem) evt.getSource()).getComponent()).getRefModel().addWherePart(where);
		} else if ("hi_psndoc_courtdeduction".equals(evt.getTableCode())) {
			// 發放比率 by he
			// BillItem type =
			DefaultConstEnum courtdeductways = (DefaultConstEnum) getBodyItemObject(evt.getTableCode(),
					"courtdeductways", evt.getRow());
			if (null != courtdeductways) {
				if ("monthexecutrate".equals(evt.getKey())) {
					if (!courtdeductways.getValue().toString().equals("1")) {
						return false;
					}
				}
				// 月執行金額
				if ("monthexecutamount".equals(evt.getKey())) {
					if (!courtdeductways.getValue().toString().equals("2")) {
						return false;
					}
				}
				// 最低扣款縣市
				// if ("mindeductcountry".equals(evt.getKey())) {
				// if (!courtdeductways.getValue().toString().equals("1")
				// && !courtdeductways.getValue().toString().equals("3")) {
				// return false;
				// }
				// }
				// 保留最低生活費方式
				if ("minimumlifeways".equals(evt.getKey())) {
					if (!courtdeductways.getValue().toString().equals("1")) {
						return false;
					}
				}
			}

		} else if ("hi_psndoc_debtfile".equals(evt.getTableCode()) && "dfilenumber".equals(evt.getKey())) {
			// by he
			BillItem item = (BillItem) evt.getSource();
			UIRefPane newRef = new UIRefPane();
			newRef.setRefModel(new FileNumberRefModel());
			item.setComponent(newRef);
			((FileNumberRefModel) ((UIRefPane) item.getComponent()).getRefModel())
					.setPk_psndoc((String) getHeadItemValue("pk_psndoc"));
			((UIRefPane) item.getComponent()).showModel();
			if (((UIRefPane) item.getComponent()).getRefModel().getSelectedData() != null) {
				Vector selData = (Vector) ((UIRefPane) item.getComponent()).getRefModel().getSelectedData()
						.elementAt(0);
				// dfilenumber
				// getBillCardPanel().getBillModel(evt.getTableCode()).setValueAt(selData.elementAt(0),
				// evt.getRow(), "dfilenumber");
				setBodyValue(evt.getTableCode(), evt.getRow(), "dfilenumber", selData.elementAt(0));

			}
		} else if ("hi_psndoc_debtfile".equals(evt.getTableCode()) && "repaymentratio".equals(evt.getKey())) {
			// 為非必填欄位(與還款比率擇一)，如有多個單位請求強制扣款，並且按照固定金額
			// 拆分，則需填寫各單位每月強制扣款金額，並且需等於法院強制扣款設置所計算或設定之扣款
			// 金額。
			String dfilenumber = (String) getBodyItemValue(evt.getTableCode(), "dfilenumber", evt.getRow());
			if (null == dfilenumber) {
				try {
					throw new BusinessException("请选择档案编号");
				} catch (BusinessException e) {

					e.printStackTrace();
				}
			}
		} else {
			if (CtrtVO.getDefaultTableName().endsWith(evt.getTableCode())) {
				return beforeCtrtEdit(evt);
			}
			if (CertVO.getDefaultTableName().endsWith(evt.getTableCode())) {
				Boolean isStart = (Boolean) getBodyItemValue(CertVO.getDefaultTableName(), "isstart", evt.getRow());

				if (null != isStart) {
					if (Boolean.TRUE.equals(isStart)) {
						setBodyItemEdit(evt.getTableCode(), evt.getRow(), false, new String[] { "idtype", "id" });
					}
				}
			} else if ((ArrayUtils.contains(
					new String[] { PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName() },
					evt.getTableCode()))
					&& ("pk_psncl".equals(evt.getKey()))) {

				Object grpObjValue = getBodyItemValue(evt.getTableCode(), "pk_group_ID", evt.getRow());
				BillItem item = (BillItem) evt.getSource();
				if (item != null) {
					String powerSql = HiSQLHelper.getPsnPowerSql((String) grpObjValue, "psncl", "default", "bd_psncl");

					if (!StringUtils.isBlank(powerSql)) {
						((UIRefPane) item.getComponent()).getRefModel().addWherePart(" and " + powerSql);
					}
				}
			} else if ("poststat".equals(evt.getKey())) {

				if (PartTimeVO.getDefaultTableName().equals(evt.getTableCode())) {
					Boolean isEnd = (Boolean) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(
							evt.getRow(), "endflag");
					return (isEnd != null) && (!isEnd.booleanValue());
				}
			} else if (KeyPsnVO.getDefaultTableName().endsWith(evt.getTableCode())) {
				Boolean endflag = (Boolean) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(
						evt.getRow(), "endflag");
				if ((endflag != null) && (endflag.booleanValue()) && (!evt.getKey().equals("memo"))) {
					return false;
				}
			} else
				try {
					if (PsndocDefTableUtil.getPsnLaborTablename().endsWith(evt.getTableCode())) {
						// ssx added for Janfusun on 20150416
						// Multiorg edit for cost center ref
						// 2017-05-16 upgrade to V65, from JD code
						if (evt.getKey().equals("glbdef5")) {
							BillItem item = (BillItem) evt.getSource();
							((UIRefPane) item.getComponent()).setMultiOrgSelected(false);
							((UIRefPane) item.getComponent()).setMultiCorpRef(true);
						}
					} else if (PsndocDefTableUtil.getPsnLaborTablename().endsWith(evt.getTableCode())) {
						// ssx added for Janfusun on 20150501
						// Avoid edit for old settings
						// 2017-05-16 upgrade to V65, from JD code
						Boolean lastflag = (Boolean) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(
								evt.getRow(), "lastflag");
						if (!lastflag) {
							return false;
						}
					} else if (PsndocDefTableUtil.getPsnHealthTablename().endsWith(evt.getTableCode())) {
						if (evt.getKey().equals("glbdef6") || evt.getKey().equals("glbdef7")
								|| evt.getKey().equals("glbdef16")) {
							BillModel bmodel = getBillCardPanel().getBillModel(evt.getTableCode());
							String head_id = (String) getHeadItemValue("id");
							String cur_line_id = (String) bmodel.getValueAt(evt.getRow(), "glbdef3");
							if (!head_id.equals(cur_line_id)) {
								return false;
							}
						} else if (evt.getKey().equals("glbdef1") || evt.getKey().equals("glbdef2")
								|| evt.getKey().equals("glbdef3") || evt.getKey().equals("glbdef4")) {
							BillItem item = (BillItem) evt.getSource();
							UIRefPane newRef = new UIRefPane();
							newRef.setRefModel(new TWHIFamilyRefModel());
							item.setComponent(newRef);
							((TWHIFamilyRefModel) ((UIRefPane) item.getComponent()).getRefModel())
									.setPk_psndoc((String) getHeadItemValue("pk_psndoc"));
							((UIRefPane) item.getComponent()).showModel();
							if (((UIRefPane) item.getComponent()).getRefModel().getSelectedData() != null) {
								Vector selData = (Vector) ((UIRefPane) item.getComponent()).getRefModel()
										.getSelectedData().elementAt(0);
								// glbdef1: 投保人或眷属姓名
								getBillCardPanel().getBillModel(evt.getTableCode()).setValueAt(selData.elementAt(0),
										evt.getRow(), "glbdef1");
								// glbdef2: 称谓
								getBillCardPanel().getBillModel(evt.getTableCode()).setValueAt(selData.elementAt(1),
										evt.getRow(), "glbdef2");
								// glbdef3: 身份证号码
								getBillCardPanel().getBillModel(evt.getTableCode()).setValueAt(selData.elementAt(2),
										evt.getRow(), "glbdef3");
								// glbdef4: 出生日期
								getBillCardPanel().getBillModel(evt.getTableCode()).setValueAt(selData.elementAt(3),
										evt.getRow(), "glbdef4");
							}
							return false;
						}
					} else if (PsndocDefTableUtil.getGroupInsuranceTablename() == null ? false : PsndocDefTableUtil
							.getGroupInsuranceTablename().endsWith(evt.getTableCode())) {
						// ssx added for Group Insurance on 2017-08-28
						if (evt.getKey().equals("glbdef5")) { // 投保险种
							if (StringUtils.isEmpty((String) getBillCardPanel().getBillModel().getValueAt(evt.getRow(),
									"glbdef4_ID"))) {
								return false; // 未选择投保身份不能编辑投保险种
							}

							String psnType = (String) getBillCardPanel().getBillModel().getValueAt(evt.getRow(),
									"glbdef4_ID");
							BillItem item = (BillItem) evt.getSource();
							((UIRefPane) item.getComponent())
									.setWhereString(" pk_defdoc in (select cgrpinsid from twhr_groupinsurancesetting where cgrpinsrelid = '"
											+ psnType + "') "); // 按投保身份过滤投保险种
						} else if (evt.getKey().equals("insurancecompany")) {
							return false; // 不能编辑保险公司
						} else if (evt.getKey().equals("glbdef1") || evt.getKey().equals("glbdef2")
								|| evt.getKey().equals("glbdef3")) {
							BillItem item = (BillItem) evt.getSource();
							UIRefPane newRef = new UIRefPane();
							newRef.setRefModel(new TWHIFamilyRefModel());
							item.setComponent(newRef);
							((TWHIFamilyRefModel) ((UIRefPane) item.getComponent()).getRefModel())
									.setPk_psndoc((String) getHeadItemValue("pk_psndoc"));
							((UIRefPane) item.getComponent()).showModel();
							if (((UIRefPane) item.getComponent()).getRefModel().getSelectedData() != null) {
								Vector selData = (Vector) ((UIRefPane) item.getComponent()).getRefModel()
										.getSelectedData().elementAt(0);
								// glbdef1: 投保人或眷属姓名
								getBillCardPanel().getBillModel(evt.getTableCode()).setValueAt(selData.elementAt(0),
										evt.getRow(), "glbdef1");
								// glbdef2: 身份证号码
								getBillCardPanel().getBillModel(evt.getTableCode()).setValueAt(selData.elementAt(2),
										evt.getRow(), "glbdef2");
								// glbdef3: 出生日期
								getBillCardPanel().getBillModel(evt.getTableCode()).setValueAt(selData.elementAt(3),
										evt.getRow(), "glbdef3");
							}
							return false;
						}
					}

				} catch (BusinessException e) {
				}
		}
		selectedRow = evt.getRow();
		return billItemSource.isEdit();
	}

	private boolean beforeCtrtEdit(BillEditEvent evt) {
		if ((evt.getRow() == 0) && ("conttype".equals(evt.getKey()))) {
			return false;
		}
		if (ArrayUtils.contains(ctrtTrialFlds, evt.getKey())) {
			Boolean ifProp = (Boolean) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(),
					"ifprop");
			return (ifProp != null) && (ifProp.booleanValue());
		}

		if ("pk_conttext".equals(evt.getKey())) {
			BillItem item = (BillItem) evt.getSource();
			UIRefPane rp = (UIRefPane) item.getComponent();

			rp.getRefModel().addWherePart(
					" and hrcm_contmodel.pk_org in ('GLOBLE00000000000000','" + getModel().getContext().getPk_group()
							+ "','" + getModel().getContext().getPk_org()
							+ "') and hrcm_contmodel.VMODELTYPE = '1002Z710000000017GUF' ");

			return item.isEdit();
		}

		String termtype = (String) getBodyItemValue(CtrtVO.getDefaultTableName(), "termtype", evt.getRow());

		if (StringUtils.isNotBlank(termtype)) {
			if ("CM02".equals(termtype)) {
				setBodyItemEdit(evt.getTableCode(), evt.getRow(), false, new String[] { "termmonth", "enddate" });
				setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, new String[] { "ifprop" });
			} else if ("CM03".equals(termtype)) {
				setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, new String[] { "termmonth", "enddate" });
				setBodyItemEdit(evt.getTableCode(), evt.getRow(), false, new String[] { "ifprop" });
			} else {
				setBodyItemEdit(evt.getTableCode(), evt.getRow(), true,
						new String[] { "termmonth", "enddate", "ifprop" });
			}
		}

		return getBillCardPanel().getBillModel(evt.getTableCode()).getItemByKey(evt.getKey()).isEdit();
	}

	public boolean beforeEdit(BillItemEvent evt) {
		if ((PsnJobVO.getDefaultTableName() + "_" + "pk_dept").equals(evt.getItem().getKey())) {

			Object objValue = getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_org");
			if (evt.getItem() != null) {
				((UIRefPane) evt.getItem().getComponent()).getRefModel().setPk_org((String) objValue);
				String cond = " and ( " + SQLHelper.getNullSql("hrcanceled")
						+ " or hrcanceled = 'N' ) and depttype <> 1 ";
				String powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "60050deptinfo", "default",
						"org_dept");

				if (!StringUtils.isBlank(powerSql)) {
					cond = cond + " and " + powerSql;
				}
				((UIRefPane) evt.getItem().getComponent()).getRefModel().addWherePart(cond);
			}
		} else if ((PsnJobVO.getDefaultTableName() + "_" + "trnstype").equals(evt.getItem().getKey())) {

			String strTrnEvent = PsnJobVO.getDefaultTableName() + "_" + "trnsevent";
			Object objValue = getHeadItemValue(strTrnEvent);
			if (objValue != null) {
				BillItem item = evt.getItem();
				if (item != null) {
					((UIRefPane) item.getComponent()).getRefModel().addWherePart(" and trnsevent=" + objValue);
				}
			}
		} else if ((PsnJobVO.getDefaultTableName() + "_" + "pk_post").equals(evt.getItem().getKey())) {

			String pk_org = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_org");
			String pk_dept = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_dept");
			BillItem item = evt.getItem();
			if (item != null) {
				PostRefModel postModel = (PostRefModel) ((UIRefPane) item.getComponent()).getRefModel();
				postModel.setPk_org(pk_org);
				postModel.setPkdept(pk_dept);
				String cond = " and ( " + SQLHelper.getNullSql("om_post.hrcanceled") + " or " + "om_post"
						+ ".hrcanceled = 'N' ) ";

				String powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "60050deptinfo", "default",
						"org_dept");

				if (!StringUtils.isBlank(powerSql)) {
					cond = cond + " and om_post.pk_dept in ( select pk_dept from org_dept where  " + powerSql + " ) ";
				}
				postModel.addWherePart(cond);
			}
		} else if ((PsnJobVO.getDefaultTableName() + "_" + "pk_job").equals(evt.getItem().getKey())) {

			Object objValue = getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_org");
			BillItem item = evt.getItem();
			if (item != null) {
				if (objValue != null) {
					((UIRefPane) item.getComponent()).setPk_org(objValue.toString());
				} else {
					((UIRefPane) item.getComponent()).setPk_org(getModel().getContext().getPk_group());
				}
			}
		} else if ((PsnJobVO.getDefaultTableName() + "_" + "pk_jobgrade").equals(evt.getItem().getKey())) {

			String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_job");
			String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_post");
			String pk_jobtype = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "series");
			String pk_postseries = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_postseries");

			BillItem item = (BillItem) evt.getSource();
			if (item != null) {
				FilterTypeEnum filterType = null;
				String gradeSource = "";
				Map<String, Object> resultMap = null;
				try {
					resultMap = ((IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class))
							.getLevelRankCondition(pk_jobtype, pk_job, pk_postseries, pk_post);

				} catch (BusinessException e) {

					Logger.error(e.getMessage(), e);
				}

				if (!resultMap.isEmpty()) {
					filterType = (FilterTypeEnum) resultMap.get("filterType");
					gradeSource = (String) resultMap.get("gradeSource");
				}

				((JobGradeRefModel2) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(gradeSource,
						filterType);
			}
			if ((StringUtils.isBlank(pk_jobtype)) && (StringUtils.isBlank(pk_postseries))
					&& (StringUtils.isBlank(pk_job)) && (StringUtils.isBlank(pk_post))) {
				item.setEnabled(false);
			} else {
				item.setEnabled(true);
			}
		} else if ((PsnJobVO.getDefaultTableName() + "_" + "pk_jobrank").equals(evt.getItem().getKey())) {

			BillItem item = evt.getItem();
			String pk_jobrank = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobrank");
			if (StringUtils.isBlank(pk_jobrank)) {
				((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(null, null);

				return true;
			}

			String pk_jobgrade = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_jobgrade");
			String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_job");
			String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_post");
			String pk_jobtype = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "series");
			String pk_postseries = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_postseries");
			if (item != null) {
				FilterTypeEnum filterType = null;
				String gradeSource = "";
				Map<String, Object> resultMap = null;
				try {
					resultMap = ((IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class))
							.getLevelRankCondition(pk_jobtype, pk_job, pk_postseries, pk_post);

				} catch (BusinessException e) {

					Logger.error(e.getMessage(), e);
				}

				if (!resultMap.isEmpty()) {
					filterType = (FilterTypeEnum) resultMap.get("filterType");
					gradeSource = (String) resultMap.get("gradeSource");
				}
				((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_joblevel(pk_jobgrade);
				((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(gradeSource,
						filterType);
			}
		} else if ((PsnJobVO.getDefaultTableName() + "_" + "pk_psncl").equals(evt.getItem().getKey())) {

			BillItem item = evt.getItem();
			if (item != null) {
				String powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "psncl", "default", "bd_psncl");

				if (!StringUtils.isBlank(powerSql)) {
					((UIRefPane) item.getComponent()).getRefModel().addWherePart(" and " + powerSql);
				}
			}
		} else if ((PsnJobVO.getDefaultTableName() + "_" + "pk_org").equals(evt.getItem().getKey())) {

			BillItem item = evt.getItem();
			if (item != null) {
				String enableSql = " and pk_adminorg in (select pk_adminorg from org_admin_enable) ";

				String powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), "60050orginfo", "default",
						"org_orgs");

				if (!StringUtils.isBlank(powerSql)) {
					enableSql = enableSql + " and pk_adminorg in ( select pk_org from org_orgs where " + powerSql
							+ ") ";
				}
				((UIRefPane) item.getComponent()).getRefModel().setUseDataPower(false);

				try {
					String gkSql = ((IPsndocService) NCLocator.getInstance().lookup(IPsndocService.class))
							.queryControlSql("@@@@Z710000000006M1Y", getModel().getContext().getPk_org(), true);

					if (!StringUtils.isEmpty(gkSql)) {
						enableSql = enableSql + " and org_adminorg.pk_adminorg in ( " + gkSql + " )";
					}
				} catch (BusinessException e1) {
					Logger.error(e1.getMessage(), e1);
				}
				((UIRefPane) item.getComponent()).getRefModel().addWherePart(enableSql);
			}
		} else if ((PsnJobVO.getDefaultTableName() + "_" + "pk_postseries").equals(evt.getItem().getKey())) {
			String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_post");
			if (StringUtils.isNotBlank(pk_post)) {
				return false;
			}
		} else if ((PsnJobVO.getDefaultTableName() + "_" + "series").equals(evt.getItem().getKey())) {
			String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_job");
			if (StringUtils.isNotBlank(pk_job)) {
				return false;
			}
		} else if ("nativeplace".equals(evt.getItem().getKey())) {

			AbstractRefModel nativeRefModel = ((UIRefPane) getBillCardPanel().getHeadItem("nativeplace").getComponent())
					.getRefModel();
			((RegionDefaultRefTreeModel) nativeRefModel).setPk_country("0001Z010000000079UJJ");
		} else if ("permanreside".equals(evt.getItem().getKey())) {

			AbstractRefModel refModel = ((UIRefPane) getBillCardPanel().getHeadItem("permanreside").getComponent())
					.getRefModel();
			((RegionDefaultRefTreeModel) refModel).setPk_country("0001Z010000000079UJJ");
		}
		return true;
	}

	private void beforePartTimePk_OrgEdit(BillEditEvent evt) {
		Object objValue = getBodyItemValue(evt.getTableCode(), "pk_group_ID", evt.getRow());
		if ((objValue != null) && (evt.getSource() != null)) {
			BillItem item = (BillItem) evt.getSource();
			((UIRefPane) item.getComponent()).getRefModel().setPk_group(objValue.toString());
			String enableSql = "  ";
			String powerSql = HiSQLHelper
					.getPsnPowerSql(objValue.toString(), "60050orginfo", "partdefault", "org_orgs");

			if (!StringUtils.isBlank(powerSql)) {
				enableSql = enableSql + " and pk_adminorg in ( select pk_org from org_orgs where " + powerSql + ")";
			}
			((UIRefPane) item.getComponent()).getRefModel().setUseDataPower(false);
			((UIRefPane) item.getComponent()).getRefModel().addWherePart(enableSql);
		}
	}

	private void beforePkDeptEdit(BillEditEvent evt, String defaultTableName) {
		Object orgObjValue = getBodyItemValue(evt.getTableCode(), "pk_org_ID", evt.getRow());
		Object grpObjValue = getBodyItemValue(evt.getTableCode(), "pk_group_ID", evt.getRow());

		HRDeptRefModel deptRefModel = (HRDeptRefModel) ((UIRefPane) ((BillItem) evt.getSource()).getComponent())
				.getRefModel();
		deptRefModel.setPk_org((String) orgObjValue);

		String cond = " and depttype <> 1 ";
		String powerSql = HiSQLHelper.getPsnPowerSql((String) grpObjValue, "60050deptinfo", "default", "org_dept");

		if (!StringUtils.isBlank(powerSql)) {
			cond = cond + " and " + powerSql;
		}
		if (PartTimeVO.getDefaultTableName().equals(defaultTableName)) {
			deptRefModel.setShowDisbleOrg(Boolean.TRUE.booleanValue());
		} else {
			deptRefModel.setShowDisbleOrg(Boolean.FALSE.booleanValue());
		}
		deptRefModel.addWherePart(cond);
	}

	private void beforePkJobEdit(BillEditEvent evt) {
		Object objValue = getBodyItemValue(evt.getTableCode(), "pk_group_ID", evt.getRow());
		BillItem item = (BillItem) evt.getSource();
		if (item != null) {
			((UIRefPane) item.getComponent()).getRefModel().setPk_group((String) objValue);
		}
	}

	private void beforePkJobGradeEdit(BillEditEvent evt) {
		String pk_job = (String) getBodyItemValue(evt.getTableCode(), "pk_job_ID", evt.getRow());
		String pk_post = (String) getBodyItemValue(evt.getTableCode(), "pk_post_ID", evt.getRow());
		String pk_jobtype = (String) getBodyItemValue(evt.getTableCode(), "series_ID", evt.getRow());
		String pk_postseries = (String) getBodyItemValue(evt.getTableCode(), "pk_postseries_ID", evt.getRow());

		BillItem item = (BillItem) evt.getSource();

		if (item == null)
			return;
		FilterTypeEnum filterType = null;
		String gradeSource = "";
		Map<String, Object> resultMap = null;
		try {
			resultMap = ((IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class))
					.getLevelRankCondition(pk_jobtype, pk_job, pk_postseries, pk_post);

		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}

		if (!resultMap.isEmpty()) {
			filterType = (FilterTypeEnum) resultMap.get("filterType");
			gradeSource = (String) resultMap.get("gradeSource");
		}

		((JobGradeRefModel2) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(gradeSource, filterType);

		if ((StringUtils.isBlank(pk_jobtype)) && (StringUtils.isBlank(pk_postseries)) && (StringUtils.isBlank(pk_job))
				&& (StringUtils.isBlank(pk_post))) {
			item.setEnabled(false);
		} else {
			item.setEnabled(true);
		}
	}

	private void beforePkPostEdit(BillEditEvent evt) {
		String pk_org = (String) getBodyItemValue(evt.getTableCode(), "pk_org_ID", evt.getRow());
		String pk_dept = (String) getBodyItemValue(evt.getTableCode(), "pk_dept_ID", evt.getRow());
		String pk_group = (String) getBodyItemValue(evt.getTableCode(), "pk_group_ID", evt.getRow());
		BillItem item = (BillItem) evt.getSource();
		if (item != null) {
			PostRefModel postModel = (PostRefModel) ((UIRefPane) item.getComponent()).getRefModel();
			postModel.setPk_group(pk_group);
			postModel.setPk_org(pk_org);
			if (!StringUtils.isBlank(pk_dept)) {
				postModel.setPkdept(pk_dept);
			} else {
				postModel.setPkdept(null);
				String powerSql = HiSQLHelper.getPsnPowerSql(pk_group, "60050deptinfo", "default", "org_dept");

				if (!StringUtils.isBlank(powerSql)) {
					String cond = " and om_post.pk_dept in ( select pk_dept from org_dept where  " + powerSql + " ) ";
					postModel.addWherePart(cond);
				}
			}
		}
	}

	private void beforePkPostRequire_b(BillEditEvent evt) {
		BillItem billItemSource = (BillItem) evt.getSource();
		Object objValue = getBillCardPanel().getBodyItem(evt.getTableCode(), "pk_postrequire_h").getValueObject();
		if (objValue != null) {
			((UIRefPane) billItemSource.getComponent()).getRefModel().addWherePart(" and pk_cindex='" + objValue + "'");
		}
	}

	private void beforeTrnsEventEdit(BillEditEvent evt) {
		BillItem billItemSource = (BillItem) evt.getSource();
		Object objValue = getBillCardPanel().getBodyItem(evt.getTableCode(), "trnsevent").getValueObject();
		if (objValue != null) {
			((UIRefPane) billItemSource.getComponent()).getRefModel().addWherePart(
					" and trnsevent=" + objValue + " and enablestate = " + 2);
		}
	}

	public boolean canBeHidden() {
		if (ArrayUtils.contains(new UIState[] { UIState.ADD, UIState.EDIT }, getModel().getUiState())) {
			return false;
		}
		return super.canBeHidden();
	}

	private void clearBodyItemValue(String strTabCode, int iRowIndex, String... strBodyItemKeys) {
		if ((strBodyItemKeys == null) || (strBodyItemKeys.length == 0)) {
			return;
		}
		BillModel billModel = strTabCode == null ? getBillCardPanel().getBillModel() : getBillCardPanel().getBillModel(
				strTabCode);
		if (billModel == null) {
			return;
		}
		for (String strItemKey : strBodyItemKeys) {
			billModel.setValueAt(null, iRowIndex, strItemKey);
		}
	}

	private void clearHeadItemValue(String... strHeadItemKeys) {
		if ((strHeadItemKeys == null) || (strHeadItemKeys.length == 0)) {
			return;
		}
		for (String strItemKey : strHeadItemKeys) {
			BillItem item = getBillCardPanel().getHeadItem(strItemKey);
			if (item != null) {
				item.clearViewData();
			}
		}
	}

	private void filteByPsncl() {
		if (("60070poi".equals(getModel().getNodeCode())) || ("60070keypsn".equals(getModel().getNodeCode()))) {
			disableHeaditems();

			if ("60070keypsn".equals(getModel().getNodeCode())) {
				PsndocViewHelper
						.changeBusiness(getBillCardPanel().getBodyTabbedPane(), getModel().getBusinessInfoSet());
			}
			return;
		}

		disableHeaditems();

		if (ObjectUtils.equals(strPk_psncl, getModel().getPk_psncl())) {
			return;
		}
		strPk_psncl = getModel().getPk_psncl();

		BillTempletBodyVO[] billTempletBodyVOs = getBillCardPanel().getBillData().getBillTempletVO().getBodyVO();
		if ((billTempletBodyVOs == null) || (billTempletBodyVOs.length <= 0)) {
			return;
		}

		HashMap<String, PsnclinfosetVO> getConfigMap = new HrPsnclTemplateContainer().getPsnclConfigMap(getModel()
				.getContext().getPk_org(), strPk_psncl);

		if ((getConfigMap == null) || (getConfigMap.isEmpty())) {

			for (BillTempletBodyVO billTempletBodyVO : billTempletBodyVOs) {
				int pos = billTempletBodyVO.getPos().intValue();
				BillItem item = null;
				if (0 == pos) {
					item = getBillCardPanel().getHeadItem(billTempletBodyVO.getItemkey());
				} else if (1 == pos) {
					item = getBillCardPanel().getBodyItem(billTempletBodyVO.getTable_code(),
							billTempletBodyVO.getItemkey());
				}
				if (item != null) {

					if (item.getTableCode().equals(KeyPsnVO.getDefaultTableName())) {
						item.setShow(false);
						item.setNull(false);
					} else if (ArrayUtils.contains(fldBlastList, item.getKey())) {
						item.setShow(false);
						item.setNull(false);
					} else {
						item.setShow(billTempletBodyVO.getShowflag().booleanValue());
						item.setNull(billTempletBodyVO.getNullflag().booleanValue());
					}
				}
			}
			afterFilterPsncl();

			return;
		}

		for (BillTempletBodyVO billTempletBodyVO : billTempletBodyVOs) {
			int pos = billTempletBodyVO.getPos().intValue();
			BillItem item = null;
			if (0 == pos) {
				item = getBillCardPanel().getHeadItem(billTempletBodyVO.getItemkey());
			} else if (1 == pos) {
				item = getBillCardPanel()
						.getBodyItem(billTempletBodyVO.getTable_code(), billTempletBodyVO.getItemkey());
			}
			if (item != null) {

				if ((1 == pos)
						&& ((PsnOrgVO.getDefaultTableName().equals(billTempletBodyVO.getTable_code())) || (KeyPsnVO
								.getDefaultTableName().equals(billTempletBodyVO.getTable_code())))) {

					item.setShow(false);
					item.setNull(false);

				} else if (ArrayUtils.contains(fldBlastList, item.getKey())) {
					item.setShow(false);
					item.setNull(false);
				} else {
					item.setShow(billTempletBodyVO.getShowflag().booleanValue());
					item.setNull(billTempletBodyVO.getNullflag().booleanValue());

					PsnclinfosetVO configVO = (PsnclinfosetVO) getConfigMap
							.get(billTempletBodyVO.getMetadataproperty());
					if (configVO != null) {

						item.setShow((configVO.getUsedflag() != null) && (configVO.getUsedflag().booleanValue())
								&& (billTempletBodyVO.getShowflag().booleanValue()));
						if (!item.isShow()) {
							item.setNull(false);
						}

						item.setNull((configVO.getMustflag() != null) && (configVO.getMustflag().booleanValue())
								&& (item.isShow()));
					}
				}
			}
		}
		afterFilterPsncl();
	}

	private void afterFilterPsncl() {
		hideQutifySet();

		getBillCardPanel().setBillData(getBillCardPanel().getBillData());

		PsndocViewHelper.changeBusiness(getBillCardPanel().getBodyTabbedPane(), getModel().getBusinessInfoSet());

		String[] strTabCodes = getBillCardPanel().getBillData().getBodyTableCodes();
		if (strTabCodes != null) {
			for (String strTabCode : strTabCodes) {
				// getBillCardPanel().getBillTable(strTabCode).removeSortListener();
				getBillCardPanel().getBodyPanel(strTabCode).setBBodyMenuShow(false);
			}
		}

		setCellRenderer();
	}

	private void hideQutifySet() {
		boolean isJQStart = false;
		isJQStart = PubEnv.isModuleStarted(PubEnv.getPk_group(), "6019");
		if (isJQStart) {
			return;
		}

		BillModel bm = getBillCardPanel().getBillModel(QulifyVO.getDefaultTableName());
		if (bm == null) {
			return;
		}
		BillItem[] items = bm.getBodyItems();
		for (int i = 0; (items != null) && (i < items.length); i++) {
			items[i].setShow(false);
		}
	}

	private Object getBodyItemValue(String strTabCode, String strItemKey, int iRowIndex) {
		return getBillCardPanel().getBillModel(strTabCode).getValueAt(iRowIndex, strItemKey);
	}

	private Object getBodyItemObject(String strTabCode, String strItemKey, int iRowIndex) {
		return getBillCardPanel().getBillModel(strTabCode).getValueObjectAt(iRowIndex, strItemKey);
	}

	public PsndocDataManager getDataManger() {
		return dataManger;
	}

	public FieldRelationUtil getFieldRelationUtil() {
		return fieldRelationUtil;
	}

	public PsndocModel getModel() {
		return (PsndocModel) super.getModel();
	}

	public SuperFormEditorValidatorUtil getSuperValidator() {
		return superValidator;
	}

	public Object getValue() {
		PsndocAggVO psndocChangedAggVO = (PsndocAggVO) super.getValue();

		if (getModel().getContext().getNodeCode().equals("60070employee")) {
			SuperVO[] capa = psndocChangedAggVO.getTableVO(CapaVO.getDefaultTableName());
			for (int i = 0; (capa != null) && (i < capa.length); i++) {
				if (capa[i].getStatus() == 1) {
					capa[i].setAttributeValue("asssourcetype", Integer.valueOf(3));
				}
			}
		}

		PsndocAggVO psndocAggVO = (PsndocAggVO) getBillCardPanel().getBillData().getBillObjectByMetaData();

		psndocAggVO.mergeDeletedAggVO(psndocChangedAggVO);

		try {
			validateData(psndocAggVO);
		} catch (BusinessException ex) {
			throw new BusinessRuntimeException(ex.getMessage(), ex);
		}

		PsndocVO psndocVO = psndocAggVO.getParentVO();

		getBillCardPanel().getBillData().getHeaderValueVO(psndocVO);
		psndocVO.setPk_hrorg(getModel().getContext().getPk_org());
		psndocVO.getPsnOrgVO().setPk_hrorg(getModel().getContext().getPk_org());
		psndocVO.getPsnJobVO().setPk_hrorg(getModel().getContext().getPk_org());
		resetMacaId(psndocAggVO);

		if (psndocVO.getPsnOrgVO().getPk_group() == null) {
			psndocVO.getPsnOrgVO().setPk_group(psndocVO.getPk_group());
		}
		if (psndocVO.getPsnJobVO().getPk_group() == null) {
			psndocVO.getPsnJobVO().setPk_group(psndocVO.getPk_group());
		}

		psndocVO.setStatus(psndocVO.getPk_psndoc() == null ? 2 : 1);
		if ("hire".equals(getModel().getInJobType())) {

			psndocVO.getPsnJobVO().setStatus(psndocVO.getStatus());
			psndocVO.getPsnOrgVO().setStatus(psndocVO.getStatus());
		} else if ("rehire".equals(getModel().getInJobType())) {

			psndocVO.getPsnJobVO().setStatus(2);
			psndocVO.getPsnOrgVO().setStatus(2);
		}

		if ("rehire".equals(getModel().getInJobType())) {

			SuperVO[] childVO = psndocAggVO.getAllChildrenVO();
			for (int i = 0; (childVO != null) && (i < childVO.length); i++) {
				if ((childVO[i].getPrimaryKey() != null) && (2 == childVO[i].getStatus())) {
					childVO[i].setStatus(1);
				}
			}
		}

		psndocAggVO.setParentVO(psndocVO);

		setBodyValue("hi_psndoc_cert", 0, "idtype", psndocVO.getIdtype());
		setBodyValue("hi_psndoc_cert", 0, "id", psndocVO.getId());

		return psndocAggVO;
	}

	private void resetMacaId(PsndocAggVO psndocAggVO) {
		SuperVO[] subVOs = psndocAggVO.getAllChildrenVO();
		PsndocVO psndocVO = psndocAggVO.getParentVO();
		String pid = psndocVO.getId();
		if (("1001Z01000000000CHUN".equals(psndocVO.getIdtype())) && (pid.endsWith(")"))
				&& ("(".equals(String.valueOf(pid.charAt(pid.length() - 3))))) {

			psndocVO.setId(pid.substring(0, pid.length() - 3) + pid.charAt(pid.length() - 2));
		}

		for (int m = 0; m < subVOs.length; m++) {
			if (((subVOs[m] instanceof CertVO)) && ("1001Z01000000000CHUN".equals(((CertVO) subVOs[m]).getIdtype()))) {
				String id = ((CertVO) subVOs[m]).getId();
				if ((id.endsWith(")")) && ("(".equals(String.valueOf(id.charAt(id.length() - 3))))) {
					((CertVO) subVOs[m]).setId(id.substring(0, id.length() - 3) + id.charAt(id.length() - 2));
				}
			}
		}
	}

	public void handleEvent(AppEvent evt) {
		if ("Show_Editor" == evt.getType()) {
			getModel().fireEvent(new AppEvent("tabchanged", evt.getSource(), null));
		}

		if ("Selection_Changed" == evt.getType()) {
			onSelectionChanged();

			SuperVO[] subVOs = null;
			BillModel billModel = getBillCardPanel().getBillModel(PsnJobVO.getDefaultTableName());
			try {
				subVOs = getDataManger().querySubVO(PsnJobVO.getDefaultTableName(), null);
			} catch (BusinessException ex) {
				throw new BusinessRuntimeException(ex.getMessage(), ex);
			}

			if ((subVOs != null) && (subVOs.length > 0)) {
				billModel.clearBodyData();
				billModel.addLine(subVOs.length);
				for (int i = 0; i < subVOs.length; i++) {
					billModel.setBodyRowObjectByMetaData(subVOs[i], i);
					billModel.setRowState(i, 0);
				}
				billModel.execLoadFormula();

				// ssx added on 2020-02-16
				// 模板上增加father_serial_name自定義項，接收次職類對應的父職類名稱
				if (getBillCardPanel().getHeadItem("father_serial_name") != null) {
					nc.vo.pub.lang.MultiLangText names = (MultiLangText) getBillCardPanel().getBodyValueAt(
							subVOs.length - 1, "series.father_pk.jobtypename");
					if (names != null) {
						getBillCardPanel().setHeadItem("father_serial_name",
								names.getText(MultiLangUtil.getCurrentLangSeq() - 1));
					}
				}
				// end
			}

			getHashSubHaveLoad().add(PsnJobVO.getDefaultTableName());
		} else {
			super.handleEvent(evt);
		}
	}

	protected void onNotEdit() {
		super.onNotEdit();
		getBillCardPanel().getBillData().clearShowWarning();
	}

	public void initUI() {
		if ((getTemplateContainer() instanceof HrPsnclTemplateContainer)) {
			HrPsnclTemplateContainer templateContainer = (HrPsnclTemplateContainer) getTemplateContainer();
			templateContainer.setPk_org(getModel().getContext().getPk_org());
		}
		super.initUI();

		isEditBeginDate = getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + "begindate").isEdit();
		isEditEndDate = getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + "enddate").isEdit();
		getBillCardPanel().getHeadItem("hi_psnorg_workagestartdate").setEdit(false);

		hideQutifySet();
		getBillCardPanel().setBillData(getBillCardPanel().getBillData());

		getBillCardPanel().addBodyEditListener2(this);
		getBillCardPanel().setBillBeforeEditListenerHeadTail(this);
		String[] strTabCodes = getBillCardPanel().getBillData().getBodyTableCodes();
		if ((strTabCodes != null) && (strTabCodes.length > 0)) {
			for (String strTabCode : strTabCodes) {
				getBillCardPanel().addEditListener(strTabCode, this);
				getBillCardPanel().addBodyEditListener2(strTabCode, this);
			}
		}

		getBillCardPanel().getBodyTabbedPane().addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent event) {
				PsndocFormEditor.this.subTabChanged(event);
			}
		});

		if (getFieldRelationUtil() != null) {
			getFieldRelationUtil().setFormeditor(this);
			getFieldRelationUtil().putBombToFormEditor();
		}

		if (superValidator != null) {
			superValidator.setFormeditor(this);
			superValidator.getComponentMap().put("model", getModel());
			superValidator.getComponentMap().put("utils", new EvalUtils(getModel().getContext()));
		}

		PsndocViewHelper.changeBusiness(getBillCardPanel().getBodyTabbedPane(), getModel().getBusinessInfoSet());
		getBillCardPanel().getBodyTabbedPane().setTabLayoutPolicy(1);

		String[] tabCodes = getBillCardPanel().getBillData().getBodyTableCodes();
		for (int i = 0; (tabCodes != null) && (i < tabCodes.length); i++) {
			// getBillCardPanel().getBillTable(tabCodes[i]).removeSortListener();
			getBillCardPanel().getBodyPanel(tabCodes[i]).setBBodyMenuShow(false);
		}

		disableHeaditems();

		setCellRenderer();

		if ("60070register".equals(getModel().getContext().getNodeCode())) {

			DefaultConstEnum[] enumItems = initTransevent();
			BillItem item = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "trnsevent");
			UIComboBox combobox = (UIComboBox) item.getComponent();
			combobox.removeAllItems();
			combobox.addItems(enumItems);
		}

		((UIRefPane) getBillCardPanel().getBodyItem(CertVO.getDefaultTableName(), "id").getComponent())
				.getUITextField().addFocusListener(this);
	}

	private DefaultConstEnum[] initTransevent() {
		List<DefaultConstEnum> items = new ArrayList();

		try {
			IComponent ibean = MDBaseQueryFacade.getInstance().getComponentByID("f57904bd-0037-4cea-842d-f33708084ab8");
			List<IEnumType> enums = ibean.getEnums();

			IConstEnum[] agreementTypeEnum = null;
			for (IEnumType iet : enums) {
				if ("trnsevent".equals(iet.getName())) {
					agreementTypeEnum = iet.getConstEnums();
					break;
				}
			}

			items = new ArrayList();
			for (IConstEnum pte : agreementTypeEnum) {

				if ((((Integer) pte.getValue()).intValue() != 4) && (((Integer) pte.getValue()).intValue() != 5)) {

					items.add(new DefaultConstEnum(pte.getValue(), pte.getName()));
				}
			}
		} catch (Throwable t) {
			Logger.error("[异?事件]枚?加?失?，可能是指定的元?据不存在，或注入的枚?名???。");
		}
		return (DefaultConstEnum[]) items.toArray(new DefaultConstEnum[items.size()]);
	}

	private void disableHeaditems() {
		try {
			String[] keys = getModel().getHiddenKeys();

			for (int i = 0; (keys != null) && (i < keys.length); i++) {
				BillItem item = getBillCardPanel().getHeadItem(keys[i]);
				if (item != null) {
					item.setEnabled(false);
				}
			}
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
	}

	public void loadCurrentRowSubData() {
		int tabIndex = getBillCardPanel().getBodyTabbedPane().getSelectedIndex();
		if (tabIndex < 0) {

			return;
		}
		BillModel billModel = getBillCardPanel().getBillModel();
		String strTabCode = billModel.getTabvo().getTabcode();

		if ((getModel().getBusinessInfoSet().contains(strTabCode)) && (UIState.ADD == getModel().getUiState())) {

			return;
		}

		if (((UIState.EDIT == getModel().getUiState()) || (UIState.ADD == getModel().getUiState()))
				&& (getHashSubHaveLoad().contains(strTabCode)) && (billModel.getRowCount() > 0)) {

			return;
		}

		SuperVO[] subVOs = null;
		try {
			subVOs = getDataManger().querySubVO(strTabCode, null);
		} catch (BusinessException ex) {
			throw new BusinessRuntimeException(ex.getMessage(), ex);
		}
		if ((subVOs != null) && (subVOs.length > 0)) {
			billModel.clearBodyData();
			billModel.addLine(subVOs.length);
			for (int i = 0; i < subVOs.length; i++) {
				billModel.setBodyRowObjectByMetaData(subVOs[i], i);
				billModel.setRowState(i, 0);
			}
			billModel.execLoadFormula();
		}
		getHashSubHaveLoad().add(strTabCode);
		getModel().fireEvent(new AppEvent("changeBtnState"));
	}

	public void showMeUp() {
		super.showMeUp();
		if (UIState.ADD != getModel().getUiState()) {
			synchronizeDataFromModel();
		}
		loadCurrentRowSubData();
	}

	protected void onAdd() {
		getHashSubHaveLoad().clear();
		filteByPsncl();
		super.onAdd();

		BillItem[] items = getBillCardPanel().getBillData().getHeadTailItems();
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				BillItem item = items[i];
				if ((!"hi_psnorg_orgrelaid".equals(item.getKey())) && (!"hi_psnjob_poststat".equals(item.getKey()))) {

					Object value = item.getDefaultValueObject();
					if (value != null) {
						item.setValue(value);
					}
				}
			}
		}

		if (getModel().getPsndocCodeContext() != null) {
			BillItem billItemCode = getBillCardPanel().getHeadItem("code");
			if ((billItemCode != null) && ("hire".equals(getModel().getInJobType()))) {

				billItemCode.setEdit(getModel().getPsndocCodeContext().isEditable());
				getBillCardPanel().execHeadEditFormulas();
			}
		}
		if (getModel().getPsndocClerkCodeContext() != null) {
			BillItem item2 = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "clerkcode");
			if ((item2 != null) && ("hire".equals(getModel().getInJobType()))) {

				item2.setEdit(getModel().getPsndocClerkCodeContext().isEditable());
				getBillCardPanel().execHeadEditFormulas();
			}
		}
		disableHeaditems();
		getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "pk_jobgrade").setEnabled(false);

		// ssx added on 2020-05-28 for #35540
		// 入職時入職前采計年資默認為0
		getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + "orgglbdef5").setValue(UFDouble.ZERO_DBL);
		// end ssx
	}

	protected void onEdit() {
		getHashSubHaveLoad().clear();
		filteByPsncl();
		super.onEdit();
		// mod by Connie.ZH
		// 2019-05-28 started
		// set residence due date column's state when editor change to edit
		// state
		// ssx modified on 2019-12-01
		// 不是所有人的居留證到期日在入職時都能拿到，所以不能變為必輸
		// getBillCardPanel().getHeadItem(Residence_Due_Date).setNull(
		// (Boolean)
		// getBillCardPanel().getHeadItem(Is_Foreign_Key).getValueObject());
		// ssx end
		getBillCardPanel().getHeadItem(Residence_Due_Date).setEnabled(
				(Boolean) getBillCardPanel().getHeadItem(Is_Foreign_Key).getValueObject());
		// 2019-05-28 end

		if (("60070employee".equals(getModel().getNodeCode())) || ("60070keypsn".equals(getModel().getNodeCode()))) {

			for (String strFieldCode : strBusiFieldInJobs) {
				BillItem billItem = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + strFieldCode);
				if (billItem != null) {
					billItem.setEdit(false);
				}
			}
			BillItem[] item = getBillCardPanel().getHeadShowItems("hi_psnjob");
			for (int i = 0; (item != null) && (i < item.length); i++) {
				if (!(PsnJobVO.getDefaultTableName() + "_" + "clerkcode").equals(item[i].getKey())) {

					item[i].setEdit(false);
				}
			}

			Boolean isEnd = (Boolean) getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + "endflag")
					.getValueObject();

			if ((isEnd != null) && (isEnd.booleanValue())) {
				getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + "begindate").setEdit(false);
				getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + "enddate").setEdit(false);
			} else {
				if (isEditBeginDate == true)
					getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + "begindate").setEdit(true);
				if (isEditEndDate == true) {
					getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + "enddate").setEdit(true);
				}
			}
		}
		if (getModel().getPsndocCodeContext() != null) {
			BillItem billItemCode = getBillCardPanel().getHeadItem("code");
			if (billItemCode != null) {
				billItemCode.setEdit(getModel().getPsndocCodeContext().isEditable());
			}
		}

		if (getModel().getPsndocClerkCodeContext() != null) {
			BillItem item2 = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "clerkcode");
			if (item2 != null) {

				item2.setEdit(getModel().getPsndocClerkCodeContext().isEditable());
			}
		}

		String[] codes = { "name" };
		for (String strFieldCode : codes) {
			BillItem billItem = getBillCardPanel().getHeadItem(strFieldCode);
			if (billItem != null) {
				billItem.setEdit(billItem.isEdit());
			}
		}

		BillItem billItemPsncl = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "pk_psncl");
		if (billItemPsncl != null) {
			billItemPsncl.setEdit(false);
		}

		Iterator iterator;
		if (ArrayUtils.contains(new String[] { "60070employee", "60070psninfo", "60070keypsn" }, getModel()
				.getContext().getNodeCode())) {

			for (iterator = getModel().getBusinessInfoSet().iterator(); iterator.hasNext();) {
				String strTabCode = (String) iterator.next();
				if (((!CtrtVO.getDefaultTableName().equals(strTabCode)) || (HiCacheUtils.isModuleStarted("6011")))
						&& ((!CapaVO.getDefaultTableName().equals(strTabCode)) || (HiCacheUtils.isModuleStarted("6004")))
						&& ((!TrainVO.getDefaultTableName().equals(strTabCode)) || (HiCacheUtils
								.isModuleStarted("6025")))
						&& ((!AssVO.getDefaultTableName().equals(strTabCode)) || (HiCacheUtils.isModuleStarted("6029")))
						&& (

						(!"60070keypsn".equals(getModel().getContext().getNodeCode())) || (!KeyPsnVO
								.getDefaultTableName().equals(strTabCode)))) {

					BillModel billModel = getBillCardPanel().getBillModel(strTabCode);
					if (billModel != null) {
						billModel.setEnabled(false);
					}
				}
			}
		}
		BillItem itemJOBGRADE = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + "pk_jobgrade");
		String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_job");
		String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_post");
		String pk_jobtype = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "series");
		String pk_postseries = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + "pk_postseries");

		if ((StringUtils.isBlank(pk_jobtype)) && (StringUtils.isBlank(pk_postseries)) && (StringUtils.isBlank(pk_job))
				&& (StringUtils.isBlank(pk_post))) {
			itemJOBGRADE.setEnabled(false);
		} else
			itemJOBGRADE.setEnabled(true);
		disableHeaditems();

		SuperVO[] subVOs = null;
		BillModel billModel = getBillCardPanel().getBillModel(CertVO.getDefaultTableName());
		try {
			subVOs = getDataManger().querySubVO(CertVO.getDefaultTableName(), null);
		} catch (BusinessException ex) {
			throw new BusinessRuntimeException(ex.getMessage(), ex);
		}
		if ((subVOs != null) && (subVOs.length > 0)) {
			billModel.clearBodyData();
			billModel.addLine(subVOs.length);
			for (int i = 0; i < subVOs.length; i++) {
				billModel.setBodyRowObjectByMetaData(subVOs[i], i);
				billModel.setRowState(i, 0);
			}
			billModel.execLoadFormula();
		}
		getHashSubHaveLoad().add(CertVO.getDefaultTableName());
		getModel().fireEvent(new AppEvent("changeBtnState"));
	}

	public void setDataManger(PsndocDataManager dataManger) {
		this.dataManger = dataManger;
	}

	protected void setDefaultValue() {
		super.setDefaultValue();

		BillItem nativeplace_item = getBillCardPanel().getHeadItem("nativeplace");
		BillItem permanreside_item = getBillCardPanel().getHeadItem("permanreside");
		if ((null != nativeplace_item) && (null != nativeplace_item.getComponent())) {
			RegionDefaultRefTreeModel regoin = (RegionDefaultRefTreeModel) ((UIRefPane) nativeplace_item.getComponent())
					.getRefModel();
			regoin.setPk_country("0001Z010000000079UJJ");
		}
		if ((null != permanreside_item) && (null != permanreside_item.getComponent())) {
			RegionDefaultRefTreeModel regoin = (RegionDefaultRefTreeModel) ((UIRefPane) permanreside_item
					.getComponent()).getRefModel();
			regoin.setPk_country("0001Z010000000079UJJ");
		}
	}

	public void setFieldRelationUtil(FieldRelationUtil fieldRelationUtil) {
		this.fieldRelationUtil = fieldRelationUtil;
	}

	public void setModel(PsndocModel model) {
		super.setModel(model);
	}

	public void setSuperValidator(SuperFormEditorValidatorUtil superValidator) {
		this.superValidator = superValidator;
	}

	public void setValue(Object object) {
		getBillCardPanel().getBillData().setHeaderValueVO(null);
		String[] strTabCodes = getBillCardPanel().getBillData().getBodyTableCodes();
		if (strTabCodes != null) {
			for (String strTabCode : strTabCodes) {
				getBillCardPanel().getBillModel(strTabCode).clearBodyData();
			}
		}
		super.setValue(object);
		IBusinessEntity be = getBillCardPanel().getBillData().getBillTempletVO().getHeadVO()
				.getBillMetaDataBusinessEntity();
		BillItem[] headItems = getBillCardPanel().getBillData().getHeadTailItems();
		if (headItems == null) {
			return;
		}
		PsndocVO psndocVO = null;
		NCObject ncobject = null;
		if (be.getBeanStyle().getStyle() == BeanStyleEnum.AGGVO_HEAD) {
			ncobject = DASFacade.newInstanceWithContainedObject(be, object);
		} else if ((be.getBeanStyle().getStyle() == BeanStyleEnum.NCVO)
				|| (be.getBeanStyle().getStyle() == BeanStyleEnum.POJO)) {
			if ((object instanceof AggregatedValueObject)) {
				object = ((AggregatedValueObject) object).getParentVO();
				ncobject = DASFacade.newInstanceWithContainedObject(be, object);
			} else {
				ncobject = DASFacade.newInstanceWithContainedObject(be, object);
			}
		}
		if (ncobject == null) {
			return;
		}
		psndocVO = (PsndocVO) ncobject.getModelConsistObject();

		try {
			BatchMatchContext.getShareInstance().setInBatchMatch(true);
			BatchMatchContext.getShareInstance().clear();
			for (BillItem item : headItems) {
				boolean isOrgTableItem = item.getKey().startsWith("hi_psnorg_");
				boolean isJobTableItem = item.getKey().startsWith("hi_psnjob_");
				boolean isOrgTable = item.getTableCode() != null && item.getTableCode().equals("hi_psnorg");
				boolean isJobTable = item.getTableCode() != null && item.getTableCode().equals("hi_psnjob");
				if (isOrgTableItem || isJobTableItem || isOrgTable || isJobTable
						&& (item.getMetaDataProperty() != null)) {
					if (!isOrgTableItem && isOrgTable) {
						item.setKey("hi_psnorg_" + item.getKey());
					} else if (!isJobTableItem && isJobTable) {
						item.setKey("hi_psnjob_" + item.getKey());
					}
					Object value = psndocVO.getAttributeValue(item.getKey());
					if (item.isIsDef()) {
						value = item.converType(value);
					}
					item.setValue(value);
				}
			}
			BatchMatchContext.getShareInstance().executeBatch();
		} finally {
			BatchMatchContext.getShareInstance().setInBatchMatch(false);
		}
		getBillCardPanel().execHeadLoadFormulas();
	}

	private void subTabChanged(ChangeEvent evt) {
		loadCurrentRowSubData();
		// ssx added on 2020-06-10
		// 增加按第2列顺序排序逻辑，以免画面排序混乱
		try {
			List<SortItem> sortList = new ArrayList<SortItem>();
			SortItem sItem = new SortItem(2);
			sItem.setAscending(true);
			sortList.add(sItem);
			((IMutilSortableTableModel) ((UITable) getBillCardPanel().getBillTable(
					((BillTabbedPane) evt.getSource()).getSelectedTableCode())).getModel()).sortByColumns(sortList,
					new int[0]);
			// end
			getModel().fireEvent(new AppEvent("tabchanged", evt.getSource(), null));
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	}

	private void fillData() {
		if (!isShowing()) {
			return;
		}

		try {
			if (getModel().getSelectedData() != null) {
				Object agg = ((IPsndocService) NCLocator.getInstance().lookup(IPsndocService.class))
						.fillData4Psndoc(getModel().getSelectedData());
				int i = agg == null ? -1 : getModel().findBusinessData(agg);
				if (i >= 0) {
					getModel().getData().set(i, agg);
				}
			}
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
	}

	protected void synchronizeDataFromModel() {
		fillData();

		Object selectedData = getModel().getSelectedData();

		if (selectedData == null) {
			getModel().setPk_psncl(null);
			getModel().setCurrentPkPsndoc(null);
		}

		if ((selectedData instanceof PsndocAggVO)) {
			getModel().setPk_psncl(((PsndocAggVO) selectedData).getParentVO().getPsnJobVO().getPk_psncl());
			getModel().setCurrentPkPsndoc(((PsndocAggVO) selectedData).getParentVO().getPk_psndoc());
		}

		filteByPsncl();
		super.synchronizeDataFromModel();
		getBillCardPanel().getBillData().loadLoadHeadRelation();

		PsndocAggVO seldata = (PsndocAggVO) getModel().getSelectedData();
		if (seldata == null) {
			return;
		}
		int tabIndex = getBillCardPanel().getBodyTabbedPane().getSelectedIndex();
		if (tabIndex < 0) {

			return;
		}

		BillModel billModel = getBillCardPanel().getBillModel();
		String strTabCode = billModel.getTabvo().getTabcode();
		if ((seldata.getTableVO(strTabCode) == null) || (seldata.getTableVO(strTabCode).length == 0)) {
			loadCurrentRowSubData();
		}
	}

	private SimpleDocServiceTemplate getService() {
		return new SimpleDocServiceTemplate("PsndocFormEditor");
	}

	private void validateData(PsndocAggVO psndocAggVO) throws BusinessException {
		String[] busiSet = { PsnOrgVO.getDefaultTableName(), PsnJobVO.getDefaultTableName(),
				TrialVO.getDefaultTableName(), PsnChgVO.getDefaultTableName(), RetireVO.getDefaultTableName() };

		String[] checkSet = { PsnJobVO.getDefaultTableName(), PsnChgVO.getDefaultTableName(),
				RetireVO.getDefaultTableName() };

		String[] tabCodes = psndocAggVO.getTableCodes();
		for (int i = 0; (tabCodes != null) && (i < tabCodes.length); i++) {
			if ((psndocAggVO.getTableVO(tabCodes[i]) != null) && (psndocAggVO.getTableVO(tabCodes[i]).length != 0)) {

				if (!CtrtVO.getDefaultTableName().equals(tabCodes[i])) {

					BillItem begin = getBillCardPanel().getBodyItem(tabCodes[i], "begindate");
					BillItem end = getBillCardPanel().getBodyItem(tabCodes[i], "enddate");
					if ((begin != null) && (end != null)) {

						boolean isBusinessSub = ArrayUtils.contains(busiSet, tabCodes[i]);
						boolean isCheckBtwRds = ArrayUtils.contains(checkSet, tabCodes[i]);
						String tableName = getBillCardPanel().getBillData().getBodyTableName(tabCodes[i]);
						String beginName = begin.getName();
						String endName = end.getName();
						CommnonValidator.validateLiteralDate(psndocAggVO.getTableVO(tabCodes[i]), "begindate",
								beginName, "enddate", endName, tableName, isBusinessSub, isCheckBtwRds);
					}
				}
			}
		}
	}

	public void setHashSubHaveLoad(HashSet<String> hashSubHaveLoad) {
		this.hashSubHaveLoad = hashSubHaveLoad;
	}

	public HashSet<String> getHashSubHaveLoad() {
		return hashSubHaveLoad;
	}

	private void setCellRenderer() {
		BillModel bm = getBillCardPanel().getBillModel(QulifyVO.getDefaultTableName());
		if (bm == null) {
			return;
		}
		int colIndex = bm.getBodyColByKey("authenyear");
		UITable bt = getBillCardPanel().getBillTable(QulifyVO.getDefaultTableName());
		if (bt == null) {
			return;
		}
		colIndex = bt.convertColumnIndexToView(colIndex);
		if (colIndex < 0) {
			return;
		}
		bt.getColumnModel().getColumn(colIndex).setCellRenderer(new AuthenyearCellRenderer());
	}

	public void focusGained(FocusEvent arg0) {
		String id = (String) getBillCardPanel().getBodyItem(CertVO.getDefaultTableName(), "id").getValueObject();
		if (StringUtils.isNotEmpty(id)) {
			if (id.endsWith(")")) {
				id = id.substring(0, id.length() - 3) + id.charAt(id.length() - 2);
			}
			getBillCardPanel().getBodyItem(CertVO.getDefaultTableName(), "id").setValue(id);
		}
	}

	public void focusLost(FocusEvent arg0) {
		String[] formulas = getBillCardPanel().getBodyItem("id").getLoadFormula();
		getBillCardPanel().getBillData().getBillModel(CertVO.getDefaultTableName()).execFormulas(selectedRow, formulas);
	}

	private IPersistenceRetrieve getServiece() {
		if (retrieveService == null) {
			return (IPersistenceRetrieve) NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
		}
		return retrieveService;
	}

	private String getCluster(String dept) {
		if (dept == null || dept.equals(""))
			return "";
		String queryClusterSql = "SELECT glbdef1 FROM org_dept WHERE pk_dept='" + dept + "'";

		IUAPQueryBS impl = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());

		ArrayList<HashMap<String, Object>> result = null;

		try {
			result = (ArrayList<HashMap<String, Object>>) impl.executeQuery(queryClusterSql, new MapListProcessor());
		} catch (BusinessException e1) {
			result = null;
			return "";
		}
		return result == null ? "" : (String) result.get(0).get("glbdef1");
	}

	private String getPrincipal(String dept, Boolean isSupervisor) {
		if (dept == null || dept.equals(""))
			return "";
		String queryClusterSql = null;
		if (isSupervisor.booleanValue()) {
			queryClusterSql = "select principal from org_dept where  pk_dept =(select pk_fatherorg from org_dept where pk_dept='"
					+ dept + "' and dr = 0) and dr =0 ";
		} else {
			queryClusterSql = "SELECT principal FROM org_dept WHERE pk_dept='" + dept + "' and dr=0 ";
		}

		IUAPQueryBS impl = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());

		ArrayList<HashMap<String, Object>> result = null;

		try {
			result = (ArrayList<HashMap<String, Object>>) impl.executeQuery(queryClusterSql, new MapListProcessor());
		} catch (BusinessException e1) {
			result = null;
			return "";
		}
		return result == null ? "" : (String) result.get(0).get("principal");
	}

}
