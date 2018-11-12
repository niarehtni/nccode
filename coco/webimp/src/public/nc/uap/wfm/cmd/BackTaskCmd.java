package nc.uap.wfm.cmd;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.uap.wfm.completesgy.ICompleteSgy;
import nc.uap.wfm.contanier.ProDefsContainer;
import nc.uap.wfm.context.PwfmContext;
import nc.uap.wfm.context.WfmFlowInfoCtx;
import nc.uap.wfm.convert.ModelBuilder;
import nc.uap.wfm.exception.WfmServiceException;
import nc.uap.wfm.handler.PortAndEdgeHandler;
import nc.uap.wfm.itf.IWfmProDefBill;
import nc.uap.wfm.itf.IWfmProDefQry;
import nc.uap.wfm.logger.WfmLogger;
import nc.uap.wfm.model.Activity;
import nc.uap.wfm.model.CompleteStrategy;
import nc.uap.wfm.model.EndEvent;
import nc.uap.wfm.model.GateWay;
import nc.uap.wfm.model.HumAct;
import nc.uap.wfm.model.HumActIns;
import nc.uap.wfm.model.IEdge;
import nc.uap.wfm.model.IPort;
import nc.uap.wfm.model.ProDef;
import nc.uap.wfm.model.ProIns;
import nc.uap.wfm.model.SequenceFlow;
import nc.uap.wfm.model.Task;
import nc.uap.wfm.prodef.robot.PXUtil;
import nc.uap.wfm.prodef.robot.ProdefFacade;
import nc.uap.wfm.prodef.robot.ProdefSerializer;
import nc.uap.wfm.utils.WfmCPTaskUtil;
import nc.uap.wfm.utils.WfmCPUtilFacade;
import nc.uap.wfm.vo.WfmFormInfoCtx;
import nc.uap.wfm.vo.WfmProdefVO;
import nc.uap.wfm.vo.WfmTaskVO;
import nc.vo.jcom.xml.XMLUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.lang.UFBoolean;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * 收回cmd
 *
 *
 */
public class BackTaskCmd extends AbstractCommand implements ICommand<Void> {
	protected Task task = null;
	public BackTaskCmd() {
		super();
	}
	public Void execute() throws WfmServiceException {
		task = PwfmContext.getCurrentBpmnSession().getTask();
		if (task == null) {
			throw new WfmServiceException(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "BackTaskCmd-000009")/*空任务不允许收回*/);
		}
		if (!task.getIsnotexe().booleanValue()) {
			throw new WfmServiceException(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "BackTaskCmd-000010")/*未执行的任务不允许收回*/);
		}
		if(Task.CreateType_BeforeAddSign.equals(task.getCreateType())){
			if(!isBefAddSignBackPermit()){
				return null;
			}
		}
		if(Task.ActionType_SelfCircle.equalsIgnoreCase(task.getActionType())){
			Set<Task> subTasks = task.getSubTasks();
			//网关，下一步任务未产生时也允许收回
			if(subTasks!=null&&subTasks.size()>0){
				Task nextTask=subTasks.iterator().next();
				if(nextTask.getIsnotexe().booleanValue()||nextTask.getSignDate()!=null){
					throw new WfmServiceException(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "BackTaskCmd-020010")/*逐级审批下一步任务已经执行，无法收回！*/);
				}
			}
			
		}
		HumActIns humActIns = null;
		try {
			humActIns = ModelBuilder.builderSubHumactIns(task.getHumActIns());
		} catch (WfmServiceException e) {
			WfmLogger.error(e.getMessage(),e);
		}
		if (task != null && Task.CreateType_AfterAddSign.equals(task.getCreateType()))  {
			throw new WfmServiceException(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "BackTaskCmd-000010")/*后加签活动所在任务不允许收回！*/);
		}
		else if(!isPermit(humActIns)){
				return null;
		}
		{
			//流程中只有抄送没有审批 收回后待阅更改为阅毕
			HumAct humAct = humActIns.getHumAct();
			ProDef prodef = task.getProDef();
			
			IPort port = WfmCPUtilFacade.getNextPortAfterStrat(prodef)==null?null:(IPort) WfmCPUtilFacade.getNextPortAfterStrat(prodef);
			if (ProIns.End.equalsIgnoreCase(PwfmContext.getCurrentBpmnSession().getProIns().getState())&&!humAct.getId().equalsIgnoreCase(port.getId())) {
				if (task.getProDef().isNotBack()) {
					if(isLastedHumActTask(task)){
							this.backProIns();
								return null;
					}
				
				} else {
					throw new WfmServiceException(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "BackTaskCmd-000011")/*该流程已经结束，不能够收回*/);
				}
			}
		}
		if(Task.CreateType_BeforeAddSign.equals(task.getCreateType()))
			this.backAddSignTask();
		else 
			this.backNormalTask(humActIns);
		WfmCPTaskUtil.updateTaskCache(task.getPk_task(), task);
		
