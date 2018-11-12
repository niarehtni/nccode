package nc.bs.hrsms.ta.common.ctrl;

import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.core.refnode.RefNode;
import nc.uap.lfw.reference.ILfwRefModel;
import nc.uap.lfw.reference.app.AppReferenceController;

import org.apache.commons.lang.StringUtils;


/**
 * 作用：设置参照的组织主键和集团主键.<br/>
 * 值来源：参照写入数据集(写入数据集存在主数据集时,取值为主数据集)的组织主键和集团主键<br/>
 * 适用范围：店员加班登记的非查询区的参照.<br/>
 * 
 * @author shaochj
 * @date May 22, 2015
 * 
 */
public class ShopTaRegRefController extends AppReferenceController {
	@Override
	protected void processSelfWherePart(Dataset ds, RefNode rfnode, String filterValue, ILfwRefModel refModel) {
		resetRefnode(rfnode, refModel);
	}

	@Override
	protected void processTreeSelWherePart(Dataset ds, RefNode rfnode, ILfwRefModel refModel) {
		resetRefnode(rfnode, refModel);
	}

	/**
	 * 重新设置RefNode的值
	 * 
	 * @param refModel
	 */
	private void resetRefnode(RefNode rfnode, ILfwRefModel refModel) {
		// 集团
		String pk_group = null;
		// 人力资源组织
		String pk_hr_org = null;
		// 非查询区的参照
		Dataset parentDs = getParentDs(rfnode);
		if (parentDs == null) {
			return;
		}
		Row row = parentDs.getSelectedRow();
		if (row == null) {
			return;
		}
		if (parentDs.nameToIndex("pk_group") > -1) {
			pk_group = row.getString(parentDs.nameToIndex("pk_group"));
		}
		if (parentDs.nameToIndex("pk_org") > -1) {
			pk_hr_org = row.getString(parentDs.nameToIndex("pk_org"));
		}
		refModel.setPk_group(pk_group);
		refModel.setPk_org(pk_hr_org);
	}

	/**
	 * 获取参照所在数据集的当前选中行
	 * 
	 * @return
	 */
	private Dataset getParentDs(RefNode rfnode) {
		// 参照所在页面
		String parentPageId = LfwRuntimeEnvironment.getWebContext().getWebSession().getOriginalParameter("otherPageId");
		LfwWindow parentPm = null;
		if (!StringUtils.isEmpty(parentPageId)) {
			parentPm = AppLifeCycleContext.current().getApplicationContext().getWindowContext(parentPageId).getWindow();
		} else {
			parentPm = AppLifeCycleContext.current().getWindowContext().getWindow();
		}
		// 参照所在片段
		LfwView widget = parentPm.getView(rfnode.getView().getId());
		// 参照写入数据集
		String writeDsId = rfnode.getWriteDs();
		return widget.getViewModels().getDataset(writeDsId);
		
	}

}
