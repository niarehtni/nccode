package nc.ui.ta.item.formula.varcreator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IGlobalCountryQueryService;
import nc.itf.ta.ITimeItemQueryService;
import nc.ui.hr.formula.variable.Content;
import nc.vo.bd.countryzone.CountryZoneVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.OrgQueryUtil;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;

public class OvertimeContentCreator extends TimeItemContentCreator {

	public OvertimeContentCreator() {
		
	}
	@Override
	public Content[] createContents(Object... params) {
		String pk_org =getModel().getContext().getPk_org();
		TimeItemCopyVO[] vos = null;
		try {
			vos = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryCopyTypesByOrgAndItemType(pk_org, TimeItemVO.OVERTIME_TYPE);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			return null;
		}
		List<Content> contentList = new ArrayList<Content>();
		if(vos!=null&&vos.length>0){
			for(TimeItemCopyVO vo:vos){
				Content content = new Content();
				contentList.add(content);
				String contentName = ResHelper.getString("6017basedoc","06017basedoc1423")
/*@res "{0}时长"*/;
				content.setContentName(MessageFormat.format(contentName, vo.getMultilangName()));
				content.setColName(vo.getPk_timeitem());
				
				//MOD 增加非当月单据当月审批 James 2017/09/29
				if(vo.getItemtype()==TimeItemCopyVO.OVERTIME_TYPE)
				{
					try {
						if (isTWOrg(pk_org)) {
							if("1001A1100000000009PC".equals(vo.getPk_timeitem())){//平日加班
								Content cont = new Content();
								contentList.add(cont);
								String contName = ResHelper.getString("6017basedoc","2basedocext-000004");
								
								cont.setContentName(MessageFormat.format(contName, vo.getMultilangName()));
								cont.setColName("overtime_notcurrmonth_.WorkFirst2H."+vo.getPk_timeitem());
								
								Content cont1 = new Content();
								contentList.add(cont1);
								String contName1 = ResHelper.getString("6017basedoc","2basedocext-000005");
								cont1.setContentName(MessageFormat.format(contName1, vo.getMultilangName()));
								cont1.setColName("overtime_notcurrmonth_.WorkLast2H."+vo.getPk_timeitem());
							}else if("1001A1100000000009PE".equals(vo.getPk_timeitem())){//国定假日加班
								Content cont = new Content();
								contentList.add(cont);
								String contName = ResHelper.getString("6017basedoc","2basedocext-000006");
								
								cont.setContentName(MessageFormat.format(contName, vo.getMultilangName()));
								cont.setColName("overtime_notcurrmonth_.HolidayFirst8H."+vo.getPk_timeitem());
								
								Content cont1 = new Content();
								contentList.add(cont1);
								String contName1 = ResHelper.getString("6017basedoc","2basedocext-000007");
								cont1.setContentName(MessageFormat.format(contName1, vo.getMultilangName()));
								cont1.setColName("overtime_notcurrmonth_.Holiday8To12H."+vo.getPk_timeitem());
								
								Content cont2 = new Content();
								contentList.add(cont2);
								String contName2 = ResHelper.getString("6017basedoc","2basedocext-000008");
								cont2.setContentName(MessageFormat.format(contName2, vo.getMultilangName()));
								cont2.setColName("overtime_notcurrmonth_.HolidayAfter12H."+vo.getPk_timeitem());
							}
							else if("1001A1100000000009PG".equals(vo.getPk_timeitem())){//休息日加班
								Content cont = new Content();
								contentList.add(cont);
								String contName = ResHelper.getString("6017basedoc","2basedocext-000009");
								
								cont.setContentName(MessageFormat.format(contName, vo.getMultilangName()));
								cont.setColName("overtime_notcurrmonth_.RestFirst2H."+vo.getPk_timeitem());
								
								Content cont1 = new Content();
								contentList.add(cont1);
								String contName1 = ResHelper.getString("6017basedoc","2basedocext-000010");
								cont1.setContentName(MessageFormat.format(contName1, vo.getMultilangName()));
								cont1.setColName("overtime_notcurrmonth_.Rest2To8H."+vo.getPk_timeitem());
								
								Content cont2 = new Content();
								contentList.add(cont2);
								String contName2 = ResHelper.getString("6017basedoc","2basedocext-000011");
								cont2.setContentName(MessageFormat.format(contName2, vo.getMultilangName()));
								cont2.setColName("overtime_notcurrmonth_.RestAfter8H."+vo.getPk_timeitem());
							}
							
						}
						else{
							Content cont = new Content();
							contentList.add(cont);
							String contName = ResHelper.getString("6017basedoc","2basedocext-000002")
							/*@res "{0}非当月单据当月审批"*/;
							cont.setContentName(MessageFormat.format(contName, vo.getMultilangName()));
							cont.setColName("overtime_notcurrmonth_."+vo.getPk_timeitem());
						}
					} catch (BusinessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				String desc = ResHelper.getString("6017basedoc","06017basedoc1424")
/*@res "{0}的时长,单位为{1}。因为各人力资源组织可以自行修改时长单位，因此时长单位应该以人力资源组织实际设置的为准。"*/;
				int timeitemUnit = vo.getTimeitemunit();
				content.setDescription(MessageFormat.format(desc, vo.getMultilangName(),timeitemUnit==TimeItemCopyVO.TIMEITEMUNIT_DAY?PublicLangRes.DAY():PublicLangRes.HOUR()));
				
				content = new Content();
				contentList.add(content);
				String contentName2 = ResHelper.getString("6017basedoc","06017basedoc1843")
/*@res "{0}转调休时长"*/;
				content.setContentName(MessageFormat.format(contentName2, vo.getMultilangName()));
				content.setColName("_OVER_TO_REST"+vo.getPk_timeitem());
				
				
				//MOD 增加非当月单据当月审批 James 2017/09/29
				if(vo.getItemtype()==TimeItemCopyVO.OVERTIME_TYPE)
				{
					Content cont = new Content();
					contentList.add(cont);
					String contName = ResHelper.getString("6017basedoc","2basedocext-000003")
					/*@res "{0}非当月单据当月审批"*/;
					cont.setContentName(MessageFormat.format(contName, vo.getMultilangName()));
					cont.setColName("_OVER_TO_RESTovertime_notcurrmonth_."+vo.getPk_timeitem());
				}
				
				String desc2 = ResHelper.getString("6017basedoc","06017basedoc1844")
/*@res "{0}的转调休时长,单位为{1}。因为各人力资源组织可以自行修改时长单位，因此时长单位应该以人力资源组织实际设置的为准。"*/;
				content.setDescription(MessageFormat.format(desc2, vo.getMultilangName(),timeitemUnit==TimeItemCopyVO.TIMEITEMUNIT_DAY?PublicLangRes.DAY():PublicLangRes.HOUR()));
			}
		}
		

		return contentList.toArray(new Content[0]);
	}
	private boolean isTWOrg(String pk_org) throws BusinessException {
		OrgVO[] orgVOs = OrgQueryUtil.queryOrgVOByPks(new String[] { pk_org });
		IGlobalCountryQueryService czQry = NCLocator.getInstance().lookup(IGlobalCountryQueryService.class);
		CountryZoneVO czVO = czQry.getCountryZoneByPK(orgVOs[0].getCountryzone());
		return czVO.getCode().equals("TW");
	}

}
