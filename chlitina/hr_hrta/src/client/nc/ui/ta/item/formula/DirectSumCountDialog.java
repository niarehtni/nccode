package nc.ui.ta.item.formula;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.ta.IViewOrderQueryService;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIRadioButton;
import nc.ui.pub.beans.UITextField;
import nc.vo.pub.BusinessException;
import nc.vo.ta.item.ItemVO;
import nc.vo.ta.overtime.CalendarDateTypeEnum;
import nc.vo.ta.overtime.OvertimeSettleTypeEnum;
import nc.vo.ta.vieworder.ViewOrderVO;
import nc.vo.ta.vieworder.ViewOrderVOWithItemTypeName;

/**
 * 计次对话框，显示所有的字符型、布尔型项目
 * 计次的结果是，计数器碰到='Y'的情形就+1
 * @author zengcheng
 *
 */
public class DirectSumCountDialog extends AbstractDayItemDialog{
  private static final long serialVersionUID = -6810882341170250103L;
  private UIComboBox calendartypeComb = null;
  private UIComboBox cycleitemComb = null;
  public DirectSumCountDialog(Container parent)
  {
    super(parent);
    setTitle(ResHelper.getString("6017item", "06017item0091"));
  }
  

  protected DefaultFormBuilder getFormBuilder()
  {
	  
	  FormLayout layout = new FormLayout("right:pref, 2dlu, left:pref, 2dlu, right:pref, 2dlu, left:pref", "");
	    
	    DefaultFormBuilder builder = new DefaultFormBuilder(layout);
	    builder.append(ResHelper.getString("6017basedoc", "06017basedoc0001"), getCalendartypeComb());
	    builder.nextLine();
	    builder.append(ResHelper.getString("6017basedoc", "06017basedoc0002"), getCycleItemComb());
	    builder.nextLine();
	    return builder;
  }
  
  public String[] getPara()
  {
	  if( getCalendartypeComb().isVisible()==false  && getCycleItemComb().isVisible()==false){
			return null;
		}
      return getCalendarandcyclePara();
  }
  
  private String[] getCalendarandcyclePara() {
		String[] paras = new String[2];

		int calendarindex = getCalendartypeComb().getSelectedIndex();
		if (calendarindex == 0) {
			paras[0] = "NORMAL";
		} else
			if (calendarindex == 1) {
				paras[0] = "OFFDAY";
			} else
				if (calendarindex == 2) {
					paras[0] = "HOLIDAYS";
				} else {
					paras[0] = "NATIONALDAY";
				}
					
		int cycleindex = getCycleItemComb().getSelectedIndex();
		if(cycleindex == 0){
			paras[1] = "PERIOD_TOSALARY";
		}else if (cycleindex == 1){
			paras[1] = "OTHER_TOSALARY";
		}else if (cycleindex == 2){
			paras[1] = "PERIOD_TOREST";
		}else {
			paras[1] = "TOTAL";
		}
		

		return paras;
}


public String[] getParaStr()
  {
	if( getCalendartypeComb().isVisible()==false  && getCycleItemComb().isVisible()==false){
		return null;
	}
    return getCalendarParaStr();
  }
  
  private String[] getCalendarParaStr() {
	  String[] parasstr = new String[2];

		int calendarindex = getCalendartypeComb().getSelectedIndex();
		if (calendarindex == 0) {
			parasstr[0] = "平日";
		} else
			if (calendarindex == 1) {
				parasstr[0] = "休息日";
			} else
				if (calendarindex == 2) {
					parasstr[0] = "例假日";
				} else {
					parasstr[0] = "国定假日";
				}
					
		int cycleindex = getCycleItemComb().getSelectedIndex();
		if(cycleindex == 0){
			parasstr[1] = "本期加班转薪时数";
		}else if (cycleindex == 1){
			parasstr[1] = "往期加班转薪时数";
		}else if (cycleindex == 2){
			parasstr[1] = "本期加班转休时数";
		}else {
			parasstr[1] = "全部时数";
		}
	return parasstr;
}


protected UIComboBox getCalendartypeComb()
  {
    if (this.calendartypeComb == null) {
      getNewCalendarComb();
    }
    return this.calendartypeComb;
  }
  
