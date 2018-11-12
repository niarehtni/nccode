package nc.ui.hi.ref;

import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.org.OrgVO;
import nc.vo.org.orgmodel.OrgTypeVO;
import nc.vo.org.util.OrgTypeManager;

public class FileNumberRefModel extends AbstractRefModel
{
	private String pk_psndoc;
	public FileNumberRefModel() {
		super();
		super.setOrderPart("filenumber");
		super.setMutilLangNameRef(false);
	  }

	public String getPk_psndoc() {
		return pk_psndoc;
	}

	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	public String[] getFieldCode() {
		return new String[] { "filenumber", "filenumber as name" };
	    }

	    public String[] getFieldName() {
		return new String[] { "±àÂë", "Ãû³Æ"};
	    }

	    public int getDefaultFieldCount() {
		return 2;
	    }


	    public java.lang.String getPkFieldCode() {
		return "filenumber";
	    }

	    public java.lang.String getRefTitle() {
		return "µµ°¸±àºÅ";
	    }


	    public java.lang.String getTableName() {
		return " hi_psndoc_courtdeduction ";
	    }

	  
	  public String getWherePart() {
			return " pk_psndoc='"+pk_psndoc+"' and (isstop ='N' or isstop is null) ";
		    }
	}
