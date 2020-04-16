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
 * 加班分段明細雙向鏈表工具類
 * 
 * @author ssx
 * 
 */
public class OTSChainUtils {
	private static BaseDAO baseDAO = null;
	protected static String SPLT = "::";
	private static OTSChainNode cachedPsnChainNodes = null;

	/**
	 * 根據人員及加班類別創建現有記錄創建鏈表
	 * 
	 * @param pk_psndoc
	 *            人員PK
	 * @param otDate
	 *            加班日期
	 * @param pk_overtimereg
	 *            加班登記PK
	 * @param isForceComp
	 *            是否只建立轉調休記錄，True時只建立轉調休的節點
	 * @param isNoComp
	 *            是否只建立非轉調休記錄，True時建立除轉調休以外其他節點
	 * @param isForceNotCancel
	 *            是否只建立未作廢記錄，True時只建立未作廢的節點
	 * @param isForceNotConsumeFinished
	 *            是否只建立未核銷完畢的記錄，True時只建立未核銷完畢的節點
	 * @param isForceSettled
	 *            是否只建立未結算的記錄，True時只建立未結算的節點
	 * @return 鏈表頭節點
	 * 
	 * @throws BusinessException
	 */
	public static OTSChainNode buildChainNodes(String pk_psndoc, UFLiteralDate otDate, String pk_overtimereg,
			boolean isForceComp, boolean isNoComp, boolean isForceNotCancel, boolean isForceNotConsumeFinished,
			boolean isForceSettled) throws BusinessException {

		if (getCachedPsnChainNodes() != null) {
			return getCachedPsnChainNodes();
		}

		// 按人員取全節點
		List<Map<String, Object>> vodataList = retrieveSegDetailData(pk_psndoc, otDate, pk_overtimereg);

		OTSChainNode firstNode = null;
		List<String> pkList = new ArrayList<String>();
		if (vodataList != null && vodataList.size() > 0) {
			SegDetailVO[] sdList = getSegDetailVOsFromVOData(vodataList);
			for (SegDetailVO vo : sdList) {
				vo.setPk_segdetailconsume(getConsumeVO(vo.getPk_segdetail()));
				// 查找第一個節點
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
				// 構建鏈表
				for (SegDetailVO vo : sdList) {
					if (!pkList.contains(vo.getPk_segdetail())) {
						SegDetailVO childVO = getChildVO(sdList, curNode.getNodeData().getPk_segdetail());
						if (childVO != null) {
							OTSChainNode newNode = new OTSChainNode();
							newNode.setNodeData(childVO);
							appendNode(curNode, newNode, true);
							curNode = newNode;
						} else {
							break; // 一旦找不到子節點，認為鏈表已建立完畢
						}

						pkList.add(vo.getPk_segdetail());
					}
				}
			}
		}

		// 如果有過濾條件
		if (isForceComp || isForceNotCancel || isForceNotConsumeFinished || isForceSettled) {
			firstNode = filterNodes(isForceComp, isNoComp, isForceNotCancel, isForceNotConsumeFinished, isForceSettled,
					firstNode);
		}

		return firstNode;
	}

