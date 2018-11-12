package nc.impl.hrpub.dataexchange.businessprocess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.framework.common.NCLocator;
import nc.impl.hrpub.dataexchange.DataImportExecutor;
import nc.itf.hrpub.IDataExchangeExternalExecutor;
import nc.itf.ta.IOvertimeRegisterManageMaintain;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.OvertimeRegVO;

import org.apache.commons.lang.StringUtils;

public class OvertimeBPImportExecutor extends DataImportExecutor implements IDataExchangeExternalExecutor {
	private Map<String, OvertimeRegVO> rowNCVO;
	private Map<String, String> rowOTType;

	public OvertimeBPImportExecutor() throws BusinessException {
		super();
		// TODO �Զ����ɵĹ��캯�����
	}

	@Override
	public Object getBizEntityID() throws BusinessException {
		// TODO �Զ����ɵķ������
		return null;
	}

	@Override
	public void beforeUpdate() throws BusinessException {
		if (this.getNcValueObjects() != null && this.getNcValueObjects().size() > 0) {
			String rowNo = "";
			for (Map<String, Object> rowNCMap : this.getNcValueObjects()) {
				try {
					rowNo = rowNCMap.keySet().toArray(new String[0])[0].split(":")[0];

					OvertimeRegVO vo = new OvertimeRegVO();
					vo.setBillsource(0);
					vo.setCreationtime(new UFDateTime());
					vo.setCreator(this.getCuserid());
					vo.setDeduct(new UFDouble((String) rowNCMap.get(rowNo + ":deduct")).intValue());

					String otType = this.getTimeItemByCode(this.getRowOTType().get(rowNo));
					if (StringUtils.isEmpty(otType.replace("~", ""))) {
						throw new BusinessException("�]���ҵ���Ч�ļӰ�e");
					} else {
						vo.setPk_overtimetype(otType);
					}
					String otTypeCopy = this.getTimeItemCopyByOrg(otType, this.getPk_org());
					vo.setPk_overtimetypecopy(otTypeCopy);
					String startdate = getDateString((String) rowNCMap.get(rowNo + ":overtimebegintime")).substring(0,
							10);

					String pk_psndoc = (String) rowNCMap.get(rowNo + ":pk_psndoc");
					vo.setPk_psndoc(pk_psndoc);
					vo.setIstorest(UFBoolean.valueOf((String) rowNCMap.get(rowNo + ":istorest")));

					vo.setOvertimeremark(replaceBlank((String) rowNCMap.get(rowNo + ":overtimeremark")));
					vo.setIscheck(UFBoolean.FALSE);
					vo.setIsneedcheck(UFBoolean.TRUE);
					vo.setOvertimealready(UFDouble.ZERO_DBL);

					vo.setOvertimebegintime(new UFDateTime((String) rowNCMap.get(rowNo + ":overtimebegintime")));
					vo.setOvertimebegindate(new UFLiteralDate((String) rowNCMap.get(rowNo + ":overtimebegintime")));
					vo.setOvertimeendtime(new UFDateTime((String) rowNCMap.get(rowNo + ":overtimeendtime")));
					vo.setOvertimeenddate(new UFLiteralDate((String) rowNCMap.get(rowNo + ":overtimeendtime")));
					vo.setOvertimehour(new UFDouble((String) rowNCMap.get(rowNo + ":overtimehour")));

					vo.setPk_org(this.getPk_org());
					vo.setPk_org_v(this.getPk_org_v());
					vo.setPk_group(this.getPk_group());

					// ��Ա������¼ PK_PSNJOB
					Map<String, Object> psnjob = this.getPsnjob(pk_psndoc, startdate);
					if (psnjob != null && psnjob.size() > 0 && !StringUtils.isEmpty((String) psnjob.get("pk_psnjob"))) {
						vo.setPk_psnjob((String) psnjob.get("pk_psnjob"));
						vo.setPk_dept_v((String) psnjob.get("pk_dept_v"));
						vo.setPk_psnorg((String) psnjob.get("pk_psnorg"));
					} else {
						throw new BusinessException("δ�ҵ��T������ӛ�");
					}
					vo.setToresthour(UFDouble.ZERO_DBL);
					vo.setTs(new UFDateTime());

					// MOD(�����ʱ��) ssx modified for Mapped Approve time
					vo.setApprove_time(new UFDateTime((String) rowNCMap.get(rowNo + ":approve_time")));
					// this.getRowNCVO().put(rowNo+":"+psnjobVO.getBegindate(),
					// aggVO);
					this.getRowNCVO().put(rowNo + ":" + startdate, vo);
				} catch (Exception e) {
					this.getErrorMessages().put(rowNo, e.getMessage());
				}
			}
		}
	}

