package nc.ui.ta.machineset.ref;

import nc.hr.utils.ResHelper;
import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.ta.machineset.MachineVO;

public class MachineSetRefModel extends AbstractRefModel {

	public MachineSetRefModel(){
		super();
		reset();
	}

	@Override
    public void reset(){
		setRefTitle(ResHelper.getString("6017basedoc","06017basedoc1454")
/*@res "考勤机地点"*/);
        setDefaultFieldCount(2);
        setPkFieldCode(MachineVO.PK_MACHINESET);
        setRefCodeField(MachineVO.MACHINECODE);
        setRefNameField(MachineVO.MACHINENAME);
        setTableName(MachineVO.getDefaultTableName());
		setHiddenFieldCode(new String[]{MachineVO.PK_MACHINESET});
		setRefNodeName("考勤机地点"); /*-=notranslate=-*/
        setFieldCode(new String[]{MachineVO.MACHINECODE, MachineVO.MACHINENAME});

        setFieldName(new String[]{ResHelper.getString("6017basedoc","06017basedoc1455")
/*@res "考勤机编码"*/, ResHelper.getString("6017basedoc","06017basedoc1456")
/*@res "考勤机名称"*/});

        resetFieldName();
	}
}