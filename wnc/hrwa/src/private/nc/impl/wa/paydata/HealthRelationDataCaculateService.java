package nc.impl.wa.paydata;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.hr.caculate.CaculateTypeVO;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.pub.WaLoginContext;


public class HealthRelationDataCaculateService extends DataCaculateService {

	public HealthRelationDataCaculateService(WaLoginContext loginContext,
			CaculateTypeVO caculateTypeVO, String selectWhere)
			throws BusinessException {
		super(loginContext, caculateTypeVO, selectWhere);
	}
	private BaseDAO subDao = null;
	
	
	/**
	 * ֻ��ȡ�Ͷ�������н�����йصķ�����Ŀ
	 */
	@Override
	protected void initClassItems() throws BusinessException {
		super.initClassItems();
		String pk_org = getLoginContext().getWaLoginVO().getPk_org();
		String pk_wa_class = getLoginContext().getWaLoginVO().getPk_wa_class();
		String cyear = getLoginContext().getWaLoginVO().getCyear();
		String cperiod = getLoginContext().getWaLoginVO().getCperiod();
		//��Ҫ���¼����н��itemKey(��������������itemkey)
		Set<String> waClassKey4ReCaculate = new HashSet<>();
		//��Ҫ���¼����н����Ŀpk(��������PK����)
		List<WaClassItemVO> waClassItem4ReCacalateList = new ArrayList<>();
		//��������������д��н����
		// ��ѯн�Y�Ŀ���a�䱣�M�Ŀ(TWEX0000�涨��)
		String strSQL = "select itemkey from wa_classitem where pk_org='"
				+ pk_org
				+ "' and pk_wa_class='"
				+ pk_wa_class
				+ "' and cyear= '"
				+ cyear
				+ "' and cperiod= '"
				+ cperiod
				+ "' and pk_wa_item = (select refvalue from twhr_basedoc where pk_org='"
				+ pk_org + "' and code = 'TWEX0000' and dr=0) ";

		String itemkey = null ;
		try {
			itemkey = (String) getSubDao().executeQuery(strSQL,
					new ColumnProcessor());
		} catch (DAOException e) {
			throw new BusinessException("Ӌ��ʧ��!δ�ҵ��a�䱣�M�ĿTWEX0000.");
		}
		if(null == itemkey){
			throw new BusinessException("Ӌ��ʧ��!δ�ҵ��a�䱣�M�ĿTWEX0000.");
		}
		if (!StringUtils.isEmpty(itemkey)) {
			
			//�����н�ʷ���,��֯,�ڼ��ڵ�����н����Ŀ������,����˳��,��ʽ,��н����
			//classItemVOs;
			
			//˳����Ҷ���������Ҫ�����¼����н����Ŀ
			waClassKey4ReCaculate.add(itemkey);
			int size = 1;
			do{
				size = waClassKey4ReCaculate.size();
				for (WaClassItemVO vo : classItemVOs) {
					// (����Ѿ������˸���,��ѭ����)
					if (null == vo || vo.getItemkey() == null || waClassKey4ReCaculate.contains(vo.getItemkey())) {
						continue;
					}
					// �����ʽ�а�������Щн����,��������¼����б�,�����µ�н����,
					if (isFormulaContain(vo, waClassKey4ReCaculate)) {
						waClassItem4ReCacalateList.add(vo);
						waClassKey4ReCaculate.add(vo.getItemkey());
						// ���ݼ��и���,break��,��������,��Ϊ��Ϊֻ�и�н����Ŀ��Ҫ����������,������ʱ�临�Ӷ�Ϊn2,������н����Ŀ����Ҫ�Ż��˶��㷨
						// 2019��9��2��11:09:21 tank
						break;
					}
				}
				
			}while(waClassKey4ReCaculate.size() != size);
			//н����Ŀû�����ӵ�ʱ��,����Խ���������
			
		}
		//waClassItem4ReCacalateListһ��Ҫ��˳��������¼���
		classItemVOs = new ArrayList<>(waClassItem4ReCacalateList).toArray(new WaClassItemVO[0]);
	}

	/**
	 * �ж�VO��Ĺ�˾�ǲ��ǰ������漰����������н����
	 * @param vo
	 * @param waClassKey4ReCaculate
	 * @return
	 */
	private boolean isFormulaContain(WaClassItemVO vo,
			Set<String> waClassKey4ReCaculate) {
		if(null == vo || null==vo.getPk_wa_classitem() || null == vo.getVformula() || null == vo.getItemkey()){
			return false;
		}
		//��Ҫ�б�Ĺ�ʽ
		String formula = vo.getVformula();
		for(String healthRelationItemKey : waClassKey4ReCaculate){
			if(null == healthRelationItemKey){
				continue;
			}
			//����itemkey���ֶ�
			int index = formula.indexOf(healthRelationItemKey);
			if(index >= 0){
				//���ӵ�һλ������0-9������,����Ҫ����f_1,�����ܳ�f_11
				//���������key�Ѿ�������λ��,��ô�Ƿ���Ҫ���
				boolean isLast = (index + healthRelationItemKey.length()) >= formula.length();
				if(isLast){
					return true;
				}else{
					char nextChar = formula.charAt(index + healthRelationItemKey.length());
					//���������,�򲻷���
					if(Character.isDigit(nextChar)){
						return false;
					}else{
						//��������,����
						return true;
					}
				}
			}	
		}
		return false;
	}

	@Override
	public void doCaculate() throws BusinessException {
		super.doCaculate();
	}

	public BaseDAO getSubDao() {
		if(null == subDao){
			subDao = new BaseDAO();
		}
		return subDao ;
	}

	public void setSubDao(BaseDAO subDao) {
		this.subDao = subDao;
	}
	
	

}