//		HumActIns humActIns = task.getHumActIns();
		HumAct humAct = humActIns.getHumAct();
		ProDef prodef = task.getProDef();
		
		IPort port = WfmCPUtilFacade.getNextPortAfterStrat(prodef)==null?null:(IPort) WfmCPUtilFacade.getNextPortAfterStrat(prodef);
		//判断是否在制单节点
		if (humAct.getId().equalsIgnoreCase(port.getId())) {
			WfmFlowInfoCtx flowInfoCtx = PwfmContext.getCurrentBpmnSession().getFlwInfoCtx();
			flowInfoCtx.setFirstHumanAct(true);
			PwfmContext.getCurrentBpmnSession().setFlwInfoCtx(flowInfoCtx);
			ProIns proins=task.getProIns();
			proins.setState(ProIns.NottStart);
			proins.asyn();
			WfmFormInfoCtx formInfo = PwfmContext.getCurrentBpmnSession().getFormVo();
			formInfo.setAttributeValue(formInfo.getFrmStateField(),ProIns.NottStart);
		}
		//收回的活动为后加签活动，则删除该活动和相应连线，重新连线两侧活动
		HumActIns afterHumActIns = isAfterAddSignHumact(task);
		if(afterHumActIns!=null){
			String proDefPk = prodef.getPk_prodef();
			WfmProdefVO vo = NCLocator.getInstance().lookup(IWfmProDefQry.class).getProDefVOByProDefPk(proDefPk);
			Document doc;
			try {
				doc = XMLUtil.getDocumentBuilder().parse(new InputSource(new StringReader(vo.getProcessstr())));
			} catch (Exception e) {
				WfmLogger.error(e.getMessage(), e);
				throw new WfmServiceException(e.getMessage());
			} 
			NodeList list = doc.getChildNodes();
			Node define = list.item(0);
			Node process = define.getChildNodes().item(1);
			Node processDiagram = define.getChildNodes().item(3);
			ProdefSerializer proser = new ProdefSerializer();
			proser.setDocument(doc);
			
			SequenceFlow outLine1 = (SequenceFlow)task.getHumActIns().getHumAct().getOutEdge()[0];
			HumAct afterAddSignHumAct = (HumAct)outLine1.getTarget();
			
			//后加签活动的进出线一定只有一条
			SequenceFlow outLine = (SequenceFlow)afterAddSignHumAct.getOutEdge()[0];
			SequenceFlow inLine = (SequenceFlow)afterAddSignHumAct.getInEdge()[0];
			HumAct beforeHumAct = (HumAct)inLine.getSource();
			IPort afterHumAct = outLine.getTarget();
			//删除后加签活动及其连线
			deleteHumactAndSequenceFlow(processDiagram,process,afterAddSignHumAct,outLine,inLine);
			//重新连线活动
			ProdefFacade.createNewSequenceFlow(processDiagram,process,beforeHumAct,afterHumAct);
			//更新流程定义
			saveProdef(vo,doc);
			//更新任务
			task = WfmCPTaskUtil.getTaskFromDBByTaskPk(task.getPk_task());
			ProDefsContainer.destory(task.getProDef().getPk_prodef());
			// 清空前台任务缓存，主要是为了清空流程定义
			WfmCPTaskUtil.updateTaskCache(task.getPk_task(), null);
			try{
				ProDef newprodef= ProDefsContainer.getProDef(vo);
				updateProdef(newprodef);
			}catch (Exception e) {
				WfmLogger.error(e.getMessage(), e);
			}
		}
		return null;
	}
	
	private boolean isLastedHumActTask(Task task){
		boolean flag = false;
		if(task!=null){
			Set<HumActIns> subHumins=task.getHumActIns().getSubHumActIns();
			if(subHumins != null && subHumins.size() == 1){
				HumActIns humActIns = subHumins.iterator().next();
				IPort port = humActIns.getPort();
				if (port != null && port instanceof EndEvent) {
					flag =  true;
				}
			}
			if(!flag){
				Set<Task>  subTasks=task.getSubTasks();
				if(subTasks!=null&&subTasks.size()>0){
					for (Task task2 : subTasks) {
						if(Task.ActionType_Deliver.equals(task2.getActionType())){
							return isLastedHumActTask(task2);
						}
					}
				}
			}
		}
		return flag;
	}
	
	/**
	 * 收回的任务是否已经经过网关
	 * @param task
	 * @return
	 */
	private boolean isPassGateWay(Task task) {
		boolean flag = false;
		HumAct humAct = task.getHumActIns().getHumAct();
		IEdge[] edgets = humAct.getOutEdge();
		for(IEdge edge : edgets){
			IPort port = edge.getTarget();
			if(port instanceof GateWay){
				flag = true;
			}
		}
		return flag;
	}
	
	/**
	 * 是否是后加签活动
	 * @param currTask
	 * @return
	 */
	public static HumActIns isAfterAddSignHumact(Task currTask) {
		Set<Task> subTasks = currTask.getSubTasks();
		if (subTasks == null || subTasks.size() == 0) {
			return null;
		} else {
			for (Iterator<Task> iter = subTasks.iterator(); iter.hasNext();) {
				Task subTask = iter.next();
				if(Task.CreateType_AfterAddSign.equals(subTask.getCreateType()))
					return subTask.getHumActIns();
			}
		}
		return null;
	}
	
	/**
	 * 删除后加签活动及其连线
	 * @param afterAddSignHumAct
	 * @param outLine
	 * @param inLine
	 * @throws WfmServiceException 
	 */
	private void deleteHumactAndSequenceFlow(Node processDiagram,Node process,HumAct afterAddSignHumAct,SequenceFlow outLine,SequenceFlow inLine) throws WfmServiceException {
		try {
			ProdefFacade.removeNode(process,afterAddSignHumAct);
			ProdefFacade.removeHumActC(processDiagram,afterAddSignHumAct);
			ProdefFacade.removeNode(process,outLine);
			ProdefFacade.removeSFC(processDiagram,outLine);
			ProdefFacade.removeNode(process,inLine);
			ProdefFacade.removeSFC(processDiagram,inLine);
		} catch (Exception e) {
			WfmLogger.error(e.getMessage(), e);
			throw new WfmServiceException(e.getMessage());
		}
	}
	
	/**
	 * 更新流程定义
	 * @param vo
	 * @param doc
	 * @throws WfmServiceException 
	 */
	private void saveProdef(WfmProdefVO vo,Document doc) throws WfmServiceException{
		try{
			ProdefSerializer proser = new ProdefSerializer();
			proser.setDocument(doc);
			vo.setProcessstr(PXUtil.docToString(doc));
			NCLocator.getInstance().lookup(IWfmProDefBill.class).updateWfmProdef(vo);
		}catch(Exception e){
			WfmLogger.error(e.getMessage(), e);
			throw new WfmServiceException(e.getMessage());
		}
	}
	
	/**
	 * 前加签收回权限判断
	 * @return
	 * @throws WfmServiceException 
	 */
	private boolean isBefAddSignBackPermit() throws WfmServiceException {		
//		HumActIns humActIns = null;
//		try {
//			humActIns = ModelBuilder.builder(task.getHumActIns());
//		} catch (WfmServiceException e) {
//			WfmLogger.error(e.getMessage(),e);
//		}
		Set<Task> subTasks = task.getSubTasks();		
		Task tmpTask = null;
		Task parent = PwfmContext.getCurrentBpmnSession().getParentTask();
		
		Set<Task> paralTasks = parent.getSubTasks();//humActIns.getTasks();
		//如果是前加签产生
		if(Task.CreateType_BeforeAddSign.equals(task.getCreateType())){
			if("and".equals(task.getSysext2())){
				for (Iterator<Task> iter = paralTasks.iterator(); iter.hasNext();) {
					tmpTask = iter.next();
					if(task.getPk_task().equals(tmpTask.getPk_task())&&(task.getAddSignTimes()!=null&&!task.getAddSignTimes().equals(tmpTask.getAddSignTimes())))
						continue;
					if (tmpTask.getSignDate() != null&&(task.getSignDate()!=null&&tmpTask.getSignDate().after(task.getSignDate()))) {
						//throw new LfwRuntimeException("该任务的串行下级任务已经被签收，不允许收回");
						throw new WfmServiceException(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "BackTaskCmd-000013")/*该任务产生的下游任务已经执行，不允许收回*/);
					}				
				}
			}
			 //如果是会签并行，或者抢占等(如果下一步活动已经产生任务，只有并行的最后一个任务才能收回)
				if(Task.State_BeforeAddSignCmplt.equals(parent.getState())||Task.State_End.equals(parent.getState())){
					throw new WfmServiceException(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "BackTaskCmd-000018")/*该加签任务的下游任务已经执行，不允许收回*/);
				}
			 
		}		
		
		if(subTasks!=null)
			for (Iterator<Task> iter = subTasks.iterator(); iter.hasNext();) {
				tmpTask = iter.next();
				if (tmpTask.getSignDate() != null) {
					//throw new LfwRuntimeException("该任务产生的子任务已经被签收，不允许收回");
					throw new WfmServiceException(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "BackTaskCmd-000013")/*该任务产生的下游任务已经执行，不允许收回*/);
				}			
			}
		return true;
	}
	
	private boolean isPermit(HumActIns humActIns) throws WfmServiceException {
		boolean result=true;
		if (Task.CreateType_Deliver.equalsIgnoreCase(task.getCreateType())) {
			throw new WfmServiceException(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "BackTaskCmd-000012")/*传阅任务不允许收回*/);
		}
		
