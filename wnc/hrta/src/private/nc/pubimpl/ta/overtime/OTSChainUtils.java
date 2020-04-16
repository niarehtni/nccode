package nc.pubimpl.ta.overtime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.uap.oid.OidGenerator;
import nc.hr.utils.InSQLCreator;
import nc.impl.ta.overtime.SegdetailMaintainImpl;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.ml.MultiLangUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.AggSegDetailVO;
import nc.vo.ta.overtime.OTSChainNode;
import nc.vo.ta.overtime.SegDetailConsumeVO;
import nc.vo.ta.overtime.SegDetailVO;
import nc.vo.ta.overtime.SegRuleTermVO;
import nc.vo.ta.overtime.SegRuleVO;

import org.apache.commons.lang.StringUtils;

/**
 * �Ӱ�ֶ������p��朱����
 * 
 * @author ssx
 * 
 */
public class OTSChainUtils {
	private static BaseDAO baseDAO = null;
	protected static String SPLT = "::";
	private static OTSChainNode cachedPsnChainNodes = null;

	/**
	 * �����ˆT���Ӱ�e�����F��ӛ䛄���朱�
	 * 
	 * @param pk_psndoc
	 *            �ˆTPK
	 * @param otDate
	 *            �Ӱ�����
	 * @param pk_overtimereg
	 *            �Ӱ��ӛPK
	 * @param isForceComp
	 *            �Ƿ�ֻ�����D�{��ӛ䛣�True�rֻ�����D�{�ݵĹ��c
	 * @param isNoComp
	 *            �Ƿ�ֻ�������D�{��ӛ䛣�True�r�������D�{�������������c
	 * @param isForceNotCancel
	 *            �Ƿ�ֻ����δ���Uӛ䛣�True�rֻ����δ���U�Ĺ��c
	 * @param isForceNotConsumeFinished
	 *            �Ƿ�ֻ����δ���N�ꮅ��ӛ䛣�True�rֻ����δ���N�ꮅ�Ĺ��c
	 * @param isForceSettled
	 *            �Ƿ�ֻ����δ�Y���ӛ䛣�True�rֻ����δ�Y��Ĺ��c
	 * @return 朱��^���c
	 * 
	 * @throws BusinessException
	 */
	public static OTSChainNode buildChainNodes(String pk_psndoc, UFLiteralDate otDate, String pk_overtimereg,
			boolean isForceComp, boolean isNoComp, boolean isForceNotCancel, boolean isForceNotConsumeFinished,
			boolean isForceSettled) throws BusinessException {

		if (getCachedPsnChainNodes() != null) {
			return getCachedPsnChainNodes();
		}

		// ���ˆTȡȫ���c
		List<Map<String, Object>> vodataList = retrieveSegDetailData(pk_psndoc, otDate, pk_overtimereg);

		OTSChainNode firstNode = null;
		List<String> pkList = new ArrayList<String>();
		if (vodataList != null && vodataList.size() > 0) {
			SegDetailVO[] sdList = getSegDetailVOsFromVOData(vodataList);
			for (SegDetailVO vo : sdList) {
				vo.setPk_segdetailconsume(getConsumeVO(vo.getPk_segdetail()));
				// ���ҵ�һ�����c
				if (vo.getPk_parentsegdetail() == null || !existsParent(vo, sdList)) {
					firstNode = new OTSChainNode();
					firstNode.setNodeData(vo);
					firstNode.setNextNode(null);
					firstNode.setPriorNode(null);
					pkList.add(vo.getPk_segdetail());
				}
			}

			if (firstNode != null) {
				OTSChainNode curNode = firstNode;
				// ����朱�
				for (SegDetailVO vo : sdList) {
					if (!pkList.contains(vo.getPk_segdetail())) {
						SegDetailVO childVO = getChildVO(sdList, curNode.getNodeData().getPk_segdetail());
						if (childVO != null) {
							OTSChainNode newNode = new OTSChainNode();
							newNode.setNodeData(childVO);
							appendNode(curNode, newNode, true);
							curNode = newNode;
						} else {
							break; // һ���Ҳ����ӹ��c���J��朱��ѽ����ꮅ
						}

						pkList.add(vo.getPk_segdetail());
					}
				}
			}
		}

		// ������^�V�l��
		if (isForceComp || isForceNotCancel || isForceNotConsumeFinished || isForceSettled) {
			firstNode = filterNodes(isForceComp, isNoComp, isForceNotCancel, isForceNotConsumeFinished, isForceSettled,
					firstNode);
		}

		return firstNode;
	}

