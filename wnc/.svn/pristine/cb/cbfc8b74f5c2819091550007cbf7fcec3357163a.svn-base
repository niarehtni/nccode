package nc.impl.wa.payroll;

import java.util.ArrayList;
import java.util.HashMap;

import nc.bs.dao.DAOException;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.ResHelper;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pub.encryption.util.SalaryEncryptionUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.util.AuditInfoUtil;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.payroll.AggPayrollVO;
import nc.vo.wa.payroll.PayrollVO;

public class PayrollDAO extends BaseDAOManager {

	/**
	 * 校验编码是否重复
	 * 
	 * @param vo
	 *            PayrollVO
	 * @throws BusinessException
	 */
	public void checkPayrollCode(AggPayrollVO aggVO) throws BusinessException {
		PayrollVO vo = (PayrollVO) aggVO.getParentVO();
		String sql = " select 1 from wa_payroll where pk_group = ? and pk_org = ? and billcode = ? ";
		if (vo.getPk_payroll() != null) {
			sql += " and pk_payroll <> '" + vo.getPk_payroll() + "'";
		}
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(vo.getPk_group());
		parameter.addParam(vo.getPk_org());
		parameter.addParam(vo.getBillcode());
		if (isValueExist(sql, parameter)) {
			throw new BusinessException(ResHelper.getString("60130adjapprove",
					"060130adjapprove0118", vo.getBillcode())/*
															 * @res
															 * "下列字段值已存在，不允许重复，请检查：\n[申请单编码：{0}]"
															 */);
			// throw new
			// BusinessException(ResHelper.getString("60130payslipaly","060130payslipaly0550")/*@res
			// "申请单编码重复，请重新输入！"*/);
		}
	}

	/**
	 * 校验方案是否重复
	 * 
	 * @param vo
	 *            PayrollVO
	 * @throws BusinessException
	 */
	public void checkPayrollClass(AggPayrollVO aggVO) throws BusinessException {
		PayrollVO vo = (PayrollVO) aggVO.getParentVO();
		String sql = " select pk_wa_class from wa_payroll where pk_group = ? and pk_org = ? and pk_wa_class = ? and cyear = ? and cperiod = ? and billstate!="
				+ IPfRetCheckInfo.NOPASS;
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(vo.getPk_group());
		parameter.addParam(vo.getPk_org());
		parameter.addParam(vo.getPk_wa_class());
		parameter.addParam(vo.getCyear());
		parameter.addParam(vo.getCperiod());
		if (isValueExist(sql, parameter)) {
			throw new BusinessException(ResHelper.getString("60130adjapprove",
					"060130adjapprove0168")/* @res "该方案已经生成申请单，请勿重复申请！" */);
		}
		sql = " select pk_wa_class from wa_waclass where pk_group = ? and pk_org = ? and pk_wa_class = ? and cyear = ? and cperiod = ? "
				+ "and exists (select 1 from wa_periodstate,wa_period "
				+ "where wa_periodstate.pk_wa_period = wa_period.pk_wa_period "
				+ "and wa_waclass.pk_wa_class = wa_periodstate.pk_wa_class "
				+ "and wa_waclass.cyear = wa_period.cyear "
				+ "and wa_waclass.cperiod = wa_period.cperiod "
				+ "and wa_periodstate.checkflag = 'Y') ";
		if (!isValueExist(sql, parameter)) {
			throw new BusinessException(ResHelper.getString("60130paydata",
					"060130paydata0457")/* @res "选择的薪资方案状态发生变化, 请刷新后再试！" */);
			// 选择的薪资类别状态发生变化,请刷新后再试！
		}
	}

	public void updatePayroll(PayrollVO vo) throws DAOException {
		String sql = "update wa_payroll set approvedate = ?, billstate = ?, approve_note = ?, approver = ? , modifier =?, modifiedtime=? where pk_payroll = ?";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(vo.getApprovedate());
		parameter.addParam(vo.getBillstate());
		parameter.addParam(vo.getApprove_note());
		parameter.addParam(vo.getApprover());
		// 设置审计信息
		parameter.addParam(AuditInfoUtil.getCurrentUser());
		parameter.addParam(AuditInfoUtil.getCurrentTime());
		parameter.addParam(vo.getPk_payroll());

		getBaseDao().executeUpdate(sql, parameter);

	}

