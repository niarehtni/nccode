package nc.ui.om.hrdept.action;


public class DeptManager {
	public int rowno=0;
	public String deptcode=null;
	public String org=null;
	public String deptmanager=null;
	public Boolean isfuzeren=null;	//­t³d¤H
	
	//rowno
	public void setRowNo(int row)
	{
		rowno=row;
	}
	
	public int getRowNo()
	{
		return rowno;
	}
	
	
	//Dept code
	public void setDeptcode(String sdeptCode)
	{
		deptcode=sdeptCode;
	}
	
	public String getDeptcode()
	{
		return deptcode;
	}
	
	//org
	public void setOrg(String sorg)
	{
		org=sorg;
	}
	
	public String getOrg()
	{
		return org;
	}
	
	//deptmanager
	public void setDeptManager(String sdeptmanager)
	{
		deptmanager=sdeptmanager;
	}
	
	public String getDeptManager()
	{
		return deptmanager;
	}
	
	//isfuzeren
	public void setIsfuzeren(Boolean sfuzeren)
	{
		isfuzeren=sfuzeren;
	}
	
	public Boolean getIsfuzeren()
	{
		return isfuzeren;
	}
	
}
