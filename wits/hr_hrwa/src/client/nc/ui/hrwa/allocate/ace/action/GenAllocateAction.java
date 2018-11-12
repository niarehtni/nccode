/**
 * @(#)GenAllocateAction.java 1.0 2017��9��19��
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
	/** �ļ�ѡ�п� */
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
		setBtnName(ResHelper.getString("allocate", "0allcate-ui-000002")); // ����Allocate
	}

	@Override
	public void doAction(ActionEvent evt) throws Exception {
		this.putValue("message_after_action", "");
		final Object[] datas = ((BillManageModel) getModel())
				.getSelectedOperaDatas();
		if (ArrayUtils.isEmpty(datas)) {
			ExceptionUtils.wrappBusinessException(ResHelper.getString(
					"projsalary", "0pjsalary-00024")
			/* @res "��ѡ��Ҫ���������ݣ�" */);
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
					dialog.setStartText("��������Allocate�n");
					dialog.start();

					IAllocateMaintain serive = NCLocator.getInstance().lookup(
							IAllocateMaintain.class);
					csvVOList = serive.transferToCsvInfo(datas);
					if (null == csvVOList || csvVOList.isEmpty()) {
						throw new BusinessException(ResHelper.getString(
								"allocate", "0allcate-ui-000015")
						/* @res "û��csv��������" */);
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
							/* @res "����ļ�����Ϊ�գ�" */);
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
									/* @res "Ҫ�������ļ��Ѵ��ڻ򵼳��ļ���·�������ƷǷ�������ִ����?" */)) {
								putValue(MESSAGE_AFTER_ACTION, null);
								message = "������ȡ��";
								isgen = false;
							} else {
								isgen = true;
							}
						} else {
							isgen = true;
						}

						if (isgen) {
							// ���½���ҵ�����
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
					ShowStatusBarMsgUtil.showErrorMsg("�a��Allocate�n�l���e�`", error,
							getModel().getContext());
				} else {
					if (message != null) {
						ShowStatusBarMsgUtil.showStatusBarMsg(message,
								getModel().getContext());
					} else {
						ShowStatusBarMsgUtil.showStatusBarMsg("����Allocate�n�ɹ�",
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
		// ��ѡ���ļ��������ͣ�׷��(Y)/����(N)?
		int returnval = JOptionPane.showConfirmDialog(listView,
				ResHelper.getString("syslog", "CSVAction-000000"),
				ResHelper.getString("syslog", "CSVAction-000001")/* CSV���� */,
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
				// ����ԭ�ļ�
				List<AllocateCsvVO> oldVOs = (List<AllocateCsvVO>) csvTool
						.readCsvFile(AllocateCsvVO.class, selectedFile,
								getCsvHeadInfo());
				// ���������
				List<AllocateCsvVO> validData = getValidData(oldVOs, csvVOList);
				oldVOs.addAll(validData);
				// д���ļ�
				writeBeansToCsv(AllocateCsvVO.class, oldVOs, selectedFile,
						getCsvHeadInfo());
			} else {
				// ��ǰdata�����ļ�
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
	 *            ����������ظ���¼��oldVOs
	 */
	protected List<AllocateCsvVO> getValidData(List<AllocateCsvVO> oldVOs,
			List<AllocateCsvVO> data) {
		List<AllocateCsvVO> validData = new ArrayList<AllocateCsvVO>();
		// �����ļ����Ѵ����ظ���¼������
		StringBuffer notValid = new StringBuffer(ResHelper.getString("syslog",
				"CSVAction-000007"));
		for (int i = 0; i < data.size(); i++) {
			boolean valid = true;
			for (int j = 0; j < oldVOs.size(); j++) {
				// �ظ�
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
					/* @res "CSV�ļ�(*.csv)" */;
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
				"account"); // �ܷ�����Ŀ
		headMap.put(ResHelper.getString("allocate", "0allcate-ui-000006"),
				"vouchmny"); // ƾ֤���ҽ��
		headMap.put(ResHelper.getString("allocate", "0allcate-ui-000007"),
				"costcenter"); // �ɱ�����
		headMap.put(ResHelper.getString("allocate", "0allcate-ui-000008"),
				"orderno"); // ������
		headMap.put(ResHelper.getString("allocate", "0allcate-ui-000009"),
				"share"); // ��̯
		headMap.put(ResHelper.getString("allocate", "0allcate-ui-000010"),
				"projtext"); // ��Ŀ�ı�
		headMap.put(ResHelper.getString("allocate", "0allcate-ui-000011"),
				"venderno"); // ��Ӧ�̱��
		return headMap;
	}

}
