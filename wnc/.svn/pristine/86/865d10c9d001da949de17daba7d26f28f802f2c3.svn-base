package nc.ui.hr.tools.rtf.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import nc.hr.utils.ResHelper;
import nc.vo.hr.tools.rtf.TempletfieldVO;
import nc.vo.pub.lang.UFBoolean;

/**
 * 鼠标监听类
 * @author sunxj
 * @version 2009-12-29
 */
public class WordSetMouseListener extends MouseAdapter
{
    
    private ItemSetPnl wordSet;
    
    public WordSetMouseListener(ItemSetPnl wordSet)
    {
        this.wordSet = wordSet;
    }
    
    public void addRef(String name, String strRef, String tipTxet)
    {
        if (wordSet.getWord() != null)
        {
            wordSet.getWord().appendItem(name, strRef, tipTxet);
            wordSet.getWord().active();
        }
        
        if (wordSet.getWordShowPanel() != null)
        {
            wordSet.getWordShowPanel().appendItem(name, strRef, tipTxet);
        }
        // if(wordSet.getWord2() != null){
        // wordSet.getWord2().appendItem(name, strRef, tipTxet);
        // }
        
    }
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
        {
            if (e.getSource() == wordSet.getListSysItem())
            {
                // 系统变量
                wordSet.setCurVar((ListNode) wordSet.getListSysItem().getSelectedValue());
            }
            else if (e.getSource() == wordSet.getTableProp())
            {
                // 信息项改变
                int row = wordSet.getTableProp().getSelectedRow();
                if (row >= 0)
                {
                    int column = 0;
                    wordSet.setCurVar((ListNode) wordSet.getTableProp().getValueAt(row, column));
                }
            }
            onAdd();
        }
    }
    
    private void onAdd()
    {
        
        ListNode oSelected = wordSet.getCurVar();// 双击选中表格的数据
        
        int intNo = wordSet.getSpinbox_order().getValue();
        
        UFBoolean flag = UFBoolean.TRUE;
        
        UFBoolean preFlag = UFBoolean.TRUE;
        if (oSelected == null)
        {
            return;
        }
        
        if (oSelected.getObject() instanceof TempletfieldVO)
        {
            // 拼接需要的内容
            TempletfieldVO tv = (TempletfieldVO) oSelected.getObject();
            String name = tv.getField_name();
            /** 如果是单一模式 */
            String strFieldRef = tv.getField_code().toLowerCase();// 选中item的PK
            if (strFieldRef.indexOf(".") < 0)
            {
                strFieldRef = "system." + strFieldRef;
            }
            
            String strShowName = "";
            
            // 条件定位选择
            if (wordSet.getRdoBtn_order().isEnabled() && wordSet.getRdoBtn_order().isSelected())
            {
                String selectName = (String) wordSet.getCombobox_order().getSelectdItemValue();
                String preStr = null;
                if (ResHelper.getString("6001rtf", "06001rtf0016")
                /* @res "最近第" */.equals(selectName))
                {
                    preStr = "";
                }
                else if (ResHelper.getString("6001rtf", "06001rtf0017")
                /* @res "最近" */.equals(selectName))
                {
                    preStr = "0:";
                }
                else if (ResHelper.getString("6001rtf", "06001rtf0018")
                /* @res "最初第" */.equals(selectName))
                {
                	preFlag = UFBoolean.FALSE;
                    preStr = "-";
                }
                else if (ResHelper.getString("6001rtf", "06001rtf0019")
                /* @res "最初" */.equals(selectName))
                {
                	preFlag = UFBoolean.FALSE;
                    preStr = "0:-";
                }
                //int intNo = wordSet.getSpinbox_order().getValue();
                // strShowName = tv.getTable_Name() + "." + tv.getField_name();
                flag = UFBoolean.FALSE;
                if (intNo > 0)
                {
                    // strShowName=strShowName+ "[" +selectName+ intNo + "条]";
                    strFieldRef = strFieldRef + "[" + preStr + intNo + "]";
                    name = name + "[" + selectName + intNo + ResHelper.getString("6001rtf", "06001rtf0047")
                    /* @res "条]" */;
                }
            }
            // 日期格式
            if (wordSet.getCombobobx_dateformat().isEnabled() && wordSet.getCombobobx_dateformat().getSelectedIndex() >= 0)
            {
                // strShowName =strShowName+ "("
                // +wordSet.getCombobobx_dateformat().getSelectdItemValue()+
                // ")";
//                strFieldRef =
//                    strFieldRef + "(" + wordSet.getCombobobx_dateformat().getSelectdItemValue() + "+"
//                        + wordSet.getCombobobx_dateformat().getSelectedIndex() + ")";
//                name =
//                    name + "(" + wordSet.getCombobobx_dateformat().getSelectdItemValue() + "+"
//                        + wordSet.getCombobobx_dateformat().getSelectedIndex() + ")";
            	//日期格式处理，否则无法显示
            	 strFieldRef =
                         strFieldRef + "(1977-08-28+"
                             + wordSet.getCombobobx_dateformat().getSelectedIndex() + ")";
                     name =
                         name + "(1977-08-28+"
                             + wordSet.getCombobobx_dateformat().getSelectedIndex() + ")";
            }
            // 前缀
            if (wordSet.getTfBeforeContent().getText() != null && !"".equals(wordSet.getTfBeforeContent().getText()))
            {
                // strShowName = "'"+wordSet.getTfBeforeContent().getText()+
                // strShowName;
                // strShowName =
                // "'"+wordSet.getTfBeforeContent().getText()+"'+"+ strShowName;
                strFieldRef = "'" + wordSet.getTfBeforeContent().getText() + "'+" + strFieldRef;
                name = "'" + wordSet.getTfBeforeContent().getText() + "'+" + name;
            }
            // 后缀
            if (wordSet.getTfAfterContent().getText() != null && !"".equals(wordSet.getTfAfterContent().getText()))
            {
                // strShowName = strShowName
                // +"+'"+wordSet.getTfAfterContent().getText()+"'";
                // strShowName = strShowName
                // +wordSet.getTfAfterContent().getText()+"'";
                strFieldRef = strFieldRef + "+'" + wordSet.getTfAfterContent().getText() + "'";
                name = name + "+'" + wordSet.getTfAfterContent().getText() + "'";
            }
            // 子集显示行数
            if (wordSet.getTfRowno().isEnabled() && wordSet.getTfRowno().getText() != null && !"".equals(wordSet.getTfRowno().getText()))
            {
                strFieldRef = strFieldRef + "-'" + wordSet.getTfRowno().getText() + "'";
                name = name + "-'" + wordSet.getTfRowno().getText() + "'";
            }
            strShowName = strShowName + "{" + strFieldRef + "}";
            name = "@" + name + "@";
            if(!flag.booleanValue()){
            	String[] aa = strShowName.split("\\.");
            	if(preFlag.booleanValue()){
            		strShowName = aa[0]+"new"+intNo+"\\."+aa[1];
            	}else{
            		strShowName = aa[0]+"old"+intNo+"\\."+aa[1];
            	}
                addRef(name, name, strShowName);
            }else{
            	addRef(name, name, strShowName);
            }
            
        }
    }
}
