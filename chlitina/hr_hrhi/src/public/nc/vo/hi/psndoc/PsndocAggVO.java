package nc.vo.hi.psndoc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.hr.utils.PubEnv;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.trade.pub.HYBillVO;
import nc.vo.trade.pub.IExAggVO;

import org.apache.commons.lang.ArrayUtils;

/***************************************************************************
 * <br>
 * Created on 2010-1-26 14:04:55<br>
 * 
 * @author Rocex Wang
 ***************************************************************************/
@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.hi.psndoc.PsndocVO")
public class PsndocAggVO extends HYBillVO implements IExAggVO {

    /** ������Դ */
    public static final String DATA_FROM = "data_from";

    /** ������Դ-�ⲿ���� */
    public static final String DATA_FROM_IMPORT = "data_from_import";

    /**
     * �ж��Ƿ�ҵ���Ӽ� ��ְ��¼����ְ��¼�����������������������˴�����ҵ���Ӽ�
     * ��ͬ��¼���������ʣ�������Ϣ���ڲ���ְ�ʸ��Ǹ���ģ������û�����ж��Ƿ�Ϊҵ���Ӽ���
     * 
     * @param infoset
     * @return
     */
    public static boolean isBusinessSet(String infoset, String pk_group) {
	Map<String, String> mapInfoset = new HashMap<String, String>();
	mapInfoset.put("hi_psndoc_ctrt", "6011"); // ��ͬ��¼
	mapInfoset.put("hi_psndoc_capa", "6004"); // ��������
	mapInfoset.put("hi_psndoc_ass", "6029"); // ������Ϣ
	mapInfoset.put("hi_psndoc_qulify", "6019");// �ڲ���ְ�ʸ�
	mapInfoset.put("hi_psndoc_train", "6025");// ��ѵ����
	if (PsndocAggVO.hashBusinessInfoSet.contains(infoset)) {

	    // �Ǻ�ͬ��¼���������ʡ�������Ϣ���ڲ���ְ�ʸ��е�һ�������Ҷ�Ӧ��ģ��û����������ʾ����Ӽ��Ƿ�ҵ���Ӽ�
	    if (mapInfoset.keySet().contains(infoset) && !PubEnv.isModuleStarted(pk_group, mapInfoset.get(infoset))) {

		return false;
	    }

	    return true;
	}

	return false;
    }

    /** ҵ���Ӽ� */
    public static final HashSet<String> hashBusinessInfoSet = new HashSet<String>() {
	{
	    add(PsnOrgVO.getDefaultTableName()); // ��֯��ϵ
	    add(PsnJobVO.getDefaultTableName()); // ��ְ��¼
	    add(PartTimeVO.getDefaultTableName()); // ��ְ��¼
	    add(TrialVO.getDefaultTableName());// �������
	    add(PsnChgVO.getDefaultTableName());// �������
	    add(CtrtVO.getDefaultTableName()); // �Ͷ���ͬ
	    add(RetireVO.getDefaultTableName());// ���˴���
	    add(TrainVO.getDefaultTableName());// ��ѵ
	    add(AssVO.getDefaultTableName());// ����
	    add(CapaVO.getDefaultTableName());// ��������
	    add(QulifyVO.getDefaultTableName());// ��ְ�ʸ�
	}
    };

    /** ����ְ���ҵ���Ӽ� */
    public static final HashSet<String> hashPsnJobInfoSet = new HashSet<String>() {
	{
	    // add(PsnJobVO.getDefaultTableName()); // ��ְ��¼
	    add(CtrtVO.getDefaultTableName()); // �Ͷ���ͬ
	    // add(TrainVO.getDefaultTableName());// ��ѵ
	    add(AssVO.getDefaultTableName());// ����
	    // add(CapaVO.getDefaultTableName());// ��������
	    // add(QulifyVO.getDefaultTableName());// ��ְ�ʸ�
	    // add(BminfoVO.getDefaultTableName());// ������Ϣ
	    add(WainfoVO.getDefaultTableName());// н����Ϣ
	}
    };

