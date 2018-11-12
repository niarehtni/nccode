package nc.ui.ta.item.formula.varcreator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ITimeItemQueryService;
import nc.ui.hr.formula.variable.AbstractContentCreator;
import nc.ui.hr.formula.variable.Content;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.timeitem.TimeItemCopyVO;

public class TimeItemContentCreator extends AbstractContentCreator {

	private int itemType;

	public Content[] createContents(Object... params) {
		String pk_org = getModel().getContext().getPk_org();
		TimeItemCopyVO[] vos = null;
		try {
			vos = NCLocator.getInstance().lookup(ITimeItemQueryService.class)
					.queryCopyTypesByOrgAndItemType(pk_org, itemType);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			return null;
		}
		List<Content> contentList = new ArrayList<Content>();
		if (vos != null && vos.length > 0) {
			for (TimeItemCopyVO vo : vos) {
				Content content = new Content();
				contentList.add(content);
				String contentName = ResHelper.getString("6017basedoc",
						"06017basedoc1423")
				/* @res "{0}时长" */;
				content.setContentName(MessageFormat.format(contentName,
						vo.getMultilangName()));
				content.setColName(vo.getPk_timeitem());
				String desc = ResHelper.getString("6017basedoc",
						"06017basedoc1424")
				/*
				 * @res
				 * "{0}的时长,单位为{1}。因为各人力资源组织可以自行修改时长单位，因此时长单位应该以人力资源组织实际设置的为准。"
				 */;

				if (vo.getItemtype() == TimeItemCopyVO.LEAVE_TYPE) {
					Content cont = new Content();
					contentList.add(cont);
					String contName = ResHelper.getString("6017basedoc",
							"2basedocext-000001")/* @res "{0}非当月单据当月审批" */;
					cont.setContentName(MessageFormat.format(contName,
							vo.getMultilangName()));
					cont.setColName("leave_notcurrmonth_."
							+ vo.getPk_timeitem());
					
					
					//实际时长  yejk 2018-08-31
					int timeitemUnit = vo.getTimeitemunit();
					Content content2 = new Content();
					contentList.add(content2);
					String contentName2 = ResHelper.getString("6017basedoc",
							"3basedocext-000001");

					content2.setContentName(MessageFormat.format(contentName2,
							new Object[] { vo.getMultilangName() }));
					content2.setColName("_LEAVE_ACTUAL_HOUR" + vo.getPk_timeitem());
					String desc2 = ResHelper.getString("6017basedoc",
							"3basedocext-000002");

					content2.setDescription(MessageFormat.format(desc2,
							new Object[] {
									vo.getMultilangName(),
									timeitemUnit == 0 ? PublicLangRes.DAY()
											: PublicLangRes.HOUR() }));
				}
				int timeitemUnit = vo.getTimeitemunit();
				content.setDescription(MessageFormat.format(
						desc,
						vo.getMultilangName(),
						timeitemUnit == TimeItemCopyVO.TIMEITEMUNIT_DAY ? PublicLangRes
								.DAY() : PublicLangRes.HOUR()));
			}
		}
		return contentList.toArray(new Content[0]);
	}

	public int getItemType() {
		return itemType;
	}

	public void setItemType(int itemType) {
		this.itemType = itemType;
	}
}