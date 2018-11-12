package nc.ui.ta.item.formula.varcreator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ITimeItemQueryService;
import nc.ui.hr.formula.variable.Content;
import nc.vo.pub.BusinessException;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;

public class OvertimeContentCreator extends TimeItemContentCreator {

	public OvertimeContentCreator() {

	}

	@Override
	public Content[] createContents(Object... params) {
		String pk_org = getModel().getContext().getPk_org();
		TimeItemCopyVO[] vos = null;
		try {
			vos = NCLocator
					.getInstance()
					.lookup(ITimeItemQueryService.class)
					.queryCopyTypesByOrgAndItemType(pk_org,
							TimeItemVO.OVERTIME_TYPE);
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
				int timeitemUnit = vo.getTimeitemunit();
				content.setDescription(MessageFormat.format(
						desc,
						vo.getMultilangName(),
						timeitemUnit == TimeItemCopyVO.TIMEITEMUNIT_DAY ? PublicLangRes
								.DAY() : PublicLangRes.HOUR()));

				content = new Content();
				contentList.add(content);
				String contentName2 = ResHelper.getString("6017basedoc",
						"06017basedoc1843")
				/* @res "{0}转调休时长" */;
				content.setContentName(MessageFormat.format(contentName2,
						vo.getMultilangName()));
				content.setColName("_OVER_TO_REST" + vo.getPk_timeitem());
				String desc2 = ResHelper.getString("6017basedoc",
						"06017basedoc1844")
				/*
				 * @res
				 * "{0}的转调休时长,单位为{1}。因为各人力资源组织可以自行修改时长单位，因此时长单位应该以人力资源组织实际设置的为准。"
				 */;
				content.setDescription(MessageFormat.format(
						desc2,
						vo.getMultilangName(),
						timeitemUnit == TimeItemCopyVO.TIMEITEMUNIT_DAY ? PublicLangRes
								.DAY() : PublicLangRes.HOUR()));
				
				
				
				//总时长  yejk 2018-08-28  #21266
				content = new Content();
				contentList.add(content);
				String contentName3 = ResHelper.getString("6017basedoc",
						"06017basedocext_0001");

				content.setContentName(MessageFormat.format(contentName3,
						new Object[] { vo.getMultilangName() }));
				content.setColName("_OVERTIME_SUM_HOUR" + vo.getPk_timeitem());
				String desc3 = ResHelper.getString("6017basedoc",
						"06017basedocext_0002");

				content.setDescription(MessageFormat.format(desc3,
						new Object[] {
								vo.getMultilangName(),
								timeitemUnit == 0 ? PublicLangRes.DAY()
										: PublicLangRes.HOUR() }));
				
				//转调休总时长  yejk 2018-08-28  #21266
				content = new Content();
				contentList.add(content);
				String contentName4 = ResHelper.getString("6017basedoc",
						"06017basedocext_0003");

				content.setContentName(MessageFormat.format(contentName4,
						new Object[] { vo.getMultilangName() }));
				content.setColName("_OVERTIME_SUM_HOUR_TOREST" + vo.getPk_timeitem());
				String desc4 = ResHelper.getString("6017basedoc",
						"06017basedocext_0004");

				content.setDescription(MessageFormat.format(desc4,
						new Object[] {
								vo.getMultilangName(),
								timeitemUnit == 0 ? PublicLangRes.DAY()
										: PublicLangRes.HOUR() }));
			}
		}

		return contentList.toArray(new Content[0]);
	}

}