    /** ҵ���Ӽ� class */
    public static final ArrayList<Class<? extends SuperVO>> listBusinessInfoSetClass = new ArrayList<Class<? extends SuperVO>>() {
	{
	    add(PsnJobVO.class); // ��ְ��¼
	    // add( PartTimeVO.getDefaultTableName() ); // ��ְ��¼
	    add(TrialVO.class);// �������
	    add(PsnChgVO.class);// �������
	    add(CtrtVO.class); // �Ͷ���ͬ
	    add(RetireVO.class);// ���˴���
	    add(PsnOrgVO.class); // ��֯��ϵ
	    add(TrainVO.class);// ��ѵ
	    add(AssVO.class);// ����
	    add(CapaVO.class);// ��������
	    add(QulifyVO.class);// ��ְ�ʸ�
	}
    };

    /** */
    private static final long serialVersionUID = -1954147816664582721L;

    private HashMap<String, SuperVO[]> hashTableVO = new HashMap<String, SuperVO[]>();

    private PsndocVO psndocVO;

    /***************************************************************************
     * <br>
     * Created on 2010-6-13 11:01:31<br>
     * 
     * @param strTabCode
     * @author Rocex Wang
     ***************************************************************************/
    public void clearTableVO(String strTabCode) {
	if (strTabCode == null) {
	    hashTableVO.clear();
	} else {
	    hashTableVO.put(strTabCode, null);
	}
    }

    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-7-23 14:29:08<br>
     * 
     * @see java.lang.Object#clone()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public Object clone() {
	PsndocAggVO hrAggVO = new PsndocAggVO();
	if (hashTableVO != null) {
	    hrAggVO.hashTableVO = (HashMap) hashTableVO.clone();
	}
	if (psndocVO != null) {
	    hrAggVO.psndocVO = (PsndocVO) psndocVO.clone();
	}
	return hrAggVO;
    }

    private <T extends SuperVO> SuperVO[] dealWithSubVO(Class T, T[] chgVOs, T[] tableVO) {
	List<T> listDeleted = new ArrayList<T>();
	for (int j = 0; tableVO != null && j < tableVO.length; j++) {
	    listDeleted.add(tableVO[j]);
	}
	for (int j = 0; j < chgVOs.length; j++) {
	    if (chgVOs[j].getStatus() == VOStatus.DELETED) {
		listDeleted.add(chgVOs[j]);
	    }
	}

	return listDeleted.toArray((T[]) Array.newInstance(T, 0));
    }

    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-1-26 15:17:02<br>
     * 
     * @see nc.vo.trade.pub.IExAggVO#getAllChildrenVO()
     * @author Rocex Wang
     ****************************************************************************/
    public SuperVO[] getAllChildrenVO() {
	List<SuperVO> listVO = new ArrayList<SuperVO>();
	for (Iterator iterator = hashTableVO.values().iterator(); iterator.hasNext();) {
	    SuperVO superVOs[] = (SuperVO[]) iterator.next();
	    listVO.addAll(Arrays.asList(superVOs));
	}
	return listVO.toArray(new SuperVO[listVO.size()]);
    }

    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-1-26 15:10:16<br>
     * 
     * @see nc.vo.pub.AggregatedValueObject#getChildrenVO()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public SuperVO[] getChildrenVO() {
	return null;
    }

    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-1-26 15:17:02<br>
     * 
     * @see nc.vo.trade.pub.IExAggVO#getChildVOsByParentId(java.lang.String,
     *      java.lang.String)
     * @author Rocex Wang
     ****************************************************************************/
    public SuperVO[] getChildVOsByParentId(String tableCode, String parentid) {
	List<SuperVO> retSuperVOs = new ArrayList<SuperVO>();
	if (hashTableVO.containsKey(tableCode)) {
	    for (SuperVO vo : hashTableVO.get(tableCode)) {
		if (vo.getAttributeValue("pk_psndoc") != null && vo.getAttributeValue("pk_psndoc").equals(parentid)) {
		    retSuperVOs.add(vo);
		}
	    }
	}
	return retSuperVOs.toArray(new SuperVO[0]);
    }

    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-1-26 15:17:02<br>
     * 
     * @see nc.vo.trade.pub.IExAggVO#getDefaultTableCode()
     * @author Rocex Wang
     ****************************************************************************/
    public String getDefaultTableCode() {
	return null;
    }

    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-1-26 15:17:02<br>
     * 
     * @see nc.vo.trade.pub.IExAggVO#getHmEditingVOs()
     * @author Rocex Wang
     ****************************************************************************/
    public HashMap getHmEditingVOs() throws Exception {
	return null;
    }

    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-1-26 15:17:02<br>
     * 
     * @see nc.vo.trade.pub.IExAggVO#getParentId(nc.vo.pub.SuperVO)
     * @author Rocex Wang
     ****************************************************************************/
    public String getParentId(SuperVO item) {
	return getParentVO().getPrimaryKey();
    }

    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-1-26 14:05:34<br>
     * 
     * @see nc.vo.pub.ExtendedAggregatedValueObject#getParentVO()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public PsndocVO getParentVO() {
	return psndocVO;
    }

    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-1-26 14:05:34<br>
     * 
     * @see nc.vo.pub.ExtendedAggregatedValueObject#getTableCodes()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public String[] getTableCodes() {
	Set<String> keySet = hashTableVO.keySet();
	return keySet.toArray(new String[keySet.size()]);
    }

    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-1-26 14:05:34<br>
     * 
     * @see nc.vo.pub.ExtendedAggregatedValueObject#getTableNames()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public String[] getTableNames() {
	return getTableCodes();
    }

    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-1-26 14:05:34<br>
     * 
     * @see nc.vo.pub.ExtendedAggregatedValueObject#getTableVO(java.lang.String)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public SuperVO[] getTableVO(String strTableCode) {
	return hashTableVO.get(strTableCode);
    }

    /***************************************************************************
     * �ϲ��Ѿ�ɾ�����Ӽ��е�����<br>
     * Created on 2010-7-22 13:38:30<br>
     * 
     * @author Rocex Wang
     * @param changedAggVO
     *            the deletedAggVO to set
     ***************************************************************************/
    public void mergeDeletedAggVO(PsndocAggVO changedAggVO) {
	SuperVO[] allChildrenVO = null;
	if (changedAggVO == null || (allChildrenVO = changedAggVO.getAllChildrenVO()) == null
		|| allChildrenVO.length == 0) {
	    return;
	}
	String strTabCodes[] = getTableCodes();
	String strChangedTabCodes[] = changedAggVO.getTableCodes();
	for (int i = 0; i < strTabCodes.length; i++) {
	    if (!ArrayUtils.contains(strChangedTabCodes, strTabCodes[i])) {
		setTableVO(strTabCodes[i], null);
	    }
	}
	for (int i = 0; i < strChangedTabCodes.length; i++) {
	    SuperVO[] chgVOs = changedAggVO.getTableVO(strChangedTabCodes[i]);
	    if (chgVOs == null || chgVOs.length == 0) {
		continue;
	    }
	    Class curClass = chgVOs[0].getClass();
	    // ͨ�����ʹ����ӱ�VO
	    SuperVO[] subVOs = dealWithSubVO(curClass, chgVOs, getTableVO(strChangedTabCodes[i]));
	    if (subVOs != null && subVOs.length > 0) {
		setTableVO(strChangedTabCodes[i], subVOs);
	    }
	}
    }

    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-1-26 15:10:16<br>
     * 
     * @see nc.vo.pub.AggregatedValueObject#setChildrenVO(nc.vo.pub.CircularlyAccessibleValueObject[])
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void setChildrenVO(CircularlyAccessibleValueObject[] children) {
    }

    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-1-26 15:17:02<br>
     * 
     * @see nc.vo.trade.pub.IExAggVO#setParentId(nc.vo.pub.SuperVO,
     *      java.lang.String)
     * @author Rocex Wang
     ****************************************************************************/
    public void setParentId(SuperVO item, String id) {
    }

    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-1-26 14:05:34<br>
     * 
     * @see nc.vo.pub.ExtendedAggregatedValueObject#setParentVO(nc.vo.pub.CircularlyAccessibleValueObject)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void setParentVO(CircularlyAccessibleValueObject parent) {
	psndocVO = (PsndocVO) parent;
    }

    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-1-26 14:05:34<br>
     * 
     * @see nc.vo.pub.ExtendedAggregatedValueObject#setTableVO(java.lang.String,
     *      nc.vo.pub.CircularlyAccessibleValueObject[])
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void setTableVO(String strTableCode, CircularlyAccessibleValueObject[] values) {
	if (values == null || values.length == 0) {
	    hashTableVO.remove(strTableCode);
	    return;
	}
	hashTableVO.put(strTableCode, (SuperVO[]) values);
    }
}
