package nc.ui.twhr.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nc.bs.uif2.BusinessExceptionAdapter;
import nc.bs.uif2.validation.IBatchValidationService;
import nc.bs.uif2.validation.ValidationException;
import nc.ui.ml.NCLangRes;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.pubapp.uif2app.model.BatchBillTableModel;
import nc.ui.uif2.model.BatchModelUtil;
import nc.ui.uif2.model.IBatchAppModelService;
import nc.ui.uif2.model.RowOperationInfo;
import nc.ui.uif2.model.RowSelectionOperationInfo;
import nc.uif2.annoations.SupportEvents;
import nc.uif2.annoations.client.SupportUIStates;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.bd.meta.IBDObject;
import nc.vo.bd.meta.IBDObjectAdapterFactory;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.twhr.basedoc.BaseDocVO;

/**
 * ����������Ӧ��ģ��
 * 
 * �������ӡ��޸ġ�ɾ��֮���������ͳһ���棻
 * 
 * 
 * @author lkp
 * 
 */
@SupportEvents({ AppEventConst.SELECTION_CHANGED, AppEventConst.MODEL_INITIALIZED, AppEventConst.UISTATE_CHANGED,
		AppEventConst.SELECTED_DATE_CHANGED, AppEventConst.DATA_DELETED, AppEventConst.DATA_INSERTED,
		AppEventConst.MULTI_SELECTION_CHANGED })
@SupportUIStates({ UIState.NOT_EDIT, UIState.EDIT })
public class GlbBatchBillTableModel extends BatchBillTableModel {

	// ģ�ͳ־û�����������ҪIOCע��
	private IBatchAppModelService service;

	// ����ת�������࣬��ҪIOCע�롣
	private IBDObjectAdapterFactory businessObjectAdapterFactory;

	// ����ǰ��ģ��У���߼����벻Ҫʹ��IOCע��,ͨ��BatchSaveAction����ע�롣
	private IBatchValidationService validationService = null;

	// �洢��ǰ����ͼͬ���������У��������������޸ģ�ʼ�ն����û���ǰ�༭�����½����
	private List<Object> rows = new ArrayList<Object>();

	// �ָ��õ����ݱ���,�༭֮ǰ�ᱣ���ڴ˴�
	// private List<Object> backupRows = new ArrayList<Object>();
	private Map<String, Object> backupMap = new LinkedHashMap<String, Object>();

	// �������ݵĴ洢���洢���ñ���map�洢hashcodeδ�����������
	private List<Object> newRows = new ArrayList<Object>();

	// ���汻ɾ�����ݵı�ʶ
	private List<String> deletedRows = new ArrayList<String>();
	private List<Integer> delIndexs = new ArrayList<Integer>();

	// �洢�����²����ı�ʶ
	private List<String> updRows = new ArrayList<String>();

	// ��ǰѡ���к�
	private int selectedIndex = -1;

	// ��ǰѡ������
	private Object selectedObject = null;

	// ��ȸ��ƶ��ɵĿ�������
	private Object copyObject = null;

	/** ��ǰ��ѡ��ѡ�е��У���������ɾ���������Ȳ��� */
	private List<Integer> selectedOperaRows = new ArrayList<Integer>();

	// �Ƿ����Զ�����״̬������Զ�����״̬����Ҫ����ѡ���С��ڲ����ԣ�����Ҫע�롣
	private boolean inAutoAddLineMode = false;

	// �Ƿ�������ж�ѡ
	private boolean inMultiSelectmode = false;

	@Override
	public void initModel(Object data) {

		this.dataReset();
		rows.clear();
		setUiState(UIState.NOT_EDIT);
		if (data == null) {
		} else if (data.getClass().isArray()) {
			rows.addAll(Arrays.asList((Object[]) data));
		} else {
			rows.add(data);
		}
		fireEvent(new AppEvent(AppEventConst.MODEL_INITIALIZED, this, null));
		// this.setSelectedIndex(rows.size()>0?0:-1);
		// ͬʱ���õ�ѡ�Ͷ�ѡ��־
		innerSetSelectedIndex(rows.size() > 0 ? 0 : -1);
	}

	/**
	 * ׷���ض���������
	 * 
	 * @param rowCount
	 * @throws Exception
	 */
	public void addLines(Object[] objs) {
		addLines(objs, true);
	}

