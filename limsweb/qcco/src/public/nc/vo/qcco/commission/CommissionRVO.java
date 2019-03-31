package nc.vo.qcco.commission;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此处简要描述此类功能 </b>
 * <p>
 * 此处添加累的描述信息
 * </p>
 * 创建日期:2019/3/25
 * 
 * @author yonyouBQ
 * @version NCPrj ??
 */

public class CommissionRVO extends SuperVO {

	/**
	 * 参数行主键
	 */
	public String pk_commission_r;
	/**
	 * 行号
	 */
	public String rowno;
	/**
	 * 实验前参数名称
	 */
	public String analysisname;
	/**
	 * 样品组别
	 */
	public String pk_samplegroup;
	/**
	 * 参数项
	 */
	public String pk_component;
	/**
	 * 值类型
	 */
	public String pk_valuetype;
	/**
	 * 最大值
	 */
	public String stdmaxvalue;
	/**
	 * 最小值
	 */
	public String stdminvalue;
	/**
	 * 单位
	 */
	public String unitname;
	/**
	 * 是否判定
	 */
	public UFBoolean judgeflag;
	/**
	 * 是否测试
	 */
	public UFBoolean testflag;
	/**
	 * 测试温度
	 */
	public String productstage;
	/**
	 * 上层单据主键
	 */
	public String pk_commission_b;
	/**
	 * 时间戳
	 */
	public UFDateTime ts;

	/**
	 * 属性 pk_commission_r的Getter方法.属性名：参数行主键 创建日期:2019/3/25
	 * 
	 * @return java.lang.String
	 */
	public String getPk_commission_r() {
		return this.pk_commission_r;
	}

	/**
	 * 属性pk_commission_r的Setter方法.属性名：参数行主键 创建日期:2019/3/25
	 * 
	 * @param newPk_commission_r
	 *            java.lang.String
	 */
	public void setPk_commission_r(String pk_commission_r) {
		this.pk_commission_r = pk_commission_r;
	}

	/**
	 * 属性 rowno的Getter方法.属性名：行号 创建日期:2019/3/25
	 * 
	 * @return java.lang.String
	 */
	public String getRowno() {
		return this.rowno;
	}

	/**
	 * 属性rowno的Setter方法.属性名：行号 创建日期:2019/3/25
	 * 
	 * @param newRowno
	 *            java.lang.String
	 */
	public void setRowno(String rowno) {
		this.rowno = rowno;
	}

	/**
	 * 属性 analysisname的Getter方法.属性名：实验前参数名称 创建日期:2019/3/25
	 * 
	 * @return java.lang.String
	 */
	public String getAnalysisname() {
		return this.analysisname;
	}

	/**
	 * 属性analysisname的Setter方法.属性名：实验前参数名称 创建日期:2019/3/25
	 * 
	 * @param newAnalysisname
	 *            java.lang.String
	 */
	public void setAnalysisname(String analysisname) {
		this.analysisname = analysisname;
	}

	/**
	 * 属性 pk_samplegroup的Getter方法.属性名：样品组别 创建日期:2019/3/25
	 * 
	 * @return nc.vo.bd.defdoc.DefdocVO
	 */
	public String getPk_samplegroup() {
		return this.pk_samplegroup;
	}

	/**
	 * 属性pk_samplegroup的Setter方法.属性名：样品组别 创建日期:2019/3/25
	 * 
	 * @param newPk_samplegroup
	 *            nc.vo.bd.defdoc.DefdocVO
	 */
	public void setPk_samplegroup(String pk_samplegroup) {
		this.pk_samplegroup = pk_samplegroup;
	}

	/**
	 * 属性 pk_component的Getter方法.属性名：参数项 创建日期:2019/3/25
	 * 
	 * @return nc.vo.bd.defdoc.DefdocVO
	 */
	public String getPk_component() {
		return this.pk_component;
	}

	/**
	 * 属性pk_component的Setter方法.属性名：参数项 创建日期:2019/3/25
	 * 
	 * @param newPk_component
	 *            nc.vo.bd.defdoc.DefdocVO
	 */
	public void setPk_component(String pk_component) {
		this.pk_component = pk_component;
	}

