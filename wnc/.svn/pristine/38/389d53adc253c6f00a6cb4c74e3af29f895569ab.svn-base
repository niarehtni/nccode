package nc.vo.wa.datainterface;

import java.util.Map;

import nc.vo.bd.bankaccount.BankAccbasVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.wa.category.WaClassVO;

public class ImpParamVO {
	private Map<String, HRDeptVO> codeDeptVOMap;
	private Map<String, PsndocVO> codePsnVOMap;
	private Map<String, PsndocVO> codeDeptPsnVOMap;
	private Map<String, BankAccbasVO> codeBankVOMap;
	private Map<String, MappingFieldVO> indexItemKeyMap;
	private Map<String, WaClassVO> codeWaclassVOMap;
	private int limitNum;
	private int startIndex = 0;
	private int count = 0; // ¼ÆÊýÆ÷

	public ImpParamVO() {

	}

	public ImpParamVO(Map<String, HRDeptVO> deptMap, Map<String, PsndocVO> psnMap, Map<String, PsndocVO> deptPsnMap,
			Map<String, BankAccbasVO> bankMap, Map<String, MappingFieldVO> mappMap, Map<String, WaClassVO> waclassMap,
			int limit, int index) {
		this.codeDeptVOMap = deptMap;
		this.codePsnVOMap = psnMap;
		this.codeDeptPsnVOMap = deptPsnMap;
		this.codeBankVOMap = bankMap;
		this.indexItemKeyMap = mappMap;
		this.codeWaclassVOMap = waclassMap;
		this.limitNum = limit;
		this.startIndex = index;
	}

	public Map<String, HRDeptVO> getCodeDeptVOMap() {
		return codeDeptVOMap;
	}

	public void setCodeDeptVOMap(Map<String, HRDeptVO> codeDeptVOMap) {
		this.codeDeptVOMap = codeDeptVOMap;
	}

	public Map<String, PsndocVO> getCodePsnVOMap() {
		return codePsnVOMap;
	}

	public void setCodePsnVOMap(Map<String, PsndocVO> codePsnVOMap) {
		this.codePsnVOMap = codePsnVOMap;
	}

	public Map<String, PsndocVO> getCodeDeptPsnVOMap() {
		return codeDeptPsnVOMap;
	}

	public void setCodeDeptPsnVOMap(Map<String, PsndocVO> codeDeptPsnVOMap) {
		this.codeDeptPsnVOMap = codeDeptPsnVOMap;
	}

	public Map<String, BankAccbasVO> getCodeBankVOMap() {
		return codeBankVOMap;
	}

	public void setCodeBankVOMap(Map<String, BankAccbasVO> codeBankVOMap) {
		this.codeBankVOMap = codeBankVOMap;
	}

	public Map<String, MappingFieldVO> getIndexItemKeyMap() {
		return indexItemKeyMap;
	}

	public void setIndexItemKeyMap(Map<String, MappingFieldVO> indexItemKeyMap) {
		this.indexItemKeyMap = indexItemKeyMap;
	}

	public Map<String, WaClassVO> getCodeWaclassVOMap() {
		return codeWaclassVOMap;
	}

	public void setCodeWaclassVOMap(Map<String, WaClassVO> codeWaclassVOMap) {
		this.codeWaclassVOMap = codeWaclassVOMap;
	}

	public int getLimitNum() {
		return limitNum;
	}

	public void setLimitNum(int limitNum) {
		this.limitNum = limitNum;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void countReset() {
		this.count = 0;
	}

	public void countIncrement() {
		this.count += 1;
	}

}