  private void getNewCalendarComb(){
	  if (this.calendartypeComb == null) {
	      this.calendartypeComb = new UIComboBox();
	      this.calendartypeComb.setPreferredSize(new Dimension(150, this.calendartypeComb.getHeight()));
	    }
	    this.calendartypeComb.removeAllItems();
	   /* String[] ml = new String[4];
		ml[0] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6017item","2taitem-000033")@res "平日","UPPnc_hr_wa_pub1-000016"); -=notranslate=- 
		ml[1] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6017item","2taitem-000034")@res "休息日","UPPnc_hr_wa_pub1-000017"); -=notranslate=- 
		ml[2] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6017item","2taitem-000035")@res "例假日","UPPnc_hr_wa_pub1-000018"); -=notranslate=- 
		ml[3] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6017item","2taitem-000036")@res "国定假日","UPPnc_hr_wa_pub1-000019"); -=notranslate=- 

		String[] mlDefault = new String[]{ResHelper.getString("6017item","2taitem-000033")@res "平日",ResHelper.getString("6017item","2taitem-000034")@res "休息日",ResHelper.getString("6017item","2taitem-000035")@res "例假日",ResHelper.getString("6017item","2taitem-000036")@res "国定假日"}; -=notranslate=- 
		nc.hr.utils.PairFactory mPairFactory=new nc.hr.utils.PairFactory(ml, mlDefault );*/

		this.calendartypeComb.addItems(nc.md.model.impl.MDEnum.valueOfConstEnum(CalendarDateTypeEnum.class));
		//this.calendartypeComb.addItems(mPairFactory.getAllConstEnums());
}


protected UIComboBox getCycleItemComb()
  {
    if (this.cycleitemComb == null) {
      getNewCycleItemComb();
    }
    return this.cycleitemComb;
  }
  
  private void getNewCycleItemComb() {
	  if (this.cycleitemComb == null) {
	      this.cycleitemComb = new UIComboBox();
	      this.cycleitemComb.setPreferredSize(new Dimension(150, this.cycleitemComb.getHeight()));
	    }
	    this.cycleitemComb.removeAllItems();
	    /*String[] ml = new String[4];
		ml[0] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6017item","2taitem-000037")@res "本期加班（转薪）时数","UPPnc_hr_wa_pub1-000016"); -=notranslate=- 
		ml[1] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6017item","2taitem-000038")@res "往期加班（转薪）时数","UPPnc_hr_wa_pub1-000017"); -=notranslate=- 
		ml[2] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6017item","2taitem-000039")@res "本期加班（转休）时数","UPPnc_hr_wa_pub1-000018"); -=notranslate=- 
		ml[3] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6017item","2taitem-000040")@res "全部时数","UPPnc_hr_wa_pub1-000019"); -=notranslate=- 

		String[] mlDefault = new String[]{ResHelper.getString("6017item","2taitem-000037")@res "本期加班转薪时数",ResHelper.getString("6017item","2taitem-000038")@res "往期加班（转薪）时数",ResHelper.getString("6017item","2taitem-000039")@res "本期加班（转休）时数",ResHelper.getString("6017item","2taitem-000040")@res "全部时数"}; -=notranslate=- 
		nc.hr.utils.PairFactory mPairFactory=new nc.hr.utils.PairFactory(ml, mlDefault );*/

		this.cycleitemComb.addItems(nc.md.model.impl.MDEnum.valueOfConstEnum(OvertimeSettleTypeEnum.class));
	
}


public void actionPerformed(ActionEvent evt)
  {
   
    super.actionPerformed(evt);
  }


@Override
protected ViewOrderVO[] filter(ViewOrderVO[] paramArrayOfViewOrderVO) {
	// TODO Auto-generated method stub
	return null;
}
  

 
  
}
