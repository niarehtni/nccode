package nc.bs.bd.psn.validator;

import nc.bs.uif2.validation.ValidationFailure;
import nc.bs.uif2.validation.Validator;
import nc.vo.bd.psn.PsndocVO;

/**
 * <code>TwanIdtypeValidator<code>
 * <strong></strong>
 * <p>说明：
 * <li>台湾身份证证件号校验类</li>
 * </p>
 * 
 * @since NC6.31
 * @version 2013-9-2 上午11:05:07
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
        // 判断证件号长度和格式是否正确：只能包含大写字母、数字和空格
        msg = isFormatRightful(psnid);
        if (msg == null) {
            // 根据校验规则校验最后一位校验位是否合法
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
     * <p>说明：
     * <li> 判断证件号格式是否正确：只能包含大写字母、数字和空格</li>
     * </p>
     * 
     * @param accnum
     * @return
     * @date 2013-9-1 下午08:45:47
     * @since NC6.31
     */
    private String isFormatRightful(String psnid) {
        // 校验长度是否为10位
        if (psnid.length() != 10) {
            return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                    "10140psn", "010140psn0122")/*
                                                   * @res
                                                   * "证件号长度不正确,台湾身份证证件号为10"
                                                   */;
        }
        for (int i = 0; i < psnid.length(); i++) {
            if (!((psnid.charAt(i) >= 'A' && psnid.charAt(i) <= 'Z')
                    || Character.isDigit(psnid.charAt(i)) || psnid.charAt(i) == ' ')) {
                return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                        "10140psn", "010140psn0123")/*
                                                       * @res
                                                       * "证件号只能包含大写字母、数字、空格"
                                                       */;
            }
        }
        if (psnid.charAt(0) < 'A' || psnid.charAt(0) > 'Z') {
            return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                    "10140psn", "010140psn0125")/*
                                                   * @res
                                                   * "第一位为大写字母,且必须为区域码"
                                                   */;
        }
        if (!((psnid.charAt(1) >= 'A' && psnid.charAt(1) <= 'D') || Character
                .isDigit(psnid.charAt(1)))) {
            return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                    "10140psn", "010140psn0126")/*
                                                   * @res
                                                   * "第二位为数字,或为性别码A-D"
                                                   */;
        }
        return null;
    }

    /**
     * 
     * <p>说明：
     * <li>根据校验规则校验最后一位校验位是否合法</li>
     * </p>
     * 
     * @param accnum
     * @return
     * @date 2013-9-1 下午08:47:05
     * @since NC6.31
     */
    private String isLastoneRigthful(String psnid) {
        int[] ruleid = new int[] {
            1, 8, 7, 6, 5, 4, 3, 2, 1
        };
        int num = 0;
        // 将证件号的前两位换成相应的数字
        String rebuildId = rebuildId(psnid);
        // 将前9位数字依次乘以１、８、７、６、５、４、３、２、１，并把乘积相加取和
        for (int i = 0; i < ruleid.length; i++) {
            num += convertNum(rebuildId.charAt(i)) * ruleid[i];
        }
        // 取补
        int complement = 10 - num % 10;
        if (complement == 10)
            complement = 0;

        if (complement != convertNum(psnid.charAt(9))) {
            return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                    "10140psn", "010140psn0127")/* @res "证件号转换后取补应与最后一位校验位相等" */;
        }
        return null;
    }

    /*
     * 把证件号psnid的前两位按规则转换成数字
     */
    private String rebuildId(String psnid) {
        // 把psnid第一个字母转换成相应的数字
        String firstchar = convert2Num(psnid.charAt(0));
        int firstnum =
                (convertNum(firstchar.charAt(1)) * 9 + convertNum(firstchar
                        .charAt(0))) % 10;
        psnid =
                psnid.replaceFirst(psnid.substring(0, 1),
                        String.valueOf(firstnum));
        // 把psnid第二个字母转换成相应的数字
        if (psnid.charAt(1) >= 'A' && psnid.charAt(1) <= 'D') {
            String secondchar = convert2Num(psnid.charAt(1));
            psnid =
                    psnid.replaceFirst(psnid.substring(1, 2),
                            secondchar.substring(1, 2));
        }
        return psnid;
    }

    /**
     * 将字母转换成数字
     */
    private String convert2Num(char c) {
        int number = (int) c - 65;
        return Integer.toString(regionCode[number]);
    }

    /**
     * 将字母转换成数字
     */
    private int convertNum(char c) {
        return (int) c - 48;
    }

}
