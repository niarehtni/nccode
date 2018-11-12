package nc.ui.wa.pub.standardpsntemplet;

import java.util.ArrayList;
import java.util.List;

import nc.ui.pub.bill.IBillItem;
import nc.ui.ta.pub.standardpsntemplet.PsnTempletUtils;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.org.DeptVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletHeadVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.importdata.ImportDataVO;
import nc.vo.ta.machineset.MachineVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.signcard.SignCommonVO;
import nc.vo.ta.signcard.SignRegVO;

public class PsnTempletAddUtils {

	/**
	 * 创建一个基本的模板，隐藏列有任职主键和基本档案主键，显示员工号编码姓名组织部门
	 * @return
	 */
	public static BillTempletVO createBasicItems(int pos){
		BillTempletVO btv = createBillTempletVO();
		btv.setChildrenVO(getBasicBillTempletVO(pos).toArray(new BillTempletBodyVO[0]));
		return btv;
	}
	
	public static BillTempletVO createBasicDeptItems(int pos){
//		BillTempletVO btv = createBillTempletVO("hrhi.hi_psnjob","HRHI");
		BillTempletVO btv = createBillTempletVO("hrta.tbm_psndoc","HRHI");
		btv.setChildrenVO(getBasicDeptTempletVO(pos).toArray(new BillTempletBodyVO[0]));
		return btv;
	}
	
	/**
	 * basic信息界面的可见项目个数
	 * @return
	 */
	public static int getBasicVisibleItemCount(){
		return 5;
	}
	
	/**
	 * senior信息界面的可见项目个数
	 * @return
	 */
	public static int getSeniorVisibleItemCount(){
		return 7;
	}
	
	/**
	 * 创建一个高级的模板，除了basicitem中的列之外，还提供了职务、岗位列
	 * @return
	 */
	public static BillTempletVO createSeniorItems(int pos){
		BillTempletVO btv = createBillTempletVO();
		btv.setChildrenVO(getSeniorBillTempletVO(pos).toArray(new BillTempletBodyVO[0]));
		return btv;
	}
	
	/**
	 * 创建标准的表头信息
	 * @return
	 */
	public static BillTempletVO createBillTempletVO(){
		return createBillTempletVO("hrta.tbm_psndoc", "HRTA");
	}
	
	/**
	 * 创建标准的表头信息
	 * @return
	 */
	public static BillTempletVO createBillTempletVO(String metadataclass,String moduleCode){
		BillTempletVO btv = new BillTempletVO();
		BillTempletHeadVO headVO = new BillTempletHeadVO();
		headVO.setBillTempletName("SYSTEM");
		headVO.setMetadataclass(metadataclass);
		headVO.setModulecode(moduleCode);
		btv.setParentVO(headVO);
		return btv;
	}
	
	/**
	 * 在基本信息的基础上增加职务岗位
	 * @return
	 */
	public static List<BillTempletBodyVO> getSeniorBillTempletVO(int pos) {
		List<BillTempletBodyVO> basicItemList = getBasicBillTempletVO(pos);
		int iShowOrder = basicItemList.get(basicItemList.size()-1).getShoworder();
		BillTempletBodyVO templetBodyVO = createDefaultBillTempletBodyVO(pos,iShowOrder++);
		int width = pos==IBillItem.HEAD?1:100;
		
		//职务列
		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
		templetBodyVO.setNullflag(false);
		templetBodyVO.setListshowflag(false);
		templetBodyVO.setShowflag(false);
		templetBodyVO.setDatatype(IBillItem.UFREF);
		templetBodyVO.setDefaultshowname(null);
		templetBodyVO.setItemkey(TBMPsndocVO.PK_PSNJOB+"."+PsnJobVO.PK_JOB);
		templetBodyVO.setIdcolname(TBMPsndocVO.PK_PSNJOB);
		templetBodyVO.setMetadataproperty("hrhi.hi_psnjob.pk_job");
		templetBodyVO.setMetadatapath(PsnJobVO.PK_JOB);
		templetBodyVO.setShoworder(iShowOrder++);
		templetBodyVO.setWidth(width);
		basicItemList.add(templetBodyVO);
		
		//岗位列
		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
		templetBodyVO.setNullflag(false);
		templetBodyVO.setListshowflag(true);
		templetBodyVO.setShowflag(true);
		templetBodyVO.setDatatype(IBillItem.UFREF);
		templetBodyVO.setDefaultshowname(null);
		templetBodyVO.setItemkey(TBMPsndocVO.PK_PSNJOB+"."+PsnJobVO.PK_POST);
		templetBodyVO.setIdcolname(TBMPsndocVO.PK_PSNJOB);
		templetBodyVO.setMetadataproperty("hrhi.hi_psnjob.pk_post");
		templetBodyVO.setMetadatapath(PsnJobVO.PK_POST);
		templetBodyVO.setShoworder(iShowOrder++);
		templetBodyVO.setWidth(width);
		basicItemList.add(templetBodyVO);
		
		return basicItemList;
	}
	