	/**
	 * 属性 pk_valuetype的Getter方法.属性名：值类型 创建日期:2019/3/25
	 * 
	 * @return nc.vo.bd.defdoc.DefdocVO
	 */
	public String getPk_valuetype() {
		return this.pk_valuetype;
	}

	/**
	 * 属性pk_valuetype的Setter方法.属性名：值类型 创建日期:2019/3/25
	 * 
	 * @param newPk_valuetype
	 *            nc.vo.bd.defdoc.DefdocVO
	 */
	public void setPk_valuetype(String pk_valuetype) {
		this.pk_valuetype = pk_valuetype;
	}

	/**
	 * 属性 stdmaxvalue的Getter方法.属性名：最大值 创建日期:2019/3/25
	 * 
	 * @return java.lang.String
	 */
	public String getStdmaxvalue() {
		return this.stdmaxvalue;
	}

	/**
	 * 属性stdmaxvalue的Setter方法.属性名：最大值 创建日期:2019/3/25
	 * 
	 * @param newStdmaxvalue
	 *            java.lang.String
	 */
	public void setStdmaxvalue(String stdmaxvalue) {
		this.stdmaxvalue = stdmaxvalue;
	}

	/**
	 * 属性 stdminvalue的Getter方法.属性名：最小值 创建日期:2019/3/25
	 * 
	 * @return java.lang.String
	 */
	public String getStdminvalue() {
		return this.stdminvalue;
	}

	/**
	 * 属性stdminvalue的Setter方法.属性名：最小值 创建日期:2019/3/25
	 * 
	 * @param newStdminvalue
	 *            java.lang.String
	 */
	public void setStdminvalue(String stdminvalue) {
		this.stdminvalue = stdminvalue;
	}

	/**
	 * 属性 unitname的Getter方法.属性名：单位 创建日期:2019/3/25
	 * 
	 * @return java.lang.String
	 */
	public String getUnitname() {
		return this.unitname;
	}

	/**
	 * 属性unitname的Setter方法.属性名：单位 创建日期:2019/3/25
	 * 
	 * @param newUnitname
	 *            java.lang.String
	 */
	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}

	/**
	 * 属性 judgeflag的Getter方法.属性名：是否判定 创建日期:2019/3/25
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public UFBoolean getJudgeflag() {
		return this.judgeflag;
	}

	/**
	 * 属性judgeflag的Setter方法.属性名：是否判定 创建日期:2019/3/25
	 * 
	 * @param newJudgeflag
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setJudgeflag(UFBoolean judgeflag) {
		this.judgeflag = judgeflag;
	}

	/**
	 * 属性 testflag的Getter方法.属性名：是否测试 创建日期:2019/3/25
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public UFBoolean getTestflag() {
		return this.testflag;
	}

	/**
	 * 属性testflag的Setter方法.属性名：是否测试 创建日期:2019/3/25
	 * 
	 * @param newTestflag
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setTestflag(UFBoolean testflag) {
		this.testflag = testflag;
	}

	/**
	 * 属性 productstage的Getter方法.属性名：测试温度 创建日期:2019/3/25
	 * 
	 * @return java.lang.String
	 */
	public String getProductstage() {
		return this.productstage;
	}

	/**
	 * 属性productstage的Setter方法.属性名：测试温度 创建日期:2019/3/25
	 * 
	 * @param newProductstage
	 *            java.lang.String
	 */
	public void setProductstage(String productstage) {
		this.productstage = productstage;
	}

	/**
	 * 属性 生成上层主键的Getter方法.属性名：上层主键 创建日期:2019/3/25
	 * 
	 * @return String
	 */
	public String getPk_commission_b() {
		return this.pk_commission_b;
	}

	/**
	 * 属性生成上层主键的Setter方法.属性名：上层主键 创建日期:2019/3/25
	 * 
	 * @param newPk_commission_b
	 *            String
	 */
	public void setPk_commission_b(String pk_commission_b) {
		this.pk_commission_b = pk_commission_b;
	}

	/**
	 * 属性 生成时间戳的Getter方法.属性名：时间戳 创建日期:2019/3/25
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * 属性生成时间戳的Setter方法.属性名：时间戳 创建日期:2019/3/25
	 * 
	 * @param newts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("qcco.commission_r");
	}

	public String getParentPKFieldName() {
		return "pk_commission_b";
	}
}
