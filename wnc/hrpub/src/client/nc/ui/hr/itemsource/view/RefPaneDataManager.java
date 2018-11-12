package nc.ui.hr.itemsource.view;

import java.awt.Component;

import nc.ui.pub.beans.UIRefPane;
import nc.vo.hr.itemsource.ItemPropertyConst;
import nc.vo.pub.SuperVO;

/**
 * @author: wh
 * @date: 2009-12-1 10:35:30
 * @since: eHR V6.0
 */
public abstract class RefPaneDataManager implements ITypeSourceComponentManager
{
    
    private String field;
    
    public RefPaneDataManager(String field)
    {
        this.field = field;
    }
    
    public void clearData(SuperVO item, Component comp)
    {
        
        ((UIRefPane) comp).setPK(null);
        
    }
    
    /**
     * @author wh on 2009-12-1
     * @see nc.ui.hr.itemsource.view.ITypeSourceComponentManager#getData(java.awt.Component,
     *      nc.vo.wa.item.SuperVO)
     */
    @Override
    public SuperVO getData(Component comp, SuperVO item)
    {
        String pk = ((UIRefPane) comp).getRefModel().getPkValue();
        String formula = getFunction(pk);
        item.setAttributeValue(ItemPropertyConst.VFORMULA, formula);
        item.setAttributeValue(ItemPropertyConst.VFORMULASTR, formula);
        item.setAttributeValue(ItemPropertyConst.IFROMFLAG, getFromType());
        return item;
    }
    
    public String getField()
    {
        return field;
    }
    
    public Integer getFromType()
    {
        if (ItemPropertyConst.PK_WA_WAGEFORM.equals(field))
        {
            return ItemPropertyConst.WA_WAGEFORM;
            
        }
        else if (ItemPropertyConst.PK_WA_GRADE.equals(field))
        {
            return ItemPropertyConst.WA_GRADE;
            
        } else if (ItemPropertyConst.PK_OTHERSOURCE.equals(field)) {
			return ItemPropertyConst.OTHERSOURCE;

		} 
        else
        {
            return ItemPropertyConst.USER_INPUT;
        }
    }
    
    protected abstract String getFunction(String pk);
    
    /**
     * @author wh on 2009-12-1
     * @see nc.ui.hr.itemsource.view.ITypeSourceComponentManager#setData(nc.vo.wa.item.SuperVO,
     *      java.awt.Component)
     */
    @Override
    public abstract void setData(SuperVO item, Component comp);
    
    public void setField(String field)
    {
        this.field = field;
    }
    
    /**
     * @author wh on 2009-12-14
     * @see nc.ui.hr.itemsource.view.ITypeSourceComponentManager#setPk_org(java.lang.String)
     */
    @Override
    public void setPk_org(String pk_org, Component comp)
    {
        ((UIRefPane) comp).getRefModel().setPk_org(pk_org);
    }
    
}