	public void addLines(Object[] objs, boolean isFireDataInsertEvent) {
		// ���Ϊ�Ǳ༭״̬�����л�Ϊ�༭״̬
		turnToEditStateIfNeed();
		innerInsertLine(rows.size(), objs, isFireDataInsertEvent);
	}

	/**
	 * ���ض����кŲ����ض��е�������
	 * 
	 * @param startRowIndex
	 *            ���Ϊ-1����ڵ�ǰ�������򰴵�ǰѡ���е�λ�ã������ѡ���У��򲻽��в�����
	 * @throws Exception
	 */
	public void insertLines(int startRowIndex, Object[] objs) {
		int index = getInsertIndex(startRowIndex);
		turnToEditStateIfNeed();
		innerInsertLine(index, objs, true);
	}

	/**
	 * ����ģ��ĳ������
	 * 
	 * @param i
	 * @param obj
	 */
	public void updateLine(int index, Object obj) {

		// �����ͷ�����rows�е�����δ�����ⲿ��������index���ɿ�
		int realIndex = findBusinessDataIndex(obj, rows);

		int operatorIndex = -1;
		if (realIndex == -1) {
			operatorIndex = index;
		} else {
			operatorIndex = realIndex;
		}

		Object targetObj = rows.get(operatorIndex);
		// ����������ӵ����ݽ����޸ģ���ֱ���޸ļ��ɡ�
		if (newRows.contains(targetObj)) {
			int newRowsIndex = newRows.indexOf(targetObj);
			newRows.set(newRowsIndex, obj);
		} else {
			String id = getObjectID(obj);
			if (id != null && !updRows.contains(id))
				updRows.add(id);
		}

		rows.set(operatorIndex, obj);
		if (selectedIndex != operatorIndex) {
			// this.setSelectedIndex(index);
			this.innerSetSelectedIndex(operatorIndex);
		} else {
			selectedObject = obj;
		}
		fireEvent(new AppEvent(AppEventConst.SELECTED_DATE_CHANGED, this, new RowOperationInfo(operatorIndex, obj)));

	}

	public void updateLines(int[] rowIndexs, Object[] objs) {

		for (int i = 0; i < rowIndexs.length; i++) {

			int index = rowIndexs[i];
			Object obj = objs[i];
			Object targetObj = rows.get(index);
			// ����������ӵ����ݽ����޸ģ���ֱ���޸ļ��ɡ�
			if (newRows.contains(targetObj)) {
				int newRowsIndex = newRows.indexOf(targetObj);
				newRows.set(newRowsIndex, obj);
			} else {
				String id = getObjectID(obj);
				if (id != null && !updRows.contains(id))
					updRows.add(id);
			}
			rows.set(index, obj);
		}
		if (isInMultiSelectmode())
			setSelectedOperaRows(rowIndexs);

		fireEvent(new AppEvent(AppEventConst.SELECTED_DATE_CHANGED, this, new RowOperationInfo(rowIndexs, objs)));
	}

	/**
	 * ģ��ɾ�в���
	 * 
	 */
	public void delLine(int delIndex) throws Exception {
		Integer[] indexs = getDelLineIndex(delIndex);

		// ����ǷǱ༭״̬����ֱ�ӽ������ݿ�ɾ������
		List<Object> list = new ArrayList<Object>();
		if (getUiState() == UIState.NOT_EDIT) {
			processDelLindeInNotEdit(indexs, list);
		} else {

			processDelLineInEdit(indexs, list);
		}

		rows.removeAll(list);

		// ɾ���к��轫ѡ�����е���ɾ�����Ƴ�������getSelectedOperaDatas()���׳�����Խ�����
		for (int i = 0; i < indexs.length; i++) {
			selectedOperaRows.remove(indexs[i]);
		}

		// ֪ͨ����ɾ������
		fireEvent(new AppEvent(AppEventConst.DATA_DELETED, this, new RowOperationInfo(Arrays.asList(indexs),
				list.toArray())));

		// ��������ѡ����
		processSelectedIndexWhenDel(indexs);
	}

	/**
	 * �����������Ϸ��Դ������Ϊ׼�������������������Ե�ǰѡ����Ϊ׼������ѡ���У����쳣
	 * 
	 * @param index
	 * @throws Exception
	 */
	public void copyLine(int copyIndex) {

		int index = getInsertIndex(copyIndex);
		copyObject = BatchModelUtil.cloneObject(getRow(index));
	}

