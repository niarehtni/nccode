package nc.ui.hr.tools.rtf.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.tree.DefaultMutableTreeNode;

import nc.hr.utils.ResHelper;
import nc.ui.hr.frame.HrQueryDialog;
import nc.ui.hr.tools.rtf.pub.QueryConditionTransformer;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.querytemplate.QueryConditionEditor;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.hr.tools.rtf.ConditionsOfType;
import nc.vo.hr.tools.rtf.TempletfieldVO;
import nc.vo.iufo.hr.PubEnv;
import nc.vo.pub.query.QueryTempletTotalVO;
import nc.vo.pub.query.QueryTempletVO;
import nc.vo.querytemplate.TemplateInfo;

import org.apache.commons.lang.StringUtils;

/**
 * �����������ü���
 * @author sunxj
 * @version 2009-12-29
 */
public class WordSetActionListener implements ActionListener
{
    
    private HrQueryDialog hrQryDlg;
    private boolean isInit = false;
    private ItemSetPnl wordSet;
    
    public WordSetActionListener(ItemSetPnl wordSet)
    {
        this.wordSet = wordSet;
    }
    
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        if (source == wordSet.getBtnSetCondition())
        {
            // ���ò�ѯ�����������ֶ�
            // ������ѯģ�壬����ѯ��������conditionMap��������һ��־����word�ĵ��������ֶη���word�ĵ�
            onSetCondition();
        }
    }
    
    private void addCondition(TempletfieldVO tv, String whereSql, String sqlDscrpt)
    {
        ConditionsOfType conditions = new ConditionsOfType();
        conditions.setTableCode(tv.getTable_Code());
        conditions.setTableName(tv.getTable_Name());
        conditions.setFieldCode(tv.getField_code());
        conditions.setFieldName(tv.getField_name());
        conditions.setWhereSql(whereSql);
        conditions.setSqlDscrpt(sqlDscrpt);
        
        // String randomKeyCondition = new UUID().toString();
        String strField = tv.getField_code();
        int index = strField.indexOf(".");
        String strCode = strField.substring(index + 1, strField.length());
        String randomKeyCondition = tv.getTable_Code() + "_" + strCode;
        if(wordSet.getConditionMap().containsKey(randomKeyCondition)){
        	randomKeyCondition += PubEnv.getServerDate().getMillis();
        }
        wordSet.getConditionMap().put(randomKeyCondition, conditions);
        String displayName = tv.getTable_Name() + "." + tv.getField_name();
        String nameCondition = /* "@" + */displayName + ResHelper.getString("6001rtf", "06001rtf0038")
        /* "��������" */;
        String strFieldRefCondition = ResHelper.getString("6001rtf", "06001rtf0039")
        /* @res "����->" */+ sqlDscrpt; // + "\n  ����->" +
        // getOrderStr();
        String strFieldRef = "Filter." + randomKeyCondition;
        // String ymdStr = getYMDSelect();
        // if (ymdStr.length() > 0)
        // {
        // strFieldRefCondition += ymdStr + "}";
        // }
        // else
        // {
        // strFieldRefCondition += "}";
        // }
        
        String strShowName = "";
        String name = nameCondition;
        
        // ���ڸ�ʽ
        if (wordSet.getCombobobx_dateformat().isEnabled() && wordSet.getCombobobx_dateformat().getSelectedIndex() >= 0)
        {
            strFieldRef =
                strFieldRef + "(" + wordSet.getCombobobx_dateformat().getSelectdItemValue() + "+"
                    + wordSet.getCombobobx_dateformat().getSelectedIndex() + ")";
            name =
                name + "(" + wordSet.getCombobobx_dateformat().getSelectdItemValue() + "+"
                    + wordSet.getCombobobx_dateformat().getSelectedIndex() + ")";
        }
        // ǰ׺
        if (wordSet.getTfBeforeContent().getText() != null && !"".equals(wordSet.getTfBeforeContent().getText()))
        {
            strFieldRef = "'" + wordSet.getTfBeforeContent().getText() + "'+" + strFieldRef;
            name = "'" + wordSet.getTfBeforeContent().getText() + "'+" + name;
        }
        // ��׺
        if (wordSet.getTfAfterContent().getText() != null && !"".equals(wordSet.getTfAfterContent().getText()))
        {
            strFieldRef = strFieldRef + "+'" + wordSet.getTfAfterContent().getText() + "'";
            name = name + "+'" + wordSet.getTfAfterContent().getText() + "'";
        }
        
        strShowName = strShowName + strFieldRefCondition + "{" + strFieldRef + "}";
        name = "@" + name + "@";
        addRef(name, name, strShowName);
        // addRef(nameCondition, nameCondition, strFieldRefCondition);
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
        
    }
    
    private QueryConditionEditor getHrQryConditionEditor()
    {
        getHrQryDlg().getSimpleEditorFilterEditors();
        return getHrQryDlg().getQryCondEditor();
    }
    
    private HrQueryDialog getHrQryDlg()
    {
        if (!isInit)
        {
            // hrQryDlg = new HrQueryDialog(wordSet, new TemplateInfo());
            hrQryDlg = new WordConditionQueryDialog(wordSet, new TemplateInfo(), wordSet.getContext());
            hrQryDlg.setTitle(ResHelper.getString("6001rtf", "06001rtf0040")
            /* @res "���ù�������" */);
            QueryConditionEditor editor = hrQryDlg.getQryCondEditor();
            // editor.setVisibleFavoritePanel(false);
            editor.setVisibleQSTreePanel(false);
            editor.setMultiTable(false);
            isInit = true;
        }
        return hrQryDlg;
    }
    
    private InfoSetVO getSelectInfosetVO()
    {
        return (InfoSetVO) ((DefaultMutableTreeNode) wordSet.getTreeContent().getLastSelectedPathComponent()).getUserObject();
    }
    
    private void onSetCondition()
    {
        
        QueryTempletTotalVO qtTotalVO = new QueryTempletTotalVO();
        QueryTempletVO tVO = new QueryTempletVO();
        qtTotalVO.setTemplet(tVO);
        qtTotalVO.setConditionVOs(QueryConditionTransformer.InfosetVO2QueryConditionVO(getSelectInfosetVO()));
        getHrQryConditionEditor().setTotalVO(qtTotalVO);
        
        if (wordSet.getCurVar() == null)
        {
            MessageDialog.showHintDlg(wordSet, null, ResHelper.getString("6001rtf", "06001rtf0041")
            /* @res "��ѡ����Ϣ�" */);
            return;
        }
        TempletfieldVO tv = (TempletfieldVO) wordSet.getCurVar().getObject();
        
        if (getHrQryDlg().showModal() == UIDialog.ID_OK)
        {
            // ��ѯ����
            // ���API��ƽ̨ȥ����getHrQryConditionEditor().getWhereSqlWithoutPower()
            // ���ھ���ѯȷ�ϸ�Ϊ getHrQryConditionEditor().getWhereSql()���Ժ�ģ�鸺�����ٸ��������ϸ����һ���Ƿ����
            String condition = getHrQryConditionEditor().getWhereSql();
            if ("hi_psnjob".equalsIgnoreCase(getSelectInfosetVO().getTable_code())
                && getSelectInfosetVO().getPk_infoset_sort().equals("1001Z710000000002XPO") && StringUtils.isNotBlank(condition))
            {
                // ��Ա��Ƭ�����������������
                HashSet<String> tabName = getHrQryConditionEditor().getUsedTableName();
                
                String fromPart = " hi_psnjob ";
                if (tabName.contains("hi_psnorg"))
                {
                    fromPart += " inner join hi_psnorg on hi_psnjob.pk_psnorg = hi_psnorg.pk_psnorg ";
                }
                
                condition = " hi_psnjob.pk_psnjob in ( select pk_psnjob from " + fromPart + " where " + condition + " ) ";
            }
            addCondition(tv, condition, getHrQryConditionEditor().getSqlDscrpt());
        }
        isInit = false;
    }
}
