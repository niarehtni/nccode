/**
 * @(#)GenAllocateAction.java 1.0 2017年9月19日
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.ui.hrwa.allocate.ace.action;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.ResHelper;
import nc.itf.hrwa.IAllocateMaintain;
import nc.itf.uap.IUAPQueryBS;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.sm.busilog.csv.BeanToCsv;
import nc.ui.sm.busilog.csv.CSVTool;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.allocate.AllocateCsvVO;
import nc.vo.wa.pub.WaLoginContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings({ "serial", "restriction" })
public class GenAllocateAction extends HrAction {
	private BillListView listView;
	/** 文件选中框 */
	public UIFileChooser fileChooser;
	public String strFileName;
	List<AllocateCsvVO> csvVOList;

	public BillListView getListView() {
		return listView;
	}

	public void setListView(BillListView listView) {
		this.listView = listView;
	}

	public GenAllocateAction() {
		setCode("genAllocate");
		setBtnName(ResHelper.getString("allocate", "0allcate-ui-000002")); // 生成Allocate
	}

	@Override
	public void doAction(ActionEvent evt) throws Exception {
		this.putValue("message_after_action", "");
		final Object[] datas = ((BillManageModel) getModel())
				.getSelectedOperaDatas();
		if (ArrayUtils.isEmpty(datas)) {
			ExceptionUtils.wrappBusinessException(ResHelper.getString(
					"projsalary", "0pjsalary-00024")
			/* @res "请选择要导出的数据！" */);
			return;
		}

		new SwingWorker() {
			BannerTimerDialog dialog = new BannerTimerDialog(
					SwingUtilities.getWindowAncestor(getModel().getContext()
							.getEntranceUI()));
			String error = null;
			String message = null;

			protected Boolean doInBackground() throws Exception {
				try {
					dialog.setStartText("正在生成Allocate檔");
					dialog.start();

					IAllocateMaintain serive = NCLocator.getInstance().lookup(
							IAllocateMaintain.class);
					csvVOList = serive.transferToCsvInfo(datas);
					if (null == csvVOList || csvVOList.isEmpty()) {
						throw new BusinessException(ResHelper.getString(
								"allocate", "0allcate-ui-000015")
						/* @res "没有csv导出数据" */);
					}

					WaLoginContext context = (WaLoginContext) getContext();
					OrgVO orgVO = (OrgVO) NCLocator.getInstance()
							.lookup(IUAPQueryBS.class)
							.retrieveByPK(OrgVO.class, context.getPk_org());
					StringBuilder fileName = new StringBuilder();
					fileName.append(context.getCyear()).append(
							context.getCperiod());
					fileName.append(MultiLangHelper.getName(orgVO));
					fileName.append("allocate.csv");

					fileChooser = getFileChooser();
					fileChooser.setSelectedFile(new File(fileName.toString()));
					int returnVal = fileChooser.showSaveDialog(getEntranceUI());
					if (returnVal != JFileChooser.APPROVE_OPTION) {
						putValue(MESSAGE_AFTER_ACTION,
								IShowMsgConstant.getCancelInfo());
					} else {
						strFileName = fileChooser.getSelectedFile().getPath();
						if (StringUtils.isBlank(strFileName)) {
							throw new BusinessException(ResHelper.getString(
									"projsalary", "0pjsalary-00025")
							/* @res "输出文件不能为空！" */);
						}
						FileFilter fileFilter = fileChooser.getFileFilter();

						if (fileFilter != null) {
							if (fileFilter.getDescription().contains(".csv")
									&& !strFileName.toUpperCase().endsWith(
											"CSV")) {
								strFileName = strFileName + ".csv";
							}
						}
						if (!strFileName.toUpperCase().endsWith("CSV")) {
							strFileName = strFileName + ".csv";
						}
						File file = new File(strFileName);
						boolean isgen = false;
						if (file.exists()) {
							if (MessageDialog.ID_YES != MessageDialog
									.showYesNoDlg(getEntranceUI(), null,
											ResHelper.getString("projsalary",
													"0pjsalary-00026")
									/* @res "要导出的文件已存在或导出文件的路径、名称非法，继续执行吗?" */)) {
								putValue(MESSAGE_AFTER_ACTION, null);
								message = "生成已取消";
								isgen = false;
							} else {
								isgen = true;
							}
						} else {
							isgen = true;
						}

						if (isgen) {
							// 以下进行业务操作
							doExport(csvVOList);
						}
					}

				} catch (LockFailedException le) {
					error = le.getMessage();
				} catch (VersionConflictException le) {
					throw new BusinessException(le.getBusiObject().toString(),
							le);
				} catch (Exception e) {
					error = e.getMessage();
				} finally {
					dialog.end();
				}
				return Boolean.TRUE;
			}

			protected void done() {
				if (error != null) {
					ShowStatusBarMsgUtil.showErrorMsg("產生Allocate檔發生錯誤", error,
							getModel().getContext());
				} else {
					if (message != null) {
						ShowStatusBarMsgUtil.showStatusBarMsg(message,
								getModel().getContext());
					} else {
						ShowStatusBarMsgUtil.showStatusBarMsg("生成Allocate檔成功",
								getModel().getContext());
					}
				}
			}
		}.execute();
	}

	@Override
	protected boolean isActionEnable() {
		Object[] datas = ((BillManageModel) getModel()).getSelectedOperaDatas();
		boolean enable = (getModel().getUiState() == UIState.NOT_EDIT || getModel()
				.getUiState() == UIState.INIT) && !ArrayUtils.isEmpty(datas); // &&
																				// ((WaLoginContext)
																				// getContext()).isContextNotNull();
		return super.isActionEnable() && enable;
	}

	@SuppressWarnings({ "unchecked", "static-access" })
	protected void doExport(List<AllocateCsvVO> csvVOList) {
		// 请选择文件导出类型：追加(Y)/覆盖(N)?
		int returnval = JOptionPane.showConfirmDialog(listView,
				ResHelper.getString("syslog", "CSVAction-000000"),
				ResHelper.getString("syslog", "CSVAction-000001")/* CSV导出 */,
				JOptionPane.YES_NO_CANCEL_OPTION);
		boolean append = true;
		if (returnval == 0) {
			append = true;
		} else if (returnval == 1) {
			append = false;
		} else {
			return;
		}

		File selectedFile = new File(strFileName);
		CSVTool csvTool = new CSVTool();
		try {
			if (append) {
				// 读入原文件
				List<AllocateCsvVO> oldVOs = (List<AllocateCsvVO>) csvTool
						.readCsvFile(AllocateCsvVO.class, selectedFile,
								getCsvHeadInfo());
				// 添加新数据
				List<AllocateCsvVO> validData = getValidData(oldVOs, csvVOList);
				oldVOs.addAll(validData);
				// 写入文件
				writeBeansToCsv(AllocateCsvVO.class, oldVOs, selectedFile,
						getCsvHeadInfo());
			} else {
				// 当前data覆盖文件
				writeBeansToCsv(AllocateCsvVO.class, csvVOList, selectedFile,
						getCsvHeadInfo());
			}
		} catch (Exception e) {
			ExceptionUtils.wrappException(e);
		}

	}

	/**
	 * @param oldVOs
	 * @param data
	 *            添加主键不重复记录至oldVOs
	 */
	protected List<AllocateCsvVO> getValidData(List<AllocateCsvVO> oldVOs,
			List<AllocateCsvVO> data) {
		List<AllocateCsvVO> validData = new ArrayList<AllocateCsvVO>();
		// 导出文件中已存在重复记录，如下
		StringBuffer notValid = new StringBuffer(ResHelper.getString("syslog",
				"CSVAction-000007"));
		for (int i = 0; i < data.size(); i++) {
			boolean valid = true;
			for (int j = 0; j < oldVOs.size(); j++) {
				// 重复
				if (data.get(i).getUnionID().equals(oldVOs.get(j).getUnionID())) {
					valid = false;
					break;
				}
			}
			if (valid) {
				validData.add(data.get(i));
			} else {
				notValid.append("[").append(data.get(i).getUnionID())
						.append("]").append(",");
			}
		}
		if (notValid.toString().contains(",")) {
			ExceptionUtils.wrappBusinessException(notValid.substring(0,
					notValid.length() - 1));
		}
		return validData;
	}

	public UIFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new UIFileChooser();
			fileChooser.setFileFilter(new FileFilter() {
				String ext = ".csv";

				@Override
				public boolean accept(File f) {
					if (f.isDirectory()) {
						return true;
					}
					if (((f.getName().toLowerCase()).endsWith(ext))) {
						return true;
					} else {
						return false;
					}
				}

				@Override
				public String getDescription() {
					return ResHelper
							.getString("allocate", "0allcate-ui-000012")
					/* @res "CSV文件(*.csv)" */;
				}

			});
		}
		return fileChooser;
	}

	protected void writeBeansToCsv(Class<?> target, List<?> vos, File file,
			Map<String, String> colMapping) throws IOException {
		Writer writer = null;
		try {
			String enCode = "UTF-8";
			// String langCode =
			// InvocationInfoProxy.getInstance().getLangCode().toLowerCase();
			// if (langCode.equals("simpchn")) {
			// enCode = "gb2312";
			// } else if (langCode.equals("tradchn")) {
			// enCode = "big5";
			// }
			// writer = new FileWriter(file);
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), enCode));
			// writer.write(new String(new byte[] { (byte) 0xEF, (byte)
			// 0xBB,(byte) 0xBF }));
			BeanToCsv btc = new BeanToCsv();
			btc.setColMapping(colMapping);
			btc.write(target, vos, writer, (String[]) colMapping.values()
					.toArray(new String[0]));
			writer.close();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			Logger.error(e);
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
				Logger.error(e);
			}
		}
	}

	protected Map<String, String> getCsvHeadInfo() {
		Map<String, String> headMap = new LinkedHashMap<String, String>();
		headMap.put(ResHelper.getString("allocate", "0allcate-ui-000005"),
				"account"); // 总分类账目
		headMap.put(ResHelper.getString("allocate", "0allcate-ui-000006"),
				"vouchmny"); // 凭证货币金额
		headMap.put(ResHelper.getString("allocate", "0allcate-ui-000007"),
				"costcenter"); // 成本中心
		headMap.put(ResHelper.getString("allocate", "0allcate-ui-000008"),
				"orderno"); // 订单号
		headMap.put(ResHelper.getString("allocate", "0allcate-ui-000009"),
				"share"); // 分摊
		headMap.put(ResHelper.getString("allocate", "0allcate-ui-000010"),
				"projtext"); // 项目文本
		headMap.put(ResHelper.getString("allocate", "0allcate-ui-000011"),
				"venderno"); // 供应商编号
		return headMap;
	}

}
