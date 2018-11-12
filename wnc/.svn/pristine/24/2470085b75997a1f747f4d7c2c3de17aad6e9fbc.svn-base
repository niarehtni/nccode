package nc.ui.hrpub.ioschema.ace.view;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrpub.IIOSchemaMaintain;
import nc.pub.templet.converter.util.helper.ExceptionUtils;
import nc.ui.pubapp.uif2app.model.BatchModelDataManager;
import nc.vo.hrpub.mdmapping.IOSchemaVO;
import nc.vo.pub.BusinessException;

public class IOSchemaDataManager extends BatchModelDataManager {
	public void initModel() {
		IOSchemaVO[] allVOs = null;
		try {
			allVOs = getQueryIOSchemaService().queryAll();
		} catch (BusinessException e) {
			ExceptionUtils.wrapException(e);
		}
		model.initModel(allVOs);
	}

	private IIOSchemaMaintain getQueryIOSchemaService() {
		return (IIOSchemaMaintain) NCLocator.getInstance().lookup(
				IIOSchemaMaintain.class);
	}
}
