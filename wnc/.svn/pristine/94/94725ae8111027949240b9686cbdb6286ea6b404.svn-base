package nc.impl.pub.ace;

import nc.bs.hrwa.sumincometax.ace.bp.AceSumincometaxInsertBP;
import nc.bs.hrwa.sumincometax.ace.bp.AceSumincometaxUpdateBP;
import nc.bs.hrwa.sumincometax.ace.bp.AceSumincometaxDeleteBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pub.wa.salaryencryption.util.SalaryEncryptionUtil;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;
import nc.vo.hrwa.sumincometax.CIncomeTaxVO;

public abstract class AceSumincometaxPubServiceImpl {
	// 新增
	public AggSumIncomeTaxVO[] pubinsertBills(AggSumIncomeTaxVO[] vos)
			throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggSumIncomeTaxVO> transferTool = new BillTransferTool<AggSumIncomeTaxVO>(
					vos);
			AggSumIncomeTaxVO[] mergedVO = transferTool.getClientFullInfoBill();

			// 调用BP
			AceSumincometaxInsertBP action = new AceSumincometaxInsertBP();
			//Ares.Tank 2018-8-20 15:08:56 数据加密
			salaryEncrypt(mergedVO);
			AggSumIncomeTaxVO[] retvos = action.insert(mergedVO);
			//Ares.Tank 2018-8-20 15:09:19 数据解密
			salaryDecrypt(retvos);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggSumIncomeTaxVO[] vos)
			throws BusinessException {
		try {
			// 加锁 比较ts
			BillTransferTool<AggSumIncomeTaxVO> transferTool = new BillTransferTool<AggSumIncomeTaxVO>(
					vos);
			AggSumIncomeTaxVO[] fullBills = transferTool
					.getClientFullInfoBill();
			AceSumincometaxDeleteBP deleteBP = new AceSumincometaxDeleteBP();
			//Ares.Tank 2018-8-20 15:08:56 数据加密
			salaryEncrypt(fullBills);
			deleteBP.delete(fullBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggSumIncomeTaxVO[] pubupdateBills(AggSumIncomeTaxVO[] vos)
			throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggSumIncomeTaxVO> transTool = new BillTransferTool<AggSumIncomeTaxVO>(
					vos);
			// 补全前台VO
			AggSumIncomeTaxVO[] fullBills = transTool.getClientFullInfoBill();
			// 获得修改前vo
			AggSumIncomeTaxVO[] originBills = transTool.getOriginBills();
			// 调用BP
			AceSumincometaxUpdateBP bp = new AceSumincometaxUpdateBP();
			//Ares.Tank 2018-8-20 15:08:56 数据加密
			salaryEncrypt(fullBills);
			AggSumIncomeTaxVO[] retBills = bp.update(fullBills, originBills);
			//Ares.Tank 2018-8-20 15:09:19 数据解密
			salaryDecrypt(retBills);
			// 构造返回数据
			retBills = transTool.getBillForToClient(retBills);
			return retBills;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggSumIncomeTaxVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggSumIncomeTaxVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggSumIncomeTaxVO> query = new BillLazyQuery<AggSumIncomeTaxVO>(
					AggSumIncomeTaxVO.class);
			bills = query.query(queryScheme, null);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		// start Ares.tank 2018-8-9 16:32:50 对加密数据进行解密
		salaryDecrypt(bills);
		// end Ares.Tank 2018-8-9 16:33:02 对加密数据进行解密
		return bills;
	}

	/**
	 * 对数据进行解密
	 * 
	 * @author Ares.Tank
	 * @param bills
	 * @return
	 */
	private AggSumIncomeTaxVO[] salaryDecrypt(AggSumIncomeTaxVO[] bills) {
		if (bills == null || bills.length == 0) {
			return bills;
		}
		for (AggSumIncomeTaxVO vo : bills) {
			if (vo != null) {
				if (vo.getParentVO() != null) {
					if (vo.getParentVO().getTaxbase() != null) {
						vo.getParentVO().setTaxbase(
								new UFDouble(SalaryDecryptUtil
										.decrypt(vo.getParentVO().getTaxbase()
												.getDouble())));
					}

					if (vo.getParentVO().getTaxbaseadjust() != null) {
						vo.getParentVO().setTaxbaseadjust(
								new UFDouble(SalaryDecryptUtil.decrypt(vo
										.getParentVO().getTaxbaseadjust()
										.getDouble())));
					}

					if (vo.getParentVO().getCacu_value() != null) {
						vo.getParentVO().setCacu_value(
								new UFDouble(SalaryDecryptUtil.decrypt(vo
										.getParentVO().getCacu_value()
										.getDouble())));
					}

					if (vo.getParentVO().getCacu_valueadjust() != null) {
						vo.getParentVO().setCacu_valueadjust(
								new UFDouble(SalaryDecryptUtil.decrypt(vo
										.getParentVO().getCacu_valueadjust()
										.getDouble())));
					}

					if (vo.getParentVO().getNetincome() != null) {
						vo.getParentVO().setNetincome(
								new UFDouble(SalaryDecryptUtil.decrypt(vo
										.getParentVO().getNetincome()
										.getDouble())));
					}

					if (vo.getParentVO().getPickedup() != null) {
						vo.getParentVO().setPickedup(
								new UFDouble(SalaryDecryptUtil.decrypt(vo
										.getParentVO().getPickedup()
										.getDouble())));
					}

					if (vo.getParentVO().getPickedupadjust() != null) {
						vo.getParentVO().setPickedupadjust(
								new UFDouble(SalaryDecryptUtil.decrypt(vo
										.getParentVO().getPickedupadjust()
										.getDouble())));
					}
				}

				CIncomeTaxVO[] chVOs = (CIncomeTaxVO[]) vo
						.getChildren(CIncomeTaxVO.class);
				vo.getAllChildrenVO();
				vo.getChildrenVO();

				if (chVOs != null && chVOs.length > 0) {
					for (CIncomeTaxVO chVO : chVOs) {
						if (chVO.getTaxbase() != null) {
							chVO.setTaxbase(new UFDouble(SalaryDecryptUtil
									.decrypt(chVO.getTaxbase().getDouble())));
						}
						if (chVO.getCacu_value() != null) {
							chVO.setCacu_value(new UFDouble(SalaryDecryptUtil
									.decrypt(chVO.getCacu_value().getDouble())));
						}
						if (chVO.getNetincome() != null) {
							chVO.setNetincome(new UFDouble(SalaryDecryptUtil
									.decrypt(chVO.getNetincome().getDouble())));
						}
						if (chVO.getPickedup() != null) {
							chVO.setPickedup(new UFDouble(SalaryDecryptUtil
									.decrypt(chVO.getPickedup().getDouble())));
						}
					}
				}

			}

		}
		return bills;
	}
	/**
	 * 对数据进行加密
	 * 
	 * @author Ares.Tank
	 * @param bills
	 * @return
	 */
	private AggSumIncomeTaxVO[] salaryEncrypt(AggSumIncomeTaxVO[] bills) {
		if (bills == null || bills.length == 0) {
			return bills;
		}
		for (AggSumIncomeTaxVO vo : bills) {
			if (vo != null) {
				if (vo.getParentVO() != null) {
					if (vo.getParentVO().getTaxbase() != null) {
						vo.getParentVO().setTaxbase(
								new UFDouble(SalaryEncryptionUtil
										.encryption(vo.getParentVO().getTaxbase()
												.getDouble())));
					}

					if (vo.getParentVO().getTaxbaseadjust() != null) {
						vo.getParentVO().setTaxbaseadjust(
								new UFDouble(SalaryEncryptionUtil
										.encryption(vo
										.getParentVO().getTaxbaseadjust()
										.getDouble())));
					}

					if (vo.getParentVO().getCacu_value() != null) {
						vo.getParentVO().setCacu_value(
								new UFDouble(SalaryEncryptionUtil
										.encryption(vo
										.getParentVO().getCacu_value()
										.getDouble())));
					}

					if (vo.getParentVO().getCacu_valueadjust() != null) {
						vo.getParentVO().setCacu_valueadjust(
								new UFDouble(SalaryEncryptionUtil
										.encryption(vo
										.getParentVO().getCacu_valueadjust()
										.getDouble())));
					}

					if (vo.getParentVO().getNetincome() != null) {
						vo.getParentVO().setNetincome(
								new UFDouble(SalaryEncryptionUtil
										.encryption(vo
										.getParentVO().getNetincome()
										.getDouble())));
					}

					if (vo.getParentVO().getPickedup() != null) {
						vo.getParentVO().setPickedup(
								new UFDouble(SalaryEncryptionUtil
										.encryption(vo
										.getParentVO().getPickedup()
										.getDouble())));
					}

					if (vo.getParentVO().getPickedupadjust() != null) {
						vo.getParentVO().setPickedupadjust(
								new UFDouble(SalaryEncryptionUtil
										.encryption(vo
										.getParentVO().getPickedupadjust()
										.getDouble())));
					}
				}

				CIncomeTaxVO[] chVOs = (CIncomeTaxVO[]) vo
						.getChildren(CIncomeTaxVO.class);
				vo.getAllChildrenVO();
				vo.getChildrenVO();

				if (chVOs != null && chVOs.length > 0) {
					for (CIncomeTaxVO chVO : chVOs) {
						if (chVO.getTaxbase() != null) {
							chVO.setTaxbase(new UFDouble(SalaryEncryptionUtil
									.encryption(chVO.getTaxbase().getDouble())));
						}
						if (chVO.getCacu_value() != null) {
							chVO.setCacu_value(new UFDouble(SalaryEncryptionUtil
									.encryption(chVO.getCacu_value().getDouble())));
						}
						if (chVO.getNetincome() != null) {
							chVO.setNetincome(new UFDouble(SalaryEncryptionUtil
									.encryption(chVO.getNetincome().getDouble())));
						}
						if (chVO.getPickedup() != null) {
							chVO.setPickedup(new UFDouble(SalaryEncryptionUtil
									.encryption(chVO.getPickedup().getDouble())));
						}
					}
				}

			}

		}
		return bills;
	}

	/**
	 * 由子类实现，查询之前对queryScheme进行加工，加入自己的逻辑
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// 查询之前对queryScheme进行加工，加入自己的逻辑
	}

}