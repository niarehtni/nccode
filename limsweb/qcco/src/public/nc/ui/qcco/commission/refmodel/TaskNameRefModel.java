package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class TaskNameRefModel extends AbstractRefModel{
	public TaskNameRefModel(){
		super();
		this.setTableName("NC_REPORT_TYPE");
		this.setMutilLangNameRef(false);
	}
}
