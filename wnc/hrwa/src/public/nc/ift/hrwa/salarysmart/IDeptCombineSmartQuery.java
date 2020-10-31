package nc.ift.hrwa.salarysmart;

import nc.pub.smart.data.DataSet;
import nc.vo.pub.BusinessException;

public interface IDeptCombineSmartQuery {
	/**
	 * ���T�ρ�
	 * @param pk_org �M��
	 * @param pk_wa_class н�Y����
	 * @param startPeriod �_ʼ���g
	 * @param endPeriod �Y�����g
	 * @param source ��Դ(0,н�Y,1,��������)
	 * @param psnType �ˆTe IDL,DL
	 * @return
	 * @throws BusinessException
	 * 1.��С��λ�顸����(���T���e���70�ߣ�һ�����Ϻρ�����������o�����t�����Ϻρ�)���˄�������ᣬ���M���˔��ρ㡣
	 *  2.С춵��2�˵Ĳ��TҪ���Ϻρ㣬���2�˵Ĳ��T�t�������Ϻρ㣬������ԭ����λ�á�
	 *  3.DL��IDL���_�ρ� (�e��:��ԓ���TDL��25�ˡ�IDL��2�ˣ�IDLҪ���Ϻρ�)
	 *  4.ȫ���ρ���ᣬ�ρ���ı��У���������(���T���e��С�����)�z�飬����С춵��2�˵Ĳ��T���t���˔��c���~�ρ�����һ��ͬ��֮���T�У����T���a��С���˔�/���~�����0�Ĳ��T������һ�Ӳ��Tȫ������0���t������һ�ӡ�
	 */
	DataSet deptCombine(String pk_org,String pk_wa_class,String startPeriod,String endPeriod,Double source,String psnType) throws BusinessException;
}