	/**
	 * 人员主键，任职主键，员工号编码姓名组织部门
	 * @return
	 */
	public static List<BillTempletBodyVO> getBasicBillTempletVO(int pos) {
		return getBasicBillTempletVO(pos, "hrta.tbm_psndoc");
	}
	public static List<BillTempletBodyVO> getBasicDeptTempletVO(int pos){
//		return getBasicDeptTempletVO(pos, "hrhi.hi_psnjob");
		return getBasicDeptTempletVO(pos, "hrta.tbm_psndoc");
	}
	
	public static List<BillTempletBodyVO> getBasicDeptTempletVO(int pos,String metaData){
		int iShowOrder = 0;
		List<BillTempletBodyVO> arrListBodyVO = new ArrayList<BillTempletBodyVO>();
		BillTempletBodyVO templetBodyVO = createDefaultBillTempletBodyVO(pos,iShowOrder++);
		int width = pos==IBillItem.HEAD?1:100;

		//部门主键列
		templetBodyVO.setListshowflag(false);
		templetBodyVO.setShowflag(true);
		templetBodyVO.setDatatype(IBillItem.STRING);
		templetBodyVO.setDefaultshowname("pk_dept");
		templetBodyVO.setItemkey(DeptVO.PK_DEPT);
		templetBodyVO.setMetadataproperty(metaData+"."+DeptVO.PK_DEPT);
		templetBodyVO.setMetadatapath(DeptVO.PK_DEPT);
		templetBodyVO.setShoworder(iShowOrder++);
		templetBodyVO.setWidth(width);
		arrListBodyVO.add(templetBodyVO);


//		//组织列
//		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
//		templetBodyVO.setNullflag(false);
//		templetBodyVO.setListshowflag(true);
//		templetBodyVO.setShowflag(true);
//		templetBodyVO.setDatatype(IBillItem.UFREF);
//		templetBodyVO.setDefaultshowname("组织");
//		templetBodyVO.setItemkey(DeptVO.PK_DEPT+"."+DeptVO.PK_ORG);
//		templetBodyVO.setIdcolname(DeptVO.PK_DEPT);
//		templetBodyVO.setMetadataproperty("hrjf.hrdept.pk_org");
//		templetBodyVO.setMetadatapath(TBMPsndocVO.PK_ORG);
//		templetBodyVO.setShoworder(iShowOrder++);
//		templetBodyVO.setWidth(width);
//		arrListBodyVO.add(templetBodyVO);
		
		//组织版本列
		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
		templetBodyVO.setNullflag(false);
		templetBodyVO.setListshowflag(true);
		templetBodyVO.setShowflag(true);
		templetBodyVO.setDatatype(IBillItem.UFREF);
		templetBodyVO.setDefaultshowname(PublicLangRes.ORG());
		templetBodyVO.setItemkey(TBMPsndocVO.PK_ORG_V);
		templetBodyVO.setIdcolname(null);
		templetBodyVO.setMetadataproperty(metaData+"."+TBMPsndocVO.PK_ORG_V);
		templetBodyVO.setMetadatapath(TBMPsndocVO.PK_ORG_V);
		templetBodyVO.setShoworder(iShowOrder++);
		templetBodyVO.setWidth(width);
		arrListBodyVO.add(templetBodyVO);

//		//部门列
//		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
//		templetBodyVO.setNullflag(false);
//		templetBodyVO.setListshowflag(true);
//		templetBodyVO.setShowflag(true);
//		templetBodyVO.setDatatype(IBillItem.MULTILANGTEXT);
//		templetBodyVO.setDefaultshowname("部门");
//		templetBodyVO.setItemkey(DeptVO.PK_DEPT+"."+DeptVO.NAME);
//		templetBodyVO.setIdcolname(DeptVO.PK_DEPT);
//		templetBodyVO.setMetadataproperty("hrjf.hrdept.name");
//		templetBodyVO.setMetadatapath(DeptVO.NAME);
//		templetBodyVO.setShoworder(iShowOrder++);
//		templetBodyVO.setWidth(width);
//		arrListBodyVO.add(templetBodyVO);
		
		//部门版本列
		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
		templetBodyVO.setNullflag(false);
		templetBodyVO.setListshowflag(true);
		templetBodyVO.setShowflag(true);
		templetBodyVO.setDatatype(IBillItem.UFREF);
		templetBodyVO.setDefaultshowname(PublicLangRes.DEPT());
		templetBodyVO.setItemkey(TBMPsndocVO.PK_DEPT_V);
		templetBodyVO.setIdcolname(null);
		templetBodyVO.setMetadataproperty(metaData+"."+TBMPsndocVO.PK_DEPT_V);
		templetBodyVO.setMetadatapath(TBMPsndocVO.PK_DEPT_V);
		templetBodyVO.setShoworder(iShowOrder++);
		templetBodyVO.setWidth(width);
		arrListBodyVO.add(templetBodyVO);
		
		return arrListBodyVO;
	}
	
