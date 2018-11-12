package nc.bs.hrsms.hi.psninfo;

import nc.uap.lfw.core.comp.ReferenceComp;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.refnode.NCRefNode;
import nc.uap.lfw.core.refnode.RefNode;
import nc.uap.lfw.reference.ILfwRefModel;
import uap.lfw.ref.filter.LfwAbstractFilter;
import uap.lfw.ref.model.LfwAbstractRefModel;
import uap.lfw.ref.sqlvo.LfwReferenceSqlVO;
/**
 * 籍贯定位参照专用
 * @author tianxf
 *
 */
@SuppressWarnings("serial")
public class RefPlaceRefModel extends LfwAbstractFilter{
	
	public void processNCRefModel(ILfwRefModel refModel, RefNode refNode) {
		if(refNode instanceof NCRefNode){
			 NCRefNode ncRefNode = (NCRefNode) refNode;
			 if(ncRefNode.isFilterRefNodeNames() && refModel.getFilterRefNodeNames() != null){
				 ReferenceComp[] refComps = AppLifeCycleContext.current().getViewContext().getView().getViewComponents().getComponentByType(ReferenceComp.class);
				 int len = refComps != null ? refComps.length : 0;
				 if(len > 0 && refComps[0] != null){
					 refModel.filterValueChanged(refComps[0].getValue());
				 }
			 }
		 }
	}

	@Override
	public void processLfwRefSqlVO(LfwAbstractRefModel refModel,
			LfwReferenceSqlVO treeSqlVO, LfwReferenceSqlVO gridSqlVO) {
	}
	
}
