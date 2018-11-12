/**
 * 
 */
package nc.pubitf.para;

import nc.vo.twhr.twhr_declaration.NonPartTimeBVO;
import nc.vo.twhr.twhr_declaration.BusinessBVO;
import nc.vo.twhr.twhr_declaration.CompanyBVO;
import nc.vo.twhr.twhr_declaration.DeclarationHVO;
import nc.vo.twhr.twhr_declaration.PartTimeBVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.twhr.twhr_declaration.AggDeclarationVO;

/**
 * @author Ares.Tank
 *
 */
public class DeclarationUtils {
	//主表的主键
	//public static final String KEY_OF_DECLARATION = "TWHRDECLAR0000000000";
	
	public static NonPartTimeBVO getNonPartTimeBVO(){
		NonPartTimeBVO newVO = new NonPartTimeBVO();
		
		
		return newVO;
	}
	public static BusinessBVO getBusinessBVO(){
		BusinessBVO newVO = new BusinessBVO();
		
		
		return newVO;
	}
	public static CompanyBVO getCompanyBVO(){
		CompanyBVO newVO = new CompanyBVO();
		
		
		return newVO;
	}
	public static PartTimeBVO getPartTimeBVO(){
		PartTimeBVO newVO = new PartTimeBVO();
		
		
		return newVO;
	}
	
	public static DeclarationHVO getDeclarationHVO(){
		DeclarationHVO newVO = new DeclarationHVO();
		newVO.setApprovestatus(-1);
		newVO.setTs(new UFDateTime());
		
		//newVO.setBillmaker("");
		
		return newVO;
	}
	
	public static AggDeclarationVO getAggDeclarationVO(){
		AggDeclarationVO newVO = new AggDeclarationVO();
		DeclarationHVO hvo = getDeclarationHVO();
		newVO.setParent(hvo);
		
		
		/*SuperVO[] svos= new SuperVO[1];
		svos[0] = getNonPartTimeBVO();
		newVO.setChildren(NonPartTimeBVO.class, svos);
		
		SuperVO[] svos1= new SuperVO[1];
		svos1[0] = getBusinessBVO();
		newVO.setChildren(BusinessBVO.class, svos1);
		
		SuperVO[] svos2= new SuperVO[1];
		svos2[0] = getCompanyBVO();
		newVO.setChildren(CompanyBVO.class, svos2);
		
		SuperVO[] svos3= new SuperVO[1];
		svos3[0] = getPartTimeBVO();
		newVO.setChildren(PartTimeBVO.class, svos3);*/
		return newVO;
	}
	
	
}


