	/**
	 * 人员主键，任职主键，员工号编码姓名组织部门
	 * @return
	 */
	public static List<BillTempletBodyVO> getBasicBillTempletVO(int pos,String metaData) {
		int iShowOrder = 0;
		List<BillTempletBodyVO> arrListBodyVO = new ArrayList<BillTempletBodyVO>();
		BillTempletBodyVO templetBodyVO = createDefaultBillTempletBodyVO(pos,iShowOrder++);
		int width = pos==IBillItem.HEAD?1:100;

		//任职主键列
		templetBodyVO.setListshowflag(false);
		templetBodyVO.setShowflag(false);
		templetBodyVO.setDatatype(IBillItem.STRING);
		templetBodyVO.setDefaultshowname("pk_psnjob");
		templetBodyVO.setItemkey(TBMPsndocVO.PK_PSNJOB);
		templetBodyVO.setMetadataproperty(metaData+"."+TBMPsndocVO.PK_PSNJOB);
		templetBodyVO.setMetadatapath(TBMPsndocVO.PK_PSNJOB);
		templetBodyVO.setShoworder(iShowOrder++);
		templetBodyVO.setWidth(width);
		arrListBodyVO.add(templetBodyVO);

		//基本信息主键列
		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
		templetBodyVO.setListshowflag(false);
		templetBodyVO.setShowflag(false);
		templetBodyVO.setDatatype(IBillItem.UFREF);
		templetBodyVO.setDefaultshowname("pk_psndoc");
		templetBodyVO.setItemkey(TBMPsndocVO.PK_PSNDOC);
		templetBodyVO.setMetadataproperty(metaData+"."+TBMPsndocVO.PK_PSNDOC);
		templetBodyVO.setMetadatapath(TBMPsndocVO.PK_PSNDOC);
		templetBodyVO.setShoworder(iShowOrder++);
		templetBodyVO.setWidth(width);
		arrListBodyVO.add(templetBodyVO);

		//员工号列
		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
		templetBodyVO.setNullflag(false);
		templetBodyVO.setListshowflag(true);
		templetBodyVO.setShowflag(true);
		templetBodyVO.setDatatype(IBillItem.STRING);
		templetBodyVO.setDefaultshowname(null);
		templetBodyVO.setItemkey(TBMPsndocVO.PK_PSNJOB+"."+PsnJobVO.CLERKCODE);
		templetBodyVO.setIdcolname(TBMPsndocVO.PK_PSNJOB);
		templetBodyVO.setMetadataproperty("hrhi.hi_psnjob.clerkcode");
		templetBodyVO.setMetadatapath(PsnJobVO.CLERKCODE);
		templetBodyVO.setShoworder(iShowOrder++);
		templetBodyVO.setWidth(width);
		arrListBodyVO.add(templetBodyVO);
		
		//编码列
		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
		templetBodyVO.setNullflag(false);
		templetBodyVO.setListshowflag(true);
		templetBodyVO.setShowflag(true);
		templetBodyVO.setDatatype(IBillItem.STRING);
		templetBodyVO.setDefaultshowname(null);
		templetBodyVO.setItemkey(TBMPsndocVO.PK_PSNJOB+"."+PsnJobVO.PK_PSNDOC+"."+PsndocVO.CODE);
		templetBodyVO.setIdcolname(TBMPsndocVO.PK_PSNJOB);
		templetBodyVO.setMetadataproperty("hrhi.bd_psndoc.code");
		templetBodyVO.setMetadatapath(PsnJobVO.PK_PSNDOC+"."+PsndocVO.CODE);
		templetBodyVO.setShoworder(iShowOrder++);
		templetBodyVO.setWidth(width);
		arrListBodyVO.add(templetBodyVO);

		//姓名列
		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
		templetBodyVO.setNullflag(false);
		templetBodyVO.setListshowflag(true);
		templetBodyVO.setShowflag(true);
		templetBodyVO.setDatatype(IBillItem.MULTILANGTEXT);
		templetBodyVO.setDefaultshowname(null);
		templetBodyVO.setItemkey(TBMPsndocVO.PK_PSNJOB+"."+PsnJobVO.PK_PSNDOC+"."+PsndocVO.NAME);
		templetBodyVO.setIdcolname(TBMPsndocVO.PK_PSNJOB);
		templetBodyVO.setMetadataproperty("hrhi.bd_psndoc.name");
		templetBodyVO.setMetadatapath(PsnJobVO.PK_PSNDOC+"."+PsndocVO.NAME);
		templetBodyVO.setShoworder(iShowOrder++);
		templetBodyVO.setWidth(width);
		arrListBodyVO.add(templetBodyVO);

//		//组织列
//		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
//		templetBodyVO.setNullflag(false);
//		templetBodyVO.setListshowflag(true);
//		templetBodyVO.setShowflag(true);
//		templetBodyVO.setDatatype(IBillItem.UFREF);
//		templetBodyVO.setDefaultshowname(null);
//		templetBodyVO.setItemkey(TBMPsndocVO.PK_PSNJOB+"."+PsnJobVO.PK_ORG);
//		templetBodyVO.setIdcolname(TBMPsndocVO.PK_PSNJOB);
//		templetBodyVO.setMetadataproperty("hrhi.hi_psnjob.pk_org");
//		templetBodyVO.setMetadatapath(PsnJobVO.PK_ORG);
//		templetBodyVO.setShoworder(iShowOrder++);
//		templetBodyVO.setWidth(width);
//		arrListBodyVO.add(templetBodyVO);
		
		//组织版本列
		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
		templetBodyVO.setNullflag(false);
		templetBodyVO.setListshowflag(false);
		templetBodyVO.setShowflag(false);
		templetBodyVO.setDatatype(IBillItem.UFREF);
		templetBodyVO.setDefaultshowname(PublicLangRes.ORG());
		templetBodyVO.setItemkey(TBMPsndocVO.PK_ORG_V);
		templetBodyVO.setIdcolname(null);
		templetBodyVO.setMetadataproperty(metaData+"."+TBMPsndocVO.PK_ORG_V);
		templetBodyVO.setMetadatapath(TBMPsndocVO.PK_ORG_V);
		templetBodyVO.setShoworder(iShowOrder++);
		templetBodyVO.setWidth(width);
		arrListBodyVO.add(templetBodyVO);

//		//部门列
//		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
//		templetBodyVO.setNullflag(false);
//		templetBodyVO.setListshowflag(true);
//		templetBodyVO.setShowflag(true);
//		templetBodyVO.setDatatype(IBillItem.UFREF);
//		templetBodyVO.setDefaultshowname(null);
//		templetBodyVO.setItemkey(TBMPsndocVO.PK_PSNJOB+"."+PsnJobVO.PK_DEPT);
//		templetBodyVO.setIdcolname(TBMPsndocVO.PK_PSNJOB);
//		templetBodyVO.setMetadataproperty("hrhi.hi_psnjob.pk_dept");
//		templetBodyVO.setMetadatapath(PsnJobVO.PK_DEPT);
//		templetBodyVO.setShoworder(iShowOrder++);
//		templetBodyVO.setWidth(width);
//		arrListBodyVO.add(templetBodyVO);
		
		//部门版本列
		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
		templetBodyVO.setNullflag(false);
		templetBodyVO.setListshowflag(true);
		templetBodyVO.setShowflag(true);
		templetBodyVO.setDatatype(IBillItem.UFREF);
		templetBodyVO.setDefaultshowname(PublicLangRes.DEPT());
		templetBodyVO.setItemkey(TBMPsndocVO.PK_DEPT_V);
		templetBodyVO.setIdcolname(null);
		templetBodyVO.setMetadataproperty(metaData+"."+TBMPsndocVO.PK_DEPT_V);
		templetBodyVO.setMetadatapath(TBMPsndocVO.PK_DEPT_V);
		templetBodyVO.setShoworder(iShowOrder++);
		templetBodyVO.setWidth(width);
		arrListBodyVO.add(templetBodyVO);
		
		return arrListBodyVO;
	}
	
