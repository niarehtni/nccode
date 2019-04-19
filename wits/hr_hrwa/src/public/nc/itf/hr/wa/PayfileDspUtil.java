package nc.itf.hr.wa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.hr.utils.ResHelper;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.payfile.WaPayfileDspVO;

/**
 * н�ʵ�����ʾ���õĹ�����
 * @author erl
 *
 */
public class PayfileDspUtil {
	public static final String personalDsp = "1";
	public static final String commonDsp = "0";
	
	//н�ʵ���Ĭ����ʾ���ֶμ�˳��     code�͵���ģ���itemcode����һ��
	private final static String[] defaultShowColKey = new String[]{
		"pk_psndoc"/*��Ա����*/,
		"pk_psnjob"/*Ա����*/,
		"pk_psnjob.pk_psndoc.name"/*����*/,
		"pk_psnjob.pk_psncl"/*��Ա���*/,
		"workorgvid"/*��ְ��֯*/,
		"workdeptvid"/*����*/,
		"pk_psnjob.pk_post"/*��λ*/,
		"fiporgvid",/*������֯*/
		"fipdeptvid",/*������*/
		"taxorg",/*��˰�걨��֯*/
		"pk_liabilityorg",/*�ɱ�����*/
		"libdeptvid",/*�ɱ�����*/
		"taxtype",/*��˰��ʽ*/
		"taxtableid",/*˰�ʱ�*/
		"isndebuct",/*ʹ�ø��ӷ��ÿ۳���*/
		"isderate",/*����˰*/
		"derateptg",/*��˰����*/
		"stopflag",/*ͣ��*/
		"partflag",/*��ְ*/
		"pk_bankaccbas1",/*�����˺�*/
		"pk_banktype1",/*������������*/
		"pk_bankaccbas2",/*����1�˺�*/
		"pk_banktype2",/*����1��������*/
		"pk_bankaccbas3",/*����2�˺�*/
		"pk_banktype3"/*����2��������*/
	};
	

	/**
	 * ��getColMap()��ѯ���Ľ����ת����List<WaPayfileDspVO>
	 * 
	 * @return
	 */
	public static List<WaPayfileDspVO> getDefaultDsp() {
		List<WaPayfileDspVO> dspList = new ArrayList<WaPayfileDspVO>();
		Map<String,String> showNameMap = initShowNameMap();
		for(int i=0;i<defaultShowColKey.length;i++) {
			WaPayfileDspVO vo = new WaPayfileDspVO();
			vo.setItem_key(defaultShowColKey[i]);
			vo.setItemName(showNameMap.get(defaultShowColKey[i]));
			vo.setBshow(UFBoolean.TRUE);
			dspList.add(vo);
		}
		return dspList;
	}
	
	/**
	 * ���ñ�ͷ��������
	 * @param dspList
	 * @return
	 */
	public static List<WaPayfileDspVO> setPayfileDisplayName(List<WaPayfileDspVO> dspList) {
		Map<String,String> showNameMap = initShowNameMap();
		for (int i = 0; i < dspList.size(); i++) {
			dspList.get(i).setItemName(showNameMap.get(dspList.get(i).getItem_key()));
		}
		return dspList;
	}
	
	private static Map<String,String> initShowNameMap(){
		Map<String,String> nameMap = new HashMap<String, String>();
		nameMap.put("pk_psndoc", ResHelper.getString("common", "UC000-0000147")/*@res "��Ա���� */);
		nameMap.put("pk_psnjob", ResHelper.getString("60130payfile","060130payfile0333")/*@res "Ա����"*/);
        nameMap.put("pk_psnjob.pk_psndoc.name", ResHelper.getString("60130payfile","060130payfile0339")/*@res "����"*/);
        nameMap.put("pk_psnjob.pk_psncl", ResHelper.getString("60130payfile","060130payfile0340")/*@res "��Ա���"*/);
        nameMap.put("workorgvid", ResHelper.getString("60130payfile","160130payfile0009")/*@res "��ְ��֯"*/);
        nameMap.put("workdeptvid", ResHelper.getString("60130payfile","060130payfile0341")/*@res "����"*/);
        nameMap.put("pk_psnjob.pk_post", ResHelper.getString("60130payfile","060130payfile0342")/*@res "��λ"*/);
        nameMap.put("fiporgvid", ResHelper.getString("60130payfile","160130payfile0026")/*@res "������֯"*/);
        nameMap.put("fipdeptvid", ResHelper.getString("60130payfile","160130payfile0023")/*@res "������"*/);
        nameMap.put("pk_liabilityorg", ResHelper.getString("60130payfile","160130payfile0024")/*@res "�ɱ�����"*/);
        nameMap.put("libdeptvid", ResHelper.getString("60130payfile","160130payfile0025")/*@res "�ɱ�����"*/);
        nameMap.put("taxtype", ResHelper.getString("60130payfile","060130payfile0310")/*@res "��˰��ʽ"*/);
        nameMap.put("taxtableid", ResHelper.getString("60130payfile","060130payfile0343")/*@res "˰�ʱ�"*/);
        nameMap.put("isndebuct", ResHelper.getString("60130payfile","060130payfile0344")/*@res "ʹ�ø��ӷ��ÿ۳���"*/);
        nameMap.put("isderate", ResHelper.getString("60130payfile","160130payfile0005")/*@res "����˰"*/);
        nameMap.put("derateptg", ResHelper.getString("60130payfile","160130payfile0001")/*@res "��˰����"*/);
        nameMap.put("stopflag", ResHelper.getString("60130payfile","060130payfile0345")/*@res "ͣ��"*/);
        nameMap.put("partflag", ResHelper.getString("60130payfile","060130payfile0346")/*@res "��ְ"*/);
        nameMap.put("taxorg", "��˰�걨��֯");
        nameMap.put("pk_bankaccbas1", ResHelper.getString("60130payfile","160130payfile0006")/*@res "�����˺�"*/);
        nameMap.put("pk_banktype1", ResHelper.getString("60130payfile","160130payfile0011")/*@res "������������"*/);
        nameMap.put("pk_bankaccbas2", ResHelper.getString("60130payfile","160130payfile0008")/*@res "����1�˺�"*/);
        nameMap.put("pk_banktype2", ResHelper.getString("60130payfile","160130payfile0012")/*@res "����1��������"*/);
        nameMap.put("pk_bankaccbas3", ResHelper.getString("60130payfile","160130payfile0007")/*@res "����2�˺�"*/);
        nameMap.put("pk_banktype3", ResHelper.getString("60130payfile","160130payfile0010")/*@res "����2��������"*/);
		return nameMap;
	}

}
