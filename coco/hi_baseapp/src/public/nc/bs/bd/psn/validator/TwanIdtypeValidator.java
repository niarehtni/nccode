package nc.bs.bd.psn.validator;

import nc.bs.uif2.validation.ValidationFailure;
import nc.bs.uif2.validation.Validator;
import nc.vo.bd.psn.PsndocVO;

/**
 * <code>TwanIdtypeValidator<code>
 * <strong></strong>
 * <p>˵����
 * <li>̨�����֤֤����У����</li>
 * </p>
 * 
 * @since NC6.31
 * @version 2013-9-2 ����11:05:07
 * @author wushzh
 */
public class TwanIdtypeValidator implements Validator {

    private static final long serialVersionUID = -9178266794990275190L;

    int[] regionCode = new int[] {
        10, 11, 12, 13, 14, 15, 16, 17, 34, 18, 19, 20, 21, 22, 35, 23, 24, 25,
        26, 27, 28, 29, 32, 30, 31, 33
    };

    @Override
    public ValidationFailure validate(Object obj) {
        String msg = null;
        PsndocVO vo = (PsndocVO) obj;
        String psnid = vo.getId();
        // �ж�֤���ų��Ⱥ͸�ʽ�Ƿ���ȷ��ֻ�ܰ�����д��ĸ�����ֺͿո�
        msg = isFormatRightful(psnid);
        if (msg == null) {
            // ����У�����У�����һλУ��λ�Ƿ�Ϸ�
            msg = isLastoneRigthful(psnid);
        }
        if (msg != null) {
            ValidationFailure vf = new ValidationFailure(msg);
            return vf;
        }
        return null;
    }

    /**
     * 
     * <p>˵����
     * <li> �ж�֤���Ÿ�ʽ�Ƿ���ȷ��ֻ�ܰ�����д��ĸ�����ֺͿո�</li>
     * </p>
     * 
     * @param accnum
     * @return
     * @date 2013-9-1 ����08:45:47
     * @since NC6.31
     */
    private String isFormatRightful(String psnid) {
        // У�鳤���Ƿ�Ϊ10λ
        if (psnid.length() != 10) {
            return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                    "10140psn", "010140psn0122")/*
                                                   * @res
                                                   * "֤���ų��Ȳ���ȷ,̨�����֤֤����Ϊ10"
                                                   */;
        }
        for (int i = 0; i < psnid.length(); i++) {
            if (!((psnid.charAt(i) >= 'A' && psnid.charAt(i) <= 'Z')
                    || Character.isDigit(psnid.charAt(i)) || psnid.charAt(i) == ' ')) {
                return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                        "10140psn", "010140psn0123")/*
                                                       * @res
                                                       * "֤����ֻ�ܰ�����д��ĸ�����֡��ո�"
                                                       */;
            }
        }
        if (psnid.charAt(0) < 'A' || psnid.charAt(0) > 'Z') {
            return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                    "10140psn", "010140psn0125")/*
                                                   * @res
                                                   * "��һλΪ��д��ĸ,�ұ���Ϊ������"
                                                   */;
        }
        if (!((psnid.charAt(1) >= 'A' && psnid.charAt(1) <= 'D') || Character
                .isDigit(psnid.charAt(1)))) {
            return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                    "10140psn", "010140psn0126")/*
                                                   * @res
                                                   * "�ڶ�λΪ����,��Ϊ�Ա���A-D"
                                                   */;
        }
        return null;
    }

    /**
     * 
     * <p>˵����
     * <li>����У�����У�����һλУ��λ�Ƿ�Ϸ�</li>
     * </p>
     * 
     * @param accnum
     * @return
     * @date 2013-9-1 ����08:47:05
     * @since NC6.31
     */
    private String isLastoneRigthful(String psnid) {
        int[] ruleid = new int[] {
            1, 8, 7, 6, 5, 4, 3, 2, 1
        };
        int num = 0;
        // ��֤���ŵ�ǰ��λ������Ӧ������
        String rebuildId = rebuildId(psnid);
        // ��ǰ9λ�������γ��ԣ��������������������������������������ѳ˻����ȡ��
        for (int i = 0; i < ruleid.length; i++) {
            num += convertNum(rebuildId.charAt(i)) * ruleid[i];
        }
        // ȡ��
        int complement = 10 - num % 10;
        if (complement == 10)
            complement = 0;

        if (complement != convertNum(psnid.charAt(9))) {
            return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                    "10140psn", "010140psn0127")/* @res "֤����ת����ȡ��Ӧ�����һλУ��λ���" */;
        }
        return null;
    }

    /*
     * ��֤����psnid��ǰ��λ������ת��������
     */
    private String rebuildId(String psnid) {
        // ��psnid��һ����ĸת������Ӧ������
        String firstchar = convert2Num(psnid.charAt(0));
        int firstnum =
                (convertNum(firstchar.charAt(1)) * 9 + convertNum(firstchar
                        .charAt(0))) % 10;
        psnid =
                psnid.replaceFirst(psnid.substring(0, 1),
                        String.valueOf(firstnum));
        // ��psnid�ڶ�����ĸת������Ӧ������
        if (psnid.charAt(1) >= 'A' && psnid.charAt(1) <= 'D') {
            String secondchar = convert2Num(psnid.charAt(1));
            psnid =
                    psnid.replaceFirst(psnid.substring(1, 2),
                            secondchar.substring(1, 2));
        }
        return psnid;
    }

    /**
     * ����ĸת��������
     */
    private String convert2Num(char c) {
        int number = (int) c - 65;
        return Integer.toString(regionCode[number]);
    }

    /**
     * ����ĸת��������
     */
    private int convertNum(char c) {
        return (int) c - 48;
    }

}
