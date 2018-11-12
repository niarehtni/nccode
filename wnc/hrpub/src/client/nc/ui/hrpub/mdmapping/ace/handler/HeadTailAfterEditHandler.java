package nc.ui.hrpub.mdmapping.ace.handler;

import java.util.ArrayList;
import java.util.List;

import nc.md.innerservice.MDQueryService;
import nc.md.model.IAttribute;
import nc.md.model.IBusinessEntity;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.vo.hrpub.mdmapping.AggMDClassVO;
import nc.vo.hrpub.mdmapping.MDClassVO;
import nc.vo.hrpub.mdmapping.MDPropertyVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.uif2.LoginContext;

public class HeadTailAfterEditHandler implements
		IAppEventHandler<CardHeadTailAfterEditEvent> {

	private BillForm editor;
	private LoginContext context;

	@Override
	public void handleAppEvent(CardHeadTailAfterEditEvent e) {
		if (e.getKey().equals("pk_class")) {
			BillCardPanel card = this.getEditor().getBillCardPanel();
			AggregatedValueObject aggVO = card.getBillValueVO(
					AggMDClassVO.class.getName(), MDClassVO.class.getName(),
					MDPropertyVO.class.getName());

			try {
				generateProperties(card, aggVO);
			} catch (BusinessException ex) {
				ExceptionUtils.wrappBusinessException(ex.getMessage());
			}
		}
	}

	private void generateProperties(BillCardPanel card,
			AggregatedValueObject aggVO) throws BusinessException {
		MDClassVO headVO = (MDClassVO) aggVO.getParentVO();

		IBusinessEntity entity = MDQueryService.lookupMDInnerQueryService()
				.getBusinessEntityByID(headVO.getPk_class());

		List<IAttribute> attribs = entity.getAttributes();
		List<MDPropertyVO> lines = new ArrayList<MDPropertyVO>();
		for (int i = 0; i < attribs.size(); i++) {
			if (attribs.get(i).getName().equals("status")
					|| attribs.get(i).getName().equals("dr")
					|| attribs.get(i).getName().equals("ts")
					|| (attribs.get(i).getAccessStrategy() != null && attribs
							.get(i).getAccessStrategy().getClass().getName()
							.equals("nc.md.model.access.BodyOfAggVOAccessor"))) {
				continue;
			}
			MDPropertyVO newline = new MDPropertyVO();
			newline.setPk_group(headVO.getPk_group());
			newline.setPk_org(headVO.getPk_org());
			newline.setPk_property(attribs.get(i).getID());
			newline.setCreationtime(new UFDateTime());
			newline.setCreator(getContext().getPk_loginUser());
			newline.setPk_group(this.getContext().getPk_group());
			newline.setPk_org(this.getContext().getPk_org());
			lines.add(newline);
		}
		aggVO.setChildrenVO(lines.toArray(new MDPropertyVO[0]));
		card.setBillValueVO(aggVO);

		for (int i = 0; i < card.getRowCount(); i++) {
			String id = (String) card.getBodyValueAt(i, "pk_property");
			for (IAttribute attrib : attribs) {
				if (attrib.getID().equals(id)) {
					card.setBodyValueAt(attrib.getDisplayName(), i,
							"displayname");
					card.setBodyValueAt(
							attrib.getDataType().getClass().getName()
									.equals("nc.md.model.type.impl.RefType") ? ((nc.md.model.type.impl.RefType) attrib
									.getDataType()).getRefType()
									.getDisplayName() : attrib.getDataType()
									.getDisplayName(), i, "datatype");
					card.setBodyValueAt(attrib.getLength(), i, "length");
					card.setBodyValueAt(attrib.getPrecise(), i, "precision");
					if (entity.getPrimaryKey().getPKColumn().getName()
							.equals(attrib.getName())) {
						card.setBodyValueAt(UFBoolean.TRUE, i, "iskey");
					} else {
						card.setBodyValueAt(UFBoolean.FALSE, i, "iskey");
					}
				}
			}
		}
	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

	public LoginContext getContext() {
		return context;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}

}
