package nc.vo.wa.allocate;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

@SuppressWarnings("serial")
public class AllocateOutHVO extends SuperVO {
	/**
	 * 审批人
	 */
	public String approver;
	/**
	 * 制单人
	 */
	public String billmaker;
	/**
	 * 单据类型
	 */
	public String cbilltypeid;
	/**
	 * 业务流程
	 */
	public String cbiztypeid;
	/**
	 * 薪资期间
	 */
	public String cperiod;
	/**
	 * 创建时间
	 */
	public UFDateTime creationtime;
	/**
	 * 创建人
	 */
	public String creator;
	/**
	 * 交易类型
	 */
	public String ctrantypeid;
	/**
	 * 单据日期
	 */
	public UFDate dbilldate;
	/**
	 * 自定义项1
	 */
	public String def1;
	/**
	 * 自定义项10
	 */
	public String def10;
	/**
	 * 自定义项11
	 */
	public String def11;
	/**
	 * 自定义项12
	 */
	public String def12;
	/**
	 * 自定义项13
	 */
	public String def13;
	/**
	 * 自定义项14
	 */
	public String def14;
	/**
	 * 自定义项15
	 */
	public String def15;
	/**
	 * 自定义项16
	 */
	public String def16;
	/**
	 * 自定义项17
	 */
	public String def17;
	/**
	 * 自定义项18
	 */
	public String def18;
	/**
	 * 自定义项19
	 */
	public String def19;
	/**
	 * 自定义项2
	 */
	public String def2;
	/**
	 * 自定义项20
	 */
	public String def20;
	/**
	 * 自定义项3
	 */
	public String def3;
	/**
	 * 自定义项4
	 */
	public String def4;
	/**
	 * 自定义项5
	 */
	public String def5;
	/**
	 * 自定义项6
	 */
	public String def6;
	/**
	 * 自定义项7
	 */
	public String def7;
	/**
	 * 自定义项8
	 */
	public String def8;
	/**
	 * 自定义项9
	 */
	public String def9;
	/**
	 * 制单日期
	 */
	public UFDate dmakedate;
	/**
	 * 单据状态
	 */
	public Integer fstatusflag;
	/**
	 * 修改时间
	 */
	public UFDateTime modifiedtime;
	/**
	 * 修改人
	 */
	public String modifier;
	/**
	 * 主键
	 */
	public String pk_allocate;
	/**
	 * 薪资项目
	 */
	public String pk_classitem;
	/**
	 * 成本中心
	 */
	public String pk_costcenter;
	/**
	 * 集团
	 */
	public String pk_group;
	/**
	 * 人力资源组织
	 */
	public String pk_org;
	/**
	 * 人力资源组织版本
	 */
	public String pk_org_v;
	/**
	 * 专案代码
	 */
	public String pk_project;
	/**
	 * 员工
	 */
	public String pk_psndoc;
	/**
	 * 任职记录
	 */
	public String pk_psnjob;
	/**
	 * 薪资方案
	 */
	public String pk_wa_calss;
	/**
	 * 审核日期
	 */
	public UFDateTime taudittime;
	/**
	 * 时间戳
	 */
	public UFDateTime ts;
	/**
	 * 审批批语
	 */
	public String vapprovenote;
	/**
	 * 单据号
	 */
	public String vbillcode;
	/**
	 * 交易类型编码
	 */
	public String vtrantypecode;

	/**
	 * 获取审批人
	 * 
	 * @return 审批人
	 */
	public String getApprover() {
		return this.approver;
	}

	/**
	 * 设置审批人
	 * 
	 * @param approver
	 *            审批人
	 */
	public void setApprover(String approver) {
		this.approver = approver;
	}

	/**
	 * 获取制单人
	 * 
	 * @return 制单人
	 */
	public String getBillmaker() {
		return this.billmaker;
	}

