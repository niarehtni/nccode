package com.ufida.report.free.plugin.formula;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.text.PlainDocument;

import nc.ui.pub.beans.UIComboBox;

import com.ufida.dataset.IContext;
import com.ufida.iufo.table.areafomula.NCDataFormatFunc;
import com.ufsoft.script.function.IufoRefProcessor;
import com.ufsoft.script.function.UfoFuncInfo;

/**
 * 
 * @author ward
 * @date 2018-06-05
 * @功能描述：薪资解密函数[SALARYDECRYPT]
 *
 */
public class AnaFormulaSalaryDecryptProcessor extends IufoRefProcessor {
	

	/* (non-Javadoc)
	 * @see com.ufsoft.script.function.IufoRefProcessor#doRefAction(com.ufida.dataset.IContext, com.ufsoft.script.function.UfoFuncInfo)
	 */
	@Override
	public void doRefAction(IContext context, UfoFuncInfo nFuncInfo) {
		// TODO Auto-generated method stub
	}
	
	public JPanel getRefPane (IContext context, UfoFuncInfo nFuncInfo, PlainDocument document){
		initPanel(document);
		this.context = context;
		this.nFuncInfo = nFuncInfo;
		inputPane = new JPanel(new BorderLayout());
		inputPane.setBorder(null);
		return inputPane;
	}

}
