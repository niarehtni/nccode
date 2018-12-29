package nc.ui.twhr.twhr.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.ui.uif2.NCAction;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.uif2.annoations.MethodType;
import nc.uif2.annoations.ModelMethod;
import nc.uif2.annoations.ModelType;
import nc.vo.twhr.nhicalc.BaoAccountVO;

import com.google.gdata.model.Element;

public class SynchroAction extends NCAction {
	private BatchBillTableModel model = null;
	private IAppModelDataManager modelManager = null;

	@Override
	public void doAction(ActionEvent paramActionEvent) throws Exception {
		// TODO 自动生成的方法存根
		BaoAccountVO[] bao = null;
		Element ele = null;
		List<BaoAccountVO> ggllist = new ArrayList<BaoAccountVO>();

	}

	public SynchroAction() {
		setCode("synchroAction");

	}

	public IAppModelDataManager getModelManager() {
		return modelManager;
	}

	public void setModelManager(IAppModelDataManager modelManager) {
		this.modelManager = modelManager;
	}

	@ModelMethod(modelType = ModelType.BatchBillTableModel, methodType = MethodType.GETTER)
	public BatchBillTableModel getModel() {
		return this.model;
	}

	@ModelMethod(modelType = ModelType.BatchBillTableModel, methodType = MethodType.SETTER)
	public void setModel(BatchBillTableModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

}
