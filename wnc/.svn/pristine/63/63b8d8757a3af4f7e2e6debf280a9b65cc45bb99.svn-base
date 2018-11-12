package nc.ui.hrpub.mdmapping.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.md.innerservice.MDQueryService;
import nc.md.model.IAttribute;
import nc.md.model.IBusinessEntity;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.uif2.NCAction;
import nc.vo.hrpub.mdmapping.AggMDClassVO;
import nc.vo.hrpub.mdmapping.MDClassVO;
import nc.vo.hrpub.mdmapping.MDPropertyVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.uif2.LoginContext;

public class SynMetaPropertiesAction extends NCAction {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = -2895471547416080694L;
	private BillForm editor;
	private LoginContext context;

	public SynMetaPropertiesAction() {
		setBtnName("同步元数据");
		setCode("synMetaAction");
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		BillCardPanel card = this.getEditor().getBillCardPanel();
		AggregatedValueObject aggVO = card.getBillValueVO(
				AggMDClassVO.class.getName(), MDClassVO.class.getName(),
				MDPropertyVO.class.getName());
		try {
			synProperties(card, aggVO);
		} catch (BusinessException ex) {
			ExceptionUtils.wrappBusinessException(ex.getMessage());
		}
	}

	private void synProperties(BillCardPanel card, AggregatedValueObject aggVO)
			throws BusinessException {
		MDClassVO headVO = (MDClassVO) aggVO.getParentVO();

		IBusinessEntity entity = MDQueryService.lookupMDInnerQueryService()
				.getBusinessEntityByID(headVO.getPk_class());

		List<IAttribute> attribs = entity.getAttributes();
		List<MDPropertyVO> lines = new ArrayList<MDPropertyVO>();
		List<String> newIDs = new ArrayList<String>();
		for (CircularlyAccessibleValueObject linevo : aggVO.getChildrenVO()) {
			lines.add((MDPropertyVO) linevo);
		}

		for (int i = 0; i < attribs.size(); i++) {
			if (attribs.get(i).getName().equals("status")
					|| attribs.get(i).getName().equals("dr")
					|| attribs.get(i).getName().equals("ts")
					|| (attribs.get(i).getAccessStrategy() != null && attribs
							.get(i).getAccessStrategy().getClass().getName()
							.equals("nc.md.model.access.BodyOfAggVOAccessor"))) {
				continue;
			}

			updateLinePropID(lines, attribs.get(i).getID(), attribs.get(i)
					.getDisplayName());

			if (!isExistsLine(lines, attribs.get(i).getID())) {
				MDPropertyVO newline = new MDPropertyVO();
				newline.setPk_group(headVO.getPk_group());
				newline.setPk_org(headVO.getPk_org());
				newline.setPk_property(attribs.get(i).getID());
				newline.setCreationtime(new UFDateTime());
				newline.setCreator(getContext().getPk_loginUser());
				newline.setPk_group(this.getContext().getPk_group());
				newline.setPk_org(this.getContext().getPk_org());
				newIDs.add(attribs.get(i).getID());
				lines.add(newline);
			}
		}
		aggVO.setChildrenVO(lines.toArray(new MDPropertyVO[0]));
		card.setBillValueVO(aggVO);

		for (int i = 0; i < card.getRowCount(); i++) {
			String id = (String) card.getBodyValueAt(i, "pk_property");
			if (newIDs.contains(id)) {
				for (IAttribute attrib : attribs) {
					if (attrib.getID().equals(id)) {
						card.setBodyValueAt(attrib.getDisplayName(), i,
								"displayname");
						card.setBodyValueAt(
								attrib.getDataType()
										.getClass()
										.getName()
										.equals("nc.md.model.type.impl.RefType") ? ((nc.md.model.type.impl.RefType) attrib
										.getDataType()).getRefType()
										.getDisplayName() : attrib
										.getDataType().getDisplayName(), i,
								"datatype");
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
	}

	private void updateLinePropID(List<MDPropertyVO> lines, String id,
			String displayName) {
		for (MDPropertyVO vo : lines) {
			if (displayName.equals(vo.getDisplayname())
					&& id != vo.getPk_property()) {
				vo.setPk_property(id);
			}
		}
	}

	private boolean isExistsLine(List<MDPropertyVO> lines, String id) {
		for (MDPropertyVO vo : lines) {
			if (id.equals(vo.getPk_property())) {
				return true;
			}
		}
		return false;
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