	/**
	 * ��֮ǰ�Ĳ������г���
	 * 
	 */
	public void doCancel() {
		rows.clear();
		// if(backupRows.size() > 0)
		// {
		// rows.addAll(backupRows);
		// backupRows.clear();
		// }
		if (!backupMap.isEmpty()) {
			rows.addAll(backupMap.values());
			backupMap.clear();
		}

		int tempIndex = this.selectedIndex;
		dataReset();

		if (tempIndex < 0 || tempIndex > rows.size() - 1)
			tempIndex = 0;
		if (rows.size() == 0)
			tempIndex = -1;

		this.selectedIndex = tempIndex;
		if (this.selectedIndex != -1)
			this.selectedObject = rows.get(this.selectedIndex);

		// ���л����Ǳ༭״̬��������ѡ���С�
		setUiState(UIState.NOT_EDIT);
		innerSetSelectedIndex(tempIndex);
	}

	@Override
	public void save() throws Exception {

		beforeSaveProcess();

		BatchOperateVO vo = getCurrentSaveObject();
		vo = service.batchSave(vo);

		directSave(vo);
	}

	public void directSave(BatchOperateVO vo) throws Exception {

		if (vo.getAddObjs() != null && vo.getAddObjs().length > 0) {
			for (int j = 0; j < vo.getAddObjs().length; j++) {
				int addIndex = rows.indexOf(newRows.get(j));
				rows.set(addIndex, vo.getAddObjs()[j]);
				if (addIndex == this.selectedIndex)
					selectedObject = vo.getAddObjs()[j];
			}
			newRows.clear();
		}

		if (vo.getUpdObjs() != null && vo.getUpdObjs().length > 0) {
			for (int j = 0; j < vo.getUpdObjs().length; j++) {
				int updIndex = findBusinessDataIndex(vo.getUpdObjs()[j], rows);
				rows.set(updIndex, vo.getUpdObjs()[j]);
				if (updIndex == selectedIndex)
					selectedObject = vo.getUpdObjs()[j];
			}
			updRows.clear();
		}

		deletedRows.clear();
		delIndexs.clear();
		// backupRows.clear();
		backupMap.clear();
		setUiState(UIState.NOT_EDIT);

	}

	public void setSelectedOperaRows(int[] selectedRows) {

		if (selectedOperaRows == null)
			selectedOperaRows = new ArrayList<Integer>();
		selectedOperaRows.clear();

		if (selectedRows == null || selectedRows.length == 0 || (selectedRows.length == 1 && selectedRows[0] == -1))
			return;
		for (int row : selectedRows)
			selectedOperaRows.add(row);

		fireEvent(new AppEvent(AppEventConst.MULTI_SELECTION_CHANGED, this, getRowInfo(selectedRows,
				RowSelectionOperationInfo.SET_SELECTED_SATTE)));
	}

	public Integer[] getSelectedOperaRows() {
		return selectedOperaRows.toArray(new Integer[0]);
	}

	public Object[] getSelectedOperaDatas() {
		if (selectedOperaRows.size() == 0)
			return null;

		Object[] objs = new Object[selectedOperaRows.size()];
		int i = 0;
		for (int index : selectedOperaRows) {
			objs[i++] = rows.get(index);
		}
		return objs;
	}

	/**
	 * ��ȡ��ǰģ�����ݵ��к�
	 * 
	 * @param obj
	 * @param isNew
	 *            �Ƿ���������true:������false���޸�
	 * @return
	 */
	public int getIndexOfObject(Object obj, boolean isNew) {
		if (isNew)
			return rows.indexOf(obj);
		else
			return findBusinessDataIndex(obj, rows);
	}

	/**
	 * ״̬�л�
	 */
	public void setUiState(UIState uiState) {

		// ����༭̬֮ǰ���������ݱ���
		if (uiState == UIState.EDIT && getUiState() != UIState.EDIT) {
			// backupRows = BatchModelUtil.cloneObjects(rows, backupRows);
			List<Object> list = BatchModelUtil.cloneObjects(rows);
			if (list != null && !list.isEmpty()) {
				for (Object obj : list) {
					IBDObject target = businessObjectAdapterFactory.createBDObject(obj);
					String id = (String) target.getId();
					if (!StringUtil.isEmptyWithTrim(id))
						backupMap.put(id, obj);
				}
			}
		}

		super.setUiState(uiState);
	}

	/**
	 * ģ���ڲ�ʹ������ѡ���У���ͬʱ�����л����ѡ�еı�־λ
	 * 
	 * @param index
	 */
	protected void innerSetSelectedIndex(int index) {
		setSelectedIndex(index);
		if (isInMultiSelectmode())
			setSelectedOperaRows(new int[] { index });
	}