	/**
	 * 设置制单人
	 * 
	 * @param billmaker
	 *            制单人
	 */
	public void setBillmaker(String billmaker) {
		this.billmaker = billmaker;
	}

	/**
	 * 获取单据类型
	 * 
	 * @return 单据类型
	 */
	public String getCbilltypeid() {
		return this.cbilltypeid;
	}

	/**
	 * 设置单据类型
	 * 
	 * @param cbilltypeid
	 *            单据类型
	 */
	public void setCbilltypeid(String cbilltypeid) {
		this.cbilltypeid = cbilltypeid;
	}

	/**
	 * 获取业务流程
	 * 
	 * @return 业务流程
	 */
	public String getCbiztypeid() {
		return this.cbiztypeid;
	}

	/**
	 * 设置业务流程
	 * 
	 * @param cbiztypeid
	 *            业务流程
	 */
	public void setCbiztypeid(String cbiztypeid) {
		this.cbiztypeid = cbiztypeid;
	}

	/**
	 * 获取薪资期间
	 * 
	 * @return 薪资期间
	 */
	public String getCperiod() {
		return this.cperiod;
	}

	/**
	 * 设置薪资期间
	 * 
	 * @param cperiod
	 *            薪资期间
	 */
	public void setCperiod(String cperiod) {
		this.cperiod = cperiod;
	}

	/**
	 * 获取创建时间
	 * 
	 * @return 创建时间
	 */
	public UFDateTime getCreationtime() {
		return this.creationtime;
	}

