package nc.ui.twhr.nhicalc.ace.view;

import nc.bs.logging.Logger;
import nc.ui.pubapp.uif2app.view.BatchBillTable;
import nc.ui.uif2.components.AutoShowUpEventSource;
import nc.ui.uif2.components.IAutoShowUpComponent;
import nc.ui.uif2.components.IAutoShowUpEventListener;
import nc.ui.uif2.components.ITabbedPaneAwareComponent;
import nc.ui.uif2.components.ITabbedPaneAwareComponentListener;
import nc.ui.uif2.components.TabbedPaneAwareCompnonetDelegate;
import nc.ui.uif2.editor.TemplateContainer;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.bill.BillTempletVO;

public class ShowUpableBatchBillTableaccount extends BatchBillTable implements
		ITabbedPaneAwareComponent, IAutoShowUpComponent {
	private static final long serialVersionUID = 1L;
	private AutoShowUpEventSource autoShowUpComponent = new AutoShowUpEventSource(
			this);
	private TemplateContainer templateContainer;
	private TabbedPaneAwareCompnonetDelegate tabbedPaneAwareCompnonetDelegate = new TabbedPaneAwareCompnonetDelegate();
	private String beanId;
	private String nodekey;

	public ShowUpableBatchBillTableaccount() {
	}

	public void addTabbedPaneAwareComponentListener(
			ITabbedPaneAwareComponentListener l) {
		this.tabbedPaneAwareCompnonetDelegate
				.addTabbedPaneAwareComponentListener(l);
	}

	public boolean canBeHidden() {
		return this.tabbedPaneAwareCompnonetDelegate.canBeHidden();
	}

	public boolean isComponentVisible() {
		return this.tabbedPaneAwareCompnonetDelegate.isComponentVisible();
	}

	public void setAutoShowUpEventListener(IAutoShowUpEventListener l) {
		this.autoShowUpComponent.setAutoShowUpEventListener(l);
	}

	public void setComponentVisible(boolean visible) {
		this.tabbedPaneAwareCompnonetDelegate.setComponentVisible(visible);
	}

	public void showMeUp() {
		this.autoShowUpComponent.showMeUp();
	}

	protected void setTemplateVO() {
		BillTempletVO template = null;
		if (this.templateContainer == null) {

			this.billCardPanel.setBillType("68J62507");
			this.billCardPanel.setBusiType(null);
			this.billCardPanel.setOperator(this.getModel().getContext()
					.getPk_loginUser());
			this.billCardPanel.setCorp(this.getModel().getContext()
					.getPk_group());
			template = this.billCardPanel.getDefaultTemplet(
					this.billCardPanel.getBillType(), null,
					this.billCardPanel.getOperator(),
					this.billCardPanel.getCorp(), this.getNodekey(), null);

		} else {

			template = this.templateContainer.getTemplate(this.getNodekey(),
					null, null);
		}
		if (template == null) {
			Logger.error("没有找到nodekey：" + this.getNodekey() + "对应的卡片模板");
			throw new IllegalArgumentException(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("uif2", "BatchBillTable-000000"));
		}

		if (StringUtil.isEmptyWithTrim(this.getBeanId())) {
			beanId = (template.getPKBillTemplet() + "_BatchBillTable");
		}
		setBillData(template);
	}

}