	/**
	 * 根據人員及加班類別創建現有記錄創建鏈表
	 * 
	 * @param pk_psndocs
	 *            人員PKs
	 * @param startDate
	 *            起始日期
	 * @param endDate
	 *            截止日期
	 * @param pk_overtimereg
	 *            加班登記PK
	 * @param isForceComp
	 *            是否只建立轉調休記錄，True時只建立轉調休的節點
	 * @param isNoComp
	 *            是否只建立非轉調休記錄，True時建立除轉調休以外其他節點
	 * @param isForceNotCancel
	 *            是否只建立未作廢記錄，True時只建立未作廢的節點
	 * @param isForceNotConsumeFinished
	 *            是否只建立未核銷完畢的記錄，True時只建立未核銷完畢的節點
	 * @param isForceSettled
	 *            是否只建立未結算的記錄，True時只建立未結算的節點
	 * @return 鏈表頭節點
	 * 
	 * @throws BusinessException
	 */
	public static Map<String, OTSChainNode> buildChainPsnNodeMap(String[] pk_psndocs, UFLiteralDate startDate,
			UFLiteralDate endDate, String pk_overtimereg, boolean isForceComp, boolean isNoComp,
			boolean isForceNotCancel, boolean isForceNotConsumeFinished, boolean isForceSettled)
			throws BusinessException {

		// 按人員取全節點
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
					// 構建鏈表
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
							break; // 一旦找不到子節點，認為鏈表已建立完畢
						}
					}
				}
			}
		}

		// 如果有過濾條件
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
	 * 按參數從物理鏈表中過濾出邏輯鏈表
	 * 
	 * @param isForceComp
	 *            是否只建立轉調休記錄，True時只建立轉調休的節點
	 * @param isNoComp
	 *            是否只建立非轉調休記錄，True時建立除轉調休外其他所有節點
	 * @param isForceNotCancel
	 *            是否只建立未作廢記錄，True時只建立未作廢的節點
	 * @param isForceNotConsumeFinished
	 *            是否只建立未核銷完畢的記錄，True時只建立未核銷完畢的節點
	 * @param isForceSettled
	 *            是否只建立未結算的記錄，True時只建立未結算的節點
	 * @param firstNode
	 *            第一個節點
	 * @return 第一個節點
	 * @throws BusinessException
	 */
	public static OTSChainNode filterNodes(boolean isForceComp, boolean isNoComp, boolean isForceNotCancel,
			boolean isForceNotConsumeFinished, boolean isForceSettled, OTSChainNode firstNode) throws BusinessException {
		if (firstNode != null) {
			OTSChainNode curNode = firstNode.clone(); // 克隆節點，以免因改動造成其他邏輯鏈表混亂
			OTSChainNode holdNode = curNode; // 保持返回用節點
			do {
				OTSChainNode tmpNextNode = curNode.getNextNode();
				if ((isForceComp && !curNode.getNodeData().getIscompensation().booleanValue() // 刪除非加班轉調休節點
						)
						|| (isForceNotCancel && curNode.getNodeData().getIscanceled().booleanValue() // 刪除已作廢節點
						) || (isForceNotConsumeFinished && curNode.getNodeData().getIsconsumed().booleanValue() // 刪除已核銷完畢節點
						) || (isForceSettled && curNode.getNodeData().getIssettled().booleanValue() // 刪除已結算節點
						) || (isNoComp && curNode.getNodeData().getIscompensation().booleanValue()) // 刪除轉調休節點
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
	 * 取子節點
	 * 
	 * @param sdList
	 *            節點列表
	 * @param pk_segdetail
	 *            當前節點PK
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
			// mod 用code和审批日期为key的方式区分分段的顺序 tank 2020年3月9日 00:11:28
			// TODO :同一個人，同一天，同一分段，審核日期都一樣场景下,分段顺序的区分
			String key = parentNodeCode + parentApproveddateStr;
			StringBuilder comparetorSB = new StringBuilder();

			for (OTSChainNode curNode : psnNodes) {

				comparetorSB.append(curNode.getNodeData().getNodecode()).append(
						curNode.getNodeData().getApproveddate().toStdString());
				if (comparetorSB.toString().compareTo(key) > 0) {
					// mod end 用code和审批日期为key的方式区分分段的顺序 tank 2020年3月9日 00:11:32
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
	 * 給定VO是否不存在上級節點
	 * 
	 * @param sdList
	 *            全節點
	 * @param vo
	 *            檢查節點
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
	 * 在指定節點後增加節點
	 * 
	 * @param targetNode
	 *            目標節點
	 * @param newNode
	 *            新增節點
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

			// 物理表中保存的是单向链表，所以对后续节点的改变不进行持久化
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
	 * 刪除指定節點的下一節點
	 * 
	 * @param targetNode
	 *            目標節點
	 * @param removeFromDB
	 *            是否從數據庫中刪除
	 * @throws BusinessException
	 */
	public static void removeNextNode(OTSChainNode targetNode, boolean removeFromDB) throws BusinessException {
		if (targetNode != null) {
			if (targetNode.getNextNode() != null) {
				removeCurrentNode(targetNode.getNextNode(), removeFromDB);
			} else {
				throw new BusinessException("節點刪除錯誤：當前節點後已無後繼節點。");
			}
		} else {
			throw new BusinessException("節點刪除錯誤：目標節點為空。");
		}
	}

	/**
	 * 刪除指定節點的前一節點
	 * 
	 * @param targetNode
	 *            目標節點
	 * @param removeFromDB
	 *            是否從數據庫中刪除
	 * @throws BusinessException
	 */
	public static void removePriorNode(OTSChainNode targetNode, boolean removeFromDB) throws BusinessException {
		if (targetNode != null) {
			if (targetNode.getPriorNode() != null) {
				removeCurrentNode(targetNode.getPriorNode(), removeFromDB);
			} else {
				throw new BusinessException("節點刪除錯誤：當前節點後已無前續節點。");
			}
		} else {
			throw new BusinessException("節點刪除錯誤：目標節點為空。");
		}
	}

	/**
	 * 刪除當前節點
	 * 
	 * @param targetNode
	 *            當前要刪除的節點
	 * @param removeFromDB
	 *            是否從數據庫中刪除
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
			// 刪除
			AggSegDetailVO aggvo = new AggSegDetailVO();
			aggvo.setParent(vo);

			Collection<SegDetailConsumeVO> lstChildVOs = getBaseDAO().retrieveByClause(SegDetailConsumeVO.class,
					"pk_segdetail='" + vo.getPk_segdetail() + "'");
			aggvo.setChildrenVO(lstChildVOs.toArray(new SegDetailConsumeVO[0]));
			new SegdetailMaintainImpl().delete(new AggSegDetailVO[] { aggvo });
		}
	}

	/**
	 * 根據給定節點查找第一個節點
	 * 
	 * @param node
	 *            節點
	 * @return 第一個節點
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
			// throw new BusinessException("獲取第一個節點錯誤：當前節點不能為空。");
			return node;
		}
	}

	/**
	 * 根據給定的節點查找最後一個節點
	 * 
	 * @param node
	 *            節點
	 * @return 最後一個節點
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
			// throw new BusinessException("獲取最後一個節點錯誤：當前節點不能為空。");
			return node;
		}
	}

	/**
	 * 保存所有節點的加班分段明細 （僅用於對SegDetailVO實體的操作，不處理消耗作業）
	 * 
	 * @param node
	 *            節點
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
			throw new BusinessException("全部保存錯誤：當前節點不能為空。");
		}
	}

	/**
	 * 單節點保存
	 * 
	 * @param node
	 *            當前保存的節點
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public static void save(OTSChainNode node) throws BusinessException {
		SegDetailVO vo = node.getNodeData();
		if (vo.getPk_segdetail() == null || VOStatus.NEW == vo.getStatus()) {
			// 新增
			AggSegDetailVO aggvo = new AggSegDetailVO();

			aggvo.setParent(vo);
			AggSegDetailVO[] ret = new SegdetailMaintainImpl().insert(new AggSegDetailVO[] { aggvo });
			vo.setPk_segdetail(ret[0].getPrimaryKey());
			// 修改flag,ts tank 2020年1月13日17:53:04
			vo.setStatus(VOStatus.UNCHANGED);
			vo.setTs(ret[0].getParentVO().getTs());
		} else if (vo.getPk_segdetail() != null && VOStatus.UPDATED == vo.getStatus()) {
			// 修改
			AggSegDetailVO aggvo = new AggSegDetailVO();
			aggvo.setParent(vo);

			Collection<SegDetailConsumeVO> lstChildVOs = getBaseDAO().retrieveByClause(SegDetailConsumeVO.class,
					"pk_segdetail='" + vo.getPk_segdetail() + "'");
			aggvo.setChildrenVO(lstChildVOs.toArray(new SegDetailConsumeVO[0]));
			AggSegDetailVO[] ret = new SegdetailMaintainImpl().update(new AggSegDetailVO[] { aggvo });
			// 修改flag,ts tank 2020年1月13日17:53:04
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
	 * 合併節點
	 * 
	 * @param originalNode
	 *            原始節點
	 * @param newNode
	 *            新節點
	 * @throws BusinessException
	 */
	public static OTSChainNode combineNodes(OTSChainNode originalNode, OTSChainNode newNode) throws BusinessException {
		if (newNode != null) {
			OTSChainNode curNode = null;
			OTSChainNode parentNode = null;

			// 按新節點遍歷
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

				// 查找父節點
				// 基本邏輯：查找NodeCode相同（人員，日期，分段號碼都相同）的節點
				// 或從鏈表末端往前回遡，出現的第一個NodeCode小於當前NodeCode的節點
				parentNode = findParentNode(originalNode, curNode);
				OTSChainNode addedNode = curNode.cloneSingle();
				if (parentNode == null) {
					// 不存在父節點，即鏈表中所有節點均比當前節點發生的晚，所以當前節點應為首節點
					addedNode.setNextNode(null);
					addedNode.setPriorNode(null);
					originalNode = OTSChainUtils.appendNode(addedNode, originalNode, false);
				} else {
					if (parentNode.getNodeData().getNodecode().equals(addedNode.getNodeData().getNodecode())) {
						OTSChainNode nextNewNode = null;
						// 節點編碼相同=請假人員、日期、分段完全一樣
						UFDouble ruleHours = parentNode.getNodeData().getRulehours(); // 分段規則定義的分段時長
						UFDouble parentHours = getParentTotalHoursBySameCode(parentNode); // 已佔用的規則時長
						UFDouble newHours = addedNode.getNodeData().getHours();
						if (parentHours.doubleValue() < ruleHours.doubleValue()) {
							// 父節點加班時長小於分段時長時=上次請假未請滿該分段，檢查分段纍計時長
							if (parentHours.add(newHours).doubleValue() <= ruleHours.doubleValue()) {
								// 纍加後仍然小於等於分段時長的，直接增加後續節點
								originalNode = appendNode(parentNode, addedNode, false);
							} else {
								// 纍加後超過分段時長的，將增補本段時長的後續節點
								UFDouble appendHours = ruleHours.sub(parentHours); // 未佔用的規則時長
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

								// 超過部分加班分估取後一段
								nextNewNode = getNextNewNode(addedNode, nextHours, nextHoursTaxfree, nextHoursTaxable);
								originalNode = combineNodes(originalNode, nextNewNode);
							}
						} else if (parentHours.doubleValue() == ruleHours.doubleValue()) {
							// 父節點加班時長等於分段時長=直接在當前節點上增加後續節點，加班分段要取後一段
							nextNewNode = getNextNewNode(addedNode, addedNode.getNodeData().getHours(), addedNode
									.getNodeData().getHourstaxfree(), addedNode.getNodeData().getHourstaxable());
							originalNode = combineNodes(originalNode, nextNewNode);
						}
					} else {
						// 節點編碼不相同，直接在當前節點上增加後續節點
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
			throw new BusinessException("創建新後續節點失敗：未找到已設定的分段規則明細");
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
					// 最後一條明細匹配成功
					// 檢查最後一條明細的可用性
					if (lastTerm != null) {
						UFDouble start = lastTerm.getStartpoint();
						UFDouble end = lastTerm.getEndpoint() == null ? new UFDouble(24) : lastTerm.getEndpoint();
						if (end.sub(start).sub(lastNode.getRemainhours()).doubleValue() < nextHours.doubleValue()) {
							throw new BusinessException("創建新後續節點失敗：加班分段規則不足加班時數");
						}
						nextTerm = lastTerm;
					}
				} else {
					throw new BusinessException("創建新後續節點失敗：未找到可用的分段規則明細");
				}
			}
		}

		return nextTerm;
	}

	/**
	 * 取所有上線節點Code完全一樣的各級父節點加班時數之和
	 * 
	 * @param currentNode
	 * @return
	 */
	private static UFDouble getParentTotalHoursBySameCode(OTSChainNode currentNode) {
		UFDouble hours = currentNode.getNodeData().getHours();
		OTSChainNode curNode = currentNode;
		// 上級節點不為空，且上級節點的節點編碼=本次纍加的節點編碼
		while (curNode.getPriorNode() != null
				&& curNode.getPriorNode().getNodeData().getNodecode().equals(currentNode.getNodeData().getNodecode())) {
			hours = hours.add(curNode.getPriorNode().getNodeData().getHours());
			curNode = curNode.getPriorNode();
		}
		return hours;
	}

	/**
	 * 查找父節點
	 * 
	 * @param originalNode
	 *            原始節點鏈表
	 * @param checkNode
	 *            檢查節點
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
			String[] curCodeList = curNodeCode.split(SPLT); // 0:員編，1:日期，2:分段依據Code，3:分段依據明細編號

			if (originalDateType == 5 && checkDateType == 5) {
				if (curNodeCode.equals(checkedNodeCode)
						|| (curCodeList[0].equals(checkedCodeList[0]) && (curCodeList[1].compareTo(checkedCodeList[1]) == 0))) {
					retNode = curNode;
					break;
				}
			} else {
				// 向上尋找第一個NodeCode相同或者早於檢查節點
				if (curNodeCode.equals(checkedNodeCode)
						|| (curCodeList[0].equals(checkedCodeList[0]) && (curCodeList[1] + curCodeList[3])
								.compareTo(checkedCodeList[1] + checkedCodeList[3]) <= 0)) {
					retNode = curNode;
					break;
				}
			}

			// 上一節點
			curNode = curNode.getPriorNode();
			// 边界条件修改 tank 最上层的条件 2019年8月12日15:44:26
		} while (curNode != null);
		return retNode;
	}

	/**
	 * 取所有節點的加班分段明細
	 * 
	 * @param node
	 *            節點
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