	public static BillTempletBodyVO createDefaultBillTempletBodyVO(int pos,int order){
		boolean isCard = pos==IBillItem.BODY;
		int width = pos==IBillItem.HEAD?1:100;
		BillTempletBodyVO templetBodyVO = new BillTempletBodyVO();
		templetBodyVO.setCardflag(isCard);
		templetBodyVO.setListshowflag(!isCard);
		templetBodyVO.setEditflag(false);
		templetBodyVO.setInputlength(200);
		templetBodyVO.setItemtype(0);
		templetBodyVO.setNullflag(true);
		templetBodyVO.setPos(pos);
		templetBodyVO.setShowflag(true);
		templetBodyVO.setShoworder(order);
		templetBodyVO.setWidth(width);
		templetBodyVO.setList(!isCard);
		templetBodyVO.setListflag(!isCard);
		return templetBodyVO;
	}
	
	public static  BillTempletVO createCardInfoTempletVO(boolean showPlace/*是否显示考勤地点的相关列*/){
		String metadataclass = "hrta.CardInfo";
		BillTempletVO btv = createBillTempletVO(metadataclass, "HRTA");
		

		List<BillTempletBodyVO> retList = new ArrayList<BillTempletBodyVO>();
		int order = 1000;
		
		//主键字段
		BillTempletBodyVO bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
		retList.add(bodyVO);
		bodyVO.setDatatype(IBillItem.STRING);
		bodyVO.setDefaultshowname(null);
		bodyVO.setListshowflag(Boolean.FALSE);
		bodyVO.setItemkey("pk_cardinfo");
		bodyVO.setMetadataproperty(metadataclass+".pk_cardinfo");
		bodyVO.setMetadatapath("pk_cardinfo");
		
		//员工考勤卡号
		bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
		retList.add(bodyVO);
		bodyVO.setDatatype(IBillItem.STRING);
		bodyVO.setNullflag(false);
		bodyVO.setDefaultshowname(null);
		bodyVO.setItemkey(ImportDataVO.TIMECARDID);
		bodyVO.setMetadataproperty(metadataclass+"."+ImportDataVO.TIMECARDID);
		bodyVO.setMetadatapath(ImportDataVO.TIMECARDID);
		
		//刷卡时间字段
		bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
		retList.add(bodyVO);
		bodyVO.setDatatype(IBillItem.STRING);
		bodyVO.setNullflag(false);
		bodyVO.setDefaultshowname(null);
		bodyVO.setItemkey("datetime");
		bodyVO.setWidth(2);
		bodyVO.setMetadataproperty(metadataclass+".datetime");
		bodyVO.setMetadatapath("datetime");
		
		//卡类型
		bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
		retList.add(bodyVO);
		bodyVO.setDatatype(IBillItem.COMBO);
		bodyVO.setNullflag(false);
		bodyVO.setDefaultshowname(null);
		bodyVO.setItemkey("timeflag");
		bodyVO.setMetadataproperty(metadataclass+".timeflag");
		bodyVO.setMetadatapath("timeflag");
		
		//刷卡状态字段
		bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
		retList.add(bodyVO);
		bodyVO.setDatatype(IBillItem.COMBO);
		bodyVO.setNullflag(false);
		bodyVO.setDefaultshowname(null);
		bodyVO.setItemkey("checkflag");
		bodyVO.setMetadataproperty(metadataclass+".checkflag");
		bodyVO.setMetadatapath("checkflag");

		bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
		retList.add(bodyVO);
		bodyVO.setDatatype(IBillItem.UFREF);
		bodyVO.setNullflag(false);
		bodyVO.setDefaultshowname(null);
		bodyVO.setItemkey("pk_machine");
		bodyVO.setMetadataproperty(metadataclass+".pk_machine");
		
		if(showPlace){
			//考勤地点字段
			bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
			retList.add(bodyVO);
			bodyVO.setDatatype(IBillItem.UFREF);
			bodyVO.setNullflag(false);
			bodyVO.setDefaultshowname(null);
			bodyVO.setItemkey(MachineVO.PK_PLACE);
			bodyVO.setMetadataproperty(metadataclass+"."+MachineVO.PK_PLACE);

			//地点异常
			bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
			retList.add(bodyVO);
			bodyVO.setDatatype(IBillItem.UFREF);
			bodyVO.setNullflag(false);
			bodyVO.setDefaultshowname(null);
			bodyVO.setItemkey("placeabnormal");
			bodyVO.setMetadataproperty(metadataclass+".placeabnormal");
			bodyVO.setMetadatapath("placeabnormal");
		}
		
		//签卡原因
		bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
		retList.add(bodyVO);
		bodyVO.setDatatype(IBillItem.STRING);
		bodyVO.setNullflag(false);
		bodyVO.setDefaultshowname(null);
		bodyVO.setItemkey(SignCommonVO.SIGNREASON);
		bodyVO.setMetadataproperty(metadataclass+"."+SignCommonVO.SIGNREASON);
		bodyVO.setMetadatapath(SignCommonVO.SIGNREASON);
		
		//处理人
		bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
		retList.add(bodyVO);
		bodyVO.setDatatype(IBillItem.UFREF);
		bodyVO.setNullflag(false);
		bodyVO.setDefaultshowname(null);
		bodyVO.setItemkey(SignRegVO.CREATOR);
		bodyVO.setMetadataproperty(metadataclass+"."+SignRegVO.CREATOR);
		bodyVO.setMetadatapath(SignRegVO.CREATOR);
		
		//处理时间
		bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
		retList.add(bodyVO);
		bodyVO.setDatatype(IBillItem.STRING);
		bodyVO.setWidth(2);
		bodyVO.setNullflag(false);
		bodyVO.setDefaultshowname(null);
		bodyVO.setItemkey(SignRegVO.CREATIONTIME);
		bodyVO.setMetadataproperty(metadataclass+"."+SignRegVO.CREATIONTIME);
		bodyVO.setMetadatapath(SignRegVO.CREATIONTIME);
		
		btv.setChildrenVO(retList.toArray(new BillTempletBodyVO[0]));
		return btv;
	}
}
