package nc.ui.hrwa.incometax.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.hrwa.IGetAggIncomeTaxData;
import nc.itf.hrwa.IIncometaxMaintain;
import nc.itf.hrwa.ISumincometaxMaintain;
import nc.ui.hrwa.sumincometax.view.AddSumIncomeTaxView;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.IMultiRowSelectModel;
import nc.vo.hrwa.incometax.AggIncomeTaxVO;
import nc.vo.hrwa.incometax.IncomeTaxUtil;
import nc.vo.hrwa.incometax.IncomeTaxVO;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;
import nc.vo.hrwa.sumincometax.CIncomeTaxVO;
import nc.vo.hrwa.sumincometax.SumIncomeTaxVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author ward.wong
 * @date 20180126
 * @�������� �걨��ϸ������
 * 
 */
public class AddSumIncomeTaxAction extends NCAction {

	private static final long serialVersionUID = 1201191366621497639L;

	private AddSumIncomeTaxView addSumTaxView;

	private AbstractAppModel model;

	public AddSumIncomeTaxAction() {
		this.setBtnName(ResHelper.getString("incometax", "2incometax-n-000006")/*"�걨��ϸ������"*/);
		super.setCode("declaresum");
		super.putValue("declaresum", ResHelper.getString("incometax", "2incometax-n-000006"));
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		MessageDialog.showWarningDlg(getModel().getContext().getEntranceUI(),
				ResHelper.getString("incometax", "2incometax-n-000004"),ResHelper.getString("incometax", "2incometax-n-000007") /*"�걨��ϸ����������ʱ���Ѿ����ܹ��ĵ��ݽ����»��ܣ���ɾ���Ѿ����ܵ���Ϣ"*/);
		if (addSumTaxView == null) {
			addSumTaxView = new AddSumIncomeTaxView();
			addSumTaxView.setTitle(ResHelper.getString("incometax", "2incometax-n-000006")/*"�걨��ϸ������"*/);
		}
		if (addSumTaxView.showModal() == 1) {
			new Thread() {
				@Override
				public void run() {

					IProgressMonitor progressMonitor = NCProgresses
							.createDialogProgressMonitor(getModel()
									.getContext().getEntranceUI());

					try {
						progressMonitor.beginTask("�R����...",
								IProgressMonitor.UNKNOWN_REMAIN_TIME);
						progressMonitor.setProcessInfo("�R���У�Ո�Ժ�.....");
						CreateSumIncomeTax();
						ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("incometax", "2incometax-n-000008")/*"���ܳɹ�"*/,
								getModel().getContext());
					} catch (Exception e) {
						e.printStackTrace();
						ShowStatusBarMsgUtil.showErrorMsg(ResHelper.getString("incometax", "2incometax-n-000004")/*"��ʾ"*/,
								e.getMessage(), getModel().getContext());
					} finally {
						progressMonitor.done(); // �����������
					}
				}
			}.start();
		}
	}

	public AddSumIncomeTaxView getAddSumTaxView() {
		return addSumTaxView;
	}

	public void setAddSumTaxView(AddSumIncomeTaxView addSumTaxView) {
		this.addSumTaxView = addSumTaxView;
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	/**
	 * �걨��ϸ������
	 * 
	 * @throws BusinessException
	 */
	public void CreateSumIncomeTax() throws BusinessException {
		String declaretype = addSumTaxView.getDeclaretype();
		String granttype = addSumTaxView.getGranttype();// ƾ�����ʽ
		String declarenum = addSumTaxView.getDeclarenum();// �걨����
		String reason = addSumTaxView.getReason();// �ظ��걨ԭ��
		String businessno = addSumTaxView.getBusinessno();// ҵ�����
		String costno = addSumTaxView.getCostno();// ���ñ����
		String projectno = addSumTaxView.getProjectno();// ��Ŀ����
		String contactname = addSumTaxView.getContactName();// ����������
		String contacttel = addSumTaxView.getContactTel();// �����˵绰
		String contactemail = addSumTaxView.getContactEmail();// �걨��λ��������
		Object[] objects = ((IMultiRowSelectModel) getModel())
				.getSelectedOperaDatas();
		if (null == objects || objects.length < 1) {
			return;
		}
		Map<String, SumIncomeTaxVO> mapSum = new HashMap<String, SumIncomeTaxVO>();
		Map<String, List<CIncomeTaxVO>> mapIncome = new HashMap<String, List<CIncomeTaxVO>>();
		List<String> deletePks = new ArrayList<String>();// Ҫɾ������Ϣ
		List<AggIncomeTaxVO> listAggVos = new ArrayList<AggIncomeTaxVO>();// �����걨��ϸ��Ϊ�ѻ���
		for (int i = 0; i < objects.length; i++) {
			AggIncomeTaxVO aggvo = (AggIncomeTaxVO) objects[i];
			CIncomeTaxVO incomeVo = parseCIncomeTaxVO(aggvo
					.getParentVO());
			if (incomeVo.getIsdeclare().booleanValue()) {
				// ѡ�е����ݴ�����ע�ǵ�����ʱ������������
				throw new BusinessException(ResHelper.getString("incometax", "2incometax-n-000009")/*"ѡ�е����ݴ���ע�ǵ����ݣ�����ȡ��ע�ǣ������»��ܣ�"*/);
			}
			if (!declaretype.equals(incomeVo.getDeclaretype())) {
				// ѡ�е����ݵ��걨��ʽ�ͻ��ܸ�ʽ��һ��ʱ������
				continue;
			}
			//�������ܶ�۽�˰��������Ա�������Ϊ��ʱ��������������Ϣ
			if (incomeVo.getTaxbase().intValue() == 0
					&& incomeVo.getCacu_value().intValue() == 0
					&& incomeVo.getNetincome().intValue() == 0
					&& incomeVo.getPickedup().intValue() == 0) {
				listAggVos.add(aggvo);
				continue;
			}
//			aggvo.getParentVO().setIsgather(UFBoolean.TRUE);
//			aggvo.getParentVO().setStatus(VOStatus.UPDATED);
			listAggVos.add(aggvo);
			deletePks.add(incomeVo.getPk_incometax());
			String key = incomeVo.getPk_psndoc() + incomeVo.getDeclaretype();
			if (!mapSum.containsKey(key)) {
				SumIncomeTaxVO sumTaxVO = getSumIncomeTaxVO(incomeVo,
						granttype, declarenum, reason, businessno, costno,
						projectno, contactname, contacttel, contactemail);
				mapSum.put(key, sumTaxVO);
				List<CIncomeTaxVO> listIncomes = new ArrayList<CIncomeTaxVO>();
				listIncomes.add(incomeVo);
				mapIncome.put(key, listIncomes);
			} else {
				SumIncomeTaxVO sumTaxVO = mapSum.get(key);
				sumTaxVO.setTaxbase(sumTaxVO.getTaxbase().add(
						incomeVo.getTaxbase()));
				sumTaxVO.setCacu_value(sumTaxVO.getCacu_value().add(
						incomeVo.getCacu_value()));
				sumTaxVO.setNetincome(sumTaxVO.getNetincome().add(
						incomeVo.getNetincome()));
				sumTaxVO.setPickedup(sumTaxVO.getPickedup().add(
						incomeVo.getPickedup()));
				if (isperiod(sumTaxVO.getBeginperiod(),
						incomeVo.getCyearperiod())) {
					sumTaxVO.setBeginperiod(incomeVo.getCyearperiod());
				}
				if (!isperiod(sumTaxVO.getEndperiod(),
						incomeVo.getCyearperiod())) {
					sumTaxVO.setEndperiod(incomeVo.getCyearperiod());
				}
				mapIncome.get(key).add(incomeVo);
			}
		}
		if (mapSum.size() < 1) {
			return;
		}

		IGetAggIncomeTaxData getServices = NCLocator.getInstance().lookup(
				IGetAggIncomeTaxData.class);
		AggSumIncomeTaxVO[] aggTaxVOs = new AggSumIncomeTaxVO[mapSum.size()];
		int j = 0;
		for (Entry<String, SumIncomeTaxVO> e : mapSum.entrySet()) {
			AggSumIncomeTaxVO aggvo = new AggSumIncomeTaxVO();
			String key = e.getKey();
			SumIncomeTaxVO sumTaxVO = e.getValue();
			sumTaxVO.setIdtypeno(getServices.getIdtypeno(
					sumTaxVO.getPk_psndoc(), sumTaxVO.getEndperiod()));// ��ȡ֤���
			sumTaxVO.setBilldate(new UFDate());// ��������
			aggvo.setParentVO(sumTaxVO);
			aggvo.setChildrenVO(mapIncome.get(key).toArray(new CIncomeTaxVO[0]));
			aggTaxVOs[j] = aggvo;
			j++;
		}
		IIncometaxMaintain service2 = NCLocator.getInstance().lookup(
				IIncometaxMaintain.class);
		for (int i = 0; i < listAggVos.size(); i++) {
			AggIncomeTaxVO aggvo = listAggVos.get(i);
			aggvo.getParentVO().setIsgather(UFBoolean.TRUE);
			aggvo.getParentVO().setStatus(VOStatus.UPDATED);
			
		}
		AggIncomeTaxVO[] aggvos = service2.update(
				listAggVos.toArray(new AggIncomeTaxVO[0]), null);
		((BillManageModel) getModel()).directlyUpdate(aggvos);
		// ����
		// ɾ���Ѿ������Ļ�����Ϣ
		getServices.deleteSumIncomeTax(deletePks.toArray(new String[0]));
		ISumincometaxMaintain service = NCLocator.getInstance().lookup(
				ISumincometaxMaintain.class);
		service.insert(aggTaxVOs);// �����������
	}

	/**
	 * ͨ���걨��ϸ�����ݹ����걨���ܵ��ݵ�����VO
	 * 
	 * @param incomeVo
	 * @param granttype
	 * @param declarenum
	 * @param reason
	 * @param businessno
	 * @param costno
	 * @param projectno
	 * @param contactname
	 * @param contacttel
	 * @param contactemail
	 * @return
	 */
	public SumIncomeTaxVO getSumIncomeTaxVO(CIncomeTaxVO incomeVo,
			String granttype, String declarenum, String reason,
			String businessno, String costno, String projectno,
			String contactname, String contacttel, String contactemail) {
		SumIncomeTaxVO sumTaxVO = new SumIncomeTaxVO();
		sumTaxVO.setCode(incomeVo.getCode());
		sumTaxVO.setPk_psndoc(incomeVo.getPk_psndoc());
		sumTaxVO.setId(incomeVo.getId());
		sumTaxVO.setDeclaretype(incomeVo.getDeclaretype());
		sumTaxVO.setBusinessno(businessno);
		sumTaxVO.setCostno(costno);
		sumTaxVO.setProjectno(projectno);
		sumTaxVO.setIdtypeno(null);
		sumTaxVO.setGranttype(granttype);
		sumTaxVO.setDeclarenum(declarenum);
		sumTaxVO.setReason(reason);
		sumTaxVO.setContactname(contactname);
		sumTaxVO.setContacttel(contacttel);
		sumTaxVO.setContactemail(contactemail);
		sumTaxVO.setBeginperiod(incomeVo.getCyearperiod());
		sumTaxVO.setEndperiod(incomeVo.getCyearperiod());
		sumTaxVO.setTaxbase(incomeVo.getTaxbase());
		sumTaxVO.setCacu_value(incomeVo.getCacu_value());
		sumTaxVO.setNetincome(incomeVo.getNetincome());
		sumTaxVO.setPickedup(incomeVo.getPickedup());
		sumTaxVO.setPk_hrorg(incomeVo.getPk_hrorg());
		sumTaxVO.setPk_org(getModel().getContext().getPk_org());
		sumTaxVO.setPk_group(getModel().getContext().getPk_group());
		sumTaxVO.setCreator(getModel().getContext().getPk_loginUser());
		sumTaxVO.setUnifiednumber(incomeVo.getUnifiednumber());//ͳһ���
		return sumTaxVO;
	}

	/**
	 * н���ڼ�Ƚ�
	 * 
	 * @param period1
	 *            н���ڼ�
	 * @param period2
	 *            н���ڼ�
	 * @return
	 */
	public boolean isperiod(String period1, String period2) {
		Integer int1 = Integer.parseInt(period1);
		Integer int2 = Integer.parseInt(period2);
		if (int1.intValue() > int2.intValue()) {
			return true;
		}
		return false;
	}

	/**
	 * �걨��ϸ������ת�����걨���ܵ��ݵ��ӱ�VO
	 * 
	 * @param incomeVo
	 * @return
	 */
	public CIncomeTaxVO parseCIncomeTaxVO(IncomeTaxVO incomeVo) {
		CIncomeTaxVO cIncomeTaxVO = new CIncomeTaxVO();
		Map<String, String> voAttrTypeMap = IncomeTaxUtil
				.getVOFieldType(CIncomeTaxVO.class);
		for (String voAttr : voAttrTypeMap.keySet()) {
			String value = incomeVo.getAttributeValue(voAttr) != null ? incomeVo
					.getAttributeValue(voAttr).toString() : "";
			if (StringUtils.isNotBlank(voAttr)) {
				String attrType = voAttrTypeMap.get(voAttr);
				if (StringUtils.isNotBlank(attrType)) {
					IncomeTaxUtil.setVoFieldValueByType(cIncomeTaxVO, attrType,
							voAttr, value);
				}
			}
		}
		return cIncomeTaxVO;
	}

	@Override
	protected boolean isActionEnable() {
		Object[] objects = ((IMultiRowSelectModel) getModel())
				.getSelectedOperaDatas();
		if (null == objects) {
			return false;
		}
		for (int i = 0; i < objects.length; i++) {
			AggIncomeTaxVO aggvo = (AggIncomeTaxVO) objects[i];
			if (!aggvo.getParentVO().getIsdeclare().booleanValue()) {
				return true;
			}
		}
		return false;
	}
}