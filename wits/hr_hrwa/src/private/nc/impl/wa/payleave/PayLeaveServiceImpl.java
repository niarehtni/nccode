package nc.impl.wa.payleave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.hr.wa.log.WaBusilogUtil;
import nc.impl.wa.classitem.ClassItemManageServiceImpl;
import nc.impl.wa.paydata.PaydataDAO;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.wa.IPayLeaveManageService;
import nc.itf.hr.wa.IPayLeaveQueryService;
import nc.itf.hr.wa.IWaClass;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.ui.wa.pub.WADelegator;
import nc.vo.hi.psndoc.Attribute;
import nc.vo.hr.combinesort.SortVO;
import nc.vo.hr.combinesort.SortconVO;
import nc.vo.hr.tools.pub.Pair;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.category.WaInludeclassVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.paydata.AggPayDataVO;
import nc.vo.wa.paydata.DataSVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.periodsate.WaPeriodstateVO;
import nc.vo.wa.pub.WACLASSTYPE;
import nc.vo.wa.pub.WaClassStateHelper;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.WaLoginVOHelper;
import nc.vo.wabm.util.WaCacheUtils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class PayLeaveServiceImpl implements IPayLeaveManageService,IPayLeaveQueryService{

	private PayLeaveDao dao = null;

	@Override
	public PeriodVO queryPeriodDate(String pk_periodscheme, String cyear,
			String cperiod) throws BusinessException {
		return getPayLeaveDao().queryPeriodDate(pk_periodscheme, cyear, cperiod);
	}

	@Override
	public PayfileVO[] queryLeavePsnInfo(String pk_waclass, String cyear,
			String cperiod, String startDate, String endDate)
					throws BusinessException {
		return getPayLeaveDao().queryLeavePsnInfo(pk_waclass, cyear, cperiod, startDate, endDate);
	}

	@Override
	public WaClassItemVO[] queryUserItemInfoVO(WaLoginContext context)
			throws BusinessException {
		return getPayLeaveDao().queryUserItemInfoVO(context);
	}

	@Override
	public void processNewLeave(WaLoginContext context,PayfileVO[] psns,Object[] vos)throws BusinessException{
		WaClassVO childVO = getNotCheckLeaveClass(context.getPk_prnt_class());
		if (childVO == null) {
			if (getPayLeaveDao().isExistNotPayLeaveClass(context.getPk_prnt_class())) {
				throw new BusinessException(ResHelper.getString("60130payleave","060130payleave0022")/*@res "����δ��ɵ���ְ��н���봦����ٽ��в�����"*/);
			}
			//��ѯ������
			WaClassVO parentClass = WADelegator.getWaClassQuery().queryWaClassByPK(context.getPk_prnt_class());
			if(WaLoginVOHelper.isNormalClass(context.getWaLoginVO())){
				//������н������Ҫת������ְ��н����
				getWaClassService().changeNormal2Leaveclass(parentClass);
			}
			childVO = createLeaveClass(parentClass);
			// ������ְ�ӷ���ʱPeriodState
			getPayLeaveDao().updatePeriodState(context, false);
			// ���·�����ְ��н��ʾ
			getPayLeaveDao().updateLeaveFlag(context, true);

			addUserClassItem(childVO, vos);
		}else{
			processUserClassItem(context,childVO, vos);
		}
		
		//20151212 shenliangc NCdp205558973 ����������ְ��н�����󣬼����Ӧ���ϼ���Ŀ��ʽ����ȷ       
		ClassItemManageServiceImpl service = new ClassItemManageServiceImpl();
		service.regenerateSystemFormula(childVO.getPk_org(), childVO.getPk_wa_class(), childVO.getCyear(), childVO.getCperiod());
		
		childVO.setPk_prnt_class(context.getPk_prnt_class());
		addLeavePsn(childVO,psns);
	}

	public WaClassVO getNotCheckLeaveClass(String pk_waclass) throws BusinessException{
		return getPayLeaveDao().getNotCheckLeaveClass(pk_waclass);
	}

	public WaClassVO createLeaveClass(WaClassVO parentClass) throws BusinessException{

		//������ְ�ӷ���
		WaClassVO childVO = createChildClass(parentClass);
		childVO.setPk_prnt_class(parentClass.getPk_wa_class());
		//�����ڼ�״̬
		createPeriodState(parentClass,childVO);
		//����Ĭ��н����Ŀ
		createDefClassItem(childVO);
		return childVO;
	}

	public void addLeavePsn(WaClassVO childVO,PayfileVO[] psns)throws BusinessException{
		if(psns==null){
			return;
		}
		for (int i = 0; i < psns.length; i++) {
			psns[i].setCheckflag(UFBoolean.FALSE);
			psns[i].setPk_wa_class(childVO.getPk_wa_class());
			psns[i].setCyear(childVO.getCyear());
			psns[i].setCperiod(childVO.getCperiod());
			psns[i].setCyearperiod(childVO.getCyear() + childVO.getCperiod());
			psns[i].setPk_wa_data(null);
		}
		getPayLeaveDao().deleteFromNormalClass(childVO,psns);
		getPayLeaveDao().getBaseDao().insertVOArray(psns);
	}

	public void processUserClassItem(WaLoginContext context,WaClassVO childVO,Object[] vos)throws BusinessException{
		if(vos==null||vos.length==0){
			return;
		}
		Map<String, String> map = getSelectedItem(context);
		WaClassItemVO vo = null;
		List<WaClassItemVO> addList = new ArrayList<WaClassItemVO>();
		for(int i = 0;i<vos.length;i++){
			vo = (WaClassItemVO)vos[i];
			if(map.containsKey(vo.getItemkey())){
				map.remove(vo.getItemkey());
			}else{
				addList.add(vo);
			}
		}
		addUserClassItem(childVO, addList.toArray());
		getPayLeaveDao().deleteClassItemByKey(childVO,map.keySet().toArray());
	}

	public void addUserClassItem(WaClassVO childVO,Object[] vos)throws BusinessException{
		if(vos==null||vos.length==0){
			return;
		}
		//�滻н�ʷ�����н������  ״̬ ��
		WaClassItemVO[] itemVOs = new WaClassItemVO[vos.length];
		for (int i = 0; i < vos.length; i++) {
			WaClassItemVO waClassItemVO = (WaClassItemVO)vos[i];
			waClassItemVO.setPk_wa_class(childVO.getPk_wa_class());
			waClassItemVO.setCyear(childVO.getCyear());
			waClassItemVO.setCperiod(childVO.getCperiod());
			waClassItemVO.setPk_wa_classitem(null);
			waClassItemVO.setStatus(VOStatus.NEW);
			itemVOs[i] = waClassItemVO;
		}
		getPayLeaveDao().getBaseDao().insertVOArray(itemVOs);
		WaCacheUtils.synCache(WaClassItemVO.TABLE_NAME);
	}

	private WaClassVO createChildClass(WaClassVO parentClass) throws BusinessException{
		//������ְ�ӷ���
		WaClassVO childVO = (WaClassVO)parentClass.clone();
		WaInludeclassVO vo = getPayLeaveDao().queryNewLeaveClasses(
				parentClass.getPk_wa_class(), parentClass.getCyear(),
				parentClass.getCperiod());
		int batch = 101;
		if(vo!=null){
			batch = vo.getBatch()+1;
		}
		//childVO.setName(parentClass.getMultilangName());
		childVO.setCode(parentClass.getCode() + parentClass.getCyear()+ parentClass.getCperiod()+"_times_"+batch);
		childVO.setMutipleflag(UFBoolean.FALSE);
		childVO.setShowflag(UFBoolean.FALSE);
		childVO.setWaPsnhiVOs(null);
		childVO.setWaPsnhiBVOs(null);
		childVO.setPk_wa_class(null);
		childVO.setLeaveflag(UFBoolean.TRUE);
		String pk_child = getPayLeaveDao().getBaseDao().insertVO(childVO);
		childVO.setPk_wa_class(pk_child);
		//�������ӹ�ϵ
		createClassRelation(parentClass,childVO,batch);
		return childVO;
	}

	private void createClassRelation(WaClassVO parentClass,WaClassVO childVO,int batch ) throws BusinessException{
		//�������ӹ�ϵ
		WaInludeclassVO  includeClassvo = new WaInludeclassVO();
		includeClassvo.setStatus(VOStatus.NEW);
		includeClassvo.setPk_parentclass(parentClass.getPk_wa_class());
		includeClassvo.setCyear(parentClass.getCyear());
		includeClassvo.setCperiod(parentClass.getCperiod());
		includeClassvo.setBatch(batch);
		includeClassvo.setPk_childclass(childVO.getPk_wa_class());
		includeClassvo.setPk_group(childVO.getPk_group());
		includeClassvo.setPk_org(childVO.getPk_org());
		getPayLeaveDao().getBaseDao().insertVO(includeClassvo);
	}

	private void createPeriodState(WaClassVO parentClass,WaClassVO childVO ) throws BusinessException{
		//Ϊ��ְ�ӷ��������ڼ�״̬
		PeriodVO periodVO = WADelegator.getPeriodQueryService().queryBySchemeYP(parentClass.getPk_periodscheme(),parentClass.getCyear(),parentClass.getCperiod());
		WaPeriodstateVO tempVO = new WaPeriodstateVO();
		tempVO.setEnableflag(UFBoolean.TRUE);
		tempVO.setIsapproved(UFBoolean.FALSE);
		tempVO.setCheckflag(UFBoolean.FALSE);
		tempVO.setCaculateflag(UFBoolean.FALSE);
		tempVO.setPayoffflag(UFBoolean.FALSE);
		tempVO.setAccountmark(UFBoolean.FALSE);
		tempVO.setClasstype(WACLASSTYPE.CHILDCLASS.getValue());
		tempVO.setPk_group(childVO.getPk_group());
		tempVO.setPk_org(childVO.getPk_org());
		tempVO.setPk_wa_class(childVO.getPk_wa_class());
		tempVO.setPk_wa_period(periodVO.getPk_wa_period());
		tempVO.setIsapporve(childVO.getIsapporve());
		getPayLeaveDao().getBaseDao().insertVO(tempVO);
	}

	private void createDefClassItem(WaClassVO vo)throws BusinessException {
		//��ѯ�����еķ�����Ŀ
		// {MOD:�¸�˰����}
		// begin
		WaClassItemVO[] newvos = WADelegator.getClassItemQuery()
				.queryItemInfoVO(vo.getPk_org(), vo.getPk_prnt_class(),
						vo.getCyear(), vo.getCperiod()," " +
								"wa_classitem.itemkey in(select itemkey from wa_item where  defaultflag = 'Y')");
		// vo.getCyear(), vo.getCperiod()," wa_classitem.itemkey in(select itemkey from wa_item where pk_org = 'GLOBLE00000000000000')");
		// end
		//�滻н�ʷ�����н������  ״̬ ��
		for (int i = 0; i < newvos.length; i++) {
			WaClassItemVO waClassItemVO = newvos[i];
			waClassItemVO.setPk_wa_class(vo.getPk_wa_class());
			waClassItemVO.setCyear(vo.getCyear());
			waClassItemVO.setCperiod(vo.getCperiod());
			waClassItemVO.setPk_wa_classitem(null);
			waClassItemVO.setStatus(VOStatus.NEW);

		}
		getPayLeaveDao().getBaseDao().insertVOArray(newvos);
		WaCacheUtils.synCache(WaClassItemVO.TABLE_NAME);
	}

	@Override
	public AggPayDataVO queryAggPayDataVOByCondition(WaLoginContext loginContext, String condition,
			String orderCondtion) throws BusinessException {
		AggPayDataVO aggPayDataVO = new AggPayDataVO();
		// н�������Ϣ
		WaLoginVO waLoginVO = WaClassStateHelper.getWaclassVOWithState(loginContext.getWaLoginVO());
		aggPayDataVO.setLoginVO(waLoginVO);
		// ��Ȩ�޵�н����Ŀ
		WaClassItemVO[] classItemVOs = WADelegator.getPaydataQuery().getUserClassItemVOs(loginContext);
		aggPayDataVO.setClassItemVOs(classItemVOs);
		WaBusilogUtil.writePaydataQueryBusiLog(loginContext);
		if(StringUtils.isBlank(orderCondtion)){
			// ���û�������ֶ� �ȵ����ݿ��в�ѯ��û�е�ǰ�û�����������  by wangqim
	        SortVO sortVOs[] = null;
	        SortconVO sortconVOs[] = null;
	        String strCondition = " func_code='" + loginContext.getNodeCode() + "'" + " and group_code= 'TableCode' and ((pk_corp='"
	                + PubEnv.getPk_group() + "' and pk_user='" + PubEnv.getPk_user() + "') or pk_corp ='@@@@') order by pk_corp";

	        sortVOs = (SortVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, SortVO.class, strCondition);
	        Vector<Attribute> vectSortField = new Vector<Attribute>();
	        if (sortVOs != null && sortVOs.length > 0)
	        {
	        	strCondition = "pk_hr_sort='" + sortVOs[0].getPrimaryKey() + "' order by field_seq ";
	            sortconVOs = (SortconVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
	                    .retrieveByClause(null, SortconVO.class, strCondition);
	            for (int i = 0; sortconVOs != null && i < sortconVOs.length; i++)
	            {
	                Pair<String> field = new Pair<String>(sortconVOs[i].getField_name(), sortconVOs[i].getField_code());
	                Attribute attribute = new Attribute(field, sortconVOs[i].getAscend_flag().booleanValue());
	                vectSortField.addElement(attribute);
	            }
	            orderCondtion = getOrderby(vectSortField);
	        }
	        orderCondtion = StringUtils.isBlank(orderCondtion)?" org_dept.code,hi_psnjob.clerkcode ":orderCondtion;;
		}
		// 2015-10-21 zhousze �޸�������������ְ��н�á��������ڡ�����ѯ����ʱ������֮ǰ���Զ���������ֻ��ȡ=��
		// �����ݿ��д�����ڴ���ʱ���룬�����ǲ�ѯ�������ݵġ������޸�ΪԪ�����������������ڡ�������Ĵ����Ǹ�
		// ��ȷ�������ĸ����(2015-10-22 zhousze ����ְ���ڡ���ѯ��������������Ҫ�����ڽ�����������sql�У���leavejob
		// ���Ѿ���ע���ˣ����Բ鲻���������ڸ��ݸ�sql��leavejob����hi_psnjobȥ��ѯ����Ա����ְ����) begin
		if(StringUtils.isEmpty(condition)){
			condition = "";
		}else if(condition.contains("cpaydate")) {
    		condition = condition.replaceAll("cpaydate", "wa_data.cpaydate");
		}else if(condition.contains("leavejob")){
			condition = condition.replaceAll("leavejob", "hi_psnjob");
		}
		// end
		String[] pks = getPayLeaveDao().queryPKSByCondition(loginContext, condition, orderCondtion);
		aggPayDataVO.setDataPKs(pks);
		DataSVO[] dsvos = new PaydataDAO().getDataSVOs(loginContext);
		aggPayDataVO.setDataSmallVO(dsvos);
		return aggPayDataVO;
	}
	
	//wangqim
	public static String getOrderby(Vector<Attribute> vectSortField) {
		if (vectSortField == null || vectSortField.size() == 0) {
			return "";
		}
		String strOrderBy = "";
		for (Attribute attr : vectSortField) {
			String strFullCode = attr.getAttribute().getValue();
			strOrderBy = strOrderBy + "," + strFullCode
					+ (attr.isAscend() ? "" : " desc");
		}
		return strOrderBy.length() > 0 ? strOrderBy.substring(1) : "";
	}
	
	@Override
	public DataVO[] queryDataVOByPks(String[] pk_wa_data) throws BusinessException {
		if(ArrayUtils.isEmpty(pk_wa_data)){
			return new DataVO[0];
		}

		InSQLCreator inSQLCreator = new InSQLCreator();
		try{
			String conditon = inSQLCreator.getInSQL(pk_wa_data);
			DataVO[] dataVOArrays = getPayLeaveDao().queryByPKSCondition(conditon, "");

			//����������������˳������
			List<DataVO> dataVOList = new ArrayList<DataVO>();
			Map<String,DataVO> dataVOMap = new HashMap<String,DataVO>();
			for(DataVO dataVO:dataVOArrays){
				dataVOMap.put(dataVO.getPk_wa_data(), dataVO);
			}

			for(String str_pk_wa_data:pk_wa_data){
				dataVOList.add(dataVOMap.get(str_pk_wa_data));
			}
			return  dataVOList.toArray(new DataVO[0]);
		} finally {
			inSQLCreator.clear();
		}
	}



	@Override
	public WaInludeclassVO queryNewLeaveClasses(String pk_waclass,String cyear,String cperiod)
			throws BusinessException {
		return getPayLeaveDao().queryNewLeaveClasses(pk_waclass,cyear,cperiod);
	}

	@Override
	public boolean hasLeaveClasses(WaLoginVO waLoginVO)
			throws BusinessException {
		return getPayLeaveDao().hasLeaveClasses(waLoginVO);
	}
	@Override
	public Map<String, String> getSelectedItem(WaLoginContext context)
			throws BusinessException {
		return getPayLeaveDao().getSelectedItem(context);
	}


	@Override
	public String queryUnCheckPsnInfo(WaLoginContext context,PayfileVO[] psns)
			throws BusinessException {

		InSQLCreator inSQLCreator = new InSQLCreator();

		try {
			String sql = "SELECT distinct hi_psnjob.clerkcode, " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name")
					+ " as psnname FROM bd_psndoc,hi_psnjob,wa_data WHERE bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc "
					+ "	AND wa_data.pk_psndoc = hi_psnjob.pk_psndoc AND wa_data.checkflag = 'N' ";
			SQLParameter param = new SQLParameter();
			if (WaLoginVOHelper.isMultiClass(context.getWaLoginVO())) {
				sql += " AND wa_data.pk_wa_class in ( SELECT pk_childclass FROM wa_inludeclass "
						+ "	WHERE pk_parentclass = ? AND cyear = ? AND cperiod = ? AND batch <100 ) ";
				param.addParam(context.getPk_prnt_class());
				param.addParam(context.getWaYear());
				param.addParam(context.getWaPeriod());
			}else if(WaLoginVOHelper.isSubClass(context.getWaLoginVO())){
				sql += " AND wa_data.pk_wa_class in ( SELECT pk_childclass FROM wa_inludeclass "
						+ "	WHERE pk_parentclass = ? AND cyear = ? AND cperiod = ? AND batch <100 ) ";
				param.addParam(context.getPk_prnt_class());
				param.addParam(context.getWaYear());
				param.addParam(context.getWaPeriod());
			}else {
				sql += " and wa_data.pk_wa_class = ? ";
				param.addParam(context.getPk_prnt_class());
			}
			sql += " AND hi_psnjob.pk_psnjob IN (" + inSQLCreator.getInSQL(psns, PayfileVO.PK_PSNJOB) + ") ";

			List result = (List) getPayLeaveDao().getBaseDao().executeQuery(sql, param, new MapListProcessor());
			if (result == null || result.size() == 0) {
				return null;
			}
			
			//�����������˲���ʾ����һ�仰
			if (context.getWaLoginVO().getPeriodVO().getCheckflag().booleanValue()) {
				return null;
			}
			
			Map map = null;
			StringBuffer msg = new StringBuffer();
			msg.append(ResHelper.getString("60130payleave","060130payleave0000"))/*@res "������ְ��Ա����δ��˵�������н��ɾ�����Ƿ������\r\n"*/;
			for(int i=0;i<result.size();i++){
				map = (Map)result.get(i);
				msg.append("["+map.get("clerkcode"));
				msg.append(":"+map.get("psnname")+"]");
			}
			return msg.toString();

		}
		finally
		{
			inSQLCreator.clear();
		}
	}

	private PayLeaveDao getPayLeaveDao(){
		if(dao == null){
			dao = new PayLeaveDao();
		}
		return dao;
	}
	private IWaClass getWaClassService(){
		return NCLocator.getInstance().lookup(IWaClass.class);
	}
	
	@Override
	public boolean isNormalClassCheck(String sql) throws BusinessException {
		return getPayLeaveDao().isNormalClassCheck(sql);
	}
}