package nc.bs.hrsms.ta.shift.lsnr;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.itf.hrsms.ta.shift.IStoreShiftManageMaintain;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.data.Dataset;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.RTVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.AggregatedValueObject;

import org.apache.commons.lang.StringUtils;

public class StoreShiftSaveProcessor implements ISaveProcessor{

	@Override
	public boolean checkBeforeVOSave(AggregatedValueObject arg0)
			throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public AggregatedValueObject onAfterVOSave(Dataset ds, Dataset[] dsDetails,
			AggregatedValueObject aggVO) throws Exception {
		// TODO Auto-generated method stub
//		ApplicationContext appCtx = AppLifeCycleContext.current().getApplicationContext();
		//appCtx.removeAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS);
		// 关闭弹出页面
		CmdInvoker.invoke(new CloseWindowCmd());
		// 执行左侧快捷查询
		CmdInvoker.invoke(new UifPlugoutCmd("main", "closewindow"));
		return null;
	}

	@Override
	public void onBeforeVOSave(AggregatedValueObject arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AggregatedValueObject onVOSave(AggregatedValueObject aggVO)
			throws Exception {
		// TODO Auto-generated method stub
		IStoreShiftManageMaintain service = null;
		try {
			service = NCLocator.getInstance().lookup(IStoreShiftManageMaintain.class);
			String primaryKey = aggVO.getParentVO().getPrimaryKey();
			AggShiftVO aggvo = (AggShiftVO) aggVO;
			RTVO[] rtVOs = aggvo.getRTVOs();
			List<RTVO> rtlist = new ArrayList<RTVO>();
			for(int i=0;i<rtVOs.length;i++){
				RTVO rtVO = new RTVO();
				if(StringUtils.isNotEmpty(rtVOs[i].getBegintime())){
					rtVO = rtVOs[i];
					rtlist.add(rtVO);
				}
			}
			RTVO[] newrtVOs = (RTVO[])rtlist.toArray(new RTVO[rtlist.size()]);
			aggvo.setRTVOs(newrtVOs);
			
//			IShiftManageMaintain shift = NCLocator.getInstance().lookup(IShiftManageMaintain.class);
			if (StringUtil.isEmptyWithTrim(primaryKey)) {
				aggvo = service.insert(aggvo);
			} else {
//				service.deleteByUpdate(aggvo);
				aggvo = service.update(aggvo);
			}
		} catch (Exception e) {
			new HrssException(e).deal();
		}
		return aggVO;
	}

}