	/**
	 * 设置创建时间
	 * 
	 * @param creationtime
	 *            创建时间
	 */
	public void setCreationtime(UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	/**
	 * 获取创建人
	 * 
	 * @return 创建人
	 */
	public String getCreator() {
		return this.creator;
	}

	/**
	 * 设置创建人
	 * 
	 * @param creator
	 *            创建人
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * 获取交易类型
	 * 
	 * @return 交易类型
	 */
	public String getCtrantypeid() {
		return this.ctrantypeid;
	}

	/**
	 * 设置交易类型
	 * 
	 * @param ctrantypeid
	 *            交易类型
	 */
	public void setCtrantypeid(String ctrantypeid) {
		this.ctrantypeid = ctrantypeid;
	}

	/**
	 * 获取单据日期
	 * 
	 * @return 单据日期
	 */
	public UFDate getDbilldate() {
		return this.dbilldate;
	}

	/**
	 * 设置单据日期
	 * 
	 * @param dbilldate
	 *            单据日期
	 */
	public void setDbilldate(UFDate dbilldate) {
		this.dbilldate = dbilldate;
	}

	/**
	 * 获取自定义项1
	 * 
	 * @return 自定义项1
	 */
	public String getDef1() {
		return this.def1;
	}

	/**
	 * 设置自定义项1
	 * 
	 * @param def1
	 *            自定义项1
	 */
	public void setDef1(String def1) {
		this.def1 = def1;
	}

	/**
	 * 获取自定义项10
	 * 
	 * @return 自定义项10
	 */
	public String getDef10() {
		return this.def10;
	}

	/**
	 * 设置自定义项10
	 * 
	 * @param def10
	 *            自定义项10
	 */
	public void setDef10(String def10) {
		this.def10 = def10;
	}

	/**
	 * 获取自定义项11
	 * 
	 * @return 自定义项11
	 */
	public String getDef11() {
		return this.def11;
	}

	/**
	 * 设置自定义项11
	 * 
	 * @param def11
	 *            自定义项11
	 */
	public void setDef11(String def11) {
		this.def11 = def11;
	}

	/**
	 * 获取自定义项12
	 * 
	 * @return 自定义项12
	 */
	public String getDef12() {
		return this.def12;
	}

	/**
	 * 设置自定义项12
	 * 
	 * @param def12
	 *            自定义项12
	 */
	public void setDef12(String def12) {
		this.def12 = def12;
	}

	/**
	 * 获取自定义项13
	 * 
	 * @return 自定义项13
	 */
	public String getDef13() {
		return this.def13;
	}

	/**
	 * 设置自定义项13
	 * 
	 * @param def13
	 *            自定义项13
	 */
	public void setDef13(String def13) {
		this.def13 = def13;
	}

	/**
	 * 获取自定义项14
	 * 
	 * @return 自定义项14
	 */
	public String getDef14() {
		return this.def14;
	}

	/**
	 * 设置自定义项14
	 * 
	 * @param def14
	 *            自定义项14
	 */
	public void setDef14(String def14) {
		this.def14 = def14;
	}

	/**
	 * 获取自定义项15
	 * 
	 * @return 自定义项15
	 */
	public String getDef15() {
		return this.def15;
	}

	/**
	 * 设置自定义项15
	 * 
	 * @param def15
	 *            自定义项15
	 */
	public void setDef15(String def15) {
		this.def15 = def15;
	}

	/**
	 * 获取自定义项16
	 * 
	 * @return 自定义项16
	 */
	public String getDef16() {
		return this.def16;
	}

	/**
	 * 设置自定义项16
	 * 
	 * @param def16
	 *            自定义项16
	 */
	public void setDef16(String def16) {
		this.def16 = def16;
	}

	/**
	 * 获取自定义项17
	 * 
	 * @return 自定义项17
	 */
	public String getDef17() {
		return this.def17;
	}

	/**
	 * 设置自定义项17
	 * 
	 * @param def17
	 *            自定义项17
	 */
	public void setDef17(String def17) {
		this.def17 = def17;
	}

	/**
	 * 获取自定义项18
	 * 
	 * @return 自定义项18
	 */
	public String getDef18() {
		return this.def18;
	}

	/**
	 * 设置自定义项18
	 * 
	 * @param def18
	 *            自定义项18
	 */
	public void setDef18(String def18) {
		this.def18 = def18;
	}

	/**
	 * 获取自定义项19
	 * 
	 * @return 自定义项19
	 */
	public String getDef19() {
		return this.def19;
	}

	/**
	 * 设置自定义项19
	 * 
	 * @param def19
	 *            自定义项19
	 */
	public void setDef19(String def19) {
		this.def19 = def19;
	}

	/**
	 * 获取自定义项2
	 * 
	 * @return 自定义项2
	 */
	public String getDef2() {
		return this.def2;
	}

	/**
	 * 设置自定义项2
	 * 
	 * @param def2
	 *            自定义项2
	 */
	public void setDef2(String def2) {
		this.def2 = def2;
	}

	/**
	 * 获取自定义项20
	 * 
	 * @return 自定义项20
	 */
	public String getDef20() {
		return this.def20;
	}

	/**
	 * 设置自定义项20
	 * 
	 * @param def20
	 *            自定义项20
	 */
	public void setDef20(String def20) {
		this.def20 = def20;
	}

	/**
	 * 获取自定义项3
	 * 
	 * @return 自定义项3
	 */
	public String getDef3() {
		return this.def3;
	}

	/**
	 * 设置自定义项3
	 * 
	 * @param def3
	 *            自定义项3
	 */
	public void setDef3(String def3) {
		this.def3 = def3;
	}

	/**
	 * 获取自定义项4
	 * 
	 * @return 自定义项4
	 */
	public String getDef4() {
		return this.def4;
	}

	/**
	 * 设置自定义项4
	 * 
	 * @param def4
	 *            自定义项4
	 */
	public void setDef4(String def4) {
		this.def4 = def4;
	}

	/**
	 * 获取自定义项5
	 * 
	 * @return 自定义项5
	 */
	public String getDef5() {
		return this.def5;
	}

	/**
	 * 设置自定义项5
	 * 
	 * @param def5
	 *            自定义项5
	 */
	public void setDef5(String def5) {
		this.def5 = def5;
	}

	/**
	 * 获取自定义项6
	 * 
	 * @return 自定义项6
	 */
	public String getDef6() {
		return this.def6;
	}

	/**
	 * 设置自定义项6
	 * 
	 * @param def6
	 *            自定义项6
	 */
	public void setDef6(String def6) {
		this.def6 = def6;
	}

	/**
	 * 获取自定义项7
	 * 
	 * @return 自定义项7
	 */
	public String getDef7() {
		return this.def7;
	}

	/**
	 * 设置自定义项7
	 * 
	 * @param def7
	 *            自定义项7
	 */
	public void setDef7(String def7) {
		this.def7 = def7;
	}

	/**
	 * 获取自定义项8
	 * 
	 * @return 自定义项8
	 */
	public String getDef8() {
		return this.def8;
	}

	/**
	 * 设置自定义项8
	 * 
	 * @param def8
	 *            自定义项8
	 */
	public void setDef8(String def8) {
		this.def8 = def8;
	}

	/**
	 * 获取自定义项9
	 * 
	 * @return 自定义项9
	 */
	public String getDef9() {
		return this.def9;
	}

	/**
	 * 设置自定义项9
	 * 
	 * @param def9
	 *            自定义项9
	 */
	public void setDef9(String def9) {
		this.def9 = def9;
	}

	/**
	 * 获取制单日期
	 * 
	 * @return 制单日期
	 */
	public UFDate getDmakedate() {
		return this.dmakedate;
	}

	/**
	 * 设置制单日期
	 * 
	 * @param dmakedate
	 *            制单日期
	 */
	public void setDmakedate(UFDate dmakedate) {
		this.dmakedate = dmakedate;
	}

	/**
	 * 获取单据状态
	 * 
	 * @return 单据状态
	 * @see String
	 */
	public Integer getFstatusflag() {
		return this.fstatusflag;
	}

	/**
	 * 设置单据状态
	 * 
	 * @param fstatusflag
	 *            单据状态
	 * @see String
	 */
	public void setFstatusflag(Integer fstatusflag) {
		this.fstatusflag = fstatusflag;
	}

	/**
	 * 获取修改时间
	 * 
	 * @return 修改时间
	 */
	public UFDateTime getModifiedtime() {
		return this.modifiedtime;
	}

	/**
	 * 设置修改时间
	 * 
	 * @param modifiedtime
	 *            修改时间
	 */
	public void setModifiedtime(UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	/**
	 * 获取修改人
	 * 
	 * @return 修改人
	 */
	public String getModifier() {
		return this.modifier;
	}

	/**
	 * 设置修改人
	 * 
	 * @param modifier
	 *            修改人
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	/**
	 * 获取主键
	 * 
	 * @return 主键
	 */
	public String getPk_allocate() {
		return this.pk_allocate;
	}

	/**
	 * 设置主键
	 * 
	 * @param pk_allocate
	 *            主键
	 */
	public void setPk_allocate(String pk_allocate) {
		this.pk_allocate = pk_allocate;
	}

	/**
	 * 获取薪资项目
	 * 
	 * @return 薪资项目
	 */
	public String getPk_classitem() {
		return this.pk_classitem;
	}

	/**
	 * 设置薪资项目
	 * 
	 * @param pk_classitem
	 *            薪资项目
	 */
	public void setPk_classitem(String pk_classitem) {
		this.pk_classitem = pk_classitem;
	}

	/**
	 * 获取成本中心
	 * 
	 * @return 成本中心
	 */
	public String getPk_costcenter() {
		return this.pk_costcenter;
	}

	/**
	 * 设置成本中心
	 * 
	 * @param pk_costcenter
	 *            成本中心
	 */
	public void setPk_costcenter(String pk_costcenter) {
		this.pk_costcenter = pk_costcenter;
	}

	/**
	 * 获取集团
	 * 
	 * @return 集团
	 */
	public String getPk_group() {
		return this.pk_group;
	}

	/**
	 * 设置集团
	 * 
	 * @param pk_group
	 *            集团
	 */
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	/**
	 * 获取人力资源组织
	 * 
	 * @return 人力资源组织
	 */
	public String getPk_org() {
		return this.pk_org;
	}

	/**
	 * 设置人力资源组织
	 * 
	 * @param pk_org
	 *            人力资源组织
	 */
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	/**
	 * 获取人力资源组织版本
	 * 
	 * @return 人力资源组织版本
	 */
	public String getPk_org_v() {
		return this.pk_org_v;
	}

	/**
	 * 设置人力资源组织版本
	 * 
	 * @param pk_org_v
	 *            人力资源组织版本
	 */
	public void setPk_org_v(String pk_org_v) {
		this.pk_org_v = pk_org_v;
	}

	/**
	 * 获取专案代码
	 * 
	 * @return 专案代码
	 */
	public String getPk_project() {
		return this.pk_project;
	}

	/**
	 * 设置专案代码
	 * 
	 * @param pk_project
	 *            专案代码
	 */
	public void setPk_project(String pk_project) {
		this.pk_project = pk_project;
	}

	/**
	 * 获取员工
	 * 
	 * @return 员工
	 */
	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	/**
	 * 设置员工
	 * 
	 * @param pk_psndoc
	 *            员工
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * 获取任职记录
	 * 
	 * @return 任职记录
	 */
	public String getPk_psnjob() {
		return this.pk_psnjob;
	}

	/**
	 * 设置任职记录
	 * 
	 * @param pk_psnjob
	 *            任职记录
	 */
	public void setPk_psnjob(String pk_psnjob) {
		this.pk_psnjob = pk_psnjob;
	}

	/**
	 * 获取薪资方案
	 * 
	 * @return 薪资方案
	 */
	public String getPk_wa_calss() {
		return this.pk_wa_calss;
	}

	/**
	 * 设置薪资方案
	 * 
	 * @param pk_wa_calss
	 *            薪资方案
	 */
	public void setPk_wa_calss(String pk_wa_calss) {
		this.pk_wa_calss = pk_wa_calss;
	}

	/**
	 * 获取审核日期
	 * 
	 * @return 审核日期
	 */
	public UFDateTime getTaudittime() {
		return this.taudittime;
	}

	/**
	 * 设置审核日期
	 * 
	 * @param taudittime
	 *            审核日期
	 */
	public void setTaudittime(UFDateTime taudittime) {
		this.taudittime = taudittime;
	}

	/**
	 * 获取时间戳
	 * 
	 * @return 时间戳
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * 设置时间戳
	 * 
	 * @param ts
	 *            时间戳
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	/**
	 * 获取审批批语
	 * 
	 * @return 审批批语
	 */
	public String getVapprovenote() {
		return this.vapprovenote;
	}

	/**
	 * 设置审批批语
	 * 
	 * @param vapprovenote
	 *            审批批语
	 */
	public void setVapprovenote(String vapprovenote) {
		this.vapprovenote = vapprovenote;
	}

	/**
	 * 获取单据号
	 * 
	 * @return 单据号
	 */
	public String getVbillcode() {
		return this.vbillcode;
	}

	/**
	 * 设置单据号
	 * 
	 * @param vbillcode
	 *            单据号
	 */
	public void setVbillcode(String vbillcode) {
		this.vbillcode = vbillcode;
	}

	/**
	 * 获取交易类型编码
	 * 
	 * @return 交易类型编码
	 */
	public String getVtrantypecode() {
		return this.vtrantypecode;
	}

	/**
	 * 设置交易类型编码
	 * 
	 * @param vtrantypecode
	 *            交易类型编码
	 */
	public void setVtrantypecode(String vtrantypecode) {
		this.vtrantypecode = vtrantypecode;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("hrwa.allocate");
	}
}