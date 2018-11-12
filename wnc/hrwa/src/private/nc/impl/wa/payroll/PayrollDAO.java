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
	 * У������Ƿ��ظ�
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
															 * "�����ֶ�ֵ�Ѵ��ڣ��������ظ������飺\n[���뵥���룺{0}]"
															 */);
			// throw new
			// BusinessException(ResHelper.getString("60130payslipaly","060130payslipaly0550")/*@res
			// "���뵥�����ظ������������룡"*/);
		}
	}

	/**
	 * У�鷽���Ƿ��ظ�
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
					"060130adjapprove0168")/* @res "�÷����Ѿ��������뵥�������ظ����룡" */);
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
					"060130paydata0457")/* @res "ѡ���н�ʷ���״̬�����仯, ��ˢ�º����ԣ�" */);
			// ѡ���н�����״̬�����仯,��ˢ�º����ԣ�
		}
	}

	public void updatePayroll(PayrollVO vo) throws DAOException {
		String sql = "update wa_payroll set approvedate = ?, billstate = ?, approve_note = ?, approver = ? , modifier =?, modifiedtime=? where pk_payroll = ?";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(vo.getApprovedate());
		parameter.addParam(vo.getBillstate());
		parameter.addParam(vo.getApprove_note());
		parameter.addParam(vo.getApprover());
		// ���������Ϣ
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
	 * ��ѯ���뵥��Ӧ���ϼƺͲ����ϼ�
	 * 
	 * @author liangxr on 2010-6-18
	 * @param mainvo
	 * @return vo:yf,sf
	 */
	public PayrollVO sumYf(PayrollVO mainvo) throws DAOException {

		// 2016-12-12 zhousze н�ʼ��ܣ����ﴦ���������ѯ�ϼ�����ʱ���ȸ������ݿ����ݣ����ݽ��� begin
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

		// 2016-12-12 zhousze н�ʼ��ܣ����ﴦ���������ѯ��ϼ�ֵ���������ݼ��ܵ����ݿ��� begin
		vos = SalaryEncryptionUtil.encryption4Array(vos);
		getBaseDao().updateVOArray(vos);
		// end

		return vo;
	}

	/**
	 * ��ѯ���뵥��Ӧ���ϼƺͲ����ϼ�
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
	 * �ж����뵥�����ķ����Ƿ��ѷ���
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