	/**
	 * �����ˆT���Ӱ�e�����F��ӛ䛄���朱�
	 * 
	 * @param pk_psndocs
	 *            �ˆTPKs
	 * @param startDate
	 *            ��ʼ����
	 * @param endDate
	 *            ��ֹ����
	 * @param pk_overtimereg
	 *            �Ӱ��ӛPK
	 * @param isForceComp
	 *            �Ƿ�ֻ�����D�{��ӛ䛣�True�rֻ�����D�{�ݵĹ��c
	 * @param isNoComp
	 *            �Ƿ�ֻ�������D�{��ӛ䛣�True�r�������D�{�������������c
	 * @param isForceNotCancel
	 *            �Ƿ�ֻ����δ���Uӛ䛣�True�rֻ����δ���U�Ĺ��c
	 * @param isForceNotConsumeFinished
	 *            �Ƿ�ֻ����δ���N�ꮅ��ӛ䛣�True�rֻ����δ���N�ꮅ�Ĺ��c
	 * @param isForceSettled
	 *            �Ƿ�ֻ����δ�Y���ӛ䛣�True�rֻ����δ�Y��Ĺ��c
	 * @return 朱��^���c
	 * 
	 * @throws BusinessException
	 */
	public static Map<String, OTSChainNode> buildChainPsnNodeMap(String[] pk_psndocs, UFLiteralDate startDate,
			UFLiteralDate endDate, String pk_overtimereg, boolean isForceComp, boolean isNoComp,
			boolean isForceNotCancel, boolean isForceNotConsumeFinished, boolean isForceSettled)
			throws BusinessException {

		// ���ˆTȡȫ���c
		List<Map<String, Object>> vodataList = retrieveSegDetailData(pk_psndocs, startDate, endDate, pk_overtimereg);

		Map<String, List<OTSChainNode>> psnNodes = null;
		Map<String, OTSChainNode> psnFirstNode = new HashMap<String, OTSChainNode>();
		Map<String, OTSChainNode> minNode = new HashMap<String, OTSChainNode>();
		;
		Map<String, String> minsegCode = new HashMap<String, String>();
		if (vodataList != null && vodataList.size() > 0) {
			SegDetailVO[] sdList = getSegDetailVOsFromVOData(vodataList);
			for (SegDetailVO vo : sdList) {
				if (!vo.getRemainhours().equals(vo.getHours())) {
					vo.setPk_segdetailconsume(getConsumeVO(vo.getPk_segdetail()));
				}

				if (psnNodes == null) {
					psnNodes = new HashMap<String, List<OTSChainNode>>();
				}

				if (psnNodes.get(vo.getPk_psndoc()) == null) {
					psnNodes.put(vo.getPk_psndoc(), new ArrayList<OTSChainNode>());
				}

				OTSChainNode newnode = new OTSChainNode();
				newnode.setNodeData(vo);
				newnode.setNextNode(null);
				newnode.setPriorNode(null);
				psnNodes.get(vo.getPk_psndoc()).add(newnode);

				if (StringUtils.isEmpty(minsegCode.get(vo.getPk_psndoc()))) {
					minsegCode.put(vo.getPk_psndoc(), vo.getNodecode());
					minNode.put(vo.getPk_psndoc(), newnode);
				} else {
					if (minsegCode.get(vo.getPk_psndoc()).compareToIgnoreCase(vo.getNodecode()) > 0) {
						minsegCode.put(vo.getPk_psndoc(), vo.getNodecode());
						minNode.put(vo.getPk_psndoc(), newnode);
					}
				}

				psnFirstNode.put(vo.getPk_psndoc(), minNode.get(vo.getPk_psndoc()));
			}

			if (psnNodes != null && psnNodes.size() > 0) {
				for (String pk_psndoc : psnNodes.keySet()) {
					OTSChainNode curNode = psnFirstNode.get(pk_psndoc);
					// ����朱�
					for (OTSChainNode vo : psnNodes.get(pk_psndoc).toArray(new OTSChainNode[0])) {
						SegDetailVO childVO = getChildVOByCode(psnNodes.get(pk_psndoc).toArray(new OTSChainNode[0]),
								curNode.getNodeData().getNodecode(), curNode.getNodeData().getApproveddate()
										.toStdString());
						if (childVO != null) {
							OTSChainNode newNode = new OTSChainNode();
							newNode.setNodeData(childVO);
							appendNode(curNode, newNode, true);
							curNode = newNode;
						} else {
							break; // һ���Ҳ����ӹ��c���J��朱��ѽ����ꮅ
						}
					}
				}
			}
		}

		// ������^�V�l��
		if (isForceComp || isForceNotCancel || isForceNotConsumeFinished || isForceSettled) {
			for (String pk_psndoc : psnFirstNode.keySet()) {
				psnFirstNode.put(
						pk_psndoc,
						filterNodes(isForceComp, isNoComp, isForceNotCancel, isForceNotConsumeFinished, isForceSettled,
								psnFirstNode.get(pk_psndoc)));
			}
		}

		return psnFirstNode;
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> retrieveSegDetailData(String pk_psndoc, UFLiteralDate otDate,
			String pk_overtimereg) throws BusinessException {
		return (List<Map<String, Object>>) getBaseDAO()
				.executeQuery(
						"select pk_segdetail,pk_group,pk_org,pk_org_v,creator,creationtime,modifier,modifiedtime,maketime,"
								+ "pk_parentsegdetail,nodeno,nodecode,nodename,regdate,pk_segrule,pk_segruleterm,pk_psndoc,pk_overtimereg,"
								+ "rulehours,hours,hourstaxfree,hourstaxable,hourlypay,taxfreerate,taxablerate,consumedhours,"
								+ "consumedhourstaxfree,consumedhourstaxable,remainhours,remainhourstaxfree,"
								+ "remainhourstaxable,remainamount,remainamounttaxfree,remainamounttaxable,iscanceled,"
								+ "iscompensation,hourstorest,isconsumed,issettled,frozenhours,frozenhourstaxfree,frozenhourstaxable,settledate,"
								+ "extrahourstaxable, extrataxablerate, extraamounttaxable, expirydate,approveddate,ts,dr from "
								+ SegDetailVO.getDefaultTableName()
								+ " where dr=0 and pk_psndoc='"
								+ pk_psndoc
								+ "' "
								+ (otDate == null ? "" : " and regdate='" + otDate.toString() + "'")
								+ (StringUtils.isEmpty(pk_overtimereg) ? "" : " and pk_overtimereg='" + pk_overtimereg
										+ "' "), new MapListProcessor());
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> retrieveSegDetailData(String[] pk_psndocs, UFLiteralDate startDate,
			UFLiteralDate endDate, String pk_overtimereg) throws BusinessException {
		return (List<Map<String, Object>>) getBaseDAO()
				.executeQuery(
						"select pk_segdetail,pk_group,pk_org,pk_org_v,creator,creationtime,modifier,modifiedtime,maketime,"
								+ "pk_parentsegdetail,nodeno,nodecode,nodename,regdate,pk_segrule,pk_segruleterm,pk_psndoc,pk_overtimereg,"
								+ "rulehours,hours,hourstaxfree,hourstaxable,hourlypay,taxfreerate,taxablerate,consumedhours,"
								+ "consumedhourstaxfree,consumedhourstaxable,remainhours,remainhourstaxfree,"
								+ "remainhourstaxable,remainamount,remainamounttaxfree,remainamounttaxable,iscanceled,"
								+ "iscompensation,hourstorest,isconsumed,issettled,frozenhours,frozenhourstaxfree,frozenhourstaxable,settledate,"
								+ "extrahourstaxable, extrataxablerate, extraamounttaxable, expirydate,approveddate,ts,dr from "
								+ SegDetailVO.getDefaultTableName()
								+ " where dr=0 and (iscompensation = 'Y' or issettled = 'N') and pk_psndoc in ("
								+ (new InSQLCreator().getInSQL(pk_psndocs))
								+ ") "
								+ (startDate == null ? "" : " and regdate >= '" + startDate.toString() + "' ")
								+ (endDate == null ? "" : " and regdate <= '" + endDate.toString() + "' ")
								+ (StringUtils.isEmpty(pk_overtimereg) ? "" : " and pk_overtimereg='" + pk_overtimereg
										+ "'"), new MapListProcessor());
	}

	private static SegDetailVO[] getSegDetailVOsFromVOData(List<Map<String, Object>> vodataList) {
		List<SegDetailVO> ret = new ArrayList<SegDetailVO>();
		for (Map<String, Object> vodata : vodataList) {
			SegDetailVO vo = getVOByVOData(vodata);
			ret.add(vo);
		}
		return ret.toArray(new SegDetailVO[0]);
	}

	private static SegDetailVO getVOByVOData(Map<String, Object> vodata) {
		SegDetailVO vo = new SegDetailVO();
		vo.setPk_segdetail((String) vodata.get("pk_segdetail"));
		vo.setPk_group((String) vodata.get("pk_group"));
		vo.setPk_org((String) vodata.get("pk_org"));
		vo.setPk_org_v((String) vodata.get("pk_org_v"));
		vo.setCreator((String) vodata.get("creator"));
		vo.setCreationtime(getUFDateTime(vodata.get("creationtime")));
		vo.setModifier((String) vodata.get("modifier"));
		vo.setModifiedtime(getUFDateTime(vodata.get("modifiedtime")));
		vo.setMaketime(vodata.get("maketime") == null ? null : new UFDate((String) vodata.get("maketime")));
		vo.setPk_parentsegdetail((String) vodata.get("pk_parentsegdetail"));
		vo.setNodeno((Integer) vodata.get("nodeno"));
		vo.setNodecode((String) vodata.get("nodecode"));
		vo.setNodename((String) vodata.get("nodename"));
		vo.setRegdate(vodata.get("regdate") == null ? null : new UFLiteralDate((String) vodata.get("regdate")));
		vo.setPk_segrule((String) vodata.get("pk_segrule"));
		vo.setPk_segruleterm((String) vodata.get("pk_segruleterm"));
		vo.setPk_psndoc((String) vodata.get("pk_psndoc"));
		vo.setPk_overtimereg((String) vodata.get("pk_overtimereg"));
		vo.setRulehours(getUFDoubleValue(vodata.get("rulehours")));
		vo.setHours(getUFDoubleValue(vodata.get("hours")));
		vo.setHourstaxfree(getUFDoubleValue(vodata.get("hourstaxfree")));
		vo.setHourstaxable(getUFDoubleValue(vodata.get("hourstaxable")));
		vo.setHourlypay(getUFDoubleValue(vodata.get("hourlypay")));
		vo.setTaxfreerate(getUFDoubleValue(vodata.get("taxfreerate")));
		vo.setTaxablerate(getUFDoubleValue(vodata.get("taxablerate")));
		vo.setExtrahourstaxable(getUFDoubleValue(vodata.get("extrahourstaxable")));
		vo.setExtrataxablerate(getUFDoubleValue(vodata.get("extrataxablerate")));
		vo.setExtraamounttaxable(getUFDoubleValue(vodata.get("extraamounttaxable")));
		vo.setConsumedhours(getUFDoubleValue(vodata.get("consumedhours")));
		vo.setConsumedhourstaxfree(getUFDoubleValue(vodata.get("consumedhourstaxfree")));
		vo.setConsumedhourstaxable(getUFDoubleValue(vodata.get("consumedhourstaxable")));
		vo.setRemainhours(getUFDoubleValue(vodata.get("remainhours")));
		vo.setRemainhourstaxfree(getUFDoubleValue(vodata.get("remainhourstaxfree")));
		vo.setRemainhourstaxable(getUFDoubleValue(vodata.get("remainhourstaxable")));
		vo.setRemainamount(getUFDoubleValue(vodata.get("remainamount")));
		vo.setRemainamounttaxfree(getUFDoubleValue(vodata.get("remainamounttaxfree")));
		vo.setRemainamounttaxable(getUFDoubleValue(vodata.get("remainamounttaxable")));
		vo.setFrozenhours(getUFDoubleValue(vodata.get("frozenhours")));
		vo.setFrozenhourstaxfree(getUFDoubleValue(vodata.get("frozenhourstaxfree")));
		vo.setFrozenhourstaxable(getUFDoubleValue(vodata.get("frozenhourstaxable")));
		vo.setIscanceled(new UFBoolean("Y".equals((String) vodata.get("iscanceled"))));
		vo.setIscompensation(new UFBoolean("Y".equals((String) vodata.get("iscompensation"))));
		vo.setHourstorest(getUFDoubleValue(vodata.get("hourstorest")));
		vo.setIsconsumed(new UFBoolean("Y".equals((String) vodata.get("isconsumed"))));
		vo.setIssettled(new UFBoolean("Y".equals((String) vodata.get("issettled"))));
		vo.setSettledate(getUFLiteralDate(vodata.get("settledate")));
		vo.setApproveddate(getUFLiteralDate(vodata.get("approveddate")));
		vo.setExpirydate(getUFLiteralDate(vodata.get("expirydate")));
		vo.setTs(getUFDateTime(vodata.get("ts")));
		vo.setDr(vodata.get("dr") == null ? null : (Integer) vodata.get("dr"));
		return vo;
	}

	private static UFLiteralDate getUFLiteralDate(Object value) {
		if (value == null) {
			return null;
		} else {
			return new UFLiteralDate((String) value);
		}
	}

	private static UFDateTime getUFDateTime(Object value) {
		if (value == null) {
			return null;
		} else {
			return new UFDateTime((String) value);
		}
	}

	private static UFDouble getUFDoubleValue(Object value) {
		if (value == null) {
			return UFDouble.ZERO_DBL;
		} else {
			if (value instanceof BigDecimal) {
				return new UFDouble((BigDecimal) value);
			} else if (value instanceof Integer) {
				return new UFDouble((Integer) value);
			} else {
				return new UFDouble(String.valueOf(value));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static SegDetailConsumeVO[] getConsumeVO(String pk_segdetail) throws BusinessException {
		Collection<SegDetailConsumeVO> consumevos = getBaseDAO().retrieveByClause(SegDetailConsumeVO.class,
				"pk_segdetail='" + pk_segdetail + "'");
		return consumevos == null ? null : consumevos.toArray(new SegDetailConsumeVO[0]);
	}

	/**
	 * ������������朱����^�V��߉݋朱�
	 * 
	 * @param isForceComp
	 *            �Ƿ�ֻ�����D�{��ӛ䛣�True�rֻ�����D�{�ݵĹ��c
	 * @param isNoComp
	 *            �Ƿ�ֻ�������D�{��ӛ䛣�True�r�������D�{�����������й��c
	 * @param isForceNotCancel
	 *            �Ƿ�ֻ����δ���Uӛ䛣�True�rֻ����δ���U�Ĺ��c
	 * @param isForceNotConsumeFinished
	 *            �Ƿ�ֻ����δ���N�ꮅ��ӛ䛣�True�rֻ����δ���N�ꮅ�Ĺ��c
	 * @param isForceSettled
	 *            �Ƿ�ֻ����δ�Y���ӛ䛣�True�rֻ����δ�Y��Ĺ��c
	 * @param firstNode
	 *            ��һ�����c
	 * @return ��һ�����c
	 * @throws BusinessException
	 */
	public static OTSChainNode filterNodes(boolean isForceComp, boolean isNoComp, boolean isForceNotCancel,
			boolean isForceNotConsumeFinished, boolean isForceSettled, OTSChainNode firstNode) throws BusinessException {
		if (firstNode != null) {
			OTSChainNode curNode = firstNode.clone(); // ��¡���c��������Ą��������߉݋朱��y
			OTSChainNode holdNode = curNode; // ���ַ����ù��c
			do {
				OTSChainNode tmpNextNode = curNode.getNextNode();
				if ((isForceComp && !curNode.getNodeData().getIscompensation().booleanValue() // �h���ǼӰ��D�{�ݹ��c
						)
						|| (isForceNotCancel && curNode.getNodeData().getIscanceled().booleanValue() // �h�������U���c
						) || (isForceNotConsumeFinished && curNode.getNodeData().getIsconsumed().booleanValue() // �h���Ѻ��N�ꮅ���c
						) || (isForceSettled && curNode.getNodeData().getIssettled().booleanValue() // �h���ѽY�㹝�c
						) || (isNoComp && curNode.getNodeData().getIscompensation().booleanValue()) // �h���D�{�ݹ��c
				) {
					removeCurrentNode(curNode, false);
					holdNode = holdNode == curNode ? null : holdNode;
				} else {
					holdNode = curNode;
				}

				curNode = tmpNextNode;
			} while (curNode != null);

			firstNode = getFirstNode(holdNode);
		}

		return firstNode;
	}

	/**
	 * ȡ�ӹ��c
	 * 
	 * @param sdList
	 *            ���c�б�
	 * @param pk_segdetail
	 *            ��ǰ���cPK
	 * @return
	 */
	private static SegDetailVO getChildVO(OTSChainNode[] psnNodes, String pk_segdetail) {
		if (psnNodes != null && psnNodes.length > 0) {
			for (OTSChainNode curNode : psnNodes) {
				if (pk_segdetail.equals(curNode.getNodeData().getPk_parentsegdetail())) {
					return curNode.getNodeData();
				}
			}
		}
		return null;
	}

	private static SegDetailVO getChildVOByCode(OTSChainNode[] psnNodes, String parentNodeCode,
			String parentApproveddateStr) {
		String minCode = null;
		SegDetailVO minSeg = null;
		if (psnNodes != null && psnNodes.length > 0) {
			// mod ��code����������Ϊkey�ķ�ʽ���ֶַε�˳�� tank 2020��3��9�� 00:11:28
			// TODO :ͬһ���ˣ�ͬһ�죬ͬһ�ֶΣ��������ڶ�һ�ӳ�����,�ֶ�˳�������
			String key = parentNodeCode + parentApproveddateStr;
			StringBuilder comparetorSB = new StringBuilder();

			for (OTSChainNode curNode : psnNodes) {

				comparetorSB.append(curNode.getNodeData().getNodecode()).append(
						curNode.getNodeData().getApproveddate().toStdString());
				if (comparetorSB.toString().compareTo(key) > 0) {
					// mod end ��code����������Ϊkey�ķ�ʽ���ֶַε�˳�� tank 2020��3��9�� 00:11:32
					if (StringUtils.isEmpty(minCode)) {
						minCode = curNode.getNodeData().getNodecode();
						minSeg = curNode.getNodeData();
					} else {
						if (curNode.getNodeData().getNodecode().compareTo(minCode) < 0) {
							minCode = curNode.getNodeData().getNodecode();
							minSeg = curNode.getNodeData();
						}
					}
				}
				comparetorSB.delete(0, comparetorSB.length());
			}
		}
		return minSeg;
	}

	private static SegDetailVO getChildVO(SegDetailVO[] sdList, String pk_segdetail) {
		for (SegDetailVO vo : sdList) {
			if (pk_segdetail.equals(vo.getPk_parentsegdetail())) {
				return vo;
			}
		}
		return null;
	}

	/**
	 * �o��VO�Ƿ񲻴����ϼ����c
	 * 
	 * @param sdList
	 *            ȫ���c
	 * @param vo
	 *            �z�鹝�c
	 * @return
	 */
	private static boolean existsParent(SegDetailVO vo, SegDetailVO[] sdList) {
		for (SegDetailVO childvo : sdList) {
			if (vo.getPk_parentsegdetail().equals(childvo.getPk_segdetail())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ��ָ�����c�����ӹ��c
	 * 
	 * @param targetNode
	 *            Ŀ�˹��c
	 * @param newNode
	 *            �������c
	 * @throws BusinessException
	 */
	public static OTSChainNode appendNode(OTSChainNode targetNode, OTSChainNode newNode, boolean readonly)
			throws BusinessException {
		if (targetNode != null) {
			if (targetNode.getNextNode() != null) {
				if (newNode != null) {
					newNode.setNextNode(targetNode.getNextNode());
				}
				targetNode.getNextNode().setPriorNode(newNode);
				if (!readonly) {
					targetNode.getNextNode().getNodeData()
							.setPk_parentsegdetail(newNode.getNodeData().getPk_segdetail());
					if (VOStatus.NEW != targetNode.getNextNode().getNodeData().getStatus()) {
						targetNode.getNextNode().getNodeData().setStatus(VOStatus.UPDATED);
					}
				}
			}

			// ������б�����ǵ����������ԶԺ����ڵ�ĸı䲻���г־û�
			targetNode.setNextNode(newNode);

			if (newNode != null) {
				newNode.setPriorNode(targetNode);
				if (!readonly) {
					newNode.getNodeData().setPk_parentsegdetail(targetNode.getNodeData().getPk_segdetail());
					if (VOStatus.NEW != newNode.getNodeData().getStatus()) {
						newNode.getNodeData().setStatus(VOStatus.UPDATED);
					}
				}
			}
			return targetNode;
		} else {
			return newNode;
		}
	}

	/**
	 * �h��ָ�����c����һ���c
	 * 
	 * @param targetNode
	 *            Ŀ�˹��c
	 * @param removeFromDB
	 *            �Ƿ�Ĕ������Єh��
	 * @throws BusinessException
	 */
	public static void removeNextNode(OTSChainNode targetNode, boolean removeFromDB) throws BusinessException {
		if (targetNode != null) {
			if (targetNode.getNextNode() != null) {
				removeCurrentNode(targetNode.getNextNode(), removeFromDB);
			} else {
				throw new BusinessException("���c�h���e�`����ǰ���c���џo���^���c��");
			}
		} else {
			throw new BusinessException("���c�h���e�`��Ŀ�˹��c��ա�");
		}
	}

	/**
	 * �h��ָ�����c��ǰһ���c
	 * 
	 * @param targetNode
	 *            Ŀ�˹��c
	 * @param removeFromDB
	 *            �Ƿ�Ĕ������Єh��
	 * @throws BusinessException
	 */
	public static void removePriorNode(OTSChainNode targetNode, boolean removeFromDB) throws BusinessException {
		if (targetNode != null) {
			if (targetNode.getPriorNode() != null) {
				removeCurrentNode(targetNode.getPriorNode(), removeFromDB);
			} else {
				throw new BusinessException("���c�h���e�`����ǰ���c���џoǰ�m���c��");
			}
		} else {
			throw new BusinessException("���c�h���e�`��Ŀ�˹��c��ա�");
		}
	}

	/**
	 * �h����ǰ���c
	 * 
	 * @param targetNode
	 *            ��ǰҪ�h���Ĺ��c
	 * @param removeFromDB
	 *            �Ƿ�Ĕ������Єh��
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public static void removeCurrentNode(OTSChainNode targetNode, boolean removeFromDB) throws BusinessException {
		if (targetNode.getPriorNode() != null) {
			targetNode.getPriorNode().setNextNode(targetNode.getNextNode());
		}

		if (targetNode.getNextNode() != null) {
			targetNode.getNextNode().setPriorNode(targetNode.getPriorNode());
			if (targetNode.getPriorNode() != null) {
				targetNode.getNextNode().getNodeData()
						.setPk_parentsegdetail(targetNode.getPriorNode().getNodeData().getPk_segdetail());
			} else {
				targetNode.getNextNode().getNodeData().setPk_parentsegdetail(null);
			}
			if (removeFromDB) {
				targetNode.getNextNode().getNodeData().setStatus(VOStatus.UPDATED);
			}
		}

		if (removeFromDB) {
			SegDetailVO vo = targetNode.getNodeData();
			// �h��
			AggSegDetailVO aggvo = new AggSegDetailVO();
			aggvo.setParent(vo);

			Collection<SegDetailConsumeVO> lstChildVOs = getBaseDAO().retrieveByClause(SegDetailConsumeVO.class,
					"pk_segdetail='" + vo.getPk_segdetail() + "'");
			aggvo.setChildrenVO(lstChildVOs.toArray(new SegDetailConsumeVO[0]));
			new SegdetailMaintainImpl().delete(new AggSegDetailVO[] { aggvo });
		}
	}

	/**
	 * �����o�����c���ҵ�һ�����c
	 * 
	 * @param node
	 *            ���c
	 * @return ��һ�����c
	 * @throws BusinessException
	 */
	public static OTSChainNode getFirstNode(OTSChainNode node) throws BusinessException {
		if (node != null) {
			if (node.getPriorNode() == null) {
				return node;
			} else {
				return getFirstNode(node.getPriorNode());
			}
		} else {
			// throw new BusinessException("�@ȡ��һ�����c�e�`����ǰ���c���ܞ�ա�");
			return node;
		}
	}

	/**
	 * �����o���Ĺ��c��������һ�����c
	 * 
	 * @param node
	 *            ���c
	 * @return ����һ�����c
	 * @throws BusinessException
	 */
	public static OTSChainNode getLastNode(OTSChainNode node) throws BusinessException {
		if (node != null) {
			if (node.getNextNode() == null) {
				return node;
			} else {
				return getLastNode(node.getNextNode());
			}
		} else {
			// throw new BusinessException("�@ȡ����һ�����c�e�`����ǰ���c���ܞ�ա�");
			return node;
		}
	}

	/**
	 * �������й��c�ļӰ�ֶ����� ���H��춌�SegDetailVO���w�Ĳ�������̎���������I��
	 * 
	 * @param node
	 *            ���c
	 * @return
	 * @throws BusinessException
	 */
	public static OTSChainNode saveAll(OTSChainNode node) throws BusinessException {
		if (node != null) {
			OTSChainNode curNode = getFirstNode(node);
			do {
				if (curNode.getPriorNode() != null) {
					curNode.getNodeData().setPk_parentsegdetail(curNode.getPriorNode().getNodeData().getPk_segdetail());
				} else {
					curNode.getNodeData().setPk_parentsegdetail(null);
				}
				save(curNode);
				curNode = curNode.getNextNode();
			} while (curNode != null);
			return node;
		} else {
			throw new BusinessException("ȫ�������e�`����ǰ���c���ܞ�ա�");
		}
	}

	/**
	 * �ι��c����
	 * 
	 * @param node
	 *            ��ǰ����Ĺ��c
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public static void save(OTSChainNode node) throws BusinessException {
		SegDetailVO vo = node.getNodeData();
		if (vo.getPk_segdetail() == null || VOStatus.NEW == vo.getStatus()) {
			// ����
			AggSegDetailVO aggvo = new AggSegDetailVO();

			aggvo.setParent(vo);
			AggSegDetailVO[] ret = new SegdetailMaintainImpl().insert(new AggSegDetailVO[] { aggvo });
			vo.setPk_segdetail(ret[0].getPrimaryKey());
			// �޸�flag,ts tank 2020��1��13��17:53:04
			vo.setStatus(VOStatus.UNCHANGED);
			vo.setTs(ret[0].getParentVO().getTs());
		} else if (vo.getPk_segdetail() != null && VOStatus.UPDATED == vo.getStatus()) {
			// �޸�
			AggSegDetailVO aggvo = new AggSegDetailVO();
			aggvo.setParent(vo);

			Collection<SegDetailConsumeVO> lstChildVOs = getBaseDAO().retrieveByClause(SegDetailConsumeVO.class,
					"pk_segdetail='" + vo.getPk_segdetail() + "'");
			aggvo.setChildrenVO(lstChildVOs.toArray(new SegDetailConsumeVO[0]));
			AggSegDetailVO[] ret = new SegdetailMaintainImpl().update(new AggSegDetailVO[] { aggvo });
			// �޸�flag,ts tank 2020��1��13��17:53:04
			vo.setStatus(VOStatus.UNCHANGED);
			vo.setTs(ret[0].getParentVO().getTs());
		}
	}

	public static BaseDAO getBaseDAO() {
		if (baseDAO == null) {
			baseDAO = new BaseDAO();
		}

		return baseDAO;
	}

	/**
	 * �ρ㹝�c
	 * 
	 * @param originalNode
	 *            ԭʼ���c
	 * @param newNode
	 *            �¹��c
	 * @throws BusinessException
	 */
	public static OTSChainNode combineNodes(OTSChainNode originalNode, OTSChainNode newNode) throws BusinessException {
		if (newNode != null) {
			OTSChainNode curNode = null;
			OTSChainNode parentNode = null;

			// ���¹��c��v
			do {
				if (originalNode == null) {
					originalNode = newNode;
					break;
				} else {
					if (curNode == null) {
						curNode = newNode;
					} else {
						curNode = curNode.getNextNode();
					}
				}

				// ���Ҹ����c
				// ����߉݋������NodeCode��ͬ���ˆT�����ڣ��ֶ�̖�a����ͬ���Ĺ��c
				// ���朱�ĩ����ǰ���i�����F�ĵ�һ��NodeCodeС춮�ǰNodeCode�Ĺ��c
				parentNode = findParentNode(originalNode, curNode);
				OTSChainNode addedNode = curNode.cloneSingle();
				if (parentNode == null) {
					// �����ڸ����c����朱������й��c���Ȯ�ǰ���c�l���������Ԯ�ǰ���c�����׹��c
					addedNode.setNextNode(null);
					addedNode.setPriorNode(null);
					originalNode = OTSChainUtils.appendNode(addedNode, originalNode, false);
				} else {
					if (parentNode.getNodeData().getNodecode().equals(addedNode.getNodeData().getNodecode())) {
						OTSChainNode nextNewNode = null;
						// ���c���a��ͬ=Ո���ˆT�����ڡ��ֶ���ȫһ��
						UFDouble ruleHours = parentNode.getNodeData().getRulehours(); // �ֶ�Ҏ�t���x�ķֶΕr�L
						UFDouble parentHours = getParentTotalHoursBySameCode(parentNode); // �с��õ�Ҏ�t�r�L
						UFDouble newHours = addedNode.getNodeData().getHours();
						if (parentHours.doubleValue() < ruleHours.doubleValue()) {
							// �����c�Ӱ��r�LС춷ֶΕr�L�r=�ϴ�Ո��δՈ�Mԓ�ֶΣ��z��ֶ��nӋ�r�L
							if (parentHours.add(newHours).doubleValue() <= ruleHours.doubleValue()) {
								// �n������ȻС춵�춷ֶΕr�L�ģ�ֱ���������m���c
								originalNode = appendNode(parentNode, addedNode, false);
							} else {
								// �n���ᳬ�^�ֶΕr�L�ģ������a���Εr�L�����m���c
								UFDouble appendHours = ruleHours.sub(parentHours); // δ���õ�Ҏ�t�r�L
								UFDouble appendHoursTaxfree = UFDouble.ZERO_DBL;
								UFDouble appendHoursTaxable = UFDouble.ZERO_DBL;
								if (appendHours.doubleValue() >= addedNode.getNodeData().getHourstaxfree()
										.doubleValue()) {
									appendHoursTaxfree = addedNode.getNodeData().getHourstaxfree();
									if (appendHours.sub(appendHoursTaxfree).doubleValue() >= addedNode.getNodeData()
											.getHourstaxable().doubleValue()) {
										appendHoursTaxable = addedNode.getNodeData().getHourstaxable();
									} else {
										appendHoursTaxable = appendHours.sub(appendHoursTaxfree);
									}
								} else {
									appendHoursTaxfree = appendHours;
									appendHoursTaxable = UFDouble.ZERO_DBL;
								}

								UFDouble nextHours = addedNode.getNodeData().getHours().sub(appendHours);
								UFDouble nextHoursTaxfree = addedNode.getNodeData().getHourstaxfree()
										.sub(appendHoursTaxfree);
								UFDouble nextHoursTaxable = addedNode.getNodeData().getHourstaxable()
										.sub(appendHoursTaxable);

								addedNode.getNodeData().setHours(appendHours);
								addedNode.getNodeData().setHourstaxfree(appendHoursTaxfree);
								addedNode.getNodeData().setHourstaxable(appendHoursTaxable);

								originalNode = appendNode(parentNode, addedNode, false);

								// ���^���ּӰ�ֹ�ȡ��һ��
								nextNewNode = getNextNewNode(addedNode, nextHours, nextHoursTaxfree, nextHoursTaxable);
								originalNode = combineNodes(originalNode, nextNewNode);
							}
						} else if (parentHours.doubleValue() == ruleHours.doubleValue()) {
							// �����c�Ӱ��r�L��춷ֶΕr�L=ֱ���ڮ�ǰ���c���������m���c���Ӱ�ֶ�Ҫȡ��һ��
							nextNewNode = getNextNewNode(addedNode, addedNode.getNodeData().getHours(), addedNode
									.getNodeData().getHourstaxfree(), addedNode.getNodeData().getHourstaxable());
							originalNode = combineNodes(originalNode, nextNewNode);
						}
					} else {
						// ���c���a����ͬ��ֱ���ڮ�ǰ���c���������m���c
						originalNode = appendNode(parentNode, addedNode, false);
					}
				}
			} while ((curNode.getNextNode() != null));
		}

		return OTSChainUtils.getFirstNode(originalNode);
	}

	private static OTSChainNode getNextNewNode(OTSChainNode curNode, UFDouble nextHours, UFDouble nextHoursTaxfree,
			UFDouble nextHoursTaxable) throws BusinessException {
		OTSChainNode nextNewNode;
		nextNewNode = curNode.cloneSingle();
		nextNewNode.setNextNode(null);
		nextNewNode.setPriorNode(null);
		nextNewNode.getNodeData().setPk_segdetail(OidGenerator.getInstance().nextOid());
		nextNewNode.getNodeData().setPk_segdetailconsume(null);
		nextNewNode.getNodeData().setConsumedhours(UFDouble.ZERO_DBL);
		nextNewNode.getNodeData().setConsumedhourstaxable(UFDouble.ZERO_DBL);
		nextNewNode.getNodeData().setConsumedhourstaxfree(UFDouble.ZERO_DBL);
		nextNewNode.getNodeData().setFrozenhours(UFDouble.ZERO_DBL);
		nextNewNode.getNodeData().setFrozenhourstaxable(UFDouble.ZERO_DBL);
		nextNewNode.getNodeData().setFrozenhourstaxfree(UFDouble.ZERO_DBL);
		nextNewNode.getNodeData().setHours(nextHours);
		nextNewNode.getNodeData().setHourstaxfree(nextHoursTaxfree);
		nextNewNode.getNodeData().setHourstaxable(nextHoursTaxable);
		nextNewNode.getNodeData().setExtrahourstaxable(nextHoursTaxfree);
		SegRuleTermVO nextTerm = getNextSegRuleTerm(curNode.getNodeData(), nextHours);
		nextNewNode.getNodeData().setPk_segruleterm(nextTerm.getPk_segruleterm());
		UFDouble start = nextTerm.getStartpoint();
		UFDouble end = nextTerm.getEndpoint() == null ? new UFDouble(24) : nextTerm.getEndpoint();
		UFDouble taxablerate = nextTerm.getTaxableotrate();
		UFDouble taxfreerate = nextTerm.getTaxfreeotrate();
		nextNewNode.getNodeData().setRulehours(end.sub(start));
		nextNewNode.getNodeData().setTaxfreerate(taxfreerate);
		nextNewNode.getNodeData().setTaxablerate(taxfreerate);
		nextNewNode.getNodeData().setExtrataxablerate(taxablerate);

		SegRuleVO rule = (SegRuleVO) getBaseDAO().retrieveByPK(SegRuleVO.class, curNode.getNodeData().getPk_segrule());
		PsndocVO psnVo = (PsndocVO) getBaseDAO().retrieveByPK(PsndocVO.class, curNode.getNodeData().getPk_psndoc());
		nextNewNode.getNodeData().setNodecode(
				psnVo.getCode() + OTSChainUtils.SPLT + curNode.getNodeData().getRegdate().toString()
						+ OTSChainUtils.SPLT + rule.getCode() + OTSChainUtils.SPLT
						+ String.valueOf(String.format("%02d", nextTerm.getSegno())));
		nextNewNode.getNodeData().setNodename(
				MultiLangUtil.getSuperVONameOfCurrentLang(psnVo, PsndocVO.NAME, psnVo.getName()) + OTSChainUtils.SPLT
						+ curNode.getNodeData().getRegdate().toString().replace("-", "") + OTSChainUtils.SPLT
						+ MultiLangUtil.getSuperVONameOfCurrentLang(rule, SegRuleVO.NAME, rule.getName())
						+ OTSChainUtils.SPLT + String.valueOf(nextTerm.getSegno()));
		nextNewNode.getNodeData().setStatus(VOStatus.NEW);

		return nextNewNode;
	}

	@SuppressWarnings("unchecked")
	private static SegRuleTermVO getNextSegRuleTerm(SegDetailVO lastNode, UFDouble nextHours) throws BusinessException {
		Collection<SegRuleTermVO> terms = getBaseDAO().retrieveByClause(SegRuleTermVO.class,
				"pk_segrule='" + lastNode.getPk_segrule() + "' and dr=0", "segno");
		SegRuleTermVO nextTerm = null;
		if (terms == null || terms.size() == 0) {
			throw new BusinessException("���������m���cʧ����δ�ҵ����O���ķֶ�Ҏ�t����");
		} else {
			boolean matchCur = false;
			SegRuleTermVO lastTerm = null;
			for (SegRuleTermVO term : terms) {
				if (matchCur) {
					nextTerm = term;
					break;
				}

				if (term.getPk_segruleterm().equals(lastNode.getPk_segruleterm())) {
					matchCur = true;
				}

				lastTerm = term;
			}

			if (nextTerm == null) {
				if (matchCur) {
					// ����һ�l����ƥ��ɹ�
					// �z������һ�l�����Ŀ�����
					if (lastTerm != null) {
						UFDouble start = lastTerm.getStartpoint();
						UFDouble end = lastTerm.getEndpoint() == null ? new UFDouble(24) : lastTerm.getEndpoint();
						if (end.sub(start).sub(lastNode.getRemainhours()).doubleValue() < nextHours.doubleValue()) {
							throw new BusinessException("���������m���cʧ�����Ӱ�ֶ�Ҏ�t����Ӱ��r��");
						}
						nextTerm = lastTerm;
					}
				} else {
					throw new BusinessException("���������m���cʧ����δ�ҵ����õķֶ�Ҏ�t����");
				}
			}
		}

		return nextTerm;
	}

	/**
	 * ȡ�����Ͼ����cCode��ȫһ�ӵĸ��������c�Ӱ��r��֮��
	 * 
	 * @param currentNode
	 * @return
	 */
	private static UFDouble getParentTotalHoursBySameCode(OTSChainNode currentNode) {
		UFDouble hours = currentNode.getNodeData().getHours();
		OTSChainNode curNode = currentNode;
		// �ϼ����c����գ����ϼ����c�Ĺ��c���a=�����n�ӵĹ��c���a
		while (curNode.getPriorNode() != null
				&& curNode.getPriorNode().getNodeData().getNodecode().equals(currentNode.getNodeData().getNodecode())) {
			hours = hours.add(curNode.getPriorNode().getNodeData().getHours());
			curNode = curNode.getPriorNode();
		}
		return hours;
	}

	/**
	 * ���Ҹ����c
	 * 
	 * @param originalNode
	 *            ԭʼ���c朱�
	 * @param checkNode
	 *            �z�鹝�c
	 * @return
	 * @throws BusinessException
	 */
	public static OTSChainNode findParentNode(OTSChainNode originalNode, OTSChainNode checkNode)
			throws BusinessException {
		if (originalNode == null) {
			return null;
		}
		String checkedNodeCode = checkNode.getNodeData().getNodecode();
		String[] checkedCodeList = checkedNodeCode.split(SPLT);
		OTSChainNode retNode = null;
		OTSChainNode curNode = getLastNode(originalNode);

		Integer originalDateType = ((SegRuleVO) getBaseDAO().retrieveByPK(SegRuleVO.class,
				originalNode.getNodeData().getPk_segrule())).getDatetype();
		Integer checkDateType = ((SegRuleVO) getBaseDAO().retrieveByPK(SegRuleVO.class,
				checkNode.getNodeData().getPk_segrule())).getDatetype();
		do {
			String curNodeCode = StringUtils.isEmpty(curNode.getNodeData().getNodecode()) ? "" : curNode.getNodeData()
					.getNodecode();
			String[] curCodeList = curNodeCode.split(SPLT); // 0:�T����1:���ڣ�2:�ֶ�����Code��3:�ֶ�����������̖

			if (originalDateType == 5 && checkDateType == 5) {
				if (curNodeCode.equals(checkedNodeCode)
						|| (curCodeList[0].equals(checkedCodeList[0]) && (curCodeList[1].compareTo(checkedCodeList[1]) == 0))) {
					retNode = curNode;
					break;
				}
			} else {
				// ���ό��ҵ�һ��NodeCode��ͬ������춙z�鹝�c
				if (curNodeCode.equals(checkedNodeCode)
						|| (curCodeList[0].equals(checkedCodeList[0]) && (curCodeList[1] + curCodeList[3])
								.compareTo(checkedCodeList[1] + checkedCodeList[3]) <= 0)) {
					retNode = curNode;
					break;
				}
			}

			// ��һ���c
			curNode = curNode.getPriorNode();
			// �߽������޸� tank ���ϲ������ 2019��8��12��15:44:26
		} while (curNode != null);
		return retNode;
	}

	/**
	 * ȡ���й��c�ļӰ�ֶ�����
	 * 
	 * @param node
	 *            ���c
	 * @return
	 * @throws BusinessException
	 */
	public static SegDetailVO[] getAllNodeData(OTSChainNode node) throws BusinessException {
		List<SegDetailVO> segDetailVOs = new ArrayList<SegDetailVO>();

		if (node != null) {
			OTSChainNode curNode = getFirstNode(node);
			do {
				segDetailVOs.add(curNode.getNodeData());
				curNode = curNode.getNextNode();
			} while (curNode != null);
		}

		return segDetailVOs.toArray(new SegDetailVO[0]);
	}

	public static OTSChainNode getCachedPsnChainNodes() {
		return cachedPsnChainNodes;
	}

	public static void setCachedPsnChainNodes(OTSChainNode cachedData) {
		cachedPsnChainNodes = cachedData;
	}

	public static SegRuleTermVO getSegRuleTerm(String pk_segRuleTerm) throws DAOException {
		return (SegRuleTermVO) getBaseDAO().retrieveByPK(SegRuleTermVO.class, pk_segRuleTerm);
	}
}
