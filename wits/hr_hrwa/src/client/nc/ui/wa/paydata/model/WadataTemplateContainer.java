package nc.ui.wa.paydata.model;

import java.util.LinkedList;
import java.util.List;

import com.ibm.db2.jcc.am.bo;

import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.ui.pub.bill.IBillItem;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.pub.bill.BillStructVO;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletHeadVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.item.FromEnumVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.paydata.ICommonAlterName;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 薪资发放单据模板
 * 
 * @author: zhangg
 * @date: 2009-11-23 下午01:56:08
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WadataTemplateContainer extends nc.ui.uif2.editor.TemplateContainer implements IPaydataModel, ICommonAlterName
{
    
    private List<BillTempletBodyVO> preFixBodyVOs = null;
    
    protected WadataAppDataModel paydataModel = null;
    
    private static String TAB_NAME_KEY = "wa_data";
    private static String TAB_NAME_INFO = "wa_info";
    protected static String TAB_NAME_STATE = "wa_state";
    private static String TAB_NAME_ITEM = "wa_item";
    
    /**
     * 获得模板， 并对模板添加自定义的模板项目
     * 
     * @author zhangg on 2009-9-10
     * @see nc.ui.uif2.editor.TemplateContainer#getTemplate(java.lang.String, int, java.util.List)
     */
    @Override
    public BillTempletVO getTemplate(String nodeKey, int pos, List<String> tab)
    {
        return getDefualtTemplate();
    }
    
    public BillTempletVO getDefualtTemplate()
    {
        BillTempletVO billTempletVO = new BillTempletVO();
        billTempletVO.setParentVO(getBillTempletHeadVO());
        billTempletVO.setChildrenVO(initBillTempletVO());
        // 20150922 xiejie3 NCdp205496449 薪资发放卡片界面显示为页签，begin
        // guoqt解决节点打开关闭不了问题适配uap的ts不能为空
        // BillTempletHeadVO headVO = new BillTempletHeadVO();
        BillTempletHeadVO headVO = billTempletVO.getHeadVO();
        // end
        headVO.setTs(PubEnv.getServerTime());
        billTempletVO.setParentVO(headVO);
        return billTempletVO;
    }
    
    /**
     * 
     * @author zhangg on 2009-11-23
     * @return
     */
    private BillTempletBodyVO[] initBillTempletVO()
    {
        preFixBodyVOs = null;// 初始化
        List<BillTempletBodyVO> allBillVOList = new LinkedList<BillTempletBodyVO>();
        allBillVOList.addAll(getPreFixBodyVOs());
        if (getWaContext().isContextNotNull())
        {
            List<BillTempletBodyVO> list = getUserTempletBodyVO();
            if (list != null)
            {
                allBillVOList.addAll(list);
            }
        }
        return allBillVOList.toArray(new BillTempletBodyVO[0]);
    }
    
    private BillTempletHeadVO getBillTempletHeadVO()
    {
        // 创建新VO
        BillTempletHeadVO headvo = new BillTempletHeadVO();
        headvo.setBillTempletCaption(ResHelper.getString("60130paydata", "060130paydata0376")/* @res "薪资发放" */);
        headvo.setBillTempletName("SYSTEM");
        headvo.setModulecode("hrwa");
        headvo.setPkBillTypeCode("SYSTEM");
        headvo.setPkCorp("SYSTEM");
        headvo.setPkPubBilltempletHead("SYSTEM0001WA100000001");
        
        BillTabVO billTabVO = new BillTabVO();
        billTabVO.setPos(IBillItem.HEAD);
        billTabVO.setTabcode(TAB_NAME_KEY);
        billTabVO.setTabname(ResHelper.getString("common", "UC000-0000085")/* @res "主键" */);
        billTabVO.setTabindex(0);
        
        BillTabVO billTabVO1 = new BillTabVO();
        billTabVO1.setPos(IBillItem.HEAD);
        billTabVO1.setBasetab(TAB_NAME_KEY);
        billTabVO1.setTabcode(TAB_NAME_INFO);
        billTabVO1.setTabname(ResHelper.getString("60130paydata", "060130paydata0377")/* @res "人员信息" */);
        billTabVO1.setTabindex(1);
        
        BillTabVO billTabVO2 = new BillTabVO();
        billTabVO2.setPos(IBillItem.HEAD);
        billTabVO2.setBasetab(TAB_NAME_KEY);
        billTabVO2.setTabcode(TAB_NAME_STATE);
        billTabVO2.setTabname(ResHelper.getString("60130paydata", "060130paydata0378")/* @res "状态信息" */);
        billTabVO2.setTabindex(2);
        
        BillTabVO billTabVO3 = new BillTabVO();
        billTabVO3.setPos(IBillItem.HEAD);
        billTabVO3.setBasetab(TAB_NAME_KEY);
        billTabVO3.setTabcode(TAB_NAME_ITEM);
        billTabVO3.setTabname(ResHelper.getString("60130paydata", "060130paydata0379")/* @res "发放项目" */);
        billTabVO3.setTabindex(3);
        
        BillStructVO bsVO = new BillStructVO();
        bsVO.setBillTabVOs(new BillTabVO[]{billTabVO, billTabVO1, billTabVO2, billTabVO3});
        
        headvo.setStructvo(bsVO);
        
        return headvo;
    }
    
    /**
     * 得到预制的信息项目
     * 
     * @author zhangg on 2009-11-23
     * @return
     */
    public List<BillTempletBodyVO> getPreFixBodyVOs()
    {
        if (preFixBodyVOs == null)
        {
            preFixBodyVOs = new LinkedList<BillTempletBodyVO>();
            int showOrder = 0;
            // 隐藏的部分
            BillTempletBodyVO bodyVO = getbaseBodyVO(TAB_NAME_KEY);
            bodyVO.setItemkey(DataVO.PK_WA_DATA);
            bodyVO.setDefaultshowname(DataVO.PK_WA_DATA);
            bodyVO.setListshowflag(false);
            bodyVO.setShowflag(false);
            bodyVO.setShoworder(showOrder++);
            preFixBodyVOs.add(bodyVO);
            
            bodyVO = getbaseBodyVO(TAB_NAME_KEY);
            bodyVO.setItemkey("ts");
            bodyVO.setDefaultshowname("ts");
            bodyVO.setListshowflag(false);
            bodyVO.setShowflag(false);
            bodyVO.setShoworder(showOrder++);
            preFixBodyVOs.add(bodyVO);
            
            // 显示部分
            bodyVO = getbaseBodyVO(TAB_NAME_INFO);
            bodyVO.setItemkey(PSNCODE);
            bodyVO.setDefaultshowname(ResHelper.getString("common", "UC000-0000147")/* @res "人员编码 */);
            bodyVO.setShoworder(showOrder++);
            preFixBodyVOs.add(bodyVO);
            
            // 显示部分
            bodyVO = getbaseBodyVO(TAB_NAME_INFO);
            bodyVO.setItemkey(CLERKCODE);
            bodyVO.setDefaultshowname(ResHelper.getString("60130adjapprove", "160130adjapprove0009")/*
                                                                                                     * @res
                                                                                                     * "员工号"
                                                                                                     */);
            bodyVO.setShoworder(showOrder++);
            preFixBodyVOs.add(bodyVO);
            
            bodyVO = getbaseBodyVO(TAB_NAME_INFO);
            bodyVO.setItemkey(DataVO.PSNNAME);
            bodyVO.setDefaultshowname(ResHelper.getString("common", "UC000-0001403")/* @res "姓名" */);
            bodyVO.setShoworder(showOrder++);
            preFixBodyVOs.add(bodyVO);
            
            bodyVO = getbaseBodyVO(TAB_NAME_INFO);
            bodyVO.setItemkey(PLSNAME);
            bodyVO.setDefaultshowname(ResHelper.getString("common", "UC000-0000140")/* @res "人员类别" */);
            bodyVO.setShoworder(showOrder++);
            preFixBodyVOs.add(bodyVO);
            
            bodyVO = getbaseBodyVO(TAB_NAME_INFO);
            bodyVO.setItemkey(ORGNAME);
            bodyVO.setDefaultshowname(ResHelper.getString("60130payfile", "160130payfile0009")/* @res "任职组织" */);
            bodyVO.setShoworder(showOrder++);
            preFixBodyVOs.add(bodyVO);
            
            bodyVO = getbaseBodyVO(TAB_NAME_INFO);
            bodyVO.setItemkey(DEPTNAME);
            bodyVO.setDefaultshowname(ResHelper.getString("common", "UC000-0004064")/* @res "部门" */);
            bodyVO.setShoworder(showOrder++);
            preFixBodyVOs.add(bodyVO);
            
            bodyVO = getbaseBodyVO(TAB_NAME_INFO);
            bodyVO.setItemkey(POSTNAME);
            bodyVO.setDefaultshowname(ResHelper.getString("common", "UC000-0001653")/* @res "所在岗位" */);
            bodyVO.setShoworder(showOrder++);
            preFixBodyVOs.add(bodyVO);
            
            // 2015-07-30 zhosuze NCdp205099799 薪资发放界面添加财务组织财务部门 begin
            bodyVO = getbaseBodyVO(TAB_NAME_INFO);
            bodyVO.setItemkey(FINANCEORG);
            bodyVO.setDefaultshowname(ResHelper.getString("60130payfile", "160130payfile0026")/* @res "财务组织" */);
            bodyVO.setShoworder(showOrder++);
            preFixBodyVOs.add(bodyVO);
            
            bodyVO = getbaseBodyVO(TAB_NAME_INFO);
            bodyVO.setItemkey(FINANCEDEPT);
            bodyVO.setDefaultshowname(ResHelper.getString("60130payfile", "160130payfile0023")/* @res "财务部门" */);
            bodyVO.setShoworder(showOrder++);
            preFixBodyVOs.add(bodyVO);
            
            //zhaochxs加入纳税申报组织
            bodyVO = getbaseBodyVO(TAB_NAME_INFO);
            bodyVO.setItemkey("taxorgname");
            bodyVO.setDefaultshowname("纳税申报组织");
            bodyVO.setShoworder(showOrder++);
            preFixBodyVOs.add(bodyVO);
            
            bodyVO = getbaseBodyVO(TAB_NAME_INFO);
            bodyVO.setItemkey(LIABILITYORG);
            bodyVO.setDefaultshowname(ResHelper.getString("60130payfile", "160130payfile0024")/* @res "成本中心" */);
            bodyVO.setShoworder(showOrder++);
            preFixBodyVOs.add(bodyVO);
            
            bodyVO = getbaseBodyVO(TAB_NAME_INFO);
            bodyVO.setItemkey(LIABILITYDEPT);
            bodyVO.setDefaultshowname(ResHelper.getString("60130payfile", "160130payfile0025")/* @res "成本部门" */);
            bodyVO.setShoworder(showOrder++);
            preFixBodyVOs.add(bodyVO);
            // end
            
            bodyVO = getbaseBodyVO(TAB_NAME_INFO);
            bodyVO.setItemkey(DataVO.TAXTYPE);
            bodyVO.setDatatype(IBillItem.COMBO);
            bodyVO.setDefaultshowname(ResHelper.getString("60130payfile", "060130payfile0310")/* @res "扣税方式" */);
            bodyVO.setReftype(ResHelper.getString("60130payfile", "060130payfile0312")/*
                                                                                       * @res
                                                                                       * "IX,代扣税=0,代付税=1,不扣税=2"
                                                                                       */);
            bodyVO.setShoworder(showOrder++);
            preFixBodyVOs.add(bodyVO);
            
            bodyVO = getbaseBodyVO(TAB_NAME_STATE);
            bodyVO.setItemkey(DataVO.CACULATEFLAG);
            bodyVO.setDatatype(IBillItem.BOOLEAN);
            bodyVO.setDefaultshowname(ResHelper.getString("60130paydata", "060130paydata0380")/* @res "计算标志" */);
            bodyVO.setShoworder(showOrder++);
            preFixBodyVOs.add(bodyVO);
            
            bodyVO = getbaseBodyVO(TAB_NAME_STATE);
            bodyVO.setItemkey(DataVO.CHECKFLAG);
            bodyVO.setDatatype(IBillItem.BOOLEAN);
            bodyVO.setDefaultshowname(ResHelper.getString("60130paydata", "060130paydata0381")/* @res "审核标志" */);
            bodyVO.setShoworder(showOrder++);
            preFixBodyVOs.add(bodyVO);
            
            List list = getCenterBodyVOs(showOrder);
            
            showOrder = showOrder + list.size();
            
            preFixBodyVOs.addAll(list);
            
            bodyVO = getbaseBodyVO(TAB_NAME_STATE);
            bodyVO.setItemkey(DataVO.CYEAR);
            bodyVO.setDefaultshowname(ResHelper.getString("common", "UC000-0001802")/* @res "年度" */);
            bodyVO.setListshowflag(false);
            bodyVO.setShowflag(false);
            bodyVO.setShoworder(showOrder++);
            preFixBodyVOs.add(bodyVO);
            
            bodyVO = getbaseBodyVO(TAB_NAME_STATE);
            bodyVO.setItemkey(DataVO.CPERIOD);
            bodyVO.setDefaultshowname(ResHelper.getString("common", "UC000-0002560")/* @res "期间" */);
            bodyVO.setListshowflag(false);
            bodyVO.setShowflag(false);
            bodyVO.setShoworder(showOrder++);
            preFixBodyVOs.add(bodyVO);
            
        }
        return preFixBodyVOs;
    }
    
    /**
     * 得到预制的信息项目
     * 
     * @author zhangg on 2009-11-23
     * @return
     */
    protected List<BillTempletBodyVO> getCenterBodyVOs(int showOrder)
    {
        LinkedList centerBodyVOs = new LinkedList<BillTempletBodyVO>();
        return centerBodyVOs;
    }
    
    /**
     * 
     * @author zhangg on 2009-9-9
     * @return
     */
    public List<BillTempletBodyVO> getUserTempletBodyVO()
    {
        List<BillTempletBodyVO> billTempletBodyVOs = null;
        WaClassItemVO[] itemVOs = getUserItems();
        if (itemVOs != null)
        {
            billTempletBodyVOs = new LinkedList<BillTempletBodyVO>();
            for (int i = 0; i < itemVOs.length; i++)
            {
                billTempletBodyVOs.add(convert2TempletBodyVO(itemVOs[i]));
            }
        }
        return billTempletBodyVOs;
    }
    
    public WaClassItemVO[] getUserItems()
    {
        return getPaydataModel().getClassItemVOs();
    }
    
    /**
     * 将item转换为bodyVO
     * 
     * @author zhangg on 2009-11-23
     * @param baseBodyVO
     * @param object
     * @return
     */
    public BillTempletBodyVO convert2TempletBodyVO(WaClassItemVO item)
    {
        
        if (item == null)
        {
            return null;
        }
        BillTempletBodyVO billTempletBodyVO = getbaseBodyVO(TAB_NAME_ITEM);
        billTempletBodyVO.setDefaultshowname(item.getMultilangName());
        billTempletBodyVO.setItemkey(item.getItemkey());
        // 录入长度
        billTempletBodyVO.setInputlength(item.getIfldwidth());
        // 根据ITEM的数据类型， 设置BILLVO类型
        if (item.getIitemtype().equals(TypeEnumVO.FLOATTYPE.value()))
        {
            billTempletBodyVO.setDatatype(IBillItem.DECIMAL);
            
            // 数值型设置小数位数
            int digits = item.getIflddecimal();
            billTempletBodyVO.setInputlength(item.getIfldwidth() + 1 + digits);
            String refType = Math.abs(digits) + "";
            billTempletBodyVO.setReftype(refType);
            billTempletBodyVO.setTotalflag(true);
        }
        else if (item.getIitemtype().equals(TypeEnumVO.DATETYPE.value()))
        {
            billTempletBodyVO.setDatatype(IBillItem.LITERALDATE);
        }
        else
        {
            billTempletBodyVO.setDatatype(IBillItem.STRING);
        }
        // 根据数据来源设置是否允许编辑
        if (item.getIfromflag().equals(FromEnumVO.USER_INPUT.value()))
        {
            billTempletBodyVO.setEditflag(item.getEditflag().booleanValue());
        }
        else
        {
            billTempletBodyVO.setEditflag(Boolean.FALSE);
        }
        // 显示顺序
        billTempletBodyVO.setShoworder(item.getIdisplayseq() + 18);
        // 是否显示
        billTempletBodyVO.setShowflag(item.getShowflag().booleanValue());
        return billTempletBodyVO;
    }
    
    /**
     * 
     * @author zhangg on 2009-11-23
     * @return
     */
    public static BillTempletBodyVO getbaseBodyVO(String tabCode)
    {
        BillTempletBodyVO bVO = new BillTempletBodyVO();
        // 变化部分
        bVO.setEditflag(Boolean.FALSE);
        bVO.setItemkey("itemKey");
        bVO.setDefaultshowname("defaultShowName");
        bVO.setShoworder(0);
        bVO.setTotalflag(Boolean.FALSE);
        bVO.setDatatype(IBillItem.STRING);
        // 不变部分
        bVO.setInputlength(Integer.valueOf(-1));
        bVO.setNullflag(Boolean.FALSE); // null allowed
        bVO.setCardflag(Boolean.TRUE);
        bVO.setListflag(Boolean.TRUE);
        bVO.setLockflag(Boolean.FALSE);
        bVO.setShowflag(Boolean.TRUE);
        bVO.setUsereditflag(Boolean.TRUE);
        bVO.setUserflag(Boolean.TRUE);
        bVO.setUsershowflag(Boolean.TRUE);
        bVO.setLeafflag(UFBoolean.FALSE);
        bVO.setNewlineflag(UFBoolean.FALSE);
        bVO.setReviseflag(UFBoolean.FALSE);
        bVO.setPos(IBillItem.HEAD);
        bVO.setWidth(1);
        bVO.setForeground(Integer.valueOf(-1));
        bVO.setTable_code(tabCode);
        
        return bVO;
    }
    
    public WaLoginContext getWaContext()
    {
        return (WaLoginContext) super.getContext();
    }
    
    /**
     * @author zhangg on 2009-12-29
     * @return the paydataModel
     */
    public WadataAppDataModel getPaydataModel()
    {
        return paydataModel;
    }
    
    /**
     * @author zhangg on 2009-12-29
     * @param paydataModel the paydataModel to set
     */
    public void setPaydataModel(WadataAppDataModel paydataModel)
    {
        this.paydataModel = paydataModel;
    }
    
}