	public void setSelectedIndex(int index) {
		if (index < 0 || index >= rows.size()) {
			this.selectedIndex = -1;
			this.selectedObject = null;
			fireEvent(new AppEvent(AppEventConst.SELECTION_CHANGED, this, null));
			return;
		}

		selectedIndex = index;

		if (rows.size() > 0)
			selectedObject = rows.get(index);

		fireEvent(new AppEvent(AppEventConst.SELECTION_CHANGED, this, null));
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	/**
	 * ��ȡ��ǰ�ɱ�������ݶ���
	 * 
	 * @return
	 */
	public BatchOperateVO getCurrentSaveObject() {
		// ׼����������
		Object[] newAddObj = newRows.toArray();
		// ���pk_org��pk_group��Ϊ����Ϊȫ��
		for (int i = 0; i < newAddObj.length; i++) {
			if (null == ((BaseDocVO) newAddObj[i]).getPk_group() && null == ((BaseDocVO) newAddObj[i]).getPk_org()) {
				((BaseDocVO) newAddObj[i]).setPk_group("~");
				((BaseDocVO) newAddObj[i]).setPk_org("GLOBLE00000000000000");
			}
		}
		Object[] delObj = getDeleteRows();
		Object[] updObj = getUpdRows();
		for (int i = 0; i < updObj.length; i++) {
			if (null == ((BaseDocVO) updObj[i]).getPk_group() && null == ((BaseDocVO) updObj[i]).getPk_org()) {
				((BaseDocVO) updObj[i]).setPk_group("~");
				((BaseDocVO) updObj[i]).setPk_org("GLOBLE00000000000000");
			}
		}
		int[] addIndexs = new int[newRows.size()];
		int[] updIndexs = new int[updRows.size()];

		for (int i = 0; i < newRows.size(); i++)
			addIndexs[i] = rows.indexOf(newRows.get(i));
		for (int i = 0; i < updRows.size(); i++)
			updIndexs[i] = findBusinessDataIndex(updRows.get(i), rows);

		// ���ݿ����
		BatchOperateVO vo = new BatchOperateVO();
		vo.setAddObjs(newAddObj);
		vo.setDelObjs(delObj);
		vo.setUpdObjs(updObj);
		vo.setAddIndexs(addIndexs);
		vo.setUpdIndexs(updIndexs);
		vo.setDelIndexs(getDelIndexs(delIndexs));
		return vo;
	}

	public void beforeSaveProcess() throws Exception {
		if (getValidationService() != null) {
			int[] needDelRows = getValidationService().unNecessaryData(getRows());
			if (needDelRows != null && needDelRows.length > 0) {
				Arrays.sort(needDelRows);
				for (int i = needDelRows.length - 1; i > -1; i--)
					delLine(needDelRows[i]);
			}

			try {
				BatchOperateVO batchVO = getCurrentSaveObject();
				getValidationService().validate(batchVO);
			} catch (ValidationException e) {
				throw new BusinessExceptionAdapter(e);
			}
		}
	}

	/**
	 * ��ȡ���޸Ķ����Ӧ���޸�֮ǰ�Ķ���
	 * 
	 * @param newObject
	 * @return
	 */
	public Object getBeforeUpdateObject(Object newObject) {
		// int index = findBusinessDataIndex(newObject, backupRows);
		// if(index == -1 || index > backupRows.size()- 1)
		// return null;
		// return backupRows.get(index);
		IBDObject bdObj = getBusinessObjectAdapterFactory().createBDObject(newObject);
		return backupMap.get(bdObj.getId());
	}

	public IBDObjectAdapterFactory getBusinessObjectAdapterFactory() {
		return businessObjectAdapterFactory;
	}

	public void setBusinessObjectAdapterFactory(IBDObjectAdapterFactory businessObjectAdapterFactory) {
		this.businessObjectAdapterFactory = businessObjectAdapterFactory;
	}

	public IBatchAppModelService getService() {
		return service;
	}

	public void setService(IBatchAppModelService service) {
		this.service = service;
	}

	@Override
	public Object getSelectedData() {
		return selectedObject;
	}

	public void clearState() {
		deletedRows.clear();
		delIndexs.clear();
		updRows.clear();
		// backupRows.clear();
		backupMap.clear();
		newRows.clear();
	}

	public List<Object> getRows() {
		return this.rows;
	}

	public int getRowCount() {
		if (getRows() == null)
			return 0;
		else
			return getRows().size();
	}

	public Object getRow(int i) {
		return rows.get(i);
	}

	public Object getCopyedObject() {
		return this.copyObject;
	}

	public IBatchValidationService getValidationService() {
		return validationService;
	}

	public void setValidationService(IBatchValidationService validationService) {
		this.validationService = validationService;
	}

	public boolean isInAutoAddLineMode() {
		return inAutoAddLineMode;
	}

	public void setInAutoAddLineMode(boolean inAutoAddLineMode) {
		this.inAutoAddLineMode = inAutoAddLineMode;
	}

	protected String getObjectID(Object obj) {
		IBDObject bdObj = businessObjectAdapterFactory.createBDObject(obj);
		String id = (String) bdObj.getId();
		return id;
	}

	protected List<Object> getNewRows() {
		return Collections.unmodifiableList(newRows);
	}

	protected List<String> getUpdatedObjectIDs() {
		return Collections.unmodifiableList(updRows);
	}

	protected List<String> getDelRowIndex() {
		return Collections.unmodifiableList(deletedRows);
	}

	protected Integer[] getDelLineIndex(int delIndex) {

		if ((delIndex == -1 || delIndex > rows.size() - 1) && this.selectedIndex == -1)
			throw new IllegalArgumentException(NCLangRes.getInstance().getStrByID("uif2", "BatchBillTableModel-000000")/*
																														 * ���봫����ȷ��ɾ����
																														 * ��
																														 * ������ѡ���е������ɾ��
																														 * !
																														 */);

		Integer indexs[] = null;
		if (selectedOperaRows != null && !selectedOperaRows.isEmpty())
			indexs = getSelectedOperaRows();
		else
			indexs = new Integer[] { selectedIndex };

		if (delIndex > -1 && delIndex < rows.size() && rows.size() > 0)
			indexs = new Integer[] { delIndex };
		return indexs;
	}

	/**
	 * ɾ��֮���ѡ���еĴ���
	 * 
	 * @param indexs
	 */
	protected void processSelectedIndexWhenDel(Integer[] indexs) {

		// ֻҪɾ�����д��ڵ�ǰѡ���л��������һ���ȵ�ǰѡ�������С���У�����Ҫ����ѡ���е���
		boolean isExist = false;
		for (Integer index : indexs) {
			if (index <= selectedIndex)
				isExist = true;
		}
		if (!isExist)
			return;
		// TODO �Ȳ��ô��ַ�ʽ�������ȷ���㣬��Ҫѡ��ɾ��ǰѡ���м�¼����һ����¼������ɾ����ļ�¼��ţ�����Ҫ�������㣬�ȽϷ�����
		if (selectedIndex > rows.size() - 1)
			innerSetSelectedIndex(selectedIndex - 1);
		else
			innerSetSelectedIndex(selectedIndex);
	}

	protected void processDelLineInEdit(Integer[] indexs, List<Object> list) {

		for (Integer index : indexs) {
			Object delObj = getRow(index);
			if (newRows.contains(delObj))
				newRows.remove(delObj);
			else {
				String id = getObjectID(delObj);

				if (updRows.contains(id)) {// ��ǰ��ɾ������֮ǰ���޸Ĺ�����ɾ�����޸ļ�¼��
					updRows.remove(id);
				}
				deletedRows.add(id);
				delIndexs.add(index);
			}
			list.add(delObj);
		}
	}

	private int[] getDelIndexs(List<Integer> delIndexList) {
		int[] delIndexs = new int[delIndexList.size()];
		for (int i = 0; i < delIndexs.length; i++) {
			delIndexs[i] = delIndexList.get(i);
		}
		return delIndexs;
	}

	protected void processDelLindeInNotEdit(Integer[] indexs, List<Object> list) throws Exception {

		List<Object> tempList = new ArrayList<Object>();
		List<Integer> tempIndexList = new ArrayList<Integer>();
		for (Integer index : indexs) {
			Object delObj = getRow(index);
			if (newRows.contains(delObj))
				newRows.remove(delObj);
			else {
				tempList.add(delObj);
				tempIndexList.add(index);
			}
			list.add(delObj);
		}
		if (!tempList.isEmpty()) {
			BatchOperateVO vo = new BatchOperateVO();
			vo.setDelObjs(tempList.toArray());
			vo.setDelIndexs(getDelIndexs(tempIndexList));
			service.batchSave(vo);
		}
	}

	private void dataReset() {
		selectedIndex = -1;
		selectedObject = null;
		deletedRows.clear();
		delIndexs.clear();
		selectedOperaRows.clear();
		updRows.clear();
		// backupRows.clear();
		backupMap.clear();
		newRows.clear();
		copyObject = null;
	}

	private int findBusinessDataIndex(Object obj, List<Object> rowsx) {

		int result = -1;
		IBDObject target = businessObjectAdapterFactory.createBDObject(obj);
		if (target != null) {
			result = this.findBusinessDataIndex((String) target.getId(), rowsx);
		}
		return result;
	}

	private int findBusinessDataIndex(String id, List<Object> rowsx) {
		int i = 0;
		for (Object object : rowsx) {
			IBDObject tmp = businessObjectAdapterFactory.createBDObject(object);
			if (tmp != null && tmp.getId() != null && tmp.getId().equals(id)) {
				return i;
			} else {
				i++;
			}
		}
		return -1;
	}

	private Object[] getDeleteRows() {
		if (deletedRows.size() == 0)
			return new Object[0];

		Object[] objs = new Object[deletedRows.size()];
		int i = 0;
		for (String id : deletedRows) {
			// int index = findBusinessDataIndex(id, backupRows);
			// objs[i++] = backupRows.get(index);
			objs[i++] = backupMap.get(id);
		}
		return objs;
	}

	private Object[] getUpdRows() {
		if (updRows.size() == 0)
			return new Object[0];
		Object[] objs = new Object[updRows.size()];
		int i = 0;
		for (String id : updRows) {
			int index = findBusinessDataIndex(id, rows);
			objs[i++] = rows.get(index);
		}
		return objs;
	}

	private RowSelectionOperationInfo getRowInfo(int[] rowIndexs, int selectionState) {
		RowSelectionOperationInfo rowInfo = new RowSelectionOperationInfo();
		rowInfo.setRowIndexes(rowIndexs);
		Object[] objs = new Object[rowIndexs.length];
		for (int i = 0; i < objs.length; i++)
			objs[i] = rows.get(rowIndexs[i]);
		rowInfo.setRowDatas(objs);
		rowInfo.setSelectionState(selectionState);
		return rowInfo;
	}

	/**
	 * ���ض�λ�ò����ض�����������
	 * 
	 * @param index
	 * @param number
	 */
	private void innerInsertLine(int index, Object[] objs, boolean isFireDataInsertEvent) {
		if (objs == null || objs.length == 0)
			return;

		newRows.addAll(Arrays.asList(objs));
		rows.addAll(index, Arrays.asList(objs));

		List<Integer> rowList = new ArrayList<Integer>();
		for (int i = 0; i < objs.length; i++)
			rowList.add(index + i);

		if (isFireDataInsertEvent)
			fireEvent(new AppEvent(AppEventConst.DATA_INSERTED, this, new RowOperationInfo(rowList, objs)));

		// ��������Զ�����״̬������Ҫ����������ѡ�С�
		if (!isInAutoAddLineMode())
			innerSetSelectedIndex(index);
		// setSelectedIndex(index);
		// ��ֹ�������ݺ�-��ӡ����getSelectedOperaDatas��ȡ����
		if (isInMultiSelectmode())
			setSelectedOperaRows(new int[] { index });
	}

	private int getInsertIndex(int startRowIndex) {

		if (selectedIndex == -1 && (startRowIndex < 0 || startRowIndex > rows.size())) {
			throw new IllegalArgumentException(NCLangRes.getInstance().getStrByID("uif2", "BatchBillTableModel-000001")/*
																														 * δѡ��������
																														 * ��
																														 * Ҳδ������ȷ�Ŀ�ʼ�к�
																														 * ��
																														 * �޷��²�������
																														 * ��
																														 */);
		}

		int index = selectedIndex;
		if (startRowIndex > -1 && startRowIndex <= rows.size())
			index = startRowIndex;
		return index;
	}

	private void turnToEditStateIfNeed() {
		if (getUiState() == UIState.NOT_EDIT)
			this.setUiState(UIState.EDIT);
	}

	public boolean isInMultiSelectmode() {
		return inMultiSelectmode;
	}

	public void setInMultiSelectmode(boolean inMultiSelectmode) {
		this.inMultiSelectmode = inMultiSelectmode;
	}

}