//		HumActIns humActIns = null;
//		try {
//			humActIns = ModelBuilder.builderSubHumactIns(task.getHumActIns());
//		} catch (WfmServiceException e) {
//			WfmLogger.error(e.getMessage(),e);
//		}
		HumAct humAct = humActIns.getHumAct();
		CompleteStrategy sgy = humAct.getCompleteStrategy();
		Set<Task> subTasks = task.getSubTasks();		
		Task tmpTask = null;
		Task parent = PwfmContext.getCurrentBpmnSession().getParentTask();	
		//通过当前任务的父任务再次获取其所有子任务：这里得到的所有子任务是与当前任务并行的子任务
		if(parent!=null){	
			
			Set<Task> paralTasks = parent.getSubTasks();//得到与当前任务并行的子任务
			String isbunch = sgy.getIsNotBunch();
			int compType = sgy.getStrategyType();
			
			// 如果是会签串行
			if(ICompleteSgy.CompleteSgy_Countersign==compType&&"true".equals(isbunch)){			
				for (Iterator<Task> iter = paralTasks.iterator(); iter.hasNext();) {
					tmpTask = iter.next();
					if(task.getPk_task().equals(tmpTask.getPk_task()))
						continue;
					if (tmpTask.getEndDate()!=null||(tmpTask.getSignDate() != null&&tmpTask.getSignDate().after(task.getSignDate()))) {
						throw new WfmServiceException(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "BackTaskCmd-000013")/*该任务产生的下游任务已经执行，不允许收回*/);
					}
					
				}
			 } else { //如果是会签并行，或者抢占等(如果下一步活动已经产生任务，只有并行的最后一个任务才能收回)
				Set<HumActIns> subHumActIns = humActIns.getSubHumActIns();
				if (subHumActIns == null || subHumActIns.size() == 0) {
					result= true;
				} else if(subHumActIns.size() == 1){//如果下一个活动实例对应的是结束的话，也可以收回
					Iterator<HumActIns> it = subHumActIns.iterator();
					HumActIns humactTemp = it.next();
					if(humactTemp.getPort() != null && humactTemp.getPort() instanceof EndEvent)
						result= true;
				}
			 }
		 }
		 if(subTasks!=null){
			 
			 if(subTasks.size()==1){
					Task autoTask= subTasks.iterator().next();
					autoTask.getFlowType().getPageid();
					Set<Task>  autoSubTasks=autoTask.getSubTasks();
					if(!(autoSubTasks==null||autoSubTasks.size()==0)){
						if(autoTask!=null&&Task.FinishType_Auto.equals(autoTask.getFinishType())){
							throw new WfmServiceException(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "BackTaskCmd-z30013")/*连岗合办任务需逐级收回*/);
						}
					}
						
				 }
			for (Iterator<Task> iter = subTasks.iterator(); iter.hasNext();) {
				tmpTask = iter.next();
				String pageid =tmpTask.getFlowType().getPageid();
				//modify by ward 20180427 【COCO】允许制单人收回已经审批结束的单据
				if("0001A110000000005X8B".equals(pageid)){
					return true;
				}else{
					/*
					 * 因为后加签时,即使已经执行过,getSignDate属性值有的还是为空，
					 * 所以改为用getIsnotexe判断是否执行过，还有待验证其它情况对否
					 */
					if(!(Task.CreateType_Deliver.equals(tmpTask.getCreateType())||Task.ActionType_Deliver.equals(tmpTask.getActionType()))){
						if (tmpTask.getEndDate()!=null||(tmpTask.getSignDate() != null&&(task.getSignDate()!=null&&tmpTask.getSignDate().after(task.getSignDate())))) {
							throw new WfmServiceException(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "BackTaskCmd-000013")/*该任务产生的下游任务已经执行，不允许收回*/);
							
						}
						if ((tmpTask.getIsnotexe().booleanValue() && !Task.CreateType_BeforeAddSign.equals(tmpTask.getCreateType())
								|| Task.State_BeforeAddSignCmplt.equals(tmpTask.getState())|| Task.State_BeforeAddSignSend.equals(tmpTask.getState())|| Task.State_BeforeAddSignPlmnt.equals(tmpTask.getState()))) {
							throw new WfmServiceException(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "BackTaskCmd-000013")/*该任务产生的下游任务已经执行，不允许收回*/);
						}
					}
				}
			}
		 }
		 return result;
	}
	private void backProIns() throws WfmServiceException {
		HumActIns humActIns = task.getHumActIns();
		Set<HumActIns> humActInses = humActIns.getSubHumActIns();
		if(humActInses!=null&&humActInses.size()!=0){
		Iterator<HumActIns> iter = humActInses.iterator();
		HumActIns tmpHumActIns = null;
		IPort tmpPort = null;
			while (iter.hasNext()) {
				tmpHumActIns = iter.next();
				tmpPort = tmpHumActIns.getPort();
				if (tmpPort instanceof EndEvent) {
					humActInsExe.realDeleteHumActIns(tmpHumActIns);
					continue;
				}
			}
		}
			taskExe.backTask(task);
			task.setFinishType(null);
			task.asyn();
			humActIns.setIsNotPas(UFBoolean.FALSE);
			humActIns.setIsNotExe(UFBoolean.FALSE);
			humActIns.setState(HumActIns.Run);
			humActIns.asyn();
			
			ProIns proins = PwfmContext.getCurrentBpmnSession().getProIns();
			proins.setState(ProIns.Run);
			proins.setEndDate(null);
			proins.asyn();
			
			IPort port = WfmCPUtilFacade.getNextPortAfterStrat(task.getProDef())==null?null:(IPort) WfmCPUtilFacade.getNextPortAfterStrat(task.getProDef());
			//判断是否在制单节点
			HumAct humAct=humActIns.getHumAct();
		    WfmFormInfoCtx formVo = PwfmContext.getCurrentBpmnSession().getFormVo();
		   if (formVo != null) {
			   if (humAct.getId().equalsIgnoreCase(port.getId())) {
				   formVo.setAttributeValue(formVo.getFrmStateField(), ProIns.NottStart);
			   }else{
				   formVo.setAttributeValue(formVo.getFrmStateField(), ProIns.Run);
			   }
			   
		   
		   }
//		}
	}
	private void backNormalTask(HumActIns humActIns) throws WfmServiceException {
		this.handlerParalTasks(task);
		this.handlerSubHumActIns(humActIns);
		this.handlerSubTasks(humActIns);
		this.handlerNearRouterTaskAndHumActIns(humActIns);
		taskExe.backTask(task);
		task.setFinishType(null);
		task.asyn();
		humActIns.setIsNotPas(UFBoolean.FALSE);
		humActIns.setIsNotExe(UFBoolean.FALSE);
		humActIns.setState(HumActIns.Run);
		humActIns.asyn();
	}
	/**
	 * 处理所有同网关的下游任务
	 * @param humActIns
	 * @throws WfmServiceException 
	 */
	private void handlerNearRouterTaskAndHumActIns(HumActIns humActIns) throws WfmServiceException{
		if(humActIns==null || humActIns.getProIns()==null){
			return;
		}
		HumAct currHumAct = humActIns.getHumAct();
		IEdge[] outEdges = currHumAct.getOutEdge();
		boolean notNeedCheck = true;
		if(outEdges!=null && outEdges.length>0){
			for(IEdge outEdge : outEdges){
				if(outEdge.getTarget() instanceof GateWay){
					notNeedCheck = false;
					break;
				}
			}
		}
		if(notNeedCheck){
			return ;
		}
		Set<HumActIns> humActInses=humActIns.getProIns().getHumActInses();
		Set<HumActIns> notDoHumActInses = new HashSet<HumActIns>();
		if(humActInses!=null && humActInses.size()>0){
			for (HumActIns tempHumActIns : humActInses) {
				if(HumActIns.Run.equals(tempHumActIns.getState()) || HumActIns.Exe.equals(tempHumActIns.getState())){
					notDoHumActInses.add(tempHumActIns);
				}
			}
			if(notDoHumActInses.size()>0){
				List<HumActIns> portArea = new ArrayList<HumActIns>();
				Iterator<HumActIns> it = notDoHumActInses.iterator();
				while(it.hasNext()){
					Set<IPort> ports = new HashSet<IPort>();
					HumActIns currCheckHumActIns = it.next();
					PortAndEdgeHandler.getAllBeforeHumActs(ports, currCheckHumActIns.getHumAct().getInEdge());
					if(ports.contains(currHumAct)){
						portArea.add(currCheckHumActIns);
					}
				}
				if(portArea.size()>0){
					//当待办任务查找源头，看路径中是不是都是网关
					for(HumActIns item : portArea){
						if(this.isMatch(item.getPort(), currHumAct)){
							StringBuilder taskArea = new StringBuilder();
							Set<Task> subTaskSet = item.getTasks();
							Iterator<Task> subTaskIt = subTaskSet.iterator();
							while(subTaskIt.hasNext()){
								taskArea.append("'").append(subTaskIt.next().getPk_task()).append("',");
							}
							if(taskArea.length()>0){
								taskExe.realDeleteTaskFast(taskArea.toString().substring(0, taskArea.length()-1));
								taskExe.handleTaskMsg(subTaskSet);
							}
							if(!item.getIsNotExe().booleanValue()){
								humActInsExe.realDeleteHumActIns(item);
							}
						}
					}
				}
			}
		}
	}
	private boolean isMatch(IPort sourcePort, HumAct currHumAct){
		IEdge[] inEdges = PortAndEdgeHandler.getInEdges(sourcePort);
		if(inEdges!=null && inEdges.length>0){
			for(IEdge inEdge : inEdges){
				IPort targetPort = inEdge.getSource();
				if(targetPort.equals(sourcePort)){
					return true;
				}else if(targetPort instanceof GateWay){
					IEdge[] inGateWayEdges = ((GateWay) targetPort).getInEdges();
					if(inGateWayEdges!=null && inGateWayEdges.length>0){
						for(IEdge inGateWayEdge : inGateWayEdges){
							IPort gateWaySource = inGateWayEdge.getSource();
							if(gateWaySource instanceof Activity && currHumAct.getId().equals(gateWaySource.getId())){
								return true;
							}else if(gateWaySource instanceof GateWay && this.isMatch(gateWaySource, currHumAct)){
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	private void backAddSignTask() throws WfmServiceException {
		HumActIns humActIns = task.getHumActIns();
//		HumAct humAct = humActIns.getHumAct();
//		ProDef proDef = task.getProDef();
//		IPort port = WfmCPUtilFacade.getNextPortAfterStrat(proDef)==null?null:(IPort) WfmCPUtilFacade.getNextPortAfterStrat(proDef);
//		humActIns = ModelBuilder.builder(humActIns);
		this.handlerNextTasks(task);
		//this.handlerSubHumActIns(humActIns);
		taskExe.backTask(task);
		task.asyn();
		this.handlerParalTasks(task);
		humActIns.setIsNotPas(UFBoolean.FALSE);
		humActIns.setIsNotExe(UFBoolean.FALSE);
		humActIns.setState(HumActIns.Run);
		humActIns.asyn();
	}
	private void handlerSubHumActIns(HumActIns humActIns) throws WfmServiceException {
		Set<HumActIns> subHumActIns = humActIns.getSubHumActIns();
		if (subHumActIns == null || subHumActIns.size() == 0) {
			return;
		}
		HumActIns tmpHumActIns = null;
		//ProIns tmpProIns = null;
		//IWfmAssignActorsBill assignBill = NCLocator.getInstance().lookup(IWfmAssignActorsBill.class);
		for (Iterator<HumActIns> iter = subHumActIns.iterator(); iter.hasNext();) {
			tmpHumActIns = iter.next();
			//传阅节点后面实例未删除
//			tmpHumActIns=ModelBuilder.builderSubHumactIns(tmpHumActIns);
			if(tmpHumActIns.getSubHumActIns()!=null&&tmpHumActIns.getSubHumActIns().size()>0){
				handlerSubHumActIns(tmpHumActIns);
			}
			if(!tmpHumActIns.getIsNotExe().booleanValue()){
				taskExe.handleTaskMsg(tmpHumActIns.getTasks());
				humActInsExe.realDeleteHumActIns(tmpHumActIns);
			}
			//tmpProIns = tmpHumActIns.getProIns();
			//assignBill.deleteAssignActors(tmpProIns.getPk_proins(), tmpProIns.getProDef().getId(), tmpHumActIns.getHumAct().getId());
		}
	}
	/**
	 * 处理平行任务,把平行任务的优先级减1
	 * @param task
	 * @throws WfmServiceException
	 */
	private void handlerParalTasks(Task task) throws WfmServiceException {
		HumAct humAct = task.getHumActIns().getHumAct();
		if (ICompleteSgy.CompleteSgy_Countersign == humAct.getCompleteStrategy().getStrategyType() && Boolean.valueOf(humAct.getCompleteStrategy().getIsNotBunch())) {
			Task parenttask = PwfmContext.getCurrentBpmnSession().getParentTask();
			if(parenttask != null){
				Set<Task> paralTasks = parenttask.getSubTasks();
				
				Task tmpTask = null;	
				for (Iterator<Task> iter = paralTasks.iterator(); iter.hasNext();) {
					tmpTask = iter.next();
					if(task.getPk_task().equals(tmpTask.getPk_task()))
						continue;
					String priority = tmpTask.getPriority();
					tmpTask.setPriority(String.valueOf(Integer.parseInt(priority)+1));
					tmpTask.asyn();
				}
			}
		}
	}
	/**
	 * 一次性创建任务过多，性能问题
	 * @param humActIns
	 * @throws WfmServiceException
	 */
	private void handlerSubTasks(HumActIns humActIns) throws WfmServiceException {
		String sql = "select pk_task from "+WfmTaskVO.getDefaultTableName()+" where "+WfmTaskVO.PK_PARENT+"='"+task.getPk_task()+"' ";
		sql+=" and createtype!='"+Task.CreateType_BeforeAddSign+"'";
		taskExe.realDeleteTaskFast(sql);
	}
	private void handlerNextTasks(Task task) throws WfmServiceException {
		Task parenttask = PwfmContext.getCurrentBpmnSession().getParentTask();		
		Set<Task> paralTasks = parenttask.getSubTasks();
		
		Task tmpTask = null;	
		if(Task.CreateType_BeforeAddSign.equals(task.getCreateType())){
			if("and".equals(task.getSysext2())){
				for (Iterator<Task> iter = paralTasks.iterator(); iter.hasNext();) {
					tmpTask = iter.next();
					if(task.getPk_task().equals(tmpTask.getPk_task()))
						continue;
					String priority = tmpTask.getPriority();
					tmpTask.setPriority(String.valueOf(Integer.parseInt(priority)+1));
					tmpTask.asyn();
				}
			}
		}
	}
}