	public Map<String, String> getRowOTType() {
		if (rowOTType == null) {
			rowOTType = new HashMap<String, String>();
		}
		return rowOTType;
	}

	public void setRowOTType(Map<String, String> rowOTType) {
		this.rowOTType = rowOTType;
	}

	@Override
	public void afterUpdate() throws BusinessException {
		// TODO �Զ����ɵķ������

	}

	@Override
	public void afterConvert() throws BusinessException {
		// TODO �Զ����ɵķ������

	}

	@Override
	public void beforeConvert() throws BusinessException {
		if (this.getJsonValueObjects() != null && this.getJsonValueObjects().size() > 0) {
			for (Map<String, Object> jsonobj : this.getJsonValueObjects()) {
				String rowNo = "";
				for (Entry<String, Object> entry : jsonobj.entrySet()) {
					if (entry.getKey().equals("ROWNO")) {
						rowNo = (String) entry.getValue();
					} else if (entry.getKey().equals("DAYTYPE")) {
						this.getRowOTType().put(rowNo, (String) entry.getValue());
					}
				}
			}
		}
	}

	@Override
	public void beforeQuery() throws BusinessException {
		// TODO �Զ����ɵķ������

	}

	@Override
	public void afterQuery() throws BusinessException {
		// TODO �Զ����ɵķ������

	}

	@Override
	public void doUpdateByBP() throws BusinessException {
		if (rowNCVO != null && rowNCVO.size() > 0) {
			List<String> arraylist = new ArrayList<String>();
			for (Entry<String, OvertimeRegVO> rowData : rowNCVO.entrySet()) {
				arraylist.add(rowData.getKey());
			}
			// ͨ�����ڽ�������
			ListSort(arraylist);
			// for (Entry<String, OvertimeRegVO> rowData : rowNCVO.entrySet()) {
			for (String s : arraylist) {
				try {
					this.getVOSaveService().insertData(rowNCVO.get(s));
					//�Ӱ�ת����
					UFLiteralDate beiginTime= new UFLiteralDate();
					if(rowNCVO.get(s).getIstorest().toString().equals("Y")){
						if( beiginTime.after(rowNCVO.get(s).getBegindate()))
						{
							beiginTime = rowNCVO.get(s).getBegindate();
						}
						rowNCVO.get(s).setIstorest(UFBoolean.FALSE);
						rowNCVO.get(s).setToresthour(rowNCVO.get(s).getOvertimehour());
						IOvertimeRegisterManageMaintain overtimeService = NCLocator.getInstance().lookup(IOvertimeRegisterManageMaintain.class);
						overtimeService.over2RestFirst(new OvertimeRegVO[]{rowNCVO.get(s)},String.valueOf(beiginTime.getYear()),null);
					}
				} catch (BusinessException e) {
					this.getErrorMessages().put(s.split(":")[0], e.getMessage());
				}
			}
		}
		// }
	}

	@Override
	public void beforeInsertOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO �Զ����ɵķ������

	}

	@Override
	public void beforeUpdateOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO �Զ����ɵķ������

	}

	IOvertimeRegisterManageMaintain saveService = null;

	private IOvertimeRegisterManageMaintain getVOSaveService() {
		if (saveService == null) {
			saveService = NCLocator.getInstance().lookup(IOvertimeRegisterManageMaintain.class);
		}
		return saveService;
	}

	public Map<String, OvertimeRegVO> getRowNCVO() {
		if (rowNCVO == null) {
			rowNCVO = new HashMap<String, OvertimeRegVO>();
		}
		return rowNCVO;
	}

	public void setRowNCVO(Map<String, OvertimeRegVO> rowNCVO) {
		this.rowNCVO = rowNCVO;
	}

	public static void ListSort(List<String> list) {
		Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				try {
					Date dt1 = format.parse(o1.split(":")[1]);
					Date dt2 = format.parse(o2.split(":")[1]);
					if (dt1.getTime() > dt2.getTime()) {
						return 1;
					} else if (dt1.getTime() < dt2.getTime()) {
						return -1;
					} else {
						return 0;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return 0;
			}
		});
	}

	public static String replaceBlank(String str) {
		if (str != null) {
			str = str.replaceAll("\\\\r|\\\\n", "");
		}
		return str;
	}
}