	public PayrollVO getPayroll(String pk_wa_class, String cyear, String cperiod)
			throws DAOException {
		String sql = "select wa_payroll.* from wa_payroll where pk_wa_class = ?  and cyear = ? and cperiod = ? and billstate != ?";

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_wa_class);
		parameter.addParam(cyear);
		parameter.addParam(cperiod);
		parameter.addParam(IPfRetCheckInfo.NOPASS);

		return executeQueryVO(sql, parameter, PayrollVO.class);
	}

	/**
	 * 查询申请单的应发合计和补发合计
	 * 
	 * @author liangxr on 2010-6-18
	 * @param mainvo
	 * @return vo:yf,sf
	 */
	public PayrollVO sumYf(PayrollVO mainvo) throws DAOException {

		// 2016-12-12 zhousze 薪资加密：这里处理发放申请查询合计数据时，先更新数据库数据，数据解密 begin
		StringBuffer sql1 = new StringBuffer();
		sql1.append("select * from wa_data where pk_wa_class= '"
				+ mainvo.getPk_wa_class() + "' and cyear='" + mainvo.getCyear()
				+ "' and cperiod = '" + mainvo.getCperiod()
				+ "' and stopflag = 'N'");
		ArrayList<DataVO> dataVOs = (ArrayList<DataVO>) getBaseDao()
				.executeQuery(sql1.toString(),
						new BeanListProcessor(DataVO.class));
		DataVO[] vos = new DataVO[dataVOs.size()];
		for (int i = 0; i < dataVOs.size(); i++) {
			vos[i] = dataVOs.get(i);
		}
		vos = SalaryDecryptUtil.decrypt4Array(vos);
		getBaseDao().updateVOArray(vos);
		// end

		String sql = "select sum(f_1) as yf,sum(f_3) as sf from wa_data where pk_wa_class= ? and cyear=? and cperiod = ? and stopflag = 'N' ";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(mainvo.getPk_wa_class());
		parameter.addParam(mainvo.getCyear());
		parameter.addParam(mainvo.getCperiod());
		PayrollVO vo = executeQueryVO(sql, parameter, PayrollVO.class);

		// 2016-12-12 zhousze 薪资加密：这里处理发放申请查询完合计值后，重新数据加密到数据库中 begin
		vos = SalaryEncryptionUtil.encryption4Array(vos);
		getBaseDao().updateVOArray(vos);
		// end

		return vo;
	}

	/**
	 * 查询申请单的应发合计和补发合计
	 * 
	 * @author liangxr on 2010-6-18
	 * @param mainvo
	 * @return vo:yf,sf
	 */
	public HashMap<String, PayrollVO> batchsumYf(String tablename,
			PayrollVO[] vos) throws DAOException {
		StringBuffer sql = new StringBuffer();
		sql.append("select sum(f_1) as yf,sum(f_3) as sf, wa_data.pk_wa_class,wa_data.cyear,wa_data.cperiod from wa_data,");
		sql.append(tablename);
		sql.append(" where wa_data.pk_wa_class= ");
		sql.append(tablename);
		sql.append(".pk_wa_class and wa_data.cyear = ");
		sql.append(tablename);
		sql.append(".cyear and wa_data.cperiod = ");
		sql.append(tablename);
		sql.append(".cperiod and wa_data.stopflag = 'N' group by wa_data.pk_wa_class,wa_data.cyear,wa_data.cperiod");
		PayrollVO[] tvos = executeQueryVOs(sql.toString(), PayrollVO.class);
		HashMap<String, PayrollVO> map = new HashMap<String, PayrollVO>();
		for (PayrollVO vo : tvos) {
			String key = vo.getPk_wa_class() + vo.getCyear() + vo.getCperiod();
			map.put(key, vo);
		}
		return map;
	}

	/**
	 * 判断申请单关联的方案是否已发放
	 * 
	 * @author liangxr on 2010-7-5
	 * @param mainvo
	 * @return
	 * @throws DAOException
	 */
	public boolean isPayed(PayrollVO mainvo) throws DAOException {
		String sql = "select 1 from wa_periodstate,wa_period "
				+ "where wa_periodstate.pk_wa_period = wa_period.pk_wa_period "
				+ "and wa_periodstate.pk_wa_class = ? and wa_period.cyear = ? "
				+ "and wa_period.cperiod=? and wa_periodstate.payoffflag='Y'";

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(mainvo.getPk_wa_class());
		parameter.addParam(mainvo.getCyear());
		parameter.addParam(mainvo.getCperiod());

		return isValueExist(sql, parameter);
	}

}