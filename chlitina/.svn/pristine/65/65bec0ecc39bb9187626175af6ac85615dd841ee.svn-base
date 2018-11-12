package nc.ui.ta.item.formula.varcreator;

import java.util.ArrayList;
import java.util.List;

import nc.ui.hr.formula.variable.Content;
import nc.ui.hr.formula.variable.MDContentCreator;

/**
 * 变量区域的人员组织关系信息创建器
 * 
 * @author wangdca
 */
@SuppressWarnings("serial")
public class LeaveRulePsnorgContentCreator extends MDContentCreator {
    @Override
    protected String getMDID() {
	return "3a521756-6a6b-4027-858e-e64104b7851b";
    }

    private final ArrayList<String> list = new ArrayList<String>() {
	{
	    add("joinsysdate");
	    add("begindate");
	    add("enddate");
	    add("adjustcorpage");
	    add("workagestartdate");
	}
    };

    @Override
    public Content[] createContents(Object... params) {
	Content[] createContents = super.createContents(params);
	// 只需要进出组织日期和进入集团日期
	List<Content> returnList = new ArrayList<Content>();
	for (Content c : createContents) {
	    if (list.contains(c.getColName()))
		returnList.add(c);
	}
	return returnList.toArray(new Content[0]);
    }
}
