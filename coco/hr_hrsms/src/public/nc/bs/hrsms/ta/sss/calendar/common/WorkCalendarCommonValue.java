package nc.bs.hrsms.ta.sss.calendar.common;

import nc.hr.utils.CommonUtils;
import nc.vo.pub.lang.UFLiteralDate;

public class WorkCalendarCommonValue {

	 public WorkCalendarCommonValue()
	    {
	    }

	    public static String[] createExportFields(UFLiteralDate beginDate, UFLiteralDate endDate)
	    {
	        UFLiteralDate dateScope[] = CommonUtils.createDateArray(beginDate, endDate);
	        String fields[] = new String[dateScope.length + 4];
	        fields[0] = "clerkcode";
	        fields[1] = "psncode";
	        fields[2] = "psnname";
	        fields[3] = "totaltimes";
	        for(int i = 4; i < fields.length; i++)
	            fields[i] = dateScope[i - 4].toString();

	        return fields;
	    }

	    public static final String LISTCODE_CLERKCODE = "clerkcode";
	    public static final String LISTCODE_PSNCODE = "psncode";
	    public static final String LISTCODE_PSNNAME = "psnname";
	    public static final String LISTCODE_ISNULLROW = "isnullrow";
	    public static final String LISTCODE_PKPSNDOC = "pk_psndoc";
	    public static final String LISTCODE_TOTALTIMES = "totaltimes";
}
