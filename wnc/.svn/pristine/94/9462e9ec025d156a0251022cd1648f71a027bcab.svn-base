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
 * 批量操作的应用模型
 * 
 * 批量增加、修改、删除之后进行批量统一保存；
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

	// 模型持久化操作服务，需要IOC注入
	private IBatchAppModelService service;

	// 对象转换工厂类，需要IOC注入。
	private IBDObjectAdapterFactory businessObjectAdapterFactory;

	// 保存前的模型校验逻辑，请不要使用IOC注入,通过BatchSaveAction进行注入。
	private IBatchValidationService validationService = null;

	// 存储当前与视图同步的数据行，无论是新增或修改，始终都是用户当前编辑的最新结果。
	private List<Object> rows = new ArrayList<Object>();

	// 恢复用的数据备份,编辑之前会保存在此处
	// private List<Object> backupRows = new ArrayList<Object>();
	private Map<String, Object> backupMap = new LinkedHashMap<String, Object>();

	// 新增数据的存储，存储引用避免map存储hashcode未被定义的问题
	private List<Object> newRows = new ArrayList<Object>();

	// 保存被删除数据的标识
	private List<String> deletedRows = new ArrayList<String>();
	private List<Integer> delIndexs = new ArrayList<Integer>();

	// 存储被更新操作的标识
	private List<String> updRows = new ArrayList<String>();

	// 当前选中行号
	private int selectedIndex = -1;

	// 当前选择数据
	private Object selectedObject = null;

	// 深度复制而成的拷贝对象
	private Object copyObject = null;

	/** 当前多选框选中的行，比如用于删除、审批等操作 */
	private List<Integer> selectedOperaRows = new ArrayList<Integer>();

	// 是否处于自动增行状态，如果自动增行状态则不需要设置选中行。内部属性，不需要注入。
	private boolean inAutoAddLineMode = false;

	// 是否允许进行多选
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
		// 同时设置单选和多选标志
		innerSetSelectedIndex(rows.size() > 0 ? 0 : -1);
	}

	/**
	 * 追加特定的新数据
	 * 
	 * @param rowCount
	 * @throws Exception
	 */
	public void addLines(Object[] objs) {
		addLines(objs, true);
	}

	public void addLines(Object[] objs, boolean isFireDataInsertEvent) {
		// 如果为非编辑状态，则切换为编辑状态
		turnToEditStateIfNeed();
		innerInsertLine(rows.size(), objs, isFireDataInsertEvent);
	}

	/**
	 * 从特定的行号插入特定行的新数据
	 * 
	 * @param startRowIndex
	 *            如果为-1或大于当前行数，则按当前选择行的位置，如果无选择行，则不进行操作。
	 * @throws Exception
	 */
	public void insertLines(int startRowIndex, Object[] objs) {
		int index = getInsertIndex(startRowIndex);
		turnToEditStateIfNeed();
		innerInsertLine(index, objs, true);
	}

	/**
	 * 更新模型某行数据
	 * 
	 * @param i
	 * @param obj
	 */
	public void updateLine(int index, Object obj) {

		// 点击表头排序后，rows中的数据未排序，外部传进来的index不可靠
		int realIndex = findBusinessDataIndex(obj, rows);

		int operatorIndex = -1;
		if (realIndex == -1) {
			operatorIndex = index;
		} else {
			operatorIndex = realIndex;
		}

		Object targetObj = rows.get(operatorIndex);
		// 如果是新增加的数据进行修改，则直接修改即可。
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
			// 如果是新增加的数据进行修改，则直接修改即可。
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
	 * 模型删行操作
	 * 
	 */
	public void delLine(int delIndex) throws Exception {
		Integer[] indexs = getDelLineIndex(delIndex);

		// 如果是非编辑状态，则直接进行数据库删除操作
		List<Object> list = new ArrayList<Object>();
		if (getUiState() == UIState.NOT_EDIT) {
			processDelLindeInNotEdit(indexs, list);
		} else {

			processDelLineInEdit(indexs, list);
		}

		rows.removeAll(list);

		// 删除行后，需将选中行中的已删除行移除，否则getSelectedOperaDatas()会抛出数组越界错误
		for (int i = 0; i < indexs.length; i++) {
			selectedOperaRows.remove(indexs[i]);
		}

		// 通知界面删除数据
		fireEvent(new AppEvent(AppEventConst.DATA_DELETED, this, new RowOperationInfo(Arrays.asList(indexs),
				list.toArray())));

		// 重新设置选中行
		processSelectedIndexWhenDel(indexs);
	}

	/**
	 * 如果传入参数合法以传入参数为准，如果传入参数错误，则以当前选择行为准。如无选择行，则异常
	 * 
	 * @param index
	 * @throws Exception
	 */
	public void copyLine(int copyIndex) {

		int index = getInsertIndex(copyIndex);
		copyObject = BatchModelUtil.cloneObject(getRow(index));
	}

	/**
	 * 对之前的操作进行撤销
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

		// 先切换到非编辑状态，再设置选中行。
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
	 * 获取当前模型数据的行号
	 * 
	 * @param obj
	 * @param isNew
	 *            是否是新增，true:新增；false：修改
	 * @return
	 */
	public int getIndexOfObject(Object obj, boolean isNew) {
		if (isNew)
			return rows.indexOf(obj);
		else
			return findBusinessDataIndex(obj, rows);
	}

	/**
	 * 状态切换
	 */
	public void setUiState(UIState uiState) {

		// 进入编辑态之前，进行数据备份
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
	 * 模型内部使用设置选中行，会同时处理单行或多行选中的标志位
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
	 * 获取当前可保存的数据对象
	 * 
	 * @return
	 */
	public BatchOperateVO getCurrentSaveObject() {
		// 准备操作数据
		Object[] newAddObj = newRows.toArray();
		// 如果pk_org和pk_group都为空则为全局
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

		// 数据库操作
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
	 * 获取被修改对象对应的修改之前的对象
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
																														 * 必须传入正确的删除行
																														 * ，
																														 * 或在有选中行的情况下删除
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
	 * 删除之后对选中行的处理
	 * 
	 * @param indexs
	 */
	protected void processSelectedIndexWhenDel(Integer[] indexs) {

		// 只要删除行中存在当前选中行或包含至少一个比当前选中行序号小的行，都需要进行选中行调整
		boolean isExist = false;
		for (Integer index : indexs) {
			if (index <= selectedIndex)
				isExist = true;
		}
		if (!isExist)
			return;
		// TODO 先采用此种方式，如果精确计算，需要选中删除前选中行记录的上一条记录，至于删除后的记录序号，则需要经过计算，比较繁琐。
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

				if (updRows.contains(id)) {// 当前被删除数据之前被修改过，则删除掉修改记录。
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
	 * 向特定位置插入特定行数的数据
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

		// 如果处于自动增行状态，则不需要设置新增行选中。
		if (!isInAutoAddLineMode())
			innerSetSelectedIndex(index);
		// setSelectedIndex(index);
		// 防止导入数据后-打印调用getSelectedOperaDatas获取不到
		if (isInMultiSelectmode())
			setSelectedOperaRows(new int[] { index });
	}

	private int getInsertIndex(int startRowIndex) {

		if (selectedIndex == -1 && (startRowIndex < 0 || startRowIndex > rows.size())) {
			throw new IllegalArgumentException(NCLangRes.getInstance().getStrByID("uif2", "BatchBillTableModel-000001")/*
																														 * 未选择数据行
																														 * ，
																														 * 也未传入正确的开始行号
																														 * ，
																														 * 无法新插入数据
																														 * ！
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
